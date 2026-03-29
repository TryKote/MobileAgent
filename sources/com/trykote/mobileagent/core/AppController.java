package com.trykote.mobileagent.core;


import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.map.MapUtils;
import com.trykote.mobileagent.map.TileCache;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.net.Telemetry;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.mmp.MmpContact;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.MrimContact;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.ui.handler.ScreenHandler;
import com.trykote.mobileagent.ui.handler.ScreenHandlerRegistry;
import com.trykote.mobileagent.util.*;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import java.util.Calendar;
import java.util.Vector;

public final class AppController {

    // Soft key action codes
    private static final int SOFTKEY_NO_ACTION = 200;
    private static final int SOFTKEY_USE_SELECTED = 199;

    // Timing constants
    private static final long LONG_PRESS_MS = 600L;
    private static final long SLEEP_ANIMATION_MS = 500L;
    private static final long SLEEP_ACTIVE_MS = 25L;
    private static final long SLEEP_BACKGROUND_MS = 200L;
    private static final long REPAINT_INTERVAL_MS = 1000L;
    private static final long CONTACT_REFRESH_MS = 1000L;
    private static final int PENDING_CONN_TIMEOUT_MS = 10000;
    private static final int COMPLETION_TIMEOUT_MS = 15000;
    private static final int COMPLETION_POLL_MS = 500;

    // Header layout
    private static final int HEADER_HEIGHT = 17;

    // Phone search
    private static final int PHONE_PAGE_SIZE = 10;
    private static final int ICON_GENDER_MALE = 377;
    private static final int ICON_GENDER_FEMALE = 378;
    private static final int ICON_GENDER_UNKNOWN = 379;

    // Key binding offset
    private static final int KEY_BINDING_OFFSET = 159;

    // Key action IDs (from key mapping settings)
    private static final int KEY_ACTION_BACK = 4;
    private static final int KEY_ACTION_SCROLL_END = 8;
    private static final int KEY_ACTION_TOGGLE_SORT = 11;
    private static final int KEY_ACTION_SELECT_ITEM = 12;

    // Minimum popup icon height
    private static final int MIN_POPUP_ICON_HEIGHT = 16;

    // Chat list modes
    private static final int CHAT_MODE_NORMAL = 5;
    private static final int CHAT_MODE_EXTENDED = 4;

    // Map scroll directions
    private static final int SCROLL_LEFT = 1;
    private static final int SCROLL_RIGHT = -1;

    // ListView layout mode
    private static final int LAYOUT_GRID = 1;

    // Server icon command parsing
    private static final int SERVER_CMD_PREFIX_LEN = 7;
    private static final int SERVER_OPTION_MIN = 4;
    private static final int SERVER_OPTION_MAX = 53;
    private static final int SERVER_OPTION_SKIP_1 = 25;
    private static final int SERVER_OPTION_SKIP_2 = 31;
    private static final int SERVER_OPTION_TO_SCREEN = 157;

    // Phone search popup
    private static final int ICON_NAVIGATION = 6;
    private static final int GENDER_MALE = 1;
    private static final int GENDER_FEMALE = 2;
    private static final int PAGE_ACTION_PREV = 1;
    private static final int PAGE_ACTION_NEXT = 2;

    private static int lastMinute;
    public static int clockWidth;

    public static String pendingUrl;
    public static MrimAccount pendingAccount;
    public static Object appLock;
    public static boolean isShuttingDown;
    public static boolean needsLayoutUpdate;
    public static boolean needsRepaint;
    public static boolean isBackgrounded;
    public static boolean saveOnExit;

    public static void toggleOnlineMode(boolean extended) {
        if (!extended) {
            AppState.setInt(ChatKeys.INT_CHAT_LIST_MODE, CHAT_MODE_NORMAL);
        } else {
            AppState.setInt(ChatKeys.INT_CHAT_LIST_MODE, CHAT_MODE_EXTENDED);
            AppState.setBool(ChatKeys.FLAG_EXTENDED_CHAT_VIEW, true);
        }
    }

    public static String getFreeMemoryString() {
        return StringUtils.intern(Long.toString(Runtime.getRuntime().freeMemory()));
    }

    public static void clearPreviewState() {
        AppState.clearRange(UIKeys.SLOT_SCREEN_TITLE, UIKeys.SLOT_APP_VERSION_STRING);
    }

    public static int dialPhoneContactNext() {
        ContactListManager.dialPhoneContact((PhoneContact) AppState.pool[UIKeys.RANGE_PHONE_CONTACT_START], AppState.getInt(RuntimeKeys.INT_PHONE_SCROLL_OFFSET) + PHONE_PAGE_SIZE);
        return ScreenId.MAP;
    }

    public static int dialPhoneContactPrev() {
        ContactListManager.dialPhoneContact((PhoneContact) AppState.pool[UIKeys.RANGE_PHONE_CONTACT_START], AppState.getInt(RuntimeKeys.INT_PHONE_SCROLL_OFFSET) - PHONE_PAGE_SIZE);
        return ScreenId.MAP;
    }

    public static String getPendingDisplayText() {
        Object obj = AppState.pool[UIKeys.OBJ_PHOTO_CACHE_1];
        if (obj == null) {
            return null;
        }
        return obj instanceof String ? (String) obj : ((MrimContact) obj).simpleIdentifier;
    }

