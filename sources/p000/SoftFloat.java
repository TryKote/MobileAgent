package p000;

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
            int iM360b = AppController.countLeadingZeros(j);
            long j2 = j << iM360b;
            int i2 = i - iM360b;
            int i3 = i2;
            if (i2 < -1085) {
                j = AppController.roundedShiftRight(j2, (-1074) - i3);
            } else {
                long jM362c = AppController.roundedShiftRight(j2, 11);
                long j3 = jM362c;
                if (jM362c == 9007199254740992L) {
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
        boolean zM679o = isNaN(j);
        boolean zM679o2 = isNaN(j2);
        if (!zM679o && !zM679o2) {
            return compareRaw(j, j2);
        }
        if (zM679o && zM679o2) {
            return 0;
        }
        return zM679o ? 1 : -1;
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
        long jM689d = floatToLong(j);
        if (jM689d >= 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (jM689d <= -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int) jM689d;
    }

    /* renamed from: d */
    public static final long floatToLong(long j) {
        long j2;
        if (isNaN(j)) {
            return 0L;
        }
        boolean zM675l = isNegative(j);
        int iM676m = getExponent(j);
        long jM677n = getMantissa(j);
        if (iM676m > 0) {
            if (iM676m >= 63 || (jM677n >> (63 - iM676m)) != 0) {
                return zM675l ? Long.MIN_VALUE : Long.MAX_VALUE;
            }
            j2 = jM677n << iM676m;
        } else {
            if (iM676m <= -53) {
                return 0L;
            }
            j2 = jM677n >>> (-iM676m);
        }
        return zM675l ? -j2 : j2;
    }

    /* renamed from: b */
    public static final long add(long j, long j2) {
        if (isNaN(j) || isNaN(j2)) {
            return 9221120237041090560L;
        }
        boolean zM675l = isNegative(j);
        boolean zM675l2 = isNegative(j2);
        boolean zM680p = isInfinite(j);
        boolean zM680p2 = isInfinite(j2);
        if (zM680p || zM680p2) {
            if (!zM680p || !zM680p2) {
                return zM680p ? j : j2;
            }
            if (zM675l != zM675l2) {
                return 9221120237041090560L;
            }
            return j;
        }
        boolean zM681q = isZero(j);
        boolean zM681q2 = isZero(j2);
        if (zM681q || zM681q2) {
            if (!zM681q || !zM681q2) {
                return zM681q ? j2 : j;
            }
            if (zM675l != zM675l2) {
                return 0L;
            }
            return j;
        }
        long jM677n = getMantissa(j) << 3;
        int iM676m = getExponent(j) - 3;
        long jM677n2 = getMantissa(j2) << 3;
        int iM676m2 = getExponent(j2) - 3;
        int i = iM676m - iM676m2;
        if (i > 0) {
            jM677n2 = AppController.shiftRightSticky(jM677n2, i);
        } else if (i < 0) {
            jM677n = AppController.shiftRightSticky(jM677n, -i);
            iM676m = iM676m2;
        }
        if (zM675l ^ zM675l2) {
            if (jM677n > jM677n2) {
                jM677n2 = -jM677n2;
            } else {
                jM677n = -jM677n;
                zM675l = zM675l2;
            }
        }
        long jM678a = packFloat(zM675l, iM676m, jM677n + jM677n2);
        if (jM678a == Long.MIN_VALUE) {
            return 0L;
        }
        return jM678a;
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
        boolean zM675l = isNegative(j) ^ isNegative(j2);
        if (isInfinite(j) || isInfinite(j2)) {
            if (isZero(j) || isZero(j2)) {
                return 9221120237041090560L;
            }
            return zM675l ? -4503599627370496L : 9218868437227405312L;
        }
        long jM677n = getMantissa(j);
        int iM676m = getExponent(j);
        long jM677n2 = getMantissa(j2);
        int iM676m2 = iM676m + getExponent(j2);
        long j3 = jM677n & 268435455;
        long j4 = jM677n >> 28;
        long j5 = jM677n2 & 268435455;
        long j6 = jM677n2 >> 28;
        long j7 = j3 * j5;
        long j8 = (j3 * j6) + (j4 * j5);
        long j9 = j4 * j6;
        long j10 = j7 + ((j8 & 268435455) << 28);
        long j11 = j9 + (j8 >>> 28) + (j10 >>> 56);
        long j12 = j10 << 8;
        if (j11 == 0) {
            return packFloat(zM675l, iM676m2, j12);
        }
        int iM360b = AppController.countLeadingZeros(j11);
        int i = iM676m2 + (56 - iM360b);
        long j13 = (j11 << iM360b) | (j12 >>> (64 - iM360b));
        if ((j12 << iM360b) != 0) {
            j13 |= 1;
        }
        return packFloat(zM675l, i, j13);
    }

    /* renamed from: e */
    public static final long divide(long j, long j2) {
        if (isNaN(j) || isNaN(j2)) {
            return 9221120237041090560L;
        }
        boolean zM675l = isNegative(j) ^ isNegative(j2);
        boolean zM680p = isInfinite(j);
        boolean zM680p2 = isInfinite(j2);
        if (zM680p || zM680p2) {
            if (zM680p && zM680p2) {
                return 9221120237041090560L;
            }
            return zM680p ? zM675l ? -4503599627370496L : 9218868437227405312L : zM675l ? Long.MIN_VALUE : 0L;
        }
        boolean zM681q = isZero(j);
        boolean zM681q2 = isZero(j2);
        if (zM681q || zM681q2) {
            if (zM681q && zM681q2) {
                return 9221120237041090560L;
            }
            return zM681q ? zM675l ? Long.MIN_VALUE : 0L : zM675l ? -4503599627370496L : 9218868437227405312L;
        }
        long jM677n = getMantissa(j);
        int iM676m = getExponent(j);
        long jM677n2 = getMantissa(j2);
        long j3 = 0;
        int iM676m2 = iM676m - getExponent(j2);
        while (true) {
            int iM503b = Utils.min(AppController.countLeadingZeros(jM677n) - 1, AppController.countLeadingZeros(j3));
            if (iM503b <= 8) {
                break;
            }
            long j4 = jM677n << iM503b;
            iM676m2 -= iM503b;
            j3 = (j3 << iM503b) | (j4 / jM677n2);
            jM677n = j4 % jM677n2;
        }
        if (jM677n != 0) {
            j3 |= 1;
        }
        return packFloat(zM675l, iM676m2, j3);
    }

    /* renamed from: a */
    private static long roundToInt(long j, boolean z, boolean z2) {
        long j2;
        long jM362c;
        if (isNaN(j)) {
            return 9221120237041090560L;
        }
        if (isZero(j) || isInfinite(j)) {
            return j;
        }
        int iM676m = getExponent(j);
        if (iM676m >= 0) {
            return j;
        }
        boolean zM675l = isNegative(j);
        long jM677n = getMantissa(j);
        if (z) {
            jM362c = AppController.roundedShiftRight(jM677n, -iM676m);
        } else {
            if (iM676m <= -64) {
                j2 = jM677n;
                jM362c = 0;
            } else {
                j2 = jM677n << (iM676m + 64);
                jM362c = jM677n >>> (-iM676m);
            }
            if (zM675l && j2 != 0) {
                jM362c++;
            }
        }
        return packFloat(zM675l, 0, jM362c);
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
        short s = ((short[]) AppState.pool[988])[i3];
        int iM360b = AppController.countLeadingZeros(j);
        int i4 = s - iM360b;
        long jM696j = multiplyHigh(j << iM360b, ((long[]) AppState.pool[987])[i3]);
        for (int i5 = i2 % 3; i5 > 0; i5--) {
            if (jM696j < 0) {
                jM696j >>>= 1;
                i4++;
            }
            jM696j += jM696j >>> 2;
            i4 += 3;
        }
        return packFloat(z, i4, jM696j);
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
        char cCharAt;
        String strM17c = StringUtils.intern(str.trim().toUpperCase());
        int length = strM17c.length();
        if (length == 0) {
            throw new NumberFormatException(strM17c);
        }
        if (NetworkUtils.longToHex(5136718).equals(strM17c)) {
            return 9221120237041090560L;
        }
        int i = 0;
        char cCharAt2 = strM17c.charAt(0);
        boolean z = cCharAt2 == '-';
        boolean z2 = z;
        if (z || cCharAt2 == '+') {
            i = 1;
        }
        if (i < length && (((cCharAt = strM17c.charAt(i)) == 'I' || cCharAt == 'i') && StringUtils.equals(AppState.getString(984), StringUtils.intern(StringUtils.suffix(strM17c, i).toUpperCase())))) {
            return z2 ? -4503599627370496L : 9218868437227405312L;
        }
        long j = 0;
        int i2 = 0;
        int i3 = 0;
        boolean z3 = false;
        while (i < length) {
            char cCharAt3 = strM17c.charAt(i);
            if (cCharAt3 == '.') {
                if (z3) {
                    throw new NumberFormatException(strM17c);
                }
                z3 = true;
            } else {
                if (cCharAt3 < '0' || cCharAt3 > '9') {
                    break;
                }
                i3++;
                if (j <= 1844674407370955160L) {
                    j = (j << 3) + (j << 1) + (cCharAt3 - '0');
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
            throw new NumberFormatException(strM17c);
        }
        if (i + 1 < length && (strM17c.charAt(i) == 'E' || strM17c.charAt(i) == 'e')) {
            i2 += Integer.parseInt(StringUtils.suffix(strM17c, i + 1));
        } else if (i != length) {
            throw new NumberFormatException(strM17c);
        }
        return decimalToFloat(z2, i2, j);
    }

    /* renamed from: a */
    public static final String formatFloat(long j, int i) {
        boolean z;
        int i2;
        if (isNaN(j)) {
            return NetworkUtils.longToHex(5136718);
        }
        boolean zM675l = isNegative(j);
        if (isZero(j)) {
            return NetworkUtils.longToHex(zM675l ? 808333357 : 3157552);
        }
        if (isInfinite(j)) {
            return AppState.getString(zM675l ? 985 : 984);
        }
        if (i < 9) {
            i = 9;
        }
        int iM676m = getExponent(j) + 1075;
        long jM677n = getMantissa(j) << (iM676m % 11);
        int i3 = iM676m / 11;
        int i4 = ((short[]) AppState.pool[989])[i3];
        while (jM677n <= 922337203685477580L) {
            jM677n = (jM677n << 3) + (jM677n << 1);
            i4--;
        }
        long jM696j = multiplyHigh(jM677n, ((long[]) AppState.pool[986])[i3]);
        boolean z2 = false;
        while (true) {
            int i5 = (int) (jM696j % 10);
            long j2 = jM696j / 10;
            int i6 = i4 + 1;
            if (i5 != 0) {
                if (i5 > 5 || (i5 == 5 && !z2)) {
                    z = true;
                    j2++;
                } else {
                    z = false;
                }
                if (decimalToFloat(zM675l, i6, j2) != j) {
                    j2 = z ? j2 - 1 : j2 + 1;
                    z = !z;
                    if (decimalToFloat(zM675l, i6, j2) != j) {
                        break;
                    }
                }
                z2 = z;
            }
            jM696j = j2;
            i4 = i6;
        }
        while (true) {
            int i7 = i4;
            long j3 = jM696j;
            StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
            if (zM675l) {
                stringBufferM1217h.append('-');
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
                    stringBufferM1217h.append('0');
                }
            }
            int i9 = 0;
            while (i9 < i2) {
                stringBufferM1217h.append(i9 < string.length() ? string.charAt(i9) : '0');
                i9++;
            }
            stringBufferM1217h.append('.');
            if (i2 >= string.length()) {
                stringBufferM1217h.append('0');
            } else {
                while (i2 < string.length()) {
                    stringBufferM1217h.append(i2 < 0 ? '0' : string.charAt(i2));
                    i2++;
                }
            }
            if (z4) {
                stringBufferM1217h.append('E').append(length);
            }
            String strM1215a = NetworkUtils.bufToStringCached(stringBufferM1217h);
            if (strM1215a.length() <= i) {
                return strM1215a;
            }
            int i10 = (int) (jM696j % 10);
            jM696j /= 10;
            i4++;
            if (i10 > 5 || (i10 == 5 && !z2)) {
                z2 = true;
                jM696j++;
            } else {
                z2 = false;
            }
            while (jM696j % 10 == 0) {
                jM696j /= 10;
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
        int iM688c;
        long jM691c = j;
        if (isNaN(j)) {
            return 9221120237041090560L;
        }
        if (isZero(jM691c)) {
            return 4607182418800017408L;
        }
        if (lessOrEqual(jM691c, -4573606559926636463L)) {
            return 0L;
        }
        if (greaterOrEqual(jM691c, 4649454530587146735L)) {
            return 9218868437227405312L;
        }
        long jM691c2 = 0;
        long jM692d = 0;
        int i = ((int) (jM691c >> 32)) & Integer.MAX_VALUE;
        if (i > 1071001154) {
            if (i >= 1072734898) {
                iM688c = floatToInt(roundToInt(multiply(4609176140021203710L, jM691c), true, false));
                jM691c2 = subtract(jM691c, multiply(0L, 4604418534311723008L));
                jM692d = multiply(0L, 4461442080421002358L);
            } else if (isNegative(jM691c)) {
                jM691c2 = add(jM691c, 4604418534311723008L);
                jM692d = -4761929956433773450L;
                iM688c = -1;
            } else {
                jM691c2 = subtract(jM691c, 4604418534311723008L);
                jM692d = 4461442080421002358L;
                iM688c = 1;
            }
            jM691c = subtract(jM691c2, jM692d);
        } else {
            if (i < 1043333120) {
                return add(jM691c, 4607182418800017408L);
            }
            iM688c = 0;
        }
        long jM692d2 = multiply(jM691c, jM691c);
        long jM691c3 = subtract(jM691c, multiply(jM692d2, add(4595172819793696062L, multiply(jM692d2, add(-4654820494858601069L, multiply(jM692d2, add(4544508515198557740L, multiply(jM692d2, add(-4702957295668925455L, multiply(jM692d2, 4496342204012209360L))))))))));
        return iM688c == 0 ? subtract(4607182418800017408L, subtract(divide(multiply(jM691c, jM691c3), subtract(jM691c3, 4611686018427387904L)), jM691c)) : scalb(subtract(4607182418800017408L, subtract(subtract(jM692d, divide(multiply(jM691c, jM691c3), subtract(4611686018427387904L, jM691c3))), jM691c2)), iM688c);
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
        long jM692d;
        long jM690b;
        long jM691c;
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
                int iM676m = getExponent(j);
                long jM677n = getMantissa(j);
                while (jM677n < 4503599627370496L) {
                    jM677n <<= 1;
                    iM676m--;
                }
                if ((iM676m & 1) != 0) {
                    jM677n <<= 1;
                }
                int i12 = (iM676m >> 1) - 26;
                long j3 = jM677n << 1;
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
        long jM702c = j & Long.MAX_VALUE;
        if (i3 == 0 && (i6 == 2146435072 || i6 == 0 || i6 == 1072693248)) {
            long jM682a = jM702c;
            if (i4 < 0) {
                jM682a = divide(4607182418800017408L, jM682a);
            }
            if (i2 < 0) {
                if (((i6 - 1072693248) | i8) == 0) {
                    jM682a = 9221120237041090560L;
                } else if (i8 == 1) {
                    jM682a = negate(jM682a);
                }
            }
            return jM682a;
        }
        int i13 = (i2 >> 31) + 1;
        if ((i13 | i8) == 0) {
            return 9221120237041090560L;
        }
        boolean z = (i13 | (i8 - 1)) == 0;
        if (i7 <= 1105199104) {
            int i14 = 0;
            if (i6 < 1048576) {
                jM702c = scalb(jM702c, 53);
                i14 = 0 - 53;
                i6 = (int) (jM702c >> 32);
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
            long jM700b = packLowInt(jM702c, i17);
            long jM691c2 = subtract(jM700b, jArr[i]);
            long jM693e = divide(4607182418800017408L, add(jM700b, jArr[i]));
            long jM692d2 = multiply(jM691c2, jM693e);
            long j9 = jM692d2 & (-4294967296L);
            long jM700b2 = packLowInt(0L, ((i17 >> 1) | 536870912) + 524288 + (i << 18));
            long jM692d3 = multiply(jM693e, subtract(subtract(jM691c2, multiply(j9, jM700b2)), multiply(j9, subtract(jM700b, subtract(jM700b2, jArr[i])))));
            jM692d = multiply(jM692d2, jM692d2);
            long jM690b2 = add(multiply(multiply(jM692d, jM692d), add(4603579539098120963L, multiply(jM692d, add(4601392076422097919L, multiply(jM692d, add(4599676419357746765L, multiply(jM692d, add(4598584653024936193L, multiply(jM692d, add(4597478449480325989L, multiply(jM692d, 4596625081194860271L))))))))))), multiply(jM692d3, add(j9, jM692d2)));
            long jM692d4 = multiply(j9, j9);
            long jM690b3 = add(add(4613937818241073152L, jM692d4), jM690b2) & (-4294967296L);
            long jM691c3 = subtract(jM690b2, subtract(subtract(jM690b3, 4613937818241073152L), jM692d4));
            long jM692d5 = multiply(j9, jM690b3);
            long jM690b4 = add(multiply(jM692d3, jM690b3), multiply(jM691c3, jM692d2));
            long jM690b5 = add(jM692d5, jM690b4) & (-4294967296L);
            long jM691c4 = subtract(jM690b4, subtract(jM690b5, jM692d5));
            long jM692d6 = multiply(4606838314073325568L, jM690b5);
            long jM690b6 = add(add(multiply(-4738297118486494731L, jM690b5), multiply(jM691c4, 4606838314010018813L)), jArr3[i]);
            long jM687b = longToFloat(i15);
            jM690b = add(add(add(jM692d6, jM690b6), jArr2[i]), jM687b) & (-4294967296L);
            jM691c = subtract(jM690b6, subtract(subtract(subtract(jM690b, jM687b), jArr2[i]), jM692d6));
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
            jM692d = subtract(jM702c, 4607182418800017408L);
            long jM692d7 = multiply(multiply(jM692d, jM692d), subtract(4602678819172646912L, multiply(jM692d, subtract(4599676419421066581L, multiply(jM692d, 4598175219545276416L)))));
            long jM692d8 = multiply(4609176139934466048L, jM692d);
            long jM691c5 = subtract(multiply(jM692d, 4491406094830001988L), multiply(jM692d7, 4609176140021203710L));
            jM690b = add(jM692d8, jM691c5) & (-4294967296L);
            jM691c = subtract(jM691c5, subtract(jM690b, jM692d8));
        }
        long j10 = j2 & (-4294967296L);
        long jM690b7 = add(multiply(subtract(j2, j10), jM690b), multiply(j2, jM691c));
        long jM692d9 = multiply(j10, jM690b);
        long j11 = jM692d;
        int iM690b = (int) (add(jM690b7, jM692d9) >> 32);
        int i18 = (int) j11;
        if (iM690b >= 1083179008) {
            if (((iM690b - 1083179008) | i18) == 0) {
                long jM690b8 = add(jM690b7, 4365981760143196926L);
                long jM691c6 = subtract(j11, jM692d9);
                if (!isNaN(jM690b8) && !isNaN(jM691c6)) {
                    boolean z2 = compareRaw((jM690b8 > 0L ? 1 : (jM690b8 == 0L ? 0 : -1)) == 0 ? Long.MIN_VALUE : jM690b8, jM691c6) > 0;
                }
            }
            return z ? -4503599627370496L : 9218868437227405312L;
        }
        if ((iM690b & Integer.MAX_VALUE) >= 1083231232 && (((iM690b - (-1064252416)) | i18) != 0 || lessOrEqual(jM690b7, subtract(j11, jM692d9)))) {
            return z ? Long.MIN_VALUE : 0L;
        }
        int i19 = iM690b & Integer.MAX_VALUE;
        int i20 = (i19 >> 20) - 1023;
        int i21 = 0;
        if (i19 > 1071644672) {
            int i22 = iM690b + (1048576 >> (i20 + 1));
            int i23 = ((i22 & Integer.MAX_VALUE) >> 20) - 1023;
            long jM700b3 = packLowInt(0L, i22 & ((1048575 >> i23) ^ (-1)));
            i21 = ((i22 & 1048575) | 1048576) >> (20 - i23);
            if (iM690b < 0) {
                i21 = -i21;
            }
            jM692d9 = subtract(jM692d9, jM700b3);
        }
        long j12 = jM692d;
        long jM692d10 = multiply(add(jM690b7, jM692d9) & (-4294967296L), 4604418534330597376L);
        long jM690b9 = add(multiply(subtract(jM690b7, subtract(j12, jM692d9)), 4604418534313441775L), multiply(j12, -4746692435354555335L));
        long jM690b10 = add(jM692d10, jM690b9);
        long jM691c7 = subtract(jM690b9, subtract(jM690b10, jM692d10));
        long jM692d11 = multiply(jM690b10, jM690b10);
        long jM691c8 = subtract(jM690b10, multiply(jM692d11, add(4595172819793696062L, multiply(jM692d11, add(-4654820494858601069L, multiply(jM692d11, add(4544508515198557740L, multiply(jM692d11, add(-4702957295668925455L, multiply(jM692d11, 4496342204012209360L))))))))));
        long j13 = jM692d;
        long jM702c2 = ((((int) (subtract(4607182418800017408L, subtract(subtract(divide(multiply(jM690b10, jM691c8), subtract(jM691c8, 4611686018427387904L)), add(jM691c7, multiply(jM690b10, jM691c7))), jM690b10)) >> 32)) + (i21 << 20)) >> 20) <= 0 ? scalb(j13, i21) : packLowInt(j13, ((int) (j13 >> 32)) + (i21 << 20));
        if (z) {
            jM702c2 = negate(jM702c2);
        }
        return jM702c2;
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
            long jM702c = scalb(j, 54);
            j = jM702c;
            i = (int) (jM702c >> 32);
        }
        int i3 = i & 1048575;
        int i4 = (i3 + 614244) & 1048576;
        long jM700b = packLowInt(j, i3 | (i4 ^ 1072693248));
        int i5 = i2 + ((i >> 20) - 1023) + (i4 >> 20);
        long jM691c = subtract(jM700b, 4607182418800017408L);
        if ((1048575 & (i3 + 2)) >= 3) {
            long jM687b = longToFloat(i5);
            long jM693e = divide(jM691c, add(4611686018427387904L, jM691c));
            long jM690b = add(multiply(multiply(multiply(jM693e, jM693e), jM693e), add(4600877379321592324L, multiply(jM693e, add(4597174411056806063L, multiply(jM693e, 4594685411790997151L))))), multiply(jM693e, add(4604180019048437139L, multiply(jM693e, add(4598818590951641945L, multiply(jM693e, add(4595719342595441630L, multiply(jM693e, 4594499633228436036L))))))));
            if (((i3 - 398458) | (440401 - i3)) <= 0) {
                return i5 == 0 ? subtract(jM691c, multiply(jM693e, subtract(jM691c, jM690b))) : subtract(multiply(jM687b, 4604418534311723008L), subtract(subtract(multiply(jM693e, subtract(jM691c, jM690b)), multiply(jM687b, 4461442080421002358L)), jM691c));
            }
            long jM692d = multiply(scalb(jM691c, -1), jM691c);
            return i5 == 0 ? subtract(jM691c, subtract(jM692d, multiply(jM693e, add(jM692d, jM690b)))) : subtract(multiply(jM687b, 4604418534311723008L), subtract(subtract(jM692d, add(multiply(jM693e, add(jM692d, jM690b)), multiply(jM687b, 4461442080421002358L))), jM691c));
        }
        if (isZero(jM691c)) {
            if (i5 == 0) {
                return 0L;
            }
            long jM687b2 = longToFloat(i5);
            return add(multiply(jM687b2, 4604418534311723008L), multiply(jM687b2, 4461442080421002358L));
        }
        long jM692d2 = multiply(multiply(jM691c, jM691c), subtract(4602678819172646912L, multiply(4599676419421066581L, jM691c)));
        if (i5 == 0) {
            return subtract(jM691c, jM692d2);
        }
        long jM687b3 = longToFloat(i5);
        return subtract(multiply(jM687b3, 4604418534311723008L), subtract(subtract(jM692d2, multiply(jM687b3, 4461442080421002358L)), jM691c));
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
            long jM690b = add(j, j2);
            long j3 = jM690b & (-4294967296L);
            long jM691c = subtract(j2, subtract(j3, j));
            long jM693e = divide(-4616189618054758400L, jM690b);
            long j4 = jM693e & (-4294967296L);
            return add(j4, multiply(jM693e, add(add(4607182418800017408L, multiply(j4, j3)), multiply(j4, jM691c))));
        }
        if (i3 >= 1072010280) {
            if (i2 < 0) {
                j = negate(j);
                j2 = negate(j2);
            }
            j = add(subtract(4605249457297304856L, j), subtract(4359948597267291143L, j2));
            j2 = 0;
        }
        long jM692d = multiply(j, j);
        long jM692d2 = multiply(jM692d, jM692d);
        long jM690b2 = add(4593971859893059194L, multiply(jM692d2, add(4581960672245896759L, multiply(jM692d2, add(4570429193025094440L, multiply(jM692d2, add(4558562946408670465L, multiply(jM692d2, add(4545397049192321702L, multiply(jM692d2, -4687273268743220365L))))))))));
        long jM692d3 = multiply(jM692d, add(4587938466107703806L, multiply(jM692d2, add(4576262931677611155L, multiply(jM692d2, add(4564358403679355669L, multiply(jM692d2, add(4553182066015801448L, multiply(jM692d2, add(4544897349388904425L, multiply(jM692d2, 4538267711989316308L)))))))))));
        long jM692d4 = multiply(jM692d, j);
        long jM690b3 = add(add(j2, multiply(jM692d, add(multiply(jM692d4, add(jM690b2, jM692d3)), j2))), multiply(4599676419421066595L, jM692d4));
        long jM690b4 = add(j, jM690b3);
        if (i3 >= 1072010280) {
            long jM687b = longToFloat(i);
            return multiply(longToFloat(1 - ((i2 >> 30) & 2)), subtract(jM687b, multiply(4611686018427387904L, subtract(j, subtract(divide(multiply(jM690b4, jM690b4), add(jM690b4, jM687b)), jM690b3)))));
        }
        if (i == 1) {
            return jM690b4;
        }
        long j5 = jM690b4 & (-4294967296L);
        long jM691c2 = subtract(jM690b3, subtract(j5, j));
        long jM693e2 = divide(-4616189618054758400L, jM690b4) & (-4294967296L);
        return add(jM693e2, multiply(jM692d, add(add(4607182418800017408L, multiply(jM693e2, j5)), multiply(jM693e2, jM691c2))));
    }

    /* renamed from: b */
    private static long sinKernel(long j, long j2, int i) {
        if ((((int) (j >> 32)) & Integer.MAX_VALUE) < 1044381696) {
            return j;
        }
        long jM692d = multiply(j, j);
        long jM692d2 = multiply(jM692d, j);
        long jM690b = add(4575957461383575718L, multiply(jM692d, add(-4671919876304969259L, multiply(jM692d, add(4523617212983017085L, multiply(jM692d, add(-4730215680275931925L, multiply(jM692d, 4460209850635244924L))))))));
        return i == 0 ? add(j, multiply(jM692d2, add(-4628199217061079735L, multiply(jM692d, jM690b)))) : subtract(j, subtract(subtract(multiply(jM692d, subtract(multiply(4602678819172646912L, j2), multiply(jM692d2, jM690b))), j2), multiply(jM692d2, -4628199217061079735L)));
    }

    /* renamed from: l */
    private static long cosKernel(long j, long j2) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i < 1044381696) {
            return 4607182418800017408L;
        }
        long jM692d = multiply(j, j);
        long jM692d2 = multiply(jM692d, add(4586165620538955084L, multiply(jM692d, add(-4659324094485802633L, multiply(jM692d, add(4537941361668330896L, multiply(jM692d, add(-4714566979978243411L, multiply(jM692d, add(4477121870137962948L, multiply(jM692d, -4780295122622859052L)))))))))));
        if (i < 1070805811) {
            return subtract(4607182418800017408L, subtract(multiply(4602678819172646912L, jM692d), subtract(multiply(jM692d, jM692d2), multiply(j, j2))));
        }
        long j3 = i > 1072234496 ? 4598738169498697728L : (i - 2097152) << 32;
        return subtract(subtract(4607182418800017408L, j3), subtract(subtract(multiply(4602678819172646912L, jM692d), j3), subtract(multiply(jM692d, jM692d2), multiply(j, j2))));
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00de  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int reduceArg(long j, long[] jArr) {
        boolean z;
        int iM688c;
        long jM691c;
        int i;
        int i2 = (int) (j >> 32);
        int i3 = i2 & Integer.MAX_VALUE;
        if (i3 <= 1072243195) {
            jArr[0] = j;
            jArr[1] = 0;
            return 0;
        }
        if (i3 < 1073928572) {
            long jM682a = 4609753056924401664L;
            long jM682a2 = i3 == 1073291771 ? 4297306550709743731L : 4454258360616903473L;
            if (i2 > 0) {
                jM682a = negate(4609753056924401664L);
                jM682a2 = negate(jM682a2);
            }
            long jM690b = add(j, jM682a);
            jArr[0] = add(jM690b, jM682a2);
            jArr[1] = add(subtract(jM690b, jArr[0]), jM682a2);
            return i2 > 0 ? 1 : -1;
        }
        if (i3 <= 1094263291) {
            long j2 = j & Long.MAX_VALUE;
            int iM688c2 = floatToInt(roundToInt(multiply(j2, 4603909380684499075L), true, false));
            long jM691c2 = subtract(j2, multiply(0L, 4609753056924401664L));
            long jM692d = multiply(0L, 4454258360616903473L);
            if (iM688c2 < 32) {
                if (i3 != ((int[]) AppState.pool[993])[iM688c2 - 1]) {
                    jArr[0] = subtract(jM691c2, jM692d);
                } else {
                    int i4 = i3 >> 20;
                    jArr[0] = subtract(jM691c2, jM692d);
                    if (i4 - ((((int) (jArr[0] >> 32)) >> 20) & 2047) > 16) {
                        long jM692d2 = multiply(0L, 4454258360616747008L);
                        jM691c2 = subtract(jM691c2, jM692d2);
                        jM692d = subtract(multiply(0L, 4297306550709743731L), subtract(subtract(jM691c2, jM691c2), jM692d2));
                        jArr[0] = subtract(jM691c2, jM692d);
                        if (i4 - ((((int) (jArr[0] >> 32)) >> 20) & 2047) > 49) {
                            long jM692d3 = multiply(0L, 4297306550709518336L);
                            jM691c2 = subtract(jM691c2, jM692d3);
                            jM692d = subtract(multiply(0L, 4142048980368378305L), subtract(subtract(jM691c2, jM691c2), jM692d3));
                            jArr[0] = subtract(jM691c2, jM692d);
                        }
                    }
                }
            }
            jArr[1] = subtract(subtract(jM691c2, jArr[0]), jM692d);
            if (i2 >= 0) {
                return iM688c2;
            }
            jArr[0] = negate(jArr[0]);
            jArr[1] = negate(jArr[1]);
            return -iM688c2;
        }
        if (i3 >= 2146435072) {
            jArr[1] = 9221120237041090560L;
            jArr[0] = 9221120237041090560L;
            return 0;
        }
        int i5 = (i3 >> 20) - 1046;
        long jM700b = packLowInt((int) j, i3 - (i5 << 20));
        long[] jArr2 = new long[3];
        for (int i6 = 0; i6 < 2; i6++) {
            jArr2[i6] = longToFloat(floatToInt(jM700b));
            jM700b = scalb(subtract(jM700b, jArr2[i6]), 24);
        }
        jArr2[2] = jM700b;
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
            jArr3[i14] = i12 < 0 ? 0L : longToFloat(ResourceManager.getPiMultiple(i12));
            i14++;
            i12++;
        }
        long[] jArr4 = new long[20];
        for (int i15 = 0; i15 <= 4; i15++) {
            long jM690b2 = 0;
            for (int i16 = 0; i16 <= i8; i16++) {
                jM690b2 = add(jM690b2, multiply(jArr2[i16], jArr3[(i8 + i15) - i16]));
            }
            jArr4[i15] = jM690b2;
        }
        int i17 = 4;
        int[] iArr = new int[20];
        do {
            z = false;
            int i18 = 0;
            long jM690b3 = jArr4[i17];
            for (int i19 = i17; i19 > 0; i19--) {
                long jM687b = longToFloat(floatToInt(scalb(jM690b3, -24)));
                iArr[i18] = floatToInt(subtract(jM690b3, scalb(jM687b, 24)));
                jM690b3 = add(jArr4[i19 - 1], jM687b);
                i18++;
            }
            long jM702c = scalb(jM690b3, i11);
            long jM691c3 = subtract(jM702c, scalb(roundToInt(scalb(jM702c, -3), false, false), 3));
            long jM691c4 = subtract(jM691c3, multiply(4620693217682128896L, roundToInt(multiply(jM691c3, 4593671619917905920L), false, false)));
            iM688c = floatToInt(jM691c4);
            jM691c = subtract(jM691c4, longToFloat(iM688c));
            i = 0;
            if (i11 > 0) {
                int i20 = iArr[i17 - 1] >> (24 - i11);
                iM688c += i20;
                int i21 = i17 - 1;
                iArr[i21] = iArr[i21] - (i20 << (24 - i11));
                i = iArr[i17 - 1] >> (23 - i11);
            } else if (i11 == 0) {
                i = iArr[i17 - 1] >> 23;
            } else if (greaterOrEqual(jM691c, 4602678819172646912L)) {
                i = 2;
            }
            if (i > 0) {
                iM688c++;
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
                    jM691c = subtract(4607182418800017408L, jM691c);
                    if (z2) {
                        jM691c = subtract(jM691c, scalb(4607182418800017408L, i11));
                    }
                }
            }
            if (isZero(jM691c)) {
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
                        jArr3[i8 + i29] = longToFloat(ResourceManager.getPiMultiple(i10 + i29));
                        long jM690b4 = 0;
                        for (int i30 = 0; i30 <= i8; i30++) {
                            jM690b4 = add(jM690b4, multiply(jArr2[i30], jArr3[(i8 + i29) - i30]));
                        }
                        jArr4[i29] = jM690b4;
                    }
                    i17 += i28;
                    z = true;
                }
            }
        } while (z);
        if (isZero(jM691c)) {
            do {
                i17--;
                i11 -= 24;
            } while (iArr[i17] == 0);
        } else {
            long jM702c2 = scalb(jM691c, -i11);
            if (greaterOrEqual(jM702c2, 4715268809856909312L)) {
                long jM687b2 = longToFloat(floatToInt(scalb(jM702c2, -24)));
                iArr[i17] = floatToInt(subtract(jM702c2, scalb(jM687b2, 24)));
                i17++;
                i11 += 24;
                iArr[i17] = floatToInt(jM687b2);
            } else {
                iArr[i17] = floatToInt(jM702c2);
            }
        }
        long jM702c3 = scalb(4607182418800017408L, i11);
        for (int i31 = i17; i31 >= 0; i31--) {
            jArr4[i31] = multiply(jM702c3, longToFloat(iArr[i31]));
            jM702c3 = scalb(jM702c3, -24);
        }
        long[] jArr5 = new long[20];
        for (int i32 = i17; i32 >= 0; i32--) {
            long jM690b5 = 0;
            for (int i33 = 0; i33 <= 4 && i33 <= i17 - i32; i33++) {
                jM690b5 = add(jM690b5, multiply(((long[]) AppState.pool[990])[i33], jArr4[i32 + i33]));
            }
            jArr5[i17 - i32] = jM690b5;
        }
        long jM690b6 = 0;
        for (int i34 = i17; i34 >= 0; i34--) {
            jM690b6 = add(jM690b6, jArr5[i34]);
        }
        jArr[0] = i == 0 ? jM690b6 : negate(jM690b6);
        long jM691c5 = subtract(jArr5[0], jM690b6);
        for (int i35 = 1; i35 <= i17; i35++) {
            jM691c5 = add(jM691c5, jArr5[i35]);
        }
        jArr[1] = i == 0 ? jM691c5 : negate(jM691c5);
        int i36 = iM688c & 7;
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
                return i2 > 0 ? ResourceManager.getTrigConstant(3) : negate(ResourceManager.getTrigConstant(3));
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
        long jM692d = multiply(j, j);
        long jM692d2 = multiply(jM692d, jM692d);
        long jM692d3 = multiply(jM692d, add(4599676419421066509L, multiply(jM692d2, add(4594314991288484863L, multiply(jM692d2, add(4591215095208222830L, multiply(jM692d2, add(4589464229703073105L, multiply(jM692d2, add(4587333258118041067L, multiply(jM692d2, 4580351289466214929L)))))))))));
        long jM692d4 = multiply(jM692d2, add(-4626998257160492092L, multiply(jM692d2, add(-4630701217362536847L, multiply(jM692d2, add(-4633165035261879699L, multiply(jM692d2, add(-4634804155249132134L, multiply(jM692d2, -4637946461342241745L)))))))));
        if (i < 0) {
            return subtract(j, multiply(j, add(jM692d3, jM692d4)));
        }
        long jM691c = subtract(ResourceManager.getTrigConstant(i), subtract(subtract(multiply(j, add(jM692d3, jM692d4)), ResourceManager.getTrigConstant(i + 4)), j));
        return i2 < 0 ? negate(jM691c) : jM691c;
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
