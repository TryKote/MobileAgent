package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public final class MapHandler extends BaseScreenHandler {

    // Map scroll and timing
    private static final long SCROLL_FRAME_INTERVAL_MS = 45L;
    private static final long CROSSHAIR_DELAY_MS = 500L;
    private static final long MAP_IDLE_TIMEOUT_MS = 300000L;
    private static final int SCROLL_MULTIPLIER = 9;

    // Map point heights
    private static final int MAP_POINT_HEIGHT_SAVED = 4;

    // Saved locations
    private static final int MAX_SAVED_LOCATIONS = 50;

    // Map search action codes stored in INT_SCREEN_BUILDER_ACTION
    private static final int MAP_SEARCH_ACTION_ROUTE = 407;
    private static final int MAP_SEARCH_ACTION_PLACE = 408;

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.MAP:
                MapController.showMapScreen();
                break;
            case ScreenId.MAP_MENU:
                Screens.mapMenu(this).show();
                break;
            case ScreenId.MAP_POINTS:
                Screen screen11 = Screens.mapPoints(this);
                Vector mapPoints = ContactState.getContactGroups();
                for (int i19 = 0; i19 < mapPoints.size(); i19++) {
                    MapPoint mapPoint = (MapPoint) mapPoints.elementAt(i19);
                    screen11.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
                }
                ScreenManager.showScreen(screen11);
                break;
            case ScreenId.MAP_TOOLTIP:
                MapState.setTooltipText1(AppState.emptyStr);
                String tooltipText = MapRenderer.getTooltipText();
                if (tooltipText != null) {
                    MapState.setTooltipText1(tooltipText);
                }
                Screens.mapTooltip(this).show();
                break;
            case ScreenId.PEOPLE_NEARBY:
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
                    ScreenManager.showScreen(ContactListManager.addContactItems(Screens.peopleNearby(this), allContacts));
                }
                ObjectPool.releaseVector(allContacts);
                return;
            case ScreenId.MAP_CONTEXT_MENU:
                MapController.showMapContextMenu();
                break;
            case ScreenId.SAVE_LOCATION:
                MapState.setTooltipText2(AppState.emptyStr);
                String tooltipText2 = MapRenderer.getTooltipText();
                if (tooltipText2 != null) {
                    MapState.setTooltipText2(tooltipText2);
                }
                Screens.saveLocation(this).show();
                break;
            case ScreenId.MAP_ROUTE:
                Screen screen13 = Screens.mapRoute(this);
                Enumeration routeEnum = MapController.getRouteElements();
                while (routeEnum.hasMoreElements()) {
                    MapPoint mapPoint2 = (MapPoint) routeEnum.nextElement();
                    screen13.addIconItemWithData(-1, mapPoint2.name, 118, mapPoint2);
                }
                ScreenManager.showScreen(screen13);
                break;
            case ScreenId.MAP_STATUS:
                Conversation.updateStatusText(375);
                Screens.mapStatus(this).show();
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                Screen screen14 = Screens.mapRouteSelect(this);
                Enumeration routeEnum2 = MapController.getRouteElements();
                while (routeEnum2.hasMoreElements()) {
                    MapPoint mapPoint3 = (MapPoint) routeEnum2.nextElement();
                    screen14.addIconItemWithData(-1, mapPoint3.name, 6, mapPoint3);
                }
                ScreenManager.showScreen(screen14);
                break;
            case ScreenId.SHARE_LOCATION:
                Screens.shareLocation(this).show();
                break;
            case ScreenId.MAP_SEARCH:
                MapState.setScreenBuilderAction(MapState.isMapModeActive() ? MAP_SEARCH_ACTION_ROUTE : MAP_SEARCH_ACTION_PLACE);
                Conversation.updateStatusText(411);
                Screens.mapSearch(this).show();
                break;
            case ScreenId.SAVED_LOCATIONS:
                MapController.showSavedLocations();
                break;
            case ScreenId.MAP_OPTIONS:
                Screens.mapOptions(this).show();
                break;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView currentScreen, MenuItem menuItem, String title, int action, Object obj) {
        int nextScreen;
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
                if (!MapState.isTilesPending()) {
                    MapController.toggleMapControls(currentScreen);
                }
                nextScreen = 0;
                break;
            case ScreenId.MAP_MENU:
                nextScreen = 0;
                break;
            case ScreenId.MAP_POINTS:
                nextScreen = MapUtils.handleMapSearch(action, obj);
                break;
            case ScreenId.MAP_TOOLTIP:
                ScreenManager.processScreenForm();
                nextScreen = StringUtils.isEmpty(Utils.defaultStr(MapState.getTooltipText1())) ? NotificationHelper.showError(352) : 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                nextScreen = handleMapSearchAction(obj);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                nextScreen = MapController.handleMapAction(action);
                break;
            case ScreenId.SAVE_LOCATION:
                ScreenManager.processScreenForm();
                String locationName = Utils.defaultStr(MapState.getTooltipText2());
                int errorCode7;
                if (StringUtils.isEmpty(locationName)) {
                    errorCode7 = NotificationHelper.showError(372);
                } else {
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
                    XmppContactGroup.addMapPointIfNew(screenStack, mapPoint, 0, MAX_SAVED_LOCATIONS);
                    XmppContactGroup.saveMapPoints(screenStack, 226);
                    MapRenderer.navigateToMapPoint(mapPoint);
                    errorCode7 = 0;
                }
                nextScreen = errorCode7;
                break;
            case ScreenId.MAP_ROUTE:
                nextScreen = handleMapResultAction(obj);
                break;
            case ScreenId.MAP_STATUS:
                nextScreen = processLoginField(title);
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                nextScreen = handleMapLocationSelect(obj);
                break;
            case ScreenId.SHARE_LOCATION:
                ScreenManager.processScreenForm();
                String msgId = RuntimeState.getMsgId1();
                long lon2 = MapRenderer.currentLon;
                long lat2 = MapRenderer.currentLat;
                ListItem tooltipItem2 = MapRenderer.tooltipItem;
                if (tooltipItem2 != null && tooltipItem2.isSelected()) {
                    lon2 = tooltipItem2.getWidth();
                    lat2 = tooltipItem2.getBaseHeight();
                    tooltipItem2.select();
                }
                String msgId2 = RuntimeState.getMsgId2();
                long j = lon2;
                long j2 = lat2;
                if (msgId != null) {
                    XmppContactGroup.sharedContactList.addElement(new Object[]{msgId, new long[]{j, j2}, msgId2});
                }
                long j3 = lon2;
                long j4 = lat2;
                if (msgId != null) {
                    String sessionKey = Utils.defaultStr(SessionState.getSessionKey());
                    ByteBuffer requestBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MAP_POINT_ADD).writeUInt(15713).writeRawString(msgId).writeUInt(4022822).writeLongAsString(j3).writeUInt(4023078).writeLongAsString(j4).writeUInt(4023334).writeRawString(sessionKey).writeUInt(4023590).writeRawString(new ByteBuffer().writeRawString(sessionKey).writeCompressed(PackedStringKeys.TAG_SECRET).writeLongAsString(j3).encryptMD5().toHexString());
                    if (msgId2 != null) {
                        requestBuf.writeUInt(4023846).writeRawString(Conversation.urlEncodeCyrillic(msgId2));
                    }
                    if (RegistrationState.isRegistrationDone()) {
                        String msgId3 = SessionState.getLastAccountName();
                        if (Utils.nonEmpty(msgId3)) {
                            requestBuf.writeUInt(4024102).writeRawString(Conversation.urlEncodeCyrillic(msgId3));
                        }
                    }
                    new AsyncTask(AsyncTaskId.HTTP_FIRE_AND_FORGET, requestBuf.getStringAndClear());
                }
                MapRenderer.needsRedraw = true;
                nextScreen = 0;
                break;
            case ScreenId.MAP_SEARCH:
                nextScreen = MapController.handleViewOption(action);
                break;
            case ScreenId.SAVED_LOCATIONS:
                nextScreen = MapController.applyLocationProfile(obj);
                break;
            case ScreenId.MAP_OPTIONS:
                nextScreen = 0;
                break;
            default:
                nextScreen = 0;
                break;
        }
        return nextScreen;
    }

    public int onMenuItemAction(ListView currentScreen, MenuItem menuItem, Object data) {
        int result;
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
                result = MapController.handleMapBack(currentScreen);
                break;
            case ScreenId.MAP_MENU:
                result = 0;
                break;
            case ScreenId.MAP_POINTS:
                UIState.setNewMessage(0);
                UIState.setLoading(0);
                result = 0;
                break;
            case ScreenId.MAP_TOOLTIP:
                result = 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                result = 0;
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                result = 0;
                break;
            case ScreenId.SAVE_LOCATION:
                result = 0;
                break;
            case ScreenId.MAP_ROUTE:
                UIState.setNewMessage(0);
                result = 0;
                break;
            case ScreenId.MAP_STATUS:
                result = 0;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                ContactState.setLoaded(false);
                result = 0;
                break;
            case ScreenId.SHARE_LOCATION:
                result = 0;
                break;
            case ScreenId.MAP_SEARCH:
                result = 0;
                break;
            case ScreenId.SAVED_LOCATIONS:
                ((MrimAccount) AppState.getAccount()).isHighlighted = true;
                result = 0;
                break;
            case ScreenId.MAP_OPTIONS:
                result = 0;
                break;
            default:
                result = 0;
                break;
        }
        return result;
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
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

    public void onScreenResumed(ListView screen, int closedScreenId) {
        if (screen.screenId == ScreenId.MAP) {
            AppController.needsRepaint = true;
        }
    }

    public int onItemSelected(ListView screen, MenuItem menuItem, String title, int selectedOption,
                              Object data, Object headerData) {
        int actionResult;
        switch (screen.screenId) {
            case ScreenId.MAP:
                int i2;
                if (!MapState.isMapOverlayActive()) {
                    MapController.toggleMapControls(screen);
                    i2 = -1;
                } else if (MapState.isMapLoading()) {
                    String lonStr = MapUtils.pixelToLongitude(MapRenderer.currentLon);
                    String latStr = MapUtils.pixelToLatitude(MapRenderer.currentLat);
                    MapState.setMapLoading(false);
                    MapController.startGeoSearch(VCard.formatLocationUrl(MapState.getZoomLevel(), lonStr, latStr), MapRenderer.currentLon, MapRenderer.currentLat);
                    i2 = 0;
                } else {
                    i2 = ScreenId.MAP_CONTEXT_MENU;
                }
                actionResult = i2;
                break;
            case ScreenId.MAP_MENU:
                actionResult = 0;
                break;
            case ScreenId.MAP_POINTS:
                actionResult = MapUtils.handleMapPointAction(data);
                break;
            case ScreenId.MAP_TOOLTIP:
                actionResult = 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                actionResult = handleMapSearchAction(data);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                actionResult = MapController.handleMapAction(selectedOption);
                break;
            case ScreenId.SAVE_LOCATION:
                actionResult = 0;
                break;
            case ScreenId.MAP_ROUTE:
                actionResult = handleMapResultAction(data);
                break;
            case ScreenId.MAP_STATUS:
                actionResult = processLoginField(title);
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                actionResult = handleMapLocationSelect(data);
                break;
            case ScreenId.SHARE_LOCATION:
                actionResult = 0;
                break;
            case ScreenId.MAP_SEARCH:
                actionResult = MapController.handleViewOption(selectedOption);
                break;
            case ScreenId.SAVED_LOCATIONS:
                actionResult = MapController.applyLocationProfile(data);
                break;
            case ScreenId.MAP_OPTIONS:
                actionResult = 0;
                break;
            default:
                actionResult = 0;
                break;
        }
        return actionResult;
    }

    public static final int handleMapSearchAction(Object contactObj) {
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
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return 0;
    }

    public static final int handleMapResultAction(Object mapPointObj) {
        MapState.setResourceUrl((Object) ((MapPoint) mapPointObj).getResourceUrl());
        return 0;
    }

    public static final int handleGeoSearch() {
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

    public static final int handleMapLocationSelect(Object obj) {
        if (UIState.isNewMessage()) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return ScreenId.MAP;
        }
        if (!ContactState.isLoaded()) {
            MapController.pendingMapPoint = (MapPoint) obj;
            return ScreenId.CHAT_LIST_OPTIONS;
        }
        MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
        MapPoint mapPoint = (MapPoint) obj;
        mrimAccount.profileManager.setSimpleLocation(MapUtils.pixelToLongitude(mapPoint.longitude), MapUtils.pixelToLatitude(mapPoint.latitude));
        mrimAccount.profileManager.sync();
        ContactState.setLoaded(false);
        return ScreenId.PROFILE_EDIT;
    }

    public static final int handleMapModeOption(int optionId) {
        if (optionId != 0) {
            UIState.setLoading(1);
            return ScreenId.MAP_POINTS;
        }
        MapState.setMapLoading(true);
        ((MrimAccount) AppState.getAccount()).isHighlighted = false;
        return ScreenId.CLOSE;
    }

    public int onIdleProcess(ListView currentScreen, MenuItem menuItem, Object data, String title) {
        boolean z5;
        long currentTime = System.currentTimeMillis();
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
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
                int stateInt2 = MapState.getScrollDirection();
                if (stateInt2 >= 0 && MapState.getScrollTimestamp() == currentTime && !MapState.isScrolling()) {
                    MapState.setScrollLon(MapRenderer.currentLon);
                    MapState.setScrollLat(MapRenderer.currentLat);
                    int stateInt3 = MapState.getZoomLevel();
                    long scrollDelta = (MapUtils.getZoomNumerator(stateInt3) / MapUtils.getZoomDenominator(stateInt3)) * SCROLL_MULTIPLIER;
                    switch (stateInt2) {
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
                    Vector vec4 = MapState.getMapData();
                    for (int idx = vec4.size() - 1; idx >= 0; idx--) {
                        if (TileRequest.TYPE_OVERLAY == ((TileRequest) vec4.elementAt(idx)).tileType) {
                            vec4.removeElementAt(idx);
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
                    z5 = true;
                } else {
                    z5 = false;
                }
                return z5 ? ScreenId.MAP_CONTEXT_MENU : MapController.showMapSearchResults();
            default:
                return 0;
        }
    }

    public static int processLoginField(String protocol) {
        if (SessionState.getActiveProtocolName().equals(protocol)) {
            ScreenBuilder.onScreenClosed();
            if (MapController.hasRoutePoints()) {
                return 0;
            }
            return NotificationHelper.showError(354);
        }
        if (ResourceAccessor.str(StringResKeys.STR_PROTOCOL_MRIM).equals(protocol)) {
            MapState.setGpsActive(true);
            XmppContactGroup.stopMapAnimation(UIState.getPhotoQueue());
            MapRenderer.needsRedraw = true;
            return ScreenId.MAP;
        }
        if (!ResourceAccessor.str(StringResKeys.STR_PROTOCOL_MMP).equals(protocol)) {
            return 0;
        }
        MapState.setGpsActive(false);
        XmppContactGroup.startMapAnimation(UIState.getPhotoQueue());
        MapRenderer.needsRedraw = true;
        return ScreenId.MAP;
    }
}
