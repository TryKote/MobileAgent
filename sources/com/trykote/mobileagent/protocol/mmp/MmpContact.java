package com.trykote.mobileagent.protocol.mmp;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.ProtocolFactory;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

public final class MmpContact extends Contact {

    // Icon IDs
    private static final int ICON_DEFAULT = 255;
    private static final int ICON_SPECIAL = 263;
    private static final int ICON_BLINK_FLAG = 16384;
    private static final int ICON_CUSTOM_STATUS = 26;

    // Icon composition: masks and bases (parseStatus)
    private static final int ICON_ONLINE = 256;
    private static final int ICON_HIGH_MASK = -65536;
    private static final int ICON_CUSTOM_GUID_BASE = 269;

    // TLV attribute types in parseStatus
    private static final int ATTR_STATUS = 6;
    private static final int ATTR_GUID_LIST = 13;
    private static final int ATTR_CAPABILITIES = 29;
    private static final int GUID_SIZE = 16;
    private static final int GUID_TABLE_SIZE = 576;
    private static final int TLV_TYPE_CAPABILITY_STRING = 14;
    private static final int TLV_LENGTH_MASK = 255;
    private static final int TLV_CONTINUATION_BIT = 128;
    private static final int CAPABILITY_ICON_OFFSET = 7;
    private static final int CAPABILITY_ICON_MAX = 23;

    // Contact status exact match values (parseStatus)
    private static final int CONTACT_STATUS_ONLINE = 0;

    // Contact status bitmask values (tested in descending priority)
    private static final int CONTACT_FLAG_ON_THE_PHONE = 24576;
    private static final int CONTACT_FLAG_IN_SHOWER = 20480;
    private static final int CONTACT_FLAG_EATING = 16384;
    private static final int CONTACT_FLAG_AT_HOME = 12288;
    private static final int CONTACT_FLAG_DEPRESSION = 8192;
    private static final int CONTACT_FLAG_AT_WORK = 256;
    private static final int CONTACT_FLAG_FREE_CHAT = 32;
    private static final int CONTACT_FLAG_INVISIBLE = 16;
    private static final int CONTACT_FLAG_AWAY = 4;
    private static final int CONTACT_FLAG_DND = 2;

    // Contact icon overlays (parseStatus)
    private static final int CONTACT_ICON_ONLINE = 16318720;
    private static final int CONTACT_ICON_DND = 16449792;
    private static final int CONTACT_ICON_AWAY = 16580864;
    private static final int CONTACT_ICON_INVISIBLE = 16646400;
    private static final int CONTACT_ICON_FREE_CHAT = 16384256;
    private static final int CONTACT_ICON_AT_WORK = 16515328;
    private static final int CONTACT_ICON_DEPRESSION = 17104896;
    private static final int CONTACT_ICON_AT_HOME = 16842752;
    private static final int CONTACT_ICON_EATING = 16908288;
    private static final int CONTACT_ICON_IN_SHOWER = 16973824;
    private static final int CONTACT_ICON_ON_THE_PHONE = 17039360;

    // MMP protocol tags (for encodeContactUpdate)
    private static final int TAG_DISPLAY_NAME = 305;
    private static final int TAG_AUTHORIZATION_FLAG = 102;

    // Text style indices for createMenuItem
    private static final int TEXT_STYLE_DEFAULT = 0;
    private static final int TEXT_STYLE_DELETABLE = 2;
    private static final int TEXT_STYLE_BLOCKABLE = 3;

    // Text color indices for createMenuItem
    private static final int TEXT_COLOR_DEFAULT = 0;
    private static final int TEXT_COLOR_UNREAD_GENERIC = 3;
    private static final int TEXT_COLOR_UNREAD_DELETABLE = 4;
    private static final int TEXT_COLOR_UNREAD_BLOCKABLE = 5;

    // Permission type IDs (for updatePermissionFlags)
    private static final int PERMISSION_DELETE = 2;
    private static final int PERMISSION_BLOCK = 3;

    // Contact update operation types (for encodeContactUpdate)
    private static final int UPDATE_TYPE_RENAME = 2;
    private static final int UPDATE_TYPE_AUTHORIZE = 5;

    public final int userId;

    public int onlineSemaphore;

    public String identifier;

    private boolean hasUnread;

    public int canDelete;

    public int canBlock;

    public int canUnblock;

    public boolean isBlocked;

    public boolean isUnblocked;

