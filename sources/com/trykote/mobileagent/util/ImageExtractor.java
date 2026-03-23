package com.trykote.mobileagent.util;


import javax.microedition.lcdui.Image;

public final class ImageExtractor {

    public static Image toImage(byte[] data, int offset, int length) {
        return Image.createImage(data, offset, length);
    }

    public static ByteBuffer extractPNG(ByteBuffer buffer) {
        int i = buffer.length;
        if (i >= 4 && buffer.peekIntAt(0) != -559038737) {
            throw new RuntimeException();
        }
        if (i < 44 || i < 44 + buffer.peekIntAt(16)) {
            return null;
        }
        return new ByteBuffer().copyFrom(buffer, 44 + buffer.peekIntAt(16)).compact();
    }

    public static ByteBuffer extractJPEG(ByteBuffer buffer) {
        int i = buffer.length;
        if (i < 6) {
            return null;
        }
        if (buffer.peekByteAt(0) != 42) {
            throw new RuntimeException();
        }
        int typeCode = buffer.peekByteAt(1);
        if (typeCode < 1 || typeCode > 5) {
            throw new RuntimeException();
        }
        int dataLen = buffer.peekShortBE(4);
        if (dataLen + 6 > i) {
            return null;
        }
        return new ByteBuffer().copyFrom(buffer, dataLen + 6).compact();
    }
}
