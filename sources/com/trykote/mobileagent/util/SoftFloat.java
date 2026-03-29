package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
/* renamed from: b */
/* loaded from: MobileAgent_3.9.jar:b.class */
public final class SoftFloat {
    /* renamed from: l */
    private static boolean isNegative(long j) {
        return j < 0;
    }

    /* renamed from: m */
    private static int getExponent(long j) {
        return (((int) (j >> 52)) & 2047) - 1075;
    }

    /* renamed from: n */
    private static long getMantissa(long j) {
        return (j & 9218868437227405312L) == 0 ? (j & 4503599627370495L) << 1 : (j & 4503599627370495L) | 4503599627370496L;
    }

    /* renamed from: a */
    private static long packFloat(boolean z, int i, long j) {
        if (j != 0) {
            int leadingZeros = BitMath.countLeadingZeros(j);
            long j2 = j << leadingZeros;
            int i2 = i - leadingZeros;
            int i3 = i2;
            if (i2 < -1085) {
                j = BitMath.roundedShiftRight(j2, (-1074) - i3);
            } else {
                long rounded = BitMath.roundedShiftRight(j2, 11);
                long j3 = rounded;
                if (rounded == 9007199254740992L) {
                    j3 = 4503599627370496L;
                    i3++;
                }
                j = i3 > 960 ? 9218868437227405312L : (j3 ^ 4503599627370496L) | ((i3 + 1086) << 52);
            }
        }
        if (z) {
            j |= Long.MIN_VALUE;
        }
        return j;
    }

    /* renamed from: o */
    private static boolean isNaN(long j) {
        return (j & Long.MAX_VALUE) > 9218868437227405312L;
    }

    /* renamed from: p */
    private static boolean isInfinite(long j) {
        return (j & Long.MAX_VALUE) == 9218868437227405312L;
    }

    /* renamed from: q */
    private static boolean isZero(long j) {
        return (j & Long.MAX_VALUE) == 0;
    }

    /* renamed from: a */
    public static long negate(long j) {
        if (isNaN(j)) {
            return 9221120237041090560L;
        }
        return j ^ Long.MIN_VALUE;
    }

    /* renamed from: g */
    private static boolean lessOrEqual(long j, long j2) {
        if (isNaN(j) || isNaN(j2)) {
            return false;
        }
        return compareRaw(j, (j2 > Long.MIN_VALUE ? 1 : (j2 == Long.MIN_VALUE ? 0 : -1)) == 0 ? 0L : j2) <= 0;
    }

    /* renamed from: h */
    private static boolean greaterOrEqual(long j, long j2) {
        if (isNaN(j) || isNaN(j2)) {
            return false;
        }
        return compareRaw((j > Long.MIN_VALUE ? 1 : (j == Long.MIN_VALUE ? 0 : -1)) == 0 ? 0L : j, j2) >= 0;
    }

    /* renamed from: a */
    public static final int compare(long j, long j2) {
        boolean aNaN = isNaN(j);
        boolean aNaN2 = isNaN(j2);
        if (!aNaN && !aNaN2) {
            return compareRaw(j, j2);
        }
        if (aNaN && aNaN2) {
            return 0;
        }
        return aNaN ? 1 : -1;
    }

    /* renamed from: i */
    private static final int compareRaw(long j, long j2) {
        if (j == j2) {
            return 0;
        }
        return j < 0 ? (j2 >= 0 || j >= j2) ? -1 : 1 : (j2 >= 0 && j < j2) ? -1 : 1;
    }

    /* renamed from: b */
    public static final long longToFloat(long j) {
        return j < 0 ? packFloat(true, 0, -j) : packFloat(false, 0, j);
    }

