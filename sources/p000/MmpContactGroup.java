package p000;

/* renamed from: ap */
/* loaded from: MobileAgent_3.9.jar:ap.class */
public final class MmpContactGroup extends ContactGroup {

    /* renamed from: a */
    public int groupId;

    public MmpContactGroup(MmpProtocol c0033d, int i, String str) {
        super(c0033d);
        this.groupId = i;
        setNameIfChanged(str);
    }

    public MmpContactGroup(MmpProtocol c0033d, ByteBuffer c0043n) {
        super(c0033d);
        this.groupId = c0043n.readInt();
        setNameIfChanged(c0043n.readUTF8Str((String) null));
        int iM1328e = c0043n.readInt();
        while (true) {
            iM1328e--;
            if (iM1328e < 0) {
                this.isSpecial = c0043n.readBoolean();
                return;
            }
            addContact((Object) new MmpContact(c0033d, c0043n));
        }
    }

    public MmpContactGroup() {
        super(null);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final void serialize(ByteBuffer c0043n, boolean z) {
        c0043n.writeIntLE(this.groupId);
        c0043n.writeStringUTF16(this.name);
        super.serialize(c0043n, z);
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int getGroupType() {
        return this.groupId;
    }

    /* renamed from: a */
    public final ByteBuffer createUpdatePacket(String str, int i, int i2) {
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortBE(200);
        int i3 = (i2 != -1 ? 2 : 0) - (i != -1 ? 2 : 0);
        int size = this.contacts.size();
        ByteBuffer c0043nM1357m2 = c0043nM1357m.writeShortBE(i3 + (size << 1));
        for (int i4 = 0; i4 < size; i4++) {
            int i5 = ((MmpContact) getContact(i4)).userId;
            if (i != i5) {
                c0043nM1357m2.writeShortBE(i5);
            }
        }
        if (i2 != -1) {
            c0043nM1357m2.writeShortBE(i2);
        }
        return new ByteBuffer().writeUTF(str).writeShortBE(this.groupId).writeIntBE(1).writeBufferShortLen(c0043nM1357m2);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean isCustom() {
        return this.groupId >= 0;
    }
}
