package p000;

import java.util.Vector;

/* renamed from: c */
/* loaded from: MobileAgent_3.9.jar:c.class */
public final class C0032c {

    /* renamed from: a */
    public final int f258a;

    /* renamed from: b */
    public String f259b;

    /* renamed from: c */
    public int f260c;

    /* renamed from: g */
    private int f261g;

    /* renamed from: h */
    private int f262h;

    /* renamed from: i */
    private Vector f263i;

    /* renamed from: j */
    private int[] f264j;

    /* renamed from: d */
    public Object f265d;

    /* renamed from: e */
    public boolean f266e;

    /* renamed from: f */
    public boolean f267f;

    /* renamed from: k */
    private int f268k;

    public C0032c(int i, String str) {
        this.f258a = i;
        this.f263i = NetworkUtils.m1213g();
        this.f264j = new int[16];
        this.f259b = str;
        this.f260c = 200;
    }

    private C0032c(String str, int i) {
        this(1, str);
        this.f260c = i;
    }

    /* renamed from: a */
    public final C0032c m884a() {
        this.f263i.removeAllElements();
        this.f264j[0] = 0;
        this.f261g = 0;
        this.f262h = 0;
        return this;
    }

    /* renamed from: b */
    public final boolean m885b() {
        return this.f258a != 0;
    }

    /* renamed from: c */
    public static final C0032c m886c() {
        return new C0032c(1, AppState.m584b(1038));
    }

    /* renamed from: a */
    public static final C0032c m887a(String str) {
        return new C0032c(1, str);
    }

    /* renamed from: a */
    public static final C0032c m888a(String str, int i) {
        return new C0032c(str, i);
    }

    /* renamed from: d */
    public static final C0032c m889d() {
        return new C0032c(0, AppState.f181d);
    }

    /* renamed from: a */
    public static final C0032c m890a(String str, boolean z) {
        C0032c c0032cM899a = new C0032c(2, str).m899a(z ? 25 : 24, str);
        c0032cM899a.f265d = C0034e.m968a(z);
        return c0032cM899a;
    }

    /* renamed from: a */
    public final C0032c m891a(Object obj, String str, Object obj2, Object obj3, Object obj4) {
        String str2 = Utils.m535l(str) ? str : null;
        if (obj instanceof String) {
            m898b(Utils.m527g(this.f259b));
        } else {
            m896a(((Integer) obj).intValue());
        }
        if (str2 != null) {
            m901a(((Integer) obj3).intValue() != 327680 ? str2 : Utils.m506c(str2), 1, 7);
        } else {
            m895e();
        }
        this.f265d = new Object[]{str, obj2, obj3, obj4, obj};
        return this;
    }

    /* renamed from: a */
    public final C0032c m892a(Vector vector, int i, String str) {
        int size = vector.size();
        int i2 = size;
        String[] strArr = new String[size];
        while (true) {
            i2--;
            if (i2 < 0) {
                NetworkUtils.m1212a(vector);
                C0032c c0032cM896a = m884a().m898b(Utils.m527g(str)).m901a(strArr[i], 1, 7).m896a(247);
                c0032cM896a.f265d = new Object[]{C0034e.m967e(i), strArr};
                return c0032cM896a;
            }
            strArr[i2] = (String) vector.elementAt(i2);
        }
    }

    /* renamed from: a */
    public static final C0032c m893a(C0012al c0012al) {
        C0032c c0032c = new C0032c(11, AppState.f181d);
        c0032c.f265d = c0012al;
        c0032c.f261g = c0012al.f91a.getWidth();
        c0032c.f262h = c0012al.f91a.getHeight() + 5;
        return c0032c;
    }

