package p000;

import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ai */
/* loaded from: MobileAgent_3.9.jar:ai.class */
public final class C0009ai extends AbstractC0041l {

    /* renamed from: a */
    public final int f55a;

    /* renamed from: b */
    public int f56b;

    /* renamed from: c */
    public String f57c;

    /* renamed from: z */
    private boolean f58z;

    /* renamed from: d */
    public int f59d;

    /* renamed from: e */
    public int f60e;

    /* renamed from: f */
    public int f61f;

    /* renamed from: g */
    public boolean f62g;

    /* renamed from: h */
    public boolean f63h;

    /* renamed from: i */
    public static long[] f64i;

    /* renamed from: j */
    public static long[] f65j;

    /* renamed from: k */
    public static Vector f66k;

    /* renamed from: l */
    public static Vector f67l;

    /* renamed from: m */
    public static Object[] f68m;

    /* renamed from: A */
    private static int f69A;

    /* renamed from: n */
    public static Vector f70n;

    /* renamed from: y */
    public static boolean f71y;

    /* renamed from: B */
    private static int f72B;

    /* renamed from: C */
    private static int f73C;

    public C0009ai(C0033d c0033d, int i, int i2, String str, String str2, boolean z) {
        super(c0033d);
        this.f55a = i;
        this.f56b = i2;
        this.f57c = str;
        this.f376u = str2;
        this.f377v = StringUtils.m17c(str2.toLowerCase());
        this.f58z = z;
        this.f373r = 255;
        this.f380w = c0033d.m1050q().m1314d(str).m1337i();
        c0033d.m1081h(this);
        m1228A();
        this.f381x = str;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: c */
    public final void mo134c() {
        this.f373r = 255;
        this.f62g = false;
        this.f63h = false;
        super.mo134c();
    }

    @Override // p000.AbstractC0041l
    /* renamed from: a */
    public final String mo135a() {
        return this.f57c;
    }

    public C0009ai(AbstractC0037h abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.f55a = c0043n.m1328e();
        this.f56b = c0043n.m1328e();
        this.f57c = c0043n.m1334g();
        m1249c(c0043n.m1335e((String) null));
        this.f58z = c0043n.m1340l();
        c0043n.m1340l();
        this.f59d = c0043n.m1353u();
        this.f60e = c0043n.m1353u();
        this.f61f = c0043n.m1353u();
        byte bM1344o = c0043n.m1344o();
        this.f374s = bM1344o;
        if (bM1344o != 0) {
            C0015ao.m414a((AbstractC0041l) this);
        }
        this.f373r = 255;
        this.f380w = abstractC0037h.m1050q().m1314d(this.f57c).m1337i();
        abstractC0037h.m1081h(this);
        m1228A();
        this.f381x = this.f57c;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: a */
    public final void mo136a(ByteBuffer c0043n) {
        c0043n.m1360p(this.f55a).m1360p(this.f56b).m1308a(this.f57c).m1309b(this.f376u).m1322a(this.f58z).m1322a(false).m1357m(this.f59d).m1357m(this.f60e).m1357m(this.f61f).m1321f(this.f374s);
    }

    @Override // p000.AbstractC0041l
    /* renamed from: b */
    public final C0032c mo138b() {
        C0032c c0032cM901a = C0032c.m887a(this.f380w).m896a(mo139e()).m901a(this.f376u, mo141j() ? 3 : mo140i() ? 2 : 0, this.f373r == 255 ? 0 : mo140i() ? 4 : mo141j() ? 5 : 3);
        c0032cM901a.f265d = this;
        return c0032cM901a;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: e */
    public final int mo139e() {
        int iMo139e = super.mo139e();
        if (iMo139e == 16384 || iMo139e == 26) {
            return iMo139e;
        }
        if (mo144l() || mo143m()) {
            return 263;
        }
        return iMo139e;
    }

    /* renamed from: a */
    public final ByteBuffer m180a(int i, String str, int i2) {
        ByteBuffer c0043n = new ByteBuffer();
        if (i != 2) {
            c0043n.m1357m(305).m1376j(str);
        }
        if (i == 5) {
            c0043n.m1357m(102).m1357m(0);
        }
        return new ByteBuffer().m1376j(this.f57c).m1357m(i2).m1357m(this.f55a).m1357m(0).m1326b(c0043n);
    }

    @Override // p000.AbstractC0041l
    /* renamed from: i */
    public final boolean mo140i() {
        return this.f59d != 0;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: j */
    public final boolean mo141j() {
        return this.f60e != 0;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: k */
    public final boolean mo142k() {
        return this.f61f != 0;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: m */
    public final boolean mo143m() {
        return this.f55a == -1;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: l */
    public final boolean mo144l() {
        return this.f58z && this.f55a != -1;
    }

    @Override // p000.AbstractC0041l
    /* renamed from: h */
    public final void mo145h() {
        if (mo143m()) {
            return;
        }
        this.f58z = false;
        m1228A();
    }

    /* renamed from: a */
    public final void m181a(int i, int i2) {
        if (i == 2) {
            this.f59d = i2;
        } else if (i == 3) {
            this.f60e = i2;
        } else {
            this.f61f = i2;
        }
    }

    /* renamed from: a */
    public static final void m182a(long j, long j2) {
        f64i[0] = j;
        f64i[1] = j2;
    }

    /* renamed from: b */
    public static final void m183b(long j, long j2) {
        f65j[0] = j;
        f65j[1] = j2;
    }

    /* renamed from: a */
    public static final void m184a(boolean z) {
        f71y = z;
        AppState.m599a(1573, z);
        AppState.m599a(1574, z && !AppState.m587e(1575));
    }

    /* renamed from: f */
    public static final void m185f() {
        f64i[0] = 0;
        f64i[1] = 0;
        f65j[0] = 0;
        f65j[1] = 0;
        f70n.removeAllElements();
        f66k.removeAllElements();
        f67l.removeAllElements();
        m184a(false);
        f69A = 0;
        AppState.m594c(1573, 0);
        AppState.m594c(1574, 0);
        AppState.m594c(1575, 0);
    }

    /* renamed from: o */
    public static final String m186o() {
        ByteBuffer c0043nM1314d = new ByteBuffer().m1310c(1442705).m1310c(3085016).m1314d(C0029bb.m809a(f64i[0])).m1385u(1026586918).m1314d(C0029bb.m810b(f64i[1]));
        int size = f66k.size();
        int i = 0;
        while (i <= size) {
            int[] iArr = i < size ? (int[]) f66k.elementAt(i) : new int[]{(int) f65j[0], (int) f65j[1]};
            c0043nM1314d.m1385u(30758).m1382s(i + 1).m1321f(61).m1314d(C0029bb.m809a(iArr[0])).m1385u(31014).m1382s(i + 1).m1321f(61).m1314d(C0029bb.m810b(iArr[1]));
            i++;
        }
        return c0043nM1314d.m1317c();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v46, types: [java.lang.Object, java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v61, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v78, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v90, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* renamed from: b */
    public static final void m187b(ByteBuffer c0043n) {
        int[] iArr = null;
        int i = 0;
        int i2 = 0;
        int iM192t = 0;
        Object z = Boolean.FALSE;
        int i3 = 0;
        f70n.removeAllElements();
        f72B = 0;
        f73C = 0;
        Hashtable hashtable = (Hashtable) JsonParser.m466a(c0043n, 2);
        f72B = ((Integer) hashtable.get("totalLength")).intValue();
        f73C = ((Integer) hashtable.get("totalTime")).intValue();
        Vector vector = (Vector) hashtable.get("regions");
        int size = vector.size();
        int i4 = 0;
        while (i4 < size) {
            Object[] r0 = new Object[2];
            Hashtable hashtable2 = (Hashtable) vector.elementAt(i4);
            Vector vector2 = (Vector) hashtable2.get("lefttop");
            Vector vector3 = (Vector) hashtable2.get("rightbottom");
            r0[0] = new int[]{((Integer) vector2.elementAt(0)).intValue(), ((Integer) vector2.elementAt(1)).intValue(), ((Integer) vector3.elementAt(0)).intValue(), ((Integer) vector3.elementAt(1)).intValue()};
            Vector vector4 = (Vector) hashtable2.get("points");
            int size2 = vector4.size() + 2;
            Object[] r02 = new Object[size2];
            r02[0] = z;
            r02[size2 - 1] = 0;
            int i5 = 1;
            while (i5 < size2 - 1) {
                Vector vector5 = (Vector) vector4.elementAt(i5 - 1);
                int size3 = ((i4 == 0 && i5 == 1) || (i4 == size - 1 && i5 == size2 - 2)) ? 4 : vector5.size();
                int i6 = size3;
                Object[] r03 = new Object[size3 - 1];
                r03[0] = new int[]{((Integer) vector5.elementAt(0)).intValue(), ((Integer) vector5.elementAt(1)).intValue()};
                if (i6 == 4) {
                    if (i4 == 0 && i5 == 1) {
                        StringBuffer stringBufferAppend = C0040k.m1217h().append(AppState.m584b(979));
                        int i7 = 952;
                        int i8 = f72B;
                        int i9 = 0;
                        if (i8 > 1000) {
                            i9 = i8 % 1000;
                            i8 /= 1000;
                            i7 = 952 + 1;
                        }
                        StringBuffer stringBufferM1217h = C0040k.m1217h();
                        stringBufferM1217h.append(i8);
                        if (i9 != 0) {
                            stringBufferM1217h.append('.');
                            String strM17c = StringUtils.m17c(Integer.toString(i9));
                            String strM13b = strM17c;
                            if (strM17c.length() > 2) {
                                strM13b = StringUtils.m13b(strM13b, 2);
                            }
                            stringBufferM1217h.append(strM13b);
                        }
                        StringBuffer stringBufferAppend2 = stringBufferAppend.append(C0040k.m1215a(stringBufferM1217h.append(AppState.m584b(i7)))).append(AppState.m584b(983));
                        int i10 = f73C;
                        StringBuffer stringBufferM1217h2 = C0040k.m1217h();
                        int i11 = i10 / 60;
                        if (i11 < 90) {
                            stringBufferM1217h2.append(i11);
                        } else {
                            stringBufferM1217h2.append(i11 / 60).append(AppState.m584b(954)).append(i11 % 60);
                        }
                        r03[1] = stringBufferAppend2.append(C0040k.m1215a(stringBufferM1217h2.append(AppState.m584b(955)))).toString();
                        r03[2] = AppState.f181d;
                    } else if (i4 == size - 1 && i5 == size2 - 2) {
                        r03[1] = AppState.m584b(980);
                        r03[2] = AppState.f181d;
                    } else {
                        r03[1] = vector5.elementAt(2);
                        r03[2] = vector5.elementAt(3);
                    }
                }
                r02[i5] = r03;
                if (i5 == 1 && size2 > 2 && i4 > 0 && i3 != 0) {
                    ((Object[]) ((Object[]) f70n.elementAt(i4 - 1))[1])[i3 - 1] = r03;
                }
                z = r03;
                i5++;
            }
            i3 = size2;
            r0[1] = r02;
            f70n.addElement(r0);
            i4++;
        }
        f67l.removeAllElements();
        for (int i12 = 0; i12 < f66k.size(); i12++) {
            try {
                iArr = (int[]) f66k.elementAt(i12);
                i = iArr[0];
                i2 = iArr[1];
                iM192t = m192t();
            } catch (Throwable unused) {
            }
            if (iM192t == 0) {
                throw new RuntimeException();
            }
            int i13 = 0;
            int[] iArrM193a = m193a(0);
            int iM319a = C0015ao.m319a(iArrM193a[0], iArrM193a[1], i, i2);
            for (int i14 = 1; i14 < iM192t; i14++) {
                int[] iArrM193a2 = m193a(i14);
                int iM319a2 = C0015ao.m319a(iArrM193a2[0], iArrM193a2[1], i, i2);
                if (iM319a2 < iM319a) {
                    iM319a = iM319a2;
                    i13 = i14;
                }
            }
            f67l.addElement(new Object[]{C0034e.m967e(i13), iArr});
        }
    }

    /* renamed from: p */
    public static final boolean m188p() {
        return (f64i[0] == 0 || f64i[1] == 0) ? false : true;
    }

    /* renamed from: q */
    public static final boolean m189q() {
        return (f65j[0] == 0 || f65j[1] == 0) ? false : true;
    }

    /* renamed from: r */
    public static final int[] m190r() {
        int iM192t = m192t();
        int iM586d = AppState.m586d(39);
        int[] iArrM193a = m193a(f69A);
        int iM317a = (int) C0015ao.m317a(iArrM193a[0], iM586d);
        int iM317a2 = (int) C0015ao.m317a(iArrM193a[1], iM586d);
        for (int i = f69A + 1; i < iM192t; i++) {
            if (m194b(i) != null) {
                int[] iArrM193a2 = m193a(i);
                if (AbstractC0030bc.m833a(iM317a, (int) C0015ao.m317a(iArrM193a2[0], iM586d), iM317a2, (int) C0015ao.m317a(iArrM193a2[1], iM586d)) || i == iM192t - 1) {
                    f69A = i;
                    break;
                }
            }
        }
        return m193a(f69A);
    }

    /* renamed from: s */
    public static final int[] m191s() {
        int[] iArrM193a;
        if (f69A == 0 && m194b(f69A) != null) {
            return m193a(f69A);
        }
        int iM586d = AppState.m586d(39);
        int[] iArrM193a2 = m193a(f69A);
        int iM317a = (int) C0015ao.m317a(iArrM193a2[0], iM586d);
        int iM317a2 = (int) C0015ao.m317a(iArrM193a2[1], iM586d);
        int i = f69A;
        while (true) {
            i--;
            if (i < 0) {
                return null;
            }
            if (m194b(i) != null) {
                iArrM193a = m193a(i);
                if (AbstractC0030bc.m833a(iM317a, (int) C0015ao.m317a(iArrM193a[0], iM586d), iM317a2, (int) C0015ao.m317a(iArrM193a[1], iM586d)) || i == 0) {
                    break;
                }
            }
        }
        f69A = i;
        return iArrM193a;
    }

    /* renamed from: t */
    public static final int m192t() {
        int length = 0;
        for (int i = 0; i < f70n.size(); i++) {
            Object[] objArr = (Object[]) ((Object[]) f70n.elementAt(i))[1];
            if (objArr != null) {
                length += objArr.length - 2;
            }
        }
        return length;
    }

    /* renamed from: a */
    public static final int[] m193a(int i) {
        if (i > m192t()) {
            return null;
        }
        int length = ((Object[]) ((Object[]) f70n.firstElement())[1]).length - 2;
        return (int[]) ((Object[]) ((Object[]) ((Object[]) f70n.elementAt(i / length))[1])[(i % length) + 1])[0];
    }

    /* renamed from: b */
    public static final String[] m194b(int i) {
        if (i > m192t()) {
            return null;
        }
        int length = ((Object[]) ((Object[]) f70n.firstElement())[1]).length - 2;
        Object[] objArr = (Object[]) ((Object[]) ((Object[]) f70n.elementAt(i / length))[1])[(i % length) + 1];
        if (objArr.length > 1) {
            return new String[]{(String) objArr[1], (String) objArr[2]};
        }
        return null;
    }

    /* renamed from: u */
    public static final void m195u() {
        f70n.removeAllElements();
        f69A = 0;
    }
}
