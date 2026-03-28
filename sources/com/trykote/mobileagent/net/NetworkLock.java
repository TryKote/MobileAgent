package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.util.*;
/* Extracted from AppController: network synchronization subsystem */
public final class NetworkLock {

    /* renamed from: ae */
    public static final Object[] createSyncState() {
        return (Object[]) AppState.pool[SessionKeys.OBJ_CALLBACK_ARRAY];
    }

    /* renamed from: s */
    public static final void acquireNetworkLock() {
        Object[] syncState = createSyncState();
        while (true) {
            synchronized (syncState) {
                if (syncState[0] == null) {
                    syncState[0] = Thread.currentThread();
                    return;
                }
            }
            try {
                Thread.sleep(100);
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: t */
    public static final void releaseNetworkLock() {
        Object[] syncState = createSyncState();
        synchronized (syncState) {
            syncState[0] = null;
        }
    }

    /* renamed from: u */
    public static final boolean isNetworkBusy() {
        return createSyncState()[0] != null;
    }
}
