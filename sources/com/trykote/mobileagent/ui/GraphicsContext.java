package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
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

/* renamed from: al */
/* loaded from: MobileAgent_3.9.jar:al.class */
public final class GraphicsContext {

    /* renamed from: a */
    public Image image;

    /* renamed from: b */
    public Graphics graphics;

    /* renamed from: c */
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

    /* renamed from: a */
    public final GraphicsContext setColor(int i) {
        this.graphics.setColor(i);
        return this;
    }

    /* renamed from: b */
    public final GraphicsContext setColorFromPalette(int i) {
        this.graphics.setColor(AppState.getInt(StateKeys.PALETTE_COLORS_BASE + (i << 3) + AppState.getInt(StateKeys.SETTING_COLOR_THEME)));
        return this;
    }

    /* renamed from: a */
    public final GraphicsContext setClip(int i, int i2, int i3, int i4) {
        this.graphics.setClip(i, i2, i3, i4);
        return this;
    }

    /* renamed from: b */
    public final GraphicsContext drawLine(int i, int i2, int i3, int i4) {
        this.graphics.drawLine(i, i2, i3, i4);
        return this;
    }

    /* renamed from: c */
    public final GraphicsContext fillRect(int i, int i2, int i3, int i4) {
        this.graphics.fillRect(i, i2, i3, i4);
        return this;
    }

    /* renamed from: d */
    public final GraphicsContext drawRect(int i, int i2, int i3, int i4) {
        this.graphics.drawRect(i, i2, i3, i4);
        return this;
    }

    /* renamed from: a */
    public final GraphicsContext setFont(GraphicsContext other) {
        this.graphics.setFont(other.font);
        return this;
    }

    /* renamed from: a */
    public final GraphicsContext drawString(String str, int i, int i2, int i3) {
        if (i2 > 0 && i2 < AppState.getInt(StateKeys.INT_SCREEN_HEIGHT)) {
            this.graphics.drawString(str, i, i2, i3);
        }
        return this;
    }

    /* renamed from: a */
    public final int stringWidth(String str) {
        if (str != null) {
            return this.font.stringWidth(str);
        }
        return 0;
    }

    /* renamed from: a */
    public final int substringWidth(String str, int i, int i2) {
        if (i2 > 0) {
            return this.font.substringWidth(str, i, i2);
        }
        return 0;
    }

    /* renamed from: a */
    public final GraphicsContext drawIcon(int i, int i2, int i3) {
        int clipWidth;
        int clipWidth2;
        int clipHeight;
        int clipHeight2;
        int i4 = i >>> 16;
        if (i4 != 0) {
            return drawIcon(i & 65535, i2, i3).drawIcon(i4, i2, i3);
        }
        if ((i & 16384) != 0 && AppState.getBool(StateKeys.FLAG_BLINK_STATE)) {
            return this;
        }
        Graphics graphics = this.graphics;
        int i5 = i & (-16385);
        int i6 = i3;
        int i7 = i2;
        int clipX = graphics.getClipX();
        if (clipX - i7 < 16) {
            int clipY = graphics.getClipY();
            if (clipY - i6 < 16 && (clipWidth2 = (clipX - i7) + (clipWidth = graphics.getClipWidth())) > 0 && (clipHeight2 = (clipY - i6) + (clipHeight = graphics.getClipHeight())) > 0) {
                int drawW = Utils.min(clipWidth2, 16);
                int drawW2 = Utils.min(clipHeight2, 16);
                int i8 = i5 <= 354 ? 255 & AppState.getBytes(StateKeys.RES_STRING_DATA)[i5 + 39] : 256 + AppState.getBytes(StateKeys.RES_STRING_DATA)[i5 + 39];
                int i9 = i8;
                int i10 = i8 >> 4;
                int i11 = i9 & 15;
                int i12 = (i11 & 3) << 4;
                int i13 = (i11 >> 2) << 4;
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

    /* renamed from: c */
    public static final int getIconSize(int i) {
        return i == 247 ? 6 : 16;
    }
}
