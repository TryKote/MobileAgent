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
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

public abstract class AppState {

    public static byte[] emptyBytes;
    public static Object[] pool;
    private static Object[] delta;
    private static int[] intPool;
    public static Object currentScreen;
    public static String emptyStr;
    private static String separator;

    // Pool structure sizes
    private static final int DELTA_SIZE = 295;
    private static final int OBJECT_POOL_SIZE = 1406;
    private static final int INT_POOL_SIZE = 3777;
    private static final int SCREEN_DATA_COUNT = 3605;
    private static final int RUNTIME_DEFAULTS_COUNT = 172;

    // Packed string encoding
    public static final int PACKED_STRING_THRESHOLD = 5179;
    private static final int RAW_BYTES_END = 1036;

    // Persistence
    private static final int DELTA_FORMAT_VERSION = 3096;
    private static final int CFG_BUFFER_CAPACITY = 45000;

    // Memory threshold (1.5 MB)
    private static final int MEMORY_THRESHOLD = 1572864;

    // Packed resource identifiers
    private static final int PACKED_SEPARATOR = 1819047278;       // "null" separator marker
    private static final int PACKED_CFG_RESOURCE = 1734763311;    // "/cfg" config binary
    private static final int PACKED_DELTA_STORE = 1164404323;     // "cfgE" delta record store

    // Ellipsis character
    private static final char CHAR_ELLIPSIS = '\u2026';

    // Timezone constants
    private static final int TIMEZONE_UTC_INDEX = 13;
    private static final int MILLIS_PER_HOUR = 3600000;

    // Write-only legacy defaults (from original binary, never read by name)
    private static final int LEGACY_FLAG_1417 = 1417;
    private static final int LEGACY_FLAG_1420 = 1420;
    private static final int LEGACY_FLAG_1461 = 1461;
    private static final int LEGACY_DEFAULT_1571 = 1571;
    private static final int LEGACY_VALUE_1571 = 400;

    // Blink timing
    private static final int BLINK_CYCLE_MS = 2000;
    private static final int BLINK_ON_MS = 1000;

    public static void init(Object midlet) {
        initPools();
        Storage.init();
        initObjectPool();
        Storage.initConstants();
        pool[UIKeys.OBJ_TRANSITION_DATA] = new TransitionData();
        loadDelta();
        initSessionState(midlet);
        initGraphics();
        initCaches();
    }

    private static void initPools() {
        ObjectPool.initCaches();
        StringUtils.initInternCache();
        ObjectPool.initPools();
        IOUtils.initResourceTracking();
        separator = ObjectPool.unpackChars(PACKED_SEPARATOR);
        emptyBytes = new byte[0];
        delta = new Object[DELTA_SIZE];
        pool = new Object[OBJECT_POOL_SIZE];
        intPool = new int[INT_POOL_SIZE];
        // Non-zero defaults for runtime variables (intPool[0..171])
        initRuntimeDefaults();
    }

    private static void initRuntimeDefaults() {
        intPool[LEGACY_FLAG_1417 - OBJECT_POOL_SIZE] = 1;
        intPool[LEGACY_FLAG_1420 - OBJECT_POOL_SIZE] = 1;
        intPool[SessionKeys.FLAG_MRIM_DATA_LOADED - OBJECT_POOL_SIZE] = 1;
        intPool[LEGACY_FLAG_1461 - OBJECT_POOL_SIZE] = 1;
        intPool[SessionKeys.FLAG_HAS_XMPP_ACCOUNTS - OBJECT_POOL_SIZE] = 1;
        intPool[SessionKeys.FLAG_CAPTCHA_SHOWN - OBJECT_POOL_SIZE] = 1;
        intPool[MapKeys.INT_MAP_SCROLL_DIRECTION - OBJECT_POOL_SIZE] = -1;
        intPool[LEGACY_DEFAULT_1571 - OBJECT_POOL_SIZE] = LEGACY_VALUE_1571;
    }

