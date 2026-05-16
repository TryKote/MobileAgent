package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.map.MapPoint;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.map.RouteData;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.MailHelper;
import com.trykote.mobileagent.model.Message;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Enumeration;
import java.util.Vector;

public final class ChatScreen extends ScreenView {

    private static final int CHAT_NAME_COUNTER_MODULO = 1000;
    private static final int EMOTICON_CONFIG_BASE_OFFSET = 157;
    private static final int EMOTICON_CONFIG_ACTION_APPLY = 4;
    private static final int CHAT_MODE_NORMAL = 5;
    private static final int CHAT_MODE_EXTENDED = 4;
    private static final int CHECKED_ITEMS_MODE_CONTACTS = 3;
    private static final int ALERT_ICON_INVITE = 61;
    private static final int ALERT_TEXT_INVITE = 857;

    private static final int ACTION_NAVIGATE_TO = 6;
    private static final int ACTION_VIEW_RESOURCE = 118;
    private static final int ACTION_REMOVE_POINT = 120;
    private static final int ACTION_SHOW_MAP = 100;

    public ChatScreen(int screenId) {
        super(typeFor(screenId), screenId);
    }

    private static int typeFor(int screenId) {
        switch (screenId) {
            case ScreenId.CHAT_ROOM_CONTEXT:
            case ScreenId.CHAT_STATUS:
            case ScreenId.CHAT_LIST_OPTIONS:
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenManager.TYPE_DIALOG_BOTTOM;
            case ScreenId.CHAT_ROOM_OPTIONS:
            case ScreenId.CHAT_DETAIL:
            case ScreenId.CHAT_OPTIONS:
                return ScreenManager.TYPE_POPUP;
            default:
                return ScreenManager.TYPE_FULLSCREEN;
        }
    }

