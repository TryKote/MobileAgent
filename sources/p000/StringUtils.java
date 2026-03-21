package p000;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.rms.RecordStore;

/* renamed from: a */
/* loaded from: MobileAgent_3.9.jar:a.class */
public final class StringUtils {

    /* renamed from: a */
    public static Vector f0a;

    /* renamed from: b */
    public static boolean f1b;

    /* renamed from: c */
    public static boolean f2c;

    /* renamed from: a */
    public static final String m0a(byte[] bArr, int i) {
        ByteArrayInputStream byteArrayInputStream = null;
        DataInputStream dataInputStream = null;
        try {
            try {
                ByteArrayInputStream byteArrayInputStream2 = (ByteArrayInputStream) IOUtils.m761a((Object) new ByteArrayInputStream(bArr, i, bArr.length - i));
                byteArrayInputStream = byteArrayInputStream2;
                DataInputStream dataInputStream2 = (DataInputStream) IOUtils.m761a((Object) new DataInputStream(byteArrayInputStream2));
                dataInputStream = dataInputStream2;
                String strM17c = m17c(dataInputStream2.readUTF());
                IOUtils.m763a((InputStream) dataInputStream);
                IOUtils.m763a((InputStream) byteArrayInputStream);
                return strM17c;
            } catch (Throwable unused) {
                String str = AppState.f181d;
                IOUtils.m763a((InputStream) dataInputStream);
                IOUtils.m763a((InputStream) byteArrayInputStream);
                return str;
            }
        } catch (Throwable th) {
            IOUtils.m763a((InputStream) dataInputStream);
            IOUtils.m763a((InputStream) byteArrayInputStream);
            throw th;
        }
    }

    /* renamed from: a */
    public static final boolean m1a(String str) {
        return str != null && str.length() == 0;
    }

