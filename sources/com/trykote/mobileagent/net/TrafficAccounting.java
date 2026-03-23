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
/* Extracted from AppController: traffic accounting subsystem */
public final class TrafficAccounting {

    /* renamed from: af */
    public static final int initStartupState() {
        int currentDate = AppState.getDateCode();
        int savedDate = AppState.getInt(1);
        if (currentDate != savedDate) {
            for (int i = 0; i < 4; i++) {
                int offset = i << 3;
                AppState.setInt(offset + 4, 0);
                AppState.setInt(offset + 5, 0);
                if ((currentDate >>> 8) != (savedDate >>> 8)) {
                    AppState.setInt(offset + 6, 0);
                    AppState.setInt(offset + 7, 0);
                }
            }
            AppState.setInt(1, currentDate);
        }
        return currentDate;
    }

    /* renamed from: C */
    public static final void addSentBytes(int i) {
        initStartupState();
        AppState.addInt(10, i);
        AppState.addInt(12, i);
        AppState.addInt(14, i);
        AppState.addInt(16, i);
        AppState.addInt(293, i);
    }

    /* renamed from: D */
    public static final void addReceivedBytes(int i) {
        initStartupState();
        AppState.addInt(11, i);
        AppState.addInt(13, i);
        AppState.addInt(15, i);
        AppState.addInt(17, i);
        AppState.addInt(294, i);
    }

    /* renamed from: E */
    public static final void addDownloadBytes(int i) {
        initStartupState();
        AppState.addInt(18, i);
        AppState.addInt(20, i);
        AppState.addInt(22, i);
        AppState.addInt(24, i);
        AppState.addInt(293, i);
    }

    /* renamed from: F */
    public static final void addUploadBytes(int i) {
        initStartupState();
        AppState.addInt(19, i);
        AppState.addInt(21, i);
        AppState.addInt(23, i);
        AppState.addInt(25, i);
        AppState.addInt(294, i);
    }

    /* renamed from: G */
    public static final void addConnectionBytes(int i) {
        initStartupState();
        AppState.addInt(26, i);
        AppState.addInt(28, i);
        AppState.addInt(30, i);
        AppState.addInt(32, i);
        AppState.addInt(293, i);
    }

    /* renamed from: H */
    public static final void addProtocolBytes(int i) {
        initStartupState();
        AppState.addInt(27, i);
        AppState.addInt(29, i);
        AppState.addInt(31, i);
        AppState.addInt(33, i);
        AppState.addInt(294, i);
    }

    /* renamed from: a */
    public static final int getTrafficCount(int i, int i2, int i3) {
        return AppState.getInt(2 + (i << 3) + (i2 << 1) + i3);
    }

    /* renamed from: b */
    public static final int getTotalTraffic(int i, int i2) {
        return getTrafficCount(0, i, i2) + getTrafficCount(1, i, i2) + getTrafficCount(2, i, i2) + getTrafficCount(3, i, i2);
    }

    /* renamed from: b */
    public static final void addTrafficCount(int i, int i2, int i3) {
        AppState.setInt(2 + (i << 3) + (i2 << 1) + i3, 0);
    }
}
