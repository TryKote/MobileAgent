package com.trykote.editor.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Parses handler Java source files and builds a catalog:
 * (screenId, cmd) → handler class, method, return value, navigation target.
 *
 * Can be run standalone: java ActionCatalogBuilder &lt;sources-dir&gt; &lt;output.json&gt;
 */
public class ActionCatalogBuilder {

    /** One entry in the action catalog. */
    public static class CatalogEntry {
        public String handler;
        public String screenIdName;
        public int screenId;
        public String method;
        public String returnValue;
        public String actionCheck;
        public String navigation;      // from onMenuItemSelected: showScreen/pushScreen
        public String screenDef;       // from buildScreen: which ScreenDef is used
        public String buildNavigation; // from buildScreen: showScreen/pushScreen calls

        public CatalogEntry() {}
    }

    // ScreenId name → numeric value (parsed from ScreenId.java)
    private final Map<String, Integer> screenIdValues = new LinkedHashMap<>();
    // Reverse: numeric → name
    private final Map<Integer, String> screenIdNames = new LinkedHashMap<>();

    private final List<CatalogEntry> catalog = new ArrayList<>();

    public List<CatalogEntry> getCatalog() { return catalog; }

    public void build(Path sourcesDir) throws IOException {
        Path handlerDir = sourcesDir.resolve("com/trykote/mobileagent/ui/handler");
        Path screenIdFile = sourcesDir.resolve("com/trykote/mobileagent/core/ScreenId.java");

        parseScreenIds(screenIdFile);

        try (var stream = Files.list(handlerDir)) {
            stream.filter(p -> p.getFileName().toString().endsWith("Handler.java"))
                  .filter(p -> !p.getFileName().toString().equals("ScreenHandler.java"))
                  .filter(p -> !p.getFileName().toString().equals("BaseScreenHandler.java"))
                  .filter(p -> !p.getFileName().toString().equals("ScreenHandlerRegistry.java"))
                  .sorted()
                  .forEach(this::parseHandler);
        }
    }

    private void parseScreenIds(Path file) throws IOException {
        String src = Files.readString(file);
        Pattern p = Pattern.compile("public\\s+static\\s+final\\s+int\\s+(\\w+)\\s*=\\s*(\\d+)");
        Matcher m = p.matcher(src);
        while (m.find()) {
            String name = m.group(1);
            int value = Integer.parseInt(m.group(2));
            screenIdValues.put(name, value);
            screenIdNames.put(value, name);
        }
    }

    private void parseHandler(Path file) {
        String handlerName = file.getFileName().toString().replace(".java", "");
        try {
            String src = Files.readString(file);
            parseBuildScreen(src, handlerName);
            parseOnMenuItemSelected(src, handlerName);
        } catch (IOException e) {
            System.err.println("Failed to parse " + file + ": " + e.getMessage());
        }
    }

    /**
     * Extract buildScreen cases: which ScreenDef is created for each ScreenId.
     */
    private void parseBuildScreen(String src, String handler) {
        // Find buildScreen method body
        String body = extractMethodBody(src, "buildScreen");
        if (body == null) return;

        // Pattern: case ScreenId.XXX: ... createScreen(ScreenDef.YYY)
        Pattern caseP = Pattern.compile("case\\s+ScreenId\\.(\\w+)\\s*:");
        Pattern defP = Pattern.compile("createScreen\\(ScreenDef\\.(\\w+)\\)");

        Matcher caseM = caseP.matcher(body);
        while (caseM.find()) {
            String screenName = caseM.group(1);
            int caseStart = caseM.end();

            // Find next case or end
            int caseEnd = body.length();
            Matcher nextCase = caseP.matcher(body);
            if (nextCase.find(caseStart)) {
                caseEnd = nextCase.start();
            }

            String caseBody = body.substring(caseStart, caseEnd);
            Matcher defM = defP.matcher(caseBody);
            if (defM.find()) {
                String screenDef = defM.group(1);
                CatalogEntry entry = findOrCreate(handler, screenName, "buildScreen");
                entry.screenDef = screenDef;

                // Also collect all ScreenDef references as navigation targets
                List<String> allDefs = new ArrayList<>();
                Matcher allDefsM = defP.matcher(caseBody);
                while (allDefsM.find()) {
                    String d = allDefsM.group(1);
                    if (!d.equals(screenDef) && !allDefs.contains(d)) allDefs.add(d);
                }
                if (!allDefs.isEmpty()) {
                    entry.buildNavigation = String.join(", ", allDefs);
                }
            }
        }
    }

