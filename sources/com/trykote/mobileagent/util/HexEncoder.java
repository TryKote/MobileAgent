package com.trykote.mobileagent.util;


import com.trykote.mobileagent.net.*;

public final class HexEncoder {

    private static final int ASCII_ZERO = '0';            // 48
    private static final int ASCII_LOWERCASE_A_MINUS_10 = 'a' - 10; // 87

    public static String encode(byte[] data, int offset, int length) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int i = 0; i < length; i++) {
            int b = data[offset + i] & 255;
            int hi = b >> 4;
            sb.append(hi < 10 ? (char) (hi + ASCII_ZERO) : (char) (hi + ASCII_LOWERCASE_A_MINUS_10));
            int lo = b & 15;
            sb.append(lo < 10 ? (char) (lo + ASCII_ZERO) : (char) (lo + ASCII_LOWERCASE_A_MINUS_10));
        }
        return ObjectPool.toStringAndRelease(sb);
    }
}
