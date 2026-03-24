package com.trykote.mobileagent.ui;

public final class LineBreak implements RenderElement {

    public static final LineBreak INSTANCE = new LineBreak();

    public int getWidth() {
        return 0;
    }

    public int getHeight() {
        return 0;
    }

    public void render(GraphicsContext gfx, int x, int y, int baseX, int containerWidth) {
    }
}
