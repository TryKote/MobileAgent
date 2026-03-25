package com.trykote.mobileagent.core;


import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.ui.handler.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

/* renamed from: ao */
/* loaded from: MobileAgent_3.9.jar:ao.class */
public final class AppController {

    /* renamed from: a */
    public static long[] timers;

    /* renamed from: b */
    public static String pendingUrl;

    /* renamed from: c */
    public static MrimAccount pendingAccount;

    /* renamed from: d */
    public static Object appLock;

    /* renamed from: e */
    public static boolean isShuttingDown;

    /* renamed from: f */
    public static boolean needsLayoutUpdate;

    /* renamed from: g */
    public static boolean needsRepaint;

    /* renamed from: i */
    public static boolean isBackgrounded;

    /* renamed from: h */
    public static boolean saveOnExit;

    /* renamed from: j */
    private static MapPoint pendingMapPoint;

    /* renamed from: a */
    public static final int handleAction(Object obj) {
        int targetState = AppState.getInt(StateKeys.INT_TARGET_STATE);
        if (obj != null) {
            AppState.setAccount(obj);
            return targetState;
        }
        if (targetState != 152) {
            return ScreenId.COLOR_PICKER;
        }
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        return ScreenId.MAP_VIEW_SETTINGS;
    }

    /* renamed from: a */
    public static final int handleMenuAction(String str, Object obj) {
        if (StringUtils.matchesKey(548, str)) {
            AccountManager.rebuildAccountCaches();
            return ScreenId.CONTACT_LIST;
        }
        Account account = (Account) obj;
        int errorCode = account.isConnecting() ? account.disconnect() : account.connect(0);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    /* renamed from: a */
    public static final int handleScreenAction(int i) {
        ScreenBuilder.onScreenClosed();
        AppState.setInt(StateKeys.INT_ASYNC_TASK_ID, i);
        return AppState.getInt(StateKeys.INT_CURRENT_SCREEN_ID);
    }

    /* renamed from: a */
    public static final int handleSoftKeyAction(String str) {
        int chatRoomId = AppState.getInt(StateKeys.INT_CHATROOM_ID);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(chatRoomId);
        IOUtils.setSelectedItems(chatRoom.readMessages);
        if (StringUtils.matchesKey(852, str)) {
            chatRoom.readMessages.removeAllElements();
            return 0;
        }
        if (StringUtils.matchesKey(853, str)) {
            AppState.setInt(StateKeys.INT_CHAT_VIEW_MODE, 2);
            return 0;
        }
        if (StringUtils.matchesKey(854, str)) {
            AppState.setInt(StateKeys.INT_CHAT_VIEW_MODE, 1);
            return 0;
        }
        if (!StringUtils.matchesKey(845, str)) {
            return 0;
        }
        AppState.setInt(StateKeys.INT_ACTIVE_CHATROOM_ID, account.findDefaultChatRoom().id);
        return 0;
    }

    /* renamed from: a */
    public static final void toggleOnlineMode(boolean z) {
        if (!z) {
            AppState.setInt(StateKeys.INT_CHAT_LIST_MODE, 5);
        } else {
            AppState.setInt(StateKeys.INT_CHAT_LIST_MODE, 4);
            AppState.setBool(StateKeys.FLAG_EXTENDED_CHAT_VIEW, true);
        }
    }

    /* renamed from: a */
    public static final int handleEnterKey() {
        restoreState();
        return ScreenManager.processScreenForm();
    }

    /* renamed from: b */
    public static final int handleBackKey() {
        restoreState();
        return 0;
    }

    /* renamed from: c */
    public static final void resetSearchResults() {
        AppState.clearRange(StateKeys.SLOT_MSG_SUBJECT, StateKeys.SLOT_MSG_EXTRA_1);
    }

    /* renamed from: ad */
    private static final void restoreState() {
        ((MrimAccount) AppState.getAccount()).getLastChatRoom().clear();
    }

    /* renamed from: d */
    public static final int handleLeftKey() {
        int errorCode = AppState.getCurrentContact().validateDelete();
        if (0 != errorCode) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    /* renamed from: b */
    public static final int handleItemAction(Object obj) {
        AppState.setInt(StateKeys.FLAG_REGISTRATION_DONE, 1);
        if (obj != null) {
            AppState.pool[StateKeys.LAST_ACCOUNT_NAME] = obj;
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: e */
    public static final int handleRightKey() {
        AppState.setInt(StateKeys.FLAG_APP_STARTING, 1);
        MapController.toggleScrollMode();
        return ScreenId.MAP;
    }

    /* renamed from: f */
    public static final String getAppVersion() {
        return StringUtils.intern(Long.toString(Runtime.getRuntime().freeMemory()));
    }

    /* renamed from: g */
    public static final void clearPreviewState() {
        AppState.clearRange(StateKeys.SLOT_SCREEN_TITLE, StateKeys.SLOT_APP_VERSION_STRING);
    }

    /* renamed from: h */
    public static final void clearSearchState() {
        Contact contact = AppState.getCurrentContact();
        markContactUnread(contact);
        contact.flags = (byte) 0;
        contact.dirty = true;
        contact.updateRenderState();
        ScreenManager.showScreen(contact.showMessages().measureContent());
    }

    /* renamed from: b */
    public static final int mapKeyToAction(int i) {
        if (i == 4) {
            ScreenManager.handleScreenClose();
            return 0;
        }
        if (i != 137) {
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: a */
    public static final void setTimer(int i, long j) {
        timers[i] = System.currentTimeMillis() + j;
    }

    /* renamed from: K */
    public static boolean isTimerType(int i) {
        return timers[i] < System.currentTimeMillis();
    }

    /* renamed from: a */
    public static final boolean isTimerExpired(long j) {
        return j != 0 && j < System.currentTimeMillis();
    }

    /* renamed from: b */
    public static final boolean checkTimer(int i, long j) {
        long[] timerArray = timers;
        long timerValue = timerArray[i];
        long currentTime = System.currentTimeMillis();
        if (timerValue >= timerValue) {
            return false;
        }
        timerArray[i] = currentTime + j;
        return true;
    }

    /* renamed from: i */
    public static final int handleHashKey() {
        if (ScreenManager.hasScreen(43)) {
            return ScreenId.CHAT_ROOM_VIEW;
        }
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return -1;
    }

    /* renamed from: c */
    public static final int handleStarAction(Object obj) {
        AppState.pool[StateKeys.OBJ_SEARCH_RESULT] = obj;
        return ScreenId.SEARCH_ENTRY;
    }

    /* renamed from: j */
    public static final int showPeopleNearby() {
        ResourceManager.dialPhoneContact((PhoneContact) AppState.pool[StateKeys.RANGE_PHONE_CONTACT_START], AppState.getInt(StateKeys.INT_PHONE_SCROLL_OFFSET) + 10);
        return ScreenId.MAP;
    }

    /* renamed from: k */
    public static final int showPeopleSearch() {
        ResourceManager.dialPhoneContact((PhoneContact) AppState.pool[StateKeys.RANGE_PHONE_CONTACT_START], AppState.getInt(StateKeys.INT_PHONE_SCROLL_OFFSET) - 10);
        return ScreenId.MAP;
    }

    /* renamed from: l */
    public static final UserSearchResult getCurrentSearchResult() {
        return (UserSearchResult) AppState.pool[StateKeys.OBJ_SEARCH_RESULT];
    }

    /* renamed from: c */
    public static final int handleAccountOption(int i) {
        Account account = AppState.getAccount();
        if (!(account instanceof MmpProtocol)) {
            return interpolateColor((i + 161) - 4, (i + 155) - 4, 4);
        }
        ((MmpProtocol) account).reserved2 = i;
        if (i == 0) {
            return ScreenId.STATUS_DIALOG;
        }
        return interpolateColor(i + 268, i + 118, 3);
    }

    /* renamed from: m */
    public static final String getStatusText() {
        Object obj = AppState.pool[StateKeys.OBJ_PHOTO_CACHE_1];
        if (obj == null) {
            return null;
        }
        return obj instanceof String ? (String) obj : ((MrimContact) obj).simpleIdentifier;
    }

    /* renamed from: b */
    public static final int processLoginField(String str) {
        if (AppState.getString(StateKeys.SLOT_ACTIVE_PROTOCOL_NAME).equals(str)) {
            ScreenBuilder.onScreenClosed();
            if (MapController.hasRoutePoints()) {
                return 0;
            }
            return NotificationHelper.showError(354);
        }
        if (AppState.getString(StateKeys.STR_PROTOCOL_MRIM).equals(str)) {
            AppState.setInt(StateKeys.FLAG_GPS_ACTIVE, 1);
            XmppContactGroup.stopMapAnimation(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE));
            MapRenderer.needsRedraw = true;
            return ScreenId.MAP;
        }
        if (!AppState.getString(StateKeys.STR_PROTOCOL_MMP).equals(str)) {
            return 0;
        }
        AppState.setInt(StateKeys.FLAG_GPS_ACTIVE, 0);
        XmppContactGroup.startMapAnimation(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE));
        MapRenderer.needsRedraw = true;
        return ScreenId.MAP;
    }

    /* renamed from: f */
    public static final int handleGroupSelection(int i) {
        Message message = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID)).getMessage(AppState.getString(StateKeys.SLOT_MESSAGE_ID));
        String body = message.body;
        message.body = i == 0 ? Conversation.encodeAlternate(body) : Conversation.decodeAlternate(body);
        return ScreenId.MESSAGE_PREVIEW;
    }

    /* renamed from: g */
    public static final int handleGroupRename(int i) {
        if (i != 147 && i != 133 && i != 89) {
            return 0;
        }
        Vector accounts = AccountManager.getMrimAccountList();
        int size = accounts.size();
        if (size == 0) {
            return NotificationHelper.showError(549);
        }
        if (size != 1) {
            return AccountManager.showAccountList(accounts, i, false);
        }
        AppState.setAccount(accounts.firstElement());
        return i;
    }

    /* renamed from: n */
    public static final void finishScreenBuild() {
        AppState.clearRange(StateKeys.SLOT_INIT_PARAMS, StateKeys.SLOT_LANGUAGE_OPTION);
        Telemetry.sendReport(true, (MrimAccount) AppState.getAccount());
    }

    /* renamed from: h */
    public static final int handleContactOption(int i) {
        if (i != 6) {
            return 0;
        }
        MrimAccount account = (MrimAccount) AppState.getAccount();
        account.isHighlighted = true;
        if (!account.isSelected()) {
            return NotificationHelper.showError(667);
        }
        applyViewMode(true, false, !AppState.getBool(StateKeys.FLAG_MAP_VIEW_ACTIVE));
        AppState.setInt(StateKeys.FLAG_REFRESH_CONTACTS, 1);
        MapController.selectMapItem((ListItem) account);
        return 0;
    }

    /* renamed from: o */
    public static final int handleContactListKey() {
        MrimAccount account = (MrimAccount) AppState.pool[StateKeys.SLOT_TEMP_ACCOUNT];
        ResourceManager.showMailAccountList();
        AppState.setAccount(account);
        AppState.setInt(StateKeys.INT_SCREEN_ACTION, 38);
        return ScreenId.CHAT_ROOMS;
    }

    /* renamed from: p */
    public static final int handlePresenceAction() {
        Vector accounts = AppState.getVector(StateKeys.VEC_ACCOUNT_SELECTION);
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return ScreenId.CONTACT_LIST;
            }
            ((Account) accounts.elementAt(size)).connect(0);
        }
    }

    /* renamed from: i */
    public static final int handleProfileAction(int i) {
        switch (i) {
            case 0:
                Telemetry.sendReport(false, null);
                return ScreenId.CLOSE;
            case 1:
                applyViewMode(false, true, true);
                return ScreenId.CLOSE;
            case 2:
                applyViewMode(true, false, true);
                return ScreenId.CLOSE;
            case 3:
                Conversation.setMapEnabled(true);
                return ScreenId.CLOSE;
            case 4:
                Conversation.setMapEnabled(false);
                return ScreenId.CLOSE;
            case 5:
                return AppController.getIconOffset();
            default:
                return 0;
        }
    }

