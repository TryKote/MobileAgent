package p000;

import java.util.Vector;

/* renamed from: c */
/* loaded from: MobileAgent_3.9.jar:c.class */
public final class MenuItem {

    /* renamed from: a */
    public final int id;

    /* renamed from: b */
    public String title;

    /* renamed from: c */
    public int width;

    /* renamed from: g */
    private int totalWidth;

    /* renamed from: h */
    private int maxHeight;

    /* renamed from: i */
    private Vector elements;

    /* renamed from: j */
    private int[] positions;

    /* renamed from: d */
    public Object data;

    /* renamed from: e */
    public boolean enabled;

    /* renamed from: f */
    public boolean visible;

    /* renamed from: k */
    private int wrapWidth;

    public MenuItem(int i, String str) {
        this.id = i;
        this.elements = NetworkUtils.newVector();
        this.positions = new int[16];
        this.title = str;
        this.width = 200;
    }

    private MenuItem(String str, int i) {
        this(1, str);
        this.width = i;
    }

    /* renamed from: a */
    public final MenuItem clear() {
        this.elements.removeAllElements();
        this.positions[0] = 0;
        this.totalWidth = 0;
        this.maxHeight = 0;
        return this;
    }

    /* renamed from: b */
    public final boolean isEnabled() {
        return this.id != 0;
    }

    /* renamed from: c */
    public static final MenuItem createDefault() {
        return new MenuItem(1, AppState.getString(1038));
    }

    /* renamed from: a */
    public static final MenuItem create(String str) {
        return new MenuItem(1, str);
    }

    /* renamed from: a */
    public static final MenuItem createWithWidth(String str, int i) {
        return new MenuItem(str, i);
    }

    /* renamed from: d */
    public static final MenuItem createSeparator() {
        return new MenuItem(0, AppState.emptyStr);
    }

    /* renamed from: a */
    public static final MenuItem createCheckbox(String str, boolean z) {
        MenuItem c0032cM899a = new MenuItem(2, str).setIconAndLabel(z ? 25 : 24, str);
        c0032cM899a.data = ResourceManager.booleanOf(z);
        return c0032cM899a;
    }

    /* renamed from: a */
    public final MenuItem setAction(Object obj, String str, Object obj2, Object obj3, Object obj4) {
        String str2 = Utils.nonEmpty(str) ? str : null;
        if (obj instanceof String) {
            setLabel(Utils.m527g(this.title));
        } else {
            setIcon(((Integer) obj).intValue());
        }
        if (str2 != null) {
            addText(((Integer) obj3).intValue() != 327680 ? str2 : Utils.m506c(str2), 1, 7);
        } else {
            setDefaultFont();
        }
        this.data = new Object[]{str, obj2, obj3, obj4, obj};
        return this;
    }

    /* renamed from: a */
    public final MenuItem setChoices(Vector vector, int i, String str) {
        int size = vector.size();
        int i2 = size;
        String[] strArr = new String[size];
        while (true) {
            i2--;
            if (i2 < 0) {
                NetworkUtils.releaseVector(vector);
                MenuItem c0032cM896a = clear().setLabel(Utils.m527g(str)).addText(strArr[i], 1, 7).setIcon(247);
                c0032cM896a.data = new Object[]{ResourceManager.integerOf(i), strArr};
                return c0032cM896a;
            }
            strArr[i2] = (String) vector.elementAt(i2);
        }
    }

    /* renamed from: a */
    public static final MenuItem createGraphics(GraphicsContext c0012al) {
        MenuItem c0032c = new MenuItem(11, AppState.emptyStr);
        c0032c.data = c0012al;
        c0032c.totalWidth = c0012al.image.getWidth();
        c0032c.maxHeight = c0012al.image.getHeight() + 5;
        return c0032c;
    }

