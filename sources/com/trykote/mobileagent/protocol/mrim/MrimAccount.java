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

/* renamed from: ba */
/* loaded from: MobileAgent_3.9.jar:ba.class */
public final class MrimAccount extends Account implements ListItem {

    /* renamed from: a */
    public String jabberId;

    /* renamed from: b */
    public String customDomain;

    /* renamed from: c */
    public boolean hasCustomDomain;

    /* renamed from: d */
    public Vector chatRoomsList;

    /* renamed from: e */
    public boolean chatRoomsLoaded;

    /* renamed from: f */
    public String accountNickname;

    /* renamed from: g */
    public VCard accountProfile;

    /* renamed from: h */
    public boolean isHighlighted;

    /* renamed from: K */
    private SizeCache accountSizeCache;

    /* renamed from: L */
    private Vector searchEntryList;

    public MrimAccount(int i, String str, String str2) {
        super(i, str, str2);
        this.lastError = 0;
        this.configFlags = 1;
        MrimContactGroup mrimGroup = new MrimContactGroup(this, -1, 102, AppState.getString(1039));
        mrimGroup.isSpecial = true;
        this.defaultGroup = mrimGroup;
        this.accountProfile = new VCard();
        this.isHighlighted = true;
        this.accountSizeCache = new SizeCache();
        this.searchEntryList = NetworkUtils.newVector();
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int getType() {
        return 0;
    }

    public MrimAccount(ByteBuffer buffer) {
        super(buffer);
        int groupCount = buffer.readInt();
        while (true) {
            groupCount--;
            if (groupCount < 0) {
                break;
            } else {
                this.groups.addElement(new MrimContactGroup(this, buffer));
            }
        }
        MrimContactGroup mrimGroup = new MrimContactGroup(this, buffer);
        mrimGroup.isSpecial = true;
        this.defaultGroup = mrimGroup;
        ByteBuffer extraBuffer = new ByteBuffer();
        int extraLen = buffer.readInt();
        if (extraLen > 0) {
            extraBuffer.writeBytesAt(buffer.data, buffer.offset, extraLen);
            buffer.skip(extraLen);
        }
        try {
        } catch (Throwable unused) {
            this.accountNickname = null;
            this.chatRoomsList = null;
        }
        if (extraBuffer.length == 0) {
            throw new RuntimeException();
        }
        this.accountNickname = extraBuffer.readUTF8Str((String) null);
        extraBuffer.readWideStr();
        this.chatRoomsList = NetworkUtils.newVector();
        int chatRoomCount = extraBuffer.readInt();
        for (int i = 0; i < chatRoomCount; i++) {
            this.chatRoomsList.addElement(ChatRoom.deserialize(extraBuffer));
        }
        if (extraBuffer.readShortBE() != 21554) {
            throw new RuntimeException();
        }
        assignDefaultChatRoom(false);
        this.accountProfile = new VCard();
        this.isHighlighted = true;
        this.accountSizeCache = new SizeCache();
        this.searchEntryList = NetworkUtils.newVector();
    }

    @Override // p000.Account
    /* renamed from: a */
    public final Account serializeAccount(ByteBuffer buffer, boolean z, boolean z2) {
        super.serializeAccount(buffer, z, z2);
        if (z2) {
            buffer.writeBufferIntLen(serializePrivateData(z));
        } else {
            buffer.writeIntLE(0);
        }
        return this;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final void saveProperties(ByteBuffer buffer) {
        buffer.writeIntLE(13).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount);
        VCard profile = this.accountProfile;
        boolean hasCoords = profile.hasCoordinates();
        buffer.writeBoolean(hasCoords);
        if (hasCoords) {
            buffer.writeStringLatin1(profile.latStr).writeStringLatin1(profile.lonStr).writeStringLatin1(profile.mapTypeStr).writeStringUTF16(profile.phone).writeStringLatin1(profile.email).writeStringLatin1(profile.nickname).writeStringLatin1(profile.address).writeStringLatin1(profile.zoomStr).writeIntBE(profile.gender).writeBoolean(profile.dirty);
        }
    }

    @Override // p000.Account
    /* renamed from: a */
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
            this.accountProfile = VCard.deserializeFromBuffer(buffer);
        }
    }

    /* renamed from: a */
    private final ByteBuffer serializePrivateData(boolean z) {
        ByteBuffer buffer = new ByteBuffer();
        if (z) {
            try {
                int roomCount = getChatRoomCount();
                if (this.accountNickname == null || this.jabberId == null || roomCount < 3) {
                    throw new Throwable();
                }
                buffer.ensureCapacity(20480);
                buffer.writeStringUTF16(this.accountNickname);
                buffer.writeIntLE(0);
                buffer.writeIntLE(roomCount - 1);
                for (int i = 0; i < roomCount; i++) {
                    ChatRoom chatRoom = (ChatRoom) this.chatRoomsList.elementAt(i);
                    if (chatRoom != getLastChatRoom()) {
                        chatRoom.serialize(buffer);
                    }
                }
                buffer.writeShortBE(21554);
            } catch (Throwable unused) {
                buffer.clear();
            }
        }
        return buffer;
    }

    /* renamed from: f */
    public final MrimContact findContactByIdentifier(String str) {
        return (MrimContact) getContact((Object) str);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final ContactGroup createOnlineGroup() {
        return new MrimContactGroup(this, -1, 101, AppState.getString(1040));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final ContactGroup createBlockedGroup() {
        return new MrimContactGroup(this, -1, 104, AppState.getString(1042));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final ContactGroup createOfflineGroup() {
        return new MrimContactGroup(this, -1, 103, AppState.getString(1041));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final ContactGroup createSpecialGroup() {
        return new MrimContactGroup(this, -1, 105, AppState.getString(1043));
    }

    /* renamed from: f */
    public final MrimContactGroup getFirstContactGroup() {
        return (MrimContactGroup) this.groups.elementAt(0);
    }

    @Override // p000.Account
    /* renamed from: g */
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        this.extras.removeAllElements();
        return 0;
    }

    @Override // p000.Account
    /* renamed from: h */
    public final int getIconId() {
        if (this.progress >= 1 && this.progress < 100) {
            return 153;
        }
        switch (this.lastError) {
            case 0:
                return 155;
            case 1:
                return 156;
            case 2:
                return 157;
            case 3:
                return 158;
            case 260:
                return 159;
            case 516:
                return 160;
            default:
                return 157 + (this.lastError >> 8);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:107:0x0608  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x060c A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:111:0x0617  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x061b A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:115:0x067c A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:119:0x06a3 A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x06e7 A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    @Override // p000.Account
    /* renamed from: i */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void loadData() throws Throwable {
        RemoteLogger.log("MRIM", "loadData progress=" + this.progress + " msgCount=" + this.msgCount + " login=" + this.login);
        int i;
        String senderName;
        String str;
        int i2;
        long timestamp;
        int i3;
        String messageBody = null;
        switch (this.progress) {
            case 0:
                this.dataBuffer.clear();
                this.msgCount = 0;
                break;
            case 1:
                this.msgCount = 20;
                this.state = 0;
                this.connection = new ConnectionThread(AppState.getString(1114895));
                this.progress = 2;
                AppController.needsRepaint = true;
                break;
            case 2:
                this.msgCount = 30;
                if (this.connection.getState() == 2) {
                    this.msgCount = 40;
                    this.progress = 3;
                    AppController.needsRepaint = true;
                    break;
                }
                break;
            case 3:
                this.connection.drainInput(this.dataBuffer);
                int i4 = this.dataBuffer.length;
                int i5 = i4;
                if (i4 > 0) {
                    AccountManager.updateAccountStatus((Account) this, i5);
                    this.msgCount = 60;
                    StringBuffer sb = NetworkUtils.newStringBuffer();
                    while (true) {
                        int i6 = i5;
                        i5 = i6 - 1;
                        if (i6 <= 0) {
                            this.connection.state = 3;
                            this.connection = new ConnectionThread(NetworkUtils.bufToStringCached(sb));
                            this.progress = 4;
                            AppController.needsRepaint = true;
                            break;
                        } else {
                            char ch = (char) this.dataBuffer.readByte();
                            if (Utils.isDigitOrSep(ch)) {
                                sb.append(ch);
                            }
                        }
                    }
                }
                break;
            case 4:
                if (this.connection.getState() == 2) {
                    this.msgCount = 80;
                    sendData(ProtocolFactory.createMrimAuthPacket(this));
                    this.progress = 5;
                    AppController.needsRepaint = true;
                    break;
                }
                break;
        }
        if (this.progress < 5) {
            return;
        }
        this.connection.drainInput(this.dataBuffer);
        while (true) {
            ByteBuffer packet = this.dataBuffer.extractPNG();
            if (packet == null) {
                if (packet == null && this.lastError != 0 && this.connection != null && this.connection.getState() == 0) {
                    closeConnection();
                    this.lastError = getDefaultError();
                }
                if (this.timeout <= 0 || !AppController.isTimerExpired(this.deadline)) {
                    return;
                }
                trySendData(ProtocolFactory.createMrimPacket(this, 4102, (ByteBuffer) null));
                return;
            }
            AccountManager.processAccountData((Account) this, packet);
            int msgType = packet.peekIntAt(12);
            int seqId = packet.peekIntAt(8);
            packet.skip(44);
            switch (msgType) {
                case 4098:
                    long pingInterval = Utils.min(packet.readInt(), AppState.getBool(1536) ? 25 : 45) * 1000;
                    this.timeout = pingInterval;
                    this.deadline = System.currentTimeMillis() + pingInterval;
                    ByteBuffer authPacket = new ByteBuffer().writeStringLatin1(this.login).writeStringLatin1(getFormattedName());
                    boolean useExtended = AppState.getBool(105);
                    sendData(ProtocolFactory.createMrimPacket(this, 4216, authPacket.writeIntLE(useExtended ? -1 : 22).writeStringLatin1(useExtended ? null : new ByteBuffer().writeCompressed(1642077).writeExtendedInt(2229599).getStringAndClear()).writeCompressed(1704823).writeStringLatin1(XmppContactGroup.buildAuthData()).writeBuffer(XmppContactGroup.buildSyncPayload(this))));
                    this.progress = 6;
                    break;
                case 4100:
                    this.msgCount = 85;
                    incrementSync();
                    break;
                case 4101:
                    AppController.handleMrimMailNotify(this, packet);
                    break;
                case 4105:
                    Conversation.handleMessage(this, packet, 0L);
                    break;
                case 4111:
                    int statusCode = packet.readInt();
                    String statusTitle = packet.readWideStr();
                    packet.readUTF8Str((String) null);
                    packet.readUTF8Str((String) null);
                    MrimContact contact = findContactByIdentifier(packet.readHexStr());
                    if (contact != null && !contact.isOnline()) {
                        packet.readInt();
                        String statusMsg = packet.readWideStr();
                        int i7 = contact.unreadCount;
                        contact.unreadCount = statusCode;
                        contact.statusMessage = statusMsg;
                        contact.defaultIcon = AppController.handleServerAction(statusCode, statusTitle);
                        contact.highlighted = statusCode != 0;
                        if (statusCode == 0) {
                            contact.clearVCard();
                        }
                        contact.dirty = true;
                        contact.updateRenderState();
                        if (i7 == 0 && statusCode != 0) {
                            ResourceManager.playNotificationSound(1);
                            break;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                case 4114:
                    XmppMailRuProtocol.handleMrimResponse(this, packet, seqId);
                    break;
                case 4115:
                    handleTimeout();
                    break;
                case 4117:
                    while (packet.length > 0) {
                        String paramKey = packet.readWideStr();
                        if (StringUtils.matchesKey(852768, paramKey)) {
                            setDisplayName(packet.readUTF8Str((String) null));
                        } else if (StringUtils.matchesKey(983853, paramKey)) {
                            IOUtils.notifyNewMail(this, Utils.parseInt((Object) packet.readUTF8Str((String) null)), (String) null, (String) null);
                        } else if (StringUtils.matchesKey(983868, paramKey)) {
                            String domainStr = packet.readUTF8Str((String) null);
                            this.customDomain = StringUtils.prefix(domainStr, domainStr.indexOf(58));
                        } else if (StringUtils.matchesKey(656203, paramKey)) {
                            packet.readWideStr();
                            this.hasCustomDomain = true;
                        } else if (StringUtils.matchesKey(1114965, paramKey)) {
                            packet.skip((((((((packet.readInt() - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + (packet.readUTF8Str((String) null).length() << 1))) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length())) - (4 + packet.readWideStr().length()));
                        } else {
                            packet.readWideStr();
                        }
                    }
                    break;
                case 4122:
                    XmppMailRuProtocol.handleMrimResponse(this, packet, seqId);
                    break;
                case 4124:
                    XmppMailRuProtocol.handleMrimResponse(this, packet, seqId);
                    break;
                case 4125:
                    trySendData(ProtocolFactory.createMrimPacket(this, 4126, new ByteBuffer().writeIntLE(packet.readInt()).writeIntLE(packet.readInt())));
                    try {
                        int i8 = this.reserved1;
                        this.reserved1 = i8 + 1;
                        if (0 != i8) {
                            AppState.setInt(1449, 0);
                        }
                        Hashtable hashtable = new Hashtable();
                        String headerKey = null;
                        String rawText = Utils.removeChar(packet.readWideStr(), '\r');
                        int length = rawText.length();
                        StringBuffer sb2 = NetworkUtils.newStringBuffer();
                        boolean z = false;
                        int i9 = 0;
                        while (i9 < length) {
                            char ch2 = rawText.charAt(i9);
                            if (!z) {
                                if (ch2 == '\n' && sb2.length() == 0) {
                                    NetworkUtils.bufToStringCached(sb2);
                                    String str2 = (String) hashtable.get(AppState.getString(1379315));
                                    int i10 = str2 != null ? -1 : Integer.parseInt(str2);
                                    i = i10;
                                    senderName = i10 >= 0 ? null : ResourceManager.decodeBase64(StringUtils.suffix((String) hashtable.get(AppState.getString(460837)), 13)).readAllWideStr();
                                    str = (String) hashtable.get(AppState.getString(396269));
                                    i2 = Integer.parseInt((String) hashtable.get(AppState.getString(789512)), 16);
                                    timestamp = Utils.parseDateTime((String) hashtable.get(AppState.getString(264254)));
                                    i3 = 1;
                                    if ((i2 & 128) != 0) {
                                        String bodyText = StringUtils.suffix(rawText, i9);
                                        if ((i2 & 2097160) == 0) {
                                            messageBody = ResourceManager.decodeBase64(bodyText).readAllWideStr();
                                        } else {
                                            messageBody = bodyText;
                                            i3 = 0;
                                        }
                                    } else {
                                        int tagIdx = AppState.indexOfLong(rawText, 57408234938722L);
                                        messageBody = ResourceManager.decodeBase64(StringUtils.substring(rawText, tagIdx + 6, rawText.indexOf(AppState.getString(134123), tagIdx))).readAllWideStr();
                                    }
                                    if (i != -1 || (i >= 0 && i <= 5 && i != 1 && i != 3)) {
                                        Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(i2 | 4 | 128).writeStringLatin1((String) hashtable.get(AppState.getString(264203))).writeString(messageBody, i3).writeIntLE(0).writeIntLE(0).writeIntLE(i).writeStringUTF16(senderName).writeStringLatin1(str), timestamp);
                                    }
                                    AppState.setInt(1449, 1);
                                    break;
                                } else if (ch2 == ':') {
                                    headerKey = NetworkUtils.bufToString(sb2, false);
                                    z = true;
                                    i9++;
                                } else {
                                    sb2.append(ch2);
                                }
                            } else if (ch2 == '\n') {
                                hashtable.put(headerKey, NetworkUtils.bufToString(sb2, false));
                                z = false;
                            } else {
                                sb2.append(ch2);
                            }
                            i9++;
                        }
                        NetworkUtils.bufToStringCached(sb2);
                        String str22 = (String) hashtable.get(AppState.getString(1379315));
                        int i10_2 = str22 != null ? -1 : Integer.parseInt(str22);
                        i = i10_2;
                        senderName = i10_2 >= 0 ? null : ResourceManager.decodeBase64(StringUtils.suffix((String) hashtable.get(AppState.getString(460837)), 13)).readAllWideStr();
                        str = (String) hashtable.get(AppState.getString(396269));
                        i2 = Integer.parseInt((String) hashtable.get(AppState.getString(789512)), 16);
                        timestamp = Utils.parseDateTime((String) hashtable.get(AppState.getString(264254)));
                        i3 = 1;
                        if ((i2 & 128) != 0) {
                        }
                        if (i != -1) {
                            Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(i2 | 4 | 128).writeStringLatin1((String) hashtable.get(AppState.getString(264203))).writeString(messageBody, i3).writeIntLE(0).writeIntLE(0).writeIntLE(i).writeStringUTF16(senderName).writeStringLatin1(str), timestamp);
                            AppState.setInt(1449, 1);
                        }
                    } catch (Throwable th) {
                        AppState.setInt(1449, 1);
                        throw th;
                    }
                    break;
                case 4129:
                    MrimContact readContact = findContactByIdentifier(packet.readWideStr());
                    if (null == readContact) {
                        break;
                    } else {
                        readContact.hasUnreadFlag &= -2;
                        break;
                    }
                case 4133:
                    XmppMailRuProtocol.handleMrimResponse(this, packet, seqId);
                    break;
                case 4136:
                    XmppMailRuProtocol.handleMrimResponse(this, packet, seqId);
                    break;
                case 4151:
                    Conversation.parseContactList(this, packet);
                    break;
                case 4160:
                    XmppMailRuProtocol.handleMrimResponse(this, packet, seqId);
                    break;
                case 4163:
                    if (packet.readInt() != 1 || packet.readInt() <= 0) {
                        break;
                    } else {
                        String foundEmail = packet.readWideStr();
                        int size = this.searchEntryList.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                break;
                            }
                            SearchEntry entry = (SearchEntry) this.searchEntryList.elementAt(size);
                            if (seqId == entry.id) {
                                this.searchEntryList.removeElementAt(size);
                                int i11 = entry.type;
                                if (i11 == 1) {
                                    sendDeleteCommand(foundEmail);
                                    AppController.openUserProfile(this, foundEmail);
                                } else if (i11 == 2) {
                                    ContactInfo contactInfo = ContactInfo.createForAccount(this);
                                    contactInfo.setEmailAddress(foundEmail);
                                    AppState.pool[1319] = contactInfo;
                                    IOUtils.postEvent(new IOUtils(5, null));
                                }
                            }
                        }
                    }
                    break;
                case 4168:
                    IOUtils.notifyNewMail(this, packet.readInt(), packet.readUnicodeStr(), packet.readUnicodeStr());
                    break;
                case 4180:
                    String sectionKey = packet.readWideStr();
                    String xmlData = packet.readWideStr();
                    if (!StringUtils.matchesKey(525167, sectionKey)) {
                        break;
                    } else {
                        this.accountProfile.updatePhotos(new XmlParser(xmlData).parse());
                        syncProfile();
                        break;
                    }
                case 4182:
                    packet.readInt();
                    break;
                case 4195:
                    int noteFlags = packet.readInt();
                    String contactAddr = StringUtils.intern(packet.readWideStr().toLowerCase());
                    long sentTime = packet.readLong();
                    int noteTimestamp = packet.readInt();
                    String noteText = packet.readUTF8Str((String) null);
                    MrimContact noteContact = findContactByIdentifier(contactAddr);
                    if (noteContact != null && !noteContact.isOnline()) {
                        if ((noteFlags & 2) == 0) {
                            if ((noteFlags & 5) != 0) {
                                if (AppState.getBool(244) && !StringUtils.equals(noteText, noteContact.customNote) && ((int) (System.currentTimeMillis() / 1000)) - noteTimestamp < 172800 && noteContact.getLastSentTime() != sentTime) {
                                    AppState.setObject(1237, (Object) noteContact.identifier);
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
                                break;
                            } else {
                                break;
                            }
                        } else {
                            noteContact.customLink = noteText;
                            break;
                        }
                    } else {
                        break;
                    }
                case 4215:
                    receiveProfileData(packet.readWideStr(), packet.readBufferArray());
                    break;
                case 4229:
                    if (!AppState.hasMemory()) {
                        break;
                    } else {
                        Vector buffers = packet.readBufferArray();
                        if (!buffers.isEmpty()) {
                            String[] cardFields = VCard.parseCardFromBuffer((ByteBuffer) buffers.elementAt(0));
                            if (cardFields.length >= 8 && !this.accountProfile.hasCoordinates()) {
                                String str3 = cardFields[2];
                                if (StringUtils.matchesKey(525044, str3)) {
                                    setSimpleProfile(cardFields[1], cardFields[0]);
                                } else if (StringUtils.matchesKey(590588, str3)) {
                                    String str4 = cardFields[1];
                                    String str5 = cardFields[0];
                                    String str6 = cardFields[6];
                                    String str7 = cardFields[7];
                                    try {
                                        VCard profile = this.accountProfile;
                                        String typeStr = AppState.getString(590588);
                                        String str8 = AppState.emptyStr;
                                        profile.setCardData(str5, str4, typeStr, str8, str8, str8, str6, str7);
                                    } catch (Throwable unused) {
                                        this.accountProfile.clearCoordinates();
                                    }
                                    this.accountSizeCache.lastScale = -1;
                                    this.accountProfile.phone = cardFields[3];
                                }
                                this.accountProfile.dirty = true;
                                if (AccountManager.getActiveScreenId() != 10) {
                                    break;
                                } else {
                                    IOUtils.postEvent((Object) AppState.getString(786));
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
            }
            packet.clear();
            AppController.needsLayoutUpdate = true;
        }
    }

    /* renamed from: a */
    public final ByteBuffer createAndQueueCommand(Object obj) {
        if (!isConnected()) {
            return null;
        }
        Object[] objArr = (Object[]) obj;
        ByteBuffer buffer = (ByteBuffer) objArr[0];
        objArr[0] = ResourceManager.integerOf(buffer.peekIntAt(8));
        this.extras.addElement(obj);
        return buffer;
    }

    @Override // p000.Account
    /* renamed from: O */
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

    /* renamed from: d */
    public final int setConfiguration(int i) {
        String statusText;
        String typeStr;
        this.configFlags = i;
        if (!isConnected()) {
            if (isConnecting()) {
                return 487;
            }
            return connect(0);
        }
        this.lastError = i;
        int i2 = this.configFlags & 7;
        int i3 = 1225;
        switch (this.configFlags) {
            case 1:
                i3 = 1225 - 1;
            case 2:
                i3++;
            case 3:
                i3 -= 3;
            case 260:
                i3--;
            case 516:
                statusText = AppState.getString(i3);
                break;
            default:
                statusText = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(1226)).append(this.configFlags >> 8));
                break;
        }
        switch (this.configFlags) {
            case 1:
                typeStr = AppState.getString(642);
                break;
            case 2:
                typeStr = AppState.getString(644);
                break;
            case 3:
                typeStr = AppState.getString(642);
                break;
            case 260:
                typeStr = AppState.getString(643);
                break;
            case 516:
                typeStr = AppState.getString(645);
                break;
            default:
                typeStr = AppState.getString(151 + (this.configFlags >> 8));
                break;
        }
        return trySendData(ProtocolFactory.createMrimPacket(this, 4130, new ByteBuffer().writeIntLE(i2 != 3 ? i2 : -2147483647).writeStringLatin1(statusText).writeStringUTF16(typeStr).writeStringUTF16(AppState.emptyStr).writeIntLE(AppState.getBool(105) ? -1 : 22)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateSend(Contact baseContact, String str, long j) {
        int result = super.validateSend(baseContact, str, j);
        if (0 != result) {
            return result;
        }
        this.sentCount++;
        return trySendData(XmppContactGroup.createContactAddCommand(this, (MrimContact) baseContact, str, j));
    }

    /* renamed from: a */
    private int sendProfileUpdate(int i, String[] strArr, VCard profile) {
        if (!profile.hasCoordinates() || profile.dirty) {
            return 0;
        }
        String[] strArr2 = {profile.latStr, profile.lonStr, profile.mapTypeStr, profile.phone, profile.email, profile.nickname, profile.address, profile.zoomStr};
        trySendData(ProtocolFactory.createMrimPacket(this, 4213, new ByteBuffer().writeIntLE(i).writeStringArr(strArr).writeStringLatin1(AppState.getString(590694)).writeBuffer(new ByteBuffer().writeBufferIntLen(new ByteBuffer().writeStringLatin1(strArr2[0]).writeStringLatin1(strArr2[1]).writeStringLatin1(strArr2[2]).writeStringUTF16(strArr2[3]).writeStringLatin1(strArr2[4]).writeStringLatin1(strArr2[5]).writeStringLatin1(strArr2[6]).writeStringLatin1(strArr2[7])))));
        return 0;
    }

    /* renamed from: a */
    private int sendGroupRename(String[] strArr, String str) {
        return trySendData(ProtocolFactory.createMrimPacket(this, 4214, new ByteBuffer().writeStringArr(strArr).writeStringLatin1(str)));
    }

    /* renamed from: j */
    public final void syncProfile() {
        if (isConnected()) {
            int i = this.accountProfile.gender;
            if (i == 1) {
                sendProfileUpdate(1, new String[0], this.accountProfile);
            } else if (i == 2) {
                sendProfileUpdate(0, new String[0], this.accountProfile);
            } else if (i == 3) {
                sendProfileUpdate(0, this.accountProfile.photoUrls, this.accountProfile);
            }
        }
    }

    /* renamed from: k */
    public final void markProfileForPublish() {
        int i = this.accountProfile.gender;
        this.accountProfile.gender = 1;
        if (isConnected()) {
            if (i == 3) {
                sendGroupRename(this.accountProfile.photoUrls, AppState.getString(590694));
            }
            sendProfileUpdate(1, new String[0], this.accountProfile);
        }
    }

    /* renamed from: m */
    public final void markProfileForHide() {
        int i = this.accountProfile.gender;
        this.accountProfile.gender = 2;
        if (isConnected()) {
            if (i == 3) {
                sendGroupRename(this.accountProfile.photoUrls, AppState.getString(590694));
            }
            sendProfileUpdate(0, new String[0], this.accountProfile);
        }
    }

    /* renamed from: S */
    public final void clearProfileGroups() {
        int i = this.accountProfile.gender;
        this.accountProfile.gender = 4;
        if (isConnected()) {
            if (i == 3) {
                sendGroupRename(this.accountProfile.photoUrls, AppState.getString(590694));
            }
            sendGroupRename(new String[0], AppState.getString(590694));
        }
    }

    /* renamed from: T */
    public final void setProfileGroups() {
        int i = this.accountProfile.gender;
        this.accountProfile.gender = 3;
        if (isConnected()) {
            if (i == 3) {
                sendGroupRename(this.accountProfile.prevPhotoUrls, AppState.getString(590694));
            } else if (i == 1 || i == 2) {
                sendGroupRename(new String[0], AppState.getString(590694));
            }
            sendProfileUpdate(0, this.accountProfile.photoUrls, this.accountProfile);
        }
    }

    /* renamed from: a */
    public final void receiveProfileData(String str, Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            ByteBuffer buffer = (ByteBuffer) vector.elementAt(i);
            buffer.readInt();
            if (StringUtils.matchesKey(590694, buffer.readWideStr())) {
                String[] cardFields = VCard.parseCardFromBuffer(buffer);
                MrimContact contact = findContactByIdentifier(str);
                if (contact != null) {
                    if (cardFields == null) {
                        contact.clearVCard();
                    } else {
                        try {
                            contact.vCardInfo = new VCard();
                            contact.vCardInfo.setCardData(cardFields[0], cardFields[1], cardFields[2], cardFields[3], cardFields[4], cardFields[5], cardFields[6], cardFields[7]);
                            contact.isSelected = true;
                        } catch (Throwable unused) {
                            contact.clearVCard();
                        }
                        contact.sizeCache.lastScale = -1;
                    }
                }
            }
        }
        NetworkUtils.releaseVector(vector);
    }

    /* renamed from: b */
    public final void setSimpleProfile(String str, String str2) {
        try {
            VCard profile = this.accountProfile;
            String typeStr = AppState.getString(525044);
            String str3 = AppState.emptyStr;
            profile.setCardData(str2, str, typeStr, str3, str3, str3, str3, str3);
        } catch (Throwable unused) {
            this.accountProfile.clearCoordinates();
        }
        this.accountSizeCache.lastScale = -1;
    }

    /* renamed from: a */
    public final void setLocationProfile(MapPoint mapPoint) {
        try {
            VCard profile = this.accountProfile;
            String latStr = IOUtils.pixelToLatitude(mapPoint.latitude);
            String lonStr = IOUtils.pixelToLongitude(mapPoint.longitude);
            String typeStr = AppState.getString(590588);
            String pointName = mapPoint.getDisplayName();
            String str = AppState.emptyStr;
            profile.setCardData(latStr, lonStr, typeStr, pointName, str, str, StringUtils.intern(Integer.toString(mapPoint.objectCode)), StringUtils.intern(Integer.toString(mapPoint.typeCode)));
        } catch (Throwable unused) {
            this.accountProfile.clearCoordinates();
        }
        this.accountSizeCache.lastScale = -1;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupRename(ContactGroup group, String str) {
        int result = super.validateGroupRename(group, str);
        if (0 != result) {
            return result;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) group;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, 4123, new ByteBuffer().writeIntLE(mrimGroup.serverId).writeIntLE(mrimGroup.groupId).writeIntLE(0).writeStringUTF16(str).writeStringUTF16(str).writeIntLE(0)), ResourceManager.integerOf(1), mrimGroup, str}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupCreate(String str) {
        int result = super.validateGroupCreate(str);
        if (0 != result) {
            return result;
        }
        ByteBuffer buffer = new ByteBuffer();
        int size = (this.groups.size() << 24) | 2;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, 4121, buffer.writeIntLE(size).writeZeros(8).writeStringUTF16(str).writeZeros(12)), ResourceManager.integerOf(4), str, ResourceManager.integerOf(size)}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupDelete(ContactGroup group) {
        int result = super.validateGroupDelete(group);
        if (0 != result) {
            return result;
        }
        MrimContactGroup mrimGroup = (MrimContactGroup) group;
        ByteBuffer deletePacket = new ByteBuffer().writeIntLE(mrimGroup.serverId).writeIntLE(mrimGroup.groupId | 1).writeIntLE(0);
        String str = mrimGroup.name;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, 4123, deletePacket.writeStringUTF16(str).writeStringUTF16(str).writeIntLE(0)), ResourceManager.integerOf(3), mrimGroup}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateResend(Contact baseContact) {
        int result = super.validateResend(baseContact);
        if (0 != result) {
            return result;
        }
        if (baseContact.isOnline()) {
            return removeContact(baseContact, true);
        }
        MrimContact mrimContact = (MrimContact) baseContact;
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, 4123, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags | 1).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(mrimContact.displayName).writeStringLatin1(mrimContact.contactGroupsStr)), ResourceManager.integerOf(2), mrimContact}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateDelete(Contact baseContact) {
        return sendDeleteCommand(((MrimContact) baseContact).simpleIdentifier);
    }

    /* renamed from: g */
    public final int sendDeleteCommand(String str) {
        return trySendData(ProtocolFactory.createChatRoomCmd(this, str, 7));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateObject(Object obj) {
        String[] strArr = (String[]) obj;
        ByteBuffer buffer = new ByteBuffer();
        for (int i = 0; i < strArr.length; i++) {
            if (i != 9) {
                String str = strArr[i];
                if (Utils.nonEmpty(str)) {
                    buffer.writeIntLE(i).writeString(str, (1 << i) & 28);
                }
            }
        }
        if (Utils.nonEmpty(strArr[9])) {
            buffer.writeIntLE(9).writeStringLatin1(strArr[9]);
        }
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, 4137, buffer), ResourceManager.integerOf(8)}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateModify(Contact baseContact, Object[] objArr) {
        int result = super.validateModify(baseContact, objArr);
        if (0 != result) {
            return result;
        }
        String str = (String) objArr[0];
        int length = objArr.length - 1;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = Utils.extractDigits((String) objArr[i + 1]);
        }
        MrimContact mrimContact = (MrimContact) baseContact;
        if (mrimContact.isOffline() && length == 0) {
            return 709;
        }
        Enumeration elements = this.contactMap.elements();
        while (elements.hasMoreElements()) {
            MrimContact otherContact = (MrimContact) elements.nextElement();
            int i2 = length;
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                }
                if (otherContact != baseContact && otherContact.isInGroup(strArr[i2])) {
                    return 486;
                }
            }
        }
        String groupsStr = Utils.joinComma(strArr);
        return trySendData(createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(this, 4123, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(str).writeStringLatin1(groupsStr)), ResourceManager.integerOf(0), mrimContact, str, groupsStr}));
    }

    /* renamed from: U */
    public final int findAvailableGroupId() {
        int i;
        Vector vector = this.groups;
        int size = vector.size();
        for (int i2 = 0; i2 < 20; i2++) {
            for (i = 0; i <= size; i = i + 1) {
                if (i == size) {
                    return i2;
                }
                i = ((MrimContactGroup) vector.elementAt(i)).serverId != i2 ? i + 1 : 0;
            }
        }
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupAdd(String str, String str2, String str3, ContactGroup group, boolean z) {
        int result = super.validateGroupAdd(str, str2, str3, group, z);
        if (0 != result) {
            return result;
        }
        MrimContact contact = findContactByIdentifier(str);
        if (contact == null || contact.isOnline()) {
            trySendData(ProtocolFactory.createPasswordAuthCmd(this, str));
            return trySendData(XmppContactGroup.createContactCommand(this, 0, str, str2, str3, (MrimContactGroup) group, z));
        }
        trySendData(ResourceManager.createAddToGroupCmd(this, contact, (MrimContactGroup) group));
        return trySendData(ProtocolFactory.createMrimPacket(this, 4104, new ByteBuffer().writeIntLE(z ? 524300 : 12).writeStringLatin1(str).writeStringArray(new String[]{this.displayName, str3}).writeIntLE(0)));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int validateContactDelete(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        if (mrimContact.isOnline()) {
            return trySendData(XmppContactGroup.createContactCommand(this, 48, mrimContact.simpleIdentifier, mrimContact.displayName, AppState.emptyStr, getFirstContactGroup(), false));
        }
        int i = mrimContact.statusFlags;
        return trySendData(ResourceManager.createMoveContactCmd(this, mrimContact, (i & 16) != 0 ? i & (-49) : i | 16 | 32));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int validateContactBlock(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        int i = mrimContact.statusFlags ^ 8;
        int i2 = i;
        if ((i & 8) != 0) {
            i2 &= -5;
        }
        return trySendData(ResourceManager.createMoveContactCmd(this, mrimContact, i2));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int validateContactUnblock(Contact baseContact) {
        MrimContact mrimContact = (MrimContact) baseContact;
        int i = mrimContact.statusFlags ^ 4;
        int i2 = i;
        if ((i & 4) != 0) {
            i2 &= -9;
        }
        return trySendData(ResourceManager.createMoveContactCmd(this, mrimContact, i2));
    }

    @Override // p000.Account
    /* renamed from: f */
    public final int validateContactResend(Contact baseContact) {
        int result = super.validateContactResend(baseContact);
        return 0 != result ? result : trySendData(ProtocolFactory.createMrimPacket(this, 4104, new ByteBuffer().writeIntLE(1024).writeStringLatin1(((MrimContact) baseContact).simpleIdentifier).writeIntLE(0).writeIntLE(0)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateMove(Contact baseContact, ContactGroup group, ContactGroup destGroup) {
        int result = super.validateMove(baseContact, group, destGroup);
        return 0 != result ? result : trySendData(ResourceManager.createAddToGroupCmd(this, (MrimContact) baseContact, (MrimContactGroup) destGroup));
    }

    @Override // p000.Account
    /* renamed from: l */
    public final int disconnect() {
        int result = super.disconnect();
        if (0 != result) {
            return result;
        }
        trySendData(ProtocolFactory.createMrimPacket(this, 4194, (ByteBuffer) null));
        closeConnection();
        this.lastError = getDefaultError();
        return 0;
    }

    /* renamed from: k */
    private final String getContactDisplayName(String str) {
        MrimContact contact = findContactByIdentifier(str);
        return contact != null ? contact.displayName : str;
    }

    /* renamed from: l */
    private final StringBuffer formatContactName(String str) {
        return Utils.appendCommaIf(NetworkUtils.newStringBuffer().append(getContactDisplayName(str)), true).append('\n');
    }

    /* renamed from: a */
    public final void receivePrivateMessage(String str, String str2, String str3, String str4, long j) {
        MrimContact contact = findContactByIdentifier(str);
        MrimContact mrimContact = contact;
        if (null == contact) {
            String str5 = AppState.emptyStr;
            ContactGroup group = this.defaultGroup;
            MrimContact otherContact = new MrimContact(this, 0, 65664, 3, str, str3, 0, 0, str5, str5, str5);
            group.addContact((Object) otherContact);
            if (this.groups.size() > 0) {
                trySendData(XmppContactGroup.createContactCommand(this, 128, str, str3, str5, getFirstContactGroup(), false));
            }
            mrimContact = otherContact;
        }
        this.recvCount++;
        mrimContact.receiveMessage(j, formatContactName(str4).append(str2));
    }

    /* renamed from: a */
    public final void receiveGroupMessage(String str, String str2, String str3, String str4, ByteBuffer buffer, long j) {
        MrimContact contact = findContactByIdentifier(str);
        if (null == contact) {
            return;
        }
        this.recvCount++;
        StringBuffer msgBuf = formatContactName(str4).append(str2);
        buffer.readInt();
        int memberCount = buffer.readInt();
        while (true) {
            memberCount--;
            if (memberCount < 0) {
                contact.receiveMessage(j, msgBuf);
                return;
            }
            Utils.appendCommaIf(msgBuf.append(getContactDisplayName(buffer.readWideStr())), memberCount != 0);
        }
    }

    /* renamed from: h */
    public final void addOfflineContact(String str) {
        if (StringUtils.equals(str, this.login) || findContactByIdentifier(str) != null) {
            return;
        }
        createNewContact(str, 16);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final Contact newContact(String str) {
        return createNewContact(str, 13);
    }

    /* renamed from: a */
    private final Contact createNewContact(String str, int i) {
        String str2 = AppState.emptyStr;
        ContactGroup group = this.defaultGroup;
        MrimContact mrimContact = new MrimContact(this, 0, 65536, 3, str, str, 0, 0, str2, str2, str2);
        group.addContact((Object) mrimContact);
        trySendData(ProtocolFactory.createChatRoomCmd(this, str, i));
        return mrimContact;
    }

    @Override // p000.Account
    /* renamed from: c */
    public final void onError(int i) {
        int i2;
        switch (i) {
            case 0:
                i2 = 1;
                break;
            case 1:
                i2 = 260;
                break;
            case 2:
                i2 = 2;
                break;
            case 3:
                i2 = 516;
                break;
            case 4:
                i2 = 3;
                break;
            default:
                disconnect();
                return;
        }
        setConfiguration(i2);
    }

    /* renamed from: V */
    public final int getChatRoomCount() {
        if (this.chatRoomsList == null) {
            return 0;
        }
        return this.chatRoomsList.size();
    }

    /* renamed from: e */
    public final void parseChatRoomsFromJson(Object obj) {
        this.chatRoomsLoaded = false;
        boolean z = true;
        if (this.chatRoomsList == null) {
            z = false;
            this.chatRoomsList = NetworkUtils.newVector();
        }
        Object roomsArray = JsonParser.getValue(obj, AppState.getString(329785));
        for (int i = 0; i < ((Vector) roomsArray).size(); i++) {
            Object roomObj = JsonParser.getVectorElement(roomsArray, i);
            ChatRoom existingRoom = findChatRoomById(JsonParser.getIntValue(roomObj, AppState.getString(132297)));
            if (existingRoom == null) {
                this.chatRoomsList.addElement(new ChatRoom(roomObj));
            } else {
                existingRoom.parseJson(roomObj);
            }
        }
        this.accountNickname = JsonParser.getStringValue(obj, AppState.getString(526385));
        assignDefaultChatRoom(z);
    }

    /* renamed from: b */
    private void assignDefaultChatRoom(boolean z) {
        boolean z2;
        if (z) {
            return;
        }
        int i = 0;
        do {
            z2 = false;
            i++;
            Enumeration elements = this.chatRoomsList.elements();
            while (elements.hasMoreElements()) {
                if (((ChatRoom) elements.nextElement()).id == i) {
                    z2 = true;
                }
            }
        } while (z2);
        this.chatRoomsList.addElement(new ChatRoom(i));
    }

    /* renamed from: h */
    public final ChatRoom findChatRoomById(int i) {
        Enumeration elements = this.chatRoomsList.elements();
        while (elements.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) elements.nextElement();
            if (chatRoom.id == i) {
                return chatRoom;
            }
        }
        return null;
    }

    /* renamed from: W */
    public final ChatRoom getLastChatRoom() {
        return (ChatRoom) this.chatRoomsList.lastElement();
    }

    /* renamed from: i */
    public final ChatRoom findChatRoomByName(String str) {
        Enumeration elements = this.chatRoomsList.elements();
        while (elements.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) elements.nextElement();
            if (chatRoom.hasMessage(str)) {
                return chatRoom;
            }
        }
        return null;
    }

    /* renamed from: j */
    public final void removeUserFromChatRooms(String str) {
        int roomCount = getChatRoomCount();
        while (true) {
            roomCount--;
            if (roomCount < 0) {
                return;
            }
            ChatRoom chatRoom = (ChatRoom) this.chatRoomsList.elementAt(roomCount);
            if (chatRoom.hasMessage(str)) {
                chatRoom.messageIds.removeElement(str);
                chatRoom.readMessages.removeElement(str);
                chatRoom.messages.remove(str);
                if (str.equals(chatRoom.subject)) {
                    chatRoom.subject = AppState.emptyStr;
                }
            }
        }
    }

    /* renamed from: X */
    public final ChatRoom findDefaultChatRoom() {
        ChatRoom defaultRoom = findChatRoomByNameHelper(AppState.getString(897));
        return defaultRoom != null ? defaultRoom : findChatRoomByNameHelper(AppState.getString(892));
    }

    /* renamed from: m */
    private ChatRoom findChatRoomByNameHelper(String str) {
        Enumeration elements = this.chatRoomsList.elements();
        while (elements.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) elements.nextElement();
            if (chatRoom.name.equals(str)) {
                return chatRoom;
            }
        }
        return null;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return 6;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.isHighlighted && this.accountProfile != null && this.accountProfile.hasCoordinates();
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.isHighlighted = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.isHighlighted = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        if (this.accountProfile != null) {
            return (int) this.accountProfile.getLongitude();
        }
        return 0;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        if (this.accountProfile != null) {
            return (int) this.accountProfile.getLatitude();
        }
        return 0;
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        String typeStr;
        int i;
        StringBuffer sb = NetworkUtils.newStringBuffer();
        if (this.accountProfile.dirty) {
            sb.append(AppState.getString(489));
            String str = this.accountProfile.phone;
            if (Utils.nonEmpty(str)) {
                sb.append(str).append('.').append(' ');
            }
            sb.append("Уточнить?");
        } else {
            sb.append(AppState.getString(488));
            if (AccountManager.getMrimAccountList().size() > 1) {
                sb.append(' ').append('(').append(this.login).append(')').append('.').append(' ');
            }
            String str2 = this.accountProfile.phone;
            if (Utils.nonEmpty(str2)) {
                sb.append(str2).append('.').append(' ');
            }
            switch (this.accountProfile.gender) {
                case 1:
                    i = 781;
                    typeStr = AppState.getString(i);
                    break;
                case 2:
                    i = 782;
                    typeStr = AppState.getString(i);
                    break;
                case 3:
                    i = 783;
                    typeStr = AppState.getString(i);
                    break;
                case 4:
                    i = 784;
                    typeStr = AppState.getString(i);
                    break;
                default:
                    typeStr = null;
                    break;
            }
            String str3 = typeStr;
            if (Utils.nonEmpty(typeStr)) {
                sb.append(str3).append('.');
            }
        }
        return NetworkUtils.bufToStringCached(sb);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        return this.accountProfile.getCommandCount();
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return this.accountProfile.hasCoordinates() && !this.accountProfile.dirty;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.accountSizeCache.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.accountSizeCache.getHeight(i, this);
    }

    /* renamed from: a */
    public final void performUserSearch(SearchEntry entry) {
        if (isConnected()) {
            entry.id = this.state;
            sendData(ProtocolFactory.createMrimPacket(this, 4162, new ByteBuffer().writeIntLE(1).writeStringLatin1(entry.query)));
            this.searchEntryList.addElement(entry);
        }
    }
}
