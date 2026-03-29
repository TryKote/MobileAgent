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
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* renamed from: ay */
/* loaded from: MobileAgent_3.9.jar:ay.class */
public abstract class MapRenderer {

    /* renamed from: a */
    public static int viewportWidth;

    /* renamed from: b */
    public static int viewportHeight;

    /* renamed from: c */
    public static long currentLat;

    /* renamed from: d */
    public static long currentLon;

    /* renamed from: e */
    public static long currentPixelX;

    /* renamed from: f */
    public static long currentPixelY;

    /* renamed from: g */
    public static Object syncLock;

    /* renamed from: h */
    public static boolean needsRedraw;

    /* renamed from: i */
    public static boolean crosshairVisible;

    /* renamed from: j */
    public static MapPoint selectedMapPoint;

    /* renamed from: k */
    public static ListItem tooltipItem;

    /* renamed from: l */
    public static boolean tooltipLocked;

    /* renamed from: m */
    public static Vector animationSteps;

    /* renamed from: u */
    private static int animationIndex;

    /* renamed from: v */
    private static long animationTimestamp;

    /* renamed from: n */
    public static int autoScrollCount;

    /* renamed from: o */
    public static long autoScrollTimestamp;

    /* renamed from: w */
    private static GeoRegion currentRegion;

    /* renamed from: p */
    public static boolean dragActive;

    /* renamed from: q */
    public static boolean tapConsumed;

    /* renamed from: r */
    public static long rippleTimestamp;

    /* renamed from: s */
    public static int rippleX;

    /* renamed from: t */
    public static int rippleY;

    /* renamed from: a */
    public static final void invalidate() {
        Storage.state().setBool(MapKeys.FLAG_TILE_CACHE_ENABLED, false);
        needsRedraw = true;
    }

