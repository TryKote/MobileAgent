package com.trykote.editor;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders a ScreenDef onto a JavaFX Canvas, approximating J2ME look.
 */
public class ScreenRenderer {

    // J2ME screen dimensions
    public static final int SCREEN_W = 240;
    public static final int SCREEN_H = 320;

    // Layout constants (matching ListView.java)
    private static final int OUTER_PADDING = 4;
    private static final int INNER_MARGIN = 2;
    private static final int BORDER_INSET = 3;
    private static final int ICON_SIZE = 16;
    private static final int ITEM_PADDING = 4;
    private static final int SCROLLBAR_WIDTH = 7;
    private static final int SOFT_KEY_HEIGHT = 18;

    // Palette color indices
    private static final int PAL_TEXT = 0;
    private static final int PAL_BACKGROUND = 1;
    private static final int PAL_BACKGROUND_ALT = 2;
    private static final int PAL_SELECTION_BG = 8;
    private static final int PAL_SEPARATOR = 10;
    private static final int PAL_DISABLED = 11;
    private static final int PAL_BORDER = 12;
    private static final int PAL_POPUP_BG = 14;
    private static final int PAL_POPUP_TEXT = 15;
    private static final int PAL_GRADIENT_START = 16;
    private static final int PAL_GRADIENT_END = 21;

    private final ConfigLoader config;
    private final Map<Path, Image> spriteCache = new HashMap<>();
    private int theme = 0;

    // Fonts approximating J2ME medium size
    private final Font fontPlain = Font.font("Monospaced", FontWeight.NORMAL, 12);
    private final Font fontBold = Font.font("Monospaced", FontWeight.BOLD, 12);
    private final Font fontSmall = Font.font("Monospaced", FontWeight.NORMAL, 10);
    private final int fontHeight = 14;
    private final int fontSmallHeight = 12;

    public ScreenRenderer(ConfigLoader config) {
        this.config = config;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public int getTheme() {
        return theme;
    }

    private int selectedItem = -1;

    /** Estimate total content height for a screen. */
    private int estimateContentHeight(ScreenDef screen) {
        int headerHeight = fontHeight + ITEM_PADDING * 2;
        int total = headerHeight + INNER_MARGIN + SOFT_KEY_HEIGHT;
        for (var item : screen.items) {
            total += switch (item.type) {
                case SEPARATOR -> (fontHeight + ITEM_PADDING * 2) / 2 + INNER_MARGIN;
                case LABEL_SEPARATOR -> fontSmallHeight + ITEM_PADDING + INNER_MARGIN;
                case TEXT_SEPARATOR -> fontSmallHeight + ITEM_PADDING;
                case TEXT_INPUT -> fontHeight + ITEM_PADDING * 2 + INNER_MARGIN;
                case IMAGE -> 48 + ITEM_PADDING;
                default -> fontHeight + ITEM_PADDING * 2;
            };
        }
        return total;
    }

    public void render(Canvas canvas, ScreenDef screen, int selectedItemIndex) {
        this.selectedItem = selectedItemIndex;

        if (screen == null) {
            canvas.setHeight(SCREEN_H);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.GRAY);
            gc.fillRect(0, 0, SCREEN_W, SCREEN_H);
            gc.setFill(Color.WHITE);
            gc.setFont(fontPlain);
            gc.fillText("No screen selected", 60, 160);
            return;
        }

        // Calculate needed height
        int contentNeeded = estimateContentHeight(screen);
        int canvasH = Math.max(SCREEN_H, contentNeeded + (screen.type.isDialog() ? 40 : 0));
        canvas.setHeight(canvasH);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvasH);

        int x = 0, y = 0, w = SCREEN_W, h = canvasH;

        if (screen.type.isDialog()) {
            gc.setFill(paletteColor(PAL_BACKGROUND_ALT));
            gc.fillRect(0, 0, SCREEN_W, canvasH);

            int dw = (int) (SCREEN_W * 0.9);
            x = (SCREEN_W - dw) / 2;
            w = dw;
            h = contentNeeded;
            y = 20;
        }