    public static void clearInitParamsAndReport() {
        AppState.clearRange(UIKeys.SLOT_INIT_PARAMS, UIKeys.SLOT_LANGUAGE_OPTION);
        AppState.setInt(UIKeys.INT_OK_MENU_ACTION, 0);
        AppState.setInt(UIKeys.INT_OK_MENU_TYPE, 0);
        AppState.setInt(UIKeys.INT_CANCEL_MENU_ACTION, 0);
        AppState.setInt(UIKeys.INT_CANCEL_MENU_TYPE, 0);
        TransitionData.get().clear();
        Telemetry.sendReport(true, (MrimAccount) AppState.getAccount());
    }

    public static int resolveServerIcon(int index, String command) {
        switch (index) {
            case 0:
                return ScreenId.VISIBLE_CONTACTS;
            case 1:
                return ScreenId.PHOTO_SELECTOR;
            case 2:
                return ScreenId.ACCOUNT_SETUP;
            case 3:
                return ScreenId.SHARE_LOCATION;
            default:
                if (AppState.getString(StringResKeys.STR_CMD_SHOW_LIST).equals(command)) {
                    return ScreenId.PROFILE_EDIT;
                }
                if (AppState.getString(StringResKeys.STR_CMD_SHOW_MAP).equals(command)) {
                    return ScreenId.WIFI_ACCOUNT_LIST;
                }
                int optionId = Integer.parseInt(StringUtils.suffix(command, SERVER_CMD_PREFIX_LEN));
                boolean isScreenOption = optionId >= SERVER_OPTION_MIN && optionId <= SERVER_OPTION_MAX
                        && optionId != SERVER_OPTION_SKIP_1 && optionId != SERVER_OPTION_SKIP_2;
                if (isScreenOption) {
                    return optionId + SERVER_OPTION_TO_SCREEN;
                }
                return (index & Integer.MIN_VALUE) != 0 ? ScreenId.MAP_SEARCH : ScreenId.PHOTO_SELECTOR;
        }
    }

    public static void waitForCompletion(Object[] data) throws InterruptedException {
        int remaining = COMPLETION_TIMEOUT_MS;
        do {
            remaining -= COMPLETION_POLL_MS;
            if (remaining < 0) {
                EventDispatcher.postEvent(new AccountDataEvent(data));
                return;
            }
            Thread.sleep(COMPLETION_POLL_MS);
        } while (!isShuttingDown);
    }

    public static int handleChatOption(int optionId) {
        if (optionId != ScreenId.COMPOSE_MESSAGE) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
            ContactListManager.openContactMessages();
            return 0;
        }
        MrimContact contact = AppState.getCurrentMrimContact();
        AppState.setAccount(contact.account);
        MailHelper.composeEmail(MailHelper.parseRecipientList(contact.simpleIdentifier), (String) null, (String) null);
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static void showSettingsScreen() {
        AppState.setInt(SessionKeys.FLAG_INIT_COMPLETE, 0);
        AppState.setInt(UIKeys.FLAG_FULLSCREEN_ACTIVE, 1);
        ScreenManager.pushScreen(ScreenManager.createScreen(ScreenDef.SETTINGS_MAIN));
    }

    public static void openUserProfile(MrimAccount account, String url) {
        pendingAccount = account;
        pendingUrl = url;
    }

    public static void clearPendingProfile() {
        pendingAccount = null;
        pendingUrl = null;
    }

    public static void resetClock() {
        TimerManager.timers[4] = 0;
        lastMinute = -1;
        clockWidth = 0;
        AppState.clearIndex(UIKeys.SLOT_CLOCK_STRING);
        updateClock();
    }

