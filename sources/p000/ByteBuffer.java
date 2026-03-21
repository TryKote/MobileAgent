package p000;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: n */
/* loaded from: MobileAgent_3.9.jar:n.class */
public final class ByteBuffer {

    /* renamed from: a */
    public byte[] f383a;

    /* renamed from: b */
    public int f384b;

    /* renamed from: c */
    public int f385c;

    public ByteBuffer() {
        this.f383a = AppState.f176a;
    }

    public ByteBuffer(C0024ax c0024ax) {
        this.f383a = C0040k.m1211a(2048);
        try {
            byte[] bArrM1211a = C0040k.m1211a(2048);
            while (true) {
                int iM638a = c0024ax.m638a(bArrM1211a);
                if (iM638a < 0) {
                    C0040k.m1209a(bArrM1211a);
                    return;
                }
                m1303a(bArrM1211a, 0, iM638a);
            }
        } catch (Throwable unused) {
        }
    }

    public ByteBuffer(String str) {
        this(str, 2048);
    }

    public ByteBuffer(String str, int i) {
        this((InputStream) C0029bb.m761a((Object) str.getClass().getResourceAsStream(str)), i);
    }

    private ByteBuffer(InputStream inputStream, int i) {
        this.f383a = C0040k.m1211a(i);
        try {
            byte[] bArrM1211a = C0040k.m1211a(8192);
            while (true) {
                int i2 = inputStream.read(bArrM1211a);
                if (i2 < 0) {
                    break;
                } else {
                    m1303a(bArrM1211a, 0, i2);
                }
            }
            C0040k.m1209a(bArrM1211a);
        } catch (Throwable unused) {
        }
        C0029bb.m763a(inputStream);
    }