    /* renamed from: a */
    public final int m894a(C0013am c0013am) {
        if (this.f258a == 2) {
            if (this.f265d != null) {
                Boolean boolM968a = C0034e.m968a(!((Boolean) this.f265d).booleanValue());
                this.f265d = boolM968a;
                this.f263i.setElementAt(m897c(boolM968a.booleanValue() ? 25 : 24), 0);
            }
            C0029bb.m778d(this);
            return 0;
        }
        if (this.f258a == 15) {
            new RunnableC0055z(c0013am, this);
            return 0;
        }
        if (this.f258a != 9) {
            if (this.f258a == 4) {
                C0015ao.m340m(Utils.m522f(AppState.m584b(1379)).length() > 0 ? 427 : 428);
                return 0;
            }
            if (this.f258a != 5) {
                return -1;
            }
            C0015ao.m340m(429);
            return 0;
        }
        C0013am c0013amM75b = AbstractC0004ad.m75b(2351);
        Object[] objArr = (Object[]) this.f265d;
        String[] strArr = (String[]) objArr[1];
        int iIntValue = ((Integer) objArr[0]).intValue();
        Object[] objArr2 = {objArr, this, c0013am};
        for (String str : strArr) {
            C0032c c0032cM898b = new C0032c(13, str).m898b(str);
            c0032cM898b.f265d = objArr2;
            c0013amM75b.m225a(c0032cM898b);
        }
        c0013amM75b.m257b(strArr[iIntValue]);
        AbstractC0004ad.m71b(c0013amM75b);
        return 0;
    }

    /* renamed from: e */
    public final C0032c m895e() {
        return m903a(new int[]{16, AppState.m586d(1450)});
    }

    /* renamed from: a */
    public final C0032c m896a(int i) {
        return i >= 0 ? m903a(m897c(i)) : this;
    }

    /* renamed from: c */
    private static Object m897c(int i) {
        return new int[]{C0012al.m217c(i), 16, i};
    }

    /* renamed from: b */
    public final C0032c m898b(String str) {
        return m900a(-1, str, 0, 0);
    }

    /* renamed from: a */
    public final C0032c m899a(int i, String str) {
        return m900a(i, str, 0, 0);
    }

    /* renamed from: a */
    public final C0032c m900a(int i, String str, int i2, int i3) {
        if (i >= 0) {
            m896a(i);
        }
        return m901a(str, i2, i3);
    }

    /* renamed from: a */
    public final C0032c m901a(String str, int i, int i2) {
        return m902a(str, i, i2, -1);
    }

    /* renamed from: a */
    public final C0032c m902a(String str, int i, int i2, int i3) {
        if (str != null) {
            Vector vectorM907a = m907a(NetworkUtils.m1213g(), str, 0, str.length(), i, i2, i3);
            int size = vectorM907a.size();
            for (int i4 = 0; i4 < size; i4++) {
                m903a(vectorM907a.elementAt(i4));
            }
            NetworkUtils.m1212a(vectorM907a);
        }
        return this;
    }

    /* renamed from: a */
    private C0032c m903a(Object obj) {
        this.f263i.addElement(obj);
        this.f264j = C0015ao.m302a(this.f264j, this.f261g, 0);
        this.f261g += m904b(obj);
        int i = this.f262h;
        int iM905c = m905c(obj);
        if (i < iM905c) {
            this.f262h = iM905c;
        }
        return this;
    }

    /* renamed from: b */
    private static int m904b(Object obj) {
        if (obj == AppState.f177b[1370]) {
            return 0;
        }
        return obj instanceof int[] ? ((int[]) obj)[0] + 2 : ((int[]) ((Object[]) obj)[1])[0];
    }

    /* renamed from: c */
    private static int m905c(Object obj) {
        if (obj == AppState.f177b[1370]) {
            return 0;
        }
        return obj instanceof int[] ? ((int[]) obj)[1] : ((int[]) ((Object[]) obj)[1])[1];
    }

