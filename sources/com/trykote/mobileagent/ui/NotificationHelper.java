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
/* Extracted from AppController: notification and error display */
public final class NotificationHelper {

    /* renamed from: l */
    public static final int showError(int i) {
        if (ScreenManager.getCurrentScreen().screenType == 8) {
            ScreenBuilder.onScreenClosed();
        }
        AppState.setFromPool(1294, i);
        return 112;
    }

    /* renamed from: e */
    public static final void showNotification(String str) {
        AppState.setInt(3329, 112);
        AppState.setObject(1294, (Object) str);
        clearNotifications();
    }

    /* renamed from: m */
    public static final void showMessageById(int i) {
        AppState.setInt(3329, 112);
        AppState.setFromPool(1294, i);
        clearNotifications();
    }

    /* renamed from: r */
    public static final void clearNotifications() {
        ResourceManager.playNotificationSound(5);
        ScreenManager.showScreen(ScreenManager.createScreen(3328));
        AppState.clearIndex(1294);
    }
}
