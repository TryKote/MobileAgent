package com.trykote.mobileagent.protocol.mmp;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Hashtable;
import java.util.Vector;

public final class MmpContact extends Contact {

    // Icon IDs
    private static final int ICON_DEFAULT = 255;
    private static final int ICON_SPECIAL = 263;
    private static final int ICON_BLINK_FLAG = 16384;
    private static final int ICON_CUSTOM_STATUS = 26;

    // MMP protocol tags (for encodeContactUpdate)
    private static final int TAG_DISPLAY_NAME = 305;
    private static final int TAG_AUTHORIZATION_FLAG = 102;

    // URL parameter separators (writeUInt LSB-first encoding)
    private static final int URL_PARAM_LAT_PREFIX = 1026586918;  // "&y0="
    private static final int URL_PARAM_X_PREFIX = 30758;          // "&x"
    private static final int URL_PARAM_Y_PREFIX = 31014;          // "&y"
    private static final int URL_PARAM_EQUALS = 61;               // "="

    // Text style indices for createMenuItem
    private static final int TEXT_STYLE_DEFAULT = 0;
    private static final int TEXT_STYLE_DELETABLE = 2;
    private static final int TEXT_STYLE_BLOCKABLE = 3;

    // Text color indices for createMenuItem
    private static final int TEXT_COLOR_DEFAULT = 0;
    private static final int TEXT_COLOR_UNREAD_GENERIC = 3;
    private static final int TEXT_COLOR_UNREAD_DELETABLE = 4;
    private static final int TEXT_COLOR_UNREAD_BLOCKABLE = 5;

    // Time threshold for hour/minute display (minutes)
    private static final int MINUTES_THRESHOLD_FOR_HOURS = 90;

    // Distance threshold for km/m display (meters)
    private static final int METERS_IN_KILOMETER = 1000;

    // Resource string base offset for distance units
    private static final int STR_DISTANCE_UNIT_BASE = 952;

    // Permission type IDs (for updatePermissionFlags)
    private static final int PERMISSION_DELETE = 2;
    private static final int PERMISSION_BLOCK = 3;

    // Contact update operation types (for encodeContactUpdate)
    private static final int UPDATE_TYPE_RENAME = 2;
    private static final int UPDATE_TYPE_AUTHORIZE = 5;

    public final int userId;

    public int onlineSemaphore;

    public String identifier;

    private boolean hasUnread;

    public int canDelete;

    public int canBlock;

    public int canUnblock;

    public boolean isBlocked;

    public boolean isUnblocked;

    public static long[] lastTokenPair;

    public static long[] currentTokenPair;

    public static Vector routePoints;

    public static Vector nearestPoints;

    public static Object[] mapDataCache;

    private static int currentRouteIndex;

    public static Vector routeRegions;

    public static boolean locationEnabled;

    private static int totalRouteLength;

    private static int totalRouteDuration;

    public MmpContact(MmpProtocol protocol, int userId, int onlineSemaphore, String rawIdentifier, String name, boolean unread) {
        super(protocol);
        this.userId = userId;
        this.onlineSemaphore = onlineSemaphore;
        this.identifier = rawIdentifier;
        this.displayName = name;
        this.sortKey = StringUtils.intern(name.toLowerCase());
        this.hasUnread = unread;
        this.defaultIcon = ICON_DEFAULT;
        this.identifier = protocol.encodeId().writeRawString(rawIdentifier).readAllByteStr();
        protocol.registerContact(this);
        updateRenderState();
        this.extra = rawIdentifier;
    }

    @Override // p000.Contact
    public final void clearUnread() {
        this.defaultIcon = ICON_DEFAULT;
        this.isBlocked = false;
        this.isUnblocked = false;
        super.clearUnread();
    }

    @Override // p000.Contact
    public final String getIdentifier() {
        return this.identifier;
    }

