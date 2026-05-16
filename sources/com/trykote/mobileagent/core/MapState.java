package com.trykote.mobileagent.core;

import com.trykote.mobileagent.key.MapKeys;

import javax.microedition.lcdui.Image;
import java.util.Vector;

/**
 * Typed facade for map-related state in AppState.
 * Delegates to AppState — zero runtime overhead.
 */
public final class MapState extends AppState {

    public static final MapState INSTANCE = new MapState();
    private MapState() {}

    protected String storeName() { return STORE_MAP; }
    protected int deltaStart() { return RANGE_MAP_START; }
    protected int deltaEnd() { return RANGE_MAP_END; }

    // --- Map coordinates (stored as long = 2 ints) ---

    public static long getLongitude() {
        return getLong(MapKeys.MAP_LONGITUDE);
    }

    public static void setLongitude(long value) {
        setLong(MapKeys.MAP_LONGITUDE, value);
    }

    public static long getLatitude() {
        return getLong(MapKeys.MAP_LATITUDE);
    }

    public static void setLatitude(long value) {
        setLong(MapKeys.MAP_LATITUDE, value);
    }

    public static long getSavedLongitude() {
        return getLong(MapKeys.MAP_SAVED_LONGITUDE);
    }

    public static void setSavedLongitude(long value) {
        setLong(MapKeys.MAP_SAVED_LONGITUDE, value);
    }

    public static long getSavedLatitude() {
        return getLong(MapKeys.MAP_SAVED_LATITUDE);
    }

    public static void setSavedLatitude(long value) {
        setLong(MapKeys.MAP_SAVED_LATITUDE, value);
    }

    public static long getScrollLon() {
        return getLong(MapKeys.MAP_SCROLL_LON);
    }

    public static void setScrollLon(long value) {
        setLong(MapKeys.MAP_SCROLL_LON, value);
    }

    public static long getScrollLat() {
        return getLong(MapKeys.MAP_SCROLL_LAT);
    }

    public static void setScrollLat(long value) {
        setLong(MapKeys.MAP_SCROLL_LAT, value);
    }

    // --- Map config ---

    public static int getZoomLevel() {
        return getInt(MapKeys.MAP_ZOOM_LEVEL);
    }

    public static void setZoomLevel(int level) {
        setInt(MapKeys.MAP_ZOOM_LEVEL, level);
    }

    public static boolean isGpsEnabled() {
        return getBool(MapKeys.MAP_GPS_ENABLED);
    }

    public static void setGpsEnabled(boolean enabled) {
        setInt(MapKeys.MAP_GPS_ENABLED, enabled ? 1 : 0);
    }

    public static boolean isInitialized() {
        return getBool(MapKeys.MAP_INITIALIZED);
    }

    public static void setInitialized(boolean initialized) {
        setInt(MapKeys.MAP_INITIALIZED, initialized ? 1 : 0);
    }

    public static String getResourceUrl() {
        return getString(MapKeys.MAP_RESOURCE_URL);
    }

    public static void setResourceUrl(Object url) {
        setObject(MapKeys.MAP_RESOURCE_URL, url);
    }

    public static int getViewportWidth() {
        return getInt(MapKeys.MAP_VIEWPORT_WIDTH);
    }

    public static void setViewportWidth(int width) {
        setInt(MapKeys.MAP_VIEWPORT_WIDTH, width);
    }

    public static int getViewportHeight() {
        return getInt(MapKeys.MAP_VIEWPORT_HEIGHT);
    }

    public static void setViewportHeight(int height) {
        setInt(MapKeys.MAP_VIEWPORT_HEIGHT, height);
    }

    // --- GPS flags ---

    public static boolean isGpsActive() {
        return getBool(MapKeys.FLAG_GPS_ACTIVE);
    }

    public static void setGpsActive(boolean active) {
        setBool(MapKeys.FLAG_GPS_ACTIVE, active);
    }

    public static boolean isGpsNoMap() {
        return getBool(MapKeys.FLAG_GPS_NO_MAP);
    }