    private static void initObjectPool() {
        ByteBuffer buffer = new ByteBuffer(ObjectPool.unpackChars(PACKED_CFG_RESOURCE), CFG_BUFFER_CAPACITY);
        for (int poolIndex = 0; poolIndex < OBJECT_POOL_SIZE; poolIndex++) {
            pool[poolIndex] = StateCodec.decodeObject(buffer, poolIndex, DELTA_SIZE, RAW_BYTES_END, separator);
        }
        for (int screenIndex = 0; screenIndex < SCREEN_DATA_COUNT; screenIndex++) {
            intPool[screenIndex + RUNTIME_DEFAULTS_COUNT] = StateCodec.decodeInt(buffer);
        }
        emptyStr = (String) pool[StringResKeys.STR_EMPTY];
    }

    private static void loadDelta() {
        ByteBuffer recordBuf = ChunkedRecordStore.readChunkedRecord(ObjectPool.unpackChars(PACKED_DELTA_STORE));
        while (recordBuf.length > 0) {
            try {
                delta[((Integer) StateCodec.decodeObject(recordBuf, 0, DELTA_SIZE, RAW_BYTES_END, separator)).intValue()] = StateCodec.decodeObject(recordBuf, 0, DELTA_SIZE, RAW_BYTES_END, separator);
            } catch (Throwable unused) {
            }
        }
        if (delta[0] == null) {
            delta = new Object[DELTA_SIZE];
            try {
                String[] stores = StringUtils.listRecordStores();
                if (stores != null) {
                    for (int idx = stores.length - 1; idx >= 0; idx--) {
                        try {
                            RecordStore.deleteRecordStore(stores[idx]);
                        } catch (Throwable unused2) {
                        }
                    }
                }
            } catch (Throwable unused3) {
            }
            setInt(TrafficKeys.DELTA_VERSION, DELTA_FORMAT_VERSION);
        }
        if (((Integer) delta[0]).intValue() != DELTA_FORMAT_VERSION) {
            throw new RuntimeException();
        }
        setInt(TrafficKeys.DELTA_VERSION, DELTA_FORMAT_VERSION);
        setObject(StringResKeys.STR_SEPARATOR, separator);
    }

    private static void initSessionState(Object midlet) {
        pool[SessionKeys.OBJ_MIDLET] = midlet;
        pool[SessionKeys.ARR_EMPTY_INT] = new int[0];
        Date date = new Date();
        pool[SessionKeys.OBJ_DATE] = date;
        pool[SessionKeys.OBJ_CALENDAR] = Calendar.getInstance();
        setLong(SessionKeys.TIMESTAMP_OFFSET, date.getTime() - System.currentTimeMillis());
        updateTime();
        pool[SessionKeys.OBJ_RANDOM] = new Random(System.currentTimeMillis() ^ Thread.currentThread().hashCode());
        pool[SessionKeys.VEC_EVENT_QUEUE] = ObjectPool.newVector();
        StringUtils.initPlatform();
        TimerManager.timers = new long[TimerManager.SLOT_COUNT];
        pool[SessionKeys.OBJ_CALLBACK_ARRAY] = new Object[1];
    }

    private static void initGraphics() {
        pool[UIKeys.OBJ_GFX_CONTEXTS_ARRAY] = new Object[UIKeys.GFX_CONTEXT_COUNT];
        pool[UIKeys.ARR_GFX_HEIGHTS] = new int[UIKeys.GFX_HEIGHT_COUNT];
        try {
            setBool(UIKeys.FLAG_SUPPORTS_ALPHA, Display.getDisplay(getMidlet()).numAlphaLevels() > 2);
        } catch (Throwable unused) {
        }
    }

    private static void initCaches() {
        ObjectPool.cacheString(separator);
        ObjectPool.cacheString(getEllipsis());
        ObjectPool.cacheString(getString(StringResKeys.STR_PHONE_SUFFIX));
        ObjectPool.cacheString(getString(StringResKeys.STR_PHONE_PREFIX));
        ObjectPool.cacheString(getString(StringResKeys.STR_EMPTY));
        ObjectPool.cacheString(getString(PackedStringKeys.MRIM_MAPPOINT));
        ObjectPool.cacheString(getString(PackedStringKeys.MRIM_MAPOBJECT));
        pool[SettingsKeys.SETTING_COMPRESSION_ENABLED] = ObjectPool.integerOf(!StringUtils.isKnownDevice1 && !StringUtils.isKnownDevice2 ? 1 : 0);
    }

