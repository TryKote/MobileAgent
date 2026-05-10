package com.trykote.editor.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Serializes config.json to binary cfg and back.
 * Java replacement for: python3 tools/cfg_tool.py --serialize / --deserialize
 */
public class CfgSerializer {

    static final int OBJECT_POOL_SIZE = 1406;
    static final int SCREEN_DATA_SIZE = 3605;
    static final int INT_POOL_HEADER_SIZE = 172;
    static final int RAW_BYTES_START = 295;
    static final int RAW_BYTES_END = 1036;
    static final int PALETTE_TOTAL_INTS = 176;
    private static final String SEPARATOR = "null";
    // Note: we use custom encoding matching cfg_tool.py char_to_win1251,
    // NOT standard Charset CP1251 (which replaces unknowns with '?').

    // ---- Serialize ----

    public static void serialize(Path inputDir, Path outputCfg) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode config = mapper.readTree(inputDir.resolve("config.json").toFile());

        injectPalette(config, inputDir);

        List<JsonNode> pool = flattenZones(config);
        long[] intPool = compileScreens(config);

        if (intPool.length != SCREEN_DATA_SIZE) {
            System.err.println("Warning: screen data size " + intPool.length
                + ", expected " + SCREEN_DATA_SIZE);
        }

        try (var out = new ByteArrayOutputStream()) {
            // Write object pool
            for (int i = 0; i < pool.size(); i++) {
                JsonNode obj = pool.get(i);
                String type = obj.path("type").asText("null");

                switch (type) {
                    case "packed_strings" -> {
                        byte[] blob = rebuildPackedBlob(obj);
                        writeByteArray(out, blob);
                    }
                    case "string_list" -> {
                        JsonNode values = obj.get("value");
                        var buf = new ByteArrayOutputStream();
                        for (int j = 0; j < values.size(); j++) {
                            if (j > 0) buf.write(0);
                            buf.write(encodeCP1251(values.get(j).asText()));
                        }
                        writeByteArray(out, buf.toByteArray());
                    }
                    case "bytes" -> {
                        byte[] data = Base64.getDecoder().decode(obj.get("value").asText());
                        writeByteArray(out, data);
                    }
                    case "null" -> writeByteArray(out, encodeCP1251(SEPARATOR));
                    case "string" -> writeByteArray(out, encodeCP1251(obj.get("value").asText()));
                    case "int" -> writeIntLong(out, obj.get("value").asLong());
                    default -> writeByteArray(out, encodeCP1251(SEPARATOR));
                }
            }

            // Write screen data (no header)
            for (long v : intPool) {
                writeIntLong(out, v);
            }

            Files.write(outputCfg, out.toByteArray());
            System.out.println("Wrote " + outputCfg + " (" + Files.size(outputCfg) + " bytes)");
        }
    }

    // ---- Deserialize ----

    public static void deserialize(Path cfgPath, Path outputDir) throws IOException {
        byte[] data = Files.readAllBytes(cfgPath);
        int pos = 0;

        // Read object pool
        List<JsonNode> objects = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (int i = 0; i < OBJECT_POOL_SIZE; i++) {
            int flag = data[pos] & 0xFF;

            if ((flag & 0x80) != 0) {
                // Byte array
                int length;
                if ((flag & 0x40) != 0) {
                    length = flag & 0x3F;
                    pos++;
                } else {
                    length = ((flag & 0x1F) << 8) | (data[pos + 1] & 0xFF);
                    pos += 2;
                }
                byte[] payload = Arrays.copyOfRange(data, pos, pos + length);
                pos += length;

                ObjectNode obj = mapper.createObjectNode();
                obj.put("index", i);

                if (i >= RAW_BYTES_START && i < RAW_BYTES_END) {
                    obj.put("type", "bytes");
                    obj.put("value", Base64.getEncoder().encodeToString(payload));
                } else {
                    String str = decodeCP1251(payload);
                    if (SEPARATOR.equals(str)) {
                        obj.put("type", "null");
                    } else {
                        obj.put("type", "string");
                        obj.put("value", str);
                    }
                }
                objects.add(obj);
            } else {
                // Integer
                int value = readIntAt(data, pos);
                pos += intEncodedSize(data, pos);
                ObjectNode obj = mapper.createObjectNode();
                obj.put("index", i);
                obj.put("type", "int");
                obj.put("value", value);
                objects.add(obj);
            }
        }

        // Read screen data
        List<Integer> intPoolRaw = new ArrayList<>();
        while (pos < data.length) {
            intPoolRaw.add(readIntAt(data, pos));
            pos += intEncodedSize(data, pos);
        }

        if (intPoolRaw.size() != SCREEN_DATA_SIZE) {
            System.err.println("Warning: screen data size " + intPoolRaw.size()
                + ", expected " + SCREEN_DATA_SIZE);
        }

        // Build output config
        ObjectNode config = mapper.createObjectNode();
        config.put("format", "mobileagent-cfg-v2");
        config.set("objectPool", mapper.valueToTree(objects));

        // Note: screen decompilation requires KNOWN_SCREENS catalog
        // For now, store raw int pool — full decompilation done by cfg_tool.py
        ArrayNode intPoolArray = mapper.createArrayNode();
        for (int v : intPoolRaw) intPoolArray.add(v);
        config.set("intPoolRaw", intPoolArray);

        Files.createDirectories(outputDir);
        Path outPath = outputDir.resolve("config.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(outPath.toFile(), config);
        System.out.println("Wrote " + outPath);
    }

    // ---- Binary encoding helpers ----

    /** Read JSON int preserving unsigned: 4294967295 stays positive (not -1). */
    private static int readJsonUnsigned(JsonNode node) {
        long v = node.asLong();
        return (int) v; // 4294967295L → -1 as int, that's fine for writeInt
    }

    /**
     * Write int matching Python encode_int exactly.
     * Positive values use minimal unsigned bytes.
     * Negative values (including -1 from JSON 4294967295 via asInt) → 0 data bytes.
     * JSON values > MAX_INT should be read via asLong and written via writeIntLong.
     */
    static void writeInt(OutputStream out, int value) throws IOException {
        // Match Python cfg_tool.py encode_int exactly:
        // Minimal big-endian SIGNED representation.
        // 199 (0xC7) needs 2 bytes because 1 byte 0xC7 reads as -57 signed.
        if (value == 0) {
            out.write(0x09);
            out.write(0x00);
            return;
        }

        // Python cfg_tool.py encode_int: "while remaining > 0" loop.
        // Negative values → remaining < 0 → loop skips → 0 bytes → flag 0x08 only.
        // Positive: minimal unsigned byte count.
        if (value < 0) {
            out.write(0x08); // 0 data bytes, matches Python
            return;
        }
        int byteCount;
        if (value <= 0xFF) {
            byteCount = 1;
        } else if (value <= 0xFFFF) {
            byteCount = 2;
        } else if (value <= 0xFFFFFF) {
            byteCount = 3;
        } else {
            byteCount = 4;
        }

        out.write(0x08 | byteCount);
        for (int i = byteCount - 1; i >= 0; i--) {
            out.write((value >> (i * 8)) & 0xFF);
        }
    }

    /** Write long value — for JSON values that may exceed Integer.MAX_VALUE (e.g. 4294967295). */
    static void writeIntLong(OutputStream out, long value) throws IOException {
        if (value == 0) { out.write(0x09); out.write(0x00); return; }
        if (value < 0) { out.write(0x08); return; }
        int byteCount;
        if (value <= 0xFFL) byteCount = 1;
        else if (value <= 0xFFFFL) byteCount = 2;
        else if (value <= 0xFFFFFFL) byteCount = 3;
        else byteCount = 4;
        out.write(0x08 | byteCount);
        for (int i = byteCount - 1; i >= 0; i--) {
            out.write((int) ((value >> (i * 8)) & 0xFF));
        }
    }

    static void writeByteArray(OutputStream out, byte[] payload) throws IOException {
        int len = payload.length;
        if (len < 64) {
            out.write(0x80 | 0x40 | len);
        } else {
            out.write(0x80 | ((len >> 8) & 0x1F));
            out.write(len & 0xFF);
        }
        out.write(payload);
    }

    static int readIntAt(byte[] data, int pos) {
        int flag = data[pos] & 0xFF;
        if ((flag & 0x40) != 0) return flag & 0x3F;
        if ((flag & 0x20) != 0) return ((flag & 0x1F) << 8) | (data[pos + 1] & 0xFF);
        int byteCount = flag & 0x07;
        // No sign extension — matches Python's unsigned read
        int value = 0;
        for (int i = 0; i < byteCount; i++) {
            value = (value << 8) | (data[pos + 1 + i] & 0xFF);
        }
        return value;
    }

    static int intEncodedSize(byte[] data, int pos) {
        int flag = data[pos] & 0xFF;
        if ((flag & 0x40) != 0) return 1;
        if ((flag & 0x20) != 0) return 2;
        return 1 + (flag & 0x07);
    }

    // ---- Screen compilation ----

    static long[] compileScreens(JsonNode config) {
        JsonNode screens = config.get("screens");
        if (screens == null) return new long[0];

        var result = new ArrayList<Long>();
        for (JsonNode screen : screens) {
            // 10-int header
            long typeFlags = screenTypeToInt(screen.path("type").asText("fullscreen"));
            if (screen.path("checkboxes").asBoolean()) typeFlags |= 0x10;

            result.add((long) screen.path("title").asInt());
            result.add((long) screen.path("screenId").asInt());
            result.add(typeFlags);
            result.add(screen.path("headerMode").asLong());

            JsonNode lsk = screen.get("leftSoftKey");
            JsonNode rsk = screen.get("rightSoftKey");
            result.add((long) (lsk != null ? lsk.path("label").asInt() : 0));
            result.add((long) (rsk != null ? rsk.path("label").asInt() : 0));
            result.add((long) (lsk != null ? lsk.path("cmd").asInt() : 0));
            result.add((long) (rsk != null ? rsk.path("cmd").asInt() : 0));
            result.add((long) screen.path("extraCmd").asInt());

            // Items
            JsonNode items = screen.get("items");
            int itemCount = (items != null) ? items.size() : 0;
            result.add((long) itemCount);

            if (items != null) {
                for (JsonNode item : items) {
                    for (long v : compileItem(item)) result.add(v);
                }
            }

            // Trailing data — asLong() to preserve unsigned values (palette colors)
            JsonNode trailing = screen.get("trailingData");
            if (trailing != null && trailing.isArray()) {
                for (JsonNode v : trailing) result.add(v.asLong());
            }
        }

        return result.stream().mapToLong(Long::longValue).toArray();
    }

    /**
     * Compile a screen item to an array of long values.
     * Uses asLong() to preserve unsigned JSON values like 4294967295 (0xFFFFFFFF).
     */
    static long[] compileItem(JsonNode item) {
        String type = item.path("type").asText();
        long typeFlags = itemTypeToInt(type);
        if ("text".equals(item.path("style").asText(null))) typeFlags |= 0x10;

        return switch (type) {
            case "action" -> {
                if (item.has("extra")) {
                    typeFlags |= 0x20;
                    yield new long[]{typeFlags, item.path("extra").asLong(),
                        item.path("condKey").asLong(), item.path("icon").asLong(),
                        item.path("cmd").asLong()};
                }
                yield new long[]{typeFlags, item.path("label").asLong(),
                    item.path("icon").asLong(), item.path("cmd").asLong()};
            }
            case "separator" -> new long[]{typeFlags, item.path("label").asLong(),
                item.path("sublabel").asLong()};
            case "checkbox" -> new long[]{typeFlags, item.path("label").asLong(),
                item.path("stateKey").asLong()};
            case "dropdown" -> new long[]{typeFlags, item.path("label").asLong(),
                item.path("choices").asLong(), item.path("indexKey").asLong()};
            case "text_separator", "label_separator" -> new long[]{typeFlags,
                item.path("label").asLong()};
            case "text_input" -> {
                long validation = item.path("validation").asLong();
                if (validation == 2) {
                    yield new long[]{typeFlags, item.path("dataKey").asLong(),
                        item.path("inputType").asLong(), item.path("hint").asLong(),
                        validation, item.path("min").asLong(), item.path("max").asLong(),
                        item.path("default").asLong(), item.path("stateKey").asLong()};
                }
                yield new long[]{typeFlags, item.path("dataKey").asLong(),
                    item.path("inputType").asLong(), item.path("hint").asLong(),
                    validation, item.path("valueKey").asLong()};
            }
            case "conditional_if", "conditional_unless" -> new long[]{typeFlags,
                item.path("condKey").asLong(), item.path("label").asLong(),
                item.path("icon").asLong(), item.path("cmd").asLong()};
            case "login" -> new long[]{typeFlags, item.path("label").asLong(),
                item.path("value").asLong()};
            case "password" -> new long[]{typeFlags, item.path("value").asLong()};
            case "image" -> new long[]{typeFlags, item.path("poolIndex").asLong()};
            case "redirect" -> new long[]{typeFlags, item.path("targetOffset").asLong()};
            default -> new long[]{typeFlags, item.path("data").asLong()};
        };
    }

    // ---- Palette injection ----

    static void injectPalette(JsonNode config, Path inputDir) throws IOException {
        Path palettePath = inputDir.resolve("palette.json");
        if (!Files.exists(palettePath)) return;

        long[] flat = loadPalette(palettePath);
        JsonNode screens = config.get("screens");
        if (screens == null) return;

        for (JsonNode screen : screens) {
            if ("VCARD_ACTIONS".equals(screen.path("name").asText())) {
                ArrayNode arr = new ObjectMapper().createArrayNode();
                for (long v : flat) arr.add(v);
                ((ObjectNode) screen).set("trailingData", arr);
                return;
            }
        }
    }

    static long[] loadPalette(Path palettePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(palettePath.toFile());
        JsonNode themes = root.get("themes");
        if (themes == null || themes.size() != 8)
            throw new IOException("Expected 8 themes in palette.json");

        String[] roles = {
            "text", "background", "background_alt", "link",
            "online", "away", "offline", "accent",
            "selection_bg", "selection_text", "separator", "disabled",
            "border", "scrollbar", "popup_bg", "popup_text",
            "gradient_start", "map_fill", "map_border", "map_bg",
            "map_pulse", "gradient_end"
        };

        long[] flat = new long[PALETTE_TOTAL_INTS];
        int idx = 0;
        for (String role : roles) {
            for (JsonNode theme : themes) {
                String hex = theme.path(role).asText("0");
                flat[idx++] = parseHexColorUnsigned(hex);
            }
        }
        return flat;
    }

    /** Decode bytes from custom CP1251 mapping (matches cfg_tool.py win1251_to_char). */
    static String decodeCP1251(byte[] data) {
        var sb = new StringBuilder(data.length);
        for (byte b : data) {
            int code = b & 0xFF;
            if (code >= 0xC0 && code <= 0xFF) {       // А-я
                sb.append((char) (code + 848));
            } else if (code == 0xA8) {                 // Ё
                sb.append('\u0401');
            } else if (code == 0xB8) {                 // ё
                sb.append('\u0451');
            } else {
                sb.append((char) code);
            }
        }
        return sb.toString();
    }

    /** Encode string to bytes using custom CP1251 mapping (matches cfg_tool.py). */
    static byte[] encodeCP1251(String s) {
        byte[] result = new byte[s.length()];
        for (int i = 0; i < s.length(); i++) {
            int code = s.charAt(i);
            if (code >= 1040 && code <= 1103) {       // А-я
                result[i] = (byte) ((code - 848) & 0xFF);
            } else if (code == 1025) {                 // Ё
                result[i] = (byte) 0xA8;
            } else if (code == 1105) {                 // ё
                result[i] = (byte) 0xB8;
            } else {
                result[i] = (byte) (code & 0xFF);      // passthrough
            }
        }
        return result;
    }

    private static long parseHexColorUnsigned(String hex) {
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        return Long.parseLong(hex, 16); // stays positive even for 0xFF000000
    }

    // ---- Pool flattening ----

    static List<JsonNode> flattenZones(JsonNode config) {
        // Legacy format: objectPool already flat
        JsonNode pool = config.get("objectPool");
        if (pool != null && pool.isArray() && pool.size() == OBJECT_POOL_SIZE) {
            var result = new ArrayList<JsonNode>();
            pool.forEach(result::add);
            return result;
        }

        // Zones format
        ObjectMapper mapper = new ObjectMapper();
        JsonNode[] flat = new JsonNode[OBJECT_POOL_SIZE];
        JsonNode zones = config.get("zones");
        if (zones != null) {
            for (JsonNode zone : zones) {
                int start = zone.path("start").asInt(0);
                JsonNode items = zone.get("items");
                if (items == null) continue;
                for (int i = 0; i < items.size(); i++) {
                    JsonNode item = items.get(i);
                    int pos = item.has("position") ? item.get("position").asInt() : start + i;
                    if (pos >= 0 && pos < OBJECT_POOL_SIZE) flat[pos] = item;
                }
            }
        }

        var result = new ArrayList<JsonNode>();
        ObjectNode nullObj = mapper.createObjectNode();
        nullObj.put("type", "null");
        for (int i = 0; i < OBJECT_POOL_SIZE; i++) {
            result.add(flat[i] != null ? flat[i] : nullObj);
        }
        return result;
    }

    private static byte[] rebuildPackedBlob(JsonNode obj) {
        JsonNode entries = obj.get("entries");
        if (entries == null) return new byte[0];
        var buf = new ByteArrayOutputStream();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) buf.write(0);
            JsonNode entry = entries.get(i);
            if (entry.has("bytes")) {
                byte[] decoded = Base64.getDecoder().decode(entry.get("bytes").asText());
                buf.writeBytes(decoded);
            } else if (entry.has("value")) {
                String val = entry.get("value").asText();
                buf.writeBytes(encodeCP1251(val));
            }
        }
        return buf.toByteArray();
    }

    // ---- Type mapping ----

    private static final String[] SCREEN_TYPE_NAMES = {
        "fullscreen", "fullscreen_alt", "dialog_center", "dialog_bottom",
        "dialog_corner", "fullscreen_noscroll", "map", "toast",
        "toast_center", "fullscreen_noscroll_alt", "popup", "dialog_low", "map_alt"
    };

    private static int screenTypeToInt(String name) {
        for (int i = 0; i < SCREEN_TYPE_NAMES.length; i++) {
            if (SCREEN_TYPE_NAMES[i].equals(name)) return i;
        }
        return 0;
    }

    private static final Map<String, Integer> ITEM_TYPE_MAP = Map.ofEntries(
        Map.entry("action", 0), Map.entry("separator", 1), Map.entry("checkbox", 2),
        Map.entry("dropdown", 3), Map.entry("text_separator", 4), Map.entry("text_input", 5),
        Map.entry("label_separator", 6), Map.entry("conditional_if", 7),
        Map.entry("conditional_unless", 8), Map.entry("login", 9),
        Map.entry("password", 10), Map.entry("image", 11), Map.entry("redirect", 12)
    );

    private static int itemTypeToInt(String name) {
        return ITEM_TYPE_MAP.getOrDefault(name, 0);
    }
}
