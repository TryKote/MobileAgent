package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.ChatKeys;

import java.util.Vector;

/**
 * Typed facade for chat-related state in AppState.
 * Delegates to AppState — zero runtime overhead.
 */
public final class ChatState extends AppState {
    private ChatState() {}

    public static final ChatState INSTANCE = new ChatState();

    // --- Chat room identity ---

    public static int getChatRoomId() {
        return getInt(ChatKeys.INT_CHATROOM_ID);
    }

    public static void setChatRoomId(int id) {
        setInt(ChatKeys.INT_CHATROOM_ID, id);
    }

    public static int getActiveChatRoomId() {
        return getInt(ChatKeys.INT_ACTIVE_CHATROOM_ID);
    }

    public static void setActiveChatRoomId(int id) {
        setInt(ChatKeys.INT_ACTIVE_CHATROOM_ID, id);
    }

    // --- Chat name (also used as login slot during auth) ---

    public static String getChatName() {
        return getString(ChatKeys.SLOT_CHAT_NAME);
    }

    public static void setChatName(String name) {
        setObject(ChatKeys.SLOT_CHAT_NAME, name);
    }

    public static void setChatNameFromBuffer(StringBuffer buf) {
        setFromBuffer(ChatKeys.SLOT_CHAT_NAME, buf);
    }

    public static void clearChatName() {
        clearIndex(ChatKeys.SLOT_CHAT_NAME);
    }

    // --- Unread count display ---

    public static void setUnreadCountText(StringBuffer buf) {
        setFromBuffer(ChatKeys.SLOT_UNREAD_COUNT_TEXT, buf);
    }

    public static void clearUnreadCountText() {
        clearIndex(ChatKeys.SLOT_UNREAD_COUNT_TEXT);
    }

    // --- Scroll ---

    public static int getScrollOffset() {
        return getInt(ChatKeys.INT_SCROLL_OFFSET);
    }

    public static void setScrollOffset(int offset) {
        setInt(ChatKeys.INT_SCROLL_OFFSET, offset);
    }

    // --- View mode ---

    public static int getChatViewMode() {
        return getInt(ChatKeys.INT_CHAT_VIEW_MODE);
    }

    public static void setChatViewMode(int mode) {
        setInt(ChatKeys.INT_CHAT_VIEW_MODE, mode);
    }

    public static int getChatListMode() {
        return getInt(ChatKeys.INT_CHAT_LIST_MODE);
    }

    public static void setChatListMode(int mode) {
        setInt(ChatKeys.INT_CHAT_LIST_MODE, mode);
    }

    public static boolean isExtendedView() {
        return getBool(ChatKeys.FLAG_EXTENDED_CHAT_VIEW);
    }

    public static void setExtendedView(boolean extended) {
        setBool(ChatKeys.FLAG_EXTENDED_CHAT_VIEW, extended);
    }

    // --- Chat room creation ---

    public static boolean isChatRoomCreated() {
        return getBool(ChatKeys.FLAG_CHAT_ROOM_CREATED);
    }

    public static void setChatRoomCreated(boolean created) {
        setBool(ChatKeys.FLAG_CHAT_ROOM_CREATED, created);
    }

    // --- Screen-scoped flags ---

    public static void setIsChatRoom(boolean value) {
        setBool(ChatKeys.FLAG_IS_CHATROOM, value);
    }

    public static void setMsgReadSelected(boolean value) {
        setBool(ChatKeys.FLAG_MSG_READ_SELECTED, value);
    }

    public static void setMsgUnreadSelected(boolean value) {
        setBool(ChatKeys.FLAG_MSG_UNREAD_SELECTED, value);
    }

    public static void setHasMembers(boolean value) {
        setBool(ChatKeys.FLAG_CHATROOM_HAS_MEMBERS, value);
    }

    public static void setHasMore(boolean value) {
        setBool(ChatKeys.FLAG_CHATROOM_HAS_MORE, value);
    }

    public static void setMsgUnread(boolean value) {
        setBool(ChatKeys.FLAG_MSG_UNREAD, value);
    }

    public static void setMsgRead(boolean value) {
        setBool(ChatKeys.FLAG_MSG_READ, value);
    }

    public static void clearScreenFlags() {
        clearRange(ChatKeys.SCREEN_FLAGS_START, ChatKeys.SCREEN_FLAGS_END);
        setBool(ChatKeys.FLAG_CHAT_ROOM_CREATED, false);
    }

    // --- Tile request queue (map subsystem, stored in chat keys) ---

    public static Vector getTileRequestQueue() {
        return getVector(ChatKeys.VEC_TILE_REQUEST_QUEUE);
    }

    public static void setTileRequestQueue(Vector queue) {
        setObject(ChatKeys.VEC_TILE_REQUEST_QUEUE, queue);
    }

    // --- Message list (search results) ---

    public static Vector getMessageList() {
        return getVector(ChatKeys.VEC_MESSAGE_LIST);
    }

    public static void setMessageList(Vector list) {
        setObject(ChatKeys.VEC_MESSAGE_LIST, list);
    }

    public static void clearMessageList() {
        clearIndex(ChatKeys.VEC_MESSAGE_LIST);
    }
}
