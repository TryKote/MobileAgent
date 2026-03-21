package p000;

import java.util.Vector;

/* renamed from: q */
/* loaded from: MobileAgent_3.9.jar:q.class */
public abstract class ContactGroup implements Sortable {

    /* renamed from: d */
    public final Account f396d;

    /* renamed from: e */
    public final Vector f397e = NetworkUtils.m1213g();

    /* renamed from: f */
    public String f398f;

    /* renamed from: g */
    public boolean f399g;

    /* renamed from: a */
    private String f400a;

    public ContactGroup(Account abstractC0037h) {
        this.f396d = abstractC0037h;
    }

    /* renamed from: a */
    public final void m1393a(Contact abstractC0041l) {
        this.f397e.removeElement(abstractC0041l);
    }

    /* renamed from: e */
    public final Contact m1394e(int i) {
        return (Contact) this.f397e.elementAt(i);
    }

    /* renamed from: a */
    public void mo196a(ByteBuffer c0043n, boolean z) {
        int size = this.f397e.size();
        c0043n.m1360p(size);
        for (int i = 0; i < size; i++) {
            m1394e(i).mo136a(c0043n);
        }
        c0043n.m1322a(this.f399g);
        if (z) {
            this.f397e.removeAllElements();
        }
        Utils.m526b(this.f397e);
    }

    /* renamed from: f */
    public final MenuItem m1395f(int i) {
        MenuItem c0032cM896a = MenuItem.m887a(new ByteBuffer().m1321f(35).m1382s(this.f396d.f314j).m1321f(35).m1382s(mo197b()).m1337i()).m896a(this.f399g ? 30 : 31);
        c0032cM896a.f265d = this;
        if (mo198a()) {
            MenuItem c0032cM901a = c0032cM896a.m901a(NetworkUtils.m1215a(NetworkUtils.m1217h().append(this.f398f).append(' ').append('(')), 1, 0);
            int i2 = 0;
            int size = this.f397e.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Contact abstractC0041lM1394e = m1394e(size);
                if (abstractC0041lM1394e.f371p && !abstractC0041lM1394e.mo144l() && !abstractC0041lM1394e.mo142k() && !abstractC0041lM1394e.mo990d() && !abstractC0041lM1394e.mo996n()) {
                    i2++;
                }
            }
            MenuItem c0032cM901a2 = c0032cM901a.m901a(StringUtils.m17c(Integer.toString(i2)), 1, 20);
            StringBuffer stringBufferAppend = NetworkUtils.m1217h().append('/');
            int size2 = this.f397e.size();
            int i3 = size2;
            int i4 = size2;
            while (true) {
                i4--;
                if (i4 < 0) {
                    break;
                }
                Contact abstractC0041lM1394e2 = m1394e(i4);
                if (abstractC0041lM1394e2.mo990d() || abstractC0041lM1394e2.mo144l() || abstractC0041lM1394e2.mo142k() || abstractC0041lM1394e2.mo996n()) {
                    i3--;
                }
            }
            c0032cM901a2.m901a(NetworkUtils.m1215a(stringBufferAppend.append(i3).append(')')), 1, 0);
        } else if (i >= 0) {
            c0032cM896a.m901a(NetworkUtils.m1215a(NetworkUtils.m1217h().append(this.f398f).append(' ').append('(').append(i).append(')')), 1, 0);
        } else {
            c0032cM896a.m901a(this.f398f, 1, 0);
        }
        return c0032cM896a;
    }

    /* renamed from: m */
    public int mo1396m() {
        return this.f396d.mo123a(this);
    }

    /* renamed from: n */
    public int mo1397n() {
        this.f399g = !this.f399g;
        return 4;
    }

    /* renamed from: o */
    public final boolean m1398o() {
        return !this.f399g;
    }

    /* renamed from: b */
    public int mo1399b(String str) {
        return this.f396d.mo124a(this, str);
    }

    /* renamed from: b */
    public final boolean m1400b(Contact abstractC0041l) {
        return this.f397e.contains(abstractC0041l);
    }

    /* renamed from: a */
    public abstract boolean mo198a();

    @Override // p000.Sortable
    /* renamed from: a */
    public final int mo1237a(Object obj) {
        return this.f400a.compareTo(((ContactGroup) obj).f400a);
    }

    /* renamed from: b */
    public final void m1401b(Object obj) {
        this.f397e.addElement(obj);
    }

    /* renamed from: c */
    public final void m1402c(Object obj) {
        this.f397e.removeElement(obj);
    }

    /* renamed from: c */
    public final void m1403c(String str) {
        if (StringUtils.m6a(str, this.f398f)) {
            return;
        }
        m1404d(str);
        C0015ao.f152f = true;
    }

    /* renamed from: d */
    public final void m1404d(String str) {
        this.f398f = str;
        this.f400a = StringUtils.m17c(str.toLowerCase());
    }

    /* renamed from: b */
    public abstract int mo197b();

    public final String toString() {
        return this.f398f;
    }
}
