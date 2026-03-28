package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;

public final class TextElement implements RenderElement {

    public final String text;
    public final int textWidth;
    public final int offsetH;
    public final int startIdx;
    public final int length;
    public final int fontIdx;
    public final int colorIdx;

    public TextElement(String text, int textWidth, int offsetH, int startIdx, int length, int fontIdx, int colorIdx) {
        this.text = text;
        this.textWidth = textWidth;
        this.offsetH = offsetH;
        this.startIdx = startIdx;
        this.length = length;
        this.fontIdx = fontIdx;
        this.colorIdx = colorIdx;
    }

    public int getWidth() {
        return this.textWidth;
    }

    public int getHeight() {
        return this.offsetH;
    }

    public void render(GraphicsContext gfx, int x, int y, int baseX, int containerWidth) {
        GraphicsContext fontGfx = AppState.getGfxContext(this.fontIdx);
        gfx.setFont(fontGfx).setColorFromPalette(this.colorIdx);
        if (y > 0 && y < AppState.getInt(UIKeys.INT_SCREEN_HEIGHT)) {
            gfx.graphics.drawSubstring(this.text, this.startIdx, this.length, x, y, 20);
        }
        if (this.fontIdx == 3) {
            gfx.drawRect(x, y + (AppState.getInt(UIKeys.INT_FONT_HEIGHT) >> 1), fontGfx.substringWidth(this.text, this.startIdx, this.length), 0);
        } else if (this.fontIdx == 5) {
            gfx.drawRect(x, y + AppState.getInt(UIKeys.INT_FONT_HEIGHT), fontGfx.substringWidth(this.text, this.startIdx, this.length), 0);
        }
    }
}
