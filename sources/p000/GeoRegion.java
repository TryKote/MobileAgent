package p000;

/* renamed from: x */
/* loaded from: MobileAgent_3.9.jar:x.class */
public final class GeoRegion {

    /* renamed from: a */
    public String name;

    /* renamed from: b */
    public long minLat;

    /* renamed from: c */
    public long maxLon;

    /* renamed from: d */
    public long maxLat;

    /* renamed from: e */
    public long minLon;

    /* renamed from: f */
    public long centerLat;

    /* renamed from: g */
    public long centerLon;

    /* renamed from: h */
    public int zoomLevel;

    /* renamed from: i */
    public int mapType;

    /* renamed from: j */
    public String description;

    /* renamed from: k */
    public int precision;

    private GeoRegion() {
        this.zoomLevel = -1;
        this.precision = 16;
    }

    public GeoRegion(String str, long j, long j2, long j3, long j4) {
        this();
        this.name = str;
        this.minLat = j;
        this.maxLon = j2;
        this.maxLat = j3;
        this.minLon = j4;
    }

    public GeoRegion(ByteBuffer c0043n) {
        this(c0043n.m1335e((String) null), c0043n.m1341m(), c0043n.m1341m(), c0043n.m1341m(), c0043n.m1341m());
        this.description = c0043n.m1335e((String) null);
        this.centerLat = c0043n.m1341m();
        this.centerLon = c0043n.m1341m();
        this.precision = c0043n.m1328e();
    }

    /* renamed from: a */
    public final boolean containsPoint(long j, long j2) {
        return j < this.maxLat && j > this.minLat && j2 < this.maxLon && j2 > this.minLon;
    }
}
