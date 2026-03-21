package p000;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: m */
/* loaded from: MobileAgent_3.9.jar:m.class */
public final class ContactInfo extends Hashtable {
    public ContactInfo(Contact abstractC0041l) {
        put(ResourceManager.m967e(-2), abstractC0041l.account);
        m1258a(0, abstractC0041l.displayName);
        if (abstractC0041l instanceof MrimContact) {
            m1258a(3, ((MrimContact) abstractC0041l).f297d);
        } else if (abstractC0041l instanceof MmpContact) {
            m1283d(Utils.parseInt((Object) ((MmpContact) abstractC0041l).f57c));
        } else if (abstractC0041l instanceof XmppContact) {
            m1258a(26, ((XmppContact) abstractC0041l).f38a);
        }
    }

    public ContactInfo() {
    }

    private ContactInfo(Account abstractC0037h) {
        put(ResourceManager.m967e(-2), abstractC0037h);
    }

    /* renamed from: a */
    public static final ContactInfo m1251a(Account abstractC0037h) {
        return new ContactInfo(abstractC0037h);
    }

    /* renamed from: a */
    public final boolean m1252a() {
        if (m1256a(26) == null) {
            return m1255c() != null && (m1255c() instanceof XmppProtocol);
        }
        return true;
    }

    /* renamed from: b */
    public final boolean m1253b() {
        if (m1256a(60) != null) {
            return false;
        }
        if ((m1255c() == null || !(m1255c() instanceof MmpProtocol)) && m1256a(26) == null) {
            return m1255c() == null || !(m1255c() instanceof XmppProtocol);
        }
        return false;
    }

    /* renamed from: b */
    public static final ContactInfo m1254b(Account abstractC0037h) {
        return new ContactInfo(abstractC0037h);
    }

    /* renamed from: c */
    public final Account m1255c() {
        return (Account) get(ResourceManager.m967e(-2));
    }

    /* renamed from: a */
    public final String m1256a(int i) {
        return (String) get(ResourceManager.m967e(i));
    }

    /* renamed from: a */
    public final ContactInfo m1257a(String str) {
        return m1258a(-1, str);
    }

    /* renamed from: a */
    private final ContactInfo m1258a(int i, String str) {
        if (Utils.nonEmpty(str)) {
            put(ResourceManager.m967e(i), str);
        }
        return this;
    }

    /* renamed from: b */
    public final ContactInfo m1259b(String str) {
        return m1258a(0, str);
    }

    /* renamed from: c */
    public final ContactInfo m1260c(String str) {
        return m1258a(1, str);
    }

    /* renamed from: d */
    public final ContactInfo m1261d(String str) {
        return m1258a(2, str);
    }

    /* renamed from: e */
    public final ContactInfo m1262e(String str) {
        return m1258a(3, str);
    }

    /* renamed from: d */
    public final ContactInfo m1263d() {
        return m1258a(4, AppState.getString(318));
    }

    /* renamed from: e */
    public final ContactInfo m1264e() {
        return m1258a(4, AppState.getString(319));
    }

    /* renamed from: f */
    public final ContactInfo m1265f(String str) {
        return m1258a(6, str);
    }

    /* renamed from: g */
    public final ContactInfo m1266g(String str) {
        return m1258a(9, str);
    }

    /* renamed from: h */
    public final ContactInfo m1267h(String str) {
        return m1258a(10, str);
    }

    /* renamed from: i */
    public final ContactInfo m1268i(String str) {
        return m1258a(32, str);
    }

    /* renamed from: j */
    public final ContactInfo m1269j(String str) {
        return m1258a(33, str);
    }

    /* renamed from: k */
    public final ContactInfo m1270k(String str) {
        return m1258a(34, str);
    }

    /* renamed from: l */
    public final ContactInfo m1271l(String str) {
        return m1258a(35, str);
    }

    /* renamed from: m */
    public final ContactInfo m1272m(String str) {
        return m1258a(36, str);
    }

