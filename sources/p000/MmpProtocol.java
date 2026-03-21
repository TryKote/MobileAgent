package p000;

import java.util.Hashtable;
import java.util.Vector;

/* renamed from: d */
/* loaded from: MobileAgent_3.9.jar:d.class */
public final class MmpProtocol extends Account {

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
        this.lastError = -1;
        this.configFlags = 0;
        this.protocolVersion = 4;
        MmpContactGroup c0016ap = new MmpContactGroup(this, 0, AppState.getString(1039));
        c0016ap.isSpecial = true;
        this.defaultGroup = c0016ap;
        this.contactsByIdMap = new Hashtable();
        this.contactGroupsMap = new Hashtable();
        this.additionalDataMap = new Hashtable();
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int getType() {
        return 1;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int setCredentials(String str, String str2) {
        int iMo102a = super.setCredentials(str, str2);
        if (iMo102a != 0) {
            return iMo102a;
        }
        return 0;
    }

    public MmpProtocol(ByteBuffer c0043n) {
        super(c0043n);
        this.protocolVersion = this.configFlags >>> 16;
        if (this.protocolVersion <= 0 || this.protocolVersion > 5) {
            this.protocolVersion = 4;
        }
        this.configFlags &= 65535;
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                MmpContactGroup c0016ap = new MmpContactGroup(this, c0043n);
                c0016ap.isSpecial = true;
                this.defaultGroup = c0016ap;
                this.contactsByIdMap = new Hashtable();
                this.contactGroupsMap = new Hashtable();
                this.additionalDataMap = new Hashtable();
                return;
            }
            addGroup((ContactGroup) new MmpContactGroup(this, c0043n));
        }
    }

    @Override // p000.Account
    /* renamed from: a */
    public final Account serializeAccount(ByteBuffer c0043n, boolean z, boolean z2) {
        this.configFlags = (this.configFlags & 65535) + (this.protocolVersion << 16);
        return super.serializeAccount(c0043n, z, z2);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final ContactGroup createOnlineGroup() {
        return new MmpContactGroup(this, -1, AppState.getString(1040));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final ContactGroup createBlockedGroup() {
        return new MmpContactGroup(this, -2, AppState.getString(1042));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final ContactGroup createOfflineGroup() {
        return new MmpContactGroup(this, -3, AppState.getString(1041));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final ContactGroup createSpecialGroup() {
        return new MmpContactGroup(this, -4, AppState.getString(1043));
    }

    @Override // p000.Account
    /* renamed from: a_ */
    public final int connect(int i) {
        int iMo914a_ = super.connect(i);
        if (iMo914a_ != 0) {
            return iMo914a_;
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
        return -1;
    }

    @Override // p000.Account
    /* renamed from: h */
    public final int getIconId() {
        if (this.progress >= 1 && this.progress < 100) {
            return 265;
        }
        int iM915f = getIconResourceId();
        switch (this.lastError) {
            case -1:
                return 255;
            case 1:
                return 16318464 | iM915f;
            case 2:
                return 16449536 | iM915f;
            case 4:
                return 16580608 | iM915f;
            case 16:
                return 16646144 | iM915f;
            case 32:
                return 16384000 | iM915f;
            case 256:
                return 16515072 | iM915f;
            case 8193:
                return 17104896 | iM915f;
            case 12288:
                return 16842752 | iM915f;
            case 16384:
                return 16908288 | iM915f;
            case 20480:
                return 16973824 | iM915f;
            case 24576:
                return 17039360 | iM915f;
            default:
                return iM915f;
        }
    }

    /* renamed from: a */
    public final ByteBuffer queueCommand(Object obj) {
        if (!isConnecting()) {
            return null;
        }
        Object[] objArr = (Object[]) obj;
        ByteBuffer c0043n = (ByteBuffer) objArr[0];
        objArr[0] = ResourceManager.integerOf(c0043n.readIntBEAt());
        this.extras.addElement(obj);
        return c0043n;
    }

    @Override // p000.Account
    /* renamed from: i */
    public final void loadData() throws Throwable {
        Contact abstractC0041lM1069c;
        if (this.progress <= 0) {
            closeConnection();
            this.lastError = -1;
        }
        switch (this.progress) {
            case 0:
                this.dataBuffer.clear();
                if (this.connectionData != null) {
                    this.connectionData[0] = null;
                }
                this.connectionData = null;
                this.msgCount = 0;
                break;
            case 1:
                AppController.needsRepaint = true;
                this.msgCount = 10;
                this.networkResourceMode = ResourceManager.checkForUpdates();
                if (this.networkResourceMode != -1 || this.networkResourceMode == 1) {
                    this.progress = 2;
                    break;
                }
                break;
            case 2:
                if (this.networkResourceMode != 0) {
                    Vector vectorM439R = AppController.m439R();
                    int iM541c = Utils.m541c(vectorM439R);
                    while (true) {
                        iM541c--;
                        if (iM541c >= 0) {
                            Account abstractC0037h = (Account) vectorM439R.elementAt(iM541c);
                            if (abstractC0037h.isConnected()) {
                                this.progress = 3;
                            } else if (!abstractC0037h.isConnecting()) {
                                vectorM439R.removeElementAt(iM541c);
                            }
                        } else {
                            break;
                        }
                    }
                    if (Utils.m541c(vectorM439R) == 0) {
                        IOUtils.postEvent((Object) AppState.getString(479));
                        this.progress = 0;
                    }
                    NetworkUtils.releaseVector(vectorM439R);
                    break;
                } else {
                    this.progress = 3;
                    break;
                }
            case 3:
                new AsyncTask(31, new Object[]{this, ResourceManager.integerOf(0), this.login, getFormattedName()});
                this.msgCount = 20;
                this.progress = 5;
                AppController.needsRepaint = true;
                break;
            case 5:
                if (this.connectionData != null) {
                    this.msgCount = 70;
                    AppController.needsRepaint = true;
                    this.state = 28179;
                    this.encryptionKey = ResourceManager.decodeBase64(this.connectionData[2]).toByteArray();
                    this.serverId = Integer.parseInt(this.connectionData[0]);
                    this.connection = new ConnectionThread(this.connectionData[1]);
                    this.progress = 6;
                    this.connectionData = null;
                    break;
                }
                break;
            case 6:
                if (this.connection.m1131a() != 2) {
                    if (this.connection.m1131a() <= 0) {
                        closeConnection();
                        this.lastError = getDefaultError();
                        break;
                    }
                } else {
                    this.progress = 7;
                    break;
                }
                break;
            case 7:
                this.connection.m1132a(this.dataBuffer);
                ByteBuffer c0043nM1350t = this.dataBuffer.extractJPEG();
                if (c0043nM1350t != null) {
                    AppController.needsRepaint = true;
                    this.msgCount = 85;
                    AppController.m421a((Account) this, c0043nM1350t);
                    if (c0043nM1350t.peekByteAt(1) == 1) {
                        long j = AppState.getBool(1536) ? 25000L : 60000L;
                        this.timeout = j;
                        this.deadline = System.currentTimeMillis() + j;
                        incrementSync();
                        byte[] bArr = this.encryptionKey;
                        sendData(AppController.createPingPacket(this, 1).writeIntBE(1).writeShortBE(6).writeShortBE(bArr.length).writeBytes(bArr).updateLength());
                        this.encryptionKey = null;
                        sendData(AppController.m375a(this));
                        sendData(AppController.m464a(this, 1026, new ByteBuffer().writeCompressed(1051079)));
                        sendData(queueCommand(new Object[]{AppController.m464a(this, 286, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(268435456 | getConnectionModeValue()).writeCompressed(2689260)), ResourceManager.integerOf(17)}));
                        this.contactListIndex = 0;
                        sendData(queueCommand(new Object[]{AppController.m464a(this, 4868, (ByteBuffer) null), ResourceManager.integerOf(6)}));
                        sendData(StringUtils.m18a(this, this.serverId));
                        this.progress = 8;
                        break;
                    }
                }
                break;
        }
        if (this.progress < 8) {
            return;
        }
        this.connection.m1132a(this.dataBuffer);
        if (this.networkResourceMode == 1) {
            Vector vectorM440S = AppController.m440S();
            if (Utils.m541c(vectorM440S) == 0) {
                closeConnection();
                this.lastError = getDefaultError();
                return;
            }
            NetworkUtils.releaseVector(vectorM440S);
        }
        while (true) {
            ByteBuffer c0043nM1350t2 = this.dataBuffer.extractJPEG();
            ByteBuffer c0043nM1299a = c0043nM1350t2;
            if (c0043nM1350t2 == null) {
                if (this.lastError != -1 && this.connection != null && this.connection.m1131a() == 0) {
                    closeConnection();
                    this.lastError = getDefaultError();
                }
                if (this.timeout > 0 && isConnected() && AppController.isTimerExpired(this.deadline)) {
                    trySendData(AppController.createPingPacket(this, 5));
                    return;
                }
                return;
            }
            AppController.m421a((Account) this, c0043nM1299a);
            this.msgCount = 90;
            if (c0043nM1299a.peekByteAt(1) == 2) {
                int iM1331i = (c0043nM1299a.peekByteAt(6) << 24) | (c0043nM1299a.peekByteAt(8) << 16) | (c0043nM1299a.peekByteAt(7) << 8) | c0043nM1299a.peekByteAt(9);
                int iM1356x = c0043nM1299a.readIntBEAt();
                int iM1351l = c0043nM1299a.peekShortBE(10);
                int iM1351l2 = c0043nM1299a.peekShortBE(10);
                c0043nM1299a.skip(16);
                if (iM1351l2 == 32768) {
                    c0043nM1299a.skip(c0043nM1299a.readShortBE());
                }
                c0043nM1299a = c0043nM1299a.compact();
                switch (iM1331i) {
                    case 271:
                        Vector vector = this.extras;
                        int size = vector.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                XmppMailRuProtocol.handleMmpResponse(this, c0043nM1299a, iM1356x, 0);
                                break;
                            } else {
                                Object[] objArr = (Object[]) vector.elementAt(size);
                                if (((Integer) objArr[1]).intValue() == 17) {
                                    objArr[0] = ResourceManager.integerOf(iM1356x);
                                }
                            }
                        }
                    case 287:
                        try {
                            int iM1355w = c0043nM1299a.readIntBE();
                            int iM1355w2 = c0043nM1299a.readIntBE();
                            String strM17c = AppState.emptyStr;
                            boolean z = false;
                            byte[] bArr2 = AppState.emptyBytes;
                            while (c0043nM1299a.length > 0) {
                                int iM1353u = c0043nM1299a.readShortBE();
                                int iM1353u2 = c0043nM1299a.readShortBE();
                                byte[] bArr3 = iM1353u2 > 0 ? new byte[iM1353u2] : null;
                                byte[] bArr4 = bArr3;
                                if (bArr3 != null) {
                                    c0043nM1299a.readIntoBytes(bArr4);
                                }
                                if (iM1353u == 1) {
                                    strM17c = StringUtils.intern(new String(bArr4));
                                } else if (iM1353u == 2) {
                                    z = true;
                                } else if (iM1353u == 3) {
                                    bArr2 = bArr4;
                                }
                                if (iM1353u != 3) {
                                    NetworkUtils.releaseBytes(bArr4);
                                }
                            }
                            MrimAccount c0028ba = (MrimAccount) AppController.m440S().elementAt(0);
                            c0028ba.trySendData(ResourceManager.createAuthPacket(c0028ba, this, iM1355w, iM1355w2, strM17c, z, bArr2));
                            break;
                        } catch (Throwable unused) {
                            break;
                        }
                    case 779:
                        parseContactStatus(c0043nM1299a.readLenPrefixStr(), c0043nM1299a);
                        break;
                    case 780:
                        parseContactStatus(c0043nM1299a.readLenPrefixStr(), c0043nM1299a);
                        break;
                    case 1025:
                        XmppMailRuProtocol.removeQueuedCommand(this, iM1356x);
                        break;
                    case 1031:
                        IOUtils.handleFileTransfer(this, c0043nM1299a);
                        break;
                    case 1035:
                        long jM1341m = c0043nM1299a.readLong();
                        c0043nM1299a.readShortBE();
                        updateStatus(c0043nM1299a.readLenPrefixStr(), jM1341m, 64);
                        break;
                    case 1036:
                        long jM1341m2 = c0043nM1299a.readLong();
                        c0043nM1299a.readShortBE();
                        updateStatus(c0043nM1299a.readLenPrefixStr(), jM1341m2, 128);
                        break;
                    case 1044:
                        c0043nM1299a.skip(10);
                        String strM1363z = c0043nM1299a.readLenPrefixStr();
                        if (c0043nM1299a.readShortBE() != 0) {
                            deleteContact(strM1363z);
                            break;
                        } else {
                            markRead(strM1363z);
                            break;
                        }
                    case 4870:
                        if (this.contactListIndex == 0) {
                            removeAllContacts();
                        }
                        XmppMailRuProtocol.handleMmpResponse(this, c0043nM1299a, iM1356x, iM1351l);
                        break;
                    case 4878:
                        XmppMailRuProtocol.handleMmpResponse(this, c0043nM1299a, iM1356x, 0);
                        break;
                    case 4885:
                        Contact abstractC0041lM1069c2 = getContact((Object) c0043nM1299a.readLenPrefixStr());
                        if (null != abstractC0041lM1069c2) {
                            abstractC0041lM1069c2.performAction();
                            break;
                        }
                        break;
                    case 4889:
                        ResourceManager.playNotificationSound(3);
                        onMessage(c0043nM1299a.readLenPrefixStr(), 0L, c0043nM1299a.readVarLenStr());
                        break;
                    case 4891:
                        String strM1363z2 = c0043nM1299a.readLenPrefixStr();
                        byte bM1344o = c0043nM1299a.readByte();
                        onMessage(strM1363z2, 0L, NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(483)).append(AppState.getString(bM1344o == 1 ? 484 : 485)).append(c0043nM1299a.readVarLenStr())));
                        if (bM1344o == 1 && null != (abstractC0041lM1069c = getContact((Object) strM1363z2))) {
                            abstractC0041lM1069c.performAction();
                            break;
                        }
                        break;
                    case 4892:
                        onMessage(c0043nM1299a.readLenPrefixStr(), 0L, AppState.getString(480));
                        break;
                    case 5377:
                        IOUtils.postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(481)).append(1501).append('/').append(c0043nM1299a.readShortBE()).append(AppState.getString(482))));
                        XmppMailRuProtocol.removeQueuedCommand(this, iM1356x);
                        break;
                    case 5379:
                        XmppMailRuProtocol.handleMmpResponse(this, c0043nM1299a, iM1356x, iM1351l);
                        break;
                }
                AppController.needsLayoutUpdate = true;
            } else {
                if (c0043nM1299a.peekByteAt(1) == 4) {
                    AppController.m386a(this, c0043nM1299a);
                    AppController.needsLayoutUpdate = true;
                }
            }
            c0043nM1299a.clear();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x0130  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void parseContactStatus(String str, ByteBuffer c0043n) {
        int iM511a;
        int i = 255;
        MmpContact c0009ai = (MmpContact) getContact((Object) str);
        if (c0009ai == null || c0009ai.isOnline()) {
            return;
        }
        boolean z = c0009ai.highlighted;
        c0009ai.defaultIcon = 255;
        c0009ai.highlighted = false;
        c0009ai.isBlocked = false;
        c0009ai.isUnblocked = false;
        c0009ai.dirty = true;
        try {
            c0043n.skip(2);
            int iM1353u = c0043n.readShortBE();
            for (int i2 = 0; i2 < iM1353u; i2++) {
                int iM1353u2 = c0043n.readShortBE();
                int iM1353u3 = c0043n.readShortBE();
                if (iM1353u2 == 6) {
                    int iM1355w = c0043n.readIntBE() & 65535;
                    if (iM1355w == 0) {
                        i = 256;
                        c0009ai.defaultIcon = i;
                        iM1353u3 -= 4;
                        c0009ai.highlighted = true;
                    } else {
                        if (iM1355w == 19) {
                            i = 16449792;
                        } else if (iM1355w == 17) {
                            i = 16646400;
                        } else if ((iM1355w & 24576) == 24576) {
                            i = 17039360;
                        } else if ((iM1355w & 20480) == 20480) {
                            i = 16973824;
                        } else if ((iM1355w & 16384) == 16384) {
                            i = 16908288;
                        } else if ((iM1355w & 12288) == 12288) {
                            i = 16842752;
                        } else if ((iM1355w & 8192) == 8192) {
                            i = 17104896;
                        } else if ((iM1355w & 256) == 256) {
                            i = 16515328;
                        } else if ((iM1355w & 32) == 32) {
                            i = 16384256;
                        } else if ((iM1355w & 16) == 16) {
                            i = 16646400;
                        } else if ((iM1355w & 4) == 4) {
                            i = 16580864;
                        } else if ((iM1355w & 2) == 2) {
                            i = 16449792;
                        } else if ((iM1355w & 1) == 1) {
                            i = 16318720;
                        }
                        c0009ai.defaultIcon = i;
                        iM1353u3 -= 4;
                        c0009ai.highlighted = true;
                    }
                } else if (iM1353u2 == 13) {
                    byte[] bArrM581a = AppState.getBytes(905);
                    byte[] bArrM581a2 = AppState.getBytes(906);
                    byte[] bArrM581a3 = AppState.getBytes(908);
                    byte[] bArr = c0043n.data;
                    int i3 = c0043n.offset;
                    for (int i4 = 0; i4 < iM1353u3; i4 += 16) {
                        int i5 = i3 + i4;
                        for (int i6 = 0; i6 < 576; i6 += 16) {
                            if (Utils.m509a(bArrM581a3, i6, bArr, i5, 16)) {
                                c0009ai.defaultIcon &= -65536;
                                c0009ai.defaultIcon |= (i6 >> 4) + 269;
                            }
                        }
                        if (Utils.m509a(bArrM581a, 0, bArr, i5, 16)) {
                            c0009ai.isBlocked = true;
                        } else if (Utils.m509a(bArrM581a2, 0, bArr, i5, 16)) {
                            c0009ai.isUnblocked = true;
                        }
                    }
                } else if (iM1353u2 == 29) {
                    while (0 < iM1353u3 - 4) {
                        int iM1353u4 = c0043n.readShortBE();
                        int iM1353u5 = c0043n.readShortBE() & 255;
                        int i7 = (iM1353u3 - 2) - 2;
                        if ((iM1353u5 & 128) != 0 || i7 < (iM1353u5 & 128)) {
                            c0043n.skip(iM1353u5);
                            iM1353u3 = i7 - iM1353u5;
                        } else if (iM1353u4 == 14) {
                            byte[] bArr2 = new byte[iM1353u5];
                            c0043n.readIntoBytes(bArr2);
                            String strM17c = StringUtils.intern(new String(bArr2));
                            if (strM17c.startsWith(NetworkUtils.longToHex(28270022039266153L)) && (iM511a = Utils.m511a(StringUtils.suffix(strM17c, 7), 0, 23, -1)) >= 0) {
                                c0009ai.defaultIcon &= -65536;
                                c0009ai.defaultIcon |= iM511a + 269;
                            }
                            NetworkUtils.releaseBytes(bArr2);
                            iM1353u3 = i7 - iM1353u5;
                        } else {
                            c0043n.skip(iM1353u5);
                            iM1353u3 = i7 - iM1353u5;
                        }
                    }
                }
                c0043n.skip(iM1353u3);
            }
        } catch (Throwable unused) {
        }
        c0009ai.updateRenderState();
        if (z || !c0009ai.highlighted) {
            return;
        }
        ResourceManager.playNotificationSound(1);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateSend(Contact abstractC0041l, String str, long j) {
        ByteBuffer c0043nM464a;
        int iMo125a = super.validateSend(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.sentCount++;
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        int i = c0009ai.isBlocked ? 1 : 0;
        int i2 = c0009ai.isUnblocked ? 2 : 1;
        ByteBuffer c0043nM1373h = new ByteBuffer().writeLong(j).writeShortBE(i2).writeByteLenStr(c0009ai.identifier);
        ByteBuffer c0043n = new ByteBuffer();
        if (i2 == 1) {
            if (i == 1) {
                c0043n.writeShortBE(2).writeShortBE(0).writeAsShorts(str);
            } else {
                c0043n.writeIntLE(0).writeCharBytes(str);
            }
            c0043nM1373h.writeShortBE(2).writeShortBE(i + 9 + c0043n.length).writeShortBE(1281).writeShortBE(i + 1);
            if (i == 1) {
                c0043nM1373h.writeShortBE(262);
            } else {
                c0043nM1373h.writeByte(1);
            }
            c0043nM464a = AppController.m464a(this, 1030, c0043nM1373h.writeShortBE(257).writeBufferShortLen(c0043n).writeShortBE(6).writeShortBE(0));
        } else {
            if (i == 1) {
                c0043n.writeUTFNoLen(str);
            } else {
                c0043n.writeCharBytes(str);
            }
            c0043n.writeByte(0);
            int i3 = c0043n.length;
            int i4 = i3 - (i == 1 ? 0 : 42);
            c0043nM464a = AppController.m464a(this, 1030, c0043nM1373h.writeShortBE(5).writeShortBE(i4 + 143).writeShortBE(0).writeLong(j).writeCompressed(906).writeShortBE(10).writeShortBE(2).writeShortBE(1).writeShortBE(15).writeShortBE(0).writeShortBE(10001).writeShortBE(i4 + 103).writeShortLE(27).writeShortLE(8).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortBE(0).writeIntLE(3).writeByte(0).writeShortBE(0).writeIntLE(14).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortLE(1).writeShortLE(getConnectionModeValue()).writeShortLE(1).writeShortLE(i3).writeBuffer(c0043n).writeCompressed(i == 0 ? 526807 : 3279327).writeShortBE(3).writeShortBE(0));
        }
        return trySendData(c0043nM464a);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateModify(Contact abstractC0041l, Object[] objArr) {
        int iMo112a = super.validateModify(abstractC0041l, objArr);
        if (0 != iMo112a) {
            return iMo112a;
        }
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        String str = (String) objArr[0];
        return trySendData(queueCommand(new Object[]{AppController.m464a(this, 4873, c0009ai.encodeContactUpdate(3, str, c0009ai.onlineSemaphore)), ResourceManager.integerOf(0), c0009ai, str}));
    }

    /* renamed from: b */
    public final int updateConnectionMode(int i) {
        if (i == 256) {
            scheduleVersionUpdate(3);
        } else if (this.protocolVersion == 3) {
            scheduleVersionUpdate(4);
        }
        this.configFlags = i;
        if (isConnected()) {
            trySendData(XmppMailRuProtocol.sendContactListRequest(this, this.groupSequenceId));
            trySendData(queueCommand(new Object[]{AppController.m464a(this, 286, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(268435456 | getConnectionModeValue())), ResourceManager.integerOf(17)}));
            return trySendData(AppController.m375a(this));
        }
        if (isConnecting()) {
            return 487;
        }
        return connect(0);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateDelete(Contact abstractC0041l) {
        if (!isConnected()) {
            return 299;
        }
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        AppState.pool[1316] = ContactInfo.createAccountInfo(this).setMmpContactIdStr(c0009ai.identifier);
        return trySendData(StringUtils.m18a(this, Utils.parseInt((Object) c0009ai.identifier)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateMove(Contact abstractC0041l, ContactGroup abstractC0046q, ContactGroup abstractC0046q2) {
        int iMo113a = super.validateMove(abstractC0041l, abstractC0046q, abstractC0046q2);
        if (0 != iMo113a) {
            return iMo113a;
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        return trySendData(queueCommand(new Object[]{AppController.m464a(this, 4874, c0009ai.encodeContactUpdate(2, c0009ai.displayName, c0009ai.onlineSemaphore)), ResourceManager.integerOf(10), c0009ai, abstractC0046q, abstractC0046q2}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupRename(ContactGroup abstractC0046q, String str) {
        int iMo124a = super.validateGroupRename(abstractC0046q, str);
        if (iMo124a != 0) {
            return iMo124a;
        }
        MmpContactGroup c0016ap = (MmpContactGroup) abstractC0046q;
        return trySendData(queueCommand(new Object[]{AppController.m464a(this, 4873, c0016ap.createUpdatePacket(str, -1, -1)), ResourceManager.integerOf(1), c0016ap, str}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupCreate(String str) {
        int iMo122a = super.validateGroupCreate(str);
        if (iMo122a != 0) {
            return iMo122a;
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        return trySendData(ResourceManager.sendAddGroupCommand(this, str));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupDelete(ContactGroup abstractC0046q) {
        int iMo123a = super.validateGroupDelete(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        MmpContactGroup c0016ap = (MmpContactGroup) abstractC0046q;
        return trySendData(queueCommand(new Object[]{AppController.m464a(this, 4874, c0016ap.createUpdatePacket(c0016ap.name, -1, -1)), ResourceManager.integerOf(2), c0016ap}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateResend(Contact abstractC0041l) {
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        int iMo118b = super.validateResend((Contact) c0009ai);
        if (0 != iMo118b) {
            return iMo118b;
        }
        if (c0009ai.isOnline()) {
            removeContact((Contact) c0009ai, true);
            return 0;
        }
        if (c0009ai.canUnblock()) {
            trySendData(IOUtils.unblockContact(this, c0009ai));
        }
        if (c0009ai.canDelete()) {
            trySendData(IOUtils.deleteContact(this, c0009ai));
        }
        if (c0009ai.canBlock()) {
            trySendData(IOUtils.blockContact(this, c0009ai));
        }
        return trySendData(queueCommand(new Object[]{AppController.m464a(this, 4874, c0009ai.encodeContactUpdate(2, c0009ai.displayName, c0009ai.onlineSemaphore)), ResourceManager.integerOf(5), c0009ai}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupAdd(String str, String str2, String str3, ContactGroup abstractC0046q, boolean z) {
        int iMo734a = super.validateGroupAdd(str, str2, str3, abstractC0046q, z);
        if (0 != iMo734a) {
            return iMo734a;
        }
        trySendData(AppController.m464a(this, 4884, new ByteBuffer().writeByteLenStr(str).writeIntLE(0)));
        MmpContact c0009ai = (MmpContact) getContact((Object) str);
        if (null != c0009ai && !c0009ai.isOnline()) {
            return trySendData(IOUtils.createSendMessageCmd(this, c0009ai, str3));
        }
        trySendData(ResourceManager.createGetContactsCmd(this));
        MmpContactGroup c0016ap = (MmpContactGroup) abstractC0046q;
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortString(str).writeShortBE(c0016ap.groupId);
        int iM920k = generateUniqueGroupId();
        return trySendData(queueCommand(new Object[]{AppController.m464a(this, 4872, c0043nM1357m.writeShortBE(iM920k).writeShortBE(0).writeBufferShortLen(new ByteBuffer().writeShortBE(102).writeShortBE(0).writeShortBE(347).writeShortBE(1).writeByte(32).writeShortBE(305).writeUTF(str2))), ResourceManager.integerOf(14), str, str2, c0016ap, ResourceManager.integerOf(iM920k), str3}));
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
        int iM520a;
        do {
            z = false;
            iM520a = (Utils.m520a() & 28671) + 4096;
            if (iM520a == this.groupSequenceId) {
                z = true;
            } else {
                Vector vector = this.groups;
                int size = vector.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    MmpContactGroup c0016ap = (MmpContactGroup) vector.elementAt(size);
                    if (c0016ap.groupId == iM520a) {
                        z = true;
                    }
                    Vector vector2 = c0016ap.contacts;
                    int size2 = vector2.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        MmpContact c0009ai = (MmpContact) vector2.elementAt(size2);
                        if (c0009ai.userId == iM520a || c0009ai.canDelete == iM520a || c0009ai.canBlock == iM520a || c0009ai.canUnblock == iM520a) {
                            z = true;
                        }
                    }
                }
            }
        } while (z);
        return iM520a;
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int validateContactDelete(Contact abstractC0041l) {
        if (abstractC0041l.isOnline()) {
            return 310;
        }
        return trySendData(IOUtils.unblockContact(this, (MmpContact) abstractC0041l));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int validateContactBlock(Contact abstractC0041l) {
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        if (c0009ai.canBlock() && !c0009ai.canDelete()) {
            trySendData(IOUtils.blockContact(this, c0009ai));
        }
        return trySendData(IOUtils.deleteContact(this, c0009ai));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int validateContactUnblock(Contact abstractC0041l) {
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        if (!c0009ai.canBlock() && c0009ai.canDelete()) {
            trySendData(IOUtils.deleteContact(this, c0009ai));
        }
        return trySendData(IOUtils.blockContact(this, c0009ai));
    }

    @Override // p000.Account
    /* renamed from: l */
    public final int disconnect() {
        if (this.connectionData != null) {
            this.connectionData[0] = null;
        }
        this.connectionData = null;
        int iMo120l = super.disconnect();
        if (0 != iMo120l) {
            return iMo120l;
        }
        trySendData(AppController.createPingPacket(this, 4));
        closeConnection();
        this.lastError = getDefaultError();
        return 0;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateObject(Object obj) {
        String[] strArr = (String[]) obj;
        ByteBuffer c0043nM1358n = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(2000).writeShortBE(0).writeShortLE(1375);
        String str = strArr[0];
        if (str.length() > 0) {
            c0043nM1358n.writeShortBE(13825).writeShortLE(4).writeIntLE(Utils.parseInt((Object) str));
        } else {
            c0043nM1358n.writeShortBE(12290).writeShortLE(1).writeByte(strArr[1].length()).writeProtocolStr(21505, strArr[2]).writeProtocolStr(16385, strArr[3]).writeProtocolStr(18945, strArr[4]).writeProtocolStr(24065, strArr[5]).writeProtocolStr(36865, strArr[6]).writeProtocolStr(9730, strArr[7]);
        }
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortBE(1);
        int i = c0043nM1358n.length;
        return trySendData(queueCommand(new Object[]{AppController.m464a(this, 5378, c0043nM1357m.writeShortBE(i + 2).writeShortLE(i).writeBuffer(c0043nM1358n)), ResourceManager.integerOf(9)}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final Contact newContact(String str) {
        ContactGroup abstractC0046q = this.defaultGroup;
        MmpContact c0009ai = new MmpContact(this, -1, -1, str, str, true);
        abstractC0046q.addContact((Object) c0009ai);
        ByteBuffer c0043nM1360p = new ByteBuffer().writeIntLE(this.serverId).writeShortLE(2000).writeShortBE(0).writeShortLE(1375).writeShortBE(13825).writeShortLE(4).writeIntLE(Utils.parseInt((Object) str));
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortBE(1);
        int i = c0043nM1360p.length;
        trySendData(queueCommand(new Object[]{AppController.m464a(this, 5378, c0043nM1357m.writeShortBE(i + 2).writeShortLE(i).writeBuffer(c0043nM1360p)), ResourceManager.integerOf(21)}));
        return c0009ai;
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
        return 4;
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
