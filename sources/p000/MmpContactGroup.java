package p000;

/* renamed from: ap */
/* loaded from: MobileAgent_3.9.jar:ap.class */
public final class MmpContactGroup extends ContactGroup {

    /* renamed from: a */
    public int f157a;

    public MmpContactGroup(MmpProtocol c0033d, int i, String str) {
        super(c0033d);
        this.f157a = i;
        m1403c(str);
    }

    public MmpContactGroup(MmpProtocol c0033d, ByteBuffer c0043n) {
        super(c0033d);
        this.f157a = c0043n.readInt();
        m1403c(c0043n.readUTF8Str((String) null));
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                this.f399g = c0043n.readBoolean();
                return;
            }
            m1401b((Object) new MmpContact(c0033d, c0043n));
        }
    }

    public MmpContactGroup() {
        super(null);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final void mo196a(ByteBuffer c0043n, boolean z) {
        c0043n.writeIntLE(this.f157a);
        c0043n.writeStringUTF16(this.f398f);
        super.mo196a(c0043n, z);
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int mo197b() {
        return this.f157a;
    }

    /* renamed from: a */
    public final ByteBuffer m465a(String str, int i, int i2) {
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortBE(200);
        int i3 = (i2 != -1 ? 2 : 0) - (i != -1 ? 2 : 0);
        int size = this.f397e.size();
        ByteBuffer c0043nM1357m2 = c0043nM1357m.writeShortBE(i3 + (size << 1));
        for (int i4 = 0; i4 < size; i4++) {
            int i5 = ((MmpContact) m1394e(i4)).f55a;
            if (i != i5) {
                c0043nM1357m2.writeShortBE(i5);
            }
        }
        if (i2 != -1) {
            c0043nM1357m2.writeShortBE(i2);
        }
        return new ByteBuffer().writeUTF(str).writeShortBE(this.f157a).writeIntBE(1).writeBufferShortLen(c0043nM1357m2);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean mo198a() {
        return this.f157a >= 0;
    }
}
