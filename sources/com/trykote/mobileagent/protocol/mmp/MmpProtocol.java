package com.trykote.mobileagent.protocol.mmp;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: d */
/* loaded from: MobileAgent_3.9.jar:d.class */
public final class MmpProtocol extends Account {

    // MMP progress states
    public static final int PROGRESS_CHECK_RESOURCES = 2;
    public static final int PROGRESS_WAIT_ACCOUNTS = 3;
    public static final int PROGRESS_WAIT_AUTH = 5;
    public static final int PROGRESS_CONNECTING = 6;
    public static final int PROGRESS_HANDSHAKING = 7;
    public static final int PROGRESS_PROCESSING = 8;

    // MMP status codes
    public static final int STATUS_OFFLINE = -1;
    public static final int STATUS_ONLINE = 0;
    public static final int STATUS_DND = 2;
    public static final int STATUS_AWAY = 4;
    public static final int STATUS_INVISIBLE = 16;
    public static final int STATUS_FREE_CHAT = 32;
    public static final int STATUS_AT_WORK = 256;

    /* renamed from: a */
    public int serverId;

    /* renamed from: b */
    public int messageSequence;

    /* renamed from: K */
    private byte[] encryptionKey;

    /* renamed from: c */
    public String[] connectionData;

    /* renamed from: d */
    public int contactListIndex;

    /* renamed from: e */
    public int groupSequenceId;

    /* renamed from: f */
    public final Hashtable contactsByIdMap;

    /* renamed from: g */
    public final Hashtable contactGroupsMap;

    /* renamed from: h */
    public final Hashtable additionalDataMap;

    /* renamed from: L */
    private int protocolVersion;

    /* renamed from: M */
    private int pendingVersionUpdate;

    /* renamed from: N */
    private int networkResourceMode;

