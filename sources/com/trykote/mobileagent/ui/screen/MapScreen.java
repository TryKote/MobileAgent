package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.map.MapPoint;
import com.trykote.mobileagent.map.MapPointStore;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.map.MapUtils;
import com.trykote.mobileagent.map.TileRequest;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.VCard;
import com.trykote.mobileagent.net.RequestQueue;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.ui.TabBar;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;

import java.util.Enumeration;
import java.util.Vector;

public final class MapScreen extends ScreenView {

    private static final long SCROLL_FRAME_INTERVAL_MS = 45L;
    private static final long CROSSHAIR_DELAY_MS = 500L;
    private static final long MAP_IDLE_TIMEOUT_MS = 300000L;
    private static final int SCROLL_MULTIPLIER = 9;
    private static final int MAP_POINT_HEIGHT_SAVED = 4;
    private static final int MAX_SAVED_LOCATIONS = 50;
    private static final int MAP_SEARCH_ACTION_ROUTE = 407;
    private static final int MAP_SEARCH_ACTION_PLACE = 408;

    public MapScreen(int screenId) {
        super(typeFor(screenId), screenId);
    }

    private static int typeFor(int screenId) {
        switch (screenId) {
            case ScreenId.MAP:
                return ScreenManager.TYPE_FULLSCREEN;
            case ScreenId.MAP_MENU:
            case ScreenId.MAP_CONTEXT_MENU:
            case ScreenId.MAP_OPTIONS:
                return ScreenManager.TYPE_POPUP;
            default:
                return ScreenManager.TYPE_FULLSCREEN;
        }
    }

    public void showSelf() {
        switch (screenId) {
            case ScreenId.MAP:
                MapController.showMapScreen();
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                MapController.showMapContextMenu();
                break;
            case ScreenId.SAVED_LOCATIONS:
                MapController.showSavedLocations();
                break;
            default:
                ScreenManager.showScreen(this);
        }
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.MAP:
            case ScreenId.MAP_CONTEXT_MENU:
            case ScreenId.SAVED_LOCATIONS:
                break;
            case ScreenId.MAP_MENU:
                Screens.mapMenu().show();
                break;
            case ScreenId.MAP_POINTS:
                buildMapPoints();
                break;
            case ScreenId.MAP_TOOLTIP:
                MapState.setTooltipText1(AppState.emptyStr);
                String tooltipText = MapRenderer.getTooltipText();
                if (tooltipText != null) {
                    MapState.setTooltipText1(tooltipText);
                }
                Screens.mapTooltip().show();
                break;
            case ScreenId.PEOPLE_NEARBY:
                buildPeopleNearby();
                break;
            case ScreenId.SAVE_LOCATION:
                MapState.setTooltipText2(AppState.emptyStr);
                String tooltipText2 = MapRenderer.getTooltipText();
                if (tooltipText2 != null) {
                    MapState.setTooltipText2(tooltipText2);
                }
                Screens.saveLocation().show();
                break;
            case ScreenId.MAP_ROUTE:
                buildMapRoute(Screens.mapRoute());
                break;
            case ScreenId.MAP_STATUS:
                Conversation.updateStatusText(375);
                Screens.mapStatus().show();
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                buildMapRoute(Screens.mapRouteSelect());
                break;
            case ScreenId.SHARE_LOCATION:
                Screens.shareLocation().show();
                break;
            case ScreenId.MAP_SEARCH:
                MapState.setScreenBuilderAction(MapState.isMapModeActive() ? MAP_SEARCH_ACTION_ROUTE : MAP_SEARCH_ACTION_PLACE);
                Conversation.updateStatusText(411);
                Screens.mapSearch().show();
                break;
            case ScreenId.MAP_OPTIONS:
                Screens.mapOptions().show();
                break;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.MAP:
                if (!MapState.isTilesPending()) {
                    MapController.toggleMapControls(ScreenManager.getCurrentScreen());
                }
                return 0;
            case ScreenId.MAP_POINTS:
                return MapUtils.handleMapSearch(action, data);
            case ScreenId.MAP_TOOLTIP:
                ScreenManager.processScreenForm();
                return StringUtils.isEmpty(Utils.defaultStr(MapState.getTooltipText1())) ? NotificationHelper.showError(352) : 0;
            case ScreenId.PEOPLE_NEARBY:
                return handleMapSearchAction(data);
            case ScreenId.MAP_CONTEXT_MENU:
                return MapController.handleMapAction(action);
            case ScreenId.SAVE_LOCATION:
                return handleSaveLocation();
            case ScreenId.MAP_ROUTE:
                return handleMapResultAction(data);
            case ScreenId.MAP_STATUS:
                return processLoginField(title);
            case ScreenId.MAP_ROUTE_SELECT:
                return handleMapLocationSelect(data);
            case ScreenId.SHARE_LOCATION:
                return handleShareLocation();
            case ScreenId.MAP_SEARCH:
                return MapController.handleViewOption(action);
            case ScreenId.SAVED_LOCATIONS:
                return MapController.applyLocationProfile(data);
            default:
                return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.MAP:
                return handleMapSelect();
            case ScreenId.MAP_POINTS:
                return MapUtils.handleMapPointAction(data);
            case ScreenId.PEOPLE_NEARBY:
                return handleMapSearchAction(data);
            case ScreenId.MAP_CONTEXT_MENU:
                return MapController.handleMapAction(selectedOption);
            case ScreenId.MAP_ROUTE:
                return handleMapResultAction(data);
            case ScreenId.MAP_STATUS:
                return processLoginField(title);
            case ScreenId.MAP_ROUTE_SELECT:
                return handleMapLocationSelect(data);
            case ScreenId.MAP_SEARCH:
                return MapController.handleViewOption(selectedOption);
            case ScreenId.SAVED_LOCATIONS:
                return MapController.applyLocationProfile(data);
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        switch (screenId) {
            case ScreenId.MAP:
                return MapController.handleMapBack(ScreenManager.getCurrentScreen());
            case ScreenId.MAP_POINTS:
                UIState.setNewMessage(0);
                UIState.setLoading(0);
                return 0;
            case ScreenId.MAP_ROUTE:
                UIState.setNewMessage(0);
                return 0;
            case ScreenId.MAP_ROUTE_SELECT:
                ContactState.setLoaded(false);
                return 0;
            case ScreenId.SAVED_LOCATIONS:
                AppState.getAccount().setMapHighlighted(true);
                return 0;
            default:
                return 0;
        }
    }

