package p000;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/* renamed from: am */
/* loaded from: MobileAgent_3.9.jar:am.class */
public final class Screen {

    /* renamed from: a */
    public int f94a;

    /* renamed from: b */
    public int f95b;

    /* renamed from: c */
    public int f96c;

    /* renamed from: d */
    public int f97d;

    /* renamed from: e */
    public int f98e;

    /* renamed from: f */
    public int f99f;

    /* renamed from: g */
    public int f100g;

    /* renamed from: C */
    private MenuItem f101C;

    /* renamed from: h */
    public final int f102h;

    /* renamed from: i */
    public boolean f103i;

    /* renamed from: D */
    private boolean f104D;

    /* renamed from: j */
    public int f105j;

    /* renamed from: k */
    public int f106k;

    /* renamed from: l */
    public int f107l;

    /* renamed from: m */
    public Vector f108m;

    /* renamed from: n */
    public int[] f109n;

    /* renamed from: o */
    public Vector f110o;

    /* renamed from: E */
    private int f111E;

    /* renamed from: F */
    private int f112F;

    /* renamed from: p */
    public int f113p;

    /* renamed from: q */
    public int f114q;

    /* renamed from: r */
    public int f115r;

    /* renamed from: G */
    private int f116G;

    /* renamed from: H */
    private int f117H;

    /* renamed from: I */
    private int f118I;

    /* renamed from: J */
    private int f119J;

    /* renamed from: s */
    public int f120s;

    /* renamed from: t */
    public int f121t;

    /* renamed from: u */
    public int f122u;

    /* renamed from: v */
    public String f123v;

    /* renamed from: w */
    public String f124w;

    /* renamed from: x */
    public boolean f125x;

    /* renamed from: K */
    private int f126K;

    /* renamed from: y */
    public boolean f127y;

    /* renamed from: L */
    private int f128L;

    /* renamed from: M */
    private int f129M;

    /* renamed from: z */
    public int f130z;

    /* renamed from: A */
    public int f131A;

    /* renamed from: B */
    public boolean f132B;

    public Screen() {
        this.f102h = 2;
        this.f94a = 63;
    }

    /* renamed from: a */
    public final Screen m218a() {
        if (AppState.getBool(245)) {
            this.f110o = NetworkUtils.newVector();
            m219s();
        }
        return this;
    }

    private Screen(int i, int i2, int i3, int i4) {
        this.f108m = NetworkUtils.newVector();
        this.f109n = new int[16];
        this.f102h = i;
        this.f95b = i3;
        this.f96c = i4;
        this.f94a = i2;
        this.f106k = -1;
        m219s();
    }

    public Screen(int i, int i2, int i3, int i4, boolean z) {
        this(i, i2, i3, i4);
        this.f103i = z;
    }

    /* renamed from: s */
    private final Screen m219s() {
        int i = this.f95b - 4;
        int i2 = this.f96c - 4;
        this.f111E = this.f95b - 2;
        if (this.f101C != null) {
            this.f112F = this.f101C.getTotalHeight() + 2;
        } else {
            this.f112F = 0;
        }
        this.f116G = this.f95b - 3;
        this.f117H = 1 + this.f112F;
        this.f118I = (i2 - this.f117H) + 3;
        this.f113p = this.f117H + 1;
        this.f114q = i - 2;
        this.f115r = this.f118I - 2;
        if (this.f110o != null) {
            int i3 = this.f118I;
            int iM502a = Utils.max(AppState.getInt(1450), 16) + 3;
            this.f118I = i3 - iM502a;
            this.f115r -= iM502a;
        }
        return this;
    }

    /* renamed from: b */
    public final String m220b() {
        if (!this.f103i || this.f108m.size() <= 0 || this.f106k < 0) {
            return null;
        }
        return m239b(this.f106k).title;
    }

    /* renamed from: c */
    public final int m221c() {
        if (!this.f103i || this.f108m.size() <= 0) {
            return 200;
        }
        return m239b(this.f106k).width;
    }

    /* renamed from: d */
    public final MenuItem m222d() {
        if (!this.f103i || this.f108m.size() <= 0) {
            return null;
        }
        return m239b(this.f106k);
    }

    /* renamed from: e */
    public final MenuItem m223e() {
        int iM261v;
        if (this.f97d != 9 || (iM261v = m261v()) >= this.f108m.size()) {
            return null;
        }
        return m239b(iM261v);
    }

    /* renamed from: a */
    public final Screen m224a(int i, String str) {
        this.f101C = MenuItem.createSeparator().addText(AppState.getString(1037), 1, 0).setLabelInternal(i, str, 1, 0);
        m219s();
        return this;
    }

    /* renamed from: a */
    public final Screen m225a(MenuItem c0032c) {
        if (this.f102h == 0) {
            c0032c.layout(this.f114q);
            this.f108m.addElement(c0032c);
            this.f109n = AppController.m302a(this.f109n, 0, this.f107l);
            this.f107l += c0032c.getTotalHeight();
        } else {
            int iM240c = 0;
            int iM241d = 0;
            int size = this.f108m.size() - 1;
            if (this.f108m.size() > 0) {
                iM240c = m240c(size) + m239b(size).getTotalWidth();
                iM241d = m241d(size);
            }
            if (iM240c > 0 && iM240c + c0032c.getTotalWidth() >= this.f114q) {
                iM240c = 0;
                iM241d = m241d(size) + m239b(size).getTotalHeight();
            }
            this.f108m.addElement(c0032c);
            this.f109n = AppController.m302a(this.f109n, iM240c, iM241d);
            this.f107l = iM241d + c0032c.getTotalHeight();
        }
        if (this.f107l <= 0 || this.f107l < this.f115r) {
            this.f104D = false;
        } else {
            this.f119J = ((this.f118I - 4) * this.f115r) / this.f107l;
            this.f104D = true;
        }
        if (this.f103i && this.f106k < 0 && c0032c.isEnabled()) {
            this.f106k = this.f108m.size() - 1;
        }
        return this;
    }

