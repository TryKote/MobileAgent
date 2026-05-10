package com.trykote.editor.analysis;

import com.trykote.editor.ScreenCategory;
import com.trykote.editor.ScreenDef;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Builds a navigation graph between screens.
 * Parses handler source code to find all ScreenId → ScreenDef transitions.
 */
public class ScreenFlowGraph {

    public record Node(String name, String handler, int screenId, ScreenCategory category,
                       String screenType, double x, double y) {}
    public record Edge(String source, String target, String label) {}

    private final Map<String, Node> nodes = new LinkedHashMap<>();
    private final List<Edge> edges = new ArrayList<>();

    // ScreenId name → int
    private final Map<String, Integer> screenIdValues = new LinkedHashMap<>();

    public Map<String, Node> getNodes() { return nodes; }
    public List<Edge> getEdges() { return edges; }

    public void build(Path sourcesDir, List<ScreenDef> screens) throws IOException {
        Path handlerDir = sourcesDir.resolve("com/trykote/mobileagent/ui/handler");
        Path screenIdFile = sourcesDir.resolve("com/trykote/mobileagent/core/ScreenId.java");

        parseScreenIds(screenIdFile);

        // Create nodes from screen definitions
        for (var s : screens) {
            nodes.put(s.name, new Node(s.name, s.handler, s.screenId,
                s.type.category, s.type.toConfigString(), 0, 0));
        }

        // AST-based call graph analysis across ALL source files
        SourceAnalyzer analyzer = new SourceAnalyzer();
        analyzer.analyze(sourcesDir);
        for (var entry : analyzer.getEdges().entrySet()) {
            String source = entry.getKey();
            if (!nodes.containsKey(source)) continue;
            for (String target : entry.getValue()) {
                if (nodes.containsKey(target)) {
                    edges.add(new Edge(source, target, "call"));
                }
            }
        }

        // Extract cmd→ScreenId navigation from screen definitions
        extractCmdEdges(screens);

        // Deduplicate edges
        var seen = new HashSet<String>();
        edges.removeIf(e -> !seen.add(e.source() + "|" + e.target()));

        layoutNodes();
    }

    private void parseScreenIds(Path file) throws IOException {
        String src = Files.readString(file);
        Pattern p = Pattern.compile("public\\s+static\\s+final\\s+int\\s+(\\w+)\\s*=\\s*(\\d+)");
        Matcher m = p.matcher(src);
        while (m.find()) {
            screenIdValues.put(m.group(1), Integer.parseInt(m.group(2)));
        }
    }

    /**
     * Extract navigation edges from cmd values in screen definitions.
     * If a cmd value matches a known ScreenId, it's a potential navigation path.
     */
    private void extractCmdEdges(List<ScreenDef> screens) {
        // Build reverse ScreenId map: value → name
        var idToName = new HashMap<Integer, String>();
        for (var entry : screenIdValues.entrySet()) {
            if (nodes.containsKey(entry.getKey())) {
                idToName.put(entry.getValue(), entry.getKey());
            }
        }

        for (var screen : screens) {
            String src = screen.name;
            // Left soft key cmd
            if (screen.leftKey != null) {
                addCmdEdge(src, screen.leftKey.cmd, idToName);
            }
            // Item cmds
            for (var item : screen.items) {
                addCmdEdge(src, item.cmd, idToName);
            }
        }
    }

    private void addCmdEdge(String source, int cmd, Map<Integer, String> idToName) {
        String target = idToName.get(cmd);
        if (target != null && !target.equals(source)
            && !"NONE".equals(target) && !"CLOSE".equals(target) && !"UNUSED".equals(target)) {
            edges.add(new Edge(source, target, "cmd"));
        }
    }

