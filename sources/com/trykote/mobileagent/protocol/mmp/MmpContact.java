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

/* renamed from: ai */
/* loaded from: MobileAgent_3.9.jar:ai.class */
public final class MmpContact extends Contact {

    /* renamed from: a */
    public final int userId;

    /* renamed from: b */
    public int onlineSemaphore;

    /* renamed from: c */
    public String identifier;

    /* renamed from: z */
    private boolean hasUnread;

    /* renamed from: d */
    public int canDelete;

    /* renamed from: e */
    public int canBlock;

    /* renamed from: f */
    public int canUnblock;

    /* renamed from: g */
    public boolean isBlocked;

    /* renamed from: h */
    public boolean isUnblocked;

    /* renamed from: i */
    public static long[] lastTokenPair;

    /* renamed from: j */
    public static long[] currentTokenPair;

    /* renamed from: k */
    public static Vector routePoints;

    /* renamed from: l */
    public static Vector nearestPoints;

    /* renamed from: m */
    public static Object[] mapDataCache;

    /* renamed from: A */
    private static int currentRouteIndex;

    /* renamed from: n */
    public static Vector routeRegions;

    /* renamed from: y */
    public static boolean locationEnabled;

    /* renamed from: B */
    private static int totalRouteLength;

    /* renamed from: C */
    private static int totalRouteDuration;

    public MmpContact(MmpProtocol protocol, int i, int i2, String str, String str2, boolean z) {
        super(protocol);
        this.userId = i;
        this.onlineSemaphore = i2;
        this.identifier = str;
        this.displayName = str2;
        this.sortKey = StringUtils.intern(str2.toLowerCase());
        this.hasUnread = z;
        this.defaultIcon = 255;
        this.identifier = protocol.encodeId().writeRawString(str).readAllByteStr();
        protocol.registerContact(this);
        updateRenderState();
        this.extra = str;
    }

    @Override // p000.Contact
    /* renamed from: c */
    public final void clearUnread() {
        this.defaultIcon = 255;
        this.isBlocked = false;
        this.isUnblocked = false;
        super.clearUnread();
    }

    @Override // p000.Contact
    /* renamed from: a */
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
        this.defaultIcon = 255;
        this.identifier = account.encodeId().writeRawString(this.identifier).readAllByteStr();
        account.registerContact(this);
        updateRenderState();
        this.extra = this.identifier;
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void deserialize(ByteBuffer buffer) {
        buffer.writeIntLE(this.userId).writeIntLE(this.onlineSemaphore).writeStringLatin1(this.identifier).writeStringUTF16(this.displayName).writeBoolean(this.hasUnread).writeBoolean(false).writeShortBE(this.canDelete).writeShortBE(this.canBlock).writeShortBE(this.canUnblock).writeByte(this.flags);
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier).setIcon(getIcon()).addText(this.displayName, canBlock() ? 3 : canDelete() ? 2 : 0, this.defaultIcon == 255 ? 0 : canDelete() ? 4 : canBlock() ? 5 : 3);
        menuItem.data = this;
        return menuItem;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int getIcon() {
        int icon = super.getIcon();
        if (icon == 16384 || icon == 26) {
            return icon;
        }
        if (hasUnread() || isOnline()) {
            return 263;
        }
        return icon;
    }

    /* renamed from: a */
    public final ByteBuffer encodeContactUpdate(int i, String str, int i2) {
        ByteBuffer buffer = new ByteBuffer();
        if (i != 2) {
            buffer.writeShortBE(305).writeUTF(str);
        }
        if (i == 5) {
            buffer.writeShortBE(102).writeShortBE(0);
        }
        return new ByteBuffer().writeUTF(this.identifier).writeShortBE(i2).writeShortBE(this.userId).writeShortBE(0).writeBufferShortLen(buffer);
    }

    @Override // p000.Contact
    /* renamed from: i */
    public final boolean canDelete() {
        return this.canDelete != 0;
    }

