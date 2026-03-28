package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
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

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.SETTINGS:
                AppController.showSettingsScreen();
                return;
            case ScreenId.SETTINGS_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SETTINGS_MENU));
                return;
            case ScreenId.GPS_SETTINGS:
                boolean flag = AppState.getBool(MapKeys.MAP_GPS_ENABLED);
                boolean flag2 = AppState.getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE);
                AppState.setBool(MapKeys.FLAG_GPS_NO_MAP, !flag && flag2);
                AppState.setBool(MapKeys.FLAG_GPS_WITH_MAP, flag && flag2);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.GPS_SETTINGS));
                return;
            case ScreenId.THEME_SETTINGS:
                AppState.setInt(SettingsKeys.INT_THEME_CACHE, AppState.getInt(SettingsKeys.SETTING_COLOR_THEME));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.THEME_SETTINGS));
                return;
            case ScreenId.NOTIFICATION_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.NOTIFICATION_SETTINGS));
                return;
            case ScreenId.SOUND_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SOUND_SETTINGS));
                return;
            case ScreenId.PRIVACY_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.PRIVACY_SETTINGS));
                return;
            case ScreenId.CONNECTION_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONNECTION_SETTINGS));
                return;
            case ScreenId.NOTIFICATION_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.NOTIFICATION_OPTIONS));
                return;
            case ScreenId.THEME_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.THEME_OPTIONS));
                return;
            case ScreenId.VIEW_MODE:
                boolean isOnline4 = AppState.getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE);
                boolean isCustom = AppState.getBool(SettingsKeys.SETTING_CUSTOM_VIEW_MODE);
                AppState.setBool(UIKeys.FLAG_ONLINE_CUSTOM_OFF, isOnline4 && !isCustom);
                AppState.setBool(UIKeys.FLAG_ONLINE_CUSTOM_ON, isOnline4 && isCustom);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.VIEW_MODE));
                return;
            case ScreenId.COLOR_PICKER:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.COLOR_PICKER));
                return;
            case ScreenId.KEY_MAPPING:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.KEY_MAPPING));
                return;
            case ScreenId.FORM_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.FORM_SETTINGS));
                return;
            case ScreenId.EXT_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.EXT_SETTINGS));
                return;
            case ScreenId.MAP_VIEW_SETTINGS:
                MapController.showMapView();
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_VIEW_SETTINGS));
                return;
            case ScreenId.NEARBY_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.NEARBY_SETTINGS));
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
                AppState.setInt(SettingsKeys.INT_THEME_CACHE, AppState.getInt(SettingsKeys.SETTING_COLOR_THEME));
                ScreenManager.initializeFonts();
                AppState.getCanvas().updateFullScreenMode();
                TabBar.initialize();
                ResourceManager.resetClock();
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
                if (AppState.getBool(MapKeys.FLAG_MAP_DATA_LOADED)) {
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
                AppState.setBool(SettingsKeys.SETTING_STATUS_BAR_VISIBLE, AppState.getBool(UIKeys.FLAG_FULLSCREEN_REQUESTED));
                AppState.getCanvas().updateFullScreenMode();
                AppState.setInt(UIKeys.FLAG_FULLSCREEN_ACTIVE, 0);
                break;
            case ScreenId.THEME_SETTINGS:
                AppState.setInt(SettingsKeys.SETTING_COLOR_THEME, AppState.getInt(SettingsKeys.INT_THEME_CACHE));
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
            if (AppState.getString(StringResKeys.STR_MENU_SETTINGS).equals(item.title)) {
                AppState.setInt(SettingsKeys.SETTING_COLOR_THEME, ((Integer) themeData[0]).intValue());
            }
        } else if (screen.screenId == ScreenId.SOUND_SETTINGS) {
            ResourceManager.playAlertIfEnabled(((Integer) ((Object[]) item.data)[0]).intValue(), false);
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
        MapController.applyViewMode(true, false, !AppState.getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE));
        AppState.setInt(ContactKeys.FLAG_REFRESH_CONTACTS, 1);
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
        AppState.setInt(RuntimeKeys.INT_PERIOD_INDEX, optionId);
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
        if (AppState.getBool(SessionKeys.FLAG_UPDATE_AVAILABLE)) {
            return AppState.getInt(SessionKeys.INT_CONNECTION_STATE);
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
        if (optionId == 54) {
            ScreenBuilder.onScreenClosed();
            ResourceManager.composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (optionId == 68) {
            ScreenBuilder.onScreenClosed();
            AppController.toggleOnlineMode(true);
            return 0;
        }
        if (optionId != 37) {
            return 0;
        }
        ((MrimAccount) AppState.getAccount()).chatRoomManager.loaded = true;
        return 0;
    }

    public static int mapKeyToAction(int keyCode) {
        if (keyCode == 4) {
            ScreenManager.handleScreenClose();
            return 0;
        }
        if (keyCode != 137) {
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }
}
