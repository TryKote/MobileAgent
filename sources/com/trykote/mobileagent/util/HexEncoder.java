package com.trykote.mobileagent.util;


import com.trykote.mobileagent.net.*;

public final class HexEncoder {

    public static String encode(byte[] data, int offset, int length) {
        StringBuffer sb = NetworkUtils.newStringBuffer();
        for (int i = 0; i < length; i++) {
            int b = data[offset + i] & 255;
            int hi = b >> 4;
            sb.append(hi < 10 ? (char) (hi + 48) : (char) (hi + 87));
            int lo = b & 15;
            sb.append(lo < 10 ? (char) (lo + 48) : (char) (lo + 87));
        }
        return NetworkUtils.bufToStringCached(sb);
    }
}