    @Override // p000.Contact
    /* renamed from: j */
    public final boolean canBlock() {
        return this.canBlock != 0;
    }

    @Override // p000.Contact
    /* renamed from: k */
    public final boolean canUnblock() {
        return this.canUnblock != 0;
    }

    @Override // p000.Contact
    /* renamed from: m */
    public final boolean isOnline() {
        return this.userId == -1;
    }

    @Override // p000.Contact
    /* renamed from: l */
    public final boolean hasUnread() {
        return this.hasUnread && this.userId != -1;
    }

    @Override // p000.Contact
    /* renamed from: h */
    public final void performAction() {
        if (isOnline()) {
            return;
        }
        this.hasUnread = false;
        updateRenderState();
    }

    /* renamed from: a */
    public final void updatePermissionFlags(int i, int i2) {
        if (i == 2) {
            this.canDelete = i2;
        } else if (i == 3) {
            this.canBlock = i2;
        } else {
            this.canUnblock = i2;
        }
    }

    /* renamed from: a */
    public static final void setFirstToken(long j, long j2) {
        lastTokenPair[0] = j;
        lastTokenPair[1] = j2;
    }

    /* renamed from: b */
    public static final void setSecondToken(long j, long j2) {
        currentTokenPair[0] = j;
        currentTokenPair[1] = j2;
    }

