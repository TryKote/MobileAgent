package com.trykote.mobileagent.protocol.mmp;

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
    private static final int ICON_CUSTOM_GUID_BASE = 269;
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

    // Contact icon overlays (parseContactStatus uses different base offsets)
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

    // Icon mask for clearing the high 16 bits
    private static final int ICON_HIGH_MASK = -65536;

    // Bit mask for lower 16 bits
    private static final int MASK_LOW_16 = 65535;

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

    // TLV attribute types in parseContactStatus
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

    // Contact status flags (parseContactStatus exact match values)
    private static final int CONTACT_STATUS_ONLINE = 0;
    private static final int CONTACT_STATUS_DND = 19;
    private static final int CONTACT_STATUS_INVISIBLE = 17;

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

    // Contact list entry types
    private static final int ENTRY_TYPE_CONTACT = 0;
    private static final int ENTRY_TYPE_GROUP = 1;
    private static final int ENTRY_TYPE_CONTACT_ID_MAP = 2;
    private static final int ENTRY_TYPE_GROUP_ID_MAP = 3;
    private static final int ENTRY_TYPE_SETTINGS = 4;
    private static final int ENTRY_TYPE_ADDITIONAL_DATA = 14;

    // Contact attribute tag IDs
    private static final int TAG_DISPLAY_NAME = 305;
    private static final int TAG_AUTHORIZATION_FLAG = 102;
    private static final int TAG_NICKNAME = 347;
    private static final int TAG_GROUP_ORDER = 200;
    private static final int TAG_SEQUENCE_MARKER = 202;

    // Packet header constants
    private static final int HEADER_SIZE = 16;
    private static final int HEADER_EXTENDED_FLAG = 32768;

    // Queued command response handler IDs
    private static final int RESP_RENAME_CONTACT = 0;
    private static final int RESP_RENAME_GROUP = 1;
    private static final int RESP_DELETE_GROUP = 2;
    private static final int RESP_SYNC_GROUP_ORDER = 3;
    private static final int RESP_ADD_GROUP = 4;
    private static final int RESP_DELETE_CONTACT = 5;
    private static final int RESP_CONTACT_LIST = 6;
    private static final int RESP_CONTACT_INFO = 7;
    private static final int RESP_HISTORY = 8;
    private static final int RESP_SEARCH = 9;
    private static final int RESP_MOVE_PHASE1 = 10;
    private static final int RESP_MOVE_PHASE2 = 11;
    private static final int RESP_MOVE_PHASE3 = 12;
    private static final int RESP_MOVE_PHASE4 = 13;
    private static final int RESP_ADD_CONTACT_PHASE1 = 14;
    private static final int RESP_ADD_CONTACT_PHASE2 = 15;
    private static final int RESP_ADD_CONTACT_PHASE3 = 16;
    private static final int RESP_AUTH_STATUS = 17;
    private static final int RESP_UPDATE_PERMISSIONS = 18;
    private static final int RESP_REMOVE_PERMISSIONS = 19;
    private static final int RESP_CONFIG_UPDATE = 20;
    private static final int RESP_NEW_CONTACT_SEARCH = 21;

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
    private static final int SEARCH_HISTORY_MARKER = 60;
    private static final int SEARCH_HISTORY_FULL_MARKER = 62;

    // Server response sub-types
    private static final int SERVER_RESPONSE_MARKER = 2010;
    private static final int SUBTYPE_USER_BASIC = 200;
    private static final int SUBTYPE_USER_EXTENDED = 220;
    private static final int SUBTYPE_USER_ABOUT = 230;
    private static final int SUBTYPE_SEARCH_RESULT = 420;
    private static final int SUBTYPE_SEARCH_LAST = 430;
    private static final int SUBTYPE_HISTORY_ENTRY = 65;
    private static final int SUBTYPE_HISTORY_END = 66;

    // TLV types in AUTH_REQUEST
    private static final int TLV_AUTH_NAME = 1;
    private static final int TLV_AUTH_FLAG = 2;
    private static final int TLV_AUTH_DATA = 3;

    // Message encoding TLV types (validateSend)
    private static final int MSG_TLV_BODY = 257;
    private static final int MSG_TLV_FLAGS = 262;
    private static final int MSG_TLV_HEADER = 1281;
    private static final int MSG_TLV_RICH_CONTAINER = 10001;

    // File transfer message type IDs
    private static final int FILE_MSG_TEXT = 1;
    private static final int FILE_MSG_RICH = 2;
    private static final int FILE_MSG_NOTIFICATION = 4;

    // Encoding type values
    private static final int ENCODING_UNICODE = 2;

    // Offline message filter code
    private static final int OFFLINE_MSG_FILTERED = 1004;

    // Date/time constants for timestamp calculation
    private static final int EPOCH_YEAR = 1970;
    private static final int LEAP_CYCLE_START = 1968;
    private static final int CENTURY_YEAR = 2000;
    private static final int DAYS_IN_YEAR = 365;
    private static final int FEB_DAYS_NORMAL = 28;
    private static final int FEB_DAYS_LEAP = 29;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MILLIS_PER_SECOND = 1000;

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
        MmpContactGroup group = new MmpContactGroup(this, 0, Storage.resources().getString(StringResKeys.STR_GROUP_DEFAULT));
        group.isSpecial = true;
        this.defaultGroup = group;
        this.contactsByIdMap = new Hashtable();
        this.contactGroupsMap = new Hashtable();
        this.additionalDataMap = new Hashtable();
    }

    @Override // p000.Account
    public final int getType() {
        return TYPE_MMP;
    }

    @Override // p000.Account
    public final int setCredentials(String login, String password) {
        int result = super.setCredentials(login, password);
        if (result != 0) {
            return result;
        }
        return 0;
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
    public final Account serializeAccount(ByteBuffer buffer, boolean includeGroups, boolean includeContacts) {
        this.configFlags = (this.configFlags & MASK_LOW_16) + (this.protocolVersion << 16);
        return super.serializeAccount(buffer, includeGroups, includeContacts);
    }

    @Override // p000.Account
    public final ContactGroup createOnlineGroup() {
        return new MmpContactGroup(this, -1, Storage.resources().getString(StringResKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override // p000.Account
    public final ContactGroup createBlockedGroup() {
        return new MmpContactGroup(this, -2, Storage.resources().getString(StringResKeys.STR_GROUP_TEMPORARY));
    }

    @Override // p000.Account
    public final ContactGroup createOfflineGroup() {
        return new MmpContactGroup(this, -3, Storage.resources().getString(StringResKeys.STR_GROUP_IGNORE));
    }

    @Override // p000.Account
    public final ContactGroup createSpecialGroup() {
        return new MmpContactGroup(this, -4, Storage.resources().getString(StringResKeys.STR_GROUP_PHONE_CONTACTS));
    }

    @Override // p000.Account
    public final int connect(int mode) {
        int result = super.connect(mode);
        if (result != 0) {
            return result;
        }
        return 0;
    }

    public final int getIconResourceId() {
        if (this.reserved2 == 0) {
            return ICON_ONLINE;
        }
        return ICON_CUSTOM_BASE + this.reserved2;
    }

    @Override // p000.Account
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        this.extras.removeAllElements();
        return STATUS_OFFLINE;
    }

    @Override // p000.Account
    public final int getIconId() {
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

    public final ByteBuffer queueCommand(Object command) {
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
                this.networkResourceMode = RegistrationService.checkForUpdates();
                if (this.networkResourceMode != -1 || this.networkResourceMode == 1) {
                    this.progress = PROGRESS_CHECK_RESOURCES;
                    break;
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
                        EventDispatcher.postNotification(Storage.resources().getString(StringResKeys.STR_MMP_AUTH_ERROR));
                        this.progress = PROGRESS_DISCONNECTED;
                    }
                    ObjectPool.releaseVector(accounts);
                    break;
                } else {
                    this.progress = PROGRESS_WAIT_ACCOUNTS;
                    break;
                }
            case PROGRESS_WAIT_ACCOUNTS:
                new AsyncTask(AsyncTaskId.FETCH_HISTORY, new Object[]{this, ObjectPool.integerOf(0), this.login, getFormattedName()});
                this.msgCount = 20;
                this.progress = PROGRESS_WAIT_AUTH;
                AppController.needsRepaint = true;
                break;
            case PROGRESS_WAIT_AUTH:
                if (this.connectionData != null) {
                    this.msgCount = 70;
                    AppController.needsRepaint = true;
                    this.state = HANDSHAKE_STATE;
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
                        long timeoutMs = Storage.state().getBool(UIKeys.FLAG_WIFI_CONNECTION) ? TIMEOUT_WIFI : TIMEOUT_MOBILE;
                        this.timeout = timeoutMs;
                        this.deadline = System.currentTimeMillis() + timeoutMs;
                        incrementSync();
                        byte[] key = this.encryptionKey;
                        sendData(ProtocolFactory.updateMmpPacketLength(ProtocolFactory.createPingPacket(this, MmpCommand.PACKET_HANDSHAKE).writeIntBE(1).writeShortBE(6).writeShortBE(key.length).writeBytes(key)));
                        this.encryptionKey = null;
                        sendData(ProtocolFactory.createAuthData(this));
                        sendData(ProtocolFactory.createMmpCommand(this, MmpCommand.SET_PREFS, new ByteBuffer().writeCompressed(PackedStringKeys.MMP_LOGIN_HEADER)));
                        sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SET_STATUS, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(STATUS_CMD_FLAG | getConnectionModeValue()).writeCompressed(PackedStringKeys.MMP_AUTH_PACKET)), ObjectPool.integerOf(RESP_AUTH_STATUS)}));
                        this.contactListIndex = 0;
                        sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.GET_CONTACT_LIST, (ByteBuffer) null), ObjectPool.integerOf(RESP_CONTACT_LIST)}));
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
            ByteBuffer packet = this.dataBuffer.extractJPEG();
            if (packet == null) {
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
                int seqNum = packet.peekIntBEAt(12);
                int flags = packet.peekShortBE(10);
                int headerFlags = packet.peekShortBE(10);
                packet.skip(HEADER_SIZE);
                if (headerFlags == HEADER_EXTENDED_FLAG) {
                    packet.skip(packet.readShortBE());
                }
                packet = packet.compact();
                switch (commandId) {
                    case MmpCommand.AUTH_RESULT:
                        Vector authQueue = this.extras;
                        for (int si = authQueue.size() - 1; si >= 0; si--) {
                            Object[] queuedCmd = (Object[]) authQueue.elementAt(si);
                            if (((Integer) queuedCmd[1]).intValue() == RESP_AUTH_STATUS) {
                                queuedCmd[0] = ObjectPool.integerOf(seqNum);
                            }
                        }
                        handleMmpResponse(packet, seqNum, 0);
                    case MmpCommand.AUTH_REQUEST:
                        try {
                            int authParam1 = packet.readIntBE();
                            int authParam2 = packet.readIntBE();
                            String authName = Storage.emptyStr;
                            boolean authGranted = false;
                            byte[] authData = Storage.emptyBytes;
                            while (packet.length > 0) {
                                int tlvType = packet.readShortBE();
                                int tlvLen = packet.readShortBE();
                                byte[] tlvBytes = tlvLen > 0 ? new byte[tlvLen] : null;
                                byte[] tlvValue = tlvBytes;
                                if (tlvBytes != null) {
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
                            break;
                        } catch (Throwable ignored) {
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
                        if (typingContact != null) {
                            typingContact.performAction();
                            break;
                        }
                        break;
                    case MmpCommand.MESSAGE_RECEIVED:
                        NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CONVERSATION_MESSAGE);
                        onMessage(packet.readLenPrefixStr(), 0L, packet.readVarLenStr());
                        break;
                    case MmpCommand.AUTH_RECEIVED:
                        String senderId = packet.readLenPrefixStr();
                        byte authFlag = packet.readByte();
                        onMessage(senderId, 0L, ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_MMP_FILE_TRANSFER)).append(Storage.state().getString(authFlag == AUTH_FLAG_ACCEPTED ? 484 : 485)).append(packet.readVarLenStr())));
                        if (authFlag == AUTH_FLAG_ACCEPTED && (authContact = getContact((Object) senderId)) != null) {
                            authContact.performAction();
                            break;
                        }
                        break;
                    case MmpCommand.SYSTEM_MESSAGE:
                        onMessage(packet.readLenPrefixStr(), 0L, Storage.resources().getString(StringResKeys.STR_MMP_SYSTEM_MESSAGE));
                        break;
                    case MmpCommand.SPAM_REPORT_ACK:
                        EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_MMP_SPAM_REPORT)).append(1501).append('/').append(packet.readShortBE()).append(Storage.resources().getString(StringResKeys.STR_MMP_SPAM_SUFFIX))));
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
    private void parseContactStatus(String contactId, ByteBuffer buffer) {
        int iconIndex;
        int icon = ICON_OFFLINE;
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
        try {
            buffer.skip(2);
            int attrCount = buffer.readShortBE();
            for (int attrIndex = 0; attrIndex < attrCount; attrIndex++) {
                int attrType = buffer.readShortBE();
                int attrLen = buffer.readShortBE();
                if (attrType == ATTR_STATUS) {
                    int statusFlags = buffer.readIntBE() & MASK_LOW_16;
                    if (statusFlags == CONTACT_STATUS_ONLINE) {
                        icon = ICON_ONLINE;
                        contact.defaultIcon = icon;
                        attrLen -= 4;
                        contact.highlighted = true;
                    } else {
                        if (statusFlags == CONTACT_STATUS_DND) {
                            icon = CONTACT_ICON_DND;
                        } else if (statusFlags == CONTACT_STATUS_INVISIBLE) {
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
                    byte[] blockedGuid = Storage.resources().getBytes(StringResKeys.RES_BLOCKED_GUID);
                    byte[] unblockedGuid = Storage.resources().getBytes(StringResKeys.RES_UNBLOCKED_GUID);
                    byte[] iconGuids = Storage.resources().getBytes(StringResKeys.RES_AUTH_SLOT_GUIDS);
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
        if (wasHighlighted || !contact.highlighted) {
            return;
        }
        NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CONTACT_ONLINE);
    }

    @Override // p000.Account
    public final int validateSend(Contact contactParam, String messageText, long timestamp) {
        ByteBuffer command;
        int result = super.validateSend(contactParam, messageText, timestamp);
        if (result != 0) {
            return result;
        }
        this.sentCount++;
        MmpContact contact = (MmpContact) contactParam;
        int blockedFlag = contact.isBlocked ? 1 : 0;
        int encodingType = contact.isUnblocked ? 2 : 1;
        ByteBuffer headerBuffer = new ByteBuffer().writeLong(timestamp).writeShortBE(encodingType).writeByteLenStr(contact.identifier);
        ByteBuffer bodyBuffer = new ByteBuffer();
        if (encodingType == 1) {
            if (blockedFlag == 1) {
                bodyBuffer.writeShortBE(2).writeShortBE(0).writeAsShorts(messageText);
            } else {
                bodyBuffer.writeIntLE(0).writeCharBytes(messageText);
            }
            headerBuffer.writeShortBE(2).writeShortBE(blockedFlag + 9 + bodyBuffer.length).writeShortBE(MSG_TLV_HEADER).writeShortBE(blockedFlag + 1);
            if (blockedFlag == 1) {
                headerBuffer.writeShortBE(MSG_TLV_FLAGS);
            } else {
                headerBuffer.writeByte(1);
            }
            command = ProtocolFactory.createMmpCommand(this, MmpCommand.SEND_MESSAGE, headerBuffer.writeShortBE(MSG_TLV_BODY).writeBufferShortLen(bodyBuffer).writeShortBE(6).writeShortBE(0));
        } else {
            if (blockedFlag == 1) {
                bodyBuffer.writeUTFNoLen(messageText);
            } else {
                bodyBuffer.writeCharBytes(messageText);
            }
            bodyBuffer.writeByte(0);
            int bodyLength = bodyBuffer.length;
            int contentLength = bodyLength - (blockedFlag == 1 ? 0 : 42);
            command = ProtocolFactory.createMmpCommand(this, MmpCommand.SEND_MESSAGE, headerBuffer.writeShortBE(5).writeShortBE(contentLength + 143).writeShortBE(0).writeLong(timestamp).writeCompressed(906).writeShortBE(10).writeShortBE(2).writeShortBE(1).writeShortBE(15).writeShortBE(0).writeShortBE(MSG_TLV_RICH_CONTAINER).writeShortBE(contentLength + 103).writeShortLE(27).writeShortLE(8).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortBE(0).writeIntLE(3).writeByte(0).writeShortBE(0).writeIntLE(14).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortLE(1).writeShortLE(getConnectionModeValue()).writeShortLE(1).writeShortLE(bodyLength).writeBuffer(bodyBuffer).writeCompressed(blockedFlag == 0 ? 526807 : 3279327).writeShortBE(3).writeShortBE(0));
        }
        return trySendData(command);
    }

    @Override // p000.Account
    public final int validateModify(Contact contactParam, Object[] params) {
        int result = super.validateModify(contactParam, params);
        if (result != 0) {
            return result;
        }
        MmpContact contact = (MmpContact) contactParam;
        String newName = (String) params[0];
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, contact.encodeContactUpdate(3, newName, contact.onlineSemaphore)), ObjectPool.integerOf(RESP_RENAME_CONTACT), contact, newName}));
    }

    public final int updateConnectionMode(int statusCode) {
        if (statusCode == STATUS_AT_WORK) {
            scheduleVersionUpdate(PROTOCOL_VERSION_COMPACT);
        } else if (this.protocolVersion == PROTOCOL_VERSION_COMPACT) {
            scheduleVersionUpdate(PROTOCOL_VERSION_DEFAULT);
        }
        this.configFlags = statusCode;
        if (isConnected()) {
            trySendData(sendContactListRequest(this.groupSequenceId));
            trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SET_STATUS, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(STATUS_CMD_FLAG | getConnectionModeValue())), ObjectPool.integerOf(RESP_AUTH_STATUS)}));
            return trySendData(ProtocolFactory.createAuthData(this));
        }
        if (isConnecting()) {
            return 487;
        }
        return connect(0);
    }

    @Override // p000.Account
    public final int validateDelete(Contact contactParam) {
        if (!isConnected()) {
            return 299;
        }
        MmpContact contact = (MmpContact) contactParam;
        Storage.state().setObject(RegistrationKeys.SLOT_REG_PARAM_2, ContactInfo.createAccountInfo(this).setMmpContactIdStr(contact.identifier));
        return trySendData(StringUtils.createContactInfoCmd(this, Utils.parseInt((Object) contact.identifier)));
    }

    @Override // p000.Account
    public final int validateMove(Contact contactParam, ContactGroup groupParam, ContactGroup targetGroup) {
        int result = super.validateMove(contactParam, groupParam, targetGroup);
        if (result != 0) {
            return result;
        }
        trySendData(createGetContactsCmd());
        MmpContact contact = (MmpContact) contactParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, contact.encodeContactUpdate(2, contact.displayName, contact.onlineSemaphore)), ObjectPool.integerOf(RESP_MOVE_PHASE1), contact, groupParam, targetGroup}));
    }

    @Override // p000.Account
    public final int validateGroupRename(ContactGroup groupParam, String newName) {
        int result = super.validateGroupRename(groupParam, newName);
        if (result != 0) {
            return result;
        }
        MmpContactGroup group = (MmpContactGroup) groupParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, group.createUpdatePacket(newName, -1, -1)), ObjectPool.integerOf(RESP_RENAME_GROUP), group, newName}));
    }

    @Override // p000.Account
    public final int validateGroupCreate(String groupName) {
        int result = super.validateGroupCreate(groupName);
        if (result != 0) {
            return result;
        }
        trySendData(createGetContactsCmd());
        return trySendData(sendAddGroupCommand(groupName));
    }

    @Override // p000.Account
    public final int validateGroupDelete(ContactGroup groupParam) {
        int result = super.validateGroupDelete(groupParam);
        if (result != 0) {
            return result;
        }
        trySendData(createGetContactsCmd());
        MmpContactGroup group = (MmpContactGroup) groupParam;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, group.createUpdatePacket(group.name, -1, -1)), ObjectPool.integerOf(RESP_DELETE_GROUP), group}));
    }

    @Override // p000.Account
    public final int validateResend(Contact contactParam) {
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
            trySendData(unblockContact(this, contact));
        }
        if (contact.canDelete()) {
            trySendData(deleteContact(this, contact));
        }
        if (contact.canBlock()) {
            trySendData(blockContact(this, contact));
        }
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.DELETE_CONTACT, contact.encodeContactUpdate(2, contact.displayName, contact.onlineSemaphore)), ObjectPool.integerOf(RESP_DELETE_CONTACT), contact}));
    }

    @Override // p000.Account
    public final int validateGroupAdd(String contactId, String displayName, String messageText, ContactGroup groupParam, boolean requiresAuth) {
        int result = super.validateGroupAdd(contactId, displayName, messageText, groupParam, requiresAuth);
        if (result != 0) {
            return result;
        }
        trySendData(ProtocolFactory.createMmpCommand(this, MmpCommand.AUTH_GRANT, new ByteBuffer().writeByteLenStr(contactId).writeIntLE(0)));
        MmpContact contact = (MmpContact) getContact((Object) contactId);
        if (contact != null && !contact.isOnline()) {
            return trySendData(createSendMessageCmd(this, contact, messageText));
        }
        trySendData(createGetContactsCmd());
        MmpContactGroup group = (MmpContactGroup) groupParam;
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortString(contactId).writeShortBE(group.groupId);
        int uniqueId = generateUniqueGroupId();
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.ADD_CONTACT, wrapperBuffer.writeShortBE(uniqueId).writeShortBE(0).writeBufferShortLen(new ByteBuffer().writeShortBE(TAG_AUTHORIZATION_FLAG).writeShortBE(0).writeShortBE(TAG_NICKNAME).writeShortBE(1).writeByte(32).writeShortBE(TAG_DISPLAY_NAME).writeUTF(displayName))), ObjectPool.integerOf(RESP_ADD_CONTACT_PHASE1), contactId, displayName, group, ObjectPool.integerOf(uniqueId), messageText}));
    }

    public final int getConnectionModeValue() {
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

    public final int generateUniqueGroupId() {
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
    public final int validateContactDelete(Contact contactParam) {
        if (contactParam.isOnline()) {
            return 310;
        }
        return trySendData(unblockContact(this, (MmpContact) contactParam));
    }

    @Override // p000.Account
    public final int validateContactBlock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (contact.canBlock() && !contact.canDelete()) {
            trySendData(blockContact(this, contact));
        }
        return trySendData(deleteContact(this, contact));
    }

    @Override // p000.Account
    public final int validateContactUnblock(Contact contactParam) {
        MmpContact contact = (MmpContact) contactParam;
        if (!contact.canBlock() && contact.canDelete()) {
            trySendData(deleteContact(this, contact));
        }
        return trySendData(blockContact(this, contact));
    }

    @Override // p000.Account
    public final int disconnect() {
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
    public final int validateObject(Object searchFields) {
        String[] searchParams = (String[]) searchFields;
        ByteBuffer searchBuffer = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(SEARCH_MAX_RESULTS).writeShortBE(0).writeShortLE(SEARCH_HEADER_MARKER);
        String searchId = searchParams[0];
        if (searchId.length() > 0) {
            searchBuffer.writeShortBE(SEARCH_BY_ID).writeShortLE(4).writeIntLE(Utils.parseInt((Object) searchId));
        } else {
            searchBuffer.writeShortBE(SEARCH_BY_FIELDS).writeShortLE(1).writeByte(searchParams[1].length());
            writeTaggedStr(searchBuffer, SEARCH_TAG_FIRST_NAME, searchParams[2]);
            writeTaggedStr(searchBuffer, SEARCH_TAG_LAST_NAME, searchParams[3]);
            writeTaggedStr(searchBuffer, SEARCH_TAG_NICKNAME, searchParams[4]);
            writeTaggedStr(searchBuffer, SEARCH_TAG_CITY, searchParams[5]);
            writeTaggedStr(searchBuffer, SEARCH_TAG_COUNTRY, searchParams[6]);
            writeTaggedStr(searchBuffer, SEARCH_TAG_GENDER, searchParams[7]);
        }
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortBE(1);
        int searchLength = searchBuffer.length;
        return trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, wrapperBuffer.writeShortBE(searchLength + 2).writeShortLE(searchLength).writeBuffer(searchBuffer)), ObjectPool.integerOf(RESP_SEARCH)}));
    }

    @Override // p000.Account
    public final Contact newContact(String contactId) {
        ContactGroup groupParam = this.defaultGroup;
        MmpContact contact = new MmpContact(this, -1, -1, contactId, contactId, true);
        groupParam.addContact((Object) contact);
        ByteBuffer queryBuffer = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(SEARCH_MAX_RESULTS).writeShortBE(0).writeShortLE(SEARCH_HEADER_MARKER).writeShortBE(SEARCH_BY_ID).writeShortLE(4).writeIntLE(Utils.parseInt((Object) contactId));
        ByteBuffer wrapperBuffer = new ByteBuffer().writeShortBE(1);
        int queryLength = queryBuffer.length;
        trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, wrapperBuffer.writeShortBE(queryLength + 2).writeShortLE(queryLength).writeBuffer(queryBuffer)), ObjectPool.integerOf(RESP_NEW_CONTACT_SEARCH)}));
        return contact;
    }

    @Override // p000.Account
    public final void onError(int errorCode) {
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

    public final int getPendingVersion() {
        if (this.pendingVersionUpdate > 0 && this.pendingVersionUpdate <= PROTOCOL_VERSION_MAX) {
            this.protocolVersion = this.pendingVersionUpdate;
            this.pendingVersionUpdate = 0;
        }
        return this.protocolVersion;
    }

    @Override // p000.Account
    public final int getExtType() {
        return EXT_TYPE_BASE + this.protocolVersion;
    }

    public final int scheduleVersionUpdate(int version) {
        this.pendingVersionUpdate = version;
        if (!isConnected()) {
            return 0;
        }
        trySendData(sendContactListRequest(this.groupSequenceId));
        return ScreenId.CONTACT_LIST;
    }

    @Override // p000.Account
    public final void resetCounters() {
        super.resetCounters();
        this.syncSeq = 0;
    }

    @Override // p000.Account
    public final int getSessionStringKey() {
        return SESSION_KEY;
    }

    public final ByteBuffer sendContactListRequest(int sequenceId) {
        return queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, new ByteBuffer().writeIntLE(0).writeShortBE(sequenceId).writeShortBE(4).writeShortBE(5).writeShortBE(TAG_SEQUENCE_MARKER).writeShortBE(1).writeByte(getPendingVersion())), ObjectPool.integerOf(RESP_CONFIG_UPDATE)});
    }

    public final void removeQueuedCommand(int seqNum) {
        Vector queue = this.extras;
        for (int si = queue.size() - 1; si >= 0; si--) {
            if (((Integer) ((Object[]) queue.elementAt(si))[0]).intValue() == seqNum) {
                queue.removeElementAt(si);
            }
        }
    }

    public final void handleMmpResponse(ByteBuffer buf, int seqNum, int responseFlags) {
        Object[] cmdEntry = null;
        boolean handled = false;
        boolean searchDone;
        MmpContactGroup group;
        Vector queue = this.extras;
        int queueIndex = -1;
        for (int si = queue.size() - 1; si >= 0; si--) {
            cmdEntry = (Object[]) queue.elementAt(si);
            if (((Integer) cmdEntry[0]).intValue() == seqNum) {
                queueIndex = si;
                break;
            }
        }
        if (queueIndex < 0) {
            return;
        }
        boolean shouldRemove = true;
        switch (((Integer) cmdEntry[1]).intValue()) {
            case RESP_RENAME_CONTACT:
                int renameStatus = buf.readShortBE();
                if (renameStatus == 0) {
                    ((MmpContact) cmdEntry[2]).setDisplayName((String) cmdEntry[3]);
                } else {
                    EventDispatcher.postRenameError(cmdEntry, renameStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_RENAME_GROUP:
                int renameGroupStatus = buf.readShortBE();
                if (renameGroupStatus == 0) {
                    ((MmpContactGroup) cmdEntry[2]).setNameIfChanged((String) cmdEntry[3]);
                } else {
                    EventDispatcher.postRenameError(cmdEntry, renameGroupStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_DELETE_GROUP:
                int deleteGroupStatus = buf.readShortBE();
                if (deleteGroupStatus == 0) {
                    removeGroup((ContactGroup) cmdEntry[2]);
                    trySendData(createSyncGroupsCmd());
                } else {
                    EventDispatcher.postDeleteError(cmdEntry, deleteGroupStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_SYNC_GROUP_ORDER:
                int syncStatus = buf.readShortBE();
                if (syncStatus == 0) {
                    trySendData(createSyncContactsCmd());
                } else {
                    EventDispatcher.postOperationError(syncStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_ADD_GROUP:
                int addGroupStatus = buf.readShortBE();
                if (addGroupStatus == 0) {
                    addGroup(new MmpContactGroup(this, ((Integer) cmdEntry[3]).intValue(), (String) cmdEntry[2]));
                    trySendData(createSyncGroupsCmd());
                } else {
                    EventDispatcher.postAddGroupError(cmdEntry, addGroupStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_DELETE_CONTACT:
                int deleteContactStatus = buf.readShortBE();
                if (deleteContactStatus == 0) {
                    removeContact((Contact) cmdEntry[2], true);
                    trySendData(createSyncContactsCmd());
                } else {
                    EventDispatcher.postDeleteError(cmdEntry, deleteContactStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_CONTACT_LIST:
                boolean isLastChunk = (responseFlags & 1) == 0;
                buf.skip(1);
                Vector results = ObjectPool.newVector();
                int contactCount = buf.readShortBE();
                for (int entryIndex = 0; entryIndex < contactCount; entryIndex++) {
                    String name = buf.readVarLenStr();
                    int groupId = buf.readShortBE();
                    int contactId = buf.readShortBE();
                    int entryType = buf.readShortBE();
                    int dataRemaining = buf.readShortBE();
                    switch (entryType) {
                        case ENTRY_TYPE_CONTACT:
                            String displayName = name;
                            boolean needsAuth = false;
                            while (dataRemaining > 0) {
                                int attrType = buf.readShortBE();
                                int dataLen = buf.peekShortBE(0);
                                if (attrType == TAG_DISPLAY_NAME) {
                                    displayName = buf.readVarLenStr();
                                } else {
                                    if (attrType == TAG_AUTHORIZATION_FLAG) {
                                        needsAuth = true;
                                    }
                                    buf.skip(dataLen + 2);
                                }
                                dataRemaining -= dataLen + 4;
                            }
                            results.addElement(new MmpContact(this, contactId, groupId, name, displayName, needsAuth));
                            continue;
                        case ENTRY_TYPE_GROUP:
                            if (groupId != 0) {
                                this.groups.addElement(new MmpContactGroup(this, groupId, name));
                            }
                            buf.skip(dataRemaining);
                            continue;
                        case ENTRY_TYPE_CONTACT_ID_MAP:
                            this.contactsByIdMap.put(name, ObjectPool.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                        case ENTRY_TYPE_GROUP_ID_MAP:
                            this.contactGroupsMap.put(name, ObjectPool.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                        case ENTRY_TYPE_SETTINGS:
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
                        case ENTRY_TYPE_ADDITIONAL_DATA:
                            this.additionalDataMap.put(name, ObjectPool.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                    }
                    while (dataRemaining > 0) {
                        if (buf.readShortBE() == TAG_SEQUENCE_MARKER) {
                            this.groupSequenceId = contactId;
                        }
                        int entryLen = buf.readShortBE();
                        buf.skip(entryLen);
                        dataRemaining -= entryLen + 4;
                    }
                }
                this.contactListIndex = contactCount;
                for (int ri = results.size() - 1; ri >= 0; ri--) {
                    MmpContact parsedContact = (MmpContact) results.elementAt(ri);
                    int contactGroupId = parsedContact.onlineSemaphore;
                    group = null;
                    for (int gi = this.groups.size() - 1; gi >= 0; gi--) {
                        MmpContactGroup candidateGroup = (MmpContactGroup) getGroup(gi);
                        if (candidateGroup.groupId == contactGroupId) {
                            group = candidateGroup;
                            break;
                        }
                    }
                    if (group != null) {
                        group.addContact((Object) parsedContact);
                    }
                }
                if (isLastChunk) {
                    sendData(ProtocolFactory.createMmpCommand(this, MmpCommand.CONTACT_LIST_ACK, (ByteBuffer) null));
                    int savedSequenceId = this.groupSequenceId;
                    if (savedSequenceId != 0) {
                        sendData(sendContactListRequest(savedSequenceId));
                    }
                    sendData(ProtocolFactory.createMmpCommand(this, MmpCommand.SET_CAPABILITIES, new ByteBuffer().writeCompressed(PackedStringKeys.MMP_TRANSFER_HEADER)));
                    sendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(this.serverId).writeShortLE(SEARCH_HISTORY_MARKER).writeShortBE(0)), ObjectPool.integerOf(RESP_HISTORY)}));
                    Enumeration elements = this.contactMap.elements();
                    while (elements.hasMoreElements()) {
                        Hashtable idMap = this.contactsByIdMap;
                        MmpContact contact = (MmpContact) elements.nextElement();
                        Object deleteId = idMap.get(contact.identifier);
                        if (deleteId != null) {
                            contact.canDelete = ((Integer) deleteId).intValue();
                        }
                        Object blockId = this.contactGroupsMap.get(contact.identifier);
                        if (blockId != null) {
                            contact.canBlock = ((Integer) blockId).intValue();
                        }
                        Object unblockId = this.additionalDataMap.get(contact.identifier);
                        if (unblockId != null) {
                            contact.canUnblock = ((Integer) unblockId).intValue();
                        }
                    }
                    this.contactsByIdMap.clear();
                    this.contactGroupsMap.clear();
                    this.additionalDataMap.clear();
                    if (this.groups.size() == 0) {
                        sendData(sendAddGroupCommand(Storage.resources().getString(PackedStringKeys.XMPP_GROUP_GENERAL)));
                    }
                    this.progress = PROGRESS_CONNECTED;
                    this.msgCount = PROGRESS_CONNECTED;
                }
                ObjectPool.releaseVector(results);
                handled = isLastChunk;
                shouldRemove = handled;
            case RESP_CONTACT_INFO:
                buf.skip(10);
                if (buf.readShortLE() == SERVER_RESPONSE_MARKER) {
                    buf.readShortLE();
                    int subType = buf.readShortLE();
                    buf.readByte();
                    ContactInfo info = (ContactInfo) Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_2);
                    if (info == null) {
                        info = ContactInfo.createAccountInfo(this);
                    }
                    switch (subType) {
                        case SUBTYPE_USER_BASIC:
                            String nickName = buf.readPascalStr();
                            ContactInfo basicInfo = info.setDisplayName(nickName).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr()).setCustomField1(buf.readPascalStr());
                            buf.readPascalStr();
                            basicInfo.setJobTitle(buf.readPascalStr()).setCustomField2(buf.readPascalStr()).setCustomField3(buf.readPascalStr()).setCustomField4(buf.readPascalStr());
                            if (this.serverId == ((Integer) cmdEntry[2]).intValue()) {
                                setDisplayName(nickName);
                                break;
                            }
                            break;
                        case SUBTYPE_USER_EXTENDED:
                            ContactInfo extInfo = info.setAge(buf.readShortLE()).setMaritalStatus(buf.readByte()).setCustomField6(buf.readPascalStr());
                            int birthYear = buf.readShortLE();
                            byte birthDay = buf.readByte();
                            byte birthMonth = buf.readByte();
                            if (birthMonth >= 0) {
                                extInfo.setCompany(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Utils.zeroPad(birthMonth + 1)).append('/').append(Utils.zeroPad(birthDay)).append('/').append(birthYear)));
                                break;
                            }
                            break;
                        case SUBTYPE_USER_ABOUT:
                            info.setCustomField5(buf.readPascalStr());
                            break;
                    }
                    boolean isInfoComplete = (responseFlags & 1) == 0;
                    if (isInfoComplete) {
                        Storage.state().setObject(RegistrationKeys.SLOT_REG_PARAM_1, Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_2));
                        Storage.state().setObject(RegistrationKeys.SLOT_REG_PARAM_1, Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_2));
                        Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_2);
                    }
                    handled = isInfoComplete;
                } else {
                    handled = true;
                }
                shouldRemove = handled;
                break;
            case RESP_HISTORY:
                int prevReserved = this.reserved1;
                this.reserved1 = prevReserved + 1;
                if (prevReserved != 0) {
                    Storage.state().setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, 0);
                }
                buf.skip(10);
                int historySubType = buf.readShortLE();
                buf.readShortLE();
                switch (historySubType) {
                    case SUBTYPE_HISTORY_ENTRY:
                        int senderId = buf.readInt();
                        int year = buf.readShortLE();
                        byte month = buf.readByte();
                        byte dayOfMonth = buf.readByte();
                        byte hour = buf.readByte();
                        byte minute = buf.readByte();
                        byte febDays = (year % 4 != 0 || year == CENTURY_YEAR) ? (byte) FEB_DAYS_NORMAL : (byte) FEB_DAYS_LEAP;
                        int totalDays = (((((year - EPOCH_YEAR) * DAYS_IN_YEAR) + ((year - LEAP_CYCLE_START) / 4)) + dayOfMonth) + FEB_DAYS_NORMAL) - febDays;
                        if (year >= CENTURY_YEAR) {
                            totalDays--;
                        }
                        byte[] monthDays = Storage.resources().getBytes(StringResKeys.RES_MONTH_DAYS);
                        int monthIndex = 0;
                        while (monthIndex < month - 1) {
                            totalDays += monthIndex == 1 ? febDays : monthDays[monthIndex];
                            monthIndex++;
                        }
                        long timestampMs = MILLIS_PER_SECOND * ((SECONDS_PER_DAY * totalDays) + (hour * SECONDS_PER_HOUR) + (minute * SECONDS_PER_MINUTE));
                        buf.readShortBE();
                        if (senderId != OFFLINE_MSG_FILTERED) {
                            onMessage(Integer.toString(senderId), timestampMs, buf.readModifiedStr());
                        }
                        handled = false;
                        break;
                    case SUBTYPE_HISTORY_END:
                        Storage.state().setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, 1);
                        trySendData(ProtocolFactory.createMmpCommand(this, MmpCommand.SEARCH, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(this.serverId).writeShortLE(SEARCH_HISTORY_FULL_MARKER).writeShortBE(0)));
                        break;
                    default:
                        handled = false;
                        break;
                }
                shouldRemove = handled;
                break;
            case RESP_SEARCH:
                Vector searchResults = Storage.state().getVector(RegistrationKeys.SLOT_REG_PARAM_3);
                buf.skip(10);
                if (buf.readShortLE() == SERVER_RESPONSE_MARKER) {
                    buf.readShortLE();
                    int searchSubType = buf.readShortLE();
                    if ((SUBTYPE_SEARCH_RESULT == searchSubType || SUBTYPE_SEARCH_LAST == searchSubType) && buf.readByte() == 10) {
                        buf.readShortBE();
                        ContactInfo searchResult = ContactInfo.createAccountInfo(this).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
                        buf.readByte();
                        searchResults.addElement(searchResult.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE()));
                    }
                    if (searchSubType == SUBTYPE_SEARCH_LAST) {
                        Storage.state().setObject(RegistrationKeys.SLOT_REG_PARAM_4, Storage.state().getVector(RegistrationKeys.SLOT_REG_PARAM_3));
                        Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_3);
                        searchDone = true;
                        shouldRemove = searchDone;
                        break;
                    } else {
                        searchDone = false;
                        shouldRemove = searchDone;
                    }
                } else {
                    searchDone = true;
                    shouldRemove = searchDone;
                }
                break;
            case RESP_MOVE_PHASE1:
                int moveStatus1 = buf.readShortBE();
                if (moveStatus1 == 0) {
                    MmpContact movedContact = (MmpContact) cmdEntry[2];
                    MmpContactGroup srcGroup = (MmpContactGroup) cmdEntry[3];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, srcGroup.createUpdatePacket(srcGroup.name, movedContact.userId, -1)), ObjectPool.integerOf(RESP_MOVE_PHASE2), movedContact, srcGroup, cmdEntry[4]}));
                } else {
                    EventDispatcher.postRenameError(cmdEntry, moveStatus1);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_MOVE_PHASE2:
                int moveStatus2 = buf.readShortBE();
                if (moveStatus2 == 0) {
                    MmpContact movedContact = (MmpContact) cmdEntry[2];
                    Object prevGroup = cmdEntry[3];
                    MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.ADD_CONTACT, movedContact.encodeContactUpdate(4, movedContact.displayName, destGroup.groupId)), ObjectPool.integerOf(RESP_MOVE_PHASE3), movedContact, prevGroup, destGroup}));
                } else {
                    EventDispatcher.postRenameError(cmdEntry, moveStatus2);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_MOVE_PHASE3:
                int moveStatus3 = buf.readShortBE();
                if (moveStatus3 == 0) {
                    MmpContact movedContact = (MmpContact) cmdEntry[2];
                    Object prevGroup = cmdEntry[3];
                    MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, destGroup.createUpdatePacket(destGroup.name, -1, movedContact.userId)), ObjectPool.integerOf(RESP_MOVE_PHASE4), movedContact, prevGroup, destGroup}));
                } else {
                    EventDispatcher.postRenameError(cmdEntry, moveStatus3);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_MOVE_PHASE4:
                int moveStatus4 = buf.readShortBE();
                if (moveStatus4 == 0) {
                    MmpContactGroup srcGroup = (MmpContactGroup) cmdEntry[3];
                    MmpContact movedContact = (MmpContact) cmdEntry[2];
                    srcGroup.removeElement(movedContact);
                    MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
                    destGroup.addContact((Object) movedContact);
                    movedContact.onlineSemaphore = destGroup.groupId;
                    trySendData(createSyncContactsCmd());
                } else {
                    EventDispatcher.postRenameError(cmdEntry, moveStatus4);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_ADD_CONTACT_PHASE1:
                int addStatus1 = buf.readShortBE();
                if (addStatus1 == 0) {
                    MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, destGroup.createUpdatePacket(destGroup.name, -1, ((Integer) cmdEntry[5]).intValue())), ObjectPool.integerOf(RESP_ADD_CONTACT_PHASE2), cmdEntry[2], cmdEntry[3], destGroup, cmdEntry[5], cmdEntry[6]}));
                } else {
                    EventDispatcher.postRenameError(cmdEntry, addStatus1);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_ADD_CONTACT_PHASE2:
                int addStatus2 = buf.readShortBE();
                if (addStatus2 == 0) {
                    MmpContactGroup destGroup = (MmpContactGroup) cmdEntry[4];
                    MmpContact newContact = new MmpContact(this, ((Integer) cmdEntry[5]).intValue(), destGroup.groupId, (String) cmdEntry[2], (String) cmdEntry[3], true);
                    destGroup.addContact((Object) newContact);
                    trySendData(createSyncContactsCmd());
                    trySendData(createGetContactsCmd());
                    trySendData(queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.MODIFY_CONTACT, newContact.encodeContactUpdate(5, newContact.displayName, newContact.onlineSemaphore)), ObjectPool.integerOf(RESP_ADD_CONTACT_PHASE3), cmdEntry[2], cmdEntry[3], cmdEntry[4], cmdEntry[5], cmdEntry[6], newContact}));
                    trySendData(createSyncContactsCmd());
                } else {
                    EventDispatcher.postRenameError(cmdEntry, addStatus2);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_ADD_CONTACT_PHASE3:
                int addStatus3 = buf.readShortBE();
                if (addStatus3 == 0) {
                    trySendData(createSyncContactsCmd());
                    trySendData(createSendMessageCmd(this, (MmpContact) cmdEntry[7], (String) cmdEntry[6]));
                } else {
                    EventDispatcher.postRenameError(cmdEntry, addStatus3);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_AUTH_STATUS:
                this.lastError = this.configFlags & MASK_LOW_16;
                shouldRemove = handled;
                break;
            case RESP_UPDATE_PERMISSIONS:
                int permStatus = buf.readShortBE();
                if (permStatus == 0) {
                    ((MmpContact) cmdEntry[2]).updatePermissionFlags(((Integer) cmdEntry[3]).intValue(), ((Integer) cmdEntry[4]).intValue());
                } else {
                    EventDispatcher.postRenameError(cmdEntry, permStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_REMOVE_PERMISSIONS:
                int removePermStatus = buf.readShortBE();
                if (removePermStatus == 0) {
                    ((MmpContact) cmdEntry[2]).updatePermissionFlags(((Integer) cmdEntry[3]).intValue(), 0);
                } else {
                    EventDispatcher.postRenameError(cmdEntry, removePermStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_CONFIG_UPDATE:
                int configStatus = buf.readShortBE();
                if (configStatus != 0) {
                    EventDispatcher.postOperationError(configStatus);
                }
                handled = true;
                shouldRemove = handled;
                break;
            case RESP_NEW_CONTACT_SEARCH:
                buf.skip(10);
                if (buf.readShortLE() == SERVER_RESPONSE_MARKER) {
                    buf.readShortLE();
                    int newSearchSubType = buf.readShortLE();
                    if ((SUBTYPE_SEARCH_RESULT == newSearchSubType || SUBTYPE_SEARCH_LAST == newSearchSubType) && buf.readByte() == 10) {
                        buf.readShortBE();
                        ContactInfo searchResult = ContactInfo.createAccountInfo(this).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
                        buf.readByte();
                        searchResult.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE());
                        MmpContact foundContact = (MmpContact) this.contactMap.get(searchResult.getString(60));
                        if (foundContact != null) {
                            foundContact.setDisplayName(searchResult.getDisplayNameOrId());
                        }
                    }
                    handled = newSearchSubType == SUBTYPE_SEARCH_LAST;
                    shouldRemove = handled;
                    break;
                }
                break;
        }
        if (shouldRemove) {
            queue.removeElementAt(queueIndex);
        }
    }

    public static final ByteBuffer createSendMessageCmd(MmpProtocol protocol, MmpContact contact, String messageText) {
        return ProtocolFactory.createMmpCommand(protocol, MmpCommand.SEND_AUTH_MESSAGE, new ByteBuffer().writeByteLenStr(contact.identifier).writeUTF(messageText).writeShortBE(0));
    }

    private static void writeTaggedStr(ByteBuffer buf, int tag, String value) {
        int len = value.length();
        if (len > 0) {
            buf.writeShortBE(tag).writeShortLE(len + 3).writeShortLE(len + 1).writeCharBytes(value).writeByte(0);
        }
    }

    private static final ByteBuffer createContactCommand(MmpProtocol protocol, MmpContact contact, int permissionType) {
        ByteBuffer contactBuffer = new ByteBuffer().writeShortString(contact.identifier).writeShortBE(0);
        int uniqueId = protocol.generateUniqueGroupId();
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, MmpCommand.ADD_CONTACT, contactBuffer.writeShortBE(uniqueId).writeShortBE(permissionType).writeShortBE(0)), ObjectPool.integerOf(RESP_UPDATE_PERMISSIONS), contact, ObjectPool.integerOf(permissionType), ObjectPool.integerOf(uniqueId)});
    }

    private static final ByteBuffer updateContactCommand(MmpProtocol protocol, MmpContact contact, int existingId, int permissionType) {
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, MmpCommand.DELETE_CONTACT, new ByteBuffer().writeShortString(contact.identifier).writeShortBE(0).writeShortBE(existingId).writeShortBE(permissionType).writeShortBE(0)), ObjectPool.integerOf(RESP_REMOVE_PERMISSIONS), contact, ObjectPool.integerOf(permissionType)});
    }

    public static final ByteBuffer deleteContact(MmpProtocol protocol, MmpContact contact) {
        return contact.canDelete() ? updateContactCommand(protocol, contact, contact.canDelete, 2) : createContactCommand(protocol, contact, 2);
    }

    public static final ByteBuffer blockContact(MmpProtocol protocol, MmpContact contact) {
        return contact.canBlock() ? updateContactCommand(protocol, contact, contact.canBlock, 3) : createContactCommand(protocol, contact, 3);
    }

    public static final ByteBuffer unblockContact(MmpProtocol protocol, MmpContact contact) {
        return contact.canUnblock() ? updateContactCommand(protocol, contact, contact.canUnblock, 14) : createContactCommand(protocol, contact, 14);
    }
    public static final void handleFileTransfer(MmpProtocol protocol, ByteBuffer packet) {
        int bodyLength;
        String messageBody;
        String richText;
        String decodedText;
        long timestamp = packet.readLong();
        int messageType = packet.readShortBE();
        String senderId = packet.readLenPrefixStr();
        packet.readShortBE();
        int headerCount = packet.readShortBE();
        for (int hi = headerCount - 1; hi >= 0; hi--) {
            packet.readShortBE();
            packet.skip(packet.readShortBE());
        }
        while (true) {
            int sectionType = packet.readShortBE();
            bodyLength = packet.readShortBE();
            if (sectionType == 2 || sectionType == 5) {
                break;
            } else {
                packet.skip(bodyLength);
            }
        }
        switch (messageType) {
            case FILE_MSG_TEXT:
                int remaining = bodyLength;
                while (true) {
                    if (remaining <= 0) {
                        decodedText = null;
                        break;
                    } else {
                        int tlvType = packet.readShortBE();
                        int tlvLength = packet.readShortBE();
                        int afterHeader = remaining - 4;
                        if (tlvType == MSG_TLV_BODY) {
                            int encoding = packet.readShortBE();
                            packet.readShortBE();
                            decodedText = encoding == ENCODING_UNICODE ? packet.readUnicodeChars(tlvLength - 4) : packet.readByteChars(tlvLength - 4);
                            break;
                        } else {
                            packet.skip(tlvLength);
                            remaining = afterHeader - tlvLength;
                        }
                    }
                }
                messageBody = decodedText;
                break;
            case FILE_MSG_RICH:
                if (packet.readShortBE() == 0) {
                    packet.skip(24);
                    int richRemaining = bodyLength - 26;
                    while (richRemaining > 0) {
                        int tlvType = packet.readShortBE();
                        int tlvLength = packet.readShortBE();
                        richRemaining -= tlvLength + 4;
                        if (tlvType == MSG_TLV_RICH_CONTAINER) {
                            packet.readShortLE();
                            packet.readShortLE();
                            int colorWord1 = packet.readIntBE();
                            int colorWord2 = packet.readIntBE();
                            int colorWord3 = packet.readIntBE();
                            int colorWord4 = packet.readIntBE();
                            packet.readShortBE();
                            packet.readInt();
                            packet.readByte();
                            packet.readShortBE();
                            int fontDataLen = packet.readShortLE();
                            packet.readShortBE();
                            packet.skip(fontDataLen - 2);
                            if ((colorWord1 | colorWord2 | colorWord3 | colorWord4) == 0) {
                                packet.readShortBE();
                                packet.readShortLE();
                                packet.readShortLE();
                                richText = packet.readModifiedStrTrim();
                            } else {
                                richText = null;
                            }
                            messageBody = richText;
                            if (richText != null && messageBody.length() > 0) {
                                protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, MmpCommand.MSG_DELIVERED, new ByteBuffer().writeLong(timestamp).writeShortBE(2).writeByteLenStr(senderId).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER).writeShortLE(protocol.getConnectionModeValue()).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER_2)));
                                break;
                            }
                        } else {
                            packet.skip(tlvLength);
                        }
                    }
                    richText = null;
                    messageBody = richText;
                    if (richText != null) {
                        protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, MmpCommand.MSG_DELIVERED, new ByteBuffer().writeLong(timestamp).writeShortBE(2).writeByteLenStr(senderId).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER).writeShortLE(protocol.getConnectionModeValue()).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER_2)));
                    }
                } else {
                    richText = null;
                    messageBody = richText;
                    if (richText != null) {
                    }
                }
                break;
            case 3:
            default:
                messageBody = null;
                break;
            case FILE_MSG_NOTIFICATION:
                packet.readIntBE();
                int notifType = packet.readShortBE();
                messageBody = (notifType == 1 || notifType == 4) ? packet.readByteChars(packet.readShortLE() - 1) : null;
                break;
        }
        if (!Utils.nonEmpty(messageBody) || StringUtils.matchesEncoded(senderId, 875573297)) {
            return;
        }
        if (StringUtils.matchesEncoded(senderId, 49)) {
            throw new RuntimeException();
        }
        protocol.onMessage(senderId, 0L, messageBody);
    }

    public final void handleStatusPacket(ByteBuffer buffer) {
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
        return queueCommand(new Object[]{ProtocolFactory.createMmpCommand(this, MmpCommand.ADD_CONTACT, groupBuffer.writeShortBE(groupId).writeShortBE(0).writeShortBE(ENTRY_TYPE_GROUP).writeShortBE(0)), ObjectPool.integerOf(RESP_ADD_GROUP), groupName, ObjectPool.integerOf(groupId)});
    }

    ByteBuffer createGetContactsCmd() {
        return ProtocolFactory.createMmpCommand(this, MmpCommand.GET_CONTACTS_SYNC, (ByteBuffer) null);
    }

    ByteBuffer createSyncContactsCmd() {
        return ProtocolFactory.createMmpCommand(this, MmpCommand.SYNC_CONTACTS, (ByteBuffer) null);
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
        cmdArgs[1] = ObjectPool.integerOf(RESP_SYNC_GROUP_ORDER);
        return queueCommand(cmdArgs);
    }
}
