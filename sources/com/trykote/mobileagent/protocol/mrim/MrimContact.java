package com.trykote.mobileagent.protocol.mrim;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public final class MrimContact extends Contact implements ListItem {

    // Icon IDs
    static final int ICON_OFFLINE = 155;
    private static final int ICON_PHONE_CONTACT = 27;
    private static final int ICON_SYSTEM = 232;
    private static final int ICON_ONLINE = 154;
    private static final int ICON_BLINK_FLAG = 16384;
    private static final int ICON_SYSTEM_BLINK = ICON_SYSTEM | ICON_BLINK_FLAG;
    private static final int ICON_CUSTOM_STATUS = 26;
    private static final int ICON_CUSTOM_LINK = 242;

    // Status flags (bitmask in statusFlags field)
    private static final int STATUS_FLAG_OFFLINE = 1048576;
    private static final int STATUS_FLAG_DELETABLE = 8;
    private static final int STATUS_FLAG_BLOCKABLE = 4;
    private static final int STATUS_FLAG_ONLINE = 65536;
    private static final int STATUS_FLAG_SYSTEM = 128;

    private static final char CHAR_COMMA = ',';
    private static final long STATUS_CHECK_INTERVAL_MS = 60000;
    static final int ERROR_TOO_FREQUENT = 925;
    private static final int MASK_CLEAR_UNREAD = -2;
    private static final int DEFAULT_COMMAND_COUNT = 10;

    public final int contactId;

    public int statusFlags;

    public int groupId;

    public String simpleIdentifier;

    public int hasUnreadFlag;

    public int unreadCount;

    public String contactGroupsStr;

    public String statusMessage;

    private long lastStatusCheckTime;

    public String customNote;

    public String customLink;

    public Vector groupsList;

    public VCard vCardInfo;

    public boolean isSelected;

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
    public final String getIdentifier() {
        if (!isOffline()) {
            return this.simpleIdentifier;
        }
        Vector groups = Utils.splitNonEmpty(this.contactGroupsStr, CHAR_COMMA);
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
        this.defaultIcon = ICON_OFFLINE;
        updateIdentifierAndRegister();
        this.sizeCache = new SizeCache();
    }

    @Override // p000.Contact
    public final MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier).setIcon(getIcon());
        String str = this.displayName;
        int i = canBlock() ? 3 : canDelete() ? 2 : 0;
        int i2 = this.statusFlags;
        menuItem.addText(str, i, (i2 & STATUS_FLAG_OFFLINE) != 0 ? 0 : (i2 & STATUS_FLAG_DELETABLE) != 0 ? 4 : (i2 & STATUS_FLAG_BLOCKABLE) != 0 ? 5 : this.unreadCount == 0 ? 0 : 3).data = this;
        if (!isOffline() && Utils.nonEmpty(this.contactGroupsStr)) {
            menuItem.setIcon(ICON_PHONE_CONTACT);
        }
        if (Utils.nonEmpty(this.customLink)) {
            menuItem.setIcon(ICON_CUSTOM_LINK);
        }
        menuItem.data = this;
        return menuItem;
    }

    @Override // p000.Contact
    public final void clearUnread() {
        this.defaultIcon = ICON_OFFLINE;
        this.unreadCount = 0;
        this.customLink = null;
        this.customNote = null;
        clearVCard();
        super.clearUnread();
    }

    @Override
    public final void serialize(ByteBuffer buffer) {
        buffer.writeIntLE(this.contactId).writeIntLE(this.statusFlags).writeStringLatin1(this.simpleIdentifier).writeStringUTF16(this.displayName).writeIntLE(this.hasUnreadFlag).writeStringLatin1(this.contactGroupsStr).writeByte(this.flags);
    }

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
        for (int i = vector.size() - 1; i >= 0; i--) {
            this.groupsList.addElement(vector.elementAt(i));
        }
    }

    @Override // p000.Contact
    public final boolean isOffline() {
        return (this.statusFlags & STATUS_FLAG_OFFLINE) != 0;
    }

    @Override // p000.Contact
    public final int getIcon() {
        if (isOffline()) {
            return this.flags == 0 ? ICON_PHONE_CONTACT : ICON_BLINK_FLAG;
        }
        if (isSystem()) {
            return this.flags == 0 ? ICON_SYSTEM : ICON_SYSTEM_BLINK;
        }
        int icon = super.getIcon();
        if (icon == ICON_BLINK_FLAG || icon == ICON_CUSTOM_STATUS) {
            return icon;
        }
        if ((this.hasUnreadFlag & 1) != 0 || isOnline()) {
            return ICON_ONLINE;
        }
        return icon;
    }

    private final String getFirstGroupName() {
        int idx = -1;
        try {
            idx = this.contactGroupsStr.indexOf(CHAR_COMMA);
        } catch (Throwable unused) {
        }
        return idx >= 0 ? StringUtils.prefix(this.contactGroupsStr, idx) : Utils.defaultStr(this.contactGroupsStr);
    }

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

    public final int requestUserDetails() {
        long now = SessionState.getTimestampCurrent();
        if (now - this.lastStatusCheckTime <= STATUS_CHECK_INTERVAL_MS) {
            return ERROR_TOO_FREQUENT;
        }
        this.lastStatusCheckTime = now;
        MrimAccount mrimAccount = (MrimAccount) this.account;
        int sendResult = mrimAccount.trySendData(mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(16512).writeStringLatin1(this.simpleIdentifier).writeStringUTF16(ResourceAccessor.str(StringResKeys.STR_MRIM_RENAME_CONTACT)).writeStringLatin1(ResourceAccessor.str(PackedStringKeys.MRIM_MESSAGE_RTF_BLOB))), ObjectPool.integerOf(MrimAccount.RESP_RENAME_CONTACT)}));
        if (sendResult != 0) {
            return sendResult;
        }
        appendMessage(1, ResourceAccessor.str(StringResKeys.STR_WELCOME_MESSAGE), 0L, 0L);
        return 0;
    }

    public final boolean isInGroup(String str) {
        Vector groups = Utils.splitNonEmpty(this.contactGroupsStr, CHAR_COMMA);
        for (int i = groups.size() - 1; i >= 0; i--) {
            if (str.equals(groups.elementAt(i))) {
                ObjectPool.releaseVector(groups);
                return true;
            }
        }
        ObjectPool.releaseVector(groups);
        return false;
    }

    @Override // p000.Contact
    public final String getDefaultName() {
        return this.contactGroupsStr;
    }

    @Override // p000.Contact
    public final void performAction() {
        this.hasUnreadFlag &= MASK_CLEAR_UNREAD;
    }

    @Override // p000.Contact
    public final boolean canDelete() {
        return (this.statusFlags & STATUS_FLAG_DELETABLE) != 0;
    }

    @Override // p000.Contact
    public final boolean canBlock() {
        return (this.statusFlags & STATUS_FLAG_BLOCKABLE) != 0;
    }

    @Override // p000.Contact
    public final boolean canUnblock() {
        return (this.statusFlags & 16) != 0;
    }

    @Override // p000.Contact
    public final boolean hasUnread() {
        return (this.hasUnreadFlag & 1) != 0;
    }

    @Override // p000.Contact
    public final boolean isOnline() {
        return (this.statusFlags & STATUS_FLAG_ONLINE) != 0;
    }

    @Override // p000.Contact
    public final boolean isSystem() {
        return (this.statusFlags & STATUS_FLAG_SYSTEM) != 0;
    }

    public final void updateDisplayNameAndGroups(String str, String str2) {
        setDisplayName(str);
        this.contactGroupsStr = str2;
        this.extra = isOffline() ? Utils.formatPhone(getFirstGroupName()) : this.simpleIdentifier;
    }

    public final String getVCardDescription() {
        try {
            return this.vCardInfo.phone;
        } catch (Throwable unused) {
            return null;
        }
    }

    public final void clearVCard() {
        this.vCardInfo = null;
        ChatRenderer.mapItems = null;
        MapRenderer.needsRedraw = true;
    }

    public final boolean hasVCard() {
        return this.vCardInfo != null;
    }

    @Override // p000.ListItem
    public final int getHeight() {
        return 3;
    }

    @Override // p000.ListItem
    public final boolean isSelected() {
        return this.isSelected && this.vCardInfo != null && this.vCardInfo.hasCoordinates();
    }

    @Override // p000.ListItem
    public final void select() {
        this.isSelected = false;
    }

    @Override // p000.ListItem
    public final void deselect() {
        this.isSelected = true;
    }

    @Override // p000.ListItem
    public final int getWidth() {
        try {
            return (int) this.vCardInfo.getLongitude();
        } catch (Throwable unused) {
            clearVCard();
            return 0;
        }
    }

    @Override // p000.ListItem
    public final int getBaseHeight() {
        try {
            return (int) this.vCardInfo.getLatitude();
        } catch (Throwable unused) {
            clearVCard();
            return 0;
        }
    }

    @Override // p000.ListItem
    public final String getText() {
        StringBuffer sb = ObjectPool.newStringBuffer().append(this.displayName);
        String str = this.vCardInfo.phone;
        if (str.length() > 0) {
            sb.append(',').append(' ').append(str).append('.');
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    @Override // p000.ListItem
    public final int getCommandCount() {
        if (this.vCardInfo != null) {
            return this.vCardInfo.getCommandCount();
        }
        return DEFAULT_COMMAND_COUNT;
    }

    @Override // p000.ListItem
    public final boolean isHighlighted() {
        return this.vCardInfo.hasCoordinates() && !this.vCardInfo.dirty;
    }

    @Override // p000.ListItem
    public final int getCommandId(int i) {
        return this.sizeCache.getWidth(i, this);
    }

    @Override // p000.ListItem
    public final int executeCommand(int i) {
        return this.sizeCache.getHeight(i, this);
    }
}