    public MmpContact(MmpProtocol protocol, int userId, int onlineSemaphore, String rawIdentifier, String name, boolean unread) {
        super(protocol);
        this.userId = userId;
        this.onlineSemaphore = onlineSemaphore;
        this.identifier = rawIdentifier;
        this.displayName = name;
        this.sortKey = StringUtils.intern(name.toLowerCase());
        this.hasUnread = unread;
        this.defaultIcon = ICON_DEFAULT;
        this.identifier = protocol.encodeId().writeRawString(rawIdentifier).readAllByteStr();
        protocol.registerContact(this);
        updateRenderState();
        this.extra = rawIdentifier;
    }

    @Override // p000.Contact
    public final void clearUnread() {
        this.defaultIcon = ICON_DEFAULT;
        this.isBlocked = false;
        this.isUnblocked = false;
        super.clearUnread();
    }

    @Override // p000.Contact
    public final String getIdentifier() {
        return this.identifier;
    }

    public MmpContact(Account account, ByteBuffer buffer) {
        super(account);
        this.userId = buffer.readInt();
        this.onlineSemaphore = buffer.readInt();
        this.identifier = buffer.readWideStr();
        setDisplayName(buffer.readUTF8Str((String) null));
        this.hasUnread = buffer.readBoolean();
        buffer.readBoolean();
        this.canDelete = buffer.readShortBE();
        this.canBlock = buffer.readShortBE();
        this.canUnblock = buffer.readShortBE();
        byte savedFlags = buffer.readByte();
        this.flags = savedFlags;
        if (savedFlags != 0) {
            ContactListManager.markContactRead((Contact) this);
        }
        this.defaultIcon = ICON_DEFAULT;
        this.identifier = account.encodeId().writeRawString(this.identifier).readAllByteStr();
        account.registerContact(this);
        updateRenderState();
        this.extra = this.identifier;
    }

    @Override
    public final void serialize(ByteBuffer buffer) {
        buffer.writeIntLE(this.userId).writeIntLE(this.onlineSemaphore).writeStringLatin1(this.identifier).writeStringUTF16(this.displayName).writeBoolean(this.hasUnread).writeBoolean(false).writeShortBE(this.canDelete).writeShortBE(this.canBlock).writeShortBE(this.canUnblock).writeByte(this.flags);
    }