    /**
     * Extract onMenuItemSelected cases: screenId → action checks → return values.
     */
    private void parseOnMenuItemSelected(String src, String handler) {
        String body = extractMethodBody(src, "onMenuItemSelected");
        if (body == null) return;

        Pattern caseP = Pattern.compile("case\\s+ScreenId\\.(\\w+)\\s*:?\\s*\\{?");
        Matcher caseM = caseP.matcher(body);

        List<int[]> casePositions = new ArrayList<>();
        List<String> caseNames = new ArrayList<>();

        while (caseM.find()) {
            casePositions.add(new int[]{caseM.start(), caseM.end()});
            caseNames.add(caseM.group(1));
        }

        for (int i = 0; i < casePositions.size(); i++) {
            int start = casePositions.get(i)[1];
            int end = (i + 1 < casePositions.size()) ? casePositions.get(i + 1)[0] : body.length();
            String caseBody = body.substring(start, end);
            String screenName = caseNames.get(i);

            CatalogEntry entry = findOrCreate(handler, screenName, "onMenuItemSelected");

            // Extract return value
            Pattern retP = Pattern.compile("return\\s+(-?\\d+|\\w+[.\\w()]*)\\s*;");
            Matcher retM = retP.matcher(caseBody);
            List<String> returns = new ArrayList<>();
            while (retM.find()) {
                returns.add(retM.group(1));
            }
            if (!returns.isEmpty()) {
                entry.returnValue = String.join(" | ", returns);
            }

            // Extract action == checks
            Pattern actP = Pattern.compile("action\\s*==\\s*(ScreenId\\.(\\w+)|\\d+)");
            Matcher actM = actP.matcher(caseBody);
            List<String> actions = new ArrayList<>();
            while (actM.find()) {
                actions.add(actM.group(1));
            }
            if (!actions.isEmpty()) {
                entry.actionCheck = String.join(", ", actions);
            }

            // Extract navigation (showScreen/pushScreen with ScreenDef)
            Pattern navP = Pattern.compile("(?:showScreen|pushScreen)\\(.*?createScreen\\(ScreenDef\\.(\\w+)\\)");
            Matcher navM = navP.matcher(caseBody);
            List<String> navs = new ArrayList<>();
            while (navM.find()) {
                navs.add(navM.group(1));
            }
            if (!navs.isEmpty()) {
                entry.navigation = String.join(", ", navs);
            }
        }
    }

    private CatalogEntry findOrCreate(String handler, String screenName, String method) {
        // Try to find existing entry for this handler+screen
        for (CatalogEntry e : catalog) {
            if (e.handler.equals(handler) && e.screenIdName.equals(screenName)) {
                if (method.equals("onMenuItemSelected")) {
                    e.method = method;
                }
                return e;
            }
        }
        CatalogEntry entry = new CatalogEntry();
        entry.handler = handler;
        entry.screenIdName = screenName;
        entry.screenId = screenIdValues.getOrDefault(screenName, -1);
        entry.method = method;
        return addEntry(entry);
    }

    private CatalogEntry addEntry(CatalogEntry entry) {
        catalog.add(entry);
        return entry;
    }

    /**
     * Extract the body of a method by name (handles brace-counting).
     */
    private static String extractMethodBody(String src, String methodName) {
        // Find method signature
        int idx = src.indexOf(" " + methodName + "(");
        if (idx < 0) return null;

        // Find opening brace
        int braceStart = src.indexOf('{', idx);
        if (braceStart < 0) return null;

        // Count braces to find matching close
        int depth = 1;
        int pos = braceStart + 1;
        while (pos < src.length() && depth > 0) {
            char c = src.charAt(pos);
            if (c == '{') depth++;
            else if (c == '}') depth--;
            // Skip string/char literals
            else if (c == '"') {
                pos++;
                while (pos < src.length() && src.charAt(pos) != '"') {
                    if (src.charAt(pos) == '\\') pos++;
                    pos++;
                }
            } else if (c == '\'') {
                pos++;
                while (pos < src.length() && src.charAt(pos) != '\'') {
                    if (src.charAt(pos) == '\\') pos++;
                    pos++;
                }
            }
            pos++;
        }

        return src.substring(braceStart + 1, pos - 1);
    }

    public void saveJson(Path outputPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(outputPath.toFile(), catalog);
    }

    /**
     * Build catalog and return as map: screenId → list of entries.
     */
    public Map<Integer, List<CatalogEntry>> buildIndex() {
        Map<Integer, List<CatalogEntry>> index = new LinkedHashMap<>();
        for (CatalogEntry e : catalog) {
            if (e.screenId >= 0) {
                index.computeIfAbsent(e.screenId, k -> new ArrayList<>()).add(e);
            }
        }
        return index;
    }

    // CLI entry point
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: ActionCatalogBuilder <sources-dir> <output.json>");
            System.exit(1);
        }
        ActionCatalogBuilder builder = new ActionCatalogBuilder();
        builder.build(Path.of(args[0]));
        builder.saveJson(Path.of(args[1]));
        System.out.println("Catalog: " + builder.catalog.size() + " entries written to " + args[1]);
    }
}
