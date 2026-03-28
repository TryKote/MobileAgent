package com.trykote.mobileagent.protocol.mrim;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
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

    public MrimContact(Account account, int i, int i2, int i3, String str, String str2, int i4, int i5, String str3, String str4, String str5) {
        super(account);
        this.contactId = i;
        this.statusFlags = i2;
        this.groupId = i3;
        this.simpleIdentifier = str;
        this.hasUnreadFlag = i4;
        this.unreadCount = i5;
        this.contactGroupsStr = str3;
        this.statusMessage = str5;
        setDisplayName(Utils.defaultIfBlank(str2, str));
        this.defaultIcon = AppController.resolveServerIcon(i5, str4);
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
        Vector groups = Utils.splitNonEmpty(this.contactGroupsStr, ',');
        String str = (String) groups.elementAt(0);
        ObjectPool.releaseVector(groups);
        return str;
    }

    public MrimContact(Account account, ByteBuffer buffer) {
        super(account);
        this.contactId = buffer.readInt();
        String str = AppState.emptyStr;
        this.statusFlags = buffer.readInt();
        this.simpleIdentifier = StringUtils.intern(buffer.readWideStr().toLowerCase());
        setDisplayName(buffer.readUTF8Str((String) null));
        this.hasUnreadFlag = buffer.readInt();
        this.contactGroupsStr = buffer.readWideStr();
        byte flags = buffer.readByte();
        this.flags = flags;
        if (flags != 0) {
            ContactListManager.markContactRead((Contact) this);
        }
        this.statusMessage = str;
        this.defaultIcon = 155;
        updateIdentifierAndRegister();
        this.sizeCache = new SizeCache();
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier).setIcon(getIcon());
        String str = this.displayName;
        int i = canBlock() ? 3 : canDelete() ? 2 : 0;
        int i2 = this.statusFlags;
        menuItem.addText(str, i, (i2 & 1048576) != 0 ? 0 : (i2 & 8) != 0 ? 4 : (i2 & 4) != 0 ? 5 : this.unreadCount == 0 ? 0 : 3).data = this;
        if (!isOffline() && Utils.nonEmpty(this.contactGroupsStr)) {
            menuItem.setIcon(27);
        }
        if (Utils.nonEmpty(this.customLink)) {
            menuItem.setIcon(242);
        }
        menuItem.data = this;
        return menuItem;
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
    public final void deserialize(ByteBuffer buffer) {
        buffer.writeIntLE(this.contactId).writeIntLE(this.statusFlags).writeStringLatin1(this.simpleIdentifier).writeStringUTF16(this.displayName).writeIntLE(this.hasUnreadFlag).writeStringLatin1(this.contactGroupsStr).writeByte(this.flags);
    }

    /* renamed from: a */
    public final void setGroupsList(Vector vector) {
        if (vector == null) {
            ObjectPool.releaseVector(this.groupsList);
            this.groupsList = null;
            return;
        }
        if (this.groupsList == null) {
            this.groupsList = ObjectPool.newVector();
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
        int icon = super.getIcon();
        if (icon == 16384 || icon == 26) {
            return icon;
        }
        if (0 != (this.hasUnreadFlag & 1) || isOnline()) {
            return 154;
        }
        return icon;
    }

    /* renamed from: N */
    private final String getFirstGroupName() {
        int idx = -1;
        try {
            idx = this.contactGroupsStr.indexOf(44);
        } catch (Throwable unused) {
        }
        return idx >= 0 ? StringUtils.prefix(this.contactGroupsStr, idx) : Utils.defaultStr(this.contactGroupsStr);
    }

    /* renamed from: O */
    private void updateIdentifierAndRegister() {
        ByteBuffer idBuf = this.account.encodeId();
        String key = isOffline() ? getFirstGroupName() : this.simpleIdentifier;
        this.extra = key;
        this.identifier = idBuf.writeRawString(key).readAllByteStr();
        if (isOffline()) {
            this.extra = Utils.formatPhone(this.extra);
        }
        updateRenderState();
        this.account.registerContact(this);
    }

    /* JADX DEBUG: Possible override for method l.f()Ln; */
    /* renamed from: f */
    public final int requestUserDetails() {
        long now = AppState.getLong(SessionKeys.TIMESTAMP_CURRENT);
        if (now - this.lastStatusCheckTime <= 60000) {
            return 925;
        }
        this.lastStatusCheckTime = now;
        MrimAccount mrimAccount = (MrimAccount) this.account;
        int sendResult = mrimAccount.trySendData(mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(16512).writeStringLatin1(this.simpleIdentifier).writeStringUTF16(AppState.getString(StringResKeys.STR_MRIM_RENAME_CONTACT)).writeStringLatin1(AppState.getString(StringResKeys.STR_RES_MEGA_URL_5))), ResourceManager.integerOf(MrimAccount.RESP_RENAME_CONTACT)}));
        if (0 != sendResult) {
            return sendResult;
        }
        appendMessage(1, AppState.getString(StringResKeys.STR_WELCOME_MESSAGE), 0L, 0L);
        return 0;
    }

    /* renamed from: a */
    public final boolean isInGroup(String str) {
        Vector groups = Utils.splitNonEmpty(this.contactGroupsStr, ',');
        int size = groups.size();
        do {
            size--;
            if (size < 0) {
                ObjectPool.releaseVector(groups);
                return false;
            }
        } while (!str.equals(groups.elementAt(size)));
        ObjectPool.releaseVector(groups);
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
        this.extra = isOffline() ? Utils.formatPhone(getFirstGroupName()) : this.simpleIdentifier;
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
        ChatRenderer.mapItems = null;
        MapRenderer.needsRedraw = true;
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
        StringBuffer sb = ObjectPool.newStringBuffer().append(this.displayName);
        String str = this.vCardInfo.phone;
        if (str.length() > 0) {
            sb.append(',').append(' ').append(str).append('.');
        }
        return ObjectPool.toStringAndRelease(sb);
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
