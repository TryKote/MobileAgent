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
    public Vector items = NetworkUtils.newVector();

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
        StringBuffer sb = NetworkUtils.newStringBuffer();
        int size = this.items.size();
        if (this.height == 5) {
            sb.append(AppState.getString(StateKeys.STR_CONV_UNREAD_PREFIX)).append(size);
        } else {
            int i = size - 1;
            sb.append(((ListItem) this.items.firstElement()).getText()).append(AppState.getString(StateKeys.STR_CONV_SEPARATOR)).append(i).append(AppState.getString(StateKeys.STR_CONV_SUFFIX_BASE + Utils.pluralForm(i)));
        }
        return NetworkUtils.bufToStringCached(sb);
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
                    httpClient.setRequestMethod(NetworkUtils.longToHex(1414745936));
                    ByteBuffer requestBody = new ByteBuffer().writeCompressed(2755131).writeConversationStr(objArr[2]).writeCompressed(330609).writeConversationStr(objArr[3]);
                    ConnectionThread.setHeaderFromState(httpClient, 788628, 2164851);
                    httpClient.writeData(requestBody.data, requestBody.length);
                    int responseCode = httpClient.getResponseCode();
                    i = responseCode;
                    if (responseCode == 200) {
                        protocol.msgCount = 40;
                        AppController.needsRepaint = true;
                        XmlElement responseXml = new ByteBuffer(httpClient).parseXmlStr();
                        int statusCode = Integer.parseInt(StringUtils.fromBuffer(responseXml.findChildByKey(658246).textContent));
                        if (statusCode != 200) {
                            if (statusCode == 330) {
                                ((MmpProtocol) objArr[0]).handleComplete();
                                objArr[0] = null;
                            }
                            throw new RuntimeException(StringUtils.intern(Integer.toString(statusCode)));
                        }
                        XmlElement resultElement = responseXml.findChildByKey(262156);
                        new AsyncTask(31, new Object[]{objArr[0], ResourceManager.integerOf(1), StringUtils.fromBuffer(resultElement.findChildByKey(461648).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(330583).findChildByKey(65538).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(527196).textContent), StringUtils.fromBuffer(resultElement.findChildByKey(854884).textContent), objArr[3]});
                        HttpClient.closeAndUpdateStats(httpClient);
                        NetworkLock.releaseNetworkLock();
                        return;
                    }
                } else {
                    protocol.msgCount = 50;
                    AppController.needsRepaint = true;
                    ByteBuffer headerBuffer = new ByteBuffer().writeCompressed(2951781).writeByte(63);
                    String queryStr = new ByteBuffer().writeCompressed(132058).writeConversationStr(objArr[3]).writeCompressed(11012754).writeObjectStr(objArr[4]).readAllByteStr();
                    HttpClient httpClient2 = HttpClient.createMockClient(headerBuffer.writeRawString(queryStr).writeCompressed(789306).writeRawString(encryptData(new ByteBuffer().writeCompressed(265078).writeRawString(percentEncodeInternal(AppState.getString(StateKeys.STR_RES_HUGE_URL_8), false)).writeByte(38).writeRawString(percentEncodeInternal(queryStr, false)).readAllByteStr(), encryptData((String) objArr[5], (String) objArr[6]))).readAllByteStr()).sendHttpRequest(0, 5522759, 330359);
                    int responseCode2 = httpClient2.getResponseCode();
                    i = responseCode2;
                    if (responseCode2 == 200) {
                        protocol.msgCount = 60;
                        AppController.needsRepaint = true;
                        XmlElement resultElement2 = httpClient2.readChunkedResponse().parseXmlStr().findChildByKey(262156);
                        ((MmpProtocol) objArr[0]).connectionData = new String[]{(String) objArr[2], NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(StringUtils.fromBuffer(resultElement2.findChildByKey(265052).textContent)).append(':').append(StringUtils.fromBuffer(resultElement2.findChildByKey(265005).textContent))), StringUtils.fromBuffer(resultElement2.findChildByKey(395483).textContent)};
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
                protocol2.progress = 0;
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
    public static final byte[] hashData(byte[] bArr, int i) {
        int[] blockBuf = new int[16];
        int[] state = Utils.bytesToInts(AppState.getBytes(StateKeys.RES_EMOTICON_STATE));
        int[] bitCount = new int[2];
        byte[] tempBuf = NetworkUtils.newBytes(64);
        md5ProcessBuffer(bArr, i, blockBuf, state, bitCount, tempBuf);
        byte[] lengthBytes = md5Finalize(NetworkUtils.newBytes(16), bitCount, 8);
        byte[] padBuf = NetworkUtils.newBytes(64);
        padBuf[0] = -128;
        int bufIdx = (bitCount[0] >>> 3) & 63;
        md5ProcessBuffer(padBuf, bufIdx < 56 ? 56 - bufIdx : 120 - bufIdx, blockBuf, state, bitCount, tempBuf);
        md5ProcessBuffer(lengthBytes, 8, blockBuf, state, bitCount, tempBuf);
        NetworkUtils.releaseBytes(padBuf);
        NetworkUtils.releaseBytes(tempBuf);
        return md5Finalize(lengthBytes, state, 16);
    }

    /* renamed from: a */
    private static final int md5Round1(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i ^ (-1)) & i3) | (i & i2)) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: b */
    private static final int md5Round2(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i3 ^ (-1)) & i2) | (i & i3)) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: c */
    private static final int md5Round3(int i, int i2, int i3, int i4, int i5) {
        int i6 = ((i ^ i2) ^ i3) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: d */
    private static final int md5Round4(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i3 ^ (-1)) | i) ^ i2) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: a */
    private static final void md5ProcessBuffer(byte[] bArr, int i, int[] iArr, int[] iArr2, int[] iArr3, byte[] bArr2) {
        int i2 = (iArr3[0] >>> 3) & 63;
        int i3 = iArr3[0] + (i << 3);
        iArr3[0] = i3;
        if (i3 < (i << 3)) {
            iArr3[1] = iArr3[1] + 1;
        }
        iArr3[1] = iArr3[1] + (i >>> 29);
        int i4 = 64 - i2;
        int i5 = i4;
        if (i >= i4) {
            Utils.arraycopy((Object) bArr, 0, (Object) bArr2, i2, i5);
            md5ProcessBlock(bArr2, iArr, iArr2);
            byte[] tempBlock = NetworkUtils.newBytes(64);
            while (i5 + 63 < i) {
                Utils.arraycopy((Object) bArr, i5, (Object) tempBlock, 0, 64);
                md5ProcessBlock(tempBlock, iArr, iArr2);
                i5 += 64;
            }
            NetworkUtils.releaseBytes(tempBlock);
            i2 = 0;
        } else {
            i5 = 0;
        }
        Utils.arraycopy((Object) bArr, i5, (Object) bArr2, i2, i - i5);
    }

    /* renamed from: a */
    private static final void md5ProcessBlock(byte[] bArr, int[] iArr, int[] iArr2) {
        int i = 0;
        int i2 = -1;
        do {
            int i3 = i2 + 1;
            int i4 = bArr[i3] & 255;
            int i5 = i3 + 1;
            int i6 = i4 | ((bArr[i5] & 255) << 8);
            int i7 = i5 + 1;
            int i8 = i6 | ((bArr[i7] & 255) << 16);
            i2 = i7 + 1;
            iArr[i] = i8 | (bArr[i2] << 24);
            i++;
        } while (i < 16);
        int i9 = iArr2[1];
        int i10 = iArr2[2];
        int a = md5Round1(i9, i10, iArr2[3], 7, (iArr2[0] + iArr[0]) - 680876936);
        int a2 = md5Round1(a, i9, i10, 12, (iArr2[3] + iArr[1]) - 389564586);
        int a3 = md5Round1(a2, a, i9, 17, i10 + iArr[2] + 606105819);
        int a4 = md5Round1(a3, a2, a, 22, (i9 + iArr[3]) - 1044525330);
        int a5 = md5Round1(a4, a3, a2, 7, (a + iArr[4]) - 176418897);
        int a6 = md5Round1(a5, a4, a3, 12, a2 + iArr[5] + 1200080426);
        int a7 = md5Round1(a6, a5, a4, 17, (a3 + iArr[6]) - 1473231341);
        int a8 = md5Round1(a7, a6, a5, 22, (a4 + iArr[7]) - 45705983);
        int a9 = md5Round1(a8, a7, a6, 7, a5 + iArr[8] + 1770035416);
        int a10 = md5Round1(a9, a8, a7, 12, (a6 + iArr[9]) - 1958414417);
        int a11 = md5Round1(a10, a9, a8, 17, (a7 + iArr[10]) - 42063);
        int a12 = md5Round1(a11, a10, a9, 22, (a8 + iArr[11]) - 1990404162);
        int a13 = md5Round1(a12, a11, a10, 7, a9 + iArr[12] + 1804603682);
        int a14 = md5Round1(a13, a12, a11, 12, (a10 + iArr[13]) - 40341101);
        int a15 = md5Round1(a14, a13, a12, 17, (a11 + iArr[14]) - 1502002290);
        int a16 = md5Round1(a15, a14, a13, 22, a12 + iArr[15] + 1236535329);
        int b = md5Round2(a16, a15, a14, 5, (a13 + iArr[1]) - 165796510);
        int b2 = md5Round2(b, a16, a15, 9, (a14 + iArr[6]) - 1069501632);
        int b3 = md5Round2(b2, b, a16, 14, a15 + iArr[11] + 643717713);
        int b4 = md5Round2(b3, b2, b, 20, (a16 + iArr[0]) - 373897302);
        int b5 = md5Round2(b4, b3, b2, 5, (b + iArr[5]) - 701558691);
        int b6 = md5Round2(b5, b4, b3, 9, b2 + iArr[10] + 38016083);
        int b7 = md5Round2(b6, b5, b4, 14, (b3 + iArr[15]) - 660478335);
        int b8 = md5Round2(b7, b6, b5, 20, (b4 + iArr[4]) - 405537848);
        int b9 = md5Round2(b8, b7, b6, 5, b5 + iArr[9] + 568446438);
        int b10 = md5Round2(b9, b8, b7, 9, (b6 + iArr[14]) - 1019803690);
        int b11 = md5Round2(b10, b9, b8, 14, (b7 + iArr[3]) - 187363961);
        int b12 = md5Round2(b11, b10, b9, 20, b8 + iArr[8] + 1163531501);
        int b13 = md5Round2(b12, b11, b10, 5, (b9 + iArr[13]) - 1444681467);
        int b14 = md5Round2(b13, b12, b11, 9, (b10 + iArr[2]) - 51403784);
        int b15 = md5Round2(b14, b13, b12, 14, b11 + iArr[7] + 1735328473);
        int b16 = md5Round2(b15, b14, b13, 20, (b12 + iArr[12]) - 1926607734);
        int c = md5Round3(b16, b15, b14, 4, (b13 + iArr[5]) - 378558);
        int c2 = md5Round3(c, b16, b15, 11, (b14 + iArr[8]) - 2022574463);
        int c3 = md5Round3(c2, c, b16, 16, b15 + iArr[11] + 1839030562);
        int c4 = md5Round3(c3, c2, c, 23, (b16 + iArr[14]) - 35309556);
        int c5 = md5Round3(c4, c3, c2, 4, (c + iArr[1]) - 1530992060);
        int c6 = md5Round3(c5, c4, c3, 11, c2 + iArr[4] + 1272893353);
        int c7 = md5Round3(c6, c5, c4, 16, (c3 + iArr[7]) - 155497632);
        int c8 = md5Round3(c7, c6, c5, 23, (c4 + iArr[10]) - 1094730640);
        int c9 = md5Round3(c8, c7, c6, 4, c5 + iArr[13] + 681279174);
        int c10 = md5Round3(c9, c8, c7, 11, (c6 + iArr[0]) - 358537222);
        int c11 = md5Round3(c10, c9, c8, 16, (c7 + iArr[3]) - 722521979);
        int c12 = md5Round3(c11, c10, c9, 23, c8 + iArr[6] + 76029189);
        int c13 = md5Round3(c12, c11, c10, 4, (c9 + iArr[9]) - 640364487);
        int c14 = md5Round3(c13, c12, c11, 11, (c10 + iArr[12]) - 421815835);
        int c15 = md5Round3(c14, c13, c12, 16, c11 + iArr[15] + 530742520);
        int c16 = md5Round3(c15, c14, c13, 23, (c12 + iArr[2]) - 995338651);
        int d = md5Round4(c16, c15, c14, 6, (c13 + iArr[0]) - 198630844);
        int d2 = md5Round4(d, c16, c15, 10, c14 + iArr[7] + 1126891415);
        int d3 = md5Round4(d2, d, c16, 15, (c15 + iArr[14]) - 1416354905);
        int d4 = md5Round4(d3, d2, d, 21, (c16 + iArr[5]) - 57434055);
        int d5 = md5Round4(d4, d3, d2, 6, d + iArr[12] + 1700485571);
        int d6 = md5Round4(d5, d4, d3, 10, (d2 + iArr[3]) - 1894986606);
        int d7 = md5Round4(d6, d5, d4, 15, (d3 + iArr[10]) - 1051523);
        int d8 = md5Round4(d7, d6, d5, 21, (d4 + iArr[1]) - 2054922799);
        int d9 = md5Round4(d8, d7, d6, 6, d5 + iArr[8] + 1873313359);
        int d10 = md5Round4(d9, d8, d7, 10, (d6 + iArr[15]) - 30611744);
        int d11 = md5Round4(d10, d9, d8, 15, (d7 + iArr[6]) - 1560198380);
        int d12 = md5Round4(d11, d10, d9, 21, d8 + iArr[13] + 1309151649);
        int d13 = md5Round4(d12, d11, d10, 6, (d9 + iArr[4]) - 145523070);
        int d14 = md5Round4(d13, d12, d11, 10, (d10 + iArr[11]) - 1120210379);
        int d15 = md5Round4(d14, d13, d12, 15, d11 + iArr[2] + 718787259);
        int d16 = md5Round4(d15, d14, d13, 21, (d12 + iArr[9]) - 343485551);
        iArr2[0] = iArr2[0] + d13;
        iArr2[1] = iArr2[1] + d16;
        iArr2[2] = iArr2[2] + d15;
        iArr2[3] = iArr2[3] + d14;
    }

    /* JADX WARN: Type inference failed for: r2v10, types: [int] */
    /* renamed from: a */
    private static final byte[] md5Finalize(byte[] bArr, int[] iArr, int i) {
        int i2 = 0;
        int i3 = 0;
        do {
            int i4 = i3;
            int i5 = i3 + 1;
            int i6 = i2;
            i2++;
            int i7 = iArr[i6];
            bArr[i4] = (byte) i7;
            int i8 = i5 + 1;
            bArr[i5] = (byte) (i7 >>> 8);
            int i9 = i8 + 1;
            bArr[i8] = (byte) (i7 >>> 16);
            i3 = i9 + 1;
            bArr[i9] = (byte) (i7 >> 24);
        } while (i3 < i);
        return bArr;
    }

    /* renamed from: a */
    public static final Vector parseConversation(String str) {
        Vector parts = NetworkUtils.newVector();
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
            int idx = str.indexOf(NetworkUtils.longToHex(1031302438), AppState.indexOf(str, 1031302438) + 4);
            String encoded = idx < 0 ? StringUtils.suffix(str, bodyStart + 4) : StringUtils.substring(str, bodyStart + 4, idx);
            if (StringUtils.matchesEncoded(encoded, 1094795585)) {
                return AppState.emptyStr;
            }
            ByteBuffer decodeBuffer = ResourceManager.decodeBase64(replaceText(replaceText(replaceText(encoded, 200762, 65752), 200765, 65547), 200768, 65552));
            StringBuffer sb = NetworkUtils.newStringBuffer();
            while (decodeBuffer.length > 0) {
                int b1 = decodeBuffer.readUByte();
                int b2 = b1 > 127 ? decodeBuffer.readUByte() : 0;
                int b3 = b1 > 223 ? decodeBuffer.readUByte() : 0;
                sb.append(b1 < 128 ? (char) b1 : b1 < 224 ? (char) (((b1 - 192) << 6) + (b2 - 128)) : b1 < 240 ? (char) (((b1 - 224) << 12) + ((b2 - 128) << 6) + (b3 - 128)) : (char) (((b1 - 240) << 18) + ((b2 - 128) << 12) + ((b3 - 128) << 6) + ((b1 > 239 ? decodeBuffer.readUByte() : 0) - 128)));
            }
            return NetworkUtils.bufToStringCached(sb);
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
        StringBuffer sb = NetworkUtils.newStringBuffer();
        int length = 0;
        while (true) {
            int i3 = length;
            int idx = str.indexOf(searchStr, i3);
            if (idx < 0) {
                return NetworkUtils.bufToStringCached(sb.append(StringUtils.suffix(str, i3)));
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
            StringBuffer sb = NetworkUtils.newStringBuffer();
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
            messageText = NetworkUtils.bufToStringCached(sb);
        } else {
            ByteBuffer decodeBuffer = ResourceManager.decodeBase64(rawBody);
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
                    Vector members = NetworkUtils.newVector();
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
        StringBuffer sb = NetworkUtils.newStringBuffer();
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
        return NetworkUtils.bufToStringCached(sb);
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
            account.progress = 100;
            account.msgCount = 100;
            account.setConfiguration(account.configFlags);
            account.trySendData(ProtocolFactory.createMrimPacket(account, 4228, new ByteBuffer().writeVector((Vector) null).writeVector((Vector) null)));
            if (account.syncSeq == 1) {
                String searchQuery = StringUtils.intern(Utils.defaultStr(AppState.getString(StateKeys.SLOT_SESSION_TOKEN)).toLowerCase());
                if (!StringUtils.isEmpty(searchQuery)) {
                    new AsyncTask(27, new Object[]{searchQuery, account});
                }
                if (AccountManager.getActiveScreenId() == 1) {
                    AppState.setInt(StateKeys.FLAG_CONVERSATION_ACTIVE, 1);
                }
            }
        } else {
            IOUtils.postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_STATUS_CHANGED)).append(status)));
            account.closeConnection();
            account.lastError = account.getDefaultError();
            account.markAllRead();
        }
        NetworkUtils.checkCrashReport();
    }

    /* renamed from: a */
    public static final void createStatusReport(boolean z, MrimAccount account) {
        String loginParam;
        String domainParam;
        XmlElement rootElement = XmlElement.createFromState(266953);
        XmlElement childElement = XmlElement.createFromState(398003);
        XmlElement reportElement = rootElement.addChild(childElement);
        try {
            childElement.addChild(new XmlElement(99).setAttrValue(262589, NetworkUtils.longToHex(5067591)).setAttrValue(329117, NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_URL_PATH_1)).append(AppController.getScreenMode3()).append(',').append(AppController.getScreenMode4()).append(',').append(AppController.getScreenMode1()).append(',').append(AppController.getScreenMode2()).append(',').append(0))));
        } catch (Throwable unused) {
        }
        Vector accounts = AccountManager.getMrimAccountList();
        if (account != null) {
            accounts.addElement(account);
            account = null;
        }
        int count = Utils.vectorSize(accounts);
        while (true) {
            count--;
            if (count < 0) {
                break;
            }
            MrimAccount candidate = (MrimAccount) accounts.elementAt(count);
            if (candidate.isConnected()) {
                account = candidate;
                break;
            }
        }
        NetworkUtils.releaseVector(accounts);
        MrimAccount connectedAccount = account;
        if (connectedAccount != null) {
            loginParam = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_COMMAND_2)).append(connectedAccount.login));
            domainParam = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_ATTR_2)).append(connectedAccount.customDomain));
        } else {
            String str = AppState.emptyStr;
            loginParam = str;
            domainParam = str;
        }
        new AsyncTask(23, new ByteBuffer().writeCompressed(5771795).writeConversationStr((Object) new ByteBuffer().writeRawString(reportElement.toString()).toBase64()).writeEncodedInt(z ? 791174 : 1038).writeRawString(domainParam).writeRawString(loginParam).writeCompressed(397997).writeEncodedInt(223).writeCompressed(594539).writeEncodedInt(1375).getStringAndClear());
    }

    /*  JADX ERROR: Types fix failed
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:183)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:242)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
        */
    /* JADX WARN: Failed to calculate best type for var: r3v1 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r3v1 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Not initialized variable reg: 3, insn: MOVE (r2 I:??) = (r3 I:??), block:B:8:0x005d */
    /* renamed from: g */
    public static final void fetchMapData(java.lang.String r7) {
        /*
            r0 = 0
            r8 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r7
            r1 = 0
            r9 = r1
            r1 = 0
            r9 = r1
            r1 = 0
            r2 = 3
            ax r0 = p000.HttpClient.createHttpClient(r0, r1, r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0
            r8 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto Lac
            n r0 = new n     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r9 = r0
            r0 = r7
            r1 = r9
            av r1 = r1.parseXml()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = 594557(0x9127d, float:8.33152E-40)
            av r1 = r1.findChildByKey(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = 200701(0x30ffd, float:2.81242E-40)
            av r1 = r1.findChildByKey(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r9 = r1
            boolean r0 = isStatusReport(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            if (r0 == 0) goto L5d
            r0 = r9
            r1 = 197037(0x301ad, float:2.76108E-40)
            java.lang.String r0 = r0.getIntAttribute(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r9
            r2 = 197041(0x301b1, float:2.76113E-40)
            java.lang.String r1 = r1.getIntAttribute(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = r9
            r3 = 529081(0x812b9, float:7.414E-40)
            java.lang.String r2 = r2.getIntAttribute(r3)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r3 = r9
            r4 = 529089(0x812c1, float:7.41412E-40)
            java.lang.String r3 = r3.getIntAttribute(r4)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r4 = r9
            r5 = 397788(0x611dc, float:5.5742E-40)
            java.lang.String r4 = r4.getIntAttribute(r5)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            p000.AppController.setFormFields(r0, r1, r2, r3, r4)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            goto La4
        L5d:
            r0 = r9
            r1 = 197037(0x301ad, float:2.76108E-40)
            java.lang.String r0 = r0.getIntAttribute(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            long r0 = p000.IOUtils.longitudeToPixel(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0; r2 = r3;      // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r13 = r1
            r1 = r9
            r2 = 197041(0x301b1, float:2.76113E-40)
            java.lang.String r1 = r1.getIntAttribute(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            long r1 = p000.IOUtils.latitudeToPixel(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = r1; r2 = r3;      // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r11 = r2
            p000.MapRenderer.setPosition(r0, r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r9
            r1 = 725709(0xb12cd, float:1.016935E-39)
            java.lang.String r0 = r0.getIntAttribute(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            int r0 = p000.Utils.parseInt(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            p000.MapRenderer.setZoom(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = 1
            p000.MapRenderer.needsRedraw = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r13
            r1 = r11
            r2 = r9
            r3 = 594548(0x91274, float:8.33139E-40)
            java.lang.String r2 = r2.getIntAttribute(r3)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            int r2 = p000.Utils.parseInt(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r9 = r2
            r15 = r1
            p000.ChatRenderer.markerLon = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r15
            p000.ChatRenderer.markerLat = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r9
            p000.ChatRenderer.markerRadius = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
        La4:
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)
            p000.NetworkLock.releaseNetworkLock()
            return
        Lac:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            throw r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
        Lb4:
            r0 = r7
            r1 = 0
            r9 = r1
            boolean r0 = isStatusReport(r0)     // Catch: java.lang.Throwable -> Ldc
            if (r0 == 0) goto Lc9
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            p000.AppController.setFormFields(r0, r1, r2, r3, r4)     // Catch: java.lang.Throwable -> Ldc
            goto Ld4
        Lc9:
            r0 = 308(0x134, float:4.32E-43)
            r1 = 0
            r9 = r1
            java.lang.String r0 = p000.AppState.getString(r0)     // Catch: java.lang.Throwable -> Ldc
            p000.IOUtils.postEvent(r0)     // Catch: java.lang.Throwable -> Ldc
        Ld4:
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)
            p000.NetworkLock.releaseNetworkLock()
            return
        Ldc:
            r10 = move-exception
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)
            p000.NetworkLock.releaseNetworkLock()
            r0 = r10
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p000.Conversation.fetchMapData(java.lang.String):void");
    }

    /* renamed from: q */
    private static final boolean isStatusReport(String str) {
        return AppState.indexOfPool(str, 791174) > 0;
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
        StringBuffer sb = NetworkUtils.newStringBuffer();
        int length = str.length();
        for (int i3 = 0; i3 < length; i3++) {
            char ch = str.charAt(i3);
            int idx = sourceChars.indexOf(ch);
            sb.append(idx < 0 ? ch : targetChars.charAt(idx));
        }
        return NetworkUtils.bufToStringCached(sb);
    }

    /* renamed from: a */
    public static final String urlEncode(Object obj) {
        String string = obj.toString().toString();
        StringBuffer sb = NetworkUtils.newStringBuffer();
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
        return NetworkUtils.bufToStringCached(sb);
    }

    /* renamed from: b */
    public static final String urlEncodeCyrillic(Object obj) {
        String string = obj.toString();
        StringBuffer sb = NetworkUtils.newStringBuffer();
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
        return NetworkUtils.bufToStringCached(sb);
    }

    /* renamed from: a */
    public static String formatNumber(int i, int i2) {
        String numStr = StringUtils.intern(Integer.toString(i));
        int length = numStr.length();
        if (length >= 2) {
            return numStr;
        }
        StringBuffer sb = NetworkUtils.newStringBuffer();
        for (int i3 = length; i3 < 2; i3++) {
            sb.append('0');
        }
        sb.append(numStr);
        return NetworkUtils.bufToStringCached(sb);
    }

    /* renamed from: j */
    public static final String decodeHtmlSpecial(String str) {
        Vector entityNames = Utils.splitByNull(AppState.getString(StateKeys.STR_RES_API_URL_7));
        Vector entityValues = Utils.splitByNull(AppState.getString(StateKeys.STR_RES_COMMAND_1));
        StringBuffer sb = NetworkUtils.newStringBuffer();
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
        return NetworkUtils.bufToStringCached(sb);
    }

    /* renamed from: k */
    public static final String transliterateRussian(String str) {
        StringBuffer sb = NetworkUtils.newStringBuffer();
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
        NetworkUtils.releaseVector(translitTable);
        return NetworkUtils.bufToStringCached(sb);
    }

    /* renamed from: l */
    public static final String percentEncode(String str) {
        return percentEncodeInternal(str, true);
    }

    /* renamed from: a */
    private static final String percentEncodeInternal(String str, boolean z) {
        StringBuffer sb = NetworkUtils.newStringBuffer();
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
        return NetworkUtils.bufToStringCached(sb);
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
        new AsyncTask(11, MmpContact.buildLocationString());
    }

    /* renamed from: c */
    public static final void updateStatusText(int i) {
        AppState.setFromBuffer(StateKeys.SLOT_ACTIVE_PROTOCOL_NAME, NetworkUtils.newStringBuffer().append(AppState.getString(i)).append(' ').append('(').append(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE).size()).append(')'));
    }
}
