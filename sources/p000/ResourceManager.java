package p000;

import java.util.Calendar;
import java.util.Vector;
import javax.microedition.lcdui.Display;

/* renamed from: e */
/* loaded from: MobileAgent_3.9.jar:e.class */
public final class ResourceManager {

    /* renamed from: a */
    public final int f281a;

    /* renamed from: b */
    public final int f282b;

    /* renamed from: c */
    public final int f283c;

    /* renamed from: d */
    public final int f284d;

    /* renamed from: e */
    public final String f285e;

    /* renamed from: m */
    private static int f286m;

    /* renamed from: f */
    public static int f287f;

    /* renamed from: g */
    public static long f288g;

    /* renamed from: h */
    public static Vector f289h;

    /* renamed from: i */
    public static Object f290i;

    /* renamed from: j */
    public static Integer[] f291j;

    /* renamed from: k */
    public static Boolean f292k;

    /* renamed from: l */
    public static Boolean f293l;

    public ResourceManager(int i, int i2, int i3, int i4) {
        this.f281a = i;
        ByteBuffer c0043nM1385u = new ByteBuffer().writeUInt(4027430).writeUInt(i == 3 ? 1936548170 : 1936744781).writeUInt(4028966);
        this.f282b = i2;
        ByteBuffer c0043nM1385u2 = c0043nM1385u.writeIntAsString(i2).writeUInt(4028454);
        this.f283c = i3;
        ByteBuffer c0043nM1385u3 = c0043nM1385u2.writeIntAsString(i3).writeUInt(4028710);
        this.f284d = i4;
        this.f285e = c0043nM1385u3.writeIntAsString(i4).getStringAndClear();
    }

    public final boolean equals(Object obj) {
        return obj != null && (obj instanceof ResourceManager) && StringUtils.equals(this.f285e, ((ResourceManager) obj).f285e);
    }

    public final int hashCode() {
        return this.f283c ^ this.f284d;
    }

    /* renamed from: a */
    public static final void m925a(int i) {
        int i2 = 0;
        if (i == 1) {
            i2 = 2;
        } else if (i == 0) {
            i2 = 4;
        } else if (i == 3) {
            i2 = 6;
        } else if (i == 4) {
            i2 = 8;
        } else if (i == 5) {
            i2 = 10;
        } else if (i == 6) {
            i2 = 165;
        }
        m926a(AppState.getInt(i2 + 75), AppState.getBool(i2 + 76));
    }

    /* renamed from: a */
    public static final void m926a(int i, boolean z) {
        if (AppState.getBool(1449)) {
            if (z) {
                Display.getDisplay(AppState.getMidlet()).vibrate(250);
            }
            if (i == 0 || AppState.getBool(89) || !AppController.m307b(8, 1000L)) {
                return;
            }
            IOUtils.m754a(i);
        }
    }

    /* renamed from: a */
    public static final void m927a() {
        AppController.f147a[4] = 0;
        f286m = -1;
        f287f = 0;
        AppState.clearIndex(1263);
        m928b();
    }

