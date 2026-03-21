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
public final class HttpClient {

    /* renamed from: a */
    public Connection f183a;

    /* renamed from: b */
    private InputStream f184b;

    /* renamed from: c */
    private OutputStream f185c;

    /* renamed from: d */
    private Account f186d;

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
    private ByteBuffer f192j;

    /* renamed from: a */
    public static final HttpClient m629a(String str, Account abstractC0037h, int i) throws IOException {
        return new HttpClient(str, abstractC0037h, i);
    }

    /* renamed from: a */
    public static final HttpClient m630a(Object obj) throws IOException {
        return m629a((String) obj, (Account) null, 3);
    }

    /* renamed from: b */
    public static final HttpClient m631b(Object obj) throws IOException {
        return m629a((String) obj, (Account) null, 2);
    }

    /* renamed from: a */
    public static final HttpClient m632a(String str) {
        return new HttpClient(str);
    }

    /* renamed from: a */
    public static final void m633a(HttpClient c0024ax) {
        try {
            if (c0024ax.f186d != null && c0024ax.f187e == 0) {
                AppController.m419a(c0024ax.f186d, c0024ax.f190h);
                AppController.m420b(c0024ax.f186d, c0024ax.f189g);
            } else if (c0024ax.f187e == 1) {
                AppController.m422C(c0024ax.f190h);
                AppController.m423D(c0024ax.f189g);
            } else if (c0024ax.f187e == 2) {
                AppController.m424E(c0024ax.f190h);
                AppController.m425F(c0024ax.f189g);
            } else {
                AppController.m426G(c0024ax.f190h);
                AppController.m427H(c0024ax.f189g);
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

    private HttpClient(String str) {
        this.f188f = 1;
        this.f187e = 2;
        this.f191i = str.startsWith(AppState.m584b(459255)) ? StringUtils.m15c(str, 7) : str;
    }

    private HttpClient(String str, Account abstractC0037h, int i) throws IOException {
        this.f183a = Connector.open(str, 3);
        this.f186d = abstractC0037h;
        this.f187e = i;
        this.f191i = str;
    }

    /* renamed from: a */
    public final int m634a() throws IOException {
        if (this.f188f != 0) {
            m640d().flush();
            return Integer.parseInt(new String(m645e().data, 9, 3));
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
    public final HttpClient m636a(String str, String str2) throws IOException {
        if (this.f188f == 0) {
            ((HttpConnection) this.f183a).setRequestProperty(str, str2);
        } else {
            m641a(new ByteBuffer().writeRawString(str).writeUInt(8250).writeRawString(str2).writeUInt(2573));
        }
        this.f189g += str.length() + str2.length() + 4;
        return this;
    }

    /* renamed from: a */
    public final HttpClient m637a(byte[] bArr, int i) throws IOException {
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
    public final HttpClient m641a(ByteBuffer c0043n) throws IOException {
        m637a(c0043n.data, c0043n.length);
        return this;
    }

    /* renamed from: a */
    public final HttpClient m642a(int i, int i2, int i3) throws IOException {
        String str = this.f191i;
        this.f183a = Connector.open(new ByteBuffer().writeCompressed(593549).writeRawString(StringUtils.m13b(str, str.indexOf(47))).getStringAndClear(), 3);
        m641a(new ByteBuffer().writeUInt(i2).writeByte(32).writeRawString(StringUtils.m15c(str, str.indexOf(47))).writeCompressed(2951238).writeRawString(StringUtils.m13b(str, str.indexOf(58))).writeEncodedInt(i3).writeUInt(2573));
        if (i2 == 1414745936) {
            m643a(788628, 2164851);
            m643a(919726, 788668);
        }
        return m636a(AppState.m584b(919712), StringUtils.m17c(Integer.toString(i))).m643a(657608, 329938).m641a(new ByteBuffer().writeUInt(2573));
    }

    /* renamed from: a */
    private final HttpClient m643a(int i, int i2) throws IOException {
        return m636a(AppState.m584b(i), AppState.m584b(i2));
    }

    /* renamed from: b */
    public final ByteBuffer m644b() throws IOException, NumberFormatException {
        int i;
        ByteBuffer c0043nM645e = m645e();
        String str = new String(c0043nM645e.data, 0, c0043nM645e.length);
        int iIndexOf = StringUtils.m17c(str.toLowerCase()).indexOf(AppState.m584b(1052310)) + 16;
        int i2 = Integer.parseInt(StringUtils.m12a(str, iIndexOf, str.indexOf(13, iIndexOf)));
        ByteBuffer c0043n = new ByteBuffer();
        byte[] bArrM1211a = NetworkUtils.m1211a(i2);
        do {
            i = m639c().read(bArrM1211a, 0, i2 - c0043n.length);
            if (i > 0) {
                this.f190h += i;
            }
            if (i > 0) {
                c0043n.writeBytesAt(bArrM1211a, 0, i);
            }
            if (c0043n.length == i2) {
                break;
            }
        } while (i != -1);
        NetworkUtils.m1209a(bArrM1211a);
        return c0043n;
    }

    /* renamed from: e */
    private final ByteBuffer m645e() throws IOException {
        if (this.f192j != null) {
            return this.f192j.compact();
        }
        this.f192j = new ByteBuffer();
        while (true) {
            int i = 0;
            while (true) {
                int i2 = m639c().read();
                if (i2 == -1) {
                    throw new EOFException(this.f192j.getStringAndClear());
                }
                this.f192j.writeByte(i2);
                this.f190h++;
                if (i2 == 10) {
                    i++;
                    if (i == 34) {
                        return this.f192j.compact();
                    }
                } else if (i2 == 13) {
                    i += 16;
                }
            }
        }
    }
}
