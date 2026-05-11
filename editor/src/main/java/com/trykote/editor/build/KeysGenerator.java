package com.trykote.editor.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Generates all *Keys.java from annotated config.json.
 * Java replacement for: python3 tools/cfg_tool.py --gen-keys
 */
public class KeysGenerator {

    public static void generate(Path inputDir, Path outputDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode config = mapper.readTree(inputDir.resolve("config.json").toFile());

        // Collect constants per class: className → list of (name, value, comment)
        Map<String, List<Constant>> classConstants = new LinkedHashMap<>();

        // 1. Pool entries with "key" annotation (from zones)
        collectFromZones(config, classConstants);

        // 2. Block base constants
        collectFromBlocks(config, classConstants);

        // 3. intPoolKeys
        collectFromIntPoolKeys(config, classConstants);

        // 4. Pure constants
        collectFromConstants(config, classConstants);

        // 5. PackedStringKeys from packed_strings entry
        collectPackedStringKeys(config, classConstants);

        // Write one Java file per class
        int totalFiles = 0;
        for (var entry : classConstants.entrySet()) {
            String className = entry.getKey();
            List<Constant> constants = entry.getValue();

            // Sort by value
            constants.sort(Comparator.comparingInt(c -> c.value));

            // Deduplicate (same name+value)
            var deduped = new ArrayList<Constant>();
            var seen = new HashSet<String>();
            for (var c : constants) {
                if (seen.add(c.name)) deduped.add(c);
            }

            writeJavaFile(outputDir, className, deduped);
            totalFiles++;
        }
        System.out.println("Wrote " + totalFiles + " Keys files to " + outputDir);
    }

    private static void collectFromZones(JsonNode config, Map<String, List<Constant>> out) {
        JsonNode zones = config.get("zones");
        if (zones == null) return;

        for (JsonNode zone : zones) {
            int start = zone.path("start").asInt(0);
            JsonNode items = zone.get("items");
            if (items == null) continue;

            for (int i = 0; i < items.size(); i++) {
                JsonNode item = items.get(i);
                String key = item.has("key") ? item.get("key").asText() : null;
                String cls = item.has("class") ? item.get("class").asText() : null;
                if (key == null || cls == null || key.isEmpty() || cls.isEmpty()) continue;

                int pos = item.has("position") ? item.get("position").asInt() : start + i;
                String comment = null;
                if ("string".equals(item.path("type").asText())) {
                    String val = item.path("value").asText("");
                    if (!val.isEmpty()) {
                        comment = val.length() > 50 ? val.substring(0, 47) + "..." : val;
                    }
                }
                out.computeIfAbsent(cls, k -> new ArrayList<>())
                   .add(new Constant(key, pos, comment));
            }
        }
    }

    private static void collectFromBlocks(JsonNode config, Map<String, List<Constant>> out) {
        JsonNode blocks = config.get("blocks");
        if (blocks == null) return;

        for (JsonNode block : blocks) {
            String cls = block.path("class").asText("");
            String key = block.path("key").asText("");
            if (cls.isEmpty() || key.isEmpty()) continue;

            int base = block.path("base").asInt();
            int count = block.path("count").asInt();

            out.computeIfAbsent(cls, k -> new ArrayList<>())
               .add(new Constant(key + "_BASE", base, null));
            out.computeIfAbsent(cls, k -> new ArrayList<>())
               .add(new Constant(key + "_COUNT", count, null));
        }
    }

    private static void collectFromIntPoolKeys(JsonNode config, Map<String, List<Constant>> out) {
        JsonNode ipk = config.get("intPoolKeys");
        if (ipk == null) return;

        for (JsonNode entry : ipk) {
            String cls = entry.path("class").asText("");
            String key = entry.path("key").asText("");
            int value = entry.path("value").asInt();
            if (cls.isEmpty() || key.isEmpty()) continue;
            out.computeIfAbsent(cls, k -> new ArrayList<>())
               .add(new Constant(key, value, null));
        }
    }

    private static void collectFromConstants(JsonNode config, Map<String, List<Constant>> out) {
        JsonNode constants = config.get("constants");
        if (constants == null) return;

        for (JsonNode entry : constants) {
            String cls = entry.path("class").asText("");
            String key = entry.path("key").asText("");
            int value = entry.path("value").asInt();
            if (cls.isEmpty() || key.isEmpty()) continue;
            out.computeIfAbsent(cls, k -> new ArrayList<>())
               .add(new Constant(key, value, null));
        }
    }

    private static void collectPackedStringKeys(JsonNode config, Map<String, List<Constant>> out) {
        // Find packed_strings entry in zones and extract names
        JsonNode zones = config.get("zones");
        if (zones == null) return;

        for (JsonNode zone : zones) {
            JsonNode items = zone.get("items");
            if (items == null) continue;
            for (JsonNode item : items) {
                if (!"packed_strings".equals(item.path("type").asText())) continue;

                JsonNode names = item.get("names");
                if (names == null) continue;

                String cls = item.has("class") ? item.get("class").asText() : "PackedStringKeys";
                int basePosition = item.has("position") ? item.get("position").asInt() : 0;

                for (JsonNode nameEntry : names) {
                    String name = nameEntry.path("name").asText("");
                    int offset = nameEntry.path("offset").asInt();
                    int length = nameEntry.path("length").asInt();
                    if (name.isEmpty()) continue;

                    // PackedStringKeys value = (length << 16) | offset
                    // AppState.getString: offset = key & 0xFFFF, length = key >> 16
                    int packedValue = (length << 16) | offset;
                    String comment = nameEntry.has("value")
                        ? nameEntry.get("value").asText() : null;
                    if (comment != null && comment.length() > 50)
                        comment = comment.substring(0, 47) + "...";

                    out.computeIfAbsent(cls, k -> new ArrayList<>())
                       .add(new Constant(name, packedValue, comment));
                }
            }
        }
    }

    private static void writeJavaFile(Path outputDir, String className, List<Constant> constants)
            throws IOException {
        var lines = new ArrayList<String>();
        lines.add("package com.trykote.mobileagent.key;");
        lines.add("");
        lines.add("/**");
        lines.add(" * Generated by editor --gen-keys. Do not edit manually.");
        lines.add(" */");
        lines.add("public final class " + className + " {");
        lines.add("    private " + className + "() {}");
        lines.add("");

        for (var c : constants) {
            if (c.comment != null) {
                lines.add("    /** " + c.comment + " */");
            }
            lines.add("    public static final int " + c.name + " = " + c.value + ";");
        }

        lines.add("}");
        lines.add("");

        Files.createDirectories(outputDir);
        Files.writeString(outputDir.resolve(className + ".java"), String.join("\n", lines));
    }

    record Constant(String name, int value, String comment) {}
}
