package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.ui.GraphicsContext;
import java.util.Vector;
import javax.microedition.lcdui.Font;

/**
 * Typed facade for UI state (UIKeys).
 * Covers screen properties, graphics contexts, fonts, flags, vectors,
 * media slots, and compound layout helpers.
 */
public final class UIState extends AppState {

    public static final UIState INSTANCE = new UIState();
    private UIState() {}

    // === Screen dimensions ===

    public static int getScreenWidth() {
        return getInt(UIKeys.INT_SCREEN_WIDTH);
    }

    public static int getScreenHeight() {
        return getInt(UIKeys.INT_SCREEN_HEIGHT);
    }

    public static void setDimensions(int width, int height) {
        setInt(UIKeys.INT_SCREEN_WIDTH, width);
        setInt(UIKeys.INT_SCREEN_HEIGHT, height);
    }

    /** Usable content height (screen height minus status bar if visible). */
    public static int getHeight() {
        return getInt(UIKeys.INT_SCREEN_HEIGHT) - (SettingsState.isStatusBarVisible() ? getInt(UIKeys.INT_FONT_HEIGHT) + 2 : 0);
    }

    // === Graphics contexts & fonts ===

    public static GraphicsContext getGfxContext(int index) {
        return (GraphicsContext) getPoolObject(UIKeys.GFX_CONTEXT_BASE + index);
    }

    public static Font getFont() {
        return ((GraphicsContext) getPoolObject(UIKeys.GFX_CONTEXT_BASE)).font;
    }

    public static int getIntOffset(int index) {
        return getInt(UIKeys.INT_FONT_HEIGHT + index);
    }

    public static void setGfxContextBase(Object gfx) {
        setObject(UIKeys.GFX_CONTEXT_BASE, gfx);
    }

    public static void setGfxContextBold(Object gfx) {
        setObject(UIKeys.GFX_CONTEXT_BOLD, gfx);
    }

    public static void setGfxContextTitle(Object gfx) {
        setObject(UIKeys.GFX_CONTEXT_TITLE, gfx);
    }

    public static void setGfxContextNormal(Object gfx) {
        setObject(UIKeys.GFX_CONTEXT_NORMAL, gfx);
    }

    public static void setGfxContextNormal2(Object gfx) {
        setObject(UIKeys.GFX_CONTEXT_NORMAL_2, gfx);
    }

    public static void setGfxContextBold2(Object gfx) {
        setObject(UIKeys.GFX_CONTEXT_BOLD_2, gfx);
    }

    public static Object[] getGfxContextsArray() {
        return getObjectArray(UIKeys.OBJ_GFX_CONTEXTS_ARRAY);
    }

    public static int[] getGfxHeightsArray() {
        return (int[]) getPoolObject(UIKeys.ARR_GFX_HEIGHTS);
    }

    public static int getFontHeight() {
        return getInt(UIKeys.INT_FONT_HEIGHT);
    }

    public static void setFontHeight(int height) {
        setInt(UIKeys.INT_FONT_HEIGHT, height);
    }

    public static int getBoldFontHeight() {
        return getInt(UIKeys.INT_BOLD_FONT_HEIGHT);
    }

    public static void setBoldFontHeight(int height) {
        setInt(UIKeys.INT_BOLD_FONT_HEIGHT, height);
    }

    public static int getTitleFontHeight() {
        return getInt(UIKeys.INT_TITLE_FONT_HEIGHT);
    }

    public static void setTitleFontHeight(int height) {
        setInt(UIKeys.INT_TITLE_FONT_HEIGHT, height);
    }

    public static int getNormalFontHeight() {
        return getInt(UIKeys.INT_NORMAL_FONT_HEIGHT);
    }

    public static void setNormalFontHeight(int height) {
        setInt(UIKeys.INT_NORMAL_FONT_HEIGHT, height);
    }

    public static int getNormalFontHeight2() {
        return getInt(UIKeys.INT_NORMAL_FONT_HEIGHT_2);
    }

    public static void setNormalFontHeight2(int height) {
        setInt(UIKeys.INT_NORMAL_FONT_HEIGHT_2, height);
    }

