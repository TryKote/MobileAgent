package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public final class ChatHandler extends BaseScreenHandler {

    // Chat room name counter wraps at this value
    private static final int CHAT_NAME_COUNTER_MODULO = 1000;

    // Base offset for emoticon config ID calculation
    private static final int EMOTICON_CONFIG_BASE_OFFSET = 157;

    // MRIM chat room creation flags
    private static final int MRIM_CHATROOM_FLAGS = 128;

    public void buildScreen(int screenId) {
        Object[] request;
        switch (screenId) {
            case ScreenId.CHAT_ROOMS:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                MrimAccount mrimAccount = (MrimAccount) Storage.state().getAccount();
                if (mrimAccount.chatRoomManager.getCount() != 0 && !mrimAccount.chatRoomManager.loaded) {
                    MrimChatRoomManager.showChatRoomListWithCounts();
                    return;
                }
                NotificationHelper.showConfirmDialog(37, 833);
                Vector params = ObjectPool.newVector();
                JsonParser.addIntToVector(params, 0);
                params.addElement(Storage.emptyStr);
                JsonParser.addIntToVector(params, 1);
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createAuthRequest(ObjectPool.newStringBuffer().append(Storage.resources().getString(PackedStringKeys.URL_PATH_MAILBOX)).append('?').append(Storage.resources().getString(PackedStringKeys.PARAM_AJAX_CALL)).append(Storage.resources().getString(PackedStringKeys.FUNC_AJAX_GET_MAILBOX)).append(Storage.state().getString(SessionKeys.SLOT_SESSION_HASH)).append(Storage.resources().getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
                return;
            case ScreenId.CHAT_ROOM_INIT:
                MrimChatRoomManager.showChatRoomListWithCounts();
                return;
            case ScreenId.CHAT_ROOM_MESSAGES:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                ChatRoom chatRoom = ((MrimAccount) Storage.state().getAccount()).chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID));
                if (!chatRoom.isInitialized) {
                    MrimChatRoomManager.showChatRoomMessages();
                    return;
                }
                NotificationHelper.showConfirmDialog(41, 836);
                MrimAccount msgAccount = (MrimAccount) Storage.state().getAccount();
                Vector params2 = ObjectPool.newVector();
                if (chatRoom == msgAccount.chatRoomManager.getLast()) {
                    params2.addElement(StringUtils.intern(Integer.toString(0)));
                    params2.addElement(chatRoom.participants);
                    request = ApiClient.createUploadRequest(Storage.resources().getString(PackedStringKeys.URL_PATH_MAILBOX), ObjectPool.newBufferFromState(722608).append(Storage.resources().getString(PackedStringKeys.FUNC_AJAX_GET_FOLDER_LIST)).append(Storage.state().getString(SessionKeys.SLOT_SESSION_HASH)).append(Storage.resources().getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                } else {
                    params2.addElement(StringUtils.intern(Integer.toString(chatRoom.id)));
                    int intVal2 = Storage.state().getInt(SettingsKeys.SETTING_TIMEOUT_VALUE);
                    params2.addElement(StringUtils.intern(Integer.toString(Utils.max(intVal2, chatRoom.messageIds.size() + (chatRoom.isActive ? intVal2 : 0)))));
                    params2.addElement(StringUtils.intern(Integer.toString(1)));
                    params2.addElement(Storage.emptyStr);
                    Vector messageIdParams = ObjectPool.newVector();
                    Enumeration contactEnum = chatRoom.messageIds.elements();
                    while (contactEnum.hasMoreElements()) {
                        Hashtable hashtable = chatRoom.messages;
                        Object msgIdObj = contactEnum.nextElement();
                        if (hashtable.containsKey(msgIdObj)) {
                            messageIdParams.addElement(msgIdObj);
                        }
                    }
                    params2.addElement(messageIdParams);
                    request = ApiClient.createAuthRequest(ObjectPool.newBufferFromState(1050207).append('?').append(Storage.resources().getString(PackedStringKeys.PARAM_AJAX_CALL)).append(Storage.resources().getString(PackedStringKeys.FUNC_MAJAX_GET_MSGS)).append(Storage.state().getString(SessionKeys.SLOT_SESSION_HASH)).append(Storage.resources().getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                }
                MrimChatRoomManager.sendChatRoomRequest(request);
                return;
            case ScreenId.CHAT_ROOM_INVITE:
                NotificationHelper.showConfirmDialog(42, 862);
                Vector params4 = ObjectPool.newVector();
                JsonParser.addIntToVector(params4, Storage.state().getInt(ChatKeys.INT_ACTIVE_CHATROOM_ID));
                params4.addElement(Storage.state().getVector(UIKeys.SLOT_MEDIA_STREAM));
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(Storage.resources().getString(PackedStringKeys.URL_PATH_MAILBOX), ObjectPool.newStringBuffer().append(Storage.resources().getString(PackedStringKeys.PARAM_AJAX_CALL)).append(Storage.resources().getString(PackedStringKeys.FUNC_AJAX_MOVE_MSGS)).append(Storage.state().getString(SessionKeys.SLOT_SESSION_HASH)).append(Storage.resources().getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params4)))));
                return;
            case ScreenId.CHAT_ROOM_VIEW:
                MrimChatRoomManager.showChatRoomMessages();
                return;
            case ScreenId.CHAT_ROOM_CONFIG:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_ROOM_CONFIG));
                return;
            case ScreenId.CHAT_VIEW_MODE:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_VIEW_MODE));
                return;
            case ScreenId.CHAT_ROOM_CONTEXT:
                setupChatRoomContext();
                return;
            case ScreenId.CHAT_ROOM_ALERT:
                NotificationHelper.showAlertById(61, 857);
                return;
            case ScreenId.CHAT_ROOM_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_ROOM_OPTIONS));
                return;
            case ScreenId.CHAT_LIST_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_LIST_OPTIONS));
                return;
            case ScreenId.CREATE_CHAT_ROOM:
                Storage.state().setInt(ChatKeys.FLAG_CHAT_ROOM_CREATED, 0);
                Storage.state().setFromBuffer(ChatKeys.SLOT_CHAT_NAME, ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_CHAT_NAME_PREFIX)).append(1 + (Storage.state().getInt(SettingsKeys.UI_COUNTER) % CHAT_NAME_COUNTER_MODULO)));
                ScreenManager.showScreen(ContactListManager.buildContactListScreen(ScreenManager.createScreen(ScreenDef.CREATE_CHAT_ROOM), (MrimAccount) Storage.state().getAccount(), (Contact) null));
                return;
            case ScreenId.CHAT_STATUS:
                Storage.state().setBool(UIKeys.FLAG_STATUS_TEXT_SET, Utils.nonEmpty(Storage.state().getString(UIKeys.SLOT_STATUS_TEXT)));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_STATUS));
                return;
            case ScreenId.CHAT_DETAIL:
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_DETAIL));
                return;
            case ScreenId.CHAT_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_OPTIONS));
                return;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView currentScreen, MenuItem menuItem, String title, int action, Object obj) {
        int createResult;
        int configResult;
        switch (currentScreen.screenId) {
            case ScreenId.CHAT_ROOMS:
                return -1;
            case ScreenId.CHAT_ROOM_INIT:
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                return -1;
            case ScreenId.CHAT_ROOM_INVITE:
                return -1;
            case ScreenId.CHAT_ROOM_VIEW:
                Storage.state().setInt(ChatKeys.INT_SCROLL_OFFSET, currentScreen.scrollOffset);
                Storage.state().setObject(MapKeys.SLOT_MAP_POINT_2, (Object) title);
                Message selectedMsg = (Message) obj;
                Storage.state().setObject(RuntimeKeys.SLOT_MESSAGE_ID, (Object) (selectedMsg != null ? selectedMsg.from : null));
                return 0;
            case ScreenId.CHAT_ROOM_CONFIG:
                ScreenManager.processScreenForm();
                return (Storage.state().getInt(SettingsKeys.INT_EMOTICON_CONFIG_ACTION) != 4 || (configResult = ((MrimAccount) Storage.state().getAccount()).setConfiguration(((Storage.state().getInt(SettingsKeys.INT_EMOTICON_CONFIG_ID) - EMOTICON_CONFIG_BASE_OFFSET) << 8) + 4)) == 0) ? 0 : NotificationHelper.showError(configResult);
            case ScreenId.CHAT_VIEW_MODE:
                ScreenManager.processScreenForm();
                return 0;
            case ScreenId.CHAT_ROOM_CONTEXT:
                return handleChatRoomAction(title);
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenId.CHAT_ROOM_INVITE;
            case ScreenId.CHAT_ROOM_OPTIONS:
                return handleRoutePointOption(action);
            case ScreenId.CHAT_LIST_OPTIONS:
                return handleRouteListOption(action);
            case ScreenId.CREATE_CHAT_ROOM:
                ScreenManager.processScreenForm();
                String chatName = Utils.defaultStr(Storage.state().getString(ChatKeys.SLOT_CHAT_NAME));
                if (StringUtils.isEmpty(chatName)) {
                    createResult = NotificationHelper.showError(301);
                } else {
                    Vector checkedItems = ContactListManager.getCheckedItems(currentScreen, 3);
                    if (checkedItems.size() == 0) {
                        createResult = NotificationHelper.showError(775);
                    } else {
                        MrimAccount createAccount = (MrimAccount) Storage.state().getAccount();
                        boolean includeOwner = Storage.state().getBool(ChatKeys.FLAG_CHAT_ROOM_CREATED);
                        ByteBuffer buffer = new ByteBuffer();
                        int memberCount = checkedItems.size();
                        ByteBuffer membersBuf = buffer.writeIntLE(memberCount);
                        for (int mi = memberCount - 1; mi >= 0; mi--) {
                            membersBuf.writeStringLatin1((String) checkedItems.elementAt(mi));
                        }
                        ByteBuffer wrappedBuf = new ByteBuffer().writeBufferIntLen(membersBuf);
                        Object[] command = new Object[3];
                        command[0] = ProtocolFactory.createMrimPacket(createAccount, MrimCommand.CS_ADD_CONTACT, new ByteBuffer().writeIntLE(MRIM_CHATROOM_FLAGS).writeZeros(8).writeStringUTF16(chatName).writeZeros(12).writeBufferIntLen(includeOwner ? wrappedBuf.writeStringLatin1(createAccount.login) : wrappedBuf));
                        command[1] = ObjectPool.integerOf(15);
                        command[2] = chatName;
                        int sendResult = createAccount.trySendData(createAccount.createAndQueueCommand(command));
                        if (sendResult != 0) {
                            createResult = NotificationHelper.showError(sendResult);
                        } else {
                            Storage.state().addInt(SettingsKeys.UI_COUNTER, 1);
                            createResult = 0;
                        }
                    }
                }
                return createResult;
            case ScreenId.CHAT_STATUS:
                return handleChatInputAction(title);
            case ScreenId.CHAT_DETAIL:
                return handleMapViewOption(action);
            case ScreenId.CHAT_OPTIONS:
                return AppController.handleChatOption(action);
        }
        return 0;
    }

    public int onMenuItemAction(ListView currentScreen, MenuItem menuItem, Object data) {
        switch (currentScreen.screenId) {
            case ScreenId.CHAT_ROOMS:
                return 0;
            case ScreenId.CHAT_ROOM_INIT:
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                return 0;
            case ScreenId.CHAT_ROOM_INVITE:
                return 0;
            case ScreenId.CHAT_ROOM_VIEW:
                return 0;
            case ScreenId.CHAT_ROOM_CONFIG:
                return 0;
            case ScreenId.CHAT_VIEW_MODE:
                return 0;
            case ScreenId.CHAT_ROOM_CONTEXT:
                return 0;
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenId.CLOSE;
            case ScreenId.CHAT_ROOM_OPTIONS:
                return 0;
            case ScreenId.CHAT_LIST_OPTIONS:
                return 0;
            case ScreenId.CREATE_CHAT_ROOM:
                return 0;
            case ScreenId.CHAT_STATUS:
                return 0;
            case ScreenId.CHAT_DETAIL:
                return 0;
            case ScreenId.CHAT_OPTIONS:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.CHAT_ROOMS:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_MESSAGES:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_INVITE:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_CONTEXT:
                Storage.state().clearIndex(ChatKeys.SLOT_UNREAD_COUNT_TEXT);
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                Storage.state().clearIndex(ChatKeys.SLOT_CHAT_NAME);
                break;
        }
        clearScreenFlags();
    }

    private static void clearScreenFlags() {
        Storage.state().clearRange(ChatKeys.SCREEN_FLAGS_START, ChatKeys.SCREEN_FLAGS_END);
        Storage.state().setInt(ChatKeys.FLAG_CHAT_ROOM_CREATED, 0);
    }

    public int onItemSelected(ListView screen, MenuItem menuItem, String title, int selectedOption,
                              Object obj, Object obj2) {
        switch (screen.screenId) {
            case ScreenId.CHAT_ROOMS:
                return -1;
            case ScreenId.CHAT_ROOM_INIT:
                Storage.state().setInt(ChatKeys.INT_CHATROOM_ID, ((ChatRoom) obj).id);
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                return -1;
            case ScreenId.CHAT_ROOM_INVITE:
                return -1;
            case ScreenId.CHAT_ROOM_VIEW:
                Storage.state().setInt(ChatKeys.INT_SCROLL_OFFSET, screen.scrollOffset);
                Storage.state().setObject(MapKeys.SLOT_MAP_POINT_2, (Object) title);
                Message selectedMsg = (Message) obj;
                if (selectedMsg == null) {
                    return -1;
                }
                Storage.state().setObject(RuntimeKeys.SLOT_MESSAGE_ID, (Object) selectedMsg.from);
                ChatRoom chatRoom = ((MrimAccount) Storage.state().getAccount()).chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID));
                if (StringUtils.matchesKey(894, chatRoom.name) || StringUtils.matchesKey(899, chatRoom.name)) {
                    MailHelper.setMailAction(54, 3);
                } else {
                    MailHelper.setMailAction(52, 0);
                }
                return 0;
            case ScreenId.CHAT_ROOM_CONFIG:
                return 0;
            case ScreenId.CHAT_VIEW_MODE:
                return 0;
            case ScreenId.CHAT_ROOM_CONTEXT:
                handleChatRoomAction(title);
                return 0;
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenId.CHAT_ROOM_INVITE;
            case ScreenId.CHAT_ROOM_OPTIONS:
                return handleRoutePointOption(selectedOption);
            case ScreenId.CHAT_LIST_OPTIONS:
                return handleRouteListOption(selectedOption);
            case ScreenId.CREATE_CHAT_ROOM:
                return 0;
            case ScreenId.CHAT_STATUS:
                return handleChatInputAction(title);
            case ScreenId.CHAT_DETAIL:
                return handleMapViewOption(selectedOption);
            case ScreenId.CHAT_OPTIONS:
                return AppController.handleChatOption(selectedOption);
        }
        return 0;
    }

    public int onIdleProcess(ListView currentScreen, MenuItem menuItem, Object data, String title) {
        int result;
        Message movedMessage;
        switch (currentScreen.screenId) {
            case ScreenId.CHAT_ROOMS:
                Object[] roomsResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (roomsResult != null) {
                    int nextScreen = Storage.state().getInt(UIKeys.INT_SCREEN_ACTION);
                    int responseCode = ApiClient.validateJsonResponse(roomsResult);
                    if (responseCode != 0) {
                        result = responseCode;
                    } else {
                        ((MrimAccount) Storage.state().getAccount()).chatRoomManager.parseFromJson(ApiClient.getJsonPayload());
                        result = nextScreen;
                    }
                } else {
                    result = 0;
                }
                return result;
            case ScreenId.CHAT_ROOM_INIT:
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                Object[] msgsResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (msgsResult != null) {
                    int responseCode = ApiClient.validateJsonResponse(msgsResult);
                    if (responseCode != 0) {
                        result = responseCode;
                    } else {
                        Object payload = ApiClient.getJsonPayload();
                        MrimAccount msgsAccount = (MrimAccount) Storage.state().getAccount();
                        ChatRoom msgsChatRoom = msgsAccount.chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID));
                        if (msgsChatRoom != msgsAccount.chatRoomManager.getLast()) {
                            msgsChatRoom.subject = JsonParser.getStringValue(payload, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_LAST_MSG_ID));
                            msgsChatRoom.messageIds.removeAllElements();
                            Enumeration allIds = ((Vector) JsonParser.getValue(payload, Storage.resources().getString(PackedStringKeys.MAIL_PARAM_MLIST_ALL))).elements();
                            while (allIds.hasMoreElements()) {
                                msgsChatRoom.messageIds.addElement(allIds.nextElement());
                            }
                            Enumeration msgEntries = ((Vector) JsonParser.getValue(payload, Storage.resources().getString(PackedStringKeys.MAIL_PARAM_MLIST))).elements();
                            while (msgEntries.hasMoreElements()) {
                                Message msg = new Message((Hashtable) msgEntries.nextElement());
                                msgsChatRoom.messages.put(msg.from, msg);
                            }
                            Enumeration existingKeys = msgsChatRoom.messages.keys();
                            while (existingKeys.hasMoreElements()) {
                                String msgKey = (String) existingKeys.nextElement();
                                if (!msgsChatRoom.messageIds.contains(msgKey)) {
                                    msgsChatRoom.messages.remove(msgKey);
                                }
                            }
                            Enumeration readEntries = msgsChatRoom.readMessages.elements();
                            while (readEntries.hasMoreElements()) {
                                String readKey = (String) readEntries.nextElement();
                                if (!msgsChatRoom.messageIds.contains(readKey)) {
                                    msgsChatRoom.readMessages.removeElement(readKey);
                                }
                            }
                            msgsChatRoom.isInitialized = false;
                        } else {
                            Enumeration newMsgs = ((Vector) payload).elements();
                            while (newMsgs.hasMoreElements()) {
                                Message msg = new Message((Hashtable) newMsgs.nextElement());
                                msgsChatRoom.messages.put(msg.from, msg);
                            }
                        }
                        result = ScreenId.CHAT_ROOM_VIEW;
                    }
                } else {
                    result = 0;
                }
                return result;
            case ScreenId.CHAT_ROOM_INVITE:
                Object[] inviteResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (inviteResult != null) {
                    int responseCode = ApiClient.validateJsonResponse(inviteResult);
                    if (responseCode != 0) {
                        result = responseCode;
                    } else {
                        Object payload = ApiClient.getJsonPayload();
                        MrimAccount inviteAccount = (MrimAccount) Storage.state().getAccount();
                        int entryCount = ((Vector) payload).size();
                        for (int ei = 0; ei < entryCount; ei++) {
                            Enumeration entryKeys = ((Hashtable) JsonParser.getVectorElement(payload, ei)).keys();
                            while (entryKeys.hasMoreElements()) {
                                String msgId = (String) entryKeys.nextElement();
                                ChatRoom sourceChatRoom = inviteAccount.chatRoomManager.findByName(msgId);
                                ChatRoom targetChatRoom = inviteAccount.chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_ACTIVE_CHATROOM_ID));
                                if (sourceChatRoom != null && (movedMessage = sourceChatRoom.getMessage(msgId)) != null && targetChatRoom != null) {
                                    if (movedMessage.hasFlag(4)) {
                                        if (targetChatRoom == inviteAccount.chatRoomManager.findDefault()) {
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
                                    inviteAccount.chatRoomManager.removeUser(msgId);
                                    targetChatRoom.setActive(false);
                                }
                            }
                        }
                        result = ScreenId.CHAT_ROOM_VIEW;
                    }
                } else {
                    result = 0;
                }
                return result;
            case ScreenId.CHAT_ROOM_VIEW:
                Storage.state().setInt(ChatKeys.INT_SCROLL_OFFSET, currentScreen.scrollOffset);
                Storage.state().setObject(MapKeys.SLOT_MAP_POINT_2, (Object) title);
                MrimAccount viewAccount = (MrimAccount) Storage.state().getAccount();
                ChatRoom viewChatRoom = viewAccount.chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID));
                if (title == null || viewChatRoom == viewAccount.chatRoomManager.getLast() || title.equals(viewChatRoom.subject)) {
                    return 0;
                } else {
                    Object lastMessage = null;
                    Enumeration idEnum = viewChatRoom.messageIds.elements();
                    while (idEnum.hasMoreElements()) {
                        Object msgIdObj = idEnum.nextElement();
                        if (viewChatRoom.messages.containsKey(msgIdObj)) {
                            lastMessage = viewChatRoom.messages.get(msgIdObj);
                        }
                    }
                    if (title == (lastMessage != null ? ((Message) lastMessage).from : null)) {
                        viewChatRoom.setActive(true);
                        return 41;
                    }
                    return 0;
                }
            case ScreenId.CHAT_ROOM_CONFIG:
                return 0;
            case ScreenId.CHAT_VIEW_MODE:
                return 0;
            case ScreenId.CHAT_ROOM_CONTEXT:
                return 0;
            case ScreenId.CHAT_ROOM_ALERT:
                return 0;
            case ScreenId.CHAT_ROOM_OPTIONS:
                return 0;
            case ScreenId.CHAT_LIST_OPTIONS:
                return 0;
            case ScreenId.CREATE_CHAT_ROOM:
                return 0;
            case ScreenId.CHAT_STATUS:
                return 0;
            case ScreenId.CHAT_DETAIL:
                return 0;
            case ScreenId.CHAT_OPTIONS:
                return 0;
        }
        return 0;
    }

    private static void setupChatRoomContext() {
        String messageId = Storage.state().getString(RuntimeKeys.SLOT_MESSAGE_ID);
        int chatRoomId = Storage.state().getInt(ChatKeys.INT_CHATROOM_ID);
        MrimAccount mrimAccount = (MrimAccount) Storage.state().getAccount();
        boolean hasMessage = messageId != null;
        Storage.state().setBool(ChatKeys.FLAG_IS_CHATROOM, hasMessage);
        ChatRoom chatRoom = mrimAccount.chatRoomManager.findById(chatRoomId);
        boolean isMarkedRead = chatRoom.isMessageRead(messageId);
        Storage.state().setBool(ChatKeys.FLAG_MSG_READ_SELECTED, hasMessage && isMarkedRead);
        Storage.state().setBool(ChatKeys.FLAG_MSG_UNREAD_SELECTED, hasMessage && !isMarkedRead);
        Message message = chatRoom.getMessage(messageId);
        Storage.state().setBool(ChatKeys.FLAG_MSG_UNREAD, hasMessage && !message.isRead());
        Storage.state().setBool(ChatKeys.FLAG_MSG_READ, hasMessage && message.isRead());
        int readCount = chatRoom.readMessages.size();
        Storage.state().setBool(ChatKeys.FLAG_CHATROOM_HAS_MEMBERS, readCount != 0);
        Storage.state().setBool(ChatKeys.FLAG_CHATROOM_HAS_MORE, chatRoom != mrimAccount.chatRoomManager.getLast());
        Storage.state().setFromBuffer(ChatKeys.SLOT_UNREAD_COUNT_TEXT,
            ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_UNREAD_COUNT_PREFIX))
                .append(readCount).append(')'));
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_ROOM_CONTEXT));
    }

    public static int handleRoutePointOption(int optionId) {
        if (optionId == 0) {
            MapController.setRouteStart();
            if (MmpContact.hasSecondToken()) {
                return ScreenId.MAP;
            }
            Storage.state().setInt(MapKeys.FLAG_MAP_MODE_ACTIVE, 1);
            return ScreenId.MAP_SEARCH;
        }
        MapController.setRouteEnd();
        if (MmpContact.hasFirstToken()) {
            return ScreenId.MAP;
        }
        Storage.state().setInt(MapKeys.FLAG_MAP_MODE_ACTIVE, 0);
        return ScreenId.MAP_SEARCH;
    }

    public static int handleRouteListOption(int optionId) {
        MapPoint mapPoint = MapController.pendingMapPoint;
        if (mapPoint == null) {
            return NotificationHelper.showError(354);
        }
        if (optionId == 6) {
            MapRenderer.navigateToMapPoint(MapController.pendingMapPoint);
            return 0;
        }
        if (optionId == 118) {
            Storage.state().setObject(MapKeys.MAP_RESOURCE_URL, (Object) mapPoint.getResourceUrl());
            return 0;
        }
        if (optionId != 120) {
            return 0;
        }
        MapController.removeRoutePoint(mapPoint);
        return 0;
    }

    public static int handleChatInputAction(String str) {
        String messageText = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_STATUS_TEXT));
        if (str != Storage.resources().getString(StringResKeys.STR_NOTIFICATION_SOUND)) {
            StringBuffer sb = Utils.getMessageBuffer();
            if (StringUtils.matchesKey(473, str)) {
                Storage.state().setFromBuffer(UIKeys.SLOT_STATUS_TEXT, sb.append(Storage.state().getString(UIKeys.SLOT_NOTIFICATION_TEXT)));
                return 0;
            }
            if (StringUtils.matchesKey(474, str)) {
                Storage.state().setObject(UIKeys.SLOT_NOTIFICATION_TEXT, (Object) messageText);
                Storage.state().setBool(UIKeys.FLAG_RESOURCE_LOADING, true);
                return 0;
            }
            if (!StringUtils.matchesKey(476, str)) {
                return 0;
            }
            Storage.state().setObject(UIKeys.SLOT_STATUS_TEXT, (Object) Conversation.transliterateRussian(messageText));
            return 0;
        }
        String phoneNumber = Storage.state().getString(ContactKeys.SLOT_SELECTED_GROUP);
        MrimContact mrimContact = (MrimContact) Storage.state().getObject(ContactKeys.SLOT_CURRENT_ENTITY);
        MrimAccount mrimAccount = (MrimAccount) mrimContact.account;
        int errorCode;
        if (mrimAccount.isConnected()) {
            mrimContact.appendMessage(1, ObjectPool.toStringAndRelease(Utils.appendColon(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_FILE_TRANSFER_PREFIX)).append(Utils.formatPhone(phoneNumber))).append(messageText)), 0L, 0L);
            StringBuffer phoneSb = ObjectPool.newStringBuffer().append('+');
            if (phoneNumber.charAt(0) == '8') {
                phoneSb.append('7').append(StringUtils.suffix(phoneNumber, 1));
            } else {
                phoneSb.append(phoneNumber);
            }
            errorCode = mrimAccount.trySendData(mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_MESSAGE_EXT, new ByteBuffer().writeIntLE(0).writeStringLatin1(ObjectPool.toStringAndRelease(phoneSb)).writeStringUTF16(messageText)), ObjectPool.integerOf(MrimAccount.RESP_XMPP_SERVICE), mrimContact, messageText, phoneNumber}));
        } else {
            errorCode = 299;
        }
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return 0;
    }

    public static int handleChatRoomAction(String str) {
        String messageId = Storage.state().getString(RuntimeKeys.SLOT_MESSAGE_ID);
        int chatRoomId = Storage.state().getInt(ChatKeys.INT_CHATROOM_ID);
        MrimAccount mrimAccount = (MrimAccount) Storage.state().getAccount();
        ChatRoom chatRoom = mrimAccount.chatRoomManager.findById(chatRoomId);
        if (StringUtils.matchesKey(848, str)) {
            chatRoom.readMessages.addElement(messageId);
            return 0;
        }
        if (StringUtils.matchesKey(847, str)) {
            chatRoom.markMessageRead(messageId);
            return 0;
        }
        if (StringUtils.matchesKey(846, str)) {
            ScreenBuilder.onScreenClosed();
            MailHelper.composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (StringUtils.matchesKey(1347, str)) {
            IOUtils.setSelectedItems(chatRoom.readMessages);
            return 0;
        }
        if (StringUtils.matchesKey(1061, str)) {
            ScreenBuilder.onScreenClosed();
            AppController.toggleOnlineMode(false);
            return 0;
        }
        if (!StringUtils.matchesKey(851, str)) {
            return 0;
        }
        Storage.state().setInt(ChatKeys.INT_SCROLL_OFFSET, 0);
        Storage.state().clearIndex(MapKeys.SLOT_MAP_POINT_2);
        mrimAccount.chatRoomManager.loaded = true;
        chatRoom.setActive(false);
        Storage.state().setInt(UIKeys.INT_SCREEN_ACTION, 41);
        return 0;
    }

    public static int handleMapViewOption(int optionId) {
        MapController.showMapView();
        if (optionId == 6) {
            MapController.applyViewMode(true, false, !Storage.state().getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE));
            Storage.state().setInt(ContactKeys.FLAG_REFRESH_CONTACTS, 1);
            Storage.state().setInt(MapKeys.FLAG_MAP_LOADING, 1);
            return 0;
        }
        if (optionId == 100) {
            Storage.state().setInt(UIKeys.FLAG_LOADING, 1);
            return 0;
        }
        if (!MapController.hasRoutePoints()) {
            return NotificationHelper.showError(354);
        }
        Storage.state().setInt(ContactKeys.FLAG_CONTACTS_LOADED, 1);
        return 0;
    }
}
