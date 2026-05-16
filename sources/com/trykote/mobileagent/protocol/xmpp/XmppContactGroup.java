package com.trykote.mobileagent.protocol.xmpp;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapPoint;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.net.ServiceRegistry;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.ConnectionThread;
import com.trykote.mobileagent.protocol.ProtocolFactory;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.MrimCommand;
import com.trykote.mobileagent.protocol.mrim.MrimContact;
import com.trykote.mobileagent.protocol.mrim.MrimContactGroup;
import com.trykote.mobileagent.util.Base64;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import java.util.Hashtable;
import java.util.Vector;

public final class XmppContactGroup extends ContactGroup {

    // Emoticon count and threshold
    private static final int EMOTICON_COUNT = 78;
    private static final int EMOTICON_INLINE_LIMIT = 42;
    private static final int EMOTICON_ICON_74 = 410;
    private static final int EMOTICON_ICON_75 = 412;
    private static final int EMOTICON_ICON_76 = 417;
    private static final int EMOTICON_ICON_77 = 432;
    private static final int EMOTICON_ICON_BASE_OFFSET = 258;

    // Periodic sync interval and sleep
    private static final long SYNC_INTERVAL_MS = 7200000L;
    private static final long SYNC_POLL_SLEEP_MS = 3072L;
    private static final long CONNECT_POLL_SLEEP_MS = 100L;
    private static final long AUTH_WAIT_SLEEP_MS = 5000L;

    // DNS buffer size
    private static final int DNS_BUFFER_SIZE = 512;

    // Image cache layout
    private static final int IMAGE_CACHE_DATA_OFFSET = 29;
    private static final int IMAGE_CACHE_EXPIRE_THRESHOLD = 16;
    private static final int IMAGE_CACHE_FULL_EXPIRE = 32;
    private static final int IMAGE_CACHE_SMALL_PREFIX = 26;

    // SHA-256 block size
    private static final int SHA256_BLOCK_SIZE = 64;
    private static final int SHA256_DIGEST_SIZE = 32;
    private static final int SHA256_ROUNDS = 8;

    // HMAC padding bytes
    private static final byte HMAC_IPAD = 54;
    private static final byte HMAC_OPAD = 92;

    // Map data freshness
    private static final long MAP_DATA_FRESHNESS_MS = 45000L;

    // Blowfish constants
    private static final int BLOWFISH_ROUNDS = 16;
    private static final int BLOWFISH_SUBKEYS_OFFSET = 1024;
    private static final int BLOWFISH_SBOX_SIZE = 256;
    private static final int BLOWFISH_P_OFFSET = 1042;
    private static final int BLOWFISH_P_LAST = 1059;

    private int groupTypeId;

    public static Vector sharedContactList;

    public static long lastUpdateTs;

    public static long lastCheckTs;

    public XmppContactGroup(XmppProtocol protocol, int typeId, String name) {
        super(protocol);
        this.groupTypeId = typeId;
        setNameIfChanged(name);
    }

    public XmppContactGroup(XmppProtocol protocol, ByteBuffer buffer) {
        super(protocol);
        setNameIfChanged(buffer.readUTF8Str((String) null));
        this.groupTypeId = buffer.readInt();
        int contactCount = buffer.readInt();
        for (int ci = contactCount - 1; ci >= 0; ci--) {
            addContact((Object) new XmppContact(protocol, buffer));
        }
        this.isSpecial = buffer.readBoolean();
    }

    public XmppContactGroup() {
        super(null);
    }

    @Override // p000.ContactGroup
    public final void serialize(ByteBuffer buffer, boolean includeContacts) {
        buffer.writeStringUTF16(this.name);
        buffer.writeIntLE(this.groupTypeId);
        super.serialize(buffer, includeContacts);
    }

    @Override // p000.ContactGroup
    public final boolean isCustom() {
        return this.groupTypeId <= 0;
    }

    @Override // p000.ContactGroup
    public final int getGroupType() {
        return this.groupTypeId;
    }

    public static final ByteBuffer createContactAddCommand(MrimAccount account, MrimContact contact, String messageText, long timestamp) {
        Object[] commandArgs = new Object[4];
        ByteBuffer packet = new ByteBuffer().writeIntLE(0).writeStringLatin1(contact.simpleIdentifier);
        Hashtable emoticonLookup = new Hashtable();
        for (int ei = EMOTICON_COUNT - 1; ei >= 0; ei--) {
            String emoticonName = ResourceAccessor.blockStr(StringResKeys.EMOTICON_NAMES_BASE, ei);
            emoticonLookup.put(emoticonName, StringUtils.intern(emoticonName.toLowerCase()));
        }
        String triggerChars = ResourceAccessor.str(PackedStringKeys.EMOTICON_TRIGGER_CHARS);
        StringBuffer result = ObjectPool.newStringBuffer();
        int textLen = messageText.length();
        int pos = 0;
        while (pos < textLen) {
            char ch = messageText.charAt(pos);
            int emoticonIndex = findSpecialCharIndex(triggerChars, messageText, pos, emoticonLookup);
            if (emoticonIndex < 0) {
                result.append(ch);
            } else {
                if (emoticonIndex < EMOTICON_INLINE_LIMIT) {
                    result.append(ResourceAccessor.str(PackedStringKeys.EMOTICON_TAG_PREFIX)).append(Utils.zeroPad(emoticonIndex)).append('>');
                } else {
                    result.append(ResourceAccessor.str(PackedStringKeys.EMOTICON_OPEN_TAG)).append(emoticonIndex < 74 ? emoticonIndex + EMOTICON_ICON_BASE_OFFSET : emoticonIndex == 74 ? EMOTICON_ICON_74 : emoticonIndex == 75 ? EMOTICON_ICON_75 : emoticonIndex == 76 ? EMOTICON_ICON_76 : EMOTICON_ICON_77).append(ResourceAccessor.str(PackedStringKeys.EMOTICON_ALT_ATTR)).append(ResourceAccessor.blockStr(StringResKeys.EMOTICON_NAMES_BASE, emoticonIndex)).append(ResourceAccessor.str(PackedStringKeys.EMOTICON_CLOSE_TAG));
                }
                pos += ResourceAccessor.blockStr(StringResKeys.EMOTICON_NAMES_BASE, emoticonIndex).length() - 1;
            }
            pos++;
        }
        commandArgs[0] = ProtocolFactory.createMrimPacket(account, MrimCommand.CS_MESSAGE, packet.writeStringUTF16(ObjectPool.toStringAndRelease(result)).writeIntLE(0));
        commandArgs[1] = ObjectPool.integerOf(MrimAccount.RESP_AUTH);
        commandArgs[2] = contact;
        commandArgs[3] = new Long(timestamp);
        return account.createAndQueueCommand(commandArgs);
    }

