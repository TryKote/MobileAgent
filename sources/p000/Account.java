package p000;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;

/* renamed from: h */
/* loaded from: MobileAgent_3.9.jar:h.class */
public abstract class Account {

    /* renamed from: i */
    public final Vector f313i;

    /* renamed from: j */
    public int f314j;

    /* renamed from: k */
    public String f315k;

    /* renamed from: l */
    public String f316l;

    /* renamed from: m */
    public final int[] f317m;

    /* renamed from: n */
    public final ByteBuffer f318n;

    /* renamed from: o */
    public int f319o;

    /* renamed from: p */
    public C0039j f320p;

    /* renamed from: q */
    public final Hashtable f321q;

    /* renamed from: r */
    public int f322r;

    /* renamed from: s */
    public int f323s;

    /* renamed from: t */
    public int f324t;

    /* renamed from: u */
    public int f325u;

    /* renamed from: v */
    public int f326v;

    /* renamed from: w */
    public int f327w;

    /* renamed from: x */
    public int f328x;

    /* renamed from: y */
    public int f329y;

    /* renamed from: z */
    public long f330z;

    /* renamed from: A */
    public long f331A;

    /* renamed from: B */
    public int f332B;

    /* renamed from: C */
    public final Vector f333C;

    /* renamed from: D */
    public ContactGroup f334D;

    /* renamed from: E */
    public final ContactGroup f335E;

    /* renamed from: F */
    public final ContactGroup f336F;

    /* renamed from: G */
    public final ContactGroup f337G;

    /* renamed from: H */
    public final ContactGroup f338H;

    /* renamed from: I */
    public String f339I;

    /* renamed from: J */
    public String f340J;

    /* renamed from: a */
    private int f341a;

    public Account(int i, String str, String str2) {
        this.f313i = C0040k.m1213g();
        this.f314j = i;
        this.f315k = str;
        this.f339I = str;
        this.f316l = str2;
        this.f317m = new int[9];
        this.f318n = new ByteBuffer();
        this.f321q = new Hashtable();
        this.f333C = C0040k.m1213g();
        ContactGroup abstractC0046qMo85b = mo85b();
        abstractC0046qMo85b.f399g = true;
        this.f335E = abstractC0046qMo85b;
        ContactGroup abstractC0046qMo87d = mo87d();
        abstractC0046qMo87d.f399g = true;
        this.f336F = abstractC0046qMo87d;
        ContactGroup abstractC0046qMo86c = mo86c();
        abstractC0046qMo86c.f399g = true;
        this.f337G = abstractC0046qMo86c;
        ContactGroup abstractC0046qMo88e = mo88e();
        abstractC0046qMo88e.f399g = true;
        this.f338H = abstractC0046qMo88e;
        this.f340J = Utils.m538m(str);
    }

    public Account(ByteBuffer c0043n) {
        this(c0043n.m1328e(), c0043n.m1338j(), c0043n.m1334g());
        c0043n.m1328e();
        for (int i = 2; i < 9; i++) {
            this.f317m[i] = c0043n.m1328e();
        }
        this.f325u = c0043n.m1328e();
        this.f339I = c0043n.m1335e((String) null);
    }

    /* renamed from: q */
    public final ByteBuffer m1050q() {
        return new ByteBuffer().m1382s(this.f314j).m1321f(95);
    }

    /* renamed from: A */
    public final String m1051A() {
        return new ByteBuffer().m1321f(35).m1382s(this.f314j).m1321f(35).m1314d(this.f315k).m1337i();
    }

    /* renamed from: b */
    public abstract ContactGroup mo85b();

    /* renamed from: d */
    public abstract ContactGroup mo87d();

    /* renamed from: c */
    public abstract ContactGroup mo86c();

    /* renamed from: e */
    public abstract ContactGroup mo88e();

    /* renamed from: h */
    public abstract int mo108h();

