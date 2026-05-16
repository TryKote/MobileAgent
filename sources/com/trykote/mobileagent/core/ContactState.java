package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.ContactKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.model.ContactInfo;

import java.util.Vector;

/**
 * Typed facade for contact-related state in AppState.
 * Delegates to AppState — zero runtime overhead.
 */
public final class ContactState extends AppState {
    private ContactState() {}
    public static final ContactState INSTANCE = new ContactState();

    // --- Current entity (contact or group being viewed/edited) ---

    public static Object getEntity() {
        return getPoolObject(ContactKeys.SLOT_CURRENT_ENTITY);
    }

    public static Contact getContact() {
        return (Contact) getPoolObject(ContactKeys.SLOT_CURRENT_ENTITY);
    }

    public static ContactGroup getGroup() {
        return (ContactGroup) getPoolObject(ContactKeys.SLOT_CURRENT_ENTITY);
    }

    public static void setEntity(Object entity) {
        setPoolDirect(ContactKeys.SLOT_CURRENT_ENTITY, entity);
    }

    public static void clearEntity() {
        clearIndex(ContactKeys.SLOT_CURRENT_ENTITY);
    }

    // --- Contact info ---

    public static ContactInfo getInfo() {
        return (ContactInfo) getPoolObject(ContactKeys.SLOT_CONTACT_INFO);
    }

    public static void setInfo(Object info) {
        setObject(ContactKeys.SLOT_CONTACT_INFO, info);
    }

    public static void clearInfo() {
        clearIndex(ContactKeys.SLOT_CONTACT_INFO);
    }

    // --- Contact identity strings ---

    public static String getContactId() {
        return getString(ContactKeys.SLOT_CURRENT_CONTACT_ID);
    }

    public static void setContactId(String id) {
        setObject(ContactKeys.SLOT_CURRENT_CONTACT_ID, id);
    }

    public static void clearContactId() {
        clearIndex(ContactKeys.SLOT_CURRENT_CONTACT_ID);
    }

    public static String getJid() {
        return getString(ContactKeys.SLOT_CONTACT_JID);
    }

    public static String getDisplayName() {
        return getString(ContactKeys.SLOT_DISPLAY_NAME);
    }

    public static void setDisplayName(String name) {
        setObject(ContactKeys.SLOT_DISPLAY_NAME, name);
    }

    // --- Group management ---

    public static Object getSelectedGroup() {
        return getPoolObject(ContactKeys.SLOT_SELECTED_GROUP);
    }

    public static void setSelectedGroup(Object group) {
        setObject(ContactKeys.SLOT_SELECTED_GROUP, group);
    }

    public static void clearSelectedGroup() {
        clearIndex(ContactKeys.SLOT_SELECTED_GROUP);
    }

    public static int getSelectedGroupIndex() {
        return getInt(ContactKeys.INT_SELECTED_GROUP_INDEX);
    }

    public static void setSelectedGroupIndex(int index) {
        setInt(ContactKeys.INT_SELECTED_GROUP_INDEX, index);
    }

    public static Vector getGroupList() {
        return getVector(ContactKeys.VEC_GROUP_LIST);
    }

    public static void setGroupList(Vector groups) {
        setObject(ContactKeys.VEC_GROUP_LIST, groups);
    }

    public static Vector getContactGroups() {
        return getVector(ContactKeys.VEC_CONTACT_GROUPS);
    }

    public static void setContactGroups(Vector groups) {
        setObject(ContactKeys.VEC_CONTACT_GROUPS, groups);
    }

    public static int getGroupOperationResult() {
        return getInt(ContactKeys.INT_GROUP_OPERATION_RESULT);
    }

    public static void setGroupOperationResult(int result) {
        setInt(ContactKeys.INT_GROUP_OPERATION_RESULT, result);
    }

    // --- Add contact/group form data ---

    public static String getGroupAddName() {
        return getString(ContactKeys.SLOT_GROUP_ADD_NAME);
    }

    public static void setGroupAddName(String name) {
        setObject(ContactKeys.SLOT_GROUP_ADD_NAME, name);
    }

    public static String getGroupAddDisplay() {
        return getString(ContactKeys.SLOT_GROUP_ADD_DISPLAY);
    }

    public static void setGroupAddDisplay(String display) {
        setObject(ContactKeys.SLOT_GROUP_ADD_DISPLAY, display);
    }

    public static String getGroupAddGroup() {
        return getString(ContactKeys.SLOT_GROUP_ADD_GROUP);
    }

    public static void setGroupAddGroup(String group) {
        setObject(ContactKeys.SLOT_GROUP_ADD_GROUP, group);
    }

    public static String getNewGroupName() {
        return getString(ContactKeys.SLOT_NEW_GROUP_NAME);
    }

    public static void clearNewGroupName() {
        clearIndex(ContactKeys.SLOT_NEW_GROUP_NAME);
    }

