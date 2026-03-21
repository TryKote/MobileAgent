package p000;

import java.util.Vector;

/* renamed from: ah */
/* loaded from: MobileAgent_3.9.jar:ah.class */
public final class TabBar {

    /* renamed from: a */
    public static boolean f44a;

    /* renamed from: b */
    public static int f45b;

    /* renamed from: c */
    public String f46c;

    /* renamed from: d */
    public int f47d;

    /* renamed from: e */
    public int f48e;

    /* renamed from: f */
    public int f49f;

    /* renamed from: g */
    public int f50g;

    /* renamed from: h */
    public final Account f51h;

    /* renamed from: i */
    public String f52i;

    /* renamed from: j */
    public int f53j;

    /* renamed from: k */
    public static Account f54k;

    private TabBar(int i, String str, int i2, Account abstractC0037h) {
        this.f47d = i;
        this.f46c = str;
        this.f50g = i2;
        this.f48e = GraphicsContext.m217c(i) + AppState.getGfxContext(1).m214a(str);
        this.f51h = abstractC0037h;
    }

    /* renamed from: a */
    public static final void m163a() {
        f45b = 0;
        f54k = null;
        AppState.pool[1246] = NetworkUtils.newVector();
        Vector vectorM614m = AppState.getVector(1241);
        int size = vectorM614m.size();
        if (size == 0 || !AppState.getBool(243)) {
            m165a(156, AppState.getString(1047), 4, null);
        } else {
            for (int i = 0; i < size; i++) {
                Account abstractC0037h = (Account) vectorM614m.elementAt(i);
                m165a(abstractC0037h.getIconId(), abstractC0037h.shortName, 4, abstractC0037h);
                if (i == 0) {
                    f54k = abstractC0037h;
                }
            }
        }
        if (AppState.getBool(67)) {
            m165a(240, AppState.getString(1044), 36, null);
        }
        if (AppState.getBool(68)) {
            m165a(264, AppState.getString(1045), 6, null);
        }
        m178j();
        AppController.f153g = true;
    }

    /* renamed from: a */
    public static final void m164a(int i, String str) {
        Vector vectorM614m = AppState.getVector(1246);
        TabBar c0008ah = (TabBar) vectorM614m.elementAt(0);
        if (c0008ah.f47d == i && c0008ah.f46c == str) {
            return;
        }
        String str2 = c0008ah.f52i;
        int i2 = c0008ah.f53j;
        TabBar c0008ah2 = new TabBar(i, str, 4, null);
        vectorM614m.setElementAt(c0008ah2, 0);
        c0008ah2.f52i = str2;
        c0008ah2.f53j = i2;
        m178j();
        AppController.f153g = true;
    }

    /* renamed from: a */
    private static void m165a(int i, String str, int i2, Account abstractC0037h) {
        AppState.getVector(1246).addElement(new TabBar(i, str, i2, abstractC0037h));
    }

