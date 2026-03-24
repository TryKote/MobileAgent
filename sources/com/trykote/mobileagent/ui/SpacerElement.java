package com.trykote.mobileagent.ui;

public final class SpacerElement implements RenderElement {

    public final int width;
    public final int height;

    public SpacerElement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width + 2;
    }

    public int getHeight() {
        return this.height;
    }

    public void render(GraphicsContext gfx, int x, int y, int baseX, int containerWidth) {
        gfx.setColorFromPalette(18).drawRect(x, y, containerWidth - x, this.height);
    }
}
