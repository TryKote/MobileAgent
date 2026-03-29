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
import javax.microedition.lcdui.TextBox;

public final class ContactHandler extends BaseScreenHandler {


    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                ContactListManager.showContactList();
                return;
            case ScreenId.CONTACT_EDITOR:
                ScreenManager.clearFormFields();
                Contact contact = Storage.state().getCurrentContact();
                Storage.state().setObject(UIKeys.SLOT_INPUT_TEXT, (Object) contact.displayName);
                Vector nameParts = Utils.splitNonEmpty(contact.getDefaultName(), ',');
                for (int i = 0; i < 3; i++) {
                    if (i < nameParts.size()) {
                        Storage.state().setObject(UIKeys.CONTACT_NAME_PARTS_BASE + i, nameParts.elementAt(i));
                    }
                }
                ObjectPool.releaseVector(nameParts);
                Storage.state().setInt(ContactKeys.INT_CONTACT_TYPE_CODE, (!(contact instanceof MrimContact) || contact.isSystem()) ? 1 : 4);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_EDITOR));
                return;
            case ScreenId.ADD_CONTACT:
                int accountType = Storage.state().getAccount().getType();
                if (accountType == 0) {
                    StringUtils.showRegionSelector();
                    return;
                }
                if (accountType == 1) {
                    Storage.state().setInt(RegistrationKeys.INT_REG_DOMAIN_INDEX, -1);
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ADD_CONTACT));
                    return;
                }
                StringUtils.resetRegForm();
                if (ContactListManager.getGroupCount(Storage.state().getAccount()) == 0) {
                    EventDispatcher.postNotification(Storage.resources().getString(StringResKeys.STR_NOTIFICATION_NEW_MSG));
                    return;
                } else {
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ADD_CONTACT_FORM));
                    return;
                }
            case ScreenId.ADD_MRIM_CONTACT:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ADD_MRIM_CONTACT));
                return;
            case ScreenId.CONTACT_GROUP_MENU:
                Storage.state().setInt(ContactKeys.FLAG_CONTACT_MENU_MODE, 1);
                Object obj = Storage.state().getObject(ContactKeys.SLOT_CURRENT_ENTITY);
                if (obj instanceof ContactGroup) {
                    Storage.state().setInt(ContactKeys.FLAG_IS_MRIM_CONTACT, 1);
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_GROUP_MENU));
                    return;
                }
                Contact selectedContact = (Contact) obj;
                if (selectedContact.isSystem()) {
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_ACTION, 30);
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_TYPE, 4);
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_MENU));
                    return;
                }
                ContactListManager.updateContactFlags(selectedContact);
                Storage.state().setInt(ContactKeys.FLAG_IS_MRIM_CONTACT, 0);
                boolean z2 = selectedContact instanceof MrimContact;
                boolean z3 = z2;
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_MRIM, z2);
                boolean z4 = z3 && selectedContact.isOffline();
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_GROUP, z4);
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_USER, z3 && !z4);
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_ONLINE, selectedContact.isOnline());
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_HAS_UNREAD, selectedContact.hasUnread() && !selectedContact.isOnline());
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_HAS_VCARD, z3 && !z4 && ((MrimContact) selectedContact).hasVCard());
                Storage.state().setInt(UIKeys.INT_OK_MENU_TYPE, 4);
                Storage.state().setInt(UIKeys.INT_OK_MENU_ACTION, 30);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_ACTIONS_MENU));
                return;
            case ScreenId.CONTACT_GROUPS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_GROUPS));
                return;
            case ScreenId.CONTACT_SETTINGS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_SETTINGS));
                return;
            case ScreenId.GROUP_SELECTOR:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.GROUP_SELECTOR));
                return;
            case ScreenId.PHONE_GROUPS:
                Vector phoneGroups = Utils.splitNonEmpty(Storage.state().getCurrentMrimContact().contactGroupsStr, ',');
                int size = phoneGroups.size();
                if (size <= 0) {
                    NotificationHelper.showMessageById(713);
                    return;
                }
                StringBuffer sb = ObjectPool.newStringBuffer();
                for (int i = 0; i < size; i++) {
                    sb.append(Utils.formatPhone((String) phoneGroups.elementAt(i))).append((char) 0);
                }
                Storage.state().setFromBuffer(RegistrationKeys.SLOT_SEARCH_LABEL_1, sb);
                Storage.state().setInt(ContactKeys.INT_SELECTED_GROUP_INDEX, 0);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.PHONE_GROUPS));
                return;
            case ScreenId.ADD_CONTACT_INFO:
                ContactListManager.showAddContactScreen();
                return;
            case ScreenId.CREATE_GROUP:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CREATE_GROUP));
                return;
            case ScreenId.RENAME_GROUP:
                Storage.state().setObject(RegistrationKeys.SLOT_SEARCH_RESULT, (Object) Storage.state().getCurrentGroup().name);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.RENAME_GROUP));
                return;
            case ScreenId.DELETE_ENTITY:
                StringBuffer sbAlert = ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ALERT_PREFIX));
                Object obj3 = Storage.state().getObject(ContactKeys.SLOT_CURRENT_ENTITY);
                NotificationHelper.showAlertBuffer(71, sbAlert.append(obj3 instanceof ContactGroup ? ((ContactGroup) obj3).name : ((Contact) obj3).displayName).append(ObjectPool.unpackChars(16167)));
                return;
            case ScreenId.BATCH_DELETE:
                NotificationHelper.showConfirmDialog(72, 866);
                Vector selectedItems = Storage.state().getVector(UIKeys.SLOT_MEDIA_STREAM);
                Vector itemsParams = ObjectPool.newVector();
                int size7 = selectedItems.size();
                while (true) {
                    size7--;
                    if (size7 < 0) {
                        Vector outerParams = ObjectPool.newVector();
                        outerParams.addElement(itemsParams);
                        MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(Storage.resources().getString(PackedStringKeys.URL_PATH_AJAX_MARKMSG), ObjectPool.newStringBuffer().append(Storage.resources().getString(PackedStringKeys.PARAM_AJAX_CALL)).append(Storage.resources().getString(PackedStringKeys.FUNC_AJAX_MARK_MSG)).append(Storage.state().getString(SessionKeys.SLOT_SESSION_HASH)).append(Storage.resources().getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(outerParams)))));
                        return;
                    } else {
                        Hashtable hashtable = new Hashtable();
                        JsonParser.putIntKey(hashtable, 329240, JsonParser.getVectorElement(selectedItems, size7));
                        JsonParser.putIntKey(hashtable, 263673, ObjectPool.integerOf(Storage.state().getInt(ChatKeys.INT_CHAT_VIEW_MODE)));
                        itemsParams.addElement(hashtable);
                    }
                }
            case ScreenId.CONTACT_DELETE:
                Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_1);
                if (Storage.state().getCurrentContact() == null) {
                    NotificationHelper.showErrorOrConfirm(85, 727, 0);
                    return;
                } else {
                    Contact contact3 = Storage.state().getCurrentContact();
                    NotificationHelper.showErrorOrConfirm(85, 727, contact3.account.validateDelete(contact3));
                    return;
                }
            case ScreenId.GROUP_MOVE:
                ListView screen4 = ScreenManager.createScreen(ScreenDef.GROUP_MOVE);
                Contact contact4 = Storage.state().getCurrentContact();
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
            case ScreenId.CONTACT_MENU:
                Storage.state().setInt(ContactKeys.FLAG_CONTACT_MENU_MODE, 0);
                Contact contact5 = Storage.state().getCurrentContact();
                if (contact5.isSystem()) {
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_ACTION, 92);
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_TYPE, 3);
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_MENU));
                    return;
                }
                ContactListManager.updateContactFlags(contact5);
                boolean z7 = contact5 instanceof MrimContact;
                boolean z8 = z7;
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_MRIM, z7);
                boolean z9 = z8 && contact5.isOffline();
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_GROUP, z9);
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_USER, z8 && !z9);
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_ONLINE, contact5.isOnline());
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_HAS_UNREAD, contact5.hasUnread() && !contact5.isOnline());
                Storage.state().setBool(ContactKeys.FLAG_CONTACT_HAS_VCARD, z8 && !z9 && ((MrimContact) contact5).hasVCard());
                Storage.state().setInt(UIKeys.INT_OK_MENU_TYPE, 3);
                Storage.state().setInt(UIKeys.INT_OK_MENU_ACTION, 92);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_ACTIONS_MENU));
                return;
            case ScreenId.CONTACT_INFO_VIEW:
                ContactInfo contactInfo = (ContactInfo) Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_1);
                String str = (String) contactInfo.get(ObjectPool.integerOf(-1));
                if (null != str) {
                    NotificationHelper.showNotification(str);
                } else {
                    Storage.state().setInt(RuntimeKeys.INT_INFO_SCREEN_MODE, contactInfo.isXmppContact() ? 0 : 503);
                    ScreenManager.showScreen(contactInfo.buildContactScreen(ScreenDef.CONTACT_INFO_VIEW_SCREEN));
                }
                Storage.state().setInt(UIKeys.INT_CURRENT_SCREEN_ID, ScreenId.USER_PROFILE);
                return;
            case ScreenId.CONTACT_INFO_DETAIL:
                ScreenManager.showScreen(((ContactInfo) Storage.state().getObject(ContactKeys.SLOT_CONTACT_INFO)).buildContactScreen(ScreenDef.CONTACT_INFO_DETAIL_SCREEN));
                Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_1);
                Storage.state().setInt(UIKeys.INT_CURRENT_SCREEN_ID, ScreenId.PROFILE_LOAD);
                return;
            case ScreenId.CONTACT_LIST_KEY:
                return;
            case ScreenId.BLOCK_CONTACT_LIST:
                Account account3 = Storage.state().getAccount();
                Vector contacts11 = ObjectPool.newVector();
                Enumeration contactEnum2 = account3.contactMap.elements();
                while (contactEnum2.hasMoreElements()) {
                    Contact contactToDelete = (Contact) contactEnum2.nextElement();
                    if (contactToDelete.canDelete()) {
                        contacts11.addElement(contactToDelete);
                    }
                }
                if (contacts11.size() > 0) {
                    ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.BLOCK_CONTACT_LIST), contacts11));
                } else {
                    NotificationHelper.showMessageById(762);
                }
                ObjectPool.releaseVector(contacts11);
                return;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                Account account4 = Storage.state().getAccount();
                Vector contacts12 = ObjectPool.newVector();
                Enumeration contactEnum3 = account4.contactMap.elements();
                while (contactEnum3.hasMoreElements()) {
                    Contact contactToBlock = (Contact) contactEnum3.nextElement();
                    if (contactToBlock.canBlock()) {
                        contacts12.addElement(contactToBlock);
                    }
                }
                if (contacts12.size() > 0) {
                    ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.UNBLOCK_CONTACT_LIST), contacts12));
                } else {
                    NotificationHelper.showMessageById(762);
                }
                ObjectPool.releaseVector(contacts12);
                return;
            case ScreenId.DELETE_CONTACT_LIST:
                Account account5 = Storage.state().getAccount();
                Vector contacts13 = ObjectPool.newVector();
                Enumeration contactEnum4 = account5.contactMap.elements();
                while (contactEnum4.hasMoreElements()) {
                    Contact contactToUnblock = (Contact) contactEnum4.nextElement();
                    if (contactToUnblock.canUnblock()) {
                        contacts13.addElement(contactToUnblock);
                    }
                }
                if (contacts13.size() > 0) {
                    ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.DELETE_CONTACT_LIST), contacts13));
                } else {
                    NotificationHelper.showMessageById(762);
                }
                ObjectPool.releaseVector(contacts13);
                return;
            case ScreenId.GROUP_MEMBERS:
                ListView screen15 = ScreenManager.createScreen(ScreenDef.GROUP_MEMBERS);
                MrimContact mrimContact = (MrimContact) Storage.state().getCurrentContact();
                MrimAccount mrimAccount4 = (MrimAccount) mrimContact.account;
                Vector groupMembers = Storage.state().getVector(RegistrationKeys.SLOT_REG_PARAM_4);
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
                    ListView labelScreen = screen15.addLabelById(772);
                    labelScreen.setSoftKeys(Storage.resources().getString(StringResKeys.STR_EMPTY), Storage.resources().getString(StringResKeys.STR_SOFTKEY_NO), labelScreen.softKeyLeft, labelScreen.softKeyCenter, labelScreen.softKeyRight);
                }
                ScreenManager.showScreen(screen15);
                Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_4);
                return;
            case ScreenId.EDIT_MEMBERS:
                ScreenManager.showScreen(ContactListManager.buildContactListScreen(ScreenManager.createScreen(ScreenDef.EDIT_MEMBERS), (Account) null, Storage.state().getCurrentContact()));
                return;
            case ScreenId.CONTACT_DELETE_MRIM:
                Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_1);
                NotificationHelper.showErrorOrConfirm(145, 727, ((MrimAccount) Storage.state().getCurrentContact().account).sendDeleteCommand(AppController.getPendingDisplayText()));
                return;
            case ScreenId.GROUP_MANAGEMENT:
                Storage.state().setBool(SessionKeys.FLAG_HAS_MULTIPLE_MRIM, AccountManager.getMrimAccountList().size() > 1);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.GROUP_MANAGEMENT));
                return;
            case ScreenId.CONTACT_MODIFY:
                ScreenManager.prepareFormData();
                MrimContact mrimContact3 = (MrimContact) Storage.state().getCurrentContact();
                MrimAccount mrimAccount5 = (MrimAccount) mrimContact3.account;
                NotificationHelper.showErrorOrConfirm(150, 504, mrimAccount5.trySendData(mrimAccount5.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount5, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(4194304).writeStringLatin1(mrimContact3.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeIntLE(4).writeIntLE(1)), ObjectPool.integerOf(MrimAccount.RESP_AUTH), mrimContact3, new Long(1L)})));
                return;
            case ScreenId.VISIBLE_CONTACTS:
                Vector contactIds = ServiceRegistry.getAllContactIds();
                int count = Utils.vectorSize(contactIds);
                if (count == 0) {
                    NotificationHelper.showMessageById(404);
                    return;
                }
                ListView screen16 = ScreenManager.createScreen(ScreenDef.GROUP_MANAGEMENT_ALT);
                for (int i23 = 0; i23 < count; i23++) {
                    Object contactId = contactIds.elementAt(i23);
                    screen16.addItem(MenuItem.createCheckbox(ServiceRegistry.getPhotoHost(contactId), !ServiceRegistry.hiddenContacts.contains(contactId)));
                }
                ScreenManager.showScreen(screen16);
                return;
        }
    }

    public int onMenuItemSelected(ListView currentScreen, MenuItem menuItem, String title, int action, Object obj) {
        switch (currentScreen.screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.getSelectedContact();
            case ScreenId.CONTACT_EDITOR:
                ScreenManager.processScreenForm();
                String[] phoneNumbers = Utils.getPhoneNumbers(false);
                Object[] objArr = new Object[phoneNumbers.length + 1];
                objArr[0] = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_INPUT_TEXT));
                for (int i = 0; i < phoneNumbers.length; i++) {
                    objArr[i + 1] = phoneNumbers[i];
                }
                Contact contact = Storage.state().getCurrentContact();
                int modifyResult;
                if (contact.isOnline()) {
                    contact.setDisplayName((String) objArr[0]);
                    AppController.needsLayoutUpdate = true;
                    modifyResult = 0;
                } else {
                    modifyResult = contact.account.validateModify(contact, objArr);
                }
                return 0 != modifyResult ? NotificationHelper.showError(modifyResult) : 0;
            case ScreenId.ADD_CONTACT:
                ScreenManager.processScreenForm();
                return Storage.state().getAccount() instanceof XmppProtocol ? ((XmppProtocol) Storage.state().getAccount()).addNewContact() : 0;
            case ScreenId.ADD_MRIM_CONTACT:
                ScreenManager.processScreenForm();
                MrimAccount mrimAccount = (MrimAccount) Storage.state().getAccount();
                String displayName = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_INPUT_TEXT));
                String[] phoneNumbers2 = Utils.getPhoneNumbers(false);
                int sendResult;
                if (!mrimAccount.isConnected()) {
                    sendResult = 299;
                } else if (Utils.nonEmpty(displayName)) {
                    int length = phoneNumbers2.length;
                    if (length == 0) {
                        sendResult = 709;
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
                                sendResult = 486;
                            } else {
                                MrimContactGroup contactGroup = mrimAccount.getFirstContactGroup();
                                ByteBuffer packetBuf = new ByteBuffer().writeIntLE(1048576).writeIntLE(103).writeStringLatin1(Storage.resources().getString(StringResKeys.STR_PHONE_SUFFIX)).writeStringUTF16(displayName);
                                String emailsJoined = Utils.joinComma(phoneNumbers2);
                                sendResult = mrimAccount.trySendData(mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_ADD_CONTACT, packetBuf.writeStringLatin1(emailsJoined).writeZeros(8)), ObjectPool.integerOf(MrimAccount.RESP_ADD_PHONE_CONTACT), displayName, emailsJoined, contactGroup}));
                            }
                        }
                    }
                } else {
                    sendResult = 708;
                }
                return 0 != sendResult ? NotificationHelper.showError(sendResult) : 0;
            case ScreenId.CONTACT_GROUP_MENU:
                return ContactListManager.handleContactGroupAction(title, action);
            case ScreenId.CONTACT_GROUPS:
                return 0;
            case ScreenId.CONTACT_SETTINGS:
                ScreenManager.processScreenForm();
                if (Storage.state().getBool(SettingsKeys.SETTING_SHOW_IN_LIST)) {
                    AccountManager.clearAllHighlights();
                }
                return 0;
            case ScreenId.GROUP_SELECTOR:
                return AccountManager.handleGroupSelection(action);
            case ScreenId.PHONE_GROUPS:
                ScreenManager.processScreenForm();
                Storage.state().setObject(ContactKeys.SLOT_SELECTED_GROUP, Utils.splitNonEmpty(Storage.state().getCurrentMrimContact().contactGroupsStr, ',').elementAt(Storage.state().getInt(ContactKeys.INT_SELECTED_GROUP_INDEX)));
                return 0;
            case ScreenId.ADD_CONTACT_INFO:
                ScreenManager.processScreenForm();
                int addResult = ((ContactInfo) Storage.state().getObject(ContactKeys.SLOT_CONTACT_INFO)).getAccount().validateGroupAdd(Utils.defaultStr(Storage.state().getString(ContactKeys.SLOT_GROUP_ADD_NAME)), Utils.defaultStr(Storage.state().getString(ContactKeys.SLOT_GROUP_ADD_DISPLAY)), Utils.defaultStr(Storage.state().getString(ContactKeys.SLOT_GROUP_ADD_GROUP)), (ContactGroup) Storage.state().getVector(ContactKeys.VEC_GROUP_LIST).elementAt(Storage.state().getInt(ContactKeys.INT_GROUP_OPERATION_RESULT)), Storage.state().getBool(ContactKeys.FLAG_GROUP_ADD_RESULT));
                return 0 != addResult ? NotificationHelper.showError(addResult) : 0;
            case ScreenId.CREATE_GROUP:
                ScreenManager.processScreenForm();
                int createResult = Storage.state().getAccount().validateGroupCreate(Utils.defaultStr(Storage.state().getString(ContactKeys.SLOT_NEW_GROUP_NAME)));
                return 0 != createResult ? NotificationHelper.showError(createResult) : 0;
            case ScreenId.RENAME_GROUP:
                ScreenManager.processScreenForm();
                int renameResult = Storage.state().getCurrentGroup().rename(Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_RESULT)));
                return 0 != renameResult ? NotificationHelper.showError(renameResult) : 0;
            case ScreenId.DELETE_ENTITY:
                return deleteSelectedEntity();
            case ScreenId.BATCH_DELETE:
                return -1;
            case ScreenId.CONTACT_DELETE:
                return -1;
            case ScreenId.GROUP_MOVE:
                return handleSearchAction(obj);
            case ScreenId.CONTACT_MENU:
                return ContactListManager.handleContactMenuAction(title, action);
            case ScreenId.CONTACT_INFO_VIEW:
                return ((ContactInfo) Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_1)).isMrimContact() ? ScreenId.VCARD_ACTIONS : ((ContactInfo) Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_1)).isXmppContact() ? -1 : Storage.state().getInt(UIKeys.INT_CURRENT_SCREEN_ID);
            case ScreenId.CONTACT_INFO_DETAIL:
                return ((ContactInfo) Storage.state().getObject(ContactKeys.SLOT_CONTACT_INFO)).isMrimContact() ? ScreenId.VCARD_ACTIONS : Storage.state().getInt(UIKeys.INT_CURRENT_SCREEN_ID);
            case ScreenId.CONTACT_LIST_KEY:
                return handleContactListKey();
            case ScreenId.BLOCK_CONTACT_LIST:
                Contact selectedContact = (Contact) obj;
                int blockResult;
                return (null == selectedContact || 0 == (blockResult = selectedContact.validateBlock())) ? 0 : NotificationHelper.showError(blockResult);
            case ScreenId.UNBLOCK_CONTACT_LIST:
                Contact contactToDelete = (Contact) obj;
                int unblockResult;
                return (null == contactToDelete || 0 == (unblockResult = contactToDelete.validateUnblock())) ? 0 : NotificationHelper.showError(unblockResult);
            case ScreenId.DELETE_CONTACT_LIST:
                Contact contactToBlock = (Contact) obj;
                int deleteResult;
                return (null == contactToBlock || 0 == (deleteResult = contactToBlock.validateDelete())) ? 0 : NotificationHelper.showError(deleteResult);
            case ScreenId.GROUP_MEMBERS:
                int errorCode;
                if (obj == null) {
                    errorCode = -1;
                } else if (obj instanceof String) {
                    Storage.state().setObject(ContactKeys.SLOT_CONTACT_INFO, ContactInfo.createForAccount(Storage.state().getCurrentContact().account).setEmailAddress((String) obj).setDisplayName((String) obj));
                    errorCode = 66;
                } else {
                    errorCode = NotificationHelper.showError(773);
                }
                return errorCode;
            case ScreenId.EDIT_MEMBERS:
                Vector checkedItems = ContactListManager.getCheckedItems(currentScreen, 0);
                int errorCode3;
                if (checkedItems.size() == 0) {
                    errorCode3 = NotificationHelper.showError(775);
                } else {
                    MrimContact mrimContact2 = (MrimContact) Storage.state().getCurrentContact();
                    MrimAccount mrimAccount4 = (MrimAccount) mrimContact2.account;
                    ByteBuffer buffer = new ByteBuffer();
                    int size = checkedItems.size();
                    int i6 = size;
                    ByteBuffer membersBuf = buffer.writeIntLE(size);
                    while (true) {
                        i6--;
                        if (i6 < 0) {
                            int sendResult4 = mrimAccount4.trySendData(mrimAccount4.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount4, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(4194304).writeStringLatin1(mrimContact2.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeBufferIntLen(new ByteBuffer().writeIntLE(3).writeBufferIntLen(membersBuf))), ObjectPool.integerOf(MrimAccount.RESP_AUTH), mrimContact2, new Long(2L)}));
                            errorCode3 = 0 != sendResult4 ? NotificationHelper.showError(sendResult4) : 0;
                        } else {
                            membersBuf.writeStringLatin1((String) checkedItems.elementAt(i6));
                        }
                    }
                }
                return errorCode3;
            case ScreenId.CONTACT_DELETE_MRIM:
                return -1;
            case ScreenId.GROUP_MANAGEMENT:
                return AccountManager.handleGroupRename(action);
            case ScreenId.CONTACT_MODIFY:
                return -1;
            case ScreenId.VISIBLE_CONTACTS:
                Vector contactIds = ServiceRegistry.getAllContactIds();
                StringBuffer sb = ObjectPool.newStringBuffer();
                Vector vector = currentScreen.menuItems;
                int size3 = vector.size();
                for (int i7 = 0; i7 < size3; i7++) {
                    if (!((Boolean) ((MenuItem) vector.elementAt(i7)).data).booleanValue()) {
                        sb.append(contactIds.elementAt(i7)).append((char) 0);
                    }
                }
                String hiddenStr = ObjectPool.toStringAndRelease(sb);
                ServiceRegistry.hiddenContacts = Utils.split(hiddenStr, (char) 0);
                Storage.state().setObject(ContactKeys.HIDDEN_CONTACTS_LIST, (Object) hiddenStr);
                return 0;
            default:
                return 0;
        }
    }

    public int onMenuItemAction(ListView currentScreen, MenuItem menuItem, Object obj) {
        switch (currentScreen.screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.onContactAction(obj);
            case ScreenId.CONTACT_EDITOR:
                return 0;
            case ScreenId.ADD_CONTACT:
                return 0;
            case ScreenId.ADD_MRIM_CONTACT:
                return 0;
            case ScreenId.CONTACT_GROUP_MENU:
                return 0;
            case ScreenId.CONTACT_GROUPS:
                return 0;
            case ScreenId.CONTACT_SETTINGS:
                return 0;
            case ScreenId.GROUP_SELECTOR:
                return 0;
            case ScreenId.PHONE_GROUPS:
                return 0;
            case ScreenId.ADD_CONTACT_INFO:
                return 0;
            case ScreenId.CREATE_GROUP:
                return 0;
            case ScreenId.RENAME_GROUP:
                return 0;
            case ScreenId.DELETE_ENTITY:
                return 0;
            case ScreenId.BATCH_DELETE:
                return 0;
            case ScreenId.CONTACT_DELETE:
                AppController.clearPendingProfile();
                return 0;
            case ScreenId.GROUP_MOVE:
                return 0;
            case ScreenId.CONTACT_MENU:
                return 0;
            case ScreenId.CONTACT_INFO_VIEW:
                AppController.clearPendingProfile();
                return 0;
            case ScreenId.CONTACT_INFO_DETAIL:
                return 0;
            case ScreenId.CONTACT_LIST_KEY:
                return 0;
            case ScreenId.BLOCK_CONTACT_LIST:
                return 0;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                return 0;
            case ScreenId.DELETE_CONTACT_LIST:
                return 0;
            case ScreenId.GROUP_MEMBERS:
                return 0;
            case ScreenId.EDIT_MEMBERS:
                return 0;
            case ScreenId.CONTACT_DELETE_MRIM:
                return 0;
            case ScreenId.GROUP_MANAGEMENT:
                return 0;
            case ScreenId.CONTACT_MODIFY:
                return ScreenId.CLOSE;
            case ScreenId.VISIBLE_CONTACTS:
                return 0;
            default:
                return 0;
        }
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.CONTACT_EDITOR:
                ScreenManager.clearFormFields();
                break;
            case ScreenId.ADD_CONTACT:
                int accountType = Storage.state().getAccount().getType();
                if (accountType != 0 && accountType == 1) {
                    Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_FIELD_1);
                    Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_FIELD_2);
                    Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_FIELD_3);
                    Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_FIELD_4);
                    Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_FIELD_5);
                    Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_FIELD_6);
                    Storage.state().setInt(RegistrationKeys.FLAG_REG_SMS_MODE, 0);
                } else {
                    StringUtils.resetRegForm();
                }
                break;
            case ScreenId.ADD_MRIM_CONTACT:
                ScreenManager.clearFormFields();
                break;
            case ScreenId.ADD_CONTACT_INFO:
                Storage.state().clearIndex(ContactKeys.SLOT_CONTACT_INFO);
                Storage.state().clearRange(ContactKeys.SLOT_GROUP_ADD_NAME, ContactKeys.VEC_GROUP_LIST);
                break;
            case ScreenId.CREATE_GROUP:
                Storage.state().clearIndex(ContactKeys.SLOT_NEW_GROUP_NAME);
                break;
            case ScreenId.RENAME_GROUP:
                Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_RESULT);
                break;
            case ScreenId.BATCH_DELETE:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CONTACT_DELETE:
                if (Storage.state().getCurrentContact() != null) {
                    Storage.state().getCurrentContact().mo148L();
                }
                break;
            case ScreenId.CONTACT_INFO_VIEW:
                Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_1);
                break;
            case ScreenId.CONTACT_LIST_KEY:
                AccountManager.clearAllHighlights();
                Storage.state().clearIndex(SessionKeys.SLOT_TEMP_ACCOUNT);
                break;
            case ScreenId.GROUP_MEMBERS:
                Storage.state().clearIndex(UIKeys.OBJ_PHOTO_CACHE_1);
                break;
        }
        clearScreenFlags();
    }

    private static void clearScreenFlags() {
        Storage.state().clearRange(ContactKeys.SCREEN_FLAGS_START, ContactKeys.SCREEN_FLAGS_END);
        Storage.state().setInt(ContactKeys.FLAG_CONTACT_MENU_MODE, 0);
    }

    public int onItemSelected(ListView screen, MenuItem menuItem, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.onContactSelected(title, data);
            case ScreenId.CONTACT_EDITOR:
                return 0;
            case ScreenId.ADD_CONTACT:
                return 0;
            case ScreenId.ADD_MRIM_CONTACT:
                return 0;
            case ScreenId.CONTACT_GROUP_MENU:
                return ContactListManager.handleContactGroupAction(title, selectedOption);
            case ScreenId.CONTACT_GROUPS:
                return 0;
            case ScreenId.CONTACT_SETTINGS:
                return 0;
            case ScreenId.GROUP_SELECTOR:
                return AccountManager.handleGroupSelection(selectedOption);
            case ScreenId.PHONE_GROUPS:
                return 0;
            case ScreenId.ADD_CONTACT_INFO:
                return 0;
            case ScreenId.CREATE_GROUP:
                return 0;
            case ScreenId.RENAME_GROUP:
                return 0;
            case ScreenId.DELETE_ENTITY:
                return deleteSelectedEntity();
            case ScreenId.BATCH_DELETE:
                return -1;
            case ScreenId.CONTACT_DELETE:
                return -1;
            case ScreenId.GROUP_MOVE:
                return handleSearchAction(data);
            case ScreenId.CONTACT_MENU:
                return ContactListManager.handleContactMenuAction(title, selectedOption);
            case ScreenId.CONTACT_INFO_VIEW:
                return 0;
            case ScreenId.CONTACT_INFO_DETAIL:
                return 0;
            case ScreenId.CONTACT_LIST_KEY:
                return handleContactListKey();
            case ScreenId.BLOCK_CONTACT_LIST:
                return -1;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                return -1;
            case ScreenId.DELETE_CONTACT_LIST:
                return -1;
            case ScreenId.GROUP_MEMBERS:
                Storage.state().setObject(UIKeys.OBJ_PHOTO_CACHE_1, data);
                return data != null ? 0 : -1;
            case ScreenId.EDIT_MEMBERS:
                return 0;
            case ScreenId.CONTACT_DELETE_MRIM:
                return -1;
            case ScreenId.GROUP_MANAGEMENT:
                return AccountManager.handleGroupRename(selectedOption);
            case ScreenId.CONTACT_MODIFY:
                return -1;
            case ScreenId.VISIBLE_CONTACTS:
                return 0;
            default:
                return 0;
        }
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.updateContextMenu(screen, data);
            case ScreenId.CONTACT_EDITOR:
                return 0;
            case ScreenId.ADD_CONTACT:
                return 0;
            case ScreenId.ADD_MRIM_CONTACT:
                return 0;
            case ScreenId.CONTACT_GROUP_MENU:
                return 0;
            case ScreenId.CONTACT_GROUPS:
                return 0;
            case ScreenId.CONTACT_SETTINGS:
                return 0;
            case ScreenId.GROUP_SELECTOR:
                return 0;
            case ScreenId.PHONE_GROUPS:
                TextBox textBox;
                if (TimerManager.checkTimer(9, 3000L) && (textBox = XmppContactGroup.getTextInputBox()) != null) {
                    String inputText = StringUtils.getTextBoxString(textBox);
                    if (Storage.state().getBool(SettingsKeys.SETTING_AUTO_RECONNECT)) {
                        String transliterated = Conversation.transliterateRussian(inputText);
                        if (!StringUtils.equals(transliterated, inputText)) {
                            textBox.setString(transliterated);
                        }
                    } else {
                        int length = inputText.length();
                        int i7 = length;
                        int i8 = length;
                        while (true) {
                            i8--;
                            if (i8 < 0) {
                                int i9 = i7 - 160;
                                if (i9 > 0) {
                                    textBox.setString(StringUtils.prefix(inputText, inputText.length() - i9));
                                }
                                break;
                            } else {
                                char ch = inputText.charAt(i8);
                                if ((ch >= 1040 && ch <= 1071) || ((ch >= 1072 && ch <= 1103) || ch == 1105 || ch == 1025)) {
                                    i7 += 2;
                                }
                            }
                        }
                    }
                }
                return 0;
            case ScreenId.ADD_CONTACT_INFO:
                return 0;
            case ScreenId.CREATE_GROUP:
                return 0;
            case ScreenId.RENAME_GROUP:
                return 0;
            case ScreenId.DELETE_ENTITY:
                return 0;
            case ScreenId.BATCH_DELETE:
                Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (asyncResult != null) {
                    int responseCode = ApiClient.validateJsonResponse(asyncResult);
                    if (responseCode != 0) {
                        return responseCode;
                    } else {
                        Object payload = ApiClient.getJsonPayload();
                        int size = ((Vector) payload).size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                return ScreenId.CHAT_ROOM_VIEW;
                            } else {
                                Object jsonObj = JsonParser.getVectorElement(payload, size);
                                int parsedInt = Utils.parseInt((Object) JsonParser.getStringByInt(jsonObj, 263673));
                                String jsonStr = JsonParser.getStringByInt(jsonObj, 329240);
                                ChatRoom selectedChatRoom = ((MrimAccount) Storage.state().getAccount()).chatRoomManager.findByName(jsonStr);
                                Message message = selectedChatRoom.getMessage(jsonStr);
                                if (selectedChatRoom != null) {
                                    selectedChatRoom.markMessageRead(jsonStr);
                                }
                                if (parsedInt == 1) {
                                    if (message != null && !message.hasFlag(4)) {
                                        message.setFlag(4, true);
                                        selectedChatRoom.incrementUnread();
                                    }
                                } else if (message != null && message.hasFlag(4)) {
                                    message.setFlag(4, false);
                                    selectedChatRoom.decrementUnread();
                                }
                            }
                        }
                    }
                }
                return 0;
            case ScreenId.CONTACT_DELETE:
                return Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_1) == null ? 0 : ScreenId.CONTACT_INFO_VIEW;
            case ScreenId.GROUP_MOVE:
                return 0;
            case ScreenId.CONTACT_MENU:
                return 0;
            case ScreenId.CONTACT_INFO_VIEW:
                return 0;
            case ScreenId.CONTACT_INFO_DETAIL:
                return 0;
            case ScreenId.CONTACT_LIST_KEY:
                return 0;
            case ScreenId.BLOCK_CONTACT_LIST:
                return 0;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                return 0;
            case ScreenId.DELETE_CONTACT_LIST:
                return 0;
            case ScreenId.GROUP_MEMBERS:
                return 0;
            case ScreenId.EDIT_MEMBERS:
                return 0;
            case ScreenId.CONTACT_DELETE_MRIM:
                return Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_1) == null ? 0 : ScreenId.CONTACT_INFO_VIEW;
            case ScreenId.GROUP_MANAGEMENT:
                return 0;
            case ScreenId.CONTACT_MODIFY:
                return Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_4) == null ? 0 : ScreenId.GROUP_MEMBERS;
            case ScreenId.VISIBLE_CONTACTS:
                return 0;
            default:
                return 0;
        }
    }

    /* renamed from: q */
    public static final int deleteSelectedEntity() {
        int groupError;
        Object obj = Storage.state().getObject(ContactKeys.SLOT_CURRENT_ENTITY);
        if ((obj instanceof ContactGroup) && 0 != (groupError = ((ContactGroup) obj).getSortIndex())) {
            return NotificationHelper.showError(groupError);
        }
        if (!(obj instanceof Contact)) {
            return ScreenId.CONTACT_LIST;
        }
        Contact selectedContact = (Contact) obj;
        int contactError = selectedContact.account.validateResend(selectedContact);
        if (0 != contactError) {
            return NotificationHelper.showError(contactError);
        }
        return ScreenId.CONTACT_LIST;
    }

    public static int handleSearchAction(Object groupObj) {
        ContactGroup group = (ContactGroup) groupObj;
        if (group == null) {
            return ScreenId.CONTACT_LIST;
        }
        Contact contact = Storage.state().getCurrentContact();
        int errorCode = contact.isOnline() ? 310 : contact.account.validateMove(contact, contact.account.findGroup(contact), group);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    public static int handleContactListKey() {
        MrimAccount account = (MrimAccount) Storage.state().getObject(SessionKeys.SLOT_TEMP_ACCOUNT);
        AccountHandler.showMailAccountList();
        Storage.state().setAccount(account);
        Storage.state().setInt(UIKeys.INT_SCREEN_ACTION, 38);
        return ScreenId.CHAT_ROOMS;
    }

    public void onMenuItemEvent(ListView screen, MenuItem item) {
        if (screen.screenId == ScreenId.ADD_CONTACT) {
            if (Storage.state().getAccount().getType() == Account.TYPE_MRIM) {
                StringUtils.updateRegDropdowns(screen, item);
            }
        }
    }
}
