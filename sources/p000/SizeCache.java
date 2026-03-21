package p000;

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
    public final int getWidth(int i, ListItem interfaceC0044o) {
        if (isCached(i)) {
            return this.cachedWidth;
        }
        updateCache(i, interfaceC0044o.getWidth(), interfaceC0044o.getBaseHeight());
        return this.cachedWidth;
    }

    /* renamed from: b */
    public final int getHeight(int i, ListItem interfaceC0044o) {
        if (isCached(i)) {
            return this.cachedHeight;
        }
        updateCache(i, interfaceC0044o.getWidth(), interfaceC0044o.getBaseHeight());
        return this.cachedHeight;
    }

    /* renamed from: a */
    private final void updateCache(int i, int i2, int i3) {
        this.cachedWidth = (int) AppController.coordToPixel(i2, i);
        this.cachedHeight = (int) AppController.coordToPixel(i3, i);
        this.lastScale = i;
    }

    /* renamed from: a */
    private final boolean isCached(int i) {
        return this.lastScale == i;
    }
}
