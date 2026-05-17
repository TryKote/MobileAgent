package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.net.HttpClient;
import com.trykote.mobileagent.net.NetworkLock;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.ProtocolFactory;
import com.trykote.mobileagent.map.RouteData;
import com.trykote.mobileagent.protocol.mmp.MmpProtocol;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.MrimContact;
import com.trykote.mobileagent.protocol.mrim.MrimContactGroup;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.util.CryptoUtils;
import com.trykote.mobileagent.ui.SizeCache;
import com.trykote.mobileagent.util.Base64;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.DiagnosticReporter;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;
import com.trykote.mobileagent.util.XmlElement;

import java.util.Enumeration;
import java.util.Vector;

public final class Conversation implements ListItem {

    // Conversation height type (collapsed summary mode)
    public static final int HEIGHT_COLLAPSED = 5;

    // ICQ login progress states
    public static final int PROGRESS_AUTH_STARTED = 30;
    public static final int PROGRESS_AUTH_COMPLETE = 40;
    public static final int PROGRESS_SESSION_STARTED = 50;
    public static final int PROGRESS_SESSION_COMPLETE = 60;
    public static final int PROGRESS_DONE = 100;

    // HTTP status codes
    private static final int HTTP_OK = 200;
    private static final int HTTP_REDIRECT = 330;

    // HMAC-SHA256 output length
    private static final int HMAC_SHA256_LENGTH = 32;

    // UTF-8 byte boundaries for manual decoding
    private static final int UTF8_1BYTE_MAX = 128;
    private static final int UTF8_2BYTE_MAX = 224;
    private static final int UTF8_3BYTE_MAX = 240;
    private static final int UTF8_2BYTE_OFFSET = 192;
    private static final int UTF8_CONTINUATION_OFFSET = 128;

    // MRIM message flags
    public static final int FLAG_ENCODING_MASK = 2097160;
    public static final int FLAG_MULTIPART = 8;
    public static final int FLAG_ENCODING = 2097152;
    public static final int FLAG_RTF = 128;
    public static final int FLAG_EXTENDED = 4194304;
    public static final int FLAG_SYSTEM_MASK = 17408;
    public static final int FLAG_NOTIFY = 2048;
    public static final int FLAG_GROUP = 8192;
    public static final int FLAG_NO_ACK = 4;
    public static final int FLAG_REMOVE = 1024;
    public static final int FLAG_CONFERENCE = 16384;

    // Contact flags
    private static final int FLAG_AUTHORIZED = 65536;
    private static final int FLAG_PHONE = 1048576;
    private static final int FLAG_DELETED = 1;
    private static final int FLAG_BOT = 128;

    // Contact list parsing
    private static final int INITIAL_CONTACT_ID = 20;
    private static final int FORMAT_EXTRA_FIELDS_START = 12;
    private static final int FORMAT_PROFILE_FIELD_INDEX = 18;

    // Emoticon constants
    private static final int MAX_EMOTICON_ID = 42;
    private static final int ENTITY_PREFIX_LENGTH = 13;

    // Emoticon tag parsing offsets
    private static final int OPEN_TAG_LENGTH = 10;
    private static final int ALT_ATTR_LENGTH = 6;
    private static final int CLOSE_TAG_LENGTH = 9;

    // Contact list command count
    private static final int COMMAND_COUNT = 10;

    private int height = HEIGHT_COLLAPSED;

    private boolean selected = true;

    public Vector items = ObjectPool.newVector();

    private SizeCache sizeCache = new SizeCache();

    public final void addItem(ListItem item) {
        this.items.addElement(item);
        this.sizeCache.lastScale = -1;
    }

    @Override // p000.ListItem
    public final int getHeight() {
        return this.height;
    }

    @Override // p000.ListItem
    public final boolean isSelected() {
        return this.selected && this.items.size() > 0;
    }

    @Override // p000.ListItem
    public final void select() {
        this.selected = false;
    }

    @Override // p000.ListItem
    public final void deselect() {
        this.selected = true;
    }

