package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import javax.microedition.lcdui.Display;
/* Extracted from AppController: notification and error display */
public final class NotificationHelper {

    /* renamed from: l */
    public static final int showError(int i) {
        if (ScreenManager.getCurrentScreen().screenType == 8) {
            ScreenBuilder.onScreenClosed();
        }
        AppState.setFromPool(UIKeys.SLOT_NOTIFICATION_TITLE, i);
        return ScreenId.CLEAR_NOTIFICATIONS;
    }

    /* renamed from: e */
    public static final void showNotification(String str) {
        AppState.setInt(UIKeys.INT_NOTIFICATION_SCREEN_ID, ScreenId.CLEAR_NOTIFICATIONS);
        AppState.setObject(UIKeys.SLOT_NOTIFICATION_TITLE, (Object) str);
        clearNotifications();
    }

    /* renamed from: m */
    public static final void showMessageById(int i) {
        AppState.setInt(UIKeys.INT_NOTIFICATION_SCREEN_ID, ScreenId.CLEAR_NOTIFICATIONS);
        AppState.setFromPool(UIKeys.SLOT_NOTIFICATION_TITLE, i);
        clearNotifications();
    }

    /* renamed from: r */
    public static final void clearNotifications() {
        playNotificationSound(5);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.NOTIFICATION_DIALOG));
        AppState.clearIndex(UIKeys.SLOT_NOTIFICATION_TITLE);
    }

    /* renamed from: a */
    public static final void showAlertBuffer(int i, StringBuffer stringBuffer) {
        AppState.setInt(UIKeys.INT_HTTP_RESULT_SCREEN, i);
        AppState.setFromBuffer(MapKeys.SLOT_MAP_POINT_1, stringBuffer);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ERROR_ALERT));
        AppState.clearIndex(MapKeys.SLOT_MAP_POINT_1);
    }

    /* renamed from: a */
    public static final void showAlertById(int i, int i2) {
        AppState.setInt(UIKeys.INT_HTTP_RESULT_SCREEN, i);
        AppState.setFromPool(MapKeys.SLOT_MAP_POINT_1, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ERROR_ALERT));
        AppState.clearIndex(MapKeys.SLOT_MAP_POINT_1);
    }

    /* renamed from: a */
    public static final void showErrorOrConfirm(int i, int i2, int i3) {
        if (i3 != 0) {
            showMessageById(i3);
        } else {
            showConfirmDialog(i, i2);
        }
    }

    /* renamed from: b */
    public static final void showConfirmDialog(int i, int i2) {
        AppState.setInt(UIKeys.INT_HTTP_PARAM_1, i);
        AppState.setInt(UIKeys.INT_HTTP_PARAM_2, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONFIRM_DIALOG));
    }

    public static void playNotificationSound(int soundType) {
        int soundIndex = 0;
        if (soundType == 1) {
            soundIndex = 2;
        } else if (soundType == 0) {
            soundIndex = 4;
        } else if (soundType == 3) {
            soundIndex = 6;
        } else if (soundType == 4) {
            soundIndex = 8;
        } else if (soundType == 5) {
            soundIndex = 10;
        } else if (soundType == 6) {
            soundIndex = 165;
        }
        playAlertIfEnabled(AppState.getInt(soundIndex + 75), AppState.getBool(soundIndex + 76));
    }

    public static void playAlertIfEnabled(int alertTone, boolean vibrateEnabled) {
        if (AppState.getBool(SessionKeys.FLAG_MRIM_DATA_LOADED)) {
            if (vibrateEnabled) {
                Display.getDisplay(AppState.getMidlet()).vibrate(250);
            }
            if (alertTone == 0 || AppState.getBool(SettingsKeys.SETTING_NOTIFICATION_ENABLED) || !TimerManager.checkTimer(8, 1000L)) {
                return;
            }
            SoundPlayer.playSound(alertTone);
        }
    }
}
