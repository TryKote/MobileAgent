package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.ui.SizeCache;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.Utils;
public final class PhoneContact implements ListItem, Identifiable {

    // Item height type for map display
    public static final int ITEM_HEIGHT = 7;

    private int width;

    private int baseHeight;

    public String firstName;

    public String surname;

    public String phone;

    public String address;

    public int userCount;

    private String id;

    private int commandCount;

    private boolean selected = true;

    private SizeCache sizeCache = new SizeCache();

    public PhoneContact(String id, int width, int baseHeight, String surname, String firstName, String address, String phone, int userCount, int commandCount) {
        this.id = id;
        this.baseHeight = baseHeight;
        this.width = width;
        this.firstName = firstName;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.userCount = userCount;
        this.commandCount = commandCount;
    }

    @Override // p000.ListItem
    public final int getHeight() {
        return ITEM_HEIGHT;
    }

    @Override // p000.ListItem
    public final boolean isSelected() {
        return this.selected;
    }

    @Override // p000.ListItem
    public final void select() {
        this.selected = false;
    }

    @Override // p000.ListItem
    public final void deselect() {
        this.selected = true;
    }

    @Override // p000.ListItem
    public final int getWidth() {
        return this.width;
    }

    @Override // p000.ListItem
    public final int getBaseHeight() {
        return this.baseHeight;
    }

    @Override // p000.ListItem
    public final String getText() {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_PHONE_CONTACTS_PREFIX)).append(this.userCount).append(StringPool.get(StringResKeys.STR_PHONE_CONTACT_SUFFIX + Utils.pluralForm(this.userCount))).append(')'));
    }

    @Override // p000.ListItem
    public final int getCommandCount() {
        return this.commandCount;
    }

    @Override // p000.ListItem
    public final boolean isHighlighted() {
        return true;
    }

    @Override // p000.ListItem
    public final int getCommandId(int index) {
        return this.sizeCache.getWidth(index, this);
    }

    @Override // p000.ListItem
    public final int executeCommand(int index) {
        return this.sizeCache.getHeight(index, this);
    }

    @Override // p000.Identifiable
    public final String getId() {
        return this.id;
    }
}
