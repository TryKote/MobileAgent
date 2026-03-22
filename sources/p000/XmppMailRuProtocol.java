package p000;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

/* renamed from: bd */
/* loaded from: MobileAgent_3.9.jar:bd.class */
public final class XmppMailRuProtocol extends XmppProtocol {

    /* renamed from: f */
    public static ListItem mapContextItem;

    public XmppMailRuProtocol(int i, String str, String str2) {
        super(i, str, str2);
        this.serverAddress = AppState.getString(989287);
        this.serverPort = 5222;
    }

    @Override // p000.XmppProtocol, p000.Account
    /* renamed from: a */
    public final int getType() {
        return 3;
    }

    public XmppMailRuProtocol(ByteBuffer buf) {
        super(buf);
        this.serverAddress = AppState.getString(989287);
        this.serverPort = 5222;
    }

    @Override // p000.XmppProtocol, p000.Account
    /* renamed from: h */
    public final int getIconId() {
        int iconId = super.getIconId();
        int i = iconId & 65535;
        return (i < 381 || i > 384) ? iconId : iconId + 4;
    }

    @Override // p000.XmppProtocol
    /* renamed from: f */
    public final boolean mo83f() {
        return true;
    }

    @Override // p000.XmppProtocol
    /* renamed from: j */
    public final String mo84j() {
        return this.serverAddress;
    }

    @Override // p000.XmppProtocol
    /* renamed from: m */
    public final String mo128m() {
        return this.serverResourceId;
    }

    @Override // p000.XmppProtocol, p000.Account
    /* renamed from: p */
    public final int mo110p() {
        return 595126;
    }

    /* renamed from: y */
    private static final int getAccountType() {
        Account account = AppState.getAccount();
        return null != account ? account.getType() : AppState.getInt(1475);
    }

    /* renamed from: r */
    public static final void showLoginScreen() {
        if (getAccountType() == 1) {
            Account account = AppState.getAccount();
            if (account != null && account.isConnecting()) {
                AppController.showMessageById(300);
                return;
            }
            clearLoginFields();
            if (account != null) {
                AppState.setObject(1292, (Object) account.login);
                AppState.setObject(1293, (Object) account.password);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(2803));
            return;
        }
        if (getAccountType() == 2) {
            XmppProtocol xmppAccount = (XmppProtocol) AppState.getAccount();
            if (xmppAccount != null && xmppAccount.isConnecting()) {
                AppController.showMessageById(300);
                return;
            }
            clearLoginFields();
            AppState.setInt(1474, 0);
            if (xmppAccount != null) {
                String loginStr = xmppAccount.login;
                Vector parts = Utils.splitNonEmpty(AppState.getString(696), (char) 0);
                int count = Utils.vectorSize(parts);
                while (true) {
                    count--;
                    if (count < 1) {
                        break;
                    }
                    int idx = loginStr.indexOf((String) parts.elementAt(count));
                    if (idx >= 0) {
                        loginStr = StringUtils.prefix(loginStr, idx);
                        break;
                    }
                }
                AppState.setInt(1474, count);
                AppState.setObject(1292, (Object) loginStr);
                AppState.setObject(1293, (Object) xmppAccount.password);
                AppState.setObject(1297, (Object) xmppAccount.displayName);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(3443));
            return;
        }
        if (getAccountType() == 3) {
            XmppMailRuProtocol mailRuAccount = (XmppMailRuProtocol) AppState.getAccount();
            if (mailRuAccount != null && mailRuAccount.isConnecting()) {
                AppController.showMessageById(300);
                return;
            }
            clearLoginFields();
            if (mailRuAccount != null) {
                AppState.setObject(1292, (Object) mailRuAccount.login);
                AppState.setObject(1293, (Object) mailRuAccount.password);
                AppState.setObject(1297, (Object) mailRuAccount.displayName);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(3463));
            return;
        }
        clearLoginFields();
        AppState.setInt(1474, 0);
        Account account2 = AppState.getAccount();
        if (null != account2) {
            AppState.setObject(1293, (Object) account2.password);
            String str = account2.login;
            Vector domains2 = Utils.splitNonEmpty(AppState.getString(694), (char) 0);
            int size = domains2.size();
            int i = 0;
            while (true) {
                if (i > size) {
                    break;
                }
                if (i == size) {
                    AppState.setObject(1292, (Object) str);
                    break;
                }
                int idx2 = str.indexOf((String) domains2.elementAt(i));
                if (idx2 >= 0) {
                    AppState.setInt(1474, i);
                    AppState.setObject(1292, (Object) StringUtils.prefix(str, idx2));
                    break;
                }
                i++;
            }
        }
        ScreenManager.showScreen(ScreenManager.createScreen(2777));
    }

