package p000;

import java.util.Vector;

/* renamed from: q */
/* loaded from: MobileAgent_3.9.jar:q.class */
public abstract class ContactGroup implements Sortable {

    /* renamed from: d */
    public final Account account;

    /* renamed from: e */
    public final Vector contacts = NetworkUtils.newVector();

    /* renamed from: f */
    public String name;

    /* renamed from: g */
    public boolean isSpecial;

    /* renamed from: a */
    private String nameLower;

    public ContactGroup(Account abstractC0037h) {
        this.account = abstractC0037h;
    }

    /* renamed from: a */
    public final void removeContact(Contact abstractC0041l) {
        this.contacts.removeElement(abstractC0041l);
    }

    /* renamed from: e */
    public final Contact getContact(int i) {
        return (Contact) this.contacts.elementAt(i);
    }

    /* renamed from: a */
    public void serialize(ByteBuffer c0043n, boolean z) {
        int size = this.contacts.size();
        c0043n.writeIntLE(size);
        for (int i = 0; i < size; i++) {
            getContact(i).deserialize(c0043n);
        }
        c0043n.writeBoolean(this.isSpecial);
        if (z) {
            this.contacts.removeAllElements();
        }
        Utils.m526b(this.contacts);
    }

    /* renamed from: f */
    public final MenuItem createMenuItem(int i) {
        MenuItem c0032cM896a = MenuItem.create(new ByteBuffer().writeByte(35).writeIntAsString(this.account.accountId).writeByte(35).writeIntAsString(getGroupType()).readAllByteStr()).setIcon(this.isSpecial ? 30 : 31);
        c0032cM896a.data = this;
        if (isCustom()) {
            MenuItem c0032cM901a = c0032cM896a.addText(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(this.name).append(' ').append('(')), 1, 0);
            int i2 = 0;
            int size = this.contacts.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Contact abstractC0041lM1394e = getContact(size);
                if (abstractC0041lM1394e.highlighted && !abstractC0041lM1394e.hasUnread() && !abstractC0041lM1394e.canUnblock() && !abstractC0041lM1394e.isOffline() && !abstractC0041lM1394e.isSystem()) {
                    i2++;
                }
            }
            MenuItem c0032cM901a2 = c0032cM901a.addText(StringUtils.intern(Integer.toString(i2)), 1, 20);
            StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append('/');
            int size2 = this.contacts.size();
            int i3 = size2;
            int i4 = size2;
            while (true) {
                i4--;
                if (i4 < 0) {
                    break;
                }
                Contact abstractC0041lM1394e2 = getContact(i4);
                if (abstractC0041lM1394e2.isOffline() || abstractC0041lM1394e2.hasUnread() || abstractC0041lM1394e2.canUnblock() || abstractC0041lM1394e2.isSystem()) {
                    i3--;
                }
            }
            c0032cM901a2.addText(NetworkUtils.bufToStringCached(stringBufferAppend.append(i3).append(')')), 1, 0);
        } else if (i >= 0) {
            c0032cM896a.addText(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(this.name).append(' ').append('(').append(i).append(')')), 1, 0);
        } else {
            c0032cM896a.addText(this.name, 1, 0);
        }
        return c0032cM896a;
    }

    /* renamed from: m */
    public int getSortIndex() {
        return this.account.validateGroupDelete(this);
    }

    /* renamed from: n */
    public int toggleSpecial() {
        this.isSpecial = !this.isSpecial;
        return 4;
    }

    /* renamed from: o */
    public final boolean isNotSpecial() {
        return !this.isSpecial;
    }

    /* renamed from: b */
    public int rename(String str) {
        return this.account.validateGroupRename(this, str);
    }

    /* renamed from: b */
    public final boolean containsContact(Contact abstractC0041l) {
        return this.contacts.contains(abstractC0041l);
    }

    /* renamed from: a */
    public abstract boolean isCustom();

    @Override // p000.Sortable
    /* renamed from: a */
    public final int compareTo(Object obj) {
        return this.nameLower.compareTo(((ContactGroup) obj).nameLower);
    }

    /* renamed from: b */
    public final void addContact(Object obj) {
        this.contacts.addElement(obj);
    }

    /* renamed from: c */
    public final void removeElement(Object obj) {
        this.contacts.removeElement(obj);
    }

    /* renamed from: c */
    public final void setNameIfChanged(String str) {
        if (StringUtils.equals(str, this.name)) {
            return;
        }
        setName(str);
        AppController.f152f = true;
    }

    /* renamed from: d */
    public final void setName(String str) {
        this.name = str;
        this.nameLower = StringUtils.intern(str.toLowerCase());
    }

    /* renamed from: b */
    public abstract int getGroupType();

    public final String toString() {
        return this.name;
    }
}
