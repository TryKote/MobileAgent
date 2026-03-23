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
        return 112;
    }

    /* renamed from: e */
    public static final void showNotification(String str) {
        AppState.setInt(StateKeys.INT_NOTIFICATION_SCREEN_ID, 112);
        AppState.setObject(StateKeys.SLOT_NOTIFICATION_TITLE, (Object) str);
        clearNotifications();
    }

    /* renamed from: m */
    public static final void showMessageById(int i) {
        AppState.setInt(StateKeys.INT_NOTIFICATION_SCREEN_ID, 112);
        AppState.setFromPool(StateKeys.SLOT_NOTIFICATION_TITLE, i);
        clearNotifications();
    }

    /* renamed from: r */
    public static final void clearNotifications() {
        ResourceManager.playNotificationSound(5);
        ScreenManager.showScreen(ScreenManager.createScreen(3328));
        AppState.clearIndex(StateKeys.SLOT_NOTIFICATION_TITLE);
    }
}
