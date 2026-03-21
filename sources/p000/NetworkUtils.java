package p000;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

/* renamed from: k */
/* loaded from: MobileAgent_3.9.jar:k.class */
public final class NetworkUtils {

    /* renamed from: a */
    public final int type;

    /* renamed from: b */
    public final int port;

    /* renamed from: c */
    public final String host;

    /* renamed from: d */
    public String url;

    /* renamed from: e */
    public final int status;

    /* renamed from: f */
    public final String protocol;

    /* renamed from: g */
    public static byte[][] bytePool;

    /* renamed from: h */
    public static StringBuffer[] bufferPool;

    /* renamed from: i */
    public static Vector[] vectorPool;

    /* renamed from: j */
    public static Hashtable stringCache;

    public NetworkUtils(ByteBuffer c0043n) {
        this.type = c0043n.readIntBE();
        this.port = c0043n.readIntBE();
        this.host = c0043n.readUTF8Str((String) null);
        this.url = c0043n.readWideStr();
        this.status = c0043n.readIntBE();
        this.protocol = c0043n.readWideStr();
    }

    public NetworkUtils(int i, String str, int i2, String str2) {
        this.type = 1;
        this.port = i;
        this.host = str;
        this.url = null;
        this.status = i2;
        this.protocol = str2;
    }

