package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
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

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.MAP:
                MapController.showMapScreen();
                break;
            case ScreenId.MAP_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_MENU));
                break;
            case ScreenId.MAP_POINTS:
                ListView screen11 = ScreenManager.createScreen(ScreenDef.MAP_POINTS);
                Vector mapPoints = Storage.state().getVector(ContactKeys.VEC_CONTACT_GROUPS);
                for (int i19 = 0; i19 < mapPoints.size(); i19++) {
                    MapPoint mapPoint = (MapPoint) mapPoints.elementAt(i19);
                    screen11.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
                }
                ScreenManager.showScreen(screen11);
                break;
            case ScreenId.MAP_TOOLTIP:
                Storage.state().setObject(MapKeys.SLOT_TOOLTIP_TEXT_1, (Object) Storage.emptyStr);
                String tooltipText = MapRenderer.getTooltipText();
                if (tooltipText != null) {
                    Storage.state().setObject(MapKeys.SLOT_TOOLTIP_TEXT_1, (Object) tooltipText);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_TOOLTIP));
                break;
            case ScreenId.PEOPLE_NEARBY:
                Vector allContacts = AccountManager.getAllContacts();
                int size10 = allContacts.size();
                while (true) {
                    size10--;
                    if (size10 < 0) {
                        if (allContacts.size() == 0) {
                            NotificationHelper.showMessageById(762);
                        } else {
                            ContactListManager.sortContacts(allContacts);
                            ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.PEOPLE_NEARBY), allContacts));
                        }
                        ObjectPool.releaseVector(allContacts);
                        return;
                    }
                    if (((Contact) allContacts.elementAt(size10)).isOffline()) {
                        allContacts.removeElementAt(size10);
                    }
                }
            case ScreenId.MAP_CONTEXT_MENU:
                MapController.showMapContextMenu();
                break;
            case ScreenId.SAVE_LOCATION:
                Storage.state().setObject(MapKeys.SLOT_TOOLTIP_TEXT_2, (Object) Storage.emptyStr);
                String tooltipText2 = MapRenderer.getTooltipText();
                if (tooltipText2 != null) {
                    Storage.state().setObject(MapKeys.SLOT_TOOLTIP_TEXT_2, (Object) tooltipText2);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SAVE_LOCATION));
                break;
            case ScreenId.MAP_ROUTE:
                ListView screen13 = ScreenManager.createScreen(ScreenDef.MAP_ROUTE);
                Enumeration routeEnum = MapController.getRouteElements();
                while (routeEnum.hasMoreElements()) {
                    MapPoint mapPoint2 = (MapPoint) routeEnum.nextElement();
                    screen13.addIconItemWithData(-1, mapPoint2.name, 118, mapPoint2);
                }
                ScreenManager.showScreen(screen13);
                break;
            case ScreenId.MAP_STATUS:
                Conversation.updateStatusText(375);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_STATUS));
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                ListView screen14 = ScreenManager.createScreen(ScreenDef.MAP_ROUTE_SELECT);
                Enumeration routeEnum2 = MapController.getRouteElements();
                while (routeEnum2.hasMoreElements()) {
                    MapPoint mapPoint3 = (MapPoint) routeEnum2.nextElement();
                    screen14.addIconItemWithData(-1, mapPoint3.name, 6, mapPoint3);
                }
                ScreenManager.showScreen(screen14);
                break;
            case ScreenId.SHARE_LOCATION:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SHARE_LOCATION));
                break;
            case ScreenId.MAP_SEARCH:
                Storage.state().setInt(MapKeys.INT_SCREEN_BUILDER_ACTION, Storage.state().getBool(MapKeys.FLAG_MAP_MODE_ACTIVE) ? 407 : 408);
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_SEARCH));
                break;
            case ScreenId.SAVED_LOCATIONS:
                MapController.showSavedLocations();
                break;
            case ScreenId.MAP_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_OPTIONS));
                break;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView currentScreen, MenuItem menuItem, String title, int action, Object obj) {
        int nextScreen;
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
                if (!Storage.state().getBool(MapKeys.FLAG_MAP_TILES_PENDING)) {
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
                nextScreen = StringUtils.isEmpty(Utils.defaultStr(Storage.state().getString(MapKeys.SLOT_TOOLTIP_TEXT_1))) ? NotificationHelper.showError(352) : 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                nextScreen = handleMapSearchAction(obj);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                nextScreen = MapController.handleMapAction(action);
                break;
            case ScreenId.SAVE_LOCATION:
                ScreenManager.processScreenForm();
                String locationName = Utils.defaultStr(Storage.state().getString(MapKeys.SLOT_TOOLTIP_TEXT_2));
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
                    MapPoint mapPoint = new MapPoint(locationName, 0L, 0L, 0L, 0L, lon, lat, Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL));
                    mapPoint.height = 4;
                    Vector screenStack = Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE);
                    XmppContactGroup.addMapPointIfNew(screenStack, mapPoint, 0, 50);
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
                String msgId = Storage.state().getString(RuntimeKeys.SLOT_MSG_ID_1);
                long lon2 = MapRenderer.currentLon;
                long lat2 = MapRenderer.currentLat;
                ListItem tooltipItem2 = MapRenderer.tooltipItem;
                if (tooltipItem2 != null && tooltipItem2.isSelected()) {
                    lon2 = tooltipItem2.getWidth();
                    lat2 = tooltipItem2.getBaseHeight();
                    tooltipItem2.select();
                }
                String msgId2 = Storage.state().getString(RuntimeKeys.SLOT_MSG_ID_2);
                long j = lon2;
                long j2 = lat2;
                if (msgId != null) {
                    XmppContactGroup.sharedContactList.addElement(new Object[]{msgId, new long[]{j, j2}, msgId2});
                }
                long j3 = lon2;
                long j4 = lat2;
                if (msgId != null) {
                    String sessionKey = Utils.defaultStr(Storage.state().getString(SessionKeys.SESSION_KEY));
                    ByteBuffer requestBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MAP_POINT_ADD).writeUInt(15713).writeRawString(msgId).writeUInt(4022822).writeLongAsString(j3).writeUInt(4023078).writeLongAsString(j4).writeUInt(4023334).writeRawString(sessionKey).writeUInt(4023590).writeRawString(new ByteBuffer().writeRawString(sessionKey).writeCompressed(PackedStringKeys.TAG_SECRET).writeLongAsString(j3).encryptMD5().toHexString());
                    if (msgId2 != null) {
                        requestBuf.writeUInt(4023846).writeRawString(Conversation.urlEncodeCyrillic(msgId2));
                    }
                    if (Storage.state().getBool(RegistrationKeys.FLAG_REGISTRATION_DONE)) {
                        String msgId3 = Storage.state().getString(SessionKeys.LAST_ACCOUNT_NAME);
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
                Storage.state().setInt(UIKeys.FLAG_NEW_MESSAGE, 0);
                Storage.state().setInt(UIKeys.FLAG_LOADING, 0);
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
                Storage.state().setInt(UIKeys.FLAG_NEW_MESSAGE, 0);
                result = 0;
                break;
            case ScreenId.MAP_STATUS:
                result = 0;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                Storage.state().setInt(ContactKeys.FLAG_CONTACTS_LOADED, 0);
                result = 0;
                break;
            case ScreenId.SHARE_LOCATION:
                result = 0;
                break;
            case ScreenId.MAP_SEARCH:
                result = 0;
                break;
            case ScreenId.SAVED_LOCATIONS:
                ((MrimAccount) Storage.state().getAccount()).isHighlighted = true;
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
                Storage.state().clearIndex(RegistrationKeys.SLOT_SEARCH_QUERY);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                MapController.mapContextItem = null;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                Storage.state().setInt(UIKeys.FLAG_NEW_MESSAGE, 0);
                break;
            case ScreenId.SHARE_LOCATION:
                Storage.state().clearIndex(RuntimeKeys.SLOT_MSG_ID_1);
                Storage.state().clearIndex(RuntimeKeys.SLOT_MSG_ID_2);
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem menuItem, String title, int selectedOption,
                              Object data, Object headerData) {
        int actionResult;
        switch (screen.screenId) {
            case ScreenId.MAP:
                int i2;
                if (!Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
                    MapController.toggleMapControls(screen);
                    i2 = -1;
                } else if (Storage.state().getBool(MapKeys.FLAG_MAP_LOADING)) {
                    String lonStr = MapUtils.pixelToLongitude(MapRenderer.currentLon);
                    String latStr = MapUtils.pixelToLatitude(MapRenderer.currentLat);
                    Storage.state().setInt(MapKeys.FLAG_MAP_LOADING, 0);
                    MapController.startGeoSearch(VCard.formatLocationUrl(Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL), lonStr, latStr), MapRenderer.currentLon, MapRenderer.currentLat);
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
        String query = Storage.state().getString(MapKeys.SLOT_TOOLTIP_TEXT_1);
        ListItem item = MapRenderer.tooltipItem;
        if (item == null || !item.isSelected()) {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        } else {
            lon = item.getWidth();
            lat = item.getBaseHeight();
        }
        int errorCode = contact.sendMessage(MapUtils.buildTileRequestUrl(lon, lat, Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL), query));
        if (0 != errorCode) {
            return NotificationHelper.showError(errorCode);
        }
        return 0;
    }

    public static final int handleMapResultAction(Object mapPointObj) {
        Storage.state().setObject(MapKeys.MAP_RESOURCE_URL, (Object) ((MapPoint) mapPointObj).getResourceUrl());
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
        Storage.state().setInt(MapKeys.FLAG_MAP_LOADING, 0);
        MapController.startGeoSearch(VCard.formatLocationUrl(Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL), MapUtils.pixelToLongitude(lon), MapUtils.pixelToLatitude(lat)), lon, lat);
        return ScreenId.MAP;
    }

    public static final int handleMapLocationSelect(Object obj) {
        if (Storage.state().getBool(UIKeys.FLAG_NEW_MESSAGE)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return ScreenId.MAP;
        }
        if (!Storage.state().getBool(ContactKeys.FLAG_CONTACTS_LOADED)) {
            MapController.pendingMapPoint = (MapPoint) obj;
            return ScreenId.CHAT_LIST_OPTIONS;
        }
        MrimAccount mrimAccount = (MrimAccount) Storage.state().getAccount();
        MapPoint mapPoint = (MapPoint) obj;
        mrimAccount.profileManager.setSimpleLocation(MapUtils.pixelToLongitude(mapPoint.longitude), MapUtils.pixelToLatitude(mapPoint.latitude));
        mrimAccount.profileManager.sync();
        Storage.state().setInt(ContactKeys.FLAG_CONTACTS_LOADED, 0);
        return ScreenId.PROFILE_EDIT;
    }

    public static final int handleMapModeOption(int optionId) {
        if (optionId != 0) {
            Storage.state().setInt(UIKeys.FLAG_LOADING, 1);
            return ScreenId.MAP_POINTS;
        }
        Storage.state().setInt(MapKeys.FLAG_MAP_LOADING, 1);
        ((MrimAccount) Storage.state().getAccount()).isHighlighted = false;
        return ScreenId.CLOSE;
    }

    public int onIdleProcess(ListView currentScreen, MenuItem menuItem, Object data, String title) {
        boolean z5;
        long currentTime = System.currentTimeMillis();
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
                if (currentTime - Storage.state().getLong(MapKeys.TIMESTAMP_MAP_SCROLL) > 45) {
                    Storage.state().setLong(MapKeys.TIMESTAMP_MAP_SCROLL, currentTime);
                }
                if (TimerManager.isTimerType(TimerManager.SLOT_MAP_CROSSHAIR) && MapRenderer.crosshairVisible) {
                    if (Storage.state().getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE)) {
                        if ((MapRenderer.currentLon < VCard.staticTs1 || MapRenderer.currentLon > VCard.staticTs3 || MapRenderer.currentLat > VCard.staticTs4 || MapRenderer.currentLat < VCard.staticTs2 || ((long) Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL)) != VCard.staticTs5) && Storage.state().getBool(MapKeys.FLAG_MAP_DATA_LOADED)) {
                            MapUtils.requestNearbyPeople();
                        }
                    }
                    MapRenderer.setCrosshairVisible(false);
                }
                int stateInt2 = Storage.state().getInt(MapKeys.INT_MAP_SCROLL_DIRECTION);
                if (stateInt2 >= 0 && Storage.state().getLong(MapKeys.TIMESTAMP_MAP_SCROLL) == currentTime && !Storage.state().getBool(MapKeys.FLAG_MAP_SCROLLING)) {
                    Storage.state().setLong(MapKeys.MAP_SCROLL_LON, MapRenderer.currentLon);
                    Storage.state().setLong(MapKeys.MAP_SCROLL_LAT, MapRenderer.currentLat);
                    int stateInt3 = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
                    long scrollDelta = (MapUtils.getZoomNumerator(stateInt3) / MapUtils.getZoomDenominator(stateInt3)) * 9;
                    switch (stateInt2) {
                        case 0:
                            Storage.state().setLong(MapKeys.MAP_SCROLL_LON, Storage.state().getLong(MapKeys.MAP_SCROLL_LON) + scrollDelta);
                            break;
                        case 1:
                            Storage.state().setLong(MapKeys.MAP_SCROLL_LON, Storage.state().getLong(MapKeys.MAP_SCROLL_LON) - scrollDelta);
                            break;
                        case 2:
                            Storage.state().setLong(MapKeys.MAP_SCROLL_LAT, Storage.state().getLong(MapKeys.MAP_SCROLL_LAT) + scrollDelta);
                            break;
                        case 3:
                            Storage.state().setLong(MapKeys.MAP_SCROLL_LAT, Storage.state().getLong(MapKeys.MAP_SCROLL_LAT) - scrollDelta);
                            break;
                    }
                    MapRenderer.setPosition(Storage.state().getLong(MapKeys.MAP_SCROLL_LON), Storage.state().getLong(MapKeys.MAP_SCROLL_LAT));
                    TimerManager.setTimer(TimerManager.SLOT_MAP_CROSSHAIR, 500L);
                    MapRenderer.resetInteraction();
                }
                if (Storage.state().getLong(MapKeys.TIMESTAMP_MAP_SCROLL) == currentTime) {
                    MapRenderer.render();
                }
                if (Storage.state().getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE) && TimerManager.checkTimer(TimerManager.SLOT_MAP_IDLE, 300000L)) {
                    TimerManager.setTimer(TimerManager.SLOT_MAP_IDLE, 300000L);
                    StringUtils.clearSatelliteTiles();
                    Vector vec4 = Storage.state().getVector(MapKeys.SLOT_MAP_DATA);
                    int size3 = vec4.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            MapRenderer.needsRedraw = true;
                            new AsyncTask(AsyncTaskId.FETCH_CITY_ZOOM);
                            break;
                        } else if (TileRequest.TYPE_OVERLAY == ((TileRequest) vec4.elementAt(size3)).tileType) {
                            vec4.removeElementAt(size3);
                        }
                    }
                }
                if (Storage.state().getBool(MapKeys.FLAG_MAP_SCROLLING)) {
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
        if (Storage.state().getString(SessionKeys.SLOT_ACTIVE_PROTOCOL_NAME).equals(protocol)) {
            ScreenBuilder.onScreenClosed();
            if (MapController.hasRoutePoints()) {
                return 0;
            }
            return NotificationHelper.showError(354);
        }
        if (Storage.resources().getString(StringResKeys.STR_PROTOCOL_MRIM).equals(protocol)) {
            Storage.state().setInt(MapKeys.FLAG_GPS_ACTIVE, 1);
            XmppContactGroup.stopMapAnimation(Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE));
            MapRenderer.needsRedraw = true;
            return ScreenId.MAP;
        }
        if (!Storage.resources().getString(StringResKeys.STR_PROTOCOL_MMP).equals(protocol)) {
            return 0;
        }
        Storage.state().setInt(MapKeys.FLAG_GPS_ACTIVE, 0);
        XmppContactGroup.startMapAnimation(Storage.state().getVector(UIKeys.VEC_PHOTO_QUEUE));
        MapRenderer.needsRedraw = true;
        return ScreenId.MAP;
    }
}
