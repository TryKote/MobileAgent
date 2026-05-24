package com.trykote.mobileagent.net;

import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.util.RemoteLogger;

import java.util.Vector;

public final class RequestQueue {

    public static Vector sharedContactList;
    public static long lastUpdateTs;

    public static void flagSyncRequired() {
        synchronized (MapState.getTileQueue()) {
            UIState.setXmppRosterLoaded(true);
        }
    }

    public static boolean checkAndClearSync() {
        synchronized (MapState.getTileQueue()) {
            if (!UIState.isXmppRosterLoaded()) {
                return false;
            }
            synchronized (MapState.getTileQueue()) {
                UIState.setXmppRosterLoaded(false);
            }
            return true;
        }
    }

    public static Object[] getContactInfoFromState(int stateKey) {
        return addContactInfoToQueue(ApiClient.getUrlComponents(com.trykote.mobileagent.core.AppState.getString(stateKey)));
    }

    public static Object[] addContactInfoToQueue(Object[] contactInfo) {
        RemoteLogger.debug("XGRP", "addContactInfoToQueue");
        if (contactInfo != null) {
            Vector queue = MapState.getTileQueue();
            synchronized (queue) {
                if (!queue.contains(contactInfo)) {
                    queue.addElement(contactInfo);
                }
                flagSyncRequired();
            }
        }
        return contactInfo;
    }

    public static void removeContactInfoFromQueue(Object[] contactInfo) {
        if (contactInfo != null) {
            Vector queue = MapState.getTileQueue();
            synchronized (queue) {
                if (queue.contains(contactInfo)) {
                    queue.removeElement(contactInfo);
                    flagSyncRequired();
                }
            }
        }
    }

}