    /* renamed from: a */
    public static final void m1174a() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis > AppState.getLong(274) + 7776000000L) {
            AppState.setLong(274, jCurrentTimeMillis);
            new AsyncTask(18);
        }
    }

    /* renamed from: a */
    public static final void m1175a(String str) {
        HttpClient c0024ax;
        byte[] bArr;
        int i;
        int i2;
        byte b;
        byte[] bArr2;
        int i3;
        int i4;
        byte b2;
        int i5;
        byte[] bArrM1211a = newBytes(3000);
        try {
            Thread.sleep(1000L);
            System.gc();
            Thread.sleep(1000L);
            AppController.m343s();
            if (str == null) {
                HttpClient c0024axM631b = HttpClient.createWithType2((Object) new ByteBuffer().writeCompressed(1442705).writeCompressed(524308).writeCompressed(720924).getStringAndClear());
                c0024ax = c0024axM631b;
                if (c0024axM631b.getResponseCode() == 200) {
                    Vector vector = new ByteBuffer(c0024ax).parseXmlStr().children;
                    XmlElement c0022avM560b = new XmlElement(103).setLongKeyAttr(103, AppState.getString(223)).setLongKeyAttr(102, AppController.m298f()).setLongKeyAttr(116, StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory()))).setLongKeyAttr(112, StringUtils.intern(Integer.toString(0))).setLongKeyAttr(115, StringUtils.intern(ResourceManager.booleanOf(false).toString()));
                    for (int i6 = 0; i6 < vector.size(); i6++) {
                        XmlElement c0022av = (XmlElement) vector.elementAt(i6);
                        String str2 = c0022av.tagName;
                        String strM11a = StringUtils.fromBuffer(c0022av.textContent);
                        if (m1177a(str2, 'p')) {
                            c0022avM560b.addChild(m1176a('p', strM11a, m1179d(strM11a)));
                        } else if (m1177a(str2, 'j')) {
                            c0022avM560b.addChild(m1176a('j', strM11a, m1180e(strM11a)));
                        } else if (m1177a(str2, 'e')) {
                            c0022avM560b.addChild(m1176a('e', strM11a, (Object) m1178c(strM11a)));
                        }
                    }
                    new AsyncTask(18, c0022avM560b.toString());
                }
            } else {
                ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(131082);
                ByteBuffer c0043nM1377k = new ByteBuffer().writeUTFNoLen(str);
                for (int i7 = 0; i7 < c0043nM1377k.length; i7 += 600) {
                    int i8 = i7;
                    int iM503b = Utils.min(i8 + 600, c0043nM1377k.length);
                    byte[] bArrM581a = AppState.getBytes(961);
                    int i9 = 0;
                    boolean z = true;
                    while (z) {
                        int i10 = 0;
                        int i11 = 0;
                        int i12 = 0;
                        int i13 = 0;
                        if (i8 < iM503b) {
                            int i14 = i8;
                            i8++;
                            i10 = c0043nM1377k.data[i14] & 255;
                            i13 = 0 + 1;
                        }
                        if (i8 < iM503b) {
                            int i15 = i8;
                            i8++;
                            i11 = c0043nM1377k.data[i15] & 255;
                            i13++;
                        }
                        if (i8 < iM503b) {
                            int i16 = i8;
                            i8++;
                            i12 = c0043nM1377k.data[i16] & 255;
                            i13++;
                        } else {
                            z = false;
                        }
                        if (i13 > 0) {
                            int i17 = (i10 << 16) | (i11 << 8) | i12;
                            int i18 = (i17 >> 18) & 63;
                            if (i18 < 62) {
                                bArr = bArrM1211a;
                                i = i9;
                                i2 = i9 + 1;
                                b = bArrM581a[i18];
                            } else {
                                int i19 = i9;
                                int i20 = i9 + 1;
                                bArrM1211a[i19] = 37;
                                int i21 = i20 + 1;
                                bArrM1211a[i20] = 50;
                                bArr = bArrM1211a;
                                i = i21;
                                i2 = i21 + 1;
                                b = i18 == 62 ? (byte) 66 : (byte) 70;
                            }
                            bArr[i] = b;
                            int i22 = (i17 >> 12) & 63;
                            if (i22 < 62) {
                                bArr2 = bArrM1211a;
                                i3 = i2;
                                i4 = i2 + 1;
                                b2 = bArrM581a[i22];
                            } else {
                                int i23 = i2;
                                int i24 = i2 + 1;
                                bArrM1211a[i23] = 37;
                                int i25 = i24 + 1;
                                bArrM1211a[i24] = 50;
                                bArr2 = bArrM1211a;
                                i3 = i25;
                                i4 = i25 + 1;
                                b2 = i22 == 62 ? (byte) 66 : (byte) 70;
                            }
                            bArr2[i3] = b2;
                            if (i13 > 1) {
                                int i26 = (i17 >> 6) & 63;
                                if (i26 < 62) {
                                    int i27 = i4;
                                    i5 = i4 + 1;
                                    bArrM1211a[i27] = bArrM581a[i26];
                                } else {
                                    int i28 = i4;
                                    int i29 = i4 + 1;
                                    bArrM1211a[i28] = 37;
                                    int i30 = i29 + 1;
                                    bArrM1211a[i29] = 50;
                                    i5 = i30 + 1;
                                    bArrM1211a[i30] = i26 == 62 ? (byte) 66 : (byte) 70;
                                }
                            } else {
                                int i31 = i4;
                                int i32 = i4 + 1;
                                bArrM1211a[i31] = 37;
                                int i33 = i32 + 1;
                                bArrM1211a[i32] = 51;
                                i5 = i33 + 1;
                                bArrM1211a[i33] = 68;
                            }
                            if (i13 > 2) {
                                int i34 = i17 & 63;
                                if (i34 < 62) {
                                    int i35 = i5;
                                    i9 = i5 + 1;
                                    bArrM1211a[i35] = bArrM581a[i34];
                                } else {
                                    int i36 = i5;
                                    int i37 = i5 + 1;
                                    bArrM1211a[i36] = 37;
                                    int i38 = i37 + 1;
                                    bArrM1211a[i37] = 50;
                                    i9 = i38 + 1;
                                    bArrM1211a[i38] = i34 == 62 ? (byte) 66 : (byte) 70;
                                }
                            } else {
                                int i39 = i5;
                                int i40 = i5 + 1;
                                bArrM1211a[i39] = 37;
                                int i41 = i40 + 1;
                                bArrM1211a[i40] = 51;
                                i9 = i41 + 1;
                                bArrM1211a[i41] = 68;
                            }
                        }
                    }
                    c0043nM1310c.writeBytesAt(bArrM1211a, 0, i9);
                }
                c0043nM1377k.clear();
                HttpClient c0024axM632a = HttpClient.createMockClient(new ByteBuffer().writeCompressed(1311655).writeCompressed(524300).writeCompressed(720924).getStringAndClear());
                c0024ax = c0024axM632a;
                c0024axM632a.sendHttpRequest(c0043nM1310c.length, 1414745936, 1038).writeBuffer(c0043nM1310c).getResponseCode();
            }
            HttpClient.closeAndUpdateStats(c0024ax);
            AppController.m344t();
            releaseBytes(bArrM1211a);
        } catch (RuntimeException th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            AppController.m344t();
            releaseBytes(bArrM1211a);
            throw th;
        } catch (Throwable th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            AppController.m344t();
            releaseBytes(bArrM1211a);
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    private static final XmlElement m1176a(char c, String str, Object obj) {
        return new XmlElement(c).setLongKeyAttr(110, str).appendText(obj);
    }

    /* renamed from: a */
    private static final boolean m1177a(String str, char c) {
        return str.charAt(0) == c;
    }

    /* renamed from: c */
    private static final Boolean m1178c(String str) {
        try {
            Class.forName(str);
            return ResourceManager.boolTrue;
        } catch (Throwable unused) {
            return ResourceManager.boolFalse;
        }
    }

    /* renamed from: d */
    private static final Object m1179d(String str) {
        String strM17c = null;
        try {
            strM17c = StringUtils.intern(System.getProperty(str));
            return strM17c;
        } catch (Throwable th) {
            return strM17c;
        }
    }

    /* renamed from: e */
    private static final Object m1180e(String str) {
        String strM17c = null;
        try {
            strM17c = StringUtils.intern(AppState.getMidlet().getAppProperty(str));
            return strM17c;
        } catch (Throwable th) {
            return strM17c;
        }
    }

    /* renamed from: a */
    public static final int m1181a(Object[] objArr) {
        AppState.clearIndex(1271);
        String str = (String) objArr[20];
        if (str != null) {
            if (Utils.parseInt((Object) str) == 0) {
                String str2 = (String) objArr[7];
                int iM437a = AppController.m437a(0, (Account) null, str2, (String) objArr[9]);
                if (0 != iM437a) {
                    return AppController.m338l(iM437a);
                }
                AppController.m328a(AppController.m438b(0, str2));
                return 4;
            }
            if (Utils.parseInt((Object) str) == 4004) {
                objArr[21] = ResourceManager.integerOf(-1);
            }
        }
        AppState.pool[1271] = objArr;
        return 164;
    }

    /* renamed from: b */
    public static final void m1182b() {
        String strM15c;
        Object[] objArr = (Object[]) AppState.pool[1271];
        AppState.clearIndex(1271);
        String str = (String) objArr[7];
        String strM13b = str;
        int iIndexOf = str.indexOf(64);
        if (iIndexOf >= 0) {
            strM15c = StringUtils.suffix(strM13b, iIndexOf);
            strM13b = StringUtils.prefix(strM13b, iIndexOf);
        } else {
            strM15c = AppState.emptyStr;
        }
        int i = 0;
        Vector vectorM516c = Utils.m516c(AppState.getString(694), (char) 0);
        int size = vectorM516c.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (StringUtils.m4a(strM15c, vectorM516c.elementAt(size))) {
                i = size;
            }
        }
        releaseVector(vectorM516c);
        AppState.pool[1341] = objArr[3];
        AppState.pool[1342] = objArr[4];
        AppState.pool[1343] = objArr[5];
        AppState.pool[1292] = strM13b;
        AppState.setInt(1474, i);
        AppState.pool[1293] = objArr[9];
        AppState.pool[1284] = objArr[10];
        AppState.setInt(4305, ((Integer) objArr[11]).intValue());
        AppState.pool[1287] = objArr[12];
        AppState.pool[1288] = objArr[13];
        AppState.pool[1298] = objArr[14];
        AppState.pool[1299] = objArr[15];
        AppState.setInt(1489, ((Integer) objArr[16]).intValue());
        AppState.setInt(1488, ((Integer) objArr[17]).intValue());
        AppState.setInt(1491, ((Integer) objArr[18]).intValue());
        AppState.setInt(1481, ((Integer) objArr[19]).intValue());
        AppState.pool[1297] = objArr[20];
        AppState.setInt(1480, ((Integer) objArr[21]).intValue());
        ScreenManager.showScreen(ScreenManager.createScreen(4399));
        String strM584b = AppState.getString(1297);
        if (strM584b == null) {
            AppController.m334q();
            return;
        }
        int iM510a = Utils.parseInt((Object) strM584b);
        int i2 = iM510a == 78 ? 818 : iM510a == 101 ? 819 : iM510a == 114 ? 820 : iM510a == 150 ? 821 : iM510a == 152 ? 822 : iM510a == 154 ? 823 : iM510a == 155 ? 824 : iM510a == 175 ? 825 : iM510a == 555 ? 826 : iM510a == 573 ? 827 : iM510a == 4003 ? 828 : iM510a == 4004 ? 829 : iM510a == 5005 ? 830 : 831;
        int i3 = i2;
        String strM584b2 = AppState.getString(i2);
        AppController.m339e(i3 != 831 ? strM584b2 : new StringBuffer().append(strM584b2).append(iM510a).toString());
    }

    /* renamed from: c */
    public static final void m1183c() {
        Vector vectorM614m = AppState.getVector(1373);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                m1185a((Object[]) vectorM614m.elementAt(size), true);
            }
        }
    }

    /* renamed from: b */
    public static final void m1184b(Object[] objArr) {
        m1185a(objArr, false);
    }

    /* renamed from: a */
    private static final void m1185a(Object[] objArr, boolean z) {
        if (objArr != null) {
            IOUtils.closeInput((InputStream) objArr[1]);
            IOUtils.closeOutput((OutputStream) objArr[2]);
            Connection connection = (Connection) objArr[0];
            if (connection == null || z) {
                IOUtils.closeConn(connection);
            } else {
                new AsyncTask(7, connection);
            }
            objArr[0] = null;
            objArr[1] = null;
            objArr[2] = null;
            Utils.removeFrom(AppState.getVector(1373), objArr);
        }
    }

    /* renamed from: a */
    public static final Object[] m1186a(String str, boolean z) throws IOException {
        Object[] objArr = new Object[z ? 5 : 3];
        try {
            SocketConnection socketConnection = (SocketConnection) IOUtils.registerResource((Object) Connector.open(str, 3));
            objArr[0] = socketConnection;
            try {
                if (socketConnection instanceof SocketConnection) {
                    byte b = 5;
                    while (true) {
                        byte b2 = (byte) (b - 1);
                        b = b2;
                        if (b2 < 2) {
                            break;
                        }
                        SocketConnection socketConnection2 = socketConnection;
                        try {
                            int iM586d = AppState.getInt(b + 107);
                            if (iM586d >= 0) {
                                socketConnection2.setSocketOption(b, iM586d);
                            }
                        } catch (Throwable unused) {
                        }
                    }
                }
            } catch (Throwable unused2) {
            }
            objArr[1] = IOUtils.registerResource((Object) socketConnection.openInputStream());
            objArr[2] = IOUtils.registerResource((Object) socketConnection.openOutputStream());
            if (z) {
                objArr[4] = new ByteBuffer();
                new AsyncTask(4, objArr);
            }
            AppState.getVector(1373).addElement(objArr);
            return objArr;
        } catch (Throwable th) {
            m1185a(objArr, true);
            throw th;
        }
    }

    /* renamed from: f */
    private static boolean m1187f(Object[] objArr) {
        return objArr.length > 3;
    }

    /* renamed from: c */
    public static final int m1188c(Object[] objArr) throws IOException {
        if (!m1187f(objArr)) {
            return ((InputStream) objArr[1]).available();
        }
        synchronized (objArr) {
            int i = ((ByteBuffer) objArr[4]).length;
            if (i > 0) {
                return i;
            }
            Throwable th = (Throwable) objArr[3];
            if (th != null) {
                if (th instanceof IOException) throw (IOException) th;
                if (th instanceof RuntimeException) throw (RuntimeException) th;
                if (th instanceof Error) throw (Error) th;
                throw new RuntimeException(th);
            }
            return 0;
        }
    }

    /* renamed from: a */
    public static final void m1189a(Object[] objArr, byte[] bArr, int i) throws IOException {
        ((OutputStream) objArr[2]).write(bArr, 0, i);
        ((OutputStream) objArr[2]).flush();
    }

    /* renamed from: a */
    public static final int m1190a(Object[] objArr, byte[] bArr, int i, int i2) throws IOException {
        if (!m1187f(objArr)) {
            return ((InputStream) objArr[1]).read(bArr, i, i2);
        }
        synchronized (objArr) {
            ((ByteBuffer) objArr[4]).readInto(bArr, i, i2);
        }
        return i2;
    }

    /* renamed from: a */
    private static final int m1191a(Object[] objArr, byte[] bArr) throws IOException {
        long jCurrentTimeMillis = System.currentTimeMillis();
        try {
            return ((InputStream) objArr[1]).read(bArr);
        } catch (IOException th) {
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            if (jCurrentTimeMillis2 >= 50000 && jCurrentTimeMillis2 <= 70000) {
                return 0;
            }
            throw th;
        } catch (RuntimeException th) {
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            if (jCurrentTimeMillis2 >= 50000 && jCurrentTimeMillis2 <= 70000) {
                return 0;
            }
            throw th;
        } catch (Throwable th) {
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            if (jCurrentTimeMillis2 >= 50000 && jCurrentTimeMillis2 <= 70000) {
                return 0;
            }
            throw new RuntimeException(th);
        }
    }

    /* renamed from: d */
    public static final void m1192d(Object[] objArr) {
        int iM1191a;
        byte[] bArr = new byte[1024];
        do {
            try {
                iM1191a = m1191a(objArr, bArr);
                if (iM1191a > 0) {
                    synchronized (objArr) {
                        ((ByteBuffer) objArr[4]).writeBytesAt(bArr, 0, iM1191a);
                    }
                }
                if (iM1191a < 1024) {
                    Thread.sleep(100L);
                }
            } catch (Throwable th) {
                try {
                    Thread.sleep(3000);
                } catch (Throwable unused) {
                }
                objArr[3] = th;
                releaseBytes(bArr);
                return;
            }
        } while (iM1191a >= 0);
        throw new RuntimeException(new EOFException());
    }

    /* renamed from: a */
    public static final Screen m1193a(Screen c0013am, Vector vector) {
        MenuItem c0032cM1057D;
        int iM541c = Utils.m541c(vector);
        for (int i = 0; i < iM541c; i++) {
            Object objElementAt = vector.elementAt(i);
            if (objElementAt instanceof Contact) {
                c0032cM1057D = ((Contact) objElementAt).createMenuItem();
            } else if (objElementAt instanceof ContactGroup) {
                c0032cM1057D = ((ContactGroup) objElementAt).createMenuItem(-1);
            } else if (objElementAt instanceof ContactInfo) {
                ContactInfo c0042m = (ContactInfo) objElementAt;
                if (c0042m.getAccount() instanceof MrimAccount) {
                    MenuItem c0032cM898b = MenuItem.createDefault().setIcon(AppController.m349a(Utils.m511a(c0042m.getString(10), 0, 4, 0), c0042m.getString(12))).addText(Utils.m495b(c0042m.getDisplayName()), 1, 0).setLabel(c0042m.getString(3));
                    c0032cM898b.data = c0042m;
                    c0032cM1057D = c0032cM898b;
                } else {
                    MenuItem c0032cM886c = MenuItem.createDefault();
                    int iM510a = Utils.parseInt((Object) c0042m.getString(61));
                    MenuItem c0032cM898b2 = c0032cM886c.setIcon(iM510a == 0 ? 255 : iM510a == 1 ? 256 : 263).setLabel(Utils.m527g(c0042m.getString(60))).addText(Utils.m495b(c0042m.getDisplayName()), 1, 0).setLabel(StringUtils.concat(Utils.m527g(c0042m.getFirstName()), c0042m.getLastName()));
                    c0032cM898b2.data = c0042m;
                    c0032cM1057D = c0032cM898b2;
                }
            } else {
                c0032cM1057D = ((Account) objElementAt).createMenuItem();
            }
            c0013am.addItem(c0032cM1057D);
        }
        return c0013am;
    }

    /* renamed from: a */
    private static final int m1194a(int i, Object obj) {
        int i2;
        int i3 = i + 1;
        switch (AppState.getInt(i)) {
            case 1:
                i3 += 2;
                break;
            case 2:
                AppState.setBool(AppState.getInt(i3 + 1), ((Boolean) obj).booleanValue());
                i3 += 2;
                break;
            case 3:
                AppState.setIntInd(i3 + 2, ((Integer) ((Object[]) obj)[0]).intValue());
                i3 += 3;
                break;
            case 4:
                i3++;
                break;
            case 5:
                int i4 = i3 + 3;
                String str = (String) ((Object[]) obj)[0];
                int i5 = i4 + 1;
                if (AppState.getInt(i4) == 2) {
                    int i6 = i5 + 1;
                    int iM586d = AppState.getInt(i5);
                    int i7 = i6 + 1;
                    int iM586d2 = AppState.getInt(i6);
                    int i8 = i7 + 1;
                    int iM511a = Utils.m511a(str, iM586d, iM586d2, AppState.getInt(i7));
                    i2 = i8 + 1;
                    AppState.setIntInd(i8, iM511a);
                } else {
                    i2 = i5 + 1;
                    AppState.setStringInd(i5, str);
                }
                i3 += i2 - i3;
                break;
            case 6:
                i3++;
                break;
            case 7:
            case 8:
                i3 += 3;
                break;
            case 9:
                AppState.setStringInd(i3 + 1, ((String[]) obj)[1]);
                i3 += 2;
                break;
            case 10:
                AppState.setStringInd(i3, (String) obj);
                i3++;
                break;
            case 11:
                i3++;
                break;
            case 12:
                m1194a(AppState.getInt(i3), obj);
                i3++;
                break;
        }
        return i3;
    }

    /* renamed from: d */
    public static final int m1195d() {
        Screen c0013amM66b = ScreenManager.getCurrentScreen();
        int i = c0013amM66b.screenFlags + 9;
        Vector vector = c0013amM66b.menuItems;
        int iM1194a = i + 1;
        int iM586d = AppState.getInt(i);
        for (int i2 = 0; i2 < iM586d; i2++) {
            iM1194a = m1194a(iM1194a, ((MenuItem) vector.elementAt(i2)).data);
        }
        return 0;
    }

    /* renamed from: e */
    public static final StringBuffer m1196e() {
        StringBuffer stringBufferM1217h = newStringBuffer();
        String strM522f = Utils.defaultStr(AppState.getString(1279));
        StringBuffer stringBufferAppend = stringBufferM1217h.append(strM522f);
        int length = strM522f.length();
        if (length != 0 && strM522f.charAt(length - 1) != ' ') {
            stringBufferAppend.append(' ');
        }
        return stringBufferAppend;
    }

    /* renamed from: a */
    public static final void m1197a(int i, StringBuffer stringBuffer) {
        AppState.setInt(4486, i);
        AppState.setFromBuffer(1344, stringBuffer);
        ScreenManager.showScreen(ScreenManager.createScreen(4485));
        AppState.clearIndex(1344);
    }

    /* renamed from: a */
    public static final void m1198a(int i, int i2) {
        AppState.setInt(4486, i);
        AppState.setFromPool(1344, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(4485));
        AppState.clearIndex(1344);
    }

    /* renamed from: a */
    public static final void m1199a(int i, int i2, int i3) {
        if (i3 != 0) {
            AppController.m340m(i3);
        } else {
            m1200b(i, i2);
        }
    }

    /* renamed from: b */
    public static final void m1200b(int i, int i2) {
        AppState.setInt(4498, i);
        AppState.setInt(4497, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(4497));
    }

    /* renamed from: f */
    public static final int m1201f() {
        return AppState.getBool(69) ? 10 : 55;
    }

    /* renamed from: a */
    public static final void m1202a(MrimAccount c0028ba, int i, ByteBuffer c0043n) {
        ContactInfo c0042mM1251a = ContactInfo.createForAccount(c0028ba);
        switch (i) {
            case 0:
                c0042mM1251a.setContactName(AppState.getString(913));
                break;
            case 1:
                c0042mM1251a = (ContactInfo) m1206a(c0028ba, c0043n).elementAt(0);
                break;
            default:
                c0042mM1251a.setContactName(bufToStringCached(newStringBuffer().append(AppState.getString(914)).append(i)));
                break;
        }
        AppState.pool[1315] = c0042mM1251a;
    }

    /* renamed from: b */
    public static final void m1203b(MrimAccount c0028ba, int i, ByteBuffer c0043n) {
        int i2 = 0;
        Vector vectorM1206a = null;
        switch (i) {
            case 0:
                i2 = 913;
                break;
            case 1:
                vectorM1206a = m1206a(c0028ba, c0043n);
                break;
            default:
                i2 = 914;
                break;
        }
        AppState.setInt(1506, i2);
        AppState.pool[1318] = vectorM1206a;
    }

    /* renamed from: c */
    public static final void m1204c(MrimAccount c0028ba, int i, ByteBuffer c0043n) {
        int i2;
        switch (i) {
            case 0:
                i2 = 913;
                break;
            case 1:
                ContactInfo c0042m = (ContactInfo) m1206a(c0028ba, c0043n).elementAt(0);
                MrimContact c0035f = (MrimContact) c0028ba.contactMap.get(c0042m.getEmailOrMmpId());
                if (null != c0035f) {
                    c0035f.setDisplayName(c0042m.getFullName());
                    return;
                }
                return;
            default:
                i2 = 914;
                break;
        }
        IOUtils.postEvent((Object) AppState.getString(i2));
    }

    /* renamed from: d */
    public static final void m1205d(MrimAccount c0028ba, int i, ByteBuffer c0043n) {
        if (i == 1) {
            ContactInfo c0042m = (ContactInfo) m1206a(c0028ba, c0043n).elementAt(0);
            Hashtable hashtable = c0028ba.contactMap;
            String strM1290i = c0042m.getEmailOrMmpId();
            MrimContact c0035f = (MrimContact) hashtable.get(strM1290i);
            if (null != c0035f) {
                String strM1292k = c0042m.getFullName();
                c0035f.setDisplayName(strM1292k);
                c0028ba.validateGroupAdd(strM1290i, strM1292k, AppState.getString(741), (ContactGroup) c0028ba.getFirstContactGroup(), true);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x0106 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0114 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0123 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0132 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0141 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0173 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0181 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x018f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x019e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x01ac A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01b5 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x01be A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01cc A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01da A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01e8 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01f5 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0203 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0212 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00f8 A[SYNTHETIC] */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static final Vector m1206a(MrimAccount c0028ba, ByteBuffer c0043n) {
        Vector vectorM1213g = newVector();
        Vector vectorM512e = Utils.splitByNull(AppState.getString(915));
        int iM1328e = c0043n.readInt();
        int iM1328e2 = c0043n.readInt();
        c0043n.readInt();
        Vector vectorM1213g2 = newVector();
        for (int i = 0; i < iM1328e; i++) {
            vectorM1213g2.addElement(c0043n.readHexStr());
        }
        for (int i2 = 0; i2 < iM1328e2 && c0043n.length > 0; i2++) {
            ContactInfo c0042mM1251a = ContactInfo.createForAccount(c0028ba);
            vectorM1213g.addElement(c0042mM1251a);
            int i3 = 0;
            while (i3 < iM1328e) {
                int i4 = i3;
                i3++;
                String str = (String) vectorM1213g2.elementAt(i4);
                int iM541c = Utils.m541c(vectorM512e);
                do {
                    iM541c--;
                    if (iM541c < 0) {
                    }
                    switch (iM541c) {
                        case 0:
                            c0042mM1251a.setPhoneHome(c0043n.readWideStr());
                            break;
                        case 1:
                            c0042mM1251a.setPhoneMobile(c0043n.readWideStr());
                            break;
                        case 2:
                            c0042mM1251a.setDisplayName(c0043n.readUTF8Str((String) null));
                            break;
                        case 3:
                            c0042mM1251a.setFirstName(c0043n.readUTF8Str((String) null));
                            break;
                        case 4:
                            c0042mM1251a.setLastName(c0043n.readUTF8Str((String) null));
                            break;
                        case 5:
                            int iM511a = Utils.m511a(c0043n.readWideStr(), 1, 2, 0);
                            if (1 == iM511a) {
                                c0042mM1251a.setMaritalMarried();
                                break;
                            } else if (2 == iM511a) {
                                c0042mM1251a.setMaritalSingle();
                                break;
                            } else {
                                break;
                            }
                        case 6:
                            c0042mM1251a.setCompany(c0043n.readWideStr());
                            break;
                        case 7:
                            c0042mM1251a.setWebsite(c0043n.readWideStr());
                            break;
                        case 8:
                            c0042mM1251a.setWorkPhone(c0043n.readUTF8Str((String) null));
                            break;
                        case 9:
                            c0042mM1251a.setBirthdayMonth(c0043n.readWideStr());
                            break;
                        case 10:
                            c0043n.readWideStr();
                            break;
                        case 11:
                            c0043n.readWideStr();
                            break;
                        case 12:
                            c0042mM1251a.setAltEmail(c0043n.readWideStr());
                            break;
                        case 13:
                            c0042mM1251a.setJobTitle(c0043n.readWideStr());
                            break;
                        case 14:
                            c0042mM1251a.setLocation(c0043n.readWideStr());
                            break;
                        case 15:
                            c0042mM1251a.setIconId(c0043n.readWideStr());
                            break;
                        case 16:
                            c0042mM1251a.setIconName(c0043n.readUTF8Str((String) null));
                            break;
                        case 17:
                            c0042mM1251a.setDescription(c0043n.readUTF8Str((String) null));
                            break;
                        default:
                            c0043n.readWideStr();
                            break;
                    }
                } while (!StringUtils.equals(str, (String) vectorM512e.elementAt(iM541c)));
                switch (iM541c) {
                }
            }
            c0042mM1251a.setEmailAddress(bufToStringCached(newStringBuffer().append(c0042mM1251a.getString(50)).append('@').append(c0042mM1251a.getString(51))));
        }
        releaseVector(vectorM512e);
        releaseVector(vectorM1213g2);
        return vectorM1213g;
    }

    /* renamed from: f */
    private static String m1207f(String str) {
        String str2 = (String) stringCache.get(str);
        return str2 != null ? str2 : StringUtils.intern(str);
    }

    /* renamed from: b */
    public static final void m1208b(String str) {
        stringCache.put(str, str);
    }

    /* renamed from: a */
    public static final void releaseBytes(byte[] bArr) {
        if (bArr == null || bArr.length > 2048 || bArr.length <= 8) {
            return;
        }
        byte[][] bArr2 = bytePool;
        synchronized (bArr2) {
            int i = 0;
            while (i < 20) {
                if (bArr2[i] == null) {
                    break;
                } else {
                    i++;
                }
            }
            if (i == 20) {
                Utils.arraycopy(bArr2, 1, bArr2, 0, 19);
                i--;
            }
            bArr2[i] = bArr;
        }
    }

    /* renamed from: a */
    public static final byte[] allocBytes(byte[] bArr, int i) {
        int length;
        if (i > 2048) {
            return null;
        }
        byte[][] bArr2 = bytePool;
        synchronized (bArr2) {
            byte[] bArr3 = null;
            int i2 = Integer.MAX_VALUE;
            int i3 = 0;
            for (int i4 = 0; i4 < 20; i4++) {
                byte[] bArr4 = bArr2[i4];
                if (bArr4 != null && (length = bArr4.length) >= i && length < i2) {
                    bArr3 = bArr4;
                    i2 = length;
                    i3 = i4;
                }
            }
            if (bArr3 == null) {
                return null;
            }
            Utils.arraycopy((Object) bArr, 0, (Object) bArr3, 0, i);
            if (i3 != 19) {
                Utils.arraycopy(bArr2, i3 + 1, bArr2, i3, 19 - i3);
            }
            bArr2[19] = null;
            releaseBytes(bArr);
            return bArr3;
        }
    }

    /* renamed from: a */
    public static final byte[] newBytes(int i) {
        if (i > 2048) {
            return new byte[i];
        }
        byte[][] bArr = bytePool;
        synchronized (bArr) {
            for (int i2 = 0; i2 < 20; i2++) {
                byte[] bArr2 = bArr[i2];
                if (bArr2 != null && bArr2.length >= i) {
                    int length = bArr2.length;
                    while (true) {
                        length--;
                        if (length < 0) {
                            Utils.arraycopy(bArr, i2 + 1, bArr, i2, 19 - i2);
                            bArr[19] = null;
                            return bArr2;
                        }
                        bArr2[length] = 0;
                    }
                }
            }
            return new byte[i];
        }
    }

    /* renamed from: a */
    public static final void releaseVector(Vector vector) {
        if (vector != null) {
            vector.removeAllElements();
            Utils.m526b(vector);
            Vector[] vectorArr = vectorPool;
            synchronized (vectorArr) {
                for (int i = 0; i < 5; i++) {
                    if (vectorArr[i] == null) {
                        vectorArr[i] = vector;
                        return;
                    }
                }
            }
        }
    }

    /* renamed from: g */
    public static final Vector newVector() {
        Vector[] vectorArr = vectorPool;
        synchronized (vectorArr) {
            for (int i = 0; i < 5; i++) {
                Vector vector = vectorArr[i];
                if (vector != null) {
                    Utils.arraycopy(vectorArr, i + 1, vectorArr, i, 4 - i);
                    vectorArr[4] = null;
                    return vector;
                }
            }
            return new Vector();
        }
    }

    /* renamed from: a */
    public static final String bufToString(StringBuffer stringBuffer, boolean z) {
        if (z) {
            return bufToStringCached(stringBuffer);
        }
        String strM1207f = m1207f(stringBuffer.toString());
        stringBuffer.setLength(0);
        return strM1207f;
    }

    /* renamed from: a */
    public static final String bufToStringCached(StringBuffer stringBuffer) {
        String strM1207f = m1207f(stringBuffer.toString());
        stringBuffer.setLength(0);
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            while (true) {
                if (i >= 5) {
                    break;
                }
                if (stringBufferArr[i] == null) {
                    stringBufferArr[i] = stringBuffer;
                    break;
                }
                i++;
            }
        }
        return strM1207f;
    }

    /* renamed from: b */
    public static final StringBuffer m1216b(int i) {
        return newStringBuffer().append(AppState.getString(i));
    }

    /* renamed from: h */
    public static final StringBuffer newStringBuffer() {
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            do {
                StringBuffer stringBuffer = stringBufferArr[i];
                if (stringBuffer != null) {
                    Utils.arraycopy(stringBufferArr, i + 1, stringBufferArr, i, 4 - i);
                    stringBufferArr[4] = null;
                    return stringBuffer;
                }
                i++;
            } while (i != 5);
            return new StringBuffer();
        }
    }

    /* renamed from: b */
    public static final String bytesToString(byte[] bArr) {
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            do {
                StringBuffer stringBuffer = stringBufferArr[i];
                if (stringBuffer != null) {
                    return m1219a(bArr, stringBuffer, false);
                }
                i++;
            } while (i != 5);
            return m1219a(bArr, new StringBuffer(), true);
        }
    }

    /* renamed from: a */
    private static final String m1219a(byte[] bArr, StringBuffer stringBuffer, boolean z) {
        for (byte b : bArr) {
            stringBuffer.append(Utils.m499a((int) b));
        }
        return bufToString(stringBuffer, z);
    }

    /* renamed from: a */
    private static final String m1220a(long j, StringBuffer stringBuffer, boolean z) {
        while (j != 0) {
            stringBuffer.append((char) (j & 255));
            j >>>= 8;
        }
        return bufToString(stringBuffer, z);
    }

    /* renamed from: a */
    public static final String longToHex(long j) {
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            do {
                StringBuffer stringBuffer = stringBufferArr[i];
                if (stringBuffer != null) {
                    return m1220a(j, stringBuffer, false);
                }
                i++;
            } while (i != 5);
            return m1220a(j, new StringBuffer(), true);
        }
    }

    /* renamed from: i */
    public static final Object[] newRequest() {
        String strM584b = AppState.getString(2950249);
        String str = AppState.emptyStr;
        Integer num = ResourceManager.integerCache[0];
        Integer numM967e = ResourceManager.integerOf(-1);
        return m1224a(0, strM584b, new Object[]{null, null, null, null, null, null, null, str, num, str, str, num, str, str, str, str, num, num, numM967e, num, null, numM967e});
    }

    /* renamed from: a */
    public static final Object[] m1223a(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, int i2, int i3, int i4, int i5, int i6, String str8, String str9) {
        return m1224a(2, bufToStringCached(Utils.m493a(Utils.m494a(Utils.m493a(Utils.m494a(Utils.m494a(Utils.m494a(Utils.m494a(Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(Utils.m493a(newStringBuffer().append(AppState.getString(2163862)), 1311927, str8), 1115339, StringUtils.prefix(str, str.indexOf(64))), 1246428, StringUtils.m5b(str)), 591087, str2), 1049848, str3), 1180936, str4), 1049882, str5), 656682, str6), 591156, str7), 591165, i2), 722246, i3), 656721, i4), 263515, i5), 1181023, str9), 1443185, i6), 198023, AppState.getString(817))), new Object[]{null, null, null, null, null, null, null, str, ResourceManager.integerOf(0), str2, str3, ResourceManager.integerCache[0], str4, str5, str6, str7, ResourceManager.integerOf(i2), ResourceManager.integerOf(i3), ResourceManager.integerOf(i4), ResourceManager.integerOf(i5), null, ResourceManager.integerOf(i6)});
    }

    /* renamed from: a */
    private static final Object[] m1224a(int i, String str, Object[] objArr) {
        objArr[0] = null;
        objArr[1] = ResourceManager.integerOf(i);
        objArr[2] = str;
        new AsyncTask(24, objArr);
        return objArr;
    }

    /* renamed from: e */
    public static final void m1225e(Object[] objArr) {
        try {
            try {
                AppController.m343s();
                HttpClient c0024axM630a = HttpClient.createWithType3(objArr[2]);
                int iM634a = c0024axM630a.getResponseCode();
                if (iM634a == 200) {
                    ByteBuffer c0043n = new ByteBuffer(c0024axM630a);
                    switch (((Integer) objArr[1]).intValue()) {
                        case 0:
                            m1226a(objArr, c0043n.parseXmlStr());
                            HttpClient.closeAndUpdateStats(c0024axM630a);
                            AppController.m344t();
                            return;
                        case 1:
                            objArr[3] = c0043n.toImage();
                            HttpClient.closeAndUpdateStats(c0024axM630a);
                            AppController.m344t();
                            return;
                        case 2:
                            m1226a(objArr, c0043n.parseXmlStr());
                            HttpClient.closeAndUpdateStats(c0024axM630a);
                            AppController.m344t();
                            return;
                    }
                }
                throw new Throwable(StringUtils.intern(Integer.toString(iM634a)));
            } catch (Throwable th) {
                objArr[0] = th;
                HttpClient.closeAndUpdateStats((HttpClient) null);
                AppController.m344t();
            }
        } catch (Throwable th2) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            AppController.m344t();
            throw th2;
        }
    }

    /* renamed from: a */
    private static final void m1226a(Object[] objArr, XmlElement c0022av) {
        Vector vector = c0022av.children;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                if (objArr[4] == null || objArr[6] == null || objArr[5] == null) {
                    throw new RuntimeException();
                }
                objArr[3] = null;
                m1224a(1, new ByteBuffer().writeCompressed(2163862).writeObjectStr(objArr[6]).getStringAndClear(), objArr);
                return;
            }
            XmlElement c0022av2 = (XmlElement) vector.elementAt(size);
            String strM554b = c0022av2.getIntAttribute(329117);
            String strM554b2 = c0022av2.getIntAttribute(262601);
            if (StringUtils.m3a(132297, strM554b2)) {
                objArr[4] = strM554b;
            } else if (StringUtils.m3a(1115488, strM554b2)) {
                objArr[5] = strM554b;
            } else if (StringUtils.m3a(1246602, strM554b2)) {
                objArr[6] = strM554b;
            } else if (StringUtils.m3a(394658, strM554b2)) {
                objArr[20] = strM554b;
                if (Integer.parseInt(strM554b) == 0) {
                    return;
                }
            } else {
                continue;
            }
        }
    }
}