    private final ListItem getItem(int index) {
        return (ListItem) this.items.elementAt(index);
    }

    @Override // p000.ListItem
    public final int getWidth() {
        long sum = 0;
        int size = this.items.size();
        for (int i = size - 1; i >= 0; i--) {
            sum += getItem(i).getWidth();
        }
        return (int) (sum / size);
    }

    @Override // p000.ListItem
    public final int getBaseHeight() {
        long sum = 0;
        int size = this.items.size();
        for (int i = size - 1; i >= 0; i--) {
            sum += getItem(i).getBaseHeight();
        }
        return (int) (sum / size);
    }

    @Override // p000.ListItem
    public final int getCommandId(int index) {
        return this.sizeCache.getWidth(index, this);
    }

    @Override // p000.ListItem
    public final int executeCommand(int index) {
        return this.sizeCache.getHeight(index, this);
    }

    @Override // p000.ListItem
    public final String getText() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int size = this.items.size();
        if (this.height == HEIGHT_COLLAPSED) {
            sb.append(StringPool.get(StringResKeys.STR_CONV_UNREAD_PREFIX)).append(size);
        } else {
            int i = size - 1;
            sb.append(((ListItem) this.items.firstElement()).getText()).append(StringPool.get(StringResKeys.STR_CONV_SEPARATOR)).append(i).append(StringPool.get(StringResKeys.STR_CONV_SUFFIX_BASE + Utils.pluralForm(i)));
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    @Override // p000.ListItem
    public final int getCommandCount() {
        return COMMAND_COUNT;
    }

    @Override // p000.ListItem
    public final boolean isHighlighted() {
        return true;
    }

    public static final void fetchHistory(Object[] params) {
        int responseCode;
        MmpProtocol protocol = (MmpProtocol) params[0];
        try {
            try {
                NetworkLock.acquireNetworkLock();
                if (((Integer) params[1]).intValue() == 0) {
                    protocol.msgCount = PROGRESS_AUTH_STARTED;
                    AppController.needsRepaint = true;
                    HttpClient httpClient = HttpClient.createHttpClient(StringPool.get(PackedStringKeys.URL_ICQ_CLIENT_LOGIN), protocol, 0);
                    httpClient.setRequestMethod(ObjectPool.unpackChars(1414745936));
                    ByteBuffer requestBody = new ByteBuffer().writeCharBytes("devId=ic122ravsLx0z-5F&f=xml&idType=ICQ&s=").writeRawString(percentEncode((String) params[2])).writeCharBytes("&pwd=").writeRawString(percentEncode((String) params[3]));
                    ApiClient.setHeaderFromState(httpClient, 788628, 2164851);
                    httpClient.writeData(requestBody.data, requestBody.length);
                    responseCode = httpClient.getResponseCode();
                    if (responseCode == HTTP_OK) {
                        protocol.msgCount = PROGRESS_AUTH_COMPLETE;
                        AppController.needsRepaint = true;
                        XmlElement responseXml = new ByteBuffer(httpClient).parseXmlStr();
                        int statusCode = Integer.parseInt(StringUtils.fromBuffer(responseXml.findChildByKey(PackedStringKeys.TAG_STATUSCODE).textContent));
                        if (statusCode != HTTP_OK) {
                            if (statusCode == HTTP_REDIRECT) {
                                ((MmpProtocol) params[0]).handleComplete();
                                params[0] = null;
                            }
                            throw new RuntimeException(StringUtils.intern(Integer.toString(statusCode)));
                        }
                        XmlElement resultElement = responseXml.findChildByKey(PackedStringKeys.TAG_DATA);
                        new AsyncTask(AsyncTaskId.FETCH_HISTORY, new Object[]{params[0], ObjectPool.integerOf(1), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_LOGINID).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_TOKEN).findChildByKey(PackedStringKeys.TAG_A).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_HOSTTIME).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(PackedStringKeys.TAG_SESSIONSECRET).textContent), params[3]});
                        HttpClient.closeAndUpdateStats(httpClient);
                        NetworkLock.releaseNetworkLock();
                        return;
                    }
                } else {
                    protocol.msgCount = PROGRESS_SESSION_STARTED;
                    AppController.needsRepaint = true;
                    ByteBuffer headerBuffer = new ByteBuffer().writeCharBytes("http://api.icq.net:5190/aim/startOSCARSession").writeByte(63);
                    String queryStr = new ByteBuffer().writeCharBytes("a=").writeRawString(percentEncode((String) params[3])).writeCharBytes("&buildNumber=1000&clientName=Mail.ru%20J2ME%20Agent&clientVersion=1000&distId=20200&f=xml&k=ic122ravsLx0z-5F&majorVersion=32&minorVersion=0&pointVersion=0&port=5190&ts=").writeObjectStr((String) params[4]).readAllByteStr();
                    HttpClient httpClient2 = HttpClient.createMockClient(headerBuffer.writeRawString(queryStr).writeCharBytes("&sig_sha256=").writeRawString(encryptData(new ByteBuffer().writeCharBytes("GET&").writeRawString(percentEncodeInternal(StringPool.get(PackedStringKeys.URL_ICQ_OSCAR_SESSION), false)).writeByte(38).writeRawString(percentEncodeInternal(queryStr, false)).readAllByteStr(), encryptData((String) params[5], (String) params[6]))).readAllByteStr()).sendHttpRequest(0, 5522759, 330359);
                    responseCode = httpClient2.getResponseCode();
                    if (responseCode == HTTP_OK) {
                        protocol.msgCount = PROGRESS_SESSION_COMPLETE;
                        AppController.needsRepaint = true;
                        XmlElement resultElement2 = httpClient2.readChunkedResponse().parseXmlStr().findChildByKey(PackedStringKeys.TAG_DATA);
                        ((MmpProtocol) params[0]).connectionData = new String[]{(String) params[2], ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.fromBuffer(resultElement2.findChildByKey(PackedStringKeys.TAG_HOST).textContent)).append(':').append(StringUtils.fromBuffer(resultElement2.findChildByKey(PackedStringKeys.TAG_PORT).textContent))), StringUtils.fromBuffer(resultElement2.findChildByKey(PackedStringKeys.TAG_COOKIE).textContent)};
                        HttpClient.closeAndUpdateStats(httpClient2);
                        NetworkLock.releaseNetworkLock();
                        return;
                    }
                }
                throw new Throwable(StringUtils.intern(Integer.toString(responseCode)));
            } catch (Throwable th) {
                MmpProtocol protocol2 = (MmpProtocol) params[0];
                protocol2.lastError = protocol2.getDefaultError();
                EventDispatcher.postAccountMessage(protocol2, th.toString());
                protocol2.progress = Account.PROGRESS_DISCONNECTED;
                HttpClient.closeAndUpdateStats((HttpClient) null);
                NetworkLock.releaseNetworkLock();
            }
        } catch (RuntimeException e) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw e;
        } catch (Error e) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw e;
        }
    }

    private static final String encryptData(String data, String key) {
        return new ByteBuffer().setData(CryptoUtils.hmacSHA256(key.getBytes(), key.length(), data.getBytes(), data.length(), HMAC_SHA256_LENGTH)).toBase64();
    }

    public static final Vector parseConversation(String text) {
        Vector parts = ObjectPool.newVector();
        if (isValidFormat(text)) {
            int searchFrom = 0;
            int lastUrlStart = 0;
            while (true) {
                try {
                    int idx = text.indexOf(StringPool.get(PackedStringKeys.URL_MAPS_MAIL_RU), searchFrom);
                    if (idx < 0) {
                        break;
                    }
                    lastUrlStart = idx;
                    if (searchFrom != idx) {
                        parts.addElement(StringUtils.substring(text, searchFrom, idx));
                    }
                    int spaceIdx = text.indexOf(32, idx);
                    searchFrom = spaceIdx;
                    if (spaceIdx < 0) {
                        parts.addElement(StringUtils.suffix(text, idx));
                        break;
                    }
                    parts.addElement(StringUtils.substring(text, idx, searchFrom));
                } catch (Throwable unused) {
                }
            }
            int trailingSpace = text.indexOf(32, lastUrlStart);
            if (trailingSpace >= 0) {
                parts.addElement(StringUtils.suffix(text, trailingSpace));
            }
        } else {
            parts.addElement(text);
        }
        return parts;
    }

    public static final String decodeMessage(String text) {
        try {
            if (!isEncodedFormat(text)) {
                if (isSimpleFormat(text)) {
                    return StringPool.get(StringResKeys.STR_CHAT_DEFAULT_TOPIC);
                }
                return null;
            }
            int bodyStart = StringUtils.indexOfPacked(text, 1031040294);
            int idx = text.indexOf(ObjectPool.unpackChars(1031302438), StringUtils.indexOfPacked(text, 1031302438) + 4);
            String encoded = idx < 0 ? StringUtils.suffix(text, bodyStart + 4) : StringUtils.substring(text, bodyStart + 4, idx);
            if (StringUtils.matchesEncoded(encoded, 1094795585)) {
                return AppState.emptyStr;
            }
            ByteBuffer decodeBuffer = Base64.decode(replaceText(replaceText(replaceText(encoded, 200762, 65752), 200765, 65547), 200768, 65552));
            StringBuffer sb = ObjectPool.newStringBuffer();
            while (decodeBuffer.length > 0) {
                int b1 = decodeBuffer.readUByte();
                int b2 = b1 >= UTF8_1BYTE_MAX ? decodeBuffer.readUByte() : 0;
                int b3 = b1 >= UTF8_2BYTE_MAX ? decodeBuffer.readUByte() : 0;
                sb.append(b1 < UTF8_1BYTE_MAX ? (char) b1 : b1 < UTF8_2BYTE_MAX ? (char) (((b1 - UTF8_2BYTE_OFFSET) << 6) + (b2 - UTF8_CONTINUATION_OFFSET)) : b1 < UTF8_3BYTE_MAX ? (char) (((b1 - UTF8_2BYTE_MAX) << 12) + ((b2 - UTF8_CONTINUATION_OFFSET) << 6) + (b3 - UTF8_CONTINUATION_OFFSET)) : (char) (((b1 - UTF8_3BYTE_MAX) << 18) + ((b2 - UTF8_CONTINUATION_OFFSET) << 12) + ((b3 - UTF8_CONTINUATION_OFFSET) << 6) + ((b1 >= UTF8_3BYTE_MAX ? decodeBuffer.readUByte() : 0) - UTF8_CONTINUATION_OFFSET)));
            }
            return ObjectPool.toStringAndRelease(sb);
        } catch (Throwable unused) {
            return null;
        }
    }

    public static final String extractFrom(String text) {
        try {
            if (isEncodedFormat(text)) {
                return StringUtils.substring(text, StringUtils.indexOfPacked(text, 1031302438) + 4, StringUtils.indexOfPacked(text, 1031367974));
            }
            if (isSimpleFormat(text)) {
                return StringUtils.substring(text, StringUtils.indexOfPacked(text, 4028451) + 3, StringUtils.indexOfPacked(text, 4028710));
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    public static final String extractTo(String text) {
        try {
            if (isEncodedFormat(text)) {
                return StringUtils.substring(text, StringUtils.indexOfPacked(text, 1031367974) + 4, StringUtils.indexOfPacked(text, 1031040294));
            }
            if (isSimpleFormat(text)) {
                return StringUtils.substring(text, StringUtils.indexOfPacked(text, 4028710) + 3, StringUtils.indexOfPacked(text, 4028966));
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    public static final String extractSubject(String text) {
        try {
            return StringUtils.substring(text, StringUtils.indexOfPacked(text, 4028966) + 3, StringUtils.indexOfPoolString(text, 397364));
        } catch (Throwable unused) {
            return null;
        }
    }

    private static final boolean isFormatted(String text) {
        return StringUtils.indexOfPoolString(text, PackedStringKeys.URL_MAPS_MAIL_RU) >= 0;
    }

    public static final boolean hasKey(String text, int key) {
        return StringUtils.indexOfPoolString(text, key) >= 0;
    }

    private static final boolean hasFlag(String text, int packedKey) {
        return StringUtils.indexOfPacked(text, packedKey) >= 0;
    }

    public static final boolean isValidFormat(String text) {
        if (!isFormatted(text)) {
            return false;
        }
        if (hasFlag(text, 4028451) && hasFlag(text, 4028710) && hasFlag(text, 4028966)) {
            return true;
        }
        return hasFlag(text, 1031302438) && hasFlag(text, 1031367974) && hasFlag(text, 1031040294);
    }

    private static boolean isEncodedFormat(String text) {
        return isFormatted(text) && hasFlag(text, 4028451) && hasFlag(text, 4028710) && hasFlag(text, 4028966) && hasFlag(text, 1031302438) && hasFlag(text, 1031367974) && hasFlag(text, 1031040294);
    }

    private static boolean isSimpleFormat(String text) {
        return isFormatted(text) && hasFlag(text, 4028451) && hasFlag(text, 4028710) && hasFlag(text, 4028966) && !hasFlag(text, 1031302438) && !hasFlag(text, 1031367974) && !hasFlag(text, 1031040294);
    }

    public static final String replaceText(String text, int searchKey, int replaceKey) {
        String searchStr = AppState.getString(searchKey);
        if (text.indexOf(searchStr) < 0) {
            return text;
        }
        String replaceStr = AppState.getString(replaceKey);
        StringBuffer sb = ObjectPool.newStringBuffer();
        int pos = 0;
        while (true) {
            int idx = text.indexOf(searchStr, pos);
            if (idx < 0) {
                return ObjectPool.toStringAndRelease(sb.append(StringUtils.suffix(text, pos)));
            }
            sb.append(StringUtils.substring(text, pos, idx)).append(replaceStr);
            pos = idx + searchStr.length();
        }
    }

    public static final void handleMessage(MrimAccount account, ByteBuffer buffer, long timestamp) {
        MrimContact contact;
        int altStart;
        int closeIdx;
        int msgId = buffer.readInt();
        int flags = buffer.readInt();
        String sender = buffer.readHexStr();
        String rawBody = buffer.readStringByMode(flags & FLAG_ENCODING_MASK);
        String messageText = null;
        String multipartBody = null;
        if ((flags & FLAG_MULTIPART) == 0) {
            String decoded = decodeHtmlEntities(rawBody);
            StringBuffer sb = ObjectPool.newStringBuffer();
            String openTag = StringPool.get(PackedStringKeys.EMOTICON_OPEN_TAG);
            String midTag = StringPool.get(PackedStringKeys.EMOTICON_ALT_ATTR);
            String closeTag = StringPool.get(PackedStringKeys.EMOTICON_CLOSE_TAG);
            int pos = 0;
            while (true) {
                if (pos >= decoded.length()) {
                    break;
                }
                int tagIdx = decoded.indexOf(openTag, pos);
                if (tagIdx < 0) {
                    sb.append(StringUtils.suffix(decoded, pos));
                    break;
                }
                sb.append(StringUtils.substring(decoded, pos, tagIdx));
                int midIdx = decoded.indexOf(midTag, tagIdx + OPEN_TAG_LENGTH);
                if (midIdx < 0 || (closeIdx = decoded.indexOf(closeTag, (altStart = midIdx + ALT_ATTR_LENGTH))) < 0) {
                    break;
                }
                sb.append(StringUtils.substring(decoded, altStart, closeIdx));
                pos = closeIdx + CLOSE_TAG_LENGTH;
            }
            messageText = ObjectPool.toStringAndRelease(sb);
        } else {
            ByteBuffer decodeBuffer = Base64.decode(rawBody);
            int encodingFlag = flags & FLAG_ENCODING;
            int partCount = decodeBuffer.readInt();
            String[] parts = new String[partCount];
            for (int p = 0; p < partCount; p++) {
                parts[p] = decodeBuffer.readStringByMode(encodingFlag);
            }
            decodeBuffer.clear();
            multipartBody = parts[1];
        }
        if ((flags & FLAG_RTF) != 0) {
            buffer.readWideStr();
        }
        if ((flags & FLAG_EXTENDED) != 0) {
            if ((flags & FLAG_SYSTEM_MASK) != 0) {
                return;
            }
            buffer.readInt();
            switch (buffer.readInt()) {
                case 0:
                    account.receivePrivateMessage(sender, messageText, buffer.readUTF8Str((String) null), buffer.readWideStr(), timestamp);
                    break;
                case 2:
                    buffer.readUTF8Str((String) null);
                    buffer.readInt();
                    Vector members = ObjectPool.newVector();
                    int memberCount = buffer.readInt();
                    for (int k = memberCount - 1; k >= 0; k--) {
                        members.addElement(buffer.readWideStr());
                    }
                    RegistrationState.setParam4(members);
                case 3:
                    account.receiveGroupMessage(sender, StringPool.get(StringResKeys.STR_GROUP_MESSAGE), buffer.readUTF8Str((String) null), buffer.readWideStr(), buffer, timestamp);
                    break;
                case 5:
                    account.receivePrivateMessage(sender, StringPool.get(StringResKeys.STR_PRIVATE_MESSAGE), buffer.readUTF8Str((String) null), buffer.readWideStr(), timestamp);
                    break;
            }
            return;
        }
        boolean isNotify = (flags & FLAG_NOTIFY) != 0;
        boolean isGroupMsg = (flags & FLAG_GROUP) != 0;
        if ((flags & FLAG_NO_ACK) == 0) {
            account.trySendData(ProtocolFactory.createMrimPacket(account, 4113, new ByteBuffer().writeStringLatin1((isGroupMsg || isNotify) ? StringPool.get(PackedStringKeys.MRIM_SMS_ADDRESS) : sender).writeIntLE(msgId)));
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
        if ((flags & FLAG_MULTIPART) != 0) {
            if (foundContact == null) {
                NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CONVERSATION_MESSAGE);
                account.onMessage(sender, 0L, multipartBody);
                return;
            } else if ((foundContact.statusFlags & FLAG_AUTHORIZED) == 0) {
                foundContact.performAction();
                account.trySendData(ProtocolFactory.createPasswordAuthCmd(account, sender));
                return;
            } else {
                NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CONVERSATION_MESSAGE);
                account.onMessage(sender, 0L, multipartBody);
                return;
            }
        }
        if ((foundContact == null || foundContact.hasUnread() || foundContact.isOnline()) && !((flags & FLAG_REMOVE) == 0 && (flags & FLAG_CONFERENCE) == 0)) {
            return;
        }
        if ((flags & FLAG_CONFERENCE) != 0) {
            account.onMessage(sender, timestamp, StringPool.get(StringResKeys.STR_CONFERENCE_INVITE));
        } else if ((flags & FLAG_REMOVE) != 0) {
            account.deleteContact(sender);
        } else {
            account.onMessage(sender, timestamp, messageText);
        }
    }

    private static final String decodeHtmlEntities(String html) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        String entityPrefix = StringPool.get(PackedStringKeys.EMOTICON_TAG_PREFIX);
        int pos = 0;
        while (true) {
            if (pos >= html.length()) {
                break;
            }
            int idx = html.indexOf(entityPrefix, pos);
            if (idx >= 0) {
                sb.append(StringUtils.substring(html, pos, idx));
                int numStart = idx + ENTITY_PREFIX_LENGTH;
                int endIdx = html.indexOf(62, numStart);
                if (endIdx < 0) {
                    break;
                }
                try {
                    int entityId = Integer.parseInt(StringUtils.substring(html, numStart, endIdx));
                    if (entityId < MAX_EMOTICON_ID && entityId >= 0) {
                        sb.append(StringPool.get(StringResKeys.EMOTICON_NAMES_BASE + entityId));
                    }
                } catch (Throwable unused) {
                }
                pos = endIdx + 1;
            } else {
                sb.append(StringUtils.suffix(html, pos));
                break;
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

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
                if ((groupFlags & FLAG_DELETED) == 0) {
                    groups.addElement(new MrimContactGroup(account, i, groupFlags, groupName));
                }
                for (int fi = 2; fi < formatLen; fi++) {
                    if (groupFormat.charAt(fi) == 'u') {
                        buffer.readInt();
                    } else {
                        buffer.readWideStr();
                    }
                }
            }
            int contactId = INITIAL_CONTACT_ID;
            Vector groups2 = account.groups;
            int contactFormatLen = contactFormat.length();
            groups2.size();
            String phoneSuffix = StringPool.get(StringResKeys.STR_PHONE_SUFFIX);
            String botSuffix = StringPool.get(StringResKeys.STR_BOT_SUFFIX);
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
                    for (int ci = 0; ci < phonesRaw.length(); ci++) {
                        char ch = phonesRaw.charAt(ci);
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
                if (StringUtils.equals(addr, phoneSuffix) || (contactFlags & FLAG_PHONE) != 0) {
                    addr = phoneSuffix;
                    contactFlags = (contactFlags | FLAG_PHONE) & (-29);
                    if (StringUtils.isEmpty(phones)) {
                        contactFlags |= FLAG_DELETED;
                    }
                }
                if (addr.endsWith(botSuffix)) {
                    contactFlags |= FLAG_BOT;
                    phones = AppState.emptyStr;
                }
                int cleanFlags = contactFlags & ~FLAG_AUTHORIZED;
                if ((cleanFlags & FLAG_DELETED) == 0) {
                    Vector groupList = account.groups;
                    group = null;
                    for (int gi = groupList.size() - 1; gi >= 0; gi--) {
                        MrimContactGroup candidate = (MrimContactGroup) groupList.elementAt(gi);
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
                for (int fi = FORMAT_EXTRA_FIELDS_START; fi < contactFormatLen; fi++) {
                    if (fi == FORMAT_PROFILE_FIELD_INDEX) {
                        account.profileManager.receiveContactProfile(addr, buffer.readBufferArray());
                    } else if (contactFormat.charAt(fi) == 'u') {
                        buffer.readInt();
                    } else {
                        buffer.readWideStr();
                    }
                }
            }
            account.progress = Account.PROGRESS_CONNECTED;
            account.msgCount = PROGRESS_DONE;
            account.setConfiguration(account.configFlags);
            account.trySendData(ProtocolFactory.createMrimPacket(account, 4228, new ByteBuffer().writeIntLE(4).writeIntLE(0).writeIntLE(4).writeIntLE(0)));
            if (account.syncSeq == 1) {
                String searchQuery = StringUtils.intern(Utils.defaultStr(SessionState.getSessionToken()).toLowerCase());
                if (!StringUtils.isEmpty(searchQuery)) {
                    new AsyncTask(AsyncTaskId.WAIT_FOR_COMPLETION, new Object[]{searchQuery, account});
                }
                if (AccountManager.getTotalSyncCount() == 1) {
                    UIState.setConversationActive(true);
                }
            }
        } else {
            EventDispatcher.postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_STATUS_CHANGED)).append(status)));
            account.closeConnection();
            account.lastError = account.getDefaultError();
            account.markAllRead();
        }
        DiagnosticReporter.checkCrashReport();
    }

    public static final String encodeAlternate(String text) {
        return encodeDecodeInternal(text, 959, 960);
    }

    public static final String decodeAlternate(String text) {
        return encodeDecodeInternal(text, 960, 959);
    }

    private static final String encodeDecodeInternal(String text, int sourceKey, int targetKey) {
        String sourceChars = AppState.getString(sourceKey);
        String targetChars = AppState.getString(targetKey);
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            int idx = sourceChars.indexOf(ch);
            sb.append(idx < 0 ? ch : targetChars.charAt(idx));
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final String urlEncode(Object value) {
        String string = value.toString().toString();
        StringBuffer sb = ObjectPool.newStringBuffer();
        StringPool.get(PackedStringKeys.URL_ENCODE_D0);
        StringPool.get(PackedStringKeys.URL_ENCODE_D1);
        StringPool.get(PackedStringKeys.URL_ENCODE_YO_UPPER);
        StringPool.get(PackedStringKeys.URL_ENCODE_YO_LOWER);
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

    public static final String urlEncodeCyrillic(Object value) {
        String string = value.toString();
        StringBuffer sb = ObjectPool.newStringBuffer();
        String hexPrefixLo = StringPool.get(PackedStringKeys.URL_ENCODE_D0);
        String hexPrefixHi = StringPool.get(PackedStringKeys.URL_ENCODE_D1);
        String yoUpper = StringPool.get(PackedStringKeys.URL_ENCODE_YO_UPPER);
        String yoLower = StringPool.get(PackedStringKeys.URL_ENCODE_YO_LOWER);
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

    public static String formatNumber(int number, int minWidth) {
        String numStr = StringUtils.intern(Integer.toString(number));
        int length = numStr.length();
        if (length >= 2) {
            return numStr;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int pad = length; pad < 2; pad++) {
            sb.append('0');
        }
        sb.append(numStr);
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final String decodeHtmlSpecial(String html) {
        Vector entityNames = Utils.splitByNull(StringPool.get(PackedStringKeys.HTML_ENTITY_NAMES));
        Vector entityValues = Utils.splitByNull(StringPool.get(PackedStringKeys.HTML_ENTITY_VALUES));
        StringBuffer sb = ObjectPool.newStringBuffer();
        int totalLen = html.length();
        int pos = 0;
        while (pos < totalLen) {
            char ch = html.charAt(pos);
            if (ch == '&') {
                boolean found = false;
                for (int i = 0; i < 4 && !found; i++) {
                    try {
                        String entityName = (String) entityNames.elementAt(i);
                        if (html.startsWith(entityName, pos)) {
                            pos += entityName.length() - 1;
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
            pos++;
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final String transliterateRussian(String text) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        Vector translitTable = Utils.splitByNull(StringPool.get(PackedStringKeys.TRANSLIT_TABLE_BASIC));
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
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

    public static final String percentEncode(String text) {
        return percentEncodeInternal(text, true);
    }

    private static final String percentEncodeInternal(String text, boolean lowercase) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if ((ch >= 'A' && ch <= 'Z') || ((ch >= 'a' && ch <= 'z') || ((ch >= '0' && ch <= '9') || ch == '.' || (ch == '-' && !lowercase)))) {
                sb.append(ch);
            } else if (lowercase) {
                sb.append('%').append(Integer.toHexString(ch >> 4)).append(Integer.toHexString(ch & 15));
            } else {
                sb.append('%').append(Integer.toHexString(ch >> 4).toUpperCase()).append(Integer.toHexString(ch & 15).toUpperCase());
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final void setMapEnabled(boolean enabled) {
        MapRenderer.needsRedraw = true;
        MapState.setGpsEnabled(enabled);
    }

    public static final void incrementZoom() {
        MapRenderer.setZoom(MapState.getZoomLevel() + 1);
    }

    public static final void decrementZoom() {
        MapRenderer.setZoom(MapState.getZoomLevel() - 1);
    }

    public static final void loadContacts() {
        new AsyncTask(AsyncTaskId.FETCH_MMP_ROUTE, RouteData.buildLocationString());
    }

    public static final void updateStatusText(int nameKey) {
        SessionState.setActiveProtocolName(ObjectPool.newStringBuffer().append(AppState.getString(nameKey)).append(' ').append('(').append(UIState.getPhotoQueue().size()).append(')'));
    }
}
