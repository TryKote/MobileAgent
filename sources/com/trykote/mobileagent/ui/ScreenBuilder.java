package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
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

/* renamed from: au */
/* loaded from: MobileAgent_3.9.jar:au.class */
public final class ScreenBuilder {
    /* renamed from: a */
    public static final void openScreen(int i) {
        RemoteLogger.log("UI", "openScreen(" + i + ")");
        boolean z;
        String str;
        String[] regData;
        Object[] request;
        int i2;
        AppController.markScreenDirty();
        AppController.needsRepaint = true;
        while (true) {
            if (!ScreenManager.hasScreen(i)) {
                Vector screenStack = AppState.getVector(StateKeys.VEC_SCREEN_STACK);
                int size = screenStack.size();
                z = false;
                while (--size >= 0) {
                    i2 = ((Screen) screenStack.elementAt(size)).screenType;
                    if (i2 == 7 || i2 == 8) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    break;
                }
            }
            onScreenClosed();
        }
        switch (i) {
            case ScreenId.NONE:
                return;
            case ScreenId.ACCOUNT_LIST:
                int size2 = AppState.getVector(StateKeys.VEC_ACCOUNTS).size();
                AppState.setBool(StateKeys.FLAG_HAS_MRIM_ACCOUNTS, size2 > 0);
                AppState.setBool(StateKeys.FLAG_HAS_MULTIPLE_MRIM, size2 > 1);
                AppState.setBool(StateKeys.FLAG_HAS_MRIM_ACCOUNTS_2, AppState.getBool(StateKeys.FLAG_HAS_MRIM_ACCOUNTS));
                AppState.setBool(StateKeys.FLAG_HAS_XMPP_ACCOUNTS, size2 > 0);
                ScreenManager.showScreen(ScreenManager.createScreen(2361));
                return;
            case ScreenId.SETTINGS:
                AppController.showSettingsScreen();
                return;
            case ScreenId.STATUS_DIALOG:
                Screen dialogScreen = ScreenManager.createDialogScreen(3);
                Account account = AppState.getAccount();
                switch (account.getType()) {
                    case 0:
                        dialogScreen.addIconById(156, 642, 0).addIconById(159, 643, 1).addIconById(157, 644, 2).addIconById(160, 645, 3).addIconById(158, 646, 4).addIconById(155, 647, 5).addActionById(-1, 718, 6);
                        break;
                    case 1:
                        MmpProtocol mmpProtocol = (MmpProtocol) account;
                        int iconResId = mmpProtocol.getIconResourceId();
                        dialogScreen.addIconById(iconResId, 642, 0).addIconById(iconResId | 16384000, 643, 1).addIconById(iconResId | 16449536, 645, 3).addIconById(iconResId | 16318464, 644, 4).addIconById(iconResId | 16580608, 648, 5).addIconById(iconResId | 16646144, 654, 6).addIconById(iconResId | 17039360, 649, 7).addIconById(iconResId | 16973824, 650, 8).addIconById(iconResId | 16908288, 651, 9).addIconById(iconResId | 16842752, 652, 10).addIconById(iconResId | 17104896, 653, 11).addIconById(iconResId | 16515072, 646, 2).addIconById(255, 647, 12).addActionById(mmpProtocol.getExtType(), 655, 14).addActionById(-1, 718, 13);
                        break;
                    default:
                        if (((XmppProtocol) account).mo83f()) {
                            dialogScreen.addIconById(387, 642, 1).addIconById(385, 647, 0);
                            break;
                        } else {
                            dialogScreen.addIconById(383, 642, 1).addIconById(16384383, 643, 4).addIconById(16318847, 644, 2).addIconById(16449919, 645, 5).addIconById(16580991, 648, 6).addIconById(16515455, 646, 3).addIconById(381, 647, 0);
                            break;
                        }
                }
                ScreenManager.showScreen(dialogScreen.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_YES), AppState.getString(StateKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.CONTACT_LIST:
                ContactListManager.showContactList();
                return;
            case ScreenId.ACCOUNTS_MENU:
                AppState.setBool(StateKeys.FLAG_MULTIPLE_MRIM, AccountManager.getMrimAccountList().size() > 1);
                AppState.setBool(StateKeys.FLAG_MULTIPLE_XMPP, AccountManager.getXmppAccountList().size() > 1);
                ScreenManager.showScreen(ScreenManager.createScreen(2733));
                return;
            case ScreenId.MAP:
                ConnectionThread.showMapScreen();
                return;
            case ScreenId.MAP_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(2641));
                return;
            case ScreenId.SETTINGS_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(3959));
                return;
            case ScreenId.ABOUT:
                AppState.setInt(StateKeys.FLAG_SHOW_NOTIFICATION, 1);
                AppState.setObject(StateKeys.SLOT_SCREEN_TITLE, (Object) StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory())));
                AppState.setObject(StateKeys.SLOT_SCREEN_SUBTITLE, (Object) AppController.getAppVersion());
                AppState.setFromBuffer(StateKeys.SLOT_APP_VERSION_STRING, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_APP_NAME)).append(AppState.getString(StateKeys.STR_APP_BUILD_SUFFIX)));
                AppState.setObject(StateKeys.SLOT_DEVICE_ID, (Object) new ByteBuffer().writeLongBytes(7234309766870429269L).writeByte(44).writeRawString(AppState.getAppProperty(StateKeys.STR_APP_PROPERTY_NAME)).getStringAndClear());
                ScreenManager.showScreen(ScreenManager.createScreen(2448));
                return;
            case ScreenId.BLOCK_CONFIRM:
                NetworkUtils.showAlertById(10, 710);
                return;
            case ScreenId.UNBLOCK_CONFIRM:
                NetworkUtils.showAlertBuffer(11, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_BLOCK_CONFIRM)).append(AppState.getCurrentContact().displayName).append(NetworkUtils.longToHex(16167)));
                return;
            case ScreenId.CLOSE:
                return;
            case ScreenId.CONFIRM_EXIT:
                NetworkUtils.showConfirmDialog(13, 505);
                return;
            case ScreenId.TRAFFIC_COST:
                StringBuffer sb = NetworkUtils.newStringBuffer();
                int intVal = AppState.getInt(StateKeys.SETTING_TRAFFIC_COST);
                AppState.setFromBuffer(StateKeys.SLOT_SCREEN_VALUE, sb.append(intVal / 100).append('.').append(Utils.zeroPad(intVal % 100)));
                ScreenManager.showScreen(ScreenManager.createScreen(3183));
                return;
            case ScreenId.ACCOUNT_SWITCHER:
                Screen contactListScreen = NetworkUtils.addContactItems(ScreenManager.createScreen(2719), AppState.getVector(StateKeys.VEC_ACCOUNTS));
                Account currentAccount = TabBar.currentAccount;
                if (currentAccount != null) {
                    contactListScreen.selectByTitle(currentAccount.getSignature());
                }
                ScreenManager.showScreen(contactListScreen);
                return;
            case ScreenId.REGISTRATION:
                AppState.setInt(StateKeys.INT_PROTOCOL_TYPE, 0);
                ScreenManager.showScreen(ScreenManager.createScreen(4467));
                return;
            case ScreenId.EMOTICON_DIALOG:
                Screen dialogScreen2 = ScreenManager.createDialogScreen(17);
                if (AppState.getAccount() instanceof MmpProtocol) {
                    for (int i3 = 0; i3 <= 36; i3++) {
                        dialogScreen2.addIconById(i3 + 268, i3 + 118, i3);
                    }
                } else {
                    for (int i4 = 0; i4 <= 49; i4++) {
                        if (i4 != 21 && i4 != 27) {
                            dialogScreen2.addIconById(i4 + 161, i4 + 155, i4 + 4);
                        }
                    }
                }
                ScreenManager.showScreen(dialogScreen2.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_YES), AppState.getString(StateKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.UNUSED_18:
                return;
            case ScreenId.CONTACT_EDITOR:
                AppController.clearFormFields();
                Contact contact = AppState.getCurrentContact();
                AppState.setObject(StateKeys.SLOT_INPUT_TEXT, (Object) contact.displayName);
                Vector nameParts = Utils.splitNonEmpty(contact.getDefaultName(), ',');
                for (int i5 = 0; i5 < 3; i5++) {
                    if (i5 < nameParts.size()) {
                        AppState.pool[i5 + 1303] = nameParts.elementAt(i5);
                    }
                }
                NetworkUtils.releaseVector(nameParts);
                AppState.setInt(StateKeys.INT_CONTACT_TYPE_CODE, (!(contact instanceof MrimContact) || contact.isSystem()) ? 1 : 4);
                ScreenManager.showScreen(ScreenManager.createScreen(3501));
                return;
            case ScreenId.GPS_SETTINGS:
                boolean flag = AppState.getBool(StateKeys.MAP_GPS_ENABLED);
                boolean flag2 = AppState.getBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE);
                AppState.setBool(StateKeys.FLAG_GPS_NO_MAP, !flag && flag2);
                AppState.setBool(StateKeys.FLAG_GPS_WITH_MAP, flag && flag2);
                ScreenManager.showScreen(ScreenManager.createScreen(1632));
                return;
            case ScreenId.ADD_CONTACT:
                int accountType = AppState.getAccount().getType();
                if (accountType == 0) {
                    StringUtils.showRegionSelector();
                    return;
                }
                if (accountType == 1) {
                    AppState.setInt(StateKeys.INT_REG_DOMAIN_INDEX, -1);
                    ScreenManager.showScreen(ScreenManager.createScreen(3569));
                    return;
                }
                StringUtils.resetRegForm();
                if (IOUtils.getGroupCount(AppState.getAccount()) == 0) {
                    IOUtils.postNotification(AppState.getString(StateKeys.STR_NOTIFICATION_NEW_MSG));
                    return;
                } else {
                    ScreenManager.showScreen(ScreenManager.createScreen(3939));
                    return;
                }
            case ScreenId.ADD_MRIM_CONTACT:
                ScreenManager.showScreen(ScreenManager.createScreen(3535));
                return;
            case ScreenId.UNUSED_23:
                return;
            case ScreenId.UNUSED_24:
                return;
            case ScreenId.MULTI_ACCOUNT_LIST:
                Screen screen = ScreenManager.createScreen(2581);
                Vector accounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
                int size3 = accounts.size();
                for (int i6 = 0; i6 < size3; i6++) {
                    screen.addItem(((Account) accounts.elementAt(i6)).createFlagMenuItem());
                }
                ScreenManager.showScreen(screen.addActionById(-1, 531, 16).addIconById(-1, 532, 1).addIconById(-1, 533, 3).addIconById(-1, 534, 2));
                return;
            case ScreenId.THEME_SETTINGS:
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, AppState.getInt(StateKeys.SETTING_COLOR_THEME));
                ScreenManager.showScreen(ScreenManager.createScreen(2917));
                return;
            case ScreenId.NOTIFICATION_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(3214));
                return;
            case ScreenId.SOUND_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(2978));
                return;
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, AppState.getInt(StateKeys.SETTING_MULTI_ACCOUNT));
                ScreenManager.showScreen(ScreenManager.createScreen(3102));
                return;
            case ScreenId.CONTACT_GROUP_MENU:
                AppState.setInt(StateKeys.FLAG_CONTACT_MENU_MODE, 1);
                Object obj = AppState.pool[StateKeys.SLOT_CURRENT_ENTITY];
                if (obj instanceof ContactGroup) {
                    AppState.setInt(StateKeys.FLAG_IS_MRIM_CONTACT, 1);
                    ScreenManager.showScreen(ScreenManager.createScreen(3686));
                    return;
                }
                Contact selectedContact = (Contact) obj;
                if (selectedContact.isSystem()) {
                    AppState.setInt(StateKeys.INT_CANCEL_MENU_ACTION, 30);
                    AppState.setInt(StateKeys.INT_CANCEL_MENU_TYPE, 4);
                    ScreenManager.showScreen(ScreenManager.createScreen(3783));
                    return;
                }
                IOUtils.updateContactFlags(selectedContact);
                AppState.setInt(StateKeys.FLAG_IS_MRIM_CONTACT, 0);
                boolean z2 = selectedContact instanceof MrimContact;
                boolean z3 = z2;
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_MRIM, z2);
                boolean z4 = z3 && selectedContact.isOffline();
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_GROUP, z4);
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_USER, z3 && !z4);
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_ONLINE, selectedContact.isOnline());
                AppState.setBool(StateKeys.FLAG_CONTACT_HAS_UNREAD, selectedContact.hasUnread() && !selectedContact.isOnline());
                AppState.setBool(StateKeys.FLAG_CONTACT_HAS_VCARD, z3 && !z4 && ((MrimContact) selectedContact).hasVCard());
                AppState.setInt(StateKeys.INT_OK_MENU_TYPE, 4);
                AppState.setInt(StateKeys.INT_OK_MENU_ACTION, 30);
                ScreenManager.showScreen(ScreenManager.createScreen(3704));
                return;
            case ScreenId.UNUSED_31:
            default:
                return;
            case ScreenId.UNUSED_32:
                return;
            case ScreenId.PRIVACY_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(2817));
                return;
            case ScreenId.TRAFFIC_STATS:
                ResourceManager.showTrafficStats();
                return;
            case ScreenId.CONNECTION_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(5157));
                return;
            case ScreenId.MAIL_ACCOUNT_LIST:
                ResourceManager.showMailAccountList();
                return;
            case ScreenId.CHAT_ROOMS:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
                if (mrimAccount.getChatRoomCount() != 0 && !mrimAccount.chatRoomsLoaded) {
                    AppController.initChatRoomList();
                    return;
                }
                NetworkUtils.showConfirmDialog(37, 833);
                Vector params = NetworkUtils.newVector();
                JsonParser.addIntToVector(params, 0);
                params.addElement(AppState.emptyStr);
                JsonParser.addIntToVector(params, 1);
                IOUtils.sendChatRoomRequest(ConnectionThread.createAuthRequest(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_LONG_URL_1)).append('?').append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_HUGE_URL_1)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
                return;
            case ScreenId.CHAT_ROOM_INIT:
                AppController.initChatRoomList();
                return;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                Vector accountList = AppState.getVector(StateKeys.VEC_FILTERED_ACCOUNTS);
                Screen screen2 = ScreenManager.createScreen(2581);
                screen2.screenId = ScreenId.ACCOUNT_CHECKBOX_LIST;
                screen2.showCheckboxes = true;
                int size4 = accountList.size();
                boolean showFlags = AppState.getBool(StateKeys.FLAG_SHOW_STATUS_FLAGS);
                for (int i7 = 0; i7 < size4; i7++) {
                    Object element = accountList.elementAt(i7);
                    if (!(element instanceof Account)) {
                        screen2.addActionById(11, 548, 0);
                    } else if (showFlags) {
                        screen2.addItem(((Account) element).createFlagMenuItem());
                    } else {
                        screen2.addItem(((Account) element).createMenuItem());
                    }
                }
                Account currentAccount2 = TabBar.currentAccount;
                if (currentAccount2 != null) {
                    screen2.selectByTitle(currentAccount2.getSignature());
                }
                ScreenManager.showScreen(screen2);
                NetworkUtils.releaseVector(accountList);
                return;
            case ScreenId.CLEAR_SEARCH:
                AppController.clearSearchState();
                return;
            case ScreenId.CHAT_ROOM_MESSAGES:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID));
                if (!chatRoom.isInitialized) {
                    IOUtils.showChatRoomMessages();
                    return;
                }
                NetworkUtils.showConfirmDialog(41, 836);
                MrimAccount mrimAccount2 = (MrimAccount) AppState.getAccount();
                Vector params2 = NetworkUtils.newVector();
                if (chatRoom == mrimAccount2.getLastChatRoom()) {
                    params2.addElement(StringUtils.intern(Integer.toString(0)));
                    params2.addElement(chatRoom.participants);
                    request = ConnectionThread.createUploadRequest(AppState.getString(StateKeys.STR_RES_LONG_URL_1), NetworkUtils.appendFromState(722608).append(AppState.getString(StateKeys.STR_RES_HUGE_URL_7)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                } else {
                    params2.addElement(StringUtils.intern(Integer.toString(chatRoom.id)));
                    int intVal2 = AppState.getInt(StateKeys.SETTING_TIMEOUT_VALUE);
                    params2.addElement(StringUtils.intern(Integer.toString(Utils.max(intVal2, chatRoom.messageIds.size() + (chatRoom.isActive ? intVal2 : 0)))));
                    params2.addElement(StringUtils.intern(Integer.toString(1)));
                    params2.addElement(AppState.emptyStr);
                    Vector messageIdParams = NetworkUtils.newVector();
                    Enumeration contactEnum = chatRoom.messageIds.elements();
                    while (contactEnum.hasMoreElements()) {
                        Hashtable hashtable = chatRoom.messages;
                        Object msgIdObj = contactEnum.nextElement();
                        if (hashtable.containsKey(msgIdObj)) {
                            messageIdParams.addElement(msgIdObj);
                        }
                    }
                    params2.addElement(messageIdParams);
                    request = ConnectionThread.createAuthRequest(NetworkUtils.appendFromState(1050207).append('?').append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_5)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                }
                IOUtils.sendChatRoomRequest(request);
                return;
            case ScreenId.CHAT_ROOM_INVITE:
                NetworkUtils.showConfirmDialog(42, 862);
                Vector params4 = NetworkUtils.newVector();
                JsonParser.addIntToVector(params4, AppState.getInt(StateKeys.INT_ACTIVE_CHATROOM_ID));
                params4.addElement(AppState.getVector(StateKeys.SLOT_MEDIA_STREAM));
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(StateKeys.STR_RES_LONG_URL_1), NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_4)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params4)))));
                return;
            case ScreenId.CHAT_ROOM_VIEW:
                IOUtils.showChatRoomMessages();
                return;
            case ScreenId.SUBMIT_REGISTRATION:
                AppController.prepareFormData();
                Account account2 = AppState.getAccount();
                if (AppState.getAccount().getType() == 0) {
                    regData = StringUtils.buildRegData();
                } else {
                    regData = new String[8];
                    int intVal3 = AppState.getInt(StateKeys.INT_REG_DOMAIN_INDEX);
                    regData[0] = intVal3 > 0 ? StringUtils.intern(Integer.toString(intVal3)) : AppState.emptyStr;
                    regData[1] = AppState.getString(AppState.getBool(StateKeys.FLAG_REG_SMS_MODE) ? 1046 : 1038);
                    regData[2] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_1));
                    regData[3] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_2));
                    regData[4] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_3));
                    regData[5] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_4));
                    regData[6] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_5));
                    regData[7] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_6));
                }
                NetworkUtils.showErrorOrConfirm(44, 729, account2.validateObject(regData));
                return;
            case ScreenId.UNUSED_45:
                return;
            case ScreenId.UNUSED_46:
                return;
            case ScreenId.CONTACT_GROUPS:
                ScreenManager.showScreen(ScreenManager.createScreen(2697));
                return;
            case ScreenId.MESSAGE_DETAIL:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                String msgId = AppState.getString(StateKeys.SLOT_MESSAGE_ID);
                Message message = (Message) ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID)).messages.get(msgId);
                Message messageWithBody = message.body != null ? message : null;
                NetworkUtils.showConfirmDialog(48, 837);
                if (messageWithBody == null) {
                    Vector params5 = NetworkUtils.newVector();
                    params5.addElement(msgId);
                    params5.addElement(AppState.emptyStr);
                    params5.addElement(NetworkUtils.longToHex(6775156));
                    IOUtils.sendChatRoomRequest(ConnectionThread.createAuthRequest(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_API_URL_2)).append('?').append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_VERY_LONG_API_3)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(params5)))));
                    return;
                }
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
                AppState.setFromBuffer(StateKeys.SLOT_UNREAD_COUNT_TEXT, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_UNREAD_COUNT_PREFIX)).append(size5).append(')'));
                ScreenManager.showScreen(ScreenManager.createScreen(4589));
                return;
            case ScreenId.MESSAGE_PREVIEW:
                String msgId3 = AppState.getString(StateKeys.SLOT_MESSAGE_ID);
                ChatRoom chatRoom3 = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID));
                int roomType = chatRoom3.getType();
                Message message4 = chatRoom3.getMessage(msgId3);
                if (roomType == 2) {
                    String[] ccRecipient = XmppMailRuProtocol.getFirstRecipient(message4.ccList);
                    str = ccRecipient != null ? ccRecipient[1] : AppState.emptyStr;
                } else {
                    String[] toRecipient = XmppMailRuProtocol.getFirstRecipient(message4.toList);
                    str = toRecipient != null ? toRecipient[1] : AppState.emptyStr;
                }
                AppState.setObject(StateKeys.SLOT_SCREEN_TITLE, (Object) str);
                AppState.setObject(StateKeys.SLOT_SCREEN_SUBTITLE, (Object) Utils.normalizeSpaces(message4.getSubject()));
                AppState.setObject(StateKeys.SLOT_SCREEN_VALUE, (Object) Utils.normalizeSpaces(message4.body));
                Screen screen3 = ScreenManager.createScreen(4537);
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
                ScreenManager.showScreen(ScreenManager.createScreen(4551));
                return;
            case ScreenId.COMPOSE_MESSAGE:
                ScreenManager.showScreen(ScreenManager.createScreen(4806));
                return;
            case ScreenId.DELETE_CONFIRM:
                NetworkUtils.showConfirmDialog(55, 761);
                return;
            case ScreenId.CONTACT_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(3052));
                return;
            case ScreenId.FIRST_RUN:
                NetworkUtils.showConfirmDialog(57, 730);
                AppState.setLong(StateKeys.TIMESTAMP_FIRST_RUN, System.currentTimeMillis());
                Object[] objArr2 = new Object[1];
                AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = objArr2;
                new AsyncTask(2, objArr2);
                NetworkUtils.checkCrashReport();
                return;
            case ScreenId.GROUP_SELECTOR:
                ScreenManager.showScreen(ScreenManager.createScreen(4729));
                return;
            case ScreenId.VERSION_CHECK:
                ResourceManager.processUpdateResult();
                return;
            case ScreenId.INPUT_DIALOG:
                ScreenManager.showScreen(ScreenManager.createScreen(4711));
                return;
            case ScreenId.CHAT_ROOM_ALERT:
                NetworkUtils.showAlertById(61, 857);
                return;
            case ScreenId.MAIL_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(4667));
                return;
            case ScreenId.STATUS_INPUT:
                XmppContactGroup.showTextInputDialog(AppState.getCurrentContact().displayName, AppState.getString(StateKeys.SLOT_STATUS_TEXT), 1000, StringUtils.isKnownDevice2 ? 2097152 : 0, AppState.getString(StateKeys.STR_INPUT_MODE_DEFAULT), 1059, 1055, new AsyncTask());
                AppState.setInt(StateKeys.INT_LAST_POLL_TIMESTAMP, 0);
                AppState.setInt(StateKeys.INT_LAST_CHECK_TIMESTAMP, 0);
                AppState.setInt(StateKeys.INT_LAST_LIST_SIZE, 0);
                ScreenManager.showScreen(new Screen());
                return;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                Contact contact2 = AppState.getCurrentContact();
                AppState.setInt(StateKeys.INT_DELETE_BUTTON_ICON, contact2.canDelete() ? 25 : 24);
                AppState.setInt(StateKeys.INT_BLOCK_BUTTON_ICON, contact2.canBlock() ? 25 : 24);
                ScreenManager.showScreen(ScreenManager.createScreen(4100));
                return;
            case ScreenId.PHONE_GROUPS:
                Vector phoneGroups = Utils.splitNonEmpty(AppState.getCurrentMrimContact().contactGroupsStr, ',');
                int size6 = phoneGroups.size();
                if (size6 <= 0) {
                    NotificationHelper.showMessageById(713);
                    return;
                }
                StringBuffer sb2 = NetworkUtils.newStringBuffer();
                for (int i8 = 0; i8 < size6; i8++) {
                    sb2.append(Utils.formatPhone((String) phoneGroups.elementAt(i8))).append((char) 0);
                }
                AppState.setFromBuffer(StateKeys.SLOT_SEARCH_LABEL_1, sb2);
                AppState.setInt(StateKeys.INT_SELECTED_GROUP_INDEX, 0);
                ScreenManager.showScreen(ScreenManager.createScreen(3627));
                return;
            case ScreenId.ADD_CONTACT_INFO:
                IOUtils.showAddContactScreen();
                return;
            case ScreenId.SOFTKEY_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(4633));
                return;
            case ScreenId.SEARCH_RESULTS:
                AppController.resetSearchResults();
                ScreenManager.showScreen(ScreenManager.createScreen(4769));
                return;
            case ScreenId.CREATE_GROUP:
                ScreenManager.showScreen(ScreenManager.createScreen(3340));
                return;
            case ScreenId.RENAME_GROUP:
                AppState.setObject(StateKeys.SLOT_SEARCH_RESULT, (Object) AppState.getCurrentGroup().name);
                ScreenManager.showScreen(ScreenManager.createScreen(3553));
                return;
            case ScreenId.DELETE_ENTITY:
                StringBuffer sbAlert = NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_ALERT_PREFIX));
                Object obj3 = AppState.pool[StateKeys.SLOT_CURRENT_ENTITY];
                NetworkUtils.showAlertBuffer(71, sbAlert.append(obj3 instanceof ContactGroup ? ((ContactGroup) obj3).name : ((Contact) obj3).displayName).append(NetworkUtils.longToHex(16167)));
                return;
            case ScreenId.BATCH_DELETE:
                NetworkUtils.showConfirmDialog(72, 866);
                Vector selectedItems = AppState.getVector(StateKeys.SLOT_MEDIA_STREAM);
                Vector itemsParams = NetworkUtils.newVector();
                int size7 = selectedItems.size();
                while (true) {
                    size7--;
                    if (size7 < 0) {
                        Vector outerParams = NetworkUtils.newVector();
                        outerParams.addElement(itemsParams);
                        IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(StateKeys.STR_RES_API_URL_1), NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_1)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncode((Object) JsonParser.toJson(outerParams)))));
                        return;
                    } else {
                        Hashtable hashtable2 = new Hashtable();
                        JsonParser.putIntKey(hashtable2, 329240, JsonParser.getVectorElement(selectedItems, size7));
                        JsonParser.putIntKey(hashtable2, 263673, ResourceManager.integerOf(AppState.getInt(StateKeys.INT_CHAT_VIEW_MODE)));
                        itemsParams.addElement(hashtable2);
                    }
                }
            case ScreenId.SEARCH_RESULT_LIST:
                int errorMsgId = AppState.getInt(StateKeys.INT_ERROR_MSG_INDEX);
                if (0 != errorMsgId) {
                    AppController.clearSearchResults2();
                    NotificationHelper.showMessageById(errorMsgId);
                    return;
                }
                Vector searchResults = AppState.getVector(StateKeys.SLOT_REG_PARAM_4);
                if (0 != searchResults.size()) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(3868), searchResults));
                    return;
                } else {
                    AppController.clearSearchResults2();
                    NotificationHelper.showMessageById(736);
                    return;
                }
            case ScreenId.UNUSED_74:
                return;
            case ScreenId.UNUSED_75:
                return;
            case ScreenId.XMPP_LOGIN:
                XmppMailRuProtocol.showLoginScreen();
                return;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                NetworkUtils.showAlertBuffer(77, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_ALERT_PREFIX)).append(AppState.getAccount().login).append(NetworkUtils.longToHex(16167)));
                return;
            case ScreenId.SHARE_MEDIA:
                NetworkUtils.showConfirmDialog(78, 861);
                Vector params8 = NetworkUtils.newVector();
                params8.addElement(AppState.getVector(StateKeys.SLOT_MEDIA_STREAM));
                JsonParser.addIntToVector(params8, AppState.getBool(StateKeys.FLAG_SPECIAL_KEY_MODE) ? 1 : 0);
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(StateKeys.STR_RES_API_URL_5), IOUtils.appendAuthParams(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_3)), Conversation.urlEncode((Object) JsonParser.toJson(params8)))));
                return;
            case ScreenId.SHARE_ALERT:
                NetworkUtils.showAlertById(79, 863);
                return;
            case ScreenId.NOTIFICATION_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(4747));
                return;
            case ScreenId.SEND_MAIL:
                NetworkUtils.showConfirmDialog(81, 872);
                Vector params9 = NetworkUtils.newVector();
                params9.addElement(AppState.getString(AppState.getBool(StateKeys.FLAG_EXTENDED_CHAT_VIEW) ? 264068 : 1038));
                JsonParser.addIntToVector(params9, AppState.getInt(StateKeys.INT_CHATROOM_ID));
                params9.addElement(Utils.defaultStr(AppState.getString(StateKeys.SLOT_MSG_SUBJECT)));
                params9.addElement(Utils.defaultStr(AppState.getString(StateKeys.SLOT_MSG_SENDER)));
                params9.addElement(Utils.defaultStr(AppState.getString(StateKeys.SLOT_MSG_BODY)));
                params9.addElement(Utils.defaultStr(AppState.getString(StateKeys.SLOT_MSG_EXTRA_1)));
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(StateKeys.STR_RES_LONG_URL_1), NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_API_URL_6)).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params9)))));
                return;
            case ScreenId.REPLY_MAIL:
                NetworkUtils.showConfirmDialog(82, 877);
                Message newMessage = new Message(XmppMailRuProtocol.parseRecipientList(Utils.defaultStr(AppState.getString(StateKeys.SLOT_MSG_EXTRA_2))), Utils.defaultStr(AppState.getString(StateKeys.SLOT_MSG_EXTRA_3)), Utils.defaultStr(AppState.getString(StateKeys.SLOT_TRAFFIC_STATUS_TEXT)));
                Vector params10 = NetworkUtils.newVector();
                params10.addElement(newMessage.toHashtable());
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(StateKeys.STR_RES_API_URL_3), IOUtils.appendAuthParams(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_2)), Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params10)))));
                return;
            case ScreenId.PRIVACY_MODE:
                ResourceManager.playNotificationSound(4);
                AppState.setInt(StateKeys.INT_NOTIFICATION_SCREEN_ID, ScreenId.PRIVACY_MODE);
                AppState.setFromPool(StateKeys.SLOT_NOTIFICATION_TITLE, StateKeys.STR_PRIVACY_MODE_BASE);
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.STATUS_PREVIEW:
                String inputText = XmppContactGroup.getTextInputValue();
                AppState.setObject(StateKeys.SLOT_STATUS_TEXT, (Object) inputText);
                AppState.setBool(StateKeys.FLAG_STATUS_TEXT_SET, !StringUtils.isEmpty(inputText));
                ScreenManager.showScreen(ScreenManager.createScreen(2299));
                return;
            case ScreenId.CONTACT_DELETE:
                AppState.clearIndex(StateKeys.SLOT_REG_PARAM_1);
                if (AppState.getCurrentContact() == null) {
                    NetworkUtils.showErrorOrConfirm(85, 727, 0);
                    return;
                } else {
                    Contact contact3 = AppState.getCurrentContact();
                    NetworkUtils.showErrorOrConfirm(85, 727, contact3.account.validateDelete(contact3));
                    return;
                }
            case ScreenId.GROUP_MOVE:
                Screen screen4 = ScreenManager.createScreen(4238);
                Contact contact4 = AppState.getCurrentContact();
                Vector vector = contact4.account.groups;
                int size8 = vector.size();
                for (int i9 = 0; i9 < size8; i9++) {
                    ContactGroup group = (ContactGroup) vector.elementAt(i9);
                    screen4.addItem(group.createMenuItem(-1));
                    if (group.containsContact(contact4)) {
                        screen4.selectedIndex = i9;
                    }
                }
                ScreenManager.showScreen(screen4);
                return;
            case ScreenId.CHAT_STATUS:
                AppState.setBool(StateKeys.FLAG_STATUS_TEXT_SET, Utils.nonEmpty(AppState.getString(StateKeys.SLOT_STATUS_TEXT)));
                ScreenManager.showScreen(ScreenManager.createScreen(3647));
                return;
            case ScreenId.THEME_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(4836));
                return;
            case ScreenId.TOS_SCREEN:
                ResourceManager.showTosScreen();
                return;
            case ScreenId.EVENT_QUEUE:
                AppController.processEventQueue();
                return;
            case ScreenId.VIEW_MODE:
                boolean isOnline4 = AppState.getBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE);
                boolean isCustom = AppState.getBool(StateKeys.SETTING_CUSTOM_VIEW_MODE);
                AppState.setBool(StateKeys.FLAG_ONLINE_CUSTOM_OFF, isOnline4 && !isCustom);
                AppState.setBool(StateKeys.FLAG_ONLINE_CUSTOM_ON, isOnline4 && isCustom);
                ScreenManager.showScreen(ScreenManager.createScreen(1600));
                return;
            case ScreenId.CONTACT_MENU:
                AppState.setInt(StateKeys.FLAG_CONTACT_MENU_MODE, 0);
                Contact contact5 = AppState.getCurrentContact();
                if (contact5.isSystem()) {
                    AppState.setInt(StateKeys.INT_CANCEL_MENU_ACTION, 92);
                    AppState.setInt(StateKeys.INT_CANCEL_MENU_TYPE, 3);
                    ScreenManager.showScreen(ScreenManager.createScreen(3783));
                    return;
                }
                IOUtils.updateContactFlags(contact5);
                boolean z7 = contact5 instanceof MrimContact;
                boolean z8 = z7;
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_MRIM, z7);
                boolean z9 = z8 && contact5.isOffline();
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_GROUP, z9);
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_USER, z8 && !z9);
                AppState.setBool(StateKeys.FLAG_CONTACT_IS_ONLINE, contact5.isOnline());
                AppState.setBool(StateKeys.FLAG_CONTACT_HAS_UNREAD, contact5.hasUnread() && !contact5.isOnline());
                AppState.setBool(StateKeys.FLAG_CONTACT_HAS_VCARD, z8 && !z9 && ((MrimContact) contact5).hasVCard());
                AppState.setInt(StateKeys.INT_OK_MENU_TYPE, 3);
                AppState.setInt(StateKeys.INT_OK_MENU_ACTION, 92);
                ScreenManager.showScreen(ScreenManager.createScreen(3704));
                return;
            case ScreenId.EMOTICON_PICKER:
                Screen screen5 = ScreenManager.createScreen(2621);
                if (AppState.getCurrentContact() instanceof MmpContact) {
                    for (int i10 = 0; i10 < 43; i10++) {
                        if (AppState.getString(i10 + 1141) != null) {
                            screen5.addIconTextItem(i10 + 110, StringUtils.intern(Integer.toString(i10)), i10);
                        }
                    }
                } else if (AppState.getCurrentContact() instanceof XmppContact) {
                    for (int i11 = 0; i11 < 37; i11++) {
                        if (AppState.getString(i11 + 1184) != null) {
                            screen5.addIconTextItem(i11 + 318, StringUtils.intern(Integer.toString(i11)), i11);
                        }
                    }
                } else {
                    for (int i12 = 10; i12 < 74; i12++) {
                        screen5.addIconTextItem(i12 + 36, StringUtils.intern(Integer.toString(i12)), i12);
                    }
                    for (int i13 = 0; i13 < 10; i13++) {
                        screen5.addIconTextItem(i13 + 36, StringUtils.intern(Integer.toString(i13)), i13);
                    }
                    screen5.addIconTextItem(142, StringUtils.intern(Integer.toString(74)), 74);
                    screen5.addIconTextItem(137, StringUtils.intern(Integer.toString(75)), 75);
                    screen5.addIconTextItem(210, StringUtils.intern(Integer.toString(76)), 76);
                    screen5.addIconTextItem(205, StringUtils.intern(Integer.toString(77)), 77);
                }
                ScreenManager.showScreen(screen5);
                return;
            case ScreenId.PHONE_INPUT:
                Screen screen6 = ScreenManager.createScreen(2611);
                for (int i14 = 0; i14 < 15; i14++) {
                    screen6.addTextItem(AppState.getString(i14 + 48));
                }
                ScreenManager.showScreen(screen6);
                return;
            case ScreenId.SERVER_ADDRESS:
                Screen screen7 = ScreenManager.createScreen(2601);
                for (int i15 = 0; i15 < 15; i15++) {
                    screen7.addTextItem(AppState.getString(i15 + 48));
                }
                ScreenManager.showScreen(screen7);
                return;
            case ScreenId.CONTACT_INFO_VIEW:
                ContactInfo contactInfo = (ContactInfo) AppState.pool[StateKeys.SLOT_REG_PARAM_1];
                String str2 = (String) contactInfo.get(ResourceManager.integerOf(-1));
                if (null != str2) {
                    NotificationHelper.showNotification(str2);
                } else {
                    AppState.setInt(StateKeys.INT_INFO_SCREEN_MODE, contactInfo.isXmppContact() ? 0 : 503);
                    ScreenManager.showScreen(contactInfo.buildContactScreen(3830));
                }
                AppState.setInt(StateKeys.INT_CURRENT_SCREEN_ID, ScreenId.USER_PROFILE);
                return;
            case ScreenId.REGION_SELECTOR:
                Vector regions = AppState.getVector(StateKeys.VEC_MAP_POINTS);
                int size9 = regions.size();
                if (size9 == 0) {
                    NotificationHelper.showMessageById(397);
                    return;
                }
                Screen screen8 = ScreenManager.createScreen(1691);
                for (int i16 = 0; i16 < size9; i16++) {
                    GeoRegion region = (GeoRegion) regions.elementAt(i16);
                    screen8.addIconItemWithData(-1, region.name, 6, region);
                }
                GeoRegion currentRegion = StringUtils.getGeoRegion();
                screen8.addIconItemWithData(-1, currentRegion.name, 6, currentRegion);
                ScreenManager.showScreen(screen8);
                return;
            case ScreenId.PHONE_INPUT_ALT:
                Screen screen9 = ScreenManager.createScreen(4080);
                for (int i17 = 0; i17 < 15; i17++) {
                    screen9.addTextItem(AppState.getString(i17 + 48));
                }
                ScreenManager.showScreen(screen9);
                return;
            case ScreenId.URL_OPEN:
                Screen screen10 = ScreenManager.createScreen(4090);
                for (int i18 = 0; i18 < 15; i18++) {
                    screen10.addTextItem(AppState.getString(i18 + 48));
                }
                ScreenManager.showScreen(screen10);
                return;
            case ScreenId.MAP_POINTS:
                Screen screen11 = ScreenManager.createScreen(1701);
                Vector mapPoints = AppState.getVector(StateKeys.VEC_CONTACT_GROUPS);
                for (int i19 = 0; i19 < mapPoints.size(); i19++) {
                    MapPoint mapPoint = (MapPoint) mapPoints.elementAt(i19);
                    screen11.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
                }
                ScreenManager.showScreen(screen11);
                return;
            case ScreenId.CONVERSATION:
                return;
            case ScreenId.USER_PROFILE:
                if (AppController.pendingAccount == null && AppController.pendingUrl == null) {
                    Contact contact6 = AppState.getCurrentContact();
                    String statusText = AppController.getStatusText();
                    NetworkUtils.showErrorOrConfirm(102, 728, statusText != null ? contact6.account.getResourceId((Object) statusText) : ResourceManager.loadUserProfile(contact6.getIdentifier(), contact6.account));
                    return;
                } else {
                    NetworkUtils.showErrorOrConfirm(102, 728, 0);
                    ResourceManager.loadUserProfile(AppController.pendingUrl, AppController.pendingAccount);
                    AppController.clearMapPoints();
                    return;
                }
            case ScreenId.CONTACT_INFO_DETAIL:
                ScreenManager.showScreen(((ContactInfo) AppState.pool[StateKeys.SLOT_CONTACT_INFO]).buildContactScreen(3878));
                AppState.clearIndex(StateKeys.SLOT_REG_PARAM_1);
                AppState.setInt(StateKeys.INT_CURRENT_SCREEN_ID, ScreenId.PROFILE_LOAD);
                return;
            case ScreenId.COLOR_PICKER:
                ScreenManager.showScreen(ScreenManager.createScreen(4204));
                return;
            case ScreenId.XMPP_LOGIN_ALT:
                XmppMailRuProtocol.showLoginScreen();
                ScreenManager.getCurrentScreen().screenId = ScreenId.XMPP_LOGIN_ALT;
                return;
            case ScreenId.CAPTCHA:
                Screen screen12 = ScreenManager.createScreen(3840);
                Object obj4 = ((Object[]) AppState.pool[StateKeys.OBJ_REGISTRATION_DATA])[2];
                if (obj4 instanceof Image) {
                    screen12.addItem(MenuItem.createGraphics(new GraphicsContext((Image) obj4)));
                } else {
                    screen12.addLabelById(((Integer) obj4).intValue());
                }
                ScreenManager.showScreen(screen12);
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                return;
            case ScreenId.PROFILE_LOAD:
                ContactInfo contactInfo2 = (ContactInfo) AppState.pool[StateKeys.SLOT_CONTACT_INFO];
                NetworkUtils.showErrorOrConfirm(107, 728, contactInfo2.getAccount().getResourceId((Object) contactInfo2.getEmailOrMmpId()));
                return;
            case ScreenId.CONTACT_LIST_KEY:
                return;
            case ScreenId.VERSION_SELECT:
                ScreenManager.showScreen(ScreenManager.createScreen(4862).selectByTitle(AppState.getString(StateKeys.STR_PRIVACY_MODE_BASE + ((MmpProtocol) AppState.getAccount()).getPendingVersion())));
                return;
            case ScreenId.MAP_TOOLTIP:
                AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_1, (Object) AppState.emptyStr);
                String tooltipText = MapRenderer.getTooltipText();
                if (tooltipText != null) {
                    AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_1, (Object) tooltipText);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(1727));
                return;
            case ScreenId.PEOPLE_NEARBY:
                Vector allContacts = AccountManager.getAllAccountsList();
                int size10 = allContacts.size();
                while (true) {
                    size10--;
                    if (size10 < 0) {
                        if (allContacts.size() == 0) {
                            NotificationHelper.showMessageById(762);
                        } else {
                            AppController.sortContacts(allContacts);
                            ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(1743), allContacts));
                        }
                        NetworkUtils.releaseVector(allContacts);
                        return;
                    }
                    if (((Contact) allContacts.elementAt(size10)).isOffline()) {
                        allContacts.removeElementAt(size10);
                    }
                }
            case ScreenId.CLEAR_NOTIFICATIONS:
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.MAP_CONTEXT_MENU:
                XmppMailRuProtocol.showMapContextMenu();
                return;
            case ScreenId.SAVE_LOCATION:
                AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_2, (Object) AppState.emptyStr);
                String tooltipText2 = MapRenderer.getTooltipText();
                if (tooltipText2 != null) {
                    AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_2, (Object) tooltipText2);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(1876));
                return;
            case ScreenId.MESSAGE_INPUT:
                ScreenManager.showScreen(ScreenManager.createScreen(2501));
                AppController.clearPreviewState();
                return;
            case ScreenId.MAP_ROUTE:
                Screen screen13 = ScreenManager.createScreen(1892);
                Enumeration routeEnum = ConnectionThread.getRouteElements();
                while (routeEnum.hasMoreElements()) {
                    MapPoint mapPoint2 = (MapPoint) routeEnum.nextElement();
                    screen13.addIconItemWithData(-1, mapPoint2.name, 118, mapPoint2);
                }
                ScreenManager.showScreen(screen13);
                return;
            case ScreenId.MAP_STATUS:
                Conversation.updateStatusText(375);
                ScreenManager.showScreen(ScreenManager.createScreen(1902));
                return;
            case ScreenId.SEND_TO_CONTACT:
                Vector allContacts2 = AccountManager.getAllAccountsList();
                int size11 = allContacts2.size();
                while (true) {
                    size11--;
                    if (size11 < 0) {
                        if (allContacts2.size() == 0) {
                            NotificationHelper.showMessageById(762);
                        } else {
                            AppController.sortContacts(allContacts2);
                            ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(1930), allContacts2));
                        }
                        NetworkUtils.releaseVector(allContacts2);
                        return;
                    }
                    if (((Contact) allContacts2.elementAt(size11)).isOffline()) {
                        allContacts2.removeElementAt(size11);
                    }
                }
            case ScreenId.CHAT_ROOM_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(1940));
                return;
            case ScreenId.MAP_ROUTE_SELECT:
                Screen screen14 = ScreenManager.createScreen(1958);
                Enumeration routeEnum2 = ConnectionThread.getRouteElements();
                while (routeEnum2.hasMoreElements()) {
                    MapPoint mapPoint3 = (MapPoint) routeEnum2.nextElement();
                    screen14.addIconItemWithData(-1, mapPoint3.name, 6, mapPoint3);
                }
                ScreenManager.showScreen(screen14);
                return;
            case ScreenId.CHAT_LIST_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(1968));
                return;
            case ScreenId.PRESENCE_ACTION:
                NetworkUtils.showAlertById(122, 535);
                return;
            case ScreenId.MESSAGE_SUMMARY:
                ScreenManager.showScreen(AppState.getCurrentContact().showMessageSummary());
                return;
            case ScreenId.EMPTY_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(3479));
                return;
            case ScreenId.BLOCK_CONTACT_LIST:
                Account account3 = AppState.getAccount();
                Vector contacts11 = NetworkUtils.newVector();
                Enumeration contactEnum2 = account3.contactMap.elements();
                while (contactEnum2.hasMoreElements()) {
                    Contact contactToDelete = (Contact) contactEnum2.nextElement();
                    if (contactToDelete.canDelete()) {
                        contacts11.addElement(contactToDelete);
                    }
                }
                if (contacts11.size() > 0) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(4070), contacts11));
                } else {
                    NotificationHelper.showMessageById(762);
                }
                NetworkUtils.releaseVector(contacts11);
                return;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                Account account4 = AppState.getAccount();
                Vector contacts12 = NetworkUtils.newVector();
                Enumeration contactEnum3 = account4.contactMap.elements();
                while (contactEnum3.hasMoreElements()) {
                    Contact contactToBlock = (Contact) contactEnum3.nextElement();
                    if (contactToBlock.canBlock()) {
                        contacts12.addElement(contactToBlock);
                    }
                }
                if (contacts12.size() > 0) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(4060), contacts12));
                } else {
                    NotificationHelper.showMessageById(762);
                }
                NetworkUtils.releaseVector(contacts12);
                return;
            case ScreenId.DELETE_CONTACT_LIST:
                Account account5 = AppState.getAccount();
                Vector contacts13 = NetworkUtils.newVector();
                Enumeration contactEnum4 = account5.contactMap.elements();
                while (contactEnum4.hasMoreElements()) {
                    Contact contactToUnblock = (Contact) contactEnum4.nextElement();
                    if (contactToUnblock.canUnblock()) {
                        contacts13.addElement(contactToUnblock);
                    }
                }
                if (contacts13.size() > 0) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(4050), contacts13));
                } else {
                    NotificationHelper.showMessageById(762);
                }
                NetworkUtils.releaseVector(contacts13);
                return;
            case ScreenId.DELETE_MESSAGES:
                NetworkUtils.showAlertBuffer(128, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_DELETE_CONFIRM)).append(AppState.getCurrentContact().displayName).append(NetworkUtils.longToHex(16167)));
                return;
            case ScreenId.MMP_ACCOUNT_SELECT:
                StringBuffer sbAccounts = NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_ACCOUNTS_HEADER));
                Vector mmpAccounts = AccountManager.getMmpAccountList();
                int i20 = 0;
                int size12 = mmpAccounts.size();
                int i21 = size12;
                while (true) {
                    i21--;
                    if (i21 < 0) {
                        AppState.setInt(StateKeys.INT_ACCOUNT_INDEX, i20);
                        AppState.setObject(StateKeys.SLOT_ACCOUNT_LIST_TEXT, (Object) NetworkUtils.bufToStringCached(sbAccounts));
                        ScreenManager.showScreen(ScreenManager.createScreen(1990));
                        return;
                    } else {
                        String str3 = ((MrimAccount) mmpAccounts.elementAt(i21)).login;
                        sbAccounts.append(str3);
                        if (i21 != 0) {
                            sbAccounts.append((char) 0);
                        }
                        if (str3.equals(AppState.getString(StateKeys.LAST_ACCOUNT_NAME))) {
                            i20 = size12 - i21;
                        }
                    }
                }
            case ScreenId.VCARD_ACTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(4892));
                return;
            case ScreenId.PEOPLE_SEARCH:
                ScreenManager.showScreen(ScreenManager.createScreen(2043));
                return;
            case ScreenId.KEY_MAPPING:
                ScreenManager.showScreen(ScreenManager.createScreen(2421));
                return;
            case ScreenId.UNUSED_133:
                return;
            case ScreenId.UNUSED_134:
                return;
            case ScreenId.UNUSED_135:
                return;
            case ScreenId.UNUSED_136:
                return;
            case ScreenId.MAIN_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(4369));
                if (AppState.getBool(StateKeys.FLAG_HAS_XMPP_ACCOUNT)) {
                    AppController.processBackgroundTasks();
                    return;
                }
                return;
            case ScreenId.UNUSED_138:
                return;
            case ScreenId.UNUSED_139:
                return;
            case ScreenId.FORM_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(5090));
                return;
            case ScreenId.UNUSED_141:
                return;
            case ScreenId.GROUP_MEMBERS:
                Screen screen15 = ScreenManager.createScreen(4159);
                MrimContact mrimContact = (MrimContact) AppState.getCurrentContact();
                MrimAccount mrimAccount4 = (MrimAccount) mrimContact.account;
                Vector groupMembers = AppState.getVector(StateKeys.SLOT_REG_PARAM_4);
                mrimContact.setGroupsList(groupMembers);
                for (int i22 = 0; i22 < groupMembers.size(); i22++) {
                    String memberLogin = Utils.getVectorString(groupMembers, i22);
                    if (!StringUtils.equals(memberLogin, mrimAccount4.login)) {
                        MrimContact mrimContact2 = (MrimContact) mrimAccount4.getContact((Object) memberLogin);
                        if (mrimContact2 != null) {
                            screen15.addItem(mrimContact2.createMenuItem());
                        } else {
                            screen15.addIconItemWithData(154, memberLogin, 0, memberLogin);
                        }
                    }
                }
                if (screen15.menuItems.size() == 0) {
                    screen15.selectable = false;
                    Screen labelScreen = screen15.addLabelById(772);
                    labelScreen.setSoftKeys(AppState.getString(StateKeys.STR_EMPTY), AppState.getString(StateKeys.STR_SOFTKEY_NO), labelScreen.softKeyLeft, labelScreen.softKeyCenter, labelScreen.softKeyRight);
                }
                ScreenManager.showScreen(screen15);
                AppState.clearIndex(StateKeys.SLOT_REG_PARAM_4);
                return;
            case ScreenId.CREATE_CHAT_ROOM:
                AppState.setInt(StateKeys.FLAG_CHAT_ROOM_CREATED, 0);
                AppState.setFromBuffer(StateKeys.SLOT_CHAT_NAME, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_CHAT_NAME_PREFIX)).append(1 + (AppState.getInt(StateKeys.UI_COUNTER) % 1000)));
                ScreenManager.showScreen(IOUtils.buildContactListScreen(ScreenManager.createScreen(4138), (MrimAccount) AppState.getAccount(), (Contact) null));
                return;
            case ScreenId.EDIT_MEMBERS:
                ScreenManager.showScreen(IOUtils.buildContactListScreen(ScreenManager.createScreen(4169), (Account) null, AppState.getCurrentContact()));
                return;
            case ScreenId.CONTACT_DELETE_MRIM:
                AppState.clearIndex(StateKeys.SLOT_REG_PARAM_1);
                NetworkUtils.showErrorOrConfirm(145, 727, ((MrimAccount) AppState.getCurrentContact().account).sendDeleteCommand(AppController.getStatusText()));
                return;
            case ScreenId.GROUP_MANAGEMENT:
                AppState.setBool(StateKeys.FLAG_HAS_MULTIPLE_MRIM, AccountManager.getMrimAccountList().size() > 1);
                ScreenManager.showScreen(ScreenManager.createScreen(2523));
                return;
            case ScreenId.BLOG_POST:
                String[] langOptions = AppController.getLanguageOptions();
                if (langOptions != null) {
                    AppState.setObject(StateKeys.SLOT_SCREEN_SUBTITLE, (Object) langOptions[0]);
                    AppState.setFromBuffer(StateKeys.SLOT_SCREEN_TITLE, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_LANGUAGE_PREFIX)).append(langOptions[1]));
                    if (AppState.getInt(StateKeys.COUNTER_SEARCH_RESULTS) != 0) {
                        AppState.clearIndex(StateKeys.SLOT_LANG_OPTION_1);
                        AppState.clearIndex(StateKeys.SLOT_LANG_OPTION_2);
                    }
                    AppState.setFromPool(StateKeys.SLOT_SCREEN_VALUE, StateKeys.SLOT_LANG_OPTION_1);
                    if (AppState.getString(StateKeys.SLOT_SCREEN_DESCRIPTION) != null) {
                        AppState.setFromPool(StateKeys.SLOT_SCREEN_VALUE, StateKeys.SLOT_SCREEN_DESCRIPTION);
                        AppState.clearIndex(StateKeys.SLOT_SCREEN_DESCRIPTION);
                    }
                    ScreenManager.showScreen(ScreenManager.createScreen(2482));
                    return;
                }
                break;
            case ScreenId.UNUSED_148:
                return;
            case ScreenId.UNUSED_149:
                return;
            case ScreenId.CONTACT_MODIFY:
                AppController.prepareFormData();
                MrimContact mrimContact3 = (MrimContact) AppState.getCurrentContact();
                MrimAccount mrimAccount5 = (MrimAccount) mrimContact3.account;
                NetworkUtils.showErrorOrConfirm(150, 504, mrimAccount5.trySendData(mrimAccount5.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount5, 4104, new ByteBuffer().writeIntLE(4194304).writeStringLatin1(mrimContact3.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeIntLE(4).writeIntLE(1)), ResourceManager.integerOf(10), mrimContact3, new Long(1L)})));
                return;
            case ScreenId.EXT_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(3272));
                return;
            case ScreenId.MAP_VIEW_SETTINGS:
                ConnectionThread.showMapView();
                ScreenManager.showScreen(ScreenManager.createScreen(3302));
                return;
            case ScreenId.WIFI_NETWORKS:
                ResourceManager.showWiFiNetworks();
                return;
            case ScreenId.SHARE_LOCATION:
                ScreenManager.showScreen(ScreenManager.createScreen(2085));
                return;
            case ScreenId.VISIBLE_CONTACTS:
                Vector contactIds = ConnectionThread.getAllContactIds();
                int count = Utils.vectorSize(contactIds);
                if (count == 0) {
                    NotificationHelper.showMessageById(404);
                    return;
                }
                Screen screen16 = ScreenManager.createScreen(2101);
                for (int i23 = 0; i23 < count; i23++) {
                    Object contactId = contactIds.elementAt(i23);
                    screen16.addItem(MenuItem.createCheckbox(ConnectionThread.getPhotoHost(contactId), !ConnectionThread.hiddenContacts.contains(contactId)));
                }
                ScreenManager.showScreen(screen16);
                return;
            case ScreenId.PHOTO_SELECTOR:
                IOUtils.showPhotoSelector();
                return;
            case ScreenId.PHOTO_VIEW:
                ScreenManager.pushScreen(ScreenManager.createScreen(4381));
                return;
            case ScreenId.MAP_SEARCH:
                AppState.setInt(StateKeys.INT_SCREEN_BUILDER_ACTION, AppState.getBool(StateKeys.FLAG_MAP_MODE_ACTIVE) ? 407 : 408);
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(2111));
                return;
            case ScreenId.WIFI_ACCOUNT_LIST:
                if (!AppState.getBool(StateKeys.FLAG_REGISTRATION_DONE)) {
                    Vector mmpAccounts2 = AccountManager.getMmpAccountList();
                    int size13 = mmpAccounts2.size();
                    int i24 = size13;
                    if (size13 > 0) {
                        Screen screen17 = ScreenManager.createScreen(2140);
                        while (true) {
                            i24--;
                            if (i24 < 0) {
                                ScreenManager.showScreen(screen17);
                                return;
                            }
                            MrimAccount mrimAccount6 = (MrimAccount) mmpAccounts2.elementAt(i24);
                            int iconId = mrimAccount6.getIconId();
                            String str4 = mrimAccount6.login;
                            screen17.addIconItemWithData(iconId, str4, 153, str4);
                        }
                    }
                }
                ResourceManager.showWiFiNetworks();
                return;
            case ScreenId.PROFILE_EDIT:
                StringBuffer stringBuffer = new StringBuffer(AppState.getString(StateKeys.STR_REGISTRATION_TEXT));
                stringBuffer.append(AppState.getString(((MrimAccount) AppState.getAccount()).accountProfile.gender + 780));
                AppState.setFromBuffer(StateKeys.OBJ_PHOTO_CACHE_2, stringBuffer);
                ResourceManager.lastTileLoadTime = System.currentTimeMillis();
                ScreenManager.showScreen(ScreenManager.createScreen(4258));
                return;
            case ScreenId.SEND_CONFIRM:
                NetworkUtils.showConfirmDialog(161, 872);
                return;
            case ScreenId.CHAT_DETAIL:
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(4270));
                return;
            case ScreenId.NOTIFY_MESSAGE:
                AppState.setInt(StateKeys.FLAG_CONVERSATION_ACTIVE, 0);
                NetworkUtils.showAlertBuffer(163, NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_NOTIFY_MESSAGE)));
                return;
            case ScreenId.REG_FORM:
                NetworkUtils.processRegForm();
                return;
            case ScreenId.ASYNC_CONFIRM:
                NetworkUtils.showConfirmDialog(165, 505);
                AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = NetworkUtils.newRequest();
                return;
            case ScreenId.CHAT_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(4179));
                return;
            case ScreenId.MAILBOX_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(2158));
                return;
            case ScreenId.INVITE_TOS:
                ResourceManager.showTosScreen();
                return;
            case ScreenId.SAVED_LOCATIONS:
                ResourceManager.showSavedLocations();
                return;
            case ScreenId.FORM_LIST:
                Screen screen18 = ScreenManager.createScreen(2176);
                Vector vector2 = ((Conversation) AppState.pool[StateKeys.SLOT_TEMP_OBJECT_1]).items;
                int size14 = vector2.size();
                while (true) {
                    size14--;
                    if (size14 < 0) {
                        ScreenManager.showScreen(screen18);
                        AppState.clearIndex(StateKeys.SLOT_TEMP_OBJECT_1);
                        return;
                    } else {
                        ListItem listItem = (ListItem) vector2.elementAt(size14);
                        screen18.addIconItemWithData(-1, listItem.getText(), 0, listItem);
                    }
                }
            case ScreenId.UPDATE_ALERT:
                NetworkUtils.showAlertById(171, 787);
                AppState.setInt(StateKeys.FLAG_UPDATE_AVAILABLE, 1);
                return;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                Screen screen19 = ScreenManager.createScreen(2186);
                Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
                int size15 = onlineAccounts.size();
                while (true) {
                    size15--;
                    if (size15 < 0) {
                        ScreenManager.showScreen(screen19);
                        return;
                    } else {
                        MrimAccount mrimAccount7 = (MrimAccount) onlineAccounts.elementAt(size15);
                        screen19.addIconItemWithData(156, mrimAccount7.login, 0, mrimAccount7);
                    }
                }
            case ScreenId.INVITE_ALERT:
                NetworkUtils.showAlertById(173, 416);
                return;
            case ScreenId.MAP_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(2198));
                return;
            case ScreenId.NEARBY_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(2218));
                return;
            case ScreenId.PHONE_CONTACTS:
                return;
            case ScreenId.SEARCH_ENTRY:
                AppState.setCurrentEntity((Object) null);
                ScreenManager.showScreen(ScreenManager.createScreen(2247));
                return;
            case ScreenId.EDIT_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(2279));
                return;
            case ScreenId.SEND_DATA:
                NetworkUtils.showConfirmDialog(179, 504);
                return;
            case ScreenId.ASYNC_TASK:
                break;
        }
        AppController.finishScreenBuild();
    }

    /* renamed from: a */
    public static final void onMenuItemSelected() {
        int errorCode;
        int errorCode2;
        int errorCode3;
        int errorCode4;
        int errorCode5;
        int deleteResult;
        int unblockResult;
        int blockResult;
        int sendResult;
        int errorCode6;
        int errorCode7;
        int errorCode8;
        int configResult;
        int sendResult2;
        int modifyResult;
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        Screen currentScreen = ScreenManager.getCurrentScreen();
        String title = ScreenManager.getCurrentTitle();
        int action = ScreenManager.getCurrentWidth();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object obj = menuItem == null ? null : menuItem.data;
        int nextScreen = 0;
        switch (ScreenManager.getCurrentScreen().screenId) {
            case ScreenId.ACCOUNT_LIST:
                nextScreen = AppController.handleMapMenuOption(action);
                break;
            case ScreenId.SETTINGS:
                nextScreen = 0;
                break;
            case ScreenId.STATUS_DIALOG:
                nextScreen = IOUtils.handleStatusChange(action);
                break;
            case ScreenId.CONTACT_LIST:
                nextScreen = ContactListManager.getSelectedContact();
                break;
            case ScreenId.ACCOUNTS_MENU:
                nextScreen = AppController.handleChatSettingsOption(action);
                break;
            case ScreenId.MAP:
                if (!AppState.getBool(StateKeys.FLAG_MAP_TILES_PENDING)) {
                    ConnectionThread.toggleMapControls(currentScreen);
                }
                nextScreen = 0;
                break;
            case ScreenId.MAP_MENU:
                nextScreen = 0;
                break;
            case ScreenId.SETTINGS_MENU:
                nextScreen = AppController.handleSettingsOption(action);
                break;
            case ScreenId.ABOUT:
                nextScreen = 0;
                break;
            case ScreenId.BLOCK_CONFIRM:
                nextScreen = ScreenId.DELETE_CONFIRM;
                break;
            case ScreenId.UNBLOCK_CONFIRM:
                nextScreen = AppController.handleLeftKey();
                break;
            case ScreenId.CONFIRM_EXIT:
                nextScreen = -1;
                break;
            case ScreenId.TRAFFIC_COST:
                nextScreen = ResourceManager.parseBalance();
                break;
            case ScreenId.ACCOUNT_SWITCHER:
                nextScreen = AppController.handleMenuAction(title, obj);
                break;
            case ScreenId.REGISTRATION:
                nextScreen = 0;
                break;
            case ScreenId.EMOTICON_DIALOG:
                nextScreen = AppController.handleAccountOption(action);
                break;
            case ScreenId.UNUSED_18:
                nextScreen = 0;
                break;
            case ScreenId.CONTACT_EDITOR:
                NetworkUtils.processScreenForm();
                String[] phoneNumbers = Utils.getPhoneNumbers(false);
                Object[] objArr = new Object[phoneNumbers.length + 1];
                objArr[0] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_INPUT_TEXT));
                for (int i = 0; i < phoneNumbers.length; i++) {
                    objArr[i + 1] = phoneNumbers[i];
                }
                Contact contact = AppState.getCurrentContact();
                if (contact.isOnline()) {
                    contact.setDisplayName((String) objArr[0]);
                    AppController.needsLayoutUpdate = true;
                    modifyResult = 0;
                } else {
                    modifyResult = contact.account.validateModify(contact, objArr);
                }
                nextScreen = 0 != modifyResult ? NotificationHelper.showError(modifyResult) : 0;
                break;
            case ScreenId.GPS_SETTINGS:
                nextScreen = AppController.handleProfileAction(action);
                break;
            case ScreenId.ADD_CONTACT:
                NetworkUtils.processScreenForm();
                nextScreen = AppState.getAccount() instanceof XmppProtocol ? ((XmppProtocol) AppState.getAccount()).addNewContact() : 0;
                break;
            case ScreenId.ADD_MRIM_CONTACT:
                NetworkUtils.processScreenForm();
                MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
                String displayName = Utils.defaultStr(AppState.getString(StateKeys.SLOT_INPUT_TEXT));
                String[] phoneNumbers2 = Utils.getPhoneNumbers(false);
                if (!mrimAccount.isConnected()) {
                    sendResult2 = 299;
                } else if (Utils.nonEmpty(displayName)) {
                    int length = phoneNumbers2.length;
                    if (length == 0) {
                        sendResult2 = 709;
                    } else {
                        Enumeration contactEnum = mrimAccount.contactMap.elements();
                        while (true) {
                            if (contactEnum.hasMoreElements()) {
                                MrimContact mrimContact = (MrimContact) contactEnum.nextElement();
                                int i2 = length;
                                do {
                                    i2--;
                                    if (i2 < 0) {
                                        break;
                                    }
                                } while (!mrimContact.isInGroup(phoneNumbers2[i2]));
                                sendResult2 = 486;
                            } else {
                                MrimContactGroup contactGroup = mrimAccount.getFirstContactGroup();
                                ByteBuffer packetBuf = new ByteBuffer().writeIntLE(1048576).writeIntLE(103).writeStringLatin1(AppState.getString(StateKeys.STR_PHONE_SUFFIX)).writeStringUTF16(displayName);
                                String emailsJoined = Utils.joinComma(phoneNumbers2);
                                sendResult2 = mrimAccount.trySendData(mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, 4121, packetBuf.writeStringLatin1(emailsJoined).writeZeros(8)), ResourceManager.integerOf(5), displayName, emailsJoined, contactGroup}));
                            }
                        }
                    }
                } else {
                    sendResult2 = 708;
                }
                nextScreen = 0 != sendResult2 ? NotificationHelper.showError(sendResult2) : 0;
                break;
            case ScreenId.UNUSED_23:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_24:
                nextScreen = 0;
                break;
            case ScreenId.MULTI_ACCOUNT_LIST:
                nextScreen = AppController.handleInputAction(action, obj);
                break;
            case ScreenId.THEME_SETTINGS:
                NetworkUtils.processScreenForm();
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, AppState.getInt(StateKeys.SETTING_COLOR_THEME));
                ScreenManager.initializeFonts();
                AppState.getCanvas().updateFullScreenMode();
                TabBar.initialize();
                ResourceManager.resetClock();
                nextScreen = 0;
                break;
            case ScreenId.NOTIFICATION_SETTINGS:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case ScreenId.SOUND_SETTINGS:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                NetworkUtils.processScreenForm();
                if (AppState.getInt(StateKeys.INT_SETTINGS_THEME) != AppState.getInt(StateKeys.SETTING_MULTI_ACCOUNT)) {
                    TabBar.initialize();
                }
                nextScreen = 0;
                break;
            case ScreenId.CONTACT_GROUP_MENU:
                nextScreen = IOUtils.handleContactGroupAction(title, action);
                break;
            case ScreenId.UNUSED_32:
                nextScreen = ResourceManager.handleDropdownSelect(title, menuItem);
                break;
            case ScreenId.PRIVACY_SETTINGS:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case ScreenId.TRAFFIC_STATS:
                int intVal = AppState.getInt(StateKeys.INT_PERIOD_INDEX);
                Account account = AppState.getAccount();
                if (account != null) {
                    account.syncArray[intVal + intVal + 1] = 0;
                    account.syncArray[intVal + intVal] = 0;
                } else {
                    for (int i3 = 0; i3 < 4; i3++) {
                        TrafficAccounting.addTrafficCount(i3, intVal, 0);
                        TrafficAccounting.addTrafficCount(i3, intVal, 1);
                    }
                }
                nextScreen = 0;
                break;
            case ScreenId.CONNECTION_SETTINGS:
                nextScreen = AppController.handleConnectionOption(action);
                break;
            case ScreenId.MAIL_ACCOUNT_LIST:
                nextScreen = 0;
                break;
            case ScreenId.CHAT_ROOMS:
                nextScreen = -1;
                break;
            case ScreenId.CHAT_ROOM_INIT:
                nextScreen = 0;
                break;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                nextScreen = AppController.handleAction(obj);
                break;
            case ScreenId.CLEAR_SEARCH:
                nextScreen = 0;
                break;
            case ScreenId.CHAT_ROOM_MESSAGES:
                nextScreen = -1;
                break;
            case ScreenId.CHAT_ROOM_INVITE:
                nextScreen = -1;
                break;
            case ScreenId.CHAT_ROOM_VIEW:
                AppState.setInt(StateKeys.INT_SCROLL_OFFSET, currentScreen.scrollOffset);
                AppState.setObject(StateKeys.SLOT_MAP_POINT_2, (Object) title);
                Message message = (Message) obj;
                AppState.setObject(StateKeys.SLOT_MESSAGE_ID, (Object) (message != null ? message.from : null));
                nextScreen = 0;
                break;
            case ScreenId.SUBMIT_REGISTRATION:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_45:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_46:
                nextScreen = 0;
                break;
            case ScreenId.CONTACT_GROUPS:
                nextScreen = 0;
                break;
            case ScreenId.MESSAGE_DETAIL:
                nextScreen = -1;
                break;
            case ScreenId.CHAT_ROOM_CONFIG:
                NetworkUtils.processScreenForm();
                nextScreen = (AppState.getInt(StateKeys.INT_SETTINGS_ACTION) != 4 || 0 == (configResult = ((MrimAccount) AppState.getAccount()).setConfiguration(((AppState.getInt(StateKeys.INT_SETTINGS_THEME) - 157) << 8) + 4))) ? 0 : NotificationHelper.showError(configResult);
                break;
            case ScreenId.CHAT_VIEW_MODE:
                NetworkUtils.processScreenForm();
                nextScreen = 0;
                break;
            case ScreenId.CHAT_ROOM_CONTEXT:
                nextScreen = ResourceManager.handleChatRoomAction(title);
                break;
            case ScreenId.MESSAGE_PREVIEW:
                nextScreen = 0;
                break;
            case ScreenId.COMPOSE_RECIPIENTS:
                nextScreen = IOUtils.handleMailForwardAction(title);
                break;
            case ScreenId.COMPOSE_MESSAGE:
                NetworkUtils.processScreenForm();
                Vector params = NetworkUtils.newVector();
                StringBuffer sb = NetworkUtils.newStringBuffer();
                String recipientStr = Utils.defaultStr(AppState.getString(StateKeys.SLOT_MSG_EXTRA_2));
                int length2 = recipientStr.length();
                int i4 = 0;
                while (i4 <= length2) {
                    char ch = i4 == length2 ? ';' : recipientStr.charAt(i4);
                    char c = ch;
                    if (ch == ';' || c == ',' || c == ' ') {
                        String token = StringUtils.extractBuffer(sb);
                        if (!StringUtils.isEmpty(token)) {
                            params.addElement(token);
                        }
                    } else {
                        sb.append(c);
                    }
                    i4++;
                }
                if (Utils.vectorSize(params) == 0) {
                    errorCode8 = NotificationHelper.showError(873);
                } else {
                    boolean z = false;
                    int count = Utils.vectorSize(params);
                    while (true) {
                        count--;
                        if (count < 0) {
                            errorCode8 = z ? NotificationHelper.showError(876) : 0;
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
                nextScreen = errorCode8;
                break;
            case ScreenId.DELETE_CONFIRM:
                nextScreen = -1;
                break;
            case ScreenId.CONTACT_SETTINGS:
                NetworkUtils.processScreenForm();
                if (AppState.getBool(StateKeys.SETTING_SHOW_IN_LIST)) {
                    AccountManager.updateTabBar();
                }
                nextScreen = 0;
                break;
            case ScreenId.FIRST_RUN:
                nextScreen = -1;
                break;
            case ScreenId.GROUP_SELECTOR:
                nextScreen = AppController.handleGroupSelection(action);
                break;
            case ScreenId.VERSION_CHECK:
                nextScreen = ResourceManager.applyVersionLabel();
                break;
            case ScreenId.INPUT_DIALOG:
                nextScreen = AppController.processInputText(title);
                break;
            case ScreenId.CHAT_ROOM_ALERT:
                nextScreen = ScreenId.CHAT_ROOM_INVITE;
                break;
            case ScreenId.MAIL_MENU:
                nextScreen = IOUtils.handleMailMenuAction(title, action);
                break;
            case ScreenId.STATUS_INPUT:
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas().updateCommands());
                onScreenClosed();
                nextScreen = ScreenId.STATUS_PREVIEW;
                break;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                nextScreen = AppController.handleAccountSwitchOption(action);
                break;
            case ScreenId.PHONE_GROUPS:
                NetworkUtils.processScreenForm();
                AppState.pool[StateKeys.SLOT_SELECTED_GROUP] = Utils.splitNonEmpty(AppState.getCurrentMrimContact().contactGroupsStr, ',').elementAt(AppState.getInt(StateKeys.INT_SELECTED_GROUP_INDEX));
                nextScreen = 0;
                break;
            case ScreenId.ADD_CONTACT_INFO:
                NetworkUtils.processScreenForm();
                int addResult = ((ContactInfo) AppState.pool[StateKeys.SLOT_CONTACT_INFO]).getAccount().validateGroupAdd(Utils.defaultStr(AppState.getString(StateKeys.SLOT_GROUP_ADD_NAME)), Utils.defaultStr(AppState.getString(StateKeys.SLOT_GROUP_ADD_DISPLAY)), Utils.defaultStr(AppState.getString(StateKeys.SLOT_GROUP_ADD_GROUP)), (ContactGroup) AppState.getVector(StateKeys.VEC_GROUP_LIST).elementAt(AppState.getInt(StateKeys.INT_GROUP_OPERATION_RESULT)), AppState.getBool(StateKeys.FLAG_GROUP_ADD_RESULT));
                nextScreen = 0 != addResult ? NotificationHelper.showError(addResult) : 0;
                break;
            case ScreenId.SOFTKEY_MENU:
                nextScreen = AppController.handleSoftKeyAction(title);
                break;
            case ScreenId.SEARCH_RESULTS:
                nextScreen = AppController.handleEnterKey();
                break;
            case ScreenId.CREATE_GROUP:
                NetworkUtils.processScreenForm();
                int createResult = AppState.getAccount().validateGroupCreate(Utils.defaultStr(AppState.getString(StateKeys.SLOT_NEW_GROUP_NAME)));
                nextScreen = 0 != createResult ? NotificationHelper.showError(createResult) : 0;
                break;
            case ScreenId.RENAME_GROUP:
                NetworkUtils.processScreenForm();
                int renameResult = AppState.getCurrentGroup().rename(Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_RESULT)));
                nextScreen = 0 != renameResult ? NotificationHelper.showError(renameResult) : 0;
                break;
            case ScreenId.DELETE_ENTITY:
                nextScreen = ResourceManager.deleteSelectedEntity();
                break;
            case ScreenId.BATCH_DELETE:
                nextScreen = -1;
                break;
            case ScreenId.SEARCH_RESULT_LIST:
                AppState.pool[StateKeys.SLOT_CONTACT_INFO] = obj;
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_74:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_75:
                nextScreen = -1;
                break;
            case ScreenId.XMPP_LOGIN:
                nextScreen = XmppMailRuProtocol.performLogin();
                break;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                nextScreen = AppController.handleInviteResult();
                break;
            case ScreenId.SHARE_MEDIA:
                nextScreen = -1;
                break;
            case ScreenId.SHARE_ALERT:
                onScreenClosed();
                onScreenClosed();
                nextScreen = 0;
                break;
            case ScreenId.NOTIFICATION_OPTIONS:
                nextScreen = AppController.handleNotificationOption(action);
                break;
            case ScreenId.SEND_MAIL:
                nextScreen = -1;
                break;
            case ScreenId.REPLY_MAIL:
                nextScreen = -1;
                break;
            case ScreenId.PRIVACY_MODE:
                nextScreen = AppController.handleHashKey();
                break;
            case ScreenId.STATUS_PREVIEW:
                nextScreen = ResourceManager.handleMessageInputAction(title, action);
                break;
            case ScreenId.CONTACT_DELETE:
                nextScreen = -1;
                break;
            case ScreenId.GROUP_MOVE:
                nextScreen = AppController.handleSearchAction(obj);
                break;
            case ScreenId.CHAT_STATUS:
                nextScreen = ResourceManager.handleChatInputAction(title);
                break;
            case ScreenId.THEME_OPTIONS:
                nextScreen = AppController.handleThemeOption(action);
                break;
            case ScreenId.TOS_SCREEN:
                nextScreen = -1;
                break;
            case ScreenId.EVENT_QUEUE:
                nextScreen = AppController.handleEventObject(obj);
                break;
            case ScreenId.VIEW_MODE:
                nextScreen = AppController.getThemeBackground(action);
                break;
            case ScreenId.CONTACT_MENU:
                nextScreen = IOUtils.handleContactMenuAction(title, action);
                break;
            case ScreenId.EMOTICON_PICKER:
                nextScreen = AppController.handleSoundOption(action);
                break;
            case ScreenId.PHONE_INPUT:
                nextScreen = AppController.processPhoneInput(title);
                break;
            case ScreenId.SERVER_ADDRESS:
                nextScreen = AppController.validateServerAddress(title);
                break;
            case ScreenId.CONTACT_INFO_VIEW:
                nextScreen = ((ContactInfo) AppState.pool[StateKeys.SLOT_REG_PARAM_1]).isMrimContact() ? ScreenId.VCARD_ACTIONS : ((ContactInfo) AppState.pool[StateKeys.SLOT_REG_PARAM_1]).isXmppContact() ? -1 : AppState.getInt(StateKeys.INT_CURRENT_SCREEN_ID);
                break;
            case ScreenId.REGION_SELECTOR:
                nextScreen = AppController.handleSearchResultAction(obj);
                break;
            case ScreenId.PHONE_INPUT_ALT:
                nextScreen = AppController.processPhoneInput(title);
                break;
            case ScreenId.URL_OPEN:
                nextScreen = AppController.openUrl(title);
                break;
            case ScreenId.MAP_POINTS:
                nextScreen = IOUtils.handleMapSearch(action, obj);
                break;
            case ScreenId.CONVERSATION:
                nextScreen = AppController.handleConversationAction(obj);
                break;
            case ScreenId.USER_PROFILE:
                nextScreen = -1;
                break;
            case ScreenId.CONTACT_INFO_DETAIL:
                nextScreen = ((ContactInfo) AppState.pool[StateKeys.SLOT_CONTACT_INFO]).isMrimContact() ? ScreenId.VCARD_ACTIONS : AppState.getInt(StateKeys.INT_CURRENT_SCREEN_ID);
                break;
            case ScreenId.COLOR_PICKER:
                nextScreen = AppController.getThemeColor(action);
                break;
            case ScreenId.XMPP_LOGIN_ALT:
                int loginResult = XmppMailRuProtocol.performLogin();
                nextScreen = 0 == loginResult ? ScreenId.CONTACT_LIST : loginResult;
                break;
            case ScreenId.CAPTCHA:
                nextScreen = 0;
                break;
            case ScreenId.PROFILE_LOAD:
                nextScreen = -1;
                break;
            case ScreenId.CONTACT_LIST_KEY:
                nextScreen = AppController.handleContactListKey();
                break;
            case ScreenId.VERSION_SELECT:
                nextScreen = ((MmpProtocol) AppState.getAccount()).scheduleVersionUpdate(action);
                break;
            case ScreenId.MAP_TOOLTIP:
                NetworkUtils.processScreenForm();
                nextScreen = StringUtils.isEmpty(Utils.defaultStr(AppState.getString(StateKeys.SLOT_TOOLTIP_TEXT_1))) ? NotificationHelper.showError(352) : 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                nextScreen = AppController.handleMapSearchAction(obj);
                break;
            case ScreenId.CLEAR_NOTIFICATIONS:
                nextScreen = -1;
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                nextScreen = XmppMailRuProtocol.handleMapAction(action);
                break;
            case ScreenId.SAVE_LOCATION:
                NetworkUtils.processScreenForm();
                String locationName = Utils.defaultStr(AppState.getString(StateKeys.SLOT_TOOLTIP_TEXT_2));
                if (StringUtils.isEmpty(locationName)) {
                    errorCode7 = NotificationHelper.showError(372);
                } else {
                    long lon = MapRenderer.currentLon;
                    long lat = MapRenderer.currentLat;
                    ListItem listItem = MapRenderer.tooltipItem;
                    if (listItem != null && listItem.isSelected()) {
                        lon = listItem.getWidth();
                        lat = listItem.getBaseHeight();
                        listItem.select();
                    }
                    MapPoint mapPoint = new MapPoint(locationName, 0L, 0L, 0L, 0L, lon, lat, AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
                    mapPoint.height = 4;
                    Vector screenStack = AppState.getVector(StateKeys.VEC_PHOTO_QUEUE);
                    XmppContactGroup.addMapPointIfNew(screenStack, mapPoint, 0, 50);
                    XmppContactGroup.saveMapPoints(screenStack, 226);
                    MapRenderer.navigateToMapPoint(mapPoint);
                    errorCode7 = 0;
                }
                nextScreen = errorCode7;
                break;
            case ScreenId.MESSAGE_INPUT:
                NetworkUtils.processScreenForm();
                String messageText4 = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SCREEN_VALUE));
                if (StringUtils.isEmpty(messageText4)) {
                    errorCode6 = NotificationHelper.showError(523);
                } else {
                    MrimAccount mrimAccount2 = (MrimAccount) AppState.getCurrentContact().account;
                    boolean flag = AppState.getBool(StateKeys.INT_GROUP_OPERATION_RESULT);
                    long timestamp = AppState.getLong(StateKeys.TIMESTAMP_SELECTED_MSG);
                    if (mrimAccount2.isConnected()) {
                        IOUtils.postNotification(AppState.getString(StateKeys.STR_OPERATION_COMPLETE));
                        sendResult = mrimAccount2.trySendData(ProtocolFactory.createMrimPacket(mrimAccount2, 4196, new ByteBuffer().writeIntLE(flag ? 5 : 20).writeStringUTF16(messageText4).writeLong(timestamp)));
                    } else {
                        sendResult = 299;
                    }
                    errorCode6 = 0 != sendResult ? NotificationHelper.showError(sendResult) : 0;
                }
                nextScreen = errorCode6;
                break;
            case ScreenId.MAP_ROUTE:
                nextScreen = AppController.handleMapResultAction(obj);
                break;
            case ScreenId.MAP_STATUS:
                nextScreen = AppController.processLoginField(title);
                break;
            case ScreenId.SEND_TO_CONTACT:
                nextScreen = AppController.handleFileAction(obj);
                break;
            case ScreenId.CHAT_ROOM_OPTIONS:
                nextScreen = AppController.handleChatRoomOption(action);
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                nextScreen = AppController.handleIncomingCall(obj);
                break;
            case ScreenId.CHAT_LIST_OPTIONS:
                nextScreen = AppController.handleChatListOption(action);
                break;
            case ScreenId.PRESENCE_ACTION:
                nextScreen = AppController.handlePresenceAction();
                break;
            case ScreenId.MESSAGE_SUMMARY:
                nextScreen = AppController.handleLocationAction(obj);
                break;
            case ScreenId.EMPTY_SCREEN:
                nextScreen = 0;
                break;
            case ScreenId.BLOCK_CONTACT_LIST:
                Contact selectedContact = (Contact) obj;
                nextScreen = (null == selectedContact || 0 == (blockResult = selectedContact.validateBlock())) ? 0 : NotificationHelper.showError(blockResult);
                break;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                Contact contactToDelete = (Contact) obj;
                nextScreen = (null == contactToDelete || 0 == (unblockResult = contactToDelete.validateUnblock())) ? 0 : NotificationHelper.showError(unblockResult);
                break;
            case ScreenId.DELETE_CONTACT_LIST:
                Contact contactToBlock = (Contact) obj;
                nextScreen = (null == contactToBlock || 0 == (deleteResult = contactToBlock.validateDelete())) ? 0 : NotificationHelper.showError(deleteResult);
                break;
            case ScreenId.DELETE_MESSAGES:
                AppState.getCurrentContact().initMessageBuffer();
                nextScreen = ScreenId.CONTACT_LIST;
                break;
            case ScreenId.MMP_ACCOUNT_SELECT:
                NetworkUtils.processScreenForm();
                AppState.setInt(StateKeys.MAP_INITIALIZED, 1);
                int intVal2 = AppState.getInt(StateKeys.INT_ACCOUNT_INDEX);
                if (intVal2 > 0) {
                    AppState.setInt(StateKeys.FLAG_REGISTRATION_DONE, 1);
                    AppState.setObject(StateKeys.LAST_ACCOUNT_NAME, (Object) Utils.splitAndGet(1252, intVal2));
                } else {
                    AppState.setObject(StateKeys.LAST_ACCOUNT_NAME, (Object) AppState.emptyStr);
                }
                nextScreen = 0;
                break;
            case ScreenId.VCARD_ACTIONS:
                nextScreen = AppController.handleScreenAction(action);
                break;
            case ScreenId.PEOPLE_SEARCH:
                nextScreen = AppController.processSearchQuery(title);
                break;
            case ScreenId.KEY_MAPPING:
                nextScreen = AppController.mapKeyToAction(action);
                break;
            case ScreenId.UNUSED_133:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_134:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_135:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_136:
                nextScreen = 0;
                break;
            case ScreenId.MAIN_SCREEN:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_138:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_139:
                nextScreen = ScreenId.MMP_ACCOUNT_SELECT;
                break;
            case ScreenId.FORM_SETTINGS:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case ScreenId.UNUSED_141:
                nextScreen = -1;
                break;
            case ScreenId.GROUP_MEMBERS:
                if (obj == null) {
                    errorCode5 = -1;
                } else if (obj instanceof String) {
                    AppState.pool[StateKeys.SLOT_CONTACT_INFO] = ContactInfo.createForAccount(AppState.getCurrentContact().account).setEmailAddress((String) obj).setDisplayName((String) obj);
                    errorCode5 = 66;
                } else {
                    errorCode5 = NotificationHelper.showError(773);
                }
                nextScreen = errorCode5;
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                NetworkUtils.processScreenForm();
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
                nextScreen = errorCode4;
                break;
            case ScreenId.EDIT_MEMBERS:
                Vector checkedItems2 = IOUtils.getCheckedItems(currentScreen, 0);
                if (checkedItems2.size() == 0) {
                    errorCode3 = NotificationHelper.showError(775);
                } else {
                    MrimContact mrimContact2 = (MrimContact) AppState.getCurrentContact();
                    MrimAccount mrimAccount4 = (MrimAccount) mrimContact2.account;
                    ByteBuffer buffer2 = new ByteBuffer();
                    int size2 = checkedItems2.size();
                    int i6 = size2;
                    ByteBuffer membersBuf2 = buffer2.writeIntLE(size2);
                    while (true) {
                        i6--;
                        if (i6 < 0) {
                            int sendResult4 = mrimAccount4.trySendData(mrimAccount4.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount4, 4104, new ByteBuffer().writeIntLE(4194304).writeStringLatin1(mrimContact2.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeBufferIntLen(new ByteBuffer().writeIntLE(3).writeBufferIntLen(membersBuf2))), ResourceManager.integerOf(10), mrimContact2, new Long(2L)}));
                            errorCode3 = 0 != sendResult4 ? NotificationHelper.showError(sendResult4) : 0;
                        } else {
                            membersBuf2.writeStringLatin1((String) checkedItems2.elementAt(i6));
                        }
                    }
                }
                nextScreen = errorCode3;
                break;
            case ScreenId.CONTACT_DELETE_MRIM:
                nextScreen = -1;
                break;
            case ScreenId.GROUP_MANAGEMENT:
                nextScreen = AppController.handleGroupRename(action);
                break;
            case ScreenId.BLOG_POST:
                NetworkUtils.processScreenForm();
                String messageText6 = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SCREEN_VALUE));
                if (StringUtils.isEmpty(messageText6)) {
                    errorCode2 = NotificationHelper.showError(523);
                } else {
                    MrimAccount mrimAccount5 = (MrimAccount) AppState.getAccount();
                    new AsyncTask(17, new ByteBuffer().writeCompressed(1442705).writeCompressed(1049531).writeUInt(4022591).writeRawString(mrimAccount5.login).writeUInt(4022822).writeRawString(mrimAccount5.password).writeCompressed(459757).writeCompressed(459750).writeRawString(Conversation.urlEncodeCyrillic((Object) messageText6)).writeRawString(Utils.defaultStr(AppState.getBool(StateKeys.FLAG_CAPTCHA_SHOWN) ? AppState.getString(StateKeys.SLOT_SCREEN_SUBTITLE) : null)).getStringAndClear());
                    AppState.addInt(StateKeys.COUNTER_SEARCH_RESULTS, 1);
                    errorCode2 = 0;
                }
                nextScreen = errorCode2;
                break;
            case ScreenId.UNUSED_148:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_149:
                nextScreen = 0;
                break;
            case ScreenId.CONTACT_MODIFY:
                nextScreen = -1;
                break;
            case ScreenId.EXT_SETTINGS:
                nextScreen = AppController.handleExtSettingsOption(action);
                break;
            case ScreenId.MAP_VIEW_SETTINGS:
                nextScreen = AppController.handleContactOption(action);
                break;
            case ScreenId.WIFI_NETWORKS:
                nextScreen = ResourceManager.setSelectedObject(obj);
                break;
            case ScreenId.SHARE_LOCATION:
                NetworkUtils.processScreenForm();
                String msgId = AppState.getString(StateKeys.SLOT_MSG_ID_1);
                long lon2 = MapRenderer.currentLon;
                long lat2 = MapRenderer.currentLat;
                ListItem tooltipItem2 = MapRenderer.tooltipItem;
                if (tooltipItem2 != null && tooltipItem2.isSelected()) {
                    lon2 = tooltipItem2.getWidth();
                    lat2 = tooltipItem2.getBaseHeight();
                    tooltipItem2.select();
                }
                String msgId2 = AppState.getString(StateKeys.SLOT_MSG_ID_2);
                long j = lon2;
                long j2 = lat2;
                if (msgId != null) {
                    XmppContactGroup.sharedContactList.addElement(new Object[]{msgId, new long[]{j, j2}, msgId2});
                }
                long j3 = lon2;
                long j4 = lat2;
                if (msgId != null) {
                    String sessionKey = Utils.defaultStr(AppState.getString(StateKeys.SESSION_KEY));
                    ByteBuffer requestBuf = new ByteBuffer().writeCompressed(3150648).writeUInt(15713).writeRawString(msgId).writeUInt(4022822).writeLongAsString(j3).writeUInt(4023078).writeLongAsString(j4).writeUInt(4023334).writeRawString(sessionKey).writeUInt(4023590).writeRawString(new ByteBuffer().writeRawString(sessionKey).writeCompressed(396139).writeLongAsString(j3).encryptMD5().toHexString());
                    if (msgId2 != null) {
                        requestBuf.writeUInt(4023846).writeEncodedString(msgId2);
                    }
                    if (AppState.getBool(StateKeys.FLAG_REGISTRATION_DONE)) {
                        String msgId3 = AppState.getString(StateKeys.LAST_ACCOUNT_NAME);
                        if (Utils.nonEmpty(msgId3)) {
                            requestBuf.writeUInt(4024102).writeEncodedString(msgId3);
                        }
                    }
                    new AsyncTask(16, requestBuf.getStringAndClear());
                }
                MapRenderer.needsRedraw = true;
                nextScreen = 0;
                break;
            case ScreenId.VISIBLE_CONTACTS:
                Vector contactIds = ConnectionThread.getAllContactIds();
                StringBuffer sb2 = NetworkUtils.newStringBuffer();
                Vector vector = currentScreen.menuItems;
                int size3 = vector.size();
                for (int i7 = 0; i7 < size3; i7++) {
                    if (!((Boolean) ((MenuItem) vector.elementAt(i7)).data).booleanValue()) {
                        sb2.append(contactIds.elementAt(i7)).append((char) 0);
                    }
                }
                String hiddenStr = NetworkUtils.bufToStringCached(sb2);
                ConnectionThread.hiddenContacts = Utils.split(hiddenStr, (char) 0);
                AppState.setObject(StateKeys.HIDDEN_CONTACTS_LIST, (Object) hiddenStr);
                nextScreen = 0;
                break;
            case ScreenId.PHOTO_SELECTOR:
                nextScreen = IOUtils.applyPhotoSelection();
                break;
            case ScreenId.PHOTO_VIEW:
                nextScreen = 0;
                break;
            case ScreenId.MAP_SEARCH:
                nextScreen = AppController.handleViewOption(action);
                break;
            case ScreenId.WIFI_ACCOUNT_LIST:
                nextScreen = AppController.handleItemAction(obj);
                break;
            case ScreenId.PROFILE_EDIT:
                nextScreen = ResourceManager.syncAndReturn();
                break;
            case ScreenId.SEND_CONFIRM:
                nextScreen = -1;
                break;
            case ScreenId.CHAT_DETAIL:
                nextScreen = AppController.handleChatDetailOption(action);
                break;
            case ScreenId.NOTIFY_MESSAGE:
                nextScreen = AppController.handleSendKey();
                break;
            case ScreenId.REG_FORM:
                NetworkUtils.processScreenForm();
                String loginLower = XmppMailRuProtocol.getLoginLowerCase();
                String fullLogin = loginLower;
                if (!XmppMailRuProtocol.isMailRuDomain(loginLower)) {
                    fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(694, AppState.getInt(StateKeys.INT_SERVER_INDEX)));
                }
                if (XmppMailRuProtocol.isValidUsername(fullLogin)) {
                    String str2 = fullLogin;
                    String password = Utils.defaultStr(AppState.getString(StateKeys.SLOT_PASSWORD));
                    String firstName = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SCREEN_TITLE));
                    int intVal3 = AppState.getInt(StateKeys.INT_SETTINGS_THEME);
                    AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = NetworkUtils.createRegRequest(str2, 0, password, firstName, 0 == intVal3 ? Utils.defaultStr(AppState.getString(StateKeys.SLOT_DEVICE_ID)) : (String) Utils.splitNonEmpty(AppState.getString(StateKeys.STR_MENU_REG_SMS), (char) 0).elementAt(intVal3), Utils.defaultStr(AppState.getString(StateKeys.SLOT_APP_VERSION_STRING)), Utils.defaultStr(AppState.getString(StateKeys.SLOT_FIRST_NAME)), Utils.defaultStr(AppState.getString(StateKeys.SLOT_LAST_NAME)), AppState.getInt(StateKeys.INT_SEARCH_GENDER), AppState.getInt(StateKeys.INT_SEARCH_AGE), AppState.getInt(StateKeys.INT_REG_DOMAIN_INDEX), AppState.getInt(StateKeys.INT_COUNTRY_CODE), AppState.getInt(StateKeys.INT_REGION_CODE), AppState.getString(StateKeys.STR_MAP_LOCATION_NAME), AppState.getString(StateKeys.STR_MAP_LOCATION_URL));
                    errorCode = 13;
                } else {
                    errorCode = NotificationHelper.showError(559);
                }
                nextScreen = errorCode;
                break;
            case ScreenId.ASYNC_CONFIRM:
                nextScreen = -1;
                break;
            case ScreenId.CHAT_OPTIONS:
                nextScreen = AppController.handleChatOption(action);
                break;
            case ScreenId.MAILBOX_OPTIONS:
                nextScreen = AppController.handleMailboxOption(action);
                break;
            case ScreenId.INVITE_TOS:
                nextScreen = ResourceManager.collectInvitees(currentScreen);
                break;
            case ScreenId.SAVED_LOCATIONS:
                nextScreen = ResourceManager.applyLocationProfile(obj);
                break;
            case ScreenId.FORM_LIST:
                nextScreen = AppController.handleFormSubmit(obj);
                break;
            case ScreenId.UPDATE_ALERT:
                nextScreen = AppController.handleRightKey();
                break;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                nextScreen = AppController.handleObjectAction(obj);
                break;
            case ScreenId.INVITE_ALERT:
                nextScreen = AppController.handleInviteAction();
                break;
            case ScreenId.MAP_OPTIONS:
                nextScreen = 0;
                break;
            case ScreenId.NEARBY_SETTINGS:
                NetworkUtils.processScreenForm();
                if (AppState.getBool(StateKeys.FLAG_MAP_DATA_LOADED)) {
                    IOUtils.requestNearbyPeople();
                }
                nextScreen = 0;
                break;
            case ScreenId.PHONE_CONTACTS:
                nextScreen = AppController.handleStarAction(obj);
                break;
            case ScreenId.SEARCH_ENTRY:
                nextScreen = ResourceManager.handleSearchResultAction(action);
                break;
            case ScreenId.EDIT_SCREEN:
                nextScreen = AppController.handleEditAction(action);
                break;
            case ScreenId.SEND_DATA:
                nextScreen = -1;
                break;
            case ScreenId.ASYNC_TASK:
                nextScreen = -1;
                break;
        }
        RemoteLogger.log("UI", "onMenuItemSelected screenId=" + ScreenManager.getCurrentScreen().screenId + " next=" + nextScreen + " skL=" + currentScreen.softKeyLeft);
        if (nextScreen != -1) {
            if (nextScreen == 12) {
                onScreenClosed();
                return;
            }
            if (nextScreen != 0) {
                openScreen(nextScreen);
                return;
            }
            int i8 = currentScreen.softKeyLeft;
            if (i8 != 200) {
                int i9 = i8 == 199 ? action : i8;
                int i10 = i9;
                RemoteLogger.log("UI", "softKeyLeft action=" + i10);
                if (i9 == 12) {
                    onScreenClosed();
                } else if (i10 != 0) {
                    openScreen(i10);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: b */
    public static final void onMenuItemAction() {
        int sendMsgResult;
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        Screen currentScreen = ScreenManager.getCurrentScreen();
        int i = ScreenManager.getCurrentScreen().screenId;
        ScreenManager.getCurrentTitle();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object obj = menuItem == null ? null : menuItem.data;
        int result = 0;
        switch (i) {
            case ScreenId.ACCOUNT_LIST:
                result = 0;
                break;
            case ScreenId.SETTINGS:
                result = 0;
                break;
            case ScreenId.STATUS_DIALOG:
                result = 0;
                break;
            case ScreenId.CONTACT_LIST:
                result = ContactListManager.onContactAction(obj);
                break;
            case ScreenId.ACCOUNTS_MENU:
                result = 0;
                break;
            case ScreenId.MAP:
                result = ConnectionThread.handleMapBack(currentScreen);
                break;
            case ScreenId.MAP_MENU:
                result = 0;
                break;
            case ScreenId.SETTINGS_MENU:
                result = 0;
                break;
            case ScreenId.ABOUT:
                result = 0;
                break;
            case ScreenId.BLOCK_CONFIRM:
                result = 0;
                break;
            case ScreenId.UNBLOCK_CONFIRM:
                result = 0;
                break;
            case ScreenId.CONFIRM_EXIT:
                result = 0;
                break;
            case ScreenId.TRAFFIC_COST:
                result = 0;
                break;
            case ScreenId.ACCOUNT_SWITCHER:
                result = 0;
                break;
            case ScreenId.REGISTRATION:
                result = 0;
                break;
            case ScreenId.EMOTICON_DIALOG:
                result = 0;
                break;
            case ScreenId.UNUSED_18:
                result = 0;
                break;
            case ScreenId.CONTACT_EDITOR:
                result = 0;
                break;
            case ScreenId.GPS_SETTINGS:
                result = 0;
                break;
            case ScreenId.ADD_CONTACT:
                result = 0;
                break;
            case ScreenId.ADD_MRIM_CONTACT:
                result = 0;
                break;
            case ScreenId.UNUSED_23:
                result = 0;
                break;
            case ScreenId.UNUSED_24:
                result = 0;
                break;
            case ScreenId.MULTI_ACCOUNT_LIST:
                result = 0;
                break;
            case ScreenId.THEME_SETTINGS:
                AppController.needsLayoutUpdate = true;
                result = 0;
                break;
            case ScreenId.NOTIFICATION_SETTINGS:
                result = 0;
                break;
            case ScreenId.SOUND_SETTINGS:
                result = 0;
                break;
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                result = 0;
                break;
            case ScreenId.CONTACT_GROUP_MENU:
                result = 0;
                break;
            case ScreenId.UNUSED_32:
                result = 0;
                break;
            case ScreenId.PRIVACY_SETTINGS:
                result = 0;
                break;
            case ScreenId.TRAFFIC_STATS:
                result = 0;
                break;
            case ScreenId.CONNECTION_SETTINGS:
                result = 0;
                break;
            case ScreenId.MAIL_ACCOUNT_LIST:
                result = ResourceManager.selectMailAccount(obj);
                break;
            case ScreenId.CHAT_ROOMS:
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_INIT:
                result = 0;
                break;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                result = 0;
                break;
            case ScreenId.CLEAR_SEARCH:
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_MESSAGES:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_INVITE:
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_VIEW:
                result = 0;
                break;
            case ScreenId.SUBMIT_REGISTRATION:
                result = 0;
                break;
            case ScreenId.UNUSED_45:
                result = 0;
                break;
            case ScreenId.UNUSED_46:
                result = 0;
                break;
            case ScreenId.CONTACT_GROUPS:
                result = 0;
                break;
            case ScreenId.MESSAGE_DETAIL:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_CONFIG:
                result = 0;
                break;
            case ScreenId.CHAT_VIEW_MODE:
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_CONTEXT:
                result = 0;
                break;
            case ScreenId.MESSAGE_PREVIEW:
                result = 0;
                break;
            case ScreenId.COMPOSE_RECIPIENTS:
                result = 0;
                break;
            case ScreenId.COMPOSE_MESSAGE:
                result = 0;
                break;
            case ScreenId.DELETE_CONFIRM:
                result = -1;
                break;
            case ScreenId.CONTACT_SETTINGS:
                result = 0;
                break;
            case ScreenId.FIRST_RUN:
                result = 0;
                break;
            case ScreenId.GROUP_SELECTOR:
                result = 0;
                break;
            case ScreenId.VERSION_CHECK:
                result = 0;
                break;
            case ScreenId.INPUT_DIALOG:
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_ALERT:
                result = ScreenId.CLOSE;
                break;
            case ScreenId.MAIL_MENU:
                result = 0;
                break;
            case ScreenId.STATUS_INPUT:
                String inputText = XmppContactGroup.getTextInputValue();
                if (!StringUtils.isEmpty(inputText) && 0 != (sendMsgResult = AppState.getCurrentContact().sendMessage(inputText))) {
                    onScreenClosed();
                    IOUtils.postNotification(AppState.getString(sendMsgResult));
                }
                AppState.setInt(StateKeys.FLAG_STATUS_TEXT_SET, 0);
                AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas());
                onScreenClosed();
                result = ScreenId.CLEAR_SEARCH;
                break;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                result = 0;
                break;
            case ScreenId.PHONE_GROUPS:
                result = 0;
                break;
            case ScreenId.ADD_CONTACT_INFO:
                result = 0;
                break;
            case ScreenId.SOFTKEY_MENU:
                result = 0;
                break;
            case ScreenId.SEARCH_RESULTS:
                result = AppController.handleBackKey();
                break;
            case ScreenId.CREATE_GROUP:
                result = 0;
                break;
            case ScreenId.RENAME_GROUP:
                result = 0;
                break;
            case ScreenId.DELETE_ENTITY:
                result = 0;
                break;
            case ScreenId.BATCH_DELETE:
                result = 0;
                break;
            case ScreenId.SEARCH_RESULT_LIST:
                result = 0;
                break;
            case ScreenId.UNUSED_74:
                result = 0;
                break;
            case ScreenId.UNUSED_75:
                result = 0;
                break;
            case ScreenId.XMPP_LOGIN:
                result = 0;
                break;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                result = 0;
                break;
            case ScreenId.SHARE_MEDIA:
                result = 0;
                break;
            case ScreenId.SHARE_ALERT:
                result = 0;
                break;
            case ScreenId.NOTIFICATION_OPTIONS:
                result = 0;
                break;
            case ScreenId.SEND_MAIL:
                result = 0;
                break;
            case ScreenId.REPLY_MAIL:
                result = 0;
                break;
            case ScreenId.PRIVACY_MODE:
                result = AppController.handleHashKey();
                break;
            case ScreenId.STATUS_PREVIEW:
                onScreenClosed();
                result = 0;
                break;
            case ScreenId.CONTACT_DELETE:
                AppController.clearMapPoints();
                result = 0;
                break;
            case ScreenId.GROUP_MOVE:
                result = 0;
                break;
            case ScreenId.CHAT_STATUS:
                result = 0;
                break;
            case ScreenId.THEME_OPTIONS:
                result = 0;
                break;
            case ScreenId.TOS_SCREEN:
                result = 0;
                break;
            case ScreenId.EVENT_QUEUE:
                result = 0;
                break;
            case ScreenId.VIEW_MODE:
                result = 0;
                break;
            case ScreenId.CONTACT_MENU:
                result = 0;
                break;
            case ScreenId.EMOTICON_PICKER:
                result = 0;
                break;
            case ScreenId.PHONE_INPUT:
                result = 0;
                break;
            case ScreenId.SERVER_ADDRESS:
                result = 0;
                break;
            case ScreenId.CONTACT_INFO_VIEW:
                AppController.clearMapPoints();
                result = 0;
                break;
            case ScreenId.REGION_SELECTOR:
                result = 0;
                break;
            case ScreenId.PHONE_INPUT_ALT:
                result = 0;
                break;
            case ScreenId.URL_OPEN:
                result = 0;
                break;
            case ScreenId.MAP_POINTS:
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                AppState.setInt(StateKeys.FLAG_LOADING, 0);
                result = 0;
                break;
            case ScreenId.CONVERSATION:
                AppState.setInt(StateKeys.FLAG_CONTACTS_LOADED, 0);
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                result = 0;
                break;
            case ScreenId.USER_PROFILE:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                result = 0;
                break;
            case ScreenId.CONTACT_INFO_DETAIL:
                result = 0;
                break;
            case ScreenId.COLOR_PICKER:
                result = 0;
                break;
            case ScreenId.XMPP_LOGIN_ALT:
                result = ScreenId.CLOSE;
                break;
            case ScreenId.CAPTCHA:
                result = 0;
                break;
            case ScreenId.PROFILE_LOAD:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                result = 0;
                break;
            case ScreenId.CONTACT_LIST_KEY:
                result = 0;
                break;
            case ScreenId.VERSION_SELECT:
                result = 0;
                break;
            case ScreenId.MAP_TOOLTIP:
                result = 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                result = 0;
                break;
            case ScreenId.CLEAR_NOTIFICATIONS:
                result = 0;
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                result = 0;
                break;
            case ScreenId.SAVE_LOCATION:
                result = 0;
                break;
            case ScreenId.MESSAGE_INPUT:
                result = 0;
                break;
            case ScreenId.MAP_ROUTE:
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                result = 0;
                break;
            case ScreenId.MAP_STATUS:
                result = 0;
                break;
            case ScreenId.SEND_TO_CONTACT:
                result = 0;
                break;
            case ScreenId.CHAT_ROOM_OPTIONS:
                result = 0;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                AppState.setInt(StateKeys.FLAG_CONTACTS_LOADED, 0);
                result = 0;
                break;
            case ScreenId.CHAT_LIST_OPTIONS:
                result = 0;
                break;
            case ScreenId.PRESENCE_ACTION:
                result = 0;
                break;
            case ScreenId.MESSAGE_SUMMARY:
                result = 0;
                break;
            case ScreenId.EMPTY_SCREEN:
                result = 0;
                break;
            case ScreenId.BLOCK_CONTACT_LIST:
                result = 0;
                break;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                result = 0;
                break;
            case ScreenId.DELETE_CONTACT_LIST:
                result = 0;
                break;
            case ScreenId.DELETE_MESSAGES:
                result = 0;
                break;
            case ScreenId.MMP_ACCOUNT_SELECT:
                result = 0;
                break;
            case ScreenId.VCARD_ACTIONS:
                result = 0;
                break;
            case ScreenId.PEOPLE_SEARCH:
                result = 0;
                break;
            case ScreenId.KEY_MAPPING:
                result = 0;
                break;
            case ScreenId.UNUSED_133:
                result = 0;
                break;
            case ScreenId.UNUSED_134:
                result = 0;
                break;
            case ScreenId.UNUSED_135:
                result = 0;
                break;
            case ScreenId.UNUSED_136:
                result = 0;
                break;
            case ScreenId.MAIN_SCREEN:
                result = ScreenId.CLOSE;
                break;
            case ScreenId.UNUSED_138:
                result = 0;
                break;
            case ScreenId.UNUSED_139:
                result = ScreenId.MAP;
                break;
            case ScreenId.FORM_SETTINGS:
                result = 0;
                break;
            case ScreenId.UNUSED_141:
                result = 0;
                break;
            case ScreenId.GROUP_MEMBERS:
                result = 0;
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                result = 0;
                break;
            case ScreenId.EDIT_MEMBERS:
                result = 0;
                break;
            case ScreenId.CONTACT_DELETE_MRIM:
                result = 0;
                break;
            case ScreenId.GROUP_MANAGEMENT:
                result = 0;
                break;
            case ScreenId.BLOG_POST:
                result = 0;
                break;
            case ScreenId.UNUSED_148:
                result = 0;
                break;
            case ScreenId.UNUSED_149:
                result = 0;
                break;
            case ScreenId.CONTACT_MODIFY:
                result = ScreenId.CLOSE;
                break;
            case ScreenId.EXT_SETTINGS:
                result = 0;
                break;
            case ScreenId.MAP_VIEW_SETTINGS:
                result = 0;
                break;
            case ScreenId.WIFI_NETWORKS:
                result = 0;
                break;
            case ScreenId.SHARE_LOCATION:
                result = 0;
                break;
            case ScreenId.VISIBLE_CONTACTS:
                result = 0;
                break;
            case ScreenId.PHOTO_SELECTOR:
                result = 0;
                break;
            case ScreenId.PHOTO_VIEW:
                result = 0;
                break;
            case ScreenId.MAP_SEARCH:
                result = 0;
                break;
            case ScreenId.WIFI_ACCOUNT_LIST:
                result = 0;
                break;
            case ScreenId.PROFILE_EDIT:
                onScreenClosed();
                result = 0;
                break;
            case ScreenId.SEND_CONFIRM:
                result = ScreenId.CONTACT_LIST;
                break;
            case ScreenId.CHAT_DETAIL:
                result = 0;
                break;
            case ScreenId.NOTIFY_MESSAGE:
                result = 0;
                break;
            case ScreenId.REG_FORM:
                result = 0;
                break;
            case ScreenId.ASYNC_CONFIRM:
                result = 0;
                break;
            case ScreenId.CHAT_OPTIONS:
                result = 0;
                break;
            case ScreenId.MAILBOX_OPTIONS:
                result = 0;
                break;
            case ScreenId.INVITE_TOS:
                result = 0;
                break;
            case ScreenId.SAVED_LOCATIONS:
                ((MrimAccount) AppState.getAccount()).isHighlighted = true;
                result = 0;
                break;
            case ScreenId.FORM_LIST:
                result = 0;
                break;
            case ScreenId.UPDATE_ALERT:
                AppState.setInt(StateKeys.FLAG_APP_STARTING, 0);
                ConnectionThread.toggleScrollMode();
                result = ScreenId.MAP;
                break;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                result = 0;
                break;
            case ScreenId.INVITE_ALERT:
                AppState.setAccount((Object) null);
                result = ScreenId.CLOSE;
                break;
            case ScreenId.MAP_OPTIONS:
                result = 0;
                break;
            case ScreenId.NEARBY_SETTINGS:
                result = 0;
                break;
            case ScreenId.PHONE_CONTACTS:
                result = ScreenId.CLOSE;
                break;
            case ScreenId.SEARCH_ENTRY:
                result = 0;
                break;
            case ScreenId.EDIT_SCREEN:
                result = 0;
                break;
            case ScreenId.SEND_DATA:
                result = 0;
                break;
            case ScreenId.ASYNC_TASK:
                result = 0;
                break;
        }
        RemoteLogger.log("UI", "onMenuItemAction screenId=" + ScreenManager.getCurrentScreen().screenId + " result=" + result + " skC=" + currentScreen.softKeyCenter);
        if (result != -1) {
            if (result == 12) {
                onScreenClosed();
                return;
            }
            if (result != 0) {
                openScreen(result);
                return;
            }
            int i2 = currentScreen.softKeyCenter;
            if (i2 != 200) {
                if (i2 == 12) {
                    onScreenClosed();
                } else if (i2 != 0) {
                    openScreen(i2);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: c */
    public static final void onScreenClosed() {
        AppController.needsRepaint = true;
        switch (ScreenManager.getCurrentScreen().screenId) {
            case ScreenId.SETTINGS:
                AppState.setBool(StateKeys.SETTING_STATUS_BAR_VISIBLE, AppState.getBool(StateKeys.FLAG_FULLSCREEN_REQUESTED));
                AppState.getCanvas().updateFullScreenMode();
                AppState.setInt(StateKeys.FLAG_FULLSCREEN_ACTIVE, 0);
                break;
            case ScreenId.MAP:
                TabBar.scrollEnabled = false;
                TabBar.removeSearchTab();
                break;
            case ScreenId.ABOUT:
                AppController.clearPreviewState();
                break;
            case ScreenId.TRAFFIC_COST:
                AppState.clearIndex(StateKeys.SLOT_SCREEN_VALUE);
                break;
            case ScreenId.CONTACT_EDITOR:
                AppController.clearFormFields();
                break;
            case ScreenId.ADD_CONTACT:
                int accountType = AppState.getAccount().getType();
                if (accountType != 0 && accountType == 1) {
                    AppState.clearIndex(StateKeys.SLOT_SEARCH_FIELD_1);
                    AppState.clearIndex(StateKeys.SLOT_SEARCH_FIELD_2);
                    AppState.clearIndex(StateKeys.SLOT_SEARCH_FIELD_3);
                    AppState.clearIndex(StateKeys.SLOT_SEARCH_FIELD_4);
                    AppState.clearIndex(StateKeys.SLOT_SEARCH_FIELD_5);
                    AppState.clearIndex(StateKeys.SLOT_SEARCH_FIELD_6);
                    AppState.setInt(StateKeys.FLAG_REG_SMS_MODE, 0);
                    break;
                } else {
                    StringUtils.resetRegForm();
                    break;
                }
            case ScreenId.ADD_MRIM_CONTACT:
                AppController.clearFormFields();
                break;
            case ScreenId.MULTI_ACCOUNT_LIST:
                AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
                break;
            case ScreenId.THEME_SETTINGS:
                AppState.setInt(StateKeys.SETTING_COLOR_THEME, AppState.getInt(StateKeys.INT_SETTINGS_THEME));
                break;
            case ScreenId.MAIL_ACCOUNT_LIST:
                TabBar.removeSettingsTab();
                break;
            case ScreenId.CHAT_ROOMS:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                AppState.clearIndex(StateKeys.VEC_FILTERED_ACCOUNTS);
                break;
            case ScreenId.CLEAR_SEARCH:
                AppState.clearIndex(StateKeys.SLOT_CURRENT_MSG_TEXT);
                AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
                break;
            case ScreenId.CHAT_ROOM_MESSAGES:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_INVITE:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.MESSAGE_DETAIL:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CHAT_ROOM_CONTEXT:
                AppState.clearIndex(StateKeys.SLOT_UNREAD_COUNT_TEXT);
                break;
            case ScreenId.COMPOSE_MESSAGE:
                AppState.clearRange(StateKeys.SLOT_MSG_EXTRA_2, StateKeys.SLOT_TRAFFIC_STATUS_TEXT);
                break;
            case ScreenId.VERSION_CHECK:
                AppState.clearIndex(StateKeys.SLOT_SCREEN_TITLE);
                AppState.clearIndex(StateKeys.SLOT_SCREEN_SUBTITLE);
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.ADD_CONTACT_INFO:
                AppState.clearIndex(StateKeys.SLOT_CONTACT_INFO);
                AppState.clearRange(StateKeys.SLOT_GROUP_ADD_NAME, StateKeys.VEC_GROUP_LIST);
                break;
            case ScreenId.SOFTKEY_MENU:
                IOUtils.setSelectedItems((Object) null);
                break;
            case ScreenId.SEARCH_RESULTS:
                AppController.resetSearchResults();
                break;
            case ScreenId.CREATE_GROUP:
                AppState.clearIndex(StateKeys.SLOT_NEW_GROUP_NAME);
                break;
            case ScreenId.RENAME_GROUP:
                AppState.clearIndex(StateKeys.SLOT_SEARCH_RESULT);
                break;
            case ScreenId.BATCH_DELETE:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.SEARCH_RESULT_LIST:
                AppController.clearSearchResults2();
                break;
            case ScreenId.XMPP_LOGIN:
                XmppMailRuProtocol.clearLoginFields();
                break;
            case ScreenId.SHARE_MEDIA:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.SEND_MAIL:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.REPLY_MAIL:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CONTACT_DELETE:
                if (AppState.getCurrentContact() != null) {
                    AppState.getCurrentContact().mo148L();
                    break;
                }
                break;
            case ScreenId.CONTACT_INFO_VIEW:
                AppState.clearIndex(StateKeys.SLOT_REG_PARAM_1);
                break;
            case ScreenId.MAP_POINTS:
                AppState.clearIndex(StateKeys.SLOT_SEARCH_QUERY);
                break;
            case ScreenId.CONVERSATION:
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                AppState.setInt(StateKeys.FLAG_LOADING, 0);
                break;
            case ScreenId.XMPP_LOGIN_ALT:
                XmppMailRuProtocol.clearLoginFields();
                break;
            case ScreenId.CONTACT_LIST_KEY:
                AccountManager.updateTabBar();
                AppState.clearIndex(StateKeys.SLOT_TEMP_ACCOUNT);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                XmppMailRuProtocol.mapContextItem = null;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                break;
            case ScreenId.PRESENCE_ACTION:
                NetworkUtils.releaseVector(AppState.getVector(StateKeys.VEC_ACCOUNT_SELECTION));
                AppState.clearIndex(StateKeys.VEC_ACCOUNT_SELECTION);
                break;
            case ScreenId.UNUSED_138:
                AppController.refreshContactList();
                break;
            case ScreenId.GROUP_MEMBERS:
                AppState.clearIndex(StateKeys.OBJ_PHOTO_CACHE_1);
                break;
            case ScreenId.CREATE_CHAT_ROOM:
                AppState.clearIndex(StateKeys.SLOT_CHAT_NAME);
                break;
            case ScreenId.BLOG_POST:
                AppController.clearPreviewState();
                break;
            case ScreenId.SHARE_LOCATION:
                AppState.clearIndex(StateKeys.SLOT_MSG_ID_1);
                AppState.clearIndex(StateKeys.SLOT_MSG_ID_2);
                break;
            case ScreenId.PHOTO_SELECTOR:
                IOUtils.photoUrlList = null;
                IOUtils.contactIdList = null;
                break;
            case ScreenId.REG_FORM:
                AppState.clearIndex(StateKeys.SLOT_CHAT_NAME);
                AppState.clearIndex(StateKeys.SLOT_PASSWORD);
                AppState.clearIndex(StateKeys.SLOT_SCREEN_TITLE);
                AppState.setInt(StateKeys.INT_SERVER_INDEX, 0);
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, 0);
                AppState.clearRange(StateKeys.SLOT_MAP_SEARCH_QUERY, StateKeys.STR_MAP_LOCATION_URL);
                AppController.clearPreviewState();
                StringUtils.resetRegForm();
                break;
            case ScreenId.INVITE_TOS:
                AppController.clearFormFields();
                break;
            case ScreenId.PHONE_CONTACTS:
                AppState.clearRange(StateKeys.RANGE_PHONE_CONTACT_START, StateKeys.OBJ_SEARCH_RESULT);
                break;
            case ScreenId.SEND_DATA:
                AppState.clearIndex(StateKeys.SLOT_SCREEN_TITLE);
                break;
            case ScreenId.ASYNC_TASK:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
        }
        Vector screenStack = AppState.getVector(StateKeys.VEC_SCREEN_STACK);
        int size = screenStack.size() - 1;
        Screen closedScreen = (Screen) screenStack.elementAt(size);
        NetworkUtils.releaseVector(closedScreen.tabItems);
        NetworkUtils.releaseVector(closedScreen.menuItems);
        screenStack.removeElementAt(size);
        Utils.trimIfEmpty(screenStack);
    }
}