    private static final int findSpecialCharIndex(String triggerChars, String text, int offset, Hashtable emoticonLookup) {
        String emoticonName;
        if (text.length() <= 0 || triggerChars.indexOf(text.charAt(offset)) < 0) {
            return -1;
        }
        for (int ei = EMOTICON_COUNT - 1; ei >= 0; ei--) {
            emoticonName = ResourceAccessor.blockStr(StringResKeys.EMOTICON_NAMES_BASE, ei);
            if (text.indexOf(emoticonName, offset) == offset) {
                return ei;
            }
            if (text.indexOf((String) emoticonLookup.get(emoticonName), offset) == offset) {
                return ei;
            }
        }
        return -1;
    }

    private static void updateLastCheckTime() {
        SessionState.setTimestampLastXmppAuth(System.currentTimeMillis());
    }

    public static final void periodicTimeSync() throws Throwable {
        while (true) {
            Thread.sleep(SYNC_POLL_SLEEP_MS);
            if (AppController.isShuttingDown) {
                throw new Throwable();
            }
            if (System.currentTimeMillis() - SessionState.getTimestampLastXmppAuth() >= SYNC_INTERVAL_MS) {
                boolean hasXmppOnly = false;
                Vector accounts = AccountManager.copyAllAccounts();
                for (int si = accounts.size() - 1; si >= 0; si--) {
                    Account account = (Account) accounts.elementAt(si);
                    if (account.isConnected()) {
                        if (account instanceof MrimAccount) {
                            hasXmppOnly = false;
                            updateLastCheckTime();
                            break;
                        }
                        hasXmppOnly = true;
                    }
                }
                ObjectPool.releaseVector(accounts);
                if (hasXmppOnly) {
                    authenticateAndSync(establishSecureConn(extractPlainText(establishSecureConn(ResourceAccessor.str(PackedStringKeys.HOST_MRIM_REDIRECT)))));
                }
            }
        }
    }

    private static final ConnectionThread establishSecureConn(String host) throws Throwable {
        int connState;
        ConnectionThread connection = new ConnectionThread(host);
        do {
            Thread.sleep(CONNECT_POLL_SLEEP_MS);
            connState = connection.getState();
        } while (connState == ConnectionThread.STATE_CONNECTING);
        if (connState != ConnectionThread.STATE_CONNECTED) {
            connection.state = ConnectionThread.STATE_CLOSING;
        }
        return connection;
    }

    private static final String extractPlainText(ConnectionThread connection) {
        int readLen;
        int dataLen;
        try {
            ByteBuffer buffer = new ByteBuffer();
            do {
                Thread.sleep(CONNECT_POLL_SLEEP_MS);
                connection.drainInput(buffer);
                readLen = buffer.length;
                dataLen = readLen;
            } while (readLen == 0);
            StringBuffer result = ObjectPool.newStringBuffer();
            for (int idx = dataLen - 1; idx >= 0; idx--) {
                char ch = (char) buffer.readByte();
                if (Utils.isDigitOrSep(ch)) {
                    result.append(ch);
                }
            }
            String plainText = ObjectPool.toStringAndRelease(result);
            if (connection != null) {
                connection.state = ConnectionThread.STATE_CLOSING;
            }
            return plainText;
        } catch (RuntimeException th) {
            if (connection != null) {
                connection.state = ConnectionThread.STATE_CLOSING;
            }
            throw th;
        } catch (Throwable th) {
            if (connection != null) {
                connection.state = ConnectionThread.STATE_CLOSING;
            }
            throw new RuntimeException(th.toString());
        }
    }

