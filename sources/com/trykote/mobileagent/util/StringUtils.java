package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextBox;
import javax.microedition.rms.RecordStore;

public final class StringUtils {

    public static Vector internCache;

    public static boolean isKnownDevice1;

    public static boolean isKnownDevice2;

    private static final int INTERN_CACHE_INITIAL_CAPACITY = 128;
    private static final int MAX_INTERNED_LENGTH = 256;
    private static final int INTERN_HOT_ZONE_SIZE = 64;
    private static final int INTERN_CACHE_LAST_INDEX = INTERN_CACHE_INITIAL_CAPACITY - 1;

    public static void initInternCache() {
        internCache = new Vector(INTERN_CACHE_INITIAL_CAPACITY);
    }

    public static final String decodeFromBytes(byte[] data, int offset) {
        ByteArrayInputStream byteStream = null;
        DataInputStream dataStream = null;
        try {
            try {
                byteStream = (ByteArrayInputStream) IOUtils.registerResource((Object) new ByteArrayInputStream(data, offset, data.length - offset));
                dataStream = (DataInputStream) IOUtils.registerResource((Object) new DataInputStream(byteStream));
                String result = intern(dataStream.readUTF());
                IOUtils.closeInput((InputStream) dataStream);
                IOUtils.closeInput((InputStream) byteStream);
                return result;
            } catch (Throwable unused) {
                String empty = AppState.emptyStr;
                IOUtils.closeInput((InputStream) dataStream);
                IOUtils.closeInput((InputStream) byteStream);
                return empty;
            }
        } catch (RuntimeException e) {
            IOUtils.closeInput((InputStream) dataStream);
            IOUtils.closeInput((InputStream) byteStream);
            throw e;
        } catch (Error e) {
            IOUtils.closeInput((InputStream) dataStream);
            IOUtils.closeInput((InputStream) byteStream);
            throw e;
        }
    }

    public static final boolean isEmpty(String str) {
        return str != null && str.length() == 0;
    }

    public static final boolean matchesEncoded(String str, int packedChars) {
        long packed = packedChars;
        int length = str.length();
        for (int idx = 0; idx < length; idx++) {
            if (str.charAt(idx) != (packed & 255)) {
                return false;
            }
            packed >>>= 8;
        }
        return packed == 0;
    }

    public static final boolean matchesKey(int key, String str) {
        if (str == null) {
            return false;
        }
        if (key <= AppState.PACKED_STRING_THRESHOLD) {
            return equals(AppState.getString(key), str);
        }
        byte[] bytes = ResourceAccessor.bytes(StringResKeys.RES_STRING_DATA);
        int expectedLen = key >> 16;
        int idx = expectedLen;
        if (expectedLen != str.length()) {
            return false;
        }
        int baseOffset = key & 65535;
        do {
            idx--;
            if (idx < 0) {
                return true;
            }
        } while (str.charAt(idx) == bytes[baseOffset + idx]);
        return false;
    }

    public static final boolean equalsObj(String str, Object obj) {
        return equals(str, (String) obj);
    }

    public static final String getDomain(String str) {
        return suffix(str, str.indexOf(64) + 1);
    }

    public static final boolean equals(String a, String b) {
        if (a == b) {
            return true;
        }
        if (b == null) {
            return false;
        }
        return a.equals(b);
    }

