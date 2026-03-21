package p000;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* renamed from: ay */
/* loaded from: MobileAgent_3.9.jar:ay.class */
public abstract class AbstractC0025ay {

    /* renamed from: a */
    public static int f193a;

    /* renamed from: b */
    public static int f194b;

    /* renamed from: c */
    public static long f195c;

    /* renamed from: d */
    public static long f196d;

    /* renamed from: e */
    public static long f197e;

    /* renamed from: f */
    public static long f198f;

    /* renamed from: g */
    public static Object f199g;

    /* renamed from: h */
    public static boolean f200h;

    /* renamed from: i */
    public static boolean f201i;

    /* renamed from: j */
    public static C0014an f202j;

    /* renamed from: k */
    public static ListItem f203k;

    /* renamed from: l */
    public static boolean f204l;

    /* renamed from: m */
    public static Vector f205m;

    /* renamed from: u */
    private static int f206u;

    /* renamed from: v */
    private static long f207v;

    /* renamed from: n */
    public static int f208n;

    /* renamed from: o */
    public static long f209o;

    /* renamed from: w */
    private static GeoRegion f210w;

    /* renamed from: p */
    public static boolean f211p;

    /* renamed from: q */
    public static boolean f212q;

    /* renamed from: r */
    public static long f213r;

    /* renamed from: s */
    public static int f214s;

    /* renamed from: t */
    public static int f215t;

    /* renamed from: a */
    public static final void m646a() {
        AppState.m599a(1551, false);
        f200h = true;
    }

