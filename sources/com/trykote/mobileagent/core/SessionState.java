package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.SessionKeys;
import com.trykote.mobileagent.protocol.Account;

import java.util.Vector;

/**
 * Typed facade for session/connection state (SessionKeys).
 * Covers accounts, connection state, flags, timestamps, counters, session objects.
 * Delta keys (217-294) are in the "ses" session store managed by AppState.
 */
public final class SessionState extends AppState {

    public static final SessionState INSTANCE = new SessionState();
    private SessionState() {}

    // === Account management ===

    public static Vector getAccounts() {
        return getVector(SessionKeys.VEC_ACCOUNTS);
    }

    public static void setAccounts(Vector accounts) {
        setObject(SessionKeys.VEC_ACCOUNTS, accounts);
    }

    public static Account getCurrentAccount() {
        return (Account) getPoolObject(SessionKeys.SLOT_CURRENT_ACCOUNT);
    }

    public static void setCurrentAccount(Object account) {
        setObject(SessionKeys.SLOT_CURRENT_ACCOUNT, account);
    }

    public static void clearCurrentAccount() {
        clearIndex(SessionKeys.SLOT_CURRENT_ACCOUNT);
    }

    public static Vector getFilteredAccounts() {
        return getVector(SessionKeys.VEC_FILTERED_ACCOUNTS);
    }

    public static void setFilteredAccounts(Object accounts) {
        setObject(SessionKeys.VEC_FILTERED_ACCOUNTS, accounts);
    }

    public static void clearFilteredAccounts() {
        clearIndex(SessionKeys.VEC_FILTERED_ACCOUNTS);
    }

    public static Vector getAccountSelection() {
        return getVector(SessionKeys.VEC_ACCOUNT_SELECTION);
    }

    public static Object getAccountSelectionObj() {
        return getPoolObject(SessionKeys.VEC_ACCOUNT_SELECTION);
    }

    public static void setAccountSelection(Object selection) {
        setObject(SessionKeys.VEC_ACCOUNT_SELECTION, selection);
    }

    public static void clearAccountSelection() {
        clearIndex(SessionKeys.VEC_ACCOUNT_SELECTION);
    }

    public static void setAccountListText(Object text) {
        setObject(SessionKeys.SLOT_ACCOUNT_LIST_TEXT, text);
    }

    public static int getAccountIndex() {
        return getInt(SessionKeys.INT_ACCOUNT_INDEX);
    }

    public static void setAccountIndex(int index) {
        setInt(SessionKeys.INT_ACCOUNT_INDEX, index);
    }

    public static Object getTempAccount() {
        return getPoolObject(SessionKeys.SLOT_TEMP_ACCOUNT);
    }

    public static void setTempAccount(Object account) {
        setObject(SessionKeys.SLOT_TEMP_ACCOUNT, account);
    }

    public static void clearTempAccount() {
        clearIndex(SessionKeys.SLOT_TEMP_ACCOUNT);
    }

    public static String getLastAccountName() {
        return getString(SessionKeys.LAST_ACCOUNT_NAME);
    }

    public static void setLastAccountName(Object name) {
        setObject(SessionKeys.LAST_ACCOUNT_NAME, name);
    }

    public static String getActiveProtocolName() {
        return getString(SessionKeys.SLOT_ACTIVE_PROTOCOL_NAME);
    }

    public static void setActiveProtocolName(StringBuffer buf) {
        setFromBuffer(SessionKeys.SLOT_ACTIVE_PROTOCOL_NAME, buf);
    }

    // === Account credentials ===

    public static String getAccountLogin() {
        return getString(SessionKeys.SLOT_ACCOUNT_LOGIN);
    }

    public static void setAccountLogin(String value) {
        setString(SessionKeys.SLOT_ACCOUNT_LOGIN, value);
    }

    public static String getAccountPassword() {
        return getString(SessionKeys.SLOT_ACCOUNT_PASSWORD);
    }

    public static void setAccountPassword(String value) {
        setString(SessionKeys.SLOT_ACCOUNT_PASSWORD, value);
    }

    public static void setAccountServer(String value) {
        setString(SessionKeys.SLOT_ACCOUNT_SERVER, value);
    }

    public static String getAccountDisplayName() {
        return getString(SessionKeys.SLOT_ACCOUNT_DISPLAY_NAME);
    }

    public static void setAccountDisplayName(String value) {
        setString(SessionKeys.SLOT_ACCOUNT_DISPLAY_NAME, value);
    }

