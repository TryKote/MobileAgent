package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.SessionState;
/* Extracted from AppController: network synchronization subsystem */
public final class NetworkLock {

    public static final Object[] createSyncState() {
        return SessionState.getCallbackArray();
    }

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

    public static final void releaseNetworkLock() {
        Object[] syncState = createSyncState();
        synchronized (syncState) {
            syncState[0] = null;
        }
    }

    public static final boolean isNetworkBusy() {
        return createSyncState()[0] != null;
    }
}
