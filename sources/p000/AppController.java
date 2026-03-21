package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

/* renamed from: ao */
/* loaded from: MobileAgent_3.9.jar:ao.class */
public final class AppController {

    /* renamed from: a */
    public static long[] f147a;

    /* renamed from: b */
    public static String f148b;

    /* renamed from: c */
    public static MrimAccount f149c;

    /* renamed from: d */
    public static Object f150d;

    /* renamed from: e */
    public static boolean f151e;

    /* renamed from: f */
    public static boolean f152f;

    /* renamed from: g */
    public static boolean f153g;

    /* renamed from: i */
    private static boolean f154i;

    /* renamed from: h */
    public static boolean f155h;

    /* renamed from: j */
    private static MapPoint f156j;

    /* renamed from: a */
    private static int m285a(Vector vector, int i, boolean z) {
        AppState.setBool(1467, z);
        AppState.clearIndex(1281);
        int size = vector.size();
        if (size == 0) {
            return m338l(551);
        }
        if (size == 1) {
            AppState.setAccount(vector.firstElement());
            return i;
        }
        AppState.pool[1283] = vector;
        AppState.setInt(1466, i);
        return 39;
    }

    /* renamed from: a */
    public static final int m286a(Object obj) {
        int iM586d = AppState.getInt(1466);
        if (obj != null) {
            AppState.setAccount(obj);
            return iM586d;
        }
        if (iM586d != 152) {
            return 104;
        }
        AppState.clearIndex(1281);
        return 152;
    }

    /* renamed from: a */
    public static final int m287a(String str, Object obj) {
        if (StringUtils.m3a(548, str)) {
            m444ai();
            return 4;
        }
        Account abstractC0037h = (Account) obj;
        int iMo120l = abstractC0037h.isConnecting() ? abstractC0037h.disconnect() : abstractC0037h.connect(0);
        int i = iMo120l;
        if (iMo120l != 0) {
            return m338l(i);
        }
        return 4;
    }

    /* renamed from: a */
    public static final int m288a(int i) {
        ScreenBuilder.m549c();
        AppState.setInt(4895, i);
        return AppState.getInt(3650);
    }

    /* renamed from: a */
    public static final int m289a(String str) {
        int iM586d = AppState.getInt(1513);
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        ChatRoom c0052wM745h = c0028ba.m745h(iM586d);
        IOUtils.m814e(c0052wM745h.f415g);
        if (StringUtils.m3a(852, str)) {
            c0052wM745h.f415g.removeAllElements();
            return 0;
        }
        if (StringUtils.m3a(853, str)) {
            AppState.setInt(1525, 2);
            return 0;
        }
        if (StringUtils.m3a(854, str)) {
            AppState.setInt(1525, 1);
            return 0;
        }
        if (!StringUtils.m3a(845, str)) {
            return 0;
        }
        AppState.setInt(1527, c0028ba.m749X().f409a);
        return 0;
    }

    /* renamed from: a */
    public static final void m290a(boolean z) {
        if (!z) {
            AppState.setInt(4778, 5);
        } else {
            AppState.setInt(4778, 4);
            AppState.setBool(1526, true);
        }
    }

    /* renamed from: a */
    public static final int m291a() {
        m294ad();
        return NetworkUtils.m1195d();
    }

    /* renamed from: b */
    public static final int m292b() {
        m294ad();
        return 0;
    }

    /* renamed from: c */
    public static final void m293c() {
        AppState.clearRange(1348, 1351);
    }

    /* renamed from: ad */
    private static final void m294ad() {
        ((MrimAccount) AppState.getAccount()).m746W().m1422e();
    }

    /* renamed from: d */
    public static final int m295d() {
        int iM1234D = AppState.getCurrentContact().validateDelete();
        if (0 != iM1234D) {
            return m338l(iM1234D);
        }
        return 4;
    }

    /* renamed from: b */
    public static final int m296b(Object obj) {
        AppState.setInt(266, 1);
        if (obj != null) {
            AppState.pool[267] = obj;
        }
        ScreenBuilder.m549c();
        return 0;
    }

    /* renamed from: e */
    public static final int m297e() {
        AppState.setInt(285, 1);
        ConnectionThread.m1164h();
        return 6;
    }

    /* renamed from: f */
    public static final String m298f() {
        return StringUtils.intern(Long.toString(Runtime.getRuntime().freeMemory()));
    }

    /* renamed from: g */
    public static final void m299g() {
        AppState.clearRange(1284, 1288);
    }

    /* renamed from: h */
    public static final void m300h() {
        Contact abstractC0041lM611g = AppState.getCurrentContact();
        m415b(abstractC0041lM611g);
        abstractC0041lM611g.flags = (byte) 0;
        abstractC0041lM611g.dirty = true;
        abstractC0041lM611g.updateRenderState();
        ScreenManager.m71b(abstractC0041lM611g.showMessages().m236o());
    }

    /* renamed from: b */
    public static final int m301b(int i) {
        if (i == 4) {
            ScreenManager.m74g();
            return 0;
        }
        if (i != 137) {
            return 0;
        }
        ScreenBuilder.m549c();
        ScreenBuilder.m549c();
        return 0;
    }

    /* renamed from: a */
    public static final int[] m302a(int[] iArr, int i, int i2) {
        return m303a(m303a(iArr, i), i2);
    }

    /* renamed from: a */
    private static int[] m303a(int[] iArr, int i) {
        int[] iArr2 = iArr;
        int i2 = 1 + iArr[0];
        if (i2 == iArr2.length) {
            int[] iArr3 = new int[i2 << 1];
            iArr2 = iArr3;
            Utils.arraycopy(iArr, 0, iArr3, 0, i2);
        }
        iArr2[i2] = i;
        int[] iArr4 = iArr2;
        iArr4[0] = iArr4[0] + 1;
        return iArr2;
    }

    /* renamed from: a */
    public static final void m304a(int i, long j) {
        f147a[i] = System.currentTimeMillis() + j;
    }

    /* renamed from: K */
    private static boolean m305K(int i) {
        return f147a[i] < System.currentTimeMillis();
    }

    /* renamed from: a */
    public static final boolean m306a(long j) {
        return j != 0 && j < System.currentTimeMillis();
    }

    /* renamed from: b */
    public static final boolean m307b(int i, long j) {
        long[] jArr = f147a;
        long j2 = jArr[i];
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (j2 >= j2) {
            return false;
        }
        jArr[i] = jCurrentTimeMillis + j;
        return true;
    }

    /* renamed from: i */
    public static final int m308i() {
        if (ScreenManager.m72a(43)) {
            return 43;
        }
        ScreenBuilder.m549c();
        ScreenBuilder.m549c();
        return -1;
    }

    /* renamed from: c */
    public static final int m309c(Object obj) {
        AppState.pool[1258] = obj;
        return 177;
    }

    /* renamed from: j */
    public static final int m310j() {
        ResourceManager.m931a((PhoneContact) AppState.pool[1256], AppState.getInt(1444) + 10);
        return 6;
    }

    /* renamed from: k */
    public static final int m311k() {
        ResourceManager.m931a((PhoneContact) AppState.pool[1256], AppState.getInt(1444) - 10);
        return 6;
    }

    /* renamed from: l */
    public static final UserSearchResult m312l() {
        return (UserSearchResult) AppState.pool[1258];
    }

    /* renamed from: c */
    public static final int m313c(int i) {
        Account abstractC0037hM616i = AppState.getAccount();
        if (!(abstractC0037hM616i instanceof MmpProtocol)) {
            return m383c((i + 161) - 4, (i + 155) - 4, 4);
        }
        ((MmpProtocol) abstractC0037hM616i).reserved2 = i;
        if (i == 0) {
            return 3;
        }
        return m383c(i + 268, i + 118, 3);
    }

    /* renamed from: m */
    public static final String m314m() {
        Object obj = AppState.pool[1336];
        if (obj == null) {
            return null;
        }
        return obj instanceof String ? (String) obj : ((MrimContact) obj).f297d;
    }

    /* renamed from: d */
    public static final long m315d(int i) {
        return (1 << (17 - i)) * ((i < 8 || i > 17) ? 119432 : 1194329);
    }

    /* renamed from: e */
    public static final long m316e(int i) {
        return (i < 8 || i > 17) ? 100000L : 1000000L;
    }

    /* renamed from: a */
    public static long m317a(long j, int i) {
        return (j * m316e(i)) / m315d(i);
    }

    /* renamed from: a */
    public static final long m318a(int i, int i2) {
        return (i * m315d(i2)) / m316e(i2);
    }

    /* renamed from: a */
    public static final int m319a(int i, int i2, int i3, int i4) {
        return Utils.abs(i2 - i4) + Utils.abs(i - i3);
    }

    /* renamed from: b */
    public static final int m320b(String str) {
        if (AppState.getString(1251).equals(str)) {
            ScreenBuilder.m549c();
            if (ConnectionThread.m1168k()) {
                return 0;
            }
            return m338l(354);
        }
        if (AppState.getString(376).equals(str)) {
            AppState.setInt(253, 1);
            XmppContactGroup.m1049b(AppState.getVector(1401));
            MapRenderer.f200h = true;
            return 6;
        }
        if (!AppState.getString(377).equals(str)) {
            return 0;
        }
        AppState.setInt(253, 0);
        XmppContactGroup.m1048a(AppState.getVector(1401));
        MapRenderer.f200h = true;
        return 6;
    }

    /* renamed from: a */
    public static final ByteBuffer m321a(MrimAccount c0028ba, int i, ByteBuffer c0043n) {
        ByteBuffer c0043nM1360p = new ByteBuffer().writeIntLE(-559038737).writeIntLE(65557);
        int i2 = c0028ba.state;
        c0028ba.state = i2 + 1;
        return c0043nM1360p.writeIntLE(i2).writeIntLE(i).writeIntLE(c0043n != null ? c0043n.length : 0).writeZeros(24).writeBuffer(c0043n);
    }

    /* renamed from: f */
    public static final int m322f(int i) {
        Message c0026azM1415b = ((MrimAccount) AppState.getAccount()).m745h(AppState.getInt(1513)).m1415b(AppState.getString(1346));
        String str = c0026azM1415b.body;
        c0026azM1415b.body = i == 0 ? Conversation.m1116h(str) : Conversation.m1117i(str);
        return 52;
    }

    /* renamed from: g */
    public static final int m323g(int i) {
        if (i != 147 && i != 133 && i != 89) {
            return 0;
        }
        Vector vectorM439R = m439R();
        int size = vectorM439R.size();
        if (size == 0) {
            return m338l(549);
        }
        if (size != 1) {
            return m285a(vectorM439R, i, false);
        }
        AppState.setAccount(vectorM439R.firstElement());
        return i;
    }

    /* renamed from: n */
    public static final void m324n() {
        NetworkUtils.m1200b(180, 504);
        AppState.clearRange(1239, 1240);
        Conversation.m1113a(true, (MrimAccount) AppState.getAccount());
    }

    /* renamed from: h */
    public static final int m325h(int i) {
        if (i != 6) {
            return 0;
        }
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        c0028ba.f232h = true;
        if (!c0028ba.isSelected()) {
            return m338l(667);
        }
        m331a(true, false, !AppState.getBool(276));
        AppState.setInt(281, 1);
        ConnectionThread.m1172a((ListItem) c0028ba);
        return 0;
    }

    /* renamed from: a */
    public static final ByteBuffer m326a(MmpProtocol c0033d, int i) {
        ByteBuffer c0043nM1321f = new ByteBuffer().writeByte(42).writeByte(i);
        int i2 = c0033d.state + 1;
        c0033d.state = i2;
        return c0043nM1321f.writeShortBE((i2 & 16777215) % 32768).writeShortBE(0);
    }

    /* renamed from: o */
    public static final int m327o() {
        MrimAccount c0028ba = (MrimAccount) AppState.pool[1282];
        ResourceManager.m954k();
        AppState.setAccount(c0028ba);
        AppState.setInt(1512, 38);
        return 37;
    }

    /* renamed from: a */
    public static final void m328a(Account abstractC0037h) {
        Vector vectorM614m = AppState.getVector(1291);
        Vector vector = vectorM614m;
        if (vectorM614m == null) {
            Vector vectorM1213g = NetworkUtils.m1213g();
            vector = vectorM1213g;
            AppState.pool[1291] = vectorM1213g;
        }
        vector.addElement(abstractC0037h);
    }

