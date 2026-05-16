package com.trykote.mobileagent.map;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.UserSearchResult;
import com.trykote.mobileagent.net.NetworkLock;
import com.trykote.mobileagent.net.ServiceRegistry;
import com.trykote.mobileagent.net.RequestQueue;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.util.ImageCache;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.GraphicsContext;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.ui.Palette;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.util.Enumeration;
import java.util.Vector;

public abstract class MapRenderer {

    private static final int TILE_SIZE = 128;
    private static final int HALF_TILE = TILE_SIZE / 2;
    private static final int CLUSTER_CELL_SIZE = 32;
    private static final int TOOLTIP_PROXIMITY = 20;
    private static final int LABEL_PADDING = 10;
    private static final int LABEL_MARGIN = 5;
    private static final int LABEL_CORNER_RADIUS = 10;

    private static final int CHECKERBOARD_COLOR = 0xC8C8C8;
    private static final int MISSING_TILE_BORDER_COLOR = 0x646464;
    private static final int MISSING_TILE_OVERLAY_ARGB = 0x3C000000;
    private static final int CROSSHAIR_ARM_LENGTH = 5;
    private static final int CROSSHAIR_GAP = 2;

    private static final int SPEED_ZONE_GREEN = 0x00FF00;
    private static final int SPEED_ZONE_YELLOW = 0xF9C201;
    private static final int SPEED_ZONE_RED = 0xFF0000;
    private static final int SPEED_ZONE_MEDIUM_LIMIT = 45;
    private static final int SPEED_ZONE_HIGH_LIMIT = 75;
    private static final int SPEED_INDICATOR_SIZE = 10;
    private static final int SPEED_INDICATOR_CORNER = 5;
    private static final int MIN_STATUS_BAR_HEIGHT = 18;

    private static final int MIN_ZOOM = 3;
    private static final int MAX_ZOOM = 17;
    private static final int SKIP_ZOOM_LEVEL = 8;
    private static final int ANIMATION_FRAME_COUNT = 5;
    private static final int MAX_ANIMATION_STEPS = 5;
    private static final long ANIMATION_FRAME_INTERVAL_MS = 80L;
    private static final long MAP_DATA_REFRESH_INTERVAL_MS = 600000L;
    private static final long TILE_RETRY_INTERVAL_MS = 2000L;

    private static final int RIPPLE_DELAY_MS = 200;
    private static final int RIPPLE_STEP_1_MS = 300;
    private static final int RIPPLE_STEP_2_MS = 400;
    private static final int RIPPLE_STEP_3_MS = 500;
    private static final int RIPPLE_SIZE_1 = 40;
    private static final int RIPPLE_SIZE_2 = 80;
    private static final int RIPPLE_SIZE_3 = 120;
    private static final int RIPPLE_SIZE_4 = 140;

    private static final int MAP_CLAMP_ZOOM = 10;
    private static final int MIN_OVERLAY_ZOOM = 9;

    public static int viewportWidth;

    public static int viewportHeight;

    public static long currentLat;

    public static long currentLon;

    public static long currentPixelX;

    public static long currentPixelY;

    public static Object syncLock;

    public static boolean needsRedraw;

    public static boolean crosshairVisible;

    public static MapPoint selectedMapPoint;

    public static ListItem tooltipItem;

    public static boolean tooltipLocked;

    public static Vector animationSteps;

    private static int animationIndex;

    private static long animationTimestamp;

    public static int autoScrollCount;

    public static long autoScrollTimestamp;

    private static GeoRegion currentRegion;

    public static boolean dragActive;

    public static boolean tapConsumed;

    public static long rippleTimestamp;

    public static int rippleX;

    public static int rippleY;

    public static final void invalidate() {
        MapState.setTileCacheEnabled(false);
        needsRedraw = true;
    }