    public static boolean hasMemory() {
        return Runtime.getRuntime().totalMemory() > MEMORY_THRESHOLD;
    }

    public static void updateTime() {
        long now = System.currentTimeMillis();
        setLong(SessionKeys.TIMESTAMP_CURRENT, now);
        setBool(UIKeys.FLAG_BLINK_STATE, (((int) now) & Integer.MAX_VALUE) % BLINK_CYCLE_MS < BLINK_ON_MS);
    }

    public static byte[] getBytes(int key) {
        return (byte[]) pool[key];
    }

    public static MainCanvas getCanvas() {
        return (MainCanvas) pool[SessionKeys.OBJ_CANVAS];
    }

    private static Object getOrDefault(int key) {
        Object override;
        return (key >= DELTA_SIZE || (override = delta[key]) == null) ? pool[key] : override;
    }

    public static String getString(int key) {
        if (key > PACKED_STRING_THRESHOLD) {
            return StringUtils.intern(new String(getBytes(StringResKeys.RES_STRING_DATA), key & 0xFFFF, key >> 16));
        }
        Object result = getOrDefault(key);
        if (result == null) {
            return null;
        }
        if (result instanceof byte[]) {
            return ObjectPool.decodeWin1251((byte[]) result);
        }
        if (result instanceof String) {
            return (String) result;
        }
        return null;
    }

    public static int getAndClearInt(int key) {
        int val = getInt(key);
        setInt(key, 0);
        return val;
    }

    public static int getInt(int key) {
        if (key >= OBJECT_POOL_SIZE) {
            return intPool[key - OBJECT_POOL_SIZE];
        }
        Object val = getOrDefault(key);
        return val instanceof Integer ? ((Integer) val).intValue() : 0;
    }

    public static boolean getBool(int key) {
        return getInt(key) != 0;
    }

    public static void setFromBuffer(int key, StringBuffer stringBuffer) {
        setObject(key, ObjectPool.toStringAndRelease(stringBuffer));
    }

    public static void setFromPool(int key, int sourceKey) {
        setObject(key, getString(sourceKey));
    }

    public static void clearRange(int from, int to) {
        for (int key = from; key <= to; key++) {
            clearIndex(key);
        }
    }

    public static void clearIndex(int key) {
        if (key >= OBJECT_POOL_SIZE) {
            intPool[key - OBJECT_POOL_SIZE] = 0;
        } else if (key >= DELTA_SIZE) {
            pool[key] = null;
        } else {
            delta[key] = null;
        }
    }

    public static void setString(int key, String value) {
        setObject(key, Utils.defaultStr(value));
    }

    public static void setStringIndirect(int key, String value) {
        setObject(getInt(key), value);
    }

    public static void setInt(int key, int value) {
        if (key < OBJECT_POOL_SIZE) {
            setObject(key, ObjectPool.integerOf(value));
        } else {
            intPool[key - OBJECT_POOL_SIZE] = value;
        }
    }

    public static void addInt(int key, int value) {
        setInt(key, getInt(key) + value);
    }

    public static void setIntIndirect(int key, int value) {
        setInt(getInt(key), value);
    }

    public static void setLong(int key, long value) {
        setInt(key, (int) (value >>> 32));
        setInt(key + 1, (int) value);
    }

    public static long getLong(int key) {
        return (getInt(key) << 32) | (getInt(key + 1) & 0xFFFFFFFFL);
    }

    public static boolean setBool(int key, boolean value) {
        setInt(key, value ? 1 : 0);
        return value;
    }

    public static boolean toggleBool(int key) {
        boolean newVal = !getBool(key);
        setBool(key, newVal);
        return newVal;
    }