    /* renamed from: f */
    public final Screen m226f() {
        if (this.f115r - this.f107l > 5 && this.f102h == 0) {
            this.f96c = this.f112F + this.f107l + 4;
            m219s();
        }
        int i = 0;
        Enumeration enumerationElements = this.f108m.elements();
        while (enumerationElements.hasMoreElements()) {
            int i2 = i;
            int iM910f = ((MenuItem) enumerationElements.nextElement()).getMaxHeight();
            if (i2 < iM910f) {
                i = iM910f;
            }
        }
        int i3 = i + 16;
        if (i3 < this.f95b) {
            this.f95b = i3;
        }
        m219s();
        return this;
    }

    /* JADX WARN: Removed duplicated region for block: B:198:0x0363 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x0352  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0356  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void m227a(GraphicsContext c0012al, boolean z, boolean z2) {
        int iM417c;
        boolean z3 = false;
        if (this.f102h != 2) {
            int i = this.f98e;
            int i2 = this.f99f;
            int i3 = this.f95b;
            int i4 = this.f96c;
            int i5 = z ? 1 : 2;
            Graphics graphics = c0012al.graphics;
            c0012al.setColorFromPalette(i5);
            graphics.fillRect(i, i2, i3, i4);
            c0012al.setColorFromPalette(16);
            graphics.drawRect(i, i2, i3 - 1, i4 - 1);
            if (this.f101C != null) {
                int i6 = this.f98e + 1;
                int i7 = this.f99f + 1;
                c0012al.setClip(i6, i7, this.f111E, this.f112F);
                int iM586d = AppState.getInt(72);
                int iM586d2 = AppState.getInt(5042 + iM586d);
                if (iM586d2 != AppState.getInt(iM586d + 5082)) {
                    for (int i8 = 1; i8 < this.f112F; i8++) {
                        c0012al.setColor(((255 - ((i8 * (255 - (iM586d2 >> 16))) / this.f112F)) << 16) | ((255 - ((i8 * (255 - ((iM586d2 >> 8) & 255))) / this.f112F)) << 8) | (255 - ((i8 * (255 - (iM586d2 & 255))) / this.f112F)));
                        c0012al.drawRect(i6, i7 + i8, this.f111E, 0);
                    }
                } else {
                    c0012al.setColor(iM586d2);
                    c0012al.fillRect(i6, i7, this.f111E, this.f112F);
                }
                this.f101C.render(c0012al, i6, i7, 0);
            }
            int i9 = this.f98e + 2;
            int i10 = this.f99f + this.f113p;
            int i11 = this.f104D ? this.f114q : this.f114q + 2;
            int size = this.f108m.size();
            boolean z4 = this.f102h != 0;
            int i12 = this.f105j;
            for (int i13 = 0; i13 < size; i13++) {
                int iM241d = m241d(i13);
                if (iM241d - i12 > this.f115r) {
                    break;
                }
                MenuItem c0032cM239b = m239b(i13);
                int iM912h = c0032cM239b.getTotalHeight();
                if (iM241d + iM912h >= i12) {
                    int iM240c = m240c(i13);
                    int iM911g = c0032cM239b.getTotalWidth();
                    c0012al.setClip(i9, i10, i11, this.f115r);
                    int i14 = i9 + iM240c;
                    int i15 = (i10 + iM241d) - i12;
                    int i16 = this.f102h == 0 ? i11 : iM911g;
                    int i17 = c0032cM239b.id;
                    if (this.f103i && i13 == this.f106k && i17 != 11) {
                        c0012al.setColorFromPalette(13);
                        c0012al.fillRect(i14, i15, i16, iM912h);
                    }
                    if (i17 == 13 && c0032cM239b.visible) {
                        c0012al.setColorFromPalette(13);
                        c0012al.fillRect(i14, i15, i16, iM912h);
                    }
                    if (z4) {
                        int i18 = iM912h;
                        int i19 = i16;
                        int i20 = i15;
                        int i21 = i14;
                        int i22 = i21 + i19;
                        Graphics graphics2 = c0012al.graphics;
                        int clipX = graphics2.getClipX();
                        if (i22 >= clipX) {
                            int i23 = i20 + i18;
                            int clipY = graphics2.getClipY();
                            if (i23 >= clipY) {
                                int clipWidth = graphics2.getClipWidth();
                                if (i21 <= clipX + clipWidth) {
                                    int clipHeight = graphics2.getClipHeight();
                                    if (i20 > clipY + clipHeight) {
                                        z3 = false;
                                        if (!z3) {
                                            c0032cM239b.render(c0012al, i14, i15, i11);
                                        }
                                    } else {
                                        if (clipX + clipWidth < i21 + i19) {
                                            i19 = (clipX + clipWidth) - i21;
                                        }
                                        if (clipY + clipHeight < i20 + i18) {
                                            i18 = (clipY + clipHeight) - i20;
                                        }
                                        if (clipX > i21) {
                                            int i24 = clipX - i21;
                                            i21 = clipX;
                                            i19 -= i24;
                                        }
                                        if (i19 > 0) {
                                            if (clipY > i20) {
                                                int i25 = clipY - i20;
                                                i20 = clipY;
                                                i18 -= i25;
                                            }
                                            if (i18 > 0) {
                                                graphics2.setClip(i21, i20, i19, i18);
                                                z3 = true;
                                            }
                                            if (!z3) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.f104D) {
                int i26 = this.f98e + this.f116G;
                int i27 = this.f99f + this.f117H;
                c0012al.setClip(i26, i27, 7, this.f118I + 4);
                c0012al.setColorFromPalette(16);
                c0012al.fillRect(i26 + 1, i27 + (this.f107l == 0 ? 0 : Utils.min(((this.f118I - 4) * this.f105j) / this.f107l, (this.f118I - 4) - this.f119J)), 1, this.f119J + 2);
                c0012al.drawRect(i26, i27 - 1, 2, this.f118I + 1);
            }
            if (this.f110o != null) {
                int iM502a = Utils.max(AppState.getInt(1450), 16);
                int iM605e = AppState.getHeight() - 1;
                int iM586d3 = AppState.getInt(1528);
                c0012al.setClip(0, (iM605e - iM502a) - 3, iM586d3, iM502a + 4).setColorFromPalette(16).fillRect(0, (iM605e - iM502a) - 3, iM586d3, iM502a + 4).setColorFromPalette(17).fillRect(1, (iM605e - iM502a) - 2, iM586d3 - 2, iM502a + 2).setColorFromPalette(0).setFont(AppState.getGfxContext(0));
                Vector vector = this.f110o;
                int i28 = 3;
                boolean z5 = false;
                int iM73f = ((iM605e - iM502a) - 1) + ScreenManager.getCenterOffset();
                for (int i29 = 0; i29 < vector.size(); i29++) {
                    Object objElementAt = vector.elementAt(i29);
                    if (!(objElementAt instanceof Integer)) {
                        z5 = true;
                        c0012al.drawString((String) objElementAt, i28, (iM605e - iM502a) - 1, 20);
                        i28 = iM586d3;
                    } else if (z5) {
                        i28 -= 18;
                        c0012al.drawIcon(((Integer) objElementAt).intValue(), i28, iM73f);
                    } else {
                        c0012al.drawIcon(((Integer) objElementAt).intValue(), 3, iM73f);
                        i28 += 18;
                    }
                }
            }
            if (z && AppState.getBool(71)) {
                int iM586d4 = AppState.getInt(1528);
                int iM605e2 = AppState.getHeight();
                c0012al.setClip(0, 0, iM586d4, 2048 + iM605e2);
                c0012al.setFont(AppState.getGfxContext(0));
                c0012al.setColorFromPalette(15);
                if (this.f123v != null) {
                    c0012al.drawString(this.f123v, 1, iM605e2, 20);
                }
                if (this.f124w != null) {
                    c0012al.drawString(this.f124w, iM586d4 - 1, iM605e2, 24);
                }
                if (ResourceManager.clockWidth + this.f126K < iM586d4 - 6) {
                    c0012al.drawString(Utils.defaultStr(AppState.getString(1263)), iM586d4 >> 1, iM605e2, 17);
                }
            }
        }
        if (this.f97d == 1 || this.f97d == 12) {
            c0012al.setFont(AppState.getGfxContext(1));
            TabBar c0008ah = (TabBar) AppState.getVector(1246).elementAt(TabBar.currentIndex);
            Vector vectorM614m = AppState.getVector(1245);
            int size2 = vectorM614m.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                Object objElementAt2 = vectorM614m.elementAt(size2);
                if (objElementAt2 instanceof TabBar) {
                    TabBar c0008ah2 = (TabBar) objElementAt2;
                    boolean z6 = objElementAt2 == c0008ah && !TabBar.scrollEnabled;
                    GraphicsContext c0012alM207b = c0012al.setColorFromPalette(16);
                    int i30 = c0008ah2.xOffset;
                    int i31 = c0008ah2.width;
                    int iM623o = AppState.getIntOffset(1) + 7;
                    c0012alM207b.setClip(i30, 2, i31, iM623o - 2).drawLine(c0008ah2.xOffset, iM623o, c0008ah2.xOffset, 6).drawLine(c0008ah2.xOffset, 6, c0008ah2.xOffset + 4, 2).drawLine(c0008ah2.xOffset + 4, 2, (c0008ah2.xOffset + c0008ah2.width) - 2, 2).drawLine((c0008ah2.xOffset + c0008ah2.width) - 2, 2, (c0008ah2.xOffset + c0008ah2.width) - 2, iM623o).setColorFromPalette(z6 ? 1 : 17);
                    int i32 = z6 ? iM623o : iM623o - 1;
                    int i33 = 3;
                    while (i33 < i32) {
                        c0012al.drawLine(c0008ah2.xOffset + 1 + (i33 < 6 ? 6 - i33 : 0), i33, (c0008ah2.xOffset + c0008ah2.width) - 3, i33);
                        i33++;
                    }
                    if (c0008ah2.account == null) {
                        int i34 = c0008ah2.iconId;
                        iM417c = (i34 == 240 && AppController.m410N()) ? 16385 : (i34 == 240 || i34 == 264 || AppState.getVector(1243).size() <= 0) ? i34 : 16384;
                    } else {
                        iM417c = AppController.m417c(c0008ah2.account);
                    }
                    c0012al.drawIcon(iM417c, c0008ah2.xOffset + 4, 4 + ScreenManager.getCenterOffset()).setColorFromPalette(0).setClip(c0008ah2.xOffset, 2, c0008ah2.width - 3, iM623o - 2).drawString(c0008ah2.title, c0008ah2.xOffset + 6 + 16, 4, 20);
                } else {
                    int[] iArr = (int[]) objElementAt2;
                    int i35 = iArr[0];
                    int iM73f2 = 4 + ScreenManager.getCenterOffset();
                    c0012al.setClip(i35, iM73f2, 16, 16);
                    c0012al.drawIcon(iArr[1], i35, iM73f2);
                }
            }
        }
        if (this.f94a == 6) {
            int i36 = this.f98e + 2;
            int i37 = this.f99f + this.f113p;
            c0012al.setClip(i36, i37, this.f95b, this.f115r);
            try {
                int iM586d5 = AppState.getInt(1415);
                int iM586d6 = AppState.getInt(1416);
                Graphics graphics3 = c0012al.graphics;
                graphics3.drawImage(AppState.getImage(1364), iM586d5 >> 1, i37 + (iM586d6 >> 1), 3);
                if (!AppState.getBool(1414) && AppState.getBool(1535)) {
                    int[] iArr2 = new int[iM586d5];
                    int i38 = iM586d5;
                    while (true) {
                        i38--;
                        if (i38 < 0) {
                            break;
                        } else {
                            iArr2[i38] = 1006632960;
                        }
                    }
                    while (true) {
                        iM586d6--;
                        if (iM586d6 < 0) {
                            break;
                        } else {
                            graphics3.drawRGB(iArr2, 0, iM586d5, 0, i37 + iM586d6, iM586d5, 1, true);
                        }
                    }
                }
            } catch (Throwable unused) {
            }
            AppState.setInt(1553, 0);
            return;
        }
        if (this.f94a != 4) {
            return;
        }
        c0012al.setClip(this.f98e + 2, this.f99f + this.f113p, this.f95b, this.f115r);
        int iM586d7 = AppState.getInt(1408);
        if (iM586d7 <= 0) {
            return;
        }
        c0012al.setFont(AppState.getGfxContext(0));
        int iM605e3 = AppState.getHeight() - 1;
        int iM586d8 = AppState.getInt(1528);
        c0012al.setClip(0, (iM605e3 - iM586d7) - 1, iM586d8, iM586d7 + 1);
        c0012al.setColorFromPalette(16);
        c0012al.fillRect(0, (iM605e3 - iM586d7) - 1, iM586d8, iM586d7 + 1);
        c0012al.setClip(1, iM605e3 - iM586d7, iM586d8 - 2, iM586d7);
        c0012al.setColorFromPalette(1);
        c0012al.fillRect(0, 0, 2048, 2048);
        int iM502a2 = Utils.max(AppState.getInt(1450), 16);
        Vector vectorM614m2 = AppState.getVector(1247);
        int size3 = vectorM614m2.size();
        while (true) {
            size3--;
            if (size3 < 0) {
                return;
            }
            Account abstractC0037h = (Account) vectorM614m2.elementAt(size3);
            int i39 = iM605e3;
            int iM586d9 = AppState.getInt(1450);
            int iM502a3 = Utils.max(iM586d9, 16);
            c0012al.setColorFromPalette(13);
            int i40 = i39 - iM586d9;
            c0012al.fillRect(1, i40, ((AppState.getInt(1528) - 2) * abstractC0037h.msgCount) / 100, iM502a3);
            c0012al.drawIcon(abstractC0037h.getIconId(), 3, i40 + ScreenManager.getCenterOffset());
            c0012al.setColorFromPalette(0);
            c0012al.drawString(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(abstractC0037h.login).append(' ').append(abstractC0037h.msgCount).append('%')), 21, i39, 36);
            iM605e3 -= iM502a2;
        }
    }

    /* renamed from: g */
    public final boolean m228g() {
        int size = this.f108m.size();
        if (size == 0) {
            return true;
        }
        int i = this.f106k;
        int i2 = (i + 1) % size;
        return i2 <= i || m240c(i2) <= m240c(i);
    }

