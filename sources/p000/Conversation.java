package p000;

import java.util.Enumeration;
import java.util.Vector;

/* renamed from: i */
/* loaded from: MobileAgent_3.9.jar:i.class */
public final class Conversation implements ListItem {

    /* renamed from: c */
    private int f344c = 5;

    /* renamed from: b */
    private boolean f342b = true;

    /* renamed from: a */
    public Vector f343a = NetworkUtils.m1213g();

    /* renamed from: d */
    private SizeCache f345d = new SizeCache();

    /* renamed from: a */
    public final void m1086a(ListItem interfaceC0044o) {
        this.f343a.addElement(interfaceC0044o);
        this.f345d.lastScale = -1;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return this.f344c;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.f342b && this.f343a.size() > 0;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.f342b = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.f342b = true;
    }

    /* renamed from: d */
    private final ListItem m1087d(int i) {
        return (ListItem) this.f343a.elementAt(i);
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        long jMo274v = 0;
        int size = this.f343a.size();
        int i = size;
        while (true) {
            i--;
            if (i < 0) {
                return (int) (jMo274v / size);
            }
            jMo274v += m1087d(i).getWidth();
        }
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        long jMo275w = 0;
        int size = this.f343a.size();
        int i = size;
        while (true) {
            i--;
            if (i < 0) {
                return (int) (jMo275w / size);
            }
            jMo275w += m1087d(i).getBaseHeight();
        }
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.f345d.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.f345d.getHeight(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int size = this.f343a.size();
        if (this.f344c == 5) {
            stringBufferM1217h.append(AppState.m584b(445)).append(size);
        } else {
            int i = size - 1;
            stringBufferM1217h.append(((ListItem) this.f343a.firstElement()).getText()).append(AppState.m584b(446)).append(i).append(AppState.m584b(442 + Utils.m540f(i)));
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        return 10;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return true;
    }

    /* renamed from: a */
    public static final void m1088a(Object[] objArr) {
        int i;
        MmpProtocol c0033d = (MmpProtocol) objArr[0];
        try {
            try {
                AppController.m343s();
                if (((Integer) objArr[1]).intValue() == 0) {
                    c0033d.f323s = 30;
                    AppController.f153g = true;
                    HttpClient c0024axM629a = HttpClient.m629a(AppState.m584b(2755089), c0033d, 0);
                    c0024axM629a.m635b(NetworkUtils.m1221a(1414745936));
                    ByteBuffer c0043nM1315a = new ByteBuffer().writeCompressed(2755131).writeConversationStr(objArr[2]).writeCompressed(330609).writeConversationStr(objArr[3]);
                    ConnectionThread.m1153a(c0024axM629a, 788628, 2164851);
                    c0024axM629a.m637a(c0043nM1315a.data, c0043nM1315a.length);
                    int iM634a = c0024axM629a.m634a();
                    i = iM634a;
                    if (iM634a == 200) {
                        c0033d.f323s = 40;
                        AppController.f153g = true;
                        XmlElement c0022avM1389J = new ByteBuffer(c0024axM629a).parseXmlStr();
                        int i2 = Integer.parseInt(StringUtils.m11a(c0022avM1389J.findChildByKey(658246).textContent));
                        if (i2 != 200) {
                            if (i2 == 330) {
                                ((MmpProtocol) objArr[0]).m1065J();
                                objArr[0] = null;
                            }
                            throw new RuntimeException(StringUtils.m17c(Integer.toString(i2)));
                        }
                        XmlElement c0022avM562f = c0022avM1389J.findChildByKey(262156);
                        new AsyncTask(31, new Object[]{objArr[0], ResourceManager.m967e(1), StringUtils.m11a(c0022avM562f.findChildByKey(461648).textContent), StringUtils.m11a(c0022avM562f.findChildByKey(330583).findChildByKey(65538).textContent), StringUtils.m11a(c0022avM562f.findChildByKey(527196).textContent), StringUtils.m11a(c0022avM562f.findChildByKey(854884).textContent), objArr[3]});
                        HttpClient.m633a(c0024axM629a);
                        AppController.m344t();
                        return;
                    }
                } else {
                    c0033d.f323s = 50;
                    AppController.f153g = true;
                    ByteBuffer c0043nM1321f = new ByteBuffer().writeCompressed(2951781).writeByte(63);
                    String strM1337i = new ByteBuffer().writeCompressed(132058).writeConversationStr(objArr[3]).writeCompressed(11012754).writeObjectStr(objArr[4]).readAllByteStr();
                    HttpClient c0024axM642a = HttpClient.m632a(c0043nM1321f.writeRawString(strM1337i).writeCompressed(789306).writeRawString(m1089a(new ByteBuffer().writeCompressed(265078).writeRawString(m1125a(AppState.m584b(2951781), false)).writeByte(38).writeRawString(m1125a(strM1337i, false)).readAllByteStr(), m1089a((String) objArr[5], (String) objArr[6]))).readAllByteStr()).m642a(0, 5522759, 330359);
                    int iM634a2 = c0024axM642a.m634a();
                    i = iM634a2;
                    if (iM634a2 == 200) {
                        c0033d.f323s = 60;
                        AppController.f153g = true;
                        XmlElement c0022avM562f2 = c0024axM642a.m644b().parseXmlStr().findChildByKey(262156);
                        ((MmpProtocol) objArr[0]).f272c = new String[]{(String) objArr[2], NetworkUtils.m1215a(NetworkUtils.m1217h().append(StringUtils.m11a(c0022avM562f2.findChildByKey(265052).textContent)).append(':').append(StringUtils.m11a(c0022avM562f2.findChildByKey(265005).textContent))), StringUtils.m11a(c0022avM562f2.findChildByKey(395483).textContent)};
                        HttpClient.m633a(c0024axM642a);
                        AppController.m344t();
                        return;
                    }
                }
                throw new Throwable(StringUtils.m17c(Integer.toString(i)));
            } catch (Throwable th) {
                MmpProtocol c0033d2 = (MmpProtocol) objArr[0];
                c0033d2.f324t = c0033d2.mo89g();
                IOUtils.m784a(c0033d2, th.toString());
                c0033d2.f322r = 0;
                HttpClient.m633a((HttpClient) null);
                AppController.m344t();
            }
        } catch (Throwable th2) {
            HttpClient.m633a((HttpClient) null);
            AppController.m344t();
            throw th2;
        }
    }

    /* renamed from: a */
    private static final String m1089a(String str, String str2) {
        return new ByteBuffer().setData(XmppContactGroup.m1015a(str2.getBytes(), str2.length(), str.getBytes(), str.length(), 32)).toBase64();
    }

    /* renamed from: a */
    public static final byte[] m1090a(byte[] bArr, int i) {
        int[] iArr = new int[16];
        int[] iArrM536a = Utils.m536a(AppState.m581a(962));
        int[] iArr2 = new int[2];
        byte[] bArrM1211a = NetworkUtils.m1211a(64);
        m1095a(bArr, i, iArr, iArrM536a, iArr2, bArrM1211a);
        byte[] bArrM1097a = m1097a(NetworkUtils.m1211a(16), iArr2, 8);
        byte[] bArrM1211a2 = NetworkUtils.m1211a(64);
        bArrM1211a2[0] = -128;
        int i2 = (iArr2[0] >>> 3) & 63;
        m1095a(bArrM1211a2, i2 < 56 ? 56 - i2 : 120 - i2, iArr, iArrM536a, iArr2, bArrM1211a);
        m1095a(bArrM1097a, 8, iArr, iArrM536a, iArr2, bArrM1211a);
        NetworkUtils.m1209a(bArrM1211a2);
        NetworkUtils.m1209a(bArrM1211a);
        return m1097a(bArrM1097a, iArrM536a, 16);
    }

    /* renamed from: a */
    private static final int m1091a(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i ^ (-1)) & i3) | (i & i2)) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: b */
    private static final int m1092b(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i3 ^ (-1)) & i2) | (i & i3)) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: c */
    private static final int m1093c(int i, int i2, int i3, int i4, int i5) {
        int i6 = ((i ^ i2) ^ i3) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: d */
    private static final int m1094d(int i, int i2, int i3, int i4, int i5) {
        int i6 = (((i3 ^ (-1)) | i) ^ i2) + i5;
        return ((i6 << i4) | (i6 >>> (32 - i4))) + i;
    }

    /* renamed from: a */
    private static final void m1095a(byte[] bArr, int i, int[] iArr, int[] iArr2, int[] iArr3, byte[] bArr2) {
        int i2 = (iArr3[0] >>> 3) & 63;
        int i3 = iArr3[0] + (i << 3);
        iArr3[0] = i3;
        if (i3 < (i << 3)) {
            iArr3[1] = iArr3[1] + 1;
        }
        iArr3[1] = iArr3[1] + (i >>> 29);
        int i4 = 64 - i2;
        int i5 = i4;
        if (i >= i4) {
            Utils.m490a((Object) bArr, 0, (Object) bArr2, i2, i5);
            m1096a(bArr2, iArr, iArr2);
            byte[] bArrM1211a = NetworkUtils.m1211a(64);
            while (i5 + 63 < i) {
                Utils.m490a((Object) bArr, i5, (Object) bArrM1211a, 0, 64);
                m1096a(bArrM1211a, iArr, iArr2);
                i5 += 64;
            }
            NetworkUtils.m1209a(bArrM1211a);
            i2 = 0;
        } else {
            i5 = 0;
        }
        Utils.m490a((Object) bArr, i5, (Object) bArr2, i2, i - i5);
    }

    /* renamed from: a */
    private static final void m1096a(byte[] bArr, int[] iArr, int[] iArr2) {
        int i = 0;
        int i2 = -1;
        do {
            int i3 = i2 + 1;
            int i4 = bArr[i3] & 255;
            int i5 = i3 + 1;
            int i6 = i4 | ((bArr[i5] & 255) << 8);
            int i7 = i5 + 1;
            int i8 = i6 | ((bArr[i7] & 255) << 16);
            i2 = i7 + 1;
            iArr[i] = i8 | (bArr[i2] << 24);
            i++;
        } while (i < 16);
        int i9 = iArr2[1];
        int i10 = iArr2[2];
        int iM1091a = m1091a(i9, i10, iArr2[3], 7, (iArr2[0] + iArr[0]) - 680876936);
        int iM1091a2 = m1091a(iM1091a, i9, i10, 12, (iArr2[3] + iArr[1]) - 389564586);
        int iM1091a3 = m1091a(iM1091a2, iM1091a, i9, 17, i10 + iArr[2] + 606105819);
        int iM1091a4 = m1091a(iM1091a3, iM1091a2, iM1091a, 22, (i9 + iArr[3]) - 1044525330);
        int iM1091a5 = m1091a(iM1091a4, iM1091a3, iM1091a2, 7, (iM1091a + iArr[4]) - 176418897);
        int iM1091a6 = m1091a(iM1091a5, iM1091a4, iM1091a3, 12, iM1091a2 + iArr[5] + 1200080426);
        int iM1091a7 = m1091a(iM1091a6, iM1091a5, iM1091a4, 17, (iM1091a3 + iArr[6]) - 1473231341);
        int iM1091a8 = m1091a(iM1091a7, iM1091a6, iM1091a5, 22, (iM1091a4 + iArr[7]) - 45705983);
        int iM1091a9 = m1091a(iM1091a8, iM1091a7, iM1091a6, 7, iM1091a5 + iArr[8] + 1770035416);
        int iM1091a10 = m1091a(iM1091a9, iM1091a8, iM1091a7, 12, (iM1091a6 + iArr[9]) - 1958414417);
        int iM1091a11 = m1091a(iM1091a10, iM1091a9, iM1091a8, 17, (iM1091a7 + iArr[10]) - 42063);
        int iM1091a12 = m1091a(iM1091a11, iM1091a10, iM1091a9, 22, (iM1091a8 + iArr[11]) - 1990404162);
        int iM1091a13 = m1091a(iM1091a12, iM1091a11, iM1091a10, 7, iM1091a9 + iArr[12] + 1804603682);
        int iM1091a14 = m1091a(iM1091a13, iM1091a12, iM1091a11, 12, (iM1091a10 + iArr[13]) - 40341101);
        int iM1091a15 = m1091a(iM1091a14, iM1091a13, iM1091a12, 17, (iM1091a11 + iArr[14]) - 1502002290);
        int iM1091a16 = m1091a(iM1091a15, iM1091a14, iM1091a13, 22, iM1091a12 + iArr[15] + 1236535329);
        int iM1092b = m1092b(iM1091a16, iM1091a15, iM1091a14, 5, (iM1091a13 + iArr[1]) - 165796510);
        int iM1092b2 = m1092b(iM1092b, iM1091a16, iM1091a15, 9, (iM1091a14 + iArr[6]) - 1069501632);
        int iM1092b3 = m1092b(iM1092b2, iM1092b, iM1091a16, 14, iM1091a15 + iArr[11] + 643717713);
        int iM1092b4 = m1092b(iM1092b3, iM1092b2, iM1092b, 20, (iM1091a16 + iArr[0]) - 373897302);
        int iM1092b5 = m1092b(iM1092b4, iM1092b3, iM1092b2, 5, (iM1092b + iArr[5]) - 701558691);
        int iM1092b6 = m1092b(iM1092b5, iM1092b4, iM1092b3, 9, iM1092b2 + iArr[10] + 38016083);
        int iM1092b7 = m1092b(iM1092b6, iM1092b5, iM1092b4, 14, (iM1092b3 + iArr[15]) - 660478335);
        int iM1092b8 = m1092b(iM1092b7, iM1092b6, iM1092b5, 20, (iM1092b4 + iArr[4]) - 405537848);
        int iM1092b9 = m1092b(iM1092b8, iM1092b7, iM1092b6, 5, iM1092b5 + iArr[9] + 568446438);
        int iM1092b10 = m1092b(iM1092b9, iM1092b8, iM1092b7, 9, (iM1092b6 + iArr[14]) - 1019803690);
        int iM1092b11 = m1092b(iM1092b10, iM1092b9, iM1092b8, 14, (iM1092b7 + iArr[3]) - 187363961);
        int iM1092b12 = m1092b(iM1092b11, iM1092b10, iM1092b9, 20, iM1092b8 + iArr[8] + 1163531501);
        int iM1092b13 = m1092b(iM1092b12, iM1092b11, iM1092b10, 5, (iM1092b9 + iArr[13]) - 1444681467);
        int iM1092b14 = m1092b(iM1092b13, iM1092b12, iM1092b11, 9, (iM1092b10 + iArr[2]) - 51403784);
        int iM1092b15 = m1092b(iM1092b14, iM1092b13, iM1092b12, 14, iM1092b11 + iArr[7] + 1735328473);
        int iM1092b16 = m1092b(iM1092b15, iM1092b14, iM1092b13, 20, (iM1092b12 + iArr[12]) - 1926607734);
        int iM1093c = m1093c(iM1092b16, iM1092b15, iM1092b14, 4, (iM1092b13 + iArr[5]) - 378558);
        int iM1093c2 = m1093c(iM1093c, iM1092b16, iM1092b15, 11, (iM1092b14 + iArr[8]) - 2022574463);
        int iM1093c3 = m1093c(iM1093c2, iM1093c, iM1092b16, 16, iM1092b15 + iArr[11] + 1839030562);
        int iM1093c4 = m1093c(iM1093c3, iM1093c2, iM1093c, 23, (iM1092b16 + iArr[14]) - 35309556);
        int iM1093c5 = m1093c(iM1093c4, iM1093c3, iM1093c2, 4, (iM1093c + iArr[1]) - 1530992060);
        int iM1093c6 = m1093c(iM1093c5, iM1093c4, iM1093c3, 11, iM1093c2 + iArr[4] + 1272893353);
        int iM1093c7 = m1093c(iM1093c6, iM1093c5, iM1093c4, 16, (iM1093c3 + iArr[7]) - 155497632);
        int iM1093c8 = m1093c(iM1093c7, iM1093c6, iM1093c5, 23, (iM1093c4 + iArr[10]) - 1094730640);
        int iM1093c9 = m1093c(iM1093c8, iM1093c7, iM1093c6, 4, iM1093c5 + iArr[13] + 681279174);
        int iM1093c10 = m1093c(iM1093c9, iM1093c8, iM1093c7, 11, (iM1093c6 + iArr[0]) - 358537222);
        int iM1093c11 = m1093c(iM1093c10, iM1093c9, iM1093c8, 16, (iM1093c7 + iArr[3]) - 722521979);
        int iM1093c12 = m1093c(iM1093c11, iM1093c10, iM1093c9, 23, iM1093c8 + iArr[6] + 76029189);
        int iM1093c13 = m1093c(iM1093c12, iM1093c11, iM1093c10, 4, (iM1093c9 + iArr[9]) - 640364487);
        int iM1093c14 = m1093c(iM1093c13, iM1093c12, iM1093c11, 11, (iM1093c10 + iArr[12]) - 421815835);
        int iM1093c15 = m1093c(iM1093c14, iM1093c13, iM1093c12, 16, iM1093c11 + iArr[15] + 530742520);
        int iM1093c16 = m1093c(iM1093c15, iM1093c14, iM1093c13, 23, (iM1093c12 + iArr[2]) - 995338651);
        int iM1094d = m1094d(iM1093c16, iM1093c15, iM1093c14, 6, (iM1093c13 + iArr[0]) - 198630844);
        int iM1094d2 = m1094d(iM1094d, iM1093c16, iM1093c15, 10, iM1093c14 + iArr[7] + 1126891415);
        int iM1094d3 = m1094d(iM1094d2, iM1094d, iM1093c16, 15, (iM1093c15 + iArr[14]) - 1416354905);
        int iM1094d4 = m1094d(iM1094d3, iM1094d2, iM1094d, 21, (iM1093c16 + iArr[5]) - 57434055);
        int iM1094d5 = m1094d(iM1094d4, iM1094d3, iM1094d2, 6, iM1094d + iArr[12] + 1700485571);
        int iM1094d6 = m1094d(iM1094d5, iM1094d4, iM1094d3, 10, (iM1094d2 + iArr[3]) - 1894986606);
        int iM1094d7 = m1094d(iM1094d6, iM1094d5, iM1094d4, 15, (iM1094d3 + iArr[10]) - 1051523);
        int iM1094d8 = m1094d(iM1094d7, iM1094d6, iM1094d5, 21, (iM1094d4 + iArr[1]) - 2054922799);
        int iM1094d9 = m1094d(iM1094d8, iM1094d7, iM1094d6, 6, iM1094d5 + iArr[8] + 1873313359);
        int iM1094d10 = m1094d(iM1094d9, iM1094d8, iM1094d7, 10, (iM1094d6 + iArr[15]) - 30611744);
        int iM1094d11 = m1094d(iM1094d10, iM1094d9, iM1094d8, 15, (iM1094d7 + iArr[6]) - 1560198380);
        int iM1094d12 = m1094d(iM1094d11, iM1094d10, iM1094d9, 21, iM1094d8 + iArr[13] + 1309151649);
        int iM1094d13 = m1094d(iM1094d12, iM1094d11, iM1094d10, 6, (iM1094d9 + iArr[4]) - 145523070);
        int iM1094d14 = m1094d(iM1094d13, iM1094d12, iM1094d11, 10, (iM1094d10 + iArr[11]) - 1120210379);
        int iM1094d15 = m1094d(iM1094d14, iM1094d13, iM1094d12, 15, iM1094d11 + iArr[2] + 718787259);
        int iM1094d16 = m1094d(iM1094d15, iM1094d14, iM1094d13, 21, (iM1094d12 + iArr[9]) - 343485551);
        iArr2[0] = iArr2[0] + iM1094d13;
        iArr2[1] = iArr2[1] + iM1094d16;
        iArr2[2] = iArr2[2] + iM1094d15;
        iArr2[3] = iArr2[3] + iM1094d14;
    }

    /* JADX WARN: Type inference failed for: r2v10, types: [int] */
    /* renamed from: a */
    private static final byte[] m1097a(byte[] bArr, int[] iArr, int i) {
        int i2 = 0;
        int i3 = 0;
        do {
            int i4 = i3;
            int i5 = i3 + 1;
            int i6 = i2;
            i2++;
            int i7 = iArr[i6];
            bArr[i4] = (byte) i7;
            int i8 = i5 + 1;
            bArr[i5] = (byte) (i7 >>> 8);
            int i9 = i8 + 1;
            bArr[i8] = (byte) (i7 >>> 16);
            i3 = i9 + 1;
            bArr[i9] = (byte) (i7 >> 24);
        } while (i3 < i);
        return bArr;
    }

    /* renamed from: a */
    public static final Vector m1098a(String str) {
        Vector vectorM1213g = NetworkUtils.m1213g();
        if (m1106f(str)) {
            int i = 0;
            int i2 = 0;
            while (true) {
                try {
                    int iIndexOf = str.indexOf(AppState.m584b(1245774), i);
                    if (iIndexOf < 0) {
                        break;
                    }
                    i2 = iIndexOf;
                    if (i != iIndexOf) {
                        vectorM1213g.addElement(StringUtils.m12a(str, i, iIndexOf));
                    }
                    int iIndexOf2 = str.indexOf(32, iIndexOf);
                    i = iIndexOf2;
                    if (iIndexOf2 < 0) {
                        vectorM1213g.addElement(StringUtils.m15c(str, iIndexOf));
                        break;
                    }
                    vectorM1213g.addElement(StringUtils.m12a(str, iIndexOf, i));
                } catch (Throwable unused) {
                }
            }
            int iIndexOf3 = str.indexOf(32, i2);
            if (iIndexOf3 >= 0) {
                vectorM1213g.addElement(StringUtils.m15c(str, iIndexOf3));
            }
        } else {
            vectorM1213g.addElement(str);
        }
        return vectorM1213g;
    }

    /* renamed from: b */
    public static final String m1099b(String str) {
        try {
            if (!m1107n(str)) {
                if (m1108o(str)) {
                    return AppState.m584b(994);
                }
                return null;
            }
            int iM626a = AppState.m626a(str, 1031040294);
            int iIndexOf = str.indexOf(NetworkUtils.m1221a(1031302438), AppState.m626a(str, 1031302438) + 4);
            String strM15c = iIndexOf < 0 ? StringUtils.m15c(str, iM626a + 4) : StringUtils.m12a(str, iM626a + 4, iIndexOf);
            String str2 = strM15c;
            if (StringUtils.m2a(strM15c, 1094795585)) {
                return AppState.f181d;
            }
            ByteBuffer c0043nM986d = ResourceManager.m986d(m1109a(m1109a(m1109a(str2, 200762, 65752), 200765, 65547), 200768, 65552));
            StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
            while (c0043nM986d.length > 0) {
                int iM1346q = c0043nM986d.readUByte();
                int iM1346q2 = iM1346q > 127 ? c0043nM986d.readUByte() : 0;
                int iM1346q3 = iM1346q > 223 ? c0043nM986d.readUByte() : 0;
                stringBufferM1217h.append(iM1346q < 128 ? (char) iM1346q : iM1346q < 224 ? (char) (((iM1346q - 192) << 6) + (iM1346q2 - 128)) : iM1346q < 240 ? (char) (((iM1346q - 224) << 12) + ((iM1346q2 - 128) << 6) + (iM1346q3 - 128)) : (char) (((iM1346q - 240) << 18) + ((iM1346q2 - 128) << 12) + ((iM1346q3 - 128) << 6) + ((iM1346q > 239 ? c0043nM986d.readUByte() : 0) - 128)));
            }
            return NetworkUtils.m1215a(stringBufferM1217h);
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: c */
    public static final String m1100c(String str) {
        try {
            if (m1107n(str)) {
                return StringUtils.m12a(str, AppState.m626a(str, 1031302438) + 4, AppState.m626a(str, 1031367974));
            }
            if (m1108o(str)) {
                return StringUtils.m12a(str, AppState.m626a(str, 4028451) + 3, AppState.m626a(str, 4028710));
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: d */
    public static final String m1101d(String str) {
        try {
            if (m1107n(str)) {
                return StringUtils.m12a(str, AppState.m626a(str, 1031367974) + 4, AppState.m626a(str, 1031040294));
            }
            if (m1108o(str)) {
                return StringUtils.m12a(str, AppState.m626a(str, 4028710) + 3, AppState.m626a(str, 4028966));
            }
            return null;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: e */
    public static final String m1102e(String str) {
        try {
            return StringUtils.m12a(str, AppState.m626a(str, 4028966) + 3, AppState.m628b(str, 397364));
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: m */
    private static final boolean m1103m(String str) {
        return AppState.m628b(str, 1245774) >= 0;
    }

    /* renamed from: a */
    public static final boolean m1104a(String str, int i) {
        return AppState.m628b(str, i) >= 0;
    }

    /* renamed from: b */
    private static final boolean m1105b(String str, int i) {
        return AppState.m626a(str, i) >= 0;
    }

    /* renamed from: f */
    public static final boolean m1106f(String str) {
        if (!m1103m(str)) {
            return false;
        }
        if (m1105b(str, 4028451) && m1105b(str, 4028710) && m1105b(str, 4028966)) {
            return true;
        }
        return m1105b(str, 1031302438) && m1105b(str, 1031367974) && m1105b(str, 1031040294);
    }

    /* renamed from: n */
    private static boolean m1107n(String str) {
        return m1103m(str) && m1105b(str, 4028451) && m1105b(str, 4028710) && m1105b(str, 4028966) && m1105b(str, 1031302438) && m1105b(str, 1031367974) && m1105b(str, 1031040294);
    }

    /* renamed from: o */
    private static boolean m1108o(String str) {
        return m1103m(str) && m1105b(str, 4028451) && m1105b(str, 4028710) && m1105b(str, 4028966) && !m1105b(str, 1031302438) && !m1105b(str, 1031367974) && !m1105b(str, 1031040294);
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:8:0x0038 */
    /* renamed from: a */
    public static final String m1109a(String str, int i, int i2) {
        String strM584b = AppState.m584b(i);
        if (str.indexOf(strM584b) < 0) {
            return str;
        }
        String strM584b2 = AppState.m584b(i2);
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int length = 0;
        while (true) {
            int i3 = length;
            int iIndexOf = str.indexOf(strM584b, i3);
            if (iIndexOf < 0) {
                return NetworkUtils.m1215a(stringBufferM1217h.append(StringUtils.m15c(str, i3)));
            }
            stringBufferM1217h.append(StringUtils.m12a(str, i3, iIndexOf)).append(strM584b2);
            length = iIndexOf + strM584b.length();
        }
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:13:0x00be */
    /* renamed from: a */
    public static final void m1110a(MrimAccount c0028ba, ByteBuffer c0043n, long j) {
        MrimContact c0035f;
        int i;
        int iIndexOf;
        int iM1328e = c0043n.readInt();
        int iM1328e2 = c0043n.readInt();
        String strM1338j = c0043n.readHexStr();
        String strM1332j = c0043n.readStringByMode(iM1328e2 & 2097160);
        String strM1215a = null;
        String str = null;
        if ((iM1328e2 & 8) == 0) {
            String strM1111p = m1111p(strM1332j);
            StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
            String strM584b = AppState.m584b(658377);
            String strM584b2 = AppState.m584b(396261);
            String strM584b3 = AppState.m584b(592851);
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= strM1111p.length()) {
                    break;
                }
                int iIndexOf2 = strM1111p.indexOf(strM584b, i3);
                if (iIndexOf2 < 0) {
                    stringBufferM1217h.append(StringUtils.m15c(strM1111p, i3));
                    break;
                }
                stringBufferM1217h.append(StringUtils.m12a(strM1111p, i3, iIndexOf2));
                int iIndexOf3 = strM1111p.indexOf(strM584b2, iIndexOf2 + 10);
                if (iIndexOf3 < 0 || (iIndexOf = strM1111p.indexOf(strM584b3, (i = iIndexOf3 + 6))) < 0) {
                    break;
                }
                stringBufferM1217h.append(StringUtils.m12a(strM1111p, i, iIndexOf));
                i2 = iIndexOf + 9;
            }
            strM1215a = NetworkUtils.m1215a(stringBufferM1217h);
        } else {
            ByteBuffer c0043nM986d = ResourceManager.m986d(strM1332j);
            int i4 = iM1328e2 & 2097152;
            int iM1328e3 = c0043nM986d.readInt();
            String[] strArr = new String[iM1328e3];
            for (int i5 = 0; i5 < iM1328e3; i5++) {
                strArr[i5] = c0043nM986d.readStringByMode(i4);
            }
            c0043nM986d.clear();
            str = strArr[1];
        }
        if ((iM1328e2 & 128) != 0) {
            c0043n.readWideStr();
        }
        if ((iM1328e2 & 4194304) != 0) {
            if ((iM1328e2 & 17408) != 0) {
                return;
            }
            c0043n.readInt();
            switch (c0043n.readInt()) {
                case 0:
                    c0028ba.m738a(strM1338j, strM1215a, c0043n.readUTF8Str((String) null), c0043n.readWideStr(), j);
                    break;
                case 2:
                    c0043n.readUTF8Str((String) null);
                    c0043n.readInt();
                    Vector vectorM1213g = NetworkUtils.m1213g();
                    int iM1328e4 = c0043n.readInt();
                    while (true) {
                        iM1328e4--;
                        if (iM1328e4 < 0) {
                            AppState.f177b[1318] = vectorM1213g;
                            break;
                        } else {
                            vectorM1213g.addElement(c0043n.readWideStr());
                        }
                    }
                case 3:
                    c0028ba.m739a(strM1338j, AppState.m584b(911), c0043n.readUTF8Str((String) null), c0043n.readWideStr(), c0043n, j);
                    break;
                case 5:
                    c0028ba.m738a(strM1338j, AppState.m584b(912), c0043n.readUTF8Str((String) null), c0043n.readWideStr(), j);
                    break;
            }
            return;
        }
        boolean z = (iM1328e2 & 2048) != 0;
        boolean z2 = (iM1328e2 & 8192) != 0;
        if ((iM1328e2 & 4) == 0) {
            c0028ba.m1052c(AppController.m321a(c0028ba, 4113, new ByteBuffer().writeStringLatin1((z2 || z) ? AppState.m584b(1052223) : strM1338j).writeIntLE(iM1328e)));
        }
        if (z2) {
            Enumeration enumerationElements = c0028ba.f321q.elements();
            while (true) {
                if (!enumerationElements.hasMoreElements()) {
                    c0035f = null;
                    break;
                }
                MrimContact c0035f2 = (MrimContact) enumerationElements.nextElement();
                if (c0035f2.m994a(strM1338j) && c0035f2 != null) {
                    c0035f = c0035f2;
                    break;
                }
            }
            MrimContact c0035f3 = c0035f;
            if (c0035f != null) {
                c0035f3.receiveMessageFull(0L, strM1215a, 1);
                return;
            }
            return;
        }
        MrimContact c0035fM717f = c0028ba.m717f(strM1338j);
        if ((iM1328e2 & 8) != 0) {
            if (c0035fM717f == null) {
                ResourceManager.m925a(3);
                c0028ba.m1072a(strM1338j, 0L, str);
                return;
            } else if ((c0035fM717f.f295b & 65536) == 0) {
                c0035fM717f.performAction();
                c0028ba.m1052c(AppController.m395b(c0028ba, strM1338j));
                return;
            } else {
                ResourceManager.m925a(3);
                c0028ba.m1072a(strM1338j, 0L, str);
                return;
            }
        }
        if ((c0035fM717f == null || c0035fM717f.hasUnread() || c0035fM717f.isOnline()) && !((iM1328e2 & 1024) == 0 && (iM1328e2 & 16384) == 0)) {
            return;
        }
        if ((iM1328e2 & 16384) != 0) {
            c0028ba.m1072a(strM1338j, j, AppState.m584b(910));
        } else if ((iM1328e2 & 1024) != 0) {
            c0028ba.m1070d(strM1338j);
        } else {
            c0028ba.m1072a(strM1338j, j, strM1215a);
        }
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:17:0x0074 */
    /* renamed from: p */
    private static final String m1111p(String str) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        String strM584b = AppState.m584b(854972);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= str.length()) {
                break;
            }
            int iIndexOf = str.indexOf(strM584b, i2);
            if (iIndexOf >= 0) {
                stringBufferM1217h.append(StringUtils.m12a(str, i2, iIndexOf));
                int i3 = iIndexOf + 13;
                int iIndexOf2 = str.indexOf(62, i3);
                if (iIndexOf2 < 0) {
                    break;
                }
                try {
                    int i4 = Integer.parseInt(StringUtils.m12a(str, i3, iIndexOf2));
                    if (i4 < 42 && i4 >= 0) {
                        stringBufferM1217h.append(AppState.m584b(i4 + 1063));
                    }
                } catch (Throwable unused) {
                }
                i = iIndexOf2 + 1;
            } else {
                stringBufferM1217h.append(StringUtils.m15c(str, i2));
                break;
            }
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: a */
    public static final void m1112a(MrimAccount c0028ba, ByteBuffer c0043n) {
        MrimContactGroup c0010aj;
        c0028ba.f324t = c0028ba.f325u;
        c0028ba.m1067K();
        int iM1328e = c0043n.readInt();
        if (iM1328e == 0) {
            int iM1328e2 = c0043n.readInt();
            String strM1334g = c0043n.readWideStr();
            String strM1334g2 = c0043n.readWideStr();
            Vector vector = c0028ba.f313i;
            int length = strM1334g.length();
            for (int i = 0; i < iM1328e2; i++) {
                int iM1328e3 = c0043n.readInt();
                String strM1335e = c0043n.readUTF8Str((String) null);
                if ((iM1328e3 & 1) == 0) {
                    vector.addElement(new MrimContactGroup(c0028ba, i, iM1328e3, strM1335e));
                }
                for (int i2 = 2; i2 < length; i2++) {
                    if (strM1334g.charAt(i2) == 'u') {
                        c0043n.readInt();
                    } else {
                        c0043n.readWideStr();
                    }
                }
            }
            int i3 = 20;
            Vector vector2 = c0028ba.f313i;
            int length2 = strM1334g2.length();
            vector2.size();
            String strM584b = AppState.m584b(1233);
            String strM584b2 = AppState.m584b(923);
            while (c0043n.length > 0) {
                int iM1328e4 = c0043n.readInt();
                int iM1328e5 = c0043n.readInt();
                String strM1338j = c0043n.readHexStr();
                String str = strM1338j;
                String strM1335e2 = c0043n.readUTF8Str(strM1338j);
                int iM1328e6 = c0043n.readInt();
                int iM1328e7 = c0043n.readInt();
                String strM1334g3 = c0043n.readWideStr();
                ByteBuffer c0043n2 = new ByteBuffer();
                if (strM1334g3 != null) {
                    for (int i4 = 0; i4 < strM1334g3.length(); i4++) {
                        char cCharAt = strM1334g3.charAt(i4);
                        if ((cCharAt == ',' && c0043n2.length > 0) || (cCharAt >= '0' && cCharAt <= '9')) {
                            c0043n2.writeByte(cCharAt);
                        }
                    }
                }
                String strM1317c = c0043n2.getStringAndClear();
                String strM1334g4 = c0043n.readWideStr();
                c0043n.readUTF8Str((String) null);
                c0043n.readUTF8Str((String) null);
                c0043n.readInt();
                String strM1334g5 = c0043n.readWideStr();
                if (StringUtils.m6a(str, strM584b) || (iM1328e4 & 1048576) != 0) {
                    str = strM584b;
                    iM1328e4 = (iM1328e4 | 1048576) & (-29);
                    if (StringUtils.m1a(strM1317c)) {
                        iM1328e4 |= 1;
                    }
                }
                if (str.endsWith(strM584b2)) {
                    iM1328e4 |= 128;
                    strM1317c = AppState.f181d;
                }
                int i5 = iM1328e4 & (-65537);
                if (0 == (i5 & 1)) {
                    Vector vector3 = c0028ba.f313i;
                    int size = vector3.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            c0010aj = null;
                            break;
                        }
                        MrimContactGroup c0010aj2 = (MrimContactGroup) vector3.elementAt(size);
                        if (c0010aj2.f74a == iM1328e5) {
                            c0010aj = c0010aj2;
                            break;
                        }
                    }
                    MrimContactGroup c0010ajM718f = c0010aj;
                    if (c0010aj == null) {
                        c0010ajM718f = c0028ba.m718f();
                    }
                    c0010ajM718f.addContact((Object) new MrimContact(c0028ba, i3, i5, iM1328e5, str, strM1335e2, iM1328e6, iM1328e7, strM1317c, strM1334g4, strM1334g5));
                }
                i3++;
                for (int i6 = 12; i6 < length2; i6++) {
                    if (i6 == 18) {
                        c0028ba.m729a(str, c0043n.readBufferArray());
                    } else if (strM1334g2.charAt(i6) == 'u') {
                        c0043n.readInt();
                    } else {
                        c0043n.readWideStr();
                    }
                }
            }
            c0028ba.f322r = 100;
            c0028ba.f323s = 100;
            c0028ba.m721d(c0028ba.f325u);
            c0028ba.m1052c(AppController.m321a(c0028ba, 4228, new ByteBuffer().writeVector((Vector) null).writeVector((Vector) null)));
            if (c0028ba.f326v == 1) {
                String strM17c = StringUtils.m17c(Utils.m522f(AppState.m584b(1382)).toLowerCase());
                if (!StringUtils.m1a(strM17c)) {
                    new AsyncTask(27, new Object[]{strM17c, c0028ba});
                }
                if (AppController.m442U() == 1) {
                    AppState.m594c(1577, 1);
                }
            }
        } else {
            IOUtils.m778d((Object) NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(458)).append(iM1328e)));
            c0028ba.m1061F();
            c0028ba.f324t = c0028ba.mo89g();
            c0028ba.m1068L();
        }
        NetworkUtils.m1174a();
    }

    /* renamed from: a */
    public static final void m1113a(boolean z, MrimAccount c0028ba) {
        String strM1215a;
        String strM1215a2;
        XmlElement c0022avM550a = XmlElement.createFromState(266953);
        XmlElement c0022avM550a2 = XmlElement.createFromState(398003);
        XmlElement c0022avM552a = c0022avM550a.addChild(c0022avM550a2);
        try {
            c0022avM550a2.addChild(new XmlElement(99).setAttrValue(262589, NetworkUtils.m1221a(5067591)).setAttrValue(329117, NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(529061)).append(AppController.m367C()).append(',').append(AppController.m368D()).append(',').append(AppController.m365A()).append(',').append(AppController.m366B()).append(',').append(0))));
        } catch (Throwable unused) {
        }
        Vector vectorM439R = AppController.m439R();
        if (c0028ba != null) {
            vectorM439R.addElement(c0028ba);
            c0028ba = null;
        }
        int iM541c = Utils.m541c(vectorM439R);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                break;
            }
            MrimAccount c0028ba2 = (MrimAccount) vectorM439R.elementAt(iM541c);
            if (c0028ba2.m1056C()) {
                c0028ba = c0028ba2;
                break;
            }
        }
        NetworkUtils.m1212a(vectorM439R);
        MrimAccount c0028ba3 = c0028ba;
        if (c0028ba3 != null) {
            strM1215a = NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(463517)).append(c0028ba3.f315k));
            strM1215a2 = NetworkUtils.m1215a(NetworkUtils.m1217h().append(AppState.m584b(725650)).append(c0028ba3.f226b));
        } else {
            String str = AppState.f181d;
            strM1215a = str;
            strM1215a2 = str;
        }
        new AsyncTask(23, new ByteBuffer().writeCompressed(5771795).writeConversationStr((Object) new ByteBuffer().writeRawString(c0022avM552a.toString()).toBase64()).writeEncodedInt(z ? 791174 : 1038).writeRawString(strM1215a2).writeRawString(strM1215a).writeCompressed(397997).writeEncodedInt(223).writeCompressed(594539).writeEncodedInt(1375).getStringAndClear());
    }

    /*  JADX ERROR: Types fix failed
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:183)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:242)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
        */
    /* JADX WARN: Failed to calculate best type for var: r3v1 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r3v1 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Not initialized variable reg: 3, insn: MOVE (r2 I:??) = (r3 I:??), block:B:8:0x005d */
    /* renamed from: g */
    public static final void m1114g(java.lang.String r7) {
        /*
            r0 = 0
            r8 = r0
            p000.AppController.m343s()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r7
            r1 = 0
            r9 = r1
            r1 = 0
            r9 = r1
            r1 = 0
            r2 = 3
            ax r0 = p000.HttpClient.m629a(r0, r1, r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0
            r8 = r1
            int r0 = r0.m634a()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto Lac
            n r0 = new n     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r9 = r0
            r0 = r7
            r1 = r9
            av r1 = r1.parseXml()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = 594557(0x9127d, float:8.33152E-40)
            av r1 = r1.findChildByKey(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = 200701(0x30ffd, float:2.81242E-40)
            av r1 = r1.findChildByKey(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r9 = r1
            boolean r0 = m1115q(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            if (r0 == 0) goto L5d
            r0 = r9
            r1 = 197037(0x301ad, float:2.76108E-40)
            java.lang.String r0 = r0.getIntAttribute(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r9
            r2 = 197041(0x301b1, float:2.76113E-40)
            java.lang.String r1 = r1.getIntAttribute(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = r9
            r3 = 529081(0x812b9, float:7.414E-40)
            java.lang.String r2 = r2.getIntAttribute(r3)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r3 = r9
            r4 = 529089(0x812c1, float:7.41412E-40)
            java.lang.String r3 = r3.getIntAttribute(r4)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r4 = r9
            r5 = 397788(0x611dc, float:5.5742E-40)
            java.lang.String r4 = r4.getIntAttribute(r5)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            p000.AppController.m450a(r0, r1, r2, r3, r4)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            goto La4
        L5d:
            r0 = r9
            r1 = 197037(0x301ad, float:2.76108E-40)
            java.lang.String r0 = r0.getIntAttribute(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            long r0 = p000.IOUtils.m807b(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0; r2 = r3;      // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r13 = r1
            r1 = r9
            r2 = 197041(0x301b1, float:2.76113E-40)
            java.lang.String r1 = r1.getIntAttribute(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            long r1 = p000.IOUtils.m808c(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r2 = r1; r2 = r3;      // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r11 = r2
            p000.MapRenderer.m649a(r0, r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r9
            r1 = 725709(0xb12cd, float:1.016935E-39)
            java.lang.String r0 = r0.getIntAttribute(r1)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            int r0 = p000.Utils.m510a(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            p000.MapRenderer.m651a(r0)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = 1
            p000.MapRenderer.f200h = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r13
            r1 = r11
            r2 = r9
            r3 = 594548(0x91274, float:8.33139E-40)
            java.lang.String r2 = r2.getIntAttribute(r3)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            int r2 = p000.Utils.m510a(r2)     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r9 = r2
            r15 = r1
            p000.ChatRenderer.f254i = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r15
            p000.ChatRenderer.f255j = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r0 = r9
            p000.ChatRenderer.f256k = r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
        La4:
            r0 = r8
            p000.HttpClient.m633a(r0)
            p000.AppController.m344t()
            return
        Lac:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
            throw r0     // Catch: java.lang.Throwable -> Lb4 java.lang.Throwable -> Ldc
        Lb4:
            r0 = r7
            r1 = 0
            r9 = r1
            boolean r0 = m1115q(r0)     // Catch: java.lang.Throwable -> Ldc
            if (r0 == 0) goto Lc9
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            p000.AppController.m450a(r0, r1, r2, r3, r4)     // Catch: java.lang.Throwable -> Ldc
            goto Ld4
        Lc9:
            r0 = 308(0x134, float:4.32E-43)
            r1 = 0
            r9 = r1
            java.lang.String r0 = p000.AppState.m584b(r0)     // Catch: java.lang.Throwable -> Ldc
            p000.IOUtils.m778d(r0)     // Catch: java.lang.Throwable -> Ldc
        Ld4:
            r0 = r8
            p000.HttpClient.m633a(r0)
            p000.AppController.m344t()
            return
        Ldc:
            r10 = move-exception
            r0 = r8
            p000.HttpClient.m633a(r0)
            p000.AppController.m344t()
            r0 = r10
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: p000.Conversation.m1114g(java.lang.String):void");
    }

    /* renamed from: q */
    private static final boolean m1115q(String str) {
        return AppState.m628b(str, 791174) > 0;
    }

    /* renamed from: h */
    public static final String m1116h(String str) {
        return m1118b(str, 959, 960);
    }

    /* renamed from: i */
    public static final String m1117i(String str) {
        return m1118b(str, 960, 959);
    }

    /* renamed from: b */
    private static final String m1118b(String str, int i, int i2) {
        String strM584b = AppState.m584b(i);
        String strM584b2 = AppState.m584b(i2);
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int length = str.length();
        for (int i3 = 0; i3 < length; i3++) {
            char cCharAt = str.charAt(i3);
            int iIndexOf = strM584b.indexOf(cCharAt);
            stringBufferM1217h.append(iIndexOf < 0 ? cCharAt : strM584b2.charAt(iIndexOf));
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: a */
    public static final String m1119a(Object obj) {
        String string = obj.toString().toString();
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        AppState.m584b(266215);
        AppState.m584b(266221);
        AppState.m584b(397287);
        AppState.m584b(397293);
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = string.charAt(i);
            if (cCharAt <= '(' || cCharAt >= 128) {
                stringBufferM1217h.append('%').append(Integer.toHexString(cCharAt));
            } else if (cCharAt == '[' || cCharAt == ']') {
                stringBufferM1217h.append('%').append(Integer.toHexString(cCharAt));
            } else {
                stringBufferM1217h.append(cCharAt);
            }
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: b */
    public static final String m1120b(Object obj) {
        String string = obj.toString();
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        String strM584b = AppState.m584b(266215);
        String strM584b2 = AppState.m584b(266221);
        String strM584b3 = AppState.m584b(397287);
        String strM584b4 = AppState.m584b(397293);
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = string.charAt(i);
            if (cCharAt == 1025) {
                stringBufferM1217h.append(strM584b3);
            } else if (cCharAt == 1105) {
                stringBufferM1217h.append(strM584b4);
            } else if (cCharAt >= 1040 && cCharAt <= 1071) {
                stringBufferM1217h.append(strM584b).append(Integer.toHexString(cCharAt - 896));
            } else if (cCharAt >= 1072 && cCharAt <= 1087) {
                stringBufferM1217h.append(strM584b).append(Integer.toHexString(cCharAt - 896));
            } else if (cCharAt >= 1088 && cCharAt <= 1103) {
                stringBufferM1217h.append(strM584b2).append(Integer.toHexString(cCharAt - 960));
            } else if ((cCharAt >= '0' && cCharAt <= '9') || (cCharAt >= 'a' && cCharAt <= 'z') || ((cCharAt >= 'A' && cCharAt <= 'Z') || cCharAt == '.')) {
                stringBufferM1217h.append(cCharAt);
            } else {
                stringBufferM1217h.append('%').append(Integer.toHexString((cCharAt >> 4) & 15)).append(Integer.toHexString(cCharAt & 15));
            }
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: a */
    public static String m1121a(int i, int i2) {
        String strM17c = StringUtils.m17c(Integer.toString(i));
        int length = strM17c.length();
        if (length >= 2) {
            return strM17c;
        }
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        for (int i3 = length; i3 < 2; i3++) {
            stringBufferM1217h.append('0');
        }
        stringBufferM1217h.append(strM17c);
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: j */
    public static final String m1122j(String str) {
        Vector vectorM512e = Utils.m512e(AppState.m584b(1511369));
        Vector vectorM512e2 = Utils.m512e(AppState.m584b(462816));
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int length = str.length();
        int length2 = 0;
        while (length2 < length) {
            char cCharAt = str.charAt(length2);
            if (cCharAt == '&') {
                boolean z = false;
                for (int i = 0; i < 4 && !z; i++) {
                    try {
                        String str2 = (String) vectorM512e.elementAt(i);
                        if (str.startsWith(str2, length2)) {
                            length2 += str2.length() - 1;
                            stringBufferM1217h.append(vectorM512e2.elementAt(i));
                            z = true;
                        }
                    } catch (Throwable unused) {
                    }
                }
                if (!z) {
                    stringBufferM1217h.append(cCharAt);
                }
            } else {
                stringBufferM1217h.append(cCharAt);
            }
            length2++;
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: k */
    public static final String m1123k(String str) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        Vector vectorM512e = Utils.m512e(AppState.m584b(4788096));
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            int i2 = (cCharAt < 1072 || cCharAt > 1103) ? cCharAt == 1105 ? 32 : (cCharAt < 1040 || cCharAt > 1071) ? cCharAt == 1025 ? 72 : -1 : (cCharAt - 1040) + 40 : cCharAt - 1072;
            int i3 = i2;
            if (i2 >= 40) {
                stringBufferM1217h.append(Utils.m521a(vectorM512e, i3 - 40).toUpperCase());
            } else if (i3 >= 0) {
                stringBufferM1217h.append(vectorM512e.elementAt(i3));
            } else {
                stringBufferM1217h.append(cCharAt);
            }
        }
        NetworkUtils.m1212a(vectorM512e);
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: l */
    public static final String m1124l(String str) {
        return m1125a(str, true);
    }

    /* renamed from: a */
    private static final String m1125a(String str, boolean z) {
        StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if ((cCharAt >= 'A' && cCharAt <= 'Z') || ((cCharAt >= 'a' && cCharAt <= 'z') || ((cCharAt >= '0' && cCharAt <= '9') || cCharAt == '.' || (cCharAt == '-' && !z)))) {
                stringBufferM1217h.append(cCharAt);
            } else if (z) {
                stringBufferM1217h.append('%').append(Integer.toHexString(cCharAt >> 4)).append(Integer.toHexString(cCharAt & 15));
            } else {
                stringBufferM1217h.append('%').append(Integer.toHexString(cCharAt >> 4).toUpperCase()).append(Integer.toHexString(cCharAt & 15).toUpperCase());
            }
        }
        return NetworkUtils.m1215a(stringBufferM1217h);
    }

    /* renamed from: a */
    public static final void m1126a(boolean z) {
        MapRenderer.f200h = true;
        AppState.m599a(41, z);
    }

    /* renamed from: a */
    public static final void m1127a() {
        MapRenderer.m651a(AppState.m586d(39) + 1);
    }

    /* renamed from: b */
    public static final void m1128b() {
        MapRenderer.m651a(AppState.m586d(39) - 1);
    }

    /* renamed from: c */
    public static final void m1129c() {
        new AsyncTask(11, MmpContact.m186o());
    }

    /* renamed from: c */
    public static final void m1130c(int i) {
        AppState.m588a(1251, NetworkUtils.m1217h().append(AppState.m584b(i)).append(' ').append('(').append(AppState.m614m(1401).size()).append(')'));
    }
}