    /* renamed from: f */
    private static final Image createCheckerboard() {
        Image cachedImage = Storage.state().getImage(MapKeys.OBJ_FONT_1);
        if (cachedImage != null) {
            return cachedImage;
        }
        Image image = Image.createImage(128, 128);
        Graphics graphics = image.getGraphics();
        graphics.setColor(13158600);
        int i = 0;
        int i2 = 0;
        while (i2 < 128) {
            for (int i3 = i; i3 < 128; i3 += 4) {
                graphics.fillRect(i2, i3, 2, 2);
            }
            i2 += 2;
            i ^= 2;
        }
        Storage.state().setObject(MapKeys.OBJ_FONT_1, image);
        return image;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v435, resolved type: p */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:456:0x1094  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void render() {
        long j = 0;
        int i;
        int i2;
        int i3 = 0;
        ListItem item;
        Vector poiVector;
        int size;
        Image markerIcon;
        int deltaY;
        Image pinIcon;
        int i4;
        Vector vector;
        int size2;
        Image profileImage;
        boolean z;
        if (selectedMapPoint != null) {
            MapPoint mapPoint = selectedMapPoint;
            if (mapPoint.dirty) {
                mapPoint.dirty = false;
                z = true;
            } else {
                z = false;
            }
            if (z) {
                needsRedraw = true;
            }
        }
        if (XmppContactGroup.checkAndClearSync()) {
            needsRedraw = true;
        }
        if (needsRedraw) {
            int i5 = (int) (currentPixelX - (viewportWidth / 2));
            int i6 = (int) (currentPixelY - (viewportHeight / 2));
            int i7 = (int) (currentPixelX + (viewportWidth / 2));
            int i8 = (int) (currentPixelY + (viewportHeight / 2));
            int i9 = i5 < 0 ? (i5 / 128) - 1 : i5 / 128;
            int i10 = i6 < 0 ? (i6 / 128) - 1 : i6 / 128;
            int i11 = i7 < 0 ? (i7 / 128) - 1 : i7 / 128;
            int i12 = i8 < 0 ? (i8 / 128) - 1 : i8 / 128;
            int i13 = (int) ((((i9 << 7) + 64) - currentPixelX) + (viewportWidth / 2));
            int i14 = (int) (viewportHeight - ((((i10 << 7) + 64) - currentPixelY) + (viewportHeight / 2)));
            Vector visibleTiles = ObjectPool.newVector();
            Graphics graphics = Storage.state().getImage(MapKeys.OBJ_FONT_2).getGraphics();
            int zoomLevel = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
            for (int i15 = i9; i15 <= i11; i15++) {
                for (int i16 = i10; i16 <= i12; i16++) {
                    TileRequest tile = new TileRequest(TileRequest.TYPE_MAP, zoomLevel, i15, i16);
                    TileRequest overlayTile = null;
                    visibleTiles.addElement(tile);
                    if (Storage.state().getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE) && zoomLevel > 8 && Storage.state().getBool(MapKeys.MAP_GPS_ENABLED) && StringUtils.isInSavedRegion(currentLon, currentLat)) {
                        TileRequest newOverlayTile = new TileRequest(TileRequest.TYPE_OVERLAY, zoomLevel, i15, i16);
                        overlayTile = newOverlayTile;
                        visibleTiles.addElement(newOverlayTile);
                    }
                    Image tileImage = StringUtils.getTileImage(tile);
                    Image overlayImage = overlayTile != null ? StringUtils.getTileImage(overlayTile) : null;
                    if (tileImage == null) {
                        tileImage = createCheckerboard();
                    }
                    graphics.drawImage(tileImage, i13 + (128 * (i15 - i9)), i14 - (128 * (i16 - i10)), 3);
                    if (overlayTile != null && tileImage != createCheckerboard()) {
                        if (overlayImage != null) {
                            graphics.drawImage(overlayImage, i13 + (128 * (i15 - i9)), i14 - (128 * (i16 - i10)), 3);
                        } else if (overlayImage == null && !Storage.state().getVector(RuntimeKeys.OBJ_SEARCH_PARAMS_1).contains(overlayTile)) {
                            int i17 = i13 + (128 * (i15 - i9));
                            int i18 = i14 - (128 * (i16 - i10));
                            int color = graphics.getColor();
                            graphics.setColor(6579300);
                            graphics.setStrokeStyle(1);
                            graphics.drawRect((i17 - 64) + 2, (i18 - 64) + 2, 124, 124);
                            graphics.setStrokeStyle(0);
                            graphics.setColor(color);
                            if (Storage.state().getBool(UIKeys.FLAG_SUPPORTS_ALPHA) && Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
                                int[] iArr = new int[128];
                                int i19 = 128;
                                while (true) {
                                    i19--;
                                    if (i19 < 0) {
                                        break;
                                    } else {
                                        iArr[i19] = 1006632960;
                                    }
                                }
                                int i20 = 128;
                                while (true) {
                                    i20--;
                                    if (i20 < 0) {
                                        break;
                                    } else {
                                        graphics.drawRGB(iArr, 0, 128, i17 - 64, (i18 - 64) + i20, 128, 1, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Vector loadedTiles = Storage.state().getVector(MapKeys.SLOT_MAP_DATA);
            int size3 = visibleTiles.size();
            for (int i21 = 0; i21 < size3; i21++) {
                TileRequest visibleTile = (TileRequest) visibleTiles.elementAt(i21);
                if (!loadedTiles.contains(visibleTile)) {
                    int i22 = visibleTile.tileType;
                    if (i22 == TileRequest.TYPE_MAP) {
                        Storage.state().addInt(MapKeys.COUNTER_MAP_CACHE_HIT, 1);
                    } else if (i22 == TileRequest.TYPE_OVERLAY) {
                        Storage.state().addInt(MapKeys.COUNTER_MAP_CACHE_MISS, 1);
                    }
                    loadedTiles.addElement(visibleTile);
                }
            }
            int size4 = loadedTiles.size();
            while (true) {
                size4--;
                if (size4 < 0) {
                    break;
                } else if (!visibleTiles.contains(loadedTiles.elementAt(size4))) {
                    loadedTiles.removeElementAt(size4);
                }
            }
            Vector currentTiles = Storage.state().getVector(RuntimeKeys.OBJ_SEARCH_PARAMS_2);
            synchronized (currentTiles) {
                currentTiles.removeAllElements();
                int size5 = visibleTiles.size();
                for (int i23 = 0; i23 < size5; i23++) {
                    currentTiles.addElement(visibleTiles.elementAt(i23));
                }
                ObjectPool.releaseVector(visibleTiles);
            }
            Vector infoLabels = Storage.state().getVector(MapKeys.VEC_TILE_QUEUE);
            synchronized (infoLabels) {
                int size6 = infoLabels.size();
                if (size6 > 0) {
                    String str = null;
                    int i24 = 0;
                    int i25 = size6;
                    while (true) {
                        i25--;
                        if (i25 < 0) {
                            break;
                        }
                        Object[] objArr = (Object[]) infoLabels.elementAt(i25);
                        int priority = ((Integer) objArr[0]).intValue();
                        if (priority > i24) {
                            i24 = priority;
                            str = (String) objArr[1];
                        }
                    }
                    Font font = graphics.getFont();
                    int color2 = graphics.getColor();
                    Font labelFont = Storage.state().getFont();
                    graphics.setFont(labelFont);
                    int colorScheme = Storage.state().getInt(SettingsKeys.SETTING_COLOR_THEME);
                    graphics.setColor(Storage.state().getInt(PaletteKeys.MAP_FILL + colorScheme));
                    int labelWidth = labelFont.stringWidth(str) + 10;
                    int labelHeight = Storage.state().getInt(UIKeys.INT_FONT_HEIGHT);
                    graphics.fillRoundRect(5, 5, labelWidth, labelHeight, 10, 10);
                    graphics.setColor(Storage.state().getInt(PaletteKeys.COLORS_BASE + colorScheme));
                    graphics.drawRoundRect(5, 5, labelWidth, labelHeight, 10, 10);
                    graphics.drawString(str, 10, 5, 20);
                    graphics.setFont(font);
                    graphics.setColor(color2);
                }
            }
            long j2 = currentPixelX;
            long j3 = currentPixelY;
            int i26 = viewportWidth;
            int i27 = viewportHeight;
            if (Storage.state().getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE) && !XmppContactGroup.isMapDataRecent() && zoomLevel >= 9 && (vector = XmppContactGroup.sharedContactList) != null && (size2 = vector.size()) != 0) {
                long j4 = (j2 - (i26 / 2)) / 32;
                long j5 = (j3 - (i26 / 2)) / 32;
                long j6 = (j2 + (i26 / 2)) / 32;
                long j7 = (j3 + (i26 / 2)) / 32;
                int i28 = ((int) (j6 - j4)) + 1;
                int[] iArr2 = new int[i28 * (((int) (j7 - j5)) + 1)];
                String str2 = null;
                int i29 = 0;
                int height = 0;
                for (int i30 = 0; i30 < size2; i30++) {
                    Object[] objArr2 = (Object[]) vector.elementAt(i30);
                    if (!ServiceRegistry.hiddenContacts.contains((String) objArr2[0])) {
                        long j8 = ((long[]) objArr2[1])[0];
                        long j9 = ((long[]) objArr2[1])[1];
                        long pixelX = MapUtils.coordToPixel(j8, zoomLevel);
                        long pixelY = MapUtils.coordToPixel(j9, zoomLevel);
                        int i31 = (int) (pixelX / 32);
                        int i32 = (int) (pixelY / 32);
                        if (i31 >= j4 && i31 <= j6 && i32 >= j5 && i32 <= j7 && (profileImage = ServiceRegistry.getProfileImage((String) objArr2[0])) != null) {
                            int i33 = (int) (i31 - j4);
                            int i34 = (int) (i32 - j5);
                            if (iArr2[(i34 * i28) + i33] == 0) {
                                int i35 = (int) ((i26 / 2) + (pixelX - j2));
                                int i36 = (int) ((i27 / 2) + (j3 - pixelY));
                                graphics.drawImage(profileImage, i35, i36, 3);
                                iArr2[(i34 * i28) + i33] = 1;
                                if (str2 == null && Utils.absLong(j2 - pixelX) < 20 && Utils.absLong(j3 - pixelY) < 20) {
                                    String str3 = (String) objArr2[2];
                                    if (Utils.nonEmpty(str3)) {
                                        str2 = str3;
                                        i29 = i35;
                                        height = (i36 - (profileImage.getHeight() / 2)) + 2;
                                    }
                                }
                            }
                        }
                    }
                }
                if (str2 != null) {
                    ChatRenderer.renderTooltip(graphics, str2, Storage.state().getFont(), i26 - 40, i29, height);
                }
            }
            MapPoint selectedPoint = selectedMapPoint;
            long j10 = currentPixelX;
            long j11 = currentPixelY;
            int i37 = viewportWidth;
            int i38 = viewportHeight;
            hideTooltip();
            if (selectedPoint != null && selectedPoint.selected && selectedPoint.getDisplayName() != null && Utils.absLong(j10 - selectedPoint.getLonAtZoom(zoomLevel)) <= i37 / 2 && Utils.absLong(j11 - selectedPoint.getLatAtZoom(zoomLevel)) <= i38 / 2) {
                int pinX = (int) ((i37 / 2) + (selectedPoint.getLonAtZoom(zoomLevel) - j10));
                int pinY = (int) ((i38 / 2) + (j11 - selectedPoint.getLatAtZoom(zoomLevel)));
                if (selectedPoint.height == 2) {
                    pinIcon = XmppContactGroup.getOrLoadImage(25);
                    i4 = 1;
                } else {
                    pinIcon = XmppContactGroup.getOrLoadImage(24);
                    i4 = 4;
                }
                graphics.drawImage(pinIcon, pinX, pinY, 32 | i4);
                if (Utils.absLong(j10 - selectedPoint.getLonAtZoom(zoomLevel)) >= 20 || Utils.absLong(j11 - selectedPoint.getLatAtZoom(zoomLevel)) >= 20) {
                    hideTooltip();
                } else {
                    showTooltip((ListItem) selectedPoint);
                }
            }
            long j12 = currentPixelX;
            long j13 = currentPixelY;
            int i39 = viewportWidth;
            int i40 = viewportHeight;
            Enumeration routeElements = MapController.getRouteElements();
            boolean z2 = false;
            MapPoint nearestRoute = null;
            while (routeElements.hasMoreElements()) {
                MapPoint routePoint = (MapPoint) routeElements.nextElement();
                if (Utils.absLong(j12 - routePoint.getLonAtZoom(zoomLevel)) < i39 / 2 && Utils.absLong(j13 - routePoint.getLatAtZoom(zoomLevel)) < i40 / 2 && routePoint.selected) {
                    graphics.drawImage(XmppContactGroup.getOrLoadImage(18), (int) ((i39 / 2) + (routePoint.getLonAtZoom(zoomLevel) - j12)), (int) ((i40 / 2) + (j13 - routePoint.getLatAtZoom(zoomLevel))), 36);
                    if (Utils.absLong(j12 - routePoint.getLonAtZoom(zoomLevel)) < 20 && (deltaY = (int) (j13 - routePoint.getLatAtZoom(zoomLevel))) < 20 && deltaY > -10 && !z2) {
                        nearestRoute = routePoint;
                        z2 = true;
                    }
                }
            }
            if (!hasTooltip()) {
                if (z2) {
                    showTooltip((ListItem) nearestRoute);
                } else {
                    hideTooltip();
                }
            }
            long j14 = currentPixelX;
            long j15 = currentPixelY;
            int i41 = viewportWidth;
            int i42 = viewportHeight;
            if (Storage.state().getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE) && Storage.state().getBool(MapKeys.FLAG_MAP_DATA_LOADED) && !XmppContactGroup.isMapDataRecent() && (poiVector = Storage.state().getVector(UIKeys.OBJ_HTTP_CALLBACK)) != null && (size = poiVector.size()) != 0) {
                ListItem nearestPoi = null;
                int currentZoom = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
                for (int i43 = 0; i43 < size; i43++) {
                    ListItem poiItem = (ListItem) poiVector.elementAt(i43);
                    if (poiItem.isSelected() && currentZoom == poiItem.getCommandCount()) {
                        long poiPixelX = poiItem.getCommandId(zoomLevel);
                        long poiPixelY = poiItem.executeCommand(zoomLevel);
                        int i44 = (int) ((i41 / 2) + (poiPixelX - j14));
                        int i45 = (int) ((i42 / 2) + (j15 - poiPixelY));
                        if (i44 > 0 && i44 < i41 && i45 > 0 && i45 < i42) {
                            if (poiItem.getHeight() == 8) {
                                int i46 = ((UserSearchResult) poiItem).gender;
                                markerIcon = (i46 == 1 || i46 == 0) ? XmppContactGroup.getOrLoadImage(27) : XmppContactGroup.getOrLoadImage(28);
                            } else {
                                markerIcon = XmppContactGroup.getOrLoadImage(23);
                            }
                            graphics.drawImage(markerIcon, i44, i45, 3);
                        }
                        if (Utils.absLong(j14 - poiPixelX) < 20 && Utils.absLong(j15 - poiPixelY) < 20 && nearestPoi == null) {
                            nearestPoi = poiItem;
                        }
                    }
                }
                if (!hasTooltip()) {
                    if (nearestPoi != null) {
                        showTooltip(nearestPoi);
                    } else {
                        hideTooltip();
                    }
                }
            }
            long j16 = currentPixelX;
            long j17 = currentPixelY;
            int i47 = viewportWidth;
            int i48 = viewportHeight;
            if (Storage.state().getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE) && Storage.state().getBool(MapKeys.FLAG_MAP_ROUTE_SEARCH) && !XmppContactGroup.isMapDataRecent()) {
                Vector mapContacts = ContactListManager.getMapContacts();
                int size7 = mapContacts.size();
                if (size7 > 0) {
                    long j18 = (j16 - (i47 / 2)) / 32;
                    long j19 = (j17 - (i47 / 2)) / 32;
                    long j20 = (j16 + (i47 / 2)) / 32;
                    long j21 = (j17 + (i47 / 2)) / 32;
                    ListItem activeContact = MapController.activeMapItem;
                    if (ChatRenderer.mapItems == null || j18 < ChatRenderer.coord1 || j19 < ChatRenderer.coord2 || j20 > ChatRenderer.coord3 || j21 > ChatRenderer.coord4) {
                        ChatRenderer.coord1 = j18 - 10;
                        ChatRenderer.coord2 = j19 - 10;
                        ChatRenderer.coord3 = j20 + 10;
                        ChatRenderer.coord4 = j21 + 10;
                        int i49 = ((int) (ChatRenderer.coord3 - ChatRenderer.coord1)) + 1;
                        ListItem[] gridItems = new ListItem[i49 * (((int) (ChatRenderer.coord4 - ChatRenderer.coord2)) + 1)];
                        ChatRenderer.mapItems = gridItems;
                        for (int i50 = 0; i50 < size7; i50++) {
                            ListItem contactItem = (ListItem) mapContacts.elementAt(i50);
                            if (contactItem.isSelected() && activeContact != contactItem) {
                                long contactPixelX = contactItem.getCommandId(zoomLevel);
                                long contactPixelY = contactItem.executeCommand(zoomLevel);
                                int i51 = (int) (contactPixelX / 32);
                                int i52 = (int) (contactPixelY / 32);
                                if (i51 >= ChatRenderer.coord1 && i51 <= ChatRenderer.coord3 && i52 >= ChatRenderer.coord2 && i52 <= ChatRenderer.coord4) {
                                    int i53 = (int) (i51 - ChatRenderer.coord1);
                                    int i54 = (int) (i52 - ChatRenderer.coord2);
                                    ListItem existingItem = gridItems[(i54 * i49) + i53];
                                    if (existingItem == null) {
                                        gridItems[(i54 * i49) + i53] = contactItem;
                                    } else if (existingItem.getHeight() == 5) {
                                        ((Conversation) existingItem).addItem(contactItem);
                                    } else if (existingItem.getHeight() == 3) {
                                        Conversation cluster = new Conversation();
                                        cluster.addItem(contactItem);
                                        cluster.addItem(existingItem);
                                        gridItems[(i54 * i49) + i53] = cluster;
                                    }
                                }
                            }
                        }
                    }
                    ListItem[] mapItemGrid = ChatRenderer.mapItems;
                    ListItem nearestContact = null;
                    int length = mapItemGrid.length;
                    while (true) {
                        length--;
                        if (length < 0) {
                            break;
                        }
                        ListItem gridItem = mapItemGrid[length];
                        if (gridItem != null) {
                            long gridPixelX = gridItem.getCommandId(zoomLevel);
                            long gridPixelY = gridItem.executeCommand(zoomLevel);
                            int i55 = (int) ((i47 / 2) + (gridPixelX - j16));
                            int i56 = (int) ((i48 / 2) + (j17 - gridPixelY));
                            int itemType = gridItem.getHeight();
                            Image contactIcon = itemType == 3 ? XmppContactGroup.getOrLoadImage(26) : itemType == 5 ? XmppContactGroup.getOrLoadImage(23) : null;
                            Image image = contactIcon;
                            if (contactIcon != null) {
                                graphics.drawImage(image, i55, i56, 3);
                            }
                            if (Utils.absLong(j16 - gridPixelX) < 20 && Utils.absLong(j17 - gridPixelY) < 20 && nearestContact == null) {
                                nearestContact = gridItem;
                            }
                        }
                    }
                    if (!hasTooltip()) {
                        if (nearestContact != null) {
                            showTooltip(nearestContact);
                        } else {
                            hideTooltip();
                        }
                    }
                }
                ObjectPool.releaseVector(mapContacts);
            }
            long j22 = currentPixelX;
            long j23 = currentPixelY;
            int i57 = viewportWidth;
            int i58 = viewportHeight;
            if (Storage.state().getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE) && Storage.state().getBool(MapKeys.FLAG_MAP_POI_SEARCH) && !XmppContactGroup.isMapDataRecent()) {
                Storage.state().setInt(MapKeys.FLAG_MAP_TILES_PENDING, 0);
                Vector mapProfiles = ContactListManager.getMapProfiles();
                int size8 = mapProfiles.size();
                if (size8 != 0) {
                    MrimAccount nearestProfile = null;
                    for (int i59 = 0; i59 < size8; i59++) {
                        MrimAccount profile = (MrimAccount) mapProfiles.elementAt(i59);
                        if (profile.isSelected()) {
                            long profilePixelX = profile.getCommandId(zoomLevel);
                            long profilePixelY = profile.executeCommand(zoomLevel);
                            int i60 = (int) ((i57 / 2) + (profilePixelX - j22));
                            int i61 = (int) ((i58 / 2) + (j23 - profilePixelY));
                            if (i60 > 0 && i60 < i57 && i61 > 0 && i61 < i58) {
                                graphics.drawImage(XmppContactGroup.getOrLoadImage(22), i60, i61, 3);
                            }
                            if (Utils.absLong(j22 - profilePixelX) < 20 && Utils.absLong(j23 - profilePixelY) < 20 && nearestProfile == null) {
                                nearestProfile = profile;
                            }
                        }
                    }
                    if (!hasTooltip()) {
                        if (nearestProfile != null) {
                            showTooltip(nearestProfile);
                            if (nearestProfile.profileManager.profile.dirty) {
                                Storage.state().setInt(MapKeys.FLAG_MAP_TILES_PENDING, 1);
                            }
                            Storage.state().setAccount(nearestProfile);
                        } else {
                            hideTooltip();
                        }
                    }
                    MapController.updateMapSoftKeys();
                }
            }
            long j24 = currentPixelX;
            long j25 = currentPixelY;
            int i62 = viewportWidth;
            int i63 = viewportHeight;
            if (Storage.state().getBool(MapKeys.FLAG_MAP_VIEW_ACTIVE) && !XmppContactGroup.isMapDataRecent() && (item = MapController.activeMapItem) != null) {
                long activePixelX = item.getCommandId(zoomLevel);
                long activePixelY = item.executeCommand(zoomLevel);
                graphics.drawImage(XmppContactGroup.getOrLoadImage(26), (int) ((i62 / 2) + (activePixelX - j24)), (int) ((i63 / 2) + (j25 - activePixelY)), 3);
                if (Utils.absLong(j24 - activePixelX) < 20 && Utils.absLong(j25 - activePixelY) < 20) {
                    showTooltip(item);
                }
            }
            ChatRenderer.renderMapOverlay(graphics, currentPixelX, currentPixelY, currentLon, currentLat, zoomLevel, viewportWidth, viewportHeight);
            ChatRenderer.renderBubble(graphics, viewportWidth, viewportHeight, zoomLevel, currentPixelX, currentPixelY, tooltipItem);
            ChatRenderer.renderMarker(graphics, currentPixelX, currentPixelY, zoomLevel, viewportWidth, viewportHeight, currentLat);
            int i64 = viewportWidth / 2;
            int i65 = viewportHeight / 2;
            if (crosshairVisible || Storage.state().getBool(MapKeys.FLAG_MAP_LOADING)) {
                int color3 = graphics.getColor();
                graphics.setColor(0);
                graphics.fillRect(i64 - 1, i65 - 7, 2, 5);
                graphics.fillRect(i64 - 1, i65 + 2, 2, 5);
                graphics.fillRect(i64 - 7, i65 - 1, 5, 2);
                graphics.fillRect(i64 + 2, i65 - 1, 5, 2);
                graphics.setColor(color3);
            }
            long j26 = currentLon;
            long j27 = currentLat;
            GeoRegion bestRegion = null;
            Vector regions = Storage.state().getVector(MapKeys.VEC_MAP_POINTS);
            int size9 = regions.size();
            while (true) {
                size9--;
                if (size9 < 0) {
                    break;
                }
                GeoRegion region = (GeoRegion) regions.elementAt(size9);
                if (region.containsPoint(j26, j27) && region.zoomLevel != -1) {
                    if (bestRegion != null) {
                        GeoRegion currentBest = bestRegion;
                        if (region.maxLat - region.minLat < currentBest.maxLat - currentBest.minLat && region.maxLon - region.minLon < currentBest.maxLon - currentBest.minLon) {
                        }
                    }
                    bestRegion = region;
                }
            }
            GeoRegion activeRegion = bestRegion;
            if (Storage.state().getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE)) {
                boolean showDetails = Storage.state().getBool(SettingsKeys.SETTING_CUSTOM_VIEW_MODE);
                int clipWidth = showDetails ? graphics.getClipWidth() - 4 : 18;
                int i66 = -1;
                int i67 = 0;
                boolean z3 = false;
                int i68 = 0;
                if (activeRegion != null) {
                    i66 = activeRegion.zoomLevel;
                    i67 = activeRegion.mapType;
                    if (i66 >= 0) {
                        z3 = true;
                        i68 = i66 <= 45 ? 65280 : (i66 <= 45 || i66 >= 75) ? 16711680 : 16361985;
                    }
                }
                if (activeRegion != null) {
                    i = activeRegion.zoomLevel;
                    i2 = activeRegion.mapType;
                } else {
                    i = -1;
                    i2 = -1;
                }
                if (ChatRenderer.offsetX != i || ChatRenderer.offsetY != i2) {
                    int i69 = i66;
                    StringBuffer sb = ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_MAP_INFO_PREFIX));
                    if (i69 < 0 || activeRegion == null) {
                        i3 = 975;
                    } else {
                        sb.append(i69);
                        if (i69 <= 4 || i69 >= 21) {
                            i3 = i69 % 10 == 1 ? 977 : (i69 % 10 <= 1 || i69 % 10 >= 5) ? 976 : 978;
                        }
                    }
                    Storage.state().setObject(MapKeys.SLOT_XMPP_SESSION_ID, (Object) ObjectPool.toStringAndRelease(sb.append(Storage.state().getString(i3))));
                    ChatRenderer.offsetX = i;
                    ChatRenderer.offsetY = i2;
                }
                String zoomText = Storage.state().getString(MapKeys.SLOT_XMPP_SESSION_ID);
                Font font2 = graphics.getFont();
                int color4 = graphics.getColor();
                Font zoomFont = Storage.state().getFont();
                graphics.setFont(zoomFont);
                int fontHeight = Storage.state().getInt(UIKeys.INT_FONT_HEIGHT);
                int schemeIndex = Storage.state().getInt(SettingsKeys.SETTING_COLOR_THEME);
                int borderColor = Storage.state().getInt(PaletteKeys.COLORS_BASE + schemeIndex);
                int i70 = fontHeight > 18 ? fontHeight : 18;
                int clipHeight = (graphics.getClipHeight() - i70) - 1;
                if (showDetails) {
                    graphics.setColor(Storage.state().getInt(PaletteKeys.MAP_FILL + schemeIndex));
                    graphics.fillRoundRect(2, clipHeight, clipWidth, i70, 10, 10);
                }
                graphics.setColor(borderColor);
                if (showDetails) {
                    graphics.drawRoundRect(2, clipHeight, clipWidth, i70, 10, 10);
                }
                int i71 = 0;
                if (z3) {
                    graphics.setColor(i68);
                    graphics.fillRoundRect(6, clipHeight + ((i70 - 10) / 2), 10, 10, 5, 5);
                    i71 = 10;
                    graphics.setColor(borderColor);
                    graphics.drawRoundRect(6, clipHeight + ((i70 - 10) / 2), 10, 10, 5, 5);
                    if (i67 > 0 && showDetails) {
                        new GraphicsContext(graphics).drawIcon(i67 == 1 ? 212 : 211, 20 + zoomFont.stringWidth(zoomText) + 4, clipHeight + ((i70 - 16) / 2));
                    }
                    graphics.setColor(borderColor);
                }
                if (showDetails) {
                    graphics.drawString(zoomText, i71 + 10, clipHeight + ((i70 - fontHeight) / 2), 20);
                }
                graphics.setColor(color4);
                graphics.setFont(font2);
            }
            ChatRenderer.renderScaleBar(graphics, zoomLevel, currentLat);
            int i72 = rippleX;
            int i73 = rippleY;
            long j28 = rippleTimestamp;
            j = j28;
            if (j28 != 0) {
                int elapsed = (int) (System.currentTimeMillis() - j28);
                j = j28;
                if (elapsed >= 200) {
                    int i74 = elapsed < 300 ? 40 : elapsed < 400 ? 80 : elapsed < 500 ? 120 : 140;
                    int color5 = graphics.getColor();
                    graphics.setColor(Storage.state().getInt(PaletteKeys.MAP_FILL + Storage.state().getInt(SettingsKeys.SETTING_COLOR_THEME)));
                    int i75 = i74;
                    graphics.fillArc(i72 - (i74 / 2), i73 - (i74 / 2), i75, i74, 0, 360);
                    graphics.setColor(color5);
                    j = i75;
                }
            }
            Storage.state().setInt(MapKeys.FLAG_MAP_SCROLLING, 1);
            if (rippleTimestamp == 0) {
                needsRedraw = false;
            }
        }
        if (TimerManager.checkTimer(11, 2000L)) {
            Storage.state().setInt(MapKeys.FLAG_TILES_READY, 0);
        }
        Vector vector2 = animationSteps;
        synchronized (vector2) {
            if (animationIndex <= 5 && vector2.size() > 0) {
                long now = System.currentTimeMillis();
                if (j - animationTimestamp > 80) {
                    long[] jArr = (long[]) vector2.elementAt(animationIndex);
                    setPosition(jArr[0], jArr[1]);
                    animationIndex++;
                    animationTimestamp = now;
                }
            }
        }
        if (autoScrollCount > 0 && !crosshairVisible) {
            long scrollNow = System.currentTimeMillis();
            if (scrollNow - autoScrollTimestamp > 80) {
                int scrollZoom = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
                setPosition(currentLon, currentLat + ((MapUtils.getZoomNumerator(scrollZoom) / MapUtils.getZoomDenominator(scrollZoom)) * 9));
                autoScrollCount -= 9;
                autoScrollTimestamp = scrollNow;
            }
        }
        if (Storage.state().getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE) && System.currentTimeMillis() - XmppContactGroup.lastUpdateTs > 600000 && Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY) && Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE) && !NetworkLock.isNetworkBusy()) {
            XmppContactGroup.initializeMapData();
        }
    }

    /* renamed from: a */
    public static final void setPosition(long j, long j2) {
        GeoRegion bestRegion;
        if (j2 == currentLat && j == currentLon) {
            return;
        }
        int zoomLevel = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        synchronized (syncLock) {
            currentLat = j2;
            Storage.state().setLong(MapKeys.MAP_LATITUDE, 37L);
            currentLon = j;
            Storage.state().setLong(MapKeys.MAP_LONGITUDE, j);
            currentPixelX = MapUtils.coordToPixel(j, zoomLevel);
            currentPixelY = MapUtils.coordToPixel(j2, zoomLevel);
            GeoRegion prevRegion = currentRegion;
            Vector regionList = Storage.state().getVector(MapKeys.VEC_MAP_POINTS);
            int idx = Utils.vectorSize(regionList);
            while (true) {
                idx--;
                if (idx < 0) {
                    bestRegion = null;
                    break;
                }
                GeoRegion candidate = (GeoRegion) regionList.elementAt(idx);
                if (candidate.containsPoint(j, j2)) {
                    bestRegion = candidate;
                    break;
                }
            }
            GeoRegion activeRegion = bestRegion;
            if (prevRegion != bestRegion) {
                if (Storage.state().getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE)) {
                    XmppContactGroup.initializeMapData();
                }
                currentRegion = activeRegion;
            }
            setZoom(clampZoom(zoomLevel));
        }
        needsRedraw = true;
    }

    /* renamed from: b */
    private static final int clampZoom(int i) {
        int i2;
        if (StringUtils.isInSavedRegion(currentLon, currentLat) || i <= 10) {
            return (currentRegion == null || i <= (i2 = currentRegion.precision)) ? i : i2;
        }
        return 10;
    }

    /* renamed from: a */
    public static final void setZoom(int i) {
        int zoomLevel = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        if (i == zoomLevel || i < 3 || i > 17) {
            return;
        }
        int clampedZoom = clampZoom(i);
        int i2 = clampedZoom != 8 ? clampedZoom : zoomLevel < clampedZoom ? 9 : 7;
        Storage.state().setInt(MapKeys.MAP_ZOOM_LEVEL, i2);
        currentPixelX = MapUtils.coordToPixel(currentLon, i2);
        currentPixelY = MapUtils.coordToPixel(currentLat, i2);
        resetInteraction();
        needsRedraw = true;
    }

    /* renamed from: a */
    public static final void setCrosshairVisible(boolean z) {
        if (crosshairVisible != z) {
            crosshairVisible = z;
            needsRedraw = true;
        }
    }

    /* renamed from: a */
    public static final void confirmMapPoint(MapPoint mapPoint) {
        if (Storage.state().getBool(MapKeys.FLAG_MAP_MODE_ACTIVE)) {
            MmpContact.setSecondToken(mapPoint.longitude, mapPoint.latitude);
        } else {
            MmpContact.setFirstToken(mapPoint.longitude, mapPoint.latitude);
        }
        needsRedraw = true;
        if (hasRouteEndpoints()) {
            Conversation.loadContacts();
        }
        mapPoint.markInactive();
        Storage.state().setInt(UIKeys.FLAG_NEW_MESSAGE, 0);
    }

    /* renamed from: b */
    public static final void navigateToMapPoint(MapPoint mapPoint) {
        invalidate();
        setPosition(mapPoint.longitude, mapPoint.latitude);
        setZoom(mapPoint.zoomLevel);
        mapPoint.markActive();
        resetInteraction();
    }

    /* renamed from: c */
    public static final String getTooltipText() {
        if (tooltipItem != null) {
            return tooltipItem.getText();
        }
        return null;
    }

    /* renamed from: d */
    public static final boolean hasRouteEndpoints() {
        return (MmpContact.lastTokenPair[0] > 0L ? 1 : (MmpContact.lastTokenPair[0] == 0L ? 0 : -1)) != 0 && (MmpContact.lastTokenPair[1] > 0L ? 1 : (MmpContact.lastTokenPair[1] == 0L ? 0 : -1)) != 0 && (MmpContact.currentTokenPair[0] > 0L ? 1 : (MmpContact.currentTokenPair[0] == 0L ? 0 : -1)) != 0 && (MmpContact.currentTokenPair[1] > 0L ? 1 : (MmpContact.currentTokenPair[1] == 0L ? 0 : -1)) != 0 && ((MmpContact.lastTokenPair[0] > MmpContact.currentTokenPair[0] ? 1 : (MmpContact.lastTokenPair[0] == MmpContact.currentTokenPair[0] ? 0 : -1)) != 0 || (MmpContact.lastTokenPair[1] > MmpContact.currentTokenPair[1] ? 1 : (MmpContact.lastTokenPair[1] == MmpContact.currentTokenPair[1] ? 0 : -1)) != 0);
    }

    /* renamed from: b */
    public static final void animateTo(long j, long j2) {
        synchronized (animationSteps) {
            animationSteps.removeAllElements();
            animationIndex = 0;
            long j3 = currentLon;
            long j4 = currentLat;
            long j5 = (j - j3) / 5;
            long j6 = (j2 - j4) / 5;
            for (int i = 0; i < 5; i++) {
                animationSteps.addElement(new long[]{j3 + (j5 * i), j4 + (j6 * i)});
            }
            animationSteps.addElement(new long[]{j, j2});
            animationTimestamp = System.currentTimeMillis();
        }
    }

    /* renamed from: g */
    private static boolean hasTooltip() {
        return tooltipItem != null;
    }

    /* renamed from: a */
    private static void showTooltip(ListItem item) {
        if (tooltipLocked) {
            return;
        }
        tooltipItem = item;
    }

    /* renamed from: h */
    private static void hideTooltip() {
        if (tooltipLocked) {
            return;
        }
        tooltipItem = null;
    }

    /* renamed from: e */
    public static final void resetInteraction() {
        setCrosshairVisible(true);
        tooltipLocked = false;
        autoScrollCount = 0;
    }

    /* renamed from: c */
    private static final int screenToTileX(int i) {
        return ((int) currentPixelX) + (i - (viewportWidth >> 1));
    }

    /* renamed from: d */
    private static final int screenToTileY(int i) {
        return ((int) currentPixelY) - (i - (viewportHeight >> 1));
    }

    /* renamed from: a */
    public static final void onTap(int i, int i2) {
        int[] iArr;
        rippleTimestamp = 0L;
        if (!dragActive) {
            if (tooltipItem != null && (iArr = ChatRenderer.buttonBounds) != null && i > iArr[0] && i < iArr[0] + iArr[2] && i2 > iArr[1] - (iArr[3] / 2) && i2 < iArr[1] + (iArr[3] / 2)) {
                tapConsumed = true;
                return;
            } else {
                int zoomLevel = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
                animateTo((int) MapUtils.pixelToCoord(screenToTileX(i), zoomLevel), (int) MapUtils.pixelToCoord(screenToTileY(i2), zoomLevel));
            }
        }
        needsRedraw = true;
    }

    /* renamed from: b */
    public static final void onDrag(int i, int i2) {
        tapConsumed = true;
        rippleTimestamp = 0L;
        int zoomLevel = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        setPosition((int) MapUtils.pixelToCoord(screenToTileX(i), zoomLevel), (int) MapUtils.pixelToCoord(screenToTileY(i2), zoomLevel));
        needsRedraw = true;
    }

    public static void paintOverlay(GraphicsContext g, int mapX, int mapY, int width, int height) {
        g.setClip(mapX, mapY, width, height);
        try {
            int viewportW = Storage.state().getInt(MapKeys.MAP_VIEWPORT_WIDTH);
            int viewportH = Storage.state().getInt(MapKeys.MAP_VIEWPORT_HEIGHT);
            Graphics gfx = g.graphics;
            gfx.drawImage(Storage.state().getImage(MapKeys.OBJ_FONT_2), viewportW >> 1, mapY + (viewportH >> 1), 3);
            if (!Storage.state().getBool(MapKeys.FLAG_MAP_OVERLAY_ACTIVE) && Storage.state().getBool(UIKeys.FLAG_SUPPORTS_ALPHA)) {
                int[] overlay = new int[viewportW];
                for (int col = viewportW - 1; col >= 0; col--) {
                    overlay[col] = 1006632960;
                }
                for (int row = viewportH - 1; row >= 0; row--) {
                    gfx.drawRGB(overlay, 0, viewportW, 0, mapY + row, viewportW, 1, true);
                }
            }
        } catch (Throwable unused) {
        }
        Storage.state().setInt(MapKeys.FLAG_MAP_SCROLLING, 0);
    }
}
