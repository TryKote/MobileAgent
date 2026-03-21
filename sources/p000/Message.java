package p000;

import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: az */
/* loaded from: MobileAgent_3.9.jar:az.class */
public final class Message {

    /* renamed from: a */
    public String f216a;

    /* renamed from: b */
    public long f217b;

    /* renamed from: c */
    public Vector f218c;

    /* renamed from: d */
    public Vector f219d;

    /* renamed from: e */
    public int f220e;

    /* renamed from: f */
    public int f221f;

    /* renamed from: g */
    public String f222g;

    /* renamed from: h */
    public String f223h;

    /* renamed from: i */
    public Object[] f224i;

    public Message(Hashtable hashtable) {
        this.f216a = JsonParser.getStringValue(hashtable, AppState.m584b(591892));
        this.f217b = JsonParser.getIntValue(hashtable, AppState.m584b(264254)) * 1000;
        this.f218c = XmppMailRuProtocol.m868b(JsonParser.getStringValue(hashtable, AppState.m584b(591883)), JsonParser.getStringValue(hashtable, AppState.m584b(526365)));
        this.f219d = XmppMailRuProtocol.m868b(JsonParser.getStringValue(hashtable, AppState.m584b(460804)), JsonParser.getStringValue(hashtable, AppState.m584b(395262)));
        this.f220e = JsonParser.getIntValue(hashtable, AppState.m584b(591847));
        m670a(4, JsonParser.getIntValue(hashtable, AppState.m584b(657373)) != 0);
        m670a(1, JsonParser.getIntValue(hashtable, AppState.m584b(657363)) != 0);
        this.f222g = Conversation.m1122j(JsonParser.getStringValue(hashtable, AppState.m584b(460837)));
    }

    public Message(Vector vector, String str, String str2) {
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        this.f218c = XmppMailRuProtocol.m867a(NetworkUtils.m1213g(), AppController.m459a(c0028ba.f315k, Utils.m522f(c0028ba.f230f)));
        this.f219d = vector;
        this.f222g = str;
        this.f223h = str2;
    }

    public Message(ByteBuffer c0043n, String str) {
        this.f216a = str;
        this.f217b = c0043n.readLong();
        this.f218c = XmppMailRuProtocol.m863e(c0043n);
        this.f219d = XmppMailRuProtocol.m863e(c0043n);
        this.f220e = c0043n.readInt();
        this.f221f = c0043n.readInt();
        this.f222g = c0043n.readUTF8Str((String) null);
        if (c0043n.readInt() != 0) {
            this.f223h = c0043n.readUTF8Str((String) null);
        }
        if (c0043n.readInt() != 0) {
            this.f224i = ResourceManager.m934a(c0043n);
        }
    }

    /* renamed from: a */
    public final boolean m666a() {
        return !m671a(4);
    }

