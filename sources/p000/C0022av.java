package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: av */
/* loaded from: MobileAgent_3.9.jar:av.class */
public final class C0022av {

    /* renamed from: a */
    public String f171a;

    /* renamed from: b */
    public Vector f172b;

    /* renamed from: c */
    public StringBuffer f173c;

    /* renamed from: d */
    public C0022av f174d;

    /* renamed from: e */
    private Hashtable f175e;

    public C0022av() {
        this(null, null, null);
    }

    /* renamed from: a */
    public static final C0022av m550a(int i) {
        return new C0022av(AbstractC0023aw.m584b(i));
    }

    public C0022av(int i) {
        this(C0040k.m1221a(i));
    }

    private C0022av(String str) {
        this.f171a = str;
    }

    public C0022av(String str, C0022av c0022av, Hashtable hashtable) {
        this.f174d = c0022av;
        this.f175e = hashtable;
        this.f171a = str;
    }

    /* renamed from: a */
    public final C0022av m551a(int i, int i2) {
        m571a(AbstractC0023aw.m584b(i), AbstractC0023aw.m584b(i2));
        return this;
    }

    /* renamed from: a */
    public final C0022av m552a(C0022av c0022av) {
        if (this.f172b == null) {
            this.f172b = C0040k.m1213g();
        }
        this.f172b.addElement(c0022av);
        return this;
    }

    /* renamed from: a */
    public final C0022av m553a(Object obj) {
        if (this.f173c == null) {
            this.f173c = C0040k.m1217h();
        }
        this.f173c.append(obj);
        return this;
    }

    /* renamed from: b */
    public final String m554b(int i) {
        return m558d(AbstractC0023aw.m584b(i));
    }

    /* renamed from: c */
    public final String m555c(int i) {
        return m558d(C0040k.m1221a(i));
    }

    /* renamed from: d */
    public final long m556d(int i) {
        return Long.parseLong(m555c(i));
    }

    /* renamed from: e */
    public final int m557e(int i) {
        return Integer.parseInt(m555c(i));
    }

    /* renamed from: d */
    private String m558d(String str) {
        return (String) this.f175e.get(str);
    }

    /* renamed from: a */
    public final C0022av m559a(int i, String str) {
        return m561b(AbstractC0023aw.m584b(i), str);
    }

    /* renamed from: b */
    public final C0022av m560b(int i, String str) {
        return m561b(C0040k.m1221a(i), str);
    }

    /* renamed from: b */
    private C0022av m561b(String str, String str2) {
        if (str != null) {
            if (str2 != null) {
                if (this.f175e == null) {
                    this.f175e = new Hashtable();
                }
                this.f175e.put(str, str2);
            } else if (this.f175e != null) {
                this.f175e.remove(str);
            }
        }
        return this;
    }

    /* renamed from: f */
    public final C0022av m562f(int i) {
        return m563e(AbstractC0023aw.m584b(i));
    }

