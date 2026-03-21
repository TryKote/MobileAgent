package p000;

import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ac */
/* loaded from: MobileAgent_3.9.jar:ac.class */
public final class VCard {

    /* renamed from: a */
    public String f13a = AppState.f181d;

    /* renamed from: b */
    public String f14b = AppState.f181d;

    /* renamed from: c */
    public String f15c = AppState.f181d;

    /* renamed from: d */
    public String f16d = AppState.f181d;

    /* renamed from: e */
    public String f17e = AppState.f181d;

    /* renamed from: f */
    public String f18f = AppState.f181d;

    /* renamed from: g */
    public String f19g = AppState.f181d;

    /* renamed from: h */
    public String f20h = AppState.f181d;

    /* renamed from: i */
    public int f21i = 2;

    /* renamed from: j */
    public String[] f22j = new String[0];

    /* renamed from: k */
    public String[] f23k = new String[0];

    /* renamed from: l */
    public boolean f24l;

    /* renamed from: m */
    public static long f25m;

    /* renamed from: n */
    public static long f26n;

    /* renamed from: o */
    public static long f27o;

    /* renamed from: p */
    public static long f28p;

    /* renamed from: q */
    public static long f29q;

    /* renamed from: a */
    public final void m53a(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) {
        this.f13a = str;
        this.f14b = str2;
        this.f15c = str3;
        this.f16d = str4;
        this.f17e = str5;
        this.f18f = str6;
        this.f19g = str8;
        this.f20h = str7;
        this.f24l = false;
    }

    /* renamed from: a */
    public final void m54a(XmlElement c0022av) {
        Vector vector;
        String[] strArr;
        if (c0022av == null) {
            return;
        }
        this.f23k = this.f22j;
        String[] strArr2 = new String[0];
        if (c0022av.f172b == null || (vector = ((XmlElement) c0022av.f172b.elementAt(0)).f172b) == null) {
            strArr = strArr2;
        } else {
            int size = vector.size();
            String[] strArr3 = new String[size];
            for (int i = 0; i < size; i++) {
                strArr3[i] = ((XmlElement) vector.elementAt(i)).m554b(328413);
            }
            strArr = strArr3;
        }
        this.f22j = strArr;
    }

    /* renamed from: a */
    public static final String[] m55a(ByteBuffer c0043n) {
        if (c0043n.length == 0 || c0043n.readInt() == 0) {
            return null;
        }
        String[] strArr = new String[8];
        strArr[0] = c0043n.readWideStr();
        strArr[1] = c0043n.readWideStr();
        strArr[2] = c0043n.readWideStr();
        strArr[3] = c0043n.readUTF8Str((String) null);
        strArr[4] = c0043n.readWideStr();
        strArr[5] = c0043n.readWideStr();
        if (StringUtils.m3a(590588, strArr[2])) {
            strArr[6] = c0043n.readWideStr();
            strArr[7] = c0043n.readWideStr();
        } else {
            strArr[6] = AppState.f181d;
            strArr[7] = AppState.f181d;
        }
        return strArr;
    }

    /* renamed from: a */
    public final long m56a() {
        return IOUtils.m807b(this.f14b);
    }

    /* renamed from: b */
    public final long m57b() {
        return IOUtils.m808c(this.f13a);
    }

    /* renamed from: b */
    public static final VCard m58b(ByteBuffer c0043n) {
        VCard c0003ac = new VCard();
        if (c0043n.readBoolean()) {
            try {
                c0003ac.m53a(c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readUTF8Str((String) null), c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readWideStr());
                c0003ac.f21i = c0043n.readIntBE();
                c0003ac.f23k = c0003ac.f22j;
                c0003ac.f22j = new String[0];
                c0003ac.f24l = c0043n.readBoolean();
            } catch (Throwable unused) {
                return null;
            }
        }
        return c0003ac;
    }

    /* renamed from: c */
    public final boolean m59c() {
        return (StringUtils.m1a(this.f13a) || StringUtils.m1a(this.f14b)) ? false : true;
    }

    /* renamed from: d */
    public final int m60d() {
        try {
            if (StringUtils.m3a(590588, this.f15c)) {
                return MapPoint.m284e(Integer.parseInt(this.f19g));
            }
            return 10;
        } catch (Throwable unused) {
            return 10;
        }
    }

    /* renamed from: e */
    public final void m61e() {
        String str = AppState.f181d;
        this.f14b = str;
        this.f13a = str;
        this.f24l = false;
    }

    /* renamed from: a */
    public static final String m62a(int i, String str, String str2) {
        return new ByteBuffer().writeCompressed(3473998).writeIntAsString(i).writeUInt(4028454).writeRawString(str).writeUInt(4028710).writeRawString(str2).writeCompressed(1311433).writeIntAsString(Utils.m520a()).getStringAndClear();
    }

    /* renamed from: a */
    public static final String m63a(PhoneContact c0020at, int i) {
        return new ByteBuffer().writeCompressed(1901187).writeRawString(c0020at.f164b).writeCompressed(393954).writeRawString(c0020at.f163a).writeCompressed(393960).writeRawString(c0020at.f166d).writeCompressed(393966).writeRawString(c0020at.f165c).writeCompressed(1311413).writeIntAsString(i).writeCompressed(393943).writeIntAsString(Utils.m520a()).getStringAndClear();
    }

    /* renamed from: a */
    public static final Vector m64a(ByteBuffer c0043n, long j, long j2) throws NumberFormatException {
        Vector vector = null;
        Vector vectorM1213g = null;
        try {
            vector = (Vector) JsonParser.m466a(c0043n, 2);
        } catch (Throwable unused) {
        }
        if (vector != null) {
            if (!vector.isEmpty()) {
                vectorM1213g = NetworkUtils.m1213g();
            }
            int size = vector.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Hashtable hashtable = (Hashtable) vector.elementAt(size);
                String str = (String) hashtable.get("Path");
                int i = Integer.parseInt((String) hashtable.get("TypeCode"));
                MapPoint c0014an = new MapPoint(str, j, j2, MapPoint.m284e(i));
                c0014an.f143k = 1;
                c0014an.f144l = i;
                c0014an.f145m = Integer.parseInt((String) hashtable.get("ObjCode"));
                vectorM1213g.addElement(c0014an);
            }
        }
        return vectorM1213g;
    }
}
