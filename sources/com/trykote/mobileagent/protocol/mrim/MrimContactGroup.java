package com.trykote.mobileagent.protocol.mrim;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
/* renamed from: aj */
/* loaded from: MobileAgent_3.9.jar:aj.class */
public final class MrimContactGroup extends ContactGroup {

    /* renamed from: a */
    public int serverId;

    /* renamed from: b */
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
        while (true) {
            count--;
            if (count < 0) {
                this.isSpecial = buffer.readBoolean();
                return;
            }
            addContact((Object) new MrimContact(account, buffer));
        }
    }

    public MrimContactGroup() {
        super(null);
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final void serialize(ByteBuffer buffer, boolean z) {
        buffer.writeIntLE(this.groupId);
        buffer.writeStringUTF16(this.name);
        super.serialize(buffer, z);
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int getGroupType() {
        return this.groupId;
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean isCustom() {
        return this.serverId != -1;
    }
}
