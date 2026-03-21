package p000;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;

/* renamed from: h */
/* loaded from: MobileAgent_3.9.jar:h.class */
public abstract class Account {

    /* renamed from: i */
    public final Vector groups;

    /* renamed from: j */
    public int accountId;

    /* renamed from: k */
    public String login;

    /* renamed from: l */
    public String password;

    /* renamed from: m */
    public final int[] syncArray;

    /* renamed from: n */
    public final ByteBuffer dataBuffer;

    /* renamed from: o */
    public int state;

    /* renamed from: p */
    public ConnectionThread connection;

    /* renamed from: q */
    public final Hashtable contactMap;

    /* renamed from: r */
    public int progress;

    /* renamed from: s */
    public int msgCount;

    /* renamed from: t */
    public int lastError;

    /* renamed from: u */
    public int configFlags;

    /* renamed from: v */
    public int syncSeq;

    /* renamed from: w */
    public int sentCount;

    /* renamed from: x */
    public int recvCount;

    /* renamed from: y */
    public int reserved1;

    /* renamed from: z */
    public long timeout;

    /* renamed from: A */
    public long deadline;

    /* renamed from: B */
    public int reserved2;

    /* renamed from: C */
    public final Vector extras;

    /* renamed from: D */
    public ContactGroup defaultGroup;

    /* renamed from: E */
    public final ContactGroup onlineGroup;

    /* renamed from: F */
    public final ContactGroup offlineGroup;

    /* renamed from: G */
    public final ContactGroup blockedGroup;

    /* renamed from: H */
    public final ContactGroup specialGroup;

    /* renamed from: I */
    public String displayName;

    /* renamed from: J */
    public String shortName;

    /* renamed from: a */
    private int authMode;

    public Account(int i, String str, String str2) {
        this.groups = NetworkUtils.newVector();
        this.accountId = i;
        this.login = str;
        this.displayName = str;
        this.password = str2;
        this.syncArray = new int[9];
        this.dataBuffer = new ByteBuffer();
        this.contactMap = new Hashtable();
        this.extras = NetworkUtils.newVector();
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
        this.shortName = Utils.beforeAt(str);
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

    /* renamed from: q */
    public final ByteBuffer encodeId() {
        return new ByteBuffer().writeIntAsString(this.accountId).writeByte(95);
    }

    /* renamed from: A */
    public final String getSignature() {
        return new ByteBuffer().writeByte(35).writeIntAsString(this.accountId).writeByte(35).writeRawString(this.login).readAllByteStr();
    }

    /* renamed from: b */
    public abstract ContactGroup createOnlineGroup();

    /* renamed from: d */
    public abstract ContactGroup createOfflineGroup();

    /* renamed from: c */
    public abstract ContactGroup createBlockedGroup();

    /* renamed from: e */
    public abstract ContactGroup createSpecialGroup();

    /* renamed from: h */
    public abstract int getIconId();

    /* renamed from: a */
    public void loadProperties(ByteBuffer buffer) {
        if (buffer.readInt() == 12) {
            this.syncSeq = buffer.readInt();
            this.sentCount = buffer.readInt();
            this.recvCount = buffer.readInt();
            buffer.readInt();
        }
    }

    /* renamed from: b */
    public void saveProperties(ByteBuffer buffer) {
        buffer.writeIntLE(12).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount).writeIntLE(0);
    }

