package p000;

/* renamed from: s */
/* loaded from: MobileAgent_3.9.jar:s.class */
public final class SizeCache {

    /* renamed from: b */
    private int f401b;

    /* renamed from: c */
    private int f402c;

    /* renamed from: a */
    public int f403a = -1;

    /* renamed from: a */
    public final int m1405a(int i, ListItem interfaceC0044o) {
        if (m1408a(i)) {
            return this.f401b;
        }
        m1407a(i, interfaceC0044o.mo274v(), interfaceC0044o.mo275w());
        return this.f401b;
    }

    /* renamed from: b */
    public final int m1406b(int i, ListItem interfaceC0044o) {
        if (m1408a(i)) {
            return this.f402c;
        }
        m1407a(i, interfaceC0044o.mo274v(), interfaceC0044o.mo275w());
        return this.f402c;
    }

    /* renamed from: a */
    private final void m1407a(int i, int i2, int i3) {
        this.f401b = (int) AppController.m317a(i2, i);
        this.f402c = (int) AppController.m317a(i3, i);
        this.f403a = i;
    }

    /* renamed from: a */
    private final boolean m1408a(int i) {
        return this.f403a == i;
    }
}