    /* renamed from: a */
    public final ByteBuffer m1299a() {
        int i = this.f384b;
        if (i == 0) {
            m1301b();
        } else {
            byte[] bArr = this.f383a;
            if (i < bArr.length) {
                m1300a(0);
                byte[] bArrM1210a = C0040k.m1210a(bArr, i);
                if (bArrM1210a != null) {
                    this.f383a = bArrM1210a;
                }
            }
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer m1300a(int i) {
        byte[] bArr = this.f383a;
        int length = bArr.length;
        int i2 = this.f384b;
        int i3 = i2 + i;
        boolean z = length < i3;
        boolean z2 = z;
        byte[] bArrM1211a = z ? C0040k.m1211a(i3 + 32) : bArr;
        if (z2 || this.f385c != 0) {
            Utils.m490a((Object) bArr, this.f385c, (Object) bArrM1211a, 0, i2);
            if (z2) {
                C0040k.m1209a(bArr);
                this.f383a = bArrM1211a;
            }
        }
        this.f385c = 0;
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer m1301b() {
        C0040k.m1209a(this.f383a);
        this.f383a = AppState.f176a;
        this.f384b = 0;
        this.f385c = 0;
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer m1302a(byte[] bArr) {
        if (bArr != null) {
            m1303a(bArr, 0, bArr.length);
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer m1303a(byte[] bArr, int i, int i2) {
        if (i2 > 0) {
            m1300a(i2);
            Utils.m490a((Object) bArr, i, (Object) this.f383a, this.f384b, i2);
            this.f384b += i2;
        }
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer m1304b(byte[] bArr) {
        m1301b();
        this.f383a = bArr;
        this.f384b = bArr.length;
        return this;
    }

    /* renamed from: b */
    public final byte[] m1305b(byte[] bArr, int i, int i2) {
        Utils.m490a((Object) this.f383a, this.f385c, (Object) bArr, i, i2);
        this.f384b -= i2;
        this.f385c += i2;
        m1299a();
        return bArr;
    }

    /* renamed from: b */
    public final ByteBuffer m1306b(int i) {
        m1300a(i);
        while (true) {
            int i2 = i;
            i = i2 - 1;
            if (i2 <= 0) {
                return this;
            }
            byte[] bArr = this.f383a;
            int i3 = this.f384b;
            this.f384b = i3 + 1;
            bArr[i3] = 0;
        }
    }

    /* renamed from: a */
    public final ByteBuffer m1307a(String str, int i) {
        return i != 0 ? m1309b(str) : m1308a(str);
    }

    /* renamed from: a */
    public final ByteBuffer m1308a(String str) {
        int length;
        if (str == null || (length = str.length()) <= 0) {
            return m1360p(0);
        }
        m1300a(length + 4);
        m1360p(length);
        for (int i = 0; i < length; i++) {
            byte[] bArr = this.f383a;
            int i2 = this.f384b;
            this.f384b = i2 + 1;
            bArr[i2] = (byte) str.charAt(i);
        }
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer m1309b(String str) {
        int length;
        if (str == null || (length = str.length()) <= 0) {
            return m1360p(0);
        }
        int i = length << 1;
        m1300a(i + 4);
        m1360p(i);
        for (int i2 = 0; i2 < length; i2++) {
            byte[] bArr = this.f383a;
            int i3 = this.f384b;
            this.f384b = i3 + 1;
            char cCharAt = str.charAt(i2);
            bArr[i3] = (byte) cCharAt;
            byte[] bArr2 = this.f383a;
            int i4 = this.f384b;
            this.f384b = i4 + 1;
            bArr2[i4] = (byte) (cCharAt >> '\b');
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer m1310c(int i) {
        return i > 5179 ? m1303a(AppState.m581a(295), i & 65535, i >> 16) : m1302a(AppState.m581a(i));
    }

    /* renamed from: d */
    public final ByteBuffer m1311d(int i) {
        return m1314d(AppState.m584b(i));
    }

    /* renamed from: e */
    public final ByteBuffer m1312e(int i) {
        m1314d(AppState.m584b(i & 65535));
        int i2 = i >>> 16;
        if (i2 != 0) {
            m1321f(i2);
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer m1313c(String str) {
        return m1314d(C0038i.m1120b((Object) str));
    }

    /* renamed from: d */
    public final ByteBuffer m1314d(String str) {
        int length;
        if (str != null && (length = str.length()) > 0) {
            m1300a(length);
            byte[] bArr = this.f383a;
            for (int i = 0; i < length; i++) {
                int i2 = this.f384b;
                this.f384b = i2 + 1;
                bArr[i2] = (byte) str.charAt(i);
            }
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer m1315a(Object obj) {
        return m1314d(C0038i.m1124l((String) obj));
    }

    /* renamed from: b */
    public final ByteBuffer m1316b(Object obj) {
        return m1314d((String) obj);
    }

    /* renamed from: c */
    public final String m1317c() {
        String strM17c = this.f384b == 0 ? AppState.f181d : StringUtils.m17c(new String(this.f383a, this.f385c, this.f384b));
        m1301b();
        return strM17c;
    }

    /* renamed from: a */
    public final ByteBuffer m1318a(String[] strArr) {
        ByteBuffer c0043n = new ByteBuffer();
        c0043n.m1360p(2);
        for (int i = 0; i < 2; i++) {
            c0043n.m1309b(strArr[i]);
        }
        return m1308a(c0043n.m1320d());
    }

    /* renamed from: w */
    private static char m1319w(int i) {
        return (char) AppState.m581a(961)[i & 63];
    }

    /* renamed from: d */
    public final String m1320d() {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        m1300a(0);
        int i = 0;
        int i2 = this.f384b;
        boolean z = true;
        while (z) {
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            if (i < i2) {
                int i7 = i;
                i++;
                i3 = this.f383a[i7] & 255;
                i6 = 0 + 1;
            }
            if (i < i2) {
                int i8 = i;
                i++;
                i4 = this.f383a[i8] & 255;
                i6++;
            }
            if (i < i2) {
                int i9 = i;
                i++;
                i5 = this.f383a[i9] & 255;
                i6++;
            } else {
                z = false;
            }
            if (i6 > 0) {
                int i10 = (i3 << 16) | (i4 << 8) | i5;
                stringBufferM1217h.append(m1319w(i10 >> 18)).append(m1319w(i10 >> 12)).append(i6 > 1 ? m1319w(i10 >> 6) : '=').append(i6 > 2 ? m1319w(i10) : '=');
            }
        }
        m1301b();
        return C0040k.m1215a(stringBufferM1217h);
    }

    /* renamed from: f */
    public final ByteBuffer m1321f(int i) {
        m1300a(1);
        byte[] bArr = this.f383a;
        int i2 = this.f384b;
        this.f384b = i2 + 1;
        bArr[i2] = (byte) i;
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer m1322a(boolean z) {
        return m1321f(z ? 1 : 0);
    }

    /* renamed from: a */
    public final ByteBuffer m1323a(long j) {
        return m1360p((int) j).m1360p((int) (j >> 32));
    }

    /* renamed from: a */
    private ByteBuffer m1324a(ByteBuffer c0043n, int i) {
        byte[] bArrM1211a = C0040k.m1211a(i);
        c0043n.m1305b(bArrM1211a, 0, i);
        m1303a(bArrM1211a, 0, i);
        C0040k.m1209a(bArrM1211a);
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer m1325a(ByteBuffer c0043n) {
        if (c0043n != null) {
            m1324a(c0043n, c0043n.f384b);
        }
        return this;
    }

    /* renamed from: b */
    public final ByteBuffer m1326b(ByteBuffer c0043n) {
        if (c0043n != null) {
            m1357m(c0043n.f384b).m1324a(c0043n, c0043n.f384b);
        }
        return this;
    }

    /* renamed from: c */
    public final ByteBuffer m1327c(ByteBuffer c0043n) {
        return c0043n != null ? m1360p(c0043n.f384b).m1324a(c0043n, c0043n.f384b) : m1360p(0);
    }

    /* renamed from: e */
    public final int m1328e() {
        int iM1330h = m1330h(0);
        this.f385c += 4;
        this.f384b -= 4;
        return iM1330h;
    }

    /* renamed from: g */
    public final ByteBuffer m1329g(int i) {
        this.f385c += i;
        int i2 = this.f384b - i;
        this.f384b = i2;
        if (i2 == 0) {
            m1301b();
        }
        return this;
    }

    /* renamed from: h */
    public final int m1330h(int i) {
        int i2 = this.f385c + i;
        byte[] bArr = this.f383a;
        int i3 = i2 + 1;
        int i4 = (bArr[i2] & 255) | ((bArr[i3] & 255) << 8);
        int i5 = i3 + 1;
        return i4 | ((bArr[i5] & 255) << 16) | (bArr[i5 + 1] << 24);
    }

    /* renamed from: i */
    public final int m1331i(int i) {
        return this.f383a[this.f385c + i] & 255;
    }

    /* renamed from: j */
    public final String m1332j(int i) {
        return i != 0 ? m1333f() : m1335e((String) null);
    }

    /* renamed from: f */
    public final String m1333f() {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int iM1328e = m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return C0040k.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append(Utils.m499a((int) m1344o()));
        }
    }

    /* renamed from: g */
    public final String m1334g() {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int iM1328e = m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return C0040k.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append((char) m1346q());
        }
    }

    /* renamed from: e */
    public final String m1335e(String str) {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int iM1328e = m1328e();
        while (true) {
            iM1328e -= 2;
            if (iM1328e < 0) {
                break;
            }
            stringBufferM1217h.append((char) (m1346q() | (m1344o() << 8)));
        }
        if (iM1328e == -1) {
            m1344o();
            if (str != null) {
                return str;
            }
        }
        return C0040k.m1215a(stringBufferM1217h);
    }

    /* renamed from: h */
    public final String m1336h() {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        while (this.f384b > 0) {
            stringBufferM1217h.append((char) (m1346q() | (m1344o() << 8)));
        }
        m1301b();
        return C0040k.m1215a(stringBufferM1217h);
    }

    /* renamed from: i */
    public final String m1337i() {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        while (this.f384b > 0) {
            stringBufferM1217h.append(Utils.m499a((int) m1344o()));
        }
        m1301b();
        return C0040k.m1215a(stringBufferM1217h);
    }

    /* renamed from: j */
    public final String m1338j() {
        return StringUtils.m17c(m1334g().toLowerCase());
    }

    /* renamed from: k */
    public final byte[] m1339k() {
        byte[] bArr = this.f383a;
        int i = this.f385c;
        byte[] bArr2 = new byte[this.f384b];
        Utils.m490a((Object) bArr, i, (Object) bArr2, 0, this.f384b);
        m1301b();
        return bArr2;
    }

    /* renamed from: l */
    public final boolean m1340l() {
        return m1344o() != 0;
    }

    /* renamed from: m */
    public final long m1341m() {
        return (m1328e() & 4294967295L) | (m1328e() << 32);
    }

    /* renamed from: n */
    public final int m1342n() {
        try {
            return m1346q();
        } catch (Throwable unused) {
            return -1;
        }
    }

    /* renamed from: k */
    public final int m1343k(int i) {
        return this.f383a[this.f385c + i] & 255;
    }

    /* renamed from: o */
    public final byte m1344o() {
        if (this.f384b <= 0) {
            throw new RuntimeException();
        }
        this.f384b--;
        byte[] bArr = this.f383a;
        int i = this.f385c;
        this.f385c = i + 1;
        return bArr[i];
    }

    /* renamed from: p */
    public final Vector m1345p() {
        Vector vectorM1213g = C0040k.m1213g();
        m1328e();
        int iM1328e = m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return vectorM1213g;
            }
            vectorM1213g.addElement(new ByteBuffer().m1324a(this, m1330h(0) + 4));
        }
    }

    /* renamed from: q */
    public final int m1346q() {
        return m1344o() & 255;
    }

    /* renamed from: c */
    public final int m1347c(byte[] bArr) {
        if (this.f384b == 0) {
            return -1;
        }
        int iM503b = Utils.m503b(bArr.length, this.f384b);
        m1305b(bArr, 0, iM503b);
        return iM503b;
    }

    /* renamed from: r */
    public final Image m1348r() {
        try {
            return Image.createImage(this.f383a, this.f385c, this.f384b);
        } finally {
            m1301b();
        }
    }

    /* renamed from: s */
    public final ByteBuffer m1349s() {
        int i = this.f384b;
        if (i >= 4 && m1330h(0) != -559038737) {
            throw new RuntimeException();
        }
        if (i < 44 || i < 44 + m1330h(16)) {
            return null;
        }
        return new ByteBuffer().m1324a(this, 44 + m1330h(16)).m1299a();
    }

    /* renamed from: t */
    public final ByteBuffer m1350t() {
        int i = this.f384b;
        if (i < 6) {
            return null;
        }
        if (m1331i(0) != 42) {
            throw new RuntimeException();
        }
        int iM1331i = m1331i(1);
        if (iM1331i < 1 || iM1331i > 5) {
            throw new RuntimeException();
        }
        int iM1351l = m1351l(4);
        if (iM1351l + 6 > i) {
            return null;
        }
        return new ByteBuffer().m1324a(this, iM1351l + 6).m1299a();
    }

    /* renamed from: l */
    public final int m1351l(int i) {
        return ((this.f383a[this.f385c + i] & 255) << 8) | (this.f383a[this.f385c + i + 1] & 255);
    }

    /* renamed from: K */
    private int m1352K() {
        return ((this.f383a[this.f385c + 1] & 255) << 8) | (this.f383a[this.f385c] & 255);
    }

    /* renamed from: u */
    public final int m1353u() {
        this.f384b -= 2;
        byte[] bArr = this.f383a;
        int i = this.f385c;
        this.f385c = i + 1;
        int i2 = (bArr[i] & 255) << 8;
        byte[] bArr2 = this.f383a;
        int i3 = this.f385c;
        this.f385c = i3 + 1;
        return i2 | (bArr2[i3] & 255);
    }

    /* renamed from: v */
    public final int m1354v() {
        this.f384b -= 2;
        byte[] bArr = this.f383a;
        int i = this.f385c;
        this.f385c = i + 1;
        int i2 = bArr[i] & 255;
        byte[] bArr2 = this.f383a;
        int i3 = this.f385c;
        this.f385c = i3 + 1;
        return i2 | ((bArr2[i3] & 255) << 8);
    }

    /* renamed from: w */
    public final int m1355w() {
        int i = ((((((this.f383a[this.f385c] & 255) << 8) | (this.f383a[this.f385c + 1] & 255)) << 8) | (this.f383a[this.f385c + 2] & 255)) << 8) | (this.f383a[this.f385c + 3] & 255);
        this.f385c += 4;
        this.f384b -= 4;
        return i;
    }

    /* renamed from: x */
    public final int m1356x() {
        return ((((((this.f383a[this.f385c + 12] & 255) << 8) | (this.f383a[this.f385c + 13] & 255)) << 8) | (this.f383a[this.f385c + 14] & 255)) << 8) | (this.f383a[this.f385c + 15] & 255);
    }

    /* renamed from: m */
    public final ByteBuffer m1357m(int i) {
        m1300a(2);
        byte[] bArr = this.f383a;
        int i2 = this.f384b;
        this.f384b = i2 + 1;
        bArr[i2] = (byte) (i >> 8);
        byte[] bArr2 = this.f383a;
        int i3 = this.f384b;
        this.f384b = i3 + 1;
        bArr2[i3] = (byte) i;
        return this;
    }

    /* renamed from: n */
    public final ByteBuffer m1358n(int i) {
        m1300a(2);
        byte[] bArr = this.f383a;
        int i2 = this.f384b;
        this.f384b = i2 + 1;
        bArr[i2] = (byte) i;
        byte[] bArr2 = this.f383a;
        int i3 = this.f384b;
        this.f384b = i3 + 1;
        bArr2[i3] = (byte) (i >> 8);
        return this;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v3, resolved type: byte[] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v4, types: [int] */
    /* renamed from: o */
    public final ByteBuffer m1359o(int i) {
        m1300a(4);
        byte[] bArr = this.f383a;
        int i2 = this.f384b;
        this.f384b = i2 + 1;
        bArr[i2] = (byte) (i >> 24);
        byte[] bArr2 = this.f383a;
        int i3 = this.f384b;
        this.f384b = i3 + 1;
        bArr2[i3] = (byte) (i >> 16);
        byte[] bArr3 = this.f383a;
        int i4 = this.f384b;
        this.f384b = i4 + 1;
        bArr3[i4] = (byte) (i >> 8);
        byte[] bArr4 = this.f383a;
        int i5 = this.f384b;
        this.f384b = i5 + 1;
        bArr4[i5] = (byte) i;
        return this;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v9, resolved type: byte[] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v21, types: [int] */
    /* renamed from: p */
    public final ByteBuffer m1360p(int i) {
        m1300a(4);
        byte[] bArr = this.f383a;
        int i2 = this.f384b;
        this.f384b = i2 + 1;
        bArr[i2] = (byte) i;
        byte[] bArr2 = this.f383a;
        int i3 = this.f384b;
        this.f384b = i3 + 1;
        bArr2[i3] = (byte) (i >> 8);
        byte[] bArr3 = this.f383a;
        int i4 = this.f384b;
        this.f384b = i4 + 1;
        bArr3[i4] = (byte) (i >> 16);
        byte[] bArr4 = this.f383a;
        int i5 = this.f384b;
        this.f384b = i5 + 1;
        bArr4[i5] = (byte) (i >> 24);
        return this;
    }

    /* renamed from: f */
    public final ByteBuffer m1361f(String str) {
        int length = str.length();
        m1300a(2 + length);
        m1357m(length);
        for (int i = 0; i < length; i++) {
            byte[] bArr = this.f383a;
            int i2 = this.f384b;
            this.f384b = i2 + 1;
            bArr[i2] = (byte) str.charAt(i);
        }
        return this;
    }

    /* renamed from: y */
    public final ByteBuffer m1362y() {
        m1300a(0);
        byte[] bArr = this.f383a;
        int i = this.f384b - 6;
        bArr[4] = (byte) (i >> 8);
        this.f383a[5] = (byte) i;
        return this;
    }

    /* renamed from: z */
    public final String m1363z() {
        int i = this.f383a[this.f385c] & 255;
        byte[] bArr = this.f383a;
        int i2 = this.f385c + 1;
        this.f385c = i2;
        String strM17c = StringUtils.m17c(new String(bArr, i2, i));
        this.f385c += i;
        this.f384b -= i + 1;
        return strM17c;
    }

    /* renamed from: A */
    public final String m1364A() {
        String strM0a = StringUtils.m0a(this.f383a, this.f385c);
        m1329g(2 + m1351l(0));
        return strM0a;
    }

    /* renamed from: B */
    public final ByteBuffer m1365B() {
        m1300a(0);
        byte[] bArrM1090a = C0038i.m1090a(this.f383a, this.f384b);
        m1301b();
        this.f383a = bArrM1090a;
        this.f384b = 16;
        return this;
    }

    /* renamed from: C */
    public final String m1366C() {
        int iM1352K = m1352K() - 1;
        if (iM1352K <= 0) {
            m1329g(3);
            return AppState.f181d;
        }
        this.f383a[this.f385c] = (byte) (iM1352K >> 8);
        this.f383a[this.f385c + 1] = (byte) iM1352K;
        String strM1378L = m1378L();
        m1344o();
        return strM1378L;
    }

    /* renamed from: D */
    public final String m1367D() {
        byte[] bArr = this.f383a;
        int i = this.f385c;
        int iM1352K = m1352K();
        bArr[i] = (byte) (iM1352K >> 8);
        this.f383a[this.f385c + 1] = (byte) iM1352K;
        return m1379a(this.f385c + 2, iM1352K) ? m1364A() : m1378L();
    }

    /* renamed from: E */
    public final String m1368E() {
        byte[] bArr = this.f383a;
        int i = this.f385c;
        int iM1352K = m1352K();
        bArr[i] = (byte) (iM1352K >> 8);
        this.f383a[this.f385c + 1] = (byte) iM1352K;
        String strM1364A = m1379a(this.f385c + 2, iM1352K) ? m1364A() : m1378L();
        String str = strM1364A;
        int length = strM1364A.length();
        return length > 0 ? StringUtils.m13b(str, length - 1) : str;
    }

    /* renamed from: q */
    public final String m1369q(int i) {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        while (true) {
            i -= 2;
            if (i < 0) {
                return C0040k.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append((char) m1353u());
        }
    }

    /* renamed from: r */
    public final String m1370r(int i) {
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        while (true) {
            i--;
            if (i < 0) {
                return C0040k.m1215a(stringBufferM1217h);
            }
            stringBufferM1217h.append(Utils.m499a(m1346q()));
        }
    }

    /* renamed from: g */
    public final ByteBuffer m1371g(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            m1321f(Utils.m500b(str.charAt(i)));
        }
        return this;
    }

    /* renamed from: a */
    public final ByteBuffer m1372a(int i, String str) {
        int length = str.length();
        return length > 0 ? m1357m(i).m1358n(length + 3).m1358n(length + 1).m1371g(str).m1321f(0) : this;
    }

    /* renamed from: h */
    public final ByteBuffer m1373h(String str) {
        int length = str.length();
        m1321f(length);
        for (int i = 0; i < length; i++) {
            m1321f(str.charAt(i));
        }
        return this;
    }

    /* renamed from: i */
    public final ByteBuffer m1374i(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            m1357m(str.charAt(i));
        }
        return this;
    }

    /* renamed from: l */
    private static byte[] m1375l(String str) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream2 = (ByteArrayOutputStream) C0029bb.m761a((Object) new ByteArrayOutputStream());
            byteArrayOutputStream = byteArrayOutputStream2;
            DataOutputStream dataOutputStream2 = (DataOutputStream) C0029bb.m761a((Object) new DataOutputStream(byteArrayOutputStream2));
            dataOutputStream = dataOutputStream2;
            dataOutputStream2.writeUTF(str);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            C0029bb.m764a((OutputStream) dataOutputStream);
            C0029bb.m764a((OutputStream) byteArrayOutputStream);
            return byteArray;
        } catch (Throwable th) {
            C0029bb.m764a((OutputStream) dataOutputStream);
            C0029bb.m764a((OutputStream) byteArrayOutputStream);
            if (th instanceof RuntimeException) throw (RuntimeException) th;
            if (th instanceof Error) throw (Error) th;
            throw new RuntimeException(th);
        }
    }

    /* renamed from: j */
    public final ByteBuffer m1376j(String str) {
        byte[] bArrM1375l = m1375l(str);
        m1302a(bArrM1375l);
        C0040k.m1209a(bArrM1375l);
        return this;
    }

    /* renamed from: k */
    public final ByteBuffer m1377k(String str) {
        byte[] bArrM1375l = m1375l(str);
        m1303a(bArrM1375l, 2, bArrM1375l.length - 2);
        C0040k.m1209a(bArrM1375l);
        return this;
    }

    /* renamed from: L */
    private String m1378L() {
        return m1370r(m1353u());
    }

    /* renamed from: a */
    private final boolean m1379a(int i, int i2) {
        if (i2 <= 0) {
            return false;
        }
        byte[] bArr = this.f383a;
        while (i2 > 0) {
            int i3 = i;
            i++;
            byte b = bArr[i3];
            i2--;
            int i4 = (b & 224) == 192 ? 1 : (b & 240) == 224 ? 2 : (b & 248) == 240 ? 3 : (b & 252) == 248 ? 4 : (b & 254) == 252 ? 5 : 0;
            if (i4 != 0) {
                while (true) {
                    i4--;
                    if (i4 < 0) {
                        if (i2 == 0) {
                            return true;
                        }
                    } else {
                        if (i2 <= 0) {
                            return false;
                        }
                        int i5 = i;
                        i++;
                        if ((bArr[i5] & 192) != 128) {
                            return false;
                        }
                        i2--;
                    }
                }
            } else if ((b & 128) == 128) {
                return false;
            }
        }
        return true;
    }

    /* renamed from: F */
    public final ByteBuffer m1380F() {
        return new ByteBuffer().m1303a(this.f383a, this.f385c, this.f384b);
    }

    /* renamed from: G */
    public final String m1381G() {
        m1300a(2);
        byte[] bArr = this.f383a;
        int i = this.f384b;
        Utils.m490a((Object) bArr, 0, (Object) bArr, 2, i);
        bArr[0] = (byte) (i >>> 8);
        bArr[1] = (byte) i;
        String strM0a = StringUtils.m0a(bArr, this.f385c);
        m1301b();
        return strM0a;
    }

    /* renamed from: s */
    public final ByteBuffer m1382s(int i) {
        return m1314d(StringUtils.m17c(Integer.toString(i)));
    }

    /* renamed from: b */
    public final ByteBuffer m1383b(long j) {
        return m1314d(StringUtils.m17c(Long.toString(j)));
    }

    /* renamed from: t */
    public final ByteBuffer m1384t(int i) {
        String strM17c = StringUtils.m17c(Integer.toString(i));
        return m1360p(strM17c.length()).m1314d(strM17c);
    }

    /* renamed from: u */
    public final ByteBuffer m1385u(int i) {
        return m1386c(i & 4294967295L);
    }

    /* renamed from: c */
    public final ByteBuffer m1386c(long j) {
        while (j != 0) {
            m1321f((int) j);
            j >>>= 8;
        }
        return this;
    }

    /* JADX DEBUG: Move duplicate insns, count: 2 to block B:11:0x005d */
    /* renamed from: H */
    public final String m1387H() {
        int i;
        int i2;
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int i3 = this.f385c;
        int i4 = this.f384b;
        for (int i5 = 0; i5 < i4; i5++) {
            int i6 = this.f383a[i3 + i5] & 255;
            int i7 = i6 >> 4;
            StringBuffer stringBufferAppend = stringBufferM1217h.append(i7 < 10 ? (char) (i7 + 48) : (char) (i7 + 87));
            int i8 = i6 & 15;
            if (i8 < 10) {
                i = i8;
                i2 = 48;
            } else {
                i = i8;
                i2 = 87;
            }
            stringBufferAppend.append((char) (i + i2));
        }
        m1301b();
        return C0040k.m1215a(stringBufferM1217h);
    }

    /* renamed from: I */
    public final XmlElement m1388I() {
        return new XmlParser(m1381G()).m47a();
    }

    /* renamed from: J */
    public final XmlElement m1389J() {
        return new XmlParser(m1317c()).m47a();
    }

    /* renamed from: v */
    public final ByteBuffer m1390v(int i) {
        return m1360p(i & 255).m1321f(i >>> 8);
    }

    /* renamed from: b */
    public final ByteBuffer m1391b(String[] strArr) {
        ByteBuffer c0043n = new ByteBuffer();
        int length = strArr == null ? 0 : strArr.length;
        int i = length;
        c0043n.m1360p(length);
        for (int i2 = 0; i2 < i; i2++) {
            c0043n.m1308a(strArr[i2]);
        }
        return m1327c(c0043n);
    }

    /* renamed from: a */
    public final ByteBuffer m1392a(Vector vector) {
        ByteBuffer c0043n = new ByteBuffer();
        c0043n.m1360p(0);
        return m1327c(c0043n);
    }
}
