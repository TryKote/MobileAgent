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
import java.util.Map;

/**
 * Renders a ScreenDef onto a JavaFX Canvas, approximating J2ME look.
 * Uses ScreenLayout to determine type-specific rendering behavior.
 */
public class ScreenRenderer {

    public static final int SCREEN_W = 240;
    public static final int SCREEN_H = 320;

    private static final int OUTER_PADDING = 4;
    private static final int INNER_MARGIN = 2;
    private static final int ICON_SIZE = 16;
    private static final int ITEM_PADDING = 4;
    private static final int SOFT_KEY_HEIGHT = 18;
    private static final int TAB_BAR_HEIGHT = 16;

    // Palette indices
    private static final int PAL_TEXT = 0;
    private static final int PAL_BACKGROUND = 1;
    private static final int PAL_BACKGROUND_ALT = 2;
    private static final int PAL_SEPARATOR = 10;
    private static final int PAL_DISABLED = 11;
    private static final int PAL_BORDER = 12;
    private static final int PAL_POPUP_BG = 14;
    private static final int PAL_GRADIENT_START = 16;
    private static final int PAL_GRADIENT_END = 21;

    private final ConfigLoader config;
    private final Map<Path, Image> spriteCache = new HashMap<>();
    private int theme = 0;

    private final Font fontPlain = Font.font("SansSerif", FontWeight.NORMAL, 10);
    private final Font fontBold = Font.font("SansSerif", FontWeight.BOLD, 10);
    private final Font fontSmall = Font.font("SansSerif", FontWeight.NORMAL, 8);
    private final int fontHeight = 12;
    private final int fontSmallHeight = 10;

    private int selectedItem = -1;
    private boolean showSprites = true;

    public ScreenRenderer(ConfigLoader config) {
        this.config = config;
    }

    public void setTheme(int theme) { this.theme = theme; }
    public int getTheme() { return theme; }
    public void setShowSprites(boolean show) { this.showSprites = show; }
    public boolean getShowSprites() { return showSprites; }

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

        ScreenLayout layout = ScreenLayout.of(screen.type);

