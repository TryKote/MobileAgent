package com.trykote.mobileagent.util;


import javax.microedition.lcdui.Image;

public final class ImageExtractor {

    private static final int PNG_MAGIC = 0xDEADBEEF;  // -559038737
    private static final int PNG_MIN_HEADER_SIZE = 4;
    private static final int PNG_DATA_OFFSET = 44;
    private static final int PNG_DATA_LENGTH_OFFSET = 16;

    private static final int JPEG_MIN_HEADER_SIZE = 6;
    private static final int JPEG_MARKER = 42;
    private static final int JPEG_MIN_TYPE = 1;
    private static final int JPEG_MAX_TYPE = 5;
    private static final int JPEG_DATA_LENGTH_OFFSET = 4;

    public static Image toImage(byte[] data, int offset, int length) {
        return Image.createImage(data, offset, length);
    }

    public static ByteBuffer extractPNG(ByteBuffer buffer) {
        int i = buffer.length;
        if (i >= PNG_MIN_HEADER_SIZE && buffer.peekIntAt(0) != PNG_MAGIC) {
            throw new RuntimeException();
        }
        if (i < PNG_DATA_OFFSET || i < PNG_DATA_OFFSET + buffer.peekIntAt(PNG_DATA_LENGTH_OFFSET)) {
            return null;
        }
        return new ByteBuffer().copyFrom(buffer, PNG_DATA_OFFSET + buffer.peekIntAt(PNG_DATA_LENGTH_OFFSET)).compact();
    }

    public static ByteBuffer extractJPEG(ByteBuffer buffer) {
        int i = buffer.length;
        if (i < JPEG_MIN_HEADER_SIZE) {
            return null;
        }
        if (buffer.peekByteAt(0) != JPEG_MARKER) {
            throw new RuntimeException();
        }
        int typeCode = buffer.peekByteAt(1);
        if (typeCode < JPEG_MIN_TYPE || typeCode > JPEG_MAX_TYPE) {
            throw new RuntimeException();
        }
        int dataLen = buffer.peekShortBE(JPEG_DATA_LENGTH_OFFSET);
        if (dataLen + JPEG_MIN_HEADER_SIZE > i) {
            return null;
        }
        return new ByteBuffer().copyFrom(buffer, dataLen + JPEG_MIN_HEADER_SIZE).compact();
    }
}
