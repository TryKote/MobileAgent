package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class GraphicsContext {

    private static final int COMPOSITE_LOWER_MASK = 0xFFFF;
    private static final int BLINK_FLAG = 16384;
    private static final int BLINK_CLEAR_MASK = ~BLINK_FLAG;

    private static final int ICON_SIZE = 16;
    private static final int ICON_DATA_BOUNDARY = 354;
    private static final int ICON_DATA_UNSIGNED_MASK = 255;
    private static final int ICON_DATA_SIGNED_OFFSET = 256;

    private static final int DROPDOWN_ICON = 247;
    private static final int DROPDOWN_ICON_WIDTH = 6;

    private static final int NIBBLE_SHIFT = 4;
    private static final int NIBBLE_MASK = 15;
    private static final int TILE_INDEX_MASK = 3;

    private static final int PALETTE_SHIFT = 3;

    public Image image;

    public Graphics graphics;

    public Font font;

    public GraphicsContext() {
    }

    public GraphicsContext(Graphics graphics) {
        this.graphics = graphics;
    }

    public GraphicsContext(Image image) {
        this.image = image;
    }

    public GraphicsContext(int i, int i2) {
        this.font = Font.getFont(64, i, i2);
    }

    public final GraphicsContext setColor(int i) {
        this.graphics.setColor(i);
        return this;
    }

    public final GraphicsContext setColorFromPalette(int role) {
        this.graphics.setColor(Palette.getColor(SettingsState.getColorTheme(), role));
        return this;
    }

    public final GraphicsContext setClip(int i, int i2, int i3, int i4) {
        this.graphics.setClip(i, i2, i3, i4);
        return this;
    }

    public final GraphicsContext drawLine(int i, int i2, int i3, int i4) {
        this.graphics.drawLine(i, i2, i3, i4);
        return this;
    }

    public final GraphicsContext fillRect(int i, int i2, int i3, int i4) {
        this.graphics.fillRect(i, i2, i3, i4);
        return this;
    }

    public final GraphicsContext drawRect(int i, int i2, int i3, int i4) {
        this.graphics.drawRect(i, i2, i3, i4);
        return this;
    }

    public final GraphicsContext setFont(GraphicsContext other) {
        this.graphics.setFont(other.font);
        return this;
    }

    public final GraphicsContext drawString(String str, int i, int i2, int i3) {
        if (i2 > 0 && i2 < UIState.getScreenHeight()) {
            this.graphics.drawString(str, i, i2, i3);
        }
        return this;
    }

    public final int stringWidth(String str) {
        if (str != null) {
            return this.font.stringWidth(str);
        }
        return 0;
    }

    public final int substringWidth(String str, int i, int i2) {
        if (i2 > 0) {
            return this.font.substringWidth(str, i, i2);
        }
        return 0;
    }

    public final GraphicsContext drawIcon(int i, int i2, int i3) {
        int clipWidth;
        int clipWidth2;
        int clipHeight;
        int clipHeight2;
        int i4 = i >>> 16;
        if (i4 != 0) {
            return drawIcon(i & COMPOSITE_LOWER_MASK, i2, i3).drawIcon(i4, i2, i3);
        }
        if ((i & BLINK_FLAG) != 0 && UIState.isBlinkState()) {
            return this;
        }
        Graphics graphics = this.graphics;
        int i5 = i & BLINK_CLEAR_MASK;
        int i6 = i3;
        int i7 = i2;
        int clipX = graphics.getClipX();
        if (clipX - i7 < ICON_SIZE) {
            int clipY = graphics.getClipY();
            if (clipY - i6 < ICON_SIZE && (clipWidth2 = (clipX - i7) + (clipWidth = graphics.getClipWidth())) > 0 && (clipHeight2 = (clipY - i6) + (clipHeight = graphics.getClipHeight())) > 0) {
                int drawW = Utils.min(clipWidth2, ICON_SIZE);
                int drawW2 = Utils.min(clipHeight2, ICON_SIZE);
                int i8 = i5 <= ICON_DATA_BOUNDARY ? ICON_DATA_UNSIGNED_MASK & ResourceAccessor.bytes(StringResKeys.RES_STRING_DATA)[i5 + 39] : ICON_DATA_SIGNED_OFFSET + ResourceAccessor.bytes(StringResKeys.RES_STRING_DATA)[i5 + 39];
                int i9 = i8;
                int i10 = i8 >> NIBBLE_SHIFT;
                int i11 = i9 & NIBBLE_MASK;
                int i12 = (i11 & TILE_INDEX_MASK) << NIBBLE_SHIFT;
                int i13 = (i11 >> 2) << NIBBLE_SHIFT;
                int i14 = clipX - i7;
                if (i14 > 0) {
                    drawW -= i14;
                    i7 = clipX;
                    i12 += i14;
                }
                if (drawW > 0) {
                    int i15 = clipY - i6;
                    if (i15 > 0) {
                        drawW2 -= i15;
                        i6 = clipY;
                        i13 += i15;
                    }
                    if (drawW2 > 0) {
                        graphics.setClip(i7, i6, drawW, drawW2);
                        graphics.drawImage(XmppContactGroup.getOrLoadImage(i10), i7 - i12, i6 - i13, 20);
                        graphics.setClip(clipX, clipY, clipWidth, clipHeight);
                    }
                }
            }
        }
        return this;
    }

    public static final int getIconSize(int i) {
        return i == DROPDOWN_ICON ? DROPDOWN_ICON_WIDTH : ICON_SIZE;
    }
}
