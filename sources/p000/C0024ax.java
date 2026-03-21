package p000;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/* renamed from: ax */
/* loaded from: MobileAgent_3.9.jar:ax.class */
public final class C0024ax {

    /* renamed from: a */
    public Connection f183a;

    /* renamed from: b */
    private InputStream f184b;

    /* renamed from: c */
    private OutputStream f185c;

    /* renamed from: d */
    private AbstractC0037h f186d;

    /* renamed from: e */
    private int f187e;

    /* renamed from: f */
    private int f188f;

    /* renamed from: g */
    private int f189g;

    /* renamed from: h */
    private int f190h;

    /* renamed from: i */
    private String f191i;

    /* renamed from: j */
    private C0043n f192j;

    /* renamed from: a */
    public static final C0024ax m629a(String str, AbstractC0037h abstractC0037h, int i) throws IOException {
        return new C0024ax(str, abstractC0037h, i);
    }

    /* renamed from: a */
    public static final C0024ax m630a(Object obj) throws IOException {
        return m629a((String) obj, (AbstractC0037h) null, 3);
    }

    /* renamed from: b */
    public static final C0024ax m631b(Object obj) throws IOException {
        return m629a((String) obj, (AbstractC0037h) null, 2);
    }

    /* renamed from: a */
    public static final C0024ax m632a(String str) {
        return new C0024ax(str);
    }

    /* renamed from: a */
    public static final void m633a(C0024ax c0024ax) {
        try {
            if (c0024ax.f186d != null && c0024ax.f187e == 0) {
                C0015ao.m419a(c0024ax.f186d, c0024ax.f190h);
                C0015ao.m420b(c0024ax.f186d, c0024ax.f189g);
            } else if (c0024ax.f187e == 1) {
                C0015ao.m422C(c0024ax.f190h);
                C0015ao.m423D(c0024ax.f189g);
            } else if (c0024ax.f187e == 2) {
                C0015ao.m424E(c0024ax.f190h);
                C0015ao.m425F(c0024ax.f189g);
            } else {
                C0015ao.m426G(c0024ax.f190h);
                C0015ao.m427H(c0024ax.f189g);
            }
            if (c0024ax.f188f == 0) {
                try {
                    if (c0024ax.f184b != null) {
                        c0024ax.f184b.close();
                    }
                } catch (Throwable unused) {
                }
                try {
                    if (c0024ax.f185c != null) {
                        c0024ax.f185c.close();
                    }
                } catch (Throwable unused2) {
                }
                try {
                    c0024ax.f183a.close();
                } catch (Throwable unused3) {
                }
            }
        } catch (Throwable unused4) {
        }
    }

    private C0024ax(String str) {
        this.f188f = 1;
        this.f187e = 2;
        this.f191i = str.startsWith(AbstractC0023aw.m584b(459255)) ? C0000a.m15c(str, 7) : str;
    }

    private C0024ax(String str, AbstractC0037h abstractC0037h, int i) throws IOException {
        this.f183a = Connector.open(str, 3);
        this.f186d = abstractC0037h;
        this.f187e = i;
        this.f191i = str;
    }

    /* renamed from: a */
    public final int m634a() throws IOException {
        if (this.f188f != 0) {
            m640d().flush();
            return Integer.parseInt(new String(m645e().f383a, 9, 3));
        }
        int responseCode = ((HttpConnection) this.f183a).getResponseCode();
        this.f189g += this.f191i.length() + 127;
        this.f190h += 255;
        return responseCode;
    }

    /* renamed from: b */
    public final void m635b(String str) throws IOException {
        ((HttpConnection) this.f183a).setRequestMethod(str);
    }

    /* renamed from: a */
    public final C0024ax m636a(String str, String str2) throws IOException {
        if (this.f188f == 0) {
            ((HttpConnection) this.f183a).setRequestProperty(str, str2);
        } else {
            m641a(new C0043n().m1314d(str).m1385u(8250).m1314d(str2).m1385u(2573));
        }
        this.f189g += str.length() + str2.length() + 4;
        return this;
    }