    /* renamed from: h */
    public final boolean m229h() {
        int size = this.f108m.size();
        if (size == 0) {
            return true;
        }
        int i = this.f106k;
        int i2 = ((i + size) - 1) % size;
        return i2 >= i || m240c(i2) >= m240c(i);
    }

    /* renamed from: i */
    public final void m230i() {
        MenuItem c0032cM222d = m222d();
        if (null != c0032cM222d && c0032cM222d.enabled) {
            IOUtils.postSelectEvent();
            return;
        }
        if (this.f94a == 6) {
            AppState.setInt(1564, 0);
            return;
        }
        if (this.f94a != 4) {
            if (this.f102h == 1) {
                this.f106k = (this.f106k + 1) % this.f108m.size();
                m235n();
                return;
            }
            return;
        }
        int size = this.f108m.size();
        if (size == 0) {
            return;
        }
        int i = this.f106k;
        int i2 = (i + 1) % size;
        if (i2 <= i || m240c(i2) <= m240c(i)) {
            return;
        }
        this.f106k = i2;
        m235n();
    }

    /* renamed from: j */
    public final int m231j() {
        if (this.f108m.size() == 0) {
            return 0;
        }
        if (this.f103i) {
            this.f105j -= this.f115r - 20;
            if (this.f105j < 0) {
                this.f105j = 0;
            }
            this.f106k = m244u();
            int iM912h = m239b(this.f106k).getTotalHeight();
            int iM241d = m241d(this.f106k);
            if (iM912h < this.f115r && this.f105j > iM241d) {
                this.f105j = iM241d;
            }
        } else {
            this.f105j -= this.f115r - 20;
            if (this.f105j < 0) {
                this.f105j = 0;
            }
        }
        m235n();
        return 0;
    }