    private static final void authenticateAndSync(ConnectionThread connection) {
        ByteBuffer responsePacket;
        try {
            String tag = ResourceAccessor.str(PackedStringKeys.TAG_STATISTICS);
            MrimAccount account = new MrimAccount(-1, tag, tag);
            account.connection = connection;
            account.sendData(ProtocolFactory.createMrimAuthPacket(account));
            ByteBuffer inputBuffer = new ByteBuffer();
            do {
                Thread.sleep(CONNECT_POLL_SLEEP_MS);
                connection.drainInput(inputBuffer);
                responsePacket = inputBuffer.extractPNG();
            } while (responsePacket == null);
            if (responsePacket.peekIntAt(12) == MrimCommand.CS_HELLO_ACK) {
                updateLastCheckTime();
                account.sendData(ProtocolFactory.createMrimPacket(account, MrimCommand.CS_LOGIN2, new ByteBuffer().writeStringLatin1(account.login).writeStringLatin1(account.password).writeCompressed(PackedStringKeys.MMP_AGENT_ID).writeStringLatin1(buildAuthData()).writeBuffer(buildSyncPayload(account))));
                Thread.sleep(AUTH_WAIT_SLEEP_MS);
            }
        } catch (Throwable unused) {
        } finally {
            if (connection != null) {
                connection.state = ConnectionThread.STATE_CLOSING;
            }
        }
    }

    private static final int[] getSHA256Constants() {
        return (int[]) AppState.getObject(StringResKeys.RES_EMOTICON_MAP);
    }

    private static final int rotateLeft(int value, int shift) {
        return (value >>> shift) | (value << (32 - shift));
    }

    private static final void writeIntToBytes(int value, byte[] dest, int offset) {
        dest[offset] = (byte) (value >> 24);
        dest[offset + 1] = (byte) (value >>> 16);
        dest[offset + 2] = (byte) (value >>> 8);
        dest[offset + 3] = (byte) value;
    }

    private static final Object[] initHashState() {
        Object[] state = {new int[10], ObjectPool.newBytes(128)};
        int[] hashValues = (int[]) state[0];
        int[] constants = getSHA256Constants();
        for (int hi = SHA256_ROUNDS - 1; hi >= 0; hi--) {
            hashValues[hi] = constants[hi];
        }
        return state;
    }

    private static final Object[] updateHashBuffer(Object[] state, byte[] data, int dataLen) {
        int[] hashValues = (int[]) state[0];
        byte[] blockBuffer = (byte[]) state[1];
        int buffered = hashValues[9];
        int copyLen = Utils.min(dataLen, SHA256_BLOCK_SIZE - buffered);
        System.arraycopy(data, 0, blockBuffer, buffered, copyLen);
        if (buffered + dataLen < SHA256_BLOCK_SIZE) {
            hashValues[9] = buffered + dataLen;
        } else {
            processSHA256Block(state, blockBuffer, 0, 1);
            int remaining = dataLen - copyLen;
            int fullBlocks = remaining >> 6;
            processSHA256Block(state, data, copyLen, fullBlocks);
            int processedOffset = copyLen + (fullBlocks << 6);
            int tailLen = remaining & 63;
            System.arraycopy(data, processedOffset, blockBuffer, 0, tailLen);
            hashValues[9] = tailLen;
            hashValues[SHA256_ROUNDS] = hashValues[SHA256_ROUNDS] + ((fullBlocks + 1) << 6);
        }
        return state;
    }

    private static final byte[] finalizeSHA256(Object[] state) {
        int[] hashValues = (int[]) state[0];
        int buffered = hashValues[9];
        int paddingBlocks = 55 < (buffered & 63) ? 2 : 1;
        int totalBits = (hashValues[SHA256_ROUNDS] + buffered) << 3;
        byte[] blockBuffer = (byte[]) state[1];
        int paddingLen = paddingBlocks << 6;
        for (int ci = paddingLen - 1; ci >= buffered; ci--) {
            blockBuffer[ci] = 0;
        }
        blockBuffer[buffered] = -128;
        writeIntToBytes(totalBits, blockBuffer, paddingLen - 4);
        processSHA256Block(state, blockBuffer, 0, paddingBlocks);
        byte[] digest = new byte[SHA256_DIGEST_SIZE];
        for (int di = SHA256_ROUNDS - 1; di >= 0; di--) {
            writeIntToBytes(hashValues[di], digest, di << 2);
        }
        ObjectPool.releaseBytes(blockBuffer);
        return digest;
    }

    private static final void processSHA256Block(Object[] state, byte[] data, int offset, int blockCount) {
        int[] hashValues = (int[]) state[0];
        int[] schedule = new int[SHA256_BLOCK_SIZE];
        int[] working = new int[SHA256_ROUNDS];
        int[] constants = getSHA256Constants();
        for (int bi = 0; bi < blockCount; bi++) {
            int wi = 0;
            do {
                int bytePos = offset + (bi << 6) + (wi << 2);
                schedule[wi] = (data[bytePos] << 24) | ((data[bytePos + 1] & 255) << 16) | ((data[bytePos + 2] & 255) << 8) | (data[bytePos + 3] & 255);
                wi++;
            } while (wi < BLOWFISH_ROUNDS);
            do {
                int si = wi;
                int w2 = schedule[si - 2];
                int sigma1 = ((rotateLeft(w2, 17) ^ rotateLeft(w2, 19)) ^ (w2 >>> 10)) + schedule[si - 7];
                int w15 = schedule[si - 15];
                schedule[si] = sigma1 + ((rotateLeft(w15, 7) ^ rotateLeft(w15, 18)) ^ (w15 >>> 3)) + schedule[si - 16];
                wi++;
            } while (wi < SHA256_BLOCK_SIZE);
            for (int ci = SHA256_ROUNDS - 1; ci >= 0; ci--) {
                working[ci] = hashValues[ci];
            }
            int ri = 0;
            do {
                int h = working[7];
                int e = working[4];
                int sum1 = h + ((rotateLeft(e, 6) ^ rotateLeft(e, 11)) ^ rotateLeft(e, 25));
                int eCopy = working[4];
                int t1 = sum1 + ((eCopy & working[5]) ^ ((eCopy ^ (-1)) & working[6])) + constants[ri + SHA256_ROUNDS] + schedule[ri];
                int a = working[0];
                int sum0 = (rotateLeft(a, 2) ^ rotateLeft(a, 13)) ^ rotateLeft(a, 22);
                int aCopy = working[0];
                int b = working[1];
                int c = working[2];
                working[7] = working[6];
                working[6] = working[5];
                working[5] = working[4];
                working[4] = working[3] + t1;
                working[3] = working[2];
                working[2] = working[1];
                working[1] = working[0];
                working[0] = t1 + sum0 + (((aCopy & b) ^ (aCopy & c)) ^ (b & c));
                ri++;
            } while (ri < SHA256_BLOCK_SIZE);
            int mi = 0;
            do {
                int idx = mi;
                hashValues[idx] = hashValues[idx] + working[mi];
                mi++;
            } while (mi < SHA256_ROUNDS);
        }
    }

