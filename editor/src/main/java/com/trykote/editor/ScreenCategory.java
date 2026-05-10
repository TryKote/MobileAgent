package com.trykote.editor;

/**
 * Broad classification of screen types for display, filtering, and graph coloring.
 * Mapped from ScreenType via {@link ScreenDef.ScreenType#category}.
 */
public enum ScreenCategory {
    FULLSCREEN("fullscreen", "#2d5f8a", "#4a90c4"),
    POPUP     ("popup",      "#6a4c93", "#9b72cf"),
    DIALOG    ("dialog",     "#4a7c59", "#6db07f"),
    TOAST     ("toast",      "#8b6914", "#c9a020"),
    GRID      ("grid",       "#7c4040", "#b06060");

    public final String label;
    public final String fillColor;
    public final String borderColor;

    ScreenCategory(String label, String fillColor, String borderColor) {
        this.label = label;
        this.fillColor = fillColor;
        this.borderColor = borderColor;
    }
}
