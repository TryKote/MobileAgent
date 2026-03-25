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
                        IOUtils.postNotification(AppState.getString(StateKeys.STR_MMP_AUTH_ERROR));
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
                    AccountManager.processAccountData((Account) this, handshakePacket);
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
                if (this.timeout > 0 && isConnected() && AppController.isTimerExpired(this.deadline)) {
                    trySendData(ProtocolFactory.createPingPacket(this, MmpCommand.PACKET_KEEPALIVE));
                    return;
                }
                return;
            }
            AccountManager.processAccountData((Account) this, packet);
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
                                XmppMailRuProtocol.handleMmpResponse(this, packet, seqNum, 0);
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
                        XmppMailRuProtocol.removeQueuedCommand(this, seqNum);
                        break;
                    case MmpCommand.FILE_TRANSFER:
                        IOUtils.handleFileTransfer(this, packet);
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
                        XmppMailRuProtocol.handleMmpResponse(this, packet, seqNum, flags);
                        break;
                    case MmpCommand.CONTACT_INFO_RESPONSE:
                        XmppMailRuProtocol.handleMmpResponse(this, packet, seqNum, 0);
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
                        IOUtils.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_MMP_SPAM_REPORT)).append(1501).append('/').append(packet.readShortBE()).append(AppState.getString(StateKeys.STR_MMP_SPAM_SUFFIX))));
                        XmppMailRuProtocol.removeQueuedCommand(this, seqNum);
                        break;
                    case MmpCommand.SEARCH_RESPONSE:
                        XmppMailRuProtocol.handleMmpResponse(this, packet, seqNum, flags);
                        break;
                }
                AppController.needsLayoutUpdate = true;
            } else {
                if (packet.peekByteAt(1) == MmpCommand.PACKET_NOTIFICATION) {
                    AppController.handleMmpPacket(this, packet);
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
            trySendData(XmppMailRuProtocol.sendContactListRequest(this, this.groupSequenceId));
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
            trySendData(IOUtils.unblockContact(this, contact));
        }
        if (contact.canDelete()) {
            trySendData(IOUtils.deleteContact(this, contact));
        }
        if (contact.canBlock()) {
            trySendData(IOUtils.blockContact(this, contact));
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
            return trySendData(IOUtils.createSendMessageCmd(this, contact, str3));
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
        return trySendData(IOUtils.unblockContact(this, (MmpContact) contactParam));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int validateContactBlock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (contact.canBlock() && !contact.canDelete()) {
            trySendData(IOUtils.blockContact(this, contact));
        }
        return trySendData(IOUtils.deleteContact(this, contact));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int validateContactUnblock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (!contact.canBlock() && contact.canDelete()) {
            trySendData(IOUtils.deleteContact(this, contact));
        }
        return trySendData(IOUtils.blockContact(this, contact));
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
        trySendData(XmppMailRuProtocol.sendContactListRequest(this, this.groupSequenceId));
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
    public final int mo110p() {
        return 197381;
    }
}
