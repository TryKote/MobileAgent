package p000;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/* renamed from: am */
/* loaded from: MobileAgent_3.9.jar:am.class */
public final class Screen {

    /* renamed from: a */
    public int screenId;

    /* renamed from: b */
    public int containerWidth;

    /* renamed from: c */
    public int containerHeight;

    /* renamed from: d */
    public int screenType;

    /* renamed from: e */
    public int offsetX;

    /* renamed from: f */
    public int offsetY;

    /* renamed from: g */
    public int screenFlags;

    /* renamed from: C */
    private MenuItem headerItem;

    /* renamed from: h */
    public final int layoutMode;

    /* renamed from: i */
    public boolean selectable;

    /* renamed from: D */
    private boolean hasScrollbar;

    /* renamed from: j */
    public int scrollOffset;

    /* renamed from: k */
    public int selectedIndex;

    /* renamed from: l */
    public int totalHeight;

    /* renamed from: m */
    public Vector menuItems;

    /* renamed from: n */
    public int[] layoutCache;

    /* renamed from: o */
    public Vector tabItems;

    /* renamed from: E */
    private int innerWidth;

    /* renamed from: F */
    private int headerHeight;

    /* renamed from: p */
    public int contentTop;

    /* renamed from: q */
    public int contentWidth;

    /* renamed from: r */
    public int contentHeight;

    /* renamed from: G */
    private int borderWidth;

    /* renamed from: H */
    private int contentStart;

    /* renamed from: I */
    private int contentBottom;

    /* renamed from: J */
    private int scrollRange;

    /* renamed from: s */
    public int softKeyLeft;

    /* renamed from: t */
    public int softKeyCenter;

    /* renamed from: u */
    public int softKeyRight;

    /* renamed from: v */
    public String titleLeft;

    /* renamed from: w */
    public String titleRight;

    /* renamed from: x */
    public boolean showCheckboxes;

    /* renamed from: K */
    private int titleMaxWidth;

    /* renamed from: y */
    public boolean reverseScroll;

    /* renamed from: L */
    private int visibleExpandedIndex;

    /* renamed from: M */
    private int expandDirection;

    /* renamed from: z */
    public int marginLeft;

    /* renamed from: A */
    public int marginTop;

    /* renamed from: B */
    public boolean touchConsumed;

    public Screen() {
        this.layoutMode = 2;
        this.screenId = 63;
    }

    /* renamed from: a */
    public final Screen initTabs() {
        if (AppState.getBool(245)) {
            this.tabItems = NetworkUtils.newVector();
            recalcLayout();
        }
        return this;
    }

    private Screen(int i, int i2, int i3, int i4) {
        this.menuItems = NetworkUtils.newVector();
        this.layoutCache = new int[16];
        this.layoutMode = i;
        this.containerWidth = i3;
        this.containerHeight = i4;
        this.screenId = i2;
        this.selectedIndex = -1;
        recalcLayout();
    }

    public Screen(int i, int i2, int i3, int i4, boolean z) {
        this(i, i2, i3, i4);
        this.selectable = z;
    }

    /* renamed from: s */
    private final Screen recalcLayout() {
        int i = this.containerWidth - 4;
        int i2 = this.containerHeight - 4;
        this.innerWidth = this.containerWidth - 2;
        if (this.headerItem != null) {
            this.headerHeight = this.headerItem.getTotalHeight() + 2;
        } else {
            this.headerHeight = 0;
        }
        this.borderWidth = this.containerWidth - 3;
        this.contentStart = 1 + this.headerHeight;
        this.contentBottom = (i2 - this.contentStart) + 3;
        this.contentTop = this.contentStart + 1;
        this.contentWidth = i - 2;
        this.contentHeight = this.contentBottom - 2;
        if (this.tabItems != null) {
            int i3 = this.contentBottom;
            int iM502a = Utils.max(AppState.getInt(1450), 16) + 3;
            this.contentBottom = i3 - iM502a;
            this.contentHeight -= iM502a;
        }
        return this;
    }

    /* renamed from: b */
    public final String getSelectedTitle() {
        if (!this.selectable || this.menuItems.size() <= 0 || this.selectedIndex < 0) {
            return null;
        }
        return getItemAt(this.selectedIndex).title;
    }

    /* renamed from: c */
    public final int getSelectedWidth() {
        if (!this.selectable || this.menuItems.size() <= 0) {
            return 200;
        }
        return getItemAt(this.selectedIndex).width;
    }

    /* renamed from: d */
    public final MenuItem getSelectedItem() {
        if (!this.selectable || this.menuItems.size() <= 0) {
            return null;
        }
        return getItemAt(this.selectedIndex);
    }

    /* renamed from: e */
    public final MenuItem getHeaderItem() {
        int iM261v;
        if (this.screenType != 9 || (iM261v = findVisibleExpanded()) >= this.menuItems.size()) {
            return null;
        }
        return getItemAt(iM261v);
    }

    /* renamed from: a */
    public final Screen setHeader(int i, String str) {
        this.headerItem = MenuItem.createSeparator().addText(AppState.getString(1037), 1, 0).setLabelInternal(i, str, 1, 0);
        recalcLayout();
        return this;
    }

    /* renamed from: a */
    public final Screen addItem(MenuItem c0032c) {
        if (this.layoutMode == 0) {
            c0032c.layout(this.contentWidth);
            this.menuItems.addElement(c0032c);
            this.layoutCache = AppController.resizeArray(this.layoutCache, 0, this.totalHeight);
            this.totalHeight += c0032c.getTotalHeight();
        } else {
            int iM240c = 0;
            int iM241d = 0;
            int size = this.menuItems.size() - 1;
            if (this.menuItems.size() > 0) {
                iM240c = getItemX(size) + getItemAt(size).getTotalWidth();
                iM241d = getItemY(size);
            }
            if (iM240c > 0 && iM240c + c0032c.getTotalWidth() >= this.contentWidth) {
                iM240c = 0;
                iM241d = getItemY(size) + getItemAt(size).getTotalHeight();
            }
            this.menuItems.addElement(c0032c);
            this.layoutCache = AppController.resizeArray(this.layoutCache, iM240c, iM241d);
            this.totalHeight = iM241d + c0032c.getTotalHeight();
        }
        if (this.totalHeight <= 0 || this.totalHeight < this.contentHeight) {
            this.hasScrollbar = false;
        } else {
            this.scrollRange = ((this.contentBottom - 4) * this.contentHeight) / this.totalHeight;
            this.hasScrollbar = true;
        }
        if (this.selectable && this.selectedIndex < 0 && c0032c.isEnabled()) {
            this.selectedIndex = this.menuItems.size() - 1;
        }
        return this;
    }