    /* renamed from: a */
    public static final void setLocationEnabled(boolean z) {
        locationEnabled = z;
        Storage.state().setBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE, z);
        Storage.state().setBool(UIKeys.FLAG_ROUTE_POINT_VISIBLE, z && !Storage.state().getBool(UIKeys.FLAG_ROUTE_POINT_HIDDEN));
    }

    /* renamed from: f */
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

    /* renamed from: o */
    public static final String buildLocationString() {
        ByteBuffer urlBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_TRACKPOINTS).writeRawString(MapUtils.pixelToLongitude(lastTokenPair[0])).writeUInt(1026586918).writeRawString(MapUtils.pixelToLatitude(lastTokenPair[1]));
        int size = routePoints.size();
        int i = 0;
        while (i <= size) {
            int[] iArr = i < size ? (int[]) routePoints.elementAt(i) : new int[]{(int) currentTokenPair[0], (int) currentTokenPair[1]};
            urlBuf.writeUInt(30758).writeIntAsString(i + 1).writeByte(61).writeRawString(MapUtils.pixelToLongitude(iArr[0])).writeUInt(31014).writeIntAsString(i + 1).writeByte(61).writeRawString(MapUtils.pixelToLatitude(iArr[1]));
            i++;
        }
        return urlBuf.getStringAndClear();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v46, types: [java.lang.Object, java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v61, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v78, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v90, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* renamed from: b */
    public static final void parseRouteFromJson(ByteBuffer buffer) {
        int[] iArr = null;
        int i = 0;
        int i2 = 0;
        int totalPoints = 0;
        Object z = Boolean.FALSE;
        int i3 = 0;
        routeRegions.removeAllElements();
        totalRouteLength = 0;
        totalRouteDuration = 0;
        Hashtable hashtable = (Hashtable) JsonParser.parseUTF8(buffer, 2);
        totalRouteLength = ((Integer) hashtable.get("totalLength")).intValue();
        totalRouteDuration = ((Integer) hashtable.get("totalTime")).intValue();
        Vector vector = (Vector) hashtable.get("regions");
        int size = vector.size();
        int i4 = 0;
        while (i4 < size) {
            Object[] r0 = new Object[2];
            Hashtable hashtable2 = (Hashtable) vector.elementAt(i4);
            Vector vector2 = (Vector) hashtable2.get("lefttop");
            Vector vector3 = (Vector) hashtable2.get("rightbottom");
            r0[0] = new int[]{((Integer) vector2.elementAt(0)).intValue(), ((Integer) vector2.elementAt(1)).intValue(), ((Integer) vector3.elementAt(0)).intValue(), ((Integer) vector3.elementAt(1)).intValue()};
            Vector vector4 = (Vector) hashtable2.get("points");
            int size2 = vector4.size() + 2;
            Object[] r02 = new Object[size2];
            r02[0] = z;
            r02[size2 - 1] = 0;
            int i5 = 1;
            while (i5 < size2 - 1) {
                Vector vector5 = (Vector) vector4.elementAt(i5 - 1);
                int size3 = ((i4 == 0 && i5 == 1) || (i4 == size - 1 && i5 == size2 - 2)) ? 4 : vector5.size();
                int i6 = size3;
                Object[] r03 = new Object[size3 - 1];
                r03[0] = new int[]{((Integer) vector5.elementAt(0)).intValue(), ((Integer) vector5.elementAt(1)).intValue()};
                if (i6 == 4) {
                    if (i4 == 0 && i5 == 1) {
                        StringBuffer routeInfo = ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ROUTE_PREFIX));
                        int i7 = 952;
                        int i8 = totalRouteLength;
                        int i9 = 0;
                        if (i8 > 1000) {
                            i9 = i8 % 1000;
                            i8 /= 1000;
                            i7 = 952 + 1;
                        }
                        StringBuffer distBuf = ObjectPool.newStringBuffer();
                        distBuf.append(i8);
                        if (i9 != 0) {
                            distBuf.append('.');
                            String fracStr = StringUtils.intern(Integer.toString(i9));
                            String trimmed = fracStr;
                            if (fracStr.length() > 2) {
                                trimmed = StringUtils.prefix(trimmed, 2);
                            }
                            distBuf.append(trimmed);
                        }
                        StringBuffer routeInfo2 = routeInfo.append(ObjectPool.toStringAndRelease(distBuf.append(Storage.state().getString(i7)))).append(Storage.resources().getString(StringResKeys.STR_DISTANCE_UNIT));
                        int i10 = totalRouteDuration;
                        StringBuffer distBuf2 = ObjectPool.newStringBuffer();
                        int i11 = i10 / 60;
                        if (i11 < 90) {
                            distBuf2.append(i11);
                        } else {
                            distBuf2.append(i11 / 60).append(Storage.resources().getString(StringResKeys.STR_HOUR_SEPARATOR)).append(i11 % 60);
                        }
                        r03[1] = routeInfo2.append(ObjectPool.toStringAndRelease(distBuf2.append(Storage.resources().getString(StringResKeys.STR_DISTANCE_SUFFIX)))).toString();
                        r03[2] = Storage.emptyStr;
                    } else if (i4 == size - 1 && i5 == size2 - 2) {
                        r03[1] = Storage.resources().getString(StringResKeys.STR_NO_ROUTE);
                        r03[2] = Storage.emptyStr;
                    } else {
                        r03[1] = vector5.elementAt(2);
                        r03[2] = vector5.elementAt(3);
                    }
                }
                r02[i5] = r03;
                if (i5 == 1 && size2 > 2 && i4 > 0 && i3 != 0) {
                    ((Object[]) ((Object[]) routeRegions.elementAt(i4 - 1))[1])[i3 - 1] = r03;
                }
                z = r03;
                i5++;
            }
            i3 = size2;
            r0[1] = r02;
            routeRegions.addElement(r0);
            i4++;
        }
        nearestPoints.removeAllElements();
        for (int i12 = 0; i12 < routePoints.size(); i12++) {
            try {
                iArr = (int[]) routePoints.elementAt(i12);
                i = iArr[0];
                i2 = iArr[1];
                totalPoints = getTotalRoutePoints();
            } catch (Throwable unused) {
            }
            if (totalPoints == 0) {
                throw new RuntimeException();
            }
            int i13 = 0;
            int[] coords = getRoutePointAt(0);
            int minDist = MapUtils.computeColor(coords[0], coords[1], i, i2);
            for (int i14 = 1; i14 < totalPoints; i14++) {
                int[] coords2 = getRoutePointAt(i14);
                int minDist2 = MapUtils.computeColor(coords2[0], coords2[1], i, i2);
                if (minDist2 < minDist) {
                    minDist = minDist2;
                    i13 = i14;
                }
            }
            nearestPoints.addElement(new Object[]{ObjectPool.integerOf(i13), iArr});
        }
    }

    /* renamed from: p */
    public static final boolean hasFirstToken() {
        return (lastTokenPair[0] == 0 || lastTokenPair[1] == 0) ? false : true;
    }

    /* renamed from: q */
    public static final boolean hasSecondToken() {
        return (currentTokenPair[0] == 0 || currentTokenPair[1] == 0) ? false : true;
    }

    /* renamed from: r */
    public static final int[] getNextRoutePoint() {
        int totalPoints = getTotalRoutePoints();
        int zoom = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        int[] coords = getRoutePointAt(currentRouteIndex);
        int px = (int) MapUtils.coordToPixel(coords[0], zoom);
        int px2 = (int) MapUtils.coordToPixel(coords[1], zoom);
        for (int i = currentRouteIndex + 1; i < totalPoints; i++) {
            if (getRouteLabelsAt(i) != null) {
                int[] coords2 = getRoutePointAt(i);
                if (ChatRenderer.isDistant(px, (int) MapUtils.coordToPixel(coords2[0], zoom), px2, (int) MapUtils.coordToPixel(coords2[1], zoom)) || i == totalPoints - 1) {
                    currentRouteIndex = i;
                    break;
                }
            }
        }
        return getRoutePointAt(currentRouteIndex);
    }

    /* renamed from: s */
    public static final int[] getPrevRoutePoint() {
        int[] coords;
        if (currentRouteIndex == 0 && getRouteLabelsAt(currentRouteIndex) != null) {
            return getRoutePointAt(currentRouteIndex);
        }
        int zoom = Storage.state().getInt(MapKeys.MAP_ZOOM_LEVEL);
        int[] coords2 = getRoutePointAt(currentRouteIndex);
        int px = (int) MapUtils.coordToPixel(coords2[0], zoom);
        int px2 = (int) MapUtils.coordToPixel(coords2[1], zoom);
        int i = currentRouteIndex;
        while (true) {
            i--;
            if (i < 0) {
                return null;
            }
            if (getRouteLabelsAt(i) != null) {
                coords = getRoutePointAt(i);
                if (ChatRenderer.isDistant(px, (int) MapUtils.coordToPixel(coords[0], zoom), px2, (int) MapUtils.coordToPixel(coords[1], zoom)) || i == 0) {
                    break;
                }
            }
        }
        currentRouteIndex = i;
        return coords;
    }

    /* renamed from: t */
    public static final int getTotalRoutePoints() {
        int length = 0;
        for (int i = 0; i < routeRegions.size(); i++) {
            Object[] objArr = (Object[]) ((Object[]) routeRegions.elementAt(i))[1];
            if (objArr != null) {
                length += objArr.length - 2;
            }
        }
        return length;
    }

    /* renamed from: a */
    public static final int[] getRoutePointAt(int i) {
        if (i > getTotalRoutePoints()) {
            return null;
        }
        int length = ((Object[]) ((Object[]) routeRegions.firstElement())[1]).length - 2;
        return (int[]) ((Object[]) ((Object[]) ((Object[]) routeRegions.elementAt(i / length))[1])[(i % length) + 1])[0];
    }

    /* renamed from: b */
    public static final String[] getRouteLabelsAt(int i) {
        if (i > getTotalRoutePoints()) {
            return null;
        }
        int length = ((Object[]) ((Object[]) routeRegions.firstElement())[1]).length - 2;
        Object[] objArr = (Object[]) ((Object[]) ((Object[]) routeRegions.elementAt(i / length))[1])[(i % length) + 1];
        if (objArr.length > 1) {
            return new String[]{(String) objArr[1], (String) objArr[2]};
        }
        return null;
    }

    /* renamed from: u */
    public static final void clearRouteProgress() {
        routeRegions.removeAllElements();
        currentRouteIndex = 0;
    }
}