    /* renamed from: e */
    private final C0022av m563e(String str) {
        C0022av c0022av;
        int iM541c = AbstractC0019as.m541c(this.f172b);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            c0022av = (C0022av) this.f172b.elementAt(iM541c);
        } while (!C0000a.m6a(c0022av.f171a, str));
        return c0022av;
    }

    /* renamed from: g */
    public final C0022av m564g(int i) {
        return (C0022av) this.f172b.elementAt(i);
    }

    public final String toString() {
        StringBuffer stringBufferAppend = C0040k.m1217h().append('<').append(this.f171a);
        if (this.f175e != null) {
            Enumeration enumerationKeys = this.f175e.keys();
            while (enumerationKeys.hasMoreElements()) {
                StringBuffer stringBufferAppend2 = stringBufferAppend.append(' ');
                Object objNextElement = enumerationKeys.nextElement();
                StringBuffer stringBufferAppend3 = stringBufferAppend2.append(objNextElement).append('=').append('\"');
                StringBuffer stringBufferM565b = m565b(this.f175e.get(objNextElement));
                stringBufferAppend3.append((Object) stringBufferM565b).append('\"');
                C0040k.m1215a(stringBufferM565b);
            }
        }
        StringBuffer stringBufferAppend4 = C0040k.m1217h().append(C0040k.m1215a(m566c() ? stringBufferAppend.append('/').append('>') : stringBufferAppend.append('>')));
        if (this.f173c != null) {
            stringBufferAppend4.append((Object) m565b(this.f173c));
        }
        for (int i = 0; i < AbstractC0019as.m541c(this.f172b); i++) {
            stringBufferAppend4.append(this.f172b.elementAt(i));
        }
        return C0040k.m1215a(m566c() ? stringBufferAppend4 : stringBufferAppend4.append(C0040k.m1215a(C0040k.m1217h().append('<').append('/').append(this.f171a).append('>'))));
    }

    /* renamed from: b */
    private final StringBuffer m565b(Object obj) {
        if (obj == null) {
            return null;
        }
        String string = obj.toString();
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = string.charAt(i);
            if (cCharAt == '&') {
                stringBufferM1217h.append(C0040k.m1221a(255289286950L));
            } else if (cCharAt == '\"') {
                stringBufferM1217h.append(C0040k.m1221a(65371272212774L));
            } else if (cCharAt == '<') {
                stringBufferM1217h.append(C0040k.m1221a(997485606));
            } else if (cCharAt == '>') {
                stringBufferM1217h.append(C0040k.m1221a(997484326));
            } else {
                stringBufferM1217h.append(cCharAt);
            }
        }
        return stringBufferM1217h;
    }

    /* renamed from: c */
    private final boolean m566c() {
        return this.f173c == null && this.f172b == null;
    }

    /* renamed from: a */
    public final C0022av m567a(String str) {
        C0022av c0022av;
        int iM541c = AbstractC0019as.m541c(this.f172b);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            c0022av = (C0022av) this.f172b.elementAt(iM541c);
        } while (!C0000a.m6a(str, C0000a.m11a(c0022av.f173c)));
        return c0022av;
    }

    /* renamed from: b */
    public final C0022av m568b(String str) {
        return m563e(str);
    }

    /* renamed from: h */
    public final C0022av m569h(int i) {
        return m559a(333027, AbstractC0023aw.m584b(i));
    }

    /* renamed from: i */
    public final C0022av m570i(int i) {
        return m559a(262589, AbstractC0023aw.m584b(i));
    }

    /* renamed from: a */
    public final C0022av m571a(String str, String str2) {
        C0022av c0022av = new C0022av(str);
        if (str2 != null) {
            c0022av.m553a((Object) str2);
        }
        m552a(c0022av);
        return c0022av;
    }

    /* renamed from: b */
    public final C0022av m572b(int i, int i2) {
        return m571a(AbstractC0023aw.m584b(i), (String) null).m569h(i2);
    }

    /* renamed from: c */
    public final C0022av m573c(int i, int i2) {
        return m552a(new C0022av(AbstractC0023aw.m584b(i)).m569h(i2));
    }

    /* renamed from: a */
    public final String m574a() {
        return m558d(AbstractC0023aw.m584b(262589));
    }

    /* renamed from: c */
    public final String m575c(String str) {
        try {
            return C0000a.m11a(m563e(str).f173c);
        } catch (Throwable unused) {
            return AbstractC0023aw.f181d;
        }
    }

    /* renamed from: d */
    public final C0022av m576d(int i, int i2) {
        int iM541c = AbstractC0019as.m541c(this.f172b);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            C0022av c0022avM564g = m564g(iM541c);
            if (C0000a.m3a(i, c0022avM564g.f171a) && C0000a.m3a(i2, c0022avM564g.m558d(AbstractC0023aw.m584b(333027)))) {
                return c0022avM564g;
            }
        }
    }

    /* renamed from: b */
    public final C0022av m577b() {
        return m570i(398982).m559a(131590, m558d(AbstractC0023aw.m584b(262852))).m559a(262852, (String) null);
    }
}
