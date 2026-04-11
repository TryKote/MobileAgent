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

public final class VCard {

    public String latStr = Storage.emptyStr;

    public String lonStr = Storage.emptyStr;

    public String mapTypeStr = Storage.emptyStr;

    public String phone = Storage.emptyStr;

    public String email = Storage.emptyStr;

    public String nickname = Storage.emptyStr;

    public String zoomStr = Storage.emptyStr;

    public String address = Storage.emptyStr;

    public int gender = 2;

    public String[] photoUrls = new String[0];

    public String[] prevPhotoUrls = new String[0];

    public boolean dirty;

    public static long staticTs1;

    public static long staticTs2;

    public static long staticTs3;

    public static long staticTs4;

    public static long staticTs5;

    public final void setCardData(String lat, String lon, String mapType, String phone, String email, String nickname, String address, String zoom) {
        this.latStr = lat;
        this.lonStr = lon;
        this.mapTypeStr = mapType;
        this.phone = phone;
        this.email = email;
        this.nickname = nickname;
        this.zoomStr = zoom;
        this.address = address;
        this.dirty = false;
    }

    public final void updatePhotos(XmlElement element) {
        Vector children;
        String[] urls;
        if (element == null) {
            return;
        }
        this.prevPhotoUrls = this.photoUrls;
        String[] emptyUrls = new String[0];
        if (element.children == null || (children = ((XmlElement) element.children.elementAt(0)).children) == null) {
            urls = emptyUrls;
        } else {
            int size = children.size();
            String[] parsedUrls = new String[size];
            for (int i = 0; i < size; i++) {
                parsedUrls[i] = ((XmlElement) children.elementAt(i)).getIntAttribute(PackedStringKeys.ATTR_EMAIL);
            }
            urls = parsedUrls;
        }
        this.photoUrls = urls;
    }

    public static final String[] parseCardFromBuffer(ByteBuffer buffer) {
        if (buffer.length == 0 || buffer.readInt() == 0) {
            return null;
        }
        String[] cardData = new String[8];
        cardData[0] = buffer.readWideStr();
        cardData[1] = buffer.readWideStr();
        cardData[2] = buffer.readWideStr();
        cardData[3] = buffer.readUTF8Str((String) null);
        cardData[4] = buffer.readWideStr();
        cardData[5] = buffer.readWideStr();
        if (StringUtils.matchesKey(PackedStringKeys.MRIM_MAPOBJECT, cardData[2])) {
            cardData[6] = buffer.readWideStr();
            cardData[7] = buffer.readWideStr();
        } else {
            cardData[6] = Storage.emptyStr;
            cardData[7] = Storage.emptyStr;
        }
        return cardData;
    }

    public final long getLongitude() {
        return MapUtils.longitudeToPixel(this.lonStr);
    }

    public final long getLatitude() {
        return MapUtils.latitudeToPixel(this.latStr);
    }

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

    public final boolean hasCoordinates() {
        return (StringUtils.isEmpty(this.latStr) || StringUtils.isEmpty(this.lonStr)) ? false : true;
    }

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

    public final void clearCoordinates() {
        String empty = Storage.emptyStr;
        this.lonStr = empty;
        this.latStr = empty;
        this.dirty = false;
    }

    public static final String formatLocationUrl(int zoom, String lat, String lon) {
        return new ByteBuffer().writeCompressed(PackedStringKeys.URL_GEO_SEARCH_ZOOM).writeIntAsString(zoom).writeUInt(4028454).writeRawString(lat).writeUInt(4028710).writeRawString(lon).writeCompressed(PackedStringKeys.PARAM_DIST_RAND).writeIntAsString(Utils.nextRandom()).getStringAndClear();
    }

    public static final String formatPhoneContactUrl(PhoneContact phoneContact, int idFrom) {
        return new ByteBuffer().writeCompressed(PackedStringKeys.URL_GEO_LAT1).writeRawString(phoneContact.surname).writeCompressed(PackedStringKeys.PARAM_LON1).writeRawString(phoneContact.firstName).writeCompressed(PackedStringKeys.PARAM_LAT2).writeRawString(phoneContact.address).writeCompressed(PackedStringKeys.PARAM_LON2).writeRawString(phoneContact.phone).writeCompressed(PackedStringKeys.PARAM_QUANTITY_IDFROM).writeIntAsString(idFrom).writeCompressed(PackedStringKeys.PARAM_RAND).writeIntAsString(Utils.nextRandom()).getStringAndClear();
    }

    public static final Vector parseMapPointsFromJson(ByteBuffer buffer, long centerX, long centerY) throws NumberFormatException {
        Vector jsonArray = null;
        Vector points = null;
        try {
            jsonArray = (Vector) JsonParser.parseUTF8(buffer, 2);
        } catch (Throwable unused) {
        }
        if (jsonArray != null) {
            if (!jsonArray.isEmpty()) {
                points = ObjectPool.newVector();
            }
            int size = jsonArray.size();
            for (int i = size - 1; i >= 0; i--) {
                Hashtable entry = (Hashtable) jsonArray.elementAt(i);
                String path = (String) entry.get("Path");
                int typeCode = Integer.parseInt((String) entry.get("TypeCode"));
                MapPoint mapPoint = new MapPoint(path, centerX, centerY, MapPoint.getMarkerType(typeCode));
                mapPoint.height = 1;
                mapPoint.typeCode = typeCode;
                mapPoint.objectCode = Integer.parseInt((String) entry.get("ObjCode"));
                points.addElement(mapPoint);
            }
        }
        return points;
    }
}