    /* renamed from: a */
    public static final boolean m2a(String str, int i) {
        long j = i;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            if (str.charAt(i2) != (j & 255)) {
                return false;
            }
            j >>>= 8;
        }
        return j == 0;
    }

    /* renamed from: a */
    public static final boolean m3a(int i, String str) {
        if (str == null) {
            return false;
        }
        if (i <= 5179) {
            return m6a(AppState.m584b(i), str);
        }
        byte[] bArrM581a = AppState.m581a(295);
        int i2 = i >> 16;
        int i3 = i2;
        if (i2 != str.length()) {
            return false;
        }
        int i4 = i & 65535;
        do {
            i3--;
            if (i3 < 0) {
                return true;
            }
        } while (str.charAt(i3) == bArrM581a[i4 + i3]);
        return false;
    }

    /* renamed from: a */
    public static final boolean m4a(String str, Object obj) {
        return m6a(str, (String) obj);
    }

    /* renamed from: b */
    public static final String m5b(String str) {
        return m15c(str, str.indexOf(64) + 1);
    }

    /* renamed from: a */
    public static final boolean m6a(String str, String str2) {
        if (str == str2) {
            return true;
        }
        if (str2 == null) {
            return false;
        }
        return str.equals(str2);
    }

    /* renamed from: b */
    public static final String m7b(int i, String str) {
        return NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(i)).append(str));
    }

    /* renamed from: a */
    public static final String m8a(int i, Object obj) {
        return NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(i)).append(obj));
    }

    /* renamed from: b */
    public static final String m9b(String str, String str2) {
        return NetworkUtils.m1215a(NetworkUtils.m1217h().append(str).append(str2));
    }

    /* renamed from: a */
    public static final String[] m10a() {
        String[] strArrListRecordStores = RecordStore.listRecordStores();
        if (strArrListRecordStores != null) {
            int length = strArrListRecordStores.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                strArrListRecordStores[length] = m17c(strArrListRecordStores[length]);
            }
        }
        return strArrListRecordStores;
    }

    /* renamed from: a */
    public static final String m11a(StringBuffer stringBuffer) {
        if (stringBuffer != null) {
            return m17c(stringBuffer.toString());
        }
        return null;
    }

    /* renamed from: a */
    public static final String m12a(String str, int i, int i2) {
        return m17c(str.substring(i, i2));
    }

    /* renamed from: b */
    public static final String m13b(String str, int i) {
        return m17c(str.substring(0, i));
    }

    /* renamed from: b */
    public static final String m14b(StringBuffer stringBuffer) {
        String strM11a = m11a(stringBuffer);
        stringBuffer.setLength(0);
        return strM11a;
    }

    /* renamed from: c */
    public static final String m15c(String str, int i) {
        return m17c(str.substring(i));
    }

    /* renamed from: a */
    public static final String m16a(TextBox textBox) {
        return Utils.m522f(m17c(textBox.getString()));
    }

    /* renamed from: c */
    public static final String m17c(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() > 256) {
            return str;
        }
        Vector vector = f0a;
        synchronized (vector) {
            int iIndexOf = vector.indexOf(str);
            if (iIndexOf >= 0) {
                String strM521a = Utils.m521a(vector, iIndexOf);
                if (iIndexOf != 0) {
                    vector.removeElementAt(iIndexOf);
                    vector.insertElementAt(strM521a, 0);
                }
                return strM521a;
            }
            if (vector.size() > 64) {
                if (vector.size() == 128) {
                    vector.removeElementAt(127);
                }
                vector.insertElementAt(str, 64);
            } else {
                vector.addElement(str);
            }
            return str;
        }
    }

    /* renamed from: a */
    public static final ByteBuffer m18a(MmpProtocol c0033d, int i) {
        return c0033d.m916a(new Object[]{AppController.m464a(c0033d, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(16).writeShortLE(14).writeIntLE(c0033d.f269a).writeShortLE(2000).writeShortBE(0).writeShortLE(1202).writeIntLE(i)), ResourceManager.m967e(7), ResourceManager.m967e(i)});
    }

    /* renamed from: b */
    public static final void m19b() {
        int iM586d = AppState.m587e(277) ? (((AppState.m586d(1415) >> 7) + 2) * ((AppState.m586d(1416) >> 7) + 2)) << 1 : ((AppState.m586d(1415) >> 7) + 2) * ((AppState.m586d(1416) >> 7) + 2);
        AppState.f177b[1394] = new LruCache(iM586d);
        AppState.m594c(1550, iM586d);
    }

    /* renamed from: a */
    public static final Image m20a(ResourceManager c0034e) {
        Image image = (Image) m25l().m50a(c0034e);
        if (image == null && !AppState.m614m(1396).contains(c0034e)) {
            ResourceManager.m950b(c0034e);
        }
        return image;
    }

    /* renamed from: k */
    private static final void m21k() {
        Vector vectorM614m = AppState.m614m(1397);
        synchronized (vectorM614m) {
            Vector vectorM614m2 = AppState.m614m(1398);
            synchronized (vectorM614m2) {
                int size = vectorM614m2.size();
                while (true) {
                    size--;
                    if (size >= 0) {
                        Object objElementAt = vectorM614m2.elementAt(size);
                        if (!vectorM614m.contains(objElementAt)) {
                            vectorM614m2.removeElement(objElementAt);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: c */
    public static final void m22c() {
        Image imageM876b = null;
        int i = 4;
        while (0 == AppState.m586d(1549)) {
            Object[] objArr = (Object[]) AppState.f177b[1395];
            while (true) {
                if (!(AppState.m614m(1398).size() == 0)) {
                    break;
                }
                XmppContactGroup.m1042b(objArr);
                try {
                    Thread.sleep(100);
                } catch (Throwable unused) {
                }
            }
            ResourceManager c0034eM949i = ResourceManager.m949i();
            int i2 = c0034eM949i.f281a;
            objArr[1] = new StringBuffer().append(AppState.m584b(i2 == 3 ? 997 : i2 == 1 ? 998 : 999)).append(Utils.m523d(AppState.m586d(1548))).toString();
            XmppContactGroup.m1041a(objArr);
            try {
                Image imageM847a = (c0034eM949i.f281a == 1 && AppState.m587e(1551)) ? XmppMailRuProtocol.m847a(c0034eM949i) : null;
                imageM876b = imageM847a;
                if (imageM847a == null) {
                    imageM876b = XmppMailRuProtocol.m876b(c0034eM949i);
                }
            } catch (IOException unused2) {
                int i3 = i;
                i = i3 - 1;
                if (i3 > 0) {
                    m21k();
                    Vector vectorM614m = AppState.m614m(1398);
                    synchronized (vectorM614m) {
                        if (vectorM614m.removeElement(c0034eM949i)) {
                            ResourceManager.m950b(c0034eM949i);
                        }
                    }
                } else {
                    AppState.m594c(1549, 1);
                }
            } catch (Throwable unused3) {
                ResourceManager.m948a(c0034eM949i);
            }
            if (imageM876b == null) {
                if (i2 == 3) {
                    Vector vectorM614m2 = AppState.m614m(1396);
                    while (vectorM614m2.size() >= AppState.m586d(1550)) {
                        vectorM614m2.removeElementAt(0);
                    }
                    vectorM614m2.addElement(c0034eM949i);
                    XmppContactGroup.m1038k();
                } else {
                    m23a(c0034eM949i, AppState.m615n(1393));
                }
                throw new RuntimeException();
            }
            i = 4;
            m23a(c0034eM949i, imageM876b);
            ResourceManager.m948a(c0034eM949i);
            m21k();
        }
    }

    /* renamed from: a */
    private static final void m23a(ResourceManager c0034e, Image image) {
        try {
            m25l().m49a(c0034e, image, 1);
            MapRenderer.f200h = true;
        } catch (Throwable unused) {
        }
    }

    /* renamed from: d */
    public static final void m24d() {
        Enumeration enumerationM52a = m25l().m52a();
        while (enumerationM52a.hasMoreElements()) {
            ResourceManager c0034e = (ResourceManager) enumerationM52a.nextElement();
            if (c0034e.f281a == 3) {
                m25l().m51b(c0034e);
            }
        }
    }

    /* renamed from: l */
    private static final LruCache m25l() {
        return (LruCache) AppState.f177b[1394];
    }

    /* renamed from: m */
    private static final Vector m26m() {
        Vector vectorM1213g = NetworkUtils.m1213g();
        vectorM1213g.addElement(AppState.m584b(684));
        return vectorM1213g;
    }

    /* renamed from: a */
    private static final void m27a(Vector vector, Object obj) {
        Vector vector2 = ((XmlElement) obj).f172b;
        for (int i = 0; i < Utils.m541c(vector2); i++) {
            vector.addElement(m28b((XmlElement) vector2.elementAt(i)));
        }
    }

    /* renamed from: b */
    private static final String m28b(XmlElement c0022av) {
        String strM555c = c0022av.m555c(110);
        return strM555c != null ? strM555c : m11a(c0022av.f173c);
    }

    /* renamed from: e */
    public static final void m29e() {
        m30f();
        AppState.f177b[1301] = new XmlParser(new ByteBuffer(NetworkUtils.m1221a(25135), 41000)).m47a().f172b;
        StringBuffer stringBufferAppend = NetworkUtils.m1217h().append(AppState.m584b(683));
        Vector vectorM614m = AppState.m614m(1301);
        for (int i = 0; i < Utils.m541c(vectorM614m); i++) {
            stringBufferAppend.append((char) 0).append(m28b((XmlElement) vectorM614m.elementAt(i)));
        }
        AppState.m588a(1300, stringBufferAppend);
        ScreenManager.m71b(ScreenManager.m75b(3356));
    }

    /* renamed from: f */
    public static final void m30f() {
        AppState.m590b(1296, 1301);
        AppState.m594c(1480, 0);
        AppState.m594c(1481, 0);
        AppState.m594c(1482, -1);
        AppState.m594c(1483, -1);
        AppState.m594c(1484, 0);
        AppState.m594c(1485, 0);
        AppState.m594c(1486, 0);
        AppState.m594c(1487, 0);
        AppState.m594c(1488, 0);
        AppState.m594c(1489, 0);
        AppState.m594c(1490, 0);
    }

    /* renamed from: a */
    public static final void m31a(Screen c0013am, Object obj) {
        MenuItem c0032c = (MenuItem) obj;
        int iIntValue = ((Integer) ((Object[]) c0032c.f265d)[0]).intValue();
        String str = c0032c.f259b;
        String strM584b = AppState.m584b(689);
        String strM584b2 = AppState.m584b(690);
        String strM584b3 = AppState.m584b(691);
        String strM584b4 = AppState.m584b(688);
        String strM584b5 = AppState.m584b(692);
        String strM584b6 = AppState.m584b(693);
        MenuItem c0032c2 = null;
        MenuItem c0032c3 = null;
        MenuItem c0032c4 = null;
        MenuItem c0032c5 = null;
        MenuItem c0032c6 = null;
        int iIntValue2 = 0;
        Vector vector = c0013am.f108m;
        int iM541c = Utils.m541c(vector);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                break;
            }
            MenuItem c0032c7 = (MenuItem) vector.elementAt(iM541c);
            if (c0032c7.f258a == 9) {
                String str2 = c0032c7.f259b;
                if (str2.startsWith(strM584b4)) {
                    c0032c4 = c0032c7;
                } else if (str2.startsWith(strM584b)) {
                    iIntValue2 = ((Integer) ((Object[]) c0032c7.f265d)[0]).intValue();
                } else if (str2.startsWith(strM584b2)) {
                    c0032c2 = c0032c7;
                } else if (str2.startsWith(strM584b3)) {
                    c0032c3 = c0032c7;
                } else if (str2.startsWith(strM584b5)) {
                    c0032c5 = c0032c7;
                } else if (str2.startsWith(strM584b6)) {
                    c0032c6 = c0032c7;
                }
            }
        }
        if (m6a(str, strM584b)) {
            MenuItem c0032c8 = c0032c2;
            Vector vectorM26m = m26m();
            if (iIntValue > 0) {
                m27a(vectorM26m, AppState.m614m(1301).elementAt(iIntValue - 1));
            }
            c0032c8.m892a(vectorM26m, 0, strM584b2);
            c0032c3.m892a(Utils.m512e(AppState.m584b(684)), 0, strM584b3);
        } else if (m6a(str, strM584b2)) {
            MenuItem c0032c9 = c0032c3;
            int i = iIntValue2;
            Vector vectorM26m2 = m26m();
            if (iIntValue > 0) {
                m27a(vectorM26m2, ((XmlElement) AppState.m614m(1301).elementAt(i - 1)).f172b.elementAt(iIntValue - 1));
            }
            c0032c9.m892a(vectorM26m2, 0, strM584b3);
        } else if (m6a(str, strM584b4)) {
            c0032c5.m892a(Utils.m512e(AppState.m584b(687)), 0, strM584b5);
            c0032c6.m892a(Utils.m512e(AppState.m584b(686)), 0, strM584b6);
        } else if (m6a(str, strM584b5) || m6a(str, strM584b6)) {
            c0032c4.m892a(Utils.m512e(AppState.m584b(685)), 0, strM584b4);
        }
        c0013am.m258q();
    }

    /* renamed from: g */
    public static final String[] m32g() {
        String[] strArr = new String[16];
        String strM522f = Utils.m522f(AppState.m584b(1296));
        if (!m1a(strM522f)) {
            String strM17c = m17c(strM522f.toLowerCase());
            int iIndexOf = strM17c.indexOf(64);
            if (iIndexOf >= 0) {
                strArr[0] = m13b(strM17c, iIndexOf);
                strArr[1] = m15c(strM17c, iIndexOf + 1);
            } else {
                strArr[0] = strM17c;
                strArr[1] = m15c(Utils.m542c(694, AppState.m586d(1480)), 1);
            }
            return strArr;
        }
        strArr[2] = Utils.m522f(AppState.m584b(1297));
        strArr[3] = Utils.m522f(AppState.m584b(1298));
        strArr[4] = Utils.m522f(AppState.m584b(1299));
        strArr[5] = m33b(1481);
        strArr[7] = m33b(1482);
        strArr[8] = m33b(1483);
        int iM586d = AppState.m586d(1485);
        if (iM586d > 0) {
            XmlElement c0022av = (XmlElement) AppState.m614m(1301).elementAt(iM586d - 1);
            strArr[15] = c0022av.m555c(105);
            int iM586d2 = AppState.m586d(1486);
            if (iM586d2 > 0) {
                XmlElement c0022av2 = (XmlElement) c0022av.f172b.elementAt(iM586d2 - 1);
                strArr[11] = c0022av2.m555c(105);
                strArr[15] = null;
                int iM586d3 = AppState.m586d(1487);
                if (iM586d3 > 0) {
                    strArr[11] = ((XmlElement) c0022av2.f172b.elementAt(iM586d3 - 1)).m555c(105);
                }
            }
        }
        strArr[12] = m33b(1484);
        strArr[13] = m33b(1488);
        strArr[14] = m33b(1489);
        if (AppState.m587e(1490)) {
            strArr[9] = m17c(Integer.toString(1));
        }
        return strArr;
    }

    /* renamed from: b */
    private static final String m33b(int i) {
        int iM586d = AppState.m586d(i);
        if (iM586d > 0) {
            return m17c(Integer.toString(iM586d));
        }
        return null;
    }

    /* renamed from: h */
    public static final void m34h() {
        AppState.m601a(1375, (Object) AppState.m603i(1375));
        while (Utils.m510a((Object) Utils.m522f(AppState.m584b(222))) <= 106) {
            try {
                throw new Throwable();
            } catch (Throwable unused) {
                AppState.m601a(222, (Object) m17c(Integer.toString(Utils.m520a())));
            }
        }
        m38d(m39e(AppState.m603i(1381)));
        AppState.m601a(1382, (Object) AppState.m603i(1382));
        AppState.m601a(1381, (Object) new ByteBuffer().writeUInt(1029990694).writeRawString(Utils.m522f(AppState.m584b(222))).writeLongBytes(263912257062L).writeRawString(m36n()).getStringAndClear());
        AppState.m592a(1376, m37a(963));
        AppState.m592a(1377, m37a(964));
        AppState.m592a(1378, m37a(1378));
        AppState.m592a(1380, m37a(1380));
        AppState.m592a(1379, m37a(1379));
        int i = 967;
        while (true) {
            i--;
            if (i < 965) {
                break;
            }
            try {
                String strM522f = Utils.m522f(AppState.m584b(i));
                AppState.m591f(i);
                Class.forName(strM522f);
                AppState.m594c(1537, 1);
                break;
            } catch (Throwable unused2) {
            }
        }
        if (AppState.m584b(1375).charAt(0) == '3' && AppState.m584b(1375).charAt(2) == '9') {
            if (AppState.m584b(239) == null) {
                AppState.m589a(239, 1024);
            }
            AppState.m591f(403);
            String strM17c = m17c(m9b(AppState.m584b(1376), AppState.m584b(1377)).toLowerCase());
            f2c = AppState.m627a(strM17c, 7163382462464028531L) >= 0 || AppState.m626a(strM17c, 842019699) == 0 || AppState.m626a(strM17c, 842019703) == 0;
            f1b = AppState.m627a(strM17c, 418380476270L) >= 0;
            AppState.m599a(1536, AppState.m626a(strM17c, 761620851) == 0 || AppState.m626a(strM17c, 1903060322) == 0);
            AppState.m599a(1543, f1b || f2c);
            AppState.m599a(1538, AppState.m587e(1537) || AppState.m627a(strM17c, 29113373327974771L) >= 0 || AppState.m626a(strM17c, 6514035) == 0 || AppState.m626a(strM17c, 6841203) == 0 || AppState.m626a(strM17c, 6842227) == 0 || AppState.m626a(strM17c, 29799) == 0);
            byte bM35c = m35c(0);
            byte bM35c2 = m35c(1);
            byte bM35c3 = m35c(2);
            byte[] bArrM581a = AppState.m581a(907);
            bArrM581a[13] = bM35c;
            bArrM581a[14] = bM35c2;
            bArrM581a[15] = bM35c3;
            if (AppState.m598g(219) == 0) {
                AppState.m597a(219, System.currentTimeMillis());
                return;
            }
            return;
        }
        while (true) {
            Object obj = AppState.f177b[1375];
            AppState.f177b[1375] = new Object[]{obj, obj, obj};
        }
    }

    /* renamed from: c */
    private static final byte m35c(int i) {
        try {
            return (byte) Utils.m510a(Utils.m515b(AppState.m584b(1375), '.').elementAt(i));
        } catch (Throwable unused) {
            return (byte) 0;
        }
    }

    /* renamed from: n */
    private static final String m36n() {
        String strM584b = AppState.m584b(1375);
        String[] strArr = new String[3];
        String str = AppState.f181d;
        strArr[0] = str;
        strArr[1] = str;
        strArr[2] = str;
        int i = 0;
        for (int i2 = 0; i2 < strM584b.length(); i2++) {
            char cCharAt = strM584b.charAt(i2);
            if (cCharAt == '.') {
                i++;
            } else {
                int i3 = i;
                strArr[i3] = new StringBuffer().append(strArr[i3]).append(cCharAt).toString();
            }
        }
        while (strArr[0].length() < 2) {
            strArr[0] = new StringBuffer().append('0').append(strArr[0]).toString();
        }
        while (strArr[1].length() < 2) {
            strArr[1] = new StringBuffer().append('0').append(strArr[1]).toString();
        }
        while (strArr[2].length() < 4) {
            strArr[2] = new StringBuffer().append('0').append(strArr[2]).toString();
        }
        return new ByteBuffer().writeRawString(strArr[0]).writeRawString(strArr[1]).writeRawString(strArr[2]).getStringAndClear();
    }

    /* renamed from: a */
    public static final String m37a(int i) {
        return m17c(System.getProperty(AppState.m584b(i)));
    }

    /* JADX DEBUG: Move duplicate insns, count: 2 to block B:19:0x0071 */
    /* renamed from: d */
    private static final void m38d(String str) {
        int i;
        int i2;
        if (str != null) {
            AppState.m601a(1374, (Object) str);
        }
        if (null == AppState.m584b(223)) {
            if (null != str) {
                AppState.m601a(223, (Object) str);
                return;
            }
            StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
            int i3 = 0;
            while (i3 < 2) {
                long jCurrentTimeMillis = i3 == 0 ? System.currentTimeMillis() : (Utils.m520a() << 32) | Utils.m510a((Object) Utils.m522f(AppState.m584b(222)));
                for (int i4 = 0; i4 < 64; i4 += 4) {
                    int i5 = ((int) (jCurrentTimeMillis >>> (60 - i4))) & 15;
                    if (i5 < 10) {
                        i = i5;
                        i2 = 48;
                    } else {
                        i = i5;
                        i2 = 87;
                    }
                    stringBufferM1217h.append((char) (i + i2));
                }
                i3++;
            }
            AppState.m588a(223, stringBufferM1217h);
        }
    }

    /* renamed from: e */
    private static final String m39e(String str) {
        if (str == null || str.length() != 32) {
            return null;
        }
        int i = 32;
        while (true) {
            i--;
            if (i < 0) {
                return str;
            }
            char cCharAt = str.charAt(i);
            if (cCharAt < '0' || cCharAt > '9') {
                if (cCharAt < 'a' || cCharAt > 'f') {
                    return null;
                }
            }
        }
    }

    /* renamed from: i */
    public static GeoRegion m40i() {
        return (GeoRegion) AppState.f177b[1391];
    }

    /* renamed from: j */
    public static final void m41j() {
        AppState.f177b[1389] = NetworkUtils.m1213g();
        AppState.f177b[1390] = new GeoRegion(AppState.m584b(996), 4115426L, 7539707L, 4267459L, 7412592L);
        try {
            ByteBuffer c0043nM986d = ResourceManager.m986d(AppState.m584b(227));
            AppState.m614m(1389).removeAllElements();
            if (c0043nM986d.length > 0) {
                int iM1355w = c0043nM986d.readIntBE();
                while (true) {
                    iM1355w--;
                    if (iM1355w < 0) {
                        break;
                    } else {
                        m42a(new GeoRegion(c0043nM986d));
                    }
                }
            }
        } catch (Throwable unused) {
        }
        GeoRegion c0053x = new GeoRegion(AppState.m584b(995), 1866877L, 15815124L, 21989606L, 4133096L);
        c0053x.centerLat = 10848141L;
        c0053x.centerLon = 8758455L;
        AppState.f177b[1391] = c0053x;
    }

    /* renamed from: a */
    private static void m42a(GeoRegion c0053x) {
        Vector vectorM614m = AppState.m614m(1389);
        if (vectorM614m.contains(c0053x)) {
            return;
        }
        vectorM614m.addElement(c0053x);
    }

    /* renamed from: a */
    public static final boolean m43a(long j, long j2) {
        Vector vectorM614m = AppState.m614m(1389);
        int iM541c = Utils.m541c(vectorM614m);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                return false;
            }
            GeoRegion c0053x = (GeoRegion) vectorM614m.elementAt(iM541c);
            if (c0053x.containsPoint(j, j2) && AppState.m628b(c0053x.name, 995) < 0) {
                return true;
            }
        }
    }

    /* renamed from: a */
    public static final void m44a(XmlElement c0022av) {
        Vector vector;
        if (c0022av == null || (vector = c0022av.f172b) == null) {
            return;
        }
        AppState.m614m(1389).removeAllElements();
        String strM554b = c0022av.m554b(594023);
        if (strM554b != null) {
            AppState.m601a(254, (Object) strM554b);
        }
        for (int i = 0; i < Utils.m541c(vector); i++) {
            XmlElement c0022av2 = (XmlElement) vector.elementAt(i);
            String str = c0022av2.f171a;
            if (m2a(str, 1936156018)) {
                Vector vector2 = c0022av2.f172b;
                for (int i2 = 0; i2 < Utils.m541c(vector2); i2++) {
                    XmlElement c0022av3 = (XmlElement) vector2.elementAt(i2);
                    GeoRegion c0053x = new GeoRegion(c0022av3.m555c(1701667182), c0022av3.m556d(28780), c0022av3.m556d(28788), c0022av3.m556d(28786), c0022av3.m556d(28770));
                    c0053x.description = c0022av3.m555c(25705);
                    c0053x.centerLat = c0022av3.m556d(1852796003);
                    c0053x.centerLon = c0022av3.m556d(1952541795);
                    c0053x.precision = c0022av3.m557e(2054709613);
                    m42a(c0053x);
                }
            } else if (m3a(397424, str)) {
                ConnectionThread.m1137a(1, c0022av2, true);
            }
        }
        try {
            AppState.m607j(227);
            ByteBuffer c0043n = new ByteBuffer();
            Vector vectorM614m = AppState.m614m(1389);
            int size = vectorM614m.size();
            c0043n.writeIntBE(size);
            for (int i3 = 0; i3 < size; i3++) {
                GeoRegion c0053x2 = (GeoRegion) vectorM614m.elementAt(i3);
                c0043n.writeStringUTF16(c0053x2.name).writeLong(c0053x2.minLat).writeLong(c0053x2.maxLon).writeLong(c0053x2.maxLat).writeLong(c0053x2.minLon).writeStringUTF16(c0053x2.description).writeLong(c0053x2.centerLat).writeLong(c0053x2.centerLon).writeIntLE(c0053x2.precision);
            }
            AppState.m601a(227, (Object) c0043n.toBase64());
        } catch (Throwable unused) {
            AppState.m607j(254);
        }
    }
}