    public static void setAccountTypeStr(String value) {
        setString(SessionKeys.SLOT_ACCOUNT_TYPE_STR, value);
    }

    // === Session identity ===

    public static String getRandomId() {
        return getString(SessionKeys.SESSION_RANDOM_ID);
    }

    public static void setRandomId(Object id) {
        setObject(SessionKeys.SESSION_RANDOM_ID, id);
    }

    public static String getSessionKey() {
        return getString(SessionKeys.SESSION_KEY);
    }

    public static void setSessionKey(Object key) {
        setObject(SessionKeys.SESSION_KEY, key);
    }

    public static void setSessionKeyFromBuffer(StringBuffer buf) {
        setFromBuffer(SessionKeys.SESSION_KEY, buf);
    }

    public static String getDeviceInfo() {
        return getString(SessionKeys.SESSION_DEVICE_INFO);
    }

    public static void setDeviceInfoFromPool(int sourceKey) {
        setFromPool(SessionKeys.SESSION_DEVICE_INFO, sourceKey);
    }

    public static void clearPlatformInfo() {
        clearIndex(SessionKeys.SESSION_PLATFORM_INFO);
    }

    public static String getSessionHash() {
        return getString(SessionKeys.SLOT_SESSION_HASH);
    }

    public static void setSessionHash(Object hash) {
        setObject(SessionKeys.SLOT_SESSION_HASH, hash);
    }

    public static String getSessionToken() {
        return getString(SessionKeys.SLOT_SESSION_TOKEN);
    }

    public static void setSessionToken(Object token) {
        setObject(SessionKeys.SLOT_SESSION_TOKEN, token);
    }

    public static String getAppProperty(int key) {
        return AppState.getAppProperty(key);
    }

    // === Connection state ===

    public static int getConnectionState() {
        return getInt(SessionKeys.INT_CONNECTION_STATE);
    }

    public static void setConnectionState(int state) {
        setInt(SessionKeys.INT_CONNECTION_STATE, state);
    }

    public static int getProtocolType() {
        return getInt(SessionKeys.INT_PROTOCOL_TYPE);
    }

    public static void setProtocolType(int type) {
        setInt(SessionKeys.INT_PROTOCOL_TYPE, type);
    }

    public static int getServerIndex() {
        return getInt(SessionKeys.INT_SERVER_INDEX);
    }

    public static void setServerIndex(int index) {
        setInt(SessionKeys.INT_SERVER_INDEX, index);
    }

    public static int getTargetState() {
        return getInt(SessionKeys.INT_TARGET_STATE);
    }

    public static void setTargetState(int state) {
        setInt(SessionKeys.INT_TARGET_STATE, state);
    }

    // === Flags ===

    public static boolean isInitComplete() {
        return getBool(SessionKeys.FLAG_INIT_COMPLETE);
    }

    public static void setInitComplete(int value) {
        setInt(SessionKeys.FLAG_INIT_COMPLETE, value);
    }

    public static boolean hasSavedAccounts() {
        return getBool(SessionKeys.FLAG_HAS_SAVED_ACCOUNTS);
    }

    public static boolean hasXmppAccount() {
        return getBool(SessionKeys.FLAG_HAS_XMPP_ACCOUNT);
    }

    public static boolean hasMrimAccount() {
        return getBool(SessionKeys.FLAG_HAS_MRIM_ACCOUNT);
    }

    public static boolean isKeepScreenOn() {
        return getBool(SessionKeys.FLAG_KEEP_SCREEN_ON);
    }

    public static boolean isAppStarting() {
        return getBool(SessionKeys.FLAG_APP_STARTING);
    }

    public static void setAppStarting(int value) {
        setInt(SessionKeys.FLAG_APP_STARTING, value);
    }

    public static boolean isUpdateAvailable() {
        return getBool(SessionKeys.FLAG_UPDATE_AVAILABLE);
    }

    public static void setUpdateAvailable(int value) {
        setInt(SessionKeys.FLAG_UPDATE_AVAILABLE, value);
    }

    public static boolean isMrimDataLoaded() {
        return getBool(SessionKeys.FLAG_MRIM_DATA_LOADED);
    }