    public static Object setObject(int key, Object value) {
        if (key >= delta.length) {
            pool[key] = value;
        } else {
            Object poolValue = pool[key];
            if (poolValue == null && value != null) {
                delta[key] = value;
            } else if (poolValue == null || poolValue.equals(value)) {
                delta[key] = null;
            } else {
                delta[key] = value;
            }
        }
        return value;
    }

    public static Midlet getMidlet() {
        return (Midlet) pool[SessionKeys.OBJ_MIDLET];
    }

    public static String getAppProperty(int key) {
        return StringUtils.intern(getMidlet().getAppProperty(getString(key)));
    }

    public static void setScreen(Object screen) {
        Storage.currentScreen = screen;
        TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
    }

    public static int getHeight() {
        return getInt(UIKeys.INT_SCREEN_HEIGHT) - (getBool(SettingsKeys.SETTING_STATUS_BAR_VISIBLE) ? getInt(UIKeys.INT_FONT_HEIGHT) + 2 : 0);
    }

    public static void setDimensions(int width, int height) {
        setInt(UIKeys.INT_SCREEN_WIDTH, width);
        setInt(UIKeys.INT_SCREEN_HEIGHT, height);
    }

    public static void resetToEmpty(int key) {
        setObject(key, emptyStr);
    }

    public static GraphicsContext getGfxContext(int index) {
        return (GraphicsContext) pool[index + UIKeys.GFX_CONTEXT_BASE];
    }

    public static Object[] getObjectArray(int key) {
        return (Object[]) pool[key];
    }

    public static ContactGroup getCurrentGroup() {
        return (ContactGroup) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    public static Contact getCurrentContact() {
        return (Contact) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    public static MrimContact getCurrentMrimContact() {
        return (MrimContact) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    public static void setCurrentEntity(Object entity) {
        pool[ContactKeys.SLOT_CURRENT_ENTITY] = entity;
    }

    public static Vector getVector(int key) {
        return (Vector) pool[key];
    }

    public static Image getImage(int key) {
        return (Image) pool[key];
    }

    public static Account getAccount() {
        return (Account) pool[SessionKeys.SLOT_CURRENT_ACCOUNT];
    }

    public static void setAccount(Object account) {
        pool[SessionKeys.SLOT_CURRENT_ACCOUNT] = account;
    }

    public static void saveDelta(boolean chunked) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            for (int key = 0; key < DELTA_SIZE; key++) {
                Object value = delta[key];
                if (value != null) {
                    StateCodec.encodeIndex(buffer, key);
                    if (value instanceof String) {
                        StateCodec.encodeString(buffer, (String) value);
                    } else {
                        StateCodec.encodeIndex(buffer, ((Integer) value).intValue());
                    }
                }
            }
            ChunkedRecordStore.writeRecord(ObjectPool.unpackChars(PACKED_DELTA_STORE), buffer, chunked);
        } catch (Throwable unused) {
        }
    }

    public static String getEllipsis() {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(CHAR_ELLIPSIS));
    }

    public static Calendar getCalendar() {
        Calendar calendar = (Calendar) pool[SessionKeys.OBJ_CALENDAR];
        Date date = (Date) pool[SessionKeys.OBJ_DATE];
        date.setTime((getLong(SessionKeys.TIMESTAMP_CURRENT) - getLong(SessionKeys.TIMESTAMP_OFFSET)) + ((getInt(SettingsKeys.SETTING_TIMEZONE_OFFSET) - TIMEZONE_UTC_INDEX) * MILLIS_PER_HOUR));
        calendar.setTime(date);
        return calendar;
    }

    public static int getIntOffset(int index) {
        return getInt(index + UIKeys.INT_FONT_HEIGHT);
    }

    public static int getDateCode() {
        Calendar cal = getCalendar();
        return (cal.get(Calendar.YEAR) << 16) + (cal.get(Calendar.MONTH) << 8) + cal.get(Calendar.DAY_OF_MONTH);
    }

    public static Font getFont() {
        return ((GraphicsContext) pool[UIKeys.GFX_CONTEXT_BASE]).font;
    }
}
