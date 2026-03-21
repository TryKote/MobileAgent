package p000;

/* renamed from: af */
/* loaded from: MobileAgent_3.9.jar:af.class */
public final class XmppContact extends Contact {

    /* renamed from: a */
    public String jabberId;

    /* renamed from: c */
    private int status;

    /* renamed from: d */
    private int unreadCount;

    /* renamed from: e */
    private String statusMessage;

    /* renamed from: f */
    private String vCardHash;

    /* renamed from: b */
    public boolean online;

    public XmppContact(XmppProtocol c0005ae, String str, String str2, String str3) {
        super(c0005ae);
        this.extra = str;
        this.jabberId = str;
        this.statusMessage = str3;
        this.unreadCount = 0;
        setDisplayName(Utils.m528a(str2, str));
        this.defaultIcon = XmppProtocol.getIconForError(this.status);
        this.identifier = c0005ae.encodeId().writeRawString(str).getStringAndClear();
        c0005ae.registerContact(this);
        updateRenderState();
    }

    @Override // p000.Contact
    /* renamed from: c */
    public final void clearUnread() {
        this.status = 0;
        this.defaultIcon = 381;
        this.statusMessage = null;
        this.vCardHash = null;
        this.unreadCount = 0;
        super.clearUnread();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final String getIdentifier() {
        return this.jabberId;
    }

    public XmppContact(Account abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.jabberId = c0043n.readWideStr();
        setDisplayName(c0043n.readUTF8Str((String) null));
        this.identifier = abstractC0037h.encodeId().writeRawString(this.jabberId).getStringAndClear();
        this.status = 0;
        this.defaultIcon = XmppProtocol.getIconForError(0);
        abstractC0037h.registerContact(this);
        updateRenderState();
        this.extra = this.jabberId;
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void deserialize(ByteBuffer c0043n) {
        c0043n.writeStringLatin1(this.jabberId).writeStringUTF16(this.displayName);
    }

    /* renamed from: o */
    private final int getDisplayIcon() {
        int iMo139e = getIcon();
        int i = iMo139e & 65535;
        return (!(this.account instanceof XmppMailRuProtocol) || i < 381 || i > 384) ? iMo139e : iMo139e + 4;
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem createMenuItem() {
        MenuItem c0032cM901a = MenuItem.create(this.identifier).setIcon(getDisplayIcon()).addText(this.displayName, 0, this.unreadCount);
        c0032cM901a.data = this;
        return c0032cM901a;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int getIcon() {
        int iMo139e = super.getIcon();
        int i = iMo139e;
        if (iMo139e == 16384) {
            return i;
        }
        if (StringUtils.m3a(262852, this.statusMessage) || StringUtils.m3a(267931, this.statusMessage) || StringUtils.m3a(202403, this.statusMessage)) {
            i = 384;
        }
        if (StringUtils.m3a(131590, this.statusMessage)) {
            i = (i & 65535) | 20578304;
        }
        return i;
    }

    @Override // p000.Contact
    /* renamed from: i */
    public final boolean canDelete() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: j */
    public final boolean canBlock() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: k */
    public final boolean canUnblock() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: m */
    public final boolean isOnline() {
        return this.online;
    }

    @Override // p000.Contact
    /* renamed from: l */
    public final boolean hasUnread() {
        return StringUtils.m3a(267931, this.statusMessage) || StringUtils.m3a(262852, this.statusMessage);
    }

    @Override // p000.Contact
    /* renamed from: h */
    public final void performAction() {
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0070  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updateFromPresence(String str, XmlElement c0022av) {
        int i = 0;
        this.vCardHash = str;
        this.status = 0;
        if (StringUtils.m3a(594984, str)) {
            XmlElement c0022avM562f = c0022av.findChildByKey(267927);
            if (c0022avM562f != null) {
                String strM11a = StringUtils.fromBuffer(c0022avM562f.textContent);
                if (StringUtils.isEmpty(strM11a)) {
                    i = 1;
                    this.status = i;
                } else {
                    if (StringUtils.m3a(265215, strM11a)) {
                        i = 4;
                    } else if (StringUtils.m3a(267829, strM11a)) {
                        i = 2;
                    } else if (StringUtils.m3a(136761, strM11a)) {
                        i = 6;
                    } else if (StringUtils.m3a(202299, strM11a)) {
                        i = 5;
                    } else if (StringUtils.m3a(202302, strM11a)) {
                        i = 3;
                    }
                    this.status = i;
                }
            }
        }
        updateFromContact(this);
    }

    /* renamed from: a */
    public final void updateFromContact(XmppContact c0006af) {
        this.status = c0006af != null ? c0006af.status : 0;
        this.vCardHash = c0006af != null ? c0006af.vCardHash : null;
        this.highlighted = this.status != 0;
        this.defaultIcon = XmppProtocol.getIconForError(this.status);
        this.unreadCount = this.status == 0 ? 0 : 3;
        this.dirty = true;
        updateRenderState();
    }

    @Override // p000.Contact
    /* renamed from: L */
    public final void mo148L() {
        AppState.clearIndex(1316);
    }

    /* renamed from: a */
    public final int sendPresence(int i) {
        int iM119a = ((XmppProtocol) this.account).updateContactPresence(this, i);
        if (iM119a != i) {
            return iM119a;
        }
        setPresenceFeature(1);
        setPresenceFeature(0);
        return i;
    }

    /* renamed from: b */
    public final void setPresenceFeature(int i) {
        ((XmppProtocol) this.account).updatePresenceStatus(this.jabberId, i);
    }

    /* JADX DEBUG: Possible override for method l.f()Ln; */
    /* renamed from: f */
    public final ContactInfo getContactInfo() {
        return new ContactInfo(this).m1298g(getDisplayIcon());
    }
}
