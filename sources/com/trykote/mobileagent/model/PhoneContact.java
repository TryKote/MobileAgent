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
/* renamed from: at */
/* loaded from: MobileAgent_3.9.jar:at.class */
public final class PhoneContact implements ListItem, Identifiable {

    /* renamed from: g */
    private int width;

    /* renamed from: h */
    private int baseHeight;

    /* renamed from: a */
    public String firstName;

    /* renamed from: b */
    public String surname;

    /* renamed from: c */
    public String phone;

    /* renamed from: d */
    public String address;

    /* renamed from: e */
    public int userCount;

    /* renamed from: i */
    private String id;

    /* renamed from: j */
    private int commandCount;

    /* renamed from: f */
    private boolean selected = true;

    /* renamed from: k */
    private SizeCache sizeCache = new SizeCache();

    public PhoneContact(String str, int i, int i2, String str2, String str3, String str4, String str5, int i3, int i4) {
        this.id = str;
        this.baseHeight = i2;
        this.width = i;
        this.firstName = str3;
        this.surname = str2;
        this.phone = str5;
        this.address = str4;
        this.userCount = i3;
        this.commandCount = i4;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return 7;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.selected;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.selected = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.selected = true;
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        return this.width;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        return this.baseHeight;
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(450)).append(this.userCount).append(AppState.getString(447 + Utils.pluralForm(this.userCount))).append(')'));
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        return this.commandCount;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return true;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.sizeCache.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.sizeCache.getHeight(i, this);
    }

    @Override // p000.Identifiable
    /* renamed from: a */
    public final String getId() {
        return this.id;
    }
}
