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
public final class C0031bd extends C0005ae {

    /* renamed from: f */
    public static ListItem f257f;

    public C0031bd(int i, String str, String str2) {
        super(i, str, str2);
        this.f33b = AppState.m584b(989287);
        this.f34c = 5222;
    }

    @Override // p000.C0005ae, p000.Account
    /* renamed from: a */
    public final int mo80a() {
        return 3;
    }

    public C0031bd(ByteBuffer c0043n) {
        super(c0043n);
        this.f33b = AppState.m584b(989287);
        this.f34c = 5222;
    }

    @Override // p000.C0005ae, p000.Account
    /* renamed from: h */
    public final int mo108h() {
        int iMo108h = super.mo108h();
        int i = iMo108h & 65535;
        return (i < 381 || i > 384) ? iMo108h : iMo108h + 4;
    }

    @Override // p000.C0005ae
    /* renamed from: f */
    public final boolean mo83f() {
        return true;
    }

    @Override // p000.C0005ae
    /* renamed from: j */
    public final String mo84j() {
        return this.f33b;
    }

    @Override // p000.C0005ae
    /* renamed from: m */
    public final String mo128m() {
        return this.f37e;
    }

    @Override // p000.C0005ae, p000.Account
    /* renamed from: p */
    public final int mo110p() {
        return 595126;
    }

    /* renamed from: y */
    private static final int m837y() {
        Account abstractC0037hM616i = AppState.m616i();
        return null != abstractC0037hM616i ? abstractC0037hM616i.mo80a() : AppState.m586d(1475);
    }

    /* renamed from: r */
    public static final void m838r() {
        if (m837y() == 1) {
            Account abstractC0037hM616i = AppState.m616i();
            if (abstractC0037hM616i != null && abstractC0037hM616i.m1055B()) {
                C0015ao.m340m(300);
                return;
            }
            m840t();
            if (abstractC0037hM616i != null) {
                AppState.m601a(1292, (Object) abstractC0037hM616i.f315k);
                AppState.m601a(1293, (Object) abstractC0037hM616i.f316l);
            }
            AbstractC0004ad.m71b(AbstractC0004ad.m75b(2803));
            return;
        }
        if (m837y() == 2) {
            C0005ae c0005ae = (C0005ae) AppState.m616i();
            if (c0005ae != null && c0005ae.m1055B()) {
                C0015ao.m340m(300);
                return;
            }
            m840t();
            AppState.m594c(1474, 0);
            if (c0005ae != null) {
                String strM13b = c0005ae.f315k;
                Vector vectorM516c = Utils.m516c(AppState.m584b(696), (char) 0);
                int iM541c = Utils.m541c(vectorM516c);
                while (true) {
                    iM541c--;
                    if (iM541c < 1) {
                        break;
                    }
                    int iIndexOf = strM13b.indexOf((String) vectorM516c.elementAt(iM541c));
                    if (iIndexOf >= 0) {
                        strM13b = StringUtils.m13b(strM13b, iIndexOf);
                        break;
                    }
                }
                AppState.m594c(1474, iM541c);
                AppState.m601a(1292, (Object) strM13b);
                AppState.m601a(1293, (Object) c0005ae.f316l);
                AppState.m601a(1297, (Object) c0005ae.f339I);
            }
            AbstractC0004ad.m71b(AbstractC0004ad.m75b(3443));
            return;
        }
        if (m837y() == 3) {
            C0031bd c0031bd = (C0031bd) AppState.m616i();
            if (c0031bd != null && c0031bd.m1055B()) {
                C0015ao.m340m(300);
                return;
            }
            m840t();
            if (c0031bd != null) {
                AppState.m601a(1292, (Object) c0031bd.f315k);
                AppState.m601a(1293, (Object) c0031bd.f316l);
                AppState.m601a(1297, (Object) c0031bd.f339I);
            }
            AbstractC0004ad.m71b(AbstractC0004ad.m75b(3463));
            return;
        }
        m840t();
        AppState.m594c(1474, 0);
        Account abstractC0037hM616i2 = AppState.m616i();
        if (null != abstractC0037hM616i2) {
            AppState.m601a(1293, (Object) abstractC0037hM616i2.f316l);
            String str = abstractC0037hM616i2.f315k;
            Vector vectorM516c2 = Utils.m516c(AppState.m584b(694), (char) 0);
            int size = vectorM516c2.size();
            int i = 0;
            while (true) {
                if (i > size) {
                    break;
                }
                if (i == size) {
                    AppState.m601a(1292, (Object) str);
                    break;
                }
                int iIndexOf2 = str.indexOf((String) vectorM516c2.elementAt(i));
                if (iIndexOf2 >= 0) {
                    AppState.m594c(1474, i);
                    AppState.m601a(1292, (Object) StringUtils.m13b(str, iIndexOf2));
                    break;
                }
                i++;
            }
        }
        AbstractC0004ad.m71b(AbstractC0004ad.m75b(2777));
    }

    /* renamed from: s */
    public static final int m839s() {
        C0040k.m1195d();
        if (m837y() == 1) {
            Account abstractC0037hM616i = AppState.m616i();
            String strM843u = m843u();
            int iM437a = C0015ao.m437a(1, abstractC0037hM616i, strM843u, Utils.m522f(AppState.m584b(1293)));
            if (0 != iM437a) {
                return C0015ao.m338l(iM437a);
            }
            C0015ao.m328a(C0015ao.m438b(1, strM843u));
            return 0;
        }
        if (m837y() == 2) {
            return C0029bb.m820c(2);
        }
        if (m837y() == 3) {
            AppState.m594c(1474, 0);
            return C0029bb.m820c(3);
        }
        String strM522f = Utils.m522f(AppState.m584b(1293));
        String strM843u2 = m843u();
        String strM9b = strM843u2;
        if (StringUtils.m1a(strM843u2)) {
            return C0015ao.m338l(301);
        }
        if (!m842c(strM9b, 694) && !m842c(strM9b, 695)) {
            strM9b = StringUtils.m9b(strM9b, Utils.m542c(694, AppState.m586d(1474)));
        }
        if (!m844g(strM9b)) {
            return C0015ao.m338l(559);
        }
        int iM437a2 = C0015ao.m437a(0, AppState.m616i(), strM9b, strM522f);
        if (0 != iM437a2) {
            return C0015ao.m338l(iM437a2);
        }
        C0015ao.m328a(C0015ao.m438b(0, strM9b));
        return 0;
    }

    /* renamed from: t */
    public static final void m840t() {
        AppState.m590b(1292, 1293);
        AppState.m591f(1297);
    }

    /* renamed from: f */
    public static final boolean m841f(String str) {
        return m842c(str, 694);
    }

    /* renamed from: c */
    private static final boolean m842c(String str, int i) {
        Vector vectorM516c = Utils.m516c(AppState.m584b(i), (char) 0);
        int size = vectorM516c.size();
        do {
            size--;
            if (size < 0) {
                C0040k.m1212a(vectorM516c);
                return false;
            }
        } while (str.indexOf((String) vectorM516c.elementAt(size)) < 0);
        return true;
    }

