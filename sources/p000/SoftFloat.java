package p000;

/* renamed from: b */
/* loaded from: MobileAgent_3.9.jar:b.class */
public final class SoftFloat {
    /* renamed from: l */
    private static boolean m675l(long j) {
        return j < 0;
    }

    /* renamed from: m */
    private static int m676m(long j) {
        return (((int) (j >> 52)) & 2047) - 1075;
    }

    /* renamed from: n */
    private static long m677n(long j) {
        return (j & 9218868437227405312L) == 0 ? (j & 4503599627370495L) << 1 : (j & 4503599627370495L) | 4503599627370496L;
    }

    /* renamed from: a */
    private static long m678a(boolean z, int i, long j) {
        if (j != 0) {
            int iM360b = C0015ao.m360b(j);
            long j2 = j << iM360b;
            int i2 = i - iM360b;
            int i3 = i2;
            if (i2 < -1085) {
                j = C0015ao.m362c(j2, (-1074) - i3);
            } else {
                long jM362c = C0015ao.m362c(j2, 11);
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
    private static boolean m679o(long j) {
        return (j & Long.MAX_VALUE) > 9218868437227405312L;
    }

    /* renamed from: p */
    private static boolean m680p(long j) {
        return (j & Long.MAX_VALUE) == 9218868437227405312L;
    }

    /* renamed from: q */
    private static boolean m681q(long j) {
        return (j & Long.MAX_VALUE) == 0;
    }

    /* renamed from: a */
    public static long m682a(long j) {
        if (m679o(j)) {
            return 9221120237041090560L;
        }
        return j ^ Long.MIN_VALUE;
    }

    /* renamed from: g */
    private static boolean m683g(long j, long j2) {
        if (m679o(j) || m679o(j2)) {
            return false;
        }
        return m686i(j, (j2 > Long.MIN_VALUE ? 1 : (j2 == Long.MIN_VALUE ? 0 : -1)) == 0 ? 0L : j2) <= 0;
    }

    /* renamed from: h */
    private static boolean m684h(long j, long j2) {
        if (m679o(j) || m679o(j2)) {
            return false;
        }
        return m686i((j > Long.MIN_VALUE ? 1 : (j == Long.MIN_VALUE ? 0 : -1)) == 0 ? 0L : j, j2) >= 0;
    }

    /* renamed from: a */
    public static final int m685a(long j, long j2) {
        boolean zM679o = m679o(j);
        boolean zM679o2 = m679o(j2);
        if (!zM679o && !zM679o2) {
            return m686i(j, j2);
        }
        if (zM679o && zM679o2) {
            return 0;
        }
        return zM679o ? 1 : -1;
    }

    /* renamed from: i */
    private static final int m686i(long j, long j2) {
        if (j == j2) {
            return 0;
        }
        return j < 0 ? (j2 >= 0 || j >= j2) ? -1 : 1 : (j2 >= 0 && j < j2) ? -1 : 1;
    }

    /* renamed from: b */
    public static final long m687b(long j) {
        return j < 0 ? m678a(true, 0, -j) : m678a(false, 0, j);
    }

    /* renamed from: c */
    public static final int m688c(long j) {
        long jM689d = m689d(j);
        if (jM689d >= 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (jM689d <= -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int) jM689d;
    }

    /* renamed from: d */
    public static final long m689d(long j) {
        long j2;
        if (m679o(j)) {
            return 0L;
        }
        boolean zM675l = m675l(j);
        int iM676m = m676m(j);
        long jM677n = m677n(j);
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
    public static final long m690b(long j, long j2) {
        if (m679o(j) || m679o(j2)) {
            return 9221120237041090560L;
        }
        boolean zM675l = m675l(j);
        boolean zM675l2 = m675l(j2);
        boolean zM680p = m680p(j);
        boolean zM680p2 = m680p(j2);
        if (zM680p || zM680p2) {
            if (!zM680p || !zM680p2) {
                return zM680p ? j : j2;
            }
            if (zM675l != zM675l2) {
                return 9221120237041090560L;
            }
            return j;
        }
        boolean zM681q = m681q(j);
        boolean zM681q2 = m681q(j2);
        if (zM681q || zM681q2) {
            if (!zM681q || !zM681q2) {
                return zM681q ? j2 : j;
            }
            if (zM675l != zM675l2) {
                return 0L;
            }
            return j;
        }
        long jM677n = m677n(j) << 3;
        int iM676m = m676m(j) - 3;
        long jM677n2 = m677n(j2) << 3;
        int iM676m2 = m676m(j2) - 3;
        int i = iM676m - iM676m2;
        if (i > 0) {
            jM677n2 = C0015ao.m361b(jM677n2, i);
        } else if (i < 0) {
            jM677n = C0015ao.m361b(jM677n, -i);
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
        long jM678a = m678a(zM675l, iM676m, jM677n + jM677n2);
        if (jM678a == Long.MIN_VALUE) {
            return 0L;
        }
        return jM678a;
    }

    /* renamed from: c */
    public static final long m691c(long j, long j2) {
        return m690b(j, m682a(j2));
    }

    /* renamed from: d */
    public static final long m692d(long j, long j2) {
        if (m679o(j) || m679o(j2)) {
            return 9221120237041090560L;
        }
        boolean zM675l = m675l(j) ^ m675l(j2);
        if (m680p(j) || m680p(j2)) {
            if (m681q(j) || m681q(j2)) {
                return 9221120237041090560L;
            }
            return zM675l ? -4503599627370496L : 9218868437227405312L;
        }
        long jM677n = m677n(j);
        int iM676m = m676m(j);
        long jM677n2 = m677n(j2);
        int iM676m2 = iM676m + m676m(j2);
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
            return m678a(zM675l, iM676m2, j12);
        }
        int iM360b = C0015ao.m360b(j11);
        int i = iM676m2 + (56 - iM360b);
        long j13 = (j11 << iM360b) | (j12 >>> (64 - iM360b));
        if ((j12 << iM360b) != 0) {
            j13 |= 1;
        }
        return m678a(zM675l, i, j13);
    }

    /* renamed from: e */
    public static final long m693e(long j, long j2) {
        if (m679o(j) || m679o(j2)) {
            return 9221120237041090560L;
        }
        boolean zM675l = m675l(j) ^ m675l(j2);
        boolean zM680p = m680p(j);
        boolean zM680p2 = m680p(j2);
        if (zM680p || zM680p2) {
            if (zM680p && zM680p2) {
                return 9221120237041090560L;
            }
            return zM680p ? zM675l ? -4503599627370496L : 9218868437227405312L : zM675l ? Long.MIN_VALUE : 0L;
        }
        boolean zM681q = m681q(j);
        boolean zM681q2 = m681q(j2);
        if (zM681q || zM681q2) {
            if (zM681q && zM681q2) {
                return 9221120237041090560L;
            }
            return zM681q ? zM675l ? Long.MIN_VALUE : 0L : zM675l ? -4503599627370496L : 9218868437227405312L;
        }
        long jM677n = m677n(j);
        int iM676m = m676m(j);
        long jM677n2 = m677n(j2);
        long j3 = 0;
        int iM676m2 = iM676m - m676m(j2);
        while (true) {
            int iM503b = Utils.m503b(C0015ao.m360b(jM677n) - 1, C0015ao.m360b(j3));
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
        return m678a(zM675l, iM676m2, j3);
    }

    /* renamed from: a */
    private static long m694a(long j, boolean z, boolean z2) {
        long j2;
        long jM362c;
        if (m679o(j)) {
            return 9221120237041090560L;
        }
        if (m681q(j) || m680p(j)) {
            return j;
        }
        int iM676m = m676m(j);
        if (iM676m >= 0) {
            return j;
        }
        boolean zM675l = m675l(j);
        long jM677n = m677n(j);
        if (z) {
            jM362c = C0015ao.m362c(jM677n, -iM676m);
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
        return m678a(zM675l, 0, jM362c);
    }

    /* renamed from: b */
    private static final long m695b(boolean z, int i, long j) {
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
        short s = ((short[]) AppState.f177b[988])[i3];
        int iM360b = C0015ao.m360b(j);
        int i4 = s - iM360b;
        long jM696j = m696j(j << iM360b, ((long[]) AppState.f177b[987])[i3]);
        for (int i5 = i2 % 3; i5 > 0; i5--) {
            if (jM696j < 0) {
                jM696j >>>= 1;
                i4++;
            }
            jM696j += jM696j >>> 2;
            i4 += 3;
        }
        return m678a(z, i4, jM696j);
    }

    /* renamed from: j */
    private static final long m696j(long j, long j2) {
        long j3 = (j & 4294967295L) * (j2 >>> 32);
        long j4 = (j >>> 32) * (j2 & 4294967295L);
        long j5 = ((j >>> 32) * (j2 >>> 32)) + (j3 >>> 32) + (j4 >>> 32);
        return ((j3 + j4) << 32) < 0 ? j5 + 1 : j5;
    }

    /* renamed from: a */
    public static final long m697a(String str) {
        char cCharAt;
        String strM17c = StringUtils.m17c(str.trim().toUpperCase());
        int length = strM17c.length();
        if (length == 0) {
            throw new NumberFormatException(strM17c);
        }
        if (C0040k.m1221a(5136718).equals(strM17c)) {
            return 9221120237041090560L;
        }
        int i = 0;
        char cCharAt2 = strM17c.charAt(0);
        boolean z = cCharAt2 == '-';
        boolean z2 = z;
        if (z || cCharAt2 == '+') {
            i = 1;
        }
        if (i < length && (((cCharAt = strM17c.charAt(i)) == 'I' || cCharAt == 'i') && StringUtils.m6a(AppState.m584b(984), StringUtils.m17c(StringUtils.m15c(strM17c, i).toUpperCase())))) {
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
            i2 += Integer.parseInt(StringUtils.m15c(strM17c, i + 1));
        } else if (i != length) {
            throw new NumberFormatException(strM17c);
        }
        return m695b(z2, i2, j);
    }

    /* renamed from: a */
    public static final String m698a(long j, int i) {
        boolean z;
        int i2;
        if (m679o(j)) {
            return C0040k.m1221a(5136718);
        }
        boolean zM675l = m675l(j);
        if (m681q(j)) {
            return C0040k.m1221a(zM675l ? 808333357 : 3157552);
        }
        if (m680p(j)) {
            return AppState.m584b(zM675l ? 985 : 984);
        }
        if (i < 9) {
            i = 9;
        }
        int iM676m = m676m(j) + 1075;
        long jM677n = m677n(j) << (iM676m % 11);
        int i3 = iM676m / 11;
        int i4 = ((short[]) AppState.f177b[989])[i3];
        while (jM677n <= 922337203685477580L) {
            jM677n = (jM677n << 3) + (jM677n << 1);
            i4--;
        }
        long jM696j = m696j(jM677n, ((long[]) AppState.f177b[986])[i3]);
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
                if (m695b(zM675l, i6, j2) != j) {
                    j2 = z ? j2 - 1 : j2 + 1;
                    z = !z;
                    if (m695b(zM675l, i6, j2) != j) {
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
            StringBuffer stringBufferM1217h = C0040k.m1217h();
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
            String strM1215a = C0040k.m1215a(stringBufferM1217h);
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
    public static final long m699e(long j) {
        return m692d(m693e(j, 4641240890982006784L), 4614256656552045848L);
    }

    /* renamed from: b */
    private static final long m700b(long j, int i) {
        return (j & 4294967295L) | (i << 32);
    }

    /* renamed from: k */
    private static long m701k(long j, long j2) {
        return (j & Long.MAX_VALUE) | (j2 & Long.MIN_VALUE);
    }

    /* renamed from: c */
    private static long m702c(long j, int i) {
        if (m679o(j)) {
            return 9221120237041090560L;
        }
        if (i == 0 || m680p(j) || m681q(j)) {
            return j;
        }
        if (i >= 2098) {
            return m701k(9218868437227405312L, j);
        }
        if (i <= -2099) {
            return m701k(0L, j);
        }
        int i2 = ((int) (j >> 52)) & 2047;
        int i3 = i2 + i;
        return (i2 == 0 || i3 <= 0) ? m678a(m675l(j), i3 - 1075, m677n(j)) : i3 >= 2047 ? m701k(9218868437227405312L, j) : (j & (-9218868437227405313L)) | (i3 << 52);
    }

    /* renamed from: f */
    public static final long m703f(long j) {
        int iM688c;
        long jM691c = j;
        if (m679o(j)) {
            return 9221120237041090560L;
        }
        if (m681q(jM691c)) {
            return 4607182418800017408L;
        }
        if (m683g(jM691c, -4573606559926636463L)) {
            return 0L;
        }
        if (m684h(jM691c, 4649454530587146735L)) {
            return 9218868437227405312L;
        }
        long jM691c2 = 0;
        long jM692d = 0;
        int i = ((int) (jM691c >> 32)) & Integer.MAX_VALUE;
        if (i > 1071001154) {
            if (i >= 1072734898) {
                iM688c = m688c(m694a(m692d(4609176140021203710L, jM691c), true, false));
                jM691c2 = m691c(jM691c, m692d(0L, 4604418534311723008L));
                jM692d = m692d(0L, 4461442080421002358L);
            } else if (m675l(jM691c)) {
                jM691c2 = m690b(jM691c, 4604418534311723008L);
                jM692d = -4761929956433773450L;
                iM688c = -1;
            } else {
                jM691c2 = m691c(jM691c, 4604418534311723008L);
                jM692d = 4461442080421002358L;
                iM688c = 1;
            }
            jM691c = m691c(jM691c2, jM692d);
        } else {
            if (i < 1043333120) {
                return m690b(jM691c, 4607182418800017408L);
            }
            iM688c = 0;
        }
        long jM692d2 = m692d(jM691c, jM691c);
        long jM691c3 = m691c(jM691c, m692d(jM692d2, m690b(4595172819793696062L, m692d(jM692d2, m690b(-4654820494858601069L, m692d(jM692d2, m690b(4544508515198557740L, m692d(jM692d2, m690b(-4702957295668925455L, m692d(jM692d2, 4496342204012209360L))))))))));
        return iM688c == 0 ? m691c(4607182418800017408L, m691c(m693e(m692d(jM691c, jM691c3), m691c(jM691c3, 4611686018427387904L)), jM691c)) : m702c(m691c(4607182418800017408L, m691c(m691c(jM692d, m693e(m692d(jM691c, jM691c3), m691c(4611686018427387904L, jM691c3))), jM691c2)), iM688c);
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:93:0x01f1 */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x060b, code lost:
    
        if (r1 == false) goto L226;
     */
    /* renamed from: f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static long m704f(long j, long j2) {
        int i;
        long jM692d;
        long jM690b;
        long jM691c;
        if (m681q(j2)) {
            return 4607182418800017408L;
        }
        if (m679o(j) || m679o(j2)) {
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
                return i4 < 0 ? m693e(4607182418800017408L, j) : j;
            }
            if (i4 == 1073741824) {
                return m692d(j, j);
            }
            if (i4 == 1071644672 && i2 >= 0) {
                if (m681q(j)) {
                    return j;
                }
                if (m675l(j) || m679o(j)) {
                    return 9221120237041090560L;
                }
                if (j == 9218868437227405312L) {
                    return j;
                }
                int iM676m = m676m(j);
                long jM677n = m677n(j);
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
                jM682a = m693e(4607182418800017408L, jM682a);
            }
            if (i2 < 0) {
                if (((i6 - 1072693248) | i8) == 0) {
                    jM682a = 9221120237041090560L;
                } else if (i8 == 1) {
                    jM682a = m682a(jM682a);
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
                jM702c = m702c(jM702c, 53);
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
            long jM700b = m700b(jM702c, i17);
            long jM691c2 = m691c(jM700b, jArr[i]);
            long jM693e = m693e(4607182418800017408L, m690b(jM700b, jArr[i]));
            long jM692d2 = m692d(jM691c2, jM693e);
            long j9 = jM692d2 & (-4294967296L);
            long jM700b2 = m700b(0L, ((i17 >> 1) | 536870912) + 524288 + (i << 18));
            long jM692d3 = m692d(jM693e, m691c(m691c(jM691c2, m692d(j9, jM700b2)), m692d(j9, m691c(jM700b, m691c(jM700b2, jArr[i])))));
            jM692d = m692d(jM692d2, jM692d2);
            long jM690b2 = m690b(m692d(m692d(jM692d, jM692d), m690b(4603579539098120963L, m692d(jM692d, m690b(4601392076422097919L, m692d(jM692d, m690b(4599676419357746765L, m692d(jM692d, m690b(4598584653024936193L, m692d(jM692d, m690b(4597478449480325989L, m692d(jM692d, 4596625081194860271L))))))))))), m692d(jM692d3, m690b(j9, jM692d2)));
            long jM692d4 = m692d(j9, j9);
            long jM690b3 = m690b(m690b(4613937818241073152L, jM692d4), jM690b2) & (-4294967296L);
            long jM691c3 = m691c(jM690b2, m691c(m691c(jM690b3, 4613937818241073152L), jM692d4));
            long jM692d5 = m692d(j9, jM690b3);
            long jM690b4 = m690b(m692d(jM692d3, jM690b3), m692d(jM691c3, jM692d2));
            long jM690b5 = m690b(jM692d5, jM690b4) & (-4294967296L);
            long jM691c4 = m691c(jM690b4, m691c(jM690b5, jM692d5));
            long jM692d6 = m692d(4606838314073325568L, jM690b5);
            long jM690b6 = m690b(m690b(m692d(-4738297118486494731L, jM690b5), m692d(jM691c4, 4606838314010018813L)), jArr3[i]);
            long jM687b = m687b(i15);
            jM690b = m690b(m690b(m690b(jM692d6, jM690b6), jArr2[i]), jM687b) & (-4294967296L);
            jM691c = m691c(jM690b6, m691c(m691c(m691c(jM690b, jM687b), jArr2[i]), jM692d6));
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
            jM692d = m691c(jM702c, 4607182418800017408L);
            long jM692d7 = m692d(m692d(jM692d, jM692d), m691c(4602678819172646912L, m692d(jM692d, m691c(4599676419421066581L, m692d(jM692d, 4598175219545276416L)))));
            long jM692d8 = m692d(4609176139934466048L, jM692d);
            long jM691c5 = m691c(m692d(jM692d, 4491406094830001988L), m692d(jM692d7, 4609176140021203710L));
            jM690b = m690b(jM692d8, jM691c5) & (-4294967296L);
            jM691c = m691c(jM691c5, m691c(jM690b, jM692d8));
        }
        long j10 = j2 & (-4294967296L);
        long jM690b7 = m690b(m692d(m691c(j2, j10), jM690b), m692d(j2, jM691c));
        long jM692d9 = m692d(j10, jM690b);
        long j11 = jM692d;
        int iM690b = (int) (m690b(jM690b7, jM692d9) >> 32);
        int i18 = (int) j11;
        if (iM690b >= 1083179008) {
            if (((iM690b - 1083179008) | i18) == 0) {
                long jM690b8 = m690b(jM690b7, 4365981760143196926L);
                long jM691c6 = m691c(j11, jM692d9);
                if (!m679o(jM690b8) && !m679o(jM691c6)) {
                    boolean z2 = m686i((jM690b8 > 0L ? 1 : (jM690b8 == 0L ? 0 : -1)) == 0 ? Long.MIN_VALUE : jM690b8, jM691c6) > 0;
                }
            }
            return z ? -4503599627370496L : 9218868437227405312L;
        }
        if ((iM690b & Integer.MAX_VALUE) >= 1083231232 && (((iM690b - (-1064252416)) | i18) != 0 || m683g(jM690b7, m691c(j11, jM692d9)))) {
            return z ? Long.MIN_VALUE : 0L;
        }
        int i19 = iM690b & Integer.MAX_VALUE;
        int i20 = (i19 >> 20) - 1023;
        int i21 = 0;
        if (i19 > 1071644672) {
            int i22 = iM690b + (1048576 >> (i20 + 1));
            int i23 = ((i22 & Integer.MAX_VALUE) >> 20) - 1023;
            long jM700b3 = m700b(0L, i22 & ((1048575 >> i23) ^ (-1)));
            i21 = ((i22 & 1048575) | 1048576) >> (20 - i23);
            if (iM690b < 0) {
                i21 = -i21;
            }
            jM692d9 = m691c(jM692d9, jM700b3);
        }
        long j12 = jM692d;
        long jM692d10 = m692d(m690b(jM690b7, jM692d9) & (-4294967296L), 4604418534330597376L);
        long jM690b9 = m690b(m692d(m691c(jM690b7, m691c(j12, jM692d9)), 4604418534313441775L), m692d(j12, -4746692435354555335L));
        long jM690b10 = m690b(jM692d10, jM690b9);
        long jM691c7 = m691c(jM690b9, m691c(jM690b10, jM692d10));
        long jM692d11 = m692d(jM690b10, jM690b10);
        long jM691c8 = m691c(jM690b10, m692d(jM692d11, m690b(4595172819793696062L, m692d(jM692d11, m690b(-4654820494858601069L, m692d(jM692d11, m690b(4544508515198557740L, m692d(jM692d11, m690b(-4702957295668925455L, m692d(jM692d11, 4496342204012209360L))))))))));
        long j13 = jM692d;
        long jM702c2 = ((((int) (m691c(4607182418800017408L, m691c(m691c(m693e(m692d(jM690b10, jM691c8), m691c(jM691c8, 4611686018427387904L)), m690b(jM691c7, m692d(jM690b10, jM691c7))), jM690b10)) >> 32)) + (i21 << 20)) >> 20) <= 0 ? m702c(j13, i21) : m700b(j13, ((int) (j13 >> 32)) + (i21 << 20));
        if (z) {
            jM702c2 = m682a(jM702c2);
        }
        return jM702c2;
    }

    /* renamed from: g */
    public static long m705g(long j) {
        if (m681q(j)) {
            return -4503599627370496L;
        }
        if (m679o(j) || m675l(j)) {
            return 9221120237041090560L;
        }
        if (j == 9218868437227405312L) {
            return j;
        }
        int i = (int) (j >> 32);
        int i2 = 0;
        if (i < 1048576) {
            i2 = 0 - 54;
            long jM702c = m702c(j, 54);
            j = jM702c;
            i = (int) (jM702c >> 32);
        }
        int i3 = i & 1048575;
        int i4 = (i3 + 614244) & 1048576;
        long jM700b = m700b(j, i3 | (i4 ^ 1072693248));
        int i5 = i2 + ((i >> 20) - 1023) + (i4 >> 20);
        long jM691c = m691c(jM700b, 4607182418800017408L);
        if ((1048575 & (i3 + 2)) >= 3) {
            long jM687b = m687b(i5);
            long jM693e = m693e(jM691c, m690b(4611686018427387904L, jM691c));
            long jM690b = m690b(m692d(m692d(m692d(jM693e, jM693e), jM693e), m690b(4600877379321592324L, m692d(jM693e, m690b(4597174411056806063L, m692d(jM693e, 4594685411790997151L))))), m692d(jM693e, m690b(4604180019048437139L, m692d(jM693e, m690b(4598818590951641945L, m692d(jM693e, m690b(4595719342595441630L, m692d(jM693e, 4594499633228436036L))))))));
            if (((i3 - 398458) | (440401 - i3)) <= 0) {
                return i5 == 0 ? m691c(jM691c, m692d(jM693e, m691c(jM691c, jM690b))) : m691c(m692d(jM687b, 4604418534311723008L), m691c(m691c(m692d(jM693e, m691c(jM691c, jM690b)), m692d(jM687b, 4461442080421002358L)), jM691c));
            }
            long jM692d = m692d(m702c(jM691c, -1), jM691c);
            return i5 == 0 ? m691c(jM691c, m691c(jM692d, m692d(jM693e, m690b(jM692d, jM690b)))) : m691c(m692d(jM687b, 4604418534311723008L), m691c(m691c(jM692d, m690b(m692d(jM693e, m690b(jM692d, jM690b)), m692d(jM687b, 4461442080421002358L))), jM691c));
        }
        if (m681q(jM691c)) {
            if (i5 == 0) {
                return 0L;
            }
            long jM687b2 = m687b(i5);
            return m690b(m692d(jM687b2, 4604418534311723008L), m692d(jM687b2, 4461442080421002358L));
        }
        long jM692d2 = m692d(m692d(jM691c, jM691c), m691c(4602678819172646912L, m692d(4599676419421066581L, jM691c)));
        if (i5 == 0) {
            return m691c(jM691c, jM692d2);
        }
        long jM687b3 = m687b(i5);
        return m691c(m692d(jM687b3, 4604418534311723008L), m691c(m691c(jM692d2, m692d(jM687b3, 4461442080421002358L)), jM691c));
    }

    /* renamed from: h */
    public static long m706h(long j) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i <= 1072243195) {
            return m709b(j, 0L, 0);
        }
        if (i >= 2146435072) {
            return 9221120237041090560L;
        }
        long[] jArr = new long[2];
        switch (m711a(j, jArr) & 3) {
            case 0:
                return m709b(jArr[0], jArr[1], 1);
            case 1:
                return m710l(jArr[0], jArr[1]);
            case 2:
                return m682a(m709b(jArr[0], jArr[1], 1));
            default:
                return m682a(m710l(jArr[0], jArr[1]));
        }
    }

    /* renamed from: i */
    public static long m707i(long j) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i <= 1072243195) {
            return m708a(j, 0L, 1);
        }
        if (i >= 2146435072) {
            return 9221120237041090560L;
        }
        long[] jArr = new long[2];
        return m708a(jArr[0], jArr[1], 1 - ((m711a(j, jArr) & 1) << 1));
    }

    /* renamed from: a */
    private static long m708a(long j, long j2, int i) {
        int i2 = (int) (j >> 32);
        int i3 = i2 & Integer.MAX_VALUE;
        if (i3 < 1043333120 && m688c(j) == 0) {
            if ((i3 | ((int) j) | (i + 1)) == 0) {
                return 9218868437227405312L;
            }
            if (i == 1) {
                return j;
            }
            long jM690b = m690b(j, j2);
            long j3 = jM690b & (-4294967296L);
            long jM691c = m691c(j2, m691c(j3, j));
            long jM693e = m693e(-4616189618054758400L, jM690b);
            long j4 = jM693e & (-4294967296L);
            return m690b(j4, m692d(jM693e, m690b(m690b(4607182418800017408L, m692d(j4, j3)), m692d(j4, jM691c))));
        }
        if (i3 >= 1072010280) {
            if (i2 < 0) {
                j = m682a(j);
                j2 = m682a(j2);
            }
            j = m690b(m691c(4605249457297304856L, j), m691c(4359948597267291143L, j2));
            j2 = 0;
        }
        long jM692d = m692d(j, j);
        long jM692d2 = m692d(jM692d, jM692d);
        long jM690b2 = m690b(4593971859893059194L, m692d(jM692d2, m690b(4581960672245896759L, m692d(jM692d2, m690b(4570429193025094440L, m692d(jM692d2, m690b(4558562946408670465L, m692d(jM692d2, m690b(4545397049192321702L, m692d(jM692d2, -4687273268743220365L))))))))));
        long jM692d3 = m692d(jM692d, m690b(4587938466107703806L, m692d(jM692d2, m690b(4576262931677611155L, m692d(jM692d2, m690b(4564358403679355669L, m692d(jM692d2, m690b(4553182066015801448L, m692d(jM692d2, m690b(4544897349388904425L, m692d(jM692d2, 4538267711989316308L)))))))))));
        long jM692d4 = m692d(jM692d, j);
        long jM690b3 = m690b(m690b(j2, m692d(jM692d, m690b(m692d(jM692d4, m690b(jM690b2, jM692d3)), j2))), m692d(4599676419421066595L, jM692d4));
        long jM690b4 = m690b(j, jM690b3);
        if (i3 >= 1072010280) {
            long jM687b = m687b(i);
            return m692d(m687b(1 - ((i2 >> 30) & 2)), m691c(jM687b, m692d(4611686018427387904L, m691c(j, m691c(m693e(m692d(jM690b4, jM690b4), m690b(jM690b4, jM687b)), jM690b3)))));
        }
        if (i == 1) {
            return jM690b4;
        }
        long j5 = jM690b4 & (-4294967296L);
        long jM691c2 = m691c(jM690b3, m691c(j5, j));
        long jM693e2 = m693e(-4616189618054758400L, jM690b4) & (-4294967296L);
        return m690b(jM693e2, m692d(jM692d, m690b(m690b(4607182418800017408L, m692d(jM693e2, j5)), m692d(jM693e2, jM691c2))));
    }

    /* renamed from: b */
    private static long m709b(long j, long j2, int i) {
        if ((((int) (j >> 32)) & Integer.MAX_VALUE) < 1044381696) {
            return j;
        }
        long jM692d = m692d(j, j);
        long jM692d2 = m692d(jM692d, j);
        long jM690b = m690b(4575957461383575718L, m692d(jM692d, m690b(-4671919876304969259L, m692d(jM692d, m690b(4523617212983017085L, m692d(jM692d, m690b(-4730215680275931925L, m692d(jM692d, 4460209850635244924L))))))));
        return i == 0 ? m690b(j, m692d(jM692d2, m690b(-4628199217061079735L, m692d(jM692d, jM690b)))) : m691c(j, m691c(m691c(m692d(jM692d, m691c(m692d(4602678819172646912L, j2), m692d(jM692d2, jM690b))), j2), m692d(jM692d2, -4628199217061079735L)));
    }

    /* renamed from: l */
    private static long m710l(long j, long j2) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i < 1044381696) {
            return 4607182418800017408L;
        }
        long jM692d = m692d(j, j);
        long jM692d2 = m692d(jM692d, m690b(4586165620538955084L, m692d(jM692d, m690b(-4659324094485802633L, m692d(jM692d, m690b(4537941361668330896L, m692d(jM692d, m690b(-4714566979978243411L, m692d(jM692d, m690b(4477121870137962948L, m692d(jM692d, -4780295122622859052L)))))))))));
        if (i < 1070805811) {
            return m691c(4607182418800017408L, m691c(m692d(4602678819172646912L, jM692d), m691c(m692d(jM692d, jM692d2), m692d(j, j2))));
        }
        long j3 = i > 1072234496 ? 4598738169498697728L : (i - 2097152) << 32;
        return m691c(m691c(4607182418800017408L, j3), m691c(m691c(m692d(4602678819172646912L, jM692d), j3), m691c(m692d(jM692d, jM692d2), m692d(j, j2))));
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00de  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static int m711a(long j, long[] jArr) {
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
                jM682a = m682a(4609753056924401664L);
                jM682a2 = m682a(jM682a2);
            }
            long jM690b = m690b(j, jM682a);
            jArr[0] = m690b(jM690b, jM682a2);
            jArr[1] = m690b(m691c(jM690b, jArr[0]), jM682a2);
            return i2 > 0 ? 1 : -1;
        }
        if (i3 <= 1094263291) {
            long j2 = j & Long.MAX_VALUE;
            int iM688c2 = m688c(m694a(m692d(j2, 4603909380684499075L), true, false));
            long jM691c2 = m691c(j2, m692d(0L, 4609753056924401664L));
            long jM692d = m692d(0L, 4454258360616903473L);
            if (iM688c2 < 32) {
                if (i3 != ((int[]) AppState.f177b[993])[iM688c2 - 1]) {
                    jArr[0] = m691c(jM691c2, jM692d);
                } else {
                    int i4 = i3 >> 20;
                    jArr[0] = m691c(jM691c2, jM692d);
                    if (i4 - ((((int) (jArr[0] >> 32)) >> 20) & 2047) > 16) {
                        long jM692d2 = m692d(0L, 4454258360616747008L);
                        jM691c2 = m691c(jM691c2, jM692d2);
                        jM692d = m691c(m692d(0L, 4297306550709743731L), m691c(m691c(jM691c2, jM691c2), jM692d2));
                        jArr[0] = m691c(jM691c2, jM692d);
                        if (i4 - ((((int) (jArr[0] >> 32)) >> 20) & 2047) > 49) {
                            long jM692d3 = m692d(0L, 4297306550709518336L);
                            jM691c2 = m691c(jM691c2, jM692d3);
                            jM692d = m691c(m692d(0L, 4142048980368378305L), m691c(m691c(jM691c2, jM691c2), jM692d3));
                            jArr[0] = m691c(jM691c2, jM692d);
                        }
                    }
                }
            }
            jArr[1] = m691c(m691c(jM691c2, jArr[0]), jM692d);
            if (i2 >= 0) {
                return iM688c2;
            }
            jArr[0] = m682a(jArr[0]);
            jArr[1] = m682a(jArr[1]);
            return -iM688c2;
        }
        if (i3 >= 2146435072) {
            jArr[1] = 9221120237041090560L;
            jArr[0] = 9221120237041090560L;
            return 0;
        }
        int i5 = (i3 >> 20) - 1046;
        long jM700b = m700b((int) j, i3 - (i5 << 20));
        long[] jArr2 = new long[3];
        for (int i6 = 0; i6 < 2; i6++) {
            jArr2[i6] = m687b(m688c(jM700b));
            jM700b = m702c(m691c(jM700b, jArr2[i6]), 24);
        }
        jArr2[2] = jM700b;
        int i7 = 3;
        while (m681q(jArr2[i7 - 1])) {
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
            jArr3[i14] = i12 < 0 ? 0L : m687b(C0034e.m944c(i12));
            i14++;
            i12++;
        }
        long[] jArr4 = new long[20];
        for (int i15 = 0; i15 <= 4; i15++) {
            long jM690b2 = 0;
            for (int i16 = 0; i16 <= i8; i16++) {
                jM690b2 = m690b(jM690b2, m692d(jArr2[i16], jArr3[(i8 + i15) - i16]));
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
                long jM687b = m687b(m688c(m702c(jM690b3, -24)));
                iArr[i18] = m688c(m691c(jM690b3, m702c(jM687b, 24)));
                jM690b3 = m690b(jArr4[i19 - 1], jM687b);
                i18++;
            }
            long jM702c = m702c(jM690b3, i11);
            long jM691c3 = m691c(jM702c, m702c(m694a(m702c(jM702c, -3), false, false), 3));
            long jM691c4 = m691c(jM691c3, m692d(4620693217682128896L, m694a(m692d(jM691c3, 4593671619917905920L), false, false)));
            iM688c = m688c(jM691c4);
            jM691c = m691c(jM691c4, m687b(iM688c));
            i = 0;
            if (i11 > 0) {
                int i20 = iArr[i17 - 1] >> (24 - i11);
                iM688c += i20;
                int i21 = i17 - 1;
                iArr[i21] = iArr[i21] - (i20 << (24 - i11));
                i = iArr[i17 - 1] >> (23 - i11);
            } else if (i11 == 0) {
                i = iArr[i17 - 1] >> 23;
            } else if (m684h(jM691c, 4602678819172646912L)) {
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
                    jM691c = m691c(4607182418800017408L, jM691c);
                    if (z2) {
                        jM691c = m691c(jM691c, m702c(4607182418800017408L, i11));
                    }
                }
            }
            if (m681q(jM691c)) {
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
                        jArr3[i8 + i29] = m687b(C0034e.m944c(i10 + i29));
                        long jM690b4 = 0;
                        for (int i30 = 0; i30 <= i8; i30++) {
                            jM690b4 = m690b(jM690b4, m692d(jArr2[i30], jArr3[(i8 + i29) - i30]));
                        }
                        jArr4[i29] = jM690b4;
                    }
                    i17 += i28;
                    z = true;
                }
            }
        } while (z);
        if (m681q(jM691c)) {
            do {
                i17--;
                i11 -= 24;
            } while (iArr[i17] == 0);
        } else {
            long jM702c2 = m702c(jM691c, -i11);
            if (m684h(jM702c2, 4715268809856909312L)) {
                long jM687b2 = m687b(m688c(m702c(jM702c2, -24)));
                iArr[i17] = m688c(m691c(jM702c2, m702c(jM687b2, 24)));
                i17++;
                i11 += 24;
                iArr[i17] = m688c(jM687b2);
            } else {
                iArr[i17] = m688c(jM702c2);
            }
        }
        long jM702c3 = m702c(4607182418800017408L, i11);
        for (int i31 = i17; i31 >= 0; i31--) {
            jArr4[i31] = m692d(jM702c3, m687b(iArr[i31]));
            jM702c3 = m702c(jM702c3, -24);
        }
        long[] jArr5 = new long[20];
        for (int i32 = i17; i32 >= 0; i32--) {
            long jM690b5 = 0;
            for (int i33 = 0; i33 <= 4 && i33 <= i17 - i32; i33++) {
                jM690b5 = m690b(jM690b5, m692d(((long[]) AppState.f177b[990])[i33], jArr4[i32 + i33]));
            }
            jArr5[i17 - i32] = jM690b5;
        }
        long jM690b6 = 0;
        for (int i34 = i17; i34 >= 0; i34--) {
            jM690b6 = m690b(jM690b6, jArr5[i34]);
        }
        jArr[0] = i == 0 ? jM690b6 : m682a(jM690b6);
        long jM691c5 = m691c(jArr5[0], jM690b6);
        for (int i35 = 1; i35 <= i17; i35++) {
            jM691c5 = m690b(jM691c5, jArr5[i35]);
        }
        jArr[1] = i == 0 ? jM691c5 : m682a(jM691c5);
        int i36 = iM688c & 7;
        if (i2 >= 0) {
            return i36;
        }
        jArr[0] = m682a(jArr[0]);
        jArr[1] = m682a(jArr[1]);
        return -i36;
    }

    /* renamed from: j */
    public static long m712j(long j) {
        int i;
        int i2 = (int) (j >> 32);
        int i3 = i2 & Integer.MAX_VALUE;
        if (i3 >= 1141899264) {
            if (i3 > 2146435072) {
                return 9221120237041090560L;
            }
            if (i3 != 2146435072 || ((int) j) == 0) {
                return i2 > 0 ? C0034e.m943b(3) : m682a(C0034e.m943b(3));
            }
            return 9221120237041090560L;
        }
        if (i3 >= 1071382528) {
            long j2 = j & Long.MAX_VALUE;
            if (i3 < 1072889856) {
                if (i3 < 1072037888) {
                    i = 0;
                    j = m693e(m691c(m702c(j2, 1), 4607182418800017408L), m690b(4611686018427387904L, j2));
                } else {
                    i = 1;
                    j = m693e(m691c(j2, 4607182418800017408L), m690b(j2, 4607182418800017408L));
                }
            } else if (i3 < 1073971200) {
                i = 2;
                j = m693e(m691c(j2, 4609434218613702656L), m690b(4607182418800017408L, m692d(4609434218613702656L, j2)));
            } else {
                i = 3;
                j = m693e(-4616189618054758400L, j2);
            }
        } else {
            if (i3 < 1042284544) {
                return j;
            }
            i = -1;
        }
        long jM692d = m692d(j, j);
        long jM692d2 = m692d(jM692d, jM692d);
        long jM692d3 = m692d(jM692d, m690b(4599676419421066509L, m692d(jM692d2, m690b(4594314991288484863L, m692d(jM692d2, m690b(4591215095208222830L, m692d(jM692d2, m690b(4589464229703073105L, m692d(jM692d2, m690b(4587333258118041067L, m692d(jM692d2, 4580351289466214929L)))))))))));
        long jM692d4 = m692d(jM692d2, m690b(-4626998257160492092L, m692d(jM692d2, m690b(-4630701217362536847L, m692d(jM692d2, m690b(-4633165035261879699L, m692d(jM692d2, m690b(-4634804155249132134L, m692d(jM692d2, -4637946461342241745L)))))))));
        if (i < 0) {
            return m691c(j, m692d(j, m690b(jM692d3, jM692d4)));
        }
        long jM691c = m691c(C0034e.m943b(i), m691c(m691c(m692d(j, m690b(jM692d3, jM692d4)), C0034e.m943b(i + 4)), j));
        return i2 < 0 ? m682a(jM691c) : jM691c;
    }

    public final String toString() {
        return m698a(0L, 100);
    }

    public final int hashCode() {
        return 0;
    }

    public final boolean equals(Object obj) {
        return obj instanceof SoftFloat;
    }

    /* renamed from: k */
    public static final long m713k(long j) {
        int i = ((int) (j >> 32)) & Integer.MAX_VALUE;
        if (i <= 1072243195) {
            return m710l(j, 0L);
        }
        if (i >= 2146435072) {
            return 9221120237041090560L;
        }
        long[] jArr = new long[2];
        switch (m711a(j, jArr) & 3) {
            case 0:
                return m710l(jArr[0], jArr[1]);
            case 1:
                return m682a(m709b(jArr[0], jArr[1], 1));
            case 2:
                return m682a(m710l(jArr[0], jArr[1]));
            default:
                return m709b(jArr[0], jArr[1], 1);
        }
    }
}
