package p000;

import java.util.Hashtable;
import java.util.Vector;

/* renamed from: d */
/* loaded from: MobileAgent_3.9.jar:d.class */
public final class MmpProtocol extends Account {

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

    public MmpProtocol(int i, String str, String str2) {
        super(i, str, str2);
        this.f324t = -1;
        this.f325u = 0;
        this.f278L = 4;
        MmpContactGroup c0016ap = new MmpContactGroup(this, 0, AppState.m584b(1039));
        c0016ap.f399g = true;
        this.f334D = c0016ap;
        this.f275f = new Hashtable();
        this.f276g = new Hashtable();
        this.f277h = new Hashtable();
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo80a() {
        return 1;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo102a(String str, String str2) {
        int iMo102a = super.mo102a(str, str2);
        if (iMo102a != 0) {
            return iMo102a;
        }
        return 0;
    }

    public MmpProtocol(ByteBuffer c0043n) {
        super(c0043n);
        this.f278L = this.f325u >>> 16;
        if (this.f278L <= 0 || this.f278L > 5) {
            this.f278L = 4;
        }
        this.f325u &= 65535;
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                MmpContactGroup c0016ap = new MmpContactGroup(this, c0043n);
                c0016ap.f399g = true;
                this.f334D = c0016ap;
                this.f275f = new Hashtable();
                this.f276g = new Hashtable();
                this.f277h = new Hashtable();
                return;
            }
            m1083b((ContactGroup) new MmpContactGroup(this, c0043n));
        }
    }

