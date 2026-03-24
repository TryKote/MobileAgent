package com.trykote.mobileagent.ui;

public interface RenderElement {
    int getWidth();
    int getHeight();
    void render(GraphicsContext gfx, int x, int y, int baseX, int containerWidth);
}
