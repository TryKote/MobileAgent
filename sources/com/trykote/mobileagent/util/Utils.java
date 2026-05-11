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
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Font;

public abstract class Utils {

    public static String getFreeMemoryString() {
        return StringUtils.intern(Long.toString(Runtime.getRuntime().freeMemory()));
    }

    // Windows-1251 ↔ Unicode Cyrillic conversion offsets
    private static final int WIN1251_CYRILLIC_START = 192;
    private static final int WIN1251_TO_UNICODE_OFFSET = 848;
    private static final int WIN1251_YO_UPPER = 168;    // Ё
    private static final int WIN1251_YO_LOWER = 184;    // ё
    private static final int UNICODE_YO_UPPER = 1025;   // Ё
    private static final int UNICODE_YO_LOWER = 1105;   // ё
    private static final int UNICODE_CYRILLIC_A = 1040;  // А
    private static final int UNICODE_CYRILLIC_YA = 1103; // я

    // formatSize() unit key offsets
    private static final int SIZE_UNIT_BYTES = 752;
    private static final int SIZE_UNIT_MB = 754;

    private static final long ONE_WEEK_MS = 604800000L;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final long MS_PER_SECOND = 1000L;
    public static final void arraycopy(Object obj, int i, Object obj2, int i2, int i3) {
        if (i3 > 0) {
            System.arraycopy(obj, i, obj2, i2, i3);
        }
    }

    public static final long parseDateTime(String str) {
        Vector parts = splitImpl(str, ' ', false);
        int day = parseInt(parts.elementAt(1));
        int idx = ResourceAccessor.str(PackedStringKeys.MONTH_ABBREV_TABLE).indexOf(getVectorString(parts, 2)) / 3;
        int year = parseInt(parts.elementAt(3));
        String timeStr = getVectorString(parts, 4);
        ObjectPool.releaseVector(parts);
        Vector timeParts = splitImpl(timeStr, ':', false);
        int hours = parseInt(timeParts.elementAt(0));
        int minutes = parseInt(timeParts.elementAt(1));
        parseInt(timeParts.elementAt(2));
        ObjectPool.releaseVector(timeParts);
        byte b = (year % 4 != 0 || year == 2000) ? (byte) 28 : (byte) 29;
        int i = (((((year - 1970) * 365) + ((year - 1968) / 4)) + day) + 28) - b;
        if (year >= 2000) {
            i--;
        }
        for (int i2 = idx - 2; i2 >= 0; i2--) {
            i += i2 == 1 ? b : ResourceAccessor.bytes(StringResKeys.RES_MONTH_DAYS)[i2];
        }
        return (MS_PER_SECOND * (((SECONDS_PER_DAY * i) + (hours * SECONDS_PER_HOUR)) + (minutes * SECONDS_PER_MINUTE))) - ((SettingsState.getTimezoneOffset() - 13) * (SECONDS_PER_HOUR * MS_PER_SECOND));
    }

    private static StringBuffer appendKey(StringBuffer stringBuffer, int i) {
        return stringBuffer.append(AppState.getString(i));
    }

    public static final StringBuffer appendParam(StringBuffer stringBuffer, int i, String str) {
        return appendKey(stringBuffer, i).append('=').append(Conversation.urlEncodeCyrillic((Object) defaultStr(str)));
    }

    public static final StringBuffer appendIntParam(StringBuffer stringBuffer, int i, int i2) {
        return appendKey(stringBuffer, i).append('=').append(i2);
    }

    public static final String withComma(String str) {
        return ObjectPool.toStringAndRelease(appendCommaIf(ObjectPool.newStringBuffer().append(str), true));
    }

    public static final StringBuffer appendCommaIf(StringBuffer stringBuffer, boolean z) {
        if (z) {
            stringBuffer.append(',').append(' ');
        }
        return stringBuffer;
    }

    public static final StringBuffer appendColon(StringBuffer stringBuffer) {
        return stringBuffer.append(':').append(' ');
    }

    public static final boolean isDigitOrSep(char c) {
        return (c >= '0' && c <= '9') || c == '.' || c == ':';
    }

