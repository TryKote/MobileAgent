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

public final class MessageHandler extends BaseScreenHandler {


    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.MESSAGE_DETAIL:
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                String msgId = AppState.getString(RuntimeKeys.SLOT_MESSAGE_ID);
                Message message = (Message) ((MrimAccount) AppState.getAccount()).chatRoomManager.findById(AppState.getInt(ChatKeys.INT_CHATROOM_ID)).messages.get(msgId);
                Message messageWithBody = message.body != null ? message : null;
                NotificationHelper.showConfirmDialog(48, 837);
                if (messageWithBody == null) {
                    Vector params = ObjectPool.newVector();
                    params.addElement(msgId);
                    params.addElement(AppState.emptyStr);
                    params.addElement(ObjectPool.unpackChars(6775156));
                    MrimChatRoomManager.sendChatRoomRequest(ApiClient.createAuthRequest(ObjectPool.newStringBuffer().append(AppState.getString(PackedStringKeys.URL_PATH_AJAX_READMSG)).append('?').append(AppState.getString(PackedStringKeys.PARAM_AJAX_CALL)).append(AppState.getString(PackedStringKeys.FUNC_AJAX_GET_MSG_DATA)).append(AppState.getString(SessionKeys.SLOT_SESSION_HASH)).append(AppState.getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
                }
                return;
            case ScreenId.MESSAGE_PREVIEW:
                String msgId3 = AppState.getString(RuntimeKeys.SLOT_MESSAGE_ID);
                ChatRoom chatRoom3 = ((MrimAccount) AppState.getAccount()).chatRoomManager.findById(AppState.getInt(ChatKeys.INT_CHATROOM_ID));
                int roomType = chatRoom3.getType();
                Message message4 = chatRoom3.getMessage(msgId3);
                String str;
                if (roomType == 2) {
                    String[] ccRecipient = MailHelper.getFirstRecipient(message4.ccList);
                    str = ccRecipient != null ? ccRecipient[1] : AppState.emptyStr;
                } else {
                    String[] toRecipient = MailHelper.getFirstRecipient(message4.toList);
                    str = toRecipient != null ? toRecipient[1] : AppState.emptyStr;
                }
                AppState.setObject(UIKeys.SLOT_SCREEN_TITLE, (Object) str);
                AppState.setObject(UIKeys.SLOT_SCREEN_SUBTITLE, (Object) Utils.normalizeSpaces(message4.getSubject()));
                AppState.setObject(UIKeys.SLOT_SCREEN_VALUE, (Object) Utils.normalizeSpaces(message4.body));
                ListView screen3 = ScreenManager.createScreen(ScreenDef.MESSAGE_PREVIEW);
                Object[] objArr = message4.attachments;
                if (objArr != null) {
                    for (Object obj2 : objArr) {
                        screen3.addItem(MenuItem.createSeparator().setIcon(221).addText(((String[]) obj2)[1], 1, 0));
                    }
                }
                ScreenManager.showScreen(screen3);
                AppController.clearPreviewState();
                return;
            case ScreenId.COMPOSE_RECIPIENTS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.COMPOSE_RECIPIENTS));
                return;
            case ScreenId.COMPOSE_MESSAGE:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.COMPOSE_MESSAGE));
                return;
            case ScreenId.MAIL_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAIL_MENU));
                return;
            case ScreenId.SEND_MAIL:
                NotificationHelper.showConfirmDialog(81, 872);
                Vector params9 = ObjectPool.newVector();
                params9.addElement(AppState.getString(AppState.getBool(ChatKeys.FLAG_EXTENDED_CHAT_VIEW) ? 264068 : 1038));
                JsonParser.addIntToVector(params9, AppState.getInt(ChatKeys.INT_CHATROOM_ID));
                params9.addElement(Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_MSG_SUBJECT)));
                params9.addElement(Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_MSG_SENDER)));
                params9.addElement(Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_MSG_BODY)));
                params9.addElement(Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_MSG_EXTRA_1)));
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(AppState.getString(PackedStringKeys.URL_PATH_MAILBOX), ObjectPool.newStringBuffer().append(AppState.getString(PackedStringKeys.PARAM_AJAX_CALL)).append(AppState.getString(PackedStringKeys.FUNC_MAJAX_SEARCH)).append(AppState.getString(SessionKeys.SLOT_SESSION_HASH)).append(AppState.getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params9)))));
                return;
            case ScreenId.REPLY_MAIL:
                NotificationHelper.showConfirmDialog(82, 877);
                Message newMessage = new Message(MailHelper.parseRecipientList(Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_MSG_EXTRA_2))), Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_MSG_EXTRA_3)), Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_TRAFFIC_STATUS_TEXT)));
                Vector params10 = ObjectPool.newVector();
                params10.addElement(newMessage.toHashtable());
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(AppState.getString(PackedStringKeys.URL_PATH_AJAX_SENDMSG), ApiClient.appendAuthParams(ObjectPool.newStringBuffer().append(AppState.getString(PackedStringKeys.PARAM_AJAX_CALL)).append(AppState.getString(PackedStringKeys.FUNC_AJAX_SEND_MSG)), Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params10)))));
                return;
            case ScreenId.MESSAGE_INPUT:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MESSAGE_INPUT));
                AppController.clearPreviewState();
                return;
            case ScreenId.SEND_TO_CONTACT:
                Vector allContacts = AccountManager.getAllContacts();
                int size = allContacts.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        if (allContacts.size() == 0) {
                            NotificationHelper.showMessageById(762);
                        } else {
                            ContactListManager.sortContacts(allContacts);
                            ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.SEND_TO_CONTACT), allContacts));
                        }
                        ObjectPool.releaseVector(allContacts);
                        return;
                    }
                    if (((Contact) allContacts.elementAt(size)).isOffline()) {
                        allContacts.removeElementAt(size);
                    }
                }
            case ScreenId.MESSAGE_SUMMARY:
                ScreenManager.showScreen(AppState.getCurrentContact().showMessageSummary());
                return;
            case ScreenId.DELETE_MESSAGES:
                NotificationHelper.showAlertBuffer(128, ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_DELETE_CONFIRM)).append(AppState.getCurrentContact().displayName).append(ObjectPool.unpackChars(16167)));
                return;
            case ScreenId.SEND_CONFIRM:
                NotificationHelper.showConfirmDialog(161, 872);
                return;
            case ScreenId.NOTIFY_MESSAGE:
                AppState.setInt(UIKeys.FLAG_CONVERSATION_ACTIVE, 0);
                NotificationHelper.showAlertBuffer(163, ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_NOTIFY_MESSAGE)));
                return;
            case ScreenId.MAILBOX_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAILBOX_OPTIONS));
                return;
            case ScreenId.SEND_DATA:
                NotificationHelper.showConfirmDialog(179, 504);
                return;
        }
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.MESSAGE_DETAIL:
                return -1;
            case ScreenId.MESSAGE_PREVIEW:
                return 0;
            case ScreenId.COMPOSE_RECIPIENTS:
                return MailHelper.handleMailForwardAction(title);
            case ScreenId.COMPOSE_MESSAGE:
                ScreenManager.processScreenForm();
                Vector params = ObjectPool.newVector();
                StringBuffer sb = ObjectPool.newStringBuffer();
                String recipientStr = Utils.defaultStr(AppState.getString(RuntimeKeys.SLOT_MSG_EXTRA_2));
                int length = recipientStr.length();
                int i = 0;
                while (i <= length) {
                    char ch = i == length ? ';' : recipientStr.charAt(i);
                    char c = ch;
                    if (ch == ';' || c == ',' || c == ' ') {
                        String token = StringUtils.extractBuffer(sb);
                        if (!StringUtils.isEmpty(token)) {
                            params.addElement(token);
                        }
                    } else {
                        sb.append(c);
                    }
                    i++;
                }
                int errorCode;
                if (Utils.vectorSize(params) == 0) {
                    errorCode = NotificationHelper.showError(873);
                } else {
                    boolean z = false;
                    int count = Utils.vectorSize(params);
                    while (true) {
                        count--;
                        if (count < 0) {
                            errorCode = z ? NotificationHelper.showError(876) : 0;
                            break;
                        } else {
                            String str = (String) params.elementAt(count);
                            int atIdx = str.indexOf(64);
                            if (atIdx <= 0 || str.indexOf(46) <= 0 || str.indexOf(32) >= 0 || atIdx != str.lastIndexOf(64) || str.indexOf(44) >= 0) {
                                z = true;
                            }
                        }
                    }
                }
                return errorCode;
            case ScreenId.MAIL_MENU:
                return MailHelper.handleMailMenuAction(title, action);
            case ScreenId.SEND_MAIL:
                return -1;
            case ScreenId.REPLY_MAIL:
                return -1;
            case ScreenId.MESSAGE_INPUT:
                ScreenManager.processScreenForm();
                String messageText = Utils.defaultStr(AppState.getString(UIKeys.SLOT_SCREEN_VALUE));
                int errorCode6;
                if (StringUtils.isEmpty(messageText)) {
                    errorCode6 = NotificationHelper.showError(523);
                } else {
                    MrimAccount mrimAccount = (MrimAccount) AppState.getCurrentContact().account;
                    boolean flag = AppState.getBool(ContactKeys.INT_GROUP_OPERATION_RESULT);
                    long timestamp = AppState.getLong(RuntimeKeys.TIMESTAMP_SELECTED_MSG);
                    int sendResult;
                    if (mrimAccount.isConnected()) {
                        EventDispatcher.postNotification(AppState.getString(StringResKeys.STR_OPERATION_COMPLETE));
                        sendResult = mrimAccount.trySendData(ProtocolFactory.createMrimPacket(mrimAccount, 4196, new ByteBuffer().writeIntLE(flag ? 5 : 20).writeStringUTF16(messageText).writeLong(timestamp)));
                    } else {
                        sendResult = 299;
                    }
                    errorCode6 = 0 != sendResult ? NotificationHelper.showError(sendResult) : 0;
                }
                return errorCode6;
            case ScreenId.SEND_TO_CONTACT:
                return handleFileAction(data);
            case ScreenId.MESSAGE_SUMMARY:
                return MapController.handleLocationAction(data);
            case ScreenId.DELETE_MESSAGES:
                AppState.getCurrentContact().initMessageBuffer();
                return ScreenId.CONTACT_LIST;
            case ScreenId.SEND_CONFIRM:
                return -1;
            case ScreenId.NOTIFY_MESSAGE:
                return AccountManager.handleSendKey();
            case ScreenId.MAILBOX_OPTIONS:
                return MapHandler.handleMapModeOption(action);
            case ScreenId.SEND_DATA:
                return -1;
            default:
                return 0;
        }
    }

    public int onMenuItemAction(ListView screen, MenuItem item, Object data) {
        switch (screen.screenId) {
            case ScreenId.MESSAGE_DETAIL:
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                return 0;
            case ScreenId.MESSAGE_PREVIEW:
                return 0;
            case ScreenId.COMPOSE_RECIPIENTS:
                return 0;
            case ScreenId.COMPOSE_MESSAGE:
                return 0;
            case ScreenId.MAIL_MENU:
                return 0;
            case ScreenId.SEND_MAIL:
                return 0;
            case ScreenId.REPLY_MAIL:
                return 0;
            case ScreenId.MESSAGE_INPUT:
                return 0;
            case ScreenId.SEND_TO_CONTACT:
                return 0;
            case ScreenId.MESSAGE_SUMMARY:
                return 0;
            case ScreenId.DELETE_MESSAGES:
                return 0;
            case ScreenId.SEND_CONFIRM:
                return ScreenId.CONTACT_LIST;
            case ScreenId.NOTIFY_MESSAGE:
                return 0;
            case ScreenId.MAILBOX_OPTIONS:
                return 0;
            case ScreenId.SEND_DATA:
                return 0;
            default:
                return 0;
        }
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.MESSAGE_DETAIL:
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.COMPOSE_MESSAGE:
                AppState.clearRange(RuntimeKeys.SLOT_MSG_EXTRA_2, RuntimeKeys.SLOT_TRAFFIC_STATUS_TEXT);
                break;
            case ScreenId.SEND_MAIL:
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.REPLY_MAIL:
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.SEND_DATA:
                AppState.clearIndex(UIKeys.SLOT_SCREEN_TITLE);
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.MESSAGE_DETAIL:
                return -1;
            case ScreenId.MESSAGE_PREVIEW:
                return 0;
            case ScreenId.COMPOSE_RECIPIENTS:
                return MailHelper.handleMailForwardAction(title);
            case ScreenId.COMPOSE_MESSAGE:
                return 0;
            case ScreenId.MAIL_MENU:
                return MailHelper.handleMailMenuAction(title, selectedOption);
            case ScreenId.SEND_MAIL:
                return -1;
            case ScreenId.REPLY_MAIL:
                return -1;
            case ScreenId.MESSAGE_INPUT:
                return 0;
            case ScreenId.SEND_TO_CONTACT:
                return handleFileAction(data);
            case ScreenId.MESSAGE_SUMMARY:
                return MapController.handleLocationAction(data);
            case ScreenId.DELETE_MESSAGES:
                AppState.getCurrentContact().initMessageBuffer();
                return ScreenId.CONTACT_LIST;
            case ScreenId.SEND_CONFIRM:
                return -1;
            case ScreenId.NOTIFY_MESSAGE:
                return AccountManager.handleSendKey();
            case ScreenId.MAILBOX_OPTIONS:
                return MapHandler.handleMapModeOption(selectedOption);
            case ScreenId.SEND_DATA:
                return -1;
            default:
                return 0;
        }
    }

    public static int handleFileAction(Object contactObj) {
        int errorCode = ((Contact) contactObj).sendMessage(AppState.getString(MapKeys.MAP_RESOURCE_URL));
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.MESSAGE_DETAIL:
                return MailHelper.processMailResponse();
            case ScreenId.MESSAGE_PREVIEW:
                return 0;
            case ScreenId.COMPOSE_RECIPIENTS:
                return 0;
            case ScreenId.COMPOSE_MESSAGE:
                return 0;
            case ScreenId.MAIL_MENU:
                return 0;
            case ScreenId.SEND_MAIL: {
                Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (asyncResult != null) {
                    int responseCode = ApiClient.validateJsonResponse(asyncResult);
                    if (responseCode != 0) {
                        return responseCode;
                    }
                    ChatRoom lastChatRoom = ((MrimAccount) AppState.getAccount()).chatRoomManager.getLast();
                    Vector vector = (Vector) ApiClient.getJsonPayload();
                    lastChatRoom.clear();
                    int size = vector.size();
                    for (int i = 0; i < size; i++) {
                        Hashtable hashtable = (Hashtable) vector.elementAt(i);
                        Vector vector2 = (Vector) JsonParser.getValue(hashtable, AppState.getString(PackedStringKeys.MAIL_PARAM_MLIST));
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
                        MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
                        for (int i2 = 0; i2 < vecSize; i2++) {
                            String messageId = Utils.getVectorString(lastChatRoom.messageIds, i2);
                            Message message = mrimAccount.chatRoomManager.findById(Utils.parseInt(lastChatRoom.metadata.get(messageId))).getMessage(messageId);
                            if (message != null) {
                                lastChatRoom.messages.put(messageId, message);
                            } else {
                                lastChatRoom.participants.addElement(messageId);
                                lastChatRoom.isInitialized = true;
                            }
                        }
                        lastChatRoom.name = new StringBuffer().append(AppState.getString(StringResKeys.STR_CHATROOM_PREFIX)).append(lastChatRoom.messageIds.size()).toString();
                    }
                    if (lastChatRoom.messageIds.size() == 0) {
                        return NotificationHelper.showError(736);
                    }
                    AppState.setInt(ChatKeys.INT_CHATROOM_ID, lastChatRoom.id);
                    return ScreenId.CHAT_ROOM_MESSAGES;
                }
                return 0;
            }
            case ScreenId.REPLY_MAIL: {
                Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (asyncResult != null) {
                    int responseCode = ApiClient.validateJsonResponse(asyncResult);
                    return responseCode != 0 ? responseCode : StringUtils.isEmpty((String) ApiClient.getJsonPayload()) ? NotificationHelper.showError(878) : 83;
                }
                return 0;
            }
            case ScreenId.MESSAGE_INPUT:
                return 0;
            case ScreenId.SEND_TO_CONTACT:
                return 0;
            case ScreenId.MESSAGE_SUMMARY:
                return 0;
            case ScreenId.DELETE_MESSAGES:
                return 0;
            case ScreenId.SEND_CONFIRM:
                return MapController.showMapSearchResults();
            case ScreenId.NOTIFY_MESSAGE:
                return 0;
            case ScreenId.MAILBOX_OPTIONS:
                return 0;
            case ScreenId.SEND_DATA: {
                Vector vec = AppState.getVector(UIKeys.SLOT_SCREEN_TITLE);
                if (Utils.vectorSize(vec) <= 1) {
                    ObjectPool.releaseVector(vec);
                    EventDispatcher.postNotification(AppState.getString(StringResKeys.STR_EXIT_CONFIRM));
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
            default:
                return 0;
        }
    }
}