    /* renamed from: c */
    public static final int floatToInt(long j) {
        long longValue = floatToLong(j);
        if (longValue >= 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (longValue <= -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int) longValue;
    }

    /* renamed from: d */
    public static final long floatToLong(long j) {
        long j2;
        if (isNaN(j)) {
            return 0L;
        }
        boolean negative = isNegative(j);
        int exponent = getExponent(j);
        long mantissa = getMantissa(j);
        if (exponent > 0) {
            if (exponent >= 63 || (mantissa >> (63 - exponent)) != 0) {
                return negative ? Long.MIN_VALUE : Long.MAX_VALUE;
            }
            j2 = mantissa << exponent;
        } else {
            if (exponent <= -53) {
                return 0L;
            }
            j2 = mantissa >>> (-exponent);
        }
        return negative ? -j2 : j2;
    }

    /* renamed from: b */
    public static final long add(long j, long j2) {
        if (isNaN(j) || isNaN(j2)) {
            return 9221120237041090560L;
        }
        boolean negative = isNegative(j);
        boolean negative2 = isNegative(j2);
        boolean infinite = isInfinite(j);
        boolean infinite2 = isInfinite(j2);
        if (infinite || infinite2) {
            if (!infinite || !infinite2) {
                return infinite ? j : j2;
            }
            if (negative != negative2) {
                return 9221120237041090560L;
            }
            return j;
        }
        boolean zero = isZero(j);
        boolean zero2 = isZero(j2);
        if (zero || zero2) {
            if (!zero || !zero2) {
                return zero ? j2 : j;
            }
            if (negative != negative2) {
                return 0L;
            }
            return j;
        }
        long mantissa = getMantissa(j) << 3;
        int exponent = getExponent(j) - 3;
        long mantissa2 = getMantissa(j2) << 3;
        int exponent2 = getExponent(j2) - 3;
        int i = exponent - exponent2;
        if (i > 0) {
            mantissa2 = BitMath.shiftRightSticky(mantissa2, i);
        } else if (i < 0) {
            mantissa = BitMath.shiftRightSticky(mantissa, -i);
            exponent = exponent2;
        }
        if (negative ^ negative2) {
            if (mantissa > mantissa2) {
                mantissa2 = -mantissa2;
            } else {
                mantissa = -mantissa;
                negative = negative2;
            }
        }
        long result = packFloat(negative, exponent, mantissa + mantissa2);
        if (result == Long.MIN_VALUE) {
            return 0L;
        }
        return result;
    }

    /* renamed from: c */
    public static final long subtract(long j, long j2) {
        return add(j, negate(j2));
    }

    /* renamed from: d */
    public static final long multiply(long j, long j2) {
        if (isNaN(j) || isNaN(j2)) {
            return 9221120237041090560L;
        }
        boolean negative = isNegative(j) ^ isNegative(j2);
        if (isInfinite(j) || isInfinite(j2)) {
            if (isZero(j) || isZero(j2)) {
                return 9221120237041090560L;
            }
            return negative ? -4503599627370496L : 9218868437227405312L;
        }
        long mantissa = getMantissa(j);
        int exponent = getExponent(j);
        long mantissa2 = getMantissa(j2);
        int exponent2 = exponent + getExponent(j2);
        long j3 = mantissa & 268435455;
        long j4 = mantissa >> 28;
        long j5 = mantissa2 & 268435455;
        long j6 = mantissa2 >> 28;
        long j7 = j3 * j5;
        long j8 = (j3 * j6) + (j4 * j5);
        long j9 = j4 * j6;
        long j10 = j7 + ((j8 & 268435455) << 28);
        long j11 = j9 + (j8 >>> 28) + (j10 >>> 56);
        long j12 = j10 << 8;
        if (j11 == 0) {
            return packFloat(negative, exponent2, j12);
        }
        int leadingZeros = BitMath.countLeadingZeros(j11);
        int i = exponent2 + (56 - leadingZeros);
        long j13 = (j11 << leadingZeros) | (j12 >>> (64 - leadingZeros));
        if ((j12 << leadingZeros) != 0) {
            j13 |= 1;
        }
        return packFloat(negative, i, j13);
    }

    /* renamed from: e */
    public static final long divide(long j, long j2) {
        if (isNaN(j) || isNaN(j2)) {
            return 9221120237041090560L;
        }
        boolean negative = isNegative(j) ^ isNegative(j2);
        boolean infinite = isInfinite(j);
        boolean infinite2 = isInfinite(j2);
        if (infinite || infinite2) {
            if (infinite && infinite2) {
                return 9221120237041090560L;
            }
            return infinite ? negative ? -4503599627370496L : 9218868437227405312L : negative ? Long.MIN_VALUE : 0L;
        }
        boolean zero = isZero(j);
        boolean zero2 = isZero(j2);
        if (zero || zero2) {
            if (zero && zero2) {
                return 9221120237041090560L;
            }
            return zero ? negative ? Long.MIN_VALUE : 0L : negative ? -4503599627370496L : 9218868437227405312L;
        }
        long mantissa = getMantissa(j);
        int exponent = getExponent(j);
        long mantissa2 = getMantissa(j2);
        long j3 = 0;
        int exponent2 = exponent - getExponent(j2);
        while (true) {
            int shiftAmount = Utils.min(BitMath.countLeadingZeros(mantissa) - 1, BitMath.countLeadingZeros(j3));
            if (shiftAmount <= 8) {
                break;
            }
            long j4 = mantissa << shiftAmount;
            exponent2 -= shiftAmount;
            j3 = (j3 << shiftAmount) | (j4 / mantissa2);
            mantissa = j4 % mantissa2;
        }
        if (mantissa != 0) {
            j3 |= 1;
        }
        return packFloat(negative, exponent2, j3);
    }

    /* renamed from: a */
    private static long roundToInt(long j, boolean z, boolean z2) {
        long j2;
        long rounded;
        if (isNaN(j)) {
            return 9221120237041090560L;
        }
        if (isZero(j) || isInfinite(j)) {
            return j;
        }
        int exponent = getExponent(j);
        if (exponent >= 0) {
            return j;
        }
        boolean negative = isNegative(j);
        long mantissa = getMantissa(j);
        if (z) {
            rounded = BitMath.roundedShiftRight(mantissa, -exponent);
        } else {
            if (exponent <= -64) {
                j2 = mantissa;
                rounded = 0;
            } else {
                j2 = mantissa << (exponent + 64);
                rounded = mantissa >>> (-exponent);
            }
            if (negative && j2 != 0) {
                rounded++;
            }
        }
        return packFloat(negative, 0, rounded);
    }

    /* renamed from: b */
    private static final long decimalToFloat(boolean z, int i, long j) {
        if (j == 0) {
            return z ? Long.MIN_VALUE : 0L;
        }
        while (j > 0 && j <= 1844674407370955161L) {
            j = (j << 3) + (j << 1);
            i--;
        }
        int i2 = i + 345;
        int i3 = i2 / 3;
        if (i3 < 0) {
            return z ? Long.MIN_VALUE : 0L;
        }
        if (i3 > 218) {
            return z ? -4503599627370496L : 9218868437227405312L;
        }
        short s = ((short[]) Storage.state().getObject(StringResKeys.RES_SHORT_INDEX_TABLE_1))[i3];
        int leadingZeros = BitMath.countLeadingZeros(j);
        int i4 = s - leadingZeros;
        long product = multiplyHigh(j << leadingZeros, ((long[]) Storage.state().getObject(StringResKeys.RES_POW_BASE_TABLE))[i3]);
        for (int i5 = i2 % 3; i5 > 0; i5--) {
            if (product < 0) {
                product >>>= 1;
                i4++;
            }
            product += product >>> 2;
            i4 += 3;
        }
        return packFloat(z, i4, product);
    }

    /* renamed from: j */
    private static final long multiplyHigh(long j, long j2) {
        long j3 = (j & 4294967295L) * (j2 >>> 32);
        long j4 = (j >>> 32) * (j2 & 4294967295L);
        long j5 = ((j >>> 32) * (j2 >>> 32)) + (j3 >>> 32) + (j4 >>> 32);
        return ((j3 + j4) << 32) < 0 ? j5 + 1 : j5;
    }

    /* renamed from: a */
    public static final long parseFloat(String str) {
        char ch;
        String normalized = StringUtils.intern(str.trim().toUpperCase());
        int length = normalized.length();
        if (length == 0) {
            throw new NumberFormatException(normalized);
        }
        if (ObjectPool.unpackChars(5136718).equals(normalized)) {
            return 9221120237041090560L;
        }
        int i = 0;
        char firstChar = normalized.charAt(0);
        boolean z = firstChar == '-';
        boolean z2 = z;
        if (z || firstChar == '+') {
            i = 1;
        }
        if (i < length && (((ch = normalized.charAt(i)) == 'I' || ch == 'i') && StringUtils.equals(Storage.resources().getString(StringResKeys.STR_INFINITY), StringUtils.intern(StringUtils.suffix(normalized, i).toUpperCase())))) {
            return z2 ? -4503599627370496L : 9218868437227405312L;
        }
        long j = 0;
        int i2 = 0;
        int i3 = 0;
        boolean z3 = false;
        while (i < length) {
            char digit = normalized.charAt(i);
            if (digit == '.') {
                if (z3) {
                    throw new NumberFormatException(normalized);
                }
                z3 = true;
            } else {
                if (digit < '0' || digit > '9') {
                    break;
                }
                i3++;
                if (j <= 1844674407370955160L) {
                    j = (j << 3) + (j << 1) + (digit - '0');
                    if (z3) {
                        i2--;
                    }
                } else if (!z3) {
                    i2++;
                }
            }
            i++;
        }
        if (i3 == 0) {
            throw new NumberFormatException(normalized);
        }
        if (i + 1 < length && (normalized.charAt(i) == 'E' || normalized.charAt(i) == 'e')) {
            i2 += Integer.parseInt(StringUtils.suffix(normalized, i + 1));
        } else if (i != length) {
            throw new NumberFormatException(normalized);
        }
        return decimalToFloat(z2, i2, j);
    }

    /* renamed from: a */
    public static final String formatFloat(long j, int i) {
        boolean z;
        int i2;
        if (isNaN(j)) {
            return ObjectPool.unpackChars(5136718);
        }
        boolean negative = isNegative(j);
        if (isZero(j)) {
            return ObjectPool.unpackChars(negative ? 808333357 : 3157552);
        }
        if (isInfinite(j)) {
            return Storage.state().getString(negative ? 985 : 984);
        }
        if (i < 9) {
            i = 9;
        }
        int exponent = getExponent(j) + 1075;
        long mantissa = getMantissa(j) << (exponent % 11);
        int i3 = exponent / 11;
        int i4 = ((short[]) Storage.state().getObject(StringResKeys.RES_SHORT_INDEX_TABLE_2))[i3];
        while (mantissa <= 922337203685477580L) {
            mantissa = (mantissa << 3) + (mantissa << 1);
            i4--;
        }
        long product = multiplyHigh(mantissa, ((long[]) Storage.state().getObject(StringResKeys.RES_LOG_BASE_TABLE))[i3]);
        boolean z2 = false;
        while (true) {
            int i5 = (int) (product % 10);
            long j2 = product / 10;
            int i6 = i4 + 1;
            if (i5 != 0) {
                if (i5 > 5 || (i5 == 5 && !z2)) {
                    z = true;
                    j2++;
                } else {
                    z = false;
                }
                if (decimalToFloat(negative, i6, j2) != j) {
                    j2 = z ? j2 - 1 : j2 + 1;
                    z = !z;
                    if (decimalToFloat(negative, i6, j2) != j) {
                        break;
                    }
                }
                z2 = z;
            }
            product = j2;
            i4 = i6;
        }
        while (true) {
            int i7 = i4;
            long j3 = product;
            StringBuffer buf = ObjectPool.newStringBuffer();
            if (negative) {
                buf.append('-');
            }
            String string = Long.toString(j3);
            int length = i7 + (string.length() - 1);
            boolean z3 = length < -3 || length >= 7;
            boolean z4 = z3;
            if (z3) {
                i2 = 1;
            } else {
                int i8 = length + 1;
                i2 = i8;
                if (i8 < 1) {
                    buf.append('0');
                }
            }
            int i9 = 0;
            while (i9 < i2) {
                buf.append(i9 < string.length() ? string.charAt(i9) : '0');
                i9++;
            }
            buf.append('.');
            if (i2 >= string.length()) {
                buf.append('0');
            } else {
                while (i2 < string.length()) {
                    buf.append(i2 < 0 ? '0' : string.charAt(i2));
                    i2++;
                }
            }
            if (z4) {
                buf.append('E').append(length);
            }
            String formatted = ObjectPool.toStringAndRelease(buf);
            if (formatted.length() <= i) {
                return formatted;
            }
            int i10 = (int) (product % 10);
            product /= 10;
            i4++;
            if (i10 > 5 || (i10 == 5 && !z2)) {
                z2 = true;
                product++;
            } else {
                z2 = false;
            }
            while (product % 10 == 0) {
                product /= 10;
                i4++;
            }
        }
    }

    /* renamed from: e */
    public static final long reciprocal(long j) {
        return multiply(divide(j, 4641240890982006784L), 4614256656552045848L);
    }

    /* renamed from: b */
    private static final long packLowInt(long j, int i) {
        return (j & 4294967295L) | (i << 32);
    }

    /* renamed from: k */
    private static long copySign(long j, long j2) {
        return (j & Long.MAX_VALUE) | (j2 & Long.MIN_VALUE);
    }

    /* renamed from: c */
    private static long scalb(long j, int i) {
        if (isNaN(j)) {
            return 9221120237041090560L;
        }
        if (i == 0 || isInfinite(j) || isZero(j)) {
            return j;
        }
        if (i >= 2098) {
            return copySign(9218868437227405312L, j);
        }
        if (i <= -2099) {
            return copySign(0L, j);
        }
        int i2 = ((int) (j >> 52)) & 2047;
        int i3 = i2 + i;
        return (i2 == 0 || i3 <= 0) ? packFloat(isNegative(j), i3 - 1075, getMantissa(j)) : i3 >= 2047 ? copySign(9218868437227405312L, j) : (j & (-9218868437227405313L)) | (i3 << 52);
    }

    /* renamed from: f */
    public static final long exp(long j) {
        int intPart;
        long lo = j;
        if (isNaN(j)) {
            return 9221120237041090560L;
        }
        if (isZero(lo)) {
            return 4607182418800017408L;
        }
        if (lessOrEqual(lo, -4573606559926636463L)) {
            return 0L;
        }
        if (greaterOrEqual(lo, 4649454530587146735L)) {
            return 9218868437227405312L;
        }
        long lo2 = 0;
        long t = 0;
        int i = ((int) (lo >> 32)) & Integer.MAX_VALUE;
        if (i > 1071001154) {
            if (i >= 1072734898) {
                intPart = floatToInt(roundToInt(multiply(4609176140021203710L, lo), true, false));
                lo2 = subtract(lo, multiply(0L, 4604418534311723008L));
                t = multiply(0L, 4461442080421002358L);
            } else if (isNegative(lo)) {
                lo2 = add(lo, 4604418534311723008L);
                t = -4761929956433773450L;
                intPart = -1;
            } else {
                lo2 = subtract(lo, 4604418534311723008L);
                t = 4461442080421002358L;
                intPart = 1;
            }
            lo = subtract(lo2, t);
        } else {
            if (i < 1043333120) {
                return add(lo, 4607182418800017408L);
            }
            intPart = 0;
        }
        long t2 = multiply(lo, lo);
        long lo3 = subtract(lo, multiply(t2, add(4595172819793696062L, multiply(t2, add(-4654820494858601069L, multiply(t2, add(4544508515198557740L, multiply(t2, add(-4702957295668925455L, multiply(t2, 4496342204012209360L))))))))));
        return intPart == 0 ? subtract(4607182418800017408L, subtract(divide(multiply(lo, lo3), subtract(lo3, 4611686018427387904L)), lo)) : scalb(subtract(4607182418800017408L, subtract(subtract(t, divide(multiply(lo, lo3), subtract(4611686018427387904L, lo3))), lo2)), intPart);
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:93:0x01f1 */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x060b, code lost:
    
        if (r1 == false) goto L226;
     */
    /* renamed from: f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static long pow(long j, long j2) {
        int i;
        long t;
        long s;
        long lo;
        if (isZero(j2)) {
            return 4607182418800017408L;
        }
        if (isNaN(j) || isNaN(j2)) {
            return 9221120237041090560L;
        }
        long[] jArr = {4607182418800017408L, 4609434218613702656L};
        long[] jArr2 = {0, 4603444093224222720L};
        long[] jArr3 = {0, 4489242115478376454L};
        int i2 = (int) (j >> 32);
        int i3 = (int) j;
        int i4 = (int) (j2 >> 32);
        int i5 = (int) j2;
        int i6 = i2 & Integer.MAX_VALUE;
        int i7 = i4 & Integer.MAX_VALUE;
        int i8 = 0;
        if (i2 < 0) {
            if (i7 >= 1128267776) {
                i8 = 2;
            } else if (i7 >= 1072693248) {
                int i9 = (i7 >> 20) - 1023;
                if (i9 > 20) {
                    int i10 = i5 >>> (52 - i9);
                    if ((i10 << (52 - i9)) == i5) {
                        i8 = 2 - (i10 & 1);
                    }
                } else if (i5 == 0) {
                    int i11 = i7 >> (20 - i9);
                    if ((i11 << (20 - i9)) == i7) {
                        i8 = 2 - (i11 & 1);
                    }
                }
            }
        }
        if (i5 == 0) {
            if (i7 == 2146435072) {
                if (((i6 - 1072693248) | i3) == 0) {
                    return 9221120237041090560L;
                }
                return i6 >= 1072693248 ? i4 >= 0 ? 9218868437227405312L : 0L : i4 < 0 ? 9218868437227405312L : 0L;
            }
            if (i7 == 1072693248) {
                return i4 < 0 ? divide(4607182418800017408L, j) : j;
            }
            if (i4 == 1073741824) {
                return multiply(j, j);
            }
            if (i4 == 1071644672 && i2 >= 0) {
                if (isZero(j)) {
                    return j;
                }
                if (isNegative(j) || isNaN(j)) {
                    return 9221120237041090560L;
                }
                if (j == 9218868437227405312L) {
                    return j;
                }
                int exponent = getExponent(j);
                long mantissa = getMantissa(j);
                while (mantissa < 4503599627370496L) {
                    mantissa <<= 1;
                    exponent--;
                }
                if ((exponent & 1) != 0) {
                    mantissa <<= 1;
                }
                int i12 = (exponent >> 1) - 26;
                long j3 = mantissa << 1;
                long j4 = 0;
                long j5 = 0;
                long j6 = 9007199254740992L;
                while (true) {
                    long j7 = j6;
                    if (j7 == 0) {
                        break;
                    }
                    long j8 = j5 + j7;
                    if (j8 < j3) {
                        j5 = j8 + j7;
                        j3 -= j8;
                        j4 |= j7;
                    }
                    j3 <<= 1;
                    j6 = j7 >> 1;
                }
                if (j3 != 0) {
                    j4 += j4 & 1;
                }
                return ((i12 + 1075) << 52) | ((j4 >> 1) & 4503599627370495L);
            }
        }
        long ax = j & Long.MAX_VALUE;
        if (i3 == 0 && (i6 == 2146435072 || i6 == 0 || i6 == 1072693248)) {
            long val = ax;
            if (i4 < 0) {
                val = divide(4607182418800017408L, val);
            }
            if (i2 < 0) {
                if (((i6 - 1072693248) | i8) == 0) {
                    val = 9221120237041090560L;
                } else if (i8 == 1) {
                    val = negate(val);
                }
            }
            return val;
        }
        int i13 = (i2 >> 31) + 1;
        if ((i13 | i8) == 0) {
            return 9221120237041090560L;
        }
        boolean z = (i13 | (i8 - 1)) == 0;
        if (i7 <= 1105199104) {
            int i14 = 0;
            if (i6 < 1048576) {
                ax = scalb(ax, 53);
                i14 = 0 - 53;
                i6 = (int) (ax >> 32);
            }
            int i15 = i14 + ((i6 >> 20) - 1023);
            int i16 = i6 & 1048575;
            int i17 = i16 | 1072693248;
            if (i16 <= 235662) {
                i = 0;
            } else if (i16 < 767610) {
                i = 1;
            } else {
                i = 0;
                i15++;
                i17 -= 1048576;
            }
            long packed = packLowInt(ax, i17);
            long lo2 = subtract(packed, jArr[i]);
            long inv = divide(4607182418800017408L, add(packed, jArr[i]));
            long t2 = multiply(lo2, inv);
            long j9 = t2 & (-4294967296L);
            long packed2 = packLowInt(0L, ((i17 >> 1) | 536870912) + 524288 + (i << 18));
            long t3 = multiply(inv, subtract(subtract(lo2, multiply(j9, packed2)), multiply(j9, subtract(packed, subtract(packed2, jArr[i])))));
            t = multiply(t2, t2);
            long s2 = add(multiply(multiply(t, t), add(4603579539098120963L, multiply(t, add(4601392076422097919L, multiply(t, add(4599676419357746765L, multiply(t, add(4598584653024936193L, multiply(t, add(4597478449480325989L, multiply(t, 4596625081194860271L))))))))))), multiply(t3, add(j9, t2)));
            long t4 = multiply(j9, j9);
            long s3 = add(add(4613937818241073152L, t4), s2) & (-4294967296L);
            long lo3 = subtract(s2, subtract(subtract(s3, 4613937818241073152L), t4));
            long t5 = multiply(j9, s3);
            long s4 = add(multiply(t3, s3), multiply(lo3, t2));
            long s5 = add(t5, s4) & (-4294967296L);
            long lo4 = subtract(s4, subtract(s5, t5));
            long t6 = multiply(4606838314073325568L, s5);
            long s6 = add(add(multiply(-4738297118486494731L, s5), multiply(lo4, 4606838314010018813L)), jArr3[i]);
            long floatK = longToFloat(i15);
            s = add(add(add(t6, s6), jArr2[i]), floatK) & (-4294967296L);
            lo = subtract(s6, subtract(subtract(subtract(s, floatK), jArr2[i]), t6));
        } else {
            if (i7 > 1139802112) {
                return i6 <= 1072693247 ? i4 < 0 ? 9218868437227405312L : 0L : i4 > 0 ? 9218868437227405312L : 0L;
            }
            if (i6 < 1072693247) {
                return z ? i4 < 0 ? -4503599627370496L : Long.MIN_VALUE : i4 < 0 ? 9218868437227405312L : 0L;
            }
            if (i6 > 1072693248) {
                return z ? i4 > 0 ? -4503599627370496L : Long.MIN_VALUE : i4 > 0 ? 9218868437227405312L : 0L;
            }
            t = subtract(ax, 4607182418800017408L);
            long t7 = multiply(multiply(t, t), subtract(4602678819172646912L, multiply(t, subtract(4599676419421066581L, multiply(t, 4598175219545276416L)))));
            long t8 = multiply(4609176139934466048L, t);
            long lo5 = subtract(multiply(t, 4491406094830001988L), multiply(t7, 4609176140021203710L));
            s = add(t8, lo5) & (-4294967296L);
            lo = subtract(lo5, subtract(s, t8));
        }
        long j10 = j2 & (-4294967296L);
        long s7 = add(multiply(subtract(j2, j10), s), multiply(j2, lo));
        long t9 = multiply(j10, s);
        long j11 = t;
        int highWord = (int) (add(s7, t9) >> 32);
        int i18 = (int) j11;
        if (highWord >= 1083179008) {
            if (((highWord - 1083179008) | i18) == 0) {
                long s8 = add(s7, 4365981760143196926L);
                long lo6 = subtract(j11, t9);
                if (!isNaN(s8) && !isNaN(lo6)) {
                    boolean z2 = compareRaw((s8 > 0L ? 1 : (s8 == 0L ? 0 : -1)) == 0 ? Long.MIN_VALUE : s8, lo6) > 0;
                }
            }
            return z ? -4503599627370496L : 9218868437227405312L;
        }
        if ((highWord & Integer.MAX_VALUE) >= 1083231232 && (((highWord - (-1064252416)) | i18) != 0 || lessOrEqual(s7, subtract(j11, t9)))) {
            return z ? Long.MIN_VALUE : 0L;
        }
        int i19 = highWord & Integer.MAX_VALUE;
        int i20 = (i19 >> 20) - 1023;
        int i21 = 0;
        if (i19 > 1071644672) {
            int i22 = highWord + (1048576 >> (i20 + 1));
            int i23 = ((i22 & Integer.MAX_VALUE) >> 20) - 1023;
            long packed3 = packLowInt(0L, i22 & ((1048575 >> i23) ^ (-1)));
            i21 = ((i22 & 1048575) | 1048576) >> (20 - i23);
            if (highWord < 0) {
                i21 = -i21;
            }
            t9 = subtract(t9, packed3);
        }
        long j12 = t;
        long t10 = multiply(add(s7, t9) & (-4294967296L), 4604418534330597376L);
        long s9 = add(multiply(subtract(s7, subtract(j12, t9)), 4604418534313441775L), multiply(j12, -4746692435354555335L));
        long s10 = add(t10, s9);
        long lo7 = subtract(s9, subtract(s10, t10));
        long t11 = multiply(s10, s10);
        long lo8 = subtract(s10, multiply(t11, add(4595172819793696062L, multiply(t11, add(-4654820494858601069L, multiply(t11, add(4544508515198557740L, multiply(t11, add(-4702957295668925455L, multiply(t11, 4496342204012209360L))))))))));
        long j13 = t;
        long ax2 = ((((int) (subtract(4607182418800017408L, subtract(subtract(divide(multiply(s10, lo8), subtract(lo8, 4611686018427387904L)), add(lo7, multiply(s10, lo7))), s10)) >> 32)) + (i21 << 20)) >> 20) <= 0 ? scalb(j13, i21) : packLowInt(j13, ((int) (j13 >> 32)) + (i21 << 20));
        if (z) {
            ax2 = negate(ax2);
        }
        return ax2;
    }

    /* renamed from: g */
    public static long log(long j) {
        if (isZero(j)) {
            return -4503599627370496L;
        }
        if (isNaN(j) || isNegative(j)) {
            return 9221120237041090560L;
        }
        if (j == 9218868437227405312L) {
            return j;
        }
        int i = (int) (j >> 32);
        int i2 = 0;
        if (i < 1048576) {
            i2 = 0 - 54;
            long ax = scalb(j, 54);
            j = ax;
            i = (int) (ax >> 32);
        }
        int i3 = i & 1048575;
        int i4 = (i3 + 614244) & 1048576;
        long packed = packLowInt(j, i3 | (i4 ^ 1072693248));
        int i5 = i2 + ((i >> 20) - 1023) + (i4 >> 20);
        long lo = subtract(packed, 4607182418800017408L);
        if ((1048575 & (i3 + 2)) >= 3) {
            long floatK = longToFloat(i5);
            long inv = divide(lo, add(4611686018427387904L, lo));
            long s = add(multiply(multiply(multiply(inv, inv), inv), add(4600877379321592324L, multiply(inv, add(4597174411056806063L, multiply(inv, 4594685411790997151L))))), multiply(inv, add(4604180019048437139L, multiply(inv, add(4598818590951641945L, multiply(inv, add(4595719342595441630L, multiply(inv, 4594499633228436036L))))))));
            if (((i3 - 398458) | (440401 - i3)) <= 0) {
                return i5 == 0 ? subtract(lo, multiply(inv, subtract(lo, s))) : subtract(multiply(floatK, 4604418534311723008L), subtract(subtract(multiply(inv, subtract(lo, s)), multiply(floatK, 4461442080421002358L)), lo));
            }
            long t = multiply(scalb(lo, -1), lo);
            return i5 == 0 ? subtract(lo, subtract(t, multiply(inv, add(t, s)))) : subtract(multiply(floatK, 4604418534311723008L), subtract(subtract(t, add(multiply(inv, add(t, s)), multiply(floatK, 4461442080421002358L))), lo));
        }
        if (isZero(lo)) {
            if (i5 == 0) {
                return 0L;
            }
            long floatK2 = longToFloat(i5);
            return add(multiply(floatK2, 4604418534311723008L), multiply(floatK2, 4461442080421002358L));
        }
        long t2 = multiply(multiply(lo, lo), subtract(4602678819172646912L, multiply(4599676419421066581L, lo)));
        if (i5 == 0) {
            return subtract(lo, t2);
        }
        long floatK3 = longToFloat(i5);
        return subtract(multiply(floatK3, 4604418534311723008L), subtract(subtract(t2, multiply(floatK3, 4461442080421002358L)), lo));
    }

    /* renamed from: h */
    public static long sin(long j) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i <= 1072243195) {
            return sinKernel(j, 0L, 0);
        }
        if (i >= 2146435072) {
            return 9221120237041090560L;
        }
        long[] jArr = new long[2];
        switch (reduceArg(j, jArr) & 3) {
            case 0:
                return sinKernel(jArr[0], jArr[1], 1);
            case 1:
                return cosKernel(jArr[0], jArr[1]);
            case 2:
                return negate(sinKernel(jArr[0], jArr[1], 1));
            default:
                return negate(cosKernel(jArr[0], jArr[1]));
        }
    }

