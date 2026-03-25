package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Vector;

/* renamed from: i */
/* loaded from: MobileAgent_3.9.jar:i.class */
public final class Conversation implements ListItem {

    /* renamed from: c */
    private int height = 5;

    /* renamed from: b */
    private boolean selected = true;

    /* renamed from: a */
    public Vector items = ObjectPool.newVector();

    /* renamed from: d */
    private SizeCache sizeCache = new SizeCache();

    /* renamed from: a */
    public final void addItem(ListItem item) {
        this.items.addElement(item);
        this.sizeCache.lastScale = -1;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return this.height;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.selected && this.items.size() > 0;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.selected = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.selected = true;
    }

    /* renamed from: d */
    private final ListItem getItem(int i) {
        return (ListItem) this.items.elementAt(i);
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        long sum = 0;
        int size = this.items.size();
        int i = size;
        while (true) {
            i--;
            if (i < 0) {
                return (int) (sum / size);
            }
            sum += getItem(i).getWidth();
        }
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        long sum = 0;
        int size = this.items.size();
        int i = size;
        while (true) {
            i--;
            if (i < 0) {
                return (int) (sum / size);
            }
            sum += getItem(i).getBaseHeight();
        }
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.sizeCache.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.sizeCache.getHeight(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int size = this.items.size();
        if (this.height == 5) {
            sb.append(AppState.getString(StateKeys.STR_CONV_UNREAD_PREFIX)).append(size);
        } else {
            int i = size - 1;
            sb.append(((ListItem) this.items.firstElement()).getText()).append(AppState.getString(StateKeys.STR_CONV_SEPARATOR)).append(i).append(AppState.getString(StateKeys.STR_CONV_SUFFIX_BASE + Utils.pluralForm(i)));
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        return 10;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return true;
    }

    /* renamed from: a */
    public static final void fetchHistory(Object[] objArr) {
        int i;
        MmpProtocol protocol = (MmpProtocol) objArr[0];
        try {
            try {
                NetworkLock.acquireNetworkLock();
                if (((Integer) objArr[1]).intValue() == 0) {
                    protocol.msgCount = 30;
                    AppController.needsRepaint = true;
                    HttpClient httpClient = HttpClient.createHttpClient(AppState.getString(StateKeys.STR_RES_HUGE_URL_5), protocol, 0);
                    httpClient.setRequestMethod(ObjectPool.unpackChars(1414745936));
                    ByteBuffer requestBody = new ByteBuffer().writeCompressed(PackedStringKeys.ICQ_AUTH_PARAMS).writeConversationStr(objArr[2]).writeCompressed(PackedStringKeys.PARAM_PWD).writeConversationStr(objArr[3]);
                    ApiClient.setHeaderFromState(httpClient, 788628, 2164851);
                    httpClient.writeData(requestBody.data, requestBody.length);
                    int responseCode = httpClient.getResponseCode();
                    i = responseCode;
                    if (responseCode == 200) {
                        protocol.msgCount = 40;
                        AppController.needsRepaint = true;
                        XmlElement responseXml = new ByteBuffer(httpClient).parseXmlStr();
                        int statusCode = Integer.parseInt(StringUtils.fromBuffer(responseXml.findChildByKey(PackedStringKeys.TAG_STATUSCODE).textContent));
                        if (statusCode != 200) {
                            if (statusCode == 330) {
                                ((MmpProtocol) objArr[0]).handleComplete();
                                objArr[0] = null;
                            }
                            throw new RuntimeException(StringUtils.intern(Integer.toString(statusCode)));
                        }
                        XmlElement resultElement = responseXml.findChildByKey(PackedStringKeys.TAG_DATA);
                        new AsyncTask(AsyncTaskId.FETCH_HISTORY, new Object[]{objArr[0], ResourceManager.integerOf(1), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_LOGINID).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_TOKEN).findChildByKey(PackedStringKeys.TAG_A).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_HOSTTIME).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_SESSIONSECRET).textContent), objArr[3]});
                        HttpClient.closeAndUpdateStats(httpClient);
                        NetworkLock.releaseNetworkLock();
                        return;
                    }
                } else {
                    protocol.msgCount = 50;
                    AppController.needsRepaint = true;
                    ByteBuffer headerBuffer = new ByteBuffer().writeCompressed(PackedStringKeys.URL_ICQ_OSCAR_SESSION).writeByte(63);
                    String queryStr = new ByteBuffer().writeCompressed(PackedStringKeys.PARAM_A_EQ).writeConversationStr(objArr[3]).writeCompressed(PackedStringKeys.ICQ_OSCAR_PARAMS).writeObjectStr(objArr[4]).readAllByteStr();
                    HttpClient httpClient2 = HttpClient.createMockClient(headerBuffer.writeRawString(queryStr).writeCompressed(PackedStringKeys.PARAM_SIG_SHA256).writeRawString(encryptData(new ByteBuffer().writeCompressed(PackedStringKeys.HTTP_GET_AMP).writeRawString(percentEncodeInternal(AppState.getString(StateKeys.STR_RES_HUGE_URL_8), false)).writeByte(38).writeRawString(percentEncodeInternal(queryStr, false)).readAllByteStr(), encryptData((String) objArr[5], (String) objArr[6]))).readAllByteStr()).sendHttpRequest(0, 5522759, 330359);
                    int responseCode2 = httpClient2.getResponseCode();
                    i = responseCode2;
                    if (responseCode2 == 200) {
                        protocol.msgCount = 60;
                        AppController.needsRepaint = true;
                        XmlElement resultElement2 = httpClient2.readChunkedResponse().parseXmlStr().findChildByKey(PackedStringKeys.TAG_DATA);
                        ((MmpProtocol) objArr[0]).connectionData = new String[]{(String) objArr[2], ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.fromBuffer(resultElement2.findChildByKey(PackedStringKeys.TAG_HOST).textContent)).append(':').append(StringUtils.fromBuffer(resultElement2.findChildByKey(PackedStringKeys.TAG_PORT).textContent))), StringUtils.fromBuffer(resultElement2.findChildByKey(PackedStringKeys.TAG_COOKIE).textContent)};
                        HttpClient.closeAndUpdateStats(httpClient2);
                        NetworkLock.releaseNetworkLock();
                        return;
                    }
                }
                throw new Throwable(StringUtils.intern(Integer.toString(i)));
            } catch (Throwable th) {
                MmpProtocol protocol2 = (MmpProtocol) objArr[0];
                protocol2.lastError = protocol2.getDefaultError();
                IOUtils.postAccountMessage(protocol2, th.toString());
                protocol2.progress = Account.PROGRESS_DISCONNECTED;
                HttpClient.closeAndUpdateStats((HttpClient) null);
                NetworkLock.releaseNetworkLock();
            }
        } catch (Throwable th2) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw th2;
        }
    }

    /* renamed from: a */
    private static final String encryptData(String str, String str2) {
        return new ByteBuffer().setData(XmppContactGroup.hmacSHA256(str2.getBytes(), str2.length(), str.getBytes(), str.length(), 32)).toBase64();
    }

    /* renamed from: a */
    public static final Vector parseConversation(String str) {
        Vector parts = ObjectPool.newVector();
        if (isValidFormat(str)) {
            int i = 0;
            int i2 = 0;
            while (true) {
                try {
                    int idx = str.indexOf(AppState.getString(StateKeys.STR_RES_VERY_LONG_URL_1), i);
                    if (idx < 0) {
                        break;
                    }
                    i2 = idx;
                    if (i != idx) {
                        parts.addElement(StringUtils.substring(str, i, idx));
                    }
                    int idx2 = str.indexOf(32, idx);
                    i = idx2;
                    if (idx2 < 0) {
                        parts.addElement(StringUtils.suffix(str, idx));
                        break;
                    }
                    parts.addElement(StringUtils.substring(str, idx, i));
                } catch (Throwable unused) {
                }
            }
            int idx3 = str.indexOf(32, i2);
            if (idx3 >= 0) {
                parts.addElement(StringUtils.suffix(str, idx3));
            }
        } else {
            parts.addElement(str);
        }
        return parts;
    }

    /* renamed from: b */
    public static final String decodeMessage(String str) {
        try {
            if (!isEncodedFormat(str)) {
                if (isSimpleFormat(str)) {
                    return AppState.getString(StateKeys.STR_CHAT_DEFAULT_TOPIC);
                }
                return null;
            }
            int bodyStart = AppState.indexOf(str, 1031040294);
            int idx = str.indexOf(ObjectPool.unpackChars(1031302438), AppState.indexOf(str, 1031302438) + 4);
            String encoded = idx < 0 ? StringUtils.suffix(str, bodyStart + 4) : StringUtils.substring(str, bodyStart + 4, idx);
            if (StringUtils.matchesEncoded(encoded, 1094795585)) {
                return AppState.emptyStr;
            }
            ByteBuffer decodeBuffer = Base64.decode(replaceText(replaceText(replaceText(encoded, 200762, 65752), 200765, 65547), 200768, 65552));
            StringBuffer sb = ObjectPool.newStringBuffer();
            while (decodeBuffer.length > 0) {
                int b1 = decodeBuffer.readUByte();
                int b2 = b1 > 127 ? decodeBuffer.readUByte() : 0;
                int b3 = b1 > 223 ? decodeBuffer.readUByte() : 0;
                sb.append(b1 < 128 ? (char) b1 : b1 < 224 ? (char) (((b1 - 192) << 6) + (b2 - 128)) : b1 < 240 ? (char) (((b1 - 224) << 12) + ((b2 - 128) << 6) + (b3 - 128)) : (char) (((b1 - 240) << 18) + ((b2 - 128) << 12) + ((b3 - 128) << 6) + ((b1 > 239 ? decodeBuffer.readUByte() : 0) - 128)));
            }
            return ObjectPool.toStringAndRelease(sb);
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: c */
    public static final String extractFrom(String str) {
        try {
            if (isEncodedFormat(str)) {
                return StringUtils.substring(str, AppState.indexOf(str, 1031302438) + 4, AppState.indexOf(str, 1031367974));
            }
            if (isSimpleFormat(str)) {
                return StringUtils.substring(str, AppState.indexOf(str, 4028451) + 3, AppState.indexOf(str, 4028710));
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: d */
    public static final String extractTo(String str) {
        try {
            if (isEncodedFormat(str)) {
                return StringUtils.substring(str, AppState.indexOf(str, 1031367974) + 4, AppState.indexOf(str, 1031040294));
            }
            if (isSimpleFormat(str)) {
                return StringUtils.substring(str, AppState.indexOf(str, 4028710) + 3, AppState.indexOf(str, 4028966));
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: e */
    public static final String extractSubject(String str) {
        try {
            return StringUtils.substring(str, AppState.indexOf(str, 4028966) + 3, AppState.indexOfPool(str, 397364));
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: m */
    private static final boolean isFormatted(String str) {
        return AppState.indexOfPool(str, 1245774) >= 0;
    }

    /* renamed from: a */
    public static final boolean hasKey(String str, int i) {
        return AppState.indexOfPool(str, i) >= 0;
    }

    /* renamed from: b */
    private static final boolean hasFlag(String str, int i) {
        return AppState.indexOf(str, i) >= 0;
    }

    /* renamed from: f */
    public static final boolean isValidFormat(String str) {
        if (!isFormatted(str)) {
            return false;
        }
        if (hasFlag(str, 4028451) && hasFlag(str, 4028710) && hasFlag(str, 4028966)) {
            return true;
        }
        return hasFlag(str, 1031302438) && hasFlag(str, 1031367974) && hasFlag(str, 1031040294);
    }

    /* renamed from: n */
    private static boolean isEncodedFormat(String str) {
        return isFormatted(str) && hasFlag(str, 4028451) && hasFlag(str, 4028710) && hasFlag(str, 4028966) && hasFlag(str, 1031302438) && hasFlag(str, 1031367974) && hasFlag(str, 1031040294);
    }

    /* renamed from: o */
    private static boolean isSimpleFormat(String str) {
        return isFormatted(str) && hasFlag(str, 4028451) && hasFlag(str, 4028710) && hasFlag(str, 4028966) && !hasFlag(str, 1031302438) && !hasFlag(str, 1031367974) && !hasFlag(str, 1031040294);
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:8:0x0038 */
    /* renamed from: a */
    public static final String replaceText(String str, int i, int i2) {
        String searchStr = AppState.getString(i);
        if (str.indexOf(searchStr) < 0) {
            return str;
        }
        String replaceStr = AppState.getString(i2);
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = 0;
        while (true) {
            int i3 = length;
            int idx = str.indexOf(searchStr, i3);
            if (idx < 0) {
                return ObjectPool.toStringAndRelease(sb.append(StringUtils.suffix(str, i3)));
            }
            sb.append(StringUtils.substring(str, i3, idx)).append(replaceStr);
            length = idx + searchStr.length();
        }
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:13:0x00be */
    /* renamed from: a */
    public static final void handleMessage(MrimAccount account, ByteBuffer buffer, long j) {
        MrimContact contact;
        int i;
        int idx;
        int msgId = buffer.readInt();
        int flags = buffer.readInt();
        String sender = buffer.readHexStr();
        String rawBody = buffer.readStringByMode(flags & 2097160);
        String messageText = null;
        String str = null;
        if ((flags & 8) == 0) {
            String decoded = decodeHtmlEntities(rawBody);
            StringBuffer sb = ObjectPool.newStringBuffer();
            String openTag = AppState.getString(StateKeys.STR_RES_PROTOCOL_TAG_6);
            String midTag = AppState.getString(StateKeys.STR_RES_HEADER_1);
            String closeTag = AppState.getString(StateKeys.STR_RES_XMPP_TAG_1);
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= decoded.length()) {
                    break;
                }
                int idx2 = decoded.indexOf(openTag, i3);
                if (idx2 < 0) {
                    sb.append(StringUtils.suffix(decoded, i3));
                    break;
                }
                sb.append(StringUtils.substring(decoded, i3, idx2));
                int idx3 = decoded.indexOf(midTag, idx2 + 10);
                if (idx3 < 0 || (idx = decoded.indexOf(closeTag, (i = idx3 + 6))) < 0) {
                    break;
                }
                sb.append(StringUtils.substring(decoded, i, idx));
                i2 = idx + 9;
            }
            messageText = ObjectPool.toStringAndRelease(sb);
        } else {
            ByteBuffer decodeBuffer = Base64.decode(rawBody);
            int encodingFlag = flags & 2097152;
            int partCount = decodeBuffer.readInt();
            String[] strArr = new String[partCount];
            for (int i5 = 0; i5 < partCount; i5++) {
                strArr[i5] = decodeBuffer.readStringByMode(encodingFlag);
            }
            decodeBuffer.clear();
            str = strArr[1];
        }
        if ((flags & 128) != 0) {
            buffer.readWideStr();
        }
        if ((flags & 4194304) != 0) {
            if ((flags & 17408) != 0) {
                return;
            }
            buffer.readInt();
            switch (buffer.readInt()) {
                case 0:
                    account.receivePrivateMessage(sender, messageText, buffer.readUTF8Str((String) null), buffer.readWideStr(), j);
                    break;
                case 2:
                    buffer.readUTF8Str((String) null);
                    buffer.readInt();
                    Vector members = ObjectPool.newVector();
                    int memberCount = buffer.readInt();
                    while (true) {
                        memberCount--;
                        if (memberCount < 0) {
                            AppState.pool[StateKeys.SLOT_REG_PARAM_4] = members;
                            break;
                        } else {
                            members.addElement(buffer.readWideStr());
                        }
                    }
                case 3:
                    account.receiveGroupMessage(sender, AppState.getString(StateKeys.STR_GROUP_MESSAGE), buffer.readUTF8Str((String) null), buffer.readWideStr(), buffer, j);
                    break;
                case 5:
                    account.receivePrivateMessage(sender, AppState.getString(StateKeys.STR_PRIVATE_MESSAGE), buffer.readUTF8Str((String) null), buffer.readWideStr(), j);
                    break;
            }
            return;
        }
        boolean isNotify = (flags & 2048) != 0;
        boolean isGroupMsg = (flags & 8192) != 0;
        if ((flags & 4) == 0) {
            account.trySendData(ProtocolFactory.createMrimPacket(account, 4113, new ByteBuffer().writeStringLatin1((isGroupMsg || isNotify) ? AppState.getString(StateKeys.STR_RES_LONG_URL_2) : sender).writeIntLE(msgId)));
        }
        if (isGroupMsg) {
            Enumeration elements = account.contactMap.elements();
            while (true) {
                if (!elements.hasMoreElements()) {
                    contact = null;
                    break;
                }
                MrimContact candidate = (MrimContact) elements.nextElement();
                if (candidate.isInGroup(sender) && candidate != null) {
                    contact = candidate;
                    break;
                }
            }
            MrimContact groupContact = contact;
            if (contact != null) {
                groupContact.receiveMessageFull(0L, messageText, 1);
                return;
            }
            return;
        }
        MrimContact foundContact = account.findContactByIdentifier(sender);
        if ((flags & 8) != 0) {
            if (foundContact == null) {
                ResourceManager.playNotificationSound(3);
                account.onMessage(sender, 0L, str);
                return;
            } else if ((foundContact.statusFlags & 65536) == 0) {
                foundContact.performAction();
                account.trySendData(ProtocolFactory.createPasswordAuthCmd(account, sender));
                return;
            } else {
                ResourceManager.playNotificationSound(3);
                account.onMessage(sender, 0L, str);
                return;
            }
        }
        if ((foundContact == null || foundContact.hasUnread() || foundContact.isOnline()) && !((flags & 1024) == 0 && (flags & 16384) == 0)) {
            return;
        }
        if ((flags & 16384) != 0) {
            account.onMessage(sender, j, AppState.getString(StateKeys.STR_CONFERENCE_INVITE));
        } else if ((flags & 1024) != 0) {
            account.deleteContact(sender);
        } else {
            account.onMessage(sender, j, messageText);
        }
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:17:0x0074 */
    /* renamed from: p */
    private static final String decodeHtmlEntities(String str) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        String entityPrefix = AppState.getString(StateKeys.STR_RES_LONG_LABEL_2);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= str.length()) {
                break;
            }
            int idx = str.indexOf(entityPrefix, i2);
            if (idx >= 0) {
                sb.append(StringUtils.substring(str, i2, idx));
                int i3 = idx + 13;
                int idx2 = str.indexOf(62, i3);
                if (idx2 < 0) {
                    break;
                }
                try {
                    int entityId = Integer.parseInt(StringUtils.substring(str, i3, idx2));
                    if (entityId < 42 && entityId >= 0) {
                        sb.append(AppState.getString(entityId + 1063));
                    }
                } catch (Throwable unused) {
                }
                i = idx2 + 1;
            } else {
                sb.append(StringUtils.suffix(str, i2));
                break;
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: a */
    public static final void parseContactList(MrimAccount account, ByteBuffer buffer) {
        MrimContactGroup group;
        account.lastError = account.configFlags;
        account.removeAllContacts();
        int status = buffer.readInt();
        if (status == 0) {
            int groupCount = buffer.readInt();
            String groupFormat = buffer.readWideStr();
            String contactFormat = buffer.readWideStr();
            Vector groups = account.groups;
            int formatLen = groupFormat.length();
            for (int i = 0; i < groupCount; i++) {
                int groupFlags = buffer.readInt();
                String groupName = buffer.readUTF8Str((String) null);
                if ((groupFlags & 1) == 0) {
                    groups.addElement(new MrimContactGroup(account, i, groupFlags, groupName));
                }
                for (int i2 = 2; i2 < formatLen; i2++) {
                    if (groupFormat.charAt(i2) == 'u') {
                        buffer.readInt();
                    } else {
                        buffer.readWideStr();
                    }
                }
            }
            int contactId = 20;
            Vector groups2 = account.groups;
            int contactFormatLen = contactFormat.length();
            groups2.size();
            String phoneSuffix = AppState.getString(StateKeys.STR_PHONE_SUFFIX);
            String botSuffix = AppState.getString(StateKeys.STR_BOT_SUFFIX);
            while (buffer.length > 0) {
                int contactFlags = buffer.readInt();
                int groupId = buffer.readInt();
                String contactAddr = buffer.readHexStr();
                String addr = contactAddr;
                String nickname = buffer.readUTF8Str(contactAddr);
                int serverFlags = buffer.readInt();
                int statusVal = buffer.readInt();
                String phonesRaw = buffer.readWideStr();
                ByteBuffer phoneBuf = new ByteBuffer();
                if (phonesRaw != null) {
                    for (int i4 = 0; i4 < phonesRaw.length(); i4++) {
                        char ch = phonesRaw.charAt(i4);
                        if ((ch == ',' && phoneBuf.length > 0) || (ch >= '0' && ch <= '9')) {
                            phoneBuf.writeByte(ch);
                        }
                    }
                }
                String phones = phoneBuf.getStringAndClear();
                String statusText = buffer.readWideStr();
                buffer.readUTF8Str((String) null);
                buffer.readUTF8Str((String) null);
                buffer.readInt();
                String clientId = buffer.readWideStr();
                if (StringUtils.equals(addr, phoneSuffix) || (contactFlags & 1048576) != 0) {
                    addr = phoneSuffix;
                    contactFlags = (contactFlags | 1048576) & (-29);
                    if (StringUtils.isEmpty(phones)) {
                        contactFlags |= 1;
                    }
                }
                if (addr.endsWith(botSuffix)) {
                    contactFlags |= 128;
                    phones = AppState.emptyStr;
                }
                int cleanFlags = contactFlags & (-65537);
                if (0 == (cleanFlags & 1)) {
                    Vector groupList = account.groups;
                    int size = groupList.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            group = null;
                            break;
                        }
                        MrimContactGroup candidate = (MrimContactGroup) groupList.elementAt(size);
                        if (candidate.serverId == groupId) {
                            group = candidate;
                            break;
                        }
                    }
                    MrimContactGroup targetGroup = group;
                    if (group == null) {
                        targetGroup = account.getFirstContactGroup();
                    }
                    targetGroup.addContact((Object) new MrimContact(account, contactId, cleanFlags, groupId, addr, nickname, serverFlags, statusVal, phones, statusText, clientId));
                }
                contactId++;
                for (int i6 = 12; i6 < contactFormatLen; i6++) {
                    if (i6 == 18) {
                        account.receiveProfileData(addr, buffer.readBufferArray());
                    } else if (contactFormat.charAt(i6) == 'u') {
                        buffer.readInt();
                    } else {
                        buffer.readWideStr();
                    }
                }
            }
            account.progress = Account.PROGRESS_CONNECTED;
            account.msgCount = 100;
            account.setConfiguration(account.configFlags);
            account.trySendData(ProtocolFactory.createMrimPacket(account, 4228, new ByteBuffer().writeVector((Vector) null).writeVector((Vector) null)));
            if (account.syncSeq == 1) {
                String searchQuery = StringUtils.intern(Utils.defaultStr(AppState.getString(StateKeys.SLOT_SESSION_TOKEN)).toLowerCase());
                if (!StringUtils.isEmpty(searchQuery)) {
                    new AsyncTask(AsyncTaskId.WAIT_FOR_COMPLETION, new Object[]{searchQuery, account});
                }
                if (AccountManager.getActiveScreenId() == 1) {
                    AppState.setInt(StateKeys.FLAG_CONVERSATION_ACTIVE, 1);
                }
            }
        } else {
            IOUtils.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_STATUS_CHANGED)).append(status)));
            account.closeConnection();
            account.lastError = account.getDefaultError();
            account.markAllRead();
        }
        DiagnosticReporter.checkCrashReport();
    }

    /* renamed from: h */
    public static final String encodeAlternate(String str) {
        return encodeDecodeInternal(str, 959, 960);
    }

    /* renamed from: i */
    public static final String decodeAlternate(String str) {
        return encodeDecodeInternal(str, 960, 959);
    }

    /* renamed from: b */
    private static final String encodeDecodeInternal(String str, int i, int i2) {
        String sourceChars = AppState.getString(i);
        String targetChars = AppState.getString(i2);
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str.length();
        for (int i3 = 0; i3 < length; i3++) {
            char ch = str.charAt(i3);
            int idx = sourceChars.indexOf(ch);
            sb.append(idx < 0 ? ch : targetChars.charAt(idx));
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: a */
    public static final String urlEncode(Object obj) {
        String string = obj.toString().toString();
        StringBuffer sb = ObjectPool.newStringBuffer();
        AppState.getString(StateKeys.STR_RES_DASH_SEPARATOR);
        AppState.getString(StateKeys.STR_RES_SPACE_DASH_SPACE);
        AppState.getString(StateKeys.STR_RES_FIELD_NAME_1);
        AppState.getString(StateKeys.STR_RES_FIELD_NAME_2);
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char ch = string.charAt(i);
            if (ch <= '(' || ch >= 128) {
                sb.append('%').append(Integer.toHexString(ch));
            } else if (ch == '[' || ch == ']') {
                sb.append('%').append(Integer.toHexString(ch));
            } else {
                sb.append(ch);
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: b */
    public static final String urlEncodeCyrillic(Object obj) {
        String string = obj.toString();
        StringBuffer sb = ObjectPool.newStringBuffer();
        String hexPrefixLo = AppState.getString(StateKeys.STR_RES_DASH_SEPARATOR);
        String hexPrefixHi = AppState.getString(StateKeys.STR_RES_SPACE_DASH_SPACE);
        String yoUpper = AppState.getString(StateKeys.STR_RES_FIELD_NAME_1);
        String yoLower = AppState.getString(StateKeys.STR_RES_FIELD_NAME_2);
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char ch = string.charAt(i);
            if (ch == 1025) {
                sb.append(yoUpper);
            } else if (ch == 1105) {
                sb.append(yoLower);
            } else if (ch >= 1040 && ch <= 1071) {
                sb.append(hexPrefixLo).append(Integer.toHexString(ch - 896));
            } else if (ch >= 1072 && ch <= 1087) {
                sb.append(hexPrefixLo).append(Integer.toHexString(ch - 896));
            } else if (ch >= 1088 && ch <= 1103) {
                sb.append(hexPrefixHi).append(Integer.toHexString(ch - 960));
            } else if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || ((ch >= 'A' && ch <= 'Z') || ch == '.')) {
                sb.append(ch);
            } else {
                sb.append('%').append(Integer.toHexString((ch >> 4) & 15)).append(Integer.toHexString(ch & 15));
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: a */
    public static String formatNumber(int i, int i2) {
        String numStr = StringUtils.intern(Integer.toString(i));
        int length = numStr.length();
        if (length >= 2) {
            return numStr;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int i3 = length; i3 < 2; i3++) {
            sb.append('0');
        }
        sb.append(numStr);
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: j */
    public static final String decodeHtmlSpecial(String str) {
        Vector entityNames = Utils.splitByNull(AppState.getString(StateKeys.STR_RES_API_URL_7));
        Vector entityValues = Utils.splitByNull(AppState.getString(StateKeys.STR_RES_COMMAND_1));
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str.length();
        int length2 = 0;
        while (length2 < length) {
            char ch = str.charAt(length2);
            if (ch == '&') {
                boolean found = false;
                for (int i = 0; i < 4 && !found; i++) {
                    try {
                        String entityName = (String) entityNames.elementAt(i);
                        if (str.startsWith(entityName, length2)) {
                            length2 += entityName.length() - 1;
                            sb.append(entityValues.elementAt(i));
                            found = true;
                        }
                    } catch (Throwable unused) {
                    }
                }
                if (!found) {
                    sb.append(ch);
                }
            } else {
                sb.append(ch);
            }
            length2++;
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: k */
    public static final String transliterateRussian(String str) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        Vector translitTable = Utils.splitByNull(AppState.getString(StateKeys.STR_RES_MEGA_URL_3));
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            int tableIdx = (ch < 1072 || ch > 1103) ? ch == 1105 ? 32 : (ch < 1040 || ch > 1071) ? ch == 1025 ? 72 : -1 : (ch - 1040) + 40 : ch - 1072;
            if (tableIdx >= 40) {
                sb.append(Utils.getVectorString(translitTable, tableIdx - 40).toUpperCase());
            } else if (tableIdx >= 0) {
                sb.append(translitTable.elementAt(tableIdx));
            } else {
                sb.append(ch);
            }
        }
        ObjectPool.releaseVector(translitTable);
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: l */
    public static final String percentEncode(String str) {
        return percentEncodeInternal(str, true);
    }

    /* renamed from: a */
    private static final String percentEncodeInternal(String str, boolean z) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || ((ch >= 'a' && ch <= 'z') || ((ch >= '0' && ch <= '9') || ch == '.' || (ch == '-' && !z)))) {
                sb.append(ch);
            } else if (z) {
                sb.append('%').append(Integer.toHexString(ch >> 4)).append(Integer.toHexString(ch & 15));
            } else {
                sb.append('%').append(Integer.toHexString(ch >> 4).toUpperCase()).append(Integer.toHexString(ch & 15).toUpperCase());
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: a */
    public static final void setMapEnabled(boolean z) {
        MapRenderer.needsRedraw = true;
        AppState.setBool(StateKeys.MAP_GPS_ENABLED, z);
    }

    /* renamed from: a */
    public static final void incrementZoom() {
        MapRenderer.setZoom(AppState.getInt(StateKeys.MAP_ZOOM_LEVEL) + 1);
    }

    /* renamed from: b */
    public static final void decrementZoom() {
        MapRenderer.setZoom(AppState.getInt(StateKeys.MAP_ZOOM_LEVEL) - 1);
    }

    /* renamed from: c */
    public static final void loadContacts() {
        new AsyncTask(AsyncTaskId.FETCH_MMP_ROUTE, MmpContact.buildLocationString());
    }

    /* renamed from: c */
    public static final void updateStatusText(int i) {
        AppState.setFromBuffer(StateKeys.SLOT_ACTIVE_PROTOCOL_NAME, ObjectPool.newStringBuffer().append(AppState.getString(i)).append(' ').append('(').append(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE).size()).append(')'));
    }
}