    private static final Image createCheckerboard() {
        Image cachedImage = (Image) MapState.getFont1();
        if (cachedImage != null) {
            return cachedImage;
        }
        Image image = Image.createImage(TILE_SIZE, TILE_SIZE);
        Graphics graphics = image.getGraphics();
        graphics.setColor(CHECKERBOARD_COLOR);
        int i = 0;
        int i2 = 0;
        while (i2 < TILE_SIZE) {
            for (int i3 = i; i3 < TILE_SIZE; i3 += 4) {
                graphics.fillRect(i2, i3, 2, 2);
            }
            i2 += 2;
            i ^= 2;
        }
        MapState.setFont1(image);
        return image;
    }
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
        if (RequestQueue.checkAndClearSync()) {
            needsRedraw = true;
        }
        if (needsRedraw) {
            int i5 = (int) (currentPixelX - (viewportWidth / 2));
            int i6 = (int) (currentPixelY - (viewportHeight / 2));
            int i7 = (int) (currentPixelX + (viewportWidth / 2));
            int i8 = (int) (currentPixelY + (viewportHeight / 2));
            int i9 = i5 < 0 ? (i5 / TILE_SIZE) - 1 : i5 / TILE_SIZE;
            int i10 = i6 < 0 ? (i6 / TILE_SIZE) - 1 : i6 / TILE_SIZE;
            int i11 = i7 < 0 ? (i7 / TILE_SIZE) - 1 : i7 / TILE_SIZE;
            int i12 = i8 < 0 ? (i8 / TILE_SIZE) - 1 : i8 / TILE_SIZE;
            int i13 = (int) ((((i9 << 7) + HALF_TILE) - currentPixelX) + (viewportWidth / 2));
            int i14 = (int) (viewportHeight - ((((i10 << 7) + HALF_TILE) - currentPixelY) + (viewportHeight / 2)));
            Vector visibleTiles = ObjectPool.newVector();
            Graphics graphics = ((Image) MapState.getFont2()).getGraphics();
            int zoomLevel = MapState.getZoomLevel();
            for (int i15 = i9; i15 <= i11; i15++) {
                for (int i16 = i10; i16 <= i12; i16++) {
                    TileRequest tile = new TileRequest(TileRequest.TYPE_MAP, zoomLevel, i15, i16);
                    TileRequest overlayTile = null;
                    visibleTiles.addElement(tile);
                    if (ContactState.isListActive() && zoomLevel > SKIP_ZOOM_LEVEL && MapState.isGpsEnabled() && StringUtils.isInSavedRegion(currentLon, currentLat)) {
                        TileRequest newOverlayTile = new TileRequest(TileRequest.TYPE_OVERLAY, zoomLevel, i15, i16);
                        overlayTile = newOverlayTile;
                        visibleTiles.addElement(newOverlayTile);
                    }
                    Image tileImage = StringUtils.getTileImage(tile);
                    Image overlayImage = overlayTile != null ? StringUtils.getTileImage(overlayTile) : null;
                    if (tileImage == null) {
                        tileImage = createCheckerboard();
                    }
                    graphics.drawImage(tileImage, i13 + (TILE_SIZE * (i15 - i9)), i14 - (TILE_SIZE * (i16 - i10)), 3);
                    if (overlayTile != null && tileImage != createCheckerboard()) {
                        if (overlayImage != null) {
                            graphics.drawImage(overlayImage, i13 + (TILE_SIZE * (i15 - i9)), i14 - (TILE_SIZE * (i16 - i10)), 3);
                        } else if (overlayImage == null && !RuntimeState.getSearchParams1().contains(overlayTile)) {
                            int i17 = i13 + (TILE_SIZE * (i15 - i9));
                            int i18 = i14 - (TILE_SIZE * (i16 - i10));
                            int color = graphics.getColor();
                            graphics.setColor(MISSING_TILE_BORDER_COLOR);
                            graphics.setStrokeStyle(1);
                            graphics.drawRect((i17 - HALF_TILE) + 2, (i18 - HALF_TILE) + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                            graphics.setStrokeStyle(0);
                            graphics.setColor(color);
                            if (UIState.isSupportsAlpha() && MapState.isMapOverlayActive()) {
                                int[] iArr = new int[TILE_SIZE];
                                for (int i19 = TILE_SIZE - 1; i19 >= 0; i19--) {
                                    iArr[i19] = MISSING_TILE_OVERLAY_ARGB;
                                }
                                for (int i20 = TILE_SIZE - 1; i20 >= 0; i20--) {
                                    graphics.drawRGB(iArr, 0, TILE_SIZE, i17 - HALF_TILE, (i18 - HALF_TILE) + i20, TILE_SIZE, 1, true);
                                }
                            }
                        }
                    }
                }
            }
            Vector loadedTiles = MapState.getMapData();
            int size3 = visibleTiles.size();
            for (int i21 = 0; i21 < size3; i21++) {
                TileRequest visibleTile = (TileRequest) visibleTiles.elementAt(i21);
                if (!loadedTiles.contains(visibleTile)) {
                    int i22 = visibleTile.tileType;
                    if (i22 == TileRequest.TYPE_MAP) {
                        MapState.addCacheHit();
                    } else if (i22 == TileRequest.TYPE_OVERLAY) {
                        MapState.addCacheMiss();
                    }
                    loadedTiles.addElement(visibleTile);
                }
            }
            for (int idx = loadedTiles.size() - 1; idx >= 0; idx--) {
                if (!visibleTiles.contains(loadedTiles.elementAt(idx))) {
                    loadedTiles.removeElementAt(idx);
                }
            }
            Vector currentTiles = RuntimeState.getSearchParams2();
            synchronized (currentTiles) {
                currentTiles.removeAllElements();
                int size5 = visibleTiles.size();
                for (int i23 = 0; i23 < size5; i23++) {
                    currentTiles.addElement(visibleTiles.elementAt(i23));
                }
                ObjectPool.releaseVector(visibleTiles);
            }
            Vector infoLabels = MapState.getTileQueue();
            synchronized (infoLabels) {
                int size6 = infoLabels.size();
                if (size6 > 0) {
                    String str = null;
                    int i24 = 0;
                    for (int i25 = size6 - 1; i25 >= 0; i25--) {
                        Object[] objArr = (Object[]) infoLabels.elementAt(i25);
                        int priority = ((Integer) objArr[0]).intValue();
                        if (priority > i24) {
                            i24 = priority;
                            str = (String) objArr[1];
                        }
                    }
                    Font font = graphics.getFont();
                    int color2 = graphics.getColor();
                    Font labelFont = UIState.getFont();
                    graphics.setFont(labelFont);
                    int colorScheme = SettingsState.getColorTheme();
                    graphics.setColor(Palette.getColor(colorScheme, Palette.MAP_FILL));
                    int labelWidth = labelFont.stringWidth(str) + LABEL_PADDING;
                    int labelHeight = UIState.getFontHeight();
                    graphics.fillRoundRect(LABEL_MARGIN, LABEL_MARGIN, labelWidth, labelHeight, LABEL_CORNER_RADIUS, LABEL_CORNER_RADIUS);
                    graphics.setColor(Palette.getColor(colorScheme, Palette.TEXT));
                    graphics.drawRoundRect(LABEL_MARGIN, LABEL_MARGIN, labelWidth, labelHeight, LABEL_CORNER_RADIUS, LABEL_CORNER_RADIUS);
                    graphics.drawString(str, LABEL_PADDING, LABEL_MARGIN, 20);
                    graphics.setFont(font);
                    graphics.setColor(color2);
                }
            }
            long j2 = currentPixelX;
            long j3 = currentPixelY;
            int i26 = viewportWidth;
            int i27 = viewportHeight;
            if (ContactState.isListActive() && !MapPointStore.isMapDataRecent() && zoomLevel >= MIN_OVERLAY_ZOOM && (vector = RequestQueue.sharedContactList) != null && (size2 = vector.size()) != 0) {
                long j4 = (j2 - (i26 / 2)) / CLUSTER_CELL_SIZE;
                long j5 = (j3 - (i26 / 2)) / CLUSTER_CELL_SIZE;
                long j6 = (j2 + (i26 / 2)) / CLUSTER_CELL_SIZE;
                long j7 = (j3 + (i26 / 2)) / CLUSTER_CELL_SIZE;
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
                        int i31 = (int) (pixelX / CLUSTER_CELL_SIZE);
                        int i32 = (int) (pixelY / CLUSTER_CELL_SIZE);
                        if (i31 >= j4 && i31 <= j6 && i32 >= j5 && i32 <= j7 && (profileImage = ServiceRegistry.getProfileImage((String) objArr2[0])) != null) {
                            int i33 = (int) (i31 - j4);
                            int i34 = (int) (i32 - j5);
                            if (iArr2[(i34 * i28) + i33] == 0) {
                                int i35 = (int) ((i26 / 2) + (pixelX - j2));
                                int i36 = (int) ((i27 / 2) + (j3 - pixelY));
                                graphics.drawImage(profileImage, i35, i36, 3);
                                iArr2[(i34 * i28) + i33] = 1;
                                if (str2 == null && Utils.absLong(j2 - pixelX) < TOOLTIP_PROXIMITY && Utils.absLong(j3 - pixelY) < TOOLTIP_PROXIMITY) {
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
                    ChatRenderer.renderTooltip(graphics, str2, UIState.getFont(), i26 - (TOOLTIP_PROXIMITY * 2), i29, height);
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
                    pinIcon = ImageCache.getOrLoadImage(25);
                    i4 = 1;
                } else {
                    pinIcon = ImageCache.getOrLoadImage(24);
                    i4 = 4;
                }
                graphics.drawImage(pinIcon, pinX, pinY, 32 | i4);
                if (Utils.absLong(j10 - selectedPoint.getLonAtZoom(zoomLevel)) >= TOOLTIP_PROXIMITY || Utils.absLong(j11 - selectedPoint.getLatAtZoom(zoomLevel)) >= TOOLTIP_PROXIMITY) {
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
                    graphics.drawImage(ImageCache.getOrLoadImage(18), (int) ((i39 / 2) + (routePoint.getLonAtZoom(zoomLevel) - j12)), (int) ((i40 / 2) + (j13 - routePoint.getLatAtZoom(zoomLevel))), 36);
                    if (Utils.absLong(j12 - routePoint.getLonAtZoom(zoomLevel)) < TOOLTIP_PROXIMITY && (deltaY = (int) (j13 - routePoint.getLatAtZoom(zoomLevel))) < TOOLTIP_PROXIMITY && deltaY > -10 && !z2) {
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
            if (MapState.isMapViewActive() && MapState.isMapDataLoaded() && !MapPointStore.isMapDataRecent() && (poiVector = (Vector) UIState.getHttpCallback()) != null && (size = poiVector.size()) != 0) {
                ListItem nearestPoi = null;
                int currentZoom = MapState.getZoomLevel();
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
                                markerIcon = (i46 == 1 || i46 == 0) ? ImageCache.getOrLoadImage(27) : ImageCache.getOrLoadImage(28);
                            } else {
                                markerIcon = ImageCache.getOrLoadImage(23);
                            }
                            graphics.drawImage(markerIcon, i44, i45, 3);
                        }
                        if (Utils.absLong(j14 - poiPixelX) < TOOLTIP_PROXIMITY && Utils.absLong(j15 - poiPixelY) < TOOLTIP_PROXIMITY && nearestPoi == null) {
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
            if (MapState.isMapViewActive() && MapState.isRouteSearch() && !MapPointStore.isMapDataRecent()) {
                Vector mapContacts = ContactListManager.getMapContacts();
                int size7 = mapContacts.size();
                if (size7 > 0) {
                    long j18 = (j16 - (i47 / 2)) / CLUSTER_CELL_SIZE;
                    long j19 = (j17 - (i47 / 2)) / CLUSTER_CELL_SIZE;
                    long j20 = (j16 + (i47 / 2)) / CLUSTER_CELL_SIZE;
                    long j21 = (j17 + (i47 / 2)) / CLUSTER_CELL_SIZE;
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
                                int i51 = (int) (contactPixelX / CLUSTER_CELL_SIZE);
                                int i52 = (int) (contactPixelY / CLUSTER_CELL_SIZE);
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
                    for (int idx = mapItemGrid.length - 1; idx >= 0; idx--) {
                        ListItem gridItem = mapItemGrid[idx];
                        if (gridItem != null) {
                            long gridPixelX = gridItem.getCommandId(zoomLevel);
                            long gridPixelY = gridItem.executeCommand(zoomLevel);
                            int i55 = (int) ((i47 / 2) + (gridPixelX - j16));
                            int i56 = (int) ((i48 / 2) + (j17 - gridPixelY));
                            int itemType = gridItem.getHeight();
                            Image contactIcon = itemType == 3 ? ImageCache.getOrLoadImage(26) : itemType == 5 ? ImageCache.getOrLoadImage(23) : null;
                            Image image = contactIcon;
                            if (contactIcon != null) {
                                graphics.drawImage(image, i55, i56, 3);
                            }
                            if (Utils.absLong(j16 - gridPixelX) < TOOLTIP_PROXIMITY && Utils.absLong(j17 - gridPixelY) < TOOLTIP_PROXIMITY && nearestContact == null) {
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
            if (MapState.isMapViewActive() && MapState.isPoiSearch() && !MapPointStore.isMapDataRecent()) {
                MapState.setTilesPending(false);
                Vector mapProfiles = ContactListManager.getMapProfiles();
                int size8 = mapProfiles.size();
                if (size8 != 0) {
                    Account nearestProfile = null;
                    for (int i59 = 0; i59 < size8; i59++) {
                        Account profile = (Account) mapProfiles.elementAt(i59);
                        ListItem profileItem = profile.asListItem();
                        if (profileItem != null && profileItem.isSelected()) {
                            long profilePixelX = profileItem.getCommandId(zoomLevel);
                            long profilePixelY = profileItem.executeCommand(zoomLevel);
                            int i60 = (int) ((i57 / 2) + (profilePixelX - j22));
                            int i61 = (int) ((i58 / 2) + (j23 - profilePixelY));
                            if (i60 > 0 && i60 < i57 && i61 > 0 && i61 < i58) {
                                graphics.drawImage(ImageCache.getOrLoadImage(22), i60, i61, 3);
                            }
                            if (Utils.absLong(j22 - profilePixelX) < TOOLTIP_PROXIMITY && Utils.absLong(j23 - profilePixelY) < TOOLTIP_PROXIMITY && nearestProfile == null) {
                                nearestProfile = profile;
                            }
                        }
                    }
                    if (!hasTooltip()) {
                        if (nearestProfile != null) {
                            showTooltip(nearestProfile.asListItem());
                            if (nearestProfile.isProfileDirty()) {
                                MapState.setTilesPending(true);
                            }
                            AppState.setAccount(nearestProfile);
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
            if (MapState.isMapViewActive() && !MapPointStore.isMapDataRecent() && (item = MapController.activeMapItem) != null) {
                long activePixelX = item.getCommandId(zoomLevel);
                long activePixelY = item.executeCommand(zoomLevel);
                graphics.drawImage(ImageCache.getOrLoadImage(26), (int) ((i62 / 2) + (activePixelX - j24)), (int) ((i63 / 2) + (j25 - activePixelY)), 3);
                if (Utils.absLong(j24 - activePixelX) < TOOLTIP_PROXIMITY && Utils.absLong(j25 - activePixelY) < TOOLTIP_PROXIMITY) {
                    showTooltip(item);
                }
            }
            ChatRenderer.renderMapOverlay(graphics, currentPixelX, currentPixelY, currentLon, currentLat, zoomLevel, viewportWidth, viewportHeight);
            ChatRenderer.renderBubble(graphics, viewportWidth, viewportHeight, zoomLevel, currentPixelX, currentPixelY, tooltipItem);
            ChatRenderer.renderMarker(graphics, currentPixelX, currentPixelY, zoomLevel, viewportWidth, viewportHeight, currentLat);
            int i64 = viewportWidth / 2;
            int i65 = viewportHeight / 2;
            if (crosshairVisible || MapState.isMapLoading()) {
                int color3 = graphics.getColor();
                graphics.setColor(0);
                graphics.fillRect(i64 - 1, i65 - (CROSSHAIR_ARM_LENGTH + CROSSHAIR_GAP), 2, CROSSHAIR_ARM_LENGTH);
                graphics.fillRect(i64 - 1, i65 + CROSSHAIR_GAP, 2, CROSSHAIR_ARM_LENGTH);
                graphics.fillRect(i64 - (CROSSHAIR_ARM_LENGTH + CROSSHAIR_GAP), i65 - 1, CROSSHAIR_ARM_LENGTH, 2);
                graphics.fillRect(i64 + CROSSHAIR_GAP, i65 - 1, CROSSHAIR_ARM_LENGTH, 2);
                graphics.setColor(color3);
            }
            long j26 = currentLon;
            long j27 = currentLat;
            GeoRegion bestRegion = null;
            Vector regions = MapState.getMapPoints();
            for (int idx = regions.size() - 1; idx >= 0; idx--) {
                GeoRegion region = (GeoRegion) regions.elementAt(idx);
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
            if (ContactState.isListActive()) {
                boolean showDetails = SettingsState.getCustomViewMode() != 0;
                int clipWidth = showDetails ? graphics.getClipWidth() - 4 : MIN_STATUS_BAR_HEIGHT;
                int i66 = -1;
                int i67 = 0;
                boolean z3 = false;
                int i68 = 0;
                if (activeRegion != null) {
                    i66 = activeRegion.zoomLevel;
                    i67 = activeRegion.mapType;
                    if (i66 >= 0) {
                        z3 = true;
                        i68 = i66 <= SPEED_ZONE_MEDIUM_LIMIT ? SPEED_ZONE_GREEN : (i66 <= SPEED_ZONE_MEDIUM_LIMIT || i66 >= SPEED_ZONE_HIGH_LIMIT) ? SPEED_ZONE_RED : SPEED_ZONE_YELLOW;
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
                    StringBuffer sb = ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_MAP_INFO_PREFIX));
                    if (i69 < 0 || activeRegion == null) {
                        i3 = 975;
                    } else {
                        sb.append(i69);
                        if (i69 <= 4 || i69 >= 21) {
                            i3 = i69 % 10 == 1 ? 977 : (i69 % 10 <= 1 || i69 % 10 >= 5) ? 976 : 978;
                        }
                    }
                    MapState.setXmppSessionId(ObjectPool.toStringAndRelease(sb.append(AppState.getString(i3))));
                    ChatRenderer.offsetX = i;
                    ChatRenderer.offsetY = i2;
                }
                String zoomText = MapState.getXmppSessionId();
                Font font2 = graphics.getFont();
                int color4 = graphics.getColor();
                Font zoomFont = UIState.getFont();
                graphics.setFont(zoomFont);
                int fontHeight = UIState.getFontHeight();
                int schemeIndex = SettingsState.getColorTheme();
                int borderColor = Palette.getColor(schemeIndex, Palette.TEXT);
                int i70 = fontHeight > MIN_STATUS_BAR_HEIGHT ? fontHeight : MIN_STATUS_BAR_HEIGHT;
                int clipHeight = (graphics.getClipHeight() - i70) - 1;
                if (showDetails) {
                    graphics.setColor(Palette.getColor(schemeIndex, Palette.MAP_FILL));
                    graphics.fillRoundRect(2, clipHeight, clipWidth, i70, LABEL_CORNER_RADIUS, LABEL_CORNER_RADIUS);
                }
                graphics.setColor(borderColor);
                if (showDetails) {
                    graphics.drawRoundRect(2, clipHeight, clipWidth, i70, LABEL_CORNER_RADIUS, LABEL_CORNER_RADIUS);
                }
                int i71 = 0;
                if (z3) {
                    graphics.setColor(i68);
                    graphics.fillRoundRect(6, clipHeight + ((i70 - SPEED_INDICATOR_SIZE) / 2), SPEED_INDICATOR_SIZE, SPEED_INDICATOR_SIZE, SPEED_INDICATOR_CORNER, SPEED_INDICATOR_CORNER);
                    i71 = SPEED_INDICATOR_SIZE;
                    graphics.setColor(borderColor);
                    graphics.drawRoundRect(6, clipHeight + ((i70 - SPEED_INDICATOR_SIZE) / 2), SPEED_INDICATOR_SIZE, SPEED_INDICATOR_SIZE, SPEED_INDICATOR_CORNER, SPEED_INDICATOR_CORNER);
                    if (i67 > 0 && showDetails) {
                        new GraphicsContext(graphics).drawIcon(i67 == 1 ? 212 : 211, 20 + zoomFont.stringWidth(zoomText) + 4, clipHeight + ((i70 - 16) / 2));
                    }
                    graphics.setColor(borderColor);
                }
                if (showDetails) {
                    graphics.drawString(zoomText, i71 + LABEL_PADDING, clipHeight + ((i70 - fontHeight) / 2), 20);
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
                if (elapsed >= RIPPLE_DELAY_MS) {
                    int i74 = elapsed < RIPPLE_STEP_1_MS ? RIPPLE_SIZE_1 : elapsed < RIPPLE_STEP_2_MS ? RIPPLE_SIZE_2 : elapsed < RIPPLE_STEP_3_MS ? RIPPLE_SIZE_3 : RIPPLE_SIZE_4;
                    int color5 = graphics.getColor();
                    graphics.setColor(Palette.getColor(SettingsState.getColorTheme(), Palette.MAP_FILL));
                    int i75 = i74;
                    graphics.fillArc(i72 - (i74 / 2), i73 - (i74 / 2), i75, i74, 0, 360);
                    graphics.setColor(color5);
                    j = i75;
                }
            }
            MapState.setScrolling(true);
            if (rippleTimestamp == 0) {
                needsRedraw = false;
            }
        }
        if (TimerManager.checkTimer(11, TILE_RETRY_INTERVAL_MS)) {
            MapState.setTilesReady(false);
        }
        Vector vector2 = animationSteps;
        synchronized (vector2) {
            if (animationIndex <= MAX_ANIMATION_STEPS && vector2.size() > 0) {
                long now = System.currentTimeMillis();
                if (j - animationTimestamp > ANIMATION_FRAME_INTERVAL_MS) {
                    long[] jArr = (long[]) vector2.elementAt(animationIndex);
                    setPosition(jArr[0], jArr[1]);
                    animationIndex++;
                    animationTimestamp = now;
                }
            }
        }
        if (autoScrollCount > 0 && !crosshairVisible) {
            long scrollNow = System.currentTimeMillis();
            if (scrollNow - autoScrollTimestamp > ANIMATION_FRAME_INTERVAL_MS) {
                int scrollZoom = MapState.getZoomLevel();
                setPosition(currentLon, currentLat + ((MapUtils.getZoomNumerator(scrollZoom) / MapUtils.getZoomDenominator(scrollZoom)) * 9));
                autoScrollCount -= 9;
                autoScrollTimestamp = scrollNow;
            }
        }
        if (ContactState.isListActive() && System.currentTimeMillis() - RequestQueue.lastUpdateTs > MAP_DATA_REFRESH_INTERVAL_MS && UIState.isPhotoRegistryReady() && MapState.isMapOverlayActive() && !NetworkLock.isNetworkBusy()) {
            MapPointStore.initializeMapData();
        }
    }

    public static final void setPosition(long j, long j2) {
        GeoRegion bestRegion;
        if (j2 == currentLat && j == currentLon) {
            return;
        }
        int zoomLevel = MapState.getZoomLevel();
        synchronized (syncLock) {
            currentLat = j2;
            MapState.setLatitude(37L);
            currentLon = j;
            MapState.setLongitude(j);
            currentPixelX = MapUtils.coordToPixel(j, zoomLevel);
            currentPixelY = MapUtils.coordToPixel(j2, zoomLevel);
            GeoRegion prevRegion = currentRegion;
            Vector regionList = MapState.getMapPoints();
            bestRegion = null;
            for (int idx = Utils.vectorSize(regionList) - 1; idx >= 0; idx--) {
                GeoRegion candidate = (GeoRegion) regionList.elementAt(idx);
                if (candidate.containsPoint(j, j2)) {
                    bestRegion = candidate;
                    break;
                }
            }
            GeoRegion activeRegion = bestRegion;
            if (prevRegion != bestRegion) {
                if (ContactState.isListActive()) {
                    MapPointStore.initializeMapData();
                }
                currentRegion = activeRegion;
            }
            setZoom(clampZoom(zoomLevel));
        }
        needsRedraw = true;
    }

    private static final int clampZoom(int i) {
        int i2;
        if (StringUtils.isInSavedRegion(currentLon, currentLat) || i <= MAP_CLAMP_ZOOM) {
            return (currentRegion == null || i <= (i2 = currentRegion.precision)) ? i : i2;
        }
        return MAP_CLAMP_ZOOM;
    }

    public static final void setZoom(int i) {
        int zoomLevel = MapState.getZoomLevel();
        if (i == zoomLevel || i < MIN_ZOOM || i > MAX_ZOOM) {
            return;
        }
        int clampedZoom = clampZoom(i);
        int i2 = clampedZoom != SKIP_ZOOM_LEVEL ? clampedZoom : zoomLevel < clampedZoom ? SKIP_ZOOM_LEVEL + 1 : SKIP_ZOOM_LEVEL - 1;
        MapState.setZoomLevel(i2);
        currentPixelX = MapUtils.coordToPixel(currentLon, i2);
        currentPixelY = MapUtils.coordToPixel(currentLat, i2);
        resetInteraction();
        needsRedraw = true;
    }

    public static final void setCrosshairVisible(boolean z) {
        if (crosshairVisible != z) {
            crosshairVisible = z;
            needsRedraw = true;
        }
    }

    public static final void confirmMapPoint(MapPoint mapPoint) {
        if (MapState.isMapModeActive()) {
            RouteData.setSecondToken(mapPoint.longitude, mapPoint.latitude);
        } else {
            RouteData.setFirstToken(mapPoint.longitude, mapPoint.latitude);
        }
        needsRedraw = true;
        if (hasRouteEndpoints()) {
            Conversation.loadContacts();
        }
        mapPoint.markInactive();
        UIState.setNewMessage(0);
    }

    public static final void navigateToMapPoint(MapPoint mapPoint) {
        invalidate();
        setPosition(mapPoint.longitude, mapPoint.latitude);
        setZoom(mapPoint.zoomLevel);
        mapPoint.markActive();
        resetInteraction();
    }

    public static final String getTooltipText() {
        if (tooltipItem != null) {
            return tooltipItem.getText();
        }
        return null;
    }

    public static final boolean hasRouteEndpoints() {
        return (RouteData.lastTokenPair[0] > 0L ? 1 : (RouteData.lastTokenPair[0] == 0L ? 0 : -1)) != 0 && (RouteData.lastTokenPair[1] > 0L ? 1 : (RouteData.lastTokenPair[1] == 0L ? 0 : -1)) != 0 && (RouteData.currentTokenPair[0] > 0L ? 1 : (RouteData.currentTokenPair[0] == 0L ? 0 : -1)) != 0 && (RouteData.currentTokenPair[1] > 0L ? 1 : (RouteData.currentTokenPair[1] == 0L ? 0 : -1)) != 0 && ((RouteData.lastTokenPair[0] > RouteData.currentTokenPair[0] ? 1 : (RouteData.lastTokenPair[0] == RouteData.currentTokenPair[0] ? 0 : -1)) != 0 || (RouteData.lastTokenPair[1] > RouteData.currentTokenPair[1] ? 1 : (RouteData.lastTokenPair[1] == RouteData.currentTokenPair[1] ? 0 : -1)) != 0);
    }

    public static final void animateTo(long j, long j2) {
        synchronized (animationSteps) {
            animationSteps.removeAllElements();
            animationIndex = 0;
            long j3 = currentLon;
            long j4 = currentLat;
            long j5 = (j - j3) / ANIMATION_FRAME_COUNT;
            long j6 = (j2 - j4) / ANIMATION_FRAME_COUNT;
            for (int i = 0; i < ANIMATION_FRAME_COUNT; i++) {
                animationSteps.addElement(new long[]{j3 + (j5 * i), j4 + (j6 * i)});
            }
            animationSteps.addElement(new long[]{j, j2});
            animationTimestamp = System.currentTimeMillis();
        }
    }

    private static boolean hasTooltip() {
        return tooltipItem != null;
    }

    private static void showTooltip(ListItem item) {
        if (tooltipLocked) {
            return;
        }
        tooltipItem = item;
    }

    private static void hideTooltip() {
        if (tooltipLocked) {
            return;
        }
        tooltipItem = null;
    }

    public static final void resetInteraction() {
        setCrosshairVisible(true);
        tooltipLocked = false;
        autoScrollCount = 0;
    }

    private static final int screenToTileX(int i) {
        return ((int) currentPixelX) + (i - (viewportWidth >> 1));
    }

    private static final int screenToTileY(int i) {
        return ((int) currentPixelY) - (i - (viewportHeight >> 1));
    }

    public static final void onTap(int i, int i2) {
        int[] iArr;
        rippleTimestamp = 0L;
        if (!dragActive) {
            if (tooltipItem != null && (iArr = ChatRenderer.buttonBounds) != null && i > iArr[0] && i < iArr[0] + iArr[2] && i2 > iArr[1] - (iArr[3] / 2) && i2 < iArr[1] + (iArr[3] / 2)) {
                tapConsumed = true;
                return;
            } else {
                int zoomLevel = MapState.getZoomLevel();
                animateTo((int) MapUtils.pixelToCoord(screenToTileX(i), zoomLevel), (int) MapUtils.pixelToCoord(screenToTileY(i2), zoomLevel));
            }
        }
        needsRedraw = true;
    }

    public static final void onDrag(int i, int i2) {
        tapConsumed = true;
        rippleTimestamp = 0L;
        int zoomLevel = MapState.getZoomLevel();
        setPosition((int) MapUtils.pixelToCoord(screenToTileX(i), zoomLevel), (int) MapUtils.pixelToCoord(screenToTileY(i2), zoomLevel));
        needsRedraw = true;
    }

    public static void paintOverlay(GraphicsContext g, int mapX, int mapY, int width, int height) {
        g.setClip(mapX, mapY, width, height);
        try {
            int viewportW = MapState.getViewportWidth();
            int viewportH = MapState.getViewportHeight();
            Graphics gfx = g.graphics;
            gfx.drawImage((Image) MapState.getFont2(), viewportW >> 1, mapY + (viewportH >> 1), 3);
            if (!MapState.isMapOverlayActive() && UIState.isSupportsAlpha()) {
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
        MapState.setScrolling(false);
    }
}
