package com.trykote.editor.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Enriches config.json screen definitions with _-suffix annotations:
 * cmd_, condKey_, stateKey_, valueKey_, indexKey_, dataKey_, targetOffset_.
 *
 * Java replacement for: python3 tools/cfg_tool.py --annotate-screens
 */
public class ScreenAnnotator {

    public static void annotate(Path inputDir) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Path configPath = inputDir.resolve("config.json");
        JsonNode root = mapper.readTree(configPath.toFile());

        // Load actions.json
        Map<Integer, String> actions = loadActions(inputDir.resolve("actions.json"));

        // Build key name map from zones + intPoolKeys
        Map<Integer, String> keyMap = buildKeyMap(root);

        // Build string pool for label resolution
        Map<Integer, String> stringPool = buildStringPool(root);

        int[] stats = new int[4]; // cmd, condKey, targetOffset, other

        JsonNode screens = root.get("screens");
        if (screens != null) {
            for (JsonNode screenNode : screens) {
                ObjectNode screen = (ObjectNode) screenNode;

                // Soft key cmd_
                annotateSoftKey(screen, "leftSoftKey", actions, stats);
                annotateSoftKey(screen, "rightSoftKey", actions, stats);

                // extraCmd_
                annotateCmd(screen, "extraCmd", actions, stats);

                // Items
                JsonNode items = screen.get("items");
                if (items != null) {
                    for (JsonNode itemNode : items) {
                        ObjectNode item = (ObjectNode) itemNode;
                        annotateCmd(item, "cmd", actions, stats);
                        annotateKey(item, "condKey", keyMap, stats);
                        annotateKey(item, "dataKey", keyMap, stats);
                        annotateKey(item, "valueKey", keyMap, stats);
                        annotateKey(item, "indexKey", keyMap, stats);
                        annotateKey(item, "stateKey", keyMap, stats);
                        annotateKey(item, "poolIndex", keyMap, stats);

                        // label_ if missing
                        if (item.has("label") && !item.has("label_")) {
                            String s = stringPool.get(item.get("label").asInt());
                            if (s != null) item.put("label_", s);
                        }
                    }
                }
            }
        }

        mapper.writeValue(configPath.toFile(), root);

        int total = stats[0] + stats[1] + stats[2] + stats[3];
        System.out.println("Annotated " + total + " fields: cmd_=" + stats[0]
            + ", condKey_=" + stats[1] + ", targetOffset_=" + stats[2]
            + ", other=" + stats[3]);
    }

    private static void annotateSoftKey(ObjectNode screen, String field,
                                        Map<Integer, String> actions, int[] stats) {
        JsonNode sk = screen.get(field);
        if (sk == null || !sk.isObject()) return;
        annotateCmd((ObjectNode) sk, "cmd", actions, stats);
    }

    private static void annotateCmd(ObjectNode node, String field,
                                    Map<Integer, String> actions, int[] stats) {
        if (!node.has(field) || node.has(field + "_")) return;
        int cmd = node.get(field).asInt();
        if (cmd <= 0) return;
        String name = actions.get(cmd);
        if (name != null) {
            node.put(field + "_", name);
            stats[0]++;
        }
    }

    private static void annotateKey(ObjectNode node, String field,
                                    Map<Integer, String> keyMap, int[] stats) {
        if (!node.has(field) || node.has(field + "_")) return;
        int value = node.get(field).asInt();
        if (value <= 0) return;
        String name = keyMap.get(value);
        if (name != null) {
            node.put(field + "_", name);
            stats[field.equals("condKey") ? 1 : 3]++;
        }
    }

    private static Map<Integer, String> loadActions(Path path) throws IOException {
        Map<Integer, String> result = new HashMap<>();
        if (!Files.exists(path)) return result;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(path.toFile());
        root.fields().forEachRemaining(e -> {
            JsonNode name = e.getValue().get("name");
            if (name != null) {
                try {
                    result.put(Integer.parseInt(e.getKey()), name.asText());
                } catch (NumberFormatException ignored) {}
            }
        });
        return result;
    }

    private static Map<Integer, String> buildKeyMap(JsonNode root) {
        Map<Integer, String> map = new HashMap<>();
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
                            map.put(pos, cls.isEmpty() ? key : cls + "." + key);
                        }
                    }
                }
            }
        }
        JsonNode ipk = root.get("intPoolKeys");
        if (ipk != null) {
            for (JsonNode entry : ipk) {
                int value = entry.path("value").asInt();
                String cls = entry.path("class").asText("");
                String key = entry.path("key").asText("");
                if (value > 0 && !key.isEmpty()) {
                    map.put(value, cls.isEmpty() ? key : cls + "." + key);
                }
            }
        }
        return map;
    }

    private static Map<Integer, String> buildStringPool(JsonNode root) {
        Map<Integer, String> pool = new HashMap<>();
        JsonNode zones = root.get("zones");
        if (zones == null) return pool;
        for (JsonNode zone : zones) {
            int start = zone.path("start").asInt(0);
            JsonNode items = zone.get("items");
            if (items == null) continue;
            for (int i = 0; i < items.size(); i++) {
                JsonNode item = items.get(i);
                int pos = item.has("position") ? item.get("position").asInt() : start + i;
                if ("string".equals(item.path("type").asText())) {
                    String val = item.path("value").asText("");
                    if (!val.isEmpty()) {
                        pool.put(pos, val.length() > 60 ? val.substring(0, 57) + "..." : val);
                    }
                }
            }
        }
        return pool;
    }
}
