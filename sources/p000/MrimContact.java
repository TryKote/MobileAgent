package p000;

import java.util.Vector;

/* renamed from: f */
/* loaded from: MobileAgent_3.9.jar:f.class */
public final class MrimContact extends Contact implements ListItem {

    /* renamed from: a */
    public final int contactId;

    /* renamed from: b */
    public int statusFlags;

    /* renamed from: c */
    public int groupId;

    /* renamed from: d */
    public String simpleIdentifier;

    /* renamed from: e */
    public int hasUnreadFlag;

    /* renamed from: f */
    public int unreadCount;

    /* renamed from: g */
    public String contactGroupsStr;

    /* renamed from: h */
    public String statusMessage;

    /* renamed from: y */
    private long lastStatusCheckTime;

    /* renamed from: i */
    public String customNote;

    /* renamed from: j */
    public String customLink;

    /* renamed from: k */
    public Vector groupsList;

    /* renamed from: l */
    public VCard vCardInfo;

    /* renamed from: m */
    public boolean isSelected;

    /* renamed from: n */
    public SizeCache sizeCache;

    public MrimContact(Account abstractC0037h, int i, int i2, int i3, String str, String str2, int i4, int i5, String str3, String str4, String str5) {
        super(abstractC0037h);
        this.contactId = i;
        this.statusFlags = i2;
        this.groupId = i3;
        this.simpleIdentifier = str;
        this.hasUnreadFlag = i4;
        this.unreadCount = i5;
        this.contactGroupsStr = str3;
        this.statusMessage = str5;
        setDisplayName(Utils.m528a(str2, str));
        this.defaultIcon = AppController.m349a(i5, str4);
        this.highlighted = i5 != 0;
        updateIdentifierAndRegister();
        this.sizeCache = new SizeCache();
    }

