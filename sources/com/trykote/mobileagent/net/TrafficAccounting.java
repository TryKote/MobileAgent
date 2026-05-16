package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.ContactKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.key.TrafficKeys;
import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;
/* Extracted from AppController: traffic accounting subsystem */
public final class TrafficAccounting extends AppState {

    public static final TrafficAccounting INSTANCE = new TrafficAccounting();
    private TrafficAccounting() {}

    protected String storeName() { return STORE_TRAFFIC; }
    protected int deltaStart() { return RANGE_TRAFFIC_START; }
    protected int deltaEnd() { return RANGE_TRAFFIC_END; }

    // Base key for each traffic category (MRIM, MMP, XMPP, HTTP)
    private static final int[] CATEGORY_BASE = {
        TrafficKeys.TRAFFIC_MRIM_SENT_BYTES,
        TrafficKeys.TRAFFIC_MMP_SENT_BYTES,
        TrafficKeys.TRAFFIC_XMPP_SENT_BYTES,
        TrafficKeys.TRAFFIC_HTTP_SENT_BYTES
    };

    // Offsets from category base for daily/monthly counters
    private static final int OFFSET_DAILY_SENT = 2;
    private static final int OFFSET_DAILY_RECV = 3;
    private static final int OFFSET_MONTHLY_SENT = 4;
    private static final int OFFSET_MONTHLY_RECV = 5;
    private static final int MONTH_SHIFT = 8;

    private static final int STAT_ROWS_PER_ACCOUNT = 8;
    private static final int STAT_COLS_PER_ACCOUNT = 3;
    private static final int STAT_ROWS_TOTAL = 5;
    private static final int STAT_COLS_TOTAL = 16;

    private static final int CENTS_PER_UNIT = 100;
    private static final int BYTES_PER_MB = 1048576;

    public static final int initStartupState() {
        int currentDate = AppState.getDateCode();
        int savedDate = AppState.getInt(TrafficKeys.TRAFFIC_SAVED_DATE);
        if (currentDate != savedDate) {
            boolean monthChanged = (currentDate >>> MONTH_SHIFT) != (savedDate >>> MONTH_SHIFT);
            for (int i = 0; i < CATEGORY_BASE.length; i++) {
                int base = CATEGORY_BASE[i];
                AppState.setInt(base + OFFSET_DAILY_SENT, 0);
                AppState.setInt(base + OFFSET_DAILY_RECV, 0);
                if (monthChanged) {
                    AppState.setInt(base + OFFSET_MONTHLY_SENT, 0);
                    AppState.setInt(base + OFFSET_MONTHLY_RECV, 0);
                }
            }
            AppState.setInt(TrafficKeys.TRAFFIC_SAVED_DATE, currentDate);
        }
        return currentDate;
    }

    public static void addMrimInbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_SENT_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_SENT_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_SENT_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_SENT_FILES, i);
        SessionState.addTotalTraffic(i);
    }

    public static void addMrimOutbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_RECV_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_RECV_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_RECV_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MRIM_RECV_FILES, i);
        SessionState.addReservedTraffic(i);
    }

    public static void resetByteCounters() {
        AppState.setInt(TrafficKeys.TRAFFIC_MRIM_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_MRIM_RECV_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_MMP_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_MMP_RECV_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_XMPP_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_XMPP_RECV_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_HTTP_SENT_BYTES, 0);
        AppState.setInt(TrafficKeys.TRAFFIC_HTTP_RECV_BYTES, 0);
    }

    public static final void addMmpInbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_SENT_FILES, i);
        SessionState.addTotalTraffic(i);
    }

    public static final void addMmpOutbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_MMP_RECV_FILES, i);
        SessionState.addReservedTraffic(i);
    }

    public static final void addXmppInbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_SENT_FILES, i);
        SessionState.addTotalTraffic(i);
    }

    public static final void addXmppOutbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_XMPP_RECV_FILES, i);
        SessionState.addReservedTraffic(i);
    }

    public static final void addHttpInbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_SENT_FILES, i);
        SessionState.addTotalTraffic(i);
    }

    public static final void addHttpOutbound(int i) {
        initStartupState();
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_BYTES, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_PACKETS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_MSGS, i);
        AppState.addInt(TrafficKeys.TRAFFIC_HTTP_RECV_FILES, i);
        SessionState.addReservedTraffic(i);
    }

    public static final int getTrafficCount(int category, int period, int direction) {
        return AppState.getInt(CATEGORY_BASE[category] + period * 2 + direction);
    }

    public static final int getTotalTraffic(int period, int direction) {
        int total = 0;
        for (int i = 0; i < CATEGORY_BASE.length; i++) {
            total += getTrafficCount(i, period, direction);
        }
        return total;
    }

    public static final void addTrafficCount(int category, int period, int direction) {
        AppState.setInt(CATEGORY_BASE[category] + period * 2 + direction, 0);
    }

    public static void showTrafficStats() {
        int i;
        int i2;
        int periodIndex = RuntimeState.getPeriodIndex();
        Account account = AppState.getAccount();
        if (account != null) {
            int sentBytes = account.getSyncValue(periodIndex, 0);
            i = sentBytes;
            formatTrafficItem(1326, sentBytes);
            int recvBytes = account.getSyncValue(periodIndex, 1);
            i2 = recvBytes;
            formatTrafficItem(1325, recvBytes);
            RuntimeState.setStatRows(STAT_ROWS_PER_ACCOUNT);
            RuntimeState.setStatCols(STAT_COLS_PER_ACCOUNT);
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
            RuntimeState.setStatRows(STAT_ROWS_TOTAL);
            RuntimeState.setStatCols(STAT_COLS_TOTAL);
        }
        long j = i + i2;
        int blockSize = SettingsState.getBlockSizeKb() << 10;
        if (blockSize > 0) {
            long j2 = j % blockSize;
            if (j2 > 0) {
                j += blockSize - j2;
            }
        }
        int costCents = (int) ((j * SettingsState.getTrafficCost()) / BYTES_PER_MB);
        RuntimeState.setTrafficCostText(ObjectPool.newStringBuffer().append(costCents / CENTS_PER_UNIT).append('.').append(Utils.zeroPad(costCents % CENTS_PER_UNIT)).append(' ').append(ResourceAccessor.str(StringResKeys.STR_CURRENCY_SYMBOL)));
        RuntimeState.setTrafficPeriodLabel(periodIndex + 745);
        Screens.trafficStats().show();
        AppState.clearRange(ContactKeys.SLOT_GROUP_LIST_INDEX, UIKeys.RANGE_SEARCH_LABEL_END);
    }

    private static void formatTrafficItem(int i, int i2) {
        AppState.setObject(i, (Object) Utils.formatSize(i2));
    }

    public static int parseBalance() {
        ScreenManager.processScreenForm();
        String balanceStr = Utils.defaultStr(UIState.getScreenValue());
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
        SettingsState.setTrafficCost(i);
        return 0;
    }
}
