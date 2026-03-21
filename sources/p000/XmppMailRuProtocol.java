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

    public XmppMailRuProtocol(ByteBuffer c0043n) {
        super(c0043n);
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
        Account abstractC0037hM616i = AppState.getAccount();
        return null != abstractC0037hM616i ? abstractC0037hM616i.getType() : AppState.getInt(1475);
    }

    /* renamed from: r */
    public static final void showLoginScreen() {
        if (getAccountType() == 1) {
            Account abstractC0037hM616i = AppState.getAccount();
            if (abstractC0037hM616i != null && abstractC0037hM616i.isConnecting()) {
                AppController.showMessageById(300);
                return;
            }
            clearLoginFields();
            if (abstractC0037hM616i != null) {
                AppState.setObject(1292, (Object) abstractC0037hM616i.login);
                AppState.setObject(1293, (Object) abstractC0037hM616i.password);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(2803));
            return;
        }
        if (getAccountType() == 2) {
            XmppProtocol c0005ae = (XmppProtocol) AppState.getAccount();
            if (c0005ae != null && c0005ae.isConnecting()) {
                AppController.showMessageById(300);
                return;
            }
            clearLoginFields();
            AppState.setInt(1474, 0);
            if (c0005ae != null) {
                String strM13b = c0005ae.login;
                Vector parts = Utils.splitNonEmpty(AppState.getString(696), (char) 0);
                int count = Utils.vectorSize(parts);
                while (true) {
                    count--;
                    if (count < 1) {
                        break;
                    }
                    int iIndexOf = strM13b.indexOf((String) parts.elementAt(count));
                    if (iIndexOf >= 0) {
                        strM13b = StringUtils.prefix(strM13b, iIndexOf);
                        break;
                    }
                }
                AppState.setInt(1474, count);
                AppState.setObject(1292, (Object) strM13b);
                AppState.setObject(1293, (Object) c0005ae.password);
                AppState.setObject(1297, (Object) c0005ae.displayName);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(3443));
            return;
        }
        if (getAccountType() == 3) {
            XmppMailRuProtocol c0031bd = (XmppMailRuProtocol) AppState.getAccount();
            if (c0031bd != null && c0031bd.isConnecting()) {
                AppController.showMessageById(300);
                return;
            }
            clearLoginFields();
            if (c0031bd != null) {
                AppState.setObject(1292, (Object) c0031bd.login);
                AppState.setObject(1293, (Object) c0031bd.password);
                AppState.setObject(1297, (Object) c0031bd.displayName);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(3463));
            return;
        }
        clearLoginFields();
        AppState.setInt(1474, 0);
        Account abstractC0037hM616i2 = AppState.getAccount();
        if (null != abstractC0037hM616i2) {
            AppState.setObject(1293, (Object) abstractC0037hM616i2.password);
            String str = abstractC0037hM616i2.login;
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
                int iIndexOf2 = str.indexOf((String) domains2.elementAt(i));
                if (iIndexOf2 >= 0) {
                    AppState.setInt(1474, i);
                    AppState.setObject(1292, (Object) StringUtils.prefix(str, iIndexOf2));
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
            Account abstractC0037hM616i = AppState.getAccount();
            String strM843u = getLoginLowerCase();
            int errorCode = AppController.validateCredentials(1, abstractC0037hM616i, strM843u, Utils.defaultStr(AppState.getString(1293)));
            if (0 != errorCode) {
                return AppController.showError(errorCode);
            }
            AppController.setCurrentAccount(AppController.createAccount(1, strM843u));
            return 0;
        }
        if (getAccountType() == 2) {
            return IOUtils.loginXmpp(2);
        }
        if (getAccountType() == 3) {
            AppState.setInt(1474, 0);
            return IOUtils.loginXmpp(3);
        }
        String strM522f = Utils.defaultStr(AppState.getString(1293));
        String strM843u2 = getLoginLowerCase();
        String strM9b = strM843u2;
        if (StringUtils.isEmpty(strM843u2)) {
            return AppController.showError(301);
        }
        if (!containsDomainSuffix(strM9b, 694) && !containsDomainSuffix(strM9b, 695)) {
            strM9b = StringUtils.concat(strM9b, Utils.splitAndGet(694, AppState.getInt(1474)));
        }
        if (!isValidUsername(strM9b)) {
            return AppController.showError(559);
        }
        int errorCode2 = AppController.validateCredentials(0, AppState.getAccount(), strM9b, strM522f);
        if (0 != errorCode2) {
            return AppController.showError(errorCode2);
        }
        AppController.setCurrentAccount(AppController.createAccount(0, strM9b));
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
            char cCharAt = str.charAt(length);
            if (cCharAt < 'A' || cCharAt > 'Z') {
                if (cCharAt < 'a' || cCharAt > 'z') {
                    if (cCharAt < '0' || cCharAt > '9') {
                        if (cCharAt != '.' && cCharAt != '_' && cCharAt != '-' && cCharAt != '@') {
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
    private static void saveTileToCache(ResourceManager c0034e, byte[] bArr, int i, int i2) {
        if (c0034e == null || bArr == null) {
            return;
        }
        String strM850c = buildTileCacheKey(c0034e);
        ByteBuffer c0043nM1303a = new ByteBuffer().writeStringLatin1(c0034e.tileUrl).writeLong(System.currentTimeMillis()).writeBytesAt(bArr, 0, i2);
        int i3 = 4;
        while (true) {
            i3--;
            if (i3 <= 0) {
                return;
            }
            try {
                try {
                    if (c0043nM1303a.length + AppState.getInt(1552) >= 204800) {
                        throw new Throwable();
                    }
                    RecordStore store = IOUtils.openRecordStore(strM850c, true);
                    byte[] bArr2 = c0043nM1303a.data;
                    int i4 = c0043nM1303a.offset;
                    int i5 = c0043nM1303a.length;
                    store.addRecord(bArr2, i4, i5);
                    AppState.setInt(1552, AppState.getInt(1552) + i5);
                    c0043nM1303a.clear();
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
    public static final Image loadTileFromCache(ResourceManager c0034e) {
        String strM850c = buildTileCacheKey(c0034e);
        RecordStore recordStore = null;
        try {
            String str = c0034e.tileUrl;
            RecordStore store = IOUtils.openRecordStore(strM850c, false);
            recordStore = store;
            int numRecords = store.getNumRecords();
            for (int i = 1; i <= numRecords; i++) {
                ByteBuffer c0043nM1304b = new ByteBuffer().setData(recordStore.getRecord(i));
                if (c0043nM1304b.readWideStr().equals(str)) {
                    c0043nM1304b.readLong();
                    Image imageM1348r = c0043nM1304b.toImage();
                    IOUtils.closeRecordStore(recordStore);
                    return imageM1348r;
                }
                c0043nM1304b.clear();
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
            String strM584b = AppState.getString(332005);
            int length = storeNames.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                String str2 = storeNames[length];
                if (str2.startsWith(strM584b)) {
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
        String strM848z = findOldestCacheStore();
        if (strM848z != null) {
            RecordStore recordStore = null;
            try {
                RecordStore store = IOUtils.openRecordStore(strM848z, false);
                recordStore = store;
                int numRecords = store.getNumRecords();
                for (int i = 1; i <= numRecords; i++) {
                    AppState.setInt(1552, AppState.getInt(1552) - recordStore.getRecordSize(i));
                }
                IOUtils.closeRecordStore(recordStore);
                try {
                    RecordStore.deleteRecordStore(strM848z);
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                IOUtils.closeRecordStore(recordStore);
                try {
                    RecordStore.deleteRecordStore(strM848z);
                } catch (Throwable unused3) {
                }
            }
        }
    }

    /* renamed from: c */
    private static final String buildTileCacheKey(ResourceManager c0034e) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(332005)).append(c0034e.tileType).append('z').append(c0034e.zoomLevel).append('x').append((c0034e.tileX / 4) << 2).append('y').append((c0034e.tileY / 4) << 2));
    }

    /* renamed from: h */
    public static final ByteBuffer readChunkedRecord(String str) {
        ByteBuffer c0043n = new ByteBuffer();
        int i = 0;
        while (true) {
            RecordStore recordStore = null;
            try {
                int i2 = i;
                i++;
                RecordStore store = IOUtils.openRecordStore(buildChunkName(str, i2), false);
                recordStore = store;
                byte[] record = store.getRecord(1);
                c0043n.writeBytes(record);
                NetworkUtils.releaseBytes(record);
                IOUtils.closeRecordStore(recordStore);
            } catch (RuntimeException th) {
                IOUtils.closeRecordStore(recordStore);
                throw th;
            } catch (Throwable th) {
                IOUtils.closeRecordStore(recordStore);
                throw new RuntimeException(th);
            }
        }
    }

    /* renamed from: a */
    public static final void writeRecord(String str, ByteBuffer c0043n, boolean z) {
        if (z) {
            writeChunkedRecord(str, c0043n);
            return;
        }
        int i = c0043n.length;
        if (i == 0) {
            String[] storeNames = StringUtils.listRecordStores();
            int i2 = 0;
            while (true) {
                int i3 = i2;
                i2++;
                String strM856d = buildChunkName(str, i3);
                if (!recordStoreExists(storeNames, strM856d)) {
                    break;
                } else {
                    try {
                        RecordStore.deleteRecordStore(strM856d);
                    } catch (Throwable unused) {
                    }
                }
            }
        } else {
            RecordStore recordStore = null;
            try {
                byte[] bArr = c0043n.compact().data;
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
                    String strM856d2 = buildChunkName(str, i4);
                    if (!recordStoreExists(storeNames2, strM856d2)) {
                        break;
                    } else {
                        try {
                            RecordStore.deleteRecordStore(strM856d2);
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
        c0043n.clear();
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:6:0x0033 */
    /* renamed from: a */
    public static final void writeChunkedRecord(String str, ByteBuffer c0043n) {
        int i = 0;
        int i2 = c0043n.length;
        if (i2 > 0) {
            byte[] bArr = c0043n.compact().data;
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
            String strM856d = buildChunkName(str, i5);
            if (!recordStoreExists(storeNames, strM856d)) {
                return;
            } else {
                try {
                    RecordStore.deleteRecordStore(strM856d);
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
        StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append(i);
        StringBuffer stringBufferAppend2 = NetworkUtils.newStringBuffer().append('s').append(str).append('s');
        while (stringBufferAppend2.length() + stringBufferAppend.length() > 32) {
            stringBufferAppend2.setLength(stringBufferAppend2.length() - 1);
        }
        return NetworkUtils.bufToStringCached(stringBufferAppend2.append((Object) stringBufferAppend));
    }

    /* renamed from: a */
    public static final ByteBuffer sendContactListRequest(MmpProtocol c0033d, int i) {
        return c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4873, new ByteBuffer().writeIntLE(0).writeShortBE(i).writeShortBE(4).writeShortBE(5).writeShortBE(202).writeShortBE(1).writeByte(c0033d.getPendingVersion())), ResourceManager.integerOf(20)});
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:30:0x00ce */
    /* renamed from: w */
    public static final void showMapContextMenu() {
        ListItem interfaceC0044o;
        int i = 3072;
        if (AppController.getOnlineMrimAccounts().size() > 0) {
            i = 11264;
        }
        if (mapContextItem != null) {
            interfaceC0044o = mapContextItem;
        } else {
            interfaceC0044o = MapRenderer.tooltipItem;
            mapContextItem = interfaceC0044o;
        }
        ListItem interfaceC0044o2 = interfaceC0044o;
        if (interfaceC0044o != null && interfaceC0044o2.isSelected()) {
            switch (interfaceC0044o2.getHeight()) {
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
        long jMo274v;
        long jMo275w;
        ListItem interfaceC0044o = mapContextItem;
        int itemType = interfaceC0044o == null ? 0 : interfaceC0044o.getHeight();
        switch (i) {
            case 0:
                AppState.setCurrentEntity(interfaceC0044o);
                ScreenBuilder.onScreenClosed();
                return 63;
            case 1:
                if (itemType == 8) {
                    AppState.setInt(4895, 0);
                    AppController.openUserProfile((MrimAccount) null, ((UserSearchResult) interfaceC0044o).userId);
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
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) interfaceC0044o).userId, 1));
                ScreenBuilder.onScreenClosed();
                return 85;
            case 3:
                Vector onlineAccounts2 = AppController.getOnlineMrimAccounts();
                if (onlineAccounts2 == null || onlineAccounts2.size() <= 0) {
                    return AppController.showError(422);
                }
                ((MrimAccount) onlineAccounts2.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) interfaceC0044o).userId, 2));
                ScreenBuilder.onScreenClosed();
                return 6;
            case 4:
                ScreenBuilder.onScreenClosed();
                AppState.pool[1255] = (Conversation) interfaceC0044o;
                return 170;
            case 5:
                ResourceManager.dialPhoneUrl(VCard.formatPhoneContactUrl((PhoneContact) interfaceC0044o, 0), (PhoneContact) interfaceC0044o, 0);
                return 12;
            case 6:
                AppState.setAccount(interfaceC0044o);
                ScreenBuilder.onScreenClosed();
                return 167;
            case 7:
                AppState.setAccount(interfaceC0044o);
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
                ListItem interfaceC0044o2 = MapRenderer.tooltipItem;
                if (interfaceC0044o2 == null || !interfaceC0044o2.isSelected()) {
                    jMo274v = MapRenderer.currentLon;
                    jMo275w = MapRenderer.currentLat;
                } else {
                    jMo274v = interfaceC0044o2.getWidth();
                    jMo275w = interfaceC0044o2.getBaseHeight();
                    interfaceC0044o2.select();
                }
                int[] iArr = {(int) jMo274v, (int) jMo275w};
                MmpContact.routePoints.addElement(iArr);
                MmpContact.nearestPoints.addElement(new Object[]{null, iArr});
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
                ListItem interfaceC0044o3 = MapRenderer.tooltipItem;
                if (interfaceC0044o3 != null && interfaceC0044o3.isSelected()) {
                    interfaceC0044o3.select();
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
            String strM861j = dnsLookupSrv(StringUtils.concatKey(1185660, StringUtils.suffix(str, str.indexOf(64) + 1)));
            if (strM861j == null || strM861j.indexOf(58) <= 0) {
                XmppProtocol c0005ae = (XmppProtocol) objArr[0];
                c0005ae.setAuthParameters(c0005ae.mo84j(), 5222);
            } else {
                Vector parts = Utils.splitNonEmpty(strM861j, ':');
                ((XmppProtocol) objArr[0]).setAuthParameters(Utils.getVectorString(parts, 0), Integer.parseInt(Utils.getVectorString(parts, 1)));
                NetworkUtils.releaseVector(parts);
            }
        } catch (Throwable th) {
            ((XmppProtocol) objArr[0]).setException(th);
        }
    }

    /* renamed from: j */
    private static final String dnsLookupSrv(String str) {
        String strM1317c;
        DatagramConnection datagramConnection = null;
        try {
            AppController.acquireNetworkLock();
            Vector parts = Utils.splitNonEmpty(str, '.');
            ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(792490);
            for (int i = 0; i < Utils.vectorSize(parts); i++) {
                c0043nM1310c.writeByteLenStr(Utils.getVectorString(parts, i));
            }
            NetworkUtils.releaseVector(parts);
            c0043nM1310c.writeCompressed(333750);
            DatagramConnection datagramConnection2 = (DatagramConnection) IOUtils.registerResource((Object) Connector.open(AppState.getString(1841038)));
            datagramConnection = datagramConnection2;
            datagramConnection2.send(datagramConnection2.newDatagram(c0043nM1310c.data, c0043nM1310c.length));
            c0043nM1310c.clear();
            Datagram datagramNewDatagram = datagramConnection.newDatagram(512);
            datagramConnection.receive(datagramNewDatagram);
            ByteBuffer c0043nM1304b = new ByteBuffer().setData(datagramNewDatagram.getData());
            c0043nM1304b.skip(6);
            if (c0043nM1304b.readShortBE() <= 0) {
                strM1317c = null;
            } else {
                c0043nM1304b.readInt();
                while (true) {
                    int labelLen = c0043nM1304b.readUByte();
                    int i2 = labelLen;
                    if (labelLen == 0) {
                        break;
                    }
                    while (true) {
                        i2--;
                        if (i2 < 0) {
                            break;
                        }
                        c0043nM1304b.readUByte();
                    }
                }
                c0043nM1304b.skip(20);
                int iM1353u = c0043nM1304b.readShortBE();
                ByteBuffer c0043n = new ByteBuffer();
                int labelLen2 = c0043nM1304b.readUByte();
                while (true) {
                    labelLen2--;
                    if (labelLen2 < 0) {
                        int labelLen3 = c0043nM1304b.readUByte();
                        labelLen2 = labelLen3;
                        if (labelLen3 == 0) {
                            break;
                        }
                        c0043n.writeByte(46);
                    } else {
                        c0043n.writeByte(c0043nM1304b.readUByte());
                    }
                }
                strM1317c = c0043n.writeByte(58).writeIntAsString(iM1353u).getStringAndClear();
            }
            String str2 = strM1317c;
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
    public static final void writeAddressPairs(Vector vector, ByteBuffer c0043n) {
        int count = Utils.vectorSize(vector);
        c0043n.writeIntLE(count);
        for (int i = 0; i < count; i++) {
            String[] strArr = (String[]) vector.elementAt(i);
            c0043n.writeStringUTF16(strArr[0]).writeStringUTF16(strArr[1]);
        }
    }

    /* renamed from: e */
    public static final Vector readAddressPairs(ByteBuffer c0043n) {
        Vector results = NetworkUtils.newVector();
        int resultCode = c0043n.readInt();
        while (true) {
            resultCode--;
            if (resultCode < 0) {
                return results;
            }
            results.addElement(new String[]{c0043n.readUTF8Str((String) null), c0043n.readUTF8Str((String) null)});
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
            Enumeration enumerationElements = vector2.elements();
            while (enumerationElements.hasMoreElements()) {
                addUniqueAddress(vector, (String[]) enumerationElements.nextElement());
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
            char cCharAt = i < length ? str.charAt(i) : ',';
            if (!z) {
                z = true;
            } else if (cCharAt == ',') {
                results.addElement(NetworkUtils.bufToString(sb, false));
                z = false;
            } else {
                sb.append(cCharAt);
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
            char cCharAt = i == length ? ';' : str.charAt(i);
            char c = cCharAt;
            if (cCharAt != ';' && c != ',' && c != ' ') {
                sb.append(c);
            } else if (sb.length() > 0) {
                String strM1214a = NetworkUtils.bufToString(sb, false);
                results.addElement(new String[]{strM1214a, strM1214a});
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
        String strM1215a;
        Object[] objArrM816k = IOUtils.pollAsyncResult();
        if (objArrM816k == null) {
            return handleMailRedirect();
        }
        Object[] objArrM1156c = ConnectionThread.getAsyncResult(objArrM816k);
        if (objArrM1156c == null) {
            return 0;
        }
        int iM818c = IOUtils.validateJsonResponse(objArrM1156c);
        if (iM818c != 0) {
            return iM818c;
        }
        String strM584b = AppState.getString(1346);
        ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513));
        Message message = chatRoom.getMessage(strM584b);
        boolean zM671a = message.hasFlag(4);
        Object objM819l = IOUtils.getJsonPayload();
        Object objM476a = JsonParser.getValueByInt(objM819l, 722874);
        int size = ((Vector) objM476a).size();
        int i = size;
        Object[] objArr = new Object[size];
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            Object objM482e = JsonParser.getVectorElement(objM476a, i);
            objArr[i] = new String[]{JsonParser.getStringByInt(objM482e, 1227), JsonParser.getStringByInt(objM482e, 1228), JsonParser.getStringByInt(objM482e, 1229), JsonParser.getStringByInt(objM482e, 1230), JsonParser.getStringByInt(objM482e, 1231), JsonParser.getStringByInt(objM482e, 1232)};
        }
        message.attachments = objArr;
        String str = (String) JsonParser.getValueByInt(objM819l, 919493);
        if (str == null) {
            strM1215a = AppState.emptyStr;
        } else {
            StringBuffer sb = NetworkUtils.newStringBuffer();
            int length = str.length();
            int i2 = 0;
            while (i2 < length) {
                char cCharAt = str.charAt(i2);
                sb.append(cCharAt);
                if (cCharAt == ' ') {
                    while (i2 + 1 < length && str.charAt(i2 + 1) == ' ') {
                        i2++;
                    }
                }
                if (cCharAt == '\n') {
                    while (i2 + 1 < length && str.charAt(i2 + 1) == '\n') {
                        i2++;
                    }
                }
                i2++;
            }
            strM1215a = NetworkUtils.bufToStringCached(sb);
        }
        message.body = strM1215a;
        if (zM671a) {
            message.setFlag(4, false);
            chatRoom.decrementUnread();
        }
        return handleMailRedirect();
    }

    /* renamed from: T */
    private static final int handleMailRedirect() {
        int iM586d = AppState.getInt(1515);
        if (iM586d == 54) {
            Message message = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513)).getMessage(AppState.getString(1346));
            Vector toList = message.getToList();
            Vector ccList = message.getCcList();
            getFirstRecipient(toList);
            String strM673d = message.getSubject();
            String str = message.body;
            String strM584b = AppState.getString(198549);
            String strM584b2 = AppState.getString(198546);
            String string = new StringBuffer().append(AppState.getString(838)).append(Utils.quoteText(str)).toString();
            switch (AppState.getInt(1516)) {
                case 0:
                    ResourceManager.composeEmail(getFirstAddress(toList), new StringBuffer().append(strM584b).append(strM673d).toString(), string);
                    break;
                case 1:
                    ResourceManager.composeEmail(mergeAddressLists(copyAddressList(toList), ccList), new StringBuffer().append(strM584b).append(strM673d).toString(), string);
                    break;
                case 2:
                    ResourceManager.composeEmail(NetworkUtils.newVector(), new StringBuffer().append(strM584b2).append(strM673d).toString(), string);
                    break;
                case 3:
                    ResourceManager.composeEmail(copyAddressList(ccList), strM673d, str);
                    break;
            }
        }
        return iM586d;
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
    public static final Image fetchTileImage(ResourceManager c0034e) throws IOException {
        ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(2232520).writeRawString(c0034e.tileUrl).writeCompressed(3870861).writeExtendedInt(2950495).writeEncodedInt(222).writeCompressed(6689002);
        try {
            Object[] objArrM609l = AppState.getObjectArray(1392);
            byte[] bArr = c0043nM1310c.data;
            int i = c0043nM1310c.length;
            NetworkUtils.writeSocket(objArrM609l, bArr, i);
            AppController.addUploadBytes(i);
        } catch (Throwable unused) {
            if (!reconnectHttp()) {
                throw new IOException();
            }
            Object[] objArrM609l2 = AppState.getObjectArray(1392);
            byte[] bArr2 = c0043nM1310c.data;
            int i2 = c0043nM1310c.length;
            NetworkUtils.writeSocket(objArrM609l2, bArr2, i2);
            AppController.addUploadBytes(i2);
        } finally {
            c0043nM1310c.clear();
        }
        String strM877V = readHttpHeaders();
        if (strM877V == null) {
            NetworkUtils.closeConnection(AppState.getObjectArray(1392));
            throw new IOException();
        }
        AppState.addInt(1548, strM877V.getBytes().length);
        if (parseHttpStatus(strM877V) != 200) {
            int iM880m = parseContentLength(strM877V);
            try {
                if (iM880m > 0) {
                    ((InputStream) AppState.getObjectArray(1392)[1]).skip(iM880m);
                } else {
                    NetworkUtils.closeConnection(AppState.getObjectArray(1392));
                }
                return null;
            } catch (Throwable unused2) {
                return null;
            }
        }
        ByteBuffer c0043nM878i = readHttpBody(parseContentLength(strM877V));
        if (c0043nM878i == null) {
            NetworkUtils.closeConnection(AppState.getObjectArray(1392));
            throw new IOException();
        }
        AppState.addInt(1548, c0043nM878i.length);
        byte[] bArr3 = c0043nM878i.data;
        int i3 = c0043nM878i.length;
        if (AppState.getBool(1551)) {
            saveTileToCache(c0034e, bArr3, 0, i3);
        }
        AppController.addDownloadBytes(c0043nM878i.length + 255);
        return c0043nM878i.toImage();
    }

    /* renamed from: V */
    private static final String readHttpHeaders() {
        Object[] objArrM609l = AppState.getObjectArray(1392);
        ByteBuffer c0043n = new ByteBuffer();
        int i = 0;
        while (true) {
            try {
                int i2 = ((InputStream) objArrM609l[1]).read();
                if (i2 == -1) {
                    return null;
                }
                c0043n.writeByte(i2);
                if (i2 == 10) {
                    i++;
                    if (i == 34) {
                        return c0043n.getStringAndClear();
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
            ByteBuffer c0043n = new ByteBuffer();
            int iM1190a = 0;
            int i2 = 0;
            byte[] bArrM1211a = NetworkUtils.newBytes(8192);
            int length = bArrM1211a.length;
            Object[] objArrM609l = AppState.getObjectArray(1392);
            while (i2 != i && iM1190a != -1) {
                iM1190a = NetworkUtils.readSocket(objArrM609l, bArrM1211a, 0, Utils.min(length, i - i2));
                c0043n.writeBytesAt(bArrM1211a, 0, iM1190a);
                i2 += iM1190a;
            }
            NetworkUtils.releaseBytes(bArrM1211a);
            return c0043n;
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
            int iM628b = AppState.indexOfPool(StringUtils.intern(str.toLowerCase()), 1052310) + 16;
            return Integer.parseInt(StringUtils.substring(str, iM628b, str.indexOf(13, iM628b)));
        } catch (Throwable unused) {
            return -1;
        }
    }

    /* renamed from: b */
    public static final void removeQueuedCommand(MmpProtocol c0033d, int i) {
        Vector vector = c0033d.extras;
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
    public static final void handleMmpResponse(MmpProtocol c0033d, ByteBuffer c0043n, int i, int i2) {
        Object[] objArr;
        boolean z = false;
        boolean z2;
        MmpContactGroup group;
        Vector vector = c0033d.extras;
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
                int iM1353u = c0043n.readShortBE();
                if (iM1353u == 0) {
                    ((MmpContact) objArr[2]).setDisplayName((String) objArr[3]);
                } else {
                    IOUtils.postRenameError(objArr, iM1353u);
                }
                z = true;
                z3 = z;
                break;
            case 1:
                int iM1353u2 = c0043n.readShortBE();
                if (iM1353u2 == 0) {
                    ((MmpContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                } else {
                    IOUtils.postRenameError(objArr, iM1353u2);
                }
                z = true;
                z3 = z;
                break;
            case 2:
                int iM1353u3 = c0043n.readShortBE();
                if (iM1353u3 == 0) {
                    c0033d.removeGroup((ContactGroup) objArr[2]);
                    c0033d.trySendData(ResourceManager.createSyncGroupsCmd(c0033d));
                } else {
                    IOUtils.postDeleteError(objArr, iM1353u3);
                }
                z = true;
                z3 = z;
                break;
            case 3:
                int iM1353u4 = c0043n.readShortBE();
                if (iM1353u4 == 0) {
                    c0033d.trySendData(ResourceManager.createSyncContactsCmd(c0033d));
                } else {
                    IOUtils.postOperationError(iM1353u4);
                }
                z = true;
                z3 = z;
                break;
            case 4:
                int iM1353u5 = c0043n.readShortBE();
                if (iM1353u5 == 0) {
                    c0033d.addGroup(new MmpContactGroup(c0033d, ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    c0033d.trySendData(ResourceManager.createSyncGroupsCmd(c0033d));
                } else {
                    IOUtils.postAddGroupError(objArr, iM1353u5);
                }
                z = true;
                z3 = z;
                break;
            case 5:
                int iM1353u6 = c0043n.readShortBE();
                if (iM1353u6 == 0) {
                    c0033d.removeContact((Contact) objArr[2], true);
                    c0033d.trySendData(ResourceManager.createSyncContactsCmd(c0033d));
                } else {
                    IOUtils.postDeleteError(objArr, iM1353u6);
                }
                z = true;
                z3 = z;
                break;
            case 6:
                boolean z4 = (i2 & 1) == 0;
                c0043n.skip(1);
                Vector results = NetworkUtils.newVector();
                int iM1353u7 = c0043n.readShortBE();
                for (int i3 = 0; i3 < iM1353u7; i3++) {
                    String strM1364A = c0043n.readVarLenStr();
                    int iM1353u8 = c0043n.readShortBE();
                    int iM1353u9 = c0043n.readShortBE();
                    int iM1353u10 = c0043n.readShortBE();
                    int iM1353u11 = c0043n.readShortBE();
                    switch (iM1353u10) {
                        case 0:
                            String strM1364A2 = strM1364A;
                            boolean z5 = false;
                            while (iM1353u11 > 0) {
                                int iM1353u12 = c0043n.readShortBE();
                                int iM1351l = c0043n.peekShortBE(0);
                                if (iM1353u12 == 305) {
                                    strM1364A2 = c0043n.readVarLenStr();
                                } else {
                                    if (iM1353u12 == 102) {
                                        z5 = true;
                                    }
                                    c0043n.skip(iM1351l + 2);
                                }
                                iM1353u11 -= iM1351l + 4;
                            }
                            results.addElement(new MmpContact(c0033d, iM1353u9, iM1353u8, strM1364A, strM1364A2, z5));
                            continue;
                        case 1:
                            if (iM1353u8 != 0) {
                                c0033d.groups.addElement(new MmpContactGroup(c0033d, iM1353u8, strM1364A));
                            }
                            c0043n.skip(iM1353u11);
                            continue;
                        case 2:
                            c0033d.contactsByIdMap.put(strM1364A, ResourceManager.integerOf(iM1353u9));
                            c0043n.skip(iM1353u11);
                            continue;
                        case 3:
                            c0033d.contactGroupsMap.put(strM1364A, ResourceManager.integerOf(iM1353u9));
                            c0043n.skip(iM1353u11);
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
                            c0043n.skip(iM1353u11);
                            continue;
                        case 14:
                            c0033d.additionalDataMap.put(strM1364A, ResourceManager.integerOf(iM1353u9));
                            c0043n.skip(iM1353u11);
                            continue;
                    }
                    while (iM1353u11 > 0) {
                        if (c0043n.readShortBE() == 202) {
                            c0033d.groupSequenceId = iM1353u9;
                        }
                        int iM1353u13 = c0043n.readShortBE();
                        c0043n.skip(iM1353u13);
                        iM1353u11 -= iM1353u13 + 4;
                    }
                }
                c0033d.contactListIndex = iM1353u7;
                int size2 = results.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        if (z4) {
                            c0033d.sendData(AppController.createMmpCommand(c0033d, 4871, (ByteBuffer) null));
                            int i4 = c0033d.groupSequenceId;
                            if (i4 != 0) {
                                c0033d.sendData(sendContactListRequest(c0033d, i4));
                            }
                            c0033d.sendData(AppController.createMmpCommand(c0033d, 258, new ByteBuffer().writeCompressed(5245205)));
                            c0033d.sendData(c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(c0033d.serverId).writeShortLE(60).writeShortBE(0)), ResourceManager.integerOf(8)}));
                            Enumeration enumerationElements = c0033d.contactMap.elements();
                            while (enumerationElements.hasMoreElements()) {
                                Hashtable hashtable = c0033d.contactsByIdMap;
                                MmpContact mmpContact = (MmpContact) enumerationElements.nextElement();
                                Object obj = hashtable.get(mmpContact.identifier);
                                if (null != obj) {
                                    mmpContact.canDelete = ((Integer) obj).intValue();
                                }
                                Object obj2 = c0033d.contactGroupsMap.get(mmpContact.identifier);
                                if (null != obj2) {
                                    mmpContact.canBlock = ((Integer) obj2).intValue();
                                }
                                Object obj3 = c0033d.additionalDataMap.get(mmpContact.identifier);
                                if (null != obj3) {
                                    mmpContact.canUnblock = ((Integer) obj3).intValue();
                                }
                            }
                            c0033d.contactsByIdMap.clear();
                            c0033d.contactGroupsMap.clear();
                            c0033d.additionalDataMap.clear();
                            if (c0033d.groups.size() == 0) {
                                c0033d.sendData(ResourceManager.sendAddGroupCommand(c0033d, AppState.getString(459528)));
                            }
                            c0033d.progress = 100;
                            c0033d.msgCount = 100;
                        }
                        NetworkUtils.releaseVector(results);
                        z = z4;
                        z3 = z;
                        break;
                    } else {
                        MmpContact mmpContact2 = (MmpContact) results.elementAt(size2);
                        int i5 = mmpContact2.onlineSemaphore;
                        int size3 = c0033d.groups.size();
                        while (true) {
                            size3--;
                            if (size3 < 0) {
                                group = null;
                                break;
                            } else {
                                MmpContactGroup group2 = (MmpContactGroup) c0033d.getGroup(size3);
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
                c0043n.skip(10);
                if (c0043n.readShortLE() == 2010) {
                    c0043n.readShortLE();
                    int iM1354v = c0043n.readShortLE();
                    c0043n.readByte();
                    ContactInfo contactInfo = (ContactInfo) AppState.pool[1316];
                    ContactInfo contactInfo2 = contactInfo;
                    if (contactInfo == null) {
                        contactInfo2 = ContactInfo.createAccountInfo(c0033d);
                    }
                    switch (iM1354v) {
                        case 200:
                            String strM1366C = c0043n.readPascalStr();
                            ContactInfo contactInfo3 = contactInfo2.setDisplayName(strM1366C).setFirstName(c0043n.readPascalStr()).setLastName(c0043n.readPascalStr()).setEmailAddress(c0043n.readPascalStr()).setCustomField1(c0043n.readPascalStr());
                            c0043n.readPascalStr();
                            contactInfo3.setJobTitle(c0043n.readPascalStr()).setCustomField2(c0043n.readPascalStr()).setCustomField3(c0043n.readPascalStr()).setCustomField4(c0043n.readPascalStr());
                            if (c0033d.serverId == ((Integer) objArr[2]).intValue()) {
                                c0033d.setDisplayName(strM1366C);
                                break;
                            }
                            break;
                        case 220:
                            ContactInfo contactInfo4 = contactInfo2.setAge(c0043n.readShortLE()).setMaritalStatus(c0043n.readByte()).setCustomField6(c0043n.readPascalStr());
                            int iM1354v2 = c0043n.readShortLE();
                            byte bM1344o = c0043n.readByte();
                            byte bM1344o2 = c0043n.readByte();
                            if (bM1344o2 >= 0) {
                                contactInfo4.setCompany(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(Utils.zeroPad(bM1344o2 + 1)).append('/').append(Utils.zeroPad(bM1344o)).append('/').append(iM1354v2)));
                                break;
                            }
                            break;
                        case 230:
                            contactInfo2.setCustomField5(c0043n.readPascalStr());
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
                int i6 = c0033d.reserved1;
                c0033d.reserved1 = i6 + 1;
                if (0 != i6) {
                    AppState.setInt(1449, 0);
                }
                c0043n.skip(10);
                int iM1354v3 = c0043n.readShortLE();
                c0043n.readShortLE();
                switch (iM1354v3) {
                    case 65:
                        int resultCode = c0043n.readInt();
                        int iM1354v4 = c0043n.readShortLE();
                        byte bM1344o3 = c0043n.readByte();
                        byte bM1344o4 = c0043n.readByte();
                        byte bM1344o5 = c0043n.readByte();
                        byte bM1344o6 = c0043n.readByte();
                        byte b = (iM1354v4 % 4 != 0 || iM1354v4 == 2000) ? (byte) 28 : (byte) 29;
                        int i7 = (((((iM1354v4 - 1970) * 365) + ((iM1354v4 - 1968) / 4)) + bM1344o4) + 28) - b;
                        if (iM1354v4 >= 2000) {
                            i7--;
                        }
                        byte[] bArrM581a = AppState.getBytes(945);
                        int i8 = 0;
                        while (i8 < bM1344o3 - 1) {
                            i7 += i8 == 1 ? b : bArrM581a[i8];
                            i8++;
                        }
                        long j = 1000 * ((86400 * i7) + (bM1344o5 * 3600) + (bM1344o6 * 60));
                        c0043n.readShortBE();
                        if (resultCode != 1004) {
                            c0033d.onMessage(Integer.toString(resultCode), j, c0043n.readModifiedStr());
                        }
                        z = false;
                        break;
                    case 66:
                        AppState.setInt(1449, 1);
                        c0033d.trySendData(AppController.createMmpCommand(c0033d, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(c0033d.serverId).writeShortLE(62).writeShortBE(0)));
                        break;
                    default:
                        z = false;
                        break;
                }
                z3 = z;
                break;
            case 9:
                Vector searchResults = AppState.getVector(1317);
                c0043n.skip(10);
                if (c0043n.readShortLE() == 2010) {
                    c0043n.readShortLE();
                    int iM1354v5 = c0043n.readShortLE();
                    if ((420 == iM1354v5 || 430 == iM1354v5) && c0043n.readByte() == 10) {
                        c0043n.readShortBE();
                        ContactInfo searchResult = ContactInfo.createAccountInfo(c0033d).setMmpContactId(c0043n.readInt()).setDisplayName(c0043n.readPascalStr()).setFirstName(c0043n.readPascalStr()).setLastName(c0043n.readPascalStr()).setEmailAddress(c0043n.readPascalStr());
                        c0043n.readByte();
                        searchResults.addElement(searchResult.setMmpTypeId(c0043n.readShortLE()).setMaritalStatus(c0043n.readByte()).setAge(c0043n.readShortLE()));
                    }
                    if (iM1354v5 == 430) {
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
                int iM1353u14 = c0043n.readShortBE();
                if (iM1353u14 == 0) {
                    MmpContact mmpContact3 = (MmpContact) objArr[2];
                    MmpContactGroup srcGroup4 = (MmpContactGroup) objArr[3];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4873, srcGroup4.createUpdatePacket(srcGroup4.name, mmpContact3.userId, -1)), ResourceManager.integerOf(11), mmpContact3, srcGroup4, objArr[4]}));
                } else {
                    IOUtils.postRenameError(objArr, iM1353u14);
                }
                z = true;
                z3 = z;
                break;
            case 11:
                int iM1353u15 = c0043n.readShortBE();
                if (iM1353u15 == 0) {
                    MmpContact mmpContact4 = (MmpContact) objArr[2];
                    Object obj4 = objArr[3];
                    MmpContactGroup destGroup5 = (MmpContactGroup) objArr[4];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4872, mmpContact4.encodeContactUpdate(4, mmpContact4.displayName, destGroup5.groupId)), ResourceManager.integerOf(12), mmpContact4, obj4, destGroup5}));
                } else {
                    IOUtils.postRenameError(objArr, iM1353u15);
                }
                z = true;
                z3 = z;
                break;
            case 12:
                int iM1353u16 = c0043n.readShortBE();
                if (iM1353u16 == 0) {
                    MmpContact mmpContact5 = (MmpContact) objArr[2];
                    Object obj5 = objArr[3];
                    MmpContactGroup destGroup6 = (MmpContactGroup) objArr[4];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4873, destGroup6.createUpdatePacket(destGroup6.name, -1, mmpContact5.userId)), ResourceManager.integerOf(13), mmpContact5, obj5, destGroup6}));
                } else {
                    IOUtils.postRenameError(objArr, iM1353u16);
                }
                z = true;
                z3 = z;
                break;
            case 13:
                int iM1353u17 = c0043n.readShortBE();
                if (iM1353u17 == 0) {
                    MmpContactGroup srcGroup7 = (MmpContactGroup) objArr[3];
                    MmpContact mmpContact6 = (MmpContact) objArr[2];
                    srcGroup7.removeElement(mmpContact6);
                    MmpContactGroup destGroup8 = (MmpContactGroup) objArr[4];
                    destGroup8.addContact((Object) mmpContact6);
                    mmpContact6.onlineSemaphore = destGroup8.groupId;
                    c0033d.trySendData(ResourceManager.createSyncContactsCmd(c0033d));
                } else {
                    IOUtils.postRenameError(objArr, iM1353u17);
                }
                z = true;
                z3 = z;
                break;
            case 14:
                int iM1353u18 = c0043n.readShortBE();
                if (iM1353u18 == 0) {
                    MmpContactGroup destGroup9 = (MmpContactGroup) objArr[4];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4873, destGroup9.createUpdatePacket(destGroup9.name, -1, ((Integer) objArr[5]).intValue())), ResourceManager.integerOf(15), objArr[2], objArr[3], destGroup9, objArr[5], objArr[6]}));
                } else {
                    IOUtils.postRenameError(objArr, iM1353u18);
                }
                z = true;
                z3 = z;
                break;
            case 15:
                int iM1353u19 = c0043n.readShortBE();
                if (iM1353u19 == 0) {
                    MmpContactGroup destGroup10 = (MmpContactGroup) objArr[4];
                    MmpContact mmpContact7 = new MmpContact(c0033d, ((Integer) objArr[5]).intValue(), destGroup10.groupId, (String) objArr[2], (String) objArr[3], true);
                    destGroup10.addContact((Object) mmpContact7);
                    c0033d.trySendData(ResourceManager.createSyncContactsCmd(c0033d));
                    c0033d.trySendData(ResourceManager.createGetContactsCmd(c0033d));
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4873, mmpContact7.encodeContactUpdate(5, mmpContact7.displayName, mmpContact7.onlineSemaphore)), ResourceManager.integerOf(16), objArr[2], objArr[3], objArr[4], objArr[5], objArr[6], mmpContact7}));
                    c0033d.trySendData(ResourceManager.createSyncContactsCmd(c0033d));
                } else {
                    IOUtils.postRenameError(objArr, iM1353u19);
                }
                z = true;
                z3 = z;
                break;
            case 16:
                int iM1353u20 = c0043n.readShortBE();
                if (iM1353u20 == 0) {
                    c0033d.trySendData(ResourceManager.createSyncContactsCmd(c0033d));
                    c0033d.trySendData(IOUtils.createSendMessageCmd(c0033d, (MmpContact) objArr[7], (String) objArr[6]));
                } else {
                    IOUtils.postRenameError(objArr, iM1353u20);
                }
                z = true;
                z3 = z;
                break;
            case 17:
                c0033d.lastError = c0033d.configFlags & 65535;
                z3 = z;
                break;
            case 18:
                int iM1353u21 = c0043n.readShortBE();
                if (iM1353u21 == 0) {
                    ((MmpContact) objArr[2]).updatePermissionFlags(((Integer) objArr[3]).intValue(), ((Integer) objArr[4]).intValue());
                } else {
                    IOUtils.postRenameError(objArr, iM1353u21);
                }
                z = true;
                z3 = z;
                break;
            case 19:
                int status22 = c0043n.readShortBE();
                if (status22 == 0) {
                    ((MmpContact) objArr[2]).updatePermissionFlags(((Integer) objArr[3]).intValue(), 0);
                } else {
                    IOUtils.postRenameError(objArr, status22);
                }
                z = true;
                z3 = z;
                break;
            case 20:
                int status23 = c0043n.readShortBE();
                if (status23 != 0) {
                    IOUtils.postOperationError(status23);
                }
                z = true;
                z3 = z;
                break;
            case 21:
                c0043n.skip(10);
                if (c0043n.readShortLE() == 2010) {
                    c0043n.readShortLE();
                    int iM1354v6 = c0043n.readShortLE();
                    if ((420 == iM1354v6 || 430 == iM1354v6) && c0043n.readByte() == 10) {
                        c0043n.readShortBE();
                        ContactInfo searchResult2 = ContactInfo.createAccountInfo(c0033d).setMmpContactId(c0043n.readInt()).setDisplayName(c0043n.readPascalStr()).setFirstName(c0043n.readPascalStr()).setLastName(c0043n.readPascalStr()).setEmailAddress(c0043n.readPascalStr());
                        c0043n.readByte();
                        ContactInfo searchResult3 = searchResult2.setMmpTypeId(c0043n.readShortLE()).setMaritalStatus(c0043n.readByte()).setAge(c0043n.readShortLE());
                        MmpContact mmpContact8 = (MmpContact) c0033d.contactMap.get(searchResult3.getString(60));
                        if (null != mmpContact8) {
                            mmpContact8.setDisplayName(searchResult3.getDisplayNameOrId());
                        }
                    }
                    z = iM1354v6 == 430;
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
    public static final void handleMrimResponse(MrimAccount c0028ba, ByteBuffer c0043n, int i) {
        Object[] objArr;
        int resultCode = c0043n.readInt();
        Vector vector = c0028ba.extras;
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
                    c0028ba.removeContact((Contact) objArr[2], true);
                    break;
                }
            case 3:
                if (resultCode != 0) {
                    IOUtils.postDeleteError(objArr, resultCode);
                    break;
                } else {
                    MrimContactGroup mrimGroup = (MrimContactGroup) objArr[2];
                    int i2 = mrimGroup.groupId >> 24;
                    int size2 = c0028ba.groups.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            c0028ba.removeGroup((ContactGroup) mrimGroup);
                            break;
                        } else {
                            MrimContactGroup mrimGroup2 = (MrimContactGroup) c0028ba.getGroup(size2);
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
                    c0028ba.groups.addElement(new MrimContactGroup(c0028ba, c0028ba.findAvailableGroupId(), ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    break;
                }
            case 5:
                if (resultCode != 0) {
                    IOUtils.postAddGroupError(objArr, resultCode);
                    break;
                } else {
                    MrimContactGroup mrimGroup3 = (MrimContactGroup) objArr[4];
                    int contactId2 = c0043n.readInt();
                    String strM584b = AppState.getString(1233);
                    String str = (String) objArr[2];
                    String str2 = (String) objArr[3];
                    String str3 = AppState.emptyStr;
                    mrimGroup3.addContact((Object) new MrimContact(c0028ba, contactId2, 1048576, 103, strM584b, str, 0, 1, str2, str3, str3));
                    break;
                }
            case 6:
                if (resultCode != 1) {
                    IOUtils.postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(922)).append(objArr[2]).append(AppState.getString(457)).append(resultCode)));
                    break;
                }
                break;
            case 7:
                NetworkUtils.createSingleContact(c0028ba, resultCode, c0043n);
                break;
            case 8:
                NetworkUtils.parseContactInfoResponse(c0028ba, resultCode, c0043n);
                break;
            case 9:
                if (resultCode != 0) {
                    if (resultCode != 5) {
                        IOUtils.postAddGroupError(objArr, resultCode);
                        break;
                    }
                } else {
                    MrimContactGroup mrimGroup4 = (MrimContactGroup) objArr[4];
                    int contactId3 = c0043n.readInt();
                    int iIntValue = ((Integer) objArr[5]).intValue();
                    int i3 = mrimGroup4.serverId;
                    String str4 = (String) objArr[2];
                    String str5 = (String) objArr[3];
                    String str6 = AppState.emptyStr;
                    mrimGroup4.addContact((Object) new MrimContact(c0028ba, contactId3, iIntValue, i3, str4, str5, 1, 0, str6, str6, str6));
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
                    int size3 = c0028ba.groups.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            mrimGroup5.addContact((Object) mrimContact2);
                            break;
                        } else {
                            c0028ba.getGroup(size3).removeElement(mrimContact2);
                        }
                    }
                }
            case 13:
                NetworkUtils.updateContactName(c0028ba, resultCode, c0043n);
                break;
            case 15:
                if (resultCode != 0) {
                    IOUtils.postAddGroupError(objArr, resultCode);
                    break;
                } else {
                    MrimContactGroup defaultGroup = c0028ba.getFirstContactGroup();
                    int contactId4 = c0043n.readInt();
                    int i4 = defaultGroup.serverId;
                    String strM1334g = c0043n.readWideStr();
                    String str7 = (String) objArr[2];
                    String str8 = AppState.emptyStr;
                    defaultGroup.addContact(new MrimContact(c0028ba, contactId4, 128, i4, strM1334g, str7, 0, 1, str8, str8, str8));
                    break;
                }
            case 16:
                NetworkUtils.addContactToGroup(c0028ba, resultCode, c0043n);
                break;
            case 17:
                ResourceManager.handleAuthResponse(c0028ba, resultCode, objArr, c0043n);
                break;
        }
        vector.removeElementAt(size);
    }
}
