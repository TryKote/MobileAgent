package p000;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* renamed from: j */
/* loaded from: MobileAgent_3.9.jar:j.class */
public final class ConnectionThread {

    /* renamed from: b */
    public Throwable f348b;

    /* renamed from: j */
    private Object[] f350j;

    /* renamed from: k */
    private String f351k;

    /* renamed from: l */
    private static Hashtable f352l;

    /* renamed from: d */
    public static Hashtable f353d;

    /* renamed from: e */
    public static String f354e;

    /* renamed from: f */
    public static Vector f355f;

    /* renamed from: g */
    public static boolean f356g;

    /* renamed from: m */
    private static Screen f357m;

    /* renamed from: h */
    public static ListItem f358h;

    /* renamed from: i */
    private ByteBuffer f346i = new ByteBuffer();

    /* renamed from: a */
    public final ByteBuffer f347a = new ByteBuffer();

    /* renamed from: c */
    public int f349c = 1;

    public ConnectionThread(String str) {
        this.f351k = str;
        Vector vectorM614m = AppState.m614m(1358);
        if (vectorM614m != null) {
            synchronized (vectorM614m) {
                vectorM614m.addElement(IOUtils.m761a(this));
            }
        }
    }

    /* renamed from: a */
    public final int m1131a() throws Throwable {
        if (this.f348b != null) {
            throw this.f348b;
        }
        return this.f349c;
    }

    /* renamed from: a */
    public final void m1132a(ByteBuffer c0043n) throws Throwable {
        synchronized (this.f350j) {
            if (this.f346i.length > 0) {
                ByteBuffer c0043n2 = this.f346i;
                int i = c0043n2.length;
                if (i > 0) {
                    synchronized (c0043n2) {
                        c0043n.writeBytesAt(c0043n2.data, c0043n2.offset, i);
                        c0043n2.offset += i;
                        c0043n2.length -= i;
                        c0043n2.compact();
                    }
                }
            } else if (this.f348b != null) {
                throw this.f348b;
            }
        }
    }

    /* renamed from: b */
    public final void m1133b() {
        switch (this.f349c) {
            case 1:
                try {
                    this.f350j = NetworkUtils.m1186a(new ByteBuffer().writeCompressed(593549).writeRawString(this.f351k).getStringAndClear(), AppState.m587e(112));
                    if (this.f349c == 1) {
                        this.f349c = 2;
                    }
                    this.f351k = null;
                    return;
                } catch (Throwable th) {
                    this.f349c = -1;
                    this.f348b = th;
                    NetworkUtils.m1184b(this.f350j);
                    return;
                }
            case 2:
                m1134o();
                m1135p();
                return;
            case 3:
                m1134o();
                m1135p();
                NetworkUtils.m1184b(this.f350j);
                this.f349c = 0;
                return;
            default:
                Vector vectorM614m = AppState.m614m(1358);
                if (vectorM614m != null) {
                    synchronized (vectorM614m) {
                        vectorM614m.removeElement(this);
                        Utils.m526b(vectorM614m);
                        IOUtils.m762b(this);
                    }
                    return;
                }
                return;
        }
    }

    /* renamed from: o */
    private final void m1134o() {
        int iM1190a;
        try {
            if (this.f349c == 2) {
                ByteBuffer c0043n = this.f346i;
                Object[] objArr = this.f350j;
                int iM1188c = NetworkUtils.m1188c(objArr);
                if (iM1188c > 0) {
                    byte[] bArrM1211a = NetworkUtils.m1211a(iM1188c);
                    int i = 0;
                    do {
                        iM1190a = i + NetworkUtils.m1190a(objArr, bArrM1211a, i, iM1188c - i);
                        i = iM1190a;
                    } while (iM1190a != iM1188c);
                    synchronized (c0043n) {
                        c0043n.ensureCapacity(iM1188c);
                        Utils.m490a((Object) bArrM1211a, 0, (Object) c0043n.data, c0043n.length, iM1188c);
                        c0043n.length += iM1188c;
                        c0043n.compact();
                    }
                    NetworkUtils.m1209a(bArrM1211a);
                }
            }
        } catch (Throwable th) {
            this.f349c = -1;
            this.f348b = th;
            NetworkUtils.m1184b(this.f350j);
        }
    }

