package p000;

import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: ad */
/* loaded from: MobileAgent_3.9.jar:ad.class */
public abstract class AbstractC0004ad {
    /* renamed from: a */
    public static final void m65a() {
        int iM586d = AppState.m586d(73);
        int i = iM586d == 0 ? 8 : iM586d == 1 ? 0 : 16;
        C0012al c0012al = new C0012al(0, i);
        AppState.f177b[1273] = c0012al;
        C0012al c0012al2 = new C0012al(1, i);
        AppState.f177b[1274] = c0012al2;
        C0012al c0012al3 = AppState.m587e(70) ? new C0012al(2, i) : c0012al;
        AppState.f177b[1275] = c0012al3;
        AppState.f177b[1276] = c0012al;
        AppState.f177b[1277] = c0012al;
        AppState.f177b[1278] = c0012al2;
        AppState.m594c(1450, c0012al.f93c.getHeight());
        AppState.m594c(1453, c0012al.f93c.getHeight());
        AppState.m594c(1454, c0012al.f93c.getHeight());
        AppState.m594c(1455, c0012al2.f93c.getHeight());
        AppState.m594c(1451, c0012al2.f93c.getHeight());
        AppState.m594c(1452, c0012al3.f93c.getHeight());
        Vector vectorM614m = AppState.m614m(1272);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((C0013am) vectorM614m.elementAt(size)).m258q();
            }
        }
    }

    /* renamed from: b */
    public static final C0013am m66b() {
        Vector vectorM614m = AppState.m614m(1272);
        if (vectorM614m.isEmpty()) {
            return null;
        }
        return (C0013am) vectorM614m.lastElement();
    }

    /* renamed from: c */
    public static final String m67c() {
        if (AppState.m614m(1272).size() > 0) {
            return m66b().m220b();
        }
        return null;
    }

    /* renamed from: d */
    public static final int m68d() {
        if (AppState.m614m(1272).size() > 0) {
            return m66b().m221c();
        }
        return 200;
    }

    /* renamed from: e */
    public static final C0032c m69e() {
        if (AppState.m614m(1272).size() > 0) {
            return m66b().m222d();
        }
        return null;
    }

    /* renamed from: a */
    public static final void m70a(C0013am c0013am) {
        Vector vectorM614m = AppState.m614m(1272);
        while (vectorM614m.size() > 0) {
            C0021au.m549c();
        }
        vectorM614m.addElement(c0013am);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0166  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void m71b(C0013am c0013am) {
        C0013am c0013am2 = null;
        Vector vectorM614m = AppState.m614m(1272);
        int size = vectorM614m.size() - 1;
        int i = size >= 0 ? ((C0013am) vectorM614m.elementAt(size)).f94a : -1;
        if (i == 137 || i == 63) {
            C0013am c0013am3 = (C0013am) vectorM614m.elementAt(size);
            c0013am2 = c0013am3;
            vectorM614m.removeElement(c0013am3);
        }
        int i2 = c0013am.f94a;
        if (i2 != 112) {
            int size2 = vectorM614m.size();
            for (int i3 = 0; i3 < size2; i3++) {
                if (((C0013am) vectorM614m.elementAt(i3)).f94a == i2) {
                    size2 = i3;
                }
            }
            while (vectorM614m.size() > size2) {
                C0021au.m549c();
            }
        }
        int i4 = c0013am.f97d;
        if (((1 << i4) & 3484) != 0) {
            c0013am.m226f();
        }
        int i5 = c0013am.f95b;
        int i6 = c0013am.f96c;
        int iM586d = AppState.m586d(1528) - i5;
        int iM605e = AppState.m605e() - i6;
        switch (i4) {
            case 2:
            case 7:
            case 8:
                c0013am.m266a(iM586d >> 1, iM605e >> 1);
                break;
            case 3:
                c0013am.m266a(0, iM605e);
                break;
            case 4:
                c0013am.m266a(iM586d, iM605e);
                break;
            case 10:
                C0013am c0013amM66b = m66b();
                if (c0013amM66b != null) {
                    int iM586d2 = c0013amM66b.f98e + c0013amM66b.f95b;
                    int iM265r = c0013amM66b.m265r();
                    if (iM586d2 + c0013am.f95b > AppState.m586d(1528)) {
                        iM586d2 = AppState.m586d(1528) - c0013am.f95b;
                    }
                    if (iM265r + c0013am.f96c > AppState.m605e()) {
                        iM265r = AppState.m605e() - c0013am.f96c;
                    }
                    c0013am.m266a(iM586d2, iM265r);
                    break;
                }
                break;
            case 11:
                c0013am.m266a(iM586d >> 1, (AppState.m605e() - i6) - (i6 / 10));
                break;
        }
        int size3 = vectorM614m.size();
        for (int i7 = 0; i7 < size3; i7++) {
            if (((C0013am) vectorM614m.elementAt(i7)).f97d == 7) {
                size3 = i7;
            }
        }
        while (vectorM614m.size() > size3) {
            C0021au.m549c();
        }
        c0013am.m235n();
        vectorM614m.addElement(c0013am);
        if (c0013am2 != null) {
            vectorM614m.addElement(c0013am2);
        }
    }

    /* renamed from: a */
    public static final boolean m72a(int i) {
        Vector vectorM614m = AppState.m614m(1272);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return false;
            }
        } while (((C0013am) vectorM614m.elementAt(size)).f94a != i);
        return true;
    }

    /* renamed from: f */
    public static final int m73f() {
        return Utils.m502a(0, (AppState.m586d(1450) - 16) >> 1);
    }

    /* renamed from: g */
    public static final int m74g() {
        if (!AppState.m587e(1543)) {
            return C0015ao.m338l(470);
        }
        AppState.m604b(new Object());
        return 0;
    }

    /* renamed from: b */
    public static final C0013am m75b(int i) {
        C0013am c0013am;
        int i2 = i + 1;
        String strM522f = Utils.m522f(AppState.m584b(AppState.m586d(i)));
        int i3 = i2 + 1;
        int iM586d = AppState.m586d(i2);
        int i4 = i3 + 1;
        int iM586d2 = AppState.m586d(i3);
        boolean z = (iM586d2 & 16) != 0;
        int i5 = iM586d2 & 15;
        int i6 = i4 + 1;
        int iM586d3 = AppState.m586d(i4);
        int i7 = i6 + 1;
        int iM586d4 = AppState.m586d(i6);
        int i8 = i7 + 1;
        int iM586d5 = AppState.m586d(i7);
        int i9 = i8 + 1;
        int iM586d6 = AppState.m586d(i8);
        int i10 = i9 + 1;
        int iM586d7 = AppState.m586d(i9);
        int i11 = i10 + 1;
        int iM586d8 = AppState.m586d(i10);
        int iM79a = i11 + 1;
        int iM586d9 = AppState.m586d(i11);
        int iM586d10 = AppState.m586d(1528);
        int iM605e = AppState.m605e();
        switch (i5) {
            case 0:
            case 1:
                c0013am = new C0013am(0, iM586d, iM586d10, iM605e, true);
                break;
            case 2:
            case 3:
            case 4:
            case 10:
            case 11:
                c0013am = new C0013am(0, iM586d, (iM586d10 * 9) / 10, (iM605e * 9) / 10, true);
                break;
            case 5:
            case 9:
                c0013am = new C0013am(0, iM586d, iM586d10, iM605e, false);
                break;
            case 6:
            case 12:
                c0013am = new C0013am(1, iM586d, iM586d10, iM605e, true);
                break;
            case 7:
            case 8:
                c0013am = new C0013am(0, iM586d, (iM586d10 * 9) / 10, (iM605e * 9) / 10, false);
                break;
            default:
                c0013am = null;
                break;
        }
        if (i5 != 3 && i5 != 4 && i5 != 2 && i5 != 11 && i5 != 10 && i5 != 8) {
            if (i5 != 7) {
                c0013am.m224a(iM586d3, strM522f);
            } else {
                c0013am.m225a(new C0032c(0, strM522f).m901a(strM522f, 1, 0));
            }
        }
        c0013am.f125x = z;
        c0013am.f100g = i;
        for (int i12 = 0; i12 < iM586d9; i12++) {
            iM79a = m79a(c0013am, iM79a, iM586d);
        }
        C0013am c0013amM259a = c0013am.m259a(iM586d4 > 0 ? AppState.m584b(iM586d4) : null, iM586d5 > 0 ? AppState.m584b(iM586d5) : null, iM586d6, iM586d7, iM586d8);
        c0013amM259a.f97d = i5;
        return c0013amM259a;
    }

    /* renamed from: c */
    public static final C0013am m76c(int i) {
        C0013am c0013am = new C0013am(0, i, (AppState.m586d(1528) * 9) / 10, (AppState.m605e() * 9) / 10, true);
        c0013am.f97d = 2;
        c0013am.f125x = true;
        return c0013am;
    }

    /* renamed from: h */
    public static final boolean m77h() {
        Vector vectorM614m = AppState.m614m(1272);
        int size = vectorM614m.size();
        do {
            size--;
            if (size <= 0) {
                return false;
            }
        } while (((C0013am) vectorM614m.elementAt(size)).f99f != 0);
        return true;
    }

    /* renamed from: a */
    private static final int m78a(boolean z, C0013am c0013am, int i, boolean z2) {
        int i2 = i + 1;
        int iM586d = AppState.m586d(i);
        int i3 = i2 + 1;
        int iM586d2 = AppState.m586d(i2);
        int i4 = i3 + 1;
        int iM586d3 = AppState.m586d(i3);
        if (z) {
            if (z2) {
                c0013am.m252b(iM586d2, iM586d3, iM586d);
            } else {
                c0013am.m249a(iM586d2, iM586d3, iM586d);
            }
        }
        return i4;
    }

    /* renamed from: a */
    private static final int m79a(C0013am c0013am, int i, int i2) {
        int i3;
        Object objM522f;
        int i4;
        String strM522f;
        int i5 = i + 1;
        int iM586d = AppState.m586d(i);
        boolean z = (iM586d & 16) != 0;
        boolean z2 = (iM586d & 32) != 0;
        switch (iM586d & 15) {
            case 0:
                boolean zM587e = z;
                int i6 = i5;
                if (z2) {
                    i6++;
                    zM587e = AppState.m587e(AppState.m586d(i6));
                }
                int i7 = i6;
                int i8 = i6 + 1;
                int iM586d2 = AppState.m586d(i7);
                int i9 = i8 + 1;
                int iM586d3 = AppState.m586d(i8);
                int i10 = i9 + 1;
                int iM586d4 = AppState.m586d(i9);
                if (zM587e) {
                    c0013am.m252b(iM586d3, iM586d4, iM586d2);
                } else {
                    c0013am.m249a(iM586d3, iM586d4, iM586d2);
                }
                return i10;
            case 1:
                int i11 = i5 + 1;
                C0032c c0032cM901a = C0032c.m889d().m901a(NetworkUtils.m1215a(NetworkUtils.m1217h().append(Utils.m522f(AppState.m584b(AppState.m586d(i5)))).append(' ')), 0, 0);
                int i12 = i11 + 1;
                String strM522f2 = Utils.m522f(AppState.m584b(AppState.m586d(i11)));
                if (!StringUtils.m1a(strM522f2)) {
                    c0032cM901a.m901a(strM522f2, 0, 6);
                }
                c0013am.m225a(c0032cM901a);
                return i12;
            case 2:
                int i13 = i5 + 1;
                String strM522f3 = Utils.m522f(AppState.m584b(AppState.m586d(i5)));
                int i14 = i13 + 1;
                c0013am.m225a(C0032c.m890a(strM522f3, AppState.m587e(AppState.m586d(i13))));
                return i14;
            case 3:
                int i15 = i5 + 1;
                String strM522f4 = Utils.m522f(AppState.m584b(AppState.m586d(i5)));
                int i16 = i15 + 1;
                Vector vectorM512e = Utils.m512e(Utils.m522f(AppState.m584b(AppState.m586d(i15))));
                int i17 = i16 + 1;
                c0013am.m225a(new C0032c(9, strM522f4).m892a(vectorM512e, AppState.m586d(AppState.m586d(i16)), strM522f4));
                return i17;
            case 4:
                int i18 = i5 + 1;
                c0013am.m225a(C0032c.m889d().m901a(Utils.m522f(AppState.m584b(AppState.m586d(i5))), 1, 0));
                return i18;
            case 5:
                if (i2 == 49) {
                    i3 = i5 + 1;
                    int iM586d5 = AppState.m586d(i5);
                    objM522f = (iM586d5 < 268 || iM586d5 > 304) ? (iM586d5 < 161 || iM586d5 > 210) ? AppState.m584b(iM586d5) : C0034e.m967e(iM586d5) : C0034e.m967e(iM586d5);
                } else {
                    i3 = i5 + 1;
                    objM522f = Utils.m522f(AppState.m584b(AppState.m586d(i5)));
                }
                Object obj = objM522f;
                int i19 = i3;
                int i20 = i3 + 1;
                int iM586d6 = AppState.m586d(i19);
                int i21 = i20 + 1;
                String strM522f5 = Utils.m522f(AppState.m584b(AppState.m586d(i20)));
                int i22 = i21 + 1;
                int iM586d7 = AppState.m586d(i21);
                if (iM586d7 == 2) {
                    int i23 = i22 + 3;
                    i4 = i23 + 1;
                    int iM586d8 = AppState.m586d(AppState.m586d(i23));
                    strM522f = iM586d8 >= 0 ? StringUtils.m17c(Integer.toString(iM586d8)) : AppState.f181d;
                } else {
                    i4 = i22 + 1;
                    strM522f = Utils.m522f(Utils.m522f(AppState.m584b(AppState.m586d(i22))));
                }
                c0013am.m225a(new C0032c(15, obj instanceof String ? (String) obj : AppState.f181d).m891a(obj, strM522f, C0034e.m967e(iM586d6), C0034e.m967e(iM586d7), strM522f5));
                return i4;
            case 6:
                int i24 = i5 + 1;
                c0013am.m225a(C0032c.m889d().m898b(Utils.m522f(AppState.m584b(AppState.m586d(i5)))));
                return i24;
            case 7:
                return m78a(AppState.m587e(AppState.m586d(i5)), c0013am, i5 + 1, z);
            case 8:
                return m78a(!AppState.m587e(AppState.m586d(i5)), c0013am, i5 + 1, z);
            case 9:
                String strM522f6 = Utils.m522f(AppState.m584b(AppState.m586d(i5)));
                String strM522f7 = Utils.m522f(AppState.m584b(AppState.m586d(i5 + 1)));
                C0032c c0032cM901a2 = new C0032c(4, (String) null).m884a().m896a(221).m901a(Utils.m535l(strM522f7) ? strM522f7 : strM522f6, 1, 7);
                c0032cM901a2.f265d = new String[]{strM522f6, strM522f7};
                c0013am.m225a(c0032cM901a2);
                return i5 + 2;
            case 10:
                String strM522f8 = Utils.m522f(AppState.m584b(AppState.m586d(i5)));
                C0032c c0032c = new C0032c(5, (String) null);
                c0032c.m884a();
                c0032c.m896a(219);
                if (Utils.m535l(strM522f8)) {
                    int iIndexOf = strM522f8.indexOf(0);
                    c0032c.m901a(iIndexOf < 0 ? strM522f8 : StringUtils.m13b(strM522f8, iIndexOf), 1, 7);
                } else {
                    c0032c.m895e();
                }
                c0032c.f265d = strM522f8;
                c0013am.m225a(c0032c);
                return i5 + 1;
            case 11:
                c0013am.m225a(C0032c.m893a(new C0012al((Image) AppState.f177b[AppState.m586d(i5)])));
                return i5 + 1;
            default:
                m79a(c0013am, AppState.m586d(i5), i2);
                return i5 + 1;
        }
    }
}
