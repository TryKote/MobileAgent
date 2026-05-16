package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.VCard;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.SoftFloat;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Image;

/* Extracted from AppController: map coordinate math utilities */
public final class MapUtils {

    // Tile coordinate system
    private static final int MAX_ZOOM_BITS = 17;
    private static final int ZOOM_RANGE_MIN = 8;

    // Projection scale factors
    private static final long SCALE_FACTOR_LOW = 119432;
    private static final long SCALE_FACTOR_HIGH = 1194329;
    private static final long DENOMINATOR_LOW = 100000L;
    private static final long DENOMINATOR_HIGH = 1000000L;

    // Zoom levels for coordinate search
    private static final int ZOOM_IN_REGION = 13;
    private static final int ZOOM_OUT_OF_REGION = 10;

    public static final long getZoomNumerator(int zoomLevel) {
        return (1 << (MAX_ZOOM_BITS - zoomLevel)) * ((zoomLevel < ZOOM_RANGE_MIN || zoomLevel > MAX_ZOOM_BITS) ? SCALE_FACTOR_LOW : SCALE_FACTOR_HIGH);
    }

    public static final long getZoomDenominator(int zoomLevel) {
        return (zoomLevel < ZOOM_RANGE_MIN || zoomLevel > MAX_ZOOM_BITS) ? DENOMINATOR_LOW : DENOMINATOR_HIGH;
    }

    public static long coordToPixel(long coord, int zoomLevel) {
        return (coord * getZoomDenominator(zoomLevel)) / getZoomNumerator(zoomLevel);
    }

    public static final long pixelToCoord(int pixel, int zoomLevel) {
        return (pixel * getZoomNumerator(zoomLevel)) / getZoomDenominator(zoomLevel);
    }

    public static final int computeColor(int x1, int y1, int x2, int y2) {
        return Utils.abs(y1 - y2) + Utils.abs(x1 - x2);
    }

