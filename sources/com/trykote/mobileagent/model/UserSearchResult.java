package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
/* renamed from: p */
/* loaded from: MobileAgent_3.9.jar:p.class */
public final class UserSearchResult implements ListItem, Identifiable {

    /* renamed from: e */
    private boolean selected;

    /* renamed from: f */
    private int width;

    /* renamed from: g */
    private int baseHeight;

    /* renamed from: h */
    private String description;

    /* renamed from: a */
    public String userId;

    /* renamed from: b */
    public String nickname;

    /* renamed from: c */
    public int age;

    /* renamed from: d */
    public int gender;

    /* renamed from: i */
    private int commandCount;

    /* renamed from: j */
    private SizeCache sizeCache;

    private UserSearchResult() {
    }

    public UserSearchResult(int i, int i2, String str, int i3) {
        this.width = i;
        this.baseHeight = i2;
        this.description = str;
        this.commandCount = i3;
        this.selected = true;
        this.sizeCache = new SizeCache();
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return 8;
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

    /* JADX WARN: Removed duplicated region for block: B:23:0x007c  */
    @Override // p000.ListItem
    /* renamed from: x */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final String getText() {
        int i;
        StringBuffer sb = NetworkUtils.newStringBuffer().append(Utils.nonEmpty(this.nickname) ? this.nickname : AppState.getString(StateKeys.STR_ANONYMOUS_NAME));
        if (this.age > 0) {
            StringBuffer sb2 = sb.append(',').append(' ').append(this.age);
            if (this.age >= 100) {
                i = 323;
            } else if (this.age < 5 || this.age > 20) {
                int i2 = this.age % 10;
                i = i2 == 1 ? 321 : (i2 < 2 || i2 > 4) ? 320 : 322;
            } else {
                i = 320;
            }
            sb2.append(AppState.getString(i));
        }
        if (Utils.nonEmpty(this.description)) {
            sb.append(',').append(' ').append(this.description);
        }
        return NetworkUtils.bufToStringCached(sb);
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
        return this.userId;
    }
}
