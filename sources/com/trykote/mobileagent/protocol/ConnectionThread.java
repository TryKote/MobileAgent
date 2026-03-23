package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* renamed from: j */
/* loaded from: MobileAgent_3.9.jar:j.class */
public final class ConnectionThread {

    /* renamed from: b */
    public Throwable exception;

    /* renamed from: j */
    private Object[] connArray;

    /* renamed from: k */
    private String connUrl;

    /* renamed from: l */
    private static Hashtable photoRegistry;

    /* renamed from: d */
    public static Hashtable photoCache;

    /* renamed from: e */
    public static String pendingPhotoKey;

    /* renamed from: f */
    public static Vector hiddenContacts;

    /* renamed from: g */
    public static boolean mapInitialized;

    /* renamed from: m */
    private static Screen mapScreen;

    /* renamed from: h */
    public static ListItem activeMapItem;

    /* renamed from: i */
    private ByteBuffer inBuffer = new ByteBuffer();

    /* renamed from: a */
    public final ByteBuffer outBuffer = new ByteBuffer();

    /* renamed from: c */
    public int state = 1;

    public ConnectionThread(String str) {
        this.connUrl = str;
        RemoteLogger.log("CONN", "new ConnectionThread url=" + str);
        Vector vectorM614m = AppState.getVector(StateKeys.SLOT_MEDIA_CONTROL);
        if (vectorM614m != null) {
            synchronized (vectorM614m) {
                vectorM614m.addElement(IOUtils.registerResource(this));
            }
        }
    }

    /* renamed from: a */
    public final int getState() throws Throwable {
        if (this.exception != null) {
            throw this.exception;
        }
        return this.state;
    }

    /* renamed from: a */
    public final void drainInput(ByteBuffer c0043n) throws Throwable {
        synchronized (this.connArray) {
            if (this.inBuffer.length > 0) {
                ByteBuffer c0043n2 = this.inBuffer;
                int i = c0043n2.length;
                if (i > 0) {
                    synchronized (c0043n2) {
                        c0043n.writeBytesAt(c0043n2.data, c0043n2.offset, i);
                        c0043n2.offset += i;
                        c0043n2.length -= i;
                        c0043n2.compact();
                    }
                }
            } else if (this.exception != null) {
                throw this.exception;
            }
        }
    }

    /* renamed from: b */
    public final void process() {
        RemoteLogger.log("CONN", "process() state=" + this.state);
        switch (this.state) {
            case 1:
                try {
                    this.connArray = NetworkUtils.openSocket(new ByteBuffer().writeCompressed(593549).writeRawString(this.connUrl).getStringAndClear(), AppState.getBool(StateKeys.SETTING_COMPRESSION_ENABLED));
                    if (this.state == 1) {
                        this.state = 2;
                        RemoteLogger.log("CONN", "state 1->2 (socket opened)");
                    }
                    this.connUrl = null;
                    return;
                } catch (Throwable th) {
                    this.state = -1;
                    this.exception = th;
                    RemoteLogger.log("CONN", "state 1 ERROR: " + th, th);
                    NetworkUtils.closeConnection(this.connArray);
                    return;
                }
            case 2:
                readFromSocket();
                writeToSocket();
                return;
            case 3:
                readFromSocket();
                writeToSocket();
                RemoteLogger.log("CONN", "state 3->0 (closing)");
                NetworkUtils.closeConnection(this.connArray);
                this.state = 0;
                return;
            default:
                Vector vectorM614m = AppState.getVector(StateKeys.SLOT_MEDIA_CONTROL);
                if (vectorM614m != null) {
                    synchronized (vectorM614m) {
                        vectorM614m.removeElement(this);
                        Utils.trimIfEmpty(vectorM614m);
                        IOUtils.unregisterResource(this);
                    }
                    return;
                }
                return;
        }
    }

