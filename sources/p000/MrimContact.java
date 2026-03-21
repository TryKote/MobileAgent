package p000;

import java.util.Vector;

/* renamed from: f */
/* loaded from: MobileAgent_3.9.jar:f.class */
public final class MrimContact extends Contact implements ListItem {

    /* renamed from: a */
    public final int f294a;

    /* renamed from: b */
    public int f295b;

    /* renamed from: c */
    public int f296c;

    /* renamed from: d */
    public String f297d;

    /* renamed from: e */
    public int f298e;

    /* renamed from: f */
    public int f299f;

    /* renamed from: g */
    public String f300g;

    /* renamed from: h */
    public String f301h;

    /* renamed from: y */
    private long f302y;

    /* renamed from: i */
    public String f303i;

    /* renamed from: j */
    public String f304j;

    /* renamed from: k */
    public Vector f305k;

    /* renamed from: l */
    public VCard f306l;

    /* renamed from: m */
    public boolean f307m;

    /* renamed from: n */
    public SizeCache f308n;

    public MrimContact(Account abstractC0037h, int i, int i2, int i3, String str, String str2, int i4, int i5, String str3, String str4, String str5) {
        super(abstractC0037h);
        this.f294a = i;
        this.f295b = i2;
        this.f296c = i3;
        this.f297d = str;
        this.f298e = i4;
        this.f299f = i5;
        this.f300g = str3;
        this.f301h = str5;
        m1249c(Utils.m528a(str2, str));
        this.f373r = C0015ao.m349a(i5, str4);
        this.f371p = i5 != 0;
        m992O();
        this.f308n = new SizeCache();
    }

