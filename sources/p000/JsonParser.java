package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: aq */
/* loaded from: MobileAgent_3.9.jar:aq.class */
public abstract class JsonParser {
    /* renamed from: a */
    public static final Object parseUTF8(ByteBuffer c0043n, int i) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        Object objM469a = parseValue(c0043n, stringBufferM1217h, 2);
        NetworkUtils.bufToStringCached(stringBufferM1217h);
        return objM469a;
    }

    /* renamed from: a */
    public static final Object parseJson(ByteBuffer c0043n) {
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        Object objM469a = parseValue(c0043n, stringBufferM1217h, 1);
        NetworkUtils.bufToStringCached(stringBufferM1217h);
        return objM469a;
    }

    /* renamed from: a */
    private static final char skipWhitespace(ByteBuffer c0043n, StringBuffer stringBuffer) {
        while (true) {
            char cM472c = nextJsonChar(c0043n, stringBuffer);
            if (cM472c != ' ' && cM472c != '\n' && cM472c != '\r') {
                return cM472c;
            }
        }
    }

    /* renamed from: a */
    private static final Object parseValue(ByteBuffer c0043n, StringBuffer stringBuffer, int i) {
        char cM468a;
        char cM468a2;
        char cM468a3 = skipWhitespace(c0043n, stringBuffer);
        if (cM468a3 == 65535) {
            throw new RuntimeException();
        }
        if (cM468a3 == '}' || cM468a3 == ']' || cM468a3 == ',') {
            stringBuffer.append(cM468a3);
            return null;
        }
        if (cM468a3 == '{') {
            Hashtable hashtable = new Hashtable();
            do {
                Object objM469a = parseValue(c0043n, stringBuffer, i);
                if (!(objM469a instanceof String)) {
                    throw new RuntimeException();
                }
                if (skipWhitespace(c0043n, stringBuffer) != ':') {
                    throw new RuntimeException();
                }
                hashtable.put(objM469a, parseValue(c0043n, stringBuffer, i));
                cM468a2 = skipWhitespace(c0043n, stringBuffer);
                if (cM468a2 == '}') {
                    return hashtable;
                }
            } while (cM468a2 == ',');
            throw new RuntimeException();
        }
        if (cM468a3 != '[') {
            if (cM468a3 == '\"') {
                return parseString(c0043n, stringBuffer, i);
            }
            stringBuffer.append(cM468a3);
            return parseUnquoted(c0043n, stringBuffer);
        }
        Vector vectorM1213g = NetworkUtils.newVector();
        do {
            Object objM469a2 = parseValue(c0043n, stringBuffer, i);
            if (objM469a2 != null) {
                vectorM1213g.addElement(objM469a2);
            }
            cM468a = skipWhitespace(c0043n, stringBuffer);
            if (cM468a == ']') {
                return vectorM1213g;
            }
        } while (cM468a == ',');
        throw new RuntimeException();
    }

    /* renamed from: b */
    private static final String parseString(ByteBuffer c0043n, StringBuffer stringBuffer, int i) {
        char cM499a;
        char cM1342n;
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        while (true) {
            if (i == 2) {
                int length = stringBuffer.length() - 1;
                if (length >= 0) {
                    char cCharAt = stringBuffer.charAt(length);
                    stringBuffer.setLength(length);
                    cM1342n = cCharAt;
                } else if (i == 2) {
                    int iM1346q = c0043n.readUByte();
                    int iM1346q2 = iM1346q > 127 ? c0043n.readUByte() : 0;
                    int iM1346q3 = iM1346q > 223 ? c0043n.readUByte() : 0;
                    cM1342n = iM1346q < 128 ? (char) iM1346q : iM1346q < 224 ? (char) (((iM1346q - 192) << 6) + (iM1346q2 - 128)) : iM1346q < 240 ? (char) (((iM1346q - 224) << 12) + ((iM1346q2 - 128) << 6) + (iM1346q3 - 128)) : (char) (((iM1346q - 240) << 18) + ((iM1346q2 - 128) << 12) + ((iM1346q3 - 128) << 6) + ((iM1346q > 239 ? c0043n.readUByte() : 0) - 128));
                } else {
                    cM1342n = (char) c0043n.readByteOrEOF();
                }
                cM499a = cM1342n;
            } else {
                cM499a = Utils.win1251ToChar((int) nextJsonChar(c0043n, stringBuffer));
            }
            if (cM499a == '\"') {
                return NetworkUtils.bufToStringCached(stringBufferM1217h);
            }
            if (cM499a == '\\') {
                switch (nextJsonChar(c0043n, stringBuffer)) {
                    case '\"':
                        stringBufferM1217h.append('\"');
                        break;
                    case '/':
                        stringBufferM1217h.append('/');
                        break;
                    case '\\':
                        stringBufferM1217h.append('\\');
                        break;
                    case 'b':
                        stringBufferM1217h.append('\b');
                        break;
                    case 'f':
                        stringBufferM1217h.append('\f');
                        break;
                    case 'n':
                        stringBufferM1217h.append('\n');
                        break;
                    case 'r':
                        stringBufferM1217h.append('\r');
                        break;
                    case 't':
                        stringBufferM1217h.append('\t');
                        break;
                }
            } else {
                stringBufferM1217h.append(cM499a);
            }
        }
    }

    /* renamed from: b */
    private static final Object parseUnquoted(ByteBuffer c0043n, StringBuffer stringBuffer) {
        char cM472c;
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        while (true) {
            cM472c = nextJsonChar(c0043n, stringBuffer);
            if (cM472c == ',' || cM472c == '}' || cM472c == ']') {
                break;
            }
            stringBufferM1217h.append(cM472c);
        }
        stringBuffer.append(cM472c);
        String strM1215a = NetworkUtils.bufToStringCached(stringBufferM1217h);
        return StringUtils.matchesKey(264068, strM1215a) ? ResourceManager.boolTrue : StringUtils.matchesKey(329608, strM1215a) ? ResourceManager.boolFalse : StringUtils.matchesKey(1369, strM1215a) ? ResourceManager.syncObject : ResourceManager.integerOf(Utils.parseInt((Object) strM1215a));
    }

    /* renamed from: c */
    private static final char nextJsonChar(ByteBuffer c0043n, StringBuffer stringBuffer) {
        int length = stringBuffer.length() - 1;
        if (length < 0) {
            return (char) c0043n.readByteOrEOF();
        }
        char cCharAt = stringBuffer.charAt(length);
        stringBuffer.setLength(length);
        return cCharAt;
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
        return NetworkUtils.bufToStringCached(serializeValue(obj, NetworkUtils.newStringBuffer()));
    }

    /* renamed from: a */
    private static final StringBuffer serializeValue(Object obj, StringBuffer stringBuffer) {
        if (obj == null || obj == ResourceManager.syncObject) {
            stringBuffer.append(AppState.getString(1369));
        } else if ((obj instanceof Boolean) || (obj instanceof Integer)) {
            stringBuffer.append(obj);
        } else if (obj instanceof String) {
            stringBuffer.append('\"');
            String str = (String) obj;
            int length = str.length();
            for (int i = 0; i < length; i++) {
                char cCharAt = str.charAt(i);
                switch (cCharAt) {
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
                        stringBuffer.append(cCharAt);
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
            Enumeration enumerationKeys = hashtable.keys();
            while (enumerationKeys.hasMoreElements()) {
                if (!z) {
                    stringBuffer.append(',');
                }
                z = false;
                Object objNextElement = enumerationKeys.nextElement();
                serializeValue(objNextElement, stringBuffer);
                stringBuffer.append(':');
                serializeValue(hashtable.get(objNextElement), stringBuffer);
            }
            stringBuffer.append('}');
        }
        return stringBuffer;
    }

    /* renamed from: b */
    public static final boolean isSuccess(Object obj) {
        return StringUtils.matchesKey(133005, getVectorString(obj, 1)) && StringUtils.matchesKey(788024, (String) getVectorElement(obj, 0));
    }
}
