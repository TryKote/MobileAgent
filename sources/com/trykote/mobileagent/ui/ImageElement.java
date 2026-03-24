package com.trykote.mobileagent.ui;


import javax.microedition.lcdui.Image;

public final class ImageElement implements RenderElement {

    public final Image image;

    public ImageElement(Image image) {
        this.image = image;
    }

    public int getWidth() {
        return this.image.getWidth();
    }

    public int getHeight() {
        return this.image.getHeight() + 5;
    }

    public void render(GraphicsContext gfx, int x, int y, int baseX, int containerWidth) {
        gfx.graphics.drawImage(this.image, x, y, 20);
    }
}
