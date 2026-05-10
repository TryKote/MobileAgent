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

    // Cyrillic Unicode ranges
    private static final char CYRILLIC_UPPER_START = '\u0410'; // А (1040)
    private static final char CYRILLIC_UPPER_END = '\u042F';   // Я (1071)
    private static final char CYRILLIC_LOWER_START = '\u0430'; // а (1072)
    private static final char CYRILLIC_LOWER_END = '\u044F';   // я (1103)
    private static final char CYRILLIC_IO_UPPER = '\u0401';    // Ё (1025)
    private static final char CYRILLIC_IO_LOWER = '\u0451';    // ё (1105)

    // SMS encoding: each Cyrillic char takes 2 extra bytes in UCS-2
    private static final int CYRILLIC_EXTRA_BYTES = 2;
    private static final int SMS_CHAR_LIMIT = 160;

    // Timer for phone input validation
    private static final long PHONE_INPUT_CHECK_INTERVAL_MS = 3000L;

    // Contact name parts (first, middle, last)
    private static final int NAME_PARTS_COUNT = 3;

    // INT_CONTACT_TYPE_CODE values (used by cfg conditional items)
    private static final int CONTACT_TYPE_BASIC = 1;
    private static final int CONTACT_TYPE_MRIM = 4;

    // Menu type values for INT_OK/CANCEL_MENU_TYPE
    private static final int MENU_TYPE_CONTACT_MENU = 3;
    private static final int MENU_TYPE_CONTACT_GROUP = 4;

    // Packed string suffix for confirmation dialogs ("?")
    private static final int PACKED_CONFIRM_SUFFIX = 16167;

    // ContactInfo error notification key
    private static final int CONTACT_INFO_ERROR_KEY = -1;

    // INT_INFO_SCREEN_MODE value for MRIM contacts
    private static final int INFO_MODE_MRIM = 503;

    // JSON keys for batch message operations
    private static final int JSON_KEY_MSG_ID = 329240;
    private static final int JSON_KEY_MARK_ACTION = 263673;

    // Message flag for starred/flagged state
    private static final int MSG_FLAG_STARRED = 4;

    // Batch mark action: mark as read
    private static final int MARK_ACTION_READ = 1;

    // MRIM contact flags for phone contact creation
    private static final int MRIM_FLAG_PHONE_CONTACT = 1048576;

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                ContactListManager.showContactList();
                return;
            case ScreenId.CONTACT_EDITOR:
                ScreenManager.clearFormFields();
                Contact editorContact = Storage.state().getCurrentContact();
                Storage.state().setObject(UIKeys.SLOT_INPUT_TEXT, editorContact.displayName);
                Vector nameParts = Utils.splitNonEmpty(editorContact.getDefaultName(), ',');
                for (int i = 0; i < NAME_PARTS_COUNT; i++) {
                    if (i < nameParts.size()) {
                        Storage.state().setObject(UIKeys.CONTACT_NAME_PARTS_BASE + i, nameParts.elementAt(i));
                    }
                }
                ObjectPool.releaseVector(nameParts);
                Storage.state().setInt(ContactKeys.INT_CONTACT_TYPE_CODE, (!(editorContact instanceof MrimContact) || editorContact.isSystem()) ? CONTACT_TYPE_BASIC : CONTACT_TYPE_MRIM);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_EDITOR));
                return;
            case ScreenId.ADD_CONTACT:
                int accountType = Storage.state().getAccount().getType();
                if (accountType == Account.TYPE_MRIM) {
                    StringUtils.showRegionSelector();
                    return;
                }
                if (accountType == Account.TYPE_MMP) {
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
                Object entity = Storage.state().getObject(ContactKeys.SLOT_CURRENT_ENTITY);
                if (entity instanceof ContactGroup) {
                    Storage.state().setInt(ContactKeys.FLAG_IS_MRIM_CONTACT, 1);
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_GROUP_MENU));
                    return;
                }
                Contact contact = (Contact) entity;
                if (contact.isSystem()) {
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_ACTION, ScreenId.CONTACT_GROUP_MENU);
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_TYPE, MENU_TYPE_CONTACT_GROUP);
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_MENU));
                    return;
                }
                setupContactMenuFlags(contact);
                Storage.state().setInt(ContactKeys.FLAG_IS_MRIM_CONTACT, 0);
                Storage.state().setInt(UIKeys.INT_OK_MENU_TYPE, MENU_TYPE_CONTACT_GROUP);
                Storage.state().setInt(UIKeys.INT_OK_MENU_ACTION, ScreenId.CONTACT_GROUP_MENU);
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
                Storage.state().setObject(RegistrationKeys.SLOT_SEARCH_RESULT, Storage.state().getCurrentGroup().name);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.RENAME_GROUP));
                return;
            case ScreenId.DELETE_ENTITY:
                StringBuffer alertBuffer = ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ALERT_PREFIX));
                Object deleteTarget = Storage.state().getObject(ContactKeys.SLOT_CURRENT_ENTITY);
                NotificationHelper.showAlertBuffer(71, alertBuffer.append(deleteTarget instanceof ContactGroup ? ((ContactGroup) deleteTarget).name : ((Contact) deleteTarget).displayName).append(ObjectPool.unpackChars(PACKED_CONFIRM_SUFFIX)));
                return;
            case ScreenId.BATCH_DELETE:
                NotificationHelper.showConfirmDialog(72, 866);
                Vector selectedItems = Storage.state().getVector(UIKeys.SLOT_MEDIA_STREAM);
                Vector itemsParams = ObjectPool.newVector();
                for (int idx = selectedItems.size() - 1; idx >= 0; idx--) {
                    Hashtable hashtable = new Hashtable();
                    JsonParser.putIntKey(hashtable, JSON_KEY_MSG_ID, JsonParser.getVectorElement(selectedItems, idx));
                    JsonParser.putIntKey(hashtable, JSON_KEY_MARK_ACTION, ObjectPool.integerOf(Storage.state().getInt(ChatKeys.INT_CHAT_VIEW_MODE)));
                    itemsParams.addElement(hashtable);
                }
                Vector outerParams = ObjectPool.newVector();
                outerParams.addElement(itemsParams);
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(Storage.resources().getString(PackedStringKeys.URL_PATH_AJAX_MARKMSG), ObjectPool.newStringBuffer().append(Storage.resources().getString(PackedStringKeys.PARAM_AJAX_CALL)).append(Storage.resources().getString(PackedStringKeys.FUNC_AJAX_MARK_MSG)).append(Storage.state().getString(SessionKeys.SLOT_SESSION_HASH)).append(Storage.resources().getString(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(outerParams)))));
                return;
            case ScreenId.CONTACT_DELETE:
                Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_1);
                Contact deleteContact = Storage.state().getCurrentContact();
                NotificationHelper.showErrorOrConfirm(85, 727, deleteContact == null ? 0 : deleteContact.account.validateDelete(deleteContact));
                return;
            case ScreenId.GROUP_MOVE:
                ListView groupScreen = ScreenManager.createScreen(ScreenDef.GROUP_MOVE);
                Contact moveContact = Storage.state().getCurrentContact();
                Vector groups = moveContact.account.groups;
                int groupCount = groups.size();
                for (int gi = 0; gi < groupCount; gi++) {
                    ContactGroup group = (ContactGroup) groups.elementAt(gi);
                    groupScreen.addItem(group.createMenuItem(-1));
                    if (group.containsContact(moveContact)) {
                        groupScreen.selectedIndex = gi;
                    }
                }
                ScreenManager.showScreen(groupScreen);
                return;
            case ScreenId.CONTACT_MENU:
                Storage.state().setInt(ContactKeys.FLAG_CONTACT_MENU_MODE, 0);
                Contact menuContact = Storage.state().getCurrentContact();
                if (menuContact.isSystem()) {
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_ACTION, ScreenId.CONTACT_MENU);
                    Storage.state().setInt(UIKeys.INT_CANCEL_MENU_TYPE, MENU_TYPE_CONTACT_MENU);
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_MENU));
                    return;
                }
                setupContactMenuFlags(menuContact);
                Storage.state().setInt(UIKeys.INT_OK_MENU_TYPE, MENU_TYPE_CONTACT_MENU);
                Storage.state().setInt(UIKeys.INT_OK_MENU_ACTION, ScreenId.CONTACT_MENU);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.CONTACT_ACTIONS_MENU));
                return;
            case ScreenId.CONTACT_INFO_VIEW:
                ContactInfo contactInfo = (ContactInfo) Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_1);
                String errorMessage = (String) contactInfo.get(ObjectPool.integerOf(CONTACT_INFO_ERROR_KEY));
                if (errorMessage != null) {
                    NotificationHelper.showNotification(errorMessage);
                } else {
                    Storage.state().setInt(RuntimeKeys.INT_INFO_SCREEN_MODE, contactInfo.isXmppContact() ? 0 : INFO_MODE_MRIM);
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
                showFilteredContactList(ScreenDef.BLOCK_CONTACT_LIST, 0);
                return;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                showFilteredContactList(ScreenDef.UNBLOCK_CONTACT_LIST, 1);
                return;
            case ScreenId.DELETE_CONTACT_LIST:
                showFilteredContactList(ScreenDef.DELETE_CONTACT_LIST, 2);
                return;
            case ScreenId.GROUP_MEMBERS:
                ListView membersScreen = ScreenManager.createScreen(ScreenDef.GROUP_MEMBERS);
                MrimContact mrimContact = (MrimContact) Storage.state().getCurrentContact();
                MrimAccount mrimAccount = (MrimAccount) mrimContact.account;
                Vector groupMembers = Storage.state().getVector(RegistrationKeys.SLOT_REG_PARAM_4);
                mrimContact.setGroupsList(groupMembers);
                for (int mi = 0; mi < groupMembers.size(); mi++) {
                    String memberLogin = Utils.getVectorString(groupMembers, mi);
                    if (!StringUtils.equals(memberLogin, mrimAccount.login)) {
                        MrimContact member = (MrimContact) mrimAccount.getContact(memberLogin);
                        if (member != null) {
                            membersScreen.addItem(member.createMenuItem());
                        } else {
                            membersScreen.addIconItemWithData(154, memberLogin, 0, memberLogin);
                        }
                    }
                }
                if (membersScreen.menuItems.size() == 0) {
                    membersScreen.selectable = false;
                    ListView labelScreen = membersScreen.addLabelById(772);
                    labelScreen.setSoftKeys(Storage.resources().getString(StringResKeys.STR_EMPTY), Storage.resources().getString(StringResKeys.STR_SOFTKEY_NO), labelScreen.softKeyLeft, labelScreen.softKeyCenter, labelScreen.softKeyRight);
                }
                ScreenManager.showScreen(membersScreen);
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
                MrimContact modifyContact = (MrimContact) Storage.state().getCurrentContact();
                MrimAccount modifyAccount = (MrimAccount) modifyContact.account;
                NotificationHelper.showErrorOrConfirm(150, 504, modifyAccount.trySendData(modifyAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(modifyAccount, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(Conversation.FLAG_EXTENDED).writeStringLatin1(modifyContact.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeIntLE(4).writeIntLE(1)), ObjectPool.integerOf(MrimAccount.RESP_AUTH), modifyContact, new Long(1L)})));
                return;
            case ScreenId.VISIBLE_CONTACTS:
                Vector contactIds = ServiceRegistry.getAllContactIds();
                int contactCount = Utils.vectorSize(contactIds);
                if (contactCount == 0) {
                    NotificationHelper.showMessageById(404);
                    return;
                }
                ListView visibleScreen = ScreenManager.createScreen(ScreenDef.GROUP_MANAGEMENT_ALT);
                for (int ci = 0; ci < contactCount; ci++) {
                    Object contactId = contactIds.elementAt(ci);
                    visibleScreen.addItem(MenuItem.createCheckbox(ServiceRegistry.getPhotoHost(contactId), !ServiceRegistry.hiddenContacts.contains(contactId)));
                }
                ScreenManager.showScreen(visibleScreen);
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
                Object[] contactData = new Object[phoneNumbers.length + 1];
                contactData[0] = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_INPUT_TEXT));
                for (int pi = 0; pi < phoneNumbers.length; pi++) {
                    contactData[pi + 1] = phoneNumbers[pi];
                }
                Contact editContact = Storage.state().getCurrentContact();
                int modifyResult;
                if (editContact.isOnline()) {
                    editContact.setDisplayName((String) contactData[0]);
                    AppController.needsLayoutUpdate = true;
                    modifyResult = 0;
                } else {
                    modifyResult = editContact.account.validateModify(editContact, contactData);
                }
                return modifyResult != 0 ? NotificationHelper.showError(modifyResult) : 0;
            case ScreenId.ADD_CONTACT:
                ScreenManager.processScreenForm();
                return Storage.state().getAccount() instanceof XmppProtocol ? ((XmppProtocol) Storage.state().getAccount()).addNewContact() : 0;
            case ScreenId.ADD_MRIM_CONTACT:
                ScreenManager.processScreenForm();
                MrimAccount addAccount = (MrimAccount) Storage.state().getAccount();
                String displayName = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_INPUT_TEXT));
                String[] addPhones = Utils.getPhoneNumbers(false);
                int sendResult;
                if (!addAccount.isConnected()) {
                    sendResult = 299;
                } else if (Utils.nonEmpty(displayName)) {
                    int phoneCount = addPhones.length;
                    if (phoneCount == 0) {
                        sendResult = 709;
                    } else {
                        boolean duplicateFound = false;
                        Enumeration contactEnum = addAccount.contactMap.elements();
                        while (!duplicateFound && contactEnum.hasMoreElements()) {
                            MrimContact existing = (MrimContact) contactEnum.nextElement();
                            for (int pi = phoneCount - 1; pi >= 0; pi--) {
                                if (existing.isInGroup(addPhones[pi])) {
                                    duplicateFound = true;
                                    break;
                                }
                            }
                        }
                        if (duplicateFound) {
                            sendResult = 486;
                        } else {
                            MrimContactGroup contactGroup = addAccount.getFirstContactGroup();
                            ByteBuffer packetBuf = new ByteBuffer().writeIntLE(MRIM_FLAG_PHONE_CONTACT).writeIntLE(103).writeStringLatin1(Storage.resources().getString(StringResKeys.STR_PHONE_SUFFIX)).writeStringUTF16(displayName);
                            String emailsJoined = Utils.joinComma(addPhones);
                            sendResult = addAccount.trySendData(addAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(addAccount, MrimCommand.CS_ADD_CONTACT, packetBuf.writeStringLatin1(emailsJoined).writeZeros(8)), ObjectPool.integerOf(MrimAccount.RESP_ADD_PHONE_CONTACT), displayName, emailsJoined, contactGroup}));
                        }
                    }
                } else {
                    sendResult = 708;
                }
                return sendResult != 0 ? NotificationHelper.showError(sendResult) : 0;
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
                return addResult != 0 ? NotificationHelper.showError(addResult) : 0;
            case ScreenId.CREATE_GROUP:
                ScreenManager.processScreenForm();
                int createResult = Storage.state().getAccount().validateGroupCreate(Utils.defaultStr(Storage.state().getString(ContactKeys.SLOT_NEW_GROUP_NAME)));
                return createResult != 0 ? NotificationHelper.showError(createResult) : 0;
            case ScreenId.RENAME_GROUP:
                ScreenManager.processScreenForm();
                int renameResult = Storage.state().getCurrentGroup().rename(Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_RESULT)));
                return renameResult != 0 ? NotificationHelper.showError(renameResult) : 0;
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
                Contact blockContact = (Contact) obj;
                int blockResult;
                return (blockContact == null || (blockResult = blockContact.validateBlock()) == 0) ? 0 : NotificationHelper.showError(blockResult);
            case ScreenId.UNBLOCK_CONTACT_LIST:
                Contact unblockContact = (Contact) obj;
                int unblockResult;
                return (unblockContact == null || (unblockResult = unblockContact.validateUnblock()) == 0) ? 0 : NotificationHelper.showError(unblockResult);
            case ScreenId.DELETE_CONTACT_LIST:
                Contact deleteListContact = (Contact) obj;
                int deleteResult;
                return (deleteListContact == null || (deleteResult = deleteListContact.validateDelete()) == 0) ? 0 : NotificationHelper.showError(deleteResult);
            case ScreenId.GROUP_MEMBERS:
                if (obj == null) {
                    return -1;
                }
                if (obj instanceof String) {
                    Storage.state().setObject(ContactKeys.SLOT_CONTACT_INFO, ContactInfo.createForAccount(Storage.state().getCurrentContact().account).setEmailAddress((String) obj).setDisplayName((String) obj));
                    return ScreenId.ADD_CONTACT_INFO;
                }
                return NotificationHelper.showError(773);
            case ScreenId.EDIT_MEMBERS:
                Vector checkedItems = ContactListManager.getCheckedItems(currentScreen, 0);
                int editResult;
                if (checkedItems.size() == 0) {
                    editResult = NotificationHelper.showError(775);
                } else {
                    MrimContact editMember = (MrimContact) Storage.state().getCurrentContact();
                    MrimAccount editAccount = (MrimAccount) editMember.account;
                    ByteBuffer buffer = new ByteBuffer();
                    int memberCount = checkedItems.size();
                    ByteBuffer membersBuf = buffer.writeIntLE(memberCount);
                    for (int mi = memberCount - 1; mi >= 0; mi--) {
                        membersBuf.writeStringLatin1((String) checkedItems.elementAt(mi));
                    }
                    int editSendResult = editAccount.trySendData(editAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(editAccount, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(Conversation.FLAG_EXTENDED).writeStringLatin1(editMember.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeBufferIntLen(new ByteBuffer().writeIntLE(3).writeBufferIntLen(membersBuf))), ObjectPool.integerOf(MrimAccount.RESP_AUTH), editMember, new Long(2L)}));
                    editResult = editSendResult != 0 ? NotificationHelper.showError(editSendResult) : 0;
                }
                return editResult;
            case ScreenId.CONTACT_DELETE_MRIM:
                return -1;
            case ScreenId.GROUP_MANAGEMENT:
                return AccountManager.handleGroupRename(action);
            case ScreenId.CONTACT_MODIFY:
                return -1;
            case ScreenId.VISIBLE_CONTACTS:
                Vector contactIds = ServiceRegistry.getAllContactIds();
                StringBuffer sb = ObjectPool.newStringBuffer();
                Vector items = currentScreen.menuItems;
                int itemCount = items.size();
                for (int vi = 0; vi < itemCount; vi++) {
                    if (!((Boolean) ((MenuItem) items.elementAt(vi)).data).booleanValue()) {
                        sb.append(contactIds.elementAt(vi)).append((char) 0);
                    }
                }
                String hiddenStr = ObjectPool.toStringAndRelease(sb);
                ServiceRegistry.hiddenContacts = Utils.split(hiddenStr, (char) 0);
                Storage.state().setObject(ContactKeys.HIDDEN_CONTACTS_LIST, hiddenStr);
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
                if (Storage.state().getAccount().getType() == Account.TYPE_MMP) {
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
                    Storage.state().getCurrentContact().clearRegistrationData();
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

    public void onScreenResumed(ListView screen, int closedScreenId) {
        if (screen.screenId == ScreenId.CONTACT_LIST) {
            AppController.needsLayoutUpdate = true;
        }
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
                if (TimerManager.checkTimer(TimerManager.SLOT_PHONE_INPUT_CHECK, PHONE_INPUT_CHECK_INTERVAL_MS) && (textBox = XmppContactGroup.getTextInputBox()) != null) {
                    String inputText = StringUtils.getTextBoxString(textBox);
                    if (Storage.state().getBool(SettingsKeys.SETTING_AUTO_RECONNECT)) {
                        String transliterated = Conversation.transliterateRussian(inputText);
                        if (!StringUtils.equals(transliterated, inputText)) {
                            textBox.setString(transliterated);
                        }
                    } else {
                        int length = inputText.length();
                        int encodedLength = length;
                        for (int ci = length - 1; ci >= 0; ci--) {
                            char ch = inputText.charAt(ci);
                            if ((ch >= CYRILLIC_UPPER_START && ch <= CYRILLIC_UPPER_END)
                                    || ((ch >= CYRILLIC_LOWER_START && ch <= CYRILLIC_LOWER_END)
                                        || ch == CYRILLIC_IO_LOWER || ch == CYRILLIC_IO_UPPER)) {
                                encodedLength += CYRILLIC_EXTRA_BYTES;
                            }
                        }
                        int overflow = encodedLength - SMS_CHAR_LIMIT;
                        if (overflow > 0) {
                            textBox.setString(StringUtils.prefix(inputText, inputText.length() - overflow));
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
                        for (int idx = ((Vector) payload).size() - 1; idx >= 0; idx--) {
                            Object entry = JsonParser.getVectorElement(payload, idx);
                            int markAction = Utils.parseInt(JsonParser.getStringByInt(entry, JSON_KEY_MARK_ACTION));
                            String msgId = JsonParser.getStringByInt(entry, JSON_KEY_MSG_ID);
                            ChatRoom chatRoom = ((MrimAccount) Storage.state().getAccount()).chatRoomManager.findByName(msgId);
                            Message message = chatRoom.getMessage(msgId);
                            if (chatRoom != null) {
                                chatRoom.markMessageRead(msgId);
                            }
                            if (markAction == MARK_ACTION_READ) {
                                if (message != null && !message.hasFlag(MSG_FLAG_STARRED)) {
                                    message.setFlag(MSG_FLAG_STARRED, true);
                                    chatRoom.incrementUnread();
                                }
                            } else if (message != null && message.hasFlag(MSG_FLAG_STARRED)) {
                                message.setFlag(MSG_FLAG_STARRED, false);
                                chatRoom.decrementUnread();
                            }
                        }
                        return ScreenId.CHAT_ROOM_VIEW;
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

    private static void setupContactMenuFlags(Contact contact) {
        ContactListManager.updateContactFlags(contact);
        boolean isMrim = contact instanceof MrimContact;
        Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_MRIM, isMrim);
        boolean isMrimGroup = isMrim && contact.isOffline();
        Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_GROUP, isMrimGroup);
        Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_USER, isMrim && !isMrimGroup);
        Storage.state().setBool(ContactKeys.FLAG_CONTACT_IS_ONLINE, contact.isOnline());
        Storage.state().setBool(ContactKeys.FLAG_CONTACT_HAS_UNREAD, contact.hasUnread() && !contact.isOnline());
        Storage.state().setBool(ContactKeys.FLAG_CONTACT_HAS_VCARD, isMrim && !isMrimGroup && ((MrimContact) contact).hasVCard());
    }

    // filterType: 0=canDelete, 1=canBlock, 2=canUnblock
    private static void showFilteredContactList(int screenDef, int filterType) {
        Account account = Storage.state().getAccount();
        Vector filtered = ObjectPool.newVector();
        Enumeration contactEnum = account.contactMap.elements();
        while (contactEnum.hasMoreElements()) {
            Contact candidate = (Contact) contactEnum.nextElement();
            boolean matches = filterType == 0 ? candidate.canDelete()
                    : filterType == 1 ? candidate.canBlock() : candidate.canUnblock();
            if (matches) {
                filtered.addElement(candidate);
            }
        }
        if (filtered.size() > 0) {
            ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(screenDef), filtered));
        } else {
            NotificationHelper.showMessageById(762);
        }
        ObjectPool.releaseVector(filtered);
    }

    public static int deleteSelectedEntity() {
        int groupError;
        Object entity = Storage.state().getObject(ContactKeys.SLOT_CURRENT_ENTITY);
        if ((entity instanceof ContactGroup) && (groupError = ((ContactGroup) entity).getSortIndex()) != 0) {
            return NotificationHelper.showError(groupError);
        }
        if (!(entity instanceof Contact)) {
            return ScreenId.CONTACT_LIST;
        }
        Contact contact = (Contact) entity;
        int contactError = contact.account.validateResend(contact);
        if (contactError != 0) {
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
        Storage.state().setInt(UIKeys.INT_SCREEN_ACTION, ScreenId.CHAT_ROOM_INIT);
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