    public void showSelf() {
        if (screenId == ScreenId.CREATE_CHAT_ROOM) {
            ScreenManager.showScreen(ContactListManager.buildContactListScreen(
                this, AppState.getAccount(), (Contact) null));
        } else if (screenId == ScreenId.CHAT_ROOM_ALERT) {
            NotificationHelper.showAlertById(ALERT_ICON_INVITE, ALERT_TEXT_INVITE);
        } else {
            ScreenManager.showScreen(this);
        }
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.CHAT_ROOM_INIT:
                buildChatRoomList();
                break;
            case ScreenId.CHAT_ROOM_VIEW:
                buildChatRoomMessages();
                break;
            case ScreenId.CHAT_ROOM_CONFIG:
                configureHeader(0, 795);
                addTextInput(0, 63, 424, 0, 0);
                configureSoftKeys(1053, 0, 1050, 12, 0);
                break;
            case ScreenId.CHAT_VIEW_MODE:
                configureHeader(309, 544);
                addCheckbox(625, 112);
                configureSoftKeys(1053, 12, 1050, 12, 0);
                break;
            case ScreenId.CHAT_ROOM_CONTEXT:
                setupChatRoomContext();
                configureHeader(0, 1038);
                addConditionalIf(1517, 37, 243, 851);
                addActionById(238, 846, 54);
                addConditionalIf(1518, 43, 24, 847);
                addConditionalIf(1519, 43, 25, 848);
                addConditionalIf(1520, 67, 25, 1347);
                addConditionalIf(1521, 62, 240, 850);
                addConditionalIf(1517, 68, 227, 1061);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.CHAT_STATUS:
                UIState.setStatusTextSet(Utils.nonEmpty(UIState.getStatusText()));
                configureHeader(0, 1038);
                addConditionalIf(1456, 40, -1, 1060);
                addConditionalIf(1456, 65, -1, 476);
                addActionById(-1, 472, 99);
                addConditionalIf(1460, 65, -1, 473);
                addConditionalIf(1456, 65, -1, 474);
                addConditionalIf(1456, 98, -1, 475);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.CHAT_ROOM_OPTIONS:
                configureHeader(0, 1038);
                showCheckboxes = true;
                addActionById(228, 357, 0);
                addActionById(229, 358, 1);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.CHAT_LIST_OPTIONS:
                configureHeader(0, 1038);
                addActionById(308, 379, ACTION_NAVIGATE_TO);
                addActionById(16, 1060, ACTION_VIEW_RESOURCE);
                addActionById(34, 719, ACTION_REMOVE_POINT);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                ChatState.setChatRoomCreated(false);
                ChatState.setChatNameFromBuffer(ObjectPool.newStringBuffer()
                    .append(StringPool.get(StringResKeys.STR_CHAT_NAME_PREFIX))
                    .append(1 + (SettingsState.getUiCounter() % CHAT_NAME_COUNTER_MODULO)));
                configureHeader(232, 553);
                addTextInput(869, 255, 424, 0, 1292);
                addCheckbox(769, 2722);
                addTextSeparator(770);
                configureSoftKeys(1053, 4, 1050, 12, 0);
                break;
            case ScreenId.CHAT_DETAIL:
                Conversation.updateStatusText(411);
                configureHeader(0, 1038);
                showCheckboxes = true;
                addActionById(308, 657, ACTION_NAVIGATE_TO);
                addActionById(303, 659, ACTION_SHOW_MAP);
                addActionById(360, 1251, ACTION_REMOVE_POINT);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
            case ScreenId.CHAT_OPTIONS:
                configureHeader(0, 1038);
                showCheckboxes = true;
                addConditionalIf(1497, 65, 27, 776);
                addConditionalIf(1497, 54, 238, 777);
                addConditionalIf(1500, 135, 221, 778);
                configureSoftKeys(1048, 199, 1050, 12, 199);
                break;
        }
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.CHAT_ROOM_VIEW:
                ChatState.setScrollOffset(scrollOffset);
                MapState.setMapPoint2(title);
                Message msg = (Message) data;
                RuntimeState.setMessageId(msg != null ? msg.from : null);
                return 0;
            case ScreenId.CHAT_ROOM_CONFIG:
                ScreenManager.processScreenForm();
                if (SettingsState.getEmoticonConfigAction() == EMOTICON_CONFIG_ACTION_APPLY) {
                    int result = AppState.getAccount().applyConfiguration(
                        ((SettingsState.getEmoticonConfigId() - EMOTICON_CONFIG_BASE_OFFSET) << 8) + EMOTICON_CONFIG_ACTION_APPLY);
                    if (result != 0) {
                        return NotificationHelper.showError(result);
                    }
                }
                return 0;
            case ScreenId.CHAT_VIEW_MODE:
                ScreenManager.processScreenForm();
                return 0;
            case ScreenId.CHAT_ROOM_CONTEXT:
                return handleChatRoomAction(title);
            case ScreenId.CHAT_STATUS:
                return handleChatInputAction(title);
            case ScreenId.CHAT_ROOM_OPTIONS:
                return handleRoutePointOption(action);
            case ScreenId.CHAT_LIST_OPTIONS:
                return handleRouteListOption(action);
            case ScreenId.CREATE_CHAT_ROOM:
                return handleCreateChatRoom();
            case ScreenId.CHAT_DETAIL:
                return handleMapViewOption(action);
            case ScreenId.CHAT_OPTIONS:
                return handleChatOption(action);
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenId.CHAT_ROOM_INVITE;
            default:
                return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.CHAT_ROOM_INIT:
                ChatState.setChatRoomId(((ChatRoom) data).id);
                return 0;
            case ScreenId.CHAT_ROOM_VIEW:
                return handleViewSelect(title, data);
            case ScreenId.CHAT_ROOM_CONTEXT:
                handleChatRoomAction(title);
                return 0;
            case ScreenId.CHAT_STATUS:
                return handleChatInputAction(title);
            case ScreenId.CHAT_ROOM_OPTIONS:
                return handleRoutePointOption(selectedOption);
            case ScreenId.CHAT_LIST_OPTIONS:
                return handleRouteListOption(selectedOption);
            case ScreenId.CHAT_DETAIL:
                return handleMapViewOption(selectedOption);
            case ScreenId.CHAT_OPTIONS:
                return handleChatOption(selectedOption);
            case ScreenId.CHAT_ROOM_ALERT:
                return ScreenId.CHAT_ROOM_INVITE;
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        if (screenId == ScreenId.CHAT_ROOM_ALERT) {
            return ScreenId.CLOSE;
        }
        return 0;
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.CHAT_ROOM_CONTEXT:
                ChatState.clearUnreadCountText();
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                ChatState.clearChatName();
                break;
        }
        ChatState.clearScreenFlags();
    }

    public int onIdle(MenuItem item, Object data, String title) {
        if (screenId == ScreenId.CHAT_ROOM_VIEW) {
            return processViewIdle(title);
        }
        return 0;
    }

    // --- Content builders ---

    private void buildChatRoomList() {
        Account account = AppState.getAccount();
        configureHeader(35, 834);
        configureSoftKeys(1059, 80, 1050, 12, 41);
        Vector chatRooms = account.getChatRooms();
        if (chatRooms == null) {
            return;
        }
        Enumeration elements = chatRooms.elements();
        while (elements.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) elements.nextElement();
            if (!account.isLastChatRoom(chatRoom)) {
                MenuItem menuItem = MenuItem.createDefault().setIcon(234)
                    .setLabel(ObjectPool.toStringAndRelease(
                        ObjectPool.newStringBuffer().append(chatRoom.name).append(' ').append('[')))
                    .addText(StringUtils.intern(Integer.toString(chatRoom.unreadCount)), 1, 0)
                    .setLabel(ObjectPool.toStringAndRelease(
                        ObjectPool.newStringBuffer().append('/').append(chatRoom.memberCount).append(']')));
                menuItem.data = chatRoom;
                addItem(menuItem);
            }
        }
    }

    private void buildChatRoomMessages() {
        Account account = AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(ChatState.getChatRoomId());
        configureHeader(0, 1038);
        setHeader(234, chatRoom.getDisplayName());
        configureSoftKeys(1059, 51, 1050, 12, 48);
        Vector messages = ObjectPool.newVector();
        Enumeration elements = chatRoom.messageIds.elements();
        while (elements.hasMoreElements()) {
            Object key = elements.nextElement();
            if (chatRoom.messages.containsKey(key)) {
                messages.addElement(chatRoom.messages.get(key));
            }
        }
        Enumeration msgEnum = messages.elements();
        while (msgEnum.hasMoreElements()) {
            addItem(((Message) msgEnum.nextElement()).createMenuItem(chatRoom));
        }
        if (menuItems.size() == 0) {
            selectable = false;
            addLabelById(835);
        } else {
            scrollOffset = ChatState.getScrollOffset();
            selectByTitle((String) MapState.getMapPoint2());
            invalidateLayout();
        }
        reverseScroll = true;
    }

    // --- Idle processing ---

    private int processViewIdle(String title) {
        ChatState.setScrollOffset(scrollOffset);
        MapState.setMapPoint2(title);
        Account account = AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(ChatState.getChatRoomId());
        if (title == null || account.isLastChatRoom(chatRoom) || title.equals(chatRoom.subject)) {
            return 0;
        }
        Object lastMessage = null;
        Enumeration idEnum = chatRoom.messageIds.elements();
        while (idEnum.hasMoreElements()) {
            Object msgId = idEnum.nextElement();
            if (chatRoom.messages.containsKey(msgId)) {
                lastMessage = chatRoom.messages.get(msgId);
            }
        }
        if (title == (lastMessage != null ? ((Message) lastMessage).from : null)) {
            chatRoom.setActive(true);
            return ScreenId.CHAT_ROOM_MESSAGES;
        }
        return 0;
    }

    // --- Action handlers ---

    private static void setupChatRoomContext() {
        String messageId = RuntimeState.getMessageId();
        int chatRoomId = ChatState.getChatRoomId();
        Account account = AppState.getAccount();
        boolean hasMessage = messageId != null;
        ChatState.setIsChatRoom(hasMessage);
        ChatRoom chatRoom = account.findChatRoomById(chatRoomId);
        boolean isMarkedRead = chatRoom.isMessageRead(messageId);
        ChatState.setMsgReadSelected(hasMessage && isMarkedRead);
        ChatState.setMsgUnreadSelected(hasMessage && !isMarkedRead);
        Message message = chatRoom.getMessage(messageId);
        ChatState.setMsgUnread(hasMessage && !message.isRead());
        ChatState.setMsgRead(hasMessage && message.isRead());
        int readCount = chatRoom.readMessages.size();
        ChatState.setHasMembers(readCount != 0);
        ChatState.setHasMore(!account.isLastChatRoom(chatRoom));
        ChatState.setUnreadCountText(
            ObjectPool.newStringBuffer()
                .append(StringPool.get(StringResKeys.STR_UNREAD_COUNT_PREFIX))
                .append(readCount).append(')'));
    }

    private int handleCreateChatRoom() {
        ScreenManager.processScreenForm();
        String chatName = Utils.defaultStr(ChatState.getChatName());
        if (StringUtils.isEmpty(chatName)) {
            return NotificationHelper.showError(301);
        }
        Vector checkedItems = ContactListManager.getCheckedItems(this, CHECKED_ITEMS_MODE_CONTACTS);
        if (checkedItems.size() == 0) {
            return NotificationHelper.showError(775);
        }
        int sendResult = AppState.getAccount().createChatRoom(
            chatName, checkedItems, ChatState.isChatRoomCreated());
        if (sendResult != 0) {
            return NotificationHelper.showError(sendResult);
        }
        SettingsState.incrementUiCounter();
        return 0;
    }

    private static int handleViewSelect(String title, Object data) {
        ChatState.setScrollOffset(0);
        MapState.setMapPoint2(title);
        Message msg = (Message) data;
        if (msg == null) {
            return -1;
        }
        RuntimeState.setMessageId(msg.from);
        ChatRoom chatRoom = AppState.getAccount().findChatRoomById(ChatState.getChatRoomId());
        if (StringUtils.matchesKey(894, chatRoom.name) || StringUtils.matchesKey(899, chatRoom.name)) {
            MailHelper.setMailAction(54, 3);
        } else {
            MailHelper.setMailAction(52, 0);
        }
        return 0;
    }

    public static int handleChatOption(int optionId) {
        if (optionId != ScreenId.COMPOSE_MESSAGE) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
            ContactListManager.openContactMessages();
            return 0;
        }
        Contact contact = AppState.getCurrentContact();
        AppState.setAccount(contact.account);
        MailHelper.composeEmail(MailHelper.parseRecipientList(contact.getContactEmail()), (String) null, (String) null);
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static int handleRoutePointOption(int optionId) {
        if (optionId == 0) {
            MapController.setRouteStart();
            if (RouteData.hasSecondToken()) {
                return ScreenId.MAP;
            }
            MapState.setMapModeActive(true);
            return ScreenId.MAP_SEARCH;
        }
        MapController.setRouteEnd();
        if (RouteData.hasFirstToken()) {
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
        if (optionId == ACTION_NAVIGATE_TO) {
            MapRenderer.navigateToMapPoint(mapPoint);
            return 0;
        }
        if (optionId == ACTION_VIEW_RESOURCE) {
            MapState.setResourceUrl(mapPoint.getResourceUrl());
            return 0;
        }
        if (optionId == ACTION_REMOVE_POINT) {
            MapController.removeRoutePoint(mapPoint);
        }
        return 0;
    }

    public static int handleChatInputAction(String actionLabel) {
        String messageText = Utils.defaultStr(UIState.getStatusText());
        if (actionLabel != StringPool.get(StringResKeys.STR_NOTIFICATION_SOUND)) {
            StringBuffer sb = Utils.getMessageBuffer();
            if (StringUtils.matchesKey(473, actionLabel)) {
                UIState.setStatusTextFromBuffer(sb.append(UIState.getNotificationText()));
                return 0;
            }
            if (StringUtils.matchesKey(474, actionLabel)) {
                UIState.setNotificationText(messageText);
                UIState.setResourceLoading(true);
                return 0;
            }
            if (!StringUtils.matchesKey(476, actionLabel)) {
                return 0;
            }
            UIState.setStatusText(Conversation.transliterateRussian(messageText));
            return 0;
        }
        String phoneNumber = (String) ContactState.getSelectedGroup();
        Contact smsContact = ContactState.getContact();
        int errorCode = smsContact.account.sendSmsMessage(smsContact, phoneNumber, messageText);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return 0;
    }

    public static int handleChatRoomAction(String actionLabel) {
        String messageId = RuntimeState.getMessageId();
        int chatRoomId = ChatState.getChatRoomId();
        Account account = AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(chatRoomId);
        if (StringUtils.matchesKey(848, actionLabel)) {
            chatRoom.readMessages.addElement(messageId);
            return 0;
        }
        if (StringUtils.matchesKey(847, actionLabel)) {
            chatRoom.markMessageRead(messageId);
            return 0;
        }
        if (StringUtils.matchesKey(846, actionLabel)) {
            ScreenBuilder.onScreenClosed();
            MailHelper.composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (StringUtils.matchesKey(1347, actionLabel)) {
            IOUtils.setSelectedItems(chatRoom.readMessages);
            return 0;
        }
        if (StringUtils.matchesKey(1061, actionLabel)) {
            ScreenBuilder.onScreenClosed();
            toggleOnlineMode(false);
            return 0;
        }
        if (!StringUtils.matchesKey(851, actionLabel)) {
            return 0;
        }
        ChatState.setScrollOffset(0);
        MapState.setMapPoint2(null);
        account.setChatRoomsLoaded();
        chatRoom.setActive(false);
        UIState.setScreenAction(ScreenId.CHAT_ROOM_MESSAGES);
        return 0;
    }

    public static int handleMapViewOption(int optionId) {
        MapController.showMapView();
        if (optionId == ACTION_NAVIGATE_TO) {
            MapController.applyViewMode(true, false, !MapState.isMapViewActive());
            ContactState.setRefreshNeeded(true);
            MapState.setMapLoading(true);
            return 0;
        }
        if (optionId == ACTION_SHOW_MAP) {
            UIState.setLoading(1);
            return 0;
        }
        if (!MapController.hasRoutePoints()) {
            return NotificationHelper.showError(354);
        }
        ContactState.setLoaded(true);
        return 0;
    }

    public static void toggleOnlineMode(boolean extended) {
        if (!extended) {
            ChatState.setChatListMode(CHAT_MODE_NORMAL);
        } else {
            ChatState.setChatListMode(CHAT_MODE_EXTENDED);
            ChatState.setExtendedView(true);
        }
    }
}
