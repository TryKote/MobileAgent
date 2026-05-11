package com.trykote.mobileagent.model;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
public final class UserSearchResult implements ListItem, Identifiable {

    // Item height type for map display
    public static final int ITEM_HEIGHT = 8;

    private boolean selected;

    private int width;

    private int baseHeight;

    private String description;

    public String userId;

    public String nickname;

    public int age;

    public int gender;

    private int commandCount;

    private SizeCache sizeCache;

    private UserSearchResult() {
    }

    public UserSearchResult(int width, int baseHeight, String description, int commandCount) {
        this.width = width;
        this.baseHeight = baseHeight;
        this.description = description;
        this.commandCount = commandCount;
        this.selected = true;
        this.sizeCache = new SizeCache();
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
        int suffixKey;
        StringBuffer sb = ObjectPool.newStringBuffer().append(Utils.nonEmpty(this.nickname) ? this.nickname : ResourceAccessor.str(StringResKeys.STR_ANONYMOUS_NAME));
        if (this.age > 0) {
            StringBuffer sb2 = sb.append(',').append(' ').append(this.age);
            if (this.age >= ContactInfo.AGE_MAX_VALID) {
                suffixKey = StringResKeys.STR_AGE_UNKNOWN;
            } else if (this.age < ContactInfo.AGE_MIN_SPECIAL || this.age > ContactInfo.AGE_MAX_SPECIAL) {
                int lastDigit = this.age % 10;
                suffixKey = lastDigit == 1 ? ContactInfo.AGE_SUFFIX_SINGULAR : (lastDigit < 2 || lastDigit > 4) ? ContactInfo.AGE_SUFFIX_GENERAL : ContactInfo.AGE_SUFFIX_FEW;
            } else {
                suffixKey = ContactInfo.AGE_SUFFIX_GENERAL;
            }
            sb2.append(AppState.getString(suffixKey));
        }
        if (Utils.nonEmpty(this.description)) {
            sb.append(',').append(' ').append(this.description);
        }
        return ObjectPool.toStringAndRelease(sb);
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
        return this.userId;
    }
}