    public static void setGpsNoMap(boolean value) {
        setBool(MapKeys.FLAG_GPS_NO_MAP, value);
    }

    public static boolean isGpsWithMap() {
        return getBool(MapKeys.FLAG_GPS_WITH_MAP);
    }

    public static void setGpsWithMap(boolean value) {
        setBool(MapKeys.FLAG_GPS_WITH_MAP, value);
    }

    // --- Map view flags ---

    public static boolean isMapViewActive() {
        return getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE);
    }

    public static void setMapViewActive(boolean active) {
        setBool(MapKeys.FLAG_MAP_VIEW_ACTIVE, active);
    }

    public static boolean isMapScreenVisible() {
        return getBool(MapKeys.FLAG_MAP_SCREEN_VISIBLE);
    }

    public static void setMapScreenVisible(boolean visible) {
        setBool(MapKeys.FLAG_MAP_SCREEN_VISIBLE, visible);
    }

    public static boolean isMapOverlayActive() {
        return getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE);
    }

    public static void setMapOverlayActive(boolean active) {
        setBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE, active);
    }

    public static boolean isMapModeActive() {
        return getBool(MapKeys.FLAG_MAP_MODE_ACTIVE);
    }

    public static void setMapModeActive(boolean active) {
        setBool(MapKeys.FLAG_MAP_MODE_ACTIVE, active);
    }

    public static boolean isMapLoading() {
        return getBool(MapKeys.FLAG_MAP_LOADING);
    }

    public static void setMapLoading(boolean loading) {
        setBool(MapKeys.FLAG_MAP_LOADING, loading);
    }

    public static boolean isMapDataLoaded() {
        return getBool(MapKeys.FLAG_MAP_DATA_LOADED);
    }

    public static void setMapDataLoaded(boolean loaded) {
        setBool(MapKeys.FLAG_MAP_DATA_LOADED, loaded);
    }

    // --- Search flags ---

    public static boolean isPoiSearch() {
        return getBool(MapKeys.FLAG_MAP_POI_SEARCH);
    }

    public static void setPoiSearch(boolean active) {
        setBool(MapKeys.FLAG_MAP_POI_SEARCH, active);
    }

    public static boolean isRouteSearch() {
        return getBool(MapKeys.FLAG_MAP_ROUTE_SEARCH);
    }

    public static void setRouteSearch(boolean active) {
        setBool(MapKeys.FLAG_MAP_ROUTE_SEARCH, active);
    }

    // --- Scroll state ---

    public static boolean isScrolling() {
        return getBool(MapKeys.FLAG_MAP_SCROLLING);
    }

    public static void setScrolling(boolean scrolling) {
        setBool(MapKeys.FLAG_MAP_SCROLLING, scrolling);
    }

    public static int getScrollDirection() {
        return getInt(MapKeys.INT_MAP_SCROLL_DIRECTION);
    }

    public static void setScrollDirection(int direction) {
        setInt(MapKeys.INT_MAP_SCROLL_DIRECTION, direction);
    }

    public static long getScrollTimestamp() {
        return getLong(MapKeys.TIMESTAMP_MAP_SCROLL);
    }

    public static void setScrollTimestamp(long timestamp) {
        setLong(MapKeys.TIMESTAMP_MAP_SCROLL, timestamp);
    }

    // --- Map points ---

    public static Vector getMapPoints() {
        return getVector(MapKeys.VEC_MAP_POINTS);
    }

    public static void setMapPoints(Vector points) {
        setObject(MapKeys.VEC_MAP_POINTS, points);
    }

    public static Object getMapPoint1() {
        return getPoolObject(MapKeys.SLOT_MAP_POINT_1);
    }

    public static void setMapPoint1(Object point) {
        setObject(MapKeys.SLOT_MAP_POINT_1, point);
    }

    public static Object getMapPoint2() {
        return getPoolObject(MapKeys.SLOT_MAP_POINT_2);
    }

    public static void setMapPoint2(Object point) {
        setObject(MapKeys.SLOT_MAP_POINT_2, point);
    }

    // --- Map search ---

    public static String getSearchQuery() {
        return getString(MapKeys.SLOT_MAP_SEARCH_QUERY);
    }

    public static void setSearchQuery(Object query) {
        setObject(MapKeys.SLOT_MAP_SEARCH_QUERY, query);
    }

    public static String getLocationName() {
        return getString(MapKeys.STR_MAP_LOCATION_NAME);
    }

    public static void setLocationName(Object name) {
        setObject(MapKeys.STR_MAP_LOCATION_NAME, name);
    }

    public static String getLocationUrl() {
        return getString(MapKeys.STR_MAP_LOCATION_URL);
    }

    public static void setLocationUrl(Object url) {
        setObject(MapKeys.STR_MAP_LOCATION_URL, url);
    }

    // --- Tooltip ---

    public static String getTooltipText1() {
        return getString(MapKeys.SLOT_TOOLTIP_TEXT_1);
    }

    public static void setTooltipText1(Object text) {
        setObject(MapKeys.SLOT_TOOLTIP_TEXT_1, text);
    }

    public static String getTooltipText2() {
        return getString(MapKeys.SLOT_TOOLTIP_TEXT_2);
    }

    public static void setTooltipText2(Object text) {
        setObject(MapKeys.SLOT_TOOLTIP_TEXT_2, text);
    }

    // --- Geo regions ---

    public static Object getGeoRegion() {
        return getPoolObject(MapKeys.OBJ_GEO_REGION);
    }

    public static void setGeoRegion(Object region) {
        setObject(MapKeys.OBJ_GEO_REGION, region);
    }

    public static Object getGeoRegion2() {
        return getPoolObject(MapKeys.OBJ_GEO_REGION_2);
    }

    public static void setGeoRegion2(Object region) {
        setObject(MapKeys.OBJ_GEO_REGION_2, region);
    }

    public static String getGeoSavedData() {
        return getString(MapKeys.GEO_SAVED_DATA);
    }

    public static void setGeoSavedData(Object data) {
        setObject(MapKeys.GEO_SAVED_DATA, data);
    }

    public static String getGeoConfigUrl() {
        return getString(MapKeys.URL_GEO_CONFIG);
    }

    public static void setGeoConfigUrl(Object url) {
        setObject(MapKeys.URL_GEO_CONFIG, url);
    }

    public static void resetGeoConfigUrl() {
        resetToEmpty(MapKeys.URL_GEO_CONFIG);
    }

    public static void resetGeoSavedData() {
        resetToEmpty(MapKeys.GEO_SAVED_DATA);
    }

    // --- Tile cache ---

    public static Object getTileCache() {
        return getPoolObject(MapKeys.OBJ_TILE_CACHE);
    }

    public static void setTileCache(Object cache) {
        setObject(MapKeys.OBJ_TILE_CACHE, cache);
    }

    public static Object getTileRequestArray() {
        return getPoolObject(MapKeys.OBJ_TILE_REQUEST_ARRAY);
    }

    public static void setTileRequestArray(Object array) {
        setObject(MapKeys.OBJ_TILE_REQUEST_ARRAY, array);
    }

    public static String getTileRequest() {
        return getString(MapKeys.SLOT_MAP_TILE_REQUEST);
    }

    public static Vector getTileRequestVector() {
        return getVector(MapKeys.SLOT_MAP_TILE_REQUEST);
    }

    public static void setTileRequest(Object request) {
        setObject(MapKeys.SLOT_MAP_TILE_REQUEST, request);
    }

    public static Object getTileData() {
        return getPoolObject(MapKeys.SLOT_MAP_TILE_DATA);
    }

    public static void setTileData(Object data) {
        setObject(MapKeys.SLOT_MAP_TILE_DATA, data);
    }

    public static Vector getTileQueue() {
        return getVector(MapKeys.VEC_TILE_QUEUE);
    }

    public static void setTileQueue(Vector queue) {
        setObject(MapKeys.VEC_TILE_QUEUE, queue);
    }

    public static boolean isTilesPending() {
        return getBool(MapKeys.FLAG_MAP_TILES_PENDING);
    }

    public static void setTilesPending(boolean pending) {
        setBool(MapKeys.FLAG_MAP_TILES_PENDING, pending);
    }

    public static boolean isTilesReady() {
        return getBool(MapKeys.FLAG_TILES_READY);
    }

    public static void setTilesReady(boolean ready) {
        setBool(MapKeys.FLAG_TILES_READY, ready);
    }

    public static boolean isTileCacheEnabled() {
        return getBool(MapKeys.FLAG_TILE_CACHE_ENABLED);
    }

    public static void setTileCacheEnabled(boolean enabled) {
        setBool(MapKeys.FLAG_TILE_CACHE_ENABLED, enabled);
    }

    public static int getTileCacheSize() {
        return getInt(MapKeys.INT_TILE_CACHE_SIZE);
    }

    public static void setTileCacheSize(int size) {
        setInt(MapKeys.INT_TILE_CACHE_SIZE, size);
    }

    public static int getCacheHitCount() {
        return getInt(MapKeys.COUNTER_MAP_CACHE_HIT);
    }

    public static int getAndClearCacheHitCount() {
        return getAndClearInt(MapKeys.COUNTER_MAP_CACHE_HIT);
    }

    public static void addCacheHit() {
        addInt(MapKeys.COUNTER_MAP_CACHE_HIT, 1);
    }

    public static int getCacheMissCount() {
        return getInt(MapKeys.COUNTER_MAP_CACHE_MISS);
    }

    public static int getAndClearCacheMissCount() {
        return getAndClearInt(MapKeys.COUNTER_MAP_CACHE_MISS);
    }

    public static void addCacheMiss() {
        addInt(MapKeys.COUNTER_MAP_CACHE_MISS, 1);
    }

    // --- Fonts ---

    public static Object getFont1() {
        return getPoolObject(MapKeys.OBJ_FONT_1);
    }

    public static void setFont1(Object font) {
        setObject(MapKeys.OBJ_FONT_1, font);
    }

    public static Object getFont2() {
        return getPoolObject(MapKeys.OBJ_FONT_2);
    }

    public static void setFont2(Object font) {
        setObject(MapKeys.OBJ_FONT_2, font);
    }

    // --- Menu ---

    public static Object getMenuActions() {
        return getPoolObject(MapKeys.OBJ_MENU_ACTIONS);
    }

    public static void setMenuActions(Object actions) {
        setObject(MapKeys.OBJ_MENU_ACTIONS, actions);
    }

    public static Object getMenuLabels() {
        return getPoolObject(MapKeys.OBJ_MENU_LABELS);
    }

    public static Image getMenuLabelsImage() {
        return getImage(MapKeys.OBJ_MENU_LABELS);
    }

    public static void setMenuLabels(Object labels) {
        setObject(MapKeys.OBJ_MENU_LABELS, labels);
    }

    // --- Misc ---

    public static Vector getMapData() {
        return getVector(MapKeys.SLOT_MAP_DATA);
    }

    public static void setMapData(Object data) {
        setObject(MapKeys.SLOT_MAP_DATA, data);
    }

    public static String getXmppSessionId() {
        return getString(MapKeys.SLOT_XMPP_SESSION_ID);
    }

    public static void setXmppSessionId(Object id) {
        setObject(MapKeys.SLOT_XMPP_SESSION_ID, id);
    }

    public static boolean hasChatItems() {
        return getBool(MapKeys.FLAG_CHAT_HAS_ITEMS);
    }

    public static void setChatHasItems(boolean has) {
        setBool(MapKeys.FLAG_CHAT_HAS_ITEMS, has);
    }

    public static int getScreenBuilderAction() {
        return getInt(MapKeys.INT_SCREEN_BUILDER_ACTION);
    }

    public static void setScreenBuilderAction(int action) {
        setInt(MapKeys.INT_SCREEN_BUILDER_ACTION, action);
    }
}
