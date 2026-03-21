package p000;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/* renamed from: l */
/* loaded from: MobileAgent_3.9.jar:l.class */
public abstract class Contact implements Sortable {

    /* renamed from: o */
    public final Account f369o;

    /* renamed from: a */
    private ByteBuffer f370a;

    /* renamed from: p */
    public boolean f371p;

    /* renamed from: q */
    public int f372q;

    /* renamed from: r */
    public int f373r;

    /* renamed from: s */
    public byte f374s;

    /* renamed from: t */
    public boolean f375t;

    /* renamed from: u */
    public String f376u;

    /* renamed from: v */
    public String f377v;

    /* renamed from: b */
    private int f378b;

    /* renamed from: c */
    private long f379c;

    /* renamed from: w */
    public String f380w;

    /* renamed from: x */
    public String f381x;

    public Contact(Account abstractC0037h) {
        this.f369o = abstractC0037h;
    }

    /* renamed from: a */
    public abstract void mo136a(ByteBuffer c0043n);

    /* renamed from: e */
    public int mo139e() {
        if (this.f374s != 0) {
            return (this.f374s & 1) != 0 ? 16384 : 16386;
        }
        if (this.f372q != 0) {
            return 26;
        }
        return this.f373r;
    }

    /* renamed from: c */
    public final void m1227c(int i) {
        this.f374s = (byte) (this.f374s | i);
        C0015ao.m414a(this);
        this.f375t = true;
        this.f379c = AppState.m598g(1530);
        m1228A();
    }

    /* renamed from: g */
    public String mo995g() {
        return AppState.f181d;
    }

    /* renamed from: A */
    public final void m1228A() {
        int i = this.f374s != 0 ? 1073741824 : 0;
        if (this.f379c != 0) {
            i |= 268435456;
        }
        if (this.f371p) {
            i |= 536870912;
        }
        if (!mo144l()) {
            i |= 67108864;
        }
        int i2 = !mo990d() ? i | 33554432 : i & (-1912602625);
        int i3 = !mo143m() ? i2 | 134217728 : i2 & (-100663297);
        if (i3 != this.f378b) {
            this.f378b = i3;
            C0015ao.f152f = true;
        }
    }

    /* renamed from: B */
    public final void m1229B() {
        this.f370a = new ByteBuffer();
        m1245o();
    }

    /* renamed from: m */
    public abstract boolean mo143m();

    /* renamed from: l */
    public abstract boolean mo144l();

    /* renamed from: n */
    public boolean mo996n() {
        return false;
    }

    /* renamed from: C */
    public final void m1230C() {
        this.f372q = 0;
        this.f375t = true;
    }

    /* renamed from: a */
    public final void m1231a(long j, StringBuffer stringBuffer) {
        m1232a(j, C0040k.m1215a(stringBuffer), 4);
    }

