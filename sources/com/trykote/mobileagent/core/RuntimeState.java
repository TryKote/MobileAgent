package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.RuntimeKeys;

import java.util.Vector;

/**
 * Typed facade for runtime computation state (RuntimeKeys).
 * Covers message slots, XMPP commands, traffic stats, search params, timestamps.
 * No delta persistence — all keys are above DELTA_SIZE.
 */
public final class RuntimeState extends AppState {

    public static final RuntimeState INSTANCE = new RuntimeState();
    private RuntimeState() {}

    // --- Message ID slots ---

    public static String getMsgId1() {
        return getString(RuntimeKeys.SLOT_MSG_ID_1);
    }

    public static void setMsgId1(Object id) {
        setObject(RuntimeKeys.SLOT_MSG_ID_1, id);
    }

    public static void clearMsgId1() {
        clearIndex(RuntimeKeys.SLOT_MSG_ID_1);
    }

    public static String getMsgId2() {
        return getString(RuntimeKeys.SLOT_MSG_ID_2);
    }

    public static void clearMsgId2() {
        clearIndex(RuntimeKeys.SLOT_MSG_ID_2);
    }

    // --- Current message text ---

    public static void setCurrentMsgText(Object text) {
        setObject(RuntimeKeys.SLOT_CURRENT_MSG_TEXT, text);
    }

    public static void clearCurrentMsgText() {
        clearIndex(RuntimeKeys.SLOT_CURRENT_MSG_TEXT);
    }

    // --- Traffic cost text ---

    public static void setTrafficCostText(StringBuffer buf) {
        setFromBuffer(RuntimeKeys.SLOT_TRAFFIC_COST_TEXT, buf);
    }

    // --- Message fields ---

    public static String getMessageId() {
        return getString(RuntimeKeys.SLOT_MESSAGE_ID);
    }

    public static void setMessageId(Object id) {
        setObject(RuntimeKeys.SLOT_MESSAGE_ID, id);
    }

    public static String getMsgSubject() {
        return getString(RuntimeKeys.SLOT_MSG_SUBJECT);
    }

    public static String getMsgSender() {
        return getString(RuntimeKeys.SLOT_MSG_SENDER);
    }

    public static String getMsgBody() {
        return getString(RuntimeKeys.SLOT_MSG_BODY);
    }

    public static String getMsgExtra1() {
        return getString(RuntimeKeys.SLOT_MSG_EXTRA_1);
    }

    public static String getMsgExtra2() {
        return getString(RuntimeKeys.SLOT_MSG_EXTRA_2);
    }

    public static void setMsgExtra2(Object value) {
        setObject(RuntimeKeys.SLOT_MSG_EXTRA_2, value);
    }

    public static String getMsgExtra3() {
        return getString(RuntimeKeys.SLOT_MSG_EXTRA_3);
    }

    public static void setMsgExtra3(Object value) {
        setObject(RuntimeKeys.SLOT_MSG_EXTRA_3, value);
    }

    public static String getTrafficStatusText() {
        return getString(RuntimeKeys.SLOT_TRAFFIC_STATUS_TEXT);
    }

    public static void setTrafficStatusText(StringBuffer buf) {
        setFromBuffer(RuntimeKeys.SLOT_TRAFFIC_STATUS_TEXT, buf);
    }

    /** Clear SLOT_MSG_SUBJECT through SLOT_MSG_EXTRA_1 (1348-1351). */
    public static void clearMsgFields() {
        clearRange(RuntimeKeys.SLOT_MSG_SUBJECT, RuntimeKeys.SLOT_MSG_EXTRA_1);
    }

    /** Clear SLOT_MSG_EXTRA_2 through SLOT_TRAFFIC_STATUS_TEXT (1352-1354). */
    public static void clearMsgExtras() {
        clearRange(RuntimeKeys.SLOT_MSG_EXTRA_2, RuntimeKeys.SLOT_TRAFFIC_STATUS_TEXT);
    }

    // --- Message timestamps ---

    public static long getSelectedMsgTimestamp() {
        return getLong(RuntimeKeys.TIMESTAMP_SELECTED_MSG);
    }

    public static void setSelectedMsgTimestamp(long value) {
        setLong(RuntimeKeys.TIMESTAMP_SELECTED_MSG, value);
    }

    public static int getMessageIcon() {
        return getInt(RuntimeKeys.INT_MESSAGE_ICON);
    }

    public static void setMessageIcon(int icon) {
        setInt(RuntimeKeys.INT_MESSAGE_ICON, icon);
    }

    public static int getErrorMsgIndex() {
        return getInt(RuntimeKeys.INT_ERROR_MSG_INDEX);
    }

    public static void setErrorMsgIndex(int index) {
        setInt(RuntimeKeys.INT_ERROR_MSG_INDEX, index);
    }

    // --- XMPP commands ---

    public static Object getXmppCommand1() {
        return getPoolObject(RuntimeKeys.SLOT_XMPP_COMMAND_1);
    }

    public static void setXmppCommand1(Object cmd) {
        setObject(RuntimeKeys.SLOT_XMPP_COMMAND_1, cmd);
    }

    public static void clearXmppCommand1() {
        clearIndex(RuntimeKeys.SLOT_XMPP_COMMAND_1);
    }

    public static Object getXmppCommand2() {
        return getPoolObject(RuntimeKeys.SLOT_XMPP_COMMAND_2);
    }

    public static void setXmppCommand2(Object cmd) {
        setObject(RuntimeKeys.SLOT_XMPP_COMMAND_2, cmd);
    }

    public static void clearXmppCommand2() {
        clearIndex(RuntimeKeys.SLOT_XMPP_COMMAND_2);
    }

    public static int getXmppCommandIndex() {
        return getInt(RuntimeKeys.INT_XMPP_COMMAND_INDEX);
    }

