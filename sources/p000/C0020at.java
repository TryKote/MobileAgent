package p000;

/* renamed from: at */
/* loaded from: MobileAgent_3.9.jar:at.class */
public final class C0020at implements ListItem, Identifiable {

    /* renamed from: g */
    private int f161g;

    /* renamed from: h */
    private int f162h;

    /* renamed from: a */
    public String f163a;

    /* renamed from: b */
    public String f164b;

    /* renamed from: c */
    public String f165c;

    /* renamed from: d */
    public String f166d;

    /* renamed from: e */
    public int f167e;

    /* renamed from: i */
    private String f168i;

    /* renamed from: j */
    private int f169j;

    /* renamed from: f */
    private boolean f160f = true;

    /* renamed from: k */
    private SizeCache f170k = new SizeCache();

    public C0020at(String str, int i, int i2, String str2, String str3, String str4, String str5, int i3, int i4) {
        this.f168i = str;
        this.f162h = i2;
        this.f161g = i;
        this.f163a = str3;
        this.f164b = str2;
        this.f165c = str5;
        this.f166d = str4;
        this.f167e = i3;
        this.f169j = i4;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int mo276r() {
        return 7;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean mo277s() {
        return this.f160f;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void mo278t() {
        this.f160f = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void mo279u() {
        this.f160f = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int mo274v() {
        return this.f161g;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int mo275w() {
        return this.f162h;
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String mo273x() {
        return NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(450)).append(this.f167e).append(AppState.m584b(447 + Utils.m540f(this.f167e))).append(')'));
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int mo280y() {
        return this.f169j;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean mo281z() {
        return true;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int mo282a(int i) {
        return this.f170k.m1405a(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int mo283b(int i) {
        return this.f170k.m1406b(i, this);
    }

    @Override // p000.Identifiable
    /* renamed from: a */
    public final String mo545a() {
        return this.f168i;
    }
}
