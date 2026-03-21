package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: aq */
/* loaded from: MobileAgent_3.9.jar:aq.class */
public abstract class JsonParser {
    /* renamed from: a */
    public static final Object m466a(ByteBuffer c0043n, int i) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        Object objM469a = m469a(c0043n, stringBufferM1217h, 2);
        NetworkUtils.m1215a(stringBufferM1217h);
        return objM469a;
    }

    /* renamed from: a */
    public static final Object m467a(ByteBuffer c0043n) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        Object objM469a = m469a(c0043n, stringBufferM1217h, 1);
        NetworkUtils.m1215a(stringBufferM1217h);
        return objM469a;
    }

    /* renamed from: a */
    private static final char m468a(ByteBuffer c0043n, StringBuffer stringBuffer) {
        while (true) {
            char cM472c = m472c(c0043n, stringBuffer);
            if (cM472c != ' ' && cM472c != '\n' && cM472c != '\r') {
                return cM472c;
            }
        }
    }

    /* renamed from: a */
    private static final Object m469a(ByteBuffer c0043n, StringBuffer stringBuffer, int i) {
        char cM468a;
        char cM468a2;
        char cM468a3 = m468a(c0043n, stringBuffer);
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
                Object objM469a = m469a(c0043n, stringBuffer, i);
                if (!(objM469a instanceof String)) {
                    throw new RuntimeException();
                }
                if (m468a(c0043n, stringBuffer) != ':') {
                    throw new RuntimeException();
                }
                hashtable.put(objM469a, m469a(c0043n, stringBuffer, i));
                cM468a2 = m468a(c0043n, stringBuffer);
                if (cM468a2 == '}') {
                    return hashtable;
                }
            } while (cM468a2 == ',');
            throw new RuntimeException();
        }
        if (cM468a3 != '[') {
            if (cM468a3 == '\"') {
                return m470b(c0043n, stringBuffer, i);
            }
            stringBuffer.append(cM468a3);
            return m471b(c0043n, stringBuffer);
        }
        Vector vectorM1213g = NetworkUtils.m1213g();
        do {
            Object objM469a2 = m469a(c0043n, stringBuffer, i);
            if (objM469a2 != null) {
                vectorM1213g.addElement(objM469a2);
            }
            cM468a = m468a(c0043n, stringBuffer);
            if (cM468a == ']') {
                return vectorM1213g;
            }
        } while (cM468a == ',');
        throw new RuntimeException();
    }

    /* renamed from: b */
    private static final String m470b(ByteBuffer c0043n, StringBuffer stringBuffer, int i) {
        char cM499a;
        char cM1342n;
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
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
                cM499a = Utils.m499a((int) m472c(c0043n, stringBuffer));
            }
            if (cM499a == '\"') {
                return NetworkUtils.m1215a(stringBufferM1217h);
            }
            if (cM499a == '\\') {
                switch (m472c(c0043n, stringBuffer)) {
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
    private static final Object m471b(ByteBuffer c0043n, StringBuffer stringBuffer) {
        char cM472c;
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        while (true) {
            cM472c = m472c(c0043n, stringBuffer);
            if (cM472c == ',' || cM472c == '}' || cM472c == ']') {
                break;
            }
            stringBufferM1217h.append(cM472c);
        }
        stringBuffer.append(cM472c);
        String strM1215a = NetworkUtils.m1215a(stringBufferM1217h);
        return StringUtils.m3a(264068, strM1215a) ? ResourceManager.f292k : StringUtils.m3a(329608, strM1215a) ? ResourceManager.f293l : StringUtils.m3a(1369, strM1215a) ? ResourceManager.f290i : ResourceManager.m967e(Utils.m510a((Object) strM1215a));
    }

    /* renamed from: c */
    private static final char m472c(ByteBuffer c0043n, StringBuffer stringBuffer) {
        int length = stringBuffer.length() - 1;
        if (length < 0) {
            return (char) c0043n.readByteOrEOF();
        }
        char cCharAt = stringBuffer.charAt(length);
        stringBuffer.setLength(length);
        return cCharAt;
    }

    /* renamed from: a */
    public static final void m473a(Hashtable hashtable, int i, Object obj) {
        hashtable.put(AppState.m584b(i), obj);
    }

    /* renamed from: a */
    public static final void m474a(Hashtable hashtable, String str, int i) {
        hashtable.put(str, ResourceManager.m967e(i));
    }

    /* renamed from: a */
    public static final Object m475a(Object obj, String str) {
        return ((Hashtable) obj).get(str);
    }

    /* renamed from: a */
    public static final Object m476a(Object obj, int i) {
        return ((Hashtable) obj).get(AppState.m584b(i));
    }

    /* renamed from: b */
    public static final int m477b(Object obj, String str) {
        return ((Integer) m475a(obj, str)).intValue();
    }

    /* renamed from: b */
    public static final int m478b(Object obj, int i) {
        return ((Integer) m475a(obj, AppState.m584b(i))).intValue();
    }

    /* renamed from: c */
    public static final String m479c(Object obj, String str) {
        return (String) m475a(obj, str);
    }

    /* renamed from: c */
    public static final String m480c(Object obj, int i) {
        return (String) m475a(obj, AppState.m584b(i));
    }

    /* renamed from: d */
    public static final void m481d(Object obj, int i) {
        ((Vector) obj).addElement(ResourceManager.m967e(i));
    }

    /* renamed from: e */
    public static final Object m482e(Object obj, int i) {
        return ((Vector) obj).elementAt(i);
    }

    /* renamed from: f */
    public static final String m483f(Object obj, int i) {
        return Utils.m521a((Vector) obj, i);
    }

    /* renamed from: a */
    public static final String m484a(Object obj) {
        return NetworkUtils.m1215a(m485a(obj, NetworkUtils.m1217h()));
    }

    /* renamed from: a */
    private static final StringBuffer m485a(Object obj, StringBuffer stringBuffer) {
        if (obj == null || obj == ResourceManager.f290i) {
            stringBuffer.append(AppState.m584b(1369));
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
                m485a(vector.elementAt(i2), stringBuffer);
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
                m485a(objNextElement, stringBuffer);
                stringBuffer.append(':');
                m485a(hashtable.get(objNextElement), stringBuffer);
            }
            stringBuffer.append('}');
        }
        return stringBuffer;
    }

    /* renamed from: b */
    public static final boolean m486b(Object obj) {
        return StringUtils.m3a(133005, m483f(obj, 1)) && StringUtils.m3a(788024, (String) m482e(obj, 0));
    }
}
