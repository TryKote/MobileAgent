package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.ContactKeys;
import com.trykote.mobileagent.key.MapKeys;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.SessionKeys;
import com.trykote.mobileagent.key.SettingsKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.key.TrafficKeys;
import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.net.TrafficAccounting;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.GraphicsContext;
import com.trykote.mobileagent.ui.MainCanvas;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ChunkedRecordStore;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

/**
 * Central state management for the application.
 *
 * Owns the shared backing arrays (pool, delta, intPool), provides generic
 * typed accessors, initialization, persistence, and compound accessors.
 * Domain facades extend this class and add domain-specific typed methods.
 */
public abstract class AppState {

    // --- Shared backing arrays ---

    public static Object[] pool;
    protected static Object[] delta;
    protected static int[] intPool;
    public static byte[] emptyBytes;
    public static String emptyStr;

    /** Pending screen to display on next repaint. */
    public static Object currentScreen;

    // --- Pool structure sizes ---

    static final int DELTA_SIZE = 295;
    static final int OBJECT_POOL_SIZE = 1406;
    static final int INT_POOL_SIZE = 3494; // covers keys 1406..4899

    // Packed string encoding
    public static final int PACKED_STRING_THRESHOLD = 5179;

    // --- Persistence ---

    static final int DELTA_FORMAT_VERSION = 3098;
    private static final int DELTA_TYPE_INT = 1;
    private static final int DELTA_TYPE_STRING = 2;

    // --- Per-domain delta store names ---

    public static final String STORE_TRAFFIC = "trf";
    public static final String STORE_MAP = "geo";
    public static final String STORE_SETTINGS = "set";
    static final String STORE_SESSION = "ses";

    // --- Per-domain delta ranges (inclusive) ---

    public static final int RANGE_TRAFFIC_START = 0;
    public static final int RANGE_TRAFFIC_END = 33;
    public static final int RANGE_MAP_START = 34;
    public static final int RANGE_MAP_END = 44;
    public static final int RANGE_SETTINGS_START = 45;
    public static final int RANGE_SETTINGS_END = 114;
    static final int RANGE_SESSION_START = 115;

    // --- Init constants ---

    private static final int PACKED_SEPARATOR = 1819047278;
    private static final char CHAR_ELLIPSIS = '\u2026';
    private static final int MEMORY_THRESHOLD = 1572864;
    private static final int TIMEZONE_UTC_INDEX = 13;
    private static final int MILLIS_PER_HOUR = 3600000;
    private static final int LEGACY_FLAG_1417 = 1417;
    private static final int LEGACY_FLAG_1420 = 1420;
    private static final int LEGACY_FLAG_1461 = 1461;
    private static final int LEGACY_DEFAULT_1571 = 1571;
    private static final int LEGACY_VALUE_1571 = 400;
    private static final int BLINK_CYCLE_MS = 2000;
    private static final int BLINK_ON_MS = 1000;

    private static String separator;

    // --- Per-domain persistence config (override in subclasses with own store) ---

    protected String storeName() { return null; }

    protected int deltaStart() { return -1; }

    protected int deltaEnd() { return -1; }

    // --- Inherited persistence methods (no-op if storeName is null) ---

    public void saveDelta() {
        String store = storeName();
        if (store != null) {
            saveDeltaRange(store, deltaStart(), deltaEnd());
        }
    }

    public void loadDelta() {
        String store = storeName();
        if (store != null) {
            loadDomainDelta(store);
        }
    }

    // ========================== Initialization ==========================

    public static void init(Object midlet) {
        RemoteLogger.log("INIT", "initPools");
        initPools();
        RemoteLogger.log("INIT", "initObjectPool");
        initObjectPool();
        pool[UIKeys.OBJ_TRANSITION_DATA] = new TransitionData();
        RemoteLogger.log("INIT", "loadAllDeltas");
        loadAllDeltas();
        RemoteLogger.log("INIT", "initSessionState");
        initSessionState(midlet);
        RemoteLogger.log("INIT", "initGraphics");
        initGraphics();
        RemoteLogger.log("INIT", "initCaches");
        initCaches();
        RemoteLogger.log("INIT", "init done");
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
        PoolInit.init(pool);
        emptyStr = (String) pool[StringResKeys.STR_EMPTY];
    }

