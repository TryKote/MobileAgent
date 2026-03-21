package p000;

import java.util.Hashtable;
import java.util.Vector;

/* renamed from: w */
/* loaded from: MobileAgent_3.9.jar:w.class */
public final class ChatRoom {

    /* renamed from: a */
    public int f409a;

    /* renamed from: b */
    public String f410b;

    /* renamed from: c */
    public int f411c;

    /* renamed from: d */
    public int f412d;

    /* renamed from: e */
    public String f413e;

    /* renamed from: f */
    public final Vector f414f;

    /* renamed from: g */
    public final Vector f415g;

    /* renamed from: h */
    public final Hashtable f416h;

    /* renamed from: i */
    public Hashtable f417i;

    /* renamed from: j */
    public Vector f418j;

    /* renamed from: k */
    public boolean f419k;

    /* renamed from: l */
    public boolean f420l;

    public ChatRoom() {
        this.f414f = NetworkUtils.m1213g();
        this.f415g = NetworkUtils.m1213g();
        this.f416h = new Hashtable();
        this.f420l = true;
    }

    public ChatRoom(Object obj) {
        this();
        m1412a(obj);
        this.f420l = true;
    }

    /* renamed from: a */
    public final void m1410a(ByteBuffer c0043n) {
        c0043n.writeStringUTF16(this.f410b).writeIntLE(this.f411c).writeIntLE(this.f409a).writeIntLE(this.f412d).writeStringLatin1(this.f413e);
        if (this.f414f.size() > 20) {
            this.f414f.setSize(20);
        }
        int size = this.f414f.size();
        c0043n.writeIntLE(size);
        for (int i = 0; i < size; i++) {
            String strM521a = Utils.m521a(this.f414f, i);
            c0043n.writeStringLatin1(strM521a);
            Message c0026azM1415b = m1415b(strM521a);
            c0043n.writeLong(c0026azM1415b.f217b);
            XmppMailRuProtocol.m862a(c0026azM1415b.f218c, c0043n);
            XmppMailRuProtocol.m862a(c0026azM1415b.f219d, c0043n);
            c0043n.writeIntLE(c0026azM1415b.f220e).writeIntLE(c0026azM1415b.f221f).writeStringUTF16(Utils.m522f(c0026azM1415b.f222g));
            if (c0026azM1415b.f223h == null || c0026azM1415b.f223h.length() > 3072) {
                c0043n.writeIntLE(0).writeIntLE(0);
            } else {
                c0043n.writeIntLE(1).writeStringUTF16(c0026azM1415b.f223h).writeIntLE(1);
                Object[] objArr = c0026azM1415b.f224i;
                if (objArr == null) {
                    c0043n.writeIntLE(0);
                } else {
                    c0043n.writeIntLE(objArr.length);
                    for (Object obj : objArr) {
                        String[] strArr = (String[]) obj;
                        for (int i2 = 0; i2 < 6; i2++) {
                            c0043n.writeStringUTF16(strArr[i2]);
                        }
                    }
                }
            }
            c0026azM1415b.f218c = null;
            c0026azM1415b.f219d = null;
            c0026azM1415b.f222g = null;
            c0026azM1415b.f223h = null;
            c0026azM1415b.f224i = null;
        }
    }

    /* renamed from: b */
    public static final ChatRoom m1411b(ByteBuffer c0043n) {
        ChatRoom c0052w = new ChatRoom();
        c0052w.f410b = c0043n.readUTF8Str((String) null);
        c0052w.f411c = c0043n.readInt();
        c0052w.f409a = c0043n.readInt();
        c0052w.f412d = c0043n.readInt();
        c0052w.f413e = c0043n.readWideStr();
        int iM1328e = c0043n.readInt();
        for (int i = 0; i < iM1328e; i++) {
            Vector vector = c0052w.f414f;
            String strM1334g = c0043n.readWideStr();
            vector.addElement(strM1334g);
            c0052w.f416h.put(strM1334g, new Message(c0043n, strM1334g));
        }
        return c0052w;
    }

    /* renamed from: a */
    public final void m1412a(Object obj) {
        this.f410b = JsonParser.m480c(obj, 263472);
        this.f411c = JsonParser.m478b(obj, 526252);
        this.f409a = JsonParser.m478b(obj, 132297);
        this.f412d = JsonParser.m478b(obj, 395188);
        this.f413e = AppState.f181d;
        this.f419k = true;
    }

    public ChatRoom(int i) {
        this.f409a = i;
        this.f414f = NetworkUtils.m1213g();
        this.f415g = NetworkUtils.m1213g();
        this.f416h = new Hashtable();
        this.f417i = new Hashtable();
        this.f418j = NetworkUtils.m1213g();
    }

    /* renamed from: g */
    private String m1413g() {
        int i = 5;
        do {
            i--;
            if (i < 0) {
                return this.f410b;
            }
        } while (!this.f410b.equals(AppState.m584b(i + 891)));
        return AppState.m584b(i + 896);
    }

    /* renamed from: a */
    public final boolean m1414a(String str) {
        return this.f415g.contains(str);
    }

    /* renamed from: b */
    public final Message m1415b(String str) {
        if (str != null) {
            return (Message) this.f416h.get(str);
        }
        return null;
    }

    /* renamed from: c */
    public final boolean m1416c(String str) {
        return this.f414f.contains(str);
    }

    /* renamed from: d */
    public final void m1417d(String str) {
        this.f415g.removeElement(str);
    }

    /* renamed from: a */
    public final int m1418a() {
        String strM1413g = m1413g();
        if (StringUtils.m3a(896, strM1413g) || StringUtils.m3a(900, strM1413g)) {
            return 1;
        }
        return (StringUtils.m3a(898, strM1413g) || StringUtils.m3a(899, strM1413g)) ? 2 : 3;
    }

    /* renamed from: b */
    public final void m1419b() {
        this.f412d--;
    }

    /* renamed from: c */
    public final void m1420c() {
        this.f412d++;
    }

    /* renamed from: d */
    public final void m1421d() {
        this.f411c--;
    }

    /* renamed from: e */
    public final void m1422e() {
        this.f413e = null;
        this.f414f.removeAllElements();
        this.f415g.removeAllElements();
        this.f418j.removeAllElements();
        this.f416h.clear();
        this.f417i.clear();
    }

    /* renamed from: e */
    public final int m1423e(String str) {
        return Integer.parseInt((String) this.f417i.get(str));
    }

    /* renamed from: a */
    public final void m1424a(boolean z) {
        this.f420l = z;
        this.f419k = true;
    }

    /* renamed from: f */
    public final String m1425f() {
        if (this == ((MrimAccount) AppState.m616i()).m746W()) {
            return this.f410b;
        }
        return NetworkUtils.m1215a(NetworkUtils.m1217h().append(m1413g()).append(' ').append('[').append(this.f412d).append('/').append(this.f411c).append(']'));
    }
}
