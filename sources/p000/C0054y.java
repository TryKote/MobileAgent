package p000;

import java.util.Vector;

/* renamed from: y */
/* loaded from: MobileAgent_3.9.jar:y.class */
public final class C0054y extends AbstractC0046q {

    /* renamed from: a */
    private AbstractC0046q f432a;

    /* renamed from: b */
    private int f433b;

    public C0054y(AbstractC0046q abstractC0046q, int i) {
        super(abstractC0046q.f396d);
        this.f433b = i;
        this.f432a = abstractC0046q;
        m1404d(abstractC0046q.f398f);
        this.f399g = abstractC0046q.f399g;
    }

    public C0054y() {
        super(null);
        this.f432a = null;
    }

    @Override // p000.AbstractC0046q
    /* renamed from: m */
    public final int mo1396m() {
        return this.f432a.mo1396m();
    }

    @Override // p000.AbstractC0046q
    /* renamed from: b */
    public final int mo1399b(String str) {
        return this.f432a.mo1399b(str);
    }

    @Override // p000.AbstractC0046q
    /* renamed from: n */
    public final int mo1397n() {
        if (this.f399g) {
            String str = this.f398f;
            Vector vectorM614m = AppState.m614m(1241);
            int size = vectorM614m.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                AbstractC0037h abstractC0037h = (AbstractC0037h) vectorM614m.elementAt(size);
                int size2 = abstractC0037h.f313i.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        break;
                    }
                    AbstractC0046q abstractC0046qM1082g = abstractC0037h.m1082g(size2);
                    if (StringUtils.m6a(str, abstractC0046qM1082g.f398f)) {
                        abstractC0046qM1082g.f399g = false;
                    }
                }
                if (str.equals(abstractC0037h.f334D.f398f)) {
                    abstractC0037h.f334D.f399g = false;
                }
                if (str.equals(abstractC0037h.f335E.f398f)) {
                    abstractC0037h.f335E.f399g = false;
                }
                if (str.equals(abstractC0037h.f338H.f398f)) {
                    abstractC0037h.f338H.f399g = false;
                }
                if (str.equals(abstractC0037h.f336F.f398f)) {
                    abstractC0037h.f336F.f399g = false;
                }
            }
        } else {
            String str2 = this.f398f;
            Vector vectorM614m2 = AppState.m614m(1241);
            int size3 = vectorM614m2.size();
            while (true) {
                size3--;
                if (size3 < 0) {
                    break;
                }
                AbstractC0037h abstractC0037h2 = (AbstractC0037h) vectorM614m2.elementAt(size3);
                int size4 = abstractC0037h2.f313i.size();
                while (true) {
                    size4--;
                    if (size4 < 0) {
                        break;
                    }
                    AbstractC0046q abstractC0046qM1082g2 = abstractC0037h2.m1082g(size4);
                    if (StringUtils.m6a(str2, abstractC0046qM1082g2.f398f)) {
                        abstractC0046qM1082g2.f399g = true;
                    }
                }
                if (str2.equals(abstractC0037h2.f334D.f398f)) {
                    abstractC0037h2.f334D.f399g = true;
                }
                if (str2.equals(abstractC0037h2.f335E.f398f)) {
                    abstractC0037h2.f335E.f399g = true;
                }
                if (str2.equals(abstractC0037h2.f338H.f398f)) {
                    abstractC0037h2.f338H.f399g = true;
                }
                if (str2.equals(abstractC0037h2.f336F.f398f)) {
                    abstractC0037h2.f336F.f399g = true;
                }
            }
        }
        return super.mo1397n();
    }

    @Override // p000.AbstractC0046q
    /* renamed from: a */
    public final boolean mo198a() {
        return this.f433b >= 0 || this.f433b < -4;
    }

    @Override // p000.AbstractC0046q
    /* renamed from: b */
    public final int mo197b() {
        return this.f433b + 8323072;
    }
}