    /* renamed from: n */
    public final ContactInfo m1273n(String str) {
        return m1258a(37, str);
    }

    /* renamed from: o */
    public final ContactInfo m1274o(String str) {
        return m1258a(8, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0048  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ContactInfo m1275b(int i) {
        String strM584b;
        int i2 = i % 10;
        if (i <= 0 || i >= 100) {
            strM584b = AppState.getString(323);
        } else if (i < 5 || i > 20) {
            strM584b = i2 == 1 ? m1276a(i, 321) : (i2 < 2 || i2 > 4) ? m1276a(i, 320) : m1276a(i, 322);
        } else {
            strM584b = m1276a(i, 320);
        }
        return m1258a(5, strM584b);
    }

    /* renamed from: a */
    private static final String m1276a(int i, int i2) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(i).append(AppState.getString(i2)));
    }

    /* renamed from: c */
    public final ContactInfo m1277c(int i) {
        return i == 1 ? m1264e() : i == 2 ? m1263d() : m1258a(4, AppState.getString(197069));
    }

    /* renamed from: p */
    public final ContactInfo m1278p(String str) {
        int iM511a = Utils.m511a(str, 1, 12, 0);
        if (iM511a != 0) {
            Vector vectorM512e = Utils.splitByNull(AppState.getString(685));
            m1258a(7, (String) vectorM512e.elementAt(iM511a));
            NetworkUtils.releaseVector(vectorM512e);
        }
        return this;
    }

    /* renamed from: q */
    public final ContactInfo m1279q(String str) {
        return m1258a(50, str);
    }

    /* renamed from: r */
    public final ContactInfo m1280r(String str) {
        return m1258a(51, str);
    }

    /* renamed from: s */
    public final ContactInfo m1281s(String str) {
        return m1258a(52, str);
    }

    /* renamed from: t */
    public final ContactInfo m1282t(String str) {
        return m1258a(53, str);
    }

    /* renamed from: d */
    public final ContactInfo m1283d(int i) {
        return m1258a(60, StringUtils.intern(Integer.toString(i)));
    }

    /* renamed from: u */
    public final ContactInfo m1284u(String str) {
        return m1258a(60, str);
    }

    /* renamed from: e */
    public final ContactInfo m1285e(int i) {
        return m1258a(61, StringUtils.intern(Integer.toString(i)));
    }

    /* renamed from: h */
    private final String m1286h(int i) {
        return Utils.defaultStr(m1256a(i));
    }

    /* renamed from: f */
    public final String m1287f() {
        return m1286h(0);
    }

    /* renamed from: g */
    public final String m1288g() {
        return m1286h(1);
    }

    /* renamed from: h */
    public final String m1289h() {
        return m1286h(2);
    }

    /* renamed from: i */
    public final String m1290i() {
        String strM1256a = m1256a(3);
        return strM1256a != null ? strM1256a : m1286h(60);
    }

    /* renamed from: j */
    public final String m1291j() {
        String strM533j = Utils.trim(m1286h(0));
        return StringUtils.isEmpty(strM533j) ? m1256a(60) : strM533j;
    }

    /* renamed from: k */
    public final String m1292k() {
        String strM533j = Utils.trim(StringUtils.concat(Utils.m527g(m1286h(1)), Utils.trim(m1286h(2))));
        String str = strM533j;
        if (StringUtils.isEmpty(strM533j)) {
            String strM1256a = m1256a(0);
            str = strM1256a;
            if (null == strM1256a) {
                return m1290i();
            }
        }
        return str;
    }