    public static final int handleMapSearch(int action, Object obj) {
        if (action == 6) {
            return handleMapPointAction(obj);
        }
        ScreenManager.processScreenForm();
        String query = Utils.defaultStr(RegistrationState.getSearchQuery());
        if (StringUtils.isEmpty(query)) {
            return NotificationHelper.showError(351);
        }
        boolean isCoordinate = true;
        int separatorScore = 0;
        for (int idx = query.length() - 1; idx >= 0; idx--) {
            char ch = query.charAt(idx);
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
                MapRenderer.setZoom(StringUtils.isInSavedRegion(lon, lat) ? ZOOM_IN_REGION : ZOOM_OUT_OF_REGION);
            } catch (Throwable unused) {
            }
        } else {
            String encodedQuery = Conversation.replaceText(query, 1046, 199350);
            Image mapImage = (Image) MapState.getFont2();
            long currentLat = MapRenderer.currentLat;
            new AsyncTask(AsyncTaskId.FETCH_MAP_POINTS, new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_MAPSSEARCH).writeRawString(Conversation.urlEncodeCyrillic((Object) encodedQuery)).writeCompressed(PackedStringKeys.PARAM_USER_LAT).writeLongAsString(currentLat).writeCompressed(PackedStringKeys.PARAM_USER_LON).writeLongAsString(MapRenderer.currentLon).writeCompressed(PackedStringKeys.PARAM_X_SCREEN).writeIntAsString(mapImage.getWidth()).writeCompressed(PackedStringKeys.PARAM_Y_SCREEN).writeIntAsString(mapImage.getHeight()).getStringAndClear());
        }
        return UIState.isLoading() ? 161 : 6;
    }

    public static final int handleMapPointAction(Object obj) {
        if (UIState.isNewMessage()) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return ScreenId.MAP;
        }
        if (!UIState.isLoading()) {
            MapController.navigateToPoint((MapPoint) obj, true);
            return ScreenId.MAP;
        }
        Account account = AppState.getAccount();
        account.setProfileMapLocation(obj);
        account.syncProfile();
        UIState.setLoading(0);
        return ScreenId.PROFILE_EDIT;
    }

    public static final void requestNearbyPeople() {
        ByteBuffer urlBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_GEO_LAT1).writeRawString(pixelToLatitude((int) pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), MapState.getZoomLevel()))).writeCompressed(PackedStringKeys.PARAM_LON1).writeRawString(pixelToLongitude((int) pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), MapState.getZoomLevel()))).writeCompressed(PackedStringKeys.PARAM_LAT2).writeRawString(pixelToLatitude((int) pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), MapState.getZoomLevel()))).writeCompressed(PackedStringKeys.PARAM_LON2).writeRawString(pixelToLongitude((int) pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), MapState.getZoomLevel()))).writeCompressed(PackedStringKeys.PARAM_QUANTITY_DENSITY);
        long tileCount = SoftFloat.multiply(4612811918334230528L, SoftFloat.longToFloat(((MapRenderer.viewportHeight / 128) + 2) * ((MapRenderer.viewportWidth / 128) + 2)));
        int zoomLevel = MapState.getZoomLevel();
        long centerX = MapRenderer.currentPixelX;
        int halfWidth = MapRenderer.viewportWidth / 2;
        long viewportCoordWidth = pixelToCoord((int) (centerX + halfWidth), zoomLevel) - pixelToCoord((int) (MapRenderer.currentPixelX - halfWidth), zoomLevel);
        long centerY = MapRenderer.currentPixelY;
        int halfHeight = MapRenderer.viewportHeight / 2;
        urlBuf.writeRawString(SoftFloat.formatFloat(SoftFloat.divide(tileCount, SoftFloat.longToFloat(viewportCoordWidth * (pixelToCoord((int) (centerY + halfHeight), zoomLevel) - pixelToCoord((int) (MapRenderer.currentPixelY - halfHeight), zoomLevel)))), 100));
        VCard.staticTs1 = (int) pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), MapState.getZoomLevel());
        VCard.staticTs2 = (int) pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), MapState.getZoomLevel());
        VCard.staticTs3 = (int) pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), MapState.getZoomLevel());
        VCard.staticTs4 = (int) pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), MapState.getZoomLevel());
        VCard.staticTs5 = MapState.getZoomLevel();
        new AsyncTask(AsyncTaskId.PARSE_CONTACTS_SYNC, new Object[]{urlBuf.getStringAndClear(), ObjectPool.integerOf(MapState.getZoomLevel())});
    }

    public static final long longitudeToPixel(String lonStr) {
        return SoftFloat.floatToLong(SoftFloat.multiply(4708606483430899712L, SoftFloat.multiply(SoftFloat.parseFloat(lonStr), 4580687790476533044L)));
    }

    public static final long latitudeToPixel(String latStr) {
        long latitude = SoftFloat.parseFloat(latStr);
        long clampedLat = latitude;
        if (latitude > 4635963235168681984L) {
            clampedLat = 4635963235168681984L;
        }
        if (clampedLat < -4587408801686093824L) {
            clampedLat = -4587408801686093824L;
        }
        long radians = SoftFloat.multiply(clampedLat, 4580687790476533044L);
        long sinValue = SoftFloat.multiply(4590560114707566468L, SoftFloat.sin(radians));
        return SoftFloat.floatToLong(SoftFloat.subtract(0L, SoftFloat.multiply(4708606483430899712L, SoftFloat.log(SoftFloat.divide(SoftFloat.cos(SoftFloat.divide(SoftFloat.subtract(4609753056924675352L, radians), 4611686018427387904L)), SoftFloat.pow(SoftFloat.divide(SoftFloat.subtract(4607182418800017408L, sinValue), SoftFloat.add(4607182418800017408L, sinValue)), 4586056515080195972L))))));
    }

    public static final String pixelToLongitude(long pixel) {
        return SoftFloat.formatFloat(SoftFloat.divide(SoftFloat.divide(SoftFloat.longToFloat(pixel), 4708606483430899712L), 4580687790476533044L), 9);
    }

    public static final String pixelToLatitude(long pixel) {
        long expValue = SoftFloat.exp(SoftFloat.divide(SoftFloat.negate(SoftFloat.longToFloat(pixel)), 4708606483430899712L));
        long halfEcc = SoftFloat.divide(4590560114707566468L, 4611686018427387904L);
        long latitude = SoftFloat.subtract(4609753056924675352L, SoftFloat.multiply(SoftFloat.atan(expValue), 4611686018427387904L));
        long delta = 4591870180066957722L;
        for (int iter = 14; iter > 0 && SoftFloat.compare(delta & Long.MAX_VALUE, 4502148214488346440L) > 0; iter--) {
            long sinLat = SoftFloat.multiply(4590560114707566468L, SoftFloat.sin(latitude));
            delta = SoftFloat.subtract(SoftFloat.subtract(4609753056924675352L, SoftFloat.multiply(SoftFloat.atan(SoftFloat.multiply(expValue, SoftFloat.pow(SoftFloat.divide(SoftFloat.subtract(4607182418800017408L, sinLat), SoftFloat.add(4607182418800017408L, sinLat)), halfEcc))), 4611686018427387904L)), latitude);
            latitude = SoftFloat.add(latitude, delta);
        }
        return SoftFloat.formatFloat(SoftFloat.divide(latitude, 4580687790476533044L), 9);
    }

    private static String extractLatitude(String coordStr) {
        try {
            return StringUtils.prefix(coordStr, Utils.removeChar(coordStr, ' ').indexOf(44));
        } catch (Throwable unused) {
            return null;
        }
    }

    private static String extractLongitude(String coordStr) {
        try {
            return StringUtils.suffix(coordStr, Utils.removeChar(coordStr, ' ').indexOf(44) + 1);
        } catch (Throwable unused) {
            return null;
        }
    }

    public static String buildTileRequestUrl(long pixelLon, long pixelLat, int zoomLevel, String query) {
        String encodedQuery;
        ByteBuffer urlBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MAPS_MAIL_RU).writeUInt(1031283503);
        String longitude = pixelToLongitude(pixelLon);
        ByteBuffer urlBuf2 = urlBuf.writeRawString(longitude).writeUInt(4028710);
        String latitude = pixelToLatitude(pixelLat);
        ByteBuffer urlBuffer = urlBuf2.writeRawString(latitude).writeUInt(4028966).writeIntAsString(zoomLevel).writeCompressed(PackedStringKeys.PARAM_MAP_FULLSCREEN);
        if (query != null) {
            ByteBuffer urlBuf3 = urlBuffer.writeUInt(1031302438).writeRawString(longitude).writeUInt(1031367974).writeRawString(latitude).writeUInt(1031040294);
            if (StringUtils.isEmpty(query)) {
                encodedQuery = ObjectPool.unpackChars(1094795585);
            } else {
                ByteBuffer buffer = new ByteBuffer();
                int length = query.length();
                for (int i = 0; i < length; i++) {
                    int ch = query.charAt(i) & 0xFFFF;
                    if (ch < 128) {
                        buffer.writeByte(ch);
                    } else if (ch < 2048) {
                        buffer.writeByte(192 + (ch >> 6)).writeByte(128 + (ch & 63));
                    } else {
                        buffer.writeByte(224 + (ch >> 12)).writeByte(128 + ((ch >> 6) & 63)).writeByte(128 + (ch & 63));
                    }
                }
                encodedQuery = Conversation.replaceText(Conversation.replaceText(buffer.toBase64(), 65547, 200765), 65552, 200768);
            }
            urlBuf3.writeRawString(encodedQuery);
        }
        return urlBuffer.getStringAndClear();
    }
}
