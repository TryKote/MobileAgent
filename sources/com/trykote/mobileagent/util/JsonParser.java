package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: aq */
/* loaded from: MobileAgent_3.9.jar:aq.class */
public abstract class JsonParser {
    /* renamed from: a */
    public static final Object parseUTF8(ByteBuffer buffer, int i) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        Object result = parseValue(buffer, sb, 2);
        ObjectPool.toStringAndRelease(sb);
        return result;
    }

    /* renamed from: a */
    public static final Object parseJson(ByteBuffer buffer) {
        StringBuffer sb = ObjectPool.newStringBuffer();
        Object result = parseValue(buffer, sb, 1);
        ObjectPool.toStringAndRelease(sb);
        return result;
    }

    /* renamed from: a */
    private static final char skipWhitespace(ByteBuffer buffer, StringBuffer stringBuffer) {
        while (true) {
            char next = nextJsonChar(buffer, stringBuffer);
            if (next != ' ' && next != '\n' && next != '\r') {
                return next;
            }
        }
    }

    /* renamed from: a */
    private static final Object parseValue(ByteBuffer buffer, StringBuffer stringBuffer, int i) {
        char sep;
        char delim;
        char firstChar = skipWhitespace(buffer, stringBuffer);
        if (firstChar == 65535) {
            throw new RuntimeException();
        }
        if (firstChar == '}' || firstChar == ']' || firstChar == ',') {
            stringBuffer.append(firstChar);
            return null;
        }
        if (firstChar == '{') {
            Hashtable hashtable = new Hashtable();
            do {
                Object result = parseValue(buffer, stringBuffer, i);
                if (!(result instanceof String)) {
                    throw new RuntimeException();
                }
                if (skipWhitespace(buffer, stringBuffer) != ':') {
                    throw new RuntimeException();
                }
                hashtable.put(result, parseValue(buffer, stringBuffer, i));
                delim = skipWhitespace(buffer, stringBuffer);
                if (delim == '}') {
                    return hashtable;
                }
            } while (delim == ',');
            throw new RuntimeException();
        }
        if (firstChar != '[') {
            if (firstChar == '\"') {
                return parseString(buffer, stringBuffer, i);
            }
            stringBuffer.append(firstChar);
            return parseUnquoted(buffer, stringBuffer);
        }
        Vector items = ObjectPool.newVector();
        do {
            Object value = parseValue(buffer, stringBuffer, i);
            if (value != null) {
                items.addElement(value);
            }
            sep = skipWhitespace(buffer, stringBuffer);
            if (sep == ']') {
                return items;
            }
        } while (sep == ',');
        throw new RuntimeException();
    }

    /* renamed from: b */
    private static final String parseString(ByteBuffer buffer, StringBuffer stringBuffer, int i) {
        char ch;
        char decoded;
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (true) {
            if (i == 2) {
                int length = stringBuffer.length() - 1;
                if (length >= 0) {
                    char c = stringBuffer.charAt(length);
                    stringBuffer.setLength(length);
                    decoded = c;
                } else if (i == 2) {
                    int b1 = buffer.readUByte();
                    int b2 = b1 > 127 ? buffer.readUByte() : 0;
                    int b3 = b1 > 223 ? buffer.readUByte() : 0;
                    decoded = b1 < 128 ? (char) b1 : b1 < 224 ? (char) (((b1 - 192) << 6) + (b2 - 128)) : b1 < 240 ? (char) (((b1 - 224) << 12) + ((b2 - 128) << 6) + (b3 - 128)) : (char) (((b1 - 240) << 18) + ((b2 - 128) << 12) + ((b3 - 128) << 6) + ((b1 > 239 ? buffer.readUByte() : 0) - 128));
                } else {
                    decoded = (char) buffer.readByteOrEOF();
                }
                ch = decoded;
            } else {
                ch = Utils.win1251ToChar((int) nextJsonChar(buffer, stringBuffer));
            }
            if (ch == '\"') {
                return ObjectPool.toStringAndRelease(sb);
            }
            if (ch == '\\') {
                switch (nextJsonChar(buffer, stringBuffer)) {
                    case '\"':
                        sb.append('\"');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                }
            } else {
                sb.append(ch);
            }
        }
    }

    /* renamed from: b */
    private static final Object parseUnquoted(ByteBuffer buffer, StringBuffer stringBuffer) {
        char next;
        StringBuffer sb = ObjectPool.newStringBuffer();
        while (true) {
            next = nextJsonChar(buffer, stringBuffer);
            if (next == ',' || next == '}' || next == ']') {
                break;
            }
            sb.append(next);
        }
        stringBuffer.append(next);
        String text = ObjectPool.toStringAndRelease(sb);
        return StringUtils.matchesKey(PackedStringKeys.VALUE_TRUE, text) ? ResourceManager.boolTrue : StringUtils.matchesKey(PackedStringKeys.VALUE_FALSE, text) ? ResourceManager.boolFalse : StringUtils.matchesKey(1369, text) ? ResourceManager.syncObject : ResourceManager.integerOf(Utils.parseInt((Object) text));
    }

    /* renamed from: c */
    private static final char nextJsonChar(ByteBuffer buffer, StringBuffer stringBuffer) {
        int length = stringBuffer.length() - 1;
        if (length < 0) {
            return (char) buffer.readByteOrEOF();
        }
        char c = stringBuffer.charAt(length);
        stringBuffer.setLength(length);
        return c;
    }

    /* renamed from: a */
    public static final void putIntKey(Hashtable hashtable, int i, Object obj) {
        hashtable.put(AppState.getString(i), obj);
    }

    /* renamed from: a */
    public static final void putIntValue(Hashtable hashtable, String str, int i) {
        hashtable.put(str, ResourceManager.integerOf(i));
    }

    /* renamed from: a */
    public static final Object getValue(Object obj, String str) {
        return ((Hashtable) obj).get(str);
    }

    /* renamed from: a */
    public static final Object getValueByInt(Object obj, int i) {
        return ((Hashtable) obj).get(AppState.getString(i));
    }

    /* renamed from: b */
    public static final int getIntValue(Object obj, String str) {
        return ((Integer) getValue(obj, str)).intValue();
    }

    /* renamed from: b */
    public static final int getIntByInt(Object obj, int i) {
        return ((Integer) getValue(obj, AppState.getString(i))).intValue();
    }

    /* renamed from: c */
    public static final String getStringValue(Object obj, String str) {
        return (String) getValue(obj, str);
    }

    /* renamed from: c */
    public static final String getStringByInt(Object obj, int i) {
        return (String) getValue(obj, AppState.getString(i));
    }

    /* renamed from: d */
    public static final void addIntToVector(Object obj, int i) {
        ((Vector) obj).addElement(ResourceManager.integerOf(i));
    }

    /* renamed from: e */
    public static final Object getVectorElement(Object obj, int i) {
        return ((Vector) obj).elementAt(i);
    }

    /* renamed from: f */
    public static final String getVectorString(Object obj, int i) {
        return Utils.getVectorString((Vector) obj, i);
    }

    /* renamed from: a */
    public static final String toJson(Object obj) {
        return ObjectPool.toStringAndRelease(serializeValue(obj, ObjectPool.newStringBuffer()));
    }

    /* renamed from: a */
    private static final StringBuffer serializeValue(Object obj, StringBuffer stringBuffer) {
        if (obj == null || obj == ResourceManager.syncObject) {
            stringBuffer.append(AppState.getString(StateKeys.STR_SEPARATOR));
        } else if ((obj instanceof Boolean) || (obj instanceof Integer)) {
            stringBuffer.append(obj);
        } else if (obj instanceof String) {
            stringBuffer.append('\"');
            String str = (String) obj;
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '\b':
                        stringBuffer.append('\\').append('b');
                        break;
                    case '\t':
                        stringBuffer.append('\\').append('t');
                        break;
                    case '\n':
                        stringBuffer.append('\\').append('n');
                        break;
                    case '\f':
                        stringBuffer.append('\\').append('f');
                        break;
                    case '\r':
                        stringBuffer.append('\\').append('r');
                        break;
                    case '\"':
                        stringBuffer.append('\\').append('\"');
                        break;
                    case '/':
                        stringBuffer.append('\\').append('/');
                        break;
                    default:
                        stringBuffer.append(c);
                        break;
                }
            }
            stringBuffer.append('\"');
        } else if (obj instanceof Vector) {
            stringBuffer.append('[');
            Vector vector = (Vector) obj;
            for (int i2 = 0; i2 < vector.size(); i2++) {
                if (i2 > 0) {
                    stringBuffer.append(',');
                }
                serializeValue(vector.elementAt(i2), stringBuffer);
            }
            stringBuffer.append(']');
        } else {
            stringBuffer.append('{');
            Hashtable hashtable = (Hashtable) obj;
            boolean z = true;
            Enumeration keys = hashtable.keys();
            while (keys.hasMoreElements()) {
                if (!z) {
                    stringBuffer.append(',');
                }
                z = false;
                Object key = keys.nextElement();
                serializeValue(key, stringBuffer);
                stringBuffer.append(':');
                serializeValue(hashtable.get(key), stringBuffer);
            }
            stringBuffer.append('}');
        }
        return stringBuffer;
    }

    /* renamed from: b */
    public static final boolean isSuccess(Object obj) {
        return StringUtils.matchesKey(PackedStringKeys.STATUS_OK, getVectorString(obj, 1)) && StringUtils.matchesKey(PackedStringKeys.TAG_AJAX_RESPONSE, (String) getVectorElement(obj, 0));
    }
}