    /* JADX WARN: Removed duplicated region for block: B:99:0x0302  */
    /* renamed from: f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Screen m1293f(int i) {
        int i2 = 0;
        int iIndexOf;
        int iIndexOf2;
        int iIndexOf3;
        Account abstractC0037hM1255c = m1255c();
        Screen c0013amM75b = ScreenManager.m75b(i);
        Vector vectorM512e = Utils.splitByNull(AppState.getString(312));
        int size = vectorM512e.size();
        if (abstractC0037hM1255c instanceof MrimAccount) {
            MrimContact c0035f = (MrimContact) abstractC0037hM1255c.getContact((Object) m1256a(3));
            int i3 = 0;
            while (i3 < size) {
                try {
                    String strM1215a = NetworkUtils.bufToStringCached(Utils.m497a(NetworkUtils.newStringBuffer().append((String) vectorM512e.elementAt(i3))));
                    String strM1256a = m1256a(i3);
                    if (null != strM1256a) {
                        if (i3 == 6) {
                            c0013amM75b.m248a(strM1215a, NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(StringUtils.substring(strM1256a, 8, 10)).append('/').append(StringUtils.substring(strM1256a, 5, 7)).append('/').append(StringUtils.prefix(strM1256a, 4))));
                        } else {
                            if (i3 == 10) {
                                c0013amM75b.m225a(MenuItem.m889d().m901a(strM1215a, 0, 6).m896a(c0035f == null ? AppController.m349a(Utils.m511a(strM1256a, 0, 4, 0), Utils.defaultStr(m1256a(12))) : c0035f.getIcon()).m898b(Utils.defaultStr(m1256a(13))));
                                break;
                            }
                            c0013amM75b.m248a(strM1215a, i3 == 9 ? Utils.m530h(Utils.m532i(strM1256a)) : strM1256a);
                        }
                    }
                } catch (Throwable unused) {
                }
                i3++;
            }
            if (c0035f != null) {
                String strM522f = Utils.defaultStr(c0035f.f301h);
                int i4 = Conversation.m1104a(strM522f, 927) ? 936 : Conversation.m1104a(strM522f, 926) ? 935 : Conversation.m1104a(strM522f, 929) ? 937 : Conversation.m1104a(strM522f, 928) ? 938 : Conversation.m1104a(strM522f, 930) ? 939 : Conversation.m1104a(strM522f, 931) ? 940 : Conversation.m1104a(strM522f, 932) ? 941 : Conversation.m1104a(strM522f, 933) ? 942 : 934;
                StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
                if (i4 == 934) {
                    int iM627a = AppState.indexOfLong(strM522f, 2467256188365532259L);
                    if (iM627a >= 0 && (iIndexOf3 = strM522f.indexOf(34, iM627a + 9)) >= 0) {
                        stringBufferM1217h.append(StringUtils.substring(strM522f, iM627a + 8, iIndexOf3));
                    }
                } else {
                    stringBufferM1217h.append(AppState.getString(i4));
                }
                int iM628b = AppState.indexOfPool(strM522f, 943);
                if (iM628b >= 0 && (iIndexOf = strM522f.indexOf(34, iM628b + 11)) >= 0) {
                    stringBufferM1217h.append(AppState.getString(944)).append(StringUtils.substring(strM522f, iM628b + 10, iIndexOf));
                    int iM628b2 = AppState.indexOfPool(strM522f, 527990);
                    if (iM628b2 >= 0 && (iIndexOf2 = strM522f.indexOf(34, iM628b2 + 9)) >= 0) {
                        stringBufferM1217h.append('.').append(StringUtils.substring(strM522f, iM628b2 + 8, iIndexOf2));
                    }
                }
                String strM1215a2 = NetworkUtils.bufToStringCached(stringBufferM1217h);
                if (Utils.nonEmpty(strM1215a2)) {
                    MenuItem c0032cM901a = MenuItem.m889d().m901a(AppState.getString(317), 0, 6);
                    String str = c0035f.f301h;
                    if (str == null) {
                        i2 = -1;
                        c0013amM75b.m225a(c0032cM901a.m896a(i2).m898b(strM1215a2));
                    } else {
                        if (Conversation.m1104a(str, 927)) {
                            i2 = 357;
                        } else if (Conversation.m1104a(str, 926)) {
                            i2 = 317;
                        } else if (Conversation.m1104a(str, 929)) {
                            i2 = 355;
                        } else if (Conversation.m1104a(str, 928)) {
                            i2 = 356;
                        } else if (Conversation.m1104a(str, 930)) {
                            i2 = 358;
                        } else if (Conversation.m1104a(str, 931) || Conversation.m1104a(str, 932)) {
                            i2 = 359;
                        } else if (Conversation.m1104a(str, 933)) {
                            i2 = 307;
                        }
                        c0013amM75b.m225a(c0032cM901a.m896a(i2).m898b(strM1215a2));
                    }
                }
                String str2 = c0035f.f304j;
                if (Utils.nonEmpty(str2)) {
                    c0013amM75b.m225a(MenuItem.m889d().m901a(AppState.getString(324), 0, 6).m896a(242).m898b(str2));
                }
                String str3 = c0035f.f303i;
                if (Utils.nonEmpty(str3)) {
                    c0013amM75b.m225a(MenuItem.m889d().m901a(AppState.getString(325), 0, 6).m896a(2).m898b(str3));
                }
                String strM998o = c0035f.m998o();
                if (Utils.nonEmpty(strM998o)) {
                    c0013amM75b.m225a(MenuItem.m889d().m901a(AppState.getString(326), 0, 6).m896a(365).m898b(strM998o));
                }
            }
        } else if (abstractC0037hM1255c instanceof MmpProtocol) {
            String strM1256a2 = m1256a(60);
            if (null != strM1256a2) {
                c0013amM75b.m248a(Utils.m527g(AppState.getString(263250)), strM1256a2);
            }
            for (int i5 = 0; i5 < 5; i5++) {
                try {
                    String strM1256a3 = m1256a(i5);
                    if (null != strM1256a3) {
                        c0013amM75b.m248a(NetworkUtils.bufToStringCached(Utils.m497a(NetworkUtils.newStringBuffer().append(vectorM512e.elementAt(i5)))), strM1256a3);
                    }
                } catch (Throwable unused2) {
                }
            }
            String strM1256a4 = m1256a(5);
            if (null != strM1256a4) {
                c0013amM75b.m248a(AppState.getString(315), strM1256a4);
            }
            String strM1256a5 = m1256a(32);
            if (null != strM1256a5) {
                c0013amM75b.m248a(AppState.getString(313), strM1256a5);
            }
            String strM1256a6 = m1256a(37);
            if (null != strM1256a6) {
                c0013amM75b.m248a(AppState.getString(314), strM1256a6);
            }
            String strM1256a7 = m1256a(36);
            if (null != strM1256a7) {
                c0013amM75b.m248a(AppState.getString(316), strM1256a7);
            }
        } else if (abstractC0037hM1255c instanceof XmppProtocol) {
            Image image = (Image) get(ResourceManager.m967e(25));
            if (image != null) {
                c0013amM75b.m225a(MenuItem.m893a(new GraphicsContext(image)));
            }
            c0013amM75b.m246a(Utils.parseInt((Object) m1256a(24)), m1286h(0), 0);
            c0013amM75b.m245a(AppState.getString(744), m1256a(26), 0);
            String strM1256a8 = m1256a(11);
            if (null != strM1256a8) {
                c0013amM75b.m253a(strM1256a8);
            }
        }
        NetworkUtils.releaseVector(vectorM512e);
        return c0013amM75b;
    }

    /* renamed from: v */
    public final void m1294v(String str) {
        m1258a(12, str);
    }

    /* renamed from: w */
    public final void m1295w(String str) {
        m1258a(13, str);
    }

    /* renamed from: x */
    public final ContactInfo m1296x(String str) {
        return m1258a(11, str);
    }

    /* renamed from: y */
    public final ContactInfo m1297y(String str) {
        return m1258a(11, str);
    }

    /* renamed from: g */
    public final ContactInfo m1298g(int i) {
        return m1258a(24, StringUtils.intern(Integer.toString(i)));
    }
}
