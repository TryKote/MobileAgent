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
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class MapController {

    // Default map center (Moscow)
    private static final long DEFAULT_LONGITUDE = 4178628L;
    private static final long DEFAULT_LATITUDE = 7482960L;

    // Tile dimensions in pixels
    private static final int TILE_SIZE = 128;

    // Checkerboard pattern color (light gray)
    private static final int COLOR_CHECKERBOARD = 13158600;

    // Checkerboard pattern step
    private static final int CHECKER_STEP = 2;
    private static final int CHECKER_STRIDE = 4;

    // Loading icon parameters
    private static final int LOADING_ICON_ID = 312;
    private static final int LOADING_ICON_OFFSET = 56;

    // Context menu action bitmasks
    private static final int ACTION_BASE = 3072;
    private static final int ACTION_WITH_MRIM = 11264;
    private static final int ACTION_OWN_CONTACT = 4384;
    private static final int ACTION_SAVE_LOCATION = 128;
    private static final int ACTION_CONVERSATION = 2064;
    private static final int ACTION_PHONE_CONTACT = 64;
    private static final int ACTION_SEARCH_RESULT = 4640;
    private static final int ACTION_MASK_REMOVE_LIST = -1025;
    private static final int ACTION_FRIEND_FLAG = 3;

    // Context menu flags base slot
    private static final int MENU_FLAGS_BASE_SLOT = 1424;
    private static final int MENU_FLAGS_SCAN_LIMIT = 16384;

    // Cached tile invalidation range
    private static final int TILE_CACHE_START_OFFSET = 18;
    private static final int TILE_CACHE_COUNT = 10;

    // Zoom levels for search results
    private static final int ZOOM_HOME_REGION = 3;
    private static final int ZOOM_SEARCH_RESULT = 11;

    public static boolean mapInitialized;

    private static ListView mapScreen;

    public static ListItem activeMapItem;

    public static ListItem mapContextItem;

    public static MapPoint pendingMapPoint;

    public static final void showMapScreen() {
        initMapState();
        Storage.state().setInt(SessionKeys.INT_CONNECTION_STATE, 6);
        ListView screen = ScreenManager.createScreen(ScreenDef.MAP_VIEW);
        mapScreen = screen;
        setMapSoftKeys(screen);
        ScreenManager.pushScreen(screen);
        TabBar.ensureSearchTab();
        TabBar.findTab(TabBar.TYPE_SEARCH, (Account) null);
        TabBar.scrollEnabled = Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE);
        if (Storage.state().getBool(ContactKeys.FLAG_REFRESH_CONTACTS)) {
            return;
        }
        ScreenBuilder.openScreen(ScreenId.EDIT_SCREEN);
    }

    public static final void updateMapSoftKeys() {
        if (Storage.state().getBool(MapKeys.FLAG_MAP_TILES_PENDING)) {
            if (Storage.state().getBool(MapKeys.FLAG_MAP_SCREEN_VISIBLE) || mapScreen == null) {
                return;
            }
            mapScreen.setSoftKeys(Storage.resources().getString(StringResKeys.STR_SOFTKEY_MAP), Storage.resources().getString(StringResKeys.STR_SOFTKEY_CLOSE), 167, 4, 167);
            Storage.state().setInt(MapKeys.FLAG_MAP_SCREEN_VISIBLE, 1);
            return;
        }
        if (!Storage.state().getBool(MapKeys.FLAG_MAP_SCREEN_VISIBLE) || mapScreen == null) {
            return;
        }
        setMapSoftKeys(mapScreen);
        Storage.state().setInt(MapKeys.FLAG_MAP_SCREEN_VISIBLE, 0);
    }

    private static final void initMapState() {
        if (mapInitialized) {
            return;
        }
        mapInitialized = true;
        int contentHeight = ScreenManager.createScreen(ScreenDef.MAP_VIEW).contentHeight;
        Storage.state().setLong(MapKeys.MAP_SCROLL_LON, DEFAULT_LONGITUDE);
        Storage.state().setLong(MapKeys.MAP_SCROLL_LAT, DEFAULT_LATITUDE);
        Storage.state().setObject(ContactKeys.VEC_CONTACT_GROUPS, XmppContactGroup.loadMapPoints(225));
        Storage.state().setObject(UIKeys.VEC_PHOTO_QUEUE, XmppContactGroup.loadMapPoints(226));
        Storage.state().setInt(MapKeys.MAP_VIEWPORT_WIDTH, Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH));
        Storage.state().setInt(MapKeys.MAP_VIEWPORT_HEIGHT, contentHeight);
        Storage.state().setLong(MapKeys.MAP_SAVED_LONGITUDE, Storage.state().getLong(MapKeys.MAP_LONGITUDE));
        Storage.state().setLong(MapKeys.MAP_SAVED_LATITUDE, Storage.state().getLong(MapKeys.MAP_LATITUDE));
        MapRenderer.viewportWidth = Storage.state().getInt(MapKeys.MAP_VIEWPORT_WIDTH);
        MapRenderer.viewportHeight = Storage.state().getInt(MapKeys.MAP_VIEWPORT_HEIGHT);
        MapRenderer.currentLat = Storage.state().getLong(MapKeys.MAP_SAVED_LATITUDE);
        MapRenderer.currentLon = Storage.state().getLong(MapKeys.MAP_SAVED_LONGITUDE);
        int zoomLevel = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        MapRenderer.currentPixelX = MapUtils.coordToPixel(MapRenderer.currentLon, zoomLevel);
        MapRenderer.currentPixelY = MapUtils.coordToPixel(MapRenderer.currentLat, zoomLevel);
        Storage.state().setObject(MapKeys.OBJ_FONT_2, Image.createImage(MapRenderer.viewportWidth, MapRenderer.viewportHeight));
        StringUtils.initTileCache();
        Storage.state().setObject(ChatKeys.VEC_TILE_REQUEST_QUEUE, ObjectPool.newVector());
        Storage.state().setObject(RuntimeKeys.OBJ_SEARCH_PARAMS_1, ObjectPool.newVector());
        Object[] urlComponents = ApiClient.getUrlComponents(Storage.emptyStr);
        Storage.state().setObject(MapKeys.OBJ_TILE_REQUEST_ARRAY, urlComponents);
        XmppContactGroup.addContactInfoToQueue(urlComponents);
        Image checkerImage = Image.createImage(TILE_SIZE, TILE_SIZE);
        Graphics graphics = checkerImage.getGraphics();
        int rowOffset = 0;
        graphics.setColor(COLOR_CHECKERBOARD);
        for (int col = 0; col < TILE_SIZE; col += CHECKER_STEP) {
            for (int row = rowOffset; row < TILE_SIZE; row += CHECKER_STRIDE) {
                graphics.fillRect(col, row, CHECKER_STEP, CHECKER_STEP);
            }
            rowOffset ^= CHECKER_STEP;
        }
        new GraphicsContext(graphics).drawIcon(LOADING_ICON_ID, LOADING_ICON_OFFSET, LOADING_ICON_OFFSET);
        Storage.state().setObject(MapKeys.OBJ_MENU_LABELS, checkerImage);
        Storage.state().setObject(RuntimeKeys.OBJ_SEARCH_PARAMS_2, ObjectPool.newVector());
        new AsyncTask(AsyncTaskId.TILE_LOADER);
        MapRenderer.syncLock = new Object();
        StringUtils.initGeoRegions();
        MapRenderer.invalidate();
        MmpContact.routeRegions = ObjectPool.newVector();
        MmpContact.routePoints = ObjectPool.newVector();
        MmpContact.nearestPoints = ObjectPool.newVector();
        MmpContact.lastTokenPair = new long[2];
        MmpContact.currentTokenPair = new long[2];
        MapRenderer.animationSteps = ObjectPool.newVector();
        if (Storage.state().getBool(MapKeys.FLAG_GPS_ACTIVE)) {
            XmppContactGroup.stopMapAnimation(Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE));
        }
        Storage.state().setObject(MapKeys.SLOT_MAP_DATA, ObjectPool.newVector());
        MapRenderer.needsRedraw = true;
        Storage.state().setLong(MapKeys.TIMESTAMP_MAP_SCROLL, System.currentTimeMillis() - 90);
        new AsyncTask(AsyncTaskId.FETCH_GEO_CONFIG);
        ServiceRegistry.loadSavedData();
    }

    private static final void setMapSoftKeys(ListView screen) {
        screen.setSoftKeys(Storage.resources().getString(StringResKeys.STR_SOFTKEY_MENU), Storage.state().getString(Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE) ? 1050 : 328), 20, 0, 0);
    }

    public static final void toggleMapControls(ListView screen) {
        if (Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            return;
        }
        toggleScrollMode();
        setMapSoftKeys(screen);
    }

    public static final int handleMapBack(ListView screen) {
        MrimAccount mrimAccount;
        if (Storage.state().getBool(MapKeys.FLAG_MAP_TILES_PENDING)) {
            ((MrimAccount) Storage.state().getAccount()).isHighlighted = false;
            MapRenderer.needsRedraw = true;
            toggleScrollMode();
            return 0;
        }
        if (Storage.state().getBool(MapKeys.FLAG_MAP_LOADING) && (mrimAccount = (MrimAccount) Storage.state().getAccount()) != null) {
            mrimAccount.deselect();
        }
        toggleScrollMode();
        setMapSoftKeys(screen);
        return 0;
    }

    public static final void handleMapSwitch(ListView screen) {
        if (Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            Storage.state().setInt(MapKeys.INT_MAP_SCROLL_DIRECTION, 3);
        } else {
            toggleMapControls(screen);
        }
    }

    public static final void toggleScrollMode() {
        boolean overlayActive = !Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE);
        Storage.state().setBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE, overlayActive);
        if (!overlayActive) {
            Storage.state().setInt(MapKeys.FLAG_MAP_LOADING, 0);
        }
        TabBar.scrollEnabled = overlayActive;
    }

    public static final void navigateToPoint(MapPoint mapPoint, boolean addToHistory) {
        initMapState();
        if (addToHistory) {
            XmppContactGroup.addMapPointIfNew(Storage.state().getVector(ContactKeys.VEC_CONTACT_GROUPS), mapPoint, 0, 5);
            XmppContactGroup.saveMapPoints(Storage.state().getVector(ContactKeys.VEC_CONTACT_GROUPS), 225);
        }
        MapRenderer.selectedMapPoint = mapPoint;
        MapRenderer.invalidate();
        MapRenderer.setPosition(MapRenderer.selectedMapPoint.longitude, MapRenderer.selectedMapPoint.latitude);
        MapRenderer.setZoom(MapRenderer.selectedMapPoint.zoomLevel);
        MapRenderer.selectedMapPoint.markActive();
        MapRenderer.resetInteraction();
    }

    public static final int showMapSearchResults() {
        Vector searchResults = Storage.state().getVector(ChatKeys.VEC_MESSAGE_LIST);
        if (searchResults != null) {
            Storage.state().clearIndex(ChatKeys.VEC_MESSAGE_LIST);
        }
        if (searchResults == null) {
            return 0;
        }
        AppController.needsRepaint = true;
        int size = searchResults.size();
        if (size == 0) {
            return NotificationHelper.showError(327);
        }
        ListView resultScreen = ScreenManager.createScreen(ScreenDef.MAP_OVERLAY);
        for (int i = 0; i < size; i++) {
            MapPoint mapPoint = (MapPoint) searchResults.elementAt(i);
            resultScreen.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
        }
        ScreenManager.showScreen(resultScreen);
        return 0;
    }

    public static final Enumeration getRouteElements() {
        return Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE).elements();
    }

    public static final boolean hasRoutePoints() {
        return Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE).size() > 0;
    }

    public static final void removeRoutePoint(MapPoint mapPoint) {
        Vector routePoints = Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE);
        routePoints.removeElement(mapPoint);
        XmppContactGroup.saveMapPoints(routePoints, 226);
    }

    public static final void setRouteStart() {
        long lon;
        long lat;
        if (MapRenderer.hasRouteEndpoints()) {
            MmpContact.clearRouteProgress();
        }
        ListItem tooltip = MapRenderer.tooltipItem;
        if (tooltip == null || !tooltip.isSelected()) {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        } else {
            lon = tooltip.getWidth();
            lat = tooltip.getBaseHeight();
            tooltip.select();
        }
        MmpContact.setFirstToken(lon, lat);
        MapRenderer.needsRedraw = true;
        if (MapRenderer.hasRouteEndpoints()) {
            Conversation.loadContacts();
        }
    }

    public static final void setRouteEnd() {
        long lon;
        long lat;
        if (MapRenderer.hasRouteEndpoints()) {
            MmpContact.clearRouteProgress();
        }
        ListItem tooltip = MapRenderer.tooltipItem;
        if (tooltip == null || !tooltip.isSelected()) {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        } else {
            lon = tooltip.getWidth();
            lat = tooltip.getBaseHeight();
            tooltip.select();
        }
        MmpContact.setSecondToken(lon, lat);
        MapRenderer.needsRedraw = true;
        if (MapRenderer.hasRouteEndpoints()) {
            Conversation.loadContacts();
        }
    }

    public static final void selectMapItem(ListItem item) {
        if (item.isSelected()) {
            showMapView();
            MapRenderer.setPosition(item.getWidth(), item.getBaseHeight());
            MapRenderer.setZoom(item.getCommandCount());
            activeMapItem = item;
        }
    }

    public static final void showMapView() {
        initMapState();
        Storage.state().setInt(MapKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
        MapRenderer.invalidate();
    }

    public static final void showMapContextMenu() {
        ListItem item;
        int actionFlags = ACTION_BASE;
        if (AccountManager.getOnlineMrimAccounts().size() > 0) {
            actionFlags = ACTION_WITH_MRIM;
        }
        if (mapContextItem != null) {
            item = mapContextItem;
        } else {
            item = MapRenderer.tooltipItem;
            mapContextItem = item;
        }
        if (item != null && item.isSelected()) {
            switch (item.getHeight()) {
                case 3:
                    actionFlags = ACTION_OWN_CONTACT;
                    break;
                case 4:
                    actionFlags |= ACTION_FRIEND_FLAG;
                    break;
                case 5:
                    actionFlags = ACTION_SAVE_LOCATION;
                    break;
                case 6:
                    actionFlags = ACTION_CONVERSATION;
                    break;
                case 7:
                    actionFlags = ACTION_PHONE_CONTACT;
                    break;
                case 8:
                    actionFlags = ACTION_SEARCH_RESULT;
                    break;
                case 10:
                    actionFlags &= ACTION_MASK_REMOVE_LIST;
                    break;
            }
        }
        if (!Storage.state().getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE)) {
            actionFlags &= ACTION_MASK_REMOVE_LIST;
        }
        int slotIndex = MENU_FLAGS_BASE_SLOT;
        for (int flagBit = 1; flagBit < MENU_FLAGS_SCAN_LIMIT; flagBit <<= 1) {
            Storage.state().setInt(slotIndex, actionFlags & flagBit);
            slotIndex++;
        }
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.XMPP_MAP_CONTEXT));
    }

    public static final int handleMapAction(int actionId) {
        long lon;
        long lat;
        ListItem item = mapContextItem;
        int itemType = item == null ? 0 : item.getHeight();
        switch (actionId) {
            case 0:
                Storage.state().setCurrentEntity(item);
                ScreenBuilder.onScreenClosed();
                return ScreenId.STATUS_INPUT;
            case 1:
                if (itemType == 8) {
                    Storage.state().setInt(RuntimeKeys.INT_ASYNC_TASK_ID, 0);
                    AppController.openUserProfile((MrimAccount) null, ((UserSearchResult) item).userId);
                } else {
                    Storage.state().setCurrentEntity(mapContextItem);
                }
                ScreenBuilder.onScreenClosed();
                return ScreenId.USER_PROFILE;
            case 2:
                if (itemType == 3) {
                    Storage.state().setCurrentEntity(mapContextItem);
                    ScreenBuilder.onScreenClosed();
                    return ScreenId.CONTACT_DELETE;
                }
                Storage.state().setCurrentEntity((Object) null);
                Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
                if (onlineAccounts == null || onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) item).userId, 1));
                ScreenBuilder.onScreenClosed();
                return ScreenId.CONTACT_DELETE;
            case 3:
                Vector onlineAccounts2 = AccountManager.getOnlineMrimAccounts();
                if (onlineAccounts2 == null || onlineAccounts2.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts2.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) item).userId, 2));
                ScreenBuilder.onScreenClosed();
                return ScreenId.MAP;
            case 4:
                ScreenBuilder.onScreenClosed();
                Storage.state().setObject(UIKeys.SLOT_TEMP_OBJECT_1, (Conversation) item);
                return ScreenId.FORM_LIST;
            case 5:
                ContactListManager.dialPhoneUrl(VCard.formatPhoneContactUrl((PhoneContact) item, 0), (PhoneContact) item, 0);
                return ScreenId.CLOSE;
            case 6:
                Storage.state().setAccount(item);
                ScreenBuilder.onScreenClosed();
                return ScreenId.MAILBOX_OPTIONS;
            case 7:
                Storage.state().setAccount(item);
                ScreenBuilder.onScreenClosed();
                return ScreenId.EXT_SETTINGS;
            case 8:
            case 9:
            default:
                return ScreenId.MAP;
            case 10:
                if (MapRenderer.hasRouteEndpoints()) {
                    MmpContact.clearRouteProgress();
                }
                ListItem tooltip = MapRenderer.tooltipItem;
                if (tooltip == null || !tooltip.isSelected()) {
                    lon = MapRenderer.currentLon;
                    lat = MapRenderer.currentLat;
                } else {
                    lon = tooltip.getWidth();
                    lat = tooltip.getBaseHeight();
                    tooltip.select();
                }
                int[] coords = {(int) lon, (int) lat};
                MmpContact.routePoints.addElement(coords);
                MmpContact.nearestPoints.addElement(new Object[]{null, coords});
                MapRenderer.needsRedraw = true;
                if (!MapRenderer.hasRouteEndpoints()) {
                    return ScreenId.MAP;
                }
                Conversation.loadContacts();
                return ScreenId.MAP;
            case 11:
                if (MapRenderer.hasRouteEndpoints()) {
                    MmpContact.clearRouteProgress();
                }
                if (MmpContact.mapDataCache != null) {
                    MmpContact.routePoints.removeElement((int[]) MmpContact.mapDataCache[1]);
                    MmpContact.nearestPoints.removeElement(MmpContact.mapDataCache);
                }
                Storage.state().setInt(UIKeys.FLAG_ROUTE_POINT_HIDDEN, 0);
                Storage.state().setBool(UIKeys.FLAG_ROUTE_POINT_VISIBLE, Storage.state().getBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE));
                MapRenderer.needsRedraw = true;
                if (!MapRenderer.hasRouteEndpoints()) {
                    return ScreenId.MAP;
                }
                Conversation.loadContacts();
                return ScreenId.MAP;
            case 12:
                MmpContact.clearLocationData();
                MapRenderer.needsRedraw = true;
                return ScreenId.MAP;
            case 13:
                ListItem activeTooltip = MapRenderer.tooltipItem;
                if (activeTooltip != null && activeTooltip.isSelected()) {
                    activeTooltip.select();
                }
                MapRenderer.needsRedraw = true;
                return ScreenId.MAP;
            case 14:
                setRouteStart();
                if (MmpContact.hasSecondToken()) {
                    return ScreenId.MAP;
                }
                Storage.state().setInt(MapKeys.FLAG_MAP_MODE_ACTIVE, 1);
                return ScreenId.MAP_SEARCH;
            case 15:
                setRouteEnd();
                if (MmpContact.hasFirstToken()) {
                    return ScreenId.MAP;
                }
                Storage.state().setInt(MapKeys.FLAG_MAP_MODE_ACTIVE, 0);
                return ScreenId.MAP_SEARCH;
            case 16:
                return ScreenId.WIFI_ACCOUNT_LIST;
            case 17:
                return ScreenId.SAVE_LOCATION;
            case 18:
                ScreenBuilder.onScreenClosed();
                Vector onlineAccounts3 = AccountManager.getOnlineMrimAccounts();
                if (onlineAccounts3.size() > 1) {
                    return ScreenId.MRIM_ACCOUNT_SELECT;
                }
                Storage.state().setAccount(onlineAccounts3.elementAt(0));
                return ScreenId.INVITE_ALERT;
            case 19:
                return ScreenId.MAP_TOOLTIP;
            case 20:
                removeRoutePoint((MapPoint) mapContextItem);
                return ScreenId.MAP;
            case 21:
                Conversation.incrementZoom();
                return ScreenId.MAP;
            case 22:
                Conversation.decrementZoom();
                return ScreenId.MAP;
        }
    }

    public static final void applyViewMode(boolean showMap, boolean showList, boolean shouldInvalidate) {
        Storage.state().setBool(MapKeys.FLAG_MAP_VIEW_ACTIVE, showMap);
        Storage.state().setBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE, showList);
        if (!shouldInvalidate || !MapController.mapInitialized) {
            return;
        }
        for (int cacheIdx = TILE_CACHE_COUNT; cacheIdx >= 0; cacheIdx--) {
            XmppContactGroup.invalidateCachedImage(cacheIdx + TILE_CACHE_START_OFFSET);
        }
        MmpContact.clearLocationData();
        StringUtils.initTileCache();
        ServiceRegistry.clearPhotoCache();
        MapRenderer.needsRedraw = true;
    }

    public static final int handleMapMenuOption(int optionId) {
        int activeCount = AccountManager.getActiveAccountCount();
        if (optionId == 15) {
            if (activeCount == 0) {
                return NotificationHelper.showError(551);
            }
            if (activeCount == 1) {
                AccountManager.toggleAllConnections();
                return ScreenId.CONTACT_LIST;
            }
        } else {
            if (optionId == 3) {
                Vector accounts = AccountManager.copyAllAccounts();
                int result = AccountManager.showAccountList(accounts, 3, true);
                if (result != 39) {
                    return result;
                }
                accounts.insertElementAt(accounts, 0);
                return ScreenId.ACCOUNT_CHECKBOX_LIST;
            }
            if (optionId == 152) {
                return AccountManager.showAccountList(AccountManager.getMrimAccountList(), 152, true);
            }
        }
        if (optionId == 10) {
            return ScreenManager.getIconOffset();
        }
        if (optionId != 6) {
            return 0;
        }
        Storage.state().setInt(MapKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
        return 0;
    }

    public static final int handleMapPointAction(Object obj) {
        if (Storage.state().getBool(UIKeys.FLAG_NEW_MESSAGE)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 0;
        }
        if (!Storage.state().getBool(UIKeys.FLAG_LOADING)) {
            MapController.navigateToPoint((MapPoint) obj, true);
            return 0;
        }
        MapPoint mapPoint = (MapPoint) obj;
        ((MrimAccount) Storage.state().getAccount()).profileManager.setMapLocation(mapPoint);
        XmppContactGroup.addMapPointIfNew(Storage.state().getVector(ContactKeys.VEC_CONTACT_GROUPS), mapPoint, 0, 5);
        XmppContactGroup.saveMapPoints(Storage.state().getVector(ContactKeys.VEC_CONTACT_GROUPS), 225);
        Storage.state().setInt(UIKeys.FLAG_LOADING, 0);
        return ScreenId.PROFILE_EDIT;
    }

    public static final int handleViewOption(int optionId) {
        if (optionId == 120) {
            if (!MapController.hasRoutePoints()) {
                return NotificationHelper.showError(354);
            }
            Storage.state().setInt(UIKeys.FLAG_NEW_MESSAGE, 1);
            return 0;
        }
        if (optionId != 100) {
            return optionId == 0 ? ScreenId.MAP : 0;
        }
        Storage.state().setInt(UIKeys.FLAG_NEW_MESSAGE, 1);
        return 0;
    }

    public static final int handleSearchResultAction(Object regionObj) {
        MapRenderer.invalidate();
        GeoRegion region = (GeoRegion) regionObj;
        MapRenderer.setPosition(region.centerLat, region.centerLon);
        MapRenderer.setZoom(region == StringUtils.getGeoRegion() ? ZOOM_HOME_REGION : ZOOM_SEARCH_RESULT);
        return 0;
    }

    public static final int processSearchQuery(String query) {
        if (!Storage.resources().getString(StringResKeys.STR_PROTOCOL_XMPP).equals(query)) {
            return 0;
        }
        if (MapRenderer.selectedMapPoint != null) {
            MapRenderer.selectedMapPoint.markInactive();
        }
        XmppContactGroup.startMapAnimation(Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE));
        Storage.state().setInt(MapKeys.FLAG_GPS_ACTIVE, 0);
        MmpContact.clearLocationData();
        MapRenderer.needsRedraw = true;
        XmppContactGroup.lastCheckTs = System.currentTimeMillis();
        MapRenderer.needsRedraw = true;
        return 0;
    }

    public static final int handleLocationAction(Object locationData) {
        if (locationData == null) {
            return 0;
        }
        Storage.state().setFromBuffer(UIKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(locationData));
        return 0;
    }

    public static Vector savedLocations;

    public static void showSavedLocations() {
        Vector locations = savedLocations;
        if (locations == null) {
            return;
        }
        ListView screen = ScreenManager.createScreen(ScreenDef.MAIL_ACCOUNT_LIST);
        for (int i = locations.size() - 1; i >= 0; i--) {
            MapPoint mapPoint = (MapPoint) locations.elementAt(i);
            screen.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
        }
        ScreenManager.showScreen(screen);
        AppController.needsRepaint = true;
    }

    public static int applyLocationProfile(Object obj) {
        MrimAccount mrimAccount = (MrimAccount) Storage.state().getAccount();
        MapPoint mapPoint = (MapPoint) obj;
        mrimAccount.profileManager.setMapLocation(mapPoint);
        XmppContactGroup.addMapPointIfNew(Storage.state().getVector(ContactKeys.VEC_CONTACT_GROUPS), mapPoint, 0, 5);
        XmppContactGroup.saveMapPoints(Storage.state().getVector(ContactKeys.VEC_CONTACT_GROUPS), 225);
        Storage.state().setInt(UIKeys.FLAG_LOADING, 0);
        mrimAccount.isHighlighted = true;
        return ScreenId.PROFILE_EDIT;
    }

    public static void startGeoSearch(String url, long lon, long lat) {
        new AsyncTask(AsyncTaskId.FETCH_SAVED_LOCATIONS, new Object[]{url, new long[]{lon, lat}});
    }
}
