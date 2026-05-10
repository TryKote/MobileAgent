package com.trykote.editor;

import com.trykote.editor.analysis.ActionCatalogBuilder;
import com.trykote.editor.analysis.ScreenFlowGraph;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.stmt.SwitchEntry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.nio.file.*;
import java.util.*;

public class EditorApp extends Application {

    private static final String[] THEME_NAMES = {
        "Copper", "Violet", "Mint", "Rose",
        "Silver", "Dark Purple", "Dark Blue", "Sky"
    };

    private ConfigLoader config;
    private ScreenRenderer renderer;
    private Canvas canvas;
    private ListView<String> itemList;
    private VBox itemEditPane;
    private Label statusLabel;
    private boolean dirty = false;
    private TabPane mainTabs;
    private Tab screensTab;

    // Currently edited
    private ScreenDef currentScreen;
    private int selectedItemIndex = -1;

    @Override
    public void start(Stage stage) {
        config = new ConfigLoader();
        try {
            Path resourcesDir = Path.of(getParameters().getRaw().isEmpty()
                ? "resources-src"
                : getParameters().getRaw().get(0));
            config.load(resourcesDir);
        } catch (Exception e) {
            showError("Failed to load config", e);
            return;
        }

        renderer = new ScreenRenderer(config);

        // Left panel: screen list + search
        VBox leftPanel = createScreenListPanel();

        // Center: canvas preview in scroll pane
        canvas = new Canvas(ScreenRenderer.SCREEN_W, ScreenRenderer.SCREEN_H);
        StackPane canvasInner = new StackPane(canvas);
        canvasInner.setAlignment(Pos.TOP_CENTER);
        canvasInner.setStyle("-fx-background-color: #2b2b2b; -fx-padding: 16;");
        ScrollPane canvasHolder = new ScrollPane(canvasInner);
        canvasHolder.setFitToWidth(true);
        canvasHolder.setMinWidth(ScreenRenderer.SCREEN_W + 48);

        // Right panel: item list + edit form
        VBox rightPanel = createRightPanel();

        // Toolbar
        ComboBox<String> themeBox = new ComboBox<>(
            FXCollections.observableArrayList(THEME_NAMES));
        themeBox.getSelectionModel().select(0);
        themeBox.setOnAction(e -> {
            renderer.setTheme(themeBox.getSelectionModel().getSelectedIndex());
            renderSelected();
        });

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> saveConfig());

        CheckBox spriteToggle = new CheckBox("Sprites");
        spriteToggle.setSelected(true);
        spriteToggle.setOnAction(e -> {
            renderer.setShowSprites(spriteToggle.isSelected());
            renderSelected();
        });

        statusLabel = new Label(config.getScreens().size() + " screens, "
            + config.getStringPool().size() + " strings");

        HBox toolbar = new HBox(8,
            new Label("Theme:"), themeBox,
            new Separator(Orientation.VERTICAL),
            saveBtn,
            spriteToggle,
            new Separator(Orientation.VERTICAL),
            statusLabel
        );
        toolbar.setPadding(new Insets(4, 8, 4, 8));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Screens tab
        SplitPane splitPane = new SplitPane(leftPanel, canvasHolder, rightPanel);
        splitPane.setDividerPositions(0.18, 0.58);

        screensTab = new Tab("Screens", splitPane);
        screensTab.setClosable(false);

        // Object Pool tab
        Tab poolTab = new Tab("Object Pool", createPoolBrowser());
        poolTab.setClosable(false);

        // Flow graph tab
        Tab flowTab = new Tab("Flow", createFlowGraph());
        flowTab.setClosable(false);

        mainTabs = new TabPane(screensTab, poolTab, flowTab);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(mainTabs);

        Scene scene = new Scene(root, 1100, 650);
        stage.setTitle("MobileAgent Screen Editor");
        stage.setScene(scene);
        stage.show();

