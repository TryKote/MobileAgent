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
    public static Vector internCache;

    /* renamed from: b */
    public static boolean isKnownDevice1;

    /* renamed from: c */
    public static boolean isKnownDevice2;

    /* renamed from: a */
    public static final String decodeFromBytes(byte[] bArr, int i) {
        ByteArrayInputStream byteArrayInputStream = null;
        DataInputStream dataInputStream = null;
        try {
            try {
                ByteArrayInputStream byteArrayInputStream2 = (ByteArrayInputStream) IOUtils.registerResource((Object) new ByteArrayInputStream(bArr, i, bArr.length - i));
                byteArrayInputStream = byteArrayInputStream2;
                DataInputStream dataInputStream2 = (DataInputStream) IOUtils.registerResource((Object) new DataInputStream(byteArrayInputStream2));
                dataInputStream = dataInputStream2;
                String strM17c = intern(dataInputStream2.readUTF());
                IOUtils.closeInput((InputStream) dataInputStream);
                IOUtils.closeInput((InputStream) byteArrayInputStream);
                return strM17c;
            } catch (Throwable unused) {
                String str = AppState.emptyStr;
                IOUtils.closeInput((InputStream) dataInputStream);
                IOUtils.closeInput((InputStream) byteArrayInputStream);
                return str;
            }
        } catch (Throwable th) {
            IOUtils.closeInput((InputStream) dataInputStream);
            IOUtils.closeInput((InputStream) byteArrayInputStream);
            throw th;
        }
    }

    /* renamed from: a */
    public static final boolean isEmpty(String str) {
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
            return equals(AppState.getString(i), str);
        }
        byte[] bArrM581a = AppState.getBytes(295);
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
        return equals(str, (String) obj);
    }

    /* renamed from: b */
    public static final String m5b(String str) {
        return suffix(str, str.indexOf(64) + 1);
    }

    /* renamed from: a */
    public static final boolean equals(String str, String str2) {
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
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(i)).append(str));
    }

    /* renamed from: a */
    public static final String m8a(int i, Object obj) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(i)).append(obj));
    }

    /* renamed from: b */
    public static final String concat(String str, String str2) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(str).append(str2));
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
                strArrListRecordStores[length] = intern(strArrListRecordStores[length]);
            }
        }
        return strArrListRecordStores;
    }

    /* renamed from: a */
    public static final String fromBuffer(StringBuffer stringBuffer) {
        if (stringBuffer != null) {
            return intern(stringBuffer.toString());
        }
        return null;
    }

    /* renamed from: a */
    public static final String substring(String str, int i, int i2) {
        return intern(str.substring(i, i2));
    }

    /* renamed from: b */
    public static final String prefix(String str, int i) {
        return intern(str.substring(0, i));
    }

    /* renamed from: b */
    public static final String extractBuffer(StringBuffer stringBuffer) {
        String strM11a = fromBuffer(stringBuffer);
        stringBuffer.setLength(0);
        return strM11a;
    }

    /* renamed from: c */
    public static final String suffix(String str, int i) {
        return intern(str.substring(i));
    }

    /* renamed from: a */
    public static final String m16a(TextBox textBox) {
        return Utils.defaultStr(intern(textBox.getString()));
    }

    /* renamed from: c */
    public static final String intern(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() > 256) {
            return str;
        }
        Vector vector = internCache;
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
        return c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(16).writeShortLE(14).writeIntLE(c0033d.serverId).writeShortLE(2000).writeShortBE(0).writeShortLE(1202).writeIntLE(i)), ResourceManager.m967e(7), ResourceManager.m967e(i)});
    }

    /* renamed from: b */
    public static final void m19b() {
        int iM586d = AppState.getBool(277) ? (((AppState.getInt(1415) >> 7) + 2) * ((AppState.getInt(1416) >> 7) + 2)) << 1 : ((AppState.getInt(1415) >> 7) + 2) * ((AppState.getInt(1416) >> 7) + 2);
        AppState.pool[1394] = new LruCache(iM586d);
        AppState.setInt(1550, iM586d);
    }

    /* renamed from: a */
    public static final Image m20a(ResourceManager c0034e) {
        Image image = (Image) m25l().get(c0034e);
        if (image == null && !AppState.getVector(1396).contains(c0034e)) {
            ResourceManager.m950b(c0034e);
        }
        return image;
    }

    /* renamed from: k */
    private static final void m21k() {
        Vector vectorM614m = AppState.getVector(1397);
        synchronized (vectorM614m) {
            Vector vectorM614m2 = AppState.getVector(1398);
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
        while (0 == AppState.getInt(1549)) {
            Object[] objArr = (Object[]) AppState.pool[1395];
            while (true) {
                if (!(AppState.getVector(1398).size() == 0)) {
                    break;
                }
                XmppContactGroup.removeContactInfoFromQueue(objArr);
                try {
                    Thread.sleep(100);
                } catch (Throwable unused) {
                }
            }
            ResourceManager c0034eM949i = ResourceManager.m949i();
            int i2 = c0034eM949i.f281a;
            objArr[1] = new StringBuffer().append(AppState.getString(i2 == 3 ? 997 : i2 == 1 ? 998 : 999)).append(Utils.formatSize(AppState.getInt(1548))).toString();
            XmppContactGroup.addContactInfoToQueue(objArr);
            try {
                Image imageM847a = (c0034eM949i.f281a == 1 && AppState.getBool(1551)) ? XmppMailRuProtocol.m847a(c0034eM949i) : null;
                imageM876b = imageM847a;
                if (imageM847a == null) {
                    imageM876b = XmppMailRuProtocol.m876b(c0034eM949i);
                }
            } catch (IOException unused2) {
                int i3 = i;
                i = i3 - 1;
                if (i3 > 0) {
                    m21k();
                    Vector vectorM614m = AppState.getVector(1398);
                    synchronized (vectorM614m) {
                        if (vectorM614m.removeElement(c0034eM949i)) {
                            ResourceManager.m950b(c0034eM949i);
                        }
                    }
                } else {
                    AppState.setInt(1549, 1);
                }
            } catch (Throwable unused3) {
                ResourceManager.m948a(c0034eM949i);
            }
            if (imageM876b == null) {
                if (i2 == 3) {
                    Vector vectorM614m2 = AppState.getVector(1396);
                    while (vectorM614m2.size() >= AppState.getInt(1550)) {
                        vectorM614m2.removeElementAt(0);
                    }
                    vectorM614m2.addElement(c0034eM949i);
                    XmppContactGroup.flagSyncRequired();
                } else {
                    m23a(c0034eM949i, AppState.getImage(1393));
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
            m25l().put(c0034e, image, 1);
            MapRenderer.f200h = true;
        } catch (Throwable unused) {
        }
    }

    /* renamed from: d */
    public static final void m24d() {
        Enumeration enumerationM52a = m25l().keys();
        while (enumerationM52a.hasMoreElements()) {
            ResourceManager c0034e = (ResourceManager) enumerationM52a.nextElement();
            if (c0034e.f281a == 3) {
                m25l().remove(c0034e);
            }
        }
    }

    /* renamed from: l */
    private static final LruCache m25l() {
        return (LruCache) AppState.pool[1394];
    }

    /* renamed from: m */
    private static final Vector m26m() {
        Vector vectorM1213g = NetworkUtils.newVector();
        vectorM1213g.addElement(AppState.getString(684));
        return vectorM1213g;
    }

    /* renamed from: a */
    private static final void m27a(Vector vector, Object obj) {
        Vector vector2 = ((XmlElement) obj).children;
        for (int i = 0; i < Utils.m541c(vector2); i++) {
            vector.addElement(m28b((XmlElement) vector2.elementAt(i)));
        }
    }

    /* renamed from: b */
    private static final String m28b(XmlElement c0022av) {
        String strM555c = c0022av.getLongKeyAttr(110);
        return strM555c != null ? strM555c : fromBuffer(c0022av.textContent);
    }

    /* renamed from: e */
    public static final void m29e() {
        m30f();
        AppState.pool[1301] = new XmlParser(new ByteBuffer(NetworkUtils.longToHex(25135), 41000)).parse().children;
        StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append(AppState.getString(683));
        Vector vectorM614m = AppState.getVector(1301);
        for (int i = 0; i < Utils.m541c(vectorM614m); i++) {
            stringBufferAppend.append((char) 0).append(m28b((XmlElement) vectorM614m.elementAt(i)));
        }
        AppState.setFromBuffer(1300, stringBufferAppend);
        ScreenManager.showScreen(ScreenManager.createScreen(3356));
    }

    /* renamed from: f */
    public static final void m30f() {
        AppState.clearRange(1296, 1301);
        AppState.setInt(1480, 0);
        AppState.setInt(1481, 0);
        AppState.setInt(1482, -1);
        AppState.setInt(1483, -1);
        AppState.setInt(1484, 0);
        AppState.setInt(1485, 0);
        AppState.setInt(1486, 0);
        AppState.setInt(1487, 0);
        AppState.setInt(1488, 0);
        AppState.setInt(1489, 0);
        AppState.setInt(1490, 0);
    }

    /* renamed from: a */
    public static final void m31a(Screen c0013am, Object obj) {
        MenuItem c0032c = (MenuItem) obj;
        int iIntValue = ((Integer) ((Object[]) c0032c.data)[0]).intValue();
        String str = c0032c.title;
        String strM584b = AppState.getString(689);
        String strM584b2 = AppState.getString(690);
        String strM584b3 = AppState.getString(691);
        String strM584b4 = AppState.getString(688);
        String strM584b5 = AppState.getString(692);
        String strM584b6 = AppState.getString(693);
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
            if (c0032c7.id == 9) {
                String str2 = c0032c7.title;
                if (str2.startsWith(strM584b4)) {
                    c0032c4 = c0032c7;
                } else if (str2.startsWith(strM584b)) {
                    iIntValue2 = ((Integer) ((Object[]) c0032c7.data)[0]).intValue();
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
        if (equals(str, strM584b)) {
            MenuItem c0032c8 = c0032c2;
            Vector vectorM26m = m26m();
            if (iIntValue > 0) {
                m27a(vectorM26m, AppState.getVector(1301).elementAt(iIntValue - 1));
            }
            c0032c8.setChoices(vectorM26m, 0, strM584b2);
            c0032c3.setChoices(Utils.splitByNull(AppState.getString(684)), 0, strM584b3);
        } else if (equals(str, strM584b2)) {
            MenuItem c0032c9 = c0032c3;
            int i = iIntValue2;
            Vector vectorM26m2 = m26m();
            if (iIntValue > 0) {
                m27a(vectorM26m2, ((XmlElement) AppState.getVector(1301).elementAt(i - 1)).children.elementAt(iIntValue - 1));
            }
            c0032c9.setChoices(vectorM26m2, 0, strM584b3);
        } else if (equals(str, strM584b4)) {
            c0032c5.setChoices(Utils.splitByNull(AppState.getString(687)), 0, strM584b5);
            c0032c6.setChoices(Utils.splitByNull(AppState.getString(686)), 0, strM584b6);
        } else if (equals(str, strM584b5) || equals(str, strM584b6)) {
            c0032c4.setChoices(Utils.splitByNull(AppState.getString(685)), 0, strM584b4);
        }
        c0013am.m258q();
    }

    /* renamed from: g */
    public static final String[] m32g() {
        String[] strArr = new String[16];
        String strM522f = Utils.defaultStr(AppState.getString(1296));
        if (!isEmpty(strM522f)) {
            String strM17c = intern(strM522f.toLowerCase());
            int iIndexOf = strM17c.indexOf(64);
            if (iIndexOf >= 0) {
                strArr[0] = prefix(strM17c, iIndexOf);
                strArr[1] = suffix(strM17c, iIndexOf + 1);
            } else {
                strArr[0] = strM17c;
                strArr[1] = suffix(Utils.m542c(694, AppState.getInt(1480)), 1);
            }
            return strArr;
        }
        strArr[2] = Utils.defaultStr(AppState.getString(1297));
        strArr[3] = Utils.defaultStr(AppState.getString(1298));
        strArr[4] = Utils.defaultStr(AppState.getString(1299));
        strArr[5] = m33b(1481);
        strArr[7] = m33b(1482);
        strArr[8] = m33b(1483);
        int iM586d = AppState.getInt(1485);
        if (iM586d > 0) {
            XmlElement c0022av = (XmlElement) AppState.getVector(1301).elementAt(iM586d - 1);
            strArr[15] = c0022av.getLongKeyAttr(105);
            int iM586d2 = AppState.getInt(1486);
            if (iM586d2 > 0) {
                XmlElement c0022av2 = (XmlElement) c0022av.children.elementAt(iM586d2 - 1);
                strArr[11] = c0022av2.getLongKeyAttr(105);
                strArr[15] = null;
                int iM586d3 = AppState.getInt(1487);
                if (iM586d3 > 0) {
                    strArr[11] = ((XmlElement) c0022av2.children.elementAt(iM586d3 - 1)).getLongKeyAttr(105);
                }
            }
        }
        strArr[12] = m33b(1484);
        strArr[13] = m33b(1488);
        strArr[14] = m33b(1489);
        if (AppState.getBool(1490)) {
            strArr[9] = intern(Integer.toString(1));
        }
        return strArr;
    }

    /* renamed from: b */
    private static final String m33b(int i) {
        int iM586d = AppState.getInt(i);
        if (iM586d > 0) {
            return intern(Integer.toString(iM586d));
        }
        return null;
    }

    /* renamed from: h */
    public static final void initPlatform() {
        AppState.setObject(1375, (Object) AppState.getAppProperty(1375));
        while (Utils.parseInt((Object) Utils.defaultStr(AppState.getString(222))) <= 106) {
            try {
                throw new Throwable();
            } catch (Throwable unused) {
                AppState.setObject(222, (Object) intern(Integer.toString(Utils.m520a())));
            }
        }
        m38d(m39e(AppState.getAppProperty(1381)));
        AppState.setObject(1382, (Object) AppState.getAppProperty(1382));
        AppState.setObject(1381, (Object) new ByteBuffer().writeUInt(1029990694).writeRawString(Utils.defaultStr(AppState.getString(222))).writeLongBytes(263912257062L).writeRawString(m36n()).getStringAndClear());
        AppState.setString(1376, getSystemProp(963));
        AppState.setString(1377, getSystemProp(964));
        AppState.setString(1378, getSystemProp(1378));
        AppState.setString(1380, getSystemProp(1380));
        AppState.setString(1379, getSystemProp(1379));
        int i = 967;
        while (true) {
            i--;
            if (i < 965) {
                break;
            }
            try {
                String strM522f = Utils.defaultStr(AppState.getString(i));
                AppState.clearIndex(i);
                Class.forName(strM522f);
                AppState.setInt(1537, 1);
                break;
            } catch (Throwable unused2) {
            }
        }
        if (AppState.getString(1375).charAt(0) == '3' && AppState.getString(1375).charAt(2) == '9') {
            if (AppState.getString(239) == null) {
                AppState.setFromPool(239, 1024);
            }
            AppState.clearIndex(403);
            String strM17c = intern(concat(AppState.getString(1376), AppState.getString(1377)).toLowerCase());
            isKnownDevice2 = AppState.indexOfLong(strM17c, 7163382462464028531L) >= 0 || AppState.indexOf(strM17c, 842019699) == 0 || AppState.indexOf(strM17c, 842019703) == 0;
            isKnownDevice1 = AppState.indexOfLong(strM17c, 418380476270L) >= 0;
            AppState.setBool(1536, AppState.indexOf(strM17c, 761620851) == 0 || AppState.indexOf(strM17c, 1903060322) == 0);
            AppState.setBool(1543, isKnownDevice1 || isKnownDevice2);
            AppState.setBool(1538, AppState.getBool(1537) || AppState.indexOfLong(strM17c, 29113373327974771L) >= 0 || AppState.indexOf(strM17c, 6514035) == 0 || AppState.indexOf(strM17c, 6841203) == 0 || AppState.indexOf(strM17c, 6842227) == 0 || AppState.indexOf(strM17c, 29799) == 0);
            byte bM35c = m35c(0);
            byte bM35c2 = m35c(1);
            byte bM35c3 = m35c(2);
            byte[] bArrM581a = AppState.getBytes(907);
            bArrM581a[13] = bM35c;
            bArrM581a[14] = bM35c2;
            bArrM581a[15] = bM35c3;
            if (AppState.getLong(219) == 0) {
                AppState.setLong(219, System.currentTimeMillis());
                return;
            }
            return;
        }
        while (true) {
            Object obj = AppState.pool[1375];
            AppState.pool[1375] = new Object[]{obj, obj, obj};
        }
    }

    /* renamed from: c */
    private static final byte m35c(int i) {
        try {
            return (byte) Utils.parseInt(Utils.split(AppState.getString(1375), '.').elementAt(i));
        } catch (Throwable unused) {
            return (byte) 0;
        }
    }

    /* renamed from: n */
    private static final String m36n() {
        String strM584b = AppState.getString(1375);
        String[] strArr = new String[3];
        String str = AppState.emptyStr;
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
    public static final String getSystemProp(int i) {
        return intern(System.getProperty(AppState.getString(i)));
    }

    /* JADX DEBUG: Move duplicate insns, count: 2 to block B:19:0x0071 */
    /* renamed from: d */
    private static final void m38d(String str) {
        int i;
        int i2;
        if (str != null) {
            AppState.setObject(1374, (Object) str);
        }
        if (null == AppState.getString(223)) {
            if (null != str) {
                AppState.setObject(223, (Object) str);
                return;
            }
            StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
            int i3 = 0;
            while (i3 < 2) {
                long jCurrentTimeMillis = i3 == 0 ? System.currentTimeMillis() : (Utils.m520a() << 32) | Utils.parseInt((Object) Utils.defaultStr(AppState.getString(222)));
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
            AppState.setFromBuffer(223, stringBufferM1217h);
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
    public static GeoRegion getGeoRegion() {
        return (GeoRegion) AppState.pool[1391];
    }

    /* renamed from: j */
    public static final void m41j() {
        AppState.pool[1389] = NetworkUtils.newVector();
        AppState.pool[1390] = new GeoRegion(AppState.getString(996), 4115426L, 7539707L, 4267459L, 7412592L);
        try {
            ByteBuffer c0043nM986d = ResourceManager.m986d(AppState.getString(227));
            AppState.getVector(1389).removeAllElements();
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
        GeoRegion c0053x = new GeoRegion(AppState.getString(995), 1866877L, 15815124L, 21989606L, 4133096L);
        c0053x.centerLat = 10848141L;
        c0053x.centerLon = 8758455L;
        AppState.pool[1391] = c0053x;
    }

    /* renamed from: a */
    private static void m42a(GeoRegion c0053x) {
        Vector vectorM614m = AppState.getVector(1389);
        if (vectorM614m.contains(c0053x)) {
            return;
        }
        vectorM614m.addElement(c0053x);
    }

    /* renamed from: a */
    public static final boolean m43a(long j, long j2) {
        Vector vectorM614m = AppState.getVector(1389);
        int iM541c = Utils.m541c(vectorM614m);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                return false;
            }
            GeoRegion c0053x = (GeoRegion) vectorM614m.elementAt(iM541c);
            if (c0053x.containsPoint(j, j2) && AppState.indexOfPool(c0053x.name, 995) < 0) {
                return true;
            }
        }
    }

    /* renamed from: a */
    public static final void m44a(XmlElement c0022av) {
        Vector vector;
        if (c0022av == null || (vector = c0022av.children) == null) {
            return;
        }
        AppState.getVector(1389).removeAllElements();
        String strM554b = c0022av.getIntAttribute(594023);
        if (strM554b != null) {
            AppState.setObject(254, (Object) strM554b);
        }
        for (int i = 0; i < Utils.m541c(vector); i++) {
            XmlElement c0022av2 = (XmlElement) vector.elementAt(i);
            String str = c0022av2.tagName;
            if (m2a(str, 1936156018)) {
                Vector vector2 = c0022av2.children;
                for (int i2 = 0; i2 < Utils.m541c(vector2); i2++) {
                    XmlElement c0022av3 = (XmlElement) vector2.elementAt(i2);
                    GeoRegion c0053x = new GeoRegion(c0022av3.getLongKeyAttr(1701667182), c0022av3.getAttrAsLong(28780), c0022av3.getAttrAsLong(28788), c0022av3.getAttrAsLong(28786), c0022av3.getAttrAsLong(28770));
                    c0053x.description = c0022av3.getLongKeyAttr(25705);
                    c0053x.centerLat = c0022av3.getAttrAsLong(1852796003);
                    c0053x.centerLon = c0022av3.getAttrAsLong(1952541795);
                    c0053x.precision = c0022av3.getAttrAsInt(2054709613);
                    m42a(c0053x);
                }
            } else if (m3a(397424, str)) {
                ConnectionThread.m1137a(1, c0022av2, true);
            }
        }
        try {
            AppState.resetToEmpty(227);
            ByteBuffer c0043n = new ByteBuffer();
            Vector vectorM614m = AppState.getVector(1389);
            int size = vectorM614m.size();
            c0043n.writeIntBE(size);
            for (int i3 = 0; i3 < size; i3++) {
                GeoRegion c0053x2 = (GeoRegion) vectorM614m.elementAt(i3);
                c0043n.writeStringUTF16(c0053x2.name).writeLong(c0053x2.minLat).writeLong(c0053x2.maxLon).writeLong(c0053x2.maxLat).writeLong(c0053x2.minLon).writeStringUTF16(c0053x2.description).writeLong(c0053x2.centerLat).writeLong(c0053x2.centerLon).writeIntLE(c0053x2.precision);
            }
            AppState.setObject(227, (Object) c0043n.toBase64());
        } catch (Throwable unused) {
            AppState.resetToEmpty(254);
        }
    }
}