    /* renamed from: s */
    public static final int performLogin() {
        NetworkUtils.processScreenForm();
        if (getAccountType() == 1) {
            Account account = AppState.getAccount();
            String login = getLoginLowerCase();
            int errorCode = AppController.validateCredentials(1, account, login, Utils.defaultStr(AppState.getString(1293)));
            if (0 != errorCode) {
                return AppController.showError(errorCode);
            }
            AppController.setCurrentAccount(AppController.createAccount(1, login));
            return 0;
        }
        if (getAccountType() == 2) {
            return IOUtils.loginXmpp(2);
        }
        if (getAccountType() == 3) {
            AppState.setInt(1474, 0);
            return IOUtils.loginXmpp(3);
        }
        String password = Utils.defaultStr(AppState.getString(1293));
        String login2 = getLoginLowerCase();
        String fullLogin = login2;
        if (StringUtils.isEmpty(login2)) {
            return AppController.showError(301);
        }
        if (!containsDomainSuffix(fullLogin, 694) && !containsDomainSuffix(fullLogin, 695)) {
            fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(694, AppState.getInt(1474)));
        }
        if (!isValidUsername(fullLogin)) {
            return AppController.showError(559);
        }
        int errorCode2 = AppController.validateCredentials(0, AppState.getAccount(), fullLogin, password);
        if (0 != errorCode2) {
            return AppController.showError(errorCode2);
        }
        AppController.setCurrentAccount(AppController.createAccount(0, fullLogin));
        return 0;
    }

    /* renamed from: t */
    public static final void clearLoginFields() {
        AppState.clearRange(1292, 1293);
        AppState.clearIndex(1297);
    }

    /* renamed from: f */
    public static final boolean isMailRuDomain(String str) {
        return containsDomainSuffix(str, 694);
    }

    /* renamed from: c */
    private static final boolean containsDomainSuffix(String str, int i) {
        Vector parts = Utils.splitNonEmpty(AppState.getString(i), (char) 0);
        int size = parts.size();
        do {
            size--;
            if (size < 0) {
                NetworkUtils.releaseVector(parts);
                return false;
            }
        } while (str.indexOf((String) parts.elementAt(size)) < 0);
        return true;
    }

    /* renamed from: u */
    public static final String getLoginLowerCase() {
        return StringUtils.intern(Utils.defaultStr(AppState.getString(1292)).toLowerCase());
    }

    /* renamed from: g */
    public static final boolean isValidUsername(String str) {
        int length = str.length();
        while (true) {
            length--;
            if (length < 0) {
                return true;
            }
            char ch = str.charAt(length);
            if (ch < 'A' || ch > 'Z') {
                if (ch < 'a' || ch > 'z') {
                    if (ch < '0' || ch > '9') {
                        if (ch != '.' && ch != '_' && ch != '-' && ch != '@') {
                            return false;
                        }
                    }
                }
            }
        }
    }

    /* renamed from: v */
    public static final void calculateCacheSize() {
        int size = 0;
        String[] storeNames = StringUtils.listRecordStores();
        if (storeNames != null) {
            int length = storeNames.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                String str = storeNames[length];
                if (str.startsWith(AppState.getString(332005))) {
                    RecordStore recordStore = null;
                    try {
                        RecordStore store = IOUtils.openRecordStore(str, false);
                        recordStore = store;
                        size += store.getSize();
                        IOUtils.closeRecordStore(recordStore);
                    } catch (Throwable unused) {
                        IOUtils.closeRecordStore(recordStore);
                    }
                }
            }
        }
        AppState.setInt(1552, size);
    }

    /* renamed from: a */
    private static void saveTileToCache(ResourceManager resource, byte[] bArr, int i, int i2) {
        if (resource == null || bArr == null) {
            return;
        }
        String cacheKey = buildTileCacheKey(resource);
        ByteBuffer tileData = new ByteBuffer().writeStringLatin1(resource.tileUrl).writeLong(System.currentTimeMillis()).writeBytesAt(bArr, 0, i2);
        int i3 = 4;
        while (true) {
            i3--;
            if (i3 <= 0) {
                return;
            }
            try {
                try {
                    if (tileData.length + AppState.getInt(1552) >= 204800) {
                        throw new Throwable();
                    }
                    RecordStore store = IOUtils.openRecordStore(cacheKey, true);
                    byte[] bArr2 = tileData.data;
                    int i4 = tileData.offset;
                    int i5 = tileData.length;
                    store.addRecord(bArr2, i4, i5);
                    AppState.setInt(1552, AppState.getInt(1552) + i5);
                    tileData.clear();
                    IOUtils.closeRecordStore(store);
                    return;
                } catch (Throwable unused) {
                    evictOldestCache();
                    IOUtils.closeRecordStore((RecordStore) null);
                }
            } catch (Throwable th) {
                IOUtils.closeRecordStore((RecordStore) null);
                throw th;
            }
        }
    }

    /* renamed from: a */
    public static final Image loadTileFromCache(ResourceManager resource) {
        String cacheKey = buildTileCacheKey(resource);
        RecordStore recordStore = null;
        try {
            String str = resource.tileUrl;
            RecordStore store = IOUtils.openRecordStore(cacheKey, false);
            recordStore = store;
            int numRecords = store.getNumRecords();
            for (int i = 1; i <= numRecords; i++) {
                ByteBuffer recordBuf = new ByteBuffer().setData(recordStore.getRecord(i));
                if (recordBuf.readWideStr().equals(str)) {
                    recordBuf.readLong();
                    Image image = recordBuf.toImage();
                    IOUtils.closeRecordStore(recordStore);
                    return image;
                }
                recordBuf.clear();
            }
            IOUtils.closeRecordStore(recordStore);
            return null;
        } catch (RuntimeException th) {
            IOUtils.closeRecordStore(recordStore);
            throw th;
        } catch (Throwable th) {
            IOUtils.closeRecordStore(recordStore);
            throw new RuntimeException(th);
        }
    }

    /* renamed from: z */
    private static final String findOldestCacheStore() {
        String str = null;
        long j = 0;
        String[] storeNames = StringUtils.listRecordStores();
        if (storeNames != null) {
            String cachePrefix = AppState.getString(332005);
            int length = storeNames.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                String str2 = storeNames[length];
                if (str2.startsWith(cachePrefix)) {
                    RecordStore store = null;
                    try {
                        store = IOUtils.openRecordStore(str2, false);
                        long j2 = j;
                        long lastModified = store.getLastModified();
                        if (j2 > j2 || j == 0) {
                            j = lastModified;
                            str = str2;
                        }
                        IOUtils.closeRecordStore(store);
                    } catch (RuntimeException th) {
                        IOUtils.closeRecordStore(store);
                        throw th;
                    } catch (Throwable th) {
                        IOUtils.closeRecordStore(store);
                        throw new RuntimeException(th);
                    }
                }
            }
        }
        return str;
    }

    /* renamed from: S */
    private static final void evictOldestCache() {
        String oldestStore = findOldestCacheStore();
        if (oldestStore != null) {
            RecordStore recordStore = null;
            try {
                RecordStore store = IOUtils.openRecordStore(oldestStore, false);
                recordStore = store;
                int numRecords = store.getNumRecords();
                for (int i = 1; i <= numRecords; i++) {
                    AppState.setInt(1552, AppState.getInt(1552) - recordStore.getRecordSize(i));
                }
                IOUtils.closeRecordStore(recordStore);
                try {
                    RecordStore.deleteRecordStore(oldestStore);
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                IOUtils.closeRecordStore(recordStore);
                try {
                    RecordStore.deleteRecordStore(oldestStore);
                } catch (Throwable unused3) {
                }
            }
        }
    }

    /* renamed from: c */
    private static final String buildTileCacheKey(ResourceManager resource) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(332005)).append(resource.tileType).append('z').append(resource.zoomLevel).append('x').append((resource.tileX / 4) << 2).append('y').append((resource.tileY / 4) << 2));
    }

    /* renamed from: h */
    public static final ByteBuffer readChunkedRecord(String str) {
        ByteBuffer buf = new ByteBuffer();
        int i = 0;
        while (true) {
            RecordStore recordStore = null;
            try {
                int i2 = i;
                i++;
                RecordStore store = IOUtils.openRecordStore(buildChunkName(str, i2), false);
                recordStore = store;
                byte[] record = store.getRecord(1);
                buf.writeBytes(record);
                NetworkUtils.releaseBytes(record);
                IOUtils.closeRecordStore(recordStore);
            } catch (RuntimeException th) {
                IOUtils.closeRecordStore(recordStore);
                throw th;
            } catch (Throwable th) {
                IOUtils.closeRecordStore(recordStore);
                return buf;
            }
        }
    }

    /* renamed from: a */
    public static final void writeRecord(String str, ByteBuffer buf, boolean z) {
        if (z) {
            writeChunkedRecord(str, buf);
            return;
        }
        int i = buf.length;
        if (i == 0) {
            String[] storeNames = StringUtils.listRecordStores();
            int i2 = 0;
            while (true) {
                int i3 = i2;
                i2++;
                String chunkName = buildChunkName(str, i3);
                if (!recordStoreExists(storeNames, chunkName)) {
                    break;
                } else {
                    try {
                        RecordStore.deleteRecordStore(chunkName);
                    } catch (Throwable unused) {
                    }
                }
            }
        } else {
            RecordStore recordStore = null;
            try {
                byte[] bArr = buf.compact().data;
                RecordStore store = IOUtils.openRecordStore(str, true);
                recordStore = store;
                if (store.getNumRecords() == 0) {
                    recordStore.addRecord(bArr, 0, i);
                } else {
                    recordStore.setRecord(1, bArr, 0, i);
                }
                String[] storeNames2 = StringUtils.listRecordStores();
                int i4 = 0;
                while (true) {
                    i4++;
                    String chunkName2 = buildChunkName(str, i4);
                    if (!recordStoreExists(storeNames2, chunkName2)) {
                        break;
                    } else {
                        try {
                            RecordStore.deleteRecordStore(chunkName2);
                        } catch (Throwable unused2) {
                        }
                    }
                }
                IOUtils.closeRecordStore(recordStore);
            } catch (RuntimeException th) {
                IOUtils.closeRecordStore(recordStore);
                throw th;
            } catch (Throwable th) {
                IOUtils.closeRecordStore(recordStore);
                throw new RuntimeException(th);
            }
        }
        buf.clear();
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:6:0x0033 */
    /* renamed from: a */
    public static final void writeChunkedRecord(String str, ByteBuffer buf) {
        int i = 0;
        int i2 = buf.length;
        if (i2 > 0) {
            byte[] bArr = buf.compact().data;
            int written = 0;
            while (true) {
                int i3 = written;
                if (i3 >= i2) {
                    break;
                }
                int i4 = i;
                i++;
                written = i3 + writeRecordChunk(buildChunkName(str, i4), bArr, i3, i2 - i3);
            }
        }
        String[] storeNames = StringUtils.listRecordStores();
        while (true) {
            int i5 = i;
            i++;
            String chunkName = buildChunkName(str, i5);
            if (!recordStoreExists(storeNames, chunkName)) {
                return;
            } else {
                try {
                    RecordStore.deleteRecordStore(chunkName);
                } catch (Throwable unused) {
                }
            }
        }
    }

    /* renamed from: a */
    private static final int writeRecordChunk(String str, byte[] bArr, int i, int i2) {
        RecordStore recordStore = null;
        try {
            try {
                RecordStore.deleteRecordStore(str);
            } catch (Throwable unused) {
            }
            RecordStore store = IOUtils.openRecordStore(str, true);
            recordStore = store;
            int chunkSize = Utils.min(i2, Utils.max(recordStore.getSizeAvailable() - 128, 2048));
            store.addRecord(bArr, i, chunkSize);
            IOUtils.closeRecordStore(recordStore);
            return chunkSize;
        } catch (RuntimeException th) {
            IOUtils.closeRecordStore(recordStore);
            throw th;
        } catch (Throwable th) {
            IOUtils.closeRecordStore(recordStore);
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    private static final boolean recordStoreExists(String[] strArr, String str) {
        if (strArr == null) {
            return false;
        }
        int length = strArr.length;
        do {
            length--;
            if (length < 0) {
                return false;
            }
        } while (!str.equals(strArr[length]));
        return true;
    }

    /* renamed from: d */
    private static final String buildChunkName(String str, int i) {
        if (i == 0) {
            return str.length() <= 32 ? str : StringUtils.prefix(str, 32);
        }
        StringBuffer sb = NetworkUtils.newStringBuffer().append(i);
        StringBuffer sb2 = NetworkUtils.newStringBuffer().append('s').append(str).append('s');
        while (sb2.length() + sb.length() > 32) {
            sb2.setLength(sb2.length() - 1);
        }
        return NetworkUtils.bufToStringCached(sb2.append((Object) sb));
    }

    /* renamed from: a */
    public static final ByteBuffer sendContactListRequest(MmpProtocol protocol, int i) {
        return protocol.queueCommand(new Object[]{AppController.createMmpCommand(protocol, 4873, new ByteBuffer().writeIntLE(0).writeShortBE(i).writeShortBE(4).writeShortBE(5).writeShortBE(202).writeShortBE(1).writeByte(protocol.getPendingVersion())), ResourceManager.integerOf(20)});
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:30:0x00ce */
    /* renamed from: w */
    public static final void showMapContextMenu() {
        ListItem item;
        int i = 3072;
        if (AppController.getOnlineMrimAccounts().size() > 0) {
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
        if (!AppState.getBool(277)) {
            i &= -1025;
        }
        int i2 = 1424;
        int i3 = 1;
        while (true) {
            int i4 = i3;
            if (i4 >= 16384) {
                ScreenManager.showScreen(ScreenManager.createScreen(1753));
                return;
            }
            int i5 = i2;
            i2++;
            AppState.setInt(i5, i & i4);
            i3 = i4 << 1;
        }
    }

    /* renamed from: h */
    public static final int handleMapAction(int i) {
        long lon;
        long lat;
        ListItem item = mapContextItem;
        int itemType = item == null ? 0 : item.getHeight();
        switch (i) {
            case 0:
                AppState.setCurrentEntity(item);
                ScreenBuilder.onScreenClosed();
                return 63;
            case 1:
                if (itemType == 8) {
                    AppState.setInt(4895, 0);
                    AppController.openUserProfile((MrimAccount) null, ((UserSearchResult) item).userId);
                } else {
                    AppState.setCurrentEntity(mapContextItem);
                }
                ScreenBuilder.onScreenClosed();
                return 102;
            case 2:
                if (itemType == 3) {
                    AppState.setCurrentEntity(mapContextItem);
                    ScreenBuilder.onScreenClosed();
                    return 85;
                }
                AppState.setCurrentEntity((Object) null);
                Vector onlineAccounts = AppController.getOnlineMrimAccounts();
                if (onlineAccounts == null || onlineAccounts.size() <= 0) {
                    return AppController.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) item).userId, 1));
                ScreenBuilder.onScreenClosed();
                return 85;
            case 3:
                Vector onlineAccounts2 = AppController.getOnlineMrimAccounts();
                if (onlineAccounts2 == null || onlineAccounts2.size() <= 0) {
                    return AppController.showError(422);
                }
                ((MrimAccount) onlineAccounts2.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) item).userId, 2));
                ScreenBuilder.onScreenClosed();
                return 6;
            case 4:
                ScreenBuilder.onScreenClosed();
                AppState.pool[1255] = (Conversation) item;
                return 170;
            case 5:
                ResourceManager.dialPhoneUrl(VCard.formatPhoneContactUrl((PhoneContact) item, 0), (PhoneContact) item, 0);
                return 12;
            case 6:
                AppState.setAccount(item);
                ScreenBuilder.onScreenClosed();
                return 167;
            case 7:
                AppState.setAccount(item);
                ScreenBuilder.onScreenClosed();
                return 151;
            case 8:
            case 9:
            default:
                return 6;
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
                    return 6;
                }
                Conversation.loadContacts();
                return 6;
            case 11:
                if (MapRenderer.hasRouteEndpoints()) {
                    MmpContact.clearRouteProgress();
                }
                if (MmpContact.mapDataCache != null) {
                    MmpContact.routePoints.removeElement((int[]) MmpContact.mapDataCache[1]);
                    MmpContact.nearestPoints.removeElement(MmpContact.mapDataCache);
                }
                AppState.setInt(1575, 0);
                AppState.setBool(1574, AppState.getBool(1573));
                MapRenderer.needsRedraw = true;
                if (!MapRenderer.hasRouteEndpoints()) {
                    return 6;
                }
                Conversation.loadContacts();
                return 6;
            case 12:
                MmpContact.clearLocationData();
                MapRenderer.needsRedraw = true;
                return 6;
            case 13:
                ListItem tooltipItem3 = MapRenderer.tooltipItem;
                if (tooltipItem3 != null && tooltipItem3.isSelected()) {
                    tooltipItem3.select();
                }
                MapRenderer.needsRedraw = true;
                return 6;
            case 14:
                ConnectionThread.setRouteStart();
                if (MmpContact.hasSecondToken()) {
                    return 6;
                }
                AppState.setInt(1442, 1);
                return 158;
            case 15:
                ConnectionThread.setRouteEnd();
                if (MmpContact.hasFirstToken()) {
                    return 6;
                }
                AppState.setInt(1442, 0);
                return 158;
            case 16:
                return 159;
            case 17:
                return 114;
            case 18:
                ScreenBuilder.onScreenClosed();
                Vector onlineAccounts3 = AppController.getOnlineMrimAccounts();
                if (onlineAccounts3.size() > 1) {
                    return 172;
                }
                AppState.setAccount(onlineAccounts3.elementAt(0));
                return 173;
            case 19:
                return 110;
            case 20:
                ConnectionThread.removeRoutePoint((MapPoint) mapContextItem);
                return 6;
            case 21:
                Conversation.incrementZoom();
                return 6;
            case 22:
                Conversation.decrementZoom();
                return 6;
        }
    }

    /* renamed from: a */
    public static final void resolveXmppServer(Object[] objArr) {
        try {
            String str = ((XmppProtocol) objArr[0]).login;
            String srvRecord = dnsLookupSrv(StringUtils.concatKey(1185660, StringUtils.suffix(str, str.indexOf(64) + 1)));
            if (srvRecord == null || srvRecord.indexOf(58) <= 0) {
                XmppProtocol xmppAccount = (XmppProtocol) objArr[0];
                xmppAccount.setAuthParameters(xmppAccount.mo84j(), 5222);
            } else {
                Vector parts = Utils.splitNonEmpty(srvRecord, ':');
                ((XmppProtocol) objArr[0]).setAuthParameters(Utils.getVectorString(parts, 0), Integer.parseInt(Utils.getVectorString(parts, 1)));
                NetworkUtils.releaseVector(parts);
            }
        } catch (Throwable th) {
            ((XmppProtocol) objArr[0]).setException(th);
        }
    }

    /* renamed from: j */
    private static final String dnsLookupSrv(String str) {
        String result;
        DatagramConnection datagramConnection = null;
        try {
            AppController.acquireNetworkLock();
            Vector parts = Utils.splitNonEmpty(str, '.');
            ByteBuffer requestBuf = new ByteBuffer().writeCompressed(792490);
            for (int i = 0; i < Utils.vectorSize(parts); i++) {
                requestBuf.writeByteLenStr(Utils.getVectorString(parts, i));
            }
            NetworkUtils.releaseVector(parts);
            requestBuf.writeCompressed(333750);
            DatagramConnection datagramConnection2 = (DatagramConnection) IOUtils.registerResource((Object) Connector.open(AppState.getString(1841038)));
            datagramConnection = datagramConnection2;
            datagramConnection2.send(datagramConnection2.newDatagram(requestBuf.data, requestBuf.length));
            requestBuf.clear();
            Datagram datagram = datagramConnection.newDatagram(512);
            datagramConnection.receive(datagram);
            ByteBuffer recordBuf = new ByteBuffer().setData(datagram.getData());
            recordBuf.skip(6);
            if (recordBuf.readShortBE() <= 0) {
                result = null;
            } else {
                recordBuf.readInt();
                while (true) {
                    int labelLen = recordBuf.readUByte();
                    int i2 = labelLen;
                    if (labelLen == 0) {
                        break;
                    }
                    while (true) {
                        i2--;
                        if (i2 < 0) {
                            break;
                        }
                        recordBuf.readUByte();
                    }
                }
                recordBuf.skip(20);
                int port = recordBuf.readShortBE();
                ByteBuffer buf = new ByteBuffer();
                int labelLen2 = recordBuf.readUByte();
                while (true) {
                    labelLen2--;
                    if (labelLen2 < 0) {
                        int labelLen3 = recordBuf.readUByte();
                        labelLen2 = labelLen3;
                        if (labelLen3 == 0) {
                            break;
                        }
                        buf.writeByte(46);
                    } else {
                        buf.writeByte(recordBuf.readUByte());
                    }
                }
                result = buf.writeByte(58).writeIntAsString(port).getStringAndClear();
            }
            String str2 = result;
            IOUtils.closeConn((Connection) datagramConnection);
            AppController.releaseNetworkLock();
            return str2;
        } catch (RuntimeException th) {
            IOUtils.closeConn((Connection) datagramConnection);
            AppController.releaseNetworkLock();
            throw th;
        } catch (Throwable th) {
            IOUtils.closeConn((Connection) datagramConnection);
            AppController.releaseNetworkLock();
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    public static final void writeAddressPairs(Vector vector, ByteBuffer buf) {
        int count = Utils.vectorSize(vector);
        buf.writeIntLE(count);
        for (int i = 0; i < count; i++) {
            String[] strArr = (String[]) vector.elementAt(i);
            buf.writeStringUTF16(strArr[0]).writeStringUTF16(strArr[1]);
        }
    }

    /* renamed from: e */
    public static final Vector readAddressPairs(ByteBuffer buf) {
        Vector results = NetworkUtils.newVector();
        int resultCode = buf.readInt();
        while (true) {
            resultCode--;
            if (resultCode < 0) {
                return results;
            }
            results.addElement(new String[]{buf.readUTF8Str((String) null), buf.readUTF8Str((String) null)});
        }
    }

    /* renamed from: a */
    public static final Vector copyAddressList(Vector vector) {
        Vector results = NetworkUtils.newVector();
        for (int i = 0; i < Utils.vectorSize(vector); i++) {
            results.addElement(vector.elementAt(i));
        }
        return results;
    }

    /* renamed from: a */
    public static final Vector mergeAddressLists(Vector vector, Vector vector2) {
        if (vector2 != null) {
            Enumeration elements = vector2.elements();
            while (elements.hasMoreElements()) {
                addUniqueAddress(vector, (String[]) elements.nextElement());
            }
        }
        return vector;
    }

    /* renamed from: b */
    public static final Vector getFirstAddress(Vector vector) {
        Vector results = NetworkUtils.newVector();
        if (Utils.vectorSize(vector) > 0) {
            results.addElement(vector.elementAt(0));
        }
        return results;
    }

    /* renamed from: a */
    public static final Vector addUniqueAddress(Vector vector, String[] strArr) {
        String str = strArr[0];
        if (str.indexOf(64) != -1) {
            boolean z = false;
            int count = Utils.vectorSize(vector);
            while (true) {
                count--;
                if (count < 0) {
                    break;
                }
                if (StringUtils.equals(str, ((String[]) vector.elementAt(count))[0])) {
                    z = true;
                }
            }
            if (!z) {
                vector.addElement(strArr);
            }
        }
        return vector;
    }

    /* renamed from: b */
    public static final Vector parseAddressHeader(String str, String str2) {
        Vector results = NetworkUtils.newVector();
        Vector decodedNames = splitCommaSeparated(Conversation.decodeHtmlSpecial(str2));
        Vector rawAddresses = splitCommaSeparated(str);
        for (int i = 0; i < Utils.vectorSize(rawAddresses); i++) {
            addUniqueAddress(results, AppController.createAddressPair((String) rawAddresses.elementAt(i), (String) decodedNames.elementAt(i)));
        }
        NetworkUtils.releaseVector(decodedNames);
        NetworkUtils.releaseVector(rawAddresses);
        return results;
    }

    /* renamed from: c */
    public static final String[] getFirstRecipient(Vector vector) {
        if (Utils.vectorSize(vector) > 0) {
            return (String[]) vector.elementAt(0);
        }
        return null;
    }

    /* renamed from: k */
    private static final Vector splitCommaSeparated(String str) {
        Vector results = NetworkUtils.newVector();
        StringBuffer sb = NetworkUtils.newStringBuffer();
        int length = str.length();
        boolean z = true;
        int i = 0;
        while (i <= length) {
            char ch = i < length ? str.charAt(i) : ',';
            if (!z) {
                z = true;
            } else if (ch == ',') {
                results.addElement(NetworkUtils.bufToString(sb, false));
                z = false;
            } else {
                sb.append(ch);
            }
            i++;
        }
        NetworkUtils.bufToStringCached(sb);
        return results;
    }

    /* renamed from: i */
    public static final Vector parseRecipientList(String str) {
        Vector results = NetworkUtils.newVector();
        StringBuffer sb = NetworkUtils.newStringBuffer();
        int length = str.length();
        int i = 0;
        while (i <= length) {
            char ch = i == length ? ';' : str.charAt(i);
            char c = ch;
            if (ch != ';' && c != ',' && c != ' ') {
                sb.append(c);
            } else if (sb.length() > 0) {
                String address = NetworkUtils.bufToString(sb, false);
                results.addElement(new String[]{address, address});
            }
            i++;
        }
        NetworkUtils.bufToStringCached(sb);
        return results;
    }

    /* renamed from: b */
    public static final void setMailAction(int i, int i2) {
        AppState.setInt(1515, i);
        AppState.setInt(1516, i2);
    }

    /* renamed from: x */
    public static final int processMailResponse() {
        String bodyText;
        Object[] asyncResult = IOUtils.pollAsyncResult();
        if (asyncResult == null) {
            return handleMailRedirect();
        }
        Object[] responseData = ConnectionThread.getAsyncResult(asyncResult);
        if (responseData == null) {
            return 0;
        }
        int validationResult = IOUtils.validateJsonResponse(responseData);
        if (validationResult != 0) {
            return validationResult;
        }
        String messageId = AppState.getString(1346);
        ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513));
        Message message = chatRoom.getMessage(messageId);
        boolean wasUnread = message.hasFlag(4);
        Object jsonPayload = IOUtils.getJsonPayload();
        Object attachmentsList = JsonParser.getValueByInt(jsonPayload, 722874);
        int size = ((Vector) attachmentsList).size();
        int i = size;
        Object[] objArr = new Object[size];
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            Object attachmentObj = JsonParser.getVectorElement(attachmentsList, i);
            objArr[i] = new String[]{JsonParser.getStringByInt(attachmentObj, 1227), JsonParser.getStringByInt(attachmentObj, 1228), JsonParser.getStringByInt(attachmentObj, 1229), JsonParser.getStringByInt(attachmentObj, 1230), JsonParser.getStringByInt(attachmentObj, 1231), JsonParser.getStringByInt(attachmentObj, 1232)};
        }
        message.attachments = objArr;
        String str = (String) JsonParser.getValueByInt(jsonPayload, 919493);
        if (str == null) {
            bodyText = AppState.emptyStr;
        } else {
            StringBuffer sb = NetworkUtils.newStringBuffer();
            int length = str.length();
            int i2 = 0;
            while (i2 < length) {
                char ch = str.charAt(i2);
                sb.append(ch);
                if (ch == ' ') {
                    while (i2 + 1 < length && str.charAt(i2 + 1) == ' ') {
                        i2++;
                    }
                }
                if (ch == '\n') {
                    while (i2 + 1 < length && str.charAt(i2 + 1) == '\n') {
                        i2++;
                    }
                }
                i2++;
            }
            bodyText = NetworkUtils.bufToStringCached(sb);
        }
        message.body = bodyText;
        if (wasUnread) {
            message.setFlag(4, false);
            chatRoom.decrementUnread();
        }
        return handleMailRedirect();
    }

    /* renamed from: T */
    private static final int handleMailRedirect() {
        int action = AppState.getInt(1515);
        if (action == 54) {
            Message message = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513)).getMessage(AppState.getString(1346));
            Vector toList = message.getToList();
            Vector ccList = message.getCcList();
            getFirstRecipient(toList);
            String subject = message.getSubject();
            String str = message.body;
            String replyPrefix = AppState.getString(198549);
            String fwdPrefix = AppState.getString(198546);
            String string = new StringBuffer().append(AppState.getString(838)).append(Utils.quoteText(str)).toString();
            switch (AppState.getInt(1516)) {
                case 0:
                    ResourceManager.composeEmail(getFirstAddress(toList), new StringBuffer().append(replyPrefix).append(subject).toString(), string);
                    break;
                case 1:
                    ResourceManager.composeEmail(mergeAddressLists(copyAddressList(toList), ccList), new StringBuffer().append(replyPrefix).append(subject).toString(), string);
                    break;
                case 2:
                    ResourceManager.composeEmail(NetworkUtils.newVector(), new StringBuffer().append(fwdPrefix).append(subject).toString(), string);
                    break;
                case 3:
                    ResourceManager.composeEmail(copyAddressList(ccList), subject, str);
                    break;
            }
        }
        return action;
    }

    /* renamed from: U */
    private static final boolean reconnectHttp() {
        try {
            AppController.acquireNetworkLock();
            NetworkUtils.closeConnection(AppState.getObjectArray(1392));
            AppState.pool[1392] = NetworkUtils.openSocket(new ByteBuffer().writeCompressed(593549).writeCompressed(1511542).getStringAndClear(), false);
            return true;
        } catch (Throwable unused) {
            return false;
        } finally {
            AppController.releaseNetworkLock();
        }
    }

    /* JADX DEBUG: Finally have unexpected throw blocks count: 2, expect 1 */
    /* renamed from: b */
    public static final Image fetchTileImage(ResourceManager resource) throws IOException {
        ByteBuffer requestBuf = new ByteBuffer().writeCompressed(2232520).writeRawString(resource.tileUrl).writeCompressed(3870861).writeExtendedInt(2950495).writeEncodedInt(222).writeCompressed(6689002);
        try {
            Object[] socket = AppState.getObjectArray(1392);
            byte[] bArr = requestBuf.data;
            int i = requestBuf.length;
            NetworkUtils.writeSocket(socket, bArr, i);
            AppController.addUploadBytes(i);
        } catch (Throwable unused) {
            if (!reconnectHttp()) {
                throw new IOException();
            }
            Object[] socket2 = AppState.getObjectArray(1392);
            byte[] bArr2 = requestBuf.data;
            int i2 = requestBuf.length;
            NetworkUtils.writeSocket(socket2, bArr2, i2);
            AppController.addUploadBytes(i2);
        } finally {
            requestBuf.clear();
        }
        String headers = readHttpHeaders();
        if (headers == null) {
            NetworkUtils.closeConnection(AppState.getObjectArray(1392));
            throw new IOException();
        }
        AppState.addInt(1548, headers.getBytes().length);
        if (parseHttpStatus(headers) != 200) {
            int contentLen = parseContentLength(headers);
            try {
                if (contentLen > 0) {
                    ((InputStream) AppState.getObjectArray(1392)[1]).skip(contentLen);
                } else {
                    NetworkUtils.closeConnection(AppState.getObjectArray(1392));
                }
                return null;
            } catch (Throwable unused2) {
                return null;
            }
        }
        ByteBuffer bodyBuf = readHttpBody(parseContentLength(headers));
        if (bodyBuf == null) {
            NetworkUtils.closeConnection(AppState.getObjectArray(1392));
            throw new IOException();
        }
        AppState.addInt(1548, bodyBuf.length);
        byte[] bArr3 = bodyBuf.data;
        int i3 = bodyBuf.length;
        if (AppState.getBool(1551)) {
            saveTileToCache(resource, bArr3, 0, i3);
        }
        AppController.addDownloadBytes(bodyBuf.length + 255);
        return bodyBuf.toImage();
    }

    /* renamed from: V */
    private static final String readHttpHeaders() {
        Object[] socket = AppState.getObjectArray(1392);
        ByteBuffer buf = new ByteBuffer();
        int i = 0;
        while (true) {
            try {
                int i2 = ((InputStream) socket[1]).read();
                if (i2 == -1) {
                    return null;
                }
                buf.writeByte(i2);
                if (i2 == 10) {
                    i++;
                    if (i == 34) {
                        return buf.getStringAndClear();
                    }
                } else {
                    i = i2 == 13 ? i + 16 : 0;
                }
            } catch (Throwable unused) {
                return null;
            }
        }
    }

    /* renamed from: i */
    private static final ByteBuffer readHttpBody(int i) {
        if (i <= 0) {
            return null;
        }
        try {
            ByteBuffer buf = new ByteBuffer();
            int bytesRead = 0;
            int i2 = 0;
            byte[] readBuf = NetworkUtils.newBytes(8192);
            int length = readBuf.length;
            Object[] socket = AppState.getObjectArray(1392);
            while (i2 != i && bytesRead != -1) {
                bytesRead = NetworkUtils.readSocket(socket, readBuf, 0, Utils.min(length, i - i2));
                buf.writeBytesAt(readBuf, 0, bytesRead);
                i2 += bytesRead;
            }
            NetworkUtils.releaseBytes(readBuf);
            return buf;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: l */
    private static final int parseHttpStatus(String str) {
        try {
            return Integer.parseInt(StringUtils.substring(str, 9, 12));
        } catch (Throwable unused) {
            return 0;
        }
    }

    /* renamed from: m */
    private static final int parseContentLength(String str) {
        try {
            int headerOffset = AppState.indexOfPool(StringUtils.intern(str.toLowerCase()), 1052310) + 16;
            return Integer.parseInt(StringUtils.substring(str, headerOffset, str.indexOf(13, headerOffset)));
        } catch (Throwable unused) {
            return -1;
        }
    }

    /* renamed from: b */
    public static final void removeQueuedCommand(MmpProtocol protocol, int i) {
        Vector vector = protocol.extras;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            if (((Integer) ((Object[]) vector.elementAt(size))[0]).intValue() == i) {
                vector.removeElementAt(size);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:241:0x0cd4  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void handleMmpResponse(MmpProtocol protocol, ByteBuffer buf, int i, int i2) {
        Object[] objArr;
        boolean z = false;
        boolean z2;
        MmpContactGroup group;
        Vector vector = protocol.extras;
        int size = vector.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                objArr = (Object[]) vector.elementAt(size);
            }
        } while (((Integer) objArr[0]).intValue() != i);
        boolean z3 = true;
        switch (((Integer) objArr[1]).intValue()) {
            case 0:
                int status = buf.readShortBE();
                if (status == 0) {
                    ((MmpContact) objArr[2]).setDisplayName((String) objArr[3]);
                } else {
                    IOUtils.postRenameError(objArr, status);
                }
                z = true;
                z3 = z;
                break;
            case 1:
                int status2 = buf.readShortBE();
                if (status2 == 0) {
                    ((MmpContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                } else {
                    IOUtils.postRenameError(objArr, status2);
                }
                z = true;
                z3 = z;
                break;
            case 2:
                int status3 = buf.readShortBE();
                if (status3 == 0) {
                    protocol.removeGroup((ContactGroup) objArr[2]);
                    protocol.trySendData(ResourceManager.createSyncGroupsCmd(protocol));
                } else {
                    IOUtils.postDeleteError(objArr, status3);
                }
                z = true;
                z3 = z;
                break;
            case 3:
                int status4 = buf.readShortBE();
                if (status4 == 0) {
                    protocol.trySendData(ResourceManager.createSyncContactsCmd(protocol));
                } else {
                    IOUtils.postOperationError(status4);
                }
                z = true;
                z3 = z;
                break;
            case 4:
                int status5 = buf.readShortBE();
                if (status5 == 0) {
                    protocol.addGroup(new MmpContactGroup(protocol, ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    protocol.trySendData(ResourceManager.createSyncGroupsCmd(protocol));
                } else {
                    IOUtils.postAddGroupError(objArr, status5);
                }
                z = true;
                z3 = z;
                break;
            case 5:
                int status6 = buf.readShortBE();
                if (status6 == 0) {
                    protocol.removeContact((Contact) objArr[2], true);
                    protocol.trySendData(ResourceManager.createSyncContactsCmd(protocol));
                } else {
                    IOUtils.postDeleteError(objArr, status6);
                }
                z = true;
                z3 = z;
                break;
            case 6:
                boolean z4 = (i2 & 1) == 0;
                buf.skip(1);
                Vector results = NetworkUtils.newVector();
                int contactCount = buf.readShortBE();
                for (int i3 = 0; i3 < contactCount; i3++) {
                    String name = buf.readVarLenStr();
                    int groupId = buf.readShortBE();
                    int contactId = buf.readShortBE();
                    int entryType = buf.readShortBE();
                    int dataRemaining = buf.readShortBE();
                    switch (entryType) {
                        case 0:
                            String displayName = name;
                            boolean z5 = false;
                            while (dataRemaining > 0) {
                                int attrType = buf.readShortBE();
                                int dataLen = buf.peekShortBE(0);
                                if (attrType == 305) {
                                    displayName = buf.readVarLenStr();
                                } else {
                                    if (attrType == 102) {
                                        z5 = true;
                                    }
                                    buf.skip(dataLen + 2);
                                }
                                dataRemaining -= dataLen + 4;
                            }
                            results.addElement(new MmpContact(protocol, contactId, groupId, name, displayName, z5));
                            continue;
                        case 1:
                            if (groupId != 0) {
                                protocol.groups.addElement(new MmpContactGroup(protocol, groupId, name));
                            }
                            buf.skip(dataRemaining);
                            continue;
                        case 2:
                            protocol.contactsByIdMap.put(name, ResourceManager.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                        case 3:
                            protocol.contactGroupsMap.put(name, ResourceManager.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                        case 4:
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                        case 13:
                        default:
                            buf.skip(dataRemaining);
                            continue;
                        case 14:
                            protocol.additionalDataMap.put(name, ResourceManager.integerOf(contactId));
                            buf.skip(dataRemaining);
                            continue;
                    }
                    while (dataRemaining > 0) {
                        if (buf.readShortBE() == 202) {
                            protocol.groupSequenceId = contactId;
                        }
                        int entryLen = buf.readShortBE();
                        buf.skip(entryLen);
                        dataRemaining -= entryLen + 4;
                    }
                }
                protocol.contactListIndex = contactCount;
                int size2 = results.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        if (z4) {
                            protocol.sendData(AppController.createMmpCommand(protocol, 4871, (ByteBuffer) null));
                            int i4 = protocol.groupSequenceId;
                            if (i4 != 0) {
                                protocol.sendData(sendContactListRequest(protocol, i4));
                            }
                            protocol.sendData(AppController.createMmpCommand(protocol, 258, new ByteBuffer().writeCompressed(5245205)));
                            protocol.sendData(protocol.queueCommand(new Object[]{AppController.createMmpCommand(protocol, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(protocol.serverId).writeShortLE(60).writeShortBE(0)), ResourceManager.integerOf(8)}));
                            Enumeration elements = protocol.contactMap.elements();
                            while (elements.hasMoreElements()) {
                                Hashtable hashtable = protocol.contactsByIdMap;
                                MmpContact mmpContact = (MmpContact) elements.nextElement();
                                Object obj = hashtable.get(mmpContact.identifier);
                                if (null != obj) {
                                    mmpContact.canDelete = ((Integer) obj).intValue();
                                }
                                Object obj2 = protocol.contactGroupsMap.get(mmpContact.identifier);
                                if (null != obj2) {
                                    mmpContact.canBlock = ((Integer) obj2).intValue();
                                }
                                Object obj3 = protocol.additionalDataMap.get(mmpContact.identifier);
                                if (null != obj3) {
                                    mmpContact.canUnblock = ((Integer) obj3).intValue();
                                }
                            }
                            protocol.contactsByIdMap.clear();
                            protocol.contactGroupsMap.clear();
                            protocol.additionalDataMap.clear();
                            if (protocol.groups.size() == 0) {
                                protocol.sendData(ResourceManager.sendAddGroupCommand(protocol, AppState.getString(459528)));
                            }
                            protocol.progress = 100;
                            protocol.msgCount = 100;
                        }
                        NetworkUtils.releaseVector(results);
                        z = z4;
                        z3 = z;
                        break;
                    } else {
                        MmpContact mmpContact2 = (MmpContact) results.elementAt(size2);
                        int i5 = mmpContact2.onlineSemaphore;
                        int size3 = protocol.groups.size();
                        while (true) {
                            size3--;
                            if (size3 < 0) {
                                group = null;
                                break;
                            } else {
                                MmpContactGroup group2 = (MmpContactGroup) protocol.getGroup(size3);
                                if (group2.groupId == i5) {
                                    group = group2;
                                    break;
                                }
                            }
                        }
                        MmpContactGroup group3 = group;
                        if (null != group) {
                            group3.addContact((Object) mmpContact2);
                        }
                    }
                }
            case 7:
                buf.skip(10);
                if (buf.readShortLE() == 2010) {
                    buf.readShortLE();
                    int subType = buf.readShortLE();
                    buf.readByte();
                    ContactInfo contactInfo = (ContactInfo) AppState.pool[1316];
                    ContactInfo contactInfo2 = contactInfo;
                    if (contactInfo == null) {
                        contactInfo2 = ContactInfo.createAccountInfo(protocol);
                    }
                    switch (subType) {
                        case 200:
                            String nickName = buf.readPascalStr();
                            ContactInfo contactInfo3 = contactInfo2.setDisplayName(nickName).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr()).setCustomField1(buf.readPascalStr());
                            buf.readPascalStr();
                            contactInfo3.setJobTitle(buf.readPascalStr()).setCustomField2(buf.readPascalStr()).setCustomField3(buf.readPascalStr()).setCustomField4(buf.readPascalStr());
                            if (protocol.serverId == ((Integer) objArr[2]).intValue()) {
                                protocol.setDisplayName(nickName);
                                break;
                            }
                            break;
                        case 220:
                            ContactInfo contactInfo4 = contactInfo2.setAge(buf.readShortLE()).setMaritalStatus(buf.readByte()).setCustomField6(buf.readPascalStr());
                            int birthYear = buf.readShortLE();
                            byte birthDay = buf.readByte();
                            byte birthMonth = buf.readByte();
                            if (birthMonth >= 0) {
                                contactInfo4.setCompany(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(Utils.zeroPad(birthMonth + 1)).append('/').append(Utils.zeroPad(birthDay)).append('/').append(birthYear)));
                                break;
                            }
                            break;
                        case 230:
                            contactInfo2.setCustomField5(buf.readPascalStr());
                            break;
                    }
                    boolean z6 = (i2 & 1) == 0;
                    boolean z7 = z6;
                    if (z6) {
                        AppState.pool[1315] = AppState.pool[1316];
                        AppState.pool[1315] = AppState.pool[1316];
                        AppState.clearIndex(1316);
                    }
                    z = z7;
                } else {
                    z = true;
                }
                z3 = z;
                break;
            case 8:
                int i6 = protocol.reserved1;
                protocol.reserved1 = i6 + 1;
                if (0 != i6) {
                    AppState.setInt(1449, 0);
                }
                buf.skip(10);
                int subType3 = buf.readShortLE();
                buf.readShortLE();
                switch (subType3) {
                    case 65:
                        int resultCode = buf.readInt();
                        int year = buf.readShortLE();
                        byte month = buf.readByte();
                        byte dayOfYear = buf.readByte();
                        byte hour = buf.readByte();
                        byte minute = buf.readByte();
                        byte b = (year % 4 != 0 || year == 2000) ? (byte) 28 : (byte) 29;
                        int i7 = (((((year - 1970) * 365) + ((year - 1968) / 4)) + dayOfYear) + 28) - b;
                        if (year >= 2000) {
                            i7--;
                        }
                        byte[] monthDays = AppState.getBytes(945);
                        int i8 = 0;
                        while (i8 < month - 1) {
                            i7 += i8 == 1 ? b : monthDays[i8];
                            i8++;
                        }
                        long j = 1000 * ((86400 * i7) + (hour * 3600) + (minute * 60));
                        buf.readShortBE();
                        if (resultCode != 1004) {
                            protocol.onMessage(Integer.toString(resultCode), j, buf.readModifiedStr());
                        }
                        z = false;
                        break;
                    case 66:
                        AppState.setInt(1449, 1);
                        protocol.trySendData(AppController.createMmpCommand(protocol, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(protocol.serverId).writeShortLE(62).writeShortBE(0)));
                        break;
                    default:
                        z = false;
                        break;
                }
                z3 = z;
                break;
            case 9:
                Vector searchResults = AppState.getVector(1317);
                buf.skip(10);
                if (buf.readShortLE() == 2010) {
                    buf.readShortLE();
                    int subType5 = buf.readShortLE();
                    if ((420 == subType5 || 430 == subType5) && buf.readByte() == 10) {
                        buf.readShortBE();
                        ContactInfo searchResult = ContactInfo.createAccountInfo(protocol).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
                        buf.readByte();
                        searchResults.addElement(searchResult.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE()));
                    }
                    if (subType5 == 430) {
                        AppState.pool[1318] = AppState.getVector(1317);
                        AppState.clearIndex(1317);
                        z2 = true;
                        z3 = z2;
                        break;
                    } else {
                        z2 = false;
                        z3 = z2;
                    }
                } else {
                    z2 = true;
                    z3 = z2;
                }
                break;
            case 10:
                int status14 = buf.readShortBE();
                if (status14 == 0) {
                    MmpContact mmpContact3 = (MmpContact) objArr[2];
                    MmpContactGroup srcGroup4 = (MmpContactGroup) objArr[3];
                    protocol.trySendData(protocol.queueCommand(new Object[]{AppController.createMmpCommand(protocol, 4873, srcGroup4.createUpdatePacket(srcGroup4.name, mmpContact3.userId, -1)), ResourceManager.integerOf(11), mmpContact3, srcGroup4, objArr[4]}));
                } else {
                    IOUtils.postRenameError(objArr, status14);
                }
                z = true;
                z3 = z;
                break;
            case 11:
                int status15 = buf.readShortBE();
                if (status15 == 0) {
                    MmpContact mmpContact4 = (MmpContact) objArr[2];
                    Object obj4 = objArr[3];
                    MmpContactGroup destGroup5 = (MmpContactGroup) objArr[4];
                    protocol.trySendData(protocol.queueCommand(new Object[]{AppController.createMmpCommand(protocol, 4872, mmpContact4.encodeContactUpdate(4, mmpContact4.displayName, destGroup5.groupId)), ResourceManager.integerOf(12), mmpContact4, obj4, destGroup5}));
                } else {
                    IOUtils.postRenameError(objArr, status15);
                }
                z = true;
                z3 = z;
                break;
            case 12:
                int status16 = buf.readShortBE();
                if (status16 == 0) {
                    MmpContact mmpContact5 = (MmpContact) objArr[2];
                    Object obj5 = objArr[3];
                    MmpContactGroup destGroup6 = (MmpContactGroup) objArr[4];
                    protocol.trySendData(protocol.queueCommand(new Object[]{AppController.createMmpCommand(protocol, 4873, destGroup6.createUpdatePacket(destGroup6.name, -1, mmpContact5.userId)), ResourceManager.integerOf(13), mmpContact5, obj5, destGroup6}));
                } else {
                    IOUtils.postRenameError(objArr, status16);
                }
                z = true;
                z3 = z;
                break;
            case 13:
                int status17 = buf.readShortBE();
                if (status17 == 0) {
                    MmpContactGroup srcGroup7 = (MmpContactGroup) objArr[3];
                    MmpContact mmpContact6 = (MmpContact) objArr[2];
                    srcGroup7.removeElement(mmpContact6);
                    MmpContactGroup destGroup8 = (MmpContactGroup) objArr[4];
                    destGroup8.addContact((Object) mmpContact6);
                    mmpContact6.onlineSemaphore = destGroup8.groupId;
                    protocol.trySendData(ResourceManager.createSyncContactsCmd(protocol));
                } else {
                    IOUtils.postRenameError(objArr, status17);
                }
                z = true;
                z3 = z;
                break;
            case 14:
                int status18 = buf.readShortBE();
                if (status18 == 0) {
                    MmpContactGroup destGroup9 = (MmpContactGroup) objArr[4];
                    protocol.trySendData(protocol.queueCommand(new Object[]{AppController.createMmpCommand(protocol, 4873, destGroup9.createUpdatePacket(destGroup9.name, -1, ((Integer) objArr[5]).intValue())), ResourceManager.integerOf(15), objArr[2], objArr[3], destGroup9, objArr[5], objArr[6]}));
                } else {
                    IOUtils.postRenameError(objArr, status18);
                }
                z = true;
                z3 = z;
                break;
            case 15:
                int status19 = buf.readShortBE();
                if (status19 == 0) {
                    MmpContactGroup destGroup10 = (MmpContactGroup) objArr[4];
                    MmpContact mmpContact7 = new MmpContact(protocol, ((Integer) objArr[5]).intValue(), destGroup10.groupId, (String) objArr[2], (String) objArr[3], true);
                    destGroup10.addContact((Object) mmpContact7);
                    protocol.trySendData(ResourceManager.createSyncContactsCmd(protocol));
                    protocol.trySendData(ResourceManager.createGetContactsCmd(protocol));
                    protocol.trySendData(protocol.queueCommand(new Object[]{AppController.createMmpCommand(protocol, 4873, mmpContact7.encodeContactUpdate(5, mmpContact7.displayName, mmpContact7.onlineSemaphore)), ResourceManager.integerOf(16), objArr[2], objArr[3], objArr[4], objArr[5], objArr[6], mmpContact7}));
                    protocol.trySendData(ResourceManager.createSyncContactsCmd(protocol));
                } else {
                    IOUtils.postRenameError(objArr, status19);
                }
                z = true;
                z3 = z;
                break;
            case 16:
                int status20 = buf.readShortBE();
                if (status20 == 0) {
                    protocol.trySendData(ResourceManager.createSyncContactsCmd(protocol));
                    protocol.trySendData(IOUtils.createSendMessageCmd(protocol, (MmpContact) objArr[7], (String) objArr[6]));
                } else {
                    IOUtils.postRenameError(objArr, status20);
                }
                z = true;
                z3 = z;
                break;
            case 17:
                protocol.lastError = protocol.configFlags & 65535;
                z3 = z;
                break;
            case 18:
                int status21 = buf.readShortBE();
                if (status21 == 0) {
                    ((MmpContact) objArr[2]).updatePermissionFlags(((Integer) objArr[3]).intValue(), ((Integer) objArr[4]).intValue());
                } else {
                    IOUtils.postRenameError(objArr, status21);
                }
                z = true;
                z3 = z;
                break;
            case 19:
                int status22 = buf.readShortBE();
                if (status22 == 0) {
                    ((MmpContact) objArr[2]).updatePermissionFlags(((Integer) objArr[3]).intValue(), 0);
                } else {
                    IOUtils.postRenameError(objArr, status22);
                }
                z = true;
                z3 = z;
                break;
            case 20:
                int status23 = buf.readShortBE();
                if (status23 != 0) {
                    IOUtils.postOperationError(status23);
                }
                z = true;
                z3 = z;
                break;
            case 21:
                buf.skip(10);
                if (buf.readShortLE() == 2010) {
                    buf.readShortLE();
                    int subType6 = buf.readShortLE();
                    if ((420 == subType6 || 430 == subType6) && buf.readByte() == 10) {
                        buf.readShortBE();
                        ContactInfo searchResult2 = ContactInfo.createAccountInfo(protocol).setMmpContactId(buf.readInt()).setDisplayName(buf.readPascalStr()).setFirstName(buf.readPascalStr()).setLastName(buf.readPascalStr()).setEmailAddress(buf.readPascalStr());
                        buf.readByte();
                        ContactInfo searchResult3 = searchResult2.setMmpTypeId(buf.readShortLE()).setMaritalStatus(buf.readByte()).setAge(buf.readShortLE());
                        MmpContact mmpContact8 = (MmpContact) protocol.contactMap.get(searchResult3.getString(60));
                        if (null != mmpContact8) {
                            mmpContact8.setDisplayName(searchResult3.getDisplayNameOrId());
                        }
                    }
                    z = subType6 == 430;
                    z3 = z;
                    break;
                }
                break;
        }
        if (z3) {
            vector.removeElementAt(size);
        }
    }

    /* renamed from: a */
    public static final void handleMrimResponse(MrimAccount mrimAccount, ByteBuffer buf, int i) {
        Object[] objArr;
        int resultCode = buf.readInt();
        Vector vector = mrimAccount.extras;
        int size = vector.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                objArr = (Object[]) vector.elementAt(size);
            }
        } while (((Integer) objArr[0]).intValue() != i);
        switch (((Integer) objArr[1]).intValue()) {
            case 0:
                if (resultCode != 0) {
                    IOUtils.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContact) objArr[2]).updateDisplayNameAndGroups((String) objArr[3], (String) objArr[4]);
                    break;
                }
            case 1:
                if (resultCode != 0) {
                    IOUtils.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                    break;
                }
            case 2:
                if (resultCode != 0) {
                    IOUtils.postDeleteError(objArr, resultCode);
                    break;
                } else {
                    mrimAccount.removeContact((Contact) objArr[2], true);
                    break;
                }
            case 3:
                if (resultCode != 0) {
                    IOUtils.postDeleteError(objArr, resultCode);
                    break;
                } else {
                    MrimContactGroup mrimGroup = (MrimContactGroup) objArr[2];
                    int i2 = mrimGroup.groupId >> 24;
                    int size2 = mrimAccount.groups.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            mrimAccount.removeGroup((ContactGroup) mrimGroup);
                            break;
                        } else {
                            MrimContactGroup mrimGroup2 = (MrimContactGroup) mrimAccount.getGroup(size2);
                            if ((mrimGroup2.groupId >> 24) > i2) {
                                mrimGroup2.groupId -= 16777216;
                            }
                        }
                    }
                }
            case 4:
                if (resultCode != 0) {
                    IOUtils.postAddGroupError(objArr, resultCode);
                    break;
                } else {
                    mrimAccount.groups.addElement(new MrimContactGroup(mrimAccount, mrimAccount.findAvailableGroupId(), ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    break;
                }
            case 5:
                if (resultCode != 0) {
                    IOUtils.postAddGroupError(objArr, resultCode);
                    break;
                } else {
                    MrimContactGroup mrimGroup3 = (MrimContactGroup) objArr[4];
                    int contactId2 = buf.readInt();
                    String statusStr = AppState.getString(1233);
                    String str = (String) objArr[2];
                    String str2 = (String) objArr[3];
                    String str3 = AppState.emptyStr;
                    mrimGroup3.addContact((Object) new MrimContact(mrimAccount, contactId2, 1048576, 103, statusStr, str, 0, 1, str2, str3, str3));
                    break;
                }
            case 6:
                if (resultCode != 1) {
                    IOUtils.postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(922)).append(objArr[2]).append(AppState.getString(457)).append(resultCode)));
                    break;
                }
                break;
            case 7:
                NetworkUtils.createSingleContact(mrimAccount, resultCode, buf);
                break;
            case 8:
                NetworkUtils.parseContactInfoResponse(mrimAccount, resultCode, buf);
                break;
            case 9:
                if (resultCode != 0) {
                    if (resultCode != 5) {
                        IOUtils.postAddGroupError(objArr, resultCode);
                        break;
                    }
                } else {
                    MrimContactGroup mrimGroup4 = (MrimContactGroup) objArr[4];
                    int contactId3 = buf.readInt();
                    int flags = ((Integer) objArr[5]).intValue();
                    int i3 = mrimGroup4.serverId;
                    String str4 = (String) objArr[2];
                    String str5 = (String) objArr[3];
                    String str6 = AppState.emptyStr;
                    mrimGroup4.addContact((Object) new MrimContact(mrimAccount, contactId3, flags, i3, str4, str5, 1, 0, str6, str6, str6));
                    break;
                }
                break;
            case 10:
                MrimContact mrimContact = (MrimContact) objArr[2];
                switch (resultCode) {
                    case 0:
                        mrimContact.updateMessageFlag(((Long) objArr[3]).longValue(), 64);
                        break;
                    case 32769:
                        if (mrimContact.isSystem()) {
                            IOUtils.postEvent((Object) AppState.getString(452));
                            break;
                        }
                    default:
                        IOUtils.postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(453)).append(objArr[2]).append(AppState.getString(457)).append(resultCode)));
                        break;
                }
            case 11:
                if (resultCode != 0) {
                    IOUtils.postRenameError(objArr, resultCode);
                    break;
                } else {
                    ((MrimContact) objArr[2]).statusFlags = ((Integer) objArr[3]).intValue();
                    break;
                }
            case 12:
                if (resultCode != 0) {
                    IOUtils.postRenameError(objArr, resultCode);
                    break;
                } else {
                    MrimContact mrimContact2 = (MrimContact) objArr[2];
                    MrimContactGroup mrimGroup5 = (MrimContactGroup) objArr[3];
                    mrimContact2.groupId = mrimGroup5.serverId;
                    int size3 = mrimAccount.groups.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            mrimGroup5.addContact((Object) mrimContact2);
                            break;
                        } else {
                            mrimAccount.getGroup(size3).removeElement(mrimContact2);
                        }
                    }
                }
            case 13:
                NetworkUtils.updateContactName(mrimAccount, resultCode, buf);
                break;
            case 15:
                if (resultCode != 0) {
                    IOUtils.postAddGroupError(objArr, resultCode);
                    break;
                } else {
                    MrimContactGroup defaultGroup = mrimAccount.getFirstContactGroup();
                    int contactId4 = buf.readInt();
                    int i4 = defaultGroup.serverId;
                    String contactName = buf.readWideStr();
                    String str7 = (String) objArr[2];
                    String str8 = AppState.emptyStr;
                    defaultGroup.addContact(new MrimContact(mrimAccount, contactId4, 128, i4, contactName, str7, 0, 1, str8, str8, str8));
                    break;
                }
            case 16:
                NetworkUtils.addContactToGroup(mrimAccount, resultCode, buf);
                break;
            case 17:
                ResourceManager.handleAuthResponse(mrimAccount, resultCode, objArr, buf);
                break;
        }
        vector.removeElementAt(size);
    }
}
