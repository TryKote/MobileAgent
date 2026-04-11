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

    private static final int CATEGORY_COUNT = 4;
    private static final int SLOTS_PER_CATEGORY = 8;
    private static final int DAILY_SENT_OFFSET = 4;
    private static final int DAILY_RECV_OFFSET = 5;
    private static final int MONTHLY_SENT_OFFSET = 6;
    private static final int MONTHLY_RECV_OFFSET = 7;
    private static final int MONTH_SHIFT = 8;

    private static final int STAT_ROWS_PER_ACCOUNT = 8;
    private static final int STAT_COLS_PER_ACCOUNT = 3;
    private static final int STAT_ROWS_TOTAL = 5;
    private static final int STAT_COLS_TOTAL = 16;

    private static final int CENTS_PER_UNIT = 100;
    private static final int BYTES_PER_MB = 1048576;

    public static final int initStartupState() {
        int currentDate = Storage.state().getDateCode();
        int savedDate = Storage.state().getInt(TrafficKeys.TRAFFIC_SAVED_DATE);
        if (currentDate != savedDate) {
            for (int i = 0; i < CATEGORY_COUNT; i++) {
                int offset = i * SLOTS_PER_CATEGORY;
                Storage.state().setInt(offset + DAILY_SENT_OFFSET, 0);
                Storage.state().setInt(offset + DAILY_RECV_OFFSET, 0);
                if ((currentDate >>> MONTH_SHIFT) != (savedDate >>> MONTH_SHIFT)) {
                    Storage.state().setInt(offset + MONTHLY_SENT_OFFSET, 0);
                    Storage.state().setInt(offset + MONTHLY_RECV_OFFSET, 0);
                }
            }
            Storage.state().setInt(TrafficKeys.TRAFFIC_SAVED_DATE, currentDate);
        }
        return currentDate;
    }

    public static final void addMmpInbound(int i) {
        initStartupState();
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_SENT_BYTES, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_SENT_PACKETS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_SENT_MSGS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_SENT_FILES, i);
        Storage.state().addInt(SessionKeys.COUNTER_TOTAL_TRAFFIC, i);
    }

    public static final void addMmpOutbound(int i) {
        initStartupState();
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_RECV_BYTES, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_RECV_PACKETS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_RECV_MSGS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_MMP_RECV_FILES, i);
        Storage.state().addInt(SessionKeys.COUNTER_RESERVED, i);
    }

    public static final void addXmppInbound(int i) {
        initStartupState();
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_SENT_BYTES, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_SENT_PACKETS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_SENT_MSGS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_SENT_FILES, i);
        Storage.state().addInt(SessionKeys.COUNTER_TOTAL_TRAFFIC, i);
    }

    public static final void addXmppOutbound(int i) {
        initStartupState();
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_RECV_BYTES, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_RECV_PACKETS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_RECV_MSGS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_XMPP_RECV_FILES, i);
        Storage.state().addInt(SessionKeys.COUNTER_RESERVED, i);
    }

    public static final void addHttpInbound(int i) {
        initStartupState();
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_SENT_BYTES, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_SENT_PACKETS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_SENT_MSGS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_SENT_FILES, i);
        Storage.state().addInt(SessionKeys.COUNTER_TOTAL_TRAFFIC, i);
    }

    public static final void addHttpOutbound(int i) {
        initStartupState();
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_RECV_BYTES, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_RECV_PACKETS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_RECV_MSGS, i);
        Storage.state().addInt(TrafficKeys.TRAFFIC_HTTP_RECV_FILES, i);
        Storage.state().addInt(SessionKeys.COUNTER_RESERVED, i);
    }

    public static final int getTrafficCount(int category, int period, int direction) {
        return Storage.state().getInt(TrafficKeys.TRAFFIC_MRIM_SENT_BYTES + category * SLOTS_PER_CATEGORY + period * 2 + direction);
    }

    public static final int getTotalTraffic(int period, int direction) {
        return getTrafficCount(0, period, direction) + getTrafficCount(1, period, direction) + getTrafficCount(2, period, direction) + getTrafficCount(3, period, direction);
    }

    public static final void addTrafficCount(int category, int period, int direction) {
        Storage.state().setInt(TrafficKeys.TRAFFIC_MRIM_SENT_BYTES + category * SLOTS_PER_CATEGORY + period * 2 + direction, 0);
    }

    public static void showTrafficStats() {
        int i;
        int i2;
        int periodIndex = Storage.state().getInt(RuntimeKeys.INT_PERIOD_INDEX);
        Account account = Storage.state().getAccount();
        if (account != null) {
            int sentBytes = account.getSyncValue(periodIndex, 0);
            i = sentBytes;
            formatTrafficItem(1326, sentBytes);
            int recvBytes = account.getSyncValue(periodIndex, 1);
            i2 = recvBytes;
            formatTrafficItem(1325, recvBytes);
            Storage.state().setInt(RuntimeKeys.INT_STAT_ROWS, STAT_ROWS_PER_ACCOUNT);
            Storage.state().setInt(RuntimeKeys.INT_STAT_COLS, STAT_COLS_PER_ACCOUNT);
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
            Storage.state().setInt(RuntimeKeys.INT_STAT_ROWS, STAT_ROWS_TOTAL);
            Storage.state().setInt(RuntimeKeys.INT_STAT_COLS, STAT_COLS_TOTAL);
        }
        long j = i + i2;
        int blockSize = Storage.state().getInt(SettingsKeys.SETTING_BLOCK_SIZE_KB) << 10;
        if (blockSize > 0) {
            long j2 = j % blockSize;
            if (j2 > 0) {
                j += blockSize - j2;
            }
        }
        int costCents = (int) ((j * Storage.state().getInt(SettingsKeys.SETTING_TRAFFIC_COST)) / BYTES_PER_MB);
        Storage.state().setFromBuffer(RuntimeKeys.SLOT_TRAFFIC_COST_TEXT, ObjectPool.newStringBuffer().append(costCents / CENTS_PER_UNIT).append('.').append(Utils.zeroPad(costCents % CENTS_PER_UNIT)).append(' ').append(Storage.resources().getString(StringResKeys.STR_CURRENCY_SYMBOL)));
        Storage.state().setInt(RuntimeKeys.INT_TRAFFIC_PERIOD_LABEL, periodIndex + 745);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.TRAFFIC_STATS));
        Storage.state().clearRange(ContactKeys.SLOT_GROUP_LIST_INDEX, UIKeys.RANGE_SEARCH_LABEL_END);
    }

    private static void formatTrafficItem(int i, int i2) {
        Storage.state().setObject(i, (Object) Utils.formatSize(i2));
    }

    public static int parseBalance() {
        ScreenManager.processScreenForm();
        String balanceStr = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_SCREEN_VALUE));
        int i = 0;
        int sepIdx = balanceStr.lastIndexOf(46);
        int dotIdx = sepIdx;
        if (sepIdx == -1) {
            dotIdx = balanceStr.lastIndexOf(44);
        }
        if (dotIdx != -1) {
            try {
                i = Integer.parseInt(StringUtils.prefix(balanceStr, dotIdx)) * CENTS_PER_UNIT;
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
                i = Integer.parseInt(balanceStr) * CENTS_PER_UNIT;
            } catch (Throwable unused3) {
            }
        }
        Storage.state().setInt(SettingsKeys.SETTING_TRAFFIC_COST, i);
        return 0;
    }
}