    public MmpProtocol(int i, String str, String str2) {
        super(i, str, str2);
        this.lastError = STATUS_OFFLINE;
        this.configFlags = STATUS_ONLINE;
        this.protocolVersion = 4;
        MmpContactGroup group = new MmpContactGroup(this, 0, AppState.getString(StateKeys.STR_GROUP_DEFAULT));
        group.isSpecial = true;
        this.defaultGroup = group;
        this.contactsByIdMap = new Hashtable();
        this.contactGroupsMap = new Hashtable();
        this.additionalDataMap = new Hashtable();
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int getType() {
        return TYPE_MMP;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int setCredentials(String str, String str2) {
        int result = super.setCredentials(str, str2);
        if (result != 0) {
            return result;
        }
        return 0;
    }

    public MmpProtocol(ByteBuffer buffer) {
        super(buffer);
        this.protocolVersion = this.configFlags >>> 16;
        if (this.protocolVersion <= 0 || this.protocolVersion > 5) {
            this.protocolVersion = 4;
        }
        this.configFlags &= 65535;
        int groupCount = buffer.readInt();
        while (true) {
            groupCount--;
            if (groupCount < 0) {
                MmpContactGroup group = new MmpContactGroup(this, buffer);
                group.isSpecial = true;
                this.defaultGroup = group;
                this.contactsByIdMap = new Hashtable();
                this.contactGroupsMap = new Hashtable();
                this.additionalDataMap = new Hashtable();
                return;
            }
            addGroup((ContactGroup) new MmpContactGroup(this, buffer));
        }
    }

    @Override // p000.Account
    /* renamed from: a */
    public final Account serializeAccount(ByteBuffer buffer, boolean z, boolean z2) {
        this.configFlags = (this.configFlags & 65535) + (this.protocolVersion << 16);
        return super.serializeAccount(buffer, z, z2);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final ContactGroup createOnlineGroup() {
        return new MmpContactGroup(this, -1, AppState.getString(StateKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final ContactGroup createBlockedGroup() {
        return new MmpContactGroup(this, -2, AppState.getString(StateKeys.STR_GROUP_TEMPORARY));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final ContactGroup createOfflineGroup() {
        return new MmpContactGroup(this, -3, AppState.getString(StateKeys.STR_GROUP_IGNORE));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final ContactGroup createSpecialGroup() {
        return new MmpContactGroup(this, -4, AppState.getString(StateKeys.STR_GROUP_PHONE_CONTACTS));
    }

    @Override // p000.Account
    /* renamed from: a_ */
    public final int connect(int i) {
        int result = super.connect(i);
        if (result != 0) {
            return result;
        }
        return 0;
    }

    /* renamed from: f */
    public final int getIconResourceId() {
        if (this.reserved2 == 0) {
            return 256;
        }
        return 268 + this.reserved2;
    }

    @Override // p000.Account
    /* renamed from: g */
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        this.extras.removeAllElements();
        return STATUS_OFFLINE;
    }

    @Override // p000.Account
    /* renamed from: h */
    public final int getIconId() {
        if (this.progress >= PROGRESS_STARTING && this.progress < PROGRESS_CONNECTED) {
            return 265;
        }
        int iconRes = getIconResourceId();
        switch (this.lastError) {
            case STATUS_OFFLINE:
                return 255;
            case 1:
                return 16318464 | iconRes;
            case STATUS_DND:
                return 16449536 | iconRes;
            case STATUS_AWAY:
                return 16580608 | iconRes;
            case STATUS_INVISIBLE:
                return 16646144 | iconRes;
            case STATUS_FREE_CHAT:
                return 16384000 | iconRes;
            case STATUS_AT_WORK:
                return 16515072 | iconRes;
            case 8193:
                return 17104896 | iconRes;
            case 12288:
                return 16842752 | iconRes;
            case 16384:
                return 16908288 | iconRes;
            case 20480:
                return 16973824 | iconRes;
            case 24576:
                return 17039360 | iconRes;
            default:
                return iconRes;
        }
    }

    /* renamed from: a */
    public final ByteBuffer queueCommand(Object obj) {
        if (!isConnecting()) {
            return null;
        }
        Object[] cmdEntry = (Object[]) obj;
        ByteBuffer buffer = (ByteBuffer) cmdEntry[0];
        cmdEntry[0] = ResourceManager.integerOf(buffer.readIntBEAt());
        this.extras.addElement(obj);
        return buffer;
    }

    @Override // p000.Account
    /* renamed from: i */
    public final void loadData() throws Throwable {
        Contact authContact;
        if (this.progress <= PROGRESS_DISCONNECTED) {
            closeConnection();
            this.lastError = STATUS_OFFLINE;
        }
        switch (this.progress) {
            case PROGRESS_DISCONNECTED:
                this.dataBuffer.clear();
                if (this.connectionData != null) {
                    this.connectionData[0] = null;
                }
                this.connectionData = null;
                this.msgCount = 0;
                break;
            case PROGRESS_STARTING:
                AppController.needsRepaint = true;
                this.msgCount = 10;
                this.networkResourceMode = ResourceManager.checkForUpdates();
                if (this.networkResourceMode != -1 || this.networkResourceMode == 1) {
                    this.progress = PROGRESS_CHECK_RESOURCES;
                    break;
                }
                break;
            case PROGRESS_CHECK_RESOURCES:
                if (this.networkResourceMode != 0) {
                    Vector accounts = AccountManager.getMrimAccountList();
                    int idx = Utils.vectorSize(accounts);
                    while (true) {
                        idx--;
                        if (idx >= 0) {
                            Account account = (Account) accounts.elementAt(idx);
                            if (account.isConnected()) {
                                this.progress = PROGRESS_WAIT_ACCOUNTS;
                            } else if (!account.isConnecting()) {
                                accounts.removeElementAt(idx);
                            }
                        } else {
                            break;
                        }
                    }
                    if (Utils.vectorSize(accounts) == 0) {
                        EventDispatcher.postNotification(AppState.getString(StateKeys.STR_MMP_AUTH_ERROR));
                        this.progress = PROGRESS_DISCONNECTED;
                    }
                    ObjectPool.releaseVector(accounts);
                    break;
                } else {
                    this.progress = PROGRESS_WAIT_ACCOUNTS;
                    break;
                }
            case PROGRESS_WAIT_ACCOUNTS:
                new AsyncTask(AsyncTaskId.FETCH_HISTORY, new Object[]{this, ResourceManager.integerOf(0), this.login, getFormattedName()});
                this.msgCount = 20;
                this.progress = PROGRESS_WAIT_AUTH;
                AppController.needsRepaint = true;
                break;
            case PROGRESS_WAIT_AUTH:
                if (this.connectionData != null) {
                    this.msgCount = 70;
                    AppController.needsRepaint = true;
                    this.state = 28179;
                    this.encryptionKey = Base64.decode(this.connectionData[2]).toByteArray();
                    this.serverId = Integer.parseInt(this.connectionData[0]);
                    this.connection = new ConnectionThread(this.connectionData[1]);
                    this.progress = PROGRESS_CONNECTING;
                    this.connectionData = null;
                    break;
                }
                break;
            case PROGRESS_CONNECTING:
                if (this.connection.getState() != ConnectionThread.STATE_CONNECTED) {
                    if (this.connection.getState() <= ConnectionThread.STATE_CLOSED) {
                        closeConnection();
                        this.lastError = getDefaultError();
                        break;
                    }
                } else {
                    this.progress = PROGRESS_HANDSHAKING;
                    break;
                }
                break;
            case PROGRESS_HANDSHAKING:
                this.connection.drainInput(this.dataBuffer);
                ByteBuffer handshakePacket = this.dataBuffer.extractJPEG();
                if (handshakePacket != null) {
                    AppController.needsRepaint = true;
                    this.msgCount = 85;
                    AccountManager.recordInboundPacket((Account) this, handshakePacket);
                    if (handshakePacket.peekByteAt(1) == MmpCommand.PACKET_HANDSHAKE) {
                        long j = AppState.getBool(StateKeys.FLAG_WIFI_CONNECTION) ? 25000L : 60000L;
                        this.timeout = j;
                        this.deadline = System.currentTimeMillis() + j;
                        incrementSync();
                        byte[] key = this.encryptionKey;
                        sendData(ProtocolFactory.createPingPacket(this, MmpCommand.PACKET_HANDSHAKE).writeIntBE(1).writeShortBE(6).writeShortBE(key.length).writeBytes(key).updateLength());
                        this.encryptionKey = null;
                        sendData(ProtocolFactory.createAuthData(this));
                        sendData(ProtocolFactory.createMmpCommand(this, MmpCommand.SET_PREFS, new ByteBuffer().writeCompressed(PackedStringKeys.MMP_LOGIN_HEADER)));
                        sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SET_STATUS, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(268435456 | getConnectionModeValue()).writeCompressed(PackedStringKeys.MMP_AUTH_PACKET)), ResourceManager.integerOf(17)}));
                        this.contactListIndex = 0;
                        sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.GET_CONTACT_LIST, (ByteBuffer) null), ResourceManager.integerOf(6)}));
                        sendData(StringUtils.createContactInfoCmd(this, this.serverId));
                        this.progress = PROGRESS_PROCESSING;
                        break;
                    }
                }
                break;
        }
        if (this.progress < PROGRESS_PROCESSING) {
            return;
        }
        this.connection.drainInput(this.dataBuffer);
        if (this.networkResourceMode == 1) {
            Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
            if (Utils.vectorSize(onlineAccounts) == 0) {
                closeConnection();
                this.lastError = getDefaultError();
                return;
            }
            ObjectPool.releaseVector(onlineAccounts);
        }
        while (true) {
            ByteBuffer extractedPacket = this.dataBuffer.extractJPEG();
            ByteBuffer packet = extractedPacket;
            if (extractedPacket == null) {
                if (this.lastError != STATUS_OFFLINE && this.connection != null && this.connection.getState() == ConnectionThread.STATE_CLOSED) {
                    closeConnection();
                    this.lastError = getDefaultError();
                }
                if (this.timeout > 0 && isConnected() && TimerManager.isTimerExpired(this.deadline)) {
                    trySendData(ProtocolFactory.createPingPacket(this, MmpCommand.PACKET_KEEPALIVE));
                    return;
                }
                return;
            }
            AccountManager.recordInboundPacket((Account) this, packet);
            this.msgCount = 90;
            if (packet.peekByteAt(1) == MmpCommand.PACKET_COMMAND) {
                int commandId = (packet.peekByteAt(6) << 24) | (packet.peekByteAt(8) << 16) | (packet.peekByteAt(7) << 8) | packet.peekByteAt(9);
                int seqNum = packet.readIntBEAt();
                int flags = packet.peekShortBE(10);
                int headerFlags = packet.peekShortBE(10);
                packet.skip(16);
                if (headerFlags == 32768) {
                    packet.skip(packet.readShortBE());
                }
                packet = packet.compact();
                switch (commandId) {
                    case MmpCommand.AUTH_RESULT:
                        Vector vector = this.extras;
                        int size = vector.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                handleMmpResponse(packet, seqNum, 0);
                                break;
                            } else {
                                Object[] queuedCmd = (Object[]) vector.elementAt(size);
                                if (((Integer) queuedCmd[1]).intValue() == 17) {
                                    queuedCmd[0] = ResourceManager.integerOf(seqNum);
                                }
                            }
                        }
                    case MmpCommand.AUTH_REQUEST:
                        try {
                            int authParam1 = packet.readIntBE();
                            int authParam2 = packet.readIntBE();
                            String authName = AppState.emptyStr;
                            boolean z = false;
                            byte[] authData = AppState.emptyBytes;
                            while (packet.length > 0) {
                                int tlvType = packet.readShortBE();
                                int tlvLen = packet.readShortBE();
                                byte[] tlvBytes = tlvLen > 0 ? new byte[tlvLen] : null;
                                byte[] tlvValue = tlvBytes;
                                if (tlvBytes != null) {
                                    packet.readIntoBytes(tlvValue);
                                }
                                if (tlvType == 1) {
                                    authName = StringUtils.intern(new String(tlvValue));
                                } else if (tlvType == 2) {
                                    z = true;
                                } else if (tlvType == 3) {
                                    authData = tlvValue;
                                }
                                if (tlvType != 3) {
                                    ObjectPool.releaseBytes(tlvValue);
                                }
                            }
                            MrimAccount mrimAccount = (MrimAccount) AccountManager.getOnlineMrimAccounts().elementAt(0);
                            mrimAccount.trySendData(ResourceManager.createAuthPacket(mrimAccount, this, authParam1, authParam2, authName, z, authData));
                            break;
                        } catch (Throwable unused) {
                            break;
                        }
                    case MmpCommand.CONTACT_ONLINE:
                        parseContactStatus(packet.readLenPrefixStr(), packet);
                        break;
                    case MmpCommand.CONTACT_OFFLINE:
                        parseContactStatus(packet.readLenPrefixStr(), packet);
                        break;
                    case MmpCommand.ACK:
                        removeQueuedCommand(seqNum);
                        break;
                    case MmpCommand.FILE_TRANSFER:
                        handleFileTransfer(this, packet);
                        break;
                    case MmpCommand.MSG_DELIVERED:
                        long timestamp = packet.readLong();
                        packet.readShortBE();
                        updateStatus(packet.readLenPrefixStr(), timestamp, 64);
                        break;
                    case MmpCommand.MSG_READ:
                        long offlineTimestamp = packet.readLong();
                        packet.readShortBE();
                        updateStatus(packet.readLenPrefixStr(), offlineTimestamp, 128);
                        break;
                    case MmpCommand.CONTACT_STATUS_CHANGE:
                        packet.skip(10);
                        String contactId = packet.readLenPrefixStr();
                        if (packet.readShortBE() != 0) {
                            deleteContact(contactId);
                            break;
                        } else {
                            markRead(contactId);
                            break;
                        }
                    case MmpCommand.CONTACT_LIST_RESPONSE:
                        if (this.contactListIndex == 0) {
                            removeAllContacts();
                        }
                        handleMmpResponse(packet, seqNum, flags);
                        break;
                    case MmpCommand.CONTACT_INFO_RESPONSE:
                        handleMmpResponse(packet, seqNum, 0);
                        break;
                    case MmpCommand.TYPING_NOTIFY:
                        Contact typingContact = getContact((Object) packet.readLenPrefixStr());
                        if (null != typingContact) {
                            typingContact.performAction();
                            break;
                        }
                        break;
                    case MmpCommand.MESSAGE_RECEIVED:
                        ResourceManager.playNotificationSound(3);
                        onMessage(packet.readLenPrefixStr(), 0L, packet.readVarLenStr());
                        break;
                    case MmpCommand.AUTH_RECEIVED:
                        String senderId = packet.readLenPrefixStr();
                        byte authFlag = packet.readByte();
                        onMessage(senderId, 0L, ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_MMP_FILE_TRANSFER)).append(AppState.getString(authFlag == 1 ? 484 : 485)).append(packet.readVarLenStr())));
                        if (authFlag == 1 && null != (authContact = getContact((Object) senderId))) {
                            authContact.performAction();
                            break;
                        }
                        break;
                    case MmpCommand.SYSTEM_MESSAGE:
                        onMessage(packet.readLenPrefixStr(), 0L, AppState.getString(StateKeys.STR_MMP_SYSTEM_MESSAGE));
                        break;
                    case MmpCommand.SPAM_REPORT_ACK:
                        EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_MMP_SPAM_REPORT)).append(1501).append('/').append(packet.readShortBE()).append(AppState.getString(StateKeys.STR_MMP_SPAM_SUFFIX))));
                        removeQueuedCommand(seqNum);
                        break;
                    case MmpCommand.SEARCH_RESPONSE:
                        handleMmpResponse(packet, seqNum, flags);
                        break;
                }
                AppController.needsLayoutUpdate = true;
            } else {
                if (packet.peekByteAt(1) == MmpCommand.PACKET_NOTIFICATION) {
                    handleStatusPacket(packet);
                    AppController.needsLayoutUpdate = true;
                }
            }
            packet.clear();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x0130  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void parseContactStatus(String str, ByteBuffer buffer) {
        int iconIndex;
        int icon = 255;
        MmpContact contact = (MmpContact) getContact((Object) str);
        if (contact == null || contact.isOnline()) {
            return;
        }
        boolean wasHighlighted = contact.highlighted;
        contact.defaultIcon = 255;
        contact.highlighted = false;
        contact.isBlocked = false;
        contact.isUnblocked = false;
        contact.dirty = true;
        try {
            buffer.skip(2);
            int attrCount = buffer.readShortBE();
            for (int i2 = 0; i2 < attrCount; i2++) {
                int attrType = buffer.readShortBE();
                int attrLen = buffer.readShortBE();
                if (attrType == 6) {
                    int statusFlags = buffer.readIntBE() & 65535;
                    if (statusFlags == 0) {
                        icon = 256;
                        contact.defaultIcon = icon;
                        attrLen -= 4;
                        contact.highlighted = true;
                    } else {
                        if (statusFlags == 19) {
                            icon = 16449792;
                        } else if (statusFlags == 17) {
                            icon = 16646400;
                        } else if ((statusFlags & 24576) == 24576) {
                            icon = 17039360;
                        } else if ((statusFlags & 20480) == 20480) {
                            icon = 16973824;
                        } else if ((statusFlags & 16384) == 16384) {
                            icon = 16908288;
                        } else if ((statusFlags & 12288) == 12288) {
                            icon = 16842752;
                        } else if ((statusFlags & 8192) == 8192) {
                            icon = 17104896;
                        } else if ((statusFlags & 256) == 256) {
                            icon = 16515328;
                        } else if ((statusFlags & 32) == 32) {
                            icon = 16384256;
                        } else if ((statusFlags & 16) == 16) {
                            icon = 16646400;
                        } else if ((statusFlags & 4) == 4) {
                            icon = 16580864;
                        } else if ((statusFlags & 2) == 2) {
                            icon = 16449792;
                        } else if ((statusFlags & 1) == 1) {
                            icon = 16318720;
                        }
                        contact.defaultIcon = icon;
                        attrLen -= 4;
                        contact.highlighted = true;
                    }
                } else if (attrType == 13) {
                    byte[] blockedGuid = AppState.getBytes(StateKeys.RES_BLOCKED_GUID);
                    byte[] unblockedGuid = AppState.getBytes(StateKeys.RES_UNBLOCKED_GUID);
                    byte[] iconGuids = AppState.getBytes(StateKeys.RES_AUTH_SLOT_GUIDS);
                    byte[] rawData = buffer.data;
                    int baseOffset = buffer.offset;
                    for (int i4 = 0; i4 < attrLen; i4 += 16) {
                        int pos = baseOffset + i4;
                        for (int i6 = 0; i6 < 576; i6 += 16) {
                            if (Utils.compareBytes(iconGuids, i6, rawData, pos, 16)) {
                                contact.defaultIcon &= -65536;
                                contact.defaultIcon |= (i6 >> 4) + 269;
                            }
                        }
                        if (Utils.compareBytes(blockedGuid, 0, rawData, pos, 16)) {
                            contact.isBlocked = true;
                        } else if (Utils.compareBytes(unblockedGuid, 0, rawData, pos, 16)) {
                            contact.isUnblocked = true;
                        }
                    }
                } else if (attrType == 29) {
                    while (0 < attrLen - 4) {
                        int tlvType = buffer.readShortBE();
                        int tlvLen = buffer.readShortBE() & 255;
                        int remaining = (attrLen - 2) - 2;
                        if ((tlvLen & 128) != 0 || remaining < (tlvLen & 128)) {
                            buffer.skip(tlvLen);
                            attrLen = remaining - tlvLen;
                        } else if (tlvType == 14) {
                            byte[] tlvData = new byte[tlvLen];
                            buffer.readIntoBytes(tlvData);
                            String capStr = StringUtils.intern(new String(tlvData));
                            if (capStr.startsWith(ObjectPool.unpackChars(28270022039266153L)) && (iconIndex = Utils.parseIntBounded(StringUtils.suffix(capStr, 7), 0, 23, -1)) >= 0) {
                                contact.defaultIcon &= -65536;
                                contact.defaultIcon |= iconIndex + 269;
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
        } catch (Throwable unused) {
        }
        contact.updateRenderState();
        if (wasHighlighted || !contact.highlighted) {
            return;
        }
        ResourceManager.playNotificationSound(1);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateSend(Contact contactParam, String str, long j) {
        ByteBuffer command;
        int result = super.validateSend(contactParam, str, j);
        if (0 != result) {
            return result;
        }
        this.sentCount++;
        MmpContact contact = (MmpContact) contactParam;
        int i = contact.isBlocked ? 1 : 0;
        int i2 = contact.isUnblocked ? 2 : 1;
        ByteBuffer headerBuffer = new ByteBuffer().writeLong(j).writeShortBE(i2).writeByteLenStr(contact.identifier);
        ByteBuffer bodyBuffer = new ByteBuffer();
        if (i2 == 1) {
            if (i == 1) {
                bodyBuffer.writeShortBE(2).writeShortBE(0).writeAsShorts(str);
            } else {
                bodyBuffer.writeIntLE(0).writeCharBytes(str);
            }
            headerBuffer.writeShortBE(2).writeShortBE(i + 9 + bodyBuffer.length).writeShortBE(1281).writeShortBE(i + 1);
            if (i == 1) {
                headerBuffer.writeShortBE(262);
            } else {
                headerBuffer.writeByte(1);
            }
            command = ProtocolFactory.createMmpCommand(this, MmpCommand.SEND_MESSAGE, headerBuffer.writeShortBE(257).writeBufferShortLen(bodyBuffer).writeShortBE(6).writeShortBE(0));
        } else {
            if (i == 1) {
                bodyBuffer.writeUTFNoLen(str);
            } else {
                bodyBuffer.writeCharBytes(str);
            }
            bodyBuffer.writeByte(0);
            int i3 = bodyBuffer.length;
            int i4 = i3 - (i == 1 ? 0 : 42);
            command = ProtocolFactory.createMmpCommand(this, MmpCommand.SEND_MESSAGE, headerBuffer.writeShortBE(5).writeShortBE(i4 + 143).writeShortBE(0).writeLong(j).writeCompressed(906).writeShortBE(10).writeShortBE(2).writeShortBE(1).writeShortBE(15).writeShortBE(0).writeShortBE(10001).writeShortBE(i4 + 103).writeShortLE(27).writeShortLE(8).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortBE(0).writeIntLE(3).writeByte(0).writeShortBE(0).writeIntLE(14).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortLE(1).writeShortLE(getConnectionModeValue()).writeShortLE(1).writeShortLE(i3).writeBuffer(bodyBuffer).writeCompressed(i == 0 ? 526807 : 3279327).writeShortBE(3).writeShortBE(0));
        }
        return trySendData(command);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateModify(Contact contactParam, Object[] params) {
        int result = super.validateModify(contactParam, params);
        if (0 != result) {
            return result;
        }
        MmpContact contact = (MmpContact) contactParam;
        String str = (String) params[0];
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, contact.encodeContactUpdate(3, str, contact.onlineSemaphore)), ResourceManager.integerOf(0), contact, str}));
    }

    /* renamed from: b */
    public final int updateConnectionMode(int i) {
        if (i == STATUS_AT_WORK) {
            scheduleVersionUpdate(3);
        } else if (this.protocolVersion == 3) {
            scheduleVersionUpdate(4);
        }
        this.configFlags = i;
        if (isConnected()) {
            trySendData(sendContactListRequest(this.groupSequenceId));
            trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SET_STATUS, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(268435456 | getConnectionModeValue())), ResourceManager.integerOf(17)}));
            return trySendData(ProtocolFactory.createAuthData(this));
        }
        if (isConnecting()) {
            return 487;
        }
        return connect(0);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateDelete(Contact contactParam) {
        if (!isConnected()) {
            return 299;
        }
        MmpContact contact = (MmpContact) contactParam;
        AppState.pool[StateKeys.SLOT_REG_PARAM_2] = ContactInfo.createAccountInfo(this).setMmpContactIdStr(contact.identifier);
        return trySendData(StringUtils.createContactInfoCmd(this, Utils.parseInt((Object) contact.identifier)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateMove(Contact contactParam, ContactGroup groupParam, ContactGroup targetGroup) {
        int result = super.validateMove(contactParam, groupParam, targetGroup);
        if (0 != result) {
            return result;
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        MmpContact contact = (MmpContact) contactParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, contact.encodeContactUpdate(2, contact.displayName, contact.onlineSemaphore)), ResourceManager.integerOf(10), contact, groupParam, targetGroup}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupRename(ContactGroup groupParam, String str) {
        int result = super.validateGroupRename(groupParam, str);
        if (result != 0) {
            return result;
        }
        MmpContactGroup group = (MmpContactGroup) groupParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, group.createUpdatePacket(str, -1, -1)), ResourceManager.integerOf(1), group, str}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupCreate(String str) {
        int result = super.validateGroupCreate(str);
        if (result != 0) {
            return result;
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        return trySendData(ResourceManager.sendAddGroupCommand(this, str));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupDelete(ContactGroup groupParam) {
        int result = super.validateGroupDelete(groupParam);
        if (0 != result) {
            return result;
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        MmpContactGroup group = (MmpContactGroup) groupParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, group.createUpdatePacket(group.name, -1, -1)), ResourceManager.integerOf(2), group}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateResend(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        int result = super.validateResend((Contact) contact);
        if (0 != result) {
            return result;
        }
        if (contact.isOnline()) {
            removeContact((Contact) contact, true);
            return 0;
        }
        if (contact.canUnblock()) {
            trySendData(unblockContact(this, contact));
        }
        if (contact.canDelete()) {
            trySendData(deleteContact(this, contact));
        }
        if (contact.canBlock()) {
            trySendData(blockContact(this, contact));
        }
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, contact.encodeContactUpdate(2, contact.displayName, contact.onlineSemaphore)), ResourceManager.integerOf(5), contact}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupAdd(String str, String str2, String str3, ContactGroup groupParam, boolean z) {
        int result = super.validateGroupAdd(str, str2, str3, groupParam, z);
        if (0 != result) {
            return result;
        }
        trySendData(ProtocolFactory.createMmpCommand(this, MmpCommand.AUTH_GRANT, new ByteBuffer().writeByteLenStr(str).writeIntLE(0)));
        MmpContact contact = (MmpContact) getContact((Object) str);
        if (null != contact && !contact.isOnline()) {
            return trySendData(createSendMessageCmd(this, contact, str3));
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        MmpContactGroup group = (MmpContactGroup) groupParam;
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortString(str).writeShortBE(group.groupId);
        int uniqueId = generateUniqueGroupId();
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.ADD_CONTACT, wrapperBuffer.writeShortBE(uniqueId).writeShortBE(0).writeBufferShortLen(new ByteBuffer().writeShortBE(102).writeShortBE(0).writeShortBE(347).writeShortBE(1).writeByte(32).writeShortBE(305).writeUTF(str2))), ResourceManager.integerOf(14), str, str2, group, ResourceManager.integerOf(uniqueId), str3}));
    }

    /* renamed from: j */
    public final int getConnectionModeValue() {
        switch (this.configFlags) {
            case 2:
                return 19;
            case 4:
                return 5;
            case 16:
                return 17;
            default:
                return this.configFlags & 65535;
        }
    }

    /* renamed from: k */
    public final int generateUniqueGroupId() {
        boolean z;
        int candidateId;
        do {
            z = false;
            candidateId = (Utils.nextRandom() & 28671) + 4096;
            if (candidateId == this.groupSequenceId) {
                z = true;
            } else {
                Vector vector = this.groups;
                int size = vector.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    MmpContactGroup group = (MmpContactGroup) vector.elementAt(size);
                    if (group.groupId == candidateId) {
                        z = true;
                    }
                    Vector vector2 = group.contacts;
                    int size2 = vector2.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        MmpContact contact = (MmpContact) vector2.elementAt(size2);
                        if (contact.userId == candidateId || contact.canDelete == candidateId || contact.canBlock == candidateId || contact.canUnblock == candidateId) {
                            z = true;
                        }
                    }
                }
            }
        } while (z);
        return candidateId;
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int validateContactDelete(Contact contactParam) {
        if (contactParam.isOnline()) {
            return 310;
        }
        return trySendData(unblockContact(this, (MmpContact) contactParam));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int validateContactBlock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (contact.canBlock() && !contact.canDelete()) {
            trySendData(blockContact(this, contact));
        }
        return trySendData(deleteContact(this, contact));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int validateContactUnblock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (!contact.canBlock() && contact.canDelete()) {
            trySendData(deleteContact(this, contact));
        }
        return trySendData(blockContact(this, contact));
    }

    @Override // p000.Account
    /* renamed from: l */
    public final int disconnect() {
        if (this.connectionData != null) {
            this.connectionData[0] = null;
        }
        this.connectionData = null;
        int result = super.disconnect();
        if (0 != result) {
            return result;
        }
        trySendData(ProtocolFactory.createPingPacket(this, 4));
        closeConnection();
        this.lastError = getDefaultError();
        return 0;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateObject(Object obj) {
        String[] searchParams = (String[]) obj;
        ByteBuffer searchBuffer = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(2000).writeShortBE(0).writeShortLE(1375);
        String str = searchParams[0];
        if (str.length() > 0) {
            searchBuffer.writeShortBE(13825).writeShortLE(4).writeIntLE(Utils.parseInt((Object) str));
        } else {
            searchBuffer.writeShortBE(12290).writeShortLE(1).writeByte(searchParams[1].length()).writeProtocolStr(21505, searchParams[2]).writeProtocolStr(16385, searchParams[3]).writeProtocolStr(18945, searchParams[4]).writeProtocolStr(24065, searchParams[5]).writeProtocolStr(36865, searchParams[6]).writeProtocolStr(9730, searchParams[7]);
        }
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortBE(1);
        int i = searchBuffer.length;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, wrapperBuffer.writeShortBE(i + 2).writeShortLE(i).writeBuffer(searchBuffer)), ResourceManager.integerOf(9)}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final Contact newContact(String str) {
        ContactGroup groupParam = this.defaultGroup;
        MmpContact contact = new MmpContact(this, -1, -1, str, str, true);
        groupParam.addContact((Object) contact);
        ByteBuffer queryBuffer = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(2000).writeShortBE(0).writeShortLE(1375).writeShortBE(13825).writeShortLE(4).writeIntLE(Utils.parseInt((Object) str));
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortBE(1);
        int i = queryBuffer.length;
        trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, wrapperBuffer.writeShortBE(i + 2).writeShortLE(i).writeBuffer(queryBuffer)), ResourceManager.integerOf(21)}));
        return contact;
    }

    @Override // p000.Account
    /* renamed from: c */
    public final void onError(int i) {
        switch (i) {
            case 0:
                updateConnectionMode(0);
                break;
            case 1:
                updateConnectionMode(32);
                break;
            case 2:
                updateConnectionMode(1);
                break;
            case 3:
                updateConnectionMode(2);
                break;
            case 4:
                updateConnectionMode(256);
                break;
            default:
                disconnect();
                break;
        }
    }

    /* renamed from: m */
    public final int getPendingVersion() {
        if (this.pendingVersionUpdate > 0 && this.pendingVersionUpdate <= 5) {
            this.protocolVersion = this.pendingVersionUpdate;
            this.pendingVersionUpdate = 0;
        }
        return this.protocolVersion;
    }

    @Override // p000.Account
    /* renamed from: n */
    public final int getExtType() {
        return 369 + this.protocolVersion;
    }

    /* renamed from: d */
    public final int scheduleVersionUpdate(int i) {
        this.pendingVersionUpdate = i;
        if (!isConnected()) {
            return 0;
        }
        trySendData(sendContactListRequest(this.groupSequenceId));
        return ScreenId.CONTACT_LIST;
    }

    @Override // p000.Account
    /* renamed from: o */
    public final void resetCounters() {
        super.resetCounters();
        this.syncSeq = 0;
    }

    @Override // p000.Account
    /* renamed from: p */
    public final int getSessionStringKey() {
        return 197381;
    }

    public final ByteBuffer sendContactListRequest(int i) {
        return queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, new ByteBuffer().writeIntLE(0).writeShortBE(i).writeShortBE(4).writeShortBE(5).writeShortBE(202).writeShortBE(1).writeByte(getPendingVersion())), ResourceManager.integerOf(20)});
    }

    public final void removeQueuedCommand(int i) {
        Vector vector = this.extras;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            if (((Integer) ((Object[]) vector.elementAt(size))[0]).intValue() == i) {
                vector.removeElementAt(size);
            }
        }
    }

    public final void handleMmpResponse(ByteBuffer buf, int i, int i2) {
        Object[] objArr;
        boolean z = false;
        boolean z2;
        MmpContactGroup group;
        Vector vector = this.extras;
        int size = vector.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                objArr = (Object[]) vector.elementAt(size);
            }
        } while (((Integer) objArr[0]).intValue() != i);
        boolean z3 = true;
        switch (((Integer) objArr[1]).intValue()) {
            case 0:
                int status = buf.readShortBE();
                if (status == 0) {
                    ((MmpContact) objArr[2]).setDisplayName((String) objArr[3]);
                } else {
                    EventDispatcher.postRenameError(objArr, status);
                }
                z = true;
                z3 = z;
                break;
            case 1:
                int status2 = buf.readShortBE();
                if (status2 == 0) {
                    ((MmpContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                } else {
                    EventDispatcher.postRenameError(objArr, status2);
                }
                z = true;
                z3 = z;
                break;
            case 2:
                int status3 = buf.readShortBE();
                if (status3 == 0) {
                    removeGroup((ContactGroup) objArr[2]);
                    trySendData(ResourceManager.createSyncGroupsCmd(this));
                } else {
                    EventDispatcher.postDeleteError(objArr, status3);
                }
                z = true;
                z3 = z;
                break;
            case 3:
                int status4 = buf.readShortBE();
                if (status4 == 0) {
                    trySendData(ResourceManager.createSyncContactsCmd(this));
                } else {
                    EventDispatcher.postOperationError(status4);
                }
                z = true;
                z3 = z;
                break;
            case 4:
                int status5 = buf.readShortBE();
                if (status5 == 0) {
                    addGroup(new MmpContactGroup(this, ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    trySendData(ResourceManager.createSyncGroupsCmd(this));
                } else {
                    EventDispatcher.postAddGroupError(objArr, status5);
                }
                z = true;
                z3 = z;
                break;
            case 5:
                int status6 = buf.readShortBE();
                if (status6 == 0) {
                    removeContact((Contact) objArr[2], true);
                    trySendData(ResourceManager.createSyncContactsCmd(this));
                } else {
                    EventDispatcher.postDeleteError(objArr, status6);
                }
                z = true;
                z3 = z;
                break;
            case 6:
                boolean z4 = (i2 & 1) == 0;
                buf.skip(1);
                Vector results = ObjectPool.newVector();
                int contactCount = buf.readShortBE();
                for (int i3 = 0; i3 < contactCount; i3++) {
                    String name = buf.readVarLenStr();
                    int groupId = buf.readShortBE();
                    int contactId = buf.readShortBE();
                    int entryType = buf.readShortBE();
                    int dataRemaining = buf.readShortBE();
                    switch (entryType) {
                        case 0:
                            String displayName = name;
                            boolean z5 = false;
                            while (dataRemaining > 0) {
                                int attrType = buf.readShortBE();
                                int dataLen = buf.peekShortBE(0);
                                if (attrType == 305) {
                                    displayName = buf.readVarLenStr();
                                } else {
                                    if (attrType == 102) {
                                        z5 = true;
                                    }
                                    buf.skip(dataLen + 2);
                                }
                                dataRemaining -= dataLen + 4;
                            }
                            results.addElement(new MmpContact(this, contactId, groupId, name, displayName, z5));
                            continue;
                        case 1:
                            if (groupId != 0) {
                                this.groups.addElement(new MmpContactGroup(this, groupId, name));
                            }
                            buf.skip(dataRemaining);
                            continue;
                        case 2:
                            this.contactsByIdMap.put(name, ResourceManager.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                        case 3:
                            this.contactGroupsMap.put(name, ResourceManager.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                        case 4:
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        default:
                            buf.skip(dataRemaining);
                            continue;
                        case 14:
                            this.additionalDataMap.put(name, ResourceManager.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                    }
                    while (dataRemaining > 0) {
                        if (buf.readShortBE() == 202) {
                            this.groupSequenceId = contactId;
                        }
                        int entryLen = buf.readShortBE();
                        buf.skip(entryLen);
                        dataRemaining -= entryLen + 4;
                    }
                }
                this.contactListIndex = contactCount;
                int size2 = results.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        if (z4) {
                            sendData(ProtocolFactory.createMmpCommand(this, MmpCommand.CONTACT_LIST_ACK, (ByteBuffer) null));
                            int i4 = this.groupSequenceId;
                            if (i4 != 0) {
                                sendData(sendContactListRequest(i4));
                            }
                            sendData(ProtocolFactory.createMmpCommand(this, MmpCommand.SET_CAPABILITIES, new ByteBuffer().writeCompressed(PackedStringKeys.MMP_TRANSFER_HEADER)));
                            sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(this.serverId).writeShortLE(60).writeShortBE(0)), ResourceManager.integerOf(8)}));
                            Enumeration elements = this.contactMap.elements();
                            while (elements.hasMoreElements()) {
                                Hashtable hashtable = this.contactsByIdMap;
                                MmpContact mmpContact = (MmpContact) elements.nextElement();
                                Object obj = hashtable.get(mmpContact.identifier);
                                if (null != obj) {
                                    mmpContact.canDelete = ((Integer) obj).intValue();
                                }
                                Object obj2 = this.contactGroupsMap.get(mmpContact.identifier);
                                if (null != obj2) {
                                    mmpContact.canBlock = ((Integer) obj2).intValue();
                                }
                                Object obj3 = this.additionalDataMap.get(mmpContact.identifier);
                                if (null != obj3) {
                                    mmpContact.canUnblock = ((Integer) obj3).intValue();
                                }
                            }
                            this.contactsByIdMap.clear();
                            this.contactGroupsMap.clear();
                            this.additionalDataMap.clear();
                            if (this.groups.size() == 0) {
                                sendData(ResourceManager.sendAddGroupCommand(this, AppState.getString(StateKeys.STR_RES_MENU_ITEM_2)));
                            }
                            this.progress = PROGRESS_CONNECTED;
                            this.msgCount = PROGRESS_CONNECTED;
                        }
                        ObjectPool.releaseVector(results);
                        z = z4;
                        z3 = z;
                        break;
                    } else {
                        MmpContact mmpContact2 = (MmpContact) results.elementAt(size2);
                        int i5 = mmpContact2.onlineSemaphore;
                        int size3 = this.groups.size();
                        while (true) {
                            size3--;
                            if (size3 < 0) {
                                group = null;
                                break;
                            } else {
                                MmpContactGroup group2 = (MmpContactGroup) getGroup(size3);
                                if (group2.groupId == i5) {
                                    group = group2;
                                    break;
                                }
                            }
                        }
                        MmpContactGroup group3 = group;
                        if (null != group) {
                            group3.addContact((Object) mmpContact2);
                        }
                    }
                }
            case 7:
                buf.skip(10);
                if (buf.readShortLE() == 2010) {
                    buf.readShortLE();
                    int subType = buf.readShortLE();
                    buf.readByte();
                    ContactInfo contactInfo = (ContactInfo) AppState.pool[StateKeys.SLOT_REG_PARAM_2];
                    ContactInfo contactInfo2 = contactInfo;
                    if (contactInfo == null) {
                        contactInfo2 = ContactInfo.createAccountInfo(this);
                    }
                    switch (subType) {
                        case 200:
                            String nickName = buf.readPascalStr();
                            ContactInfo contactInfo3 = contactInfo2.setDisplayName(nickName).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr()).setCustomField1(buf.readPascalStr());
                            buf.readPascalStr();
                            contactInfo3.setJobTitle(buf.readPascalStr()).setCustomField2(buf.readPascalStr()).setCustomField3(buf.readPascalStr()).setCustomField4(buf.readPascalStr());
                            if (this.serverId == ((Integer) objArr[2]).intValue()) {
                                setDisplayName(nickName);
                                break;
                            }
                            break;
                        case 220:
                            ContactInfo contactInfo4 = contactInfo2.setAge(buf.readShortLE()).setMaritalStatus(buf.readByte()).setCustomField6(buf.readPascalStr());
                            int birthYear = buf.readShortLE();
                            byte birthDay = buf.readByte();
                            byte birthMonth = buf.readByte();
                            if (birthMonth >= 0) {
                                contactInfo4.setCompany(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Utils.zeroPad(birthMonth + 1)).append('/').append(Utils.zeroPad(birthDay)).append('/').append(birthYear)));
                                break;
                            }
                            break;
                        case 230:
                            contactInfo2.setCustomField5(buf.readPascalStr());
                            break;
                    }
                    boolean z6 = (i2 & 1) == 0;
                    boolean z7 = z6;
                    if (z6) {
                        AppState.pool[StateKeys.SLOT_REG_PARAM_1] = AppState.pool[StateKeys.SLOT_REG_PARAM_2];
                        AppState.pool[StateKeys.SLOT_REG_PARAM_1] = AppState.pool[StateKeys.SLOT_REG_PARAM_2];
                        AppState.clearIndex(StateKeys.SLOT_REG_PARAM_2);
                    }
                    z = z7;
                } else {
                    z = true;
                }
                z3 = z;
                break;
            case 8:
                int i6 = this.reserved1;
                this.reserved1 = i6 + 1;
                if (0 != i6) {
                    AppState.setInt(StateKeys.FLAG_MRIM_DATA_LOADED, 0);
                }
                buf.skip(10);
                int subType3 = buf.readShortLE();
                buf.readShortLE();
                switch (subType3) {
                    case 65:
                        int resultCode = buf.readInt();
                        int year = buf.readShortLE();
                        byte month = buf.readByte();
                        byte dayOfYear = buf.readByte();
                        byte hour = buf.readByte();
                        byte minute = buf.readByte();
                        byte b = (year % 4 != 0 || year == 2000) ? (byte) 28 : (byte) 29;
                        int i7 = (((((year - 1970) * 365) + ((year - 1968) / 4)) + dayOfYear) + 28) - b;
                        if (year >= 2000) {
                            i7--;
                        }
                        byte[] monthDays = AppState.getBytes(StateKeys.RES_MONTH_DAYS);
                        int i8 = 0;
                        while (i8 < month - 1) {
                            i7 += i8 == 1 ? b : monthDays[i8];
                            i8++;
                        }
                        long j = 1000 * ((86400 * i7) + (hour * 3600) + (minute * 60));
                        buf.readShortBE();
                        if (resultCode != 1004) {
                            onMessage(Integer.toString(resultCode), j, buf.readModifiedStr());
                        }
                        z = false;
                        break;
                    case 66:
                        AppState.setInt(StateKeys.FLAG_MRIM_DATA_LOADED, 1);
                        trySendData(ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(this.serverId).writeShortLE(62).writeShortBE(0)));
                        break;
                    default:
                        z = false;
                        break;
                }
                z3 = z;
                break;
            case 9:
                Vector searchResults = AppState.getVector(StateKeys.SLOT_REG_PARAM_3);
                buf.skip(10);
                if (buf.readShortLE() == 2010) {
                    buf.readShortLE();
                    int subType5 = buf.readShortLE();
                    if ((420 == subType5 || 430 == subType5) && buf.readByte() == 10) {
                        buf.readShortBE();
                        ContactInfo searchResult = ContactInfo.createAccountInfo(this).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
                        buf.readByte();
                        searchResults.addElement(searchResult.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE()));
                    }
                    if (subType5 == 430) {
                        AppState.pool[StateKeys.SLOT_REG_PARAM_4] = AppState.getVector(StateKeys.SLOT_REG_PARAM_3);
                        AppState.clearIndex(StateKeys.SLOT_REG_PARAM_3);
                        z2 = true;
                        z3 = z2;
                        break;
                    } else {
                        z2 = false;
                        z3 = z2;
                    }
                } else {
                    z2 = true;
                    z3 = z2;
                }
                break;
            case 10:
                int status14 = buf.readShortBE();
                if (status14 == 0) {
                    MmpContact mmpContact3 = (MmpContact) objArr[2];
                    MmpContactGroup srcGroup4 = (MmpContactGroup) objArr[3];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, srcGroup4.createUpdatePacket(srcGroup4.name, mmpContact3.userId, -1)), ResourceManager.integerOf(11), mmpContact3, srcGroup4, objArr[4]}));
                } else {
                    EventDispatcher.postRenameError(objArr, status14);
                }
                z = true;
                z3 = z;
                break;
            case 11:
                int status15 = buf.readShortBE();
                if (status15 == 0) {
                    MmpContact mmpContact4 = (MmpContact) objArr[2];
                    Object obj4 = objArr[3];
                    MmpContactGroup destGroup5 = (MmpContactGroup) objArr[4];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.ADD_CONTACT, mmpContact4.encodeContactUpdate(4, mmpContact4.displayName, destGroup5.groupId)), ResourceManager.integerOf(12), mmpContact4, obj4, destGroup5}));
                } else {
                    EventDispatcher.postRenameError(objArr, status15);
                }
                z = true;
                z3 = z;
                break;
            case 12:
                int status16 = buf.readShortBE();
                if (status16 == 0) {
                    MmpContact mmpContact5 = (MmpContact) objArr[2];
                    Object obj5 = objArr[3];
                    MmpContactGroup destGroup6 = (MmpContactGroup) objArr[4];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, destGroup6.createUpdatePacket(destGroup6.name, -1, mmpContact5.userId)), ResourceManager.integerOf(13), mmpContact5, obj5, destGroup6}));
                } else {
                    EventDispatcher.postRenameError(objArr, status16);
                }
                z = true;
                z3 = z;
                break;
            case 13:
                int status17 = buf.readShortBE();
                if (status17 == 0) {
                    MmpContactGroup srcGroup7 = (MmpContactGroup) objArr[3];
                    MmpContact mmpContact6 = (MmpContact) objArr[2];
                    srcGroup7.removeElement(mmpContact6);
                    MmpContactGroup destGroup8 = (MmpContactGroup) objArr[4];
                    destGroup8.addContact((Object) mmpContact6);
                    mmpContact6.onlineSemaphore = destGroup8.groupId;
                    trySendData(ResourceManager.createSyncContactsCmd(this));
                } else {
                    EventDispatcher.postRenameError(objArr, status17);
                }
                z = true;
                z3 = z;
                break;
            case 14:
                int status18 = buf.readShortBE();
                if (status18 == 0) {
                    MmpContactGroup destGroup9 = (MmpContactGroup) objArr[4];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, destGroup9.createUpdatePacket(destGroup9.name, -1, ((Integer) objArr[5]).intValue())), ResourceManager.integerOf(15), objArr[2], objArr[3], destGroup9, objArr[5], objArr[6]}));
                } else {
                    EventDispatcher.postRenameError(objArr, status18);
                }
                z = true;
                z3 = z;
                break;
            case 15:
                int status19 = buf.readShortBE();
                if (status19 == 0) {
                    MmpContactGroup destGroup10 = (MmpContactGroup) objArr[4];
                    MmpContact mmpContact7 = new MmpContact(this, ((Integer) objArr[5]).intValue(), destGroup10.groupId, (String) objArr[2], (String) objArr[3], true);
                    destGroup10.addContact((Object) mmpContact7);
                    trySendData(ResourceManager.createSyncContactsCmd(this));
                    trySendData(ResourceManager.createGetContactsCmd(this));
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, mmpContact7.encodeContactUpdate(5, mmpContact7.displayName, mmpContact7.onlineSemaphore)), ResourceManager.integerOf(16), objArr[2], objArr[3], objArr[4], objArr[5], objArr[6], mmpContact7}));
                    trySendData(ResourceManager.createSyncContactsCmd(this));
                } else {
                    EventDispatcher.postRenameError(objArr, status19);
                }
                z = true;
                z3 = z;
                break;
            case 16:
                int status20 = buf.readShortBE();
                if (status20 == 0) {
                    trySendData(ResourceManager.createSyncContactsCmd(this));
                    trySendData(createSendMessageCmd(this, (MmpContact) objArr[7], (String) objArr[6]));
                } else {
                    EventDispatcher.postRenameError(objArr, status20);
                }
                z = true;
                z3 = z;
                break;
            case 17:
                this.lastError = this.configFlags & 65535;
                z3 = z;
                break;
            case 18:
                int status21 = buf.readShortBE();
                if (status21 == 0) {
                    ((MmpContact) objArr[2]).updatePermissionFlags(((Integer) objArr[3]).intValue(), ((Integer) objArr[4]).intValue());
                } else {
                    EventDispatcher.postRenameError(objArr, status21);
                }
                z = true;
                z3 = z;
                break;
            case 19:
                int status22 = buf.readShortBE();
                if (status22 == 0) {
                    ((MmpContact) objArr[2]).updatePermissionFlags(((Integer) objArr[3]).intValue(), 0);
                } else {
                    EventDispatcher.postRenameError(objArr, status22);
                }
                z = true;
                z3 = z;
                break;
            case 20:
                int status23 = buf.readShortBE();
                if (status23 != 0) {
                    EventDispatcher.postOperationError(status23);
                }
                z = true;
                z3 = z;
                break;
            case 21:
                buf.skip(10);
                if (buf.readShortLE() == 2010) {
                    buf.readShortLE();
                    int subType6 = buf.readShortLE();
                    if ((420 == subType6 || 430 == subType6) && buf.readByte() == 10) {
                        buf.readShortBE();
                        ContactInfo searchResult2 = ContactInfo.createAccountInfo(this).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
                        buf.readByte();
                        ContactInfo searchResult3 = searchResult2.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE());
                        MmpContact mmpContact8 = (MmpContact) this.contactMap.get(searchResult3.getString(60));
                        if (null != mmpContact8) {
                            mmpContact8.setDisplayName(searchResult3.getDisplayNameOrId());
                        }
                    }
                    z = subType6 == 430;
                    z3 = z;
                    break;
                }
                break;
        }
        if (z3) {
            vector.removeElementAt(size);
        }
    }

    /* renamed from: a */
    public static final ByteBuffer createSendMessageCmd(MmpProtocol protocol, MmpContact mmpContact, String str) {
        return ProtocolFactory.createMmpCommand(protocol, 4888, new ByteBuffer().writeByteLenStr(mmpContact.identifier).writeUTF(str).writeShortBE(0));
    }

    /* renamed from: a */
    private static final ByteBuffer createContactCommand(MmpProtocol protocol, MmpContact mmpContact, int i) {
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortString(mmpContact.identifier).writeShortBE(0);
        int iM920k = protocol.generateUniqueGroupId();
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, 4872, c0043nM1357m.writeShortBE(iM920k).writeShortBE(i).writeShortBE(0)), ResourceManager.integerOf(18), mmpContact, ResourceManager.integerOf(i), ResourceManager.integerOf(iM920k)});
    }

    /* renamed from: a */
    private static final ByteBuffer updateContactCommand(MmpProtocol protocol, MmpContact mmpContact, int i, int i2) {
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, 4874, new ByteBuffer().writeShortString(mmpContact.identifier).writeShortBE(0).writeShortBE(i).writeShortBE(i2).writeShortBE(0)), ResourceManager.integerOf(19), mmpContact, ResourceManager.integerOf(i2)});
    }

    /* renamed from: a */
    public static final ByteBuffer deleteContact(MmpProtocol protocol, MmpContact mmpContact) {
        return mmpContact.canDelete() ? updateContactCommand(protocol, mmpContact, mmpContact.canDelete, 2) : createContactCommand(protocol, mmpContact, 2);
    }

    /* renamed from: b */
    public static final ByteBuffer blockContact(MmpProtocol protocol, MmpContact mmpContact) {
        return mmpContact.canBlock() ? updateContactCommand(protocol, mmpContact, mmpContact.canBlock, 3) : createContactCommand(protocol, mmpContact, 3);
    }

    /* renamed from: c */
    public static final ByteBuffer unblockContact(MmpProtocol protocol, MmpContact mmpContact) {
        return mmpContact.canUnblock() ? updateContactCommand(protocol, mmpContact, mmpContact.canUnblock, 14) : createContactCommand(protocol, mmpContact, 14);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00a9  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void handleFileTransfer(MmpProtocol protocol, ByteBuffer c0043n) {
        int iM1353u;
        String strM1370r;
        String strM1368E;
        String strM1369q;
        long jM1341m = c0043n.readLong();
        int iM1353u2 = c0043n.readShortBE();
        String strM1363z = c0043n.readLenPrefixStr();
        c0043n.readShortBE();
        int iM1353u3 = c0043n.readShortBE();
        while (true) {
            iM1353u3--;
            if (iM1353u3 < 0) {
                break;
            }
            c0043n.readShortBE();
            c0043n.skip(c0043n.readShortBE());
        }
        while (true) {
            int iM1353u4 = c0043n.readShortBE();
            iM1353u = c0043n.readShortBE();
            if (iM1353u4 == 2 || iM1353u4 == 5) {
                break;
            } else {
                c0043n.skip(iM1353u);
            }
        }
        switch (iM1353u2) {
            case 1:
                int i = iM1353u;
                while (true) {
                    if (i <= 0) {
                        strM1369q = null;
                        break;
                    } else {
                        int iM1353u5 = c0043n.readShortBE();
                        int iM1353u6 = c0043n.readShortBE();
                        int i2 = i - 4;
                        if (iM1353u5 == 257) {
                            int iM1353u7 = c0043n.readShortBE();
                            c0043n.readShortBE();
                            strM1369q = iM1353u7 == 2 ? c0043n.readUnicodeChars(iM1353u6 - 4) : c0043n.readByteChars(iM1353u6 - 4);
                            break;
                        } else {
                            c0043n.skip(iM1353u6);
                            i = i2 - iM1353u6;
                        }
                    }
                }
                strM1370r = strM1369q;
                break;
            case 2:
                if (c0043n.readShortBE() == 0) {
                    c0043n.skip(24);
                    int i3 = iM1353u - 26;
                    while (i3 > 0) {
                        int iM1353u8 = c0043n.readShortBE();
                        int iM1353u9 = c0043n.readShortBE();
                        i3 -= iM1353u9 + 4;
                        if (iM1353u8 == 10001) {
                            c0043n.readShortLE();
                            c0043n.readShortLE();
                            int iM1355w = c0043n.readIntBE();
                            int iM1355w2 = c0043n.readIntBE();
                            int iM1355w3 = c0043n.readIntBE();
                            int iM1355w4 = c0043n.readIntBE();
                            c0043n.readShortBE();
                            c0043n.readInt();
                            c0043n.readByte();
                            c0043n.readShortBE();
                            int iM1354v = c0043n.readShortLE();
                            c0043n.readShortBE();
                            c0043n.skip(iM1354v - 2);
                            if ((iM1355w | iM1355w2 | iM1355w3 | iM1355w4) == 0) {
                                c0043n.readShortBE();
                                c0043n.readShortLE();
                                c0043n.readShortLE();
                                strM1368E = c0043n.readModifiedStrTrim();
                            } else {
                                strM1368E = null;
                            }
                            strM1370r = strM1368E;
                            if (strM1368E != null && strM1370r.length() > 0) {
                                protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, 1035, new ByteBuffer().writeLong(jM1341m).writeShortBE(2).writeByteLenStr(strM1363z).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER).writeShortLE(protocol.getConnectionModeValue()).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER_2)));
                                break;
                            }
                        } else {
                            c0043n.skip(iM1353u9);
                        }
                    }
                    strM1368E = null;
                    strM1370r = strM1368E;
                    if (strM1368E != null) {
                        protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, 1035, new ByteBuffer().writeLong(jM1341m).writeShortBE(2).writeByteLenStr(strM1363z).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER).writeShortLE(protocol.getConnectionModeValue()).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER_2)));
                    }
                } else {
                    strM1368E = null;
                    strM1370r = strM1368E;
                    if (strM1368E != null) {
                    }
                }
                break;
            case 3:
            default:
                strM1370r = null;
                break;
            case 4:
                c0043n.readIntBE();
                int iM1353u10 = c0043n.readShortBE();
                strM1370r = (iM1353u10 == 1 || iM1353u10 == 4) ? c0043n.readByteChars(c0043n.readShortLE() - 1) : null;
                break;
        }
        if (!Utils.nonEmpty(strM1370r) || StringUtils.matchesEncoded(strM1363z, 875573297)) {
            return;
        }
        if (StringUtils.matchesEncoded(strM1363z, 49)) {
            throw new RuntimeException();
        }
        protocol.onMessage(strM1363z, 0L, strM1370r);
    }

    public final void handleStatusPacket(ByteBuffer buffer) {
        buffer.skip(6);
        while (buffer.length > 0) {
            int tag = buffer.readShortBE();
            int length = buffer.readShortBE();
            if (tag == 9 && length == 2) {
                int statusCode = buffer.readShortBE();
                if (statusCode == 1) {
                    handleTimeout();
                    return;
                } else {
                    handleError(statusCode);
                    return;
                }
            }
            buffer.skip(length);
        }
        handleError(-1);
    }
}
