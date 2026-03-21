package p000;

import java.util.Vector;

/* renamed from: y */
/* loaded from: MobileAgent_3.9.jar:y.class */
public final class MergedContactGroup extends ContactGroup {

    /* renamed from: a */
    private ContactGroup f432a;

    /* renamed from: b */
    private int f433b;

    public MergedContactGroup(ContactGroup abstractC0046q, int i) {
        super(abstractC0046q.account);
        this.f433b = i;
        this.f432a = abstractC0046q;
        setName(abstractC0046q.name);
        this.isSpecial = abstractC0046q.isSpecial;
    }

    public MergedContactGroup() {
        super(null);
        this.f432a = null;
    }

    @Override // p000.ContactGroup
    /* renamed from: m */
    public final int getSortIndex() {
        return this.f432a.getSortIndex();
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int rename(String str) {
        return this.f432a.rename(str);
    }

    @Override // p000.ContactGroup
    /* renamed from: n */
    public final int toggleSpecial() {
        if (this.isSpecial) {
            String str = this.name;
            Vector vectorM614m = AppState.m614m(1241);
            int size = vectorM614m.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Account abstractC0037h = (Account) vectorM614m.elementAt(size);
                int size2 = abstractC0037h.f313i.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        break;
                    }
                    ContactGroup abstractC0046qM1082g = abstractC0037h.m1082g(size2);
                    if (StringUtils.m6a(str, abstractC0046qM1082g.name)) {
                        abstractC0046qM1082g.isSpecial = false;
                    }
                }
                if (str.equals(abstractC0037h.f334D.name)) {
                    abstractC0037h.f334D.isSpecial = false;
                }
                if (str.equals(abstractC0037h.f335E.name)) {
                    abstractC0037h.f335E.isSpecial = false;
                }
                if (str.equals(abstractC0037h.f338H.name)) {
                    abstractC0037h.f338H.isSpecial = false;
                }
                if (str.equals(abstractC0037h.f336F.name)) {
                    abstractC0037h.f336F.isSpecial = false;
                }
            }
        } else {
            String str2 = this.name;
            Vector vectorM614m2 = AppState.m614m(1241);
            int size3 = vectorM614m2.size();
            while (true) {
                size3--;
                if (size3 < 0) {
                    break;
                }
                Account abstractC0037h2 = (Account) vectorM614m2.elementAt(size3);
                int size4 = abstractC0037h2.f313i.size();
                while (true) {
                    size4--;
                    if (size4 < 0) {
                        break;
                    }
                    ContactGroup abstractC0046qM1082g2 = abstractC0037h2.m1082g(size4);
                    if (StringUtils.m6a(str2, abstractC0046qM1082g2.name)) {
                        abstractC0046qM1082g2.isSpecial = true;
                    }
                }
                if (str2.equals(abstractC0037h2.f334D.name)) {
                    abstractC0037h2.f334D.isSpecial = true;
                }
                if (str2.equals(abstractC0037h2.f335E.name)) {
                    abstractC0037h2.f335E.isSpecial = true;
                }
                if (str2.equals(abstractC0037h2.f338H.name)) {
                    abstractC0037h2.f338H.isSpecial = true;
                }
                if (str2.equals(abstractC0037h2.f336F.name)) {
                    abstractC0037h2.f336F.isSpecial = true;
                }
            }
        }
        return super.toggleSpecial();
    }

    @Override // p000.ContactGroup
    /* renamed from: a */
    public final boolean isCustom() {
        return this.f433b >= 0 || this.f433b < -4;
    }

    @Override // p000.ContactGroup
    /* renamed from: b */
    public final int getGroupType() {
        return this.f433b + 8323072;
    }
}
