package p000;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* renamed from: al */
/* loaded from: MobileAgent_3.9.jar:al.class */
public final class C0012al {

    /* renamed from: a */
    public Image f91a;

    /* renamed from: b */
    public Graphics f92b;

    /* renamed from: c */
    public Font f93c;

    public C0012al() {
    }

    public C0012al(Graphics graphics) {
        this.f92b = graphics;
    }

    public C0012al(Image image) {
        this.f91a = image;
    }

    public C0012al(int i, int i2) {
        this.f93c = Font.getFont(64, i, i2);
    }

    /* renamed from: a */
    public final C0012al m206a(int i) {
        this.f92b.setColor(i);
        return this;
    }

    /* renamed from: b */
    public final C0012al m207b(int i) {
        this.f92b.setColor(AppState.m586d(4914 + (i << 3) + AppState.m586d(72)));
        return this;
    }

    /* renamed from: a */
    public final C0012al m208a(int i, int i2, int i3, int i4) {
        this.f92b.setClip(i, i2, i3, i4);
        return this;
    }

    /* renamed from: b */
    public final C0012al m209b(int i, int i2, int i3, int i4) {
        this.f92b.drawLine(i, i2, i3, i4);
        return this;
    }

    /* renamed from: c */
    public final C0012al m210c(int i, int i2, int i3, int i4) {
        this.f92b.fillRect(i, i2, i3, i4);
        return this;
    }

    /* renamed from: d */
    public final C0012al m211d(int i, int i2, int i3, int i4) {
        this.f92b.drawRect(i, i2, i3, i4);
        return this;
    }

    /* renamed from: a */
    public final C0012al m212a(C0012al c0012al) {
        this.f92b.setFont(c0012al.f93c);
        return this;
    }

    /* renamed from: a */
    public final C0012al m213a(String str, int i, int i2, int i3) {
        if (i2 > 0 && i2 < AppState.m586d(1529)) {
            this.f92b.drawString(str, i, i2, i3);
        }
        return this;
    }

    /* renamed from: a */
    public final int m214a(String str) {
        if (str != null) {
            return this.f93c.stringWidth(str);
        }
        return 0;
    }

    /* renamed from: a */
    public final int m215a(String str, int i, int i2) {
        if (i2 > 0) {
            return this.f93c.substringWidth(str, i, i2);
        }
        return 0;
    }

    /* renamed from: a */
    public final C0012al m216a(int i, int i2, int i3) {
        int clipWidth;
        int clipWidth2;
        int clipHeight;
        int clipHeight2;
        int i4 = i >>> 16;
        if (i4 != 0) {
            return m216a(i & 65535, i2, i3).m216a(i4, i2, i3);
        }
        if ((i & 16384) != 0 && AppState.m587e(1534)) {
            return this;
        }
        Graphics graphics = this.f92b;
        int i5 = i & (-16385);
        int i6 = i3;
        int i7 = i2;
        int clipX = graphics.getClipX();
        if (clipX - i7 < 16) {
            int clipY = graphics.getClipY();
            if (clipY - i6 < 16 && (clipWidth2 = (clipX - i7) + (clipWidth = graphics.getClipWidth())) > 0 && (clipHeight2 = (clipY - i6) + (clipHeight = graphics.getClipHeight())) > 0) {
                int iM503b = Utils.m503b(clipWidth2, 16);
                int iM503b2 = Utils.m503b(clipHeight2, 16);
                int i8 = i5 <= 354 ? 255 & AppState.m581a(295)[i5 + 39] : 256 + AppState.m581a(295)[i5 + 39];
                int i9 = i8;
                int i10 = i8 >> 4;
                int i11 = i9 & 15;
                int i12 = (i11 & 3) << 4;
                int i13 = (i11 >> 2) << 4;
                int i14 = clipX - i7;
                if (i14 > 0) {
                    iM503b -= i14;
                    i7 = clipX;
                    i12 += i14;
                }
                if (iM503b > 0) {
                    int i15 = clipY - i6;
                    if (i15 > 0) {
                        iM503b2 -= i15;
                        i6 = clipY;
                        i13 += i15;
                    }
                    if (iM503b2 > 0) {
                        graphics.setClip(i7, i6, iM503b, iM503b2);
                        graphics.drawImage(C0036g.m1023b(i10), i7 - i12, i6 - i13, 20);
                        graphics.setClip(clipX, clipY, clipWidth, clipHeight);
                    }
                }
            }
        }
        return this;
    }

    /* renamed from: c */
    public static final int m217c(int i) {
        return i == 247 ? 6 : 16;
    }
}
