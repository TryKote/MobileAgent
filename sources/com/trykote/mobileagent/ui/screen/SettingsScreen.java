package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.map.MapUtils;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.MailHelper;
import com.trykote.mobileagent.net.Telemetry;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.TabBar;
import com.trykote.mobileagent.ui.screen.ChatScreen;
import com.trykote.mobileagent.util.ObjectPool;

import java.util.Vector;

public final class SettingsScreen extends ScreenView {

    private static final int ACTION_COMPOSE_EMAIL = 54;
    private static final int ACTION_TOGGLE_ONLINE = 68;
    private static final int ACTION_MARK_LOADED = 37;
    private static final int KEY_ACTION_CLOSE = 4;
    private static final int KEY_ACTION_BACK_TWO = 137;

    public SettingsScreen(int screenId) {
        super(typeFor(screenId), screenId);
    }

    private static int typeFor(int screenId) {
        switch (screenId) {
            case ScreenId.SETTINGS:
                return ScreenManager.TYPE_FULLSCREEN_NOSCROLL;
            case ScreenId.EXT_SETTINGS:
                return ScreenManager.TYPE_DIALOG_CENTER;
            case ScreenId.GPS_SETTINGS:
            case ScreenId.NOTIFICATION_OPTIONS:
            case ScreenId.THEME_OPTIONS:
                return ScreenManager.TYPE_DIALOG_BOTTOM;
            case ScreenId.SETTINGS_MENU:
            case ScreenId.CONNECTION_SETTINGS:
            case ScreenId.VIEW_MODE:
            case ScreenId.COLOR_PICKER:
            case ScreenId.KEY_MAPPING:
            case ScreenId.MAP_VIEW_SETTINGS:
                return ScreenManager.TYPE_POPUP;
            default:
                return ScreenManager.TYPE_FULLSCREEN;
        }
    }