    /* renamed from: p */
    private final void m1135p() {
        try {
            if (this.f349c == 2) {
                ByteBuffer c0043n = this.f347a;
                Object[] objArr = this.f350j;
                synchronized (c0043n) {
                    int i = c0043n.length;
                    if (i > 0) {
                        byte[] bArrM1211a = NetworkUtils.m1211a(i);
                        Utils.m490a((Object) c0043n.data, c0043n.offset, (Object) bArrM1211a, 0, i);
                        c0043n.offset += i;
                        c0043n.length -= i;
                        c0043n.compact();
                        NetworkUtils.m1189a(objArr, bArrM1211a, i);
                        NetworkUtils.m1209a(bArrM1211a);
                    }
                }
            }
        } catch (Throwable th) {
            this.f349c = -1;
            this.f348b = th;
            NetworkUtils.m1184b(this.f350j);
        }
    }

    /* renamed from: q */
    private static void m1136q() {
        XmppContactGroup.f310a = NetworkUtils.m1213g();
        f355f = Utils.m515b(AppState.m584b(264), (char) 0);
        try {
            ByteBuffer c0043nM986d = ResourceManager.m986d(AppState.m584b(265));
            f352l = new Hashtable();
            try {
                if (c0043nM986d.length > 0) {
                    int iM1355w = c0043nM986d.readIntBE();
                    while (true) {
                        iM1355w--;
                        if (iM1355w < 0) {
                            break;
                        }
                        NetworkUtils c0040k = new NetworkUtils(c0043nM986d);
                        f352l.put(StringUtils.m17c(Integer.toString(c0040k.f360b)), c0040k);
                    }
                }
            } catch (Throwable unused) {
            }
            m1146e();
            AppState.m594c(1576, 1);
        } catch (Throwable unused2) {
        }
    }

    /* renamed from: a */
    public static final void m1137a(int i, XmlElement c0022av, boolean z) {
        f352l = new Hashtable();
        Vector vector = c0022av.children;
        if (vector == null) {
            return;
        }
        for (int i2 = 0; i2 < Utils.m541c(vector); i2++) {
            XmlElement c0022av2 = (XmlElement) vector.elementAt(i2);
            String strM555c = c0022av2.getLongKeyAttr(25705);
            NetworkUtils c0040k = new NetworkUtils(Integer.parseInt(strM555c), c0022av2.getIntAttribute(262601), Integer.parseInt(c0022av2.getIntAttribute(201594)), c0022av2.getIntAttribute(529266));
            Vector vector2 = c0022av2.children;
            int i3 = 0;
            while (i3 < Utils.m541c(vector2)) {
                int i4 = i3;
                i3++;
                XmlElement c0022av3 = (XmlElement) vector2.elementAt(i4);
                if (StringUtils.m3a(263156, c0022av3.tagName)) {
                    c0040k.f362d = StringUtils.m11a(c0022av3.textContent);
                }
            }
            f352l.put(strM555c, c0040k);
        }
        f353d = new Hashtable();
        AppState.m594c(1576, 1);
        try {
            AppState.m601a(265, (Object) AppState.f181d);
            AppState.m601a(265, (Object) m1145r().toBase64());
        } catch (Throwable unused) {
            AppState.m601a(254, (Object) AppState.f181d);
        }
    }

    /* renamed from: a */
    public static final String m1138a(Object obj) {
        NetworkUtils c0040k;
        if (!AppState.m587e(1576) || (c0040k = (NetworkUtils) f352l.get(obj)) == null) {
            return null;
        }
        return c0040k.f361c;
    }

