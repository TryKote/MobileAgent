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
        int savedDate = AppState.getInt(TrafficKeys.TRAFFIC_SAVED_DATE);
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
            AppState.setInt(TrafficKeys.TRAFFIC_SAVED_DATE, currentDate);
        }
        return currentDate;
    }

    public static final void addMmpInbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_FILES, i);
        AppState.addInt(SessionKeys.COUNTER_TOTAL_TRAFFIC, i);
    }

    public static final void addMmpOutbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_FILES, i);
        AppState.addInt(SessionKeys.COUNTER_RESERVED, i);
    }

    public static final void addXmppInbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_FILES, i);
        AppState.addInt(SessionKeys.COUNTER_TOTAL_TRAFFIC, i);
    }

    public static final void addXmppOutbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_FILES, i);
        AppState.addInt(SessionKeys.COUNTER_RESERVED, i);
    }

    public static final void addHttpInbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_FILES, i);
        AppState.addInt(SessionKeys.COUNTER_TOTAL_TRAFFIC, i);
    }

    public static final void addHttpOutbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_FILES, i);
        AppState.addInt(SessionKeys.COUNTER_RESERVED, i);
    }

    /* renamed from: a */
    public static final int getTrafficCount(int i, int i2, int i3) {
        return AppState.getInt(TrafficKeys.TRAFFIC_MRIM_SENT_BYTES + (i << 3) + (i2 << 1) + i3);
    }

    /* renamed from: b */
    public static final int getTotalTraffic(int i, int i2) {
        return getTrafficCount(0, i, i2) + getTrafficCount(1, i, i2) + getTrafficCount(2, i, i2) + getTrafficCount(3, i, i2);
    }

    /* renamed from: b */
    public static final void addTrafficCount(int i, int i2, int i3) {
        AppState.setInt(TrafficKeys.TRAFFIC_MRIM_SENT_BYTES + (i << 3) + (i2 << 1) + i3, 0);
    }
}
