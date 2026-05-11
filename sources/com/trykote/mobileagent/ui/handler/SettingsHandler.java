package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
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
import javax.microedition.lcdui.Image;

public final class SettingsHandler extends BaseScreenHandler {

    // Notification option action IDs (label resource IDs from screen definition)
    private static final int ACTION_COMPOSE_EMAIL = 54;
    private static final int ACTION_TOGGLE_ONLINE = 68;
    private static final int ACTION_MARK_LOADED = 37;

    // Key mapping action IDs (label resource IDs from screen definition)
    private static final int KEY_ACTION_CLOSE = 4;
    private static final int KEY_ACTION_BACK_TWO = 137;

    public static void showSettingsScreen() {
        SessionState.setInitComplete(0);
        UIState.setFullscreenActive(1);
        ScreenManager.pushScreen(Screens.settingsMain(null));
    }

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.SETTINGS:
                showSettingsScreen();
                return;
            case ScreenId.SETTINGS_MENU:
                Screens.settingsMenu(this).show();
                return;
            case ScreenId.GPS_SETTINGS:
                boolean flag = MapState.isGpsEnabled();
                boolean flag2 = ContactState.isListActive();
                MapState.setGpsNoMap(!flag && flag2);
                MapState.setGpsWithMap(flag && flag2);
                Screens.gpsSettings(this).show();
                return;
            case ScreenId.THEME_SETTINGS:
                SettingsState.setThemeCache(SettingsState.getColorTheme());
                Screens.themeSettings(this).show();
                return;
            case ScreenId.NOTIFICATION_SETTINGS:
                Screens.notificationSettings(this).show();
                return;
            case ScreenId.SOUND_SETTINGS:
                Screens.soundSettings(this).show();
                return;
            case ScreenId.PRIVACY_SETTINGS:
                Screens.privacySettings(this).show();
                return;
            case ScreenId.CONNECTION_SETTINGS:
                Screens.connectionSettings(this).show();
                return;
            case ScreenId.NOTIFICATION_OPTIONS:
                Screens.notificationOptions(this).show();
                return;
            case ScreenId.THEME_OPTIONS:
                Screens.themeOptions(this).show();
                return;
            case ScreenId.VIEW_MODE:
                boolean isOnline4 = ContactState.isListActive();
                boolean isCustom = SettingsState.getCustomViewMode() != 0;
                UIState.setOnlineCustomOff(isOnline4 && !isCustom);
                UIState.setOnlineCustomOn(isOnline4 && isCustom);
                Screens.viewMode(this).show();
                return;
            case ScreenId.COLOR_PICKER:
                Screens.colorPicker(this).show();
                return;
            case ScreenId.KEY_MAPPING:
                Screens.keyMapping(this).show();
                return;
            case ScreenId.FORM_SETTINGS:
                Screens.formSettings(this).show();
                return;
            case ScreenId.EXT_SETTINGS:
                Screens.extSettings(this).show();
                return;
            case ScreenId.MAP_VIEW_SETTINGS:
                MapController.showMapView();
                Screens.mapViewSettings(this).show();
                return;
            case ScreenId.NEARBY_SETTINGS:
                Screens.nearbySettings(this).show();
                return;
        }
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.SETTINGS:
                return 0;
            case ScreenId.SETTINGS_MENU:
                return handleSettingsOption(action);
            case ScreenId.GPS_SETTINGS:
                return handleProfileAction(action);
            case ScreenId.THEME_SETTINGS:
                ScreenManager.processScreenForm();
                SettingsState.setThemeCache(SettingsState.getColorTheme());
                ScreenManager.initializeFonts();
                AppState.getCanvas().updateFullScreenMode();
                TabBar.initialize();
                AppController.resetClock();
                return 0;
            case ScreenId.NOTIFICATION_SETTINGS:
                return ScreenManager.processScreenForm();
            case ScreenId.SOUND_SETTINGS:
                return ScreenManager.processScreenForm();
            case ScreenId.PRIVACY_SETTINGS:
                return ScreenManager.processScreenForm();
            case ScreenId.CONNECTION_SETTINGS:
                return AccountManager.handleConnectionOption(action);
            case ScreenId.NOTIFICATION_OPTIONS:
                return handleNotificationOption(action);
            case ScreenId.THEME_OPTIONS:
                return ScreenManager.handleThemeOption(action);
            case ScreenId.VIEW_MODE:
                return ScreenManager.getThemeBackground(action);
            case ScreenId.COLOR_PICKER:
                return ScreenManager.getThemeColor(action);
            case ScreenId.KEY_MAPPING:
                return mapKeyToAction(action);
            case ScreenId.FORM_SETTINGS:
                return ScreenManager.processScreenForm();
            case ScreenId.EXT_SETTINGS:
                return handleExtSettingsOption(action);
            case ScreenId.MAP_VIEW_SETTINGS:
                return handleContactOption(action);
            case ScreenId.NEARBY_SETTINGS:
                ScreenManager.processScreenForm();
                if (MapState.isMapDataLoaded()) {
                    MapUtils.requestNearbyPeople();
                }
                return 0;
        }
        return 0;
    }

    public int onMenuItemAction(ListView screen, MenuItem item, Object data) {
        switch (screen.screenId) {
            case ScreenId.SETTINGS:
                return 0;
            case ScreenId.SETTINGS_MENU:
                return 0;
            case ScreenId.GPS_SETTINGS:
                return 0;
            case ScreenId.THEME_SETTINGS:
                AppController.needsLayoutUpdate = true;
                return 0;
            case ScreenId.NOTIFICATION_SETTINGS:
                return 0;
            case ScreenId.SOUND_SETTINGS:
                return 0;
            case ScreenId.PRIVACY_SETTINGS:
                return 0;
            case ScreenId.CONNECTION_SETTINGS:
                return 0;
            case ScreenId.NOTIFICATION_OPTIONS:
                return 0;
            case ScreenId.THEME_OPTIONS:
                return 0;
            case ScreenId.VIEW_MODE:
                return 0;
            case ScreenId.COLOR_PICKER:
                return 0;
            case ScreenId.KEY_MAPPING:
                return 0;
            case ScreenId.FORM_SETTINGS:
                return 0;
            case ScreenId.EXT_SETTINGS:
                return 0;
            case ScreenId.MAP_VIEW_SETTINGS:
                return 0;
            case ScreenId.NEARBY_SETTINGS:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.SETTINGS:
                SettingsState.setStatusBarVisible(UIState.isFullscreenRequested());
                AppState.getCanvas().updateFullScreenMode();
                UIState.setFullscreenActive(0);
                break;
            case ScreenId.THEME_SETTINGS:
                SettingsState.setColorTheme(SettingsState.getThemeCache());
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.SETTINGS:
                return 0;
            case ScreenId.SETTINGS_MENU:
                return handleSettingsOption(selectedOption);
            case ScreenId.GPS_SETTINGS:
                return handleProfileAction(selectedOption);
            case ScreenId.THEME_SETTINGS:
                return 0;
            case ScreenId.NOTIFICATION_SETTINGS:
                return 0;
            case ScreenId.SOUND_SETTINGS:
                return 0;
            case ScreenId.PRIVACY_SETTINGS:
                return 0;
            case ScreenId.CONNECTION_SETTINGS:
                return AccountManager.handleConnectionOption(selectedOption);
            case ScreenId.NOTIFICATION_OPTIONS:
                return handleNotificationOption(selectedOption);
            case ScreenId.THEME_OPTIONS:
                return ScreenManager.handleThemeOption(selectedOption);
            case ScreenId.VIEW_MODE:
                return ScreenManager.getThemeBackground(selectedOption);
            case ScreenId.COLOR_PICKER:
                return ScreenManager.getThemeColor(selectedOption);
            case ScreenId.KEY_MAPPING:
                return mapKeyToAction(selectedOption);
            case ScreenId.FORM_SETTINGS:
                return 0;
            case ScreenId.EXT_SETTINGS:
                return handleExtSettingsOption(selectedOption);
            case ScreenId.MAP_VIEW_SETTINGS:
                return handleContactOption(selectedOption);
            case ScreenId.NEARBY_SETTINGS:
                return 0;
        }
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        return 0;
    }

    public void onMenuItemEvent(ListView screen, MenuItem item) {
        if (screen.screenId == ScreenId.THEME_SETTINGS) {
            Object[] themeData = (Object[]) item.data;
            if (ResourceAccessor.str(StringResKeys.STR_MENU_SETTINGS).equals(item.title)) {
                SettingsState.setColorTheme(((Integer) themeData[0]).intValue());
            }
        } else if (screen.screenId == ScreenId.SOUND_SETTINGS) {
            NotificationHelper.playAlertIfEnabled(((Integer) ((Object[]) item.data)[0]).intValue(), false);
        }
    }

    public static int handleContactOption(int optionId) {
        if (optionId != 6) {
            return 0;
        }
        MrimAccount account = (MrimAccount) AppState.getAccount();
        account.isHighlighted = true;
        if (!account.isSelected()) {
            return NotificationHelper.showError(667);
        }
        MapController.applyViewMode(true, false, !MapState.isMapViewActive());
        ContactState.setRefreshNeeded(true);
        MapController.selectMapItem((ListItem) account);
        return 0;
    }

    public static int handleProfileAction(int actionId) {
        switch (actionId) {
            case 0: Telemetry.sendReport(false, null); return ScreenId.CLOSE;
            case 1: MapController.applyViewMode(false, true, true); return ScreenId.CLOSE;
            case 2: MapController.applyViewMode(true, false, true); return ScreenId.CLOSE;
            case 3: Conversation.setMapEnabled(true); return ScreenId.CLOSE;
            case 4: Conversation.setMapEnabled(false); return ScreenId.CLOSE;
            case 5: return ScreenManager.getIconOffset();
            default: return 0;
        }
    }

    public static int handleSettingsOption(int optionId) {
        RuntimeState.setPeriodIndex(optionId);
        return ScreenId.TRAFFIC_STATS;
    }

    public static int handleExtSettingsOption(int actionId) {
        MrimAccount account = (MrimAccount) AppState.getAccount();
        switch (actionId) {
            case 0: case 1: case 2: case 3:
                if (account != null) {
                    applyProfileAction(account.profileManager, actionId);
                } else {
                    forEachMrimProfile(actionId);
                }
                break;
            case 4: return ScreenId.PHOTO_SELECTOR;
        }
        if (SessionState.isUpdateAvailable()) {
            return SessionState.getConnectionState();
        }
        ScreenBuilder.onScreenClosed();
        return ScreenId.UPDATE_ALERT;
    }

    private static void forEachMrimProfile(int actionId) {
        Vector accounts = AccountManager.getMrimAccountList();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            applyProfileAction(AccountManager.getMrimAccount(accounts, i).profileManager, actionId);
        }
        ObjectPool.releaseVector(accounts);
    }

    private static void applyProfileAction(MrimProfileManager profileManager, int actionId) {
        switch (actionId) {
            case 0: profileManager.publishLocation(); break;
            case 1: profileManager.hideLocation(); break;
            case 2: profileManager.setGroups(); break;
            case 3: profileManager.clearGroups(); break;
        }
    }

    public static int handleNotificationOption(int optionId) {
        if (optionId == ACTION_COMPOSE_EMAIL) {
            ScreenBuilder.onScreenClosed();
            MailHelper.composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (optionId == ACTION_TOGGLE_ONLINE) {
            ScreenBuilder.onScreenClosed();
            ChatHandler.toggleOnlineMode(true);
            return 0;
        }
        if (optionId != ACTION_MARK_LOADED) {
            return 0;
        }
        ((MrimAccount) AppState.getAccount()).chatRoomManager.loaded = true;
        return 0;
    }

    public static int mapKeyToAction(int keyCode) {
        if (keyCode == KEY_ACTION_CLOSE) {
            ScreenManager.handleScreenClose();
            return 0;
        }
        if (keyCode != KEY_ACTION_BACK_TWO) {
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }
}
