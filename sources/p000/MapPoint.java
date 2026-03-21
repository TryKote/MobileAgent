package p000;

/* renamed from: an */
/* loaded from: MobileAgent_3.9.jar:an.class */
public final class MapPoint implements ListItem {

    /* renamed from: a */
    public String f133a;

    /* renamed from: b */
    public long f134b;

    /* renamed from: c */
    public long f135c;

    /* renamed from: d */
    public long f136d;

    /* renamed from: e */
    public long f137e;

    /* renamed from: f */
    public long f138f;

    /* renamed from: g */
    public long f139g;

    /* renamed from: h */
    public int f140h;

    /* renamed from: i */
    public boolean f141i;

    /* renamed from: j */
    public boolean f142j;

    /* renamed from: k */
    public int f143k;

    /* renamed from: l */
    public int f144l;

    /* renamed from: m */
    public int f145m;

    /* renamed from: n */
    private SizeCache f146n;

    public MapPoint() {
        this.f146n = new SizeCache();
    }

    public MapPoint(String str, long j, long j2, long j3, long j4, long j5, long j6, int i) {
        this.f133a = str;
        this.f134b = 0L;
        this.f135c = 0L;
        this.f136d = 0L;
        this.f137e = 0L;
        this.f138f = j5;
        this.f139g = j6;
        this.f140h = i;
        this.f146n = new SizeCache();
    }

    public MapPoint(String str, long j, long j2, int i) {
        this.f133a = str;
        this.f138f = j;
        this.f139g = j2;
        this.f140h = i;
        this.f146n = new SizeCache();
    }

    public MapPoint(String str) {
        try {
            this.f133a = Conversation.m1099b(str);
            this.f138f = IOUtils.m807b(Conversation.m1100c(str));
            this.f139g = IOUtils.m808c(Conversation.m1101d(str));
            this.f140h = Integer.parseInt(Conversation.m1102e(str));
        } catch (Throwable unused) {
        }
        this.f146n = new SizeCache();
    }

    public MapPoint(ByteBuffer c0043n) {
        this.f133a = c0043n.m1335e((String) null);
        this.f134b = c0043n.m1341m();
        this.f135c = c0043n.m1341m();
        this.f136d = c0043n.m1341m();
        this.f137e = c0043n.m1341m();
        this.f138f = c0043n.m1341m();
        this.f139g = c0043n.m1341m();
        this.f140h = c0043n.m1328e();
        this.f143k = c0043n.m1328e();
        this.f145m = c0043n.m1328e();
        this.f144l = c0043n.m1328e();
        this.f146n = new SizeCache();
    }

    /* renamed from: a */
    public final String m267a() {
        int iIndexOf;
        try {
            int iIndexOf2 = this.f133a.indexOf(59);
            return (iIndexOf2 < 0 || (iIndexOf = this.f133a.indexOf(59, iIndexOf2 + 1)) < 0) ? this.f133a : StringUtils.m13b(this.f133a, iIndexOf);
        } catch (Throwable unused) {
            return this.f133a;
        }
    }

    /* renamed from: b */
    public final void m268b() {
        this.f141i = true;
        this.f142j = true;
    }

    /* renamed from: c */
    public final void m269c() {
        this.f141i = false;
        this.f142j = true;
    }

    /* renamed from: c */
    public final long m270c(int i) {
        return AppController.m317a(this.f138f, i);
    }

    /* renamed from: d */
    public final long m271d(int i) {
        return AppController.m317a(this.f139g, i);
    }

    /* renamed from: d */
    public final String m272d() {
        return ResourceManager.m975a(this.f138f, this.f139g, this.f140h, this.f133a);
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String mo273x() {
        return m267a();
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int mo274v() {
        return (int) this.f138f;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int mo275w() {
        return (int) this.f139g;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int mo276r() {
        return this.f143k;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean mo277s() {
        return this.f141i;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void mo278t() {
        this.f141i = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void mo279u() {
        this.f141i = true;
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int mo280y() {
        if (this.f144l != 0) {
            return m284e(this.f144l);
        }
        return 10;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean mo281z() {
        return true;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int mo282a(int i) {
        return this.f146n.m1405a(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int mo283b(int i) {
        return this.f146n.m1406b(i, this);
    }

    /* renamed from: e */
    public static final int m284e(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 8:
            case 9:
            case 13:
            case 2000:
            case 2001:
                return 5;
            case 5:
            case 6:
            case 10:
            case 11:
            case 14:
            case 15:
            case 16:
            case 18:
            case 19:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 444:
            case 445:
            case 456:
            case 480:
            case 509:
            case 510:
            case 558:
            case 559:
            case 560:
                return 13;
            case 30:
            case 32:
            case 33:
            case 40:
            case 41:
            case 43:
            case 44:
            case 45:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 53:
            case 54:
            case 132:
            case 133:
            case 134:
            case 451:
                return 16;
            case 154:
            case 155:
            case 156:
            case 157:
                return 17;
            case 476:
                return 3;
            case 504:
                return 4;
            default:
                return 10;
        }
    }
}
