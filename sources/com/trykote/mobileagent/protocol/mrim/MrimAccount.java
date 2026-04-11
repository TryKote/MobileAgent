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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class MrimAccount extends Account implements ListItem {

    // MRIM progress states
    public static final int PROGRESS_CONNECTING_REDIRECT = 2;
    public static final int PROGRESS_READING_REDIRECT = 3;
    public static final int PROGRESS_CONNECTING_MAIN = 4;
    public static final int PROGRESS_AUTHENTICATING = 5;
    public static final int PROGRESS_LOGGED_IN = 6;

    // MRIM status/config codes
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_DND = 2;
    public static final int STATUS_FREE_CHAT = 3;
    public static final int STATUS_AWAY = 260;
    public static final int STATUS_INVISIBLE = 516;

    // Response type IDs for command queue (used in createAndQueueCommand/handleMrimResponse)
    public static final int RESP_MODIFY_CONTACT = 0;
    public static final int RESP_RENAME_GROUP = 1;
    public static final int RESP_DELETE_CONTACT = 2;
    public static final int RESP_DELETE_GROUP = 3;
    public static final int RESP_ADD_GROUP = 4;
    public static final int RESP_ADD_PHONE_CONTACT = 5;
    public static final int RESP_XMPP_SERVICE = 6;
    public static final int RESP_SINGLE_CONTACT = 7;
    public static final int RESP_CONTACT_INFO = 8;
    public static final int RESP_ADD_CONTACT = 9;
    public static final int RESP_AUTH = 10;
    public static final int RESP_MOVE_FLAG = 11;
    public static final int RESP_MOVE_TO_GROUP = 12;
    public static final int RESP_UPDATE_NAME = 13;
    public static final int RESP_RENAME_CONTACT = 14;
    public static final int RESP_ADD_PHONE = 15;
    public static final int RESP_ADD_TO_GROUP = 16;
    public static final int RESP_AUTH_RESPONSE = 17;

    // Server-side group type IDs
    static final int GROUP_NOT_IN_LIST = 101;
    static final int GROUP_DEFAULT = 102;
    static final int GROUP_BLOCKED = 103;
    static final int GROUP_TEMPORARY = 104;
    static final int GROUP_PHONE_CONTACTS = 105;

    // Property serialization format versions
    private static final int PROPERTY_VERSION_V1 = 12;
    private static final int PROPERTY_VERSION_V2 = 13;

    // Packet header layout
    private static final int OFFSET_SEQ_ID = 8;
    private static final int OFFSET_MSG_TYPE = 12;
    private static final int HEADER_SIZE = 44;

    // Message flags (MRIM protocol)
    private static final int MSG_FLAG_BODY_PRESENT = 128;
    private static final int MSG_FLAG_PLAINTEXT_MASK = 2097160;
    private static final int MSG_FLAG_OFFLINE = 4;
    private static final int MSG_FLAG_RESEND = 1024;
    private static final int MSG_FLAGS_AUTH_REQUEST = 524300;

    // Contact status flags (bitfield operations)
    private static final int CONTACT_FLAG_PENDING = 16;
    private static final int CONTACT_FLAG_NEW = 32;
    private static final int MASK_CLEAR_PENDING = ~(CONTACT_FLAG_PENDING | CONTACT_FLAG_NEW);
    private static final int MASK_CLEAR_IGNORE = ~4;
    private static final int MASK_CLEAR_BLOCK = ~8;

    // Contact flags for new contacts
    private static final int CONTACT_FLAGS_RECEIVED = 65664;
    private static final int CONTACT_FLAGS_PENDING = 65536;

    // Ping interval thresholds (seconds)
    private static final int PING_INTERVAL_WIFI_SEC = 25;
    private static final int PING_INTERVAL_DEFAULT_SEC = 45;
    private static final int MS_PER_SECOND = 1000;

    // Custom note expiry (seconds) — 2 days
    private static final int MAX_NOTE_AGE_SEC = 172800;

    // Buffer size for chat room serialization
    private static final int SERIALIZE_BUFFER_SIZE = 20480;

    // Maximum group ID to scan when finding available slot
    private static final int MAX_GROUP_ID = 20;

    // Auth protocol version identifiers
    private static final int AUTH_VERSION_TAG = 266;
    private static final int AUTH_PROTOCOL_VERSION = 20200;

    // Alignment mask for auth payload padding
    private static final int ALIGNMENT_MASK = 7;

    // Login progress percentages (for UI display)
    private static final int PROGRESS_PERCENT_STARTING = 20;
    private static final int PROGRESS_PERCENT_CONNECTING = 30;
    private static final int PROGRESS_PERCENT_REDIRECT = 40;
    private static final int PROGRESS_PERCENT_RESOLVED = 60;
    private static final int PROGRESS_PERCENT_AUTH_SENT = 80;
    private static final int PROGRESS_PERCENT_LOGOUT = 85;

    // Icon resource IDs
    private static final int ICON_CONNECTING = 153;
    private static final int ICON_STATUS_BASE = 155;

    // Gender string resource IDs
    private static final int STR_GENDER_BASE = 781;

    // Mail notification type codes
    private static final int MAIL_TYPE_LOGIN_OK = 65;
    private static final int MAIL_TYPE_LOGIN_FAIL = 66;
    private static final int MAIL_TYPE_AUTH_ERROR = 68;
    private static final int MAIL_TYPE_COMPLETE = 73;

    // Status text string resource IDs
    private static final int STR_STATUS_TEXT_BASE = 1221;

    // Status change: free_chat maps to hidden DND flag
    private static final int STATUS_FREE_CHAT_FLAG = -2147483647;

    // Extended status feature version
    private static final int FEATURE_VERSION_BASIC = 22;

    public String jabberId;
    public String customDomain;
    public boolean hasCustomDomain;

    public final MrimChatRoomManager chatRoomManager = new MrimChatRoomManager();
    private final MrimResponseHandler responseHandler = new MrimResponseHandler(this);
    public final MrimProfileManager profileManager = new MrimProfileManager(this);

    public boolean isHighlighted;
    private Vector searchEntryList;

    public MrimAccount(int accountId, String login, String password) {
        super(accountId, login, password);
        this.lastError = 0;
        this.configFlags = 1;
        MrimContactGroup defaultGrp = new MrimContactGroup(this, -1, GROUP_DEFAULT, Storage.resources().getString(StringResKeys.STR_GROUP_DEFAULT));
        defaultGrp.isSpecial = true;
        this.defaultGroup = defaultGrp;
        this.isHighlighted = true;
        this.searchEntryList = ObjectPool.newVector();
    }

    @Override
    public final int getType() {
        return TYPE_MRIM;
    }

    public MrimAccount(ByteBuffer buffer) {
        super(buffer);
        int groupCount = buffer.readInt();
        for (int i = 0; i < groupCount; i++) {
            this.groups.addElement(new MrimContactGroup(this, buffer));
        }
        MrimContactGroup defaultGrp = new MrimContactGroup(this, buffer);
        defaultGrp.isSpecial = true;
        this.defaultGroup = defaultGrp;
        ByteBuffer extraBuffer = new ByteBuffer();
        int extraLen = buffer.readInt();
        if (extraLen > 0) {
            extraBuffer.writeBytesAt(buffer.data, buffer.offset, extraLen);
            buffer.skip(extraLen);
        }
        if (extraBuffer.length > 0) {
            try {
                this.chatRoomManager.deserialize(extraBuffer);
            } catch (Throwable unused) {
                this.chatRoomManager.nickname = null;
                this.chatRoomManager.list = null;
            }
        } else {
            this.chatRoomManager.nickname = null;
            this.chatRoomManager.list = null;
        }
        this.isHighlighted = true;
        this.searchEntryList = ObjectPool.newVector();
    }

    @Override
    public final Account serializeAccount(ByteBuffer buffer, boolean includeGroups, boolean includePrivate) {
        super.serializeAccount(buffer, includeGroups, includePrivate);
        if (includePrivate) {
            buffer.writeBufferIntLen(serializePrivateData(includeGroups));
        } else {
            buffer.writeIntLE(0);
        }
        return this;
    }

    @Override
    public final void saveProperties(ByteBuffer buffer) {
        buffer.writeIntLE(PROPERTY_VERSION_V2).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount);
        VCard profile = this.profileManager.profile;
        boolean hasCoords = profile.hasCoordinates();
        buffer.writeBoolean(hasCoords);
        if (hasCoords) {
            buffer.writeStringLatin1(profile.latStr).writeStringLatin1(profile.lonStr).writeStringLatin1(profile.mapTypeStr).writeStringUTF16(profile.phone).writeStringLatin1(profile.email).writeStringLatin1(profile.nickname).writeStringLatin1(profile.address).writeStringLatin1(profile.zoomStr).writeIntBE(profile.gender).writeBoolean(profile.dirty);
        }
    }

    @Override
    public final void loadProperties(ByteBuffer buffer) {
        int version = buffer.readInt();
        if (version == PROPERTY_VERSION_V1) {
            this.syncSeq = buffer.readInt();
            this.sentCount = buffer.readInt();
            this.recvCount = buffer.readInt();
            buffer.readInt();
            return;
        }
        if (version == PROPERTY_VERSION_V2) {
            this.syncSeq = buffer.readInt();
            this.sentCount = buffer.readInt();
            this.recvCount = buffer.readInt();
            this.profileManager.profile = VCard.deserializeFromBuffer(buffer);
        }
    }

    private final ByteBuffer serializePrivateData(boolean includeChats) {
        ByteBuffer buffer = new ByteBuffer();
        if (includeChats && this.jabberId != null && this.chatRoomManager.getCount() >= 3) {
            try {
                buffer.ensureCapacity(SERIALIZE_BUFFER_SIZE);
                this.chatRoomManager.serialize(buffer);
            } catch (Throwable unused) {
                buffer.clear();
            }
        }
        return buffer;
    }

    public final MrimContact findContactByIdentifier(String identifier) {
        return (MrimContact) getContact((Object) identifier);
    }

    @Override
    public final ContactGroup createOnlineGroup() {
        return new MrimContactGroup(this, -1, GROUP_NOT_IN_LIST, Storage.resources().getString(StringResKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override
    public final ContactGroup createBlockedGroup() {
        return new MrimContactGroup(this, -1, GROUP_TEMPORARY, Storage.resources().getString(StringResKeys.STR_GROUP_TEMPORARY));
    }

    @Override
    public final ContactGroup createOfflineGroup() {
        return new MrimContactGroup(this, -1, GROUP_BLOCKED, Storage.resources().getString(StringResKeys.STR_GROUP_IGNORE));
    }

    @Override
    public final ContactGroup createSpecialGroup() {
        return new MrimContactGroup(this, -1, GROUP_PHONE_CONTACTS, Storage.resources().getString(StringResKeys.STR_GROUP_PHONE_CONTACTS));
    }

    public final MrimContactGroup getFirstContactGroup() {
        return (MrimContactGroup) this.groups.elementAt(0);
    }

    @Override
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        this.extras.removeAllElements();
        return 0;
    }

    @Override
    public final int getIconId() {
        if (this.progress >= PROGRESS_STARTING && this.progress < PROGRESS_CONNECTED) {
            return ICON_CONNECTING;
        }
        switch (this.lastError) {
            case STATUS_DISCONNECTED:
                return ICON_STATUS_BASE;
            case STATUS_ONLINE:
                return ICON_STATUS_BASE + 1;
            case STATUS_DND:
                return ICON_STATUS_BASE + 2;
            case STATUS_FREE_CHAT:
                return ICON_STATUS_BASE + 3;
            case STATUS_AWAY:
                return ICON_STATUS_BASE + 4;
            case STATUS_INVISIBLE:
                return ICON_STATUS_BASE + 5;
            default:
                return ICON_STATUS_BASE + 2 + (this.lastError >> 8);
        }
    }

    private void handleConnectionProgress() throws Throwable {
        switch (this.progress) {
            case PROGRESS_DISCONNECTED:
                this.dataBuffer.clear();
                this.msgCount = 0;
                break;
            case PROGRESS_STARTING:
                RemoteLogger.log("MRIM", "progress STARTING, connecting to redirect server");
                this.msgCount = PROGRESS_PERCENT_STARTING;
                this.state = 0;
                this.connection = new ConnectionThread(Storage.resources().getString(PackedStringKeys.HOST_MRIM_REDIRECT));
                this.progress = PROGRESS_CONNECTING_REDIRECT;
                AppController.needsRepaint = true;
                break;
            case PROGRESS_CONNECTING_REDIRECT:
                this.msgCount = PROGRESS_PERCENT_CONNECTING;
                if (this.connection.getState() == ConnectionThread.STATE_CONNECTED) {
                    RemoteLogger.log("MRIM", "redirect server connected, reading address");
                    this.msgCount = PROGRESS_PERCENT_REDIRECT;
                    this.progress = PROGRESS_READING_REDIRECT;
                    AppController.needsRepaint = true;
                }
                break;
            case PROGRESS_READING_REDIRECT:
                this.connection.drainInput(this.dataBuffer);
                int dataLen = this.dataBuffer.length;
                if (dataLen > 0) {
                    AccountManager.recordInboundTraffic((Account) this, dataLen);
                    this.msgCount = PROGRESS_PERCENT_RESOLVED;
                    StringBuffer sb = ObjectPool.newStringBuffer();
                    while (dataLen > 0) {
                        dataLen--;
                        char ch = (char) this.dataBuffer.readByte();
                        if (Utils.isDigitOrSep(ch)) {
                            sb.append(ch);
                        }
                    }
                    this.connection.state = ConnectionThread.STATE_CLOSING;
                    String mainServer = ObjectPool.toStringAndRelease(sb);
                    RemoteLogger.log("MRIM", "redirect resolved to: " + mainServer);
                    this.connection = new ConnectionThread(mainServer);
                    this.progress = PROGRESS_CONNECTING_MAIN;
                    AppController.needsRepaint = true;
                }
                break;
            case PROGRESS_CONNECTING_MAIN:
                if (this.connection.getState() == ConnectionThread.STATE_CONNECTED) {
                    RemoteLogger.log("MRIM", "main server connected, sending auth packet");
                    this.msgCount = PROGRESS_PERCENT_AUTH_SENT;
                    sendData(ProtocolFactory.createMrimAuthPacket(this));
                    this.progress = PROGRESS_AUTHENTICATING;
                    AppController.needsRepaint = true;
                }
                break;
        }
    }

    @Override
    public final void loadData() throws Throwable {
        handleConnectionProgress();
        if (this.progress < PROGRESS_AUTHENTICATING) {
            return;
        }
        this.connection.drainInput(this.dataBuffer);
        while (true) {
            ByteBuffer packet = this.dataBuffer.extractPNG();
            if (packet == null) {
                if (packet == null && this.lastError != 0 && this.connection != null && this.connection.getState() == ConnectionThread.STATE_CLOSED) {
                    closeConnection();
                    this.lastError = getDefaultError();
                }
                if (this.timeout <= 0 || !TimerManager.isTimerExpired(this.deadline)) {
                    return;
                }
                trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_PING, (ByteBuffer) null));
                return;
            }
            AccountManager.recordInboundPacket((Account) this, packet);
            int msgType = packet.peekIntAt(OFFSET_MSG_TYPE);
            int seqId = packet.peekIntAt(OFFSET_SEQ_ID);
            packet.skip(HEADER_SIZE);
            switch (msgType) {
                case MrimCommand.CS_HELLO_ACK:
                    handleHelloAck(packet);
                    break;
                case MrimCommand.CS_LOGOUT:
                    this.msgCount = PROGRESS_PERCENT_LOGOUT;
                    incrementSync();
                    break;
                case MrimCommand.CS_MAIL_NOTIFY:
                    handleMailNotify(packet);
                    break;
                case MrimCommand.CS_MESSAGE_ACK:
                    Conversation.handleMessage(this, packet, 0L);
                    break;
                case MrimCommand.CS_USER_STATUS:
                    handleUserStatus(packet);
                    break;
                case MrimCommand.CS_CONTACT_LIST_REPLY:
                    this.responseHandler.dispatch(packet, seqId);
                    break;
                case MrimCommand.CS_LOGOUT_FORCE:
                    handleTimeout();
                    break;
                case MrimCommand.CS_USER_INFO:
                    handleUserInfo(packet);
                    break;
                case MrimCommand.CS_ADD_CONTACT_ACK:
                    this.responseHandler.dispatch(packet, seqId);
                    break;
                case MrimCommand.CS_MODIFY_CONTACT_ACK:
                    this.responseHandler.dispatch(packet, seqId);
                    break;
                case MrimCommand.CS_OFFLINE_MESSAGE_ACK:
                    handleOfflineMessage(packet);
                    break;
                case MrimCommand.CS_AUTHORIZE_ACK:
                    MrimContact readContact = findContactByIdentifier(packet.readWideStr());
                    if (readContact != null) {
                        readContact.hasUnreadFlag &= -2;
                    }
                    break;
                case MrimCommand.CS_CONTACT_LIST_ACK:
                    this.responseHandler.dispatch(packet, seqId);
                    break;
                case MrimCommand.CS_ANKETA_UPDATE_ACK:
                    this.responseHandler.dispatch(packet, seqId);
                    break;
                case MrimCommand.CS_CONTACT_LIST2:
                    Conversation.parseContactList(this, packet);
                    break;
                case MrimCommand.CS_SEARCH_RESULT_ACK:
                    this.responseHandler.dispatch(packet, seqId);
                    break;
                case MrimCommand.CS_ANKETA_INFO:
                    handleAnketaInfo(packet, seqId);
                    break;
                case MrimCommand.CS_MAILBOX_STATUS:
                    notifyNewMail(packet.readInt(), packet.readUnicodeStr(), packet.readUnicodeStr());
                    break;
                case MrimCommand.CS_MPOP_SESSION:
                    String sectionKey = packet.readWideStr();
                    String xmlData = packet.readWideStr();
                    if (StringUtils.matchesKey(PackedStringKeys.MRIM_GEO_LIST, sectionKey)) {
                        this.profileManager.profile.updatePhotos(new XmlParser(xmlData).parse());
                        this.profileManager.sync();
                    }
                    break;
                case MrimCommand.CS_MPOP_SESSION_ACK:
                    packet.readInt();
                    break;
                case MrimCommand.CS_CUSTOM_NOTE:
                    handleCustomNote(packet);
                    break;
                case MrimCommand.CS_ANKETA_INFO_ACK:
                    this.profileManager.receiveContactProfile(packet.readWideStr(), packet.readBufferArray());
                    break;
                case MrimCommand.CS_PROFILE_DATA:
                    handleProfileData(packet);
                    break;
            }
            packet.clear();
            AppController.needsLayoutUpdate = true;
        }
    }

    private void handleHelloAck(ByteBuffer packet) throws Throwable {
        long pingInterval = Utils.min(packet.readInt(), Storage.state().getBool(UIKeys.FLAG_WIFI_CONNECTION) ? PING_INTERVAL_WIFI_SEC : PING_INTERVAL_DEFAULT_SEC) * MS_PER_SECOND;
        this.timeout = pingInterval;
        this.deadline = System.currentTimeMillis() + pingInterval;
        ByteBuffer authPacket = new ByteBuffer().writeStringLatin1(this.login).writeStringLatin1(getFormattedName());
        boolean useExtended = Storage.state().getBool(SettingsKeys.SETTING_EXTENDED_STATUS);
        sendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_LOGIN2, authPacket.writeIntLE(useExtended ? -1 : FEATURE_VERSION_BASIC).writeStringLatin1(useExtended ? null : new ByteBuffer().writeCompressed(PackedStringKeys.MRIM_CLIENT_VERSION).writeExtendedInt(2229599).getStringAndClear()).writeCompressed(PackedStringKeys.MRIM_GEO_LIST_PACKET).writeStringLatin1(XmppContactGroup.buildAuthData()).writeBuffer(XmppContactGroup.buildSyncPayload(this))));
        this.progress = PROGRESS_LOGGED_IN;
    }

    private void handleUserStatus(ByteBuffer packet) {
        int statusCode = packet.readInt();
        String statusTitle = packet.readWideStr();
        packet.readUTF8Str((String) null);
        packet.readUTF8Str((String) null);
        MrimContact contact = findContactByIdentifier(packet.readHexStr());
        if (contact != null && !contact.isOnline()) {
            packet.readInt();
            String statusMsg = packet.readWideStr();
            int prevCount = contact.unreadCount;
            contact.unreadCount = statusCode;
            contact.statusMessage = statusMsg;
            contact.defaultIcon = AppController.resolveServerIcon(statusCode, statusTitle);
            contact.highlighted = statusCode != 0;
            if (statusCode == 0) {
                contact.clearVCard();
            }
            contact.dirty = true;
            contact.updateRenderState();
            if (prevCount == 0 && statusCode != 0) {
                NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CONTACT_ONLINE);
            }
        }
    }

    private void handleUserInfo(ByteBuffer packet) {
        while (packet.length > 0) {
            String paramKey = packet.readWideStr();
            if (StringUtils.matchesKey(PackedStringKeys.MRIM_NICKNAME, paramKey)) {
                setDisplayName(packet.readUTF8Str((String) null));
            } else if (StringUtils.matchesKey(PackedStringKeys.MRIM_MESSAGES_UNREAD, paramKey)) {
                notifyNewMail(Utils.parseInt((Object) packet.readUTF8Str((String) null)), (String) null, (String) null);
            } else if (StringUtils.matchesKey(PackedStringKeys.MRIM_CLIENT_ENDPOINT, paramKey)) {
                String domainStr = packet.readUTF8Str((String) null);
                this.customDomain = StringUtils.prefix(domainStr, domainStr.indexOf(58));
            } else if (StringUtils.matchesKey(PackedStringKeys.MRIM_HAS_MYMAIL, paramKey)) {
                packet.readWideStr();
                this.hasCustomDomain = true;
            } else if (StringUtils.matchesKey(PackedStringKeys.MRIM_GEO_SUGGEST, paramKey)) {
                packet.skip((((((((packet.readInt() - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + (packet.readUTF8Str((String) null).length() << 1))) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length()));
            } else {
                packet.readWideStr();
            }
        }
    }

    private void handleOfflineMessage(ByteBuffer packet) throws Throwable {
        int messageType;
        String senderName;
        String headerRef;
        int messageFlags;
        long timestamp;
        int encodingType;
        String messageBody = null;
        trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_DELETE_OFFLINE_MESSAGE, new ByteBuffer().writeIntLE(packet.readInt()).writeIntLE(packet.readInt())));
        try {
            int prevReserved = this.reserved1;
            this.reserved1 = prevReserved + 1;
            if (prevReserved != 0) {
                Storage.state().setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, 0);
            }
            Hashtable headers = new Hashtable();
            String headerKey = null;
            String rawText = Utils.removeChar(packet.readWideStr(), '\r');
            int length = rawText.length();
            StringBuffer lineBuffer = ObjectPool.newStringBuffer();
            boolean parsingValue = false;
            int pos = 0;
            while (pos < length) {
                char ch = rawText.charAt(pos);
                if (!parsingValue) {
                    if (ch == '\n' && lineBuffer.length() == 0) {
                        ObjectPool.toStringAndRelease(lineBuffer);
                        String typeCodeStr = (String) headers.get(Storage.resources().getString(PackedStringKeys.HEADER_X_MRIM_MULTICHAT));
                        int typeCode = typeCodeStr != null ? -1 : Integer.parseInt(typeCodeStr);
                        messageType = typeCode;
                        senderName = typeCode >= 0 ? null : Base64.decode(StringUtils.suffix((String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_SUBJECT)), 13)).readAllWideStr();
                        headerRef = (String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_SENDER));
                        messageFlags = Integer.parseInt((String) headers.get(Storage.resources().getString(PackedStringKeys.HEADER_X_MRIM_FLAGS)), 16);
                        timestamp = Utils.parseDateTime((String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_DATE)));
                        encodingType = 1;
                        if ((messageFlags & MSG_FLAG_BODY_PRESENT) != 0) {
                            String bodyText = StringUtils.suffix(rawText, pos);
                            if ((messageFlags & MSG_FLAG_PLAINTEXT_MASK) == 0) {
                                messageBody = Base64.decode(bodyText).readAllWideStr();
                            } else {
                                messageBody = bodyText;
                                encodingType = 0;
                            }
                        } else {
                            int tagIdx = StringUtils.indexOfPackedLong(rawText, 57408234938722L);
                            messageBody = Base64.decode(StringUtils.substring(rawText, tagIdx + 6, rawText.indexOf(Storage.resources().getString(PackedStringKeys.MRIM_MESSAGE_DELIMITER), tagIdx))).readAllWideStr();
                        }
                        if (messageType != -1 || (messageType >= 0 && messageType <= 5 && messageType != 1 && messageType != 3)) {
                            Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(messageFlags | MSG_FLAG_OFFLINE | MSG_FLAG_BODY_PRESENT).writeStringLatin1((String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_FROM))).writeString(messageBody, encodingType).writeIntLE(0).writeIntLE(0).writeIntLE(messageType).writeStringUTF16(senderName).writeStringLatin1(headerRef), timestamp);
                        }
                        Storage.state().setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, 1);
                        return;
                    } else if (ch == ':') {
                        headerKey = ObjectPool.toString(lineBuffer, false);
                        parsingValue = true;
                        pos++;
                    } else {
                        lineBuffer.append(ch);
                    }
                } else if (ch == '\n') {
                    headers.put(headerKey, ObjectPool.toString(lineBuffer, false));
                    parsingValue = false;
                } else {
                    lineBuffer.append(ch);
                }
                pos++;
            }
            ObjectPool.toStringAndRelease(lineBuffer);
            String typeCodeStr = (String) headers.get(Storage.resources().getString(PackedStringKeys.HEADER_X_MRIM_MULTICHAT));
            int typeCode = typeCodeStr != null ? -1 : Integer.parseInt(typeCodeStr);
            messageType = typeCode;
            senderName = typeCode >= 0 ? null : Base64.decode(StringUtils.suffix((String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_SUBJECT)), 13)).readAllWideStr();
            headerRef = (String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_SENDER));
            messageFlags = Integer.parseInt((String) headers.get(Storage.resources().getString(PackedStringKeys.HEADER_X_MRIM_FLAGS)), 16);
            timestamp = Utils.parseDateTime((String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_DATE)));
            encodingType = 1;
            if ((messageFlags & MSG_FLAG_BODY_PRESENT) != 0) {
            }
            if (messageType != -1) {
                Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(messageFlags | MSG_FLAG_OFFLINE | MSG_FLAG_BODY_PRESENT).writeStringLatin1((String) headers.get(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_FROM))).writeString(messageBody, encodingType).writeIntLE(0).writeIntLE(0).writeIntLE(messageType).writeStringUTF16(senderName).writeStringLatin1(headerRef), timestamp);
                Storage.state().setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, 1);
            }
        } catch (RuntimeException e) {
            Storage.state().setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, 1);
            throw e;
        } catch (Error e) {
            Storage.state().setInt(SessionKeys.FLAG_MRIM_DATA_LOADED, 1);
            throw e;
        }
    }

    private void handleAnketaInfo(ByteBuffer packet, int seqId) {
        if (packet.readInt() != 1 || packet.readInt() <= 0) {
            return;
        }
        String foundEmail = packet.readWideStr();
        for (int i = this.searchEntryList.size() - 1; i >= 0; i--) {
            SearchEntry entry = (SearchEntry) this.searchEntryList.elementAt(i);
            if (seqId == entry.id) {
                this.searchEntryList.removeElementAt(i);
                int entryType = entry.type;
                if (entryType == 1) {
                    sendDeleteCommand(foundEmail);
                    AppController.openUserProfile(this, foundEmail);
                } else if (entryType == 2) {
                    ContactInfo contactInfo = ContactInfo.createForAccount(this);
                    contactInfo.setEmailAddress(foundEmail);
                    Storage.state().setObject(ContactKeys.SLOT_CONTACT_INFO, contactInfo);
                    EventDispatcher.postEvent(new ProtocolEvent(ProtocolEvent.ADD_CONTACT_CONFIRM, null));
                }
            }
        }
    }

    private void handleCustomNote(ByteBuffer packet) {
        int noteFlags = packet.readInt();
        String contactAddr = StringUtils.intern(packet.readWideStr().toLowerCase());
        long sentTime = packet.readLong();
        int noteTimestamp = packet.readInt();
        String noteText = packet.readUTF8Str((String) null);
        MrimContact noteContact = findContactByIdentifier(contactAddr);
        if (noteContact == null || noteContact.isOnline()) {
            return;
        }
        if ((noteFlags & 2) != 0) {
            noteContact.customLink = noteText;
        } else if ((noteFlags & 5) != 0) {
            if (Storage.state().getBool(SettingsKeys.SETTING_CUSTOM_NOTE_ENABLED) && !StringUtils.equals(noteText, noteContact.customNote) && ((int) (System.currentTimeMillis() / MS_PER_SECOND)) - noteTimestamp < MAX_NOTE_AGE_SEC && noteContact.getLastSentTime() != sentTime) {
                Storage.state().setObject(ContactKeys.SLOT_CURRENT_CONTACT_ID, (Object) noteContact.identifier);
                NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CUSTOM_NOTE);
                noteContact.addFlag(2);
                noteContact.appendMessage(16, noteText, 0L, sentTime);
                ContactGroup contactGroup = noteContact.account.findGroup(noteContact);
                if (contactGroup != null && contactGroup.isSpecial) {
                    contactGroup.toggleSpecial();
                }
                noteContact.updateRenderState();
            }
            noteContact.customNote = noteText;
        }
    }

    private void handleProfileData(ByteBuffer packet) {
        if (!Storage.state().hasMemory()) {
            return;
        }
        Vector buffers = packet.readBufferArray();
        if (buffers.isEmpty()) {
            return;
        }
        String[] cardFields = VCard.parseCardFromBuffer((ByteBuffer) buffers.elementAt(0));
        if (cardFields.length < 8 || this.profileManager.profile.hasCoordinates()) {
            return;
        }
        String cardType = cardFields[2];
        if (StringUtils.matchesKey(PackedStringKeys.MRIM_MAPPOINT, cardType)) {
            this.profileManager.setSimpleLocation(cardFields[1], cardFields[0]);
        } else if (StringUtils.matchesKey(PackedStringKeys.MRIM_MAPOBJECT, cardType)) {
            try {
                VCard profile = this.profileManager.profile;
                String typeStr = Storage.resources().getString(PackedStringKeys.MRIM_MAPOBJECT);
                String empty = Storage.emptyStr;
                profile.setCardData(cardFields[0], cardFields[1], typeStr, empty, empty, empty, cardFields[6], cardFields[7]);
            } catch (Throwable unused) {
                this.profileManager.profile.clearCoordinates();
            }
            this.profileManager.sizeCache.lastScale = -1;
            this.profileManager.profile.phone = cardFields[3];
        }
        this.profileManager.profile.dirty = true;
        if (AccountManager.getTotalSyncCount() == 10) {
            EventDispatcher.postNotification(Storage.resources().getString(StringResKeys.STR_MRIM_DISCONNECT));
        }
    }

    public final ByteBuffer createAndQueueCommand(Object commandData) {
        if (!isConnected()) {
            return null;
        }
        Object[] tuple = (Object[]) commandData;
        ByteBuffer buffer = (ByteBuffer) tuple[0];
        tuple[0] = ObjectPool.integerOf(buffer.peekIntAt(OFFSET_SEQ_ID));
        this.extras.addElement(commandData);
        return buffer;
    }

    @Override
    public final Vector getPendingContacts() {
        Vector pendingList = super.getPendingContacts();
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            MrimContact mrimContact = (MrimContact) elements.nextElement();
            if (mrimContact.isSystem() && !mrimContact.canUnblock()) {
                pendingList.addElement(mrimContact);
            }
        }
        return pendingList;
    }

    public final int setConfiguration(int statusCode) {
        String statusText;
        String typeStr;
        this.configFlags = statusCode;
        if (!isConnected()) {
            if (isConnecting()) {
                return 487;
            }
            return connect(0);
        }
        this.lastError = statusCode;
        int rawStatus = this.configFlags & 7;
        int statusTextId;
        switch (this.configFlags) {
            case STATUS_ONLINE:
                statusTextId = STR_STATUS_TEXT_BASE;
                break;
            case STATUS_DND:
                statusTextId = STR_STATUS_TEXT_BASE + 1;
                break;
            case STATUS_FREE_CHAT:
                statusTextId = STR_STATUS_TEXT_BASE;
                break;
            case STATUS_AWAY:
                statusTextId = STR_STATUS_TEXT_BASE + 3;
                break;
            case STATUS_INVISIBLE:
                statusTextId = STR_STATUS_TEXT_BASE + 4;
                break;
            default:
                statusTextId = -1;
                break;
        }
        if (statusTextId >= 0) {
            statusText = Storage.state().getString(statusTextId);
        } else {
            statusText = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_CONFIG_STATUS_PREFIX)).append(this.configFlags >> 8));
        }
        switch (this.configFlags) {
            case STATUS_ONLINE:
                typeStr = Storage.resources().getString(StringResKeys.STR_STATUS_ONLINE);
                break;
            case STATUS_DND:
                typeStr = Storage.resources().getString(StringResKeys.STR_STATUS_DND);
                break;
            case STATUS_FREE_CHAT:
                typeStr = Storage.resources().getString(StringResKeys.STR_STATUS_ONLINE);
                break;
            case STATUS_AWAY:
                typeStr = Storage.resources().getString(StringResKeys.STR_STATUS_AWAY);
                break;
            case STATUS_INVISIBLE:
                typeStr = Storage.resources().getString(StringResKeys.STR_STATUS_INVISIBLE);
                break;
            default:
                typeStr = Storage.resources().getString(StringResKeys.STR_CONFIG_TYPE_BASE + (this.configFlags >> 8));
                break;
        }
        return trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_CHANGE_STATUS, new ByteBuffer().writeIntLE(rawStatus != STATUS_FREE_CHAT ? rawStatus : STATUS_FREE_CHAT_FLAG).writeStringLatin1(statusText).writeStringUTF16(typeStr).writeStringUTF16(Storage.emptyStr).writeIntLE(Storage.state().getBool(SettingsKeys.SETTING_EXTENDED_STATUS) ? -1 : FEATURE_VERSION_BASIC)));
    }

    @Override
    public final int validateSend(Contact baseContact, String message, long timestamp) {
        int result = super.validateSend(baseContact, message, timestamp);
        if (result != 0) {
            return result;
        }
        this.sentCount++;
        return trySendData(XmppContactGroup.createContactAddCommand(this, (MrimContact) baseContact, message, timestamp));
    }

    @Override
    public final int validateGroupRename(ContactGroup group, String newName) {
        int result = super.validateGroupRename(group, newName);
        if (result != 0) {
            return result;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) group;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimGroup.serverId).writeIntLE(mrimGroup.groupId).writeIntLE(0).writeStringUTF16(newName).writeStringUTF16(newName).writeIntLE(0)), ObjectPool.integerOf(RESP_RENAME_GROUP), mrimGroup, newName}));
    }

    @Override
    public final int validateGroupCreate(String groupName) {
        int result = super.validateGroupCreate(groupName);
        if (result != 0) {
            return result;
        }
        ByteBuffer buffer = new ByteBuffer();
        int size = (this.groups.size() << 24) | 2;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_ADD_CONTACT, buffer.writeIntLE(size).writeZeros(8).writeStringUTF16(groupName).writeZeros(12)), ObjectPool.integerOf(RESP_ADD_GROUP), groupName, ObjectPool.integerOf(size)}));
    }

    @Override
    public final int validateGroupDelete(ContactGroup group) {
        int result = super.validateGroupDelete(group);
        if (result != 0) {
            return result;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) group;
        ByteBuffer deletePacket = new ByteBuffer().writeIntLE(mrimGroup.serverId).writeIntLE(mrimGroup.groupId | 1).writeIntLE(0);
        String name = mrimGroup.name;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, deletePacket.writeStringUTF16(name).writeStringUTF16(name).writeIntLE(0)), ObjectPool.integerOf(RESP_DELETE_GROUP), mrimGroup}));
    }

    @Override
    public final int validateResend(Contact baseContact) {
        int result = super.validateResend(baseContact);
        if (result != 0) {
            return result;
        }
        if (baseContact.isOnline()) {
            return removeContact(baseContact, true);
        }
        MrimContact mrimContact = (MrimContact) baseContact;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags | 1).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(mrimContact.displayName).writeStringLatin1(mrimContact.contactGroupsStr)), ObjectPool.integerOf(RESP_DELETE_CONTACT), mrimContact}));
    }

    @Override
    public final int validateDelete(Contact baseContact) {
        return sendDeleteCommand(((MrimContact) baseContact).simpleIdentifier);
    }

    public final int sendDeleteCommand(String contactAddress) {
        return trySendData(ProtocolFactory.createChatRoomCmd(this, contactAddress, 7));
    }

    @Override
    public final int validateObject(Object searchFields) {
        String[] fields = (String[]) searchFields;
        ByteBuffer buffer = new ByteBuffer();
        for (int i = 0; i < fields.length; i++) {
            if (i != 9) {
                String value = fields[i];
                if (Utils.nonEmpty(value)) {
                    buffer.writeIntLE(i).writeString(value, (1 << i) & 28);
                }
            }
        }
        if (Utils.nonEmpty(fields[9])) {
            buffer.writeIntLE(9).writeStringLatin1(fields[9]);
        }
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_WP_REQUEST, buffer), ObjectPool.integerOf(RESP_CONTACT_INFO)}));
    }

    @Override
    public final int validateModify(Contact baseContact, Object[] fieldValues) {
        int result = super.validateModify(baseContact, fieldValues);
        if (result != 0) {
            return result;
        }
        String displayName = (String) fieldValues[0];
        int groupCount = fieldValues.length - 1;
        String[] groupIds = new String[groupCount];
        for (int i = 0; i < groupCount; i++) {
            groupIds[i] = Utils.extractDigits((String) fieldValues[i + 1]);
        }
        MrimContact mrimContact = (MrimContact) baseContact;
        if (mrimContact.isOffline() && groupCount == 0) {
            return 709;
        }
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            MrimContact otherContact = (MrimContact) elements.nextElement();
            for (int j = groupCount - 1; j >= 0; j--) {
                if (otherContact != baseContact && otherContact.isInGroup(groupIds[j])) {
                    return 486;
                }
            }
        }
        String groupsStr = Utils.joinComma(groupIds);
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(displayName).writeStringLatin1(groupsStr)), ObjectPool.integerOf(RESP_MODIFY_CONTACT), mrimContact, displayName, groupsStr}));
    }

    public final int findAvailableGroupId() {
        int idx;
        int size = this.groups.size();
        for (int candidateId = 0; candidateId < MAX_GROUP_ID; candidateId++) {
            for (idx = 0; idx <= size; idx = idx + 1) {
                if (idx == size) {
                    return candidateId;
                }
                idx = ((MrimContactGroup) this.groups.elementAt(idx)).serverId != candidateId ? idx + 1 : 0;
            }
        }
        return 0;
    }

    @Override
    public final int validateGroupAdd(String contactAddress, String displayName, String authMessage, ContactGroup group, boolean requestAuth) {
        int result = super.validateGroupAdd(contactAddress, displayName, authMessage, group, requestAuth);
        if (result != 0) {
            return result;
        }
        MrimContact contact = findContactByIdentifier(contactAddress);
        if (contact == null || contact.isOnline()) {
            trySendData(ProtocolFactory.createPasswordAuthCmd(this, contactAddress));
            return trySendData(XmppContactGroup.createContactCommand(this, 0, contactAddress, displayName, authMessage, (MrimContactGroup) group, requestAuth));
        }
        trySendData(createAddToGroupCmd(contact, (MrimContactGroup) group));
        return trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(requestAuth ? MSG_FLAGS_AUTH_REQUEST : 12).writeStringLatin1(contactAddress).writeStringArray(new String[]{this.displayName, authMessage}).writeIntLE(0)));
    }

    @Override
    public final int validateContactDelete(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        if (mrimContact.isOnline()) {
            return trySendData(XmppContactGroup.createContactCommand(this, 48, mrimContact.simpleIdentifier, mrimContact.displayName, Storage.emptyStr, getFirstContactGroup(), false));
        }
        int flags = mrimContact.statusFlags;
        return trySendData(createMoveContactCmd(mrimContact, (flags & CONTACT_FLAG_PENDING) != 0 ? flags & MASK_CLEAR_PENDING : flags | CONTACT_FLAG_PENDING | CONTACT_FLAG_NEW));
    }

    @Override
    public final int validateContactBlock(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        int flags = mrimContact.statusFlags ^ 8;
        int newFlags = flags;
        if ((flags & 8) != 0) {
            newFlags &= MASK_CLEAR_IGNORE;
        }
        return trySendData(createMoveContactCmd(mrimContact, newFlags));
    }

    @Override
    public final int validateContactUnblock(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        int flags = mrimContact.statusFlags ^ 4;
        int newFlags = flags;
        if ((flags & 4) != 0) {
            newFlags &= MASK_CLEAR_BLOCK;
        }
        return trySendData(createMoveContactCmd(mrimContact, newFlags));
    }

    @Override
    public final int validateContactResend(Contact baseContact) {
        int result = super.validateContactResend(baseContact);
        return result != 0 ? result : trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(MSG_FLAG_RESEND).writeStringLatin1(((MrimContact) baseContact).simpleIdentifier).writeIntLE(0).writeIntLE(0)));
    }

    @Override
    public final int validateMove(Contact baseContact, ContactGroup group, ContactGroup destGroup) {
        int result = super.validateMove(baseContact, group, destGroup);
        return result != 0 ? result : trySendData(createAddToGroupCmd((MrimContact) baseContact, (MrimContactGroup) destGroup));
    }

    @Override
    public final int disconnect() {
        int result = super.disconnect();
        if (result != 0) {
            return result;
        }
        trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_LOGOUT_CMD, (ByteBuffer) null));
        closeConnection();
        this.lastError = getDefaultError();
        return 0;
    }

    private final String getContactDisplayName(String identifier) {
        MrimContact contact = findContactByIdentifier(identifier);
        return contact != null ? contact.displayName : identifier;
    }

    private final StringBuffer formatContactName(String identifier) {
        return Utils.appendCommaIf(ObjectPool.newStringBuffer().append(getContactDisplayName(identifier)), true).append('\n');
    }

    public final void receivePrivateMessage(String senderAddress, String messageBody, String senderName, String messageAuthor, long timestamp) {
        MrimContact contact = findContactByIdentifier(senderAddress);
        MrimContact targetContact = contact;
        if (contact == null) {
            String emptyStr = Storage.emptyStr;
            ContactGroup group = this.defaultGroup;
            MrimContact newContact = new MrimContact(this, 0, CONTACT_FLAGS_RECEIVED, 3, senderAddress, senderName, 0, 0, emptyStr, emptyStr, emptyStr);
            group.addContact((Object) newContact);
            if (this.groups.size() > 0) {
                trySendData(XmppContactGroup.createContactCommand(this, 128, senderAddress, senderName, emptyStr, getFirstContactGroup(), false));
            }
            targetContact = newContact;
        }
        this.recvCount++;
        targetContact.receiveMessage(timestamp, formatContactName(messageAuthor).append(messageBody));
    }

    public final void receiveGroupMessage(String roomAddress, String messageBody, String roomName, String messageAuthor, ByteBuffer buffer, long timestamp) {
        MrimContact contact = findContactByIdentifier(roomAddress);
        if (contact == null) {
            return;
        }
        this.recvCount++;
        StringBuffer msgBuf = formatContactName(messageAuthor).append(messageBody);
        buffer.readInt();
        int memberCount = buffer.readInt();
        for (int i = 0; i < memberCount; i++) {
            Utils.appendCommaIf(msgBuf.append(getContactDisplayName(buffer.readWideStr())), i < memberCount - 1);
        }
        contact.receiveMessage(timestamp, msgBuf);
    }

    public final void addOfflineContact(String contactAddress) {
        if (StringUtils.equals(contactAddress, this.login) || findContactByIdentifier(contactAddress) != null) {
            return;
        }
        createNewContact(contactAddress, 16);
    }

    @Override
    public final Contact newContact(String contactAddress) {
        return createNewContact(contactAddress, 13);
    }

    private final Contact createNewContact(String contactAddress, int commandType) {
        String emptyStr = Storage.emptyStr;
        ContactGroup group = this.defaultGroup;
        MrimContact mrimContact = new MrimContact(this, 0, CONTACT_FLAGS_PENDING, 3, contactAddress, contactAddress, 0, 0, emptyStr, emptyStr, emptyStr);
        group.addContact((Object) mrimContact);
        trySendData(ProtocolFactory.createChatRoomCmd(this, contactAddress, commandType));
        return mrimContact;
    }

    @Override
    public final void onError(int errorCode) {
        int statusCode;
        switch (errorCode) {
            case 0:
                statusCode = STATUS_ONLINE;
                break;
            case 1:
                statusCode = STATUS_AWAY;
                break;
            case 2:
                statusCode = STATUS_DND;
                break;
            case 3:
                statusCode = STATUS_INVISIBLE;
                break;
            case 4:
                statusCode = STATUS_FREE_CHAT;
                break;
            default:
                disconnect();
                return;
        }
        setConfiguration(statusCode);
    }

    @Override
    public final int getHeight() {
        return 6;
    }

    @Override
    public final boolean isSelected() {
        return this.isHighlighted && this.profileManager.profile != null && this.profileManager.profile.hasCoordinates();
    }

    @Override
    public final void select() {
        this.isHighlighted = false;
    }

    @Override
    public final void deselect() {
        this.isHighlighted = true;
    }

    @Override
    public final int getWidth() {
        if (this.profileManager.profile != null) {
            return (int) this.profileManager.profile.getLongitude();
        }
        return 0;
    }

    @Override
    public final int getBaseHeight() {
        if (this.profileManager.profile != null) {
            return (int) this.profileManager.profile.getLatitude();
        }
        return 0;
    }

    @Override
    public final String getText() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (this.profileManager.profile.dirty) {
            sb.append(Storage.resources().getString(StringResKeys.STR_MRIM_AWAY_SUFFIX));
            String phone = this.profileManager.profile.phone;
            if (Utils.nonEmpty(phone)) {
                sb.append(phone).append('.').append(' ');
            }
            sb.append("Уточнить?");
        } else {
            sb.append(Storage.resources().getString(StringResKeys.STR_MRIM_OFFLINE_SUFFIX));
            if (AccountManager.getMrimAccountList().size() > 1) {
                sb.append(' ').append('(').append(this.login).append(')').append('.').append(' ');
            }
            String phone = this.profileManager.profile.phone;
            if (Utils.nonEmpty(phone)) {
                sb.append(phone).append('.').append(' ');
            }
            String genderText;
            switch (this.profileManager.profile.gender) {
                case 1:
                    genderText = Storage.state().getString(STR_GENDER_BASE);
                    break;
                case 2:
                    genderText = Storage.state().getString(STR_GENDER_BASE + 1);
                    break;
                case 3:
                    genderText = Storage.state().getString(STR_GENDER_BASE + 2);
                    break;
                case 4:
                    genderText = Storage.state().getString(STR_GENDER_BASE + 3);
                    break;
                default:
                    genderText = null;
                    break;
            }
            if (Utils.nonEmpty(genderText)) {
                sb.append(genderText).append('.');
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    @Override
    public final int getCommandCount() {
        return this.profileManager.profile.getCommandCount();
    }

    @Override
    public final boolean isHighlighted() {
        return this.profileManager.profile.hasCoordinates() && !this.profileManager.profile.dirty;
    }

    @Override
    public final int getCommandId(int index) {
        return this.profileManager.sizeCache.getWidth(index, this);
    }

    @Override
    public final int executeCommand(int index) {
        return this.profileManager.sizeCache.getHeight(index, this);
    }

    public final void performUserSearch(SearchEntry entry) {
        if (isConnected()) {
            entry.id = this.state;
            sendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_WP_REQUEST2, new ByteBuffer().writeIntLE(1).writeStringLatin1(entry.query)));
            this.searchEntryList.addElement(entry);
        }
    }

    public final void handleMailNotify(ByteBuffer buffer) {
        buffer.readInt();
        switch (buffer.readInt() & 255) {
            case MAIL_TYPE_LOGIN_OK:
                processMailData(490);
                break;
            case MAIL_TYPE_LOGIN_FAIL:
                processMailData(491);
                break;
            case 67:
            case 69:
            case 70:
            case 71:
            case 72:
            default:
                handleError(0);
                DiagnosticReporter.checkCrashReport();
                break;
            case MAIL_TYPE_AUTH_ERROR:
                processMailData(492);
                break;
            case MAIL_TYPE_COMPLETE:
                handleComplete();
                break;
        }
    }

    private final void processMailData(int errorCode) {
        EventDispatcher.postAccountError(this, errorCode);
        closeConnection();
        lastError = getDefaultError();
    }

    public final void notifyNewMail(int i, String str, String str2) {
        boolean showPopup = Storage.state().getBool(SettingsKeys.SETTING_SHOW_POPUP);
        boolean showInList = Storage.state().getBool(SettingsKeys.SETTING_SHOW_IN_LIST);
        if (showInList || showPopup) {
            if (str != null) {
                int iLastIndexOf = str.lastIndexOf(60);
                if (str.length() > 30 && iLastIndexOf > 1) {
                    StringUtils.prefix(str, iLastIndexOf - 1);
                }
                NotificationHelper.playNotificationSound(NotificationHelper.SOUND_NEW_MAIL);
            }
            if (showPopup && (AccountManager.getTotalSyncCount() != 10 || !Storage.state().hasMemory())) {
                StringBuffer sb = ObjectPool.newStringBuffer();
                if (str2 != null && str != null) {
                    EventDispatcher.postAccountNotification(this, ObjectPool.toStringAndRelease(sb.append(Storage.resources().getString(StringResKeys.STR_NEW_MAIL_FROM)).append(str).append(' ').append('\"').append(str2).append('\"').append('.').append('\n').append(new StringBuffer().append(i > 0 ? new StringBuffer().append(Storage.resources().getString(StringResKeys.STR_NEW_MAIL_COUNT)).append(i).append(Storage.resources().getString(StringResKeys.STR_NEW_MAIL_SUFFIX + Utils.pluralForm(i))).append('\n').toString() : Storage.emptyStr).append(Storage.resources().getString(StringResKeys.STR_MAIL_PREFIX)).toString())));
                } else if (i > 0) {
                    EventDispatcher.postAccountNotification(this, ObjectPool.toStringAndRelease(sb.append(Storage.resources().getString(StringResKeys.STR_NEW_MAIL_COUNT)).append(i).append(Storage.resources().getString(StringResKeys.STR_NEW_MAIL_SUFFIX + Utils.pluralForm(i))).append('\n').append(Storage.resources().getString(StringResKeys.STR_MAIL_PREFIX))));
                }
            }
            if (showInList) {
                if (i > 0 || !(str2 == null || str == null)) {
                    TimerManager.resetBacklightTimer();
                    AccountManager.clearAccountHighlight(this);
                    if (Storage.state().getBool(SettingsKeys.SETTING_SHOW_IN_LIST)) {
                        Storage.state().getVector(UIKeys.VEC_ACTIVE_CONNECTIONS).addElement(this);
                    }
                    TabBar.layout();
                }
            }
        }
    }

    ByteBuffer createMoveContactCmd(MrimContact mrimContact, int flags) {
        return createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(flags).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(mrimContact.displayName).writeStringLatin1(mrimContact.contactGroupsStr)), ObjectPool.integerOf(RESP_MOVE_FLAG), mrimContact, ObjectPool.integerOf(flags)});
    }

    ByteBuffer createAddToGroupCmd(MrimContact mrimContact, MrimContactGroup group) {
        return createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags).writeIntLE(group.serverId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(mrimContact.displayName).writeStringLatin1(mrimContact.contactGroupsStr)), ObjectPool.integerOf(RESP_MOVE_TO_GROUP), mrimContact, group});
    }

    public ByteBuffer createAuthPacket(Account targetAccount, int authParam1, int authParam2, String authName, boolean enableFlag, byte[] authData) {
        ByteBuffer payload = new ByteBuffer().writeIntWithLen(AUTH_VERSION_TAG).writeIntWithLen(AUTH_PROTOCOL_VERSION).writeIntLE(authParam1).writeIntLE(authParam2).writeStringLatin1(authName).writeIntLE(enableFlag ? 1 : 0).writeIntLE(authData.length).writeBytes(authData);
        while ((payload.length & ALIGNMENT_MASK) != 0) {
            payload.writeByte(0);
        }
        ByteBuffer buffer = new ByteBuffer();
        ByteBuffer passwordHash = hashPassword();
        XmppContactGroup.encryptBlowfish(passwordHash.data, passwordHash.length, payload.data, payload.length);
        passwordHash.clear();
        return createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_AUTH_UPDATE, buffer.writeBufferIntLen(payload)), ObjectPool.integerOf(RESP_AUTH_RESPONSE), targetAccount});
    }

    public void handleAuthResponse(int resultCode, Object[] cmdArgs, ByteBuffer buffer) {
        if (resultCode == 1) {
            buffer.readInt();
            buffer.ensureCapacity(0);
            ByteBuffer passwordHash = hashPassword();
            XmppContactGroup.decryptBlowfish(passwordHash.data, passwordHash.length, buffer.data, buffer.length);
            passwordHash.clear();
            buffer.readInt();
            MmpProtocol protocol = (MmpProtocol) cmdArgs[2];
            protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, 288, new ByteBuffer().writeShortBE(16).writeIntLE(buffer.readInt()).writeIntLE(buffer.readInt()).writeIntLE(buffer.readInt()).writeIntLE(buffer.readInt())));
        }
    }

    private ByteBuffer hashPassword() {
        return new ByteBuffer().writeRawString(this.password).encryptMD5();
    }
}
