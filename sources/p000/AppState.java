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
public abstract class AppState {

    /* renamed from: a */
    public static byte[] emptyBytes;

    /* renamed from: b */
    public static Object[] pool;

    /* renamed from: e */
    private static Object[] delta;

    /* renamed from: f */
    private static int[] intPool;

    /* renamed from: c */
    public static Object currentScreen;

    /* renamed from: d */
    public static String emptyStr;

    /* renamed from: g */
    private static String separator;

    /* JADX WARN: Type inference failed for: r0v9, types: [byte[], byte[][]] */
    /* renamed from: a */
    public static final void init(Object obj) {
        int iM1346q;
        ResourceManager.boolTrue = new Boolean(true);
        ResourceManager.boolFalse = new Boolean(false);
        ResourceManager.syncObject = new Object();
        ResourceManager.integerCache = new Integer[32];
        int i = 32;
        while (true) {
            i--;
            if (i < 0) {
                break;
            } else {
                ResourceManager.integerCache[i] = new Integer(i);
            }
        }
        StringUtils.internCache = new Vector(128);
        NetworkUtils.bytePool = new byte[20][];
        NetworkUtils.bufferPool = new StringBuffer[5];
        NetworkUtils.vectorPool = new Vector[5];
        NetworkUtils.stringCache = new Hashtable();
        IOUtils.openResources = NetworkUtils.newVector();
        separator = NetworkUtils.longToHex(1819047278);
        emptyBytes = new byte[0];
        delta = new Object[295];
        pool = new Object[1406];
        intPool = new int[3773];
        ByteBuffer c0043n = new ByteBuffer(NetworkUtils.longToHex(1734763311), 45000);
        for (int i2 = 0; i2 < 1406; i2++) {
            pool[i2] = decodeObject(c0043n, i2);
        }
        for (int i3 = 0; i3 < 3773; i3++) {
            int[] iArr = intPool;
            int i4 = i3;
            byte bM1344o = c0043n.readByte();
            if ((bM1344o & 64) != 0) {
                iM1346q = bM1344o & 63;
            } else if ((bM1344o & 32) != 0) {
                iM1346q = ((bM1344o & 31) << 8) + c0043n.readUByte();
            } else {
                int iM1346q2 = 0;
                int i5 = bM1344o & 7;
                while (true) {
                    i5--;
                    if (i5 < 0) {
                        break;
                    } else {
                        iM1346q2 = (iM1346q2 << 8) + c0043n.readUByte();
                    }
                }
                iM1346q = iM1346q2;
            }
            iArr[i4] = iM1346q;
        }
        emptyStr = (String) pool[1038];
        ByteBuffer c0043nM851h = XmppMailRuProtocol.readChunkedRecord(NetworkUtils.longToHex(1164404323));
        while (c0043nM851h.length > 0) {
            try {
                delta[((Integer) decodeObject(c0043nM851h, 0)).intValue()] = decodeObject(c0043nM851h, 0);
            } catch (Throwable unused) {
            }
        }
        try {
            try {
            } catch (Throwable th) {
                setInt(0, 3096);
                throw th;
            }
        } catch (Throwable unused2) {
            delta = new Object[295];
            try {
                String[] strArrM10a = StringUtils.m10a();
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
            setInt(0, 3096);
        }
        if (((Integer) delta[0]).intValue() != 3096) {
            throw new RuntimeException();
        }
        setInt(0, 3096);
        setObject(1369, (Object) separator);
        pool[1366] = obj;
        pool[1370] = new int[0];
        Date date = new Date();
        pool[1368] = date;
        pool[1367] = Calendar.getInstance();
        setLong(1532, date.getTime() - System.currentTimeMillis());
        updateTime();
        pool[1372] = new Random(System.currentTimeMillis() ^ Thread.currentThread().hashCode());
        pool[1361] = new Object[58];
        pool[1362] = new int[29];
        pool[1266] = NetworkUtils.newVector();
        pool[1267] = new int[]{1};
        pool[1268] = new int[]{2};
        pool[1269] = new int[]{3};
        pool[1270] = new int[]{4};
        StringUtils.initPlatform();
        AppController.f147a = new long[14];
        pool[1238] = new Object[1];
        NetworkUtils.m1208b(separator);
        NetworkUtils.m1208b(getEllipsis());
        NetworkUtils.m1208b(getString(1233));
        NetworkUtils.m1208b(getString(1234));
        NetworkUtils.m1208b(getString(1038));
        NetworkUtils.m1208b(getString(525044));
        NetworkUtils.m1208b(getString(590588));
        pool[112] = ResourceManager.integerOf(!StringUtils.isKnownDevice1 && !StringUtils.isKnownDevice2 ? 1 : 0);
        try {
            setBool(1535, Display.getDisplay(getMidlet()).numAlphaLevels() > 2);
        } catch (Throwable unused5) {
        }
    }

    /* renamed from: a */
    public static final boolean hasMemory() {
        return Runtime.getRuntime().totalMemory() > 1572864;
    }

    /* renamed from: b */
    public static final void updateTime() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        setLong(1530, jCurrentTimeMillis);
        setBool(1534, (((int) jCurrentTimeMillis) & Integer.MAX_VALUE) % 2000 < 1000);
    }