    /* renamed from: o */
    private final void readFromSocket() {
        int iM1190a;
        try {
            if (this.state == 2) {
                ByteBuffer c0043n = this.inBuffer;
                Object[] objArr = this.connArray;
                int iM1188c = NetworkUtils.availableBytes(objArr);
                if (iM1188c > 0) {
                    byte[] bArrM1211a = NetworkUtils.newBytes(iM1188c);
                    int i = 0;
                    do {
                        iM1190a = i + NetworkUtils.readSocket(objArr, bArrM1211a, i, iM1188c - i);
                        i = iM1190a;
                    } while (iM1190a != iM1188c);
                    synchronized (c0043n) {
                        c0043n.ensureCapacity(iM1188c);
                        Utils.arraycopy((Object) bArrM1211a, 0, (Object) c0043n.data, c0043n.length, iM1188c);
                        c0043n.length += iM1188c;
                        c0043n.compact();
                        RemoteLogger.log("CONN", "read " + iM1188c + " bytes, inBuf=" + c0043n.length);
                    }
                    NetworkUtils.releaseBytes(bArrM1211a);
                }
            }
        } catch (Throwable th) {
            this.state = -1;
            this.exception = th;
            RemoteLogger.log("CONN", "readFromSocket ERROR: " + th, th);
            NetworkUtils.closeConnection(this.connArray);
        }
    }

    /* renamed from: p */
    private final void writeToSocket() {
        try {
            if (this.state == 2) {
                ByteBuffer c0043n = this.outBuffer;
                Object[] objArr = this.connArray;
                synchronized (c0043n) {
                    int i = c0043n.length;
                    if (i > 0) {
                        byte[] bArrM1211a = NetworkUtils.newBytes(i);
                        Utils.arraycopy((Object) c0043n.data, c0043n.offset, (Object) bArrM1211a, 0, i);
                        c0043n.offset += i;
                        c0043n.length -= i;
                        c0043n.compact();
                        NetworkUtils.writeSocket(objArr, bArrM1211a, i);
                        RemoteLogger.log("CONN", "wrote " + i + " bytes");
                        NetworkUtils.releaseBytes(bArrM1211a);
                    }
                }
            }
        } catch (Throwable th) {
            this.state = -1;
            this.exception = th;
            RemoteLogger.log("CONN", "writeToSocket ERROR: " + th, th);
            NetworkUtils.closeConnection(this.connArray);
        }
    }

    /* renamed from: q */
    private static void loadSavedMapData() {
        XmppContactGroup.sharedContactList = NetworkUtils.newVector();
        hiddenContacts = Utils.split(AppState.getString(StateKeys.HIDDEN_CONTACTS_LIST), (char) 0);
        try {
            ByteBuffer c0043nM986d = Base64.decode(AppState.getString(StateKeys.CONTACT_REGISTRY_DATA));
            photoRegistry = new Hashtable();
            try {
                if (c0043nM986d.length > 0) {
                    int iM1355w = c0043nM986d.readIntBE();
                    while (true) {
                        iM1355w--;
                        if (iM1355w < 0) {
                            break;
                        }
                        NetworkUtils c0040k = new NetworkUtils(c0043nM986d);
                        photoRegistry.put(StringUtils.intern(Integer.toString(c0040k.port)), c0040k);
                    }
                }
            } catch (Throwable unused) {
            }
            clearPhotoCache();
            AppState.setInt(StateKeys.FLAG_PHOTO_REGISTRY_READY, 1);
        } catch (Throwable unused2) {
        }
    }

