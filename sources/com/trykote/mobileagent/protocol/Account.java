package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;

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

    public Account(int accountId, String login, String password) {
        this.groups = ObjectPool.newVector();
        this.accountId = accountId;
        this.login = login;
        this.displayName = login;
        this.password = password;
        this.syncArray = new int[9];
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
        for (int i = 2; i < 9; i++) {
            this.syncArray[i] = buffer.readInt();
        }
        this.configFlags = buffer.readInt();
        this.displayName = buffer.readUTF8Str((String) null);
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
        if (buffer.readInt() == 12) {
            this.syncSeq = buffer.readInt();
            this.sentCount = buffer.readInt();
            this.recvCount = buffer.readInt();
            buffer.readInt();
        }
    }

    public void saveProperties(ByteBuffer buffer) {
        buffer.writeIntLE(12).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount).writeIntLE(0);
    }

    public Account serializeAccount(ByteBuffer buffer, boolean includeGroups, boolean includePrivate) {
        buffer.writeByte(getType() | 8).writeIntLE(this.accountId).writeStringLatin1(this.login).writeStringLatin1(this.password).writeIntLE(0);
        for (int i = 2; i < 9; i++) {
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
            int remaining = size;
            buffer.writeIntLE(size);
            while (true) {
                remaining--;
                if (remaining < 0) {
                    break;
                }
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
        return 299;
    }

    public final int sendData(ByteBuffer buffer) {
        AccountManager.recordOutboundTraffic(this, buffer.length);
        ConnectionThread conn = this.connection;
        if (conn.exception != null) {
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
        if (null == th) {
            errorMsg = AppState.getString(StringResKeys.STR_TIMEOUT_ERROR);
        } else {
            errorMsg = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(th).append(AppState.getString(StringResKeys.STR_ERROR_SEPARATOR)).append(AppState.getString(th instanceof IllegalArgumentException ? 947 : th instanceof ConnectionNotFoundException ? 948 : th instanceof IOException ? 949 : th instanceof SecurityException ? 950 : 463)));
        }
        RemoteLogger.log("ACCT", "handleConnError login=" + this.login + " err=" + errorMsg);
        EventDispatcher.postAccountMessage(this, errorMsg);
        closeConnection();
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
        EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_ACCOUNT_CONNECTED)).append(this).append(AppState.getString(StringResKeys.STR_ACCOUNT_SEPARATOR)).append(AppState.getString(StringResKeys.STR_MESSAGE_SEPARATOR)).append(errorCode)));
        closeConnection();
        this.lastError = getDefaultError();
    }

    public final void removeAllContacts() {
        int size = this.groups.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            ContactGroup group = getGroup(size);
            int size2 = group.contacts.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                Contact contact = group.getContact(size2);
                removeContact(contact, false);
                ContactListManager.markContactUnread(contact);
            }
            removeGroup(group);
        }
    }

    public final void markAllRead() {
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            ((Contact) elements.nextElement()).clearUnread();
        }
        AppController.needsLayoutUpdate = true;
    }

    public final Contact getContact(Object obj) {
        return (Contact) this.contactMap.get(obj);
    }

    public final void deleteContact(String contactId) {
        Contact contact = getContact((Object) contactId);
        if (contact == null || contact.isOnline() || contact.hasUnread() || contact.isSystem()) {
            return;
        }
        ContactListManager.deleteContact(contact);
        AppState.getVector(UIKeys.VEC_PENDING_CONNECTIONS).addElement(contact);
        contact.statusCode = AppState.getInt(RuntimeKeys.INT_CURRENT_TIMESTAMP);
        contact.dirty = true;
    }

    public final void markRead(String contactId) {
        Contact contact = getContact((Object) contactId);
        if (contact != null) {
            ContactListManager.deleteContact(contact);
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
        return isConnected() ? 0 : 299;
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
        int size = this.groups.size();
        while (true) {
            size--;
            if (size < 0) {
                this.defaultGroup.removeElement(contact);
                ContactListManager.markContactUnread(contact);
                return 0;
            }
            getGroup(size).removeElement(contact);
        }
    }

    public int validateGroupAdd(String contactAddress, String displayName, String authMessage, ContactGroup group, boolean requestAuth) {
        if (!isConnected()) {
            return 299;
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
        return 299;
    }

    public abstract int validateObject(Object searchFields);

    public abstract int validateDelete(Contact contact);

    public int validateGroupCreate(String groupName) {
        if (isConnected()) {
            return StringUtils.isEmpty(groupName) ? 301 : 0;
        }
        return 299;
    }

    public int validateGroupRename(ContactGroup group, String newName) {
        if (group == this.defaultGroup || group == this.onlineGroup || group == this.specialGroup || group == this.offlineGroup) {
            return 304;
        }
        if (isConnected()) {
            return StringUtils.isEmpty(newName) ? 301 : 0;
        }
        return 299;
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
        return (contact.isOnline() || isConnected()) ? 0 : 299;
    }

    public final int getResourceId(Object key) {
        return ResourceManager.loadUserProfile((String) key, this);
    }

    public abstract int validateContactDelete(Contact contact);

    public abstract int validateContactBlock(Contact contact);

    public abstract int validateContactUnblock(Contact contact);

    public int validateContactResend(Contact contact) {
        return !isConnected() ? 299 : 0;
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
        return !isConnected() ? 299 : 0;
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
        int size = this.groups.size();
        do {
            size--;
            if (size < 0) {
                return null;
            }
            group = getGroup(size);
        } while (!group.containsContact(contact));
        return group;
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
}
