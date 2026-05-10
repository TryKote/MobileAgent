package com.trykote.editor;

/**
 * Layout parameters for rendering a screen preview.
 * Derived from ScreenDef.ScreenType, encapsulates all type-specific behavior.
 */
public class ScreenLayout {

    public enum Position { FULL, CENTER, BOTTOM, CORNER, LOW, POPUP }

    public final double widthRatio;
    public final double heightRatio;
    public final Position position;
    public final boolean hasHeader;
    public final boolean scrollable;
    public final boolean hasTabBar;
    public final boolean firstItemAsTitle;

    private ScreenLayout(double widthRatio, double heightRatio, Position position,
                         boolean hasHeader, boolean scrollable, boolean hasTabBar,
                         boolean firstItemAsTitle) {
        this.widthRatio = widthRatio;
        this.heightRatio = heightRatio;
        this.position = position;
        this.hasHeader = hasHeader;
        this.scrollable = scrollable;
        this.hasTabBar = hasTabBar;
        this.firstItemAsTitle = firstItemAsTitle;
    }

    public boolean isFullscreen() {
        return position == Position.FULL;
    }

    public boolean isOverlay() {
        return !isFullscreen();
    }

    /** Compute X offset within the screen canvas. */
    public int offsetX(int screenW, int contentW, int contentH) {
        return switch (position) {
            case FULL -> 0;
            case CENTER, LOW -> (screenW - contentW) / 2;
            case BOTTOM -> 0;
            case CORNER -> screenW - contentW;
            case POPUP -> Math.min(screenW / 3, screenW - contentW);
        };
    }

    /** Compute Y offset within the screen canvas. */
    public int offsetY(int screenH, int contentW, int contentH) {
        return switch (position) {
            case FULL -> 0;
            case CENTER -> (screenH - contentH) / 2;
            case BOTTOM -> screenH - contentH;
            case CORNER -> screenH - contentH;
            case LOW -> (screenH - contentH) / 2 + contentH / 10;
            case POPUP -> 20;
        };
    }

    public static ScreenLayout of(ScreenDef.ScreenType type) {
        return switch (type) {
            case FULLSCREEN ->
                new ScreenLayout(1, 1, Position.FULL, true, true, false, false);
            case FULLSCREEN_ALT ->
                new ScreenLayout(1, 1, Position.FULL, true, true, true, false);
            case FULLSCREEN_NOSCROLL ->
                new ScreenLayout(1, 1, Position.FULL, true, false, false, false);
            case FULLSCREEN_NOSCROLL_ALT ->
                new ScreenLayout(1, 1, Position.FULL, true, false, true, false);
            case DIALOG_CENTER ->
                new ScreenLayout(0.9, 0.9, Position.CENTER, false, true, false, false);
            case DIALOG_BOTTOM ->
                new ScreenLayout(0.9, 0.9, Position.BOTTOM, false, true, false, false);
            case DIALOG_CORNER ->
                new ScreenLayout(0.9, 0.9, Position.CORNER, false, true, false, false);
            case DIALOG_LOW ->
                new ScreenLayout(0.9, 0.9, Position.LOW, false, true, false, false);
            case POPUP ->
                new ScreenLayout(0.9, 0.9, Position.POPUP, false, true, false, false);
            case TOAST ->
                new ScreenLayout(0.9, 0.9, Position.CENTER, false, false, false, true);
            case TOAST_CENTER ->
                new ScreenLayout(0.9, 0.9, Position.CENTER, false, false, false, false);
            case MAP ->
                new ScreenLayout(1, 1, Position.FULL, true, true, false, false);
        };
    }
}
