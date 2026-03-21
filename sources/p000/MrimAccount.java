package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ba */
/* loaded from: MobileAgent_3.9.jar:ba.class */
public final class MrimAccount extends Account implements ListItem {

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
    public VCard f231g;

    /* renamed from: h */
    public boolean f232h;

    /* renamed from: K */
    private SizeCache f233K;

    /* renamed from: L */
    private Vector f234L;

    public MrimAccount(int i, String str, String str2) {
        super(i, str, str2);
        this.f324t = 0;
        this.f325u = 1;
        MrimContactGroup c0010aj = new MrimContactGroup(this, -1, 102, AppState.m584b(1039));
        c0010aj.f399g = true;
        this.f334D = c0010aj;
        this.f231g = new VCard();
        this.f232h = true;
        this.f233K = new SizeCache();
        this.f234L = NetworkUtils.m1213g();
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo80a() {
        return 0;
    }

    public MrimAccount(ByteBuffer c0043n) {
        super(c0043n);
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                break;
            } else {
                this.f313i.addElement(new MrimContactGroup(this, c0043n));
            }
        }
        MrimContactGroup c0010aj = new MrimContactGroup(this, c0043n);
        c0010aj.f399g = true;
        this.f334D = c0010aj;
        ByteBuffer c0043n2 = new ByteBuffer();
        int iM1328e2 = c0043n.readInt();
        if (iM1328e2 > 0) {
            c0043n2.writeBytesAt(c0043n.data, c0043n.offset, iM1328e2);
            c0043n.skip(iM1328e2);
        }
        try {
        } catch (Throwable unused) {
            this.f230f = null;
            this.f228d = null;
        }
        if (c0043n2.length == 0) {
            throw new RuntimeException();
        }
        this.f230f = c0043n2.readUTF8Str((String) null);
        c0043n2.readWideStr();
        this.f228d = NetworkUtils.m1213g();
        int iM1328e3 = c0043n2.readInt();
        for (int i = 0; i < iM1328e3; i++) {
            this.f228d.addElement(ChatRoom.m1411b(c0043n2));
        }
        if (c0043n2.readShortBE() != 21554) {
            throw new RuntimeException();
        }
        m744b(false);
        this.f231g = new VCard();
        this.f232h = true;
        this.f233K = new SizeCache();
        this.f234L = NetworkUtils.m1213g();
    }

    @Override // p000.Account
    /* renamed from: a */
    public final Account mo82a(ByteBuffer c0043n, boolean z, boolean z2) {
        super.mo82a(c0043n, z, z2);
        if (z2) {
            c0043n.writeBufferIntLen(m716a(z));
        } else {
            c0043n.writeIntLE(0);
        }
        return this;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final void mo714b(ByteBuffer c0043n) {
        c0043n.writeIntLE(13).writeIntLE(this.f326v).writeIntLE(this.f327w).writeIntLE(this.f328x);
        VCard c0003ac = this.f231g;
        boolean zM59c = c0003ac.m59c();
        c0043n.writeBoolean(zM59c);
        if (zM59c) {
            c0043n.writeStringLatin1(c0003ac.f13a).writeStringLatin1(c0003ac.f14b).writeStringLatin1(c0003ac.f15c).writeStringUTF16(c0003ac.f16d).writeStringLatin1(c0003ac.f17e).writeStringLatin1(c0003ac.f18f).writeStringLatin1(c0003ac.f20h).writeStringLatin1(c0003ac.f19g).writeIntBE(c0003ac.f21i).writeBoolean(c0003ac.f24l);
        }
    }

    @Override // p000.Account
    /* renamed from: a */
    public final void mo715a(ByteBuffer c0043n) {
        int iM1328e = c0043n.readInt();
        if (iM1328e == 12) {
            this.f326v = c0043n.readInt();
            this.f327w = c0043n.readInt();
            this.f328x = c0043n.readInt();
            c0043n.readInt();
            return;
        }
        if (iM1328e == 13) {
            this.f326v = c0043n.readInt();
            this.f327w = c0043n.readInt();
            this.f328x = c0043n.readInt();
            this.f231g = VCard.m58b(c0043n);
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
                c0043n.ensureCapacity(20480);
                c0043n.writeStringUTF16(this.f230f);
                c0043n.writeIntLE(0);
                c0043n.writeIntLE(iM742V - 1);
                for (int i = 0; i < iM742V; i++) {
                    ChatRoom c0052w = (ChatRoom) this.f228d.elementAt(i);
                    if (c0052w != m746W()) {
                        c0052w.m1410a(c0043n);
                    }
                }
                c0043n.writeShortBE(21554);
            } catch (Throwable unused) {
                c0043n.clear();
            }
        }
        return c0043n;
    }

    /* renamed from: f */
    public final MrimContact m717f(String str) {
        return (MrimContact) m1069c((Object) str);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final ContactGroup mo85b() {
        return new MrimContactGroup(this, -1, 101, AppState.m584b(1040));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final ContactGroup mo86c() {
        return new MrimContactGroup(this, -1, 104, AppState.m584b(1042));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final ContactGroup mo87d() {
        return new MrimContactGroup(this, -1, 103, AppState.m584b(1041));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final ContactGroup mo88e() {
        return new MrimContactGroup(this, -1, 105, AppState.m584b(1043));
    }

    /* renamed from: f */
    public final MrimContactGroup m718f() {
        return (MrimContactGroup) this.f313i.elementAt(0);
    }

    @Override // p000.Account
    /* renamed from: g */
    public final int mo89g() {
        m1061F();
        this.f331A = 0L;
        this.f330z = 0L;
        m1068L();
        this.f333C.removeAllElements();
        return 0;
    }

    @Override // p000.Account
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
    @Override // p000.Account
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
                this.f318n.clear();
                this.f323s = 0;
                break;
            case 1:
                this.f323s = 20;
                this.f319o = 0;
                this.f320p = new ConnectionThread(AppState.m584b(1114895));
                this.f322r = 2;
                AppController.f153g = true;
                break;
            case 2:
                this.f323s = 30;
                if (this.f320p.m1131a() == 2) {
                    this.f323s = 40;
                    this.f322r = 3;
                    AppController.f153g = true;
                    break;
                }
                break;
            case 3:
                this.f320p.m1132a(this.f318n);
                int i4 = this.f318n.length;
                int i5 = i4;
                if (i4 > 0) {
                    AppController.m419a((Account) this, i5);
                    this.f323s = 60;
                    StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
                    while (true) {
                        int i6 = i5;
                        i5 = i6 - 1;
                        if (i6 <= 0) {
                            this.f320p.f349c = 3;
                            this.f320p = new ConnectionThread(NetworkUtils.m1215a(stringBufferM1217h));
                            this.f322r = 4;
                            AppController.f153g = true;
                            break;
                        } else {
                            char cM1344o = (char) this.f318n.readByte();
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
                    m1053d(AppController.m377a(this));
                    this.f322r = 5;
                    AppController.f153g = true;
                    break;
                }
                break;
        }
        if (this.f322r < 5) {
            return;
        }
        this.f320p.m1132a(this.f318n);
        while (true) {
            ByteBuffer c0043nM1349s = this.f318n.extractPNG();
            if (c0043nM1349s == null) {
                if (c0043nM1349s == null && this.f324t != 0 && this.f320p != null && this.f320p.m1131a() == 0) {
                    m1061F();
                    this.f324t = mo89g();
                }
                if (this.f330z <= 0 || !AppController.m306a(this.f331A)) {
                    return;
                }
                m1052c(AppController.m321a(this, 4102, (ByteBuffer) null));
                return;
            }
            AppController.m421a((Account) this, c0043nM1349s);
            int iM1330h = c0043nM1349s.peekIntAt(12);
            int iM1330h2 = c0043nM1349s.peekIntAt(8);
            c0043nM1349s.skip(44);
            switch (iM1330h) {
                case 4098:
                    long jM503b = Utils.m503b(c0043nM1349s.readInt(), AppState.m587e(1536) ? 25 : 45) * 1000;
                    this.f330z = jM503b;
                    this.f331A = System.currentTimeMillis() + jM503b;
                    ByteBuffer c0043nM1308a = new ByteBuffer().writeStringLatin1(this.f315k).writeStringLatin1(m1064I());
                    boolean zM587e = AppState.m587e(105);
                    m1053d(AppController.m321a(this, 4216, c0043nM1308a.writeIntLE(zM587e ? -1 : 22).writeStringLatin1(zM587e ? null : new ByteBuffer().writeCompressed(1642077).writeExtendedInt(2229599).getStringAndClear()).writeCompressed(1704823).writeStringLatin1(XmppContactGroup.m1017d()).writeBuffer(XmppContactGroup.m1016a(this))));
                    this.f322r = 6;
                    break;
                case 4100:
                    this.f323s = 85;
                    m1085R();
                    break;
                case 4101:
                    AppController.m461a(this, c0043nM1349s);
                    break;
                case 4105:
                    Conversation.m1110a(this, c0043nM1349s, 0L);
                    break;
                case 4111:
                    int iM1328e = c0043nM1349s.readInt();
                    String strM1334g = c0043nM1349s.readWideStr();
                    c0043nM1349s.readUTF8Str((String) null);
                    c0043nM1349s.readUTF8Str((String) null);
                    MrimContact c0035fM717f = m717f(c0043nM1349s.readHexStr());
                    if (c0035fM717f != null && !c0035fM717f.mo143m()) {
                        c0043nM1349s.readInt();
                        String strM1334g2 = c0043nM1349s.readWideStr();
                        int i7 = c0035fM717f.f299f;
                        c0035fM717f.f299f = iM1328e;
                        c0035fM717f.f301h = strM1334g2;
                        c0035fM717f.f373r = AppController.m349a(iM1328e, strM1334g);
                        c0035fM717f.f371p = iM1328e != 0;
                        if (iM1328e == 0) {
                            c0035fM717f.m999p();
                        }
                        c0035fM717f.f375t = true;
                        c0035fM717f.m1228A();
                        if (i7 == 0 && iM1328e != 0) {
                            ResourceManager.m925a(1);
                            break;
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                case 4114:
                    XmppMailRuProtocol.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4115:
                    m1063H();
                    break;
                case 4117:
                    while (c0043nM1349s.length > 0) {
                        String strM1334g3 = c0043nM1349s.readWideStr();
                        if (StringUtils.m3a(852768, strM1334g3)) {
                            m1054c(c0043nM1349s.readUTF8Str((String) null));
                        } else if (StringUtils.m3a(983853, strM1334g3)) {
                            IOUtils.m759a(this, Utils.m510a((Object) c0043nM1349s.readUTF8Str((String) null)), (String) null, (String) null);
                        } else if (StringUtils.m3a(983868, strM1334g3)) {
                            String strM1335e = c0043nM1349s.readUTF8Str((String) null);
                            this.f226b = StringUtils.m13b(strM1335e, strM1335e.indexOf(58));
                        } else if (StringUtils.m3a(656203, strM1334g3)) {
                            c0043nM1349s.readWideStr();
                            this.f227c = true;
                        } else if (StringUtils.m3a(1114965, strM1334g3)) {
                            c0043nM1349s.skip((((((((c0043nM1349s.readInt() - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + (c0043nM1349s.readUTF8Str((String) null).length() << 1))) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length())) - (4 + c0043nM1349s.readWideStr().length()));
                        } else {
                            c0043nM1349s.readWideStr();
                        }
                    }
                    break;
                case 4122:
                    XmppMailRuProtocol.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4124:
                    XmppMailRuProtocol.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4125:
                    m1052c(AppController.m321a(this, 4126, new ByteBuffer().writeIntLE(c0043nM1349s.readInt()).writeIntLE(c0043nM1349s.readInt())));
                    try {
                        int i8 = this.f329y;
                        this.f329y = i8 + 1;
                        if (0 != i8) {
                            AppState.m594c(1449, 0);
                        }
                        Hashtable hashtable = new Hashtable();
                        String strM1214a = null;
                        String strM529d = Utils.m529d(c0043nM1349s.readWideStr(), '\r');
                        int length = strM529d.length();
                        StringBuffer stringBufferM1217h2 = NetworkUtils.m1217h();
                        boolean z = false;
                        int i9 = 0;
                        while (i9 < length) {
                            char cCharAt = strM529d.charAt(i9);
                            if (!z) {
                                if (cCharAt == '\n' && stringBufferM1217h2.length() == 0) {
                                    NetworkUtils.m1215a(stringBufferM1217h2);
                                    String str2 = (String) hashtable.get(AppState.m584b(1379315));
                                    int i10 = str2 != null ? -1 : Integer.parseInt(str2);
                                    i = i10;
                                    strM1336h = i10 >= 0 ? null : ResourceManager.m986d(StringUtils.m15c((String) hashtable.get(AppState.m584b(460837)), 13)).readAllWideStr();
                                    str = (String) hashtable.get(AppState.m584b(396269));
                                    i2 = Integer.parseInt((String) hashtable.get(AppState.m584b(789512)), 16);
                                    jM491a = Utils.m491a((String) hashtable.get(AppState.m584b(264254)));
                                    i3 = 1;
                                    if ((i2 & 128) != 0) {
                                        String strM15c = StringUtils.m15c(strM529d, i9);
                                        if ((i2 & 2097160) == 0) {
                                            strM1336h2 = ResourceManager.m986d(strM15c).readAllWideStr();
                                        } else {
                                            strM1336h2 = strM15c;
                                            i3 = 0;
                                        }
                                    } else {
                                        int iM627a = AppState.m627a(strM529d, 57408234938722L);
                                        strM1336h2 = ResourceManager.m986d(StringUtils.m12a(strM529d, iM627a + 6, strM529d.indexOf(AppState.m584b(134123), iM627a))).readAllWideStr();
                                    }
                                    if (i != -1 || (i >= 0 && i <= 5 && i != 1 && i != 3)) {
                                        Conversation.m1110a(this, new ByteBuffer().writeIntLE(0).writeIntLE(i2 | 4 | 128).writeStringLatin1((String) hashtable.get(AppState.m584b(264203))).writeString(strM1336h2, i3).writeIntLE(0).writeIntLE(0).writeIntLE(i).writeStringUTF16(strM1336h).writeStringLatin1(str), jM491a);
                                    }
                                    AppState.m594c(1449, 1);
                                    break;
                                } else if (cCharAt == ':') {
                                    strM1214a = NetworkUtils.m1214a(stringBufferM1217h2, false);
                                    z = true;
                                    i9++;
                                } else {
                                    stringBufferM1217h2.append(cCharAt);
                                }
                            } else if (cCharAt == '\n') {
                                hashtable.put(strM1214a, NetworkUtils.m1214a(stringBufferM1217h2, false));
                                z = false;
                            } else {
                                stringBufferM1217h2.append(cCharAt);
                            }
                            i9++;
                        }
                        NetworkUtils.m1215a(stringBufferM1217h2);
                        String str22 = (String) hashtable.get(AppState.m584b(1379315));
                        int i10_2 = str22 != null ? -1 : Integer.parseInt(str22);
                        i = i10_2;
                        strM1336h = i10_2 >= 0 ? null : ResourceManager.m986d(StringUtils.m15c((String) hashtable.get(AppState.m584b(460837)), 13)).readAllWideStr();
                        str = (String) hashtable.get(AppState.m584b(396269));
                        i2 = Integer.parseInt((String) hashtable.get(AppState.m584b(789512)), 16);
                        jM491a = Utils.m491a((String) hashtable.get(AppState.m584b(264254)));
                        i3 = 1;
                        if ((i2 & 128) != 0) {
                        }
                        if (i != -1) {
                            Conversation.m1110a(this, new ByteBuffer().writeIntLE(0).writeIntLE(i2 | 4 | 128).writeStringLatin1((String) hashtable.get(AppState.m584b(264203))).writeString(strM1336h2, i3).writeIntLE(0).writeIntLE(0).writeIntLE(i).writeStringUTF16(strM1336h).writeStringLatin1(str), jM491a);
                            AppState.m594c(1449, 1);
                        }
                    } catch (Throwable th) {
                        AppState.m594c(1449, 1);
                        throw th;
                    }
                    break;
                case 4129:
                    MrimContact c0035fM717f2 = m717f(c0043nM1349s.readWideStr());
                    if (null == c0035fM717f2) {
                        break;
                    } else {
                        c0035fM717f2.f298e &= -2;
                        break;
                    }
                case 4133:
                    XmppMailRuProtocol.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4136:
                    XmppMailRuProtocol.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4151:
                    Conversation.m1112a(this, c0043nM1349s);
                    break;
                case 4160:
                    XmppMailRuProtocol.m883a(this, c0043nM1349s, iM1330h2);
                    break;
                case 4163:
                    if (c0043nM1349s.readInt() != 1 || c0043nM1349s.readInt() <= 0) {
                        break;
                    } else {
                        String strM1334g4 = c0043nM1349s.readWideStr();
                        int size = this.f234L.size();
                        while (true) {
                            size--;
                            if (size < 0) {
                                break;
                            }
                            SearchEntry c0050u = (SearchEntry) this.f234L.elementAt(size);
                            if (iM1330h2 == c0050u.id) {
                                this.f234L.removeElementAt(size);
                                int i11 = c0050u.type;
                                if (i11 == 1) {
                                    m732g(strM1334g4);
                                    AppController.m393a(this, strM1334g4);
                                } else if (i11 == 2) {
                                    ContactInfo c0042mM1251a = ContactInfo.m1251a(this);
                                    c0042mM1251a.m1262e(strM1334g4);
                                    AppState.f177b[1319] = c0042mM1251a;
                                    IOUtils.m778d(new IOUtils(5, null));
                                }
                            }
                        }
                    }
                    break;
                case 4168:
                    IOUtils.m759a(this, c0043nM1349s.readInt(), c0043nM1349s.readUnicodeStr(), c0043nM1349s.readUnicodeStr());
                    break;
                case 4180:
                    String strM1334g5 = c0043nM1349s.readWideStr();
                    String strM1334g6 = c0043nM1349s.readWideStr();
                    if (!StringUtils.m3a(525167, strM1334g5)) {
                        break;
                    } else {
                        this.f231g.m54a(new XmlParser(strM1334g6).parse());
                        m724j();
                        break;
                    }
                case 4182:
                    c0043nM1349s.readInt();
                    break;
                case 4195:
                    int iM1328e2 = c0043nM1349s.readInt();
                    String strM17c = StringUtils.m17c(c0043nM1349s.readWideStr().toLowerCase());
                    long jM1341m = c0043nM1349s.readLong();
                    int iM1328e3 = c0043nM1349s.readInt();
                    String strM1335e2 = c0043nM1349s.readUTF8Str((String) null);
                    MrimContact c0035fM717f3 = m717f(strM17c);
                    if (c0035fM717f3 != null && !c0035fM717f3.mo143m()) {
                        if ((iM1328e2 & 2) == 0) {
                            if ((iM1328e2 & 5) != 0) {
                                if (AppState.m587e(244) && !StringUtils.m6a(strM1335e2, c0035fM717f3.f303i) && ((int) (System.currentTimeMillis() / 1000)) - iM1328e3 < 172800 && c0035fM717f3.m1241H() != jM1341m) {
                                    AppState.m601a(1237, (Object) c0035fM717f3.f380w);
                                    ResourceManager.m925a(6);
                                    c0035fM717f3.m1227c(2);
                                    c0035fM717f3.m1239a(16, strM1335e2, 0L, jM1341m);
                                    ContactGroup abstractC0046qM1080g = c0035fM717f3.f369o.m1080g(c0035fM717f3);
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
                    m729a(c0043nM1349s.readWideStr(), c0043nM1349s.readBufferArray());
                    break;
                case 4229:
                    if (!AppState.m579a()) {
                        break;
                    } else {
                        Vector vectorM1345p = c0043nM1349s.readBufferArray();
                        if (!vectorM1345p.isEmpty()) {
                            String[] strArrM55a = VCard.m55a((ByteBuffer) vectorM1345p.elementAt(0));
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
                                        VCard c0003ac = this.f231g;
                                        String strM584b = AppState.m584b(590588);
                                        String str8 = AppState.f181d;
                                        c0003ac.m53a(str5, str4, strM584b, str8, str8, str8, str6, str7);
                                    } catch (Throwable unused) {
                                        this.f231g.m61e();
                                    }
                                    this.f233K.lastScale = -1;
                                    this.f231g.f16d = strArrM55a[3];
                                }
                                this.f231g.f24l = true;
                                if (AppController.m442U() != 10) {
                                    break;
                                } else {
                                    IOUtils.m778d((Object) AppState.m584b(786));
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
            c0043nM1349s.clear();
            AppController.f152f = true;
        }
    }

    /* renamed from: a */
    public final ByteBuffer m719a(Object obj) {
        if (!m1056C()) {
            return null;
        }
        Object[] objArr = (Object[]) obj;
        ByteBuffer c0043n = (ByteBuffer) objArr[0];
        objArr[0] = ResourceManager.m967e(c0043n.peekIntAt(8));
        this.f333C.addElement(obj);
        return c0043n;
    }

    @Override // p000.Account
    /* renamed from: O */
    public final Vector mo720O() {
        Vector vectorMo720O = super.mo720O();
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            MrimContact c0035f = (MrimContact) enumerationElements.nextElement();
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
                strM1215a = NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(1226)).append(this.f325u >> 8));
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
        return m1052c(AppController.m321a(this, 4130, new ByteBuffer().writeIntLE(i2 != 3 ? i2 : -2147483647).writeStringLatin1(strM1215a).writeStringUTF16(strM584b).writeStringUTF16(AppState.f181d).writeIntLE(AppState.m587e(105) ? -1 : 22)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo125a(Contact abstractC0041l, String str, long j) {
        int iMo125a = super.mo125a(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.f327w++;
        return m1052c(XmppContactGroup.m1001a(this, (MrimContact) abstractC0041l, str, j));
    }

    /* renamed from: a */
    private int m722a(int i, String[] strArr, VCard c0003ac) {
        if (!c0003ac.m59c() || c0003ac.f24l) {
            return 0;
        }
        String[] strArr2 = {c0003ac.f13a, c0003ac.f14b, c0003ac.f15c, c0003ac.f16d, c0003ac.f17e, c0003ac.f18f, c0003ac.f20h, c0003ac.f19g};
        m1052c(AppController.m321a(this, 4213, new ByteBuffer().writeIntLE(i).writeStringArr(strArr).writeStringLatin1(AppState.m584b(590694)).writeBuffer(new ByteBuffer().writeBufferIntLen(new ByteBuffer().writeStringLatin1(strArr2[0]).writeStringLatin1(strArr2[1]).writeStringLatin1(strArr2[2]).writeStringUTF16(strArr2[3]).writeStringLatin1(strArr2[4]).writeStringLatin1(strArr2[5]).writeStringLatin1(strArr2[6]).writeStringLatin1(strArr2[7])))));
        return 0;
    }

    /* renamed from: a */
    private int m723a(String[] strArr, String str) {
        return m1052c(AppController.m321a(this, 4214, new ByteBuffer().writeStringArr(strArr).writeStringLatin1(str)));
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
            c0043n.readInt();
            if (StringUtils.m3a(590694, c0043n.readWideStr())) {
                String[] strArrM55a = VCard.m55a(c0043n);
                MrimContact c0035fM717f = m717f(str);
                if (c0035fM717f != null) {
                    if (strArrM55a == null) {
                        c0035fM717f.m999p();
                    } else {
                        try {
                            c0035fM717f.f306l = new VCard();
                            c0035fM717f.f306l.m53a(strArrM55a[0], strArrM55a[1], strArrM55a[2], strArrM55a[3], strArrM55a[4], strArrM55a[5], strArrM55a[6], strArrM55a[7]);
                            c0035fM717f.f307m = true;
                        } catch (Throwable unused) {
                            c0035fM717f.m999p();
                        }
                        c0035fM717f.f308n.lastScale = -1;
                    }
                }
            }
        }
        NetworkUtils.m1212a(vector);
    }

    /* renamed from: b */
    public final void m730b(String str, String str2) {
        try {
            VCard c0003ac = this.f231g;
            String strM584b = AppState.m584b(525044);
            String str3 = AppState.f181d;
            c0003ac.m53a(str2, str, strM584b, str3, str3, str3, str3, str3);
        } catch (Throwable unused) {
            this.f231g.m61e();
        }
        this.f233K.lastScale = -1;
    }

    /* renamed from: a */
    public final void m731a(MapPoint c0014an) {
        try {
            VCard c0003ac = this.f231g;
            String strM810b = IOUtils.m810b(c0014an.f139g);
            String strM809a = IOUtils.m809a(c0014an.f138f);
            String strM584b = AppState.m584b(590588);
            String strM267a = c0014an.m267a();
            String str = AppState.f181d;
            c0003ac.m53a(strM810b, strM809a, strM584b, strM267a, str, str, StringUtils.m17c(Integer.toString(c0014an.f145m)), StringUtils.m17c(Integer.toString(c0014an.f144l)));
        } catch (Throwable unused) {
            this.f231g.m61e();
        }
        this.f233K.lastScale = -1;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo124a(ContactGroup abstractC0046q, String str) {
        int iMo124a = super.mo124a(abstractC0046q, str);
        if (0 != iMo124a) {
            return iMo124a;
        }
        MrimContactGroup c0010aj = (MrimContactGroup) abstractC0046q;
        return m1052c(m719a(new Object[]{AppController.m321a(this, 4123, new ByteBuffer().writeIntLE(c0010aj.f74a).writeIntLE(c0010aj.f75b).writeIntLE(0).writeStringUTF16(str).writeStringUTF16(str).writeIntLE(0)), ResourceManager.m967e(1), c0010aj, str}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo122a(String str) {
        int iMo122a = super.mo122a(str);
        if (0 != iMo122a) {
            return iMo122a;
        }
        ByteBuffer c0043n = new ByteBuffer();
        int size = (this.f313i.size() << 24) | 2;
        return m1052c(m719a(new Object[]{AppController.m321a(this, 4121, c0043n.writeIntLE(size).writeZeros(8).writeStringUTF16(str).writeZeros(12)), ResourceManager.m967e(4), str, ResourceManager.m967e(size)}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo123a(ContactGroup abstractC0046q) {
        int iMo123a = super.mo123a(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        MrimContactGroup c0010aj = (MrimContactGroup) abstractC0046q;
        ByteBuffer c0043nM1360p = new ByteBuffer().writeIntLE(c0010aj.f74a).writeIntLE(c0010aj.f75b | 1).writeIntLE(0);
        String str = c0010aj.f398f;
        return m1052c(m719a(new Object[]{AppController.m321a(this, 4123, c0043nM1360p.writeStringUTF16(str).writeStringUTF16(str).writeIntLE(0)), ResourceManager.m967e(3), c0010aj}));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int mo118b(Contact abstractC0041l) {
        int iMo118b = super.mo118b(abstractC0041l);
        if (0 != iMo118b) {
            return iMo118b;
        }
        if (abstractC0041l.mo143m()) {
            return m1074a(abstractC0041l, true);
        }
        MrimContact c0035f = (MrimContact) abstractC0041l;
        return m1052c(m719a(new Object[]{AppController.m321a(this, 4123, new ByteBuffer().writeIntLE(c0035f.f294a).writeIntLE(c0035f.f295b | 1).writeIntLE(c0035f.f296c).writeStringLatin1(c0035f.f297d).writeStringUTF16(c0035f.f376u).writeStringLatin1(c0035f.f300g)), ResourceManager.m967e(2), c0035f}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo114a(Contact abstractC0041l) {
        return m732g(((MrimContact) abstractC0041l).f297d);
    }

    /* renamed from: g */
    public final int m732g(String str) {
        return m1052c(AppController.m403a(this, str, 7));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int mo115b(Object obj) {
        String[] strArr = (String[]) obj;
        ByteBuffer c0043n = new ByteBuffer();
        for (int i = 0; i < strArr.length; i++) {
            if (i != 9) {
                String str = strArr[i];
                if (Utils.m535l(str)) {
                    c0043n.writeIntLE(i).writeString(str, (1 << i) & 28);
                }
            }
        }
        if (Utils.m535l(strArr[9])) {
            c0043n.writeIntLE(9).writeStringLatin1(strArr[9]);
        }
        return m1052c(m719a(new Object[]{AppController.m321a(this, 4137, c0043n), ResourceManager.m967e(8)}));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo112a(Contact abstractC0041l, Object[] objArr) {
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
        MrimContact c0035f = (MrimContact) abstractC0041l;
        if (c0035f.mo990d() && length == 0) {
            return 709;
        }
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            MrimContact c0035f2 = (MrimContact) enumerationElements.nextElement();
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
        return m1052c(m719a(new Object[]{AppController.m321a(this, 4123, new ByteBuffer().writeIntLE(c0035f.f294a).writeIntLE(c0035f.f295b).writeIntLE(c0035f.f296c).writeStringLatin1(c0035f.f297d).writeStringUTF16(str).writeStringLatin1(strM519a)), ResourceManager.m967e(0), c0035f, str, strM519a}));
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
                i = ((MrimContactGroup) vector.elementAt(i)).f74a != i2 ? i + 1 : 0;
            }
        }
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo734a(String str, String str2, String str3, ContactGroup abstractC0046q, boolean z) {
        int iMo734a = super.mo734a(str, str2, str3, abstractC0046q, z);
        if (0 != iMo734a) {
            return iMo734a;
        }
        MrimContact c0035fM717f = m717f(str);
        if (c0035fM717f == null || c0035fM717f.mo143m()) {
            m1052c(AppController.m395b(this, str));
            return m1052c(XmppContactGroup.m1024a(this, 0, str, str2, str3, (MrimContactGroup) abstractC0046q, z));
        }
        m1052c(ResourceManager.m988a(this, c0035fM717f, (MrimContactGroup) abstractC0046q));
        return m1052c(AppController.m321a(this, 4104, new ByteBuffer().writeIntLE(z ? 524300 : 12).writeStringLatin1(str).writeStringArray(new String[]{this.f339I, str3}).writeIntLE(0)));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int mo104c(Contact abstractC0041l) {
        MrimContact c0035f = (MrimContact) abstractC0041l;
        if (c0035f.mo143m()) {
            return m1052c(XmppContactGroup.m1024a(this, 48, c0035f.f297d, c0035f.f376u, AppState.f181d, m718f(), false));
        }
        int i = c0035f.f295b;
        return m1052c(ResourceManager.m987a(this, c0035f, (i & 16) != 0 ? i & (-49) : i | 16 | 32));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int mo105d(Contact abstractC0041l) {
        MrimContact c0035f = (MrimContact) abstractC0041l;
        int i = c0035f.f295b ^ 8;
        int i2 = i;
        if ((i & 8) != 0) {
            i2 &= -5;
        }
        return m1052c(ResourceManager.m987a(this, c0035f, i2));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int mo106e(Contact abstractC0041l) {
        MrimContact c0035f = (MrimContact) abstractC0041l;
        int i = c0035f.f295b ^ 4;
        int i2 = i;
        if ((i & 4) != 0) {
            i2 &= -9;
        }
        return m1052c(ResourceManager.m987a(this, c0035f, i2));
    }

    @Override // p000.Account
    /* renamed from: f */
    public final int mo735f(Contact abstractC0041l) {
        int iMo735f = super.mo735f(abstractC0041l);
        return 0 != iMo735f ? iMo735f : m1052c(AppController.m321a(this, 4104, new ByteBuffer().writeIntLE(1024).writeStringLatin1(((MrimContact) abstractC0041l).f297d).writeIntLE(0).writeIntLE(0)));
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int mo113a(Contact abstractC0041l, ContactGroup abstractC0046q, ContactGroup abstractC0046q2) {
        int iMo113a = super.mo113a(abstractC0041l, abstractC0046q, abstractC0046q2);
        return 0 != iMo113a ? iMo113a : m1052c(ResourceManager.m988a(this, (MrimContact) abstractC0041l, (MrimContactGroup) abstractC0046q2));
    }

    @Override // p000.Account
    /* renamed from: l */
    public final int mo120l() {
        int iMo120l = super.mo120l();
        if (0 != iMo120l) {
            return iMo120l;
        }
        m1052c(AppController.m321a(this, 4194, (ByteBuffer) null));
        m1061F();
        this.f324t = mo89g();
        return 0;
    }

    /* renamed from: k */
    private final String m736k(String str) {
        MrimContact c0035fM717f = m717f(str);
        return c0035fM717f != null ? c0035fM717f.f376u : str;
    }

    /* renamed from: l */
    private final StringBuffer m737l(String str) {
        return Utils.m496a(NetworkUtils.m1217h().append(m736k(str)), true).append('\n');
    }

    /* renamed from: a */
    public final void m738a(String str, String str2, String str3, String str4, long j) {
        MrimContact c0035fM717f = m717f(str);
        MrimContact c0035f = c0035fM717f;
        if (null == c0035fM717f) {
            String str5 = AppState.f181d;
            ContactGroup abstractC0046q = this.f334D;
            MrimContact c0035f2 = new MrimContact(this, 0, 65664, 3, str, str3, 0, 0, str5, str5, str5);
            abstractC0046q.m1401b((Object) c0035f2);
            if (this.f313i.size() > 0) {
                m1052c(XmppContactGroup.m1024a(this, 128, str, str3, str5, m718f(), false));
            }
            c0035f = c0035f2;
        }
        this.f328x++;
        c0035f.m1231a(j, m737l(str4).append(str2));
    }

    /* renamed from: a */
    public final void m739a(String str, String str2, String str3, String str4, ByteBuffer c0043n, long j) {
        MrimContact c0035fM717f = m717f(str);
        if (null == c0035fM717f) {
            return;
        }
        this.f328x++;
        StringBuffer stringBufferAppend = m737l(str4).append(str2);
        c0043n.readInt();
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                c0035fM717f.m1231a(j, stringBufferAppend);
                return;
            }
            Utils.m496a(stringBufferAppend.append(m736k(c0043n.readWideStr())), iM1328e != 0);
        }
    }

    /* renamed from: h */
    public final void m740h(String str) {
        if (StringUtils.m6a(str, this.f315k) || m717f(str) != null) {
            return;
        }
        m741a(str, 16);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final Contact mo107b(String str) {
        return m741a(str, 13);
    }

    /* renamed from: a */
    private final Contact m741a(String str, int i) {
        String str2 = AppState.f181d;
        ContactGroup abstractC0046q = this.f334D;
        MrimContact c0035f = new MrimContact(this, 0, 65536, 3, str, str, 0, 0, str2, str2, str2);
        abstractC0046q.m1401b((Object) c0035f);
        m1052c(AppController.m403a(this, str, i));
        return c0035f;
    }

    @Override // p000.Account
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
            this.f228d = NetworkUtils.m1213g();
        }
        Object objM475a = JsonParser.getValue(obj, AppState.m584b(329785));
        for (int i = 0; i < ((Vector) objM475a).size(); i++) {
            Object objM482e = JsonParser.getVectorElement(objM475a, i);
            ChatRoom c0052wM745h = m745h(JsonParser.getIntValue(objM482e, AppState.m584b(132297)));
            if (c0052wM745h == null) {
                this.f228d.addElement(new ChatRoom(objM482e));
            } else {
                c0052wM745h.m1412a(objM482e);
            }
        }
        this.f230f = JsonParser.getStringValue(obj, AppState.m584b(526385));
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
                if (((ChatRoom) enumerationElements.nextElement()).f409a == i) {
                    z2 = true;
                }
            }
        } while (z2);
        this.f228d.addElement(new ChatRoom(i));
    }

    /* renamed from: h */
    public final ChatRoom m745h(int i) {
        Enumeration enumerationElements = this.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
            if (c0052w.f409a == i) {
                return c0052w;
            }
        }
        return null;
    }

    /* renamed from: W */
    public final ChatRoom m746W() {
        return (ChatRoom) this.f228d.lastElement();
    }

    /* renamed from: i */
    public final ChatRoom m747i(String str) {
        Enumeration enumerationElements = this.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
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
            ChatRoom c0052w = (ChatRoom) this.f228d.elementAt(iM742V);
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
    public final ChatRoom m749X() {
        ChatRoom c0052wM750m = m750m(AppState.m584b(897));
        return c0052wM750m != null ? c0052wM750m : m750m(AppState.m584b(892));
    }

    /* renamed from: m */
    private ChatRoom m750m(String str) {
        Enumeration enumerationElements = this.f228d.elements();
        while (enumerationElements.hasMoreElements()) {
            ChatRoom c0052w = (ChatRoom) enumerationElements.nextElement();
            if (c0052w.f410b.equals(str)) {
                return c0052w;
            }
        }
        return null;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return 6;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.f232h && this.f231g != null && this.f231g.m59c();
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.f232h = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.f232h = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        if (this.f231g != null) {
            return (int) this.f231g.m56a();
        }
        return 0;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        if (this.f231g != null) {
            return (int) this.f231g.m57b();
        }
        return 0;
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        String strM584b;
        int i;
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        if (this.f231g.f24l) {
            stringBufferM1217h.append(AppState.m584b(489));
            String str = this.f231g.f16d;
            if (Utils.m535l(str)) {
                stringBufferM1217h.append(str).append('.').append(' ');
            }
            stringBufferM1217h.append("Уточнить?");
        } else {
            stringBufferM1217h.append(AppState.m584b(488));
            if (AppController.m439R().size() > 1) {
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
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        return this.f231g.m60d();
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return this.f231g.m59c() && !this.f231g.f24l;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.f233K.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.f233K.getHeight(i, this);
    }

    /* renamed from: a */
    public final void m751a(SearchEntry c0050u) {
        if (m1056C()) {
            c0050u.id = this.f319o;
            m1053d(AppController.m321a(this, 4162, new ByteBuffer().writeIntLE(1).writeStringLatin1(c0050u.query)));
            this.f234L.addElement(c0050u);
        }
    }
}