    private static void loadAllDeltas() {
        RemoteLogger.log("DELTA", "loading trf");
        TrafficAccounting.INSTANCE.loadDelta();
        RemoteLogger.log("DELTA", "after trf: delta[0]=" + delta[0] + " type=" + (delta[0] == null ? "null" : delta[0].getClass().getName()));
        RemoteLogger.log("DELTA", "loading geo");
        MapState.INSTANCE.loadDelta();
        RemoteLogger.log("DELTA", "loading set");
        SettingsState.INSTANCE.loadDelta();
        RemoteLogger.log("DELTA", "loading ses");
        loadDomainDelta(STORE_SESSION);
        RemoteLogger.log("DELTA", "after ses: delta[0]=" + delta[0] + " type=" + (delta[0] == null ? "null" : delta[0].getClass().getName()));
        if (delta[0] == null) {
            RemoteLogger.log("DELTA", "delta[0] is null, fresh install — clearing all stores");
            delta = new Object[DELTA_SIZE];
            try {
                String[] stores = StringUtils.listRecordStores();
                if (stores != null) {
                    for (int idx = stores.length - 1; idx >= 0; idx--) {
                        try {
                            RecordStore.deleteRecordStore(stores[idx]);
                        } catch (Throwable unused) {
                        }
                    }
                }
            } catch (Throwable unused) {
            }
            setInt(TrafficKeys.DELTA_VERSION, DELTA_FORMAT_VERSION);
        }
        RemoteLogger.log("DELTA", "version check: delta[0]=" + delta[0] + " type=" + (delta[0] == null ? "null" : delta[0].getClass().getName()));
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

    // ========================== Generic accessors ==========================

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

    public static long getLong(int key) {
        return (getInt(key) << 32) | (getInt(key + 1) & 0xFFFFFFFFL);
    }

    public static byte[] getBytes(int key) {
        Object value = pool[key];
        if (value instanceof String) {
            String s = (String) value;
            byte[] result = new byte[s.length()];
            for (int i = 0; i < s.length(); i++) {
                result[i] = Utils.charToWin1251(s.charAt(i));
            }
            return result;
        }
        return (byte[]) value;
    }

    public static int getAndClearInt(int key) {
        int val = getInt(key);
        setInt(key, 0);
        return val;
    }

    public static Object getObject(int key) {
        return pool[key];
    }

    public static Vector getVector(int key) {
        return (Vector) pool[key];
    }

    public static Image getImage(int key) {
        return (Image) pool[key];
    }

    public static Object[] getObjectArray(int key) {
        return (Object[]) pool[key];
    }

    // --- Writes ---

    public static void setInt(int key, int value) {
        if (key < OBJECT_POOL_SIZE) {
            setObject(key, ObjectPool.integerOf(value));
        } else {
            intPool[key - OBJECT_POOL_SIZE] = value;
        }
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

    public static void setString(int key, String value) {
        setObject(key, Utils.defaultStr(value));
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

    public static void setLong(int key, long value) {
        setInt(key, (int) (value >>> 32));
        setInt(key + 1, (int) value);
    }

    public static void addInt(int key, int value) {
        setInt(key, getInt(key) + value);
    }

    public static void setFromBuffer(int key, StringBuffer stringBuffer) {
        setObject(key, ObjectPool.toStringAndRelease(stringBuffer));
    }

    public static void setFromPool(int key, int sourceKey) {
        setObject(key, getString(sourceKey));
    }

    public static void setStringIndirect(int key, String value) {
        setObject(getInt(key), value);
    }

    public static void setIntIndirect(int key, int value) {
        setInt(getInt(key), value);
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

    public static void clearRange(int from, int to) {
        for (int key = from; key <= to; key++) {
            clearIndex(key);
        }
    }

    public static void resetToEmpty(int key) {
        setObject(key, emptyStr);
    }

    // --- Protected pool-direct access for typed casts ---

    protected static Object getPoolObject(int key) {
        return pool[key];
    }

    protected static void setPoolDirect(int key, Object value) {
        pool[key] = value;
    }

    // ========================== Compound accessors ==========================

    public static boolean hasMemory() {
        return Runtime.getRuntime().totalMemory() > MEMORY_THRESHOLD;
    }

    public static void updateTime() {
        long now = System.currentTimeMillis();
        setLong(SessionKeys.TIMESTAMP_CURRENT, now);
        setBool(UIKeys.FLAG_BLINK_STATE, (((int) now) & Integer.MAX_VALUE) % BLINK_CYCLE_MS < BLINK_ON_MS);
    }

    public static MainCanvas getCanvas() {
        return (MainCanvas) pool[SessionKeys.OBJ_CANVAS];
    }

    public static Midlet getMidlet() {
        return (Midlet) pool[SessionKeys.OBJ_MIDLET];
    }

    public static String getAppProperty(int key) {
        return StringUtils.intern(getMidlet().getAppProperty(getString(key)));
    }

    public static void setScreen(Object screen) {
        currentScreen = screen;
        TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
    }

    public static int getHeight() {
        return getInt(UIKeys.INT_SCREEN_HEIGHT) - (SettingsState.isStatusBarVisible() ? getInt(UIKeys.INT_FONT_HEIGHT) + 2 : 0);
    }

    public static void setDimensions(int width, int height) {
        setInt(UIKeys.INT_SCREEN_WIDTH, width);
        setInt(UIKeys.INT_SCREEN_HEIGHT, height);
    }

    public static GraphicsContext getGfxContext(int index) {
        return (GraphicsContext) pool[index + UIKeys.GFX_CONTEXT_BASE];
    }

    public static int getIntOffset(int index) {
        return getInt(index + UIKeys.INT_FONT_HEIGHT);
    }

    public static Font getFont() {
        return ((GraphicsContext) pool[UIKeys.GFX_CONTEXT_BASE]).font;
    }

    public static Account getAccount() {
        return (Account) pool[SessionKeys.SLOT_CURRENT_ACCOUNT];
    }

    public static void setAccount(Object account) {
        pool[SessionKeys.SLOT_CURRENT_ACCOUNT] = account;
    }

    public static ContactGroup getCurrentGroup() {
        return (ContactGroup) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    public static Contact getCurrentContact() {
        return (Contact) pool[ContactKeys.SLOT_CURRENT_ENTITY];
    }

    public static void setCurrentEntity(Object entity) {
        pool[ContactKeys.SLOT_CURRENT_ENTITY] = entity;
    }

    public static Calendar getCalendar() {
        Calendar calendar = (Calendar) pool[SessionKeys.OBJ_CALENDAR];
        Date date = (Date) pool[SessionKeys.OBJ_DATE];
        date.setTime((getLong(SessionKeys.TIMESTAMP_CURRENT) - getLong(SessionKeys.TIMESTAMP_OFFSET)) + ((SettingsState.getTimezoneOffset() - TIMEZONE_UTC_INDEX) * MILLIS_PER_HOUR));
        calendar.setTime(date);
        return calendar;
    }

    public static int getDateCode() {
        Calendar cal = getCalendar();
        return (cal.get(Calendar.YEAR) << 16) + (cal.get(Calendar.MONTH) << 8) + cal.get(Calendar.DAY_OF_MONTH);
    }

    public static String getEllipsis() {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(CHAR_ELLIPSIS));
    }

    // ========================== Persistence orchestration ==========================

    public static void saveAllDeltas(boolean chunked) {
        TrafficAccounting.INSTANCE.saveDelta();
        MapState.INSTANCE.saveDelta();
        SettingsState.INSTANCE.saveDelta();
        saveDeltaRange(STORE_SESSION, RANGE_SESSION_START, DELTA_SIZE - 1);
    }

    public static void loadDomainDelta(String storeName) {
        ByteBuffer buf = ChunkedRecordStore.readChunkedRecord(storeName);
        RemoteLogger.log("DELTA", "loadDomainDelta(" + storeName + "): " + buf.length + " bytes");
        if (buf.length >= 2) {
            int version = buf.readShortBE();
            RemoteLogger.log("DELTA", "  version=" + version + " expected=" + DELTA_FORMAT_VERSION);
            if (version == DELTA_FORMAT_VERSION) {
                while (buf.length > 0) {
                    try {
                        int type = buf.readUByte();
                        int key = buf.readShortBE();
                        if (type == DELTA_TYPE_INT) {
                            int val = buf.readIntBE();
                            RemoteLogger.log("DELTA", "  [" + storeName + "] key=" + key + " INT=" + val);
                            delta[key] = ObjectPool.integerOf(val);
                        } else {
                            String val = buf.readVarLenStr();
                            RemoteLogger.log("DELTA", "  [" + storeName + "] key=" + key + " STR=" + (val == null ? "null" : val.substring(0, Math.min(val.length(), 40))));
                            delta[key] = val;
                        }
                    } catch (Throwable unused) {
                        RemoteLogger.log("DELTA", "  [" + storeName + "] parse error, breaking");
                        break;
                    }
                }
            }
        }
    }

    public static void saveDeltaRange(String storeName, int from, int to) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            buffer.writeShortBE(DELTA_FORMAT_VERSION);
            for (int key = from; key <= to; key++) {
                Object value = delta[key];
                if (value != null) {
                    if (value instanceof String) {
                        buffer.writeByte(DELTA_TYPE_STRING);
                        buffer.writeShortBE(key);
                        buffer.writeUTF((String) value);
                    } else {
                        buffer.writeByte(DELTA_TYPE_INT);
                        buffer.writeShortBE(key);
                        buffer.writeIntBE(((Integer) value).intValue());
                    }
                }
            }
            ChunkedRecordStore.writeRecord(storeName, buffer, false);
        } catch (Throwable unused) {
        }
    }
}
