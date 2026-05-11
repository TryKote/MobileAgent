package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public abstract class ContactListManager {

    private static final int ICON_SIZE_SMALL = 1;
    private static final int ICON_SIZE_LARGE = 12;
    private static final int PHONE_PAGE_SIZE = 10;

    public static int dialPhoneContactNext() {
        dialPhoneContact((PhoneContact) UIState.getPhoneContact(), RuntimeState.getPhoneScrollOffset() + PHONE_PAGE_SIZE);
        return ScreenId.MAP;
    }

    public static int dialPhoneContactPrev() {
        dialPhoneContact((PhoneContact) UIState.getPhoneContact(), RuntimeState.getPhoneScrollOffset() - PHONE_PAGE_SIZE);
        return ScreenId.MAP;
    }
    private static final long ONE_WEEK_MS = 604800000L;
    private static final int PRESENCE_SUBSCRIBE = 40;
    private static final int PRESENCE_UNSUBSCRIBE = 4;
    public static void showContactList() {
        RemoteLogger.log("CL", "showContactList called");
        SessionState.clearCurrentAccount();
        ContactState.clearEntity();
        SessionState.setConnectionState(4);
        TabBar.findTab(TabBar.TYPE_CONTACTS, TabBar.currentAccount);
        ListView contactList = buildContactList();
        TabBar currentTab = TabBar.getCurrentTab();
        ListView selectedScreen = contactList.selectByTitle(currentTab.selectedTitle);
        ScreenManager.pushScreen(selectedScreen);
        selectedScreen.scrollOffset = currentTab.selectedIndex;
        selectedScreen.invalidateLayout();
    }

    public static int selectContact() {
        updateState();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        AppState.setCurrentEntity(menuItem == null ? null : menuItem.data);
        return validateContactAction();
    }

    private static void updateState() {
        TabBar currentTab = TabBar.getCurrentTab();
        ListView currentScreen = ScreenManager.getCurrentScreen();
        currentTab.selectedIndex = currentScreen.scrollOffset;
        currentTab.selectedTitle = currentScreen.getSelectedTitle();
    }

    public static void clearState() {
        SessionState.clearCurrentAccount();
        ContactState.clearEntity();
        updateState();
    }

    public static void refreshList() {
        RemoteLogger.log("CL", "refreshList called");
        clearState();
        TabBar currentTab = TabBar.getCurrentTab();
        ListView selectedScreen = buildContactList().selectByTitle(currentTab.selectedTitle);
        ScreenManager.pushScreen(selectedScreen);
        selectedScreen.scrollOffset = currentTab.selectedIndex;
        selectedScreen.invalidateLayout();
        TabBar.findTab(TabBar.TYPE_CONTACTS, TabBar.currentAccount);
        AppController.needsRepaint = true;
    }

    public static int getSelectedContact() {
        updateState();
        return 0;
    }

    public static int onContactSelected(String title, Object entity) {
        if (title == null) {
            return -1;
        }
        updateState();
        AppState.setCurrentEntity(entity);
        if (entity == null) {
            return 0;
        }
        if (entity instanceof ContactGroup) {
            AppController.needsLayoutUpdate = true;
            return ((ContactGroup) entity).toggleSpecial();
        }
        if (!(entity instanceof Contact)) {
            return 0;
        }
        UIState.clearStatusText();
        openContactMessages();
        return ((Contact) entity).getDefaultAction();
    }

    public static int onContactAction(Object entity) {
        updateState();
        AppState.setCurrentEntity(entity);
        return entity != null ? 30 : -1;
    }
    public static int updateContextMenu(ListView screen, Object entity) {
        Account account;
        int contextAction = -1;
        if (SessionState.getAccountSelectionObj() != null) {
            return ScreenId.PRESENCE_ACTION;
        }
        if (!SessionState.isCleanupDone()) {
            SessionState.setCleanupDone(1);
            if (System.currentTimeMillis() - SessionState.getTimestampFirstRun() > ONE_WEEK_MS) {
                UIState.setShowNotification(0);
                return ScreenId.FIRST_RUN;
            }
        }
        updateState();
        Vector tabItems = screen.tabItems;
        if (entity != null) {
            Contact contact = null;
            ContactGroup group = null;
            if (entity instanceof ContactGroup) {
                ContactGroup selectedGroup = (ContactGroup) entity;
                group = selectedGroup;
                account = selectedGroup.account;
            } else {
                Contact selectedContact = (Contact) entity;
                contact = selectedContact;
                account = selectedContact.account;
            }
            int iconId = account.getIconId();
            String shortName = account.shortName;
            if (!SettingsState.isMultiAccount()) {
                TabBar.updateTitle(iconId, shortName);
            }
            if (tabItems != null) {
                boolean needsRebuild = false;
                String separator = AppState.emptyStr;
                int expectedSize = 0;
                if (contact != null) {
                    try {
                        contextAction = contact.getContextAction();
                    } catch (Throwable unused) {
                        needsRebuild = true;
                    }
                    if (contextAction >= 0) {
                        expectedSize = 0 + 1;
                        if (tabItems.size() < expectedSize || ((Integer) tabItems.elementAt(0)).intValue() != contextAction) {
                            needsRebuild = true;
                        } else {
                            int extraIdx = expectedSize;
                            expectedSize++;
                            if (tabItems.size() <= extraIdx || !tabItems.elementAt(extraIdx).equals(contact.extra)) {
                                needsRebuild = true;
                            } else if (group != null) {
                                expectedSize++;
                                if (tabItems.elementAt(0).equals(separator)) {
                                    int extType = account.getExtType();
                                    if (extType >= 0) {
                                        int extIdx = expectedSize;
                                        expectedSize++;
                                        if (tabItems.size() <= extIdx || ((Integer) tabItems.elementAt(extIdx)).intValue() != extType) {
                                            needsRebuild = true;
                                        } else if (expectedSize != tabItems.size()) {
                                            needsRebuild = true;
                                        }
                                    }
                                } else {
                                    needsRebuild = true;
                                }
                            }
                        }
                        if (needsRebuild) {
                            tabItems.removeAllElements();
                            if (contact != null) {
                                int newContextAction = contact.getContextAction();
                                if (newContextAction >= 0) {
                                    tabItems.addElement(ObjectPool.integerOf(newContextAction));
                                }
                                tabItems.addElement(contact.extra);
                            }
                            if (group != null) {
                                tabItems.addElement(separator);
                            }
                            int extType = account.getExtType();
                            if (extType >= 0) {
                                tabItems.addElement(ObjectPool.integerOf(extType));
                            }
                            AppController.needsRepaint = true;
                        }
                    }
                }
            }
        } else if (tabItems != null && tabItems.size() > 0) {
            tabItems.removeAllElements();
            AppController.needsRepaint = true;
        }
        return UIState.isConversationActive() ? ScreenId.NOTIFY_MESSAGE : 0;
    }

    private static ListView buildContactList() {
        RemoteLogger.log("CL", "buildContactList: currentAccount=" + (TabBar.currentAccount != null ? TabBar.currentAccount.login : "null"));
        int layoutColumns = 1 + SettingsState.getContactSortMode();
        ContactState.setIconSize(layoutColumns == 1 ? ICON_SIZE_SMALL : ICON_SIZE_LARGE);
        Screen screen = Screens.contactListTemplate(null);
        int availableWidth = screen.contentWidth - 1;
        if (!SettingsState.isShowOffline()) {
            buildFlatList(screen, layoutColumns, availableWidth);
        } else if (SettingsState.isGroupByStatus()) {
            buildGroupedByStatus(screen, layoutColumns, availableWidth);
        } else {
            buildGroupedDefault(screen, layoutColumns, availableWidth);
        }
        TabBar.layout();
        return screen.initTabs();
    }

    private static void buildFlatList(ListView screen, int layoutColumns, int availableWidth) {
        boolean isConnected;
        boolean reverseSort = SettingsState.getSortOrder() == 0;
        Account currentAccount = TabBar.currentAccount;
        Vector allContacts = currentAccount == null ? AccountManager.getAllContacts() : currentAccount.getAllContacts();
        int contactCount = sortContacts(allContacts);
        for (int i = 0; i < contactCount; i++) {
            Contact contact = (Contact) allContacts.elementAt(i);
            if (!contact.canUnblock() && (contact.hasMessages() || contact.isOnline() || (!contact.canUnblock() && (reverseSort || (((isConnected = contact.account.isConnected()) && contact.highlighted) || (!isConnected && contact.isOffline())))))) {
                screen.addItem(contact.createMenuItem().setLayout(layoutColumns, availableWidth / layoutColumns));
            }
        }
        ObjectPool.releaseVector(allContacts);
    }

    private static void buildGroupedByStatus(ListView screen, int layoutColumns, int availableWidth) {
        int itemWidth = availableWidth / layoutColumns;
        boolean showGroups = SettingsState.isShowGroups();
        boolean reverseSort = SettingsState.getSortOrder() == 0;
        Vector mergedGroups = ObjectPool.newVector();
        Vector contactGroups = AccountManager.getContactGroups(TabBar.currentAccount);
        for (int idx = contactGroups.size() - 1; idx >= 0; idx--) {
            ContactGroup group = (ContactGroup) contactGroups.elementAt(idx);
            String groupName = group.name;
            MergedContactGroup foundGroup = null;
            for (int idx2 = mergedGroups.size() - 1; idx2 >= 0; idx2--) {
                MergedContactGroup candidate = (MergedContactGroup) mergedGroups.elementAt(idx2);
                if (candidate.name.equals(groupName)) {
                    foundGroup = candidate;
                    break;
                }
            }
            MergedContactGroup mergedGroup = foundGroup;
            if (foundGroup == null) {
                MergedContactGroup newMergedGroup = new MergedContactGroup(group, mergedGroups.size());
                mergedGroup = newMergedGroup;
                mergedGroups.addElement(newMergedGroup);
            }
            Vector groupContacts = group.contacts;
            for (int idx3 = groupContacts.size() - 1; idx3 >= 0; idx3--) {
                mergedGroup.addContact(groupContacts.elementAt(idx3));
            }
        }
        ObjectPool.releaseVector(contactGroups);
        int groupCount = sortContacts(mergedGroups);
        for (int i = 0; i < groupCount; i++) {
            addGroupWithContacts(screen, (ContactGroup) mergedGroups.elementAt(i),
                    reverseSort, showGroups, layoutColumns, itemWidth, availableWidth);
        }
        ObjectPool.releaseVector(mergedGroups);
        MergedContactGroup pendingMerged = null;
        MergedContactGroup offlineMerged = null;
        MergedContactGroup onlineMerged = null;
        MergedContactGroup unreadMerged = null;
        for (int acctIdx = AccountManager.getActiveAccountCount() - 1; acctIdx >= 0; acctIdx--) {
            Account account = AccountManager.getAccountByIndex(acctIdx);
            Account currentAccount = TabBar.currentAccount;
            if (currentAccount == null || currentAccount == account) {
                pendingMerged = mergeContacts(pendingMerged, account.specialGroup, -4, account.getPendingContacts());
                offlineMerged = mergeContacts(offlineMerged, account.offlineGroup, -1, account.getOfflineContacts());
                onlineMerged = mergeContacts(onlineMerged, account.defaultGroup, -2, account.getOnlineContacts());
                unreadMerged = mergeContacts(unreadMerged, account.onlineGroup, -3, account.getUnreadContacts());
            }
        }
        addMergedGroupSection(screen, pendingMerged, layoutColumns, itemWidth, availableWidth);
        addMergedGroupSection(screen, offlineMerged, layoutColumns, itemWidth, availableWidth);
        addMergedGroupSection(screen, unreadMerged, layoutColumns, itemWidth, availableWidth);
        addMergedGroupSection(screen, onlineMerged, layoutColumns, itemWidth, availableWidth);
    }

    private static void buildGroupedDefault(ListView screen, int layoutColumns, int availableWidth) {
        int itemWidth = availableWidth / layoutColumns;
        Vector contactGroups = AccountManager.getContactGroups(TabBar.currentAccount);
        int groupCount = sortContacts(contactGroups);
        boolean showGroups = SettingsState.isShowGroups();
        boolean reverseSort = SettingsState.getSortOrder() == 0;
        for (int i = 0; i < groupCount; i++) {
            addGroupWithContacts(screen, (ContactGroup) contactGroups.elementAt(i),
                    reverseSort, showGroups, layoutColumns, itemWidth, availableWidth);
        }
        ObjectPool.releaseVector(contactGroups);
        int accountCount = AccountManager.getActiveAccountCount();
        for (int acctIdx = accountCount - 1; acctIdx >= 0; acctIdx--) {
            Account account = AccountManager.getAccountByIndex(acctIdx);
            Account currentAccount = TabBar.currentAccount;
            if (currentAccount == null || currentAccount == account) {
                addSpecialSection(screen, account.specialGroup, account.getPendingContacts(),
                        layoutColumns, itemWidth, availableWidth);
            }
        }
        for (int acctIdx = accountCount - 1; acctIdx >= 0; acctIdx--) {
            Account account = AccountManager.getAccountByIndex(acctIdx);
            Account currentAccount = TabBar.currentAccount;
            if (currentAccount == null || currentAccount == account) {
                addSpecialSection(screen, account.offlineGroup, account.getOfflineContacts(),
                        layoutColumns, itemWidth, availableWidth);
            }
        }
        for (int acctIdx = accountCount - 1; acctIdx >= 0; acctIdx--) {
            Account account = AccountManager.getAccountByIndex(acctIdx);
            Account currentAccount = TabBar.currentAccount;
            if (currentAccount == null || currentAccount == account) {
                addSpecialSection(screen, account.defaultGroup, account.getOnlineContacts(),
                        layoutColumns, itemWidth, availableWidth);
            }
        }
        for (int acctIdx = accountCount - 1; acctIdx >= 0; acctIdx--) {
            Account account = AccountManager.getAccountByIndex(acctIdx);
            Account currentAccount = TabBar.currentAccount;
            if (currentAccount == null || currentAccount == account) {
                addSpecialSection(screen, account.onlineGroup, account.getUnreadContacts(),
                        layoutColumns, itemWidth, availableWidth);
            }
        }
    }

    private static void addGroupWithContacts(ListView screen, ContactGroup group, boolean reverseSort,
            boolean showGroups, int layoutColumns, int itemWidth, int availableWidth) {
        boolean headerAdded = false;
        if (showGroups || !group.isNotSpecial()) {
            screen.addItem(group.createMenuItem(-1).setLayout(layoutColumns, availableWidth));
            headerAdded = true;
        }
        if (group.isNotSpecial()) {
            Vector contacts = group.contacts;
            int contactCount = sortContacts(contacts);
            for (int j = 0; j < contactCount; j++) {
                Contact contact = (Contact) contacts.elementAt(j);
                if (shouldDisplayContact(reverseSort, contact)) {
                    if (!headerAdded) {
                        screen.addItem(group.createMenuItem(-1).setLayout(layoutColumns, availableWidth));
                        headerAdded = true;
                    }
                    screen.addItem(contact.createMenuItem().setLayout(layoutColumns, itemWidth));
                }
            }
        }
    }

    private static MergedContactGroup mergeContacts(MergedContactGroup existing, ContactGroup template,
            int groupId, Vector contacts) {
        int size = contacts.size();
        if (size > 0) {
            if (existing == null) {
                existing = new MergedContactGroup(template, groupId);
            }
            for (int j = size - 1; j >= 0; j--) {
                existing.addContact(contacts.elementAt(j));
            }
        }
        ObjectPool.releaseVector(contacts);
        return existing;
    }

    private static void addMergedGroupSection(ListView screen, MergedContactGroup merged,
            int layoutColumns, int itemWidth, int availableWidth) {
        if (merged == null) {
            return;
        }
        Vector contacts = merged.contacts;
        int sortedCount = sortContacts(contacts);
        screen.addItem(merged.createMenuItem(sortedCount).setLayout(layoutColumns, availableWidth));
        if (merged.isNotSpecial()) {
            for (int j = 0; j < sortedCount; j++) {
                screen.addItem(((Contact) contacts.elementAt(j)).createMenuItem().setLayout(layoutColumns, itemWidth));
            }
            ObjectPool.releaseVector(contacts);
        }
    }

    private static void addSpecialSection(ListView screen, ContactGroup group, Vector contacts,
            int layoutColumns, int itemWidth, int availableWidth) {
        int size = contacts.size();
        if (size > 0) {
            screen.addItem(group.createMenuItem(size).setLayout(layoutColumns, availableWidth));
            if (group.isNotSpecial()) {
                sortContacts(contacts);
                for (int j = 0; j < size; j++) {
                    screen.addItem(((Contact) contacts.elementAt(j)).createMenuItem().setLayout(layoutColumns, itemWidth));
                }
            }
        }
        ObjectPool.releaseVector(contacts);
    }

    private static boolean shouldDisplayContact(boolean reverseSort, Contact contact) {
        return ((!contact.hasMessages() && !reverseSort && !contact.highlighted) || contact.canUnblock() || contact.hasUnread() || contact.isOnline() || contact.isOffline() || contact.isSystem()) ? false : true;
    }

    public static ListView addContactItems(ListView screen, Vector items) {
        MenuItem menuItem;
        int count = Utils.vectorSize(items);
        for (int i = 0; i < count; i++) {
            Object item = items.elementAt(i);
            if (item instanceof Contact) {
                menuItem = ((Contact) item).createMenuItem();
            } else if (item instanceof ContactGroup) {
                menuItem = ((ContactGroup) item).createMenuItem(-1);
            } else if (item instanceof ContactInfo) {
                ContactInfo contactInfo = (ContactInfo) item;
                if (contactInfo.getAccount() instanceof MrimAccount) {
                    MenuItem entry = MenuItem.createDefault().setIcon(AppController.resolveServerIcon(Utils.parseIntBounded(contactInfo.getString(10), 0, 4, 0), contactInfo.getString(12))).addText(Utils.withComma(contactInfo.getDisplayName()), 1, 0).setLabel(contactInfo.getString(3));
                    entry.data = contactInfo;
                    menuItem = entry;
                } else {
                    MenuItem entry = MenuItem.createDefault();
                    int gender = Utils.parseInt((Object) contactInfo.getString(61));
                    MenuItem entry2 = entry.setIcon(gender == 0 ? 255 : gender == 1 ? 256 : 263).setLabel(Utils.appendSpace(contactInfo.getString(60))).addText(Utils.withComma(contactInfo.getDisplayName()), 1, 0).setLabel(StringUtils.concat(Utils.appendSpace(contactInfo.getFirstName()), contactInfo.getLastName()));
                    entry2.data = contactInfo;
                    menuItem = entry2;
                }
            } else {
                menuItem = ((Account) item).createMenuItem();
            }
            screen.addItem(menuItem);
        }
        return screen;
    }

    public static void updateContactFlags(Contact contact) {
        UIState.setXmppCanEdit((contact instanceof XmppContact) && !((XmppProtocol) contact.account).isMailRuVariant());
    }

    public static int getGroupCount(Account acct) {
        Vector groups = acct.groups;
        int count = Utils.vectorSize(groups);
        if (count > 0) {
            StringBuffer sb = ObjectPool.newStringBuffer();
            for (int i = 0; i < count; i++) {
                sb.append(((ContactGroup) groups.elementAt(i)).name).append((char) 0);
            }
            UIState.setMenuItemFromBuffer(sb);
            ContactState.setGroupList(groups);
            ContactState.setGroupOperationResult(0);
        }
        return count;
    }

    public static void showAddContactScreen() {
        ContactInfo contactInfo = ContactState.getInfo();
        Account acctRef = contactInfo.getAccount();
        if (getGroupCount(acctRef) == 0) {
            EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_NOTIFICATION_NEW_MSG));
            return;
        }
        if (UIState.isShowPhoto()) {
            ContactState.setGroupAddGroup(AppState.getString(StringResKeys.STR_SOFTKEY_OK));
            UIState.setShowPhoto(0);
        } else {
            ContactState.setGroupAddGroup(AppState.getString(StringResKeys.STR_DEFAULT_GROUP_NAME));
        }
        if (acctRef.getType() == Account.TYPE_MMP) {
            ContactState.setGroupAddName(contactInfo.getString(60));
            ContactState.setGroupAddDisplay(contactInfo.getDisplayNameOrId());
            Screens.contactListScreen(null).show();
            return;
        }
        if (((MrimAccount) acctRef).hasCustomDomain) {
            ContactState.setGroupAddResult(true);
            ContactState.setAddMode(5);
        } else {
            ContactState.setGroupAddResult(false);
            ContactState.setAddMode(4);
        }
        ContactState.setGroupAddName(contactInfo.getEmailOrMmpId());
        ContactState.setGroupAddDisplay(contactInfo.getFullName());
        Screens.contactAddScreen(null).show();
    }
    public static ListView buildContactListScreen(ListView screen, Account acct, Contact contact) {
        MenuItem menuItem = null;
        if (contact != null) {
            acct = contact.account;
        }
        Vector contacts = acct.getAllContacts();
        for (int idx = contacts.size() - 1; idx >= 0; idx--) {
            MrimContact mrimContact = (MrimContact) contacts.elementAt(idx);
            if (mrimContact.isSystem() || mrimContact.isOnline() || mrimContact.isOffline() || mrimContact.hasUnread()) {
                contacts.removeElementAt(idx);
            }
        }
        sortContacts(contacts);
        for (int i = 0; i < contacts.size(); i++) {
            MrimContact candidate = (MrimContact) contacts.elementAt(i);
            String identifier = candidate.simpleIdentifier;
            String displayName = candidate.displayName;
            if (contact != null) {
                MrimContact targetContact = (MrimContact) contact;
                menuItem = targetContact.groupsList != null && targetContact.groupsList.contains(identifier) ? new MenuItem(2, displayName).setIconAndLabel(375, displayName) : MenuItem.createCheckbox(displayName, false);
            }
            menuItem.title = identifier;
            screen.addItem(menuItem);
        }
        ObjectPool.releaseVector(contacts);
        return screen;
    }

    public static Vector getCheckedItems(ListView screen, int startIndex) {
        Vector checkedItems = ObjectPool.newVector();
        Vector items = screen.menuItems;
        for (int idx = items.size() - 1; idx >= startIndex; idx--) {
            MenuItem menuItem = (MenuItem) items.elementAt(idx);
            Object data = menuItem.data;
            if (data != null && ((Boolean) data).booleanValue()) {
                checkedItems.addElement(menuItem.title);
            }
        }
        return checkedItems;
    }

    public static int handleContactMenuAction(String label, int actionId) {
        SessionState.clearCurrentAccount();
        Contact contact = AppState.getCurrentContact();
        if (actionId == 63 && !contact.account.isConnected()) {
            return NotificationHelper.showError(299);
        }
        if (actionId == 54 || actionId == 63 || actionId == 85) {
            ScreenBuilder.onScreenClosed();
        }
        if (StringUtils.matchesKey(717, label)) {
            int errorCode = ((MrimContact) contact).requestUserDetails();
            return errorCode != 0 ? NotificationHelper.showError(errorCode) : actionId;
        }
        if (actionId == 65) {
            ScreenBuilder.onScreenClosed();
            return clearSmsFields();
        }
        if (actionId == 66) {
            if (contact instanceof XmppContact) {
                return ((XmppContact) contact).sendPresence(PRESENCE_SUBSCRIBE);
            }
            ContactState.setInfo(new ContactInfo(contact));
        } else if (actionId == 54) {
            AppState.setAccount(contact.account);
            MailHelper.composeEmail(MailHelper.parseRecipientList(((MrimContact) contact).simpleIdentifier), (String) null, (String) null);
        } else if (actionId == 6) {
            ListItem item = (ListItem) contact;
            item.deselect();
            MapController.selectMapItem(item);
        }
        return actionId;
    }

    public static int handleContactGroupAction(String label, int actionId) {
        SessionState.clearCurrentAccount();
        Object entity = ContactState.getEntity();
        if (actionId == 63 && !((Contact) entity).account.isConnected()) {
            return NotificationHelper.showError(299);
        }
        if (actionId == 40 || actionId == 63 || actionId == 85) {
            ScreenBuilder.onScreenClosed();
            if (actionId != 85) {
                openContactMessages();
            }
        }
        if (StringUtils.matchesKey(717, label)) {
            int errorCode = ((MrimContact) entity).requestUserDetails();
            if (errorCode != 0) {
                return NotificationHelper.showError(errorCode);
            }
            return ScreenId.CLEAR_SEARCH;
        }
        if (actionId == 65) {
            ScreenBuilder.onScreenClosed();
            openContactMessages();
            return clearSmsFields();
        }
        if (actionId == 66) {
            if (entity instanceof XmppContact) {
                return ((XmppContact) entity).sendPresence(PRESENCE_UNSUBSCRIBE);
            }
            ContactState.setInfo(new ContactInfo((Contact) entity));
        } else if (actionId == 54) {
            AppState.setAccount(((MrimContact) entity).account);
            MailHelper.composeEmail(MailHelper.parseRecipientList(((MrimContact) entity).simpleIdentifier), (String) null, (String) null);
        } else if (actionId == 6) {
            ListItem item = (ListItem) entity;
            item.deselect();
            MapController.selectMapItem(item);
            MapController.applyViewMode(true, false, !MapState.isMapViewActive());
            ContactState.setRefreshNeeded(true);
        }
        return actionId;
    }

    public static int sortContacts(Vector vector) {
        int size = vector.size();
        sortRange(vector, 0, size - 1);
        return size;
    }

    private static void sortRange(Vector vector, int left, int right) {
        if (left < right) {
            if (left + 1 == right) {
                if (((Sortable) vector.elementAt(left)).compareTo(vector.elementAt(right)) > 0) {
                    Utils.swapElements(vector, left, right);
                    return;
                }
                return;
            }
            int lo = left;
            int hi = right;
            boolean moveLow = true;
            while (lo < hi) {
                if (((Sortable) vector.elementAt(lo)).compareTo(vector.elementAt(hi)) > 0) {
                    Utils.swapElements(vector, lo, hi);
                    moveLow = !moveLow;
                }
                if (moveLow) {
                    lo++;
                } else {
                    hi--;
                }
            }
            sortRange(vector, left, lo - 1);
            sortRange(vector, hi + 1, right);
        }
    }

    public static Vector getMapContacts() {
        Vector result = ObjectPool.newVector();
        Vector mrimAccounts = AccountManager.getMrimAccountList();
        for (int idx = mrimAccounts.size() - 1; idx >= 0; idx--) {
            Vector contacts = AccountManager.getMrimAccount(mrimAccounts, idx).getAllContacts();
            for (int idx2 = contacts.size() - 1; idx2 >= 0; idx2--) {
                MrimContact contact = (MrimContact) contacts.elementAt(idx2);
                if (contact.hasVCard()) {
                    result.addElement(contact);
                }
            }
            ObjectPool.releaseVector(contacts);
        }
        return result;
    }

    public static Vector getMapProfiles() {
        Vector result = ObjectPool.newVector();
        Vector mrimAccounts = AccountManager.getMrimAccountList();
        for (int idx = mrimAccounts.size() - 1; idx >= 0; idx--) {
            MrimAccount account = AccountManager.getMrimAccount(mrimAccounts, idx);
            if (account.profileManager.profile.hasCoordinates()) {
                result.addElement(account);
            }
        }
        return result;
    }

    public static void openContactMessages() {
        Contact contact = AppState.getCurrentContact();
        markContactUnread(contact);
        contact.flags = (byte) 0;
        contact.dirty = true;
        contact.updateRenderState();
        ScreenManager.showScreen(contact.showMessages().measureContent());
    }

    public static void markContactRead(Contact contact) {
        TimerManager.resetBacklightTimer();
        Vector contacts = UIState.getOnlineContacts();
        if (contacts.contains(contact)) {
            return;
        }
        contacts.addElement(contact);
        TabBar.layout();
    }

    public static void markContactUnread(Contact contact) {
        Vector contacts = UIState.getOnlineContacts();
        if (contacts.contains(contact)) {
            Utils.removeFrom(contacts, contact);
            TabBar.layout();
        }
    }

    public static void deleteContact(Contact contact) {
        contact.clearStatus();
        UIState.getPendingConnections().removeElement(contact);
        AppController.needsLayoutUpdate = true;
    }

    public static void refreshContactList() {
        RemoteLogger.log("CL", "refreshContactList called");
        AppState.clearRange(UIKeys.RANGE_ACCOUNT_CACHE_START, UIKeys.RANGE_ACCOUNT_CACHE_END);
    }

    public static void paintPopup(GraphicsContext g, int clipX, int clipY, int clipW, int clipH) {
        g.setClip(clipX, clipY, clipW, clipH);
        int popupHeight = UIState.getPopupHeight();
        if (popupHeight <= 0) {
            return;
        }
        g.setFont(UIState.getGfxContext(UIKeys.GFX_INDEX_DEFAULT));
        int screenHeight = UIState.getHeight() - 1;
        int screenWidth = UIState.getScreenWidth();
        g.setClip(0, (screenHeight - popupHeight) - 1, screenWidth, popupHeight + 1);
        g.setColorFromPalette(16);
        g.fillRect(0, (screenHeight - popupHeight) - 1, screenWidth, popupHeight + 1);
        g.setClip(1, screenHeight - popupHeight, screenWidth - 2, popupHeight);
        g.setColorFromPalette(1);
        g.fillRect(0, 0, 2048, 2048);
        int barHeight = Utils.max(UIState.getFontHeight(), 16);
        Vector tabs = UIState.getPopupItems();
        for (int idx = tabs.size() - 1; idx >= 0; idx--) {
            Account account = (Account) tabs.elementAt(idx);
            int barTop = screenHeight;
            int fontHeight = UIState.getFontHeight();
            int barHeight3 = Utils.max(fontHeight, 16);
            g.setColorFromPalette(13);
            int textY = barTop - fontHeight;
            g.fillRect(1, textY, ((UIState.getScreenWidth() - 2) * account.msgCount) / 100, barHeight3);
            g.drawIcon(account.getIconId(), 3, textY + ScreenManager.getCenterOffset());
            g.setColorFromPalette(0);
            g.drawString(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(account.login).append(' ').append(account.msgCount).append('%')), 21, barTop, 36);
            screenHeight -= barHeight;
        }
    }

    public static void dialPhoneContact(PhoneContact contact, int phoneIndex) {
        dialPhoneUrl(VCard.formatPhoneContactUrl(contact, phoneIndex), contact, phoneIndex);
    }

    public static void dialPhoneUrl(String url, PhoneContact contact, int phoneIndex) {
        new AsyncTask(AsyncTaskId.PARSE_CONTACTS_ASYNC, new Object[]{url, contact, ObjectPool.integerOf(phoneIndex)});
    }

    public static int clearSmsFields() {
        RegistrationState.clearSearchLabel();
        UIState.clearStatusText();
        ContactState.clearSelectedGroup();
        return ScreenId.PHONE_GROUPS;
    }

    public static int validateContactAction() {
        Object entity = ContactState.getEntity();
        if (entity == null || !(entity instanceof Contact)) {
            return 0;
        }
        Contact contact = (Contact) entity;
        if (!contact.account.isConnected()) {
            return NotificationHelper.showError(299);
        }
        SessionState.clearCurrentAccount();
        return (contact.isSystem() || contact.isOffline()) ? 0 : ScreenId.CONTACT_DELETE;
    }
}