    /* renamed from: a */
    public void mo715a(ByteBuffer c0043n) {
        if (c0043n.m1328e() == 12) {
            this.f326v = c0043n.m1328e();
            this.f327w = c0043n.m1328e();
            this.f328x = c0043n.m1328e();
            c0043n.m1328e();
        }
    }

    /* renamed from: b */
    public void mo714b(ByteBuffer c0043n) {
        c0043n.m1360p(12).m1360p(this.f326v).m1360p(this.f327w).m1360p(this.f328x).m1360p(0);
    }

    /* renamed from: a */
    public Account mo82a(ByteBuffer c0043n, boolean z, boolean z2) {
        c0043n.m1321f(mo80a() | 8).m1360p(this.f314j).m1308a(this.f315k).m1308a(this.f316l).m1360p(0);
        for (int i = 2; i < 9; i++) {
            c0043n.m1360p(this.f317m[i]);
        }
        c0043n.m1360p(this.f325u);
        if (this.f339I != null) {
            c0043n.m1309b(this.f339I);
        } else {
            c0043n.m1360p(0);
        }
        if (z2) {
            if (!z) {
                this.f313i.removeAllElements();
            }
            int size = this.f313i.size();
            int i2 = size;
            c0043n.m1360p(size);
            while (true) {
                i2--;
                if (i2 < 0) {
                    break;
                }
                m1082g(i2).mo196a(c0043n, true);
            }
            this.f334D.mo196a(c0043n, true);
            this.f321q.clear();
        } else {
            this.f334D.mo196a(c0043n.m1360p(0), false);
        }
        return this;
    }

    /* renamed from: c */
    public final int m1052c(ByteBuffer c0043n) {
        if (m1056C()) {
            return m1053d(c0043n);
        }
        return 299;
    }

    /* renamed from: d */
    public final int m1053d(ByteBuffer c0043n) {
        C0015ao.m420b(this, c0043n.f384b);
        C0039j c0039j = this.f320p;
        if (c0039j.f348b != null) {
            throw new RuntimeException();
        }
        ByteBuffer c0043n2 = c0039j.f347a;
        int i = c0043n.f384b;
        if (i > 0) {
            synchronized (c0043n2) {
                c0043n2.m1300a(i);
                Utils.m490a((Object) c0043n.f383a, c0043n.f385c, (Object) c0043n2.f383a, c0043n2.f384b, i);
                c0043n.m1301b();
                c0043n2.f384b += i;
                c0043n2.m1299a();
            }
        }
        if (this.f330z <= 0) {
            return 0;
        }
        this.f331A = System.currentTimeMillis() + this.f330z;
        return 0;
    }

    /* renamed from: c */
    public final Account m1054c(String str) {
        this.f339I = Utils.m528a(str, this.f315k);
        return this;
    }

    /* renamed from: B */
    public final boolean m1055B() {
        return this.f322r > 0;
    }

    /* renamed from: C */
    public final boolean m1056C() {
        return this.f322r == 100;
    }

    /* renamed from: D */
    public final C0032c m1057D() {
        C0032c c0032cM898b = C0032c.m887a(m1051A()).m896a(mo108h()).m898b(this.f315k);
        c0032cM898b.f265d = this;
        return c0032cM898b;
    }

    /* renamed from: E */
    public final C0032c m1058E() {
        C0032c c0032cM896a = C0032c.m887a(m1051A()).m896a(mo108h()).m898b(this.f315k).m896a(244);
        c0032cM896a.f266e = true;
        c0032cM896a.f265d = this;
        return c0032cM896a;
    }

    /* renamed from: a */
    public abstract int mo80a();

    /* renamed from: a_ */
    public int mo914a_(int i) {
        if (m1055B()) {
            return 297;
        }
        if (i == 0) {
            char cCharAt = this.f316l.charAt(0);
            this.f341a = (cCharAt < 'A' || cCharAt > 'Z') ? 1 : 2;
        } else {
            this.f341a = i;
        }
        this.f323s = 0;
        this.f322r = 1;
        return 0;
    }

