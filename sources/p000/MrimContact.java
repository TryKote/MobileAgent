package p000;

import java.util.Vector;

/* renamed from: f */
/* loaded from: MobileAgent_3.9.jar:f.class */
public final class MrimContact extends Contact implements ListItem {

    /* renamed from: a */
    public final int f294a;

    /* renamed from: b */
    public int f295b;

    /* renamed from: c */
    public int f296c;

    /* renamed from: d */
    public String f297d;

    /* renamed from: e */
    public int f298e;

    /* renamed from: f */
    public int f299f;

    /* renamed from: g */
    public String f300g;

    /* renamed from: h */
    public String f301h;

    /* renamed from: y */
    private long f302y;

    /* renamed from: i */
    public String f303i;

    /* renamed from: j */
    public String f304j;

    /* renamed from: k */
    public Vector f305k;

    /* renamed from: l */
    public VCard f306l;

    /* renamed from: m */
    public boolean f307m;

    /* renamed from: n */
    public SizeCache f308n;

    public MrimContact(Account abstractC0037h, int i, int i2, int i3, String str, String str2, int i4, int i5, String str3, String str4, String str5) {
        super(abstractC0037h);
        this.f294a = i;
        this.f295b = i2;
        this.f296c = i3;
        this.f297d = str;
        this.f298e = i4;
        this.f299f = i5;
        this.f300g = str3;
        this.f301h = str5;
        setDisplayName(Utils.m528a(str2, str));
        this.defaultIcon = AppController.m349a(i5, str4);
        this.highlighted = i5 != 0;
        m992O();
        this.f308n = new SizeCache();
    }