    public int onIdle(MenuItem item, Object data, String title) {
        if (screenId != ScreenId.MAP) {
            return 0;
        }
        return processMapIdle();
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.MAP:
                TabBar.scrollEnabled = false;
                TabBar.removeSearchTab();
                break;
            case ScreenId.MAP_POINTS:
                RegistrationState.clearSearchQuery();
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                MapController.mapContextItem = null;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                UIState.setNewMessage(0);
                break;
            case ScreenId.SHARE_LOCATION:
                RuntimeState.clearMsgId1();
                RuntimeState.clearMsgId2();
                break;
        }
    }

    public void onResumed(int closedScreenId) {
        if (screenId == ScreenId.MAP) {
            AppController.needsRepaint = true;
        }
    }

    // --- Build helpers ---

    private void buildMapPoints() {
        Screen screen = Screens.mapPoints();
        Vector mapPoints = ContactState.getContactGroups();
        for (int i = 0; i < mapPoints.size(); i++) {
            MapPoint mapPoint = (MapPoint) mapPoints.elementAt(i);
            screen.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
        }
        ScreenManager.showScreen(screen);
    }

    private void buildPeopleNearby() {
        Vector allContacts = AccountManager.getAllContacts();
        for (int idx = allContacts.size() - 1; idx >= 0; idx--) {
            if (((Contact) allContacts.elementAt(idx)).isOffline()) {
                allContacts.removeElementAt(idx);
            }
        }
        if (allContacts.size() == 0) {
            NotificationHelper.showMessageById(762);
        } else {
            ContactListManager.sortContacts(allContacts);
            ScreenManager.showScreen(ContactListManager.addContactItems(Screens.peopleNearby(), allContacts));
        }
        ObjectPool.releaseVector(allContacts);
    }

    private static void buildMapRoute(Screen screen) {
        Enumeration routeEnum = MapController.getRouteElements();
        while (routeEnum.hasMoreElements()) {
            MapPoint mapPoint = (MapPoint) routeEnum.nextElement();
            screen.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
        }
        ScreenManager.showScreen(screen);
    }

    // --- Selection handlers ---

    private static int handleMapSelect() {
        if (!MapState.isMapOverlayActive()) {
            MapController.toggleMapControls(ScreenManager.getCurrentScreen());
            return -1;
        }
        if (MapState.isMapLoading()) {
            String lonStr = MapUtils.pixelToLongitude(MapRenderer.currentLon);
            String latStr = MapUtils.pixelToLatitude(MapRenderer.currentLat);
            MapState.setMapLoading(false);
            MapController.startGeoSearch(VCard.formatLocationUrl(MapState.getZoomLevel(), lonStr, latStr), MapRenderer.currentLon, MapRenderer.currentLat);
            return 0;
        }
        return ScreenId.MAP_CONTEXT_MENU;
    }

    private static int handleSaveLocation() {
        ScreenManager.processScreenForm();
        String locationName = Utils.defaultStr(MapState.getTooltipText2());
        if (StringUtils.isEmpty(locationName)) {
            return NotificationHelper.showError(372);
        }
        long lon = MapRenderer.currentLon;
        long lat = MapRenderer.currentLat;
        ListItem listItem = MapRenderer.tooltipItem;
        if (listItem != null && listItem.isSelected()) {
            lon = listItem.getWidth();
            lat = listItem.getBaseHeight();
            listItem.select();
        }
        MapPoint mapPoint = new MapPoint(locationName, 0L, 0L, 0L, 0L, lon, lat, MapState.getZoomLevel());
        mapPoint.height = MAP_POINT_HEIGHT_SAVED;
        Vector screenStack = UIState.getPhotoQueue();
        MapPointStore.addMapPointIfNew(screenStack, mapPoint, 0, MAX_SAVED_LOCATIONS);
        MapPointStore.saveMapPoints(screenStack, 226);
        MapRenderer.navigateToMapPoint(mapPoint);
        return 0;
    }

    private static int handleShareLocation() {
        ScreenManager.processScreenForm();
        String msgId = RuntimeState.getMsgId1();
        long lon = MapRenderer.currentLon;
        long lat = MapRenderer.currentLat;
        ListItem tooltipItem = MapRenderer.tooltipItem;
        if (tooltipItem != null && tooltipItem.isSelected()) {
            lon = tooltipItem.getWidth();
            lat = tooltipItem.getBaseHeight();
            tooltipItem.select();
        }
        String msgId2 = RuntimeState.getMsgId2();
        if (msgId != null) {
            RequestQueue.sharedContactList.addElement(new Object[]{msgId, new long[]{lon, lat}, msgId2});
            String sessionKey = Utils.defaultStr(SessionState.getSessionKey());
            ByteBuffer requestBuf = new ByteBuffer().writeCharBytes("http://mobile.mail.ru/data/map_point/add_object?").writeUInt(15713).writeRawString(msgId).writeUInt(4022822).writeLongAsString(lon).writeUInt(4023078).writeLongAsString(lat).writeUInt(4023334).writeRawString(sessionKey).writeUInt(4023590).writeRawString(new ByteBuffer().writeRawString(sessionKey).writeCharBytes("secret").writeLongAsString(lon).encryptMD5().toHexString());
            if (msgId2 != null) {
                requestBuf.writeUInt(4023846).writeRawString(Conversation.urlEncodeCyrillic(msgId2));
            }
            if (RegistrationState.isRegistrationDone()) {
                String lastAccount = SessionState.getLastAccountName();
                if (Utils.nonEmpty(lastAccount)) {
                    requestBuf.writeUInt(4024102).writeRawString(Conversation.urlEncodeCyrillic(lastAccount));
                }
            }
            new AsyncTask(AsyncTaskId.HTTP_FIRE_AND_FORGET, requestBuf.getStringAndClear());
        }
        MapRenderer.needsRedraw = true;
        return 0;
    }

    // --- Public static (used by other handlers) ---

    public static int handleMapSearchAction(Object contactObj) {
        long lon;
        long lat;
        Contact contact = (Contact) contactObj;
        String query = MapState.getTooltipText1();
        ListItem item = MapRenderer.tooltipItem;
        if (item == null || !item.isSelected()) {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        } else {
            lon = item.getWidth();
            lat = item.getBaseHeight();
        }
        int errorCode = contact.sendMessage(MapUtils.buildTileRequestUrl(lon, lat, MapState.getZoomLevel(), query));
        return errorCode != 0 ? NotificationHelper.showError(errorCode) : 0;
    }

    public static int handleMapResultAction(Object mapPointObj) {
        MapState.setResourceUrl((Object) ((MapPoint) mapPointObj).getResourceUrl());
        return 0;
    }

    public static int handleGeoSearch() {
        long lon;
        long lat;
        ListItem item = MapRenderer.tooltipItem;
        if (item != null) {
            lon = item.getWidth();
            lat = item.getBaseHeight();
        } else {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        }
        MapState.setMapLoading(false);
        MapController.startGeoSearch(VCard.formatLocationUrl(MapState.getZoomLevel(), MapUtils.pixelToLongitude(lon), MapUtils.pixelToLatitude(lat)), lon, lat);
        return ScreenId.MAP;
    }

    public static int handleMapLocationSelect(Object obj) {
        if (UIState.isNewMessage()) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return ScreenId.MAP;
        }
        if (!ContactState.isLoaded()) {
            MapController.pendingMapPoint = (MapPoint) obj;
            return ScreenId.CHAT_LIST_OPTIONS;
        }
        Account account = AppState.getAccount();
        MapPoint mapPoint = (MapPoint) obj;
        account.setProfileSimpleLocation(MapUtils.pixelToLongitude(mapPoint.longitude), MapUtils.pixelToLatitude(mapPoint.latitude));
        account.syncProfile();
        ContactState.setLoaded(false);
        return ScreenId.PROFILE_EDIT;
    }

    public static int handleMapModeOption(int optionId) {
        if (optionId != 0) {
            UIState.setLoading(1);
            return ScreenId.MAP_POINTS;
        }
        MapState.setMapLoading(true);
        AppState.getAccount().setMapHighlighted(false);
        return ScreenId.CLOSE;
    }

    public static int processLoginField(String protocol) {
        if (SessionState.getActiveProtocolName().equals(protocol)) {
            ScreenBuilder.onScreenClosed();
            if (MapController.hasRoutePoints()) {
                return 0;
            }
            return NotificationHelper.showError(354);
        }
        if (StringPool.get(StringResKeys.STR_PROTOCOL_MRIM).equals(protocol)) {
            MapState.setGpsActive(true);
            MapPointStore.stopMapAnimation(UIState.getPhotoQueue());
            MapRenderer.needsRedraw = true;
            return ScreenId.MAP;
        }
        if (!StringPool.get(StringResKeys.STR_PROTOCOL_MMP).equals(protocol)) {
            return 0;
        }
        MapState.setGpsActive(false);
        MapPointStore.startMapAnimation(UIState.getPhotoQueue());
        MapRenderer.needsRedraw = true;
        return ScreenId.MAP;
    }

    // --- Idle processing ---

    private static int processMapIdle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - MapState.getScrollTimestamp() > SCROLL_FRAME_INTERVAL_MS) {
            MapState.setScrollTimestamp(currentTime);
        }
        if (TimerManager.isTimerType(TimerManager.SLOT_MAP_CROSSHAIR) && MapRenderer.crosshairVisible) {
            if (MapState.isMapViewActive()) {
                if ((MapRenderer.currentLon < VCard.staticTs1 || MapRenderer.currentLon > VCard.staticTs3 || MapRenderer.currentLat > VCard.staticTs4 || MapRenderer.currentLat < VCard.staticTs2 || ((long) MapState.getZoomLevel()) != VCard.staticTs5) && MapState.isMapDataLoaded()) {
                    MapUtils.requestNearbyPeople();
                }
            }
            MapRenderer.setCrosshairVisible(false);
        }
        int scrollDirection = MapState.getScrollDirection();
        if (scrollDirection >= 0 && MapState.getScrollTimestamp() == currentTime && !MapState.isScrolling()) {
            MapState.setScrollLon(MapRenderer.currentLon);
            MapState.setScrollLat(MapRenderer.currentLat);
            int zoomLevel = MapState.getZoomLevel();
            long scrollDelta = (MapUtils.getZoomNumerator(zoomLevel) / MapUtils.getZoomDenominator(zoomLevel)) * SCROLL_MULTIPLIER;
            switch (scrollDirection) {
                case 0:
                    MapState.setScrollLon(MapState.getScrollLon() + scrollDelta);
                    break;
                case 1:
                    MapState.setScrollLon(MapState.getScrollLon() - scrollDelta);
                    break;
                case 2:
                    MapState.setScrollLat(MapState.getScrollLat() + scrollDelta);
                    break;
                case 3:
                    MapState.setScrollLat(MapState.getScrollLat() - scrollDelta);
                    break;
            }
            MapRenderer.setPosition(MapState.getScrollLon(), MapState.getScrollLat());
            TimerManager.setTimer(TimerManager.SLOT_MAP_CROSSHAIR, CROSSHAIR_DELAY_MS);
            MapRenderer.resetInteraction();
        }
        if (MapState.getScrollTimestamp() == currentTime) {
            MapRenderer.render();
        }
        if (ContactState.isListActive() && TimerManager.checkTimer(TimerManager.SLOT_MAP_IDLE, MAP_IDLE_TIMEOUT_MS)) {
            TimerManager.setTimer(TimerManager.SLOT_MAP_IDLE, MAP_IDLE_TIMEOUT_MS);
            StringUtils.clearSatelliteTiles();
            Vector mapData = MapState.getMapData();
            for (int idx = mapData.size() - 1; idx >= 0; idx--) {
                if (TileRequest.TYPE_OVERLAY == ((TileRequest) mapData.elementAt(idx)).tileType) {
                    mapData.removeElementAt(idx);
                }
            }
            MapRenderer.needsRedraw = true;
            new AsyncTask(AsyncTaskId.FETCH_CITY_ZOOM);
        }
        if (MapState.isScrolling()) {
            AppController.needsRepaint = true;
        }
        if (MapRenderer.tapConsumed) {
            MapRenderer.tapConsumed = false;
            return ScreenId.MAP_CONTEXT_MENU;
        }
        return MapController.showMapSearchResults();
    }
}
