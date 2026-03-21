package p000;

import java.util.Vector;

/* renamed from: ag */
/* loaded from: MobileAgent_3.9.jar:ag.class */
public abstract class AbstractC0007ag {
    /* renamed from: a */
    public static final void m152a() {
        AbstractC0023aw.m591f(1281);
        AbstractC0023aw.m591f(1365);
        AbstractC0023aw.m594c(1476, 4);
        C0008ah.m176a(4, C0008ah.f54k);
        C0013am c0013amM161g = m161g();
        C0008ah c0008ahM175i = C0008ah.m175i();
        C0013am c0013amM257b = c0013amM161g.m257b(c0008ahM175i.f52i);
        AbstractC0004ad.m70a(c0013amM257b);
        c0013amM257b.f105j = c0008ahM175i.f53j;
        c0013amM257b.m235n();
    }

    /* renamed from: b */
    public static final int m153b() {
        m154f();
        C0032c c0032cM69e = AbstractC0004ad.m69e();
        AbstractC0023aw.m613c(c0032cM69e == null ? null : c0032cM69e.f265d);
        return C0015ao.m457ac();
    }

    /* renamed from: f */
    private static final void m154f() {
        C0008ah c0008ahM175i = C0008ah.m175i();
        C0013am c0013amM66b = AbstractC0004ad.m66b();
        c0008ahM175i.f53j = c0013amM66b.f105j;
        c0008ahM175i.f52i = c0013amM66b.m220b();
    }

    /* renamed from: c */
    public static final void m155c() {
        AbstractC0023aw.m591f(1281);
        AbstractC0023aw.m591f(1365);
        m154f();
    }

    /* renamed from: d */
    public static final void m156d() {
        m155c();
        C0008ah c0008ahM175i = C0008ah.m175i();
        C0013am c0013amM257b = m161g().m257b(c0008ahM175i.f52i);
        AbstractC0004ad.m70a(c0013amM257b);
        c0013amM257b.f105j = c0008ahM175i.f53j;
        c0013amM257b.m235n();
        C0008ah.m176a(4, C0008ah.f54k);
        C0015ao.f153g = true;
    }

    /* renamed from: e */
    public static final int m157e() {
        m154f();
        return 0;
    }

    /* renamed from: a */
    public static final int m158a(String str, Object obj) {
        if (str == null) {
            return -1;
        }
        m154f();
        AbstractC0023aw.m613c(obj);
        if (obj == null) {
            return 0;
        }
        if (obj instanceof AbstractC0046q) {
            C0015ao.f152f = true;
            return ((AbstractC0046q) obj).mo1397n();
        }
        if (!(obj instanceof AbstractC0041l)) {
            return 0;
        }
        AbstractC0023aw.m591f(1279);
        C0015ao.m300h();
        return ((AbstractC0041l) obj).m1247K();
    }

