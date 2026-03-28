package com.trykote.mobileagent.core;


import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
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
        int value;
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
        ObjectPool.bytePool = new byte[20][];
        ObjectPool.bufferPool = new StringBuffer[5];
        ObjectPool.vectorPool = new Vector[5];
        ObjectPool.stringCache = new Hashtable();
        IOUtils.openResources = ObjectPool.newVector();
        separator = ObjectPool.unpackChars(1819047278);
        emptyBytes = new byte[0];
        delta = new Object[295];
        pool = new Object[1406];
        intPool = new int[3777];
        // Non-zero defaults for runtime variables (intPool[0..171])
        intPool[1417 - 1406] = 1;
        intPool[1420 - 1406] = 1;
        intPool[SessionKeys.FLAG_MRIM_DATA_LOADED - 1406] = 1;
        intPool[1461 - 1406] = 1;
        intPool[SessionKeys.FLAG_HAS_XMPP_ACCOUNTS - 1406] = 1;
        intPool[SessionKeys.FLAG_CAPTCHA_SHOWN - 1406] = 1;
        intPool[MapKeys.INT_MAP_SCROLL_DIRECTION - 1406] = -1;
        intPool[1571 - 1406] = 400;
        ByteBuffer buffer = new ByteBuffer(ObjectPool.unpackChars(1734763311), 45000);
        for (int i2 = 0; i2 < 1406; i2++) {
            pool[i2] = decodeObject(buffer, i2);
        }
        for (int i3 = 0; i3 < 3605; i3++) {
            int[] iArr = intPool;
            int i4 = i3 + 172;
            byte flag = buffer.readByte();
            if ((flag & 64) != 0) {
                value = flag & 63;
            } else if ((flag & 32) != 0) {
                value = ((flag & 31) << 8) + buffer.readUByte();
            } else {
                int accum = 0;
                int i5 = flag & 7;
                while (true) {
                    i5--;
                    if (i5 < 0) {
                        break;
                    } else {
                        accum = (accum << 8) + buffer.readUByte();
                    }
                }
                value = accum;
            }
            iArr[i4] = value;
        }
        emptyStr = (String) pool[StringResKeys.STR_EMPTY];
        ByteBuffer recordBuf = ChunkedRecordStore.readChunkedRecord(ObjectPool.unpackChars(1164404323));
        RemoteLogger.log("PERSIST", "delta RMS read: " + recordBuf.length + " bytes");
        while (recordBuf.length > 0) {
            try {
                delta[((Integer) decodeObject(recordBuf, 0)).intValue()] = decodeObject(recordBuf, 0);
            } catch (Throwable unused) {
            }
        }
        RemoteLogger.log("PERSIST", "delta[0]=" + delta[0]);
        if (delta[0] == null) {
            RemoteLogger.log("PERSIST", "FACTORY RESET — deleting all record stores!");
            delta = new Object[295];
            try {
                String[] stores = StringUtils.listRecordStores();
                if (stores != null) {
                    int length = stores.length;
                    while (true) {
                        length--;
                        if (length < 0) {
                            break;
                        } else {
                            try {
                                RecordStore.deleteRecordStore(stores[length]);
                            } catch (Throwable unused3) {
                            }
                        }
                    }
                }
            } catch (Throwable unused4) {
            }
            setInt(TrafficKeys.DELTA_VERSION, 3096);
        }
        if (((Integer) delta[0]).intValue() != 3096) {
            throw new RuntimeException();
        }
        setInt(TrafficKeys.DELTA_VERSION, 3096);
        setObject(StringResKeys.STR_SEPARATOR, (Object) separator);
        pool[SessionKeys.OBJ_MIDLET] = obj;
        pool[SessionKeys.ARR_EMPTY_INT] = new int[0];
        Date date = new Date();
        pool[SessionKeys.OBJ_DATE] = date;
        pool[SessionKeys.OBJ_CALENDAR] = Calendar.getInstance();
        setLong(SessionKeys.TIMESTAMP_OFFSET, date.getTime() - System.currentTimeMillis());
        updateTime();
        pool[SessionKeys.OBJ_RANDOM] = new Random(System.currentTimeMillis() ^ Thread.currentThread().hashCode());
        pool[UIKeys.OBJ_GFX_CONTEXTS_ARRAY] = new Object[58];
        pool[UIKeys.ARR_GFX_HEIGHTS] = new int[29];
        pool[SessionKeys.VEC_EVENT_QUEUE] = ObjectPool.newVector();
        // Event type arrays removed — replaced by CommandEvent singletons
        StringUtils.initPlatform();
        TimerManager.timers = new long[14];
        pool[SessionKeys.OBJ_CALLBACK_ARRAY] = new Object[1];
        ObjectPool.cacheString(separator);
        ObjectPool.cacheString(getEllipsis());
        ObjectPool.cacheString(getString(StringResKeys.STR_PHONE_SUFFIX));
        ObjectPool.cacheString(getString(StringResKeys.STR_PHONE_PREFIX));
        ObjectPool.cacheString(getString(StringResKeys.STR_EMPTY));
        ObjectPool.cacheString(getString(StringResKeys.STR_RES_CONTENT_TYPE));
        ObjectPool.cacheString(getString(StringResKeys.STR_RES_HTTP_METHOD));
        pool[SettingsKeys.SETTING_COMPRESSION_ENABLED] = ResourceManager.integerOf(!StringUtils.isKnownDevice1 && !StringUtils.isKnownDevice2 ? 1 : 0);
        try {
            setBool(UIKeys.FLAG_SUPPORTS_ALPHA, Display.getDisplay(getMidlet()).numAlphaLevels() > 2);
        } catch (Throwable unused5) {
        }
    }

    /* renamed from: a */
    public static final boolean hasMemory() {
        return Runtime.getRuntime().totalMemory() > 1572864;
    }

    /* renamed from: b */
    public static final void updateTime() {
        long now = System.currentTimeMillis();
        setLong(SessionKeys.TIMESTAMP_CURRENT, now);
        setBool(UIKeys.FLAG_BLINK_STATE, (((int) now) & Integer.MAX_VALUE) % 2000 < 1000);
    }

    /* renamed from: a */
    public static final byte[] getBytes(int i) {
        return (byte[]) pool[i];
    }

    /* renamed from: c */
    public static final MainCanvas getCanvas() {
        return (MainCanvas) pool[SessionKeys.OBJ_CANVAS];
    }

    /* renamed from: p */
    private static final Object getOrDefault(int i) {
        Object obj;
        return (i >= 295 || (obj = delta[i]) == null) ? pool[i] : obj;
    }

    /* renamed from: b */
    public static final String getString(int i) {
        if (i > 5179) {
            return StringUtils.intern(new String(getBytes(StringResKeys.RES_STRING_DATA), i & 65535, i >> 16));
        }
        Object result = getOrDefault(i);
        if (result == null) {
            return null;
        }
        return result instanceof byte[] ? ObjectPool.decodeWin1251((byte[]) result) : (String) result;
    }

    /* renamed from: c */
    public static final int getAndClearInt(int i) {
        int val = getInt(i);
        setInt(i, 0);
        return val;
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
        setObject(i, (Object) ObjectPool.toStringAndRelease(stringBuffer));
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
        return (Midlet) pool[SessionKeys.OBJ_MIDLET];
    }

    /* renamed from: i */
    public static final String getAppProperty(int i) {
        return StringUtils.intern(getMidlet().getAppProperty(getString(i)));
    }

    /* renamed from: b */
    public static final void setScreen(Object obj) {
        currentScreen = obj;
        TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
    }

    /* renamed from: e */
    public static final int getHeight() {
        return getInt(UIKeys.INT_SCREEN_HEIGHT) - (getBool(SettingsKeys.SETTING_STATUS_BAR_VISIBLE) ? getInt(UIKeys.INT_FONT_HEIGHT) + 2 : 0);
    }

    /* renamed from: f */
    public static final void setDimensions(int i, int i2) {
        setInt(UIKeys.INT_SCREEN_WIDTH, i);
        setInt(UIKeys.INT_SCREEN_HEIGHT, i2);
    }

    /* renamed from: j */
    public static final void resetToEmpty(int i) {
        setObject(i, (Object) emptyStr);
    }

    /* renamed from: k */
    public static final GraphicsContext getGfxContext(int i) {
        return (GraphicsContext) pool[i + UIKeys.GFX_CONTEXT_BASE];
    }

    /* renamed from: l */
    public static final Object[] getObjectArray(int i) {
        return (Object[]) pool[i];
    }

    /* renamed from: f */
    public static final ContactGroup getCurrentGroup() {
        return (ContactGroup) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    /* renamed from: g */
    public static final Contact getCurrentContact() {
        return (Contact) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    /* renamed from: h */
    public static final MrimContact getCurrentMrimContact() {
        return (MrimContact) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    /* renamed from: c */
    public static final void setCurrentEntity(Object obj) {
        pool[ContactKeys.SLOT_CURRENT_ENTITY] = obj;
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
        return (Account) pool[SessionKeys.SLOT_CURRENT_ACCOUNT];
    }

    /* renamed from: d */
    public static final void setAccount(Object obj) {
        pool[SessionKeys.SLOT_CURRENT_ACCOUNT] = obj;
    }

    /* renamed from: a */
    private static final Object decodeObject(ByteBuffer buffer, int i) {
        byte flag = buffer.readByte();
        if ((flag & 128) != 0) {
            byte[] bArr = new byte[(flag & 64) != 0 ? flag & 63 : ((flag & 31) << 8) + buffer.readUByte()];
            buffer.readIntoBytes(bArr);
            if (i >= 295 && i < 1036) {
                return bArr;
            }
            StringBuffer sb = ObjectPool.newStringBuffer();
            for (byte b : bArr) {
                sb.append(Utils.win1251ToChar((int) b));
            }
            ObjectPool.releaseBytes(bArr);
            String str = separator;
            String decoded = ObjectPool.toStringAndRelease(sb);
            if (str.equals(decoded)) {
                return null;
            }
            return decoded;
        }
        if ((flag & 64) != 0) {
            return ResourceManager.integerOf(flag & 63);
        }
        if ((flag & 32) != 0) {
            return ResourceManager.integerOf(((flag & 31) << 8) + buffer.readUByte());
        }
        int value = 0;
        int i2 = flag & 7;
        while (true) {
            i2--;
            if (i2 < 0) {
                return ResourceManager.integerOf(value);
            }
            value = (value << 8) + buffer.readUByte();
        }
    }

    /* renamed from: a */
    public static void saveDelta(boolean z) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            for (int i = 0; i < 295; i++) {
                Object obj = delta[i];
                if (obj != null) {
                    encodeIndex(buffer, i);
                    if (obj instanceof String) {
                        String str = (String) obj;
                        int length = str.length();
                        byte[] bArr = new byte[length];
                        for (int i2 = 0; i2 < length; i2++) {
                            bArr[i2] = Utils.charToWin1251(str.charAt(i2));
                        }
                        int length2 = str.length();
                        if (length2 <= 0 || length2 >= 64) {
                            buffer.writeShortBE(length2 | 32768);
                        } else {
                            buffer.writeByte(192 | length2);
                        }
                        buffer.writeBytes(bArr);
                    } else {
                        encodeIndex(buffer, ((Integer) obj).intValue());
                    }
                }
            }
            RemoteLogger.log("PERSIST", "saveDelta: " + buffer.length + " bytes, chunked=" + z);
            ChunkedRecordStore.writeRecord(ObjectPool.unpackChars(1164404323), buffer, z);
            RemoteLogger.log("PERSIST", "saveDelta: writeRecord done");
        } catch (Throwable th) {
            RemoteLogger.log("PERSIST", "saveDelta FAILED", th);
        }
    }

    /* renamed from: j */
    public static final String getEllipsis() {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append((char) 8230));
    }

    /* renamed from: b */
    private static final void encodeIndex(ByteBuffer buffer, int i) {
        if (i >= 0 && i <= 63) {
            buffer.writeByte(64 | i);
            return;
        }
        ByteBuffer tempBuf = new ByteBuffer();
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
            tempBuf.writeByte(iArr[i5]);
        }
        byte[] bytes = tempBuf.toByteArray();
        buffer.writeByte(8 | bytes.length);
        buffer.writeBytes(bytes);
    }

    /* renamed from: k */
    public static final Calendar getCalendar() {
        Calendar calendar = (Calendar) pool[SessionKeys.OBJ_CALENDAR];
        Date date = (Date) pool[SessionKeys.OBJ_DATE];
        date.setTime((getLong(SessionKeys.TIMESTAMP_CURRENT) - getLong(SessionKeys.TIMESTAMP_OFFSET)) + ((getInt(SettingsKeys.SETTING_TIMEZONE_OFFSET) - 13) * 3600000));
        calendar.setTime(date);
        return calendar;
    }

    /* renamed from: o */
    public static final int getIntOffset(int i) {
        return getInt(i + UIKeys.INT_FONT_HEIGHT);
    }

    /* renamed from: l */
    public static final int getDateCode() {
        Calendar cal = getCalendar();
        return (cal.get(1) << 16) + (cal.get(2) << 8) + cal.get(5);
    }

    /* renamed from: m */
    public static final Font getFont() {
        return ((GraphicsContext) pool[UIKeys.GFX_CONTEXT_BASE]).font;
    }

    /* renamed from: a */
    public static final int indexOf(String str, int i) {
        return str.indexOf(ObjectPool.unpackChars(i));
    }

    /* renamed from: a */
    public static final int indexOfLong(String str, long j) {
        return str.indexOf(ObjectPool.unpackChars(j));
    }

    /* renamed from: b */
    public static final int indexOfPool(String str, int i) {
        return str.indexOf(getString(i));
    }
}