    /* renamed from: a */
    public final int execute(Screen c0013am) {
        if (this.id == 2) {
            if (this.data != null) {
                Boolean boolM968a = ResourceManager.booleanOf(!((Boolean) this.data).booleanValue());
                this.data = boolM968a;
                this.elements.setElementAt(createIconData(boolM968a.booleanValue() ? 25 : 24), 0);
            }
            IOUtils.postEvent(this);
            return 0;
        }
        if (this.id == 15) {
            new AsyncTask(c0013am, this);
            return 0;
        }
        if (this.id != 9) {
            if (this.id == 4) {
                AppController.m340m(Utils.defaultStr(AppState.getString(1379)).length() > 0 ? 427 : 428);
                return 0;
            }
            if (this.id != 5) {
                return -1;
            }
            AppController.m340m(429);
            return 0;
        }
        Screen c0013amM75b = ScreenManager.createScreen(2351);
        Object[] objArr = (Object[]) this.data;
        String[] strArr = (String[]) objArr[1];
        int iIntValue = ((Integer) objArr[0]).intValue();
        Object[] objArr2 = {objArr, this, c0013am};
        for (String str : strArr) {
            MenuItem c0032cM898b = new MenuItem(13, str).setLabel(str);
            c0032cM898b.data = objArr2;
            c0013amM75b.m225a(c0032cM898b);
        }
        c0013amM75b.m257b(strArr[iIntValue]);
        ScreenManager.showScreen(c0013amM75b);
        return 0;
    }

    /* renamed from: e */
    public final MenuItem setDefaultFont() {
        return addElement(new int[]{16, AppState.getInt(1450)});
    }

    /* renamed from: a */
    public final MenuItem setIcon(int i) {
        return i >= 0 ? addElement(createIconData(i)) : this;
    }

    /* renamed from: c */
    private static Object createIconData(int i) {
        return new int[]{GraphicsContext.getIconSize(i), 16, i};
    }

    /* renamed from: b */
    public final MenuItem setLabel(String str) {
        return setLabelInternal(-1, str, 0, 0);
    }

    /* renamed from: a */
    public final MenuItem setIconAndLabel(int i, String str) {
        return setLabelInternal(i, str, 0, 0);
    }

    /* renamed from: a */
    public final MenuItem setLabelInternal(int i, String str, int i2, int i3) {
        if (i >= 0) {
            setIcon(i);
        }
        return addText(str, i2, i3);
    }

    /* renamed from: a */
    public final MenuItem addText(String str, int i, int i2) {
        return addTextInternal(str, i, i2, -1);
    }

    /* renamed from: a */
    public final MenuItem addTextInternal(String str, int i, int i2, int i3) {
        if (str != null) {
            Vector vectorM907a = wrapText(NetworkUtils.newVector(), str, 0, str.length(), i, i2, i3);
            int size = vectorM907a.size();
            for (int i4 = 0; i4 < size; i4++) {
                addElement(vectorM907a.elementAt(i4));
            }
            NetworkUtils.releaseVector(vectorM907a);
        }
        return this;
    }

    /* renamed from: a */
    private MenuItem addElement(Object obj) {
        this.elements.addElement(obj);
        this.positions = AppController.m302a(this.positions, this.totalWidth, 0);
        this.totalWidth += getElementWidth(obj);
        int i = this.maxHeight;
        int iM905c = getElementHeight(obj);
        if (i < iM905c) {
            this.maxHeight = iM905c;
        }
        return this;
    }

    /* renamed from: b */
    private static int getElementWidth(Object obj) {
        if (obj == AppState.pool[1370]) {
            return 0;
        }
        return obj instanceof int[] ? ((int[]) obj)[0] + 2 : ((int[]) ((Object[]) obj)[1])[0];
    }

    /* renamed from: c */
    private static int getElementHeight(Object obj) {
        if (obj == AppState.pool[1370]) {
            return 0;
        }
        return obj instanceof int[] ? ((int[]) obj)[1] : ((int[]) ((Object[]) obj)[1])[1];
    }

    /* renamed from: a */
    private final void wrapTextLine(Vector vector, String str, GraphicsContext c0012al, int i, int i2, int i3, int i4, int i5) {
        int iM215a = c0012al.substringWidth(str, i2, i3);
        if (iM215a < (AppState.getInt(1528) << 2) / 5) {
            vector.addElement(new Object[]{str, new int[]{iM215a, i, i2, i3, i4, i5}});
            return;
        }
        int i6 = 0;
        while (true) {
            if (i6 >= i3 - 1) {
                break;
            }
            char cCharAt = str.charAt(i2 + i6);
            if ((cCharAt >= ' ' && cCharAt <= '/') || (cCharAt >= ':' && cCharAt <= '@') || ((cCharAt >= '[' && cCharAt <= '`') || (cCharAt >= '{' && cCharAt <= '~'))) {
                wrapTextLine(vector, str, c0012al, i, i2, i6 + 1, i4, i5);
                wrapTextLine(vector, str, c0012al, i, i2 + i6 + 1, (i3 - i6) - 1, i4, i5);
                break;
            }
            i6++;
        }
        if (i6 == i3 - 1) {
            int i7 = i3 >> 1;
            wrapTextLine(vector, str, c0012al, i, i2, i7, i4, i5);
            wrapTextLine(vector, str, c0012al, i, i2 + i7, i3 - i7, i4, i5);
        }
    }

