package p000;

import java.util.Vector;

/* renamed from: y */
/* loaded from: MobileAgent_3.9.jar:y.class */
public final class MergedContactGroup extends ContactGroup {

    /* renamed from: a */
    private ContactGroup sourceGroup;

    /* renamed from: b */
    private int groupType;

    public MergedContactGroup(ContactGroup abstractC0046q, int i) {
        super(abstractC0046q.account);
        this.groupType = i;
        this.sourceGroup = abstractC0046q;
        setName(abstractC0046q.name);
        this.isSpecial = abstractC0046q.isSpecial;
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
            Vector vectorM614m = AppState.getVector(1241);
            int size = vectorM614m.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Account abstractC0037h = (Account) vectorM614m.elementAt(size);
                int size2 = abstractC0037h.groups.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        break;
                    }
                    ContactGroup abstractC0046qM1082g = abstractC0037h.getGroup(size2);
                    if (StringUtils.equals(str, abstractC0046qM1082g.name)) {
                        abstractC0046qM1082g.isSpecial = false;
                    }
                }
                if (str.equals(abstractC0037h.defaultGroup.name)) {
                    abstractC0037h.defaultGroup.isSpecial = false;
                }
                if (str.equals(abstractC0037h.onlineGroup.name)) {
                    abstractC0037h.onlineGroup.isSpecial = false;
                }
                if (str.equals(abstractC0037h.specialGroup.name)) {
                    abstractC0037h.specialGroup.isSpecial = false;
                }
                if (str.equals(abstractC0037h.offlineGroup.name)) {
                    abstractC0037h.offlineGroup.isSpecial = false;
                }
            }
        } else {
            String str2 = this.name;
            Vector vectorM614m2 = AppState.getVector(1241);
            int size3 = vectorM614m2.size();
            while (true) {
                size3--;
                if (size3 < 0) {
                    break;
                }
                Account abstractC0037h2 = (Account) vectorM614m2.elementAt(size3);
                int size4 = abstractC0037h2.groups.size();
                while (true) {
                    size4--;
                    if (size4 < 0) {
                        break;
                    }
                    ContactGroup abstractC0046qM1082g2 = abstractC0037h2.getGroup(size4);
                    if (StringUtils.equals(str2, abstractC0046qM1082g2.name)) {
                        abstractC0046qM1082g2.isSpecial = true;
                    }
                }
                if (str2.equals(abstractC0037h2.defaultGroup.name)) {
                    abstractC0037h2.defaultGroup.isSpecial = true;
                }
                if (str2.equals(abstractC0037h2.onlineGroup.name)) {
                    abstractC0037h2.onlineGroup.isSpecial = true;
                }
                if (str2.equals(abstractC0037h2.specialGroup.name)) {
                    abstractC0037h2.specialGroup.isSpecial = true;
                }
                if (str2.equals(abstractC0037h2.offlineGroup.name)) {
                    abstractC0037h2.offlineGroup.isSpecial = true;
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