    public static void updateClock() {
        Calendar calendar;
        int i;
        if (!TimerManager.checkTimer(4, 1000L) || (i = (calendar = AppState.getCalendar()).get(12)) == lastMinute) {
            return;
        }
        String timeStr = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Utils.zeroPad(calendar.get(11))).append(':').append(Utils.zeroPad(i)));
        AppState.setObject(UIKeys.SLOT_CLOCK_STRING, (Object) timeStr);
        clockWidth = AppState.getGfxContext(UIKeys.GFX_INDEX_DEFAULT).stringWidth(timeStr);
        lastMinute = i;
        needsRepaint = true;
    }

    public static void clearImageCache() {
        AppState.clearRange(UIKeys.SLOT_MEDIA_CALLBACK, MapKeys.OBJ_FONT_2);
    }

    public static void dispatchCommand(Object midlet, int width, int height) {
        appLock = new Object();
        synchronized (appLock) {
            RemoteLogger.init();
            AppState.init(midlet);
            initializeVectors(width, height);
            initializeTimers();
            showInitialScreen();
            new AsyncTask(AsyncTaskId.PERIODIC_TIME_SYNC);
            new AsyncTask(AsyncTaskId.PROCESS_SOFTKEY);
        }
    }

    private static void initializeVectors(int width, int height) {
        AppState.clearRange(UIKeys.RANGE_SESSION_TEMP_START, UIKeys.RANGE_SESSION_TEMP_END);
        AppState.pool[MapKeys.SLOT_MAP_TILE_REQUEST] = ObjectPool.newVector();
        AppState.pool[UIKeys.VEC_SCREEN_STACK] = ObjectPool.newVector();
        ScreenManager.initializeFonts();
        AppState.pool[UIKeys.VEC_ONLINE_CONTACTS] = ObjectPool.newVector();
        AppState.pool[UIKeys.VEC_ACTIVE_CONNECTIONS] = ObjectPool.newVector();
        AppState.pool[UIKeys.SLOT_MEDIA_CONTROL] = ObjectPool.newVector();
        AppState.pool[UIKeys.SLOT_MEDIA_VOLUME] = ObjectPool.newVector();
        new AsyncTask(AsyncTaskId.CONNECTION_LOOP);
        AccountManager.loadSavedAccounts();
        AppState.pool[UIKeys.VEC_POPUP_ITEMS] = ObjectPool.newVector();
        updatePopupHeight();
        AppState.pool[UIKeys.VEC_PENDING_CONNECTIONS] = ObjectPool.newVector();
        resetClock();
        SoftFloat.initMathTables();
        AppState.setInt(TrafficKeys.TRAFFIC_MRIM_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_MRIM_RECV_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_MMP_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_MMP_RECV_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_XMPP_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_XMPP_RECV_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_HTTP_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_HTTP_RECV_BYTES, 0);
        AppState.pool[MapKeys.VEC_TILE_QUEUE] = ObjectPool.newVector();
        TileCache.calculateCacheSize();
        AppState.pool[SessionKeys.OBJ_CANVAS] = new MainCanvas(width, height);
        AppState.clearRange(UIKeys.RANGE_TEMP_DATA_START, UIKeys.RANGE_TEMP_DATA_END);
        TabBar.initialize();
        AppState.pool[StringResKeys.RES_EMOTICON_MAP] = Utils.bytesToInts(AppState.getBytes(StringResKeys.RES_EMOTICON_MAP));
        AppState.pool[UIKeys.SLOT_MEDIA_RESOURCE] = new byte[1];
    }

    private static void initializeTimers() {
        try {
            ScreenManager.getScreenMode1();
            ScreenManager.getScreenMode2();
            ScreenManager.getScreenMode3();
            ScreenManager.getScreenMode4();
        } catch (Throwable unused) {
            AppState.clearRange(StringResKeys.RES_UPDATE_DATA, UIKeys.RANGE_UPDATE_DATA_END);
        }
        TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
        AppState.addInt(SessionKeys.COUNTER_APP_STARTS, 1);
        AppState.saveDelta(true);
    }

    private static void showInitialScreen() {
        if (AppState.getBool(SessionKeys.FLAG_INIT_COMPLETE)) {
            showSettingsScreen();
            return;
        }
        int accountCount = AccountManager.getActiveAccountCount();
        if (accountCount == 0) {
            ScreenManager.pushScreen(ScreenManager.createScreen(ScreenDef.ACCOUNT_SETUP));
        } else {
            for (int i = accountCount - 1; i >= 0; i--) {
                AccountManager.addToAccountSelection(AccountManager.getAccountByIndex(i));
            }
            ContactListManager.showContactList();
        }
        ContactListManager.refreshContactList();
    }

    public static void runEventLoop() {
        while (!isShuttingDown) {
            synchronized (appLock) {
                if (!isShuttingDown) {
                    AppState.updateTime();
                    updateClock();
                    detectLongPress();
                    pollAccountStates();
                    expirePendingConnections();
                    refreshContactListIfNeeded();
                    Object event = Utils.dequeue(AppState.getVector(SessionKeys.VEC_EVENT_QUEUE));
                    if (event == null) {
                        ListView currentScreen = ScreenManager.getCurrentScreen();
                        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
                        Object menuData = menuItem == null ? null : menuItem.data;
                        String menuTitle = menuItem == null ? null : menuItem.title;
                        int nextState = 0;
                        ScreenHandler handler = ScreenHandlerRegistry.getHandler(ScreenManager.getCurrentScreen().screenId);
                        if (handler != null) {
                            nextState = handler.onIdleProcess(currentScreen, menuItem, menuData, menuTitle);
                        }
                        if (nextState == ScreenId.CLOSE) {
                            ScreenBuilder.onScreenClosed();
                        } else if (nextState != ScreenId.NONE) {
                            ScreenBuilder.openScreen(nextState);
                        }
                    } else if (event instanceof KeyEvent) {
                        handleKeyEvent((KeyEvent) event);
                    } else if (event instanceof CommandEvent) {
                        handleCommandEvent((CommandEvent) event);
                    } else if (event instanceof PointerEvent) {
                        if (handlePointerEvent((PointerEvent) event)) {
                            break;
                        }
                    } else if (event instanceof NotificationEvent) {
                        handleNotificationEvent((NotificationEvent) event);
                    } else if (event instanceof AccountDataEvent) {
                        handleAccountDataEvent((AccountDataEvent) event);
                    } else if (event instanceof ProtocolEvent) {
                        handleProtocolEvent((ProtocolEvent) event);
                    } else if (event instanceof MenuItemEvent) {
                        handleMenuItemEvent((MenuItemEvent) event);
                    }
                    if (!AppState.getBool(SettingsKeys.SETTING_STATUS_BAR_VISIBLE)) {
                        ListView activeScreen = ScreenManager.getCurrentScreen();
                        if (activeScreen != null) {
                            AppState.getCanvas().setCommands(activeScreen.titleLeft, activeScreen.titleRight);
                        }
                    }
                    SoundPlayer.checkSoundTimer();
                    if (TimerManager.isTimerExpired(TimerManager.timers[TimerManager.SLOT_BACKLIGHT]) && (!AppState.getBool(SessionKeys.FLAG_KEEP_SCREEN_ON) || ScreenManager.getCurrentScreen().screenId != ScreenId.MAP)) {
                        if (AppState.getCanvas().isShown()) {
                            TimerManager.disableBacklight();
                        } else {
                            TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
                        }
                    }
                }
            }
            String savedStr = AppState.getString(UIKeys.SLOT_SAVED_STRING);
            if (savedStr != null) {
                isBackgrounded = true;
                try {
                    AppState.getMidlet().platformRequest(savedStr);
                } catch (Throwable unused) {
                }
                AppState.clearIndex(UIKeys.SLOT_SAVED_STRING);
            }
            if (isBackgrounded) {
                AppState.getMidlet().destroyApp(true);
                isShuttingDown = true;
                return;
            }
            handleScreenRepaint();
        }
    }

    private static void detectLongPress() {
        if (!MainCanvas.pointerDragged && MainCanvas.pointerDownTime != 0 && System.currentTimeMillis() - MainCanvas.pointerDownTime > LONG_PRESS_MS) {
            int longPressX = MainCanvas.pointerDownX;
            int longPressY = MainCanvas.pointerDownY;
            Vector eventQueue = AppState.getVector(SessionKeys.VEC_EVENT_QUEUE);
            synchronized (eventQueue) {
                eventQueue.addElement(PointerEvent.longPress(longPressX, longPressY));
            }
            MainCanvas.pointerDownTime = 0L;
        }
    }

    private static void pollAccountStates() {
        Vector accounts = AppState.getVector(SessionKeys.VEC_ACCOUNTS);
        for (int acctIdx = accounts.size() - 1; acctIdx >= 0; acctIdx--) {
            Account acct = (Account) accounts.elementAt(acctIdx);
            try {
                if (acct.progress <= Account.PROGRESS_DISCONNECTED || acct.progress == Account.PROGRESS_CONNECTED) {
                    Vector popupItems = AppState.getVector(UIKeys.VEC_POPUP_ITEMS);
                    if (popupItems.contains(acct)) {
                        Utils.removeFrom(popupItems, acct);
                        updatePopupHeight();
                    }
                } else {
                    Vector popupItems = AppState.getVector(UIKeys.VEC_POPUP_ITEMS);
                    if (!popupItems.contains(acct)) {
                        popupItems.addElement(acct);
                        updatePopupHeight();
                    }
                }
                acct.loadData();
            } catch (Throwable unused) {
                acct.handleConnError();
            }
        }
    }

    private static void expirePendingConnections() {
        int currentTimestamp = AppState.getInt(RuntimeKeys.INT_CURRENT_TIMESTAMP);
        Vector pendingConns = AppState.getVector(UIKeys.VEC_PENDING_CONNECTIONS);
        for (int connIdx = pendingConns.size() - 1; connIdx >= 0; connIdx--) {
            Contact contact = (Contact) pendingConns.elementAt(connIdx);
            if (Utils.abs(currentTimestamp - contact.statusCode) > PENDING_CONN_TIMEOUT_MS) {
                ContactListManager.deleteContact(contact);
            }
        }
    }

    private static void refreshContactListIfNeeded() {
        if (needsLayoutUpdate && ScreenManager.getCurrentScreen().screenId == ScreenId.CONTACT_LIST && TimerManager.isTimerType(TimerManager.SLOT_CONTACT_REFRESH)) {
            needsLayoutUpdate = false;
            AppState.getString(ContactKeys.SLOT_CURRENT_CONTACT_ID);
            ContactListManager.refreshList();
            AppState.clearIndex(ContactKeys.SLOT_CURRENT_CONTACT_ID);
            TimerManager.setTimer(TimerManager.SLOT_CONTACT_REFRESH, CONTACT_REFRESH_MS);
        }
    }

    private static void handleScreenRepaint() {
        if ((AccountManager.getCombinedContactFlags() != 0 || AccountManager.hasActiveConnection()) && TimerManager.isTimerType(TimerManager.SLOT_REPAINT)) {
            needsRepaint = true;
        }
        MainCanvas canvas = AppState.getCanvas();
        if (!isShuttingDown && needsRepaint) {
            Object pendingScreen = AppState.currentScreen;
            boolean screenSwitched;
            if (pendingScreen != null) {
                if (pendingScreen == AppState.getCanvas()) {
                    AppState.getCanvas().updateFullScreenMode();
                }
                Display.getDisplay(AppState.getMidlet()).setCurrent(pendingScreen instanceof Displayable ? (Displayable) pendingScreen : null);
                TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
                AppState.currentScreen = null;
                screenSwitched = true;
            } else {
                screenSwitched = false;
            }
            if (!screenSwitched) {
                if (canvas.isShown()) {
                    canvas.repaint();
                    TimerManager.setTimer(TimerManager.SLOT_REPAINT, REPAINT_INTERVAL_MS);
                } else {
                    try {
                        Thread.sleep(SLEEP_BACKGROUND_MS);
                    } catch (Throwable unused) {
                    }
                }
            }
        }
        try {
            Thread.sleep(TimerManager.isTimerType(TimerManager.SLOT_ANIMATION) ? SLEEP_ANIMATION_MS : SLEEP_ACTIVE_MS);
        } catch (Throwable unused) {
        }
    }

    private static void handleKeyEvent(KeyEvent keyEvt) {
        ListView currentScreen = ScreenManager.getCurrentScreen();
        if (currentScreen == null) {
            return;
        }
        if (currentScreen.screenId != ScreenId.MAP) {
            needsRepaint = true;
        }
        int keyCode = keyEvt.keyCode;
        int gameAction = keyEvt.gameAction;
        int screenId = currentScreen.screenId;
        boolean isLastTab = TabBar.currentIndex == AppState.getVector(UIKeys.VEC_TAB_BARS).size() - 1;
        boolean keyConsumed = false;
        if (screenId == ScreenId.CONTACT_LIST) {
            keyConsumed = handleContactListKeys(currentScreen, gameAction);
        } else if (screenId == ScreenId.MAIL_ACCOUNT_LIST) {
            keyConsumed = handleMailListKeys(gameAction, isLastTab);
        } else if (screenId == ScreenId.MAP) {
            keyConsumed = handleMapKeys(keyCode, gameAction, currentScreen, isLastTab);
        }
        if (!keyConsumed) {
            handleDefaultKeys(keyCode, gameAction, currentScreen);
        }
    }

    private static boolean handleContactListKeys(ListView screen, int gameAction) {
        int tabCount = AppState.getVector(UIKeys.VEC_TAB_BARS).size();
        ContactListManager.clearState();
        if (tabCount <= 1) {
            return false;
        }
        AppState.setInt(MapKeys.FLAG_MAP_OVERLAY_ACTIVE, 0);
        if (gameAction == Canvas.LEFT) {
            if (screen.isAtStart()) {
                TabBar prevTab = TabBar.getPreviousTab();
                if (prevTab != null) {
                    ScreenBuilder.openScreen(prevTab.selectTab());
                }
                return true;
            }
        } else if (gameAction == Canvas.RIGHT) {
            if (screen.isAtEnd()) {
                TabBar nextTab = TabBar.getNextTab();
                if (nextTab != null) {
                    ScreenBuilder.openScreen(nextTab.selectTab());
                }
                return true;
            }
        }
        return false;
    }

    private static boolean handleMailListKeys(int gameAction, boolean isLastTab) {
        AppState.setInt(MapKeys.FLAG_MAP_OVERLAY_ACTIVE, 0);
        if (gameAction == Canvas.LEFT) {
            ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
            return true;
        }
        if (gameAction == Canvas.RIGHT) {
            if (!isLastTab) {
                ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
            }
            return true;
        }
        return false;
    }

    private static boolean handleMapKeys(int keyCode, int gameAction, ListView screen, boolean isLastTab) {
        if (AppState.getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            if (keyCode == Canvas.KEY_STAR) {
                Conversation.incrementZoom();
            } else if (keyCode == Canvas.KEY_POUND) {
                Conversation.decrementZoom();
            } else if (keyCode == Canvas.KEY_NUM0) {
                Telemetry.sendReport(false, null);
                ScreenBuilder.openScreen(ScreenId.MAP);
            } else if (keyCode == Canvas.KEY_NUM1) {
                ScreenBuilder.openScreen(ScreenId.MAP_POINTS);
            } else if (keyCode == Canvas.KEY_NUM2) {
                boolean isEnabled = AppState.getBool(MapKeys.MAP_GPS_ENABLED);
                Conversation.setMapEnabled(!isEnabled);
                AppState.setBool(MapKeys.MAP_GPS_ENABLED, !isEnabled);
                ScreenBuilder.openScreen(ScreenId.MAP);
            } else if (keyCode == Canvas.KEY_NUM3) {
                EventDispatcher.postEvent(new ProtocolEvent(ProtocolEvent.MAP_CONTROL, null));
            } else if (keyCode == Canvas.KEY_NUM5) {
                AppState.setBool(SettingsKeys.SETTING_CUSTOM_VIEW_MODE, !AppState.getBool(SettingsKeys.SETTING_CUSTOM_VIEW_MODE));
                MapRenderer.needsRedraw = true;
            } else if (keyCode == Canvas.KEY_NUM7) {
                int[] prevPoint;
                if (MmpContact.locationEnabled && (prevPoint = MmpContact.getPrevRoutePoint()) != null) {
                    MapRenderer.animateTo(prevPoint[0], prevPoint[1]);
                }
            } else if (keyCode == Canvas.KEY_NUM9) {
                int[] nextPoint;
                if (MmpContact.locationEnabled && (nextPoint = MmpContact.getNextRoutePoint()) != null) {
                    MapRenderer.animateTo(nextPoint[0], nextPoint[1]);
                }
            } else {
                return false;
            }
            return true;
        }
        if (gameAction == Canvas.LEFT) {
            ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
            return true;
        }
        if (gameAction == Canvas.RIGHT) {
            if (!isLastTab) {
                ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
            }
            return true;
        }
        if (gameAction == Canvas.UP) {
            return true;
        }
        if (gameAction == Canvas.DOWN) {
            MapController.handleMapSwitch(screen);
            return true;
        }
        return false;
    }

    private static void handleDefaultKeys(int keyCode, int gameAction, ListView screen) {
        int keyAction = keyCode == Canvas.KEY_STAR ? getKeyAction(AppState.getInt(SettingsKeys.SETTING_KEY_STAR_ACTION)) : keyCode == Canvas.KEY_POUND ? getKeyAction(AppState.getInt(SettingsKeys.SETTING_KEY_HASH_ACTION)) : (keyCode < Canvas.KEY_NUM0 || keyCode > Canvas.KEY_NUM9) ? 0 : getKeyAction(AppState.getInt(keyCode + KEY_BINDING_OFFSET));
        if (keyAction != 0) {
            ScreenBuilder.openScreen(keyAction);
        } else if (gameAction == Canvas.FIRE) {
            onItemSelected();
        } else if (gameAction == Canvas.UP) {
            screen.scrollUp();
        } else if (gameAction == Canvas.DOWN) {
            screen.scrollDown();
        } else if (gameAction == Canvas.LEFT) {
            if (screen.showCheckboxes) {
                ScreenBuilder.onScreenClosed();
            } else if (screen.screenId == ScreenId.MAP) {
                AppState.setInt(MapKeys.INT_MAP_SCROLL_DIRECTION, SCROLL_LEFT);
            } else if (screen.layoutMode == LAYOUT_GRID) {
                int selectedIdx = screen.selectedIndex;
                int itemCount = screen.menuItems.size();
                screen.selectedIndex = ((selectedIdx + itemCount) - 1) % itemCount;
                screen.invalidateLayout();
            }
        } else if (gameAction == Canvas.RIGHT) {
            screen.onActionKey();
        }
    }

    private static void handleCommandEvent(CommandEvent cmdEvt) {
        int cmdType = cmdEvt.command;
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
                AppState.setInt(MapKeys.INT_MAP_SCROLL_DIRECTION, SCROLL_RIGHT);
            }
        }
    }

    // Returns true if the caller should break out of the synchronized block
    private static boolean handlePointerEvent(PointerEvent ptrEvt) {
        switch (ptrEvt.action) {
            case PointerEvent.PRESS:
                return handlePointerPress(ptrEvt.x, ptrEvt.y);
            case PointerEvent.DRAG:
                handlePointerDrag(ptrEvt.x, ptrEvt.y);
                break;
            case PointerEvent.RELEASE:
                handlePointerRelease(ptrEvt);
                break;
            case PointerEvent.LONG_PRESS:
                handleLongPress(ptrEvt.x, ptrEvt.y);
                break;
        }
        return false;
    }

    private static boolean handlePointerPress(int ptrX, int ptrY) {
        if (AppState.getBool(SettingsKeys.SETTING_STATUS_BAR_VISIBLE) && ptrY > AppState.getHeight()) {
            if (ptrX < (AppState.getInt(UIKeys.INT_SCREEN_WIDTH) >> 1)) {
                ScreenBuilder.onMenuItemSelected();
            } else {
                ScreenBuilder.onMenuItemAction();
            }
            return true;
        }
        int screenId = ScreenManager.getCurrentScreen().screenId;
        if (screenId == ScreenId.MAIN_SCREEN) {
            return true;
        }
        if (ptrY > HEADER_HEIGHT || !ScreenManager.hasModal()) {
            if (screenId != ScreenId.NONE) {
                if (screenId == ScreenId.CONTACT_LIST) {
                    ContactListManager.clearState();
                }
                ScreenBuilder.openScreen(ScreenId.NONE);
            }
            return true;
        }
        int headerWidth = AppState.getInt(UIKeys.INT_SCREEN_WIDTH) - HEADER_HEIGHT;
        int targetScreen = ScreenId.NONE;
        if (AccountManager.getCombinedContactFlags() == 0) {
            if (!AccountManager.hasActiveConnection() && ptrX > headerWidth) {
                targetScreen = ScreenId.MAIL_ACCOUNT_LIST;
            }
        } else if (ptrX > headerWidth) {
            targetScreen = !AppState.getBool(SettingsKeys.SETTING_MULTI_ACCOUNT) ? ScreenId.CONTACT_LIST : ScreenId.NONE;
        }
        if (targetScreen <= 0) {
            if (screenId != targetScreen) {
                if (screenId == ScreenId.CONTACT_LIST) {
                    ContactListManager.clearState();
                }
                ScreenBuilder.openScreen(targetScreen);
            }
            return true;
        }
        return false;
    }

    private static void handlePointerDrag(int dragX, int dragY) {
        ListView currentScreen = ScreenManager.getCurrentScreen();
        if (currentScreen == null || !currentScreen.touchConsumed) {
            return;
        }
        int relX = dragX - currentScreen.offsetX;
        int relY = dragY - (currentScreen.offsetY + currentScreen.contentTop);
        if (currentScreen.marginLeft == 0 && currentScreen.marginTop == 0) {
            currentScreen.marginLeft = relX;
            currentScreen.marginTop = relY;
        }
        int deltaX = relX - currentScreen.marginLeft;
        int deltaY = relY - currentScreen.marginTop;
        currentScreen.marginLeft = relX;
        currentScreen.marginTop = relY;
        if (currentScreen.screenId == ScreenId.MAP) {
            MapController.toggleMapControls(currentScreen);
            MapRenderer.dragActive = true;
            MapRenderer.rippleTimestamp = 0L;
            int zoomLevel = AppState.getInt(MapKeys.MAP_ZOOM_LEVEL);
            MapRenderer.setPosition(MapRenderer.currentLon - ((int) MapUtils.pixelToCoord(deltaX, zoomLevel)), MapRenderer.currentLat + ((int) MapUtils.pixelToCoord(deltaY, zoomLevel)));
            MapRenderer.needsRedraw = true;
        } else {
            currentScreen.scrollOffset -= deltaY;
            if (currentScreen.totalHeight < currentScreen.contentHeight) {
                currentScreen.scrollOffset = 0;
            }
            if (currentScreen.scrollOffset > currentScreen.totalHeight - currentScreen.contentHeight) {
                currentScreen.scrollOffset = currentScreen.totalHeight - currentScreen.contentHeight;
            }
            if (currentScreen.scrollOffset < 0) {
                currentScreen.scrollOffset = 0;
            }
            needsRepaint = true;
        }
    }

    private static void handlePointerRelease(PointerEvent ptrEvt) {
        int releaseX = ptrEvt.x;
        int releaseY = ptrEvt.y;
        int startX = ptrEvt.startX;
        int startY = ptrEvt.startY;
        boolean wasDragged = ptrEvt.wasDragged;
        ListView currentScreen = ScreenManager.getCurrentScreen();
        if (currentScreen != null) {
            currentScreen.onPointerEvent(releaseX, releaseY, startX, startY, wasDragged);
        }
    }

    private static void handleLongPress(int longPressX, int longPressY) {
        ListView currentScreen = ScreenManager.getCurrentScreen();
        if (currentScreen != null) {
            int localX = longPressX - currentScreen.offsetX;
            int localY = longPressY - currentScreen.offsetY;
            currentScreen.touchConsumed = false;
            if (currentScreen.screenId == ScreenId.MAP) {
                int contentY = localY - currentScreen.contentTop;
                MapController.toggleMapControls(currentScreen);
                MapRenderer.onDrag(localX, contentY);
            }
        }
    }

    private static void handleNotificationEvent(NotificationEvent notifEvt) {
        NotificationHelper.showNotification(notifEvt.message);
        needsRepaint = true;
    }

    private static void handleAccountDataEvent(AccountDataEvent acctEvt) {
        Object[] evtData = acctEvt.data;
        if (evtData[0] instanceof MrimAccount) {
            AppState.setInt(UIKeys.INT_HTTP_RESULT_SCREEN, ScreenId.CONTACT_LIST_KEY);
            AppState.setObject(MapKeys.SLOT_MAP_POINT_1, evtData[1]);
            MrimAccount mrimAccount = (MrimAccount) evtData[0];
            mrimAccount.chatRoomManager.loaded = true;
            AppState.pool[SessionKeys.SLOT_TEMP_ACCOUNT] = mrimAccount;
            ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ERROR_ALERT));
            AppState.clearIndex(MapKeys.SLOT_MAP_POINT_1);
            needsRepaint = true;
        } else {
            ((MrimAccount) evtData[1]).addOfflineContact((String) evtData[0]);
        }
    }

    private static void handleProtocolEvent(ProtocolEvent protoEvt) {
        int eventType = protoEvt.type;
        Object eventData = protoEvt.data;
        switch (eventType) {
            case ProtocolEvent.MAP_LOCATIONS_LOADED:
                MapController.showSavedLocations();
                break;
            case ProtocolEvent.PHONE_SEARCH_RESULT:
                Object[] resultArr = (Object[]) eventData;
                PhoneContact phoneContact = (PhoneContact) resultArr[0];
                AppState.pool[UIKeys.RANGE_PHONE_CONTACT_START] = phoneContact;
                Vector resultVector = (Vector) resultArr[1];
                AppState.pool[UIKeys.VEC_PHONE_RESULTS] = resultVector;
                int scrollOffset = ((Integer) resultArr[2]).intValue();
                AppState.setInt(RuntimeKeys.INT_PHONE_SCROLL_OFFSET, scrollOffset);
                ListView popupScreen = ScreenManager.createScreen(ScreenDef.CONTACT_POPUP);
                if (scrollOffset >= PHONE_PAGE_SIZE) {
                    popupScreen.addIconItemWithData(ICON_NAVIGATION, AppState.getString(StringResKeys.STR_MENU_SMS), PAGE_ACTION_PREV, null);
                }
                for (int resultIdx = resultVector.size() - 1; resultIdx >= 0; resultIdx--) {
                    UserSearchResult searchResult = (UserSearchResult) resultVector.elementAt(resultIdx);
                    int genderIcon = searchResult.gender == GENDER_MALE ? ICON_GENDER_MALE : searchResult.gender == GENDER_FEMALE ? ICON_GENDER_FEMALE : ICON_GENDER_UNKNOWN;
                    popupScreen.addIconItemWithData(genderIcon, searchResult.getText(), 0, searchResult);
                }
                if (scrollOffset < phoneContact.userCount - PHONE_PAGE_SIZE) {
                    popupScreen.addIconItemWithData(ICON_NAVIGATION, AppState.getString(StringResKeys.STR_MENU_CALL), PAGE_ACTION_NEXT, null);
                }
                AppState.setBool(UIKeys.FLAG_PHONE_HAS_NEXT, scrollOffset < phoneContact.userCount - PHONE_PAGE_SIZE);
                AppState.setBool(UIKeys.FLAG_PHONE_HAS_PREV, scrollOffset >= PHONE_PAGE_SIZE);
                ScreenManager.showScreen(popupScreen);
                // fall through
            case ProtocolEvent.ADD_CONTACT_CONFIRM:
                AppState.setInt(UIKeys.FLAG_SHOW_PHOTO, 1);
                ContactListManager.showAddContactScreen();
                break;
            case ProtocolEvent.ACCOUNT_SYNC:
                ((MrimAccount) eventData).profileManager.sync();
                break;
            case ProtocolEvent.MAP_CONTROL:
                break;
        }
        needsRepaint = true;
    }

    private static void handleMenuItemEvent(MenuItemEvent menuEvt) {
        needsRepaint = true;
        needsLayoutUpdate = true;
        ListView currentScreen = ScreenManager.getCurrentScreen();
        MenuItem eventItem = menuEvt.item;
        ScreenHandler handler = ScreenHandlerRegistry.getHandler(currentScreen.screenId);
        if (handler != null) {
            handler.onMenuItemEvent(currentScreen, eventItem);
        }
    }

    public static void onItemSelected() {
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        if (menuItem != null && menuItem.execute(ScreenManager.getCurrentScreen()) != -1) {
            return;
        }
        ListView screen = ScreenManager.getCurrentScreen();
        int selectedOption = ScreenManager.getCurrentWidth();
        MenuItem currentItem = ScreenManager.getCurrentMenuItem();
        MenuItem headerItem = AppState.getVector(UIKeys.VEC_SCREEN_STACK).size() > 0 ? screen.getHeaderItem() : null;
        int actionResult = 0;
        ScreenHandler handler = ScreenHandlerRegistry.getHandler(screen.screenId);
        if (handler != null) {
            actionResult = handler.onItemSelected(screen, currentItem, ScreenManager.getCurrentTitle(),
                    selectedOption, currentItem == null ? null : currentItem.data,
                    headerItem == null ? null : headerItem.data);
        }
        if (actionResult == -1) {
            return;
        }
        if (actionResult == ScreenId.CLOSE) {
            ScreenBuilder.onScreenClosed();
            return;
        }
        if (actionResult != ScreenId.NONE) {
            ScreenBuilder.openScreen(actionResult);
            return;
        }
        int softKeyAction = screen.softKeyRight;
        if (softKeyAction == SOFTKEY_NO_ACTION) {
            return;
        }
        int targetScreen = softKeyAction == SOFTKEY_USE_SELECTED ? selectedOption : softKeyAction;
        if (targetScreen == ScreenId.CLOSE) {
            ScreenBuilder.onScreenClosed();
        } else if (targetScreen != ScreenId.NONE) {
            ScreenBuilder.openScreen(targetScreen);
        }
    }

    private static int getKeyAction(int action) {
        ListView screen = ScreenManager.getCurrentScreen();
        if (screen.screenId == ScreenId.MAIN_SCREEN) {
            return 0;
        }
        switch (action) {
            case KEY_ACTION_BACK:
                break;
            case KEY_ACTION_SCROLL_END:
                scrollToEnd(screen);
                break;
            case KEY_ACTION_TOGGLE_SORT:
                AppState.toggleBool(SettingsKeys.SETTING_SORT_ORDER);
                needsLayoutUpdate = true;
                break;
            case KEY_ACTION_SELECT_ITEM:
                if (screen.screenId == ScreenId.SEARCH_RESULT_LIST) {
                    AppState.pool[ContactKeys.SLOT_CONTACT_INFO] = screen.getSelectedItem().data;
                }
                break;
        }
        return 0;
    }

    private static void scrollToEnd(ListView screen) {
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
            int[] cache = screen.layoutCache;
            screen.scrollOffset = cache[cache[0]];
        }
        screen.invalidateLayout();
    }

    private static void updatePopupHeight() {
        AppState.setInt(UIKeys.INT_POPUP_HEIGHT, AppState.getVector(UIKeys.VEC_POPUP_ITEMS).size() * Utils.max(MIN_POPUP_ICON_HEIGHT, AppState.getInt(UIKeys.INT_FONT_HEIGHT)));
        needsRepaint = true;
    }

}
