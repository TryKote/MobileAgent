package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.StateKeys;
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

    public static boolean mapInitialized;

    private static Screen mapScreen;

    public static ListItem activeMapItem;

    public static final void showMapScreen() {
        initMapState();
        AppState.setInt(StateKeys.INT_CONNECTION_STATE, 6);
        Screen c0013amM75b = ScreenManager.createScreen(ScreenDef.MAP_VIEW);
        mapScreen = c0013amM75b;
        setMapSoftKeys(c0013amM75b);
        ScreenManager.pushScreen(c0013amM75b);
        TabBar.ensureSearchTab();
        TabBar.findTab(6, (Account) null);
        TabBar.scrollEnabled = AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE);
        if (AppState.getBool(StateKeys.FLAG_REFRESH_CONTACTS)) {
            return;
        }
        ScreenBuilder.openScreen(ScreenId.EDIT_SCREEN);
    }

    public static final void updateMapSoftKeys() {
        if (AppState.getBool(StateKeys.FLAG_MAP_TILES_PENDING)) {
            if (AppState.getBool(StateKeys.FLAG_MAP_SCREEN_VISIBLE) || mapScreen == null) {
                return;
            }
            mapScreen.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_MAP), AppState.getString(StateKeys.STR_SOFTKEY_CLOSE), 167, 4, 167);
            AppState.setInt(StateKeys.FLAG_MAP_SCREEN_VISIBLE, 1);
            return;
        }
        if (!AppState.getBool(StateKeys.FLAG_MAP_SCREEN_VISIBLE) || mapScreen == null) {
            return;
        }
        setMapSoftKeys(mapScreen);
        AppState.setInt(StateKeys.FLAG_MAP_SCREEN_VISIBLE, 0);
    }

    private static final void initMapState() {
        if (mapInitialized) {
            return;
        }
        mapInitialized = true;
        int i = ScreenManager.createScreen(ScreenDef.MAP_VIEW).contentHeight;
        AppState.setLong(StateKeys.MAP_SCROLL_LON, 4178628L);
        AppState.setLong(StateKeys.MAP_SCROLL_LAT, 7482960L);
        AppState.pool[StateKeys.VEC_CONTACT_GROUPS] = XmppContactGroup.loadMapPoints(225);
        AppState.pool[StateKeys.VEC_PHOTO_QUEUE] = XmppContactGroup.loadMapPoints(226);
        AppState.setInt(StateKeys.MAP_VIEWPORT_WIDTH, AppState.getInt(StateKeys.INT_SCREEN_WIDTH));
        AppState.setInt(StateKeys.MAP_VIEWPORT_HEIGHT, i);
        AppState.setLong(StateKeys.MAP_SAVED_LONGITUDE, AppState.getLong(StateKeys.MAP_LONGITUDE));
        AppState.setLong(StateKeys.MAP_SAVED_LATITUDE, AppState.getLong(StateKeys.MAP_LATITUDE));
        MapRenderer.viewportWidth = AppState.getInt(StateKeys.MAP_VIEWPORT_WIDTH);
        MapRenderer.viewportHeight = AppState.getInt(StateKeys.MAP_VIEWPORT_HEIGHT);
        MapRenderer.currentLat = AppState.getLong(StateKeys.MAP_SAVED_LATITUDE);
        MapRenderer.currentLon = AppState.getLong(StateKeys.MAP_SAVED_LONGITUDE);
        int iM586d = AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
        MapRenderer.currentPixelX = MapUtils.coordToPixel(MapRenderer.currentLon, iM586d);
        MapRenderer.currentPixelY = MapUtils.coordToPixel(MapRenderer.currentLat, iM586d);
        AppState.pool[StateKeys.OBJ_FONT_2] = Image.createImage(MapRenderer.viewportWidth, MapRenderer.viewportHeight);
        StringUtils.initTileCache();
        AppState.pool[StateKeys.VEC_CHATROOM_LIST] = ObjectPool.newVector();
        AppState.pool[StateKeys.OBJ_SEARCH_PARAMS_1] = ObjectPool.newVector();
        Object[] objArrM332c = AppController.getUrlComponents(AppState.emptyStr);
        AppState.pool[StateKeys.OBJ_TILE_REQUEST_ARRAY] = objArrM332c;
        XmppContactGroup.addContactInfoToQueue(objArrM332c);
        Image imageCreateImage = Image.createImage(128, 128);
        Graphics graphics = imageCreateImage.getGraphics();
        int i2 = 0;
        graphics.setColor(13158600);
        for (int i3 = 0; i3 < 128; i3 += 2) {
            for (int i4 = i2; i4 < 128; i4 += 4) {
                graphics.fillRect(i3, i4, 2, 2);
            }
            i2 ^= 2;
        }
        new GraphicsContext(graphics).drawIcon(312, 56, 56);
        AppState.pool[StateKeys.OBJ_MENU_LABELS] = imageCreateImage;
        AppState.pool[StateKeys.OBJ_SEARCH_PARAMS_2] = ObjectPool.newVector();
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
        if (AppState.getBool(StateKeys.FLAG_GPS_ACTIVE)) {
            XmppContactGroup.stopMapAnimation(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE));
        }
        AppState.pool[StateKeys.SLOT_MAP_DATA] = ObjectPool.newVector();
        MapRenderer.needsRedraw = true;
        AppState.setLong(StateKeys.TIMESTAMP_MAP_SCROLL, System.currentTimeMillis() - 90);
        new AsyncTask(AsyncTaskId.FETCH_GEO_CONFIG);
        ServiceRegistry.loadSavedData();
    }

    private static final void setMapSoftKeys(Screen c0013am) {
        c0013am.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_MENU), AppState.getString(AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE) ? 1050 : 328), 20, 0, 0);
    }

    public static final void toggleMapControls(Screen c0013am) {
        if (AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            return;
        }
        toggleScrollMode();
        setMapSoftKeys(c0013am);
    }

    public static final int handleMapBack(Screen c0013am) {
        MrimAccount c0028ba;
        if (AppState.getBool(StateKeys.FLAG_MAP_TILES_PENDING)) {
            ((MrimAccount) AppState.getAccount()).isHighlighted = false;
            MapRenderer.needsRedraw = true;
            toggleScrollMode();
            return 0;
        }
        if (AppState.getBool(StateKeys.FLAG_MAP_LOADING) && (c0028ba = (MrimAccount) AppState.getAccount()) != null) {
            c0028ba.deselect();
        }
        toggleScrollMode();
        setMapSoftKeys(c0013am);
        return 0;
    }

    public static final void handleMapSwitch(Screen c0013am) {
        if (AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            AppState.setInt(StateKeys.INT_MAP_SCROLL_DIRECTION, 3);
        } else {
            toggleMapControls(c0013am);
        }
    }

    public static final void toggleScrollMode() {
        boolean z = !AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE);
        boolean z2 = z;
        AppState.setBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, z);
        if (!z2) {
            AppState.setInt(StateKeys.FLAG_MAP_LOADING, 0);
        }
        TabBar.scrollEnabled = z2;
    }

    public static final void navigateToPoint(MapPoint c0014an, boolean z) {
        initMapState();
        if (z) {
            XmppContactGroup.addMapPointIfNew(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), c0014an, 0, 5);
            XmppContactGroup.saveMapPoints(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), 225);
        }
        MapRenderer.selectedMapPoint = c0014an;
        MapRenderer.invalidate();
        MapRenderer.setPosition(MapRenderer.selectedMapPoint.longitude, MapRenderer.selectedMapPoint.latitude);
        MapRenderer.setZoom(MapRenderer.selectedMapPoint.zoomLevel);
        MapRenderer.selectedMapPoint.markActive();
        MapRenderer.resetInteraction();
    }

    public static final int showMapSearchResults() {
        Vector vectorM614m = AppState.getVector(StateKeys.VEC_MESSAGE_LIST);
        if (vectorM614m != null) {
            AppState.clearIndex(StateKeys.VEC_MESSAGE_LIST);
        }
        if (vectorM614m == null) {
            return 0;
        }
        AppController.needsRepaint = true;
        int size = vectorM614m.size();
        if (size == 0) {
            return NotificationHelper.showError(327);
        }
        Screen c0013amM75b = ScreenManager.createScreen(ScreenDef.MAP_OVERLAY);
        for (int i = 0; i < size; i++) {
            MapPoint c0014an = (MapPoint) vectorM614m.elementAt(i);
            c0013amM75b.addIconItemWithData(-1, c0014an.name, 6, c0014an);
        }
        ScreenManager.showScreen(c0013amM75b);
        return 0;
    }

    public static final Enumeration getRouteElements() {
        return AppState.getVector(StateKeys.VEC_PHOTO_QUEUE).elements();
    }

    public static final boolean hasRoutePoints() {
        return AppState.getVector(StateKeys.VEC_PHOTO_QUEUE).size() > 0;
    }

    public static final void removeRoutePoint(MapPoint c0014an) {
        Vector vectorM614m = AppState.getVector(StateKeys.VEC_PHOTO_QUEUE);
        vectorM614m.removeElement(c0014an);
        XmppContactGroup.saveMapPoints(vectorM614m, 226);
    }

    public static final void setRouteStart() {
        long jMo274v;
        long jMo275w;
        if (MapRenderer.hasRouteEndpoints()) {
            MmpContact.clearRouteProgress();
        }
        ListItem interfaceC0044o = MapRenderer.tooltipItem;
        if (interfaceC0044o == null || !interfaceC0044o.isSelected()) {
            jMo274v = MapRenderer.currentLon;
            jMo275w = MapRenderer.currentLat;
        } else {
            jMo274v = interfaceC0044o.getWidth();
            jMo275w = interfaceC0044o.getBaseHeight();
            interfaceC0044o.select();
        }
        MmpContact.setFirstToken(jMo274v, jMo275w);
        MapRenderer.needsRedraw = true;
        if (MapRenderer.hasRouteEndpoints()) {
            Conversation.loadContacts();
        }
    }

    public static final void setRouteEnd() {
        long jMo274v;
        long jMo275w;
        if (MapRenderer.hasRouteEndpoints()) {
            MmpContact.clearRouteProgress();
        }
        ListItem interfaceC0044o = MapRenderer.tooltipItem;
        if (interfaceC0044o == null || !interfaceC0044o.isSelected()) {
            jMo274v = MapRenderer.currentLon;
            jMo275w = MapRenderer.currentLat;
        } else {
            jMo274v = interfaceC0044o.getWidth();
            jMo275w = interfaceC0044o.getBaseHeight();
            interfaceC0044o.select();
        }
        MmpContact.setSecondToken(jMo274v, jMo275w);
        MapRenderer.needsRedraw = true;
        if (MapRenderer.hasRouteEndpoints()) {
            Conversation.loadContacts();
        }
    }

    public static final void selectMapItem(ListItem interfaceC0044o) {
        if (interfaceC0044o.isSelected()) {
            showMapView();
            MapRenderer.setPosition(interfaceC0044o.getWidth(), interfaceC0044o.getBaseHeight());
            MapRenderer.setZoom(interfaceC0044o.getCommandCount());
            activeMapItem = interfaceC0044o;
        }
    }

    public static final void showMapView() {
        initMapState();
        AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
        MapRenderer.invalidate();
    }
}
