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

    public ContactGroup(Account account) {
        this.account = account;
    }

    /* renamed from: a */
    public final void removeContact(Contact contact) {
        this.contacts.removeElement(contact);
    }

    /* renamed from: e */
    public final Contact getContact(int i) {
        return (Contact) this.contacts.elementAt(i);
    }

    /* renamed from: a */
    public void serialize(ByteBuffer buffer, boolean z) {
        int size = this.contacts.size();
        buffer.writeIntLE(size);
        for (int i = 0; i < size; i++) {
            getContact(i).deserialize(buffer);
        }
        buffer.writeBoolean(this.isSpecial);
        if (z) {
            this.contacts.removeAllElements();
        }
        Utils.trimIfEmpty(this.contacts);
    }

    /* renamed from: f */
    public final MenuItem createMenuItem(int i) {
        MenuItem menuItem = MenuItem.create(new ByteBuffer().writeByte(35).writeIntAsString(this.account.accountId).writeByte(35).writeIntAsString(getGroupType()).readAllByteStr()).setIcon(this.isSpecial ? 30 : 31);
        menuItem.data = this;
        if (isCustom()) {
            MenuItem nameText = menuItem.addText(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(this.name).append(' ').append('(')), 1, 0);
            int i2 = 0;
            int size = this.contacts.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                Contact contactM1394e = getContact(size);
                if (contactM1394e.highlighted && !contactM1394e.hasUnread() && !contactM1394e.canUnblock() && !contactM1394e.isOffline() && !contactM1394e.isSystem()) {
                    i2++;
                }
            }
            MenuItem nameText2 = nameText.addText(StringUtils.intern(Integer.toString(i2)), 1, 20);
            StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append('/');
            int size2 = this.contacts.size();
            int i3 = size2;
            int i4 = size2;
            while (true) {
                i4--;
                if (i4 < 0) {
                    break;
                }
                Contact contactM1394e2 = getContact(i4);
                if (contactM1394e2.isOffline() || contactM1394e2.hasUnread() || contactM1394e2.canUnblock() || contactM1394e2.isSystem()) {
                    i3--;
                }
            }
            nameText2.addText(NetworkUtils.bufToStringCached(stringBufferAppend.append(i3).append(')')), 1, 0);
        } else if (i >= 0) {
            menuItem.addText(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(this.name).append(' ').append('(').append(i).append(')')), 1, 0);
        } else {
            menuItem.addText(this.name, 1, 0);
        }
        return menuItem;
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
    public final boolean containsContact(Contact contact) {
        return this.contacts.contains(contact);
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
        AppController.needsLayoutUpdate = true;
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
