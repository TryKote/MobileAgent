package p000;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/* renamed from: bb */
/* loaded from: MobileAgent_3.9.jar:bb.class */
public final class IOUtils {

    /* renamed from: a */
    public int f235a;

    /* renamed from: b */
    public Object f236b;

    /* renamed from: c */
    public static Vector f237c;

    /* renamed from: d */
    public static Vector f238d;

    /* renamed from: e */
    public static String[] f239e;

    /* renamed from: f */
    private static Screen f240f;

    public IOUtils(int i, Object obj) {
        this.f235a = i;
        this.f236b = obj;
    }

    /* renamed from: a */
    public static final int m752a(String str, int i) {
        String strM584b = AppState.m584b(1346);
        m815g(strM584b);
        int iM586d = AppState.m586d(1513);
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        Message c0026azM1415b = c0028ba.m745h(iM586d).m1415b(strM584b);
        String strM673d = c0026azM1415b.m673d();
        Vector vectorM668b = c0026azM1415b.m668b();
        Vector vectorM669c = c0026azM1415b.m669c();
        XmppMailRuProtocol.m869c(vectorM668b);
        boolean zM587e = AppState.m587e(96);
        String strM584b2 = AppState.m584b(198549);
        String strM584b3 = AppState.m584b(198546);
        String str2 = AppState.f181d;
        if (i == 48) {
            ScreenBuilder.m549c();
            ScreenBuilder.m549c();
        }
        if (StringUtils.m3a(839, str)) {
            if (!zM587e) {
                return ResourceManager.m966a(XmppMailRuProtocol.m866b(vectorM668b), new StringBuffer().append(strM584b2).append(strM673d).toString(), str2);
            }
            XmppMailRuProtocol.m872b(54, 0);
            return 0;
        }
        if (StringUtils.m3a(840, str)) {
            if (!zM587e) {
                return ResourceManager.m966a(XmppMailRuProtocol.m865a(XmppMailRuProtocol.m864a(vectorM668b), vectorM669c), new StringBuffer().append(strM584b2).append(strM673d).toString(), str2);
            }
            XmppMailRuProtocol.m872b(54, 1);
            return 0;
        }
        if (StringUtils.m3a(841, str)) {
            if (!zM587e) {
                return ResourceManager.m966a(NetworkUtils.m1213g(), new StringBuffer().append(strM584b3).append(strM673d).toString(), str2);
            }
            XmppMailRuProtocol.m872b(54, 2);
            return 0;
        }
        if (StringUtils.m3a(855, str)) {
            AppState.m594c(1525, 2);
            return 0;
        }
        if (StringUtils.m3a(856, str)) {
            AppState.m594c(1525, 1);
            return 0;
        }
        if (!StringUtils.m3a(845, str)) {
            return 0;
        }
        AppState.m594c(1527, c0028ba.m749X().f409a);
        return 0;
    }

    /* renamed from: a */
    public static final ByteBuffer m753a(MmpProtocol c0033d, MmpContact c0009ai, String str) {
        return AppController.m464a(c0033d, 4888, new ByteBuffer().m1373h(c0009ai.f57c).m1376j(str).m1357m(0));
    }

