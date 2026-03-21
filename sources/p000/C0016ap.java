package p000;

/* renamed from: ap */
/* loaded from: MobileAgent_3.9.jar:ap.class */
public final class C0016ap extends AbstractC0046q {

    /* renamed from: a */
    public int f157a;

    public C0016ap(C0033d c0033d, int i, String str) {
        super(c0033d);
        this.f157a = i;
        m1403c(str);
    }

    public C0016ap(C0033d c0033d, C0043n c0043n) {
        super(c0033d);
        this.f157a = c0043n.m1328e();
        m1403c(c0043n.m1335e((String) null));
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                this.f399g = c0043n.m1340l();
                return;
            }
            m1401b((Object) new C0009ai(c0033d, c0043n));
        }
    }

    public C0016ap() {
        super(null);
    }

    @Override // p000.AbstractC0046q
    /* renamed from: a */
    public final void mo196a(C0043n c0043n, boolean z) {
        c0043n.m1360p(this.f157a);
        c0043n.m1309b(this.f398f);
        super.mo196a(c0043n, z);
    }

    @Override // p000.AbstractC0046q
    /* renamed from: b */
    public final int mo197b() {
        return this.f157a;
    }

    /* renamed from: a */
    public final C0043n m465a(String str, int i, int i2) {
        C0043n c0043nM1357m = new C0043n().m1357m(200);
        int i3 = (i2 != -1 ? 2 : 0) - (i != -1 ? 2 : 0);
        int size = this.f397e.size();
        C0043n c0043nM1357m2 = c0043nM1357m.m1357m(i3 + (size << 1));
        for (int i4 = 0; i4 < size; i4++) {
            int i5 = ((C0009ai) m1394e(i4)).f55a;
            if (i != i5) {
                c0043nM1357m2.m1357m(i5);
            }
        }
        if (i2 != -1) {
            c0043nM1357m2.m1357m(i2);
        }
        return new C0043n().m1376j(str).m1357m(this.f157a).m1359o(1).m1326b(c0043nM1357m2);
    }

    @Override // p000.AbstractC0046q
    /* renamed from: a */
    public final boolean mo198a() {
        return this.f157a >= 0;
    }
}
