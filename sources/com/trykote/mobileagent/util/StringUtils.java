package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
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

/* renamed from: a */
/* loaded from: MobileAgent_3.9.jar:a.class */
public final class StringUtils {

    /* renamed from: a */
    public static Vector internCache;

    /* renamed from: b */
    public static boolean isKnownDevice1;

    /* renamed from: c */
    public static boolean isKnownDevice2;

    private static final int INTERN_CACHE_INITIAL_CAPACITY = 128;

    public static void initInternCache() {
        internCache = new Vector(INTERN_CACHE_INITIAL_CAPACITY);
    }

    /* renamed from: a */
    public static final String decodeFromBytes(byte[] bArr, int i) {
        ByteArrayInputStream byteArrayInputStream = null;
        DataInputStream dataInputStream = null;
        try {
            try {
                ByteArrayInputStream byteArrayInputStream2 = (ByteArrayInputStream) IOUtils.registerResource((Object) new ByteArrayInputStream(bArr, i, bArr.length - i));
                byteArrayInputStream = byteArrayInputStream2;
                DataInputStream dataInputStream2 = (DataInputStream) IOUtils.registerResource((Object) new DataInputStream(byteArrayInputStream2));
                dataInputStream = dataInputStream2;
                String result = intern(dataInputStream2.readUTF());
                IOUtils.closeInput((InputStream) dataInputStream);
                IOUtils.closeInput((InputStream) byteArrayInputStream);
                return result;
            } catch (Throwable unused) {
                String str = AppState.emptyStr;
                IOUtils.closeInput((InputStream) dataInputStream);
                IOUtils.closeInput((InputStream) byteArrayInputStream);
                return str;
            }
        } catch (RuntimeException e) {
            IOUtils.closeInput((InputStream) dataInputStream);
            IOUtils.closeInput((InputStream) byteArrayInputStream);
            throw e;
        } catch (Error e) {
            IOUtils.closeInput((InputStream) dataInputStream);
            IOUtils.closeInput((InputStream) byteArrayInputStream);
            throw e;
        }
    }

    /* renamed from: a */
    public static final boolean isEmpty(String str) {
        return str != null && str.length() == 0;
    }

