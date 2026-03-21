package p000;

import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Font;

/* renamed from: as */
/* loaded from: MobileAgent_3.9.jar:as.class */
public abstract class Utils {
    /* renamed from: a */
    public static final void arraycopy(Object obj, int i, Object obj2, int i2, int i3) {
        if (i3 > 0) {
            System.arraycopy(obj, i, obj2, i2, i3);
        }
    }

    /* renamed from: a */
    public static final long parseDateTime(String str) {
        Vector vectorM517a = splitImpl(str, ' ', false);
        int iM510a = parseInt(vectorM517a.elementAt(1));
        int iIndexOf = AppState.getString(2558996).indexOf(getVectorString(vectorM517a, 2)) / 3;
        int iM510a2 = parseInt(vectorM517a.elementAt(3));
        String strM521a = getVectorString(vectorM517a, 4);
        NetworkUtils.releaseVector(vectorM517a);
        Vector vectorM517a2 = splitImpl(strM521a, ':', false);
        int iM510a3 = parseInt(vectorM517a2.elementAt(0));
        int iM510a4 = parseInt(vectorM517a2.elementAt(1));
        parseInt(vectorM517a2.elementAt(2));
        NetworkUtils.releaseVector(vectorM517a2);
        byte b = (iM510a2 % 4 != 0 || iM510a2 == 2000) ? (byte) 28 : (byte) 29;
        int i = (((((iM510a2 - 1970) * 365) + ((iM510a2 - 1968) / 4)) + iM510a) + 28) - b;
        if (iM510a2 >= 2000) {
            i--;
        }
        int i2 = iIndexOf - 1;
        while (true) {
            i2--;
            if (i2 < 0) {
                return (1000 * (((86400 * i) + (iM510a3 * 3600)) + (iM510a4 * 60))) - ((AppState.getInt(246) - 13) * 3600000);
            }
            i += i2 == 1 ? b : AppState.getBytes(945)[i2];
        }
    }

    /* renamed from: a */
    private static StringBuffer appendKey(StringBuffer stringBuffer, int i) {
        return stringBuffer.append(AppState.getString(i));
    }

    /* renamed from: a */
    public static final StringBuffer appendParam(StringBuffer stringBuffer, int i, String str) {
        return appendKey(stringBuffer, i).append('=').append(Conversation.urlEncodeCyrillic((Object) defaultStr(str)));
    }

    /* renamed from: a */
    public static final StringBuffer appendIntParam(StringBuffer stringBuffer, int i, int i2) {
        return appendKey(stringBuffer, i).append('=').append(i2);
    }

    /* renamed from: b */
    public static final String withComma(String str) {
        return NetworkUtils.bufToStringCached(appendCommaIf(NetworkUtils.newStringBuffer().append(str), true));
    }

    /* renamed from: a */
    public static final StringBuffer appendCommaIf(StringBuffer stringBuffer, boolean z) {
        if (z) {
            stringBuffer.append(',').append(' ');
        }
        return stringBuffer;
    }

    /* renamed from: a */
    public static final StringBuffer appendColon(StringBuffer stringBuffer) {
        return stringBuffer.append(':').append(' ');
    }

    /* renamed from: a */
    public static final boolean isDigitOrSep(char c) {
        return (c >= '0' && c <= '9') || c == '.' || c == ':';
    }

    /* renamed from: a */
    public static final char win1251ToChar(int i) {
        int i2 = i & 255;
        if (i2 >= 192) {
            return (char) (i2 + 848);
        }
        if (i2 == 168) {
            return (char) 1025;
        }
        if (i2 == 184) {
            return (char) 1105;
        }
        return (char) i2;
    }

    /* renamed from: b */
    public static final byte charToWin1251(char c) {
        if (c >= 1040 && c <= 1103) {
            return (byte) (c - 848);
        }
        if (c == 1025) {
            return (byte) -88;
        }
        if (c == 1105) {
            return (byte) -72;
        }
        return (byte) c;
    }

