package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Conversation;

import javax.microedition.lcdui.Font;
import java.util.Vector;

public abstract class Utils {

    private static final int WIN1251_CYRILLIC_START = 192;
    private static final int WIN1251_TO_UNICODE_OFFSET = 848;
    private static final int WIN1251_YO_UPPER = 168;
    private static final int WIN1251_YO_LOWER = 184;
    private static final int UNICODE_YO_UPPER = 1025;
    private static final int UNICODE_YO_LOWER = 1105;
    private static final int UNICODE_CYRILLIC_A = 1040;
    private static final int UNICODE_CYRILLIC_YA = 1103;

    private static final int SIZE_UNIT_BYTES = 752;
    private static final int SIZE_UNIT_MB = 754;

    private static final long ONE_WEEK_MS = 604800000L;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final long MS_PER_SECOND = 1000L;

    private static final int PHONE_SLOTS = 3;
    private static final int MERGE_MAX_PARTS = 5;
    private static final int UZ_COUNTRY_CODE = 99897;
    private static final int UA_COUNTRY_CODE = 380;

    public static String getFreeMemoryString() {
        return StringUtils.intern(Long.toString(Runtime.getRuntime().freeMemory()));
    }

    public static void arraycopy(Object src, int srcPos, Object dst, int dstPos, int length) {
        if (length > 0) {
            System.arraycopy(src, srcPos, dst, dstPos, length);
        }
    }

    public static long parseDateTime(String dateTimeStr) {
        Vector parts = splitImpl(dateTimeStr, ' ', false);
        int day = parseInt(parts.elementAt(1));
        int monthIndex = StringPool.get(PackedStringKeys.MONTH_ABBREV_TABLE).indexOf(getVectorString(parts, 2)) / 3;
        int year = parseInt(parts.elementAt(3));
        String timeStr = getVectorString(parts, 4);
        ObjectPool.releaseVector(parts);

        Vector timeParts = splitImpl(timeStr, ':', false);
        int hours = parseInt(timeParts.elementAt(0));
        int minutes = parseInt(timeParts.elementAt(1));
        parseInt(timeParts.elementAt(2));
        ObjectPool.releaseVector(timeParts);

        int febDays = (year % 4 != 0 || year == 2000) ? 28 : 29;
        int totalDays = (((year - 1970) * 365) + ((year - 1968) / 4)) + day + 28 - febDays;
        if (year >= 2000) {
            totalDays--;
        }
        for (int month = monthIndex - 2; month >= 0; month--) {
            totalDays += month == 1 ? febDays : AppState.getBytes(StringResKeys.RES_MONTH_DAYS)[month];
        }
        return (MS_PER_SECOND * ((SECONDS_PER_DAY * totalDays) + (hours * SECONDS_PER_HOUR) + (minutes * SECONDS_PER_MINUTE)))
            - ((SettingsState.getTimezoneOffset() - 13) * (SECONDS_PER_HOUR * MS_PER_SECOND));
    }

    private static StringBuffer appendKey(StringBuffer buffer, int key) {
        return buffer.append(AppState.getString(key));
    }

    public static StringBuffer appendParam(StringBuffer buffer, int key, String value) {
        return appendKey(buffer, key).append('=').append(Conversation.urlEncodeCyrillic((Object) defaultStr(value)));
    }

    public static StringBuffer appendIntParam(StringBuffer buffer, int key, int value) {
        return appendKey(buffer, key).append('=').append(value);
    }

    public static String withComma(String text) {
        return ObjectPool.toStringAndRelease(appendCommaIf(ObjectPool.newStringBuffer().append(text), true));
    }

    public static StringBuffer appendCommaIf(StringBuffer buffer, boolean append) {
        if (append) {
            buffer.append(',').append(' ');
        }
        return buffer;
    }

    public static StringBuffer appendColon(StringBuffer buffer) {
        return buffer.append(':').append(' ');
    }

    public static boolean isDigitOrSep(char ch) {
        return (ch >= '0' && ch <= '9') || ch == '.' || ch == ':';
    }

    public static char win1251ToChar(int byteValue) {
        int unsigned = byteValue & 0xFF;
        if (unsigned >= WIN1251_CYRILLIC_START) {
            return (char) (unsigned + WIN1251_TO_UNICODE_OFFSET);
        }
        if (unsigned == WIN1251_YO_UPPER) {
            return (char) UNICODE_YO_UPPER;
        }
        if (unsigned == WIN1251_YO_LOWER) {
            return (char) UNICODE_YO_LOWER;
        }
        return (char) unsigned;
    }