    /* renamed from: l */
    public int mo120l() {
        return !m1055B() ? 298 : 0;
    }

    /* renamed from: e */
    public final void m1059e(int i) {
        int[] iArr = this.f317m;
        int i2 = iArr[8];
        if (i != i2) {
            iArr[2] = 0;
            iArr[3] = 0;
            if ((i >>> 8) != (i2 >>> 8)) {
                iArr[4] = 0;
                iArr[5] = 0;
            }
            iArr[8] = i;
        }
    }

    /* renamed from: a */
    public final int m1060a(int i, int i2) {
        return this.f317m[i + i + i2];
    }

    /* renamed from: i */
    public abstract void mo97i() throws Throwable;

    /* renamed from: g */
    public abstract int mo89g();

    /* renamed from: F */
    public final void m1061F() {
        if (this.f320p != null) {
            this.f320p.f349c = 3;
        }
        this.f320p = null;
        this.f322r = 0;
    }

    /* renamed from: G */
    public final void m1062G() {
        String strM1215a;
        Throwable th = this.f320p.f348b;
        if (null == th) {
            strM1215a = AppState.m584b(951);
        } else {
            strM1215a = C0040k.m1215a(C0040k.m1217h().append(th).append(AppState.m584b(946)).append(AppState.m584b(th instanceof IllegalArgumentException ? 947 : th instanceof ConnectionNotFoundException ? 948 : th instanceof IOException ? 949 : th instanceof SecurityException ? 950 : 463)));
        }
        C0029bb.m784a(this, strM1215a);
        m1061F();
        this.f324t = mo89g();
    }

    /* renamed from: H */
    public final void m1063H() {
        C0029bb.m783a(this, 462);
        m1061F();
        this.f324t = mo89g();
    }

    /* renamed from: I */
    public final String m1064I() {
        return this.f341a != 3 ? this.f316l : C0040k.m1215a(C0040k.m1217h().append((char) (this.f316l.charAt(0) + ' ')).append(StringUtils.m15c(this.f316l, 1)));
    }

    /* renamed from: J */
    public final void m1065J() {
        m1061F();
        this.f324t = mo89g();
        if (this.f341a == 2) {
            mo914a_(3);
        } else {
            C0029bb.m783a(this, 461);
        }
    }

    /* renamed from: f */
    public final void m1066f(int i) {
        C0029bb.m778d((Object) C0040k.m1215a(C0040k.m1217h().append(AppState.m584b(459)).append(this).append(AppState.m584b(460)).append(AppState.m584b(457)).append(i)));
        m1061F();
        this.f324t = mo89g();
    }