    /* renamed from: a */
    public static final Image m1139a(String str) {
        NetworkUtils c0040k;
        Image image;
        if (!AppState.m587e(1576)) {
            return null;
        }
        synchronized (f353d) {
            Image image2 = (Image) f353d.get(str);
            Image image3 = image2;
            if (image2 == null) {
                try {
                    Hashtable hashtable = f353d;
                    Image imageM1348r = XmppMailRuProtocol.m851h(StringUtils.m9b("upi", str)).toImage();
                    image3 = imageM1348r;
                    hashtable.put(str, imageM1348r);
                } catch (Throwable unused) {
                    if (f354e == null) {
                        f354e = str;
                        new AsyncTask(14, (!AppState.m587e(1576) || (c0040k = (NetworkUtils) f352l.get(str)) == null) ? null : c0040k.f362d);
                    }
                }
                image = image3;
            } else {
                image = image3;
            }
        }
        return image;
    }

    /* renamed from: a */
    public static final Vector m1140a(int i) {
        if (!AppState.m587e(1576)) {
            return null;
        }
        Vector vectorM1213g = NetworkUtils.m1213g();
        Enumeration enumerationKeys = f352l.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!m1144c(objNextElement)) {
                NetworkUtils c0040k = (NetworkUtils) f352l.get(objNextElement);
                if (c0040k != null && c0040k.f359a == 1) {
                    vectorM1213g.addElement(objNextElement);
                }
            }
        }
        return vectorM1213g;
    }

    /* renamed from: c */
    public static final Vector m1141c() {
        if (!AppState.m587e(1576)) {
            return null;
        }
        Vector vectorM1213g = NetworkUtils.m1213g();
        Enumeration enumerationKeys = f352l.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!m1144c(objNextElement)) {
                vectorM1213g.addElement(objNextElement);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: d */
    public static final Vector m1142d() {
        if (!AppState.m587e(1576)) {
            return null;
        }
        Vector vectorM1213g = NetworkUtils.m1213g();
        Enumeration enumerationKeys = f352l.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!(m1143b(objNextElement) == 2)) {
                vectorM1213g.addElement(objNextElement);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: b */
    private static final int m1143b(Object obj) {
        if (!AppState.m587e(1576)) {
            return 2;
        }
        try {
            return ((NetworkUtils) f352l.get(obj)).f363e;
        } catch (Throwable unused) {
            return 2;
        }
    }

    /* renamed from: c */
    private static final boolean m1144c(Object obj) {
        return m1143b(obj) == 0;
    }

    /* renamed from: r */
    private static ByteBuffer m1145r() {
        ByteBuffer c0043n = new ByteBuffer();
        c0043n.writeIntBE(f352l.size());
        Enumeration enumerationKeys = f352l.keys();
        while (enumerationKeys.hasMoreElements()) {
            NetworkUtils c0040k = (NetworkUtils) f352l.get(enumerationKeys.nextElement());
            c0043n.writeIntBE(c0040k.f359a).writeIntBE(c0040k.f360b).writeStringUTF16(c0040k.f361c).writeStringLatin1(c0040k.f362d).writeIntBE(c0040k.f363e).writeStringLatin1(c0040k.f364f);
        }
        return c0043n;
    }

    /* renamed from: e */
    public static final void m1146e() {
        f353d = new Hashtable();
    }

    /* renamed from: a */
    public static final Object[] m1147a(StringBuffer stringBuffer) {
        Object[] objArr = new Object[9];
        objArr[0] = NetworkUtils.m1221a(5522759);
        objArr[2] = NetworkUtils.m1215a(stringBuffer);
        return objArr;
    }

    /* renamed from: a */
    public static final Object[] m1148a(String str, StringBuffer stringBuffer) {
        Object[] objArr = new Object[9];
        objArr[0] = NetworkUtils.m1221a(1414745936);
        objArr[2] = str;
        objArr[3] = NetworkUtils.m1215a(stringBuffer).getBytes();
        return objArr;
    }

    /* renamed from: a */
    public static final Object[] m1149a(Object[] objArr) {
        objArr[7] = new AsyncTask(5, objArr);
        return objArr;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v19, resolved type: java.lang.Object[] */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: b */
    public static final void m1150b(Object[] objArr) throws InterruptedException {
        MrimAccount c0028ba = (MrimAccount) AppState.m616i();
        Object[] objArrM1151a = m1151a(objArr, c0028ba);
        if (objArr[8] != null) {
            objArr[4] = objArrM1151a;
            return;
        }
        if (IOUtils.m805a(objArrM1151a) && JsonParser.isSuccess(JsonParser.parseJson(((ByteBuffer) objArrM1151a[3]).duplicate()))) {
            objArr[4] = objArrM1151a;
            return;
        }
        objArr[8] = objArr;
        MrimAccount c0028ba2 = (MrimAccount) AppState.m616i();
        Object[] objArrM1147a = m1147a(NetworkUtils.m1217h().append(AppState.m584b(1771076)).append(c0028ba2.login).append(AppState.m584b(656925)).append(c0028ba2.password).append(AppState.m584b(1381)));
        objArrM1147a[8] = objArrM1147a;
        ((AsyncTask) m1149a(objArrM1147a)[7]).f436a.join();
        c0028ba.f225a = (String) objArrM1147a[6];
        objArr[4] = m1151a(objArr, c0028ba);
    }

    /* renamed from: a */
    private static final Object[] m1151a(Object[] objArr, MrimAccount c0028ba) {
        String strM1215a;
        HttpClient c0024ax = null;
        try {
            try {
                try {
                    try {
                        AppController.m343s();
                        String str = (String) objArr[5];
                        if (str == null) {
                            strM1215a = NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(1115687)).append(objArr[2]));
                        } else {
                            strM1215a = str;
                        }
                        HttpClient c0024axM629a = HttpClient.m629a(strM1215a, c0028ba, 1);
                        c0024ax = c0024axM629a;
                        Object[] objArrM1152a = m1152a(objArr, c0024axM629a);
                        HttpClient.m633a(c0024ax);
                        AppController.m344t();
                        return objArrM1152a;
                    } catch (ConnectionNotFoundException e) {
                        Object[] objArrM798a = IOUtils.m798a((Throwable) null);
                        HttpClient.m633a(c0024ax);
                        AppController.m344t();
                        return objArrM798a;
                    }
                } catch (Throwable th) {
                    Object[] objArrM801d = IOUtils.m801d((Throwable) null);
                    HttpClient.m633a(c0024ax);
                    AppController.m344t();
                    return objArrM801d;
                }
            } catch (IllegalArgumentException e2) {
                Object[] objArrM799b = IOUtils.m799b((Throwable) null);
                HttpClient.m633a(c0024ax);
                AppController.m344t();
                return objArrM799b;
            } catch (SecurityException e3) {
                Object[] objArrM800c = IOUtils.m800c((Throwable) null);
                HttpClient.m633a(c0024ax);
                AppController.m344t();
                return objArrM800c;
            }
        } catch (Throwable th2) {
            HttpClient.m633a(c0024ax);
            AppController.m344t();
            throw th2;
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [java.lang.Object[], java.lang.Throwable] */
    /* renamed from: a */
    private static final Object[] m1152a(Object[] objArr, HttpClient c0024ax) {
        Object[] M1155b;
        try {
            c0024ax.m635b((String) objArr[0]);
            m1153a(c0024ax, 919726, 788668);
            m1153a(c0024ax, 657608, 329938);
            m1154a(c0024ax, 395489, ((MrimAccount) AppState.m616i()).f225a);
            byte[] bArr = (byte[]) objArr[3];
            if (bArr != null) {
                m1153a(c0024ax, 788628, 2164851);
                c0024ax.m637a(bArr, bArr.length);
            }
            M1155b = m1155b(objArr, c0024ax);
            return M1155b;
        } catch (Throwable th) {
            return IOUtils.m802e(th);
        }
    }

    /* renamed from: a */
    public static final void m1153a(HttpClient c0024ax, int i, int i2) throws IOException {
        m1154a(c0024ax, i, AppState.m584b(i2));
    }

    /* renamed from: a */
    private static void m1154a(HttpClient c0024ax, int i, String str) throws IOException {
        if (str != null) {
            c0024ax.m636a(AppState.m584b(i), str);
        }
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.Object[], java.lang.Throwable] */
    /* renamed from: b */
    private static final Object[] m1155b(Object[] objArr, HttpClient c0024ax) {
        Object[] M804a;
        try {
            int iM634a = c0024ax.m634a();
            int i = 0;
            while (true) {
                try {
                    String headerFieldKey = ((javax.microedition.io.HttpConnection) c0024ax.f183a).getHeaderFieldKey(i);
                    String headerField = ((javax.microedition.io.HttpConnection) c0024ax.f183a).getHeaderField(i);
                    if (headerFieldKey == null && headerField == null) {
                        break;
                    }
                    if (headerFieldKey != null && headerField != null && headerField.startsWith(AppState.m584b(329959)) && StringUtils.m3a(657623, StringUtils.m17c(headerFieldKey.toLowerCase()))) {
                        objArr[6] = StringUtils.m13b(headerField, headerField.indexOf(59));
                    }
                    i++;
                } catch (Throwable unused) {
                }
            }
            M804a = IOUtils.m804a(iM634a, StringUtils.m17c(Integer.toString(iM634a)), new ByteBuffer(c0024ax));
            return M804a;
        } catch (Throwable th) {
            return IOUtils.m803f(th);
        }
    }

    /* renamed from: c */
    public static final Object[] m1156c(Object[] objArr) {
        Object[] objArr2 = (Object[]) objArr[4];
        if (objArr2 != null) {
            return objArr2;
        }
        return null;
    }

    /* renamed from: f */
    public static final void m1157f() {
        m1159s();
        AppState.m594c(1476, 6);
        Screen c0013amM75b = ScreenManager.m75b(1578);
        f357m = c0013amM75b;
        m1160d(c0013amM75b);
        ScreenManager.m70a(c0013amM75b);
        TabBar.m171g();
        TabBar.m176a(6, (Account) null);
        TabBar.f44a = AppState.m587e(1414);
        if (AppState.m587e(281)) {
            return;
        }
        ScreenBuilder.m546a(178);
    }

    /* renamed from: g */
    public static final void m1158g() {
        if (AppState.m587e(1547)) {
            if (AppState.m587e(1409) || f357m == null) {
                return;
            }
            f357m.m259a(AppState.m584b(330), AppState.m584b(1055), 167, 4, 167);
            AppState.m594c(1409, 1);
            return;
        }
        if (!AppState.m587e(1409) || f357m == null) {
            return;
        }
        m1160d(f357m);
        AppState.m594c(1409, 0);
    }

    /* renamed from: s */
    private static final void m1159s() {
        if (f356g) {
            return;
        }
        f356g = true;
        int i = ScreenManager.m75b(1578).f115r;
        AppState.m597a(1558, 4178628L);
        AppState.m597a(1560, 7482960L);
        AppState.f177b[1400] = XmppContactGroup.m1047d(225);
        AppState.f177b[1401] = XmppContactGroup.m1047d(226);
        AppState.m594c(1415, AppState.m586d(1528));
        AppState.m594c(1416, i);
        AppState.m597a(1410, AppState.m598g(35));
        AppState.m597a(1412, AppState.m598g(37));
        MapRenderer.f193a = AppState.m586d(1415);
        MapRenderer.f194b = AppState.m586d(1416);
        MapRenderer.f195c = AppState.m598g(1412);
        MapRenderer.f196d = AppState.m598g(1410);
        int iM586d = AppState.m586d(39);
        MapRenderer.f197e = AppController.m317a(MapRenderer.f196d, iM586d);
        MapRenderer.f198f = AppController.m317a(MapRenderer.f195c, iM586d);
        AppState.f177b[1364] = Image.createImage(MapRenderer.f193a, MapRenderer.f194b);
        StringUtils.m19b();
        AppState.f177b[1398] = NetworkUtils.m1213g();
        AppState.f177b[1396] = NetworkUtils.m1213g();
        Object[] objArrM332c = AppController.m332c(AppState.f181d);
        AppState.f177b[1395] = objArrM332c;
        XmppContactGroup.m1041a(objArrM332c);
        Image imageCreateImage = Image.createImage(128, 128);
        Graphics graphics = imageCreateImage.getGraphics();
        int i2 = 0;
        graphics.setColor(13158600);
        for (int i3 = 0; i3 < 128; i3 += 2) {
            for (int i4 = i2; i4 < 128; i4 += 4) {
                graphics.fillRect(i3, i4, 2, 2);
            }
            i2 ^= 2;
        }
        new GraphicsContext(graphics).m216a(312, 56, 56);
        AppState.f177b[1393] = imageCreateImage;
        AppState.f177b[1397] = NetworkUtils.m1213g();
        new AsyncTask(8);
        MapRenderer.f199g = new Object();
        StringUtils.m41j();
        MapRenderer.m646a();
        MmpContact.f70n = NetworkUtils.m1213g();
        MmpContact.f66k = NetworkUtils.m1213g();
        MmpContact.f67l = NetworkUtils.m1213g();
        MmpContact.f64i = new long[2];
        MmpContact.f65j = new long[2];
        MapRenderer.f205m = NetworkUtils.m1213g();
        if (AppState.m587e(253)) {
            XmppContactGroup.m1049b(AppState.m614m(1401));
        }
        AppState.f177b[1383] = NetworkUtils.m1213g();
        MapRenderer.f200h = true;
        AppState.m597a(1556, System.currentTimeMillis() - 90);
        new AsyncTask(10);
        m1136q();
    }

    /* renamed from: d */
    private static final void m1160d(Screen c0013am) {
        c0013am.m259a(AppState.m584b(1062), AppState.m584b(AppState.m587e(1414) ? 1050 : 328), 20, 0, 0);
    }

    /* renamed from: a */
    public static final void m1161a(Screen c0013am) {
        if (AppState.m587e(1414)) {
            return;
        }
        m1164h();
        m1160d(c0013am);
    }

    /* renamed from: b */
    public static final int m1162b(Screen c0013am) {
        MrimAccount c0028ba;
        if (AppState.m587e(1547)) {
            ((MrimAccount) AppState.m616i()).f232h = false;
            MapRenderer.f200h = true;
            m1164h();
            return 0;
        }
        if (AppState.m587e(1479) && (c0028ba = (MrimAccount) AppState.m616i()) != null) {
            c0028ba.deselect();
        }
        m1164h();
        m1160d(c0013am);
        return 0;
    }

    /* renamed from: c */
    public static final void m1163c(Screen c0013am) {
        if (AppState.m587e(1414)) {
            AppState.m594c(1564, 3);
        } else {
            m1161a(c0013am);
        }
    }

    /* renamed from: h */
    public static final void m1164h() {
        boolean z = !AppState.m587e(1414);
        boolean z2 = z;
        AppState.m599a(1414, z);
        if (!z2) {
            AppState.m594c(1479, 0);
        }
        TabBar.f44a = z2;
    }

    /* renamed from: a */
    public static final void m1165a(MapPoint c0014an, boolean z) {
        m1159s();
        if (z) {
            XmppContactGroup.m1043a(AppState.m614m(1400), c0014an, 0, 5);
            XmppContactGroup.m1046a(AppState.m614m(1400), 225);
        }
        MapRenderer.f202j = c0014an;
        MapRenderer.m646a();
        MapRenderer.m649a(MapRenderer.f202j.f138f, MapRenderer.f202j.f139g);
        MapRenderer.m651a(MapRenderer.f202j.f140h);
        MapRenderer.f202j.m268b();
        MapRenderer.m661e();
    }

    /* renamed from: i */
    public static final int m1166i() {
        Vector vectorM614m = AppState.m614m(1399);
        if (vectorM614m != null) {
            AppState.m591f(1399);
        }
        if (vectorM614m == null) {
            return 0;
        }
        AppController.f153g = true;
        int size = vectorM614m.size();
        if (size == 0) {
            return AppController.m338l(327);
        }
        Screen c0013amM75b = ScreenManager.m75b(1717);
        for (int i = 0; i < size; i++) {
            MapPoint c0014an = (MapPoint) vectorM614m.elementAt(i);
            c0013amM75b.m247a(-1, c0014an.f133a, 6, c0014an);
        }
        ScreenManager.m71b(c0013amM75b);
        return 0;
    }

    /* renamed from: j */
    public static final Enumeration m1167j() {
        return AppState.m614m(1401).elements();
    }

    /* renamed from: k */
    public static final boolean m1168k() {
        return AppState.m614m(1401).size() > 0;
    }

    /* renamed from: a */
    public static final void m1169a(MapPoint c0014an) {
        Vector vectorM614m = AppState.m614m(1401);
        vectorM614m.removeElement(c0014an);
        XmppContactGroup.m1046a(vectorM614m, 226);
    }

    /* renamed from: l */
    public static final void m1170l() {
        long jMo274v;
        long jMo275w;
        if (MapRenderer.m656d()) {
            MmpContact.m195u();
        }
        ListItem interfaceC0044o = MapRenderer.f203k;
        if (interfaceC0044o == null || !interfaceC0044o.isSelected()) {
            jMo274v = MapRenderer.f196d;
            jMo275w = MapRenderer.f195c;
        } else {
            jMo274v = interfaceC0044o.getWidth();
            jMo275w = interfaceC0044o.getBaseHeight();
            interfaceC0044o.select();
        }
        MmpContact.m182a(jMo274v, jMo275w);
        MapRenderer.f200h = true;
        if (MapRenderer.m656d()) {
            Conversation.m1129c();
        }
    }

    /* renamed from: m */
    public static final void m1171m() {
        long jMo274v;
        long jMo275w;
        if (MapRenderer.m656d()) {
            MmpContact.m195u();
        }
        ListItem interfaceC0044o = MapRenderer.f203k;
        if (interfaceC0044o == null || !interfaceC0044o.isSelected()) {
            jMo274v = MapRenderer.f196d;
            jMo275w = MapRenderer.f195c;
        } else {
            jMo274v = interfaceC0044o.getWidth();
            jMo275w = interfaceC0044o.getBaseHeight();
            interfaceC0044o.select();
        }
        MmpContact.m183b(jMo274v, jMo275w);
        MapRenderer.f200h = true;
        if (MapRenderer.m656d()) {
            Conversation.m1129c();
        }
    }

    /* renamed from: a */
    public static final void m1172a(ListItem interfaceC0044o) {
        if (interfaceC0044o.isSelected()) {
            m1173n();
            MapRenderer.m649a(interfaceC0044o.getWidth(), interfaceC0044o.getBaseHeight());
            MapRenderer.m651a(interfaceC0044o.getCommandCount());
            f358h = interfaceC0044o;
        }
    }

    /* renamed from: n */
    public static final void m1173n() {
        m1159s();
        AppState.m594c(1414, 1);
        MapRenderer.m646a();
    }
}