    public static final String concatKey(int key, String str) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(key)).append(str));
    }

    public static final String concatKeyObj(int key, Object obj) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(key)).append(obj));
    }

    public static final String concat(String a, String b) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(a).append(b));
    }

    public static final String[] listRecordStores() {
        String[] stores = RecordStore.listRecordStores();
        if (stores != null) {
            for (int idx = stores.length - 1; idx >= 0; idx--) {
                stores[idx] = intern(stores[idx]);
            }
        }
        return stores;
    }

    public static final String fromBuffer(StringBuffer stringBuffer) {
        if (stringBuffer != null) {
            return intern(stringBuffer.toString());
        }
        return null;
    }

    public static final String substring(String str, int start, int end) {
        return intern(str.substring(start, end));
    }

    public static final String prefix(String str, int endIndex) {
        return intern(str.substring(0, endIndex));
    }

    public static final String extractBuffer(StringBuffer stringBuffer) {
        String extracted = fromBuffer(stringBuffer);
        stringBuffer.setLength(0);
        return extracted;
    }

    public static final String suffix(String str, int startIndex) {
        return intern(str.substring(startIndex));
    }

    public static final String getTextBoxString(TextBox textBox) {
        return Utils.defaultStr(intern(textBox.getString()));
    }

    public static final String intern(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() > MAX_INTERNED_LENGTH) {
            return str;
        }
        Vector vector = internCache;
        synchronized (vector) {
            int idx = vector.indexOf(str);
            if (idx >= 0) {
                String cached = Utils.getVectorString(vector, idx);
                if (idx != 0) {
                    vector.removeElementAt(idx);
                    vector.insertElementAt(cached, 0);
                }
                return cached;
            }
            if (vector.size() > INTERN_HOT_ZONE_SIZE) {
                if (vector.size() == INTERN_CACHE_INITIAL_CAPACITY) {
                    vector.removeElementAt(INTERN_CACHE_LAST_INDEX);
                }
                vector.insertElementAt(str, INTERN_HOT_ZONE_SIZE);
            } else {
                vector.addElement(str);
            }
            return str;
        }
    }

    public static final ByteBuffer createContactInfoCmd(MmpProtocol protocol, int contactId) {
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(16).writeShortLE(14).writeIntLE(protocol.serverId).writeShortLE(2000).writeShortBE(0).writeShortLE(1202).writeIntLE(contactId)), ObjectPool.integerOf(7), ObjectPool.integerOf(contactId)});
    }

    public static final void initTileCache() {
        int cacheCapacity = ContactState.isListActive() ? (((MapState.getViewportWidth() >> 7) + 2) * ((MapState.getViewportHeight() >> 7) + 2)) << 1 : ((MapState.getViewportWidth() >> 7) + 2) * ((MapState.getViewportHeight() >> 7) + 2);
        MapState.setTileCache(new LruCache(cacheCapacity));
        RuntimeState.setMaxPendingRequests(cacheCapacity);
    }

    public static final Image getTileImage(TileRequest tile) {
        Image image = (Image) getTileCache().get(tile);
        if (image == null && !RuntimeState.getSearchParams1().contains(tile)) {
            TileCache.enqueueTileRequest(tile);
        }
        return image;
    }

    private static final void pruneStaleRequests() {
        Vector items = RuntimeState.getSearchParams2();
        synchronized (items) {
            Vector pendingReqs = ChatState.getTileRequestQueue();
            synchronized (pendingReqs) {
                for (int idx = pendingReqs.size() - 1; idx >= 0; idx--) {
                    Object req = pendingReqs.elementAt(idx);
                    if (!items.contains(req)) {
                        pendingReqs.removeElement(req);
                    }
                }
            }
        }
    }

    public static final void tileLoaderLoop() {
        Image tileImage = null;
        int retryCount = 4;
        while (!MapState.isTilesReady()) {
            Object[] statusArr = (Object[]) MapState.getTileRequestArray();
            while (true) {
                if (!(ChatState.getTileRequestQueue().size() == 0)) {
                    break;
                }
                XmppContactGroup.removeContactInfoFromQueue(statusArr);
                try {
                    Thread.sleep(100);
                } catch (Throwable unused) {
                }
            }
            TileRequest tileReq = TileCache.peekTileRequest();
            int tileType = tileReq.tileType;
            statusArr[1] = new StringBuffer().append(AppState.getString(tileType == 3 ? 997 : tileType == 1 ? 998 : 999)).append(Utils.formatSize(RuntimeState.getXmppTrafficBytes())).toString();
            XmppContactGroup.addContactInfoToQueue(statusArr);
            try {
                Image cachedImage = (tileReq.tileType == 1 && MapState.isTileCacheEnabled()) ? TileCache.loadTileFromCache(tileReq) : null;
                tileImage = cachedImage;
                if (cachedImage == null) {
                    tileImage = TileCache.fetchTileImage(tileReq);
                }
            } catch (IOException unused2) {
                int remaining = retryCount;
                retryCount = remaining - 1;
                if (remaining > 0) {
                    pruneStaleRequests();
                    Vector items = ChatState.getTileRequestQueue();
                    synchronized (items) {
                        if (items.removeElement(tileReq)) {
                            TileCache.enqueueTileRequest(tileReq);
                        }
                    }
                } else {
                    MapState.setTilesReady(true);
                }
            } catch (Throwable unused3) {
                TileCache.removeTileRequest(tileReq);
            }
            if (tileImage == null) {
                if (tileType == 3) {
                    Vector pendingReqs = RuntimeState.getSearchParams1();
                    while (pendingReqs.size() >= RuntimeState.getMaxPendingRequests()) {
                        pendingReqs.removeElementAt(0);
                    }
                    pendingReqs.addElement(tileReq);
                    XmppContactGroup.flagSyncRequired();
                } else {
                    cacheTileImage(tileReq, MapState.getMenuLabelsImage());
                }
                throw new RuntimeException();
            }
            retryCount = 4;
            cacheTileImage(tileReq, tileImage);
            TileCache.removeTileRequest(tileReq);
            pruneStaleRequests();
        }
    }

    private static final void cacheTileImage(TileRequest tile, Image image) {
        try {
            getTileCache().put(tile, image, 1);
            MapRenderer.needsRedraw = true;
        } catch (Throwable unused) {
        }
    }

    public static final void clearSatelliteTiles() {
        Enumeration keys = getTileCache().keys();
        while (keys.hasMoreElements()) {
            TileRequest tile = (TileRequest) keys.nextElement();
            if (tile.tileType == 3) {
                getTileCache().remove(tile);
            }
        }
    }

    private static final LruCache getTileCache() {
        return (LruCache) MapState.getTileCache();
    }

    private static final Vector createRegionVector() {
        Vector result = ObjectPool.newVector();
        result.addElement(ResourceAccessor.str(StringResKeys.STR_CITY_LIST));
        return result;
    }

    private static final void addXmlChildTexts(Vector result, Object parentElement) {
        Vector children = ((XmlElement) parentElement).children;
        for (int i = 0; i < Utils.vectorSize(children); i++) {
            result.addElement(getXmlText((XmlElement) children.elementAt(i)));
        }
    }

    private static final String getXmlText(XmlElement element) {
        String attrText = element.getLongKeyAttr(110);
        return attrText != null ? attrText : fromBuffer(element.textContent);
    }

    public static final void showRegionSelector() {
        resetRegForm();
        RegistrationState.setRegionData(new XmlParser(new ByteBuffer(ObjectPool.unpackChars(25135), 41000)).parse().children);
        StringBuffer sb = ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_SEARCH_TITLE));
        Vector items = RegistrationState.getRegionData();
        for (int i = 0; i < Utils.vectorSize(items); i++) {
            sb.append((char) 0).append(getXmlText((XmlElement) items.elementAt(i)));
        }
        RegistrationState.setRegionNames(sb);
        Screens.regionChoice(null).show();
    }

    public static final void resetRegForm() {
        AppState.clearRange(ContactKeys.SLOT_CONTACT_JID, RegistrationKeys.SLOT_REG_FIELD_2);
        RegistrationState.resetSearchParams();
    }

    public static final void updateRegDropdowns(ListView screen, Object selectedItem) {
        MenuItem menuItem = (MenuItem) selectedItem;
        int selectedIdx = ((Integer) ((Object[]) menuItem.data)[0]).intValue();
        String dropdownTitle = menuItem.title;
        String countryLabel = ResourceAccessor.str(StringResKeys.STR_LABEL_COUNTRY);
        String regionLabel = ResourceAccessor.str(StringResKeys.STR_LABEL_REGION);
        String cityLabel = ResourceAccessor.str(StringResKeys.STR_LABEL_CITY);
        String monthLabel = ResourceAccessor.str(StringResKeys.STR_LABEL_MONTH);
        String ageLabel = ResourceAccessor.str(StringResKeys.STR_LABEL_AGE_RANGE);
        String genderLabel = ResourceAccessor.str(StringResKeys.STR_LABEL_GENDER);
        MenuItem regionItem = null;
        MenuItem cityItem = null;
        MenuItem monthDropdown = null;
        MenuItem ageDropdown = null;
        MenuItem genderDropdown = null;
        int countryIdx = 0;
        Vector items = screen.menuItems;
        for (int idx = Utils.vectorSize(items) - 1; idx >= 0; idx--) {
            MenuItem dropdownItem = (MenuItem) items.elementAt(idx);
            if (dropdownItem.id == 9) {
                String itemTitle = dropdownItem.title;
                if (itemTitle.startsWith(monthLabel)) {
                    monthDropdown = dropdownItem;
                } else if (itemTitle.startsWith(countryLabel)) {
                    countryIdx = ((Integer) ((Object[]) dropdownItem.data)[0]).intValue();
                } else if (itemTitle.startsWith(regionLabel)) {
                    regionItem = dropdownItem;
                } else if (itemTitle.startsWith(cityLabel)) {
                    cityItem = dropdownItem;
                } else if (itemTitle.startsWith(ageLabel)) {
                    ageDropdown = dropdownItem;
                } else if (itemTitle.startsWith(genderLabel)) {
                    genderDropdown = dropdownItem;
                }
            }
        }
        if (equals(dropdownTitle, countryLabel)) {
            MenuItem regionDropdown = regionItem;
            Vector regions = createRegionVector();
            if (selectedIdx > 0) {
                addXmlChildTexts(regions, RegistrationState.getRegionData().elementAt(selectedIdx - 1));
            }
            regionDropdown.setChoices(regions, 0, regionLabel);
            cityItem.setChoices(Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_CITY_LIST)), 0, cityLabel);
        } else if (equals(dropdownTitle, regionLabel)) {
            MenuItem cityDropdown = cityItem;
            int selectedCountryIdx = countryIdx;
            Vector cities = createRegionVector();
            if (selectedIdx > 0) {
                addXmlChildTexts(cities, ((XmlElement) RegistrationState.getRegionData().elementAt(selectedCountryIdx - 1)).children.elementAt(selectedIdx - 1));
            }
            cityDropdown.setChoices(cities, 0, cityLabel);
        } else if (equals(dropdownTitle, monthLabel)) {
            ageDropdown.setChoices(Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_AGE_RANGES)), 0, ageLabel);
            genderDropdown.setChoices(Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_GENDER_LIST)), 0, genderLabel);
        } else if (equals(dropdownTitle, ageLabel) || equals(dropdownTitle, genderLabel)) {
            monthDropdown.setChoices(Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_MONTH_NAMES)), 0, monthLabel);
        }
        screen.rebuildItems();
    }

    public static final String[] buildRegData() {
        String[] fields = new String[16];
        String inputStr = Utils.defaultStr(ContactState.getJid());
        if (!isEmpty(inputStr)) {
            String lowered = intern(inputStr.toLowerCase());
            int atIdx = lowered.indexOf(64);
            if (atIdx >= 0) {
                fields[0] = prefix(lowered, atIdx);
                fields[1] = suffix(lowered, atIdx + 1);
            } else {
                fields[0] = lowered;
                fields[1] = suffix(Utils.splitAndGet(694, RegistrationState.getRegionCode()), 1);
            }
            return fields;
        }
        fields[2] = Utils.defaultStr(ContactState.getDisplayName());
        fields[3] = Utils.defaultStr(RegistrationState.getFirstName());
        fields[4] = Utils.defaultStr(RegistrationState.getLastName());
        fields[5] = intToStringPositive(1481);
        fields[7] = intToStringPositive(1482);
        fields[8] = intToStringPositive(1483);
        int countryIdx = RegistrationState.getSearchCountry();
        if (countryIdx > 0) {
            XmlElement countryElem = (XmlElement) RegistrationState.getRegionData().elementAt(countryIdx - 1);
            fields[15] = countryElem.getLongKeyAttr(105);
            int regionIdx = RegistrationState.getSearchRegion();
            if (regionIdx > 0) {
                XmlElement regionElem = (XmlElement) countryElem.children.elementAt(regionIdx - 1);
                fields[11] = regionElem.getLongKeyAttr(105);
                fields[15] = null;
                int cityIdx = RegistrationState.getSearchCity();
                if (cityIdx > 0) {
                    fields[11] = ((XmlElement) regionElem.children.elementAt(cityIdx - 1)).getLongKeyAttr(105);
                }
            }
        }
        fields[12] = intToStringPositive(1484);
        fields[13] = intToStringPositive(1488);
        fields[14] = intToStringPositive(1489);
        if (RegistrationState.isSearchOnlineOnly()) {
            fields[9] = intern(Integer.toString(1));
        }
        return fields;
    }

    private static final String intToStringPositive(int key) {
        int value = AppState.getInt(key);
        if (value > 0) {
            return intern(Integer.toString(value));
        }
        return null;
    }

    public static final void initPlatform() {
        AppState.setObject(StringResKeys.STR_APP_NAME, (Object) AppState.getAppProperty(StringResKeys.STR_APP_NAME));
        while (Utils.parseInt((Object) Utils.defaultStr(SessionState.getRandomId())) <= 106) {
            try {
                throw new Throwable();
            } catch (Throwable unused) {
                SessionState.setRandomId(intern(Integer.toString(Utils.nextRandom())));
            }
        }
        setOrGenerateGuid(validateGuid(SessionState.getAppProperty(SessionKeys.SLOT_SESSION_HASH)));
        SessionState.setSessionToken(SessionState.getAppProperty(SessionKeys.SLOT_SESSION_TOKEN));
        SessionState.setSessionHash(new ByteBuffer().writeUInt(1029990694).writeRawString(Utils.defaultStr(SessionState.getRandomId())).writeLongBytes(263912257062L).writeRawString(formatVersion()).getStringAndClear());
        SessionState.setAccountLogin(getSystemProp(963));
        SessionState.setAccountPassword(getSystemProp(964));
        SessionState.setAccountServer(getSystemProp(1378));
        SessionState.setAccountTypeStr(getSystemProp(1380));
        SessionState.setAccountDisplayName(getSystemProp(1379));
        for (int i = 966; i >= 965; i--) {
            try {
                String inputStr = Utils.defaultStr(AppState.getString(i));
                AppState.clearIndex(i);
                Class.forName(inputStr);
                UIState.setKnownPlatform(true);
                break;
            } catch (Throwable unused2) {
            }
        }
        if (ResourceAccessor.str(StringResKeys.STR_APP_NAME).charAt(0) == '3' && ResourceAccessor.str(StringResKeys.STR_APP_NAME).charAt(2) == '9') {
            if (SessionState.getDeviceInfo() == null) {
                SessionState.setDeviceInfoFromPool(StringResKeys.STR_DEVICE_FEATURES);
            }
            SessionState.clearPlatformInfo();
            String credentials = intern(concat(SessionState.getAccountLogin(), SessionState.getAccountPassword()).toLowerCase());
            isKnownDevice2 = indexOfPackedLong(credentials, 7163382462464028531L) >= 0 || indexOfPacked(credentials, 842019699) == 0 || indexOfPacked(credentials, 842019703) == 0;
            isKnownDevice1 = indexOfPackedLong(credentials, 418380476270L) >= 0;
            UIState.setWifiConnection(indexOfPacked(credentials, 761620851) == 0 || indexOfPacked(credentials, 1903060322) == 0);
            UIState.setKnownDevice(isKnownDevice1 || isKnownDevice2);
            UIState.setAdvancedFeatures(UIState.isKnownPlatform() || indexOfPackedLong(credentials, 29113373327974771L) >= 0 || indexOfPacked(credentials, 6514035) == 0 || indexOfPacked(credentials, 6841203) == 0 || indexOfPacked(credentials, 6842227) == 0 || indexOfPacked(credentials, 29799) == 0);
            byte major = parseVersionByte(0);
            byte minor = parseVersionByte(1);
            byte patch = parseVersionByte(2);
            byte[] bytes = ResourceAccessor.bytes(StringResKeys.RES_SESSION_BYTES);
            bytes[13] = major;
            bytes[14] = minor;
            bytes[15] = patch;
            if (SessionState.getTimestampFirstRun() == 0) {
                SessionState.setTimestampFirstRun(System.currentTimeMillis());
                return;
            }
            return;
        }
        while (true) {
            Object appName = AppState.getObject(StringResKeys.STR_APP_NAME);
            AppState.setObject(StringResKeys.STR_APP_NAME, new Object[]{appName, appName, appName});
        }
    }

    private static final byte parseVersionByte(int partIndex) {
        try {
            return (byte) Utils.parseInt(Utils.split(ResourceAccessor.str(StringResKeys.STR_APP_NAME), '.').elementAt(partIndex));
        } catch (Throwable unused) {
            return (byte) 0;
        }
    }

    private static final String formatVersion() {
        String versionStr = ResourceAccessor.str(StringResKeys.STR_APP_NAME);
        String[] parts = new String[3];
        String empty = AppState.emptyStr;
        parts[0] = empty;
        parts[1] = empty;
        parts[2] = empty;
        int partIdx = 0;
        for (int pos = 0; pos < versionStr.length(); pos++) {
            char ch = versionStr.charAt(pos);
            if (ch == '.') {
                partIdx++;
            } else {
                parts[partIdx] = new StringBuffer().append(parts[partIdx]).append(ch).toString();
            }
        }
        while (parts[0].length() < 2) {
            parts[0] = new StringBuffer().append('0').append(parts[0]).toString();
        }
        while (parts[1].length() < 2) {
            parts[1] = new StringBuffer().append('0').append(parts[1]).toString();
        }
        while (parts[2].length() < 4) {
            parts[2] = new StringBuffer().append('0').append(parts[2]).toString();
        }
        return new ByteBuffer().writeRawString(parts[0]).writeRawString(parts[1]).writeRawString(parts[2]).getStringAndClear();
    }

    public static final String getSystemProp(int key) {
        return intern(System.getProperty(AppState.getString(key)));
    }

    private static final void setOrGenerateGuid(String guid) {
        if (guid != null) {
            MapState.setTileData(guid);
        }
        if (SessionState.getSessionKey() == null) {
            if (guid != null) {
                SessionState.setSessionKey(guid);
                return;
            }
            StringBuffer sb = ObjectPool.newStringBuffer();
            int part = 0;
            while (part < 2) {
                long seed = part == 0 ? System.currentTimeMillis() : (Utils.nextRandom() << 32) | Utils.parseInt((Object) Utils.defaultStr(SessionState.getRandomId()));
                for (int bitPos = 0; bitPos < 64; bitPos += 4) {
                    int nibble = ((int) (seed >>> (60 - bitPos))) & 15;
                    int baseChar = nibble < 10 ? 48 : 87;
                    sb.append((char) (nibble + baseChar));
                }
                part++;
            }
            SessionState.setSessionKeyFromBuffer(sb);
        }
    }

    private static final String validateGuid(String guid) {
        if (guid == null || guid.length() != 32) {
            return null;
        }
        for (int idx = 31; idx >= 0; idx--) {
            char ch = guid.charAt(idx);
            if (ch < '0' || ch > '9') {
                if (ch < 'a' || ch > 'f') {
                    return null;
                }
            }
        }
        return guid;
    }

    public static GeoRegion getGeoRegion() {
        return (GeoRegion) MapState.getGeoRegion2();
    }

    public static final void initGeoRegions() {
        MapState.setMapPoints(ObjectPool.newVector());
        MapState.setGeoRegion(new GeoRegion(ResourceAccessor.str(StringResKeys.STR_REGION_NAME_2), 4115426L, 7539707L, 4267459L, 7412592L));
        try {
            ByteBuffer geoBuffer = Base64.decode(MapState.getGeoSavedData());
            MapState.getMapPoints().removeAllElements();
            if (geoBuffer.length > 0) {
                for (int count = geoBuffer.readIntBE() - 1; count >= 0; count--) {
                    addGeoRegion(new GeoRegion(geoBuffer));
                }
            }
        } catch (Throwable unused) {
        }
        GeoRegion region = new GeoRegion(ResourceAccessor.str(StringResKeys.STR_REGION_NAME_1), 1866877L, 15815124L, 21989606L, 4133096L);
        region.centerLat = 10848141L;
        region.centerLon = 8758455L;
        MapState.setGeoRegion2(region);
    }

    private static void addGeoRegion(GeoRegion region) {
        Vector items = MapState.getMapPoints();
        if (items.contains(region)) {
            return;
        }
        items.addElement(region);
    }

    public static final boolean isInSavedRegion(long j, long j2) {
        Vector items = MapState.getMapPoints();
        for (int idx = Utils.vectorSize(items) - 1; idx >= 0; idx--) {
            GeoRegion region = (GeoRegion) items.elementAt(idx);
            if (region.containsPoint(j, j2) && indexOfPoolString(region.name, 995) < 0) {
                return true;
            }
        }
        return false;
    }

    public static final void parseGeoConfig(XmlElement element) {
        Vector children;
        if (element == null || (children = element.children) == null) {
            return;
        }
        MapState.getMapPoints().removeAllElements();
        String configUrl = element.getIntAttribute(PackedStringKeys.ATTR_TIMESTAMP);
        if (configUrl != null) {
            MapState.setGeoConfigUrl(configUrl);
        }
        for (int i = 0; i < Utils.vectorSize(children); i++) {
            XmlElement childElem = (XmlElement) children.elementAt(i);
            String tagName = childElem.tagName;
            if (matchesEncoded(tagName, 1936156018)) {
                Vector regionElements = childElem.children;
                for (int j = 0; j < Utils.vectorSize(regionElements); j++) {
                    XmlElement regionElem = (XmlElement) regionElements.elementAt(j);
                    GeoRegion region = new GeoRegion(regionElem.getLongKeyAttr(1701667182), regionElem.getAttrAsLong(28780), regionElem.getAttrAsLong(28788), regionElem.getAttrAsLong(28786), regionElem.getAttrAsLong(28770));
                    region.description = regionElem.getLongKeyAttr(25705);
                    region.centerLat = regionElem.getAttrAsLong(1852796003);
                    region.centerLon = regionElem.getAttrAsLong(1952541795);
                    region.precision = regionElem.getAttrAsInt(2054709613);
                    addGeoRegion(region);
                }
            } else if (matchesKey(PackedStringKeys.TAG_POINTS, tagName)) {
                ServiceRegistry.parseServiceConfig(1, childElem, true);
            }
        }
        try {
            MapState.resetGeoSavedData();
            ByteBuffer buffer = new ByteBuffer();
            Vector items = MapState.getMapPoints();
            int size = items.size();
            buffer.writeIntBE(size);
            for (int idx = 0; idx < size; idx++) {
                GeoRegion region = (GeoRegion) items.elementAt(idx);
                buffer.writeStringUTF16(region.name).writeLong(region.minLat).writeLong(region.maxLon).writeLong(region.maxLat).writeLong(region.minLon).writeStringUTF16(region.description).writeLong(region.centerLat).writeLong(region.centerLon).writeIntLE(region.precision);
            }
            MapState.setGeoSavedData(buffer.toBase64());
        } catch (Throwable unused) {
            MapState.resetGeoConfigUrl();
        }
    }

    public static int indexOfPacked(String text, int packedChars) {
        return text.indexOf(ObjectPool.unpackChars(packedChars));
    }

    public static int indexOfPackedLong(String text, long packedChars) {
        return text.indexOf(ObjectPool.unpackChars(packedChars));
    }

    public static int indexOfPoolString(String text, int poolKey) {
        return text.indexOf(AppState.getString(poolKey));
    }

    public static final String transliterate(String str) {
        boolean isUpperCase = false;
        String translitResult = null;
        String upperVariant;
        Vector sourceChars = Utils.splitByNull(ResourceAccessor.str(PackedStringKeys.TRANSLIT_TABLE_EXTENDED));
        Vector targetChars = Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_SOUND_LIST));
        Hashtable translitMap = new Hashtable();
        for (int idx = sourceChars.size() - 1; idx >= 0; idx--) {
            translitMap.put(sourceChars.elementAt(idx), targetChars.elementAt(idx));
        }
        String lowerAlphabet = ResourceAccessor.str(StringResKeys.STR_SOUND_TYPE_1);
        String upperAlphabet = ResourceAccessor.str(StringResKeys.STR_SOUND_TYPE_2);
        Hashtable caseMap = new Hashtable();
        StringBuffer sb = ObjectPool.newStringBuffer();
        for (int idx = lowerAlphabet.length() - 1; idx >= 0; idx--) {
            caseMap.put(extractBuffer(sb.append(lowerAlphabet.charAt(idx))), extractBuffer(sb.append(upperAlphabet.charAt(idx))));
        }
        int length = str.length();
        int pos = 0;
        while (pos < length) {
            String mapped = null;
            int matchLen = 3;
            while (true) {
                if (matchLen < 1) {
                    break;
                }
                try {
                    String chunk = substring(str, pos, pos + matchLen);
                    isUpperCase = Character.isUpperCase(chunk.charAt(0));
                    translitResult = (String) translitMap.get(intern(chunk.toLowerCase()));
                    mapped = translitResult;
                } catch (Throwable unused) {
                }
                if (translitResult != null) {
                    if (isUpperCase && (upperVariant = (String) caseMap.get(prefix(mapped, 1))) != null) {
                        mapped = mapped.length() == 1 ? upperVariant : concat(upperVariant, suffix(mapped, 1));
                    }
                    pos += matchLen - 1;
                    sb.append(mapped);
                } else {
                    matchLen--;
                }
            }
            if (mapped == null) {
                sb.append(str.charAt(pos));
            }
            pos++;
        }
        ObjectPool.releaseVector(sourceChars);
        ObjectPool.releaseVector(targetChars);
        return ObjectPool.toStringAndRelease(sb);
    }
}
