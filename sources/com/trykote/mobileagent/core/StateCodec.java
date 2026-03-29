package com.trykote.mobileagent.core;


import com.trykote.mobileagent.util.*;

/**
 * Binary encoding/decoding for the AppState object pool and delta persistence.
 * Handles variable-length integer encoding and Win-1251 string encoding used
 * in the cfg resource binary and the delta record store.
 */
abstract class StateCodec {

    // Variable-length int encoding flags
    static final int FLAG_BYTE_ARRAY = 0x80;
    static final int FLAG_SMALL_INT = 0x40;
    static final int FLAG_MEDIUM_INT = 0x20;
    static final int MASK_SMALL_VALUE = 0x3F;
    static final int MASK_MEDIUM_HIGH = 0x1F;
    static final int MASK_BYTE_COUNT = 0x07;
    static final int FLAG_SHORT_BYTES = 0xC0;
    static final int FLAG_LONG_BYTES = 0x8000;
    static final int ENCODE_FLAG_LENGTH = 0x08;

    // String encoding threshold
    static final int SHORT_STRING_MAX_LENGTH = 63;

    static Object decodeObject(ByteBuffer buffer, int poolIndex,
                               int deltaSize, int rawBytesEnd, String separator) {
        byte flag = buffer.readByte();
        if ((flag & FLAG_BYTE_ARRAY) != 0) {
            byte[] bytes = new byte[(flag & FLAG_SMALL_INT) != 0
                    ? flag & MASK_SMALL_VALUE
                    : ((flag & MASK_MEDIUM_HIGH) << 8) + buffer.readUByte()];
            buffer.readIntoBytes(bytes);
            if (poolIndex >= deltaSize && poolIndex < rawBytesEnd) {
                return bytes;
            }
            StringBuffer sb = ObjectPool.newStringBuffer();
            for (byte b : bytes) {
                sb.append(Utils.win1251ToChar((int) b));
            }
            ObjectPool.releaseBytes(bytes);
            String decoded = ObjectPool.toStringAndRelease(sb);
            if (separator.equals(decoded)) {
                return null;
            }
            return decoded;
        }
        if ((flag & FLAG_SMALL_INT) != 0) {
            return ObjectPool.integerOf(flag & MASK_SMALL_VALUE);
        }
        if ((flag & FLAG_MEDIUM_INT) != 0) {
            return ObjectPool.integerOf(((flag & MASK_MEDIUM_HIGH) << 8) + buffer.readUByte());
        }
        int value = 0;
        for (int bytesRemaining = flag & MASK_BYTE_COUNT; bytesRemaining > 0; bytesRemaining--) {
            value = (value << 8) + buffer.readUByte();
        }
        return ObjectPool.integerOf(value);
    }

    static int decodeInt(ByteBuffer buffer) {
        byte flag = buffer.readByte();
        if ((flag & FLAG_SMALL_INT) != 0) {
            return flag & MASK_SMALL_VALUE;
        }
        if ((flag & FLAG_MEDIUM_INT) != 0) {
            return ((flag & MASK_MEDIUM_HIGH) << 8) + buffer.readUByte();
        }
        int accum = 0;
        for (int bytesRemaining = flag & MASK_BYTE_COUNT; bytesRemaining > 0; bytesRemaining--) {
            accum = (accum << 8) + buffer.readUByte();
        }
        return accum;
    }

    static void encodeIndex(ByteBuffer buffer, int value) {
        if (value >= 0 && value <= MASK_SMALL_VALUE) {
            buffer.writeByte(FLAG_SMALL_INT | value);
            return;
        }
        ByteBuffer tempBuf = new ByteBuffer();
        int[] byteValues = new int[8];
        int shift = 24;
        int firstNonZero = -1;
        for (int byteIdx = 0; byteIdx < 4; byteIdx++) {
            byteValues[byteIdx] = (value >> shift) & 255;
            shift -= 8;
            if (firstNonZero == -1 && byteValues[byteIdx] != 0) {
                firstNonZero = byteIdx;
            }
        }
        if (firstNonZero < 0) {
            firstNonZero = 3;
        }
        for (int writeIdx = firstNonZero; writeIdx < 4; writeIdx++) {
            tempBuf.writeByte(byteValues[writeIdx]);
        }
        byte[] bytes = tempBuf.toByteArray();
        buffer.writeByte(ENCODE_FLAG_LENGTH | bytes.length);
        buffer.writeBytes(bytes);
    }

    static void encodeString(ByteBuffer buffer, String text) {
        int textLength = text.length();
        byte[] encoded = new byte[textLength];
        for (int charIndex = 0; charIndex < textLength; charIndex++) {
            encoded[charIndex] = Utils.charToWin1251(text.charAt(charIndex));
        }
        if (textLength <= 0 || textLength > SHORT_STRING_MAX_LENGTH) {
            buffer.writeShortBE(textLength | FLAG_LONG_BYTES);
        } else {
            buffer.writeByte(FLAG_SHORT_BYTES | textLength);
        }
        buffer.writeBytes(encoded);
    }
}