    /* renamed from: b */
    public static final String zeroPad(int i) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        if (i < 10) {
            stringBufferM1217h.append('0');
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h.append(i));
    }

    /* renamed from: a */
    public static final int max(int i, int i2) {
        return i > i2 ? i : i2;
    }

    /* renamed from: b */
    public static final int min(int i, int i2) {
        return i < i2 ? i : i2;
    }

    /* renamed from: c */
    public static final int abs(int i) {
        return i >= 0 ? i : -i;
    }

    /* renamed from: a */
    public static final long absLong(long j) {
        return j >= 0 ? j : -j;
    }

    /* renamed from: c */
    public static final String maskPassword(String str) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = str.length();
        while (true) {
            length--;
            if (length < 0) {
                return NetworkUtils.bufToStringCached(stringBufferM1217h);
            }
            stringBufferM1217h.append('*');
        }
    }

    /* renamed from: d */
    public static final String quoteText(String str) {
        StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append('>');
        int length = str == null ? 0 : str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '\n') {
                stringBufferAppend.append('\n').append('>');
            } else {
                stringBufferAppend.append(cCharAt);
            }
        }
        return NetworkUtils.bufToStringCached(stringBufferAppend);
    }

    /* renamed from: a */
    public static final void swapElements(Vector vector, int i, int i2) {
        Object objElementAt = vector.elementAt(i);
        vector.setElementAt(vector.elementAt(i2), i);
        vector.setElementAt(objElementAt, i2);
    }

    /* renamed from: a */
    public static final boolean compareBytes(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        do {
            i3--;
            if (i3 < 0) {
                return true;
            }
        } while (bArr[i + i3] == bArr2[i2 + i3]);
        return false;
    }

    /* renamed from: a */
    public static final int parseInt(Object obj) {
        try {
            return Integer.parseInt((String) obj);
        } catch (Throwable unused) {
            return 0;
        }
    }

    /* renamed from: a */
    public static final int parseIntBounded(String str, int i, int i2, int i3) {
        int i4 = i3;
        try {
            i4 = Integer.parseInt(str);
        } catch (Throwable unused) {
        }
        return (i4 < i || i4 > i2) ? i3 : i4;
    }

    /* renamed from: e */
    public static final Vector splitByNull(String str) {
        return splitImpl(str, (char) 0, false);
    }

    /* renamed from: a */
    public static final Vector splitReplace(String str, char c, char c2) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = str == null ? 0 : str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            stringBufferM1217h.append(cCharAt == c2 ? c : cCharAt);
        }
        return splitImpl(NetworkUtils.bufToStringCached(stringBufferM1217h), c, false);
    }

    /* renamed from: a */
    public static final Vector splitMerge(String str, char c) {
        Vector vectorM517a = splitImpl(str, '|', true);
        while (vectorM517a.size() > 5) {
            vectorM517a.setElementAt(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(getVectorString(vectorM517a, 4)).append('|').append(vectorM517a.elementAt(5))), 4);
            vectorM517a.removeElementAt(5);
        }
        return vectorM517a;
    }

    /* renamed from: b */
    public static final Vector split(String str, char c) {
        return splitImpl(str, c, true);
    }

    /* renamed from: c */
    public static final Vector splitNonEmpty(String str, char c) {
        return splitImpl(str, c, false);
    }

    /* renamed from: a */
    private static final Vector splitImpl(String str, char c, boolean z) {
        Vector vectorM1213g = NetworkUtils.newVector();
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = str == null ? 0 : str.length();
        int i = 0;
        while (i <= length) {
            char cCharAt = i < length ? str.charAt(i) : c;
            char c2 = cCharAt;
            if (cCharAt != c) {
                stringBufferM1217h.append(c2);
            } else if (z || stringBufferM1217h.length() > 0) {
                vectorM1213g.addElement(NetworkUtils.bufToString(stringBufferM1217h, false));
            }
            i++;
        }
        NetworkUtils.bufToStringCached(stringBufferM1217h);
        return vectorM1213g;
    }

    /* renamed from: a */
    public static final String[] getPhoneNumbers(boolean z) {
        Vector vectorM1213g = NetworkUtils.newVector();
        for (int i = 0; i < 3; i++) {
            StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
            String strM522f = defaultStr(AppState.getString(i + 1303));
            int length = strM522f.length();
            for (int i2 = 0; i2 < length; i2++) {
                char cCharAt = strM522f.charAt(i2);
                if ((cCharAt >= '0' && cCharAt <= '9') || (z && cCharAt == '+')) {
                    stringBufferM1217h.append(cCharAt);
                }
            }
            String strM1215a = NetworkUtils.bufToStringCached(stringBufferM1217h);
            if (!StringUtils.isEmpty(strM1215a)) {
                vectorM1213g.addElement(strM1215a);
            }
        }
        String[] strArr = new String[vectorM1213g.size()];
        vectorM1213g.copyInto(strArr);
        return strArr;
    }

    /* renamed from: a */
    public static final String joinComma(String[] strArr) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                stringBufferM1217h.append(',');
            }
            stringBufferM1217h.append(strArr[i]);
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
    }

    /* renamed from: a */
    public static final int nextRandom() {
        return ((Random) AppState.pool[1372]).nextInt();
    }

    /* renamed from: a */
    public static final String getVectorString(Vector vector, int i) {
        return (String) vector.elementAt(i);
    }

    /* renamed from: f */
    public static final String defaultStr(String str) {
        return str != null ? str : AppState.emptyStr;
    }

    /* renamed from: d */
    public static final String formatSize(int i) {
        int i2 = 752;
        int i3 = 0;
        for (int i4 = 0; i4 < 2 && i > 1024; i4++) {
            i3 = i % 1024;
            i /= 1024;
            i2++;
        }
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        stringBufferM1217h.append(i);
        if (i3 != 0 && i2 == 754) {
            stringBufferM1217h.append('.');
            String strM17c = StringUtils.intern(Integer.toString(i3));
            String strM13b = strM17c;
            if (strM17c.length() > 2) {
                strM13b = StringUtils.prefix(strM13b, 2);
            }
            if (strM13b.length() < 2) {
                stringBufferM1217h.append('0');
            }
            stringBufferM1217h.append(strM13b);
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h.append(AppState.getString(i2)));
    }

    /* renamed from: a */
    public static final Object dequeue(Vector vector) {
        synchronized (vector) {
            if (vector.size() == 0) {
                return null;
            }
            Object objElementAt = vector.elementAt(0);
            vector.removeElementAt(0);
            trimIfEmpty(vector);
            return objElementAt;
        }
    }

    /* renamed from: a */
    public static final void removeFrom(Vector vector, Object obj) {
        vector.removeElement(obj);
        trimIfEmpty(vector);
    }

    /* renamed from: b */
    public static final void trimIfEmpty(Vector vector) {
        if (vector.size() == 0) {
            vector.trimToSize();
        }
    }

    /* renamed from: g */
    public static final String appendSpace(String str) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(str).append(' '));
    }

    /* renamed from: a */
    public static final String defaultIfBlank(String str, String str2) {
        for (int length = str.length() - 1; length >= 0; length--) {
            if (str.charAt(length) != ' ') {
                return str;
            }
        }
        return str2;
    }

    /* renamed from: d */
    public static final String removeChar(String str, char c) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt != c) {
                stringBufferM1217h.append(cCharAt);
            }
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
    }

    /* renamed from: h */
    public static final String formatPhone(String str) {
        if (str == null) {
            return AppState.emptyStr;
        }
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        if (startsWithInt(str, 99897)) {
            for (int i = 0; i < str.length(); i++) {
                if (i == 0) {
                    stringBufferM1217h.append('(').append('+');
                } else if (i == 3 || i == 8) {
                    stringBufferM1217h.append(' ');
                } else if (i == 5) {
                    stringBufferM1217h.append(')');
                }
                stringBufferM1217h.append(str.charAt(i));
            }
        } else {
            int i2 = startsWithInt(str, 380) ? 1 : 0;
            for (int i3 = 0; i3 < str.length(); i3++) {
                if (i3 == 0) {
                    stringBufferM1217h.append('+');
                } else if (i3 == i2 + 1) {
                    stringBufferM1217h.append('(');
                } else if (i3 == i2 + 4) {
                    stringBufferM1217h.append(')');
                } else if (i3 == i2 + 7) {
                    stringBufferM1217h.append('-');
                }
                stringBufferM1217h.append(str.charAt(i3));
            }
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
    }

    /* renamed from: a */
    private static boolean startsWithInt(String str, int i) {
        return str.startsWith(Integer.toString(i));
    }

    /* renamed from: i */
    public static final String extractDigits(String str) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char cCharAt = str.charAt(i);
                if (cCharAt >= '0' && cCharAt <= '9') {
                    stringBufferM1217h.append(cCharAt);
                }
            }
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
    }

    /* renamed from: j */
    public static final String trim(String str) {
        while (str.length() > 0 && str.charAt(0) == ' ') {
            str = StringUtils.suffix(str, 1);
        }
        int length = str.length();
        while (true) {
            length--;
            if (length < 0 || str.charAt(length) != ' ') {
                break;
            }
            str = StringUtils.prefix(str, length);
        }
        return str;
    }

    /* renamed from: k */
    public static final String trimAll(String str) {
        while (str.length() > 0 && (str.charAt(0) & 65535) <= 32) {
            str = StringUtils.suffix(str, 1);
        }
        int length = str.length();
        while (true) {
            length--;
            if (length < 0 || (str.charAt(length) & 65535) > 32) {
                break;
            }
            str = StringUtils.prefix(str, length);
        }
        return str;
    }

    /* renamed from: l */
    public static final boolean nonEmpty(String str) {
        return str != null && str.length() > 0;
    }

    /* renamed from: a */
    public static final int[] bytesToInts(byte[] bArr) {
        int length = bArr.length >> 2;
        int[] iArr = new int[length];
        int i = 0;
        int i2 = 0;
        while (i2 < length) {
            int i3 = 0;
            do {
                int i4 = i;
                i++;
                i3 = (i3 << 8) | (bArr[i4] & 255);
            } while ((i & 3) != 0);
            int i5 = i2;
            i2++;
            iArr[i5] = i3;
        }
        return iArr;
    }

    /* renamed from: e */
    public static final short[] readShortArray(int i) {
        byte[] bArrM581a = AppState.getBytes(i);
        int length = bArrM581a.length >> 1;
        short[] sArr = new short[length];
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            int i4 = i3;
            i3++;
            int i5 = i2;
            int i6 = i2 + 1;
            i2 = i6 + 1;
            sArr[i4] = (short) ((bArrM581a[i5] << 8) | (bArrM581a[i6] & 255));
        }
        NetworkUtils.releaseBytes(bArrM581a);
        return sArr;
    }

    /* renamed from: m */
    public static final String beforeAt(String str) {
        int iIndexOf = str.indexOf(64);
        return iIndexOf >= 0 ? StringUtils.prefix(str, iIndexOf) : str;
    }

    /* renamed from: n */
    public static final String normalizeSpaces(String str) {
        int length;
        if (str == null || (length = str.length()) == 0) {
            return str;
        }
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        char c = 0;
        int i = 0;
        while (i < length) {
            char cCharAt = str.charAt(i);
            char c2 = cCharAt;
            switch (cCharAt) {
                case '\t':
                    c2 = ' ';
                case ' ':
                    if (c == ' ') {
                        break;
                    }
                default:
                    stringBufferM1217h.append(c2);
                    break;
            }
            i++;
            c = c2;
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
    }

    /* renamed from: f */
    public static final int pluralForm(int i) {
        int i2 = i % 10;
        if (i2 == 1 && i % 100 != 11) {
            return 0;
        }
        if (i2 > 4 || i2 == 0) {
            return 1;
        }
        return (i <= 10 || i >= 20) ? 2 : 1;
    }

    /* renamed from: c */
    public static final int vectorSize(Vector vector) {
        if (vector == null) {
            return 0;
        }
        return vector.size();
    }

    /* renamed from: c */
    public static final String splitAndGet(int i, int i2) {
        Vector vectorM517a = splitImpl(AppState.getString(i), (char) 0, false);
        String str = (String) vectorM517a.elementAt(i2);
        NetworkUtils.releaseVector(vectorM517a);
        return str;
    }

    /* renamed from: a */
    public static Vector wrapText(String str, Font font, int i) {
        Vector vectorM1213g = NetworkUtils.newVector();
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int i2 = 0;
        int iIndexOf = str.indexOf(32);
        int length = iIndexOf;
        if (iIndexOf < 0) {
            length = str.length();
        }
        int i3 = 0;
        int iStringWidth = font.stringWidth(AppState.getString(1046));
        while (length != -1) {
            String strM12a = StringUtils.substring(str, i2, length);
            int iStringWidth2 = font.stringWidth(strM12a);
            i3 += iStringWidth2;
            if (stringBufferM1217h.length() > 0) {
                i3 += iStringWidth;
            }
            if (stringBufferM1217h.length() <= 0 || i3 <= i) {
                if (stringBufferM1217h.length() > 0) {
                    stringBufferM1217h.append(' ');
                }
                stringBufferM1217h.append(strM12a);
            } else {
                vectorM1213g.addElement(StringUtils.extractBuffer(stringBufferM1217h));
                stringBufferM1217h.append(strM12a);
                i3 = iStringWidth2;
            }
            if (length == str.length()) {
                break;
            }
            i2 = length + 1;
            int iIndexOf2 = str.indexOf(32, i2);
            length = iIndexOf2;
            if (iIndexOf2 < 0) {
                length = str.length();
            }
        }
        if (stringBufferM1217h.length() > 0) {
            vectorM1213g.addElement(StringUtils.extractBuffer(stringBufferM1217h));
        }
        return vectorM1213g;
    }

    /* renamed from: b */
    public static final String generateRandomHash() {
        return new ByteBuffer().writeIntLE(nextRandom()).writeLong(System.currentTimeMillis()).encryptMD5().toHexString();
    }
}
