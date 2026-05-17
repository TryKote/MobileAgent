package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.PackedStringKeys;
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
                configureHeader(156, AppState.getString(PackedStringKeys.LABEL_MAIL_RU));
                addLabelSeparator(AppState.getString(1016));
                configureSoftKeys(AppState.getString(1017), 157, null, 157, 157);
                break;
            case ScreenId.SETTINGS_MENU:
                configureHeader(28, AppState.getString(1038));
                showCheckboxes = true;
                addAction(28, AppState.getString(745), 0);
                addAction(28, AppState.getString(746), 1);
                addAction(28, AppState.getString(747), 2);
                addAction(28, AppState.getString(748), 3);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.GPS_SETTINGS:
                MapState.setGpsNoMap(!MapState.isGpsEnabled() && ContactState.isListActive());
                MapState.setGpsWithMap(MapState.isGpsEnabled() && ContactState.isListActive());
                configureHeader(0, AppState.getString(1038));
                addAction(361, AppState.getString(340), 0);
                addAction(303, AppState.getString(339), 100);
                addConditionalIf(276, AppState.getString(348), 365, 1);
                addConditionalIf(277, AppState.getString(347), 6, 2);
                addConditionalIf(1422, AppState.getString(341), 310, 3);
                addConditionalIf(1423, AppState.getString(342), 310, 4);
                addAction(230, AppState.getString(345), 174);
                addAction(313, AppState.getString(343), 91);
                addConditionalIf(277, AppState.getString(346), 229, 119);
                addAction(12, AppState.getString(500), 131);
                addAction(10, AppState.getString(1049), 5);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.THEME_SETTINGS:
                SettingsState.setThemeCache(SettingsState.getColorTheme());
                configureHeader(12, AppState.getString(538));
                addCheckbox(AppState.getString(561), 71);
                addCheckbox(AppState.getString(562), 65);
                addCheckbox(AppState.getString(563), 67);
                addCheckbox(AppState.getString(564), 68);
                addDropdown(AppState.getString(565), 566, 73);
                addDropdown(AppState.getString(560), 567, 72);
                addCheckbox(AppState.getString(568), 70);
                addCheckbox(AppState.getString(572), 66);
                addDropdown(AppState.getString(569), 570, 74);
                addDropdown(AppState.getString(573), 574, 246);
                addCheckbox(AppState.getString(571), 69);
                configureSoftKeys(AppState.getString(1053), 4, AppState.getString(1050), 12, 0);
                break;
            case ScreenId.NOTIFICATION_SETTINGS:
                configureHeader(14, AppState.getString(540));
                addDropdown(AppState.getString(629), 641, 205);
                addDropdown(AppState.getString(630), 641, 206);
                addDropdown(AppState.getString(631), 641, 207);
                addDropdown(AppState.getString(632), 641, 208);
                addDropdown(AppState.getString(633), 641, 209);
                addDropdown(AppState.getString(634), 641, 210);
                addDropdown(AppState.getString(635), 641, 211);
                addDropdown(AppState.getString(636), 641, 212);
                addDropdown(AppState.getString(637), 641, 213);
                addDropdown(AppState.getString(638), 641, 214);
                addDropdown(AppState.getString(639), 641, 215);
                addDropdown(AppState.getString(640), 641, 216);
                configureSoftKeys(AppState.getString(1053), 12, AppState.getString(1050), 12, 0);
                break;
            case ScreenId.SOUND_SETTINGS:
                configureHeader(13, AppState.getString(539));
                addDropdown(AppState.getString(581), 591, 75);
                addCheckbox(AppState.getString(589), 76);
                addDropdown(AppState.getString(582), 591, 77);
                addCheckbox(AppState.getString(589), 78);
                addDropdown(AppState.getString(583), 591, 79);
                addCheckbox(AppState.getString(589), 80);
                addDropdown(AppState.getString(584), 591, 81);
                addCheckbox(AppState.getString(589), 82);
                addDropdown(AppState.getString(585), 591, 83);
                addCheckbox(AppState.getString(589), 84);
                addDropdown(AppState.getString(587), 591, 240);
                addCheckbox(AppState.getString(589), 241);
                addDropdown(AppState.getString(586), 591, 85);
                addCheckbox(AppState.getString(589), 86);
                addCheckbox(AppState.getString(588), 87);
                addNumericInput(AppState.getString(590), 3, AppState.getString(1262), 88, 0, 100, 50);
                addCheckbox(AppState.getString(592), 89);
                configureSoftKeys(AppState.getString(1053), 12, AppState.getString(1050), 12, 0);
                break;
            case ScreenId.PRIVACY_SETTINGS:
                configureHeader(32, AppState.getString(543));
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 48);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 49);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 50);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 51);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 52);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 53);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 54);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 55);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 56);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 57);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 58);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 59);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 60);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 61);
                addTextInput(AppState.getString(1038), 255, AppState.getString(424), 0, 62);
                configureSoftKeys(AppState.getString(1053), 12, AppState.getString(1050), 12, 0);
                break;
            case ScreenId.CONNECTION_SETTINGS:
                configureHeader(0, AppState.getString(1038));
                showCheckboxes = true;
                addAction(314, AppState.getString(1034), 0);
                addAction(314, AppState.getString(1035), 1);
                addAction(314, AppState.getString(719), 2);
                configureSoftKeys(AppState.getString(1048), 4, AppState.getString(1050), 12, 4);
                break;
            case ScreenId.NOTIFICATION_OPTIONS:
                configureHeader(0, AppState.getString(1038));
                addAction(243, AppState.getString(851), 37);
                addAction(238, AppState.getString(846), 54);
                addAction(227, AppState.getString(1061), 68);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.THEME_OPTIONS:
                configureHeader(156, AppState.getString(1038));
                addAction(8, AppState.getString(500), 56);
                addAction(10, AppState.getString(1049), 10);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.VIEW_MODE:
                UIState.setOnlineCustomOff(ContactState.isListActive() && SettingsState.getCustomViewMode() == 0);
                UIState.setOnlineCustomOn(ContactState.isListActive() && SettingsState.getCustomViewMode() != 0);
                configureHeader(0, AppState.getString(1038));
                showCheckboxes = true;
                addAction(311, AppState.getString(334), 0);
                addAction(312, AppState.getString(335), 1);
                addAction(363, AppState.getString(344), 120);
                addConditionalIf(1418, AppState.getString(336), 310, 2);
                addConditionalIf(1419, AppState.getString(337), 310, 3);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.COLOR_PICKER:
                configureHeader(0, AppState.getString(1038));
                showCheckboxes = true;
                addAction(156, AppState.getString(642), 0);
                addAction(159, AppState.getString(643), 1);
                addAction(157, AppState.getString(644), 2);
                addAction(160, AppState.getString(645), 3);
                addAction(158, AppState.getString(646), 4);
                addAction(155, AppState.getString(647), 5);
                configureSoftKeys(AppState.getString(1048), 4, AppState.getString(1050), 12, 4);
                break;
            case ScreenId.KEY_MAPPING:
                configureHeader(0, AppState.getString(1038));
                showCheckboxes = true;
                addAction(28, AppState.getString(497), 8);
                addAction(5, AppState.getString(502), 137);
                addConditionalIf(1543, AppState.getString(501), 15, 4);
                addAction(9, AppState.getString(546), 9);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.FORM_SETTINGS:
                configureHeader(236, AppState.getString(1009));
                addCheckbox(AppState.getString(1010), 268);
                addCheckbox(AppState.getString(1011), 269);
                addCheckbox(AppState.getString(1012), 270);
                addDropdown(AppState.getString(1013), 1015, 271);
                addCheckbox(AppState.getString(1014), 272);
                configureSoftKeys(AppState.getString(1053), 12, AppState.getString(1050), 12, 0);
                break;
            case ScreenId.EXT_SETTINGS:
                configureHeader(303, AppState.getString(1038));
                showCheckboxes = true;
                addAction(364, AppState.getString(880), 0);
                addAction(365, AppState.getString(884), 1);
                addAction(366, AppState.getString(882), 2);
                addAction(367, AppState.getString(881), 3);
                addAction(369, AppState.getString(656), 4);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.MAP_VIEW_SETTINGS:
                MapController.showMapView();
                configureHeader(0, AppState.getString(1038));
                showCheckboxes = true;
                addAction(361, AppState.getString(666), 6);
                addAction(308, AppState.getString(668), 162);
                addAction(369, AppState.getString(665), 151);
                addAction(8, AppState.getString(500), 129);
                configureSoftKeys(AppState.getString(1048), 199, AppState.getString(1050), 12, 199);
                break;
            case ScreenId.NEARBY_SETTINGS:
                configureHeader(12, AppState.getString(369));
                addCheckbox(AppState.getString(417), 278);
                addCheckbox(AppState.getString(418), 279);
                addCheckbox(AppState.getString(548), 280);
                configureSoftKeys(AppState.getString(1053), 6, AppState.getString(1050), 12, 6);
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