    /**
     * Layout nodes in columns grouped by handler, with barycenter ordering
     * to minimize edge crossings.
     */
    private void layoutNodes() {
        double colWidth = 200;
        double rowHeight = 36;
        double colGap = 40;
        double headerHeight = 24;

        // Group nodes by handler
        var byHandler = new LinkedHashMap<String, List<String>>();
        for (var node : nodes.values()) {
            String h = node.handler().isEmpty() ? "Other" : node.handler();
            byHandler.computeIfAbsent(h, k -> new ArrayList<>()).add(node.name());
        }

        // Order columns by inter-handler connectivity (most connected first)
        var handlerOrder = orderColumns(byHandler);

        // Build adjacency for barycenter computation
        var neighbors = new HashMap<String, List<String>>();
        for (var node : nodes.values()) neighbors.put(node.name(), new ArrayList<>());
        for (var edge : edges) {
            neighbors.computeIfAbsent(edge.source(), k -> new ArrayList<>()).add(edge.target());
            neighbors.computeIfAbsent(edge.target(), k -> new ArrayList<>()).add(edge.source());
        }

        // Initial placement
        var columns = new ArrayList<List<String>>();
        for (String handler : handlerOrder) {
            columns.add(new ArrayList<>(byHandler.get(handler)));
        }

        // Assign initial y-positions
        var yPos = new HashMap<String, Double>();
        for (var col : columns) {
            for (int i = 0; i < col.size(); i++) {
                yPos.put(col.get(i), headerHeight + i * rowHeight);
            }
        }

        // Barycenter iterations: reorder nodes within columns to minimize crossings
        for (int iter = 0; iter < 12; iter++) {
            // Alternate direction each iteration
            int start = (iter % 2 == 0) ? 0 : columns.size() - 1;
            int end = (iter % 2 == 0) ? columns.size() : -1;
            int step = (iter % 2 == 0) ? 1 : -1;

            for (int ci = start; ci != end; ci += step) {
                List<String> col = columns.get(ci);
                // Compute barycenter for each node
                var barycenters = new HashMap<String, Double>();
                for (String name : col) {
                    var nbrs = neighbors.getOrDefault(name, List.of());
                    if (nbrs.isEmpty()) {
                        barycenters.put(name, yPos.getOrDefault(name, 0.0));
                        continue;
                    }
                    double sum = 0;
                    int count = 0;
                    for (String nbr : nbrs) {
                        Double ny = yPos.get(nbr);
                        if (ny != null) { sum += ny; count++; }
                    }
                    barycenters.put(name, count > 0 ? sum / count : yPos.getOrDefault(name, 0.0));
                }
                // Sort by barycenter
                col.sort(Comparator.comparingDouble(n -> barycenters.getOrDefault(n, 0.0)));
                // Update y-positions
                for (int i = 0; i < col.size(); i++) {
                    yPos.put(col.get(i), headerHeight + i * rowHeight);
                }
            }
        }

        // Apply final positions
        for (int ci = 0; ci < columns.size(); ci++) {
            double x = ci * (colWidth + colGap);
            List<String> col = columns.get(ci);
            for (int row = 0; row < col.size(); row++) {
                String name = col.get(row);
                Node old = nodes.get(name);
                double y = headerHeight + row * rowHeight;
                nodes.put(name, new Node(old.name(), old.handler(), old.screenId(), old.category(), old.screenType(), x, y));
            }
        }
    }

    /** Order handler columns to place connected handlers adjacent. */
    private List<String> orderColumns(Map<String, List<String>> byHandler) {
        // Count cross-handler edges
        var handlerNames = new ArrayList<>(byHandler.keySet());
        var connectivity = new HashMap<String, Map<String, Integer>>();
        for (var edge : edges) {
            Node src = nodes.get(edge.source());
            Node tgt = nodes.get(edge.target());
            if (src == null || tgt == null) continue;
            String sh = src.handler().isEmpty() ? "Other" : src.handler();
            String th = tgt.handler().isEmpty() ? "Other" : tgt.handler();
            if (!sh.equals(th)) {
                connectivity.computeIfAbsent(sh, k -> new HashMap<>())
                    .merge(th, 1, Integer::sum);
                connectivity.computeIfAbsent(th, k -> new HashMap<>())
                    .merge(sh, 1, Integer::sum);
            }
        }

        // Greedy: start with most-connected handler, then add nearest neighbor
        var result = new ArrayList<String>();
        var remaining = new LinkedHashSet<>(handlerNames);

        // Start with handler that has most cross-edges
        String best = handlerNames.get(0);
        int bestCount = 0;
        for (String h : handlerNames) {
            int count = connectivity.getOrDefault(h, Map.of()).values().stream()
                .mapToInt(Integer::intValue).sum();
            if (count > bestCount) { bestCount = count; best = h; }
        }
        result.add(best);
        remaining.remove(best);

        while (!remaining.isEmpty()) {
            String last = result.get(result.size() - 1);
            var lastConns = connectivity.getOrDefault(last, Map.of());
            // Find remaining handler with most connections to last
            String next = null;
            int nextScore = -1;
            for (String h : remaining) {
                int score = lastConns.getOrDefault(h, 0);
                if (score > nextScore) { nextScore = score; next = h; }
            }
            if (next == null) next = remaining.iterator().next();
            result.add(next);
            remaining.remove(next);
        }
        return result;
    }

    /** Get handler group headers with positions for rendering. */
    public Map<String, double[]> getHandlerHeaders() {
        var result = new LinkedHashMap<String, double[]>();
        var byHandler = new LinkedHashMap<String, List<Node>>();
        for (var node : nodes.values()) {
            String h = node.handler().isEmpty() ? "Other" : node.handler();
            byHandler.computeIfAbsent(h, k -> new ArrayList<>()).add(node);
        }
        for (var entry : byHandler.entrySet()) {
            var first = entry.getValue().get(0);
            result.put(entry.getKey(), new double[]{
                nodes.get(first.name()).x(), nodes.get(first.name()).y() - 20
            });
        }
        return result;
    }

    public List<Edge> getEdgesFrom(String nodeName) {
        return edges.stream().filter(e -> e.source().equals(nodeName)).toList();
    }

    public List<Edge> getEdgesTo(String nodeName) {
        return edges.stream().filter(e -> e.target().equals(nodeName)).toList();
    }

}
