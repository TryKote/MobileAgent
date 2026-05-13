package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
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
                Contact editorContact = AppState.getCurrentContact();
                UIState.setInputText(editorContact.displayName);
                Vector nameParts = Utils.splitNonEmpty(editorContact.getDefaultName(), ',');
                for (int i = 0; i < NAME_PARTS_COUNT; i++) {
                    if (i < nameParts.size()) {
                        UIState.setContactNamePart(i, nameParts.elementAt(i));
                    }
                }
                ObjectPool.releaseVector(nameParts);
                ContactState.setTypeCode((!(editorContact instanceof MrimContact) || editorContact.isSystem()) ? CONTACT_TYPE_BASIC : CONTACT_TYPE_MRIM);
                Screens.contactEditor(this).show();
                return;
            case ScreenId.ADD_CONTACT:
                int accountType = AppState.getAccount().getType();
                if (accountType == Account.TYPE_MRIM) {
                    StringUtils.showRegionSelector();
                    return;
                }
                if (accountType == Account.TYPE_MMP) {
                    RegistrationState.setRegDomainIndex(-1);
                    Screens.addContact(this).show();
                    return;
                }
                StringUtils.resetRegForm();
                if (ContactListManager.getGroupCount(AppState.getAccount()) == 0) {
                    EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_NOTIFICATION_NEW_MSG));
                    return;
                } else {
                    Screens.addContactForm(this).show();
                    return;
                }
            case ScreenId.ADD_MRIM_CONTACT:
                Screens.addMrimContact(this).show();
                return;
            case ScreenId.CONTACT_GROUP_MENU:
                ContactState.setMenuMode(true);
                Object entity = ContactState.getEntity();
                if (entity instanceof ContactGroup) {
                    ContactState.setMrimEntityFlag(true);
                    Screens.contactGroupMenu(this).show();
                    return;
                }
                Contact contact = (Contact) entity;
                if (contact.isSystem()) {
                    UIState.setCancelMenuAction(ScreenId.CONTACT_GROUP_MENU);
                    UIState.setCancelMenuType(MENU_TYPE_CONTACT_GROUP);
                    Screens.contactMenu(this).show();
                    return;
                }
                setupContactMenuFlags(contact);
                ContactState.setMrimEntityFlag(false);
                UIState.setOkMenuType(MENU_TYPE_CONTACT_GROUP);
                UIState.setOkMenuAction(ScreenId.CONTACT_GROUP_MENU);
                Screens.contactActionsMenu(this).show();
                return;
            case ScreenId.CONTACT_GROUPS:
                Screens.contactGroups(this).show();
                return;
            case ScreenId.CONTACT_SETTINGS:
                Screens.contactSettings(this).show();
                return;
            case ScreenId.GROUP_SELECTOR:
                Screens.groupSelector(this).show();
                return;
            case ScreenId.PHONE_GROUPS:
                Vector phoneGroups = Utils.splitNonEmpty(AppState.getCurrentMrimContact().contactGroupsStr, ',');
                int size = phoneGroups.size();
                if (size <= 0) {
                    NotificationHelper.showMessageById(713);
                    return;
                }
                StringBuffer sb = ObjectPool.newStringBuffer();
                for (int i = 0; i < size; i++) {
                    sb.append(Utils.formatPhone((String) phoneGroups.elementAt(i))).append((char) 0);
                }
                RegistrationState.setSearchLabel(sb);
                ContactState.setSelectedGroupIndex(0);
                Screens.phoneGroups(this).show();
                return;
            case ScreenId.ADD_CONTACT_INFO:
                ContactListManager.showAddContactScreen();
                return;
            case ScreenId.CREATE_GROUP:
                Screens.createGroup(this).show();
                return;
            case ScreenId.RENAME_GROUP:
                RegistrationState.setSearchResultName(AppState.getCurrentGroup().name);
                Screens.renameGroup(this).show();
                return;
            case ScreenId.DELETE_ENTITY:
                StringBuffer alertBuffer = ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_ALERT_PREFIX));
                Object deleteTarget = ContactState.getEntity();
                NotificationHelper.showAlertBuffer(71, alertBuffer.append(deleteTarget instanceof ContactGroup ? ((ContactGroup) deleteTarget).name : ((Contact) deleteTarget).displayName).append(ObjectPool.unpackChars(PACKED_CONFIRM_SUFFIX)));
                return;
            case ScreenId.BATCH_DELETE:
                NotificationHelper.showConfirmDialog(72, 866);
                Vector selectedItems = UIState.getMediaStream();
                Vector itemsParams = ObjectPool.newVector();
                for (int idx = selectedItems.size() - 1; idx >= 0; idx--) {
                    Hashtable hashtable = new Hashtable();
                    JsonParser.putIntKey(hashtable, JSON_KEY_MSG_ID, JsonParser.getVectorElement(selectedItems, idx));
                    JsonParser.putIntKey(hashtable, JSON_KEY_MARK_ACTION, ObjectPool.integerOf(ChatState.getChatViewMode()));
                    itemsParams.addElement(hashtable);
                }
                Vector outerParams = ObjectPool.newVector();
                outerParams.addElement(itemsParams);
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(ResourceAccessor.str(PackedStringKeys.URL_PATH_AJAX_MARKMSG), ObjectPool.newStringBuffer().append(ResourceAccessor.str(PackedStringKeys.PARAM_AJAX_CALL)).append(ResourceAccessor.str(PackedStringKeys.FUNC_AJAX_MARK_MSG)).append(SessionState.getSessionHash()).append(ResourceAccessor.str(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(outerParams)))));
                return;
            case ScreenId.CONTACT_DELETE:
                RegistrationState.clearParam1();
                Contact deleteContact = AppState.getCurrentContact();
                NotificationHelper.showErrorOrConfirm(85, 727, deleteContact == null ? 0 : deleteContact.account.validateDelete(deleteContact));
                return;
            case ScreenId.GROUP_MOVE:
                Screen groupScreen = Screens.groupMove(this);
                Contact moveContact = AppState.getCurrentContact();
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
                ContactState.setMenuMode(false);
                Contact menuContact = AppState.getCurrentContact();
                if (menuContact.isSystem()) {
                    UIState.setCancelMenuAction(ScreenId.CONTACT_MENU);
                    UIState.setCancelMenuType(MENU_TYPE_CONTACT_MENU);
                    Screens.contactMenu(this).show();
                    return;
                }
                setupContactMenuFlags(menuContact);
                UIState.setOkMenuType(MENU_TYPE_CONTACT_MENU);
                UIState.setOkMenuAction(ScreenId.CONTACT_MENU);
                Screens.contactActionsMenu(this).show();
                return;
            case ScreenId.CONTACT_INFO_VIEW:
                ContactInfo contactInfo = (ContactInfo) RegistrationState.getParam1();
                String errorMessage = (String) contactInfo.get(ObjectPool.integerOf(CONTACT_INFO_ERROR_KEY));
                if (errorMessage != null) {
                    NotificationHelper.showNotification(errorMessage);
                } else {
                    RuntimeState.setInfoScreenMode(contactInfo.isXmppContact() ? 0 : INFO_MODE_MRIM);
                    ScreenManager.showScreen(contactInfo.buildContactScreen(Screens.contactInfoViewScreen(this)));
                }
                UIState.setCurrentScreenId(ScreenId.USER_PROFILE);
                return;
            case ScreenId.CONTACT_INFO_DETAIL:
                ScreenManager.showScreen(ContactState.getInfo().buildContactScreen(Screens.contactInfoDetailScreen(this)));
                RegistrationState.clearParam1();
                UIState.setCurrentScreenId(ScreenId.PROFILE_LOAD);
                return;
            case ScreenId.CONTACT_LIST_KEY:
                return;
            case ScreenId.BLOCK_CONTACT_LIST:
                showFilteredContactList(Screens.blockContactList(this), 0);
                return;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                showFilteredContactList(Screens.unblockContactList(this), 1);
                return;
            case ScreenId.DELETE_CONTACT_LIST:
                showFilteredContactList(Screens.deleteContactList(this), 2);
                return;
            case ScreenId.GROUP_MEMBERS:
                Screen membersScreen = Screens.groupMembers(this);
                MrimContact mrimContact = (MrimContact) AppState.getCurrentContact();
                MrimAccount mrimAccount = (MrimAccount) mrimContact.account;
                Vector groupMembers = (Vector) RegistrationState.getParam4();
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
                    labelScreen.setSoftKeys(ResourceAccessor.str(StringResKeys.STR_EMPTY), ResourceAccessor.str(StringResKeys.STR_SOFTKEY_NO), labelScreen.softKeyLeft, labelScreen.softKeyCenter, labelScreen.softKeyRight);
                }
                ScreenManager.showScreen(membersScreen);
                RegistrationState.clearParam4();
                return;
            case ScreenId.EDIT_MEMBERS:
                ScreenManager.showScreen(ContactListManager.buildContactListScreen(Screens.editMembers(this), (Account) null, AppState.getCurrentContact()));
                return;
            case ScreenId.CONTACT_DELETE_MRIM:
                RegistrationState.clearParam1();
                NotificationHelper.showErrorOrConfirm(145, 727, ((MrimAccount) AppState.getCurrentContact().account).sendDeleteCommand(MrimProfileManager.getPendingDisplayText()));
                return;
            case ScreenId.GROUP_MANAGEMENT:
                SessionState.setHasMultipleMrim(AccountManager.getMrimAccountList().size() > 1);
                Screens.groupManagement(this).show();
                return;
            case ScreenId.CONTACT_MODIFY:
                ScreenManager.prepareFormData();
                MrimContact modifyContact = (MrimContact) AppState.getCurrentContact();
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
                Screen visibleScreen = Screens.groupManagementAlt(this);
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
                return ContactListManager.saveSelectionState();
            case ScreenId.CONTACT_EDITOR:
                ScreenManager.processScreenForm();
                String[] phoneNumbers = Utils.getPhoneNumbers(false);
                Object[] contactData = new Object[phoneNumbers.length + 1];
                contactData[0] = Utils.defaultStr(UIState.getInputText());
                for (int pi = 0; pi < phoneNumbers.length; pi++) {
                    contactData[pi + 1] = phoneNumbers[pi];
                }
                Contact editContact = AppState.getCurrentContact();
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
                return AppState.getAccount() instanceof XmppProtocol ? ((XmppProtocol) AppState.getAccount()).addNewContact() : 0;
            case ScreenId.ADD_MRIM_CONTACT:
                ScreenManager.processScreenForm();
                MrimAccount addAccount = (MrimAccount) AppState.getAccount();
                String displayName = Utils.defaultStr(UIState.getInputText());
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
                            ByteBuffer packetBuf = new ByteBuffer().writeIntLE(MRIM_FLAG_PHONE_CONTACT).writeIntLE(103).writeStringLatin1(ResourceAccessor.str(StringResKeys.STR_PHONE_SUFFIX)).writeStringUTF16(displayName);
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
                if (SettingsState.isShowInList()) {
                    AccountManager.clearAllHighlights();
                }
                return 0;
            case ScreenId.GROUP_SELECTOR:
                return AccountManager.handleGroupSelection(action);
            case ScreenId.PHONE_GROUPS:
                ScreenManager.processScreenForm();
                ContactState.setSelectedGroup(Utils.splitNonEmpty(AppState.getCurrentMrimContact().contactGroupsStr, ',').elementAt(ContactState.getSelectedGroupIndex()));
                return 0;
            case ScreenId.ADD_CONTACT_INFO:
                ScreenManager.processScreenForm();
                int addResult = ContactState.getInfo().getAccount().validateGroupAdd(Utils.defaultStr(ContactState.getGroupAddName()), Utils.defaultStr(ContactState.getGroupAddDisplay()), Utils.defaultStr(ContactState.getGroupAddGroup()), (ContactGroup) ContactState.getGroupList().elementAt(ContactState.getGroupOperationResult()), ContactState.isGroupAddResult());
                return addResult != 0 ? NotificationHelper.showError(addResult) : 0;
            case ScreenId.CREATE_GROUP:
                ScreenManager.processScreenForm();
                int createResult = AppState.getAccount().validateGroupCreate(Utils.defaultStr(ContactState.getNewGroupName()));
                return createResult != 0 ? NotificationHelper.showError(createResult) : 0;
            case ScreenId.RENAME_GROUP:
                ScreenManager.processScreenForm();
                int renameResult = AppState.getCurrentGroup().rename(Utils.defaultStr(RegistrationState.getSearchResultName()));
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
                return ((ContactInfo) RegistrationState.getParam1()).isMrimContact() ? ScreenId.VCARD_ACTIONS : ((ContactInfo) RegistrationState.getParam1()).isXmppContact() ? -1 : UIState.getCurrentScreenId();
            case ScreenId.CONTACT_INFO_DETAIL:
                return ContactState.getInfo().isMrimContact() ? ScreenId.VCARD_ACTIONS : UIState.getCurrentScreenId();
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
                    ContactState.setInfo(ContactInfo.createForAccount(AppState.getCurrentContact().account).setEmailAddress((String) obj).setDisplayName((String) obj));
                    return ScreenId.ADD_CONTACT_INFO;
                }
                return NotificationHelper.showError(773);
            case ScreenId.EDIT_MEMBERS:
                Vector checkedItems = ContactListManager.getCheckedItems(currentScreen, 0);
                int editResult;
                if (checkedItems.size() == 0) {
                    editResult = NotificationHelper.showError(775);
                } else {
                    MrimContact editMember = (MrimContact) AppState.getCurrentContact();
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
                ContactState.setHiddenList(hiddenStr);
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
                MrimProfileManager.clearPendingProfile();
                return 0;
            case ScreenId.GROUP_MOVE:
                return 0;
            case ScreenId.CONTACT_MENU:
                return 0;
            case ScreenId.CONTACT_INFO_VIEW:
                MrimProfileManager.clearPendingProfile();
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
                if (AppState.getAccount().getType() == Account.TYPE_MMP) {
                    RegistrationState.clearSearchFields();
                    RegistrationState.setRegSmsMode(false);
                } else {
                    StringUtils.resetRegForm();
                }
                break;
            case ScreenId.ADD_MRIM_CONTACT:
                ScreenManager.clearFormFields();
                break;
            case ScreenId.ADD_CONTACT_INFO:
                ContactState.clearInfo();
                ContactState.clearGroupAddData();
                break;
            case ScreenId.CREATE_GROUP:
                ContactState.clearNewGroupName();
                break;
            case ScreenId.RENAME_GROUP:
                RegistrationState.clearSearchResultName();
                break;
            case ScreenId.BATCH_DELETE:
                RegistrationState.clearRegistrationData();
                break;
            case ScreenId.CONTACT_DELETE:
                if (AppState.getCurrentContact() != null) {
                    AppState.getCurrentContact().clearRegistrationData();
                }
                break;
            case ScreenId.CONTACT_INFO_VIEW:
                RegistrationState.clearParam1();
                break;
            case ScreenId.CONTACT_LIST_KEY:
                AccountManager.clearAllHighlights();
                SessionState.clearTempAccount();
                break;
            case ScreenId.GROUP_MEMBERS:
                UIState.clearPhotoCache1();
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
        ContactState.clearScreenFlags();
        ContactState.setMenuMode(false);
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
                UIState.setPhotoCache1(data);
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
                    if (SettingsState.isAutoReconnect()) {
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
                            ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).chatRoomManager.findByName(msgId);
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
                return RegistrationState.getParam1() == null ? 0 : ScreenId.CONTACT_INFO_VIEW;
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
                return RegistrationState.getParam1() == null ? 0 : ScreenId.CONTACT_INFO_VIEW;
            case ScreenId.GROUP_MANAGEMENT:
                return 0;
            case ScreenId.CONTACT_MODIFY:
                return RegistrationState.getParam4() == null ? 0 : ScreenId.GROUP_MEMBERS;
            case ScreenId.VISIBLE_CONTACTS:
                return 0;
            default:
                return 0;
        }
    }

    private static void setupContactMenuFlags(Contact contact) {
        ContactListManager.updateContactFlags(contact);
        boolean isMrim = contact instanceof MrimContact;
        ContactState.setMrim(isMrim);
        boolean isMrimGroup = isMrim && contact.isOffline();
        ContactState.setGroup(isMrimGroup);
        ContactState.setUser(isMrim && !isMrimGroup);
        ContactState.setOnline(contact.isOnline());
        ContactState.setUnread(contact.hasUnread() && !contact.isOnline());
        ContactState.setVcard(isMrim && !isMrimGroup && ((MrimContact) contact).hasVCard());
    }

    // filterType: 0=canDelete, 1=canBlock, 2=canUnblock
    private static void showFilteredContactList(ListView screen, int filterType) {
        Account account = AppState.getAccount();
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
            ScreenManager.showScreen(ContactListManager.addContactItems(screen, filtered));
        } else {
            NotificationHelper.showMessageById(762);
        }
        ObjectPool.releaseVector(filtered);
    }

    public static int deleteSelectedEntity() {
        int groupError;
        Object entity = ContactState.getEntity();
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
        Contact contact = AppState.getCurrentContact();
        int errorCode = contact.isOnline() ? 310 : contact.account.validateMove(contact, contact.account.findGroup(contact), group);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    public static int handleContactListKey() {
        MrimAccount account = (MrimAccount) SessionState.getTempAccount();
        AccountHandler.showMailAccountList();
        AppState.setAccount(account);
        UIState.setScreenAction(ScreenId.CHAT_ROOM_INIT);
        return ScreenId.CHAT_ROOMS;
    }

    public void onMenuItemEvent(ListView screen, MenuItem item) {
        if (screen.screenId == ScreenId.ADD_CONTACT) {
            if (AppState.getAccount().getType() == Account.TYPE_MRIM) {
                StringUtils.updateRegDropdowns(screen, item);
            }
        }
    }
}