    /* renamed from: a */
    public static final void applyViewMode(boolean showMap, boolean showList, boolean shouldInvalidate) {
        AppState.setBool(StateKeys.FLAG_MAP_VIEW_ACTIVE, showMap);
        AppState.setBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE, showList);
        if (!shouldInvalidate || !MapController.mapInitialized) {
            return;
        }
        int i = 11;
        while (true) {
            i--;
            if (i < 0) {
                MmpContact.clearLocationData();
                StringUtils.initTileCache();
                ServiceRegistry.clearPhotoCache();
                MapRenderer.needsRedraw = true;
                return;
            }
            XmppContactGroup.invalidateCachedImage(i + 18);
        }
    }

    /* renamed from: c */
    public static final Object[] getUrlComponents(String str) {
        return new Object[]{ResourceManager.integerOf(20), str};
    }

    /* renamed from: d */
    public static final int openUrl(String str) {
        AppState.setObject(StateKeys.SLOT_STATUS_TEXT, (Object) new StringBuffer().append((Object) Utils.getMessageBuffer()).append(str).toString());
        return 0;
    }

    /* renamed from: q */
    public static final void refreshContactList() {
        RemoteLogger.log("CL", "refreshContactList called");
        AppState.clearRange(StateKeys.RANGE_ACCOUNT_CACHE_START, StateKeys.RANGE_ACCOUNT_CACHE_END);
    }

    /* renamed from: j */
    public static final int handleSettingsOption(int i) {
        AppState.setInt(StateKeys.INT_PERIOD_INDEX, i);
        return ScreenId.TRAFFIC_STATS;
    }

    /* renamed from: k */
    public static final int handleExtSettingsOption(int i) {
        MrimAccount account = (MrimAccount) AppState.getAccount();
        switch (i) {
            case 0:
                if (account != null) {
                    account.markProfileForPublish();
                    break;
                } else {
                    Vector accounts = AccountManager.getMrimAccountList();
                    int size = accounts.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            ObjectPool.releaseVector(accounts);
                            break;
                        } else {
                            AccountManager.findMrimAccount(accounts, size).markProfileForPublish();
                        }
                    }
                }
            case 1:
                if (account != null) {
                    account.markProfileForHide();
                    break;
                } else {
                    Vector accounts2 = AccountManager.getMrimAccountList();
                    int size2 = accounts2.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            ObjectPool.releaseVector(accounts2);
                            break;
                        } else {
                            AccountManager.findMrimAccount(accounts2, size2).markProfileForHide();
                        }
                    }
                }
            case 2:
                if (account != null) {
                    account.setProfileGroups();
                    break;
                } else {
                    Vector accounts3 = AccountManager.getMrimAccountList();
                    int size3 = accounts3.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            ObjectPool.releaseVector(accounts3);
                            break;
                        } else {
                            AccountManager.findMrimAccount(accounts3, size3).setProfileGroups();
                        }
                    }
                }
            case 3:
                if (account != null) {
                    account.clearProfileGroups();
                    break;
                } else {
                    Vector accounts4 = AccountManager.getMrimAccountList();
                    int size4 = accounts4.size();
                    while (true) {
                        size4--;
                        if (size4 < 0) {
                            ObjectPool.releaseVector(accounts4);
                            break;
                        } else {
                            AccountManager.findMrimAccount(accounts4, size4).clearProfileGroups();
                        }
                    }
                }
            case 4:
                return ScreenId.PHOTO_SELECTOR;
        }
        if (AppState.getBool(StateKeys.FLAG_UPDATE_AVAILABLE)) {
            return AppState.getInt(StateKeys.INT_CONNECTION_STATE);
        }
        ScreenBuilder.onScreenClosed();
        return ScreenId.UPDATE_ALERT;
    }

    /* renamed from: d */
    public static final int handleObjectAction(Object obj) {
        AppState.setAccount(obj);
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: n */
    public static final int handleConnectionOption(int i) {
        ((XmppContact) AppState.getCurrentContact()).setPresenceFeature(i);
        return 0;
    }

    /* renamed from: f */
    public static final int validateServerAddress(String str) {
        AppState.setFromBuffer(StateKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(str));
        return 0;
    }

    /* renamed from: v */
    public static final int handleSendKey() {
        AppState.setAccount(AccountManager.getMrimAccountList().firstElement());
        return ScreenId.INVITE_TOS;
    }

    /* renamed from: a */
    public static final int handleServerAction(int i, String str) {
        switch (i) {
            case 0:
                return ScreenId.VISIBLE_CONTACTS;
            case 1:
                return ScreenId.PHOTO_SELECTOR;
            case 2:
                return ScreenId.PHOTO_VIEW;
            case 3:
                return ScreenId.SHARE_LOCATION;
            default:
                if (AppState.getString(StateKeys.STR_CMD_SHOW_LIST).equals(str)) {
                    return ScreenId.PROFILE_EDIT;
                }
                if (AppState.getString(StateKeys.STR_CMD_SHOW_MAP).equals(str)) {
                    return ScreenId.WIFI_ACCOUNT_LIST;
                }
                int optionId = Integer.parseInt(StringUtils.suffix(str, 7));
                return (!(optionId >= 4 && optionId <= 53) || optionId == 25 || optionId == 31) ? (i & Integer.MIN_VALUE) != 0 ? ScreenId.MAP_SEARCH : ScreenId.PHOTO_SELECTOR : optionId + 157;
        }
    }

    /* renamed from: w */
    public static final void processEventQueue() {
        Screen screen = ScreenManager.createScreen(ScreenDef.DIALOG_SCREEN);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Enumeration chatRooms = account.chatRoomsList.elements();
        while (chatRooms.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) chatRooms.nextElement();
            if (chatRoom != account.getLastChatRoom()) {
                MenuItem menuItem = MenuItem.createDefault().setIcon(234).setLabel(chatRoom.name);
                menuItem.data = chatRoom;
                screen.addItem(menuItem);
            }
        }
        ScreenManager.showScreen(screen);
    }

    /* renamed from: e */
    public static final int handleEventObject(Object obj) {
        AppState.setInt(StateKeys.INT_ACTIVE_CHATROOM_ID, ((ChatRoom) obj).id);
        return 0;
    }

    /* renamed from: o */
    public static final int handleNotificationOption(int i) {
        if (i == 54) {
            ScreenBuilder.onScreenClosed();
            ResourceManager.composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (i == 68) {
            ScreenBuilder.onScreenClosed();
            toggleOnlineMode(true);
            return 0;
        }
        if (i != 37) {
            return 0;
        }
        ((MrimAccount) AppState.getAccount()).chatRoomsLoaded = true;
        return 0;
    }

    /* renamed from: a */
    public static final int sortContacts(Vector vector) {
        int size = vector.size();
        sortRange(vector, 0, size - 1);
        return size;
    }

    /* renamed from: a */
    private static final void sortRange(Vector vector, int left, int right) {
        if (left < right) {
            if (left + 1 == right) {
                if (((Sortable) vector.elementAt(left)).compareTo(vector.elementAt(right)) > 0) {
                    Utils.swapElements(vector, left, right);
                    return;
                }
                return;
            }
            int lo = left;
            int hi = right;
            boolean moveLow = true;
            while (lo < hi) {
                if (((Sortable) vector.elementAt(lo)).compareTo(vector.elementAt(hi)) > 0) {
                    Utils.swapElements(vector, lo, hi);
                    moveLow = !moveLow;
                }
                if (moveLow) {
                    lo++;
                } else {
                    hi--;
                }
            }
            sortRange(vector, left, lo - 1);
            sortRange(vector, hi + 1, right);
        }
    }

    /* renamed from: x */
    public static final void processBackgroundTasks() {
        updateTimerSlot(0);
    }

    /* renamed from: y */
    public static final void markScreenDirty() {
        if (AppState.getBool(StateKeys.FLAG_HAS_MRIM_ACCOUNT)) {
            updateTimerSlot(Integer.MAX_VALUE);
            setTimer(0, getSessionTimestamp());
        }
    }

    /* renamed from: z */
    public static final void processTimers() {
        updateTimerSlot(Integer.MAX_VALUE);
    }

    /* renamed from: L */
    private static final void updateTimerSlot(int i) {
        if (AppState.getBool(StateKeys.FLAG_HAS_SAVED_ACCOUNTS)) {
            try {
                Display.getDisplay(AppState.getMidlet()).flashBacklight(i);
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: p */
    public static final int getThemeColor(int i) {
        int size = AppState.getVector(StateKeys.VEC_ACCOUNTS).size();
        while (true) {
            size--;
            if (size < 0) {
                return 0;
            }
            AccountManager.getAccountByIndex(size).onError(i);
        }
    }

    /* renamed from: q */
    public static final int getThemeBackground(int i) {
        switch (i) {
            case 0:
                Conversation.incrementZoom();
                break;
            case 1:
                Conversation.decrementZoom();
                break;
            case 2:
                AppState.setInt(StateKeys.SETTING_CUSTOM_VIEW_MODE, 1);
                break;
            case 3:
                AppState.setInt(StateKeys.SETTING_CUSTOM_VIEW_MODE, 0);
                break;
            default:
                return 0;
        }
        MapRenderer.needsRedraw = true;
        return ScreenId.MAP;
    }

    /* renamed from: A */
    public static final int getScreenMode1() {
        return computeLayoutParam(1004);
    }

    /* renamed from: B */
    public static final int getScreenMode2() {
        return computeLayoutParam(1005);
    }

    /* renamed from: C */
    public static final int getScreenMode3() {
        return Integer.parseInt(StringUtils.getSystemProp(1006));
    }

    /* renamed from: D */
    public static final int getScreenMode4() {
        return Integer.parseInt(StringUtils.getSystemProp(1007));
    }

    /* renamed from: N */
    private static final int computeLayoutParam(int i) {
        return Integer.parseInt(StringUtils.getSystemProp(i), 16);
    }

    /* renamed from: r */
    public static final int handleViewOption(int i) {
        if (i == 120) {
            if (!MapController.hasRoutePoints()) {
                return NotificationHelper.showError(354);
            }
            AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 1);
            return 0;
        }
        if (i != 100) {
            return i == 0 ? ScreenId.MAP : 0;
        }
        AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 1);
        return 0;
    }

    /* renamed from: s */
    public static final int handleThemeOption(int i) {
        if (i == 10) {
            return AppController.getIconOffset();
        }
        return 0;
    }

    /* renamed from: g */
    public static final int processInputText(String str) {
        AppState.setBool(StateKeys.FLAG_SPECIAL_KEY_MODE, StringUtils.matchesKey(859, str));
        return 0;
    }

    /* renamed from: a */
    public static final int handleInputAction(int i, Object obj) {
        AppState.setAccount(obj);
        if (obj != null) {
            return ScreenId.CONTACT_GROUPS;
        }
        if (i > 3) {
            return i;
        }
        AppState.setInt(StateKeys.INT_PROTOCOL_TYPE, i);
        return ScreenId.XMPP_LOGIN;
    }

    /* renamed from: t */
    public static final int handleSoundOption(int i) {
        AppState.setFromBuffer(StateKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(AppState.getString(i + (AppState.getCurrentContact() instanceof MmpContact ? 1141 : AppState.getCurrentContact() instanceof XmppContact ? 1184 : 1063))));
        return ScreenId.STATUS_INPUT;
    }

    /* renamed from: E */
    public static final long getSessionTimestamp() {
        switch (AppState.getInt(StateKeys.SETTING_NETWORK_MODE)) {
            case 1:
                return 15000L;
            case 2:
                return 30000L;
            case 3:
                return 60000L;
            case 4:
                return 300000L;
            default:
                return 4294967295L;
        }
    }

    /* renamed from: F */
    public static final void clearFormFields() {
        AppState.clearRange(StateKeys.SLOT_INPUT_TEXT, StateKeys.RANGE_INPUT_TEXT_END);
    }

    /* renamed from: f */
    public static final int handleFormSubmit(Object obj) {
        XmppMailRuProtocol.mapContextItem = (ListItem) obj;
        return 0;
    }

    /* renamed from: a */
    public static final void waitForCompletion(Object[] objArr) throws InterruptedException {
        int remaining = 15000;
        do {
            remaining -= 500;
            if (remaining < 0) {
                IOUtils.postEvent(new AccountDataEvent(objArr));
                return;
            }
            Thread.sleep(500L);
        } while (!isShuttingDown);
    }

    /* renamed from: u */
    public static final int handleChatOption(int i) {
        if (i != 54) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
            clearSearchState();
            return 0;
        }
        MrimContact contact = AppState.getCurrentMrimContact();
        AppState.setAccount(contact.account);
        ResourceManager.composeEmail(XmppMailRuProtocol.parseRecipientList(contact.simpleIdentifier), (String) null, (String) null);
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: g */
    public static final int handleConversationAction(Object obj) {
        if (AppState.getBool(StateKeys.FLAG_NEW_MESSAGE)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 0;
        }
        if (!AppState.getBool(StateKeys.FLAG_LOADING)) {
            MapController.navigateToPoint((MapPoint) obj, true);
            return 0;
        }
        MapPoint mapPoint = (MapPoint) obj;
        ((MrimAccount) AppState.getAccount()).setLocationProfile(mapPoint);
        XmppContactGroup.addMapPointIfNew(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), mapPoint, 0, 5);
        XmppContactGroup.saveMapPoints(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), 225);
        AppState.setInt(StateKeys.FLAG_LOADING, 0);
        return ScreenId.PROFILE_EDIT;
    }

    /* renamed from: c */
    private static int interpolateColor(int i, int i2, int i3) {
        AppState.setInt(StateKeys.INT_SETTINGS_THEME, i);
        AppState.setInt(StateKeys.INT_SETTINGS_VALUE_1, i);
        AppState.setInt(StateKeys.INT_SETTINGS_VALUE_2, i2);
        AppState.setInt(StateKeys.INT_SETTINGS_ACTION, i3);
        return ScreenId.CHAT_ROOM_CONFIG;
    }

    /* renamed from: v */
    public static final int handleChatRoomOption(int i) {
        if (i == 0) {
            MapController.setRouteStart();
            if (MmpContact.hasSecondToken()) {
                return ScreenId.MAP;
            }
            AppState.setInt(StateKeys.FLAG_MAP_MODE_ACTIVE, 1);
            return ScreenId.MAP_SEARCH;
        }
        MapController.setRouteEnd();
        if (MmpContact.hasFirstToken()) {
            return ScreenId.MAP;
        }
        AppState.setInt(StateKeys.FLAG_MAP_MODE_ACTIVE, 0);
        return ScreenId.MAP_SEARCH;
    }

    /* renamed from: w */
    public static final int handleMailboxOption(int i) {
        if (i != 0) {
            AppState.setInt(StateKeys.FLAG_LOADING, 1);
            return ScreenId.MAP_POINTS;
        }
        AppState.setInt(StateKeys.FLAG_MAP_LOADING, 1);
        ((MrimAccount) AppState.getAccount()).isHighlighted = false;
        return ScreenId.CLOSE;
    }

    /* renamed from: a */
    public static final void handleMmpPacket(MmpProtocol protocol, ByteBuffer buffer) {
        buffer.skip(6);
        while (buffer.length > 0) {
            int tag = buffer.readShortBE();
            int length = buffer.readShortBE();
            if (tag == 9 && length == 2) {
                int statusCode = buffer.readShortBE();
                if (statusCode == 1) {
                    protocol.handleTimeout();
                    return;
                } else {
                    protocol.handleError(statusCode);
                    return;
                }
            }
            buffer.skip(length);
        }
        protocol.handleError(-1);
    }

    /* renamed from: x */
    public static final int handleChatListOption(int i) {
        MapPoint mapPoint = pendingMapPoint;
        if (mapPoint == null) {
            return NotificationHelper.showError(354);
        }
        if (i == 6) {
            MapRenderer.navigateToMapPoint(pendingMapPoint);
            return 0;
        }
        if (i == 118) {
            AppState.setObject(StateKeys.MAP_RESOURCE_URL, (Object) mapPoint.getResourceUrl());
            return 0;
        }
        if (i != 120) {
            return 0;
        }
        MapController.removeRoutePoint(mapPoint);
        return 0;
    }

    /* renamed from: y */
    public static final int handleChatDetailOption(int i) {
        MapController.showMapView();
        if (i == 6) {
            applyViewMode(true, false, !AppState.getBool(StateKeys.FLAG_MAP_VIEW_ACTIVE));
            AppState.setInt(StateKeys.FLAG_REFRESH_CONTACTS, 1);
            AppState.setInt(StateKeys.FLAG_MAP_LOADING, 1);
            return 0;
        }
        if (i == 100) {
            AppState.setInt(StateKeys.FLAG_LOADING, 1);
            return 0;
        }
        if (!MapController.hasRoutePoints()) {
            return NotificationHelper.showError(354);
        }
        AppState.setInt(StateKeys.FLAG_CONTACTS_LOADED, 1);
        return 0;
    }

    /* renamed from: z */
    public static final int handleChatSettingsOption(int i) {
        if (i == 22 || i == 143 || i == 24 || i == 23) {
            return AccountManager.showAccountList(AccountManager.getMrimAccountList(), i, false);
        }
        if (i == 21 || i == 69 || i == 124) {
            return AccountManager.showAccountList(AccountManager.getXmppAccountList(), i, false);
        }
        return 0;
    }

    /* renamed from: G */
    public static final void showSettingsScreen() {
        AppState.setInt(StateKeys.FLAG_INIT_COMPLETE, 0);
        AppState.setInt(StateKeys.FLAG_FULLSCREEN_ACTIVE, 1);
        RemoteLogger.log("UI", "showSettingsScreen: before createScreen(ScreenDef.SETTINGS_MAIN)");
        Screen s = ScreenManager.createScreen(ScreenDef.SETTINGS_MAIN);
        RemoteLogger.log("UI", "showSettingsScreen: screenId=" + s.screenId);
        RemoteLogger.log("UI", "showSettingsScreen: pushScreen, stack=" + AppState.getVector(StateKeys.VEC_SCREEN_STACK).size());
        ScreenManager.pushScreen(s);
        RemoteLogger.log("UI", "showSettingsScreen done");
    }

    /* renamed from: H */
    public static final void clearSearchResults2() {
        AppState.clearRange(StateKeys.SLOT_REG_PARAM_3, StateKeys.SLOT_CONTACT_INFO);
    }

    /* renamed from: I */
    public static final void prepareFormData() {
        AppState.setInt(StateKeys.INT_ERROR_MSG_INDEX, 0);
        AppState.clearIndex(StateKeys.SLOT_REG_PARAM_4);
        AppState.pool[StateKeys.SLOT_REG_PARAM_3] = ObjectPool.newVector();
    }

    /* renamed from: a */
    public static final void openUserProfile(MrimAccount account, String str) {
        pendingAccount = account;
        pendingUrl = str;
    }

    /* renamed from: J */
    public static final void clearMapPoints() {
        pendingAccount = null;
        pendingUrl = null;
    }

    /* renamed from: h */
    public static final int processPhoneInput(String str) {
        String newValue = AppState.getString(StateKeys.SLOT_STATUS_TEXT);
        int i = 15;
        do {
            i--;
            if (i < 0) {
                return 0;
            }
        } while (AppState.getString(i + 48) != str);
        AppState.setObject(i + 48, (Object) newValue);
        return 0;
    }

    /* renamed from: h */
    public static final int handleSearchAction(Object obj) {
        ContactGroup group = (ContactGroup) obj;
        if (null == group) {
            return ScreenId.CONTACT_LIST;
        }
        Contact contact = AppState.getCurrentContact();
        int errorCode = contact.isOnline() ? 310 : contact.account.validateMove(contact, contact.account.findGroup(contact), group);
        if (0 != errorCode) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    /* renamed from: i */
    public static final int handleSearchResultAction(Object obj) {
        MapRenderer.invalidate();
        GeoRegion region = (GeoRegion) obj;
        MapRenderer.setPosition(region.centerLat, region.centerLon);
        MapRenderer.setZoom(region == StringUtils.getGeoRegion() ? 3 : 11);
        return 0;
    }

    /* renamed from: K */
    public static final int handleInviteAction() {
        long lon;
        long lat;
        ListItem item = MapRenderer.tooltipItem;
        if (item != null) {
            lon = item.getWidth();
            lat = item.getBaseHeight();
        } else {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        }
        AppState.setInt(StateKeys.FLAG_MAP_LOADING, 0);
        ResourceManager.startGeoSearch(VCard.formatLocationUrl(AppState.getInt(StateKeys.MAP_ZOOM_LEVEL), IOUtils.pixelToLongitude(lon), IOUtils.pixelToLatitude(lat)), lon, lat);
        return ScreenId.MAP;
    }

    /* renamed from: L */
    public static final int handleInviteResult() {
        char errorCode;
        Account account = AppState.getAccount();
        if (account.isConnecting()) {
            errorCode = 300;
        } else {
            AppState.getVector(StateKeys.VEC_ACCOUNTS).removeElement(account);
            TabBar.initialize();
            AccountManager.saveAccountList();
            errorCode = 0;
        }
        if (0 != errorCode) {
            return NotificationHelper.showError(300);
        }
        return ScreenId.MULTI_ACCOUNT_LIST;
    }

    /* renamed from: A */
    public static final int handleAccountSwitchOption(int i) {
        Contact contact = AppState.getCurrentContact();
        switch (i) {
            case 0:
                int blockError = contact.validateBlock();
                if (0 != blockError) {
                    return NotificationHelper.showError(blockError);
                }
                return ScreenId.CONTACT_LIST;
            case 1:
                int unblockError = contact.validateUnblock();
                if (0 != unblockError) {
                    return NotificationHelper.showError(unblockError);
                }
                return ScreenId.CONTACT_LIST;
            default:
                return 0;
        }
    }

    /* renamed from: M */
    public static final void initChatRoomList() {
        Screen screen = ScreenManager.createScreen(ScreenDef.INPUT_FORM);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Enumeration chatRooms = account.chatRoomsList.elements();
        while (chatRooms.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) chatRooms.nextElement();
            if (chatRoom != account.getLastChatRoom()) {
                MenuItem menuItem = MenuItem.createDefault().setIcon(234).setLabel(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(chatRoom.name).append(' ').append('['))).addText(StringUtils.intern(Integer.toString(chatRoom.unreadCount)), 1, 0).setLabel(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append('/').append(chatRoom.memberCount).append(']')));
                menuItem.data = chatRoom;
                screen.addItem(menuItem);
            }
        }
        ScreenManager.showScreen(screen);
    }

    /* renamed from: j */
    public static final int handleMapSearchAction(Object obj) {
        long lon;
        long lat;
        Contact contact = (Contact) obj;
        String query = AppState.getString(StateKeys.SLOT_TOOLTIP_TEXT_1);
        ListItem item = MapRenderer.tooltipItem;
        if (item == null || !item.isSelected()) {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        } else {
            lon = item.getWidth();
            lat = item.getBaseHeight();
        }
        int errorCode = contact.sendMessage(ResourceManager.buildTileRequestUrl(lon, lat, AppState.getInt(StateKeys.MAP_ZOOM_LEVEL), query));
        if (0 != errorCode) {
            return NotificationHelper.showError(errorCode);
        }
        return 0;
    }

    /* renamed from: k */
    public static final int handleMapResultAction(Object obj) {
        AppState.setObject(StateKeys.MAP_RESOURCE_URL, (Object) ((MapPoint) obj).getResourceUrl());
        return 0;
    }

    /* renamed from: i */
    public static final int processSearchQuery(String str) {
        if (!AppState.getString(StateKeys.STR_PROTOCOL_XMPP).equals(str)) {
            return 0;
        }
        if (MapRenderer.selectedMapPoint != null) {
            MapRenderer.selectedMapPoint.markInactive();
        }
        XmppContactGroup.startMapAnimation(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE));
        AppState.setInt(StateKeys.FLAG_GPS_ACTIVE, 0);
        MmpContact.clearLocationData();
        MapRenderer.needsRedraw = true;
        XmppContactGroup.lastCheckTs = System.currentTimeMillis();
        MapRenderer.needsRedraw = true;
        return 0;
    }

    /* renamed from: l */
    public static final int handleLocationAction(Object obj) {
        if (obj == null) {
            return 0;
        }
        AppState.setFromBuffer(StateKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(obj));
        return 0;
    }

    /* renamed from: B */
    public static final int handleMapMenuOption(int i) {
        int activeCount = AccountManager.getActiveAccountCount();
        if (i == 15) {
            if (activeCount == 0) {
                return NotificationHelper.showError(551);
            }
            if (activeCount == 1) {
                AccountManager.rebuildAccountCaches();
                return ScreenId.CONTACT_LIST;
            }
        } else {
            if (i == 3) {
                Vector accounts = AccountManager.getXmppAccountList();
                int result = AccountManager.showAccountList(accounts, 3, true);
                if (result != 39) {
                    return result;
                }
                accounts.insertElementAt(accounts, 0);
                return ScreenId.ACCOUNT_CHECKBOX_LIST;
            }
            if (i == 152) {
                return AccountManager.showAccountList(AccountManager.getMrimAccountList(), 152, true);
            }
        }
        if (i == 10) {
            return AppController.getIconOffset();
        }
        if (i != 6) {
            return 0;
        }
        AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
        return 0;
    }

    /* renamed from: m */
    public static final int handleFileAction(Object obj) {
        int errorCode = ((Contact) obj).sendMessage(AppState.getString(StateKeys.MAP_RESOURCE_URL));
        if (0 != errorCode) {
            return NotificationHelper.showError(errorCode);
        }
        return 0;
    }

    /* renamed from: a */
    public static final void markContactRead(Contact contact) {
        markScreenDirty();
        Vector contacts = AppState.getVector(StateKeys.VEC_ONLINE_CONTACTS);
        if (contacts.contains(contact)) {
            return;
        }
        contacts.addElement(contact);
        TabBar.layout();
    }

    /* renamed from: b */
    public static final void markContactUnread(Contact contact) {
        Vector contacts = AppState.getVector(StateKeys.VEC_ONLINE_CONTACTS);
        if (contacts.contains(contact)) {
            Utils.removeFrom(contacts, contact);
            TabBar.layout();
        }
    }

    /* renamed from: c */
    public static final void deleteContact(Contact contact) {
        contact.clearStatus();
        AppState.getVector(StateKeys.VEC_PENDING_CONNECTIONS).removeElement(contact);
        needsLayoutUpdate = true;
    }

    /* renamed from: X */
    public static final Vector getMapContacts() {
        Vector result = ObjectPool.newVector();
        Vector mrimAccounts = AccountManager.getMrimAccountList();
        int size = mrimAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            Vector contacts = AccountManager.findMrimAccount(mrimAccounts, size).getAllContacts();
            int size2 = contacts.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                MrimContact contact = (MrimContact) contacts.elementAt(size2);
                if (contact.hasVCard()) {
                    result.addElement(contact);
                }
            }
            ObjectPool.releaseVector(contacts);
        }
    }

    /* renamed from: Y */
    public static final Vector getMapProfiles() {
        Vector result = ObjectPool.newVector();
        Vector mrimAccounts = AccountManager.getMrimAccountList();
        int size = mrimAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            MrimAccount account = AccountManager.findMrimAccount(mrimAccounts, size);
            if (account.accountProfile.hasCoordinates()) {
                result.addElement(account);
            }
        }
    }

    /* renamed from: a */
    public static final void setFormFields(String str, String str2, String str3, String str4, String str5) {
        AppState.setObject(StateKeys.SLOT_LANGUAGE_OPTION, (Object) str5);
        AppState.setFromBuffer(StateKeys.SLOT_INIT_PARAMS, Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(ObjectPool.newStringBuffer(), 262572, str), 262576, str2), 524724, str3), 590268, str4), 524741, str5));
        setTimer(13, computeInitialState());
    }

    /* renamed from: aj */
    private static final int computeInitialState() {
        return AppState.getBytes(StateKeys.RES_UPDATE_DATA) != null ? 60000 : 300000;
    }

    /* renamed from: Z */
    public static final String[] getLanguageOptions() {
        if (!AppState.getBool(StateKeys.FLAG_CAPTCHA_SHOWN)) {
            setFormFields(null, null, null, null, null);
        } else if (checkTimer(13, computeInitialState())) {
            AppState.clearIndex(StateKeys.SLOT_INIT_PARAMS);
        }
        String formData = AppState.getString(StateKeys.SLOT_INIT_PARAMS);
        if (formData == null) {
            return null;
        }
        String[] strArr = new String[2];
        strArr[0] = formData;
        String langOption = AppState.getString(StateKeys.SLOT_LANGUAGE_OPTION);
        strArr[1] = langOption != null ? langOption : AppState.getString(StateKeys.STR_DEFAULT_LANGUAGE);
        return strArr;
    }

    /* renamed from: a */
    public static final void dispatchCommand(Object obj, int i, int i2) {
        Object obj2 = new Object();
        appLock = obj2;
        synchronized (obj2) {
            RemoteLogger.init();
            RemoteLogger.log("INIT", "dispatchCommand START");
            AppState.init(obj);
            AppState.clearRange(StateKeys.RANGE_SESSION_TEMP_START, StateKeys.RANGE_SESSION_TEMP_END);
            AppState.pool[StateKeys.SLOT_MAP_TILE_REQUEST] = ObjectPool.newVector();
            AppState.pool[StateKeys.VEC_SCREEN_STACK] = ObjectPool.newVector();
            ScreenManager.initializeFonts();
            AppState.pool[StateKeys.VEC_ONLINE_CONTACTS] = ObjectPool.newVector();
            AppState.pool[StateKeys.VEC_ACTIVE_CONNECTIONS] = ObjectPool.newVector();
            AppState.pool[StateKeys.SLOT_MEDIA_CONTROL] = ObjectPool.newVector();
            AppState.pool[StateKeys.SLOT_MEDIA_VOLUME] = ObjectPool.newVector();
            RemoteLogger.log("INIT", "fonts done, starting AsyncTask(3)");
            new AsyncTask(AsyncTaskId.CONNECTION_LOOP);
            AccountManager.loadSavedAccounts();
            RemoteLogger.log("INIT", "accounts loaded: " + AppState.getVector(StateKeys.VEC_ACCOUNTS).size());
            AppState.pool[StateKeys.VEC_POPUP_ITEMS] = ObjectPool.newVector();
            processKeyRepeat();
            AppState.pool[StateKeys.VEC_PENDING_CONNECTIONS] = ObjectPool.newVector();
            ResourceManager.resetClock();
            ResourceManager.initMathTables();
            AppState.setInt(StateKeys.TRAFFIC_MRIM_SENT_BYTES, 0);
            AppState.setInt(StateKeys.TRAFFIC_MRIM_RECV_BYTES, 0);
            AppState.setInt(StateKeys.TRAFFIC_MMP_SENT_BYTES, 0);
            AppState.setInt(StateKeys.TRAFFIC_MMP_RECV_BYTES, 0);
            AppState.setInt(StateKeys.TRAFFIC_XMPP_SENT_BYTES, 0);
            AppState.setInt(StateKeys.TRAFFIC_XMPP_RECV_BYTES, 0);
            AppState.setInt(StateKeys.TRAFFIC_TOTAL_SENT_BYTES, 0);
            AppState.setInt(StateKeys.TRAFFIC_TOTAL_RECV_BYTES, 0);
            AppState.pool[StateKeys.VEC_TILE_QUEUE] = ObjectPool.newVector();
            XmppMailRuProtocol.calculateCacheSize();
            RemoteLogger.log("INIT", "creating MainCanvas w=" + i + " h=" + i2);
            AppState.pool[StateKeys.OBJ_CANVAS] = new MainCanvas(i, i2);
            RemoteLogger.log("INIT", "MainCanvas created");
            AppState.clearRange(StateKeys.RANGE_TEMP_DATA_START, StateKeys.RANGE_TEMP_DATA_END);
            TabBar.initialize();
            RemoteLogger.log("INIT", "TabBar initialized, tabs=" + AppState.getVector(StateKeys.VEC_TAB_BARS).size());
            AppState.pool[StateKeys.RES_EMOTICON_MAP] = Utils.bytesToInts(AppState.getBytes(StateKeys.RES_EMOTICON_MAP));
            AppState.pool[StateKeys.SLOT_MEDIA_RESOURCE] = new byte[1];
            try {
                computeLayoutParam(1004);
                computeLayoutParam(1005);
                getScreenMode3();
                getScreenMode4();
            } catch (Throwable unused) {
                AppState.clearRange(StateKeys.RES_UPDATE_DATA, StateKeys.RANGE_UPDATE_DATA_END);
            }
            setTimer(0, getSessionTimestamp());
            AppState.addInt(StateKeys.COUNTER_APP_STARTS, 1);
            AppState.saveDelta(true);
            RemoteLogger.log("INIT", "getBool(217)=" + AppState.getBool(StateKeys.FLAG_INIT_COMPLETE));
            RemoteLogger.log("INIT", "free=" + Runtime.getRuntime().freeMemory() + " total=" + Runtime.getRuntime().totalMemory());
            if (AppState.getBool(StateKeys.FLAG_INIT_COMPLETE)) {
                RemoteLogger.log("INIT", "calling showSettingsScreen");
                showSettingsScreen();
                RemoteLogger.log("INIT", "showSettingsScreen done");
            } else {
                int accountCount = AccountManager.getActiveAccountCount();
                RemoteLogger.log("INIT", "accountCount=" + accountCount);
                if (accountCount == 0) {
                    ScreenManager.pushScreen(ScreenManager.createScreen(ScreenDef.PHOTO_VIEW));
                    refreshContactList();
                } else {
                    while (true) {
                        accountCount--;
                        if (accountCount < 0) {
                            break;
                        } else {
                            AccountManager.setCurrentAccount(AccountManager.getAccountByIndex(accountCount));
                        }
                    }
                    RemoteLogger.log("CL", "showing contact list");
                    ContactListManager.showContactList();
                    refreshContactList();
                }
            }
            RemoteLogger.log("INIT", "starting event loop threads");
            new AsyncTask(AsyncTaskId.PERIODIC_TIME_SYNC);
            new AsyncTask(AsyncTaskId.PROCESS_SOFTKEY);
            RemoteLogger.log("INIT", "dispatchCommand DONE");
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:286:0x0b19  */
    /* JADX WARN: Removed duplicated region for block: B:635:0x1396  */
    /* JADX WARN: Removed duplicated region for block: B:646:0x13cd  */
    /* JADX WARN: Removed duplicated region for block: B:714:0x151f  */
    /* JADX WARN: Removed duplicated region for block: B:755:0x15ff  */
    /* JADX WARN: Removed duplicated region for block: B:794:0x16cc  */
    /* JADX WARN: Removed duplicated region for block: B:797:0x16d3  */
    /* JADX WARN: Removed duplicated region for block: B:804:0x16e9 A[Catch: all -> 0x1e12, Throwable -> 0x1f03, TryCatch #0 {, blocks: (B:5:0x0009, B:7:0x000f, B:9:0x001b, B:11:0x0023, B:13:0x0031, B:14:0x0047, B:15:0x0048, B:16:0x0061, B:23:0x006d, B:20:0x0069, B:22:0x006c, B:24:0x0071, B:39:0x00ef, B:25:0x0082, B:26:0x008e, B:28:0x0096, B:34:0x00c2, B:36:0x00d7, B:37:0x00e1, B:30:0x00a0, B:32:0x00b5, B:38:0x00e9, B:41:0x00f7, B:45:0x0131, B:42:0x0110, B:44:0x012c, B:47:0x0139, B:49:0x013f, B:51:0x0149, B:53:0x0150, B:54:0x016b, B:56:0x0179, B:64:0x01a9, B:65:0x01b4, B:70:0x04a7, B:72:0x04b5, B:74:0x04c9, B:75:0x04d5, B:77:0x04dd, B:79:0x04e3, B:81:0x04ec, B:83:0x04f6, B:85:0x0500, B:87:0x050a, B:89:0x0514, B:96:0x0532, B:91:0x0521, B:97:0x0535, B:98:0x0539, B:100:0x0544, B:102:0x0550, B:104:0x0559, B:105:0x0589, B:106:0x05a8, B:107:0x05be, B:108:0x05d4, B:109:0x05ea, B:110:0x05fd, B:111:0x0617, B:113:0x0623, B:114:0x0626, B:116:0x062f, B:118:0x063a, B:122:0x066e, B:119:0x0655, B:121:0x0668, B:124:0x0676, B:125:0x0682, B:127:0x068b, B:128:0x068f, B:130:0x0695, B:134:0x06a6, B:142:0x06c5, B:144:0x06e0, B:145:0x06ee, B:147:0x06fc, B:151:0x070b, B:149:0x0704, B:164:0x0743, B:179:0x0783, B:181:0x0792, B:184:0x07aa, B:188:0x07c7, B:193:0x07db, B:201:0x07f6, B:203:0x0802, B:206:0x0812, B:208:0x0833, B:210:0x0871, B:209:0x0863, B:212:0x087b, B:214:0x08b5, B:213:0x0890, B:216:0x08bf, B:220:0x08ed, B:217:0x08cb, B:219:0x08e3, B:222:0x08f7, B:226:0x0925, B:223:0x0903, B:225:0x091b, B:228:0x092f, B:229:0x0937, B:231:0x096f, B:230:0x094a, B:235:0x0982, B:237:0x0991, B:240:0x09a1, B:241:0x09bc, B:261:0x0a55, B:242:0x09d0, B:244:0x09f7, B:248:0x0a08, B:250:0x0a10, B:252:0x0a1a, B:253:0x0a20, B:254:0x0a25, B:256:0x0a31, B:257:0x0a36, B:260:0x0a48, B:263:0x0a5f, B:268:0x0a70, B:270:0x0a89, B:272:0x0aab, B:274:0x0abb, B:278:0x0aee, B:275:0x0ad0, B:277:0x0ae3, B:282:0x0afc, B:285:0x0b0e, B:288:0x0b1f, B:290:0x0b2c, B:291:0x0b34, B:299:0x0b58, B:306:0x0b76, B:307:0x0b85, B:309:0x0b8f, B:311:0x0ba6, B:321:0x0bd0, B:323:0x0bda, B:325:0x0be5, B:327:0x0bee, B:329:0x0bfd, B:331:0x0c0c, B:332:0x0c15, B:346:0x0c5d, B:333:0x0c23, B:345:0x0c5a, B:348:0x0c65, B:350:0x0c6f, B:358:0x0c9c, B:360:0x0ca8, B:363:0x0cb8, B:379:0x0d42, B:364:0x0ccc, B:366:0x0d00, B:371:0x0d10, B:373:0x0d19, B:376:0x0d2d, B:378:0x0d36, B:388:0x0d67, B:390:0x0d76, B:393:0x0d86, B:395:0x0d9e, B:405:0x0de9, B:396:0x0dab, B:398:0x0dc5, B:400:0x0dd1, B:402:0x0dda, B:403:0x0ddf, B:404:0x0de4, B:411:0x0e02, B:413:0x0e0e, B:416:0x0e1e, B:417:0x0e45, B:418:0x0e73, B:421:0x0e9b, B:424:0x0ea4, B:426:0x0eb8, B:427:0x0ed6, B:429:0x0efc, B:431:0x0f1a, B:430:0x0f0b, B:434:0x0f24, B:435:0x0f47, B:437:0x0f52, B:438:0x0f5b, B:440:0x0f72, B:442:0x0f7e, B:445:0x0f8e, B:447:0x0f9a, B:452:0x0fb4, B:473:0x100c, B:482:0x1032, B:524:0x10dc, B:533:0x1104, B:547:0x1141, B:549:0x114f, B:551:0x1159, B:555:0x116b, B:557:0x117c, B:558:0x118b, B:561:0x1196, B:575:0x11d2, B:577:0x11e2, B:578:0x11f7, B:580:0x1206, B:581:0x1248, B:583:0x1255, B:585:0x125d, B:586:0x126f, B:588:0x1279, B:595:0x1292, B:986:0x1db5, B:988:0x1dbd, B:990:0x1dc6, B:991:0x1dd4, B:993:0x1de2, B:995:0x1deb, B:997:0x1df6, B:999:0x1dff, B:1000:0x1e06, B:598:0x129d, B:63:0x01a1, B:59:0x018e, B:600:0x12a5, B:602:0x12ac, B:603:0x12b4, B:604:0x12e8, B:606:0x12f0, B:608:0x12f9, B:609:0x12fd, B:615:0x1338, B:617:0x1341, B:619:0x134d, B:621:0x1358, B:623:0x1360, B:718:0x152c, B:730:0x156f, B:733:0x157e, B:736:0x158a, B:739:0x1598, B:742:0x15a5, B:744:0x15af, B:745:0x15b5, B:747:0x15bf, B:748:0x15c9, B:750:0x15d2, B:754:0x15fb, B:721:0x153f, B:726:0x1559, B:628:0x1374, B:630:0x137f, B:632:0x1387, B:637:0x139d, B:639:0x13a9, B:644:0x13c0, B:648:0x13d4, B:652:0x13e2, B:657:0x13f9, B:664:0x1415, B:667:0x1425, B:670:0x1433, B:673:0x1441, B:676:0x1456, B:679:0x1466, B:681:0x1471, B:687:0x1488, B:682:0x1478, B:690:0x149c, B:693:0x14b4, B:697:0x14c5, B:700:0x14d8, B:702:0x14de, B:704:0x14e7, B:708:0x14ff, B:710:0x1505, B:712:0x150e, B:756:0x1602, B:757:0x1608, B:758:0x160e, B:759:0x1618, B:761:0x1623, B:763:0x1631, B:765:0x1647, B:767:0x164f, B:769:0x165c, B:774:0x166d, B:778:0x1688, B:780:0x168e, B:784:0x16a6, B:801:0x16de, B:802:0x16e1, B:804:0x16e9, B:806:0x16f2, B:808:0x1727, B:810:0x1733, B:812:0x173d, B:818:0x1758, B:820:0x1762, B:822:0x1770, B:826:0x17a2, B:828:0x17ab, B:830:0x17b5, B:833:0x17c4, B:837:0x17d7, B:839:0x17e2, B:841:0x17ea, B:842:0x17f1, B:843:0x180c, B:844:0x1818, B:846:0x1824, B:848:0x1843, B:858:0x1863, B:857:0x185c, B:788:0x16b7, B:789:0x16ba, B:770:0x1662, B:860:0x187b, B:862:0x1891, B:864:0x18a2, B:866:0x18c4, B:868:0x18cc, B:869:0x18da, B:871:0x1906, B:872:0x1941, B:874:0x1961, B:875:0x1967, B:877:0x197a, B:878:0x198a, B:880:0x1992, B:881:0x1998, B:883:0x199f, B:889:0x19cf, B:891:0x19d6, B:893:0x19e6, B:895:0x1a13, B:898:0x1a2f, B:900:0x1a36, B:901:0x1a44, B:903:0x1a4b, B:905:0x1a57, B:906:0x1aa4, B:907:0x1abc, B:909:0x1ac3, B:910:0x1ad6, B:911:0x1af8, B:912:0x1afe, B:914:0x1b4b, B:915:0x1b5b, B:924:0x1ba1, B:916:0x1b64, B:923:0x1b95, B:919:0x1b83, B:926:0x1ba8, B:928:0x1bb8, B:929:0x1bc8, B:933:0x1bdd, B:937:0x1bf0, B:938:0x1bfc, B:939:0x1c09, B:940:0x1c10, B:941:0x1c17, B:943:0x1c2e, B:945:0x1c35, B:949:0x1c4d, B:951:0x1c65, B:955:0x1c7e, B:957:0x1c8d, B:961:0x1c9e, B:967:0x1d04, B:962:0x1cdb, B:964:0x1cef, B:969:0x1d0b, B:973:0x1d23, B:975:0x1d34, B:974:0x1d2f, B:976:0x1d53, B:979:0x1d61, B:981:0x1d84, B:985:0x1d9c, B:1002:0x1e0e), top: B:1058:0x0009, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:833:0x17c4 A[Catch: all -> 0x1e12, Throwable -> 0x1f03, TryCatch #0 {, blocks: (B:5:0x0009, B:7:0x000f, B:9:0x001b, B:11:0x0023, B:13:0x0031, B:14:0x0047, B:15:0x0048, B:16:0x0061, B:23:0x006d, B:20:0x0069, B:22:0x006c, B:24:0x0071, B:39:0x00ef, B:25:0x0082, B:26:0x008e, B:28:0x0096, B:34:0x00c2, B:36:0x00d7, B:37:0x00e1, B:30:0x00a0, B:32:0x00b5, B:38:0x00e9, B:41:0x00f7, B:45:0x0131, B:42:0x0110, B:44:0x012c, B:47:0x0139, B:49:0x013f, B:51:0x0149, B:53:0x0150, B:54:0x016b, B:56:0x0179, B:64:0x01a9, B:65:0x01b4, B:70:0x04a7, B:72:0x04b5, B:74:0x04c9, B:75:0x04d5, B:77:0x04dd, B:79:0x04e3, B:81:0x04ec, B:83:0x04f6, B:85:0x0500, B:87:0x050a, B:89:0x0514, B:96:0x0532, B:91:0x0521, B:97:0x0535, B:98:0x0539, B:100:0x0544, B:102:0x0550, B:104:0x0559, B:105:0x0589, B:106:0x05a8, B:107:0x05be, B:108:0x05d4, B:109:0x05ea, B:110:0x05fd, B:111:0x0617, B:113:0x0623, B:114:0x0626, B:116:0x062f, B:118:0x063a, B:122:0x066e, B:119:0x0655, B:121:0x0668, B:124:0x0676, B:125:0x0682, B:127:0x068b, B:128:0x068f, B:130:0x0695, B:134:0x06a6, B:142:0x06c5, B:144:0x06e0, B:145:0x06ee, B:147:0x06fc, B:151:0x070b, B:149:0x0704, B:164:0x0743, B:179:0x0783, B:181:0x0792, B:184:0x07aa, B:188:0x07c7, B:193:0x07db, B:201:0x07f6, B:203:0x0802, B:206:0x0812, B:208:0x0833, B:210:0x0871, B:209:0x0863, B:212:0x087b, B:214:0x08b5, B:213:0x0890, B:216:0x08bf, B:220:0x08ed, B:217:0x08cb, B:219:0x08e3, B:222:0x08f7, B:226:0x0925, B:223:0x0903, B:225:0x091b, B:228:0x092f, B:229:0x0937, B:231:0x096f, B:230:0x094a, B:235:0x0982, B:237:0x0991, B:240:0x09a1, B:241:0x09bc, B:261:0x0a55, B:242:0x09d0, B:244:0x09f7, B:248:0x0a08, B:250:0x0a10, B:252:0x0a1a, B:253:0x0a20, B:254:0x0a25, B:256:0x0a31, B:257:0x0a36, B:260:0x0a48, B:263:0x0a5f, B:268:0x0a70, B:270:0x0a89, B:272:0x0aab, B:274:0x0abb, B:278:0x0aee, B:275:0x0ad0, B:277:0x0ae3, B:282:0x0afc, B:285:0x0b0e, B:288:0x0b1f, B:290:0x0b2c, B:291:0x0b34, B:299:0x0b58, B:306:0x0b76, B:307:0x0b85, B:309:0x0b8f, B:311:0x0ba6, B:321:0x0bd0, B:323:0x0bda, B:325:0x0be5, B:327:0x0bee, B:329:0x0bfd, B:331:0x0c0c, B:332:0x0c15, B:346:0x0c5d, B:333:0x0c23, B:345:0x0c5a, B:348:0x0c65, B:350:0x0c6f, B:358:0x0c9c, B:360:0x0ca8, B:363:0x0cb8, B:379:0x0d42, B:364:0x0ccc, B:366:0x0d00, B:371:0x0d10, B:373:0x0d19, B:376:0x0d2d, B:378:0x0d36, B:388:0x0d67, B:390:0x0d76, B:393:0x0d86, B:395:0x0d9e, B:405:0x0de9, B:396:0x0dab, B:398:0x0dc5, B:400:0x0dd1, B:402:0x0dda, B:403:0x0ddf, B:404:0x0de4, B:411:0x0e02, B:413:0x0e0e, B:416:0x0e1e, B:417:0x0e45, B:418:0x0e73, B:421:0x0e9b, B:424:0x0ea4, B:426:0x0eb8, B:427:0x0ed6, B:429:0x0efc, B:431:0x0f1a, B:430:0x0f0b, B:434:0x0f24, B:435:0x0f47, B:437:0x0f52, B:438:0x0f5b, B:440:0x0f72, B:442:0x0f7e, B:445:0x0f8e, B:447:0x0f9a, B:452:0x0fb4, B:473:0x100c, B:482:0x1032, B:524:0x10dc, B:533:0x1104, B:547:0x1141, B:549:0x114f, B:551:0x1159, B:555:0x116b, B:557:0x117c, B:558:0x118b, B:561:0x1196, B:575:0x11d2, B:577:0x11e2, B:578:0x11f7, B:580:0x1206, B:581:0x1248, B:583:0x1255, B:585:0x125d, B:586:0x126f, B:588:0x1279, B:595:0x1292, B:986:0x1db5, B:988:0x1dbd, B:990:0x1dc6, B:991:0x1dd4, B:993:0x1de2, B:995:0x1deb, B:997:0x1df6, B:999:0x1dff, B:1000:0x1e06, B:598:0x129d, B:63:0x01a1, B:59:0x018e, B:600:0x12a5, B:602:0x12ac, B:603:0x12b4, B:604:0x12e8, B:606:0x12f0, B:608:0x12f9, B:609:0x12fd, B:615:0x1338, B:617:0x1341, B:619:0x134d, B:621:0x1358, B:623:0x1360, B:718:0x152c, B:730:0x156f, B:733:0x157e, B:736:0x158a, B:739:0x1598, B:742:0x15a5, B:744:0x15af, B:745:0x15b5, B:747:0x15bf, B:748:0x15c9, B:750:0x15d2, B:754:0x15fb, B:721:0x153f, B:726:0x1559, B:628:0x1374, B:630:0x137f, B:632:0x1387, B:637:0x139d, B:639:0x13a9, B:644:0x13c0, B:648:0x13d4, B:652:0x13e2, B:657:0x13f9, B:664:0x1415, B:667:0x1425, B:670:0x1433, B:673:0x1441, B:676:0x1456, B:679:0x1466, B:681:0x1471, B:687:0x1488, B:682:0x1478, B:690:0x149c, B:693:0x14b4, B:697:0x14c5, B:700:0x14d8, B:702:0x14de, B:704:0x14e7, B:708:0x14ff, B:710:0x1505, B:712:0x150e, B:756:0x1602, B:757:0x1608, B:758:0x160e, B:759:0x1618, B:761:0x1623, B:763:0x1631, B:765:0x1647, B:767:0x164f, B:769:0x165c, B:774:0x166d, B:778:0x1688, B:780:0x168e, B:784:0x16a6, B:801:0x16de, B:802:0x16e1, B:804:0x16e9, B:806:0x16f2, B:808:0x1727, B:810:0x1733, B:812:0x173d, B:818:0x1758, B:820:0x1762, B:822:0x1770, B:826:0x17a2, B:828:0x17ab, B:830:0x17b5, B:833:0x17c4, B:837:0x17d7, B:839:0x17e2, B:841:0x17ea, B:842:0x17f1, B:843:0x180c, B:844:0x1818, B:846:0x1824, B:848:0x1843, B:858:0x1863, B:857:0x185c, B:788:0x16b7, B:789:0x16ba, B:770:0x1662, B:860:0x187b, B:862:0x1891, B:864:0x18a2, B:866:0x18c4, B:868:0x18cc, B:869:0x18da, B:871:0x1906, B:872:0x1941, B:874:0x1961, B:875:0x1967, B:877:0x197a, B:878:0x198a, B:880:0x1992, B:881:0x1998, B:883:0x199f, B:889:0x19cf, B:891:0x19d6, B:893:0x19e6, B:895:0x1a13, B:898:0x1a2f, B:900:0x1a36, B:901:0x1a44, B:903:0x1a4b, B:905:0x1a57, B:906:0x1aa4, B:907:0x1abc, B:909:0x1ac3, B:910:0x1ad6, B:911:0x1af8, B:912:0x1afe, B:914:0x1b4b, B:915:0x1b5b, B:924:0x1ba1, B:916:0x1b64, B:923:0x1b95, B:919:0x1b83, B:926:0x1ba8, B:928:0x1bb8, B:929:0x1bc8, B:933:0x1bdd, B:937:0x1bf0, B:938:0x1bfc, B:939:0x1c09, B:940:0x1c10, B:941:0x1c17, B:943:0x1c2e, B:945:0x1c35, B:949:0x1c4d, B:951:0x1c65, B:955:0x1c7e, B:957:0x1c8d, B:961:0x1c9e, B:967:0x1d04, B:962:0x1cdb, B:964:0x1cef, B:969:0x1d0b, B:973:0x1d23, B:975:0x1d34, B:974:0x1d2f, B:976:0x1d53, B:979:0x1d61, B:981:0x1d84, B:985:0x1d9c, B:1002:0x1e0e), top: B:1058:0x0009, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:859:0x1878  */
    /* JADX WARN: Removed duplicated region for block: B:953:0x1c78  */
    /* renamed from: aa */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void onSoftKeyPressed() {
        boolean z;
        boolean z2;
        int i;
        int i2 = 0;
        Object stateObj;
        boolean z3;
        boolean z4 = false;
        int[] keyArr;
        int[] configArr;
        Screen screen;
        int action = 0;
        Message message;
        TextBox textBox;
        int i3 = 0;
        MrimAccount mrimAccount;
        ChatRoom chatRoom;
        Message message2;
        boolean z5;
        while (!isShuttingDown) {
            synchronized (appLock) {
                if (!isShuttingDown) {
                    AppState.updateTime();
                    ResourceManager.updateClock();
                    if (!MainCanvas.pointerDragged && MainCanvas.pointerDownTime != 0 && System.currentTimeMillis() - MainCanvas.pointerDownTime > 600) {
                        int i4 = MainCanvas.pointerDownX;
                        int i5 = MainCanvas.pointerDownY;
                        Vector vec = AppState.getVector(StateKeys.VEC_EVENT_QUEUE);
                        synchronized (vec) {
                            vec.addElement(PointerEvent.longPress(i4, i5));
                        }
                        MainCanvas.pointerDownTime = 0L;
                    }
                    Vector vec2 = AppState.getVector(StateKeys.VEC_ACCOUNTS);
                    int size = vec2.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            int stateInt = AppState.getInt(StateKeys.INT_CURRENT_TIMESTAMP);
                            Vector vec3 = AppState.getVector(StateKeys.VEC_PENDING_CONNECTIONS);
                            int size2 = vec3.size();
                            while (true) {
                                size2--;
                                if (size2 < 0) {
                                    if (needsLayoutUpdate && ScreenManager.getCurrentScreen().screenId == ScreenId.CONTACT_LIST && isTimerType(1)) {
                                        needsLayoutUpdate = false;
                                        AppState.getString(StateKeys.SLOT_CURRENT_CONTACT_ID);
                                        ContactListManager.refreshList();
                                        AppState.clearIndex(StateKeys.SLOT_CURRENT_CONTACT_ID);
                                        setTimer(1, 1000L);
                                    }
                                    Object event = Utils.dequeue(AppState.getVector(StateKeys.VEC_EVENT_QUEUE));
                                    if (event == null) {
                                        Screen currentScreen = ScreenManager.getCurrentScreen();
                                        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
                                        Object obj = menuItem == null ? null : menuItem.data;
                                        String str = menuItem == null ? null : menuItem.title;
                                        int nextState = 0;
                                        ScreenHandler handler = ScreenHandlerRegistry.getHandler(ScreenManager.getCurrentScreen().screenId);
                                        if (handler != null) {
                                            nextState = handler.onIdleProcess(currentScreen, menuItem, obj, str);
                                        }
                                        if (nextState == 12) {
                                            ScreenBuilder.onScreenClosed();
                                        } else if (nextState != 0) {
                                            ScreenBuilder.openScreen(nextState);
                                        }
                                    } else if (event instanceof KeyEvent) {
                                        KeyEvent keyEvt = (KeyEvent) event;
                                                Screen screen3 = ScreenManager.getCurrentScreen();
                                                if (screen3 != null) {
                                                    if (screen3.screenId != ScreenId.MAP) {
                                                        needsRepaint = true;
                                                    }
                                                    int i13 = keyEvt.keyCode;
                                                    int i14 = keyEvt.gameAction;
                                                    int i15 = ScreenManager.getCurrentScreen().screenId;
                                                    int i16 = TabBar.currentIndex;
                                                    int size9 = AppState.getVector(StateKeys.VEC_TAB_BARS).size();
                                                    boolean z6 = i16 == size9 - 1;
                                                    z4 = false;
                                                    if (i15 == 4) {
                                                        ContactListManager.clearState();
                                                        if (size9 > 1) {
                                                            AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 0);
                                                            if (i14 == 2) {
                                                                if (screen3.isAtStart()) {
                                                                    TabBar prevTab = TabBar.getPreviousTab();
                                                                    if (prevTab != null) {
                                                                        ScreenBuilder.openScreen(prevTab.selectTab());
                                                                    }
                                                                    z4 = true;
                                                                }
                                                            } else if (i14 == 5) {
                                                                if (screen3.isAtEnd()) {
                                                                    TabBar nextTab = TabBar.getNextTab();
                                                                    if (nextTab != null) {
                                                                        ScreenBuilder.openScreen(nextTab.selectTab());
                                                                    }
                                                                    z4 = true;
                                                                }
                                                            }
                                                        }
                                                    } else if (i15 == 36) {
                                                        AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 0);
                                                        if (i14 == 2) {
                                                            ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
                                                            z4 = true;
                                                        } else if (i14 == 5) {
                                                            if (!z6) {
                                                                ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
                                                            }
                                                            z4 = true;
                                                        }
                                                    } else if (i15 == 6) {
                                                        if (AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
                                                            if (i13 == 42) {
                                                                Conversation.incrementZoom();
                                                                z4 = true;
                                                            } else if (i13 == 35) {
                                                                Conversation.decrementZoom();
                                                                z4 = true;
                                                            } else if (i13 == 48) {
                                                                Telemetry.sendReport(false, null);
                                                                ScreenBuilder.openScreen(ScreenId.MAP);
                                                                z4 = true;
                                                            } else if (i13 == 49) {
                                                                ScreenBuilder.openScreen(ScreenId.MAP_POINTS);
                                                                z4 = true;
                                                            } else if (i13 == 50) {
                                                                boolean isEnabled = AppState.getBool(StateKeys.MAP_GPS_ENABLED);
                                                                if (isEnabled) {
                                                                    Conversation.setMapEnabled(false);
                                                                } else {
                                                                    Conversation.setMapEnabled(true);
                                                                }
                                                                AppState.setBool(StateKeys.MAP_GPS_ENABLED, !isEnabled);
                                                                ScreenBuilder.openScreen(ScreenId.MAP);
                                                                z4 = true;
                                                            } else if (i13 == 51) {
                                                                IOUtils.postEvent(new ProtocolEvent(ProtocolEvent.MAP_CONTROL, null));
                                                                z4 = true;
                                                            } else if (i13 == 53) {
                                                                AppState.setBool(StateKeys.SETTING_CUSTOM_VIEW_MODE, !AppState.getBool(StateKeys.SETTING_CUSTOM_VIEW_MODE));
                                                                MapRenderer.needsRedraw = true;
                                                                z4 = true;
                                                            } else if (i13 == 55) {
                                                                if (MmpContact.locationEnabled && (configArr = MmpContact.getPrevRoutePoint()) != null) {
                                                                    MapRenderer.animateTo(configArr[0], configArr[1]);
                                                                }
                                                                z4 = true;
                                                            } else if (i13 == 57) {
                                                                if (MmpContact.locationEnabled && (keyArr = MmpContact.getNextRoutePoint()) != null) {
                                                                    MapRenderer.animateTo(keyArr[0], keyArr[1]);
                                                                }
                                                                z4 = true;
                                                            }
                                                        } else {
                                                            if (i14 == 2) {
                                                                ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
                                                                z4 = true;
                                                            } else if (i14 == 5) {
                                                                if (!z6) {
                                                                    ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
                                                                }
                                                                z4 = true;
                                                            } else if (i14 == 1) {
                                                                z4 = true;
                                                            } else if (i14 == 6) {
                                                                MapController.handleMapSwitch(screen3);
                                                                z4 = true;
                                                            }
                                                        }
                                                    }
                                                    if (!z4) {
                                                        int keyAction = i13 == 42 ? getKeyAction(AppState.getInt(StateKeys.SETTING_KEY_STAR_ACTION)) : i13 == 35 ? getKeyAction(AppState.getInt(StateKeys.SETTING_KEY_HASH_ACTION)) : (i13 < 48 || i13 > 57) ? 0 : getKeyAction(AppState.getInt(i13 + 159));
                                                        int i17 = keyAction;
                                                        if (keyAction != 0) {
                                                            ScreenBuilder.openScreen(i17);
                                                        } else if (i14 == 8) {
                                                            onItemSelected();
                                                        } else if (i14 == 1) {
                                                            screen3.scrollUp();
                                                        } else if (i14 == 6) {
                                                            screen3.scrollDown();
                                                        } else if (i14 == 2) {
                                                            if (screen3.showCheckboxes) {
                                                                ScreenBuilder.onScreenClosed();
                                                            } else if (screen3.screenId == ScreenId.MAP) {
                                                                AppState.setInt(StateKeys.INT_MAP_SCROLL_DIRECTION, 1);
                                                            } else if (screen3.layoutMode == 1) {
                                                                int i18 = screen3.selectedIndex;
                                                                int size10 = screen3.menuItems.size();
                                                                screen3.selectedIndex = ((i18 + size10) - 1) % size10;
                                                                screen3.invalidateLayout();
                                                            }
                                                        } else if (i14 == 5) {
                                                            screen3.onActionKey();
                                                        }
                                                    }
                                                }
                                    } else if (event instanceof CommandEvent) {
                                        int cmdType = ((CommandEvent) event).command;
                                        if (cmdType == CommandEvent.OK) {
                                                ScreenBuilder.onMenuItemSelected();
                                        } else if (cmdType == CommandEvent.CANCEL) {
                                                ScreenBuilder.onMenuItemAction();
                                        } else if (cmdType == CommandEvent.SELECT) {
                                                needsRepaint = true;
                                                onItemSelected();
                                        } else if (cmdType == CommandEvent.BACK) {
                                                if (ScreenManager.getCurrentScreen().screenId == ScreenId.MAP) {
                                                    needsRepaint = true;
                                                    AppState.setInt(StateKeys.INT_MAP_SCROLL_DIRECTION, -1);
                                                }
                                        }
                                    } else if (event instanceof PointerEvent) {
                                        PointerEvent ptrEvt = (PointerEvent) event;
                                        int ptrAction = ptrEvt.action;
                                        if (ptrAction == PointerEvent.PRESS) {
                                                int i19 = ptrEvt.x;
                                                int i20 = ptrEvt.y;
                                                if (!AppState.getBool(StateKeys.SETTING_STATUS_BAR_VISIBLE) || i20 <= AppState.getHeight()) {
                                                    z2 = false;
                                                } else {
                                                    if (i19 < (AppState.getInt(StateKeys.INT_SCREEN_WIDTH) >> 1)) {
                                                        ScreenBuilder.onMenuItemSelected();
                                                    } else {
                                                        ScreenBuilder.onMenuItemAction();
                                                    }
                                                    z2 = true;
                                                }
                                                if (z2 || (i = ScreenManager.getCurrentScreen().screenId) == 137) {
                                                    break;
                                                } else if (i20 > 17 || !ScreenManager.hasModal()) {
                                                    i2 = 0;
                                                    int i21 = i2;
                                                    if (i2 <= 0) {
                                                        if (i != i21) {
                                                            if (i == 4) {
                                                                ContactListManager.clearState();
                                                            }
                                                            ScreenBuilder.openScreen(i21);
                                                        }
                                                        break;
                                                    } else {
                                                        Screen screen4 = ScreenManager.getCurrentScreen();
                                                        if (screen4 != null) {
                                                            screen4.touchConsumed = true;
                                                            screen4.marginLeft = 0;
                                                            screen4.marginTop = 0;
                                                            int i22 = i19 - screen4.offsetX;
                                                            int i23 = i20 - screen4.offsetY;
                                                            boolean z7 = i22 >= 2 && i22 < 2 + screen4.contentWidth && i23 >= screen4.contentTop && i23 < screen4.contentTop + screen4.contentHeight;
                                                            boolean z8 = z7;
                                                            if (z7 && screen4.screenId == ScreenId.MAP) {
                                                                int i24 = i23 - screen4.contentTop;
                                                                if (i24 > 0) {
                                                                    MapController.toggleMapControls(screen4);
                                                                    MapRenderer.dragActive = false;
                                                                    MapRenderer.rippleTimestamp = System.currentTimeMillis();
                                                                    MapRenderer.rippleX = i22;
                                                                    MapRenderer.rippleY = i24;
                                                                    MapRenderer.needsRedraw = true;
                                                                    z3 = true;
                                                                } else {
                                                                    z3 = false;
                                                                }
                                                            } else if (z8 || screen4.screenType == 1 || screen4.screenType == 12) {
                                                                z3 = false;
                                                            } else {
                                                                ScreenBuilder.onScreenClosed();
                                                                needsRepaint = true;
                                                                z3 = true;
                                                            }
                                                            if (!z3) {
                                                                int i25 = screen4.screenType;
                                                                if ((i25 == 1 || i25 == 12) && (stateObj = TabBar.hitTest(i19, i20)) != null) {
                                                                    if (!(stateObj instanceof int[])) {
                                                                        int i26 = ((TabBar) stateObj).type;
                                                                        Account acct = ((TabBar) stateObj).account;
                                                                        AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 0);
                                                                        if (i == 4) {
                                                                            ContactListManager.clearState();
                                                                        }
                                                                        if (i26 != 6 && i26 != 36 && acct != null) {
                                                                            TabBar.findTab(4, ((TabBar) stateObj).account);
                                                                            ScreenBuilder.openScreen(ScreenId.CONTACT_LIST);
                                                                        } else if (i != i26) {
                                                                            ScreenBuilder.openScreen(i26);
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        switch (((int[]) stateObj)[1]) {
                                                                            case 246:
                                                                                ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
                                                                                break;
                                                                            case 248:
                                                                                ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
                                                                                break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    int stateInt6 = AppState.getInt(StateKeys.INT_SCREEN_WIDTH) - 17;
                                                    if (AccountManager.handleTabAction() == 0) {
                                                        if (!AccountManager.hasActiveConnection() && i19 > stateInt6) {
                                                            i2 = ScreenId.MAIL_ACCOUNT_LIST;
                                                        }
                                                        int i212 = i2;
                                                        if (i2 <= 0) {
                                                        }
                                                    } else if (i19 > stateInt6) {
                                                        i2 = !AppState.getBool(StateKeys.SETTING_MULTI_ACCOUNT) ? ScreenId.CONTACT_LIST : 0;
                                                        int i2122 = i2;
                                                        if (i2 <= 0) {
                                                        }
                                                    } else {
                                                        stateInt6 -= 17;
                                                        if (!AccountManager.hasActiveConnection()) {
                                                            i2 = 0;
                                                            int i21222 = i2;
                                                            if (i2 <= 0) {
                                                            }
                                                        }
                                                    }
                                                }
                                        } else if (ptrAction == PointerEvent.DRAG) {
                                                int i27 = ptrEvt.x;
                                                int i28 = ptrEvt.y;
                                                Screen screen5 = ScreenManager.getCurrentScreen();
                                                if (screen5 != null && screen5.touchConsumed) {
                                                    int i29 = i27 - screen5.offsetX;
                                                    int i30 = i28 - (screen5.offsetY + screen5.contentTop);
                                                    if (screen5.marginLeft == 0 && screen5.marginTop == 0) {
                                                        screen5.marginLeft = i29;
                                                        screen5.marginTop = i30;
                                                    }
                                                    int i31 = i29 - screen5.marginLeft;
                                                    int i32 = i30 - screen5.marginTop;
                                                    screen5.marginLeft = i29;
                                                    screen5.marginTop = i30;
                                                    if (screen5.screenId == ScreenId.MAP) {
                                                        MapController.toggleMapControls(screen5);
                                                        MapRenderer.dragActive = true;
                                                        MapRenderer.rippleTimestamp = 0L;
                                                        int stateInt7 = AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
                                                        MapRenderer.setPosition(MapRenderer.currentLon - ((int) MapUtils.pixelToCoord(i31, stateInt7)), MapRenderer.currentLat + ((int) MapUtils.pixelToCoord(i32, stateInt7)));
                                                        MapRenderer.needsRedraw = true;
                                                    } else {
                                                        screen5.scrollOffset -= i32;
                                                        if (screen5.totalHeight < screen5.contentHeight) {
                                                            screen5.scrollOffset = 0;
                                                        }
                                                        if (screen5.scrollOffset > screen5.totalHeight - screen5.contentHeight) {
                                                            screen5.scrollOffset = screen5.totalHeight - screen5.contentHeight;
                                                        }
                                                        if (screen5.scrollOffset < 0) {
                                                            screen5.scrollOffset = 0;
                                                        }
                                                        needsRepaint = true;
                                                    }
                                                }
                                        } else if (ptrAction == PointerEvent.RELEASE) {
                                                int i33 = ptrEvt.x;
                                                int i34 = ptrEvt.y;
                                                int i35 = ptrEvt.startX;
                                                int i36 = ptrEvt.startY;
                                                boolean i37 = ptrEvt.wasDragged;
                                                Screen screen6 = ScreenManager.getCurrentScreen();
                                                if (screen6 != null) {
                                                    screen6.onPointerEvent(i33, i34, i35, i36, i37);
                                                }
                                        } else if (ptrAction == PointerEvent.LONG_PRESS) {
                                                int i38 = ptrEvt.x;
                                                int i39 = ptrEvt.y;
                                                Screen screen7 = ScreenManager.getCurrentScreen();
                                                if (screen7 != null) {
                                                    int i40 = i38 - screen7.offsetX;
                                                    int i41 = i39 - screen7.offsetY;
                                                    screen7.touchConsumed = false;
                                                    if (screen7.screenId == ScreenId.MAP) {
                                                        int i42 = i41 - screen7.contentTop;
                                                        MapController.toggleMapControls(screen7);
                                                        MapRenderer.onDrag(i40, i42);
                                                    }
                                                }
                                        }
                                    } else if (event instanceof NotificationEvent) {
                                        NotificationHelper.showNotification(((NotificationEvent) event).message);
                                        needsRepaint = true;
                                    } else if (event instanceof AccountDataEvent) {
                                        Object[] evtData = ((AccountDataEvent) event).data;
                                        if (evtData[0] instanceof MrimAccount) {
                                            AppState.setInt(StateKeys.INT_HTTP_RESULT_SCREEN, 108);
                                            AppState.setObject(StateKeys.SLOT_MAP_POINT_1, evtData[1]);
                                            MrimAccount mrimAccount6 = (MrimAccount) evtData[0];
                                            mrimAccount6.chatRoomsLoaded = true;
                                            AppState.pool[StateKeys.SLOT_TEMP_ACCOUNT] = mrimAccount6;
                                            ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ERROR_ALERT));
                                            AppState.clearIndex(StateKeys.SLOT_MAP_POINT_1);
                                            needsRepaint = true;
                                        } else {
                                            ((MrimAccount) evtData[1]).addOfflineContact((String) evtData[0]);
                                        }
                                    } else if (event instanceof ProtocolEvent) {
                                        ProtocolEvent protoEvt = (ProtocolEvent) event;
                                        int i43 = protoEvt.type;
                                        Object obj6 = protoEvt.data;
                                        switch (i43) {
                                            case ProtocolEvent.MAP_LOCATIONS_LOADED:
                                                ResourceManager.showSavedLocations();
                                                break;
                                            case ProtocolEvent.PHONE_SEARCH_RESULT:
                                                Object[] objArr3 = (Object[]) obj6;
                                                PhoneContact phoneContact = (PhoneContact) objArr3[0];
                                                AppState.pool[StateKeys.RANGE_PHONE_CONTACT_START] = phoneContact;
                                                Vector vector4 = (Vector) objArr3[1];
                                                AppState.pool[StateKeys.VEC_PHONE_RESULTS] = vector4;
                                                int iIntValue = ((Integer) objArr3[2]).intValue();
                                                AppState.setInt(StateKeys.INT_PHONE_SCROLL_OFFSET, iIntValue);
                                                Screen popupScreen = ScreenManager.createScreen(ScreenDef.CONTACT_POPUP);
                                                if (iIntValue >= 10) {
                                                    popupScreen.addIconItemWithData(6, AppState.getString(StateKeys.STR_MENU_SMS), 1, null);
                                                }
                                                int size11 = vector4.size();
                                                while (true) {
                                                    size11--;
                                                    if (size11 < 0) {
                                                        if (iIntValue < phoneContact.userCount - 10) {
                                                            popupScreen.addIconItemWithData(6, AppState.getString(StateKeys.STR_MENU_CALL), 2, null);
                                                        }
                                                        AppState.setBool(StateKeys.FLAG_PHONE_HAS_NEXT, iIntValue < phoneContact.userCount - 10);
                                                        AppState.setBool(StateKeys.FLAG_PHONE_HAS_PREV, iIntValue >= 10);
                                                        ScreenManager.showScreen(popupScreen);
                                                        break;
                                                    } else {
                                                        UserSearchResult searchResult = (UserSearchResult) vector4.elementAt(size11);
                                                        popupScreen.addIconItemWithData(searchResult.gender == 1 ? 377 : searchResult.gender == 2 ? 378 : 379, searchResult.getText(), 0, searchResult);
                                                    }
                                                }
                                            case ProtocolEvent.ADD_CONTACT_CONFIRM:
                                                AppState.setInt(StateKeys.FLAG_SHOW_PHOTO, 1);
                                                IOUtils.showAddContactScreen();
                                                break;
                                            case ProtocolEvent.ACCOUNT_SYNC:
                                                ((MrimAccount) obj6).syncProfile();
                                                break;
                                            case ProtocolEvent.MAP_CONTROL:
                                                break;
                                        }
                                        needsRepaint = true;
                                    } else if (event instanceof MenuItemEvent) {
                                        needsRepaint = true;
                                        needsLayoutUpdate = true;
                                        Screen screen8 = ScreenManager.getCurrentScreen();
                                        int i44 = ScreenManager.getCurrentScreen().screenId;
                                        MenuItem eventItem = ((MenuItemEvent) event).item;
                                        if (eventItem.id == 2) {
                                            if (i44 == 147 && AppState.setBool(StateKeys.FLAG_CAPTCHA_SHOWN, ((Boolean) eventItem.data).booleanValue())) {
                                                ScreenManager.processScreenForm();
                                                AppState.setFromPool(StateKeys.SLOT_SCREEN_DESCRIPTION, StateKeys.SLOT_SCREEN_VALUE);
                                                finishScreenBuild();
                                            }
                                        } else if (i44 == 21) {
                                            if (AppState.getAccount().getType() == Account.TYPE_MRIM) {
                                                StringUtils.updateRegDropdowns(screen8, eventItem);
                                            }
                                        } else if (i44 == 164) {
                                            Object[] objArr4 = (Object[]) eventItem.data;
                                            int iIntValue2 = ((Integer) objArr4[0]).intValue();
                                            String[] strArr = (String[]) objArr4[1];
                                            MenuItem menuItem3 = null;
                                            Vector vector5 = screen8.menuItems;
                                            int size12 = vector5.size();
                                            while (true) {
                                                size12--;
                                                if (size12 < 0) {
                                                    if (eventItem.title.equals(AppState.getString(StateKeys.STR_MENU_OPTIONS))) {
                                                        MenuItem menuItem4 = menuItem3;
                                                        String optionStr = iIntValue2 == 0 ? Utils.defaultStr(AppState.getString(StateKeys.SLOT_DEVICE_ID)) : strArr[iIntValue2];
                                                        Object[] objArr5 = (Object[]) menuItem4.data;
                                                        menuItem4.clear().setAction(objArr5[4], optionStr, objArr5[1], objArr5[2], objArr5[3]);
                                                    }
                                                    screen8.rebuildItems();
                                                    break;
                                                } else {
                                                    MenuItem item = (MenuItem) vector5.elementAt(size12);
                                                    if (item.id == 15 && item.title.startsWith(AppState.getString(StateKeys.STR_MENU_PHONE_PREFIX))) {
                                                        menuItem3 = item;
                                                    }
                                                }
                                            }
                                        } else if (i44 == 26) {
                                            Object[] objArr6 = (Object[]) eventItem.data;
                                            if (AppState.getString(StateKeys.STR_MENU_SETTINGS).equals(eventItem.title)) {
                                                AppState.setInt(StateKeys.SETTING_COLOR_THEME, ((Integer) objArr6[0]).intValue());
                                            }
                                        } else if (i44 == 28) {
                                            ResourceManager.playAlertIfEnabled(((Integer) ((Object[]) eventItem.data)[0]).intValue(), false);
                                        }
                                    }
                                    if (!AppState.getBool(StateKeys.SETTING_STATUS_BAR_VISIBLE) && null != (screen = ScreenManager.getCurrentScreen())) {
                                        AppState.getCanvas().setCommands(screen.titleLeft, screen.titleRight);
                                    }
                                    IOUtils.checkSoundTimer();
                                    if (isTimerExpired(timers[0]) && (!AppState.getBool(StateKeys.FLAG_KEEP_SCREEN_ON) || ScreenManager.getCurrentScreen().screenId != ScreenId.MAP)) {
                                        if (AppState.getCanvas().isShown()) {
                                            updateTimerSlot(0);
                                        } else {
                                            setTimer(0, getSessionTimestamp());
                                        }
                                    }
                                    break;
                                } else {
                                    Contact contact = (Contact) vec3.elementAt(size2);
                                    if (Utils.abs(stateInt - contact.statusCode) > 10000) {
                                        deleteContact(contact);
                                    }
                                }
                            }
                            break;
                        } else {
                            Account acct2 = (Account) vec2.elementAt(size);
                            try {
                                if (acct2.progress <= Account.PROGRESS_DISCONNECTED || acct2.progress == Account.PROGRESS_CONNECTED) {
                                    Vector vec6 = AppState.getVector(StateKeys.VEC_POPUP_ITEMS);
                                    if (vec6.contains(acct2)) {
                                        Utils.removeFrom(vec6, acct2);
                                        processKeyRepeat();
                                    }
                                } else {
                                    Vector vec7 = AppState.getVector(StateKeys.VEC_POPUP_ITEMS);
                                    if (!vec7.contains(acct2)) {
                                        vec7.addElement(acct2);
                                        processKeyRepeat();
                                    }
                                }
                                acct2.loadData();
                            } catch (Throwable unused2) {
                                acct2.handleConnError();
                            }
                        }
                    }
                }
            }
            String savedStr = AppState.getString(StateKeys.SLOT_SAVED_STRING);
            if (savedStr != null) {
                try {
                    isBackgrounded = true;
                    AppState.getMidlet().platformRequest(savedStr);
                    throw new Throwable();
                } catch (Throwable unused3) {
                    AppState.clearIndex(StateKeys.SLOT_SAVED_STRING);
                }
            }
            if (isBackgrounded) {
                AppState.getMidlet().destroyApp(true);
                isShuttingDown = true;
                throw new RuntimeException();
            }
            if ((AccountManager.handleTabAction() != 0 || AccountManager.hasActiveConnection()) && isTimerType(5)) {
                needsRepaint = true;
            }
            MainCanvas canvas = AppState.getCanvas();
            if (!isShuttingDown && needsRepaint) {
                Object obj7 = AppState.currentScreen;
                if (null != obj7) {
                    if (obj7 == AppState.getCanvas()) {
                        AppState.getCanvas().updateFullScreenMode();
                    }
                    Display.getDisplay(AppState.getMidlet()).setCurrent(obj7 instanceof Displayable ? (Displayable) obj7 : null);
                    setTimer(0, getSessionTimestamp());
                    AppState.currentScreen = null;
                    z = true;
                } else {
                    z = false;
                }
                if (!z) {
                    if (canvas.isShown()) {
                        canvas.repaint();
                        setTimer(5, 1000L);
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch (Throwable unused4) {
                        }
                    }
                }
            }
            try {
                Thread.sleep(isTimerType(3) ? 500 : 25);
            } catch (Throwable unused5) {
            }
        }
    }

    /* renamed from: ab */
    public static final void onItemSelected() {
        int i;
        int nextState;
        int i2;
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        if (menuItem == null || menuItem.execute(ScreenManager.getCurrentScreen()) == -1) {
            Screen screen = ScreenManager.getCurrentScreen();
            String title = ScreenManager.getCurrentTitle();
            int selectedOption = ScreenManager.getCurrentWidth();
            MenuItem menuItem2 = ScreenManager.getCurrentMenuItem();
            MenuItem headerItem = AppState.getVector(StateKeys.VEC_SCREEN_STACK).size() > 0 ? ScreenManager.getCurrentScreen().getHeaderItem() : null;
            Object obj = menuItem2 == null ? null : menuItem2.data;
            Object obj2 = headerItem == null ? null : headerItem.data;
            int actionResult = 0;
            ScreenHandler handler = ScreenHandlerRegistry.getHandler(ScreenManager.getCurrentScreen().screenId);
            if (handler != null) {
                actionResult = handler.onItemSelected(screen, menuItem2, title, selectedOption, obj, obj2);
            } else switch (ScreenManager.getCurrentScreen().screenId) {
                case ScreenId.UNUSED_32:
                    actionResult = ResourceManager.handleDropdownSelect(title, menuItem2);
                    break;
                case ScreenId.UNUSED_45:
                    actionResult = -1;
                    break;
                case ScreenId.UNUSED_74:
                    actionResult = -1;
                    break;
                case ScreenId.UNUSED_75:
                    actionResult = -1;
                    break;
                case ScreenId.UNUSED_138:
                    actionResult = -1;
                    break;
                case ScreenId.UNUSED_139:
                    actionResult = ScreenId.MMP_ACCOUNT_SELECT;
                    break;
                case ScreenId.UNUSED_141:
                    actionResult = -1;
                    break;
            }
            if (actionResult != -1) {
                if (actionResult == 12) {
                    ScreenBuilder.onScreenClosed();
                    return;
                }
                if (actionResult != 0) {
                    ScreenBuilder.openScreen(actionResult);
                    return;
                }
                int i3 = screen.softKeyRight;
                if (i3 != 200) {
                    int i4 = i3 == 199 ? selectedOption : i3;
                    int i5 = i4;
                    if (i4 == 12) {
                        ScreenBuilder.onScreenClosed();
                    } else if (i5 != 0) {
                        ScreenBuilder.openScreen(i5);
                    }
                }
            }
        }
    }

    /* renamed from: O */
    private static final int getKeyAction(int i) {
        if (ScreenManager.getCurrentScreen().screenId == ScreenId.MAIN_SCREEN) {
            return 0;
        }
        Screen screen = ScreenManager.getCurrentScreen();
        int i2 = ScreenManager.getCurrentScreen().screenId;
        switch (i) {
            case 4:
                break;
            case 8:
                if (screen.selectable) {
                    screen.selectedIndex = screen.menuItems.size() - 1;
                    screen.scrollOffset = screen.totalHeight - screen.contentHeight;
                    if (screen.scrollOffset < 0) {
                        screen.scrollOffset = 0;
                    }
                } else if (screen.totalHeight < screen.contentHeight) {
                    screen.scrollOffset = 0;
                } else if (((MenuItem) screen.menuItems.lastElement()).getTotalHeight() < screen.contentHeight) {
                    screen.scrollOffset = screen.totalHeight - screen.contentHeight;
                } else {
                    int[] iArr = screen.layoutCache;
                    screen.scrollOffset = iArr[iArr[0]];
                }
                screen.invalidateLayout();
                break;
            case 11:
                AppState.toggleBool(StateKeys.SETTING_SORT_ORDER);
                needsLayoutUpdate = true;
                break;
            case 12:
                if (i2 != 73) {
                    if (i2 != 4) {
                        if (i2 == 30 || i2 == 92 || i2 == 40) {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    AppState.pool[StateKeys.SLOT_CONTACT_INFO] = screen.getSelectedItem().data;
                    break;
                }
                break;
        }
        return 0;
    }

    /* renamed from: ac */
    public static final int handleGameAction() {
        Object obj = AppState.pool[StateKeys.SLOT_CURRENT_ENTITY];
        if (obj == null || !(obj instanceof Contact)) {
            return 0;
        }
        Contact contact = (Contact) obj;
        if (!contact.account.isConnected()) {
            return NotificationHelper.showError(299);
        }
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        return (contact.isSystem() || contact.isOffline()) ? 0 : ScreenId.CONTACT_DELETE;
    }

    /* renamed from: ak */
    private static final void processKeyRepeat() {
        AppState.setInt(StateKeys.INT_POPUP_HEIGHT, AppState.getVector(StateKeys.VEC_POPUP_ITEMS).size() * Utils.max(16, AppState.getInt(StateKeys.INT_FONT_HEIGHT)));
        needsRepaint = true;
    }

    /* renamed from: a */
    public static final String[] createAddressPair(String str, String str2) {
        return new String[]{str, str2};
    }

    /* renamed from: J */
    public static final int handleEditAction(int i) {
        applyViewMode(i == 0, i != 0, true);
        AppState.setInt(StateKeys.FLAG_REFRESH_CONTACTS, 1);
        return 0;
    }

    /* renamed from: a */
    public static final void handleMrimMailNotify(MrimAccount mrimAccount, ByteBuffer buffer) {
        buffer.readInt();
        switch (buffer.readInt() & 255) {
            case 65:
                processMrimMailData(mrimAccount, 490);
                break;
            case 66:
                processMrimMailData(mrimAccount, 491);
                break;
            case 67:
            case 69:
            case 70:
            case 71:
            case 72:
            default:
                mrimAccount.handleError(0);
                DiagnosticReporter.checkCrashReport();
                break;
            case 68:
                processMrimMailData(mrimAccount, 492);
                break;
            case 73:
                mrimAccount.handleComplete();
                break;
        }
    }

    /* renamed from: a */
    private static final void processMrimMailData(MrimAccount mrimAccount, int i) {
        IOUtils.postAccountError(mrimAccount, i);
        mrimAccount.closeConnection();
        mrimAccount.lastError = mrimAccount.getDefaultError();
    }

    /* renamed from: n */
    public static final int handleIncomingCall(Object obj) {
        if (AppState.getBool(StateKeys.FLAG_NEW_MESSAGE)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return ScreenId.MAP;
        }
        if (!AppState.getBool(StateKeys.FLAG_CONTACTS_LOADED)) {
            pendingMapPoint = (MapPoint) obj;
            return ScreenId.CHAT_LIST_OPTIONS;
        }
        MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
        MapPoint mapPoint = (MapPoint) obj;
        mrimAccount.setSimpleProfile(IOUtils.pixelToLongitude(mapPoint.longitude), IOUtils.pixelToLatitude(mapPoint.latitude));
        mrimAccount.syncProfile();
        AppState.setInt(StateKeys.FLAG_CONTACTS_LOADED, 0);
        return ScreenId.PROFILE_EDIT;
    }

    /* renamed from: f */
    public static final int getIconOffset() {
        return AppState.getBool(StateKeys.SETTING_FAST_CONNECTION) ? 10 : 55;
    }

}
