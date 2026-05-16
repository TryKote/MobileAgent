package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.SettingsKeys;
import com.trykote.mobileagent.util.ObjectPool;

/**
 * Typed facade for settings-related state.
 * Extends AppState for inherited persistence and shared accessors.
 */
public final class SettingsState extends AppState {

    public static final SettingsState INSTANCE = new SettingsState();
    private SettingsState() {}

    protected String storeName() { return STORE_SETTINGS; }
    protected int deltaStart() { return RANGE_SETTINGS_START; }
    protected int deltaEnd() { return RANGE_SETTINGS_END; }

    // --- UI appearance ---

    public static boolean isFullscreen() {
        return getBool(SettingsKeys.SETTING_FULLSCREEN);
    }

    public static boolean isTransparencyEnabled() {
        return getBool(SettingsKeys.SETTING_TRANSPARENCY);
    }

    public static boolean isBoldTitleFont() {
        return getBool(SettingsKeys.SETTING_BOLD_TITLE_FONT);
    }

    public static boolean isStatusBarVisible() {
        return getBool(SettingsKeys.SETTING_STATUS_BAR_VISIBLE);
    }

    public static boolean isHeaderVisible() {
        return getBool(SettingsKeys.SETTING_HEADER_VISIBLE);
    }

    public static int getColorTheme() {
        return getInt(SettingsKeys.SETTING_COLOR_THEME);
    }

    public static void setColorTheme(int theme) {
        setInt(SettingsKeys.SETTING_COLOR_THEME, theme);
    }

    public static int getFontSizeChat() {
        return getInt(SettingsKeys.SETTING_FONT_SIZE_CHAT);
    }

    public static int getFontSizeList() {
        return getInt(SettingsKeys.SETTING_FONT_SIZE_LIST);
    }

    public static int getCustomViewMode() {
        return getInt(SettingsKeys.SETTING_CUSTOM_VIEW_MODE);
    }

    public static void setCustomViewMode(int mode) {
        setInt(SettingsKeys.SETTING_CUSTOM_VIEW_MODE, mode);
    }

    // --- Tabs ---

    public static boolean isMailTabEnabled() {
        return getBool(SettingsKeys.SETTING_MAIL_TAB_ENABLED);
    }

    public static boolean isSearchTabEnabled() {
        return getBool(SettingsKeys.SETTING_SEARCH_TAB_ENABLED);
    }

    // --- Contacts display ---

    public static boolean isShowOffline() {
        return getBool(SettingsKeys.SETTING_SHOW_OFFLINE);
    }

    public static boolean isGroupByStatus() {
        return getBool(SettingsKeys.SETTING_GROUP_BY_STATUS);
    }

    public static boolean isShowGroups() {
        return getBool(SettingsKeys.SETTING_SHOW_GROUPS);
    }

    public static int getMaxContacts() {
        return getInt(SettingsKeys.SETTING_MAX_CONTACTS);
    }

    public static int getSortOrder() {
        return getInt(SettingsKeys.SETTING_SORT_ORDER);
    }

    public static int getContactSortMode() {
        return getInt(SettingsKeys.SETTING_CONTACT_SORT_MODE);
    }

    public static boolean isShowInList() {
        return getBool(SettingsKeys.SETTING_SHOW_IN_LIST);
    }

    public static boolean isShowPopup() {
        return getBool(SettingsKeys.SETTING_SHOW_POPUP);
    }

    // --- Sound & notifications ---

    public static boolean isSoundEnabled() {
        return getBool(SettingsKeys.SETTING_SOUND_ENABLED);
    }

    public static int getVolumeLevel() {
        return getInt(SettingsKeys.SETTING_VOLUME_LEVEL);
    }

    public static boolean isNotificationEnabled() {
        return getBool(SettingsKeys.SETTING_NOTIFICATION_ENABLED);
    }

    // --- Network & connection ---

    public static boolean isFastConnection() {
        return getBool(SettingsKeys.SETTING_FAST_CONNECTION);
    }

    public static boolean isCompressionEnabled() {
        return getBool(SettingsKeys.SETTING_COMPRESSION_ENABLED);
    }

    public static void setCompressionEnabled(int value) {
        setObject(SettingsKeys.SETTING_COMPRESSION_ENABLED, ObjectPool.integerOf(value));
    }

    public static boolean isAutoReconnect() {
        return getBool(SettingsKeys.SETTING_AUTO_RECONNECT);
    }

    public static int getNetworkMode() {
        return getInt(SettingsKeys.SETTING_NETWORK_MODE);
    }

    public static boolean isAuthRequired() {
        return getBool(SettingsKeys.SETTING_AUTH_REQUIRED);
    }

