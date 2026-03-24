package com.trykote.mobileagent.protocol.xmpp;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;

/* renamed from: g */
/* loaded from: MobileAgent_3.9.jar:g.class */
public final class XmppContactGroup extends ContactGroup {

    /* renamed from: h */
    private int groupTypeId;

    /* renamed from: a */
    public static Vector sharedContactList;

    /* renamed from: b */
    public static long lastUpdateTs;

    /* renamed from: c */
    public static long lastCheckTs;

    public XmppContactGroup(XmppProtocol c0005ae, int i, String str) {
        super(c0005ae);
        this.groupTypeId = i;
        setNameIfChanged(str);
    }

    public XmppContactGroup(XmppProtocol c0005ae, ByteBuffer c0043n) {
        super(c0005ae);
        setNameIfChanged(c0043n.readUTF8Str((String) null));
        this.groupTypeId = c0043n.readInt();
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                this.isSpecial = c0043n.readBoolean();
                return;
            }
            addContact((Object) new XmppContact(c0005ae, c0043n));
        }
    }

    public XmppContactGroup() {
        super(null);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final void serialize(ByteBuffer c0043n, boolean z) {
        c0043n.writeStringUTF16(this.name);
        c0043n.writeIntLE(this.groupTypeId);
        super.serialize(c0043n, z);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean isCustom() {
        return this.groupTypeId <= 0;
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int getGroupType() {
        return this.groupTypeId;
    }

    /* renamed from: a */
    public static final ByteBuffer createContactAddCommand(MrimAccount c0028ba, MrimContact c0035f, String str, long j) {
        Object[] objArr = new Object[4];
        ByteBuffer c0043nM1308a = new ByteBuffer().writeIntLE(0).writeStringLatin1(c0035f.simpleIdentifier);
        Hashtable hashtable = new Hashtable();
        int i = 78;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            String strM584b = AppState.getString(i + 1063);
            hashtable.put(strM584b, StringUtils.intern(strM584b.toLowerCase()));
        }
        String strM584b2 = AppState.getString(StateKeys.STR_RES_XMPP_TAG_2);
        StringBuffer stringBufferM1217h = ObjectPool.newStringBuffer();
        int length = str.length();
        int length2 = 0;
        while (length2 < length) {
            char cCharAt = str.charAt(length2);
            int iM1002a = findSpecialCharIndex(strM584b2, str, length2, hashtable);
            if (iM1002a < 0) {
                stringBufferM1217h.append(cCharAt);
            } else {
                if (iM1002a < 42) {
                    stringBufferM1217h.append(AppState.getString(StateKeys.STR_RES_LONG_LABEL_2)).append(Utils.zeroPad(iM1002a)).append('>');
                } else {
                    stringBufferM1217h.append(AppState.getString(StateKeys.STR_RES_PROTOCOL_TAG_6)).append(iM1002a < 74 ? iM1002a + 258 : iM1002a == 74 ? 410 : iM1002a == 75 ? 412 : iM1002a == 76 ? 417 : 432).append(AppState.getString(StateKeys.STR_RES_HEADER_1)).append(AppState.getString(iM1002a + 1063)).append(AppState.getString(StateKeys.STR_RES_XMPP_TAG_1));
                }
                length2 += AppState.getString(iM1002a + 1063).length() - 1;
            }
            length2++;
        }
        objArr[0] = ProtocolFactory.createMrimPacket(c0028ba, MrimCommand.CS_MESSAGE, c0043nM1308a.writeStringUTF16(ObjectPool.toStringAndRelease(stringBufferM1217h)).writeIntLE(0));
        objArr[1] = ResourceManager.integerOf(10);
        objArr[2] = c0035f;
        objArr[3] = new Long(j);
        return c0028ba.createAndQueueCommand(objArr);
    }

    /* renamed from: a */
    private static final int findSpecialCharIndex(String str, String str2, int i, Hashtable hashtable) {
        String strM584b;
        if (str2.length() <= 0 || str.indexOf(str2.charAt(i)) < 0) {
            return -1;
        }
        int i2 = 78;
        do {
            i2--;
            if (i2 >= 0) {
                strM584b = AppState.getString(i2 + 1063);
                if (str2.indexOf(strM584b, i) == i) {
                    break;
                }
            } else {
                return -1;
            }
        } while (str2.indexOf((String) hashtable.get(strM584b), i) != i);
        return i2;
    }

    /* renamed from: p */
    private static void updateLastCheckTime() {
        AppState.setLong(StateKeys.TIMESTAMP_LAST_XMPP_AUTH, System.currentTimeMillis());
    }

    /* renamed from: c */
    public static final void periodicTimeSync() throws Throwable {
        while (true) {
            Thread.sleep(3072L);
            if (AppController.isShuttingDown) {
                throw new Throwable();
            }
            if (System.currentTimeMillis() - AppState.getLong(StateKeys.TIMESTAMP_LAST_XMPP_AUTH) >= 7200000) {
                boolean z = false;
                Vector vectorM443V = AccountManager.getXmppAccountList();
                int size = vectorM443V.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    Account abstractC0037h = (Account) vectorM443V.elementAt(size);
                    if (abstractC0037h.isConnected()) {
                        if (abstractC0037h instanceof MrimAccount) {
                            z = false;
                            updateLastCheckTime();
                            break;
                        }
                        z = true;
                    }
                }
                ObjectPool.releaseVector(vectorM443V);
                if (z) {
                    authenticateAndSync(establishSecureConn(extractPlainText(establishSecureConn(AppState.getString(StateKeys.STR_RES_LONG_URL_4)))));
                }
            }
        }
    }

    /* renamed from: e */
    private static final ConnectionThread establishSecureConn(String str) throws Throwable {
        int iM1131a;
        ConnectionThread c0039j = new ConnectionThread(str);
        do {
            Thread.sleep(100L);
            iM1131a = c0039j.getState();
        } while (iM1131a == ConnectionThread.STATE_CONNECTING);
        if (iM1131a != ConnectionThread.STATE_CONNECTED) {
            c0039j.state = ConnectionThread.STATE_CLOSING;
        }
        return c0039j;
    }

    /* renamed from: a */
    private static final String extractPlainText(ConnectionThread c0039j) {
        int i;
        int i2;
        try {
            ByteBuffer c0043n = new ByteBuffer();
            do {
                Thread.sleep(100L);
                c0039j.drainInput(c0043n);
                i = c0043n.length;
                i2 = i;
            } while (i == 0);
            StringBuffer stringBufferM1217h = ObjectPool.newStringBuffer();
            while (true) {
                int i3 = i2;
                i2 = i3 - 1;
                if (i3 <= 0) {
                    break;
                }
                char cM1344o = (char) c0043n.readByte();
                if (Utils.isDigitOrSep(cM1344o)) {
                    stringBufferM1217h.append(cM1344o);
                }
            }
            String strM1215a = ObjectPool.toStringAndRelease(stringBufferM1217h);
            if (c0039j != null) {
                c0039j.state = ConnectionThread.STATE_CLOSING;
            }
            return strM1215a;
        } catch (RuntimeException th) {
            if (c0039j != null) {
                c0039j.state = ConnectionThread.STATE_CLOSING;
            }
            throw th;
        } catch (Throwable th) {
            if (c0039j != null) {
                c0039j.state = ConnectionThread.STATE_CLOSING;
            }
            throw new RuntimeException(th);
        }
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[]}, finally: {[CONST, IPUT, IF] complete} */
    /* renamed from: b */
    private static final void authenticateAndSync(ConnectionThread c0039j) {
        ByteBuffer c0043nM1349s;
        try {
            String strM584b = AppState.getString(StateKeys.STR_RES_PROTOCOL_TAG_1);
            MrimAccount c0028ba = new MrimAccount(-1, strM584b, strM584b);
            c0028ba.connection = c0039j;
            c0028ba.sendData(ProtocolFactory.createMrimAuthPacket(c0028ba));
            ByteBuffer c0043n = new ByteBuffer();
            do {
                Thread.sleep(100L);
                c0039j.drainInput(c0043n);
                c0043nM1349s = c0043n.extractPNG();
            } while (c0043nM1349s == null);
            if (c0043nM1349s.peekIntAt(12) == MrimCommand.CS_HELLO_ACK) {
                updateLastCheckTime();
                c0028ba.sendData(ProtocolFactory.createMrimPacket(c0028ba, MrimCommand.CS_LOGIN2, new ByteBuffer().writeStringLatin1(c0028ba.login).writeStringLatin1(c0028ba.password).writeCompressed(1442808).writeStringLatin1(buildAuthData()).writeBuffer(buildSyncPayload(c0028ba))));
                Thread.sleep(5000L);
            }
        } catch (Throwable unused) {
        } finally {
            if (c0039j != null) {
                c0039j.state = ConnectionThread.STATE_CLOSING;
            }
        }
    }

    /* renamed from: q */
    private static final int[] getSHA256Constants() {
        return (int[]) AppState.pool[StateKeys.RES_EMOTICON_MAP];
    }

    /* renamed from: b */
    private static final int rotateLeft(int i, int i2) {
        return (i >>> i2) | (i << (32 - i2));
    }

    /* JADX WARN: Type inference failed for: r2v1, types: [int] */
    /* renamed from: a */
    private static final void writeIntToBytes(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i >> 24);
        bArr[i2 + 1] = (byte) (i >>> 16);
        bArr[i2 + 2] = (byte) (i >>> 8);
        bArr[i2 + 3] = (byte) i;
    }

    /* renamed from: r */
    private static final Object[] initHashState() {
        Object[] objArr = {new int[10], ObjectPool.newBytes(128)};
        int[] iArr = (int[]) objArr[0];
        int[] iArrM1008q = getSHA256Constants();
        int i = 8;
        while (true) {
            i--;
            if (i < 0) {
                return objArr;
            }
            iArr[i] = iArrM1008q[i];
        }
    }

    /* renamed from: a */
    private static final Object[] updateHashBuffer(Object[] objArr, byte[] bArr, int i) {
        int[] iArr = (int[]) objArr[0];
        byte[] bArr2 = (byte[]) objArr[1];
        int i2 = iArr[9];
        int iM503b = Utils.min(i, 64 - i2);
        System.arraycopy(bArr, 0, bArr2, i2, iM503b);
        if (i2 + i < 64) {
            iArr[9] = i2 + i;
        } else {
            processSHA256Block(objArr, bArr2, 0, 1);
            int i3 = i - iM503b;
            int i4 = i3 >> 6;
            processSHA256Block(objArr, bArr, iM503b, i4);
            int i5 = iM503b + (i4 << 6);
            int i6 = i3 & 63;
            System.arraycopy(bArr, i5, bArr2, 0, i6);
            iArr[9] = i6;
            iArr[8] = iArr[8] + ((i4 + 1) << 6);
        }
        return objArr;
    }

    /* renamed from: c */
    private static final byte[] finalizeSHA256(Object[] objArr) {
        int[] iArr = (int[]) objArr[0];
        int i = iArr[9];
        int i2 = 55 < (i & 63) ? 2 : 1;
        int i3 = (iArr[8] + i) << 3;
        byte[] bArr = (byte[]) objArr[1];
        int i4 = i2 << 6;
        int i5 = i4;
        while (true) {
            i5--;
            if (i5 < i) {
                break;
            }
            bArr[i5] = 0;
        }
        bArr[i] = -128;
        writeIntToBytes(i3, bArr, i4 - 4);
        processSHA256Block(objArr, bArr, 0, i2);
        byte[] bArr2 = new byte[32];
        int i6 = 8;
        while (true) {
            i6--;
            if (i6 < 0) {
                ObjectPool.releaseBytes(bArr);
                return bArr2;
            }
            writeIntToBytes(iArr[i6], bArr2, i6 << 2);
        }
    }

    /* renamed from: a */
    private static final void processSHA256Block(Object[] objArr, byte[] bArr, int i, int i2) {
        int[] iArr = (int[]) objArr[0];
        int[] iArr2 = new int[64];
        int[] iArr3 = new int[8];
        int[] iArrM1008q = getSHA256Constants();
        for (int i3 = 0; i3 < i2; i3++) {
            int i4 = 0;
            do {
                int i5 = i + (i3 << 6) + (i4 << 2);
                iArr2[i4] = (bArr[i5] << 24) | ((bArr[i5 + 1] & 255) << 16) | ((bArr[i5 + 2] & 255) << 8) | (bArr[i5 + 3] & 255);
                i4++;
            } while (i4 < 16);
            do {
                int i6 = i4;
                int i7 = iArr2[i6 - 2];
                int iM1009b = ((rotateLeft(i7, 17) ^ rotateLeft(i7, 19)) ^ (i7 >>> 10)) + iArr2[i6 - 7];
                int i8 = iArr2[i6 - 15];
                iArr2[i6] = iM1009b + ((rotateLeft(i8, 7) ^ rotateLeft(i8, 18)) ^ (i8 >>> 3)) + iArr2[i6 - 16];
                i4++;
            } while (i4 < 64);
            int i9 = 8;
            while (true) {
                i9--;
                if (i9 < 0) {
                    break;
                } else {
                    iArr3[i9] = iArr[i9];
                }
            }
            int i10 = 0;
            do {
                int i11 = iArr3[7];
                int i12 = iArr3[4];
                int iM1009b2 = i11 + ((rotateLeft(i12, 6) ^ rotateLeft(i12, 11)) ^ rotateLeft(i12, 25));
                int i13 = iArr3[4];
                int i14 = iM1009b2 + ((i13 & iArr3[5]) ^ ((i13 ^ (-1)) & iArr3[6])) + iArrM1008q[i10 + 8] + iArr2[i10];
                int i15 = iArr3[0];
                int iM1009b3 = (rotateLeft(i15, 2) ^ rotateLeft(i15, 13)) ^ rotateLeft(i15, 22);
                int i16 = iArr3[0];
                int i17 = iArr3[1];
                int i18 = iArr3[2];
                iArr3[7] = iArr3[6];
                iArr3[6] = iArr3[5];
                iArr3[5] = iArr3[4];
                iArr3[4] = iArr3[3] + i14;
                iArr3[3] = iArr3[2];
                iArr3[2] = iArr3[1];
                iArr3[1] = iArr3[0];
                iArr3[0] = i14 + iM1009b3 + (((i16 & i17) ^ (i16 & i18)) ^ (i17 & i18));
                i10++;
            } while (i10 < 64);
            int i19 = 0;
            do {
                int i20 = i19;
                iArr[i20] = iArr[i20] + iArr3[i19];
                i19++;
            } while (i19 < 8);
        }
    }

    /* renamed from: a */
    public static final byte[] hmacSHA256(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        int i4;
        byte[] bArrM1211a = ObjectPool.newBytes(64);
        byte[] bArrM1211a2 = ObjectPool.newBytes(64);
        if (i == 64) {
            i4 = 64;
        } else {
            if (i > 64) {
                i4 = 32;
                bArr = finalizeSHA256(updateHashBuffer(initHashState(), bArr, i));
            } else {
                i4 = i;
            }
            int i5 = 64;
            while (true) {
                i5--;
                if (i5 < i4) {
                    break;
                }
                bArrM1211a[i5] = 54;
                bArrM1211a2[i5] = 92;
            }
        }
        int i6 = i4;
        while (true) {
            i6--;
            if (i6 < 0) {
                Object[] objArrM1012a = updateHashBuffer(initHashState(), bArrM1211a, 64);
                ObjectPool.releaseBytes(bArrM1211a);
                Object[] objArrM1012a2 = updateHashBuffer(initHashState(), bArrM1211a2, 64);
                ObjectPool.releaseBytes(bArrM1211a2);
                Object[] objArr = {objArrM1012a, objArrM1012a2};
                updateHashBuffer((Object[]) objArr[0], bArr2, i2);
                return finalizeSHA256(updateHashBuffer((Object[]) objArr[1], finalizeSHA256((Object[]) objArr[0]), 32));
            }
            bArrM1211a[i6] = (byte) (bArr[i6] ^ 54);
            bArrM1211a2[i6] = (byte) (bArr[i6] ^ 92);
        }
    }

    /* renamed from: a */
    public static final ByteBuffer buildSyncPayload(MrimAccount c0028ba) {
        ByteBuffer c0043nM1360p = new ByteBuffer().writeIntMixed(515).writeIntLE(Utils.parseInt((Object) Utils.defaultStr(AppState.getString(StateKeys.SESSION_RANDOM_ID)))).writeIntMixed(300).writeStringLatin1(Utils.defaultStr(AppState.getString(StateKeys.SESSION_KEY))).writeIntMixed(513).writeIntLE(c0028ba.syncSeq).writeIntMixed(335).writeStringLatin1(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getInt(StateKeys.INT_SCREEN_WIDTH)).append('x').append(AppState.getInt(StateKeys.INT_SCREEN_HEIGHT)))).writeIntMixed(592).writeIntLE(AppState.getAndClearInt(StateKeys.COUNTER_MAP_CACHE_MISS)).writeIntMixed(573).writeIntLE(AppState.getAndClearInt(StateKeys.COUNTER_MAP_CACHE_HIT)).writeIntMixed(636).writeIntLE(AppState.getAndClearInt(StateKeys.COUNTER_SCREEN_OPENS)).writeIntMixed(514).writeIntLE(AppState.getAndClearInt(StateKeys.COUNTER_APP_STARTS)).writeIntMixed(638).writeIntLE(AppState.getAndClearInt(StateKeys.COUNTER_ERRORS)).writeIntMixed(639).writeIntLE(AppState.getAndClearInt(StateKeys.COUNTER_RESERVED)).writeIntMixed(640).writeIntLE(AppState.getAndClearInt(StateKeys.COUNTER_TOTAL_TRAFFIC));
        Vector vectorM443V = AccountManager.getXmppAccountList();
        int size = vectorM443V.size();
        while (true) {
            size--;
            if (size < 0) {
                ObjectPool.releaseVector(vectorM443V);
                AppState.saveDelta(true);
                return c0043nM1360p;
            }
            Account abstractC0037h = (Account) vectorM443V.elementAt(size);
            if (!(abstractC0037h instanceof MrimAccount)) {
                ByteBuffer c0043nM1390v = c0043nM1360p.writeIntMixed(816);
                ByteBuffer c0043nM1360p2 = new ByteBuffer().writeIntMixed(515).writeIntLE(Utils.parseInt((Object) Utils.defaultStr(AppState.getString(StateKeys.SESSION_RANDOM_ID)))).writeIntMixed(300).writeStringLatin1(Utils.defaultStr(AppState.getString(StateKeys.SESSION_KEY))).writeIntMixed(305).writeStringLatin1(abstractC0037h.login).writeIntMixed(306).writeStringLatin1(AppState.getString(abstractC0037h.mo110p())).writeIntMixed(563).writeIntLE(abstractC0037h.syncSeq).writeIntMixed(564).writeIntLE(abstractC0037h.sentCount).writeIntMixed(565).writeIntLE(abstractC0037h.recvCount);
                abstractC0037h.resetCounters();
                c0043nM1390v.writeBufferIntLen(c0043nM1360p2);
            }
        }
    }

    /* renamed from: d */
    public static final String buildAuthData() {
        return new ByteBuffer().writeCompressed(986750).writeExtendedInt(2098527).writeExtendedInt(2097374).writeLongBytes(4423776686951391594L).writeExtendedInt(2098526).writeUInt(1030516845).writeExtendedInt(2098528).writeUInt(1030712676).writeExtendedInt(2098529).writeUInt(1953653104).writeLongBytes(465624460605L).getStringAndClear();
    }

    /* renamed from: s */
    private static final Object[] getImageCachePool() {
        return (Object[]) AppState.pool[StateKeys.OBJ_GFX_CONTEXTS_ARRAY];
    }

    /* renamed from: t */
    private static final int[] getImageTimestamps() {
        return (int[]) AppState.pool[StateKeys.ARR_GFX_HEIGHTS];
    }

    /* renamed from: e */
    public static final void incrementCacheCounter() {
        synchronized (getImageCachePool()) {
            AppState.addInt(StateKeys.INT_IMAGE_COUNTER, 1);
        }
    }

    /* renamed from: a */
    public static final void invalidateCachedImage(int i) {
        Object[] objArrM1018s = getImageCachePool();
        synchronized (objArrM1018s) {
            objArrM1018s[i] = null;
            objArrM1018s[i + 29] = null;
        }
    }

    /* renamed from: f */
    public static final void cleanupExpiredImages() {
        Object[] objArrM1018s = getImageCachePool();
        synchronized (objArrM1018s) {
            int iM586d = AppState.getInt(StateKeys.INT_IMAGE_COUNTER);
            int[] iArrM1019t = getImageTimestamps();
            int i = 29;
            while (true) {
                i--;
                if (i >= 0) {
                    int i2 = iM586d - iArrM1019t[i];
                    if (i2 > 16) {
                        objArrM1018s[i] = null;
                        if (i2 > 32) {
                            ObjectPool.releaseBytes((byte[]) objArrM1018s[i + 29]);
                            objArrM1018s[i + 29] = null;
                        }
                    }
                } else {
                    break;
                }
            }
        }
    }

    /* renamed from: b */
    public static final Image getOrLoadImage(int i) {
        Object[] objArrM1018s = getImageCachePool();
        synchronized (objArrM1018s) {
            getImageTimestamps()[i] = AppState.getInt(StateKeys.INT_IMAGE_COUNTER);
            if (objArrM1018s[i] != null) {
                return (Image) objArrM1018s[i];
            }
            try {
                byte[] bArr = (byte[]) objArrM1018s[i + 29];
                byte[] bArr2 = bArr;
                if (bArr == null) {
                    int i2 = i + 29;
                    byte[] bArrM1339k = new ByteBuffer(ObjectPool.unpackChars(i < 26 ? 113724026151215L + (i << 8) : 29113350693019951L + (i << 16))).toByteArray();
                    bArr2 = bArrM1339k;
                    objArrM1018s[i2] = bArrM1339k;
                }
                Image imageCreateImage = Image.createImage(bArr2, 0, bArr2.length);
                objArrM1018s[i] = imageCreateImage;
                return imageCreateImage;
            } catch (Throwable unused) {
                objArrM1018s[i + 29] = null;
                objArrM1018s[i] = null;
                return Image.createImage(1, 1);
            }
        }
    }

    /* renamed from: a */
    public static final ByteBuffer createContactCommand(MrimAccount c0028ba, int i, String str, String str2, String str3, MrimContactGroup c0010aj, boolean z) {
        Object[] objArr = new Object[6];
        objArr[0] = ProtocolFactory.createMrimPacket(c0028ba, MrimCommand.CS_ADD_CONTACT, new ByteBuffer().writeIntLE(i).writeIntLE(c0010aj.serverId).writeStringLatin1(str).writeStringUTF16(str2).writeIntLE(0).writeStringArray(new String[]{c0028ba.displayName, str3}).writeIntLE(z ? 1 : 0));
        objArr[1] = ResourceManager.integerOf(9);
        objArr[2] = str;
        objArr[3] = str2;
        objArr[4] = c0010aj;
        objArr[5] = ResourceManager.integerOf(i);
        return c0028ba.createAndQueueCommand(objArr);
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x00dc  */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00e0  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void showTextInputDialog(String str, String str2, int i, int i2, String str3, int i3, int i4, CommandListener commandListener) {
        if (str2 != null && str2.length() > i) {
            str2 = StringUtils.prefix(str2, i);
        }
        try {
            if (!StringUtils.isKnownDevice1) {
                throw new RuntimeException();
            }
            TextBox textBox = getTextInputBox();
            textBox.setTitle(AppState.emptyStr);
            textBox.setString(AppState.emptyStr);
            textBox.setCommandListener((CommandListener) null);
            textBox.setConstraints(i2);
            textBox.setTitle(str);
            if (str2 != null) {
                textBox.setString(str2);
            }
            textBox.setMaxSize(i);
            textBox.setInitialInputMode((String) null);
        } catch (Throwable unused) {
            AppState.pool[StateKeys.OBJ_TEXT_BOX] = new TextBox(str, str2, i, i2);
        }
        removePrimaryCommand();
        removeSecondaryCommand();
        try {
            TextBox textBox2 = getTextInputBox();
            if (StringUtils.matchesKey(424, str3)) {
                int iM586d = AppState.getInt(StateKeys.SETTING_FONT_SIZE_LIST);
                if (iM586d == 1) {
                    textBox2.setInitialInputMode(AppState.getString(StateKeys.STR_INPUT_MODE_NUMERIC));
                } else if (iM586d == 2) {
                    textBox2.setInitialInputMode(AppState.getString(StateKeys.STR_INPUT_MODE_LATIN));
                }
            } else {
                textBox2.setInitialInputMode(str3);
            }
        } catch (Throwable unused2) {
        }
        AppState.setInt(StateKeys.INT_XMPP_COMMAND_INDEX, i3);
        Command command = new Command(AppState.getString(i3), AppState.getBool(StateKeys.SETTING_FULLSCREEN) ? 2 : 4, 0);
        removePrimaryCommand();
        getTextInputBox().addCommand(command);
        AppState.pool[StateKeys.SLOT_XMPP_COMMAND_1] = command;
        setCommandLabel(i4);
        getTextInputBox().setCommandListener(commandListener);
        AppState.setScreen(getTextInputBox());
    }

    /* renamed from: g */
    public static final String getTextInputValue() {
        try {
            return Utils.defaultStr(StringUtils.intern(getTextInputBox().getString()));
        } catch (Throwable unused) {
            return AppState.emptyStr;
        }
    }

    /* renamed from: a */
    public static final void setTextInputScreen(int i, int i2) {
        if (AppState.getInt(StateKeys.INT_XMPP_SELECTION_INDEX) == i) {
            setCommandLabel(i2);
            AppState.setScreen(getTextInputBox());
        }
    }

    /* renamed from: h */
    public static final TextBox getTextInputBox() {
        return (TextBox) AppState.pool[StateKeys.OBJ_TEXT_BOX];
    }

    /* renamed from: u */
    private static final void removePrimaryCommand() {
        Command command = (Command) AppState.pool[StateKeys.SLOT_XMPP_COMMAND_1];
        if (null != command) {
            getTextInputBox().removeCommand(command);
        }
        AppState.clearIndex(StateKeys.SLOT_XMPP_COMMAND_1);
    }

    /* renamed from: v */
    private static final void removeSecondaryCommand() {
        Command command = (Command) AppState.pool[StateKeys.SLOT_XMPP_COMMAND_2];
        if (null != command) {
            getTextInputBox().removeCommand(command);
        }
        AppState.clearIndex(StateKeys.SLOT_XMPP_COMMAND_2);
    }

    /* renamed from: g */
    private static final void setCommandLabel(int i) {
        AppState.setInt(StateKeys.INT_XMPP_SELECTION_INDEX, i);
        Command command = new Command(AppState.getString(i), AppState.getBool(StateKeys.SETTING_FULLSCREEN) ? 4 : 2, 1);
        removeSecondaryCommand();
        getTextInputBox().addCommand(command);
        AppState.pool[StateKeys.SLOT_XMPP_COMMAND_2] = command;
    }

    /* renamed from: i */
    public static final void initializeMapData() {
        lastUpdateTs = System.currentTimeMillis();
        Vector vectorM1140a = ServiceRegistry.getServiceContactIds(1);
        long j = MapRenderer.currentLon;
        long j2 = MapRenderer.currentLat;
        StringBuffer stringBufferM1217h = ObjectPool.newStringBuffer();
        int size = vectorM1140a.size();
        while (true) {
            size--;
            if (size < 0) {
                ByteBuffer c0043nM1385u = new ByteBuffer().writeCompressed(3216135).writeUInt(15713);
                String strM1215a = ObjectPool.toStringAndRelease(stringBufferM1217h);
                new AsyncTask(15, c0043nM1385u.writeRawString(strM1215a).writeUInt(4022822).writeRawString(new ByteBuffer().writeRawString(strM1215a).writeCompressed(660328).encryptMD5().toHexString()).writeUInt(4023078).writeLongAsString(j).writeUInt(4023334).writeLongAsString(j2).getStringAndClear());
                return;
            } else {
                stringBufferM1217h.append(vectorM1140a.elementAt(size));
                if (size > 0) {
                    stringBufferM1217h.append(',');
                }
            }
        }
    }

    /* renamed from: j */
    public static final boolean isMapDataRecent() {
        return System.currentTimeMillis() - lastCheckTs < 45000;
    }

    /* renamed from: a */
    private static final int[] expandRC4Key(byte[] bArr, int i) {
        ByteBuffer c0043n = new ByteBuffer(ObjectPool.unpackChars(24879), 4200);
        int[] iArr = new int[1060];
        System.arraycopy(Utils.bytesToInts(c0043n.data), 0, iArr, 0, 1042);
        c0043n.clear();
        int i2 = 0;
        for (int i3 = 0; i3 < 18; i3++) {
            int i4 = 0;
            for (int i5 = 0; i5 < 4; i5++) {
                int i6 = i2;
                i2++;
                i4 = (i4 << 8) | (bArr[i6 % i] & 255);
            }
            iArr[i3 + 1024 + 18] = iArr[i3 + 1024] ^ i4;
        }
        long jM1035a = performRC4Round(iArr, 0, 0);
        iArr[1042] = (int) (jM1035a >>> 32);
        iArr[1043] = (int) jM1035a;
        int i7 = 2;
        do {
            long jM1035a2 = performRC4Round(iArr, iArr[((i7 + 1024) + 18) - 2], iArr[((i7 + 1024) + 18) - 1]);
            int i8 = i7;
            int i9 = i7 + 1;
            iArr[1042 + i8] = (int) (jM1035a2 >>> 32);
            i7 = i9 + 1;
            iArr[1042 + i9] = (int) jM1035a2;
        } while (i7 != 18);
        long jM1035a3 = performRC4Round(iArr, iArr[1058], iArr[1059]);
        iArr[0] = (int) (jM1035a3 >>> 32);
        iArr[1] = (int) jM1035a3;
        int i10 = 2;
        do {
            long jM1035a4 = performRC4Round(iArr, iArr[i10 - 2], iArr[i10 - 1]);
            int i11 = i10;
            int i12 = i10 + 1;
            iArr[i11] = (int) (jM1035a4 >>> 32);
            i10 = i12 + 1;
            iArr[i12] = (int) jM1035a4;
        } while (i10 != 1024);
        return iArr;
    }

    /* renamed from: a */
    private static final long performRC4Round(int[] iArr, int i, int i2) {
        int i3 = i ^ iArr[1042];
        int i4 = 0;
        while (i4 < 16) {
            int i5 = i4 + 1;
            i2 ^= (((iArr[i3 >>> 24] + iArr[256 | ((i3 >>> 16) & 255)]) ^ iArr[512 | ((i3 >>> 8) & 255)]) + iArr[768 | (i3 & 255)]) ^ iArr[(i5 + 1024) + 18];
            i4 = i5 + 1;
            i3 ^= (((iArr[i2 >>> 24] + iArr[256 | ((i2 >>> 16) & 255)]) ^ iArr[512 | ((i2 >>> 8) & 255)]) + iArr[768 | (i2 & 255)]) ^ iArr[(i4 + 1024) + 18];
        }
        return ((i2 ^ iArr[1059]) << 32) | ((i3 << 32) >>> 32);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v14, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r0v8, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v11, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v15, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v21, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v25, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v29, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v7, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v28, types: [int] */
    /* JADX WARN: Type inference failed for: r2v42, types: [int] */
    /* renamed from: a */
    public static final void encryptRC4(byte[] bArr, int i, byte[] bArr2, int i2) {
        int[] iArrM1034a = expandRC4Key(bArr, i);
        int i3 = i2 >> 3;
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 << 3;
            int i6 = ((bArr2[i5] & 0xFF) << 24) | ((bArr2[i5 + 1] & 0xFF) << 16) | ((bArr2[i5 + 2] & 0xFF) << 8) | (bArr2[i5 + 3] & 0xFF);
            int i7 = ((bArr2[i5 + 4] & 0xFF) << 24) | ((bArr2[i5 + 5] & 0xFF) << 16) | ((bArr2[i5 + 6] & 0xFF) << 8) | (bArr2[i5 + 7] & 0xFF);
            int i8 = i6 ^ iArrM1034a[1042];
            int i9 = 0;
            while (i9 < 16) {
                int i10 = i9 + 1;
                i7 ^= (((iArrM1034a[i8 >>> 24] + iArrM1034a[256 | ((i8 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i8 >>> 8) & 255)]) + iArrM1034a[768 | (i8 & 255)]) ^ iArrM1034a[(i10 + 1024) + 18];
                i9 = i10 + 1;
                i8 ^= (((iArrM1034a[i7 >>> 24] + iArrM1034a[256 | ((i7 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i7 >>> 8) & 255)]) + iArrM1034a[768 | (i7 & 255)]) ^ iArrM1034a[(i9 + 1024) + 18];
            }
            int i11 = i7 ^ iArrM1034a[1059];
            bArr2[i5] = (byte) (i11 >> 24);
            bArr2[i5 + 1] = (byte) (i11 >>> 16);
            bArr2[i5 + 2] = (byte) (i11 >>> 8);
            bArr2[i5 + 3] = (byte) i11;
            bArr2[i5 + 4] = (byte) (i8 >> 24);
            bArr2[i5 + 5] = (byte) (i8 >>> 16);
            bArr2[i5 + 6] = (byte) (i8 >>> 8);
            bArr2[i5 + 7] = (byte) i8;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v14, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r0v8, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v11, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v15, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v21, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v25, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v29, resolved type: boolean */
    /* JADX DEBUG: Multi-variable search result rejected for r1v7, resolved type: boolean */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v28, types: [int] */
    /* JADX WARN: Type inference failed for: r2v42, types: [int] */
    /* renamed from: b */
    public static final void decryptRC4(byte[] bArr, int i, byte[] bArr2, int i2) {
        int[] iArrM1034a = expandRC4Key(bArr, i);
        int i3 = i2 >> 3;
        for (int i4 = 0; i4 < i3; i4++) {
            int i5 = i4 << 3;
            int i6 = ((bArr2[i5] & 0xFF) << 24) | ((bArr2[i5 + 1] & 0xFF) << 16) | ((bArr2[i5 + 2] & 0xFF) << 8) | (bArr2[i5 + 3] & 0xFF);
            int i7 = ((bArr2[i5 + 4] & 0xFF) << 24) | ((bArr2[i5 + 5] & 0xFF) << 16) | ((bArr2[i5 + 6] & 0xFF) << 8) | (bArr2[i5 + 7] & 0xFF);
            int i8 = i6 ^ iArrM1034a[1059];
            int i9 = 16;
            while (i9 > 0) {
                int i10 = i9;
                int i11 = i10 - 1;
                i7 ^= (((iArrM1034a[i8 >>> 24] + iArrM1034a[256 | ((i8 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i8 >>> 8) & 255)]) + iArrM1034a[768 | (i8 & 255)]) ^ iArrM1034a[1042 + i10];
                i9 = i11 - 1;
                i8 ^= (((iArrM1034a[i7 >>> 24] + iArrM1034a[256 | ((i7 >>> 16) & 255)]) ^ iArrM1034a[512 | ((i7 >>> 8) & 255)]) + iArrM1034a[768 | (i7 & 255)]) ^ iArrM1034a[1042 + i11];
            }
            int i12 = i7 ^ iArrM1034a[1042];
            bArr2[i5] = (byte) (i12 >> 24);
            bArr2[i5 + 1] = (byte) (i12 >>> 16);
            bArr2[i5 + 2] = (byte) (i12 >>> 8);
            bArr2[i5 + 3] = (byte) i12;
            bArr2[i5 + 4] = (byte) (i8 >> 24);
            bArr2[i5 + 5] = (byte) (i8 >>> 16);
            bArr2[i5 + 6] = (byte) (i8 >>> 8);
            bArr2[i5 + 7] = (byte) i8;
        }
    }

    /* renamed from: k */
    public static final void flagSyncRequired() {
        synchronized (AppState.getVector(StateKeys.VEC_TILE_QUEUE)) {
            AppState.setInt(StateKeys.FLAG_XMPP_ROSTER_LOADED, 1);
        }
    }

    /* renamed from: l */
    public static final boolean checkAndClearSync() {
        synchronized (AppState.getVector(StateKeys.VEC_TILE_QUEUE)) {
            if (!AppState.getBool(StateKeys.FLAG_XMPP_ROSTER_LOADED)) {
                return false;
            }
            synchronized (AppState.getVector(StateKeys.VEC_TILE_QUEUE)) {
                AppState.setInt(StateKeys.FLAG_XMPP_ROSTER_LOADED, 0);
            }
            return true;
        }
    }

    /* renamed from: c */
    public static final Object[] getContactInfoFromState(int i) {
        return addContactInfoToQueue(AppController.getUrlComponents(AppState.getString(i)));
    }

    /* renamed from: a */
    public static final Object[] addContactInfoToQueue(Object[] objArr) {
        RemoteLogger.log("XGRP", "addContactInfoToQueue");
        if (objArr != null) {
            Vector vectorM614m = AppState.getVector(StateKeys.VEC_TILE_QUEUE);
            synchronized (vectorM614m) {
                if (!vectorM614m.contains(objArr)) {
                    vectorM614m.addElement(objArr);
                }
                flagSyncRequired();
            }
        }
        return objArr;
    }

    /* renamed from: b */
    public static final void removeContactInfoFromQueue(Object[] objArr) {
        if (objArr != null) {
            Vector vectorM614m = AppState.getVector(StateKeys.VEC_TILE_QUEUE);
            synchronized (vectorM614m) {
                if (vectorM614m.contains(objArr)) {
                    vectorM614m.removeElement(objArr);
                    flagSyncRequired();
                }
            }
        }
    }

    /* renamed from: a */
    public static final void addMapPointIfNew(Vector vector, MapPoint c0014an, int i, int i2) {
        if (c0014an == null || vector.contains(c0014an)) {
            return;
        }
        if (null != findMapPointByName(vector, c0014an.name)) {
            return;
        }
        if (i2 > 0 && vector.size() >= i2) {
            vector.removeElementAt(0);
        }
        vector.insertElementAt(c0014an, 0);
    }

    /* renamed from: a */
    private static MapPoint findMapPointByName(Vector vector, String str) {
        MapPoint c0014an;
        try {
            int size = vector.size();
            do {
                size--;
                if (size < 0) {
                    return null;
                }
                c0014an = (MapPoint) vector.elementAt(size);
            } while (!str.equals(c0014an.name));
            return c0014an;
        } catch (Exception unused) {
            return null;
        }
    }

    /* renamed from: a */
    public static final Vector parseMapPointsFromStr(String str) {
        Vector vectorM1213g = ObjectPool.newVector();
        try {
            Vector vectorM513a = Utils.splitReplace(str, '\r', '\n');
            int size = vectorM513a.size();
            for (int i = 0; i < size; i++) {
                Vector vectorM516c = Utils.splitNonEmpty((String) vectorM513a.elementAt(i), '|');
                MapPoint c0014an = new MapPoint((String) vectorM516c.elementAt(0), Long.parseLong((String) vectorM516c.elementAt(2)), Long.parseLong((String) vectorM516c.elementAt(1)), Utils.parseInt(vectorM516c.elementAt(3)));
                c0014an.height = 1;
                c0014an.typeCode = Utils.parseInt(vectorM516c.elementAt(4));
                c0014an.objectCode = Utils.parseInt(vectorM516c.elementAt(5));
                vectorM1213g.addElement(c0014an);
                ObjectPool.releaseVector(vectorM516c);
            }
            ObjectPool.releaseVector(vectorM513a);
            Utils.trimIfEmpty(vectorM1213g);
        } catch (Throwable unused) {
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public static final void saveMapPoints(Vector vector, int i) {
        try {
            ByteBuffer c0043n = new ByteBuffer();
            int size = vector.size();
            c0043n.writeIntLE(size);
            for (int i2 = 0; i2 < size; i2++) {
                MapPoint c0014an = (MapPoint) vector.elementAt(i2);
                c0043n.writeStringUTF16(c0014an.name).writeLong(c0014an.boundsMinLon).writeLong(c0014an.boundsMinLat).writeLong(c0014an.boundsMaxLon).writeLong(c0014an.boundsMaxLat).writeLong(c0014an.longitude).writeLong(c0014an.latitude).writeIntLE(c0014an.zoomLevel).writeIntLE(c0014an.height).writeIntLE(c0014an.objectCode).writeIntLE(c0014an.typeCode);
            }
            AppState.setObject(i, (Object) c0043n.toBase64());
        } catch (Throwable unused) {
        }
    }

    /* renamed from: d */
    public static final Vector loadMapPoints(int i) {
        Vector vectorM1213g = ObjectPool.newVector();
        try {
            ByteBuffer c0043nM986d = Base64.decode(AppState.getString(i));
            if (c0043nM986d.length > 4) {
                int iM1328e = c0043nM986d.readInt();
                for (int i2 = 0; i2 < iM1328e; i2++) {
                    vectorM1213g.addElement(new MapPoint(c0043nM986d));
                }
            }
            Utils.trimIfEmpty(vectorM1213g);
        } catch (Throwable unused) {
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public static final void startMapAnimation(Vector vector) {
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((MapPoint) vector.elementAt(size)).markInactive();
            }
        }
    }

    /* renamed from: b */
    public static final void stopMapAnimation(Vector vector) {
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((MapPoint) vector.elementAt(size)).markActive();
            }
        }
    }
}