    /* renamed from: f */
    public final Screen buildLayout() {
        if (this.contentHeight - this.totalHeight > 5 && this.layoutMode == 0) {
            this.containerHeight = this.headerHeight + this.totalHeight + 4;
            recalcLayout();
        }
        int i = 0;
        Enumeration enumerationElements = this.menuItems.elements();
        while (enumerationElements.hasMoreElements()) {
            int i2 = i;
            int iM910f = ((MenuItem) enumerationElements.nextElement()).getMaxHeight();
            if (i2 < iM910f) {
                i = iM910f;
            }
        }
        int i3 = i + 16;
        if (i3 < this.containerWidth) {
            this.containerWidth = i3;
        }
        recalcLayout();
        return this;
    }

    /* JADX WARN: Removed duplicated region for block: B:198:0x0363 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x0352  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0356  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void paint(GraphicsContext c0012al, boolean z, boolean z2) {
        int iM417c;
        boolean z3 = false;
        if (this.layoutMode != 2) {
            int i = this.offsetX;
            int i2 = this.offsetY;
            int i3 = this.containerWidth;
            int i4 = this.containerHeight;
            int i5 = z ? 1 : 2;
            Graphics graphics = c0012al.graphics;
            c0012al.setColorFromPalette(i5);
            graphics.fillRect(i, i2, i3, i4);
            c0012al.setColorFromPalette(16);
            graphics.drawRect(i, i2, i3 - 1, i4 - 1);
            if (this.headerItem != null) {
                int i6 = this.offsetX + 1;
                int i7 = this.offsetY + 1;
                c0012al.setClip(i6, i7, this.innerWidth, this.headerHeight);
                int iM586d = AppState.getInt(72);
                int iM586d2 = AppState.getInt(5042 + iM586d);
                if (iM586d2 != AppState.getInt(iM586d + 5082)) {
                    for (int i8 = 1; i8 < this.headerHeight; i8++) {
                        c0012al.setColor(((255 - ((i8 * (255 - (iM586d2 >> 16))) / this.headerHeight)) << 16) | ((255 - ((i8 * (255 - ((iM586d2 >> 8) & 255))) / this.headerHeight)) << 8) | (255 - ((i8 * (255 - (iM586d2 & 255))) / this.headerHeight)));
                        c0012al.drawRect(i6, i7 + i8, this.innerWidth, 0);
                    }
                } else {
                    c0012al.setColor(iM586d2);
                    c0012al.fillRect(i6, i7, this.innerWidth, this.headerHeight);
                }
                this.headerItem.render(c0012al, i6, i7, 0);
            }
            int i9 = this.offsetX + 2;
            int i10 = this.offsetY + this.contentTop;
            int i11 = this.hasScrollbar ? this.contentWidth : this.contentWidth + 2;
            int size = this.menuItems.size();
            boolean z4 = this.layoutMode != 0;
            int i12 = this.scrollOffset;
            for (int i13 = 0; i13 < size; i13++) {
                int iM241d = getItemY(i13);
                if (iM241d - i12 > this.contentHeight) {
                    break;
                }
                MenuItem c0032cM239b = getItemAt(i13);
                int iM912h = c0032cM239b.getTotalHeight();
                if (iM241d + iM912h >= i12) {
                    int iM240c = getItemX(i13);
                    int iM911g = c0032cM239b.getTotalWidth();
                    c0012al.setClip(i9, i10, i11, this.contentHeight);
                    int i14 = i9 + iM240c;
                    int i15 = (i10 + iM241d) - i12;
                    int i16 = this.layoutMode == 0 ? i11 : iM911g;
                    int i17 = c0032cM239b.id;
                    if (this.selectable && i13 == this.selectedIndex && i17 != 11) {
                        c0012al.setColorFromPalette(13);
                        c0012al.fillRect(i14, i15, i16, iM912h);
                    }
                    if (i17 == 13 && c0032cM239b.visible) {
                        c0012al.setColorFromPalette(13);
                        c0012al.fillRect(i14, i15, i16, iM912h);
                    }
                    if (z4) {
                        int i18 = iM912h;
                        int i19 = i16;
                        int i20 = i15;
                        int i21 = i14;
                        int i22 = i21 + i19;
                        Graphics graphics2 = c0012al.graphics;
                        int clipX = graphics2.getClipX();
                        if (i22 >= clipX) {
                            int i23 = i20 + i18;
                            int clipY = graphics2.getClipY();
                            if (i23 >= clipY) {
                                int clipWidth = graphics2.getClipWidth();
                                if (i21 <= clipX + clipWidth) {
                                    int clipHeight = graphics2.getClipHeight();
                                    if (i20 > clipY + clipHeight) {
                                        z3 = false;
                                        if (!z3) {
                                            c0032cM239b.render(c0012al, i14, i15, i11);
                                        }
                                    } else {
                                        if (clipX + clipWidth < i21 + i19) {
                                            i19 = (clipX + clipWidth) - i21;
                                        }
                                        if (clipY + clipHeight < i20 + i18) {
                                            i18 = (clipY + clipHeight) - i20;
                                        }
                                        if (clipX > i21) {
                                            int i24 = clipX - i21;
                                            i21 = clipX;
                                            i19 -= i24;
                                        }
                                        if (i19 > 0) {
                                            if (clipY > i20) {
                                                int i25 = clipY - i20;
                                                i20 = clipY;
                                                i18 -= i25;
                                            }
                                            if (i18 > 0) {
                                                graphics2.setClip(i21, i20, i19, i18);
                                                z3 = true;
                                            }
                                            if (!z3) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (this.hasScrollbar) {
                int i26 = this.offsetX + this.borderWidth;
                int i27 = this.offsetY + this.contentStart;
                c0012al.setClip(i26, i27, 7, this.contentBottom + 4);
                c0012al.setColorFromPalette(16);
                c0012al.fillRect(i26 + 1, i27 + (this.totalHeight == 0 ? 0 : Utils.min(((this.contentBottom - 4) * this.scrollOffset) / this.totalHeight, (this.contentBottom - 4) - this.scrollRange)), 1, this.scrollRange + 2);
                c0012al.drawRect(i26, i27 - 1, 2, this.contentBottom + 1);
            }
            if (this.tabItems != null) {
                int iM502a = Utils.max(AppState.getInt(1450), 16);
                int iM605e = AppState.getHeight() - 1;
                int iM586d3 = AppState.getInt(1528);
                c0012al.setClip(0, (iM605e - iM502a) - 3, iM586d3, iM502a + 4).setColorFromPalette(16).fillRect(0, (iM605e - iM502a) - 3, iM586d3, iM502a + 4).setColorFromPalette(17).fillRect(1, (iM605e - iM502a) - 2, iM586d3 - 2, iM502a + 2).setColorFromPalette(0).setFont(AppState.getGfxContext(0));
                Vector vector = this.tabItems;
                int i28 = 3;
                boolean z5 = false;
                int iM73f = ((iM605e - iM502a) - 1) + ScreenManager.getCenterOffset();
                for (int i29 = 0; i29 < vector.size(); i29++) {
                    Object objElementAt = vector.elementAt(i29);
                    if (!(objElementAt instanceof Integer)) {
                        z5 = true;
                        c0012al.drawString((String) objElementAt, i28, (iM605e - iM502a) - 1, 20);
                        i28 = iM586d3;
                    } else if (z5) {
                        i28 -= 18;
                        c0012al.drawIcon(((Integer) objElementAt).intValue(), i28, iM73f);
                    } else {
                        c0012al.drawIcon(((Integer) objElementAt).intValue(), 3, iM73f);
                        i28 += 18;
                    }
                }
            }
            if (z && AppState.getBool(71)) {
                int iM586d4 = AppState.getInt(1528);
                int iM605e2 = AppState.getHeight();
                c0012al.setClip(0, 0, iM586d4, 2048 + iM605e2);
                c0012al.setFont(AppState.getGfxContext(0));
                c0012al.setColorFromPalette(15);
                if (this.titleLeft != null) {
                    c0012al.drawString(this.titleLeft, 1, iM605e2, 20);
                }
                if (this.titleRight != null) {
                    c0012al.drawString(this.titleRight, iM586d4 - 1, iM605e2, 24);
                }
                if (ResourceManager.clockWidth + this.titleMaxWidth < iM586d4 - 6) {
                    c0012al.drawString(Utils.defaultStr(AppState.getString(1263)), iM586d4 >> 1, iM605e2, 17);
                }
            }
        }
        if (this.screenType == 1 || this.screenType == 12) {
            c0012al.setFont(AppState.getGfxContext(1));
            TabBar c0008ah = (TabBar) AppState.getVector(1246).elementAt(TabBar.currentIndex);
            Vector vectorM614m = AppState.getVector(1245);
            int size2 = vectorM614m.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                Object objElementAt2 = vectorM614m.elementAt(size2);
                if (objElementAt2 instanceof TabBar) {
                    TabBar c0008ah2 = (TabBar) objElementAt2;
                    boolean z6 = objElementAt2 == c0008ah && !TabBar.scrollEnabled;
                    GraphicsContext c0012alM207b = c0012al.setColorFromPalette(16);
                    int i30 = c0008ah2.xOffset;
                    int i31 = c0008ah2.width;
                    int iM623o = AppState.getIntOffset(1) + 7;
                    c0012alM207b.setClip(i30, 2, i31, iM623o - 2).drawLine(c0008ah2.xOffset, iM623o, c0008ah2.xOffset, 6).drawLine(c0008ah2.xOffset, 6, c0008ah2.xOffset + 4, 2).drawLine(c0008ah2.xOffset + 4, 2, (c0008ah2.xOffset + c0008ah2.width) - 2, 2).drawLine((c0008ah2.xOffset + c0008ah2.width) - 2, 2, (c0008ah2.xOffset + c0008ah2.width) - 2, iM623o).setColorFromPalette(z6 ? 1 : 17);
                    int i32 = z6 ? iM623o : iM623o - 1;
                    int i33 = 3;
                    while (i33 < i32) {
                        c0012al.drawLine(c0008ah2.xOffset + 1 + (i33 < 6 ? 6 - i33 : 0), i33, (c0008ah2.xOffset + c0008ah2.width) - 3, i33);
                        i33++;
                    }
                    if (c0008ah2.account == null) {
                        int i34 = c0008ah2.iconId;
                        iM417c = (i34 == 240 && AppController.hasActiveConnection()) ? 16385 : (i34 == 240 || i34 == 264 || AppState.getVector(1243).size() <= 0) ? i34 : 16384;
                    } else {
                        iM417c = AppController.getAccountStatus(c0008ah2.account);
                    }
                    c0012al.drawIcon(iM417c, c0008ah2.xOffset + 4, 4 + ScreenManager.getCenterOffset()).setColorFromPalette(0).setClip(c0008ah2.xOffset, 2, c0008ah2.width - 3, iM623o - 2).drawString(c0008ah2.title, c0008ah2.xOffset + 6 + 16, 4, 20);
                } else {
                    int[] iArr = (int[]) objElementAt2;
                    int i35 = iArr[0];
                    int iM73f2 = 4 + ScreenManager.getCenterOffset();
                    c0012al.setClip(i35, iM73f2, 16, 16);
                    c0012al.drawIcon(iArr[1], i35, iM73f2);
                }
            }
        }
        if (this.screenId == 6) {
            int i36 = this.offsetX + 2;
            int i37 = this.offsetY + this.contentTop;
            c0012al.setClip(i36, i37, this.containerWidth, this.contentHeight);
            try {
                int iM586d5 = AppState.getInt(1415);
                int iM586d6 = AppState.getInt(1416);
                Graphics graphics3 = c0012al.graphics;
                graphics3.drawImage(AppState.getImage(1364), iM586d5 >> 1, i37 + (iM586d6 >> 1), 3);
                if (!AppState.getBool(1414) && AppState.getBool(1535)) {
                    int[] iArr2 = new int[iM586d5];
                    int i38 = iM586d5;
                    while (true) {
                        i38--;
                        if (i38 < 0) {
                            break;
                        } else {
                            iArr2[i38] = 1006632960;
                        }
                    }
                    while (true) {
                        iM586d6--;
                        if (iM586d6 < 0) {
                            break;
                        } else {
                            graphics3.drawRGB(iArr2, 0, iM586d5, 0, i37 + iM586d6, iM586d5, 1, true);
                        }
                    }
                }
            } catch (Throwable unused) {
            }
            AppState.setInt(1553, 0);
            return;
        }
        if (this.screenId != 4) {
            return;
        }
        c0012al.setClip(this.offsetX + 2, this.offsetY + this.contentTop, this.containerWidth, this.contentHeight);
        int iM586d7 = AppState.getInt(1408);
        if (iM586d7 <= 0) {
            return;
        }
        c0012al.setFont(AppState.getGfxContext(0));
        int iM605e3 = AppState.getHeight() - 1;
        int iM586d8 = AppState.getInt(1528);
        c0012al.setClip(0, (iM605e3 - iM586d7) - 1, iM586d8, iM586d7 + 1);
        c0012al.setColorFromPalette(16);
        c0012al.fillRect(0, (iM605e3 - iM586d7) - 1, iM586d8, iM586d7 + 1);
        c0012al.setClip(1, iM605e3 - iM586d7, iM586d8 - 2, iM586d7);
        c0012al.setColorFromPalette(1);
        c0012al.fillRect(0, 0, 2048, 2048);
        int iM502a2 = Utils.max(AppState.getInt(1450), 16);
        Vector vectorM614m2 = AppState.getVector(1247);
        int size3 = vectorM614m2.size();
        while (true) {
            size3--;
            if (size3 < 0) {
                return;
            }
            Account abstractC0037h = (Account) vectorM614m2.elementAt(size3);
            int i39 = iM605e3;
            int iM586d9 = AppState.getInt(1450);
            int iM502a3 = Utils.max(iM586d9, 16);
            c0012al.setColorFromPalette(13);
            int i40 = i39 - iM586d9;
            c0012al.fillRect(1, i40, ((AppState.getInt(1528) - 2) * abstractC0037h.msgCount) / 100, iM502a3);
            c0012al.drawIcon(abstractC0037h.getIconId(), 3, i40 + ScreenManager.getCenterOffset());
            c0012al.setColorFromPalette(0);
            c0012al.drawString(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(abstractC0037h.login).append(' ').append(abstractC0037h.msgCount).append('%')), 21, i39, 36);
            iM605e3 -= iM502a2;
        }
    }

    /* renamed from: g */
    public final boolean isAtEnd() {
        int size = this.menuItems.size();
        if (size == 0) {
            return true;
        }
        int i = this.selectedIndex;
        int i2 = (i + 1) % size;
        return i2 <= i || getItemX(i2) <= getItemX(i);
    }

