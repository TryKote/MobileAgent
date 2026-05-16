package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.Message;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.net.ServiceRegistry;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.ProtocolFactory;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.MrimChatRoomManager;
import com.trykote.mobileagent.protocol.mrim.MrimCommand;
import com.trykote.mobileagent.protocol.mrim.MrimContact;
import com.trykote.mobileagent.protocol.mrim.MrimContactGroup;
import com.trykote.mobileagent.protocol.mrim.MrimProfileManager;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.ui.TextInputHelper;
import com.trykote.mobileagent.ui.screen.AccountScreen;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.JsonParser;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.TextBox;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class ContactScreen extends ScreenView {

    private static final char CYRILLIC_UPPER_START = 'А';
    private static final char CYRILLIC_UPPER_END = 'Я';
    private static final char CYRILLIC_LOWER_START = 'а';
    private static final char CYRILLIC_LOWER_END = 'я';
    private static final char CYRILLIC_IO_UPPER = 'Ё';
    private static final char CYRILLIC_IO_LOWER = 'ё';

    private static final int CYRILLIC_EXTRA_BYTES = 2;
    private static final int SMS_CHAR_LIMIT = 160;
    private static final long PHONE_INPUT_CHECK_INTERVAL_MS = 3000L;
    private static final int NAME_PARTS_COUNT = 3;
    private static final int CONTACT_TYPE_BASIC = 1;
    private static final int CONTACT_TYPE_MRIM = 4;
    private static final int MENU_TYPE_CONTACT_MENU = 3;
    private static final int MENU_TYPE_CONTACT_GROUP = 4;
    private static final int PACKED_CONFIRM_SUFFIX = 16167;
    private static final int CONTACT_INFO_ERROR_KEY = -1;
    private static final int INFO_MODE_MRIM = 503;
    private static final int JSON_KEY_MSG_ID = 329240;
    private static final int JSON_KEY_MARK_ACTION = 263673;
    private static final int MSG_FLAG_STARRED = 4;
    private static final int MARK_ACTION_READ = 1;
    private static final int MRIM_FLAG_PHONE_CONTACT = 1048576;

    public ContactScreen(int screenId) {
        super(typeFor(screenId), screenId);
    }

    private static int typeFor(int screenId) {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                return ScreenManager.TYPE_FULLSCREEN;
            case ScreenId.CONTACT_GROUP_MENU:
            case ScreenId.CONTACT_MENU:
                return ScreenManager.TYPE_POPUP;
            case ScreenId.DELETE_ENTITY:
            case ScreenId.BATCH_DELETE:
            case ScreenId.CONTACT_DELETE:
            case ScreenId.CONTACT_DELETE_MRIM:
            case ScreenId.CONTACT_MODIFY:
                return ScreenManager.TYPE_TOAST;
            default:
                return ScreenManager.TYPE_FULLSCREEN;
        }
    }

    public void showSelf() {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                ContactListManager.showContactList();
                return;
            case ScreenId.ADD_CONTACT:
                buildAddContact();
                return;
            case ScreenId.ADD_CONTACT_INFO:
                ContactListManager.showAddContactScreen();
                return;
            case ScreenId.CONTACT_LIST_KEY:
                return;
            default:
                ScreenManager.showScreen(this);
        }
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
            case ScreenId.ADD_CONTACT:
            case ScreenId.ADD_CONTACT_INFO:
            case ScreenId.CONTACT_LIST_KEY:
                break;
            case ScreenId.CONTACT_EDITOR:
                buildContactEditor();
                break;
            case ScreenId.ADD_MRIM_CONTACT:
                Screens.addMrimContact().show();
                break;
            case ScreenId.CONTACT_GROUP_MENU:
                buildContactGroupMenu();
                break;
            case ScreenId.CONTACT_GROUPS:
                Screens.contactGroups().show();
                break;
            case ScreenId.CONTACT_SETTINGS:
                Screens.contactSettings().show();
                break;
            case ScreenId.GROUP_SELECTOR:
                Screens.groupSelector().show();
                break;
            case ScreenId.PHONE_GROUPS:
                buildPhoneGroups();
                break;
            case ScreenId.CREATE_GROUP:
                Screens.createGroup().show();
                break;
            case ScreenId.RENAME_GROUP:
                RegistrationState.setSearchResultName(AppState.getCurrentGroup().name);
                Screens.renameGroup().show();
                break;
            case ScreenId.DELETE_ENTITY:
                buildDeleteEntity();
                break;
            case ScreenId.BATCH_DELETE:
                buildBatchDelete();
                break;
            case ScreenId.CONTACT_DELETE:
                buildContactDelete();
                break;
            case ScreenId.GROUP_MOVE:
                buildGroupMove();
                break;
            case ScreenId.CONTACT_MENU:
                buildContactMenu();
                break;
            case ScreenId.CONTACT_INFO_VIEW:
                buildContactInfoView();
                break;
            case ScreenId.CONTACT_INFO_DETAIL:
                ScreenManager.showScreen(ContactState.getInfo().buildContactScreen(Screens.contactInfoDetailScreen()));
                RegistrationState.clearParam1();
                UIState.setCurrentScreenId(ScreenId.PROFILE_LOAD);
                break;
            case ScreenId.BLOCK_CONTACT_LIST:
                showFilteredContactList(Screens.blockContactList(), 0);
                break;
            case ScreenId.UNBLOCK_CONTACT_LIST:
                showFilteredContactList(Screens.unblockContactList(), 1);
                break;
            case ScreenId.DELETE_CONTACT_LIST:
                showFilteredContactList(Screens.deleteContactList(), 2);
                break;
            case ScreenId.GROUP_MEMBERS:
                buildGroupMembers();
                break;
            case ScreenId.EDIT_MEMBERS:
                ScreenManager.showScreen(ContactListManager.buildContactListScreen(Screens.editMembers(), (Account) null, AppState.getCurrentContact()));
                break;
            case ScreenId.CONTACT_DELETE_MRIM:
                RegistrationState.clearParam1();
                NotificationHelper.showErrorOrConfirm(145, 727, ((MrimAccount) AppState.getCurrentContact().account).sendDeleteCommand(MrimProfileManager.getPendingDisplayText()));
                break;
            case ScreenId.GROUP_MANAGEMENT:
                SessionState.setHasMultipleMrim(AccountManager.getMrimAccountList().size() > 1);
                Screens.groupManagement().show();
                break;
            case ScreenId.CONTACT_MODIFY:
                buildContactModify();
                break;
            case ScreenId.VISIBLE_CONTACTS:
                buildVisibleContacts();
                break;
        }
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.saveSelectionState();
            case ScreenId.CONTACT_EDITOR:
                return handleContactEditorSave();
            case ScreenId.ADD_CONTACT:
                ScreenManager.processScreenForm();
                return AppState.getAccount().addNewContact();
            case ScreenId.ADD_MRIM_CONTACT:
                return handleAddMrimContact();
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
                ContactState.setSelectedGroup(Utils.splitNonEmpty(((MrimContact) AppState.getCurrentContact()).contactGroupsStr, ',').elementAt(ContactState.getSelectedGroupIndex()));
                return 0;
            case ScreenId.ADD_CONTACT_INFO:
                return handleAddContactInfo();
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
            case ScreenId.CONTACT_DELETE:
            case ScreenId.CONTACT_DELETE_MRIM:
            case ScreenId.CONTACT_MODIFY:
                return -1;
            case ScreenId.GROUP_MOVE:
                return handleGroupMove(data);
            case ScreenId.CONTACT_MENU:
                return ContactListManager.handleContactMenuAction(title, action);
            case ScreenId.CONTACT_INFO_VIEW:
                ContactInfo viewInfo = (ContactInfo) RegistrationState.getParam1();
                return viewInfo.hasProfileActions() ? ScreenId.VCARD_ACTIONS : viewInfo.isXmppContact() ? -1 : UIState.getCurrentScreenId();
            case ScreenId.CONTACT_INFO_DETAIL:
                return ContactState.getInfo().hasProfileActions() ? ScreenId.VCARD_ACTIONS : UIState.getCurrentScreenId();
            case ScreenId.CONTACT_LIST_KEY:
                return handleContactListKey();
            case ScreenId.BLOCK_CONTACT_LIST:
                return validateBlockAction((Contact) data);
            case ScreenId.UNBLOCK_CONTACT_LIST:
                return validateUnblockAction((Contact) data);
            case ScreenId.DELETE_CONTACT_LIST:
                return validateDeleteAction((Contact) data);
            case ScreenId.GROUP_MEMBERS:
                return handleGroupMemberSelect(data);
            case ScreenId.EDIT_MEMBERS:
                return handleEditMembers();
            case ScreenId.GROUP_MANAGEMENT:
                return AccountManager.handleGroupRename(action);
            case ScreenId.VISIBLE_CONTACTS:
                return saveVisibleContacts();
            default:
                return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.onContactSelected(title, data);
            case ScreenId.CONTACT_GROUP_MENU:
                return ContactListManager.handleContactGroupAction(title, selectedOption);
            case ScreenId.GROUP_SELECTOR:
                return AccountManager.handleGroupSelection(selectedOption);
            case ScreenId.DELETE_ENTITY:
                return deleteSelectedEntity();
            case ScreenId.BATCH_DELETE:
            case ScreenId.CONTACT_DELETE:
            case ScreenId.CONTACT_DELETE_MRIM:
            case ScreenId.CONTACT_MODIFY:
                return -1;
            case ScreenId.GROUP_MOVE:
                return handleGroupMove(data);
            case ScreenId.CONTACT_MENU:
                return ContactListManager.handleContactMenuAction(title, selectedOption);
            case ScreenId.CONTACT_LIST_KEY:
                return handleContactListKey();
            case ScreenId.BLOCK_CONTACT_LIST:
            case ScreenId.UNBLOCK_CONTACT_LIST:
            case ScreenId.DELETE_CONTACT_LIST:
                return -1;
            case ScreenId.GROUP_MEMBERS:
                UIState.setPhotoCache1(data);
                return data != null ? 0 : -1;
            case ScreenId.GROUP_MANAGEMENT:
                return AccountManager.handleGroupRename(selectedOption);
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.onContactAction(data);
            case ScreenId.CONTACT_DELETE:
            case ScreenId.CONTACT_INFO_VIEW:
                MrimProfileManager.clearPendingProfile();
                return 0;
            case ScreenId.CONTACT_MODIFY:
                return ScreenId.CLOSE;
            default:
                return 0;
        }
    }

    public int onIdle(MenuItem item, Object data, String title) {
        switch (screenId) {
            case ScreenId.CONTACT_LIST:
                return ContactListManager.updateContextMenu(ScreenManager.getCurrentScreen(), data);
            case ScreenId.PHONE_GROUPS:
                return processPhoneInputIdle();
            case ScreenId.BATCH_DELETE:
                return processBatchDeleteIdle();
            case ScreenId.CONTACT_DELETE:
            case ScreenId.CONTACT_DELETE_MRIM:
                return RegistrationState.getParam1() == null ? 0 : ScreenId.CONTACT_INFO_VIEW;
            case ScreenId.CONTACT_MODIFY:
                return RegistrationState.getParam4() == null ? 0 : ScreenId.GROUP_MEMBERS;
            default:
                return 0;
        }
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.CONTACT_EDITOR:
            case ScreenId.ADD_MRIM_CONTACT:
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
        ContactState.clearScreenFlags();
        ContactState.setMenuMode(false);
    }

    public void onResumed(int closedScreenId) {
        if (screenId == ScreenId.CONTACT_LIST) {
            AppController.needsLayoutUpdate = true;
        }
    }

    public void onMenuItemChanged(MenuItem item) {
        if (screenId == ScreenId.ADD_CONTACT) {
            if (AppState.getAccount().getType() == Account.TYPE_MRIM) {
                StringUtils.updateRegDropdowns(ScreenManager.getCurrentScreen(), item);
            }
        }
    }

    // --- Build helpers ---

    private void buildAddContact() {
        int accountType = AppState.getAccount().getType();
        if (accountType == Account.TYPE_MRIM) {
            StringUtils.showRegionSelector();
            return;
        }
        if (accountType == Account.TYPE_MMP) {
            RegistrationState.setRegDomainIndex(-1);
            Screens.addContact().show();
            return;
        }
        StringUtils.resetRegForm();
        if (ContactListManager.getGroupCount(AppState.getAccount()) == 0) {
            EventDispatcher.postNotification(StringPool.get(StringResKeys.STR_NOTIFICATION_NEW_MSG));
        } else {
            Screens.addContactForm().show();
        }
    }

    private void buildContactEditor() {
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
        ContactState.setTypeCode((!editorContact.isMrimType() || editorContact.isSystem()) ? CONTACT_TYPE_BASIC : CONTACT_TYPE_MRIM);
        Screens.contactEditor().show();
    }

    private void buildContactGroupMenu() {
        ContactState.setMenuMode(true);
        Object entity = ContactState.getEntity();
        if (entity instanceof ContactGroup) {
            ContactState.setMrimEntityFlag(true);
            Screens.contactGroupMenu().show();
            return;
        }
        Contact contact = (Contact) entity;
        if (contact.isSystem()) {
            UIState.setCancelMenuAction(ScreenId.CONTACT_GROUP_MENU);
            UIState.setCancelMenuType(MENU_TYPE_CONTACT_GROUP);
            Screens.contactMenu().show();
            return;
        }
        setupContactMenuFlags(contact);
        ContactState.setMrimEntityFlag(false);
        UIState.setOkMenuType(MENU_TYPE_CONTACT_GROUP);
        UIState.setOkMenuAction(ScreenId.CONTACT_GROUP_MENU);
        Screens.contactActionsMenu().show();
    }

    private void buildPhoneGroups() {
        Vector phoneGroups = Utils.splitNonEmpty(((MrimContact) AppState.getCurrentContact()).contactGroupsStr, ',');
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
        Screens.phoneGroups().show();
    }

    private void buildDeleteEntity() {
        StringBuffer alertBuffer = ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_ALERT_PREFIX));
        Object deleteTarget = ContactState.getEntity();
        NotificationHelper.showAlertBuffer(71, alertBuffer.append(deleteTarget instanceof ContactGroup ? ((ContactGroup) deleteTarget).name : ((Contact) deleteTarget).displayName).append(ObjectPool.unpackChars(PACKED_CONFIRM_SUFFIX)));
    }

    private void buildBatchDelete() {
        NotificationHelper.showConfirmDialog(72, 866);
        Vector selectedItems = UIState.getMediaStream();
        Vector itemsParams = ObjectPool.newVector();
        for (int idx = selectedItems.size() - 1; idx >= 0; idx--) {
            Hashtable hashtable = new Hashtable();
            JsonParser.putIntKey(hashtable, JSON_KEY_MSG_ID, selectedItems.elementAt(idx));
            JsonParser.putIntKey(hashtable, JSON_KEY_MARK_ACTION, ObjectPool.integerOf(ChatState.getChatViewMode()));
            itemsParams.addElement(hashtable);
        }
        Vector outerParams = ObjectPool.newVector();
        outerParams.addElement(itemsParams);
        MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(StringPool.get(PackedStringKeys.URL_PATH_AJAX_MARKMSG), ObjectPool.newStringBuffer().append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL)).append(StringPool.get(PackedStringKeys.FUNC_AJAX_MARK_MSG)).append(SessionState.getSessionHash()).append(StringPool.get(PackedStringKeys.PARAM_DATA_EQ)).append(Conversation.urlEncode((Object) JsonParser.toJson(outerParams)))));
    }

    private void buildContactDelete() {
        RegistrationState.clearParam1();
        Contact deleteContact = AppState.getCurrentContact();
        NotificationHelper.showErrorOrConfirm(85, 727, deleteContact == null ? 0 : deleteContact.account.validateDelete(deleteContact));
    }

    private void buildGroupMove() {
        Screen groupScreen = Screens.groupMove();
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
    }

    private void buildContactMenu() {
        ContactState.setMenuMode(false);
        Contact menuContact = AppState.getCurrentContact();
        if (menuContact.isSystem()) {
            UIState.setCancelMenuAction(ScreenId.CONTACT_MENU);
            UIState.setCancelMenuType(MENU_TYPE_CONTACT_MENU);
            Screens.contactMenu().show();
            return;
        }
        setupContactMenuFlags(menuContact);
        UIState.setOkMenuType(MENU_TYPE_CONTACT_MENU);
        UIState.setOkMenuAction(ScreenId.CONTACT_MENU);
        Screens.contactActionsMenu().show();
    }

    private void buildContactInfoView() {
        ContactInfo contactInfo = (ContactInfo) RegistrationState.getParam1();
        String errorMessage = (String) contactInfo.get(ObjectPool.integerOf(CONTACT_INFO_ERROR_KEY));
        if (errorMessage != null) {
            NotificationHelper.showNotification(errorMessage);
        } else {
            RuntimeState.setInfoScreenMode(contactInfo.isXmppContact() ? 0 : INFO_MODE_MRIM);
            ScreenManager.showScreen(contactInfo.buildContactScreen(Screens.contactInfoViewScreen()));
        }
        UIState.setCurrentScreenId(ScreenId.USER_PROFILE);
    }

    private void buildGroupMembers() {
        Screen membersScreen = Screens.groupMembers();
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
            labelScreen.setSoftKeys(StringPool.get(StringResKeys.STR_EMPTY), StringPool.get(StringResKeys.STR_SOFTKEY_NO), labelScreen.softKeyLeft, labelScreen.softKeyCenter, labelScreen.softKeyRight);
        }
        ScreenManager.showScreen(membersScreen);
        RegistrationState.clearParam4();
    }

    private void buildContactModify() {
        ScreenManager.prepareFormData();
        MrimContact modifyContact = (MrimContact) AppState.getCurrentContact();
        MrimAccount modifyAccount = (MrimAccount) modifyContact.account;
        NotificationHelper.showErrorOrConfirm(150, 504, modifyAccount.trySendData(modifyAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(modifyAccount, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(Conversation.FLAG_EXTENDED).writeStringLatin1(modifyContact.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeIntLE(4).writeIntLE(1)), ObjectPool.integerOf(MrimAccount.RESP_AUTH), modifyContact, new Long(1L)})));
    }

    private void buildVisibleContacts() {
        Vector contactIds = ServiceRegistry.getAllContactIds();
        int contactCount = Utils.vectorSize(contactIds);
        if (contactCount == 0) {
            NotificationHelper.showMessageById(404);
            return;
        }
        Screen visibleScreen = Screens.groupManagementAlt();
        for (int ci = 0; ci < contactCount; ci++) {
            Object contactId = contactIds.elementAt(ci);
            visibleScreen.addItem(MenuItem.createCheckbox(ServiceRegistry.getPhotoHost(contactId), !ServiceRegistry.hiddenContacts.contains(contactId)));
        }
        ScreenManager.showScreen(visibleScreen);
    }

    // --- Selection handlers ---

    private static int handleContactEditorSave() {
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
    }

    private static int handleAddMrimContact() {
        ScreenManager.processScreenForm();
        MrimAccount addAccount = (MrimAccount) AppState.getAccount();
        String displayName = Utils.defaultStr(UIState.getInputText());
        String[] addPhones = Utils.getPhoneNumbers(false);
        if (!addAccount.isConnected()) {
            return NotificationHelper.showError(299);
        }
        if (!Utils.nonEmpty(displayName)) {
            return NotificationHelper.showError(708);
        }
        int phoneCount = addPhones.length;
        if (phoneCount == 0) {
            return NotificationHelper.showError(709);
        }
        if (hasDuplicatePhone(addAccount, addPhones, phoneCount)) {
            return NotificationHelper.showError(486);
        }
        MrimContactGroup contactGroup = addAccount.getFirstContactGroup();
        ByteBuffer packetBuf = new ByteBuffer().writeIntLE(MRIM_FLAG_PHONE_CONTACT).writeIntLE(103).writeStringLatin1(StringPool.get(StringResKeys.STR_PHONE_SUFFIX)).writeStringUTF16(displayName);
        String emailsJoined = Utils.joinComma(addPhones);
        int sendResult = addAccount.trySendData(addAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(addAccount, MrimCommand.CS_ADD_CONTACT, packetBuf.writeStringLatin1(emailsJoined).writeZeros(8)), ObjectPool.integerOf(MrimAccount.RESP_ADD_PHONE_CONTACT), displayName, emailsJoined, contactGroup}));
        return sendResult != 0 ? NotificationHelper.showError(sendResult) : 0;
    }

    private static boolean hasDuplicatePhone(MrimAccount account, String[] phones, int phoneCount) {
        Enumeration contactEnum = account.contactMap.elements();
        while (contactEnum.hasMoreElements()) {
            MrimContact existing = (MrimContact) contactEnum.nextElement();
            for (int pi = phoneCount - 1; pi >= 0; pi--) {
                if (existing.isInGroup(phones[pi])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int handleAddContactInfo() {
        ScreenManager.processScreenForm();
        int addResult = ContactState.getInfo().getAccount().validateGroupAdd(Utils.defaultStr(ContactState.getGroupAddName()), Utils.defaultStr(ContactState.getGroupAddDisplay()), Utils.defaultStr(ContactState.getGroupAddGroup()), (ContactGroup) ContactState.getGroupList().elementAt(ContactState.getGroupOperationResult()), ContactState.isGroupAddResult());
        return addResult != 0 ? NotificationHelper.showError(addResult) : 0;
    }

    private static int handleGroupMove(Object groupObj) {
        ContactGroup group = (ContactGroup) groupObj;
        if (group == null) {
            return ScreenId.CONTACT_LIST;
        }
        Contact contact = AppState.getCurrentContact();
        int errorCode = contact.isOnline() ? 310 : contact.account.validateMove(contact, contact.account.findGroup(contact), group);
        return errorCode != 0 ? NotificationHelper.showError(errorCode) : ScreenId.CONTACT_LIST;
    }

    private static int handleContactListKey() {
        MrimAccount account = (MrimAccount) SessionState.getTempAccount();
        AccountScreen.showMailAccountList();
        AppState.setAccount(account);
        UIState.setScreenAction(ScreenId.CHAT_ROOM_INIT);
        return ScreenId.CHAT_ROOMS;
    }

    private static int handleGroupMemberSelect(Object data) {
        if (data == null) {
            return -1;
        }
        if (data instanceof String) {
            ContactState.setInfo(ContactInfo.createForAccount(AppState.getCurrentContact().account).setEmailAddress((String) data).setDisplayName((String) data));
            return ScreenId.ADD_CONTACT_INFO;
        }
        return NotificationHelper.showError(773);
    }

    private static int handleEditMembers() {
        ListView currentScreen = ScreenManager.getCurrentScreen();
        Vector checkedItems = ContactListManager.getCheckedItems(currentScreen, 0);
        if (checkedItems.size() == 0) {
            return NotificationHelper.showError(775);
        }
        MrimContact editMember = (MrimContact) AppState.getCurrentContact();
        MrimAccount editAccount = (MrimAccount) editMember.account;
        ByteBuffer buffer = new ByteBuffer();
        int memberCount = checkedItems.size();
        ByteBuffer membersBuf = buffer.writeIntLE(memberCount);
        for (int mi = memberCount - 1; mi >= 0; mi--) {
            membersBuf.writeStringLatin1((String) checkedItems.elementAt(mi));
        }
        int editSendResult = editAccount.trySendData(editAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(editAccount, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(Conversation.FLAG_EXTENDED).writeStringLatin1(editMember.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeBufferIntLen(new ByteBuffer().writeIntLE(3).writeBufferIntLen(membersBuf))), ObjectPool.integerOf(MrimAccount.RESP_AUTH), editMember, new Long(2L)}));
        return editSendResult != 0 ? NotificationHelper.showError(editSendResult) : 0;
    }

    private static int validateBlockAction(Contact contact) {
        if (contact == null) {
            return 0;
        }
        int result = contact.validateBlock();
        return result != 0 ? NotificationHelper.showError(result) : 0;
    }

    private static int validateUnblockAction(Contact contact) {
        if (contact == null) {
            return 0;
        }
        int result = contact.validateUnblock();
        return result != 0 ? NotificationHelper.showError(result) : 0;
    }

    private static int validateDeleteAction(Contact contact) {
        if (contact == null) {
            return 0;
        }
        int result = contact.validateDelete();
        return result != 0 ? NotificationHelper.showError(result) : 0;
    }

    private static int saveVisibleContacts() {
        ListView currentScreen = ScreenManager.getCurrentScreen();
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
        return contactError != 0 ? NotificationHelper.showError(contactError) : ScreenId.CONTACT_LIST;
    }

    // --- Idle processing ---

    private static int processPhoneInputIdle() {
        TextBox textBox;
        if (!TimerManager.checkTimer(TimerManager.SLOT_PHONE_INPUT_CHECK, PHONE_INPUT_CHECK_INTERVAL_MS)) {
            return 0;
        }
        textBox = TextInputHelper.getTextInputBox();
        if (textBox == null) {
            return 0;
        }
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
        return 0;
    }

    private static int processBatchDeleteIdle() {
        Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
        if (asyncResult == null) {
            return 0;
        }
        int responseCode = ApiClient.validateJsonResponse(asyncResult);
        if (responseCode != 0) {
            return responseCode;
        }
        Object payload = ApiClient.getJsonPayload();
        for (int idx = ((Vector) payload).size() - 1; idx >= 0; idx--) {
            Object entry = ((Vector) payload).elementAt(idx);
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

    // --- Utility ---

    private static void setupContactMenuFlags(Contact contact) {
        ContactListManager.updateContactFlags(contact);
        boolean isMrim = contact.isMrimType();
        ContactState.setMrim(isMrim);
        boolean isMrimGroup = isMrim && contact.isOffline();
        ContactState.setGroup(isMrimGroup);
        ContactState.setUser(isMrim && !isMrimGroup);
        ContactState.setOnline(contact.isOnline());
        ContactState.setUnread(contact.hasUnread() && !contact.isOnline());
        ContactState.setVcard(isMrim && !isMrimGroup && contact.hasLocationData());
    }

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
}