    public static final byte[] hmacSHA256(byte[] key, int keyLen, byte[] data, int dataOffset, int dataLen) {
        int effectiveKeyLen;
        byte[] innerPad = ObjectPool.newBytes(SHA256_BLOCK_SIZE);
        byte[] outerPad = ObjectPool.newBytes(SHA256_BLOCK_SIZE);
        if (keyLen == SHA256_BLOCK_SIZE) {
            effectiveKeyLen = SHA256_BLOCK_SIZE;
        } else {
            if (keyLen > SHA256_BLOCK_SIZE) {
                effectiveKeyLen = SHA256_DIGEST_SIZE;
                key = finalizeSHA256(updateHashBuffer(initHashState(), key, keyLen));
            } else {
                effectiveKeyLen = keyLen;
            }
            for (int pi = SHA256_BLOCK_SIZE - 1; pi >= effectiveKeyLen; pi--) {
                innerPad[pi] = HMAC_IPAD;
                outerPad[pi] = HMAC_OPAD;
            }
        }
        for (int ki = effectiveKeyLen - 1; ki >= 0; ki--) {
            innerPad[ki] = (byte) (key[ki] ^ HMAC_IPAD);
            outerPad[ki] = (byte) (key[ki] ^ HMAC_OPAD);
        }
        Object[] innerState = updateHashBuffer(initHashState(), innerPad, SHA256_BLOCK_SIZE);
        ObjectPool.releaseBytes(innerPad);
        Object[] outerState = updateHashBuffer(initHashState(), outerPad, SHA256_BLOCK_SIZE);
        ObjectPool.releaseBytes(outerPad);
        Object[] states = {innerState, outerState};
        updateHashBuffer((Object[]) states[0], data, dataOffset);
        return finalizeSHA256(updateHashBuffer((Object[]) states[1], finalizeSHA256((Object[]) states[0]), SHA256_DIGEST_SIZE));
    }