    /* renamed from: a */
    public final C0024ax m637a(byte[] bArr, int i) throws IOException {
        if (i > 0) {
            m640d().write(bArr, 0, i);
            this.f189g += i;
        }
        return this;
    }

    /* renamed from: a */
    public final int m638a(byte[] bArr) throws IOException {
        int i = m639c().read(bArr);
        if (i > 0) {
            this.f190h += i;
        }
        return i;
    }

    /* renamed from: c */
    private final InputStream m639c() throws IOException {
        if (this.f184b != null) {
            return this.f184b;
        }
        InputStream inputStreamOpenInputStream = ((HttpConnection) this.f183a).openInputStream();
        this.f184b = inputStreamOpenInputStream;
        return inputStreamOpenInputStream;
    }

    /* renamed from: d */
    private final OutputStream m640d() throws IOException {
        if (this.f185c != null) {
            return this.f185c;
        }
        OutputStream outputStreamOpenOutputStream = ((HttpConnection) this.f183a).openOutputStream();
        this.f185c = outputStreamOpenOutputStream;
        return outputStreamOpenOutputStream;
    }

    /* renamed from: a */
    public final C0024ax m641a(C0043n c0043n) throws IOException {
        m637a(c0043n.f383a, c0043n.f384b);
        return this;
    }

    /* renamed from: a */
    public final C0024ax m642a(int i, int i2, int i3) throws IOException {
        String str = this.f191i;
        this.f183a = Connector.open(new C0043n().m1310c(593549).m1314d(C0000a.m13b(str, str.indexOf(47))).m1317c(), 3);
        m641a(new C0043n().m1385u(i2).m1321f(32).m1314d(C0000a.m15c(str, str.indexOf(47))).m1310c(2951238).m1314d(C0000a.m13b(str, str.indexOf(58))).m1311d(i3).m1385u(2573));
        if (i2 == 1414745936) {
            m643a(788628, 2164851);
            m643a(919726, 788668);
        }
        return m636a(AbstractC0023aw.m584b(919712), C0000a.m17c(Integer.toString(i))).m643a(657608, 329938).m641a(new C0043n().m1385u(2573));
    }

    /* renamed from: a */
    private final C0024ax m643a(int i, int i2) throws IOException {
        return m636a(AbstractC0023aw.m584b(i), AbstractC0023aw.m584b(i2));
    }

    /* renamed from: b */
    public final C0043n m644b() throws IOException, NumberFormatException {
        int i;
        C0043n c0043nM645e = m645e();
        String str = new String(c0043nM645e.f383a, 0, c0043nM645e.f384b);
        int iIndexOf = C0000a.m17c(str.toLowerCase()).indexOf(AbstractC0023aw.m584b(1052310)) + 16;
        int i2 = Integer.parseInt(C0000a.m12a(str, iIndexOf, str.indexOf(13, iIndexOf)));
        C0043n c0043n = new C0043n();
        byte[] bArrM1211a = C0040k.m1211a(i2);
        do {
            i = m639c().read(bArrM1211a, 0, i2 - c0043n.f384b);
            if (i > 0) {
                this.f190h += i;
            }
            if (i > 0) {
                c0043n.m1303a(bArrM1211a, 0, i);
            }
            if (c0043n.f384b == i2) {
                break;
            }
        } while (i != -1);
        C0040k.m1209a(bArrM1211a);
        return c0043n;
    }

    /* renamed from: e */
    private final C0043n m645e() throws IOException {
        if (this.f192j != null) {
            return this.f192j.m1299a();
        }
        this.f192j = new C0043n();
        while (true) {
            int i = 0;
            while (true) {
                int i2 = m639c().read();
                if (i2 == -1) {
                    throw new EOFException(this.f192j.m1317c());
                }
                this.f192j.m1321f(i2);
                this.f190h++;
                if (i2 == 10) {
                    i++;
                    if (i == 34) {
                        return this.f192j.m1299a();
                    }
                } else if (i2 == 13) {
                    i += 16;
                }
            }
        }
    }
}
