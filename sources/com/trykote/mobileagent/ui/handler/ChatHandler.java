package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
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

    // Chat list modes
    private static final int CHAT_MODE_NORMAL = 5;
    private static final int CHAT_MODE_EXTENDED = 4;

    public static void toggleOnlineMode(boolean extended) {
        if (!extended) {
            ChatState.setChatListMode(CHAT_MODE_NORMAL);
        } else {
            ChatState.setChatListMode(CHAT_MODE_EXTENDED);
            ChatState.setExtendedView(true);
        }
    }

    public static int handleChatOption(int optionId) {
        if (optionId != ScreenId.COMPOSE_MESSAGE) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
            ContactListManager.openContactMessages();
            return 0;
        }
        MrimContact contact = AppState.getCurrentMrimContact();
        AppState.setAccount(contact.account);
        MailHelper.composeEmail(MailHelper.parseRecipientList(contact.simpleIdentifier), (String) null, (String) null);
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public void buildScreen(int screenId) {
        Object[] request;
        switch (screenId) {
            case ScreenId.CHAT_ROOMS:
                RegistrationState.clearRegistrationData();
                MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
                if (mrimAccount.chatRoomManager.getCount() != 0 && !mrimAccount.chatRoomManager.loaded) {
                    MrimChatRoomManager.showChatRoomListWithCounts();
                    return;
                }
                NotificationHelper.showConfirmDialog(37, 833);
                Vector params = ObjectPool.newVector();
                JsonParser.addIntToVector(params, 0);
                params.addElement(AppState.emptyStr);
                JsonParser.addIntToVector(params, 1);
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createAuthRequest(ObjectPool.newStringBuffer().append(ResourceAccessor.str(PackedStringKeys.URL_PATH_MAILBOX)).append('?').append(ResourceAccessor.str(PackedStringKeys.PARAM_AJAX_CALL)).append(ResourceAccessor.str(PackedStringKeys.FUNC_AJAX_GET_MAILBOX)).append(SessionState.getSessionHash()).append(ResourceAccessor.str(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
                return;
            case ScreenId.CHAT_ROOM_INIT:
                MrimChatRoomManager.showChatRoomListWithCounts();
                return;
            case ScreenId.CHAT_ROOM_MESSAGES:
                RegistrationState.clearRegistrationData();
                ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).chatRoomManager.findById(ChatState.getChatRoomId());
                if (!chatRoom.isInitialized) {
                    MrimChatRoomManager.showChatRoomMessages();
                    return;
                }
                NotificationHelper.showConfirmDialog(41, 836);
                MrimAccount msgAccount = (MrimAccount) AppState.getAccount();
                Vector params2 = ObjectPool.newVector();
                if (chatRoom == msgAccount.chatRoomManager.getLast()) {
                    params2.addElement(StringUtils.intern(Integer.toString(0)));
                    params2.addElement(chatRoom.participants);
                    request = ApiClient.createUploadRequest(ResourceAccessor.str(PackedStringKeys.URL_PATH_MAILBOX), ObjectPool.newBufferFromState(722608).append(ResourceAccessor.str(PackedStringKeys.FUNC_AJAX_GET_FOLDER_LIST)).append(SessionState.getSessionHash()).append(ResourceAccessor.str(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                } else {
                    params2.addElement(StringUtils.intern(Integer.toString(chatRoom.id)));
                    int intVal2 = SettingsState.getTimeoutValue();
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
                    request = ApiClient.createAuthRequest(ObjectPool.newBufferFromState(1050207).append('?').append(ResourceAccessor.str(PackedStringKeys.PARAM_AJAX_CALL)).append(ResourceAccessor.str(PackedStringKeys.FUNC_MAJAX_GET_MSGS)).append(SessionState.getSessionHash()).append(ResourceAccessor.str(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                }
                MrimChatRoomManager.sendChatRoomRequest(request);
                return;
            case ScreenId.CHAT_ROOM_INVITE:
                NotificationHelper.showConfirmDialog(42, 862);
                Vector params4 = ObjectPool.newVector();
                JsonParser.addIntToVector(params4, ChatState.getActiveChatRoomId());
                params4.addElement(UIState.getMediaStream());
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(ResourceAccessor.str(PackedStringKeys.URL_PATH_MAILBOX), ObjectPool.newStringBuffer().append(ResourceAccessor.str(PackedStringKeys.PARAM_AJAX_CALL)).append(ResourceAccessor.str(PackedStringKeys.FUNC_AJAX_MOVE_MSGS)).append(SessionState.getSessionHash()).append(ResourceAccessor.str(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params4)))));
                return;
            case ScreenId.CHAT_ROOM_VIEW:
                MrimChatRoomManager.showChatRoomMessages();
                return;
            case ScreenId.CHAT_ROOM_CONFIG:
                Screens.chatRoomConfig(this).show();
                return;
            case ScreenId.CHAT_VIEW_MODE:
                Screens.chatViewMode(this).show();
                return;
            case ScreenId.CHAT_ROOM_CONTEXT:
                setupChatRoomContext();
                return;
            case ScreenId.CHAT_ROOM_ALERT:
                NotificationHelper.showAlertById(61, 857);
                return;
            case ScreenId.CHAT_ROOM_OPTIONS:
                Screens.chatRoomOptions(this).show();
                return;
            case ScreenId.CHAT_LIST_OPTIONS:
                Screens.chatListOptions(this).show();
                return;
            case ScreenId.CREATE_CHAT_ROOM:
                ChatState.setChatRoomCreated(false);
                ChatState.setChatNameFromBuffer(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_CHAT_NAME_PREFIX)).append(1 + (SettingsState.getUiCounter() % CHAT_NAME_COUNTER_MODULO)));
                ScreenManager.showScreen(ContactListManager.buildContactListScreen(Screens.createChatRoom(this), (MrimAccount) AppState.getAccount(), (Contact) null));
                return;
            case ScreenId.CHAT_STATUS:
                UIState.setStatusTextSet(Utils.nonEmpty(UIState.getStatusText()));
                Screens.chatStatus(this).show();
                return;
            case ScreenId.CHAT_DETAIL:
                Conversation.updateStatusText(411);
                Screens.chatDetail(this).show();
                return;
            case ScreenId.CHAT_OPTIONS:
                Screens.chatOptions(this).show();
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
                ChatState.setScrollOffset(currentScreen.scrollOffset);
                MapState.setMapPoint2((Object) title);
                Message selectedMsg = (Message) obj;
                RuntimeState.setMessageId((Object) (selectedMsg != null ? selectedMsg.from : null));
                return 0;
            case ScreenId.CHAT_ROOM_CONFIG:
                ScreenManager.processScreenForm();
                return (SettingsState.getEmoticonConfigAction() != 4 || (configResult = ((MrimAccount) AppState.getAccount()).setConfiguration(((SettingsState.getEmoticonConfigId() - EMOTICON_CONFIG_BASE_OFFSET) << 8) + 4)) == 0) ? 0 : NotificationHelper.showError(configResult);
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
                String chatName = Utils.defaultStr(ChatState.getChatName());
                if (StringUtils.isEmpty(chatName)) {
                    createResult = NotificationHelper.showError(301);
                } else {
                    Vector checkedItems = ContactListManager.getCheckedItems(currentScreen, 3);
                    if (checkedItems.size() == 0) {
                        createResult = NotificationHelper.showError(775);
                    } else {
                        MrimAccount createAccount = (MrimAccount) AppState.getAccount();
                        boolean includeOwner = ChatState.isChatRoomCreated();
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
                            SettingsState.incrementUiCounter();
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
                return handleChatOption(action);
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
                RegistrationState.clearRegistrationData();
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
                RegistrationState.clearRegistrationData();
                break;
            case ScreenId.CHAT_ROOM_MESSAGES:
                RegistrationState.clearRegistrationData();
                break;
            case ScreenId.CHAT_ROOM_INVITE:
                RegistrationState.clearRegistrationData();
                break;
            case ScreenId.CHAT_ROOM_CONTEXT:
                ChatState.clearUnreadCountText();
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                ChatState.clearChatName();
                break;
        }
        clearScreenFlags();
    }

    private static void clearScreenFlags() {
        ChatState.clearScreenFlags();
    }

    public int onItemSelected(ListView screen, MenuItem menuItem, String title, int selectedOption,
                              Object obj, Object obj2) {
        switch (screen.screenId) {
            case ScreenId.CHAT_ROOMS:
                return -1;
            case ScreenId.CHAT_ROOM_INIT:
                ChatState.setChatRoomId(((ChatRoom) obj).id);
                return 0;
            case ScreenId.CHAT_ROOM_MESSAGES:
                return -1;
            case ScreenId.CHAT_ROOM_INVITE:
                return -1;
            case ScreenId.CHAT_ROOM_VIEW:
                ChatState.setScrollOffset(screen.scrollOffset);
                MapState.setMapPoint2((Object) title);
                Message selectedMsg = (Message) obj;
                if (selectedMsg == null) {
                    return -1;
                }
                RuntimeState.setMessageId((Object) selectedMsg.from);
                ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).chatRoomManager.findById(ChatState.getChatRoomId());
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
                return handleChatOption(selectedOption);
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
                    int nextScreen = UIState.getScreenAction();
                    int responseCode = ApiClient.validateJsonResponse(roomsResult);
                    if (responseCode != 0) {
                        result = responseCode;
                    } else {
                        ((MrimAccount) AppState.getAccount()).chatRoomManager.parseFromJson(ApiClient.getJsonPayload());
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
                        MrimAccount msgsAccount = (MrimAccount) AppState.getAccount();
                        ChatRoom msgsChatRoom = msgsAccount.chatRoomManager.findById(ChatState.getChatRoomId());
                        if (msgsChatRoom != msgsAccount.chatRoomManager.getLast()) {
                            msgsChatRoom.subject = JsonParser.getStringValue(payload, ResourceAccessor.str(PackedStringKeys.MAIL_FIELD_LAST_MSG_ID));
                            msgsChatRoom.messageIds.removeAllElements();
                            Enumeration allIds = ((Vector) JsonParser.getValue(payload, ResourceAccessor.str(PackedStringKeys.MAIL_PARAM_MLIST_ALL))).elements();
                            while (allIds.hasMoreElements()) {
                                msgsChatRoom.messageIds.addElement(allIds.nextElement());
                            }
                            Enumeration msgEntries = ((Vector) JsonParser.getValue(payload, ResourceAccessor.str(PackedStringKeys.MAIL_PARAM_MLIST))).elements();
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
                        MrimAccount inviteAccount = (MrimAccount) AppState.getAccount();
                        int entryCount = ((Vector) payload).size();
                        for (int ei = 0; ei < entryCount; ei++) {
                            Enumeration entryKeys = ((Hashtable) JsonParser.getVectorElement(payload, ei)).keys();
                            while (entryKeys.hasMoreElements()) {
                                String msgId = (String) entryKeys.nextElement();
                                ChatRoom sourceChatRoom = inviteAccount.chatRoomManager.findByName(msgId);
                                ChatRoom targetChatRoom = inviteAccount.chatRoomManager.findById(ChatState.getActiveChatRoomId());
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
                ChatState.setScrollOffset(currentScreen.scrollOffset);
                MapState.setMapPoint2((Object) title);
                MrimAccount viewAccount = (MrimAccount) AppState.getAccount();
                ChatRoom viewChatRoom = viewAccount.chatRoomManager.findById(ChatState.getChatRoomId());
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
        String messageId = RuntimeState.getMessageId();
        int chatRoomId = ChatState.getChatRoomId();
        MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
        boolean hasMessage = messageId != null;
        ChatState.setIsChatRoom(hasMessage);
        ChatRoom chatRoom = mrimAccount.chatRoomManager.findById(chatRoomId);
        boolean isMarkedRead = chatRoom.isMessageRead(messageId);
        ChatState.setMsgReadSelected(hasMessage && isMarkedRead);
        ChatState.setMsgUnreadSelected(hasMessage && !isMarkedRead);
        Message message = chatRoom.getMessage(messageId);
        ChatState.setMsgUnread(hasMessage && !message.isRead());
        ChatState.setMsgRead(hasMessage && message.isRead());
        int readCount = chatRoom.readMessages.size();
        ChatState.setHasMembers(readCount != 0);
        ChatState.setHasMore(chatRoom != mrimAccount.chatRoomManager.getLast());
        ChatState.setUnreadCountText(
            ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_UNREAD_COUNT_PREFIX))
                .append(readCount).append(')'));
        Screens.chatRoomContext(null).show();
    }

    public static int handleRoutePointOption(int optionId) {
        if (optionId == 0) {
            MapController.setRouteStart();
            if (MmpContact.hasSecondToken()) {
                return ScreenId.MAP;
            }
            MapState.setMapModeActive(true);
            return ScreenId.MAP_SEARCH;
        }
        MapController.setRouteEnd();
        if (MmpContact.hasFirstToken()) {
            return ScreenId.MAP;
        }
        MapState.setMapModeActive(false);
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
            MapState.setResourceUrl((Object) mapPoint.getResourceUrl());
            return 0;
        }
        if (optionId != 120) {
            return 0;
        }
        MapController.removeRoutePoint(mapPoint);
        return 0;
    }

    public static int handleChatInputAction(String str) {
        String messageText = Utils.defaultStr(UIState.getStatusText());
        if (str != ResourceAccessor.str(StringResKeys.STR_NOTIFICATION_SOUND)) {
            StringBuffer sb = Utils.getMessageBuffer();
            if (StringUtils.matchesKey(473, str)) {
                UIState.setStatusTextFromBuffer(sb.append(UIState.getNotificationText()));
                return 0;
            }
            if (StringUtils.matchesKey(474, str)) {
                UIState.setNotificationText((Object) messageText);
                UIState.setResourceLoading(true);
                return 0;
            }
            if (!StringUtils.matchesKey(476, str)) {
                return 0;
            }
            UIState.setStatusText((Object) Conversation.transliterateRussian(messageText));
            return 0;
        }
        String phoneNumber = (String) ContactState.getSelectedGroup();
        MrimContact mrimContact = ContactState.getMrimContact();
        MrimAccount mrimAccount = (MrimAccount) mrimContact.account;
        int errorCode;
        if (mrimAccount.isConnected()) {
            mrimContact.appendMessage(1, ObjectPool.toStringAndRelease(Utils.appendColon(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_FILE_TRANSFER_PREFIX)).append(Utils.formatPhone(phoneNumber))).append(messageText)), 0L, 0L);
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
        String messageId = RuntimeState.getMessageId();
        int chatRoomId = ChatState.getChatRoomId();
        MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
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
            toggleOnlineMode(false);
            return 0;
        }
        if (!StringUtils.matchesKey(851, str)) {
            return 0;
        }
        ChatState.setScrollOffset(0);
        MapState.setMapPoint2(null);
        mrimAccount.chatRoomManager.loaded = true;
        chatRoom.setActive(false);
        UIState.setScreenAction(41);
        return 0;
    }

    public static int handleMapViewOption(int optionId) {
        MapController.showMapView();
        if (optionId == 6) {
            MapController.applyViewMode(true, false, !MapState.isMapViewActive());
            ContactState.setRefreshNeeded(true);
            MapState.setMapLoading(true);
            return 0;
        }
        if (optionId == 100) {
            UIState.setLoading(1);
            return 0;
        }
        if (!MapController.hasRoutePoints()) {
            return NotificationHelper.showError(354);
        }
        ContactState.setLoaded(true);
        return 0;
    }
}
