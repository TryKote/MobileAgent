package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.Message;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.AsyncScreenView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.util.JsonParser;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class ChatAsyncScreen extends AsyncScreenView {

    private static final int ICON_CHAT_ROOMS = 37;
    private static final int ICON_MESSAGES = 41;
    private static final int ICON_INVITE = 42;
    private static final int TEXT_CHAT_ROOMS = 833;
    private static final int TEXT_MESSAGES = 836;
    private static final int TEXT_INVITE = 862;

    public ChatAsyncScreen(int screenId) {
        super(screenId, iconFor(screenId), textFor(screenId));
    }

    private static int iconFor(int screenId) {
        switch (screenId) {
            case ScreenId.CHAT_ROOMS: return ICON_CHAT_ROOMS;
            case ScreenId.CHAT_ROOM_MESSAGES: return ICON_MESSAGES;
            default: return ICON_INVITE;
        }
    }

    private static int textFor(int screenId) {
        switch (screenId) {
            case ScreenId.CHAT_ROOMS: return TEXT_CHAT_ROOMS;
            case ScreenId.CHAT_ROOM_MESSAGES: return TEXT_MESSAGES;
            default: return TEXT_INVITE;
        }
    }

    public void showSelf() {
        if (screenId == ScreenId.CHAT_ROOMS) {
            RegistrationState.clearRegistrationData();
            Account account = AppState.getAccount();
            if (account.getChatRoomCount() != 0 && !account.areChatRoomsLoaded()) {
                ScreenBuilder.openScreen(ScreenId.CHAT_ROOM_INIT);
                return;
            }
        } else if (screenId == ScreenId.CHAT_ROOM_MESSAGES) {
            RegistrationState.clearRegistrationData();
            Account account = AppState.getAccount();
            ChatRoom chatRoom = account.findChatRoomById(ChatState.getChatRoomId());
            if (!chatRoom.isInitialized) {
                ScreenBuilder.openScreen(ScreenId.CHAT_ROOM_VIEW);
                return;
            }
        }
        startRequest();
        ScreenManager.showScreen(this);
    }

    public void startRequest() {
        switch (screenId) {
            case ScreenId.CHAT_ROOMS:
                startChatRoomsRequest();
                break;
            case ScreenId.CHAT_ROOM_MESSAGES:
                startMessagesRequest();
                break;
            case ScreenId.CHAT_ROOM_INVITE:
                startInviteRequest();
                break;
        }
    }

    public int processResponse(Object[] result) {
        switch (screenId) {
            case ScreenId.CHAT_ROOMS:
                return processChatRoomsResponse(result);
            case ScreenId.CHAT_ROOM_MESSAGES:
                return processMessagesResponse(result);
            case ScreenId.CHAT_ROOM_INVITE:
                return processInviteResponse(result);
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        if (screenId == ScreenId.CHAT_ROOM_MESSAGES) {
            RegistrationState.clearRegistrationData();
        }
        return 0;
    }

    public void onClosed() {
        RegistrationState.clearRegistrationData();
    }

    // --- Request builders ---

    private static void startChatRoomsRequest() {
        Account account = AppState.getAccount();
        Vector params = ObjectPool.newVector();
        params.addElement(ObjectPool.integerOf(0));
        params.addElement(AppState.emptyStr);
        params.addElement(ObjectPool.integerOf(1));
        account.sendChatRoomRequest(ApiClient.createAuthRequest(
            ObjectPool.newStringBuffer()
                .append(StringPool.get(PackedStringKeys.URL_PATH_MAILBOX))
                .append('?')
                .append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL))
                .append(StringPool.get(PackedStringKeys.FUNC_AJAX_GET_MAILBOX))
                .append(SessionState.getSessionHash())
                .append(StringPool.get(PackedStringKeys.PARAM_DATA_EQ))
                .append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
    }

    private static void startMessagesRequest() {
        Account account = AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(ChatState.getChatRoomId());
        Vector params = ObjectPool.newVector();
        Object[] request;
        if (account.isLastChatRoom(chatRoom)) {
            params.addElement(StringUtils.intern(Integer.toString(0)));
            params.addElement(chatRoom.participants);
            request = ApiClient.createUploadRequest(
                StringPool.get(PackedStringKeys.URL_PATH_MAILBOX),
                ObjectPool.newBufferFromState(722608)
                    .append(StringPool.get(PackedStringKeys.FUNC_AJAX_GET_FOLDER_LIST))
                    .append(SessionState.getSessionHash())
                    .append(StringPool.get(PackedStringKeys.PARAM_DATA_EQ))
                    .append(Conversation.urlEncode((Object) JsonParser.toJson(params))));
        } else {
            params.addElement(StringUtils.intern(Integer.toString(chatRoom.id)));
            int timeout = SettingsState.getTimeoutValue();
            params.addElement(StringUtils.intern(Integer.toString(
                Utils.max(timeout, chatRoom.messageIds.size() + (chatRoom.isActive ? timeout : 0)))));
            params.addElement(StringUtils.intern(Integer.toString(1)));
            params.addElement(AppState.emptyStr);
            Vector messageIdParams = ObjectPool.newVector();
            Enumeration msgEnum = chatRoom.messageIds.elements();
            while (msgEnum.hasMoreElements()) {
                Object msgId = msgEnum.nextElement();
                if (chatRoom.messages.containsKey(msgId)) {
                    messageIdParams.addElement(msgId);
                }
            }
            params.addElement(messageIdParams);
            request = ApiClient.createAuthRequest(
                ObjectPool.newBufferFromState(1050207)
                    .append('?')
                    .append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL))
                    .append(StringPool.get(PackedStringKeys.FUNC_MAJAX_GET_MSGS))
                    .append(SessionState.getSessionHash())
                    .append(StringPool.get(PackedStringKeys.PARAM_DATA_EQ))
                    .append(Conversation.urlEncode((Object) JsonParser.toJson(params))));
        }
        account.sendChatRoomRequest(request);
    }

    private static void startInviteRequest() {
        Vector params = ObjectPool.newVector();
        params.addElement(ObjectPool.integerOf(ChatState.getActiveChatRoomId()));
        params.addElement(UIState.getMediaStream());
        AppState.getAccount().sendChatRoomRequest(ApiClient.createUploadRequest(
            StringPool.get(PackedStringKeys.URL_PATH_MAILBOX),
            ObjectPool.newStringBuffer()
                .append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL))
                .append(StringPool.get(PackedStringKeys.FUNC_AJAX_MOVE_MSGS))
                .append(SessionState.getSessionHash())
                .append(StringPool.get(PackedStringKeys.PARAM_DATA_EQ))
                .append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
    }

    // --- Response processors ---

    private static int processChatRoomsResponse(Object[] result) {
        int responseCode = ApiClient.validateJsonResponse(result);
        if (responseCode != 0) {
            return responseCode;
        }
        AppState.getAccount().parseChatRoomsFromJson(ApiClient.getJsonPayload());
        return UIState.getScreenAction();
    }

    private static int processMessagesResponse(Object[] result) {
        int responseCode = ApiClient.validateJsonResponse(result);
        if (responseCode != 0) {
            return responseCode;
        }
        Object payload = ApiClient.getJsonPayload();
        Account account = AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(ChatState.getChatRoomId());
        if (!account.isLastChatRoom(chatRoom)) {
            chatRoom.subject = JsonParser.getStringValue(payload,
                StringPool.get(PackedStringKeys.MAIL_FIELD_LAST_MSG_ID));
            chatRoom.messageIds.removeAllElements();
            Enumeration allIds = ((Vector) JsonParser.getValue(payload,
                StringPool.get(PackedStringKeys.MAIL_PARAM_MLIST_ALL))).elements();
            while (allIds.hasMoreElements()) {
                chatRoom.messageIds.addElement(allIds.nextElement());
            }
            Enumeration msgEntries = ((Vector) JsonParser.getValue(payload,
                StringPool.get(PackedStringKeys.MAIL_PARAM_MLIST))).elements();
            while (msgEntries.hasMoreElements()) {
                Message msg = new Message((Hashtable) msgEntries.nextElement());
                chatRoom.messages.put(msg.from, msg);
            }
            Enumeration existingKeys = chatRoom.messages.keys();
            while (existingKeys.hasMoreElements()) {
                String msgKey = (String) existingKeys.nextElement();
                if (!chatRoom.messageIds.contains(msgKey)) {
                    chatRoom.messages.remove(msgKey);
                }
            }
            Enumeration readEntries = chatRoom.readMessages.elements();
            while (readEntries.hasMoreElements()) {
                String readKey = (String) readEntries.nextElement();
                if (!chatRoom.messageIds.contains(readKey)) {
                    chatRoom.readMessages.removeElement(readKey);
                }
            }
            chatRoom.isInitialized = false;
        } else {
            Enumeration newMsgs = ((Vector) payload).elements();
            while (newMsgs.hasMoreElements()) {
                Message msg = new Message((Hashtable) newMsgs.nextElement());
                chatRoom.messages.put(msg.from, msg);
            }
        }
        return ScreenId.CHAT_ROOM_VIEW;
    }

    private static int processInviteResponse(Object[] result) {
        int responseCode = ApiClient.validateJsonResponse(result);
        if (responseCode != 0) {
            return responseCode;
        }
        Object payload = ApiClient.getJsonPayload();
        Account account = AppState.getAccount();
        int entryCount = ((Vector) payload).size();
        for (int i = 0; i < entryCount; i++) {
            Enumeration entryKeys = ((Hashtable) ((Vector) payload).elementAt(i)).keys();
            while (entryKeys.hasMoreElements()) {
                String msgId = (String) entryKeys.nextElement();
                ChatRoom sourceChatRoom = account.findChatRoomByName(msgId);
                ChatRoom targetChatRoom = account.findChatRoomById(ChatState.getActiveChatRoomId());
                if (sourceChatRoom != null && targetChatRoom != null) {
                    Message movedMessage = sourceChatRoom.getMessage(msgId);
                    if (movedMessage != null) {
                        if (movedMessage.hasFlag(4)) {
                            if (targetChatRoom == account.findDefaultChatRoom()) {
                                movedMessage.setFlag(4, false);
                            }
                            sourceChatRoom.decrementUnread();
                        }
                        sourceChatRoom.decrementMembers();
                        if (!movedMessage.isRead()) {
                            targetChatRoom.incrementUnread();
                        }
                        targetChatRoom.memberCount++;
                    }
                    if (sourceChatRoom != targetChatRoom) {
                        account.removeChatRoomUser(msgId);
                        targetChatRoom.setActive(false);
                    }
                }
            }
        }
        return ScreenId.CHAT_ROOM_VIEW;
    }
}