    /* renamed from: a */
    public static final void parseServiceConfig(int i, XmlElement c0022av, boolean z) {
        photoRegistry = new Hashtable();
        Vector vector = c0022av.children;
        if (vector == null) {
            return;
        }
        for (int i2 = 0; i2 < Utils.vectorSize(vector); i2++) {
            XmlElement c0022av2 = (XmlElement) vector.elementAt(i2);
            String strM555c = c0022av2.getLongKeyAttr(25705);
            NetworkUtils c0040k = new NetworkUtils(Integer.parseInt(strM555c), c0022av2.getIntAttribute(262601), Integer.parseInt(c0022av2.getIntAttribute(201594)), c0022av2.getIntAttribute(529266));
            Vector vector2 = c0022av2.children;
            int i3 = 0;
            while (i3 < Utils.vectorSize(vector2)) {
                int i4 = i3;
                i3++;
                XmlElement c0022av3 = (XmlElement) vector2.elementAt(i4);
                if (StringUtils.matchesKey(263156, c0022av3.tagName)) {
                    c0040k.url = StringUtils.fromBuffer(c0022av3.textContent);
                }
            }
            photoRegistry.put(strM555c, c0040k);
        }
        photoCache = new Hashtable();
        AppState.setInt(StateKeys.FLAG_PHOTO_REGISTRY_READY, 1);
        try {
            AppState.setObject(StateKeys.CONTACT_REGISTRY_DATA, (Object) AppState.emptyStr);
            AppState.setObject(StateKeys.CONTACT_REGISTRY_DATA, (Object) serializeRegistry().toBase64());
        } catch (Throwable unused) {
            AppState.setObject(StateKeys.URL_GEO_CONFIG, (Object) AppState.emptyStr);
        }
    }

