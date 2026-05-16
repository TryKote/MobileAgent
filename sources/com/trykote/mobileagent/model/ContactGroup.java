package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Vector;

public abstract class ContactGroup implements Sortable {

    public final Account account;

    public final Vector contacts = ObjectPool.newVector();

    public String name;

    public boolean isSpecial;

    private String nameLower;

    public ContactGroup(Account account) {
        this.account = account;
    }

    public final void removeContact(Contact contact) {
        this.contacts.removeElement(contact);
    }

    public final Contact getContact(int index) {
        return (Contact) this.contacts.elementAt(index);
    }

    public void serialize(ByteBuffer buffer, boolean clearAfter) {
        int size = this.contacts.size();
        buffer.writeIntLE(size);
        for (int i = 0; i < size; i++) {
            getContact(i).serialize(buffer);
        }
        buffer.writeBoolean(this.isSpecial);
        if (clearAfter) {
            this.contacts.removeAllElements();
        }
        Utils.trimIfEmpty(this.contacts);
    }

    public final MenuItem createMenuItem(int memberCount) {
        MenuItem menuItem = MenuItem.create(new ByteBuffer().writeByte(35).writeIntAsString(this.account.accountId).writeByte(35).writeIntAsString(getGroupType()).readAllByteStr()).setIcon(this.isSpecial ? 30 : 31);
        menuItem.data = this;
        if (isCustom()) {
            MenuItem nameText = menuItem.addText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(this.name).append(' ').append('(')), 1, 0);
            int highlightedCount = 0;
            for (int k = this.contacts.size() - 1; k >= 0; k--) {
                Contact contact = getContact(k);
                if (contact.highlighted && !contact.hasUnread() && !contact.canUnblock() && !contact.isOffline() && !contact.isSystem()) {
                    highlightedCount++;
                }
            }
            MenuItem nameText2 = nameText.addText(StringUtils.intern(Integer.toString(highlightedCount)), 1, 20);
            StringBuffer sb = ObjectPool.newStringBuffer().append('/');
            int totalSize = this.contacts.size();
            int onlineCount = totalSize;
            for (int k = totalSize - 1; k >= 0; k--) {
                Contact ct = getContact(k);
                if (ct.isOffline() || ct.hasUnread() || ct.canUnblock() || ct.isSystem()) {
                    onlineCount--;
                }
            }
            nameText2.addText(ObjectPool.toStringAndRelease(sb.append(onlineCount).append(')')), 1, 0);
        } else if (memberCount >= 0) {
            menuItem.addText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(this.name).append(' ').append('(').append(memberCount).append(')')), 1, 0);
        } else {
            menuItem.addText(this.name, 1, 0);
        }
        return menuItem;
    }

    public int getSortIndex() {
        return this.account.validateGroupDelete(this);
    }

    public int toggleSpecial() {
        this.isSpecial = !this.isSpecial;
        return 4;
    }

    public final boolean isNotSpecial() {
        return !this.isSpecial;
    }

    public int rename(String newName) {
        return this.account.validateGroupRename(this, newName);
    }

    public final boolean containsContact(Contact contact) {
        return this.contacts.contains(contact);
    }

    public abstract boolean isCustom();

    @Override // p000.Sortable
    public final int compareTo(Object obj) {
        return this.nameLower.compareTo(((ContactGroup) obj).nameLower);
    }

    public final void addContact(Object contact) {
        this.contacts.addElement(contact);
    }

    public final void removeElement(Object contact) {
        this.contacts.removeElement(contact);
    }

    public final void setNameIfChanged(String name) {
        if (StringUtils.equals(name, this.name)) {
            return;
        }
        setName(name);
        AppController.needsLayoutUpdate = true;
    }

    public final void setName(String name) {
        this.name = name;
        this.nameLower = StringUtils.intern(name.toLowerCase());
    }

    public abstract int getGroupType();

    public final String toString() {
        return this.name;
    }
}
