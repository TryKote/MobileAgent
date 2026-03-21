package p000;

import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: ae */
/* loaded from: MobileAgent_3.9.jar:ae.class */
public class XmppProtocol extends Account {

    /* renamed from: a */
    public final Vector f30a;

    /* renamed from: f */
    private Object[] f31f;

    /* renamed from: g */
    private Object[] f32g;

    /* renamed from: b */
    public String f33b;

    /* renamed from: c */
    public int f34c;

    /* renamed from: d */
    public Object f35d;

    /* renamed from: h */
    private Throwable f36h;

    /* renamed from: e */
    public String f37e;

    public XmppProtocol(int i, String str, String str2) {
        super(i, str, str2);
        this.configFlags = 1;
        this.f30a = NetworkUtils.m1213g();
        XmppContactGroup c0036g = new XmppContactGroup(this, 0, AppState.getString(1039));
        c0036g.isSpecial = true;
        this.defaultGroup = c0036g;
        this.f33b = AppState.emptyStr;
        this.f37e = AppState.emptyStr;
    }

    @Override // p000.Account
    /* renamed from: a */
    public int getType() {
        return 2;
    }

    /* renamed from: r */
    private String m81r() {
        StringBuffer stringBufferAppend = NetworkUtils.m1217h().append('m');
        int i = this.state + 1;
        this.state = i;
        return NetworkUtils.m1215a(stringBufferAppend.append(i));
    }