    public static void clearGroupAddData() {
        clearRange(ContactKeys.SLOT_GROUP_ADD_NAME, ContactKeys.VEC_GROUP_LIST);
    }

    public static int getAddMode() {
        return getInt(ContactKeys.INT_ADD_CONTACT_MODE);
    }

    public static void setAddMode(int mode) {
        setInt(ContactKeys.INT_ADD_CONTACT_MODE, mode);
    }

    public static boolean isGroupAddResult() {
        return getBool(ContactKeys.FLAG_GROUP_ADD_RESULT);
    }

    public static void setGroupAddResult(boolean result) {
        setBool(ContactKeys.FLAG_GROUP_ADD_RESULT, result);
    }

    // --- Boolean flags ---

    public static boolean isListActive() {
        return getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE);
    }

    public static void setListActive(boolean active) {
        setBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE, active);
    }

    public static boolean isRefreshNeeded() {
        return getBool(ContactKeys.FLAG_REFRESH_CONTACTS);
    }

    public static void setRefreshNeeded(boolean needed) {
        setBool(ContactKeys.FLAG_REFRESH_CONTACTS, needed);
    }

    public static boolean isLoaded() {
        return getBool(ContactKeys.FLAG_CONTACTS_LOADED);
    }

    public static void setLoaded(boolean loaded) {
        setBool(ContactKeys.FLAG_CONTACTS_LOADED, loaded);
    }

    // --- Screen-scoped flags (cleared via clearScreenFlags) ---

    public static boolean isMrimEntityFlag() {
        return getBool(ContactKeys.FLAG_IS_MRIM_CONTACT);
    }

    public static void setMrimEntityFlag(boolean mrim) {
        setBool(ContactKeys.FLAG_IS_MRIM_CONTACT, mrim);
    }

    public static boolean isMrim() {
        return getBool(ContactKeys.FLAG_CONTACT_IS_MRIM);
    }

    public static void setMrim(boolean mrim) {
        setBool(ContactKeys.FLAG_CONTACT_IS_MRIM, mrim);
    }

    public static boolean isGroup() {
        return getBool(ContactKeys.FLAG_CONTACT_IS_GROUP);
    }

    public static void setGroup(boolean group) {
        setBool(ContactKeys.FLAG_CONTACT_IS_GROUP, group);
    }

    public static boolean isUser() {
        return getBool(ContactKeys.FLAG_CONTACT_IS_USER);
    }

    public static void setUser(boolean user) {
        setBool(ContactKeys.FLAG_CONTACT_IS_USER, user);
    }

    public static boolean isOnline() {
        return getBool(ContactKeys.FLAG_CONTACT_IS_ONLINE);
    }

    public static void setOnline(boolean online) {
        setBool(ContactKeys.FLAG_CONTACT_IS_ONLINE, online);
    }

    public static boolean hasUnread() {
        return getBool(ContactKeys.FLAG_CONTACT_HAS_UNREAD);
    }

    public static void setUnread(boolean unread) {
        setBool(ContactKeys.FLAG_CONTACT_HAS_UNREAD, unread);
    }

    public static boolean hasVcard() {
        return getBool(ContactKeys.FLAG_CONTACT_HAS_VCARD);
    }

    public static void setVcard(boolean vcard) {
        setBool(ContactKeys.FLAG_CONTACT_HAS_VCARD, vcard);
    }

    public static void clearScreenFlags() {
        clearRange(ContactKeys.SCREEN_FLAGS_START, ContactKeys.SCREEN_FLAGS_END);
    }

    // --- Menu state ---

    public static boolean isMenuMode() {
        return getBool(ContactKeys.FLAG_CONTACT_MENU_MODE);
    }

    public static void setMenuMode(boolean mode) {
        setBool(ContactKeys.FLAG_CONTACT_MENU_MODE, mode);
    }

    // --- Display ---

    public static int getIconSize() {
        return getInt(ContactKeys.INT_CONTACT_ICON_SIZE);
    }

    public static void setIconSize(int size) {
        setInt(ContactKeys.INT_CONTACT_ICON_SIZE, size);
    }

    public static int getTypeCode() {
        return getInt(ContactKeys.INT_CONTACT_TYPE_CODE);
    }

    public static void setTypeCode(int code) {
        setInt(ContactKeys.INT_CONTACT_TYPE_CODE, code);
    }

    // --- Persistence (delta-stored, keys < 295) ---

    public static String getHiddenList() {
        return getString(ContactKeys.HIDDEN_CONTACTS_LIST);
    }

    public static void setHiddenList(String list) {
        setObject(ContactKeys.HIDDEN_CONTACTS_LIST, list);
    }

    public static String getRegistryData() {
        return getString(ContactKeys.CONTACT_REGISTRY_DATA);
    }

    public static void setRegistryData(String data) {
        setObject(ContactKeys.CONTACT_REGISTRY_DATA, data);
    }
}
