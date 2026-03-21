package p000;

import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ac */
/* loaded from: MobileAgent_3.9.jar:ac.class */
public final class VCard {

    /* renamed from: a */
    public String latStr = AppState.emptyStr;

    /* renamed from: b */
    public String lonStr = AppState.emptyStr;

    /* renamed from: c */
    public String mapTypeStr = AppState.emptyStr;

    /* renamed from: d */
    public String phone = AppState.emptyStr;

    /* renamed from: e */
    public String email = AppState.emptyStr;

    /* renamed from: f */
    public String nickname = AppState.emptyStr;

    /* renamed from: g */
    public String zoomStr = AppState.emptyStr;

    /* renamed from: h */
    public String address = AppState.emptyStr;

    /* renamed from: i */
    public int gender = 2;

    /* renamed from: j */
    public String[] photoUrls = new String[0];

    /* renamed from: k */
    public String[] prevPhotoUrls = new String[0];

    /* renamed from: l */
    public boolean dirty;

    /* renamed from: m */
    public static long staticTs1;

    /* renamed from: n */
    public static long staticTs2;

    /* renamed from: o */
    public static long staticTs3;

    /* renamed from: p */
    public static long staticTs4;

    /* renamed from: q */
    public static long staticTs5;

    /* renamed from: a */
    public final void setCardData(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8) {
        this.latStr = str;
        this.lonStr = str2;
        this.mapTypeStr = str3;
        this.phone = str4;
        this.email = str5;
        this.nickname = str6;
        this.zoomStr = str8;
        this.address = str7;
        this.dirty = false;
    }

    /* renamed from: a */
    public final void updatePhotos(XmlElement c0022av) {
        Vector vector;
        String[] strArr;
        if (c0022av == null) {
            return;
        }
        this.prevPhotoUrls = this.photoUrls;
        String[] strArr2 = new String[0];
        if (c0022av.children == null || (vector = ((XmlElement) c0022av.children.elementAt(0)).children) == null) {
            strArr = strArr2;
        } else {
            int size = vector.size();
            String[] strArr3 = new String[size];
            for (int i = 0; i < size; i++) {
                strArr3[i] = ((XmlElement) vector.elementAt(i)).getIntAttribute(328413);
            }
            strArr = strArr3;
        }
        this.photoUrls = strArr;
    }

    /* renamed from: a */
    public static final String[] parseCardFromBuffer(ByteBuffer c0043n) {
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
            strArr[6] = AppState.emptyStr;
            strArr[7] = AppState.emptyStr;
        }
        return strArr;
    }

    /* renamed from: a */
    public final long getLongitude() {
        return IOUtils.longitudeToPixel(this.lonStr);
    }

    /* renamed from: b */
    public final long getLatitude() {
        return IOUtils.latitudeToPixel(this.latStr);
    }

    /* renamed from: b */
    public static final VCard deserializeFromBuffer(ByteBuffer c0043n) {
        VCard c0003ac = new VCard();
        if (c0043n.readBoolean()) {
            try {
                c0003ac.setCardData(c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readUTF8Str((String) null), c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readWideStr(), c0043n.readWideStr());
                c0003ac.gender = c0043n.readIntBE();
                c0003ac.prevPhotoUrls = c0003ac.photoUrls;
                c0003ac.photoUrls = new String[0];
                c0003ac.dirty = c0043n.readBoolean();
            } catch (Throwable unused) {
                return null;
            }
        }
        return c0003ac;
    }

    /* renamed from: c */
    public final boolean hasCoordinates() {
        return (StringUtils.isEmpty(this.latStr) || StringUtils.isEmpty(this.lonStr)) ? false : true;
    }

    /* renamed from: d */
    public final int getCommandCount() {
        try {
            if (StringUtils.m3a(590588, this.mapTypeStr)) {
                return MapPoint.getMarkerType(Integer.parseInt(this.zoomStr));
            }
            return 10;
        } catch (Throwable unused) {
            return 10;
        }
    }

    /* renamed from: e */
    public final void clearCoordinates() {
        String str = AppState.emptyStr;
        this.lonStr = str;
        this.latStr = str;
        this.dirty = false;
    }

    /* renamed from: a */
    public static final String formatLocationUrl(int i, String str, String str2) {
        return new ByteBuffer().writeCompressed(3473998).writeIntAsString(i).writeUInt(4028454).writeRawString(str).writeUInt(4028710).writeRawString(str2).writeCompressed(1311433).writeIntAsString(Utils.nextRandom()).getStringAndClear();
    }

    /* renamed from: a */
    public static final String formatPhoneContactUrl(PhoneContact c0020at, int i) {
        return new ByteBuffer().writeCompressed(1901187).writeRawString(c0020at.surname).writeCompressed(393954).writeRawString(c0020at.firstName).writeCompressed(393960).writeRawString(c0020at.address).writeCompressed(393966).writeRawString(c0020at.phone).writeCompressed(1311413).writeIntAsString(i).writeCompressed(393943).writeIntAsString(Utils.nextRandom()).getStringAndClear();
    }

    /* renamed from: a */
    public static final Vector parseMapPointsFromJson(ByteBuffer c0043n, long j, long j2) throws NumberFormatException {
        Vector vector = null;
        Vector vectorM1213g = null;
        try {
            vector = (Vector) JsonParser.parseUTF8(c0043n, 2);
        } catch (Throwable unused) {
        }
        if (vector != null) {
            if (!vector.isEmpty()) {
                vectorM1213g = NetworkUtils.newVector();
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
                MapPoint c0014an = new MapPoint(str, j, j2, MapPoint.getMarkerType(i));
                c0014an.height = 1;
                c0014an.typeCode = i;
                c0014an.objectCode = Integer.parseInt((String) hashtable.get("ObjCode"));
                vectorM1213g.addElement(c0014an);
            }
        }
        return vectorM1213g;
    }
}