    /* renamed from: i */
    public static long cos(long j) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i <= 1072243195) {
            return tanKernel(j, 0L, 1);
        }
        if (i >= 2146435072) {
            return 9221120237041090560L;
        }
        long[] jArr = new long[2];
        return tanKernel(jArr[0], jArr[1], 1 - ((reduceArg(j, jArr) & 1) << 1));
    }

    /* renamed from: a */
    private static long tanKernel(long j, long j2, int i) {
        int i2 = (int) (j >> 32);
        int i3 = i2 & Integer.MAX_VALUE;
        if (i3 < 1043333120 && floatToInt(j) == 0) {
            if ((i3 | ((int) j) | (i + 1)) == 0) {
                return 9218868437227405312L;
            }
            if (i == 1) {
                return j;
            }
            long s = add(j, j2);
            long j3 = s & (-4294967296L);
            long lo = subtract(j2, subtract(j3, j));
            long inv = divide(-4616189618054758400L, s);
            long j4 = inv & (-4294967296L);
            return add(j4, multiply(inv, add(add(4607182418800017408L, multiply(j4, j3)), multiply(j4, lo))));
        }
        if (i3 >= 1072010280) {
            if (i2 < 0) {
                j = negate(j);
                j2 = negate(j2);
            }
            j = add(subtract(4605249457297304856L, j), subtract(4359948597267291143L, j2));
            j2 = 0;
        }
        long t = multiply(j, j);
        long t2 = multiply(t, t);
        long s2 = add(4593971859893059194L, multiply(t2, add(4581960672245896759L, multiply(t2, add(4570429193025094440L, multiply(t2, add(4558562946408670465L, multiply(t2, add(4545397049192321702L, multiply(t2, -4687273268743220365L))))))))));
        long t3 = multiply(t, add(4587938466107703806L, multiply(t2, add(4576262931677611155L, multiply(t2, add(4564358403679355669L, multiply(t2, add(4553182066015801448L, multiply(t2, add(4544897349388904425L, multiply(t2, 4538267711989316308L)))))))))));
        long t4 = multiply(t, j);
        long s3 = add(add(j2, multiply(t, add(multiply(t4, add(s2, t3)), j2))), multiply(4599676419421066595L, t4));
        long s4 = add(j, s3);
        if (i3 >= 1072010280) {
            long floatK = longToFloat(i);
            return multiply(longToFloat(1 - ((i2 >> 30) & 2)), subtract(floatK, multiply(4611686018427387904L, subtract(j, subtract(divide(multiply(s4, s4), add(s4, floatK)), s3)))));
        }
        if (i == 1) {
            return s4;
        }
        long j5 = s4 & (-4294967296L);
        long lo2 = subtract(s3, subtract(j5, j));
        long inv2 = divide(-4616189618054758400L, s4) & (-4294967296L);
        return add(inv2, multiply(t, add(add(4607182418800017408L, multiply(inv2, j5)), multiply(inv2, lo2))));
    }

    /* renamed from: b */
    private static long sinKernel(long j, long j2, int i) {
        if ((((int) (j >> 32)) & Integer.MAX_VALUE) < 1044381696) {
            return j;
        }
        long t = multiply(j, j);
        long t2 = multiply(t, j);
        long s = add(4575957461383575718L, multiply(t, add(-4671919876304969259L, multiply(t, add(4523617212983017085L, multiply(t, add(-4730215680275931925L, multiply(t, 4460209850635244924L))))))));
        return i == 0 ? add(j, multiply(t2, add(-4628199217061079735L, multiply(t, s)))) : subtract(j, subtract(subtract(multiply(t, subtract(multiply(4602678819172646912L, j2), multiply(t2, s))), j2), multiply(t2, -4628199217061079735L)));
    }

    /* renamed from: l */
    private static long cosKernel(long j, long j2) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i < 1044381696) {
            return 4607182418800017408L;
        }
        long t = multiply(j, j);
        long t2 = multiply(t, add(4586165620538955084L, multiply(t, add(-4659324094485802633L, multiply(t, add(4537941361668330896L, multiply(t, add(-4714566979978243411L, multiply(t, add(4477121870137962948L, multiply(t, -4780295122622859052L)))))))))));
        if (i < 1070805811) {
            return subtract(4607182418800017408L, subtract(multiply(4602678819172646912L, t), subtract(multiply(t, t2), multiply(j, j2))));
        }
        long j3 = i > 1072234496 ? 4598738169498697728L : (i - 2097152) << 32;
        return subtract(subtract(4607182418800017408L, j3), subtract(subtract(multiply(4602678819172646912L, t), j3), subtract(multiply(t, t2), multiply(j, j2))));
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00de  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int reduceArg(long j, long[] jArr) {
        boolean z;
        int intPart;
        long lo;
        int i;
        int i2 = (int) (j >> 32);
        int i3 = i2 & Integer.MAX_VALUE;
        if (i3 <= 1072243195) {
            jArr[0] = j;
            jArr[1] = 0;
            return 0;
        }
        if (i3 < 1073928572) {
            long val = 4609753056924401664L;
            long val2 = i3 == 1073291771 ? 4297306550709743731L : 4454258360616903473L;
            if (i2 > 0) {
                val = negate(4609753056924401664L);
                val2 = negate(val2);
            }
            long s = add(j, val);
            jArr[0] = add(s, val2);
            jArr[1] = add(subtract(s, jArr[0]), val2);
            return i2 > 0 ? 1 : -1;
        }
        if (i3 <= 1094263291) {
            long j2 = j & Long.MAX_VALUE;
            int intPart2 = floatToInt(roundToInt(multiply(j2, 4603909380684499075L), true, false));
            long lo2 = subtract(j2, multiply(0L, 4609753056924401664L));
            long t = multiply(0L, 4454258360616903473L);
            if (intPart2 < 32) {
                if (i3 != ((int[]) Storage.state().getObject(StringResKeys.RES_PALETTE_MAP_2))[intPart2 - 1]) {
                    jArr[0] = subtract(lo2, t);
                } else {
                    int i4 = i3 >> 20;
                    jArr[0] = subtract(lo2, t);
                    if (i4 - ((((int) (jArr[0] >> 32)) >> 20) & 2047) > 16) {
                        long t2 = multiply(0L, 4454258360616747008L);
                        lo2 = subtract(lo2, t2);
                        t = subtract(multiply(0L, 4297306550709743731L), subtract(subtract(lo2, lo2), t2));
                        jArr[0] = subtract(lo2, t);
                        if (i4 - ((((int) (jArr[0] >> 32)) >> 20) & 2047) > 49) {
                            long t3 = multiply(0L, 4297306550709518336L);
                            lo2 = subtract(lo2, t3);
                            t = subtract(multiply(0L, 4142048980368378305L), subtract(subtract(lo2, lo2), t3));
                            jArr[0] = subtract(lo2, t);
                        }
                    }
                }
            }
            jArr[1] = subtract(subtract(lo2, jArr[0]), t);
            if (i2 >= 0) {
                return intPart2;
            }
            jArr[0] = negate(jArr[0]);
            jArr[1] = negate(jArr[1]);
            return -intPart2;
        }
        if (i3 >= 2146435072) {
            jArr[1] = 9221120237041090560L;
            jArr[0] = 9221120237041090560L;
            return 0;
        }
        int i5 = (i3 >> 20) - 1046;
        long packed = packLowInt((int) j, i3 - (i5 << 20));
        long[] jArr2 = new long[3];
        for (int i6 = 0; i6 < 2; i6++) {
            jArr2[i6] = longToFloat(floatToInt(packed));
            packed = scalb(subtract(packed, jArr2[i6]), 24);
        }
        jArr2[2] = packed;
        int i7 = 3;
        while (isZero(jArr2[i7 - 1])) {
            i7--;
        }
        int i8 = i7 - 1;
        int i9 = (i5 - 3) / 24;
        int i10 = i9;
        if (i9 < 0) {
            i10 = 0;
        }
        int i11 = i5 - (24 * (i10 + 1));
        int i12 = i10 - i8;
        int i13 = i8 + 4;
        long[] jArr3 = new long[20];
        int i14 = 0;
        while (i14 <= i13) {
            jArr3[i14] = i12 < 0 ? 0L : longToFloat(getPiMultiple(i12));
            i14++;
            i12++;
        }
        long[] jArr4 = new long[20];
        for (int i15 = 0; i15 <= 4; i15++) {
            long s2 = 0;
            for (int i16 = 0; i16 <= i8; i16++) {
                s2 = add(s2, multiply(jArr2[i16], jArr3[(i8 + i15) - i16]));
            }
            jArr4[i15] = s2;
        }
        int i17 = 4;
        int[] iArr = new int[20];
        do {
            z = false;
            int i18 = 0;
            long s3 = jArr4[i17];
            for (int i19 = i17; i19 > 0; i19--) {
                long floatK = longToFloat(floatToInt(scalb(s3, -24)));
                iArr[i18] = floatToInt(subtract(s3, scalb(floatK, 24)));
                s3 = add(jArr4[i19 - 1], floatK);
                i18++;
            }
            long ax = scalb(s3, i11);
            long lo3 = subtract(ax, scalb(roundToInt(scalb(ax, -3), false, false), 3));
            long lo4 = subtract(lo3, multiply(4620693217682128896L, roundToInt(multiply(lo3, 4593671619917905920L), false, false)));
            intPart = floatToInt(lo4);
            lo = subtract(lo4, longToFloat(intPart));
            i = 0;
            if (i11 > 0) {
                int i20 = iArr[i17 - 1] >> (24 - i11);
                intPart += i20;
                int i21 = i17 - 1;
                iArr[i21] = iArr[i21] - (i20 << (24 - i11));
                i = iArr[i17 - 1] >> (23 - i11);
            } else if (i11 == 0) {
                i = iArr[i17 - 1] >> 23;
            } else if (greaterOrEqual(lo, 4602678819172646912L)) {
                i = 2;
            }
            if (i > 0) {
                intPart++;
                boolean z2 = false;
                for (int i22 = 0; i22 < i17; i22++) {
                    int i23 = iArr[i22];
                    if (z2) {
                        iArr[i22] = 16777215 - i23;
                    } else if (i23 != 0) {
                        z2 = true;
                        iArr[i22] = 16777216 - i23;
                    }
                }
                if (i11 > 0) {
                    switch (i11) {
                        case 1:
                            int i24 = i17 - 1;
                            iArr[i24] = iArr[i24] & 8388607;
                            break;
                        case 2:
                            int i25 = i17 - 1;
                            iArr[i25] = iArr[i25] & 4194303;
                            break;
                    }
                }
                if (i == 2) {
                    lo = subtract(4607182418800017408L, lo);
                    if (z2) {
                        lo = subtract(lo, scalb(4607182418800017408L, i11));
                    }
                }
            }
            if (isZero(lo)) {
                int i26 = 0;
                for (int i27 = i17 - 1; i27 >= 4; i27--) {
                    i26 |= iArr[i27];
                }
                if (i26 == 0) {
                    int i28 = 1;
                    while (iArr[4 - i28] == 0) {
                        i28++;
                    }
                    for (int i29 = i17 + 1; i29 <= i17 + i28; i29++) {
                        jArr3[i8 + i29] = longToFloat(getPiMultiple(i10 + i29));
                        long s4 = 0;
                        for (int i30 = 0; i30 <= i8; i30++) {
                            s4 = add(s4, multiply(jArr2[i30], jArr3[(i8 + i29) - i30]));
                        }
                        jArr4[i29] = s4;
                    }
                    i17 += i28;
                    z = true;
                }
            }
        } while (z);
        if (isZero(lo)) {
            do {
                i17--;
                i11 -= 24;
            } while (iArr[i17] == 0);
        } else {
            long ax2 = scalb(lo, -i11);
            if (greaterOrEqual(ax2, 4715268809856909312L)) {
                long floatK2 = longToFloat(floatToInt(scalb(ax2, -24)));
                iArr[i17] = floatToInt(subtract(ax2, scalb(floatK2, 24)));
                i17++;
                i11 += 24;
                iArr[i17] = floatToInt(floatK2);
            } else {
                iArr[i17] = floatToInt(ax2);
            }
        }
        long ax3 = scalb(4607182418800017408L, i11);
        for (int i31 = i17; i31 >= 0; i31--) {
            jArr4[i31] = multiply(ax3, longToFloat(iArr[i31]));
            ax3 = scalb(ax3, -24);
        }
        long[] jArr5 = new long[20];
        for (int i32 = i17; i32 >= 0; i32--) {
            long s5 = 0;
            for (int i33 = 0; i33 <= 4 && i33 <= i17 - i32; i33++) {
                s5 = add(s5, multiply(((long[]) Storage.state().getObject(StringResKeys.RES_MULTIPLY_COEFFICIENTS))[i33], jArr4[i32 + i33]));
            }
            jArr5[i17 - i32] = s5;
        }
        long s6 = 0;
        for (int i34 = i17; i34 >= 0; i34--) {
            s6 = add(s6, jArr5[i34]);
        }
        jArr[0] = i == 0 ? s6 : negate(s6);
        long lo5 = subtract(jArr5[0], s6);
        for (int i35 = 1; i35 <= i17; i35++) {
            lo5 = add(lo5, jArr5[i35]);
        }
        jArr[1] = i == 0 ? lo5 : negate(lo5);
        int i36 = intPart & 7;
        if (i2 >= 0) {
            return i36;
        }
        jArr[0] = negate(jArr[0]);
        jArr[1] = negate(jArr[1]);
        return -i36;
    }

    /* renamed from: j */
    public static long atan(long j) {
        int i;
        int i2 = (int) (j >> 32);
        int i3 = i2 & Integer.MAX_VALUE;
        if (i3 >= 1141899264) {
            if (i3 > 2146435072) {
                return 9221120237041090560L;
            }
            if (i3 != 2146435072 || ((int) j) == 0) {
                return i2 > 0 ? getTrigConstant(3) : negate(getTrigConstant(3));
            }
            return 9221120237041090560L;
        }
        if (i3 >= 1071382528) {
            long j2 = j & Long.MAX_VALUE;
            if (i3 < 1072889856) {
                if (i3 < 1072037888) {
                    i = 0;
                    j = divide(subtract(scalb(j2, 1), 4607182418800017408L), add(4611686018427387904L, j2));
                } else {
                    i = 1;
                    j = divide(subtract(j2, 4607182418800017408L), add(j2, 4607182418800017408L));
                }
            } else if (i3 < 1073971200) {
                i = 2;
                j = divide(subtract(j2, 4609434218613702656L), add(4607182418800017408L, multiply(4609434218613702656L, j2)));
            } else {
                i = 3;
                j = divide(-4616189618054758400L, j2);
            }
        } else {
            if (i3 < 1042284544) {
                return j;
            }
            i = -1;
        }
        long t = multiply(j, j);
        long t2 = multiply(t, t);
        long t3 = multiply(t, add(4599676419421066509L, multiply(t2, add(4594314991288484863L, multiply(t2, add(4591215095208222830L, multiply(t2, add(4589464229703073105L, multiply(t2, add(4587333258118041067L, multiply(t2, 4580351289466214929L)))))))))));
        long t4 = multiply(t2, add(-4626998257160492092L, multiply(t2, add(-4630701217362536847L, multiply(t2, add(-4633165035261879699L, multiply(t2, add(-4634804155249132134L, multiply(t2, -4637946461342241745L)))))))));
        if (i < 0) {
            return subtract(j, multiply(j, add(t3, t4)));
        }
        long lo = subtract(getTrigConstant(i), subtract(subtract(multiply(j, add(t3, t4)), getTrigConstant(i + 4)), j));
        return i2 < 0 ? negate(lo) : lo;
    }

    public final String toString() {
        return formatFloat(0L, 100);
    }

    public final int hashCode() {
        return 0;
    }

    public final boolean equals(Object obj) {
        return obj instanceof SoftFloat;
    }

    public static void initMathTables() {
        Storage.state().setObject(StringResKeys.RES_LOG_BASE_TABLE, readLongArray(986));
        Storage.state().setObject(StringResKeys.RES_POW_BASE_TABLE, readLongArray(987));
        Storage.state().setObject(StringResKeys.RES_MULTIPLY_COEFFICIENTS, readLongArray(990));
        Storage.state().setObject(StringResKeys.RES_LOOKUP_TABLE, readLongArray(991));
        Storage.state().setObject(StringResKeys.RES_SHORT_INDEX_TABLE_2, Utils.readShortArray(989));
        Storage.state().setObject(StringResKeys.RES_SHORT_INDEX_TABLE_1, Utils.readShortArray(988));
        Storage.state().setObject(StringResKeys.RES_PALETTE_MAP_1, Utils.bytesToInts(Storage.resources().getBytes(StringResKeys.RES_PALETTE_MAP_1)));
        Storage.state().setObject(StringResKeys.RES_PALETTE_MAP_2, Utils.bytesToInts(Storage.resources().getBytes(StringResKeys.RES_PALETTE_MAP_2)));
        Storage.state().setObject(StringResKeys.RES_ICON_MAP, Utils.bytesToInts(Storage.resources().getBytes(StringResKeys.RES_ICON_MAP)));
    }

    public static void clearMathTables() {
        Storage.state().clearRange(StringResKeys.STR_INFINITY, StringResKeys.RES_PALETTE_MAP_2);
    }

    private static long[] readLongArray(int resourceKey) {
        byte[] bytes = Storage.state().getBytes(resourceKey);
        int length = bytes.length >> 3;
        long[] result = new long[length];
        int byteIndex = 0;
        int arrayIndex = 0;
        while (arrayIndex < length) {
            long value = 0;
            do {
                value = (value << 8) | (bytes[byteIndex] & 255);
                byteIndex++;
            } while ((byteIndex & 7) != 0);
            result[arrayIndex] = value;
            arrayIndex++;
        }
        ObjectPool.releaseBytes(bytes);
        return result;
    }

    static long getTrigConstant(int index) {
        return ((long[]) Storage.state().getObject(StringResKeys.RES_LOOKUP_TABLE))[index];
    }

    static int getPiMultiple(int index) {
        return ((int[]) Storage.state().getObject(StringResKeys.RES_PALETTE_MAP_1))[index];
    }

    /* renamed from: k */
    public static final long cosFull(long j) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i <= 1072243195) {
            return cosKernel(j, 0L);
        }
        if (i >= 2146435072) {
            return 9221120237041090560L;
        }
        long[] jArr = new long[2];
        switch (reduceArg(j, jArr) & 3) {
            case 0:
                return cosKernel(jArr[0], jArr[1]);
            case 1:
                return negate(sinKernel(jArr[0], jArr[1], 1));
            case 2:
                return negate(cosKernel(jArr[0], jArr[1]));
            default:
                return sinKernel(jArr[0], jArr[1], 1);
        }
    }
}