    public static int getTimeoutValue() {
        return getInt(SettingsKeys.SETTING_TIMEOUT_VALUE);
    }

    // --- Traffic ---

    public static boolean isTrafficInfoEnabled() {
        return getBool(SettingsKeys.SETTING_TRAFFIC_INFO_ENABLED);
    }

    public static int getTrafficInfoType() {
        return getInt(SettingsKeys.SETTING_TRAFFIC_INFO_TYPE);
    }

    public static int getTrafficCost() {
        return getInt(SettingsKeys.SETTING_TRAFFIC_COST);
    }

    public static int getBlockSizeKb() {
        return getInt(SettingsKeys.SETTING_BLOCK_SIZE_KB);
    }

    // --- Privacy / presence ---

    public static boolean isExtendedPresence() {
        return getBool(SettingsKeys.SETTING_EXTENDED_PRESENCE);
    }

    public static boolean isExtendedStatus() {
        return getBool(SettingsKeys.SETTING_EXTENDED_STATUS);
    }

    public static boolean isCustomNoteEnabled() {
        return getBool(SettingsKeys.SETTING_CUSTOM_NOTE_ENABLED);
    }

    public static int getUpdateStatus() {
        return getInt(SettingsKeys.SETTING_UPDATE_STATUS);
    }

    // --- Multi-account ---

    public static boolean isMultiAccount() {
        return getBool(SettingsKeys.SETTING_MULTI_ACCOUNT);
    }

    // --- Key actions ---

    public static int getKeyStarAction() {
        return getInt(SettingsKeys.SETTING_KEY_STAR_ACTION);
    }

    public static int getKeyHashAction() {
        return getInt(SettingsKeys.SETTING_KEY_HASH_ACTION);
    }

    // --- Timezone ---

    public static int getTimezoneOffset() {
        return getInt(SettingsKeys.SETTING_TIMEZONE_OFFSET);
    }

    // --- UI counter (session-scoped) ---

    public static int getUiCounter() {
        return getInt(SettingsKeys.UI_COUNTER);
    }

    public static void incrementUiCounter() {
        addInt(SettingsKeys.UI_COUNTER, 1);
    }

    public static void setStatusBarVisible(boolean visible) {
        setBool(SettingsKeys.SETTING_STATUS_BAR_VISIBLE, visible);
    }

    public static void setTrafficCost(int cost) {
        setInt(SettingsKeys.SETTING_TRAFFIC_COST, cost);
    }

    public static void setUpdateStatus(int status) {
        setInt(SettingsKeys.SETTING_UPDATE_STATUS, status);
    }

    public static boolean toggleSortOrder() {
        return toggleBool(SettingsKeys.SETTING_SORT_ORDER);
    }

    // --- Handler-local temp state ---

    public static int getSettingsTheme() {
        return getInt(SettingsKeys.INT_SETTINGS_THEME);
    }

    public static void setSettingsTheme(int value) {
        setInt(SettingsKeys.INT_SETTINGS_THEME, value);
    }

    public static int getThemeCache() {
        return getInt(SettingsKeys.INT_THEME_CACHE);
    }

    public static void setThemeCache(int value) {
        setInt(SettingsKeys.INT_THEME_CACHE, value);
    }

    public static int getMultiAccountCache() {
        return getInt(SettingsKeys.INT_MULTI_ACCOUNT_CACHE);
    }

    public static void setMultiAccountCache(int value) {
        setInt(SettingsKeys.INT_MULTI_ACCOUNT_CACHE, value);
    }

    public static int getEmoticonConfigAction() {
        return getInt(SettingsKeys.INT_EMOTICON_CONFIG_ACTION);
    }

    public static void setEmoticonConfigAction(int value) {
        setInt(SettingsKeys.INT_EMOTICON_CONFIG_ACTION, value);
    }

    public static int getEmoticonConfigId() {
        return getInt(SettingsKeys.INT_EMOTICON_CONFIG_ID);
    }

    public static void setEmoticonConfigId(int value) {
        setInt(SettingsKeys.INT_EMOTICON_CONFIG_ID, value);
    }

    public static int getEmoticonConfigValue1() {
        return getInt(SettingsKeys.INT_EMOTICON_CONFIG_VALUE_1);
    }

    public static void setEmoticonConfigValue1(int value) {
        setInt(SettingsKeys.INT_EMOTICON_CONFIG_VALUE_1, value);
    }

    public static int getEmoticonConfigValue2() {
        return getInt(SettingsKeys.INT_EMOTICON_CONFIG_VALUE_2);
    }

    public static void setEmoticonConfigValue2(int value) {
        setInt(SettingsKeys.INT_EMOTICON_CONFIG_VALUE_2, value);
    }
}