    /* renamed from: k */
    public final int m232k() {
        int size = this.f108m.size();
        if (size == 0) {
            return 0;
        }
        m238t();
        this.f105j += this.f115r - 20;
        if (this.f103i) {
            MenuItem c0032cM239b = m239b(size - 1);
            int iM241d = m241d(size - 1);
            int iM912h = c0032cM239b.getTotalHeight();
            if (this.f105j > (iM241d + iM912h) - this.f115r) {
                this.f105j = (iM241d + iM912h) - this.f115r;
            }
            if (this.f105j < 0) {
                this.f105j = 0;
            }
            this.f106k = m238t();
            int iM912h2 = m239b(this.f106k).getTotalHeight();
            int iM241d2 = m241d(this.f106k);
            if (iM912h2 < this.f115r && this.f105j > iM241d2) {
                this.f105j = iM241d2;
            }
        } else {
            this.f105j = Utils.min(this.f107l - this.f115r, this.f105j);
            if (this.f107l < this.f115r) {
                this.f105j = 0;
            }
        }
        m235n();
        return 0;
    }

    /* renamed from: l */
    public final int m233l() {
        this.f105j = 0;
        this.f106k = 0;
        if (this.f103i) {
            while (!m239b(this.f106k).isEnabled()) {
                this.f106k++;
            }
        }
        m235n();
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:74:0x018c, code lost:
    
        r5.f106k = r10;
        m235n();
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0196, code lost:
    
        return;
     */
    /* renamed from: m */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void m234m() {
        boolean z;
        if (this.f94a == 6) {
            AppState.setInt(1564, 2);
            return;
        }
        if (this.f108m.size() == 0) {
            return;
        }
        if (!this.f103i) {
            if (this.f103i) {
                if (m243f(0)) {
                    return;
                }
                int iM238t = m238t();
                int iM244u = m244u();
                if (!m243f(iM244u)) {
                    iM244u++;
                }
                int i = iM244u;
                while (i > 0 && m243f(iM238t)) {
                    i--;
                    this.f105j = m241d(i);
                }
                return;
            }
            if (this.f97d != 9) {
                this.f105j -= 20;
                if (this.f105j < 0) {
                    this.f105j = 0;
                    return;
                }
                return;
            }
            int iM261v = m261v();
            if (iM261v < this.f108m.size()) {
                ((MenuItem) this.f108m.elementAt(iM261v)).visible = false;
                int iM262g = m262g(iM261v);
                if (iM262g < this.f108m.size()) {
                    ((MenuItem) this.f108m.elementAt(iM262g)).visible = true;
                    this.f128L = iM262g;
                } else {
                    this.f105j -= 20;
                    if (this.f105j < 0) {
                        this.f105j = 0;
                    }
                }
            } else if (this.f128L < this.f108m.size()) {
                int iM262g2 = m262g(this.f128L);
                if (m241d(this.f128L) > this.f105j && (m241d(this.f128L) + ((MenuItem) this.f108m.elementAt(this.f128L)).getTotalHeight()) - this.f105j <= this.f115r && this.f129M != 1) {
                    iM262g2 = this.f128L;
                }
                if (m241d(this.f128L) > this.f105j && (m241d(this.f128L) + ((MenuItem) this.f108m.elementAt(this.f128L)).getTotalHeight()) - this.f105j <= this.f115r && this.f129M == 1 && m241d(this.f128L) - this.f105j <= 20) {
                    iM262g2 = this.f128L;
                }
                if (iM262g2 < this.f108m.size()) {
                    ((MenuItem) this.f108m.elementAt(iM262g2)).visible = true;
                    this.f128L = iM262g2;
                } else {
                    this.f105j -= 20;
                    if (this.f105j < 0) {
                        this.f105j = 0;
                    }
                }
            } else {
                int iM264w = m264w();
                if (iM264w < this.f108m.size()) {
                    ((MenuItem) this.f108m.elementAt(iM264w)).visible = true;
                    this.f128L = iM264w;
                } else {
                    this.f105j -= 20;
                    if (this.f105j < 0) {
                        this.f105j = 0;
                    }
                }
            }
            this.f129M = 1;
            return;
        }
        int iM240c = m240c(this.f106k);
        int iM241d = m241d(this.f106k);
        if (this.f102h == 0) {
            if (iM241d < this.f105j) {
                this.f105j -= 20;
                return;
            }
            if (this.f106k == 0) {
                z = true;
            } else {
                int i2 = this.f106k;
                while (true) {
                    i2--;
                    if (i2 < 0) {
                        z = true;
                        break;
                    } else if (m239b(i2).isEnabled()) {
                        z = false;
                        break;
                    }
                }
            }
            if (!z) {
                int i3 = this.f106k;
                while (true) {
                    i3--;
                    if (i3 < 0) {
                        break;
                    } else if (m239b(i3).isEnabled()) {
                        this.f106k = i3;
                        break;
                    }
                }
                this.f105j = Utils.min(this.f105j, m241d(this.f106k));
                return;
            }
            if (this.f105j != 0) {
                this.f105j = 0;
                return;
            }
            if (this.f127y) {
                return;
            }
            if (this.f103i) {
                this.f106k = this.f108m.size() - 1;
                this.f105j = this.f107l - this.f115r;
                if (this.f105j < 0) {
                    this.f105j = 0;
                    return;
                }
                return;
            }
            if (this.f107l < this.f115r) {
                this.f105j = 0;
                return;
            } else if (((MenuItem) this.f108m.lastElement()).getTotalHeight() < this.f115r) {
                this.f105j = this.f107l - this.f115r;
                return;
            } else {
                int[] iArr = this.f109n;
                this.f105j = iArr[iArr[0]];
                return;
            }
        }
        int i4 = -1;
        int i5 = 0;
        int i6 = this.f106k;
        while (true) {
            i6--;
            if (i6 < 0) {
                this.f106k = this.f108m.size() - 1;
                m235n();
                return;
            }
            int iM241d2 = m241d(i6);
            if (iM241d2 != iM241d) {
                if (i4 == -1) {
                    i4 = i6;
                    i5 = iM241d2;
                } else if (iM241d2 < i5) {
                    this.f106k = i4;
                    m235n();
                    return;
                }
            }
            int iM240c2 = m240c(i6);
            if (iM240c2 == iM240c || (iM240c2 == 0 && iM241d2 != iM241d)) {
                break;
            }
        }
    }

    /* renamed from: n */
    public final void m235n() {
        if (!this.f103i || this.f108m.size() <= 0) {
            return;
        }
        int iM912h = m239b(this.f106k).getTotalHeight();
        int iM241d = m241d(this.f106k);
        if (iM241d < this.f105j) {
            this.f105j = iM241d;
        }
        if (this.f105j < (iM241d + iM912h) - this.f115r) {
            this.f105j = (iM241d + iM912h) - this.f115r;
        }
        if (this.f105j < 0) {
            this.f105j = 0;
        }
    }

    /* renamed from: o */
    public final Screen m236o() {
        this.f105j = Utils.max(0, this.f107l - this.f115r);
        int size = this.f108m.size();
        if (size > 1) {
            this.f105j = Utils.min(this.f105j, m241d(size - 2));
        }
        if (this.f97d == 9) {
            int iM264w = m264w();
            if (iM264w < this.f108m.size()) {
                ((MenuItem) this.f108m.elementAt(iM264w)).visible = true;
                this.f128L = iM264w;
            } else {
                this.f128L = 1000000;
            }
            this.f129M = 2;
        }
        return this;
    }

    /* renamed from: p */
    public final void m237p() {
        if (this.f94a == 6) {
            ConnectionThread.m1163c(this);
            return;
        }
        int size = this.f108m.size();
        if (size == 0) {
            return;
        }
        if (!this.f103i) {
            if (this.f97d != 9) {
                if (this.f105j + this.f115r < this.f107l) {
                    this.f105j += 20;
                    return;
                }
                return;
            }
            int iM261v = m261v();
            if (iM261v < this.f108m.size()) {
                ((MenuItem) this.f108m.elementAt(iM261v)).visible = false;
                int iM263h = m263h(iM261v);
                if (iM263h < this.f108m.size()) {
                    ((MenuItem) this.f108m.elementAt(iM263h)).visible = true;
                    this.f128L = iM263h;
                } else if (this.f105j + this.f115r < this.f107l) {
                    this.f105j += 20;
                }
            } else if (this.f128L < this.f108m.size()) {
                int iM263h2 = m263h(this.f128L);
                if (m241d(this.f128L) > this.f105j && (m241d(this.f128L) + ((MenuItem) this.f108m.elementAt(this.f128L)).getTotalHeight()) - this.f105j <= this.f115r && this.f129M != 2) {
                    iM263h2 = this.f128L;
                }
                if (m241d(this.f128L) > this.f105j && (m241d(this.f128L) + ((MenuItem) this.f108m.elementAt(this.f128L)).getTotalHeight()) - this.f105j <= this.f115r && this.f129M == 2 && this.f115r - ((m241d(this.f128L) + ((MenuItem) this.f108m.elementAt(this.f128L)).getTotalHeight()) - this.f105j) <= 20) {
                    iM263h2 = this.f128L;
                }
                if (iM263h2 < this.f108m.size()) {
                    ((MenuItem) this.f108m.elementAt(iM263h2)).visible = true;
                    this.f128L = iM263h2;
                } else if (this.f105j + this.f115r < this.f107l) {
                    this.f105j += 20;
                }
            } else {
                int iM264w = m264w();
                if (iM264w < this.f108m.size()) {
                    ((MenuItem) this.f108m.elementAt(iM264w)).visible = true;
                    this.f128L = iM264w;
                } else if (this.f105j + this.f115r < this.f107l) {
                    this.f105j += 20;
                }
            }
            this.f129M = 2;
            return;
        }
        if (this.f102h != 0) {
            int iM240c = m240c(this.f106k);
            int iM241d = m241d(this.f106k);
            int i = -1;
            int i2 = 0;
            int i3 = this.f106k;
            do {
                i3++;
                if (i3 >= size) {
                    this.f106k = 0;
                    m235n();
                    return;
                }
                int iM241d2 = m241d(i3);
                if (iM241d2 != iM241d) {
                    if (i == -1) {
                        i = i3;
                        i2 = iM241d2;
                    } else if (iM241d2 > i2) {
                        this.f106k = i;
                        m235n();
                        return;
                    }
                }
            } while (m240c(i3) != iM240c);
            this.f106k = i3;
            m235n();
            return;
        }
        if (this.f106k >= size - 1) {
            int i4 = this.f105j;
            this.f105j += 20;
            int iM912h = m239b(this.f106k).getTotalHeight();
            int iM241d3 = m241d(this.f106k);
            if (this.f105j > (iM241d3 + iM912h) - this.f115r) {
                this.f105j -= this.f105j - ((iM241d3 + iM912h) - this.f115r);
            }
            if (this.f105j < 0) {
                this.f105j = 0;
            }
            if (this.f105j != i4 || this.f127y) {
                return;
            }
            this.f105j = 0;
            this.f106k = 0;
            if (this.f103i) {
                while (!m239b(this.f106k).isEnabled()) {
                    this.f106k++;
                }
                return;
            }
            return;
        }
        MenuItem c0032c = null;
        int i5 = this.f106k;
        while (true) {
            i5++;
            if (i5 > size) {
                break;
            }
            if (i5 == size) {
                return;
            }
            MenuItem c0032cM239b = m239b(i5);
            c0032c = c0032cM239b;
            if (c0032cM239b.isEnabled()) {
                this.f106k = i5;
                break;
            }
        }
        int iM912h2 = c0032c.getTotalHeight();
        int iM241d4 = m241d(this.f106k);
        if (iM241d4 + iM912h2 >= this.f105j + this.f115r) {
            if (iM912h2 <= this.f115r) {
                this.f105j = (iM241d4 + iM912h2) - this.f115r;
            } else {
                this.f105j += 20;
            }
        }
    }

    /* renamed from: t */
    private final int m238t() {
        int i;
        int i2 = this.f103i ? this.f106k : 0;
        int size = this.f108m.size() - 1;
        int i3 = i2;
        int i4 = -1;
        while (true) {
            int i5 = (i3 + size) >> 1;
            i = i5;
            if (i5 == i4) {
                break;
            }
            i4 = i;
            if (m241d(i) > this.f105j + this.f115r) {
                size = i - 1;
            } else {
                i3 = i;
            }
        }
        if (i < this.f108m.size() - 1 && m242e(i + 1)) {
            i++;
        }
        int i6 = i;
        if (!m243f(i6) && !m239b(i6).isEnabled()) {
            i6--;
        }
        return i6;
    }

    /* renamed from: b */
    private final MenuItem m239b(int i) {
        if (i >= 0) {
            return (MenuItem) this.f108m.elementAt(i);
        }
        return null;
    }

    /* renamed from: c */
    private final int m240c(int i) {
        return this.f109n[(i << 1) + 1];
    }

    /* renamed from: d */
    private final int m241d(int i) {
        return this.f109n[(i << 1) + 1 + 1];
    }

    /* renamed from: e */
    private final boolean m242e(int i) {
        int iM241d = m241d(i);
        return iM241d < this.f105j + this.f115r && iM241d + m239b(i).getTotalHeight() > this.f105j;
    }

    /* renamed from: f */
    private final boolean m243f(int i) {
        int iM241d = m241d(i);
        return iM241d >= this.f105j && (iM241d + m239b(i).getTotalHeight()) - 1 <= this.f105j + this.f115r;
    }

    /* renamed from: u */
    private final int m244u() {
        int i;
        int size = this.f103i ? this.f106k : this.f108m.size() - 1;
        int i2 = 0;
        int i3 = -1;
        while (true) {
            int i4 = (i2 + size) >> 1;
            i = i4;
            if (i4 == i3) {
                break;
            }
            i3 = i;
            if (m241d(i) + m239b(i).getTotalHeight() < this.f105j) {
                i2 = i + 1;
            } else {
                size = i;
            }
        }
        if (!m242e(i)) {
            i++;
        } else if (i > 0 && m242e(i - 1)) {
            i--;
        }
        return i;
    }

    /* renamed from: a */
    public final Screen m245a(String str, String str2, int i) {
        return m256a(-1, str, str2, i, (Object) null);
    }

    /* renamed from: a */
    public final Screen m246a(int i, String str, int i2) {
        return m256a(i, (String) null, str, i2, (Object) null);
    }

    /* renamed from: a */
    public final Screen m247a(int i, String str, int i2, Object obj) {
        return m256a(i, (String) null, str, i2, obj);
    }

    /* renamed from: a */
    public final Screen m248a(String str, String str2) {
        return m245a(str, str2, 200);
    }

    /* renamed from: a */
    public final Screen m249a(int i, int i2, int i3) {
        return m246a(i, AppState.getString(i2), i3);
    }

    /* renamed from: b */
    public final Screen m250b(int i, String str, int i2, Object obj) {
        MenuItem c0032cM901a = new MenuItem(13, AppState.emptyStr).setIcon(i).addText(str, 5, i2);
        c0032cM901a.data = obj;
        return m225a(c0032cM901a);
    }

    /* renamed from: a */
    public final Screen m251a(String str, int i) {
        return m225a(MenuItem.createSeparator().addText(str, 1, i));
    }

    /* renamed from: b */
    public final Screen m252b(int i, int i2, int i3) {
        String strM584b = AppState.getString(i2);
        MenuItem c0032cM896a = MenuItem.createWithWidth(strM584b, i3).setIcon(i).setLabel(strM584b).setIcon(244);
        c0032cM896a.enabled = true;
        return m225a(c0032cM896a);
    }

    /* renamed from: a */
    public final Screen m253a(String str) {
        return m246a(-1, str, 200);
    }

    /* renamed from: b */
    public final Screen m254b(int i, String str, int i2) {
        return m225a(MenuItem.createWithWidth(str, i2).setIcon(i));
    }

    /* renamed from: a */
    public final Screen m255a(int i) {
        return m225a(MenuItem.createSeparator().setLabel(AppState.getString(i)));
    }

    /* renamed from: a */
    public final Screen m256a(int i, String str, String str2, int i2, Object obj) {
        MenuItem c0032cM888a = MenuItem.createWithWidth(str2, i2);
        if (i >= 0) {
            c0032cM888a.setIcon(i);
        }
        if (str != null) {
            c0032cM888a.addText(str, 0, 6);
        }
        if (str2 != null) {
            int iIndexOf = str2.indexOf(0);
            c0032cM888a.setLabel(iIndexOf < 0 ? str2 : StringUtils.prefix(str2, iIndexOf));
        }
        c0032cM888a.data = obj;
        return m225a(c0032cM888a);
    }

    /* renamed from: b */
    public final Screen m257b(String str) {
        if (str != null) {
            int i = 0;
            Enumeration enumerationElements = this.f108m.elements();
            while (true) {
                if (!enumerationElements.hasMoreElements()) {
                    break;
                }
                if (str.equals(((MenuItem) enumerationElements.nextElement()).title)) {
                    this.f106k = i;
                    break;
                }
                i++;
            }
        }
        return this;
    }

    /* renamed from: q */
    public final void m258q() {
        Vector vectorM1213g = NetworkUtils.newVector();
        int size = this.f108m.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else {
                vectorM1213g.addElement(this.f108m.elementAt(size));
            }
        }
        this.f108m.removeAllElements();
        this.f109n[0] = 0;
        this.f107l = 0;
        int size2 = vectorM1213g.size();
        while (true) {
            size2--;
            if (size2 < 0) {
                NetworkUtils.releaseVector(vectorM1213g);
                return;
            }
            m225a((MenuItem) vectorM1213g.elementAt(size2));
        }
    }