        // Dynamic canvas height: expand for long item lists
        int estimated = estimateContentHeight(screen, layout);
        int canvasH = layout.isOverlay()
            ? SCREEN_H
            : Math.max(SCREEN_H, estimated);
        canvas.setHeight(canvasH);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvasH);

        // Background layer (for overlays, show dimmed base)
        if (layout.isOverlay()) {
            gc.setFill(Color.rgb(40, 40, 40));
            gc.fillRect(0, 0, SCREEN_W, SCREEN_H);
            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillRect(0, 0, SCREEN_W, SCREEN_H);
        }

        // Grid layout: map screens get map background, others get grid items
        if (screen.type == ScreenDef.ScreenType.MAP) {
            boolean isActualMap = screen.name.startsWith("MAP_VIEW");
            if (isActualMap) {
                renderMapScreen(gc, screen, layout);
            } else {
                renderGridScreen(gc, screen, layout);
            }
            return;
        }

        // Content area dimensions
        int contentW = (int) (SCREEN_W * layout.widthRatio);
        int contentH = layout.isFullscreen() ? canvasH : estimated;

        int x = layout.offsetX(SCREEN_W, contentW, contentH);
        int y = layout.offsetY(SCREEN_H, contentW, contentH);

        // Clamp overlay to screen bounds
        if (layout.isOverlay()) {
            contentH = Math.min(contentH, SCREEN_H - 10);
            y = Math.max(4, Math.min(y, SCREEN_H - contentH - 4));
        }

        // Background fill
        gc.setFill(paletteColor(PAL_BACKGROUND));
        gc.fillRect(x, y, contentW, contentH);

        // Border for overlays
        if (layout.isOverlay()) {
            gc.setStroke(paletteColor(PAL_BORDER));
            gc.setLineWidth(1);
            gc.strokeRect(x + 0.5, y + 0.5, contentW - 1, contentH - 1);
        }

        int drawY = y;

        // Tab bar
        if (layout.hasTabBar) {
            drawTabBar(gc, x, drawY, contentW);
            drawY += TAB_BAR_HEIGHT;
        }

        // Header
        if (layout.hasHeader) {
            int headerH = fontHeight + ITEM_PADDING * 2;
            drawHeaderGradient(gc, x, drawY, contentW, headerH);
            String titleStr = resolveLabel(screen.title);
            if (!titleStr.isEmpty()) {
                gc.setFill(Color.WHITE);
                gc.setFont(fontBold);
                gc.fillText(titleStr, x + OUTER_PADDING + 2, drawY + fontHeight + INNER_MARGIN);
            }
            drawY += headerH + INNER_MARGIN;
        }

        // Toast: first item as title
        if (layout.firstItemAsTitle && !screen.items.isEmpty()) {
            int headerH = fontHeight + ITEM_PADDING * 2;
            drawHeaderGradient(gc, x, drawY, contentW, headerH);
            String titleStr = resolveItemLabel(screen.items.get(0));
            gc.setFill(Color.WHITE);
            gc.setFont(fontBold);
            gc.fillText(titleStr, x + OUTER_PADDING + 2, drawY + fontHeight + INNER_MARGIN);
            drawY += headerH + INNER_MARGIN;
        }

        // Items
        int itemX = x + OUTER_PADDING;
        int itemW = contentW - OUTER_PADDING * 2;
        gc.setFont(fontPlain);

        int startIdx = layout.firstItemAsTitle ? 1 : 0;
        for (int i = startIdx; i < screen.items.size(); i++) {
            var item = screen.items.get(i);
            int itemY = drawY;
            drawY = drawItem(gc, item, itemX, drawY, itemW, screen.checkboxes);
            if (i == selectedItem) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                gc.strokeRect(itemX, itemY, itemW, drawY - itemY);
            }
            if (layout.isOverlay() && drawY > y + contentH - SOFT_KEY_HEIGHT) break;
        }

        // Scrollbar indicator (if scrollable and content overflows)
        if (layout.scrollable) {
            int totalContent = estimateItemsHeight(screen, startIdx);
            int visibleH = contentH - (drawY - y) + totalContent;
            if (totalContent > contentH) {
                int sbX = x + contentW - 5;
                int sbY = y + (layout.hasHeader ? fontHeight + ITEM_PADDING * 2 + INNER_MARGIN : 0);
                int sbH = contentH - SOFT_KEY_HEIGHT - sbY + y;
                gc.setFill(paletteColor(PAL_BORDER));
                gc.setGlobalAlpha(0.3);
                gc.fillRect(sbX, sbY, 3, sbH);
                gc.setGlobalAlpha(0.7);
                gc.fillRect(sbX, sbY, 3, sbH / 3);
                gc.setGlobalAlpha(1.0);
            }
        }

        // Soft keys
        if (layout.isFullscreen()) {
            drawSoftKeys(gc, screen, x, y + contentH - SOFT_KEY_HEIGHT, contentW);
        }

        // Type badge
        drawTypeBadge(gc, screen, layout);
    }

    private void renderMapScreen(GraphicsContext gc, ScreenDef screen, ScreenLayout layout) {
        // Map background
        gc.setFill(Color.rgb(200, 215, 200));
        gc.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // Grid pattern
        gc.setStroke(Color.rgb(180, 195, 180));
        gc.setLineWidth(0.5);
        for (int gx = 0; gx < SCREEN_W; gx += 32) gc.strokeLine(gx, 0, gx, SCREEN_H);
        for (int gy = 0; gy < SCREEN_H; gy += 32) gc.strokeLine(0, gy, SCREEN_W, gy);

        // Center crosshair
        gc.setStroke(Color.rgb(100, 100, 100));
        gc.setLineWidth(1);
        gc.strokeLine(SCREEN_W / 2 - 10, SCREEN_H / 2, SCREEN_W / 2 + 10, SCREEN_H / 2);
        gc.strokeLine(SCREEN_W / 2, SCREEN_H / 2 - 10, SCREEN_W / 2, SCREEN_H / 2 + 10);

        // Header
        int headerH = fontHeight + ITEM_PADDING * 2;
        drawHeaderGradient(gc, 0, 0, SCREEN_W, headerH);
        String titleStr = resolveLabel(screen.title);
        if (!titleStr.isEmpty()) {
            gc.setFill(Color.WHITE);
            gc.setFont(fontBold);
            gc.fillText(titleStr, OUTER_PADDING + 2, fontHeight + INNER_MARGIN);
        }

        // Map label
        gc.setFill(Color.rgb(80, 80, 80));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        gc.fillText("[MAP VIEW]", SCREEN_W / 2 - 40, SCREEN_H / 2 + 30);
        gc.setFont(fontPlain);

        // Soft keys
        drawSoftKeys(gc, screen, 0, SCREEN_H - SOFT_KEY_HEIGHT, SCREEN_W);

        // Type badge
        drawTypeBadge(gc, screen, layout);
    }

    private void renderGridScreen(GraphicsContext gc, ScreenDef screen, ScreenLayout layout) {
        gc.setFill(paletteColor(PAL_BACKGROUND));
        gc.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // Header
        int headerH = fontHeight + ITEM_PADDING * 2;
        drawHeaderGradient(gc, 0, 0, SCREEN_W, headerH);
        String titleStr = resolveLabel(screen.title);
        if (!titleStr.isEmpty()) {
            gc.setFill(Color.WHITE);
            gc.setFont(fontBold);
            gc.fillText(titleStr, OUTER_PADDING + 2, fontHeight + INNER_MARGIN);
        }

        // Grid of items
        int cols = 4;
        int cellSize = (SCREEN_W - OUTER_PADDING * 2) / cols;
        int startY = headerH + INNER_MARGIN;
        gc.setFont(fontSmall);
        for (int i = 0; i < screen.items.size(); i++) {
            int col = i % cols;
            int row = i / cols;
            int cx = OUTER_PADDING + col * cellSize;
            int cy = startY + row * cellSize;
            if (cy + cellSize > SCREEN_H - SOFT_KEY_HEIGHT) break;

            // Cell border
            gc.setStroke(paletteColor(PAL_BORDER));
            gc.setLineWidth(0.5);
            gc.strokeRect(cx + 1, cy + 1, cellSize - 2, cellSize - 2);

            // Icon or index
            var item = screen.items.get(i);
            if (item.icon > 0) {
                drawIcon(gc, cx + (cellSize - ICON_SIZE) / 2.0, cy + 4, item.icon);
            } else {
                gc.setFill(paletteColor(PAL_DISABLED));
                gc.fillText(String.valueOf(i), cx + cellSize / 2.0 - 4, cy + cellSize / 2.0 + 4);
            }

            if (i == selectedItem) {
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                gc.strokeRect(cx, cy, cellSize, cellSize);
            }
        }
        gc.setFont(fontPlain);

        drawSoftKeys(gc, screen, 0, SCREEN_H - SOFT_KEY_HEIGHT, SCREEN_W);
        drawTypeBadge(gc, screen, layout);
    }

    private void drawTypeBadge(GraphicsContext gc, ScreenDef screen, ScreenLayout layout) {
        ScreenCategory cat = screen.type.category;
        String label = screen.type.toConfigString();

        gc.setFont(fontSmall);
        javafx.scene.text.Text measure = new javafx.scene.text.Text(label);
        measure.setFont(fontSmall);
        double textW = measure.getLayoutBounds().getWidth();

        double bx = SCREEN_W - textW - 10;
        double by = 1;
        gc.setFill(Color.web(cat.fillColor, 0.85));
        gc.fillRoundRect(bx, by, textW + 8, fontSmallHeight + 4, 4, 4);
        gc.setFill(Color.WHITE);
        gc.fillText(label, bx + 4, by + fontSmallHeight);
        gc.setFont(fontPlain);
    }

    private void drawTabBar(GraphicsContext gc, int x, int y, int w) {
        gc.setFill(paletteColor(PAL_POPUP_BG));
        gc.fillRect(x, y, w, TAB_BAR_HEIGHT);
        gc.setStroke(paletteColor(PAL_BORDER));
        gc.strokeLine(x, y + TAB_BAR_HEIGHT, x + w, y + TAB_BAR_HEIGHT);

        gc.setFont(fontSmall);
        gc.setFill(paletteColor(PAL_TEXT));
        int tabW = w / 3;
        for (int i = 0; i < 3; i++) {
            String tabLabel = "Tab " + (i + 1);
            gc.fillText(tabLabel, x + i * tabW + 8, y + fontSmallHeight);
            if (i > 0) gc.strokeLine(x + i * tabW, y + 2, x + i * tabW, y + TAB_BAR_HEIGHT - 2);
        }
        gc.setFont(fontPlain);
    }

    private int estimateContentHeight(ScreenDef screen, ScreenLayout layout) {
        int total = 0;
        if (layout.hasHeader || layout.firstItemAsTitle) total += fontHeight + ITEM_PADDING * 2 + INNER_MARGIN;
        if (layout.hasTabBar) total += TAB_BAR_HEIGHT;
        int startIdx = layout.firstItemAsTitle ? 1 : 0;
        total += estimateItemsHeight(screen, startIdx);
        total += SOFT_KEY_HEIGHT;
        return total + 8;
    }

    private int estimateItemsHeight(ScreenDef screen, int startIdx) {
        int total = 0;
        for (int i = startIdx; i < screen.items.size(); i++) {
            total += switch (screen.items.get(i).type) {
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

    // --- Item rendering (unchanged) ---

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
            default -> {
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

    private void drawActionItem(GraphicsContext gc, int x, int y, int w, int h, int icon, String label) {
        int textX = x + INNER_MARGIN;
        if (icon > 0) {
            drawIcon(gc, x + INNER_MARGIN, y + INNER_MARGIN, icon);
            textX = x + INNER_MARGIN + ICON_SIZE + INNER_MARGIN;
        }
        gc.setFill(paletteColor(PAL_TEXT));
        gc.fillText(label, textX, y + fontHeight + INNER_MARGIN);
    }

    // --- Shared helpers ---

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

    private static int interpolate(int from, int to, double ratio) {
        return (int) (from + (to - from) * ratio);
    }

    private void drawSoftKeys(GraphicsContext gc, ScreenDef screen, int x, int y, int w) {
        gc.setFill(paletteColor(PAL_POPUP_BG));
        gc.fillRect(x, y, w, SOFT_KEY_HEIGHT);
        gc.setStroke(paletteColor(PAL_BORDER));
        gc.strokeLine(x, y, x + w, y);
        gc.setFont(fontSmall);
        gc.setFill(paletteColor(PAL_TEXT));

        String leftLabel = resolveSoftKeyLabel(screen.leftKey);
        if (!leftLabel.isEmpty())
            gc.fillText(leftLabel, x + INNER_MARGIN + 2, y + fontSmallHeight + INNER_MARGIN);

        String rightLabel = resolveSoftKeyLabel(screen.rightKey);
        if (!rightLabel.isEmpty()) {
            javafx.scene.text.Text measure = new javafx.scene.text.Text(rightLabel);
            measure.setFont(gc.getFont());
            double textW = measure.getLayoutBounds().getWidth();
            gc.fillText(rightLabel, x + w - textW - INNER_MARGIN - 2, y + fontSmallHeight + INNER_MARGIN);
        }
        gc.setFont(fontPlain);
    }

    private void drawIcon(GraphicsContext gc, double x, double y, int iconCode) {
        if (!showSprites) { drawIconFallback(gc, x, y, iconCode); return; }
        var loc = config.getIconLocation(iconCode);
        if (loc == null) { drawIconFallback(gc, x, y, iconCode); return; }
        Image sheet = spriteCache.computeIfAbsent(loc.filePath(), path -> {
            try {
                if (Files.exists(path)) return new Image(path.toUri().toString());
            } catch (Exception ignored) {}
            return null;
        });
        if (sheet == null) { drawIconFallback(gc, x, y, iconCode); return; }
        gc.drawImage(sheet, loc.tileX(), loc.tileY(), ICON_SIZE, ICON_SIZE, x, y, ICON_SIZE, ICON_SIZE);
    }

    private void drawIconFallback(GraphicsContext gc, double x, double y, int iconCode) {
        int hue = (iconCode * 37) % 360;
        gc.setFill(Color.hsb(hue, 0.3, 0.85));
        gc.fillRect(x, y, ICON_SIZE, ICON_SIZE);
    }

    private String resolveLabel(int key) {
        if (key <= 0) return "";
        return config.resolveString(key);
    }

    private String resolveItemLabel(ScreenDef.Item item) {
        if (item.labelHint != null && !item.labelHint.isEmpty()) return item.labelHint;
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
        if (pal == null || theme >= pal.length || index >= pal[theme].length) return 0x808080;
        return pal[theme][index];
    }
}