    /* renamed from: a */
    public static final int m159a(Object obj) {
        m154f();
        AbstractC0023aw.m613c(obj);
        return obj != null ? 30 : -1;
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x00bb A[Catch: Throwable -> 0x0124, PHI: r12
      0x00bb: PHI (r12v1 int) = (r12v0 int), (r12v3 int) binds: [B:27:0x009f, B:29:0x00b2] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Throwable -> 0x0124, blocks: (B:26:0x0098, B:28:0x00a2, B:31:0x00bb, B:36:0x00d8, B:39:0x00ee, B:41:0x00f8, B:44:0x0112), top: B:75:0x0098 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00d4 A[PHI: r12
      0x00d4: PHI (r12v4 int) = (r12v0 int), (r12v2 int) binds: [B:25:0x0095, B:32:0x00cb] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00ee A[Catch: Throwable -> 0x0124, PHI: r12
      0x00ee: PHI (r12v5 int) = (r12v4 int), (r12v8 int) binds: [B:35:0x00d5, B:37:0x00e5] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Throwable -> 0x0124, blocks: (B:26:0x0098, B:28:0x00a2, B:31:0x00bb, B:36:0x00d8, B:39:0x00ee, B:41:0x00f8, B:44:0x0112), top: B:75:0x0098 }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0112 A[Catch: Throwable -> 0x0124, PHI: r12
      0x0112: PHI (r12v6 int) = (r12v5 int), (r12v7 int) binds: [B:40:0x00f5, B:42:0x0109] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Throwable -> 0x0124, blocks: (B:26:0x0098, B:28:0x00a2, B:31:0x00bb, B:36:0x00d8, B:39:0x00ee, B:41:0x00f8, B:44:0x0112), top: B:75:0x0098 }] */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final int m160a(C0013am c0013am, Object obj) {
        AbstractC0037h abstractC0037h;
        int iM1250M = -1;
        if (AbstractC0023aw.f177b[1291] != null) {
            return 122;
        }
        if (!AbstractC0023aw.m587e(1471)) {
            AbstractC0023aw.m594c(1471, 1);
            if (System.currentTimeMillis() - AbstractC0023aw.m598g(219) > 604800000) {
                AbstractC0023aw.m594c(1505, 0);
                return 57;
            }
        }
        m154f();
        Vector vector = c0013am.f110o;
        if (obj != null) {
            AbstractC0041l abstractC0041l = null;
            AbstractC0046q abstractC0046q = null;
            if (obj instanceof AbstractC0046q) {
                AbstractC0046q abstractC0046q2 = (AbstractC0046q) obj;
                abstractC0046q = abstractC0046q2;
                abstractC0037h = abstractC0046q2.f396d;
            } else {
                AbstractC0041l abstractC0041l2 = (AbstractC0041l) obj;
                abstractC0041l = abstractC0041l2;
                abstractC0037h = abstractC0041l2.f369o;
            }
            AbstractC0037h abstractC0037h2 = abstractC0037h;
            int iMo108h = abstractC0037h.mo108h();
            String str = abstractC0037h2.f340J;
            if (!AbstractC0023aw.m587e(243)) {
                C0008ah.m164a(iMo108h, str);
            }
            if (vector != null) {
                boolean z = false;
                String str2 = AbstractC0023aw.f181d;
                int i = 0;
                if (abstractC0041l != null) {
                    try {
                        iM1250M = abstractC0041l.m1250M();
                    } catch (Throwable unused) {
                        z = true;
                    }
                    if (iM1250M >= 0) {
                        i = 0 + 1;
                        if (((Integer) vector.elementAt(0)).intValue() != iM1250M) {
                            z = true;
                        } else {
                            int i2 = i;
                            i++;
                            if (!vector.elementAt(i2).equals(abstractC0041l.f381x)) {
                                z = true;
                            } else if (abstractC0046q != null) {
                                i++;
                                if (vector.elementAt(0).equals(str2)) {
                                    int iMo922n = abstractC0037h2.mo922n();
                                    if (iMo922n >= 0) {
                                        int i3 = i;
                                        i++;
                                        if (((Integer) vector.elementAt(i3)).intValue() != iMo922n) {
                                            z = true;
                                        } else if (i != vector.size()) {
                                            z = true;
                                        }
                                    }
                                } else {
                                    z = true;
                                }
                            }
                        }
                        if (z) {
                            vector.removeAllElements();
                            if (abstractC0041l != null) {
                                int iM1250M2 = abstractC0041l.m1250M();
                                if (iM1250M2 >= 0) {
                                    vector.addElement(C0034e.m967e(iM1250M2));
                                }
                                vector.addElement(abstractC0041l.f381x);
                            }
                            if (abstractC0046q != null) {
                                vector.addElement(str2);
                            }
                            int iMo922n2 = abstractC0037h2.mo922n();
                            if (iMo922n2 >= 0) {
                                vector.addElement(C0034e.m967e(iMo922n2));
                            }
                            C0015ao.f153g = true;
                        }
                    }
                }
            }
        } else if (vector != null && vector.size() > 0) {
            vector.removeAllElements();
            C0015ao.f153g = true;
        }
        return AbstractC0023aw.m587e(1577) ? 163 : 0;
    }

    /* renamed from: g */
    private static final C0013am m161g() {
        boolean zM1056C;
        C0054y c0054y;
        int iM586d = 1 + AbstractC0023aw.m586d(242);
        AbstractC0023aw.m594c(2573, iM586d == 1 ? 1 : 12);
        C0013am c0013amM75b = AbstractC0004ad.m75b(2571);
        int i = c0013amM75b.f114q - 1;
        if (!AbstractC0023aw.m587e(99)) {
            boolean z = !AbstractC0023aw.m587e(98);
            AbstractC0037h abstractC0037h = C0008ah.f54k;
            Vector vectorM445W = abstractC0037h == null ? C0015ao.m445W() : abstractC0037h.m1078P();
            Vector vector = vectorM445W;
            int iM353a = C0015ao.m353a(vectorM445W);
            for (int i2 = 0; i2 < iM353a; i2++) {
                AbstractC0041l abstractC0041l = (AbstractC0041l) vector.elementAt(i2);
                if (!abstractC0041l.mo142k() && (abstractC0041l.m1240G() || abstractC0041l.mo143m() || (!abstractC0041l.mo142k() && (z || (((zM1056C = abstractC0041l.f369o.m1056C()) && abstractC0041l.f371p) || (!zM1056C && abstractC0041l.mo990d())))))) {
                    c0013amM75b.m225a(abstractC0041l.mo138b().m908a(iM586d, i / iM586d));
                }
            }
            C0040k.m1212a(vector);
        } else if (AbstractC0023aw.m587e(100)) {
            int i3 = i / iM586d;
            boolean zM587e = AbstractC0023aw.m587e(101);
            boolean z2 = !AbstractC0023aw.m587e(98);
            Vector vectorM1213g = C0040k.m1213g();
            Vector vectorM446d = C0015ao.m446d(C0008ah.f54k);
            int size = vectorM446d.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                AbstractC0046q abstractC0046q = (AbstractC0046q) vectorM446d.elementAt(size);
                String str = abstractC0046q.f398f;
                int size2 = vectorM1213g.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        c0054y = null;
                        break;
                    }
                    C0054y c0054y2 = (C0054y) vectorM1213g.elementAt(size2);
                    if (c0054y2.f398f.equals(str)) {
                        c0054y = c0054y2;
                        break;
                    }
                }
                C0054y c0054y3 = c0054y;
                if (c0054y == null) {
                    C0054y c0054y4 = new C0054y(abstractC0046q, vectorM1213g.size());
                    c0054y3 = c0054y4;
                    vectorM1213g.addElement(c0054y4);
                }
                Vector vector2 = abstractC0046q.f397e;
                int size3 = vector2.size();
                while (true) {
                    size3--;
                    if (size3 < 0) {
                        break;
                    }
                    c0054y3.m1401b(vector2.elementAt(size3));
                }
            }
            C0040k.m1212a(vectorM446d);
            int iM353a2 = C0015ao.m353a(vectorM1213g);
            for (int i4 = 0; i4 < iM353a2; i4++) {
                AbstractC0046q abstractC0046q2 = (AbstractC0046q) vectorM1213g.elementAt(i4);
                boolean z3 = false;
                if (zM587e || !abstractC0046q2.m1398o()) {
                    c0013amM75b.m225a(abstractC0046q2.m1395f(-1).m908a(iM586d, i));
                    z3 = true;
                }
                if (abstractC0046q2.m1398o()) {
                    Vector vector3 = abstractC0046q2.f397e;
                    int iM353a3 = C0015ao.m353a(vector3);
                    for (int i5 = 0; i5 < iM353a3; i5++) {
                        AbstractC0041l abstractC0041l2 = (AbstractC0041l) vector3.elementAt(i5);
                        if (m162a(z2, abstractC0041l2)) {
                            if (!z3) {
                                c0013amM75b.m225a(abstractC0046q2.m1395f(-1).m908a(iM586d, i));
                                z3 = true;
                            }
                            c0013amM75b.m225a(abstractC0041l2.mo138b().m908a(iM586d, i3));
                        }
                    }
                }
            }
            C0040k.m1212a(vectorM1213g);
            C0054y c0054y5 = null;
            C0054y c0054y6 = null;
            C0054y c0054y7 = null;
            C0054y c0054y8 = null;
            int iM433Q = C0015ao.m433Q();
            while (true) {
                iM433Q--;
                if (iM433Q < 0) {
                    break;
                }
                AbstractC0037h abstractC0037hM434I = C0015ao.m434I(iM433Q);
                AbstractC0037h abstractC0037h2 = C0008ah.f54k;
                if (abstractC0037h2 == null || abstractC0037h2 == abstractC0037hM434I) {
                    Vector vectorMo720O = abstractC0037hM434I.mo720O();
                    int size4 = vectorMo720O.size();
                    int i6 = size4;
                    if (size4 > 0) {
                        if (c0054y8 == null) {
                            c0054y8 = new C0054y(abstractC0037hM434I.f338H, -4);
                        }
                        while (true) {
                            i6--;
                            if (i6 < 0) {
                                break;
                            }
                            c0054y8.m1401b(vectorMo720O.elementAt(i6));
                        }
                    }
                    C0040k.m1212a(vectorMo720O);
                    Vector vectorM1077N = abstractC0037hM434I.m1077N();
                    int size5 = vectorM1077N.size();
                    int i7 = size5;
                    if (size5 > 0) {
                        if (c0054y5 == null) {
                            c0054y5 = new C0054y(abstractC0037hM434I.f336F, -1);
                        }
                        while (true) {
                            i7--;
                            if (i7 < 0) {
                                break;
                            }
                            c0054y5.m1401b(vectorM1077N.elementAt(i7));
                        }
                    }
                    C0040k.m1212a(vectorM1077N);
                    Vector vectorM1079Q = abstractC0037hM434I.m1079Q();
                    int size6 = vectorM1079Q.size();
                    int i8 = size6;
                    if (size6 > 0) {
                        if (c0054y6 == null) {
                            c0054y6 = new C0054y(abstractC0037hM434I.f334D, -2);
                        }
                        while (true) {
                            i8--;
                            if (i8 < 0) {
                                break;
                            }
                            c0054y6.m1401b(vectorM1079Q.elementAt(i8));
                        }
                    }
                    C0040k.m1212a(vectorM1079Q);
                    Vector vectorM1076M = abstractC0037hM434I.m1076M();
                    int size7 = vectorM1076M.size();
                    int i9 = size7;
                    if (size7 > 0) {
                        if (c0054y7 == null) {
                            c0054y7 = new C0054y(abstractC0037hM434I.f335E, -3);
                        }
                        while (true) {
                            i9--;
                            if (i9 < 0) {
                                break;
                            }
                            c0054y7.m1401b(vectorM1076M.elementAt(i9));
                        }
                    }
                    C0040k.m1212a(vectorM1076M);
                }
            }
            if (c0054y8 != null) {
                Vector vector4 = c0054y8.f397e;
                int iM353a4 = C0015ao.m353a(vector4);
                c0013amM75b.m225a(c0054y8.m1395f(iM353a4).m908a(iM586d, i));
                if (c0054y8.m1398o()) {
                    for (int i10 = 0; i10 < iM353a4; i10++) {
                        c0013amM75b.m225a(((AbstractC0041l) vector4.elementAt(i10)).mo138b().m908a(iM586d, i3));
                    }
                    C0040k.m1212a(vector4);
                }
            }
            if (c0054y5 != null) {
                Vector vector5 = c0054y5.f397e;
                int iM353a5 = C0015ao.m353a(vector5);
                c0013amM75b.m225a(c0054y5.m1395f(iM353a5).m908a(iM586d, i));
                if (c0054y5.m1398o()) {
                    for (int i11 = 0; i11 < iM353a5; i11++) {
                        c0013amM75b.m225a(((AbstractC0041l) vector5.elementAt(i11)).mo138b().m908a(iM586d, i3));
                    }
                    C0040k.m1212a(vector5);
                }
            }
            if (c0054y7 != null) {
                Vector vector6 = c0054y7.f397e;
                int iM353a6 = C0015ao.m353a(vector6);
                c0013amM75b.m225a(c0054y7.m1395f(iM353a6).m908a(iM586d, i));
                if (c0054y7.m1398o()) {
                    for (int i12 = 0; i12 < iM353a6; i12++) {
                        c0013amM75b.m225a(((AbstractC0041l) vector6.elementAt(i12)).mo138b().m908a(iM586d, i3));
                    }
                    C0040k.m1212a(vector6);
                }
            }
            if (c0054y6 != null) {
                Vector vector7 = c0054y6.f397e;
                int iM353a7 = C0015ao.m353a(vector7);
                c0013amM75b.m225a(c0054y6.m1395f(iM353a7).m908a(iM586d, i));
                if (c0054y6.m1398o()) {
                    for (int i13 = 0; i13 < iM353a7; i13++) {
                        c0013amM75b.m225a(((AbstractC0041l) vector7.elementAt(i13)).mo138b().m908a(iM586d, i3));
                    }
                    C0040k.m1212a(vector7);
                }
            }
        } else {
            int i14 = i / iM586d;
            Vector vectorM446d2 = C0015ao.m446d(C0008ah.f54k);
            int iM353a8 = C0015ao.m353a(vectorM446d2);
            boolean zM587e2 = AbstractC0023aw.m587e(101);
            boolean z4 = !AbstractC0023aw.m587e(98);
            for (int i15 = 0; i15 < iM353a8; i15++) {
                AbstractC0046q abstractC0046q3 = (AbstractC0046q) vectorM446d2.elementAt(i15);
                boolean z5 = false;
                if (zM587e2 || !abstractC0046q3.m1398o()) {
                    c0013amM75b.m225a(abstractC0046q3.m1395f(-1).m908a(iM586d, i));
                    z5 = true;
                }
                if (abstractC0046q3.m1398o()) {
                    Vector vector8 = abstractC0046q3.f397e;
                    int iM353a9 = C0015ao.m353a(vector8);
                    for (int i16 = 0; i16 < iM353a9; i16++) {
                        AbstractC0041l abstractC0041l3 = (AbstractC0041l) vector8.elementAt(i16);
                        if (m162a(z4, abstractC0041l3)) {
                            if (!z5) {
                                c0013amM75b.m225a(abstractC0046q3.m1395f(-1).m908a(iM586d, i));
                                z5 = true;
                            }
                            c0013amM75b.m225a(abstractC0041l3.mo138b().m908a(iM586d, i14));
                        }
                    }
                }
            }
            C0040k.m1212a(vectorM446d2);
            int iM433Q2 = C0015ao.m433Q();
            int i17 = iM433Q2;
            while (true) {
                i17--;
                if (i17 < 0) {
                    break;
                }
                AbstractC0037h abstractC0037hM434I2 = C0015ao.m434I(i17);
                AbstractC0037h abstractC0037h3 = C0008ah.f54k;
                if (abstractC0037h3 == null || abstractC0037h3 == abstractC0037hM434I2) {
                    AbstractC0046q abstractC0046q4 = abstractC0037hM434I2.f338H;
                    Vector vectorMo720O2 = abstractC0037hM434I2.mo720O();
                    int size8 = vectorMo720O2.size();
                    if (size8 > 0) {
                        c0013amM75b.m225a(abstractC0046q4.m1395f(size8).m908a(iM586d, i));
                        if (abstractC0046q4.m1398o()) {
                            C0015ao.m353a(vectorMo720O2);
                            for (int i18 = 0; i18 < size8; i18++) {
                                c0013amM75b.m225a(((AbstractC0041l) vectorMo720O2.elementAt(i18)).mo138b().m908a(iM586d, i14));
                            }
                        }
                    }
                    C0040k.m1212a(vectorMo720O2);
                }
            }
            int i19 = iM433Q2;
            while (true) {
                i19--;
                if (i19 < 0) {
                    break;
                }
                AbstractC0037h abstractC0037hM434I3 = C0015ao.m434I(i19);
                AbstractC0037h abstractC0037h4 = C0008ah.f54k;
                if (abstractC0037h4 == null || abstractC0037h4 == abstractC0037hM434I3) {
                    AbstractC0046q abstractC0046q5 = abstractC0037hM434I3.f336F;
                    Vector vectorM1077N2 = abstractC0037hM434I3.m1077N();
                    int size9 = vectorM1077N2.size();
                    if (size9 > 0) {
                        c0013amM75b.m225a(abstractC0046q5.m1395f(size9).m908a(iM586d, i));
                        if (abstractC0046q5.m1398o()) {
                            C0015ao.m353a(vectorM1077N2);
                            for (int i20 = 0; i20 < size9; i20++) {
                                c0013amM75b.m225a(((AbstractC0041l) vectorM1077N2.elementAt(i20)).mo138b().m908a(iM586d, i14));
                            }
                        }
                    }
                    C0040k.m1212a(vectorM1077N2);
                }
            }
            int i21 = iM433Q2;
            while (true) {
                i21--;
                if (i21 < 0) {
                    break;
                }
                AbstractC0037h abstractC0037hM434I4 = C0015ao.m434I(i21);
                AbstractC0037h abstractC0037h5 = C0008ah.f54k;
                if (abstractC0037h5 == null || abstractC0037h5 == abstractC0037hM434I4) {
                    AbstractC0046q abstractC0046q6 = abstractC0037hM434I4.f334D;
                    Vector vectorM1079Q2 = abstractC0037hM434I4.m1079Q();
                    int size10 = vectorM1079Q2.size();
                    if (size10 > 0) {
                        c0013amM75b.m225a(abstractC0046q6.m1395f(size10).m908a(iM586d, i));
                        if (abstractC0046q6.m1398o()) {
                            C0015ao.m353a(vectorM1079Q2);
                            for (int i22 = 0; i22 < size10; i22++) {
                                c0013amM75b.m225a(((AbstractC0041l) vectorM1079Q2.elementAt(i22)).mo138b().m908a(iM586d, i14));
                            }
                        }
                    }
                    C0040k.m1212a(vectorM1079Q2);
                }
            }
            int i23 = iM433Q2;
            while (true) {
                i23--;
                if (i23 < 0) {
                    break;
                }
                AbstractC0037h abstractC0037hM434I5 = C0015ao.m434I(i23);
                AbstractC0037h abstractC0037h6 = C0008ah.f54k;
                if (abstractC0037h6 == null || abstractC0037h6 == abstractC0037hM434I5) {
                    AbstractC0046q abstractC0046q7 = abstractC0037hM434I5.f335E;
                    Vector vectorM1076M2 = abstractC0037hM434I5.m1076M();
                    int size11 = vectorM1076M2.size();
                    if (size11 > 0) {
                        c0013amM75b.m225a(abstractC0046q7.m1395f(size11).m908a(iM586d, i));
                        if (abstractC0046q7.m1398o()) {
                            C0015ao.m353a(vectorM1076M2);
                            for (int i24 = 0; i24 < size11; i24++) {
                                c0013amM75b.m225a(((AbstractC0041l) vectorM1076M2.elementAt(i24)).mo138b().m908a(iM586d, i14));
                            }
                        }
                    }
                    C0040k.m1212a(vectorM1076M2);
                }
            }
        }
        C0008ah.m178j();
        return c0013amM75b.m218a();
    }

    /* renamed from: a */
    private static final boolean m162a(boolean z, AbstractC0041l abstractC0041l) {
        return ((!abstractC0041l.m1240G() && !z && !abstractC0041l.f371p) || abstractC0041l.mo142k() || abstractC0041l.mo144l() || abstractC0041l.mo143m() || abstractC0041l.mo990d() || abstractC0041l.mo996n()) ? false : true;
    }
}
