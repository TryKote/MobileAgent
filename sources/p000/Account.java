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
        ContactGroup abstractC0046qMo85b = createOnlineGroup();
        abstractC0046qMo85b.isSpecial = true;
        this.onlineGroup = abstractC0046qMo85b;
        ContactGroup abstractC0046qMo87d = createOfflineGroup();
        abstractC0046qMo87d.isSpecial = true;
        this.offlineGroup = abstractC0046qMo87d;
        ContactGroup abstractC0046qMo86c = createBlockedGroup();
        abstractC0046qMo86c.isSpecial = true;
        this.blockedGroup = abstractC0046qMo86c;
        ContactGroup abstractC0046qMo88e = createSpecialGroup();
        abstractC0046qMo88e.isSpecial = true;
        this.specialGroup = abstractC0046qMo88e;
        this.shortName = Utils.beforeAt(str);
    }

    public Account(ByteBuffer c0043n) {
        this(c0043n.readInt(), c0043n.readHexStr(), c0043n.readWideStr());
        c0043n.readInt();
        for (int i = 2; i < 9; i++) {
            this.syncArray[i] = c0043n.readInt();
        }
        this.configFlags = c0043n.readInt();
        this.displayName = c0043n.readUTF8Str((String) null);
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
    public void loadProperties(ByteBuffer c0043n) {
        if (c0043n.readInt() == 12) {
            this.syncSeq = c0043n.readInt();
            this.sentCount = c0043n.readInt();
            this.recvCount = c0043n.readInt();
            c0043n.readInt();
        }
    }

    /* renamed from: b */
    public void saveProperties(ByteBuffer c0043n) {
        c0043n.writeIntLE(12).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount).writeIntLE(0);
    }

    /* renamed from: a */
    public Account serializeAccount(ByteBuffer c0043n, boolean z, boolean z2) {
        c0043n.writeByte(getType() | 8).writeIntLE(this.accountId).writeStringLatin1(this.login).writeStringLatin1(this.password).writeIntLE(0);
        for (int i = 2; i < 9; i++) {
            c0043n.writeIntLE(this.syncArray[i]);
        }
        c0043n.writeIntLE(this.configFlags);
        if (this.displayName != null) {
            c0043n.writeStringUTF16(this.displayName);
        } else {
            c0043n.writeIntLE(0);
        }
        if (z2) {
            if (!z) {
                this.groups.removeAllElements();
            }
            int size = this.groups.size();
            int i2 = size;
            c0043n.writeIntLE(size);
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                }
                getGroup(i2).serialize(c0043n, true);
            }
            this.defaultGroup.serialize(c0043n, true);
            this.contactMap.clear();
        } else {
            this.defaultGroup.serialize(c0043n.writeIntLE(0), false);
        }
        return this;
    }

    /* renamed from: c */
    public final int trySendData(ByteBuffer c0043n) {
        if (isConnected()) {
            return sendData(c0043n);
        }
        return 299;
    }

    /* renamed from: d */
    public final int sendData(ByteBuffer c0043n) {
        AppController.m420b(this, c0043n.length);
        ConnectionThread c0039j = this.connection;
        if (c0039j.exception != null) {
            throw new RuntimeException();
        }
        ByteBuffer c0043n2 = c0039j.outBuffer;
        int i = c0043n.length;
        if (i > 0) {
            synchronized (c0043n2) {
                c0043n2.ensureCapacity(i);
                Utils.arraycopy((Object) c0043n.data, c0043n.offset, (Object) c0043n2.data, c0043n2.length, i);
                c0043n.clear();
                c0043n2.length += i;
                c0043n2.compact();
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
        this.displayName = Utils.m528a(str, this.login);
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
        MenuItem c0032cM898b = MenuItem.m887a(getSignature()).m896a(getIconId()).m898b(this.login);
        c0032cM898b.f265d = this;
        return c0032cM898b;
    }

    /* renamed from: E */
    public final MenuItem createFlagMenuItem() {
        MenuItem c0032cM896a = MenuItem.m887a(getSignature()).m896a(getIconId()).m898b(this.login).m896a(244);
        c0032cM896a.f266e = true;
        c0032cM896a.f265d = this;
        return c0032cM896a;
    }

    /* renamed from: a */
    public abstract int getType();

    /* renamed from: a_ */
    public int connect(int i) {
        if (isConnecting()) {
            return 297;
        }
        if (i == 0) {
            char cCharAt = this.password.charAt(0);
            this.authMode = (cCharAt < 'A' || cCharAt > 'Z') ? 1 : 2;
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
        String strM1215a;
        Throwable th = this.connection.exception;
        if (null == th) {
            strM1215a = AppState.getString(951);
        } else {
            strM1215a = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(th).append(AppState.getString(946)).append(AppState.getString(th instanceof IllegalArgumentException ? 947 : th instanceof ConnectionNotFoundException ? 948 : th instanceof IOException ? 949 : th instanceof SecurityException ? 950 : 463)));
        }
        IOUtils.m784a(this, strM1215a);
        closeConnection();
        this.lastError = getDefaultError();
    }

    /* renamed from: H */
    public final void handleTimeout() {
        IOUtils.m783a(this, 462);
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
            IOUtils.m783a(this, 461);
        }
    }

    /* renamed from: f */
    public final void handleError(int i) {
        IOUtils.m778d((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(459)).append(this).append(AppState.getString(460)).append(AppState.getString(457)).append(i)));
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
            ContactGroup abstractC0046qM1082g = getGroup(size);
            int size2 = abstractC0046qM1082g.contacts.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                Contact abstractC0041lM1394e = abstractC0046qM1082g.getContact(size2);
                removeContact(abstractC0041lM1394e, false);
                AppController.m415b(abstractC0041lM1394e);
            }
            removeGroup(abstractC0046qM1082g);
        }
    }

    /* renamed from: L */
    public final void markAllRead() {
        Enumeration enumerationElements = this.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            ((Contact) enumerationElements.nextElement()).clearUnread();
        }
        AppController.f152f = true;
    }

    /* renamed from: c */
    public final Contact getContact(Object obj) {
        return (Contact) this.contactMap.get(obj);
    }

    /* renamed from: d */
    public final void deleteContact(String str) {
        Contact abstractC0041lM1069c = getContact((Object) str);
        if (abstractC0041lM1069c == null || abstractC0041lM1069c.isOnline() || abstractC0041lM1069c.hasUnread() || abstractC0041lM1069c.isSystem()) {
            return;
        }
        AppController.m418c(abstractC0041lM1069c);
        AppState.getVector(1242).addElement(abstractC0041lM1069c);
        abstractC0041lM1069c.statusCode = AppState.getInt(1531);
        abstractC0041lM1069c.dirty = true;
    }

    /* renamed from: e */
    public final void markRead(String str) {
        Contact abstractC0041lM1069c = getContact((Object) str);
        if (abstractC0041lM1069c != null) {
            AppController.m418c(abstractC0041lM1069c);
        }
    }

    /* renamed from: a */
    public final void onMessage(String str, long j, String str2) {
        Contact abstractC0041lM1069c = getContact((Object) str);
        Contact abstractC0041lMo107b = abstractC0041lM1069c;
        if (abstractC0041lM1069c == null) {
            abstractC0041lMo107b = newContact(str);
        }
        this.recvCount++;
        abstractC0041lMo107b.receiveMessageFull(j, str2, 1);
    }

    /* renamed from: b */
    public abstract Contact newContact(String str);

    /* renamed from: a */
    public final void updateStatus(String str, long j, int i) {
        Contact abstractC0041lM1069c = getContact((Object) str);
        if (abstractC0041lM1069c != null) {
            abstractC0041lM1069c.updateMessageFlag(j, i);
        }
    }

    /* renamed from: a */
    public int validateSend(Contact abstractC0041l, String str, long j) {
        return isConnected() ? 0 : 299;
    }

    /* renamed from: a */
    public final int removeContact(Contact abstractC0041l, boolean z) {
        if (abstractC0041l == null) {
            return 0;
        }
        Enumeration enumerationKeys = this.contactMap.keys();
        while (true) {
            if (!enumerationKeys.hasMoreElements()) {
                break;
            }
            Hashtable hashtable = this.contactMap;
            Object objNextElement = enumerationKeys.nextElement();
            if (hashtable.get(objNextElement) == abstractC0041l) {
                this.contactMap.remove(objNextElement);
                break;
            }
        }
        int size = this.groups.size();
        while (true) {
            size--;
            if (size < 0) {
                this.defaultGroup.removeElement(abstractC0041l);
                AppController.m415b(abstractC0041l);
                return 0;
            }
            getGroup(size).removeElement(abstractC0041l);
        }
    }

    /* renamed from: a */
    public int validateGroupAdd(String str, String str2, String str3, ContactGroup abstractC0046q, boolean z) {
        if (!isConnected()) {
            return 299;
        }
        if (StringUtils.isEmpty(str2)) {
            return 301;
        }
        return StringUtils.isEmpty(str3) ? 302 : 0;
    }

    /* renamed from: a */
    public int validateModify(Contact abstractC0041l, Object[] objArr) {
        if (isConnected()) {
            return StringUtils.isEmpty((String) objArr[0]) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: b */
    public abstract int validateObject(Object obj);

    /* renamed from: a */
    public abstract int validateDelete(Contact abstractC0041l);

    /* renamed from: a */
    public int validateGroupCreate(String str) {
        if (isConnected()) {
            return StringUtils.isEmpty(str) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: a */
    public int validateGroupRename(ContactGroup abstractC0046q, String str) {
        if (abstractC0046q == this.defaultGroup || abstractC0046q == this.onlineGroup || abstractC0046q == this.specialGroup || abstractC0046q == this.offlineGroup) {
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
    public int validateGroupDelete(ContactGroup abstractC0046q) {
        if (abstractC0046q == this.defaultGroup || abstractC0046q == this.onlineGroup) {
            return 304;
        }
        return abstractC0046q.contacts.size() > 0 ? 303 : 0;
    }

    /* renamed from: b */
    public int validateResend(Contact abstractC0041l) {
        return (abstractC0041l.isOnline() || isConnected()) ? 0 : 299;
    }

    /* renamed from: d */
    public final int getResourceId(Object obj) {
        return ResourceManager.m969a((String) obj, this);
    }

    /* renamed from: c */
    public abstract int validateContactDelete(Contact abstractC0041l);

    /* renamed from: d */
    public abstract int validateContactBlock(Contact abstractC0041l);

    /* renamed from: e */
    public abstract int validateContactUnblock(Contact abstractC0041l);

    /* renamed from: f */
    public int validateContactResend(Contact abstractC0041l) {
        return !isConnected() ? 299 : 0;
    }

    /* renamed from: M */
    public final Vector getUnreadContacts() {
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationElements = this.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (abstractC0041l.hasUnread()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: N */
    public final Vector getOfflineContacts() {
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationElements = this.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (abstractC0041l.isOffline()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: O */
    public Vector getPendingContacts() {
        return NetworkUtils.newVector();
    }

    /* renamed from: P */
    public final Vector getAllContacts() {
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationElements = this.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            vectorM1213g.addElement(enumerationElements.nextElement());
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public int validateMove(Contact abstractC0041l, ContactGroup abstractC0046q, ContactGroup abstractC0046q2) {
        if (abstractC0046q == abstractC0046q2) {
            return 305;
        }
        return !isConnected() ? 299 : 0;
    }

    /* renamed from: Q */
    public final Vector getOnlineContacts() {
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationElements = this.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (abstractC0041l.isOnline()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: g */
    public final ContactGroup findGroup(Contact abstractC0041l) {
        ContactGroup abstractC0046qM1082g;
        if (abstractC0041l.isOnline() || this.defaultGroup.containsContact(abstractC0041l)) {
            return this.defaultGroup;
        }
        if (abstractC0041l.isSystem()) {
            return this.specialGroup;
        }
        if (abstractC0041l.hasUnread()) {
            return this.onlineGroup;
        }
        if (abstractC0041l.isOffline()) {
            return this.offlineGroup;
        }
        int size = this.groups.size();
        do {
            size--;
            if (size < 0) {
                return null;
            }
            abstractC0046qM1082g = getGroup(size);
        } while (!abstractC0046qM1082g.containsContact(abstractC0041l));
        return abstractC0046qM1082g;
    }

    /* renamed from: h */
    public final void registerContact(Contact abstractC0041l) {
        Contact abstractC0041l2 = (Contact) this.contactMap.get(abstractC0041l.getIdentifier());
        if (abstractC0041l2 != null && abstractC0041l2 != abstractC0041l) {
            this.defaultGroup.removeContact(abstractC0041l2);
            this.onlineGroup.removeContact(abstractC0041l2);
            this.offlineGroup.removeContact(abstractC0041l2);
            this.blockedGroup.removeContact(abstractC0041l2);
            this.specialGroup.removeContact(abstractC0041l2);
        }
        this.contactMap.put(abstractC0041l.getIdentifier(), abstractC0041l);
    }

    /* renamed from: g */
    public final ContactGroup getGroup(int i) {
        return (ContactGroup) this.groups.elementAt(i);
    }

    /* renamed from: b */
    public final void addGroup(ContactGroup abstractC0046q) {
        this.groups.addElement(abstractC0046q);
    }

    /* renamed from: c */
    public final void removeGroup(ContactGroup abstractC0046q) {
        this.groups.removeElement(abstractC0046q);
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