    public MrimContact() {
        super(null);
        this.f294a = 0;
        this.f308n = new SizeCache();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final String getIdentifier() {
        if (!isOffline()) {
            return this.f297d;
        }
        Vector vectorM516c = Utils.m516c(this.f300g, ',');
        String str = (String) vectorM516c.elementAt(0);
        NetworkUtils.releaseVector(vectorM516c);
        return str;
    }

    public MrimContact(Account abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.f294a = c0043n.readInt();
        String str = AppState.emptyStr;
        this.f295b = c0043n.readInt();
        this.f297d = StringUtils.intern(c0043n.readWideStr().toLowerCase());
        setDisplayName(c0043n.readUTF8Str((String) null));
        this.f298e = c0043n.readInt();
        this.f300g = c0043n.readWideStr();
        byte bM1344o = c0043n.readByte();
        this.flags = bM1344o;
        if (bM1344o != 0) {
            AppController.m414a((Contact) this);
        }
        this.f301h = str;
        this.defaultIcon = 155;
        m992O();
        this.f308n = new SizeCache();
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem createMenuItem() {
        MenuItem c0032cM896a = MenuItem.m887a(this.identifier).m896a(getIcon());
        String str = this.displayName;
        int i = canBlock() ? 3 : canDelete() ? 2 : 0;
        int i2 = this.f295b;
        c0032cM896a.m901a(str, i, (i2 & 1048576) != 0 ? 0 : (i2 & 8) != 0 ? 4 : (i2 & 4) != 0 ? 5 : this.f299f == 0 ? 0 : 3).f265d = this;
        if (!isOffline() && Utils.nonEmpty(this.f300g)) {
            c0032cM896a.m896a(27);
        }
        if (Utils.nonEmpty(this.f304j)) {
            c0032cM896a.m896a(242);
        }
        c0032cM896a.f265d = this;
        return c0032cM896a;
    }

    @Override // p000.Contact
    /* renamed from: c */
    public final void clearUnread() {
        this.defaultIcon = 155;
        this.f299f = 0;
        this.f304j = null;
        this.f303i = null;
        m999p();
        super.clearUnread();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void deserialize(ByteBuffer c0043n) {
        c0043n.writeIntLE(this.f294a).writeIntLE(this.f295b).writeStringLatin1(this.f297d).writeStringUTF16(this.displayName).writeIntLE(this.f298e).writeStringLatin1(this.f300g).writeByte(this.flags);
    }

    /* renamed from: a */
    public final void m989a(Vector vector) {
        if (vector == null) {
            NetworkUtils.releaseVector(this.f305k);
            this.f305k = null;
            return;
        }
        if (this.f305k == null) {
            this.f305k = NetworkUtils.newVector();
        }
        this.f305k.removeAllElements();
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                this.f305k.addElement(vector.elementAt(size));
            }
        }
    }

    @Override // p000.Contact
    /* renamed from: d */
    public final boolean isOffline() {
        return (this.f295b & 1048576) != 0;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int getIcon() {
        if (isOffline()) {
            return this.flags == 0 ? 27 : 16384;
        }
        if (isSystem()) {
            return this.flags == 0 ? 232 : 16616;
        }
        int iMo139e = super.getIcon();
        if (iMo139e == 16384 || iMo139e == 26) {
            return iMo139e;
        }
        if (0 != (this.f298e & 1) || isOnline()) {
            return 154;
        }
        return iMo139e;
    }

    /* renamed from: N */
    private final String m991N() {
        int iIndexOf = -1;
        try {
            iIndexOf = this.f300g.indexOf(44);
        } catch (Throwable unused) {
        }
        return iIndexOf >= 0 ? StringUtils.prefix(this.f300g, iIndexOf) : Utils.defaultStr(this.f300g);
    }

    /* renamed from: O */
    private void m992O() {
        ByteBuffer c0043nM1050q = this.account.encodeId();
        String strM991N = isOffline() ? m991N() : this.f297d;
        this.extra = strM991N;
        this.identifier = c0043nM1050q.writeRawString(strM991N).readAllByteStr();
        if (isOffline()) {
            this.extra = Utils.m530h(this.extra);
        }
        updateRenderState();
        this.account.registerContact(this);
    }

    /* JADX DEBUG: Possible override for method l.f()Ln; */
    /* renamed from: f */
    public final int m993f() {
        long jM598g = AppState.getLong(1530);
        if (jM598g - this.f302y <= 60000) {
            return 925;
        }
        this.f302y = jM598g;
        MrimAccount c0028ba = (MrimAccount) this.account;
        int iM1052c = c0028ba.trySendData(c0028ba.m719a(new Object[]{AppController.m321a(c0028ba, 4104, new ByteBuffer().writeIntLE(16512).writeStringLatin1(this.f297d).writeStringUTF16(AppState.getString(909)).writeStringLatin1(AppState.getString(33819707))), ResourceManager.m967e(14)}));
        if (0 != iM1052c) {
            return iM1052c;
        }
        appendMessage(1, AppState.getString(924), 0L, 0L);
        return 0;
    }

    /* renamed from: a */
    public final boolean m994a(String str) {
        Vector vectorM516c = Utils.m516c(this.f300g, ',');
        int size = vectorM516c.size();
        do {
            size--;
            if (size < 0) {
                NetworkUtils.releaseVector(vectorM516c);
                return false;
            }
        } while (!str.equals(vectorM516c.elementAt(size)));
        NetworkUtils.releaseVector(vectorM516c);
        return true;
    }

    @Override // p000.Contact
    /* renamed from: g */
    public final String getDefaultName() {
        return this.f300g;
    }

    @Override // p000.Contact
    /* renamed from: h */
    public final void performAction() {
        this.f298e &= -2;
    }

    @Override // p000.Contact
    /* renamed from: i */
    public final boolean canDelete() {
        return (this.f295b & 8) != 0;
    }

    @Override // p000.Contact
    /* renamed from: j */
    public final boolean canBlock() {
        return (this.f295b & 4) != 0;
    }

    @Override // p000.Contact
    /* renamed from: k */
    public final boolean canUnblock() {
        return (this.f295b & 16) != 0;
    }

    @Override // p000.Contact
    /* renamed from: l */
    public final boolean hasUnread() {
        return (this.f298e & 1) != 0;
    }

    @Override // p000.Contact
    /* renamed from: m */
    public final boolean isOnline() {
        return (this.f295b & 65536) != 0;
    }

    @Override // p000.Contact
    /* renamed from: n */
    public final boolean isSystem() {
        return (this.f295b & 128) != 0;
    }

    /* renamed from: a */
    public final void m997a(String str, String str2) {
        setDisplayName(str);
        this.f300g = str2;
        this.extra = isOffline() ? Utils.m530h(m991N()) : this.f297d;
    }

    /* JADX DEBUG: Possible override for method l.o()V */
    /* renamed from: o */
    public final String m998o() {
        try {
            return this.f306l.f16d;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: p */
    public final void m999p() {
        this.f306l = null;
        ChatRenderer.f253h = null;
        MapRenderer.f200h = true;
    }

    /* renamed from: q */
    public final boolean m1000q() {
        return this.f306l != null;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return 3;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.f307m && this.f306l != null && this.f306l.m59c();
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.f307m = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.f307m = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        try {
            return (int) this.f306l.m56a();
        } catch (Throwable unused) {
            m999p();
            return 0;
        }
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        try {
            return (int) this.f306l.m57b();
        } catch (Throwable unused) {
            m999p();
            return 0;
        }
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append(this.displayName);
        String str = this.f306l.f16d;
        if (str.length() > 0) {
            stringBufferAppend.append(',').append(' ').append(str).append('.');
        }
        return NetworkUtils.bufToStringCached(stringBufferAppend);
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        if (this.f306l != null) {
            return this.f306l.m60d();
        }
        return 10;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return this.f306l.m59c() && !this.f306l.f24l;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.f308n.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.f308n.getHeight(i, this);
    }
}