    /* renamed from: a */
    private final Vector wrapText(Vector vector, String str, int i, int i2, int i3, int i4, int i5) {
        int iIndexOf;
        int iIndexOf2;
        if (i < i2) {
            if (i5 == 1) {
                int i6 = -1;
                int i7 = -1;
                for (int i8 = 0; i8 < 43; i8++) {
                    String strM584b = AppState.getString(i8 + 1141);
                    if (null != strM584b && (iIndexOf2 = str.indexOf(strM584b, i)) >= 0 && (iIndexOf2 < i6 || i6 == -1)) {
                        i6 = iIndexOf2;
                        i7 = i8;
                    }
                }
                if (i6 < 0 || i6 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, -1);
                } else {
                    wrapText(vector, str, i, i6, i3, i4, -1);
                    vector.addElement(new int[]{16, 16, i7 + 110});
                    wrapText(vector, str, i6 + AppState.getString(i7 + 1141).length(), i2, i3, i4, 1);
                }
            } else if (i5 == 2 || i5 == 3) {
                int i9 = -1;
                int i10 = -1;
                for (int i11 = 0; i11 < 37; i11++) {
                    String strM584b2 = AppState.getString(i11 + 1184);
                    if (null != strM584b2 && (iIndexOf = str.indexOf(strM584b2, i)) >= 0 && (iIndexOf < i9 || i9 == -1)) {
                        i9 = iIndexOf;
                        i10 = i11;
                    }
                }
                if (i9 < 0 || i9 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, -1);
                } else {
                    wrapText(vector, str, i, i9, i3, i4, -1);
                    vector.addElement(new int[]{16, 16, i10 + 318});
                    wrapText(vector, str, i9 + AppState.getString(i10 + 1184).length(), i2, i3, i4, 2);
                }
            } else if (i5 == 0) {
                int i12 = -1;
                int i13 = -1;
                for (int i14 = 0; i14 < 78; i14++) {
                    int iIndexOf3 = str.indexOf(AppState.getString(i14 + 1063), i);
                    if (iIndexOf3 >= 0 && (iIndexOf3 < i12 || i12 == -1)) {
                        i12 = iIndexOf3;
                        i13 = i14;
                    }
                }
                if (i12 < 0 || i12 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, -1);
                } else {
                    wrapText(vector, str, i, i12, i3, i4, -1);
                    int[] iArr = new int[3];
                    iArr[0] = 16;
                    iArr[1] = 16;
                    iArr[2] = i13 < 74 ? i13 + 36 : i13 == 74 ? 142 : i13 == 75 ? 137 : i13 == 76 ? 210 : 205;
                    vector.addElement(iArr);
                    wrapText(vector, str, i12 + AppState.getString(i13 + 1063).length(), i2, i3, i4, 0);
                }
            } else if (str != AppState.getString(1037)) {
                GraphicsContext c0012alM608k = AppState.getGfxContext(i3);
                int iM623o = AppState.getIntOffset(i3);
                int i15 = i;
                int i16 = i;
                while (true) {
                    if (i16 > i2) {
                        break;
                    }
                    if (i16 == i2) {
                        int i17 = i16 - i15;
                        if (i17 > 0) {
                            wrapTextLine(vector, str, c0012alM608k, iM623o, i15, i17, i3, i4);
                        }
                    } else {
                        char cCharAt = str.charAt(i16);
                        if (cCharAt == ' ') {
                            int i18 = (i16 - i15) + 1;
                            if (i18 > 1) {
                                wrapTextLine(vector, str, c0012alM608k, iM623o, i15, i18, i3, i4);
                            }
                            i15 = i16 + 1;
                        } else if (cCharAt == '\r' || cCharAt == '\n') {
                            int i19 = i16 - i15;
                            if (i19 > 0) {
                                wrapTextLine(vector, str, c0012alM608k, iM623o, i15, i19, i3, i4);
                            }
                            vector.addElement(AppState.pool[1370]);
                            i15 = i16 + 1;
                        }
                        i16++;
                    }
                }
            } else {
                int length = str.length();
                vector.addElement(new Object[]{str, new int[]{AppState.getGfxContext(i3).substringWidth(str, 0, length), AppState.getIntOffset(i3), 0, length, i3, i4}});
            }
        }
        return vector;
    }

    /* renamed from: a */
    public final MenuItem setLayout(int i, int i2) {
        if (i > 1) {
            this.wrapWidth = i2;
        }
        return this;
    }

    /* renamed from: b */
    public final MenuItem layout(int i) {
        if (this.id == 11) {
            return this;
        }
        this.positions[0] = 0;
        Vector vector = this.elements;
        int size = vector.size();
        int i2 = 0;
        int i3 = 0;
        int iM502a = 0;
        this.maxHeight = 0;
        for (int i4 = 0; i4 < size; i4++) {
            Object objElementAt = vector.elementAt(i4);
            if (objElementAt != AppState.pool[1370]) {
                int iM904b = getElementWidth(objElementAt);
                int iM905c = getElementHeight(objElementAt);
                int i5 = 0;
                if (i4 == size - 2 && (vector.elementAt(i4 + 1) instanceof int[])) {
                    int[] iArr = (int[]) vector.elementAt(i4 + 1);
                    if (iArr.length == 3 && iArr[2] == 244) {
                        i5 = 16;
                    }
                }
                if (this.wrapWidth == 0 && i2 + iM904b + i5 > i) {
                    i2 = 0;
                    i3 += iM502a;
                    this.maxHeight += iM502a;
                    iM502a = 0;
                }
                this.positions = AppController.m302a(this.positions, i2, i3);
                iM502a = Utils.max(iM502a, iM905c);
                i2 += iM904b;
            } else {
                i2 = 0;
                i3 += iM502a;
                this.maxHeight += iM502a;
                iM502a = 0;
                this.positions = AppController.m302a(this.positions, 0, 0);
            }
        }
        this.maxHeight += iM502a;
        return this;
    }

    /* renamed from: f */
    public final int getMaxHeight() {
        int iM502a = 0;
        Vector vector = this.elements;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return iM502a + 4;
            }
            iM502a = Utils.max(iM502a, this.positions[(size << 1) + 1] + getElementWidth(vector.elementAt(size)));
        }
    }

    /* renamed from: g */
    public final int getTotalWidth() {
        return this.wrapWidth != 0 ? this.wrapWidth : this.totalWidth + 4;
    }

    /* renamed from: h */
    public final int getTotalHeight() {
        return Utils.max(this.maxHeight, AppState.getInt(1450)) + 4;
    }

    /* renamed from: a */
    public final void render(GraphicsContext c0012al, int i, int i2, int i3) {
        if (this.id == 11) {
            c0012al.graphics.drawImage(((GraphicsContext) this.data).image, i, i2, 20);
            return;
        }
        Vector vector = this.elements;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            Object objElementAt = vector.elementAt(size);
            if (objElementAt != AppState.pool[1370]) {
                int i4 = i + 2;
                int i5 = this.positions[(size << 1) + 1];
                int i6 = i2 + 2 + this.positions[(size << 1) + 1 + 1];
                int i7 = i4 + i5;
                if (objElementAt instanceof int[]) {
                    int[] iArr = (int[]) objElementAt;
                    if (iArr.length == 3) {
                        int i8 = iArr[2];
                        c0012al.drawIcon(i8, i8 != 244 ? i7 : (i4 + i3) - 13, i6 + ScreenManager.getCenterOffset());
                    } else if (iArr.length == 2) {
                        c0012al.setColorFromPalette(18).drawRect(i7, i6, i3 - i7, iArr[1]);
                    }
                } else {
                    String str = (String) ((Object[]) objElementAt)[0];
                    int[] iArr2 = (int[]) ((Object[]) objElementAt)[1];
                    int i9 = iArr2[4];
                    GraphicsContext c0012alM608k = AppState.getGfxContext(i9);
                    GraphicsContext c0012alM207b = c0012al.setFont(c0012alM608k).setColorFromPalette(iArr2[5]);
                    int i10 = iArr2[2];
                    int i11 = iArr2[3];
                    if (i6 > 0 && i6 < AppState.getInt(1529)) {
                        c0012alM207b.graphics.drawSubstring(str, i10, i11, i7, i6, 20);
                    }
                    if (i9 == 3) {
                        c0012al.drawRect(i7, i6 + (AppState.getInt(1450) >> 1), c0012alM608k.substringWidth(str, iArr2[2], iArr2[3]), 0);
                    } else if (i9 == 5) {
                        c0012al.drawRect(i7, i6 + AppState.getInt(1450), c0012alM608k.substringWidth(str, iArr2[2], iArr2[3]), 0);
                    }
                }
            }
        }
    }
}