        if (!config.getScreens().isEmpty() && !screenTree.getRoot().getChildren().isEmpty()) {
            var firstGroup = screenTree.getRoot().getChildren().get(0);
            if (!firstGroup.getChildren().isEmpty()) {
                screenTree.getSelectionModel().select(firstGroup.getChildren().get(0));
            }
        }
    }

    private TreeView<Object> screenTree;

    private VBox createScreenListPanel() {
        screenTree = new TreeView<>();
        screenTree.setShowRoot(false);

        TextField search = new TextField();
        search.setPromptText("Search...");
        search.textProperty().addListener((obs, oldVal, newVal) -> rebuildTree(newVal));

        screenTree.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null && newVal.getValue() instanceof ScreenDef sd) {
                    currentScreen = sd;
                    selectedItemIndex = -1;
                    renderSelected();
                    refreshItemList();
                    showScreenProps();
                    showHandlerCode();
                }
            });

        rebuildTree("");

        VBox panel = new VBox(4, search, screenTree);
        panel.setPadding(new Insets(8));
        VBox.setVgrow(screenTree, Priority.ALWAYS);
        return panel;
    }

    private void rebuildTree(String filter) {
        String f = filter.toLowerCase();
        TreeItem<Object> root = new TreeItem<>("Root");

        // Group by handler
        var grouped = new java.util.LinkedHashMap<String, java.util.List<ScreenDef>>();
        for (var s : config.getScreens()) {
            if (!f.isEmpty() && !s.name.toLowerCase().contains(f) && !s.handler.toLowerCase().contains(f)) {
                continue;
            }
            grouped.computeIfAbsent(s.handler.isEmpty() ? "No Handler" : s.handler, k -> new java.util.ArrayList<>()).add(s);
        }

        for (var entry : grouped.entrySet()) {
            TreeItem<Object> group = new TreeItem<>(entry.getKey() + " (" + entry.getValue().size() + ")");
            for (var s : entry.getValue()) {
                group.getChildren().add(new TreeItem<>(s));
            }
            group.setExpanded(true);
            root.getChildren().add(group);
        }

        screenTree.setRoot(root);
    }

    private TabPane rightTabs;

    private VBox createRightPanel() {
        // Properties tab
        itemList = new ListView<>();
        itemList.setPrefHeight(200);
        itemList.getSelectionModel().selectedIndexProperty().addListener(
            (obs, oldVal, newVal) -> {
                selectedItemIndex = newVal.intValue();
                showItemProps();
                renderSelected();
            });

        itemEditPane = new VBox(6);
        itemEditPane.setPadding(new Insets(4));

        ScrollPane scroll = new ScrollPane(itemEditPane);
        scroll.setFitToWidth(true);

        Button addBtn = new Button("+");
        Button removeBtn = new Button("-");
        Button upBtn = new Button("\u2191");
        Button downBtn = new Button("\u2193");
        Button dupBtn = new Button("Dup");

        addBtn.setOnAction(e -> addItem());
        removeBtn.setOnAction(e -> removeItem());
        upBtn.setOnAction(e -> moveItem(-1));
        downBtn.setOnAction(e -> moveItem(1));
        dupBtn.setOnAction(e -> duplicateItem());

        HBox itemButtons = new HBox(4, addBtn, removeBtn, upBtn, downBtn, dupBtn);
        itemButtons.setAlignment(Pos.CENTER_LEFT);

        VBox propsPanel = new VBox(4,
            new Label("Items"),
            itemList,
            itemButtons,
            new Separator(),
            scroll
        );
        propsPanel.setPadding(new Insets(8));
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Tab propsTab = new Tab("Properties", propsPanel);
        propsTab.setClosable(false);

        // Code tab
        Tab codeTab = new Tab("Code", createCodeViewer());
        codeTab.setClosable(false);

        rightTabs = new TabPane(propsTab, codeTab);
        rightTabs.setPrefWidth(420);
        rightTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox wrapper = new VBox(rightTabs);
        VBox.setVgrow(rightTabs, Priority.ALWAYS);
        return wrapper;
    }

    private void refreshItemList() {
        itemList.getItems().clear();
        if (currentScreen == null) return;

        for (int i = 0; i < currentScreen.items.size(); i++) {
            var item = currentScreen.items.get(i);
            String label = resolveLabel(item);
            itemList.getItems().add("[" + i + "] " + item.type + ": " + truncate(label, 25));
        }
    }

    private void showScreenProps() {
        itemEditPane.getChildren().clear();
        if (currentScreen == null) return;

        itemEditPane.getChildren().addAll(
            new Label("--- Screen ---"),
            labeledField("Name", currentScreen.name, v -> { currentScreen.name = v; markDirty(); }),
            labeledIntField("Screen ID", currentScreen.screenId, v -> { currentScreen.screenId = v; markDirty(); }),
            labeledCombo("Type", ScreenDef.ScreenType.values(), currentScreen.type,
                v -> { currentScreen.type = v; markDirty(); renderSelected(); }),
            pickerField("Title", currentScreen.title, config.getStringOptions(),
                v -> { currentScreen.title = v; markDirty(); renderSelected(); }),
            labeledCheck("Checkboxes", currentScreen.checkboxes, v -> { currentScreen.checkboxes = v; markDirty(); renderSelected(); }),
            labeledField("Handler", currentScreen.handler, v -> { currentScreen.handler = v; markDirty(); }),
            new Separator(),
            new Label("Left soft key"),
            pickerField("  Label", currentScreen.leftKey.label, config.getStringOptions(),
                v -> { currentScreen.leftKey.label = v; markDirty(); renderSelected(); }),
            pickerField("  Cmd", currentScreen.leftKey.cmd, config.getActionOptions(),
                v -> { currentScreen.leftKey.cmd = v; markDirty(); }),
            new Label("Right soft key"),
            pickerField("  Label", currentScreen.rightKey.label, config.getStringOptions(),
                v -> { currentScreen.rightKey.label = v; markDirty(); renderSelected(); }),
            pickerField("  Cmd", currentScreen.rightKey.cmd, config.getActionOptions(),
                v -> { currentScreen.rightKey.cmd = v; markDirty(); }),
            pickerField("Extra cmd", currentScreen.extraCmd, config.getActionOptions(),
                v -> { currentScreen.extraCmd = v; markDirty(); })
        );

        // Action catalog info
        var catalogEntries = config.getScreenCatalog(currentScreen.screenId);
        if (!catalogEntries.isEmpty()) {
            itemEditPane.getChildren().add(new Separator());
            Label header = new Label("Behaviour");
            header.setStyle("-fx-font-weight: bold;");
            itemEditPane.getChildren().add(header);

            for (var ce : catalogEntries) {
                VBox card = new VBox(3);
                card.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 6; " +
                              "-fx-border-color: #ddd; -fx-border-radius: 3; -fx-background-radius: 3;");

                // Handler with file path
                String handlerPath = "ui/handler/" + ce.handler + ".java";
                Hyperlink handlerLink = new Hyperlink(ce.handler);
                handlerLink.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
                handlerLink.setOnAction(e -> openHandlerFile(handlerPath));
                HBox handlerRow = new HBox(4, dimLabel("Code:"), handlerLink);
                card.getChildren().add(handlerRow);

                if (ce.actionCheck != null)
                    card.getChildren().add(infoRow("Condition:",
                        "only if action == " + ce.actionCheck, "#c00"));

                // What happens when item is selected
                String behavior = describeBehavior(ce);
                if (!behavior.isEmpty())
                    card.getChildren().add(infoRow("On select:", behavior, "#333"));

                // Where can you navigate from here
                String nav = describeNavigation(ce);
                if (!nav.isEmpty())
                    card.getChildren().add(infoRow("Goes to:", nav, "#06a"));

                itemEditPane.getChildren().add(card);
            }
        }
    }

    private Label dimLabel(String text) {
        Label l = new Label(text);
        l.setMinWidth(65);
        l.setStyle("-fx-text-fill: #888; -fx-font-size: 11;");
        return l;
    }

    private HBox infoRow(String label, String value, String color) {
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 11; -fx-text-fill: " + color + ";");
        val.setWrapText(true);
        return new HBox(4, dimLabel(label), val);
    }

    private String describeBehavior(ActionCatalogBuilder.CatalogEntry ce) {
        if (ce.returnValue == null) return "";

        // May have multiple return paths: "0 | NotificationHelper.showError(559)"
        String[] parts = ce.returnValue.split("\\s*\\|\\s*");
        List<String> descriptions = new ArrayList<>();
        for (String rv : parts) {
            rv = rv.trim();
            String desc = switch (rv) {
                case "0" -> "OK \u2192 left soft key action";
                case "-1" -> "ignored, screen stays";
                default -> {
                    if (rv.matches("\\d+"))
                        yield "opens screenId=" + rv;
                    yield rv;
                }
            };
            if (!descriptions.contains(desc)) descriptions.add(desc);
        }
        return String.join("  or  ", descriptions);
    }

    private String describeNavigation(ActionCatalogBuilder.CatalogEntry ce) {
        List<String> targets = new ArrayList<>();
        if (ce.navigation != null) targets.add(ce.navigation);
        if (ce.buildNavigation != null) targets.add(ce.buildNavigation);
        return String.join(", ", targets);
    }

    private void openHandlerFile(String relativePath) {
        try {
            Path full = Path.of("sources/com/trykote/mobileagent/" + relativePath).toAbsolutePath();
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(full.toFile());
            }
        } catch (Exception ignored) {}
    }

    private void showItemProps() {
        itemEditPane.getChildren().clear();
        if (currentScreen == null || selectedItemIndex < 0
            || selectedItemIndex >= currentScreen.items.size()) {
            showScreenProps();
            return;
        }

        var item = currentScreen.items.get(selectedItemIndex);

        itemEditPane.getChildren().addAll(
            new Label("--- Item [" + selectedItemIndex + "] ---"),
            labeledCombo("Type", ScreenDef.ItemType.values(), item.type,
                v -> { item.type = v; onItemStructureChanged(); showItemProps(); }),
            pickerField("Label", item.label, config.getStringOptions(),
                v -> { item.label = v; onItemChanged(); }),
            labeledIntField("Icon", item.icon, v -> { item.icon = v; onItemChanged(); }),
            pickerField("Cmd", item.cmd, config.getActionOptions(),
                v -> { item.cmd = v; onItemChanged(); }),
            pickerField("CondKey", item.condKey, config.getKeyOptions(),
                v -> { item.condKey = v; onItemChanged(); }),
            pickerField("DataKey", item.dataKey, config.getKeyOptions(),
                v -> { item.dataKey = v; onItemChanged(); }),
            pickerField("Hint", item.hint, config.getStringOptions(),
                v -> { item.hint = v; onItemChanged(); }),
            labeledField("Style", item.style != null ? item.style : "",
                v -> { item.style = v.isEmpty() ? null : v; onItemChanged(); }),
            new Separator(),
            new Label("Resolved: " + truncate(resolveLabel(item), 40))
        );
    }

    // --- Item actions ---

    private void addItem() {
        if (currentScreen == null) return;
        var item = new ScreenDef.Item(ScreenDef.ItemType.ACTION, 0, "", 0, 0, 0, 0, 0, null, null);
        int idx = selectedItemIndex >= 0 ? selectedItemIndex + 1 : currentScreen.items.size();
        currentScreen.items.add(idx, item);
        selectedItemIndex = idx;
        onItemStructureChanged();
        showItemProps();
    }

    private void removeItem() {
        if (currentScreen == null || selectedItemIndex < 0) return;
        currentScreen.items.remove(selectedItemIndex);
        selectedItemIndex = Math.min(selectedItemIndex, currentScreen.items.size() - 1);
        onItemStructureChanged();
        showItemProps();
    }

    private void moveItem(int direction) {
        if (currentScreen == null || selectedItemIndex < 0) return;
        int newIdx = selectedItemIndex + direction;
        if (newIdx < 0 || newIdx >= currentScreen.items.size()) return;
        var items = currentScreen.items;
        var temp = items.get(selectedItemIndex);
        items.set(selectedItemIndex, items.get(newIdx));
        items.set(newIdx, temp);
        selectedItemIndex = newIdx;
        onItemStructureChanged();
    }

    private void duplicateItem() {
        if (currentScreen == null || selectedItemIndex < 0) return;
        var copy = currentScreen.items.get(selectedItemIndex).copy();
        currentScreen.items.add(selectedItemIndex + 1, copy);
        selectedItemIndex = selectedItemIndex + 1;
        onItemStructureChanged();
        showItemProps();
    }

    private void onItemChanged() {
        markDirty();
        renderSelected();
    }

    private void onItemStructureChanged() {
        markDirty();
        int sel = selectedItemIndex;
        refreshItemList();
        if (sel >= 0 && sel < itemList.getItems().size()) {
            itemList.getSelectionModel().select(sel);
        }
        renderSelected();
    }

    // --- Field builders ---

    private HBox readonlyField(String name, String value) {
        Label lbl = new Label(name);
        lbl.setMinWidth(70);
        Label val = new Label(value.isEmpty() ? "-" : value);
        val.setStyle("-fx-text-fill: #666;");
        return new HBox(4, lbl, val);
    }

    private HBox labeledField(String name, String value, java.util.function.Consumer<String> setter) {
        Label lbl = new Label(name);
        lbl.setMinWidth(70);
        TextField tf = new TextField(value);
        tf.setPrefWidth(180);
        tf.textProperty().addListener((obs, o, n) -> setter.accept(n));
        return new HBox(4, lbl, tf);
    }

    private HBox labeledIntField(String name, int value, java.util.function.IntConsumer setter) {
        Label lbl = new Label(name);
        lbl.setMinWidth(70);
        TextField tf = new TextField(String.valueOf(value));
        tf.setPrefWidth(80);
        tf.textProperty().addListener((obs, o, n) -> {
            try { setter.accept(Integer.parseInt(n)); }
            catch (NumberFormatException ignored) {}
        });
        return new HBox(4, lbl, tf);
    }

    private VBox resolvedIntField(String name, int value, String resolved,
                                  java.util.function.IntConsumer setter) {
        Label lbl = new Label(name);
        lbl.setMinWidth(70);
        TextField tf = new TextField(String.valueOf(value));
        tf.setPrefWidth(60);
        Label hint = new Label(resolved.isEmpty() ? "" : "\u2192 " + resolved);
        hint.setStyle("-fx-text-fill: #888; -fx-font-size: 10; -fx-padding: 0 0 0 74;");
        hint.setWrapText(true);
        tf.textProperty().addListener((obs, o, n) -> {
            try {
                int v = Integer.parseInt(n);
                setter.accept(v);
                String r = config.resolveAny(v);
                hint.setText(r.isEmpty() ? "" : "\u2192 " + r);
            } catch (NumberFormatException ignored) {}
        });
        HBox row = new HBox(4, lbl, tf);
        if (resolved.isEmpty()) return new VBox(row);
        return new VBox(row, hint);
    }

    /**
     * Int field with a searchable dropdown of named options.
     * User can type a number directly or pick from the list.
     */
    private VBox pickerField(String name, int value, Map<Integer, String> options,
                             java.util.function.IntConsumer setter) {
        Label lbl = new Label(name);
        lbl.setMinWidth(70);

        // Build display strings and reverse map
        var reverseMap = new java.util.HashMap<String, Integer>();
        var displayItems = new java.util.ArrayList<String>();
        for (var e : options.entrySet()) {
            String display = e.getValue() + "  (" + e.getKey() + ")";
            displayItems.add(display);
            reverseMap.put(display, e.getKey());
        }
        displayItems.sort(String.CASE_INSENSITIVE_ORDER);

        var allItems = FXCollections.observableArrayList(displayItems);
        ComboBox<String> combo = new ComboBox<>(allItems);
        combo.setEditable(true);
        combo.setPrefWidth(200);
        combo.setVisibleRowCount(12);

        // Show current value
        String currentDisplay = null;
        String optName = options.get(value);
        if (optName != null) {
            currentDisplay = optName + "  (" + value + ")";
        }
        combo.getEditor().setText(currentDisplay != null ? currentDisplay : String.valueOf(value));

        // Filter on dropdown open
        combo.setOnShown(e -> {
            String text = combo.getEditor().getText().toLowerCase().trim();
            if (text.isEmpty() || text.matches("\\d+")) {
                combo.setItems(allItems);
            } else {
                combo.setItems(FXCollections.observableArrayList(
                    displayItems.stream()
                        .filter(s -> s.toLowerCase().contains(text))
                        .toList()));
            }
        });

        // On selection or manual entry
        combo.setOnAction(e -> {
            String sel = combo.getValue();
            if (sel != null && reverseMap.containsKey(sel)) {
                setter.accept(reverseMap.get(sel));
            } else {
                String text = combo.getEditor().getText().trim();
                // Try parse as raw number
                try {
                    setter.accept(Integer.parseInt(text));
                } catch (NumberFormatException ignored) {}
            }
        });

        return new VBox(new HBox(4, lbl, combo));
    }

    private <T extends Enum<T>> HBox labeledCombo(String name, T[] values, T current,
                                                   java.util.function.Consumer<T> setter) {
        Label lbl = new Label(name);
        lbl.setMinWidth(70);
        ComboBox<T> combo = new ComboBox<>(FXCollections.observableArrayList(values));
        combo.getSelectionModel().select(current);
        combo.setOnAction(e -> setter.accept(combo.getValue()));
        return new HBox(4, lbl, combo);
    }

    private HBox labeledCheck(String name, boolean value, java.util.function.Consumer<Boolean> setter) {
        CheckBox cb = new CheckBox(name);
        cb.setSelected(value);
        cb.setOnAction(e -> setter.accept(cb.isSelected()));
        return new HBox(4, cb);
    }

    // --- Handler code viewer ---

    private ListView<String> codeListView;
    private Label codeFileLabel;
    // Highlighted line ranges for current screen
    private Set<Integer> highlightedLines = new HashSet<>();
    // Cached file content: handler name → lines
    private final Map<String, List<String>> handlerFileCache = new HashMap<>();
    // Parsed case block ranges: handler → (screenIdName → list of [startLine, endLine])
    private final Map<String, Map<String, List<int[]>>> handlerCaseRanges = new HashMap<>();

    private VBox createCodeViewer() {
        codeFileLabel = new Label("Select a screen to view handler code");
        codeFileLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666;");

        codeListView = new ListView<>();
        codeListView.setStyle("-fx-font-family: monospace; -fx-font-size: 12;");
        codeListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                int lineIdx = getIndex();
                if (highlightedLines.contains(lineIdx)) {
                    setStyle("-fx-background-color: #264f78; -fx-text-fill: #e0e0e0; "
                        + "-fx-font-family: monospace; -fx-font-size: 12;");
                } else {
                    setStyle("-fx-text-fill: #999; -fx-font-family: monospace; -fx-font-size: 12;");
                }
            }
        });

        VBox panel = new VBox(4, codeFileLabel, codeListView);
        panel.setPadding(new Insets(4));
        VBox.setVgrow(codeListView, Priority.ALWAYS);
        return panel;
    }

    private void showHandlerCode() {
        if (currentScreen == null || currentScreen.handler.isEmpty()) {
            codeListView.getItems().clear();
            codeFileLabel.setText("No handler for this screen");
            highlightedLines.clear();
            return;
        }

        String handler = currentScreen.handler;
        String screenName = currentScreen.name;

        // Load file if not cached
        if (!handlerFileCache.containsKey(handler)) {
            loadHandlerFile(handler);
        }

        List<String> lines = handlerFileCache.get(handler);
        if (lines == null) {
            codeFileLabel.setText("Handler file not found: " + handler);
            return;
        }

        // Build highlighted line set
        highlightedLines.clear();
        var ranges = handlerCaseRanges.getOrDefault(handler, Map.of())
            .getOrDefault(screenName, List.of());
        for (int[] range : ranges) {
            for (int i = range[0]; i <= range[1]; i++) {
                highlightedLines.add(i);
            }
        }

        // Format lines with line numbers
        var numbered = new ArrayList<String>();
        int width = String.valueOf(lines.size()).length();
        String fmt = "%" + width + "d  %s";
        for (int i = 0; i < lines.size(); i++) {
            numbered.add(String.format(fmt, i + 1, lines.get(i)));
        }

        codeListView.getItems().setAll(numbered);
        codeFileLabel.setText(handler + ".java  |  " + ranges.size() + " blocks for " + screenName);

        // Scroll to first highlighted line
        if (!ranges.isEmpty()) {
            int firstLine = ranges.get(0)[0];
            int scrollTo = Math.max(0, firstLine - 3);
            Platform.runLater(() -> codeListView.scrollTo(scrollTo));
        }
    }

    private void loadHandlerFile(String handler) {
        Path sourcesDir = config.getSourcesDir();
        Path file = sourcesDir.resolve("com/trykote/mobileagent/ui/handler/" + handler + ".java");
        try {
            List<String> lines = Files.readAllLines(file);
            handlerFileCache.put(handler, lines);
            parseCaseRanges(handler, file);
        } catch (Exception e) {
            handlerFileCache.put(handler, null);
        }
    }

    private void parseCaseRanges(String handler, Path file) {
        var ranges = new HashMap<String, List<int[]>>();
        try {
            var cu = StaticJavaParser.parse(file);
            cu.findAll(SwitchEntry.class).forEach(entry -> {
                for (var label : entry.getLabels()) {
                    String labelStr = label.toString();
                    if (!labelStr.startsWith("ScreenId.")) continue;
                    String screenName = labelStr.substring("ScreenId.".length());

                    // Line numbers from AST (1-based → 0-based)
                    int startLine = entry.getBegin().map(p -> p.line - 1).orElse(0);
                    int endLine = entry.getEnd().map(p -> p.line - 1).orElse(startLine);

                    ranges.computeIfAbsent(screenName, k -> new ArrayList<>())
                        .add(new int[]{startLine, endLine});
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to parse " + file + ": " + e.getMessage());
        }
        handlerCaseRanges.put(handler, ranges);
    }

    /** Switch to Screens tab and select the screen by name. */
    private void navigateToScreen(String screenName) {
        mainTabs.getSelectionModel().select(screensTab);
        // Find and select the screen in the tree
        var root = screenTree.getRoot();
        if (root == null) return;
        for (var group : root.getChildren()) {
            for (var item : group.getChildren()) {
                if (item.getValue() instanceof ScreenDef sd && sd.name.equals(screenName)) {
                    screenTree.getSelectionModel().select(item);
                    screenTree.scrollTo(screenTree.getRow(item));
                    return;
                }
            }
        }
    }

    // --- Flow graph ---

    private ScreenFlowGraph flowGraph;
    private Canvas flowCanvas;
    private double flowOffsetX = 20, flowOffsetY = 40;
    private double flowZoom = 1.0;
    private String flowSelected = null;
    private String flowSearch = "";
    private Set<ScreenCategory> flowVisibleCategories =
        new HashSet<>(Set.of(ScreenCategory.FULLSCREEN, ScreenCategory.GRID));
    private double dragStartX, dragStartY, dragOffsetX, dragOffsetY;

    private BorderPane createFlowGraph() {
        // Build graph
        flowGraph = new ScreenFlowGraph();
        // sources/ is sibling to resources-src/ (which config was loaded from)
        Path sourcesDir = config.getSourcesDir();
        try {
            flowGraph.build(sourcesDir, config.getScreens());
        } catch (Exception e) {
            System.err.println("Flow graph build failed: " + e.getMessage());
        }

        flowCanvas = new Canvas(800, 600);

        // Click / double-click on node
        flowCanvas.setOnMousePressed(e -> {
            dragStartX = e.getX();
            dragStartY = e.getY();
            dragOffsetX = flowOffsetX;
            dragOffsetY = flowOffsetY;

            String clicked = hitTestNode(e.getX(), e.getY());
            if (clicked != null) {
                if (e.getClickCount() == 2) {
                    navigateToScreen(clicked);
                } else {
                    flowSelected = clicked.equals(flowSelected) ? null : clicked;
                    renderFlowGraph();
                }
            }
        });
        flowCanvas.setOnMouseDragged(e -> {
            flowOffsetX = dragOffsetX + (e.getX() - dragStartX);
            flowOffsetY = dragOffsetY + (e.getY() - dragStartY);
            renderFlowGraph();
        });
        StackPane canvasPane = new StackPane(flowCanvas);
        canvasPane.setStyle("-fx-background-color: #1e1e1e;");

        // Event filter on parent pane — intercepts scroll before SplitPane/TabPane can react
        canvasPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.getDeltaY() == 0) return;
            double oldZoom = flowZoom;
            flowZoom *= (e.getDeltaY() > 0) ? 1.15 : 0.87;
            flowZoom = Math.max(0.2, Math.min(3.0, flowZoom));
            double factor = flowZoom / oldZoom;
            double mx = e.getX(), my = e.getY();
            flowOffsetX = mx - factor * (mx - flowOffsetX);
            flowOffsetY = my - factor * (my - flowOffsetY);
            renderFlowGraph();
            e.consume();
        });

        // Resize canvas with parent
        canvasPane.widthProperty().addListener((obs, o, n) -> {
            flowCanvas.setWidth(n.doubleValue());
            renderFlowGraph();
        });
        canvasPane.heightProperty().addListener((obs, o, n) -> {
            flowCanvas.setHeight(n.doubleValue());
            renderFlowGraph();
        });

        // Toolbar
        TextField search = new TextField();
        search.setPromptText("Highlight node...");
        search.setMaxWidth(200);
        search.textProperty().addListener((obs, o, n) -> {
            flowSearch = n.toLowerCase().trim();
            renderFlowGraph();
        });

        Label flowInfoLabel = new Label();

        Button resetBtn = new Button("Reset view");
        resetBtn.setOnAction(e -> {
            flowOffsetX = 20; flowOffsetY = 40;
            flowZoom = 1.0; flowSelected = null;
            renderFlowGraph();
        });

        // Type filter checkboxes
        HBox filters = new HBox(6);
        for (ScreenCategory cat : ScreenCategory.values()) {
            CheckBox cb = new CheckBox(cat.label);
            cb.setSelected(flowVisibleCategories.contains(cat));
            cb.setStyle("-fx-text-fill: " + cat.borderColor + "; -fx-font-size: 11;");
            cb.setOnAction(e -> {
                if (cb.isSelected()) flowVisibleCategories.add(cat);
                else flowVisibleCategories.remove(cat);
                updateFlowInfo(flowInfoLabel);
                renderFlowGraph();
            });
            filters.getChildren().add(cb);
        }
        filters.setAlignment(Pos.CENTER_LEFT);

        this.flowInfoLabel = flowInfoLabel;
        updateFlowInfo(flowInfoLabel);

        HBox toolbar = new HBox(8, search, resetBtn,
            new Separator(Orientation.VERTICAL), filters,
            new Separator(Orientation.VERTICAL), flowInfoLabel);
        toolbar.setPadding(new Insets(6, 8, 6, 8));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Details panel (right side)
        Label detailLabel = new Label("Click a node to see connections");
        detailLabel.setWrapText(true);
        VBox detailPane = new VBox(6, detailLabel);
        detailPane.setPadding(new Insets(8));
        detailPane.setPrefWidth(250);
        detailPane.setStyle("-fx-background-color: #f8f8f8;");
        this.flowDetailPane = detailPane;

        SplitPane split = new SplitPane(canvasPane, detailPane);
        split.setDividerPositions(0.78);

        BorderPane panel = new BorderPane();
        panel.setTop(toolbar);
        panel.setCenter(split);

        // Initial render after layout
        Platform.runLater(this::renderFlowGraph);

        return panel;
    }

    private VBox flowDetailPane;
    private Label flowInfoLabel;

    private void updateFlowInfo(Label label) {
        long visible = flowGraph.getNodes().values().stream()
            .filter(n -> flowVisibleCategories.contains(n.category()))
            .count();
        long visibleEdges = flowGraph.getEdges().stream()
            .filter(this::isEdgeVisible).count();
        label.setText(visible + "/" + flowGraph.getNodes().size() + " nodes, " + visibleEdges + " edges");
    }

    private boolean isNodeVisible(ScreenFlowGraph.Node node) {
        return flowVisibleCategories.contains(node.category());
    }

    private boolean isEdgeVisible(ScreenFlowGraph.Edge edge) {
        var src = flowGraph.getNodes().get(edge.source());
        var tgt = flowGraph.getNodes().get(edge.target());
        return src != null && tgt != null && isNodeVisible(src) && isNodeVisible(tgt);
    }

    private static final double NODE_W = 160;
    private static final double NODE_H = 26;

    private void renderFlowGraph() {
        var gc = flowCanvas.getGraphicsContext2D();
        double w = flowCanvas.getWidth();
        double h = flowCanvas.getHeight();
        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, w, h);

        if (flowGraph == null) return;

        gc.save();
        gc.translate(flowOffsetX, flowOffsetY);
        gc.scale(flowZoom, flowZoom);

        // Collect connected nodes for highlighting
        var connected = new HashSet<String>();
        if (flowSelected != null) {
            connected.add(flowSelected);
            for (var e : flowGraph.getEdgesFrom(flowSelected)) connected.add(e.target());
            for (var e : flowGraph.getEdgesTo(flowSelected)) connected.add(e.source());
        }

        // Draw handler group headers
        gc.setFont(Font.font("System", FontWeight.BOLD, 11));
        gc.setFill(Color.web("#888"));
        for (var entry : flowGraph.getHandlerHeaders().entrySet()) {
            double[] pos = entry.getValue();
            gc.fillText(entry.getKey(), pos[0], pos[1]);
        }

        // Draw edges (only between visible nodes)
        for (var edge : flowGraph.getEdges()) {
            var src = flowGraph.getNodes().get(edge.source());
            var tgt = flowGraph.getNodes().get(edge.target());
            if (src == null || tgt == null) continue;
            if (!isNodeVisible(src) || !isNodeVisible(tgt)) continue;

            boolean highlight = flowSelected != null
                && (edge.source().equals(flowSelected) || edge.target().equals(flowSelected));

            gc.setStroke(highlight
                ? Color.web("#4fc3f7")
                : Color.web("#444"));
            gc.setLineWidth(highlight ? 2 : 1);

            double x1 = src.x() + NODE_W;
            double y1 = src.y() + NODE_H / 2;
            double x2 = tgt.x();
            double y2 = tgt.y() + NODE_H / 2;

            // If same column, route the edge around
            if (Math.abs(src.x() - tgt.x()) < 10) {
                double cx = src.x() + NODE_W + 30;
                gc.beginPath();
                gc.moveTo(x1, y1);
                gc.bezierCurveTo(cx, y1, cx, y2, x2 + NODE_W, y2);
                gc.stroke();
                drawArrowHead(gc, cx, y2, x2 + NODE_W, y2);
            } else {
                // Bezier curve
                double cx1 = x1 + 40;
                double cx2 = x2 - 40;
                gc.beginPath();
                gc.moveTo(x1, y1);
                gc.bezierCurveTo(cx1, y1, cx2, y2, x2, y2);
                gc.stroke();
                drawArrowHead(gc, cx2, y2, x2, y2);
            }
        }

        // Draw nodes (only visible categories)
        gc.setFont(Font.font("System", 10));
        for (var node : flowGraph.getNodes().values()) {
            if (!isNodeVisible(node)) continue;

            ScreenCategory cat = node.category();
            boolean selected = node.name().equals(flowSelected);
            boolean isConnected = connected.contains(node.name());
            boolean matchesSearch = !flowSearch.isEmpty()
                && node.name().toLowerCase().contains(flowSearch);

            Color bg;
            Color border;
            Color text;

            if (selected) {
                bg = Color.web("#1565c0");
                border = Color.web("#4fc3f7");
                text = Color.WHITE;
            } else if (matchesSearch) {
                bg = Color.web("#f57f17");
                border = Color.web("#ffb300");
                text = Color.WHITE;
            } else if (isConnected) {
                bg = Color.web("#263238");
                border = Color.web("#4fc3f7");
                text = Color.web("#b0bec5");
            } else {
                bg = Color.web(cat.fillColor);
                border = Color.web(cat.borderColor);
                text = Color.web("#ccc");
            }

            gc.setFill(bg);
            gc.fillRoundRect(node.x(), node.y(), NODE_W, NODE_H, 6, 6);
            gc.setStroke(border);
            gc.setLineWidth(selected ? 2 : 1);
            gc.strokeRoundRect(node.x(), node.y(), NODE_W, NODE_H, 6, 6);

            gc.setFill(text);
            String label = node.name().length() > 22
                ? node.name().substring(0, 20) + "\u2026" : node.name();
            gc.fillText(label, node.x() + 6, node.y() + 17);
        }

        gc.restore();

        // Update detail panel
        updateFlowDetails();
    }

    private void drawArrowHead(GraphicsContext gc,
                               double fromX, double fromY, double toX, double toY) {
        double angle = Math.atan2(toY - fromY, toX - fromX);
        double len = 8;
        gc.setFill(gc.getStroke());
        gc.fillPolygon(
            new double[]{toX, toX - len * Math.cos(angle - 0.4), toX - len * Math.cos(angle + 0.4)},
            new double[]{toY, toY - len * Math.sin(angle - 0.4), toY - len * Math.sin(angle + 0.4)},
            3
        );
    }

    private String hitTestNode(double mouseX, double mouseY) {
        // Convert mouse coords to graph coords
        double gx = (mouseX - flowOffsetX) / flowZoom;
        double gy = (mouseY - flowOffsetY) / flowZoom;

        for (var node : flowGraph.getNodes().values()) {
            if (!isNodeVisible(node)) continue;
            if (gx >= node.x() && gx <= node.x() + NODE_W
                && gy >= node.y() && gy <= node.y() + NODE_H) {
                return node.name();
            }
        }
        return null;
    }

    private void updateFlowDetails() {
        flowDetailPane.getChildren().clear();
        if (flowSelected == null || flowGraph == null) {
            flowDetailPane.getChildren().add(new Label("Click a node to see connections"));
            return;
        }

        var node = flowGraph.getNodes().get(flowSelected);
        if (node == null) return;

        ScreenCategory cat = node.category();

        Label title = new Label(node.name());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        Button editBtn = new Button("Edit");
        editBtn.setStyle("-fx-font-size: 11;");
        editBtn.setOnAction(e -> navigateToScreen(node.name()));
        HBox titleRow = new HBox(8, title, editBtn);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label typeLabel = new Label("Type: " + node.screenType() + " (" + cat.label + ")");
        typeLabel.setStyle("-fx-text-fill: " + cat.borderColor + "; -fx-font-weight: bold;");
        Label handler = new Label("Handler: " + (node.handler().isEmpty() ? "-" : node.handler()));
        handler.setStyle("-fx-text-fill: #666;");
        Label sid = new Label("ScreenId: " + node.screenId());
        sid.setStyle("-fx-text-fill: #666;");

        flowDetailPane.getChildren().addAll(titleRow, typeLabel, handler, sid, new Separator());

        var outgoing = flowGraph.getEdgesFrom(flowSelected);
        if (!outgoing.isEmpty()) {
            Label outLabel = new Label("Navigates to (" + outgoing.size() + "):");
            outLabel.setStyle("-fx-font-weight: bold;");
            flowDetailPane.getChildren().add(outLabel);
            for (var edge : outgoing) {
                Hyperlink link = new Hyperlink("\u2192 " + edge.target());
                link.setOnAction(e -> { flowSelected = edge.target(); renderFlowGraph(); });
                flowDetailPane.getChildren().add(link);
            }
        }

        var incoming = flowGraph.getEdgesTo(flowSelected);
        if (!incoming.isEmpty()) {
            flowDetailPane.getChildren().add(new Separator());
            Label inLabel = new Label("Reached from (" + incoming.size() + "):");
            inLabel.setStyle("-fx-font-weight: bold;");
            flowDetailPane.getChildren().add(inLabel);
            for (var edge : incoming) {
                Hyperlink link = new Hyperlink("\u2190 " + edge.source());
                link.setOnAction(e -> { flowSelected = edge.source(); renderFlowGraph(); });
                flowDetailPane.getChildren().add(link);
            }
        }

        if (outgoing.isEmpty() && incoming.isEmpty()) {
            flowDetailPane.getChildren().add(new Label("No known connections"));
        }
    }

    // --- Object Pool browser ---

    public static class PoolRow {
        private final SimpleIntegerProperty index;
        private final SimpleStringProperty type;
        private final SimpleStringProperty value;
        private final SimpleStringProperty key;
        private final SimpleStringProperty className;
        private final SimpleStringProperty zone;
        private final SimpleStringProperty usedBy;

        public PoolRow(ConfigLoader.PoolEntry e, String usedBy) {
            this.index = new SimpleIntegerProperty(e.index());
            this.type = new SimpleStringProperty(e.type());
            this.value = new SimpleStringProperty(e.value());
            this.key = new SimpleStringProperty(e.key());
            this.className = new SimpleStringProperty(e.className());
            this.zone = new SimpleStringProperty(e.zone());
            this.usedBy = new SimpleStringProperty(usedBy);
        }

        public int getIndex() { return index.get(); }
        public String getType() { return type.get(); }
        public String getValue() { return value.get(); }
        public String getKey() { return key.get(); }
        public String getClassName() { return className.get(); }
        public String getZone() { return zone.get(); }
        public String getUsedBy() { return usedBy.get(); }

        public SimpleIntegerProperty indexProperty() { return index; }
        public SimpleStringProperty typeProperty() { return type; }
        public SimpleStringProperty valueProperty() { return value; }
        public SimpleStringProperty keyProperty() { return key; }
        public SimpleStringProperty classNameProperty() { return className; }
        public SimpleStringProperty zoneProperty() { return zone; }
        public SimpleStringProperty usedByProperty() { return usedBy; }
    }

    @SuppressWarnings("unchecked")
    private BorderPane createPoolBrowser() {
        var rows = FXCollections.observableArrayList(
            config.getPoolEntries().stream()
                .map(e -> new PoolRow(e, config.getPoolScreenRefs(e.index())))
                .toList()
        );
        var filtered = new FilteredList<>(rows, p -> true);

        TableView<PoolRow> table = new TableView<>(filtered);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        var colIndex = new TableColumn<PoolRow, Number>("#");
        colIndex.setCellValueFactory(cd -> cd.getValue().indexProperty());
        colIndex.setPrefWidth(50);
        colIndex.setMaxWidth(60);

        var colType = new TableColumn<PoolRow, String>("Type");
        colType.setCellValueFactory(cd -> cd.getValue().typeProperty());
        colType.setPrefWidth(70);
        colType.setMaxWidth(100);

        var colValue = new TableColumn<PoolRow, String>("Value");
        colValue.setCellValueFactory(cd -> cd.getValue().valueProperty());
        colValue.setPrefWidth(250);

        var colKey = new TableColumn<PoolRow, String>("Key");
        colKey.setCellValueFactory(cd -> cd.getValue().keyProperty());
        colKey.setPrefWidth(180);

        var colClass = new TableColumn<PoolRow, String>("Class");
        colClass.setCellValueFactory(cd -> cd.getValue().classNameProperty());
        colClass.setPrefWidth(120);

        var colZone = new TableColumn<PoolRow, String>("Zone");
        colZone.setCellValueFactory(cd -> cd.getValue().zoneProperty());
        colZone.setPrefWidth(70);
        colZone.setMaxWidth(90);

        var colUsedBy = new TableColumn<PoolRow, String>("Used by");
        colUsedBy.setCellValueFactory(cd -> cd.getValue().usedByProperty());
        colUsedBy.setPrefWidth(250);

        table.getColumns().addAll(colIndex, colType, colValue, colKey, colClass, colZone, colUsedBy);

        // Search bar
        TextField search = new TextField();
        search.setPromptText("Search by index, key, value, class...");
        search.textProperty().addListener((obs, oldVal, newVal) -> {
            String f = newVal.toLowerCase().trim();
            filtered.setPredicate(row -> {
                if (f.isEmpty()) return true;
                // Try exact index match
                if (f.matches("\\d+") && row.getIndex() == Integer.parseInt(f)) return true;
                return row.getKey().toLowerCase().contains(f)
                    || row.getValue().toLowerCase().contains(f)
                    || row.getClassName().toLowerCase().contains(f)
                    || row.getType().toLowerCase().contains(f)
                    || row.getUsedBy().toLowerCase().contains(f)
                    || String.valueOf(row.getIndex()).contains(f);
            });
        });

        // Filter buttons
        ToggleGroup typeFilter = new ToggleGroup();
        RadioButton allBtn = new RadioButton("All");
        RadioButton strBtn = new RadioButton("Strings");
        RadioButton intBtn = new RadioButton("Ints");
        RadioButton namedBtn = new RadioButton("Named");
        RadioButton usedBtn = new RadioButton("Used");
        allBtn.setToggleGroup(typeFilter);
        strBtn.setToggleGroup(typeFilter);
        intBtn.setToggleGroup(typeFilter);
        namedBtn.setToggleGroup(typeFilter);
        usedBtn.setToggleGroup(typeFilter);
        allBtn.setSelected(true);

        typeFilter.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            String text = search.getText();
            search.setText(text + " "); // trigger re-filter
            search.setText(text);
        });

        // Combine search text + radio filter
        search.textProperty().addListener((obs, oldVal, newVal) -> {
            String f = newVal.toLowerCase().trim();
            Toggle selected = typeFilter.getSelectedToggle();
            filtered.setPredicate(row -> {
                // Radio filter
                if (selected == strBtn && !"string".equals(row.getType())) return false;
                if (selected == intBtn && !"int".equals(row.getType())) return false;
                if (selected == namedBtn && row.getKey().isEmpty()) return false;
                if (selected == usedBtn && row.getUsedBy().isEmpty()) return false;
                // Text filter
                if (f.isEmpty()) return true;
                if (f.matches("\\d+") && row.getIndex() == Integer.parseInt(f)) return true;
                return row.getKey().toLowerCase().contains(f)
                    || row.getValue().toLowerCase().contains(f)
                    || row.getClassName().toLowerCase().contains(f)
                    || row.getType().toLowerCase().contains(f)
                    || row.getUsedBy().toLowerCase().contains(f)
                    || String.valueOf(row.getIndex()).contains(f);
            });
        });

        // Re-apply radio change via search text toggle
        typeFilter.selectedToggleProperty().addListener((obs, o, n) -> {
            String t = search.getText();
            search.setText(t.isEmpty() ? " " : "");
            search.setText(t);
        });

        Label countLabel = new Label(rows.size() + " entries");
        filtered.predicateProperty().addListener((obs, o, n) ->
            countLabel.setText(filtered.size() + " / " + rows.size() + " entries"));

        HBox toolbar = new HBox(8, search, new Separator(Orientation.VERTICAL),
            allBtn, strBtn, intBtn, namedBtn, usedBtn,
            new Separator(Orientation.VERTICAL), countLabel);
        toolbar.setPadding(new Insets(6, 8, 6, 8));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(search, Priority.ALWAYS);
        search.setMaxWidth(350);

        BorderPane panel = new BorderPane();
        panel.setTop(toolbar);
        panel.setCenter(table);
        return panel;
    }

    // --- Rendering & save ---

    private void renderSelected() {
        renderer.render(canvas, currentScreen, selectedItemIndex);
    }

    private void markDirty() {
        dirty = true;
        statusLabel.setText("* Modified");
    }

    private void saveConfig() {
        try {
            config.save();
            dirty = false;
            statusLabel.setText("Saved");
        } catch (Exception e) {
            showError("Save failed", e);
        }
    }

    private String resolveLabel(ScreenDef.Item item) {
        if (item.labelHint != null && !item.labelHint.isEmpty()) return item.labelHint;
        String s = config.resolveString(item.label);
        return s.isEmpty() ? "item_" + item.label : s;
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "\u2026" : s;
    }

    private void showError(String msg, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(msg);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