    /* renamed from: a */
    public final Screen m259a(String str, String str2, int i, int i2, int i3) {
        GraphicsContext c0012alM608k = AppState.getGfxContext(0);
        this.f123v = str;
        int iM214a = c0012alM608k.stringWidth(str);
        this.f124w = str2;
        this.f126K = Utils.max(iM214a, c0012alM608k.stringWidth(str2)) << 1;
        this.f120s = i;
        this.f121t = i2;
        this.f122u = i3;
        return this;
    }

    /* renamed from: a */
    public final boolean m260a(int i, int i2, int i3, int i4, boolean z) {
        int iM240c;
        int iM241d;
        boolean z2;
        int iM240c2;
        int iM241d2;
        if (!this.f132B) {
            return true;
        }
        AppController.f153g = true;
        this.f132B = false;
        int i5 = i - this.f98e;
        int i6 = i2 - this.f99f;
        int i7 = i3 - this.f98e;
        int i8 = i4 - this.f99f;
        if (this.f94a == 6) {
            ConnectionThread.m1161a(this);
            MapRenderer.onTap(i5, i6 - this.f113p);
            return true;
        }
        if (this.f103i) {
            int i9 = (z ? i7 : i5) - 2;
            int i10 = ((z ? i8 : i6) + this.f105j) - this.f113p;
            int size = this.f108m.size();
            int i11 = this.f104D ? this.f114q : this.f114q + 2;
            int i12 = 0;
            while (true) {
                if (i12 >= size) {
                    z2 = false;
                    break;
                }
                MenuItem c0032cM239b = m239b(i12);
                if (c0032cM239b.isEnabled() && i9 > (iM240c2 = m240c(i12)) && i10 > (iM241d2 = m241d(i12))) {
                    if (i9 < iM240c2 + (this.f102h == 0 ? i11 : c0032cM239b.getTotalWidth()) && i10 < iM241d2 + c0032cM239b.getTotalHeight()) {
                        if (this.f106k != i12 || z) {
                            this.f106k = i12;
                        } else {
                            AppController.m455ab();
                        }
                        z2 = true;
                    }
                }
                i12++;
            }
            if (z2) {
                return true;
            }
        }
        if (z) {
            return false;
        }
        if (this.f97d != 9) {
            if (this.f97d == 0 || this.f97d == 1) {
                return true;
            }
            AppController.m455ab();
            return true;
        }
        int i13 = i5 - 2;
        int i14 = (i6 + this.f105j) - this.f113p;
        int i15 = this.f104D ? this.f114q : this.f114q + 2;
        int iM261v = m261v();
        if (iM261v < this.f108m.size()) {
            ((MenuItem) this.f108m.elementAt(iM261v)).visible = false;
        }
        int size2 = this.f108m.size();
        while (true) {
            size2--;
            if (size2 >= 0) {
                MenuItem c0032cM239b2 = m239b(size2);
                if (c0032cM239b2.id == 13 && i13 > (iM240c = m240c(size2)) && i14 > (iM241d = m241d(size2)) && i13 < iM240c + i15 && i14 < iM241d + c0032cM239b2.getTotalHeight()) {
                    c0032cM239b2.visible = true;
                    break;
                }
            } else {
                break;
            }
        }
        AppController.m455ab();
        return true;
    }