    public static final char win1251ToChar(int i) {
        int i2 = i & 255;
        if (i2 >= WIN1251_CYRILLIC_START) {
            return (char) (i2 + WIN1251_TO_UNICODE_OFFSET);
        }
        if (i2 == WIN1251_YO_UPPER) {
            return (char) UNICODE_YO_UPPER;
        }
        if (i2 == WIN1251_YO_LOWER) {
            return (char) UNICODE_YO_LOWER;
        }
        return (char) i2;
    }

    public static final byte charToWin1251(char c) {
        if (c >= UNICODE_CYRILLIC_A && c <= UNICODE_CYRILLIC_YA) {
            return (byte) (c - WIN1251_TO_UNICODE_OFFSET);
        }
        if (c == UNICODE_YO_UPPER) {
            return (byte) (WIN1251_YO_UPPER - 256);
        }
        if (c == UNICODE_YO_LOWER) {
            return (byte) (WIN1251_YO_LOWER - 256);
        }
        return (byte) c;
    }

    public static final String zeroPad(int i) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (i < 10) {
            sb.append('0');
        }
        return ObjectPool.toStringAndRelease(sb.append(i));
    }

    public static final int max(int i, int i2) {
        return i > i2 ? i : i2;
    }

    public static final int min(int i, int i2) {
        return i < i2 ? i : i2;
    }

    public static final int abs(int i) {
        return i >= 0 ? i : -i;
    }

    public static final long absLong(long j) {
        return j >= 0 ? j : -j;
    }

    public static final String maskPassword(String str) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int idx = str.length() - 1; idx >= 0; idx--) {
            sb.append('*');
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final String quoteText(String str) {
        StringBuffer sb = ObjectPool.newStringBuffer().append('>');
        int length = str == null ? 0 : str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (ch == '\n') {
                sb.append('\n').append('>');
            } else {
                sb.append(ch);
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final void swapElements(Vector vector, int i, int i2) {
        Object elem = vector.elementAt(i);
        vector.setElementAt(vector.elementAt(i2), i);
        vector.setElementAt(elem, i2);
    }

    public static final boolean compareBytes(byte[] bArr, int i, byte[] bArr2, int i2, int i3) {
        do {
            i3--;
            if (i3 < 0) {
                return true;
            }
        } while (bArr[i + i3] == bArr2[i2 + i3]);
        return false;
    }

    public static final int parseInt(Object obj) {
        try {
            return Integer.parseInt((String) obj);
        } catch (Throwable unused) {
            return 0;
        }
    }

    public static final int parseIntBounded(String str, int i, int i2, int i3) {
        int i4 = i3;
        try {
            i4 = Integer.parseInt(str);
        } catch (Throwable unused) {
        }
        return (i4 < i || i4 > i2) ? i3 : i4;
    }

    public static final Vector splitByNull(String str) {
        return splitImpl(str, (char) 0, false);
    }

    public static final Vector splitReplace(String str, char c, char c2) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str == null ? 0 : str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            sb.append(ch == c2 ? c : ch);
        }
        return splitImpl(ObjectPool.toStringAndRelease(sb), c, false);
    }

    public static final Vector splitMerge(String str, char c) {
        Vector parts = splitImpl(str, '|', true);
        while (parts.size() > 5) {
            parts.setElementAt(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(getVectorString(parts, 4)).append('|').append(parts.elementAt(5))), 4);
            parts.removeElementAt(5);
        }
        return parts;
    }

    public static final Vector split(String str, char c) {
        return splitImpl(str, c, true);
    }

    public static final Vector splitNonEmpty(String str, char c) {
        return splitImpl(str, c, false);
    }

    private static final Vector splitImpl(String str, char c, boolean z) {
        Vector result = ObjectPool.newVector();
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str == null ? 0 : str.length();
        int i = 0;
        while (i <= length) {
            char ch = i < length ? str.charAt(i) : c;
            char c2 = ch;
            if (ch != c) {
                sb.append(c2);
            } else if (z || sb.length() > 0) {
                result.addElement(ObjectPool.toString(sb, false));
            }
            i++;
        }
        ObjectPool.toStringAndRelease(sb);
        return result;
    }

    public static final String[] getPhoneNumbers(boolean z) {
        Vector result = ObjectPool.newVector();
        for (int i = 0; i < 3; i++) {
            StringBuffer sb = ObjectPool.newStringBuffer();
            String rawPhone = defaultStr(UIState.getContactNamePart(i));
            int length = rawPhone.length();
            for (int i2 = 0; i2 < length; i2++) {
                char ch = rawPhone.charAt(i2);
                if ((ch >= '0' && ch <= '9') || (z && ch == '+')) {
                    sb.append(ch);
                }
            }
            String phoneNum = ObjectPool.toStringAndRelease(sb);
            if (!StringUtils.isEmpty(phoneNum)) {
                result.addElement(phoneNum);
            }
        }
        String[] strArr = new String[result.size()];
        result.copyInto(strArr);
        return strArr;
    }

    public static final String joinComma(String[] strArr) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(strArr[i]);
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final int nextRandom() {
        return SessionState.getRandom().nextInt();
    }

    public static final String getVectorString(Vector vector, int i) {
        return (String) vector.elementAt(i);
    }

    public static final String defaultStr(String str) {
        return str != null ? str : AppState.emptyStr;
    }

    public static final String formatSize(int i) {
        int i2 = SIZE_UNIT_BYTES;
        int i3 = 0;
        for (int i4 = 0; i4 < 2 && i > 1024; i4++) {
            i3 = i % 1024;
            i /= 1024;
            i2++;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        sb.append(i);
        if (i3 != 0 && i2 == SIZE_UNIT_MB) {
            sb.append('.');
            String fracStr = StringUtils.intern(Integer.toString(i3));
            String frac = fracStr;
            if (fracStr.length() > 2) {
                frac = StringUtils.prefix(frac, 2);
            }
            if (frac.length() < 2) {
                sb.append('0');
            }
            sb.append(frac);
        }
        return ObjectPool.toStringAndRelease(sb.append(AppState.getString(i2)));
    }

    public static final Object dequeue(Vector vector) {
        synchronized (vector) {
            if (vector.size() == 0) {
                return null;
            }
            Object elem = vector.elementAt(0);
            vector.removeElementAt(0);
            trimIfEmpty(vector);
            return elem;
        }
    }

    public static final void removeFrom(Vector vector, Object obj) {
        vector.removeElement(obj);
        trimIfEmpty(vector);
    }

    public static final void trimIfEmpty(Vector vector) {
        if (vector.size() == 0) {
            vector.trimToSize();
        }
    }

    public static final String appendSpace(String str) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(str).append(' '));
    }

    public static final String defaultIfBlank(String str, String str2) {
        for (int length = str.length() - 1; length >= 0; length--) {
            if (str.charAt(length) != ' ') {
                return str;
            }
        }
        return str2;
    }

    public static final String removeChar(String str, char c) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            if (ch != c) {
                sb.append(ch);
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final String formatPhone(String str) {
        if (str == null) {
            return AppState.emptyStr;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (startsWithInt(str, 99897)) {
            for (int i = 0; i < str.length(); i++) {
                if (i == 0) {
                    sb.append('(').append('+');
                } else if (i == 3 || i == 8) {
                    sb.append(' ');
                } else if (i == 5) {
                    sb.append(')');
                }
                sb.append(str.charAt(i));
            }
        } else {
            int i2 = startsWithInt(str, 380) ? 1 : 0;
            for (int i3 = 0; i3 < str.length(); i3++) {
                if (i3 == 0) {
                    sb.append('+');
                } else if (i3 == i2 + 1) {
                    sb.append('(');
                } else if (i3 == i2 + 4) {
                    sb.append(')');
                } else if (i3 == i2 + 7) {
                    sb.append('-');
                }
                sb.append(str.charAt(i3));
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    private static boolean startsWithInt(String str, int i) {
        return str.startsWith(Integer.toString(i));
    }

    public static final String extractDigits(String str) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    sb.append(ch);
                }
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static final String trim(String str) {
        while (str.length() > 0 && str.charAt(0) == ' ') {
            str = StringUtils.suffix(str, 1);
        }
        for (int idx = str.length() - 1; idx >= 0 && str.charAt(idx) == ' '; idx--) {
            str = StringUtils.prefix(str, idx);
        }
        return str;
    }

    public static final String trimAll(String str) {
        while (str.length() > 0 && (str.charAt(0) & 65535) <= 32) {
            str = StringUtils.suffix(str, 1);
        }
        for (int idx = str.length() - 1; idx >= 0 && (str.charAt(idx) & 65535) <= 32; idx--) {
            str = StringUtils.prefix(str, idx);
        }
        return str;
    }

    public static final boolean nonEmpty(String str) {
        return str != null && str.length() > 0;
    }

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

    public static final short[] readShortArray(int i) {
        byte[] bytes = AppState.getBytes(i);
        int length = bytes.length >> 1;
        short[] sArr = new short[length];
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            int i4 = i3;
            i3++;
            int i5 = i2;
            int i6 = i2 + 1;
            i2 = i6 + 1;
            sArr[i4] = (short) ((bytes[i5] << 8) | (bytes[i6] & 255));
        }
        ObjectPool.releaseBytes(bytes);
        return sArr;
    }

    public static final String beforeAt(String str) {
        int idx = str.indexOf(64);
        return idx >= 0 ? StringUtils.prefix(str, idx) : str;
    }

    public static final String normalizeSpaces(String str) {
        int length;
        if (str == null || (length = str.length()) == 0) {
            return str;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        char c = 0;
        int i = 0;
        while (i < length) {
            char ch = str.charAt(i);
            char c2 = ch;
            switch (ch) {
                case '\t':
                    c2 = ' ';
                case ' ':
                    if (c == ' ') {
                        break;
                    }
                default:
                    sb.append(c2);
                    break;
            }
            i++;
            c = c2;
        }
        return ObjectPool.toStringAndRelease(sb);
    }

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

    public static final int vectorSize(Vector vector) {
        if (vector == null) {
            return 0;
        }
        return vector.size();
    }

    public static final String splitAndGet(int i, int i2) {
        Vector parts = splitImpl(AppState.getString(i), (char) 0, false);
        String str = (String) parts.elementAt(i2);
        ObjectPool.releaseVector(parts);
        return str;
    }

    public static Vector wrapText(String str, Font font, int i) {
        Vector result = ObjectPool.newVector();
        StringBuffer sb = ObjectPool.newStringBuffer();
        int i2 = 0;
        int idx = str.indexOf(32);
        int length = idx;
        if (idx < 0) {
            length = str.length();
        }
        int i3 = 0;
        int spaceWidth = font.stringWidth(ResourceAccessor.str(StringResKeys.STR_SPACE));
        while (length != -1) {
            String word = StringUtils.substring(str, i2, length);
            int wordWidth = font.stringWidth(word);
            i3 += wordWidth;
            if (sb.length() > 0) {
                i3 += spaceWidth;
            }
            if (sb.length() <= 0 || i3 <= i) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(word);
            } else {
                result.addElement(StringUtils.extractBuffer(sb));
                sb.append(word);
                i3 = wordWidth;
            }
            if (length == str.length()) {
                break;
            }
            i2 = length + 1;
            int nextSpace = str.indexOf(32, i2);
            length = nextSpace;
            if (nextSpace < 0) {
                length = str.length();
            }
        }
        if (sb.length() > 0) {
            result.addElement(StringUtils.extractBuffer(sb));
        }
        return result;
    }

    public static final String generateRandomHash() {
        return new ByteBuffer().writeIntLE(nextRandom()).writeLong(System.currentTimeMillis()).encryptMD5().toHexString();
    }

    public static final StringBuffer getMessageBuffer() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        String prefix = defaultStr(UIState.getStatusText());
        StringBuffer result = sb.append(prefix);
        int length = prefix.length();
        if (length != 0 && prefix.charAt(length - 1) != ' ') {
            result.append(' ');
        }
        return result;
    }
}