    /* renamed from: u */
    public static final String m843u() {
        return StringUtils.m17c(Utils.m522f(AppState.m584b(1292)).toLowerCase());
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
                if (str.startsWith(AppState.m584b(332005))) {
                    RecordStore recordStore = null;
                    try {
                        RecordStore recordStoreM767a = C0029bb.m767a(str, false);
                        recordStore = recordStoreM767a;
                        size += recordStoreM767a.getSize();
                        C0029bb.m766a(recordStore);
                    } catch (Throwable unused) {
                        C0029bb.m766a(recordStore);
                    }
                }
            }
        }
        AppState.m594c(1552, size);
    }

    /* renamed from: a */
    private static void m846a(C0034e c0034e, byte[] bArr, int i, int i2) {
        if (c0034e == null || bArr == null) {
            return;
        }
        String strM850c = m850c(c0034e);
        ByteBuffer c0043nM1303a = new ByteBuffer().m1308a(c0034e.f285e).m1323a(System.currentTimeMillis()).m1303a(bArr, 0, i2);
        int i3 = 4;
        while (true) {
            i3--;
            if (i3 <= 0) {
                return;
            }
            try {
                try {
                    if (c0043nM1303a.f384b + AppState.m586d(1552) >= 204800) {
                        throw new Throwable();
                    }
                    RecordStore recordStoreM767a = C0029bb.m767a(strM850c, true);
                    byte[] bArr2 = c0043nM1303a.f383a;
                    int i4 = c0043nM1303a.f385c;
                    int i5 = c0043nM1303a.f384b;
                    recordStoreM767a.addRecord(bArr2, i4, i5);
                    AppState.m594c(1552, AppState.m586d(1552) + i5);
                    c0043nM1303a.m1301b();
                    C0029bb.m766a(recordStoreM767a);
                    return;
                } catch (Throwable unused) {
                    m849S();
                    C0029bb.m766a((RecordStore) null);
                }
            } catch (Throwable th) {
                C0029bb.m766a((RecordStore) null);
                throw th;
            }
        }
    }

    /* renamed from: a */
    public static final Image m847a(C0034e c0034e) {
        String strM850c = m850c(c0034e);
        RecordStore recordStore = null;
        try {
            String str = c0034e.f285e;
            RecordStore recordStoreM767a = C0029bb.m767a(strM850c, false);
            recordStore = recordStoreM767a;
            int numRecords = recordStoreM767a.getNumRecords();
            for (int i = 1; i <= numRecords; i++) {
                ByteBuffer c0043nM1304b = new ByteBuffer().m1304b(recordStore.getRecord(i));
                if (c0043nM1304b.m1334g().equals(str)) {
                    c0043nM1304b.m1341m();
                    Image imageM1348r = c0043nM1304b.m1348r();
                    C0029bb.m766a(recordStore);
                    return imageM1348r;
                }
                c0043nM1304b.m1301b();
            }
            C0029bb.m766a(recordStore);
            return null;
        } catch (RuntimeException th) {
            C0029bb.m766a(recordStore);
            throw th;
        } catch (Throwable th) {
            C0029bb.m766a(recordStore);
            throw new RuntimeException(th);
        }
    }

    /* renamed from: z */
    private static final String m848z() {
        String str = null;
        long j = 0;
        String[] strArrM10a = StringUtils.m10a();
        if (strArrM10a != null) {
            String strM584b = AppState.m584b(332005);
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
                        recordStoreM767a = C0029bb.m767a(str2, false);
                        long j2 = j;
                        long lastModified = recordStoreM767a.getLastModified();
                        if (j2 > j2 || j == 0) {
                            j = lastModified;
                            str = str2;
                        }
                        C0029bb.m766a(recordStoreM767a);
                    } catch (RuntimeException th) {
                        C0029bb.m766a(recordStoreM767a);
                        throw th;
                    } catch (Throwable th) {
                        C0029bb.m766a(recordStoreM767a);
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
                RecordStore recordStoreM767a = C0029bb.m767a(strM848z, false);
                recordStore = recordStoreM767a;
                int numRecords = recordStoreM767a.getNumRecords();
                for (int i = 1; i <= numRecords; i++) {
                    AppState.m594c(1552, AppState.m586d(1552) - recordStore.getRecordSize(i));
                }
                C0029bb.m766a(recordStore);
                try {
                    RecordStore.deleteRecordStore(strM848z);
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                C0029bb.m766a(recordStore);
                try {
                    RecordStore.deleteRecordStore(strM848z);
                } catch (Throwable unused3) {
                }
            }
        }
    }

    /* renamed from: c */
    private static final String m850c(C0034e c0034e) {
        return C0040k.m1215a(C0040k.m1217h().append(AppState.m584b(332005)).append(c0034e.f281a).append('z').append(c0034e.f282b).append('x').append((c0034e.f283c / 4) << 2).append('y').append((c0034e.f284d / 4) << 2));
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
                RecordStore recordStoreM767a = C0029bb.m767a(m856d(str, i2), false);
                recordStore = recordStoreM767a;
                byte[] record = recordStoreM767a.getRecord(1);
                c0043n.m1302a(record);
                C0040k.m1209a(record);
                C0029bb.m766a(recordStore);
            } catch (RuntimeException th) {
                C0029bb.m766a(recordStore);
                throw th;
            } catch (Throwable th) {
                C0029bb.m766a(recordStore);
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
        int i = c0043n.f384b;
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
                byte[] bArr = c0043n.m1299a().f383a;
                RecordStore recordStoreM767a = C0029bb.m767a(str, true);
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
                C0029bb.m766a(recordStore);
            } catch (RuntimeException th) {
                C0029bb.m766a(recordStore);
                throw th;
            } catch (Throwable th) {
                C0029bb.m766a(recordStore);
                throw new RuntimeException(th);
            }
        }
        c0043n.m1301b();
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:6:0x0033 */
    /* renamed from: a */
    public static final void m853a(String str, ByteBuffer c0043n) {
        int i = 0;
        int i2 = c0043n.f384b;
        if (i2 > 0) {
            byte[] bArr = c0043n.m1299a().f383a;
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
            RecordStore recordStoreM767a = C0029bb.m767a(str, true);
            recordStore = recordStoreM767a;
            int iM503b = Utils.m503b(i2, Utils.m502a(recordStore.getSizeAvailable() - 128, 2048));
            recordStoreM767a.addRecord(bArr, i, iM503b);
            C0029bb.m766a(recordStore);
            return iM503b;
        } catch (RuntimeException th) {
            C0029bb.m766a(recordStore);
            throw th;
        } catch (Throwable th) {
            C0029bb.m766a(recordStore);
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
            return str.length() <= 32 ? str : StringUtils.m13b(str, 32);
        }
        StringBuffer stringBufferAppend = C0040k.m1217h().append(i);
        StringBuffer stringBufferAppend2 = C0040k.m1217h().append('s').append(str).append('s');
        while (stringBufferAppend2.length() + stringBufferAppend.length() > 32) {
            stringBufferAppend2.setLength(stringBufferAppend2.length() - 1);
        }
        return C0040k.m1215a(stringBufferAppend2.append((Object) stringBufferAppend));
    }

    /* renamed from: a */
    public static final ByteBuffer m857a(C0033d c0033d, int i) {
        return c0033d.m916a(new Object[]{C0015ao.m464a(c0033d, 4873, new ByteBuffer().m1360p(0).m1357m(i).m1357m(4).m1357m(5).m1357m(202).m1357m(1).m1321f(c0033d.m921m())), C0034e.m967e(20)});
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:30:0x00ce */
    /* renamed from: w */
    public static final void m858w() {
        ListItem interfaceC0044o;
        int i = 3072;
        if (C0015ao.m440S().size() > 0) {
            i = 11264;
        }
        if (f257f != null) {
            interfaceC0044o = f257f;
        } else {
            interfaceC0044o = AbstractC0025ay.f203k;
            f257f = interfaceC0044o;
        }
        ListItem interfaceC0044o2 = interfaceC0044o;
        if (interfaceC0044o != null && interfaceC0044o2.mo277s()) {
            switch (interfaceC0044o2.mo276r()) {
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
        if (!AppState.m587e(277)) {
            i &= -1025;
        }
        int i2 = 1424;
        int i3 = 1;
        while (true) {
            int i4 = i3;
            if (i4 >= 16384) {
                AbstractC0004ad.m71b(AbstractC0004ad.m75b(1753));
                return;
            }
            int i5 = i2;
            i2++;
            AppState.m594c(i5, i & i4);
            i3 = i4 << 1;
        }
    }

    /* renamed from: h */
    public static final int m859h(int i) {
        long jMo274v;
        long jMo275w;
        ListItem interfaceC0044o = f257f;
        int iMo276r = interfaceC0044o == null ? 0 : interfaceC0044o.mo276r();
        switch (i) {
            case 0:
                AppState.m613c(interfaceC0044o);
                C0021au.m549c();
                return 63;
            case 1:
                if (iMo276r == 8) {
                    AppState.m594c(4895, 0);
                    C0015ao.m393a((C0028ba) null, ((C0045p) interfaceC0044o).f390a);
                } else {
                    AppState.m613c(f257f);
                }
                C0021au.m549c();
                return 102;
            case 2:
                if (iMo276r == 3) {
                    AppState.m613c(f257f);
                    C0021au.m549c();
                    return 85;
                }
                AppState.m613c((Object) null);
                Vector vectorM440S = C0015ao.m440S();
                if (vectorM440S == null || vectorM440S.size() <= 0) {
                    return C0015ao.m338l(422);
                }
                ((C0028ba) vectorM440S.firstElement()).m751a(new SearchEntry(((C0045p) interfaceC0044o).f390a, 1));
                C0021au.m549c();
                return 85;
            case 3:
                Vector vectorM440S2 = C0015ao.m440S();
                if (vectorM440S2 == null || vectorM440S2.size() <= 0) {
                    return C0015ao.m338l(422);
                }
                ((C0028ba) vectorM440S2.firstElement()).m751a(new SearchEntry(((C0045p) interfaceC0044o).f390a, 2));
                C0021au.m549c();
                return 6;
            case 4:
                C0021au.m549c();
                AppState.f177b[1255] = (Conversation) interfaceC0044o;
                return 170;
            case 5:
                C0034e.m932a(VCard.m63a((C0020at) interfaceC0044o, 0), (C0020at) interfaceC0044o, 0);
                return 12;
            case 6:
                AppState.m617d(interfaceC0044o);
                C0021au.m549c();
                return 167;
            case 7:
                AppState.m617d(interfaceC0044o);
                C0021au.m549c();
                return 151;
            case 8:
            case 9:
            default:
                return 6;
            case 10:
                if (AbstractC0025ay.m656d()) {
                    C0009ai.m195u();
                }
                ListItem interfaceC0044o2 = AbstractC0025ay.f203k;
                if (interfaceC0044o2 == null || !interfaceC0044o2.mo277s()) {
                    jMo274v = AbstractC0025ay.f196d;
                    jMo275w = AbstractC0025ay.f195c;
                } else {
                    jMo274v = interfaceC0044o2.mo274v();
                    jMo275w = interfaceC0044o2.mo275w();
                    interfaceC0044o2.mo278t();
                }
                int[] iArr = {(int) jMo274v, (int) jMo275w};
                C0009ai.f66k.addElement(iArr);
                C0009ai.f67l.addElement(new Object[]{null, iArr});
                AbstractC0025ay.f200h = true;
                if (!AbstractC0025ay.m656d()) {
                    return 6;
                }
                Conversation.m1129c();
                return 6;
            case 11:
                if (AbstractC0025ay.m656d()) {
                    C0009ai.m195u();
                }
                if (C0009ai.f68m != null) {
                    C0009ai.f66k.removeElement((int[]) C0009ai.f68m[1]);
                    C0009ai.f67l.removeElement(C0009ai.f68m);
                }
                AppState.m594c(1575, 0);
                AppState.m599a(1574, AppState.m587e(1573));
                AbstractC0025ay.f200h = true;
                if (!AbstractC0025ay.m656d()) {
                    return 6;
                }
                Conversation.m1129c();
                return 6;
            case 12:
                C0009ai.m185f();
                AbstractC0025ay.f200h = true;
                return 6;
            case 13:
                ListItem interfaceC0044o3 = AbstractC0025ay.f203k;
                if (interfaceC0044o3 != null && interfaceC0044o3.mo277s()) {
                    interfaceC0044o3.mo278t();
                }
                AbstractC0025ay.f200h = true;
                return 6;
            case 14:
                C0039j.m1170l();
                if (C0009ai.m189q()) {
                    return 6;
                }
                AppState.m594c(1442, 1);
                return 158;
            case 15:
                C0039j.m1171m();
                if (C0009ai.m188p()) {
                    return 6;
                }
                AppState.m594c(1442, 0);
                return 158;
            case 16:
                return 159;
            case 17:
                return 114;
            case 18:
                C0021au.m549c();
                Vector vectorM440S3 = C0015ao.m440S();
                if (vectorM440S3.size() > 1) {
                    return 172;
                }
                AppState.m617d(vectorM440S3.elementAt(0));
                return 173;
            case 19:
                return 110;
            case 20:
                C0039j.m1169a((C0014an) f257f);
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
            String str = ((C0005ae) objArr[0]).f315k;
            String strM861j = m861j(StringUtils.m7b(1185660, StringUtils.m15c(str, str.indexOf(64) + 1)));
            if (strM861j == null || strM861j.indexOf(58) <= 0) {
                C0005ae c0005ae = (C0005ae) objArr[0];
                c0005ae.m95a(c0005ae.mo84j(), 5222);
            } else {
                Vector vectorM516c = Utils.m516c(strM861j, ':');
                ((C0005ae) objArr[0]).m95a(Utils.m521a(vectorM516c, 0), Integer.parseInt(Utils.m521a(vectorM516c, 1)));
                C0040k.m1212a(vectorM516c);
            }
        } catch (Throwable th) {
            ((C0005ae) objArr[0]).m94a(th);
        }
    }

    /* renamed from: j */
    private static final String m861j(String str) {
        String strM1317c;
        DatagramConnection datagramConnection = null;
        try {
            C0015ao.m343s();
            Vector vectorM516c = Utils.m516c(str, '.');
            ByteBuffer c0043nM1310c = new ByteBuffer().m1310c(792490);
            for (int i = 0; i < Utils.m541c(vectorM516c); i++) {
                c0043nM1310c.m1373h(Utils.m521a(vectorM516c, i));
            }
            C0040k.m1212a(vectorM516c);
            c0043nM1310c.m1310c(333750);
            DatagramConnection datagramConnection2 = (DatagramConnection) C0029bb.m761a((Object) Connector.open(AppState.m584b(1841038)));
            datagramConnection = datagramConnection2;
            datagramConnection2.send(datagramConnection2.newDatagram(c0043nM1310c.f383a, c0043nM1310c.f384b));
            c0043nM1310c.m1301b();
            Datagram datagramNewDatagram = datagramConnection.newDatagram(512);
            datagramConnection.receive(datagramNewDatagram);
            ByteBuffer c0043nM1304b = new ByteBuffer().m1304b(datagramNewDatagram.getData());
            c0043nM1304b.m1329g(6);
            if (c0043nM1304b.m1353u() <= 0) {
                strM1317c = null;
            } else {
                c0043nM1304b.m1328e();
                while (true) {
                    int iM1346q = c0043nM1304b.m1346q();
                    int i2 = iM1346q;
                    if (iM1346q == 0) {
                        break;
                    }
                    while (true) {
                        i2--;
                        if (i2 < 0) {
                            break;
                        }
                        c0043nM1304b.m1346q();
                    }
                }
                c0043nM1304b.m1329g(20);
                int iM1353u = c0043nM1304b.m1353u();
                ByteBuffer c0043n = new ByteBuffer();
                int iM1346q2 = c0043nM1304b.m1346q();
                while (true) {
                    iM1346q2--;
                    if (iM1346q2 < 0) {
                        int iM1346q3 = c0043nM1304b.m1346q();
                        iM1346q2 = iM1346q3;
                        if (iM1346q3 == 0) {
                            break;
                        }
                        c0043n.m1321f(46);
                    } else {
                        c0043n.m1321f(c0043nM1304b.m1346q());
                    }
                }
                strM1317c = c0043n.m1321f(58).m1382s(iM1353u).m1317c();
            }
            String str2 = strM1317c;
            C0029bb.m765a((Connection) datagramConnection);
            C0015ao.m344t();
            return str2;
        } catch (RuntimeException th) {
            C0029bb.m765a((Connection) datagramConnection);
            C0015ao.m344t();
            throw th;
        } catch (Throwable th) {
            C0029bb.m765a((Connection) datagramConnection);
            C0015ao.m344t();
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    public static final void m862a(Vector vector, ByteBuffer c0043n) {
        int iM541c = Utils.m541c(vector);
        c0043n.m1360p(iM541c);
        for (int i = 0; i < iM541c; i++) {
            String[] strArr = (String[]) vector.elementAt(i);
            c0043n.m1309b(strArr[0]).m1309b(strArr[1]);
        }
    }

    /* renamed from: e */
    public static final Vector m863e(ByteBuffer c0043n) {
        Vector vectorM1213g = C0040k.m1213g();
        int iM1328e = c0043n.m1328e();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                return vectorM1213g;
            }
            vectorM1213g.addElement(new String[]{c0043n.m1335e((String) null), c0043n.m1335e((String) null)});
        }
    }

    /* renamed from: a */
    public static final Vector m864a(Vector vector) {
        Vector vectorM1213g = C0040k.m1213g();
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
        Vector vectorM1213g = C0040k.m1213g();
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
                if (StringUtils.m6a(str, ((String[]) vector.elementAt(iM541c))[0])) {
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
        Vector vectorM1213g = C0040k.m1213g();
        Vector vectorM870k = m870k(Conversation.m1122j(str2));
        Vector vectorM870k2 = m870k(str);
        for (int i = 0; i < Utils.m541c(vectorM870k2); i++) {
            m867a(vectorM1213g, C0015ao.m459a((String) vectorM870k2.elementAt(i), (String) vectorM870k.elementAt(i)));
        }
        C0040k.m1212a(vectorM870k);
        C0040k.m1212a(vectorM870k2);
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
        Vector vectorM1213g = C0040k.m1213g();
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int length = str.length();
        boolean z = true;
        int i = 0;
        while (i <= length) {
            char cCharAt = i < length ? str.charAt(i) : ',';
            if (!z) {
                z = true;
            } else if (cCharAt == ',') {
                vectorM1213g.addElement(C0040k.m1214a(stringBufferM1217h, false));
                z = false;
            } else {
                stringBufferM1217h.append(cCharAt);
            }
            i++;
        }
        C0040k.m1215a(stringBufferM1217h);
        return vectorM1213g;
    }

    /* renamed from: i */
    public static final Vector m871i(String str) {
        Vector vectorM1213g = C0040k.m1213g();
        StringBuffer stringBufferM1217h = C0040k.m1217h();
        int length = str.length();
        int i = 0;
        while (i <= length) {
            char cCharAt = i == length ? ';' : str.charAt(i);
            char c = cCharAt;
            if (cCharAt != ';' && c != ',' && c != ' ') {
                stringBufferM1217h.append(c);
            } else if (stringBufferM1217h.length() > 0) {
                String strM1214a = C0040k.m1214a(stringBufferM1217h, false);
                vectorM1213g.addElement(new String[]{strM1214a, strM1214a});
            }
            i++;
        }
        C0040k.m1215a(stringBufferM1217h);
        return vectorM1213g;
    }

    /* renamed from: b */
    public static final void m872b(int i, int i2) {
        AppState.m594c(1515, i);
        AppState.m594c(1516, i2);
    }

    /* renamed from: x */
    public static final int m873x() {
        String strM1215a;
        Object[] objArrM816k = C0029bb.m816k();
        if (objArrM816k == null) {
            return m874T();
        }
        Object[] objArrM1156c = C0039j.m1156c(objArrM816k);
        if (objArrM1156c == null) {
            return 0;
        }
        int iM818c = C0029bb.m818c(objArrM1156c);
        if (iM818c != 0) {
            return iM818c;
        }
        String strM584b = AppState.m584b(1346);
        ChatRoom c0052wM745h = ((C0028ba) AppState.m616i()).m745h(AppState.m586d(1513));
        Message c0026azM1415b = c0052wM745h.m1415b(strM584b);
        boolean zM671a = c0026azM1415b.m671a(4);
        Object objM819l = C0029bb.m819l();
        Object objM476a = JsonParser.m476a(objM819l, 722874);
        int size = ((Vector) objM476a).size();
        int i = size;
        Object[] objArr = new Object[size];
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            Object objM482e = JsonParser.m482e(objM476a, i);
            objArr[i] = new String[]{JsonParser.m480c(objM482e, 1227), JsonParser.m480c(objM482e, 1228), JsonParser.m480c(objM482e, 1229), JsonParser.m480c(objM482e, 1230), JsonParser.m480c(objM482e, 1231), JsonParser.m480c(objM482e, 1232)};
        }
        c0026azM1415b.f224i = objArr;
        String str = (String) JsonParser.m476a(objM819l, 919493);
        if (str == null) {
            strM1215a = AppState.f181d;
        } else {
            StringBuffer stringBufferM1217h = C0040k.m1217h();
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
            strM1215a = C0040k.m1215a(stringBufferM1217h);
        }
        c0026azM1415b.f223h = strM1215a;
        if (zM671a) {
            c0026azM1415b.m670a(4, false);
            c0052wM745h.m1419b();
        }
        return m874T();
    }

    /* renamed from: T */
    private static final int m874T() {
        int iM586d = AppState.m586d(1515);
        if (iM586d == 54) {
            Message c0026azM1415b = ((C0028ba) AppState.m616i()).m745h(AppState.m586d(1513)).m1415b(AppState.m584b(1346));
            Vector vectorM668b = c0026azM1415b.m668b();
            Vector vectorM669c = c0026azM1415b.m669c();
            m869c(vectorM668b);
            String strM673d = c0026azM1415b.m673d();
            String str = c0026azM1415b.f223h;
            String strM584b = AppState.m584b(198549);
            String strM584b2 = AppState.m584b(198546);
            String string = new StringBuffer().append(AppState.m584b(838)).append(Utils.m507d(str)).toString();
            switch (AppState.m586d(1516)) {
                case 0:
                    C0034e.m966a(m866b(vectorM668b), new StringBuffer().append(strM584b).append(strM673d).toString(), string);
                    break;
                case 1:
                    C0034e.m966a(m865a(m864a(vectorM668b), vectorM669c), new StringBuffer().append(strM584b).append(strM673d).toString(), string);
                    break;
                case 2:
                    C0034e.m966a(C0040k.m1213g(), new StringBuffer().append(strM584b2).append(strM673d).toString(), string);
                    break;
                case 3:
                    C0034e.m966a(m864a(vectorM669c), strM673d, str);
                    break;
            }
        }
        return iM586d;
    }

    /* renamed from: U */
    private static final boolean m875U() {
        try {
            C0015ao.m343s();
            C0040k.m1184b(AppState.m609l(1392));
            AppState.f177b[1392] = C0040k.m1186a(new ByteBuffer().m1310c(593549).m1310c(1511542).m1317c(), false);
            return true;
        } catch (Throwable unused) {
            return false;
        } finally {
            C0015ao.m344t();
        }
    }

    /* JADX DEBUG: Finally have unexpected throw blocks count: 2, expect 1 */
    /* renamed from: b */
    public static final Image m876b(C0034e c0034e) throws IOException {
        ByteBuffer c0043nM1310c = new ByteBuffer().m1310c(2232520).m1314d(c0034e.f285e).m1310c(3870861).m1312e(2950495).m1311d(222).m1310c(6689002);
        try {
            Object[] objArrM609l = AppState.m609l(1392);
            byte[] bArr = c0043nM1310c.f383a;
            int i = c0043nM1310c.f384b;
            C0040k.m1189a(objArrM609l, bArr, i);
            C0015ao.m425F(i);
        } catch (Throwable unused) {
            if (!m875U()) {
                throw new IOException();
            }
            Object[] objArrM609l2 = AppState.m609l(1392);
            byte[] bArr2 = c0043nM1310c.f383a;
            int i2 = c0043nM1310c.f384b;
            C0040k.m1189a(objArrM609l2, bArr2, i2);
            C0015ao.m425F(i2);
        } finally {
            c0043nM1310c.m1301b();
        }
        String strM877V = m877V();
        if (strM877V == null) {
            C0040k.m1184b(AppState.m609l(1392));
            throw new IOException();
        }
        AppState.m595d(1548, strM877V.getBytes().length);
        if (m879l(strM877V) != 200) {
            int iM880m = m880m(strM877V);
            try {
                if (iM880m > 0) {
                    ((InputStream) AppState.m609l(1392)[1]).skip(iM880m);
                } else {
                    C0040k.m1184b(AppState.m609l(1392));
                }
                return null;
            } catch (Throwable unused2) {
                return null;
            }
        }
        ByteBuffer c0043nM878i = m878i(m880m(strM877V));
        if (c0043nM878i == null) {
            C0040k.m1184b(AppState.m609l(1392));
            throw new IOException();
        }
        AppState.m595d(1548, c0043nM878i.f384b);
        byte[] bArr3 = c0043nM878i.f383a;
        int i3 = c0043nM878i.f384b;
        if (AppState.m587e(1551)) {
            m846a(c0034e, bArr3, 0, i3);
        }
        C0015ao.m424E(c0043nM878i.f384b + 255);
        return c0043nM878i.m1348r();
    }

    /* renamed from: V */
    private static final String m877V() {
        Object[] objArrM609l = AppState.m609l(1392);
        ByteBuffer c0043n = new ByteBuffer();
        int i = 0;
        while (true) {
            try {
                int i2 = ((InputStream) objArrM609l[1]).read();
                if (i2 == -1) {
                    return null;
                }
                c0043n.m1321f(i2);
                if (i2 == 10) {
                    i++;
                    if (i == 34) {
                        return c0043n.m1317c();
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
            byte[] bArrM1211a = C0040k.m1211a(8192);
            int length = bArrM1211a.length;
            Object[] objArrM609l = AppState.m609l(1392);
            while (i2 != i && iM1190a != -1) {
                iM1190a = C0040k.m1190a(objArrM609l, bArrM1211a, 0, Utils.m503b(length, i - i2));
                c0043n.m1303a(bArrM1211a, 0, iM1190a);
                i2 += iM1190a;
            }
            C0040k.m1209a(bArrM1211a);
            return c0043n;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: l */
    private static final int m879l(String str) {
        try {
            return Integer.parseInt(StringUtils.m12a(str, 9, 12));
        } catch (Throwable unused) {
            return 0;
        }
    }

    /* renamed from: m */
    private static final int m880m(String str) {
        try {
            int iM628b = AppState.m628b(StringUtils.m17c(str.toLowerCase()), 1052310) + 16;
            return Integer.parseInt(StringUtils.m12a(str, iM628b, str.indexOf(13, iM628b)));
        } catch (Throwable unused) {
            return -1;
        }
    }

    /* renamed from: b */
    public static final void m881b(C0033d c0033d, int i) {
        Vector vector = c0033d.f333C;
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
    public static final void m882a(C0033d c0033d, ByteBuffer c0043n, int i, int i2) {
        Object[] objArr;
        boolean z = false;
        boolean z2;
        C0016ap c0016ap;
        Vector vector = c0033d.f333C;
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
                int iM1353u = c0043n.m1353u();
                if (iM1353u == 0) {
                    ((C0009ai) objArr[2]).m1249c((String) objArr[3]);
                } else {
                    C0029bb.m779a(objArr, iM1353u);
                }
                z = true;
                z3 = z;
                break;
            case 1:
                int iM1353u2 = c0043n.m1353u();
                if (iM1353u2 == 0) {
                    ((C0016ap) objArr[2]).m1403c((String) objArr[3]);
                } else {
                    C0029bb.m779a(objArr, iM1353u2);
                }
                z = true;
                z3 = z;
                break;
            case 2:
                int iM1353u3 = c0043n.m1353u();
                if (iM1353u3 == 0) {
                    c0033d.m1084c((ContactGroup) objArr[2]);
                    c0033d.m1052c(C0034e.m963c(c0033d));
                } else {
                    C0029bb.m781c(objArr, iM1353u3);
                }
                z = true;
                z3 = z;
                break;
            case 3:
                int iM1353u4 = c0043n.m1353u();
                if (iM1353u4 == 0) {
                    c0033d.m1052c(C0034e.m962b(c0033d));
                } else {
                    C0029bb.m782b(iM1353u4);
                }
                z = true;
                z3 = z;
                break;
            case 4:
                int iM1353u5 = c0043n.m1353u();
                if (iM1353u5 == 0) {
                    c0033d.m1083b(new C0016ap(c0033d, ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    c0033d.m1052c(C0034e.m963c(c0033d));
                } else {
                    C0029bb.m780b(objArr, iM1353u5);
                }
                z = true;
                z3 = z;
                break;
            case 5:
                int iM1353u6 = c0043n.m1353u();
                if (iM1353u6 == 0) {
                    c0033d.m1074a((Contact) objArr[2], true);
                    c0033d.m1052c(C0034e.m962b(c0033d));
                } else {
                    C0029bb.m781c(objArr, iM1353u6);
                }
                z = true;
                z3 = z;
                break;
            case 6:
                boolean z4 = (i2 & 1) == 0;
                c0043n.m1329g(1);
                Vector vectorM1213g = C0040k.m1213g();
                int iM1353u7 = c0043n.m1353u();
                for (int i3 = 0; i3 < iM1353u7; i3++) {
                    String strM1364A = c0043n.m1364A();
                    int iM1353u8 = c0043n.m1353u();
                    int iM1353u9 = c0043n.m1353u();
                    int iM1353u10 = c0043n.m1353u();
                    int iM1353u11 = c0043n.m1353u();
                    switch (iM1353u10) {
                        case 0:
                            String strM1364A2 = strM1364A;
                            boolean z5 = false;
                            while (iM1353u11 > 0) {
                                int iM1353u12 = c0043n.m1353u();
                                int iM1351l = c0043n.m1351l(0);
                                if (iM1353u12 == 305) {
                                    strM1364A2 = c0043n.m1364A();
                                } else {
                                    if (iM1353u12 == 102) {
                                        z5 = true;
                                    }
                                    c0043n.m1329g(iM1351l + 2);
                                }
                                iM1353u11 -= iM1351l + 4;
                            }
                            vectorM1213g.addElement(new C0009ai(c0033d, iM1353u9, iM1353u8, strM1364A, strM1364A2, z5));
                            continue;
                        case 1:
                            if (iM1353u8 != 0) {
                                c0033d.f313i.addElement(new C0016ap(c0033d, iM1353u8, strM1364A));
                            }
                            c0043n.m1329g(iM1353u11);
                            continue;
                        case 2:
                            c0033d.f275f.put(strM1364A, C0034e.m967e(iM1353u9));
                            c0043n.m1329g(iM1353u11);
                            continue;
                        case 3:
                            c0033d.f276g.put(strM1364A, C0034e.m967e(iM1353u9));
                            c0043n.m1329g(iM1353u11);
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
                            c0043n.m1329g(iM1353u11);
                            continue;
                        case 14:
                            c0033d.f277h.put(strM1364A, C0034e.m967e(iM1353u9));
                            c0043n.m1329g(iM1353u11);
                            continue;
                    }
                    while (iM1353u11 > 0) {
                        if (c0043n.m1353u() == 202) {
                            c0033d.f274e = iM1353u9;
                        }
                        int iM1353u13 = c0043n.m1353u();
                        c0043n.m1329g(iM1353u13);
                        iM1353u11 -= iM1353u13 + 4;
                    }
                }
                c0033d.f273d = iM1353u7;
                int size2 = vectorM1213g.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        if (z4) {
                            c0033d.m1053d(C0015ao.m464a(c0033d, 4871, (ByteBuffer) null));
                            int i4 = c0033d.f274e;
                            if (i4 != 0) {
                                c0033d.m1053d(m857a(c0033d, i4));
                            }
                            c0033d.m1053d(C0015ao.m464a(c0033d, 258, new ByteBuffer().m1310c(5245205)));
                            c0033d.m1053d(c0033d.m916a(new Object[]{C0015ao.m464a(c0033d, 5378, new ByteBuffer().m1357m(1).m1357m(10).m1358n(8).m1360p(c0033d.f269a).m1358n(60).m1357m(0)), C0034e.m967e(8)}));
                            Enumeration enumerationElements = c0033d.f321q.elements();
                            while (enumerationElements.hasMoreElements()) {
                                Hashtable hashtable = c0033d.f275f;
                                C0009ai c0009ai = (C0009ai) enumerationElements.nextElement();
                                Object obj = hashtable.get(c0009ai.f57c);
                                if (null != obj) {
                                    c0009ai.f59d = ((Integer) obj).intValue();
                                }
                                Object obj2 = c0033d.f276g.get(c0009ai.f57c);
                                if (null != obj2) {
                                    c0009ai.f60e = ((Integer) obj2).intValue();
                                }
                                Object obj3 = c0033d.f277h.get(c0009ai.f57c);
                                if (null != obj3) {
                                    c0009ai.f61f = ((Integer) obj3).intValue();
                                }
                            }
                            c0033d.f275f.clear();
                            c0033d.f276g.clear();
                            c0033d.f277h.clear();
                            if (c0033d.f313i.size() == 0) {
                                c0033d.m1053d(C0034e.m937a(c0033d, AppState.m584b(459528)));
                            }
                            c0033d.f322r = 100;
                            c0033d.f323s = 100;
                        }
                        C0040k.m1212a(vectorM1213g);
                        z = z4;
                        z3 = z;
                        break;
                    } else {
                        C0009ai c0009ai2 = (C0009ai) vectorM1213g.elementAt(size2);
                        int i5 = c0009ai2.f56b;
                        int size3 = c0033d.f313i.size();
                        while (true) {
                            size3--;
                            if (size3 < 0) {
                                c0016ap = null;
                                break;
                            } else {
                                C0016ap c0016ap2 = (C0016ap) c0033d.m1082g(size3);
                                if (c0016ap2.f157a == i5) {
                                    c0016ap = c0016ap2;
                                    break;
                                }
                            }
                        }
                        C0016ap c0016ap3 = c0016ap;
                        if (null != c0016ap) {
                            c0016ap3.m1401b((Object) c0009ai2);
                        }
                    }
                }
            case 7:
                c0043n.m1329g(10);
                if (c0043n.m1354v() == 2010) {
                    c0043n.m1354v();
                    int iM1354v = c0043n.m1354v();
                    c0043n.m1344o();
                    ContactInfo c0042m = (ContactInfo) AppState.f177b[1316];
                    ContactInfo c0042mM1254b = c0042m;
                    if (c0042m == null) {
                        c0042mM1254b = ContactInfo.m1254b(c0033d);
                    }
                    switch (iM1354v) {
                        case 200:
                            String strM1366C = c0043n.m1366C();
                            ContactInfo c0042mM1268i = c0042mM1254b.m1259b(strM1366C).m1260c(c0043n.m1366C()).m1261d(c0043n.m1366C()).m1262e(c0043n.m1366C()).m1268i(c0043n.m1366C());
                            c0043n.m1366C();
                            c0042mM1268i.m1266g(c0043n.m1366C()).m1269j(c0043n.m1366C()).m1270k(c0043n.m1366C()).m1271l(c0043n.m1366C());
                            if (c0033d.f269a == ((Integer) objArr[2]).intValue()) {
                                c0033d.m1054c(strM1366C);
                                break;
                            }
                            break;
                        case 220:
                            ContactInfo c0042mM1273n = c0042mM1254b.m1275b(c0043n.m1354v()).m1277c(c0043n.m1344o()).m1273n(c0043n.m1366C());
                            int iM1354v2 = c0043n.m1354v();
                            byte bM1344o = c0043n.m1344o();
                            byte bM1344o2 = c0043n.m1344o();
                            if (bM1344o2 >= 0) {
                                c0042mM1273n.m1265f(C0040k.m1215a(C0040k.m1217h().append(Utils.m501b(bM1344o2 + 1)).append('/').append(Utils.m501b(bM1344o)).append('/').append(iM1354v2)));
                                break;
                            }
                            break;
                        case 230:
                            c0042mM1254b.m1272m(c0043n.m1366C());
                            break;
                    }
                    boolean z6 = (i2 & 1) == 0;
                    boolean z7 = z6;
                    if (z6) {
                        AppState.f177b[1315] = AppState.f177b[1316];
                        AppState.f177b[1315] = AppState.f177b[1316];
                        AppState.m591f(1316);
                    }
                    z = z7;
                } else {
                    z = true;
                }
                z3 = z;
                break;
            case 8:
                int i6 = c0033d.f329y;
                c0033d.f329y = i6 + 1;
                if (0 != i6) {
                    AppState.m594c(1449, 0);
                }
                c0043n.m1329g(10);
                int iM1354v3 = c0043n.m1354v();
                c0043n.m1354v();
                switch (iM1354v3) {
                    case 65:
                        int iM1328e = c0043n.m1328e();
                        int iM1354v4 = c0043n.m1354v();
                        byte bM1344o3 = c0043n.m1344o();
                        byte bM1344o4 = c0043n.m1344o();
                        byte bM1344o5 = c0043n.m1344o();
                        byte bM1344o6 = c0043n.m1344o();
                        byte b = (iM1354v4 % 4 != 0 || iM1354v4 == 2000) ? (byte) 28 : (byte) 29;
                        int i7 = (((((iM1354v4 - 1970) * 365) + ((iM1354v4 - 1968) / 4)) + bM1344o4) + 28) - b;
                        if (iM1354v4 >= 2000) {
                            i7--;
                        }
                        byte[] bArrM581a = AppState.m581a(945);
                        int i8 = 0;
                        while (i8 < bM1344o3 - 1) {
                            i7 += i8 == 1 ? b : bArrM581a[i8];
                            i8++;
                        }
                        long j = 1000 * ((86400 * i7) + (bM1344o5 * 3600) + (bM1344o6 * 60));
                        c0043n.m1353u();
                        if (iM1328e != 1004) {
                            c0033d.m1072a(Integer.toString(iM1328e), j, c0043n.m1367D());
                        }
                        z = false;
                        break;
                    case 66:
                        AppState.m594c(1449, 1);
                        c0033d.m1052c(C0015ao.m464a(c0033d, 5378, new ByteBuffer().m1357m(1).m1357m(10).m1358n(8).m1360p(c0033d.f269a).m1358n(62).m1357m(0)));
                        break;
                    default:
                        z = false;
                        break;
                }
                z3 = z;
                break;
            case 9:
                Vector vectorM614m = AppState.m614m(1317);
                c0043n.m1329g(10);
                if (c0043n.m1354v() == 2010) {
                    c0043n.m1354v();
                    int iM1354v5 = c0043n.m1354v();
                    if ((420 == iM1354v5 || 430 == iM1354v5) && c0043n.m1344o() == 10) {
                        c0043n.m1353u();
                        ContactInfo c0042mM1262e = ContactInfo.m1254b(c0033d).m1283d(c0043n.m1328e()).m1259b(c0043n.m1366C()).m1260c(c0043n.m1366C()).m1261d(c0043n.m1366C()).m1262e(c0043n.m1366C());
                        c0043n.m1344o();
                        vectorM614m.addElement(c0042mM1262e.m1285e(c0043n.m1354v()).m1277c(c0043n.m1344o()).m1275b(c0043n.m1354v()));
                    }
                    if (iM1354v5 == 430) {
                        AppState.f177b[1318] = AppState.m614m(1317);
                        AppState.m591f(1317);
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
                int iM1353u14 = c0043n.m1353u();
                if (iM1353u14 == 0) {
                    C0009ai c0009ai3 = (C0009ai) objArr[2];
                    C0016ap c0016ap4 = (C0016ap) objArr[3];
                    c0033d.m1052c(c0033d.m916a(new Object[]{C0015ao.m464a(c0033d, 4873, c0016ap4.m465a(c0016ap4.f398f, c0009ai3.f55a, -1)), C0034e.m967e(11), c0009ai3, c0016ap4, objArr[4]}));
                } else {
                    C0029bb.m779a(objArr, iM1353u14);
                }
                z = true;
                z3 = z;
                break;
            case 11:
                int iM1353u15 = c0043n.m1353u();
                if (iM1353u15 == 0) {
                    C0009ai c0009ai4 = (C0009ai) objArr[2];
                    Object obj4 = objArr[3];
                    C0016ap c0016ap5 = (C0016ap) objArr[4];
                    c0033d.m1052c(c0033d.m916a(new Object[]{C0015ao.m464a(c0033d, 4872, c0009ai4.m180a(4, c0009ai4.f376u, c0016ap5.f157a)), C0034e.m967e(12), c0009ai4, obj4, c0016ap5}));
                } else {
                    C0029bb.m779a(objArr, iM1353u15);
                }
                z = true;
                z3 = z;
                break;
            case 12:
                int iM1353u16 = c0043n.m1353u();
                if (iM1353u16 == 0) {
                    C0009ai c0009ai5 = (C0009ai) objArr[2];
                    Object obj5 = objArr[3];
                    C0016ap c0016ap6 = (C0016ap) objArr[4];
                    c0033d.m1052c(c0033d.m916a(new Object[]{C0015ao.m464a(c0033d, 4873, c0016ap6.m465a(c0016ap6.f398f, -1, c0009ai5.f55a)), C0034e.m967e(13), c0009ai5, obj5, c0016ap6}));
                } else {
                    C0029bb.m779a(objArr, iM1353u16);
                }
                z = true;
                z3 = z;
                break;
            case 13:
                int iM1353u17 = c0043n.m1353u();
                if (iM1353u17 == 0) {
                    C0016ap c0016ap7 = (C0016ap) objArr[3];
                    C0009ai c0009ai6 = (C0009ai) objArr[2];
                    c0016ap7.m1402c(c0009ai6);
                    C0016ap c0016ap8 = (C0016ap) objArr[4];
                    c0016ap8.m1401b((Object) c0009ai6);
                    c0009ai6.f56b = c0016ap8.f157a;
                    c0033d.m1052c(C0034e.m962b(c0033d));
                } else {
                    C0029bb.m779a(objArr, iM1353u17);
                }
                z = true;
                z3 = z;
                break;
            case 14:
                int iM1353u18 = c0043n.m1353u();
                if (iM1353u18 == 0) {
                    C0016ap c0016ap9 = (C0016ap) objArr[4];
                    c0033d.m1052c(c0033d.m916a(new Object[]{C0015ao.m464a(c0033d, 4873, c0016ap9.m465a(c0016ap9.f398f, -1, ((Integer) objArr[5]).intValue())), C0034e.m967e(15), objArr[2], objArr[3], c0016ap9, objArr[5], objArr[6]}));
                } else {
                    C0029bb.m779a(objArr, iM1353u18);
                }
                z = true;
                z3 = z;
                break;
            case 15:
                int iM1353u19 = c0043n.m1353u();
                if (iM1353u19 == 0) {
                    C0016ap c0016ap10 = (C0016ap) objArr[4];
                    C0009ai c0009ai7 = new C0009ai(c0033d, ((Integer) objArr[5]).intValue(), c0016ap10.f157a, (String) objArr[2], (String) objArr[3], true);
                    c0016ap10.m1401b((Object) c0009ai7);
                    c0033d.m1052c(C0034e.m962b(c0033d));
                    c0033d.m1052c(C0034e.m961a(c0033d));
                    c0033d.m1052c(c0033d.m916a(new Object[]{C0015ao.m464a(c0033d, 4873, c0009ai7.m180a(5, c0009ai7.f376u, c0009ai7.f56b)), C0034e.m967e(16), objArr[2], objArr[3], objArr[4], objArr[5], objArr[6], c0009ai7}));
                    c0033d.m1052c(C0034e.m962b(c0033d));
                } else {
                    C0029bb.m779a(objArr, iM1353u19);
                }
                z = true;
                z3 = z;
                break;
            case 16:
                int iM1353u20 = c0043n.m1353u();
                if (iM1353u20 == 0) {
                    c0033d.m1052c(C0034e.m962b(c0033d));
                    c0033d.m1052c(C0029bb.m753a(c0033d, (C0009ai) objArr[7], (String) objArr[6]));
                } else {
                    C0029bb.m779a(objArr, iM1353u20);
                }
                z = true;
                z3 = z;
                break;
            case 17:
                c0033d.f324t = c0033d.f325u & 65535;
                z3 = z;
                break;
            case 18:
                int iM1353u21 = c0043n.m1353u();
                if (iM1353u21 == 0) {
                    ((C0009ai) objArr[2]).m181a(((Integer) objArr[3]).intValue(), ((Integer) objArr[4]).intValue());
                } else {
                    C0029bb.m779a(objArr, iM1353u21);
                }
                z = true;
                z3 = z;
                break;
            case 19:
                int iM1353u22 = c0043n.m1353u();
                if (iM1353u22 == 0) {
                    ((C0009ai) objArr[2]).m181a(((Integer) objArr[3]).intValue(), 0);
                } else {
                    C0029bb.m779a(objArr, iM1353u22);
                }
                z = true;
                z3 = z;
                break;
            case 20:
                int iM1353u23 = c0043n.m1353u();
                if (iM1353u23 != 0) {
                    C0029bb.m782b(iM1353u23);
                }
                z = true;
                z3 = z;
                break;
            case 21:
                c0043n.m1329g(10);
                if (c0043n.m1354v() == 2010) {
                    c0043n.m1354v();
                    int iM1354v6 = c0043n.m1354v();
                    if ((420 == iM1354v6 || 430 == iM1354v6) && c0043n.m1344o() == 10) {
                        c0043n.m1353u();
                        ContactInfo c0042mM1262e2 = ContactInfo.m1254b(c0033d).m1283d(c0043n.m1328e()).m1259b(c0043n.m1366C()).m1260c(c0043n.m1366C()).m1261d(c0043n.m1366C()).m1262e(c0043n.m1366C());
                        c0043n.m1344o();
                        ContactInfo c0042mM1275b = c0042mM1262e2.m1285e(c0043n.m1354v()).m1277c(c0043n.m1344o()).m1275b(c0043n.m1354v());
                        C0009ai c0009ai8 = (C0009ai) c0033d.f321q.get(c0042mM1275b.m1256a(60));
                        if (null != c0009ai8) {
                            c0009ai8.m1249c(c0042mM1275b.m1291j());
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
    public static final void m883a(C0028ba c0028ba, ByteBuffer c0043n, int i) {
        Object[] objArr;
        int iM1328e = c0043n.m1328e();
        Vector vector = c0028ba.f333C;
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
                    C0029bb.m779a(objArr, iM1328e);
                    break;
                } else {
                    ((C0035f) objArr[2]).m997a((String) objArr[3], (String) objArr[4]);
                    break;
                }
            case 1:
                if (iM1328e != 0) {
                    C0029bb.m779a(objArr, iM1328e);
                    break;
                } else {
                    ((C0010aj) objArr[2]).m1403c((String) objArr[3]);
                    break;
                }
            case 2:
                if (iM1328e != 0) {
                    C0029bb.m781c(objArr, iM1328e);
                    break;
                } else {
                    c0028ba.m1074a((Contact) objArr[2], true);
                    break;
                }
            case 3:
                if (iM1328e != 0) {
                    C0029bb.m781c(objArr, iM1328e);
                    break;
                } else {
                    C0010aj c0010aj = (C0010aj) objArr[2];
                    int i2 = c0010aj.f75b >> 24;
                    int size2 = c0028ba.f313i.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            c0028ba.m1084c((ContactGroup) c0010aj);
                            break;
                        } else {
                            C0010aj c0010aj2 = (C0010aj) c0028ba.m1082g(size2);
                            if ((c0010aj2.f75b >> 24) > i2) {
                                c0010aj2.f75b -= 16777216;
                            }
                        }
                    }
                }
            case 4:
                if (iM1328e != 0) {
                    C0029bb.m780b(objArr, iM1328e);
                    break;
                } else {
                    c0028ba.f313i.addElement(new C0010aj(c0028ba, c0028ba.m733U(), ((Integer) objArr[3]).intValue(), (String) objArr[2]));
                    break;
                }
            case 5:
                if (iM1328e != 0) {
                    C0029bb.m780b(objArr, iM1328e);
                    break;
                } else {
                    C0010aj c0010aj3 = (C0010aj) objArr[4];
                    int iM1328e2 = c0043n.m1328e();
                    String strM584b = AppState.m584b(1233);
                    String str = (String) objArr[2];
                    String str2 = (String) objArr[3];
                    String str3 = AppState.f181d;
                    c0010aj3.m1401b((Object) new C0035f(c0028ba, iM1328e2, 1048576, 103, strM584b, str, 0, 1, str2, str3, str3));
                    break;
                }
            case 6:
                if (iM1328e != 1) {
                    C0029bb.m778d((Object) C0040k.m1215a(C0040k.m1217h().append(AppState.m584b(922)).append(objArr[2]).append(AppState.m584b(457)).append(iM1328e)));
                    break;
                }
                break;
            case 7:
                C0040k.m1202a(c0028ba, iM1328e, c0043n);
                break;
            case 8:
                C0040k.m1203b(c0028ba, iM1328e, c0043n);
                break;
            case 9:
                if (iM1328e != 0) {
                    if (iM1328e != 5) {
                        C0029bb.m780b(objArr, iM1328e);
                        break;
                    }
                } else {
                    C0010aj c0010aj4 = (C0010aj) objArr[4];
                    int iM1328e3 = c0043n.m1328e();
                    int iIntValue = ((Integer) objArr[5]).intValue();
                    int i3 = c0010aj4.f74a;
                    String str4 = (String) objArr[2];
                    String str5 = (String) objArr[3];
                    String str6 = AppState.f181d;
                    c0010aj4.m1401b((Object) new C0035f(c0028ba, iM1328e3, iIntValue, i3, str4, str5, 1, 0, str6, str6, str6));
                    break;
                }
                break;
            case 10:
                C0035f c0035f = (C0035f) objArr[2];
                switch (iM1328e) {
                    case 0:
                        c0035f.m1238a(((Long) objArr[3]).longValue(), 64);
                        break;
                    case 32769:
                        if (c0035f.mo996n()) {
                            C0029bb.m778d((Object) AppState.m584b(452));
                            break;
                        }
                    default:
                        C0029bb.m778d((Object) C0040k.m1215a(C0040k.m1217h().append(AppState.m584b(453)).append(objArr[2]).append(AppState.m584b(457)).append(iM1328e)));
                        break;
                }
            case 11:
                if (iM1328e != 0) {
                    C0029bb.m779a(objArr, iM1328e);
                    break;
                } else {
                    ((C0035f) objArr[2]).f295b = ((Integer) objArr[3]).intValue();
                    break;
                }
            case 12:
                if (iM1328e != 0) {
                    C0029bb.m779a(objArr, iM1328e);
                    break;
                } else {
                    C0035f c0035f2 = (C0035f) objArr[2];
                    C0010aj c0010aj5 = (C0010aj) objArr[3];
                    c0035f2.f296c = c0010aj5.f74a;
                    int size3 = c0028ba.f313i.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            c0010aj5.m1401b((Object) c0035f2);
                            break;
                        } else {
                            c0028ba.m1082g(size3).m1402c(c0035f2);
                        }
                    }
                }
            case 13:
                C0040k.m1204c(c0028ba, iM1328e, c0043n);
                break;
            case 15:
                if (iM1328e != 0) {
                    C0029bb.m780b(objArr, iM1328e);
                    break;
                } else {
                    C0010aj c0010ajM718f = c0028ba.m718f();
                    int iM1328e4 = c0043n.m1328e();
                    int i4 = c0010ajM718f.f74a;
                    String strM1334g = c0043n.m1334g();
                    String str7 = (String) objArr[2];
                    String str8 = AppState.f181d;
                    c0010ajM718f.m1401b(new C0035f(c0028ba, iM1328e4, 128, i4, strM1334g, str7, 0, 1, str8, str8, str8));
                    break;
                }
            case 16:
                C0040k.m1205d(c0028ba, iM1328e, c0043n);
                break;
            case 17:
                C0034e.m982a(c0028ba, iM1328e, objArr, c0043n);
                break;
        }
        vector.removeElementAt(size);
    }
}