    public MrimContact() {
        super(null);
        this.contactId = 0;
        this.sizeCache = new SizeCache();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final String getIdentifier() {
        if (!isOffline()) {
            return this.simpleIdentifier;
        }
        Vector vectorM516c = Utils.m516c(this.contactGroupsStr, ',');
        String str = (String) vectorM516c.elementAt(0);
        NetworkUtils.releaseVector(vectorM516c);
        return str;
    }

    public MrimContact(Account abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.contactId = c0043n.readInt();
        String str = AppState.emptyStr;
        this.statusFlags = c0043n.readInt();
        this.simpleIdentifier = StringUtils.intern(c0043n.readWideStr().toLowerCase());
        setDisplayName(c0043n.readUTF8Str((String) null));
        this.hasUnreadFlag = c0043n.readInt();
        this.contactGroupsStr = c0043n.readWideStr();
        byte bM1344o = c0043n.readByte();
        this.flags = bM1344o;
        if (bM1344o != 0) {
            AppController.m414a((Contact) this);
        }
        this.statusMessage = str;
        this.defaultIcon = 155;
        updateIdentifierAndRegister();
        this.sizeCache = new SizeCache();
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem createMenuItem() {
        MenuItem c0032cM896a = MenuItem.m887a(this.identifier).m896a(getIcon());
        String str = this.displayName;
        int i = canBlock() ? 3 : canDelete() ? 2 : 0;
        int i2 = this.statusFlags;
        c0032cM896a.m901a(str, i, (i2 & 1048576) != 0 ? 0 : (i2 & 8) != 0 ? 4 : (i2 & 4) != 0 ? 5 : this.unreadCount == 0 ? 0 : 3).f265d = this;
        if (!isOffline() && Utils.nonEmpty(this.contactGroupsStr)) {
            c0032cM896a.m896a(27);
        }
        if (Utils.nonEmpty(this.customLink)) {
            c0032cM896a.m896a(242);
        }
        c0032cM896a.f265d = this;
        return c0032cM896a;
    }

    @Override // p000.Contact
    /* renamed from: c */
    public final void clearUnread() {
        this.defaultIcon = 155;
        this.unreadCount = 0;
        this.customLink = null;
        this.customNote = null;
        clearVCard();
        super.clearUnread();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void deserialize(ByteBuffer c0043n) {
        c0043n.writeIntLE(this.contactId).writeIntLE(this.statusFlags).writeStringLatin1(this.simpleIdentifier).writeStringUTF16(this.displayName).writeIntLE(this.hasUnreadFlag).writeStringLatin1(this.contactGroupsStr).writeByte(this.flags);
    }

    /* renamed from: a */
    public final void setGroupsList(Vector vector) {
        if (vector == null) {
            NetworkUtils.releaseVector(this.groupsList);
            this.groupsList = null;
            return;
        }
        if (this.groupsList == null) {
            this.groupsList = NetworkUtils.newVector();
        }
        this.groupsList.removeAllElements();
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                this.groupsList.addElement(vector.elementAt(size));
            }
        }
    }

    @Override // p000.Contact
    /* renamed from: d */
    public final boolean isOffline() {
        return (this.statusFlags & 1048576) != 0;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int getIcon() {
        if (isOffline()) {
            return this.flags == 0 ? 27 : 16384;
        }
        if (isSystem()) {
            return this.flags == 0 ? 232 : 16616;
        }
        int iMo139e = super.getIcon();
        if (iMo139e == 16384 || iMo139e == 26) {
            return iMo139e;
        }
        if (0 != (this.hasUnreadFlag & 1) || isOnline()) {
            return 154;
        }
        return iMo139e;
    }

    /* renamed from: N */
    private final String getFirstGroupName() {
        int iIndexOf = -1;
        try {
            iIndexOf = this.contactGroupsStr.indexOf(44);
        } catch (Throwable unused) {
        }
        return iIndexOf >= 0 ? StringUtils.prefix(this.contactGroupsStr, iIndexOf) : Utils.defaultStr(this.contactGroupsStr);
    }

    /* renamed from: O */
    private void updateIdentifierAndRegister() {
        ByteBuffer c0043nM1050q = this.account.encodeId();
        String strM991N = isOffline() ? getFirstGroupName() : this.simpleIdentifier;
        this.extra = strM991N;
        this.identifier = c0043nM1050q.writeRawString(strM991N).readAllByteStr();
        if (isOffline()) {
            this.extra = Utils.m530h(this.extra);
        }
        updateRenderState();
        this.account.registerContact(this);
    }

    /* JADX DEBUG: Possible override for method l.f()Ln; */
    /* renamed from: f */
    public final int requestUserDetails() {
        long jM598g = AppState.getLong(1530);
        if (jM598g - this.lastStatusCheckTime <= 60000) {
            return 925;
        }
        this.lastStatusCheckTime = jM598g;
        MrimAccount c0028ba = (MrimAccount) this.account;
        int iM1052c = c0028ba.trySendData(c0028ba.createAndQueueCommand(new Object[]{AppController.m321a(c0028ba, 4104, new ByteBuffer().writeIntLE(16512).writeStringLatin1(this.simpleIdentifier).writeStringUTF16(AppState.getString(909)).writeStringLatin1(AppState.getString(33819707))), ResourceManager.m967e(14)}));
        if (0 != iM1052c) {
            return iM1052c;
        }
        appendMessage(1, AppState.getString(924), 0L, 0L);
        return 0;
    }

    /* renamed from: a */
    public final boolean isInGroup(String str) {
        Vector vectorM516c = Utils.m516c(this.contactGroupsStr, ',');
        int size = vectorM516c.size();
        do {
            size--;
            if (size < 0) {
                NetworkUtils.releaseVector(vectorM516c);
                return false;
            }
        } while (!str.equals(vectorM516c.elementAt(size)));
        NetworkUtils.releaseVector(vectorM516c);
        return true;
    }

    @Override // p000.Contact
    /* renamed from: g */
    public final String getDefaultName() {
        return this.contactGroupsStr;
    }

    @Override // p000.Contact
    /* renamed from: h */
    public final void performAction() {
        this.hasUnreadFlag &= -2;
    }

    @Override // p000.Contact
    /* renamed from: i */
    public final boolean canDelete() {
        return (this.statusFlags & 8) != 0;
    }

    @Override // p000.Contact
    /* renamed from: j */
    public final boolean canBlock() {
        return (this.statusFlags & 4) != 0;
    }

    @Override // p000.Contact
    /* renamed from: k */
    public final boolean canUnblock() {
        return (this.statusFlags & 16) != 0;
    }

    @Override // p000.Contact
    /* renamed from: l */
    public final boolean hasUnread() {
        return (this.hasUnreadFlag & 1) != 0;
    }

    @Override // p000.Contact
    /* renamed from: m */
    public final boolean isOnline() {
        return (this.statusFlags & 65536) != 0;
    }

    @Override // p000.Contact
    /* renamed from: n */
    public final boolean isSystem() {
        return (this.statusFlags & 128) != 0;
    }

    /* renamed from: a */
    public final void updateDisplayNameAndGroups(String str, String str2) {
        setDisplayName(str);
        this.contactGroupsStr = str2;
        this.extra = isOffline() ? Utils.m530h(getFirstGroupName()) : this.simpleIdentifier;
    }

    /* JADX DEBUG: Possible override for method l.o()V */
    /* renamed from: o */
    public final String getVCardDescription() {
        try {
            return this.vCardInfo.phone;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: p */
    public final void clearVCard() {
        this.vCardInfo = null;
        ChatRenderer.f253h = null;
        MapRenderer.f200h = true;
    }

    /* renamed from: q */
    public final boolean hasVCard() {
        return this.vCardInfo != null;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return 3;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.isSelected && this.vCardInfo != null && this.vCardInfo.hasCoordinates();
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.isSelected = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.isSelected = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        try {
            return (int) this.vCardInfo.getLongitude();
        } catch (Throwable unused) {
            clearVCard();
            return 0;
        }
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        try {
            return (int) this.vCardInfo.getLatitude();
        } catch (Throwable unused) {
            clearVCard();
            return 0;
        }
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append(this.displayName);
        String str = this.vCardInfo.phone;
        if (str.length() > 0) {
            stringBufferAppend.append(',').append(' ').append(str).append('.');
        }
        return NetworkUtils.bufToStringCached(stringBufferAppend);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        if (this.vCardInfo != null) {
            return this.vCardInfo.getCommandCount();
        }
        return 10;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return this.vCardInfo.hasCoordinates() && !this.vCardInfo.dirty;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.sizeCache.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.sizeCache.getHeight(i, this);
    }
}
