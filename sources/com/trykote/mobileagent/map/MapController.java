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

    private static ListView mapScreen;

    public static ListItem activeMapItem;

    public static ListItem mapContextItem;

    public static MapPoint pendingMapPoint;

    public static final void showMapScreen() {
        initMapState();
        AppState.setInt(StateKeys.INT_CONNECTION_STATE, 6);
        ListView c0013amM75b = ScreenManager.createScreen(ScreenDef.MAP_VIEW);
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
        Object[] objArrM332c = ResourceManager.getUrlComponents(AppState.emptyStr);
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

    private static final void setMapSoftKeys(ListView c0013am) {
        c0013am.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_MENU), AppState.getString(AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE) ? 1050 : 328), 20, 0, 0);
    }

    public static final void toggleMapControls(ListView c0013am) {
        if (AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            return;
        }
        toggleScrollMode();
        setMapSoftKeys(c0013am);
    }

    public static final int handleMapBack(ListView c0013am) {
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

    public static final void handleMapSwitch(ListView c0013am) {
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
        ListView c0013amM75b = ScreenManager.createScreen(ScreenDef.MAP_OVERLAY);
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

    public static final void showMapContextMenu() {
        ListItem item;
        int i = 3072;
        if (AccountManager.getOnlineMrimAccounts().size() > 0) {
            i = 11264;
        }
        if (mapContextItem != null) {
            item = mapContextItem;
        } else {
            item = MapRenderer.tooltipItem;
            mapContextItem = item;
        }
        ListItem item2 = item;
        if (item != null && item2.isSelected()) {
            switch (item2.getHeight()) {
                case 3:
                    i = 4384;
                    break;
                case 4:
                    i |= 3;
                    break;
                case 5:
                    i = 128;
                    break;
                case 6:
                    i = 2064;
                    break;
                case 7:
                    i = 64;
                    break;
                case 8:
                    i = 4640;
                    break;
                case 10:
                    i &= -1025;
                    break;
            }
        }
        if (!AppState.getBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE)) {
            i &= -1025;
        }
        int i2 = 1424;
        int i3 = 1;
        while (true) {
            int i4 = i3;
            if (i4 >= 16384) {
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.XMPP_MAP_CONTEXT));
                return;
            }
            int i5 = i2;
            i2++;
            AppState.setInt(i5, i & i4);
            i3 = i4 << 1;
        }
    }

    public static final int handleMapAction(int i) {
        long lon;
        long lat;
        ListItem item = mapContextItem;
        int itemType = item == null ? 0 : item.getHeight();
        switch (i) {
            case 0:
                AppState.setCurrentEntity(item);
                ScreenBuilder.onScreenClosed();
                return ScreenId.STATUS_INPUT;
            case 1:
                if (itemType == 8) {
                    AppState.setInt(StateKeys.INT_ASYNC_TASK_ID, 0);
                    AppController.openUserProfile((MrimAccount) null, ((UserSearchResult) item).userId);
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
                AppState.pool[StateKeys.SLOT_TEMP_OBJECT_1] = (Conversation) item;
                return ScreenId.FORM_LIST;
            case 5:
                ResourceManager.dialPhoneUrl(VCard.formatPhoneContactUrl((PhoneContact) item, 0), (PhoneContact) item, 0);
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
                    MmpContact.clearRouteProgress();
                }
                ListItem item2 = MapRenderer.tooltipItem;
                if (item2 == null || !item2.isSelected()) {
                    lon = MapRenderer.currentLon;
                    lat = MapRenderer.currentLat;
                } else {
                    lon = item2.getWidth();
                    lat = item2.getBaseHeight();
                    item2.select();
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
                AppState.setInt(StateKeys.FLAG_TYPING_HIDDEN, 0);
                AppState.setBool(StateKeys.FLAG_TYPING_VISIBLE, AppState.getBool(StateKeys.FLAG_TYPING_INDICATOR));
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
                ListItem tooltipItem3 = MapRenderer.tooltipItem;
                if (tooltipItem3 != null && tooltipItem3.isSelected()) {
                    tooltipItem3.select();
                }
                MapRenderer.needsRedraw = true;
                return ScreenId.MAP;
            case 14:
                setRouteStart();
                if (MmpContact.hasSecondToken()) {
                    return ScreenId.MAP;
                }
                AppState.setInt(StateKeys.FLAG_MAP_MODE_ACTIVE, 1);
                return ScreenId.MAP_SEARCH;
            case 15:
                setRouteEnd();
                if (MmpContact.hasFirstToken()) {
                    return ScreenId.MAP;
                }
                AppState.setInt(StateKeys.FLAG_MAP_MODE_ACTIVE, 0);
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
        AppState.setBool(StateKeys.FLAG_MAP_VIEW_ACTIVE, showMap);
        AppState.setBool(StateKeys.FLAG_CONTACT_LIST_ACTIVE, showList);
        if (!shouldInvalidate || !MapController.mapInitialized) {
            return;
        }
        int i = 11;
        while (true) {
            i--;
            if (i < 0) {
                MmpContact.clearLocationData();
                StringUtils.initTileCache();
                ServiceRegistry.clearPhotoCache();
                MapRenderer.needsRedraw = true;
                return;
            }
            XmppContactGroup.invalidateCachedImage(i + 18);
        }
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
        AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
        return 0;
    }

    public static final int handleMapPointAction(Object obj) {
        if (AppState.getBool(StateKeys.FLAG_NEW_MESSAGE)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 0;
        }
        if (!AppState.getBool(StateKeys.FLAG_LOADING)) {
            MapController.navigateToPoint((MapPoint) obj, true);
            return 0;
        }
        MapPoint mapPoint = (MapPoint) obj;
        ((MrimAccount) AppState.getAccount()).profileManager.setMapLocation(mapPoint);
        XmppContactGroup.addMapPointIfNew(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), mapPoint, 0, 5);
        XmppContactGroup.saveMapPoints(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), 225);
        AppState.setInt(StateKeys.FLAG_LOADING, 0);
        return ScreenId.PROFILE_EDIT;
    }

    public static final int handleViewOption(int optionId) {
        if (optionId == 120) {
            if (!MapController.hasRoutePoints()) {
                return NotificationHelper.showError(354);
            }
            AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 1);
            return 0;
        }
        if (optionId != 100) {
            return optionId == 0 ? ScreenId.MAP : 0;
        }
        AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 1);
        return 0;
    }

    public static final int handleSearchResultAction(Object regionObj) {
        MapRenderer.invalidate();
        GeoRegion region = (GeoRegion) regionObj;
        MapRenderer.setPosition(region.centerLat, region.centerLon);
        MapRenderer.setZoom(region == StringUtils.getGeoRegion() ? 3 : 11);
        return 0;
    }

    public static final int processSearchQuery(String str) {
        if (!AppState.getString(StateKeys.STR_PROTOCOL_XMPP).equals(str)) {
            return 0;
        }
        if (MapRenderer.selectedMapPoint != null) {
            MapRenderer.selectedMapPoint.markInactive();
        }
        XmppContactGroup.startMapAnimation(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE));
        AppState.setInt(StateKeys.FLAG_GPS_ACTIVE, 0);
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
        AppState.setFromBuffer(StateKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(locationData));
        return 0;
    }
}
