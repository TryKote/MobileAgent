package com.trykote.editor;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Loads config.json, palette.json and builds data structures for the editor.
 */
public class ConfigLoader {

    private static final String[] PALETTE_KEYS = {
        "text", "background", "background_alt", "link",
        "online", "away", "offline", "accent",
        "selection_bg", "selection_text", "separator", "disabled",
        "border", "scrollbar", "popup_bg", "popup_text",
        "gradient_start", "map_fill", "map_border", "map_bg",
        "map_pulse", "gradient_end"
    };

    // Sprite sheet file letters in order (0→a.png, 1→b.png, ...)
    private static final String[] SHEET_LETTERS = {
        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r"
    };

    private static final int ICON_DATA_OFFSET = 39;
    private static final int ICON_DATA_BOUNDARY = 354;
    private static final int ICON_SIZE = 16;

    private final Map<Integer, String> stringPool = new HashMap<>();
    private final List<ScreenDef> screens = new ArrayList<>();
    private int[][] palette; // [themeIndex][colorRole] = 0xRRGGBB
    private byte[] packedBlob;
    private Map<String, String> imageMapping;
    private Map<String, String> actionNames = new HashMap<>(); // cmd → name
    private Path imagesDir;

    public void load(Path resourcesDir) throws IOException {
        this.imagesDir = resourcesDir.resolve("images");
        loadImageMapping(imagesDir.resolve("mapping.json"));
        loadActions(resourcesDir.resolve("actions.json"));
        loadConfig(resourcesDir.resolve("config.json"));
        loadPalette(resourcesDir.resolve("palette.json"));
    }

    public Map<Integer, String> getStringPool() { return stringPool; }
    public List<ScreenDef> getScreens() { return screens; }
    public int[][] getPalette() { return palette; }

    /**
     * Returns sprite sheet file path and tile coordinates for an icon code.
     * Result: [0]=file path, [1]=tileX, [2]=tileY, or null if invalid.
     */
    public IconLocation getIconLocation(int iconCode) {
        if (packedBlob == null || iconCode <= 0 || iconCode + ICON_DATA_OFFSET >= packedBlob.length) {
            return null;
        }
        int raw = packedBlob[iconCode + ICON_DATA_OFFSET];
        int mapped = iconCode <= ICON_DATA_BOUNDARY ? raw & 0xFF : (raw + 256) & 0xFF;
        int sheetIndex = mapped >> 4;
        int tile = mapped & 0xF;
        int tileX = (tile & 3) * ICON_SIZE;
        int tileY = (tile >> 2) * ICON_SIZE;

        if (sheetIndex >= SHEET_LETTERS.length) return null;
        String obfuscatedName = SHEET_LETTERS[sheetIndex] + ".png";
        String readableName = imageMapping != null
            ? imageMapping.getOrDefault(obfuscatedName, obfuscatedName) : obfuscatedName;
        Path filePath = imagesDir.resolve(readableName);

        return new IconLocation(filePath, tileX, tileY);
    }

    public record IconLocation(Path filePath, int tileX, int tileY) {}

    public String resolveString(int key) {
        return stringPool.getOrDefault(key, "");
    }

    // --- Config loading ---

