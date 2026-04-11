package com.trykote.editor;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.List;

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

        // Main layout
        SplitPane splitPane = new SplitPane(leftPanel, canvasHolder, rightPanel);
        splitPane.setDividerPositions(0.18, 0.58);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(splitPane);

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

    private VBox createRightPanel() {
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

        // Item action buttons
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

        VBox panel = new VBox(4,
            new Label("Items"),
            itemList,
            itemButtons,
            new Separator(),
            scroll
        );
        panel.setPadding(new Insets(8));
        panel.setPrefWidth(320);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return panel;
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
            labeledIntField("Title key", currentScreen.title, v -> { currentScreen.title = v; markDirty(); renderSelected(); }),
            labeledCheck("Checkboxes", currentScreen.checkboxes, v -> { currentScreen.checkboxes = v; markDirty(); renderSelected(); }),
            labeledField("Handler", currentScreen.handler, v -> { currentScreen.handler = v; markDirty(); }),
            new Separator(),
            new Label("Left soft key"),
            labeledIntField("  Label", currentScreen.leftKey.label, v -> { currentScreen.leftKey.label = v; markDirty(); renderSelected(); }),
            labeledField("  Hint", currentScreen.leftKey.labelHint, v -> { currentScreen.leftKey.labelHint = v; markDirty(); renderSelected(); }),
            labeledIntField("  Cmd", currentScreen.leftKey.cmd, v -> { currentScreen.leftKey.cmd = v; markDirty(); }),
            new Label("Right soft key"),
            labeledIntField("  Label", currentScreen.rightKey.label, v -> { currentScreen.rightKey.label = v; markDirty(); renderSelected(); }),
            labeledField("  Hint", currentScreen.rightKey.labelHint, v -> { currentScreen.rightKey.labelHint = v; markDirty(); renderSelected(); }),
            labeledIntField("  Cmd", currentScreen.rightKey.cmd, v -> { currentScreen.rightKey.cmd = v; markDirty(); }),
            labeledIntField("Extra cmd", currentScreen.extraCmd, v -> { currentScreen.extraCmd = v; markDirty(); })
        );
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
            labeledIntField("Label", item.label, v -> { item.label = v; onItemChanged(); }),
            labeledField("Label hint", item.labelHint != null ? item.labelHint : "",
                v -> { item.labelHint = v.isEmpty() ? null : v; onItemChanged(); }),
            labeledIntField("Icon", item.icon, v -> { item.icon = v; onItemChanged(); }),
            labeledIntField("Cmd", item.cmd, v -> { item.cmd = v; onItemChanged(); }),
            readonlyField("Action", config.getActionName(item.cmd)),
            labeledIntField("CondKey", item.condKey, v -> { item.condKey = v; onItemChanged(); }),
            labeledIntField("DataKey", item.dataKey, v -> { item.dataKey = v; onItemChanged(); }),
            labeledIntField("Hint", item.hint, v -> { item.hint = v; onItemChanged(); }),
            labeledField("Style", item.style != null ? item.style : "",
                v -> { item.style = v.isEmpty() ? null : v; onItemChanged(); }),
            new Separator(),
            new Label("Resolved: " + truncate(resolveLabel(item), 40))
        );
    }

    // --- Item actions ---

    private void addItem() {
        if (currentScreen == null) return;
        var item = new ScreenDef.Item(ScreenDef.ItemType.ACTION, 0, "", 0, 0, 0, 0, 0, null);
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
