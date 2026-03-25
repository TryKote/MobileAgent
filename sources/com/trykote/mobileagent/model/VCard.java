package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
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
    public final void updatePhotos(XmlElement element) {
        Vector vector;
        String[] strArr;
        if (element == null) {
            return;
        }
        this.prevPhotoUrls = this.photoUrls;
        String[] strArr2 = new String[0];
        if (element.children == null || (vector = ((XmlElement) element.children.elementAt(0)).children) == null) {
            strArr = strArr2;
        } else {
            int size = vector.size();
            String[] strArr3 = new String[size];
            for (int i = 0; i < size; i++) {
                strArr3[i] = ((XmlElement) vector.elementAt(i)).getIntAttribute(PackedStringKeys.ATTR_EMAIL);
            }
            strArr = strArr3;
        }
        this.photoUrls = strArr;
    }

    /* renamed from: a */
    public static final String[] parseCardFromBuffer(ByteBuffer buffer) {
        if (buffer.length == 0 || buffer.readInt() == 0) {
            return null;
        }
        String[] strArr = new String[8];
        strArr[0] = buffer.readWideStr();
        strArr[1] = buffer.readWideStr();
        strArr[2] = buffer.readWideStr();
        strArr[3] = buffer.readUTF8Str((String) null);
        strArr[4] = buffer.readWideStr();
        strArr[5] = buffer.readWideStr();
        if (StringUtils.matchesKey(PackedStringKeys.MRIM_MAPOBJECT, strArr[2])) {
            strArr[6] = buffer.readWideStr();
            strArr[7] = buffer.readWideStr();
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
    public static final VCard deserializeFromBuffer(ByteBuffer buffer) {
        VCard vcard = new VCard();
        if (buffer.readBoolean()) {
            try {
                vcard.setCardData(buffer.readWideStr(), buffer.readWideStr(), buffer.readWideStr(), buffer.readUTF8Str((String) null), buffer.readWideStr(), buffer.readWideStr(), buffer.readWideStr(), buffer.readWideStr());
                vcard.gender = buffer.readIntBE();
                vcard.prevPhotoUrls = vcard.photoUrls;
                vcard.photoUrls = new String[0];
                vcard.dirty = buffer.readBoolean();
            } catch (Throwable unused) {
                return null;
            }
        }
        return vcard;
    }

    /* renamed from: c */
    public final boolean hasCoordinates() {
        return (StringUtils.isEmpty(this.latStr) || StringUtils.isEmpty(this.lonStr)) ? false : true;
    }

    /* renamed from: d */
    public final int getCommandCount() {
        try {
            if (StringUtils.matchesKey(PackedStringKeys.MRIM_MAPOBJECT, this.mapTypeStr)) {
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
        return new ByteBuffer().writeCompressed(PackedStringKeys.URL_GEO_SEARCH_ZOOM).writeIntAsString(i).writeUInt(4028454).writeRawString(str).writeUInt(4028710).writeRawString(str2).writeCompressed(PackedStringKeys.PARAM_DIST_RAND).writeIntAsString(Utils.nextRandom()).getStringAndClear();
    }

    /* renamed from: a */
    public static final String formatPhoneContactUrl(PhoneContact phoneContact, int i) {
        return new ByteBuffer().writeCompressed(PackedStringKeys.URL_GEO_LAT1).writeRawString(phoneContact.surname).writeCompressed(PackedStringKeys.PARAM_LON1).writeRawString(phoneContact.firstName).writeCompressed(PackedStringKeys.PARAM_LAT2).writeRawString(phoneContact.address).writeCompressed(PackedStringKeys.PARAM_LON2).writeRawString(phoneContact.phone).writeCompressed(PackedStringKeys.PARAM_QUANTITY_IDFROM).writeIntAsString(i).writeCompressed(PackedStringKeys.PARAM_RAND).writeIntAsString(Utils.nextRandom()).getStringAndClear();
    }

    /* renamed from: a */
    public static final Vector parseMapPointsFromJson(ByteBuffer buffer, long j, long j2) throws NumberFormatException {
        Vector vector = null;
        Vector points = null;
        try {
            vector = (Vector) JsonParser.parseUTF8(buffer, 2);
        } catch (Throwable unused) {
        }
        if (vector != null) {
            if (!vector.isEmpty()) {
                points = ObjectPool.newVector();
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
                MapPoint mapPoint = new MapPoint(str, j, j2, MapPoint.getMarkerType(i));
                mapPoint.height = 1;
                mapPoint.typeCode = i;
                mapPoint.objectCode = Integer.parseInt((String) hashtable.get("ObjCode"));
                points.addElement(mapPoint);
            }
        }
        return points;
    }
}
