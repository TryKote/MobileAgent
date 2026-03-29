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

    public static void showTrafficStats() {
        int i;
        int i2;
        int periodIndex = AppState.getInt(RuntimeKeys.INT_PERIOD_INDEX);
        Account account = AppState.getAccount();
        if (account != null) {
            int sentBytes = account.getSyncValue(periodIndex, 0);
            i = sentBytes;
            formatTrafficItem(1326, sentBytes);
            int recvBytes = account.getSyncValue(periodIndex, 1);
            i2 = recvBytes;
            formatTrafficItem(1325, recvBytes);
            AppState.setInt(RuntimeKeys.INT_STAT_ROWS, 8);
            AppState.setInt(RuntimeKeys.INT_STAT_COLS, 3);
        } else {
            formatTrafficItem(1329, TrafficAccounting.getTrafficCount(0, periodIndex, 0));
            formatTrafficItem(1328, TrafficAccounting.getTrafficCount(0, periodIndex, 1));
            formatTrafficItem(1331, TrafficAccounting.getTrafficCount(1, periodIndex, 0));
            formatTrafficItem(1330, TrafficAccounting.getTrafficCount(1, periodIndex, 1));
            formatTrafficItem(1333, TrafficAccounting.getTrafficCount(2, periodIndex, 0));
            formatTrafficItem(1332, TrafficAccounting.getTrafficCount(2, periodIndex, 1));
            formatTrafficItem(1335, TrafficAccounting.getTrafficCount(3, periodIndex, 0));
            formatTrafficItem(1334, TrafficAccounting.getTrafficCount(3, periodIndex, 1));
            int totalSent = TrafficAccounting.getTotalTraffic(periodIndex, 0);
            i = totalSent;
            formatTrafficItem(1326, totalSent);
            int totalRecv = TrafficAccounting.getTotalTraffic(periodIndex, 1);
            i2 = totalRecv;
            formatTrafficItem(1325, totalRecv);
            AppState.setInt(RuntimeKeys.INT_STAT_ROWS, 5);
            AppState.setInt(RuntimeKeys.INT_STAT_COLS, 16);
        }
        long j = i + i2;
        int blockSize = AppState.getInt(SettingsKeys.SETTING_BLOCK_SIZE_KB) << 10;
        if (blockSize > 0) {
            long j2 = j % blockSize;
            if (j2 > 0) {
                j += blockSize - j2;
            }
        }
        int costCents = (int) ((j * AppState.getInt(SettingsKeys.SETTING_TRAFFIC_COST)) / 1048576);
        AppState.setFromBuffer(RuntimeKeys.SLOT_TRAFFIC_COST_TEXT, ObjectPool.newStringBuffer().append(costCents / 100).append('.').append(Utils.zeroPad(costCents % 100)).append(' ').append(AppState.getString(StringResKeys.STR_CURRENCY_SYMBOL)));
        AppState.setInt(RuntimeKeys.INT_TRAFFIC_PERIOD_LABEL, periodIndex + 745);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.TRAFFIC_STATS));
        AppState.clearRange(ContactKeys.SLOT_GROUP_LIST_INDEX, UIKeys.RANGE_SEARCH_LABEL_END);
    }

    private static void formatTrafficItem(int i, int i2) {
        AppState.setObject(i, (Object) Utils.formatSize(i2));
    }

    public static int parseBalance() {
        ScreenManager.processScreenForm();
        String balanceStr = Utils.defaultStr(AppState.getString(UIKeys.SLOT_SCREEN_VALUE));
        int i = 0;
        int sepIdx = balanceStr.lastIndexOf(46);
        int dotIdx = sepIdx;
        if (sepIdx == -1) {
            dotIdx = balanceStr.lastIndexOf(44);
        }
        if (dotIdx != -1) {
            try {
                i = Integer.parseInt(StringUtils.prefix(balanceStr, dotIdx)) * 100;
            } catch (Throwable unused) {
            }
            try {
                String fraction = StringUtils.suffix(balanceStr, dotIdx + 1);
                int i2 = Integer.parseInt(fraction);
                i += fraction.length() == 1 ? i2 * 10 : i2;
            } catch (Throwable unused2) {
            }
        } else {
            try {
                i = Integer.parseInt(balanceStr) * 100;
            } catch (Throwable unused3) {
            }
        }
        AppState.setInt(SettingsKeys.SETTING_TRAFFIC_COST, i);
        return 0;
    }
}
