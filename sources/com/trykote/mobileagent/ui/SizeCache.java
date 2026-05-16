package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.map.MapUtils;
public final class SizeCache {

    private int cachedWidth;

    private int cachedHeight;

    public int lastScale = -1;

    public final int getWidth(int i, ListItem item) {
        if (isCached(i)) {
            return this.cachedWidth;
        }
        updateCache(i, item.getWidth(), item.getBaseHeight());
        return this.cachedWidth;
    }

    public final int getHeight(int i, ListItem item) {
        if (isCached(i)) {
            return this.cachedHeight;
        }
        updateCache(i, item.getWidth(), item.getBaseHeight());
        return this.cachedHeight;
    }

    private final void updateCache(int i, int i2, int i3) {
        this.cachedWidth = (int) MapUtils.coordToPixel(i2, i);
        this.cachedHeight = (int) MapUtils.coordToPixel(i3, i);
        this.lastScale = i;
    }

    private final boolean isCached(int i) {
        return this.lastScale == i;
    }
}
