package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
/* renamed from: s */
/* loaded from: MobileAgent_3.9.jar:s.class */
public final class SizeCache {

    /* renamed from: b */
    private int cachedWidth;

    /* renamed from: c */
    private int cachedHeight;

    /* renamed from: a */
    public int lastScale = -1;

    /* renamed from: a */
    public final int getWidth(int i, ListItem item) {
        if (isCached(i)) {
            return this.cachedWidth;
        }
        updateCache(i, item.getWidth(), item.getBaseHeight());
        return this.cachedWidth;
    }

    /* renamed from: b */
    public final int getHeight(int i, ListItem item) {
        if (isCached(i)) {
            return this.cachedHeight;
        }
        updateCache(i, item.getWidth(), item.getBaseHeight());
        return this.cachedHeight;
    }

    /* renamed from: a */
    private final void updateCache(int i, int i2, int i3) {
        this.cachedWidth = (int) MapUtils.coordToPixel(i2, i);
        this.cachedHeight = (int) MapUtils.coordToPixel(i3, i);
        this.lastScale = i;
    }

    /* renamed from: a */
    private final boolean isCached(int i) {
        return this.lastScale == i;
    }
}