    @SuppressWarnings("unchecked")
    private void loadImageMapping(Path path) throws IOException {
        if (!Files.exists(path)) return;
        var map = parseJsonObject(Files.readString(path));
        imageMapping = new HashMap<>();
        // Reverse mapping: obfuscated → readable (mapping.json has readable → obfuscated? no, obf → readable)
        for (var entry : map.entrySet()) {
            imageMapping.put(entry.getKey(), (String) entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadActions(Path path) throws IOException {
        if (!Files.exists(path)) return;
        var map = parseJsonObject(Files.readString(path));
        for (var entry : map.entrySet()) {
            var val = (Map<String, Object>) entry.getValue();
            String name = (String) val.get("name");
            if (name != null) actionNames.put(entry.getKey(), name);
        }
    }

    public String getActionName(int cmd) {
        return actionNames.getOrDefault(String.valueOf(cmd), "");
    }

    private void loadConfig(Path path) throws IOException {
        this.configPath = path;
        String json = Files.readString(path);
        var root = parseJsonObject(json);

        loadZones(root);
        loadScreens(root);
    }

    @SuppressWarnings("unchecked")
    private void loadZones(Map<String, Object> root) {
        var zones = (List<Map<String, Object>>) root.get("zones");
        if (zones == null) return;

        for (var zone : zones) {
            var items = (List<Map<String, Object>>) zone.get("items");
            String zoneName = (String) zone.get("name");
            boolean isState = "state".equals(zoneName);

            for (int i = 0; i < items.size(); i++) {
                var item = items.get(i);
                String type = (String) item.get("type");
                int position = item.containsKey("position")
                    ? toInt(item.get("position"))
                    : (isState ? i : -1);

                if ("string".equals(type) && position >= 0) {
                    stringPool.put(position, (String) item.get("value"));
                } else if (position >= 0 && !stringPool.containsKey(position) && item.containsKey("key")) {
                    // For non-string slots, store the key name as fallback
                    String key = (String) item.get("key");
                    if (key != null && !key.isEmpty()) {
                        stringPool.put(position, "[" + key + "]");
                    }
                }
                if ("packed_strings".equals(type)) {
                    buildPackedBlob((List<Map<String, Object>>) item.get("entries"));
                }
            }
        }
    }

    private void buildPackedBlob(List<Map<String, Object>> entries) {
        var buf = new java.io.ByteArrayOutputStream();
        for (int idx = 0; idx < entries.size(); idx++) {
            if (idx > 0) buf.write(0); // null separator between segments
            var entry = entries.get(idx);
            if (entry.containsKey("bytes")) {
                buf.writeBytes(Base64.getDecoder().decode((String) entry.get("bytes")));
            } else if (entry.containsKey("value")) {
                String val = (String) entry.get("value");
                for (int i = 0; i < val.length(); i++) {
                    char ch = val.charAt(i);
                    if (ch < 128) {
                        buf.write(ch);
                    } else if (ch >= 0x410 && ch <= 0x44F) {
                        buf.write(ch - 0x410 + 0xC0);
                    } else if (ch == 0x451) {
                        buf.write(0xB8); // ё
                    } else if (ch == 0x401) {
                        buf.write(0xA8); // Ё
                    } else {
                        buf.write(ch & 0xFF);
                    }
                }
            }
        }
        packedBlob = buf.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private void loadScreens(Map<String, Object> root) {
        var screenList = (List<Map<String, Object>>) root.get("screens");
        if (screenList == null) return;

        for (var s : screenList) {
            String name = (String) s.get("name");
            int screenId = toInt(s.get("screenId"));
            var type = ScreenDef.ScreenType.fromString((String) s.get("type"));
            int title = toInt(s.get("title"));
            boolean checkboxes = Boolean.TRUE.equals(s.get("checkboxes"));
            String handler = (String) s.get("handler");
            int extraCmd = toInt(s.get("extraCmd"));

            var leftKey = parseSoftKey((Map<String, Object>) s.get("leftSoftKey"));
            var rightKey = parseSoftKey((Map<String, Object>) s.get("rightSoftKey"));

            List<ScreenDef.Item> items = new ArrayList<>();
            var itemList = (List<Map<String, Object>>) s.get("items");
            if (itemList != null) {
                for (var item : itemList) {
                    items.add(parseItem(item));
                }
            }

            screens.add(new ScreenDef(name, screenId, type, title,
                checkboxes, handler, leftKey, rightKey, extraCmd, items));
        }
    }

    private ScreenDef.SoftKey parseSoftKey(Map<String, Object> map) {
        if (map == null) return new ScreenDef.SoftKey(0, 0, "");
        return new ScreenDef.SoftKey(
            toInt(map.get("label")),
            toInt(map.get("cmd")),
            map.containsKey("label_") ? (String) map.get("label_") : ""
        );
    }

    private ScreenDef.Item parseItem(Map<String, Object> map) {
        return new ScreenDef.Item(
            ScreenDef.ItemType.fromString((String) map.get("type")),
            toInt(map.get("label")),
            map.containsKey("label_") ? (String) map.get("label_") : null,
            toInt(map.get("icon")),
            toInt(map.get("cmd")),
            toInt(map.get("condKey")),
            toInt(map.get("dataKey")),
            toInt(map.get("hint")),
            (String) map.get("style")
        );
    }

    // --- Save ---

    private Path configPath;

    public void save() throws IOException {
        if (configPath == null) return;
        String json = Files.readString(configPath);
        var root = parseJsonObject(json);

        // Rebuild screens array
        var screenJsonList = new ArrayList<Map<String, Object>>();
        for (var screen : screens) {
            var s = new LinkedHashMap<String, Object>();
            s.put("name", screen.name);
            s.put("title", screen.title);
            s.put("screenId", screen.screenId);
            s.put("type", screen.type.toConfigString());
            if (screen.checkboxes) s.put("checkboxes", true);
            if (!screen.handler.isEmpty()) s.put("handler", screen.handler);
            s.put("headerMode", 0);

            var lk = new LinkedHashMap<String, Object>();
            lk.put("label", screen.leftKey.label);
            lk.put("cmd", screen.leftKey.cmd);
            if (!screen.leftKey.labelHint.isEmpty()) lk.put("label_", screen.leftKey.labelHint);
            s.put("leftSoftKey", lk);

            var rk = new LinkedHashMap<String, Object>();
            rk.put("label", screen.rightKey.label);
            rk.put("cmd", screen.rightKey.cmd);
            if (!screen.rightKey.labelHint.isEmpty()) rk.put("label_", screen.rightKey.labelHint);
            s.put("rightSoftKey", rk);

            s.put("extraCmd", screen.extraCmd);

            var itemList = new ArrayList<Map<String, Object>>();
            for (var item : screen.items) {
                var m = new LinkedHashMap<String, Object>();
                m.put("type", item.type.toConfigString());
                if (item.type == ScreenDef.ItemType.CONDITIONAL_IF || item.type == ScreenDef.ItemType.CONDITIONAL_UNLESS) {
                    m.put("condKey", item.condKey);
                }
                m.put("label", item.label);
                if (item.labelHint != null && !item.labelHint.isEmpty()) m.put("label_", item.labelHint);
                if (item.icon > 0) m.put("icon", item.icon);
                if (item.cmd > 0) m.put("cmd", item.cmd);
                if (item.dataKey > 0) m.put("dataKey", item.dataKey);
                if (item.hint > 0) m.put("hint", item.hint);
                if (item.style != null) m.put("style", item.style);
                itemList.add(m);
            }
            s.put("items", itemList);
            screenJsonList.add(s);
        }

        root.put("screens", screenJsonList);
        String output = toJson(root, 0);
        Files.writeString(configPath, output + "\n");
    }

    private static String toJson(Object obj, int indent) {
        if (obj == null) return "null";
        if (obj instanceof Boolean b) return b.toString();
        if (obj instanceof Number n) {
            if (n instanceof Double d && d == Math.floor(d) && !Double.isInfinite(d)) {
                return String.valueOf(d.longValue());
            }
            return n.toString();
        }
        if (obj instanceof String s) return "\"" + escapeJson(s) + "\"";
        if (obj instanceof List<?> list) {
            if (list.isEmpty()) return "[]";
            var sb = new StringBuilder("[\n");
            for (int i = 0; i < list.size(); i++) {
                sb.append("  ".repeat(indent + 1)).append(toJson(list.get(i), indent + 1));
                if (i < list.size() - 1) sb.append(',');
                sb.append('\n');
            }
            sb.append("  ".repeat(indent)).append(']');
            return sb.toString();
        }
        if (obj instanceof Map<?, ?> map) {
            if (map.isEmpty()) return "{}";
            var sb = new StringBuilder("{\n");
            var entries = new ArrayList<>(map.entrySet());
            for (int i = 0; i < entries.size(); i++) {
                var e = entries.get(i);
                sb.append("  ".repeat(indent + 1))
                  .append('"').append(escapeJson(e.getKey().toString())).append("\": ")
                  .append(toJson(e.getValue(), indent + 1));
                if (i < entries.size() - 1) sb.append(',');
                sb.append('\n');
            }
            sb.append("  ".repeat(indent)).append('}');
            return sb.toString();
        }
        return obj.toString();
    }

    private static String escapeJson(String s) {
        var sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    // --- Palette loading ---

    private void loadPalette(Path path) throws IOException {
        String json = Files.readString(path);
        var root = parseJsonObject(json);
        @SuppressWarnings("unchecked")
        var themes = (List<Map<String, Object>>) root.get("themes");

        palette = new int[themes.size()][PALETTE_KEYS.length];

        for (int t = 0; t < themes.size(); t++) {
            var theme = themes.get(t);
            for (int c = 0; c < PALETTE_KEYS.length; c++) {
                String hex = (String) theme.get(PALETTE_KEYS[c]);
                if (hex != null) {
                    palette[t][c] = parseHexColor(hex);
                }
            }
        }
    }

    private static int parseHexColor(String hex) {
        hex = hex.startsWith("#") ? hex.substring(1) : hex;
        return Integer.parseInt(hex, 16);
    }

    // --- Minimal JSON parser (no external deps) ---

    private static int pos;
    private static String src;

    @SuppressWarnings("unchecked")
    static Map<String, Object> parseJsonObject(String json) {
        pos = 0;
        src = json;
        return (Map<String, Object>) parseValue();
    }

    private static Object parseValue() {
        skipWhitespace();
        char c = src.charAt(pos);
        return switch (c) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
            case 't', 'f' -> parseBoolean();
            case 'n' -> parseNull();
            default -> parseNumber();
        };
    }

    private static Map<String, Object> parseObject() {
        pos++; // skip {
        var map = new LinkedHashMap<String, Object>();
        skipWhitespace();
        if (src.charAt(pos) == '}') { pos++; return map; }

        while (true) {
            skipWhitespace();
            String key = (String) parseString();
            skipWhitespace();
            pos++; // skip :
            Object value = parseValue();
            map.put(key, value);
            skipWhitespace();
            if (src.charAt(pos) == ',') { pos++; }
            else break;
        }
        skipWhitespace();
        pos++; // skip }
        return map;
    }

    private static List<Object> parseArray() {
        pos++; // skip [
        var list = new ArrayList<>();
        skipWhitespace();
        if (src.charAt(pos) == ']') { pos++; return list; }

        while (true) {
            list.add(parseValue());
            skipWhitespace();
            if (src.charAt(pos) == ',') { pos++; }
            else break;
        }
        skipWhitespace();
        pos++; // skip ]
        return list;
    }

    private static String parseString() {
        pos++; // skip opening "
        var sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') return sb.toString();
            if (c == '\\') {
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"', '\\', '/' -> sb.append(esc);
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'u' -> {
                        sb.append((char) Integer.parseInt(src.substring(pos, pos + 4), 16));
                        pos += 4;
                    }
                    default -> { sb.append('\\'); sb.append(esc); }
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static Object parseNumber() {
        int start = pos;
        if (src.charAt(pos) == '-') pos++;
        while (pos < src.length() && (Character.isDigit(src.charAt(pos)) || src.charAt(pos) == '.' || src.charAt(pos) == 'e' || src.charAt(pos) == 'E' || src.charAt(pos) == '+' || src.charAt(pos) == '-' && pos > start + 1)) {
            pos++;
        }
        String num = src.substring(start, pos);
        if (num.contains(".") || num.contains("e") || num.contains("E")) {
            return Double.parseDouble(num);
        }
        long val = Long.parseLong(num);
        if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) return (int) val;
        return val;
    }

    private static Object parseBoolean() {
        if (src.startsWith("true", pos)) { pos += 4; return true; }
        pos += 5; return false;
    }

    private static Object parseNull() {
        pos += 4;
        return null;
    }

    private static void skipWhitespace() {
        while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) pos++;
    }

    private static int toInt(Object val) {
        if (val == null) return 0;
        if (val instanceof Integer i) return i;
        if (val instanceof Long l) return l.intValue();
        if (val instanceof Double d) return d.intValue();
        return 0;
    }
}
