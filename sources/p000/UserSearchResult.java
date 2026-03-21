package p000;

/* renamed from: p */
/* loaded from: MobileAgent_3.9.jar:p.class */
public final class UserSearchResult implements ListItem, Identifiable {

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

    private UserSearchResult() {
    }

    public UserSearchResult(int i, int i2, String str, int i3) {
        this.f387f = i;
        this.f388g = i2;
        this.f389h = str;
        this.f394i = i3;
        this.f386e = true;
        this.f395j = new SizeCache();
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return 8;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.f386e;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.f386e = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.f386e = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        return this.f387f;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        return this.f388g;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x007c  */
    @Override // p000.ListItem
    /* renamed from: x */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final String getText() {
        int i;
        StringBuffer stringBufferAppend = NetworkUtils.m1217h().append(Utils.nonEmpty(this.f391b) ? this.f391b : AppState.getString(451));
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
            stringBufferAppend2.append(AppState.getString(i));
        }
        if (Utils.nonEmpty(this.f389h)) {
            stringBufferAppend.append(',').append(' ').append(this.f389h);
        }
        return NetworkUtils.m1215a(stringBufferAppend);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        return this.f394i;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return true;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.f395j.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.f395j.getHeight(i, this);
    }

    @Override // p000.Identifiable
    /* renamed from: a */
    public final String getId() {
        return this.f390a;
    }
}
