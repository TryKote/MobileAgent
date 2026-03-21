package p000;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;

/* renamed from: g */
/* loaded from: MobileAgent_3.9.jar:g.class */
public final class C0036g extends ContactGroup {

    /* renamed from: h */
    private int f309h;

    /* renamed from: a */
    public static Vector f310a;

    /* renamed from: b */
    public static long f311b;

    /* renamed from: c */
    public static long f312c;

    public C0036g(C0005ae c0005ae, int i, String str) {
        super(c0005ae);
        this.f309h = i;
        m1403c(str);
    }

    public C0036g(C0005ae c0005ae, ByteBuffer c0043n) {
        super(c0005ae);
        m1403c(c0043n.m1335e((String) null));
        this.f309h = c0043n.m1328e();
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                this.f399g = c0043n.m1340l();
                return;
            }
            m1401b((Object) new C0006af(c0005ae, c0043n));
        }
    }

    public C0036g() {
        super(null);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final void mo196a(ByteBuffer c0043n, boolean z) {
        c0043n.m1309b(this.f398f);
        c0043n.m1360p(this.f309h);
        super.mo196a(c0043n, z);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean mo198a() {
        return this.f309h <= 0;
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int mo197b() {
        return this.f309h;
    }

    /* renamed from: a */
    public static final ByteBuffer m1001a(C0028ba c0028ba, C0035f c0035f, String str, long j) {
        Object[] objArr = new Object[4];
        ByteBuffer c0043nM1308a = new ByteBuffer().m1360p(0).m1308a(c0035f.f297d);
        Hashtable hashtable = new Hashtable();
        int i = 78;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            String strM584b = AppState.m584b(i + 1063);
            hashtable.put(strM584b, StringUtils.m17c(strM584b.toLowerCase()));
        }
        String strM584b2 = AppState.m584b(592860);
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int length = str.length();
        int length2 = 0;
        while (length2 < length) {
            char cCharAt = str.charAt(length2);
            int iM1002a = m1002a(strM584b2, str, length2, hashtable);
            if (iM1002a < 0) {
                stringBufferM1217h.append(cCharAt);
            } else {
                if (iM1002a < 42) {
                    stringBufferM1217h.append(AppState.m584b(854972)).append(Utils.m501b(iM1002a)).append('>');
                } else {
                    stringBufferM1217h.append(AppState.m584b(658377)).append(iM1002a < 74 ? iM1002a + 258 : iM1002a == 74 ? 410 : iM1002a == 75 ? 412 : iM1002a == 76 ? 417 : 432).append(AppState.m584b(396261)).append(AppState.m584b(iM1002a + 1063)).append(AppState.m584b(592851));
                }
                length2 += AppState.m584b(iM1002a + 1063).length() - 1;
            }
            length2++;
        }
        objArr[0] = C0015ao.m321a(c0028ba, 4104, c0043nM1308a.m1309b(C0040k.m1215a(stringBufferM1217h)).m1360p(0));
        objArr[1] = C0034e.m967e(10);
        objArr[2] = c0035f;
        objArr[3] = new Long(j);
        return c0028ba.m719a(objArr);
    }

    /* renamed from: a */
    private static final int m1002a(String str, String str2, int i, Hashtable hashtable) {
        String strM584b;
        if (str2.length() <= 0 || str.indexOf(str2.charAt(i)) < 0) {
            return -1;
        }
        int i2 = 78;
        do {
            i2--;
            if (i2 >= 0) {
                strM584b = AppState.m584b(i2 + 1063);
                if (str2.indexOf(strM584b, i) == i) {
                    break;
                }
            } else {
                return -1;
            }
        } while (str2.indexOf((String) hashtable.get(strM584b), i) != i);
        return i2;
    }

    /* renamed from: p */
    private static void m1003p() {
        AppState.m597a(236, System.currentTimeMillis());
    }

    /* renamed from: c */
    public static final void m1004c() throws Throwable {
        while (true) {
            Thread.sleep(3072L);
            if (C0015ao.f151e) {
                throw new Throwable();
            }
            if (System.currentTimeMillis() - AppState.m598g(236) >= 7200000) {
                boolean z = false;
                Vector vectorM443V = C0015ao.m443V();
                int size = vectorM443V.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    Account abstractC0037h = (Account) vectorM443V.elementAt(size);
                    if (abstractC0037h.m1056C()) {
                        if (abstractC0037h instanceof C0028ba) {
                            z = false;
                            m1003p();
                            break;
                        }
                        z = true;
                    }
                }
                C0040k.m1212a(vectorM443V);
                if (z) {
                    m1007b(m1005e(m1006a(m1005e(AppState.m584b(1114895)))));
                }
            }
        }
    }

    /* renamed from: e */
    private static final C0039j m1005e(String str) throws Throwable {
        int iM1131a;
        C0039j c0039j = new C0039j(str);
        do {
            Thread.sleep(100L);
            iM1131a = c0039j.m1131a();
        } while (iM1131a == 1);
        if (iM1131a != 2) {
            c0039j.f349c = 3;
        }
        return c0039j;
    }

    /* renamed from: a */
    private static final String m1006a(C0039j c0039j) {
        int i;
        int i2;
        try {
            ByteBuffer c0043n = new ByteBuffer();
            do {
                Thread.sleep(100L);
                c0039j.m1132a(c0043n);
                i = c0043n.f384b;
                i2 = i;
            } while (i == 0);
            StringBuffer stringBufferM1217h = C0040k.m1217h();
            while (true) {
                int i3 = i2;
                i2 = i3 - 1;
                if (i3 <= 0) {
                    break;
                }
                char cM1344o = (char) c0043n.m1344o();
                if (Utils.m498a(cM1344o)) {
                    stringBufferM1217h.append(cM1344o);
                }
            }
            String strM1215a = C0040k.m1215a(stringBufferM1217h);
            if (c0039j != null) {
                c0039j.f349c = 3;
            }
            return strM1215a;
        } catch (RuntimeException th) {
            if (c0039j != null) {
                c0039j.f349c = 3;
            }
            throw th;
        } catch (Throwable th) {
            if (c0039j != null) {
                c0039j.f349c = 3;
            }
            throw new RuntimeException(th);
        }
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[]}, finally: {[CONST, IPUT, IF] complete} */
    /* renamed from: b */
    private static final void m1007b(C0039j c0039j) {
        ByteBuffer c0043nM1349s;
        try {
            String strM584b = AppState.m584b(655360);
            C0028ba c0028ba = new C0028ba(-1, strM584b, strM584b);
            c0028ba.f320p = c0039j;
            c0028ba.m1053d(C0015ao.m377a(c0028ba));
            ByteBuffer c0043n = new ByteBuffer();
            do {
                Thread.sleep(100L);
                c0039j.m1132a(c0043n);
                c0043nM1349s = c0043n.m1349s();
            } while (c0043nM1349s == null);
            if (c0043nM1349s.m1330h(12) == 4098) {
                m1003p();
                c0028ba.m1053d(C0015ao.m321a(c0028ba, 4216, new ByteBuffer().m1308a(c0028ba.f315k).m1308a(c0028ba.f316l).m1310c(1442808).m1308a(m1017d()).m1325a(m1016a(c0028ba))));
                Thread.sleep(5000L);
            }
        } catch (Throwable unused) {
        } finally {
            if (c0039j != null) {
                c0039j.f349c = 3;
            }
        }
    }

    /* renamed from: q */
    private static final int[] m1008q() {
        return (int[]) AppState.f177b[430];
    }

    /* renamed from: b */
    private static final int m1009b(int i, int i2) {
        return (i >>> i2) | (i << (32 - i2));
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [int] */
    /* renamed from: a */
    private static final void m1010a(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i >> 24);
        bArr[i2 + 1] = (byte) (i >>> 16);
        bArr[i2 + 2] = (byte) (i >>> 8);
        bArr[i2 + 3] = (byte) i;
    }

    /* renamed from: r */
    private static final Object[] m1011r() {
        Object[] objArr = {new int[10], C0040k.m1211a(128)};
        int[] iArr = (int[]) objArr[0];
        int[] iArrM1008q = m1008q();
        int i = 8;
        while (true) {
            i--;
            if (i < 0) {
                return objArr;
            }
            iArr[i] = iArrM1008q[i];
        }
    }

    /* renamed from: a */
    private static final Object[] m1012a(Object[] objArr, byte[] bArr, int i) {
        int[] iArr = (int[]) objArr[0];
        byte[] bArr2 = (byte[]) objArr[1];
        int i2 = iArr[9];
        int iM503b = Utils.m503b(i, 64 - i2);
        System.arraycopy(bArr, 0, bArr2, i2, iM503b);
        if (i2 + i < 64) {
            iArr[9] = i2 + i;
        } else {
            m1014a(objArr, bArr2, 0, 1);
            int i3 = i - iM503b;
            int i4 = i3 >> 6;
            m1014a(objArr, bArr, iM503b, i4);
            int i5 = iM503b + (i4 << 6);
            int i6 = i3 & 63;
            System.arraycopy(bArr, i5, bArr2, 0, i6);
            iArr[9] = i6;
            iArr[8] = iArr[8] + ((i4 + 1) << 6);
        }
        return objArr;
    }

    /* renamed from: c */
    private static final byte[] m1013c(Object[] objArr) {
        int[] iArr = (int[]) objArr[0];
        int i = iArr[9];
        int i2 = 55 < (i & 63) ? 2 : 1;
        int i3 = (iArr[8] + i) << 3;
        byte[] bArr = (byte[]) objArr[1];
        int i4 = i2 << 6;
        int i5 = i4;
        while (true) {
            i5--;
            if (i5 < i) {
                break;
            }
            bArr[i5] = 0;
        }
        bArr[i] = -128;
        m1010a(i3, bArr, i4 - 4);
        m1014a(objArr, bArr, 0, i2);
        byte[] bArr2 = new byte[32];
        int i6 = 8;
        while (true) {
            i6--;
            if (i6 < 0) {
                C0040k.m1209a(bArr);
                return bArr2;
            }
            m1010a(iArr[i6], bArr2, i6 << 2);
        }
    }

    /* renamed from: a */
    private static final void m1014a(Object[] objArr, byte[] bArr, int i, int i2) {
        int[] iArr = (int[]) objArr[0];
        int[] iArr2 = new int[64];
        int[] iArr3 = new int[8];
        int[] iArrM1008q = m1008q();
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = 0;
            do {
                int i5 = i + (i3 << 6) + (i4 << 2);
                iArr2[i4] = (bArr[i5] << 24) | ((bArr[i5 + 1] & 255) << 16) | ((bArr[i5 + 2] & 255) << 8) | (bArr[i5 + 3] & 255);
                i4++;
            } while (i4 < 16);
            do {
                int i6 = i4;
                int i7 = iArr2[i6 - 2];
                int iM1009b = ((m1009b(i7, 17) ^ m1009b(i7, 19)) ^ (i7 >>> 10)) + iArr2[i6 - 7];
                int i8 = iArr2[i6 - 15];
                iArr2[i6] = iM1009b + ((m1009b(i8, 7) ^ m1009b(i8, 18)) ^ (i8 >>> 3)) + iArr2[i6 - 16];
                i4++;
            } while (i4 < 64);
            int i9 = 8;
            while (true) {
                i9--;
                if (i9 < 0) {
                    break;
                } else {
                    iArr3[i9] = iArr[i9];
                }
            }
            int i10 = 0;
            do {
                int i11 = iArr3[7];
                int i12 = iArr3[4];
                int iM1009b2 = i11 + ((m1009b(i12, 6) ^ m1009b(i12, 11)) ^ m1009b(i12, 25));
                int i13 = iArr3[4];
                int i14 = iM1009b2 + ((i13 & iArr3[5]) ^ ((i13 ^ (-1)) & iArr3[6])) + iArrM1008q[i10 + 8] + iArr2[i10];
                int i15 = iArr3[0];
                int iM1009b3 = (m1009b(i15, 2) ^ m1009b(i15, 13)) ^ m1009b(i15, 22);
                int i16 = iArr3[0];
                int i17 = iArr3[1];
                int i18 = iArr3[2];
                iArr3[7] = iArr3[6];
                iArr3[6] = iArr3[5];
                iArr3[5] = iArr3[4];
                iArr3[4] = iArr3[3] + i14;
                iArr3[3] = iArr3[2];
                iArr3[2] = iArr3[1];
                iArr3[1] = iArr3[0];
                iArr3[0] = i14 + iM1009b3 + (((i16 & i17) ^ (i16 & i18)) ^ (i17 & i18));
                i10++;
            } while (i10 < 64);
            int i19 = 0;
            do {
                int i20 = i19;
                iArr[i20] = iArr[i20] + iArr3[i19];
                i19++;
            } while (i19 < 8);
        }
    }

    /* renamed from: a */
    public static final byte[] m1015a(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        int i4;
        byte[] bArrM1211a = C0040k.m1211a(64);
        byte[] bArrM1211a2 = C0040k.m1211a(64);
        if (i == 64) {
            i4 = 64;
        } else {
            if (i > 64) {
                i4 = 32;
                bArr = m1013c(m1012a(m1011r(), bArr, i));
            } else {
                i4 = i;
            }
            int i5 = 64;
            while (true) {
                i5--;
                if (i5 < i4) {
                    break;
                }
                bArrM1211a[i5] = 54;
                bArrM1211a2[i5] = 92;
            }
        }
        int i6 = i4;
        while (true) {
            i6--;
            if (i6 < 0) {
                Object[] objArrM1012a = m1012a(m1011r(), bArrM1211a, 64);
                C0040k.m1209a(bArrM1211a);
                Object[] objArrM1012a2 = m1012a(m1011r(), bArrM1211a2, 64);
                C0040k.m1209a(bArrM1211a2);
                Object[] objArr = {objArrM1012a, objArrM1012a2};
                m1012a((Object[]) objArr[0], bArr2, i2);
                return m1013c(m1012a((Object[]) objArr[1], m1013c((Object[]) objArr[0]), 32));
            }
            bArrM1211a[i6] = (byte) (bArr[i6] ^ 54);
            bArrM1211a2[i6] = (byte) (bArr[i6] ^ 92);
        }
    }

    /* renamed from: a */
    public static final ByteBuffer m1016a(C0028ba c0028ba) {
        ByteBuffer c0043nM1360p = new ByteBuffer().m1390v(515).m1360p(Utils.m510a((Object) Utils.m522f(AppState.m584b(222)))).m1390v(300).m1308a(Utils.m522f(AppState.m584b(223))).m1390v(513).m1360p(c0028ba.f326v).m1390v(335).m1308a(C0040k.m1215a(C0040k.m1217h().append(AppState.m586d(1528)).append('x').append(AppState.m586d(1529)))).m1390v(592).m1360p(AppState.m585c(251)).m1390v(573).m1360p(AppState.m585c(250)).m1390v(636).m1360p(AppState.m585c(290)).m1390v(514).m1360p(AppState.m585c(291)).m1390v(638).m1360p(AppState.m585c(292)).m1390v(639).m1360p(AppState.m585c(294)).m1390v(640).m1360p(AppState.m585c(293));
        Vector vectorM443V = C0015ao.m443V();
        int size = vectorM443V.size();
        while (true) {
            size--;
            if (size < 0) {
                C0040k.m1212a(vectorM443V);
                AppState.m619a(true);
                return c0043nM1360p;
            }
            Account abstractC0037h = (Account) vectorM443V.elementAt(size);
            if (!(abstractC0037h instanceof C0028ba)) {
                ByteBuffer c0043nM1390v = c0043nM1360p.m1390v(816);
                ByteBuffer c0043nM1360p2 = new ByteBuffer().m1390v(515).m1360p(Utils.m510a((Object) Utils.m522f(AppState.m584b(222)))).m1390v(300).m1308a(Utils.m522f(AppState.m584b(223))).m1390v(305).m1308a(abstractC0037h.f315k).m1390v(306).m1308a(AppState.m584b(abstractC0037h.mo110p())).m1390v(563).m1360p(abstractC0037h.f326v).m1390v(564).m1360p(abstractC0037h.f327w).m1390v(565).m1360p(abstractC0037h.f328x);
                abstractC0037h.mo924o();
                c0043nM1390v.m1327c(c0043nM1360p2);
            }
        }
    }

    /* renamed from: d */
    public static final String m1017d() {
        return new ByteBuffer().m1310c(986750).m1312e(2098527).m1312e(2097374).m1386c(4423776686951391594L).m1312e(2098526).m1385u(1030516845).m1312e(2098528).m1385u(1030712676).m1312e(2098529).m1385u(1953653104).m1386c(465624460605L).m1317c();
    }

    /* renamed from: s */
    private static final Object[] m1018s() {
        return (Object[]) AppState.f177b[1361];
    }

    /* renamed from: t */
    private static final int[] m1019t() {
        return (int[]) AppState.f177b[1362];
    }

    /* renamed from: e */
    public static final void m1020e() {
        synchronized (m1018s()) {
            AppState.m595d(1407, 1);
        }
    }

    /* renamed from: a */
    public static final void m1021a(int i) {
        Object[] objArrM1018s = m1018s();
        synchronized (objArrM1018s) {
            objArrM1018s[i] = null;
            objArrM1018s[i + 29] = null;
        }
    }

    /* renamed from: f */
    public static final void m1022f() {
        Object[] objArrM1018s = m1018s();
        synchronized (objArrM1018s) {
            int iM586d = AppState.m586d(1407);
            int[] iArrM1019t = m1019t();
            int i = 29;
            while (true) {
                i--;
                if (i >= 0) {
                    int i2 = iM586d - iArrM1019t[i];
                    if (i2 > 16) {
                        objArrM1018s[i] = null;
                        if (i2 > 32) {
                            C0040k.m1209a((byte[]) objArrM1018s[i + 29]);
                            objArrM1018s[i + 29] = null;
                        }
                    }
                }
            }
        }
    }

    /* renamed from: b */
    public static final Image m1023b(int i) {
        Object[] objArrM1018s = m1018s();
        synchronized (objArrM1018s) {
            m1019t()[i] = AppState.m586d(1407);
            if (objArrM1018s[i] != null) {
                return (Image) objArrM1018s[i];
            }
            try {
                byte[] bArr = (byte[]) objArrM1018s[i + 29];
                byte[] bArr2 = bArr;
                if (bArr == null) {
                    int i2 = i + 29;
                    byte[] bArrM1339k = new ByteBuffer(C0040k.m1221a(i < 26 ? 113724026151215L + (i << 8) : 29113350693019951L + (i << 16))).m1339k();
                    bArr2 = bArrM1339k;
                    objArrM1018s[i2] = bArrM1339k;
                }
                Image imageCreateImage = Image.createImage(bArr2, 0, bArr2.length);
                objArrM1018s[i] = imageCreateImage;
                return imageCreateImage;
            } catch (Throwable unused) {
                objArrM1018s[i + 29] = null;
                objArrM1018s[i] = null;
                return Image.createImage(1, 1);
            }
        }
    }

    /* renamed from: a */
    public static final ByteBuffer m1024a(C0028ba c0028ba, int i, String str, String str2, String str3, C0010aj c0010aj, boolean z) {
        Object[] objArr = new Object[6];
        objArr[0] = C0015ao.m321a(c0028ba, 4121, new ByteBuffer().m1360p(i).m1360p(c0010aj.f74a).m1308a(str).m1309b(str2).m1360p(0).m1318a(new String[]{c0028ba.f339I, str3}).m1360p(z ? 1 : 0));
        objArr[1] = C0034e.m967e(9);
        objArr[2] = str;
        objArr[3] = str2;
        objArr[4] = c0010aj;
        objArr[5] = C0034e.m967e(i);
        return c0028ba.m719a(objArr);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00e0  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void m1025a(String str, String str2, int i, int i2, String str3, int i3, int i4, CommandListener commandListener) {
        TextBox textBoxM1028h = null;
        if (str2 != null && str2.length() > i) {
            str2 = StringUtils.m13b(str2, i);
        }
        try {
        } catch (Throwable unused) {
            AppState.f177b[1259] = new TextBox(str, str2, i, i2);
        }
        if (!StringUtils.f1b) {
            throw new RuntimeException();
        }
        TextBox textBoxM1028h2 = m1028h();
        textBoxM1028h2.setTitle(AppState.f181d);
        textBoxM1028h2.setString(AppState.f181d);
        textBoxM1028h2.setCommandListener((CommandListener) null);
        textBoxM1028h2.setConstraints(i2);
        textBoxM1028h2.setTitle(str);
        if (str2 != null) {
            textBoxM1028h2.setString(str2);
        }
        textBoxM1028h2.setMaxSize(i);
        textBoxM1028h2.setInitialInputMode((String) null);
        m1029u();
        m1030v();
        try {
            textBoxM1028h = m1028h();
        } catch (Throwable unused2) {
        }
        if (StringUtils.m3a(424, str3)) {
            int iM586d = AppState.m586d(74);
            if (iM586d == 1) {
                textBoxM1028h.setInitialInputMode(AppState.m584b(426));
            } else if (iM586d == 2) {
                textBoxM1028h.setInitialInputMode(AppState.m584b(425));
            }
            AppState.m594c(1447, i3);
            Command command = new Command(AppState.m584b(i3), !AppState.m587e(65) ? 2 : 4, 0);
            m1029u();
            m1028h().addCommand(command);
            AppState.f177b[1260] = command;
            m1031g(1055);
            m1028h().setCommandListener(commandListener);
            AppState.m604b(m1028h());
        }
        textBoxM1028h.setInitialInputMode(str3);
        AppState.m594c(1447, i3);
        Command command2 = new Command(AppState.m584b(i3), !AppState.m587e(65) ? 2 : 4, 0);
        m1029u();
        m1028h().addCommand(command2);
        AppState.f177b[1260] = command2;
        m1031g(1055);
        m1028h().setCommandListener(commandListener);
        AppState.m604b(m1028h());
    }

    /* renamed from: g */
    public static final String m1026g() {
        try {
            return Utils.m522f(StringUtils.m17c(m1028h().getString()));
        } catch (Throwable unused) {
            return AppState.f181d;
        }
    }

    /* renamed from: a */
    public static final void m1027a(int i, int i2) {
        if (AppState.m586d(1448) == i) {
            m1031g(i2);
            AppState.m604b(m1028h());
        }
    }

    /* renamed from: h */
    public static final TextBox m1028h() {
        return (TextBox) AppState.f177b[1259];
    }

    /* renamed from: u */
    private static final void m1029u() {
        Command command = (Command) AppState.f177b[1260];
        if (null != command) {
            m1028h().removeCommand(command);
        }
        AppState.m591f(1260);
    }

    /* renamed from: v */
    private static final void m1030v() {
        Command command = (Command) AppState.f177b[1261];
        if (null != command) {
            m1028h().removeCommand(command);
        }
        AppState.m591f(1261);
    }

    /* renamed from: g */
    private static final void m1031g(int i) {
        AppState.m594c(1448, i);
        Command command = new Command(AppState.m584b(i), AppState.m587e(65) ? 4 : 2, 1);
        m1030v();
        m1028h().addCommand(command);
        AppState.f177b[1261] = command;
    }

    /* renamed from: i */
    public static final void m1032i() {
        f311b = System.currentTimeMillis();
        Vector vectorM1140a = C0039j.m1140a(1);
        long j = AbstractC0025ay.f196d;
        long j2 = AbstractC0025ay.f195c;
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int size = vectorM1140a.size();
        while (true) {
            size--;
            if (size < 0) {
                ByteBuffer c0043nM1385u = new ByteBuffer().m1310c(3216135).m1385u(15713);
                String strM1215a = C0040k.m1215a(stringBufferM1217h);
                new RunnableC0055z(15, c0043nM1385u.m1314d(strM1215a).m1385u(4022822).m1314d(new ByteBuffer().m1314d(strM1215a).m1310c(660328).m1365B().m1387H()).m1385u(4023078).m1383b(j).m1385u(4023334).m1383b(j2).m1317c());
                return;
            } else {
                stringBufferM1217h.append(vectorM1140a.elementAt(size));
                if (size > 0) {
                    stringBufferM1217h.append(',');
                }
            }
        }
    }

    /* renamed from: j */
    public static final boolean m1033j() {
        return System.currentTimeMillis() - f312c < 45000;
    }

    /* renamed from: a */
    private static final int[] m1034a(byte[] bArr, int i) {
        ByteBuffer c0043n = new ByteBuffer(C0040k.m1221a(24879), 4200);
        int[] iArr = new int[1060];
        System.arraycopy(Utils.m536a(c0043n.f383a), 0, iArr, 0, 1042);
        c0043n.m1301b();
        int i2 = 0;
        for (int i3 = 0; i3 < 18; i3++) {
            int i4 = 0;
            for (int i5 = 0; i5 < 4; i5++) {
                int i6 = i2;
                i2++;
                i4 = (i4 << 8) | (bArr[i6 % i] & 255);
            }
            iArr[i3 + 1024 + 18] = iArr[i3 + 1024] ^ i4;
        }
        long jM1035a = m1035a(iArr, 0, 0);
        iArr[1042] = (int) (jM1035a >>> 32);
        iArr[1043] = (int) jM1035a;
        int i7 = 2;
        do {
            long jM1035a2 = m1035a(iArr, iArr[((i7 + 1024) + 18) - 2], iArr[((i7 + 1024) + 18) - 1]);
            int i8 = i7;
            int i9 = i7 + 1;
            iArr[1042 + i8] = (int) (jM1035a2 >>> 32);
            i7 = i9 + 1;
            iArr[1042 + i9] = (int) jM1035a2;
        } while (i7 != 18);
        long jM1035a3 = m1035a(iArr, iArr[1058], iArr[1059]);
        iArr[0] = (int) (jM1035a3 >>> 32);
        iArr[1] = (int) jM1035a3;
        int i10 = 2;
        do {
            long jM1035a4 = m1035a(iArr, iArr[i10 - 2], iArr[i10 - 1]);
            int i11 = i10;
            int i12 = i10 + 1;
            iArr[i11] = (int) (jM1035a4 >>> 32);
            i10 = i12 + 1;
            iArr[i12] = (int) jM1035a4;
        } while (i10 != 1024);
        return iArr;
    }

    /* renamed from: a */
    private static final long m1035a(int[] iArr, int i, int i2) {
        int i3 = i ^ iArr[1042];
        int i4 = 0;
        while (i4 < 16) {
            int i5 = i4 + 1;
            i2 ^= (((iArr[i3 >>> 24] + iArr[256 | ((i3 >>> 16) & 255)]) ^ iArr[512 | ((i3 >>> 8) & 255)]) + iArr[768 | (i3 & 255)]) ^ iArr[(i5 + 1024) + 18];
            i4 = i5 + 1;
            i3 ^= (((iArr[i2 >>> 24] + iArr[256 | ((i2 >>> 16) & 255)]) ^ iArr[512 | ((i2 >>> 8) & 255)]) + iArr[768 | (i2 & 255)]) ^ iArr[(i4 + 1024) + 18];
        }
        return ((i2 ^ iArr[1059]) << 32) | ((i3 << 32) >>> 32);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v14, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r0v8, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v11, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v15, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v21, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v25, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v29, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v7, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v28, types: [int] */
    /* JADX WARN: Type inference failed for: r2v42, types: [int] */
    /* renamed from: a */
    public static final void m1036a(byte[] bArr, int i, byte[] bArr2, int i2) {
        int[] iArrM1034a = m1034a(bArr, i);
        int i3 = i2 >> 3;
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 << 3;
            int i6 = ((bArr2[i5] & 0xFF) << 24) | ((bArr2[i5 + 1] & 0xFF) << 16) | ((bArr2[i5 + 2] & 0xFF) << 8) | (bArr2[i5 + 3] & 0xFF);
            int i7 = ((bArr2[i5 + 4] & 0xFF) << 24) | ((bArr2[i5 + 5] & 0xFF) << 16) | ((bArr2[i5 + 6] & 0xFF) << 8) | (bArr2[i5 + 7] & 0xFF);
            int i8 = i6 ^ iArrM1034a[1042];
            int i9 = 0;
            while (i9 < 16) {
                int i10 = i9 + 1;
                i7 ^= (((iArrM1034a[i8 >>> 24] + iArrM1034a[256 | ((i8 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i8 >>> 8) & 255)]) + iArrM1034a[768 | (i8 & 255)]) ^ iArrM1034a[(i10 + 1024) + 18];
                i9 = i10 + 1;
                i8 ^= (((iArrM1034a[i7 >>> 24] + iArrM1034a[256 | ((i7 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i7 >>> 8) & 255)]) + iArrM1034a[768 | (i7 & 255)]) ^ iArrM1034a[(i9 + 1024) + 18];
            }
            int i11 = i7 ^ iArrM1034a[1059];
            bArr2[i5] = (byte) (i11 >> 24);
            bArr2[i5 + 1] = (byte) (i11 >>> 16);
            bArr2[i5 + 2] = (byte) (i11 >>> 8);
            bArr2[i5 + 3] = (byte) i11;
            bArr2[i5 + 4] = (byte) (i8 >> 24);
            bArr2[i5 + 5] = (byte) (i8 >>> 16);
            bArr2[i5 + 6] = (byte) (i8 >>> 8);
            bArr2[i5 + 7] = (byte) i8;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v14, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r0v8, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v11, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v15, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v21, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v25, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v29, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v7, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v28, types: [int] */
    /* JADX WARN: Type inference failed for: r2v42, types: [int] */
    /* renamed from: b */
    public static final void m1037b(byte[] bArr, int i, byte[] bArr2, int i2) {
        int[] iArrM1034a = m1034a(bArr, i);
        int i3 = i2 >> 3;
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 << 3;
            int i6 = ((bArr2[i5] & 0xFF) << 24) | ((bArr2[i5 + 1] & 0xFF) << 16) | ((bArr2[i5 + 2] & 0xFF) << 8) | (bArr2[i5 + 3] & 0xFF);
            int i7 = ((bArr2[i5 + 4] & 0xFF) << 24) | ((bArr2[i5 + 5] & 0xFF) << 16) | ((bArr2[i5 + 6] & 0xFF) << 8) | (bArr2[i5 + 7] & 0xFF);
            int i8 = i6 ^ iArrM1034a[1059];
            int i9 = 16;
            while (i9 > 0) {
                int i10 = i9;
                int i11 = i10 - 1;
                i7 ^= (((iArrM1034a[i8 >>> 24] + iArrM1034a[256 | ((i8 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i8 >>> 8) & 255)]) + iArrM1034a[768 | (i8 & 255)]) ^ iArrM1034a[1042 + i10];
                i9 = i11 - 1;
                i8 ^= (((iArrM1034a[i7 >>> 24] + iArrM1034a[256 | ((i7 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i7 >>> 8) & 255)]) + iArrM1034a[768 | (i7 & 255)]) ^ iArrM1034a[1042 + i11];
            }
            int i12 = i7 ^ iArrM1034a[1042];
            bArr2[i5] = (byte) (i12 >> 24);
            bArr2[i5 + 1] = (byte) (i12 >>> 16);
            bArr2[i5 + 2] = (byte) (i12 >>> 8);
            bArr2[i5 + 3] = (byte) i12;
            bArr2[i5 + 4] = (byte) (i8 >> 24);
            bArr2[i5 + 5] = (byte) (i8 >>> 16);
            bArr2[i5 + 6] = (byte) (i8 >>> 8);
            bArr2[i5 + 7] = (byte) i8;
        }
    }

    /* renamed from: k */
    public static final void m1038k() {
        synchronized (AppState.m614m(1402)) {
            AppState.m594c(1566, 1);
        }
    }

    /* renamed from: l */
    public static final boolean m1039l() {
        synchronized (AppState.m614m(1402)) {
            if (!AppState.m587e(1566)) {
                return false;
            }
            synchronized (AppState.m614m(1402)) {
                AppState.m594c(1566, 0);
            }
            return true;
        }
    }

    /* renamed from: c */
    public static final Object[] m1040c(int i) {
        return m1041a(C0015ao.m332c(AppState.m584b(i)));
    }

    /* renamed from: a */
    public static final Object[] m1041a(Object[] objArr) {
        if (objArr != null) {
            Vector vectorM614m = AppState.m614m(1402);
            synchronized (vectorM614m) {
                if (!vectorM614m.contains(objArr)) {
                    vectorM614m.addElement(objArr);
                }
                m1038k();
            }
        }
        return objArr;
    }

    /* renamed from: b */
    public static final void m1042b(Object[] objArr) {
        if (objArr != null) {
            Vector vectorM614m = AppState.m614m(1402);
            synchronized (vectorM614m) {
                if (vectorM614m.contains(objArr)) {
                    vectorM614m.removeElement(objArr);
                    m1038k();
                }
            }
        }
    }

    /* renamed from: a */
    public static final void m1043a(Vector vector, C0014an c0014an, int i, int i2) {
        if (c0014an == null || vector.contains(c0014an)) {
            return;
        }
        if (null != m1044a(vector, c0014an.f133a)) {
            return;
        }
        if (i2 > 0 && vector.size() >= i2) {
            vector.removeElementAt(0);
        }
        vector.insertElementAt(c0014an, 0);
    }

    /* renamed from: a */
    private static C0014an m1044a(Vector vector, String str) {
        C0014an c0014an;
        try {
            int size = vector.size();
            do {
                size--;
                if (size < 0) {
                    return null;
                }
                c0014an = (C0014an) vector.elementAt(size);
            } while (!str.equals(c0014an.f133a));
            return c0014an;
        } catch (Exception unused) {
            return null;
        }
    }

    /* renamed from: a */
    public static final Vector m1045a(String str) {
        Vector vectorM1213g = C0040k.m1213g();
        try {
            Vector vectorM513a = Utils.m513a(str, '\r', '\n');
            int size = vectorM513a.size();
            for (int i = 0; i < size; i++) {
                Vector vectorM516c = Utils.m516c((String) vectorM513a.elementAt(i), '|');
                C0014an c0014an = new C0014an((String) vectorM516c.elementAt(0), Long.parseLong((String) vectorM516c.elementAt(2)), Long.parseLong((String) vectorM516c.elementAt(1)), Utils.m510a(vectorM516c.elementAt(3)));
                c0014an.f143k = 1;
                c0014an.f144l = Utils.m510a(vectorM516c.elementAt(4));
                c0014an.f145m = Utils.m510a(vectorM516c.elementAt(5));
                vectorM1213g.addElement(c0014an);
                C0040k.m1212a(vectorM516c);
            }
            C0040k.m1212a(vectorM513a);
            Utils.m526b(vectorM1213g);
        } catch (Throwable unused) {
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public static final void m1046a(Vector vector, int i) {
        try {
            ByteBuffer c0043n = new ByteBuffer();
            int size = vector.size();
            c0043n.m1360p(size);
            for (int i2 = 0; i2 < size; i2++) {
                C0014an c0014an = (C0014an) vector.elementAt(i2);
                c0043n.m1309b(c0014an.f133a).m1323a(c0014an.f134b).m1323a(c0014an.f135c).m1323a(c0014an.f136d).m1323a(c0014an.f137e).m1323a(c0014an.f138f).m1323a(c0014an.f139g).m1360p(c0014an.f140h).m1360p(c0014an.f143k).m1360p(c0014an.f145m).m1360p(c0014an.f144l);
            }
            AppState.m601a(i, (Object) c0043n.m1320d());
        } catch (Throwable unused) {
        }
    }

    /* renamed from: d */
    public static final Vector m1047d(int i) {
        Vector vectorM1213g = C0040k.m1213g();
        try {
            ByteBuffer c0043nM986d = C0034e.m986d(AppState.m584b(i));
            if (c0043nM986d.f384b > 4) {
                int iM1328e = c0043nM986d.m1328e();
                for (int i2 = 0; i2 < iM1328e; i2++) {
                    vectorM1213g.addElement(new C0014an(c0043nM986d));
                }
            }
            Utils.m526b(vectorM1213g);
        } catch (Throwable unused) {
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public static final void m1048a(Vector vector) {
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((C0014an) vector.elementAt(size)).m269c();
            }
        }
    }

    /* renamed from: b */
    public static final void m1049b(Vector vector) {
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((C0014an) vector.elementAt(size)).m268b();
            }
        }
    }
}