    /* renamed from: a */
    public static final byte[] getBytes(int i) {
        return (byte[]) pool[i];
    }

    /* renamed from: c */
    public static final MainCanvas getCanvas() {
        return (MainCanvas) pool[1371];
    }

    /* renamed from: p */
    private static final Object getOrDefault(int i) {
        Object obj;
        return (i >= 295 || (obj = delta[i]) == null) ? pool[i] : obj;
    }

    /* renamed from: b */
    public static final String getString(int i) {
        if (i > 5179) {
            return StringUtils.intern(new String(getBytes(295), i & 65535, i >> 16));
        }
        Object objM583p = getOrDefault(i);
        if (objM583p == null) {
            return null;
        }
        return objM583p instanceof byte[] ? NetworkUtils.bytesToString((byte[]) objM583p) : (String) objM583p;
    }

    /* renamed from: c */
    public static final int getAndClearInt(int i) {
        int iM586d = getInt(i);
        setInt(i, 0);
        return iM586d;
    }

    /* renamed from: d */
    public static final int getInt(int i) {
        return i < 1406 ? ((Integer) getOrDefault(i)).intValue() : intPool[i - 1406];
    }

    /* renamed from: e */
    public static final boolean getBool(int i) {
        return getInt(i) != 0;
    }

    /* renamed from: a */
    public static final void setFromBuffer(int i, StringBuffer stringBuffer) {
        setObject(i, (Object) NetworkUtils.bufToStringCached(stringBuffer));
    }

    /* renamed from: a */
    public static final void setFromPool(int i, int i2) {
        setObject(i, (Object) getString(i2));
    }

    /* renamed from: b */
    public static final void clearRange(int i, int i2) {
        while (i <= i2) {
            int i3 = i;
            i++;
            clearIndex(i3);
        }
    }

    /* renamed from: f */
    public static final void clearIndex(int i) {
        if (i >= 295) {
            pool[i] = null;
        } else {
            delta[i] = null;
        }
    }

    /* renamed from: a */
    public static final void setString(int i, String str) {
        setObject(i, (Object) Utils.defaultStr(str));
    }

    /* renamed from: b */
    public static final void setStringInd(int i, String str) {
        setObject(getInt(i), (Object) str);
    }

    /* renamed from: c */
    public static final void setInt(int i, int i2) {
        if (i < 1406) {
            setObject(i, ResourceManager.integerOf(i2));
        } else {
            intPool[i - 1406] = i2;
        }
    }

    /* renamed from: d */
    public static final void addInt(int i, int i2) {
        setInt(i, getInt(i) + i2);
    }

    /* renamed from: e */
    public static final void setIntInd(int i, int i2) {
        setInt(getInt(i), i2);
    }

    /* renamed from: a */
    public static final void setLong(int i, long j) {
        setInt(i, (int) (j >>> 32));
        setInt(i + 1, (int) j);
    }

    /* renamed from: g */
    public static final long getLong(int i) {
        return (getInt(i) << 32) | (getInt(i + 1) & 4294967295L);
    }

    /* renamed from: a */
    public static final boolean setBool(int i, boolean z) {
        setInt(i, z ? 1 : 0);
        return z;
    }

    /* renamed from: h */
    public static final boolean toggleBool(int i) {
        boolean z = !getBool(i);
        boolean z2 = z;
        setBool(i, z);
        return z2;
    }

    /* renamed from: a */
    public static final Object setObject(int i, Object obj) {
        if (i >= delta.length) {
            pool[i] = obj;
        } else {
            Object obj2 = pool[i];
            if (obj2 == null && obj != null) {
                delta[i] = obj;
            } else if (obj2 == null || obj2.equals(obj)) {
                delta[i] = null;
            } else {
                delta[i] = obj;
            }
        }
        return obj;
    }

    /* renamed from: d */
    public static final Midlet getMidlet() {
        return (Midlet) pool[1366];
    }

    /* renamed from: i */
    public static final String getAppProperty(int i) {
        return StringUtils.intern(getMidlet().getAppProperty(getString(i)));
    }

    /* renamed from: b */
    public static final void setScreen(Object obj) {
        currentScreen = obj;
        AppController.m304a(0, AppController.m376E());
    }

    /* renamed from: e */
    public static final int getHeight() {
        return getInt(1529) - (getBool(71) ? getInt(1450) + 2 : 0);
    }

    /* renamed from: f */
    public static final void setDimensions(int i, int i2) {
        setInt(1528, i);
        setInt(1529, i2);
    }

    /* renamed from: j */
    public static final void resetToEmpty(int i) {
        setObject(i, (Object) emptyStr);
    }

    /* renamed from: k */
    public static final GraphicsContext getGfxContext(int i) {
        return (GraphicsContext) pool[i + 1273];
    }

    /* renamed from: l */
    public static final Object[] getObjectArray(int i) {
        return (Object[]) pool[i];
    }

    /* renamed from: f */
    public static final ContactGroup getCurrentGroup() {
        return (ContactGroup) pool[1365];
    }

