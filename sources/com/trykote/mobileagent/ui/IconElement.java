package com.trykote.mobileagent.ui;

public final class IconElement implements RenderElement {

    public final int iconSize;
    public final int iconCode;

    public IconElement(int iconSize, int iconCode) {
        this.iconSize = iconSize;
        this.iconCode = iconCode;
    }

    public int getWidth() {
        return this.iconSize + 2;
    }

    public int getHeight() {
        return 16;
    }

    public void render(GraphicsContext gfx, int x, int y, int baseX, int containerWidth) {
        int drawX = (this.iconCode != 244) ? x : (baseX + containerWidth) - 13;
        gfx.drawIcon(this.iconCode, drawX, y + ScreenManager.getCenterOffset());
    }
}
