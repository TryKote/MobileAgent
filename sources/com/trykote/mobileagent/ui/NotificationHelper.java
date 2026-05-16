package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.MapKeys;
import com.trykote.mobileagent.key.SettingsKeys;
import com.trykote.mobileagent.util.SoundPlayer;
import com.trykote.mobileagent.util.TimerManager;

import javax.microedition.lcdui.Display;
/* Extracted from AppController: notification and error display */
public final class NotificationHelper {

    private static final int SCREEN_TYPE_TOAST_CENTER = 8;

    // Sound type codes for playNotificationSound()
    public static final int SOUND_NEW_MAIL = 0;
    public static final int SOUND_CONTACT_ONLINE = 1;
    public static final int SOUND_MESSAGE_RECEIVED = 2;
    public static final int SOUND_CONVERSATION_MESSAGE = 3;
    public static final int SOUND_MESSAGE_SENT = 4;
    public static final int SOUND_SYSTEM_NOTIFICATION = 5;
    public static final int SOUND_CUSTOM_NOTE = 6;

    // Sound config offsets within SOUND_CONFIG_BASE block
    private static final int SOUND_INDEX_CONTACT_ONLINE = 2;
    private static final int SOUND_INDEX_NEW_MAIL = 4;
    private static final int SOUND_INDEX_CONVERSATION = 6;
    private static final int SOUND_INDEX_MESSAGE_SENT = 8;
    private static final int SOUND_INDEX_SYSTEM = 10;
    private static final int SOUND_INDEX_CUSTOM_NOTE = 165;

    private static final int VIBRATION_DURATION_MS = 250;
    private static final long NOTIFICATION_COOLDOWN_MS = 1000L;

    public static final int showError(int i) {
        if (ScreenManager.getCurrentScreen().screenType == SCREEN_TYPE_TOAST_CENTER) {
            ScreenBuilder.onScreenClosed();
        }
        UIState.setNotificationTitleFromPool(i);
        return ScreenId.CLEAR_NOTIFICATIONS;
    }

    public static final void showNotification(String str) {
        UIState.setNotificationScreenId(ScreenId.CLEAR_NOTIFICATIONS);
        UIState.setNotificationTitle((Object) str);
        clearNotifications();
    }

    public static final void showMessageById(int i) {
        UIState.setNotificationScreenId(ScreenId.CLEAR_NOTIFICATIONS);
        UIState.setNotificationTitleFromPool(i);
        clearNotifications();
    }

    public static final void clearNotifications() {
        playNotificationSound(SOUND_SYSTEM_NOTIFICATION);
        Screens.notificationDialog().show();
        UIState.clearNotificationTitle();
    }

    public static final void showAlertBuffer(int i, StringBuffer stringBuffer) {
        UIState.setHttpResultScreen(i);
        AppState.setFromBuffer(MapKeys.SLOT_MAP_POINT_1, stringBuffer);
        Screens.errorAlert().show();
        MapState.setMapPoint1(null);
    }

    public static final void showAlertById(int i, int i2) {
        UIState.setHttpResultScreen(i);
        AppState.setFromPool(MapKeys.SLOT_MAP_POINT_1, i2);
        Screens.errorAlert().show();
        MapState.setMapPoint1(null);
    }

    public static final void showErrorOrConfirm(int i, int i2, int i3) {
        if (i3 != 0) {
            showMessageById(i3);
        } else {
            showConfirmDialog(i, i2);
        }
    }

    public static final void showConfirmDialog(int i, int i2) {
        UIState.setHttpParam1(i);
        UIState.setHttpParam2(i2);
        Screens.confirmDialog().show();
    }

    public static void playNotificationSound(int soundType) {
        int soundIndex = 0;
        if (soundType == SOUND_CONTACT_ONLINE) {
            soundIndex = SOUND_INDEX_CONTACT_ONLINE;
        } else if (soundType == SOUND_NEW_MAIL) {
            soundIndex = SOUND_INDEX_NEW_MAIL;
        } else if (soundType == SOUND_CONVERSATION_MESSAGE) {
            soundIndex = SOUND_INDEX_CONVERSATION;
        } else if (soundType == SOUND_MESSAGE_SENT) {
            soundIndex = SOUND_INDEX_MESSAGE_SENT;
        } else if (soundType == SOUND_SYSTEM_NOTIFICATION) {
            soundIndex = SOUND_INDEX_SYSTEM;
        } else if (soundType == SOUND_CUSTOM_NOTE) {
            soundIndex = SOUND_INDEX_CUSTOM_NOTE;
        }
        playAlertIfEnabled(AppState.getInt(SettingsKeys.SOUND_CONFIG_BASE + soundIndex), AppState.getBool(SettingsKeys.SOUND_CONFIG_BASE + soundIndex + 1));
    }

    public static void playAlertIfEnabled(int alertTone, boolean vibrateEnabled) {
        if (SessionState.isMrimDataLoaded()) {
            if (vibrateEnabled) {
                Display.getDisplay(AppState.getMidlet()).vibrate(VIBRATION_DURATION_MS);
            }
            if (alertTone == 0 || SettingsState.isNotificationEnabled() || !TimerManager.checkTimer(TimerManager.SLOT_NOTIFICATION_COOLDOWN, NOTIFICATION_COOLDOWN_MS)) {
                return;
            }
            SoundPlayer.playSound(alertTone);
        }
    }
}