    /* renamed from: h */
    public final boolean isAtStart() {
        int size = this.menuItems.size();
        if (size == 0) {
            return true;
        }
        int i = this.selectedIndex;
        int i2 = ((i + size) - 1) % size;
        return i2 >= i || getItemX(i2) >= getItemX(i);
    }

    /* renamed from: i */
    public final void onActionKey() {
        MenuItem c0032cM222d = getSelectedItem();
        if (null != c0032cM222d && c0032cM222d.enabled) {
            IOUtils.postSelectEvent();
            return;
        }
        if (this.screenId == 6) {
            AppState.setInt(1564, 0);
            return;
        }
        if (this.screenId != 4) {
            if (this.layoutMode == 1) {
                this.selectedIndex = (this.selectedIndex + 1) % this.menuItems.size();
                invalidateLayout();
                return;
            }
            return;
        }
        int size = this.menuItems.size();
        if (size == 0) {
            return;
        }
        int i = this.selectedIndex;
        int i2 = (i + 1) % size;
        if (i2 <= i || getItemX(i2) <= getItemX(i)) {
            return;
        }
        this.selectedIndex = i2;
        invalidateLayout();
    }

    /* renamed from: j */
    public final int pageUp() {
        if (this.menuItems.size() == 0) {
            return 0;
        }
        if (this.selectable) {
            this.scrollOffset -= this.contentHeight - 20;
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
            this.selectedIndex = findFirstVisible();
            int iM912h = getItemAt(this.selectedIndex).getTotalHeight();
            int iM241d = getItemY(this.selectedIndex);
            if (iM912h < this.contentHeight && this.scrollOffset > iM241d) {
                this.scrollOffset = iM241d;
            }
        } else {
            this.scrollOffset -= this.contentHeight - 20;
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
        }
        invalidateLayout();
        return 0;
    }

