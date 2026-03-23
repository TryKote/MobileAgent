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

/* renamed from: y */
/* loaded from: MobileAgent_3.9.jar:y.class */
public final class MergedContactGroup extends ContactGroup {

    /* renamed from: a */
    private ContactGroup sourceGroup;

    /* renamed from: b */
    private int groupType;

    public MergedContactGroup(ContactGroup group, int i) {
        super(group.account);
        this.groupType = i;
        this.sourceGroup = group;
        setName(group.name);
        this.isSpecial = group.isSpecial;
    }

    public MergedContactGroup() {
        super(null);
        this.sourceGroup = null;
    }

    @Override // p000.ContactGroup
    /* renamed from: m */
    public final int getSortIndex() {
        return this.sourceGroup.getSortIndex();
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int rename(String str) {
        return this.sourceGroup.rename(str);
    }

    @Override // p000.ContactGroup
    /* renamed from: n */
    public final int toggleSpecial() {
        if (this.isSpecial) {
            String str = this.name;
            Vector accounts = AppState.getVector(1241);
            int size = accounts.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Account acct = (Account) accounts.elementAt(size);
                int size2 = acct.groups.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        break;
                    }
                    ContactGroup groupM1082g = acct.getGroup(size2);
                    if (StringUtils.equals(str, groupM1082g.name)) {
                        groupM1082g.isSpecial = false;
                    }
                }
                if (str.equals(acct.defaultGroup.name)) {
                    acct.defaultGroup.isSpecial = false;
                }
                if (str.equals(acct.onlineGroup.name)) {
                    acct.onlineGroup.isSpecial = false;
                }
                if (str.equals(acct.specialGroup.name)) {
                    acct.specialGroup.isSpecial = false;
                }
                if (str.equals(acct.offlineGroup.name)) {
                    acct.offlineGroup.isSpecial = false;
                }
            }
        } else {
            String str2 = this.name;
            Vector accounts2 = AppState.getVector(1241);
            int size3 = accounts2.size();
            while (true) {
                size3--;
                if (size3 < 0) {
                    break;
                }
                Account acct2 = (Account) accounts2.elementAt(size3);
                int size4 = acct2.groups.size();
                while (true) {
                    size4--;
                    if (size4 < 0) {
                        break;
                    }
                    ContactGroup groupM1082g2 = acct2.getGroup(size4);
                    if (StringUtils.equals(str2, groupM1082g2.name)) {
                        groupM1082g2.isSpecial = true;
                    }
                }
                if (str2.equals(acct2.defaultGroup.name)) {
                    acct2.defaultGroup.isSpecial = true;
                }
                if (str2.equals(acct2.onlineGroup.name)) {
                    acct2.onlineGroup.isSpecial = true;
                }
                if (str2.equals(acct2.specialGroup.name)) {
                    acct2.specialGroup.isSpecial = true;
                }
                if (str2.equals(acct2.offlineGroup.name)) {
                    acct2.offlineGroup.isSpecial = true;
                }
            }
        }
        return super.toggleSpecial();
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean isCustom() {
        return this.groupType >= 0 || this.groupType < -4;
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int getGroupType() {
        return this.groupType + 8323072;
    }
}
