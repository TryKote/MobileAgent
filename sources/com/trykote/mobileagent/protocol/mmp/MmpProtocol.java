package com.trykote.mobileagent.protocol.mmp;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.ConnectionThread;
import com.trykote.mobileagent.protocol.ProtocolFactory;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.RegistrationService;
import com.trykote.mobileagent.util.Base64;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;

import java.util.Hashtable;
import java.util.Vector;

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

    // Extended status codes (used in getIconId)
    private static final int STATUS_DEPRESSION = 8193;
    private static final int STATUS_AT_HOME = 12288;
    private static final int STATUS_EATING = 16384;
    private static final int STATUS_IN_SHOWER = 20480;
    private static final int STATUS_ON_THE_PHONE = 24576;

    // Icon composition: base | overlay
    private static final int ICON_OFFLINE = 255;
    private static final int ICON_ONLINE = 256;
    private static final int ICON_CONNECTING = 265;
    private static final int ICON_CUSTOM_BASE = 268;
    private static final int ICON_OVERLAY_ONLINE = 16318464;
    private static final int ICON_OVERLAY_DND = 16449536;
    private static final int ICON_OVERLAY_AWAY = 16580608;
    private static final int ICON_OVERLAY_INVISIBLE = 16646144;
    private static final int ICON_OVERLAY_FREE_CHAT = 16384000;
    private static final int ICON_OVERLAY_AT_WORK = 16515072;
    private static final int ICON_OVERLAY_DEPRESSION = 17104896;
    private static final int ICON_OVERLAY_AT_HOME = 16842752;
    private static final int ICON_OVERLAY_EATING = 16908288;
    private static final int ICON_OVERLAY_IN_SHOWER = 16973824;
    private static final int ICON_OVERLAY_ON_THE_PHONE = 17039360;

    // Bit mask for lower 16 bits
    static final int MASK_LOW_16 = 65535;

    // Protocol version bounds
    private static final int PROTOCOL_VERSION_DEFAULT = 4;
    private static final int PROTOCOL_VERSION_MAX = 5;
    private static final int PROTOCOL_VERSION_COMPACT = 3;

    // Connection magic value written to state field
    private static final int HANDSHAKE_STATE = 28179;

    // Status flag set command bitmask
    private static final int STATUS_CMD_FLAG = 268435456;

    // Timeouts (milliseconds)
    private static final long TIMEOUT_WIFI = 25000L;
    private static final long TIMEOUT_MOBILE = 60000L;

    // Message status flags (updateStatus)
    private static final int MSG_FLAG_DELIVERED = 64;
    private static final int MSG_FLAG_READ = 128;

    // Contact status flags (used in getConnectionModeValue; parseStatus constants in MmpContact)
    static final int CONTACT_STATUS_DND = 19;
    static final int CONTACT_STATUS_INVISIBLE = 17;

    // Contact attribute tag IDs
    static final int TAG_DISPLAY_NAME = 305;
    static final int TAG_AUTHORIZATION_FLAG = 102;
    private static final int TAG_NICKNAME = 347;
    private static final int TAG_GROUP_ORDER = 200;
    static final int TAG_SEQUENCE_MARKER = 202;

    // Packet header constants
    private static final int HEADER_SIZE = 16;
    private static final int HEADER_EXTENDED_FLAG = 32768;

    // Response type IDs are in MmpResponseHandler.RESP_*

    // Search command constants
    private static final int SEARCH_MAX_RESULTS = 2000;
    private static final int SEARCH_HEADER_MARKER = 1375;
    private static final int SEARCH_BY_ID = 13825;
    private static final int SEARCH_BY_FIELDS = 12290;
    private static final int SEARCH_TAG_FIRST_NAME = 21505;
    private static final int SEARCH_TAG_LAST_NAME = 16385;
    private static final int SEARCH_TAG_NICKNAME = 18945;
    private static final int SEARCH_TAG_CITY = 24065;
    private static final int SEARCH_TAG_COUNTRY = 36865;
    private static final int SEARCH_TAG_GENDER = 9730;
    // Server response sub-types and search/history markers moved to MmpResponseHandler

    // TLV types in AUTH_REQUEST
    private static final int TLV_AUTH_NAME = 1;
    private static final int TLV_AUTH_FLAG = 2;
    private static final int TLV_AUTH_DATA = 3;

    // Message encoding TLV types are in MmpMessageParser.MSG_TLV_*

    // Group ID generation bounds
    private static final int GROUP_ID_MASK = 28671;
    private static final int GROUP_ID_OFFSET = 4096;

    // Auth flag value (AUTH_RECEIVED)
    private static final byte AUTH_FLAG_ACCEPTED = 1;

    // Notification tag/length values (handleStatusPacket)
    private static final int STATUS_TAG_DISCONNECT = 9;
    private static final int STATUS_LEN_DISCONNECT = 2;
    private static final int STATUS_CODE_TIMEOUT = 1;

    // Extension type base offset
    private static final int EXT_TYPE_BASE = 369;

    // Session key ID
    private static final int SESSION_KEY = 197381;

    // Auth response string keys (AppState pool indices)
    private static final int STR_AUTH_ACCEPTED = 484;
    private static final int STR_AUTH_REJECTED = 485;

    // Spam report protocol code
    private static final int SPAM_REPORT_CODE = 1501;

    // Message section/notification/sender constants moved to MmpMessageParser

    // Progress percentage values (loadData)
    private static final int PROGRESS_PCT_STARTING = 10;
    private static final int PROGRESS_PCT_AUTH_PENDING = 20;
    private static final int PROGRESS_PCT_CONNECTING = 70;
    private static final int PROGRESS_PCT_HANDSHAKE = 85;
    private static final int PROGRESS_PCT_PROCESSING = 90;

    // Message encoding types (validateSend)
    private static final int ENCODING_SIMPLE = 1;
    private static final int ENCODING_RICH = 2;

    // Rich message format constants (validateSend)
    private static final int RICH_MSG_OVERHEAD = 42;
    private static final int RICH_MSG_HEADER_SIZE = 143;
    private static final int RICH_MSG_CONTAINER_OVERHEAD = 103;

    // Error codes returned from validate* methods
    private static final int ERROR_ALREADY_CONNECTING = 487;
    private static final int ERROR_NOT_CONNECTED = 299;
    private static final int ERROR_CONTACT_ONLINE = 310;

    private static final byte[] RICH_MSG_CAPS = {0, 0, 0, 0, -1, -1, -1, 0};
    private static final byte[] RICH_MSG_CAPS_BLOCKED = {0, 0, 0, 0, 0, -1, -1, -1, 38, 0, 0, 0,
        123, 48, 57, 52, 54, 49, 51, 52, 69, 45, 52, 67, 55, 70, 45, 49, 49, 68, 49, 45, 56, 50,
        50, 50, 45, 52, 52, 52, 53, 53, 51, 53, 52, 48, 48, 48, 48, 125};

    final MmpResponseHandler responseHandler = new MmpResponseHandler(this);

    public int serverId;

    public int messageSequence;

    private byte[] encryptionKey;

    public String[] connectionData;

    public int contactListIndex;

    public int groupSequenceId;

    public final Hashtable contactsByIdMap;

    public final Hashtable contactGroupsMap;

    public final Hashtable additionalDataMap;

    private int protocolVersion;

    private int pendingVersionUpdate;

    private int networkResourceMode;

    public MmpProtocol(int accountId, String login, String password) {
        super(accountId, login, password);
        this.lastError = STATUS_OFFLINE;
        this.configFlags = STATUS_ONLINE;
        this.protocolVersion = PROTOCOL_VERSION_DEFAULT;
        MmpContactGroup group = new MmpContactGroup(this, 0, StringPool.get(StringResKeys.STR_GROUP_DEFAULT));
        group.isSpecial = true;
        this.defaultGroup = group;
        this.contactsByIdMap = new Hashtable();
        this.contactGroupsMap = new Hashtable();
        this.additionalDataMap = new Hashtable();
    }

    @Override // p000.Account
    public int getType() {
        return TYPE_MMP;
    }

    public MmpProtocol(ByteBuffer buffer) {
        super(buffer);
        this.protocolVersion = this.configFlags >>> 16;
        if (this.protocolVersion <= 0 || this.protocolVersion > PROTOCOL_VERSION_MAX) {
            this.protocolVersion = PROTOCOL_VERSION_DEFAULT;
        }
        this.configFlags &= MASK_LOW_16;
        int groupCount = buffer.readInt();
        for (int i = groupCount - 1; i >= 0; i--) {
            addGroup((ContactGroup) new MmpContactGroup(this, buffer));
        }
        MmpContactGroup group = new MmpContactGroup(this, buffer);
        group.isSpecial = true;
        this.defaultGroup = group;
        this.contactsByIdMap = new Hashtable();
        this.contactGroupsMap = new Hashtable();
        this.additionalDataMap = new Hashtable();
    }

    @Override // p000.Account
    public Account serializeAccount(ByteBuffer buffer, boolean includeGroups, boolean includeContacts) {
        this.configFlags = (this.configFlags & MASK_LOW_16) + (this.protocolVersion << 16);
        return super.serializeAccount(buffer, includeGroups, includeContacts);
    }

    @Override // p000.Account
    public ContactGroup createOnlineGroup() {
        return new MmpContactGroup(this, -1, StringPool.get(StringResKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override // p000.Account
    public ContactGroup createBlockedGroup() {
        return new MmpContactGroup(this, -2, StringPool.get(StringResKeys.STR_GROUP_TEMPORARY));
    }

    @Override // p000.Account
    public ContactGroup createOfflineGroup() {
        return new MmpContactGroup(this, -3, StringPool.get(StringResKeys.STR_GROUP_IGNORE));
    }

    @Override // p000.Account
    public ContactGroup createSpecialGroup() {
        return new MmpContactGroup(this, -4, StringPool.get(StringResKeys.STR_GROUP_PHONE_CONTACTS));
    }

    @Override
    public int getIconResourceId() {
        if (this.reserved2 == 0) {
            return ICON_ONLINE;
        }
        return ICON_CUSTOM_BASE + this.reserved2;
    }

    @Override // p000.Account
    public int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        this.extras.removeAllElements();
        return STATUS_OFFLINE;
    }

    @Override // p000.Account
    public int getIconId() {
        if (this.progress >= PROGRESS_STARTING && this.progress < PROGRESS_CONNECTED) {
            return ICON_CONNECTING;
        }
        int iconRes = getIconResourceId();
        switch (this.lastError) {
            case STATUS_OFFLINE:
                return ICON_OFFLINE;
            case 1:
                return ICON_OVERLAY_ONLINE | iconRes;
            case STATUS_DND:
                return ICON_OVERLAY_DND | iconRes;
            case STATUS_AWAY:
                return ICON_OVERLAY_AWAY | iconRes;
            case STATUS_INVISIBLE:
                return ICON_OVERLAY_INVISIBLE | iconRes;
            case STATUS_FREE_CHAT:
                return ICON_OVERLAY_FREE_CHAT | iconRes;
            case STATUS_AT_WORK:
                return ICON_OVERLAY_AT_WORK | iconRes;
            case STATUS_DEPRESSION:
                return ICON_OVERLAY_DEPRESSION | iconRes;
            case STATUS_AT_HOME:
                return ICON_OVERLAY_AT_HOME | iconRes;
            case STATUS_EATING:
                return ICON_OVERLAY_EATING | iconRes;
            case STATUS_IN_SHOWER:
                return ICON_OVERLAY_IN_SHOWER | iconRes;
            case STATUS_ON_THE_PHONE:
                return ICON_OVERLAY_ON_THE_PHONE | iconRes;
            default:
                return iconRes;
        }
    }

    public ByteBuffer queueCommand(Object command) {
        if (!isConnecting()) {
            return null;
        }
        Object[] cmdEntry = (Object[]) command;
        ByteBuffer buffer = (ByteBuffer) cmdEntry[0];
        cmdEntry[0] = ObjectPool.integerOf(buffer.peekIntBEAt(12));
        this.extras.addElement(command);
        return buffer;
    }

    @Override // p000.Account
    public void loadData() throws Throwable {
        if (this.progress <= PROGRESS_DISCONNECTED) {
            closeConnection();
            this.lastError = STATUS_OFFLINE;
        }
        handleConnectionProgress();
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
            ByteBuffer packet = this.dataBuffer.extractJPEG();
            if (packet == null) {
                if (this.lastError != STATUS_OFFLINE && this.connection != null && this.connection.getState() == ConnectionThread.STATE_CLOSED) {
                    closeConnection();
                    this.lastError = getDefaultError();
                }
                if (this.timeout > 0 && isConnected() && TimerManager.isTimerExpired(this.deadline)) {
                    trySendData(ProtocolFactory.createPingPacket(this, MmpCommand.PACKET_KEEPALIVE));
                }
                return;
            }
            AccountManager.recordInboundPacket((Account) this, packet);
            this.msgCount = PROGRESS_PCT_PROCESSING;
            if (packet.peekByteAt(1) == MmpCommand.PACKET_COMMAND) {
                int commandId = (packet.peekByteAt(6) << 24) | (packet.peekByteAt(8) << 16) | (packet.peekByteAt(7) << 8) | packet.peekByteAt(9);
                int seqNum = packet.peekIntBEAt(12);
                int flags = packet.peekShortBE(10);
                int headerFlags = packet.peekShortBE(10);
                packet.skip(HEADER_SIZE);
                if (headerFlags == HEADER_EXTENDED_FLAG) {
                    packet.skip(packet.readShortBE());
                }
                packet = packet.compact();
                dispatchCommand(packet, commandId, seqNum, flags);
                notifyContactListUpdated();
            } else if (packet.peekByteAt(1) == MmpCommand.PACKET_NOTIFICATION) {
                handleStatusPacket(packet);
                notifyContactListUpdated();
            }
            packet.clear();
        }
    }

    private void handleConnectionProgress() throws Throwable {
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
                notifyConnectionProgressChanged();
                this.msgCount = PROGRESS_PCT_STARTING;
                this.networkResourceMode = RegistrationService.checkForUpdates();
                if (this.networkResourceMode != -1 || this.networkResourceMode == 1) {
                    this.progress = PROGRESS_CHECK_RESOURCES;
                }
                break;
            case PROGRESS_CHECK_RESOURCES:
                if (this.networkResourceMode != 0) {
                    Vector accounts = AccountManager.getMrimAccountList();
                    for (int idx = Utils.vectorSize(accounts) - 1; idx >= 0; idx--) {
                        Account account = (Account) accounts.elementAt(idx);
                        if (account.isConnected()) {
                            this.progress = PROGRESS_WAIT_ACCOUNTS;
                        } else if (!account.isConnecting()) {
                            accounts.removeElementAt(idx);
                        }
                    }
                    if (Utils.vectorSize(accounts) == 0) {
                        EventDispatcher.postNotification(StringPool.get(StringResKeys.STR_MMP_AUTH_ERROR));
                        this.progress = PROGRESS_DISCONNECTED;
                    }
                    ObjectPool.releaseVector(accounts);
                } else {
                    this.progress = PROGRESS_WAIT_ACCOUNTS;
                }
                break;
            case PROGRESS_WAIT_ACCOUNTS:
                new AsyncTask(AsyncTaskId.FETCH_HISTORY, new Object[]{this, ObjectPool.integerOf(0), this.login, getFormattedName()});
                this.msgCount = PROGRESS_PCT_AUTH_PENDING;
                this.progress = PROGRESS_WAIT_AUTH;
                notifyConnectionProgressChanged();
                break;
            case PROGRESS_WAIT_AUTH:
                if (this.connectionData != null) {
                    this.msgCount = PROGRESS_PCT_CONNECTING;
                    notifyConnectionProgressChanged();
                    this.state = HANDSHAKE_STATE;
                    this.encryptionKey = Base64.decode(this.connectionData[2]).toByteArray();
                    this.serverId = Integer.parseInt(this.connectionData[0]);
                    this.connection = new ConnectionThread(this.connectionData[1]);
                    this.progress = PROGRESS_CONNECTING;
                    this.connectionData = null;
                }
                break;
            case PROGRESS_CONNECTING:
                if (this.connection.getState() == ConnectionThread.STATE_CONNECTED) {
                    this.progress = PROGRESS_HANDSHAKING;
                } else if (this.connection.getState() <= ConnectionThread.STATE_CLOSED) {
                    closeConnection();
                    this.lastError = getDefaultError();
                }
                break;
            case PROGRESS_HANDSHAKING:
                this.connection.drainInput(this.dataBuffer);
                ByteBuffer handshakePacket = this.dataBuffer.extractJPEG();
                if (handshakePacket != null) {
                    notifyConnectionProgressChanged();
                    this.msgCount = PROGRESS_PCT_HANDSHAKE;
                    AccountManager.recordInboundPacket((Account) this, handshakePacket);
                    if (handshakePacket.peekByteAt(1) == MmpCommand.PACKET_HANDSHAKE) {
                        long timeoutMs = UIState.isWifiConnection() ? TIMEOUT_WIFI : TIMEOUT_MOBILE;
                        this.timeout = timeoutMs;
                        this.deadline = System.currentTimeMillis() + timeoutMs;
                        incrementSync();
                        byte[] key = this.encryptionKey;
                        sendData(ProtocolFactory.updateMmpPacketLength(ProtocolFactory.createPingPacket(this, MmpCommand.PACKET_HANDSHAKE).writeIntBE(1).writeShortBE(6).writeShortBE(key.length).writeBytes(key)));
                        this.encryptionKey = null;
                        sendData(ProtocolFactory.createAuthData(this));
                        sendData(ProtocolFactory.createMmpCommand(this, MmpCommand.SET_PREFS, new ByteBuffer().writeCharBytes(StringPool.get(PackedStringKeys.MMP_LOGIN_HEADER))));
                        sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SET_STATUS, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(STATUS_CMD_FLAG | getConnectionModeValue()).writeCharBytes(StringPool.get(PackedStringKeys.MMP_AUTH_PACKET))), ObjectPool.integerOf(MmpResponseHandler.RESP_AUTH_STATUS)}));
                        this.contactListIndex = 0;
                        sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.GET_CONTACT_LIST, (ByteBuffer) null), ObjectPool.integerOf(MmpResponseHandler.RESP_CONTACT_LIST)}));
                        sendData(StringUtils.createContactInfoCmd(this, this.serverId));
                        this.progress = PROGRESS_PROCESSING;
                    }
                }
                break;
        }
    }

    private void dispatchCommand(ByteBuffer packet, int commandId, int seqNum, int flags) throws Throwable {
        switch (commandId) {
            case MmpCommand.AUTH_RESULT:
                Vector authQueue = this.extras;
                for (int queueIndex = authQueue.size() - 1; queueIndex >= 0; queueIndex--) {
                    Object[] queuedCmd = (Object[]) authQueue.elementAt(queueIndex);
                    if (((Integer) queuedCmd[1]).intValue() == MmpResponseHandler.RESP_AUTH_STATUS) {
                        queuedCmd[0] = ObjectPool.integerOf(seqNum);
                    }
                }
                this.responseHandler.dispatch(packet, seqNum, 0);
                // fall through — preserved from original bytecode
            case MmpCommand.AUTH_REQUEST:
                handleAuthRequest(packet);
                break;
            case MmpCommand.CONTACT_ONLINE:
            case MmpCommand.CONTACT_OFFLINE:
                parseContactStatus(packet.readLenPrefixStr(), packet);
                break;
            case MmpCommand.ACK:
                removeQueuedCommand(seqNum);
                break;
            case MmpCommand.FILE_TRANSFER:
                MmpMessageParser.handleFileTransfer(this, packet);
                break;
            case MmpCommand.MSG_DELIVERED:
                long timestamp = packet.readLong();
                packet.readShortBE();
                updateStatus(packet.readLenPrefixStr(), timestamp, MSG_FLAG_DELIVERED);
                break;
            case MmpCommand.MSG_READ:
                long offlineTimestamp = packet.readLong();
                packet.readShortBE();
                updateStatus(packet.readLenPrefixStr(), offlineTimestamp, MSG_FLAG_READ);
                break;
            case MmpCommand.CONTACT_STATUS_CHANGE:
                packet.skip(10);
                String contactId = packet.readLenPrefixStr();
                if (packet.readShortBE() != 0) {
                    deleteContact(contactId);
                } else {
                    markRead(contactId);
                }
                break;
            case MmpCommand.CONTACT_LIST_RESPONSE:
                if (this.contactListIndex == 0) {
                    removeAllContacts();
                }
                this.responseHandler.dispatch(packet, seqNum, flags);
                break;
            case MmpCommand.CONTACT_INFO_RESPONSE:
                this.responseHandler.dispatch(packet, seqNum, 0);
                break;
            case MmpCommand.TYPING_NOTIFY:
                Contact typingContact = getContact((Object) packet.readLenPrefixStr());
                if (typingContact != null) {
                    typingContact.performAction();
                }
                break;
            case MmpCommand.MESSAGE_RECEIVED:
                onMessage(packet.readLenPrefixStr(), 0L, packet.readVarLenStr());
                break;
            case MmpCommand.AUTH_RECEIVED:
                String senderId = packet.readLenPrefixStr();
                byte authFlag = packet.readByte();
                onMessage(senderId, 0L, ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_MMP_FILE_TRANSFER)).append(StringPool.get(authFlag == AUTH_FLAG_ACCEPTED ? STR_AUTH_ACCEPTED : STR_AUTH_REJECTED)).append(packet.readVarLenStr())));
                if (authFlag == AUTH_FLAG_ACCEPTED) {
                    Contact authContact = getContact((Object) senderId);
                    if (authContact != null) {
                        authContact.performAction();
                    }
                }
                break;
            case MmpCommand.SYSTEM_MESSAGE:
                onMessage(packet.readLenPrefixStr(), 0L, StringPool.get(StringResKeys.STR_MMP_SYSTEM_MESSAGE));
                break;
            case MmpCommand.SPAM_REPORT_ACK:
                EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_MMP_SPAM_REPORT)).append(SPAM_REPORT_CODE).append('/').append(packet.readShortBE()).append(StringPool.get(StringResKeys.STR_MMP_SPAM_SUFFIX))));
                removeQueuedCommand(seqNum);
                break;
            case MmpCommand.SEARCH_RESPONSE:
                this.responseHandler.dispatch(packet, seqNum, flags);
                break;
        }
    }

    private void handleAuthRequest(ByteBuffer packet) {
        try {
            int authParam1 = packet.readIntBE();
            int authParam2 = packet.readIntBE();
            String authName = AppState.emptyStr;
            boolean authGranted = false;
            byte[] authData = AppState.emptyBytes;
            while (packet.length > 0) {
                int tlvType = packet.readShortBE();
                int tlvLen = packet.readShortBE();
                byte[] tlvValue = tlvLen > 0 ? new byte[tlvLen] : null;
                if (tlvValue != null) {
                    packet.readIntoBytes(tlvValue);
                }
                if (tlvType == TLV_AUTH_NAME) {
                    authName = StringUtils.intern(new String(tlvValue));
                } else if (tlvType == TLV_AUTH_FLAG) {
                    authGranted = true;
                } else if (tlvType == TLV_AUTH_DATA) {
                    authData = tlvValue;
                }
                if (tlvType != TLV_AUTH_DATA) {
                    ObjectPool.releaseBytes(tlvValue);
                }
            }
            MrimAccount mrimAccount = (MrimAccount) AccountManager.getOnlineMrimAccounts().elementAt(0);
            mrimAccount.trySendData(mrimAccount.createAuthPacket(this, authParam1, authParam2, authName, authGranted, authData));
        } catch (Throwable ignored) {
        }
    }
    private void parseContactStatus(String contactId, ByteBuffer buffer) {
        MmpContact contact = (MmpContact) getContact((Object) contactId);
        if (contact == null || contact.isOnline()) {
            return;
        }
        boolean wasHighlighted = contact.highlighted;
        contact.defaultIcon = ICON_OFFLINE;
        contact.highlighted = false;
        contact.isBlocked = false;
        contact.isUnblocked = false;
        contact.dirty = true;
        MmpContact.parseStatus(contact, buffer);
        if (!wasHighlighted && contact.highlighted) {
            notifyContactOnline(contact);
        }
    }

    @Override // p000.Account
    public int validateSend(Contact contactParam, String messageText, long timestamp) {
        ByteBuffer command;
        int result = super.validateSend(contactParam, messageText, timestamp);
        if (result != 0) {
            return result;
        }
        this.sentCount++;
        MmpContact contact = (MmpContact) contactParam;
        int blockedFlag = contact.isBlocked ? 1 : 0;
        int encodingType = contact.isUnblocked ? ENCODING_RICH : ENCODING_SIMPLE;
        ByteBuffer headerBuffer = new ByteBuffer().writeLong(timestamp).writeShortBE(encodingType).writeByteLenStr(contact.identifier);
        ByteBuffer bodyBuffer = new ByteBuffer();
        if (encodingType == ENCODING_SIMPLE) {
            if (blockedFlag == 1) {
                bodyBuffer.writeShortBE(2).writeShortBE(0).writeAsShorts(messageText);
            } else {
                bodyBuffer.writeIntLE(0).writeCharBytes(messageText);
            }
            headerBuffer.writeShortBE(2).writeShortBE(blockedFlag + 9 + bodyBuffer.length).writeShortBE(MmpMessageParser.MSG_TLV_HEADER).writeShortBE(blockedFlag + 1);
            if (blockedFlag == 1) {
                headerBuffer.writeShortBE(MmpMessageParser.MSG_TLV_FLAGS);
            } else {
                headerBuffer.writeByte(1);
            }
            command = ProtocolFactory.createMmpCommand(this, MmpCommand.SEND_MESSAGE, headerBuffer.writeShortBE(MmpMessageParser.MSG_TLV_BODY).writeBufferShortLen(bodyBuffer).writeShortBE(6).writeShortBE(0));
        } else {
            if (blockedFlag == 1) {
                bodyBuffer.writeUTFNoLen(messageText);
            } else {
                bodyBuffer.writeCharBytes(messageText);
            }
            bodyBuffer.writeByte(0);
            int bodyLength = bodyBuffer.length;
            int contentLength = bodyLength - (blockedFlag == 1 ? 0 : RICH_MSG_OVERHEAD);
            command = ProtocolFactory.createMmpCommand(this, MmpCommand.SEND_MESSAGE, headerBuffer.writeShortBE(5).writeShortBE(contentLength + RICH_MSG_HEADER_SIZE).writeShortBE(0).writeLong(timestamp).writeBytes(AppState.getBytes(906)).writeShortBE(10).writeShortBE(2).writeShortBE(1).writeShortBE(15).writeShortBE(0).writeShortBE(MmpMessageParser.MSG_TLV_RICH_CONTAINER).writeShortBE(contentLength + RICH_MSG_CONTAINER_OVERHEAD).writeShortLE(27).writeShortLE(8).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortBE(0).writeIntLE(3).writeByte(0).writeShortBE(0).writeIntLE(14).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortLE(1).writeShortLE(getConnectionModeValue()).writeShortLE(1).writeShortLE(bodyLength).writeBuffer(bodyBuffer).writeBytes(blockedFlag == 0 ? RICH_MSG_CAPS : RICH_MSG_CAPS_BLOCKED).writeShortBE(3).writeShortBE(0));
        }
        return trySendData(command);
    }

    @Override // p000.Account
    public int validateModify(Contact contactParam, Object[] params) {
        int result = super.validateModify(contactParam, params);
        if (result != 0) {
            return result;
        }
        MmpContact contact = (MmpContact) contactParam;
        String newName = (String) params[0];
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, contact.encodeContactUpdate(3, newName, contact.onlineSemaphore)), ObjectPool.integerOf(MmpResponseHandler.RESP_RENAME_CONTACT), contact, newName}));
    }

    public int updateConnectionMode(int statusCode) {
        if (statusCode == STATUS_AT_WORK) {
            scheduleVersionUpdate(PROTOCOL_VERSION_COMPACT);
        } else if (this.protocolVersion == PROTOCOL_VERSION_COMPACT) {
            scheduleVersionUpdate(PROTOCOL_VERSION_DEFAULT);
        }
        this.configFlags = statusCode;
        if (isConnected()) {
            trySendData(sendContactListRequest(this.groupSequenceId));
            trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SET_STATUS, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(STATUS_CMD_FLAG | getConnectionModeValue())), ObjectPool.integerOf(MmpResponseHandler.RESP_AUTH_STATUS)}));
            return trySendData(ProtocolFactory.createAuthData(this));
        }
        if (isConnecting()) {
            return ERROR_ALREADY_CONNECTING;
        }
        return connect(0);
    }

    @Override // p000.Account
    public int validateDelete(Contact contactParam) {
        if (!isConnected()) {
            return ERROR_NOT_CONNECTED;
        }
        MmpContact contact = (MmpContact) contactParam;
        RegistrationState.setParam2(ContactInfo.createAccountInfo(this).setMmpContactIdStr(contact.identifier));
        return trySendData(StringUtils.createContactInfoCmd(this, Utils.parseInt((Object) contact.identifier)));
    }

    @Override // p000.Account
    public int validateMove(Contact contactParam, ContactGroup groupParam, ContactGroup targetGroup) {
        int result = super.validateMove(contactParam, groupParam, targetGroup);
        if (result != 0) {
            return result;
        }
        trySendData(createGetContactsCmd());
        MmpContact contact = (MmpContact) contactParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, contact.encodeContactUpdate(2, contact.displayName, contact.onlineSemaphore)), ObjectPool.integerOf(MmpResponseHandler.RESP_MOVE_PHASE1), contact, groupParam, targetGroup}));
    }

    @Override // p000.Account
    public int validateGroupRename(ContactGroup groupParam, String newName) {
        int result = super.validateGroupRename(groupParam, newName);
        if (result != 0) {
            return result;
        }
        MmpContactGroup group = (MmpContactGroup) groupParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, group.createUpdatePacket(newName, -1, -1)), ObjectPool.integerOf(MmpResponseHandler.RESP_RENAME_GROUP), group, newName}));
    }

    @Override // p000.Account
    public int validateGroupCreate(String groupName) {
        int result = super.validateGroupCreate(groupName);
        if (result != 0) {
            return result;
        }
        trySendData(createGetContactsCmd());
        return trySendData(sendAddGroupCommand(groupName));
    }

    @Override // p000.Account
    public int validateGroupDelete(ContactGroup groupParam) {
        int result = super.validateGroupDelete(groupParam);
        if (result != 0) {
            return result;
        }
        trySendData(createGetContactsCmd());
        MmpContactGroup group = (MmpContactGroup) groupParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, group.createUpdatePacket(group.name, -1, -1)), ObjectPool.integerOf(MmpResponseHandler.RESP_DELETE_GROUP), group}));
    }

    @Override // p000.Account
    public int validateResend(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        int result = super.validateResend((Contact) contact);
        if (result != 0) {
            return result;
        }
        if (contact.isOnline()) {
            removeContact((Contact) contact, true);
            return 0;
        }
        if (contact.canUnblock()) {
            trySendData(MmpContact.unblockPermission(this, contact));
        }
        if (contact.canDelete()) {
            trySendData(MmpContact.deletePermission(this, contact));
        }
        if (contact.canBlock()) {
            trySendData(MmpContact.blockPermission(this, contact));
        }
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, contact.encodeContactUpdate(2, contact.displayName, contact.onlineSemaphore)), ObjectPool.integerOf(MmpResponseHandler.RESP_DELETE_CONTACT), contact}));
    }

    @Override // p000.Account
    public int validateGroupAdd(String contactId, String displayName, String messageText, ContactGroup groupParam, boolean requiresAuth) {
        int result = super.validateGroupAdd(contactId, displayName, messageText, groupParam, requiresAuth);
        if (result != 0) {
            return result;
        }
        trySendData(ProtocolFactory.createMmpCommand(this, MmpCommand.AUTH_GRANT, new ByteBuffer().writeByteLenStr(contactId).writeIntLE(0)));
        MmpContact contact = (MmpContact) getContact((Object) contactId);
        if (contact != null && !contact.isOnline()) {
            return trySendData(MmpMessageParser.createSendMessageCmd(this, contact, messageText));
        }
        trySendData(createGetContactsCmd());
        MmpContactGroup group = (MmpContactGroup) groupParam;
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortString(contactId).writeShortBE(group.groupId);
        int uniqueId = generateUniqueGroupId();
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.ADD_CONTACT, wrapperBuffer.writeShortBE(uniqueId).writeShortBE(0).writeBufferShortLen(new ByteBuffer().writeShortBE(TAG_AUTHORIZATION_FLAG).writeShortBE(0).writeShortBE(TAG_NICKNAME).writeShortBE(1).writeByte(32).writeShortBE(TAG_DISPLAY_NAME).writeUTF(displayName))), ObjectPool.integerOf(MmpResponseHandler.RESP_ADD_CONTACT_PHASE1), contactId, displayName, group, ObjectPool.integerOf(uniqueId), messageText}));
    }

    public int getConnectionModeValue() {
        switch (this.configFlags) {
            case STATUS_DND:
                return CONTACT_STATUS_DND;
            case STATUS_AWAY:
                return 5;
            case STATUS_INVISIBLE:
                return CONTACT_STATUS_INVISIBLE;
            default:
                return this.configFlags & MASK_LOW_16;
        }
    }

    public int generateUniqueGroupId() {
        boolean collision;
        int candidateId;
        do {
            collision = false;
            candidateId = (Utils.nextRandom() & GROUP_ID_MASK) + GROUP_ID_OFFSET;
            if (candidateId == this.groupSequenceId) {
                collision = true;
            } else {
                Vector groupList = this.groups;
                for (int gi = groupList.size() - 1; gi >= 0; gi--) {
                    MmpContactGroup group = (MmpContactGroup) groupList.elementAt(gi);
                    if (group.groupId == candidateId) {
                        collision = true;
                    }
                    Vector contactList = group.contacts;
                    for (int ci = contactList.size() - 1; ci >= 0; ci--) {
                        MmpContact contact = (MmpContact) contactList.elementAt(ci);
                        if (contact.userId == candidateId || contact.canDelete == candidateId || contact.canBlock == candidateId || contact.canUnblock == candidateId) {
                            collision = true;
                        }
                    }
                }
            }
        } while (collision);
        return candidateId;
    }

    @Override // p000.Account
    public int validateContactDelete(Contact contactParam) {
        if (contactParam.isOnline()) {
            return ERROR_CONTACT_ONLINE;
        }
        return trySendData(MmpContact.unblockPermission(this, (MmpContact) contactParam));
    }

    @Override // p000.Account
    public int validateContactBlock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (contact.canBlock() && !contact.canDelete()) {
            trySendData(MmpContact.blockPermission(this, contact));
        }
        return trySendData(MmpContact.deletePermission(this, contact));
    }

    @Override // p000.Account
    public int validateContactUnblock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (!contact.canBlock() && contact.canDelete()) {
            trySendData(MmpContact.deletePermission(this, contact));
        }
        return trySendData(MmpContact.blockPermission(this, contact));
    }

    @Override // p000.Account
    public int disconnect() {
        if (this.connectionData != null) {
            this.connectionData[0] = null;
        }
        this.connectionData = null;
        int result = super.disconnect();
        if (result != 0) {
            return result;
        }
        trySendData(ProtocolFactory.createPingPacket(this, MmpCommand.PACKET_DISCONNECT));
        closeConnection();
        this.lastError = getDefaultError();
        return 0;
    }

    @Override // p000.Account
    public int validateObject(Object searchFields) {
        String[] searchParams = (String[]) searchFields;
        ByteBuffer searchBuffer = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(SEARCH_MAX_RESULTS).writeShortBE(0).writeShortLE(SEARCH_HEADER_MARKER);
        String searchId = searchParams[0];
        if (searchId.length() > 0) {
            searchBuffer.writeShortBE(SEARCH_BY_ID).writeShortLE(4).writeIntLE(Utils.parseInt((Object) searchId));
        } else {
            searchBuffer.writeShortBE(SEARCH_BY_FIELDS).writeShortLE(1).writeByte(searchParams[1].length());
            MmpMessageParser.writeTaggedStr(searchBuffer, SEARCH_TAG_FIRST_NAME, searchParams[2]);
            MmpMessageParser.writeTaggedStr(searchBuffer, SEARCH_TAG_LAST_NAME, searchParams[3]);
            MmpMessageParser.writeTaggedStr(searchBuffer, SEARCH_TAG_NICKNAME, searchParams[4]);
            MmpMessageParser.writeTaggedStr(searchBuffer, SEARCH_TAG_CITY, searchParams[5]);
            MmpMessageParser.writeTaggedStr(searchBuffer, SEARCH_TAG_COUNTRY, searchParams[6]);
            MmpMessageParser.writeTaggedStr(searchBuffer, SEARCH_TAG_GENDER, searchParams[7]);
        }
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortBE(1);
        int searchLength = searchBuffer.length;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, wrapperBuffer.writeShortBE(searchLength + 2).writeShortLE(searchLength).writeBuffer(searchBuffer)), ObjectPool.integerOf(MmpResponseHandler.RESP_SEARCH)}));
    }

    @Override // p000.Account
    public Contact newContact(String contactId) {
        ContactGroup groupParam = this.defaultGroup;
        MmpContact contact = new MmpContact(this, -1, -1, contactId, contactId, true);
        groupParam.addContact((Object) contact);
        ByteBuffer queryBuffer = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(SEARCH_MAX_RESULTS).writeShortBE(0).writeShortLE(SEARCH_HEADER_MARKER).writeShortBE(SEARCH_BY_ID).writeShortLE(4).writeIntLE(Utils.parseInt((Object) contactId));
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortBE(1);
        int queryLength = queryBuffer.length;
        trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, wrapperBuffer.writeShortBE(queryLength + 2).writeShortLE(queryLength).writeBuffer(queryBuffer)), ObjectPool.integerOf(MmpResponseHandler.RESP_NEW_CONTACT_SEARCH)}));
        return contact;
    }

    @Override // p000.Account
    public void onError(int errorCode) {
        switch (errorCode) {
            case 0:
                updateConnectionMode(STATUS_ONLINE);
                break;
            case 1:
                updateConnectionMode(STATUS_FREE_CHAT);
                break;
            case 2:
                updateConnectionMode(1);
                break;
            case 3:
                updateConnectionMode(STATUS_DND);
                break;
            case 4:
                updateConnectionMode(STATUS_AT_WORK);
                break;
            default:
                disconnect();
                break;
        }
    }

    @Override
    public int getPendingVersion() {
        if (this.pendingVersionUpdate > 0 && this.pendingVersionUpdate <= PROTOCOL_VERSION_MAX) {
            this.protocolVersion = this.pendingVersionUpdate;
            this.pendingVersionUpdate = 0;
        }
        return this.protocolVersion;
    }

    @Override // p000.Account
    public int getExtType() {
        return EXT_TYPE_BASE + this.protocolVersion;
    }

    @Override
    public int scheduleVersionUpdate(int version) {
        this.pendingVersionUpdate = version;
        if (!isConnected()) {
            return 0;
        }
        trySendData(sendContactListRequest(this.groupSequenceId));
        return ScreenId.CONTACT_LIST;
    }

    @Override // p000.Account
    public void resetCounters() {
        super.resetCounters();
        this.syncSeq = 0;
    }

    @Override // p000.Account
    public int getSessionStringKey() {
        return SESSION_KEY;
    }

    public ByteBuffer sendContactListRequest(int sequenceId) {
        return queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, new ByteBuffer().writeIntLE(0).writeShortBE(sequenceId).writeShortBE(4).writeShortBE(5).writeShortBE(TAG_SEQUENCE_MARKER).writeShortBE(1).writeByte(getPendingVersion())), ObjectPool.integerOf(MmpResponseHandler.RESP_CONFIG_UPDATE)});
    }

    public void removeQueuedCommand(int seqNum) {
        Vector queue = this.extras;
        for (int queueIndex = queue.size() - 1; queueIndex >= 0; queueIndex--) {
            if (((Integer) ((Object[]) queue.elementAt(queueIndex))[0]).intValue() == seqNum) {
                queue.removeElementAt(queueIndex);
            }
        }
    }

    // handleMmpResponse and all private handler methods moved to MmpResponseHandler
    // handleFileTransfer, createSendMessageCmd, writeTaggedStr moved to MmpMessageParser
    // deletePermission, blockPermission, unblockPermission moved to MmpContact
    public void handleStatusPacket(ByteBuffer buffer) {
        buffer.skip(6);
        while (buffer.length > 0) {
            int tag = buffer.readShortBE();
            int length = buffer.readShortBE();
            if (tag == STATUS_TAG_DISCONNECT && length == STATUS_LEN_DISCONNECT) {
                int statusCode = buffer.readShortBE();
                if (statusCode == STATUS_CODE_TIMEOUT) {
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

    ByteBuffer sendAddGroupCommand(String groupName) {
        ByteBuffer groupBuffer = new ByteBuffer().writeUTF(groupName);
        int groupId = generateUniqueGroupId();
        return queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.ADD_CONTACT, groupBuffer.writeShortBE(groupId).writeShortBE(0).writeShortBE(MmpResponseHandler.ENTRY_TYPE_GROUP).writeShortBE(0)), ObjectPool.integerOf(MmpResponseHandler.RESP_ADD_GROUP), groupName, ObjectPool.integerOf(groupId)});
    }

    ByteBuffer createGetContactsCmd() {
        return ProtocolFactory.createMmpCommand(this, MmpCommand.GET_CONTACTS_SYNC, (ByteBuffer) null);
    }

    ByteBuffer createSyncContactsCmd() {
        return ProtocolFactory.createMmpCommand(this, MmpCommand.SYNC_CONTACTS, (ByteBuffer) null);
    }

    @Override
    public int handleStatusOption(int optionIndex) {
        return updateConnectionMode(optionIndex);
    }

    @Override
    public void setEmoticonSelection(int optionId) {
        this.reserved2 = optionId;
    }

    ByteBuffer createSyncGroupsCmd() {
        Object[] cmdArgs = new Object[2];
        ByteBuffer syncBuf = new ByteBuffer().writeIntLE(0).writeShortBE(0).writeShortBE(1);
        int size = this.groups.size();
        ByteBuffer syncBuf2 = syncBuf.writeShortBE((size << 1) + 4).writeShortBE(TAG_GROUP_ORDER).writeShortBE(size << 1);
        for (int i = 0; i < size; i++) {
            syncBuf2.writeShortBE(((MmpContactGroup) getGroup(i)).groupId);
        }
        cmdArgs[0] = ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, syncBuf2);
        cmdArgs[1] = ObjectPool.integerOf(MmpResponseHandler.RESP_SYNC_GROUP_ORDER);
        return queueCommand(cmdArgs);
    }
}
