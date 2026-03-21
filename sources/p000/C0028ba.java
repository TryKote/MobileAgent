package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ba */
/* loaded from: MobileAgent_3.9.jar:ba.class */
public final class C0028ba extends AbstractC0037h implements ListItem {

    /* renamed from: a */
    public String f225a;

    /* renamed from: b */
    public String f226b;

    /* renamed from: c */
    public boolean f227c;

    /* renamed from: d */
    public Vector f228d;

    /* renamed from: e */
    public boolean f229e;

    /* renamed from: f */
    public String f230f;

    /* renamed from: g */
    public C0003ac f231g;

    /* renamed from: h */
    public boolean f232h;

    /* renamed from: K */
    private SizeCache f233K;

    /* renamed from: L */
    private Vector f234L;

    public C0028ba(int i, String str, String str2) {
        super(i, str, str2);
        this.f324t = 0;
        this.f325u = 1;
        C0010aj c0010aj = new C0010aj(this, -1, 102, AppState.m584b(1039));
        c0010aj.f399g = true;
        this.f334D = c0010aj;
        this.f231g = new C0003ac();
        this.f232h = true;
        this.f233K = new SizeCache();
        this.f234L = C0040k.m1213g();
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo80a() {
        return 0;
    }

    public C0028ba(ByteBuffer c0043n) {
        super(c0043n);
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                break;
            } else {
                this.f313i.addElement(new C0010aj(this, c0043n));
            }
        }
        C0010aj c0010aj = new C0010aj(this, c0043n);
        c0010aj.f399g = true;
        this.f334D = c0010aj;
        ByteBuffer c0043n2 = new ByteBuffer();
        int iM1328e2 = c0043n.m1328e();
        if (iM1328e2 > 0) {
            c0043n2.m1303a(c0043n.f383a, c0043n.f385c, iM1328e2);
            c0043n.m1329g(iM1328e2);
        }
        try {
        } catch (Throwable unused) {
            this.f230f = null;
            this.f228d = null;
        }
        if (c0043n2.f384b == 0) {
            throw new RuntimeException();
        }
        this.f230f = c0043n2.m1335e((String) null);
        c0043n2.m1334g();
        this.f228d = C0040k.m1213g();
        int iM1328e3 = c0043n2.m1328e();
        for (int i = 0; i < iM1328e3; i++) {
            this.f228d.addElement(C0052w.m1411b(c0043n2));
        }
        if (c0043n2.m1353u() != 21554) {
            throw new RuntimeException();
        }
        m744b(false);
        this.f231g = new C0003ac();
        this.f232h = true;
        this.f233K = new SizeCache();
        this.f234L = C0040k.m1213g();
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final AbstractC0037h mo82a(ByteBuffer c0043n, boolean z, boolean z2) {
        super.mo82a(c0043n, z, z2);
        if (z2) {
            c0043n.m1327c(m716a(z));
        } else {
            c0043n.m1360p(0);
        }
        return this;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final void mo714b(ByteBuffer c0043n) {
        c0043n.m1360p(13).m1360p(this.f326v).m1360p(this.f327w).m1360p(this.f328x);
        C0003ac c0003ac = this.f231g;
        boolean zM59c = c0003ac.m59c();
        c0043n.m1322a(zM59c);
        if (zM59c) {
            c0043n.m1308a(c0003ac.f13a).m1308a(c0003ac.f14b).m1308a(c0003ac.f15c).m1309b(c0003ac.f16d).m1308a(c0003ac.f17e).m1308a(c0003ac.f18f).m1308a(c0003ac.f20h).m1308a(c0003ac.f19g).m1359o(c0003ac.f21i).m1322a(c0003ac.f24l);
        }
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final void mo715a(ByteBuffer c0043n) {
        int iM1328e = c0043n.m1328e();
        if (iM1328e == 12) {
            this.f326v = c0043n.m1328e();
            this.f327w = c0043n.m1328e();
            this.f328x = c0043n.m1328e();
            c0043n.m1328e();
            return;
        }
        if (iM1328e == 13) {
            this.f326v = c0043n.m1328e();
            this.f327w = c0043n.m1328e();
            this.f328x = c0043n.m1328e();
            this.f231g = C0003ac.m58b(c0043n);
        }
    }

    /* renamed from: a */
    private final ByteBuffer m716a(boolean z) {
        ByteBuffer c0043n = new ByteBuffer();
        if (z) {
            try {
                int iM742V = m742V();
                if (this.f230f == null || this.f225a == null || iM742V < 3) {
                    throw new Throwable();
                }
                c0043n.m1300a(20480);
                c0043n.m1309b(this.f230f);
                c0043n.m1360p(0);
                c0043n.m1360p(iM742V - 1);
                for (int i = 0; i < iM742V; i++) {
                    C0052w c0052w = (C0052w) this.f228d.elementAt(i);
                    if (c0052w != m746W()) {
                        c0052w.m1410a(c0043n);
                    }
                }
                c0043n.m1357m(21554);
            } catch (Throwable unused) {
                c0043n.m1301b();
            }
        }
        return c0043n;
    }

    /* renamed from: f */
    public final C0035f m717f(String str) {
        return (C0035f) m1069c((Object) str);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final AbstractC0046q mo85b() {
        return new C0010aj(this, -1, 101, AppState.m584b(1040));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final AbstractC0046q mo86c() {
        return new C0010aj(this, -1, 104, AppState.m584b(1042));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: d */
    public final AbstractC0046q mo87d() {
        return new C0010aj(this, -1, 103, AppState.m584b(1041));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: e */
    public final AbstractC0046q mo88e() {
        return new C0010aj(this, -1, 105, AppState.m584b(1043));
    }

    /* renamed from: f */
    public final C0010aj m718f() {
        return (C0010aj) this.f313i.elementAt(0);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: g */
    public final int mo89g() {
        m1061F();
        this.f331A = 0L;
        this.f330z = 0L;
        m1068L();
        this.f333C.removeAllElements();
        return 0;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: h */
    public final int mo108h() {
        if (this.f322r >= 1 && this.f322r < 100) {
            return 153;
        }
        switch (this.f324t) {
            case 0:
                return 155;
            case 1:
                return 156;
            case 2:
                return 157;
            case 3:
                return 158;
            case 260:
                return 159;
            case 516:
                return 160;
            default:
                return 157 + (this.f324t >> 8);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:107:0x0608  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x060c A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:111:0x0617  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x061b A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:115:0x067c A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:119:0x06a3 A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    /* JADX WARN: Removed duplicated region for block: B:130:0x06e7 A[Catch: Throwable -> 0x073d, all -> 0x0748, TryCatch #3 {Throwable -> 0x073d, all -> 0x0748, blocks: (B:84:0x0544, B:86:0x0553, B:87:0x055a, B:88:0x0585, B:92:0x0599, B:96:0x05a8, B:102:0x05e5, B:97:0x05b8, B:100:0x05ca, B:101:0x05dd, B:105:0x05ef, B:113:0x063b, B:115:0x067c, B:117:0x068c, B:130:0x06e7, B:119:0x06a3, B:112:0x061b, B:108:0x060c), top: B:236:0x0544 }] */
    @Override // p000.AbstractC0037h
    /* renamed from: i */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void mo97i() throws Throwable {
        int i;
        String strM1336h;
        String str;
        int i2;
        long jM491a;
        int i3;
        String strM1336h2 = null;
        switch (this.f322r) {
            case 0:
                this.f318n.m1301b();
                this.f323s = 0;
                break;
            case 1:
                this.f323s = 20;
                this.f319o = 0;
                this.f320p = new C0039j(AppState.m584b(1114895));
                this.f322r = 2;
                C0015ao.f153g = true;
                break;
            case 2:
                this.f323s = 30;
                if (this.f320p.m1131a() == 2) {
                    this.f323s = 40;
                    this.f322r = 3;
                    C0015ao.f153g = true;
                    break;
                }
                break;
            case 3:
                this.f320p.m1132a(this.f318n);
                int i4 = this.f318n.f384b;
                int i5 = i4;
                if (i4 > 0) {
                    C0015ao.m419a((AbstractC0037h) this, i5);
                    this.f323s = 60;
                    StringBuffer stringBufferM1217h = C0040k.m1217h();
                    while (true) {
                        int i6 = i5;
                        i5 = i6 - 1;
                        if (i6 <= 0) {
                            this.f320p.f349c = 3;
                            this.f320p = new C0039j(C0040k.m1215a(stringBufferM1217h));
                            this.f322r = 4;
                            C0015ao.f153g = true;
                            break;
                        } else {
                            char cM1344o = (char) this.f318n.m1344o();
                            if (Utils.m498a(cM1344o)) {
                                stringBufferM1217h.append(cM1344o);
                            }
                        }
                    }
                }
                break;
            case 4:
                if (this.f320p.m1131a() == 2) {
                    this.f323s = 80;
                    m1053d(C0015ao.m377a(this));
                    this.f322r = 5;
                    C0015ao.f153g = true;
                    break;
                }
                break;
        }
        if (this.f322r < 5) {
            return;
        }
        this.f320p.m1132a(this.f318n);
        while (true) {
            ByteBuffer c0043nM1349s = this.f318n.m1349s();
            if (c0043nM1349s == null) {
                if (c0043nM1349s == null && this.f324t != 0 && this.f320p != null && this.f320p.m1131a() == 0) {
                    m1061F();
                    this.f324t = mo89g();
                }
                if (this.f330z <= 0 || !C0015ao.m306a(this.f331A)) {
                    return;
                }
                m1052c(C0015ao.m321a(this, 4102, (ByteBuffer) null));
                return;
            }
            C0015ao.m421a((AbstractC0037h) this, c0043nM1349s);
            int iM1330h = c0043nM1349s.m1330h(12);
            int iM1330h2 = c0043nM1349s.m1330h(8);
            c0043nM1349s.m1329g(44);
            switch (iM1330h) {
                case 4098:
                    long jM503b = Utils.m503b(c0043nM1349s.m1328e(), AppState.m587e(1536) ? 25 : 45) * 1000;
                    this.f330z = jM503b;
                    this.f331A = System.currentTimeMillis() + jM503b;
                    ByteBuffer c0043nM1308a = new ByteBuffer().m1308a(this.f315k).m1308a(m1064I());
                    boolean zM587e = AppState.m587e(105);
                    m1053d(C0015ao.m321a(this, 4216, c0043nM1308a.m1360p(zM587e ? -1 : 22).m1308a(zM587e ? null : new ByteBuffer().m1310c(1642077).m1312e(2229599).m1317c()).m1310c(1704823).m1308a(C0036g.m1017d()).m1325a(C0036g.m1016a(this))));
                    this.f322r = 6;
                    break;
                case 4100:
                    this.f323s = 85;
                    m1085R();
                    break;
                case 4101:
                    C0015ao.m461a(this, c0043nM1349s);
                    break;
                case 4105:
                    C0038i.m1110a(this, c0043nM1349s, 0L);
                    break;
                case 4111:
                    int iM1328e = c0043nM1349s.m1328e();
                    String strM1334g = c0043nM1349s.m1334g();
                    c0043nM1349s.m1335e((String) null);
                    c0043nM1349s.m1335e((String) null);
                    C0035f c0035fM717f = m717f(c0043nM1349s.m1338j());
                    if (c0035fM717f != null && !c0035fM717f.mo143m()) {
                        c0043nM1349s.m1328e();
                        String strM1334g2 = c0043nM1349s.m1334g();
                        int i7 = c0035fM717f.f299f;
                        c0035fM717f.f299f = iM1328e;
                        c0035fM717f.f301h = strM1334g2;
                        c0035fM717f.f373r = C0015ao.m349a(iM1328e, strM1334g);
                        c0035fM717f.f371p = iM1328e != 0;
                        if (iM1328e == 0) {
                            c0035fM717f.m999p();
                        }
                        c0035fM717f.f375t = true;
                        c0035fM717f.m1228A();
                        if (i7 == 0 && iM1328e != 0) {
                            C0034e.m925a(1);
                            break;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                case 4114:
                    C0031bd.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4115:
                    m1063H();
                    break;
                case 4117:
                    while (c0043nM1349s.f384b > 0) {
                        String strM1334g3 = c0043nM1349s.m1334g();
                        if (StringUtils.m3a(852768, strM1334g3)) {
                            m1054c(c0043nM1349s.m1335e((String) null));
                        } else if (StringUtils.m3a(983853, strM1334g3)) {
                            C0029bb.m759a(this, Utils.m510a((Object) c0043nM1349s.m1335e((String) null)), (String) null, (String) null);
                        } else if (StringUtils.m3a(983868, strM1334g3)) {
                            String strM1335e = c0043nM1349s.m1335e((String) null);
                            this.f226b = StringUtils.m13b(strM1335e, strM1335e.indexOf(58));
                        } else if (StringUtils.m3a(656203, strM1334g3)) {
                            c0043nM1349s.m1334g();
                            this.f227c = true;
                        } else if (StringUtils.m3a(1114965, strM1334g3)) {
                            c0043nM1349s.m1329g((((((((c0043nM1349s.m1328e() - (4 + c0043nM1349s.m1334g().length())) - (4 + c0043nM1349s.m1334g().length())) - (4 + c0043nM1349s.m1334g().length())) - (4 + (c0043nM1349s.m1335e((String) null).length() << 1))) - (4 + c0043nM1349s.m1334g().length())) - (4 + c0043nM1349s.m1334g().length())) - (4 + c0043nM1349s.m1334g().length())) - (4 + c0043nM1349s.m1334g().length()));
                        } else {
                            c0043nM1349s.m1334g();
                        }
                    }
                    break;
                case 4122:
                    C0031bd.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4124:
                    C0031bd.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4125:
                    m1052c(C0015ao.m321a(this, 4126, new ByteBuffer().m1360p(c0043nM1349s.m1328e()).m1360p(c0043nM1349s.m1328e())));
                    try {
                        int i8 = this.f329y;
                        this.f329y = i8 + 1;
                        if (0 != i8) {
                            AppState.m594c(1449, 0);
                        }
                        Hashtable hashtable = new Hashtable();
                        String strM1214a = null;
                        String strM529d = Utils.m529d(c0043nM1349s.m1334g(), '\r');
                        int length = strM529d.length();
                        StringBuffer stringBufferM1217h2 = C0040k.m1217h();
                        boolean z = false;
                        int i9 = 0;
                        while (i9 < length) {
                            char cCharAt = strM529d.charAt(i9);
                            if (!z) {
                                if (cCharAt == '\n' && stringBufferM1217h2.length() == 0) {
                                    C0040k.m1215a(stringBufferM1217h2);
                                    String str2 = (String) hashtable.get(AppState.m584b(1379315));
                                    int i10 = str2 != null ? -1 : Integer.parseInt(str2);
                                    i = i10;
                                    strM1336h = i10 >= 0 ? null : C0034e.m986d(StringUtils.m15c((String) hashtable.get(AppState.m584b(460837)), 13)).m1336h();
                                    str = (String) hashtable.get(AppState.m584b(396269));
                                    i2 = Integer.parseInt((String) hashtable.get(AppState.m584b(789512)), 16);
                                    jM491a = Utils.m491a((String) hashtable.get(AppState.m584b(264254)));
                                    i3 = 1;
                                    if ((i2 & 128) != 0) {
                                        String strM15c = StringUtils.m15c(strM529d, i9);
                                        if ((i2 & 2097160) == 0) {
                                            strM1336h2 = C0034e.m986d(strM15c).m1336h();
                                        } else {
                                            strM1336h2 = strM15c;
                                            i3 = 0;
                                        }
                                    } else {
                                        int iM627a = AppState.m627a(strM529d, 57408234938722L);
                                        strM1336h2 = C0034e.m986d(StringUtils.m12a(strM529d, iM627a + 6, strM529d.indexOf(AppState.m584b(134123), iM627a))).m1336h();
                                    }
                                    if (i != -1 || (i >= 0 && i <= 5 && i != 1 && i != 3)) {
                                        C0038i.m1110a(this, new ByteBuffer().m1360p(0).m1360p(i2 | 4 | 128).m1308a((String) hashtable.get(AppState.m584b(264203))).m1307a(strM1336h2, i3).m1360p(0).m1360p(0).m1360p(i).m1309b(strM1336h).m1308a(str), jM491a);
                                    }
                                    AppState.m594c(1449, 1);
                                    break;
                                } else if (cCharAt == ':') {
                                    strM1214a = C0040k.m1214a(stringBufferM1217h2, false);
                                    z = true;
                                    i9++;
                                } else {
                                    stringBufferM1217h2.append(cCharAt);
                                }
                            } else if (cCharAt == '\n') {
                                hashtable.put(strM1214a, C0040k.m1214a(stringBufferM1217h2, false));
                                z = false;
                            } else {
                                stringBufferM1217h2.append(cCharAt);
                            }
                            i9++;
                        }
                        C0040k.m1215a(stringBufferM1217h2);
                        String str22 = (String) hashtable.get(AppState.m584b(1379315));
                        int i10_2 = str22 != null ? -1 : Integer.parseInt(str22);
                        i = i10_2;
                        strM1336h = i10_2 >= 0 ? null : C0034e.m986d(StringUtils.m15c((String) hashtable.get(AppState.m584b(460837)), 13)).m1336h();
                        str = (String) hashtable.get(AppState.m584b(396269));
                        i2 = Integer.parseInt((String) hashtable.get(AppState.m584b(789512)), 16);
                        jM491a = Utils.m491a((String) hashtable.get(AppState.m584b(264254)));
                        i3 = 1;
                        if ((i2 & 128) != 0) {
                        }
                        if (i != -1) {
                            C0038i.m1110a(this, new ByteBuffer().m1360p(0).m1360p(i2 | 4 | 128).m1308a((String) hashtable.get(AppState.m584b(264203))).m1307a(strM1336h2, i3).m1360p(0).m1360p(0).m1360p(i).m1309b(strM1336h).m1308a(str), jM491a);
                            AppState.m594c(1449, 1);
                        }
                    } catch (Throwable th) {
                        AppState.m594c(1449, 1);
                        throw th;
                    }
                    break;
                case 4129:
                    C0035f c0035fM717f2 = m717f(c0043nM1349s.m1334g());
                    if (null == c0035fM717f2) {
                        break;
                    } else {
                        c0035fM717f2.f298e &= -2;
                        break;
                    }
                case 4133:
                    C0031bd.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4136:
                    C0031bd.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4151:
                    C0038i.m1112a(this, c0043nM1349s);
                    break;
                case 4160:
                    C0031bd.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4163:
                    if (c0043nM1349s.m1328e() != 1 || c0043nM1349s.m1328e() <= 0) {
                        break;
                    } else {
                        String strM1334g4 = c0043nM1349s.m1334g();
                        int size = this.f234L.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                break;
                            }
                            SearchEntry c0050u = (SearchEntry) this.f234L.elementAt(size);
                            if (iM1330h2 == c0050u.f406a) {
                                this.f234L.removeElementAt(size);
                                int i11 = c0050u.f408c;
                                if (i11 == 1) {
                                    m732g(strM1334g4);
                                    C0015ao.m393a(this, strM1334g4);
                                } else if (i11 == 2) {
                                    C0042m c0042mM1251a = C0042m.m1251a(this);
                                    c0042mM1251a.m1262e(strM1334g4);
                                    AppState.f177b[1319] = c0042mM1251a;
                                    C0029bb.m778d(new C0029bb(5, null));
                                }
                            }
                        }
                    }
                    break;
                case 4168:
                    C0029bb.m759a(this, c0043nM1349s.m1328e(), c0043nM1349s.m1333f(), c0043nM1349s.m1333f());
                    break;
                case 4180:
                    String strM1334g5 = c0043nM1349s.m1334g();
                    String strM1334g6 = c0043nM1349s.m1334g();
                    if (!StringUtils.m3a(525167, strM1334g5)) {
                        break;
                    } else {
                        this.f231g.m54a(new XmlParser(strM1334g6).m47a());
                        m724j();
                        break;
                    }
                case 4182:
                    c0043nM1349s.m1328e();
                    break;
                case 4195:
                    int iM1328e2 = c0043nM1349s.m1328e();
                    String strM17c = StringUtils.m17c(c0043nM1349s.m1334g().toLowerCase());
                    long jM1341m = c0043nM1349s.m1341m();
                    int iM1328e3 = c0043nM1349s.m1328e();
                    String strM1335e2 = c0043nM1349s.m1335e((String) null);
                    C0035f c0035fM717f3 = m717f(strM17c);
                    if (c0035fM717f3 != null && !c0035fM717f3.mo143m()) {
                        if ((iM1328e2 & 2) == 0) {
                            if ((iM1328e2 & 5) != 0) {
                                if (AppState.m587e(244) && !StringUtils.m6a(strM1335e2, c0035fM717f3.f303i) && ((int) (System.currentTimeMillis() / 1000)) - iM1328e3 < 172800 && c0035fM717f3.m1241H() != jM1341m) {
                                    AppState.m601a(1237, (Object) c0035fM717f3.f380w);
                                    C0034e.m925a(6);
                                    c0035fM717f3.m1227c(2);
                                    c0035fM717f3.m1239a(16, strM1335e2, 0L, jM1341m);
                                    AbstractC0046q abstractC0046qM1080g = c0035fM717f3.f369o.m1080g(c0035fM717f3);
                                    if (abstractC0046qM1080g != null && abstractC0046qM1080g.f399g) {
                                        abstractC0046qM1080g.mo1397n();
                                    }
                                    c0035fM717f3.m1228A();
                                }
                                c0035fM717f3.f303i = strM1335e2;
                                break;
                            } else {
                                break;
                            }
                        } else {
                            c0035fM717f3.f304j = strM1335e2;
                            break;
                        }
                    } else {
                        break;
                    }
                case 4215:
                    m729a(c0043nM1349s.m1334g(), c0043nM1349s.m1345p());
                    break;
                case 4229:
                    if (!AppState.m579a()) {
                        break;
                    } else {
                        Vector vectorM1345p = c0043nM1349s.m1345p();
                        if (!vectorM1345p.isEmpty()) {
                            String[] strArrM55a = C0003ac.m55a((ByteBuffer) vectorM1345p.elementAt(0));
                            if (strArrM55a.length >= 8 && !this.f231g.m59c()) {
                                String str3 = strArrM55a[2];
                                if (StringUtils.m3a(525044, str3)) {
                                    m730b(strArrM55a[1], strArrM55a[0]);
                                } else if (StringUtils.m3a(590588, str3)) {
                                    String str4 = strArrM55a[1];
                                    String str5 = strArrM55a[0];
                                    String str6 = strArrM55a[6];
                                    String str7 = strArrM55a[7];
                                    try {
                                        C0003ac c0003ac = this.f231g;
                                        String strM584b = AppState.m584b(590588);
                                        String str8 = AppState.f181d;
                                        c0003ac.m53a(str5, str4, strM584b, str8, str8, str8, str6, str7);
                                    } catch (Throwable unused) {
                                        this.f231g.m61e();
                                    }
                                    this.f233K.f403a = -1;
                                    this.f231g.f16d = strArrM55a[3];
                                }
                                this.f231g.f24l = true;
                                if (C0015ao.m442U() != 10) {
                                    break;
                                } else {
                                    C0029bb.m778d((Object) AppState.m584b(786));
                                    break;
                                }
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
            }
            c0043nM1349s.m1301b();
            C0015ao.f152f = true;
        }
    }

    /* renamed from: a */
    public final ByteBuffer m719a(Object obj) {
        if (!m1056C()) {
            return null;
        }
        Object[] objArr = (Object[]) obj;
        ByteBuffer c0043n = (ByteBuffer) objArr[0];
        objArr[0] = C0034e.m967e(c0043n.m1330h(8));
        this.f333C.addElement(obj);
        return c0043n;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: O */
    public final Vector mo720O() {
        Vector vectorMo720O = super.mo720O();
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            C0035f c0035f = (C0035f) enumerationElements.nextElement();
            if (c0035f.mo996n() && !c0035f.mo142k()) {
                vectorMo720O.addElement(c0035f);
            }
        }
        return vectorMo720O;
    }

    /* renamed from: d */
    public final int m721d(int i) {
        String strM1215a;
        String strM584b;
        this.f325u = i;
        if (!m1056C()) {
            if (m1055B()) {
                return 487;
            }
            return mo914a_(0);
        }
        this.f324t = i;
        int i2 = this.f325u & 7;
        int i3 = 1225;
        switch (this.f325u) {
            case 1:
                i3 = 1225 - 1;
            case 2:
                i3++;
            case 3:
                i3 -= 3;
            case 260:
                i3--;
            case 516:
                strM1215a = AppState.m584b(i3);
                break;
            default:
                strM1215a = C0040k.m1215a(C0040k.m1217h().append(AppState.m584b(1226)).append(this.f325u >> 8));
                break;
        }
        switch (this.f325u) {
            case 1:
                strM584b = AppState.m584b(642);
                break;
            case 2:
                strM584b = AppState.m584b(644);
                break;
            case 3:
                strM584b = AppState.m584b(642);
                break;
            case 260:
                strM584b = AppState.m584b(643);
                break;
            case 516:
                strM584b = AppState.m584b(645);
                break;
            default:
                strM584b = AppState.m584b(151 + (this.f325u >> 8));
                break;
        }
        return m1052c(C0015ao.m321a(this, 4130, new ByteBuffer().m1360p(i2 != 3 ? i2 : -2147483647).m1308a(strM1215a).m1309b(strM584b).m1309b(AppState.f181d).m1360p(AppState.m587e(105) ? -1 : 22)));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo125a(AbstractC0041l abstractC0041l, String str, long j) {
        int iMo125a = super.mo125a(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.f327w++;
        return m1052c(C0036g.m1001a(this, (C0035f) abstractC0041l, str, j));
    }

    /* renamed from: a */
    private int m722a(int i, String[] strArr, C0003ac c0003ac) {
        if (!c0003ac.m59c() || c0003ac.f24l) {
            return 0;
        }
        String[] strArr2 = {c0003ac.f13a, c0003ac.f14b, c0003ac.f15c, c0003ac.f16d, c0003ac.f17e, c0003ac.f18f, c0003ac.f20h, c0003ac.f19g};
        m1052c(C0015ao.m321a(this, 4213, new ByteBuffer().m1360p(i).m1391b(strArr).m1308a(AppState.m584b(590694)).m1325a(new ByteBuffer().m1327c(new ByteBuffer().m1308a(strArr2[0]).m1308a(strArr2[1]).m1308a(strArr2[2]).m1309b(strArr2[3]).m1308a(strArr2[4]).m1308a(strArr2[5]).m1308a(strArr2[6]).m1308a(strArr2[7])))));
        return 0;
    }

    /* renamed from: a */
    private int m723a(String[] strArr, String str) {
        return m1052c(C0015ao.m321a(this, 4214, new ByteBuffer().m1391b(strArr).m1308a(str)));
    }

    /* renamed from: j */
    public final void m724j() {
        if (m1056C()) {
            int i = this.f231g.f21i;
            if (i == 1) {
                m722a(1, new String[0], this.f231g);
            } else if (i == 2) {
                m722a(0, new String[0], this.f231g);
            } else if (i == 3) {
                m722a(0, this.f231g.f22j, this.f231g);
            }
        }
    }

    /* renamed from: k */
    public final void m725k() {
        int i = this.f231g.f21i;
        this.f231g.f21i = 1;
        if (m1056C()) {
            if (i == 3) {
                m723a(this.f231g.f22j, AppState.m584b(590694));
            }
            m722a(1, new String[0], this.f231g);
        }
    }

    /* renamed from: m */
    public final void m726m() {
        int i = this.f231g.f21i;
        this.f231g.f21i = 2;
        if (m1056C()) {
            if (i == 3) {
                m723a(this.f231g.f22j, AppState.m584b(590694));
            }
            m722a(0, new String[0], this.f231g);
        }
    }

    /* renamed from: S */
    public final void m727S() {
        int i = this.f231g.f21i;
        this.f231g.f21i = 4;
        if (m1056C()) {
            if (i == 3) {
                m723a(this.f231g.f22j, AppState.m584b(590694));
            }
            m723a(new String[0], AppState.m584b(590694));
        }
    }

    /* renamed from: T */
    public final void m728T() {
        int i = this.f231g.f21i;
        this.f231g.f21i = 3;
        if (m1056C()) {
            if (i == 3) {
                m723a(this.f231g.f23k, AppState.m584b(590694));
            } else if (i == 1 || i == 2) {
                m723a(new String[0], AppState.m584b(590694));
            }
            m722a(0, this.f231g.f22j, this.f231g);
        }
    }

    /* renamed from: a */
    public final void m729a(String str, Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            ByteBuffer c0043n = (ByteBuffer) vector.elementAt(i);
            c0043n.m1328e();
            if (StringUtils.m3a(590694, c0043n.m1334g())) {
                String[] strArrM55a = C0003ac.m55a(c0043n);
                C0035f c0035fM717f = m717f(str);
                if (c0035fM717f != null) {
                    if (strArrM55a == null) {
                        c0035fM717f.m999p();
                    } else {
                        try {
                            c0035fM717f.f306l = new C0003ac();
                            c0035fM717f.f306l.m53a(strArrM55a[0], strArrM55a[1], strArrM55a[2], strArrM55a[3], strArrM55a[4], strArrM55a[5], strArrM55a[6], strArrM55a[7]);
                            c0035fM717f.f307m = true;
                        } catch (Throwable unused) {
                            c0035fM717f.m999p();
                        }
                        c0035fM717f.f308n.f403a = -1;
                    }
                }
            }
        }
        C0040k.m1212a(vector);
    }

    /* renamed from: b */
    public final void m730b(String str, String str2) {
        try {
            C0003ac c0003ac = this.f231g;
            String strM584b = AppState.m584b(525044);
            String str3 = AppState.f181d;
            c0003ac.m53a(str2, str, strM584b, str3, str3, str3, str3, str3);
        } catch (Throwable unused) {
            this.f231g.m61e();
        }
        this.f233K.f403a = -1;
    }

    /* renamed from: a */
    public final void m731a(C0014an c0014an) {
        try {
            C0003ac c0003ac = this.f231g;
            String strM810b = C0029bb.m810b(c0014an.f139g);
            String strM809a = C0029bb.m809a(c0014an.f138f);
            String strM584b = AppState.m584b(590588);
            String strM267a = c0014an.m267a();
            String str = AppState.f181d;
            c0003ac.m53a(strM810b, strM809a, strM584b, strM267a, str, str, StringUtils.m17c(Integer.toString(c0014an.f145m)), StringUtils.m17c(Integer.toString(c0014an.f144l)));
        } catch (Throwable unused) {
            this.f231g.m61e();
        }
        this.f233K.f403a = -1;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo124a(AbstractC0046q abstractC0046q, String str) {
        int iMo124a = super.mo124a(abstractC0046q, str);
        if (0 != iMo124a) {
            return iMo124a;
        }
        C0010aj c0010aj = (C0010aj) abstractC0046q;
        return m1052c(m719a(new Object[]{C0015ao.m321a(this, 4123, new ByteBuffer().m1360p(c0010aj.f74a).m1360p(c0010aj.f75b).m1360p(0).m1309b(str).m1309b(str).m1360p(0)), C0034e.m967e(1), c0010aj, str}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo122a(String str) {
        int iMo122a = super.mo122a(str);
        if (0 != iMo122a) {
            return iMo122a;
        }
        ByteBuffer c0043n = new ByteBuffer();
        int size = (this.f313i.size() << 24) | 2;
        return m1052c(m719a(new Object[]{C0015ao.m321a(this, 4121, c0043n.m1360p(size).m1306b(8).m1309b(str).m1306b(12)), C0034e.m967e(4), str, C0034e.m967e(size)}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo123a(AbstractC0046q abstractC0046q) {
        int iMo123a = super.mo123a(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        C0010aj c0010aj = (C0010aj) abstractC0046q;
        ByteBuffer c0043nM1360p = new ByteBuffer().m1360p(c0010aj.f74a).m1360p(c0010aj.f75b | 1).m1360p(0);
        String str = c0010aj.f398f;
        return m1052c(m719a(new Object[]{C0015ao.m321a(this, 4123, c0043nM1360p.m1309b(str).m1309b(str).m1360p(0)), C0034e.m967e(3), c0010aj}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final int mo118b(AbstractC0041l abstractC0041l) {
        int iMo118b = super.mo118b(abstractC0041l);
        if (0 != iMo118b) {
            return iMo118b;
        }
        if (abstractC0041l.mo143m()) {
            return m1074a(abstractC0041l, true);
        }
        C0035f c0035f = (C0035f) abstractC0041l;
        return m1052c(m719a(new Object[]{C0015ao.m321a(this, 4123, new ByteBuffer().m1360p(c0035f.f294a).m1360p(c0035f.f295b | 1).m1360p(c0035f.f296c).m1308a(c0035f.f297d).m1309b(c0035f.f376u).m1308a(c0035f.f300g)), C0034e.m967e(2), c0035f}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo114a(AbstractC0041l abstractC0041l) {
        return m732g(((C0035f) abstractC0041l).f297d);
    }

    /* renamed from: g */
    public final int m732g(String str) {
        return m1052c(C0015ao.m403a(this, str, 7));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final int mo115b(Object obj) {
        String[] strArr = (String[]) obj;
        ByteBuffer c0043n = new ByteBuffer();
        for (int i = 0; i < strArr.length; i++) {
            if (i != 9) {
                String str = strArr[i];
                if (Utils.m535l(str)) {
                    c0043n.m1360p(i).m1307a(str, (1 << i) & 28);
                }
            }
        }
        if (Utils.m535l(strArr[9])) {
            c0043n.m1360p(9).m1308a(strArr[9]);
        }
        return m1052c(m719a(new Object[]{C0015ao.m321a(this, 4137, c0043n), C0034e.m967e(8)}));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo112a(AbstractC0041l abstractC0041l, Object[] objArr) {
        int iMo112a = super.mo112a(abstractC0041l, objArr);
        if (0 != iMo112a) {
            return iMo112a;
        }
        String str = (String) objArr[0];
        int length = objArr.length - 1;
        String[] strArr = new String[length];
        for (int i = 0; i < length; i++) {
            strArr[i] = Utils.m532i((String) objArr[i + 1]);
        }
        C0035f c0035f = (C0035f) abstractC0041l;
        if (c0035f.mo990d() && length == 0) {
            return 709;
        }
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            C0035f c0035f2 = (C0035f) enumerationElements.nextElement();
            int i2 = length;
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                }
                if (c0035f2 != abstractC0041l && c0035f2.m994a(strArr[i2])) {
                    return 486;
                }
            }
        }
        String strM519a = Utils.m519a(strArr);
        return m1052c(m719a(new Object[]{C0015ao.m321a(this, 4123, new ByteBuffer().m1360p(c0035f.f294a).m1360p(c0035f.f295b).m1360p(c0035f.f296c).m1308a(c0035f.f297d).m1309b(str).m1308a(strM519a)), C0034e.m967e(0), c0035f, str, strM519a}));
    }

    /* renamed from: U */
    public final int m733U() {
        int i;
        Vector vector = this.f313i;
        int size = vector.size();
        for (int i2 = 0; i2 < 20; i2++) {
            for (i = 0; i <= size; i = i + 1) {
                if (i == size) {
                    return i2;
                }
                i = ((C0010aj) vector.elementAt(i)).f74a != i2 ? i + 1 : 0;
            }
        }
        return 0;
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo734a(String str, String str2, String str3, AbstractC0046q abstractC0046q, boolean z) {
        int iMo734a = super.mo734a(str, str2, str3, abstractC0046q, z);
        if (0 != iMo734a) {
            return iMo734a;
        }
        C0035f c0035fM717f = m717f(str);
        if (c0035fM717f == null || c0035fM717f.mo143m()) {
            m1052c(C0015ao.m395b(this, str));
            return m1052c(C0036g.m1024a(this, 0, str, str2, str3, (C0010aj) abstractC0046q, z));
        }
        m1052c(C0034e.m988a(this, c0035fM717f, (C0010aj) abstractC0046q));
        return m1052c(C0015ao.m321a(this, 4104, new ByteBuffer().m1360p(z ? 524300 : 12).m1308a(str).m1318a(new String[]{this.f339I, str3}).m1360p(0)));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: c */
    public final int mo104c(AbstractC0041l abstractC0041l) {
        C0035f c0035f = (C0035f) abstractC0041l;
        if (c0035f.mo143m()) {
            return m1052c(C0036g.m1024a(this, 48, c0035f.f297d, c0035f.f376u, AppState.f181d, m718f(), false));
        }
        int i = c0035f.f295b;
        return m1052c(C0034e.m987a(this, c0035f, (i & 16) != 0 ? i & (-49) : i | 16 | 32));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: d */
    public final int mo105d(AbstractC0041l abstractC0041l) {
        C0035f c0035f = (C0035f) abstractC0041l;
        int i = c0035f.f295b ^ 8;
        int i2 = i;
        if ((i & 8) != 0) {
            i2 &= -5;
        }
        return m1052c(C0034e.m987a(this, c0035f, i2));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: e */
    public final int mo106e(AbstractC0041l abstractC0041l) {
        C0035f c0035f = (C0035f) abstractC0041l;
        int i = c0035f.f295b ^ 4;
        int i2 = i;
        if ((i & 4) != 0) {
            i2 &= -9;
        }
        return m1052c(C0034e.m987a(this, c0035f, i2));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: f */
    public final int mo735f(AbstractC0041l abstractC0041l) {
        int iMo735f = super.mo735f(abstractC0041l);
        return 0 != iMo735f ? iMo735f : m1052c(C0015ao.m321a(this, 4104, new ByteBuffer().m1360p(1024).m1308a(((C0035f) abstractC0041l).f297d).m1360p(0).m1360p(0)));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: a */
    public final int mo113a(AbstractC0041l abstractC0041l, AbstractC0046q abstractC0046q, AbstractC0046q abstractC0046q2) {
        int iMo113a = super.mo113a(abstractC0041l, abstractC0046q, abstractC0046q2);
        return 0 != iMo113a ? iMo113a : m1052c(C0034e.m988a(this, (C0035f) abstractC0041l, (C0010aj) abstractC0046q2));
    }

    @Override // p000.AbstractC0037h
    /* renamed from: l */
    public final int mo120l() {
        int iMo120l = super.mo120l();
        if (0 != iMo120l) {
            return iMo120l;
        }
        m1052c(C0015ao.m321a(this, 4194, (ByteBuffer) null));
        m1061F();
        this.f324t = mo89g();
        return 0;
    }

    /* renamed from: k */
    private final String m736k(String str) {
        C0035f c0035fM717f = m717f(str);
        return c0035fM717f != null ? c0035fM717f.f376u : str;
    }

    /* renamed from: l */
    private final StringBuffer m737l(String str) {
        return Utils.m496a(C0040k.m1217h().append(m736k(str)), true).append('\n');
    }

    /* renamed from: a */
    public final void m738a(String str, String str2, String str3, String str4, long j) {
        C0035f c0035fM717f = m717f(str);
        C0035f c0035f = c0035fM717f;
        if (null == c0035fM717f) {
            String str5 = AppState.f181d;
            AbstractC0046q abstractC0046q = this.f334D;
            C0035f c0035f2 = new C0035f(this, 0, 65664, 3, str, str3, 0, 0, str5, str5, str5);
            abstractC0046q.m1401b((Object) c0035f2);
            if (this.f313i.size() > 0) {
                m1052c(C0036g.m1024a(this, 128, str, str3, str5, m718f(), false));
            }
            c0035f = c0035f2;
        }
        this.f328x++;
        c0035f.m1231a(j, m737l(str4).append(str2));
    }

    /* renamed from: a */
    public final void m739a(String str, String str2, String str3, String str4, ByteBuffer c0043n, long j) {
        C0035f c0035fM717f = m717f(str);
        if (null == c0035fM717f) {
            return;
        }
        this.f328x++;
        StringBuffer stringBufferAppend = m737l(str4).append(str2);
        c0043n.m1328e();
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                c0035fM717f.m1231a(j, stringBufferAppend);
                return;
            }
            Utils.m496a(stringBufferAppend.append(m736k(c0043n.m1334g())), iM1328e != 0);
        }
    }

    /* renamed from: h */
    public final void m740h(String str) {
        if (StringUtils.m6a(str, this.f315k) || m717f(str) != null) {
            return;
        }
        m741a(str, 16);
    }

    @Override // p000.AbstractC0037h
    /* renamed from: b */
    public final AbstractC0041l mo107b(String str) {
        return m741a(str, 13);
    }

    /* renamed from: a */
    private final AbstractC0041l m741a(String str, int i) {
        String str2 = AppState.f181d;
        AbstractC0046q abstractC0046q = this.f334D;
        C0035f c0035f = new C0035f(this, 0, 65536, 3, str, str, 0, 0, str2, str2, str2);
        abstractC0046q.m1401b((Object) c0035f);
        m1052c(C0015ao.m403a(this, str, i));
        return c0035f;
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
                i2 = 260;
                break;
            case 2:
                i2 = 2;
                break;
            case 3:
                i2 = 516;
                break;
            case 4:
                i2 = 3;
                break;
            default:
                mo120l();
                return;
        }
        m721d(i2);
    }

    /* renamed from: V */
    public final int m742V() {
        if (this.f228d == null) {
            return 0;
        }
        return this.f228d.size();
    }

    /* renamed from: e */
    public final void m743e(Object obj) {
        this.f229e = false;
        boolean z = true;
        if (this.f228d == null) {
            z = false;
            this.f228d = C0040k.m1213g();
        }
        Object objM475a = JsonParser.m475a(obj, AppState.m584b(329785));
        for (int i = 0; i < ((Vector) objM475a).size(); i++) {
            Object objM482e = JsonParser.m482e(objM475a, i);
            C0052w c0052wM745h = m745h(JsonParser.m477b(objM482e, AppState.m584b(132297)));
            if (c0052wM745h == null) {
                this.f228d.addElement(new C0052w(objM482e));
            } else {
                c0052wM745h.m1412a(objM482e);
            }
        }
        this.f230f = JsonParser.m479c(obj, AppState.m584b(526385));
        m744b(z);
    }

    /* renamed from: b */
    private void m744b(boolean z) {
        boolean z2;
        if (z) {
            return;
        }
        int i = 0;
        do {
            z2 = false;
            i++;
            Enumeration enumerationElements = this.f228d.elements();
            while (enumerationElements.hasMoreElements()) {
                if (((C0052w) enumerationElements.nextElement()).f409a == i) {
                    z2 = true;
                }
            }
        } while (z2);
        this.f228d.addElement(new C0052w(i));
    }

    /* renamed from: h */
    public final C0052w m745h(int i) {
        Enumeration enumerationElements = this.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            C0052w c0052w = (C0052w) enumerationElements.nextElement();
            if (c0052w.f409a == i) {
                return c0052w;
            }
        }
        return null;
    }

    /* renamed from: W */
    public final C0052w m746W() {
        return (C0052w) this.f228d.lastElement();
    }

    /* renamed from: i */
    public final C0052w m747i(String str) {
        Enumeration enumerationElements = this.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            C0052w c0052w = (C0052w) enumerationElements.nextElement();
            if (c0052w.m1416c(str)) {
                return c0052w;
            }
        }
        return null;
    }

    /* renamed from: j */
    public final void m748j(String str) {
        int iM742V = m742V();
        while (true) {
            iM742V--;
            if (iM742V < 0) {
                return;
            }
            C0052w c0052w = (C0052w) this.f228d.elementAt(iM742V);
            if (c0052w.m1416c(str)) {
                c0052w.f414f.removeElement(str);
                c0052w.f415g.removeElement(str);
                c0052w.f416h.remove(str);
                if (str.equals(c0052w.f413e)) {
                    c0052w.f413e = AppState.f181d;
                }
            }
        }
    }

    /* renamed from: X */
    public final C0052w m749X() {
        C0052w c0052wM750m = m750m(AppState.m584b(897));
        return c0052wM750m != null ? c0052wM750m : m750m(AppState.m584b(892));
    }

    /* renamed from: m */
    private C0052w m750m(String str) {
        Enumeration enumerationElements = this.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            C0052w c0052w = (C0052w) enumerationElements.nextElement();
            if (c0052w.f410b.equals(str)) {
                return c0052w;
            }
        }
        return null;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int mo276r() {
        return 6;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean mo277s() {
        return this.f232h && this.f231g != null && this.f231g.m59c();
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void mo278t() {
        this.f232h = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void mo279u() {
        this.f232h = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int mo274v() {
        if (this.f231g != null) {
            return (int) this.f231g.m56a();
        }
        return 0;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int mo275w() {
        if (this.f231g != null) {
            return (int) this.f231g.m57b();
        }
        return 0;
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String mo273x() {
        String strM584b;
        int i;
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        if (this.f231g.f24l) {
            stringBufferM1217h.append(AppState.m584b(489));
            String str = this.f231g.f16d;
            if (Utils.m535l(str)) {
                stringBufferM1217h.append(str).append('.').append(' ');
            }
            stringBufferM1217h.append("Уточнить?");
        } else {
            stringBufferM1217h.append(AppState.m584b(488));
            if (C0015ao.m439R().size() > 1) {
                stringBufferM1217h.append(' ').append('(').append(this.f315k).append(')').append('.').append(' ');
            }
            String str2 = this.f231g.f16d;
            if (Utils.m535l(str2)) {
                stringBufferM1217h.append(str2).append('.').append(' ');
            }
            switch (this.f231g.f21i) {
                case 1:
                    i = 781;
                    strM584b = AppState.m584b(i);
                    break;
                case 2:
                    i = 782;
                    strM584b = AppState.m584b(i);
                    break;
                case 3:
                    i = 783;
                    strM584b = AppState.m584b(i);
                    break;
                case 4:
                    i = 784;
                    strM584b = AppState.m584b(i);
                    break;
                default:
                    strM584b = null;
                    break;
            }
            String str3 = strM584b;
            if (Utils.m535l(strM584b)) {
                stringBufferM1217h.append(str3).append('.');
            }
        }
        return C0040k.m1215a(stringBufferM1217h);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int mo280y() {
        return this.f231g.m60d();
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean mo281z() {
        return this.f231g.m59c() && !this.f231g.f24l;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int mo282a(int i) {
        return this.f233K.m1405a(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int mo283b(int i) {
        return this.f233K.m1406b(i, this);
    }

    /* renamed from: a */
    public final void m751a(SearchEntry c0050u) {
        if (m1056C()) {
            c0050u.f406a = this.f319o;
            m1053d(C0015ao.m321a(this, 4162, new ByteBuffer().m1360p(1).m1308a(c0050u.f407b)));
            this.f234L.addElement(c0050u);
        }
    }
}
