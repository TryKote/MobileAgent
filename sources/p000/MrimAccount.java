package p000;

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
        MrimContactGroup c0010aj = new MrimContactGroup(this, -1, 102, AppState.getString(1039));
        c0010aj.isSpecial = true;
        this.defaultGroup = c0010aj;
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

    public MrimAccount(ByteBuffer c0043n) {
        super(c0043n);
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                break;
            } else {
                this.groups.addElement(new MrimContactGroup(this, c0043n));
            }
        }
        MrimContactGroup c0010aj = new MrimContactGroup(this, c0043n);
        c0010aj.isSpecial = true;
        this.defaultGroup = c0010aj;
        ByteBuffer c0043n2 = new ByteBuffer();
        int iM1328e2 = c0043n.readInt();
        if (iM1328e2 > 0) {
            c0043n2.writeBytesAt(c0043n.data, c0043n.offset, iM1328e2);
            c0043n.skip(iM1328e2);
        }
        try {
        } catch (Throwable unused) {
            this.accountNickname = null;
            this.chatRoomsList = null;
        }
        if (c0043n2.length == 0) {
            throw new RuntimeException();
        }
        this.accountNickname = c0043n2.readUTF8Str((String) null);
        c0043n2.readWideStr();
        this.chatRoomsList = NetworkUtils.newVector();
        int iM1328e3 = c0043n2.readInt();
        for (int i = 0; i < iM1328e3; i++) {
            this.chatRoomsList.addElement(ChatRoom.deserialize(c0043n2));
        }
        if (c0043n2.readShortBE() != 21554) {
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
    public final Account serializeAccount(ByteBuffer c0043n, boolean z, boolean z2) {
        super.serializeAccount(c0043n, z, z2);
        if (z2) {
            c0043n.writeBufferIntLen(serializePrivateData(z));
        } else {
            c0043n.writeIntLE(0);
        }
        return this;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final void saveProperties(ByteBuffer c0043n) {
        c0043n.writeIntLE(13).writeIntLE(this.syncSeq).writeIntLE(this.sentCount).writeIntLE(this.recvCount);
        VCard c0003ac = this.accountProfile;
        boolean zM59c = c0003ac.hasCoordinates();
        c0043n.writeBoolean(zM59c);
        if (zM59c) {
            c0043n.writeStringLatin1(c0003ac.latStr).writeStringLatin1(c0003ac.lonStr).writeStringLatin1(c0003ac.mapTypeStr).writeStringUTF16(c0003ac.phone).writeStringLatin1(c0003ac.email).writeStringLatin1(c0003ac.nickname).writeStringLatin1(c0003ac.address).writeStringLatin1(c0003ac.zoomStr).writeIntBE(c0003ac.gender).writeBoolean(c0003ac.dirty);
        }
    }

    @Override // p000.Account
    /* renamed from: a */
    public final void loadProperties(ByteBuffer c0043n) {
        int iM1328e = c0043n.readInt();
        if (iM1328e == 12) {
            this.syncSeq = c0043n.readInt();
            this.sentCount = c0043n.readInt();
            this.recvCount = c0043n.readInt();
            c0043n.readInt();
            return;
        }
        if (iM1328e == 13) {
            this.syncSeq = c0043n.readInt();
            this.sentCount = c0043n.readInt();
            this.recvCount = c0043n.readInt();
            this.accountProfile = VCard.deserializeFromBuffer(c0043n);
        }
    }

    /* renamed from: a */
    private final ByteBuffer serializePrivateData(boolean z) {
        ByteBuffer c0043n = new ByteBuffer();
        if (z) {
            try {
                int iM742V = getChatRoomCount();
                if (this.accountNickname == null || this.jabberId == null || iM742V < 3) {
                    throw new Throwable();
                }
                c0043n.ensureCapacity(20480);
                c0043n.writeStringUTF16(this.accountNickname);
                c0043n.writeIntLE(0);
                c0043n.writeIntLE(iM742V - 1);
                for (int i = 0; i < iM742V; i++) {
                    ChatRoom c0052w = (ChatRoom) this.chatRoomsList.elementAt(i);
                    if (c0052w != getLastChatRoom()) {
                        c0052w.serialize(c0043n);
                    }
                }
                c0043n.writeShortBE(21554);
            } catch (Throwable unused) {
                c0043n.clear();
            }
        }
        return c0043n;
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
        int i;
        String strM1336h;
        String str;
        int i2;
        long jM491a;
        int i3;
        String strM1336h2 = null;
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
                if (this.connection.m1131a() == 2) {
                    this.msgCount = 40;
                    this.progress = 3;
                    AppController.needsRepaint = true;
                    break;
                }
                break;
            case 3:
                this.connection.m1132a(this.dataBuffer);
                int i4 = this.dataBuffer.length;
                int i5 = i4;
                if (i4 > 0) {
                    AppController.m419a((Account) this, i5);
                    this.msgCount = 60;
                    StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
                    while (true) {
                        int i6 = i5;
                        i5 = i6 - 1;
                        if (i6 <= 0) {
                            this.connection.state = 3;
                            this.connection = new ConnectionThread(NetworkUtils.bufToStringCached(stringBufferM1217h));
                            this.progress = 4;
                            AppController.needsRepaint = true;
                            break;
                        } else {
                            char cM1344o = (char) this.dataBuffer.readByte();
                            if (Utils.m498a(cM1344o)) {
                                stringBufferM1217h.append(cM1344o);
                            }
                        }
                    }
                }
                break;
            case 4:
                if (this.connection.m1131a() == 2) {
                    this.msgCount = 80;
                    sendData(AppController.m377a(this));
                    this.progress = 5;
                    AppController.needsRepaint = true;
                    break;
                }
                break;
        }
        if (this.progress < 5) {
            return;
        }
        this.connection.m1132a(this.dataBuffer);
        while (true) {
            ByteBuffer c0043nM1349s = this.dataBuffer.extractPNG();
            if (c0043nM1349s == null) {
                if (c0043nM1349s == null && this.lastError != 0 && this.connection != null && this.connection.m1131a() == 0) {
                    closeConnection();
                    this.lastError = getDefaultError();
                }
                if (this.timeout <= 0 || !AppController.isTimerExpired(this.deadline)) {
                    return;
                }
                trySendData(AppController.createMrimPacket(this, 4102, (ByteBuffer) null));
                return;
            }
            AppController.m421a((Account) this, c0043nM1349s);
            int iM1330h = c0043nM1349s.peekIntAt(12);
            int iM1330h2 = c0043nM1349s.peekIntAt(8);
            c0043nM1349s.skip(44);
            switch (iM1330h) {
                case 4098:
                    long jM503b = Utils.min(c0043nM1349s.readInt(), AppState.getBool(1536) ? 25 : 45) * 1000;
                    this.timeout = jM503b;
                    this.deadline = System.currentTimeMillis() + jM503b;
                    ByteBuffer c0043nM1308a = new ByteBuffer().writeStringLatin1(this.login).writeStringLatin1(getFormattedName());
                    boolean zM587e = AppState.getBool(105);
                    sendData(AppController.createMrimPacket(this, 4216, c0043nM1308a.writeIntLE(zM587e ? -1 : 22).writeStringLatin1(zM587e ? null : new ByteBuffer().writeCompressed(1642077).writeExtendedInt(2229599).getStringAndClear()).writeCompressed(1704823).writeStringLatin1(XmppContactGroup.buildAuthData()).writeBuffer(XmppContactGroup.buildSyncPayload(this))));
                    this.progress = 6;
                    break;
                case 4100:
                    this.msgCount = 85;
                    incrementSync();
                    break;
                case 4101:
                    AppController.m461a(this, c0043nM1349s);
                    break;
                case 4105:
                    Conversation.handleMessage(this, c0043nM1349s, 0L);
                    break;
                case 4111:
                    int iM1328e = c0043nM1349s.readInt();
                    String strM1334g = c0043nM1349s.readWideStr();
                    c0043nM1349s.readUTF8Str((String) null);
                    c0043nM1349s.readUTF8Str((String) null);
                    MrimContact c0035fM717f = findContactByIdentifier(c0043nM1349s.readHexStr());
                    if (c0035fM717f != null && !c0035fM717f.isOnline()) {
                        c0043nM1349s.readInt();
                        String strM1334g2 = c0043nM1349s.readWideStr();
                        int i7 = c0035fM717f.unreadCount;
                        c0035fM717f.unreadCount = iM1328e;
                        c0035fM717f.statusMessage = strM1334g2;
                        c0035fM717f.defaultIcon = AppController.handleServerAction(iM1328e, strM1334g);
                        c0035fM717f.highlighted = iM1328e != 0;
                        if (iM1328e == 0) {
                            c0035fM717f.clearVCard();
                        }
                        c0035fM717f.dirty = true;
                        c0035fM717f.updateRenderState();
                        if (i7 == 0 && iM1328e != 0) {
                            ResourceManager.playNotificationSound(1);
                            break;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                case 4114:
                    XmppMailRuProtocol.handleMrimResponse(this, c0043nM1349s, iM1330h2);
                    break;
                case 4115:
                    handleTimeout();
                    break;
                case 4117:
                    while (c0043nM1349s.length > 0) {
                        String strM1334g3 = c0043nM1349s.readWideStr();
                        if (StringUtils.m3a(852768, strM1334g3)) {
                            setDisplayName(c0043nM1349s.readUTF8Str((String) null));
                        } else if (StringUtils.m3a(983853, strM1334g3)) {
                            IOUtils.notifyNewMail(this, Utils.parseInt((Object) c0043nM1349s.readUTF8Str((String) null)), (String) null, (String) null);
                        } else if (StringUtils.m3a(983868, strM1334g3)) {
                            String strM1335e = c0043nM1349s.readUTF8Str((String) null);
                            this.customDomain = StringUtils.prefix(strM1335e, strM1335e.indexOf(58));
                        } else if (StringUtils.m3a(656203, strM1334g3)) {
                            c0043nM1349s.readWideStr();
                            this.hasCustomDomain = true;
                        } else if (StringUtils.m3a(1114965, strM1334g3)) {
                            c0043nM1349s.skip((((((((c0043nM1349s.readInt() - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + (c0043nM1349s.readUTF8Str((String) null).length() << 1))) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length()));
                        } else {
                            c0043nM1349s.readWideStr();
                        }
                    }
                    break;
                case 4122:
                    XmppMailRuProtocol.handleMrimResponse(this, c0043nM1349s, iM1330h2);
                    break;
                case 4124:
                    XmppMailRuProtocol.handleMrimResponse(this, c0043nM1349s, iM1330h2);
                    break;
                case 4125:
                    trySendData(AppController.createMrimPacket(this, 4126, new ByteBuffer().writeIntLE(c0043nM1349s.readInt()).writeIntLE(c0043nM1349s.readInt())));
                    try {
                        int i8 = this.reserved1;
                        this.reserved1 = i8 + 1;
                        if (0 != i8) {
                            AppState.setInt(1449, 0);
                        }
                        Hashtable hashtable = new Hashtable();
                        String strM1214a = null;
                        String strM529d = Utils.m529d(c0043nM1349s.readWideStr(), '\r');
                        int length = strM529d.length();
                        StringBuffer stringBufferM1217h2 = NetworkUtils.newStringBuffer();
                        boolean z = false;
                        int i9 = 0;
                        while (i9 < length) {
                            char cCharAt = strM529d.charAt(i9);
                            if (!z) {
                                if (cCharAt == '\n' && stringBufferM1217h2.length() == 0) {
                                    NetworkUtils.bufToStringCached(stringBufferM1217h2);
                                    String str2 = (String) hashtable.get(AppState.getString(1379315));
                                    int i10 = str2 != null ? -1 : Integer.parseInt(str2);
                                    i = i10;
                                    strM1336h = i10 >= 0 ? null : ResourceManager.decodeBase64(StringUtils.suffix((String) hashtable.get(AppState.getString(460837)), 13)).readAllWideStr();
                                    str = (String) hashtable.get(AppState.getString(396269));
                                    i2 = Integer.parseInt((String) hashtable.get(AppState.getString(789512)), 16);
                                    jM491a = Utils.parseDateTime((String) hashtable.get(AppState.getString(264254)));
                                    i3 = 1;
                                    if ((i2 & 128) != 0) {
                                        String strM15c = StringUtils.suffix(strM529d, i9);
                                        if ((i2 & 2097160) == 0) {
                                            strM1336h2 = ResourceManager.decodeBase64(strM15c).readAllWideStr();
                                        } else {
                                            strM1336h2 = strM15c;
                                            i3 = 0;
                                        }
                                    } else {
                                        int iM627a = AppState.indexOfLong(strM529d, 57408234938722L);
                                        strM1336h2 = ResourceManager.decodeBase64(StringUtils.substring(strM529d, iM627a + 6, strM529d.indexOf(AppState.getString(134123), iM627a))).readAllWideStr();
                                    }
                                    if (i != -1 || (i >= 0 && i <= 5 && i != 1 && i != 3)) {
                                        Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(i2 | 4 | 128).writeStringLatin1((String) hashtable.get(AppState.getString(264203))).writeString(strM1336h2, i3).writeIntLE(0).writeIntLE(0).writeIntLE(i).writeStringUTF16(strM1336h).writeStringLatin1(str), jM491a);
                                    }
                                    AppState.setInt(1449, 1);
                                    break;
                                } else if (cCharAt == ':') {
                                    strM1214a = NetworkUtils.bufToString(stringBufferM1217h2, false);
                                    z = true;
                                    i9++;
                                } else {
                                    stringBufferM1217h2.append(cCharAt);
                                }
                            } else if (cCharAt == '\n') {
                                hashtable.put(strM1214a, NetworkUtils.bufToString(stringBufferM1217h2, false));
                                z = false;
                            } else {
                                stringBufferM1217h2.append(cCharAt);
                            }
                            i9++;
                        }
                        NetworkUtils.bufToStringCached(stringBufferM1217h2);
                        String str22 = (String) hashtable.get(AppState.getString(1379315));
                        int i10_2 = str22 != null ? -1 : Integer.parseInt(str22);
                        i = i10_2;
                        strM1336h = i10_2 >= 0 ? null : ResourceManager.decodeBase64(StringUtils.suffix((String) hashtable.get(AppState.getString(460837)), 13)).readAllWideStr();
                        str = (String) hashtable.get(AppState.getString(396269));
                        i2 = Integer.parseInt((String) hashtable.get(AppState.getString(789512)), 16);
                        jM491a = Utils.parseDateTime((String) hashtable.get(AppState.getString(264254)));
                        i3 = 1;
                        if ((i2 & 128) != 0) {
                        }
                        if (i != -1) {
                            Conversation.handleMessage(this, new ByteBuffer().writeIntLE(0).writeIntLE(i2 | 4 | 128).writeStringLatin1((String) hashtable.get(AppState.getString(264203))).writeString(strM1336h2, i3).writeIntLE(0).writeIntLE(0).writeIntLE(i).writeStringUTF16(strM1336h).writeStringLatin1(str), jM491a);
                            AppState.setInt(1449, 1);
                        }
                    } catch (Throwable th) {
                        AppState.setInt(1449, 1);
                        throw th;
                    }
                    break;
                case 4129:
                    MrimContact c0035fM717f2 = findContactByIdentifier(c0043nM1349s.readWideStr());
                    if (null == c0035fM717f2) {
                        break;
                    } else {
                        c0035fM717f2.hasUnreadFlag &= -2;
                        break;
                    }
                case 4133:
                    XmppMailRuProtocol.handleMrimResponse(this, c0043nM1349s, iM1330h2);
                    break;
                case 4136:
                    XmppMailRuProtocol.handleMrimResponse(this, c0043nM1349s, iM1330h2);
                    break;
                case 4151:
                    Conversation.parseContactList(this, c0043nM1349s);
                    break;
                case 4160:
                    XmppMailRuProtocol.handleMrimResponse(this, c0043nM1349s, iM1330h2);
                    break;
                case 4163:
                    if (c0043nM1349s.readInt() != 1 || c0043nM1349s.readInt() <= 0) {
                        break;
                    } else {
                        String strM1334g4 = c0043nM1349s.readWideStr();
                        int size = this.searchEntryList.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                break;
                            }
                            SearchEntry c0050u = (SearchEntry) this.searchEntryList.elementAt(size);
                            if (iM1330h2 == c0050u.id) {
                                this.searchEntryList.removeElementAt(size);
                                int i11 = c0050u.type;
                                if (i11 == 1) {
                                    sendDeleteCommand(strM1334g4);
                                    AppController.m393a(this, strM1334g4);
                                } else if (i11 == 2) {
                                    ContactInfo c0042mM1251a = ContactInfo.createForAccount(this);
                                    c0042mM1251a.setEmailAddress(strM1334g4);
                                    AppState.pool[1319] = c0042mM1251a;
                                    IOUtils.postEvent(new IOUtils(5, null));
                                }
                            }
                        }
                    }
                    break;
                case 4168:
                    IOUtils.notifyNewMail(this, c0043nM1349s.readInt(), c0043nM1349s.readUnicodeStr(), c0043nM1349s.readUnicodeStr());
                    break;
                case 4180:
                    String strM1334g5 = c0043nM1349s.readWideStr();
                    String strM1334g6 = c0043nM1349s.readWideStr();
                    if (!StringUtils.m3a(525167, strM1334g5)) {
                        break;
                    } else {
                        this.accountProfile.updatePhotos(new XmlParser(strM1334g6).parse());
                        syncProfile();
                        break;
                    }
                case 4182:
                    c0043nM1349s.readInt();
                    break;
                case 4195:
                    int iM1328e2 = c0043nM1349s.readInt();
                    String strM17c = StringUtils.intern(c0043nM1349s.readWideStr().toLowerCase());
                    long jM1341m = c0043nM1349s.readLong();
                    int iM1328e3 = c0043nM1349s.readInt();
                    String strM1335e2 = c0043nM1349s.readUTF8Str((String) null);
                    MrimContact c0035fM717f3 = findContactByIdentifier(strM17c);
                    if (c0035fM717f3 != null && !c0035fM717f3.isOnline()) {
                        if ((iM1328e2 & 2) == 0) {
                            if ((iM1328e2 & 5) != 0) {
                                if (AppState.getBool(244) && !StringUtils.equals(strM1335e2, c0035fM717f3.customNote) && ((int) (System.currentTimeMillis() / 1000)) - iM1328e3 < 172800 && c0035fM717f3.getLastSentTime() != jM1341m) {
                                    AppState.setObject(1237, (Object) c0035fM717f3.identifier);
                                    ResourceManager.playNotificationSound(6);
                                    c0035fM717f3.addFlag(2);
                                    c0035fM717f3.appendMessage(16, strM1335e2, 0L, jM1341m);
                                    ContactGroup abstractC0046qM1080g = c0035fM717f3.account.findGroup(c0035fM717f3);
                                    if (abstractC0046qM1080g != null && abstractC0046qM1080g.isSpecial) {
                                        abstractC0046qM1080g.toggleSpecial();
                                    }
                                    c0035fM717f3.updateRenderState();
                                }
                                c0035fM717f3.customNote = strM1335e2;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            c0035fM717f3.customLink = strM1335e2;
                            break;
                        }
                    } else {
                        break;
                    }
                case 4215:
                    receiveProfileData(c0043nM1349s.readWideStr(), c0043nM1349s.readBufferArray());
                    break;
                case 4229:
                    if (!AppState.hasMemory()) {
                        break;
                    } else {
                        Vector vectorM1345p = c0043nM1349s.readBufferArray();
                        if (!vectorM1345p.isEmpty()) {
                            String[] strArrM55a = VCard.parseCardFromBuffer((ByteBuffer) vectorM1345p.elementAt(0));
                            if (strArrM55a.length >= 8 && !this.accountProfile.hasCoordinates()) {
                                String str3 = strArrM55a[2];
                                if (StringUtils.m3a(525044, str3)) {
                                    setSimpleProfile(strArrM55a[1], strArrM55a[0]);
                                } else if (StringUtils.m3a(590588, str3)) {
                                    String str4 = strArrM55a[1];
                                    String str5 = strArrM55a[0];
                                    String str6 = strArrM55a[6];
                                    String str7 = strArrM55a[7];
                                    try {
                                        VCard c0003ac = this.accountProfile;
                                        String strM584b = AppState.getString(590588);
                                        String str8 = AppState.emptyStr;
                                        c0003ac.setCardData(str5, str4, strM584b, str8, str8, str8, str6, str7);
                                    } catch (Throwable unused) {
                                        this.accountProfile.clearCoordinates();
                                    }
                                    this.accountSizeCache.lastScale = -1;
                                    this.accountProfile.phone = strArrM55a[3];
                                }
                                this.accountProfile.dirty = true;
                                if (AppController.m442U() != 10) {
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
            c0043nM1349s.clear();
            AppController.needsLayoutUpdate = true;
        }
    }

    /* renamed from: a */
    public final ByteBuffer createAndQueueCommand(Object obj) {
        if (!isConnected()) {
            return null;
        }
        Object[] objArr = (Object[]) obj;
        ByteBuffer c0043n = (ByteBuffer) objArr[0];
        objArr[0] = ResourceManager.integerOf(c0043n.peekIntAt(8));
        this.extras.addElement(obj);
        return c0043n;
    }

    @Override // p000.Account
    /* renamed from: O */
    public final Vector getPendingContacts() {
        Vector vectorMo720O = super.getPendingContacts();
        Enumeration enumerationElements = this.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            MrimContact c0035f = (MrimContact) enumerationElements.nextElement();
            if (c0035f.isSystem() && !c0035f.canUnblock()) {
                vectorMo720O.addElement(c0035f);
            }
        }
        return vectorMo720O;
    }

    /* renamed from: d */
    public final int setConfiguration(int i) {
        String strM1215a;
        String strM584b;
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
                strM1215a = AppState.getString(i3);
                break;
            default:
                strM1215a = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(1226)).append(this.configFlags >> 8));
                break;
        }
        switch (this.configFlags) {
            case 1:
                strM584b = AppState.getString(642);
                break;
            case 2:
                strM584b = AppState.getString(644);
                break;
            case 3:
                strM584b = AppState.getString(642);
                break;
            case 260:
                strM584b = AppState.getString(643);
                break;
            case 516:
                strM584b = AppState.getString(645);
                break;
            default:
                strM584b = AppState.getString(151 + (this.configFlags >> 8));
                break;
        }
        return trySendData(AppController.createMrimPacket(this, 4130, new ByteBuffer().writeIntLE(i2 != 3 ? i2 : -2147483647).writeStringLatin1(strM1215a).writeStringUTF16(strM584b).writeStringUTF16(AppState.emptyStr).writeIntLE(AppState.getBool(105) ? -1 : 22)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateSend(Contact abstractC0041l, String str, long j) {
        int iMo125a = super.validateSend(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.sentCount++;
        return trySendData(XmppContactGroup.createContactAddCommand(this, (MrimContact) abstractC0041l, str, j));
    }

    /* renamed from: a */
    private int sendProfileUpdate(int i, String[] strArr, VCard c0003ac) {
        if (!c0003ac.hasCoordinates() || c0003ac.dirty) {
            return 0;
        }
        String[] strArr2 = {c0003ac.latStr, c0003ac.lonStr, c0003ac.mapTypeStr, c0003ac.phone, c0003ac.email, c0003ac.nickname, c0003ac.address, c0003ac.zoomStr};
        trySendData(AppController.createMrimPacket(this, 4213, new ByteBuffer().writeIntLE(i).writeStringArr(strArr).writeStringLatin1(AppState.getString(590694)).writeBuffer(new ByteBuffer().writeBufferIntLen(new ByteBuffer().writeStringLatin1(strArr2[0]).writeStringLatin1(strArr2[1]).writeStringLatin1(strArr2[2]).writeStringUTF16(strArr2[3]).writeStringLatin1(strArr2[4]).writeStringLatin1(strArr2[5]).writeStringLatin1(strArr2[6]).writeStringLatin1(strArr2[7])))));
        return 0;
    }

    /* renamed from: a */
    private int sendGroupRename(String[] strArr, String str) {
        return trySendData(AppController.createMrimPacket(this, 4214, new ByteBuffer().writeStringArr(strArr).writeStringLatin1(str)));
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
            ByteBuffer c0043n = (ByteBuffer) vector.elementAt(i);
            c0043n.readInt();
            if (StringUtils.m3a(590694, c0043n.readWideStr())) {
                String[] strArrM55a = VCard.parseCardFromBuffer(c0043n);
                MrimContact c0035fM717f = findContactByIdentifier(str);
                if (c0035fM717f != null) {
                    if (strArrM55a == null) {
                        c0035fM717f.clearVCard();
                    } else {
                        try {
                            c0035fM717f.vCardInfo = new VCard();
                            c0035fM717f.vCardInfo.setCardData(strArrM55a[0], strArrM55a[1], strArrM55a[2], strArrM55a[3], strArrM55a[4], strArrM55a[5], strArrM55a[6], strArrM55a[7]);
                            c0035fM717f.isSelected = true;
                        } catch (Throwable unused) {
                            c0035fM717f.clearVCard();
                        }
                        c0035fM717f.sizeCache.lastScale = -1;
                    }
                }
            }
        }
        NetworkUtils.releaseVector(vector);
    }

    /* renamed from: b */
    public final void setSimpleProfile(String str, String str2) {
        try {
            VCard c0003ac = this.accountProfile;
            String strM584b = AppState.getString(525044);
            String str3 = AppState.emptyStr;
            c0003ac.setCardData(str2, str, strM584b, str3, str3, str3, str3, str3);
        } catch (Throwable unused) {
            this.accountProfile.clearCoordinates();
        }
        this.accountSizeCache.lastScale = -1;
    }

    /* renamed from: a */
    public final void setLocationProfile(MapPoint c0014an) {
        try {
            VCard c0003ac = this.accountProfile;
            String strM810b = IOUtils.pixelToLatitude(c0014an.latitude);
            String strM809a = IOUtils.pixelToLongitude(c0014an.longitude);
            String strM584b = AppState.getString(590588);
            String strM267a = c0014an.getDisplayName();
            String str = AppState.emptyStr;
            c0003ac.setCardData(strM810b, strM809a, strM584b, strM267a, str, str, StringUtils.intern(Integer.toString(c0014an.objectCode)), StringUtils.intern(Integer.toString(c0014an.typeCode)));
        } catch (Throwable unused) {
            this.accountProfile.clearCoordinates();
        }
        this.accountSizeCache.lastScale = -1;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupRename(ContactGroup abstractC0046q, String str) {
        int iMo124a = super.validateGroupRename(abstractC0046q, str);
        if (0 != iMo124a) {
            return iMo124a;
        }
        MrimContactGroup c0010aj = (MrimContactGroup) abstractC0046q;
        return trySendData(createAndQueueCommand(new Object[]{AppController.createMrimPacket(this, 4123, new ByteBuffer().writeIntLE(c0010aj.serverId).writeIntLE(c0010aj.groupId).writeIntLE(0).writeStringUTF16(str).writeStringUTF16(str).writeIntLE(0)), ResourceManager.integerOf(1), c0010aj, str}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupCreate(String str) {
        int iMo122a = super.validateGroupCreate(str);
        if (0 != iMo122a) {
            return iMo122a;
        }
        ByteBuffer c0043n = new ByteBuffer();
        int size = (this.groups.size() << 24) | 2;
        return trySendData(createAndQueueCommand(new Object[]{AppController.createMrimPacket(this, 4121, c0043n.writeIntLE(size).writeZeros(8).writeStringUTF16(str).writeZeros(12)), ResourceManager.integerOf(4), str, ResourceManager.integerOf(size)}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupDelete(ContactGroup abstractC0046q) {
        int iMo123a = super.validateGroupDelete(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        MrimContactGroup c0010aj = (MrimContactGroup) abstractC0046q;
        ByteBuffer c0043nM1360p = new ByteBuffer().writeIntLE(c0010aj.serverId).writeIntLE(c0010aj.groupId | 1).writeIntLE(0);
        String str = c0010aj.name;
        return trySendData(createAndQueueCommand(new Object[]{AppController.createMrimPacket(this, 4123, c0043nM1360p.writeStringUTF16(str).writeStringUTF16(str).writeIntLE(0)), ResourceManager.integerOf(3), c0010aj}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateResend(Contact abstractC0041l) {
        int iMo118b = super.validateResend(abstractC0041l);
        if (0 != iMo118b) {
            return iMo118b;
        }
        if (abstractC0041l.isOnline()) {
            return removeContact(abstractC0041l, true);
        }
        MrimContact c0035f = (MrimContact) abstractC0041l;
        return trySendData(createAndQueueCommand(new Object[]{AppController.createMrimPacket(this, 4123, new ByteBuffer().writeIntLE(c0035f.contactId).writeIntLE(c0035f.statusFlags | 1).writeIntLE(c0035f.groupId).writeStringLatin1(c0035f.simpleIdentifier).writeStringUTF16(c0035f.displayName).writeStringLatin1(c0035f.contactGroupsStr)), ResourceManager.integerOf(2), c0035f}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateDelete(Contact abstractC0041l) {
        return sendDeleteCommand(((MrimContact) abstractC0041l).simpleIdentifier);
    }

    /* renamed from: g */
    public final int sendDeleteCommand(String str) {
        return trySendData(AppController.m403a(this, str, 7));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateObject(Object obj) {
        String[] strArr = (String[]) obj;
        ByteBuffer c0043n = new ByteBuffer();
        for (int i = 0; i < strArr.length; i++) {
            if (i != 9) {
                String str = strArr[i];
                if (Utils.nonEmpty(str)) {
                    c0043n.writeIntLE(i).writeString(str, (1 << i) & 28);
                }
            }
        }
        if (Utils.nonEmpty(strArr[9])) {
            c0043n.writeIntLE(9).writeStringLatin1(strArr[9]);
        }
        return trySendData(createAndQueueCommand(new Object[]{AppController.createMrimPacket(this, 4137, c0043n), ResourceManager.integerOf(8)}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateModify(Contact abstractC0041l, Object[] objArr) {
        int iMo112a = super.validateModify(abstractC0041l, objArr);
        if (0 != iMo112a) {
            return iMo112a;
        }
        String str = (String) objArr[0];
        int length = objArr.length - 1;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = Utils.m532i((String) objArr[i + 1]);
        }
        MrimContact c0035f = (MrimContact) abstractC0041l;
        if (c0035f.isOffline() && length == 0) {
            return 709;
        }
        Enumeration enumerationElements = this.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            MrimContact c0035f2 = (MrimContact) enumerationElements.nextElement();
            int i2 = length;
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                }
                if (c0035f2 != abstractC0041l && c0035f2.isInGroup(strArr[i2])) {
                    return 486;
                }
            }
        }
        String strM519a = Utils.m519a(strArr);
        return trySendData(createAndQueueCommand(new Object[]{AppController.createMrimPacket(this, 4123, new ByteBuffer().writeIntLE(c0035f.contactId).writeIntLE(c0035f.statusFlags).writeIntLE(c0035f.groupId).writeStringLatin1(c0035f.simpleIdentifier).writeStringUTF16(str).writeStringLatin1(strM519a)), ResourceManager.integerOf(0), c0035f, str, strM519a}));
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
    public final int validateGroupAdd(String str, String str2, String str3, ContactGroup abstractC0046q, boolean z) {
        int iMo734a = super.validateGroupAdd(str, str2, str3, abstractC0046q, z);
        if (0 != iMo734a) {
            return iMo734a;
        }
        MrimContact c0035fM717f = findContactByIdentifier(str);
        if (c0035fM717f == null || c0035fM717f.isOnline()) {
            trySendData(AppController.m395b(this, str));
            return trySendData(XmppContactGroup.createContactCommand(this, 0, str, str2, str3, (MrimContactGroup) abstractC0046q, z));
        }
        trySendData(ResourceManager.createAddToGroupCmd(this, c0035fM717f, (MrimContactGroup) abstractC0046q));
        return trySendData(AppController.createMrimPacket(this, 4104, new ByteBuffer().writeIntLE(z ? 524300 : 12).writeStringLatin1(str).writeStringArray(new String[]{this.displayName, str3}).writeIntLE(0)));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int validateContactDelete(Contact abstractC0041l) {
        MrimContact c0035f = (MrimContact) abstractC0041l;
        if (c0035f.isOnline()) {
            return trySendData(XmppContactGroup.createContactCommand(this, 48, c0035f.simpleIdentifier, c0035f.displayName, AppState.emptyStr, getFirstContactGroup(), false));
        }
        int i = c0035f.statusFlags;
        return trySendData(ResourceManager.createMoveContactCmd(this, c0035f, (i & 16) != 0 ? i & (-49) : i | 16 | 32));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int validateContactBlock(Contact abstractC0041l) {
        MrimContact c0035f = (MrimContact) abstractC0041l;
        int i = c0035f.statusFlags ^ 8;
        int i2 = i;
        if ((i & 8) != 0) {
            i2 &= -5;
        }
        return trySendData(ResourceManager.createMoveContactCmd(this, c0035f, i2));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int validateContactUnblock(Contact abstractC0041l) {
        MrimContact c0035f = (MrimContact) abstractC0041l;
        int i = c0035f.statusFlags ^ 4;
        int i2 = i;
        if ((i & 4) != 0) {
            i2 &= -9;
        }
        return trySendData(ResourceManager.createMoveContactCmd(this, c0035f, i2));
    }

    @Override // p000.Account
    /* renamed from: f */
    public final int validateContactResend(Contact abstractC0041l) {
        int iMo735f = super.validateContactResend(abstractC0041l);
        return 0 != iMo735f ? iMo735f : trySendData(AppController.createMrimPacket(this, 4104, new ByteBuffer().writeIntLE(1024).writeStringLatin1(((MrimContact) abstractC0041l).simpleIdentifier).writeIntLE(0).writeIntLE(0)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateMove(Contact abstractC0041l, ContactGroup abstractC0046q, ContactGroup abstractC0046q2) {
        int iMo113a = super.validateMove(abstractC0041l, abstractC0046q, abstractC0046q2);
        return 0 != iMo113a ? iMo113a : trySendData(ResourceManager.createAddToGroupCmd(this, (MrimContact) abstractC0041l, (MrimContactGroup) abstractC0046q2));
    }

    @Override // p000.Account
    /* renamed from: l */
    public final int disconnect() {
        int iMo120l = super.disconnect();
        if (0 != iMo120l) {
            return iMo120l;
        }
        trySendData(AppController.createMrimPacket(this, 4194, (ByteBuffer) null));
        closeConnection();
        this.lastError = getDefaultError();
        return 0;
    }

    /* renamed from: k */
    private final String getContactDisplayName(String str) {
        MrimContact c0035fM717f = findContactByIdentifier(str);
        return c0035fM717f != null ? c0035fM717f.displayName : str;
    }

    /* renamed from: l */
    private final StringBuffer formatContactName(String str) {
        return Utils.m496a(NetworkUtils.newStringBuffer().append(getContactDisplayName(str)), true).append('\n');
    }

    /* renamed from: a */
    public final void receivePrivateMessage(String str, String str2, String str3, String str4, long j) {
        MrimContact c0035fM717f = findContactByIdentifier(str);
        MrimContact c0035f = c0035fM717f;
        if (null == c0035fM717f) {
            String str5 = AppState.emptyStr;
            ContactGroup abstractC0046q = this.defaultGroup;
            MrimContact c0035f2 = new MrimContact(this, 0, 65664, 3, str, str3, 0, 0, str5, str5, str5);
            abstractC0046q.addContact((Object) c0035f2);
            if (this.groups.size() > 0) {
                trySendData(XmppContactGroup.createContactCommand(this, 128, str, str3, str5, getFirstContactGroup(), false));
            }
            c0035f = c0035f2;
        }
        this.recvCount++;
        c0035f.receiveMessage(j, formatContactName(str4).append(str2));
    }

    /* renamed from: a */
    public final void receiveGroupMessage(String str, String str2, String str3, String str4, ByteBuffer c0043n, long j) {
        MrimContact c0035fM717f = findContactByIdentifier(str);
        if (null == c0035fM717f) {
            return;
        }
        this.recvCount++;
        StringBuffer stringBufferAppend = formatContactName(str4).append(str2);
        c0043n.readInt();
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                c0035fM717f.receiveMessage(j, stringBufferAppend);
                return;
            }
            Utils.m496a(stringBufferAppend.append(getContactDisplayName(c0043n.readWideStr())), iM1328e != 0);
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
        ContactGroup abstractC0046q = this.defaultGroup;
        MrimContact c0035f = new MrimContact(this, 0, 65536, 3, str, str, 0, 0, str2, str2, str2);
        abstractC0046q.addContact((Object) c0035f);
        trySendData(AppController.m403a(this, str, i));
        return c0035f;
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
        Object objM475a = JsonParser.getValue(obj, AppState.getString(329785));
        for (int i = 0; i < ((Vector) objM475a).size(); i++) {
            Object objM482e = JsonParser.getVectorElement(objM475a, i);
            ChatRoom c0052wM745h = findChatRoomById(JsonParser.getIntValue(objM482e, AppState.getString(132297)));
            if (c0052wM745h == null) {
                this.chatRoomsList.addElement(new ChatRoom(objM482e));
            } else {
                c0052wM745h.parseJson(objM482e);
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
            Enumeration enumerationElements = this.chatRoomsList.elements();
            while (enumerationElements.hasMoreElements()) {
                if (((ChatRoom) enumerationElements.nextElement()).id == i) {
                    z2 = true;
                }
            }
        } while (z2);
        this.chatRoomsList.addElement(new ChatRoom(i));
    }

    /* renamed from: h */
    public final ChatRoom findChatRoomById(int i) {
        Enumeration enumerationElements = this.chatRoomsList.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
            if (c0052w.id == i) {
                return c0052w;
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
        Enumeration enumerationElements = this.chatRoomsList.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
            if (c0052w.hasMessage(str)) {
                return c0052w;
            }
        }
        return null;
    }

    /* renamed from: j */
    public final void removeUserFromChatRooms(String str) {
        int iM742V = getChatRoomCount();
        while (true) {
            iM742V--;
            if (iM742V < 0) {
                return;
            }
            ChatRoom c0052w = (ChatRoom) this.chatRoomsList.elementAt(iM742V);
            if (c0052w.hasMessage(str)) {
                c0052w.messageIds.removeElement(str);
                c0052w.readMessages.removeElement(str);
                c0052w.messages.remove(str);
                if (str.equals(c0052w.subject)) {
                    c0052w.subject = AppState.emptyStr;
                }
            }
        }
    }

    /* renamed from: X */
    public final ChatRoom findDefaultChatRoom() {
        ChatRoom c0052wM750m = findChatRoomByNameHelper(AppState.getString(897));
        return c0052wM750m != null ? c0052wM750m : findChatRoomByNameHelper(AppState.getString(892));
    }

    /* renamed from: m */
    private ChatRoom findChatRoomByNameHelper(String str) {
        Enumeration enumerationElements = this.chatRoomsList.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
            if (c0052w.name.equals(str)) {
                return c0052w;
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
        String strM584b;
        int i;
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        if (this.accountProfile.dirty) {
            stringBufferM1217h.append(AppState.getString(489));
            String str = this.accountProfile.phone;
            if (Utils.nonEmpty(str)) {
                stringBufferM1217h.append(str).append('.').append(' ');
            }
            stringBufferM1217h.append("Уточнить?");
        } else {
            stringBufferM1217h.append(AppState.getString(488));
            if (AppController.m439R().size() > 1) {
                stringBufferM1217h.append(' ').append('(').append(this.login).append(')').append('.').append(' ');
            }
            String str2 = this.accountProfile.phone;
            if (Utils.nonEmpty(str2)) {
                stringBufferM1217h.append(str2).append('.').append(' ');
            }
            switch (this.accountProfile.gender) {
                case 1:
                    i = 781;
                    strM584b = AppState.getString(i);
                    break;
                case 2:
                    i = 782;
                    strM584b = AppState.getString(i);
                    break;
                case 3:
                    i = 783;
                    strM584b = AppState.getString(i);
                    break;
                case 4:
                    i = 784;
                    strM584b = AppState.getString(i);
                    break;
                default:
                    strM584b = null;
                    break;
            }
            String str3 = strM584b;
            if (Utils.nonEmpty(strM584b)) {
                stringBufferM1217h.append(str3).append('.');
            }
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
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
    public final void performUserSearch(SearchEntry c0050u) {
        if (isConnected()) {
            c0050u.id = this.state;
            sendData(AppController.createMrimPacket(this, 4162, new ByteBuffer().writeIntLE(1).writeStringLatin1(c0050u.query)));
            this.searchEntryList.addElement(c0050u);
        }
    }
}
