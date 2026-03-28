package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: n */
/* loaded from: MobileAgent_3.9.jar:n.class */
public final class ByteBuffer {

    /* renamed from: a */
    public byte[] data;

    /* renamed from: b */
    public int length;

    /* renamed from: c */
    public int offset;

    public ByteBuffer() {
        this.data = AppState.emptyBytes;
    }

    public ByteBuffer(HttpClient client) {
        this.data = ObjectPool.newBytes(2048);
        try {
            byte[] tempBuf = ObjectPool.newBytes(2048);
            while (true) {
                int bytesRead = client.readData(tempBuf);
                if (bytesRead < 0) {
                    ObjectPool.releaseBytes(tempBuf);
                    return;
                }
                writeBytesAt(tempBuf, 0, bytesRead);
            }
        } catch (Throwable unused) {
        }
    }

    public ByteBuffer(String str) {
        this(str, 2048);
    }

    public ByteBuffer(String str, int i) {
        this((InputStream) IOUtils.registerResource((Object) str.getClass().getResourceAsStream(str)), i);
    }

    private ByteBuffer(InputStream inputStream, int i) {
        this.data = ObjectPool.newBytes(i);
        try {
            byte[] tempBuf = ObjectPool.newBytes(8192);
            while (true) {
                int i2 = inputStream.read(tempBuf);
                if (i2 < 0) {
                    break;
                } else {
                    writeBytesAt(tempBuf, 0, i2);
                }
            }
            ObjectPool.releaseBytes(tempBuf);
        } catch (Throwable unused) {
        }
        IOUtils.closeInput(inputStream);
    }

