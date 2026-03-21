package p000;

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

    public MmpContact(MmpProtocol c0033d, int i, int i2, String str, String str2, boolean z) {
        super(c0033d);
        this.userId = i;
        this.onlineSemaphore = i2;
        this.identifier = str;
        this.displayName = str2;
        this.sortKey = StringUtils.intern(str2.toLowerCase());
        this.hasUnread = z;
        this.defaultIcon = 255;
        this.identifier = c0033d.encodeId().writeRawString(str).readAllByteStr();
        c0033d.registerContact(this);
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

    public MmpContact(Account abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.userId = c0043n.readInt();
        this.onlineSemaphore = c0043n.readInt();
        this.identifier = c0043n.readWideStr();
        setDisplayName(c0043n.readUTF8Str((String) null));
        this.hasUnread = c0043n.readBoolean();
        c0043n.readBoolean();
        this.canDelete = c0043n.readShortBE();
        this.canBlock = c0043n.readShortBE();
        this.canUnblock = c0043n.readShortBE();
        byte bM1344o = c0043n.readByte();
        this.flags = bM1344o;
        if (bM1344o != 0) {
            AppController.m414a((Contact) this);
        }
        this.defaultIcon = 255;
        this.identifier = abstractC0037h.encodeId().writeRawString(this.identifier).readAllByteStr();
        abstractC0037h.registerContact(this);
        updateRenderState();
        this.extra = this.identifier;
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void deserialize(ByteBuffer c0043n) {
        c0043n.writeIntLE(this.userId).writeIntLE(this.onlineSemaphore).writeStringLatin1(this.identifier).writeStringUTF16(this.displayName).writeBoolean(this.hasUnread).writeBoolean(false).writeShortBE(this.canDelete).writeShortBE(this.canBlock).writeShortBE(this.canUnblock).writeByte(this.flags);
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem createMenuItem() {
        MenuItem c0032cM901a = MenuItem.create(this.identifier).setIcon(getIcon()).addText(this.displayName, canBlock() ? 3 : canDelete() ? 2 : 0, this.defaultIcon == 255 ? 0 : canDelete() ? 4 : canBlock() ? 5 : 3);
        c0032cM901a.data = this;
        return c0032cM901a;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int getIcon() {
        int iMo139e = super.getIcon();
        if (iMo139e == 16384 || iMo139e == 26) {
            return iMo139e;
        }
        if (hasUnread() || isOnline()) {
            return 263;
        }
        return iMo139e;
    }

    /* renamed from: a */
    public final ByteBuffer encodeContactUpdate(int i, String str, int i2) {
        ByteBuffer c0043n = new ByteBuffer();
        if (i != 2) {
            c0043n.writeShortBE(305).writeUTF(str);
        }
        if (i == 5) {
            c0043n.writeShortBE(102).writeShortBE(0);
        }
        return new ByteBuffer().writeUTF(this.identifier).writeShortBE(i2).writeShortBE(this.userId).writeShortBE(0).writeBufferShortLen(c0043n);
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
        AppState.setBool(1573, z);
        AppState.setBool(1574, z && !AppState.getBool(1575));
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
        AppState.setInt(1573, 0);
        AppState.setInt(1574, 0);
        AppState.setInt(1575, 0);
    }

    /* renamed from: o */
    public static final String buildLocationString() {
        ByteBuffer c0043nM1314d = new ByteBuffer().writeCompressed(1442705).writeCompressed(3085016).writeRawString(IOUtils.m809a(lastTokenPair[0])).writeUInt(1026586918).writeRawString(IOUtils.m810b(lastTokenPair[1]));
        int size = routePoints.size();
        int i = 0;
        while (i <= size) {
            int[] iArr = i < size ? (int[]) routePoints.elementAt(i) : new int[]{(int) currentTokenPair[0], (int) currentTokenPair[1]};
            c0043nM1314d.writeUInt(30758).writeIntAsString(i + 1).writeByte(61).writeRawString(IOUtils.m809a(iArr[0])).writeUInt(31014).writeIntAsString(i + 1).writeByte(61).writeRawString(IOUtils.m810b(iArr[1]));
            i++;
        }
        return c0043nM1314d.getStringAndClear();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v46, types: [java.lang.Object, java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v61, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v78, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r0v90, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r2v9 */
    /* renamed from: b */
    public static final void parseRouteFromJson(ByteBuffer c0043n) {
        int[] iArr = null;
        int i = 0;
        int i2 = 0;
        int iM192t = 0;
        Object z = Boolean.FALSE;
        int i3 = 0;
        routeRegions.removeAllElements();
        totalRouteLength = 0;
        totalRouteDuration = 0;
        Hashtable hashtable = (Hashtable) JsonParser.parseUTF8(c0043n, 2);
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
                        StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append(AppState.getString(979));
                        int i7 = 952;
                        int i8 = totalRouteLength;
                        int i9 = 0;
                        if (i8 > 1000) {
                            i9 = i8 % 1000;
                            i8 /= 1000;
                            i7 = 952 + 1;
                        }
                        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
                        stringBufferM1217h.append(i8);
                        if (i9 != 0) {
                            stringBufferM1217h.append('.');
                            String strM17c = StringUtils.intern(Integer.toString(i9));
                            String strM13b = strM17c;
                            if (strM17c.length() > 2) {
                                strM13b = StringUtils.prefix(strM13b, 2);
                            }
                            stringBufferM1217h.append(strM13b);
                        }
                        StringBuffer stringBufferAppend2 = stringBufferAppend.append(NetworkUtils.bufToStringCached(stringBufferM1217h.append(AppState.getString(i7)))).append(AppState.getString(983));
                        int i10 = totalRouteDuration;
                        StringBuffer stringBufferM1217h2 = NetworkUtils.newStringBuffer();
                        int i11 = i10 / 60;
                        if (i11 < 90) {
                            stringBufferM1217h2.append(i11);
                        } else {
                            stringBufferM1217h2.append(i11 / 60).append(AppState.getString(954)).append(i11 % 60);
                        }
                        r03[1] = stringBufferAppend2.append(NetworkUtils.bufToStringCached(stringBufferM1217h2.append(AppState.getString(955)))).toString();
                        r03[2] = AppState.emptyStr;
                    } else if (i4 == size - 1 && i5 == size2 - 2) {
                        r03[1] = AppState.getString(980);
                        r03[2] = AppState.emptyStr;
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
                iM192t = getTotalRoutePoints();
            } catch (Throwable unused) {
            }
            if (iM192t == 0) {
                throw new RuntimeException();
            }
            int i13 = 0;
            int[] iArrM193a = getRoutePointAt(0);
            int iM319a = AppController.m319a(iArrM193a[0], iArrM193a[1], i, i2);
            for (int i14 = 1; i14 < iM192t; i14++) {
                int[] iArrM193a2 = getRoutePointAt(i14);
                int iM319a2 = AppController.m319a(iArrM193a2[0], iArrM193a2[1], i, i2);
                if (iM319a2 < iM319a) {
                    iM319a = iM319a2;
                    i13 = i14;
                }
            }
            nearestPoints.addElement(new Object[]{ResourceManager.m967e(i13), iArr});
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
        int iM192t = getTotalRoutePoints();
        int iM586d = AppState.getInt(39);
        int[] iArrM193a = getRoutePointAt(currentRouteIndex);
        int iM317a = (int) AppController.m317a(iArrM193a[0], iM586d);
        int iM317a2 = (int) AppController.m317a(iArrM193a[1], iM586d);
        for (int i = currentRouteIndex + 1; i < iM192t; i++) {
            if (getRouteLabelsAt(i) != null) {
                int[] iArrM193a2 = getRoutePointAt(i);
                if (ChatRenderer.isDistant(iM317a, (int) AppController.m317a(iArrM193a2[0], iM586d), iM317a2, (int) AppController.m317a(iArrM193a2[1], iM586d)) || i == iM192t - 1) {
                    currentRouteIndex = i;
                    break;
                }
            }
        }
        return getRoutePointAt(currentRouteIndex);
    }

    /* renamed from: s */
    public static final int[] getPrevRoutePoint() {
        int[] iArrM193a;
        if (currentRouteIndex == 0 && getRouteLabelsAt(currentRouteIndex) != null) {
            return getRoutePointAt(currentRouteIndex);
        }
        int iM586d = AppState.getInt(39);
        int[] iArrM193a2 = getRoutePointAt(currentRouteIndex);
        int iM317a = (int) AppController.m317a(iArrM193a2[0], iM586d);
        int iM317a2 = (int) AppController.m317a(iArrM193a2[1], iM586d);
        int i = currentRouteIndex;
        while (true) {
            i--;
            if (i < 0) {
                return null;
            }
            if (getRouteLabelsAt(i) != null) {
                iArrM193a = getRoutePointAt(i);
                if (ChatRenderer.isDistant(iM317a, (int) AppController.m317a(iArrM193a[0], iM586d), iM317a2, (int) AppController.m317a(iArrM193a[1], iM586d)) || i == 0) {
                    break;
                }
            }
        }
        currentRouteIndex = i;
        return iArrM193a;
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