    /* renamed from: v */
    private int m261v() {
        int size = this.f108m.size() + 1;
        for (int i = 0; i < this.f108m.size(); i++) {
            MenuItem c0032c = (MenuItem) this.f108m.elementAt(i);
            if (c0032c.id == 13 && c0032c.visible && m241d(i) > this.f105j && (m241d(i) + c0032c.getTotalHeight()) - this.f105j <= this.f115r) {
                size = i;
            }
        }
        return size;
    }

    /* renamed from: g */
    private int m262g(int i) {
        int size = this.f108m.size() + 1;
        for (int i2 = 0; i2 < this.f108m.size(); i2++) {
            MenuItem c0032c = (MenuItem) this.f108m.elementAt(i2);
            if (c0032c.id == 13 && !c0032c.visible && m241d(i2) > this.f105j && (m241d(i2) + c0032c.getTotalHeight()) - this.f105j <= this.f115r && m241d(i2) < m241d(i)) {
                size = i2;
            }
        }
        return size;
    }

    /* renamed from: h */
    private int m263h(int i) {
        int size = this.f108m.size() + 1;
        int i2 = 0;
        while (true) {
            if (i2 < this.f108m.size()) {
                MenuItem c0032c = (MenuItem) this.f108m.elementAt(i2);
                if (c0032c.id == 13 && !c0032c.visible && m241d(i2) > this.f105j && (m241d(i2) + c0032c.getTotalHeight()) - this.f105j <= this.f115r && m241d(i2) > m241d(i)) {
                    size = i2;
                    break;
                }
                i2++;
            } else {
                break;
            }
        }
        return size;
    }

    /* renamed from: w */
    private int m264w() {
        int size = this.f108m.size() + 1;
        for (int i = 0; i < this.f108m.size(); i++) {
            MenuItem c0032c = (MenuItem) this.f108m.elementAt(i);
            if (c0032c.id == 13 && !c0032c.visible && m241d(i) > this.f105j && (m241d(i) + c0032c.getTotalHeight()) - this.f105j <= this.f115r) {
                size = i;
            }
        }
        return size;
    }

    /* renamed from: r */
    public final int m265r() {
        int iM241d = 0;
        if (this.f106k > 0) {
            iM241d = m241d(this.f106k);
        }
        return this.f99f + iM241d;
    }

    /* renamed from: a */
    public final Screen m266a(int i, int i2) {
        this.f98e = i;
        this.f99f = i2;
        return this;
    }
}