    public MmpContact(Account account, ByteBuffer buffer) {
        super(account);
        this.userId = buffer.readInt();
        this.onlineSemaphore = buffer.readInt();
        this.identifier = buffer.readWideStr();
        setDisplayName(buffer.readUTF8Str((String) null));
        this.hasUnread = buffer.readBoolean();
        buffer.readBoolean();
        this.canDelete = buffer.readShortBE();
        this.canBlock = buffer.readShortBE();
        this.canUnblock = buffer.readShortBE();
        byte savedFlags = buffer.readByte();
        this.flags = savedFlags;
        if (savedFlags != 0) {
            ContactListManager.markContactRead((Contact) this);
        }
        this.defaultIcon = ICON_DEFAULT;
        this.identifier = account.encodeId().writeRawString(this.identifier).readAllByteStr();
        account.registerContact(this);
        updateRenderState();
        this.extra = this.identifier;
    }

    @Override // p000.Contact
    public final void deserialize(ByteBuffer buffer) {
        buffer.writeIntLE(this.userId).writeIntLE(this.onlineSemaphore).writeStringLatin1(this.identifier).writeStringUTF16(this.displayName).writeBoolean(this.hasUnread).writeBoolean(false).writeShortBE(this.canDelete).writeShortBE(this.canBlock).writeShortBE(this.canUnblock).writeByte(this.flags);
    }

