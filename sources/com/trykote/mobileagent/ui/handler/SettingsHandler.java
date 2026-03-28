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
                boolean flag = AppState.getBool(StateKeys.MAP_GPS_ENABLED);
                boolean flag2 = AppState.getBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE);
                AppState.setBool(StateKeys.FLAG_GPS_NO_MAP, !flag && flag2);
                AppState.setBool(StateKeys.FLAG_GPS_WITH_MAP, flag && flag2);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.GPS_SETTINGS));
                return;
            case ScreenId.THEME_SETTINGS:
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, AppState.getInt(StateKeys.SETTING_COLOR_THEME));
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
                boolean isOnline4 = AppState.getBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE);
                boolean isCustom = AppState.getBool(StateKeys.SETTING_CUSTOM_VIEW_MODE);
                AppState.setBool(StateKeys.FLAG_ONLINE_CUSTOM_OFF, isOnline4 && !isCustom);
                AppState.setBool(StateKeys.FLAG_ONLINE_CUSTOM_ON, isOnline4 && isCustom);
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
                return AppController.handleSettingsOption(action);
            case ScreenId.GPS_SETTINGS:
                return AppController.handleProfileAction(action);
            case ScreenId.THEME_SETTINGS:
                ScreenManager.processScreenForm();
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, AppState.getInt(StateKeys.SETTING_COLOR_THEME));
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
                return AppController.handleConnectionOption(action);
            case ScreenId.NOTIFICATION_OPTIONS:
                return AppController.handleNotificationOption(action);
            case ScreenId.THEME_OPTIONS:
                return AppController.handleThemeOption(action);
            case ScreenId.VIEW_MODE:
                return AppController.getThemeBackground(action);
            case ScreenId.COLOR_PICKER:
                return AppController.getThemeColor(action);
            case ScreenId.KEY_MAPPING:
                return AppController.mapKeyToAction(action);
            case ScreenId.FORM_SETTINGS:
                return ScreenManager.processScreenForm();
            case ScreenId.EXT_SETTINGS:
                return AppController.handleExtSettingsOption(action);
            case ScreenId.MAP_VIEW_SETTINGS:
                return AppController.handleContactOption(action);
            case ScreenId.NEARBY_SETTINGS:
                ScreenManager.processScreenForm();
                if (AppState.getBool(StateKeys.FLAG_MAP_DATA_LOADED)) {
                    IOUtils.requestNearbyPeople();
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
                AppState.setBool(StateKeys.SETTING_STATUS_BAR_VISIBLE, AppState.getBool(StateKeys.FLAG_FULLSCREEN_REQUESTED));
                AppState.getCanvas().updateFullScreenMode();
                AppState.setInt(StateKeys.FLAG_FULLSCREEN_ACTIVE, 0);
                break;
            case ScreenId.THEME_SETTINGS:
                AppState.setInt(StateKeys.SETTING_COLOR_THEME, AppState.getInt(StateKeys.INT_SETTINGS_THEME));
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.SETTINGS:
                return 0;
            case ScreenId.SETTINGS_MENU:
                return AppController.handleSettingsOption(selectedOption);
            case ScreenId.GPS_SETTINGS:
                return AppController.handleProfileAction(selectedOption);
            case ScreenId.THEME_SETTINGS:
                return 0;
            case ScreenId.NOTIFICATION_SETTINGS:
                return 0;
            case ScreenId.SOUND_SETTINGS:
                return 0;
            case ScreenId.PRIVACY_SETTINGS:
                return 0;
            case ScreenId.CONNECTION_SETTINGS:
                return AppController.handleConnectionOption(selectedOption);
            case ScreenId.NOTIFICATION_OPTIONS:
                return AppController.handleNotificationOption(selectedOption);
            case ScreenId.THEME_OPTIONS:
                return AppController.handleThemeOption(selectedOption);
            case ScreenId.VIEW_MODE:
                return AppController.getThemeBackground(selectedOption);
            case ScreenId.COLOR_PICKER:
                return AppController.getThemeColor(selectedOption);
            case ScreenId.KEY_MAPPING:
                return AppController.mapKeyToAction(selectedOption);
            case ScreenId.FORM_SETTINGS:
                return 0;
            case ScreenId.EXT_SETTINGS:
                return AppController.handleExtSettingsOption(selectedOption);
            case ScreenId.MAP_VIEW_SETTINGS:
                return AppController.handleContactOption(selectedOption);
            case ScreenId.NEARBY_SETTINGS:
                return 0;
        }
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        return 0;
    }
}