    /* renamed from: a */
    public final MenuItem m667a(ChatRoom c0052w) {
        boolean z;
        int iM1418a;
        String[] strArrM869c;
        String[] strArrM869c2;
        boolean zM671a = m671a(4);
        int i = zM671a ? 1 : 0;
        int i2 = i;
        int iM214a = AppState.m608k(i).m214a(AppState.m620j());
        int iM586d = (((AppState.m586d(1528) - iM214a) - 240) + 227) - 10;
        int i3 = zM671a ? 0 : 19;
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        MenuItem c0032cM887a = MenuItem.m887a(this.f216a);
        c0032cM887a.f265d = this;
        String str = this.f216a;
        Enumeration enumerationElements = c0028ba.f228d.elements();
        while (true) {
            if (!enumerationElements.hasMoreElements()) {
                z = false;
                break;
            }
            if (((ChatRoom) enumerationElements.nextElement()).m1414a(str)) {
                z = true;
                break;
            }
        }
        boolean z2 = z;
        MenuItem c0032cM896a = c0032cM887a.m896a(z ? 25 : -1);
        Calendar calendarM622k = AppState.m622k();
        int i4 = calendarM622k.get(1);
        int i5 = calendarM622k.get(2);
        int i6 = calendarM622k.get(5);
        calendarM622k.setTime(new Date(this.f217b));
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        String strM527g = Utils.m527g(NetworkUtils.m1215a((i4 == calendarM622k.get(1) && i5 == calendarM622k.get(2) && i6 == calendarM622k.get(5)) ? stringBufferM1217h.append(Conversation.m1121a(calendarM622k.get(11), 2)).append(':').append(Conversation.m1121a(calendarM622k.get(12), 2)) : stringBufferM1217h.append(Conversation.m1121a(calendarM622k.get(5), 2)).append('.').append(Conversation.m1121a(calendarM622k.get(2) + 1, 2)).append('.').append(Conversation.m1121a(calendarM622k.get(1) - 2000, 2))));
        MenuItem c0032cM901a = c0032cM896a.m901a(strM527g, i2, 10);
        String strM1215a = NetworkUtils.m1215a(NetworkUtils.m1217h().append('[').append(this.f220e).append(AppState.m584b(903)));
        MenuItem c0032cM901a2 = c0032cM901a.m901a(strM1215a, i2, i3);
        int iM214a2 = AppState.m608k(i2).m214a(NetworkUtils.m1215a(NetworkUtils.m1217h().append(strM527g).append(strM1215a)));
        if (m671a(1)) {
            c0032cM901a2.m896a(221);
            iM214a2 += 20;
        }
        if (z2) {
            iM214a2 += 20;
        }
        if (c0052w == c0028ba.m746W()) {
            iM1418a = c0028ba.m745h(c0052w.m1423e(this.f216a)).m1418a();
        } else {
            iM1418a = (c0052w == c0028ba.m746W() || !c0052w.m1416c(this.f216a)) ? 3 : c0052w.m1418a();
        }
        int i7 = iM1418a;
        boolean z3 = false;
        if ((i7 & 1) != 0 && (strArrM869c2 = XmppMailRuProtocol.m869c(m668b())) != null) {
            c0032cM901a2.m901a(m672a(NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(867)).append(' ').append(strArrM869c2[1])), i2, iM586d - iM214a2, iM214a, true), i2, i3);
            z3 = true;
        }
        if ((i7 & 2) != 0 && (strArrM869c = XmppMailRuProtocol.m869c(m669c())) != null) {
            c0032cM901a2.m901a(m672a(NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(868)).append(' ').append(strArrM869c[1])), i2, iM586d - (z3 ? 0 : iM214a2), iM214a, true), i2, i3);
        }
        boolean z4 = c0052w == c0028ba.m746W();
        c0032cM901a2.m900a(zM671a ? 225 : 237, m672a(m673d(), i2, iM586d - 22, iM214a, z4), i2, i3);
        if (z4) {
            c0032cM901a2.m896a(234);
            c0032cM901a2.m901a(m672a(c0028ba.m745h(c0052w.m1423e(this.f216a)).f410b, i2, iM586d - 22, iM214a, false), i2, i3);
        }
        return c0032cM901a2;
    }

    /* renamed from: b */
    public final Vector m668b() {
        return this.f218c == null ? NetworkUtils.m1213g() : this.f218c;
    }

    /* renamed from: c */
    public final Vector m669c() {
        return this.f219d == null ? NetworkUtils.m1213g() : this.f219d;
    }

    /* renamed from: a */
    public final void m670a(int i, boolean z) {
        if (z) {
            this.f221f |= i;
        } else {
            this.f221f &= i ^ (-1);
        }
    }

    /* renamed from: a */
    public final boolean m671a(int i) {
        return (this.f221f & i) != 0;
    }

    /* JADX DEBUG: Move duplicate insns, count: 4 to block B:12:0x0049 */
    /* renamed from: a */
    private static String m672a(String str, int i, int i2, int i3, boolean z) {
        int i4;
        int iM215a;
        GraphicsContext c0012alM608k = AppState.m608k(i);
        if (c0012alM608k.m214a(str) > i2 + i3) {
            int i5 = 4;
            int i6 = 4;
            int length = str.length();
            int i7 = length;
            while (true) {
                i4 = (i5 + length) >> 1;
                if (i4 == i6 || (iM215a = c0012alM608k.m215a(str, 0, i4)) == i2) {
                    break;
                }
                if (iM215a > i2) {
                    i7 = i4;
                } else {
                    i6 = i4;
                }
                i5 = i6;
                length = i7;
            }
            str = NetworkUtils.m1215a(NetworkUtils.m1217h().append(StringUtils.m13b(str, i4 + 1)).append((char) 8230));
        }
        return z ? NetworkUtils.m1215a(NetworkUtils.m1217h().append(str).append('\n')) : str;
    }

    /* renamed from: d */
    public final String m673d() {
        return (this.f222g == null || this.f222g.length() == 0) ? AppState.m584b(902) : this.f222g;
    }

    /* renamed from: e */
    public final Object m674e() {
        Hashtable hashtable = new Hashtable();
        String[] strArrM869c = XmppMailRuProtocol.m869c(this.f218c);
        if (strArrM869c != null) {
            hashtable.put(AppState.m584b(264203), strArrM869c[1]);
        }
        String strM584b = AppState.m584b(133118);
        Vector vector = this.f219d;
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        if (vector != null) {
            String str = AppState.f181d;
            NetworkUtils.m1221a(60);
            NetworkUtils.m1221a(62);
            String strM1221a = NetworkUtils.m1221a(44);
            Enumeration enumerationElements = vector.elements();
            while (enumerationElements.hasMoreElements()) {
                stringBufferM1217h.append(stringBufferM1217h.length() > 0 ? strM1221a : str).append(str).append(((String[]) enumerationElements.nextElement())[0]).append(str);
            }
        }
        hashtable.put(strM584b, NetworkUtils.m1215a(stringBufferM1217h));
        hashtable.put(AppState.m584b(460837), this.f222g);
        hashtable.put(AppState.m584b(264133), this.f223h);
        JsonParser.putIntValue(hashtable, AppState.m584b(264258), 1);
        JsonParser.putIntValue(hashtable, AppState.m584b(263849), 1);
        JsonParser.putIntValue(hashtable, AppState.m584b(329772), 0);
        JsonParser.putIntValue(hashtable, AppState.m584b(460784), 0);
        JsonParser.putIntValue(hashtable, AppState.m584b(919536), 0);
        Vector vectorM1213g = NetworkUtils.m1213g();
        int length = this.f224i == null ? 0 : this.f224i.length;
        for (int i = 0; i < length; i++) {
            String[] strArr = (String[]) this.f224i[i];
            Hashtable hashtable2 = new Hashtable();
            for (int i2 = 1227; i2 <= 1232; i2++) {
                hashtable2.put(AppState.m584b(i2), strArr[i2 - 1227]);
            }
            vectorM1213g.addElement(hashtable2);
        }
        hashtable.put(AppState.m584b(722874), vectorM1213g);
        if (vectorM1213g.size() > 0) {
            JsonParser.putIntValue(hashtable, AppState.m584b(657363), 1);
        }
        return hashtable;
    }
}
