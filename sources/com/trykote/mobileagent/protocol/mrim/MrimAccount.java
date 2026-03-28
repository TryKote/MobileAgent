package com.trykote.mobileagent.protocol.mrim;


import com.trykote.mobileagent.core.StateKeys;
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
        MrimContactGroup defaultGrp = new MrimContactGroup(this, -1, 102, AppState.getString(StateKeys.STR_GROUP_DEFAULT));
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
        buffer.writeIntLE(13).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount);
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
        if (version == 12) {
            this.syncSeq = buffer.readInt();
            this.sentCount = buffer.readInt();
            this.recvCount = buffer.readInt();
            buffer.readInt();
            return;
        }
        if (version == 13) {
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
                buffer.ensureCapacity(20480);
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
        return new MrimContactGroup(this, -1, 101, AppState.getString(StateKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override
    public final ContactGroup createBlockedGroup() {
        return new MrimContactGroup(this, -1, 104, AppState.getString(StateKeys.STR_GROUP_TEMPORARY));
    }

    @Override
    public final ContactGroup createOfflineGroup() {
        return new MrimContactGroup(this, -1, 103, AppState.getString(StateKeys.STR_GROUP_IGNORE));
    }

    @Override
    public final ContactGroup createSpecialGroup() {
        return new MrimContactGroup(this, -1, 105, AppState.getString(StateKeys.STR_GROUP_PHONE_CONTACTS));
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
            return 153;
        }
        switch (this.lastError) {
            case STATUS_DISCONNECTED:
                return 155;
            case STATUS_ONLINE:
                return 156;
            case STATUS_DND:
                return 157;
            case STATUS_FREE_CHAT:
                return 158;
            case STATUS_AWAY:
                return 159;
            case STATUS_INVISIBLE:
                return 160;
            default:
                return 157 + (this.lastError >> 8);
        }
    }

    private void handleConnectionProgress() throws Throwable {
        switch (this.progress) {
            case PROGRESS_DISCONNECTED:
                this.dataBuffer.clear();
                this.msgCount = 0;
                break;
            case PROGRESS_STARTING:
                this.msgCount = 20;
                this.state = 0;
                this.connection = new ConnectionThread(AppState.getString(StateKeys.STR_RES_LONG_URL_4));
                this.progress = PROGRESS_CONNECTING_REDIRECT;
                AppController.needsRepaint = true;
                break;
            case PROGRESS_CONNECTING_REDIRECT:
                this.msgCount = 30;
                if (this.connection.getState() == ConnectionThread.STATE_CONNECTED) {
                    this.msgCount = 40;
                    this.progress = PROGRESS_READING_REDIRECT;
                    AppController.needsRepaint = true;
                }
                break;
            case PROGRESS_READING_REDIRECT:
                this.connection.drainInput(this.dataBuffer);
                int dataLen = this.dataBuffer.length;
                if (dataLen > 0) {
                    AccountManager.recordInboundTraffic((Account) this, dataLen);
                    this.msgCount = 60;
                    StringBuffer sb = ObjectPool.newStringBuffer();
                    while (dataLen > 0) {
                        dataLen--;
                        char ch = (char) this.dataBuffer.readByte();
                        if (Utils.isDigitOrSep(ch)) {
                            sb.append(ch);
                        }
                    }
                    this.connection.state = ConnectionThread.STATE_CLOSING;
                    this.connection = new ConnectionThread(ObjectPool.toStringAndRelease(sb));
                    this.progress = PROGRESS_CONNECTING_MAIN;
                    AppController.needsRepaint = true;
                }
                break;
            case PROGRESS_CONNECTING_MAIN:
                if (this.connection.getState() == ConnectionThread.STATE_CONNECTED) {
                    this.msgCount = 80;
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
            int msgType = packet.peekIntAt(12);
            int seqId = packet.peekIntAt(8);
            packet.skip(44);
            switch (msgType) {
                case MrimCommand.CS_HELLO_ACK:
                    handleHelloAck(packet);
                    break;
                case MrimCommand.CS_LOGOUT:
                    this.msgCount = 85;
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
        long pingInterval = Utils.min(packet.readInt(), AppState.getBool(StateKeys.FLAG_WIFI_CONNECTION) ? 25 : 45) * 1000;
        this.timeout = pingInterval;
        this.deadline = System.currentTimeMillis() + pingInterval;
        ByteBuffer authPacket = new ByteBuffer().writeStringLatin1(this.login).writeStringLatin1(getFormattedName());
        boolean useExtended = AppState.getBool(StateKeys.SETTING_EXTENDED_STATUS);
        sendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_LOGIN2, authPacket.writeIntLE(useExtended ? -1 : 22).writeStringLatin1(useExtended ? null : new ByteBuffer().writeCompressed(PackedStringKeys.MRIM_CLIENT_VERSION).writeExtendedInt(2229599).getStringAndClear()).writeCompressed(PackedStringKeys.MRIM_GEO_LIST_PACKET).writeStringLatin1(XmppContactGroup.buildAuthData()).writeBuffer(XmppContactGroup.buildSyncPayload(this))));
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
                ResourceManager.playNotificationSound(1);
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
                AppState.setInt(StateKeys.FLAG_MRIM_DATA_LOADED, 0);
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
                        String typeCodeStr = (String) headers.get(AppState.getString(StateKeys.STR_RES_API_URL_4));
                        int typeCode = typeCodeStr != null ? -1 : Integer.parseInt(typeCodeStr);
                        messageType = typeCode;
                        senderName = typeCode >= 0 ? null : Base64.decode(StringUtils.suffix((String) headers.get(AppState.getString(StateKeys.STR_RES_DIALOG_TITLE_3)), 13)).readAllWideStr();
                        headerRef = (String) headers.get(AppState.getString(StateKeys.STR_RES_HEADER_2));
                        messageFlags = Integer.parseInt((String) headers.get(AppState.getString(StateKeys.STR_RES_LONG_LABEL_1)), 16);
                        timestamp = Utils.parseDateTime((String) headers.get(AppState.getString(StateKeys.STR_RES_SEMICOLON)));
                        encodingType = 1;
                        if ((messageFlags & 128) != 0) {
                            String bodyText = StringUtils.suffix(rawText, pos);
                            if ((messageFlags & 2097160) == 0) {
                                messageBody = Base64.decode(bodyText).readAllWideStr();
                            } else {
                                messageBody = bodyText;
                                encodingType = 0;
                            }
                        } else {
                            int tagIdx = AppState.indexOfLong(rawText, 57408234938722L);
                            messageBody = Base64.decode(StringUtils.substring(rawText, tagIdx + 6, rawText.indexOf(AppState.getString(StateKeys.STR_RES_SLASH), tagIdx))).readAllWideStr();
                        }
                        if (messageType != -1 || (messageType >= 0 && messageType <= 5 && messageType != 1 && messageType != 3)) {
                            Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(messageFlags | 4 | 128).writeStringLatin1((String) headers.get(AppState.getString(StateKeys.STR_RES_OPEN_TAG))).writeString(messageBody, encodingType).writeIntLE(0).writeIntLE(0).writeIntLE(messageType).writeStringUTF16(senderName).writeStringLatin1(headerRef), timestamp);
                        }
                        AppState.setInt(StateKeys.FLAG_MRIM_DATA_LOADED, 1);
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
            String typeCodeStr = (String) headers.get(AppState.getString(StateKeys.STR_RES_API_URL_4));
            int typeCode = typeCodeStr != null ? -1 : Integer.parseInt(typeCodeStr);
            messageType = typeCode;
            senderName = typeCode >= 0 ? null : Base64.decode(StringUtils.suffix((String) headers.get(AppState.getString(StateKeys.STR_RES_DIALOG_TITLE_3)), 13)).readAllWideStr();
            headerRef = (String) headers.get(AppState.getString(StateKeys.STR_RES_HEADER_2));
            messageFlags = Integer.parseInt((String) headers.get(AppState.getString(StateKeys.STR_RES_LONG_LABEL_1)), 16);
            timestamp = Utils.parseDateTime((String) headers.get(AppState.getString(StateKeys.STR_RES_SEMICOLON)));
            encodingType = 1;
            if ((messageFlags & 128) != 0) {
            }
            if (messageType != -1) {
                Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(messageFlags | 4 | 128).writeStringLatin1((String) headers.get(AppState.getString(StateKeys.STR_RES_OPEN_TAG))).writeString(messageBody, encodingType).writeIntLE(0).writeIntLE(0).writeIntLE(messageType).writeStringUTF16(senderName).writeStringLatin1(headerRef), timestamp);
                AppState.setInt(StateKeys.FLAG_MRIM_DATA_LOADED, 1);
            }
        } catch (Throwable th) {
            AppState.setInt(StateKeys.FLAG_MRIM_DATA_LOADED, 1);
            throw th;
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
                    AppState.pool[StateKeys.SLOT_CONTACT_INFO] = contactInfo;
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
            if (AppState.getBool(StateKeys.SETTING_CUSTOM_NOTE_ENABLED) && !StringUtils.equals(noteText, noteContact.customNote) && ((int) (System.currentTimeMillis() / 1000)) - noteTimestamp < 172800 && noteContact.getLastSentTime() != sentTime) {
                AppState.setObject(StateKeys.SLOT_CURRENT_CONTACT_ID, (Object) noteContact.identifier);
                ResourceManager.playNotificationSound(6);
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
        if (!AppState.hasMemory()) {
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
                String typeStr = AppState.getString(StateKeys.STR_RES_HTTP_METHOD);
                String empty = AppState.emptyStr;
                profile.setCardData(cardFields[0], cardFields[1], typeStr, empty, empty, empty, cardFields[6], cardFields[7]);
            } catch (Throwable unused) {
                this.profileManager.profile.clearCoordinates();
            }
            this.profileManager.sizeCache.lastScale = -1;
            this.profileManager.profile.phone = cardFields[3];
        }
        this.profileManager.profile.dirty = true;
        if (AccountManager.getTotalSyncCount() == 10) {
            EventDispatcher.postNotification(AppState.getString(StateKeys.STR_MRIM_DISCONNECT));
        }
    }

    public final ByteBuffer createAndQueueCommand(Object commandData) {
        if (!isConnected()) {
            return null;
        }
        Object[] tuple = (Object[]) commandData;
        ByteBuffer buffer = (ByteBuffer) tuple[0];
        tuple[0] = ResourceManager.integerOf(buffer.peekIntAt(8));
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
                statusTextId = 1221;
                break;
            case STATUS_DND:
                statusTextId = 1222;
                break;
            case STATUS_FREE_CHAT:
                statusTextId = 1221;
                break;
            case STATUS_AWAY:
                statusTextId = 1224;
                break;
            case STATUS_INVISIBLE:
                statusTextId = 1225;
                break;
            default:
                statusTextId = -1;
                break;
        }
        if (statusTextId >= 0) {
            statusText = AppState.getString(statusTextId);
        } else {
            statusText = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_CONFIG_STATUS_PREFIX)).append(this.configFlags >> 8));
        }
        switch (this.configFlags) {
            case STATUS_ONLINE:
                typeStr = AppState.getString(StateKeys.STR_STATUS_ONLINE);
                break;
            case STATUS_DND:
                typeStr = AppState.getString(StateKeys.STR_STATUS_DND);
                break;
            case STATUS_FREE_CHAT:
                typeStr = AppState.getString(StateKeys.STR_STATUS_ONLINE);
                break;
            case STATUS_AWAY:
                typeStr = AppState.getString(StateKeys.STR_STATUS_AWAY);
                break;
            case STATUS_INVISIBLE:
                typeStr = AppState.getString(StateKeys.STR_STATUS_INVISIBLE);
                break;
            default:
                typeStr = AppState.getString(StateKeys.STR_CONFIG_TYPE_BASE + (this.configFlags >> 8));
                break;
        }
        return trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_CHANGE_STATUS, new ByteBuffer().writeIntLE(rawStatus != 3 ? rawStatus : -2147483647).writeStringLatin1(statusText).writeStringUTF16(typeStr).writeStringUTF16(AppState.emptyStr).writeIntLE(AppState.getBool(StateKeys.SETTING_EXTENDED_STATUS) ? -1 : 22)));
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
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimGroup.serverId).writeIntLE(mrimGroup.groupId).writeIntLE(0).writeStringUTF16(newName).writeStringUTF16(newName).writeIntLE(0)), ResourceManager.integerOf(RESP_RENAME_GROUP), mrimGroup, newName}));
    }

    @Override
    public final int validateGroupCreate(String groupName) {
        int result = super.validateGroupCreate(groupName);
        if (result != 0) {
            return result;
        }
        ByteBuffer buffer = new ByteBuffer();
        int size = (this.groups.size() << 24) | 2;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_ADD_CONTACT, buffer.writeIntLE(size).writeZeros(8).writeStringUTF16(groupName).writeZeros(12)), ResourceManager.integerOf(RESP_ADD_GROUP), groupName, ResourceManager.integerOf(size)}));
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
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, deletePacket.writeStringUTF16(name).writeStringUTF16(name).writeIntLE(0)), ResourceManager.integerOf(RESP_DELETE_GROUP), mrimGroup}));
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
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags | 1).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(mrimContact.displayName).writeStringLatin1(mrimContact.contactGroupsStr)), ResourceManager.integerOf(RESP_DELETE_CONTACT), mrimContact}));
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
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_WP_REQUEST, buffer), ResourceManager.integerOf(RESP_CONTACT_INFO)}));
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
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(displayName).writeStringLatin1(groupsStr)), ResourceManager.integerOf(RESP_MODIFY_CONTACT), mrimContact, displayName, groupsStr}));
    }

    public final int findAvailableGroupId() {
        int idx;
        int size = this.groups.size();
        for (int candidateId = 0; candidateId < 20; candidateId++) {
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
        trySendData(ResourceManager.createAddToGroupCmd(this, contact, (MrimContactGroup) group));
        return trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(requestAuth ? 524300 : 12).writeStringLatin1(contactAddress).writeStringArray(new String[]{this.displayName, authMessage}).writeIntLE(0)));
    }

    @Override
    public final int validateContactDelete(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        if (mrimContact.isOnline()) {
            return trySendData(XmppContactGroup.createContactCommand(this, 48, mrimContact.simpleIdentifier, mrimContact.displayName, AppState.emptyStr, getFirstContactGroup(), false));
        }
        int flags = mrimContact.statusFlags;
        return trySendData(ResourceManager.createMoveContactCmd(this, mrimContact, (flags & 16) != 0 ? flags & (-49) : flags | 16 | 32));
    }

    @Override
    public final int validateContactBlock(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        int flags = mrimContact.statusFlags ^ 8;
        int newFlags = flags;
        if ((flags & 8) != 0) {
            newFlags &= -5;
        }
        return trySendData(ResourceManager.createMoveContactCmd(this, mrimContact, newFlags));
    }

    @Override
    public final int validateContactUnblock(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        int flags = mrimContact.statusFlags ^ 4;
        int newFlags = flags;
        if ((flags & 4) != 0) {
            newFlags &= -9;
        }
        return trySendData(ResourceManager.createMoveContactCmd(this, mrimContact, newFlags));
    }

    @Override
    public final int validateContactResend(Contact baseContact) {
        int result = super.validateContactResend(baseContact);
        return result != 0 ? result : trySendData(ProtocolFactory.createMrimPacket(this, MrimCommand.CS_MESSAGE, new ByteBuffer().writeIntLE(1024).writeStringLatin1(((MrimContact) baseContact).simpleIdentifier).writeIntLE(0).writeIntLE(0)));
    }

    @Override
    public final int validateMove(Contact baseContact, ContactGroup group, ContactGroup destGroup) {
        int result = super.validateMove(baseContact, group, destGroup);
        return result != 0 ? result : trySendData(ResourceManager.createAddToGroupCmd(this, (MrimContact) baseContact, (MrimContactGroup) destGroup));
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
            String emptyStr = AppState.emptyStr;
            ContactGroup group = this.defaultGroup;
            MrimContact newContact = new MrimContact(this, 0, 65664, 3, senderAddress, senderName, 0, 0, emptyStr, emptyStr, emptyStr);
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
        String emptyStr = AppState.emptyStr;
        ContactGroup group = this.defaultGroup;
        MrimContact mrimContact = new MrimContact(this, 0, 65536, 3, contactAddress, contactAddress, 0, 0, emptyStr, emptyStr, emptyStr);
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
            sb.append(AppState.getString(StateKeys.STR_MRIM_AWAY_SUFFIX));
            String phone = this.profileManager.profile.phone;
            if (Utils.nonEmpty(phone)) {
                sb.append(phone).append('.').append(' ');
            }
            sb.append("Уточнить?");
        } else {
            sb.append(AppState.getString(StateKeys.STR_MRIM_OFFLINE_SUFFIX));
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
                    genderText = AppState.getString(781);
                    break;
                case 2:
                    genderText = AppState.getString(782);
                    break;
                case 3:
                    genderText = AppState.getString(783);
                    break;
                case 4:
                    genderText = AppState.getString(784);
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
            case 65:
                processMailData(490);
                break;
            case 66:
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
            case 68:
                processMailData(492);
                break;
            case 73:
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
        boolean showPopup = AppState.getBool(StateKeys.SETTING_SHOW_POPUP);
        boolean showInList = AppState.getBool(StateKeys.SETTING_SHOW_IN_LIST);
        if (showInList || showPopup) {
            if (str != null) {
                int iLastIndexOf = str.lastIndexOf(60);
                if (str.length() > 30 && iLastIndexOf > 1) {
                    StringUtils.prefix(str, iLastIndexOf - 1);
                }
                ResourceManager.playNotificationSound(0);
            }
            if (showPopup && (AccountManager.getTotalSyncCount() != 10 || !AppState.hasMemory())) {
                StringBuffer sb = ObjectPool.newStringBuffer();
                if (str2 != null && str != null) {
                    EventDispatcher.postAccountNotification(this, ObjectPool.toStringAndRelease(sb.append(AppState.getString(StateKeys.STR_NEW_MAIL_FROM)).append(str).append(' ').append('\"').append(str2).append('\"').append('.').append('\n').append(new StringBuffer().append(i > 0 ? new StringBuffer().append(AppState.getString(StateKeys.STR_NEW_MAIL_COUNT)).append(i).append(AppState.getString(StateKeys.STR_NEW_MAIL_SUFFIX + Utils.pluralForm(i))).append('\n').toString() : AppState.emptyStr).append(AppState.getString(StateKeys.STR_MAIL_PREFIX)).toString())));
                } else if (i > 0) {
                    EventDispatcher.postAccountNotification(this, ObjectPool.toStringAndRelease(sb.append(AppState.getString(StateKeys.STR_NEW_MAIL_COUNT)).append(i).append(AppState.getString(StateKeys.STR_NEW_MAIL_SUFFIX + Utils.pluralForm(i))).append('\n').append(AppState.getString(StateKeys.STR_MAIL_PREFIX))));
                }
            }
            if (showInList) {
                if (i > 0 || !(str2 == null || str == null)) {
                    TimerManager.resetBacklightTimer();
                    AccountManager.clearAccountHighlight(this);
                    if (AppState.getBool(StateKeys.SETTING_SHOW_IN_LIST)) {
                        AppState.getVector(StateKeys.VEC_ACTIVE_CONNECTIONS).addElement(this);
                    }
                    TabBar.layout();
                }
            }
        }
    }

}
