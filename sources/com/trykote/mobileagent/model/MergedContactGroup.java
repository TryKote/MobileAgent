package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public final class MergedContactGroup extends ContactGroup {

    // Offset to distinguish merged group types from regular group IDs
    public static final int GROUP_TYPE_OFFSET = 8323072;

    private ContactGroup sourceGroup;

    private int groupType;

    public MergedContactGroup(ContactGroup group, int groupType) {
        super(group.account);
        this.groupType = groupType;
        this.sourceGroup = group;
        setName(group.name);
        this.isSpecial = group.isSpecial;
    }

    public MergedContactGroup() {
        super(null);
        this.sourceGroup = null;
    }

    @Override // p000.ContactGroup
    public final int getSortIndex() {
        return this.sourceGroup.getSortIndex();
    }

    @Override // p000.ContactGroup
    public final int rename(String str) {
        return this.sourceGroup.rename(str);
    }

    @Override // p000.ContactGroup
    public final int toggleSpecial() {
        boolean newSpecial = !this.isSpecial;
        String groupName = this.name;
        Vector accounts = Storage.state().getVector(SessionKeys.VEC_ACCOUNTS);
        for (int i = accounts.size() - 1; i >= 0; i--) {
            Account acct = (Account) accounts.elementAt(i);
            for (int j = acct.groups.size() - 1; j >= 0; j--) {
                ContactGroup group = acct.getGroup(j);
                if (StringUtils.equals(groupName, group.name)) {
                    group.isSpecial = newSpecial;
                }
            }
            if (groupName.equals(acct.defaultGroup.name)) {
                acct.defaultGroup.isSpecial = newSpecial;
            }
            if (groupName.equals(acct.onlineGroup.name)) {
                acct.onlineGroup.isSpecial = newSpecial;
            }
            if (groupName.equals(acct.specialGroup.name)) {
                acct.specialGroup.isSpecial = newSpecial;
            }
            if (groupName.equals(acct.offlineGroup.name)) {
                acct.offlineGroup.isSpecial = newSpecial;
            }
        }
        return super.toggleSpecial();
    }

    @Override // p000.ContactGroup
    public final boolean isCustom() {
        return this.groupType >= 0 || this.groupType < -4;
    }

    @Override // p000.ContactGroup
    public final int getGroupType() {
        return this.groupType + GROUP_TYPE_OFFSET;
    }
}
