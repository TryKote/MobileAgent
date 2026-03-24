package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
/* Extracted from AppController: notification and error display */
public final class NotificationHelper {

    /* renamed from: l */
    public static final int showError(int i) {
        if (ScreenManager.getCurrentScreen().screenType == 8) {
            ScreenBuilder.onScreenClosed();
        }
        AppState.setFromPool(StateKeys.SLOT_NOTIFICATION_TITLE, i);
        return ScreenId.CLEAR_NOTIFICATIONS;
    }

    /* renamed from: e */
    public static final void showNotification(String str) {
        AppState.setInt(StateKeys.INT_NOTIFICATION_SCREEN_ID, ScreenId.CLEAR_NOTIFICATIONS);
        AppState.setObject(StateKeys.SLOT_NOTIFICATION_TITLE, (Object) str);
        clearNotifications();
    }

    /* renamed from: m */
    public static final void showMessageById(int i) {
        AppState.setInt(StateKeys.INT_NOTIFICATION_SCREEN_ID, ScreenId.CLEAR_NOTIFICATIONS);
        AppState.setFromPool(StateKeys.SLOT_NOTIFICATION_TITLE, i);
        clearNotifications();
    }

    /* renamed from: r */
    public static final void clearNotifications() {
        ResourceManager.playNotificationSound(5);
        ScreenManager.showScreen(ScreenManager.createScreen(3328));
        AppState.clearIndex(StateKeys.SLOT_NOTIFICATION_TITLE);
    }

    /* renamed from: a */
    public static final void showAlertBuffer(int i, StringBuffer stringBuffer) {
        AppState.setInt(StateKeys.INT_HTTP_RESULT_SCREEN, i);
        AppState.setFromBuffer(StateKeys.SLOT_MAP_POINT_1, stringBuffer);
        ScreenManager.showScreen(ScreenManager.createScreen(4485));
        AppState.clearIndex(StateKeys.SLOT_MAP_POINT_1);
    }

    /* renamed from: a */
    public static final void showAlertById(int i, int i2) {
        AppState.setInt(StateKeys.INT_HTTP_RESULT_SCREEN, i);
        AppState.setFromPool(StateKeys.SLOT_MAP_POINT_1, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(4485));
        AppState.clearIndex(StateKeys.SLOT_MAP_POINT_1);
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
        AppState.setInt(StateKeys.INT_HTTP_PARAM_1, i);
        AppState.setInt(StateKeys.INT_HTTP_PARAM_2, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(4497));
    }
}
