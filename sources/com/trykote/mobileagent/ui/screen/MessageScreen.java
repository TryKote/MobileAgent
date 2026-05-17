package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.MailHelper;
import com.trykote.mobileagent.model.Message;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.util.JsonParser;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Hashtable;
import java.util.Vector;

public final class MessageScreen extends ScreenView {

    public MessageScreen(int screenId) {
        super(ScreenManager.TYPE_FULLSCREEN, screenId);
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.MESSAGE_DETAIL:
                buildMessageDetail();
                break;
            case ScreenId.MESSAGE_PREVIEW:
                buildMessagePreview();
                break;
            case ScreenId.COMPOSE_RECIPIENTS:
                Screens.composeRecipients().show();
                break;
            case ScreenId.COMPOSE_MESSAGE:
                Screens.composeMessage().show();
                break;
            case ScreenId.MAIL_MENU:
                Screens.mailMenu().show();
                break;
            case ScreenId.SEND_MAIL:
                buildSendMail();
                break;
            case ScreenId.REPLY_MAIL:
                buildReplyMail();
                break;
            case ScreenId.MESSAGE_INPUT:
                Screens.messageInput().show();
                UIState.clearScreenProperties();
                break;
            case ScreenId.SEND_TO_CONTACT:
                buildSendToContact();
                break;
            case ScreenId.MESSAGE_SUMMARY:
                ScreenManager.showScreen(AppState.getCurrentContact().showMessageSummary());
                break;
            case ScreenId.DELETE_MESSAGES:
                NotificationHelper.showAlertBuffer(128, ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_DELETE_CONFIRM)).append(AppState.getCurrentContact().displayName).append(ObjectPool.unpackChars(16167)));
                break;
            case ScreenId.SEND_CONFIRM:
                NotificationHelper.showConfirmDialog(161, 872);
                break;
            case ScreenId.NOTIFY_MESSAGE:
                UIState.setConversationActive(false);
                NotificationHelper.showAlertBuffer(163, ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_NOTIFY_MESSAGE)));
                break;
            case ScreenId.MAILBOX_OPTIONS:
                Screens.mailboxOptions().show();
                break;
            case ScreenId.SEND_DATA:
                NotificationHelper.showConfirmDialog(179, 504);
                break;
        }
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.MESSAGE_DETAIL:
            case ScreenId.SEND_MAIL:
            case ScreenId.REPLY_MAIL:
            case ScreenId.SEND_CONFIRM:
            case ScreenId.SEND_DATA:
                return -1;
            case ScreenId.COMPOSE_RECIPIENTS:
                return MailHelper.handleMailForwardAction(title);
            case ScreenId.COMPOSE_MESSAGE:
                return handleComposeMessage();
            case ScreenId.MAIL_MENU:
                return MailHelper.handleMailMenuAction(title, action);
            case ScreenId.MESSAGE_INPUT:
                return handleMessageInput();
            case ScreenId.SEND_TO_CONTACT:
                return handleFileAction(data);
            case ScreenId.MESSAGE_SUMMARY:
                return MapController.handleLocationAction(data);
            case ScreenId.DELETE_MESSAGES:
                AppState.getCurrentContact().initMessageBuffer();
                return ScreenId.CONTACT_LIST;
            case ScreenId.NOTIFY_MESSAGE:
                return AccountManager.handleSendKey();
            case ScreenId.MAILBOX_OPTIONS:
                return MapScreen.handleMapModeOption(action);
            default:
                return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.MESSAGE_DETAIL:
            case ScreenId.SEND_MAIL:
            case ScreenId.REPLY_MAIL:
            case ScreenId.SEND_CONFIRM:
            case ScreenId.SEND_DATA:
                return -1;
            case ScreenId.COMPOSE_RECIPIENTS:
                return MailHelper.handleMailForwardAction(title);
            case ScreenId.MAIL_MENU:
                return MailHelper.handleMailMenuAction(title, selectedOption);
            case ScreenId.SEND_TO_CONTACT:
                return handleFileAction(data);
            case ScreenId.MESSAGE_SUMMARY:
                return MapController.handleLocationAction(data);
            case ScreenId.DELETE_MESSAGES:
                AppState.getCurrentContact().initMessageBuffer();
                return ScreenId.CONTACT_LIST;
            case ScreenId.NOTIFY_MESSAGE:
                return AccountManager.handleSendKey();
            case ScreenId.MAILBOX_OPTIONS:
                return MapScreen.handleMapModeOption(selectedOption);
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        switch (screenId) {
            case ScreenId.MESSAGE_DETAIL:
                RegistrationState.clearRegistrationData();
                return 0;
            case ScreenId.SEND_CONFIRM:
                return ScreenId.CONTACT_LIST;
            default:
                return 0;
        }
    }

    public int onIdle(MenuItem item, Object data, String title) {
        switch (screenId) {
            case ScreenId.MESSAGE_DETAIL:
                return MailHelper.processMailResponse();
            case ScreenId.SEND_MAIL:
                return processSendMailIdle();
            case ScreenId.REPLY_MAIL:
                return processReplyMailIdle();
            case ScreenId.SEND_CONFIRM:
                return MapController.showMapSearchResults();
            case ScreenId.SEND_DATA:
                return processSendDataIdle();
            default:
                return 0;
        }
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.MESSAGE_DETAIL:
            case ScreenId.SEND_MAIL:
            case ScreenId.REPLY_MAIL:
                RegistrationState.clearRegistrationData();
                break;
            case ScreenId.COMPOSE_MESSAGE:
                RuntimeState.clearMsgExtras();
                break;
            case ScreenId.SEND_DATA:
                UIState.clearScreenTitle();
                break;
        }
    }

    // --- Build helpers ---

    private static void buildMessageDetail() {
        RegistrationState.clearRegistrationData();
        String msgId = RuntimeState.getMessageId();
        Message message = (Message) AppState.getAccount().findChatRoomById(ChatState.getChatRoomId()).messages.get(msgId);
        Message messageWithBody = message.body != null ? message : null;
        NotificationHelper.showConfirmDialog(48, 837);
        if (messageWithBody == null) {
            Vector params = ObjectPool.newVector();
            params.addElement(msgId);
            params.addElement(AppState.emptyStr);
            params.addElement(ObjectPool.unpackChars(6775156));
            AppState.getAccount().sendChatRoomRequest(ApiClient.createAuthRequest(ObjectPool.newStringBuffer().append(StringPool.get(PackedStringKeys.URL_PATH_AJAX_READMSG)).append('?').append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL)).append(StringPool.get(PackedStringKeys.FUNC_AJAX_GET_MSG_DATA)).append(SessionState.getSessionHash()).append(StringPool.get(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
        }
    }

    private static void buildMessagePreview() {
        String msgId = RuntimeState.getMessageId();
        ChatRoom chatRoom = AppState.getAccount().findChatRoomById(ChatState.getChatRoomId());
        int roomType = chatRoom.getType();
        Message message = chatRoom.getMessage(msgId);
        String str;
        if (roomType == 2) {
            String[] ccRecipient = MailHelper.getFirstRecipient(message.ccList);
            str = ccRecipient != null ? ccRecipient[1] : AppState.emptyStr;
        } else {
            String[] toRecipient = MailHelper.getFirstRecipient(message.toList);
            str = toRecipient != null ? toRecipient[1] : AppState.emptyStr;
        }
        UIState.setScreenTitle((Object) str);
        UIState.setScreenSubtitle((Object) Utils.normalizeSpaces(message.getSubject()));
        UIState.setScreenValue((Object) Utils.normalizeSpaces(message.body));
        Screen screen = Screens.messagePreview();
        Object[] attachments = message.attachments;
        if (attachments != null) {
            for (Object obj : attachments) {
                screen.addItem(MenuItem.createSeparator().setIcon(221).addText(((String[]) obj)[1], 1, 0));
            }
        }
        ScreenManager.showScreen(screen);
        UIState.clearScreenProperties();
    }

    private static void buildSendMail() {
        NotificationHelper.showConfirmDialog(81, 872);
        Vector params = ObjectPool.newVector();
        params.addElement(AppState.getString(ChatState.isExtendedView() ? PackedStringKeys.VALUE_TRUE : 1038));
        params.addElement(ObjectPool.integerOf(ChatState.getChatRoomId()));
        params.addElement(Utils.defaultStr(RuntimeState.getMsgSubject()));
        params.addElement(Utils.defaultStr(RuntimeState.getMsgSender()));
        params.addElement(Utils.defaultStr(RuntimeState.getMsgBody()));
        params.addElement(Utils.defaultStr(RuntimeState.getMsgExtra1()));
        AppState.getAccount().sendChatRoomRequest(ApiClient.createUploadRequest(StringPool.get(PackedStringKeys.URL_PATH_MAILBOX), ObjectPool.newStringBuffer().append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL)).append(StringPool.get(PackedStringKeys.FUNC_MAJAX_SEARCH)).append(SessionState.getSessionHash()).append(StringPool.get(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params)))));
    }

    private static void buildReplyMail() {
        NotificationHelper.showConfirmDialog(82, 877);
        Message newMessage = new Message(MailHelper.parseRecipientList(Utils.defaultStr(RuntimeState.getMsgExtra2())), Utils.defaultStr(RuntimeState.getMsgExtra3()), Utils.defaultStr(RuntimeState.getTrafficStatusText()));
        Vector params = ObjectPool.newVector();
        params.addElement(newMessage.toHashtable());
        AppState.getAccount().sendChatRoomRequest(ApiClient.createUploadRequest(StringPool.get(PackedStringKeys.URL_PATH_AJAX_SENDMSG), ApiClient.appendAuthParams(ObjectPool.newStringBuffer().append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL)).append(StringPool.get(PackedStringKeys.FUNC_AJAX_SEND_MSG)), Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params)))));
    }

    private static void buildSendToContact() {
        Vector allContacts = AccountManager.getAllContacts();
        for (int idx = allContacts.size() - 1; idx >= 0; idx--) {
            if (((Contact) allContacts.elementAt(idx)).isOffline()) {
                allContacts.removeElementAt(idx);
            }
        }
        if (allContacts.size() == 0) {
            NotificationHelper.showMessageById(762);
        } else {
            ContactListManager.sortContacts(allContacts);
            ScreenManager.showScreen(ContactListManager.addContactItems(Screens.sendToContact(), allContacts));
        }
        ObjectPool.releaseVector(allContacts);
    }

    // --- Selection handlers ---

    private static int handleComposeMessage() {
        ScreenManager.processScreenForm();
        Vector params = ObjectPool.newVector();
        StringBuffer sb = ObjectPool.newStringBuffer();
        String recipientStr = Utils.defaultStr(RuntimeState.getMsgExtra2());
        int length = recipientStr.length();
        int i = 0;
        while (i <= length) {
            char ch = i == length ? ';' : recipientStr.charAt(i);
            if (ch == ';' || ch == ',' || ch == ' ') {
                String token = StringUtils.extractBuffer(sb);
                if (!StringUtils.isEmpty(token)) {
                    params.addElement(token);
                }
            } else {
                sb.append(ch);
            }
            i++;
        }
        if (Utils.vectorSize(params) == 0) {
            return NotificationHelper.showError(873);
        }
        boolean invalid = false;
        for (int count = Utils.vectorSize(params) - 1; count >= 0; count--) {
            String str = (String) params.elementAt(count);
            int atIdx = str.indexOf('@');
            if (atIdx <= 0 || str.indexOf('.') <= 0 || str.indexOf(' ') >= 0 || atIdx != str.lastIndexOf('@') || str.indexOf(',') >= 0) {
                invalid = true;
            }
        }
        return invalid ? NotificationHelper.showError(876) : 0;
    }

    private static int handleMessageInput() {
        ScreenManager.processScreenForm();
        String messageText = Utils.defaultStr(UIState.getScreenValue());
        if (StringUtils.isEmpty(messageText)) {
            return NotificationHelper.showError(523);
        }
        Account blogAccount = AppState.getCurrentContact().account;
        boolean flag = ContactState.getGroupOperationResult() != 0;
        long timestamp = RuntimeState.getSelectedMsgTimestamp();
        int sendResult = blogAccount.sendBlogPost(messageText, flag, timestamp);
        return sendResult != 0 ? NotificationHelper.showError(sendResult) : 0;
    }

    private static int handleFileAction(Object contactObj) {
        int errorCode = ((Contact) contactObj).sendMessage(MapState.getResourceUrl());
        return errorCode != 0 ? NotificationHelper.showError(errorCode) : 0;
    }

    // --- Idle processing ---

    private static int processSendMailIdle() {
        Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
        if (asyncResult == null) {
            return 0;
        }
        int responseCode = ApiClient.validateJsonResponse(asyncResult);
        if (responseCode != 0) {
            return responseCode;
        }
        Account mailAccount = AppState.getAccount();
        ChatRoom lastChatRoom = mailAccount.getLastChatRoom();
        Vector vector = (Vector) ApiClient.getJsonPayload();
        lastChatRoom.clear();
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            Hashtable hashtable = (Hashtable) vector.elementAt(i);
            Vector vector2 = (Vector) JsonParser.getValue(hashtable, StringPool.get(PackedStringKeys.MAIL_PARAM_MLIST));
            String jsonStr = JsonParser.getStringByInt(hashtable, 198561);
            int size2 = vector2.size();
            for (int j = 0; j < size2; j++) {
                Vector vector3 = lastChatRoom.messageIds;
                Object objElement = vector2.elementAt(j);
                vector3.addElement(objElement);
                lastChatRoom.metadata.put(objElement, jsonStr);
            }
        }
        lastChatRoom.isInitialized = false;
        int vecSize = Utils.vectorSize(lastChatRoom.messageIds);
        if (vecSize > 0) {
            lastChatRoom.subject = (String) lastChatRoom.messageIds.lastElement();
            for (int i2 = 0; i2 < vecSize; i2++) {
                String messageId = Utils.getVectorString(lastChatRoom.messageIds, i2);
                Message message = mailAccount.findChatRoomById(Utils.parseInt(lastChatRoom.metadata.get(messageId))).getMessage(messageId);
                if (message != null) {
                    lastChatRoom.messages.put(messageId, message);
                } else {
                    lastChatRoom.participants.addElement(messageId);
                    lastChatRoom.isInitialized = true;
                }
            }
            lastChatRoom.name = new StringBuffer().append(StringPool.get(StringResKeys.STR_CHATROOM_PREFIX)).append(lastChatRoom.messageIds.size()).toString();
        }
        if (lastChatRoom.messageIds.size() == 0) {
            return NotificationHelper.showError(736);
        }
        ChatState.setChatRoomId(lastChatRoom.id);
        return ScreenId.CHAT_ROOM_MESSAGES;
    }

    private static int processReplyMailIdle() {
        Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
        if (asyncResult == null) {
            return 0;
        }
        int responseCode = ApiClient.validateJsonResponse(asyncResult);
        return responseCode != 0 ? responseCode : StringUtils.isEmpty((String) ApiClient.getJsonPayload()) ? NotificationHelper.showError(878) : 83;
    }

    private static int processSendDataIdle() {
        Vector vec = UIState.getScreenTitleAsVector();
        if (Utils.vectorSize(vec) <= 1) {
            ObjectPool.releaseVector(vec);
            EventDispatcher.postNotification(StringPool.get(StringResKeys.STR_EXIT_CONFIRM));
            return ScreenId.CONTACT_LIST;
        }
        Object objElement = vec.elementAt(0);
        if (objElement instanceof String) {
            Object[] objArr = {(String) objElement, StringUtils.concatKey(PackedStringKeys.INVITE_MESSAGE_RU, Conversation.percentEncode((String) vec.lastElement())), null};
            new AsyncTask(AsyncTaskId.SEND_SMS_DIRECT, objArr);
            vec.setElementAt(objArr, 0);
        } else {
            Object obj = ((Object[]) objElement)[2];
            if (obj != null) {
                if (obj instanceof Throwable) {
                    EventDispatcher.postNotification(StringUtils.concatKeyObj(1030, obj));
                } else {
                    Utils.dequeue(vec);
                }
            }
        }
        return 0;
    }
}