    public static void setMrimDataLoaded(int value) {
        setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, value);
    }

    public static void setHasMultipleMrim(boolean value) {
        setBool(SessionKeys.FLAG_HAS_MULTIPLE_MRIM, value);
    }

    public static void setHasMrimAccounts(boolean value) {
        setBool(SessionKeys.FLAG_HAS_MRIM_ACCOUNTS, value);
    }

    public static void setHasMrimAccounts2(boolean value) {
        setBool(SessionKeys.FLAG_HAS_MRIM_ACCOUNTS_2, value);
    }

    public static void setHasXmppAccounts(boolean value) {
        setBool(SessionKeys.FLAG_HAS_XMPP_ACCOUNTS, value);
    }

    public static boolean isCaptchaShown() {
        return getBool(SessionKeys.FLAG_CAPTCHA_SHOWN);
    }

    public static boolean setCaptchaShown(boolean value) {
        return setBool(SessionKeys.FLAG_CAPTCHA_SHOWN, value);
    }

    public static boolean isCleanupDone() {
        return getBool(SessionKeys.FLAG_CLEANUP_DONE);
    }

    public static void setCleanupDone(int value) {
        setInt(SessionKeys.FLAG_CLEANUP_DONE, value);
    }

    public static void setMultipleMrim(boolean value) {
        setBool(SessionKeys.FLAG_MULTIPLE_MRIM, value);
    }

    public static void setMultipleXmpp(boolean value) {
        setBool(SessionKeys.FLAG_MULTIPLE_XMPP, value);
    }

    // === Timestamps ===

    public static long getTimestampFirstRun() {
        return getLong(SessionKeys.TIMESTAMP_FIRST_RUN);
    }

    public static void setTimestampFirstRun(long value) {
        setLong(SessionKeys.TIMESTAMP_FIRST_RUN, value);
    }

    public static long getTimestampLastXmppAuth() {
        return getLong(SessionKeys.TIMESTAMP_LAST_XMPP_AUTH);
    }

    public static void setTimestampLastXmppAuth(long value) {
        setLong(SessionKeys.TIMESTAMP_LAST_XMPP_AUTH, value);
    }

    public static long getTimestampLastCleanup() {
        return getLong(SessionKeys.TIMESTAMP_LAST_CLEANUP);
    }

    public static void setTimestampLastCleanup(long value) {
        setLong(SessionKeys.TIMESTAMP_LAST_CLEANUP, value);
    }

    public static long getTimestampLastUpdateCheck() {
        return getLong(SessionKeys.TIMESTAMP_LAST_UPDATE_CHECK);
    }

    public static void setTimestampLastUpdateCheck(long value) {
        setLong(SessionKeys.TIMESTAMP_LAST_UPDATE_CHECK, value);
    }

    public static long getTimestampCurrent() {
        return getLong(SessionKeys.TIMESTAMP_CURRENT);
    }

    public static long getTimestampOffset() {
        return getLong(SessionKeys.TIMESTAMP_OFFSET);
    }

    // === Counters ===

    public static void addAppStarts(int delta) {
        addInt(SessionKeys.COUNTER_APP_STARTS, delta);
    }

    public static void addErrors(int delta) {
        addInt(SessionKeys.COUNTER_ERRORS, delta);
    }

    public static void addTotalTraffic(int delta) {
        addInt(SessionKeys.COUNTER_TOTAL_TRAFFIC, delta);
    }

    public static int getAndClearTotalTraffic() {
        return getAndClearInt(SessionKeys.COUNTER_TOTAL_TRAFFIC);
    }

    public static void addReservedTraffic(int delta) {
        addInt(SessionKeys.COUNTER_RESERVED, delta);
    }

    public static int getAndClearReserved() {
        return getAndClearInt(SessionKeys.COUNTER_RESERVED);
    }

    public static int getAndClearScreenOpens() {
        return getAndClearInt(SessionKeys.COUNTER_SCREEN_OPENS);
    }

    public static int getAndClearAppStarts() {
        return getAndClearInt(SessionKeys.COUNTER_APP_STARTS);
    }

    public static int getAndClearErrors() {
        return getAndClearInt(SessionKeys.COUNTER_ERRORS);
    }

    // === Session objects ===

    public static Midlet getMidlet() {
        return (Midlet) getPoolObject(SessionKeys.OBJ_MIDLET);
    }

    public static java.util.Random getRandom() {
        return (java.util.Random) getPoolObject(SessionKeys.OBJ_RANDOM);
    }

    public static void setCanvas(Object canvas) {
        setObject(SessionKeys.OBJ_CANVAS, canvas);
    }

    public static Vector getEventQueue() {
        return getVector(SessionKeys.VEC_EVENT_QUEUE);
    }

    public static Object[] getCallbackArray() {
        return (Object[]) getPoolObject(SessionKeys.OBJ_CALLBACK_ARRAY);
    }
}
