package p000;

import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: ae */
/* loaded from: MobileAgent_3.9.jar:ae.class */
public class C0005ae extends AbstractC0037h {

    /* renamed from: a */
    public final Vector f30a;

    /* renamed from: f */
    private Object[] f31f;

    /* renamed from: g */
    private Object[] f32g;

    /* renamed from: b */
    public String f33b;

    /* renamed from: c */
    public int f34c;

    /* renamed from: d */
    public Object f35d;

    /* renamed from: h */
    private Throwable f36h;

    /* renamed from: e */
    public String f37e;

    public C0005ae(int i, String str, String str2) {
        super(i, str, str2);
        this.f325u = 1;
        this.f30a = C0040k.m1213g();
        C0036g c0036g = new C0036g(this, 0, AppState.m584b(1039));
        c0036g.f399g = true;
        this.f334D = c0036g;
        this.f33b = AppState.f181d;
        this.f37e = AppState.f181d;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public int mo80a() {
        return 2;
    }

    /* renamed from: r */
    private String m81r() {
        StringBuffer stringBufferAppend = C0040k.m1217h().append('m');
        int i = this.f319o + 1;
        this.f319o = i;
        return C0040k.m1215a(stringBufferAppend.append(i));
    }

    public C0005ae(ByteBuffer c0043n) {
        super(c0043n);
        this.f30a = C0040k.m1213g();
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                break;
            } else {
                m1083b((AbstractC0046q) new C0036g(this, c0043n));
            }
        }
        C0036g c0036g = new C0036g(this, c0043n);
        int iM541c = Utils.m541c(c0036g.f397e);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                c0036g.f399g = true;
                this.f334D = c0036g;
                this.f33b = c0043n.m1334g();
                this.f34c = c0043n.m1353u();
                this.f37e = c0043n.m1334g();
                return;
            }
            ((C0006af) c0036g.f397e.elementAt(iM541c)).f43b = true;
        }
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final AbstractC0037h mo82a(ByteBuffer c0043n, boolean z, boolean z2) {
        super.mo82a(c0043n, z, z2);
        c0043n.m1308a(this.f33b).m1357m(this.f34c).m1308a(this.f37e);
        return this;
    }

    /* renamed from: f */
    public boolean mo83f() {
        return false;
    }

    /* renamed from: j */
    public String mo84j() {
        return StringUtils.m5b(this.f315k);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final AbstractC0046q mo85b() {
        return new C0036g(this, -1, AppState.m584b(1040));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final AbstractC0046q mo86c() {
        return new C0036g(this, -1, AppState.m584b(1042));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: d */
    public final AbstractC0046q mo87d() {
        return new C0036g(this, -1, AppState.m584b(1041));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: e */
    public final AbstractC0046q mo88e() {
        return new C0036g(this, -1, AppState.m584b(1043));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: g */
    public final int mo89g() {
        m1061F();
        this.f331A = 0L;
        this.f330z = 0L;
        m1068L();
        return 0;
    }

    /* renamed from: a */
    private final int m90a(byte[] bArr) {
        long j = AppState.m587e(1536) ? 25000L : 60000L;
        this.f330z = j;
        this.f331A = System.currentTimeMillis() + j;
        return m1053d(new ByteBuffer().m1302a(bArr));
    }

    /* renamed from: a */
    private int m91a(XmlElement c0022av) {
        return m1053d(new ByteBuffer().m1377k(c0022av.toString()));
    }

    /* renamed from: b */
    private int m92b(XmlElement c0022av) {
        return m91a(c0022av.m559a(131550, m81r()));
    }

    /* renamed from: s */
    private final void m93s() {
        this.f318n.m1301b();
        Object[] objArr = this.f31f;
        if (objArr != null) {
            objArr[2] = null;
            objArr[1] = null;
            objArr[0] = null;
        }
    }

    /* renamed from: a */
    public final void m94a(Throwable th) {
        if (this.f322r == 2) {
            this.f36h = th;
        }
    }

    /* renamed from: a */
    public final void m95a(String str, int i) {
        if (this.f322r == 2) {
            this.f34c = i;
            this.f33b = str;
        }
    }

    /* renamed from: t */
    private final boolean m96t() {
        return mo80a() == 2 && this.f315k.endsWith(AppState.m584b(660807));
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x0236  */
    @Override // p000.AbstractC0037h
    /* renamed from: i */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void mo97i() throws Throwable {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        XmlElement c0022avM576d;
        String strM11a;
        String strM11a2;
        Object[] objArr;
        switch (this.f322r) {
            case 0:
                m93s();
                Object[] objArr2 = this.f32g;
                if (objArr2 != null) {
                    objArr2[0] = null;
                }
                this.f32g = null;
                this.f36h = null;
                this.f323s = 0;
                break;
            case 1:
                this.f323s = 10;
                if (mo83f()) {
                    if (Utils.m535l(this.f37e)) {
                        this.f322r = 3;
                    } else {
                        this.f322r = 2;
                        if (this.f315k.indexOf(64) <= 0) {
                            ((C0031bd) this).f37e = this.f315k;
                            objArr = null;
                        } else {
                            String strM13b = StringUtils.m13b(Utils.m544b(), 16);
                            Object[] objArr3 = {this, strM13b, new ByteBuffer().m1310c(5249005).m1314d(strM13b).m1337i(), C0034e.f291j[0], this.f315k, this.f316l};
                            new RunnableC0055z(34, objArr3);
                            objArr = objArr3;
                        }
                        this.f32g = objArr;
                    }
                } else if (Utils.m535l(this.f33b)) {
                    this.f322r = 3;
                } else {
                    this.f322r = 2;
                    Object[] objArr4 = {this};
                    new RunnableC0055z(33, objArr4);
                    this.f32g = objArr4;
                }
                C0015ao.f153g = true;
                break;
            case 2:
                this.f323s = 20;
                if (mo83f()) {
                    if (Utils.m535l(this.f37e)) {
                        this.f322r = 3;
                    } else if (this.f36h != null) {
                        m98b(this.f36h);
                    }
                } else if (Utils.m535l(this.f33b)) {
                    this.f322r = 3;
                } else if (this.f36h != null) {
                    m98b(this.f36h);
                }
                C0015ao.f153g = true;
                break;
            case 3:
                this.f323s = 30;
                this.f319o = 0;
                this.f320p = new C0039j(C0040k.m1215a(C0040k.m1217h().append(this.f33b).append(':').append(this.f34c)));
                this.f322r = 4;
                if (m96t()) {
                    new RunnableC0055z(30, new Object[]{this, new ByteBuffer().m1310c(2365173).m1310c(3807001).m1314d(this.f340J).m1310c(1316577).m1314d(this.f316l).m1337i(), C0034e.f291j[0]});
                }
                C0015ao.f153g = true;
                break;
            case 4:
                m93s();
                this.f323s = 40;
                if (!m96t()) {
                    if (this.f320p.m1131a() == 2) {
                        this.f323s = 50;
                        this.f322r = 5;
                        Object[] objArr5 = new Object[3];
                        objArr5[0] = this;
                        objArr5[1] = new ByteBuffer();
                        objArr5[2] = null;
                        objArr5[2] = new XmlParser(objArr5);
                        new RunnableC0055z(29, objArr5);
                        this.f31f = objArr5;
                        C0015ao.f153g = true;
                        m109u();
                    } else if (this.f320p.m1131a() <= 0) {
                        m1061F();
                    }
                    C0015ao.f153g = true;
                    break;
                } else if (this.f35d != null) {
                    if (this.f35d instanceof Throwable) {
                        m1062G();
                        break;
                    }
                }
                break;
            default:
                this.f320p.m1132a(this.f318n);
                C0015ao.m419a(this, this.f318n.f384b);
                Object[] objArr6 = this.f31f;
                ByteBuffer c0043n = this.f318n;
                ByteBuffer c0043n2 = (ByteBuffer) objArr6[1];
                synchronized (c0043n2) {
                    c0043n2.m1303a(c0043n.f383a, c0043n.f385c, c0043n.f384b);
                    c0043n.m1301b();
                }
                XmlElement c0022av = (XmlElement) Utils.m524a(this.f30a);
                if (c0022av != null) {
                    String str = c0022av.f171a;
                    if (!StringUtils.m3a(857301, str)) {
                        if (StringUtils.m3a(988737, str)) {
                            XmlElement c0022avM568b = c0022av.m568b(AppState.m584b(660472));
                            if (c0022avM568b != null) {
                                XmlElement c0022avM569h = XmlElement.m550a(263757).m569h(2102710);
                                String strM584b = AppState.m584b(660501);
                                if (c0022avM568b.m567a(strM584b) != null) {
                                    m91a(c0022avM569h.m559a(594936, strM584b));
                                } else {
                                    String strM584b2 = AppState.m584b(922626);
                                    if (c0022avM568b.m567a(strM584b2) != null) {
                                        m91a(c0022avM569h.m559a(594936, strM584b2).m553a((Object) new ByteBuffer().m1321f(0).m1314d(this.f340J).m1321f(0).m1314d((String) this.f35d).m1320d()));
                                    } else {
                                        String strM584b3 = AppState.m584b(332816);
                                        if (c0022avM568b.m567a(strM584b3) != null) {
                                            m91a(c0022avM569h.m559a(594936, strM584b3).m553a((Object) new ByteBuffer().m1377k(new ByteBuffer().m1314d(this.f340J).m1321f(64).m1314d(this.f33b).m1337i()).m1321f(0).m1377k(this.f340J).m1321f(0).m1377k(this.f316l).m1320d()));
                                        }
                                    }
                                }
                            } else if (c0022av.m568b(AppState.m584b(267762)) != null) {
                                XmlElement c0022avM570i = XmlElement.m550a(136604).m570i(198841);
                                c0022avM570i.m572b(267762, 2102742).m571a(AppState.m584b(530129), AppState.m584b(264455));
                                m92b(c0022avM570i);
                                this.f323s = 60;
                            } else {
                                C0029bb.m783a(this, 1033);
                                m1061F();
                                this.f324t = mo89g();
                            }
                        } else if (StringUtils.m3a(595536, str)) {
                            XmlElement c0022avM569h2 = XmlElement.m550a(529537).m569h(2102710);
                            String strM1317c = C0034e.m986d(StringUtils.m11a(c0022av.f173c)).m1317c();
                            int iIndexOf = strM1317c.indexOf(AppState.m584b(398406));
                            if (iIndexOf >= 0) {
                                int i = iIndexOf + 7;
                                String strMo128m = mo128m();
                                String str2 = this.f316l;
                                String str3 = this.f33b;
                                String strM12a = StringUtils.m12a(strM1317c, i, strM1317c.indexOf(34, i));
                                ByteBuffer c0043nM1310c = new ByteBuffer().m1310c(660529).m1314d(strMo128m).m1310c(595003).m1314d(str3).m1310c(595012).m1314d(strM12a).m1310c(1446989);
                                String strM544b = Utils.m544b();
                                c0022avM569h2.m553a((Object) c0043nM1310c.m1314d(strM544b).m1310c(1840227).m1314d(str3).m1310c(791679).m1314d(new ByteBuffer().m1314d(new ByteBuffer().m1314d(strMo128m).m1321f(58).m1314d(str3).m1321f(58).m1314d(str2).m1365B().m1321f(58).m1314d(strM12a).m1321f(58).m1314d(strM544b).m1365B().m1387H()).m1321f(58).m1314d(strM12a).m1310c(660619).m1314d(strM544b).m1321f(58).m1310c(263757).m1321f(58).m1314d(new ByteBuffer().m1310c(1184917).m1314d(str3).m1365B().m1387H()).m1365B().m1387H()).m1310c(988327).m1320d());
                            }
                            m91a(c0022avM569h2);
                        } else if (StringUtils.m3a(464473, str)) {
                            m109u();
                        } else if (StringUtils.m3a(530016, str)) {
                            String strM574a = c0022av.m574a();
                            String strM584b4 = strM574a != null ? strM574a : AppState.m584b(594984);
                            String strM130h = m130h(c0022av.m554b(262852));
                            if (strM130h != null) {
                                C0006af c0006afM111f = m111f(strM130h);
                                if (StringUtils.m3a(594926, strM584b4)) {
                                    if (c0006afM111f == null) {
                                        AbstractC0046q abstractC0046q = this.f334D;
                                        C0006af c0006af = new C0006af(this, strM130h, m129a(c0022av, strM130h), null);
                                        c0006af.f43b = true;
                                        c0006afM111f = c0006af;
                                        abstractC0046q.m1401b((Object) c0006af);
                                    }
                                    c0006afM111f.m146a(strM584b4, c0022av);
                                    C0034e.m925a(3);
                                    m1072a(strM130h, 0L, AppState.m584b(1031));
                                } else if (c0006afM111f != null) {
                                    c0006afM111f.m146a(strM584b4, c0022av);
                                }
                            }
                        } else if (StringUtils.m3a(464488, str)) {
                            String strM130h2 = m130h(c0022av.m554b(262852));
                            if (m111f(strM130h2) != null) {
                                StringBuffer stringBufferM1217h = C0040k.m1217h();
                                XmlElement c0022avM562f = c0022av.m562f(464558);
                                if (c0022avM562f != null && (strM11a2 = StringUtils.m11a(c0022avM562f.f173c)) != null) {
                                    stringBufferM1217h.append(strM11a2).append('\n');
                                }
                                XmlElement c0022avM562f2 = c0022av.m562f(267946);
                                if (c0022avM562f2 != null && (strM11a = StringUtils.m11a(c0022avM562f2.f173c)) != null) {
                                    stringBufferM1217h.append(strM11a);
                                }
                                String strM1215a = C0040k.m1215a(stringBufferM1217h);
                                if (strM1215a.length() > 0) {
                                    m1072a(strM130h2, 0L, strM1215a);
                                }
                            }
                        } else if (StringUtils.m3a(464495, str)) {
                            m1065J();
                        } else if (StringUtils.m3a(136604, c0022av.f171a)) {
                            if (c0022av.m576d(267810, 857625) == null || !StringUtils.m3a(196633, c0022av.m574a())) {
                                z = false;
                            } else {
                                XmlElement c0022avM577b = c0022av.m577b();
                                c0022avM577b.f172b = null;
                                m91a(c0022avM577b);
                                z = true;
                            }
                            if (!z && !m126c(c0022av)) {
                                if (c0022av.m576d(267762, 2102742) == null || !StringUtils.m3a(398982, c0022av.m574a())) {
                                    z2 = false;
                                } else {
                                    m92b(XmlElement.m550a(136604).m570i(198841).m573c(461668, 2299382));
                                    this.f323s = 70;
                                    z2 = true;
                                }
                                if (!z2) {
                                    if (this.f323s == 70 && StringUtils.m3a(398982, c0022av.m574a())) {
                                        m92b(XmlElement.m550a(136604).m570i(196633).m573c(333360, 1054101));
                                        this.f323s = 80;
                                        z3 = true;
                                    } else {
                                        z3 = false;
                                    }
                                    if (!z3) {
                                        XmlElement c0022avM576d2 = c0022av.m576d(333360, 1119653);
                                        if (c0022avM576d2 == null || !StringUtils.m3a(196633, c0022av.m574a())) {
                                            z4 = false;
                                        } else {
                                            c0022avM576d2.m551a(262601, 1119195).m551a(459728, 1375).m551a(133230, 264455);
                                            m91a(c0022av.m577b());
                                            z4 = true;
                                        }
                                        if (!z4 && (c0022avM576d = c0022av.m576d(333360, 1054101)) != null) {
                                            String strM574a2 = c0022av.m574a();
                                            if (StringUtils.m3a(198841, strM574a2)) {
                                                m127d(c0022avM576d);
                                                XmlElement c0022avM577b2 = c0022av.m577b();
                                                c0022avM577b2.f172b = null;
                                                m91a(c0022avM577b2);
                                            } else if (StringUtils.m3a(398982, strM574a2)) {
                                                m1067K();
                                                m127d(c0022avM576d);
                                                if (Utils.m541c(this.f313i) == 0) {
                                                    this.f313i.addElement(new C0036g(this, 1, AppState.m584b(459528)));
                                                }
                                                this.f322r = 100;
                                                m103b(this.f325u);
                                                this.f323s = 100;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    C0015ao.f153g = true;
                    C0015ao.f152f = true;
                    break;
                }
                break;
        }
        if (this.f324t != 0 && this.f320p != null && this.f320p.m1131a() == 0) {
            this.f322r = 0;
            m1061F();
            this.f324t = mo89g();
        }
        if (this.f330z <= 0 || !C0015ao.m306a(this.f331A)) {
            return;
        }
        m90a(new byte[]{32});
    }

    /* renamed from: b */
    private void m98b(Throwable th) {
        C0029bb.m784a(this, th.toString());
        m1061F();
        this.f324t = mo89g();
    }

    /* renamed from: b */
    public final void m99b(String str, int i) {
        if (m1056C()) {
            m91a(XmlElement.m550a(530016).m559a(131590, str).m570i(i == 0 ? 594926 : i == 1 ? 660462 : 791532).m552a(XmlElement.m550a(267628).m569h(2037073).m553a((Object) this.f339I)));
        } else {
            C0029bb.m778d((Object) AppState.m584b(299));
        }
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final void mo100c(int i) {
        int i2;
        switch (i) {
            case 0:
                i2 = 1;
                break;
            case 1:
                i2 = 4;
                break;
            case 2:
                i2 = 2;
                break;
            case 3:
                i2 = 5;
                break;
            case 4:
                i2 = 3;
                break;
            default:
                mo120l();
                return;
        }
        if (m1056C()) {
            m101h(i2);
            return;
        }
        this.f325u = i2;
        if (m1055B()) {
            return;
        }
        mo914a_(0);
    }

    /* renamed from: h */
    private final void m101h(int i) {
        if (mo83f()) {
            i = 1;
        }
        this.f324t = i;
        XmlElement c0022avM550a = XmlElement.m550a(530016);
        int i2 = 0;
        switch (i) {
            case 1:
                i2 = 642;
                break;
            case 2:
                c0022avM550a.m551a(267927, 267829);
                i2 = 644;
                break;
            case 3:
                c0022avM550a.m570i(594975);
                break;
            case 4:
                c0022avM550a.m551a(267927, 265215);
                i2 = 643;
                break;
            case 5:
                c0022avM550a.m551a(267927, 202299);
                i2 = 645;
                break;
            case 6:
                c0022avM550a.m551a(267927, 136761);
                i2 = 648;
                break;
        }
        if (i2 != 0) {
            c0022avM550a.m571a(AppState.m584b(530137), AppState.m584b(65747));
            c0022avM550a.m551a(394658, i2);
            c0022avM550a.m572b(267628, 2037073).m553a((Object) this.f339I);
        }
        m91a(c0022avM550a);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo102a(String str, String str2) {
        int iMo102a = super.mo102a(str, str2);
        if (iMo102a != 0) {
            return iMo102a;
        }
        if (mo83f()) {
            this.f37e = AppState.f181d;
            return 0;
        }
        this.f33b = AppState.f181d;
        return 0;
    }

    /* renamed from: b */
    public final int m103b(int i) {
        this.f325u = i;
        if (m1056C()) {
            m101h(i);
            return 0;
        }
        if (m1055B()) {
            return 487;
        }
        return mo914a_(0);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final int mo104c(AbstractC0041l abstractC0041l) {
        return 1032;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: d */
    public final int mo105d(AbstractC0041l abstractC0041l) {
        return 1032;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: e */
    public final int mo106e(AbstractC0041l abstractC0041l) {
        return 1032;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final AbstractC0041l mo107b(String str) {
        return null;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: h */
    public int mo108h() {
        if (this.f322r < 1 || this.f322r >= 100) {
            return m131d(this.f324t);
        }
        return 382;
    }

    /* renamed from: u */
    private void m109u() {
        m90a(new ByteBuffer().m1310c(8131775).m1314d(mo84j()).m1310c(136911).m1339k());
    }

    @Override // p000.AbstractC0037h
    /* renamed from: p */
    public int mo110p() {
        return 398518;
    }

    /* renamed from: f */
    private C0006af m111f(String str) {
        return (C0006af) m1069c((Object) str);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo112a(AbstractC0041l abstractC0041l, Object[] objArr) {
        int iMo112a = super.mo112a(abstractC0041l, objArr);
        return 0 != iMo112a ? iMo112a : m117a(((C0006af) abstractC0041l).f38a, (String) objArr[0], m1080g(abstractC0041l).f398f);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo113a(AbstractC0041l abstractC0041l, AbstractC0046q abstractC0046q, AbstractC0046q abstractC0046q2) {
        int iMo113a = super.mo113a(abstractC0041l, abstractC0046q, abstractC0046q2);
        return 0 != iMo113a ? iMo113a : m117a(((C0006af) abstractC0041l).f38a, abstractC0041l.f376u, abstractC0046q2.f398f);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo114a(AbstractC0041l abstractC0041l) {
        if (!m1056C()) {
            return 299;
        }
        AppState.f177b[1316] = new Object[]{m81r(), ((C0006af) abstractC0041l).m151f()};
        this.f319o--;
        return m92b(XmlElement.m550a(136604).m570i(196633).m559a(131590, abstractC0041l.mo135a()).m573c(333452, 661030));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final int mo115b(Object obj) {
        return 0;
    }

    /* renamed from: k */
    public final int m116k() {
        if (!m1056C()) {
            C0029bb.m778d((Object) AppState.m584b(299));
            return 0;
        }
        String strM522f = Utils.m522f(AppState.m584b(1296));
        m117a(strM522f, Utils.m522f(AppState.m584b(1297)), ((AbstractC0046q) AppState.m614m(1324).elementAt(AppState.m586d(1507))).f398f);
        m99b(strM522f, 0);
        m99b(strM522f, 1);
        return 0;
    }

    /* renamed from: a */
    private final int m117a(String str, String str2, String str3) {
        XmlElement c0022avM569h = XmlElement.m550a(333360).m569h(1054101);
        XmlElement c0022avM559a = XmlElement.m550a(267942).m559a(202421, str).m559a(262601, str2).m559a(792248, str2 == null ? AppState.m584b(399049) : null);
        if (str3 != null && !StringUtils.m3a(459528, str3)) {
            c0022avM559a.m571a(AppState.m584b(333508), str3);
        }
        return m92b(XmlElement.m550a(136604).m570i(198841).m552a(c0022avM569h.m552a(c0022avM559a)));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final int mo118b(AbstractC0041l abstractC0041l) {
        if (m1056C()) {
            m117a(abstractC0041l.mo135a(), (String) null, (String) null);
            return 0;
        }
        C0029bb.m778d((Object) AppState.m584b(299));
        return 0;
    }

    /* renamed from: a */
    public final int m119a(C0006af c0006af, int i) {
        if (!m1056C()) {
            return 299;
        }
        String str = c0006af.f38a;
        String str2 = c0006af.f376u;
        AbstractC0046q abstractC0046qM1080g = m1080g(c0006af);
        m117a(str, str2, (abstractC0046qM1080g == this.f335E || c0006af.f43b) ? AppState.m584b(459528) : abstractC0046qM1080g.f398f);
        return i;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: l */
    public final int mo120l() {
        int iMo120l = super.mo120l();
        if (0 != iMo120l) {
            return iMo120l;
        }
        m1061F();
        this.f324t = mo89g();
        this.f32g = null;
        this.f36h = null;
        this.f323s = 0;
        m93s();
        return 0;
    }

    /* renamed from: g */
    private C0036g m121g(String str) {
        C0036g c0036g;
        Vector vector = this.f313i;
        int iM541c = Utils.m541c(vector);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            c0036g = (C0036g) vector.elementAt(iM541c);
        } while (!StringUtils.m6a(str, c0036g.f398f));
        return c0036g;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo122a(String str) {
        int iMo122a = super.mo122a(str);
        if (0 != iMo122a) {
            return iMo122a;
        }
        if (m121g(str) != null) {
            return 0;
        }
        this.f313i.addElement(new C0036g(this, 1, str));
        return 0;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo123a(AbstractC0046q abstractC0046q) {
        int iMo123a = super.mo123a(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        this.f313i.removeElement(abstractC0046q);
        return 0;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo124a(AbstractC0046q abstractC0046q, String str) {
        int iMo124a = super.mo124a(abstractC0046q, str);
        if (0 != iMo124a) {
            return iMo124a;
        }
        if (Utils.m541c(abstractC0046q.f397e) != 0) {
            return 1032;
        }
        abstractC0046q.m1403c(str);
        return 0;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo125a(AbstractC0041l abstractC0041l, String str, long j) {
        int iMo125a = super.mo125a(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.f327w++;
        return m91a(XmlElement.m550a(464488).m559a(131590, abstractC0041l.mo135a()).m570i(265215).m552a(XmlElement.m550a(267946).m553a((Object) str)).m573c(398993, 2430320));
    }

    /* renamed from: c */
    private final boolean m126c(XmlElement c0022av) {
        if (c0022av.m576d(333350, 661030) == null) {
            return false;
        }
        if (!StringUtils.m3a(398982, c0022av.m574a())) {
            if (!StringUtils.m3a(333441, c0022av.m574a())) {
                return false;
            }
            try {
                Object[] objArrM609l = AppState.m609l(1316);
                if (((String) objArrM609l[0]).equals(c0022av.m554b(131550))) {
                    AppState.f177b[1315] = ((C0042m) objArrM609l[1]).m1297y(c0022av.toString());
                }
                return true;
            } catch (Throwable unused) {
                return true;
            }
        }
        try {
            Object[] objArrM609l2 = AppState.m609l(1316);
            if (((String) objArrM609l2[0]).equals(c0022av.m554b(131550))) {
                C0042m c0042mM1297y = ((C0042m) objArrM609l2[1]).m1297y(C0040k.m1215a(m133a(C0040k.m1217h(), c0022av)));
                Image imageM132e = m132e(c0022av);
                if (imageM132e != null) {
                    c0042mM1297y.put(C0034e.m967e(25), imageM132e);
                }
                AppState.f177b[1315] = c0042mM1297y;
            }
            return true;
        } catch (Throwable unused2) {
            return true;
        }
    }

    /* renamed from: d */
    private final void m127d(XmlElement c0022av) {
        Vector vector = this.f313i;
        Vector vector2 = c0022av.f172b;
        int iM541c = Utils.m541c(vector2);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                return;
            }
            XmlElement c0022av2 = (XmlElement) vector2.elementAt(iM541c);
            if (StringUtils.m3a(267942, c0022av2.f171a)) {
                String strM554b = c0022av2.m554b(202421);
                String strM554b2 = c0022av2.m554b(792248);
                c0022av2.m554b(202403);
                String strM554b3 = c0022av2.m554b(262601);
                boolean zM3a = StringUtils.m3a(399049, strM554b2);
                if (strM554b3 == null) {
                    strM554b3 = strM554b;
                }
                String strM575c = c0022av2.m575c(AppState.m584b(333508));
                String strM584b = strM575c;
                if (!Utils.m535l(strM575c)) {
                    strM584b = AppState.m584b(459528);
                }
                C0006af c0006af = (C0006af) m1069c((Object) strM554b);
                m1074a(c0006af, zM3a);
                if (!zM3a) {
                    C0036g c0036gM121g = m121g(strM584b);
                    C0036g c0036g = c0036gM121g;
                    if (c0036gM121g == null) {
                        C0036g c0036g2 = new C0036g(this, 1, strM584b);
                        c0036g = c0036g2;
                        vector.addElement(c0036g2);
                    }
                    C0006af c0006af2 = new C0006af(this, strM554b, strM554b3, strM554b2);
                    c0036g.m1401b((Object) c0006af2);
                    c0006af2.m147a(c0006af);
                }
            }
        }
    }

    /* renamed from: m */
    public String mo128m() {
        return this.f340J;
    }

    /* renamed from: a */
    private static String m129a(XmlElement c0022av, String str) {
        try {
            return StringUtils.m11a(c0022av.m562f(267628).f173c);
        } catch (Throwable unused) {
            return str;
        }
    }

    /* renamed from: h */
    private static String m130h(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf(47);
        return iIndexOf <= 0 ? str : StringUtils.m13b(str, iIndexOf);
    }

    /* renamed from: d */
    public static final int m131d(int i) {
        switch (i) {
            case 0:
                return 381;
            case 1:
                return 383;
            case 2:
                return 16318847;
            case 3:
                return 16515455;
            case 4:
                return 16384383;
            case 5:
                return 16449919;
            default:
                return 16580991;
        }
    }

    /* renamed from: e */
    private final Image m132e(XmlElement c0022av) {
        Image imageM132e;
        String strM11a;
        if (StringUtils.m3a(398966, c0022av.f171a) && (strM11a = StringUtils.m11a(c0022av.f173c)) != null) {
            String strM534k = Utils.m534k(strM11a);
            if (Utils.m535l(strM534k)) {
                try {
                    return C0034e.m986d(strM534k).m1348r();
                } catch (Throwable unused) {
                }
            }
        }
        int iM541c = Utils.m541c(c0022av.f172b);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            imageM132e = m132e(c0022av.m564g(iM541c));
        } while (imageM132e == null);
        return imageM132e;
    }

    /* renamed from: a */
    private final StringBuffer m133a(StringBuffer stringBuffer, XmlElement c0022av) {
        if (!StringUtils.m3a(333436, c0022av.f171a)) {
            String strM11a = StringUtils.m11a(c0022av.f173c);
            if (strM11a != null) {
                String strM534k = Utils.m534k(strM11a);
                if (Utils.m535l(strM534k)) {
                    stringBuffer.append(strM534k).append('\n');
                }
            }
            for (int i = 0; i < Utils.m541c(c0022av.f172b); i++) {
                m133a(stringBuffer, c0022av.m564g(i));
            }
        }
        return stringBuffer;
    }
}
