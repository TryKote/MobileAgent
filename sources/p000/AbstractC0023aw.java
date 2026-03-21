package p000;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import p001main.Midlet;

/* renamed from: aw */
/* loaded from: MobileAgent_3.9.jar:aw.class */
public abstract class AbstractC0023aw {

    /* renamed from: a */
    public static byte[] f176a;

    /* renamed from: b */
    public static Object[] f177b;

    /* renamed from: e */
    private static Object[] f178e;

    /* renamed from: f */
    private static int[] f179f;

    /* renamed from: c */
    public static Object f180c;

    /* renamed from: d */
    public static String f181d;

    /* renamed from: g */
    private static String f182g;

    /* JADX WARN: Type inference failed for: r0v9, types: [byte[], byte[][]] */
    /* renamed from: a */
    public static final void m578a(Object obj) {
        int iM1346q;
        C0034e.f292k = new Boolean(true);
        C0034e.f293l = new Boolean(false);
        C0034e.f290i = new Object();
        C0034e.f291j = new Integer[32];
        int i = 32;
        while (true) {
            i--;
            if (i < 0) {
                break;
            } else {
                C0034e.f291j[i] = new Integer(i);
            }
        }
        C0000a.f0a = new Vector(128);
        C0040k.f365g = new byte[20][];
        C0040k.f366h = new StringBuffer[5];
        C0040k.f367i = new Vector[5];
        C0040k.f368j = new Hashtable();
        C0029bb.f237c = C0040k.m1213g();
        f182g = C0040k.m1221a(1819047278);
        f176a = new byte[0];
        f178e = new Object[295];
        f177b = new Object[1406];
        f179f = new int[3773];
        C0043n c0043n = new C0043n(C0040k.m1221a(1734763311), 45000);
        for (int i2 = 0; i2 < 1406; i2++) {
            f177b[i2] = m618a(c0043n, i2);
        }
        for (int i3 = 0; i3 < 3773; i3++) {
            int[] iArr = f179f;
            int i4 = i3;
            byte bM1344o = c0043n.m1344o();
            if ((bM1344o & 64) != 0) {
                iM1346q = bM1344o & 63;
            } else if ((bM1344o & 32) != 0) {
                iM1346q = ((bM1344o & 31) << 8) + c0043n.m1346q();
            } else {
                int iM1346q2 = 0;
                int i5 = bM1344o & 7;
                while (true) {
                    i5--;
                    if (i5 < 0) {
                        break;
                    } else {
                        iM1346q2 = (iM1346q2 << 8) + c0043n.m1346q();
                    }
                }
                iM1346q = iM1346q2;
            }
            iArr[i4] = iM1346q;
        }
        f181d = (String) f177b[1038];
        C0043n c0043nM851h = C0031bd.m851h(C0040k.m1221a(1164404323));
        while (c0043nM851h.f384b > 0) {
            try {
                f178e[((Integer) m618a(c0043nM851h, 0)).intValue()] = m618a(c0043nM851h, 0);
            } catch (Throwable unused) {
            }
        }
        try {
            try {
            } catch (Throwable th) {
                m594c(0, 3096);
                throw th;
            }
        } catch (Throwable unused2) {
            f178e = new Object[295];
            try {
                String[] strArrM10a = C0000a.m10a();
                if (strArrM10a != null) {
                    int length = strArrM10a.length;
                    while (true) {
                        length--;
                        if (length < 0) {
                            break;
                        } else {
                            try {
                                RecordStore.deleteRecordStore(strArrM10a[length]);
                            } catch (Throwable unused3) {
                            }
                        }
                    }
                }
            } catch (Throwable unused4) {
            }
            m594c(0, 3096);
        }
        if (((Integer) f178e[0]).intValue() != 3096) {
            throw new RuntimeException();
        }
        m594c(0, 3096);
        m601a(1369, (Object) f182g);
        f177b[1366] = obj;
        f177b[1370] = new int[0];
        Date date = new Date();
        f177b[1368] = date;
        f177b[1367] = Calendar.getInstance();
        m597a(1532, date.getTime() - System.currentTimeMillis());
        m580b();
        f177b[1372] = new Random(System.currentTimeMillis() ^ Thread.currentThread().hashCode());
        f177b[1361] = new Object[58];
        f177b[1362] = new int[29];
        f177b[1266] = C0040k.m1213g();
        f177b[1267] = new int[]{1};
        f177b[1268] = new int[]{2};
        f177b[1269] = new int[]{3};
        f177b[1270] = new int[]{4};
        C0000a.m34h();
        C0015ao.f147a = new long[14];
        f177b[1238] = new Object[1];
        C0040k.m1208b(f182g);
        C0040k.m1208b(m620j());
        C0040k.m1208b(m584b(1233));
        C0040k.m1208b(m584b(1234));
        C0040k.m1208b(m584b(1038));
        C0040k.m1208b(m584b(525044));
        C0040k.m1208b(m584b(590588));
        f177b[112] = C0034e.m967e(!C0000a.f1b && !C0000a.f2c ? 1 : 0);
        try {
            m599a(1535, Display.getDisplay(m602d()).numAlphaLevels() > 2);
        } catch (Throwable unused5) {
        }
    }