    /* renamed from: a */
    public static final String getPhotoHost(Object obj) {
        NetworkUtils c0040k;
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY) || (c0040k = (NetworkUtils) photoRegistry.get(obj)) == null) {
            return null;
        }
        return c0040k.host;
    }

    /* renamed from: a */
    public static final Image getProfileImage(String str) {
        NetworkUtils c0040k;
        Image image;
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        synchronized (photoCache) {
            Image image2 = (Image) photoCache.get(str);
            Image image3 = image2;
            if (image2 == null) {
                try {
                    Hashtable hashtable = photoCache;
                    Image imageM1348r = XmppMailRuProtocol.readChunkedRecord(StringUtils.concat("upi", str)).toImage();
                    image3 = imageM1348r;
                    hashtable.put(str, imageM1348r);
                } catch (Throwable unused) {
                    if (pendingPhotoKey == null) {
                        pendingPhotoKey = str;
                        new AsyncTask(14, (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY) || (c0040k = (NetworkUtils) photoRegistry.get(str)) == null) ? null : c0040k.url);
                    }
                }
                image = image3;
            } else {
                image = image3;
            }
        }
        return image;
    }

    /* renamed from: a */
    public static final Vector getServiceContactIds(int i) {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!isContactOffline(objNextElement)) {
                NetworkUtils c0040k = (NetworkUtils) photoRegistry.get(objNextElement);
                if (c0040k != null && c0040k.type == 1) {
                    vectorM1213g.addElement(objNextElement);
                }
            }
        }
        return vectorM1213g;
    }

    /* renamed from: c */
    public static final Vector getAllContactIds() {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!isContactOffline(objNextElement)) {
                vectorM1213g.addElement(objNextElement);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: d */
    public static final Vector getActiveContactIds() {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!(getContactStatus(objNextElement) == 2)) {
                vectorM1213g.addElement(objNextElement);
            }
        }
        return vectorM1213g;
    }

    /* renamed from: b */
    private static final int getContactStatus(Object obj) {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return 2;
        }
        try {
            return ((NetworkUtils) photoRegistry.get(obj)).status;
        } catch (Throwable unused) {
            return 2;
        }
    }

    /* renamed from: c */
    private static final boolean isContactOffline(Object obj) {
        return getContactStatus(obj) == 0;
    }

    /* renamed from: r */
    private static ByteBuffer serializeRegistry() {
        ByteBuffer c0043n = new ByteBuffer();
        c0043n.writeIntBE(photoRegistry.size());
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            NetworkUtils c0040k = (NetworkUtils) photoRegistry.get(enumerationKeys.nextElement());
            c0043n.writeIntBE(c0040k.type).writeIntBE(c0040k.port).writeStringUTF16(c0040k.host).writeStringLatin1(c0040k.url).writeIntBE(c0040k.status).writeStringLatin1(c0040k.protocol);
        }
        return c0043n;
    }

    /* renamed from: e */
    public static final void clearPhotoCache() {
        photoCache = new Hashtable();
    }

    /* renamed from: a */
    public static final Object[] createAuthRequest(StringBuffer stringBuffer) {
        Object[] objArr = new Object[9];
        objArr[0] = NetworkUtils.longToHex(5522759);
        objArr[2] = NetworkUtils.bufToStringCached(stringBuffer);
        return objArr;
    }

    /* renamed from: a */
    public static final Object[] createUploadRequest(String str, StringBuffer stringBuffer) {
        Object[] objArr = new Object[9];
        objArr[0] = NetworkUtils.longToHex(1414745936);
        objArr[2] = str;
        objArr[3] = NetworkUtils.bufToStringCached(stringBuffer).getBytes();
        return objArr;
    }

    /* renamed from: a */
    public static final Object[] submitAsync(Object[] objArr) {
        objArr[7] = new AsyncTask(5, objArr);
        return objArr;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v19, resolved type: java.lang.Object[] */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: b */
    public static final void executeWithReauth(Object[] objArr) throws InterruptedException {
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        Object[] objArrM1151a = executeHttpRequest(objArr, c0028ba);
        if (objArr[8] != null) {
            objArr[4] = objArrM1151a;
            return;
        }
        if (IOUtils.isHttpSuccess(objArrM1151a) && JsonParser.isSuccess(JsonParser.parseJson(((ByteBuffer) objArrM1151a[3]).duplicate()))) {
            objArr[4] = objArrM1151a;
            return;
        }
        objArr[8] = objArr;
        MrimAccount c0028ba2 = (MrimAccount) AppState.getAccount();
        Object[] objArrM1147a = createAuthRequest(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_VERY_LONG_API_1)).append(c0028ba2.login).append(AppState.getString(StateKeys.STR_RES_PROTOCOL_TAG_3)).append(c0028ba2.password).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)));
        objArrM1147a[8] = objArrM1147a;
        ((AsyncTask) submitAsync(objArrM1147a)[7]).thread.join();
        c0028ba.jabberId = (String) objArrM1147a[6];
        objArr[4] = executeHttpRequest(objArr, c0028ba);
    }

    /* renamed from: a */
    private static final Object[] executeHttpRequest(Object[] objArr, MrimAccount c0028ba) {
        String strM1215a;
        HttpClient c0024ax = null;
        try {
            try {
                try {
                    try {
                        NetworkLock.acquireNetworkLock();
                        String str = (String) objArr[5];
                        if (str == null) {
                            strM1215a = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_LONG_URL_5)).append(objArr[2]));
                        } else {
                            strM1215a = str;
                        }
                        HttpClient c0024axM629a = HttpClient.createHttpClient(strM1215a, c0028ba, 1);
                        c0024ax = c0024axM629a;
                        Object[] objArrM1152a = sendHttpRequest(objArr, c0024axM629a);
                        HttpClient.closeAndUpdateStats(c0024ax);
                        NetworkLock.releaseNetworkLock();
                        return objArrM1152a;
                    } catch (ConnectionNotFoundException e) {
                        Object[] objArrM798a = IOUtils.createConnectError((Throwable) null);
                        HttpClient.closeAndUpdateStats(c0024ax);
                        NetworkLock.releaseNetworkLock();
                        return objArrM798a;
                    }
                } catch (Throwable th) {
                    Object[] objArrM801d = IOUtils.createReceiveError((Throwable) null);
                    HttpClient.closeAndUpdateStats(c0024ax);
                    NetworkLock.releaseNetworkLock();
                    return objArrM801d;
                }
            } catch (IllegalArgumentException e2) {
                Object[] objArrM799b = IOUtils.createAuthError((Throwable) null);
                HttpClient.closeAndUpdateStats(c0024ax);
                NetworkLock.releaseNetworkLock();
                return objArrM799b;
            } catch (SecurityException e3) {
                Object[] objArrM800c = IOUtils.createSendError((Throwable) null);
                HttpClient.closeAndUpdateStats(c0024ax);
                NetworkLock.releaseNetworkLock();
                return objArrM800c;
            }
        } catch (Throwable th2) {
            HttpClient.closeAndUpdateStats(c0024ax);
            NetworkLock.releaseNetworkLock();
            throw th2;
        }
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [java.lang.Object[], java.lang.Throwable] */
    /* renamed from: a */
    private static final Object[] sendHttpRequest(Object[] objArr, HttpClient c0024ax) {
        Object[] M1155b;
        try {
            c0024ax.setRequestMethod((String) objArr[0]);
            setHeaderFromState(c0024ax, 919726, 788668);
            setHeaderFromState(c0024ax, 657608, 329938);
            setOptionalHeader(c0024ax, 395489, ((MrimAccount) AppState.getAccount()).jabberId);
            byte[] bArr = (byte[]) objArr[3];
            if (bArr != null) {
                setHeaderFromState(c0024ax, 788628, 2164851);
                c0024ax.writeData(bArr, bArr.length);
            }
            M1155b = readHttpResponse(objArr, c0024ax);
            return M1155b;
        } catch (Throwable th) {
            return IOUtils.createProtocolError(th);
        }
    }

    /* renamed from: a */
    public static final void setHeaderFromState(HttpClient c0024ax, int i, int i2) throws IOException {
        setOptionalHeader(c0024ax, i, AppState.getString(i2));
    }

    /* renamed from: a */
    private static void setOptionalHeader(HttpClient c0024ax, int i, String str) throws IOException {
        if (str != null) {
            c0024ax.setRequestProperty(AppState.getString(i), str);
        }
    }

    /* JADX WARN: Type inference failed for: r0v7, types: [java.lang.Object[], java.lang.Throwable] */
    /* renamed from: b */
    private static final Object[] readHttpResponse(Object[] objArr, HttpClient c0024ax) {
        Object[] M804a;
        try {
            int iM634a = c0024ax.getResponseCode();
            int i = 0;
            while (true) {
                try {
                    String headerFieldKey = ((javax.microedition.io.HttpConnection) c0024ax.connection).getHeaderFieldKey(i);
                    String headerField = ((javax.microedition.io.HttpConnection) c0024ax.connection).getHeaderField(i);
                    if (headerFieldKey == null && headerField == null) {
                        break;
                    }
                    if (headerFieldKey != null && headerField != null && headerField.startsWith(AppState.getString(StateKeys.STR_RES_PARAM_4)) && StringUtils.matchesKey(657623, StringUtils.intern(headerFieldKey.toLowerCase()))) {
                        objArr[6] = StringUtils.prefix(headerField, headerField.indexOf(59));
                    }
                    i++;
                } catch (Throwable unused) {
                }
            }
            M804a = IOUtils.createHttpRequest(iM634a, StringUtils.intern(Integer.toString(iM634a)), new ByteBuffer(c0024ax));
            return M804a;
        } catch (Throwable th) {
            return IOUtils.createGenericError(th);
        }
    }

    /* renamed from: c */
    public static final Object[] getAsyncResult(Object[] objArr) {
        Object[] objArr2 = (Object[]) objArr[4];
        if (objArr2 != null) {
            return objArr2;
        }
        return null;
    }

    /* renamed from: f */
    public static final void showMapScreen() {
        initMapState();
        AppState.setInt(StateKeys.INT_CONNECTION_STATE, 6);
        Screen c0013amM75b = ScreenManager.createScreen(1578);
        mapScreen = c0013amM75b;
        setMapSoftKeys(c0013amM75b);
        ScreenManager.pushScreen(c0013amM75b);
        TabBar.ensureSearchTab();
        TabBar.findTab(6, (Account) null);
        TabBar.scrollEnabled = AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE);
        if (AppState.getBool(StateKeys.FLAG_REFRESH_CONTACTS)) {
            return;
        }
        ScreenBuilder.openScreen(178);
    }

    /* renamed from: g */
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

    /* renamed from: s */
    private static final void initMapState() {
        if (mapInitialized) {
            return;
        }
        mapInitialized = true;
        int i = ScreenManager.createScreen(1578).contentHeight;
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
        AppState.pool[StateKeys.VEC_CHATROOM_LIST] = NetworkUtils.newVector();
        AppState.pool[StateKeys.OBJ_SEARCH_PARAMS_1] = NetworkUtils.newVector();
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
        AppState.pool[StateKeys.OBJ_SEARCH_PARAMS_2] = NetworkUtils.newVector();
        new AsyncTask(8);
        MapRenderer.syncLock = new Object();
        StringUtils.initGeoRegions();
        MapRenderer.invalidate();
        MmpContact.routeRegions = NetworkUtils.newVector();
        MmpContact.routePoints = NetworkUtils.newVector();
        MmpContact.nearestPoints = NetworkUtils.newVector();
        MmpContact.lastTokenPair = new long[2];
        MmpContact.currentTokenPair = new long[2];
        MapRenderer.animationSteps = NetworkUtils.newVector();
        if (AppState.getBool(StateKeys.FLAG_GPS_ACTIVE)) {
            XmppContactGroup.stopMapAnimation(AppState.getVector(StateKeys.VEC_PHOTO_QUEUE));
        }
        AppState.pool[StateKeys.SLOT_MAP_DATA] = NetworkUtils.newVector();
        MapRenderer.needsRedraw = true;
        AppState.setLong(StateKeys.TIMESTAMP_MAP_SCROLL, System.currentTimeMillis() - 90);
        new AsyncTask(10);
        loadSavedMapData();
    }

    /* renamed from: d */
    private static final void setMapSoftKeys(Screen c0013am) {
        c0013am.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_MENU), AppState.getString(AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE) ? 1050 : 328), 20, 0, 0);
    }

    /* renamed from: a */
    public static final void toggleMapControls(Screen c0013am) {
        if (AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            return;
        }
        toggleScrollMode();
        setMapSoftKeys(c0013am);
    }

    /* renamed from: b */
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

    /* renamed from: c */
    public static final void handleMapSwitch(Screen c0013am) {
        if (AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE)) {
            AppState.setInt(StateKeys.INT_MAP_SCROLL_DIRECTION, 3);
        } else {
            toggleMapControls(c0013am);
        }
    }

    /* renamed from: h */
    public static final void toggleScrollMode() {
        boolean z = !AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE);
        boolean z2 = z;
        AppState.setBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, z);
        if (!z2) {
            AppState.setInt(StateKeys.FLAG_MAP_LOADING, 0);
        }
        TabBar.scrollEnabled = z2;
    }

    /* renamed from: a */
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

    /* renamed from: i */
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
        Screen c0013amM75b = ScreenManager.createScreen(1717);
        for (int i = 0; i < size; i++) {
            MapPoint c0014an = (MapPoint) vectorM614m.elementAt(i);
            c0013amM75b.addIconItemWithData(-1, c0014an.name, 6, c0014an);
        }
        ScreenManager.showScreen(c0013amM75b);
        return 0;
    }

    /* renamed from: j */
    public static final Enumeration getRouteElements() {
        return AppState.getVector(StateKeys.VEC_PHOTO_QUEUE).elements();
    }

    /* renamed from: k */
    public static final boolean hasRoutePoints() {
        return AppState.getVector(StateKeys.VEC_PHOTO_QUEUE).size() > 0;
    }

    /* renamed from: a */
    public static final void removeRoutePoint(MapPoint c0014an) {
        Vector vectorM614m = AppState.getVector(StateKeys.VEC_PHOTO_QUEUE);
        vectorM614m.removeElement(c0014an);
        XmppContactGroup.saveMapPoints(vectorM614m, 226);
    }

    /* renamed from: l */
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

    /* renamed from: m */
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

    /* renamed from: a */
    public static final void selectMapItem(ListItem interfaceC0044o) {
        if (interfaceC0044o.isSelected()) {
            showMapView();
            MapRenderer.setPosition(interfaceC0044o.getWidth(), interfaceC0044o.getBaseHeight());
            MapRenderer.setZoom(interfaceC0044o.getCommandCount());
            activeMapItem = interfaceC0044o;
        }
    }

    /* renamed from: n */
    public static final void showMapView() {
        initMapState();
        AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
        MapRenderer.invalidate();
    }
}