    public void showSelf() {
        if (screenId == ScreenId.SETTINGS) {
            SessionState.setInitComplete(0);
            UIState.setFullscreenActive(1);
            ScreenManager.pushScreen(this);
        } else {
            ScreenManager.showScreen(this);
        }
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.SETTINGS:
                configureHeader(156, 459216);
                addLabelSeparator(1016);
                configureSoftKeys(1017, 157, 0, 157, 157);
                break;
            case ScreenId.SETTINGS_MENU:
                configureHeader(28, 1038);
                showCheckboxes = true;
                addActionById(28, 745, 0);
                addActionById(28, 746, 1);
                addActionById(28, 747, 2);
                addActionById(28, 748, 3);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.GPS_SETTINGS:
                MapState.setGpsNoMap(!MapState.isGpsEnabled() && ContactState.isListActive());
                MapState.setGpsWithMap(MapState.isGpsEnabled() && ContactState.isListActive());
                configureHeader(0, 1038);
                addActionById(361, 340, 0);
                addActionById(303, 339, 100);
                addConditionalIf(276, 1, 365, 348);
                addConditionalIf(277, 2, 6, 347);
                addConditionalIf(1422, 3, 310, 341);
                addConditionalIf(1423, 4, 310, 342);
                addActionById(230, 345, 174);
                addActionById(313, 343, 91);
                addConditionalIf(277, 119, 229, 346);
                addActionById(12, 500, 131);
                addActionById(10, 1049, 5);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.THEME_SETTINGS:
                SettingsState.setThemeCache(SettingsState.getColorTheme());
                configureHeader(12, 538);
                addCheckbox(561, 71);
                addCheckbox(562, 65);
                addCheckbox(563, 67);
                addCheckbox(564, 68);
                addDropdown(565, 566, 73);
                addDropdown(560, 567, 72);
                addCheckbox(568, 70);
                addCheckbox(572, 66);
                addDropdown(569, 570, 74);
                addDropdown(573, 574, 246);
                addCheckbox(571, 69);
                configureSoftKeys(1053, 4, 1050, 12, 0);
                break;
            case ScreenId.NOTIFICATION_SETTINGS:
                configureHeader(14, 540);
                addDropdown(629, 641, 205);
                addDropdown(630, 641, 206);
                addDropdown(631, 641, 207);
                addDropdown(632, 641, 208);
                addDropdown(633, 641, 209);
                addDropdown(634, 641, 210);
                addDropdown(635, 641, 211);
                addDropdown(636, 641, 212);
                addDropdown(637, 641, 213);
                addDropdown(638, 641, 214);
                addDropdown(639, 641, 215);
                addDropdown(640, 641, 216);
                configureSoftKeys(1053, 12, 1050, 12, 0);
                break;
            case ScreenId.SOUND_SETTINGS:
                configureHeader(13, 539);
                addDropdown(581, 591, 75);
                addCheckbox(589, 76);
                addDropdown(582, 591, 77);
                addCheckbox(589, 78);
                addDropdown(583, 591, 79);
                addCheckbox(589, 80);
                addDropdown(584, 591, 81);
                addCheckbox(589, 82);
                addDropdown(585, 591, 83);
                addCheckbox(589, 84);
                addDropdown(587, 591, 240);
                addCheckbox(589, 241);
                addDropdown(586, 591, 85);
                addCheckbox(589, 86);
                addCheckbox(588, 87);
                addNumericInput(590, 3, 1262, 88, 0, 100, 50);
                addCheckbox(592, 89);
                configureSoftKeys(1053, 12, 1050, 12, 0);
                break;
            case ScreenId.PRIVACY_SETTINGS:
                configureHeader(32, 543);
                addTextInput(1038, 255, 424, 0, 48);
                addTextInput(1038, 255, 424, 0, 49);
                addTextInput(1038, 255, 424, 0, 50);
                addTextInput(1038, 255, 424, 0, 51);
                addTextInput(1038, 255, 424, 0, 52);
                addTextInput(1038, 255, 424, 0, 53);
                addTextInput(1038, 255, 424, 0, 54);
                addTextInput(1038, 255, 424, 0, 55);
                addTextInput(1038, 255, 424, 0, 56);
                addTextInput(1038, 255, 424, 0, 57);
                addTextInput(1038, 255, 424, 0, 58);
                addTextInput(1038, 255, 424, 0, 59);
                addTextInput(1038, 255, 424, 0, 60);
                addTextInput(1038, 255, 424, 0, 61);
                addTextInput(1038, 255, 424, 0, 62);
                configureSoftKeys(1053, 12, 1050, 12, 0);
                break;
            case ScreenId.CONNECTION_SETTINGS:
                configureHeader(0, 1038);
                showCheckboxes = true;
                addActionById(314, 1034, 0);
                addActionById(314, 1035, 1);
                addActionById(314, 719, 2);
                configureSoftKeys(1048, 4, 1050, 12, 4);
                break;
            case ScreenId.NOTIFICATION_OPTIONS:
                configureHeader(0, 1038);
                addActionById(243, 851, 37);
                addActionById(238, 846, 54);
                addActionById(227, 1061, 68);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.THEME_OPTIONS:
                configureHeader(156, 1038);
                addActionById(8, 500, 56);
                addActionById(10, 1049, 10);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.VIEW_MODE:
                UIState.setOnlineCustomOff(ContactState.isListActive() && SettingsState.getCustomViewMode() == 0);
                UIState.setOnlineCustomOn(ContactState.isListActive() && SettingsState.getCustomViewMode() != 0);
                configureHeader(0, 1038);
                showCheckboxes = true;
                addActionById(311, 334, 0);
                addActionById(312, 335, 1);
                addActionById(363, 344, 120);
                addConditionalIf(1418, 2, 310, 336);
                addConditionalIf(1419, 3, 310, 337);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.COLOR_PICKER:
                configureHeader(0, 1038);
                showCheckboxes = true;
                addActionById(156, 642, 0);
                addActionById(159, 643, 1);
                addActionById(157, 644, 2);
                addActionById(160, 645, 3);
                addActionById(158, 646, 4);
                addActionById(155, 647, 5);
                configureSoftKeys(1048, 4, 1050, 12, 4);
                break;
            case ScreenId.KEY_MAPPING:
                configureHeader(0, 1038);
                showCheckboxes = true;
                addActionById(28, 497, 8);
                addActionById(5, 502, 137);
                addConditionalIf(1543, 4, 15, 501);
                addActionById(9, 546, 9);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.FORM_SETTINGS:
                configureHeader(236, 1009);
                addCheckbox(1010, 268);
                addCheckbox(1011, 269);
                addCheckbox(1012, 270);
                addDropdown(1013, 1015, 271);
                addCheckbox(1014, 272);
                configureSoftKeys(1053, 12, 1050, 12, 0);
                break;
            case ScreenId.EXT_SETTINGS:
                configureHeader(303, 1038);
                showCheckboxes = true;
                addActionById(364, 880, 0);
                addActionById(365, 884, 1);
                addActionById(366, 882, 2);
                addActionById(367, 881, 3);
                addActionById(369, 656, 4);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.MAP_VIEW_SETTINGS:
                MapController.showMapView();
                configureHeader(0, 1038);
                showCheckboxes = true;
                addActionById(361, 666, 6);
                addActionById(308, 668, 162);
                addActionById(369, 665, 151);
                addActionById(8, 500, 129);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.NEARBY_SETTINGS:
                configureHeader(12, 369);
                addCheckbox(417, 278);
                addCheckbox(418, 279);
                addCheckbox(548, 280);
                configureSoftKeys(1053, 6, 1050, 12, 6);
                break;
        }
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.THEME_SETTINGS:
                ScreenManager.processScreenForm();
                SettingsState.setThemeCache(SettingsState.getColorTheme());
                ScreenManager.initializeFonts();
                AppState.getCanvas().updateFullScreenMode();
                TabBar.initialize();
                AppController.resetClock();
                return 0;
            case ScreenId.NOTIFICATION_SETTINGS:
            case ScreenId.SOUND_SETTINGS:
            case ScreenId.PRIVACY_SETTINGS:
            case ScreenId.FORM_SETTINGS:
                return ScreenManager.processScreenForm();
            case ScreenId.NEARBY_SETTINGS:
                ScreenManager.processScreenForm();
                if (MapState.isMapDataLoaded()) {
                    MapUtils.requestNearbyPeople();
                }
                return 0;
            default:
                return handleAction(action);
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        return handleAction(selectedOption);
    }

    public int onAction(MenuItem item, Object data) {
        if (screenId == ScreenId.THEME_SETTINGS) {
            AppController.needsLayoutUpdate = true;
        }
        return 0;
    }

    public void onClosed() {
        switch (screenId) {
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

    public void onMenuItemChanged(MenuItem item) {
        if (screenId == ScreenId.THEME_SETTINGS) {
            Object[] themeData = (Object[]) item.data;
            if (StringPool.get(StringResKeys.STR_MENU_SETTINGS).equals(item.title)) {
                SettingsState.setColorTheme(((Integer) themeData[0]).intValue());
            }
        } else if (screenId == ScreenId.SOUND_SETTINGS) {
            NotificationHelper.playAlertIfEnabled(((Integer) ((Object[]) item.data)[0]).intValue(), false);
        }
    }

    // --- Action dispatch ---

    private int handleAction(int action) {
        switch (screenId) {
            case ScreenId.SETTINGS_MENU:
                return handleSettingsOption(action);
            case ScreenId.GPS_SETTINGS:
                return handleProfileAction(action);
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
            case ScreenId.EXT_SETTINGS:
                return handleExtSettingsOption(action);
            case ScreenId.MAP_VIEW_SETTINGS:
                return handleContactOption(action);
            default:
                return 0;
        }
    }

    // --- Private helpers ---

    private static int handleSettingsOption(int optionId) {
        RuntimeState.setPeriodIndex(optionId);
        return ScreenId.TRAFFIC_STATS;
    }

    private static int handleProfileAction(int actionId) {
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

    private static int handleExtSettingsOption(int actionId) {
        Account account = AppState.getAccount();
        switch (actionId) {
            case 0: case 1: case 2: case 3:
                if (account != null) {
                    applyProfileAction(account, actionId);
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
            applyProfileAction(AccountManager.getMrimAccount(accounts, i), actionId);
        }
        ObjectPool.releaseVector(accounts);
    }

    private static void applyProfileAction(Account account, int actionId) {
        switch (actionId) {
            case 0: account.publishLocation(); break;
            case 1: account.hideLocation(); break;
            case 2: account.setLocationGroups(); break;
            case 3: account.clearLocationGroups(); break;
        }
    }

    private static int handleNotificationOption(int optionId) {
        if (optionId == ACTION_COMPOSE_EMAIL) {
            ScreenBuilder.onScreenClosed();
            MailHelper.composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (optionId == ACTION_TOGGLE_ONLINE) {
            ScreenBuilder.onScreenClosed();
            ChatScreen.toggleOnlineMode(true);
            return 0;
        }
        if (optionId != ACTION_MARK_LOADED) {
            return 0;
        }
        AppState.getAccount().setChatRoomsLoaded();
        return 0;
    }

    private static int handleContactOption(int optionId) {
        if (optionId != 6) {
            return 0;
        }
        Account account = AppState.getAccount();
        account.setMapHighlighted(true);
        if (!account.isProfileSelected()) {
            return NotificationHelper.showError(667);
        }
        MapController.applyViewMode(true, false, !MapState.isMapViewActive());
        ContactState.setRefreshNeeded(true);
        MapController.selectMapItem(account.asListItem());
        return 0;
    }

    private static int mapKeyToAction(int keyCode) {
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