    @Override // p000.Contact
    public final MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier).setIcon(getIcon()).addText(this.displayName, canBlock() ? TEXT_STYLE_BLOCKABLE : canDelete() ? TEXT_STYLE_DELETABLE : TEXT_STYLE_DEFAULT, this.defaultIcon == ICON_DEFAULT ? TEXT_COLOR_DEFAULT : canDelete() ? TEXT_COLOR_UNREAD_DELETABLE : canBlock() ? TEXT_COLOR_UNREAD_BLOCKABLE : TEXT_COLOR_UNREAD_GENERIC);
        menuItem.data = this;
        return menuItem;
    }

    @Override // p000.Contact
    public final int getIcon() {
        int icon = super.getIcon();
        if (icon == ICON_BLINK_FLAG || icon == ICON_CUSTOM_STATUS) {
            return icon;
        }
        if (hasUnread() || isOnline()) {
            return ICON_SPECIAL;
        }
        return icon;
    }

    public final ByteBuffer encodeContactUpdate(int updateType, String name, int groupId) {
        ByteBuffer buffer = new ByteBuffer();
        if (updateType != UPDATE_TYPE_RENAME) {
            buffer.writeShortBE(TAG_DISPLAY_NAME).writeUTF(name);
        }
        if (updateType == UPDATE_TYPE_AUTHORIZE) {
            buffer.writeShortBE(TAG_AUTHORIZATION_FLAG).writeShortBE(0);
        }
        return new ByteBuffer().writeUTF(this.identifier).writeShortBE(groupId).writeShortBE(this.userId).writeShortBE(0).writeBufferShortLen(buffer);
    }

    @Override // p000.Contact
    public final boolean canDelete() {
        return this.canDelete != 0;
    }

    @Override // p000.Contact
    public final boolean canBlock() {
        return this.canBlock != 0;
    }

    @Override // p000.Contact
    public final boolean canUnblock() {
        return this.canUnblock != 0;
    }

    @Override // p000.Contact
    public final boolean isOnline() {
        return this.userId == -1;
    }

    @Override // p000.Contact
    public final boolean hasUnread() {
        return this.hasUnread && this.userId != -1;
    }

    @Override // p000.Contact
    public final void performAction() {
        if (isOnline()) {
            return;
        }
        this.hasUnread = false;
        updateRenderState();
    }

    public final void updatePermissionFlags(int permissionType, int value) {
        if (permissionType == PERMISSION_DELETE) {
            this.canDelete = value;
        } else if (permissionType == PERMISSION_BLOCK) {
            this.canBlock = value;
        } else {
            this.canUnblock = value;
        }
    }

    public static final void setFirstToken(long x, long y) {
        lastTokenPair[0] = x;
        lastTokenPair[1] = y;
    }

    public static final void setSecondToken(long x, long y) {
        currentTokenPair[0] = x;
        currentTokenPair[1] = y;
    }

    public static final void setLocationEnabled(boolean enabled) {
        locationEnabled = enabled;
        Storage.state().setBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE, enabled);
        Storage.state().setBool(UIKeys.FLAG_ROUTE_POINT_VISIBLE, enabled && !Storage.state().getBool(UIKeys.FLAG_ROUTE_POINT_HIDDEN));
    }

    public static final void clearLocationData() {
        lastTokenPair[0] = 0;
        lastTokenPair[1] = 0;
        currentTokenPair[0] = 0;
        currentTokenPair[1] = 0;
        routeRegions.removeAllElements();
        routePoints.removeAllElements();
        nearestPoints.removeAllElements();
        setLocationEnabled(false);
        currentRouteIndex = 0;
        Storage.state().setInt(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE, 0);
        Storage.state().setInt(UIKeys.FLAG_ROUTE_POINT_VISIBLE, 0);
        Storage.state().setInt(UIKeys.FLAG_ROUTE_POINT_HIDDEN, 0);
    }

    public static final String buildLocationString() {
        ByteBuffer urlBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_TRACKPOINTS).writeRawString(MapUtils.pixelToLongitude(lastTokenPair[0])).writeUInt(URL_PARAM_LAT_PREFIX).writeRawString(MapUtils.pixelToLatitude(lastTokenPair[1]));
        int size = routePoints.size();
        int pointIdx = 0;
        while (pointIdx <= size) {
            int[] coords = pointIdx < size ? (int[]) routePoints.elementAt(pointIdx) : new int[]{(int) currentTokenPair[0], (int) currentTokenPair[1]};
            urlBuf.writeUInt(URL_PARAM_X_PREFIX).writeIntAsString(pointIdx + 1).writeByte(URL_PARAM_EQUALS).writeRawString(MapUtils.pixelToLongitude(coords[0])).writeUInt(URL_PARAM_Y_PREFIX).writeIntAsString(pointIdx + 1).writeByte(URL_PARAM_EQUALS).writeRawString(MapUtils.pixelToLatitude(coords[1]));
            pointIdx++;
        }
        return urlBuf.getStringAndClear();
    }

    public static final void parseRouteFromJson(ByteBuffer buffer) {
        int[] waypoint = null;
        int waypointX = 0;
        int waypointY = 0;
        int totalPoints = 0;
        Object lastPointData = Boolean.FALSE;
        int prevRegionSize = 0;
        routeRegions.removeAllElements();
        totalRouteLength = 0;
        totalRouteDuration = 0;
        Hashtable routeJson = (Hashtable) JsonParser.parseUTF8(buffer, 2);
        totalRouteLength = ((Integer) routeJson.get("totalLength")).intValue();
        totalRouteDuration = ((Integer) routeJson.get("totalTime")).intValue();
        Vector regions = (Vector) routeJson.get("regions");
        int regionCount = regions.size();
        int regionIdx = 0;
        while (regionIdx < regionCount) {
            Object[] regionData = new Object[2];
            Hashtable regionJson = (Hashtable) regions.elementAt(regionIdx);
            Vector leftTop = (Vector) regionJson.get("lefttop");
            Vector rightBottom = (Vector) regionJson.get("rightbottom");
            regionData[0] = new int[]{((Integer) leftTop.elementAt(0)).intValue(), ((Integer) leftTop.elementAt(1)).intValue(), ((Integer) rightBottom.elementAt(0)).intValue(), ((Integer) rightBottom.elementAt(1)).intValue()};
            Vector points = (Vector) regionJson.get("points");
            int pointArraySize = points.size() + 2;
            Object[] pointArray = new Object[pointArraySize];
            pointArray[0] = lastPointData;
            pointArray[pointArraySize - 1] = 0;
            int pointIdx = 1;
            while (pointIdx < pointArraySize - 1) {
                Vector pointJson = (Vector) points.elementAt(pointIdx - 1);
                int allocSize = ((regionIdx == 0 && pointIdx == 1) || (regionIdx == regionCount - 1 && pointIdx == pointArraySize - 2)) ? 4 : pointJson.size();
                int fieldCount = allocSize;
                Object[] pointData = new Object[allocSize - 1];
                pointData[0] = new int[]{((Integer) pointJson.elementAt(0)).intValue(), ((Integer) pointJson.elementAt(1)).intValue()};
                if (fieldCount == 4) {
                    if (regionIdx == 0 && pointIdx == 1) {
                        StringBuffer routeInfo = ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ROUTE_PREFIX));
                        int distUnitKey = STR_DISTANCE_UNIT_BASE;
                        int distance = totalRouteLength;
                        int fraction = 0;
                        if (distance > METERS_IN_KILOMETER) {
                            fraction = distance % METERS_IN_KILOMETER;
                            distance /= METERS_IN_KILOMETER;
                            distUnitKey = STR_DISTANCE_UNIT_BASE + 1;
                        }
                        StringBuffer distBuf = ObjectPool.newStringBuffer();
                        distBuf.append(distance);
                        if (fraction != 0) {
                            distBuf.append('.');
                            String fracStr = StringUtils.intern(Integer.toString(fraction));
                            String trimmed = fracStr;
                            if (fracStr.length() > 2) {
                                trimmed = StringUtils.prefix(trimmed, 2);
                            }
                            distBuf.append(trimmed);
                        }
                        StringBuffer fullRouteInfo = routeInfo.append(ObjectPool.toStringAndRelease(distBuf.append(Storage.state().getString(distUnitKey)))).append(Storage.resources().getString(StringResKeys.STR_DISTANCE_UNIT));
                        int durationSec = totalRouteDuration;
                        StringBuffer timeBuf = ObjectPool.newStringBuffer();
                        int minutes = durationSec / 60;
                        if (minutes < MINUTES_THRESHOLD_FOR_HOURS) {
                            timeBuf.append(minutes);
                        } else {
                            timeBuf.append(minutes / 60).append(Storage.resources().getString(StringResKeys.STR_HOUR_SEPARATOR)).append(minutes % 60);
                        }
                        pointData[1] = fullRouteInfo.append(ObjectPool.toStringAndRelease(timeBuf.append(Storage.resources().getString(StringResKeys.STR_DISTANCE_SUFFIX)))).toString();
                        pointData[2] = Storage.emptyStr;
                    } else if (regionIdx == regionCount - 1 && pointIdx == pointArraySize - 2) {
                        pointData[1] = Storage.resources().getString(StringResKeys.STR_NO_ROUTE);
                        pointData[2] = Storage.emptyStr;
                    } else {
                        pointData[1] = pointJson.elementAt(2);
                        pointData[2] = pointJson.elementAt(3);
                    }
                }
                pointArray[pointIdx] = pointData;
                if (pointIdx == 1 && pointArraySize > 2 && regionIdx > 0 && prevRegionSize != 0) {
                    ((Object[]) ((Object[]) routeRegions.elementAt(regionIdx - 1))[1])[prevRegionSize - 1] = pointData;
                }
                lastPointData = pointData;
                pointIdx++;
            }
            prevRegionSize = pointArraySize;
            regionData[1] = pointArray;
            routeRegions.addElement(regionData);
            regionIdx++;
        }
        nearestPoints.removeAllElements();
        for (int wpIdx = 0; wpIdx < routePoints.size(); wpIdx++) {
            try {
                waypoint = (int[]) routePoints.elementAt(wpIdx);
                waypointX = waypoint[0];
                waypointY = waypoint[1];
                totalPoints = getTotalRoutePoints();
            } catch (Throwable unused) {
            }
            if (totalPoints == 0) {
                throw new RuntimeException();
            }
            int nearestIdx = 0;
            int[] coords = getRoutePointAt(0);
            int minDist = MapUtils.computeColor(coords[0], coords[1], waypointX, waypointY);
            for (int rpIdx = 1; rpIdx < totalPoints; rpIdx++) {
                int[] candidateCoords = getRoutePointAt(rpIdx);
                int dist = MapUtils.computeColor(candidateCoords[0], candidateCoords[1], waypointX, waypointY);
                if (dist < minDist) {
                    minDist = dist;
                    nearestIdx = rpIdx;
                }
            }
            nearestPoints.addElement(new Object[]{ObjectPool.integerOf(nearestIdx), waypoint});
        }
    }

    public static final boolean hasFirstToken() {
        return (lastTokenPair[0] == 0 || lastTokenPair[1] == 0) ? false : true;
    }

    public static final boolean hasSecondToken() {
        return (currentTokenPair[0] == 0 || currentTokenPair[1] == 0) ? false : true;
    }

    public static final int[] getNextRoutePoint() {
        int totalPoints = getTotalRoutePoints();
        int zoom = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        int[] currentCoords = getRoutePointAt(currentRouteIndex);
        int pixelX = (int) MapUtils.coordToPixel(currentCoords[0], zoom);
        int pixelY = (int) MapUtils.coordToPixel(currentCoords[1], zoom);
        for (int idx = currentRouteIndex + 1; idx < totalPoints; idx++) {
            if (getRouteLabelsAt(idx) != null) {
                int[] candidateCoords = getRoutePointAt(idx);
                if (ChatRenderer.isDistant(pixelX, (int) MapUtils.coordToPixel(candidateCoords[0], zoom), pixelY, (int) MapUtils.coordToPixel(candidateCoords[1], zoom)) || idx == totalPoints - 1) {
                    currentRouteIndex = idx;
                    break;
                }
            }
        }
        return getRoutePointAt(currentRouteIndex);
    }

    public static final int[] getPrevRoutePoint() {
        int[] candidateCoords;
        if (currentRouteIndex == 0 && getRouteLabelsAt(currentRouteIndex) != null) {
            return getRoutePointAt(currentRouteIndex);
        }
        int zoom = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        int[] currentCoords = getRoutePointAt(currentRouteIndex);
        int pixelX = (int) MapUtils.coordToPixel(currentCoords[0], zoom);
        int pixelY = (int) MapUtils.coordToPixel(currentCoords[1], zoom);
        for (int idx = currentRouteIndex - 1; idx >= 0; idx--) {
            if (getRouteLabelsAt(idx) != null) {
                candidateCoords = getRoutePointAt(idx);
                if (ChatRenderer.isDistant(pixelX, (int) MapUtils.coordToPixel(candidateCoords[0], zoom), pixelY, (int) MapUtils.coordToPixel(candidateCoords[1], zoom)) || idx == 0) {
                    currentRouteIndex = idx;
                    return candidateCoords;
                }
            }
        }
        return null;
    }

    public static final int getTotalRoutePoints() {
        int length = 0;
        for (int regionIdx = 0; regionIdx < routeRegions.size(); regionIdx++) {
            Object[] regionPoints = (Object[]) ((Object[]) routeRegions.elementAt(regionIdx))[1];
            if (regionPoints != null) {
                length += regionPoints.length - 2;
            }
        }
        return length;
    }

    public static final int[] getRoutePointAt(int index) {
        if (index > getTotalRoutePoints()) {
            return null;
        }
        int pointsPerRegion = ((Object[]) ((Object[]) routeRegions.firstElement())[1]).length - 2;
        return (int[]) ((Object[]) ((Object[]) ((Object[]) routeRegions.elementAt(index / pointsPerRegion))[1])[(index % pointsPerRegion) + 1])[0];
    }

    public static final String[] getRouteLabelsAt(int index) {
        if (index > getTotalRoutePoints()) {
            return null;
        }
        int pointsPerRegion = ((Object[]) ((Object[]) routeRegions.firstElement())[1]).length - 2;
        Object[] pointData = (Object[]) ((Object[]) ((Object[]) routeRegions.elementAt(index / pointsPerRegion))[1])[(index % pointsPerRegion) + 1];
        if (pointData.length > 1) {
            return new String[]{(String) pointData[1], (String) pointData[2]};
        }
        return null;
    }

    public static final void clearRouteProgress() {
        routeRegions.removeAllElements();
        currentRouteIndex = 0;
    }
}
