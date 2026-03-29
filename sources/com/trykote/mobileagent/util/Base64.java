package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.net.*;

public final class Base64 {

    private static char base64Char(int i) {
        return (char) Storage.resources().getBytes(StringResKeys.RES_BASE64_TABLE)[i & 63];
    }

    public static String encode(byte[] data, int offset, int length) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int i = offset;
        int end = offset + length;
        boolean z = true;
        while (z) {
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            if (i < end) {
                int i7 = i;
                i++;
                i3 = data[i7] & 255;
                i6 = 0 + 1;
            }
            if (i < end) {
                int i8 = i;
                i++;
                i4 = data[i8] & 255;
                i6++;
            }
            if (i < end) {
                int i9 = i;
                i++;
                i5 = data[i9] & 255;
                i6++;
            } else {
                z = false;
            }
            if (i6 > 0) {
                int i10 = (i3 << 16) | (i4 << 8) | i5;
                sb.append(base64Char(i10 >> 18)).append(base64Char(i10 >> 12)).append(i6 > 1 ? base64Char(i10 >> 6) : '=').append(i6 > 2 ? base64Char(i10) : '=');
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    private static final int base64CharToInt(int i) {
        if (i >= 65 && i <= 90) {
            return i - 65;
        }
        if (i >= 97 && i <= 122) {
            return i - 71;
        }
        if (i >= 48 && i <= 57) {
            return i + 4;
        }
        if (i == 43) {
            return 62;
        }
        if (i == 47) {
            return 63;
        }
        throw new RuntimeException();
    }

    public static final ByteBuffer decode(String str) {
        int i;
        char ch1;
        char ch2;
        char ch3;
        char ch4;
        ByteBuffer buffer = new ByteBuffer();
        int length = str.length();
        int i2 = 0;
        while (i2 < length) {
            int val1 = 0;
            int val2 = 0;
            int val3 = 0;
            int val4 = 0;
            while (true) {
                try {
                    int i3 = i2;
                    i2++;
                    ch1 = str.charAt(i3);
                    if (ch1 != '\n' && ch1 != '\r') {
                        break;
                    }
                } catch (Throwable unused) {
                    i = 0 - 1;
                    i2 = length;
                }
            }
            val1 = base64CharToInt(ch1);
            int i4 = 0 + 1;
            while (true) {
                int i5 = i2;
                i2++;
                ch2 = str.charAt(i5);
                if (ch2 != '\n' && ch2 != '\r') {
                    break;
                }
            }
            val2 = base64CharToInt(ch2);
            int i6 = i4 + 1;
            while (true) {
                int i7 = i2;
                i2++;
                ch3 = str.charAt(i7);
                if (ch3 != '\n' && ch3 != '\r') {
                    break;
                }
            }
            val3 = base64CharToInt(ch3);
            int i8 = i6 + 1;
            while (true) {
                int i9 = i2;
                i2++;
                ch4 = str.charAt(i9);
                if (ch4 != '\n' && ch4 != '\r') {
                    break;
                }
            }
            val4 = base64CharToInt(ch4);
            i = i8 + 1;
            if (i > 0) {
                buffer.writeByte((val1 << 2) | (val2 >> 4));
            }
            if (i > 1) {
                buffer.writeByte((val2 << 4) | (val3 >> 2));
            }
            if (i > 2) {
                buffer.writeByte((val3 << 6) | val4);
            }
        }
        return buffer;
    }
}
