package p000;

/* renamed from: p */
/* loaded from: MobileAgent_3.9.jar:p.class */
public final class C0045p implements ListItem, Identifiable {

    /* renamed from: e */
    private boolean f386e;

    /* renamed from: f */
    private int f387f;

    /* renamed from: g */
    private int f388g;

    /* renamed from: h */
    private String f389h;

    /* renamed from: a */
    public String f390a;

    /* renamed from: b */
    public String f391b;

    /* renamed from: c */
    public int f392c;

    /* renamed from: d */
    public int f393d;

    /* renamed from: i */
    private int f394i;

    /* renamed from: j */
    private SizeCache f395j;

    private C0045p() {
    }

    public C0045p(int i, int i2, String str, int i3) {
        this.f387f = i;
        this.f388g = i2;
        this.f389h = str;
        this.f394i = i3;
        this.f386e = true;
        this.f395j = new SizeCache();
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int mo276r() {
        return 8;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean mo277s() {
        return this.f386e;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void mo278t() {
        this.f386e = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void mo279u() {
        this.f386e = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int mo274v() {
        return this.f387f;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int mo275w() {
        return this.f388g;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x007c  */
    @Override // p000.ListItem
    /* renamed from: x */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final String mo273x() {
        int i;
        StringBuffer stringBufferAppend = C0040k.m1217h().append(Utils.m535l(this.f391b) ? this.f391b : AppState.m584b(451));
        if (this.f392c > 0) {
            StringBuffer stringBufferAppend2 = stringBufferAppend.append(',').append(' ').append(this.f392c);
            if (this.f392c >= 100) {
                i = 323;
            } else if (this.f392c < 5 || this.f392c > 20) {
                int i2 = this.f392c % 10;
                i = i2 == 1 ? 321 : (i2 < 2 || i2 > 4) ? 320 : 322;
            } else {
                i = 320;
            }
            stringBufferAppend2.append(AppState.m584b(i));
        }
        if (Utils.m535l(this.f389h)) {
            stringBufferAppend.append(',').append(' ').append(this.f389h);
        }
        return C0040k.m1215a(stringBufferAppend);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int mo280y() {
        return this.f394i;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean mo281z() {
        return true;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int mo282a(int i) {
        return this.f395j.m1405a(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int mo283b(int i) {
        return this.f395j.m1406b(i, this);
    }

    @Override // p000.Identifiable
    /* renamed from: a */
    public final String mo545a() {
        return this.f390a;
    }
}