    /* renamed from: a */
    public final ByteBuffer compact() {
        int i = this.length;
        if (i == 0) {
            clear();
        } else {
            byte[] bArr = this.data;
            if (i < bArr.length) {
                ensureCapacity(0);
                byte[] compactData = ObjectPool.reallocBytes(bArr, i);
                if (compactData != null) {
                    this.data = compactData;
                }
            }
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer ensureCapacity(int i) {
        byte[] bArr = this.data;
        int length = bArr.length;
        int i2 = this.length;
        int i3 = i2 + i;
        boolean z = length < i3;
        boolean z2 = z;
        byte[] tempBuf = z ? ObjectPool.newBytes(i3 + 32) : bArr;
        if (z2 || this.offset != 0) {
            Utils.arraycopy((Object) bArr, this.offset, (Object) tempBuf, 0, i2);
            if (z2) {
                ObjectPool.releaseBytes(bArr);
                this.data = tempBuf;
            }
        }
        this.offset = 0;
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer clear() {
        ObjectPool.releaseBytes(this.data);
        this.data = AppState.emptyBytes;
        this.length = 0;
        this.offset = 0;
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer writeBytes(byte[] bArr) {
        if (bArr != null) {
            writeBytesAt(bArr, 0, bArr.length);
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer writeBytesAt(byte[] bArr, int i, int i2) {
        if (i2 > 0) {
            ensureCapacity(i2);
            Utils.arraycopy((Object) bArr, i, (Object) this.data, this.length, i2);
            this.length += i2;
        }
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer setData(byte[] bArr) {
        clear();
        this.data = bArr;
        this.length = bArr.length;
        return this;
    }

    /* renamed from: b */
    public final byte[] readInto(byte[] bArr, int i, int i2) {
        Utils.arraycopy((Object) this.data, this.offset, (Object) bArr, i, i2);
        this.length -= i2;
        this.offset += i2;
        compact();
        return bArr;
    }

    /* renamed from: b */
    public final ByteBuffer writeZeros(int i) {
        ensureCapacity(i);
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 <= 0) {
                return this;
            }
            byte[] bArr = this.data;
            int i3 = this.length;
            this.length = i3 + 1;
            bArr[i3] = 0;
        }
    }

    /* renamed from: a */
    public final ByteBuffer writeString(String str, int i) {
        return i != 0 ? writeStringUTF16(str) : writeStringLatin1(str);
    }

    /* renamed from: a */
    public final ByteBuffer writeStringLatin1(String str) {
        int length;
        if (str == null || (length = str.length()) <= 0) {
            return writeIntLE(0);
        }
        ensureCapacity(length + 4);
        writeIntLE(length);
        for (int i = 0; i < length; i++) {
            byte[] bArr = this.data;
            int i2 = this.length;
            this.length = i2 + 1;
            bArr[i2] = (byte) str.charAt(i);
        }
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer writeStringUTF16(String str) {
        int length;
        if (str == null || (length = str.length()) <= 0) {
            return writeIntLE(0);
        }
        int i = length << 1;
        ensureCapacity(i + 4);
        writeIntLE(i);
        for (int i2 = 0; i2 < length; i2++) {
            byte[] bArr = this.data;
            int i3 = this.length;
            this.length = i3 + 1;
            char ch = str.charAt(i2);
            bArr[i3] = (byte) ch;
            byte[] bArr2 = this.data;
            int i4 = this.length;
            this.length = i4 + 1;
            bArr2[i4] = (byte) (ch >> '\b');
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer writeCompressed(int i) {
        return i > 5179 ? writeBytesAt(AppState.getBytes(StringResKeys.RES_STRING_DATA), i & 65535, i >> 16) : writeBytes(AppState.getBytes(i));
    }

    /* renamed from: d */
    public final ByteBuffer writeEncodedInt(int i) {
        return writeRawString(AppState.getString(i));
    }

    /* renamed from: e */
    public final ByteBuffer writeExtendedInt(int i) {
        writeRawString(AppState.getString(i & 65535));
        int i2 = i >>> 16;
        if (i2 != 0) {
            writeByte(i2);
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer writeEncodedString(String str) {
        return writeRawString(Conversation.urlEncodeCyrillic((Object) str));
    }

    /* renamed from: d */
    public final ByteBuffer writeRawString(String str) {
        int length;
        if (str != null && (length = str.length()) > 0) {
            ensureCapacity(length);
            byte[] bArr = this.data;
            for (int i = 0; i < length; i++) {
                int i2 = this.length;
                this.length = i2 + 1;
                bArr[i2] = (byte) str.charAt(i);
            }
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer writeConversationStr(Object obj) {
        return writeRawString(Conversation.percentEncode((String) obj));
    }

    /* renamed from: b */
    public final ByteBuffer writeObjectStr(Object obj) {
        return writeRawString((String) obj);
    }

    /* renamed from: c */
    public final String getStringAndClear() {
        String result = this.length == 0 ? AppState.emptyStr : StringUtils.intern(new String(this.data, this.offset, this.length));
        clear();
        return result;
    }

    /* renamed from: a */
    public final ByteBuffer writeStringArray(String[] strArr) {
        ByteBuffer buf = new ByteBuffer();
        buf.writeIntLE(2);
        for (int i = 0; i < 2; i++) {
            buf.writeStringUTF16(strArr[i]);
        }
        return writeStringLatin1(buf.toBase64());
    }

    /* renamed from: d */
    public final String toBase64() {
        ensureCapacity(0);
        String result = Base64.encode(this.data, 0, this.length);
        clear();
        return result;
    }

    /* renamed from: f */
    public final ByteBuffer writeByte(int i) {
        ensureCapacity(1);
        byte[] bArr = this.data;
        int i2 = this.length;
        this.length = i2 + 1;
        bArr[i2] = (byte) i;
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer writeBoolean(boolean z) {
        return writeByte(z ? 1 : 0);
    }

    /* renamed from: a */
    public final ByteBuffer writeLong(long j) {
        return writeIntLE((int) j).writeIntLE((int) (j >> 32));
    }

    /* renamed from: a */
    ByteBuffer copyFrom(ByteBuffer buf, int i) {
        byte[] tempBuf = ObjectPool.newBytes(i);
        buf.readInto(tempBuf, 0, i);
        writeBytesAt(tempBuf, 0, i);
        ObjectPool.releaseBytes(tempBuf);
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer writeBuffer(ByteBuffer buf) {
        if (buf != null) {
            copyFrom(buf, buf.length);
        }
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer writeBufferShortLen(ByteBuffer buf) {
        if (buf != null) {
            writeShortBE(buf.length).copyFrom(buf, buf.length);
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer writeBufferIntLen(ByteBuffer buf) {
        return buf != null ? writeIntLE(buf.length).copyFrom(buf, buf.length) : writeIntLE(0);
    }

    /* renamed from: e */
    public final int readInt() {
        int value = peekIntAt(0);
        this.offset += 4;
        this.length -= 4;
        return value;
    }

    /* renamed from: g */
    public final ByteBuffer skip(int i) {
        this.offset += i;
        int i2 = this.length - i;
        this.length = i2;
        if (i2 == 0) {
            clear();
        }
        return this;
    }

    /* renamed from: h */
    public final int peekIntAt(int i) {
        int i2 = this.offset + i;
        byte[] bArr = this.data;
        int i3 = i2 + 1;
        int i4 = (bArr[i2] & 255) | ((bArr[i3] & 255) << 8);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 16) | (bArr[i5 + 1] << 24);
    }

    /* renamed from: i */
    public final int peekByteAt(int i) {
        return this.data[this.offset + i] & 255;
    }

    /* renamed from: j */
    public final String readStringByMode(int i) {
        return i != 0 ? readUnicodeStr() : readUTF8Str((String) null);
    }

    /* renamed from: f */
    public final String readUnicodeStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int remaining = readInt();
        while (true) {
            remaining--;
            if (remaining < 0) {
                return ObjectPool.toStringAndRelease(sb);
            }
            sb.append(Utils.win1251ToChar((int) readByte()));
        }
    }

    /* renamed from: g */
    public final String readWideStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int remaining = readInt();
        while (true) {
            remaining--;
            if (remaining < 0) {
                return ObjectPool.toStringAndRelease(sb);
            }
            sb.append((char) readUByte());
        }
    }

    /* renamed from: e */
    public final String readUTF8Str(String str) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int remaining = readInt();
        while (true) {
            remaining -= 2;
            if (remaining < 0) {
                break;
            }
            sb.append((char) (readUByte() | (readByte() << 8)));
        }
        if (remaining == -1) {
            readByte();
            if (str != null) {
                return str;
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: h */
    public final String readAllWideStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (this.length > 0) {
            sb.append((char) (readUByte() | (readByte() << 8)));
        }
        clear();
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: i */
    public final String readAllByteStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (this.length > 0) {
            sb.append(Utils.win1251ToChar((int) readByte()));
        }
        clear();
        return ObjectPool.toStringAndRelease(sb);
    }

    /* renamed from: j */
    public final String readHexStr() {
        return StringUtils.intern(readWideStr().toLowerCase());
    }

    /* renamed from: k */
    public final byte[] toByteArray() {
        byte[] bArr = this.data;
        int i = this.offset;
        byte[] bArr2 = new byte[this.length];
        Utils.arraycopy((Object) bArr, i, (Object) bArr2, 0, this.length);
        clear();
        return bArr2;
    }

    /* renamed from: l */
    public final boolean readBoolean() {
        return readByte() != 0;
    }

    /* renamed from: m */
    public final long readLong() {
        return (readInt() & 4294967295L) | (readInt() << 32);
    }

    /* renamed from: n */
    public final int readByteOrEOF() {
        try {
            return readUByte();
        } catch (Throwable unused) {
            return -1;
        }
    }

    /* renamed from: k */
    public final int peekUByteAt(int i) {
        return this.data[this.offset + i] & 255;
    }

    /* renamed from: o */
    public final byte readByte() {
        if (this.length <= 0) {
            throw new RuntimeException();
        }
        this.length--;
        byte[] bArr = this.data;
        int i = this.offset;
        this.offset = i + 1;
        return bArr[i];
    }

    /* renamed from: p */
    public final Vector readBufferArray() {
        Vector buffers = ObjectPool.newVector();
        readInt();
        int remaining = readInt();
        while (true) {
            remaining--;
            if (remaining < 0) {
                return buffers;
            }
            buffers.addElement(new ByteBuffer().copyFrom(this, peekIntAt(0) + 4));
        }
    }

    /* renamed from: q */
    public final int readUByte() {
        return readByte() & 255;
    }

    /* renamed from: c */
    public final int readIntoBytes(byte[] bArr) {
        if (this.length == 0) {
            return -1;
        }
        int count = Utils.min(bArr.length, this.length);
        readInto(bArr, 0, count);
        return count;
    }

    /* renamed from: r */
    public final Image toImage() {
        try {
            return ImageExtractor.toImage(this.data, this.offset, this.length);
        } finally {
            clear();
        }
    }

    /* renamed from: s */
    public final ByteBuffer extractPNG() {
        return ImageExtractor.extractPNG(this);
    }

    /* renamed from: t */
    public final ByteBuffer extractJPEG() {
        return ImageExtractor.extractJPEG(this);
    }

    /* renamed from: l */
    public final int peekShortBE(int i) {
        return ((this.data[this.offset + i] & 255) << 8) | (this.data[this.offset + i + 1] & 255);
    }

    /* renamed from: K */
    private int peekShortLE() {
        return ((this.data[this.offset + 1] & 255) << 8) | (this.data[this.offset] & 255);
    }

    /* renamed from: u */
    public final int readShortBE() {
        this.length -= 2;
        byte[] bArr = this.data;
        int i = this.offset;
        this.offset = i + 1;
        int i2 = (bArr[i] & 255) << 8;
        byte[] bArr2 = this.data;
        int i3 = this.offset;
        this.offset = i3 + 1;
        return i2 | (bArr2[i3] & 255);
    }

    /* renamed from: v */
    public final int readShortLE() {
        this.length -= 2;
        byte[] bArr = this.data;
        int i = this.offset;
        this.offset = i + 1;
        int i2 = bArr[i] & 255;
        byte[] bArr2 = this.data;
        int i3 = this.offset;
        this.offset = i3 + 1;
        return i2 | ((bArr2[i3] & 255) << 8);
    }

    /* renamed from: w */
    public final int readIntBE() {
        int i = ((((((this.data[this.offset] & 255) << 8) | (this.data[this.offset + 1] & 255)) << 8) | (this.data[this.offset + 2] & 255)) << 8) | (this.data[this.offset + 3] & 255);
        this.offset += 4;
        this.length -= 4;
        return i;
    }

    /* renamed from: x */
    public final int readIntBEAt() {
        return ((((((this.data[this.offset + 12] & 255) << 8) | (this.data[this.offset + 13] & 255)) << 8) | (this.data[this.offset + 14] & 255)) << 8) | (this.data[this.offset + 15] & 255);
    }

    /* renamed from: m */
    public final ByteBuffer writeShortBE(int i) {
        ensureCapacity(2);
        byte[] bArr = this.data;
        int i2 = this.length;
        this.length = i2 + 1;
        bArr[i2] = (byte) (i >> 8);
        byte[] bArr2 = this.data;
        int i3 = this.length;
        this.length = i3 + 1;
        bArr2[i3] = (byte) i;
        return this;
    }

    /* renamed from: n */
    public final ByteBuffer writeShortLE(int i) {
        ensureCapacity(2);
        byte[] bArr = this.data;
        int i2 = this.length;
        this.length = i2 + 1;
        bArr[i2] = (byte) i;
        byte[] bArr2 = this.data;
        int i3 = this.length;
        this.length = i3 + 1;
        bArr2[i3] = (byte) (i >> 8);
        return this;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v3, resolved type: byte[] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v4, types: [int] */
    /* renamed from: o */
    public final ByteBuffer writeIntBE(int i) {
        ensureCapacity(4);
        byte[] bArr = this.data;
        int i2 = this.length;
        this.length = i2 + 1;
        bArr[i2] = (byte) (i >> 24);
        byte[] bArr2 = this.data;
        int i3 = this.length;
        this.length = i3 + 1;
        bArr2[i3] = (byte) (i >> 16);
        byte[] bArr3 = this.data;
        int i4 = this.length;
        this.length = i4 + 1;
        bArr3[i4] = (byte) (i >> 8);
        byte[] bArr4 = this.data;
        int i5 = this.length;
        this.length = i5 + 1;
        bArr4[i5] = (byte) i;
        return this;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v9, resolved type: byte[] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v21, types: [int] */
    /* renamed from: p */
    public final ByteBuffer writeIntLE(int i) {
        ensureCapacity(4);
        byte[] bArr = this.data;
        int i2 = this.length;
        this.length = i2 + 1;
        bArr[i2] = (byte) i;
        byte[] bArr2 = this.data;
        int i3 = this.length;
        this.length = i3 + 1;
        bArr2[i3] = (byte) (i >> 8);
        byte[] bArr3 = this.data;
        int i4 = this.length;
        this.length = i4 + 1;
        bArr3[i4] = (byte) (i >> 16);
        byte[] bArr4 = this.data;
        int i5 = this.length;
        this.length = i5 + 1;
        bArr4[i5] = (byte) (i >> 24);
        return this;
    }

    /* renamed from: f */
    public final ByteBuffer writeShortString(String str) {
        int length = str.length();
        ensureCapacity(2 + length);
        writeShortBE(length);
        for (int i = 0; i < length; i++) {
            byte[] bArr = this.data;
            int i2 = this.length;
            this.length = i2 + 1;
            bArr[i2] = (byte) str.charAt(i);
        }
        return this;
    }

    /* renamed from: y */
    public final ByteBuffer updateLength() {
        ensureCapacity(0);
        byte[] bArr = this.data;
        int i = this.length - 6;
        bArr[4] = (byte) (i >> 8);
        this.data[5] = (byte) i;
        return this;
    }

    /* renamed from: z */
    public final String readLenPrefixStr() {
        int i = this.data[this.offset] & 255;
        byte[] bArr = this.data;
        int i2 = this.offset + 1;
        this.offset = i2;
        String result = StringUtils.intern(new String(bArr, i2, i));
        this.offset += i;
        this.length -= i + 1;
        return result;
    }

    /* renamed from: A */
    public final String readVarLenStr() {
        String decoded = StringUtils.decodeFromBytes(this.data, this.offset);
        skip(2 + peekShortBE(0));
        return decoded;
    }

    /* renamed from: B */
    public final ByteBuffer encryptMD5() {
        ensureCapacity(0);
        byte[] hash = Md5Hash.hash(this.data, this.length);
        clear();
        this.data = hash;
        this.length = 16;
        return this;
    }

    /* renamed from: C */
    public final String readPascalStr() {
        int strLen = peekShortLE() - 1;
        if (strLen <= 0) {
            skip(3);
            return AppState.emptyStr;
        }
        this.data[this.offset] = (byte) (strLen >> 8);
        this.data[this.offset + 1] = (byte) strLen;
        String decoded = readByteStr();
        readByte();
        return decoded;
    }

    /* renamed from: D */
    public final String readModifiedStr() {
        byte[] bArr = this.data;
        int i = this.offset;
        int strLen = peekShortLE();
        bArr[i] = (byte) (strLen >> 8);
        this.data[this.offset + 1] = (byte) strLen;
        return isValidUTF(this.offset + 2, strLen) ? readVarLenStr() : readByteStr();
    }

    /* renamed from: E */
    public final String readModifiedStrTrim() {
        byte[] bArr = this.data;
        int i = this.offset;
        int strLen = peekShortLE();
        bArr[i] = (byte) (strLen >> 8);
        this.data[this.offset + 1] = (byte) strLen;
        String decoded = isValidUTF(this.offset + 2, strLen) ? readVarLenStr() : readByteStr();
        String str = decoded;
        int length = decoded.length();
        return length > 0 ? StringUtils.prefix(str, length - 1) : str;
    }

    /* renamed from: q */
    public final String readUnicodeChars(int i) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (true) {
            i -= 2;
            if (i < 0) {
                return ObjectPool.toStringAndRelease(sb);
            }
            sb.append((char) readShortBE());
        }
    }

    /* renamed from: r */
    public final String readByteChars(int i) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (true) {
            i--;
            if (i < 0) {
                return ObjectPool.toStringAndRelease(sb);
            }
            sb.append(Utils.win1251ToChar(readUByte()));
        }
    }

    /* renamed from: g */
    public final ByteBuffer writeCharBytes(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            writeByte(Utils.charToWin1251(str.charAt(i)));
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer writeProtocolStr(int i, String str) {
        int length = str.length();
        return length > 0 ? writeShortBE(i).writeShortLE(length + 3).writeShortLE(length + 1).writeCharBytes(str).writeByte(0) : this;
    }

    /* renamed from: h */
    public final ByteBuffer writeByteLenStr(String str) {
        int length = str.length();
        writeByte(length);
        for (int i = 0; i < length; i++) {
            writeByte(str.charAt(i));
        }
        return this;
    }

    /* renamed from: i */
    public final ByteBuffer writeAsShorts(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            writeShortBE(str.charAt(i));
        }
        return this;
    }

    /* renamed from: l */
    private static byte[] encodeUTF(String str) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream2 = (ByteArrayOutputStream) IOUtils.registerResource((Object) new ByteArrayOutputStream());
            byteArrayOutputStream = byteArrayOutputStream2;
            DataOutputStream dataOutputStream2 = (DataOutputStream) IOUtils.registerResource((Object) new DataOutputStream(byteArrayOutputStream2));
            dataOutputStream = dataOutputStream2;
            dataOutputStream2.writeUTF(str);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            IOUtils.closeOutput((OutputStream) dataOutputStream);
            IOUtils.closeOutput((OutputStream) byteArrayOutputStream);
            return byteArray;
        } catch (Throwable th) {
            IOUtils.closeOutput((OutputStream) dataOutputStream);
            IOUtils.closeOutput((OutputStream) byteArrayOutputStream);
            if (th instanceof RuntimeException) throw (RuntimeException) th;
            if (th instanceof Error) throw (Error) th;
            throw new RuntimeException(th);
        }
    }

    /* renamed from: j */
    public final ByteBuffer writeUTF(String str) {
        byte[] encoded = encodeUTF(str);
        writeBytes(encoded);
        ObjectPool.releaseBytes(encoded);
        return this;
    }

    /* renamed from: k */
    public final ByteBuffer writeUTFNoLen(String str) {
        byte[] encoded = encodeUTF(str);
        writeBytesAt(encoded, 2, encoded.length - 2);
        ObjectPool.releaseBytes(encoded);
        return this;
    }

    /* renamed from: L */
    private String readByteStr() {
        return readByteChars(readShortBE());
    }

    /* renamed from: a */
    private final boolean isValidUTF(int i, int i2) {
        if (i2 <= 0) {
            return false;
        }
        byte[] bArr = this.data;
        while (i2 > 0) {
            int i3 = i;
            i++;
            byte b = bArr[i3];
            i2--;
            int i4 = (b & 224) == 192 ? 1 : (b & 240) == 224 ? 2 : (b & 248) == 240 ? 3 : (b & 252) == 248 ? 4 : (b & 254) == 252 ? 5 : 0;
            if (i4 != 0) {
                while (true) {
                    i4--;
                    if (i4 < 0) {
                        if (i2 == 0) {
                            return true;
                        }
                    } else {
                        if (i2 <= 0) {
                            return false;
                        }
                        int i5 = i;
                        i++;
                        if ((bArr[i5] & 192) != 128) {
                            return false;
                        }
                        i2--;
                    }
                }
            } else if ((b & 128) == 128) {
                return false;
            }
        }
        return true;
    }

    /* renamed from: F */
    public final ByteBuffer duplicate() {
        return new ByteBuffer().writeBytesAt(this.data, this.offset, this.length);
    }

    /* renamed from: G */
    public final String readUTFWithLen() {
        ensureCapacity(2);
        byte[] bArr = this.data;
        int i = this.length;
        Utils.arraycopy((Object) bArr, 0, (Object) bArr, 2, i);
        bArr[0] = (byte) (i >>> 8);
        bArr[1] = (byte) i;
        String decoded = StringUtils.decodeFromBytes(bArr, this.offset);
        clear();
        return decoded;
    }

    /* renamed from: s */
    public final ByteBuffer writeIntAsString(int i) {
        return writeRawString(StringUtils.intern(Integer.toString(i)));
    }

    /* renamed from: b */
    public final ByteBuffer writeLongAsString(long j) {
        return writeRawString(StringUtils.intern(Long.toString(j)));
    }

    /* renamed from: t */
    public final ByteBuffer writeIntWithLen(int i) {
        String result = StringUtils.intern(Integer.toString(i));
        return writeIntLE(result.length()).writeRawString(result);
    }

    /* renamed from: u */
    public final ByteBuffer writeUInt(int i) {
        return writeLongBytes(i & 4294967295L);
    }

    /* renamed from: c */
    public final ByteBuffer writeLongBytes(long j) {
        while (j != 0) {
            writeByte((int) j);
            j >>>= 8;
        }
        return this;
    }

    /* JADX DEBUG: Move duplicate insns, count: 2 to block B:11:0x005d */
    /* renamed from: H */
    public final String toHexString() {
        String result = HexEncoder.encode(this.data, this.offset, this.length);
        clear();
        return result;
    }

    /* renamed from: I */
    public final XmlElement parseXml() {
        return XmlParser.parseFromBuffer(this);
    }

    /* renamed from: J */
    public final XmlElement parseXmlStr() {
        return XmlParser.parseFromString(this);
    }

    /* renamed from: v */
    public final ByteBuffer writeIntMixed(int i) {
        return writeIntLE(i & 255).writeByte(i >>> 8);
    }

    /* renamed from: b */
    public final ByteBuffer writeStringArr(String[] strArr) {
        ByteBuffer buf = new ByteBuffer();
        int length = strArr == null ? 0 : strArr.length;
        int i = length;
        buf.writeIntLE(length);
        for (int i2 = 0; i2 < i; i2++) {
            buf.writeStringLatin1(strArr[i2]);
        }
        return writeBufferIntLen(buf);
    }

    /* renamed from: a */
    public final ByteBuffer writeVector(Vector vector) {
        ByteBuffer buf = new ByteBuffer();
        buf.writeIntLE(0);
        return writeBufferIntLen(buf);
    }
}
