package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.net.HttpClient;

import javax.microedition.lcdui.Image;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public final class ByteBuffer {

    private static final int DEFAULT_BUFFER_SIZE = 2048;
    private static final int STREAM_READ_BUFFER_SIZE = 8192;
    private static final int CAPACITY_PADDING = 32;
    private static final int MD5_HASH_LENGTH = 16;

    public byte[] data;
    public int length;
    public int offset;

    // --- Low-level byte primitives ---

    private void putByte(int value) {
        this.data[this.length++] = (byte) value;
    }

    private int takeByte() {
        this.length--;
        return this.data[this.offset++] & 0xFF;
    }

    private int peekByte(int relOffset) {
        return this.data[this.offset + relOffset] & 0xFF;
    }

    // --- Constructors ---

    public ByteBuffer() {
        this.data = AppState.emptyBytes;
    }

    public ByteBuffer(HttpClient client) {
        this.data = ObjectPool.newBytes(DEFAULT_BUFFER_SIZE);
        try {
            byte[] readBuf = ObjectPool.newBytes(DEFAULT_BUFFER_SIZE);
            int bytesRead;
            while ((bytesRead = client.readData(readBuf)) >= 0) {
                writeBytesAt(readBuf, 0, bytesRead);
            }
            ObjectPool.releaseBytes(readBuf);
        } catch (Throwable unused) {
        }
    }

    public ByteBuffer(String resourcePath) {
        this(resourcePath, DEFAULT_BUFFER_SIZE);
    }

    public ByteBuffer(String resourcePath, int initialCapacity) {
        this((InputStream) IOUtils.registerResource((Object) resourcePath.getClass().getResourceAsStream(resourcePath)), initialCapacity);
    }

    private ByteBuffer(InputStream stream, int initialCapacity) {
        this.data = ObjectPool.newBytes(initialCapacity);
        try {
            byte[] readBuf = ObjectPool.newBytes(STREAM_READ_BUFFER_SIZE);
            int bytesRead;
            while ((bytesRead = stream.read(readBuf)) >= 0) {
                writeBytesAt(readBuf, 0, bytesRead);
            }
            ObjectPool.releaseBytes(readBuf);
        } catch (Throwable unused) {
        }
        IOUtils.closeInput(stream);
    }

    // --- Buffer management ---

    public ByteBuffer compact() {
        int len = this.length;
        if (len == 0) {
            clear();
        } else {
            byte[] oldData = this.data;
            if (len < oldData.length) {
                ensureCapacity(0);
                byte[] compactData = ObjectPool.reallocBytes(oldData, len);
                if (compactData != null) {
                    this.data = compactData;
                }
            }
        }
        return this;
    }

    public ByteBuffer ensureCapacity(int extraBytes) {
        byte[] oldData = this.data;
        int oldCapacity = oldData.length;
        int currentLength = this.length;
        int requiredCapacity = currentLength + extraBytes;
        boolean needsRealloc = oldCapacity < requiredCapacity;
        byte[] target = needsRealloc ? ObjectPool.newBytes(requiredCapacity + CAPACITY_PADDING) : oldData;
        if (needsRealloc || this.offset != 0) {
            Utils.arraycopy((Object) oldData, this.offset, (Object) target, 0, currentLength);
            if (needsRealloc) {
                ObjectPool.releaseBytes(oldData);
                this.data = target;
            }
        }
        this.offset = 0;
        return this;
    }

    public ByteBuffer clear() {
        ObjectPool.releaseBytes(this.data);
        this.data = AppState.emptyBytes;
        this.length = 0;
        this.offset = 0;
        return this;
    }

    public ByteBuffer skip(int count) {
        this.offset += count;
        int remaining = this.length - count;
        this.length = remaining;
        if (remaining == 0) {
            clear();
        }
        return this;
    }

    // --- Bulk write ---

    public ByteBuffer writeBytes(byte[] src) {
        if (src != null) {
            writeBytesAt(src, 0, src.length);
        }
        return this;
    }

    public ByteBuffer writeBytesAt(byte[] src, int srcOffset, int count) {
        if (count > 0) {
            ensureCapacity(count);
            Utils.arraycopy((Object) src, srcOffset, (Object) this.data, this.length, count);
            this.length += count;
        }
        return this;
    }

    public ByteBuffer writeZeros(int count) {
        ensureCapacity(count);
        for (int n = count; n > 0; n--) {
            putByte(0);
        }
        return this;
    }

    public ByteBuffer setData(byte[] newData) {
        clear();
        this.data = newData;
        this.length = newData.length;
        return this;
    }

    // --- Bulk read ---

    public byte[] readInto(byte[] dst, int dstOffset, int count) {
        Utils.arraycopy((Object) this.data, this.offset, (Object) dst, dstOffset, count);
        this.length -= count;
        this.offset += count;
        compact();
        return dst;
    }

    public int readIntoBytes(byte[] dst) {
        if (this.length == 0) {
            return -1;
        }
        int count = Utils.min(dst.length, this.length);
        readInto(dst, 0, count);
        return count;
    }

    public byte[] toByteArray() {
        byte[] result = new byte[this.length];
        Utils.arraycopy((Object) this.data, this.offset, (Object) result, 0, this.length);
        clear();
        return result;
    }

    // --- Write single byte ---

    public ByteBuffer writeByte(int value) {
        ensureCapacity(1);
        putByte(value);
        return this;
    }

    public ByteBuffer writeBoolean(boolean flag) {
        return writeByte(flag ? 1 : 0);
    }

    // --- Write multi-byte integers ---

    public ByteBuffer writeShortBE(int value) {
        ensureCapacity(2);
        putByte(value >> 8);
        putByte(value);
        return this;
    }

    public ByteBuffer writeShortLE(int value) {
        ensureCapacity(2);
        putByte(value);
        putByte(value >> 8);
        return this;
    }

    public ByteBuffer writeIntBE(int value) {
        ensureCapacity(4);
        putByte(value >> 24);
        putByte(value >> 16);
        putByte(value >> 8);
        putByte(value);
        return this;
    }

    public ByteBuffer writeIntLE(int value) {
        ensureCapacity(4);
        putByte(value);
        putByte(value >> 8);
        putByte(value >> 16);
        putByte(value >> 24);
        return this;
    }

    public ByteBuffer writeLong(long value) {
        return writeIntLE((int) value).writeIntLE((int) (value >> 32));
    }

    public ByteBuffer writeIntMixed(int value) {
        return writeIntLE(value & 0xFF).writeByte(value >>> 8);
    }

    public ByteBuffer writeUInt(int value) {
        return writeLongBytes(value & 0xFFFFFFFFL);
    }

    public ByteBuffer writeLongBytes(long value) {
        while (value != 0) {
            writeByte((int) value);
            value >>>= 8;
        }
        return this;
    }

    // --- Read single byte ---

    public byte readByte() {
        if (this.length <= 0) {
            throw new RuntimeException();
        }
        this.length--;
        byte[] buf = this.data;
        int pos = this.offset;
        this.offset = pos + 1;
        return buf[pos];
    }

    public int readUByte() {
        return readByte() & 0xFF;
    }

    public int readByteOrEOF() {
        try {
            return readUByte();
        } catch (Throwable unused) {
            return -1;
        }
    }

    // --- Read multi-byte integers ---

    public int readShortBE() {
        return (takeByte() << 8) | takeByte();
    }

    public int readShortLE() {
        int low = takeByte();
        return low | (takeByte() << 8);
    }

    public int readIntBE() {
        return (takeByte() << 24) | (takeByte() << 16) | (takeByte() << 8) | takeByte();
    }

    public int readInt() {
        int value = peekIntAt(0);
        this.offset += 4;
        this.length -= 4;
        return value;
    }

    public boolean readBoolean() {
        return readByte() != 0;
    }

    public long readLong() {
        return (readInt() & 0xFFFFFFFFL) | (readInt() << 32);
    }

    // --- Peek (read without advancing) ---

    public int peekByteAt(int relOffset) {
        return peekByte(relOffset);
    }

    public int peekShortBE(int relOffset) {
        return (peekByte(relOffset) << 8) | peekByte(relOffset + 1);
    }

    private int peekShortLE() {
        return peekByte(0) | (peekByte(1) << 8);
    }

    public int peekIntAt(int relOffset) {
        return peekByte(relOffset)
                | (peekByte(relOffset + 1) << 8)
                | (peekByte(relOffset + 2) << 16)
                | (this.data[this.offset + relOffset + 3] << 24);
    }

    public int peekIntBEAt(int relOffset) {
        return (peekByte(relOffset) << 24)
                | (peekByte(relOffset + 1) << 16)
                | (peekByte(relOffset + 2) << 8)
                | peekByte(relOffset + 3);
    }

    // --- Write strings ---

    public ByteBuffer writeRawString(String str) {
        if (str != null && str.length() > 0) {
            int len = str.length();
            ensureCapacity(len);
            for (int i = 0; i < len; i++) {
                putByte(str.charAt(i));
            }
        }
        return this;
    }

    public ByteBuffer writeObjectStr(String str) {
        return writeRawString(str);
    }

    public ByteBuffer writeString(String str, int mode) {
        return mode != 0 ? writeStringUTF16(str) : writeStringLatin1(str);
    }

    public ByteBuffer writeStringLatin1(String str) {
        if (str == null || str.length() <= 0) {
            return writeIntLE(0);
        }
        int len = str.length();
        ensureCapacity(len + 4);
        writeIntLE(len);
        for (int i = 0; i < len; i++) {
            putByte(str.charAt(i));
        }
        return this;
    }

    public ByteBuffer writeStringUTF16(String str) {
        if (str == null || str.length() <= 0) {
            return writeIntLE(0);
        }
        int charCount = str.length();
        ensureCapacity(charCount * 2 + 4);
        writeIntLE(charCount * 2);
        for (int i = 0; i < charCount; i++) {
            char ch = str.charAt(i);
            putByte(ch);
            putByte(ch >> 8);
        }
        return this;
    }

    public ByteBuffer writeShortString(String str) {
        int len = str.length();
        ensureCapacity(2 + len);
        writeShortBE(len);
        for (int i = 0; i < len; i++) {
            putByte(str.charAt(i));
        }
        return this;
    }

    public ByteBuffer writeByteLenStr(String str) {
        int len = str.length();
        writeByte(len);
        for (int i = 0; i < len; i++) {
            writeByte(str.charAt(i));
        }
        return this;
    }

    public ByteBuffer writeCharBytes(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            writeByte(Utils.charToWin1251(str.charAt(i)));
        }
        return this;
    }

    public ByteBuffer writeAsShorts(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            writeShortBE(str.charAt(i));
        }
        return this;
    }

    public ByteBuffer writeIntAsString(int value) {
        return writeRawString(StringUtils.intern(Integer.toString(value)));
    }

    public ByteBuffer writeLongAsString(long value) {
        return writeRawString(StringUtils.intern(Long.toString(value)));
    }

    public ByteBuffer writeIntWithLen(int value) {
        String str = StringUtils.intern(Integer.toString(value));
        return writeIntLE(str.length()).writeRawString(str);
    }

    // --- Read strings ---

    public String readStringByMode(int mode) {
        return mode != 0 ? readUnicodeStr() : readUTF8Str((String) null);
    }

    public String readUnicodeStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int remaining = readInt(); remaining > 0; remaining--) {
            sb.append(Utils.win1251ToChar((int) readByte()));
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public String readWideStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int remaining = readInt(); remaining > 0; remaining--) {
            sb.append((char) readUByte());
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public String readUTF8Str(String fallback) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int remaining = readInt();
        for (; remaining >= 2; remaining -= 2) {
            sb.append((char) (readUByte() | (readByte() << 8)));
        }
        if (remaining == 1) {
            readByte();
            if (fallback != null) {
                return fallback;
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public String readAllWideStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (this.length > 0) {
            sb.append((char) (readUByte() | (readByte() << 8)));
        }
        clear();
        return ObjectPool.toStringAndRelease(sb);
    }

    public String readAllByteStr() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (this.length > 0) {
            sb.append(Utils.win1251ToChar((int) readByte()));
        }
        clear();
        return ObjectPool.toStringAndRelease(sb);
    }

    public String readUnicodeChars(int byteCount) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int remaining = byteCount; remaining >= 2; remaining -= 2) {
            sb.append((char) readShortBE());
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public String readByteChars(int count) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int n = count; n > 0; n--) {
            sb.append(Utils.win1251ToChar(readUByte()));
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public String readHexStr() {
        return StringUtils.intern(readWideStr().toLowerCase());
    }

    public String readLenPrefixStr() {
        int len = peekByte(0);
        this.offset++;
        this.length--;
        String result = StringUtils.intern(new String(this.data, this.offset, len));
        this.offset += len;
        this.length -= len;
        return result;
    }

    public String readVarLenStr() {
        String decoded = StringUtils.decodeFromBytes(this.data, this.offset);
        skip(2 + peekShortBE(0));
        return decoded;
    }

    public String readPascalStr() {
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

    public String readModifiedStr() {
        int strLen = peekShortLE();
        this.data[this.offset] = (byte) (strLen >> 8);
        this.data[this.offset + 1] = (byte) strLen;
        return isValidUTF(this.offset + 2, strLen) ? readVarLenStr() : readByteStr();
    }

    public String readModifiedStrTrim() {
        int strLen = peekShortLE();
        this.data[this.offset] = (byte) (strLen >> 8);
        this.data[this.offset + 1] = (byte) strLen;
        String decoded = isValidUTF(this.offset + 2, strLen) ? readVarLenStr() : readByteStr();
        int len = decoded.length();
        return len > 0 ? StringUtils.prefix(decoded, len - 1) : decoded;
    }

    public String getStringAndClear() {
        String result = this.length == 0
                ? AppState.emptyStr
                : StringUtils.intern(new String(this.data, this.offset, this.length));
        clear();
        return result;
    }

    public String readUTFWithLen() {
        ensureCapacity(2);
        byte[] buf = this.data;
        int len = this.length;
        Utils.arraycopy((Object) buf, 0, (Object) buf, 2, len);
        buf[0] = (byte) (len >>> 8);
        buf[1] = (byte) len;
        String decoded = StringUtils.decodeFromBytes(buf, this.offset);
        clear();
        return decoded;
    }

    private String readByteStr() {
        return readByteChars(readShortBE());
    }

    // --- UTF encoding ---

    public ByteBuffer writeUTF(String str) {
        byte[] encoded = encodeUTF(str);
        writeBytes(encoded);
        ObjectPool.releaseBytes(encoded);
        return this;
    }

    public ByteBuffer writeUTFNoLen(String str) {
        byte[] encoded = encodeUTF(str);
        writeBytesAt(encoded, 2, encoded.length - 2);
        ObjectPool.releaseBytes(encoded);
        return this;
    }

    private static byte[] encodeUTF(String str) {
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try {
            baos = (ByteArrayOutputStream) IOUtils.registerResource((Object) new ByteArrayOutputStream());
            dos = (DataOutputStream) IOUtils.registerResource((Object) new DataOutputStream(baos));
            dos.writeUTF(str);
            byte[] result = baos.toByteArray();
            IOUtils.closeOutput((OutputStream) dos);
            IOUtils.closeOutput((OutputStream) baos);
            return result;
        } catch (Throwable th) {
            IOUtils.closeOutput((OutputStream) dos);
            IOUtils.closeOutput((OutputStream) baos);
            if (th instanceof RuntimeException) throw (RuntimeException) th;
            if (th instanceof Error) throw (Error) th;
            throw new RuntimeException(th.toString());
        }
    }

    private boolean isValidUTF(int startPos, int byteCount) {
        if (byteCount <= 0) {
            return false;
        }
        byte[] buf = this.data;
        int pos = startPos;
        int remaining = byteCount;
        while (remaining > 0) {
            byte b = buf[pos++];
            remaining--;
            int trailingBytes;
            if ((b & 0xE0) == 0xC0) trailingBytes = 1;
            else if ((b & 0xF0) == 0xE0) trailingBytes = 2;
            else if ((b & 0xF8) == 0xF0) trailingBytes = 3;
            else if ((b & 0xFC) == 0xF8) trailingBytes = 4;
            else if ((b & 0xFE) == 0xFC) trailingBytes = 5;
            else if ((b & 0x80) == 0x80) return false;
            else {
                trailingBytes = 0;
            }
            if (trailingBytes != 0) {
                for (int t = trailingBytes; t > 0; t--) {
                    if (remaining <= 0) {
                        return false;
                    }
                    if ((buf[pos++] & 0xC0) != 0x80) {
                        return false;
                    }
                    remaining--;
                }
                if (remaining == 0) {
                    return true;
                }
            }
        }
        return true;
    }

    // --- Buffer composition ---

    ByteBuffer copyFrom(ByteBuffer src, int count) {
        byte[] tempBuf = ObjectPool.newBytes(count);
        src.readInto(tempBuf, 0, count);
        writeBytesAt(tempBuf, 0, count);
        ObjectPool.releaseBytes(tempBuf);
        return this;
    }

    public ByteBuffer writeBuffer(ByteBuffer src) {
        if (src != null) {
            copyFrom(src, src.length);
        }
        return this;
    }

    public ByteBuffer writeBufferShortLen(ByteBuffer src) {
        if (src != null) {
            writeShortBE(src.length).copyFrom(src, src.length);
        }
        return this;
    }

    public ByteBuffer writeBufferIntLen(ByteBuffer src) {
        return src != null ? writeIntLE(src.length).copyFrom(src, src.length) : writeIntLE(0);
    }

    public ByteBuffer duplicate() {
        return new ByteBuffer().writeBytesAt(this.data, this.offset, this.length);
    }

    public Vector readBufferArray() {
        Vector buffers = ObjectPool.newVector();
        readInt();
        for (int remaining = readInt(); remaining > 0; remaining--) {
            buffers.addElement(new ByteBuffer().copyFrom(this, peekIntAt(0) + 4));
        }
        return buffers;
    }

    public ByteBuffer writeStringArray(String[] pair) {
        ByteBuffer inner = new ByteBuffer();
        inner.writeIntLE(2);
        for (int i = 0; i < 2; i++) {
            inner.writeStringUTF16(pair[i]);
        }
        return writeStringLatin1(inner.toBase64());
    }

    public ByteBuffer writeStringArr(String[] items) {
        ByteBuffer inner = new ByteBuffer();
        int count = items == null ? 0 : items.length;
        inner.writeIntLE(count);
        for (int i = 0; i < count; i++) {
            inner.writeStringLatin1(items[i]);
        }
        return writeBufferIntLen(inner);
    }

    // --- AppState integration ---

    public ByteBuffer writeEncodedInt(int key) {
        return writeRawString(AppState.getString(key));
    }

    public ByteBuffer writeExtendedInt(int key) {
        writeRawString(AppState.getString(key & 0xFFFF));
        int highByte = key >>> 16;
        if (highByte != 0) {
            writeByte(highByte);
        }
        return this;
    }

    // --- Conversion delegates (same-package utilities) ---

    public ByteBuffer encryptMD5() {
        ensureCapacity(0);
        byte[] hash = Md5Hash.hash(this.data, this.length);
        clear();
        this.data = hash;
        this.length = MD5_HASH_LENGTH;
        return this;
    }

    public String toBase64() {
        ensureCapacity(0);
        String result = Base64.encode(this.data, 0, this.length);
        clear();
        return result;
    }

    public String toHexString() {
        String result = HexEncoder.encode(this.data, this.offset, this.length);
        clear();
        return result;
    }

    public XmlElement parseXml() {
        return XmlParser.parseFromBuffer(this);
    }

    public XmlElement parseXmlStr() {
        return XmlParser.parseFromString(this);
    }

    public Image toImage() {
        try {
            return ImageExtractor.toImage(this.data, this.offset, this.length);
        } finally {
            clear();
        }
    }

    public ByteBuffer extractPNG() {
        return ImageExtractor.extractPNG(this);
    }

    public ByteBuffer extractJPEG() {
        return ImageExtractor.extractJPEG(this);
    }
}
