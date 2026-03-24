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


    public void buildScreen(int screenId) {
        Object[] request;
        switch (screenId) {
            case ScreenId.CHAT_ROOMS:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
                if (mrimAccount.getChatRoomCount() != 0 && !mrimAccount.chatRoomsLoaded) {
                    AppController.initChatRoomList();
                    return;
                }
                NotificationHelper.showConfirmDialog(37, 833);
                Vector params = ObjectPool.newVector();
                JsonParser.addIntToVector(params, 0);
                params.addElement(AppState.emptyStr);
                JsonParser.addIntToVector(params, 1);
                IOUtils.sendChatRoomRequest(ApiClient.createAuthRequest(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_LONG_URL_1)).append('?').append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_HUGE_URL_1)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
                return;
            case ScreenId.CHAT_ROOM_INIT:
                AppController.initChatRoomList();
                return;
            case ScreenId.CHAT_ROOM_MESSAGES:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID));
                if (!chatRoom.isInitialized) {
                    IOUtils.showChatRoomMessages();
                    return;
                }
                NotificationHelper.showConfirmDialog(41, 836);
                MrimAccount mrimAccount2 = (MrimAccount) AppState.getAccount();
                Vector params2 = ObjectPool.newVector();
                if (chatRoom == mrimAccount2.getLastChatRoom()) {
                    params2.addElement(StringUtils.intern(Integer.toString(0)));
                    params2.addElement(chatRoom.participants);
                    request = ApiClient.createUploadRequest(AppState.getString(StateKeys.STR_RES_LONG_URL_1), ObjectPool.newBufferFromState(722608).append(AppState.getString(StateKeys.STR_RES_HUGE_URL_7)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                } else {
                    params2.addElement(StringUtils.intern(Integer.toString(chatRoom.id)));
                    int intVal2 = AppState.getInt(StateKeys.SETTING_TIMEOUT_VALUE);
                    params2.addElement(StringUtils.intern(Integer.toString(Utils.max(intVal2, chatRoom.messageIds.size() + (chatRoom.isActive ? intVal2 : 0)))));
                    params2.addElement(StringUtils.intern(Integer.toString(1)));
                    params2.addElement(AppState.emptyStr);
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
                    request = ApiClient.createAuthRequest(ObjectPool.newBufferFromState(1050207).append('?').append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_5)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                }
                IOUtils.sendChatRoomRequest(request);
                return;
            case ScreenId.CHAT_ROOM_INVITE:
                NotificationHelper.showConfirmDialog(42, 862);
                Vector params4 = ObjectPool.newVector();
                JsonParser.addIntToVector(params4, AppState.getInt(StateKeys.INT_ACTIVE_CHATROOM_ID));
                params4.addElement(AppState.getVector(StateKeys.SLOT_MEDIA_STREAM));
                IOUtils.sendChatRoomRequest(ApiClient.createUploadRequest(AppState.getString(StateKeys.STR_RES_LONG_URL_1), ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_4)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params4)))));
                return;
            case ScreenId.CHAT_ROOM_VIEW:
                IOUtils.showChatRoomMessages();
                return;
            case ScreenId.CHAT_ROOM_CONFIG:
                ScreenManager.showScreen(ScreenManager.createScreen(4302));
                return;
            case ScreenId.CHAT_VIEW_MODE:
                ScreenManager.showScreen(ScreenManager.createScreen(3170));
                return;
            case ScreenId.CHAT_ROOM_CONTEXT:
                String msgId2 = AppState.getString(StateKeys.SLOT_MESSAGE_ID);
                int chatRoomId = AppState.getInt(StateKeys.INT_CHATROOM_ID);
                MrimAccount mrimAccount3 = (MrimAccount) AppState.getAccount();
                boolean z5 = msgId2 != null;
                boolean z6 = z5;
                AppState.setBool(StateKeys.FLAG_IS_CHATROOM, z5);
                ChatRoom chatRoom2 = mrimAccount3.findChatRoomById(chatRoomId);
                boolean isRead = chatRoom2.isMessageRead(msgId2);
                AppState.setBool(StateKeys.FLAG_MSG_READ_SELECTED, z6 && isRead);
                AppState.setBool(StateKeys.FLAG_MSG_UNREAD_SELECTED, z6 && !isRead);
                Message message3 = chatRoom2.getMessage(msgId2);
                AppState.setBool(StateKeys.FLAG_MSG_UNREAD, z6 && !message3.isRead());
                AppState.setBool(StateKeys.FLAG_MSG_READ, z6 && message3.isRead());
                int size5 = chatRoom2.readMessages.size();
                AppState.setBool(StateKeys.FLAG_CHATROOM_HAS_MEMBERS, size5 != 0);
                AppState.setBool(StateKeys.FLAG_CHATROOM_HAS_MORE, chatRoom2 != mrimAccount3.getLastChatRoom());
                AppState.setFromBuffer(StateKeys.SLOT_UNREAD_COUNT_TEXT, ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_UNREAD_COUNT_PREFIX)).append(size5).append(')'));
                ScreenManager.showScreen(ScreenManager.createScreen(4589));
                return;
            case ScreenId.CHAT_ROOM_ALERT:
                NotificationHelper.showAlertById(61, 857);
                return;
            case ScreenId.CHAT_ROOM_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(1940));
                return;
            case ScreenId.CHAT_LIST_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(1968));
                return;
            case ScreenId.CREATE_CHAT_ROOM:
                AppState.setInt(StateKeys.FLAG_CHAT_ROOM_CREATED, 0);
                AppState.setFromBuffer(StateKeys.SLOT_CHAT_NAME, ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_CHAT_NAME_PREFIX)).append(1 + (AppState.getInt(StateKeys.UI_COUNTER) % 1000)));
                ScreenManager.showScreen(IOUtils.buildContactListScreen(ScreenManager.createScreen(4138), (MrimAccount) AppState.getAccount(), (Contact) null));
                return;
            case ScreenId.CHAT_STATUS:
                AppState.setBool(StateKeys.FLAG_STATUS_TEXT_SET, Utils.nonEmpty(AppState.getString(StateKeys.SLOT_STATUS_TEXT)));
                ScreenManager.showScreen(ScreenManager.createScreen(3647));
                return;
            case ScreenId.CHAT_DETAIL:
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(4270));
                return;
            case ScreenId.CHAT_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(4179));
                return;
        }
        AppController.finishScreenBuild();
    }

    public int onMenuItemSelected(Screen currentScreen, MenuItem menuItem, String title, int action, Object obj) {
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
                AppState.setInt(StateKeys.INT_SCROLL_OFFSET, currentScreen.scrollOffset);
                AppState.setObject(StateKeys.SLOT_MAP_POINT_2, (Object) title);
                Message message = (Message) obj;
                AppState.setObject(StateKeys.SLOT_MESSAGE_ID, (Object) (message != null ? message.from : null));
                return 0;
            case ScreenId.CHAT_ROOM_CONFIG:
                ScreenManager.processScreenForm();
                return (AppState.getInt(StateKeys.INT_SETTINGS_ACTION) != 4 || 0 == (configResult = ((MrimAccount) AppState.getAccount()).setConfiguration(((AppState.getInt(StateKeys.INT_SETTINGS_THEME) - 157) << 8) + 4))) ? 0 : NotificationHelper.showError(configResult);
            case ScreenId.CHAT_VIEW_MODE:
                ScreenManager.processScreenForm();
                return 0;
            case ScreenId.CHAT_ROOM_CONTEXT:
                return ResourceManager.handleChatRoomAction(title);
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenId.CHAT_ROOM_INVITE;
            case ScreenId.CHAT_ROOM_OPTIONS:
                return AppController.handleChatRoomOption(action);
            case ScreenId.CHAT_LIST_OPTIONS:
                return AppController.handleChatListOption(action);
            case ScreenId.CREATE_CHAT_ROOM:
                ScreenManager.processScreenForm();
                String chatName = Utils.defaultStr(AppState.getString(StateKeys.SLOT_CHAT_NAME));
                if (StringUtils.isEmpty(chatName)) {
                    errorCode4 = NotificationHelper.showError(301);
                } else {
                    Vector checkedItems = IOUtils.getCheckedItems(currentScreen, 3);
                    if (checkedItems.size() == 0) {
                        errorCode4 = NotificationHelper.showError(775);
                    } else {
                        MrimAccount mrimAccount3 = (MrimAccount) AppState.getAccount();
                        boolean flag2 = AppState.getBool(StateKeys.FLAG_CHAT_ROOM_CREATED);
                        ByteBuffer buffer = new ByteBuffer();
                        int size = checkedItems.size();
                        int i5 = size;
                        ByteBuffer membersBuf = buffer.writeIntLE(size);
                        while (true) {
                            i5--;
                            if (i5 < 0) {
                                ByteBuffer wrappedBuf = new ByteBuffer().writeBufferIntLen(membersBuf);
                                Object[] objArr2 = new Object[3];
                                objArr2[0] = ProtocolFactory.createMrimPacket(mrimAccount3, 4121, new ByteBuffer().writeIntLE(128).writeZeros(8).writeStringUTF16(chatName).writeZeros(12).writeBufferIntLen(flag2 ? wrappedBuf.writeStringLatin1(mrimAccount3.login) : wrappedBuf));
                                objArr2[1] = ResourceManager.integerOf(15);
                                objArr2[2] = chatName;
                                int sendResult3 = mrimAccount3.trySendData(mrimAccount3.createAndQueueCommand(objArr2));
                                if (0 != sendResult3) {
                                    errorCode4 = NotificationHelper.showError(sendResult3);
                                } else {
                                    AppState.addInt(StateKeys.UI_COUNTER, 1);
                                    errorCode4 = 0;
                                }
                            } else {
                                membersBuf.writeStringLatin1((String) checkedItems.elementAt(i5));
                            }
                        }
                    }
                }
                return errorCode4;
            case ScreenId.CHAT_STATUS:
                return ResourceManager.handleChatInputAction(title);
            case ScreenId.CHAT_DETAIL:
                return AppController.handleChatDetailOption(action);
            case ScreenId.CHAT_OPTIONS:
                return AppController.handleChatOption(action);
        }
        return 0;
    }

    public int onMenuItemAction(Screen currentScreen, MenuItem menuItem, Object data) {
        switch (currentScreen.screenId) {
            case ScreenId.CHAT_ROOMS:
                return 0;
            case ScreenId.CHAT_ROOM_INIT:
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
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

    public void onScreenClosed(Screen screen) {
        switch (screen.screenId) {
            case ScreenId.CHAT_ROOMS:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_MESSAGES:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_INVITE:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_CONTEXT:
                AppState.clearIndex(StateKeys.SLOT_UNREAD_COUNT_TEXT);
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                AppState.clearIndex(StateKeys.SLOT_CHAT_NAME);
                break;
        }
    }

    public int onItemSelected(Screen screen, MenuItem menuItem, String title, int selectedOption,
                              Object obj, Object obj2) {
        int i;
        switch (screen.screenId) {
            case ScreenId.CHAT_ROOMS:
                return -1;
            case ScreenId.CHAT_ROOM_INIT:
                AppState.setInt(StateKeys.INT_CHATROOM_ID, ((ChatRoom) obj).id);
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                return -1;
            case ScreenId.CHAT_ROOM_INVITE:
                return -1;
            case ScreenId.CHAT_ROOM_VIEW:
                AppState.setInt(StateKeys.INT_SCROLL_OFFSET, screen.scrollOffset);
                AppState.setObject(StateKeys.SLOT_MAP_POINT_2, (Object) title);
                Message msg = (Message) obj;
                if (msg == null) {
                    i = -1;
                } else {
                    AppState.setObject(StateKeys.SLOT_MESSAGE_ID, (Object) msg.from);
                    ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID));
                    if (StringUtils.matchesKey(894, chatRoom.name) || StringUtils.matchesKey(899, chatRoom.name)) {
                        XmppMailRuProtocol.setMailAction(54, 3);
                    } else {
                        XmppMailRuProtocol.setMailAction(52, 0);
                    }
                    i = 0;
                }
                return i;
            case ScreenId.CHAT_ROOM_CONFIG:
                return 0;
            case ScreenId.CHAT_VIEW_MODE:
                return 0;
            case ScreenId.CHAT_ROOM_CONTEXT:
                ResourceManager.handleChatRoomAction(title);
                return 0;
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenId.CHAT_ROOM_INVITE;
            case ScreenId.CHAT_ROOM_OPTIONS:
                return AppController.handleChatRoomOption(selectedOption);
            case ScreenId.CHAT_LIST_OPTIONS:
                return AppController.handleChatListOption(selectedOption);
            case ScreenId.CREATE_CHAT_ROOM:
                return 0;
            case ScreenId.CHAT_STATUS:
                return ResourceManager.handleChatInputAction(title);
            case ScreenId.CHAT_DETAIL:
                return AppController.handleChatDetailOption(selectedOption);
            case ScreenId.CHAT_OPTIONS:
                return AppController.handleChatOption(selectedOption);
        }
        return 0;
    }

    public int onIdleProcess(Screen currentScreen, MenuItem menuItem, Object data, String title) {
        int action;
        Message message2;
        MrimAccount mrimAccount;
        ChatRoom chatRoom;
        switch (currentScreen.screenId) {
            case ScreenId.CHAT_ROOMS:
                Object[] asyncResult = ApiClient.getAsyncResult(IOUtils.pollAsyncResult());
                if (asyncResult != null) {
                    int stateInt4 = AppState.getInt(StateKeys.INT_SCREEN_ACTION);
                    int responseCode = IOUtils.validateJsonResponse(asyncResult);
                    if (responseCode != 0) {
                        action = responseCode;
                    } else {
                        ((MrimAccount) AppState.getAccount()).parseChatRoomsFromJson(IOUtils.getJsonPayload());
                        action = stateInt4;
                    }
                } else {
                    action = 0;
                }
                return action;
            case ScreenId.CHAT_ROOM_INIT:
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                Object[] asyncResult2 = ApiClient.getAsyncResult(IOUtils.pollAsyncResult());
                if (asyncResult2 != null) {
                    int responseCode2 = IOUtils.validateJsonResponse(asyncResult2);
                    if (responseCode2 != 0) {
                        action = responseCode2;
                    } else {
                        Object payload = IOUtils.getJsonPayload();
                        MrimAccount mrimAccount2 = (MrimAccount) AppState.getAccount();
                        ChatRoom chatRoom2 = mrimAccount2.findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID));
                        if (chatRoom2 != mrimAccount2.getLastChatRoom()) {
                            chatRoom2.subject = JsonParser.getStringValue(payload, AppState.getString(StateKeys.STR_RES_CONTENT_ENCODING));
                            chatRoom2.messageIds.removeAllElements();
                            Enumeration enumerationElements = ((Vector) JsonParser.getValue(payload, AppState.getString(StateKeys.STR_RES_HEADER_NAME_1))).elements();
                            while (enumerationElements.hasMoreElements()) {
                                chatRoom2.messageIds.addElement(enumerationElements.nextElement());
                            }
                            Enumeration enumerationElements2 = ((Vector) JsonParser.getValue(payload, AppState.getString(StateKeys.STR_RES_PARAM_1))).elements();
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
                Object[] asyncResult3 = ApiClient.getAsyncResult(IOUtils.pollAsyncResult());
                if (asyncResult3 != null) {
                    int responseCode3 = IOUtils.validateJsonResponse(asyncResult3);
                    if (responseCode3 != 0) {
                        action = responseCode3;
                    } else {
                        Object payload2 = IOUtils.getJsonPayload();
                        MrimAccount mrimAccount3 = (MrimAccount) AppState.getAccount();
                        int size4 = ((Vector) payload2).size();
                        for (int i6 = 0; i6 < size4; i6++) {
                            Enumeration keys2 = ((Hashtable) JsonParser.getVectorElement(payload2, i6)).keys();
                            while (keys2.hasMoreElements()) {
                                String str5 = (String) keys2.nextElement();
                                ChatRoom selectedChatRoom = mrimAccount3.findChatRoomByName(str5);
                                ChatRoom chatRoom3 = mrimAccount3.findChatRoomById(AppState.getInt(StateKeys.INT_ACTIVE_CHATROOM_ID));
                                if (selectedChatRoom != null && (message2 = selectedChatRoom.getMessage(str5)) != null && chatRoom3 != null) {
                                    if (message2.hasFlag(4)) {
                                        if (chatRoom3 == mrimAccount3.findDefaultChatRoom()) {
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
                                    mrimAccount3.removeUserFromChatRooms(str5);
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
                AppState.setInt(StateKeys.INT_SCROLL_OFFSET, currentScreen.scrollOffset);
                AppState.setObject(StateKeys.SLOT_MAP_POINT_2, (Object) title);
                if (title == null || (chatRoom = (mrimAccount = (MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID))) == mrimAccount.getLastChatRoom() || title.equals(chatRoom.subject)) {
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
}