    /* renamed from: a */
    public static final boolean matchesEncoded(String str, int i) {
        long j = i;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            if (str.charAt(i2) != (j & 255)) {
                return false;
            }
            j >>>= 8;
        }
        return j == 0;
    }

    /* renamed from: a */
    public static final boolean matchesKey(int i, String str) {
        if (str == null) {
            return false;
        }
        if (i <= AppState.PACKED_STRING_THRESHOLD) {
            return equals(AppState.getString(i), str);
        }
        byte[] bytes = AppState.getBytes(StringResKeys.RES_STRING_DATA);
        int i2 = i >> 16;
        int i3 = i2;
        if (i2 != str.length()) {
            return false;
        }
        int i4 = i & 65535;
        do {
            i3--;
            if (i3 < 0) {
                return true;
            }
        } while (str.charAt(i3) == bytes[i4 + i3]);
        return false;
    }

    /* renamed from: a */
    public static final boolean equalsObj(String str, Object obj) {
        return equals(str, (String) obj);
    }

    /* renamed from: b */
    public static final String getDomain(String str) {
        return suffix(str, str.indexOf(64) + 1);
    }

    /* renamed from: a */
    public static final boolean equals(String str, String str2) {
        if (str == str2) {
            return true;
        }
        if (str2 == null) {
            return false;
        }
        return str.equals(str2);
    }

    /* renamed from: b */
    public static final String concatKey(int i, String str) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(i)).append(str));
    }

    /* renamed from: a */
    public static final String concatKeyObj(int i, Object obj) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(i)).append(obj));
    }

    /* renamed from: b */
    public static final String concat(String str, String str2) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(str).append(str2));
    }

    /* renamed from: a */
    public static final String[] listRecordStores() {
        String[] stores = RecordStore.listRecordStores();
        if (stores != null) {
            int length = stores.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                stores[length] = intern(stores[length]);
            }
        }
        return stores;
    }

    /* renamed from: a */
    public static final String fromBuffer(StringBuffer stringBuffer) {
        if (stringBuffer != null) {
            return intern(stringBuffer.toString());
        }
        return null;
    }

    /* renamed from: a */
    public static final String substring(String str, int i, int i2) {
        return intern(str.substring(i, i2));
    }

    /* renamed from: b */
    public static final String prefix(String str, int i) {
        return intern(str.substring(0, i));
    }

    /* renamed from: b */
    public static final String extractBuffer(StringBuffer stringBuffer) {
        String extracted = fromBuffer(stringBuffer);
        stringBuffer.setLength(0);
        return extracted;
    }

    /* renamed from: c */
    public static final String suffix(String str, int i) {
        return intern(str.substring(i));
    }

    /* renamed from: a */
    public static final String getTextBoxString(TextBox textBox) {
        return Utils.defaultStr(intern(textBox.getString()));
    }

    /* renamed from: c */
    public static final String intern(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() > 256) {
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
            if (vector.size() > 64) {
                if (vector.size() == 128) {
                    vector.removeElementAt(127);
                }
                vector.insertElementAt(str, 64);
            } else {
                vector.addElement(str);
            }
            return str;
        }
    }

    /* renamed from: a */
    public static final ByteBuffer createContactInfoCmd(MmpProtocol protocol, int i) {
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(16).writeShortLE(14).writeIntLE(protocol.serverId).writeShortLE(2000).writeShortBE(0).writeShortLE(1202).writeIntLE(i)), ResourceManager.integerOf(7), ResourceManager.integerOf(i)});
    }

    /* renamed from: b */
    public static final void initTileCache() {
        int iM586d = AppState.getBool(ContactKeys.FLAG_CONTACT_LIST_ACTIVE) ? (((AppState.getInt(MapKeys.MAP_VIEWPORT_WIDTH) >> 7) + 2) * ((AppState.getInt(MapKeys.MAP_VIEWPORT_HEIGHT) >> 7) + 2)) << 1 : ((AppState.getInt(MapKeys.MAP_VIEWPORT_WIDTH) >> 7) + 2) * ((AppState.getInt(MapKeys.MAP_VIEWPORT_HEIGHT) >> 7) + 2);
        AppState.pool[MapKeys.OBJ_TILE_CACHE] = new LruCache(iM586d);
        AppState.setInt(RuntimeKeys.INT_MAX_PENDING_REQUESTS, iM586d);
    }

    /* renamed from: a */
    public static final Image getTileImage(ResourceManager tile) {
        Image image = (Image) getTileCache().get(tile);
        if (image == null && !AppState.getVector(RuntimeKeys.OBJ_SEARCH_PARAMS_1).contains(tile)) {
            ResourceManager.enqueueTileRequest(tile);
        }
        return image;
    }

    /* renamed from: k */
    private static final void pruneStaleRequests() {
        Vector items = AppState.getVector(RuntimeKeys.OBJ_SEARCH_PARAMS_2);
        synchronized (items) {
            Vector pendingReqs = AppState.getVector(ChatKeys.VEC_CHATROOM_LIST);
            synchronized (pendingReqs) {
                int size = pendingReqs.size();
                while (true) {
                    size--;
                    if (size >= 0) {
                        Object req = pendingReqs.elementAt(size);
                        if (!items.contains(req)) {
                            pendingReqs.removeElement(req);
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    /* renamed from: c */
    public static final void tileLoaderLoop() {
        Image tileImage = null;
        int i = 4;
        while (0 == AppState.getInt(MapKeys.FLAG_TILES_READY)) {
            Object[] objArr = (Object[]) AppState.pool[MapKeys.OBJ_TILE_REQUEST_ARRAY];
            while (true) {
                if (!(AppState.getVector(ChatKeys.VEC_CHATROOM_LIST).size() == 0)) {
                    break;
                }
                XmppContactGroup.removeContactInfoFromQueue(objArr);
                try {
                    Thread.sleep(100);
                } catch (Throwable unused) {
                }
            }
            ResourceManager tileReq = ResourceManager.peekTileRequest();
            int i2 = tileReq.tileType;
            objArr[1] = new StringBuffer().append(AppState.getString(i2 == 3 ? 997 : i2 == 1 ? 998 : 999)).append(Utils.formatSize(AppState.getInt(RuntimeKeys.INT_XMPP_TRAFFIC_BYTES))).toString();
            XmppContactGroup.addContactInfoToQueue(objArr);
            try {
                Image cachedImage = (tileReq.tileType == 1 && AppState.getBool(MapKeys.FLAG_TILE_CACHE_ENABLED)) ? TileCache.loadTileFromCache(tileReq) : null;
                tileImage = cachedImage;
                if (cachedImage == null) {
                    tileImage = TileCache.fetchTileImage(tileReq);
                }
            } catch (IOException unused2) {
                int i3 = i;
                i = i3 - 1;
                if (i3 > 0) {
                    pruneStaleRequests();
                    Vector items = AppState.getVector(ChatKeys.VEC_CHATROOM_LIST);
                    synchronized (items) {
                        if (items.removeElement(tileReq)) {
                            ResourceManager.enqueueTileRequest(tileReq);
                        }
                    }
                } else {
                    AppState.setInt(MapKeys.FLAG_TILES_READY, 1);
                }
            } catch (Throwable unused3) {
                ResourceManager.removeTileRequest(tileReq);
            }
            if (tileImage == null) {
                if (i2 == 3) {
                    Vector pendingReqs = AppState.getVector(RuntimeKeys.OBJ_SEARCH_PARAMS_1);
                    while (pendingReqs.size() >= AppState.getInt(RuntimeKeys.INT_MAX_PENDING_REQUESTS)) {
                        pendingReqs.removeElementAt(0);
                    }
                    pendingReqs.addElement(tileReq);
                    XmppContactGroup.flagSyncRequired();
                } else {
                    cacheTileImage(tileReq, AppState.getImage(MapKeys.OBJ_MENU_LABELS));
                }
                throw new RuntimeException();
            }
            i = 4;
            cacheTileImage(tileReq, tileImage);
            ResourceManager.removeTileRequest(tileReq);
            pruneStaleRequests();
        }
    }

    /* renamed from: a */
    private static final void cacheTileImage(ResourceManager tile, Image image) {
        try {
            getTileCache().put(tile, image, 1);
            MapRenderer.needsRedraw = true;
        } catch (Throwable unused) {
        }
    }

    /* renamed from: d */
    public static final void clearSatelliteTiles() {
        Enumeration keys = getTileCache().keys();
        while (keys.hasMoreElements()) {
            ResourceManager tile = (ResourceManager) keys.nextElement();
            if (tile.tileType == 3) {
                getTileCache().remove(tile);
            }
        }
    }

    /* renamed from: l */
    private static final LruCache getTileCache() {
        return (LruCache) AppState.pool[MapKeys.OBJ_TILE_CACHE];
    }

    /* renamed from: m */
    private static final Vector createRegionVector() {
        Vector result = ObjectPool.newVector();
        result.addElement(AppState.getString(StringResKeys.STR_CITY_LIST));
        return result;
    }

    /* renamed from: a */
    private static final void addXmlChildTexts(Vector vector, Object obj) {
        Vector vector2 = ((XmlElement) obj).children;
        for (int i = 0; i < Utils.vectorSize(vector2); i++) {
            vector.addElement(getXmlText((XmlElement) vector2.elementAt(i)));
        }
    }

    /* renamed from: b */
    private static final String getXmlText(XmlElement element) {
        String attrText = element.getLongKeyAttr(110);
        return attrText != null ? attrText : fromBuffer(element.textContent);
    }

    /* renamed from: e */
    public static final void showRegionSelector() {
        resetRegForm();
        AppState.pool[RegistrationKeys.SLOT_REG_FIELD_2] = new XmlParser(new ByteBuffer(ObjectPool.unpackChars(25135), 41000)).parse().children;
        StringBuffer sb = ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_SEARCH_TITLE));
        Vector items = AppState.getVector(RegistrationKeys.SLOT_REG_FIELD_2);
        for (int i = 0; i < Utils.vectorSize(items); i++) {
            sb.append((char) 0).append(getXmlText((XmlElement) items.elementAt(i)));
        }
        AppState.setFromBuffer(RegistrationKeys.SLOT_REG_FIELD_1, sb);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.REGION_CHOICE));
    }

    /* renamed from: f */
    public static final void resetRegForm() {
        AppState.clearRange(ContactKeys.SLOT_CONTACT_JID, RegistrationKeys.SLOT_REG_FIELD_2);
        AppState.setInt(RegistrationKeys.INT_REGION_CODE, 0);
        AppState.setInt(RegistrationKeys.INT_COUNTRY_CODE, 0);
        AppState.setInt(RegistrationKeys.INT_SEARCH_PARAM_1, -1);
        AppState.setInt(RegistrationKeys.INT_SEARCH_PARAM_2, -1);
        AppState.setInt(RegistrationKeys.INT_SEARCH_PARAM_3, 0);
        AppState.setInt(RegistrationKeys.INT_SEARCH_COUNTRY, 0);
        AppState.setInt(RegistrationKeys.INT_SEARCH_REGION, 0);
        AppState.setInt(RegistrationKeys.INT_SEARCH_CITY, 0);
        AppState.setInt(RegistrationKeys.INT_SEARCH_AGE, 0);
        AppState.setInt(RegistrationKeys.INT_SEARCH_GENDER, 0);
        AppState.setInt(RegistrationKeys.FLAG_SEARCH_ONLINE_ONLY, 0);
    }

    /* renamed from: a */
    public static final void updateRegDropdowns(ListView screen, Object obj) {
        MenuItem menuItem = (MenuItem) obj;
        int selectedIdx = ((Integer) ((Object[]) menuItem.data)[0]).intValue();
        String str = menuItem.title;
        String countryLabel = AppState.getString(StringResKeys.STR_LABEL_COUNTRY);
        String regionLabel = AppState.getString(StringResKeys.STR_LABEL_REGION);
        String cityLabel = AppState.getString(StringResKeys.STR_LABEL_CITY);
        String monthLabel = AppState.getString(StringResKeys.STR_LABEL_MONTH);
        String ageLabel = AppState.getString(StringResKeys.STR_LABEL_AGE_RANGE);
        String genderLabel = AppState.getString(StringResKeys.STR_LABEL_GENDER);
        MenuItem regionItem = null;
        MenuItem cityItem = null;
        MenuItem monthDropdown = null;
        MenuItem ageDropdown = null;
        MenuItem genderDropdown = null;
        int countryIdx = 0;
        Vector vector = screen.menuItems;
        int size = Utils.vectorSize(vector);
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            MenuItem dropdownItem = (MenuItem) vector.elementAt(size);
            if (dropdownItem.id == 9) {
                String str2 = dropdownItem.title;
                if (str2.startsWith(monthLabel)) {
                    monthDropdown = dropdownItem;
                } else if (str2.startsWith(countryLabel)) {
                    countryIdx = ((Integer) ((Object[]) dropdownItem.data)[0]).intValue();
                } else if (str2.startsWith(regionLabel)) {
                    regionItem = dropdownItem;
                } else if (str2.startsWith(cityLabel)) {
                    cityItem = dropdownItem;
                } else if (str2.startsWith(ageLabel)) {
                    ageDropdown = dropdownItem;
                } else if (str2.startsWith(genderLabel)) {
                    genderDropdown = dropdownItem;
                }
            }
        }
        if (equals(str, countryLabel)) {
            MenuItem regionDropdown = regionItem;
            Vector regions = createRegionVector();
            if (selectedIdx > 0) {
                addXmlChildTexts(regions, AppState.getVector(RegistrationKeys.SLOT_REG_FIELD_2).elementAt(selectedIdx - 1));
            }
            regionDropdown.setChoices(regions, 0, regionLabel);
            cityItem.setChoices(Utils.splitByNull(AppState.getString(StringResKeys.STR_CITY_LIST)), 0, cityLabel);
        } else if (equals(str, regionLabel)) {
            MenuItem cityDropdown = cityItem;
            int i = countryIdx;
            Vector regions2 = createRegionVector();
            if (selectedIdx > 0) {
                addXmlChildTexts(regions2, ((XmlElement) AppState.getVector(RegistrationKeys.SLOT_REG_FIELD_2).elementAt(i - 1)).children.elementAt(selectedIdx - 1));
            }
            cityDropdown.setChoices(regions2, 0, cityLabel);
        } else if (equals(str, monthLabel)) {
            ageDropdown.setChoices(Utils.splitByNull(AppState.getString(StringResKeys.STR_AGE_RANGES)), 0, ageLabel);
            genderDropdown.setChoices(Utils.splitByNull(AppState.getString(StringResKeys.STR_GENDER_LIST)), 0, genderLabel);
        } else if (equals(str, ageLabel) || equals(str, genderLabel)) {
            monthDropdown.setChoices(Utils.splitByNull(AppState.getString(StringResKeys.STR_MONTH_NAMES)), 0, monthLabel);
        }
        screen.rebuildItems();
    }

    /* renamed from: g */
    public static final String[] buildRegData() {
        String[] strArr = new String[16];
        String inputStr = Utils.defaultStr(AppState.getString(ContactKeys.SLOT_CONTACT_JID));
        if (!isEmpty(inputStr)) {
            String result = intern(inputStr.toLowerCase());
            int idx = result.indexOf(64);
            if (idx >= 0) {
                strArr[0] = prefix(result, idx);
                strArr[1] = suffix(result, idx + 1);
            } else {
                strArr[0] = result;
                strArr[1] = suffix(Utils.splitAndGet(694, AppState.getInt(RegistrationKeys.INT_REGION_CODE)), 1);
            }
            return strArr;
        }
        strArr[2] = Utils.defaultStr(AppState.getString(ContactKeys.SLOT_DISPLAY_NAME));
        strArr[3] = Utils.defaultStr(AppState.getString(RegistrationKeys.SLOT_FIRST_NAME));
        strArr[4] = Utils.defaultStr(AppState.getString(RegistrationKeys.SLOT_LAST_NAME));
        strArr[5] = intToStringPositive(1481);
        strArr[7] = intToStringPositive(1482);
        strArr[8] = intToStringPositive(1483);
        int iM586d = AppState.getInt(RegistrationKeys.INT_SEARCH_COUNTRY);
        if (iM586d > 0) {
            XmlElement element = (XmlElement) AppState.getVector(RegistrationKeys.SLOT_REG_FIELD_2).elementAt(iM586d - 1);
            strArr[15] = element.getLongKeyAttr(105);
            int iM586d2 = AppState.getInt(RegistrationKeys.INT_SEARCH_REGION);
            if (iM586d2 > 0) {
                XmlElement childElem = (XmlElement) element.children.elementAt(iM586d2 - 1);
                strArr[11] = childElem.getLongKeyAttr(105);
                strArr[15] = null;
                int iM586d3 = AppState.getInt(RegistrationKeys.INT_SEARCH_CITY);
                if (iM586d3 > 0) {
                    strArr[11] = ((XmlElement) childElem.children.elementAt(iM586d3 - 1)).getLongKeyAttr(105);
                }
            }
        }
        strArr[12] = intToStringPositive(1484);
        strArr[13] = intToStringPositive(1488);
        strArr[14] = intToStringPositive(1489);
        if (AppState.getBool(RegistrationKeys.FLAG_SEARCH_ONLINE_ONLY)) {
            strArr[9] = intern(Integer.toString(1));
        }
        return strArr;
    }

    /* renamed from: b */
    private static final String intToStringPositive(int i) {
        int iM586d = AppState.getInt(i);
        if (iM586d > 0) {
            return intern(Integer.toString(iM586d));
        }
        return null;
    }

    /* renamed from: h */
    public static final void initPlatform() {
        AppState.setObject(StringResKeys.STR_APP_NAME, (Object) AppState.getAppProperty(StringResKeys.STR_APP_NAME));
        while (Utils.parseInt((Object) Utils.defaultStr(AppState.getString(SessionKeys.SESSION_RANDOM_ID))) <= 106) {
            try {
                throw new Throwable();
            } catch (Throwable unused) {
                AppState.setObject(SessionKeys.SESSION_RANDOM_ID, (Object) intern(Integer.toString(Utils.nextRandom())));
            }
        }
        setOrGenerateGuid(validateGuid(AppState.getAppProperty(SessionKeys.SLOT_SESSION_HASH)));
        AppState.setObject(SessionKeys.SLOT_SESSION_TOKEN, (Object) AppState.getAppProperty(SessionKeys.SLOT_SESSION_TOKEN));
        AppState.setObject(SessionKeys.SLOT_SESSION_HASH, (Object) new ByteBuffer().writeUInt(1029990694).writeRawString(Utils.defaultStr(AppState.getString(SessionKeys.SESSION_RANDOM_ID))).writeLongBytes(263912257062L).writeRawString(formatVersion()).getStringAndClear());
        AppState.setString(SessionKeys.SLOT_ACCOUNT_LOGIN, getSystemProp(963));
        AppState.setString(SessionKeys.SLOT_ACCOUNT_PASSWORD, getSystemProp(964));
        AppState.setString(SessionKeys.SLOT_ACCOUNT_SERVER, getSystemProp(1378));
        AppState.setString(SessionKeys.SLOT_ACCOUNT_TYPE_STR, getSystemProp(1380));
        AppState.setString(SessionKeys.SLOT_ACCOUNT_DISPLAY_NAME, getSystemProp(1379));
        int i = 967;
        while (true) {
            i--;
            if (i < 965) {
                break;
            }
            try {
                String inputStr = Utils.defaultStr(AppState.getString(i));
                AppState.clearIndex(i);
                Class.forName(inputStr);
                AppState.setInt(UIKeys.FLAG_KNOWN_PLATFORM, 1);
                break;
            } catch (Throwable unused2) {
            }
        }
        if (AppState.getString(StringResKeys.STR_APP_NAME).charAt(0) == '3' && AppState.getString(StringResKeys.STR_APP_NAME).charAt(2) == '9') {
            if (AppState.getString(SessionKeys.SESSION_DEVICE_INFO) == null) {
                AppState.setFromPool(SessionKeys.SESSION_DEVICE_INFO, StringResKeys.STR_DEVICE_FEATURES);
            }
            AppState.clearIndex(SessionKeys.SESSION_PLATFORM_INFO);
            String result = intern(concat(AppState.getString(SessionKeys.SLOT_ACCOUNT_LOGIN), AppState.getString(SessionKeys.SLOT_ACCOUNT_PASSWORD)).toLowerCase());
            isKnownDevice2 = indexOfPackedLong(result, 7163382462464028531L) >= 0 || indexOfPacked(result, 842019699) == 0 || indexOfPacked(result, 842019703) == 0;
            isKnownDevice1 = indexOfPackedLong(result, 418380476270L) >= 0;
            AppState.setBool(UIKeys.FLAG_WIFI_CONNECTION, indexOfPacked(result, 761620851) == 0 || indexOfPacked(result, 1903060322) == 0);
            AppState.setBool(UIKeys.FLAG_KNOWN_DEVICE, isKnownDevice1 || isKnownDevice2);
            AppState.setBool(UIKeys.FLAG_ADVANCED_FEATURES, AppState.getBool(UIKeys.FLAG_KNOWN_PLATFORM) || indexOfPackedLong(result, 29113373327974771L) >= 0 || indexOfPacked(result, 6514035) == 0 || indexOfPacked(result, 6841203) == 0 || indexOfPacked(result, 6842227) == 0 || indexOfPacked(result, 29799) == 0);
            byte major = parseVersionByte(0);
            byte minor = parseVersionByte(1);
            byte patch = parseVersionByte(2);
            byte[] bytes = AppState.getBytes(StringResKeys.RES_SESSION_BYTES);
            bytes[13] = major;
            bytes[14] = minor;
            bytes[15] = patch;
            if (AppState.getLong(SessionKeys.TIMESTAMP_FIRST_RUN) == 0) {
                AppState.setLong(SessionKeys.TIMESTAMP_FIRST_RUN, System.currentTimeMillis());
                return;
            }
            return;
        }
        while (true) {
            Object obj = AppState.pool[StringResKeys.STR_APP_NAME];
            AppState.pool[StringResKeys.STR_APP_NAME] = new Object[]{obj, obj, obj};
        }
    }

    /* renamed from: c */
    private static final byte parseVersionByte(int i) {
        try {
            return (byte) Utils.parseInt(Utils.split(AppState.getString(StringResKeys.STR_APP_NAME), '.').elementAt(i));
        } catch (Throwable unused) {
            return (byte) 0;
        }
    }

    /* renamed from: n */
    private static final String formatVersion() {
        String countryLabel = AppState.getString(StringResKeys.STR_APP_NAME);
        String[] strArr = new String[3];
        String str = AppState.emptyStr;
        strArr[0] = str;
        strArr[1] = str;
        strArr[2] = str;
        int i = 0;
        for (int i2 = 0; i2 < countryLabel.length(); i2++) {
            char ch = countryLabel.charAt(i2);
            if (ch == '.') {
                i++;
            } else {
                int i3 = i;
                strArr[i3] = new StringBuffer().append(strArr[i3]).append(ch).toString();
            }
        }
        while (strArr[0].length() < 2) {
            strArr[0] = new StringBuffer().append('0').append(strArr[0]).toString();
        }
        while (strArr[1].length() < 2) {
            strArr[1] = new StringBuffer().append('0').append(strArr[1]).toString();
        }
        while (strArr[2].length() < 4) {
            strArr[2] = new StringBuffer().append('0').append(strArr[2]).toString();
        }
        return new ByteBuffer().writeRawString(strArr[0]).writeRawString(strArr[1]).writeRawString(strArr[2]).getStringAndClear();
    }

    /* renamed from: a */
    public static final String getSystemProp(int i) {
        return intern(System.getProperty(AppState.getString(i)));
    }

    /* JADX DEBUG: Move duplicate insns, count: 2 to block B:19:0x0071 */
    /* renamed from: d */
    private static final void setOrGenerateGuid(String str) {
        int i;
        int i2;
        if (str != null) {
            AppState.setObject(MapKeys.SLOT_MAP_TILE_DATA, (Object) str);
        }
        if (null == AppState.getString(SessionKeys.SESSION_KEY)) {
            if (null != str) {
                AppState.setObject(SessionKeys.SESSION_KEY, (Object) str);
                return;
            }
            StringBuffer sb = ObjectPool.newStringBuffer();
            int i3 = 0;
            while (i3 < 2) {
                long seed = i3 == 0 ? System.currentTimeMillis() : (Utils.nextRandom() << 32) | Utils.parseInt((Object) Utils.defaultStr(AppState.getString(SessionKeys.SESSION_RANDOM_ID)));
                for (int i4 = 0; i4 < 64; i4 += 4) {
                    int i5 = ((int) (seed >>> (60 - i4))) & 15;
                    if (i5 < 10) {
                        i = i5;
                        i2 = 48;
                    } else {
                        i = i5;
                        i2 = 87;
                    }
                    sb.append((char) (i + i2));
                }
                i3++;
            }
            AppState.setFromBuffer(SessionKeys.SESSION_KEY, sb);
        }
    }

    /* renamed from: e */
    private static final String validateGuid(String str) {
        if (str == null || str.length() != 32) {
            return null;
        }
        int i = 32;
        while (true) {
            i--;
            if (i < 0) {
                return str;
            }
            char ch = str.charAt(i);
            if (ch < '0' || ch > '9') {
                if (ch < 'a' || ch > 'f') {
                    return null;
                }
            }
        }
    }

    /* renamed from: i */
    public static GeoRegion getGeoRegion() {
        return (GeoRegion) AppState.pool[MapKeys.OBJ_GEO_REGION_2];
    }

    /* renamed from: j */
    public static final void initGeoRegions() {
        AppState.pool[MapKeys.VEC_MAP_POINTS] = ObjectPool.newVector();
        AppState.pool[MapKeys.OBJ_GEO_REGION] = new GeoRegion(AppState.getString(StringResKeys.STR_REGION_NAME_2), 4115426L, 7539707L, 4267459L, 7412592L);
        try {
            ByteBuffer geoBuffer = Base64.decode(AppState.getString(MapKeys.GEO_SAVED_DATA));
            AppState.getVector(MapKeys.VEC_MAP_POINTS).removeAllElements();
            if (geoBuffer.length > 0) {
                int count = geoBuffer.readIntBE();
                while (true) {
                    count--;
                    if (count < 0) {
                        break;
                    } else {
                        addGeoRegion(new GeoRegion(geoBuffer));
                    }
                }
            }
        } catch (Throwable unused) {
        }
        GeoRegion region = new GeoRegion(AppState.getString(StringResKeys.STR_REGION_NAME_1), 1866877L, 15815124L, 21989606L, 4133096L);
        region.centerLat = 10848141L;
        region.centerLon = 8758455L;
        AppState.pool[MapKeys.OBJ_GEO_REGION_2] = region;
    }

    /* renamed from: a */
    private static void addGeoRegion(GeoRegion region) {
        Vector items = AppState.getVector(MapKeys.VEC_MAP_POINTS);
        if (items.contains(region)) {
            return;
        }
        items.addElement(region);
    }

    /* renamed from: a */
    public static final boolean isInSavedRegion(long j, long j2) {
        Vector items = AppState.getVector(MapKeys.VEC_MAP_POINTS);
        int size = Utils.vectorSize(items);
        while (true) {
            size--;
            if (size < 0) {
                return false;
            }
            GeoRegion region = (GeoRegion) items.elementAt(size);
            if (region.containsPoint(j, j2) && indexOfPoolString(region.name, 995) < 0) {
                return true;
            }
        }
    }

    /* renamed from: a */
    public static final void parseGeoConfig(XmlElement element) {
        Vector vector;
        if (element == null || (vector = element.children) == null) {
            return;
        }
        AppState.getVector(MapKeys.VEC_MAP_POINTS).removeAllElements();
        String configUrl = element.getIntAttribute(PackedStringKeys.ATTR_TIMESTAMP);
        if (configUrl != null) {
            AppState.setObject(MapKeys.URL_GEO_CONFIG, (Object) configUrl);
        }
        for (int i = 0; i < Utils.vectorSize(vector); i++) {
            XmlElement childElem = (XmlElement) vector.elementAt(i);
            String str = childElem.tagName;
            if (matchesEncoded(str, 1936156018)) {
                Vector vector2 = childElem.children;
                for (int i2 = 0; i2 < Utils.vectorSize(vector2); i2++) {
                    XmlElement regionElem = (XmlElement) vector2.elementAt(i2);
                    GeoRegion region = new GeoRegion(regionElem.getLongKeyAttr(1701667182), regionElem.getAttrAsLong(28780), regionElem.getAttrAsLong(28788), regionElem.getAttrAsLong(28786), regionElem.getAttrAsLong(28770));
                    region.description = regionElem.getLongKeyAttr(25705);
                    region.centerLat = regionElem.getAttrAsLong(1852796003);
                    region.centerLon = regionElem.getAttrAsLong(1952541795);
                    region.precision = regionElem.getAttrAsInt(2054709613);
                    addGeoRegion(region);
                }
            } else if (matchesKey(PackedStringKeys.TAG_POINTS, str)) {
                ServiceRegistry.parseServiceConfig(1, childElem, true);
            }
        }
        try {
            AppState.resetToEmpty(MapKeys.GEO_SAVED_DATA);
            ByteBuffer buffer = new ByteBuffer();
            Vector items = AppState.getVector(MapKeys.VEC_MAP_POINTS);
            int size = items.size();
            buffer.writeIntBE(size);
            for (int i3 = 0; i3 < size; i3++) {
                GeoRegion region2 = (GeoRegion) items.elementAt(i3);
                buffer.writeStringUTF16(region2.name).writeLong(region2.minLat).writeLong(region2.maxLon).writeLong(region2.maxLat).writeLong(region2.minLon).writeStringUTF16(region2.description).writeLong(region2.centerLat).writeLong(region2.centerLon).writeIntLE(region2.precision);
            }
            AppState.setObject(MapKeys.GEO_SAVED_DATA, (Object) buffer.toBase64());
        } catch (Throwable unused) {
            AppState.resetToEmpty(MapKeys.URL_GEO_CONFIG);
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

    /* renamed from: d */
    public static final String transliterate(String str) {
        boolean zIsUpperCase = false;
        String str2 = null;
        String str3;
        Vector vectorM512e = Utils.splitByNull(AppState.getString(StringResKeys.STR_RES_MEGA_URL_4));
        Vector vectorM512e2 = Utils.splitByNull(AppState.getString(StringResKeys.STR_SOUND_LIST));
        Hashtable hashtable = new Hashtable();
        int size = vectorM512e.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            hashtable.put(vectorM512e.elementAt(size), vectorM512e2.elementAt(size));
        }
        String strM584b = AppState.getString(StringResKeys.STR_SOUND_TYPE_1);
        String strM584b2 = AppState.getString(StringResKeys.STR_SOUND_TYPE_2);
        Hashtable hashtable2 = new Hashtable();
        StringBuffer stringBufferM1217h = ObjectPool.newStringBuffer();
        int length = strM584b.length();
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            hashtable2.put(extractBuffer(stringBufferM1217h.append(strM584b.charAt(length))), extractBuffer(stringBufferM1217h.append(strM584b2.charAt(length))));
        }
        int length2 = str.length();
        int i = 0;
        while (i < length2) {
            String strM9b = null;
            int i2 = 3;
            while (true) {
                if (i2 < 1) {
                    break;
                }
                try {
                    String strM12a = substring(str, i, i + i2);
                    zIsUpperCase = Character.isUpperCase(strM12a.charAt(0));
                    str2 = (String) hashtable.get(intern(strM12a.toLowerCase()));
                    strM9b = str2;
                } catch (Throwable unused) {
                }
                if (str2 != null) {
                    if (zIsUpperCase && (str3 = (String) hashtable2.get(prefix(strM9b, 1))) != null) {
                        strM9b = strM9b.length() == 1 ? str3 : concat(str3, suffix(strM9b, 1));
                    }
                    i += i2 - 1;
                    stringBufferM1217h.append(strM9b);
                } else {
                    i2--;
                }
            }
            if (strM9b == null) {
                stringBufferM1217h.append(str.charAt(i));
            }
            i++;
        }
        ObjectPool.releaseVector(vectorM512e);
        ObjectPool.releaseVector(vectorM512e2);
        return ObjectPool.toStringAndRelease(stringBufferM1217h);
    }
}
