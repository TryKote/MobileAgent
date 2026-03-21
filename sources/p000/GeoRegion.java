package p000;

/* renamed from: x */
/* loaded from: MobileAgent_3.9.jar:x.class */
public final class GeoRegion {

    /* renamed from: a */
    public String f421a;

    /* renamed from: b */
    public long f422b;

    /* renamed from: c */
    public long f423c;

    /* renamed from: d */
    public long f424d;

    /* renamed from: e */
    public long f425e;

    /* renamed from: f */
    public long f426f;

    /* renamed from: g */
    public long f427g;

    /* renamed from: h */
    public int f428h;

    /* renamed from: i */
    public int f429i;

    /* renamed from: j */
    public String f430j;

    /* renamed from: k */
    public int f431k;

    private GeoRegion() {
        this.f428h = -1;
        this.f431k = 16;
    }

    public GeoRegion(String str, long j, long j2, long j3, long j4) {
        this();
        this.f421a = str;
        this.f422b = j;
        this.f423c = j2;
        this.f424d = j3;
        this.f425e = j4;
    }

    public GeoRegion(C0043n c0043n) {
        this(c0043n.m1335e((String) null), c0043n.m1341m(), c0043n.m1341m(), c0043n.m1341m(), c0043n.m1341m());
        this.f430j = c0043n.m1335e((String) null);
        this.f426f = c0043n.m1341m();
        this.f427g = c0043n.m1341m();
        this.f431k = c0043n.m1328e();
    }

    /* renamed from: a */
    public final boolean m1426a(long j, long j2) {
        return j < this.f424d && j > this.f422b && j2 < this.f423c && j2 > this.f425e;
    }
}
