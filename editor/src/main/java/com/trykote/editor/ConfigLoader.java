package com.trykote.editor;

import com.trykote.editor.analysis.ActionCatalogBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigLoader {

    private static final String[] PALETTE_KEYS = {
        "text", "background", "background_alt", "link",
        "online", "away", "offline", "accent",
        "selection_bg", "selection_text", "separator", "disabled",
        "border", "scrollbar", "popup_bg", "popup_text",
        "gradient_start", "map_fill", "map_border", "map_bg",
        "map_pulse", "gradient_end"
    };

    private static final String[] SHEET_LETTERS = {
        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r"
    };

    private static final int ICON_DATA_OFFSET = 39;
    private static final int ICON_DATA_BOUNDARY = 354;
    private static final int ICON_SIZE = 16;

    private final ObjectMapper mapper = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    private Path resourcesDir;
    private final Map<Integer, String> stringPool = new HashMap<>();
    private final List<ScreenDef> screens = new ArrayList<>();
    private final List<PoolEntry> poolEntries = new ArrayList<>();
    private Map<Integer, List<String>> poolScreenRefs;  // pool index → list of "SCREEN_NAME.field"
    private int[][] palette;
    private byte[] packedBlob;
    private Map<String, String> imageMapping;
    private Map<String, String> actionNames = new HashMap<>();
    private Path imagesDir;
    private Path configPath;

    // Keep full JSON tree for lossless save
    private JsonNode configRoot;

    private Map<Integer, List<ActionCatalogBuilder.CatalogEntry>> actionCatalog = new HashMap<>();

    public void load(Path resourcesDir) throws IOException {
        this.resourcesDir = resourcesDir.toAbsolutePath();
        this.imagesDir = resourcesDir.resolve("images");
        loadImageMapping(imagesDir.resolve("mapping.json"));
        loadActions(resourcesDir.resolve("actions.json"));
        loadConfig(resourcesDir.resolve("config.json"));
        loadPalette(resourcesDir.resolve("palette.json"));
        loadActionCatalog(resourcesDir);
    }

    public Map<Integer, String> getStringPool() { return stringPool; }
    public List<ScreenDef> getScreens() { return screens; }
    public int[][] getPalette() { return palette; }
    public Path getSourcesDir() { return resourcesDir.resolve("../sources").normalize(); }
    public List<PoolEntry> getPoolEntries() { return poolEntries; }

    public String getPoolScreenRefs(int index) {
        if (poolScreenRefs == null) return "";
        var refs = poolScreenRefs.get(index);
        if (refs == null || refs.isEmpty()) return "";
        return String.join(", ", refs);
    }

    public record PoolEntry(int index, String type, String value, String key,
                            String className, String zone) {}

    public List<ActionCatalogBuilder.CatalogEntry> getScreenCatalog(int screenId) {
        return actionCatalog.getOrDefault(screenId, List.of());
    }

    private void loadActionCatalog(Path resourcesDir) {
        // Sources dir is typically ../sources relative to resources-src/
        Path sourcesDir = resourcesDir.resolve("../sources").normalize();
        if (!Files.isDirectory(sourcesDir)) return;
        try {
            ActionCatalogBuilder builder = new ActionCatalogBuilder();
            builder.build(sourcesDir);
            actionCatalog = builder.buildIndex();
        } catch (Exception e) {
            System.err.println("Action catalog build failed: " + e.getMessage());
        }
    }

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
        return new IconLocation(imagesDir.resolve(readableName), tileX, tileY);
    }

    public record IconLocation(Path filePath, int tileX, int tileY) {}

    private final Map<Integer, String> keyNames = new HashMap<>();

    public String resolveString(int key) {
        return stringPool.getOrDefault(key, "");
    }

    public String getActionName(int cmd) {
        return actionNames.getOrDefault(String.valueOf(cmd), "");
    }

    public String resolveKeyName(int keyValue) {
        return keyNames.getOrDefault(keyValue, "");
    }

    /**
     * Resolve any int field to the best human-readable representation.
     * Tries: string pool → action name → key name → empty.
     */
    public String resolveAny(int value) {
        String s = resolveString(value);
        if (!s.isEmpty() && !s.startsWith("[")) return s;
        String a = getActionName(value);
        if (!a.isEmpty()) return a;
        String k = resolveKeyName(value);
        if (!k.isEmpty()) return k;
        return s; // may be "[KEY_NAME]" or ""
    }

    public Map<Integer, String> getActionOptions() {
        var result = new HashMap<Integer, String>();
        for (var e : actionNames.entrySet()) {
            try { result.put(Integer.parseInt(e.getKey()), e.getValue()); }
            catch (NumberFormatException ignored) {}
        }
        return result;
    }

    public Map<Integer, String> getKeyOptions() {
        return new HashMap<>(keyNames);
    }

    public Map<Integer, String> getStringOptions() {
        var result = new HashMap<Integer, String>();
        for (var e : stringPool.entrySet()) {
            String v = e.getValue();
            if (!v.isEmpty() && !v.startsWith("[")) {
                result.put(e.getKey(), v.length() > 50 ? v.substring(0, 47) + "..." : v);
            }
        }
        return result;
    }

    // --- Loading ---

    private void loadImageMapping(Path path) throws IOException {
        if (!Files.exists(path)) return;
        imageMapping = mapper.readValue(path.toFile(),
            new TypeReference<LinkedHashMap<String, String>>() {});
    }

    private void loadActions(Path path) throws IOException {
        if (!Files.exists(path)) return;
        JsonNode root = mapper.readTree(path.toFile());
        root.fields().forEachRemaining(e -> {
            JsonNode name = e.getValue().get("name");
            if (name != null) actionNames.put(e.getKey(), name.asText());
        });
    }

    private void loadConfig(Path path) throws IOException {
        this.configPath = path;
        configRoot = mapper.readTree(path.toFile());
        loadKeyNames(configRoot);
        loadZones(configRoot);
        loadPoolEntries(configRoot);
        loadScreens(configRoot);
        buildPoolScreenRefs();
    }

    private void loadKeyNames(JsonNode root) {
        // From intPoolKeys: high-index runtime state keys
        JsonNode ipk = root.get("intPoolKeys");
        if (ipk != null) {
            for (JsonNode entry : ipk) {
                int value = entry.path("value").asInt();
                String cls = entry.path("class").asText("");
                String key = entry.path("key").asText("");
                if (value > 0 && !key.isEmpty()) {
                    keyNames.put(value, cls.isEmpty() ? key : cls + "." + key);
                }
            }
        }

        // From zones: pool-range keys (< 1406)
        JsonNode zones = root.get("zones");
        if (zones != null) {
            for (JsonNode zone : zones) {
                int start = zone.path("start").asInt(0);
                JsonNode items = zone.get("items");
                if (items == null) continue;
                for (int i = 0; i < items.size(); i++) {
                    JsonNode item = items.get(i);
                    if (item.has("key")) {
                        int pos = item.has("position") ? item.get("position").asInt() : start + i;
                        String cls = item.path("class").asText("");
                        String key = item.get("key").asText();
                        if (!key.isEmpty()) {
                            keyNames.put(pos, cls.isEmpty() ? key : cls + "." + key);
                        }
                    }
                }
            }
        }
    }

    private void loadZones(JsonNode root) {
        JsonNode zones = root.get("zones");
        if (zones == null) return;

        for (JsonNode zone : zones) {
            JsonNode items = zone.get("items");
            boolean isState = "state".equals(zone.path("name").asText());

            for (int i = 0; i < items.size(); i++) {
                JsonNode item = items.get(i);
                String type = item.path("type").asText();
                int position = item.has("position") ? item.get("position").asInt() : (isState ? i : -1);

                if ("string".equals(type) && position >= 0) {
                    stringPool.put(position, item.path("value").asText());
                } else if (position >= 0 && !stringPool.containsKey(position) && item.has("key")) {
                    String key = item.get("key").asText();
                    if (!key.isEmpty()) {
                        stringPool.put(position, "[" + key + "]");
                    }
                }
                if ("packed_strings".equals(type)) {
                    buildPackedBlob(item.get("entries"));
                }
            }
        }
    }

    private void buildPackedBlob(JsonNode entries) {
        var buf = new ByteArrayOutputStream();
        for (int idx = 0; idx < entries.size(); idx++) {
            if (idx > 0) buf.write(0);
            JsonNode entry = entries.get(idx);
            if (entry.has("bytes")) {
                buf.writeBytes(Base64.getDecoder().decode(entry.get("bytes").asText()));
            } else if (entry.has("value")) {
                String val = entry.get("value").asText();
                for (int i = 0; i < val.length(); i++) {
                    char ch = val.charAt(i);
                    if (ch < 128) buf.write(ch);
                    else if (ch >= 0x410 && ch <= 0x44F) buf.write(ch - 0x410 + 0xC0);
                    else if (ch == 0x451) buf.write(0xB8);
                    else if (ch == 0x401) buf.write(0xA8);
                    else buf.write(ch & 0xFF);
                }
            }
        }
        packedBlob = buf.toByteArray();
    }

    private void loadScreens(JsonNode root) {
        JsonNode screenList = root.get("screens");
        if (screenList == null) return;

        for (JsonNode s : screenList) {
            String name = s.path("name").asText();
            int screenId = s.path("screenId").asInt();
            var type = ScreenDef.ScreenType.fromString(s.path("type").asText());
            int title = s.path("title").asInt();
            boolean checkboxes = s.path("checkboxes").asBoolean();
            String handler = s.path("handler").asText(null);
            int extraCmd = s.path("extraCmd").asInt();

            var leftKey = parseSoftKey(s.get("leftSoftKey"));
            var rightKey = parseSoftKey(s.get("rightSoftKey"));

            List<ScreenDef.Item> items = new ArrayList<>();
            JsonNode itemList = s.get("items");
            if (itemList != null) {
                for (JsonNode item : itemList) {
                    items.add(parseItem(item));
                }
            }

            var def = new ScreenDef(name, screenId, type, title,
                checkboxes, handler, leftKey, rightKey, extraCmd, items);
            def.rawJson = (ObjectNode) s.deepCopy();
            screens.add(def);
        }
    }

    private ScreenDef.SoftKey parseSoftKey(JsonNode node) {
        if (node == null) return new ScreenDef.SoftKey(0, 0, "");
        return new ScreenDef.SoftKey(
            node.path("label").asInt(),
            node.path("cmd").asInt(),
            node.path("label_").asText("")
        );
    }

    private ScreenDef.Item parseItem(JsonNode node) {
        // Convert JsonNode to Map for rawJson preservation
        Map<String, Object> rawJson = mapper.convertValue(node,
            new TypeReference<LinkedHashMap<String, Object>>() {});

        return new ScreenDef.Item(
            ScreenDef.ItemType.fromString(node.path("type").asText()),
            node.path("label").asInt(),
            node.has("label_") ? node.get("label_").asText() : null,
            node.path("icon").asInt(),
            node.path("cmd").asInt(),
            node.path("condKey").asInt(),
            node.path("dataKey").asInt(),
            node.path("hint").asInt(),
            node.has("style") ? node.get("style").asText() : null,
            rawJson
        );
    }

    private void loadPoolEntries(JsonNode root) {
        JsonNode zones = root.get("zones");
        if (zones == null) return;

        int globalIndex = 0;
        for (JsonNode zone : zones) {
            String zoneName = zone.path("name").asText("?");
            JsonNode items = zone.get("items");
            if (items == null) continue;

            int start = zone.has("start") ? zone.get("start").asInt() : globalIndex;
            for (int i = 0; i < items.size(); i++) {
                JsonNode item = items.get(i);
                int pos = item.has("position") ? item.get("position").asInt() : start + i;
                String type = item.path("type").asText("?");
                String key = item.path("key").asText("");
                String cls = item.path("class").asText("");
                String value = formatPoolValue(item, type);
                poolEntries.add(new PoolEntry(pos, type, value, key, cls, zoneName));
            }
            globalIndex = start + items.size();
        }

        poolEntries.sort(Comparator.comparingInt(PoolEntry::index));
    }

    private static String formatPoolValue(JsonNode item, String type) {
        return switch (type) {
            case "string" -> {
                String v = item.path("value").asText("");
                yield v.length() > 60 ? v.substring(0, 57) + "..." : v;
            }
            case "int" -> String.valueOf(item.path("value").asInt());
            case "null" -> "(null)";
            case "bytes" -> {
                String b = item.path("value").asText("");
                yield "bytes[" + (b.length() * 3 / 4) + "]"; // approx base64 length
            }
            case "string_list" -> {
                JsonNode vals = item.get("values");
                if (vals == null) yield "[]";
                int n = vals.size();
                yield "[" + n + " strings]";
            }
            case "packed_strings" -> {
                JsonNode entries = item.get("entries");
                JsonNode names = item.get("names");
                int ne = entries != null ? entries.size() : 0;
                int nn = names != null ? names.size() : 0;
                yield ne + " segments, " + nn + " named";
            }
            default -> item.has("value") ? item.path("value").asText() : "";
        };
    }

    private void buildPoolScreenRefs() {
        poolScreenRefs = new HashMap<>();
        for (var screen : screens) {
            addRef(screen.title, screen.name + ".title");
            addRef(screen.leftKey.label, screen.name + ".lsk");
            addRef(screen.rightKey.label, screen.name + ".rsk");
            addRef(screen.leftKey.cmd, screen.name + ".lskCmd");
            addRef(screen.rightKey.cmd, screen.name + ".rskCmd");
            for (int i = 0; i < screen.items.size(); i++) {
                var item = screen.items.get(i);
                String prefix = screen.name + "[" + i + "]";
                addRef(item.label, prefix + ".label");
                addRef(item.cmd, prefix + ".cmd");
                addRef(item.condKey, prefix + ".cond");
                addRef(item.dataKey, prefix + ".data");
                addRef(item.hint, prefix + ".hint");
            }
        }
    }

    private void addRef(int poolIndex, String ref) {
        if (poolIndex > 0 && poolIndex < 1406) {
            poolScreenRefs.computeIfAbsent(poolIndex, k -> new ArrayList<>()).add(ref);
        }
    }

    // --- Save ---

    public void save() throws IOException {
        if (configPath == null || configRoot == null) return;

        ObjectNode root = (ObjectNode) configRoot;
        ArrayNode screensArray = mapper.createArrayNode();

        for (var screen : screens) {
            // Start from original JSON, patch only editor-managed fields
            ObjectNode s = screen.rawJson != null
                ? screen.rawJson.deepCopy()
                : mapper.createObjectNode();

            s.put("name", screen.name);
            s.put("title", screen.title);
            s.put("screenId", screen.screenId);
            s.put("type", screen.type.toConfigString());
            if (screen.checkboxes) s.put("checkboxes", true); else s.remove("checkboxes");
            if (!screen.handler.isEmpty()) s.put("handler", screen.handler);
            s.put("extraCmd", screen.extraCmd);

            // Patch soft keys in-place
            patchSoftKey(s, "leftSoftKey", screen.leftKey);
            patchSoftKey(s, "rightSoftKey", screen.rightKey);

            // Rebuild items array (items can be added/removed/reordered)
            ArrayNode itemsArray = mapper.createArrayNode();
            for (var item : screen.items) {
                ObjectNode m = item.rawJson != null
                    ? mapper.convertValue(item.rawJson, ObjectNode.class)
                    : mapper.createObjectNode();
                // Patch only editor-managed fields
                m.put("type", item.type.toConfigString());
                m.put("label", item.label);
                if (item.labelHint != null && !item.labelHint.isEmpty()) {
                    m.put("label_", item.labelHint);
                } else {
                    m.remove("label_");
                }
                // Always write icon and cmd — cfg_tool.py requires them
                m.put("icon", item.icon);
                m.put("cmd", item.cmd);
                if (item.condKey > 0) m.put("condKey", item.condKey);
                itemsArray.add(m);
            }
            s.set("items", itemsArray);
            screensArray.add(s);
        }

        root.set("screens", screensArray);
        mapper.writeValue(configPath.toFile(), root);
    }

    private void patchSoftKey(ObjectNode screen, String field, ScreenDef.SoftKey key) {
        ObjectNode sk = screen.has(field) && screen.get(field).isObject()
            ? (ObjectNode) screen.get(field)
            : mapper.createObjectNode();
        sk.put("label", key.label);
        sk.put("cmd", key.cmd);
        if (!key.labelHint.isEmpty()) sk.put("label_", key.labelHint); else sk.remove("label_");
        screen.set(field, sk);
    }

    // --- Palette loading ---

    private void loadPalette(Path path) throws IOException {
        JsonNode root = mapper.readTree(path.toFile());
        JsonNode themes = root.get("themes");

        palette = new int[themes.size()][PALETTE_KEYS.length];

        for (int t = 0; t < themes.size(); t++) {
            JsonNode theme = themes.get(t);
            for (int c = 0; c < PALETTE_KEYS.length; c++) {
                JsonNode hex = theme.get(PALETTE_KEYS[c]);
                if (hex != null) {
                    palette[t][c] = parseHexColor(hex.asText());
                }
            }
        }
    }

    private static int parseHexColor(String hex) {
        hex = hex.startsWith("#") ? hex.substring(1) : hex;
        return Integer.parseInt(hex, 16);
    }
}