    public static int getBoldFontHeight2() {
        return getInt(UIKeys.INT_BOLD_FONT_HEIGHT_2);
    }

    public static void setBoldFontHeight2(int height) {
        setInt(UIKeys.INT_BOLD_FONT_HEIGHT_2, height);
    }

    // === Screen properties ===

    public static String getScreenTitle() {
        return getString(UIKeys.SLOT_SCREEN_TITLE);
    }

    public static void setScreenTitle(Object title) {
        setObject(UIKeys.SLOT_SCREEN_TITLE, title);
    }

    public static Vector getScreenTitleAsVector() {
        return getVector(UIKeys.SLOT_SCREEN_TITLE);
    }

    public static void clearScreenTitle() {
        clearIndex(UIKeys.SLOT_SCREEN_TITLE);
    }

    public static void setScreenTitleFromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.SLOT_SCREEN_TITLE, buf);
    }

    public static void setScreenTitleFromPool(int sourceKey) {
        setFromPool(UIKeys.SLOT_SCREEN_TITLE, sourceKey);
    }

    public static String getScreenSubtitle() {
        return getString(UIKeys.SLOT_SCREEN_SUBTITLE);
    }

    public static void setScreenSubtitle(Object subtitle) {
        setObject(UIKeys.SLOT_SCREEN_SUBTITLE, subtitle);
    }

    public static void setScreenSubtitleFromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.SLOT_SCREEN_SUBTITLE, buf);
    }

    public static void clearScreenSubtitle() {
        clearIndex(UIKeys.SLOT_SCREEN_SUBTITLE);
    }

    public static String getScreenValue() {
        return getString(UIKeys.SLOT_SCREEN_VALUE);
    }

    public static void setScreenValue(Object value) {
        setObject(UIKeys.SLOT_SCREEN_VALUE, value);
    }

    public static void setScreenValueFromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.SLOT_SCREEN_VALUE, buf);
    }

    public static void clearScreenValue() {
        clearIndex(UIKeys.SLOT_SCREEN_VALUE);
    }

    public static void setScreenValueFromPool(int sourceKey) {
        setFromPool(UIKeys.SLOT_SCREEN_VALUE, sourceKey);
    }

    public static String getScreenDescription() {
        return getString(UIKeys.SLOT_SCREEN_DESCRIPTION);
    }

    public static void setScreenDescription(Object desc) {
        setObject(UIKeys.SLOT_SCREEN_DESCRIPTION, desc);
    }

    public static void clearScreenDescription() {
        clearIndex(UIKeys.SLOT_SCREEN_DESCRIPTION);
    }

    public static void setScreenDescriptionFromPool(int sourceKey) {
        setFromPool(UIKeys.SLOT_SCREEN_DESCRIPTION, sourceKey);
    }

    public static String getAppVersionString() {
        return getString(UIKeys.SLOT_APP_VERSION_STRING);
    }

    public static void setAppVersionString(Object str) {
        setObject(UIKeys.SLOT_APP_VERSION_STRING, str);
    }

    public static void setAppVersionStringFromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.SLOT_APP_VERSION_STRING, buf);
    }

    /** Clears SLOT_SCREEN_TITLE..SLOT_APP_VERSION_STRING range. */
    public static void clearScreenProperties() {
        clearRange(UIKeys.SLOT_SCREEN_TITLE, UIKeys.SLOT_APP_VERSION_STRING);
    }

    // === Status text ===

    public static String getStatusText() {
        return getString(UIKeys.SLOT_STATUS_TEXT);
    }

    public static void setStatusText(Object text) {
        setObject(UIKeys.SLOT_STATUS_TEXT, text);
    }

    public static void setStatusTextFromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.SLOT_STATUS_TEXT, buf);
    }

    public static void clearStatusText() {
        clearIndex(UIKeys.SLOT_STATUS_TEXT);
    }

    // === Notification ===

    public static String getNotificationText() {
        return getString(UIKeys.SLOT_NOTIFICATION_TEXT);
    }

    public static void setNotificationText(Object text) {
        setObject(UIKeys.SLOT_NOTIFICATION_TEXT, text);
    }

    public static String getNotificationTitle() {
        return getString(UIKeys.SLOT_NOTIFICATION_TITLE);
    }

    public static void setNotificationTitle(Object title) {
        setObject(UIKeys.SLOT_NOTIFICATION_TITLE, title);
    }

    public static void setNotificationTitleFromPool(int sourceKey) {
        setFromPool(UIKeys.SLOT_NOTIFICATION_TITLE, sourceKey);
    }

    public static void clearNotificationTitle() {
        clearIndex(UIKeys.SLOT_NOTIFICATION_TITLE);
    }

    public static int getNotificationScreenId() {
        return getInt(UIKeys.INT_NOTIFICATION_SCREEN_ID);
    }

    public static void setNotificationScreenId(int screenId) {
        setInt(UIKeys.INT_NOTIFICATION_SCREEN_ID, screenId);
    }

    // === Input / Forms ===

    public static String getInputText() {
        return getString(UIKeys.SLOT_INPUT_TEXT);
    }

    public static void setInputText(Object text) {
        setObject(UIKeys.SLOT_INPUT_TEXT, text);
    }

    public static void clearInputRange() {
        clearRange(UIKeys.SLOT_INPUT_TEXT, UIKeys.RANGE_INPUT_TEXT_END);
    }

    public static String getInitParams() {
        return getString(UIKeys.SLOT_INIT_PARAMS);
    }

    public static void setInitParamsFromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.SLOT_INIT_PARAMS, buf);
    }

    public static void clearInitParams() {
        clearIndex(UIKeys.SLOT_INIT_PARAMS);
    }

    public static String getLanguageOption() {
        return getString(UIKeys.SLOT_LANGUAGE_OPTION);
    }

    public static void setLanguageOption(Object option) {
        setObject(UIKeys.SLOT_LANGUAGE_OPTION, option);
    }

    public static String getSavedString() {
        return getString(UIKeys.SLOT_SAVED_STRING);
    }

    public static void clearSavedString() {
        clearIndex(UIKeys.SLOT_SAVED_STRING);
    }

    public static void setSavedStringFromPool(int sourceKey) {
        setFromPool(UIKeys.SLOT_SAVED_STRING, sourceKey);
    }

    public static void clearLangOption1() {
        clearIndex(UIKeys.SLOT_LANG_OPTION_1);
    }

    public static void clearLangOption2() {
        clearIndex(UIKeys.SLOT_LANG_OPTION_2);
    }

    public static void setMenuItemFromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.SLOT_MENU_ITEM_1, buf);
    }

    // === Contact name parts (base + offset access) ===

    public static String getContactNamePart(int index) {
        return getString(UIKeys.CONTACT_NAME_PARTS_BASE + index);
    }

    public static void setContactNamePart(int index, Object value) {
        setObject(UIKeys.CONTACT_NAME_PARTS_BASE + index, value);
    }

    // === Clock ===

    public static String getClockString() {
        return getString(UIKeys.SLOT_CLOCK_STRING);
    }

    public static void setClockString(Object str) {
        setObject(UIKeys.SLOT_CLOCK_STRING, str);
    }

    public static void clearClockString() {
        clearIndex(UIKeys.SLOT_CLOCK_STRING);
    }

    // === Screen management ===

    public static int getScreenAction() {
        return getInt(UIKeys.INT_SCREEN_ACTION);
    }

    public static void setScreenAction(int action) {
        setInt(UIKeys.INT_SCREEN_ACTION, action);
    }

    public static int getCurrentScreenId() {
        return getInt(UIKeys.INT_CURRENT_SCREEN_ID);
    }

    public static void setCurrentScreenId(int id) {
        setInt(UIKeys.INT_CURRENT_SCREEN_ID, id);
    }

    public static int getOkMenuAction() {
        return getInt(UIKeys.INT_OK_MENU_ACTION);
    }

    public static void setOkMenuAction(int action) {
        setInt(UIKeys.INT_OK_MENU_ACTION, action);
    }

    public static int getOkMenuType() {
        return getInt(UIKeys.INT_OK_MENU_TYPE);
    }

    public static void setOkMenuType(int type) {
        setInt(UIKeys.INT_OK_MENU_TYPE, type);
    }

    public static int getCancelMenuAction() {
        return getInt(UIKeys.INT_CANCEL_MENU_ACTION);
    }

    public static void setCancelMenuAction(int action) {
        setInt(UIKeys.INT_CANCEL_MENU_ACTION, action);
    }

    public static int getCancelMenuType() {
        return getInt(UIKeys.INT_CANCEL_MENU_TYPE);
    }

    public static void setCancelMenuType(int type) {
        setInt(UIKeys.INT_CANCEL_MENU_TYPE, type);
    }

    public static int getPopupHeight() {
        return getInt(UIKeys.INT_POPUP_HEIGHT);
    }

    public static void setPopupHeight(int height) {
        setInt(UIKeys.INT_POPUP_HEIGHT, height);
    }

    public static int getImageCounter() {
        return getInt(UIKeys.INT_IMAGE_COUNTER);
    }

    public static void setImageCounter(int count) {
        setInt(UIKeys.INT_IMAGE_COUNTER, count);
    }

    public static void incrementImageCounter() {
        addInt(UIKeys.INT_IMAGE_COUNTER, 1);
    }

    // === HTTP params ===

    public static int getHttpResultScreen() {
        return getInt(UIKeys.INT_HTTP_RESULT_SCREEN);
    }

    public static void setHttpResultScreen(int screen) {
        setInt(UIKeys.INT_HTTP_RESULT_SCREEN, screen);
    }

    public static int getHttpParam1() {
        return getInt(UIKeys.INT_HTTP_PARAM_1);
    }

    public static void setHttpParam1(int value) {
        setInt(UIKeys.INT_HTTP_PARAM_1, value);
    }

    public static int getHttpParam2() {
        return getInt(UIKeys.INT_HTTP_PARAM_2);
    }

    public static void setHttpParam2(int value) {
        setInt(UIKeys.INT_HTTP_PARAM_2, value);
    }

    // === Flags ===

    public static boolean isFullscreenRequested() {
        return getBool(UIKeys.FLAG_FULLSCREEN_REQUESTED);
    }

    public static void setFullscreenRequested(boolean value) {
        setBool(UIKeys.FLAG_FULLSCREEN_REQUESTED, value);
    }

    public static boolean isFullscreenActive() {
        return getBool(UIKeys.FLAG_FULLSCREEN_ACTIVE);
    }

    public static void setFullscreenActive(int value) {
        setInt(UIKeys.FLAG_FULLSCREEN_ACTIVE, value);
    }

    public static boolean isOnlineCustomOff() {
        return getBool(UIKeys.FLAG_ONLINE_CUSTOM_OFF);
    }

    public static void setOnlineCustomOff(boolean value) {
        setBool(UIKeys.FLAG_ONLINE_CUSTOM_OFF, value);
    }

    public static boolean isOnlineCustomOn() {
        return getBool(UIKeys.FLAG_ONLINE_CUSTOM_ON);
    }

    public static void setOnlineCustomOn(boolean value) {
        setBool(UIKeys.FLAG_ONLINE_CUSTOM_ON, value);
    }

    public static boolean isNewMessage() {
        return getBool(UIKeys.FLAG_NEW_MESSAGE);
    }

    public static void setNewMessage(int value) {
        setInt(UIKeys.FLAG_NEW_MESSAGE, value);
    }

    public static boolean hasPhoneNext() {
        return getBool(UIKeys.FLAG_PHONE_HAS_NEXT);
    }

    public static void setPhoneHasNext(boolean value) {
        setBool(UIKeys.FLAG_PHONE_HAS_NEXT, value);
    }

    public static boolean hasPhonePrev() {
        return getBool(UIKeys.FLAG_PHONE_HAS_PREV);
    }

    public static void setPhoneHasPrev(boolean value) {
        setBool(UIKeys.FLAG_PHONE_HAS_PREV, value);
    }

    public static boolean isStatusTextSet() {
        return getBool(UIKeys.FLAG_STATUS_TEXT_SET);
    }

    public static void setStatusTextSet(boolean value) {
        setBool(UIKeys.FLAG_STATUS_TEXT_SET, value);
    }

    public static void setStatusTextSet(int value) {
        setInt(UIKeys.FLAG_STATUS_TEXT_SET, value);
    }

    public static boolean isResourceLoading() {
        return getBool(UIKeys.FLAG_RESOURCE_LOADING);
    }

    public static void setResourceLoading(boolean value) {
        setBool(UIKeys.FLAG_RESOURCE_LOADING, value);
    }

    public static boolean isShowStatusFlags() {
        return getBool(UIKeys.FLAG_SHOW_STATUS_FLAGS);
    }

    public static void setShowStatusFlags(boolean value) {
        setBool(UIKeys.FLAG_SHOW_STATUS_FLAGS, value);
    }

    public static boolean isLoading() {
        return getBool(UIKeys.FLAG_LOADING);
    }

    public static void setLoading(int value) {
        setInt(UIKeys.FLAG_LOADING, value);
    }

    public static boolean isXmppCanEdit() {
        return getBool(UIKeys.FLAG_XMPP_CAN_EDIT);
    }

    public static void setXmppCanEdit(boolean value) {
        setBool(UIKeys.FLAG_XMPP_CAN_EDIT, value);
    }

    public static boolean isShowNotification() {
        return getBool(UIKeys.FLAG_SHOW_NOTIFICATION);
    }

    public static void setShowNotification(int value) {
        setInt(UIKeys.FLAG_SHOW_NOTIFICATION, value);
    }

    public static boolean isShowPhoto() {
        return getBool(UIKeys.FLAG_SHOW_PHOTO);
    }

    public static void setShowPhoto(int value) {
        setInt(UIKeys.FLAG_SHOW_PHOTO, value);
    }

    public static boolean isSpecialKeyMode() {
        return getBool(UIKeys.FLAG_SPECIAL_KEY_MODE);
    }

    public static void setSpecialKeyMode(boolean value) {
        setBool(UIKeys.FLAG_SPECIAL_KEY_MODE, value);
    }

    public static boolean isBlinkState() {
        return getBool(UIKeys.FLAG_BLINK_STATE);
    }

    public static void setBlinkState(boolean value) {
        setBool(UIKeys.FLAG_BLINK_STATE, value);
    }

    public static boolean isSupportsAlpha() {
        return getBool(UIKeys.FLAG_SUPPORTS_ALPHA);
    }

    public static void setSupportsAlpha(boolean value) {
        setBool(UIKeys.FLAG_SUPPORTS_ALPHA, value);
    }

    public static boolean isWifiConnection() {
        return getBool(UIKeys.FLAG_WIFI_CONNECTION);
    }

    public static void setWifiConnection(boolean value) {
        setBool(UIKeys.FLAG_WIFI_CONNECTION, value);
    }

    public static boolean isKnownPlatform() {
        return getBool(UIKeys.FLAG_KNOWN_PLATFORM);
    }

    public static void setKnownPlatform(boolean value) {
        setBool(UIKeys.FLAG_KNOWN_PLATFORM, value);
    }

    public static boolean isAdvancedFeatures() {
        return getBool(UIKeys.FLAG_ADVANCED_FEATURES);
    }

    public static void setAdvancedFeatures(boolean value) {
        setBool(UIKeys.FLAG_ADVANCED_FEATURES, value);
    }

    public static boolean isKnownDevice() {
        return getBool(UIKeys.FLAG_KNOWN_DEVICE);
    }

    public static void setKnownDevice(boolean value) {
        setBool(UIKeys.FLAG_KNOWN_DEVICE, value);
    }

    public static boolean isXmppRosterLoaded() {
        return getBool(UIKeys.FLAG_XMPP_ROSTER_LOADED);
    }

    public static void setXmppRosterLoaded(boolean value) {
        setBool(UIKeys.FLAG_XMPP_ROSTER_LOADED, value);
    }

    public static boolean isRouteLocationActive() {
        return getBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE);
    }

    public static void setRouteLocationActive(boolean value) {
        setBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE, value);
    }

    public static boolean isRoutePointVisible() {
        return getBool(UIKeys.FLAG_ROUTE_POINT_VISIBLE);
    }

    public static void setRoutePointVisible(boolean value) {
        setBool(UIKeys.FLAG_ROUTE_POINT_VISIBLE, value);
    }

    public static boolean isRoutePointHidden() {
        return getBool(UIKeys.FLAG_ROUTE_POINT_HIDDEN);
    }

    public static void setRoutePointHidden(int value) {
        setInt(UIKeys.FLAG_ROUTE_POINT_HIDDEN, value);
    }

    public static boolean isPhotoRegistryReady() {
        return getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY);
    }

    public static void setPhotoRegistryReady(boolean value) {
        setBool(UIKeys.FLAG_PHOTO_REGISTRY_READY, value);
    }

    public static boolean isConversationActive() {
        return getBool(UIKeys.FLAG_CONVERSATION_ACTIVE);
    }

    public static void setConversationActive(boolean value) {
        setBool(UIKeys.FLAG_CONVERSATION_ACTIVE, value);
    }

    // === Vectors ===

    public static Vector getScreenStack() {
        return getVector(UIKeys.VEC_SCREEN_STACK);
    }

    public static void setScreenStack(Object stack) {
        setObject(UIKeys.VEC_SCREEN_STACK, stack);
    }

    public static Vector getPendingConnections() {
        return getVector(UIKeys.VEC_PENDING_CONNECTIONS);
    }

    public static void setPendingConnections(Object vec) {
        setObject(UIKeys.VEC_PENDING_CONNECTIONS, vec);
    }

    public static Vector getOnlineContacts() {
        return getVector(UIKeys.VEC_ONLINE_CONTACTS);
    }

    public static void setOnlineContacts(Object vec) {
        setObject(UIKeys.VEC_ONLINE_CONTACTS, vec);
    }

    public static Vector getActiveConnections() {
        return getVector(UIKeys.VEC_ACTIVE_CONNECTIONS);
    }

    public static void setActiveConnections(Object vec) {
        setObject(UIKeys.VEC_ACTIVE_CONNECTIONS, vec);
    }

    public static Vector getTabItems() {
        return getVector(UIKeys.VEC_TAB_ITEMS);
    }

    public static void setTabItems(Object vec) {
        setObject(UIKeys.VEC_TAB_ITEMS, vec);
    }

    public static Vector getTabBars() {
        return getVector(UIKeys.VEC_TAB_BARS);
    }

    public static void setTabBars(Object vec) {
        setObject(UIKeys.VEC_TAB_BARS, vec);
    }

    public static Vector getPopupItems() {
        return getVector(UIKeys.VEC_POPUP_ITEMS);
    }

    public static void setPopupItems(Object vec) {
        setObject(UIKeys.VEC_POPUP_ITEMS, vec);
    }

    public static Vector getPhoneResults() {
        return getVector(UIKeys.VEC_PHONE_RESULTS);
    }

    public static void setPhoneResults(Object vec) {
        setObject(UIKeys.VEC_PHONE_RESULTS, vec);
    }

    public static Vector getPhotoQueue() {
        return getVector(UIKeys.VEC_PHOTO_QUEUE);
    }

    public static void setPhotoQueue(Object vec) {
        setObject(UIKeys.VEC_PHOTO_QUEUE, vec);
    }

    // === Objects / Slots ===

    public static Object getTextBox() {
        return getPoolObject(UIKeys.OBJ_TEXT_BOX);
    }

    public static void setTextBox(Object textBox) {
        setObject(UIKeys.OBJ_TEXT_BOX, textBox);
    }

    public static Object getPhotoCache1() {
        return getPoolObject(UIKeys.OBJ_PHOTO_CACHE_1);
    }

    public static Object getPhotoCache2() {
        return getPoolObject(UIKeys.OBJ_PHOTO_CACHE_2);
    }

    public static void setPhotoCache1(Object cache) {
        setObject(UIKeys.OBJ_PHOTO_CACHE_1, cache);
    }

    public static void clearPhotoCache1() {
        clearIndex(UIKeys.OBJ_PHOTO_CACHE_1);
    }

    public static void setPhotoCache2(Object cache) {
        setObject(UIKeys.OBJ_PHOTO_CACHE_2, cache);
    }

    public static void setPhotoCache2FromBuffer(StringBuffer buf) {
        setFromBuffer(UIKeys.OBJ_PHOTO_CACHE_2, buf);
    }

    public static TransitionData getTransitionData() {
        return (TransitionData) getPoolObject(UIKeys.OBJ_TRANSITION_DATA);
    }

    public static Object getHttpCallback() {
        return getPoolObject(UIKeys.OBJ_HTTP_CALLBACK);
    }

    public static void setHttpCallback(Object callback) {
        setObject(UIKeys.OBJ_HTTP_CALLBACK, callback);
    }

    public static Object getTempObject1() {
        return getPoolObject(UIKeys.SLOT_TEMP_OBJECT_1);
    }

    public static void setTempObject1(Object obj) {
        setObject(UIKeys.SLOT_TEMP_OBJECT_1, obj);
    }

    public static void clearTempObject1() {
        clearIndex(UIKeys.SLOT_TEMP_OBJECT_1);
    }

    public static Object getPhoneContact() {
        return getPoolObject(UIKeys.RANGE_PHONE_CONTACT_START);
    }

    public static void setPhoneContact(Object contact) {
        setObject(UIKeys.RANGE_PHONE_CONTACT_START, contact);
    }

    // === JSON result (SLOT_MEDIA_PLAYER repurposed as temp JSON holder) ===

    public static Object getJsonResult() {
        return getPoolObject(UIKeys.SLOT_MEDIA_PLAYER);
    }

    public static void setJsonResult(Object result) {
        setObject(UIKeys.SLOT_MEDIA_PLAYER, result);
    }

    public static void clearJsonResult() {
        clearIndex(UIKeys.SLOT_MEDIA_PLAYER);
    }

    // === Media ===

    public static Object getMediaPlayer() {
        return getPoolObject(UIKeys.OBJ_MEDIA_PLAYER);
    }

    public static void setMediaPlayer(Object player) {
        setObject(UIKeys.OBJ_MEDIA_PLAYER, player);
    }

    public static Object getMediaResource() {
        return getPoolObject(UIKeys.RANGE_MEDIA_RESOURCES_START);
    }

    public static void setMediaResource(Object resource) {
        setObject(UIKeys.RANGE_MEDIA_RESOURCES_START, resource);
    }

    public static void clearMediaRange() {
        clearRange(UIKeys.RANGE_MEDIA_RESOURCES_START, UIKeys.OBJ_MEDIA_PLAYER);
    }

    public static Vector getMediaStream() {
        return getVector(UIKeys.SLOT_MEDIA_STREAM);
    }

    public static void setMediaStream(Object stream) {
        setObject(UIKeys.SLOT_MEDIA_STREAM, stream);
    }

    public static Vector getMediaControl() {
        return getVector(UIKeys.SLOT_MEDIA_CONTROL);
    }

    public static void setMediaControl(Object vec) {
        setObject(UIKeys.SLOT_MEDIA_CONTROL, vec);
    }

    public static void clearMediaControl() {
        clearIndex(UIKeys.SLOT_MEDIA_CONTROL);
    }

    public static Vector getMediaVolume() {
        return getVector(UIKeys.SLOT_MEDIA_VOLUME);
    }

    public static void setMediaVolume(Object vec) {
        setObject(UIKeys.SLOT_MEDIA_VOLUME, vec);
    }

    public static void setMediaCallback(Object obj) {
        setObject(UIKeys.SLOT_MEDIA_CALLBACK, obj);
    }

    // === Update check (SLOT_MEDIA_RESOURCE used as lock + byte flag) ===

    public static Object getUpdateLock() {
        return getPoolObject(UIKeys.SLOT_MEDIA_RESOURCE);
    }

    public static byte[] getUpdateFlagBytes() {
        return getBytes(UIKeys.SLOT_MEDIA_RESOURCE);
    }

    // === Bulk range operations ===

    public static void clearTempDataRange() {
        clearRange(UIKeys.RANGE_TEMP_DATA_START, UIKeys.RANGE_TEMP_DATA_END);
    }

    public static void clearSessionTempRange() {
        clearRange(UIKeys.RANGE_SESSION_TEMP_START, UIKeys.RANGE_SESSION_TEMP_END);
    }

    public static void clearInitParamsRange() {
        clearRange(UIKeys.SLOT_INIT_PARAMS, UIKeys.SLOT_LANGUAGE_OPTION);
    }
}
