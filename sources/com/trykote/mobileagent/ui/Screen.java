package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
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
        this.screenId = ScreenId.STATUS_INPUT;
    }

    /* renamed from: a */
    public final Screen initTabs() {
        if (AppState.getBool(StateKeys.SETTING_HEADER_VISIBLE)) {
            this.tabItems = ObjectPool.newVector();
            recalcLayout();
        }
        return this;
    }

    private Screen(int i, int i2, int i3, int i4) {
        this.menuItems = ObjectPool.newVector();
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
            int barHeight = Utils.max(AppState.getInt(StateKeys.INT_FONT_HEIGHT), 16) + 3;
            this.contentBottom = i3 - barHeight;
            this.contentHeight -= barHeight;
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
        int expandedIdx;
        if (this.screenType != 9 || (expandedIdx = findVisibleExpanded()) >= this.menuItems.size()) {
            return null;
        }
        return getItemAt(expandedIdx);
    }

    /* renamed from: a */
    public final Screen setHeader(int i, String str) {
        this.headerItem = MenuItem.createSeparator().addText(AppState.getString(StateKeys.STR_PLACEHOLDER_TEXT), 1, 0).setLabelInternal(i, str, 1, 0);
        recalcLayout();
        return this;
    }

    /* renamed from: a */
    public final Screen addItem(MenuItem menuItem) {
        if (this.layoutMode == 0) {
            menuItem.layout(this.contentWidth);
            this.menuItems.addElement(menuItem);
            this.layoutCache = BitMath.resizeArray(this.layoutCache, 0, this.totalHeight);
            this.totalHeight += menuItem.getTotalHeight();
        } else {
            int itemX = 0;
            int itemY = 0;
            int size = this.menuItems.size() - 1;
            if (this.menuItems.size() > 0) {
                itemX = getItemX(size) + getItemAt(size).getTotalWidth();
                itemY = getItemY(size);
            }
            if (itemX > 0 && itemX + menuItem.getTotalWidth() >= this.contentWidth) {
                itemX = 0;
                itemY = getItemY(size) + getItemAt(size).getTotalHeight();
            }
            this.menuItems.addElement(menuItem);
            this.layoutCache = BitMath.resizeArray(this.layoutCache, itemX, itemY);
            this.totalHeight = itemY + menuItem.getTotalHeight();
        }
        if (this.totalHeight <= 0 || this.totalHeight < this.contentHeight) {
            this.hasScrollbar = false;
        } else {
            this.scrollRange = ((this.contentBottom - 4) * this.contentHeight) / this.totalHeight;
            this.hasScrollbar = true;
        }
        if (this.selectable && this.selectedIndex < 0 && menuItem.isEnabled()) {
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
        Enumeration elements = this.menuItems.elements();
        while (elements.hasMoreElements()) {
            int i2 = i;
            int maxHeight = ((MenuItem) elements.nextElement()).getMaxHeight();
            if (i2 < maxHeight) {
                i = maxHeight;
            }
        }
        int i3 = i + 16;
        if (i3 < this.containerWidth) {
            this.containerWidth = i3;
        }
        recalcLayout();
        return this;
    }

    /* renamed from: a */
    public final void paint(GraphicsContext g, boolean isTop, boolean isModal) {
        if (this.layoutMode != 2) {
            paintBackground(g, isTop);
            if (this.headerItem != null) {
                paintHeaderGradient(g);
            }
            paintMenuItems(g);
            if (this.hasScrollbar) {
                paintScrollbar(g);
            }
            if (this.tabItems != null) {
                paintBottomTabBar(g);
            }
            if (isTop && AppState.getBool(StateKeys.SETTING_STATUS_BAR_VISIBLE)) {
                paintSoftKeys(g);
            }
        }
        if (this.screenType == 1 || this.screenType == 12) {
            paintTopTabBar(g);
        }
        if (this.screenId == ScreenId.MAP) {
            paintMapOverlay(g);
            return;
        }
        if (this.screenId == ScreenId.CONTACT_LIST) {
            paintContactPopup(g);
        }
    }

    private void paintBackground(GraphicsContext g, boolean isTop) {
        int i = this.offsetX;
        int i2 = this.offsetY;
        int i3 = this.containerWidth;
        int i4 = this.containerHeight;
        int colorIdx = isTop ? 1 : 2;
        Graphics graphics = g.graphics;
        g.setColorFromPalette(colorIdx);
        graphics.fillRect(i, i2, i3, i4);
        g.setColorFromPalette(16);
        graphics.drawRect(i, i2, i3 - 1, i4 - 1);
    }

    private void paintHeaderGradient(GraphicsContext g) {
        int i6 = this.offsetX + 1;
        int i7 = this.offsetY + 1;
        g.setClip(i6, i7, this.innerWidth, this.headerHeight);
        int stateVal = AppState.getInt(StateKeys.SETTING_COLOR_THEME);
        int stateVal2 = AppState.getInt(StateKeys.PALETTE_SCREEN_BASE + stateVal);
        if (stateVal2 != AppState.getInt(stateVal + 5082)) {
            for (int i8 = 1; i8 < this.headerHeight; i8++) {
                g.setColor(((255 - ((i8 * (255 - (stateVal2 >> 16))) / this.headerHeight)) << 16) | ((255 - ((i8 * (255 - ((stateVal2 >> 8) & 255))) / this.headerHeight)) << 8) | (255 - ((i8 * (255 - (stateVal2 & 255))) / this.headerHeight)));
                g.drawRect(i6, i7 + i8, this.innerWidth, 0);
            }
        } else {
            g.setColor(stateVal2);
            g.fillRect(i6, i7, this.innerWidth, this.headerHeight);
        }
        this.headerItem.render(g, i6, i7, 0);
    }

    private void paintMenuItems(GraphicsContext g) {
        int i9 = this.offsetX + 2;
        int i10 = this.offsetY + this.contentTop;
        int i11 = this.hasScrollbar ? this.contentWidth : this.contentWidth + 2;
        int size = this.menuItems.size();
        boolean isGridLayout = this.layoutMode != 0;
        int i12 = this.scrollOffset;
        for (int i13 = 0; i13 < size; i13++) {
            int itemY = getItemY(i13);
            if (itemY - i12 > this.contentHeight) {
                break;
            }
            MenuItem menuItem = getItemAt(i13);
            int itemHeight = menuItem.getTotalHeight();
            if (itemY + itemHeight >= i12) {
                int itemX = getItemX(i13);
                int itemWidth = menuItem.getTotalWidth();
                g.setClip(i9, i10, i11, this.contentHeight);
                int i14 = i9 + itemX;
                int i15 = (i10 + itemY) - i12;
                int i16 = this.layoutMode == 0 ? i11 : itemWidth;
                int i17 = menuItem.id;
                if (this.selectable && i13 == this.selectedIndex && i17 != 11) {
                    g.setColorFromPalette(13);
                    g.fillRect(i14, i15, i16, itemHeight);
                }
                if (i17 == 13 && menuItem.visible) {
                    g.setColorFromPalette(13);
                    g.fillRect(i14, i15, i16, itemHeight);
                }
                boolean isVisible = true;
                if (isGridLayout) {
                    isVisible = false;
                    int i18 = itemHeight;
                    int i19 = i16;
                    int i20 = i15;
                    int i21 = i14;
                    int i22 = i21 + i19;
                    Graphics graphics2 = g.graphics;
                    int clipX = graphics2.getClipX();
                    if (i22 >= clipX) {
                        int i23 = i20 + i18;
                        int clipY = graphics2.getClipY();
                        if (i23 >= clipY) {
                            int clipWidth = graphics2.getClipWidth();
                            if (i21 <= clipX + clipWidth) {
                                int clipHeight = graphics2.getClipHeight();
                                if (i20 <= clipY + clipHeight) {
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
                                            isVisible = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (isVisible) {
                    menuItem.render(g, i14, i15, i11);
                }
            }
        }
    }

    private void paintScrollbar(GraphicsContext g) {
        int i26 = this.offsetX + this.borderWidth;
        int i27 = this.offsetY + this.contentStart;
        g.setClip(i26, i27, 7, this.contentBottom + 4);
        g.setColorFromPalette(16);
        g.fillRect(i26 + 1, i27 + (this.totalHeight == 0 ? 0 : Utils.min(((this.contentBottom - 4) * this.scrollOffset) / this.totalHeight, (this.contentBottom - 4) - this.scrollRange)), 1, this.scrollRange + 2);
        g.drawRect(i26, i27 - 1, 2, this.contentBottom + 1);
    }

    private void paintBottomTabBar(GraphicsContext g) {
        int barHeight = Utils.max(AppState.getInt(StateKeys.INT_FONT_HEIGHT), 16);
        int screenHeight = AppState.getHeight() - 1;
        int screenWidth = AppState.getInt(StateKeys.INT_SCREEN_WIDTH);
        g.setClip(0, (screenHeight - barHeight) - 3, screenWidth, barHeight + 4).setColorFromPalette(16).fillRect(0, (screenHeight - barHeight) - 3, screenWidth, barHeight + 4).setColorFromPalette(17).fillRect(1, (screenHeight - barHeight) - 2, screenWidth - 2, barHeight + 2).setColorFromPalette(0).setFont(AppState.getGfxContext(StateKeys.GFX_INDEX_DEFAULT));
        Vector vector = this.tabItems;
        int i28 = 3;
        boolean z5 = false;
        int centerY = ((screenHeight - barHeight) - 1) + ScreenManager.getCenterOffset();
        for (int i29 = 0; i29 < vector.size(); i29++) {
            Object objElementAt = vector.elementAt(i29);
            if (!(objElementAt instanceof Integer)) {
                z5 = true;
                g.drawString((String) objElementAt, i28, (screenHeight - barHeight) - 1, 20);
                i28 = screenWidth;
            } else if (z5) {
                i28 -= 18;
                g.drawIcon(((Integer) objElementAt).intValue(), i28, centerY);
            } else {
                g.drawIcon(((Integer) objElementAt).intValue(), 3, centerY);
                i28 += 18;
            }
        }
    }

    private void paintSoftKeys(GraphicsContext g) {
        int screenWidth = AppState.getInt(StateKeys.INT_SCREEN_WIDTH);
        int screenHeight = AppState.getHeight();
        g.setClip(0, 0, screenWidth, 2048 + screenHeight);
        g.setFont(AppState.getGfxContext(StateKeys.GFX_INDEX_DEFAULT));
        g.setColorFromPalette(15);
        if (this.titleLeft != null) {
            g.drawString(this.titleLeft, 1, screenHeight, 20);
        }
        if (this.titleRight != null) {
            g.drawString(this.titleRight, screenWidth - 1, screenHeight, 24);
        }
        if (ResourceManager.clockWidth + this.titleMaxWidth < screenWidth - 6) {
            g.drawString(Utils.defaultStr(AppState.getString(StateKeys.SLOT_CLOCK_STRING)), screenWidth >> 1, screenHeight, 17);
        }
    }

    private void paintTopTabBar(GraphicsContext g) {
        int paintMode;
        g.setFont(AppState.getGfxContext(StateKeys.GFX_INDEX_BOLD));
        TabBar tab = (TabBar) AppState.getVector(StateKeys.VEC_TAB_BARS).elementAt(TabBar.currentIndex);
        Vector tabs = AppState.getVector(StateKeys.VEC_TAB_ITEMS);
        int size2 = tabs.size();
        while (true) {
            size2--;
            if (size2 < 0) {
                break;
            }
            Object objElementAt2 = tabs.elementAt(size2);
            if (objElementAt2 instanceof TabBar) {
                TabBar tab2 = (TabBar) objElementAt2;
                boolean isSelected = objElementAt2 == tab && !TabBar.scrollEnabled;
                GraphicsContext gfx = g.setColorFromPalette(16);
                int i30 = tab2.xOffset;
                int i31 = tab2.width;
                int textOffset = AppState.getIntOffset(StateKeys.OFFSET_BOLD_FONT_HEIGHT) + 7;
                gfx.setClip(i30, 2, i31, textOffset - 2).drawLine(tab2.xOffset, textOffset, tab2.xOffset, 6).drawLine(tab2.xOffset, 6, tab2.xOffset + 4, 2).drawLine(tab2.xOffset + 4, 2, (tab2.xOffset + tab2.width) - 2, 2).drawLine((tab2.xOffset + tab2.width) - 2, 2, (tab2.xOffset + tab2.width) - 2, textOffset).setColorFromPalette(isSelected ? 1 : 17);
                int i32 = isSelected ? textOffset : textOffset - 1;
                int i33 = 3;
                while (i33 < i32) {
                    g.drawLine(tab2.xOffset + 1 + (i33 < 6 ? 6 - i33 : 0), i33, (tab2.xOffset + tab2.width) - 3, i33);
                    i33++;
                }
                if (tab2.account == null) {
                    int i34 = tab2.iconId;
                    paintMode = (i34 == 240 && AccountManager.hasActiveConnection()) ? 16385 : (i34 == 240 || i34 == 264 || AppState.getVector(StateKeys.VEC_ONLINE_CONTACTS).size() <= 0) ? i34 : 16384;
                } else {
                    paintMode = AccountManager.getAccountStatus(tab2.account);
                }
                g.drawIcon(paintMode, tab2.xOffset + 4, 4 + ScreenManager.getCenterOffset()).setColorFromPalette(0).setClip(tab2.xOffset, 2, tab2.width - 3, textOffset - 2).drawString(tab2.title, tab2.xOffset + 6 + 16, 4, 20);
            } else {
                int[] iArr = (int[]) objElementAt2;
                int i35 = iArr[0];
                int centerY2 = 4 + ScreenManager.getCenterOffset();
                g.setClip(i35, centerY2, 16, 16);
                g.drawIcon(iArr[1], i35, centerY2);
            }
        }
    }

    private void paintMapOverlay(GraphicsContext g) {
        int i36 = this.offsetX + 2;
        int i37 = this.offsetY + this.contentTop;
        g.setClip(i36, i37, this.containerWidth, this.contentHeight);
        try {
            int stateVal5 = AppState.getInt(StateKeys.MAP_VIEWPORT_WIDTH);
            int stateVal6 = AppState.getInt(StateKeys.MAP_VIEWPORT_HEIGHT);
            Graphics graphics3 = g.graphics;
            graphics3.drawImage(AppState.getImage(StateKeys.OBJ_FONT_2), stateVal5 >> 1, i37 + (stateVal6 >> 1), 3);
            if (!AppState.getBool(StateKeys.FLAG_MAP_OVERLAY_ACTIVE) && AppState.getBool(StateKeys.FLAG_SUPPORTS_ALPHA)) {
                int[] iArr2 = new int[stateVal5];
                int i38 = stateVal5;
                while (true) {
                    i38--;
                    if (i38 < 0) {
                        break;
                    } else {
                        iArr2[i38] = 1006632960;
                    }
                }
                while (true) {
                    stateVal6--;
                    if (stateVal6 < 0) {
                        break;
                    } else {
                        graphics3.drawRGB(iArr2, 0, stateVal5, 0, i37 + stateVal6, stateVal5, 1, true);
                    }
                }
            }
        } catch (Throwable unused) {
        }
        AppState.setInt(StateKeys.FLAG_MAP_SCROLLING, 0);
    }

    private void paintContactPopup(GraphicsContext g) {
        g.setClip(this.offsetX + 2, this.offsetY + this.contentTop, this.containerWidth, this.contentHeight);
        int popupHeight = AppState.getInt(StateKeys.INT_POPUP_HEIGHT);
        if (popupHeight <= 0) {
            return;
        }
        g.setFont(AppState.getGfxContext(StateKeys.GFX_INDEX_DEFAULT));
        int screenHeight = AppState.getHeight() - 1;
        int screenWidth = AppState.getInt(StateKeys.INT_SCREEN_WIDTH);
        g.setClip(0, (screenHeight - popupHeight) - 1, screenWidth, popupHeight + 1);
        g.setColorFromPalette(16);
        g.fillRect(0, (screenHeight - popupHeight) - 1, screenWidth, popupHeight + 1);
        g.setClip(1, screenHeight - popupHeight, screenWidth - 2, popupHeight);
        g.setColorFromPalette(1);
        g.fillRect(0, 0, 2048, 2048);
        int barHeight = Utils.max(AppState.getInt(StateKeys.INT_FONT_HEIGHT), 16);
        Vector tabs = AppState.getVector(StateKeys.VEC_POPUP_ITEMS);
        int size = tabs.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            Account account = (Account) tabs.elementAt(size);
            int i39 = screenHeight;
            int fontHeight = AppState.getInt(StateKeys.INT_FONT_HEIGHT);
            int barHeight3 = Utils.max(fontHeight, 16);
            g.setColorFromPalette(13);
            int i40 = i39 - fontHeight;
            g.fillRect(1, i40, ((AppState.getInt(StateKeys.INT_SCREEN_WIDTH) - 2) * account.msgCount) / 100, barHeight3);
            g.drawIcon(account.getIconId(), 3, i40 + ScreenManager.getCenterOffset());
            g.setColorFromPalette(0);
            g.drawString(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(account.login).append(' ').append(account.msgCount).append('%')), 21, i39, 36);
            screenHeight -= barHeight;
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
        MenuItem selectedItem = getSelectedItem();
        if (null != selectedItem && selectedItem.enabled) {
            IOUtils.postSelectEvent();
            return;
        }
        if (this.screenId == ScreenId.MAP) {
            AppState.setInt(StateKeys.INT_MAP_SCROLL_DIRECTION, 0);
            return;
        }
        if (this.screenId != ScreenId.CONTACT_LIST) {
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
            int itemHeight = getItemAt(this.selectedIndex).getTotalHeight();
            int itemY = getItemY(this.selectedIndex);
            if (itemHeight < this.contentHeight && this.scrollOffset > itemY) {
                this.scrollOffset = itemY;
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
            MenuItem menuItem = getItemAt(size - 1);
            int itemY = getItemY(size - 1);
            int itemHeight = menuItem.getTotalHeight();
            if (this.scrollOffset > (itemY + itemHeight) - this.contentHeight) {
                this.scrollOffset = (itemY + itemHeight) - this.contentHeight;
            }
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
            this.selectedIndex = findLastVisible();
            int itemHeight2 = getItemAt(this.selectedIndex).getTotalHeight();
            int itemY2 = getItemY(this.selectedIndex);
            if (itemHeight2 < this.contentHeight && this.scrollOffset > itemY2) {
                this.scrollOffset = itemY2;
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
        if (this.screenId == ScreenId.MAP) {
            AppState.setInt(StateKeys.INT_MAP_SCROLL_DIRECTION, 2);
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
                int lastVisible = findLastVisible();
                int firstVisible = findFirstVisible();
                if (!isItemFullyVisible(firstVisible)) {
                    firstVisible++;
                }
                int i = firstVisible;
                while (i > 0 && isItemFullyVisible(lastVisible)) {
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
            int expandedIdx = findVisibleExpanded();
            if (expandedIdx < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(expandedIdx)).visible = false;
                int prevExpanded = findPrevExpanded(expandedIdx);
                if (prevExpanded < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(prevExpanded)).visible = true;
                    this.visibleExpandedIndex = prevExpanded;
                } else {
                    this.scrollOffset -= 20;
                    if (this.scrollOffset < 0) {
                        this.scrollOffset = 0;
                    }
                }
            } else if (this.visibleExpandedIndex < this.menuItems.size()) {
                int prevExpanded2 = findPrevExpanded(this.visibleExpandedIndex);
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection != 1) {
                    prevExpanded2 = this.visibleExpandedIndex;
                }
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection == 1 && getItemY(this.visibleExpandedIndex) - this.scrollOffset <= 20) {
                    prevExpanded2 = this.visibleExpandedIndex;
                }
                if (prevExpanded2 < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(prevExpanded2)).visible = true;
                    this.visibleExpandedIndex = prevExpanded2;
                } else {
                    this.scrollOffset -= 20;
                    if (this.scrollOffset < 0) {
                        this.scrollOffset = 0;
                    }
                }
            } else {
                int anyExpanded = findAnyExpanded();
                if (anyExpanded < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(anyExpanded)).visible = true;
                    this.visibleExpandedIndex = anyExpanded;
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
        int itemX = getItemX(this.selectedIndex);
        int itemY = getItemY(this.selectedIndex);
        if (this.layoutMode == 0) {
            if (itemY < this.scrollOffset) {
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
            int itemY2 = getItemY(i6);
            if (itemY2 != itemY) {
                if (i4 == -1) {
                    i4 = i6;
                    i5 = itemY2;
                } else if (itemY2 < i5) {
                    this.selectedIndex = i4;
                    invalidateLayout();
                    return;
                }
            }
            int itemX2 = getItemX(i6);
            if (itemX2 == itemX || (itemX2 == 0 && itemY2 != itemY)) {
                break;
            }
        }
    }

    /* renamed from: n */
    public final void invalidateLayout() {
        if (!this.selectable || this.menuItems.size() <= 0) {
            return;
        }
        int itemHeight = getItemAt(this.selectedIndex).getTotalHeight();
        int itemY = getItemY(this.selectedIndex);
        if (itemY < this.scrollOffset) {
            this.scrollOffset = itemY;
        }
        if (this.scrollOffset < (itemY + itemHeight) - this.contentHeight) {
            this.scrollOffset = (itemY + itemHeight) - this.contentHeight;
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
            int anyExpanded = findAnyExpanded();
            if (anyExpanded < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(anyExpanded)).visible = true;
                this.visibleExpandedIndex = anyExpanded;
            } else {
                this.visibleExpandedIndex = 1000000;
            }
            this.expandDirection = 2;
        }
        return this;
    }

    /* renamed from: p */
    public final void scrollDown() {
        if (this.screenId == ScreenId.MAP) {
            MapController.handleMapSwitch(this);
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
            int expandedIdx = findVisibleExpanded();
            if (expandedIdx < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(expandedIdx)).visible = false;
                int nextExpanded = findNextExpanded(expandedIdx);
                if (nextExpanded < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(nextExpanded)).visible = true;
                    this.visibleExpandedIndex = nextExpanded;
                } else if (this.scrollOffset + this.contentHeight < this.totalHeight) {
                    this.scrollOffset += 20;
                }
            } else if (this.visibleExpandedIndex < this.menuItems.size()) {
                int nextExpanded2 = findNextExpanded(this.visibleExpandedIndex);
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection != 2) {
                    nextExpanded2 = this.visibleExpandedIndex;
                }
                if (getItemY(this.visibleExpandedIndex) > this.scrollOffset && (getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset <= this.contentHeight && this.expandDirection == 2 && this.contentHeight - ((getItemY(this.visibleExpandedIndex) + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight()) - this.scrollOffset) <= 20) {
                    nextExpanded2 = this.visibleExpandedIndex;
                }
                if (nextExpanded2 < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(nextExpanded2)).visible = true;
                    this.visibleExpandedIndex = nextExpanded2;
                } else if (this.scrollOffset + this.contentHeight < this.totalHeight) {
                    this.scrollOffset += 20;
                }
            } else {
                int anyExpanded = findAnyExpanded();
                if (anyExpanded < this.menuItems.size()) {
                    ((MenuItem) this.menuItems.elementAt(anyExpanded)).visible = true;
                    this.visibleExpandedIndex = anyExpanded;
                } else if (this.scrollOffset + this.contentHeight < this.totalHeight) {
                    this.scrollOffset += 20;
                }
            }
            this.expandDirection = 2;
            return;
        }
        if (this.layoutMode != 0) {
            int itemX = getItemX(this.selectedIndex);
            int itemY = getItemY(this.selectedIndex);
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
                int itemY2 = getItemY(i3);
                if (itemY2 != itemY) {
                    if (i == -1) {
                        i = i3;
                        i2 = itemY2;
                    } else if (itemY2 > i2) {
                        this.selectedIndex = i;
                        invalidateLayout();
                        return;
                    }
                }
            } while (getItemX(i3) != itemX);
            this.selectedIndex = i3;
            invalidateLayout();
            return;
        }
        if (this.selectedIndex >= size - 1) {
            int i4 = this.scrollOffset;
            this.scrollOffset += 20;
            int itemHeight = getItemAt(this.selectedIndex).getTotalHeight();
            int itemY3 = getItemY(this.selectedIndex);
            if (this.scrollOffset > (itemY3 + itemHeight) - this.contentHeight) {
                this.scrollOffset -= this.scrollOffset - ((itemY3 + itemHeight) - this.contentHeight);
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
        MenuItem currentItem = null;
        int i5 = this.selectedIndex;
        while (true) {
            i5++;
            if (i5 > size) {
                break;
            }
            if (i5 == size) {
                return;
            }
            currentItem = getItemAt(i5);
            if (currentItem.isEnabled()) {
                this.selectedIndex = i5;
                break;
            }
        }
        int itemHeight2 = currentItem.getTotalHeight();
        int itemY4 = getItemY(this.selectedIndex);
        if (itemY4 + itemHeight2 >= this.scrollOffset + this.contentHeight) {
            if (itemHeight2 <= this.contentHeight) {
                this.scrollOffset = (itemY4 + itemHeight2) - this.contentHeight;
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
        int itemY = getItemY(i);
        return itemY < this.scrollOffset + this.contentHeight && itemY + getItemAt(i).getTotalHeight() > this.scrollOffset;
    }

    /* renamed from: f */
    private final boolean isItemFullyVisible(int i) {
        int itemY = getItemY(i);
        return itemY >= this.scrollOffset && (itemY + getItemAt(i).getTotalHeight()) - 1 <= this.scrollOffset + this.contentHeight;
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
        MenuItem expandItem = new MenuItem(13, AppState.emptyStr).setIcon(i).addText(str, 5, i2);
        expandItem.data = obj;
        return addItem(expandItem);
    }

    /* renamed from: a */
    public final Screen addSeparator(String str, int i) {
        return addItem(MenuItem.createSeparator().addText(str, 1, i));
    }

    /* renamed from: b */
    public final Screen addActionById(int i, int i2, int i3) {
        String labelStr = AppState.getString(i2);
        MenuItem actionItem = MenuItem.createWithWidth(labelStr, i3).setIcon(i).setLabel(labelStr).setIcon(244);
        actionItem.enabled = true;
        return addItem(actionItem);
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
        RemoteLogger.log("SCR", "addFullItem icon=" + i + " str2='" + str2 + "' w=" + i2);
        MenuItem newItem = MenuItem.createWithWidth(str2, i2);
        if (i >= 0) {
            newItem.setIcon(i);
        }
        if (str != null) {
            newItem.addText(str, 0, 6);
        }
        if (str2 != null) {
            int separatorIdx = str2.indexOf(0);
            newItem.setLabel(separatorIdx < 0 ? str2 : StringUtils.prefix(str2, separatorIdx));
        }
        newItem.data = obj;
        return addItem(newItem);
    }

    /* renamed from: b */
    public final Screen selectByTitle(String str) {
        if (str != null) {
            int i = 0;
            Enumeration elements = this.menuItems.elements();
            while (true) {
                if (!elements.hasMoreElements()) {
                    break;
                }
                if (str.equals(((MenuItem) elements.nextElement()).title)) {
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
        Vector newItems = ObjectPool.newVector();
        int size = this.menuItems.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else {
                newItems.addElement(this.menuItems.elementAt(size));
            }
        }
        this.menuItems.removeAllElements();
        this.layoutCache[0] = 0;
        this.totalHeight = 0;
        int size2 = newItems.size();
        while (true) {
            size2--;
            if (size2 < 0) {
                ObjectPool.releaseVector(newItems);
                return;
            }
            addItem((MenuItem) newItems.elementAt(size2));
        }
    }

    /* renamed from: a */
    public final Screen setSoftKeys(String str, String str2, int i, int i2, int i3) {
        GraphicsContext gfxCtx = AppState.getGfxContext(StateKeys.GFX_INDEX_DEFAULT);
        this.titleLeft = str;
        int textWidth = gfxCtx.stringWidth(str);
        this.titleRight = str2;
        this.titleMaxWidth = Utils.max(textWidth, gfxCtx.stringWidth(str2)) << 1;
        this.softKeyLeft = i;
        this.softKeyCenter = i2;
        this.softKeyRight = i3;
        return this;
    }

    /* renamed from: a */
    public final boolean onPointerEvent(int i, int i2, int i3, int i4, boolean z) {
        int itemX;
        int itemY;
        boolean z2;
        int itemX2;
        int itemY2;
        if (!this.touchConsumed) {
            return true;
        }
        AppController.needsRepaint = true;
        this.touchConsumed = false;
        int i5 = i - this.offsetX;
        int i6 = i2 - this.offsetY;
        int i7 = i3 - this.offsetX;
        int i8 = i4 - this.offsetY;
        if (this.screenId == ScreenId.MAP) {
            MapController.toggleMapControls(this);
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
                MenuItem menuItem = getItemAt(i12);
                if (menuItem.isEnabled() && i9 > (itemX2 = getItemX(i12)) && i10 > (itemY2 = getItemY(i12))) {
                    if (i9 < itemX2 + (this.layoutMode == 0 ? i11 : menuItem.getTotalWidth()) && i10 < itemY2 + menuItem.getTotalHeight()) {
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
        int expandedIdx = findVisibleExpanded();
        if (expandedIdx < this.menuItems.size()) {
            ((MenuItem) this.menuItems.elementAt(expandedIdx)).visible = false;
        }
        int size2 = this.menuItems.size();
        while (true) {
            size2--;
            if (size2 >= 0) {
                MenuItem menuItem2 = getItemAt(size2);
                if (menuItem2.id == 13 && i13 > (itemX = getItemX(size2)) && i14 > (itemY = getItemY(size2)) && i13 < itemX + i15 && i14 < itemY + menuItem2.getTotalHeight()) {
                    menuItem2.visible = true;
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
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(i);
            if (menuItem.id == 13 && menuItem.visible && getItemY(i) > this.scrollOffset && (getItemY(i) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
                size = i;
            }
        }
        return size;
    }

    /* renamed from: g */
    private int findPrevExpanded(int i) {
        int size = this.menuItems.size() + 1;
        for (int i2 = 0; i2 < this.menuItems.size(); i2++) {
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(i2);
            if (menuItem.id == 13 && !menuItem.visible && getItemY(i2) > this.scrollOffset && (getItemY(i2) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(i2) < getItemY(i)) {
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
                MenuItem menuItem = (MenuItem) this.menuItems.elementAt(i2);
                if (menuItem.id == 13 && !menuItem.visible && getItemY(i2) > this.scrollOffset && (getItemY(i2) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(i2) > getItemY(i)) {
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
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(i);
            if (menuItem.id == 13 && !menuItem.visible && getItemY(i) > this.scrollOffset && (getItemY(i) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
                size = i;
            }
        }
        return size;
    }

    /* renamed from: r */
    public final int getSelectedY() {
        int itemY = 0;
        if (this.selectedIndex > 0) {
            itemY = getItemY(this.selectedIndex);
        }
        return this.offsetY + itemY;
    }

    /* renamed from: a */
    public final Screen setOffset(int i, int i2) {
        this.offsetX = i;
        this.offsetY = i2;
        return this;
    }
}
