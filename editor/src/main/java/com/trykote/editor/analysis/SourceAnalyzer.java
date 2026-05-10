package com.trykote.editor.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * AST-based call graph analysis for screen navigation.
 * Uses JavaParser to trace ScreenId → ScreenDef connections through method calls.
 */
public class SourceAnalyzer {

    /** method key = "ClassName.methodName" */
    // Call graph: method → set of called methods
    private final Map<String, Set<String>> callGraph = new HashMap<>();
    // Which methods reference which ScreenDef constants
    private final Map<String, Set<String>> screenDefRefs = new HashMap<>();
    // case ScreenId.XXX blocks → methods called from within them
    private final List<CaseBlock> caseBlocks = new ArrayList<>();

    record CaseBlock(String screenIdName, Set<String> calledMethods) {}

    /** Resulting edges: source ScreenId name → set of target ScreenDef names. */
    private final Map<String, Set<String>> edges = new LinkedHashMap<>();

    public Map<String, Set<String>> getEdges() { return edges; }

    public void analyze(Path sourcesDir) throws IOException {
        // Parse all Java files
        try (var stream = Files.walk(sourcesDir)) {
            stream.filter(p -> p.toString().endsWith(".java"))
                  .filter(p -> !p.toString().contains("/test/"))
                  .forEach(this::parseFile);
        }

        // Trace: for each case block, find all reachable ScreenDef refs
        for (CaseBlock block : caseBlocks) {
            Set<String> reachable = new HashSet<>();
            // Direct ScreenDef refs from methods called in the case block
            Set<String> visited = new HashSet<>();
            Deque<String> queue = new ArrayDeque<>(block.calledMethods);
            while (!queue.isEmpty()) {
                String method = queue.poll();
                if (!visited.add(method)) continue;
                // Collect ScreenDef refs from this method
                Set<String> refs = screenDefRefs.get(method);
                if (refs != null) reachable.addAll(refs);
                // Follow calls (limit depth to avoid explosion)
                if (visited.size() < 50) {
                    Set<String> called = callGraph.get(method);
                    if (called != null) queue.addAll(called);
                }
            }
            // Remove self-references
            reachable.remove(block.screenIdName);
            if (!reachable.isEmpty()) {
                edges.computeIfAbsent(block.screenIdName, k -> new LinkedHashSet<>())
                    .addAll(reachable);
            }
        }
    }

    private void parseFile(Path file) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(file);
            String fileName = file.getFileName().toString().replace(".java", "");

            // Find all method declarations
            cu.findAll(MethodDeclaration.class).forEach(method -> {
                String methodKey = fileName + "." + method.getNameAsString();

                // Collect method calls
                Set<String> calls = new HashSet<>();
                method.findAll(MethodCallExpr.class).forEach(call -> {
                    String scope = call.getScope()
                        .map(s -> {
                            if (s instanceof NameExpr ne) return ne.getNameAsString();
                            if (s instanceof FieldAccessExpr fa) return fa.getNameAsString();
                            return s.toString().replaceAll("\\(.*", "");
                        })
                        .orElse(fileName);
                    calls.add(scope + "." + call.getNameAsString());
                    // Also add without scope for same-class resolution
                    if (scope.equals(fileName)) {
                        calls.add(fileName + "." + call.getNameAsString());
                    }
                });
                callGraph.put(methodKey, calls);

                // Collect ScreenDef references
                Set<String> defs = new HashSet<>();
                method.findAll(FieldAccessExpr.class).forEach(fa -> {
                    if (fa.getScope().toString().equals("ScreenDef")) {
                        defs.add(fa.getNameAsString());
                    }
                });
                if (!defs.isEmpty()) {
                    screenDefRefs.put(methodKey, defs);
                }
            });

            // Find switch statements on screenId parameter
            cu.findAll(SwitchStmt.class).forEach(sw -> {
                String selector = sw.getSelector().toString();
                if (!selector.equals("screenId") && !selector.equals("currentScreenId")) return;

                // Find enclosing method
                String enclosingMethod = sw.findAncestor(MethodDeclaration.class)
                    .map(m -> fileName + "." + m.getNameAsString())
                    .orElse(fileName + ".unknown");

                for (SwitchEntry entry : sw.getEntries()) {
                    for (var label : entry.getLabels()) {
                        String labelStr = label.toString();
                        if (!labelStr.startsWith("ScreenId.")) continue;
                        String screenIdName = labelStr.substring("ScreenId.".length());

                        // Collect all methods called within this case entry
                        Set<String> calledInCase = new HashSet<>();
                        entry.findAll(MethodCallExpr.class).forEach(call -> {
                            String scope = call.getScope()
                                .map(s -> {
                                    if (s instanceof NameExpr ne) return ne.getNameAsString();
                                    if (s instanceof FieldAccessExpr fa) return fa.getNameAsString();
                                    return s.toString().replaceAll("\\(.*", "");
                                })
                                .orElse(fileName);
                            calledInCase.add(scope + "." + call.getNameAsString());
                        });

                        // Also include the enclosing method's direct ScreenDef refs
                        // (in case createScreen is directly in the case block)
                        Set<String> directDefs = new HashSet<>();
                        entry.findAll(FieldAccessExpr.class).forEach(fa -> {
                            if (fa.getScope().toString().equals("ScreenDef")) {
                                directDefs.add(fa.getNameAsString());
                            }
                        });

                        // Create a virtual method key for this case block's direct refs
                        if (!directDefs.isEmpty()) {
                            String caseKey = fileName + ".__case_" + screenIdName;
                            screenDefRefs.put(caseKey, directDefs);
                            calledInCase.add(caseKey);
                        }

                        caseBlocks.add(new CaseBlock(screenIdName, calledInCase));
                    }
                }
            });

        } catch (Exception e) {
            // Skip unparseable files silently
        }
    }
}
