package p000;

/* renamed from: aj */
/* loaded from: MobileAgent_3.9.jar:aj.class */
public final class C0010aj extends AbstractC0046q {

    /* renamed from: a */
    public int f74a;

    /* renamed from: b */
    public int f75b;

    public C0010aj(AbstractC0037h abstractC0037h, int i, int i2, String str) {
        super(abstractC0037h);
        this.f74a = i;
        this.f75b = i2;
        m1403c(str);
    }

    public C0010aj(AbstractC0037h abstractC0037h, C0043n c0043n) {
        super(abstractC0037h);
        this.f75b = c0043n.m1328e();
        m1403c(c0043n.m1335e((String) null));
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                this.f399g = c0043n.m1340l();
                return;
            }
            m1401b((Object) new C0035f(abstractC0037h, c0043n));
        }
    }

    public C0010aj() {
        super(null);
    }

    @Override // p000.AbstractC0046q
    /* renamed from: a */
    public final void mo196a(C0043n c0043n, boolean z) {
        c0043n.m1360p(this.f75b);
        c0043n.m1309b(this.f398f);
        super.mo196a(c0043n, z);
    }

    @Override // p000.AbstractC0046q
    /* renamed from: b */
    public final int mo197b() {
        return this.f75b;
    }

    @Override // p000.AbstractC0046q
    /* renamed from: a */
    public final boolean mo198a() {
        return this.f74a != -1;
    }
}
