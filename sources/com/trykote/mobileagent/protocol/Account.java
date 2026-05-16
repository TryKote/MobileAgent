package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.AccountListener;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ContactListListener;
import com.trykote.mobileagent.core.MessageListener;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.model.SearchEntry;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.screen.ProfileScreen;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.io.ConnectionNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public abstract class Account {

    // Account type constants
    public static final int TYPE_MRIM = 0;
    public static final int TYPE_MMP = 1;
    public static final int TYPE_XMPP = 2;
    public static final int TYPE_XMPP_MAILRU = 3;

    // Common progress constants
    public static final int PROGRESS_DISCONNECTED = 0;
    public static final int PROGRESS_STARTING = 1;
    public static final int PROGRESS_CONNECTED = 100;

    // Sync array configuration
    private static final int SYNC_ARRAY_SIZE = 9;

    // Property serialization version
    private static final int PROPERTY_VERSION = 12;

    // Serialization flags
    private static final int FLAG_HAS_PROPERTIES = 8;

    // Validation error codes (string resource IDs)
    public static final int ERROR_DISCONNECTED = 299;

    // Connection retry settings
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;
    public int retryCount;

    public final Vector groups;

    public int accountId;

    public String login;

    public String password;

    public final int[] syncArray;

    public final ByteBuffer dataBuffer;

    public int state;

    public ConnectionThread connection;

    public final Hashtable contactMap;

    public int progress;

    public int msgCount;

    public int lastError;

    public int configFlags;

    public int syncSeq;

    public int sentCount;

    public int recvCount;

    public int reserved1;

    public long timeout;

    public long deadline;

    public int reserved2;

    public final Vector extras;

    public ContactGroup defaultGroup;

    public final ContactGroup onlineGroup;

    public final ContactGroup offlineGroup;

    public final ContactGroup blockedGroup;

    public final ContactGroup specialGroup;

    public String displayName;

    public String shortName;

    private int authMode;

    // Listener callbacks for protocol → UI decoupling
    private ContactListListener contactListListener;
    private AccountListener accountListener;
    private MessageListener messageListener;

    public Account(int accountId, String login, String password) {
        this.groups = ObjectPool.newVector();
        this.accountId = accountId;
        this.login = login;
        this.displayName = login;
        this.password = password;
        this.syncArray = new int[SYNC_ARRAY_SIZE];
        this.dataBuffer = new ByteBuffer();
        this.contactMap = new Hashtable();
        this.extras = ObjectPool.newVector();
        ContactGroup onlineGrp = createOnlineGroup();
        onlineGrp.isSpecial = true;
        this.onlineGroup = onlineGrp;
        ContactGroup offlineGrp = createOfflineGroup();
        offlineGrp.isSpecial = true;
        this.offlineGroup = offlineGrp;
        ContactGroup blockedGrp = createBlockedGroup();
        blockedGrp.isSpecial = true;
        this.blockedGroup = blockedGrp;
        ContactGroup specialGrp = createSpecialGroup();
        specialGrp.isSpecial = true;
        this.specialGroup = specialGrp;
        this.shortName = Utils.beforeAt(login);
    }

    public Account(ByteBuffer buffer) {
        this(buffer.readInt(), buffer.readHexStr(), buffer.readWideStr());
        buffer.readInt();
        for (int i = 2; i < SYNC_ARRAY_SIZE; i++) {
            this.syncArray[i] = buffer.readInt();
        }
        this.configFlags = buffer.readInt();
        this.displayName = buffer.readUTF8Str((String) null);
    }

    // Listener registration

    public final void setContactListListener(ContactListListener listener) {
        this.contactListListener = listener;
    }

    public final void setAccountListener(AccountListener listener) {
        this.accountListener = listener;
    }

    public final void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    // Notify helpers (null-safe)

    public final void notifyContactActivated(Contact contact) {
        if (this.contactListListener != null) {
            this.contactListListener.onContactActivated(contact);
        }
    }

    public final void notifyContactDeactivated(Contact contact) {
        if (this.contactListListener != null) {
            this.contactListListener.onContactDeactivated(contact);
        }
    }

    public final void notifyContactOnline(Contact contact) {
        if (this.contactListListener != null) {
            this.contactListListener.onContactOnline(contact);
        }
    }

    public final void notifyContactDeleted(Contact contact) {
        if (this.contactListListener != null) {
            this.contactListListener.onContactDeleted(contact);
        }
    }

    public final void notifyContactListUpdated() {
        if (this.contactListListener != null) {
            this.contactListListener.onContactListUpdated(this);
        }
    }

    public final void notifyConnectionProgressChanged() {
        if (this.accountListener != null) {
            this.accountListener.onConnectionProgressChanged(this);
        }
    }

    public final void notifyMessageReceived(Contact contact, int soundType) {
        if (this.messageListener != null) {
            this.messageListener.onMessageReceived(contact, soundType);
        }
    }

    public final void notifyMessageSent(Contact contact) {
        if (this.messageListener != null) {
            this.messageListener.onMessageSent(contact);
        }
    }

    public final ByteBuffer encodeId() {
        return new ByteBuffer().writeIntAsString(this.accountId).writeByte(95);
    }

    public final String getSignature() {
        return new ByteBuffer().writeByte(35).writeIntAsString(this.accountId).writeByte(35).writeRawString(this.login).readAllByteStr();
    }

    public abstract ContactGroup createOnlineGroup();

    public abstract ContactGroup createOfflineGroup();

    public abstract ContactGroup createBlockedGroup();

    public abstract ContactGroup createSpecialGroup();

    public abstract int getIconId();

    public void loadProperties(ByteBuffer buffer) {
        if (buffer.readInt() == PROPERTY_VERSION) {
            this.syncSeq = buffer.readInt();
            this.sentCount = buffer.readInt();
            this.recvCount = buffer.readInt();
            buffer.readInt();
        }
    }

    public void saveProperties(ByteBuffer buffer) {
        buffer.writeIntLE(PROPERTY_VERSION).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount).writeIntLE(0);
    }

    public Account serializeAccount(ByteBuffer buffer, boolean includeGroups, boolean includePrivate) {
        buffer.writeByte(getType() | FLAG_HAS_PROPERTIES).writeIntLE(this.accountId).writeStringLatin1(this.login).writeStringLatin1(this.password).writeIntLE(0);
        for (int i = 2; i < SYNC_ARRAY_SIZE; i++) {
            buffer.writeIntLE(this.syncArray[i]);
        }
        buffer.writeIntLE(this.configFlags);
        if (this.displayName != null) {
            buffer.writeStringUTF16(this.displayName);
        } else {
            buffer.writeIntLE(0);
        }
        if (includePrivate) {
            if (!includeGroups) {
                this.groups.removeAllElements();
            }
            int size = this.groups.size();
            buffer.writeIntLE(size);
            for (int remaining = size - 1; remaining >= 0; remaining--) {
                getGroup(remaining).serialize(buffer, true);
            }
            this.defaultGroup.serialize(buffer, true);
            this.contactMap.clear();
        } else {
            this.defaultGroup.serialize(buffer.writeIntLE(0), false);
        }
        return this;
    }

    public final int trySendData(ByteBuffer buffer) {
        if (isConnected()) {
            return sendData(buffer);
        }
        return ERROR_DISCONNECTED;
    }

    public final int sendData(ByteBuffer buffer) {
        RemoteLogger.log("ACCT", "sendData: " + buffer.length + " bytes, connState=" + this.connection.state);
        AccountManager.recordOutboundTraffic(this, buffer.length);
        ConnectionThread conn = this.connection;
        if (conn.exception != null) {
            RemoteLogger.log("ACCT", "sendData: connection has exception: " + conn.exception);
            throw new RuntimeException();
        }
        ByteBuffer outBuf = conn.outBuffer;
        int dataLen = buffer.length;
        if (dataLen > 0) {
            synchronized (outBuf) {
                outBuf.ensureCapacity(dataLen);
                Utils.arraycopy((Object) buffer.data, buffer.offset, (Object) outBuf.data, outBuf.length, dataLen);
                buffer.clear();
                outBuf.length += dataLen;
                outBuf.compact();
            }
        }
        if (this.timeout <= 0) {
            return 0;
        }
        this.deadline = System.currentTimeMillis() + this.timeout;
        return 0;
    }

    public final Account setDisplayName(String name) {
        this.displayName = Utils.defaultIfBlank(name, this.login);
        return this;
    }

    public final boolean isConnecting() {
        return this.progress > 0;
    }

    public final boolean isConnected() {
        return this.progress == PROGRESS_CONNECTED;
    }

    public final MenuItem createMenuItem() {
        MenuItem item = MenuItem.create(getSignature()).setIcon(getIconId()).setLabel(this.login);
        item.data = this;
        return item;
    }

    public final MenuItem createFlagMenuItem() {
        MenuItem item = MenuItem.create(getSignature()).setIcon(getIconId()).setLabel(this.login).setIcon(244);
        item.enabled = true;
        item.data = this;
        return item;
    }

    public abstract int getType();

    public int connect(int authModeParam) {
        RemoteLogger.log("ACCT", "connect(" + authModeParam + ") login=" + this.login + " progress=" + this.progress);
        if (isConnecting()) {
            RemoteLogger.log("ACCT", "already connecting, returning 297");
            return 297;
        }
        if (authModeParam == 0) {
            char firstChar = this.password.charAt(0);
            this.authMode = (firstChar < 'A' || firstChar > 'Z') ? 1 : 2;
        } else {
            this.authMode = authModeParam;
        }
        this.msgCount = 0;
        this.progress = PROGRESS_STARTING;
        RemoteLogger.log("ACCT", "connect: progress=1, authMode=" + this.authMode);
        return 0;
    }

    public int disconnect() {
        RemoteLogger.log("ACCT", "disconnect login=" + this.login + " isConnecting=" + isConnecting());
        return !isConnecting() ? 298 : 0;
    }

    public final void resetSyncIfChanged(int newValue) {
        int[] iArr = this.syncArray;
        int oldValue = iArr[8];
        if (newValue != oldValue) {
            iArr[2] = 0;
            iArr[3] = 0;
            if ((newValue >>> 8) != (oldValue >>> 8)) {
                iArr[4] = 0;
                iArr[5] = 0;
            }
            iArr[8] = newValue;
        }
    }

    public final int getSyncValue(int base, int offset) {
        return this.syncArray[base + base + offset];
    }

    public abstract void loadData() throws Throwable;

    public abstract int getDefaultError();

    public final void closeConnection() {
        RemoteLogger.log("ACCT", "closeConnection login=" + this.login);
        if (this.connection != null) {
            this.connection.state = ConnectionThread.STATE_CLOSING;
        }
        this.connection = null;
        this.progress = PROGRESS_DISCONNECTED;
    }

    public final void handleConnError() {
        String errorMsg;
        Throwable th = this.connection.exception;
        if (th == null) {
            errorMsg = StringPool.get(StringResKeys.STR_TIMEOUT_ERROR);
        } else {
            errorMsg = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(th).append(StringPool.get(StringResKeys.STR_ERROR_SEPARATOR)).append(StringPool.get(th instanceof IllegalArgumentException ? 947 : th instanceof ConnectionNotFoundException ? 948 : th instanceof IOException ? 949 : th instanceof SecurityException ? 950 : 463)));
        }

        // Retry on IOException (network errors), not on SecurityException or auth errors
        if (th instanceof IOException && this.retryCount < MAX_RETRIES) {
            this.retryCount++;
            RemoteLogger.log("ACCT", "handleConnError login=" + this.login + " retry " + this.retryCount + "/" + MAX_RETRIES + " err=" + errorMsg);
            closeConnection();
            try { Thread.sleep(RETRY_DELAY_MS); } catch (Throwable unused) {}
            this.progress = PROGRESS_STARTING;
            return;
        }

        RemoteLogger.log("ACCT", "handleConnError login=" + this.login + " err=" + errorMsg);
        EventDispatcher.postAccountMessage(this, errorMsg);
        closeConnection();
        this.retryCount = 0;
        this.lastError = getDefaultError();
    }

    public final void handleTimeout() {
        RemoteLogger.log("ACCT", "handleTimeout login=" + this.login);
        EventDispatcher.postAccountError(this, 462);
        closeConnection();
        this.lastError = getDefaultError();
    }

    public final String getFormattedName() {
        return this.authMode != 3 ? this.password : ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append((char) (this.password.charAt(0) + ' ')).append(StringUtils.suffix(this.password, 1)));
    }

    public final void handleComplete() {
        closeConnection();
        this.lastError = getDefaultError();
        if (this.authMode == 2) {
            connect(3);
        } else {
            EventDispatcher.postAccountError(this, 461);
        }
    }

    public final void handleError(int errorCode) {
        EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_ACCOUNT_CONNECTED)).append(this).append(StringPool.get(StringResKeys.STR_ACCOUNT_SEPARATOR)).append(StringPool.get(StringResKeys.STR_MESSAGE_SEPARATOR)).append(errorCode)));
        closeConnection();
        this.lastError = getDefaultError();
    }

    public final void removeAllContacts() {
        for (int i = this.groups.size() - 1; i >= 0; i--) {
            ContactGroup group = getGroup(i);
            for (int j = group.contacts.size() - 1; j >= 0; j--) {
                Contact contact = group.getContact(j);
                removeContact(contact, false);
                notifyContactDeactivated(contact);
            }
            removeGroup(group);
        }
    }

    public final void markAllRead() {
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            ((Contact) elements.nextElement()).clearUnread();
        }
        notifyContactListUpdated();
    }

    public final Contact getContact(Object obj) {
        return (Contact) this.contactMap.get(obj);
    }

    public final void deleteContact(String contactId) {
        Contact contact = getContact((Object) contactId);
        if (contact == null || contact.isOnline() || contact.hasUnread() || contact.isSystem()) {
            return;
        }
        notifyContactDeleted(contact);
        UIState.getPendingConnections().addElement(contact);
        contact.statusCode = RuntimeState.getCurrentTimestamp();
        contact.dirty = true;
    }

    public final void markRead(String contactId) {
        Contact contact = getContact((Object) contactId);
        if (contact != null) {
            notifyContactDeleted(contact);
        }
    }

    public final void onMessage(String contactId, long timestamp, String messageText) {
        Contact contact = getContact((Object) contactId);
        Contact target = contact;
        if (contact == null) {
            target = newContact(contactId);
        }
        this.recvCount++;
        target.receiveMessageFull(timestamp, messageText, 1);
    }

    public abstract Contact newContact(String contactAddress);

    public final void updateStatus(String contactId, long timestamp, int flag) {
        Contact contact = getContact((Object) contactId);
        if (contact != null) {
            contact.updateMessageFlag(timestamp, flag);
        }
    }

    public int validateSend(Contact contact, String message, long timestamp) {
        return isConnected() ? 0 : ERROR_DISCONNECTED;
    }

    public final int removeContact(Contact contact, boolean removeFromGroups) {
        if (contact == null) {
            return 0;
        }
        Enumeration keys = this.contactMap.keys();
        while (true) {
            if (!keys.hasMoreElements()) {
                break;
            }
            Hashtable hashtable = this.contactMap;
            Object key = keys.nextElement();
            if (hashtable.get(key) == contact) {
                this.contactMap.remove(key);
                break;
            }
        }
        for (int i = this.groups.size() - 1; i >= 0; i--) {
            getGroup(i).removeElement(contact);
        }
        this.defaultGroup.removeElement(contact);
        notifyContactDeactivated(contact);
        return 0;
    }

    public int validateGroupAdd(String contactAddress, String displayName, String authMessage, ContactGroup group, boolean requestAuth) {
        if (!isConnected()) {
            return ERROR_DISCONNECTED;
        }
        if (StringUtils.isEmpty(displayName)) {
            return 301;
        }
        return StringUtils.isEmpty(authMessage) ? 302 : 0;
    }

    public int validateModify(Contact contact, Object[] fieldValues) {
        if (isConnected()) {
            return StringUtils.isEmpty((String) fieldValues[0]) ? 301 : 0;
        }
        return ERROR_DISCONNECTED;
    }

    public abstract int validateObject(Object searchFields);

    public abstract int validateDelete(Contact contact);

    public int validateGroupCreate(String groupName) {
        if (isConnected()) {
            return StringUtils.isEmpty(groupName) ? 301 : 0;
        }
        return ERROR_DISCONNECTED;
    }

    public int validateGroupRename(ContactGroup group, String newName) {
        if (group == this.defaultGroup || group == this.onlineGroup || group == this.specialGroup || group == this.offlineGroup) {
            return 304;
        }
        if (isConnected()) {
            return StringUtils.isEmpty(newName) ? 301 : 0;
        }
        return ERROR_DISCONNECTED;
    }

    public int setCredentials(String newLogin, String newPassword) {
        if (StringUtils.isEmpty(newLogin)) {
            return 301;
        }
        if (this.login.equals(newLogin) && this.password.equals(newPassword)) {
            return 0;
        }
        if (isConnecting()) {
            return 300;
        }
        this.login = newLogin;
        this.shortName = Utils.beforeAt(newLogin);
        this.password = newPassword;
        return 0;
    }

    public int validateGroupDelete(ContactGroup group) {
        if (group == this.defaultGroup || group == this.onlineGroup) {
            return 304;
        }
        return group.contacts.size() > 0 ? 303 : 0;
    }

    public int validateResend(Contact contact) {
        return (contact.isOnline() || isConnected()) ? 0 : ERROR_DISCONNECTED;
    }

    public final int getResourceId(Object key) {
        return ProfileScreen.loadUserProfile((String) key, this);
    }

    public abstract int validateContactDelete(Contact contact);

    public abstract int validateContactBlock(Contact contact);

    public abstract int validateContactUnblock(Contact contact);

    public int validateContactResend(Contact contact) {
        return !isConnected() ? ERROR_DISCONNECTED : 0;
    }

    public final Vector getUnreadContacts() {
        Vector result = ObjectPool.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (contact.hasUnread()) {
                result.addElement(contact);
            }
        }
        return result;
    }

    public final Vector getOfflineContacts() {
        Vector result = ObjectPool.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (contact.isOffline()) {
                result.addElement(contact);
            }
        }
        return result;
    }

    public Vector getPendingContacts() {
        return ObjectPool.newVector();
    }

    public final Vector getAllContacts() {
        Vector result = ObjectPool.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            result.addElement(elements.nextElement());
        }
        return result;
    }

    public int validateMove(Contact contact, ContactGroup group, ContactGroup toGroup) {
        if (group == toGroup) {
            return 305;
        }
        return !isConnected() ? ERROR_DISCONNECTED : 0;
    }

    public final Vector getOnlineContacts() {
        Vector result = ObjectPool.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (contact.isOnline()) {
                result.addElement(contact);
            }
        }
        return result;
    }

    public final ContactGroup findGroup(Contact contact) {
        ContactGroup group;
        if (contact.isOnline() || this.defaultGroup.containsContact(contact)) {
            return this.defaultGroup;
        }
        if (contact.isSystem()) {
            return this.specialGroup;
        }
        if (contact.hasUnread()) {
            return this.onlineGroup;
        }
        if (contact.isOffline()) {
            return this.offlineGroup;
        }
        for (int i = this.groups.size() - 1; i >= 0; i--) {
            group = getGroup(i);
            if (group.containsContact(contact)) {
                return group;
            }
        }
        return null;
    }

    public final void registerContact(Contact contact) {
        Contact existing = (Contact) this.contactMap.get(contact.getIdentifier());
        if (existing != null && existing != contact) {
            this.defaultGroup.removeContact(existing);
            this.onlineGroup.removeContact(existing);
            this.offlineGroup.removeContact(existing);
            this.blockedGroup.removeContact(existing);
            this.specialGroup.removeContact(existing);
        }
        this.contactMap.put(contact.getIdentifier(), contact);
    }

    public final ContactGroup getGroup(int index) {
        return (ContactGroup) this.groups.elementAt(index);
    }

    public final void addGroup(ContactGroup group) {
        this.groups.addElement(group);
    }

    public final void removeGroup(ContactGroup group) {
        this.groups.removeElement(group);
    }

    public abstract void onError(int errorCode);

    public final String toString() {
        return this.login;
    }

    public int getExtType() {
        return -1;
    }

    public final void incrementSync() {
        this.password = getFormattedName();
        this.syncSeq++;
    }

    public void resetCounters() {
        this.sentCount = 0;
        this.recvCount = 0;
    }

    public int getSessionStringKey() {
        return 0;
    }

    // --- Protocol capability methods (virtual, override in subclasses) ---

    // Whether this account is any XMPP type (TYPE_XMPP or TYPE_XMPP_MAILRU)
    public boolean isXmppType() {
        int t = getType();
        return t == TYPE_XMPP || t == TYPE_XMPP_MAILRU;
    }

    // Whether this account is an XMPP Mail.ru variant
    public boolean isMailRuVariant() { return false; }

    // Whether this account has a custom domain (MRIM)
    public boolean hasCustomDomain() { return false; }

    // Handle status change for the given option index. Returns screen ID.
    public int handleStatusOption(int optionIndex) { return 0; }

    // Whether this account supports chat rooms (MRIM)
    public boolean supportsChatRooms() { return false; }

    // Whether this protocol supports vCard/profile info
    public boolean supportsVCard() { return false; }

    // Whether this protocol supports file transfer
    public boolean supportsFileTransfer() { return false; }

    // Nickname for outgoing messages (MRIM -> chatRoomManager.nickname)
    public String getNickname() { return this.login; }

    // Auth cookie/ID for HTTP requests (MRIM -> jabberId)
    public String getAuthCookie() { return null; }
    public void setAuthCookie(String cookie) {}

    // Icon resource ID for status display (MMP uses custom emoticon icons)
    public int getIconResourceId() { return getIconId(); }

    // Set emoticon selection (MMP reserved2 field)
    public void setEmoticonSelection(int optionId) {}

    // MMP pending version for privacy settings
    public int getPendingVersion() { return 0; }

    // Schedule MMP version update. Returns result code.
    public int scheduleVersionUpdate(int version) { return 0; }

    // Add new contact for this protocol (XMPP). Returns error code.
    public int addNewContact() { return 0; }

    // Map highlight state (MRIM profiles on map)
    public boolean isMapHighlighted() { return false; }
    public void setMapHighlighted(boolean value) {}

    // Send telemetry report
    public void sendTelemetryReport() {}

    // Perform profile sync
    public void syncProfile() {}

    // Set presence feature on a contact (XMPP)
    public void setContactPresenceFeature(Contact contact, int feature) {}

    // Check if a message has been read in any chat room (MRIM)
    public boolean isMessageReadInAnyRoom(String messageId) { return false; }

    // Whether this is the last/active chat room (MRIM)
    public boolean isLastChatRoom(Object chatRoom) { return false; }

    // Find a chat room by name
    public ChatRoom findChatRoomByName(String name) { return null; }

    // Find a chat room by ID
    public ChatRoom findChatRoomById(int id) { return null; }

    // Number of chat rooms
    public int getChatRoomCount() { return 0; }

    // Whether chat rooms have been loaded from server
    public boolean areChatRoomsLoaded() { return false; }

    // All chat rooms (excluding the default/last one)
    public Vector getChatRooms() { return null; }

    // Get the last (default/aggregate) chat room
    public ChatRoom getLastChatRoom() { return null; }

    // Find the default named chat room
    public ChatRoom findDefaultChatRoom() { return null; }

    // Parse chat rooms from server response
    public void parseChatRoomsFromJson(Object payload) {}


    // Show chat room selector UI
    public void showChatRoomSelector() {}

    // Submit async chat room HTTP request
    public void sendChatRoomRequest(Object[] request) {}

    // Create a new chat room with given members. Returns error code or 0.
    public int createChatRoom(String name, Vector members, boolean includeOwner) { return 0; }

    // Apply protocol configuration (status/emoticon). Returns error code or 0.
    public int applyConfiguration(int config) { return 0; }

    // Send SMS via protocol. Returns error code or 0.
    public int sendSmsMessage(Contact contact, String phone, String message) { return 0; }

    // Send blog post. Returns error code or 0.
    public int sendBlogPost(String message, boolean isReply, long timestamp) { return 0; }

    // Whether this account's profile has map coordinates
    public boolean hasProfileCoordinates() { return false; }

    // Set map location on profile (MRIM)
    public void setProfileMapLocation(Object mapPoint) {}

    // Set simple lat/lon location on profile (MRIM)
    public void setProfileSimpleLocation(String longitude, String latitude) {}

    // Mark chat rooms as loaded
    public void setChatRoomsLoaded() {}

    // Add offline contact by identifier (MRIM)
    public void addOfflineContact(String contactId) {}

    // Get default chat room ID
    public int getDefaultChatRoomId() { return 0; }

    // Remove a user from chat rooms
    public void removeChatRoomUser(String userId) {}

    // Profile gender/location visibility state (MRIM)
    public int getProfileGender() { return 0; }

    // Clear the last (default) chat room's data (MRIM)
    public void clearLastChatRoom() {}

    // Location visibility: publish to all (MRIM)
    public void publishLocation() {}

    // Location visibility: hide from all (MRIM)
    public void hideLocation() {}

    // Location visibility: show to selected groups (MRIM)
    public void setLocationGroups() {}

    // Location visibility: clear group restrictions (MRIM)
    public void clearLocationGroups() {}

    // Whether this account's profile is selected for map display (MRIM)
    public boolean isProfileSelected() { return false; }

    // Return this account as a ListItem for map display, or null (MRIM)
    public ListItem asListItem() { return null; }

    // Whether this account's profile needs refresh on map (MRIM)
    public boolean isProfileDirty() { return false; }

    // Search for a user by search entry (MRIM)
    public void performUserSearch(SearchEntry searchEntry) {}

}