    public XmppProtocol(ByteBuffer c0043n) {
        super(c0043n);
        this.f30a = NetworkUtils.m1213g();
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                break;
            } else {
                addGroup((ContactGroup) new XmppContactGroup(this, c0043n));
            }
        }
        XmppContactGroup c0036g = new XmppContactGroup(this, c0043n);
        int iM541c = Utils.m541c(c0036g.contacts);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                c0036g.isSpecial = true;
                this.defaultGroup = c0036g;
                this.f33b = c0043n.readWideStr();
                this.f34c = c0043n.readShortBE();
                this.f37e = c0043n.readWideStr();
                return;
            }
            ((XmppContact) c0036g.contacts.elementAt(iM541c)).f43b = true;
        }
    }

    @Override // p000.Account
    /* renamed from: a */
    public final Account serializeAccount(ByteBuffer c0043n, boolean z, boolean z2) {
        super.serializeAccount(c0043n, z, z2);
        c0043n.writeStringLatin1(this.f33b).writeShortBE(this.f34c).writeStringLatin1(this.f37e);
        return this;
    }

    /* renamed from: f */
    public boolean mo83f() {
        return false;
    }

    /* renamed from: j */
    public String mo84j() {
        return StringUtils.m5b(this.login);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final ContactGroup createOnlineGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(1040));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final ContactGroup createBlockedGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(1042));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final ContactGroup createOfflineGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(1041));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final ContactGroup createSpecialGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(1043));
    }

    @Override // p000.Account
    /* renamed from: g */
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        return 0;
    }

    /* renamed from: a */
    private final int m90a(byte[] bArr) {
        long j = AppState.getBool(1536) ? 25000L : 60000L;
        this.timeout = j;
        this.deadline = System.currentTimeMillis() + j;
        return sendData(new ByteBuffer().writeBytes(bArr));
    }

    /* renamed from: a */
    private int m91a(XmlElement c0022av) {
        return sendData(new ByteBuffer().writeUTFNoLen(c0022av.toString()));
    }

    /* renamed from: b */
    private int m92b(XmlElement c0022av) {
        return m91a(c0022av.setAttrValue(131550, m81r()));
    }

    /* renamed from: s */
    private final void m93s() {
        this.dataBuffer.clear();
        Object[] objArr = this.f31f;
        if (objArr != null) {
            objArr[2] = null;
            objArr[1] = null;
            objArr[0] = null;
        }
    }

    /* renamed from: a */
    public final void m94a(Throwable th) {
        if (this.progress == 2) {
            this.f36h = th;
        }
    }

    /* renamed from: a */
    public final void m95a(String str, int i) {
        if (this.progress == 2) {
            this.f34c = i;
            this.f33b = str;
        }
    }

    /* renamed from: t */
    private final boolean m96t() {
        return getType() == 2 && this.login.endsWith(AppState.getString(660807));
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x0236  */
    @Override // p000.Account
    /* renamed from: i */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void loadData() throws Throwable {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        XmlElement c0022avM576d;
        String strM11a;
        String strM11a2;
        Object[] objArr;
        switch (this.progress) {
            case 0:
                m93s();
                Object[] objArr2 = this.f32g;
                if (objArr2 != null) {
                    objArr2[0] = null;
                }
                this.f32g = null;
                this.f36h = null;
                this.msgCount = 0;
                break;
            case 1:
                this.msgCount = 10;
                if (mo83f()) {
                    if (Utils.nonEmpty(this.f37e)) {
                        this.progress = 3;
                    } else {
                        this.progress = 2;
                        if (this.login.indexOf(64) <= 0) {
                            ((XmppMailRuProtocol) this).f37e = this.login;
                            objArr = null;
                        } else {
                            String strM13b = StringUtils.prefix(Utils.m544b(), 16);
                            Object[] objArr3 = {this, strM13b, new ByteBuffer().writeCompressed(5249005).writeRawString(strM13b).readAllByteStr(), ResourceManager.f291j[0], this.login, this.password};
                            new AsyncTask(34, objArr3);
                            objArr = objArr3;
                        }
                        this.f32g = objArr;
                    }
                } else if (Utils.nonEmpty(this.f33b)) {
                    this.progress = 3;
                } else {
                    this.progress = 2;
                    Object[] objArr4 = {this};
                    new AsyncTask(33, objArr4);
                    this.f32g = objArr4;
                }
                AppController.f153g = true;
                break;
            case 2:
                this.msgCount = 20;
                if (mo83f()) {
                    if (Utils.nonEmpty(this.f37e)) {
                        this.progress = 3;
                    } else if (this.f36h != null) {
                        m98b(this.f36h);
                    }
                } else if (Utils.nonEmpty(this.f33b)) {
                    this.progress = 3;
                } else if (this.f36h != null) {
                    m98b(this.f36h);
                }
                AppController.f153g = true;
                break;
            case 3:
                this.msgCount = 30;
                this.state = 0;
                this.connection = new ConnectionThread(NetworkUtils.m1215a(NetworkUtils.m1217h().append(this.f33b).append(':').append(this.f34c)));
                this.progress = 4;
                if (m96t()) {
                    new AsyncTask(30, new Object[]{this, new ByteBuffer().writeCompressed(2365173).writeCompressed(3807001).writeRawString(this.shortName).writeCompressed(1316577).writeRawString(this.password).readAllByteStr(), ResourceManager.f291j[0]});
                }
                AppController.f153g = true;
                break;
            case 4:
                m93s();
                this.msgCount = 40;
                if (!m96t()) {
                    if (this.connection.m1131a() == 2) {
                        this.msgCount = 50;
                        this.progress = 5;
                        Object[] objArr5 = new Object[3];
                        objArr5[0] = this;
                        objArr5[1] = new ByteBuffer();
                        objArr5[2] = null;
                        objArr5[2] = new XmlParser(objArr5);
                        new AsyncTask(29, objArr5);
                        this.f31f = objArr5;
                        AppController.f153g = true;
                        m109u();
                    } else if (this.connection.m1131a() <= 0) {
                        closeConnection();
                    }
                    AppController.f153g = true;
                    break;
                } else if (this.f35d != null) {
                    if (this.f35d instanceof Throwable) {
                        handleConnError();
                        break;
                    }
                }
                break;
            default:
                this.connection.m1132a(this.dataBuffer);
                AppController.m419a(this, this.dataBuffer.length);
                Object[] objArr6 = this.f31f;
                ByteBuffer c0043n = this.dataBuffer;
                ByteBuffer c0043n2 = (ByteBuffer) objArr6[1];
                synchronized (c0043n2) {
                    c0043n2.writeBytesAt(c0043n.data, c0043n.offset, c0043n.length);
                    c0043n.clear();
                }
                XmlElement c0022av = (XmlElement) Utils.dequeue(this.f30a);
                if (c0022av != null) {
                    String str = c0022av.tagName;
                    if (!StringUtils.m3a(857301, str)) {
                        if (StringUtils.m3a(988737, str)) {
                            XmlElement c0022avM568b = c0022av.findByName(AppState.getString(660472));
                            if (c0022avM568b != null) {
                                XmlElement c0022avM569h = XmlElement.createFromState(263757).addIdAttr(2102710);
                                String strM584b = AppState.getString(660501);
                                if (c0022avM568b.findChildByText(strM584b) != null) {
                                    m91a(c0022avM569h.setAttrValue(594936, strM584b));
                                } else {
                                    String strM584b2 = AppState.getString(922626);
                                    if (c0022avM568b.findChildByText(strM584b2) != null) {
                                        m91a(c0022avM569h.setAttrValue(594936, strM584b2).appendText((Object) new ByteBuffer().writeByte(0).writeRawString(this.shortName).writeByte(0).writeRawString((String) this.f35d).toBase64()));
                                    } else {
                                        String strM584b3 = AppState.getString(332816);
                                        if (c0022avM568b.findChildByText(strM584b3) != null) {
                                            m91a(c0022avM569h.setAttrValue(594936, strM584b3).appendText((Object) new ByteBuffer().writeUTFNoLen(new ByteBuffer().writeRawString(this.shortName).writeByte(64).writeRawString(this.f33b).readAllByteStr()).writeByte(0).writeUTFNoLen(this.shortName).writeByte(0).writeUTFNoLen(this.password).toBase64()));
                                        }
                                    }
                                }
                            } else if (c0022av.findByName(AppState.getString(267762)) != null) {
                                XmlElement c0022avM570i = XmlElement.createFromState(136604).addNameAttr(198841);
                                c0022avM570i.addChildWithId(267762, 2102742).addTextChild(AppState.getString(530129), AppState.getString(264455));
                                m92b(c0022avM570i);
                                this.msgCount = 60;
                            } else {
                                IOUtils.m783a(this, 1033);
                                closeConnection();
                                this.lastError = getDefaultError();
                            }
                        } else if (StringUtils.m3a(595536, str)) {
                            XmlElement c0022avM569h2 = XmlElement.createFromState(529537).addIdAttr(2102710);
                            String strM1317c = ResourceManager.m986d(StringUtils.fromBuffer(c0022av.textContent)).getStringAndClear();
                            int iIndexOf = strM1317c.indexOf(AppState.getString(398406));
                            if (iIndexOf >= 0) {
                                int i = iIndexOf + 7;
                                String strMo128m = mo128m();
                                String str2 = this.password;
                                String str3 = this.f33b;
                                String strM12a = StringUtils.substring(strM1317c, i, strM1317c.indexOf(34, i));
                                ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(660529).writeRawString(strMo128m).writeCompressed(595003).writeRawString(str3).writeCompressed(595012).writeRawString(strM12a).writeCompressed(1446989);
                                String strM544b = Utils.m544b();
                                c0022avM569h2.appendText((Object) c0043nM1310c.writeRawString(strM544b).writeCompressed(1840227).writeRawString(str3).writeCompressed(791679).writeRawString(new ByteBuffer().writeRawString(new ByteBuffer().writeRawString(strMo128m).writeByte(58).writeRawString(str3).writeByte(58).writeRawString(str2).encryptMD5().writeByte(58).writeRawString(strM12a).writeByte(58).writeRawString(strM544b).encryptMD5().toHexString()).writeByte(58).writeRawString(strM12a).writeCompressed(660619).writeRawString(strM544b).writeByte(58).writeCompressed(263757).writeByte(58).writeRawString(new ByteBuffer().writeCompressed(1184917).writeRawString(str3).encryptMD5().toHexString()).encryptMD5().toHexString()).writeCompressed(988327).toBase64());
                            }
                            m91a(c0022avM569h2);
                        } else if (StringUtils.m3a(464473, str)) {
                            m109u();
                        } else if (StringUtils.m3a(530016, str)) {
                            String strM574a = c0022av.getNameAttr();
                            String strM584b4 = strM574a != null ? strM574a : AppState.getString(594984);
                            String strM130h = m130h(c0022av.getIntAttribute(262852));
                            if (strM130h != null) {
                                XmppContact c0006afM111f = m111f(strM130h);
                                if (StringUtils.m3a(594926, strM584b4)) {
                                    if (c0006afM111f == null) {
                                        ContactGroup abstractC0046q = this.defaultGroup;
                                        XmppContact c0006af = new XmppContact(this, strM130h, m129a(c0022av, strM130h), null);
                                        c0006af.f43b = true;
                                        c0006afM111f = c0006af;
                                        abstractC0046q.addContact((Object) c0006af);
                                    }
                                    c0006afM111f.m146a(strM584b4, c0022av);
                                    ResourceManager.m925a(3);
                                    onMessage(strM130h, 0L, AppState.getString(1031));
                                } else if (c0006afM111f != null) {
                                    c0006afM111f.m146a(strM584b4, c0022av);
                                }
                            }
                        } else if (StringUtils.m3a(464488, str)) {
                            String strM130h2 = m130h(c0022av.getIntAttribute(262852));
                            if (m111f(strM130h2) != null) {
                                StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
                                XmlElement c0022avM562f = c0022av.findChildByKey(464558);
                                if (c0022avM562f != null && (strM11a2 = StringUtils.fromBuffer(c0022avM562f.textContent)) != null) {
                                    stringBufferM1217h.append(strM11a2).append('\n');
                                }
                                XmlElement c0022avM562f2 = c0022av.findChildByKey(267946);
                                if (c0022avM562f2 != null && (strM11a = StringUtils.fromBuffer(c0022avM562f2.textContent)) != null) {
                                    stringBufferM1217h.append(strM11a);
                                }
                                String strM1215a = NetworkUtils.m1215a(stringBufferM1217h);
                                if (strM1215a.length() > 0) {
                                    onMessage(strM130h2, 0L, strM1215a);
                                }
                            }
                        } else if (StringUtils.m3a(464495, str)) {
                            handleComplete();
                        } else if (StringUtils.m3a(136604, c0022av.tagName)) {
                            if (c0022av.findByAttrs(267810, 857625) == null || !StringUtils.m3a(196633, c0022av.getNameAttr())) {
                                z = false;
                            } else {
                                XmlElement c0022avM577b = c0022av.cloneElement();
                                c0022avM577b.children = null;
                                m91a(c0022avM577b);
                                z = true;
                            }
                            if (!z && !m126c(c0022av)) {
                                if (c0022av.findByAttrs(267762, 2102742) == null || !StringUtils.m3a(398982, c0022av.getNameAttr())) {
                                    z2 = false;
                                } else {
                                    m92b(XmlElement.createFromState(136604).addNameAttr(198841).addSimpleChild(461668, 2299382));
                                    this.msgCount = 70;
                                    z2 = true;
                                }
                                if (!z2) {
                                    if (this.msgCount == 70 && StringUtils.m3a(398982, c0022av.getNameAttr())) {
                                        m92b(XmlElement.createFromState(136604).addNameAttr(196633).addSimpleChild(333360, 1054101));
                                        this.msgCount = 80;
                                        z3 = true;
                                    } else {
                                        z3 = false;
                                    }
                                    if (!z3) {
                                        XmlElement c0022avM576d2 = c0022av.findByAttrs(333360, 1119653);
                                        if (c0022avM576d2 == null || !StringUtils.m3a(196633, c0022av.getNameAttr())) {
                                            z4 = false;
                                        } else {
                                            c0022avM576d2.setIntAttribute(262601, 1119195).setIntAttribute(459728, 1375).setIntAttribute(133230, 264455);
                                            m91a(c0022av.cloneElement());
                                            z4 = true;
                                        }
                                        if (!z4 && (c0022avM576d = c0022av.findByAttrs(333360, 1054101)) != null) {
                                            String strM574a2 = c0022av.getNameAttr();
                                            if (StringUtils.m3a(198841, strM574a2)) {
                                                m127d(c0022avM576d);
                                                XmlElement c0022avM577b2 = c0022av.cloneElement();
                                                c0022avM577b2.children = null;
                                                m91a(c0022avM577b2);
                                            } else if (StringUtils.m3a(398982, strM574a2)) {
                                                removeAllContacts();
                                                m127d(c0022avM576d);
                                                if (Utils.m541c(this.groups) == 0) {
                                                    this.groups.addElement(new XmppContactGroup(this, 1, AppState.getString(459528)));
                                                }
                                                this.progress = 100;
                                                m103b(this.configFlags);
                                                this.msgCount = 100;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    AppController.f153g = true;
                    AppController.f152f = true;
                    break;
                }
                break;
        }
        if (this.lastError != 0 && this.connection != null && this.connection.m1131a() == 0) {
            this.progress = 0;
            closeConnection();
            this.lastError = getDefaultError();
        }
        if (this.timeout <= 0 || !AppController.m306a(this.deadline)) {
            return;
        }
        m90a(new byte[]{32});
    }

    /* renamed from: b */
    private void m98b(Throwable th) {
        IOUtils.m784a(this, th.toString());
        closeConnection();
        this.lastError = getDefaultError();
    }

    /* renamed from: b */
    public final void m99b(String str, int i) {
        if (isConnected()) {
            m91a(XmlElement.createFromState(530016).setAttrValue(131590, str).addNameAttr(i == 0 ? 594926 : i == 1 ? 660462 : 791532).addChild(XmlElement.createFromState(267628).addIdAttr(2037073).appendText((Object) this.displayName)));
        } else {
            IOUtils.m778d((Object) AppState.getString(299));
        }
    }

    @Override // p000.Account
    /* renamed from: c */
    public final void onError(int i) {
        int i2;
        switch (i) {
            case 0:
                i2 = 1;
                break;
            case 1:
                i2 = 4;
                break;
            case 2:
                i2 = 2;
                break;
            case 3:
                i2 = 5;
                break;
            case 4:
                i2 = 3;
                break;
            default:
                disconnect();
                return;
        }
        if (isConnected()) {
            m101h(i2);
            return;
        }
        this.configFlags = i2;
        if (isConnecting()) {
            return;
        }
        connect(0);
    }

    /* renamed from: h */
    private final void m101h(int i) {
        if (mo83f()) {
            i = 1;
        }
        this.lastError = i;
        XmlElement c0022avM550a = XmlElement.createFromState(530016);
        int i2 = 0;
        switch (i) {
            case 1:
                i2 = 642;
                break;
            case 2:
                c0022avM550a.setIntAttribute(267927, 267829);
                i2 = 644;
                break;
            case 3:
                c0022avM550a.addNameAttr(594975);
                break;
            case 4:
                c0022avM550a.setIntAttribute(267927, 265215);
                i2 = 643;
                break;
            case 5:
                c0022avM550a.setIntAttribute(267927, 202299);
                i2 = 645;
                break;
            case 6:
                c0022avM550a.setIntAttribute(267927, 136761);
                i2 = 648;
                break;
        }
        if (i2 != 0) {
            c0022avM550a.addTextChild(AppState.getString(530137), AppState.getString(65747));
            c0022avM550a.setIntAttribute(394658, i2);
            c0022avM550a.addChildWithId(267628, 2037073).appendText((Object) this.displayName);
        }
        m91a(c0022avM550a);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int setCredentials(String str, String str2) {
        int iMo102a = super.setCredentials(str, str2);
        if (iMo102a != 0) {
            return iMo102a;
        }
        if (mo83f()) {
            this.f37e = AppState.emptyStr;
            return 0;
        }
        this.f33b = AppState.emptyStr;
        return 0;
    }

    /* renamed from: b */
    public final int m103b(int i) {
        this.configFlags = i;
        if (isConnected()) {
            m101h(i);
            return 0;
        }
        if (isConnecting()) {
            return 487;
        }
        return connect(0);
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int validateContactDelete(Contact abstractC0041l) {
        return 1032;
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int validateContactBlock(Contact abstractC0041l) {
        return 1032;
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int validateContactUnblock(Contact abstractC0041l) {
        return 1032;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final Contact newContact(String str) {
        return null;
    }

    @Override // p000.Account
    /* renamed from: h */
    public int getIconId() {
        if (this.progress < 1 || this.progress >= 100) {
            return m131d(this.lastError);
        }
        return 382;
    }

    /* renamed from: u */
    private void m109u() {
        m90a(new ByteBuffer().writeCompressed(8131775).writeRawString(mo84j()).writeCompressed(136911).toByteArray());
    }

    @Override // p000.Account
    /* renamed from: p */
    public int mo110p() {
        return 398518;
    }

    /* renamed from: f */
    private XmppContact m111f(String str) {
        return (XmppContact) getContact((Object) str);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateModify(Contact abstractC0041l, Object[] objArr) {
        int iMo112a = super.validateModify(abstractC0041l, objArr);
        return 0 != iMo112a ? iMo112a : m117a(((XmppContact) abstractC0041l).f38a, (String) objArr[0], findGroup(abstractC0041l).name);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateMove(Contact abstractC0041l, ContactGroup abstractC0046q, ContactGroup abstractC0046q2) {
        int iMo113a = super.validateMove(abstractC0041l, abstractC0046q, abstractC0046q2);
        return 0 != iMo113a ? iMo113a : m117a(((XmppContact) abstractC0041l).f38a, abstractC0041l.displayName, abstractC0046q2.name);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateDelete(Contact abstractC0041l) {
        if (!isConnected()) {
            return 299;
        }
        AppState.pool[1316] = new Object[]{m81r(), ((XmppContact) abstractC0041l).m151f()};
        this.state--;
        return m92b(XmlElement.createFromState(136604).addNameAttr(196633).setAttrValue(131590, abstractC0041l.getIdentifier()).addSimpleChild(333452, 661030));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateObject(Object obj) {
        return 0;
    }

    /* renamed from: k */
    public final int m116k() {
        if (!isConnected()) {
            IOUtils.m778d((Object) AppState.getString(299));
            return 0;
        }
        String strM522f = Utils.defaultStr(AppState.getString(1296));
        m117a(strM522f, Utils.defaultStr(AppState.getString(1297)), ((ContactGroup) AppState.getVector(1324).elementAt(AppState.getInt(1507))).name);
        m99b(strM522f, 0);
        m99b(strM522f, 1);
        return 0;
    }

    /* renamed from: a */
    private final int m117a(String str, String str2, String str3) {
        XmlElement c0022avM569h = XmlElement.createFromState(333360).addIdAttr(1054101);
        XmlElement c0022avM559a = XmlElement.createFromState(267942).setAttrValue(202421, str).setAttrValue(262601, str2).setAttrValue(792248, str2 == null ? AppState.getString(399049) : null);
        if (str3 != null && !StringUtils.m3a(459528, str3)) {
            c0022avM559a.addTextChild(AppState.getString(333508), str3);
        }
        return m92b(XmlElement.createFromState(136604).addNameAttr(198841).addChild(c0022avM569h.addChild(c0022avM559a)));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateResend(Contact abstractC0041l) {
        if (isConnected()) {
            m117a(abstractC0041l.getIdentifier(), (String) null, (String) null);
            return 0;
        }
        IOUtils.m778d((Object) AppState.getString(299));
        return 0;
    }

    /* renamed from: a */
    public final int m119a(XmppContact c0006af, int i) {
        if (!isConnected()) {
            return 299;
        }
        String str = c0006af.f38a;
        String str2 = c0006af.displayName;
        ContactGroup abstractC0046qM1080g = findGroup(c0006af);
        m117a(str, str2, (abstractC0046qM1080g == this.onlineGroup || c0006af.f43b) ? AppState.getString(459528) : abstractC0046qM1080g.name);
        return i;
    }

    @Override // p000.Account
    /* renamed from: l */
    public final int disconnect() {
        int iMo120l = super.disconnect();
        if (0 != iMo120l) {
            return iMo120l;
        }
        closeConnection();
        this.lastError = getDefaultError();
        this.f32g = null;
        this.f36h = null;
        this.msgCount = 0;
        m93s();
        return 0;
    }

    /* renamed from: g */
    private XmppContactGroup m121g(String str) {
        XmppContactGroup c0036g;
        Vector vector = this.groups;
        int iM541c = Utils.m541c(vector);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            c0036g = (XmppContactGroup) vector.elementAt(iM541c);
        } while (!StringUtils.equals(str, c0036g.name));
        return c0036g;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupCreate(String str) {
        int iMo122a = super.validateGroupCreate(str);
        if (0 != iMo122a) {
            return iMo122a;
        }
        if (m121g(str) != null) {
            return 0;
        }
        this.groups.addElement(new XmppContactGroup(this, 1, str));
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupDelete(ContactGroup abstractC0046q) {
        int iMo123a = super.validateGroupDelete(abstractC0046q);
        if (0 != iMo123a) {
            return iMo123a;
        }
        this.groups.removeElement(abstractC0046q);
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupRename(ContactGroup abstractC0046q, String str) {
        int iMo124a = super.validateGroupRename(abstractC0046q, str);
        if (0 != iMo124a) {
            return iMo124a;
        }
        if (Utils.m541c(abstractC0046q.contacts) != 0) {
            return 1032;
        }
        abstractC0046q.setNameIfChanged(str);
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateSend(Contact abstractC0041l, String str, long j) {
        int iMo125a = super.validateSend(abstractC0041l, str, j);
        if (0 != iMo125a) {
            return iMo125a;
        }
        this.sentCount++;
        return m91a(XmlElement.createFromState(464488).setAttrValue(131590, abstractC0041l.getIdentifier()).addNameAttr(265215).addChild(XmlElement.createFromState(267946).appendText((Object) str)).addSimpleChild(398993, 2430320));
    }

    /* renamed from: c */
    private final boolean m126c(XmlElement c0022av) {
        if (c0022av.findByAttrs(333350, 661030) == null) {
            return false;
        }
        if (!StringUtils.m3a(398982, c0022av.getNameAttr())) {
            if (!StringUtils.m3a(333441, c0022av.getNameAttr())) {
                return false;
            }
            try {
                Object[] objArrM609l = AppState.getObjectArray(1316);
                if (((String) objArrM609l[0]).equals(c0022av.getIntAttribute(131550))) {
                    AppState.pool[1315] = ((ContactInfo) objArrM609l[1]).m1297y(c0022av.toString());
                }
                return true;
            } catch (Throwable unused) {
                return true;
            }
        }
        try {
            Object[] objArrM609l2 = AppState.getObjectArray(1316);
            if (((String) objArrM609l2[0]).equals(c0022av.getIntAttribute(131550))) {
                ContactInfo c0042mM1297y = ((ContactInfo) objArrM609l2[1]).m1297y(NetworkUtils.m1215a(m133a(NetworkUtils.m1217h(), c0022av)));
                Image imageM132e = m132e(c0022av);
                if (imageM132e != null) {
                    c0042mM1297y.put(ResourceManager.m967e(25), imageM132e);
                }
                AppState.pool[1315] = c0042mM1297y;
            }
            return true;
        } catch (Throwable unused2) {
            return true;
        }
    }

    /* renamed from: d */
    private final void m127d(XmlElement c0022av) {
        Vector vector = this.groups;
        Vector vector2 = c0022av.children;
        int iM541c = Utils.m541c(vector2);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                return;
            }
            XmlElement c0022av2 = (XmlElement) vector2.elementAt(iM541c);
            if (StringUtils.m3a(267942, c0022av2.tagName)) {
                String strM554b = c0022av2.getIntAttribute(202421);
                String strM554b2 = c0022av2.getIntAttribute(792248);
                c0022av2.getIntAttribute(202403);
                String strM554b3 = c0022av2.getIntAttribute(262601);
                boolean zM3a = StringUtils.m3a(399049, strM554b2);
                if (strM554b3 == null) {
                    strM554b3 = strM554b;
                }
                String strM575c = c0022av2.getChildText(AppState.getString(333508));
                String strM584b = strM575c;
                if (!Utils.nonEmpty(strM575c)) {
                    strM584b = AppState.getString(459528);
                }
                XmppContact c0006af = (XmppContact) getContact((Object) strM554b);
                removeContact(c0006af, zM3a);
                if (!zM3a) {
                    XmppContactGroup c0036gM121g = m121g(strM584b);
                    XmppContactGroup c0036g = c0036gM121g;
                    if (c0036gM121g == null) {
                        XmppContactGroup c0036g2 = new XmppContactGroup(this, 1, strM584b);
                        c0036g = c0036g2;
                        vector.addElement(c0036g2);
                    }
                    XmppContact c0006af2 = new XmppContact(this, strM554b, strM554b3, strM554b2);
                    c0036g.addContact((Object) c0006af2);
                    c0006af2.m147a(c0006af);
                }
            }
        }
    }

    /* renamed from: m */
    public String mo128m() {
        return this.shortName;
    }

    /* renamed from: a */
    private static String m129a(XmlElement c0022av, String str) {
        try {
            return StringUtils.fromBuffer(c0022av.findChildByKey(267628).textContent);
        } catch (Throwable unused) {
            return str;
        }
    }

    /* renamed from: h */
    private static String m130h(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf(47);
        return iIndexOf <= 0 ? str : StringUtils.prefix(str, iIndexOf);
    }

    /* renamed from: d */
    public static final int m131d(int i) {
        switch (i) {
            case 0:
                return 381;
            case 1:
                return 383;
            case 2:
                return 16318847;
            case 3:
                return 16515455;
            case 4:
                return 16384383;
            case 5:
                return 16449919;
            default:
                return 16580991;
        }
    }

    /* renamed from: e */
    private final Image m132e(XmlElement c0022av) {
        Image imageM132e;
        String strM11a;
        if (StringUtils.m3a(398966, c0022av.tagName) && (strM11a = StringUtils.fromBuffer(c0022av.textContent)) != null) {
            String strM534k = Utils.m534k(strM11a);
            if (Utils.nonEmpty(strM534k)) {
                try {
                    return ResourceManager.m986d(strM534k).toImage();
                } catch (Throwable unused) {
                }
            }
        }
        int iM541c = Utils.m541c(c0022av.children);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            imageM132e = m132e(c0022av.getChildAt(iM541c));
        } while (imageM132e == null);
        return imageM132e;
    }

    /* renamed from: a */
    private final StringBuffer m133a(StringBuffer stringBuffer, XmlElement c0022av) {
        if (!StringUtils.m3a(333436, c0022av.tagName)) {
            String strM11a = StringUtils.fromBuffer(c0022av.textContent);
            if (strM11a != null) {
                String strM534k = Utils.m534k(strM11a);
                if (Utils.nonEmpty(strM534k)) {
                    stringBuffer.append(strM534k).append('\n');
                }
            }
            for (int i = 0; i < Utils.m541c(c0022av.children); i++) {
                m133a(stringBuffer, c0022av.getChildAt(i));
            }
        }
        return stringBuffer;
    }
}
