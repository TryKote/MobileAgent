package p000;

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
        this.data = AppState.f176a;
    }

    public ByteBuffer(HttpClient c0024ax) {
        this.data = NetworkUtils.m1211a(2048);
        try {
            byte[] bArrM1211a = NetworkUtils.m1211a(2048);
            while (true) {
                int iM638a = c0024ax.m638a(bArrM1211a);
                if (iM638a < 0) {
                    NetworkUtils.m1209a(bArrM1211a);
                    return;
                }
                writeBytesAt(bArrM1211a, 0, iM638a);
            }
        } catch (Throwable unused) {
        }
    }

    public ByteBuffer(String str) {
        this(str, 2048);
    }

    public ByteBuffer(String str, int i) {
        this((InputStream) IOUtils.m761a((Object) str.getClass().getResourceAsStream(str)), i);
    }

    private ByteBuffer(InputStream inputStream, int i) {
        this.data = NetworkUtils.m1211a(i);
        try {
            byte[] bArrM1211a = NetworkUtils.m1211a(8192);
            while (true) {
                int i2 = inputStream.read(bArrM1211a);
                if (i2 < 0) {
                    break;
                } else {
                    writeBytesAt(bArrM1211a, 0, i2);
                }
            }
            NetworkUtils.m1209a(bArrM1211a);
        } catch (Throwable unused) {
        }
        IOUtils.m763a(inputStream);
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
                byte[] bArrM1210a = NetworkUtils.m1210a(bArr, i);
                if (bArrM1210a != null) {
                    this.data = bArrM1210a;
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
        byte[] bArrM1211a = z ? NetworkUtils.m1211a(i3 + 32) : bArr;
        if (z2 || this.offset != 0) {
            Utils.m490a((Object) bArr, this.offset, (Object) bArrM1211a, 0, i2);
            if (z2) {
                NetworkUtils.m1209a(bArr);
                this.data = bArrM1211a;
            }
        }
        this.offset = 0;
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer clear() {
        NetworkUtils.m1209a(this.data);
        this.data = AppState.f176a;
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
            Utils.m490a((Object) bArr, i, (Object) this.data, this.length, i2);
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
        Utils.m490a((Object) this.data, this.offset, (Object) bArr, i, i2);
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
            char cCharAt = str.charAt(i2);
            bArr[i3] = (byte) cCharAt;
            byte[] bArr2 = this.data;
            int i4 = this.length;
            this.length = i4 + 1;
            bArr2[i4] = (byte) (cCharAt >> '\b');
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer writeCompressed(int i) {
        return i > 5179 ? writeBytesAt(AppState.m581a(295), i & 65535, i >> 16) : writeBytes(AppState.m581a(i));
    }

    /* renamed from: d */
    public final ByteBuffer writeEncodedInt(int i) {
        return writeRawString(AppState.m584b(i));
    }

    /* renamed from: e */
    public final ByteBuffer writeExtendedInt(int i) {
        writeRawString(AppState.m584b(i & 65535));
        int i2 = i >>> 16;
        if (i2 != 0) {
            writeByte(i2);
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer writeEncodedString(String str) {
        return writeRawString(Conversation.m1120b((Object) str));
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
        return writeRawString(Conversation.m1124l((String) obj));
    }

    /* renamed from: b */
    public final ByteBuffer writeObjectStr(Object obj) {
        return writeRawString((String) obj);
    }

    /* renamed from: c */
    public final String getStringAndClear() {
        String strM17c = this.length == 0 ? AppState.f181d : StringUtils.m17c(new String(this.data, this.offset, this.length));
        clear();
        return strM17c;
    }

    /* renamed from: a */
    public final ByteBuffer writeStringArray(String[] strArr) {
        ByteBuffer c0043n = new ByteBuffer();
        c0043n.writeIntLE(2);
        for (int i = 0; i < 2; i++) {
            c0043n.writeStringUTF16(strArr[i]);
        }
        return writeStringLatin1(c0043n.toBase64());
    }

    /* renamed from: w */
    private static char base64Char(int i) {
        return (char) AppState.m581a(961)[i & 63];
    }

    /* renamed from: d */
    public final String toBase64() {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        ensureCapacity(0);
        int i = 0;
        int i2 = this.length;
        boolean z = true;
        while (z) {
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            if (i < i2) {
                int i7 = i;
                i++;
                i3 = this.data[i7] & 255;
                i6 = 0 + 1;
            }
            if (i < i2) {
                int i8 = i;
                i++;
                i4 = this.data[i8] & 255;
                i6++;
            }
            if (i < i2) {
                int i9 = i;
                i++;
                i5 = this.data[i9] & 255;
                i6++;
            } else {
                z = false;
            }
            if (i6 > 0) {
                int i10 = (i3 << 16) | (i4 << 8) | i5;
                stringBufferM1217h.append(base64Char(i10 >> 18)).append(base64Char(i10 >> 12)).append(i6 > 1 ? base64Char(i10 >> 6) : '=').append(i6 > 2 ? base64Char(i10) : '=');
            }
        }
        clear();
        return NetworkUtils.m1215a(stringBufferM1217h);
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
    private ByteBuffer copyFrom(ByteBuffer c0043n, int i) {
        byte[] bArrM1211a = NetworkUtils.m1211a(i);
        c0043n.readInto(bArrM1211a, 0, i);
        writeBytesAt(bArrM1211a, 0, i);
        NetworkUtils.m1209a(bArrM1211a);
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer writeBuffer(ByteBuffer c0043n) {
        if (c0043n != null) {
            copyFrom(c0043n, c0043n.length);
        }
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer writeBufferShortLen(ByteBuffer c0043n) {
        if (c0043n != null) {
            writeShortBE(c0043n.length).copyFrom(c0043n, c0043n.length);
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer writeBufferIntLen(ByteBuffer c0043n) {
        return c0043n != null ? writeIntLE(c0043n.length).copyFrom(c0043n, c0043n.length) : writeIntLE(0);
    }

    /* renamed from: e */
    public final int readInt() {
        int iM1330h = peekIntAt(0);
        this.offset += 4;
        this.length -= 4;
        return iM1330h;
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
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int iM1328e = readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return NetworkUtils.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append(Utils.m499a((int) readByte()));
        }
    }

    /* renamed from: g */
    public final String readWideStr() {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int iM1328e = readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return NetworkUtils.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append((char) readUByte());
        }
    }

    /* renamed from: e */
    public final String readUTF8Str(String str) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int iM1328e = readInt();
        while (true) {
            iM1328e -= 2;
            if (iM1328e < 0) {
                break;
            }
            stringBufferM1217h.append((char) (readUByte() | (readByte() << 8)));
        }
        if (iM1328e == -1) {
            readByte();
            if (str != null) {
                return str;
            }
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: h */
    public final String readAllWideStr() {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        while (this.length > 0) {
            stringBufferM1217h.append((char) (readUByte() | (readByte() << 8)));
        }
        clear();
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: i */
    public final String readAllByteStr() {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        while (this.length > 0) {
            stringBufferM1217h.append(Utils.m499a((int) readByte()));
        }
        clear();
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: j */
    public final String readHexStr() {
        return StringUtils.m17c(readWideStr().toLowerCase());
    }

    /* renamed from: k */
    public final byte[] toByteArray() {
        byte[] bArr = this.data;
        int i = this.offset;
        byte[] bArr2 = new byte[this.length];
        Utils.m490a((Object) bArr, i, (Object) bArr2, 0, this.length);
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
        Vector vectorM1213g = NetworkUtils.m1213g();
        readInt();
        int iM1328e = readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return vectorM1213g;
            }
            vectorM1213g.addElement(new ByteBuffer().copyFrom(this, peekIntAt(0) + 4));
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
        int iM503b = Utils.m503b(bArr.length, this.length);
        readInto(bArr, 0, iM503b);
        return iM503b;
    }

    /* renamed from: r */
    public final Image toImage() {
        try {
            return Image.createImage(this.data, this.offset, this.length);
        } finally {
            clear();
        }
    }

    /* renamed from: s */
    public final ByteBuffer extractPNG() {
        int i = this.length;
        if (i >= 4 && peekIntAt(0) != -559038737) {
            throw new RuntimeException();
        }
        if (i < 44 || i < 44 + peekIntAt(16)) {
            return null;
        }
        return new ByteBuffer().copyFrom(this, 44 + peekIntAt(16)).compact();
    }

    /* renamed from: t */
    public final ByteBuffer extractJPEG() {
        int i = this.length;
        if (i < 6) {
            return null;
        }
        if (peekByteAt(0) != 42) {
            throw new RuntimeException();
        }
        int iM1331i = peekByteAt(1);
        if (iM1331i < 1 || iM1331i > 5) {
            throw new RuntimeException();
        }
        int iM1351l = peekShortBE(4);
        if (iM1351l + 6 > i) {
            return null;
        }
        return new ByteBuffer().copyFrom(this, iM1351l + 6).compact();
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
        String strM17c = StringUtils.m17c(new String(bArr, i2, i));
        this.offset += i;
        this.length -= i + 1;
        return strM17c;
    }

    /* renamed from: A */
    public final String readVarLenStr() {
        String strM0a = StringUtils.m0a(this.data, this.offset);
        skip(2 + peekShortBE(0));
        return strM0a;
    }

    /* renamed from: B */
    public final ByteBuffer encryptMD5() {
        ensureCapacity(0);
        byte[] bArrM1090a = Conversation.m1090a(this.data, this.length);
        clear();
        this.data = bArrM1090a;
        this.length = 16;
        return this;
    }

    /* renamed from: C */
    public final String readPascalStr() {
        int iM1352K = peekShortLE() - 1;
        if (iM1352K <= 0) {
            skip(3);
            return AppState.f181d;
        }
        this.data[this.offset] = (byte) (iM1352K >> 8);
        this.data[this.offset + 1] = (byte) iM1352K;
        String strM1378L = readByteStr();
        readByte();
        return strM1378L;
    }

    /* renamed from: D */
    public final String readModifiedStr() {
        byte[] bArr = this.data;
        int i = this.offset;
        int iM1352K = peekShortLE();
        bArr[i] = (byte) (iM1352K >> 8);
        this.data[this.offset + 1] = (byte) iM1352K;
        return isValidUTF(this.offset + 2, iM1352K) ? readVarLenStr() : readByteStr();
    }

    /* renamed from: E */
    public final String readModifiedStrTrim() {
        byte[] bArr = this.data;
        int i = this.offset;
        int iM1352K = peekShortLE();
        bArr[i] = (byte) (iM1352K >> 8);
        this.data[this.offset + 1] = (byte) iM1352K;
        String strM1364A = isValidUTF(this.offset + 2, iM1352K) ? readVarLenStr() : readByteStr();
        String str = strM1364A;
        int length = strM1364A.length();
        return length > 0 ? StringUtils.m13b(str, length - 1) : str;
    }

    /* renamed from: q */
    public final String readUnicodeChars(int i) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        while (true) {
            i -= 2;
            if (i < 0) {
                return NetworkUtils.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append((char) readShortBE());
        }
    }

    /* renamed from: r */
    public final String readByteChars(int i) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        while (true) {
            i--;
            if (i < 0) {
                return NetworkUtils.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append(Utils.m499a(readUByte()));
        }
    }

    /* renamed from: g */
    public final ByteBuffer writeCharBytes(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            writeByte(Utils.m500b(str.charAt(i)));
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
            ByteArrayOutputStream byteArrayOutputStream2 = (ByteArrayOutputStream) IOUtils.m761a((Object) new ByteArrayOutputStream());
            byteArrayOutputStream = byteArrayOutputStream2;
            DataOutputStream dataOutputStream2 = (DataOutputStream) IOUtils.m761a((Object) new DataOutputStream(byteArrayOutputStream2));
            dataOutputStream = dataOutputStream2;
            dataOutputStream2.writeUTF(str);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            IOUtils.m764a((OutputStream) dataOutputStream);
            IOUtils.m764a((OutputStream) byteArrayOutputStream);
            return byteArray;
        } catch (Throwable th) {
            IOUtils.m764a((OutputStream) dataOutputStream);
            IOUtils.m764a((OutputStream) byteArrayOutputStream);
            if (th instanceof RuntimeException) throw (RuntimeException) th;
            if (th instanceof Error) throw (Error) th;
            throw new RuntimeException(th);
        }
    }

    /* renamed from: j */
    public final ByteBuffer writeUTF(String str) {
        byte[] bArrM1375l = encodeUTF(str);
        writeBytes(bArrM1375l);
        NetworkUtils.m1209a(bArrM1375l);
        return this;
    }

    /* renamed from: k */
    public final ByteBuffer writeUTFNoLen(String str) {
        byte[] bArrM1375l = encodeUTF(str);
        writeBytesAt(bArrM1375l, 2, bArrM1375l.length - 2);
        NetworkUtils.m1209a(bArrM1375l);
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
        Utils.m490a((Object) bArr, 0, (Object) bArr, 2, i);
        bArr[0] = (byte) (i >>> 8);
        bArr[1] = (byte) i;
        String strM0a = StringUtils.m0a(bArr, this.offset);
        clear();
        return strM0a;
    }

    /* renamed from: s */
    public final ByteBuffer writeIntAsString(int i) {
        return writeRawString(StringUtils.m17c(Integer.toString(i)));
    }

    /* renamed from: b */
    public final ByteBuffer writeLongAsString(long j) {
        return writeRawString(StringUtils.m17c(Long.toString(j)));
    }

    /* renamed from: t */
    public final ByteBuffer writeIntWithLen(int i) {
        String strM17c = StringUtils.m17c(Integer.toString(i));
        return writeIntLE(strM17c.length()).writeRawString(strM17c);
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
        int i;
        int i2;
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int i3 = this.offset;
        int i4 = this.length;
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = this.data[i3 + i5] & 255;
            int i7 = i6 >> 4;
            StringBuffer stringBufferAppend = stringBufferM1217h.append(i7 < 10 ? (char) (i7 + 48) : (char) (i7 + 87));
            int i8 = i6 & 15;
            if (i8 < 10) {
                i = i8;
                i2 = 48;
            } else {
                i = i8;
                i2 = 87;
            }
            stringBufferAppend.append((char) (i + i2));
        }
        clear();
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: I */
    public final XmlElement parseXml() {
        return new XmlParser(readUTFWithLen()).parse();
    }

    /* renamed from: J */
    public final XmlElement parseXmlStr() {
        return new XmlParser(getStringAndClear()).parse();
    }

    /* renamed from: v */
    public final ByteBuffer writeIntMixed(int i) {
        return writeIntLE(i & 255).writeByte(i >>> 8);
    }

    /* renamed from: b */
    public final ByteBuffer writeStringArr(String[] strArr) {
        ByteBuffer c0043n = new ByteBuffer();
        int length = strArr == null ? 0 : strArr.length;
        int i = length;
        c0043n.writeIntLE(length);
        for (int i2 = 0; i2 < i; i2++) {
            c0043n.writeStringLatin1(strArr[i2]);
        }
        return writeBufferIntLen(c0043n);
    }

    /* renamed from: a */
    public final ByteBuffer writeVector(Vector vector) {
        ByteBuffer c0043n = new ByteBuffer();
        c0043n.writeIntLE(0);
        return writeBufferIntLen(c0043n);
    }
}