    /* renamed from: a */
    public Account serializeAccount(ByteBuffer buffer, boolean z, boolean z2) {
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
        if (z2) {
            if (!z) {
                this.groups.removeAllElements();
            }
            int size = this.groups.size();
            int i2 = size;
            buffer.writeIntLE(size);
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                }
                getGroup(i2).serialize(buffer, true);
            }
            this.defaultGroup.serialize(buffer, true);
            this.contactMap.clear();
        } else {
            this.defaultGroup.serialize(buffer.writeIntLE(0), false);
        }
        return this;
    }

    /* renamed from: c */
    public final int trySendData(ByteBuffer buffer) {
        if (isConnected()) {
            return sendData(buffer);
        }
        return 299;
    }

    /* renamed from: d */
    public final int sendData(ByteBuffer buffer) {
        AppController.setAccountOption(this, buffer.length);
        ConnectionThread conn = this.connection;
        if (conn.exception != null) {
            throw new RuntimeException();
        }
        ByteBuffer outBuf = conn.outBuffer;
        int i = buffer.length;
        if (i > 0) {
            synchronized (outBuf) {
                outBuf.ensureCapacity(i);
                Utils.arraycopy((Object) buffer.data, buffer.offset, (Object) outBuf.data, outBuf.length, i);
                buffer.clear();
                outBuf.length += i;
                outBuf.compact();
            }
        }
        if (this.timeout <= 0) {
            return 0;
        }
        this.deadline = System.currentTimeMillis() + this.timeout;
        return 0;
    }

    /* renamed from: c */
    public final Account setDisplayName(String str) {
        this.displayName = Utils.defaultIfBlank(str, this.login);
        return this;
    }

    /* renamed from: B */
    public final boolean isConnecting() {
        return this.progress > 0;
    }

    /* renamed from: C */
    public final boolean isConnected() {
        return this.progress == 100;
    }

    /* renamed from: D */
    public final MenuItem createMenuItem() {
        MenuItem item = MenuItem.create(getSignature()).setIcon(getIconId()).setLabel(this.login);
        item.data = this;
        return item;
    }

    /* renamed from: E */
    public final MenuItem createFlagMenuItem() {
        MenuItem item = MenuItem.create(getSignature()).setIcon(getIconId()).setLabel(this.login).setIcon(244);
        item.enabled = true;
        item.data = this;
        return item;
    }

    /* renamed from: a */
    public abstract int getType();

    /* renamed from: a_ */
    public int connect(int i) {
        if (isConnecting()) {
            return 297;
        }
        if (i == 0) {
            char firstChar = this.password.charAt(0);
            this.authMode = (firstChar < 'A' || firstChar > 'Z') ? 1 : 2;
        } else {
            this.authMode = i;
        }
        this.msgCount = 0;
        this.progress = 1;
        return 0;
    }

    /* renamed from: l */
    public int disconnect() {
        return !isConnecting() ? 298 : 0;
    }

    /* renamed from: e */
    public final void resetSyncIfChanged(int i) {
        int[] iArr = this.syncArray;
        int i2 = iArr[8];
        if (i != i2) {
            iArr[2] = 0;
            iArr[3] = 0;
            if ((i >>> 8) != (i2 >>> 8)) {
                iArr[4] = 0;
                iArr[5] = 0;
            }
            iArr[8] = i;
        }
    }

    /* renamed from: a */
    public final int getSyncValue(int i, int i2) {
        return this.syncArray[i + i + i2];
    }

    /* renamed from: i */
    public abstract void loadData() throws Throwable;

    /* renamed from: g */
    public abstract int getDefaultError();

    /* renamed from: F */
    public final void closeConnection() {
        if (this.connection != null) {
            this.connection.state = 3;
        }
        this.connection = null;
        this.progress = 0;
    }

    /* renamed from: G */
    public final void handleConnError() {
        String errorMsg;
        Throwable th = this.connection.exception;
        if (null == th) {
            errorMsg = AppState.getString(951);
        } else {
            errorMsg = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(th).append(AppState.getString(946)).append(AppState.getString(th instanceof IllegalArgumentException ? 947 : th instanceof ConnectionNotFoundException ? 948 : th instanceof IOException ? 949 : th instanceof SecurityException ? 950 : 463)));
        }
        IOUtils.postAccountMessage(this, errorMsg);
        closeConnection();
        this.lastError = getDefaultError();
    }

    /* renamed from: H */
    public final void handleTimeout() {
        IOUtils.postAccountError(this, 462);
        closeConnection();
        this.lastError = getDefaultError();
    }

    /* renamed from: I */
    public final String getFormattedName() {
        return this.authMode != 3 ? this.password : NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append((char) (this.password.charAt(0) + ' ')).append(StringUtils.suffix(this.password, 1)));
    }

    /* renamed from: J */
    public final void handleComplete() {
        closeConnection();
        this.lastError = getDefaultError();
        if (this.authMode == 2) {
            connect(3);
        } else {
            IOUtils.postAccountError(this, 461);
        }
    }

    /* renamed from: f */
    public final void handleError(int i) {
        IOUtils.postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(459)).append(this).append(AppState.getString(460)).append(AppState.getString(457)).append(i)));
        closeConnection();
        this.lastError = getDefaultError();
    }

    /* renamed from: K */
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
                AppController.markContactUnread(contact);
            }
            removeGroup(group);
        }
    }

    /* renamed from: L */
    public final void markAllRead() {
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            ((Contact) elements.nextElement()).clearUnread();
        }
        AppController.needsLayoutUpdate = true;
    }

    /* renamed from: c */
    public final Contact getContact(Object obj) {
        return (Contact) this.contactMap.get(obj);
    }

    /* renamed from: d */
    public final void deleteContact(String str) {
        Contact contact = getContact((Object) str);
        if (contact == null || contact.isOnline() || contact.hasUnread() || contact.isSystem()) {
            return;
        }
        AppController.deleteContact(contact);
        AppState.getVector(1242).addElement(contact);
        contact.statusCode = AppState.getInt(1531);
        contact.dirty = true;
    }

    /* renamed from: e */
    public final void markRead(String str) {
        Contact contact = getContact((Object) str);
        if (contact != null) {
            AppController.deleteContact(contact);
        }
    }

    /* renamed from: a */
    public final void onMessage(String str, long j, String str2) {
        Contact contact = getContact((Object) str);
        Contact target = contact;
        if (contact == null) {
            target = newContact(str);
        }
        this.recvCount++;
        target.receiveMessageFull(j, str2, 1);
    }

    /* renamed from: b */
    public abstract Contact newContact(String str);

    /* renamed from: a */
    public final void updateStatus(String str, long j, int i) {
        Contact contact = getContact((Object) str);
        if (contact != null) {
            contact.updateMessageFlag(j, i);
        }
    }

    /* renamed from: a */
    public int validateSend(Contact contact, String str, long j) {
        return isConnected() ? 0 : 299;
    }

    /* renamed from: a */
    public final int removeContact(Contact contact, boolean z) {
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
                AppController.markContactUnread(contact);
                return 0;
            }
            getGroup(size).removeElement(contact);
        }
    }

    /* renamed from: a */
    public int validateGroupAdd(String str, String str2, String str3, ContactGroup group, boolean z) {
        if (!isConnected()) {
            return 299;
        }
        if (StringUtils.isEmpty(str2)) {
            return 301;
        }
        return StringUtils.isEmpty(str3) ? 302 : 0;
    }

    /* renamed from: a */
    public int validateModify(Contact contact, Object[] objArr) {
        if (isConnected()) {
            return StringUtils.isEmpty((String) objArr[0]) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: b */
    public abstract int validateObject(Object obj);

    /* renamed from: a */
    public abstract int validateDelete(Contact contact);

    /* renamed from: a */
    public int validateGroupCreate(String str) {
        if (isConnected()) {
            return StringUtils.isEmpty(str) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: a */
    public int validateGroupRename(ContactGroup group, String str) {
        if (group == this.defaultGroup || group == this.onlineGroup || group == this.specialGroup || group == this.offlineGroup) {
            return 304;
        }
        if (isConnected()) {
            return StringUtils.isEmpty(str) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: a */
    public int setCredentials(String str, String str2) {
        if (StringUtils.isEmpty(str)) {
            return 301;
        }
        if (this.login.equals(str) && this.password.equals(str2)) {
            return 0;
        }
        if (isConnecting()) {
            return 300;
        }
        this.login = str;
        this.shortName = Utils.beforeAt(str);
        this.password = str2;
        return 0;
    }

    /* renamed from: a */
    public int validateGroupDelete(ContactGroup group) {
        if (group == this.defaultGroup || group == this.onlineGroup) {
            return 304;
        }
        return group.contacts.size() > 0 ? 303 : 0;
    }

    /* renamed from: b */
    public int validateResend(Contact contact) {
        return (contact.isOnline() || isConnected()) ? 0 : 299;
    }

    /* renamed from: d */
    public final int getResourceId(Object obj) {
        return ResourceManager.loadUserProfile((String) obj, this);
    }

    /* renamed from: c */
    public abstract int validateContactDelete(Contact contact);

    /* renamed from: d */
    public abstract int validateContactBlock(Contact contact);

    /* renamed from: e */
    public abstract int validateContactUnblock(Contact contact);

    /* renamed from: f */
    public int validateContactResend(Contact contact) {
        return !isConnected() ? 299 : 0;
    }

    /* renamed from: M */
    public final Vector getUnreadContacts() {
        Vector result = NetworkUtils.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (contact.hasUnread()) {
                result.addElement(contact);
            }
        }
        return result;
    }

    /* renamed from: N */
    public final Vector getOfflineContacts() {
        Vector result = NetworkUtils.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (contact.isOffline()) {
                result.addElement(contact);
            }
        }
        return result;
    }

    /* renamed from: O */
    public Vector getPendingContacts() {
        return NetworkUtils.newVector();
    }

    /* renamed from: P */
    public final Vector getAllContacts() {
        Vector result = NetworkUtils.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            result.addElement(elements.nextElement());
        }
        return result;
    }

    /* renamed from: a */
    public int validateMove(Contact contact, ContactGroup group, ContactGroup toGroup) {
        if (group == toGroup) {
            return 305;
        }
        return !isConnected() ? 299 : 0;
    }

    /* renamed from: Q */
    public final Vector getOnlineContacts() {
        Vector result = NetworkUtils.newVector();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (contact.isOnline()) {
                result.addElement(contact);
            }
        }
        return result;
    }

    /* renamed from: g */
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

    /* renamed from: h */
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

    /* renamed from: g */
    public final ContactGroup getGroup(int i) {
        return (ContactGroup) this.groups.elementAt(i);
    }

    /* renamed from: b */
    public final void addGroup(ContactGroup group) {
        this.groups.addElement(group);
    }

    /* renamed from: c */
    public final void removeGroup(ContactGroup group) {
        this.groups.removeElement(group);
    }

    /* renamed from: c */
    public abstract void onError(int i);

    public final String toString() {
        return this.login;
    }

    /* renamed from: n */
    public int getExtType() {
        return -1;
    }

    /* renamed from: R */
    public final void incrementSync() {
        this.password = getFormattedName();
        this.syncSeq++;
    }

    /* renamed from: o */
    public void resetCounters() {
        this.sentCount = 0;
        this.recvCount = 0;
    }

    /* renamed from: p */
    public int mo110p() {
        return 0;
    }
}