    @Override // p000.Account
    /* renamed from: a */
    public final Account mo82a(ByteBuffer c0043n, boolean z, boolean z2) {
        this.f325u = (this.f325u & 65535) + (this.f278L << 16);
        return super.mo82a(c0043n, z, z2);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final ContactGroup mo85b() {
        return new MmpContactGroup(this, -1, AppState.m584b(1040));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final ContactGroup mo86c() {
        return new MmpContactGroup(this, -2, AppState.m584b(1042));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final ContactGroup mo87d() {
        return new MmpContactGroup(this, -3, AppState.m584b(1041));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final ContactGroup mo88e() {
        return new MmpContactGroup(this, -4, AppState.m584b(1043));
    }

    @Override // p000.Account
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

    @Override // p000.Account
    /* renamed from: g */
    public final int mo89g() {
        m1061F();
        this.f331A = 0L;
        this.f330z = 0L;
        m1068L();
        this.f333C.removeAllElements();
        return -1;
    }

    @Override // p000.Account
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
        objArr[0] = ResourceManager.m967e(c0043n.readIntBEAt());
        this.f333C.addElement(obj);
        return c0043n;
    }

    @Override // p000.Account
    /* renamed from: i */
    public final void mo97i() throws Throwable {
        Contact abstractC0041lM1069c;
        if (this.f322r <= 0) {
            m1061F();
            this.f324t = -1;
        }
        switch (this.f322r) {
            case 0:
                this.f318n.clear();
                if (this.f272c != null) {
                    this.f272c[0] = null;
                }
                this.f272c = null;
                this.f323s = 0;
                break;
            case 1:
                AppController.f153g = true;
                this.f323s = 10;
                this.f280N = ResourceManager.m972o();
                if (this.f280N != -1 || this.f280N == 1) {
                    this.f322r = 2;
                    break;
                }
                break;
            case 2:
                if (this.f280N != 0) {
                    Vector vectorM439R = AppController.m439R();
                    int iM541c = Utils.m541c(vectorM439R);
                    while (true) {
                        iM541c--;
                        if (iM541c >= 0) {
                            Account abstractC0037h = (Account) vectorM439R.elementAt(iM541c);
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
                        IOUtils.m778d((Object) AppState.m584b(479));
                        this.f322r = 0;
                    }
                    NetworkUtils.m1212a(vectorM439R);
                    break;
                } else {
                    this.f322r = 3;
                    break;
                }
            case 3:
                new AsyncTask(31, new Object[]{this, ResourceManager.m967e(0), this.f315k, m1064I()});
                this.f323s = 20;
                this.f322r = 5;
                AppController.f153g = true;
                break;
            case 5:
                if (this.f272c != null) {
                    this.f323s = 70;
                    AppController.f153g = true;
                    this.f319o = 28179;
                    this.f271K = ResourceManager.m986d(this.f272c[2]).toByteArray();
                    this.f269a = Integer.parseInt(this.f272c[0]);
                    this.f320p = new ConnectionThread(this.f272c[1]);
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
                ByteBuffer c0043nM1350t = this.f318n.extractJPEG();
                if (c0043nM1350t != null) {
                    AppController.f153g = true;
                    this.f323s = 85;
                    AppController.m421a((Account) this, c0043nM1350t);
                    if (c0043nM1350t.peekByteAt(1) == 1) {
                        long j = AppState.m587e(1536) ? 25000L : 60000L;
                        this.f330z = j;
                        this.f331A = System.currentTimeMillis() + j;
                        m1085R();
                        byte[] bArr = this.f271K;
                        m1053d(AppController.m326a(this, 1).writeIntBE(1).writeShortBE(6).writeShortBE(bArr.length).writeBytes(bArr).updateLength());
                        this.f271K = null;
                        m1053d(AppController.m375a(this));
                        m1053d(AppController.m464a(this, 1026, new ByteBuffer().writeCompressed(1051079)));
                        m1053d(m916a(new Object[]{AppController.m464a(this, 286, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(268435456 | m919j()).writeCompressed(2689260)), ResourceManager.m967e(17)}));
                        this.f273d = 0;
                        m1053d(m916a(new Object[]{AppController.m464a(this, 4868, (ByteBuffer) null), ResourceManager.m967e(6)}));
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
            Vector vectorM440S = AppController.m440S();
            if (Utils.m541c(vectorM440S) == 0) {
                m1061F();
                this.f324t = mo89g();
                return;
            }
            NetworkUtils.m1212a(vectorM440S);
        }
        while (true) {
            ByteBuffer c0043nM1350t2 = this.f318n.extractJPEG();
            ByteBuffer c0043nM1299a = c0043nM1350t2;
            if (c0043nM1350t2 == null) {
                if (this.f324t != -1 && this.f320p != null && this.f320p.m1131a() == 0) {
                    m1061F();
                    this.f324t = mo89g();
                }
                if (this.f330z > 0 && m1056C() && AppController.m306a(this.f331A)) {
                    m1052c(AppController.m326a(this, 5));
                    return;
                }
                return;
            }
            AppController.m421a((Account) this, c0043nM1299a);
            this.f323s = 90;
            if (c0043nM1299a.peekByteAt(1) == 2) {
                int iM1331i = (c0043nM1299a.peekByteAt(6) << 24) | (c0043nM1299a.peekByteAt(8) << 16) | (c0043nM1299a.peekByteAt(7) << 8) | c0043nM1299a.peekByteAt(9);
                int iM1356x = c0043nM1299a.readIntBEAt();
                int iM1351l = c0043nM1299a.peekShortBE(10);
                int iM1351l2 = c0043nM1299a.peekShortBE(10);
                c0043nM1299a.skip(16);
                if (iM1351l2 == 32768) {
                    c0043nM1299a.skip(c0043nM1299a.readShortBE());
                }
                c0043nM1299a = c0043nM1299a.compact();
                switch (iM1331i) {
                    case 271:
                        Vector vector = this.f333C;
                        int size = vector.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                XmppMailRuProtocol.m882a(this, c0043nM1299a, iM1356x, 0);
                                break;
                            } else {
                                Object[] objArr = (Object[]) vector.elementAt(size);
                                if (((Integer) objArr[1]).intValue() == 17) {
                                    objArr[0] = ResourceManager.m967e(iM1356x);
                                }
                            }
                        }
                    case 287:
                        try {
                            int iM1355w = c0043nM1299a.readIntBE();
                            int iM1355w2 = c0043nM1299a.readIntBE();
                            String strM17c = AppState.f181d;
                            boolean z = false;
                            byte[] bArr2 = AppState.f176a;
                            while (c0043nM1299a.length > 0) {
                                int iM1353u = c0043nM1299a.readShortBE();
                                int iM1353u2 = c0043nM1299a.readShortBE();
                                byte[] bArr3 = iM1353u2 > 0 ? new byte[iM1353u2] : null;
                                byte[] bArr4 = bArr3;
                                if (bArr3 != null) {
                                    c0043nM1299a.readIntoBytes(bArr4);
                                }
                                if (iM1353u == 1) {
                                    strM17c = StringUtils.m17c(new String(bArr4));
                                } else if (iM1353u == 2) {
                                    z = true;
                                } else if (iM1353u == 3) {
                                    bArr2 = bArr4;
                                }
                                if (iM1353u != 3) {
                                    NetworkUtils.m1209a(bArr4);
                                }
                            }
                            MrimAccount c0028ba = (MrimAccount) AppController.m440S().elementAt(0);
                            c0028ba.m1052c(ResourceManager.m981a(c0028ba, this, iM1355w, iM1355w2, strM17c, z, bArr2));
                            break;
                        } catch (Throwable unused) {
                            break;
                        }
                    case 779:
                        m917a(c0043nM1299a.readLenPrefixStr(), c0043nM1299a);
                        break;
                    case 780:
                        m917a(c0043nM1299a.readLenPrefixStr(), c0043nM1299a);
                        break;
                    case 1025:
                        XmppMailRuProtocol.m881b(this, iM1356x);
                        break;
                    case 1031:
                        IOUtils.m821a(this, c0043nM1299a);
                        break;
                    case 1035:
                        long jM1341m = c0043nM1299a.readLong();
                        c0043nM1299a.readShortBE();
                        m1073a(c0043nM1299a.readLenPrefixStr(), jM1341m, 64);
                        break;
                    case 1036:
                        long jM1341m2 = c0043nM1299a.readLong();
                        c0043nM1299a.readShortBE();
                        m1073a(c0043nM1299a.readLenPrefixStr(), jM1341m2, 128);
                        break;
                    case 1044:
                        c0043nM1299a.skip(10);
                        String strM1363z = c0043nM1299a.readLenPrefixStr();
                        if (c0043nM1299a.readShortBE() != 0) {
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
                        XmppMailRuProtocol.m882a(this, c0043nM1299a, iM1356x, iM1351l);
                        break;
                    case 4878:
                        XmppMailRuProtocol.m882a(this, c0043nM1299a, iM1356x, 0);
                        break;
                    case 4885:
                        Contact abstractC0041lM1069c2 = m1069c((Object) c0043nM1299a.readLenPrefixStr());
                        if (null != abstractC0041lM1069c2) {
                            abstractC0041lM1069c2.mo145h();
                            break;
                        }
                        break;
                    case 4889:
                        ResourceManager.m925a(3);
                        m1072a(c0043nM1299a.readLenPrefixStr(), 0L, c0043nM1299a.readVarLenStr());
                        break;
                    case 4891:
                        String strM1363z2 = c0043nM1299a.readLenPrefixStr();
                        byte bM1344o = c0043nM1299a.readByte();
                        m1072a(strM1363z2, 0L, NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(483)).append(AppState.m584b(bM1344o == 1 ? 484 : 485)).append(c0043nM1299a.readVarLenStr())));
                        if (bM1344o == 1 && null != (abstractC0041lM1069c = m1069c((Object) strM1363z2))) {
                            abstractC0041lM1069c.mo145h();
                            break;
                        }
                        break;
                    case 4892:
                        m1072a(c0043nM1299a.readLenPrefixStr(), 0L, AppState.m584b(480));
                        break;
                    case 5377:
                        IOUtils.m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(481)).append(1501).append('/').append(c0043nM1299a.readShortBE()).append(AppState.m584b(482))));
                        XmppMailRuProtocol.m881b(this, iM1356x);
                        break;
                    case 5379:
                        XmppMailRuProtocol.m882a(this, c0043nM1299a, iM1356x, iM1351l);
                        break;
                }
                AppController.f152f = true;
            } else {
                if (c0043nM1299a.peekByteAt(1) == 4) {
                    AppController.m386a(this, c0043nM1299a);
                    AppController.f152f = true;
                }
            }
            c0043nM1299a.clear();
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
        MmpContact c0009ai = (MmpContact) m1069c((Object) str);
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
            c0043n.skip(2);
            int iM1353u = c0043n.readShortBE();
            for (int i2 = 0; i2 < iM1353u; i2++) {
                int iM1353u2 = c0043n.readShortBE();
                int iM1353u3 = c0043n.readShortBE();
                if (iM1353u2 == 6) {
                    int iM1355w = c0043n.readIntBE() & 65535;
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
                    byte[] bArr = c0043n.data;
                    int i3 = c0043n.offset;
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
                        int iM1353u4 = c0043n.readShortBE();
                        int iM1353u5 = c0043n.readShortBE() & 255;
                        int i7 = (iM1353u3 - 2) - 2;
                        if ((iM1353u5 & 128) != 0 || i7 < (iM1353u5 & 128)) {
                            c0043n.skip(iM1353u5);
                            iM1353u3 = i7 - iM1353u5;
                        } else if (iM1353u4 == 14) {
                            byte[] bArr2 = new byte[iM1353u5];
                            c0043n.readIntoBytes(bArr2);
                            String strM17c = StringUtils.m17c(new String(bArr2));
                            if (strM17c.startsWith(NetworkUtils.m1221a(28270022039266153L)) && (iM511a = Utils.m511a(StringUtils.m15c(strM17c, 7), 0, 23, -1)) >= 0) {
                                c0009ai.f373r &= -65536;
                                c0009ai.f373r |= iM511a + 269;
                            }
                            NetworkUtils.m1209a(bArr2);
                            iM1353u3 = i7 - iM1353u5;
                        } else {
                            c0043n.skip(iM1353u5);
                            iM1353u3 = i7 - iM1353u5;
                        }
                    }
                }
                c0043n.skip(iM1353u3);
            }
        } catch (Throwable unused) {
        }
        c0009ai.m1228A();
        if (z || !c0009ai.f371p) {
            return;
        }
        ResourceManager.m925a(1);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo125a(Contact abstractC0041l, String str, long j) {
        ByteBuffer c0043nM464a;
        int iMo125a = super.mo125a(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.f327w++;
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        int i = c0009ai.f62g ? 1 : 0;
        int i2 = c0009ai.f63h ? 2 : 1;
        ByteBuffer c0043nM1373h = new ByteBuffer().writeLong(j).writeShortBE(i2).writeByteLenStr(c0009ai.f57c);
        ByteBuffer c0043n = new ByteBuffer();
        if (i2 == 1) {
            if (i == 1) {
                c0043n.writeShortBE(2).writeShortBE(0).writeAsShorts(str);
            } else {
                c0043n.writeIntLE(0).writeCharBytes(str);
            }
            c0043nM1373h.writeShortBE(2).writeShortBE(i + 9 + c0043n.length).writeShortBE(1281).writeShortBE(i + 1);
            if (i == 1) {
                c0043nM1373h.writeShortBE(262);
            } else {
                c0043nM1373h.writeByte(1);
            }
            c0043nM464a = AppController.m464a(this, 1030, c0043nM1373h.writeShortBE(257).writeBufferShortLen(c0043n).writeShortBE(6).writeShortBE(0));
        } else {
            if (i == 1) {
                c0043n.writeUTFNoLen(str);
            } else {
                c0043n.writeCharBytes(str);
            }
            c0043n.writeByte(0);
            int i3 = c0043n.length;
            int i4 = i3 - (i == 1 ? 0 : 42);
            c0043nM464a = AppController.m464a(this, 1030, c0043nM1373h.writeShortBE(5).writeShortBE(i4 + 143).writeShortBE(0).writeLong(j).writeCompressed(906).writeShortBE(10).writeShortBE(2).writeShortBE(1).writeShortBE(15).writeShortBE(0).writeShortBE(10001).writeShortBE(i4 + 103).writeShortLE(27).writeShortLE(8).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortBE(0).writeIntLE(3).writeByte(0).writeShortBE(0).writeIntLE(14).writeIntLE(0).writeIntLE(0).writeIntLE(0).writeShortLE(1).writeShortLE(m919j()).writeShortLE(1).writeShortLE(i3).writeBuffer(c0043n).writeCompressed(i == 0 ? 526807 : 3279327).writeShortBE(3).writeShortBE(0));
        }
        return m1052c(c0043nM464a);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo112a(Contact abstractC0041l, Object[] objArr) {
        int iMo112a = super.mo112a(abstractC0041l, objArr);
        if (0 != iMo112a) {
            return iMo112a;
        }
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        String str = (String) objArr[0];
        return m1052c(m916a(new Object[]{AppController.m464a(this, 4873, c0009ai.m180a(3, str, c0009ai.f56b)), ResourceManager.m967e(0), c0009ai, str}));
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
            m1052c(XmppMailRuProtocol.m857a(this, this.f274e));
            m1052c(m916a(new Object[]{AppController.m464a(this, 286, new ByteBuffer().writeShortBE(6).writeShortBE(4).writeIntBE(268435456 | m919j())), ResourceManager.m967e(17)}));
            return m1052c(AppController.m375a(this));
        }
        if (m1055B()) {
            return 487;
        }
        return mo914a_(0);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo114a(Contact abstractC0041l) {
        if (!m1056C()) {
            return 299;
        }
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        AppState.f177b[1316] = ContactInfo.m1254b(this).m1284u(c0009ai.f57c);
        return m1052c(StringUtils.m18a(this, Utils.m510a((Object) c0009ai.f57c)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo113a(Contact abstractC0041l, ContactGroup abstractC0046q, ContactGroup abstractC0046q2) {
        int iMo113a = super.mo113a(abstractC0041l, abstractC0046q, abstractC0046q2);
        if (0 != iMo113a) {
            return iMo113a;
        }
        m1052c(ResourceManager.m961a(this));
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        return m1052c(m916a(new Object[]{AppController.m464a(this, 4874, c0009ai.m180a(2, c0009ai.f376u, c0009ai.f56b)), ResourceManager.m967e(10), c0009ai, abstractC0046q, abstractC0046q2}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo124a(ContactGroup abstractC0046q, String str) {
        int iMo124a = super.mo124a(abstractC0046q, str);
        if (iMo124a != 0) {
            return iMo124a;
        }
        MmpContactGroup c0016ap = (MmpContactGroup) abstractC0046q;
        return m1052c(m916a(new Object[]{AppController.m464a(this, 4873, c0016ap.m465a(str, -1, -1)), ResourceManager.m967e(1), c0016ap, str}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo122a(String str) {
        int iMo122a = super.mo122a(str);
        if (iMo122a != 0) {
            return iMo122a;
        }
        m1052c(ResourceManager.m961a(this));
        return m1052c(ResourceManager.m937a(this, str));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo123a(ContactGroup abstractC0046q) {
        int iMo123a = super.mo123a(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        m1052c(ResourceManager.m961a(this));
        MmpContactGroup c0016ap = (MmpContactGroup) abstractC0046q;
        return m1052c(m916a(new Object[]{AppController.m464a(this, 4874, c0016ap.m465a(c0016ap.f398f, -1, -1)), ResourceManager.m967e(2), c0016ap}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int mo118b(Contact abstractC0041l) {
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        int iMo118b = super.mo118b((Contact) c0009ai);
        if (0 != iMo118b) {
            return iMo118b;
        }
        if (c0009ai.mo143m()) {
            m1074a((Contact) c0009ai, true);
            return 0;
        }
        if (c0009ai.mo142k()) {
            m1052c(IOUtils.m792c(this, c0009ai));
        }
        if (c0009ai.mo140i()) {
            m1052c(IOUtils.m790a(this, c0009ai));
        }
        if (c0009ai.mo141j()) {
            m1052c(IOUtils.m791b(this, c0009ai));
        }
        return m1052c(m916a(new Object[]{AppController.m464a(this, 4874, c0009ai.m180a(2, c0009ai.f376u, c0009ai.f56b)), ResourceManager.m967e(5), c0009ai}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo734a(String str, String str2, String str3, ContactGroup abstractC0046q, boolean z) {
        int iMo734a = super.mo734a(str, str2, str3, abstractC0046q, z);
        if (0 != iMo734a) {
            return iMo734a;
        }
        m1052c(AppController.m464a(this, 4884, new ByteBuffer().writeByteLenStr(str).writeIntLE(0)));
        MmpContact c0009ai = (MmpContact) m1069c((Object) str);
        if (null != c0009ai && !c0009ai.mo143m()) {
            return m1052c(IOUtils.m753a(this, c0009ai, str3));
        }
        m1052c(ResourceManager.m961a(this));
        MmpContactGroup c0016ap = (MmpContactGroup) abstractC0046q;
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortString(str).writeShortBE(c0016ap.f157a);
        int iM920k = m920k();
        return m1052c(m916a(new Object[]{AppController.m464a(this, 4872, c0043nM1357m.writeShortBE(iM920k).writeShortBE(0).writeBufferShortLen(new ByteBuffer().writeShortBE(102).writeShortBE(0).writeShortBE(347).writeShortBE(1).writeByte(32).writeShortBE(305).writeUTF(str2))), ResourceManager.m967e(14), str, str2, c0016ap, ResourceManager.m967e(iM920k), str3}));
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
                    MmpContactGroup c0016ap = (MmpContactGroup) vector.elementAt(size);
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
                        MmpContact c0009ai = (MmpContact) vector2.elementAt(size2);
                        if (c0009ai.f55a == iM520a || c0009ai.f59d == iM520a || c0009ai.f60e == iM520a || c0009ai.f61f == iM520a) {
                            z = true;
                        }
                    }
                }
            }
        } while (z);
        return iM520a;
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int mo104c(Contact abstractC0041l) {
        if (abstractC0041l.mo143m()) {
            return 310;
        }
        return m1052c(IOUtils.m792c(this, (MmpContact) abstractC0041l));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int mo105d(Contact abstractC0041l) {
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        if (c0009ai.mo141j() && !c0009ai.mo140i()) {
            m1052c(IOUtils.m791b(this, c0009ai));
        }
        return m1052c(IOUtils.m790a(this, c0009ai));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int mo106e(Contact abstractC0041l) {
        MmpContact c0009ai = (MmpContact) abstractC0041l;
        if (!c0009ai.mo141j() && c0009ai.mo140i()) {
            m1052c(IOUtils.m790a(this, c0009ai));
        }
        return m1052c(IOUtils.m791b(this, c0009ai));
    }

    @Override // p000.Account
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
        m1052c(AppController.m326a(this, 4));
        m1061F();
        this.f324t = mo89g();
        return 0;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int mo115b(Object obj) {
        String[] strArr = (String[]) obj;
        ByteBuffer c0043nM1358n = new ByteBuffer().writeIntLE(this.f269a).writeShortLE(2000).writeShortBE(0).writeShortLE(1375);
        String str = strArr[0];
        if (str.length() > 0) {
            c0043nM1358n.writeShortBE(13825).writeShortLE(4).writeIntLE(Utils.m510a((Object) str));
        } else {
            c0043nM1358n.writeShortBE(12290).writeShortLE(1).writeByte(strArr[1].length()).writeProtocolStr(21505, strArr[2]).writeProtocolStr(16385, strArr[3]).writeProtocolStr(18945, strArr[4]).writeProtocolStr(24065, strArr[5]).writeProtocolStr(36865, strArr[6]).writeProtocolStr(9730, strArr[7]);
        }
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortBE(1);
        int i = c0043nM1358n.length;
        return m1052c(m916a(new Object[]{AppController.m464a(this, 5378, c0043nM1357m.writeShortBE(i + 2).writeShortLE(i).writeBuffer(c0043nM1358n)), ResourceManager.m967e(9)}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final Contact mo107b(String str) {
        ContactGroup abstractC0046q = this.f334D;
        MmpContact c0009ai = new MmpContact(this, -1, -1, str, str, true);
        abstractC0046q.m1401b((Object) c0009ai);
        ByteBuffer c0043nM1360p = new ByteBuffer().writeIntLE(this.f269a).writeShortLE(2000).writeShortBE(0).writeShortLE(1375).writeShortBE(13825).writeShortLE(4).writeIntLE(Utils.m510a((Object) str));
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortBE(1);
        int i = c0043nM1360p.length;
        m1052c(m916a(new Object[]{AppController.m464a(this, 5378, c0043nM1357m.writeShortBE(i + 2).writeShortLE(i).writeBuffer(c0043nM1360p)), ResourceManager.m967e(21)}));
        return c0009ai;
    }

    @Override // p000.Account
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

    @Override // p000.Account
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
        m1052c(XmppMailRuProtocol.m857a(this, this.f274e));
        return 4;
    }

    @Override // p000.Account
    /* renamed from: o */
    public final void mo924o() {
        super.mo924o();
        this.f326v = 0;
    }

    @Override // p000.Account
    /* renamed from: p */
    public final int mo110p() {
        return 197381;
    }
}
