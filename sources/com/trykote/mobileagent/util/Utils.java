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
        Vector parts = splitImpl(str, ' ', false);
        int day = parseInt(parts.elementAt(1));
        int idx = AppState.getString(PackedStringKeys.MONTH_ABBREV_TABLE).indexOf(getVectorString(parts, 2)) / 3;
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
        int i2 = idx - 1;
        while (true) {
            i2--;
            if (i2 < 0) {
                return (1000 * (((86400 * i) + (hours * 3600)) + (minutes * 60))) - ((AppState.getInt(SettingsKeys.SETTING_TIMEZONE_OFFSET) - 13) * 3600000);
            }
            i += i2 == 1 ? b : AppState.getBytes(StringResKeys.RES_MONTH_DAYS)[i2];
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
        return ObjectPool.toStringAndRelease(appendCommaIf(ObjectPool.newStringBuffer().append(str), true));
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
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (i < 10) {
            sb.append('0');
        }
        return ObjectPool.toStringAndRelease(sb.append(i));
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
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str.length();
        while (true) {
            length--;
            if (length < 0) {
                return ObjectPool.toStringAndRelease(sb);
            }
            sb.append('*');
        }
    }

    /* renamed from: d */
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

    /* renamed from: a */
    public static final void swapElements(Vector vector, int i, int i2) {
        Object elem = vector.elementAt(i);
        vector.setElementAt(vector.elementAt(i2), i);
        vector.setElementAt(elem, i2);
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
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = str == null ? 0 : str.length();
        for (int i = 0; i < length; i++) {
            char ch = str.charAt(i);
            sb.append(ch == c2 ? c : ch);
        }
        return splitImpl(ObjectPool.toStringAndRelease(sb), c, false);
    }

    /* renamed from: a */
    public static final Vector splitMerge(String str, char c) {
        Vector parts = splitImpl(str, '|', true);
        while (parts.size() > 5) {
            parts.setElementAt(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(getVectorString(parts, 4)).append('|').append(parts.elementAt(5))), 4);
            parts.removeElementAt(5);
        }
        return parts;
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

    /* renamed from: a */
    public static final String[] getPhoneNumbers(boolean z) {
        Vector result = ObjectPool.newVector();
        for (int i = 0; i < 3; i++) {
            StringBuffer sb = ObjectPool.newStringBuffer();
            String rawPhone = defaultStr(AppState.getString(i + 1303));
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

    /* renamed from: a */
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

    /* renamed from: a */
    public static final int nextRandom() {
        return ((Random) AppState.pool[SessionKeys.OBJ_RANDOM]).nextInt();
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
        StringBuffer sb = ObjectPool.newStringBuffer();
        sb.append(i);
        if (i3 != 0 && i2 == 754) {
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

    /* renamed from: a */
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
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(str).append(' '));
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

    /* renamed from: h */
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

    /* renamed from: a */
    private static boolean startsWithInt(String str, int i) {
        return str.startsWith(Integer.toString(i));
    }

    /* renamed from: i */
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

    /* renamed from: m */
    public static final String beforeAt(String str) {
        int idx = str.indexOf(64);
        return idx >= 0 ? StringUtils.prefix(str, idx) : str;
    }

    /* renamed from: n */
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
        Vector parts = splitImpl(AppState.getString(i), (char) 0, false);
        String str = (String) parts.elementAt(i2);
        ObjectPool.releaseVector(parts);
        return str;
    }

    /* renamed from: a */
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
        int spaceWidth = font.stringWidth(AppState.getString(StringResKeys.STR_SPACE));
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

    /* renamed from: b */
    public static final String generateRandomHash() {
        return new ByteBuffer().writeIntLE(nextRandom()).writeLong(System.currentTimeMillis()).encryptMD5().toHexString();
    }

    /* renamed from: e */
    public static final StringBuffer getMessageBuffer() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        String prefix = defaultStr(AppState.getString(UIKeys.SLOT_STATUS_TEXT));
        StringBuffer result = sb.append(prefix);
        int length = prefix.length();
        if (length != 0 && prefix.charAt(length - 1) != ' ') {
            result.append(' ');
        }
        return result;
    }
}
