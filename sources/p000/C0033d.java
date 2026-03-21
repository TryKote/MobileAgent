package p000;

import java.util.Hashtable;
import java.util.Vector;

/* renamed from: d */
/* loaded from: MobileAgent_3.9.jar:d.class */
public final class C0033d extends AbstractC0037h {

    /* renamed from: a */
    public int f269a;

    /* renamed from: b */
    public int f270b;

    /* renamed from: K */
    private byte[] f271K;

    /* renamed from: c */
    public String[] f272c;

    /* renamed from: d */
    public int f273d;

    /* renamed from: e */
    public int f274e;

    /* renamed from: f */
    public final Hashtable f275f;

    /* renamed from: g */
    public final Hashtable f276g;

    /* renamed from: h */
    public final Hashtable f277h;

    /* renamed from: L */
    private int f278L;

    /* renamed from: M */
    private int f279M;

    /* renamed from: N */
    private int f280N;

    public C0033d(int i, String str, String str2) {
        super(i, str, str2);
        this.f324t = -1;
        this.f325u = 0;
        this.f278L = 4;
        C0016ap c0016ap = new C0016ap(this, 0, AppState.m584b(1039));
        c0016ap.f399g = true;
        this.f334D = c0016ap;
        this.f275f = new Hashtable();
        this.f276g = new Hashtable();
        this.f277h = new Hashtable();
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo80a() {
        return 1;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo102a(String str, String str2) {
        int iMo102a = super.mo102a(str, str2);
        if (iMo102a != 0) {
            return iMo102a;
        }
        return 0;
    }

    public C0033d(ByteBuffer c0043n) {
        super(c0043n);
        this.f278L = this.f325u >>> 16;
        if (this.f278L <= 0 || this.f278L > 5) {
            this.f278L = 4;
        }
        this.f325u &= 65535;
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                C0016ap c0016ap = new C0016ap(this, c0043n);
                c0016ap.f399g = true;
                this.f334D = c0016ap;
                this.f275f = new Hashtable();
                this.f276g = new Hashtable();
                this.f277h = new Hashtable();
                return;
            }
            m1083b((AbstractC0046q) new C0016ap(this, c0043n));
        }
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final AbstractC0037h mo82a(ByteBuffer c0043n, boolean z, boolean z2) {
        this.f325u = (this.f325u & 65535) + (this.f278L << 16);
        return super.mo82a(c0043n, z, z2);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final AbstractC0046q mo85b() {
        return new C0016ap(this, -1, AppState.m584b(1040));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final AbstractC0046q mo86c() {
        return new C0016ap(this, -2, AppState.m584b(1042));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: d */
    public final AbstractC0046q mo87d() {
        return new C0016ap(this, -3, AppState.m584b(1041));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: e */
    public final AbstractC0046q mo88e() {
        return new C0016ap(this, -4, AppState.m584b(1043));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a_ */
    public final int mo914a_(int i) {
        int iMo914a_ = super.mo914a_(i);
        if (iMo914a_ != 0) {
            return iMo914a_;
        }
        return 0;
    }

    /* renamed from: f */
    public final int m915f() {
        if (this.f332B == 0) {
            return 256;
        }
        return 268 + this.f332B;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: g */
    public final int mo89g() {
        m1061F();
        this.f331A = 0L;
        this.f330z = 0L;
        m1068L();
        this.f333C.removeAllElements();
        return -1;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: h */
    public final int mo108h() {
        if (this.f322r >= 1 && this.f322r < 100) {
            return 265;
        }
        int iM915f = m915f();
        switch (this.f324t) {
            case -1:
                return 255;
            case 1:
                return 16318464 | iM915f;
            case 2:
                return 16449536 | iM915f;
            case 4:
                return 16580608 | iM915f;
            case 16:
                return 16646144 | iM915f;
            case 32:
                return 16384000 | iM915f;
            case 256:
                return 16515072 | iM915f;
            case 8193:
                return 17104896 | iM915f;
            case 12288:
                return 16842752 | iM915f;
            case 16384:
                return 16908288 | iM915f;
            case 20480:
                return 16973824 | iM915f;
            case 24576:
                return 17039360 | iM915f;
            default:
                return iM915f;
        }
    }

    /* renamed from: a */
    public final ByteBuffer m916a(Object obj) {
        if (!m1055B()) {
            return null;
        }
        Object[] objArr = (Object[]) obj;
        ByteBuffer c0043n = (ByteBuffer) objArr[0];
        objArr[0] = C0034e.m967e(c0043n.m1356x());
        this.f333C.addElement(obj);
        return c0043n;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: i */
    public final void mo97i() throws Throwable {
        AbstractC0041l abstractC0041lM1069c;
        if (this.f322r <= 0) {
            m1061F();
            this.f324t = -1;
        }
        switch (this.f322r) {
            case 0:
                this.f318n.m1301b();
                if (this.f272c != null) {
                    this.f272c[0] = null;
                }
                this.f272c = null;
                this.f323s = 0;
                break;
            case 1:
                C0015ao.f153g = true;
                this.f323s = 10;
                this.f280N = C0034e.m972o();
                if (this.f280N != -1 || this.f280N == 1) {
                    this.f322r = 2;
                    break;
                }
                break;
            case 2:
                if (this.f280N != 0) {
                    Vector vectorM439R = C0015ao.m439R();
                    int iM541c = Utils.m541c(vectorM439R);
                    while (true) {
                        iM541c--;
                        if (iM541c >= 0) {
                            AbstractC0037h abstractC0037h = (AbstractC0037h) vectorM439R.elementAt(iM541c);
                            if (abstractC0037h.m1056C()) {
                                this.f322r = 3;
                            } else if (!abstractC0037h.m1055B()) {
                                vectorM439R.removeElementAt(iM541c);
                            }
                        } else {
                            break;
                        }
                    }
                    if (Utils.m541c(vectorM439R) == 0) {
                        C0029bb.m778d((Object) AppState.m584b(479));
                        this.f322r = 0;
                    }
                    C0040k.m1212a(vectorM439R);
                    break;
                } else {
                    this.f322r = 3;
                    break;
                }
            case 3:
                new RunnableC0055z(31, new Object[]{this, C0034e.m967e(0), this.f315k, m1064I()});
                this.f323s = 20;
                this.f322r = 5;
                C0015ao.f153g = true;
                break;
            case 5:
                if (this.f272c != null) {
                    this.f323s = 70;
                    C0015ao.f153g = true;
                    this.f319o = 28179;
                    this.f271K = C0034e.m986d(this.f272c[2]).m1339k();
                    this.f269a = Integer.parseInt(this.f272c[0]);
                    this.f320p = new C0039j(this.f272c[1]);
                    this.f322r = 6;
                    this.f272c = null;
                    break;
                }
                break;
            case 6:
                if (this.f320p.m1131a() != 2) {
                    if (this.f320p.m1131a() <= 0) {
                        m1061F();
                        this.f324t = mo89g();
                        break;
                    }
                } else {
                    this.f322r = 7;
                    break;
                }
                break;
            case 7:
                this.f320p.m1132a(this.f318n);
                ByteBuffer c0043nM1350t = this.f318n.m1350t();
                if (c0043nM1350t != null) {
                    C0015ao.f153g = true;
                    this.f323s = 85;
                    C0015ao.m421a((AbstractC0037h) this, c0043nM1350t);
                    if (c0043nM1350t.m1331i(1) == 1) {
                        long j = AppState.m587e(1536) ? 25000L : 60000L;
                        this.f330z = j;
                        this.f331A = System.currentTimeMillis() + j;
                        m1085R();
                        byte[] bArr = this.f271K;
                        m1053d(C0015ao.m326a(this, 1).m1359o(1).m1357m(6).m1357m(bArr.length).m1302a(bArr).m1362y());
                        this.f271K = null;
                        m1053d(C0015ao.m375a(this));
                        m1053d(C0015ao.m464a(this, 1026, new ByteBuffer().m1310c(1051079)));
                        m1053d(m916a(new Object[]{C0015ao.m464a(this, 286, new ByteBuffer().m1357m(6).m1357m(4).m1359o(268435456 | m919j()).m1310c(2689260)), C0034e.m967e(17)}));
                        this.f273d = 0;
                        m1053d(m916a(new Object[]{C0015ao.m464a(this, 4868, (ByteBuffer) null), C0034e.m967e(6)}));
                        m1053d(StringUtils.m18a(this, this.f269a));
                        this.f322r = 8;
                        break;
                    }
                }
                break;
        }
        if (this.f322r < 8) {
            return;
        }
        this.f320p.m1132a(this.f318n);
        if (this.f280N == 1) {
            Vector vectorM440S = C0015ao.m440S();
            if (Utils.m541c(vectorM440S) == 0) {
                m1061F();
                this.f324t = mo89g();
                return;
            }
            C0040k.m1212a(vectorM440S);
        }
        while (true) {
            ByteBuffer c0043nM1350t2 = this.f318n.m1350t();
            ByteBuffer c0043nM1299a = c0043nM1350t2;
            if (c0043nM1350t2 == null) {
                if (this.f324t != -1 && this.f320p != null && this.f320p.m1131a() == 0) {
                    m1061F();
                    this.f324t = mo89g();
                }
                if (this.f330z > 0 && m1056C() && C0015ao.m306a(this.f331A)) {
                    m1052c(C0015ao.m326a(this, 5));
                    return;
                }
                return;
            }
            C0015ao.m421a((AbstractC0037h) this, c0043nM1299a);
            this.f323s = 90;
            if (c0043nM1299a.m1331i(1) == 2) {
                int iM1331i = (c0043nM1299a.m1331i(6) << 24) | (c0043nM1299a.m1331i(8) << 16) | (c0043nM1299a.m1331i(7) << 8) | c0043nM1299a.m1331i(9);
                int iM1356x = c0043nM1299a.m1356x();
                int iM1351l = c0043nM1299a.m1351l(10);
                int iM1351l2 = c0043nM1299a.m1351l(10);
                c0043nM1299a.m1329g(16);
                if (iM1351l2 == 32768) {
                    c0043nM1299a.m1329g(c0043nM1299a.m1353u());
                }
                c0043nM1299a = c0043nM1299a.m1299a();
                switch (iM1331i) {
                    case 271:
                        Vector vector = this.f333C;
                        int size = vector.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                C0031bd.m882a(this, c0043nM1299a, iM1356x, 0);
                                break;
                            } else {
                                Object[] objArr = (Object[]) vector.elementAt(size);
                                if (((Integer) objArr[1]).intValue() == 17) {
                                    objArr[0] = C0034e.m967e(iM1356x);
                                }
                            }
                        }
                    case 287:
                        try {
                            int iM1355w = c0043nM1299a.m1355w();
                            int iM1355w2 = c0043nM1299a.m1355w();
                            String strM17c = AppState.f181d;
                            boolean z = false;
                            byte[] bArr2 = AppState.f176a;
                            while (c0043nM1299a.f384b > 0) {
                                int iM1353u = c0043nM1299a.m1353u();
                                int iM1353u2 = c0043nM1299a.m1353u();
                                byte[] bArr3 = iM1353u2 > 0 ? new byte[iM1353u2] : null;
                                byte[] bArr4 = bArr3;
                                if (bArr3 != null) {
                                    c0043nM1299a.m1347c(bArr4);
                                }
                                if (iM1353u == 1) {
                                    strM17c = StringUtils.m17c(new String(bArr4));
                                } else if (iM1353u == 2) {
                                    z = true;
                                } else if (iM1353u == 3) {
                                    bArr2 = bArr4;
                                }
                                if (iM1353u != 3) {
                                    C0040k.m1209a(bArr4);
                                }
                            }
                            C0028ba c0028ba = (C0028ba) C0015ao.m440S().elementAt(0);
                            c0028ba.m1052c(C0034e.m981a(c0028ba, this, iM1355w, iM1355w2, strM17c, z, bArr2));
                            break;
                        } catch (Throwable unused) {
                            break;
                        }
                    case 779:
                        m917a(c0043nM1299a.m1363z(), c0043nM1299a);
                        break;
                    case 780:
                        m917a(c0043nM1299a.m1363z(), c0043nM1299a);
                        break;
                    case 1025:
                        C0031bd.m881b(this, iM1356x);
                        break;
                    case 1031:
                        C0029bb.m821a(this, c0043nM1299a);
                        break;
                    case 1035:
                        long jM1341m = c0043nM1299a.m1341m();
                        c0043nM1299a.m1353u();
                        m1073a(c0043nM1299a.m1363z(), jM1341m, 64);
                        break;
                    case 1036:
                        long jM1341m2 = c0043nM1299a.m1341m();
                        c0043nM1299a.m1353u();
                        m1073a(c0043nM1299a.m1363z(), jM1341m2, 128);
                        break;
                    case 1044:
                        c0043nM1299a.m1329g(10);
                        String strM1363z = c0043nM1299a.m1363z();
                        if (c0043nM1299a.m1353u() != 0) {
                            m1070d(strM1363z);
                            break;
                        } else {
                            m1071e(strM1363z);
                            break;
                        }
                    case 4870:
                        if (this.f273d == 0) {
                            m1067K();
                        }
                        C0031bd.m882a(this, c0043nM1299a, iM1356x, iM1351l);
                        break;
                    case 4878:
                        C0031bd.m882a(this, c0043nM1299a, iM1356x, 0);
                        break;
                    case 4885:
                        AbstractC0041l abstractC0041lM1069c2 = m1069c((Object) c0043nM1299a.m1363z());
                        if (null != abstractC0041lM1069c2) {
                            abstractC0041lM1069c2.mo145h();
                            break;
                        }
                        break;
                    case 4889:
                        C0034e.m925a(3);
                        m1072a(c0043nM1299a.m1363z(), 0L, c0043nM1299a.m1364A());
                        break;
                    case 4891:
                        String strM1363z2 = c0043nM1299a.m1363z();
                        byte bM1344o = c0043nM1299a.m1344o();
                        m1072a(strM1363z2, 0L, C0040k.m1215a(C0040k.m1217h().append(AppState.m584b(483)).append(AppState.m584b(bM1344o == 1 ? 484 : 485)).append(c0043nM1299a.m1364A())));
                        if (bM1344o == 1 && null != (abstractC0041lM1069c = m1069c((Object) strM1363z2))) {
                            abstractC0041lM1069c.mo145h();
                            break;
                        }
                        break;
                    case 4892:
                        m1072a(c0043nM1299a.m1363z(), 0L, AppState.m584b(480));
                        break;
                    case 5377:
                        C0029bb.m778d((Object) C0040k.m1215a(C0040k.m1217h().append(AppState.m584b(481)).append(1501).append('/').append(c0043nM1299a.m1353u()).append(AppState.m584b(482))));
                        C0031bd.m881b(this, iM1356x);
                        break;
                    case 5379:
                        C0031bd.m882a(this, c0043nM1299a, iM1356x, iM1351l);
                        break;
                }
                C0015ao.f152f = true;
            } else {
                if (c0043nM1299a.m1331i(1) == 4) {
                    C0015ao.m386a(this, c0043nM1299a);
                    C0015ao.f152f = true;
                }
            }
            c0043nM1299a.m1301b();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x0130  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void m917a(String str, ByteBuffer c0043n) {
        int iM511a;
        int i = 255;
        C0009ai c0009ai = (C0009ai) m1069c((Object) str);
        if (c0009ai == null || c0009ai.mo143m()) {
            return;
        }
        boolean z = c0009ai.f371p;
        c0009ai.f373r = 255;
        c0009ai.f371p = false;
        c0009ai.f62g = false;
        c0009ai.f63h = false;
        c0009ai.f375t = true;
        try {
            c0043n.m1329g(2);
            int iM1353u = c0043n.m1353u();
            for (int i2 = 0; i2 < iM1353u; i2++) {
                int iM1353u2 = c0043n.m1353u();
                int iM1353u3 = c0043n.m1353u();
                if (iM1353u2 == 6) {
                    int iM1355w = c0043n.m1355w() & 65535;
                    if (iM1355w == 0) {
                        i = 256;
                        c0009ai.f373r = i;
                        iM1353u3 -= 4;
                        c0009ai.f371p = true;
                    } else {
                        if (iM1355w == 19) {
                            i = 16449792;
                        } else if (iM1355w == 17) {
                            i = 16646400;
                        } else if ((iM1355w & 24576) == 24576) {
                            i = 17039360;
                        } else if ((iM1355w & 20480) == 20480) {
                            i = 16973824;
                        } else if ((iM1355w & 16384) == 16384) {
                            i = 16908288;
                        } else if ((iM1355w & 12288) == 12288) {
                            i = 16842752;
                        } else if ((iM1355w & 8192) == 8192) {
                            i = 17104896;
                        } else if ((iM1355w & 256) == 256) {
                            i = 16515328;
                        } else if ((iM1355w & 32) == 32) {
                            i = 16384256;
                        } else if ((iM1355w & 16) == 16) {
                            i = 16646400;
                        } else if ((iM1355w & 4) == 4) {
                            i = 16580864;
                        } else if ((iM1355w & 2) == 2) {
                            i = 16449792;
                        } else if ((iM1355w & 1) == 1) {
                            i = 16318720;
                        }
                        c0009ai.f373r = i;
                        iM1353u3 -= 4;
                        c0009ai.f371p = true;
                    }
                } else if (iM1353u2 == 13) {
                    byte[] bArrM581a = AppState.m581a(905);
                    byte[] bArrM581a2 = AppState.m581a(906);
                    byte[] bArrM581a3 = AppState.m581a(908);
                    byte[] bArr = c0043n.f383a;
                    int i3 = c0043n.f385c;
                    for (int i4 = 0; i4 < iM1353u3; i4 += 16) {
                        int i5 = i3 + i4;
                        for (int i6 = 0; i6 < 576; i6 += 16) {
                            if (Utils.m509a(bArrM581a3, i6, bArr, i5, 16)) {
                                c0009ai.f373r &= -65536;
                                c0009ai.f373r |= (i6 >> 4) + 269;
                            }
                        }
                        if (Utils.m509a(bArrM581a, 0, bArr, i5, 16)) {
                            c0009ai.f62g = true;
                        } else if (Utils.m509a(bArrM581a2, 0, bArr, i5, 16)) {
                            c0009ai.f63h = true;
                        }
                    }
                } else if (iM1353u2 == 29) {
                    while (0 < iM1353u3 - 4) {
                        int iM1353u4 = c0043n.m1353u();
                        int iM1353u5 = c0043n.m1353u() & 255;
                        int i7 = (iM1353u3 - 2) - 2;
                        if ((iM1353u5 & 128) != 0 || i7 < (iM1353u5 & 128)) {
                            c0043n.m1329g(iM1353u5);
                            iM1353u3 = i7 - iM1353u5;
                        } else if (iM1353u4 == 14) {
                            byte[] bArr2 = new byte[iM1353u5];
                            c0043n.m1347c(bArr2);
                            String strM17c = StringUtils.m17c(new String(bArr2));
                            if (strM17c.startsWith(C0040k.m1221a(28270022039266153L)) && (iM511a = Utils.m511a(StringUtils.m15c(strM17c, 7), 0, 23, -1)) >= 0) {
                                c0009ai.f373r &= -65536;
                                c0009ai.f373r |= iM511a + 269;
                            }
                            C0040k.m1209a(bArr2);
                            iM1353u3 = i7 - iM1353u5;
                        } else {
                            c0043n.m1329g(iM1353u5);
                            iM1353u3 = i7 - iM1353u5;
                        }
                    }
                }
                c0043n.m1329g(iM1353u3);
            }
        } catch (Throwable unused) {
        }
        c0009ai.m1228A();
        if (z || !c0009ai.f371p) {
            return;
        }
        C0034e.m925a(1);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo125a(AbstractC0041l abstractC0041l, String str, long j) {
        ByteBuffer c0043nM464a;
        int iMo125a = super.mo125a(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.f327w++;
        C0009ai c0009ai = (C0009ai) abstractC0041l;
        int i = c0009ai.f62g ? 1 : 0;
        int i2 = c0009ai.f63h ? 2 : 1;
        ByteBuffer c0043nM1373h = new ByteBuffer().m1323a(j).m1357m(i2).m1373h(c0009ai.f57c);
        ByteBuffer c0043n = new ByteBuffer();
        if (i2 == 1) {
            if (i == 1) {
                c0043n.m1357m(2).m1357m(0).m1374i(str);
            } else {
                c0043n.m1360p(0).m1371g(str);
            }
            c0043nM1373h.m1357m(2).m1357m(i + 9 + c0043n.f384b).m1357m(1281).m1357m(i + 1);
            if (i == 1) {
                c0043nM1373h.m1357m(262);
            } else {
                c0043nM1373h.m1321f(1);
            }
            c0043nM464a = C0015ao.m464a(this, 1030, c0043nM1373h.m1357m(257).m1326b(c0043n).m1357m(6).m1357m(0));
        } else {
            if (i == 1) {
                c0043n.m1377k(str);
            } else {
                c0043n.m1371g(str);
            }
            c0043n.m1321f(0);
            int i3 = c0043n.f384b;
            int i4 = i3 - (i == 1 ? 0 : 42);
            c0043nM464a = C0015ao.m464a(this, 1030, c0043nM1373h.m1357m(5).m1357m(i4 + 143).m1357m(0).m1323a(j).m1310c(906).m1357m(10).m1357m(2).m1357m(1).m1357m(15).m1357m(0).m1357m(10001).m1357m(i4 + 103).m1358n(27).m1358n(8).m1360p(0).m1360p(0).m1360p(0).m1360p(0).m1357m(0).m1360p(3).m1321f(0).m1357m(0).m1360p(14).m1360p(0).m1360p(0).m1360p(0).m1358n(1).m1358n(m919j()).m1358n(1).m1358n(i3).m1325a(c0043n).m1310c(i == 0 ? 526807 : 3279327).m1357m(3).m1357m(0));
        }
        return m1052c(c0043nM464a);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo112a(AbstractC0041l abstractC0041l, Object[] objArr) {
        int iMo112a = super.mo112a(abstractC0041l, objArr);
        if (0 != iMo112a) {
            return iMo112a;
        }
        C0009ai c0009ai = (C0009ai) abstractC0041l;
        String str = (String) objArr[0];
        return m1052c(m916a(new Object[]{C0015ao.m464a(this, 4873, c0009ai.m180a(3, str, c0009ai.f56b)), C0034e.m967e(0), c0009ai, str}));
    }

    /* renamed from: b */
    public final int m918b(int i) {
        if (i == 256) {
            m923d(3);
        } else if (this.f278L == 3) {
            m923d(4);
        }
        this.f325u = i;
        if (m1056C()) {
            m1052c(C0031bd.m857a(this, this.f274e));
            m1052c(m916a(new Object[]{C0015ao.m464a(this, 286, new ByteBuffer().m1357m(6).m1357m(4).m1359o(268435456 | m919j())), C0034e.m967e(17)}));
            return m1052c(C0015ao.m375a(this));
        }
        if (m1055B()) {
            return 487;
        }
        return mo914a_(0);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo114a(AbstractC0041l abstractC0041l) {
        if (!m1056C()) {
            return 299;
        }
        C0009ai c0009ai = (C0009ai) abstractC0041l;
        AppState.f177b[1316] = C0042m.m1254b(this).m1284u(c0009ai.f57c);
        return m1052c(StringUtils.m18a(this, Utils.m510a((Object) c0009ai.f57c)));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo113a(AbstractC0041l abstractC0041l, AbstractC0046q abstractC0046q, AbstractC0046q abstractC0046q2) {
        int iMo113a = super.mo113a(abstractC0041l, abstractC0046q, abstractC0046q2);
        if (0 != iMo113a) {
            return iMo113a;
        }
        m1052c(C0034e.m961a(this));
        C0009ai c0009ai = (C0009ai) abstractC0041l;
        return m1052c(m916a(new Object[]{C0015ao.m464a(this, 4874, c0009ai.m180a(2, c0009ai.f376u, c0009ai.f56b)), C0034e.m967e(10), c0009ai, abstractC0046q, abstractC0046q2}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo124a(AbstractC0046q abstractC0046q, String str) {
        int iMo124a = super.mo124a(abstractC0046q, str);
        if (iMo124a != 0) {
            return iMo124a;
        }
        C0016ap c0016ap = (C0016ap) abstractC0046q;
        return m1052c(m916a(new Object[]{C0015ao.m464a(this, 4873, c0016ap.m465a(str, -1, -1)), C0034e.m967e(1), c0016ap, str}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo122a(String str) {
        int iMo122a = super.mo122a(str);
        if (iMo122a != 0) {
            return iMo122a;
        }
        m1052c(C0034e.m961a(this));
        return m1052c(C0034e.m937a(this, str));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo123a(AbstractC0046q abstractC0046q) {
        int iMo123a = super.mo123a(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        m1052c(C0034e.m961a(this));
        C0016ap c0016ap = (C0016ap) abstractC0046q;
        return m1052c(m916a(new Object[]{C0015ao.m464a(this, 4874, c0016ap.m465a(c0016ap.f398f, -1, -1)), C0034e.m967e(2), c0016ap}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final int mo118b(AbstractC0041l abstractC0041l) {
        C0009ai c0009ai = (C0009ai) abstractC0041l;
        int iMo118b = super.mo118b((AbstractC0041l) c0009ai);
        if (0 != iMo118b) {
            return iMo118b;
        }
        if (c0009ai.mo143m()) {
            m1074a((AbstractC0041l) c0009ai, true);
            return 0;
        }
        if (c0009ai.mo142k()) {
            m1052c(C0029bb.m792c(this, c0009ai));
        }
        if (c0009ai.mo140i()) {
            m1052c(C0029bb.m790a(this, c0009ai));
        }
        if (c0009ai.mo141j()) {
            m1052c(C0029bb.m791b(this, c0009ai));
        }
        return m1052c(m916a(new Object[]{C0015ao.m464a(this, 4874, c0009ai.m180a(2, c0009ai.f376u, c0009ai.f56b)), C0034e.m967e(5), c0009ai}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo734a(String str, String str2, String str3, AbstractC0046q abstractC0046q, boolean z) {
        int iMo734a = super.mo734a(str, str2, str3, abstractC0046q, z);
        if (0 != iMo734a) {
            return iMo734a;
        }
        m1052c(C0015ao.m464a(this, 4884, new ByteBuffer().m1373h(str).m1360p(0)));
        C0009ai c0009ai = (C0009ai) m1069c((Object) str);
        if (null != c0009ai && !c0009ai.mo143m()) {
            return m1052c(C0029bb.m753a(this, c0009ai, str3));
        }
        m1052c(C0034e.m961a(this));
        C0016ap c0016ap = (C0016ap) abstractC0046q;
        ByteBuffer c0043nM1357m = new ByteBuffer().m1361f(str).m1357m(c0016ap.f157a);
        int iM920k = m920k();
        return m1052c(m916a(new Object[]{C0015ao.m464a(this, 4872, c0043nM1357m.m1357m(iM920k).m1357m(0).m1326b(new ByteBuffer().m1357m(102).m1357m(0).m1357m(347).m1357m(1).m1321f(32).m1357m(305).m1376j(str2))), C0034e.m967e(14), str, str2, c0016ap, C0034e.m967e(iM920k), str3}));
    }

    /* renamed from: j */
    public final int m919j() {
        switch (this.f325u) {
            case 2:
                return 19;
            case 4:
                return 5;
            case 16:
                return 17;
            default:
                return this.f325u & 65535;
        }
    }

    /* renamed from: k */
    public final int m920k() {
        boolean z;
        int iM520a;
        do {
            z = false;
            iM520a = (Utils.m520a() & 28671) + 4096;
            if (iM520a == this.f274e) {
                z = true;
            } else {
                Vector vector = this.f313i;
                int size = vector.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    C0016ap c0016ap = (C0016ap) vector.elementAt(size);
                    if (c0016ap.f157a == iM520a) {
                        z = true;
                    }
                    Vector vector2 = c0016ap.f397e;
                    int size2 = vector2.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        C0009ai c0009ai = (C0009ai) vector2.elementAt(size2);
                        if (c0009ai.f55a == iM520a || c0009ai.f59d == iM520a || c0009ai.f60e == iM520a || c0009ai.f61f == iM520a) {
                            z = true;
                        }
                    }
                }
            }
        } while (z);
        return iM520a;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final int mo104c(AbstractC0041l abstractC0041l) {
        if (abstractC0041l.mo143m()) {
            return 310;
        }
        return m1052c(C0029bb.m792c(this, (C0009ai) abstractC0041l));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: d */
    public final int mo105d(AbstractC0041l abstractC0041l) {
        C0009ai c0009ai = (C0009ai) abstractC0041l;
        if (c0009ai.mo141j() && !c0009ai.mo140i()) {
            m1052c(C0029bb.m791b(this, c0009ai));
        }
        return m1052c(C0029bb.m790a(this, c0009ai));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: e */
    public final int mo106e(AbstractC0041l abstractC0041l) {
        C0009ai c0009ai = (C0009ai) abstractC0041l;
        if (!c0009ai.mo141j() && c0009ai.mo140i()) {
            m1052c(C0029bb.m790a(this, c0009ai));
        }
        return m1052c(C0029bb.m791b(this, c0009ai));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: l */
    public final int mo120l() {
        if (this.f272c != null) {
            this.f272c[0] = null;
        }
        this.f272c = null;
        int iMo120l = super.mo120l();
        if (0 != iMo120l) {
            return iMo120l;
        }
        m1052c(C0015ao.m326a(this, 4));
        m1061F();
        this.f324t = mo89g();
        return 0;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final int mo115b(Object obj) {
        String[] strArr = (String[]) obj;
        ByteBuffer c0043nM1358n = new ByteBuffer().m1360p(this.f269a).m1358n(2000).m1357m(0).m1358n(1375);
        String str = strArr[0];
        if (str.length() > 0) {
            c0043nM1358n.m1357m(13825).m1358n(4).m1360p(Utils.m510a((Object) str));
        } else {
            c0043nM1358n.m1357m(12290).m1358n(1).m1321f(strArr[1].length()).m1372a(21505, strArr[2]).m1372a(16385, strArr[3]).m1372a(18945, strArr[4]).m1372a(24065, strArr[5]).m1372a(36865, strArr[6]).m1372a(9730, strArr[7]);
        }
        ByteBuffer c0043nM1357m = new ByteBuffer().m1357m(1);
        int i = c0043nM1358n.f384b;
        return m1052c(m916a(new Object[]{C0015ao.m464a(this, 5378, c0043nM1357m.m1357m(i + 2).m1358n(i).m1325a(c0043nM1358n)), C0034e.m967e(9)}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final AbstractC0041l mo107b(String str) {
        AbstractC0046q abstractC0046q = this.f334D;
        C0009ai c0009ai = new C0009ai(this, -1, -1, str, str, true);
        abstractC0046q.m1401b((Object) c0009ai);
        ByteBuffer c0043nM1360p = new ByteBuffer().m1360p(this.f269a).m1358n(2000).m1357m(0).m1358n(1375).m1357m(13825).m1358n(4).m1360p(Utils.m510a((Object) str));
        ByteBuffer c0043nM1357m = new ByteBuffer().m1357m(1);
        int i = c0043nM1360p.f384b;
        m1052c(m916a(new Object[]{C0015ao.m464a(this, 5378, c0043nM1357m.m1357m(i + 2).m1358n(i).m1325a(c0043nM1360p)), C0034e.m967e(21)}));
        return c0009ai;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final void mo100c(int i) {
        switch (i) {
            case 0:
                m918b(0);
                break;
            case 1:
                m918b(32);
                break;
            case 2:
                m918b(1);
                break;
            case 3:
                m918b(2);
                break;
            case 4:
                m918b(256);
                break;
            default:
                mo120l();
                break;
        }
    }

    /* renamed from: m */
    public final int m921m() {
        if (this.f279M > 0 && this.f279M <= 5) {
            this.f278L = this.f279M;
            this.f279M = 0;
        }
        return this.f278L;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: n */
    public final int mo922n() {
        return 369 + this.f278L;
    }

    /* renamed from: d */
    public final int m923d(int i) {
        this.f279M = i;
        if (!m1056C()) {
            return 0;
        }
        m1052c(C0031bd.m857a(this, this.f274e));
        return 4;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: o */
    public final void mo924o() {
        super.mo924o();
        this.f326v = 0;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: p */
    public final int mo110p() {
        return 197381;
    }
}
