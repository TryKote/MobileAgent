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
    public static ListItem f257f;

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
        int iMo108h = super.getIconId();
        int i = iMo108h & 65535;
        return (i < 381 || i > 384) ? iMo108h : iMo108h + 4;
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
    private static final int m837y() {
        Account abstractC0037hM616i = AppState.getAccount();
        return null != abstractC0037hM616i ? abstractC0037hM616i.getType() : AppState.getInt(1475);
    }

    /* renamed from: r */
    public static final void m838r() {
        if (m837y() == 1) {
            Account abstractC0037hM616i = AppState.getAccount();
            if (abstractC0037hM616i != null && abstractC0037hM616i.isConnecting()) {
                AppController.m340m(300);
                return;
            }
            m840t();
            if (abstractC0037hM616i != null) {
                AppState.setObject(1292, (Object) abstractC0037hM616i.login);
                AppState.setObject(1293, (Object) abstractC0037hM616i.password);
            }
            ScreenManager.m71b(ScreenManager.m75b(2803));
            return;
        }
        if (m837y() == 2) {
            XmppProtocol c0005ae = (XmppProtocol) AppState.getAccount();
            if (c0005ae != null && c0005ae.isConnecting()) {
                AppController.m340m(300);
                return;
            }
            m840t();
            AppState.setInt(1474, 0);
            if (c0005ae != null) {
                String strM13b = c0005ae.login;
                Vector vectorM516c = Utils.m516c(AppState.getString(696), (char) 0);
                int iM541c = Utils.m541c(vectorM516c);
                while (true) {
                    iM541c--;
                    if (iM541c < 1) {
                        break;
                    }
                    int iIndexOf = strM13b.indexOf((String) vectorM516c.elementAt(iM541c));
                    if (iIndexOf >= 0) {
                        strM13b = StringUtils.prefix(strM13b, iIndexOf);
                        break;
                    }
                }
                AppState.setInt(1474, iM541c);
                AppState.setObject(1292, (Object) strM13b);
                AppState.setObject(1293, (Object) c0005ae.password);
                AppState.setObject(1297, (Object) c0005ae.displayName);
            }
            ScreenManager.m71b(ScreenManager.m75b(3443));
            return;
        }
        if (m837y() == 3) {
            XmppMailRuProtocol c0031bd = (XmppMailRuProtocol) AppState.getAccount();
            if (c0031bd != null && c0031bd.isConnecting()) {
                AppController.m340m(300);
                return;
            }
            m840t();
            if (c0031bd != null) {
                AppState.setObject(1292, (Object) c0031bd.login);
                AppState.setObject(1293, (Object) c0031bd.password);
                AppState.setObject(1297, (Object) c0031bd.displayName);
            }
            ScreenManager.m71b(ScreenManager.m75b(3463));
            return;
        }
        m840t();
        AppState.setInt(1474, 0);
        Account abstractC0037hM616i2 = AppState.getAccount();
        if (null != abstractC0037hM616i2) {
            AppState.setObject(1293, (Object) abstractC0037hM616i2.password);
            String str = abstractC0037hM616i2.login;
            Vector vectorM516c2 = Utils.m516c(AppState.getString(694), (char) 0);
            int size = vectorM516c2.size();
            int i = 0;
            while (true) {
                if (i > size) {
                    break;
                }
                if (i == size) {
                    AppState.setObject(1292, (Object) str);
                    break;
                }
                int iIndexOf2 = str.indexOf((String) vectorM516c2.elementAt(i));
                if (iIndexOf2 >= 0) {
                    AppState.setInt(1474, i);
                    AppState.setObject(1292, (Object) StringUtils.prefix(str, iIndexOf2));
                    break;
                }
                i++;
            }
        }
        ScreenManager.m71b(ScreenManager.m75b(2777));
    }

    /* renamed from: s */
    public static final int m839s() {
        NetworkUtils.m1195d();
        if (m837y() == 1) {
            Account abstractC0037hM616i = AppState.getAccount();
            String strM843u = m843u();
            int iM437a = AppController.m437a(1, abstractC0037hM616i, strM843u, Utils.defaultStr(AppState.getString(1293)));
            if (0 != iM437a) {
                return AppController.m338l(iM437a);
            }
            AppController.m328a(AppController.m438b(1, strM843u));
            return 0;
        }
        if (m837y() == 2) {
            return IOUtils.m820c(2);
        }
        if (m837y() == 3) {
            AppState.setInt(1474, 0);
            return IOUtils.m820c(3);
        }
        String strM522f = Utils.defaultStr(AppState.getString(1293));
        String strM843u2 = m843u();
        String strM9b = strM843u2;
        if (StringUtils.isEmpty(strM843u2)) {
            return AppController.m338l(301);
        }
        if (!m842c(strM9b, 694) && !m842c(strM9b, 695)) {
            strM9b = StringUtils.concat(strM9b, Utils.m542c(694, AppState.getInt(1474)));
        }
        if (!m844g(strM9b)) {
            return AppController.m338l(559);
        }
        int iM437a2 = AppController.m437a(0, AppState.getAccount(), strM9b, strM522f);
        if (0 != iM437a2) {
            return AppController.m338l(iM437a2);
        }
        AppController.m328a(AppController.m438b(0, strM9b));
        return 0;
    }

    /* renamed from: t */
    public static final void m840t() {
        AppState.clearRange(1292, 1293);
        AppState.clearIndex(1297);
    }

    /* renamed from: f */
    public static final boolean m841f(String str) {
        return m842c(str, 694);
    }

    /* renamed from: c */
    private static final boolean m842c(String str, int i) {
        Vector vectorM516c = Utils.m516c(AppState.getString(i), (char) 0);
        int size = vectorM516c.size();
        do {
            size--;
            if (size < 0) {
                NetworkUtils.releaseVector(vectorM516c);
                return false;
            }
        } while (str.indexOf((String) vectorM516c.elementAt(size)) < 0);
        return true;
    }

    /* renamed from: u */
    public static final String m843u() {
        return StringUtils.intern(Utils.defaultStr(AppState.getString(1292)).toLowerCase());
    }

    /* renamed from: g */
    public static final boolean m844g(String str) {
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
    public static final void m845v() {
        int size = 0;
        String[] strArrM10a = StringUtils.m10a();
        if (strArrM10a != null) {
            int length = strArrM10a.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                String str = strArrM10a[length];
                if (str.startsWith(AppState.getString(332005))) {
                    RecordStore recordStore = null;
                    try {
                        RecordStore recordStoreM767a = IOUtils.openRecordStore(str, false);
                        recordStore = recordStoreM767a;
                        size += recordStoreM767a.getSize();
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
    private static void m846a(ResourceManager c0034e, byte[] bArr, int i, int i2) {
        if (c0034e == null || bArr == null) {
            return;
        }
        String strM850c = m850c(c0034e);
        ByteBuffer c0043nM1303a = new ByteBuffer().writeStringLatin1(c0034e.f285e).writeLong(System.currentTimeMillis()).writeBytesAt(bArr, 0, i2);
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
                    RecordStore recordStoreM767a = IOUtils.openRecordStore(strM850c, true);
                    byte[] bArr2 = c0043nM1303a.data;
                    int i4 = c0043nM1303a.offset;
                    int i5 = c0043nM1303a.length;
                    recordStoreM767a.addRecord(bArr2, i4, i5);
                    AppState.setInt(1552, AppState.getInt(1552) + i5);
                    c0043nM1303a.clear();
                    IOUtils.closeRecordStore(recordStoreM767a);
                    return;
                } catch (Throwable unused) {
                    m849S();
                    IOUtils.closeRecordStore((RecordStore) null);
                }
            } catch (Throwable th) {
                IOUtils.closeRecordStore((RecordStore) null);
                throw th;
            }
        }
    }

    /* renamed from: a */
    public static final Image m847a(ResourceManager c0034e) {
        String strM850c = m850c(c0034e);
        RecordStore recordStore = null;
        try {
            String str = c0034e.f285e;
            RecordStore recordStoreM767a = IOUtils.openRecordStore(strM850c, false);
            recordStore = recordStoreM767a;
            int numRecords = recordStoreM767a.getNumRecords();
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
    private static final String m848z() {
        String str = null;
        long j = 0;
        String[] strArrM10a = StringUtils.m10a();
        if (strArrM10a != null) {
            String strM584b = AppState.getString(332005);
            int length = strArrM10a.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                String str2 = strArrM10a[length];
                if (str2.startsWith(strM584b)) {
                    RecordStore recordStoreM767a = null;
                    try {
                        recordStoreM767a = IOUtils.openRecordStore(str2, false);
                        long j2 = j;
                        long lastModified = recordStoreM767a.getLastModified();
                        if (j2 > j2 || j == 0) {
                            j = lastModified;
                            str = str2;
                        }
                        IOUtils.closeRecordStore(recordStoreM767a);
                    } catch (RuntimeException th) {
                        IOUtils.closeRecordStore(recordStoreM767a);
                        throw th;
                    } catch (Throwable th) {
                        IOUtils.closeRecordStore(recordStoreM767a);
                        throw new RuntimeException(th);
                    }
                }
            }
        }
        return str;
    }

    /* renamed from: S */
    private static final void m849S() {
        String strM848z = m848z();
        if (strM848z != null) {
            RecordStore recordStore = null;
            try {
                RecordStore recordStoreM767a = IOUtils.openRecordStore(strM848z, false);
                recordStore = recordStoreM767a;
                int numRecords = recordStoreM767a.getNumRecords();
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
    private static final String m850c(ResourceManager c0034e) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(332005)).append(c0034e.f281a).append('z').append(c0034e.f282b).append('x').append((c0034e.f283c / 4) << 2).append('y').append((c0034e.f284d / 4) << 2));
    }

    /* renamed from: h */
    public static final ByteBuffer m851h(String str) {
        ByteBuffer c0043n = new ByteBuffer();
        int i = 0;
        while (true) {
            RecordStore recordStore = null;
            try {
                int i2 = i;
                i++;
                RecordStore recordStoreM767a = IOUtils.openRecordStore(m856d(str, i2), false);
                recordStore = recordStoreM767a;
                byte[] record = recordStoreM767a.getRecord(1);
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
    public static final void m852a(String str, ByteBuffer c0043n, boolean z) {
        if (z) {
            m853a(str, c0043n);
            return;
        }
        int i = c0043n.length;
        if (i == 0) {
            String[] strArrM10a = StringUtils.m10a();
            int i2 = 0;
            while (true) {
                int i3 = i2;
                i2++;
                String strM856d = m856d(str, i3);
                if (!m855a(strArrM10a, strM856d)) {
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
                RecordStore recordStoreM767a = IOUtils.openRecordStore(str, true);
                recordStore = recordStoreM767a;
                if (recordStoreM767a.getNumRecords() == 0) {
                    recordStore.addRecord(bArr, 0, i);
                } else {
                    recordStore.setRecord(1, bArr, 0, i);
                }
                String[] strArrM10a2 = StringUtils.m10a();
                int i4 = 0;
                while (true) {
                    i4++;
                    String strM856d2 = m856d(str, i4);
                    if (!m855a(strArrM10a2, strM856d2)) {
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
    public static final void m853a(String str, ByteBuffer c0043n) {
        int i = 0;
        int i2 = c0043n.length;
        if (i2 > 0) {
            byte[] bArr = c0043n.compact().data;
            int iM854a = 0;
            while (true) {
                int i3 = iM854a;
                if (i3 >= i2) {
                    break;
                }
                int i4 = i;
                i++;
                iM854a = i3 + m854a(m856d(str, i4), bArr, i3, i2 - i3);
            }
        }
        String[] strArrM10a = StringUtils.m10a();
        while (true) {
            int i5 = i;
            i++;
            String strM856d = m856d(str, i5);
            if (!m855a(strArrM10a, strM856d)) {
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
    private static final int m854a(String str, byte[] bArr, int i, int i2) {
        RecordStore recordStore = null;
        try {
            try {
                RecordStore.deleteRecordStore(str);
            } catch (Throwable unused) {
            }
            RecordStore recordStoreM767a = IOUtils.openRecordStore(str, true);
            recordStore = recordStoreM767a;
            int iM503b = Utils.min(i2, Utils.max(recordStore.getSizeAvailable() - 128, 2048));
            recordStoreM767a.addRecord(bArr, i, iM503b);
            IOUtils.closeRecordStore(recordStore);
            return iM503b;
        } catch (RuntimeException th) {
            IOUtils.closeRecordStore(recordStore);
            throw th;
        } catch (Throwable th) {
            IOUtils.closeRecordStore(recordStore);
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    private static final boolean m855a(String[] strArr, String str) {
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
    private static final String m856d(String str, int i) {
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
    public static final ByteBuffer m857a(MmpProtocol c0033d, int i) {
        return c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 4873, new ByteBuffer().writeIntLE(0).writeShortBE(i).writeShortBE(4).writeShortBE(5).writeShortBE(202).writeShortBE(1).writeByte(c0033d.getPendingVersion())), ResourceManager.m967e(20)});
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:30:0x00ce */
    /* renamed from: w */
    public static final void m858w() {
        ListItem interfaceC0044o;
        int i = 3072;
        if (AppController.m440S().size() > 0) {
            i = 11264;
        }
        if (f257f != null) {
            interfaceC0044o = f257f;
        } else {
            interfaceC0044o = MapRenderer.f203k;
            f257f = interfaceC0044o;
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
                ScreenManager.m71b(ScreenManager.m75b(1753));
                return;
            }
            int i5 = i2;
            i2++;
            AppState.setInt(i5, i & i4);
            i3 = i4 << 1;
        }
    }

    /* renamed from: h */
    public static final int m859h(int i) {
        long jMo274v;
        long jMo275w;
        ListItem interfaceC0044o = f257f;
        int iMo276r = interfaceC0044o == null ? 0 : interfaceC0044o.getHeight();
        switch (i) {
            case 0:
                AppState.setCurrentEntity(interfaceC0044o);
                ScreenBuilder.m549c();
                return 63;
            case 1:
                if (iMo276r == 8) {
                    AppState.setInt(4895, 0);
                    AppController.m393a((MrimAccount) null, ((UserSearchResult) interfaceC0044o).userId);
                } else {
                    AppState.setCurrentEntity(f257f);
                }
                ScreenBuilder.m549c();
                return 102;
            case 2:
                if (iMo276r == 3) {
                    AppState.setCurrentEntity(f257f);
                    ScreenBuilder.m549c();
                    return 85;
                }
                AppState.setCurrentEntity((Object) null);
                Vector vectorM440S = AppController.m440S();
                if (vectorM440S == null || vectorM440S.size() <= 0) {
                    return AppController.m338l(422);
                }
                ((MrimAccount) vectorM440S.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) interfaceC0044o).userId, 1));
                ScreenBuilder.m549c();
                return 85;
            case 3:
                Vector vectorM440S2 = AppController.m440S();
                if (vectorM440S2 == null || vectorM440S2.size() <= 0) {
                    return AppController.m338l(422);
                }
                ((MrimAccount) vectorM440S2.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) interfaceC0044o).userId, 2));
                ScreenBuilder.m549c();
                return 6;
            case 4:
                ScreenBuilder.m549c();
                AppState.pool[1255] = (Conversation) interfaceC0044o;
                return 170;
            case 5:
                ResourceManager.m932a(VCard.formatPhoneContactUrl((PhoneContact) interfaceC0044o, 0), (PhoneContact) interfaceC0044o, 0);
                return 12;
            case 6:
                AppState.setAccount(interfaceC0044o);
                ScreenBuilder.m549c();
                return 167;
            case 7:
                AppState.setAccount(interfaceC0044o);
                ScreenBuilder.m549c();
                return 151;
            case 8:
            case 9:
            default:
                return 6;
            case 10:
                if (MapRenderer.m656d()) {
                    MmpContact.clearRouteProgress();
                }
                ListItem interfaceC0044o2 = MapRenderer.f203k;
                if (interfaceC0044o2 == null || !interfaceC0044o2.isSelected()) {
                    jMo274v = MapRenderer.f196d;
                    jMo275w = MapRenderer.f195c;
                } else {
                    jMo274v = interfaceC0044o2.getWidth();
                    jMo275w = interfaceC0044o2.getBaseHeight();
                    interfaceC0044o2.select();
                }
                int[] iArr = {(int) jMo274v, (int) jMo275w};
                MmpContact.routePoints.addElement(iArr);
                MmpContact.nearestPoints.addElement(new Object[]{null, iArr});
                MapRenderer.f200h = true;
                if (!MapRenderer.m656d()) {
                    return 6;
                }
                Conversation.m1129c();
                return 6;
            case 11:
                if (MapRenderer.m656d()) {
                    MmpContact.clearRouteProgress();
                }
                if (MmpContact.mapDataCache != null) {
                    MmpContact.routePoints.removeElement((int[]) MmpContact.mapDataCache[1]);
                    MmpContact.nearestPoints.removeElement(MmpContact.mapDataCache);
                }
                AppState.setInt(1575, 0);
                AppState.setBool(1574, AppState.getBool(1573));
                MapRenderer.f200h = true;
                if (!MapRenderer.m656d()) {
                    return 6;
                }
                Conversation.m1129c();
                return 6;
            case 12:
                MmpContact.clearLocationData();
                MapRenderer.f200h = true;
                return 6;
            case 13:
                ListItem interfaceC0044o3 = MapRenderer.f203k;
                if (interfaceC0044o3 != null && interfaceC0044o3.isSelected()) {
                    interfaceC0044o3.select();
                }
                MapRenderer.f200h = true;
                return 6;
            case 14:
                ConnectionThread.m1170l();
                if (MmpContact.hasSecondToken()) {
                    return 6;
                }
                AppState.setInt(1442, 1);
                return 158;
            case 15:
                ConnectionThread.m1171m();
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
                ScreenBuilder.m549c();
                Vector vectorM440S3 = AppController.m440S();
                if (vectorM440S3.size() > 1) {
                    return 172;
                }
                AppState.setAccount(vectorM440S3.elementAt(0));
                return 173;
            case 19:
                return 110;
            case 20:
                ConnectionThread.m1169a((MapPoint) f257f);
                return 6;
            case 21:
                Conversation.m1127a();
                return 6;
            case 22:
                Conversation.m1128b();
                return 6;
        }
    }

    /* renamed from: a */
    public static final void m860a(Object[] objArr) {
        try {
            String str = ((XmppProtocol) objArr[0]).login;
            String strM861j = m861j(StringUtils.m7b(1185660, StringUtils.suffix(str, str.indexOf(64) + 1)));
            if (strM861j == null || strM861j.indexOf(58) <= 0) {
                XmppProtocol c0005ae = (XmppProtocol) objArr[0];
                c0005ae.setAuthParameters(c0005ae.mo84j(), 5222);
            } else {
                Vector vectorM516c = Utils.m516c(strM861j, ':');
                ((XmppProtocol) objArr[0]).setAuthParameters(Utils.m521a(vectorM516c, 0), Integer.parseInt(Utils.m521a(vectorM516c, 1)));
                NetworkUtils.releaseVector(vectorM516c);
            }
        } catch (Throwable th) {
            ((XmppProtocol) objArr[0]).setException(th);
        }
    }

    /* renamed from: j */
    private static final String m861j(String str) {
        String strM1317c;
        DatagramConnection datagramConnection = null;
        try {
            AppController.m343s();
            Vector vectorM516c = Utils.m516c(str, '.');
            ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(792490);
            for (int i = 0; i < Utils.m541c(vectorM516c); i++) {
                c0043nM1310c.writeByteLenStr(Utils.m521a(vectorM516c, i));
            }
            NetworkUtils.releaseVector(vectorM516c);
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
                    int iM1346q = c0043nM1304b.readUByte();
                    int i2 = iM1346q;
                    if (iM1346q == 0) {
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
                int iM1346q2 = c0043nM1304b.readUByte();
                while (true) {
                    iM1346q2--;
                    if (iM1346q2 < 0) {
                        int iM1346q3 = c0043nM1304b.readUByte();
                        iM1346q2 = iM1346q3;
                        if (iM1346q3 == 0) {
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
            AppController.m344t();
            return str2;
        } catch (RuntimeException th) {
            IOUtils.closeConn((Connection) datagramConnection);
            AppController.m344t();
            throw th;
        } catch (Throwable th) {
            IOUtils.closeConn((Connection) datagramConnection);
            AppController.m344t();
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    public static final void m862a(Vector vector, ByteBuffer c0043n) {
        int iM541c = Utils.m541c(vector);
        c0043n.writeIntLE(iM541c);
        for (int i = 0; i < iM541c; i++) {
            String[] strArr = (String[]) vector.elementAt(i);
            c0043n.writeStringUTF16(strArr[0]).writeStringUTF16(strArr[1]);
        }
    }

    /* renamed from: e */
    public static final Vector m863e(ByteBuffer c0043n) {
        Vector vectorM1213g = NetworkUtils.newVector();
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return vectorM1213g;
            }
            vectorM1213g.addElement(new String[]{c0043n.readUTF8Str((String) null), c0043n.readUTF8Str((String) null)});
        }
    }

    /* renamed from: a */
    public static final Vector m864a(Vector vector) {
        Vector vectorM1213g = NetworkUtils.newVector();
        for (int i = 0; i < Utils.m541c(vector); i++) {
            vectorM1213g.addElement(vector.elementAt(i));
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public static final Vector m865a(Vector vector, Vector vector2) {
        if (vector2 != null) {
            Enumeration enumerationElements = vector2.elements();
            while (enumerationElements.hasMoreElements()) {
                m867a(vector, (String[]) enumerationElements.nextElement());
            }
        }
        return vector;
    }

    /* renamed from: b */
    public static final Vector m866b(Vector vector) {
        Vector vectorM1213g = NetworkUtils.newVector();
        if (Utils.m541c(vector) > 0) {
            vectorM1213g.addElement(vector.elementAt(0));
        }
        return vectorM1213g;
    }

    /* renamed from: a */
    public static final Vector m867a(Vector vector, String[] strArr) {
        String str = strArr[0];
        if (str.indexOf(64) != -1) {
            boolean z = false;
            int iM541c = Utils.m541c(vector);
            while (true) {
                iM541c--;
                if (iM541c < 0) {
                    break;
                }
                if (StringUtils.equals(str, ((String[]) vector.elementAt(iM541c))[0])) {
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
    public static final Vector m868b(String str, String str2) {
        Vector vectorM1213g = NetworkUtils.newVector();
        Vector vectorM870k = m870k(Conversation.m1122j(str2));
        Vector vectorM870k2 = m870k(str);
        for (int i = 0; i < Utils.m541c(vectorM870k2); i++) {
            m867a(vectorM1213g, AppController.m459a((String) vectorM870k2.elementAt(i), (String) vectorM870k.elementAt(i)));
        }
        NetworkUtils.releaseVector(vectorM870k);
        NetworkUtils.releaseVector(vectorM870k2);
        return vectorM1213g;
    }

    /* renamed from: c */
    public static final String[] m869c(Vector vector) {
        if (Utils.m541c(vector) > 0) {
            return (String[]) vector.elementAt(0);
        }
        return null;
    }

    /* renamed from: k */
    private static final Vector m870k(String str) {
        Vector vectorM1213g = NetworkUtils.newVector();
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = str.length();
        boolean z = true;
        int i = 0;
        while (i <= length) {
            char cCharAt = i < length ? str.charAt(i) : ',';
            if (!z) {
                z = true;
            } else if (cCharAt == ',') {
                vectorM1213g.addElement(NetworkUtils.bufToString(stringBufferM1217h, false));
                z = false;
            } else {
                stringBufferM1217h.append(cCharAt);
            }
            i++;
        }
        NetworkUtils.bufToStringCached(stringBufferM1217h);
        return vectorM1213g;
    }

    /* renamed from: i */
    public static final Vector m871i(String str) {
        Vector vectorM1213g = NetworkUtils.newVector();
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = str.length();
        int i = 0;
        while (i <= length) {
            char cCharAt = i == length ? ';' : str.charAt(i);
            char c = cCharAt;
            if (cCharAt != ';' && c != ',' && c != ' ') {
                stringBufferM1217h.append(c);
            } else if (stringBufferM1217h.length() > 0) {
                String strM1214a = NetworkUtils.bufToString(stringBufferM1217h, false);
                vectorM1213g.addElement(new String[]{strM1214a, strM1214a});
            }
            i++;
        }
        NetworkUtils.bufToStringCached(stringBufferM1217h);
        return vectorM1213g;
    }

    /* renamed from: b */
    public static final void m872b(int i, int i2) {
        AppState.setInt(1515, i);
        AppState.setInt(1516, i2);
    }

    /* renamed from: x */
    public static final int m873x() {
        String strM1215a;
        Object[] objArrM816k = IOUtils.m816k();
        if (objArrM816k == null) {
            return m874T();
        }
        Object[] objArrM1156c = ConnectionThread.m1156c(objArrM816k);
        if (objArrM1156c == null) {
            return 0;
        }
        int iM818c = IOUtils.m818c(objArrM1156c);
        if (iM818c != 0) {
            return iM818c;
        }
        String strM584b = AppState.getString(1346);
        ChatRoom c0052wM745h = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513));
        Message c0026azM1415b = c0052wM745h.getMessage(strM584b);
        boolean zM671a = c0026azM1415b.hasFlag(4);
        Object objM819l = IOUtils.m819l();
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
        c0026azM1415b.attachments = objArr;
        String str = (String) JsonParser.getValueByInt(objM819l, 919493);
        if (str == null) {
            strM1215a = AppState.emptyStr;
        } else {
            StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
            int length = str.length();
            int i2 = 0;
            while (i2 < length) {
                char cCharAt = str.charAt(i2);
                stringBufferM1217h.append(cCharAt);
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
            strM1215a = NetworkUtils.bufToStringCached(stringBufferM1217h);
        }
        c0026azM1415b.body = strM1215a;
        if (zM671a) {
            c0026azM1415b.setFlag(4, false);
            c0052wM745h.decrementUnread();
        }
        return m874T();
    }

    /* renamed from: T */
    private static final int m874T() {
        int iM586d = AppState.getInt(1515);
        if (iM586d == 54) {
            Message c0026azM1415b = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513)).getMessage(AppState.getString(1346));
            Vector vectorM668b = c0026azM1415b.getToList();
            Vector vectorM669c = c0026azM1415b.getCcList();
            m869c(vectorM668b);
            String strM673d = c0026azM1415b.getSubject();
            String str = c0026azM1415b.body;
            String strM584b = AppState.getString(198549);
            String strM584b2 = AppState.getString(198546);
            String string = new StringBuffer().append(AppState.getString(838)).append(Utils.m507d(str)).toString();
            switch (AppState.getInt(1516)) {
                case 0:
                    ResourceManager.m966a(m866b(vectorM668b), new StringBuffer().append(strM584b).append(strM673d).toString(), string);
                    break;
                case 1:
                    ResourceManager.m966a(m865a(m864a(vectorM668b), vectorM669c), new StringBuffer().append(strM584b).append(strM673d).toString(), string);
                    break;
                case 2:
                    ResourceManager.m966a(NetworkUtils.newVector(), new StringBuffer().append(strM584b2).append(strM673d).toString(), string);
                    break;
                case 3:
                    ResourceManager.m966a(m864a(vectorM669c), strM673d, str);
                    break;
            }
        }
        return iM586d;
    }

    /* renamed from: U */
    private static final boolean m875U() {
        try {
            AppController.m343s();
            NetworkUtils.m1184b(AppState.getObjectArray(1392));
            AppState.pool[1392] = NetworkUtils.m1186a(new ByteBuffer().writeCompressed(593549).writeCompressed(1511542).getStringAndClear(), false);
            return true;
        } catch (Throwable unused) {
            return false;
        } finally {
            AppController.m344t();
        }
    }

    /* JADX DEBUG: Finally have unexpected throw blocks count: 2, expect 1 */
    /* renamed from: b */
    public static final Image m876b(ResourceManager c0034e) throws IOException {
        ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(2232520).writeRawString(c0034e.f285e).writeCompressed(3870861).writeExtendedInt(2950495).writeEncodedInt(222).writeCompressed(6689002);
        try {
            Object[] objArrM609l = AppState.getObjectArray(1392);
            byte[] bArr = c0043nM1310c.data;
            int i = c0043nM1310c.length;
            NetworkUtils.m1189a(objArrM609l, bArr, i);
            AppController.m425F(i);
        } catch (Throwable unused) {
            if (!m875U()) {
                throw new IOException();
            }
            Object[] objArrM609l2 = AppState.getObjectArray(1392);
            byte[] bArr2 = c0043nM1310c.data;
            int i2 = c0043nM1310c.length;
            NetworkUtils.m1189a(objArrM609l2, bArr2, i2);
            AppController.m425F(i2);
        } finally {
            c0043nM1310c.clear();
        }
        String strM877V = m877V();
        if (strM877V == null) {
            NetworkUtils.m1184b(AppState.getObjectArray(1392));
            throw new IOException();
        }
        AppState.addInt(1548, strM877V.getBytes().length);
        if (m879l(strM877V) != 200) {
            int iM880m = m880m(strM877V);
            try {
                if (iM880m > 0) {
                    ((InputStream) AppState.getObjectArray(1392)[1]).skip(iM880m);
                } else {
                    NetworkUtils.m1184b(AppState.getObjectArray(1392));
                }
                return null;
            } catch (Throwable unused2) {
                return null;
            }
        }
        ByteBuffer c0043nM878i = m878i(m880m(strM877V));
        if (c0043nM878i == null) {
            NetworkUtils.m1184b(AppState.getObjectArray(1392));
            throw new IOException();
        }
        AppState.addInt(1548, c0043nM878i.length);
        byte[] bArr3 = c0043nM878i.data;
        int i3 = c0043nM878i.length;
        if (AppState.getBool(1551)) {
            m846a(c0034e, bArr3, 0, i3);
        }
        AppController.m424E(c0043nM878i.length + 255);
        return c0043nM878i.toImage();
    }

    /* renamed from: V */
    private static final String m877V() {
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
    private static final ByteBuffer m878i(int i) {
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
                iM1190a = NetworkUtils.m1190a(objArrM609l, bArrM1211a, 0, Utils.min(length, i - i2));
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
    private static final int m879l(String str) {
        try {
            return Integer.parseInt(StringUtils.substring(str, 9, 12));
        } catch (Throwable unused) {
            return 0;
        }
    }

    /* renamed from: m */
    private static final int m880m(String str) {
        try {
            int iM628b = AppState.indexOfPool(StringUtils.intern(str.toLowerCase()), 1052310) + 16;
            return Integer.parseInt(StringUtils.substring(str, iM628b, str.indexOf(13, iM628b)));
        } catch (Throwable unused) {
            return -1;
        }
    }

    /* renamed from: b */
    public static final void m881b(MmpProtocol c0033d, int i) {
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
    public static final void m882a(MmpProtocol c0033d, ByteBuffer c0043n, int i, int i2) {
        Object[] objArr;
        boolean z = false;
        boolean z2;
        MmpContactGroup c0016ap;
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
                    IOUtils.m779a(objArr, iM1353u);
                }
                z = true;
                z3 = z;
                break;
            case 1:
                int iM1353u2 = c0043n.readShortBE();
                if (iM1353u2 == 0) {
                    ((MmpContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                } else {
                    IOUtils.m779a(objArr, iM1353u2);
                }
                z = true;
                z3 = z;
                break;
            case 2:
                int iM1353u3 = c0043n.readShortBE();
                if (iM1353u3 == 0) {
                    c0033d.removeGroup((ContactGroup) objArr[2]);
                    c0033d.trySendData(ResourceManager.m963c(c0033d));
                } else {
                    IOUtils.m781c(objArr, iM1353u3);
                }
                z = true;
                z3 = z;
                break;
            case 3:
                int iM1353u4 = c0043n.readShortBE();
                if (iM1353u4 == 0) {
                    c0033d.trySendData(ResourceManager.m962b(c0033d));
                } else {
                    IOUtils.m782b(iM1353u4);
                }
                z = true;
                z3 = z;
                break;
            case 4:
                int iM1353u5 = c0043n.readShortBE();
                if (iM1353u5 == 0) {
                    c0033d.addGroup(new MmpContactGroup(c0033d, ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    c0033d.trySendData(ResourceManager.m963c(c0033d));
                } else {
                    IOUtils.m780b(objArr, iM1353u5);
                }
                z = true;
                z3 = z;
                break;
            case 5:
                int iM1353u6 = c0043n.readShortBE();
                if (iM1353u6 == 0) {
                    c0033d.removeContact((Contact) objArr[2], true);
                    c0033d.trySendData(ResourceManager.m962b(c0033d));
                } else {
                    IOUtils.m781c(objArr, iM1353u6);
                }
                z = true;
                z3 = z;
                break;
            case 6:
                boolean z4 = (i2 & 1) == 0;
                c0043n.skip(1);
                Vector vectorM1213g = NetworkUtils.newVector();
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
                            vectorM1213g.addElement(new MmpContact(c0033d, iM1353u9, iM1353u8, strM1364A, strM1364A2, z5));
                            continue;
                        case 1:
                            if (iM1353u8 != 0) {
                                c0033d.groups.addElement(new MmpContactGroup(c0033d, iM1353u8, strM1364A));
                            }
                            c0043n.skip(iM1353u11);
                            continue;
                        case 2:
                            c0033d.contactsByIdMap.put(strM1364A, ResourceManager.m967e(iM1353u9));
                            c0043n.skip(iM1353u11);
                            continue;
                        case 3:
                            c0033d.contactGroupsMap.put(strM1364A, ResourceManager.m967e(iM1353u9));
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
                            c0033d.additionalDataMap.put(strM1364A, ResourceManager.m967e(iM1353u9));
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
                int size2 = vectorM1213g.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        if (z4) {
                            c0033d.sendData(AppController.m464a(c0033d, 4871, (ByteBuffer) null));
                            int i4 = c0033d.groupSequenceId;
                            if (i4 != 0) {
                                c0033d.sendData(m857a(c0033d, i4));
                            }
                            c0033d.sendData(AppController.m464a(c0033d, 258, new ByteBuffer().writeCompressed(5245205)));
                            c0033d.sendData(c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(c0033d.serverId).writeShortLE(60).writeShortBE(0)), ResourceManager.m967e(8)}));
                            Enumeration enumerationElements = c0033d.contactMap.elements();
                            while (enumerationElements.hasMoreElements()) {
                                Hashtable hashtable = c0033d.contactsByIdMap;
                                MmpContact c0009ai = (MmpContact) enumerationElements.nextElement();
                                Object obj = hashtable.get(c0009ai.identifier);
                                if (null != obj) {
                                    c0009ai.canDelete = ((Integer) obj).intValue();
                                }
                                Object obj2 = c0033d.contactGroupsMap.get(c0009ai.identifier);
                                if (null != obj2) {
                                    c0009ai.canBlock = ((Integer) obj2).intValue();
                                }
                                Object obj3 = c0033d.additionalDataMap.get(c0009ai.identifier);
                                if (null != obj3) {
                                    c0009ai.canUnblock = ((Integer) obj3).intValue();
                                }
                            }
                            c0033d.contactsByIdMap.clear();
                            c0033d.contactGroupsMap.clear();
                            c0033d.additionalDataMap.clear();
                            if (c0033d.groups.size() == 0) {
                                c0033d.sendData(ResourceManager.m937a(c0033d, AppState.getString(459528)));
                            }
                            c0033d.progress = 100;
                            c0033d.msgCount = 100;
                        }
                        NetworkUtils.releaseVector(vectorM1213g);
                        z = z4;
                        z3 = z;
                        break;
                    } else {
                        MmpContact c0009ai2 = (MmpContact) vectorM1213g.elementAt(size2);
                        int i5 = c0009ai2.onlineSemaphore;
                        int size3 = c0033d.groups.size();
                        while (true) {
                            size3--;
                            if (size3 < 0) {
                                c0016ap = null;
                                break;
                            } else {
                                MmpContactGroup c0016ap2 = (MmpContactGroup) c0033d.getGroup(size3);
                                if (c0016ap2.groupId == i5) {
                                    c0016ap = c0016ap2;
                                    break;
                                }
                            }
                        }
                        MmpContactGroup c0016ap3 = c0016ap;
                        if (null != c0016ap) {
                            c0016ap3.addContact((Object) c0009ai2);
                        }
                    }
                }
            case 7:
                c0043n.skip(10);
                if (c0043n.readShortLE() == 2010) {
                    c0043n.readShortLE();
                    int iM1354v = c0043n.readShortLE();
                    c0043n.readByte();
                    ContactInfo c0042m = (ContactInfo) AppState.pool[1316];
                    ContactInfo c0042mM1254b = c0042m;
                    if (c0042m == null) {
                        c0042mM1254b = ContactInfo.m1254b(c0033d);
                    }
                    switch (iM1354v) {
                        case 200:
                            String strM1366C = c0043n.readPascalStr();
                            ContactInfo c0042mM1268i = c0042mM1254b.m1259b(strM1366C).m1260c(c0043n.readPascalStr()).m1261d(c0043n.readPascalStr()).m1262e(c0043n.readPascalStr()).m1268i(c0043n.readPascalStr());
                            c0043n.readPascalStr();
                            c0042mM1268i.m1266g(c0043n.readPascalStr()).m1269j(c0043n.readPascalStr()).m1270k(c0043n.readPascalStr()).m1271l(c0043n.readPascalStr());
                            if (c0033d.serverId == ((Integer) objArr[2]).intValue()) {
                                c0033d.setDisplayName(strM1366C);
                                break;
                            }
                            break;
                        case 220:
                            ContactInfo c0042mM1273n = c0042mM1254b.m1275b(c0043n.readShortLE()).m1277c(c0043n.readByte()).m1273n(c0043n.readPascalStr());
                            int iM1354v2 = c0043n.readShortLE();
                            byte bM1344o = c0043n.readByte();
                            byte bM1344o2 = c0043n.readByte();
                            if (bM1344o2 >= 0) {
                                c0042mM1273n.m1265f(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(Utils.zeroPad(bM1344o2 + 1)).append('/').append(Utils.zeroPad(bM1344o)).append('/').append(iM1354v2)));
                                break;
                            }
                            break;
                        case 230:
                            c0042mM1254b.m1272m(c0043n.readPascalStr());
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
                        int iM1328e = c0043n.readInt();
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
                        if (iM1328e != 1004) {
                            c0033d.onMessage(Integer.toString(iM1328e), j, c0043n.readModifiedStr());
                        }
                        z = false;
                        break;
                    case 66:
                        AppState.setInt(1449, 1);
                        c0033d.trySendData(AppController.m464a(c0033d, 5378, new ByteBuffer().writeShortBE(1).writeShortBE(10).writeShortLE(8).writeIntLE(c0033d.serverId).writeShortLE(62).writeShortBE(0)));
                        break;
                    default:
                        z = false;
                        break;
                }
                z3 = z;
                break;
            case 9:
                Vector vectorM614m = AppState.getVector(1317);
                c0043n.skip(10);
                if (c0043n.readShortLE() == 2010) {
                    c0043n.readShortLE();
                    int iM1354v5 = c0043n.readShortLE();
                    if ((420 == iM1354v5 || 430 == iM1354v5) && c0043n.readByte() == 10) {
                        c0043n.readShortBE();
                        ContactInfo c0042mM1262e = ContactInfo.m1254b(c0033d).m1283d(c0043n.readInt()).m1259b(c0043n.readPascalStr()).m1260c(c0043n.readPascalStr()).m1261d(c0043n.readPascalStr()).m1262e(c0043n.readPascalStr());
                        c0043n.readByte();
                        vectorM614m.addElement(c0042mM1262e.m1285e(c0043n.readShortLE()).m1277c(c0043n.readByte()).m1275b(c0043n.readShortLE()));
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
                    MmpContact c0009ai3 = (MmpContact) objArr[2];
                    MmpContactGroup c0016ap4 = (MmpContactGroup) objArr[3];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 4873, c0016ap4.createUpdatePacket(c0016ap4.name, c0009ai3.userId, -1)), ResourceManager.m967e(11), c0009ai3, c0016ap4, objArr[4]}));
                } else {
                    IOUtils.m779a(objArr, iM1353u14);
                }
                z = true;
                z3 = z;
                break;
            case 11:
                int iM1353u15 = c0043n.readShortBE();
                if (iM1353u15 == 0) {
                    MmpContact c0009ai4 = (MmpContact) objArr[2];
                    Object obj4 = objArr[3];
                    MmpContactGroup c0016ap5 = (MmpContactGroup) objArr[4];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 4872, c0009ai4.encodeContactUpdate(4, c0009ai4.displayName, c0016ap5.groupId)), ResourceManager.m967e(12), c0009ai4, obj4, c0016ap5}));
                } else {
                    IOUtils.m779a(objArr, iM1353u15);
                }
                z = true;
                z3 = z;
                break;
            case 12:
                int iM1353u16 = c0043n.readShortBE();
                if (iM1353u16 == 0) {
                    MmpContact c0009ai5 = (MmpContact) objArr[2];
                    Object obj5 = objArr[3];
                    MmpContactGroup c0016ap6 = (MmpContactGroup) objArr[4];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 4873, c0016ap6.createUpdatePacket(c0016ap6.name, -1, c0009ai5.userId)), ResourceManager.m967e(13), c0009ai5, obj5, c0016ap6}));
                } else {
                    IOUtils.m779a(objArr, iM1353u16);
                }
                z = true;
                z3 = z;
                break;
            case 13:
                int iM1353u17 = c0043n.readShortBE();
                if (iM1353u17 == 0) {
                    MmpContactGroup c0016ap7 = (MmpContactGroup) objArr[3];
                    MmpContact c0009ai6 = (MmpContact) objArr[2];
                    c0016ap7.removeElement(c0009ai6);
                    MmpContactGroup c0016ap8 = (MmpContactGroup) objArr[4];
                    c0016ap8.addContact((Object) c0009ai6);
                    c0009ai6.onlineSemaphore = c0016ap8.groupId;
                    c0033d.trySendData(ResourceManager.m962b(c0033d));
                } else {
                    IOUtils.m779a(objArr, iM1353u17);
                }
                z = true;
                z3 = z;
                break;
            case 14:
                int iM1353u18 = c0043n.readShortBE();
                if (iM1353u18 == 0) {
                    MmpContactGroup c0016ap9 = (MmpContactGroup) objArr[4];
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 4873, c0016ap9.createUpdatePacket(c0016ap9.name, -1, ((Integer) objArr[5]).intValue())), ResourceManager.m967e(15), objArr[2], objArr[3], c0016ap9, objArr[5], objArr[6]}));
                } else {
                    IOUtils.m779a(objArr, iM1353u18);
                }
                z = true;
                z3 = z;
                break;
            case 15:
                int iM1353u19 = c0043n.readShortBE();
                if (iM1353u19 == 0) {
                    MmpContactGroup c0016ap10 = (MmpContactGroup) objArr[4];
                    MmpContact c0009ai7 = new MmpContact(c0033d, ((Integer) objArr[5]).intValue(), c0016ap10.groupId, (String) objArr[2], (String) objArr[3], true);
                    c0016ap10.addContact((Object) c0009ai7);
                    c0033d.trySendData(ResourceManager.m962b(c0033d));
                    c0033d.trySendData(ResourceManager.m961a(c0033d));
                    c0033d.trySendData(c0033d.queueCommand(new Object[]{AppController.m464a(c0033d, 4873, c0009ai7.encodeContactUpdate(5, c0009ai7.displayName, c0009ai7.onlineSemaphore)), ResourceManager.m967e(16), objArr[2], objArr[3], objArr[4], objArr[5], objArr[6], c0009ai7}));
                    c0033d.trySendData(ResourceManager.m962b(c0033d));
                } else {
                    IOUtils.m779a(objArr, iM1353u19);
                }
                z = true;
                z3 = z;
                break;
            case 16:
                int iM1353u20 = c0043n.readShortBE();
                if (iM1353u20 == 0) {
                    c0033d.trySendData(ResourceManager.m962b(c0033d));
                    c0033d.trySendData(IOUtils.m753a(c0033d, (MmpContact) objArr[7], (String) objArr[6]));
                } else {
                    IOUtils.m779a(objArr, iM1353u20);
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
                    IOUtils.m779a(objArr, iM1353u21);
                }
                z = true;
                z3 = z;
                break;
            case 19:
                int iM1353u22 = c0043n.readShortBE();
                if (iM1353u22 == 0) {
                    ((MmpContact) objArr[2]).updatePermissionFlags(((Integer) objArr[3]).intValue(), 0);
                } else {
                    IOUtils.m779a(objArr, iM1353u22);
                }
                z = true;
                z3 = z;
                break;
            case 20:
                int iM1353u23 = c0043n.readShortBE();
                if (iM1353u23 != 0) {
                    IOUtils.m782b(iM1353u23);
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
                        ContactInfo c0042mM1262e2 = ContactInfo.m1254b(c0033d).m1283d(c0043n.readInt()).m1259b(c0043n.readPascalStr()).m1260c(c0043n.readPascalStr()).m1261d(c0043n.readPascalStr()).m1262e(c0043n.readPascalStr());
                        c0043n.readByte();
                        ContactInfo c0042mM1275b = c0042mM1262e2.m1285e(c0043n.readShortLE()).m1277c(c0043n.readByte()).m1275b(c0043n.readShortLE());
                        MmpContact c0009ai8 = (MmpContact) c0033d.contactMap.get(c0042mM1275b.m1256a(60));
                        if (null != c0009ai8) {
                            c0009ai8.setDisplayName(c0042mM1275b.m1291j());
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
    public static final void m883a(MrimAccount c0028ba, ByteBuffer c0043n, int i) {
        Object[] objArr;
        int iM1328e = c0043n.readInt();
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
                if (iM1328e != 0) {
                    IOUtils.m779a(objArr, iM1328e);
                    break;
                } else {
                    ((MrimContact) objArr[2]).updateDisplayNameAndGroups((String) objArr[3], (String) objArr[4]);
                    break;
                }
            case 1:
                if (iM1328e != 0) {
                    IOUtils.m779a(objArr, iM1328e);
                    break;
                } else {
                    ((MrimContactGroup) objArr[2]).setNameIfChanged((String) objArr[3]);
                    break;
                }
            case 2:
                if (iM1328e != 0) {
                    IOUtils.m781c(objArr, iM1328e);
                    break;
                } else {
                    c0028ba.removeContact((Contact) objArr[2], true);
                    break;
                }
            case 3:
                if (iM1328e != 0) {
                    IOUtils.m781c(objArr, iM1328e);
                    break;
                } else {
                    MrimContactGroup c0010aj = (MrimContactGroup) objArr[2];
                    int i2 = c0010aj.groupId >> 24;
                    int size2 = c0028ba.groups.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            c0028ba.removeGroup((ContactGroup) c0010aj);
                            break;
                        } else {
                            MrimContactGroup c0010aj2 = (MrimContactGroup) c0028ba.getGroup(size2);
                            if ((c0010aj2.groupId >> 24) > i2) {
                                c0010aj2.groupId -= 16777216;
                            }
                        }
                    }
                }
            case 4:
                if (iM1328e != 0) {
                    IOUtils.m780b(objArr, iM1328e);
                    break;
                } else {
                    c0028ba.groups.addElement(new MrimContactGroup(c0028ba, c0028ba.findAvailableGroupId(), ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    break;
                }
            case 5:
                if (iM1328e != 0) {
                    IOUtils.m780b(objArr, iM1328e);
                    break;
                } else {
                    MrimContactGroup c0010aj3 = (MrimContactGroup) objArr[4];
                    int iM1328e2 = c0043n.readInt();
                    String strM584b = AppState.getString(1233);
                    String str = (String) objArr[2];
                    String str2 = (String) objArr[3];
                    String str3 = AppState.emptyStr;
                    c0010aj3.addContact((Object) new MrimContact(c0028ba, iM1328e2, 1048576, 103, strM584b, str, 0, 1, str2, str3, str3));
                    break;
                }
            case 6:
                if (iM1328e != 1) {
                    IOUtils.m778d((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(922)).append(objArr[2]).append(AppState.getString(457)).append(iM1328e)));
                    break;
                }
                break;
            case 7:
                NetworkUtils.m1202a(c0028ba, iM1328e, c0043n);
                break;
            case 8:
                NetworkUtils.m1203b(c0028ba, iM1328e, c0043n);
                break;
            case 9:
                if (iM1328e != 0) {
                    if (iM1328e != 5) {
                        IOUtils.m780b(objArr, iM1328e);
                        break;
                    }
                } else {
                    MrimContactGroup c0010aj4 = (MrimContactGroup) objArr[4];
                    int iM1328e3 = c0043n.readInt();
                    int iIntValue = ((Integer) objArr[5]).intValue();
                    int i3 = c0010aj4.serverId;
                    String str4 = (String) objArr[2];
                    String str5 = (String) objArr[3];
                    String str6 = AppState.emptyStr;
                    c0010aj4.addContact((Object) new MrimContact(c0028ba, iM1328e3, iIntValue, i3, str4, str5, 1, 0, str6, str6, str6));
                    break;
                }
                break;
            case 10:
                MrimContact c0035f = (MrimContact) objArr[2];
                switch (iM1328e) {
                    case 0:
                        c0035f.updateMessageFlag(((Long) objArr[3]).longValue(), 64);
                        break;
                    case 32769:
                        if (c0035f.isSystem()) {
                            IOUtils.m778d((Object) AppState.getString(452));
                            break;
                        }
                    default:
                        IOUtils.m778d((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(453)).append(objArr[2]).append(AppState.getString(457)).append(iM1328e)));
                        break;
                }
            case 11:
                if (iM1328e != 0) {
                    IOUtils.m779a(objArr, iM1328e);
                    break;
                } else {
                    ((MrimContact) objArr[2]).statusFlags = ((Integer) objArr[3]).intValue();
                    break;
                }
            case 12:
                if (iM1328e != 0) {
                    IOUtils.m779a(objArr, iM1328e);
                    break;
                } else {
                    MrimContact c0035f2 = (MrimContact) objArr[2];
                    MrimContactGroup c0010aj5 = (MrimContactGroup) objArr[3];
                    c0035f2.groupId = c0010aj5.serverId;
                    int size3 = c0028ba.groups.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            c0010aj5.addContact((Object) c0035f2);
                            break;
                        } else {
                            c0028ba.getGroup(size3).removeElement(c0035f2);
                        }
                    }
                }
            case 13:
                NetworkUtils.m1204c(c0028ba, iM1328e, c0043n);
                break;
            case 15:
                if (iM1328e != 0) {
                    IOUtils.m780b(objArr, iM1328e);
                    break;
                } else {
                    MrimContactGroup c0010ajM718f = c0028ba.getFirstContactGroup();
                    int iM1328e4 = c0043n.readInt();
                    int i4 = c0010ajM718f.serverId;
                    String strM1334g = c0043n.readWideStr();
                    String str7 = (String) objArr[2];
                    String str8 = AppState.emptyStr;
                    c0010ajM718f.addContact(new MrimContact(c0028ba, iM1328e4, 128, i4, strM1334g, str7, 0, 1, str8, str8, str8));
                    break;
                }
            case 16:
                NetworkUtils.m1205d(c0028ba, iM1328e, c0043n);
                break;
            case 17:
                ResourceManager.m982a(c0028ba, iM1328e, objArr, c0043n);
                break;
        }
        vector.removeElementAt(size);
    }
}