        // Background
        gc.setFill(paletteColor(PAL_BACKGROUND));
        gc.fillRect(x, y, w, h);

        // Border
        if (screen.type.isDialog()) {
            gc.setStroke(paletteColor(PAL_BORDER));
            gc.setLineWidth(1);
            gc.strokeRect(x + 0.5, y + 0.5, w - 1, h - 1);
        }

        // Header with gradient
        int headerHeight = fontHeight + ITEM_PADDING * 2;
        drawHeaderGradient(gc, x, y, w, headerHeight);

        String titleStr = resolveLabel(screen.title);
        if (!titleStr.isEmpty()) {
            gc.setFill(Color.WHITE);
            gc.setFont(fontBold);
            gc.fillText(titleStr, x + OUTER_PADDING + 2, y + fontHeight + INNER_MARGIN);
        }

        // Items — no clipping, draw all
        int contentY = y + headerHeight + INNER_MARGIN;
        int itemX = x + OUTER_PADDING;
        int itemW = w - OUTER_PADDING * 2;

        gc.setFont(fontPlain);
        for (int i = 0; i < screen.items.size(); i++) {
            var item = screen.items.get(i);
            int itemY = contentY;
            contentY = drawItem(gc, item, itemX, contentY, itemW, screen.checkboxes);
            if (i == selectedItem) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                gc.strokeRect(itemX, itemY, itemW, contentY - itemY);
            }
        }

        // Soft keys
        drawSoftKeys(gc, screen, x, y + h - SOFT_KEY_HEIGHT, w);
    }

    private void drawHeaderGradient(GraphicsContext gc, int x, int y, int w, int h) {
        int startColor = paletteRgb(PAL_GRADIENT_START);
        int endColor = paletteRgb(PAL_GRADIENT_END);

        for (int row = 0; row < h; row++) {
            double ratio = (double) row / h;
            int r = interpolate((startColor >> 16) & 0xFF, (endColor >> 16) & 0xFF, ratio);
            int g = interpolate((startColor >> 8) & 0xFF, (endColor >> 8) & 0xFF, ratio);
            int b = interpolate(startColor & 0xFF, endColor & 0xFF, ratio);
            gc.setStroke(Color.rgb(r, g, b));
            gc.strokeLine(x + 1, y + row, x + w - 2, y + row);
        }
    }

    private int interpolate(int from, int to, double ratio) {
        return (int) (from + (to - from) * ratio);
    }

    private int drawItem(GraphicsContext gc, ScreenDef.Item item, int x, int y, int w, boolean checkboxMode) {
        int itemHeight = fontHeight + ITEM_PADDING * 2;
        String label = resolveItemLabel(item);

        switch (item.type) {
            case SEPARATOR -> {
                gc.setStroke(paletteColor(PAL_SEPARATOR));
                gc.setLineWidth(1);
                int lineY = y + itemHeight / 2;
                gc.strokeLine(x, lineY, x + w, lineY);
                return y + itemHeight / 2 + INNER_MARGIN;
            }

            case LABEL_SEPARATOR -> {
                gc.setFill(paletteColor(PAL_SEPARATOR));
                gc.setFont(fontSmall);
                gc.fillText(label, x + INNER_MARGIN, y + fontSmallHeight + INNER_MARGIN);
                gc.setFont(fontPlain);
                gc.setStroke(paletteColor(PAL_SEPARATOR));
                gc.strokeLine(x, y + fontSmallHeight + ITEM_PADDING, x + w, y + fontSmallHeight + ITEM_PADDING);
                return y + fontSmallHeight + ITEM_PADDING + INNER_MARGIN;
            }

            case TEXT_SEPARATOR -> {
                gc.setFill(paletteColor(PAL_DISABLED));
                gc.setFont(fontSmall);
                gc.fillText(label, x + INNER_MARGIN, y + fontSmallHeight + INNER_MARGIN);
                gc.setFont(fontPlain);
                return y + fontSmallHeight + ITEM_PADDING;
            }

            case CONDITIONAL_IF, CONDITIONAL_UNLESS -> {
                String condLabel = item.type == ScreenDef.ItemType.CONDITIONAL_IF ? "[IF] " : "[UNLESS] ";
                drawActionItem(gc, x, y, w, itemHeight, item.icon, condLabel + label);
                gc.setFill(Color.rgb(0, 0, 255, 0.15));
                gc.fillRect(x, y, w, itemHeight);
                return y + itemHeight;
            }

            case CHECKBOX -> {
                gc.setStroke(paletteColor(PAL_TEXT));
                gc.setLineWidth(1);
                gc.strokeRect(x + INNER_MARGIN + 0.5, y + INNER_MARGIN + 1.5, 12, 12);
                gc.setFill(paletteColor(PAL_TEXT));
                gc.fillText(label, x + INNER_MARGIN + 16, y + fontHeight + INNER_MARGIN);
                return y + itemHeight;
            }

            case DROPDOWN -> {
                gc.setFill(paletteColor(PAL_TEXT));
                gc.fillText(label, x + INNER_MARGIN, y + fontHeight + INNER_MARGIN);
                gc.fillText("\u25BC", x + w - 14, y + fontHeight + INNER_MARGIN);
                return y + itemHeight;
            }

            case TEXT_INPUT -> {
                String hintText = resolveLabel(item.hint);
                if (hintText.isEmpty()) hintText = "___";
                gc.setStroke(paletteColor(PAL_BORDER));
                gc.setLineWidth(1);
                gc.strokeRect(x + INNER_MARGIN + 0.5, y + 0.5, w - INNER_MARGIN * 2, itemHeight);
                gc.setFill(paletteColor(PAL_DISABLED));
                gc.fillText(hintText, x + ITEM_PADDING, y + fontHeight + INNER_MARGIN);
                return y + itemHeight + INNER_MARGIN;
            }

            case LOGIN -> {
                drawActionItem(gc, x, y, w, itemHeight, 221, label.isEmpty() ? "Login" : label);
                return y + itemHeight;
            }

            case PASSWORD -> {
                drawActionItem(gc, x, y, w, itemHeight, 219, "********");
                return y + itemHeight;
            }

            case REDIRECT -> {
                gc.setFill(paletteColor(PAL_TEXT));
                gc.fillText("\u2192 " + label, x + INNER_MARGIN, y + fontHeight + INNER_MARGIN);
                return y + itemHeight;
            }

            case IMAGE -> {
                gc.setFill(Color.LIGHTGRAY);
                gc.fillRect(x + INNER_MARGIN, y + INNER_MARGIN, 64, 48);
                gc.setStroke(Color.GRAY);
                gc.strokeRect(x + INNER_MARGIN, y + INNER_MARGIN, 64, 48);
                gc.setFill(Color.GRAY);
                gc.fillText("[IMG]", x + 18, y + 28);
                return y + 48 + ITEM_PADDING;
            }

            default -> { // ACTION
                if (checkboxMode) {
                    gc.setStroke(paletteColor(PAL_TEXT));
                    gc.strokeRect(x + INNER_MARGIN + 0.5, y + INNER_MARGIN + 1.5, 12, 12);
                    drawIcon(gc, x + INNER_MARGIN + 16, y + INNER_MARGIN, item.icon);
                    gc.setFill(paletteColor(PAL_TEXT));
                    gc.fillText(label, x + INNER_MARGIN + 16 + ICON_SIZE + INNER_MARGIN, y + fontHeight + INNER_MARGIN);
                } else {
                    drawActionItem(gc, x, y, w, itemHeight, item.icon, label);
                }
                return y + itemHeight;
            }
        }
    }

    private void drawActionItem(GraphicsContext gc, int x, int y, int w, int h,
                               int icon, String label) {
        int textX = x + INNER_MARGIN;

        if (icon > 0) {
            drawIcon(gc, x + INNER_MARGIN, y + INNER_MARGIN, icon);
            textX = x + INNER_MARGIN + ICON_SIZE + INNER_MARGIN;
        }

        gc.setFill(paletteColor(PAL_TEXT));
        gc.fillText(label, textX, y + fontHeight + INNER_MARGIN);
    }

    private boolean showSprites = true;

    public void setShowSprites(boolean show) {
        this.showSprites = show;
    }

    public boolean getShowSprites() {
        return showSprites;
    }

    private void drawIcon(GraphicsContext gc, double x, double y, int iconCode) {
        if (!showSprites) {
            drawIconFallback(gc, x, y, iconCode);
            return;
        }
        var loc = config.getIconLocation(iconCode);
        if (loc == null) {
            drawIconFallback(gc, x, y, iconCode);
            return;
        }
        Image sheet = spriteCache.computeIfAbsent(loc.filePath(), path -> {
            try {
                if (Files.exists(path)) {
                    return new Image(path.toUri().toString());
                }
            } catch (Exception ignored) {}
            return null;
        });
        if (sheet == null) {
            drawIconFallback(gc, x, y, iconCode);
            return;
        }
        gc.drawImage(sheet, loc.tileX(), loc.tileY(), ICON_SIZE, ICON_SIZE,
                     x, y, ICON_SIZE, ICON_SIZE);
    }

    private void drawIconFallback(GraphicsContext gc, double x, double y, int iconCode) {
        int hue = (iconCode * 37) % 360;
        gc.setFill(Color.hsb(hue, 0.3, 0.85));
        gc.fillRect(x, y, ICON_SIZE, ICON_SIZE);
    }


    private void drawSoftKeys(GraphicsContext gc, ScreenDef screen, int x, int y, int w) {
        gc.setFill(paletteColor(PAL_POPUP_BG));
        gc.fillRect(x, y, w, SOFT_KEY_HEIGHT);
        gc.setStroke(paletteColor(PAL_BORDER));
        gc.strokeLine(x, y, x + w, y);

        gc.setFont(fontSmall);
        gc.setFill(paletteColor(PAL_TEXT));

        // Left soft key
        String leftLabel = resolveSoftKeyLabel(screen.leftKey);
        if (!leftLabel.isEmpty()) {
            gc.fillText(leftLabel, x + INNER_MARGIN + 2, y + fontSmallHeight + INNER_MARGIN);
        }

        // Right soft key
        String rightLabel = resolveSoftKeyLabel(screen.rightKey);
        if (!rightLabel.isEmpty()) {
            javafx.scene.text.Text measure = new javafx.scene.text.Text(rightLabel);
            measure.setFont(gc.getFont());
            double textW = measure.getLayoutBounds().getWidth();
            gc.fillText(rightLabel, x + w - textW - INNER_MARGIN - 2, y + fontSmallHeight + INNER_MARGIN);
        }

        gc.setFont(fontPlain);
    }

    private String resolveLabel(int key) {
        if (key <= 0) return "";
        return config.resolveString(key);
    }

    private String resolveItemLabel(ScreenDef.Item item) {
        // Prefer the human-readable hint from config.json
        if (item.labelHint != null && !item.labelHint.isEmpty()) {
            return item.labelHint;
        }
        String resolved = resolveLabel(item.label);
        if (!resolved.isEmpty()) return resolved;
        return "item_" + item.label;
    }

    private String resolveSoftKeyLabel(ScreenDef.SoftKey key) {
        if (key == null || key.cmd == 0 && key.label == 0) return "";
        if (!key.labelHint.isEmpty()) return key.labelHint;
        return resolveLabel(key.label);
    }

    private Color paletteColor(int index) {
        int rgb = paletteRgb(index);
        return Color.rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    private int paletteRgb(int index) {
        int[][] pal = config.getPalette();
        if (pal == null || theme >= pal.length || index >= pal[theme].length) {
            return 0x808080;
        }
        return pal[theme][index];
    }
}
