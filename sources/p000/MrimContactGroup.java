package p000;

/* renamed from: aj */
/* loaded from: MobileAgent_3.9.jar:aj.class */
public final class MrimContactGroup extends ContactGroup {

    /* renamed from: a */
    public int f74a;

    /* renamed from: b */
    public int f75b;

    public MrimContactGroup(Account abstractC0037h, int i, int i2, String str) {
        super(abstractC0037h);
        this.f74a = i;
        this.f75b = i2;
        setNameIfChanged(str);
    }

    public MrimContactGroup(Account abstractC0037h, ByteBuffer c0043n) {
        super(abstractC0037h);
        this.f75b = c0043n.readInt();
        setNameIfChanged(c0043n.readUTF8Str((String) null));
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                this.isSpecial = c0043n.readBoolean();
                return;
            }
            addContact((Object) new MrimContact(abstractC0037h, c0043n));
        }
    }

    public MrimContactGroup() {
        super(null);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final void serialize(ByteBuffer c0043n, boolean z) {
        c0043n.writeIntLE(this.f75b);
        c0043n.writeStringUTF16(this.name);
        super.serialize(c0043n, z);
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int getGroupType() {
        return this.f75b;
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean isCustom() {
        return this.f74a != -1;
    }
}