    /* renamed from: a */
    public final void m1232a(long j, String str, int i) {
        C0008ah c0008ah;
        AppState.m601a(1237, (Object) this.f380w);
        C0034e.m925a(2);
        m1227c(i);
        this.f369o.m1071e(mo135a());
        m1230C();
        m1239a(i != 4 ? 0 : 8, str, j, 0L);
        ContactGroup abstractC0046qM1080g = this.f369o.m1080g(this);
        if (abstractC0046qM1080g != null && abstractC0046qM1080g.f399g) {
            abstractC0046qM1080g.mo1397n();
        }
        m1228A();
        Account abstractC0037h = this.f369o;
        String str2 = this.f380w;
        if (abstractC0037h == null || str2 == null) {
            return;
        }
        Vector vectorM614m = AppState.m614m(1246);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                c0008ah = (C0008ah) vectorM614m.elementAt(size);
            }
        } while (c0008ah.f51h != abstractC0037h);
        c0008ah.f52i = str2;
        c0008ah.f53j = 0;
    }

    /* renamed from: b */
    public final int m1233b(String str) {
        C0034e.m925a(4);
        if (StringUtils.m1a(str)) {
            return 309;
        }
        Account abstractC0037h = this.f369o;
        long jM598g = AppState.m598g(1530);
        int iMo125a = abstractC0037h.mo125a(this, str, jM598g);
        if (0 != iMo125a) {
            return iMo125a;
        }
        m1239a(1, str, jM598g, jM598g);
        this.f379c = AppState.m598g(1530);
        m1228A();
        return 0;
    }

    /* renamed from: D */
    public final int m1234D() {
        return this.f369o.mo104c(this);
    }

    /* renamed from: E */
    public final int m1235E() {
        if (mo143m()) {
            return 310;
        }
        return this.f369o.mo105d(this);
    }

    /* renamed from: F */
    public final int m1236F() {
        if (mo143m()) {
            return 310;
        }
        return this.f369o.mo106e(this);
    }

    @Override // p000.Sortable
    /* renamed from: a */
    public final int mo1237a(Object obj) {
        Contact abstractC0041l = (Contact) obj;
        int i = abstractC0041l.f378b - this.f378b;
        if (i != 0) {
            return i;
        }
        long j = abstractC0041l.f379c - this.f379c;
        return j != 0 ? j < 0 ? -1 : 1 : this.f377v.compareTo(abstractC0041l.f377v);
    }

    /* renamed from: c */
    public void mo134c() {
        if (mo143m()) {
            this.f379c = 0L;
        }
        this.f371p = false;
        m1228A();
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:11:0x0097 */
    /* renamed from: a */
    public final void m1238a(long j, int i) {
        this.f375t = true;
        ByteBuffer c0043nM851h = this.f370a == null ? C0031bd.m851h(this.f380w) : this.f370a;
        this.f370a = c0043nM851h;
        int i2 = c0043nM851h.f384b;
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= i2) {
                m1245o();
                return;
            }
            int iM1351l = c0043nM851h.m1351l(i4);
            int i5 = i4 + 3 + 8;
            if (j == ((c0043nM851h.m1330h(i5) & 4294967295L) | (c0043nM851h.m1330h(i5 + 4) << 32))) {
                c0043nM851h.f383a[c0043nM851h.f385c + i4 + 2] = (byte) (c0043nM851h.m1331i(i4 + 2) | i);
            }
            i3 = i4 + iM1351l + 2;
        }
    }

    /* renamed from: a */
    public final void m1239a(int i, String str, long j, long j2) {
        this.f375t = true;
        ByteBuffer c0043nM851h = this.f370a == null ? C0031bd.m851h(this.f380w) : this.f370a;
        this.f370a = c0043nM851h;
        int iM586d = AppState.m586d(102) - 1;
        ByteBuffer c0043n = this.f370a;
        int i2 = 0;
        int i3 = 0;
        int i4 = c0043n.f384b;
        while (i4 > 0) {
            int iM1351l = c0043n.m1351l(i3);
            i3 += iM1351l + 2;
            i4 -= iM1351l + 2;
            i2++;
        }
        while (i2 > iM586d) {
            c0043n.m1329g(c0043n.m1353u());
            i2--;
        }
        c0043nM851h.m1357m(17 + (str.length() << 1)).m1321f(i).m1323a((j != 0 ? j : System.currentTimeMillis()) + ((AppState.m586d(246) - 13) * 3600000)).m1323a(j2).m1374i(str).m1299a();
        m1245o();
        this.f379c = AppState.m598g(1530);
        m1228A();
    }

    /* renamed from: G */
    public final boolean m1240G() {
        return this.f379c != 0;
    }

    /* renamed from: H */
    public final long m1241H() {
        long j = 0;
        ByteBuffer c0043nM1380F = m1244f().m1380F();
        while (c0043nM1380F.f384b > 0) {
            int iM1353u = c0043nM1380F.m1353u();
            byte bM1344o = c0043nM1380F.m1344o();
            c0043nM1380F.m1341m();
            long jM1341m = c0043nM1380F.m1341m();
            c0043nM1380F.m1329g(iM1353u - 17);
            if (bM1344o == 16) {
                j = jM1341m;
            }
        }
        c0043nM1380F.m1301b();
        return j;
    }

    /* renamed from: I */
    public final C0013am m1242I() {
        this.f375t = false;
        String str = this.f376u;
        AppState.m601a(1290, (Object) str);
        int iMo139e = mo139e();
        if ((this instanceof C0006af) && ((C0005ae) this.f369o).mo83f() && iMo139e >= 381 && iMo139e <= 384) {
            iMo139e += 4;
        }
        AppState.m594c(2594, iMo139e);
        C0013am c0013amM75b = AbstractC0004ad.m75b(2591);
        ByteBuffer c0043nM1380F = m1244f().m1380F();
        int iM624l = AppState.m624l();
        while (c0043nM1380F.f384b > 0) {
            int iM1353u = c0043nM1380F.m1353u();
            byte bM1344o = c0043nM1380F.m1344o();
            long jM1341m = c0043nM1380F.m1341m() - AppState.m598g(1532);
            long jM1341m2 = c0043nM1380F.m1341m();
            String strM539n = Utils.m539n(c0043nM1380F.m1369q(iM1353u - 17));
            int i = (bM1344o == 0 || bM1344o == 16 || bM1344o == 8) ? 0 : bM1344o == 1 ? 11 : (bM1344o & 64) == 0 ? 12 : 0;
            if (bM1344o == 16) {
                c0013amM75b.m251a(C0040k.m1215a(C0040k.m1217h().append(this.f376u).append(AppState.m584b(311)).append(m1248b(jM1341m, iM624l))), 8);
                c0013amM75b.m246a(2, strM539n, 0);
                if (this.f369o.m1056C()) {
                    c0013amM75b.m250b(-1, AppState.m584b(839), i, new Object[]{C0034e.m967e(1), strM539n, str, new Long(jM1341m2)});
                }
            } else if (bM1344o == 8) {
                int iIndexOf = strM539n.indexOf(10);
                String strM13b = StringUtils.m13b(strM539n, iIndexOf);
                String strM15c = StringUtils.m15c(strM539n, iIndexOf + 1);
                c0013amM75b.m251a(StringUtils.m9b(strM13b, m1248b(jM1341m, iM624l)), 8);
                m1243a(c0013amM75b, strM15c, i);
            } else {
                c0013amM75b.m251a(C0040k.m1215a(C0040k.m1217h().append(bM1344o == 0 ? this.f376u : this.f369o.f339I).append(',').append(' ').append(m1248b(jM1341m, iM624l))), bM1344o == 0 ? 8 : 9);
                m1243a(c0013amM75b, strM539n, i);
            }
        }
        c0043nM1380F.m1301b();
        return c0013amM75b;
    }

    /* renamed from: a */
    private final void m1243a(C0013am c0013am, String str, int i) {
        Vector vectorM1098a = Conversation.m1098a(str);
        int size = vectorM1098a.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str2 = (String) vectorM1098a.elementAt(i2);
            if (Conversation.m1106f(str2)) {
                c0013am.m250b(264, Conversation.m1099b(str2), i, new Object[]{C0034e.m967e(0), str2});
            } else {
                c0013am.m225a(C0032c.m889d().m902a(str2, 0, i, this.f369o.mo80a()));
            }
        }
        C0040k.m1212a(vectorM1098a);
    }

    /* renamed from: f */
    private final ByteBuffer m1244f() {
        if (this.f370a == null) {
            this.f370a = C0031bd.m851h(this.f380w);
        }
        return this.f370a;
    }

    /* renamed from: o */
    private final void m1245o() {
        C0031bd.m853a(this.f380w, m1244f().m1380F());
    }

    /* renamed from: J */
    public final C0013am m1246J() {
        String strM1215a;
        C0013am c0013amM75b = AbstractC0004ad.m75b(2631);
        ByteBuffer c0043nM1380F = m1244f().m1380F();
        while (c0043nM1380F.f384b > 0) {
            int iM1353u = c0043nM1380F.m1353u();
            c0043nM1380F.m1344o();
            c0043nM1380F.m1341m();
            c0043nM1380F.m1341m();
            String strM1369q = c0043nM1380F.m1369q(iM1353u - 17);
            if (strM1369q.length() > 50) {
                strM1215a = C0040k.m1215a(C0040k.m1217h().append(StringUtils.m13b(strM1369q, 50)).append((char) 8230));
            } else {
                strM1215a = strM1369q;
            }
            c0013amM75b.m256a(-1, (String) null, strM1215a, 200, strM1369q);
        }
        c0043nM1380F.m1301b();
        return c0013amM75b;
    }

    /* renamed from: K */
    public final int m1247K() {
        if (m1244f().f384b > 0 || !this.f369o.m1056C()) {
            return 40;
        }
        if (mo990d()) {
            return C0034e.m946g();
        }
        return 63;
    }

    /* renamed from: b */
    public abstract C0032c mo138b();

    /* renamed from: i */
    public abstract boolean mo140i();

    /* renamed from: j */
    public abstract boolean mo141j();

    /* renamed from: k */
    public abstract boolean mo142k();

    /* renamed from: b */
    private static String m1248b(long j, int i) {
        Calendar calendarM622k = AppState.m622k();
        calendarM622k.setTime(new Date(j));
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int i2 = calendarM622k.get(1) << 16;
        int i3 = calendarM622k.get(2);
        int i4 = i2 + (i3 << 8);
        int i5 = calendarM622k.get(5);
        if (i4 + i5 != i) {
            stringBufferM1217h.append(Utils.m501b(i5)).append('/').append(Utils.m501b(i3 + 1)).append(' ');
        }
        return C0040k.m1215a(stringBufferM1217h.append(Utils.m501b(calendarM622k.get(11))).append(':').append(Utils.m501b(calendarM622k.get(12))));
    }

    /* renamed from: a */
    public abstract String mo135a();

    /* renamed from: d */
    public boolean mo990d() {
        return false;
    }

    /* renamed from: h */
    public abstract void mo145h();

    /* renamed from: c */
    public final void m1249c(String str) {
        if (StringUtils.m6a(str, this.f376u)) {
            return;
        }
        this.f376u = str;
        this.f377v = StringUtils.m17c(str.toLowerCase());
        C0015ao.f152f = true;
    }

    public final String toString() {
        return this.f376u;
    }

    /* renamed from: L */
    public void mo148L() {
    }

    /* renamed from: M */
    public final int m1250M() {
        if (mo140i()) {
            return 267;
        }
        return mo141j() ? 266 : -1;
    }
}