    /* renamed from: b */
    public static final void m928b() {
        Calendar calendarM622k;
        int i;
        if (!AppController.m307b(4, 1000L) || (i = (calendarM622k = AppState.getCalendar()).get(12)) == f286m) {
            return;
        }
        String strM1215a = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(Utils.zeroPad(calendarM622k.get(11))).append(':').append(Utils.zeroPad(i)));
        AppState.setObject(1263, (Object) strM1215a);
        f287f = AppState.getGfxContext(0).m214a(strM1215a);
        f286m = i;
        AppController.f153g = true;
    }

    /* renamed from: a */
    public static final void m929a(Object[] objArr) {
        while (true) {
            XmppProtocol c0005ae = (XmppProtocol) objArr[0];
            XmlElement c0022avM47a = ((XmlParser) objArr[2]).parse();
            synchronized (c0005ae.f30a) {
                c0005ae.f30a.addElement(c0022avM47a);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x0098, code lost:
    
        java.lang.Thread.sleep(255);
     */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final int m930b(Object[] objArr) {
        while (true) {
            ByteBuffer c0043n = (ByteBuffer) objArr[1];
            synchronized (c0043n) {
                int i = c0043n.length;
                if (i > 0) {
                    int iM1343k = c0043n.peekUByteAt(0);
                    if ((iM1343k & 128) == 0) {
                        return c0043n.readUByte();
                    }
                    if (i != 1) {
                        int iM1343k2 = c0043n.peekUByteAt(1);
                        if (iM1343k < 224) {
                            c0043n.skip(2);
                            return ((iM1343k & 31) << 6) | (iM1343k2 & 63);
                        }
                        if (i != 2) {
                            c0043n.skip(2);
                            return ((iM1343k & 15) << 12) | ((iM1343k2 & 63) << 6) | (c0043n.readUByte() & 63);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: a */
    public static final void m931a(PhoneContact c0020at, int i) {
        m932a(VCard.m63a(c0020at, i), c0020at, i);
    }

    /* renamed from: a */
    public static final void m932a(String str, PhoneContact c0020at, int i) {
        new AsyncTask(21, new Object[]{str, c0020at, m967e(i)});
    }

    /* renamed from: a */
    public static final int m933a(String str, MenuItem c0032c) {
        Object[] objArr = (Object[]) c0032c.f265d;
        Object[] objArr2 = (Object[]) objArr[0];
        MenuItem c0032c2 = (MenuItem) objArr[1];
        Screen c0013am = (Screen) objArr[2];
        String[] strArr = (String[]) objArr2[1];
        int i = 0;
        int length = strArr.length;
        while (true) {
            length--;
            if (length < 0) {
                c0032c2.m884a().m898b(Utils.m527g(c0032c2.f259b)).m901a(strArr[i], 1, 7).m896a(247).f265d = new Object[]{m967e(i), strArr};
                c0013am.m258q();
                IOUtils.m778d(c0032c2);
                return 0;
            }
            if (str == strArr[length]) {
                i = length;
            }
        }
    }

    /* renamed from: a */
    public static final Object[] m934a(ByteBuffer c0043n) {
        try {
            int iM1328e = c0043n.readInt();
            if (iM1328e == 0) {
                return null;
            }
            Object[] objArr = new Object[iM1328e];
            for (int i = 0; i < iM1328e; i++) {
                String[] strArr = new String[6];
                for (int i2 = 0; i2 < 6; i2++) {
                    strArr[i2] = c0043n.readUTF8Str((String) null);
                }
                objArr[i] = strArr;
            }
            return objArr;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: c */
    public static final void m935c() {
        int i;
        int i2;
        int iM586d = AppState.getInt(1510);
        Account abstractC0037hM616i = AppState.getAccount();
        if (abstractC0037hM616i != null) {
            int iM1060a = abstractC0037hM616i.getSyncValue(iM586d, 0);
            i = iM1060a;
            m936a(1326, iM1060a);
            int iM1060a2 = abstractC0037hM616i.getSyncValue(iM586d, 1);
            i2 = iM1060a2;
            m936a(1325, iM1060a2);
            AppState.setInt(3987, 8);
            AppState.setInt(3994, 3);
        } else {
            m936a(1329, AppController.m428a(0, iM586d, 0));
            m936a(1328, AppController.m428a(0, iM586d, 1));
            m936a(1331, AppController.m428a(1, iM586d, 0));
            m936a(1330, AppController.m428a(1, iM586d, 1));
            m936a(1333, AppController.m428a(2, iM586d, 0));
            m936a(1332, AppController.m428a(2, iM586d, 1));
            m936a(1335, AppController.m428a(3, iM586d, 0));
            m936a(1334, AppController.m428a(3, iM586d, 1));
            int iM429b = AppController.m429b(iM586d, 0);
            i = iM429b;
            m936a(1326, iM429b);
            int iM429b2 = AppController.m429b(iM586d, 1);
            i2 = iM429b2;
            m936a(1325, iM429b2);
            AppState.setInt(3987, 5);
            AppState.setInt(3994, 16);
        }
        long j = i + i2;
        int iM586d2 = AppState.getInt(114) << 10;
        if (iM586d2 > 0) {
            long j2 = j % iM586d2;
            if (j2 > 0) {
                j += iM586d2 - j2;
            }
        }
        int iM586d3 = (int) ((j * AppState.getInt(113)) / 1048576);
        AppState.setFromBuffer(1327, NetworkUtils.newStringBuffer().append(iM586d3 / 100).append('.').append(Utils.zeroPad(iM586d3 % 100)).append(' ').append(AppState.getString(117)));
        AppState.setInt(3985, iM586d + 745);
        ScreenManager.m71b(ScreenManager.m75b(3985));
        AppState.clearRange(1325, 1335);
    }

    /* renamed from: a */
    private static final void m936a(int i, int i2) {
        AppState.setObject(i, (Object) Utils.formatSize(i2));
    }

    /* renamed from: a */
    public static final ByteBuffer m937a(MmpProtocol c0033d, String str) {
        ByteBuffer c0043nM1376j = new ByteBuffer().writeUTF(str);
        int iM920k = c0033d.m920k();
        return c0033d.m916a(new Object[]{AppController.m464a(c0033d, 4872, c0043nM1376j.writeShortBE(iM920k).writeShortBE(0).writeShortBE(1).writeShortBE(0)), m967e(4), str, m967e(iM920k)});
    }

    /* renamed from: a */
    public static final int m938a(String str) {
        int iM1052c;
        String strM522f = Utils.defaultStr(AppState.getString(1279));
        if (str != AppState.getString(1060)) {
            StringBuffer stringBufferM1196e = NetworkUtils.m1196e();
            if (StringUtils.m3a(473, str)) {
                AppState.setFromBuffer(1279, stringBufferM1196e.append(AppState.getString(1280)));
                return 0;
            }
            if (StringUtils.m3a(474, str)) {
                AppState.setObject(1280, (Object) strM522f);
                AppState.setBool(1460, true);
                return 0;
            }
            if (!StringUtils.m3a(476, str)) {
                return 0;
            }
            AppState.setObject(1279, (Object) Conversation.m1123k(strM522f));
            return 0;
        }
        String strM584b = AppState.getString(1314);
        MrimContact c0035f = (MrimContact) AppState.pool[1365];
        MrimAccount c0028ba = (MrimAccount) c0035f.account;
        if (c0028ba.isConnected()) {
            c0035f.appendMessage(1, NetworkUtils.bufToStringCached(Utils.m497a(NetworkUtils.newStringBuffer().append(AppState.getString(776)).append(Utils.m530h(strM584b))).append(strM522f)), 0L, 0L);
            StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append('+');
            if (strM584b.charAt(0) == '8') {
                stringBufferAppend.append('7').append(StringUtils.suffix(strM584b, 1));
            } else {
                stringBufferAppend.append(strM584b);
            }
            iM1052c = c0028ba.trySendData(c0028ba.m719a(new Object[]{AppController.m321a(c0028ba, 4153, new ByteBuffer().writeIntLE(0).writeStringLatin1(NetworkUtils.bufToStringCached(stringBufferAppend)).writeStringUTF16(strM522f)), m967e(6), c0035f, strM522f, strM584b}));
        } else {
            iM1052c = 299;
        }
        int i = iM1052c;
        if (0 != iM1052c) {
            return AppController.m338l(i);
        }
        return 0;
    }

    /* renamed from: a */
    public static final void m939a(Object obj) {
        if (!(obj instanceof String)) {
            return;
        }
        try {
            try {
                AppController.m343s();
                HttpClient c0024axM630a = HttpClient.m630a(obj);
                if (c0024axM630a.m634a() != 200) {
                    throw new Throwable();
                }
                String str = null;
                boolean z = false;
                Vector vector = new ByteBuffer(c0024axM630a).parseXml().children;
                int size = vector.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        throw new RuntimeException();
                    }
                    XmlElement c0022av = (XmlElement) vector.elementAt(size);
                    String str2 = c0022av.tagName;
                    String strM11a = StringUtils.fromBuffer(c0022av.textContent);
                    if (StringUtils.m3a(394658, str2) && StringUtils.m3a(197596, strM11a)) {
                        z = true;
                    } else if (StringUtils.m3a(263156, str2)) {
                        str = strM11a;
                    }
                    if (z && str != null) {
                        NetworkUtils.releaseVector(vector);
                        IOUtils.m778d((Object) AppState.getString(494));
                        HttpClient.m633a(c0024axM630a);
                        AppController.m344t();
                        return;
                    }
                }
            } catch (Throwable th) {
                IOUtils.m778d((Object) StringUtils.m8a(493, (Object) null));
                HttpClient.m633a((HttpClient) null);
                AppController.m344t();
            }
        } catch (Throwable th2) {
            HttpClient.m633a((HttpClient) null);
            AppController.m344t();
            throw th2;
        }
    }

    /* renamed from: d */
    public static final void m940d() {
        AppState.pool[986] = m942f(986);
        AppState.pool[987] = m942f(987);
        AppState.pool[990] = m942f(990);
        AppState.pool[991] = m942f(991);
        AppState.pool[989] = Utils.m537e(989);
        AppState.pool[988] = Utils.m537e(988);
        AppState.pool[992] = Utils.m536a(AppState.getBytes(992));
        AppState.pool[993] = Utils.m536a(AppState.getBytes(993));
        AppState.pool[580] = Utils.m536a(AppState.getBytes(580));
    }

    /* renamed from: e */
    public static final void m941e() {
        AppState.clearRange(984, 993);
    }

    /* renamed from: f */
    private static final long[] m942f(int i) {
        byte[] bArrM581a = AppState.getBytes(i);
        int length = bArrM581a.length >> 3;
        long[] jArr = new long[length];
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            long j = 0;
            do {
                int i4 = i2;
                i2++;
                j = (j << 8) | (bArrM581a[i4] & 255);
            } while ((i2 & 7) != 0);
            int i5 = i3;
            i3++;
            jArr[i5] = j;
        }
        NetworkUtils.releaseBytes(bArrM581a);
        return jArr;
    }

    /* renamed from: b */
    public static final long m943b(int i) {
        return ((long[]) AppState.pool[991])[i];
    }

    /* renamed from: c */
    public static final int m944c(int i) {
        return ((int[]) AppState.pool[992])[i];
    }

    /* renamed from: f */
    public static final int m945f() {
        NetworkUtils.m1195d();
        String strM522f = Utils.defaultStr(AppState.getString(1286));
        int i = 0;
        int iLastIndexOf = strM522f.lastIndexOf(46);
        int iLastIndexOf2 = iLastIndexOf;
        if (iLastIndexOf == -1) {
            iLastIndexOf2 = strM522f.lastIndexOf(44);
        }
        if (iLastIndexOf2 != -1) {
            try {
                i = Integer.parseInt(StringUtils.prefix(strM522f, iLastIndexOf2)) * 100;
            } catch (Throwable unused) {
            }
            try {
                String strM15c = StringUtils.suffix(strM522f, iLastIndexOf2 + 1);
                int i2 = Integer.parseInt(strM15c);
                i += strM15c.length() == 1 ? i2 * 10 : i2;
            } catch (Throwable unused2) {
            }
        } else {
            try {
                i = Integer.parseInt(strM522f) * 100;
            } catch (Throwable unused3) {
            }
        }
        AppState.setInt(113, i);
        return 0;
    }

    /* renamed from: g */
    public static final int m946g() {
        AppState.clearIndex(1313);
        AppState.clearIndex(1279);
        AppState.clearIndex(1314);
        return 65;
    }

    /* renamed from: h */
    public static final int m947h() {
        ((MrimAccount) AppState.getAccount()).m724j();
        if (AppState.getBool(286)) {
            return AppState.getInt(1476);
        }
        ScreenBuilder.m549c();
        return 0;
    }

    /* renamed from: a */
    public static final void m948a(ResourceManager c0034e) {
        Vector vectorM614m = AppState.getVector(1398);
        synchronized (vectorM614m) {
            vectorM614m.removeElement(c0034e);
        }
    }

    /* renamed from: i */
    public static final ResourceManager m949i() {
        ResourceManager c0034e;
        Vector vectorM614m = AppState.getVector(1398);
        synchronized (vectorM614m) {
            c0034e = (ResourceManager) (vectorM614m.size() != 0 ? vectorM614m.firstElement() : null);
        }
        return c0034e;
    }

    /* renamed from: b */
    public static final void m950b(ResourceManager c0034e) {
        Vector vectorM614m = AppState.getVector(1398);
        synchronized (vectorM614m) {
            if (!vectorM614m.contains(c0034e)) {
                if (c0034e.f281a == 3) {
                    vectorM614m.addElement(c0034e);
                } else {
                    int size = vectorM614m.size();
                    while (size > 0 && ((ResourceManager) vectorM614m.elementAt(size - 1)).f281a != 1) {
                        size--;
                    }
                    vectorM614m.insertElementAt(c0034e, size);
                }
            }
        }
    }

    /* renamed from: j */
    public static final void m951j() {
        Vector vector = f289h;
        if (vector == null) {
            return;
        }
        Screen c0013amM75b = ScreenManager.m75b(4292);
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                ScreenManager.m71b(c0013amM75b);
                AppController.f153g = true;
                return;
            } else {
                MapPoint c0014an = (MapPoint) vector.elementAt(size);
                c0013amM75b.m247a(-1, c0014an.name, 6, c0014an);
            }
        }
    }

    /* renamed from: b */
    public static final int m952b(Object obj) {
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        MapPoint c0014an = (MapPoint) obj;
        c0028ba.m731a(c0014an);
        XmppContactGroup.m1043a(AppState.getVector(1400), c0014an, 0, 5);
        XmppContactGroup.m1046a(AppState.getVector(1400), 225);
        AppState.setInt(1477, 0);
        c0028ba.f232h = true;
        return 160;
    }

    /* renamed from: a */
    public static final void m953a(String str, long j, long j2) {
        new AsyncTask(19, new Object[]{str, new long[]{j, j2}});
    }

    /* renamed from: k */
    public static final void m954k() {
        AppState.clearIndex(1281);
        Screen c0013amM75b = ScreenManager.m75b(4507);
        Vector vectorM439R = AppController.m439R();
        int size = vectorM439R.size();
        if (size > 0) {
            c0013amM75b.m255a(832);
            for (int i = 0; i < size; i++) {
                c0013amM75b.m225a(((MrimAccount) vectorM439R.elementAt(i)).createMenuItem());
            }
        } else {
            c0013amM75b.f103i = false;
            c0013amM75b.m255a(551);
        }
        NetworkUtils.releaseVector(vectorM439R);
        ScreenManager.m70a(c0013amM75b);
        TabBar.m169e();
        TabBar.m176a(36, (Account) null);
    }

    /* renamed from: c */
    public static final int m955c(Object obj) {
        if (obj == null) {
            return -1;
        }
        AppState.setInt(1512, 38);
        AppState.setAccount(obj);
        return 0;
    }

    /* renamed from: d */
    public static final int m956d(int i) {
        Vector vectorM440S = AppController.m440S();
        switch (i) {
            case 0:
                if (vectorM440S.size() <= 0) {
                    return AppController.m338l(422);
                }
                ((MrimAccount) vectorM440S.firstElement()).m751a(new SearchEntry(AppController.m312l().userId, 1));
                ScreenBuilder.m549c();
                return 85;
            case 1:
                if (vectorM440S.size() <= 0) {
                    return AppController.m338l(422);
                }
                ((MrimAccount) vectorM440S.firstElement()).m751a(new SearchEntry(AppController.m312l().userId, 2));
                return 6;
            case 2:
                return AppController.m310j();
            case 3:
                return AppController.m311k();
            default:
                AppState.setInt(4895, 0);
                AppController.m393a((MrimAccount) null, AppController.m312l().userId);
                ScreenBuilder.m549c();
                return 0;
        }
    }

    /* renamed from: l */
    public static final void m957l() {
        AppState.clearRange(1360, 1364);
    }

    /* renamed from: m */
    public static final void m958m() {
        Vector vectorM1142d = ConnectionThread.m1142d();
        int size = vectorM1142d == null ? 0 : vectorM1142d.size();
        int i = size;
        if (size == 0) {
            AppController.m340m(404);
            return;
        }
        Screen c0013amM75b = ScreenManager.m75b(2075);
        while (true) {
            i--;
            if (i < 0) {
                ScreenManager.m71b(c0013amM75b);
                return;
            } else {
                Object objElementAt = vectorM1142d.elementAt(i);
                c0013amM75b.m247a(-1, ConnectionThread.m1138a(objElementAt), 6, objElementAt);
            }
        }
    }

    /* renamed from: d */
    public static final int m959d(Object obj) {
        AppState.pool[1253] = obj;
        return 0;
    }

    /* renamed from: a */
    public static final int m960a(String str, int i) {
        String strM522f = Utils.defaultStr(AppState.getString(1279));
        if (StringUtils.m3a(1060, str)) {
            int iM1233b = AppState.getCurrentContact().sendMessage(strM522f);
            if (0 != iM1233b) {
                ScreenBuilder.m549c();
                return AppController.m338l(iM1233b);
            }
            AppState.setInt(1456, 0);
            AppState.clearIndex(1279);
        } else if (StringUtils.m3a(473, str)) {
            AppState.setFromBuffer(1279, NetworkUtils.m1196e().append(AppState.getString(1280)));
        } else if (StringUtils.m3a(474, str)) {
            AppState.setObject(1280, (Object) strM522f);
            AppState.setBool(1460, true);
        } else if (StringUtils.m3a(478, str)) {
            AppState.setObject(1279, (Object) IOUtils.m825d(strM522f));
        }
        if (i == 93 || i == 123 || i == 95 || i == 94) {
            return 0;
        }
        ScreenBuilder.m549c();
        return 0;
    }

    /* renamed from: a */
    public static final ByteBuffer m961a(MmpProtocol c0033d) {
        return AppController.m464a(c0033d, 4881, (ByteBuffer) null);
    }

    /* renamed from: b */
    public static final ByteBuffer m962b(MmpProtocol c0033d) {
        return AppController.m464a(c0033d, 4882, (ByteBuffer) null);
    }

    /* renamed from: c */
    public static final ByteBuffer m963c(MmpProtocol c0033d) {
        Object[] objArr = new Object[2];
        ByteBuffer c0043nM1357m = new ByteBuffer().writeIntLE(0).writeShortBE(0).writeShortBE(1);
        int size = c0033d.groups.size();
        ByteBuffer c0043nM1357m2 = c0043nM1357m.writeShortBE((size << 1) + 4).writeShortBE(200).writeShortBE(size << 1);
        for (int i = 0; i < size; i++) {
            c0043nM1357m2.writeShortBE(((MmpContactGroup) c0033d.getGroup(i)).groupId);
        }
        objArr[0] = AppController.m464a(c0033d, 4873, c0043nM1357m2);
        objArr[1] = m967e(3);
        return c0033d.m916a(objArr);
    }

    /* renamed from: n */
    public static final int m964n() {
        try {
            if (XmppContactGroup.m1026g().length() != 0) {
                XmppContactGroup.m1027a(1055, 1060);
            } else {
                XmppContactGroup.m1027a(1060, 1055);
            }
            if (AppState.getBool(104)) {
                int iM586d = AppState.getInt(1531);
                if (Utils.abs(iM586d - AppState.getInt(1458)) > 5000) {
                    AppState.setInt(1458, iM586d);
                    int length = XmppContactGroup.m1026g().length();
                    if (length != AppState.getInt(1459) && Utils.abs(iM586d - AppState.getInt(1457)) > 10000) {
                        Contact abstractC0041lM611g = AppState.getCurrentContact();
                        if (!abstractC0041lM611g.isOnline() && !abstractC0041lM611g.hasUnread() && !abstractC0041lM611g.isOffline()) {
                            abstractC0041lM611g.account.validateContactResend(abstractC0041lM611g);
                        }
                        AppState.setInt(1457, iM586d);
                        AppState.setInt(1459, length);
                    }
                }
            }
            return 0;
        } catch (Throwable unused) {
            return 0;
        }
    }

    /* renamed from: b */
    public static final void m965b(String str) {
        try {
            AppController.m343s();
            HttpClient c0024axM631b = HttpClient.m631b((Object) str);
            if (c0024axM631b.m634a() != 200) {
                throw new Throwable();
            }
            Vector vectorM513a = Utils.m513a(new ByteBuffer(c0024axM631b).readUTFWithLen(), '\n', '\r');
            XmppContactGroup.f310a.removeAllElements();
            int size = vectorM513a.size();
            while (true) {
                size--;
                if (size < 0) {
                    NetworkUtils.releaseVector(vectorM513a);
                    HttpClient.m633a(c0024axM631b);
                    AppController.m344t();
                    return;
                } else {
                    Vector vectorM514a = Utils.m514a((String) vectorM513a.elementAt(size), '|');
                    if (vectorM514a.size() == 5) {
                        XmppContactGroup.f310a.addElement(new Object[]{vectorM514a.elementAt(0), new long[]{Long.parseLong((String) vectorM514a.elementAt(1)), Long.parseLong((String) vectorM514a.elementAt(2))}, vectorM514a.elementAt(4)});
                    }
                    NetworkUtils.releaseVector(vectorM514a);
                }
            }
        } catch (RuntimeException th) {
            HttpClient.m633a((HttpClient) null);
            AppController.m344t();
            throw th;
        } catch (Throwable th) {
            HttpClient.m633a((HttpClient) null);
            AppController.m344t();
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    public static final int m966a(Vector vector, String str, String str2) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        String str3 = AppState.emptyStr;
        String strM1221a = NetworkUtils.longToHex(8236);
        int i = 0;
        while (i < Utils.m541c(vector)) {
            stringBufferM1217h.append(i > 0 ? strM1221a : str3).append(((String[]) vector.elementAt(i))[0]);
            i++;
        }
        AppState.setObject(1352, (Object) NetworkUtils.bufToStringCached(stringBufferM1217h));
        AppState.setObject(1353, (Object) Utils.defaultStr(str));
        String str4 = AppState.emptyStr;
        AppState.setFromBuffer(1354, NetworkUtils.newStringBuffer().append(AppState.getBool(92) ? NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(93)).append('\n')) : str4).append(AppState.getBool(94) ? NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(95)).append('\n')) : str4).append(Utils.defaultStr(str2)).append(AppState.getString(874)));
        return 54;
    }

    /* renamed from: e */
    public static final Integer m967e(int i) {
        return (i & 31) == i ? f291j[i] : new Integer(i);
    }

    /* renamed from: a */
    public static final Boolean m968a(boolean z) {
        return z ? f292k : f293l;
    }

    /* renamed from: a */
    public static final int m969a(String str, Account abstractC0037h) {
        ByteBuffer c0043nM1310c;
        int iIndexOf = str.indexOf(64);
        String strM15c = StringUtils.suffix(str, iIndexOf + 1);
        Object[] objArr = new Object[3];
        if (abstractC0037h instanceof MmpProtocol) {
            c0043nM1310c = new ByteBuffer().writeCompressed(3998225).writeRawString(str);
        } else {
            ByteBuffer c0043nM1310c2 = new ByteBuffer().writeCompressed(1704439);
            int iIndexOf2 = strM15c.indexOf(46);
            c0043nM1310c = c0043nM1310c2.writeRawString(iIndexOf2 < 0 ? NetworkUtils.longToHex(6775139) : StringUtils.prefix(strM15c, iIndexOf2)).writeByte(47).writeRawString(iIndexOf < 0 ? str : StringUtils.prefix(str, iIndexOf)).writeCompressed(467 + AppState.getInt(4895));
        }
        objArr[0] = c0043nM1310c.getStringAndClear();
        objArr[1] = abstractC0037h;
        objArr[2] = null;
        AppState.pool[1271] = objArr;
        new AsyncTask(1, objArr);
        return 0;
    }

    /* renamed from: a */
    private static final void m970a(byte b) {
        AppState.getBytes(1357)[0] = b;
    }

    /* renamed from: u */
    private static final boolean m971u() {
        return AppState.getBytes(1357)[0] != 0;
    }

    /* renamed from: o */
    public static final int m972o() {
        synchronized (AppState.pool[1357]) {
            if (!m971u() && System.currentTimeMillis() > AppState.getLong(287) + 86400000) {
                AppState.setLong(287, System.currentTimeMillis());
                m970a((byte) 1);
                new AsyncTask(32);
            }
            if (m971u()) {
                return -1;
            }
            return AppState.getInt(289);
        }
    }

    /* JADX DEBUG: Finally have unexpected throw blocks count: 2, expect 1 */
    /* renamed from: p */
    public static final void m973p() {
        try {
            AppController.m343s();
            HttpClient c0024axM629a = HttpClient.m629a(AppState.getString(3607418), (Account) null, 3);
            if (c0024axM629a.m634a() != 200) {
                throw new Throwable();
            }
            ByteBuffer c0043n = new ByteBuffer(c0024axM629a);
            synchronized (AppState.pool[1357]) {
                AppState.setInt(289, Integer.parseInt(c0043n.parseXmlStr().getIntAttribute(723889)) != 0 ? 1 : 0);
            }
            synchronized (AppState.pool[1357]) {
                m970a((byte) 0);
            }
            HttpClient.m633a(c0024axM629a);
            AppController.m344t();
        } catch (Throwable unused) {
            synchronized (AppState.pool[1357]) {
                m970a((byte) 0);
                HttpClient.m633a((HttpClient) null);
                AppController.m344t();
            }
        }
    }

    /* renamed from: c */
    public static final int m974c(String str) {
        String strM584b = AppState.getString(1346);
        int iM586d = AppState.getInt(1513);
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        ChatRoom c0052wM745h = c0028ba.m745h(iM586d);
        if (StringUtils.m3a(848, str)) {
            c0052wM745h.f415g.addElement(strM584b);
            return 0;
        }
        if (StringUtils.m3a(847, str)) {
            c0052wM745h.m1417d(strM584b);
            return 0;
        }
        if (StringUtils.m3a(846, str)) {
            ScreenBuilder.m549c();
            m966a((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (StringUtils.m3a(1347, str)) {
            IOUtils.m814e(c0052wM745h.f415g);
            return 0;
        }
        if (StringUtils.m3a(1061, str)) {
            ScreenBuilder.m549c();
            AppController.m290a(false);
            return 0;
        }
        if (!StringUtils.m3a(851, str)) {
            return 0;
        }
        AppState.setInt(1514, 0);
        AppState.clearIndex(1345);
        c0028ba.f229e = true;
        c0052wM745h.m1424a(false);
        AppState.setInt(1512, 41);
        return 0;
    }

    /* renamed from: a */
    public static final String m975a(long j, long j2, int i, String str) {
        String strM1109a;
        ByteBuffer c0043nM1385u = new ByteBuffer().writeCompressed(1245774).writeUInt(1031283503);
        String strM809a = IOUtils.m809a(j);
        ByteBuffer c0043nM1385u2 = c0043nM1385u.writeRawString(strM809a).writeUInt(4028710);
        String strM810b = IOUtils.m810b(j2);
        ByteBuffer c0043nM1310c = c0043nM1385u2.writeRawString(strM810b).writeUInt(4028966).writeIntAsString(i).writeCompressed(2363459);
        if (str != null) {
            ByteBuffer c0043nM1385u3 = c0043nM1310c.writeUInt(1031302438).writeRawString(strM809a).writeUInt(1031367974).writeRawString(strM810b).writeUInt(1031040294);
            if (StringUtils.isEmpty(str)) {
                strM1109a = NetworkUtils.longToHex(1094795585);
            } else {
                ByteBuffer c0043n = new ByteBuffer();
                int length = str.length();
                for (int i2 = 0; i2 < length; i2++) {
                    int iCharAt = str.charAt(i2) & 65535;
                    if (iCharAt < 128) {
                        c0043n.writeByte(iCharAt);
                    } else if (iCharAt < 2048) {
                        c0043n.writeByte(192 + (iCharAt >> 6)).writeByte(128 + (iCharAt & 63));
                    } else {
                        c0043n.writeByte(224 + (iCharAt >> 12)).writeByte(128 + ((iCharAt >> 6) & 63)).writeByte(128 + (iCharAt & 63));
                    }
                }
                strM1109a = Conversation.m1109a(Conversation.m1109a(c0043n.toBase64(), 65547, 200765), 65552, 200768);
            }
            c0043nM1385u3.writeRawString(strM1109a);
        }
        return c0043nM1310c.getStringAndClear();
    }

    /* renamed from: q */
    public static final int m976q() {
        int iMo1396m;
        Object obj = AppState.pool[1365];
        if ((obj instanceof ContactGroup) && 0 != (iMo1396m = ((ContactGroup) obj).getSortIndex())) {
            return AppController.m338l(iMo1396m);
        }
        if (!(obj instanceof Contact)) {
            return 4;
        }
        Contact abstractC0041l = (Contact) obj;
        int iMo118b = abstractC0041l.account.validateResend(abstractC0041l);
        if (0 != iMo118b) {
            return AppController.m338l(iMo118b);
        }
        return 4;
    }

    /* renamed from: r */
    public static final void m977r() {
        int iM1346q;
        int iM1346q2;
        boolean zM587e = AppState.getBool(1505);
        Object obj = AppState.getObjectArray(1271)[0];
        if (obj instanceof Integer) {
            if (zM587e) {
                AppController.m340m(((Integer) obj).intValue());
                return;
            }
            return;
        }
        try {
            StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
            StringBuffer stringBufferM1217h2 = NetworkUtils.newStringBuffer();
            ByteBuffer c0043n = (ByteBuffer) obj;
            while (c0043n.length > 0 && 32 != (iM1346q2 = c0043n.readUByte())) {
                stringBufferM1217h.append((char) iM1346q2);
            }
            while (c0043n.length > 0 && 32 != (iM1346q = c0043n.readUByte())) {
                stringBufferM1217h2.append((char) iM1346q);
            }
            AppState.setFromBuffer(1284, stringBufferM1217h);
            AppState.setFromBuffer(1285, stringBufferM1217h2);
            if (m979e(AppState.getString(1375)) >= m979e(AppState.getString(1284))) {
                throw new Throwable();
            }
            ScreenManager.m71b(ScreenManager.m75b(3850));
        } catch (Throwable unused) {
            if (zM587e) {
                AppController.m340m(731);
            }
        }
    }

    /* renamed from: s */
    public static final int m978s() {
        AppState.setFromPool(1236, 1285);
        return 0;
    }

    /* renamed from: e */
    private static final int m979e(String str) {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            char cCharAt = str.charAt(i3);
            if (cCharAt == '.') {
                i = (i * 100) + i2;
                i2 = 0;
            } else if (cCharAt >= '0' && cCharAt <= '9') {
                i2 = ((i2 * 10) + cCharAt) - 48;
            }
        }
        return (i * 100) + i2;
    }

    /* renamed from: a */
    private static final ByteBuffer m980a(MrimAccount c0028ba) {
        return new ByteBuffer().writeRawString(c0028ba.password).encryptMD5();
    }

    /* renamed from: a */
    public static final ByteBuffer m981a(MrimAccount c0028ba, Account abstractC0037h, int i, int i2, String str, boolean z, byte[] bArr) {
        ByteBuffer c0043nM1302a = new ByteBuffer().writeIntWithLen(266).writeIntWithLen(20200).writeIntLE(i).writeIntLE(i2).writeStringLatin1(str).writeIntLE(z ? 1 : 0).writeIntLE(bArr.length).writeBytes(bArr);
        while ((c0043nM1302a.length & 7) != 0) {
            c0043nM1302a.writeByte(0);
        }
        ByteBuffer c0043n = new ByteBuffer();
        ByteBuffer c0043nM980a = m980a(c0028ba);
        XmppContactGroup.m1036a(c0043nM980a.data, c0043nM980a.length, c0043nM1302a.data, c0043nM1302a.length);
        c0043nM980a.clear();
        return c0028ba.m719a(new Object[]{AppController.m321a(c0028ba, 4132, c0043n.writeBufferIntLen(c0043nM1302a)), m967e(17), abstractC0037h});
    }

    /* renamed from: a */
    public static final void m982a(MrimAccount c0028ba, int i, Object[] objArr, ByteBuffer c0043n) {
        if (i == 1) {
            c0043n.readInt();
            c0043n.ensureCapacity(0);
            ByteBuffer c0043nM980a = m980a(c0028ba);
            XmppContactGroup.m1037b(c0043nM980a.data, c0043nM980a.length, c0043n.data, c0043n.length);
            c0043nM980a.clear();
            c0043n.readInt();
            MmpProtocol c0033d = (MmpProtocol) objArr[2];
            c0033d.trySendData(AppController.m464a(c0033d, 288, new ByteBuffer().writeShortBE(16).writeIntLE(c0043n.readInt()).writeIntLE(c0043n.readInt()).writeIntLE(c0043n.readInt()).writeIntLE(c0043n.readInt())));
        }
    }

    /* renamed from: t */
    public static final void m983t() {
        ScreenManager.m71b(ScreenManager.m75b(5141));
        AppController.m340m(1027);
    }

    /* renamed from: a */
    public static final int m984a(Screen c0013am) {
        NetworkUtils.m1195d();
        String[] strArrM518a = Utils.m518a(true);
        Vector vectorM794a = IOUtils.m794a(c0013am, 1);
        int length = strArrM518a.length;
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            vectorM794a.addElement(strArrM518a[length]);
        }
        if (vectorM794a.size() == 0) {
            return AppController.m338l(775);
        }
        vectorM794a.addElement(((MrimAccount) AppState.getAccount()).login);
        AppState.pool[1284] = vectorM794a;
        return 179;
    }

    /* renamed from: g */
    private static final int m985g(int i) {
        if (i >= 65 && i <= 90) {
            return i - 65;
        }
        if (i >= 97 && i <= 122) {
            return i - 71;
        }
        if (i >= 48 && i <= 57) {
            return i + 4;
        }
        if (i == 43) {
            return 62;
        }
        if (i == 47) {
            return 63;
        }
        throw new RuntimeException();
    }

    /* renamed from: d */
    public static final ByteBuffer m986d(String str) {
        int i;
        char cCharAt;
        char cCharAt2;
        char cCharAt3;
        char cCharAt4;
        ByteBuffer c0043n = new ByteBuffer();
        int length = str.length();
        int i2 = 0;
        while (i2 < length) {
            int iM985g = 0;
            int iM985g2 = 0;
            int iM985g3 = 0;
            int iM985g4 = 0;
            while (true) {
                try {
                    int i3 = i2;
                    i2++;
                    cCharAt = str.charAt(i3);
                    if (cCharAt != '\n' && cCharAt != '\r') {
                        break;
                    }
                } catch (Throwable unused) {
                    i = 0 - 1;
                    i2 = length;
                }
            }
            iM985g = m985g(cCharAt);
            int i4 = 0 + 1;
            while (true) {
                int i5 = i2;
                i2++;
                cCharAt2 = str.charAt(i5);
                if (cCharAt2 != '\n' && cCharAt2 != '\r') {
                    break;
                }
            }
            iM985g2 = m985g(cCharAt2);
            int i6 = i4 + 1;
            while (true) {
                int i7 = i2;
                i2++;
                cCharAt3 = str.charAt(i7);
                if (cCharAt3 != '\n' && cCharAt3 != '\r') {
                    break;
                }
            }
            iM985g3 = m985g(cCharAt3);
            int i8 = i6 + 1;
            while (true) {
                int i9 = i2;
                i2++;
                cCharAt4 = str.charAt(i9);
                if (cCharAt4 != '\n' && cCharAt4 != '\r') {
                    break;
                }
            }
            iM985g4 = m985g(cCharAt4);
            i = i8 + 1;
            if (i > 0) {
                c0043n.writeByte((iM985g << 2) | (iM985g2 >> 4));
            }
            if (i > 1) {
                c0043n.writeByte((iM985g2 << 4) | (iM985g3 >> 2));
            }
            if (i > 2) {
                c0043n.writeByte((iM985g3 << 6) | iM985g4);
            }
        }
        return c0043n;
    }

    /* renamed from: a */
    public static final ByteBuffer m987a(MrimAccount c0028ba, MrimContact c0035f, int i) {
        return c0028ba.m719a(new Object[]{AppController.m321a(c0028ba, 4123, new ByteBuffer().writeIntLE(c0035f.f294a).writeIntLE(i).writeIntLE(c0035f.f296c).writeStringLatin1(c0035f.f297d).writeStringUTF16(c0035f.displayName).writeStringLatin1(c0035f.f300g)), m967e(11), c0035f, m967e(i)});
    }

    /* renamed from: a */
    public static final ByteBuffer m988a(MrimAccount c0028ba, MrimContact c0035f, MrimContactGroup c0010aj) {
        return c0028ba.m719a(new Object[]{AppController.m321a(c0028ba, 4123, new ByteBuffer().writeIntLE(c0035f.f294a).writeIntLE(c0035f.f295b).writeIntLE(c0010aj.serverId).writeStringLatin1(c0035f.f297d).writeStringUTF16(c0035f.displayName).writeStringLatin1(c0035f.f300g)), m967e(12), c0035f, c0010aj});
    }
}