    /* renamed from: a */
    public static final void m754a(int i) {
        m755m();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new ByteBuffer().m1310c(590318).m1310c(i + 430).m1360p(3145472).m1339k());
            Object objM761a = m761a((Object) byteArrayInputStream);
            AppState.f177b[1264] = objM761a;
            if (null != objM761a) {
                Player playerCreatePlayer = Manager.createPlayer(byteArrayInputStream, AppState.m584b(655831));
                AppState.f177b[1265] = m761a(playerCreatePlayer);
                try {
                    playerCreatePlayer.realize();
                } catch (Throwable unused) {
                }
                if (AppState.m587e(87)) {
                    try {
                        ((javax.microedition.media.control.VolumeControl) playerCreatePlayer.getControl(AppState.m584b(852449))).setLevel(AppState.m586d(88));
                    } catch (Throwable unused2) {
                    }
                }
                try {
                    playerCreatePlayer.prefetch();
                } catch (Throwable unused3) {
                }
                try {
                    playerCreatePlayer.start();
                } catch (Throwable unused4) {
                }
                AppController.m304a(6, 10000L);
            }
        } catch (Throwable unused5) {
        }
    }

    /* renamed from: m */
    private static final void m755m() {
        Player player = (Player) AppState.f177b[1265];
        if (player != null) {
            m762b(player);
            try {
                player.stop();
            } catch (Throwable unused) {
            }
            try {
                player.close();
            } catch (Throwable unused2) {
            }
        }
        m763a((InputStream) AppState.f177b[1264]);
        AppState.m590b(1264, 1265);
    }

    /* renamed from: a */
    public static final void m756a() {
        boolean z;
        long[] jArr = AppController.f147a;
        long j = jArr[6];
        if (j == 0 || j >= System.currentTimeMillis()) {
            z = false;
        } else {
            jArr[6] = 0;
            z = true;
        }
        if (z) {
            m755m();
        }
    }

    /* renamed from: a */
    public static final int m757a(Account abstractC0037h) {
        Vector vector = abstractC0037h.f313i;
        int iM541c = Utils.m541c(vector);
        if (iM541c > 0) {
            StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
            for (int i = 0; i < iM541c; i++) {
                stringBufferM1217h.append(((ContactGroup) vector.elementAt(i)).f398f).append((char) 0);
            }
            AppState.m588a(1323, stringBufferM1217h);
            AppState.f177b[1324] = vector;
            AppState.m594c(1507, 0);
        }
        return iM541c;
    }

    /* renamed from: b */
    public static final void m758b() {
        ContactInfo c0042m = (ContactInfo) AppState.f177b[1319];
        Account abstractC0037hM1255c = c0042m.m1255c();
        if (m757a(abstractC0037hM1255c) == 0) {
            m778d((Object) AppState.m584b(743));
            return;
        }
        if (AppState.m587e(1508)) {
            AppState.m589a(1322, 331);
            AppState.m594c(1508, 0);
        } else {
            AppState.m589a(1322, 741);
        }
        if (abstractC0037hM1255c.mo80a() == 1) {
            AppState.m601a(1320, (Object) c0042m.m1256a(60));
            AppState.m601a(1321, (Object) c0042m.m1291j());
            ScreenManager.m71b(ScreenManager.m75b(3920));
            return;
        }
        if (((MrimAccount) abstractC0037hM1255c).f227c) {
            AppState.m594c(1509, 1);
            AppState.m594c(3897, 5);
        } else {
            AppState.m594c(1509, 0);
            AppState.m594c(3897, 4);
        }
        AppState.m601a(1320, (Object) c0042m.m1290i());
        AppState.m601a(1321, (Object) c0042m.m1292k());
        ScreenManager.m71b(ScreenManager.m75b(3888));
    }

    /* renamed from: a */
    public static final void m759a(MrimAccount c0028ba, int i, String str, String str2) {
        boolean zM587e = AppState.m587e(91);
        boolean zM587e2 = AppState.m587e(90);
        if (zM587e2 || zM587e) {
            if (str != null) {
                int iLastIndexOf = str.lastIndexOf(60);
                if (str.length() > 30 && iLastIndexOf > 1) {
                    StringUtils.m13b(str, iLastIndexOf - 1);
                }
                ResourceManager.m925a(0);
            }
            if (zM587e && (AppController.m442U() != 10 || !AppState.m579a())) {
                StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
                if (str2 != null && str != null) {
                    m785b(c0028ba, NetworkUtils.m1215a(stringBufferM1217h.append(AppState.m584b(917)).append(str).append(' ').append('\"').append(str2).append('\"').append('.').append('\n').append(new StringBuffer().append(i > 0 ? new StringBuffer().append(AppState.m584b(918)).append(i).append(AppState.m584b(919 + Utils.m540f(i))).append('\n').toString() : AppState.f181d).append(AppState.m584b(916)).toString())));
                } else if (i > 0) {
                    m785b(c0028ba, NetworkUtils.m1215a(stringBufferM1217h.append(AppState.m584b(918)).append(i).append(AppState.m584b(919 + Utils.m540f(i))).append('\n').append(AppState.m584b(916))));
                }
            }
            if (zM587e2) {
                if (i > 0 || !(str2 == null || str == null)) {
                    AppController.m356y();
                    AppController.m411b(c0028ba);
                    if (AppState.m587e(90)) {
                        AppState.m614m(1244).addElement(c0028ba);
                    }
                    TabBar.m178j();
                }
            }
        }
    }

    /* renamed from: c */
    public static final void m760c() {
        ChatRoom c0052wM745h = ((MrimAccount) AppState.m616i()).m745h(AppState.m586d(1513));
        Screen c0013amM75b = ScreenManager.m75b(4527);
        c0013amM75b.m224a(234, c0052wM745h.m1425f());
        Vector vectorM1213g = NetworkUtils.m1213g();
        Enumeration enumerationElements = c0052wM745h.f414f.elements();
        while (enumerationElements.hasMoreElements()) {
            Hashtable hashtable = c0052wM745h.f416h;
            Object objNextElement = enumerationElements.nextElement();
            if (hashtable.containsKey(objNextElement)) {
                vectorM1213g.addElement(c0052wM745h.f416h.get(objNextElement));
            }
        }
        Enumeration enumerationElements2 = vectorM1213g.elements();
        while (enumerationElements2.hasMoreElements()) {
            c0013amM75b.m225a(((Message) enumerationElements2.nextElement()).m667a(c0052wM745h));
        }
        if (c0013amM75b.f108m.size() == 0) {
            c0013amM75b.f103i = false;
            c0013amM75b.m255a(835);
        } else {
            c0013amM75b.f105j = AppState.m586d(1514);
            c0013amM75b.m257b(AppState.m584b(1345));
            c0013amM75b.m235n();
        }
        c0013amM75b.f127y = true;
        ScreenManager.m71b(c0013amM75b);
    }

    /* renamed from: a */
    public static final Object m761a(Object obj) {
        if (obj != null) {
            f237c.addElement(obj);
        }
        return obj;
    }

    /* renamed from: b */
    public static final void m762b(Object obj) {
        if (obj != null) {
            Utils.m525a(f237c, obj);
        }
    }

    /* renamed from: a */
    public static final void m763a(InputStream inputStream) {
        if (inputStream != null) {
            try {
                m762b(inputStream);
                inputStream.close();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final void m764a(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                m762b(outputStream);
                outputStream.close();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final void m765a(Connection connection) {
        if (connection != null) {
            try {
                m762b(connection);
                connection.close();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final void m766a(RecordStore recordStore) {
        if (recordStore != null) {
            try {
                m762b(recordStore);
                recordStore.closeRecordStore();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final RecordStore m767a(String str, boolean z) throws RecordStoreException {
        return (RecordStore) m761a((Object) RecordStore.openRecordStore(str, z));
    }

    /* renamed from: d */
    public static final void m768d() {
        boolean z;
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        f239e = c0028ba.f231g.f22j;
        Vector vectorM1213g = NetworkUtils.m1213g();
        Enumeration enumerationElements = c0028ba.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (!abstractC0041l.mo990d() && !abstractC0041l.mo143m()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        int size = vectorM1213g.size();
        Screen c0013amM75b = ScreenManager.m75b(4248);
        f238d = NetworkUtils.m1213g();
        for (int i = 0; i < size; i++) {
            MrimContact c0035f = (MrimContact) vectorM1213g.elementAt(i);
            String strMo135a = c0035f.mo135a();
            String str = c0035f.f376u;
            String[] strArr = f239e;
            int length = strArr.length;
            while (true) {
                length--;
                if (length >= 0) {
                    if (StringUtils.m6a(strMo135a, strArr[length])) {
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            c0013amM75b.m225a(MenuItem.m890a(str, z));
            f238d.addElement(strMo135a);
        }
        f240f = c0013amM75b;
        ScreenManager.m71b(c0013amM75b);
    }

    /* renamed from: e */
    public static final int m769e() {
        Vector vector = f240f.f108m;
        Vector vectorM1213g = NetworkUtils.m1213g();
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            if (((Boolean) ((MenuItem) vector.elementAt(i)).f265d).booleanValue()) {
                vectorM1213g.addElement(f238d.elementAt(i));
            }
        }
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        VCard c0003ac = c0028ba.f231g;
        c0003ac.f23k = c0003ac.f22j;
        int size2 = vectorM1213g.size();
        c0003ac.f22j = new String[size2];
        for (int i2 = 0; i2 < size2; i2++) {
            c0003ac.f22j[i2] = (String) vectorM1213g.elementAt(i2);
        }
        String[] strArr = c0028ba.f231g.f22j;
        XmlElement c0022av = new XmlElement(114);
        XmlElement c0022av2 = new XmlElement("visible", c0022av, null);
        c0022av.m552a(c0022av2);
        for (String str : strArr) {
            XmlElement c0022av3 = new XmlElement("u", c0022av2, null);
            c0022av3.m559a(328413, str);
            c0022av2.m552a(c0022av3);
        }
        c0028ba.m1052c(AppController.m321a(c0028ba, 4181, new ByteBuffer().m1308a("geo-list").m1308a(c0022av.toString())));
        if (c0028ba.f231g.f21i != 3) {
            return 0;
        }
        c0028ba.m728T();
        return 0;
    }

    /* renamed from: a */
    public static final int m770a(int i, Object obj) {
        if (i == 6) {
            return m771c(obj);
        }
        NetworkUtils.m1195d();
        String strM522f = Utils.m522f(AppState.m584b(1248));
        if (StringUtils.m1a(strM522f)) {
            return AppController.m338l(351);
        }
        boolean z = true;
        int i2 = 0;
        int length = strM522f.length();
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            char cCharAt = strM522f.charAt(length);
            if (cCharAt == '.') {
                i2 += 10;
            } else if (cCharAt == ',') {
                i2++;
            } else {
                z &= cCharAt >= '0' && cCharAt <= '9';
            }
        }
        if (z && i2 == 21) {
            try {
                long jM807b = m807b(m812f(strM522f));
                long jM808c = m808c(m811e(strM522f));
                MapRenderer.m649a(jM807b, jM808c);
                MapRenderer.m651a(StringUtils.m43a(jM807b, jM808c) ? 13 : 10);
            } catch (Throwable unused) {
            }
        } else {
            String strM1109a = Conversation.m1109a(strM522f, 1046, 199350);
            Image imageM615n = AppState.m615n(1364);
            long j = MapRenderer.f195c;
            new AsyncTask(9, new ByteBuffer().m1310c(1442705).m1310c(1511760).m1314d(Conversation.m1120b((Object) strM1109a)).m1310c(659815).m1383b(j).m1310c(659825).m1383b(MapRenderer.f196d).m1310c(659835).m1382s(imageM615n.getWidth()).m1310c(659845).m1382s(imageM615n.getHeight()).m1317c());
        }
        return AppState.m587e(1477) ? 161 : 6;
    }

    /* renamed from: c */
    public static final int m771c(Object obj) {
        if (AppState.m587e(1443)) {
            MapRenderer.m653a((MapPoint) obj);
            return 6;
        }
        if (!AppState.m587e(1477)) {
            ConnectionThread.m1165a((MapPoint) obj, true);
            return 6;
        }
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        c0028ba.m731a((MapPoint) obj);
        c0028ba.m724j();
        AppState.m594c(1477, 0);
        return 160;
    }

    /* renamed from: f */
    public static final void m772f() {
        ByteBuffer c0043nM1310c = new ByteBuffer().m1310c(1901187).m1314d(m810b((int) AppController.m318a((int) (MapRenderer.f198f - (MapRenderer.f194b / 2)), AppState.m586d(39)))).m1310c(393954).m1314d(m809a((int) AppController.m318a((int) (MapRenderer.f197e - (MapRenderer.f193a / 2)), AppState.m586d(39)))).m1310c(393960).m1314d(m810b((int) AppController.m318a((int) (MapRenderer.f198f + (MapRenderer.f194b / 2)), AppState.m586d(39)))).m1310c(393966).m1314d(m809a((int) AppController.m318a((int) (MapRenderer.f197e + (MapRenderer.f193a / 2)), AppState.m586d(39)))).m1310c(1376928);
        long jM692d = SoftFloat.m692d(4612811918334230528L, SoftFloat.m687b(((MapRenderer.f194b / 128) + 2) * ((MapRenderer.f193a / 128) + 2)));
        int iM586d = AppState.m586d(39);
        long j = MapRenderer.f197e;
        int i = MapRenderer.f193a / 2;
        long jM318a = AppController.m318a((int) (j + i), iM586d) - AppController.m318a((int) (MapRenderer.f197e - i), iM586d);
        long j2 = MapRenderer.f198f;
        int i2 = MapRenderer.f194b / 2;
        ByteBuffer c0043nM1314d = c0043nM1310c.m1314d(SoftFloat.m698a(SoftFloat.m693e(jM692d, SoftFloat.m687b(jM318a * (AppController.m318a((int) (j2 + i2), iM586d) - AppController.m318a((int) (MapRenderer.f198f - i2), iM586d)))), 100));
        VCard.f25m = (int) AppController.m318a((int) (MapRenderer.f197e - (MapRenderer.f193a / 2)), AppState.m586d(39));
        VCard.f26n = (int) AppController.m318a((int) (MapRenderer.f198f - (MapRenderer.f194b / 2)), AppState.m586d(39));
        VCard.f27o = (int) AppController.m318a((int) (MapRenderer.f197e + (MapRenderer.f193a / 2)), AppState.m586d(39));
        VCard.f28p = (int) AppController.m318a((int) (MapRenderer.f198f + (MapRenderer.f194b / 2)), AppState.m586d(39));
        VCard.f29q = AppState.m586d(39);
        new AsyncTask(20, new Object[]{c0043nM1314d.m1317c(), ResourceManager.m967e(AppState.m586d(39))});
    }

    /* renamed from: g */
    public static final void m773g() {
        m778d(AppState.f177b[1267]);
    }

    /* renamed from: h */
    public static final void m774h() {
        m778d(AppState.f177b[1268]);
    }

    /* renamed from: i */
    public static final void m775i() {
        m778d(AppState.f177b[1269]);
    }

    /* renamed from: j */
    public static final void m776j() {
        m778d(AppState.f177b[1270]);
    }

    /* renamed from: a */
    public static final void m777a(int i, int i2, int i3) {
        m778d(new int[]{0, i, i2, i3});
    }

    /* renamed from: d */
    public static final void m778d(Object obj) {
        Vector vectorM614m = AppState.m614m(1266);
        synchronized (vectorM614m) {
            vectorM614m.addElement(obj);
        }
    }

    /* renamed from: a */
    public static final void m779a(Object[] objArr, int i) {
        m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(455)).append(objArr[2]).append(AppState.m584b(457)).append(i)));
    }

    /* renamed from: b */
    public static final void m780b(Object[] objArr, int i) {
        m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(456)).append(objArr[2]).append(AppState.m584b(457)).append(i)));
    }

    /* renamed from: c */
    public static final void m781c(Object[] objArr, int i) {
        m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(454)).append(objArr[2]).append(AppState.m584b(457)).append(i)));
    }

    /* renamed from: b */
    public static final void m782b(int i) {
        m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(464)).append(AppState.m584b(457)).append(i)));
    }

    /* renamed from: a */
    public static final void m783a(Account abstractC0037h, int i) {
        m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(459)).append(abstractC0037h).append(AppState.m584b(460)).append(AppState.m584b(i))));
    }

    /* renamed from: a */
    public static final void m784a(Account abstractC0037h, String str) {
        m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(459)).append(abstractC0037h).append(AppState.m584b(460)).append(str)));
    }

    /* renamed from: b */
    private static void m785b(Account abstractC0037h, String str) {
        m778d((Object) new Object[]{abstractC0037h, str});
    }

    /* renamed from: a */
    public static final void m786a(MrimAccount c0028ba) {
        m778d(new IOUtils(6, c0028ba));
    }

    /* renamed from: a */
    public static final int m787a(String str) {
        String strM584b = AppState.m584b(1346);
        int iM586d = AppState.m586d(1513);
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        Message c0026azM1415b = c0028ba.m745h(iM586d).m1415b(strM584b);
        Vector vectorM668b = c0026azM1415b.m668b();
        Vector vectorM669c = c0026azM1415b.m669c();
        String strM673d = c0026azM1415b.m673d();
        String strM584b2 = AppState.m584b(198549);
        String strM584b3 = AppState.m584b(198546);
        String str2 = ((MrimAccount) AppState.m616i()).f315k;
        m815g(strM584b);
        if (StringUtils.m3a(839, str)) {
            ScreenBuilder.m549c();
            ResourceManager.m966a(XmppMailRuProtocol.m866b(vectorM668b), StringUtils.m9b(strM584b2, strM673d), Utils.m507d(c0026azM1415b.f223h));
            return 0;
        }
        if (!StringUtils.m3a(840, str)) {
            if (StringUtils.m3a(841, str)) {
                ScreenBuilder.m549c();
                ResourceManager.m966a(NetworkUtils.m1213g(), StringUtils.m9b(strM584b3, strM673d), Utils.m507d(c0026azM1415b.f223h));
                return 0;
            }
            if (!StringUtils.m3a(845, str)) {
                return 0;
            }
            AppState.m594c(1527, c0028ba.m749X().f409a);
            return 0;
        }
        ScreenBuilder.m549c();
        Vector vectorM865a = XmppMailRuProtocol.m865a(XmppMailRuProtocol.m864a(vectorM669c), vectorM668b);
        int iM541c = Utils.m541c(vectorM865a);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                break;
            }
            Object objElementAt = vectorM865a.elementAt(iM541c);
            if (StringUtils.m6a(str2, ((String[]) objElementAt)[0])) {
                vectorM865a.removeElement(objElementAt);
                break;
            }
        }
        ResourceManager.m966a(vectorM865a, StringUtils.m9b(strM584b2, strM673d), Utils.m507d(c0026azM1415b.f223h));
        return 0;
    }

    /* renamed from: a */
    private static final ByteBuffer m788a(MmpProtocol c0033d, MmpContact c0009ai, int i) {
        ByteBuffer c0043nM1357m = new ByteBuffer().m1361f(c0009ai.f57c).m1357m(0);
        int iM920k = c0033d.m920k();
        return c0033d.m916a(new Object[]{AppController.m464a(c0033d, 4872, c0043nM1357m.m1357m(iM920k).m1357m(i).m1357m(0)), ResourceManager.m967e(18), c0009ai, ResourceManager.m967e(i), ResourceManager.m967e(iM920k)});
    }

    /* renamed from: a */
    private static final ByteBuffer m789a(MmpProtocol c0033d, MmpContact c0009ai, int i, int i2) {
        return c0033d.m916a(new Object[]{AppController.m464a(c0033d, 4874, new ByteBuffer().m1361f(c0009ai.f57c).m1357m(0).m1357m(i).m1357m(i2).m1357m(0)), ResourceManager.m967e(19), c0009ai, ResourceManager.m967e(i2)});
    }

    /* renamed from: a */
    public static final ByteBuffer m790a(MmpProtocol c0033d, MmpContact c0009ai) {
        return c0009ai.mo140i() ? m789a(c0033d, c0009ai, c0009ai.f59d, 2) : m788a(c0033d, c0009ai, 2);
    }

    /* renamed from: b */
    public static final ByteBuffer m791b(MmpProtocol c0033d, MmpContact c0009ai) {
        return c0009ai.mo141j() ? m789a(c0033d, c0009ai, c0009ai.f60e, 3) : m788a(c0033d, c0009ai, 3);
    }

    /* renamed from: c */
    public static final ByteBuffer m792c(MmpProtocol c0033d, MmpContact c0009ai) {
        return c0009ai.mo142k() ? m789a(c0033d, c0009ai, c0009ai.f61f, 14) : m788a(c0033d, c0009ai, 14);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00a9  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final Screen m793a(Screen c0013am, Account abstractC0037h, Contact abstractC0041l) {
        MenuItem c0032cM899a = null;
        if (abstractC0041l != null) {
            abstractC0037h = abstractC0041l.f369o;
        }
        Vector vectorM1078P = abstractC0037h.m1078P();
        int size = vectorM1078P.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            MrimContact c0035f = (MrimContact) vectorM1078P.elementAt(size);
            if (c0035f.mo996n() || c0035f.mo143m() || c0035f.mo990d() || c0035f.mo144l()) {
                vectorM1078P.removeElementAt(size);
            }
        }
        AppController.m353a(vectorM1078P);
        for (int i = 0; i < vectorM1078P.size(); i++) {
            MrimContact c0035f2 = (MrimContact) vectorM1078P.elementAt(i);
            String str = c0035f2.f297d;
            String str2 = c0035f2.f376u;
            if (abstractC0041l != null) {
                MrimContact c0035f3 = (MrimContact) abstractC0041l;
                c0032cM899a = c0035f3.f305k != null && c0035f3.f305k.contains(str) ? new MenuItem(2, str2).m899a(375, str2) : MenuItem.m890a(str2, false);
            }
            c0032cM899a.f259b = str;
            c0013am.m225a(c0032cM899a);
        }
        NetworkUtils.m1212a(vectorM1078P);
        return c0013am;
    }

    /* renamed from: a */
    public static final Vector m794a(Screen c0013am, int i) {
        Vector vectorM1213g = NetworkUtils.m1213g();
        Vector vector = c0013am.f108m;
        int size = vector.size();
        while (true) {
            size--;
            if (size < i) {
                return vectorM1213g;
            }
            MenuItem c0032c = (MenuItem) vector.elementAt(size);
            Object obj = c0032c.f265d;
            if (obj != null && ((Boolean) obj).booleanValue()) {
                vectorM1213g.addElement(c0032c.f259b);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: l */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: b */
    public static final int m795b(String str, int i) {
        AppState.m591f(1281);
        Contact abstractC0041lM611g = AppState.m611g();
        if (i == 63 && !abstractC0041lM611g.f369o.m1056C()) {
            return AppController.m338l(299);
        }
        if (i == 54 || i == 63 || i == 85) {
            ScreenBuilder.m549c();
        }
        if (StringUtils.m3a(717, str)) {
            int iM993f = ((MrimContact) abstractC0041lM611g).m993f();
            return 0 != iM993f ? AppController.m338l(iM993f) : i;
        }
        if (i == 65) {
            ScreenBuilder.m549c();
            return ResourceManager.m946g();
        }
        if (i == 66) {
            if (abstractC0041lM611g instanceof XmppContact) {
                return ((XmppContact) abstractC0041lM611g).m149a(40);
            }
            AppState.f177b[1319] = new ContactInfo(abstractC0041lM611g);
        } else if (i == 54) {
            AppState.m617d(abstractC0041lM611g.f369o);
            ResourceManager.m966a(XmppMailRuProtocol.m871i(((MrimContact) abstractC0041lM611g).f297d), (String) null, (String) null);
        } else if (i == 6) {
            ListItem interfaceC0044o = (ListItem) abstractC0041lM611g;
            interfaceC0044o.mo279u();
            ConnectionThread.m1172a(interfaceC0044o);
        }
        return i;
    }

    /* renamed from: a */
    private static final Object[] m796a(int i, Object obj, int i2, ByteBuffer c0043n) {
        return new Object[]{ResourceManager.m967e(i), ResourceManager.m967e(i2), obj.toString(), c0043n};
    }

    /* renamed from: a */
    private static final Object[] m797a(int i, int i2, Object obj) {
        return m796a(i, NetworkUtils.m1217h().append(AppState.m584b(i2)).append(AppState.m584b(946)).append(obj), 0, (ByteBuffer) null);
    }

    /* renamed from: a */
    public static final Object[] m798a(Throwable th) {
        return m797a(1, 948, th);
    }

    /* renamed from: b */
    public static final Object[] m799b(Throwable th) {
        return m797a(2, 947, th);
    }

    /* renamed from: c */
    public static final Object[] m800c(Throwable th) {
        return m797a(4, 950, th);
    }

    /* renamed from: d */
    public static final Object[] m801d(Throwable th) {
        return m797a(3, 949, th);
    }

    /* renamed from: e */
    public static final Object[] m802e(Throwable th) {
        return m797a(5, 951, th);
    }

    /* renamed from: f */
    public static final Object[] m803f(Throwable th) {
        return m797a(6, 951, th);
    }

    /* renamed from: a */
    public static final Object[] m804a(int i, String str, ByteBuffer c0043n) {
        return m796a(0, str, i, c0043n);
    }

    /* renamed from: a */
    public static final boolean m805a(Object[] objArr) {
        return ((Integer) objArr[0]).intValue() == 0 && ((Integer) objArr[1]).intValue() == 200;
    }

    /* renamed from: e */
    private static Object m806e(Object[] objArr) {
        try {
            return JsonParser.m467a((ByteBuffer) objArr[3]);
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: b */
    public static final long m807b(String str) {
        return SoftFloat.m689d(SoftFloat.m692d(4708606483430899712L, SoftFloat.m692d(SoftFloat.m697a(str), 4580687790476533044L)));
    }

    /* renamed from: c */
    public static final long m808c(String str) {
        long jM697a = SoftFloat.m697a(str);
        long j = jM697a;
        if (jM697a > 4635963235168681984L) {
            j = 4635963235168681984L;
        }
        if (j < -4587408801686093824L) {
            j = -4587408801686093824L;
        }
        long jM692d = SoftFloat.m692d(j, 4580687790476533044L);
        long jM692d2 = SoftFloat.m692d(4590560114707566468L, SoftFloat.m706h(jM692d));
        return SoftFloat.m689d(SoftFloat.m691c(0L, SoftFloat.m692d(4708606483430899712L, SoftFloat.m705g(SoftFloat.m693e(SoftFloat.m707i(SoftFloat.m693e(SoftFloat.m691c(4609753056924675352L, jM692d), 4611686018427387904L)), SoftFloat.m704f(SoftFloat.m693e(SoftFloat.m691c(4607182418800017408L, jM692d2), SoftFloat.m690b(4607182418800017408L, jM692d2)), 4586056515080195972L))))));
    }

    /* renamed from: a */
    public static final String m809a(long j) {
        return SoftFloat.m698a(SoftFloat.m693e(SoftFloat.m693e(SoftFloat.m687b(j), 4708606483430899712L), 4580687790476533044L), 9);
    }

    /* renamed from: b */
    public static final String m810b(long j) {
        long jM703f = SoftFloat.m703f(SoftFloat.m693e(SoftFloat.m682a(SoftFloat.m687b(j)), 4708606483430899712L));
        long jM693e = SoftFloat.m693e(4590560114707566468L, 4611686018427387904L);
        long jM691c = SoftFloat.m691c(4609753056924675352L, SoftFloat.m692d(SoftFloat.m712j(jM703f), 4611686018427387904L));
        int i = 15;
        long jM691c2 = 4591870180066957722L;
        while (true) {
            i--;
            if (i <= 0 || SoftFloat.m685a(jM691c2 & Long.MAX_VALUE, 4502148214488346440L) <= 0) {
                break;
            }
            long jM692d = SoftFloat.m692d(4590560114707566468L, SoftFloat.m706h(jM691c));
            jM691c2 = SoftFloat.m691c(SoftFloat.m691c(4609753056924675352L, SoftFloat.m692d(SoftFloat.m712j(SoftFloat.m692d(jM703f, SoftFloat.m704f(SoftFloat.m693e(SoftFloat.m691c(4607182418800017408L, jM692d), SoftFloat.m690b(4607182418800017408L, jM692d)), jM693e))), 4611686018427387904L)), jM691c);
            jM691c = SoftFloat.m690b(jM691c, jM691c2);
        }
        return SoftFloat.m698a(SoftFloat.m693e(jM691c, 4580687790476533044L), 9);
    }

    /* renamed from: e */
    private static String m811e(String str) {
        try {
            return StringUtils.m13b(str, Utils.m529d(str, ' ').indexOf(44));
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: f */
    private static String m812f(String str) {
        try {
            return StringUtils.m15c(str, Utils.m529d(str, ' ').indexOf(44) + 1);
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: b */
    public static final void m813b(Object[] objArr) {
        AppController.m411b((MrimAccount) AppState.m616i());
        AppState.f177b[1271] = ConnectionThread.m1149a(objArr);
    }

    /* renamed from: e */
    public static final void m814e(Object obj) {
        AppState.f177b[1356] = obj;
    }

    /* renamed from: g */
    private static void m815g(String str) {
        Vector vectorM1213g = NetworkUtils.m1213g();
        vectorM1213g.addElement(str);
        m814e(vectorM1213g);
    }

    /* renamed from: k */
    public static final Object[] m816k() {
        Object[] objArrM609l = AppState.m609l(1271);
        if (objArrM609l != null && ConnectionThread.m1156c(objArrM609l) != null) {
            AppState.m591f(1271);
        }
        return objArrM609l;
    }

    /* renamed from: a */
    public static final StringBuffer m817a(StringBuffer stringBuffer, String str) {
        return stringBuffer.append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(str);
    }

    /* renamed from: c */
    public static final int m818c(Object[] objArr) {
        AppState.m591f(1355);
        if (!m805a(objArr)) {
            return AppController.m338l(888);
        }
        Object objM806e = m806e(objArr);
        if (objM806e == null) {
            return AppController.m338l(889);
        }
        if (!JsonParser.m486b(objM806e)) {
            return AppController.m338l(890);
        }
        AppState.f177b[1355] = objM806e;
        return 0;
    }

    /* renamed from: l */
    public static final Object m819l() {
        Object obj = AppState.f177b[1355];
        AppState.m591f(1355);
        return JsonParser.m482e(obj, 2);
    }

    /* renamed from: c */
    public static final int m820c(int i) {
        String strM522f = Utils.m522f(AppState.m584b(1293));
        String strM843u = XmppMailRuProtocol.m843u();
        String strM1215a = strM843u;
        if (StringUtils.m1a(strM843u)) {
            return AppController.m338l(301);
        }
        int iM586d = AppState.m586d(1474);
        if (iM586d != 0 && strM1215a.indexOf(64) < 0) {
            strM1215a = NetworkUtils.m1215a(NetworkUtils.m1217h().append(strM1215a).append(Utils.m512e(AppState.m584b(696)).elementAt(iM586d)));
        }
        if (i == 2 && strM1215a.indexOf(64) < 0) {
            return AppController.m338l(699);
        }
        int iM437a = AppController.m437a(i, AppState.m616i(), strM1215a, strM522f);
        if (0 != iM437a) {
            return AppController.m338l(iM437a);
        }
        AppController.m328a(AppController.m438b(i, strM1215a).m1054c(Utils.m522f(AppState.m584b(1297))));
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x0198  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void m821a(MmpProtocol c0033d, ByteBuffer c0043n) {
        int iM1353u;
        String strM1370r;
        String strM1368E;
        String strM1369q;
        long jM1341m = c0043n.m1341m();
        int iM1353u2 = c0043n.m1353u();
        String strM1363z = c0043n.m1363z();
        c0043n.m1353u();
        int iM1353u3 = c0043n.m1353u();
        while (true) {
            iM1353u3--;
            if (iM1353u3 < 0) {
                break;
            }
            c0043n.m1353u();
            c0043n.m1329g(c0043n.m1353u());
        }
        while (true) {
            int iM1353u4 = c0043n.m1353u();
            iM1353u = c0043n.m1353u();
            if (iM1353u4 == 2 || iM1353u4 == 5) {
                break;
            } else {
                c0043n.m1329g(iM1353u);
            }
        }
        switch (iM1353u2) {
            case 1:
                int i = iM1353u;
                while (true) {
                    if (i <= 0) {
                        strM1369q = null;
                        break;
                    } else {
                        int iM1353u5 = c0043n.m1353u();
                        int iM1353u6 = c0043n.m1353u();
                        int i2 = i - 4;
                        if (iM1353u5 == 257) {
                            int iM1353u7 = c0043n.m1353u();
                            c0043n.m1353u();
                            strM1369q = iM1353u7 == 2 ? c0043n.m1369q(iM1353u6 - 4) : c0043n.m1370r(iM1353u6 - 4);
                            break;
                        } else {
                            c0043n.m1329g(iM1353u6);
                            i = i2 - iM1353u6;
                        }
                    }
                }
                strM1370r = strM1369q;
                break;
            case 2:
                if (c0043n.m1353u() == 0) {
                    c0043n.m1329g(24);
                    int i3 = iM1353u - 26;
                    while (i3 > 0) {
                        int iM1353u8 = c0043n.m1353u();
                        int iM1353u9 = c0043n.m1353u();
                        i3 -= iM1353u9 + 4;
                        if (iM1353u8 == 10001) {
                            c0043n.m1354v();
                            c0043n.m1354v();
                            int iM1355w = c0043n.m1355w();
                            int iM1355w2 = c0043n.m1355w();
                            int iM1355w3 = c0043n.m1355w();
                            int iM1355w4 = c0043n.m1355w();
                            c0043n.m1353u();
                            c0043n.m1328e();
                            c0043n.m1344o();
                            c0043n.m1353u();
                            int iM1354v = c0043n.m1354v();
                            c0043n.m1353u();
                            c0043n.m1329g(iM1354v - 2);
                            if ((iM1355w | iM1355w2 | iM1355w3 | iM1355w4) == 0) {
                                c0043n.m1353u();
                                c0043n.m1354v();
                                c0043n.m1354v();
                                strM1368E = c0043n.m1368E();
                            } else {
                                strM1368E = null;
                            }
                            strM1370r = strM1368E;
                            if (strM1368E != null && strM1370r.length() > 0) {
                                c0033d.m1052c(AppController.m464a(c0033d, 1035, new ByteBuffer().m1323a(jM1341m).m1357m(2).m1373h(strM1363z).m1310c(3213669).m1358n(c0033d.m919j()).m1310c(3213718)));
                                break;
                            }
                        } else {
                            c0043n.m1329g(iM1353u9);
                        }
                    }
                    strM1368E = null;
                    strM1370r = strM1368E;
                    if (strM1368E != null) {
                        c0033d.m1052c(AppController.m464a(c0033d, 1035, new ByteBuffer().m1323a(jM1341m).m1357m(2).m1373h(strM1363z).m1310c(3213669).m1358n(c0033d.m919j()).m1310c(3213718)));
                    }
                } else {
                    strM1368E = null;
                    strM1370r = strM1368E;
                    if (strM1368E != null) {
                    }
                }
                break;
            case 3:
            default:
                strM1370r = null;
                break;
            case 4:
                c0043n.m1355w();
                int iM1353u10 = c0043n.m1353u();
                strM1370r = (iM1353u10 == 1 || iM1353u10 == 4) ? c0043n.m1370r(c0043n.m1354v() - 1) : null;
                break;
        }
        if (!Utils.m535l(strM1370r) || StringUtils.m2a(strM1363z, 875573297)) {
            return;
        }
        if (StringUtils.m2a(strM1363z, 49)) {
            throw new RuntimeException();
        }
        c0033d.m1072a(strM1363z, 0L, strM1370r);
    }

    /* renamed from: a */
    public static final void m822a(Contact abstractC0041l) {
        AppState.m599a(1504, (abstractC0041l instanceof XmppContact) && !((XmppProtocol) abstractC0041l.f369o).mo83f());
    }

    /* renamed from: c */
    public static final int m823c(String str, int i) {
        AppState.m591f(1281);
        Object obj = AppState.f177b[1365];
        if (i == 63 && !((Contact) obj).f369o.m1056C()) {
            return AppController.m338l(299);
        }
        if (i == 40 || i == 63 || i == 85) {
            ScreenBuilder.m549c();
            if (i != 85) {
                AppController.m300h();
            }
        }
        if (StringUtils.m3a(717, str)) {
            int iM993f = ((MrimContact) obj).m993f();
            if (0 != iM993f) {
                return AppController.m338l(iM993f);
            }
            return 40;
        }
        if (i == 65) {
            ScreenBuilder.m549c();
            AppController.m300h();
            return ResourceManager.m946g();
        }
        if (i == 66) {
            if (obj instanceof XmppContact) {
                return ((XmppContact) obj).m149a(4);
            }
            AppState.f177b[1319] = new ContactInfo((Contact) obj);
        } else if (i == 54) {
            AppState.m617d(((MrimContact) obj).f369o);
            ResourceManager.m966a(XmppMailRuProtocol.m871i(((MrimContact) obj).f297d), (String) null, (String) null);
        } else if (i == 6) {
            ListItem interfaceC0044o = (ListItem) obj;
            interfaceC0044o.mo279u();
            ConnectionThread.m1172a(interfaceC0044o);
            AppController.m331a(true, false, !AppState.m587e(276));
            AppState.m594c(281, 1);
        }
        return i;
    }

    /* renamed from: d */
    public static final int m824d(int i) {
        Account abstractC0037hM616i = AppState.m616i();
        switch (abstractC0037hM616i.mo80a()) {
            case 0:
                MrimAccount c0028ba = (MrimAccount) abstractC0037hM616i;
                if (i == 6) {
                    return 17;
                }
                if (i == 5) {
                    int iMo120l = c0028ba.mo120l();
                    if (0 != iMo120l) {
                        return AppController.m338l(iMo120l);
                    }
                    return 4;
                }
                int iM721d = c0028ba.m721d(new int[]{1, 260, 2, 516, 3}[i]);
                if (0 != iM721d) {
                    return AppController.m338l(iM721d);
                }
                return 4;
            case 1:
                MmpProtocol c0033d = (MmpProtocol) abstractC0037hM616i;
                if (i == 13) {
                    return 17;
                }
                if (i == 14) {
                    return 109;
                }
                if (i == 12) {
                    int iMo120l2 = c0033d.mo120l();
                    if (0 != iMo120l2) {
                        return AppController.m338l(iMo120l2);
                    }
                    return 4;
                }
                int iM918b = c0033d.m918b(new int[]{0, 32, 256, 2, 1, 4, 16, 24576, 20480, 16384, 12288, 8193}[i]);
                if (0 != iM918b) {
                    return AppController.m338l(iM918b);
                }
                return 4;
            default:
                XmppProtocol c0005ae = (XmppProtocol) abstractC0037hM616i;
                if (i == 0) {
                    int iMo120l3 = c0005ae.mo120l();
                    if (0 != iMo120l3) {
                        return AppController.m338l(iMo120l3);
                    }
                    return 4;
                }
                int iM103b = c0005ae.m103b(i);
                if (0 != iM103b) {
                    return AppController.m338l(iM103b);
                }
                return 4;
        }
    }

    /* renamed from: d */
    public static final String m825d(String str) {
        boolean zIsUpperCase = false;
        String str2 = null;
        String str3;
        Vector vectorM512e = Utils.m512e(AppState.m584b(14290598));
        Vector vectorM512e2 = Utils.m512e(AppState.m584b(958));
        Hashtable hashtable = new Hashtable();
        int size = vectorM512e.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            hashtable.put(vectorM512e.elementAt(size), vectorM512e2.elementAt(size));
        }
        String strM584b = AppState.m584b(956);
        String strM584b2 = AppState.m584b(957);
        Hashtable hashtable2 = new Hashtable();
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int length = strM584b.length();
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            hashtable2.put(StringUtils.m14b(stringBufferM1217h.append(strM584b.charAt(length))), StringUtils.m14b(stringBufferM1217h.append(strM584b2.charAt(length))));
        }
        int length2 = str.length();
        int i = 0;
        while (i < length2) {
            String strM9b = null;
            int i2 = 3;
            while (true) {
                if (i2 < 1) {
                    break;
                }
                try {
                    String strM12a = StringUtils.m12a(str, i, i + i2);
                    zIsUpperCase = Character.isUpperCase(strM12a.charAt(0));
                    str2 = (String) hashtable.get(StringUtils.m17c(strM12a.toLowerCase()));
                    strM9b = str2;
                } catch (Throwable unused) {
                }
                if (str2 != null) {
                    if (zIsUpperCase && (str3 = (String) hashtable2.get(StringUtils.m13b(strM9b, 1))) != null) {
                        strM9b = strM9b.length() == 1 ? str3 : StringUtils.m9b(str3, StringUtils.m15c(strM9b, 1));
                    }
                    i += i2 - 1;
                    stringBufferM1217h.append(strM9b);
                } else {
                    i2--;
                }
            }
            if (strM9b == null) {
                stringBufferM1217h.append(str.charAt(i));
            }
            i++;
        }
        NetworkUtils.m1212a(vectorM512e);
        NetworkUtils.m1212a(vectorM512e2);
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: d */
    public static final void m826d(Object[] objArr) {
        try {
            try {
                AppController.m343s();
                HttpClient c0024axM629a = HttpClient.m629a((String) objArr[1], (Account) objArr[0], 0);
                int iM634a = c0024axM629a.m634a();
                if (iM634a == 200) {
                    Vector vectorM516c = Utils.m516c(new ByteBuffer(c0024axM629a).m1317c(), '\n');
                    if (((Integer) objArr[2]).intValue() == 0) {
                        objArr[2] = ResourceManager.m967e(1);
                        objArr[1] = new ByteBuffer().m1310c(2365173).m1310c(2692947).m1316b(vectorM516c.elementAt(0)).m1321f(38).m1316b(vectorM516c.elementAt(1)).m1337i();
                        new AsyncTask(30, objArr);
                    } else {
                        m827a(objArr, vectorM516c.elementAt(0));
                    }
                    NetworkUtils.m1212a(vectorM516c);
                } else {
                    if (iM634a != 403) {
                        throw new Throwable(StringUtils.m17c(Integer.toString(iM634a)));
                    }
                    ((XmppProtocol) objArr[0]).m1065J();
                }
                HttpClient.m633a(c0024axM629a);
                AppController.m344t();
            } catch (Throwable th) {
                m827a(objArr, th);
                HttpClient.m633a((HttpClient) null);
                AppController.m344t();
            }
        } catch (Throwable th2) {
            HttpClient.m633a((HttpClient) null);
            AppController.m344t();
            throw th2;
        }
    }

    /* renamed from: a */
    private static final void m827a(Object[] objArr, Object obj) {
        ((XmppProtocol) objArr[0]).f35d = obj;
    }
}