    public static byte charToWin1251(char ch) {
        if (ch >= UNICODE_CYRILLIC_A && ch <= UNICODE_CYRILLIC_YA) {
            return (byte) (ch - WIN1251_TO_UNICODE_OFFSET);
        }
        if (ch == UNICODE_YO_UPPER) {
            return (byte) (WIN1251_YO_UPPER - 256);
        }
        if (ch == UNICODE_YO_LOWER) {
            return (byte) (WIN1251_YO_LOWER - 256);
        }
        return (byte) ch;
    }

    public static String zeroPad(int value) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (value < 10) {
            sb.append('0');
        }
        return ObjectPool.toStringAndRelease(sb.append(value));
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static int abs(int value) {
        return value >= 0 ? value : -value;
    }

    public static long absLong(long value) {
        return value >= 0 ? value : -value;
    }

    public static String maskPassword(String password) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int idx = password.length() - 1; idx >= 0; idx--) {
            sb.append('*');
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static String quoteText(String text) {
        StringBuffer sb = ObjectPool.newStringBuffer().append('>');
        int length = text == null ? 0 : text.length();
        for (int idx = 0; idx < length; idx++) {
            char ch = text.charAt(idx);
            if (ch == '\n') {
                sb.append('\n').append('>');
            } else {
                sb.append(ch);
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static void swapElements(Vector vector, int idx1, int idx2) {
        Object elem = vector.elementAt(idx1);
        vector.setElementAt(vector.elementAt(idx2), idx1);
        vector.setElementAt(elem, idx2);
    }

    public static boolean compareBytes(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) {
        for (int idx = length - 1; idx >= 0; idx--) {
            if (src[srcOffset + idx] != dst[dstOffset + idx]) {
                return false;
            }
        }
        return true;
    }

    public static int parseInt(Object value) {
        try {
            return Integer.parseInt((String) value);
        } catch (Throwable unused) {
            return 0;
        }
    }

    public static int parseIntBounded(String text, int min, int max, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(text);
        } catch (Throwable unused) {
        }
        return (result < min || result > max) ? defaultValue : result;
    }

    public static Vector splitByNull(String text) {
        return splitImpl(text, (char) 0, false);
    }

    public static Vector splitReplace(String text, char splitChar, char replaceChar) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = text == null ? 0 : text.length();
        for (int idx = 0; idx < length; idx++) {
            char ch = text.charAt(idx);
            sb.append(ch == replaceChar ? splitChar : ch);
        }
        return splitImpl(ObjectPool.toStringAndRelease(sb), splitChar, false);
    }

    public static Vector splitMerge(String text) {
        Vector parts = splitImpl(text, '|', true);
        while (parts.size() > MERGE_MAX_PARTS) {
            parts.setElementAt(ObjectPool.toStringAndRelease(
                ObjectPool.newStringBuffer()
                    .append(getVectorString(parts, 4))
                    .append('|')
                    .append(parts.elementAt(5))), 4);
            parts.removeElementAt(5);
        }
        return parts;
    }

    public static Vector split(String text, char delimiter) {
        return splitImpl(text, delimiter, true);
    }

    public static Vector splitNonEmpty(String text, char delimiter) {
        return splitImpl(text, delimiter, false);
    }

    private static Vector splitImpl(String text, char delimiter, boolean includeEmpty) {
        Vector result = ObjectPool.newVector();
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = text == null ? 0 : text.length();
        for (int idx = 0; idx <= length; idx++) {
            char ch = idx < length ? text.charAt(idx) : delimiter;
            if (ch != delimiter) {
                sb.append(ch);
            } else if (includeEmpty || sb.length() > 0) {
                result.addElement(ObjectPool.toString(sb, false));
            }
        }
        ObjectPool.toStringAndRelease(sb);
        return result;
    }

    public static String[] getPhoneNumbers(boolean includePlus) {
        Vector result = ObjectPool.newVector();
        for (int slot = 0; slot < PHONE_SLOTS; slot++) {
            StringBuffer sb = ObjectPool.newStringBuffer();
            String rawPhone = defaultStr(UIState.getContactNamePart(slot));
            int length = rawPhone.length();
            for (int charIdx = 0; charIdx < length; charIdx++) {
                char ch = rawPhone.charAt(charIdx);
                if ((ch >= '0' && ch <= '9') || (includePlus && ch == '+')) {
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

    public static String joinComma(String[] items) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int idx = 0; idx < items.length; idx++) {
            if (idx > 0) {
                sb.append(',');
            }
            sb.append(items[idx]);
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static int nextRandom() {
        return SessionState.getRandom().nextInt();
    }

    public static String getVectorString(Vector vector, int index) {
        return (String) vector.elementAt(index);
    }

    public static String defaultStr(String value) {
        return value != null ? value : AppState.emptyStr;
    }

    public static String formatSize(int sizeBytes) {
        int unitKey = SIZE_UNIT_BYTES;
        int fraction = 0;
        for (int step = 0; step < 2 && sizeBytes > 1024; step++) {
            fraction = sizeBytes % 1024;
            sizeBytes /= 1024;
            unitKey++;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        sb.append(sizeBytes);
        if (fraction != 0 && unitKey == SIZE_UNIT_MB) {
            sb.append('.');
            String fracStr = StringUtils.intern(Integer.toString(fraction));
            if (fracStr.length() > 2) {
                fracStr = StringUtils.prefix(fracStr, 2);
            }
            if (fracStr.length() < 2) {
                sb.append('0');
            }
            sb.append(fracStr);
        }
        return ObjectPool.toStringAndRelease(sb.append(AppState.getString(unitKey)));
    }

    public static Object dequeue(Vector vector) {
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

    public static void removeFrom(Vector vector, Object element) {
        vector.removeElement(element);
        trimIfEmpty(vector);
    }

    public static void trimIfEmpty(Vector vector) {
        if (vector.size() == 0) {
            vector.trimToSize();
        }
    }

    public static String appendSpace(String text) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(text).append(' '));
    }

    public static String defaultIfBlank(String text, String fallback) {
        for (int idx = text.length() - 1; idx >= 0; idx--) {
            if (text.charAt(idx) != ' ') {
                return text;
            }
        }
        return fallback;
    }

    public static String removeChar(String text, char target) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int length = text.length();
        for (int idx = 0; idx < length; idx++) {
            char ch = text.charAt(idx);
            if (ch != target) {
                sb.append(ch);
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static String formatPhone(String phone) {
        if (phone == null) {
            return AppState.emptyStr;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (startsWithInt(phone, UZ_COUNTRY_CODE)) {
            for (int idx = 0; idx < phone.length(); idx++) {
                if (idx == 0) {
                    sb.append('(').append('+');
                } else if (idx == 3 || idx == 8) {
                    sb.append(' ');
                } else if (idx == 5) {
                    sb.append(')');
                }
                sb.append(phone.charAt(idx));
            }
        } else {
            int offset = startsWithInt(phone, UA_COUNTRY_CODE) ? 1 : 0;
            for (int idx = 0; idx < phone.length(); idx++) {
                if (idx == 0) {
                    sb.append('+');
                } else if (idx == offset + 1) {
                    sb.append('(');
                } else if (idx == offset + 4) {
                    sb.append(')');
                } else if (idx == offset + 7) {
                    sb.append('-');
                }
                sb.append(phone.charAt(idx));
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    private static boolean startsWithInt(String text, int prefix) {
        return text.startsWith(Integer.toString(prefix));
    }

    public static String extractDigits(String text) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (text != null) {
            for (int idx = 0; idx < text.length(); idx++) {
                char ch = text.charAt(idx);
                if (ch >= '0' && ch <= '9') {
                    sb.append(ch);
                }
            }
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static String trim(String text) {
        while (text.length() > 0 && text.charAt(0) == ' ') {
            text = StringUtils.suffix(text, 1);
        }
        for (int idx = text.length() - 1; idx >= 0 && text.charAt(idx) == ' '; idx--) {
            text = StringUtils.prefix(text, idx);
        }
        return text;
    }

    public static String trimAll(String text) {
        while (text.length() > 0 && text.charAt(0) <= ' ') {
            text = StringUtils.suffix(text, 1);
        }
        for (int idx = text.length() - 1; idx >= 0 && text.charAt(idx) <= ' '; idx--) {
            text = StringUtils.prefix(text, idx);
        }
        return text;
    }

    public static boolean nonEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public static int[] bytesToInts(byte[] bytes) {
        int intCount = bytes.length >> 2;
        int[] result = new int[intCount];
        for (int intIdx = 0; intIdx < intCount; intIdx++) {
            int byteIdx = intIdx << 2;
            result[intIdx] = ((bytes[byteIdx] & 0xFF) << 24)
                | ((bytes[byteIdx + 1] & 0xFF) << 16)
                | ((bytes[byteIdx + 2] & 0xFF) << 8)
                | (bytes[byteIdx + 3] & 0xFF);
        }
        return result;
    }

    public static short[] readShortArray(int key) {
        byte[] bytes = AppState.getBytes(key);
        int shortCount = bytes.length >> 1;
        short[] result = new short[shortCount];
        for (int idx = 0; idx < shortCount; idx++) {
            int byteIdx = idx << 1;
            result[idx] = (short) ((bytes[byteIdx] << 8) | (bytes[byteIdx + 1] & 0xFF));
        }
        ObjectPool.releaseBytes(bytes);
        return result;
    }

    public static String beforeAt(String text) {
        int atIdx = text.indexOf('@');
        return atIdx >= 0 ? StringUtils.prefix(text, atIdx) : text;
    }

    public static String normalizeSpaces(String text) {
        if (text == null || text.length() == 0) {
            return text;
        }
        StringBuffer sb = ObjectPool.newStringBuffer();
        char prevChar = 0;
        for (int idx = 0; idx < text.length(); idx++) {
            char ch = text.charAt(idx);
            if (ch == '\t') {
                ch = ' ';
            }
            if (ch != ' ' || prevChar != ' ') {
                sb.append(ch);
            }
            prevChar = ch;
        }
        return ObjectPool.toStringAndRelease(sb);
    }

    public static int pluralForm(int count) {
        int lastDigit = count % 10;
        if (lastDigit == 1 && count % 100 != 11) {
            return 0;
        }
        if (lastDigit > 4 || lastDigit == 0) {
            return 1;
        }
        return (count <= 10 || count >= 20) ? 2 : 1;
    }

    public static int vectorSize(Vector vector) {
        if (vector == null) {
            return 0;
        }
        return vector.size();
    }

    public static String splitAndGet(int key, int index) {
        Vector parts = splitImpl(AppState.getString(key), (char) 0, false);
        String value = (String) parts.elementAt(index);
        ObjectPool.releaseVector(parts);
        return value;
    }

    public static Vector wrapText(String text, Font font, int maxWidth) {
        Vector result = ObjectPool.newVector();
        StringBuffer sb = ObjectPool.newStringBuffer();
        int wordStart = 0;
        int nextSpace = text.indexOf(' ');
        int wordEnd = nextSpace < 0 ? text.length() : nextSpace;
        int lineWidth = 0;
        int spaceWidth = font.stringWidth(StringPool.get(StringResKeys.STR_SPACE));
        while (wordEnd != -1) {
            String word = StringUtils.substring(text, wordStart, wordEnd);
            int wordWidth = font.stringWidth(word);
            lineWidth += wordWidth;
            if (sb.length() > 0) {
                lineWidth += spaceWidth;
            }
            if (sb.length() <= 0 || lineWidth <= maxWidth) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(word);
            } else {
                result.addElement(StringUtils.extractBuffer(sb));
                sb.append(word);
                lineWidth = wordWidth;
            }
            if (wordEnd == text.length()) {
                break;
            }
            wordStart = wordEnd + 1;
            nextSpace = text.indexOf(' ', wordStart);
            wordEnd = nextSpace < 0 ? text.length() : nextSpace;
        }
        if (sb.length() > 0) {
            result.addElement(StringUtils.extractBuffer(sb));
        }
        return result;
    }

    public static String generateRandomHash() {
        return new ByteBuffer().writeIntLE(nextRandom()).writeLong(System.currentTimeMillis()).encryptMD5().toHexString();
    }

    public static StringBuffer getMessageBuffer() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        String prefix = defaultStr(UIState.getStatusText());
        sb.append(prefix);
        int length = prefix.length();
        if (length != 0 && prefix.charAt(length - 1) != ' ') {
            sb.append(' ');
        }
        return sb;
    }
}