    /* renamed from: p */
    public static final int m329p() {
        Vector vectorM614m = AppState.getVector(1291);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return 4;
            }
            ((Account) vectorM614m.elementAt(size)).connect(0);
        }
    }

    /* renamed from: i */
    public static final int m330i(int i) {
        switch (i) {
            case 0:
                Conversation.m1113a(false, (MrimAccount) null);
                return 12;
            case 1:
                m331a(false, true, true);
                return 12;
            case 2:
                m331a(true, false, true);
                return 12;
            case 3:
                Conversation.m1126a(true);
                return 12;
            case 4:
                Conversation.m1126a(false);
                return 12;
            case 5:
                return NetworkUtils.m1201f();
            default:
                return 0;
        }
    }

    /* renamed from: a */
    public static final void m331a(boolean z, boolean z2, boolean z3) {
        AppState.setBool(276, z);
        AppState.setBool(277, z2);
        if (!z3 || !ConnectionThread.f356g) {
            return;
        }
        int i = 11;
        while (true) {
            i--;
            if (i < 0) {
                MmpContact.m185f();
                StringUtils.m19b();
                ConnectionThread.m1146e();
                MapRenderer.f200h = true;
                return;
            }
            XmppContactGroup.m1021a(i + 18);
        }
    }

    /* renamed from: c */
    public static final Object[] m332c(String str) {
        return new Object[]{ResourceManager.m967e(20), str};
    }

    /* renamed from: d */
    public static final int m333d(String str) {
        AppState.setObject(1279, (Object) new StringBuffer().append((Object) NetworkUtils.m1196e()).append(str).toString());
        return 0;
    }

    /* renamed from: q */
    public static final void m334q() {
        AppState.clearRange(1016, 1021);
    }

    /* renamed from: j */
    public static final int m335j(int i) {
        AppState.setInt(1510, i);
        return 34;
    }

    /* renamed from: k */
    public static final int m336k(int i) {
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        switch (i) {
            case 0:
                if (c0028ba != null) {
                    c0028ba.m725k();
                    break;
                } else {
                    Vector vectorM439R = m439R();
                    int size = vectorM439R.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            NetworkUtils.m1212a(vectorM439R);
                            break;
                        } else {
                            m447a(vectorM439R, size).m725k();
                        }
                    }
                }
            case 1:
                if (c0028ba != null) {
                    c0028ba.m726m();
                    break;
                } else {
                    Vector vectorM439R2 = m439R();
                    int size2 = vectorM439R2.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            NetworkUtils.m1212a(vectorM439R2);
                            break;
                        } else {
                            m447a(vectorM439R2, size2).m726m();
                        }
                    }
                }
            case 2:
                if (c0028ba != null) {
                    c0028ba.m728T();
                    break;
                } else {
                    Vector vectorM439R3 = m439R();
                    int size3 = vectorM439R3.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            NetworkUtils.m1212a(vectorM439R3);
                            break;
                        } else {
                            m447a(vectorM439R3, size3).m728T();
                        }
                    }
                }
            case 3:
                if (c0028ba != null) {
                    c0028ba.m727S();
                    break;
                } else {
                    Vector vectorM439R4 = m439R();
                    int size4 = vectorM439R4.size();
                    while (true) {
                        size4--;
                        if (size4 < 0) {
                            NetworkUtils.m1212a(vectorM439R4);
                            break;
                        } else {
                            m447a(vectorM439R4, size4).m727S();
                        }
                    }
                }
            case 4:
                return 156;
        }
        if (AppState.getBool(286)) {
            return AppState.getInt(1476);
        }
        ScreenBuilder.m549c();
        return 171;
    }

    /* renamed from: d */
    public static final int m337d(Object obj) {
        AppState.setAccount(obj);
        ScreenBuilder.m549c();
        return 0;
    }

    /* renamed from: l */
    public static final int m338l(int i) {
        if (ScreenManager.m66b().f97d == 8) {
            ScreenBuilder.m549c();
        }
        AppState.setFromPool(1294, i);
        return 112;
    }

    /* renamed from: e */
    public static final void m339e(String str) {
        AppState.setInt(3329, 112);
        AppState.setObject(1294, (Object) str);
        m341r();
    }

    /* renamed from: m */
    public static final void m340m(int i) {
        AppState.setInt(3329, 112);
        AppState.setFromPool(1294, i);
        m341r();
    }

    /* renamed from: r */
    public static final void m341r() {
        ResourceManager.m925a(5);
        ScreenManager.m71b(ScreenManager.m75b(3328));
        AppState.clearIndex(1294);
    }

    /* renamed from: ae */
    private static final Object[] m342ae() {
        return (Object[]) AppState.pool[1238];
    }

    /* renamed from: s */
    public static final void m343s() {
        Object[] objArrM342ae = m342ae();
        while (true) {
            synchronized (objArrM342ae) {
                if (objArrM342ae[0] == null) {
                    objArrM342ae[0] = Thread.currentThread();
                    return;
                }
            }
            try {
                Thread.sleep(100);
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: t */
    public static final void m344t() {
        Object[] objArrM342ae = m342ae();
        synchronized (objArrM342ae) {
            objArrM342ae[0] = null;
        }
    }

    /* renamed from: u */
    public static final boolean m345u() {
        return m342ae()[0] != null;
    }

    /* renamed from: n */
    public static final int m346n(int i) {
        ((XmppContact) AppState.getCurrentContact()).m150b(i);
        return 0;
    }

    /* renamed from: f */
    public static final int m347f(String str) {
        AppState.setFromBuffer(1279, NetworkUtils.m1196e().append(str));
        return 0;
    }

    /* renamed from: v */
    public static final int m348v() {
        AppState.setAccount(m439R().firstElement());
        return 168;
    }

    /* renamed from: a */
    public static final int m349a(int i, String str) {
        switch (i) {
            case 0:
                return 155;
            case 1:
                return 156;
            case 2:
                return 157;
            case 3:
                return 154;
            default:
                if (AppState.getString(1225).equals(str)) {
                    return 160;
                }
                if (AppState.getString(1224).equals(str)) {
                    return 159;
                }
                int i2 = Integer.parseInt(StringUtils.suffix(str, 7));
                return (!(i2 >= 4 && i2 <= 53) || i2 == 25 || i2 == 31) ? (i & Integer.MIN_VALUE) != 0 ? 158 : 156 : i2 + 157;
        }
    }

    /* renamed from: w */
    public static final void m350w() {
        Screen c0013amM75b = ScreenManager.m75b(4852);
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        Enumeration enumerationElements = c0028ba.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
            if (c0052w != c0028ba.m746W()) {
                MenuItem c0032cM898b = MenuItem.m886c().m896a(234).m898b(c0052w.f410b);
                c0032cM898b.f265d = c0052w;
                c0013amM75b.m225a(c0032cM898b);
            }
        }
        ScreenManager.m71b(c0013amM75b);
    }

    /* renamed from: e */
    public static final int m351e(Object obj) {
        AppState.setInt(1527, ((ChatRoom) obj).f409a);
        return 0;
    }

    /* renamed from: o */
    public static final int m352o(int i) {
        if (i == 54) {
            ScreenBuilder.m549c();
            ResourceManager.m966a((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (i == 68) {
            ScreenBuilder.m549c();
            m290a(true);
            return 0;
        }
        if (i != 37) {
            return 0;
        }
        ((MrimAccount) AppState.getAccount()).f229e = true;
        return 0;
    }

    /* renamed from: a */
    public static final int m353a(Vector vector) {
        int size = vector.size();
        m354a(vector, 0, size - 1);
        return size;
    }

    /* renamed from: a */
    private static final void m354a(Vector vector, int i, int i2) {
        if (i < i2) {
            if (i + 1 == i2) {
                if (((Sortable) vector.elementAt(i)).compareTo(vector.elementAt(i2)) > 0) {
                    Utils.m508a(vector, i, i2);
                    return;
                }
                return;
            }
            int i3 = i;
            int i4 = i2;
            boolean z = true;
            while (i3 < i4) {
                if (((Sortable) vector.elementAt(i3)).compareTo(vector.elementAt(i4)) > 0) {
                    Utils.m508a(vector, i3, i4);
                    z = !z;
                }
                if (z) {
                    i3++;
                } else {
                    i4--;
                }
            }
            m354a(vector, i, i3 - 1);
            m354a(vector, i4 + 1, i2);
        }
    }

    /* renamed from: x */
    public static final void m355x() {
        m358L(0);
    }

    /* renamed from: y */
    public static final void m356y() {
        if (AppState.getBool(270)) {
            m358L(Integer.MAX_VALUE);
            m304a(0, m376E());
        }
    }

    /* renamed from: z */
    public static final void m357z() {
        m358L(Integer.MAX_VALUE);
    }

    /* renamed from: L */
    private static final void m358L(int i) {
        if (AppState.getBool(268)) {
            try {
                Display.getDisplay(AppState.getMidlet()).flashBacklight(i);
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: M */
    private static int m359M(int i) {
        if (i == 0) {
            return 32;
        }
        if (i < 0) {
            return 0;
        }
        int i2 = 0;
        if ((i & (-65536)) == 0) {
            i <<= 16;
            i2 = 16;
        }
        if ((i & (-16777216)) == 0) {
            i <<= 8;
            i2 += 8;
        }
        while (i > 0) {
            i2++;
            i <<= 1;
        }
        return i2;
    }

    /* renamed from: b */
    public static final int m360b(long j) {
        int iM359M = m359M((int) (j >> 32));
        return iM359M == 32 ? m359M((int) j) + 32 : iM359M;
    }

    /* renamed from: b */
    public static final long m361b(long j, int i) {
        return i >= 64 ? j == 0 ? 0L : 1L : (j << (64 - i)) == 0 ? j >>> i : (j >>> i) | 1;
    }

    /* renamed from: c */
    public static final long m362c(long j, int i) {
        long j2;
        long j3;
        if (i > 64) {
            return 0L;
        }
        if (i == 64) {
            j2 = j;
            j3 = 0;
        } else {
            j2 = j << (64 - i);
            j3 = j >>> i;
        }
        return (j2 >= 0 || (j2 == Long.MIN_VALUE && (j3 & 1) != 1)) ? j3 : j3 + 1;
    }

    /* renamed from: p */
    public static final int m363p(int i) {
        int size = AppState.getVector(1241).size();
        while (true) {
            size--;
            if (size < 0) {
                return 0;
            }
            m434I(size).onError(i);
        }
    }

    /* renamed from: q */
    public static final int m364q(int i) {
        switch (i) {
            case 0:
                Conversation.m1127a();
                break;
            case 1:
                Conversation.m1128b();
                break;
            case 2:
                AppState.setInt(230, 1);
                break;
            case 3:
                AppState.setInt(230, 0);
                break;
            default:
                return 0;
        }
        MapRenderer.f200h = true;
        return 6;
    }

    /* renamed from: A */
    public static final int m365A() {
        return m369N(1004);
    }

    /* renamed from: B */
    public static final int m366B() {
        return m369N(1005);
    }

    /* renamed from: C */
    public static final int m367C() {
        return Integer.parseInt(StringUtils.getSystemProp(1006));
    }

    /* renamed from: D */
    public static final int m368D() {
        return Integer.parseInt(StringUtils.getSystemProp(1007));
    }

    /* renamed from: N */
    private static final int m369N(int i) {
        return Integer.parseInt(StringUtils.getSystemProp(i), 16);
    }

    /* renamed from: r */
    public static final int m370r(int i) {
        if (i == 120) {
            if (!ConnectionThread.m1168k()) {
                return m338l(354);
            }
            AppState.setInt(1443, 1);
            return 0;
        }
        if (i != 100) {
            return i == 0 ? 6 : 0;
        }
        AppState.setInt(1443, 1);
        return 0;
    }

    /* renamed from: s */
    public static final int m371s(int i) {
        if (i == 10) {
            return NetworkUtils.m1201f();
        }
        return 0;
    }

    /* renamed from: g */
    public static final int m372g(String str) {
        AppState.setBool(1524, StringUtils.m3a(859, str));
        return 0;
    }

    /* renamed from: a */
    public static final int m373a(int i, Object obj) {
        AppState.setAccount(obj);
        if (obj != null) {
            return 47;
        }
        if (i > 3) {
            return i;
        }
        AppState.setInt(1475, i);
        return 76;
    }

    /* renamed from: t */
    public static final int m374t(int i) {
        AppState.setFromBuffer(1279, NetworkUtils.m1196e().append(AppState.getString(i + (AppState.getCurrentContact() instanceof MmpContact ? 1141 : AppState.getCurrentContact() instanceof XmppContact ? 1184 : 1063))));
        return 63;
    }

    /* renamed from: a */
    public static final ByteBuffer m375a(MmpProtocol c0033d) {
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortBE(5);
        int i = c0033d.reserved2;
        ByteBuffer c0043nM1357m2 = c0043nM1357m.writeShortBE(64 + (i == 0 ? 0 : 16));
        int i2 = 4;
        while (true) {
            i2--;
            if (i2 < 0) {
                break;
            }
            c0043nM1357m2.writeCompressed(i2 + 904);
        }
        if (i != 0) {
            c0043nM1357m2.writeBytesAt(AppState.getBytes(908), (i - 1) << 4, 16);
        }
        return m464a(c0033d, 516, c0043nM1357m2);
    }

    /* renamed from: E */
    public static final long m376E() {
        switch (AppState.getInt(271)) {
            case 1:
                return 15000L;
            case 2:
                return 30000L;
            case 3:
                return 60000L;
            case 4:
                return 300000L;
            default:
                return 4294967295L;
        }
    }

    /* renamed from: a */
    public static final ByteBuffer m377a(MrimAccount c0028ba) {
        return m321a(c0028ba, 4097, new ByteBuffer().writeIntLE(120));
    }

    /* renamed from: F */
    public static final void m378F() {
        AppState.clearRange(1302, 1305);
    }

    /* renamed from: f */
    public static final int m379f(Object obj) {
        XmppMailRuProtocol.f257f = (ListItem) obj;
        return 0;
    }

    /* renamed from: a */
    public static final void m380a(Object[] objArr) throws InterruptedException {
        int i = 15000;
        do {
            int i2 = i - 500;
            i = i2;
            if (i2 < 0) {
                IOUtils.m778d((Object) objArr);
                return;
            }
            Thread.sleep(500L);
        } while (!f151e);
    }

    /* renamed from: u */
    public static final int m381u(int i) {
        if (i != 54) {
            ScreenBuilder.m549c();
            ScreenBuilder.m549c();
            m300h();
            return 0;
        }
        MrimContact c0035fM612h = AppState.getCurrentMrimContact();
        AppState.setAccount(c0035fM612h.account);
        ResourceManager.m966a(XmppMailRuProtocol.m871i(c0035fM612h.f297d), (String) null, (String) null);
        ScreenBuilder.m549c();
        ScreenBuilder.m549c();
        return 0;
    }

    /* renamed from: g */
    public static final int m382g(Object obj) {
        if (AppState.getBool(1443)) {
            MapRenderer.m653a((MapPoint) obj);
            return 0;
        }
        if (!AppState.getBool(1477)) {
            ConnectionThread.m1165a((MapPoint) obj, true);
            return 0;
        }
        MapPoint c0014an = (MapPoint) obj;
        ((MrimAccount) AppState.getAccount()).m731a(c0014an);
        XmppContactGroup.m1043a(AppState.getVector(1400), c0014an, 0, 5);
        XmppContactGroup.m1046a(AppState.getVector(1400), 225);
        AppState.setInt(1477, 0);
        return 160;
    }

    /* renamed from: c */
    private static int m383c(int i, int i2, int i3) {
        AppState.setInt(4305, i);
        AppState.setInt(4313, i);
        AppState.setInt(4317, i2);
        AppState.setInt(4308, i3);
        return 49;
    }

    /* renamed from: v */
    public static final int m384v(int i) {
        if (i == 0) {
            ConnectionThread.m1170l();
            if (MmpContact.m189q()) {
                return 6;
            }
            AppState.setInt(1442, 1);
            return 158;
        }
        ConnectionThread.m1171m();
        if (MmpContact.m188p()) {
            return 6;
        }
        AppState.setInt(1442, 0);
        return 158;
    }

    /* renamed from: w */
    public static final int m385w(int i) {
        if (i != 0) {
            AppState.setInt(1477, 1);
            return 100;
        }
        AppState.setInt(1479, 1);
        ((MrimAccount) AppState.getAccount()).f232h = false;
        return 12;
    }

    /* renamed from: a */
    public static final void m386a(MmpProtocol c0033d, ByteBuffer c0043n) {
        c0043n.skip(6);
        while (c0043n.length > 0) {
            int iM1353u = c0043n.readShortBE();
            int iM1353u2 = c0043n.readShortBE();
            if (iM1353u == 9 && iM1353u2 == 2) {
                int iM1353u3 = c0043n.readShortBE();
                if (iM1353u3 == 1) {
                    c0033d.handleTimeout();
                    return;
                } else {
                    c0033d.handleError(iM1353u3);
                    return;
                }
            }
            c0043n.skip(iM1353u2);
        }
        c0033d.handleError(-1);
    }

    /* renamed from: x */
    public static final int m387x(int i) {
        MapPoint c0014an = f156j;
        if (c0014an == null) {
            return m338l(354);
        }
        if (i == 6) {
            MapRenderer.m654b(f156j);
            return 0;
        }
        if (i == 118) {
            AppState.setObject(43, (Object) c0014an.m272d());
            return 0;
        }
        if (i != 120) {
            return 0;
        }
        ConnectionThread.m1169a(c0014an);
        return 0;
    }

    /* renamed from: y */
    public static final int m388y(int i) {
        ConnectionThread.m1173n();
        if (i == 6) {
            m331a(true, false, !AppState.getBool(276));
            AppState.setInt(281, 1);
            AppState.setInt(1479, 1);
            return 0;
        }
        if (i == 100) {
            AppState.setInt(1477, 1);
            return 0;
        }
        if (!ConnectionThread.m1168k()) {
            return m338l(354);
        }
        AppState.setInt(1478, 1);
        return 0;
    }

    /* renamed from: z */
    public static final int m389z(int i) {
        if (i == 22 || i == 143 || i == 24 || i == 23) {
            return m285a(m439R(), i, false);
        }
        if (i == 21 || i == 69 || i == 124) {
            return m285a(m443V(), i, false);
        }
        return 0;
    }

    /* renamed from: G */
    public static final void m390G() {
        AppState.setInt(217, 0);
        AppState.setInt(1511, 1);
        ScreenManager.m70a(ScreenManager.m75b(4038));
    }

    /* renamed from: H */
    public static final void m391H() {
        AppState.clearRange(1317, 1319);
    }

    /* renamed from: I */
    public static final void m392I() {
        AppState.setInt(1506, 0);
        AppState.clearIndex(1318);
        AppState.pool[1317] = NetworkUtils.m1213g();
    }

    /* renamed from: a */
    public static final void m393a(MrimAccount c0028ba, String str) {
        f149c = c0028ba;
        f148b = str;
    }

    /* renamed from: J */
    public static final void m394J() {
        f149c = null;
        f148b = null;
    }

    /* renamed from: b */
    public static final ByteBuffer m395b(MrimAccount c0028ba, String str) {
        return m321a(c0028ba, 4128, new ByteBuffer().writeStringLatin1(str));
    }

    /* renamed from: h */
    public static final int m396h(String str) {
        String strM584b = AppState.getString(1279);
        int i = 15;
        do {
            i--;
            if (i < 0) {
                return 0;
            }
        } while (AppState.getString(i + 48) != str);
        AppState.setObject(i + 48, (Object) strM584b);
        return 0;
    }

    /* renamed from: h */
    public static final int m397h(Object obj) {
        ContactGroup abstractC0046q = (ContactGroup) obj;
        if (null == abstractC0046q) {
            return 4;
        }
        Contact abstractC0041lM611g = AppState.getCurrentContact();
        int iMo113a = abstractC0041lM611g.isOnline() ? 310 : abstractC0041lM611g.account.validateMove(abstractC0041lM611g, abstractC0041lM611g.account.findGroup(abstractC0041lM611g), abstractC0046q);
        int i = iMo113a;
        if (0 != iMo113a) {
            return m338l(i);
        }
        return 4;
    }

    /* renamed from: i */
    public static final int m398i(Object obj) {
        MapRenderer.m646a();
        GeoRegion c0053x = (GeoRegion) obj;
        MapRenderer.m649a(c0053x.centerLat, c0053x.centerLon);
        MapRenderer.m651a(c0053x == StringUtils.getGeoRegion() ? 3 : 11);
        return 0;
    }

    /* renamed from: K */
    public static final int m399K() {
        long jMo274v;
        long jMo275w;
        ListItem interfaceC0044o = MapRenderer.f203k;
        if (interfaceC0044o != null) {
            jMo274v = interfaceC0044o.getWidth();
            jMo275w = interfaceC0044o.getBaseHeight();
        } else {
            jMo274v = MapRenderer.f196d;
            jMo275w = MapRenderer.f195c;
        }
        AppState.setInt(1479, 0);
        ResourceManager.m953a(VCard.m62a(AppState.getInt(39), IOUtils.m809a(jMo274v), IOUtils.m810b(jMo275w)), jMo274v, jMo275w);
        return 6;
    }

    /* renamed from: L */
    public static final int m400L() {
        char c;
        Account abstractC0037hM616i = AppState.getAccount();
        if (abstractC0037hM616i.isConnecting()) {
            c = 300;
        } else {
            AppState.getVector(1241).removeElement(abstractC0037hM616i);
            TabBar.m163a();
            m435ah();
            c = 0;
        }
        if (0 != c) {
            return m338l(300);
        }
        return 25;
    }

    /* renamed from: A */
    public static final int m401A(int i) {
        Contact abstractC0041lM611g = AppState.getCurrentContact();
        switch (i) {
            case 0:
                int iM1235E = abstractC0041lM611g.validateBlock();
                if (0 != iM1235E) {
                    return m338l(iM1235E);
                }
                return 4;
            case 1:
                int iM1236F = abstractC0041lM611g.validateUnblock();
                if (0 != iM1236F) {
                    return m338l(iM1236F);
                }
                return 4;
            default:
                return 0;
        }
    }

    /* renamed from: M */
    public static final void m402M() {
        Screen c0013amM75b = ScreenManager.m75b(4517);
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        Enumeration enumerationElements = c0028ba.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
            if (c0052w != c0028ba.m746W()) {
                MenuItem c0032cM898b = MenuItem.m886c().m896a(234).m898b(NetworkUtils.m1215a(NetworkUtils.m1217h().append(c0052w.f410b).append(' ').append('['))).m901a(StringUtils.intern(Integer.toString(c0052w.f412d)), 1, 0).m898b(NetworkUtils.m1215a(NetworkUtils.m1217h().append('/').append(c0052w.f411c).append(']')));
                c0032cM898b.f265d = c0052w;
                c0013amM75b.m225a(c0032cM898b);
            }
        }
        ScreenManager.m71b(c0013amM75b);
    }

    /* renamed from: a */
    public static final ByteBuffer m403a(MrimAccount c0028ba, String str, int i) {
        ByteBuffer c0043nM1360p = new ByteBuffer().writeIntLE(0);
        int iIndexOf = str.indexOf(64);
        return c0028ba.m719a(new Object[]{m321a(c0028ba, 4137, c0043nM1360p.writeStringLatin1(StringUtils.prefix(str, iIndexOf)).writeIntLE(1).writeStringLatin1(StringUtils.suffix(str, iIndexOf + 1))), ResourceManager.m967e(i)});
    }

    /* renamed from: j */
    public static final int m404j(Object obj) {
        long jMo274v;
        long jMo275w;
        Contact abstractC0041l = (Contact) obj;
        String strM584b = AppState.getString(1249);
        ListItem interfaceC0044o = MapRenderer.f203k;
        if (interfaceC0044o == null || !interfaceC0044o.isSelected()) {
            jMo274v = MapRenderer.f196d;
            jMo275w = MapRenderer.f195c;
        } else {
            jMo274v = interfaceC0044o.getWidth();
            jMo275w = interfaceC0044o.getBaseHeight();
        }
        int iM1233b = abstractC0041l.sendMessage(ResourceManager.m975a(jMo274v, jMo275w, AppState.getInt(39), strM584b));
        if (0 != iM1233b) {
            return m338l(iM1233b);
        }
        return 0;
    }

    /* renamed from: k */
    public static final int m405k(Object obj) {
        AppState.setObject(43, (Object) ((MapPoint) obj).m272d());
        return 0;
    }

    /* renamed from: i */
    public static final int m406i(String str) {
        if (!AppState.getString(402).equals(str)) {
            return 0;
        }
        if (MapRenderer.f202j != null) {
            MapRenderer.f202j.m269c();
        }
        XmppContactGroup.m1048a(AppState.getVector(1401));
        AppState.setInt(253, 0);
        MmpContact.m185f();
        MapRenderer.f200h = true;
        XmppContactGroup.f312c = System.currentTimeMillis();
        MapRenderer.f200h = true;
        return 0;
    }

    /* renamed from: l */
    public static final int m407l(Object obj) {
        if (obj == null) {
            return 0;
        }
        AppState.setFromBuffer(1279, NetworkUtils.m1196e().append(obj));
        return 0;
    }

    /* renamed from: B */
    public static final int m408B(int i) {
        int iM433Q = m433Q();
        if (i == 15) {
            if (iM433Q == 0) {
                return m338l(551);
            }
            if (iM433Q == 1) {
                m444ai();
                return 4;
            }
        } else {
            if (i == 3) {
                Vector vectorM443V = m443V();
                int iM285a = m285a(vectorM443V, 3, true);
                if (iM285a != 39) {
                    return iM285a;
                }
                vectorM443V.insertElementAt(vectorM443V, 0);
                return 39;
            }
            if (i == 152) {
                return m285a(m439R(), 152, true);
            }
        }
        if (i == 10) {
            return NetworkUtils.m1201f();
        }
        if (i != 6) {
            return 0;
        }
        AppState.setInt(1414, 1);
        return 0;
    }

    /* renamed from: m */
    public static final int m409m(Object obj) {
        int iM1233b = ((Contact) obj).sendMessage(AppState.getString(43));
        if (0 != iM1233b) {
            return m338l(iM1233b);
        }
        return 0;
    }

    /* renamed from: N */
    public static final boolean m410N() {
        return AppState.getVector(1244).size() != 0;
    }

    /* renamed from: b */
    public static final void m411b(MrimAccount c0028ba) {
        AppState.getVector(1244).removeElement(c0028ba);
        TabBar.m178j();
    }

    /* renamed from: O */
    public static final void m412O() {
        AppState.getVector(1244).removeAllElements();
        TabBar.m178j();
    }

    /* renamed from: P */
    public static final int m413P() {
        int i = 0;
        Vector vectorM614m = AppState.getVector(1243);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return i;
            }
            i |= ((Contact) vectorM614m.elementAt(size)).flags;
        }
    }

    /* renamed from: a */
    public static final void m414a(Contact abstractC0041l) {
        m356y();
        Vector vectorM614m = AppState.getVector(1243);
        if (vectorM614m.contains(abstractC0041l)) {
            return;
        }
        vectorM614m.addElement(abstractC0041l);
        TabBar.m178j();
    }

    /* renamed from: b */
    public static final void m415b(Contact abstractC0041l) {
        Vector vectorM614m = AppState.getVector(1243);
        if (vectorM614m.contains(abstractC0041l)) {
            Utils.removeFrom(vectorM614m, abstractC0041l);
            TabBar.m178j();
        }
    }

    /* renamed from: b */
    public static final boolean m416b(Account abstractC0037h) {
        if (abstractC0037h == null) {
            return false;
        }
        Vector vectorM614m = AppState.getVector(1243);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return false;
            }
        } while (((Contact) vectorM614m.elementAt(size)).account != abstractC0037h);
        return true;
    }

    /* renamed from: c */
    public static final int m417c(Account abstractC0037h) {
        if (abstractC0037h == null) {
            return 16384;
        }
        Vector vectorM614m = AppState.getVector(1243);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return abstractC0037h.getIconId();
            }
        } while (((Contact) vectorM614m.elementAt(size)).account != abstractC0037h);
        return 16384;
    }

    /* renamed from: c */
    public static final void m418c(Contact abstractC0041l) {
        abstractC0041l.clearStatus();
        AppState.getVector(1242).removeElement(abstractC0041l);
        f152f = true;
    }

    /* renamed from: a */
    public static final void m419a(Account abstractC0037h, int i) {
        abstractC0037h.resetSyncIfChanged(m431af());
        int[] iArr = abstractC0037h.syncArray;
        iArr[0] = iArr[0] + i;
        iArr[2] = iArr[2] + i;
        iArr[4] = iArr[4] + i;
        iArr[6] = iArr[6] + i;
        AppState.addInt(2, i);
        AppState.addInt(4, i);
        AppState.addInt(6, i);
        AppState.addInt(8, i);
        AppState.addInt(293, i);
    }

    /* renamed from: b */
    public static final void m420b(Account abstractC0037h, int i) {
        abstractC0037h.resetSyncIfChanged(m431af());
        int[] iArr = abstractC0037h.syncArray;
        iArr[1] = iArr[1] + i;
        iArr[3] = iArr[3] + i;
        iArr[5] = iArr[5] + i;
        iArr[7] = iArr[7] + i;
        AppState.addInt(3, i);
        AppState.addInt(5, i);
        AppState.addInt(7, i);
        AppState.addInt(9, i);
        AppState.addInt(294, i);
    }

    /* renamed from: a */
    public static final void m421a(Account abstractC0037h, ByteBuffer c0043n) {
        m419a(abstractC0037h, c0043n.length);
    }

    /* renamed from: C */
    public static final void m422C(int i) {
        m431af();
        AppState.addInt(10, i);
        AppState.addInt(12, i);
        AppState.addInt(14, i);
        AppState.addInt(16, i);
        AppState.addInt(293, i);
    }

    /* renamed from: D */
    public static final void m423D(int i) {
        m431af();
        AppState.addInt(11, i);
        AppState.addInt(13, i);
        AppState.addInt(15, i);
        AppState.addInt(17, i);
        AppState.addInt(294, i);
    }

    /* renamed from: E */
    public static final void m424E(int i) {
        m431af();
        AppState.addInt(18, i);
        AppState.addInt(20, i);
        AppState.addInt(22, i);
        AppState.addInt(24, i);
        AppState.addInt(293, i);
    }

    /* renamed from: F */
    public static final void m425F(int i) {
        m431af();
        AppState.addInt(19, i);
        AppState.addInt(21, i);
        AppState.addInt(23, i);
        AppState.addInt(25, i);
        AppState.addInt(294, i);
    }

    /* renamed from: G */
    public static final void m426G(int i) {
        m431af();
        AppState.addInt(26, i);
        AppState.addInt(28, i);
        AppState.addInt(30, i);
        AppState.addInt(32, i);
        AppState.addInt(293, i);
    }

    /* renamed from: H */
    public static final void m427H(int i) {
        m431af();
        AppState.addInt(27, i);
        AppState.addInt(29, i);
        AppState.addInt(31, i);
        AppState.addInt(33, i);
        AppState.addInt(294, i);
    }

    /* renamed from: a */
    public static final int m428a(int i, int i2, int i3) {
        return AppState.getInt(2 + (i << 3) + (i2 << 1) + i3);
    }

    /* renamed from: b */
    public static final int m429b(int i, int i2) {
        return m428a(0, i, i2) + m428a(1, i, i2) + m428a(2, i, i2) + m428a(3, i, i2);
    }

    /* renamed from: b */
    public static final void m430b(int i, int i2, int i3) {
        AppState.setInt(2 + (i << 3) + (i2 << 1) + i3, 0);
    }

    /* renamed from: af */
    private static final int m431af() {
        int iM624l = AppState.getDateCode();
        int iM586d = AppState.getInt(1);
        if (iM624l != iM586d) {
            for (int i = 0; i < 4; i++) {
                int i2 = i << 3;
                AppState.setInt(i2 + 4, 0);
                AppState.setInt(i2 + 5, 0);
                if ((iM624l >>> 8) != (iM586d >>> 8)) {
                    AppState.setInt(i2 + 6, 0);
                    AppState.setInt(i2 + 7, 0);
                }
            }
            AppState.setInt(1, iM624l);
        }
        return iM624l;
    }

    /* renamed from: ag */
    private static void m432ag() {
        Vector vectorM1213g = NetworkUtils.m1213g();
        ByteBuffer c0043nM851h = XmppMailRuProtocol.m851h(NetworkUtils.m1221a(6513505));
        while (c0043nM851h.length > 0) {
            try {
                Account abstractC0037h = null;
                byte bM1344o = c0043nM851h.readByte();
                switch (bM1344o & 7) {
                    case 0:
                        MrimAccount c0028ba = new MrimAccount(c0043nM851h);
                        abstractC0037h = c0028ba;
                        vectorM1213g.addElement(c0028ba);
                        break;
                    case 1:
                        MmpProtocol c0033d = new MmpProtocol(c0043nM851h);
                        abstractC0037h = c0033d;
                        vectorM1213g.addElement(c0033d);
                        break;
                    case 2:
                        XmppProtocol c0005ae = new XmppProtocol(c0043nM851h);
                        abstractC0037h = c0005ae;
                        vectorM1213g.addElement(c0005ae);
                        break;
                    case 3:
                        XmppMailRuProtocol c0031bd = new XmppMailRuProtocol(c0043nM851h);
                        abstractC0037h = c0031bd;
                        vectorM1213g.addElement(c0031bd);
                        break;
                }
                if ((bM1344o & 8) != 0) {
                    abstractC0037h.loadProperties(c0043nM851h);
                }
            } catch (Throwable unused) {
            }
        }
        AppState.pool[1241] = vectorM1213g;
    }

    /* renamed from: Q */
    public static final int m433Q() {
        return AppState.getVector(1241).size();
    }

    /* renamed from: I */
    public static final Account m434I(int i) {
        return (Account) AppState.getVector(1241).elementAt(i);
    }

    /* renamed from: ah */
    private static final void m435ah() {
        m436a(false, false);
        AppState.saveDelta(true);
    }

    /* renamed from: a */
    public static final void m436a(boolean z, boolean z2) {
        try {
            ByteBuffer c0043n = new ByteBuffer();
            Vector vectorM614m = AppState.getVector(1241);
            if (z2) {
                c0043n.ensureCapacity(20480);
                while (vectorM614m.size() > 0) {
                    ((Account) Utils.dequeue(vectorM614m)).serializeAccount(c0043n, z, true).saveProperties(c0043n);
                }
            } else {
                c0043n.ensureCapacity(3072);
                for (int i = 0; i < vectorM614m.size(); i++) {
                    ((Account) vectorM614m.elementAt(i)).serializeAccount(c0043n, z, false).saveProperties(c0043n);
                }
            }
            XmppMailRuProtocol.m852a(NetworkUtils.m1221a(6513505), c0043n, z);
        } catch (Throwable unused) {
        }
    }

    /* renamed from: a */
    public static final int m437a(int i, Account abstractC0037h, String str, String str2) {
        Account abstractC0037h2;
        if (StringUtils.isEmpty(str)) {
            return 301;
        }
        if (StringUtils.isEmpty(str2)) {
            return 306;
        }
        Vector vectorM614m = AppState.getVector(1241);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size >= 0) {
                Account abstractC0037h3 = (Account) vectorM614m.elementAt(size);
                if (i == abstractC0037h3.getType() && str.equals(abstractC0037h3.login)) {
                    abstractC0037h2 = abstractC0037h3;
                    break;
                }
            } else {
                abstractC0037h2 = null;
                break;
            }
        }
        Account abstractC0037h4 = abstractC0037h2;
        if (abstractC0037h != null) {
            if (abstractC0037h4 == null || abstractC0037h4 == abstractC0037h) {
                return abstractC0037h.setCredentials(str, str2);
            }
            return 307;
        }
        if (abstractC0037h4 != null) {
            return 307;
        }
        Vector vectorM614m2 = AppState.getVector(1241);
        Vector vectorM614m3 = AppState.getVector(1241);
        int size2 = vectorM614m3.size();
        int i2 = 0;
        while (true) {
            boolean z = false;
            int i3 = size2;
            while (true) {
                i3--;
                if (i3 < 0) {
                    break;
                }
                if (((Account) vectorM614m3.elementAt(i3)).accountId == i2) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                break;
            }
            i2++;
        }
        int i4 = i2;
        if (i == 0) {
            vectorM614m2.addElement(new MrimAccount(i4, str, str2));
        } else if (i == 1) {
            vectorM614m2.addElement(new MmpProtocol(i4, str, str2));
        } else if (i == 2) {
            vectorM614m2.addElement(new XmppProtocol(i4, str, str2));
        } else if (i == 3) {
            vectorM614m2.addElement(new XmppMailRuProtocol(i4, str, str2));
        }
        TabBar.m163a();
        m435ah();
        return 0;
    }

    /* renamed from: b */
    public static final Account m438b(int i, String str) {
        Vector vectorM614m = AppState.getVector(1241);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return null;
            }
            Account abstractC0037h = (Account) vectorM614m.elementAt(size);
            if (str.equals(abstractC0037h.login) && abstractC0037h.getType() == i) {
                return abstractC0037h;
            }
        }
    }

    /* renamed from: R */
    public static final Vector m439R() {
        Vector vectorM1213g = NetworkUtils.m1213g();
        Vector vectorM614m = AppState.getVector(1241);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return vectorM1213g;
            }
            Object objElementAt = vectorM614m.elementAt(size);
            if (objElementAt instanceof MrimAccount) {
                vectorM1213g.insertElementAt(objElementAt, 0);
            }
        }
    }

    /* renamed from: S */
    public static final Vector m440S() {
        Vector vectorM439R = m439R();
        int size = vectorM439R.size();
        while (true) {
            size--;
            if (size < 0) {
                return vectorM439R;
            }
            if (!m447a(vectorM439R, size).isConnected()) {
                vectorM439R.removeElementAt(size);
            }
        }
    }

    /* renamed from: T */
    public static final Vector m441T() {
        Vector vectorM439R = m439R();
        int size = vectorM439R.size();
        while (true) {
            size--;
            if (size < 0) {
                return vectorM439R;
            }
            if (m447a(vectorM439R, size).syncSeq == 0) {
                vectorM439R.removeElementAt(size);
            }
        }
    }

    /* renamed from: U */
    public static final int m442U() {
        int i = 0;
        Vector vectorM439R = m439R();
        int size = vectorM439R.size();
        while (true) {
            size--;
            if (size < 0) {
                NetworkUtils.m1212a(vectorM439R);
                return i;
            }
            i += m447a(vectorM439R, size).syncSeq;
        }
    }

    /* renamed from: V */
    public static final Vector m443V() {
        Vector vectorM1213g = NetworkUtils.m1213g();
        Vector vectorM614m = AppState.getVector(1241);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return vectorM1213g;
            }
            vectorM1213g.insertElementAt(vectorM614m.elementAt(size), 0);
        }
    }

    /* renamed from: ai */
    private static void m444ai() {
        boolean z = true;
        int size = AppState.getVector(1241).size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (m434I(size).isConnecting()) {
                z = false;
            }
        }
        int size2 = AppState.getVector(1241).size();
        while (true) {
            size2--;
            if (size2 < 0) {
                return;
            }
            Account abstractC0037hM434I = m434I(size2);
            if (abstractC0037hM434I.isConnecting()) {
                if (!z) {
                    abstractC0037hM434I.disconnect();
                }
            } else if (z) {
                abstractC0037hM434I.connect(0);
            }
        }
    }

    /* renamed from: W */
    public static final Vector m445W() {
        Vector vectorM1213g = NetworkUtils.m1213g();
        Vector vectorM614m = AppState.getVector(1241);
        int iM541c = Utils.m541c(vectorM614m);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                return vectorM1213g;
            }
            Vector vectorM1078P = ((Account) vectorM614m.elementAt(iM541c)).getAllContacts();
            int iM541c2 = Utils.m541c(vectorM1078P);
            while (true) {
                iM541c2--;
                if (iM541c2 < 0) {
                    break;
                }
                vectorM1213g.addElement(vectorM1078P.elementAt(iM541c2));
            }
            NetworkUtils.m1212a(vectorM1078P);
        }
    }

    /* renamed from: d */
    public static final Vector m446d(Account abstractC0037h) {
        if (abstractC0037h == null) {
            Vector vectorM1213g = NetworkUtils.m1213g();
            int iM433Q = m433Q();
            while (true) {
                iM433Q--;
                if (iM433Q < 0) {
                    return vectorM1213g;
                }
                Account abstractC0037hM434I = m434I(iM433Q);
                int size = abstractC0037hM434I.groups.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    vectorM1213g.addElement(abstractC0037hM434I.getGroup(size));
                }
            }
        } else {
            Vector vectorM1213g2 = NetworkUtils.m1213g();
            int iM433Q2 = m433Q();
            while (true) {
                iM433Q2--;
                if (iM433Q2 < 0) {
                    return vectorM1213g2;
                }
                Account abstractC0037hM434I2 = m434I(iM433Q2);
                if (abstractC0037hM434I2 == abstractC0037h) {
                    int size2 = abstractC0037hM434I2.groups.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        ContactGroup abstractC0046qM1082g = abstractC0037hM434I2.getGroup(size2);
                        if (abstractC0046qM1082g != abstractC0037hM434I2.defaultGroup && abstractC0046qM1082g != abstractC0037hM434I2.onlineGroup && abstractC0046qM1082g != abstractC0037hM434I2.offlineGroup && abstractC0046qM1082g != abstractC0037hM434I2.blockedGroup) {
                            vectorM1213g2.addElement(abstractC0046qM1082g);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: a */
    private static final MrimAccount m447a(Vector vector, int i) {
        return (MrimAccount) vector.elementAt(i);
    }

    /* renamed from: X */
    public static final Vector m448X() {
        Vector vectorM1213g = NetworkUtils.m1213g();
        Vector vectorM439R = m439R();
        int size = vectorM439R.size();
        while (true) {
            size--;
            if (size < 0) {
                return vectorM1213g;
            }
            Vector vectorM1078P = m447a(vectorM439R, size).getAllContacts();
            int size2 = vectorM1078P.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                MrimContact c0035f = (MrimContact) vectorM1078P.elementAt(size2);
                if (c0035f.m1000q()) {
                    vectorM1213g.addElement(c0035f);
                }
            }
            NetworkUtils.m1212a(vectorM1078P);
        }
    }

    /* renamed from: Y */
    public static final Vector m449Y() {
        Vector vectorM1213g = NetworkUtils.m1213g();
        Vector vectorM439R = m439R();
        int size = vectorM439R.size();
        while (true) {
            size--;
            if (size < 0) {
                return vectorM1213g;
            }
            MrimAccount c0028baM447a = m447a(vectorM439R, size);
            if (c0028baM447a.f231g.m59c()) {
                vectorM1213g.addElement(c0028baM447a);
            }
        }
    }

    /* renamed from: a */
    public static final void m450a(String str, String str2, String str3, String str4, String str5) {
        AppState.setObject(1240, (Object) str5);
        AppState.setFromBuffer(1239, Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(NetworkUtils.m1217h(), 262572, str), 262576, str2), 524724, str3), 590268, str4), 524741, str5));
        m304a(13, m451aj());
    }

    /* renamed from: aj */
    private static final int m451aj() {
        return AppState.getBytes(1004) != null ? 60000 : 300000;
    }

    /* renamed from: Z */
    public static final String[] m452Z() {
        if (!AppState.getBool(1468)) {
            m450a(null, null, null, null, null);
        } else if (m307b(13, m451aj())) {
            AppState.clearIndex(1239);
        }
        String strM584b = AppState.getString(1239);
        if (strM584b == null) {
            return null;
        }
        String[] strArr = new String[2];
        strArr[0] = strM584b;
        String strM584b2 = AppState.getString(1240);
        strArr[1] = strM584b2 != null ? strM584b2 : AppState.getString(308);
        return strArr;
    }

    /* renamed from: a */
    public static final void m453a(Object obj, int i, int i2) {
        Object obj2 = new Object();
        f150d = obj2;
        synchronized (obj2) {
            AppState.init(obj);
            AppState.clearRange(1022, 1023);
            AppState.pool[1373] = NetworkUtils.m1213g();
            AppState.pool[1272] = NetworkUtils.m1213g();
            ScreenManager.m65a();
            AppState.pool[1243] = NetworkUtils.m1213g();
            AppState.pool[1244] = NetworkUtils.m1213g();
            AppState.pool[1358] = NetworkUtils.m1213g();
            AppState.pool[1359] = NetworkUtils.m1213g();
            new AsyncTask(3);
            m432ag();
            AppState.pool[1247] = NetworkUtils.m1213g();
            m458ak();
            AppState.pool[1242] = NetworkUtils.m1213g();
            ResourceManager.m927a();
            ResourceManager.m940d();
            AppState.setInt(2, 0);
            AppState.setInt(3, 0);
            AppState.setInt(10, 0);
            AppState.setInt(11, 0);
            AppState.setInt(18, 0);
            AppState.setInt(19, 0);
            AppState.setInt(26, 0);
            AppState.setInt(27, 0);
            AppState.pool[1402] = NetworkUtils.m1213g();
            XmppMailRuProtocol.m845v();
            AppState.pool[1371] = new MainCanvas(i, i2);
            AppState.clearRange(332, 333);
            TabBar.m163a();
            AppState.pool[430] = Utils.m536a(AppState.getBytes(430));
            AppState.pool[1357] = new byte[1];
            try {
                m369N(1004);
                m369N(1005);
                m367C();
                m368D();
            } catch (Throwable unused) {
                AppState.clearRange(1004, 1007);
            }
            m304a(0, m376E());
            AppState.addInt(291, 1);
            AppState.saveDelta(true);
            if (AppState.getBool(217)) {
                m390G();
            } else {
                int iM433Q = m433Q();
                int i3 = iM433Q;
                if (iM433Q == 0) {
                    ScreenManager.m70a(ScreenManager.m75b(4381));
                    m334q();
                } else {
                    while (true) {
                        i3--;
                        if (i3 < 0) {
                            break;
                        } else {
                            m328a(m434I(i3));
                        }
                    }
                    ContactListManager.m152a();
                    m334q();
                }
            }
            new AsyncTask(13);
            new AsyncTask(0);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:286:0x0b19  */
    /* JADX WARN: Removed duplicated region for block: B:635:0x1396  */
    /* JADX WARN: Removed duplicated region for block: B:646:0x13cd  */
    /* JADX WARN: Removed duplicated region for block: B:714:0x151f  */
    /* JADX WARN: Removed duplicated region for block: B:755:0x15ff  */
    /* JADX WARN: Removed duplicated region for block: B:794:0x16cc  */
    /* JADX WARN: Removed duplicated region for block: B:797:0x16d3  */
    /* JADX WARN: Removed duplicated region for block: B:804:0x16e9 A[Catch: all -> 0x1e12, Throwable -> 0x1f03, TryCatch #0 {, blocks: (B:5:0x0009, B:7:0x000f, B:9:0x001b, B:11:0x0023, B:13:0x0031, B:14:0x0047, B:15:0x0048, B:16:0x0061, B:23:0x006d, B:20:0x0069, B:22:0x006c, B:24:0x0071, B:39:0x00ef, B:25:0x0082, B:26:0x008e, B:28:0x0096, B:34:0x00c2, B:36:0x00d7, B:37:0x00e1, B:30:0x00a0, B:32:0x00b5, B:38:0x00e9, B:41:0x00f7, B:45:0x0131, B:42:0x0110, B:44:0x012c, B:47:0x0139, B:49:0x013f, B:51:0x0149, B:53:0x0150, B:54:0x016b, B:56:0x0179, B:64:0x01a9, B:65:0x01b4, B:70:0x04a7, B:72:0x04b5, B:74:0x04c9, B:75:0x04d5, B:77:0x04dd, B:79:0x04e3, B:81:0x04ec, B:83:0x04f6, B:85:0x0500, B:87:0x050a, B:89:0x0514, B:96:0x0532, B:91:0x0521, B:97:0x0535, B:98:0x0539, B:100:0x0544, B:102:0x0550, B:104:0x0559, B:105:0x0589, B:106:0x05a8, B:107:0x05be, B:108:0x05d4, B:109:0x05ea, B:110:0x05fd, B:111:0x0617, B:113:0x0623, B:114:0x0626, B:116:0x062f, B:118:0x063a, B:122:0x066e, B:119:0x0655, B:121:0x0668, B:124:0x0676, B:125:0x0682, B:127:0x068b, B:128:0x068f, B:130:0x0695, B:134:0x06a6, B:142:0x06c5, B:144:0x06e0, B:145:0x06ee, B:147:0x06fc, B:151:0x070b, B:149:0x0704, B:164:0x0743, B:179:0x0783, B:181:0x0792, B:184:0x07aa, B:188:0x07c7, B:193:0x07db, B:201:0x07f6, B:203:0x0802, B:206:0x0812, B:208:0x0833, B:210:0x0871, B:209:0x0863, B:212:0x087b, B:214:0x08b5, B:213:0x0890, B:216:0x08bf, B:220:0x08ed, B:217:0x08cb, B:219:0x08e3, B:222:0x08f7, B:226:0x0925, B:223:0x0903, B:225:0x091b, B:228:0x092f, B:229:0x0937, B:231:0x096f, B:230:0x094a, B:235:0x0982, B:237:0x0991, B:240:0x09a1, B:241:0x09bc, B:261:0x0a55, B:242:0x09d0, B:244:0x09f7, B:248:0x0a08, B:250:0x0a10, B:252:0x0a1a, B:253:0x0a20, B:254:0x0a25, B:256:0x0a31, B:257:0x0a36, B:260:0x0a48, B:263:0x0a5f, B:268:0x0a70, B:270:0x0a89, B:272:0x0aab, B:274:0x0abb, B:278:0x0aee, B:275:0x0ad0, B:277:0x0ae3, B:282:0x0afc, B:285:0x0b0e, B:288:0x0b1f, B:290:0x0b2c, B:291:0x0b34, B:299:0x0b58, B:306:0x0b76, B:307:0x0b85, B:309:0x0b8f, B:311:0x0ba6, B:321:0x0bd0, B:323:0x0bda, B:325:0x0be5, B:327:0x0bee, B:329:0x0bfd, B:331:0x0c0c, B:332:0x0c15, B:346:0x0c5d, B:333:0x0c23, B:345:0x0c5a, B:348:0x0c65, B:350:0x0c6f, B:358:0x0c9c, B:360:0x0ca8, B:363:0x0cb8, B:379:0x0d42, B:364:0x0ccc, B:366:0x0d00, B:371:0x0d10, B:373:0x0d19, B:376:0x0d2d, B:378:0x0d36, B:388:0x0d67, B:390:0x0d76, B:393:0x0d86, B:395:0x0d9e, B:405:0x0de9, B:396:0x0dab, B:398:0x0dc5, B:400:0x0dd1, B:402:0x0dda, B:403:0x0ddf, B:404:0x0de4, B:411:0x0e02, B:413:0x0e0e, B:416:0x0e1e, B:417:0x0e45, B:418:0x0e73, B:421:0x0e9b, B:424:0x0ea4, B:426:0x0eb8, B:427:0x0ed6, B:429:0x0efc, B:431:0x0f1a, B:430:0x0f0b, B:434:0x0f24, B:435:0x0f47, B:437:0x0f52, B:438:0x0f5b, B:440:0x0f72, B:442:0x0f7e, B:445:0x0f8e, B:447:0x0f9a, B:452:0x0fb4, B:473:0x100c, B:482:0x1032, B:524:0x10dc, B:533:0x1104, B:547:0x1141, B:549:0x114f, B:551:0x1159, B:555:0x116b, B:557:0x117c, B:558:0x118b, B:561:0x1196, B:575:0x11d2, B:577:0x11e2, B:578:0x11f7, B:580:0x1206, B:581:0x1248, B:583:0x1255, B:585:0x125d, B:586:0x126f, B:588:0x1279, B:595:0x1292, B:986:0x1db5, B:988:0x1dbd, B:990:0x1dc6, B:991:0x1dd4, B:993:0x1de2, B:995:0x1deb, B:997:0x1df6, B:999:0x1dff, B:1000:0x1e06, B:598:0x129d, B:63:0x01a1, B:59:0x018e, B:600:0x12a5, B:602:0x12ac, B:603:0x12b4, B:604:0x12e8, B:606:0x12f0, B:608:0x12f9, B:609:0x12fd, B:615:0x1338, B:617:0x1341, B:619:0x134d, B:621:0x1358, B:623:0x1360, B:718:0x152c, B:730:0x156f, B:733:0x157e, B:736:0x158a, B:739:0x1598, B:742:0x15a5, B:744:0x15af, B:745:0x15b5, B:747:0x15bf, B:748:0x15c9, B:750:0x15d2, B:754:0x15fb, B:721:0x153f, B:726:0x1559, B:628:0x1374, B:630:0x137f, B:632:0x1387, B:637:0x139d, B:639:0x13a9, B:644:0x13c0, B:648:0x13d4, B:652:0x13e2, B:657:0x13f9, B:664:0x1415, B:667:0x1425, B:670:0x1433, B:673:0x1441, B:676:0x1456, B:679:0x1466, B:681:0x1471, B:687:0x1488, B:682:0x1478, B:690:0x149c, B:693:0x14b4, B:697:0x14c5, B:700:0x14d8, B:702:0x14de, B:704:0x14e7, B:708:0x14ff, B:710:0x1505, B:712:0x150e, B:756:0x1602, B:757:0x1608, B:758:0x160e, B:759:0x1618, B:761:0x1623, B:763:0x1631, B:765:0x1647, B:767:0x164f, B:769:0x165c, B:774:0x166d, B:778:0x1688, B:780:0x168e, B:784:0x16a6, B:801:0x16de, B:802:0x16e1, B:804:0x16e9, B:806:0x16f2, B:808:0x1727, B:810:0x1733, B:812:0x173d, B:818:0x1758, B:820:0x1762, B:822:0x1770, B:826:0x17a2, B:828:0x17ab, B:830:0x17b5, B:833:0x17c4, B:837:0x17d7, B:839:0x17e2, B:841:0x17ea, B:842:0x17f1, B:843:0x180c, B:844:0x1818, B:846:0x1824, B:848:0x1843, B:858:0x1863, B:857:0x185c, B:788:0x16b7, B:789:0x16ba, B:770:0x1662, B:860:0x187b, B:862:0x1891, B:864:0x18a2, B:866:0x18c4, B:868:0x18cc, B:869:0x18da, B:871:0x1906, B:872:0x1941, B:874:0x1961, B:875:0x1967, B:877:0x197a, B:878:0x198a, B:880:0x1992, B:881:0x1998, B:883:0x199f, B:889:0x19cf, B:891:0x19d6, B:893:0x19e6, B:895:0x1a13, B:898:0x1a2f, B:900:0x1a36, B:901:0x1a44, B:903:0x1a4b, B:905:0x1a57, B:906:0x1aa4, B:907:0x1abc, B:909:0x1ac3, B:910:0x1ad6, B:911:0x1af8, B:912:0x1afe, B:914:0x1b4b, B:915:0x1b5b, B:924:0x1ba1, B:916:0x1b64, B:923:0x1b95, B:919:0x1b83, B:926:0x1ba8, B:928:0x1bb8, B:929:0x1bc8, B:933:0x1bdd, B:937:0x1bf0, B:938:0x1bfc, B:939:0x1c09, B:940:0x1c10, B:941:0x1c17, B:943:0x1c2e, B:945:0x1c35, B:949:0x1c4d, B:951:0x1c65, B:955:0x1c7e, B:957:0x1c8d, B:961:0x1c9e, B:967:0x1d04, B:962:0x1cdb, B:964:0x1cef, B:969:0x1d0b, B:973:0x1d23, B:975:0x1d34, B:974:0x1d2f, B:976:0x1d53, B:979:0x1d61, B:981:0x1d84, B:985:0x1d9c, B:1002:0x1e0e), top: B:1058:0x0009, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:833:0x17c4 A[Catch: all -> 0x1e12, Throwable -> 0x1f03, TryCatch #0 {, blocks: (B:5:0x0009, B:7:0x000f, B:9:0x001b, B:11:0x0023, B:13:0x0031, B:14:0x0047, B:15:0x0048, B:16:0x0061, B:23:0x006d, B:20:0x0069, B:22:0x006c, B:24:0x0071, B:39:0x00ef, B:25:0x0082, B:26:0x008e, B:28:0x0096, B:34:0x00c2, B:36:0x00d7, B:37:0x00e1, B:30:0x00a0, B:32:0x00b5, B:38:0x00e9, B:41:0x00f7, B:45:0x0131, B:42:0x0110, B:44:0x012c, B:47:0x0139, B:49:0x013f, B:51:0x0149, B:53:0x0150, B:54:0x016b, B:56:0x0179, B:64:0x01a9, B:65:0x01b4, B:70:0x04a7, B:72:0x04b5, B:74:0x04c9, B:75:0x04d5, B:77:0x04dd, B:79:0x04e3, B:81:0x04ec, B:83:0x04f6, B:85:0x0500, B:87:0x050a, B:89:0x0514, B:96:0x0532, B:91:0x0521, B:97:0x0535, B:98:0x0539, B:100:0x0544, B:102:0x0550, B:104:0x0559, B:105:0x0589, B:106:0x05a8, B:107:0x05be, B:108:0x05d4, B:109:0x05ea, B:110:0x05fd, B:111:0x0617, B:113:0x0623, B:114:0x0626, B:116:0x062f, B:118:0x063a, B:122:0x066e, B:119:0x0655, B:121:0x0668, B:124:0x0676, B:125:0x0682, B:127:0x068b, B:128:0x068f, B:130:0x0695, B:134:0x06a6, B:142:0x06c5, B:144:0x06e0, B:145:0x06ee, B:147:0x06fc, B:151:0x070b, B:149:0x0704, B:164:0x0743, B:179:0x0783, B:181:0x0792, B:184:0x07aa, B:188:0x07c7, B:193:0x07db, B:201:0x07f6, B:203:0x0802, B:206:0x0812, B:208:0x0833, B:210:0x0871, B:209:0x0863, B:212:0x087b, B:214:0x08b5, B:213:0x0890, B:216:0x08bf, B:220:0x08ed, B:217:0x08cb, B:219:0x08e3, B:222:0x08f7, B:226:0x0925, B:223:0x0903, B:225:0x091b, B:228:0x092f, B:229:0x0937, B:231:0x096f, B:230:0x094a, B:235:0x0982, B:237:0x0991, B:240:0x09a1, B:241:0x09bc, B:261:0x0a55, B:242:0x09d0, B:244:0x09f7, B:248:0x0a08, B:250:0x0a10, B:252:0x0a1a, B:253:0x0a20, B:254:0x0a25, B:256:0x0a31, B:257:0x0a36, B:260:0x0a48, B:263:0x0a5f, B:268:0x0a70, B:270:0x0a89, B:272:0x0aab, B:274:0x0abb, B:278:0x0aee, B:275:0x0ad0, B:277:0x0ae3, B:282:0x0afc, B:285:0x0b0e, B:288:0x0b1f, B:290:0x0b2c, B:291:0x0b34, B:299:0x0b58, B:306:0x0b76, B:307:0x0b85, B:309:0x0b8f, B:311:0x0ba6, B:321:0x0bd0, B:323:0x0bda, B:325:0x0be5, B:327:0x0bee, B:329:0x0bfd, B:331:0x0c0c, B:332:0x0c15, B:346:0x0c5d, B:333:0x0c23, B:345:0x0c5a, B:348:0x0c65, B:350:0x0c6f, B:358:0x0c9c, B:360:0x0ca8, B:363:0x0cb8, B:379:0x0d42, B:364:0x0ccc, B:366:0x0d00, B:371:0x0d10, B:373:0x0d19, B:376:0x0d2d, B:378:0x0d36, B:388:0x0d67, B:390:0x0d76, B:393:0x0d86, B:395:0x0d9e, B:405:0x0de9, B:396:0x0dab, B:398:0x0dc5, B:400:0x0dd1, B:402:0x0dda, B:403:0x0ddf, B:404:0x0de4, B:411:0x0e02, B:413:0x0e0e, B:416:0x0e1e, B:417:0x0e45, B:418:0x0e73, B:421:0x0e9b, B:424:0x0ea4, B:426:0x0eb8, B:427:0x0ed6, B:429:0x0efc, B:431:0x0f1a, B:430:0x0f0b, B:434:0x0f24, B:435:0x0f47, B:437:0x0f52, B:438:0x0f5b, B:440:0x0f72, B:442:0x0f7e, B:445:0x0f8e, B:447:0x0f9a, B:452:0x0fb4, B:473:0x100c, B:482:0x1032, B:524:0x10dc, B:533:0x1104, B:547:0x1141, B:549:0x114f, B:551:0x1159, B:555:0x116b, B:557:0x117c, B:558:0x118b, B:561:0x1196, B:575:0x11d2, B:577:0x11e2, B:578:0x11f7, B:580:0x1206, B:581:0x1248, B:583:0x1255, B:585:0x125d, B:586:0x126f, B:588:0x1279, B:595:0x1292, B:986:0x1db5, B:988:0x1dbd, B:990:0x1dc6, B:991:0x1dd4, B:993:0x1de2, B:995:0x1deb, B:997:0x1df6, B:999:0x1dff, B:1000:0x1e06, B:598:0x129d, B:63:0x01a1, B:59:0x018e, B:600:0x12a5, B:602:0x12ac, B:603:0x12b4, B:604:0x12e8, B:606:0x12f0, B:608:0x12f9, B:609:0x12fd, B:615:0x1338, B:617:0x1341, B:619:0x134d, B:621:0x1358, B:623:0x1360, B:718:0x152c, B:730:0x156f, B:733:0x157e, B:736:0x158a, B:739:0x1598, B:742:0x15a5, B:744:0x15af, B:745:0x15b5, B:747:0x15bf, B:748:0x15c9, B:750:0x15d2, B:754:0x15fb, B:721:0x153f, B:726:0x1559, B:628:0x1374, B:630:0x137f, B:632:0x1387, B:637:0x139d, B:639:0x13a9, B:644:0x13c0, B:648:0x13d4, B:652:0x13e2, B:657:0x13f9, B:664:0x1415, B:667:0x1425, B:670:0x1433, B:673:0x1441, B:676:0x1456, B:679:0x1466, B:681:0x1471, B:687:0x1488, B:682:0x1478, B:690:0x149c, B:693:0x14b4, B:697:0x14c5, B:700:0x14d8, B:702:0x14de, B:704:0x14e7, B:708:0x14ff, B:710:0x1505, B:712:0x150e, B:756:0x1602, B:757:0x1608, B:758:0x160e, B:759:0x1618, B:761:0x1623, B:763:0x1631, B:765:0x1647, B:767:0x164f, B:769:0x165c, B:774:0x166d, B:778:0x1688, B:780:0x168e, B:784:0x16a6, B:801:0x16de, B:802:0x16e1, B:804:0x16e9, B:806:0x16f2, B:808:0x1727, B:810:0x1733, B:812:0x173d, B:818:0x1758, B:820:0x1762, B:822:0x1770, B:826:0x17a2, B:828:0x17ab, B:830:0x17b5, B:833:0x17c4, B:837:0x17d7, B:839:0x17e2, B:841:0x17ea, B:842:0x17f1, B:843:0x180c, B:844:0x1818, B:846:0x1824, B:848:0x1843, B:858:0x1863, B:857:0x185c, B:788:0x16b7, B:789:0x16ba, B:770:0x1662, B:860:0x187b, B:862:0x1891, B:864:0x18a2, B:866:0x18c4, B:868:0x18cc, B:869:0x18da, B:871:0x1906, B:872:0x1941, B:874:0x1961, B:875:0x1967, B:877:0x197a, B:878:0x198a, B:880:0x1992, B:881:0x1998, B:883:0x199f, B:889:0x19cf, B:891:0x19d6, B:893:0x19e6, B:895:0x1a13, B:898:0x1a2f, B:900:0x1a36, B:901:0x1a44, B:903:0x1a4b, B:905:0x1a57, B:906:0x1aa4, B:907:0x1abc, B:909:0x1ac3, B:910:0x1ad6, B:911:0x1af8, B:912:0x1afe, B:914:0x1b4b, B:915:0x1b5b, B:924:0x1ba1, B:916:0x1b64, B:923:0x1b95, B:919:0x1b83, B:926:0x1ba8, B:928:0x1bb8, B:929:0x1bc8, B:933:0x1bdd, B:937:0x1bf0, B:938:0x1bfc, B:939:0x1c09, B:940:0x1c10, B:941:0x1c17, B:943:0x1c2e, B:945:0x1c35, B:949:0x1c4d, B:951:0x1c65, B:955:0x1c7e, B:957:0x1c8d, B:961:0x1c9e, B:967:0x1d04, B:962:0x1cdb, B:964:0x1cef, B:969:0x1d0b, B:973:0x1d23, B:975:0x1d34, B:974:0x1d2f, B:976:0x1d53, B:979:0x1d61, B:981:0x1d84, B:985:0x1d9c, B:1002:0x1e0e), top: B:1058:0x0009, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:859:0x1878  */
    /* JADX WARN: Removed duplicated region for block: B:953:0x1c78  */
    /* renamed from: aa */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void m454aa() {
        boolean z;
        boolean z2;
        int i;
        int i2 = 0;
        Object objM179a;
        boolean z3;
        boolean z4 = false;
        int[] iArrM190r;
        int[] iArrM191s;
        Screen c0013amM66b;
        int iM1181a = 0;
        Message c0026azM1415b;
        TextBox textBoxM1028h;
        int i3 = 0;
        MrimAccount c0028ba;
        ChatRoom c0052wM745h;
        Message c0026azM1415b2;
        boolean z5;
        while (!f151e) {
            synchronized (f150d) {
                if (!f151e) {
                    AppState.updateTime();
                    ResourceManager.m928b();
                    if (!MainCanvas.f90d && MainCanvas.f89c != 0 && System.currentTimeMillis() - MainCanvas.f89c > 600) {
                        int i4 = MainCanvas.f86a;
                        int i5 = MainCanvas.f87b;
                        Vector vectorM614m = AppState.getVector(1266);
                        synchronized (vectorM614m) {
                            vectorM614m.addElement(new int[]{8, i4, i5});
                        }
                        MainCanvas.f89c = 0L;
                    }
                    Vector vectorM614m2 = AppState.getVector(1241);
                    int size = vectorM614m2.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            int iM586d = AppState.getInt(1531);
                            Vector vectorM614m3 = AppState.getVector(1242);
                            int size2 = vectorM614m3.size();
                            while (true) {
                                size2--;
                                if (size2 < 0) {
                                    if (f152f && ScreenManager.m66b().f94a == 4 && m305K(1)) {
                                        f152f = false;
                                        AppState.getString(1237);
                                        ContactListManager.m156d();
                                        AppState.clearIndex(1237);
                                        m304a(1, 1000L);
                                    }
                                    Object objM524a = Utils.dequeue(AppState.getVector(1266));
                                    if (objM524a == null) {
                                        Screen c0013amM66b2 = ScreenManager.m66b();
                                        MenuItem c0032cM69e = ScreenManager.m69e();
                                        Object obj = c0032cM69e == null ? null : c0032cM69e.f265d;
                                        String str = c0032cM69e == null ? null : c0032cM69e.f259b;
                                        int iM338l = 0;
                                        switch (ScreenManager.m66b().f94a) {
                                            case 1:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 2:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 3:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 4:
                                                iM1181a = ContactListManager.m160a(c0013amM66b2, obj);
                                                iM338l = iM1181a;
                                                break;
                                            case 5:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 6:
                                                long jCurrentTimeMillis = System.currentTimeMillis();
                                                if (jCurrentTimeMillis - AppState.getLong(1556) > 45) {
                                                    AppState.setLong(1556, jCurrentTimeMillis);
                                                }
                                                if (m305K(10) && MapRenderer.f201i) {
                                                    if (AppState.getBool(276)) {
                                                        if ((MapRenderer.f196d < VCard.f25m || MapRenderer.f196d > VCard.f27o || MapRenderer.f195c > VCard.f28p || MapRenderer.f195c < VCard.f26n || ((long) AppState.getInt(39)) != VCard.f29q) && AppState.getBool(280)) {
                                                            IOUtils.m772f();
                                                        }
                                                    }
                                                    MapRenderer.m652a(false);
                                                }
                                                int iM586d2 = AppState.getInt(1564);
                                                if (iM586d2 >= 0 && AppState.getLong(1556) == jCurrentTimeMillis && !AppState.getBool(1553)) {
                                                    AppState.setLong(1558, MapRenderer.f196d);
                                                    AppState.setLong(1560, MapRenderer.f195c);
                                                    int iM586d3 = AppState.getInt(39);
                                                    long jM315d = (m315d(iM586d3) / m316e(iM586d3)) * 9;
                                                    switch (iM586d2) {
                                                        case 0:
                                                            AppState.setLong(1558, AppState.getLong(1558) + jM315d);
                                                            break;
                                                        case 1:
                                                            AppState.setLong(1558, AppState.getLong(1558) - jM315d);
                                                            break;
                                                        case 2:
                                                            AppState.setLong(1560, AppState.getLong(1560) + jM315d);
                                                            break;
                                                        case 3:
                                                            AppState.setLong(1560, AppState.getLong(1560) - jM315d);
                                                            break;
                                                    }
                                                    MapRenderer.m649a(AppState.getLong(1558), AppState.getLong(1560));
                                                    m304a(10, 500L);
                                                    MapRenderer.m661e();
                                                }
                                                if (AppState.getLong(1556) == jCurrentTimeMillis) {
                                                    MapRenderer.m648b();
                                                }
                                                if (AppState.getBool(277) && m307b(7, 300000L)) {
                                                    m304a(7, 300000L);
                                                    StringUtils.m24d();
                                                    Vector vectorM614m4 = AppState.getVector(1383);
                                                    int size3 = vectorM614m4.size();
                                                    while (true) {
                                                        size3--;
                                                        if (size3 < 0) {
                                                            MapRenderer.f200h = true;
                                                            new AsyncTask(6);
                                                        } else if (3 == ((ResourceManager) vectorM614m4.elementAt(size3)).f281a) {
                                                            vectorM614m4.removeElementAt(size3);
                                                        }
                                                    }
                                                }
                                                if (AppState.getBool(1553)) {
                                                    f153g = true;
                                                }
                                                if (MapRenderer.f212q) {
                                                    MapRenderer.f212q = false;
                                                    z5 = true;
                                                } else {
                                                    z5 = false;
                                                }
                                                iM338l = z5 ? 113 : ConnectionThread.m1166i();
                                                break;
                                            case 7:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 8:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 9:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 10:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 11:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 13:
                                                Object[] objArr = (Object[]) AppState.pool[1271];
                                                Object obj2 = objArr[0];
                                                if (obj2 == null) {
                                                    String str2 = (String) objArr[20];
                                                    if ((str2 != null && Utils.parseInt((Object) str2) == 0) || objArr[3] != null) {
                                                        iM1181a = NetworkUtils.m1181a(objArr);
                                                    }
                                                    iM338l = iM1181a;
                                                    break;
                                                } else {
                                                    m339e(StringUtils.m8a(506, obj2));
                                                }
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 14:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 15:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 16:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 17:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 18:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 19:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 20:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 21:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 22:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 23:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 24:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 25:
                                                iM338l = AppState.pool[1291] != null ? 122 : 0;
                                                break;
                                            case 26:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 27:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 28:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 29:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 30:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 32:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 33:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 34:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 35:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 36:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 37:
                                                Object[] objArrM1156c = ConnectionThread.m1156c(IOUtils.m816k());
                                                if (objArrM1156c != null) {
                                                    int iM586d4 = AppState.getInt(1512);
                                                    int iM818c = IOUtils.m818c(objArrM1156c);
                                                    if (iM818c != 0) {
                                                        iM1181a = iM818c;
                                                    } else {
                                                        ((MrimAccount) AppState.getAccount()).m743e(IOUtils.m819l());
                                                        iM1181a = iM586d4;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 38:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 39:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 40:
                                                Contact abstractC0041lM611g = AppState.getCurrentContact();
                                                iM338l = (abstractC0041lM611g.flags != 0) || abstractC0041lM611g.dirty ? 40 : 0;
                                                break;
                                            case 41:
                                                Object[] objArrM1156c2 = ConnectionThread.m1156c(IOUtils.m816k());
                                                if (objArrM1156c2 != null) {
                                                    int iM818c2 = IOUtils.m818c(objArrM1156c2);
                                                    if (iM818c2 != 0) {
                                                        iM1181a = iM818c2;
                                                    } else {
                                                        Object objM819l = IOUtils.m819l();
                                                        MrimAccount c0028ba2 = (MrimAccount) AppState.getAccount();
                                                        ChatRoom c0052wM745h2 = c0028ba2.m745h(AppState.getInt(1513));
                                                        if (c0052wM745h2 != c0028ba2.m746W()) {
                                                            c0052wM745h2.f413e = JsonParser.getStringValue(objM819l, AppState.getString(591768));
                                                            c0052wM745h2.f414f.removeAllElements();
                                                            Enumeration enumerationElements = ((Vector) JsonParser.getValue(objM819l, AppState.getString(526244))).elements();
                                                            while (enumerationElements.hasMoreElements()) {
                                                                c0052wM745h2.f414f.addElement(enumerationElements.nextElement());
                                                            }
                                                            Enumeration enumerationElements2 = ((Vector) JsonParser.getValue(objM819l, AppState.getString(329636))).elements();
                                                            while (enumerationElements2.hasMoreElements()) {
                                                                Message c0026az = new Message((Hashtable) enumerationElements2.nextElement());
                                                                c0052wM745h2.f416h.put(c0026az.from, c0026az);
                                                            }
                                                            Enumeration enumerationKeys = c0052wM745h2.f416h.keys();
                                                            while (enumerationKeys.hasMoreElements()) {
                                                                String str3 = (String) enumerationKeys.nextElement();
                                                                if (!c0052wM745h2.f414f.contains(str3)) {
                                                                    c0052wM745h2.f416h.remove(str3);
                                                                }
                                                            }
                                                            Enumeration enumerationElements3 = c0052wM745h2.f415g.elements();
                                                            while (enumerationElements3.hasMoreElements()) {
                                                                String str4 = (String) enumerationElements3.nextElement();
                                                                if (!c0052wM745h2.f414f.contains(str4)) {
                                                                    c0052wM745h2.f415g.removeElement(str4);
                                                                }
                                                            }
                                                            c0052wM745h2.f419k = false;
                                                        } else {
                                                            Enumeration enumerationElements4 = ((Vector) objM819l).elements();
                                                            while (enumerationElements4.hasMoreElements()) {
                                                                Message c0026az2 = new Message((Hashtable) enumerationElements4.nextElement());
                                                                c0052wM745h2.f416h.put(c0026az2.from, c0026az2);
                                                            }
                                                        }
                                                        iM1181a = 43;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 42:
                                                Object[] objArrM1156c3 = ConnectionThread.m1156c(IOUtils.m816k());
                                                if (objArrM1156c3 != null) {
                                                    int iM818c3 = IOUtils.m818c(objArrM1156c3);
                                                    if (iM818c3 != 0) {
                                                        iM1181a = iM818c3;
                                                    } else {
                                                        Object objM819l2 = IOUtils.m819l();
                                                        MrimAccount c0028ba3 = (MrimAccount) AppState.getAccount();
                                                        int size4 = ((Vector) objM819l2).size();
                                                        for (int i6 = 0; i6 < size4; i6++) {
                                                            Enumeration enumerationKeys2 = ((Hashtable) JsonParser.getVectorElement(objM819l2, i6)).keys();
                                                            while (enumerationKeys2.hasMoreElements()) {
                                                                String str5 = (String) enumerationKeys2.nextElement();
                                                                ChatRoom c0052wM747i = c0028ba3.m747i(str5);
                                                                ChatRoom c0052wM745h3 = c0028ba3.m745h(AppState.getInt(1527));
                                                                if (c0052wM747i != null && (c0026azM1415b2 = c0052wM747i.m1415b(str5)) != null && c0052wM745h3 != null) {
                                                                    if (c0026azM1415b2.hasFlag(4)) {
                                                                        if (c0052wM745h3 == c0028ba3.m749X()) {
                                                                            c0026azM1415b2.setFlag(4, false);
                                                                        }
                                                                        c0052wM747i.m1419b();
                                                                    }
                                                                    c0052wM747i.m1421d();
                                                                    if (!c0026azM1415b2.isRead()) {
                                                                        c0052wM745h3.m1420c();
                                                                    }
                                                                    c0052wM745h3.f411c++;
                                                                }
                                                                if (c0052wM747i != c0052wM745h3) {
                                                                    c0028ba3.m748j(str5);
                                                                    c0052wM745h3.m1424a(false);
                                                                }
                                                            }
                                                        }
                                                        iM1181a = 43;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 43:
                                                AppState.setInt(1514, c0013amM66b2.f105j);
                                                AppState.setObject(1345, (Object) str);
                                                if (str == null || (c0052wM745h = (c0028ba = (MrimAccount) AppState.getAccount()).m745h(AppState.getInt(1513))) == c0028ba.m746W() || str.equals(c0052wM745h.f413e)) {
                                                    i3 = 0;
                                                    iM338l = i3;
                                                    break;
                                                } else {
                                                    Object obj3 = null;
                                                    Enumeration enumerationElements5 = c0052wM745h.f414f.elements();
                                                    while (enumerationElements5.hasMoreElements()) {
                                                        Hashtable hashtable = c0052wM745h.f416h;
                                                        Object objNextElement = enumerationElements5.nextElement();
                                                        if (hashtable.containsKey(objNextElement)) {
                                                            obj3 = c0052wM745h.f416h.get(objNextElement);
                                                        }
                                                    }
                                                    if (str == (obj3 != null ? ((Message) obj3).from : null)) {
                                                        c0052wM745h.m1424a(true);
                                                        i3 = 41;
                                                    }
                                                    iM338l = i3;
                                                }
                                                break;
                                            case 44:
                                                int iM586d5 = AppState.getInt(1506);
                                                iM338l = 0 != iM586d5 ? m338l(iM586d5) : AppState.pool[1318] == null ? 0 : 73;
                                                break;
                                            case 45:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 46:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 47:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 48:
                                                iM1181a = XmppMailRuProtocol.m873x();
                                                iM338l = iM1181a;
                                                break;
                                            case 49:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 50:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 51:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 52:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 53:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 54:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 55:
                                                NetworkUtils.m1183c();
                                                ResourceManager.m957l();
                                                ResourceManager.m941e();
                                                System.gc();
                                                try {
                                                    Thread.sleep(50);
                                                } catch (Throwable unused) {
                                                }
                                                AppState.addInt(292, 1);
                                                f155h = true;
                                                f154i = true;
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 56:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 57:
                                                iM338l = AppState.getObjectArray(1271)[0] == null ? 0 : 59;
                                                break;
                                            case 58:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 59:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 60:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 61:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 62:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 63:
                                                iM1181a = ResourceManager.m964n();
                                                iM338l = iM1181a;
                                                break;
                                            case 64:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 65:
                                                if (m307b(9, 3000L) && (textBoxM1028h = XmppContactGroup.m1028h()) != null) {
                                                    String strM16a = StringUtils.m16a(textBoxM1028h);
                                                    if (AppState.getBool(106)) {
                                                        String strM1123k = Conversation.m1123k(strM16a);
                                                        if (!StringUtils.equals(strM1123k, strM16a)) {
                                                            textBoxM1028h.setString(strM1123k);
                                                        }
                                                    } else {
                                                        int length = strM16a.length();
                                                        int i7 = length;
                                                        int i8 = length;
                                                        while (true) {
                                                            i8--;
                                                            if (i8 < 0) {
                                                                int i9 = i7 - 160;
                                                                if (i9 > 0) {
                                                                    textBoxM1028h.setString(StringUtils.prefix(strM16a, strM16a.length() - i9));
                                                                }
                                                            } else {
                                                                char cCharAt = strM16a.charAt(i8);
                                                                if ((cCharAt >= 1040 && cCharAt <= 1071) || ((cCharAt >= 1072 && cCharAt <= 1103) || cCharAt == 1105 || cCharAt == 1025)) {
                                                                    i7 += 2;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 66:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 67:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 68:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 69:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 70:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 71:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 72:
                                                Object[] objArrM1156c4 = ConnectionThread.m1156c(IOUtils.m816k());
                                                if (objArrM1156c4 != null) {
                                                    int iM818c4 = IOUtils.m818c(objArrM1156c4);
                                                    if (iM818c4 != 0) {
                                                        iM1181a = iM818c4;
                                                    } else {
                                                        Object objM819l3 = IOUtils.m819l();
                                                        int size5 = ((Vector) objM819l3).size();
                                                        while (true) {
                                                            size5--;
                                                            if (size5 < 0) {
                                                                iM1181a = 43;
                                                            } else {
                                                                Object objM482e = JsonParser.getVectorElement(objM819l3, size5);
                                                                int iM510a = Utils.parseInt((Object) JsonParser.getStringByInt(objM482e, 263673));
                                                                String strM480c = JsonParser.getStringByInt(objM482e, 329240);
                                                                ChatRoom c0052wM747i2 = ((MrimAccount) AppState.getAccount()).m747i(strM480c);
                                                                Message c0026azM1415b3 = c0052wM747i2.m1415b(strM480c);
                                                                if (c0052wM747i2 != null) {
                                                                    c0052wM747i2.m1417d(strM480c);
                                                                }
                                                                if (iM510a == 1) {
                                                                    if (c0026azM1415b3 != null && !c0026azM1415b3.hasFlag(4)) {
                                                                        c0026azM1415b3.setFlag(4, true);
                                                                        c0052wM747i2.m1420c();
                                                                    }
                                                                } else if (c0026azM1415b3 != null && c0026azM1415b3.hasFlag(4)) {
                                                                    c0026azM1415b3.setFlag(4, false);
                                                                    c0052wM747i2.m1419b();
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 73:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 74:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 75:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 76:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 77:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 78:
                                                Object[] objArrM1156c5 = ConnectionThread.m1156c(IOUtils.m816k());
                                                if (objArrM1156c5 != null) {
                                                    int iM818c5 = IOUtils.m818c(objArrM1156c5);
                                                    if (iM818c5 != 0) {
                                                        iM1181a = iM818c5;
                                                    } else {
                                                        Object objM819l4 = IOUtils.m819l();
                                                        Object objM476a = JsonParser.getValueByInt(objM819l4, 329636);
                                                        if (JsonParser.getIntByInt(objM819l4, 198543) == 1) {
                                                            int size6 = ((Vector) objM476a).size();
                                                            while (true) {
                                                                size6--;
                                                                if (size6 >= 0) {
                                                                    String strM483f = JsonParser.getVectorString(objM476a, size6);
                                                                    MrimAccount c0028ba4 = (MrimAccount) AppState.getAccount();
                                                                    ChatRoom c0052wM747i3 = c0028ba4.m747i(strM483f);
                                                                    if (c0052wM747i3 != null && (c0026azM1415b = c0052wM747i3.m1415b(strM483f)) != null) {
                                                                        if (c0026azM1415b.hasFlag(4)) {
                                                                            c0052wM747i3.m1419b();
                                                                        }
                                                                        c0052wM747i3.m1421d();
                                                                    }
                                                                    c0028ba4.m748j(strM483f);
                                                                }
                                                            }
                                                        }
                                                        iM1181a = 43;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 79:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 80:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 81:
                                                Object[] objArrM1156c6 = ConnectionThread.m1156c(IOUtils.m816k());
                                                if (objArrM1156c6 != null) {
                                                    int iM818c6 = IOUtils.m818c(objArrM1156c6);
                                                    if (iM818c6 != 0) {
                                                        iM1181a = iM818c6;
                                                    } else {
                                                        ChatRoom c0052wM746W = ((MrimAccount) AppState.getAccount()).m746W();
                                                        Vector vector = (Vector) IOUtils.m819l();
                                                        c0052wM746W.m1422e();
                                                        int size7 = vector.size();
                                                        for (int i10 = 0; i10 < size7; i10++) {
                                                            Hashtable hashtable2 = (Hashtable) vector.elementAt(i10);
                                                            Vector vector2 = (Vector) JsonParser.getValue(hashtable2, AppState.getString(329636));
                                                            String strM480c2 = JsonParser.getStringByInt(hashtable2, 198561);
                                                            int size8 = vector2.size();
                                                            for (int i11 = 0; i11 < size8; i11++) {
                                                                Vector vector3 = c0052wM746W.f414f;
                                                                Object objElementAt = vector2.elementAt(i11);
                                                                vector3.addElement(objElementAt);
                                                                c0052wM746W.f417i.put(objElementAt, strM480c2);
                                                            }
                                                        }
                                                        c0052wM746W.f419k = false;
                                                        int iM541c = Utils.m541c(c0052wM746W.f414f);
                                                        if (iM541c > 0) {
                                                            c0052wM746W.f413e = (String) c0052wM746W.f414f.lastElement();
                                                            MrimAccount c0028ba5 = (MrimAccount) AppState.getAccount();
                                                            for (int i12 = 0; i12 < iM541c; i12++) {
                                                                String strM521a = Utils.m521a(c0052wM746W.f414f, i12);
                                                                Message c0026azM1415b4 = c0028ba5.m745h(Utils.parseInt(c0052wM746W.f417i.get(strM521a))).m1415b(strM521a);
                                                                if (c0026azM1415b4 != null) {
                                                                    c0052wM746W.f416h.put(strM521a, c0026azM1415b4);
                                                                } else {
                                                                    c0052wM746W.f418j.addElement(strM521a);
                                                                    c0052wM746W.f419k = true;
                                                                }
                                                            }
                                                            c0052wM746W.f410b = new StringBuffer().append(AppState.getString(901)).append(c0052wM746W.f414f.size()).toString();
                                                        }
                                                        if (c0052wM746W.f414f.size() == 0) {
                                                            iM1181a = m338l(736);
                                                        } else {
                                                            AppState.setInt(1513, c0052wM746W.f409a);
                                                            iM1181a = 41;
                                                        }
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 82:
                                                Object[] objArrM1156c7 = ConnectionThread.m1156c(IOUtils.m816k());
                                                if (objArrM1156c7 != null) {
                                                    int iM818c7 = IOUtils.m818c(objArrM1156c7);
                                                    iM1181a = iM818c7 != 0 ? iM818c7 : StringUtils.isEmpty((String) IOUtils.m819l()) ? m338l(878) : 83;
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 83:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 84:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 85:
                                                iM338l = AppState.pool[1315] == null ? 0 : 96;
                                                break;
                                            case 86:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 87:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 88:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 89:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 90:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 91:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 92:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 93:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 94:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 95:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 96:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 97:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 98:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 99:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 100:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 101:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 102:
                                                iM338l = AppState.getObjectArray(1271)[2] == null ? 0 : 106;
                                                break;
                                            case 103:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 104:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 105:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 106:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 107:
                                                iM338l = AppState.getObjectArray(1271)[2] == null ? 0 : 106;
                                                break;
                                            case 108:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 109:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 110:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 111:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 112:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 113:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 114:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 115:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 116:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 117:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 118:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 119:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 120:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 121:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 122:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 123:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 124:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 125:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 126:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 127:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 128:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 129:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 130:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 131:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 132:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 133:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 134:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 135:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 136:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 137:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 138:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 139:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 140:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 141:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 142:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 143:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 144:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 145:
                                                iM338l = AppState.pool[1315] == null ? 0 : 96;
                                                break;
                                            case 146:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 147:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 148:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 149:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 150:
                                                iM338l = AppState.pool[1318] == null ? 0 : 142;
                                                break;
                                            case 151:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 152:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 153:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 154:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 155:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 156:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 157:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 158:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 159:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 160:
                                                iM1181a = System.currentTimeMillis() - ResourceManager.f288g > 5000 ? ResourceManager.m947h() : 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 161:
                                                iM1181a = ConnectionThread.m1166i();
                                                iM338l = iM1181a;
                                                break;
                                            case 162:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 163:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 164:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 165:
                                                Object[] objArrM609l = AppState.getObjectArray(1271);
                                                Object obj4 = objArrM609l[0];
                                                if (obj4 != null) {
                                                    m339e(StringUtils.m8a(506, obj4));
                                                    iM1181a = 0;
                                                } else {
                                                    iM1181a = objArrM609l[3] == null ? 0 : NetworkUtils.m1181a(objArrM609l);
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 166:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 167:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 168:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 169:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 170:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 171:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 172:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 173:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 174:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 175:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 176:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 177:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 178:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 179:
                                                Vector vectorM614m5 = AppState.getVector(1284);
                                                if (Utils.m541c(vectorM614m5) <= 1) {
                                                    NetworkUtils.m1212a(vectorM614m5);
                                                    IOUtils.m778d((Object) AppState.getString(1029));
                                                    iM1181a = 4;
                                                } else {
                                                    Object objElementAt2 = vectorM614m5.elementAt(0);
                                                    if (objElementAt2 instanceof String) {
                                                        Object[] objArr2 = {(String) objElementAt2, StringUtils.m7b(5510023, Conversation.m1124l((String) vectorM614m5.lastElement())), null};
                                                        new AsyncTask(26, objArr2);
                                                        vectorM614m5.setElementAt(objArr2, 0);
                                                    } else {
                                                        Object obj5 = ((Object[]) objElementAt2)[2];
                                                        if (obj5 != null) {
                                                            if (obj5 instanceof Throwable) {
                                                                IOUtils.m778d((Object) StringUtils.m8a(1030, obj5));
                                                            } else {
                                                                Utils.dequeue(vectorM614m5);
                                                            }
                                                        }
                                                    }
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 180:
                                                iM1181a = AppState.getString(1239) == null ? 0 : 147;
                                                iM338l = iM1181a;
                                                break;
                                        }
                                        if (iM338l == 12) {
                                            ScreenBuilder.m549c();
                                        } else if (iM338l != 0) {
                                            ScreenBuilder.m546a(iM338l);
                                        }
                                    } else if (objM524a instanceof int[]) {
                                        int[] iArr = (int[]) objM524a;
                                        switch (iArr[0]) {
                                            case 0:
                                                Screen c0013amM66b3 = ScreenManager.m66b();
                                                if (c0013amM66b3 == null) {
                                                    break;
                                                } else {
                                                    if (c0013amM66b3.f94a != 6) {
                                                        f153g = true;
                                                    }
                                                    int i13 = iArr[1];
                                                    int i14 = iArr[2];
                                                    int i15 = ScreenManager.m66b().f94a;
                                                    int i16 = TabBar.f45b;
                                                    int size9 = AppState.getVector(1246).size();
                                                    boolean z6 = i16 == size9 - 1;
                                                    if (i15 == 4) {
                                                        ContactListManager.m155c();
                                                        if (size9 > 1) {
                                                            AppState.setInt(1414, 0);
                                                            if (i14 == 2) {
                                                                if (c0013amM66b3.m229h()) {
                                                                    TabBar c0008ahM168d = TabBar.m168d();
                                                                    if (c0008ahM168d != null) {
                                                                        ScreenBuilder.m546a(c0008ahM168d.m166b());
                                                                    }
                                                                    z4 = true;
                                                                } else {
                                                                    z4 = false;
                                                                }
                                                            } else if (i14 == 5) {
                                                                if (c0013amM66b3.m228g()) {
                                                                    TabBar c0008ahM167c = TabBar.m167c();
                                                                    if (c0008ahM167c != null) {
                                                                        ScreenBuilder.m546a(c0008ahM167c.m166b());
                                                                    }
                                                                    z4 = true;
                                                                } else {
                                                                    z4 = false;
                                                                }
                                                            } else if (i15 == 36) {
                                                                AppState.setInt(1414, 0);
                                                                if (i14 == 2) {
                                                                    ScreenBuilder.m546a(TabBar.m168d().m166b());
                                                                    z4 = true;
                                                                } else if (i14 == 5) {
                                                                    if (!z6) {
                                                                        ScreenBuilder.m546a(TabBar.m167c().m166b());
                                                                    }
                                                                    z4 = true;
                                                                } else if (i15 != 6) {
                                                                    z4 = false;
                                                                } else if (AppState.getBool(1414)) {
                                                                    if (i13 == 42) {
                                                                        Conversation.m1127a();
                                                                        z4 = true;
                                                                    } else if (i13 == 35) {
                                                                        Conversation.m1128b();
                                                                        z4 = true;
                                                                    } else if (i13 == 48) {
                                                                        Conversation.m1113a(false, (MrimAccount) null);
                                                                        ScreenBuilder.m546a(6);
                                                                        z4 = true;
                                                                    } else if (i13 == 49) {
                                                                        ScreenBuilder.m546a(100);
                                                                        z4 = true;
                                                                    } else if (i13 == 50) {
                                                                        boolean zM587e = AppState.getBool(41);
                                                                        if (zM587e) {
                                                                            Conversation.m1126a(false);
                                                                        } else {
                                                                            Conversation.m1126a(true);
                                                                        }
                                                                        AppState.setBool(41, !zM587e);
                                                                        ScreenBuilder.m546a(6);
                                                                        z4 = true;
                                                                    } else if (i13 == 51) {
                                                                        IOUtils.m778d(new IOUtils(7, null));
                                                                        z4 = true;
                                                                    } else if (i13 == 53) {
                                                                        AppState.setBool(230, !AppState.getBool(230));
                                                                        MapRenderer.f200h = true;
                                                                        z4 = true;
                                                                    } else if (i13 == 55) {
                                                                        if (MmpContact.f71y && (iArrM191s = MmpContact.m191s()) != null) {
                                                                            MapRenderer.m657b(iArrM191s[0], iArrM191s[1]);
                                                                        }
                                                                        z4 = true;
                                                                    } else if (i13 == 57) {
                                                                        if (MmpContact.f71y && (iArrM190r = MmpContact.m190r()) != null) {
                                                                            MapRenderer.m657b(iArrM190r[0], iArrM190r[1]);
                                                                        }
                                                                        z4 = true;
                                                                    }
                                                                } else if (i14 == 2) {
                                                                    ScreenBuilder.m546a(TabBar.m168d().m166b());
                                                                    z4 = true;
                                                                } else if (i14 == 5) {
                                                                    if (!z6) {
                                                                        ScreenBuilder.m546a(TabBar.m167c().m166b());
                                                                    }
                                                                    z4 = true;
                                                                } else if (i14 == 1) {
                                                                    z4 = true;
                                                                } else if (i14 == 6) {
                                                                    ConnectionThread.m1163c(c0013amM66b3);
                                                                    z4 = true;
                                                                }
                                                            }
                                                            if (!z4) {
                                                                int iM456O = i13 == 42 ? m456O(AppState.getInt(205)) : i13 == 35 ? m456O(AppState.getInt(206)) : (i13 < 48 || i13 > 57) ? 0 : m456O(AppState.getInt(i13 + 159));
                                                                int i17 = iM456O;
                                                                if (iM456O != 0) {
                                                                    ScreenBuilder.m546a(i17);
                                                                    break;
                                                                } else if (i14 == 8) {
                                                                    m455ab();
                                                                    break;
                                                                } else if (i14 == 1) {
                                                                    c0013amM66b3.m234m();
                                                                    break;
                                                                } else if (i14 == 6) {
                                                                    c0013amM66b3.m237p();
                                                                    break;
                                                                } else if (i14 == 2) {
                                                                    if (c0013amM66b3.f125x) {
                                                                        ScreenBuilder.m549c();
                                                                        break;
                                                                    } else if (c0013amM66b3.f94a == 6) {
                                                                        AppState.setInt(1564, 1);
                                                                        break;
                                                                    } else {
                                                                        if (c0013amM66b3.f102h == 1) {
                                                                            int i18 = c0013amM66b3.f106k;
                                                                            int size10 = c0013amM66b3.f108m.size();
                                                                            c0013amM66b3.f106k = ((i18 + size10) - 1) % size10;
                                                                            c0013amM66b3.m235n();
                                                                        }
                                                                        break;
                                                                    }
                                                                } else if (i14 == 5) {
                                                                    c0013amM66b3.m230i();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ScreenBuilder.m547a();
                                                break;
                                            case 2:
                                                ScreenBuilder.m548b();
                                                break;
                                            case 3:
                                                f153g = true;
                                                m455ab();
                                                break;
                                            case 4:
                                                if (ScreenManager.m66b().f94a == 6) {
                                                    f153g = true;
                                                    AppState.setInt(1564, -1);
                                                }
                                                break;
                                            case 5:
                                                int i19 = iArr[1];
                                                int i20 = iArr[2];
                                                if (!AppState.getBool(71) || i20 <= AppState.getHeight()) {
                                                    z2 = false;
                                                } else {
                                                    if (i19 < (AppState.getInt(1528) >> 1)) {
                                                        ScreenBuilder.m547a();
                                                    } else {
                                                        ScreenBuilder.m548b();
                                                    }
                                                    z2 = true;
                                                }
                                                if (z2 || (i = ScreenManager.m66b().f94a) == 137) {
                                                    break;
                                                } else if (i20 > 17 || !ScreenManager.m77h()) {
                                                    i2 = 0;
                                                    int i21 = i2;
                                                    if (i2 <= 0) {
                                                        if (i != i21) {
                                                            if (i == 4) {
                                                                ContactListManager.m155c();
                                                            }
                                                            ScreenBuilder.m546a(i21);
                                                        }
                                                        break;
                                                    } else {
                                                        Screen c0013amM66b4 = ScreenManager.m66b();
                                                        if (c0013amM66b4 != null) {
                                                            c0013amM66b4.f132B = true;
                                                            c0013amM66b4.f130z = 0;
                                                            c0013amM66b4.f131A = 0;
                                                            int i22 = i19 - c0013amM66b4.f98e;
                                                            int i23 = i20 - c0013amM66b4.f99f;
                                                            boolean z7 = i22 >= 2 && i22 < 2 + c0013amM66b4.f114q && i23 >= c0013amM66b4.f113p && i23 < c0013amM66b4.f113p + c0013amM66b4.f115r;
                                                            boolean z8 = z7;
                                                            if (z7 && c0013amM66b4.f94a == 6) {
                                                                int i24 = i23 - c0013amM66b4.f113p;
                                                                if (i24 > 0) {
                                                                    ConnectionThread.m1161a(c0013amM66b4);
                                                                    MapRenderer.f211p = false;
                                                                    MapRenderer.f213r = System.currentTimeMillis();
                                                                    MapRenderer.f214s = i22;
                                                                    MapRenderer.f215t = i24;
                                                                    MapRenderer.f200h = true;
                                                                    z3 = true;
                                                                } else {
                                                                    z3 = false;
                                                                }
                                                            } else if (z8 || c0013amM66b4.f97d == 1 || c0013amM66b4.f97d == 12) {
                                                                z3 = false;
                                                            } else {
                                                                ScreenBuilder.m549c();
                                                                f153g = true;
                                                                z3 = true;
                                                            }
                                                            if (!z3) {
                                                                int i25 = c0013amM66b4.f97d;
                                                                if ((i25 == 1 || i25 == 12) && (objM179a = TabBar.m179a(i19, i20)) != null) {
                                                                    if (!(objM179a instanceof int[])) {
                                                                        int i26 = ((TabBar) objM179a).f50g;
                                                                        Account abstractC0037h = ((TabBar) objM179a).f51h;
                                                                        AppState.setInt(1414, 0);
                                                                        if (i == 4) {
                                                                            ContactListManager.m155c();
                                                                        }
                                                                        if (i26 != 6 && i26 != 36 && abstractC0037h != null) {
                                                                            TabBar.m176a(4, ((TabBar) objM179a).f51h);
                                                                            ScreenBuilder.m546a(4);
                                                                        } else if (i != i26) {
                                                                            ScreenBuilder.m546a(i26);
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        switch (((int[]) objM179a)[1]) {
                                                                            case 246:
                                                                                ScreenBuilder.m546a(TabBar.m167c().m166b());
                                                                                break;
                                                                            case 248:
                                                                                ScreenBuilder.m546a(TabBar.m168d().m166b());
                                                                                break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    int iM586d6 = AppState.getInt(1528) - 17;
                                                    if (m413P() == 0) {
                                                        if (!m410N() && i19 > iM586d6) {
                                                            i2 = 36;
                                                        }
                                                        int i212 = i2;
                                                        if (i2 <= 0) {
                                                        }
                                                    } else if (i19 > iM586d6) {
                                                        i2 = !AppState.getBool(243) ? 4 : 0;
                                                        int i2122 = i2;
                                                        if (i2 <= 0) {
                                                        }
                                                    } else {
                                                        iM586d6 -= 17;
                                                        if (!m410N()) {
                                                            i2 = 0;
                                                            int i21222 = i2;
                                                            if (i2 <= 0) {
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case 6:
                                                int i27 = iArr[1];
                                                int i28 = iArr[2];
                                                Screen c0013amM66b5 = ScreenManager.m66b();
                                                if (c0013amM66b5 == null || !c0013amM66b5.f132B) {
                                                    break;
                                                } else {
                                                    int i29 = i27 - c0013amM66b5.f98e;
                                                    int i30 = i28 - (c0013amM66b5.f99f + c0013amM66b5.f113p);
                                                    if (c0013amM66b5.f130z == 0 && c0013amM66b5.f131A == 0) {
                                                        c0013amM66b5.f130z = i29;
                                                        c0013amM66b5.f131A = i30;
                                                    }
                                                    int i31 = i29 - c0013amM66b5.f130z;
                                                    int i32 = i30 - c0013amM66b5.f131A;
                                                    c0013amM66b5.f130z = i29;
                                                    c0013amM66b5.f131A = i30;
                                                    if (c0013amM66b5.f94a == 6) {
                                                        ConnectionThread.m1161a(c0013amM66b5);
                                                        MapRenderer.f211p = true;
                                                        MapRenderer.f213r = 0L;
                                                        int iM586d7 = AppState.getInt(39);
                                                        MapRenderer.m649a(MapRenderer.f196d - ((int) m318a(i31, iM586d7)), MapRenderer.f195c + ((int) m318a(i32, iM586d7)));
                                                        MapRenderer.f200h = true;
                                                        break;
                                                    } else {
                                                        c0013amM66b5.f105j -= i32;
                                                        if (c0013amM66b5.f107l < c0013amM66b5.f115r) {
                                                            c0013amM66b5.f105j = 0;
                                                        }
                                                        if (c0013amM66b5.f105j > c0013amM66b5.f107l - c0013amM66b5.f115r) {
                                                            c0013amM66b5.f105j = c0013amM66b5.f107l - c0013amM66b5.f115r;
                                                        }
                                                        if (c0013amM66b5.f105j < 0) {
                                                            c0013amM66b5.f105j = 0;
                                                        }
                                                        f153g = true;
                                                    }
                                                }
                                                break;
                                            case 7:
                                                int i33 = iArr[1];
                                                int i34 = iArr[2];
                                                int i35 = iArr[3];
                                                int i36 = iArr[4];
                                                int i37 = iArr[5];
                                                Screen c0013amM66b6 = ScreenManager.m66b();
                                                if (c0013amM66b6 != null) {
                                                    c0013amM66b6.m260a(i33, i34, i35, i36, i37 != 0);
                                                }
                                                break;
                                            case 8:
                                                int i38 = iArr[1];
                                                int i39 = iArr[2];
                                                Screen c0013amM66b7 = ScreenManager.m66b();
                                                if (c0013amM66b7 != null) {
                                                    int i40 = i38 - c0013amM66b7.f98e;
                                                    int i41 = i39 - c0013amM66b7.f99f;
                                                    c0013amM66b7.f132B = false;
                                                    if (c0013amM66b7.f94a == 6) {
                                                        int i42 = i41 - c0013amM66b7.f113p;
                                                        ConnectionThread.m1161a(c0013amM66b7);
                                                        MapRenderer.m665b(i40, i42);
                                                    }
                                                }
                                                break;
                                        }
                                    } else if (objM524a instanceof String) {
                                        m339e((String) objM524a);
                                        f153g = true;
                                    } else if (objM524a instanceof Object[]) {
                                        if (((Object[]) objM524a)[0] instanceof MrimAccount) {
                                            AppState.setInt(4486, 108);
                                            AppState.setObject(1344, ((Object[]) objM524a)[1]);
                                            MrimAccount c0028ba6 = (MrimAccount) ((Object[]) objM524a)[0];
                                            c0028ba6.f229e = true;
                                            AppState.pool[1282] = c0028ba6;
                                            ScreenManager.m71b(ScreenManager.m75b(4485));
                                            AppState.clearIndex(1344);
                                            f153g = true;
                                        } else {
                                            ((MrimAccount) ((Object[]) objM524a)[1]).m740h((String) ((Object[]) objM524a)[0]);
                                        }
                                    } else if (objM524a instanceof IOUtils) {
                                        IOUtils c0029bb = (IOUtils) objM524a;
                                        int i43 = c0029bb.f235a;
                                        Object obj6 = c0029bb.f236b;
                                        switch (i43) {
                                            case 3:
                                                ResourceManager.m951j();
                                                break;
                                            case 4:
                                                Object[] objArr3 = (Object[]) obj6;
                                                PhoneContact c0020at = (PhoneContact) objArr3[0];
                                                AppState.pool[1256] = c0020at;
                                                Vector vector4 = (Vector) objArr3[1];
                                                AppState.pool[1257] = vector4;
                                                int iIntValue = ((Integer) objArr3[2]).intValue();
                                                AppState.setInt(1444, iIntValue);
                                                Screen c0013amM75b = ScreenManager.m75b(2237);
                                                if (iIntValue >= 10) {
                                                    c0013amM75b.m247a(6, AppState.getString(421), 1, null);
                                                }
                                                int size11 = vector4.size();
                                                while (true) {
                                                    size11--;
                                                    if (size11 < 0) {
                                                        if (iIntValue < c0020at.f167e - 10) {
                                                            c0013amM75b.m247a(6, AppState.getString(420), 2, null);
                                                        }
                                                        AppState.setBool(1445, iIntValue < c0020at.f167e - 10);
                                                        AppState.setBool(1446, iIntValue >= 10);
                                                        ScreenManager.m71b(c0013amM75b);
                                                        break;
                                                    } else {
                                                        UserSearchResult c0045p = (UserSearchResult) vector4.elementAt(size11);
                                                        c0013amM75b.m247a(c0045p.f393d == 1 ? 377 : c0045p.f393d == 2 ? 378 : 379, c0045p.getText(), 0, c0045p);
                                                    }
                                                }
                                            case 5:
                                                AppState.setInt(1508, 1);
                                                IOUtils.m758b();
                                                break;
                                            case 6:
                                                ((MrimAccount) obj6).m724j();
                                                break;
                                        }
                                        f153g = true;
                                    } else {
                                        f153g = true;
                                        f152f = true;
                                        Screen c0013amM66b8 = ScreenManager.m66b();
                                        int i44 = ScreenManager.m66b().f94a;
                                        if (objM524a != null && (objM524a instanceof MenuItem)) {
                                            MenuItem c0032c = (MenuItem) objM524a;
                                            if (c0032c.f258a == 2) {
                                                if (i44 == 147 && AppState.setBool(1468, ((Boolean) c0032c.f265d).booleanValue())) {
                                                    NetworkUtils.m1195d();
                                                    AppState.setFromPool(1289, 1286);
                                                    m324n();
                                                }
                                            }
                                        } else if (i44 == 21) {
                                            if (AppState.getAccount().getType() == 0) {
                                                StringUtils.m31a(c0013amM66b8, objM524a);
                                            }
                                        } else if (i44 == 164) {
                                            MenuItem c0032c2 = (MenuItem) objM524a;
                                            Object[] objArr4 = (Object[]) c0032c2.f265d;
                                            int iIntValue2 = ((Integer) objArr4[0]).intValue();
                                            String[] strArr = (String[]) objArr4[1];
                                            MenuItem c0032c3 = null;
                                            Vector vector5 = c0013amM66b8.f108m;
                                            int size12 = vector5.size();
                                            while (true) {
                                                size12--;
                                                if (size12 < 0) {
                                                    if (c0032c2.f259b.equals(AppState.getString(809))) {
                                                        MenuItem c0032c4 = c0032c3;
                                                        String strM522f = iIntValue2 == 0 ? Utils.defaultStr(AppState.getString(1287)) : strArr[iIntValue2];
                                                        Object[] objArr5 = (Object[]) c0032c4.f265d;
                                                        c0032c4.m884a().m891a(objArr5[4], strM522f, objArr5[1], objArr5[2], objArr5[3]);
                                                    }
                                                    c0013amM66b8.m258q();
                                                } else {
                                                    MenuItem c0032c5 = (MenuItem) vector5.elementAt(size12);
                                                    if (c0032c5.f258a == 15 && c0032c5.f259b.startsWith(AppState.getString(811))) {
                                                        c0032c3 = c0032c5;
                                                    }
                                                }
                                            }
                                        } else if (i44 == 26) {
                                            MenuItem c0032c6 = (MenuItem) objM524a;
                                            Object[] objArr6 = (Object[]) c0032c6.f265d;
                                            if (AppState.getString(560).equals(c0032c6.f259b)) {
                                                AppState.setInt(72, ((Integer) objArr6[0]).intValue());
                                            }
                                        } else if (i44 == 28) {
                                            ResourceManager.m926a(((Integer) ((Object[]) ((MenuItem) objM524a).f265d)[0]).intValue(), false);
                                        }
                                    }
                                    if (!AppState.getBool(71) && null != (c0013amM66b = ScreenManager.m66b())) {
                                        AppState.getCanvas().m205a(c0013amM66b.f123v, c0013amM66b.f124w);
                                    }
                                    IOUtils.m756a();
                                    if (m306a(f147a[0]) && (!AppState.getBool(272) || ScreenManager.m66b().f94a != 6)) {
                                        if (AppState.getCanvas().isShown()) {
                                            m358L(0);
                                        } else {
                                            m304a(0, m376E());
                                        }
                                    }
                                } else {
                                    Contact abstractC0041l = (Contact) vectorM614m3.elementAt(size2);
                                    if (Utils.abs(iM586d - abstractC0041l.statusCode) > 10000) {
                                        m418c(abstractC0041l);
                                    }
                                }
                            }
                        } else {
                            Account abstractC0037h2 = (Account) vectorM614m2.elementAt(size);
                            try {
                                if (abstractC0037h2.progress <= 0 || abstractC0037h2.progress == 100) {
                                    Vector vectorM614m6 = AppState.getVector(1247);
                                    if (vectorM614m6.contains(abstractC0037h2)) {
                                        Utils.removeFrom(vectorM614m6, abstractC0037h2);
                                        m458ak();
                                    }
                                } else {
                                    Vector vectorM614m7 = AppState.getVector(1247);
                                    if (!vectorM614m7.contains(abstractC0037h2)) {
                                        vectorM614m7.addElement(abstractC0037h2);
                                        m458ak();
                                    }
                                }
                                abstractC0037h2.loadData();
                            } catch (Throwable unused2) {
                                abstractC0037h2.handleConnError();
                            }
                        }
                    }
                }
            }
            String strM584b = AppState.getString(1236);
            if (strM584b != null) {
                try {
                    f154i = true;
                    AppState.getMidlet().platformRequest(strM584b);
                    throw new Throwable();
                } catch (Throwable unused3) {
                    AppState.clearIndex(1236);
                }
            }
            if (f154i) {
                AppState.getMidlet().destroyApp(true);
                f151e = true;
                throw new RuntimeException();
            }
            if ((m413P() != 0 || m410N()) && m305K(5)) {
                f153g = true;
            }
            MainCanvas c0011akM582c = AppState.getCanvas();
            if (!f151e && f153g) {
                Object obj7 = AppState.currentScreen;
                if (null != obj7) {
                    if (obj7 == AppState.getCanvas()) {
                        AppState.getCanvas().m201a();
                    }
                    Display.getDisplay(AppState.getMidlet()).setCurrent(obj7 instanceof Displayable ? (Displayable) obj7 : null);
                    m304a(0, m376E());
                    AppState.currentScreen = null;
                    z = true;
                } else {
                    z = false;
                }
                if (!z) {
                    if (c0011akM582c.isShown()) {
                        c0011akM582c.repaint();
                        m304a(5, 1000L);
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch (Throwable unused4) {
                        }
                    }
                }
            }
            try {
                Thread.sleep(m305K(3) ? 500 : 25);
            } catch (Throwable unused5) {
            }
        }
    }

    /* renamed from: ab */
    public static final void m455ab() {
        int i;
        int iM338l;
        int i2;
        MenuItem c0032cM69e = ScreenManager.m69e();
        if (c0032cM69e == null || c0032cM69e.m894a(ScreenManager.m66b()) == -1) {
            Screen c0013amM66b = ScreenManager.m66b();
            String strM67c = ScreenManager.m67c();
            int iM68d = ScreenManager.m68d();
            MenuItem c0032cM69e2 = ScreenManager.m69e();
            MenuItem c0032cM223e = AppState.getVector(1272).size() > 0 ? ScreenManager.m66b().m223e() : null;
            Object obj = c0032cM69e2 == null ? null : c0032cM69e2.f265d;
            Object obj2 = c0032cM223e == null ? null : c0032cM223e.f265d;
            int iM460J = 0;
            switch (ScreenManager.m66b().f94a) {
                case 1:
                    iM460J = m408B(iM68d);
                    break;
                case 2:
                    iM460J = 0;
                    break;
                case 3:
                    iM460J = IOUtils.m824d(iM68d);
                    break;
                case 4:
                    iM460J = ContactListManager.m158a(strM67c, obj);
                    break;
                case 5:
                    iM460J = m389z(iM68d);
                    break;
                case 6:
                    if (!AppState.getBool(1414)) {
                        ConnectionThread.m1161a(c0013amM66b);
                        i2 = -1;
                    } else if (AppState.getBool(1479)) {
                        String strM809a = IOUtils.m809a(MapRenderer.f196d);
                        String strM810b = IOUtils.m810b(MapRenderer.f195c);
                        AppState.setInt(1479, 0);
                        ResourceManager.m953a(VCard.m62a(AppState.getInt(39), strM809a, strM810b), MapRenderer.f196d, MapRenderer.f195c);
                        i2 = 0;
                    } else {
                        i2 = 113;
                    }
                    iM460J = i2;
                    break;
                case 7:
                    iM460J = 0;
                    break;
                case 8:
                    iM460J = m335j(iM68d);
                    break;
                case 9:
                    iM460J = 0;
                    break;
                case 10:
                    iM460J = 55;
                    break;
                case 11:
                    iM460J = m295d();
                    break;
                case 13:
                    iM460J = -1;
                    break;
                case 14:
                    iM460J = 0;
                    break;
                case 15:
                    iM460J = m287a(strM67c, obj);
                    break;
                case 16:
                    iM460J = 0;
                    break;
                case 17:
                    iM460J = m313c(iM68d);
                    break;
                case 18:
                    iM460J = 0;
                    break;
                case 19:
                    iM460J = 0;
                    break;
                case 20:
                    iM460J = m330i(iM68d);
                    break;
                case 21:
                    iM460J = 0;
                    break;
                case 22:
                    iM460J = 0;
                    break;
                case 23:
                    iM460J = 0;
                    break;
                case 24:
                    iM460J = 0;
                    break;
                case 25:
                    iM460J = m373a(iM68d, obj);
                    break;
                case 26:
                    iM460J = 0;
                    break;
                case 27:
                    iM460J = 0;
                    break;
                case 28:
                    iM460J = 0;
                    break;
                case 29:
                    iM460J = 0;
                    break;
                case 30:
                    iM460J = IOUtils.m823c(strM67c, iM68d);
                    break;
                case 32:
                    iM460J = ResourceManager.m933a(strM67c, c0032cM69e2);
                    break;
                case 33:
                    iM460J = 0;
                    break;
                case 34:
                    iM460J = 0;
                    break;
                case 35:
                    iM460J = m346n(iM68d);
                    break;
                case 36:
                    iM460J = ResourceManager.m955c(obj);
                    break;
                case 37:
                    iM460J = -1;
                    break;
                case 38:
                    AppState.setInt(1513, ((ChatRoom) obj).f409a);
                    iM460J = 0;
                    break;
                case 39:
                    iM460J = m286a(obj);
                    break;
                case 40:
                    if (obj2 != null) {
                        Object[] objArr = (Object[]) obj2;
                        if (((Integer) objArr[0]).intValue() == 0) {
                            MapPoint c0014an = new MapPoint((String) objArr[1]);
                            c0014an.f143k = 2;
                            ConnectionThread.m1165a(c0014an, false);
                            AppState.setInt(1414, 1);
                            iM338l = 6;
                        } else {
                            String str = (String) objArr[1];
                            String str2 = (String) objArr[2];
                            long jLongValue = ((Long) objArr[3]).longValue();
                            m299g();
                            AppState.setInt(1507, 0);
                            AppState.setObject(1287, (Object) str);
                            AppState.setFromBuffer(1284, NetworkUtils.m1217h().append(str2).append(':'));
                            AppState.setLong(1469, jLongValue);
                            iM338l = 115;
                        }
                    } else {
                        AppState.clearIndex(1279);
                        Contact abstractC0041lM611g = AppState.getCurrentContact();
                        iM338l = !abstractC0041lM611g.account.isConnected() ? m338l(299) : abstractC0041lM611g.isOffline() ? ResourceManager.m946g() : 63;
                    }
                    iM460J = iM338l;
                    break;
                case 41:
                    iM460J = -1;
                    break;
                case 42:
                    iM460J = -1;
                    break;
                case 43:
                    AppState.setInt(1514, c0013amM66b.f105j);
                    AppState.setObject(1345, (Object) strM67c);
                    Message c0026az = (Message) obj;
                    if (c0026az == null) {
                        i = -1;
                    } else {
                        AppState.setObject(1346, (Object) c0026az.from);
                        ChatRoom c0052wM745h = ((MrimAccount) AppState.getAccount()).m745h(AppState.getInt(1513));
                        if (StringUtils.m3a(894, c0052wM745h.f410b) || StringUtils.m3a(899, c0052wM745h.f410b)) {
                            XmppMailRuProtocol.m872b(54, 3);
                        } else {
                            XmppMailRuProtocol.m872b(52, 0);
                        }
                        i = 0;
                    }
                    iM460J = i;
                    break;
                case 44:
                    iM460J = -1;
                    break;
                case 45:
                    iM460J = -1;
                    break;
                case 46:
                    iM460J = 0;
                    break;
                case 47:
                    iM460J = 0;
                    break;
                case 48:
                    iM460J = -1;
                    break;
                case 49:
                    iM460J = 0;
                    break;
                case 50:
                    iM460J = 0;
                    break;
                case 51:
                    ResourceManager.m974c(strM67c);
                case 52:
                    iM460J = 0;
                    break;
                case 53:
                    iM460J = IOUtils.m787a(strM67c);
                    break;
                case 54:
                    iM460J = 0;
                    break;
                case 55:
                    iM460J = -1;
                    break;
                case 56:
                    iM460J = 0;
                    break;
                case 57:
                    iM460J = -1;
                    break;
                case 58:
                    iM460J = m322f(iM68d);
                    break;
                case 59:
                    iM460J = ResourceManager.m978s();
                    break;
                case 60:
                    iM460J = m372g(strM67c);
                    break;
                case 61:
                    iM460J = 42;
                    break;
                case 62:
                    iM460J = IOUtils.m752a(strM67c, iM68d);
                    break;
                case 63:
                    iM460J = 0;
                    break;
                case 64:
                    iM460J = m401A(iM68d);
                    break;
                case 65:
                    iM460J = 0;
                    break;
                case 66:
                    iM460J = 0;
                    break;
                case 67:
                    iM460J = m289a(strM67c);
                    break;
                case 68:
                    iM460J = 0;
                    break;
                case 69:
                    iM460J = 0;
                    break;
                case 70:
                    iM460J = 0;
                    break;
                case 71:
                    iM460J = ResourceManager.m976q();
                    break;
                case 72:
                    iM460J = -1;
                    break;
                case 73:
                    AppState.pool[1319] = obj;
                    iM460J = 0;
                    break;
                case 74:
                    iM460J = -1;
                    break;
                case 75:
                    iM460J = -1;
                    break;
                case 76:
                    iM460J = 0;
                    break;
                case 77:
                    iM460J = m400L();
                    break;
                case 78:
                    iM460J = -1;
                    break;
                case 79:
                    ScreenBuilder.m549c();
                    ScreenBuilder.m549c();
                    iM460J = 0;
                    break;
                case 80:
                    iM460J = m352o(iM68d);
                    break;
                case 81:
                    iM460J = -1;
                    break;
                case 82:
                    iM460J = -1;
                    break;
                case 83:
                    iM460J = m308i();
                    break;
                case 84:
                    iM460J = ResourceManager.m960a(strM67c, iM68d);
                    break;
                case 85:
                    iM460J = -1;
                    break;
                case 86:
                    iM460J = m397h(obj);
                    break;
                case 87:
                    iM460J = ResourceManager.m938a(strM67c);
                    break;
                case 88:
                    iM460J = m371s(iM68d);
                    break;
                case 89:
                    iM460J = -1;
                    break;
                case 90:
                    iM460J = m351e(obj);
                    break;
                case 91:
                    iM460J = m364q(iM68d);
                    break;
                case 92:
                    iM460J = IOUtils.m795b(strM67c, iM68d);
                    break;
                case 93:
                    iM460J = m374t(iM68d);
                    break;
                case 94:
                    iM460J = m396h(strM67c);
                    break;
                case 95:
                    iM460J = m347f(strM67c);
                    break;
                case 96:
                    iM460J = 0;
                    break;
                case 97:
                    iM460J = m398i(obj);
                    break;
                case 98:
                    iM460J = m396h(strM67c);
                    break;
                case 99:
                    iM460J = m333d(strM67c);
                    break;
                case 100:
                    iM460J = IOUtils.m771c(obj);
                    break;
                case 101:
                    iM460J = m382g(obj);
                    break;
                case 102:
                    iM460J = -1;
                    break;
                case 103:
                    iM460J = 0;
                    break;
                case 104:
                    iM460J = m363p(iM68d);
                    break;
                case 105:
                    iM460J = 0;
                    break;
                case 106:
                    iM460J = 0;
                    break;
                case 107:
                    iM460J = -1;
                    break;
                case 108:
                    iM460J = m327o();
                    break;
                case 109:
                    iM460J = ((MmpProtocol) AppState.getAccount()).m923d(iM68d);
                    break;
                case 110:
                    iM460J = 0;
                    break;
                case 111:
                    iM460J = m404j(obj);
                    break;
                case 112:
                    iM460J = 0;
                    break;
                case 113:
                    iM460J = XmppMailRuProtocol.m859h(iM68d);
                    break;
                case 114:
                    iM460J = 0;
                    break;
                case 115:
                    iM460J = 0;
                    break;
                case 116:
                    iM460J = m405k(obj);
                    break;
                case 117:
                    iM460J = m320b(strM67c);
                    break;
                case 118:
                    iM460J = m409m(obj);
                    break;
                case 119:
                    iM460J = m384v(iM68d);
                    break;
                case 120:
                    iM460J = m463n(obj);
                    break;
                case 121:
                    iM460J = m387x(iM68d);
                    break;
                case 122:
                    iM460J = m329p();
                    break;
                case 123:
                    iM460J = m407l(obj);
                    break;
                case 124:
                    iM460J = 0;
                    break;
                case 125:
                    iM460J = -1;
                    break;
                case 126:
                    iM460J = -1;
                    break;
                case 127:
                    iM460J = -1;
                    break;
                case 128:
                    AppState.getCurrentContact().initMessageBuffer();
                    iM460J = 4;
                    break;
                case 129:
                    iM460J = 0;
                    break;
                case 130:
                    iM460J = m288a(iM68d);
                    break;
                case 131:
                    iM460J = m406i(strM67c);
                    break;
                case 132:
                    iM460J = m301b(iM68d);
                    break;
                case 133:
                    iM460J = 0;
                    break;
                case 134:
                    iM460J = 0;
                    break;
                case 135:
                    iM460J = 0;
                    break;
                case 136:
                    iM460J = 0;
                    break;
                case 137:
                    iM460J = -1;
                    break;
                case 138:
                    iM460J = -1;
                    break;
                case 139:
                    iM460J = 129;
                    break;
                case 140:
                    iM460J = 0;
                    break;
                case 141:
                    iM460J = -1;
                    break;
                case 142:
                    AppState.pool[1336] = obj;
                    iM460J = obj != null ? 0 : -1;
                    break;
                case 143:
                    iM460J = 0;
                    break;
                case 144:
                    iM460J = 0;
                    break;
                case 145:
                    iM460J = -1;
                    break;
                case 146:
                    iM460J = m323g(iM68d);
                    break;
                case 147:
                    iM460J = 0;
                    break;
                case 148:
                    iM460J = 0;
                    break;
                case 149:
                    iM460J = 0;
                    break;
                case 150:
                    iM460J = -1;
                    break;
                case 151:
                    iM460J = m336k(iM68d);
                    break;
                case 152:
                    iM460J = m325h(iM68d);
                    break;
                case 153:
                    iM460J = ResourceManager.m959d(obj);
                    break;
                case 154:
                    iM460J = 0;
                    break;
                case 155:
                    iM460J = 0;
                    break;
                case 156:
                    iM460J = IOUtils.m769e();
                    break;
                case 157:
                    iM460J = 0;
                    break;
                case 158:
                    iM460J = m370r(iM68d);
                    break;
                case 159:
                    iM460J = m296b(obj);
                    break;
                case 160:
                    iM460J = ResourceManager.m947h();
                    break;
                case 161:
                    iM460J = -1;
                    break;
                case 162:
                    iM460J = m388y(iM68d);
                    break;
                case 163:
                    iM460J = m348v();
                    break;
                case 164:
                    iM460J = 0;
                    break;
                case 165:
                    iM460J = -1;
                    break;
                case 166:
                    iM460J = m381u(iM68d);
                    break;
                case 167:
                    iM460J = m385w(iM68d);
                    break;
                case 168:
                    iM460J = 0;
                    break;
                case 169:
                    iM460J = ResourceManager.m952b(obj);
                    break;
                case 170:
                    iM460J = m379f(obj);
                    break;
                case 171:
                    iM460J = m297e();
                    break;
                case 172:
                    iM460J = m337d(obj);
                    break;
                case 173:
                    iM460J = m399K();
                    break;
                case 174:
                    iM460J = 0;
                    break;
                case 175:
                    iM460J = 0;
                    break;
                case 176:
                    iM460J = iM68d == 1 ? m311k() : iM68d == 2 ? m310j() : m309c(obj);
                    break;
                case 177:
                    iM460J = ResourceManager.m956d(iM68d);
                    break;
                case 178:
                    iM460J = m460J(iM68d);
                    break;
                case 179:
                    iM460J = -1;
                    break;
                case 180:
                    iM460J = -1;
                    break;
            }
            if (iM460J != -1) {
                if (iM460J == 12) {
                    ScreenBuilder.m549c();
                    return;
                }
                if (iM460J != 0) {
                    ScreenBuilder.m546a(iM460J);
                    return;
                }
                int i3 = c0013amM66b.f122u;
                if (i3 != 200) {
                    int i4 = i3 == 199 ? iM68d : i3;
                    int i5 = i4;
                    if (i4 == 12) {
                        ScreenBuilder.m549c();
                    } else if (i5 != 0) {
                        ScreenBuilder.m546a(i5);
                    }
                }
            }
        }
    }

    /* renamed from: O */
    private static final int m456O(int i) {
        if (ScreenManager.m66b().f94a == 137) {
            return 0;
        }
        Screen c0013amM66b = ScreenManager.m66b();
        int i2 = ScreenManager.m66b().f94a;
        switch (i) {
            case 4:
                break;
            case 8:
                if (c0013amM66b.f103i) {
                    c0013amM66b.f106k = c0013amM66b.f108m.size() - 1;
                    c0013amM66b.f105j = c0013amM66b.f107l - c0013amM66b.f115r;
                    if (c0013amM66b.f105j < 0) {
                        c0013amM66b.f105j = 0;
                    }
                } else if (c0013amM66b.f107l < c0013amM66b.f115r) {
                    c0013amM66b.f105j = 0;
                } else if (((MenuItem) c0013amM66b.f108m.lastElement()).m912h() < c0013amM66b.f115r) {
                    c0013amM66b.f105j = c0013amM66b.f107l - c0013amM66b.f115r;
                } else {
                    int[] iArr = c0013amM66b.f109n;
                    c0013amM66b.f105j = iArr[iArr[0]];
                }
                c0013amM66b.m235n();
                break;
            case 11:
                AppState.toggleBool(98);
                f152f = true;
                break;
            case 12:
                if (i2 != 73) {
                    if (i2 != 4) {
                        if (i2 == 30 || i2 == 92 || i2 == 40) {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    AppState.pool[1319] = c0013amM66b.m222d().f265d;
                    break;
                }
                break;
        }
        return 0;
    }

    /* renamed from: ac */
    public static final int m457ac() {
        Object obj = AppState.pool[1365];
        if (obj == null || !(obj instanceof Contact)) {
            return 0;
        }
        Contact abstractC0041l = (Contact) obj;
        if (!abstractC0041l.account.isConnected()) {
            return m338l(299);
        }
        AppState.clearIndex(1281);
        return (abstractC0041l.isSystem() || abstractC0041l.isOffline()) ? 0 : 85;
    }

    /* renamed from: ak */
    private static final void m458ak() {
        AppState.setInt(1408, AppState.getVector(1247).size() * Utils.max(16, AppState.getInt(1450)));
        f153g = true;
    }

    /* renamed from: a */
    public static final String[] m459a(String str, String str2) {
        return new String[]{str, str2};
    }

    /* renamed from: J */
    public static final int m460J(int i) {
        m331a(i == 0, i != 0, true);
        AppState.setInt(281, 1);
        return 0;
    }

    /* renamed from: a */
    public static final void m461a(MrimAccount c0028ba, ByteBuffer c0043n) {
        c0043n.readInt();
        switch (c0043n.readInt() & 255) {
            case 65:
                m462a(c0028ba, 490);
                break;
            case 66:
                m462a(c0028ba, 491);
                break;
            case 67:
            case 69:
            case 70:
            case 71:
            case 72:
            default:
                c0028ba.handleError(0);
                NetworkUtils.m1174a();
                break;
            case 68:
                m462a(c0028ba, 492);
                break;
            case 73:
                c0028ba.handleComplete();
                break;
        }
    }

    /* renamed from: a */
    private static final void m462a(MrimAccount c0028ba, int i) {
        IOUtils.m783a(c0028ba, i);
        c0028ba.closeConnection();
        c0028ba.lastError = c0028ba.getDefaultError();
    }

    /* renamed from: n */
    public static final int m463n(Object obj) {
        if (AppState.getBool(1443)) {
            MapRenderer.m653a((MapPoint) obj);
            return 6;
        }
        if (!AppState.getBool(1478)) {
            f156j = (MapPoint) obj;
            return 121;
        }
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        MapPoint c0014an = (MapPoint) obj;
        c0028ba.m730b(IOUtils.m809a(c0014an.f138f), IOUtils.m810b(c0014an.f139g));
        c0028ba.m724j();
        AppState.setInt(1478, 0);
        return 160;
    }

    /* renamed from: a */
    public static final ByteBuffer m464a(MmpProtocol c0033d, int i, ByteBuffer c0043n) {
        ByteBuffer c0043nM1357m = m326a(c0033d, 2).writeShortBE(i >> 8).writeShortBE(i & 255).writeShortBE(0);
        int i2 = c0033d.f270b + 1;
        c0033d.f270b = i2;
        return c0043nM1357m.writeIntBE(i2).writeBuffer(c0043n).updateLength();
    }
}