    /* renamed from: f */
    private static final Image m647f() {
        Image imageM615n = AppState.m615n(1363);
        if (imageM615n != null) {
            return imageM615n;
        }
        Image imageCreateImage = Image.createImage(128, 128);
        Graphics graphics = imageCreateImage.getGraphics();
        graphics.setColor(13158600);
        int i = 0;
        int i2 = 0;
        while (i2 < 128) {
            for (int i3 = i; i3 < 128; i3 += 4) {
                graphics.fillRect(i2, i3, 2, 2);
            }
            i2 += 2;
            i ^= 2;
        }
        AppState.f177b[1363] = imageCreateImage;
        return imageCreateImage;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v435, resolved type: p */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:456:0x1094  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void m648b() {
        long j = 0;
        int i;
        int i2;
        int i3 = 0;
        ListItem interfaceC0044o;
        Vector vectorM614m;
        int size;
        Image imageM1023b;
        int iM271d;
        Image imageM1023b2;
        int i4;
        Vector vector;
        int size2;
        Image imageM1139a;
        boolean z;
        if (f202j != null) {
            C0014an c0014an = f202j;
            if (c0014an.f142j) {
                c0014an.f142j = false;
                z = true;
            } else {
                z = false;
            }
            if (z) {
                f200h = true;
            }
        }
        if (C0036g.m1039l()) {
            f200h = true;
        }
        if (f200h) {
            int i5 = (int) (f197e - (f193a / 2));
            int i6 = (int) (f198f - (f194b / 2));
            int i7 = (int) (f197e + (f193a / 2));
            int i8 = (int) (f198f + (f194b / 2));
            int i9 = i5 < 0 ? (i5 / 128) - 1 : i5 / 128;
            int i10 = i6 < 0 ? (i6 / 128) - 1 : i6 / 128;
            int i11 = i7 < 0 ? (i7 / 128) - 1 : i7 / 128;
            int i12 = i8 < 0 ? (i8 / 128) - 1 : i8 / 128;
            int i13 = (int) ((((i9 << 7) + 64) - f197e) + (f193a / 2));
            int i14 = (int) (f194b - ((((i10 << 7) + 64) - f198f) + (f194b / 2)));
            Vector vectorM1213g = C0040k.m1213g();
            Graphics graphics = AppState.m615n(1364).getGraphics();
            int iM586d = AppState.m586d(39);
            for (int i15 = i9; i15 <= i11; i15++) {
                for (int i16 = i10; i16 <= i12; i16++) {
                    C0034e c0034e = new C0034e(1, iM586d, i15, i16);
                    C0034e c0034e2 = null;
                    vectorM1213g.addElement(c0034e);
                    if (AppState.m587e(277) && iM586d > 8 && AppState.m587e(41) && StringUtils.m43a(f196d, f195c)) {
                        C0034e c0034e3 = new C0034e(3, iM586d, i15, i16);
                        c0034e2 = c0034e3;
                        vectorM1213g.addElement(c0034e3);
                    }
                    Image imageM20a = StringUtils.m20a(c0034e);
                    Image imageM20a2 = c0034e2 != null ? StringUtils.m20a(c0034e2) : null;
                    if (imageM20a == null) {
                        imageM20a = m647f();
                    }
                    graphics.drawImage(imageM20a, i13 + (128 * (i15 - i9)), i14 - (128 * (i16 - i10)), 3);
                    if (c0034e2 != null && imageM20a != m647f()) {
                        if (imageM20a2 != null) {
                            graphics.drawImage(imageM20a2, i13 + (128 * (i15 - i9)), i14 - (128 * (i16 - i10)), 3);
                        } else if (imageM20a2 == null && !AppState.m614m(1396).contains(c0034e2)) {
                            int i17 = i13 + (128 * (i15 - i9));
                            int i18 = i14 - (128 * (i16 - i10));
                            int color = graphics.getColor();
                            graphics.setColor(6579300);
                            graphics.setStrokeStyle(1);
                            graphics.drawRect((i17 - 64) + 2, (i18 - 64) + 2, 124, 124);
                            graphics.setStrokeStyle(0);
                            graphics.setColor(color);
                            if (AppState.m587e(1535) && AppState.m587e(1414)) {
                                int[] iArr = new int[128];
                                int i19 = 128;
                                while (true) {
                                    i19--;
                                    if (i19 < 0) {
                                        break;
                                    } else {
                                        iArr[i19] = 1006632960;
                                    }
                                }
                                int i20 = 128;
                                while (true) {
                                    i20--;
                                    if (i20 < 0) {
                                        break;
                                    } else {
                                        graphics.drawRGB(iArr, 0, 128, i17 - 64, (i18 - 64) + i20, 128, 1, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Vector vectorM614m2 = AppState.m614m(1383);
            int size3 = vectorM1213g.size();
            for (int i21 = 0; i21 < size3; i21++) {
                C0034e c0034e4 = (C0034e) vectorM1213g.elementAt(i21);
                if (!vectorM614m2.contains(c0034e4)) {
                    int i22 = c0034e4.f281a;
                    if (i22 == 1) {
                        AppState.m595d(250, 1);
                    } else if (i22 == 3) {
                        AppState.m595d(251, 1);
                    }
                    vectorM614m2.addElement(c0034e4);
                }
            }
            int size4 = vectorM614m2.size();
            while (true) {
                size4--;
                if (size4 < 0) {
                    break;
                } else if (!vectorM1213g.contains(vectorM614m2.elementAt(size4))) {
                    vectorM614m2.removeElementAt(size4);
                }
            }
            Vector vectorM614m3 = AppState.m614m(1397);
            synchronized (vectorM614m3) {
                vectorM614m3.removeAllElements();
                int size5 = vectorM1213g.size();
                for (int i23 = 0; i23 < size5; i23++) {
                    vectorM614m3.addElement(vectorM1213g.elementAt(i23));
                }
                C0040k.m1212a(vectorM1213g);
            }
            Vector vectorM614m4 = AppState.m614m(1402);
            synchronized (vectorM614m4) {
                int size6 = vectorM614m4.size();
                if (size6 > 0) {
                    String str = null;
                    int i24 = 0;
                    int i25 = size6;
                    while (true) {
                        i25--;
                        if (i25 < 0) {
                            break;
                        }
                        Object[] objArr = (Object[]) vectorM614m4.elementAt(i25);
                        int iIntValue = ((Integer) objArr[0]).intValue();
                        if (iIntValue > i24) {
                            i24 = iIntValue;
                            str = (String) objArr[1];
                        }
                    }
                    Font font = graphics.getFont();
                    int color2 = graphics.getColor();
                    Font fontM625m = AppState.m625m();
                    graphics.setFont(fontM625m);
                    int iM586d2 = AppState.m586d(72);
                    graphics.setColor(AppState.m586d(5050 + iM586d2));
                    int iStringWidth = fontM625m.stringWidth(str) + 10;
                    int iM586d3 = AppState.m586d(1450);
                    graphics.fillRoundRect(5, 5, iStringWidth, iM586d3, 10, 10);
                    graphics.setColor(AppState.m586d(iM586d2 + 4914));
                    graphics.drawRoundRect(5, 5, iStringWidth, iM586d3, 10, 10);
                    graphics.drawString(str, 10, 5, 20);
                    graphics.setFont(font);
                    graphics.setColor(color2);
                }
            }
            long j2 = f197e;
            long j3 = f198f;
            int i26 = f193a;
            int i27 = f194b;
            if (AppState.m587e(277) && !C0036g.m1033j() && iM586d >= 9 && (vector = C0036g.f310a) != null && (size2 = vector.size()) != 0) {
                long j4 = (j2 - (i26 / 2)) / 32;
                long j5 = (j3 - (i26 / 2)) / 32;
                long j6 = (j2 + (i26 / 2)) / 32;
                long j7 = (j3 + (i26 / 2)) / 32;
                int i28 = ((int) (j6 - j4)) + 1;
                int[] iArr2 = new int[i28 * (((int) (j7 - j5)) + 1)];
                String str2 = null;
                int i29 = 0;
                int height = 0;
                for (int i30 = 0; i30 < size2; i30++) {
                    Object[] objArr2 = (Object[]) vector.elementAt(i30);
                    if (!C0039j.f355f.contains((String) objArr2[0])) {
                        long j8 = ((long[]) objArr2[1])[0];
                        long j9 = ((long[]) objArr2[1])[1];
                        long jM317a = C0015ao.m317a(j8, iM586d);
                        long jM317a2 = C0015ao.m317a(j9, iM586d);
                        int i31 = (int) (jM317a / 32);
                        int i32 = (int) (jM317a2 / 32);
                        if (i31 >= j4 && i31 <= j6 && i32 >= j5 && i32 <= j7 && (imageM1139a = C0039j.m1139a((String) objArr2[0])) != null) {
                            int i33 = (int) (i31 - j4);
                            int i34 = (int) (i32 - j5);
                            if (iArr2[(i34 * i28) + i33] == 0) {
                                int i35 = (int) ((i26 / 2) + (jM317a - j2));
                                int i36 = (int) ((i27 / 2) + (j3 - jM317a2));
                                graphics.drawImage(imageM1139a, i35, i36, 3);
                                iArr2[(i34 * i28) + i33] = 1;
                                if (str2 == null && Utils.m505a(j2 - jM317a) < 20 && Utils.m505a(j3 - jM317a2) < 20) {
                                    String str3 = (String) objArr2[2];
                                    if (Utils.m535l(str3)) {
                                        str2 = str3;
                                        i29 = i35;
                                        height = (i36 - (imageM1139a.getHeight() / 2)) + 2;
                                    }
                                }
                            }
                        }
                    }
                }
                if (str2 != null) {
                    AbstractC0030bc.m831a(graphics, str2, AppState.m625m(), i26 - 40, i29, height);
                }
            }
            C0014an c0014an2 = f202j;
            long j10 = f197e;
            long j11 = f198f;
            int i37 = f193a;
            int i38 = f194b;
            m660h();
            if (c0014an2 != null && c0014an2.f141i && c0014an2.m267a() != null && Utils.m505a(j10 - c0014an2.m270c(iM586d)) <= i37 / 2 && Utils.m505a(j11 - c0014an2.m271d(iM586d)) <= i38 / 2) {
                int iM270c = (int) ((i37 / 2) + (c0014an2.m270c(iM586d) - j10));
                int iM271d2 = (int) ((i38 / 2) + (j11 - c0014an2.m271d(iM586d)));
                if (c0014an2.f143k == 2) {
                    imageM1023b2 = C0036g.m1023b(25);
                    i4 = 1;
                } else {
                    imageM1023b2 = C0036g.m1023b(24);
                    i4 = 4;
                }
                graphics.drawImage(imageM1023b2, iM270c, iM271d2, 32 | i4);
                if (Utils.m505a(j10 - c0014an2.m270c(iM586d)) >= 20 || Utils.m505a(j11 - c0014an2.m271d(iM586d)) >= 20) {
                    m660h();
                } else {
                    m659a((ListItem) c0014an2);
                }
            }
            long j12 = f197e;
            long j13 = f198f;
            int i39 = f193a;
            int i40 = f194b;
            Enumeration enumerationM1167j = C0039j.m1167j();
            boolean z2 = false;
            C0014an c0014an3 = null;
            while (enumerationM1167j.hasMoreElements()) {
                C0014an c0014an4 = (C0014an) enumerationM1167j.nextElement();
                if (Utils.m505a(j12 - c0014an4.m270c(iM586d)) < i39 / 2 && Utils.m505a(j13 - c0014an4.m271d(iM586d)) < i40 / 2 && c0014an4.f141i) {
                    graphics.drawImage(C0036g.m1023b(18), (int) ((i39 / 2) + (c0014an4.m270c(iM586d) - j12)), (int) ((i40 / 2) + (j13 - c0014an4.m271d(iM586d))), 36);
                    if (Utils.m505a(j12 - c0014an4.m270c(iM586d)) < 20 && (iM271d = (int) (j13 - c0014an4.m271d(iM586d))) < 20 && iM271d > -10 && !z2) {
                        c0014an3 = c0014an4;
                        z2 = true;
                    }
                }
            }
            if (!m658g()) {
                if (z2) {
                    m659a((ListItem) c0014an3);
                } else {
                    m660h();
                }
            }
            long j14 = f197e;
            long j15 = f198f;
            int i41 = f193a;
            int i42 = f194b;
            if (AppState.m587e(276) && AppState.m587e(280) && !C0036g.m1033j() && (vectorM614m = AppState.m614m(1404)) != null && (size = vectorM614m.size()) != 0) {
                ListItem interfaceC0044o2 = null;
                int iM586d4 = AppState.m586d(39);
                for (int i43 = 0; i43 < size; i43++) {
                    ListItem interfaceC0044o3 = (ListItem) vectorM614m.elementAt(i43);
                    if (interfaceC0044o3.mo277s() && iM586d4 == interfaceC0044o3.mo280y()) {
                        long jMo282a = interfaceC0044o3.mo282a(iM586d);
                        long jMo283b = interfaceC0044o3.mo283b(iM586d);
                        int i44 = (int) ((i41 / 2) + (jMo282a - j14));
                        int i45 = (int) ((i42 / 2) + (j15 - jMo283b));
                        if (i44 > 0 && i44 < i41 && i45 > 0 && i45 < i42) {
                            if (interfaceC0044o3.mo276r() == 8) {
                                int i46 = ((C0045p) interfaceC0044o3).f393d;
                                imageM1023b = (i46 == 1 || i46 == 0) ? C0036g.m1023b(27) : C0036g.m1023b(28);
                            } else {
                                imageM1023b = C0036g.m1023b(23);
                            }
                            graphics.drawImage(imageM1023b, i44, i45, 3);
                        }
                        if (Utils.m505a(j14 - jMo282a) < 20 && Utils.m505a(j15 - jMo283b) < 20 && interfaceC0044o2 == null) {
                            interfaceC0044o2 = interfaceC0044o3;
                        }
                    }
                }
                if (!m658g()) {
                    if (interfaceC0044o2 != null) {
                        m659a(interfaceC0044o2);
                    } else {
                        m660h();
                    }
                }
            }
            long j16 = f197e;
            long j17 = f198f;
            int i47 = f193a;
            int i48 = f194b;
            if (AppState.m587e(276) && AppState.m587e(279) && !C0036g.m1033j()) {
                Vector vectorM448X = C0015ao.m448X();
                int size7 = vectorM448X.size();
                if (size7 > 0) {
                    long j18 = (j16 - (i47 / 2)) / 32;
                    long j19 = (j17 - (i47 / 2)) / 32;
                    long j20 = (j16 + (i47 / 2)) / 32;
                    long j21 = (j17 + (i47 / 2)) / 32;
                    ListItem interfaceC0044o4 = C0039j.f358h;
                    if (AbstractC0030bc.f253h == null || j18 < AbstractC0030bc.f249d || j19 < AbstractC0030bc.f250e || j20 > AbstractC0030bc.f251f || j21 > AbstractC0030bc.f252g) {
                        AbstractC0030bc.f249d = j18 - 10;
                        AbstractC0030bc.f250e = j19 - 10;
                        AbstractC0030bc.f251f = j20 + 10;
                        AbstractC0030bc.f252g = j21 + 10;
                        int i49 = ((int) (AbstractC0030bc.f251f - AbstractC0030bc.f249d)) + 1;
                        ListItem[] interfaceC0044oArr = new ListItem[i49 * (((int) (AbstractC0030bc.f252g - AbstractC0030bc.f250e)) + 1)];
                        AbstractC0030bc.f253h = interfaceC0044oArr;
                        for (int i50 = 0; i50 < size7; i50++) {
                            ListItem interfaceC0044o5 = (ListItem) vectorM448X.elementAt(i50);
                            if (interfaceC0044o5.mo277s() && interfaceC0044o4 != interfaceC0044o5) {
                                long jMo282a2 = interfaceC0044o5.mo282a(iM586d);
                                long jMo283b2 = interfaceC0044o5.mo283b(iM586d);
                                int i51 = (int) (jMo282a2 / 32);
                                int i52 = (int) (jMo283b2 / 32);
                                if (i51 >= AbstractC0030bc.f249d && i51 <= AbstractC0030bc.f251f && i52 >= AbstractC0030bc.f250e && i52 <= AbstractC0030bc.f252g) {
                                    int i53 = (int) (i51 - AbstractC0030bc.f249d);
                                    int i54 = (int) (i52 - AbstractC0030bc.f250e);
                                    ListItem interfaceC0044o6 = interfaceC0044oArr[(i54 * i49) + i53];
                                    if (interfaceC0044o6 == null) {
                                        interfaceC0044oArr[(i54 * i49) + i53] = interfaceC0044o5;
                                    } else if (interfaceC0044o6.mo276r() == 5) {
                                        ((Conversation) interfaceC0044o6).m1086a(interfaceC0044o5);
                                    } else if (interfaceC0044o6.mo276r() == 3) {
                                        Conversation c0038i = new Conversation();
                                        c0038i.m1086a(interfaceC0044o5);
                                        c0038i.m1086a(interfaceC0044o6);
                                        interfaceC0044oArr[(i54 * i49) + i53] = c0038i;
                                    }
                                }
                            }
                        }
                    }
                    ListItem[] interfaceC0044oArr2 = AbstractC0030bc.f253h;
                    ListItem interfaceC0044o7 = null;
                    int length = interfaceC0044oArr2.length;
                    while (true) {
                        length--;
                        if (length < 0) {
                            break;
                        }
                        ListItem interfaceC0044o8 = interfaceC0044oArr2[length];
                        if (interfaceC0044o8 != null) {
                            long jMo282a3 = interfaceC0044o8.mo282a(iM586d);
                            long jMo283b3 = interfaceC0044o8.mo283b(iM586d);
                            int i55 = (int) ((i47 / 2) + (jMo282a3 - j16));
                            int i56 = (int) ((i48 / 2) + (j17 - jMo283b3));
                            int iMo276r = interfaceC0044o8.mo276r();
                            Image imageM1023b3 = iMo276r == 3 ? C0036g.m1023b(26) : iMo276r == 5 ? C0036g.m1023b(23) : null;
                            Image image = imageM1023b3;
                            if (imageM1023b3 != null) {
                                graphics.drawImage(image, i55, i56, 3);
                            }
                            if (Utils.m505a(j16 - jMo282a3) < 20 && Utils.m505a(j17 - jMo283b3) < 20 && interfaceC0044o7 == null) {
                                interfaceC0044o7 = interfaceC0044o8;
                            }
                        }
                    }
                    if (!m658g()) {
                        if (interfaceC0044o7 != null) {
                            m659a(interfaceC0044o7);
                        } else {
                            m660h();
                        }
                    }
                }
                C0040k.m1212a(vectorM448X);
            }
            long j22 = f197e;
            long j23 = f198f;
            int i57 = f193a;
            int i58 = f194b;
            if (AppState.m587e(276) && AppState.m587e(278) && !C0036g.m1033j()) {
                AppState.m594c(1547, 0);
                Vector vectorM449Y = C0015ao.m449Y();
                int size8 = vectorM449Y.size();
                if (size8 != 0) {
                    C0028ba c0028ba = null;
                    for (int i59 = 0; i59 < size8; i59++) {
                        C0028ba c0028ba2 = (C0028ba) vectorM449Y.elementAt(i59);
                        if (c0028ba2.mo277s()) {
                            long jMo282a4 = c0028ba2.mo282a(iM586d);
                            long jMo283b4 = c0028ba2.mo283b(iM586d);
                            int i60 = (int) ((i57 / 2) + (jMo282a4 - j22));
                            int i61 = (int) ((i58 / 2) + (j23 - jMo283b4));
                            if (i60 > 0 && i60 < i57 && i61 > 0 && i61 < i58) {
                                graphics.drawImage(C0036g.m1023b(22), i60, i61, 3);
                            }
                            if (Utils.m505a(j22 - jMo282a4) < 20 && Utils.m505a(j23 - jMo283b4) < 20 && c0028ba == null) {
                                c0028ba = c0028ba2;
                            }
                        }
                    }
                    if (!m658g()) {
                        if (c0028ba != null) {
                            m659a(c0028ba);
                            if (c0028ba.f231g.f24l) {
                                AppState.m594c(1547, 1);
                            }
                            AppState.m617d(c0028ba);
                        } else {
                            m660h();
                        }
                    }
                    C0039j.m1158g();
                }
            }
            long j24 = f197e;
            long j25 = f198f;
            int i62 = f193a;
            int i63 = f194b;
            if (AppState.m587e(276) && !C0036g.m1033j() && (interfaceC0044o = C0039j.f358h) != null) {
                long jMo282a5 = interfaceC0044o.mo282a(iM586d);
                long jMo283b5 = interfaceC0044o.mo283b(iM586d);
                graphics.drawImage(C0036g.m1023b(26), (int) ((i62 / 2) + (jMo282a5 - j24)), (int) ((i63 / 2) + (j25 - jMo283b5)), 3);
                if (Utils.m505a(j24 - jMo282a5) < 20 && Utils.m505a(j25 - jMo283b5) < 20) {
                    m659a(interfaceC0044o);
                }
            }
            AbstractC0030bc.m836a(graphics, f197e, f198f, f196d, f195c, iM586d, f193a, f194b);
            AbstractC0030bc.m832a(graphics, f193a, f194b, iM586d, f197e, f198f, f203k);
            AbstractC0030bc.m829a(graphics, f197e, f198f, iM586d, f193a, f194b, f195c);
            int i64 = f193a / 2;
            int i65 = f194b / 2;
            if (f201i || AppState.m587e(1479)) {
                int color3 = graphics.getColor();
                graphics.setColor(0);
                graphics.fillRect(i64 - 1, i65 - 7, 2, 5);
                graphics.fillRect(i64 - 1, i65 + 2, 2, 5);
                graphics.fillRect(i64 - 7, i65 - 1, 5, 2);
                graphics.fillRect(i64 + 2, i65 - 1, 5, 2);
                graphics.setColor(color3);
            }
            long j26 = f196d;
            long j27 = f195c;
            GeoRegion c0053x = null;
            Vector vectorM614m5 = AppState.m614m(1389);
            int size9 = vectorM614m5.size();
            while (true) {
                size9--;
                if (size9 < 0) {
                    break;
                }
                GeoRegion c0053x2 = (GeoRegion) vectorM614m5.elementAt(size9);
                if (c0053x2.m1426a(j26, j27) && c0053x2.f428h != -1) {
                    if (c0053x != null) {
                        GeoRegion c0053x3 = c0053x;
                        if (c0053x2.f424d - c0053x2.f422b < c0053x3.f424d - c0053x3.f422b && c0053x2.f423c - c0053x2.f425e < c0053x3.f423c - c0053x3.f425e) {
                        }
                    }
                    c0053x = c0053x2;
                }
            }
            GeoRegion c0053x4 = c0053x;
            if (AppState.m587e(277)) {
                boolean zM587e = AppState.m587e(230);
                int clipWidth = zM587e ? graphics.getClipWidth() - 4 : 18;
                int i66 = -1;
                int i67 = 0;
                boolean z3 = false;
                int i68 = 0;
                if (c0053x4 != null) {
                    i66 = c0053x4.f428h;
                    i67 = c0053x4.f429i;
                    if (i66 >= 0) {
                        z3 = true;
                        i68 = i66 <= 45 ? 65280 : (i66 <= 45 || i66 >= 75) ? 16711680 : 16361985;
                    }
                }
                if (c0053x4 != null) {
                    i = c0053x4.f428h;
                    i2 = c0053x4.f429i;
                } else {
                    i = -1;
                    i2 = -1;
                }
                if (AbstractC0030bc.f241a != i || AbstractC0030bc.f242b != i2) {
                    int i69 = i66;
                    StringBuffer stringBufferAppend = C0040k.m1217h().append(AppState.m584b(974));
                    if (i69 < 0 || c0053x4 == null) {
                        i3 = 975;
                    } else {
                        stringBufferAppend.append(i69);
                        if (i69 <= 4 || i69 >= 21) {
                            i3 = i69 % 10 == 1 ? 977 : (i69 % 10 <= 1 || i69 % 10 >= 5) ? 976 : 978;
                        }
                    }
                    AppState.m601a(1384, (Object) C0040k.m1215a(stringBufferAppend.append(AppState.m584b(i3))));
                    AbstractC0030bc.f241a = i;
                    AbstractC0030bc.f242b = i2;
                }
                String strM584b = AppState.m584b(1384);
                Font font2 = graphics.getFont();
                int color4 = graphics.getColor();
                Font fontM625m2 = AppState.m625m();
                graphics.setFont(fontM625m2);
                int iM586d5 = AppState.m586d(1450);
                int iM586d6 = AppState.m586d(72);
                int iM586d7 = AppState.m586d(iM586d6 + 4914);
                int i70 = iM586d5 > 18 ? iM586d5 : 18;
                int clipHeight = (graphics.getClipHeight() - i70) - 1;
                if (zM587e) {
                    graphics.setColor(AppState.m586d(iM586d6 + 5050));
                    graphics.fillRoundRect(2, clipHeight, clipWidth, i70, 10, 10);
                }
                graphics.setColor(iM586d7);
                if (zM587e) {
                    graphics.drawRoundRect(2, clipHeight, clipWidth, i70, 10, 10);
                }
                int i71 = 0;
                if (z3) {
                    graphics.setColor(i68);
                    graphics.fillRoundRect(6, clipHeight + ((i70 - 10) / 2), 10, 10, 5, 5);
                    i71 = 10;
                    graphics.setColor(iM586d7);
                    graphics.drawRoundRect(6, clipHeight + ((i70 - 10) / 2), 10, 10, 5, 5);
                    if (i67 > 0 && zM587e) {
                        new C0012al(graphics).m216a(i67 == 1 ? 212 : 211, 20 + fontM625m2.stringWidth(strM584b) + 4, clipHeight + ((i70 - 16) / 2));
                    }
                    graphics.setColor(iM586d7);
                }
                if (zM587e) {
                    graphics.drawString(strM584b, i71 + 10, clipHeight + ((i70 - iM586d5) / 2), 20);
                }
                graphics.setColor(color4);
                graphics.setFont(font2);
            }
            AbstractC0030bc.m828a(graphics, iM586d, f195c);
            int i72 = f214s;
            int i73 = f215t;
            long j28 = f213r;
            j = j28;
            if (j28 != 0) {
                int iCurrentTimeMillis = (int) (System.currentTimeMillis() - j28);
                j = j28;
                if (iCurrentTimeMillis >= 200) {
                    int i74 = iCurrentTimeMillis < 300 ? 40 : iCurrentTimeMillis < 400 ? 80 : iCurrentTimeMillis < 500 ? 120 : 140;
                    int color5 = graphics.getColor();
                    graphics.setColor(AppState.m586d(5050 + AppState.m586d(72)));
                    int i75 = i74;
                    graphics.fillArc(i72 - (i74 / 2), i73 - (i74 / 2), i75, i74, 0, 360);
                    graphics.setColor(color5);
                    j = i75;
                }
            }
            AppState.m594c(1553, 1);
            if (f213r == 0) {
                f200h = false;
            }
        }
        if (C0015ao.m307b(11, 2000L)) {
            AppState.m594c(1549, 0);
        }
        Vector vector2 = f205m;
        synchronized (vector2) {
            if (f206u <= 5 && vector2.size() > 0) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                if (j - f207v > 80) {
                    long[] jArr = (long[]) vector2.elementAt(f206u);
                    m649a(jArr[0], jArr[1]);
                    f206u++;
                    f207v = jCurrentTimeMillis;
                }
            }
        }
        if (f208n > 0 && !f201i) {
            long jCurrentTimeMillis2 = System.currentTimeMillis();
            if (jCurrentTimeMillis2 - f209o > 80) {
                int iM586d8 = AppState.m586d(39);
                m649a(f196d, f195c + ((C0015ao.m315d(iM586d8) / C0015ao.m316e(iM586d8)) * 9));
                f208n -= 9;
                f209o = jCurrentTimeMillis2;
            }
        }
        if (AppState.m587e(277) && System.currentTimeMillis() - C0036g.f311b > 600000 && AppState.m587e(1576) && AppState.m587e(1414) && !C0015ao.m345u()) {
            C0036g.m1032i();
        }
    }

    /* renamed from: a */
    public static final void m649a(long j, long j2) {
        GeoRegion c0053x;
        if (j2 == f195c && j == f196d) {
            return;
        }
        int iM586d = AppState.m586d(39);
        synchronized (f199g) {
            f195c = j2;
            AppState.m597a(37, 37L);
            f196d = j;
            AppState.m597a(35, j);
            f197e = C0015ao.m317a(j, iM586d);
            f198f = C0015ao.m317a(j2, iM586d);
            GeoRegion c0053x2 = f210w;
            Vector vectorM614m = AppState.m614m(1389);
            int iM541c = Utils.m541c(vectorM614m);
            while (true) {
                iM541c--;
                if (iM541c < 0) {
                    c0053x = null;
                    break;
                }
                GeoRegion c0053x3 = (GeoRegion) vectorM614m.elementAt(iM541c);
                if (c0053x3.m1426a(j, j2)) {
                    c0053x = c0053x3;
                    break;
                }
            }
            GeoRegion c0053x4 = c0053x;
            if (c0053x2 != c0053x) {
                if (AppState.m587e(277)) {
                    C0036g.m1032i();
                }
                f210w = c0053x4;
            }
            m651a(m650b(iM586d));
        }
        f200h = true;
    }

    /* renamed from: b */
    private static final int m650b(int i) {
        int i2;
        if (StringUtils.m43a(f196d, f195c) || i <= 10) {
            return (f210w == null || i <= (i2 = f210w.f431k)) ? i : i2;
        }
        return 10;
    }

    /* renamed from: a */
    public static final void m651a(int i) {
        int iM586d = AppState.m586d(39);
        if (i == iM586d || i < 3 || i > 17) {
            return;
        }
        int iM650b = m650b(i);
        int i2 = iM650b != 8 ? iM650b : iM586d < iM650b ? 9 : 7;
        AppState.m594c(39, i2);
        f197e = C0015ao.m317a(f196d, i2);
        f198f = C0015ao.m317a(f195c, i2);
        m661e();
        f200h = true;
    }

    /* renamed from: a */
    public static final void m652a(boolean z) {
        if (f201i != z) {
            f201i = z;
            f200h = true;
        }
    }

    /* renamed from: a */
    public static final void m653a(C0014an c0014an) {
        if (AppState.m587e(1442)) {
            C0009ai.m183b(c0014an.f138f, c0014an.f139g);
        } else {
            C0009ai.m182a(c0014an.f138f, c0014an.f139g);
        }
        f200h = true;
        if (m656d()) {
            Conversation.m1129c();
        }
        c0014an.m269c();
        AppState.m594c(1443, 0);
    }

    /* renamed from: b */
    public static final void m654b(C0014an c0014an) {
        m646a();
        m649a(c0014an.f138f, c0014an.f139g);
        m651a(c0014an.f140h);
        c0014an.m268b();
        m661e();
    }

    /* renamed from: c */
    public static final String m655c() {
        if (f203k != null) {
            return f203k.mo273x();
        }
        return null;
    }

    /* renamed from: d */
    public static final boolean m656d() {
        return (C0009ai.f64i[0] > 0L ? 1 : (C0009ai.f64i[0] == 0L ? 0 : -1)) != 0 && (C0009ai.f64i[1] > 0L ? 1 : (C0009ai.f64i[1] == 0L ? 0 : -1)) != 0 && (C0009ai.f65j[0] > 0L ? 1 : (C0009ai.f65j[0] == 0L ? 0 : -1)) != 0 && (C0009ai.f65j[1] > 0L ? 1 : (C0009ai.f65j[1] == 0L ? 0 : -1)) != 0 && ((C0009ai.f64i[0] > C0009ai.f65j[0] ? 1 : (C0009ai.f64i[0] == C0009ai.f65j[0] ? 0 : -1)) != 0 || (C0009ai.f64i[1] > C0009ai.f65j[1] ? 1 : (C0009ai.f64i[1] == C0009ai.f65j[1] ? 0 : -1)) != 0);
    }

    /* renamed from: b */
    public static final void m657b(long j, long j2) {
        synchronized (f205m) {
            f205m.removeAllElements();
            f206u = 0;
            long j3 = f196d;
            long j4 = f195c;
            long j5 = (j - j3) / 5;
            long j6 = (j2 - j4) / 5;
            for (int i = 0; i < 5; i++) {
                f205m.addElement(new long[]{j3 + (j5 * i), j4 + (j6 * i)});
            }
            f205m.addElement(new long[]{j, j2});
            f207v = System.currentTimeMillis();
        }
    }

    /* renamed from: g */
    private static boolean m658g() {
        return f203k != null;
    }

    /* renamed from: a */
    private static void m659a(ListItem interfaceC0044o) {
        if (f204l) {
            return;
        }
        f203k = interfaceC0044o;
    }

    /* renamed from: h */
    private static void m660h() {
        if (f204l) {
            return;
        }
        f203k = null;
    }

    /* renamed from: e */
    public static final void m661e() {
        m652a(true);
        f204l = false;
        f208n = 0;
    }

    /* renamed from: c */
    private static final int m662c(int i) {
        return ((int) f197e) + (i - (f193a >> 1));
    }

    /* renamed from: d */
    private static final int m663d(int i) {
        return ((int) f198f) - (i - (f194b >> 1));
    }

    /* renamed from: a */
    public static final void m664a(int i, int i2) {
        int[] iArr;
        f213r = 0L;
        if (!f211p) {
            if (f203k != null && (iArr = AbstractC0030bc.f248c) != null && i > iArr[0] && i < iArr[0] + iArr[2] && i2 > iArr[1] - (iArr[3] / 2) && i2 < iArr[1] + (iArr[3] / 2)) {
                f212q = true;
                return;
            } else {
                int iM586d = AppState.m586d(39);
                m657b((int) C0015ao.m318a(m662c(i), iM586d), (int) C0015ao.m318a(m663d(i2), iM586d));
            }
        }
        f200h = true;
    }

    /* renamed from: b */
    public static final void m665b(int i, int i2) {
        f212q = true;
        f213r = 0L;
        int iM586d = AppState.m586d(39);
        m649a((int) C0015ao.m318a(m662c(i), iM586d), (int) C0015ao.m318a(m663d(i2), iM586d));
        f200h = true;
    }
}
