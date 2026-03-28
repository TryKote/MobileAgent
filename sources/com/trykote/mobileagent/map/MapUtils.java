package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import javax.microedition.lcdui.Image;

/* Extracted from AppController: map coordinate math utilities */
public final class MapUtils {

    /* renamed from: d */
    public static final long getZoomNumerator(int i) {
        return (1 << (17 - i)) * ((i < 8 || i > 17) ? 119432 : 1194329);
    }

    /* renamed from: e */
    public static final long getZoomDenominator(int i) {
        return (i < 8 || i > 17) ? 100000L : 1000000L;
    }

    /* renamed from: a */
    public static long coordToPixel(long j, int i) {
        return (j * getZoomDenominator(i)) / getZoomNumerator(i);
    }

    /* renamed from: a */
    public static final long pixelToCoord(int i, int i2) {
        return (i * getZoomNumerator(i2)) / getZoomDenominator(i2);
    }

    /* renamed from: a */
    public static final int computeColor(int i, int i2, int i3, int i4) {
        return Utils.abs(i2 - i4) + Utils.abs(i - i3);
    }

    /* renamed from: a */
    public static final int handleMapSearch(int i, Object obj) {
        if (i == 6) {
            return handleMapPointAction(obj);
        }
        ScreenManager.processScreenForm();
        String query = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_QUERY));
        if (StringUtils.isEmpty(query)) {
            return NotificationHelper.showError(351);
        }
        boolean isCoordinate = true;
        int separatorScore = 0;
        int length = query.length();
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            char ch = query.charAt(length);
            if (ch == '.') {
                separatorScore += 10;
            } else if (ch == ',') {
                separatorScore++;
            } else {
                isCoordinate &= ch >= '0' && ch <= '9';
            }
        }
        if (isCoordinate && separatorScore == 21) {
            try {
                long lon = longitudeToPixel(extractLongitude(query));
                long lat = latitudeToPixel(extractLatitude(query));
                MapRenderer.setPosition(lon, lat);
                MapRenderer.setZoom(StringUtils.isInSavedRegion(lon, lat) ? 13 : 10);
            } catch (Throwable unused) {
            }
        } else {
            String encodedQuery = Conversation.replaceText(query, 1046, 199350);
            Image mapImage = AppState.getImage(StateKeys.OBJ_FONT_2);
            long currentLat = MapRenderer.currentLat;
            new AsyncTask(AsyncTaskId.FETCH_MAP_POINTS, new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_MAPSSEARCH).writeRawString(Conversation.urlEncodeCyrillic((Object) encodedQuery)).writeCompressed(PackedStringKeys.PARAM_USER_LAT).writeLongAsString(currentLat).writeCompressed(PackedStringKeys.PARAM_USER_LON).writeLongAsString(MapRenderer.currentLon).writeCompressed(PackedStringKeys.PARAM_X_SCREEN).writeIntAsString(mapImage.getWidth()).writeCompressed(PackedStringKeys.PARAM_Y_SCREEN).writeIntAsString(mapImage.getHeight()).getStringAndClear());
        }
        return AppState.getBool(StateKeys.FLAG_LOADING) ? 161 : 6;
    }

    /* renamed from: c */
    public static final int handleMapPointAction(Object obj) {
        if (AppState.getBool(StateKeys.FLAG_NEW_MESSAGE)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return ScreenId.MAP;
        }
        if (!AppState.getBool(StateKeys.FLAG_LOADING)) {
            MapController.navigateToPoint((MapPoint) obj, true);
            return ScreenId.MAP;
        }
        MrimAccount account = (MrimAccount) AppState.getAccount();
        account.profileManager.setMapLocation((MapPoint) obj);
        account.profileManager.sync();
        AppState.setInt(StateKeys.FLAG_LOADING, 0);
        return ScreenId.PROFILE_EDIT;
    }

    /* renamed from: f */
    public static final void requestNearbyPeople() {
        ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(PackedStringKeys.URL_GEO_LAT1).writeRawString(pixelToLatitude((int) pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(PackedStringKeys.PARAM_LON1).writeRawString(pixelToLongitude((int) pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(PackedStringKeys.PARAM_LAT2).writeRawString(pixelToLatitude((int) pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(PackedStringKeys.PARAM_LON2).writeRawString(pixelToLongitude((int) pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(PackedStringKeys.PARAM_QUANTITY_DENSITY);
        long jM692d = SoftFloat.multiply(4612811918334230528L, SoftFloat.longToFloat(((MapRenderer.viewportHeight / 128) + 2) * ((MapRenderer.viewportWidth / 128) + 2)));
        int iM586d = AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
        long j = MapRenderer.currentPixelX;
        int i = MapRenderer.viewportWidth / 2;
        long jM318a = pixelToCoord((int) (j + i), iM586d) - pixelToCoord((int) (MapRenderer.currentPixelX - i), iM586d);
        long j2 = MapRenderer.currentPixelY;
        int i2 = MapRenderer.viewportHeight / 2;
        ByteBuffer c0043nM1314d = c0043nM1310c.writeRawString(SoftFloat.formatFloat(SoftFloat.divide(jM692d, SoftFloat.longToFloat(jM318a * (pixelToCoord((int) (j2 + i2), iM586d) - pixelToCoord((int) (MapRenderer.currentPixelY - i2), iM586d)))), 100));
        VCard.staticTs1 = (int) pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs2 = (int) pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs3 = (int) pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs4 = (int) pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs5 = AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
        new AsyncTask(AsyncTaskId.PARSE_CONTACTS_SYNC, new Object[]{c0043nM1314d.getStringAndClear(), ResourceManager.integerOf(AppState.getInt(StateKeys.MAP_ZOOM_LEVEL))});
    }

    /* renamed from: b */
    public static final long longitudeToPixel(String str) {
        return SoftFloat.floatToLong(SoftFloat.multiply(4708606483430899712L, SoftFloat.multiply(SoftFloat.parseFloat(str), 4580687790476533044L)));
    }

    /* renamed from: c */
    public static final long latitudeToPixel(String str) {
        long jM697a = SoftFloat.parseFloat(str);
        long j = jM697a;
        if (jM697a > 4635963235168681984L) {
            j = 4635963235168681984L;
        }
        if (j < -4587408801686093824L) {
            j = -4587408801686093824L;
        }
        long jM692d = SoftFloat.multiply(j, 4580687790476533044L);
        long jM692d2 = SoftFloat.multiply(4590560114707566468L, SoftFloat.sin(jM692d));
        return SoftFloat.floatToLong(SoftFloat.subtract(0L, SoftFloat.multiply(4708606483430899712L, SoftFloat.log(SoftFloat.divide(SoftFloat.cos(SoftFloat.divide(SoftFloat.subtract(4609753056924675352L, jM692d), 4611686018427387904L)), SoftFloat.pow(SoftFloat.divide(SoftFloat.subtract(4607182418800017408L, jM692d2), SoftFloat.add(4607182418800017408L, jM692d2)), 4586056515080195972L))))));
    }

    /* renamed from: a */
    public static final String pixelToLongitude(long j) {
        return SoftFloat.formatFloat(SoftFloat.divide(SoftFloat.divide(SoftFloat.longToFloat(j), 4708606483430899712L), 4580687790476533044L), 9);
    }

    /* renamed from: b */
    public static final String pixelToLatitude(long j) {
        long jM703f = SoftFloat.exp(SoftFloat.divide(SoftFloat.negate(SoftFloat.longToFloat(j)), 4708606483430899712L));
        long jM693e = SoftFloat.divide(4590560114707566468L, 4611686018427387904L);
        long jM691c = SoftFloat.subtract(4609753056924675352L, SoftFloat.multiply(SoftFloat.atan(jM703f), 4611686018427387904L));
        int i = 15;
        long jM691c2 = 4591870180066957722L;
        while (true) {
            i--;
            if (i <= 0 || SoftFloat.compare(jM691c2 & Long.MAX_VALUE, 4502148214488346440L) <= 0) {
                break;
            }
            long jM692d = SoftFloat.multiply(4590560114707566468L, SoftFloat.sin(jM691c));
            jM691c2 = SoftFloat.subtract(SoftFloat.subtract(4609753056924675352L, SoftFloat.multiply(SoftFloat.atan(SoftFloat.multiply(jM703f, SoftFloat.pow(SoftFloat.divide(SoftFloat.subtract(4607182418800017408L, jM692d), SoftFloat.add(4607182418800017408L, jM692d)), jM693e))), 4611686018427387904L)), jM691c);
            jM691c = SoftFloat.add(jM691c, jM691c2);
        }
        return SoftFloat.formatFloat(SoftFloat.divide(jM691c, 4580687790476533044L), 9);
    }

    /* renamed from: e */
    private static String extractLatitude(String str) {
        try {
            return StringUtils.prefix(str, Utils.removeChar(str, ' ').indexOf(44));
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: f */
    private static String extractLongitude(String str) {
        try {
            return StringUtils.suffix(str, Utils.removeChar(str, ' ').indexOf(44) + 1);
        } catch (Throwable unused) {
            return null;
        }
    }
}
