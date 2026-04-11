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
                MrimAccount mrimAccount2 = (MrimAccount) Storage.state().getAccount();
                Vector params2 = ObjectPool.newVector();
                if (chatRoom == mrimAccount2.chatRoomManager.getLast()) {
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
                String msgId2 = Storage.state().getString(RuntimeKeys.SLOT_MESSAGE_ID);
                int chatRoomId = Storage.state().getInt(ChatKeys.INT_CHATROOM_ID);
                MrimAccount mrimAccount3 = (MrimAccount) Storage.state().getAccount();
                boolean z5 = msgId2 != null;
                boolean z6 = z5;
                Storage.state().setBool(ChatKeys.FLAG_IS_CHATROOM, z5);
                ChatRoom chatRoom2 = mrimAccount3.chatRoomManager.findById(chatRoomId);
                boolean isRead = chatRoom2.isMessageRead(msgId2);
                Storage.state().setBool(ChatKeys.FLAG_MSG_READ_SELECTED, z6 && isRead);
                Storage.state().setBool(ChatKeys.FLAG_MSG_UNREAD_SELECTED, z6 && !isRead);
                Message message3 = chatRoom2.getMessage(msgId2);
                Storage.state().setBool(ChatKeys.FLAG_MSG_UNREAD, z6 && !message3.isRead());
                Storage.state().setBool(ChatKeys.FLAG_MSG_READ, z6 && message3.isRead());
                int size5 = chatRoom2.readMessages.size();
                Storage.state().setBool(ChatKeys.FLAG_CHATROOM_HAS_MEMBERS, size5 != 0);
                Storage.state().setBool(ChatKeys.FLAG_CHATROOM_HAS_MORE, chatRoom2 != mrimAccount3.chatRoomManager.getLast());
                Storage.state().setFromBuffer(ChatKeys.SLOT_UNREAD_COUNT_TEXT, ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_UNREAD_COUNT_PREFIX)).append(size5).append(')'));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CHAT_ROOM_CONTEXT));
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
        int errorCode4;
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
                Message message = (Message) obj;
                Storage.state().setObject(RuntimeKeys.SLOT_MESSAGE_ID, (Object) (message != null ? message.from : null));
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
                    errorCode4 = NotificationHelper.showError(301);
                } else {
                    Vector checkedItems = ContactListManager.getCheckedItems(currentScreen, 3);
                    if (checkedItems.size() == 0) {
                        errorCode4 = NotificationHelper.showError(775);
                    } else {
                        MrimAccount mrimAccount3 = (MrimAccount) Storage.state().getAccount();
                        boolean flag2 = Storage.state().getBool(ChatKeys.FLAG_CHAT_ROOM_CREATED);
                        ByteBuffer buffer = new ByteBuffer();
                        int size = checkedItems.size();
                        ByteBuffer membersBuf = buffer.writeIntLE(size);
                        for (int i5 = size - 1; i5 >= 0; i5--) {
                            membersBuf.writeStringLatin1((String) checkedItems.elementAt(i5));
                        }
                        ByteBuffer wrappedBuf = new ByteBuffer().writeBufferIntLen(membersBuf);
                        Object[] objArr2 = new Object[3];
                        objArr2[0] = ProtocolFactory.createMrimPacket(mrimAccount3, MrimCommand.CS_ADD_CONTACT, new ByteBuffer().writeIntLE(MRIM_CHATROOM_FLAGS).writeZeros(8).writeStringUTF16(chatName).writeZeros(12).writeBufferIntLen(flag2 ? wrappedBuf.writeStringLatin1(mrimAccount3.login) : wrappedBuf));
                        objArr2[1] = ObjectPool.integerOf(15);
                        objArr2[2] = chatName;
                        int sendResult3 = mrimAccount3.trySendData(mrimAccount3.createAndQueueCommand(objArr2));
                        if (sendResult3 != 0) {
                            errorCode4 = NotificationHelper.showError(sendResult3);
                        } else {
                            Storage.state().addInt(SettingsKeys.UI_COUNTER, 1);
                            errorCode4 = 0;
                        }
                    }
                }
                return errorCode4;
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
        int i;
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
                Message msg = (Message) obj;
                if (msg == null) {
                    i = -1;
                } else {
                    Storage.state().setObject(RuntimeKeys.SLOT_MESSAGE_ID, (Object) msg.from);
                    ChatRoom chatRoom = ((MrimAccount) Storage.state().getAccount()).chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID));
                    if (StringUtils.matchesKey(894, chatRoom.name) || StringUtils.matchesKey(899, chatRoom.name)) {
                        MailHelper.setMailAction(54, 3);
                    } else {
                        MailHelper.setMailAction(52, 0);
                    }
                    i = 0;
                }
                return i;
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
        int action;
        Message message2;
        MrimAccount mrimAccount;
        ChatRoom chatRoom;
        switch (currentScreen.screenId) {
            case ScreenId.CHAT_ROOMS:
                Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (asyncResult != null) {
                    int stateInt4 = Storage.state().getInt(UIKeys.INT_SCREEN_ACTION);
                    int responseCode = ApiClient.validateJsonResponse(asyncResult);
                    if (responseCode != 0) {
                        action = responseCode;
                    } else {
                        ((MrimAccount) Storage.state().getAccount()).chatRoomManager.parseFromJson(ApiClient.getJsonPayload());
                        action = stateInt4;
                    }
                } else {
                    action = 0;
                }
                return action;
            case ScreenId.CHAT_ROOM_INIT:
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                Object[] asyncResult2 = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (asyncResult2 != null) {
                    int responseCode2 = ApiClient.validateJsonResponse(asyncResult2);
                    if (responseCode2 != 0) {
                        action = responseCode2;
                    } else {
                        Object payload = ApiClient.getJsonPayload();
                        MrimAccount mrimAccount2 = (MrimAccount) Storage.state().getAccount();
                        ChatRoom chatRoom2 = mrimAccount2.chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID));
                        if (chatRoom2 != mrimAccount2.chatRoomManager.getLast()) {
                            chatRoom2.subject = JsonParser.getStringValue(payload, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_LAST_MSG_ID));
                            chatRoom2.messageIds.removeAllElements();
                            Enumeration enumerationElements = ((Vector) JsonParser.getValue(payload, Storage.resources().getString(PackedStringKeys.MAIL_PARAM_MLIST_ALL))).elements();
                            while (enumerationElements.hasMoreElements()) {
                                chatRoom2.messageIds.addElement(enumerationElements.nextElement());
                            }
                            Enumeration enumerationElements2 = ((Vector) JsonParser.getValue(payload, Storage.resources().getString(PackedStringKeys.MAIL_PARAM_MLIST))).elements();
                            while (enumerationElements2.hasMoreElements()) {
                                Message msg = new Message((Hashtable) enumerationElements2.nextElement());
                                chatRoom2.messages.put(msg.from, msg);
                            }
                            Enumeration keys = chatRoom2.messages.keys();
                            while (keys.hasMoreElements()) {
                                String str3 = (String) keys.nextElement();
                                if (!chatRoom2.messageIds.contains(str3)) {
                                    chatRoom2.messages.remove(str3);
                                }
                            }
                            Enumeration enumerationElements3 = chatRoom2.readMessages.elements();
                            while (enumerationElements3.hasMoreElements()) {
                                String str4 = (String) enumerationElements3.nextElement();
                                if (!chatRoom2.messageIds.contains(str4)) {
                                    chatRoom2.readMessages.removeElement(str4);
                                }
                            }
                            chatRoom2.isInitialized = false;
                        } else {
                            Enumeration enumerationElements4 = ((Vector) payload).elements();
                            while (enumerationElements4.hasMoreElements()) {
                                Message msg2 = new Message((Hashtable) enumerationElements4.nextElement());
                                chatRoom2.messages.put(msg2.from, msg2);
                            }
                        }
                        action = ScreenId.CHAT_ROOM_VIEW;
                    }
                } else {
                    action = 0;
                }
                return action;
            case ScreenId.CHAT_ROOM_INVITE:
                Object[] asyncResult3 = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (asyncResult3 != null) {
                    int responseCode3 = ApiClient.validateJsonResponse(asyncResult3);
                    if (responseCode3 != 0) {
                        action = responseCode3;
                    } else {
                        Object payload2 = ApiClient.getJsonPayload();
                        MrimAccount mrimAccount3 = (MrimAccount) Storage.state().getAccount();
                        int size4 = ((Vector) payload2).size();
                        for (int i6 = 0; i6 < size4; i6++) {
                            Enumeration keys2 = ((Hashtable) JsonParser.getVectorElement(payload2, i6)).keys();
                            while (keys2.hasMoreElements()) {
                                String str5 = (String) keys2.nextElement();
                                ChatRoom selectedChatRoom = mrimAccount3.chatRoomManager.findByName(str5);
                                ChatRoom chatRoom3 = mrimAccount3.chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_ACTIVE_CHATROOM_ID));
                                if (selectedChatRoom != null && (message2 = selectedChatRoom.getMessage(str5)) != null && chatRoom3 != null) {
                                    if (message2.hasFlag(4)) {
                                        if (chatRoom3 == mrimAccount3.chatRoomManager.findDefault()) {
                                            message2.setFlag(4, false);
                                        }
                                        selectedChatRoom.decrementUnread();
                                    }
                                    selectedChatRoom.decrementMembers();
                                    if (!message2.isRead()) {
                                        chatRoom3.incrementUnread();
                                    }
                                    chatRoom3.memberCount++;
                                }
                                if (selectedChatRoom != chatRoom3) {
                                    mrimAccount3.chatRoomManager.removeUser(str5);
                                    chatRoom3.setActive(false);
                                }
                            }
                        }
                        action = ScreenId.CHAT_ROOM_VIEW;
                    }
                } else {
                    action = 0;
                }
                return action;
            case ScreenId.CHAT_ROOM_VIEW:
                Storage.state().setInt(ChatKeys.INT_SCROLL_OFFSET, currentScreen.scrollOffset);
                Storage.state().setObject(MapKeys.SLOT_MAP_POINT_2, (Object) title);
                if (title == null || (chatRoom = (mrimAccount = (MrimAccount) Storage.state().getAccount()).chatRoomManager.findById(Storage.state().getInt(ChatKeys.INT_CHATROOM_ID))) == mrimAccount.chatRoomManager.getLast() || title.equals(chatRoom.subject)) {
                    return 0;
                } else {
                    Object obj3 = null;
                    Enumeration enumerationElements5 = chatRoom.messageIds.elements();
                    while (enumerationElements5.hasMoreElements()) {
                        Hashtable hashtable = chatRoom.messages;
                        Object objNextElement = enumerationElements5.nextElement();
                        if (hashtable.containsKey(objNextElement)) {
                            obj3 = chatRoom.messages.get(objNextElement);
                        }
                    }
                    if (title == (obj3 != null ? ((Message) obj3).from : null)) {
                        chatRoom.setActive(true);
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

    public static final int handleRoutePointOption(int optionId) {
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

    public static final int handleRouteListOption(int optionId) {
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

    public static final int handleChatInputAction(String str) {
        int errorCode;
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
        int i = errorCode;
        if (errorCode != 0) {
            return NotificationHelper.showError(i);
        }
        return 0;
    }

    public static final int handleChatRoomAction(String str) {
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

    public static final int handleMapViewOption(int optionId) {
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