    /* renamed from: a */
    private final void m906a(Vector vector, String str, C0012al c0012al, int i, int i2, int i3, int i4, int i5) {
        int iM215a = c0012al.m215a(str, i2, i3);
        if (iM215a < (AppState.m586d(1528) << 2) / 5) {
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
                m906a(vector, str, c0012al, i, i2, i6 + 1, i4, i5);
                m906a(vector, str, c0012al, i, i2 + i6 + 1, (i3 - i6) - 1, i4, i5);
                break;
            }
            i6++;
        }
        if (i6 == i3 - 1) {
            int i7 = i3 >> 1;
            m906a(vector, str, c0012al, i, i2, i7, i4, i5);
            m906a(vector, str, c0012al, i, i2 + i7, i3 - i7, i4, i5);
        }
    }

    /* renamed from: a */
    private final Vector m907a(Vector vector, String str, int i, int i2, int i3, int i4, int i5) {
        int iIndexOf;
        int iIndexOf2;
        if (i < i2) {
            if (i5 == 1) {
                int i6 = -1;
                int i7 = -1;
                for (int i8 = 0; i8 < 43; i8++) {
                    String strM584b = AppState.m584b(i8 + 1141);
                    if (null != strM584b && (iIndexOf2 = str.indexOf(strM584b, i)) >= 0 && (iIndexOf2 < i6 || i6 == -1)) {
                        i6 = iIndexOf2;
                        i7 = i8;
                    }
                }
                if (i6 < 0 || i6 >= i2) {
                    m907a(vector, str, i, i2, i3, i4, -1);
                } else {
                    m907a(vector, str, i, i6, i3, i4, -1);
                    vector.addElement(new int[]{16, 16, i7 + 110});
                    m907a(vector, str, i6 + AppState.m584b(i7 + 1141).length(), i2, i3, i4, 1);
                }
            } else if (i5 == 2 || i5 == 3) {
                int i9 = -1;
                int i10 = -1;
                for (int i11 = 0; i11 < 37; i11++) {
                    String strM584b2 = AppState.m584b(i11 + 1184);
                    if (null != strM584b2 && (iIndexOf = str.indexOf(strM584b2, i)) >= 0 && (iIndexOf < i9 || i9 == -1)) {
                        i9 = iIndexOf;
                        i10 = i11;
                    }
                }
                if (i9 < 0 || i9 >= i2) {
                    m907a(vector, str, i, i2, i3, i4, -1);
                } else {
                    m907a(vector, str, i, i9, i3, i4, -1);
                    vector.addElement(new int[]{16, 16, i10 + 318});
                    m907a(vector, str, i9 + AppState.m584b(i10 + 1184).length(), i2, i3, i4, 2);
                }
            } else if (i5 == 0) {
                int i12 = -1;
                int i13 = -1;
                for (int i14 = 0; i14 < 78; i14++) {
                    int iIndexOf3 = str.indexOf(AppState.m584b(i14 + 1063), i);
                    if (iIndexOf3 >= 0 && (iIndexOf3 < i12 || i12 == -1)) {
                        i12 = iIndexOf3;
                        i13 = i14;
                    }
                }
                if (i12 < 0 || i12 >= i2) {
                    m907a(vector, str, i, i2, i3, i4, -1);
                } else {
                    m907a(vector, str, i, i12, i3, i4, -1);
                    int[] iArr = new int[3];
                    iArr[0] = 16;
                    iArr[1] = 16;
                    iArr[2] = i13 < 74 ? i13 + 36 : i13 == 74 ? 142 : i13 == 75 ? 137 : i13 == 76 ? 210 : 205;
                    vector.addElement(iArr);
                    m907a(vector, str, i12 + AppState.m584b(i13 + 1063).length(), i2, i3, i4, 0);
                }
            } else if (str != AppState.m584b(1037)) {
                C0012al c0012alM608k = AppState.m608k(i3);
                int iM623o = AppState.m623o(i3);
                int i15 = i;
                int i16 = i;
                while (true) {
                    if (i16 > i2) {
                        break;
                    }
                    if (i16 == i2) {
                        int i17 = i16 - i15;
                        if (i17 > 0) {
                            m906a(vector, str, c0012alM608k, iM623o, i15, i17, i3, i4);
                        }
                    } else {
                        char cCharAt = str.charAt(i16);
                        if (cCharAt == ' ') {
                            int i18 = (i16 - i15) + 1;
                            if (i18 > 1) {
                                m906a(vector, str, c0012alM608k, iM623o, i15, i18, i3, i4);
                            }
                            i15 = i16 + 1;
                        } else if (cCharAt == '\r' || cCharAt == '\n') {
                            int i19 = i16 - i15;
                            if (i19 > 0) {
                                m906a(vector, str, c0012alM608k, iM623o, i15, i19, i3, i4);
                            }
                            vector.addElement(AppState.f177b[1370]);
                            i15 = i16 + 1;
                        }
                        i16++;
                    }
                }
            } else {
                int length = str.length();
                vector.addElement(new Object[]{str, new int[]{AppState.m608k(i3).m215a(str, 0, length), AppState.m623o(i3), 0, length, i3, i4}});
            }
        }
        return vector;
    }

    /* renamed from: a */
    public final C0032c m908a(int i, int i2) {
        if (i > 1) {
            this.f268k = i2;
        }
        return this;
    }

    /* renamed from: b */
    public final C0032c m909b(int i) {
        if (this.f258a == 11) {
            return this;
        }
        this.f264j[0] = 0;
        Vector vector = this.f263i;
        int size = vector.size();
        int i2 = 0;
        int i3 = 0;
        int iM502a = 0;
        this.f262h = 0;
        for (int i4 = 0; i4 < size; i4++) {
            Object objElementAt = vector.elementAt(i4);
            if (objElementAt != AppState.f177b[1370]) {
                int iM904b = m904b(objElementAt);
                int iM905c = m905c(objElementAt);
                int i5 = 0;
                if (i4 == size - 2 && (vector.elementAt(i4 + 1) instanceof int[])) {
                    int[] iArr = (int[]) vector.elementAt(i4 + 1);
                    if (iArr.length == 3 && iArr[2] == 244) {
                        i5 = 16;
                    }
                }
                if (this.f268k == 0 && i2 + iM904b + i5 > i) {
                    i2 = 0;
                    i3 += iM502a;
                    this.f262h += iM502a;
                    iM502a = 0;
                }
                this.f264j = C0015ao.m302a(this.f264j, i2, i3);
                iM502a = Utils.m502a(iM502a, iM905c);
                i2 += iM904b;
            } else {
                i2 = 0;
                i3 += iM502a;
                this.f262h += iM502a;
                iM502a = 0;
                this.f264j = C0015ao.m302a(this.f264j, 0, 0);
            }
        }
        this.f262h += iM502a;
        return this;
    }

    /* renamed from: f */
    public final int m910f() {
        int iM502a = 0;
        Vector vector = this.f263i;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return iM502a + 4;
            }
            iM502a = Utils.m502a(iM502a, this.f264j[(size << 1) + 1] + m904b(vector.elementAt(size)));
        }
    }

    /* renamed from: g */
    public final int m911g() {
        return this.f268k != 0 ? this.f268k : this.f261g + 4;
    }

    /* renamed from: h */
    public final int m912h() {
        return Utils.m502a(this.f262h, AppState.m586d(1450)) + 4;
    }

    /* renamed from: a */
    public final void m913a(C0012al c0012al, int i, int i2, int i3) {
        if (this.f258a == 11) {
            c0012al.f92b.drawImage(((C0012al) this.f265d).f91a, i, i2, 20);
            return;
        }
        Vector vector = this.f263i;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            Object objElementAt = vector.elementAt(size);
            if (objElementAt != AppState.f177b[1370]) {
                int i4 = i + 2;
                int i5 = this.f264j[(size << 1) + 1];
                int i6 = i2 + 2 + this.f264j[(size << 1) + 1 + 1];
                int i7 = i4 + i5;
                if (objElementAt instanceof int[]) {
                    int[] iArr = (int[]) objElementAt;
                    if (iArr.length == 3) {
                        int i8 = iArr[2];
                        c0012al.m216a(i8, i8 != 244 ? i7 : (i4 + i3) - 13, i6 + AbstractC0004ad.m73f());
                    } else if (iArr.length == 2) {
                        c0012al.m207b(18).m211d(i7, i6, i3 - i7, iArr[1]);
                    }
                } else {
                    String str = (String) ((Object[]) objElementAt)[0];
                    int[] iArr2 = (int[]) ((Object[]) objElementAt)[1];
                    int i9 = iArr2[4];
                    C0012al c0012alM608k = AppState.m608k(i9);
                    C0012al c0012alM207b = c0012al.m212a(c0012alM608k).m207b(iArr2[5]);
                    int i10 = iArr2[2];
                    int i11 = iArr2[3];
                    if (i6 > 0 && i6 < AppState.m586d(1529)) {
                        c0012alM207b.f92b.drawSubstring(str, i10, i11, i7, i6, 20);
                    }
                    if (i9 == 3) {
                        c0012al.m211d(i7, i6 + (AppState.m586d(1450) >> 1), c0012alM608k.m215a(str, iArr2[2], iArr2[3]), 0);
                    } else if (i9 == 5) {
                        c0012al.m211d(i7, i6 + AppState.m586d(1450), c0012alM608k.m215a(str, iArr2[2], iArr2[3]), 0);
                    }
                }
            }
        }
    }
}