    /* renamed from: K */
    public final void m1067K() {
        int size = this.f313i.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            ContactGroup abstractC0046qM1082g = m1082g(size);
            int size2 = abstractC0046qM1082g.f397e.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                Contact abstractC0041lM1394e = abstractC0046qM1082g.m1394e(size2);
                m1074a(abstractC0041lM1394e, false);
                C0015ao.m415b(abstractC0041lM1394e);
            }
            m1084c(abstractC0046qM1082g);
        }
    }

    /* renamed from: L */
    public final void m1068L() {
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            ((Contact) enumerationElements.nextElement()).mo134c();
        }
        C0015ao.f152f = true;
    }

    /* renamed from: c */
    public final Contact m1069c(Object obj) {
        return (Contact) this.f321q.get(obj);
    }

    /* renamed from: d */
    public final void m1070d(String str) {
        Contact abstractC0041lM1069c = m1069c((Object) str);
        if (abstractC0041lM1069c == null || abstractC0041lM1069c.mo143m() || abstractC0041lM1069c.mo144l() || abstractC0041lM1069c.mo996n()) {
            return;
        }
        C0015ao.m418c(abstractC0041lM1069c);
        AppState.m614m(1242).addElement(abstractC0041lM1069c);
        abstractC0041lM1069c.f372q = AppState.m586d(1531);
        abstractC0041lM1069c.f375t = true;
    }

    /* renamed from: e */
    public final void m1071e(String str) {
        Contact abstractC0041lM1069c = m1069c((Object) str);
        if (abstractC0041lM1069c != null) {
            C0015ao.m418c(abstractC0041lM1069c);
        }
    }

    /* renamed from: a */
    public final void m1072a(String str, long j, String str2) {
        Contact abstractC0041lM1069c = m1069c((Object) str);
        Contact abstractC0041lMo107b = abstractC0041lM1069c;
        if (abstractC0041lM1069c == null) {
            abstractC0041lMo107b = mo107b(str);
        }
        this.f328x++;
        abstractC0041lMo107b.m1232a(j, str2, 1);
    }

    /* renamed from: b */
    public abstract Contact mo107b(String str);

    /* renamed from: a */
    public final void m1073a(String str, long j, int i) {
        Contact abstractC0041lM1069c = m1069c((Object) str);
        if (abstractC0041lM1069c != null) {
            abstractC0041lM1069c.m1238a(j, i);
        }
    }

    /* renamed from: a */
    public int mo125a(Contact abstractC0041l, String str, long j) {
        return m1056C() ? 0 : 299;
    }

    /* renamed from: a */
    public final int m1074a(Contact abstractC0041l, boolean z) {
        if (abstractC0041l == null) {
            return 0;
        }
        Enumeration enumerationKeys = this.f321q.keys();
        while (true) {
            if (!enumerationKeys.hasMoreElements()) {
                break;
            }
            Hashtable hashtable = this.f321q;
            Object objNextElement = enumerationKeys.nextElement();
            if (hashtable.get(objNextElement) == abstractC0041l) {
                this.f321q.remove(objNextElement);
                break;
            }
        }
        int size = this.f313i.size();
        while (true) {
            size--;
            if (size < 0) {
                this.f334D.m1402c(abstractC0041l);
                C0015ao.m415b(abstractC0041l);
                return 0;
            }
            m1082g(size).m1402c(abstractC0041l);
        }
    }

    /* renamed from: a */
    public int mo734a(String str, String str2, String str3, ContactGroup abstractC0046q, boolean z) {
        if (!m1056C()) {
            return 299;
        }
        if (StringUtils.m1a(str2)) {
            return 301;
        }
        return StringUtils.m1a(str3) ? 302 : 0;
    }

    /* renamed from: a */
    public int mo112a(Contact abstractC0041l, Object[] objArr) {
        if (m1056C()) {
            return StringUtils.m1a((String) objArr[0]) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: b */
    public abstract int mo115b(Object obj);

    /* renamed from: a */
    public abstract int mo114a(Contact abstractC0041l);

    /* renamed from: a */
    public int mo122a(String str) {
        if (m1056C()) {
            return StringUtils.m1a(str) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: a */
    public int mo124a(ContactGroup abstractC0046q, String str) {
        if (abstractC0046q == this.f334D || abstractC0046q == this.f335E || abstractC0046q == this.f338H || abstractC0046q == this.f336F) {
            return 304;
        }
        if (m1056C()) {
            return StringUtils.m1a(str) ? 301 : 0;
        }
        return 299;
    }

    /* renamed from: a */
    public int mo102a(String str, String str2) {
        if (StringUtils.m1a(str)) {
            return 301;
        }
        if (this.f315k.equals(str) && this.f316l.equals(str2)) {
            return 0;
        }
        if (m1055B()) {
            return 300;
        }
        this.f315k = str;
        this.f340J = Utils.m538m(str);
        this.f316l = str2;
        return 0;
    }

    /* renamed from: a */
    public int mo123a(ContactGroup abstractC0046q) {
        if (abstractC0046q == this.f334D || abstractC0046q == this.f335E) {
            return 304;
        }
        return abstractC0046q.f397e.size() > 0 ? 303 : 0;
    }

    /* renamed from: b */
    public int mo118b(Contact abstractC0041l) {
        return (abstractC0041l.mo143m() || m1056C()) ? 0 : 299;
    }

    /* renamed from: d */
    public final int m1075d(Object obj) {
        return C0034e.m969a((String) obj, this);
    }

    /* renamed from: c */
    public abstract int mo104c(Contact abstractC0041l);

    /* renamed from: d */
    public abstract int mo105d(Contact abstractC0041l);

    /* renamed from: e */
    public abstract int mo106e(Contact abstractC0041l);

    /* renamed from: f */
    public int mo735f(Contact abstractC0041l) {
        return !m1056C() ? 299 : 0;
    }

    /* renamed from: M */
    public final Vector m1076M() {
        Vector vectorM1213g = C0040k.m1213g();
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (abstractC0041l.mo144l()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: N */
    public final Vector m1077N() {
        Vector vectorM1213g = C0040k.m1213g();
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (abstractC0041l.mo990d()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: O */
    public Vector mo720O() {
        return C0040k.m1213g();
    }

    /* renamed from: P */
    public final Vector m1078P() {
        Vector vectorM1213g = C0040k.m1213g();
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            vectorM1213g.addElement(enumerationElements.nextElement());
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public int mo113a(Contact abstractC0041l, ContactGroup abstractC0046q, ContactGroup abstractC0046q2) {
        if (abstractC0046q == abstractC0046q2) {
            return 305;
        }
        return !m1056C() ? 299 : 0;
    }

    /* renamed from: Q */
    public final Vector m1079Q() {
        Vector vectorM1213g = C0040k.m1213g();
        Enumeration enumerationElements = this.f321q.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (abstractC0041l.mo143m()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: g */
    public final ContactGroup m1080g(Contact abstractC0041l) {
        ContactGroup abstractC0046qM1082g;
        if (abstractC0041l.mo143m() || this.f334D.m1400b(abstractC0041l)) {
            return this.f334D;
        }
        if (abstractC0041l.mo996n()) {
            return this.f338H;
        }
        if (abstractC0041l.mo144l()) {
            return this.f335E;
        }
        if (abstractC0041l.mo990d()) {
            return this.f336F;
        }
        int size = this.f313i.size();
        do {
            size--;
            if (size < 0) {
                return null;
            }
            abstractC0046qM1082g = m1082g(size);
        } while (!abstractC0046qM1082g.m1400b(abstractC0041l));
        return abstractC0046qM1082g;
    }

    /* renamed from: h */
    public final void m1081h(Contact abstractC0041l) {
        Contact abstractC0041l2 = (Contact) this.f321q.get(abstractC0041l.mo135a());
        if (abstractC0041l2 != null && abstractC0041l2 != abstractC0041l) {
            this.f334D.m1393a(abstractC0041l2);
            this.f335E.m1393a(abstractC0041l2);
            this.f336F.m1393a(abstractC0041l2);
            this.f337G.m1393a(abstractC0041l2);
            this.f338H.m1393a(abstractC0041l2);
        }
        this.f321q.put(abstractC0041l.mo135a(), abstractC0041l);
    }

    /* renamed from: g */
    public final ContactGroup m1082g(int i) {
        return (ContactGroup) this.f313i.elementAt(i);
    }

    /* renamed from: b */
    public final void m1083b(ContactGroup abstractC0046q) {
        this.f313i.addElement(abstractC0046q);
    }

    /* renamed from: c */
    public final void m1084c(ContactGroup abstractC0046q) {
        this.f313i.removeElement(abstractC0046q);
    }

    /* renamed from: c */
    public abstract void mo100c(int i);

    public final String toString() {
        return this.f315k;
    }

    /* renamed from: n */
    public int mo922n() {
        return -1;
    }

    /* renamed from: R */
    public final void m1085R() {
        this.f316l = m1064I();
        this.f326v++;
    }

    /* renamed from: o */
    public void mo924o() {
        this.f327w = 0;
        this.f328x = 0;
    }

    /* renamed from: p */
    public int mo110p() {
        return 0;
    }
}