    @Override // p000.Contact
    public final MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier).setIcon(getIcon()).addText(this.displayName, canBlock() ? TEXT_STYLE_BLOCKABLE : canDelete() ? TEXT_STYLE_DELETABLE : TEXT_STYLE_DEFAULT, this.defaultIcon == ICON_DEFAULT ? TEXT_COLOR_DEFAULT : canDelete() ? TEXT_COLOR_UNREAD_DELETABLE : canBlock() ? TEXT_COLOR_UNREAD_BLOCKABLE : TEXT_COLOR_UNREAD_GENERIC);
        menuItem.data = this;
        return menuItem;
    }

    @Override // p000.Contact
    public final int getIcon() {
        int icon = super.getIcon();
        if (icon == ICON_BLINK_FLAG || icon == ICON_CUSTOM_STATUS) {
            return icon;
        }
        if (hasUnread() || isOnline()) {
            return ICON_SPECIAL;
        }
        return icon;
    }

    public final ByteBuffer encodeContactUpdate(int updateType, String name, int groupId) {
        ByteBuffer buffer = new ByteBuffer();
        if (updateType != UPDATE_TYPE_RENAME) {
            buffer.writeShortBE(TAG_DISPLAY_NAME).writeUTF(name);
        }
        if (updateType == UPDATE_TYPE_AUTHORIZE) {
            buffer.writeShortBE(TAG_AUTHORIZATION_FLAG).writeShortBE(0);
        }
        return new ByteBuffer().writeUTF(this.identifier).writeShortBE(groupId).writeShortBE(this.userId).writeShortBE(0).writeBufferShortLen(buffer);
    }

    @Override // p000.Contact
    public final boolean canDelete() {
        return this.canDelete != 0;
    }

    @Override // p000.Contact
    public final boolean canBlock() {
        return this.canBlock != 0;
    }

    @Override // p000.Contact
    public final boolean canUnblock() {
        return this.canUnblock != 0;
    }

    @Override // p000.Contact
    public final boolean isOnline() {
        return this.userId == -1;
    }

    @Override // p000.Contact
    public final boolean hasUnread() {
        return this.hasUnread && this.userId != -1;
    }

    @Override // p000.Contact
    public final void performAction() {
        if (isOnline()) {
            return;
        }
        this.hasUnread = false;
        updateRenderState();
    }

    @Override
    public final int getEmoticonBase() {
        return StringResKeys.MMP_EMOTICONS_BASE;
    }

    @Override
    public final String getContactEmail() {
        return this.extra;
    }

    @Override
    public void populateContactInfo(Object contactInfo) {
        ((ContactInfo) contactInfo).setMmpContactId(Utils.parseInt((Object) this.identifier));
    }

    public final void updatePermissionFlags(int permissionType, int value) {
        if (permissionType == PERMISSION_DELETE) {
            this.canDelete = value;
        } else if (permissionType == PERMISSION_BLOCK) {
            this.canBlock = value;
        } else {
            this.canUnblock = value;
        }
    }

    private static ByteBuffer createPermissionCommand(MmpProtocol protocol, MmpContact contact, int permissionType) {
        ByteBuffer contactBuffer = new ByteBuffer().writeShortString(contact.identifier).writeShortBE(0);
        int uniqueId = protocol.generateUniqueGroupId();
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, MmpCommand.ADD_CONTACT, contactBuffer.writeShortBE(uniqueId).writeShortBE(permissionType).writeShortBE(0)), ObjectPool.integerOf(MmpResponseHandler.RESP_UPDATE_PERMISSIONS), contact, ObjectPool.integerOf(permissionType), ObjectPool.integerOf(uniqueId)});
    }

    private static ByteBuffer updatePermissionCommand(MmpProtocol protocol, MmpContact contact, int existingId, int permissionType) {
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, MmpCommand.DELETE_CONTACT, new ByteBuffer().writeShortString(contact.identifier).writeShortBE(0).writeShortBE(existingId).writeShortBE(permissionType).writeShortBE(0)), ObjectPool.integerOf(MmpResponseHandler.RESP_REMOVE_PERMISSIONS), contact, ObjectPool.integerOf(permissionType)});
    }

    public static ByteBuffer deletePermission(MmpProtocol protocol, MmpContact contact) {
        return contact.canDelete() ? updatePermissionCommand(protocol, contact, contact.canDelete, 2) : createPermissionCommand(protocol, contact, 2);
    }

    public static ByteBuffer blockPermission(MmpProtocol protocol, MmpContact contact) {
        return contact.canBlock() ? updatePermissionCommand(protocol, contact, contact.canBlock, 3) : createPermissionCommand(protocol, contact, 3);
    }

    public static ByteBuffer unblockPermission(MmpProtocol protocol, MmpContact contact) {
        return contact.canUnblock() ? updatePermissionCommand(protocol, contact, contact.canUnblock, 14) : createPermissionCommand(protocol, contact, 14);
    }

    static void parseStatus(MmpContact contact, ByteBuffer buffer) {
        int iconIndex;
        int icon = ICON_DEFAULT;
        try {
            buffer.skip(2);
            int attrCount = buffer.readShortBE();
            for (int attrIndex = 0; attrIndex < attrCount; attrIndex++) {
                int attrType = buffer.readShortBE();
                int attrLen = buffer.readShortBE();
                if (attrType == ATTR_STATUS) {
                    int statusFlags = buffer.readIntBE() & MmpProtocol.MASK_LOW_16;
                    if (statusFlags == CONTACT_STATUS_ONLINE) {
                        icon = ICON_ONLINE;
                        contact.defaultIcon = icon;
                        attrLen -= 4;
                        contact.highlighted = true;
                    } else {
                        if (statusFlags == MmpProtocol.CONTACT_STATUS_DND) {
                            icon = CONTACT_ICON_DND;
                        } else if (statusFlags == MmpProtocol.CONTACT_STATUS_INVISIBLE) {
                            icon = CONTACT_ICON_INVISIBLE;
                        } else if ((statusFlags & CONTACT_FLAG_ON_THE_PHONE) == CONTACT_FLAG_ON_THE_PHONE) {
                            icon = CONTACT_ICON_ON_THE_PHONE;
                        } else if ((statusFlags & CONTACT_FLAG_IN_SHOWER) == CONTACT_FLAG_IN_SHOWER) {
                            icon = CONTACT_ICON_IN_SHOWER;
                        } else if ((statusFlags & CONTACT_FLAG_EATING) == CONTACT_FLAG_EATING) {
                            icon = CONTACT_ICON_EATING;
                        } else if ((statusFlags & CONTACT_FLAG_AT_HOME) == CONTACT_FLAG_AT_HOME) {
                            icon = CONTACT_ICON_AT_HOME;
                        } else if ((statusFlags & CONTACT_FLAG_DEPRESSION) == CONTACT_FLAG_DEPRESSION) {
                            icon = CONTACT_ICON_DEPRESSION;
                        } else if ((statusFlags & CONTACT_FLAG_AT_WORK) == CONTACT_FLAG_AT_WORK) {
                            icon = CONTACT_ICON_AT_WORK;
                        } else if ((statusFlags & CONTACT_FLAG_FREE_CHAT) == CONTACT_FLAG_FREE_CHAT) {
                            icon = CONTACT_ICON_FREE_CHAT;
                        } else if ((statusFlags & CONTACT_FLAG_INVISIBLE) == CONTACT_FLAG_INVISIBLE) {
                            icon = CONTACT_ICON_INVISIBLE;
                        } else if ((statusFlags & CONTACT_FLAG_AWAY) == CONTACT_FLAG_AWAY) {
                            icon = CONTACT_ICON_AWAY;
                        } else if ((statusFlags & CONTACT_FLAG_DND) == CONTACT_FLAG_DND) {
                            icon = CONTACT_ICON_DND;
                        } else if ((statusFlags & 1) == 1) {
                            icon = CONTACT_ICON_ONLINE;
                        }
                        contact.defaultIcon = icon;
                        attrLen -= 4;
                        contact.highlighted = true;
                    }
                } else if (attrType == ATTR_GUID_LIST) {
                    byte[] blockedGuid = AppState.getBytes(StringResKeys.RES_BLOCKED_GUID);
                    byte[] unblockedGuid = AppState.getBytes(StringResKeys.RES_UNBLOCKED_GUID);
                    byte[] iconGuids = AppState.getBytes(StringResKeys.RES_AUTH_SLOT_GUIDS);
                    byte[] rawData = buffer.data;
                    int baseOffset = buffer.offset;
                    for (int guidOffset = 0; guidOffset < attrLen; guidOffset += GUID_SIZE) {
                        int pos = baseOffset + guidOffset;
                        for (int tableOffset = 0; tableOffset < GUID_TABLE_SIZE; tableOffset += GUID_SIZE) {
                            if (Utils.compareBytes(iconGuids, tableOffset, rawData, pos, GUID_SIZE)) {
                                contact.defaultIcon &= ICON_HIGH_MASK;
                                contact.defaultIcon |= (tableOffset >> 4) + ICON_CUSTOM_GUID_BASE;
                            }
                        }
                        if (Utils.compareBytes(blockedGuid, 0, rawData, pos, GUID_SIZE)) {
                            contact.isBlocked = true;
                        } else if (Utils.compareBytes(unblockedGuid, 0, rawData, pos, GUID_SIZE)) {
                            contact.isUnblocked = true;
                        }
                    }
                } else if (attrType == ATTR_CAPABILITIES) {
                    while (0 < attrLen - 4) {
                        int tlvType = buffer.readShortBE();
                        int tlvLen = buffer.readShortBE() & TLV_LENGTH_MASK;
                        int remaining = (attrLen - 2) - 2;
                        if ((tlvLen & TLV_CONTINUATION_BIT) != 0 || remaining < (tlvLen & TLV_CONTINUATION_BIT)) {
                            buffer.skip(tlvLen);
                            attrLen = remaining - tlvLen;
                        } else if (tlvType == TLV_TYPE_CAPABILITY_STRING) {
                            byte[] tlvData = new byte[tlvLen];
                            buffer.readIntoBytes(tlvData);
                            String capStr = StringUtils.intern(new String(tlvData));
                            if (capStr.startsWith(ObjectPool.unpackChars(28270022039266153L)) && (iconIndex = Utils.parseIntBounded(StringUtils.suffix(capStr, CAPABILITY_ICON_OFFSET), 0, CAPABILITY_ICON_MAX, -1)) >= 0) {
                                contact.defaultIcon &= ICON_HIGH_MASK;
                                contact.defaultIcon |= iconIndex + ICON_CUSTOM_GUID_BASE;
                            }
                            ObjectPool.releaseBytes(tlvData);
                            attrLen = remaining - tlvLen;
                        } else {
                            buffer.skip(tlvLen);
                            attrLen = remaining - tlvLen;
                        }
                    }
                }
                buffer.skip(attrLen);
            }
        } catch (Throwable ignored) {
        }
        contact.updateRenderState();
    }

}
