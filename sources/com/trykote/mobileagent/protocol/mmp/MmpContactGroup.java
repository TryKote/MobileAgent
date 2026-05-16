package com.trykote.mobileagent.protocol.mmp;


import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.util.ByteBuffer;
public final class MmpContactGroup extends ContactGroup {

    public int groupId;

    public MmpContactGroup(MmpProtocol protocol, int i, String str) {
        super(protocol);
        this.groupId = i;
        setNameIfChanged(str);
    }

    public MmpContactGroup(MmpProtocol protocol, ByteBuffer buffer) {
        super(protocol);
        this.groupId = buffer.readInt();
        setNameIfChanged(buffer.readUTF8Str((String) null));
        int count = buffer.readInt();
        for (int i = count - 1; i >= 0; i--) {
            addContact((Object) new MmpContact(protocol, buffer));
        }
        this.isSpecial = buffer.readBoolean();
    }

    public MmpContactGroup() {
        super(null);
    }

    @Override // p000.ContactGroup
    public final void serialize(ByteBuffer buffer, boolean z) {
        buffer.writeIntLE(this.groupId);
        buffer.writeStringUTF16(this.name);
        super.serialize(buffer, z);
    }

    @Override // p000.ContactGroup
    public final int getGroupType() {
        return this.groupId;
    }

    public final ByteBuffer createUpdatePacket(String str, int i, int i2) {
        ByteBuffer header = new ByteBuffer().writeShortBE(200);
        int i3 = (i2 != -1 ? 2 : 0) - (i != -1 ? 2 : 0);
        int size = this.contacts.size();
        ByteBuffer packet = header.writeShortBE(i3 + (size << 1));
        for (int i4 = 0; i4 < size; i4++) {
            int i5 = ((MmpContact) getContact(i4)).userId;
            if (i != i5) {
                packet.writeShortBE(i5);
            }
        }
        if (i2 != -1) {
            packet.writeShortBE(i2);
        }
        return new ByteBuffer().writeUTF(str).writeShortBE(this.groupId).writeIntBE(1).writeBufferShortLen(packet);
    }

    @Override // p000.ContactGroup
    public final boolean isCustom() {
        return this.groupId >= 0;
    }
}
