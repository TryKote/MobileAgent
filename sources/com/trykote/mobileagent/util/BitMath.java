package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
/* Extracted from AppController: pure bit/math utility functions */
public final class BitMath {
    public static int[] resizeArray(int[] iArr, int i, int i2) {
        return growArray(growArray(iArr, i), i2);
    }

    private static int[] growArray(int[] iArr, int i) {
        int[] result = iArr;
        int newLength = 1 + iArr[0];
        if (newLength == result.length) {
            int[] expanded = new int[newLength << 1];
            result = expanded;
            Utils.arraycopy(iArr, 0, expanded, 0, newLength);
        }
        result[newLength] = i;
        result[0] = result[0] + 1;
        return result;
    }

    private static int computeTimerValue(int i) {
        if (i == 0) {
            return 32;
        }
        if (i < 0) {
            return 0;
        }
        int count = 0;
        if ((i & (-65536)) == 0) {
            i <<= 16;
            count = 16;
        }
        if ((i & (-16777216)) == 0) {
            i <<= 8;
            count += 8;
        }
        while (i > 0) {
            count++;
            i <<= 1;
        }
        return count;
    }

    public static int countLeadingZeros(long j) {
        int highBits = computeTimerValue((int) (j >> 32));
        return highBits == 32 ? computeTimerValue((int) j) + 32 : highBits;
    }

    public static long shiftRightSticky(long j, int i) {
        return i >= 64 ? j == 0 ? 0L : 1L : (j << (64 - i)) == 0 ? j >>> i : (j >>> i) | 1;
    }

    public static long roundedShiftRight(long j, int i) {
        long shifted;
        long result;
        if (i > 64) {
            return 0L;
        }
        if (i == 64) {
            shifted = j;
            result = 0;
        } else {
            shifted = j << (64 - i);
            result = j >>> i;
        }
        return (shifted >= 0 || (shifted == Long.MIN_VALUE && (result & 1) != 1)) ? result : result + 1;
    }
}