    public static final ByteBuffer buildSyncPayload(MrimAccount mrimAccount) {
        ByteBuffer payload = new ByteBuffer().writeIntMixed(515).writeIntLE(Utils.parseInt((Object) Utils.defaultStr(SessionState.getRandomId()))).writeIntMixed(300).writeStringLatin1(Utils.defaultStr(SessionState.getSessionKey())).writeIntMixed(513).writeIntLE(mrimAccount.syncSeq).writeIntMixed(335).writeStringLatin1(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(UIState.getScreenWidth()).append('x').append(UIState.getScreenHeight()))).writeIntMixed(592).writeIntLE(MapState.getAndClearCacheMissCount()).writeIntMixed(573).writeIntLE(MapState.getAndClearCacheHitCount()).writeIntMixed(636).writeIntLE(SessionState.getAndClearScreenOpens()).writeIntMixed(514).writeIntLE(SessionState.getAndClearAppStarts()).writeIntMixed(638).writeIntLE(SessionState.getAndClearErrors()).writeIntMixed(639).writeIntLE(SessionState.getAndClearReserved()).writeIntMixed(640).writeIntLE(SessionState.getAndClearTotalTraffic());
        Vector accounts = AccountManager.copyAllAccounts();
        for (int si = accounts.size() - 1; si >= 0; si--) {
            Account account = (Account) accounts.elementAt(si);
            if (!(account instanceof MrimAccount)) {
                ByteBuffer payloadRef = payload.writeIntMixed(816);
                ByteBuffer accountPayload = new ByteBuffer().writeIntMixed(515).writeIntLE(Utils.parseInt((Object) Utils.defaultStr(SessionState.getRandomId()))).writeIntMixed(300).writeStringLatin1(Utils.defaultStr(SessionState.getSessionKey())).writeIntMixed(305).writeStringLatin1(account.login).writeIntMixed(306).writeStringLatin1(AppState.getString(account.getSessionStringKey())).writeIntMixed(563).writeIntLE(account.syncSeq).writeIntMixed(564).writeIntLE(account.sentCount).writeIntMixed(565).writeIntLE(account.recvCount);
                account.resetCounters();
                payloadRef.writeBufferIntLen(accountPayload);
            }
        }
        ObjectPool.releaseVector(accounts);
        AppState.saveAllDeltas(true);
        return payload;
    }

    public static final String buildAuthData() {
        return new ByteBuffer().writeCompressed(PackedStringKeys.USERAGENT_MOBILEJME).writeExtendedInt(2098527).writeExtendedInt(2097374).writeLongBytes(4423776686951391594L).writeExtendedInt(2098526).writeUInt(1030516845).writeExtendedInt(2098528).writeUInt(1030712676).writeExtendedInt(2098529).writeUInt(1953653104).writeLongBytes(465624460605L).getStringAndClear();
    }

    private static final Object[] getImageCachePool() {
        return UIState.getGfxContextsArray();
    }

    private static final int[] getImageTimestamps() {
        return UIState.getGfxHeightsArray();
    }

    public static final void incrementCacheCounter() {
        synchronized (getImageCachePool()) {
            UIState.incrementImageCounter();
        }
    }

    public static final void invalidateCachedImage(int slot) {
        Object[] cachePool = getImageCachePool();
        synchronized (cachePool) {
            cachePool[slot] = null;
            cachePool[slot + IMAGE_CACHE_DATA_OFFSET] = null;
        }
    }

    public static final void cleanupExpiredImages() {
        Object[] cachePool = getImageCachePool();
        synchronized (cachePool) {
            int counter = UIState.getImageCounter();
            int[] timestamps = getImageTimestamps();
            for (int si = 28; si >= 0; si--) {
                int age = counter - timestamps[si];
                if (age > IMAGE_CACHE_EXPIRE_THRESHOLD) {
                    cachePool[si] = null;
                    if (age > IMAGE_CACHE_FULL_EXPIRE) {
                        ObjectPool.releaseBytes((byte[]) cachePool[si + IMAGE_CACHE_DATA_OFFSET]);
                        cachePool[si + IMAGE_CACHE_DATA_OFFSET] = null;
                    }
                }
            }
        }
    }

    public static final Image getOrLoadImage(int slot) {
        Object[] cachePool = getImageCachePool();
        synchronized (cachePool) {
            getImageTimestamps()[slot] = UIState.getImageCounter();
            if (cachePool[slot] != null) {
                return (Image) cachePool[slot];
            }
            try {
                byte[] cachedData = (byte[]) cachePool[slot + IMAGE_CACHE_DATA_OFFSET];
                byte[] imageData = cachedData;
                if (cachedData == null) {
                    int dataSlot = slot + IMAGE_CACHE_DATA_OFFSET;
                    byte[] loadedData = new ByteBuffer(ObjectPool.unpackChars(slot < IMAGE_CACHE_SMALL_PREFIX ? 113724026151215L + (slot << 8) : 29113350693019951L + (slot << 16))).toByteArray();
                    imageData = loadedData;
                    cachePool[dataSlot] = loadedData;
                }
                Image image = Image.createImage(imageData, 0, imageData.length);
                cachePool[slot] = image;
                return image;
            } catch (Throwable unused) {
                cachePool[slot + IMAGE_CACHE_DATA_OFFSET] = null;
                cachePool[slot] = null;
                return Image.createImage(1, 1);
            }
        }
    }

    public static final ByteBuffer createContactCommand(MrimAccount account, int contactFlags, String identifier, String displayName, String authMessage, MrimContactGroup group, boolean requestAuth) {
        Object[] commandArgs = new Object[6];
        commandArgs[0] = ProtocolFactory.createMrimPacket(account, MrimCommand.CS_ADD_CONTACT, new ByteBuffer().writeIntLE(contactFlags).writeIntLE(group.serverId).writeStringLatin1(identifier).writeStringUTF16(displayName).writeIntLE(0).writeStringArray(new String[]{account.displayName, authMessage}).writeIntLE(requestAuth ? 1 : 0));
        commandArgs[1] = ObjectPool.integerOf(MrimAccount.RESP_ADD_CONTACT);
        commandArgs[2] = identifier;
        commandArgs[3] = displayName;
        commandArgs[4] = group;
        commandArgs[5] = ObjectPool.integerOf(contactFlags);
        return account.createAndQueueCommand(commandArgs);
    }
    public static final void showTextInputDialog(String title, String initialText, int maxLength, int constraints, String inputMode, int okLabelKey, int cancelLabelKey, CommandListener commandListener) {
        if (initialText != null && initialText.length() > maxLength) {
            initialText = StringUtils.prefix(initialText, maxLength);
        }
        try {
            if (!StringUtils.isKnownDevice1) {
                throw new RuntimeException();
            }
            TextBox textBox = getTextInputBox();
            textBox.setTitle(AppState.emptyStr);
            textBox.setString(AppState.emptyStr);
            textBox.setCommandListener((CommandListener) null);
            textBox.setConstraints(constraints);
            textBox.setTitle(title);
            if (initialText != null) {
                textBox.setString(initialText);
            }
            textBox.setMaxSize(maxLength);
            textBox.setInitialInputMode((String) null);
        } catch (Throwable unused) {
            UIState.setTextBox(new TextBox(title, initialText, maxLength, constraints));
        }
        removePrimaryCommand();
        removeSecondaryCommand();
        try {
            TextBox textBox2 = getTextInputBox();
            if (StringUtils.matchesKey(424, inputMode)) {
                int fontSizeSetting = SettingsState.getFontSizeList();
                if (fontSizeSetting == 1) {
                    textBox2.setInitialInputMode(ResourceAccessor.str(StringResKeys.STR_INPUT_MODE_NUMERIC));
                } else if (fontSizeSetting == 2) {
                    textBox2.setInitialInputMode(ResourceAccessor.str(StringResKeys.STR_INPUT_MODE_LATIN));
                }
            } else {
                textBox2.setInitialInputMode(inputMode);
            }
        } catch (Throwable unused2) {
        }
        RuntimeState.setXmppCommandIndex(okLabelKey);
        Command command = new Command(AppState.getString(okLabelKey), SettingsState.isFullscreen() ? 2 : 4, 0);
        removePrimaryCommand();
        getTextInputBox().addCommand(command);
        RuntimeState.setXmppCommand1(command);
        setCommandLabel(cancelLabelKey);
        getTextInputBox().setCommandListener(commandListener);
        AppState.setScreen(getTextInputBox());
    }

    public static final String getTextInputValue() {
        try {
            return Utils.defaultStr(StringUtils.intern(getTextInputBox().getString()));
        } catch (Throwable unused) {
            return AppState.emptyStr;
        }
    }

    public static final void setTextInputScreen(int selectionIndex, int labelKey) {
        if (RuntimeState.getXmppSelectionIndex() == selectionIndex) {
            setCommandLabel(labelKey);
            AppState.setScreen(getTextInputBox());
        }
    }

    public static final TextBox getTextInputBox() {
        return (TextBox) UIState.getTextBox();
    }

    private static final void removePrimaryCommand() {
        Command command = (Command) RuntimeState.getXmppCommand1();
        if (command != null) {
            getTextInputBox().removeCommand(command);
        }
        RuntimeState.clearXmppCommand1();
    }

    private static final void removeSecondaryCommand() {
        Command command = (Command) RuntimeState.getXmppCommand2();
        if (command != null) {
            getTextInputBox().removeCommand(command);
        }
        RuntimeState.clearXmppCommand2();
    }

    private static final void setCommandLabel(int labelKey) {
        RuntimeState.setXmppSelectionIndex(labelKey);
        Command command = new Command(AppState.getString(labelKey), SettingsState.isFullscreen() ? 4 : 2, 1);
        removeSecondaryCommand();
        getTextInputBox().addCommand(command);
        RuntimeState.setXmppCommand2(command);
    }

    public static final void initializeMapData() {
        lastUpdateTs = System.currentTimeMillis();
        Vector contactIds = ServiceRegistry.getServiceContactIds(1);
        long lon = MapRenderer.currentLon;
        long lat = MapRenderer.currentLat;
        StringBuffer idList = ObjectPool.newStringBuffer();
        for (int si = contactIds.size() - 1; si >= 0; si--) {
            idList.append(contactIds.elementAt(si));
            if (si > 0) {
                idList.append(',');
            }
        }
        ByteBuffer requestUrl = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MAP_POINT_VIEW).writeUInt(15713);
        String contactIdStr = ObjectPool.toStringAndRelease(idList);
        new AsyncTask(AsyncTaskId.FETCH_SHARED_CONTACTS, requestUrl.writeRawString(contactIdStr).writeUInt(4022822).writeRawString(new ByteBuffer().writeRawString(contactIdStr).writeCompressed(PackedStringKeys.SECRET_KEY_389).encryptMD5().toHexString()).writeUInt(4023078).writeLongAsString(lon).writeUInt(4023334).writeLongAsString(lat).getStringAndClear());
    }

    public static final boolean isMapDataRecent() {
        return System.currentTimeMillis() - lastCheckTs < MAP_DATA_FRESHNESS_MS;
    }

    private static final int[] expandBlowfishKey(byte[] key, int keyLen) {
        ByteBuffer sboxData = new ByteBuffer(ObjectPool.unpackChars(24879), 4200);
        int[] subkeys = new int[BLOWFISH_P_LAST + 1];
        System.arraycopy(Utils.bytesToInts(sboxData.data), 0, subkeys, 0, BLOWFISH_P_OFFSET);
        sboxData.clear();
        int keyIdx = 0;
        for (int pi = 0; pi < 18; pi++) {
            int mixed = 0;
            for (int bi = 0; bi < 4; bi++) {
                int ki = keyIdx;
                keyIdx++;
                mixed = (mixed << 8) | (key[ki % keyLen] & 255);
            }
            subkeys[pi + BLOWFISH_SUBKEYS_OFFSET + 18] = subkeys[pi + BLOWFISH_SUBKEYS_OFFSET] ^ mixed;
        }
        long roundResult = performBlowfishRound(subkeys, 0, 0);
        subkeys[BLOWFISH_P_OFFSET] = (int) (roundResult >>> 32);
        subkeys[BLOWFISH_P_OFFSET + 1] = (int) roundResult;
        int pIdx = 2;
        do {
            long pairResult = performBlowfishRound(subkeys, subkeys[((pIdx + BLOWFISH_SUBKEYS_OFFSET) + 18) - 2], subkeys[((pIdx + BLOWFISH_SUBKEYS_OFFSET) + 18) - 1]);
            int lo = pIdx;
            int hi = pIdx + 1;
            subkeys[BLOWFISH_P_OFFSET + lo] = (int) (pairResult >>> 32);
            pIdx = hi + 1;
            subkeys[BLOWFISH_P_OFFSET + hi] = (int) pairResult;
        } while (pIdx != 18);
        long lastResult = performBlowfishRound(subkeys, subkeys[BLOWFISH_P_LAST - 1], subkeys[BLOWFISH_P_LAST]);
        subkeys[0] = (int) (lastResult >>> 32);
        subkeys[1] = (int) lastResult;
        int sIdx = 2;
        do {
            long sboxResult = performBlowfishRound(subkeys, subkeys[sIdx - 2], subkeys[sIdx - 1]);
            int lo = sIdx;
            int hi = sIdx + 1;
            subkeys[lo] = (int) (sboxResult >>> 32);
            sIdx = hi + 1;
            subkeys[hi] = (int) sboxResult;
        } while (sIdx != BLOWFISH_SUBKEYS_OFFSET);
        return subkeys;
    }

    private static final long performBlowfishRound(int[] subkeys, int left, int right) {
        int xl = left ^ subkeys[BLOWFISH_P_OFFSET];
        int round = 0;
        while (round < BLOWFISH_ROUNDS) {
            int nextRound = round + 1;
            right ^= (((subkeys[xl >>> 24] + subkeys[BLOWFISH_SBOX_SIZE | ((xl >>> 16) & 255)]) ^ subkeys[(BLOWFISH_SBOX_SIZE * 2) | ((xl >>> 8) & 255)]) + subkeys[(BLOWFISH_SBOX_SIZE * 3) | (xl & 255)]) ^ subkeys[(nextRound + BLOWFISH_SUBKEYS_OFFSET) + 18];
            round = nextRound + 1;
            xl ^= (((subkeys[right >>> 24] + subkeys[BLOWFISH_SBOX_SIZE | ((right >>> 16) & 255)]) ^ subkeys[(BLOWFISH_SBOX_SIZE * 2) | ((right >>> 8) & 255)]) + subkeys[(BLOWFISH_SBOX_SIZE * 3) | (right & 255)]) ^ subkeys[(round + BLOWFISH_SUBKEYS_OFFSET) + 18];
        }
        return ((right ^ subkeys[BLOWFISH_P_LAST]) << 32) | ((xl << 32) >>> 32);
    }

    public static final void encryptBlowfish(byte[] key, int keyLen, byte[] data, int dataLen) {
        int[] subkeys = expandBlowfishKey(key, keyLen);
        int blockCount = dataLen >> 3;
        for (int bi = 0; bi < blockCount; bi++) {
            int pos = bi << 3;
            int left = ((data[pos] & 0xFF) << 24) | ((data[pos + 1] & 0xFF) << 16) | ((data[pos + 2] & 0xFF) << 8) | (data[pos + 3] & 0xFF);
            int right = ((data[pos + 4] & 0xFF) << 24) | ((data[pos + 5] & 0xFF) << 16) | ((data[pos + 6] & 0xFF) << 8) | (data[pos + 7] & 0xFF);
            int xl = left ^ subkeys[BLOWFISH_P_OFFSET];
            int round = 0;
            while (round < BLOWFISH_ROUNDS) {
                int nextRound = round + 1;
                right ^= (((subkeys[xl >>> 24] + subkeys[BLOWFISH_SBOX_SIZE | ((xl >>> 16) & 255)]) ^ subkeys[(BLOWFISH_SBOX_SIZE * 2) | ((xl >>> 8) & 255)]) + subkeys[(BLOWFISH_SBOX_SIZE * 3) | (xl & 255)]) ^ subkeys[(nextRound + BLOWFISH_SUBKEYS_OFFSET) + 18];
                round = nextRound + 1;
                xl ^= (((subkeys[right >>> 24] + subkeys[BLOWFISH_SBOX_SIZE | ((right >>> 16) & 255)]) ^ subkeys[(BLOWFISH_SBOX_SIZE * 2) | ((right >>> 8) & 255)]) + subkeys[(BLOWFISH_SBOX_SIZE * 3) | (right & 255)]) ^ subkeys[(round + BLOWFISH_SUBKEYS_OFFSET) + 18];
            }
            int encrypted = right ^ subkeys[BLOWFISH_P_LAST];
            data[pos] = (byte) (encrypted >> 24);
            data[pos + 1] = (byte) (encrypted >>> 16);
            data[pos + 2] = (byte) (encrypted >>> 8);
            data[pos + 3] = (byte) encrypted;
            data[pos + 4] = (byte) (xl >> 24);
            data[pos + 5] = (byte) (xl >>> 16);
            data[pos + 6] = (byte) (xl >>> 8);
            data[pos + 7] = (byte) xl;
        }
    }

    public static final void decryptBlowfish(byte[] key, int keyLen, byte[] data, int dataLen) {
        int[] subkeys = expandBlowfishKey(key, keyLen);
        int blockCount = dataLen >> 3;
        for (int bi = 0; bi < blockCount; bi++) {
            int pos = bi << 3;
            int left = ((data[pos] & 0xFF) << 24) | ((data[pos + 1] & 0xFF) << 16) | ((data[pos + 2] & 0xFF) << 8) | (data[pos + 3] & 0xFF);
            int right = ((data[pos + 4] & 0xFF) << 24) | ((data[pos + 5] & 0xFF) << 16) | ((data[pos + 6] & 0xFF) << 8) | (data[pos + 7] & 0xFF);
            int xl = left ^ subkeys[BLOWFISH_P_LAST];
            int round = BLOWFISH_ROUNDS;
            while (round > 0) {
                int curRound = round;
                int prevRound = curRound - 1;
                right ^= (((subkeys[xl >>> 24] + subkeys[BLOWFISH_SBOX_SIZE | ((xl >>> 16) & 255)]) ^ subkeys[(BLOWFISH_SBOX_SIZE * 2) | ((xl >>> 8) & 255)]) + subkeys[(BLOWFISH_SBOX_SIZE * 3) | (xl & 255)]) ^ subkeys[BLOWFISH_P_OFFSET + curRound];
                round = prevRound - 1;
                xl ^= (((subkeys[right >>> 24] + subkeys[BLOWFISH_SBOX_SIZE | ((right >>> 16) & 255)]) ^ subkeys[(BLOWFISH_SBOX_SIZE * 2) | ((right >>> 8) & 255)]) + subkeys[(BLOWFISH_SBOX_SIZE * 3) | (right & 255)]) ^ subkeys[BLOWFISH_P_OFFSET + prevRound];
            }
            int decrypted = right ^ subkeys[BLOWFISH_P_OFFSET];
            data[pos] = (byte) (decrypted >> 24);
            data[pos + 1] = (byte) (decrypted >>> 16);
            data[pos + 2] = (byte) (decrypted >>> 8);
            data[pos + 3] = (byte) decrypted;
            data[pos + 4] = (byte) (xl >> 24);
            data[pos + 5] = (byte) (xl >>> 16);
            data[pos + 6] = (byte) (xl >>> 8);
            data[pos + 7] = (byte) xl;
        }
    }

    public static final void flagSyncRequired() {
        synchronized (MapState.getTileQueue()) {
            UIState.setXmppRosterLoaded(true);
        }
    }

    public static final boolean checkAndClearSync() {
        synchronized (MapState.getTileQueue()) {
            if (!UIState.isXmppRosterLoaded()) {
                return false;
            }
            synchronized (MapState.getTileQueue()) {
                UIState.setXmppRosterLoaded(false);
            }
            return true;
        }
    }

    public static final Object[] getContactInfoFromState(int stateKey) {
        return addContactInfoToQueue(ApiClient.getUrlComponents(AppState.getString(stateKey)));
    }

    public static final Object[] addContactInfoToQueue(Object[] contactInfo) {
        RemoteLogger.log("XGRP", "addContactInfoToQueue");
        if (contactInfo != null) {
            Vector queue = MapState.getTileQueue();
            synchronized (queue) {
                if (!queue.contains(contactInfo)) {
                    queue.addElement(contactInfo);
                }
                flagSyncRequired();
            }
        }
        return contactInfo;
    }

    public static final void removeContactInfoFromQueue(Object[] contactInfo) {
        if (contactInfo != null) {
            Vector queue = MapState.getTileQueue();
            synchronized (queue) {
                if (queue.contains(contactInfo)) {
                    queue.removeElement(contactInfo);
                    flagSyncRequired();
                }
            }
        }
    }

    public static final void addMapPointIfNew(Vector points, MapPoint point, int unused, int maxSize) {
        if (point == null || points.contains(point)) {
            return;
        }
        if (findMapPointByName(points, point.name) != null) {
            return;
        }
        if (maxSize > 0 && points.size() >= maxSize) {
            points.removeElementAt(0);
        }
        points.insertElementAt(point, 0);
    }

    private static MapPoint findMapPointByName(Vector points, String name) {
        try {
            for (int pi = points.size() - 1; pi >= 0; pi--) {
                MapPoint point = (MapPoint) points.elementAt(pi);
                if (name.equals(point.name)) {
                    return point;
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static final Vector parseMapPointsFromStr(String data) {
        Vector points = ObjectPool.newVector();
        try {
            Vector lines = Utils.splitReplace(data, '\r', '\n');
            int lineCount = lines.size();
            for (int li = 0; li < lineCount; li++) {
                Vector fields = Utils.splitNonEmpty((String) lines.elementAt(li), '|');
                MapPoint point = new MapPoint((String) fields.elementAt(0), Long.parseLong((String) fields.elementAt(2)), Long.parseLong((String) fields.elementAt(1)), Utils.parseInt(fields.elementAt(3)));
                point.height = 1;
                point.typeCode = Utils.parseInt(fields.elementAt(4));
                point.objectCode = Utils.parseInt(fields.elementAt(5));
                points.addElement(point);
                ObjectPool.releaseVector(fields);
            }
            ObjectPool.releaseVector(lines);
            Utils.trimIfEmpty(points);
        } catch (Throwable unused) {
        }
        return points;
    }

    public static final void saveMapPoints(Vector points, int stateKey) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            int size = points.size();
            buffer.writeIntLE(size);
            for (int pi = 0; pi < size; pi++) {
                MapPoint point = (MapPoint) points.elementAt(pi);
                buffer.writeStringUTF16(point.name).writeLong(point.boundsMinLon).writeLong(point.boundsMinLat).writeLong(point.boundsMaxLon).writeLong(point.boundsMaxLat).writeLong(point.longitude).writeLong(point.latitude).writeIntLE(point.zoomLevel).writeIntLE(point.height).writeIntLE(point.objectCode).writeIntLE(point.typeCode);
            }
            AppState.setObject(stateKey, (Object) buffer.toBase64());
        } catch (Throwable unused) {
        }
    }

    public static final Vector loadMapPoints(int stateKey) {
        Vector points = ObjectPool.newVector();
        try {
            ByteBuffer buffer = Base64.decode(AppState.getString(stateKey));
            if (buffer.length > 4) {
                int count = buffer.readInt();
                for (int pi = 0; pi < count; pi++) {
                    points.addElement(new MapPoint(buffer));
                }
            }
            Utils.trimIfEmpty(points);
        } catch (Throwable unused) {
        }
        return points;
    }

    public static final void startMapAnimation(Vector points) {
        for (int pi = points.size() - 1; pi >= 0; pi--) {
            ((MapPoint) points.elementAt(pi)).markInactive();
        }
    }

    public static final void stopMapAnimation(Vector points) {
        for (int pi = points.size() - 1; pi >= 0; pi--) {
            ((MapPoint) points.elementAt(pi)).markActive();
        }
    }
}
