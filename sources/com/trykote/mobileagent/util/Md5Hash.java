package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.net.*;

public final class Md5Hash {

    public static final byte[] hash(byte[] bArr, int i) {
        int[] blockBuf = new int[16];
        int[] state = Utils.bytesToInts(ResourceAccessor.bytes(StringResKeys.RES_EMOTICON_STATE));
        int[] bitCount = new int[2];
        byte[] tempBuf = ObjectPool.newBytes(64);
        md5ProcessBuffer(bArr, i, blockBuf, state, bitCount, tempBuf);
        byte[] lengthBytes = md5Finalize(ObjectPool.newBytes(16), bitCount, 8);
        byte[] padBuf = ObjectPool.newBytes(64);
        padBuf[0] = -128;
        int bufIdx = (bitCount[0] >>> 3) & 63;
        md5ProcessBuffer(padBuf, bufIdx < 56 ? 56 - bufIdx : 120 - bufIdx, blockBuf, state, bitCount, tempBuf);
        md5ProcessBuffer(lengthBytes, 8, blockBuf, state, bitCount, tempBuf);
        ObjectPool.releaseBytes(padBuf);
        ObjectPool.releaseBytes(tempBuf);
        return md5Finalize(lengthBytes, state, 16);
    }

    private static final int md5Round1(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i ^ (-1)) & i3) | (i & i2)) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    private static final int md5Round2(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i3 ^ (-1)) & i2) | (i & i3)) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    private static final int md5Round3(int i, int i2, int i3, int i4, int i5) {
        int i6 = ((i ^ i2) ^ i3) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    private static final int md5Round4(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i3 ^ (-1)) | i) ^ i2) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

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
            byte[] tempBlock = ObjectPool.newBytes(64);
            while (i5 + 63 < i) {
                Utils.arraycopy((Object) bArr, i5, (Object) tempBlock, 0, 64);
                md5ProcessBlock(tempBlock, iArr, iArr2);
                i5 += 64;
            }
            ObjectPool.releaseBytes(tempBlock);
            i2 = 0;
        } else {
            i5 = 0;
        }
        Utils.arraycopy((Object) bArr, i5, (Object) bArr2, i2, i - i5);
    }

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
}
