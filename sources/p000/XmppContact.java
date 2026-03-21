package p000;

/* renamed from: af */
/* loaded from: MobileAgent_3.9.jar:af.class */
public final class XmppContact extends Contact {

    /* renamed from: a */
    public String f38a;

    /* renamed from: c */
    private int f39c;

    /* renamed from: d */
    private int f40d;

    /* renamed from: e */
    private String f41e;

    /* renamed from: f */
    private String f42f;

    /* renamed from: b */
    public boolean f43b;

    public XmppContact(XmppProtocol c0005ae, String str, String str2, String str3) {
        super(c0005ae);
        this.f381x = str;
        this.f38a = str;
        this.f41e = str3;
        this.f40d = 0;
        m1249c(Utils.m528a(str2, str));
        this.f373r = XmppProtocol.m131d(this.f39c);
        this.f380w = c0005ae.m1050q().writeRawString(str).getStringAndClear();
        c0005ae.m1081h(this);
        m1228A();
    }

    @Override // p000.Contact
    /* renamed from: c */
    public final void mo134c() {
        this.f39c = 0;
        this.f373r = 381;
        this.f41e = null;
        this.f42f = null;
        this.f40d = 0;
        super.mo134c();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final String mo135a() {
        return this.f38a;
    }

    public XmppContact(Account abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.f38a = c0043n.readWideStr();
        m1249c(c0043n.readUTF8Str((String) null));
        this.f380w = abstractC0037h.m1050q().writeRawString(this.f38a).getStringAndClear();
        this.f39c = 0;
        this.f373r = XmppProtocol.m131d(0);
        abstractC0037h.m1081h(this);
        m1228A();
        this.f381x = this.f38a;
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void mo136a(ByteBuffer c0043n) {
        c0043n.writeStringLatin1(this.f38a).writeStringUTF16(this.f376u);
    }

    /* renamed from: o */
    private final int m137o() {
        int iMo139e = mo139e();
        int i = iMo139e & 65535;
        return (!(this.f369o instanceof XmppMailRuProtocol) || i < 381 || i > 384) ? iMo139e : iMo139e + 4;
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem mo138b() {
        MenuItem c0032cM901a = MenuItem.m887a(this.f380w).m896a(m137o()).m901a(this.f376u, 0, this.f40d);
        c0032cM901a.f265d = this;
        return c0032cM901a;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int mo139e() {
        int iMo139e = super.mo139e();
        int i = iMo139e;
        if (iMo139e == 16384) {
            return i;
        }
        if (StringUtils.m3a(262852, this.f41e) || StringUtils.m3a(267931, this.f41e) || StringUtils.m3a(202403, this.f41e)) {
            i = 384;
        }
        if (StringUtils.m3a(131590, this.f41e)) {
            i = (i & 65535) | 20578304;
        }
        return i;
    }

    @Override // p000.Contact
    /* renamed from: i */
    public final boolean mo140i() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: j */
    public final boolean mo141j() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: k */
    public final boolean mo142k() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: m */
    public final boolean mo143m() {
        return this.f43b;
    }

    @Override // p000.Contact
    /* renamed from: l */
    public final boolean mo144l() {
        return StringUtils.m3a(267931, this.f41e) || StringUtils.m3a(262852, this.f41e);
    }

    @Override // p000.Contact
    /* renamed from: h */
    public final void mo145h() {
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0070  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void m146a(String str, XmlElement c0022av) {
        int i = 0;
        this.f42f = str;
        this.f39c = 0;
        if (StringUtils.m3a(594984, str)) {
            XmlElement c0022avM562f = c0022av.findChildByKey(267927);
            if (c0022avM562f != null) {
                String strM11a = StringUtils.m11a(c0022avM562f.textContent);
                if (StringUtils.m1a(strM11a)) {
                    i = 1;
                    this.f39c = i;
                } else {
                    if (StringUtils.m3a(265215, strM11a)) {
                        i = 4;
                    } else if (StringUtils.m3a(267829, strM11a)) {
                        i = 2;
                    } else if (StringUtils.m3a(136761, strM11a)) {
                        i = 6;
                    } else if (StringUtils.m3a(202299, strM11a)) {
                        i = 5;
                    } else if (StringUtils.m3a(202302, strM11a)) {
                        i = 3;
                    }
                    this.f39c = i;
                }
            }
        }
        m147a(this);
    }

    /* renamed from: a */
    public final void m147a(XmppContact c0006af) {
        this.f39c = c0006af != null ? c0006af.f39c : 0;
        this.f42f = c0006af != null ? c0006af.f42f : null;
        this.f371p = this.f39c != 0;
        this.f373r = XmppProtocol.m131d(this.f39c);
        this.f40d = this.f39c == 0 ? 0 : 3;
        this.f375t = true;
        m1228A();
    }

    @Override // p000.Contact
    /* renamed from: L */
    public final void mo148L() {
        AppState.m591f(1316);
    }

    /* renamed from: a */
    public final int m149a(int i) {
        int iM119a = ((XmppProtocol) this.f369o).m119a(this, i);
        if (iM119a != i) {
            return iM119a;
        }
        m150b(1);
        m150b(0);
        return i;
    }

    /* renamed from: b */
    public final void m150b(int i) {
        ((XmppProtocol) this.f369o).m99b(this.f38a, i);
    }

    /* JADX DEBUG: Possible override for method l.f()Ln; */
    /* renamed from: f */
    public final ContactInfo m151f() {
        return new ContactInfo(this).m1298g(m137o());
    }
}