    public static void setXmppCommandIndex(int index) {
        setInt(RuntimeKeys.INT_XMPP_COMMAND_INDEX, index);
    }

    public static int getXmppSelectionIndex() {
        return getInt(RuntimeKeys.INT_XMPP_SELECTION_INDEX);
    }

    public static void setXmppSelectionIndex(int index) {
        setInt(RuntimeKeys.INT_XMPP_SELECTION_INDEX, index);
    }

    public static int getXmppAction() {
        return getInt(RuntimeKeys.INT_XMPP_ACTION);
    }

    public static void setXmppAction(int action) {
        setInt(RuntimeKeys.INT_XMPP_ACTION, action);
    }

    public static int getXmppActionType() {
        return getInt(RuntimeKeys.INT_XMPP_ACTION_TYPE);
    }

    public static void setXmppActionType(int type) {
        setInt(RuntimeKeys.INT_XMPP_ACTION_TYPE, type);
    }

    // --- XMPP traffic bytes ---

    public static int getXmppTrafficBytes() {
        return getInt(RuntimeKeys.INT_XMPP_TRAFFIC_BYTES);
    }

    public static void addXmppTrafficBytes(int bytes) {
        addInt(RuntimeKeys.INT_XMPP_TRAFFIC_BYTES, bytes);
    }

    // --- Search params ---

    public static Vector getSearchParams1() {
        return getVector(RuntimeKeys.OBJ_SEARCH_PARAMS_1);
    }

    public static void setSearchParams1(Object params) {
        setObject(RuntimeKeys.OBJ_SEARCH_PARAMS_1, params);
    }

    public static Vector getSearchParams2() {
        return getVector(RuntimeKeys.OBJ_SEARCH_PARAMS_2);
    }

    public static void setSearchParams2(Object params) {
        setObject(RuntimeKeys.OBJ_SEARCH_PARAMS_2, params);
    }

    // --- Phone ---

    public static int getPhoneScrollOffset() {
        return getInt(RuntimeKeys.INT_PHONE_SCROLL_OFFSET);
    }

    public static void setPhoneScrollOffset(int offset) {
        setInt(RuntimeKeys.INT_PHONE_SCROLL_OFFSET, offset);
    }

    // --- Polling timestamps ---

    public static int getLastPollTimestamp() {
        return getInt(RuntimeKeys.INT_LAST_POLL_TIMESTAMP);
    }

    public static void setLastPollTimestamp(int ts) {
        setInt(RuntimeKeys.INT_LAST_POLL_TIMESTAMP, ts);
    }

    public static int getLastCheckTimestamp() {
        return getInt(RuntimeKeys.INT_LAST_CHECK_TIMESTAMP);
    }

    public static void setLastCheckTimestamp(int ts) {
        setInt(RuntimeKeys.INT_LAST_CHECK_TIMESTAMP, ts);
    }

    public static int getLastListSize() {
        return getInt(RuntimeKeys.INT_LAST_LIST_SIZE);
    }

    public static void setLastListSize(int size) {
        setInt(RuntimeKeys.INT_LAST_LIST_SIZE, size);
    }

    public static int getCurrentTimestamp() {
        return getInt(RuntimeKeys.INT_CURRENT_TIMESTAMP);
    }

    // --- Traffic stats display ---

    public static int getPeriodIndex() {
        return getInt(RuntimeKeys.INT_PERIOD_INDEX);
    }

    public static void setPeriodIndex(int index) {
        setInt(RuntimeKeys.INT_PERIOD_INDEX, index);
    }

    public static void setTrafficPeriodLabel(int label) {
        setInt(RuntimeKeys.INT_TRAFFIC_PERIOD_LABEL, label);
    }

    public static void setStatRows(int rows) {
        setInt(RuntimeKeys.INT_STAT_ROWS, rows);
    }

    public static void setStatCols(int cols) {
        setInt(RuntimeKeys.INT_STAT_COLS, cols);
    }

    // --- Misc ---

    public static int getMaxPendingRequests() {
        return getInt(RuntimeKeys.INT_MAX_PENDING_REQUESTS);
    }

    public static void setMaxPendingRequests(int max) {
        setInt(RuntimeKeys.INT_MAX_PENDING_REQUESTS, max);
    }

    public static int getInfoScreenMode() {
        return getInt(RuntimeKeys.INT_INFO_SCREEN_MODE);
    }

    public static void setInfoScreenMode(int mode) {
        setInt(RuntimeKeys.INT_INFO_SCREEN_MODE, mode);
    }

    public static int getDeleteButtonIcon() {
        return getInt(RuntimeKeys.INT_DELETE_BUTTON_ICON);
    }

    public static void setDeleteButtonIcon(int icon) {
        setInt(RuntimeKeys.INT_DELETE_BUTTON_ICON, icon);
    }

    public static int getBlockButtonIcon() {
        return getInt(RuntimeKeys.INT_BLOCK_BUTTON_ICON);
    }

    public static void setBlockButtonIcon(int icon) {
        setInt(RuntimeKeys.INT_BLOCK_BUTTON_ICON, icon);
    }

    public static int getAsyncTaskId() {
        return getInt(RuntimeKeys.INT_ASYNC_TASK_ID);
    }

    public static void setAsyncTaskId(int id) {
        setInt(RuntimeKeys.INT_ASYNC_TASK_ID, id);
    }

    /** Reset polling state (all timestamps and counters to 0). */
    public static void resetPollingState() {
        setInt(RuntimeKeys.INT_LAST_POLL_TIMESTAMP, 0);
        setInt(RuntimeKeys.INT_LAST_CHECK_TIMESTAMP, 0);
        setInt(RuntimeKeys.INT_LAST_LIST_SIZE, 0);
    }
}
