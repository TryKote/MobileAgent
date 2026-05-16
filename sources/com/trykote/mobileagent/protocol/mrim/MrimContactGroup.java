package com.trykote.mobileagent.protocol.mrim;


import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.util.ByteBuffer;
public final class MrimContactGroup extends ContactGroup {

    public int serverId;

    public int groupId;

    public MrimContactGroup(Account account, int i, int i2, String str) {
        super(account);
        this.serverId = i;
        this.groupId = i2;
        setNameIfChanged(str);
    }

    public MrimContactGroup(Account account, ByteBuffer buffer) {
        super(account);
        this.groupId = buffer.readInt();
        setNameIfChanged(buffer.readUTF8Str((String) null));
        int count = buffer.readInt();
        for (int i = count - 1; i >= 0; i--) {
            addContact((Object) new MrimContact(account, buffer));
        }
        this.isSpecial = buffer.readBoolean();
    }

    public MrimContactGroup() {
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

    @Override // p000.ContactGroup
    public final boolean isCustom() {
        return this.serverId != -1;
    }
}