    public MrimContact() {
        super(null);
        this.f294a = 0;
        this.f308n = new SizeCache();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final String mo135a() {
        if (!mo990d()) {
            return this.f297d;
        }
        Vector vectorM516c = Utils.m516c(this.f300g, ',');
        String str = (String) vectorM516c.elementAt(0);
        NetworkUtils.m1212a(vectorM516c);
        return str;
    }

    public MrimContact(Account abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.f294a = c0043n.m1328e();
        String str = AppState.f181d;
        this.f295b = c0043n.m1328e();
        this.f297d = StringUtils.m17c(c0043n.m1334g().toLowerCase());
        m1249c(c0043n.m1335e((String) null));
        this.f298e = c0043n.m1328e();
        this.f300g = c0043n.m1334g();
        byte bM1344o = c0043n.m1344o();
        this.f374s = bM1344o;
        if (bM1344o != 0) {
            C0015ao.m414a((Contact) this);
        }
        this.f301h = str;
        this.f373r = 155;
        m992O();
        this.f308n = new SizeCache();
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem mo138b() {
        MenuItem c0032cM896a = MenuItem.m887a(this.f380w).m896a(mo139e());
        String str = this.f376u;
        int i = mo141j() ? 3 : mo140i() ? 2 : 0;
        int i2 = this.f295b;
        c0032cM896a.m901a(str, i, (i2 & 1048576) != 0 ? 0 : (i2 & 8) != 0 ? 4 : (i2 & 4) != 0 ? 5 : this.f299f == 0 ? 0 : 3).f265d = this;
        if (!mo990d() && Utils.m535l(this.f300g)) {
            c0032cM896a.m896a(27);
        }
        if (Utils.m535l(this.f304j)) {
            c0032cM896a.m896a(242);
        }
        c0032cM896a.f265d = this;
        return c0032cM896a;
    }

    @Override // p000.Contact
    /* renamed from: c */
    public final void mo134c() {
        this.f373r = 155;
        this.f299f = 0;
        this.f304j = null;
        this.f303i = null;
        m999p();
        super.mo134c();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void mo136a(ByteBuffer c0043n) {
        c0043n.m1360p(this.f294a).m1360p(this.f295b).m1308a(this.f297d).m1309b(this.f376u).m1360p(this.f298e).m1308a(this.f300g).m1321f(this.f374s);
    }

    /* renamed from: a */
    public final void m989a(Vector vector) {
        if (vector == null) {
            NetworkUtils.m1212a(this.f305k);
            this.f305k = null;
            return;
        }
        if (this.f305k == null) {
            this.f305k = NetworkUtils.m1213g();
        }
        this.f305k.removeAllElements();
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                this.f305k.addElement(vector.elementAt(size));
            }
        }
    }

    @Override // p000.Contact
    /* renamed from: d */
    public final boolean mo990d() {
        return (this.f295b & 1048576) != 0;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int mo139e() {
        if (mo990d()) {
            return this.f374s == 0 ? 27 : 16384;
        }
        if (mo996n()) {
            return this.f374s == 0 ? 232 : 16616;
        }
        int iMo139e = super.mo139e();
        if (iMo139e == 16384 || iMo139e == 26) {
            return iMo139e;
        }
        if (0 != (this.f298e & 1) || mo143m()) {
            return 154;
        }
        return iMo139e;
    }

    /* renamed from: N */
    private final String m991N() {
        int iIndexOf = -1;
        try {
            iIndexOf = this.f300g.indexOf(44);
        } catch (Throwable unused) {
        }
        return iIndexOf >= 0 ? StringUtils.m13b(this.f300g, iIndexOf) : Utils.m522f(this.f300g);
    }

    /* renamed from: O */
    private void m992O() {
        ByteBuffer c0043nM1050q = this.f369o.m1050q();
        String strM991N = mo990d() ? m991N() : this.f297d;
        this.f381x = strM991N;
        this.f380w = c0043nM1050q.m1314d(strM991N).m1337i();
        if (mo990d()) {
            this.f381x = Utils.m530h(this.f381x);
        }
        m1228A();
        this.f369o.m1081h(this);
    }

    /* JADX DEBUG: Possible override for method l.f()Ln; */
    /* renamed from: f */
    public final int m993f() {
        long jM598g = AppState.m598g(1530);
        if (jM598g - this.f302y <= 60000) {
            return 925;
        }
        this.f302y = jM598g;
        MrimAccount c0028ba = (MrimAccount) this.f369o;
        int iM1052c = c0028ba.m1052c(c0028ba.m719a(new Object[]{C0015ao.m321a(c0028ba, 4104, new ByteBuffer().m1360p(16512).m1308a(this.f297d).m1309b(AppState.m584b(909)).m1308a(AppState.m584b(33819707))), C0034e.m967e(14)}));
        if (0 != iM1052c) {
            return iM1052c;
        }
        m1239a(1, AppState.m584b(924), 0L, 0L);
        return 0;
    }

    /* renamed from: a */
    public final boolean m994a(String str) {
        Vector vectorM516c = Utils.m516c(this.f300g, ',');
        int size = vectorM516c.size();
        do {
            size--;
            if (size < 0) {
                NetworkUtils.m1212a(vectorM516c);
                return false;
            }
        } while (!str.equals(vectorM516c.elementAt(size)));
        NetworkUtils.m1212a(vectorM516c);
        return true;
    }

    @Override // p000.Contact
    /* renamed from: g */
    public final String mo995g() {
        return this.f300g;
    }

    @Override // p000.Contact
    /* renamed from: h */
    public final void mo145h() {
        this.f298e &= -2;
    }

    @Override // p000.Contact
    /* renamed from: i */
    public final boolean mo140i() {
        return (this.f295b & 8) != 0;
    }

    @Override // p000.Contact
    /* renamed from: j */
    public final boolean mo141j() {
        return (this.f295b & 4) != 0;
    }

    @Override // p000.Contact
    /* renamed from: k */
    public final boolean mo142k() {
        return (this.f295b & 16) != 0;
    }

    @Override // p000.Contact
    /* renamed from: l */
    public final boolean mo144l() {
        return (this.f298e & 1) != 0;
    }

    @Override // p000.Contact
    /* renamed from: m */
    public final boolean mo143m() {
        return (this.f295b & 65536) != 0;
    }

    @Override // p000.Contact
    /* renamed from: n */
    public final boolean mo996n() {
        return (this.f295b & 128) != 0;
    }

    /* renamed from: a */
    public final void m997a(String str, String str2) {
        m1249c(str);
        this.f300g = str2;
        this.f381x = mo990d() ? Utils.m530h(m991N()) : this.f297d;
    }

    /* JADX DEBUG: Possible override for method l.o()V */
    /* renamed from: o */
    public final String m998o() {
        try {
            return this.f306l.f16d;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: p */
    public final void m999p() {
        this.f306l = null;
        ChatRenderer.f253h = null;
        MapRenderer.f200h = true;
    }

    /* renamed from: q */
    public final boolean m1000q() {
        return this.f306l != null;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int mo276r() {
        return 3;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean mo277s() {
        return this.f307m && this.f306l != null && this.f306l.m59c();
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void mo278t() {
        this.f307m = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void mo279u() {
        this.f307m = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int mo274v() {
        try {
            return (int) this.f306l.m56a();
        } catch (Throwable unused) {
            m999p();
            return 0;
        }
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int mo275w() {
        try {
            return (int) this.f306l.m57b();
        } catch (Throwable unused) {
            m999p();
            return 0;
        }
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String mo273x() {
        StringBuffer stringBufferAppend = NetworkUtils.m1217h().append(this.f376u);
        String str = this.f306l.f16d;
        if (str.length() > 0) {
            stringBufferAppend.append(',').append(' ').append(str).append('.');
        }
        return NetworkUtils.m1215a(stringBufferAppend);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int mo280y() {
        if (this.f306l != null) {
            return this.f306l.m60d();
        }
        return 10;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean mo281z() {
        return this.f306l.m59c() && !this.f306l.f24l;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int mo282a(int i) {
        return this.f308n.m1405a(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int mo283b(int i) {
        return this.f308n.m1406b(i, this);
    }
}