    /* renamed from: k */
    public final int pageDown() {
        int size = this.menuItems.size();
        if (size == 0) {
            return 0;
        }
        findLastVisible();
        this.scrollOffset += this.contentHeight - 20;
        if (this.selectable) {
            MenuItem c0032cM239b = getItemAt(size - 1);
            int iM241d = getItemY(size - 1);
            int iM912h = c0032cM239b.getTotalHeight();
            if (this.scrollOffset > (iM241d + iM912h) - this.contentHeight) {
                this.scrollOffset = (iM241d + iM912h) - this.contentHeight;
            }
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
            this.selectedIndex = findLastVisible();
            int iM912h2 = getItemAt(this.selectedIndex).getTotalHeight();
            int iM241d2 = getItemY(this.selectedIndex);
            if (iM912h2 < this.contentHeight && this.scrollOffset > iM241d2) {
                this.scrollOffset = iM241d2;
            }
        } else {
            this.scrollOffset = Utils.min(this.totalHeight - this.contentHeight, this.scrollOffset);
            if (this.totalHeight < this.contentHeight) {
                this.scrollOffset = 0;
            }
        }
        invalidateLayout();
        return 0;
    }

    /* renamed from: l */
    public final int scrollToTop() {
        this.scrollOffset = 0;
        this.selectedIndex = 0;
        if (this.selectable) {
            while (!getItemAt(this.selectedIndex).isEnabled()) {
                this.selectedIndex++;
            }
        }
        invalidateLayout();
        return 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:74:0x018c, code lost:
    
        r5.selectedIndex = r10;
        invalidateLayout();
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x0196, code lost:
    
        return;
     */
    /* renamed from: m */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void scrollUp() {
        boolean z;
        if (this.screenId == 6) {
            AppState.setInt(1564, 2);
            return;
        }
        if (this.menuItems.size() == 0) {
            return;
        }
        if (!this.selectable) {
            if (this.selectable) {
                if (isItemFullyVisible(0)) {
                    return;
                }
                int iM238t = findLastVisible();
                int iM244u = findFirstVisible();
                if (!isItemFullyVisible(iM244u)) {
                    iM244u++;
                }
                int i = iM244u;
                while (i > 0 && isItemFullyVisible(iM238t)) {
                    i--;
                    this.scrollOffset = getItemY(i);
                }
                return;
            }
            if (this.screenType != 9) {
                this.scrollOffset -= 20;
                if (this.scrollOffset < 0) {
                    this.scrollOffset = 0;
                    return;
                }
                return;
            }
            int iM261v = findVisibleExpanded();
            if (iM261v < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(iM261v)).visible = false;
                int iM262g = findPrevExpanded(iM261v);
                if (iM262g < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(iM262g)).visible = true;
                    this.visibleExpandedIndex = iM262g;
                } else {
                    this.scrollOffset -= 20;
                    if (this.scrollOffset < 0) {
                        this.scrollOffset = 0;
                    }
                }
            } else if (this.visibleExpandedIndex < this.menuItems.size()) {
                int iM262g2 = findPrevExpanded(this.visibleExpandedIndex);
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection != 1) {
                    iM262g2 = this.visibleExpandedIndex;
                }
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection == 1 && getItemY(this.visibleExpandedIndex) - this.scrollOffset <= 20) {
                    iM262g2 = this.visibleExpandedIndex;
                }
                if (iM262g2 < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(iM262g2)).visible = true;
                    this.visibleExpandedIndex = iM262g2;
                } else {
                    this.scrollOffset -= 20;
                    if (this.scrollOffset < 0) {
                        this.scrollOffset = 0;
                    }
                }
            } else {
                int iM264w = findAnyExpanded();
                if (iM264w < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(iM264w)).visible = true;
                    this.visibleExpandedIndex = iM264w;
                } else {
                    this.scrollOffset -= 20;
                    if (this.scrollOffset < 0) {
                        this.scrollOffset = 0;
                    }
                }
            }
            this.expandDirection = 1;
            return;
        }
        int iM240c = getItemX(this.selectedIndex);
        int iM241d = getItemY(this.selectedIndex);
        if (this.layoutMode == 0) {
            if (iM241d < this.scrollOffset) {
                this.scrollOffset -= 20;
                return;
            }
            if (this.selectedIndex == 0) {
                z = true;
            } else {
                int i2 = this.selectedIndex;
                while (true) {
                    i2--;
                    if (i2 < 0) {
                        z = true;
                        break;
                    } else if (getItemAt(i2).isEnabled()) {
                        z = false;
                        break;
                    }
                }
            }
            if (!z) {
                int i3 = this.selectedIndex;
                while (true) {
                    i3--;
                    if (i3 < 0) {
                        break;
                    } else if (getItemAt(i3).isEnabled()) {
                        this.selectedIndex = i3;
                        break;
                    }
                }
                this.scrollOffset = Utils.min(this.scrollOffset, getItemY(this.selectedIndex));
                return;
            }
            if (this.scrollOffset != 0) {
                this.scrollOffset = 0;
                return;
            }
            if (this.reverseScroll) {
                return;
            }
            if (this.selectable) {
                this.selectedIndex = this.menuItems.size() - 1;
                this.scrollOffset = this.totalHeight - this.contentHeight;
                if (this.scrollOffset < 0) {
                    this.scrollOffset = 0;
                    return;
                }
                return;
            }
            if (this.totalHeight < this.contentHeight) {
                this.scrollOffset = 0;
                return;
            } else if (((MenuItem) this.menuItems.lastElement()).getTotalHeight() < this.contentHeight) {
                this.scrollOffset = this.totalHeight - this.contentHeight;
                return;
            } else {
                int[] iArr = this.layoutCache;
                this.scrollOffset = iArr[iArr[0]];
                return;
            }
        }
        int i4 = -1;
        int i5 = 0;
        int i6 = this.selectedIndex;
        while (true) {
            i6--;
            if (i6 < 0) {
                this.selectedIndex = this.menuItems.size() - 1;
                invalidateLayout();
                return;
            }
            int iM241d2 = getItemY(i6);
            if (iM241d2 != iM241d) {
                if (i4 == -1) {
                    i4 = i6;
                    i5 = iM241d2;
                } else if (iM241d2 < i5) {
                    this.selectedIndex = i4;
                    invalidateLayout();
                    return;
                }
            }
            int iM240c2 = getItemX(i6);
            if (iM240c2 == iM240c || (iM240c2 == 0 && iM241d2 != iM241d)) {
                break;
            }
        }
    }

    /* renamed from: n */
    public final void invalidateLayout() {
        if (!this.selectable || this.menuItems.size() <= 0) {
            return;
        }
        int iM912h = getItemAt(this.selectedIndex).getTotalHeight();
        int iM241d = getItemY(this.selectedIndex);
        if (iM241d < this.scrollOffset) {
            this.scrollOffset = iM241d;
        }
        if (this.scrollOffset < (iM241d + iM912h) - this.contentHeight) {
            this.scrollOffset = (iM241d + iM912h) - this.contentHeight;
        }
        if (this.scrollOffset < 0) {
            this.scrollOffset = 0;
        }
    }

    /* renamed from: o */
    public final Screen measureContent() {
        this.scrollOffset = Utils.max(0, this.totalHeight - this.contentHeight);
        int size = this.menuItems.size();
        if (size > 1) {
            this.scrollOffset = Utils.min(this.scrollOffset, getItemY(size - 2));
        }
        if (this.screenType == 9) {
            int iM264w = findAnyExpanded();
            if (iM264w < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(iM264w)).visible = true;
                this.visibleExpandedIndex = iM264w;
            } else {
                this.visibleExpandedIndex = 1000000;
            }
            this.expandDirection = 2;
        }
        return this;
    }

    /* renamed from: p */
    public final void scrollDown() {
        if (this.screenId == 6) {
            ConnectionThread.m1163c(this);
            return;
        }
        int size = this.menuItems.size();
        if (size == 0) {
            return;
        }
        if (!this.selectable) {
            if (this.screenType != 9) {
                if (this.scrollOffset + this.contentHeight < this.totalHeight) {
                    this.scrollOffset += 20;
                    return;
                }
                return;
            }
            int iM261v = findVisibleExpanded();
            if (iM261v < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(iM261v)).visible = false;
                int iM263h = findNextExpanded(iM261v);
                if (iM263h < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(iM263h)).visible = true;
                    this.visibleExpandedIndex = iM263h;
                } else if (this.scrollOffset + this.contentHeight < this.totalHeight) {
                    this.scrollOffset += 20;
                }
            } else if (this.visibleExpandedIndex < this.menuItems.size()) {
                int iM263h2 = findNextExpanded(this.visibleExpandedIndex);
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection != 2) {
                    iM263h2 = this.visibleExpandedIndex;
                }
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection == 2 && this.contentHeight - ((getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset) <= 20) {
                    iM263h2 = this.visibleExpandedIndex;
                }
                if (iM263h2 < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(iM263h2)).visible = true;
                    this.visibleExpandedIndex = iM263h2;
                } else if (this.scrollOffset + this.contentHeight < this.totalHeight) {
                    this.scrollOffset += 20;
                }
            } else {
                int iM264w = findAnyExpanded();
                if (iM264w < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(iM264w)).visible = true;
                    this.visibleExpandedIndex = iM264w;
                } else if (this.scrollOffset + this.contentHeight < this.totalHeight) {
                    this.scrollOffset += 20;
                }
            }
            this.expandDirection = 2;
            return;
        }
        if (this.layoutMode != 0) {
            int iM240c = getItemX(this.selectedIndex);
            int iM241d = getItemY(this.selectedIndex);
            int i = -1;
            int i2 = 0;
            int i3 = this.selectedIndex;
            do {
                i3++;
                if (i3 >= size) {
                    this.selectedIndex = 0;
                    invalidateLayout();
                    return;
                }
                int iM241d2 = getItemY(i3);
                if (iM241d2 != iM241d) {
                    if (i == -1) {
                        i = i3;
                        i2 = iM241d2;
                    } else if (iM241d2 > i2) {
                        this.selectedIndex = i;
                        invalidateLayout();
                        return;
                    }
                }
            } while (getItemX(i3) != iM240c);
            this.selectedIndex = i3;
            invalidateLayout();
            return;
        }
        if (this.selectedIndex >= size - 1) {
            int i4 = this.scrollOffset;
            this.scrollOffset += 20;
            int iM912h = getItemAt(this.selectedIndex).getTotalHeight();
            int iM241d3 = getItemY(this.selectedIndex);
            if (this.scrollOffset > (iM241d3 + iM912h) - this.contentHeight) {
                this.scrollOffset -= this.scrollOffset - ((iM241d3 + iM912h) - this.contentHeight);
            }
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
            if (this.scrollOffset != i4 || this.reverseScroll) {
                return;
            }
            this.scrollOffset = 0;
            this.selectedIndex = 0;
            if (this.selectable) {
                while (!getItemAt(this.selectedIndex).isEnabled()) {
                    this.selectedIndex++;
                }
                return;
            }
            return;
        }
        MenuItem c0032c = null;
        int i5 = this.selectedIndex;
        while (true) {
            i5++;
            if (i5 > size) {
                break;
            }
            if (i5 == size) {
                return;
            }
            MenuItem c0032cM239b = getItemAt(i5);
            c0032c = c0032cM239b;
            if (c0032cM239b.isEnabled()) {
                this.selectedIndex = i5;
                break;
            }
        }
        int iM912h2 = c0032c.getTotalHeight();
        int iM241d4 = getItemY(this.selectedIndex);
        if (iM241d4 + iM912h2 >= this.scrollOffset + this.contentHeight) {
            if (iM912h2 <= this.contentHeight) {
                this.scrollOffset = (iM241d4 + iM912h2) - this.contentHeight;
            } else {
                this.scrollOffset += 20;
            }
        }
    }

    /* renamed from: t */
    private final int findLastVisible() {
        int i;
        int i2 = this.selectable ? this.selectedIndex : 0;
        int size = this.menuItems.size() - 1;
        int i3 = i2;
        int i4 = -1;
        while (true) {
            int i5 = (i3 + size) >> 1;
            i = i5;
            if (i5 == i4) {
                break;
            }
            i4 = i;
            if (getItemY(i) > this.scrollOffset + this.contentHeight) {
                size = i - 1;
            } else {
                i3 = i;
            }
        }
        if (i < this.menuItems.size() - 1 && isItemVisible(i + 1)) {
            i++;
        }
        int i6 = i;
        if (!isItemFullyVisible(i6) && !getItemAt(i6).isEnabled()) {
            i6--;
        }
        return i6;
    }

    /* renamed from: b */
    private final MenuItem getItemAt(int i) {
        if (i >= 0) {
            return (MenuItem) this.menuItems.elementAt(i);
        }
        return null;
    }

    /* renamed from: c */
    private final int getItemX(int i) {
        return this.layoutCache[(i << 1) + 1];
    }

    /* renamed from: d */
    private final int getItemY(int i) {
        return this.layoutCache[(i << 1) + 1 + 1];
    }

    /* renamed from: e */
    private final boolean isItemVisible(int i) {
        int iM241d = getItemY(i);
        return iM241d < this.scrollOffset + this.contentHeight && iM241d + getItemAt(i).getTotalHeight() > this.scrollOffset;
    }

    /* renamed from: f */
    private final boolean isItemFullyVisible(int i) {
        int iM241d = getItemY(i);
        return iM241d >= this.scrollOffset && (iM241d + getItemAt(i).getTotalHeight()) - 1 <= this.scrollOffset + this.contentHeight;
    }

    /* renamed from: u */
    private final int findFirstVisible() {
        int i;
        int size = this.selectable ? this.selectedIndex : this.menuItems.size() - 1;
        int i2 = 0;
        int i3 = -1;
        while (true) {
            int i4 = (i2 + size) >> 1;
            i = i4;
            if (i4 == i3) {
                break;
            }
            i3 = i;
            if (getItemY(i) + getItemAt(i).getTotalHeight() < this.scrollOffset) {
                i2 = i + 1;
            } else {
                size = i;
            }
        }
        if (!isItemVisible(i)) {
            i++;
        } else if (i > 0 && isItemVisible(i - 1)) {
            i--;
        }
        return i;
    }

    /* renamed from: a */
    public final Screen addTextPair(String str, String str2, int i) {
        return addFullItem(-1, str, str2, i, (Object) null);
    }

    /* renamed from: a */
    public final Screen addIconItem(int i, String str, int i2) {
        return addFullItem(i, (String) null, str, i2, (Object) null);
    }

    /* renamed from: a */
    public final Screen addIconItemWithData(int i, String str, int i2, Object obj) {
        return addFullItem(i, (String) null, str, i2, obj);
    }

    /* renamed from: a */
    public final Screen addLabelValue(String str, String str2) {
        return addTextPair(str, str2, 200);
    }

    /* renamed from: a */
    public final Screen addIconById(int i, int i2, int i3) {
        return addIconItem(i, AppState.getString(i2), i3);
    }

    /* renamed from: b */
    public final Screen addExpandableItem(int i, String str, int i2, Object obj) {
        MenuItem c0032cM901a = new MenuItem(13, AppState.emptyStr).setIcon(i).addText(str, 5, i2);
        c0032cM901a.data = obj;
        return addItem(c0032cM901a);
    }

    /* renamed from: a */
    public final Screen addSeparator(String str, int i) {
        return addItem(MenuItem.createSeparator().addText(str, 1, i));
    }

    /* renamed from: b */
    public final Screen addActionById(int i, int i2, int i3) {
        String strM584b = AppState.getString(i2);
        MenuItem c0032cM896a = MenuItem.createWithWidth(strM584b, i3).setIcon(i).setLabel(strM584b).setIcon(244);
        c0032cM896a.enabled = true;
        return addItem(c0032cM896a);
    }

    /* renamed from: a */
    public final Screen addTextItem(String str) {
        return addIconItem(-1, str, 200);
    }

    /* renamed from: b */
    public final Screen addIconTextItem(int i, String str, int i2) {
        return addItem(MenuItem.createWithWidth(str, i2).setIcon(i));
    }

    /* renamed from: a */
    public final Screen addLabelById(int i) {
        return addItem(MenuItem.createSeparator().setLabel(AppState.getString(i)));
    }

    /* renamed from: a */
    public final Screen addFullItem(int i, String str, String str2, int i2, Object obj) {
        MenuItem c0032cM888a = MenuItem.createWithWidth(str2, i2);
        if (i >= 0) {
            c0032cM888a.setIcon(i);
        }
        if (str != null) {
            c0032cM888a.addText(str, 0, 6);
        }
        if (str2 != null) {
            int iIndexOf = str2.indexOf(0);
            c0032cM888a.setLabel(iIndexOf < 0 ? str2 : StringUtils.prefix(str2, iIndexOf));
        }
        c0032cM888a.data = obj;
        return addItem(c0032cM888a);
    }

    /* renamed from: b */
    public final Screen selectByTitle(String str) {
        if (str != null) {
            int i = 0;
            Enumeration enumerationElements = this.menuItems.elements();
            while (true) {
                if (!enumerationElements.hasMoreElements()) {
                    break;
                }
                if (str.equals(((MenuItem) enumerationElements.nextElement()).title)) {
                    this.selectedIndex = i;
                    break;
                }
                i++;
            }
        }
        return this;
    }

    /* renamed from: q */
    public final void rebuildItems() {
        Vector vectorM1213g = NetworkUtils.newVector();
        int size = this.menuItems.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else {
                vectorM1213g.addElement(this.menuItems.elementAt(size));
            }
        }
        this.menuItems.removeAllElements();
        this.layoutCache[0] = 0;
        this.totalHeight = 0;
        int size2 = vectorM1213g.size();
        while (true) {
            size2--;
            if (size2 < 0) {
                NetworkUtils.releaseVector(vectorM1213g);
                return;
            }
            addItem((MenuItem) vectorM1213g.elementAt(size2));
        }
    }

    /* renamed from: a */
    public final Screen setSoftKeys(String str, String str2, int i, int i2, int i3) {
        GraphicsContext c0012alM608k = AppState.getGfxContext(0);
        this.titleLeft = str;
        int iM214a = c0012alM608k.stringWidth(str);
        this.titleRight = str2;
        this.titleMaxWidth = Utils.max(iM214a, c0012alM608k.stringWidth(str2)) << 1;
        this.softKeyLeft = i;
        this.softKeyCenter = i2;
        this.softKeyRight = i3;
        return this;
    }

    /* renamed from: a */
    public final boolean onPointerEvent(int i, int i2, int i3, int i4, boolean z) {
        int iM240c;
        int iM241d;
        boolean z2;
        int iM240c2;
        int iM241d2;
        if (!this.touchConsumed) {
            return true;
        }
        AppController.needsRepaint = true;
        this.touchConsumed = false;
        int i5 = i - this.offsetX;
        int i6 = i2 - this.offsetY;
        int i7 = i3 - this.offsetX;
        int i8 = i4 - this.offsetY;
        if (this.screenId == 6) {
            ConnectionThread.m1161a(this);
            MapRenderer.onTap(i5, i6 - this.contentTop);
            return true;
        }
        if (this.selectable) {
            int i9 = (z ? i7 : i5) - 2;
            int i10 = ((z ? i8 : i6) + this.scrollOffset) - this.contentTop;
            int size = this.menuItems.size();
            int i11 = this.hasScrollbar ? this.contentWidth : this.contentWidth + 2;
            int i12 = 0;
            while (true) {
                if (i12 >= size) {
                    z2 = false;
                    break;
                }
                MenuItem c0032cM239b = getItemAt(i12);
                if (c0032cM239b.isEnabled() && i9 > (iM240c2 = getItemX(i12)) && i10 > (iM241d2 = getItemY(i12))) {
                    if (i9 < iM240c2 + (this.layoutMode == 0 ? i11 : c0032cM239b.getTotalWidth()) && i10 < iM241d2 + c0032cM239b.getTotalHeight()) {
                        if (this.selectedIndex != i12 || z) {
                            this.selectedIndex = i12;
                        } else {
                            AppController.onItemSelected();
                        }
                        z2 = true;
                    }
                }
                i12++;
            }
            if (z2) {
                return true;
            }
        }
        if (z) {
            return false;
        }
        if (this.screenType != 9) {
            if (this.screenType == 0 || this.screenType == 1) {
                return true;
            }
            AppController.onItemSelected();
            return true;
        }
        int i13 = i5 - 2;
        int i14 = (i6 + this.scrollOffset) - this.contentTop;
        int i15 = this.hasScrollbar ? this.contentWidth : this.contentWidth + 2;
        int iM261v = findVisibleExpanded();
        if (iM261v < this.menuItems.size()) {
            ((MenuItem) this.menuItems.elementAt(iM261v)).visible = false;
        }
        int size2 = this.menuItems.size();
        while (true) {
            size2--;
            if (size2 >= 0) {
                MenuItem c0032cM239b2 = getItemAt(size2);
                if (c0032cM239b2.id == 13 && i13 > (iM240c = getItemX(size2)) && i14 > (iM241d = getItemY(size2)) && i13 < iM240c + i15 && i14 < iM241d + c0032cM239b2.getTotalHeight()) {
                    c0032cM239b2.visible = true;
                    break;
                }
            } else {
                break;
            }
        }
        AppController.onItemSelected();
        return true;
    }

    /* renamed from: v */
    private int findVisibleExpanded() {
        int size = this.menuItems.size() + 1;
        for (int i = 0; i < this.menuItems.size(); i++) {
            MenuItem c0032c = (MenuItem) this.menuItems.elementAt(i);
            if (c0032c.id == 13 && c0032c.visible && getItemY(i) > this.scrollOffset && (getItemY(i) + c0032c.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
                size = i;
            }
        }
        return size;
    }

    /* renamed from: g */
    private int findPrevExpanded(int i) {
        int size = this.menuItems.size() + 1;
        for (int i2 = 0; i2 < this.menuItems.size(); i2++) {
            MenuItem c0032c = (MenuItem) this.menuItems.elementAt(i2);
            if (c0032c.id == 13 && !c0032c.visible && getItemY(i2) > this.scrollOffset && (getItemY(i2) + c0032c.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(i2) < getItemY(i)) {
                size = i2;
            }
        }
        return size;
    }

    /* renamed from: h */
    private int findNextExpanded(int i) {
        int size = this.menuItems.size() + 1;
        int i2 = 0;
        while (true) {
            if (i2 < this.menuItems.size()) {
                MenuItem c0032c = (MenuItem) this.menuItems.elementAt(i2);
                if (c0032c.id == 13 && !c0032c.visible && getItemY(i2) > this.scrollOffset && (getItemY(i2) + c0032c.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(i2) > getItemY(i)) {
                    size = i2;
                    break;
                }
                i2++;
            } else {
                break;
            }
        }
        return size;
    }

    /* renamed from: w */
    private int findAnyExpanded() {
        int size = this.menuItems.size() + 1;
        for (int i = 0; i < this.menuItems.size(); i++) {
            MenuItem c0032c = (MenuItem) this.menuItems.elementAt(i);
            if (c0032c.id == 13 && !c0032c.visible && getItemY(i) > this.scrollOffset && (getItemY(i) + c0032c.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
                size = i;
            }
        }
        return size;
    }

    /* renamed from: r */
    public final int getSelectedY() {
        int iM241d = 0;
        if (this.selectedIndex > 0) {
            iM241d = getItemY(this.selectedIndex);
        }
        return this.offsetY + iM241d;
    }

    /* renamed from: a */
    public final Screen setOffset(int i, int i2) {
        this.offsetX = i;
        this.offsetY = i2;
        return this;
    }
}