    /* renamed from: b */
    public final int m166b() {
        Vector vectorM614m = AppState.getVector(1246);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return 0;
            }
        } while (vectorM614m.elementAt(size) != this);
        m177a(size);
        return this.f50g;
    }

    /* renamed from: c */
    public static final TabBar m167c() {
        int i = f45b + 1;
        Vector vectorM614m = AppState.getVector(1246);
        if (i < vectorM614m.size()) {
            return (TabBar) vectorM614m.elementAt(i);
        }
        return null;
    }

    /* renamed from: d */
    public static final TabBar m168d() {
        int i = f45b - 1;
        if (i >= 0) {
            return (TabBar) AppState.getVector(1246).elementAt(i);
        }
        return null;
    }

    /* renamed from: e */
    public static final void m169e() {
        m174k();
        if (m176a(36, (Account) null) == null) {
            m165a(240, AppState.getString(1044), 36, null);
        }
    }

    /* renamed from: f */
    public static final void m170f() {
        m173b(67, 36);
    }

    /* renamed from: g */
    public static final void m171g() {
        m174k();
        if (m176a(6, (Account) null) == null) {
            m165a(264, AppState.getString(1045), 6, null);
        }
    }

    /* renamed from: h */
    public static final void m172h() {
        m173b(68, 6);
    }

    /* renamed from: b */
    private static final void m173b(int i, int i2) {
        TabBar c0008ah;
        if (AppState.getBool(i)) {
            return;
        }
        Vector vectorM614m = AppState.getVector(1246);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                c0008ah = (TabBar) vectorM614m.elementAt(size);
            }
        } while (c0008ah.f50g != i2);
        vectorM614m.removeElement(c0008ah);
        m178j();
        AppController.f153g = true;
    }

    /* renamed from: k */
    private static final void m174k() {
        if (AppState.getBool(243)) {
            return;
        }
        m164a(156, AppState.getString(1047));
    }

    /* renamed from: i */
    public static final TabBar m175i() {
        return (TabBar) AppState.getVector(1246).elementAt(f45b);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0032, code lost:
    
        return m177a(r6);
     */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final TabBar m176a(int i, Account abstractC0037h) {
        Vector vectorM614m = AppState.getVector(1246);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size >= 0) {
                TabBar c0008ah = (TabBar) vectorM614m.elementAt(size);
                if (c0008ah.f50g == i && (abstractC0037h == null || c0008ah.f51h == abstractC0037h)) {
                    return c0008ah;
                }
            } else {
                return null;
            }
        }
    }

    /* renamed from: a */
    private static final TabBar m177a(int i) {
        if (f45b != i) {
            f45b = i;
            m178j();
        }
        TabBar c0008ah = (TabBar) AppState.getVector(1246).elementAt(i);
        f54k = c0008ah.f51h;
        return c0008ah;
    }

    /* renamed from: j */
    public static final void m178j() {
        int i;
        Object objElementAt;
        int[] iArr;
        Object objElementAt2;
        int[] iArr2;
        Vector vectorM614m = AppState.getVector(1246);
        if (vectorM614m == null) {
            return;
        }
        NetworkUtils.releaseVector(AppState.getVector(1245));
        Vector vectorM1213g = NetworkUtils.newVector();
        AppState.pool[1245] = vectorM1213g;
        int size = vectorM614m.size();
        int i2 = f45b;
        TabBar c0008ah = (TabBar) vectorM614m.elementAt(i2);
        int i3 = 0;
        int i4 = 0;
        int i5 = 20;
        for (int i6 = 0; i6 < size; i6++) {
            TabBar c0008ah2 = (TabBar) vectorM614m.elementAt(i6);
            c0008ah2.f48e = 26 + AppState.getGfxContext(1).m214a(c0008ah2.f46c);
            c0008ah2.f49f = i5;
            vectorM1213g.addElement(c0008ah2);
            i5 += c0008ah2.f48e;
            i3 += c0008ah2.f48e;
            if (i6 == i2) {
                i4 = i3;
            }
        }
        int i7 = i3;
        int iM586d = AppState.getInt(1528) - 20;
        while (i4 >= iM586d - 32) {
            int i8 = 0;
            while (true) {
                objElementAt2 = vectorM614m.elementAt(i8);
                if (vectorM1213g.contains(objElementAt2)) {
                    break;
                } else {
                    i8++;
                }
            }
            TabBar c0008ah3 = (TabBar) objElementAt2;
            if (c0008ah3 == c0008ah) {
                break;
            }
            int i9 = c0008ah3.f48e;
            vectorM1213g.removeElement(c0008ah3);
            Object objFirstElement = vectorM1213g.firstElement();
            if (objFirstElement instanceof int[]) {
                iArr2 = (int[]) objFirstElement;
            } else {
                int[] iArr3 = {20, 248};
                iArr2 = iArr3;
                vectorM1213g.insertElementAt(iArr3, 0);
                i9 -= 16;
            }
            if (c0008ah3.f50g == 36 && AppController.m410N()) {
                if (iArr2[1] == 248) {
                    vectorM1213g.insertElementAt(new int[]{20, 16385}, 0);
                    int[] iArr4 = iArr2;
                    iArr4[0] = iArr4[0] + 16;
                } else {
                    vectorM1213g.insertElementAt(new int[]{36, 16385}, 1);
                    int[] iArr5 = (int[]) vectorM1213g.elementAt(2);
                    iArr5[0] = iArr5[0] + 16;
                }
                i9 -= 16;
            }
            if (c0008ah3.f50g == 4) {
                Account abstractC0037h = c0008ah3.f51h;
                if (AppController.m416b(abstractC0037h) && AppState.getBool(67) && iArr2[1] == 248) {
                    vectorM1213g.insertElementAt(new int[]{20, AppController.m417c(abstractC0037h)}, 0);
                    int[] iArr6 = iArr2;
                    iArr6[0] = iArr6[0] + 16;
                    i9 -= 16;
                }
            }
            for (int i10 = 0; i10 < size; i10++) {
                ((TabBar) vectorM614m.elementAt(i10)).f49f -= i9;
            }
            i4 -= i9;
            i7 -= i9;
        }
        while (true) {
            if (i7 < iM586d) {
                break;
            }
            int i11 = size - 1;
            while (true) {
                objElementAt = vectorM614m.elementAt(i11);
                if (vectorM1213g.contains(objElementAt)) {
                    break;
                } else {
                    i11--;
                }
            }
            TabBar c0008ah4 = (TabBar) objElementAt;
            if (c0008ah4 == c0008ah) {
                break;
            }
            int i12 = c0008ah4 == vectorM614m.lastElement() ? 16 : 0;
            if (c0008ah4.f48e > (i7 - iM586d) + i12) {
                int i13 = (i7 - iM586d) + i12;
                c0008ah4.f48e -= i13;
                int size2 = vectorM1213g.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        break;
                    }
                    Object objElementAt3 = vectorM1213g.elementAt(size2);
                    if (!(objElementAt3 instanceof int[])) {
                        break;
                    }
                    int[] iArr7 = (int[]) objElementAt3;
                    iArr7[0] = iArr7[0] - i13;
                }
            } else {
                int i14 = c0008ah4.f48e;
                int i15 = c0008ah4.f49f;
                vectorM1213g.removeElement(c0008ah4);
                Object objLastElement = vectorM1213g.lastElement();
                if (objLastElement instanceof int[]) {
                    iArr = (int[]) objLastElement;
                    int size3 = vectorM1213g.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            break;
                        }
                        Object objElementAt4 = vectorM1213g.elementAt(size3);
                        if (!(objElementAt4 instanceof int[])) {
                            break;
                        }
                        int[] iArr8 = (int[]) objElementAt4;
                        iArr8[0] = iArr8[0] - i14;
                    }
                } else {
                    int[] iArr9 = {i15, 246};
                    iArr = iArr9;
                    vectorM1213g.addElement(iArr9);
                    i14 -= 16;
                }
                if (c0008ah4.f50g == 36 && AppController.m410N() && AppState.getBool(67)) {
                    vectorM1213g.addElement(new int[]{i15 + 16, 16385});
                    i14 -= 16;
                }
                if (c0008ah4.f50g == 4) {
                    Account abstractC0037h2 = c0008ah4.f51h;
                    if (AppController.m416b(abstractC0037h2)) {
                        if (iArr[1] == 246) {
                            vectorM1213g.addElement(new int[]{i15 + 16, AppController.m417c(abstractC0037h2)});
                            i14 -= 16;
                        } else {
                            int size4 = vectorM1213g.size() - 2;
                            int i16 = ((int[]) vectorM1213g.elementAt(size4))[1];
                            if (i16 != 16384 && i16 != 16386) {
                                vectorM1213g.insertElementAt(new int[]{i15 + 16, AppController.m417c(abstractC0037h2)}, size4 + 1);
                                int[] iArr10 = iArr;
                                iArr10[0] = iArr10[0] + 16;
                                i14 -= 16;
                            }
                        }
                    }
                }
                i7 -= i14;
            }
        }
        Object objLastElement2 = vectorM1213g.lastElement();
        if (objLastElement2 instanceof TabBar) {
            TabBar c0008ah5 = (TabBar) objLastElement2;
            i = (c0008ah5.f49f + c0008ah5.f48e) - 20;
        } else {
            i = ((int[]) objLastElement2)[0] - 4;
        }
        if (i <= iM586d) {
            return;
        }
        int i17 = i - iM586d;
        c0008ah.f48e -= i17;
        int size5 = vectorM1213g.size();
        while (true) {
            size5--;
            if (size5 < 0) {
                return;
            }
            Object objElementAt5 = vectorM1213g.elementAt(size5);
            if (!(objElementAt5 instanceof int[])) {
                return;
            }
            int[] iArr11 = (int[]) objElementAt5;
            iArr11[0] = iArr11[0] - i17;
        }
    }

    /* renamed from: a */
    public static final Object m179a(int i, int i2) {
        Vector vectorM614m = AppState.getVector(1245);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return null;
            }
            Object objElementAt = vectorM614m.elementAt(size);
            if (objElementAt instanceof int[]) {
                int[] iArr = (int[]) objElementAt;
                if (i >= iArr[0] && i < iArr[0] + 16 && i2 >= 0 && i2 <= 22) {
                    return iArr;
                }
            } else {
                TabBar c0008ah = (TabBar) objElementAt;
                int i3 = c0008ah.f49f;
                if (i >= i3 && i2 >= 0 && i <= i3 + c0008ah.f48e && i2 <= 22) {
                    return c0008ah;
                }
            }
        }
    }
}
