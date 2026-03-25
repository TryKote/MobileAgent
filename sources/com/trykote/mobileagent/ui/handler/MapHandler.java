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
                Screen screen11 = ScreenManager.createScreen(ScreenDef.MAP_POINTS);
                Vector mapPoints = AppState.getVector(StateKeys.VEC_CONTACT_GROUPS);
                for (int i19 = 0; i19 < mapPoints.size(); i19++) {
                    MapPoint mapPoint = (MapPoint) mapPoints.elementAt(i19);
                    screen11.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
                }
                ScreenManager.showScreen(screen11);
                break;
            case ScreenId.MAP_TOOLTIP:
                AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_1, (Object) AppState.emptyStr);
                String tooltipText = MapRenderer.getTooltipText();
                if (tooltipText != null) {
                    AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_1, (Object) tooltipText);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_TOOLTIP));
                break;
            case ScreenId.PEOPLE_NEARBY:
                Vector allContacts = AccountManager.getAllAccountsList();
                int size10 = allContacts.size();
                while (true) {
                    size10--;
                    if (size10 < 0) {
                        if (allContacts.size() == 0) {
                            NotificationHelper.showMessageById(762);
                        } else {
                            AppController.sortContacts(allContacts);
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
                XmppMailRuProtocol.showMapContextMenu();
                break;
            case ScreenId.SAVE_LOCATION:
                AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_2, (Object) AppState.emptyStr);
                String tooltipText2 = MapRenderer.getTooltipText();
                if (tooltipText2 != null) {
                    AppState.setObject(StateKeys.SLOT_TOOLTIP_TEXT_2, (Object) tooltipText2);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SAVE_LOCATION));
                break;
            case ScreenId.MAP_ROUTE:
                Screen screen13 = ScreenManager.createScreen(ScreenDef.MAP_ROUTE);
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
                Screen screen14 = ScreenManager.createScreen(ScreenDef.MAP_ROUTE_SELECT);
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
                AppState.setInt(StateKeys.INT_SCREEN_BUILDER_ACTION, AppState.getBool(StateKeys.FLAG_MAP_MODE_ACTIVE) ? 407 : 408);
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_SEARCH));
                break;
            case ScreenId.SAVED_LOCATIONS:
                ResourceManager.showSavedLocations();
                break;
            case ScreenId.MAP_OPTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAP_OPTIONS));
                break;
        }
        AppController.finishScreenBuild();
    }

    public int onMenuItemSelected(Screen currentScreen, MenuItem menuItem, String title, int action, Object obj) {
        int nextScreen;
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
                if (!AppState.getBool(StateKeys.FLAG_MAP_TILES_PENDING)) {
                    MapController.toggleMapControls(currentScreen);
                }
                nextScreen = 0;
                break;
            case ScreenId.MAP_MENU:
                nextScreen = 0;
                break;
            case ScreenId.MAP_POINTS:
                nextScreen = IOUtils.handleMapSearch(action, obj);
                break;
            case ScreenId.MAP_TOOLTIP:
                ScreenManager.processScreenForm();
                nextScreen = StringUtils.isEmpty(Utils.defaultStr(AppState.getString(StateKeys.SLOT_TOOLTIP_TEXT_1))) ? NotificationHelper.showError(352) : 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                nextScreen = AppController.handleMapSearchAction(obj);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                nextScreen = XmppMailRuProtocol.handleMapAction(action);
                break;
            case ScreenId.SAVE_LOCATION:
                ScreenManager.processScreenForm();
                String locationName = Utils.defaultStr(AppState.getString(StateKeys.SLOT_TOOLTIP_TEXT_2));
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
                    MapPoint mapPoint = new MapPoint(locationName, 0L, 0L, 0L, 0L, lon, lat, AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
                    mapPoint.height = 4;
                    Vector screenStack = AppState.getVector(StateKeys.VEC_PHOTO_QUEUE);
                    XmppContactGroup.addMapPointIfNew(screenStack, mapPoint, 0, 50);
                    XmppContactGroup.saveMapPoints(screenStack, 226);
                    MapRenderer.navigateToMapPoint(mapPoint);
                    errorCode7 = 0;
                }
                nextScreen = errorCode7;
                break;
            case ScreenId.MAP_ROUTE:
                nextScreen = AppController.handleMapResultAction(obj);
                break;
            case ScreenId.MAP_STATUS:
                nextScreen = AppController.processLoginField(title);
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                nextScreen = AppController.handleIncomingCall(obj);
                break;
            case ScreenId.SHARE_LOCATION:
                ScreenManager.processScreenForm();
                String msgId = AppState.getString(StateKeys.SLOT_MSG_ID_1);
                long lon2 = MapRenderer.currentLon;
                long lat2 = MapRenderer.currentLat;
                ListItem tooltipItem2 = MapRenderer.tooltipItem;
                if (tooltipItem2 != null && tooltipItem2.isSelected()) {
                    lon2 = tooltipItem2.getWidth();
                    lat2 = tooltipItem2.getBaseHeight();
                    tooltipItem2.select();
                }
                String msgId2 = AppState.getString(StateKeys.SLOT_MSG_ID_2);
                long j = lon2;
                long j2 = lat2;
                if (msgId != null) {
                    XmppContactGroup.sharedContactList.addElement(new Object[]{msgId, new long[]{j, j2}, msgId2});
                }
                long j3 = lon2;
                long j4 = lat2;
                if (msgId != null) {
                    String sessionKey = Utils.defaultStr(AppState.getString(StateKeys.SESSION_KEY));
                    ByteBuffer requestBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MAP_POINT_ADD).writeUInt(15713).writeRawString(msgId).writeUInt(4022822).writeLongAsString(j3).writeUInt(4023078).writeLongAsString(j4).writeUInt(4023334).writeRawString(sessionKey).writeUInt(4023590).writeRawString(new ByteBuffer().writeRawString(sessionKey).writeCompressed(PackedStringKeys.TAG_SECRET).writeLongAsString(j3).encryptMD5().toHexString());
                    if (msgId2 != null) {
                        requestBuf.writeUInt(4023846).writeEncodedString(msgId2);
                    }
                    if (AppState.getBool(StateKeys.FLAG_REGISTRATION_DONE)) {
                        String msgId3 = AppState.getString(StateKeys.LAST_ACCOUNT_NAME);
                        if (Utils.nonEmpty(msgId3)) {
                            requestBuf.writeUInt(4024102).writeEncodedString(msgId3);
                        }
                    }
                    new AsyncTask(AsyncTaskId.HTTP_FIRE_AND_FORGET, requestBuf.getStringAndClear());
                }
                MapRenderer.needsRedraw = true;
                nextScreen = 0;
                break;
            case ScreenId.MAP_SEARCH:
                nextScreen = AppController.handleViewOption(action);
                break;
            case ScreenId.SAVED_LOCATIONS:
                nextScreen = ResourceManager.applyLocationProfile(obj);
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

    public int onMenuItemAction(Screen currentScreen, MenuItem menuItem, Object data) {
        int result;
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
                result = MapController.handleMapBack(currentScreen);
                break;
            case ScreenId.MAP_MENU:
                result = 0;
                break;
            case ScreenId.MAP_POINTS:
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                AppState.setInt(StateKeys.FLAG_LOADING, 0);
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
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                result = 0;
                break;
            case ScreenId.MAP_STATUS:
                result = 0;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                AppState.setInt(StateKeys.FLAG_CONTACTS_LOADED, 0);
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

    public void onScreenClosed(Screen screen) {
        switch (screen.screenId) {
            case ScreenId.MAP:
                TabBar.scrollEnabled = false;
                TabBar.removeSearchTab();
                break;
            case ScreenId.MAP_POINTS:
                AppState.clearIndex(StateKeys.SLOT_SEARCH_QUERY);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                XmppMailRuProtocol.mapContextItem = null;
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                break;
            case ScreenId.SHARE_LOCATION:
                AppState.clearIndex(StateKeys.SLOT_MSG_ID_1);
                AppState.clearIndex(StateKeys.SLOT_MSG_ID_2);
                break;
        }
    }

    public int onItemSelected(Screen screen, MenuItem menuItem, String title, int selectedOption,
                              Object data, Object headerData) {
        int actionResult;
        switch (screen.screenId) {
            case ScreenId.MAP:
                int i2;
                if (!AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
                    MapController.toggleMapControls(screen);
                    i2 = -1;
                } else if (AppState.getBool(StateKeys.FLAG_MAP_LOADING)) {
                    String lonStr = IOUtils.pixelToLongitude(MapRenderer.currentLon);
                    String latStr = IOUtils.pixelToLatitude(MapRenderer.currentLat);
                    AppState.setInt(StateKeys.FLAG_MAP_LOADING, 0);
                    ResourceManager.startGeoSearch(VCard.formatLocationUrl(AppState.getInt(StateKeys.MAP_ZOOM_LEVEL), lonStr, latStr), MapRenderer.currentLon, MapRenderer.currentLat);
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
                actionResult = IOUtils.handleMapPointAction(data);
                break;
            case ScreenId.MAP_TOOLTIP:
                actionResult = 0;
                break;
            case ScreenId.PEOPLE_NEARBY:
                actionResult = AppController.handleMapSearchAction(data);
                break;
            case ScreenId.MAP_CONTEXT_MENU:
                actionResult = XmppMailRuProtocol.handleMapAction(selectedOption);
                break;
            case ScreenId.SAVE_LOCATION:
                actionResult = 0;
                break;
            case ScreenId.MAP_ROUTE:
                actionResult = AppController.handleMapResultAction(data);
                break;
            case ScreenId.MAP_STATUS:
                actionResult = AppController.processLoginField(title);
                break;
            case ScreenId.MAP_ROUTE_SELECT:
                actionResult = AppController.handleIncomingCall(data);
                break;
            case ScreenId.SHARE_LOCATION:
                actionResult = 0;
                break;
            case ScreenId.MAP_SEARCH:
                actionResult = AppController.handleViewOption(selectedOption);
                break;
            case ScreenId.SAVED_LOCATIONS:
                actionResult = ResourceManager.applyLocationProfile(data);
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

    public int onIdleProcess(Screen currentScreen, MenuItem menuItem, Object data, String title) {
        boolean z5;
        long currentTime = System.currentTimeMillis();
        switch (currentScreen.screenId) {
            case ScreenId.MAP:
                if (currentTime - AppState.getLong(StateKeys.TIMESTAMP_MAP_SCROLL) > 45) {
                    AppState.setLong(StateKeys.TIMESTAMP_MAP_SCROLL, currentTime);
                }
                if (AppController.isTimerType(10) && MapRenderer.crosshairVisible) {
                    if (AppState.getBool(StateKeys.FLAG_MAP_VIEW_ACTIVE)) {
                        if ((MapRenderer.currentLon < VCard.staticTs1 || MapRenderer.currentLon > VCard.staticTs3 || MapRenderer.currentLat > VCard.staticTs4 || MapRenderer.currentLat < VCard.staticTs2 || ((long) AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)) != VCard.staticTs5) && AppState.getBool(StateKeys.FLAG_MAP_DATA_LOADED)) {
                            IOUtils.requestNearbyPeople();
                        }
                    }
                    MapRenderer.setCrosshairVisible(false);
                }
                int stateInt2 = AppState.getInt(StateKeys.INT_MAP_SCROLL_DIRECTION);
                if (stateInt2 >= 0 && AppState.getLong(StateKeys.TIMESTAMP_MAP_SCROLL) == currentTime && !AppState.getBool(StateKeys.FLAG_MAP_SCROLLING)) {
                    AppState.setLong(StateKeys.MAP_SCROLL_LON, MapRenderer.currentLon);
                    AppState.setLong(StateKeys.MAP_SCROLL_LAT, MapRenderer.currentLat);
                    int stateInt3 = AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
                    long scrollDelta = (MapUtils.getZoomNumerator(stateInt3) / MapUtils.getZoomDenominator(stateInt3)) * 9;
                    switch (stateInt2) {
                        case 0:
                            AppState.setLong(StateKeys.MAP_SCROLL_LON, AppState.getLong(StateKeys.MAP_SCROLL_LON) + scrollDelta);
                            break;
                        case 1:
                            AppState.setLong(StateKeys.MAP_SCROLL_LON, AppState.getLong(StateKeys.MAP_SCROLL_LON) - scrollDelta);
                            break;
                        case 2:
                            AppState.setLong(StateKeys.MAP_SCROLL_LAT, AppState.getLong(StateKeys.MAP_SCROLL_LAT) + scrollDelta);
                            break;
                        case 3:
                            AppState.setLong(StateKeys.MAP_SCROLL_LAT, AppState.getLong(StateKeys.MAP_SCROLL_LAT) - scrollDelta);
                            break;
                    }
                    MapRenderer.setPosition(AppState.getLong(StateKeys.MAP_SCROLL_LON), AppState.getLong(StateKeys.MAP_SCROLL_LAT));
                    AppController.setTimer(10, 500L);
                    MapRenderer.resetInteraction();
                }
                if (AppState.getLong(StateKeys.TIMESTAMP_MAP_SCROLL) == currentTime) {
                    MapRenderer.render();
                }
                if (AppState.getBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE) && AppController.checkTimer(7, 300000L)) {
                    AppController.setTimer(7, 300000L);
                    StringUtils.clearSatelliteTiles();
                    Vector vec4 = AppState.getVector(StateKeys.SLOT_MAP_DATA);
                    int size3 = vec4.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            MapRenderer.needsRedraw = true;
                            new AsyncTask(AsyncTaskId.FETCH_CITY_ZOOM);
                            break;
                        } else if (3 == ((ResourceManager) vec4.elementAt(size3)).tileType) {
                            vec4.removeElementAt(size3);
                        }
                    }
                }
                if (AppState.getBool(StateKeys.FLAG_MAP_SCROLLING)) {
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
}
