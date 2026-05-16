package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.PhoneContact;
import com.trykote.mobileagent.model.SearchEntry;
import com.trykote.mobileagent.model.UserSearchResult;
import com.trykote.mobileagent.model.VCard;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.net.ServiceRegistry;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.MrimProfileManager;
import com.trykote.mobileagent.net.RequestQueue;
import com.trykote.mobileagent.util.ImageCache;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.GraphicsContext;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.ui.TabBar;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.Enumeration;
import java.util.Vector;

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
        SessionState.setConnectionState(6);
        Screen screen = Screens.mapView();
        mapScreen = screen;
        setMapSoftKeys(screen);
        ScreenManager.pushScreen(screen);
        TabBar.ensureSearchTab();
        TabBar.findTab(TabBar.TYPE_SEARCH, (Account) null);
        TabBar.scrollEnabled = MapState.isMapOverlayActive();
        if (ContactState.isRefreshNeeded()) {
            return;
        }
        ScreenBuilder.openScreen(ScreenId.EDIT_SCREEN);
    }

    public static final void updateMapSoftKeys() {
        if (MapState.isTilesPending()) {
            if (MapState.isMapScreenVisible() || mapScreen == null) {
                return;
            }
            mapScreen.setSoftKeys(ResourceAccessor.str(StringResKeys.STR_SOFTKEY_MAP), ResourceAccessor.str(StringResKeys.STR_SOFTKEY_CLOSE), 167, 4, 167);
            MapState.setMapScreenVisible(true);
            return;
        }
        if (!MapState.isMapScreenVisible() || mapScreen == null) {
            return;
        }
        setMapSoftKeys(mapScreen);
        MapState.setMapScreenVisible(false);
    }

    private static final void initMapState() {
        if (mapInitialized) {
            return;
        }
        mapInitialized = true;
        int contentHeight = Screens.mapView().contentHeight;
        MapState.setScrollLon(DEFAULT_LONGITUDE);
        MapState.setScrollLat(DEFAULT_LATITUDE);
        ContactState.setContactGroups(MapPointStore.loadMapPoints(225));
        UIState.setPhotoQueue(MapPointStore.loadMapPoints(226));
        MapState.setViewportWidth(UIState.getScreenWidth());
        MapState.setViewportHeight(contentHeight);
        MapState.setSavedLongitude(MapState.getLongitude());
        MapState.setSavedLatitude(MapState.getLatitude());
        MapRenderer.viewportWidth = MapState.getViewportWidth();
        MapRenderer.viewportHeight = MapState.getViewportHeight();
        MapRenderer.currentLat = MapState.getSavedLatitude();
        MapRenderer.currentLon = MapState.getSavedLongitude();
        int zoomLevel = MapState.getZoomLevel();
        MapRenderer.currentPixelX = MapUtils.coordToPixel(MapRenderer.currentLon, zoomLevel);
        MapRenderer.currentPixelY = MapUtils.coordToPixel(MapRenderer.currentLat, zoomLevel);
        MapState.setFont2(Image.createImage(MapRenderer.viewportWidth, MapRenderer.viewportHeight));
        StringUtils.initTileCache();
        ChatState.setTileRequestQueue(ObjectPool.newVector());
        RuntimeState.setSearchParams1(ObjectPool.newVector());
        Object[] urlComponents = ApiClient.getUrlComponents(AppState.emptyStr);
        MapState.setTileRequestArray(urlComponents);
        RequestQueue.addContactInfoToQueue(urlComponents);
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
        MapState.setMenuLabels(checkerImage);
        RuntimeState.setSearchParams2(ObjectPool.newVector());
        new AsyncTask(AsyncTaskId.TILE_LOADER);
        MapRenderer.syncLock = new Object();
        StringUtils.initGeoRegions();
        MapRenderer.invalidate();
        RouteData.routeRegions = ObjectPool.newVector();
        RouteData.routePoints = ObjectPool.newVector();
        RouteData.nearestPoints = ObjectPool.newVector();
        RouteData.lastTokenPair = new long[2];
        RouteData.currentTokenPair = new long[2];
        MapRenderer.animationSteps = ObjectPool.newVector();
        if (MapState.isGpsActive()) {
            MapPointStore.stopMapAnimation(UIState.getPhotoQueue());
        }
        MapState.setMapData(ObjectPool.newVector());
        MapRenderer.needsRedraw = true;
        MapState.setScrollTimestamp(System.currentTimeMillis() - 90);
        new AsyncTask(AsyncTaskId.FETCH_GEO_CONFIG);
        ServiceRegistry.loadSavedData();
    }

    private static final void setMapSoftKeys(ListView screen) {
        screen.setSoftKeys(ResourceAccessor.str(StringResKeys.STR_SOFTKEY_MENU), AppState.getString(MapState.isMapOverlayActive() ? 1050 : 328), 20, 0, 0);
    }

    public static final void toggleMapControls(ListView screen) {
        if (MapState.isMapOverlayActive()) {
            return;
        }
        toggleScrollMode();
        setMapSoftKeys(screen);
    }

    public static final int handleMapBack(ListView screen) {
        if (MapState.isTilesPending()) {
            AppState.getAccount().setMapHighlighted(false);
            MapRenderer.needsRedraw = true;
            toggleScrollMode();
            return 0;
        }
        Account account = AppState.getAccount();
        if (MapState.isMapLoading() && account != null) {
            account.setMapHighlighted(false);
        }
        toggleScrollMode();
        setMapSoftKeys(screen);
        return 0;
    }

    public static final void handleMapSwitch(ListView screen) {
        if (MapState.isMapOverlayActive()) {
            MapState.setScrollDirection(3);
        } else {
            toggleMapControls(screen);
        }
    }

    public static final void toggleScrollMode() {
        boolean overlayActive = !MapState.isMapOverlayActive();
        MapState.setMapOverlayActive(overlayActive);
        if (!overlayActive) {
            MapState.setMapLoading(false);
        }
        TabBar.scrollEnabled = overlayActive;
    }

    public static final void navigateToPoint(MapPoint mapPoint, boolean addToHistory) {
        initMapState();
        if (addToHistory) {
            MapPointStore.addMapPointIfNew(ContactState.getContactGroups(), mapPoint, 0, 5);
            MapPointStore.saveMapPoints(ContactState.getContactGroups(), 225);
        }
        MapRenderer.selectedMapPoint = mapPoint;
        MapRenderer.invalidate();
        MapRenderer.setPosition(MapRenderer.selectedMapPoint.longitude, MapRenderer.selectedMapPoint.latitude);
        MapRenderer.setZoom(MapRenderer.selectedMapPoint.zoomLevel);
        MapRenderer.selectedMapPoint.markActive();
        MapRenderer.resetInteraction();
    }

    public static final int showMapSearchResults() {
        Vector searchResults = ChatState.getMessageList();
        if (searchResults != null) {
            ChatState.clearMessageList();
        }
        if (searchResults == null) {
            return 0;
        }
        AppController.needsRepaint = true;
        int size = searchResults.size();
        if (size == 0) {
            return NotificationHelper.showError(327);
        }
        Screen resultScreen = Screens.mapOverlay();
        for (int i = 0; i < size; i++) {
            MapPoint mapPoint = (MapPoint) searchResults.elementAt(i);
            resultScreen.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
        }
        ScreenManager.showScreen(resultScreen);
        return 0;
    }

    public static final Enumeration getRouteElements() {
        return UIState.getPhotoQueue().elements();
    }

    public static final boolean hasRoutePoints() {
        return UIState.getPhotoQueue().size() > 0;
    }

    public static final void removeRoutePoint(MapPoint mapPoint) {
        Vector routePoints = UIState.getPhotoQueue();
        routePoints.removeElement(mapPoint);
        MapPointStore.saveMapPoints(routePoints, 226);
    }

    public static final void setRouteStart() {
        long lon;
        long lat;
        if (MapRenderer.hasRouteEndpoints()) {
            RouteData.clearRouteProgress();
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
        RouteData.setFirstToken(lon, lat);
        MapRenderer.needsRedraw = true;
        if (MapRenderer.hasRouteEndpoints()) {
            Conversation.loadContacts();
        }
    }

    public static final void setRouteEnd() {
        long lon;
        long lat;
        if (MapRenderer.hasRouteEndpoints()) {
            RouteData.clearRouteProgress();
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
        RouteData.setSecondToken(lon, lat);
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
        MapState.setMapOverlayActive(true);
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
        if (!ContactState.isListActive()) {
            actionFlags &= ACTION_MASK_REMOVE_LIST;
        }
        int slotIndex = MENU_FLAGS_BASE_SLOT;
        for (int flagBit = 1; flagBit < MENU_FLAGS_SCAN_LIMIT; flagBit <<= 1) {
            AppState.setInt(slotIndex, actionFlags & flagBit);
            slotIndex++;
        }
        Screens.xmppMapContext().show();
    }

    public static final int handleMapAction(int actionId) {
        long lon;
        long lat;
        ListItem item = mapContextItem;
        int itemType = item == null ? 0 : item.getHeight();
        switch (actionId) {
            case 0:
                AppState.setCurrentEntity(item);
                ScreenBuilder.onScreenClosed();
                return ScreenId.STATUS_INPUT;
            case 1:
                if (itemType == 8) {
                    RuntimeState.setAsyncTaskId(0);
                    MrimProfileManager.openUserProfile((MrimAccount) null, ((UserSearchResult) item).userId);
                } else {
                    AppState.setCurrentEntity(mapContextItem);
                }
                ScreenBuilder.onScreenClosed();
                return ScreenId.USER_PROFILE;
            case 2:
                if (itemType == 3) {
                    AppState.setCurrentEntity(mapContextItem);
                    ScreenBuilder.onScreenClosed();
                    return ScreenId.CONTACT_DELETE;
                }
                AppState.setCurrentEntity((Object) null);
                Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
                if (onlineAccounts == null || onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((Account) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) item).userId, 1));
                ScreenBuilder.onScreenClosed();
                return ScreenId.CONTACT_DELETE;
            case 3:
                Vector onlineAccounts2 = AccountManager.getOnlineMrimAccounts();
                if (onlineAccounts2 == null || onlineAccounts2.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((Account) onlineAccounts2.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) item).userId, 2));
                ScreenBuilder.onScreenClosed();
                return ScreenId.MAP;
            case 4:
                ScreenBuilder.onScreenClosed();
                UIState.setTempObject1((Conversation) item);
                return ScreenId.FORM_LIST;
            case 5:
                ContactListManager.dialPhoneUrl(VCard.formatPhoneContactUrl((PhoneContact) item, 0), (PhoneContact) item, 0);
                return ScreenId.CLOSE;
            case 6:
                AppState.setAccount(item);
                ScreenBuilder.onScreenClosed();
                return ScreenId.MAILBOX_OPTIONS;
            case 7:
                AppState.setAccount(item);
                ScreenBuilder.onScreenClosed();
                return ScreenId.EXT_SETTINGS;
            case 8:
            case 9:
            default:
                return ScreenId.MAP;
            case 10:
                if (MapRenderer.hasRouteEndpoints()) {
                    RouteData.clearRouteProgress();
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
                RouteData.routePoints.addElement(coords);
                RouteData.nearestPoints.addElement(new Object[]{null, coords});
                MapRenderer.needsRedraw = true;
                if (!MapRenderer.hasRouteEndpoints()) {
                    return ScreenId.MAP;
                }
                Conversation.loadContacts();
                return ScreenId.MAP;
            case 11:
                if (MapRenderer.hasRouteEndpoints()) {
                    RouteData.clearRouteProgress();
                }
                if (RouteData.mapDataCache != null) {
                    RouteData.routePoints.removeElement((int[]) RouteData.mapDataCache[1]);
                    RouteData.nearestPoints.removeElement(RouteData.mapDataCache);
                }
                UIState.setRoutePointHidden(0);
                UIState.setRoutePointVisible(UIState.isRouteLocationActive());
                MapRenderer.needsRedraw = true;
                if (!MapRenderer.hasRouteEndpoints()) {
                    return ScreenId.MAP;
                }
                Conversation.loadContacts();
                return ScreenId.MAP;
            case 12:
                RouteData.clearLocationData();
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
                if (RouteData.hasSecondToken()) {
                    return ScreenId.MAP;
                }
                MapState.setMapModeActive(true);
                return ScreenId.MAP_SEARCH;
            case 15:
                setRouteEnd();
                if (RouteData.hasFirstToken()) {
                    return ScreenId.MAP;
                }
                MapState.setMapModeActive(false);
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
                AppState.setAccount(onlineAccounts3.elementAt(0));
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
        MapState.setMapViewActive(showMap);
        ContactState.setListActive(showList);
        if (!shouldInvalidate || !MapController.mapInitialized) {
            return;
        }
        for (int cacheIdx = TILE_CACHE_COUNT; cacheIdx >= 0; cacheIdx--) {
            ImageCache.invalidateCachedImage(cacheIdx + TILE_CACHE_START_OFFSET);
        }
        RouteData.clearLocationData();
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
        MapState.setMapOverlayActive(true);
        return 0;
    }

    public static final int handleMapPointAction(Object obj) {
        if (UIState.isNewMessage()) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 0;
        }
        if (!UIState.isLoading()) {
            MapController.navigateToPoint((MapPoint) obj, true);
            return 0;
        }
        MapPoint mapPoint = (MapPoint) obj;
        AppState.getAccount().setProfileMapLocation(mapPoint);
        MapPointStore.addMapPointIfNew(ContactState.getContactGroups(), mapPoint, 0, 5);
        MapPointStore.saveMapPoints(ContactState.getContactGroups(), 225);
        UIState.setLoading(0);
        return ScreenId.PROFILE_EDIT;
    }

    public static final int handleViewOption(int optionId) {
        if (optionId == 120) {
            if (!MapController.hasRoutePoints()) {
                return NotificationHelper.showError(354);
            }
            UIState.setNewMessage(1);
            return 0;
        }
        if (optionId != 100) {
            return optionId == 0 ? ScreenId.MAP : 0;
        }
        UIState.setNewMessage(1);
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
        if (!ResourceAccessor.str(StringResKeys.STR_PROTOCOL_XMPP).equals(query)) {
            return 0;
        }
        if (MapRenderer.selectedMapPoint != null) {
            MapRenderer.selectedMapPoint.markInactive();
        }
        MapPointStore.startMapAnimation(UIState.getPhotoQueue());
        MapState.setGpsActive(false);
        RouteData.clearLocationData();
        MapRenderer.needsRedraw = true;
        MapPointStore.lastCheckTs = System.currentTimeMillis();
        MapRenderer.needsRedraw = true;
        return 0;
    }

    public static final int handleLocationAction(Object locationData) {
        if (locationData == null) {
            return 0;
        }
        UIState.setStatusTextFromBuffer(Utils.getMessageBuffer().append(locationData));
        return 0;
    }

    public static Vector savedLocations;

    public static void showSavedLocations() {
        Vector locations = savedLocations;
        if (locations == null) {
            return;
        }
        Screen screen = Screens.mailAccountList();
        for (int i = locations.size() - 1; i >= 0; i--) {
            MapPoint mapPoint = (MapPoint) locations.elementAt(i);
            screen.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
        }
        ScreenManager.showScreen(screen);
        AppController.needsRepaint = true;
    }

    public static int applyLocationProfile(Object obj) {
        Account account = AppState.getAccount();
        MapPoint mapPoint = (MapPoint) obj;
        account.setProfileMapLocation(mapPoint);
        MapPointStore.addMapPointIfNew(ContactState.getContactGroups(), mapPoint, 0, 5);
        MapPointStore.saveMapPoints(ContactState.getContactGroups(), 225);
        UIState.setLoading(0);
        account.setMapHighlighted(true);
        return ScreenId.PROFILE_EDIT;
    }

    public static void startGeoSearch(String url, long lon, long lat) {
        new AsyncTask(AsyncTaskId.FETCH_SAVED_LOCATIONS, new Object[]{url, new long[]{lon, lat}});
    }
}