    /* renamed from: a */
    public static final boolean m579a() {
        return Runtime.getRuntime().totalMemory() > 1572864;
    }

    /* renamed from: b */
    public static final void m580b() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        m597a(1530, jCurrentTimeMillis);
        m599a(1534, (((int) jCurrentTimeMillis) & Integer.MAX_VALUE) % 2000 < 1000);
    }

    /* renamed from: a */
    public static final byte[] m581a(int i) {
        return (byte[]) f177b[i];
    }

    /* renamed from: c */
    public static final C0011ak m582c() {
        return (C0011ak) f177b[1371];
    }

    /* renamed from: p */
    private static final Object m583p(int i) {
        Object obj;
        return (i >= 295 || (obj = f178e[i]) == null) ? f177b[i] : obj;
    }

    /* renamed from: b */
    public static final String m584b(int i) {
        if (i > 5179) {
            return C0000a.m17c(new String(m581a(295), i & 65535, i >> 16));
        }
        Object objM583p = m583p(i);
        if (objM583p == null) {
            return null;
        }
        return objM583p instanceof byte[] ? C0040k.m1218b((byte[]) objM583p) : (String) objM583p;
    }

    /* renamed from: c */
    public static final int m585c(int i) {
        int iM586d = m586d(i);
        m594c(i, 0);
        return iM586d;
    }

    /* renamed from: d */
    public static final int m586d(int i) {
        return i < 1406 ? ((Integer) m583p(i)).intValue() : f179f[i - 1406];
    }

    /* renamed from: e */
    public static final boolean m587e(int i) {
        return m586d(i) != 0;
    }

    /* renamed from: a */
    public static final void m588a(int i, StringBuffer stringBuffer) {
        m601a(i, (Object) C0040k.m1215a(stringBuffer));
    }

    /* renamed from: a */
    public static final void m589a(int i, int i2) {
        m601a(i, (Object) m584b(i2));
    }

    /* renamed from: b */
    public static final void m590b(int i, int i2) {
        while (i <= i2) {
            int i3 = i;
            i++;
            m591f(i3);
        }
    }

    /* renamed from: f */
    public static final void m591f(int i) {
        if (i >= 295) {
            f177b[i] = null;
        } else {
            f178e[i] = null;
        }
    }

    /* renamed from: a */
    public static final void m592a(int i, String str) {
        m601a(i, (Object) AbstractC0019as.m522f(str));
    }

    /* renamed from: b */
    public static final void m593b(int i, String str) {
        m601a(m586d(i), (Object) str);
    }

    /* renamed from: c */
    public static final void m594c(int i, int i2) {
        if (i < 1406) {
            m601a(i, C0034e.m967e(i2));
        } else {
            f179f[i - 1406] = i2;
        }
    }

    /* renamed from: d */
    public static final void m595d(int i, int i2) {
        m594c(i, m586d(i) + i2);
    }

    /* renamed from: e */
    public static final void m596e(int i, int i2) {
        m594c(m586d(i), i2);
    }

    /* renamed from: a */
    public static final void m597a(int i, long j) {
        m594c(i, (int) (j >>> 32));
        m594c(i + 1, (int) j);
    }

    /* renamed from: g */
    public static final long m598g(int i) {
        return (m586d(i) << 32) | (m586d(i + 1) & 4294967295L);
    }

    /* renamed from: a */
    public static final boolean m599a(int i, boolean z) {
        m594c(i, z ? 1 : 0);
        return z;
    }

    /* renamed from: h */
    public static final boolean m600h(int i) {
        boolean z = !m587e(i);
        boolean z2 = z;
        m599a(i, z);
        return z2;
    }

    /* renamed from: a */
    public static final Object m601a(int i, Object obj) {
        if (i >= f178e.length) {
            f177b[i] = obj;
        } else {
            Object obj2 = f177b[i];
            if (obj2 == null && obj != null) {
                f178e[i] = obj;
            } else if (obj2 == null || obj2.equals(obj)) {
                f178e[i] = null;
            } else {
                f178e[i] = obj;
            }
        }
        return obj;
    }

    /* renamed from: d */
    public static final Midlet m602d() {
        return (Midlet) f177b[1366];
    }

    /* renamed from: i */
    public static final String m603i(int i) {
        return C0000a.m17c(m602d().getAppProperty(m584b(i)));
    }

    /* renamed from: b */
    public static final void m604b(Object obj) {
        f180c = obj;
        C0015ao.m304a(0, C0015ao.m376E());
    }

    /* renamed from: e */
    public static final int m605e() {
        return m586d(1529) - (m587e(71) ? m586d(1450) + 2 : 0);
    }

    /* renamed from: f */
    public static final void m606f(int i, int i2) {
        m594c(1528, i);
        m594c(1529, i2);
    }

    /* renamed from: j */
    public static final void m607j(int i) {
        m601a(i, (Object) f181d);
    }

    /* renamed from: k */
    public static final C0012al m608k(int i) {
        return (C0012al) f177b[i + 1273];
    }

    /* renamed from: l */
    public static final Object[] m609l(int i) {
        return (Object[]) f177b[i];
    }

    /* renamed from: f */
    public static final AbstractC0046q m610f() {
        return (AbstractC0046q) f177b[1365];
    }

    /* renamed from: g */
    public static final AbstractC0041l m611g() {
        return (AbstractC0041l) f177b[1365];
    }

    /* renamed from: h */
    public static final C0035f m612h() {
        return (C0035f) f177b[1365];
    }

    /* renamed from: c */
    public static final void m613c(Object obj) {
        f177b[1365] = obj;
    }

    /* renamed from: m */
    public static final Vector m614m(int i) {
        return (Vector) f177b[i];
    }

    /* renamed from: n */
    public static final Image m615n(int i) {
        return (Image) f177b[i];
    }

    /* renamed from: i */
    public static final AbstractC0037h m616i() {
        return (AbstractC0037h) f177b[1281];
    }

    /* renamed from: d */
    public static final void m617d(Object obj) {
        f177b[1281] = obj;
    }

    /* renamed from: a */
    private static final Object m618a(C0043n c0043n, int i) {
        byte bM1344o = c0043n.m1344o();
        if ((bM1344o & 128) != 0) {
            byte[] bArr = new byte[(bM1344o & 64) != 0 ? bM1344o & 63 : ((bM1344o & 31) << 8) + c0043n.m1346q()];
            c0043n.m1347c(bArr);
            if (i >= 295 && i < 1036) {
                return bArr;
            }
            StringBuffer stringBufferM1217h = C0040k.m1217h();
            for (byte b : bArr) {
                stringBufferM1217h.append(AbstractC0019as.m499a((int) b));
            }
            C0040k.m1209a(bArr);
            String str = f182g;
            String strM1215a = C0040k.m1215a(stringBufferM1217h);
            if (str.equals(strM1215a)) {
                return null;
            }
            return strM1215a;
        }
        if ((bM1344o & 64) != 0) {
            return C0034e.m967e(bM1344o & 63);
        }
        if ((bM1344o & 32) != 0) {
            return C0034e.m967e(((bM1344o & 31) << 8) + c0043n.m1346q());
        }
        int iM1346q = 0;
        int i2 = bM1344o & 7;
        while (true) {
            i2--;
            if (i2 < 0) {
                return C0034e.m967e(iM1346q);
            }
            iM1346q = (iM1346q << 8) + c0043n.m1346q();
        }
    }

    /* renamed from: a */
    public static void m619a(boolean z) {
        try {
            C0043n c0043n = new C0043n();
            for (int i = 0; i < 295; i++) {
                Object obj = f178e[i];
                if (obj != null) {
                    m621b(c0043n, i);
                    if (obj instanceof String) {
                        String str = (String) obj;
                        int length = str.length();
                        byte[] bArr = new byte[length];
                        for (int i2 = 0; i2 < length; i2++) {
                            bArr[i2] = AbstractC0019as.m500b(str.charAt(i2));
                        }
                        int length2 = str.length();
                        if (length2 <= 0 || length2 >= 64) {
                            c0043n.m1357m(length2 | 32768);
                        } else {
                            c0043n.m1321f(192 | length2);
                        }
                        c0043n.m1302a(bArr);
                    } else {
                        m621b(c0043n, ((Integer) obj).intValue());
                    }
                }
            }
            C0031bd.m852a(C0040k.m1221a(1164404323), c0043n, z);
        } catch (Throwable unused) {
        }
    }

    /* renamed from: j */
    public static final String m620j() {
        return C0040k.m1215a(C0040k.m1217h().append((char) 8230));
    }

    /* renamed from: b */
    private static final void m621b(C0043n c0043n, int i) {
        if (i >= 0 && i <= 63) {
            c0043n.m1321f(64 | i);
            return;
        }
        C0043n c0043n2 = new C0043n();
        int[] iArr = new int[8];
        int i2 = 24;
        int i3 = -1;
        for (int i4 = 0; i4 < 4; i4++) {
            iArr[i4] = (i >> i2) & 255;
            i2 -= 8;
            if (i3 == -1 && iArr[i4] != 0) {
                i3 = i4;
            }
        }
        if (i3 < 0) {
            i3 = 3;
        }
        for (int i5 = i3; i5 < 4; i5++) {
            c0043n2.m1321f(iArr[i5]);
        }
        byte[] bArrM1339k = c0043n2.m1339k();
        c0043n.m1321f(8 | bArrM1339k.length);
        c0043n.m1302a(bArrM1339k);
    }

    /* renamed from: k */
    public static final Calendar m622k() {
        Calendar calendar = (Calendar) f177b[1367];
        Date date = (Date) f177b[1368];
        date.setTime((m598g(1530) - m598g(1532)) + ((m586d(246) - 13) * 3600000));
        calendar.setTime(date);
        return calendar;
    }

    /* renamed from: o */
    public static final int m623o(int i) {
        return m586d(i + 1450);
    }

    /* renamed from: l */
    public static final int m624l() {
        Calendar calendarM622k = m622k();
        return (calendarM622k.get(1) << 16) + (calendarM622k.get(2) << 8) + calendarM622k.get(5);
    }

    /* renamed from: m */
    public static final Font m625m() {
        return ((C0012al) f177b[1273]).f93c;
    }

    /* renamed from: a */
    public static final int m626a(String str, int i) {
        return str.indexOf(C0040k.m1221a(i));
    }

    /* renamed from: a */
    public static final int m627a(String str, long j) {
        return str.indexOf(C0040k.m1221a(j));
    }

    /* renamed from: b */
    public static final int m628b(String str, int i) {
        return str.indexOf(m584b(i));
    }
}