    /* renamed from: g */
    public static final Contact getCurrentContact() {
        return (Contact) pool[1365];
    }

    /* renamed from: h */
    public static final MrimContact getCurrentMrimContact() {
        return (MrimContact) pool[1365];
    }

    /* renamed from: c */
    public static final void setCurrentEntity(Object obj) {
        pool[1365] = obj;
    }

    /* renamed from: m */
    public static final Vector getVector(int i) {
        return (Vector) pool[i];
    }

    /* renamed from: n */
    public static final Image getImage(int i) {
        return (Image) pool[i];
    }

    /* renamed from: i */
    public static final Account getAccount() {
        return (Account) pool[1281];
    }

    /* renamed from: d */
    public static final void setAccount(Object obj) {
        pool[1281] = obj;
    }

    /* renamed from: a */
    private static final Object decodeObject(ByteBuffer c0043n, int i) {
        byte bM1344o = c0043n.readByte();
        if ((bM1344o & 128) != 0) {
            byte[] bArr = new byte[(bM1344o & 64) != 0 ? bM1344o & 63 : ((bM1344o & 31) << 8) + c0043n.readUByte()];
            c0043n.readIntoBytes(bArr);
            if (i >= 295 && i < 1036) {
                return bArr;
            }
            StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
            for (byte b : bArr) {
                stringBufferM1217h.append(Utils.m499a((int) b));
            }
            NetworkUtils.releaseBytes(bArr);
            String str = separator;
            String strM1215a = NetworkUtils.bufToStringCached(stringBufferM1217h);
            if (str.equals(strM1215a)) {
                return null;
            }
            return strM1215a;
        }
        if ((bM1344o & 64) != 0) {
            return ResourceManager.integerOf(bM1344o & 63);
        }
        if ((bM1344o & 32) != 0) {
            return ResourceManager.integerOf(((bM1344o & 31) << 8) + c0043n.readUByte());
        }
        int iM1346q = 0;
        int i2 = bM1344o & 7;
        while (true) {
            i2--;
            if (i2 < 0) {
                return ResourceManager.integerOf(iM1346q);
            }
            iM1346q = (iM1346q << 8) + c0043n.readUByte();
        }
    }

    /* renamed from: a */
    public static void saveDelta(boolean z) {
        try {
            ByteBuffer c0043n = new ByteBuffer();
            for (int i = 0; i < 295; i++) {
                Object obj = delta[i];
                if (obj != null) {
                    encodeIndex(c0043n, i);
                    if (obj instanceof String) {
                        String str = (String) obj;
                        int length = str.length();
                        byte[] bArr = new byte[length];
                        for (int i2 = 0; i2 < length; i2++) {
                            bArr[i2] = Utils.m500b(str.charAt(i2));
                        }
                        int length2 = str.length();
                        if (length2 <= 0 || length2 >= 64) {
                            c0043n.writeShortBE(length2 | 32768);
                        } else {
                            c0043n.writeByte(192 | length2);
                        }
                        c0043n.writeBytes(bArr);
                    } else {
                        encodeIndex(c0043n, ((Integer) obj).intValue());
                    }
                }
            }
            XmppMailRuProtocol.writeRecord(NetworkUtils.longToHex(1164404323), c0043n, z);
        } catch (Throwable unused) {
        }
    }

    /* renamed from: j */
    public static final String getEllipsis() {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append((char) 8230));
    }

    /* renamed from: b */
    private static final void encodeIndex(ByteBuffer c0043n, int i) {
        if (i >= 0 && i <= 63) {
            c0043n.writeByte(64 | i);
            return;
        }
        ByteBuffer c0043n2 = new ByteBuffer();
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
            c0043n2.writeByte(iArr[i5]);
        }
        byte[] bArrM1339k = c0043n2.toByteArray();
        c0043n.writeByte(8 | bArrM1339k.length);
        c0043n.writeBytes(bArrM1339k);
    }

    /* renamed from: k */
    public static final Calendar getCalendar() {
        Calendar calendar = (Calendar) pool[1367];
        Date date = (Date) pool[1368];
        date.setTime((getLong(1530) - getLong(1532)) + ((getInt(246) - 13) * 3600000));
        calendar.setTime(date);
        return calendar;
    }

    /* renamed from: o */
    public static final int getIntOffset(int i) {
        return getInt(i + 1450);
    }

    /* renamed from: l */
    public static final int getDateCode() {
        Calendar calendarM622k = getCalendar();
        return (calendarM622k.get(1) << 16) + (calendarM622k.get(2) << 8) + calendarM622k.get(5);
    }

    /* renamed from: m */
    public static final Font getFont() {
        return ((GraphicsContext) pool[1273]).font;
    }

    /* renamed from: a */
    public static final int indexOf(String str, int i) {
        return str.indexOf(NetworkUtils.longToHex(i));
    }

    /* renamed from: a */
    public static final int indexOfLong(String str, long j) {
        return str.indexOf(NetworkUtils.longToHex(j));
    }

    /* renamed from: b */
    public static final int indexOfPool(String str, int i) {
        return str.indexOf(getString(i));
    }
}
