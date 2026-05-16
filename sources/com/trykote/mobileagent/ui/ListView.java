package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.util.BitMath;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Graphics;
import java.util.Enumeration;
import java.util.Vector;

public class ListView {

    // Layout modes
    static final int LAYOUT_VERTICAL = 0;
    static final int LAYOUT_GRID = 1;
    private static final int LAYOUT_MAP_VIEW = 2;

    // Scroll / expand directions
    private static final int EXPAND_UP = 1;
    private static final int EXPAND_DOWN = 2;

    // Sentinel: no expanded item visible
    private static final int NO_EXPANDED_ITEM = 1000000;

    // Palette color indices (for setColorFromPalette)
    private static final int PALETTE_TEXT = 0;
    private static final int PALETTE_BACKGROUND = 1;
    private static final int PALETTE_BACKGROUND_ALT = 2;
    private static final int PALETTE_SELECTION = 13;
    private static final int PALETTE_SOFT_KEY_TEXT = 15;
    private static final int PALETTE_BORDER = 16;
    private static final int PALETTE_TAB_FILL = 17;

    // Dimension constants
    private static final int SCROLL_STEP = 20;
    private static final int DEFAULT_ITEM_WIDTH = 200;
    private static final int SCROLLBAR_WIDTH = 7;
    private static final int MAX_CLIP_HEIGHT = 2048;
    private static final int MIN_TAB_HEIGHT = 16;
    private static final int TAB_ICON_SPACING = 18;
    private static final int LAYOUT_CACHE_INITIAL_SIZE = 16;

    // Layout insets
    private static final int OUTER_PADDING = 4;
    private static final int INNER_MARGIN = 2;
    private static final int BORDER_INSET = 3;

    // Threshold for triggering layout rebuild (pixels of excess content height)
    private static final int LAYOUT_REBUILD_THRESHOLD = 5;

    // Margin reserved for clock display in soft key bar
    private static final int CLOCK_MARGIN = 6;

    // Text formatting constants (for MenuItem.addText)
    private static final int TEXT_FORMAT_EXPANDABLE = 5;
    private static final int TEXT_FORMAT_SECONDARY = 6;

    public int screenId;
    public int containerWidth;
    public int containerHeight;
    public int screenType;
    public int offsetX;
    public int offsetY;
    private MenuItem headerItem;
    public final int layoutMode;
    public boolean selectable;
    private boolean hasScrollbar;
    public int scrollOffset;
    public int selectedIndex;
    public int totalHeight;
    public Vector menuItems;
    public int[] layoutCache;
    public Vector tabItems;
    private int innerWidth;
    private int headerHeight;
    public int contentTop;
    public int contentWidth;
    public int contentHeight;
    private int borderWidth;
    private int contentStart;
    private int contentBottom;
    private int scrollRange;
    public int softKeyLeft;
    public int softKeyCenter;
    public int softKeyRight;
    public String titleLeft;
    public String titleRight;
    public boolean showCheckboxes;
    private int titleMaxWidth;
    public boolean reverseScroll;
    private int visibleExpandedIndex;
    private int expandDirection;
    public int marginLeft;
    public int marginTop;
    public boolean touchConsumed;

    public ListView() {
        this.layoutMode = LAYOUT_MAP_VIEW;
        this.screenId = ScreenId.STATUS_INPUT;
    }
    public final ListView initTabs() {
        if (SettingsState.isHeaderVisible()) {
            this.tabItems = ObjectPool.newVector();
            recalcLayout();
        }
        return this;
    }

    private ListView(int layoutMode, int screenId, int width, int height) {
        this.menuItems = ObjectPool.newVector();
        this.layoutCache = new int[LAYOUT_CACHE_INITIAL_SIZE];
        this.layoutMode = layoutMode;
        this.containerWidth = width;
        this.containerHeight = height;
        this.screenId = screenId;
        this.selectedIndex = -1;
        recalcLayout();
    }

    public ListView(int layoutMode, int screenId, int width, int height, boolean selectable) {
        this(layoutMode, screenId, width, height);
        this.selectable = selectable;
    }
    private ListView recalcLayout() {
        int usableWidth = this.containerWidth - OUTER_PADDING;
        int usableHeight = this.containerHeight - OUTER_PADDING;
        this.innerWidth = this.containerWidth - INNER_MARGIN;
        if (this.headerItem != null) {
            this.headerHeight = this.headerItem.getTotalHeight() + INNER_MARGIN;
        } else {
            this.headerHeight = 0;
        }
        this.borderWidth = this.containerWidth - BORDER_INSET;
        this.contentStart = 1 + this.headerHeight;
        this.contentBottom = (usableHeight - this.contentStart) + BORDER_INSET;
        this.contentTop = this.contentStart + 1;
        this.contentWidth = usableWidth - INNER_MARGIN;
        this.contentHeight = this.contentBottom - INNER_MARGIN;
        if (this.tabItems != null) {
            int prevBottom = this.contentBottom;
            int barHeight = Utils.max(UIState.getFontHeight(), MIN_TAB_HEIGHT) + BORDER_INSET;
            this.contentBottom = prevBottom - barHeight;
            this.contentHeight -= barHeight;
        }
        return this;
    }
    public final String getSelectedTitle() {
        if (!this.selectable || this.menuItems.size() <= 0 || this.selectedIndex < 0) {
            return null;
        }
        return getItemAt(this.selectedIndex).title;
    }
    public final int getSelectedWidth() {
        if (!this.selectable || this.menuItems.size() <= 0) {
            return DEFAULT_ITEM_WIDTH;
        }
        return getItemAt(this.selectedIndex).width;
    }
    public final MenuItem getSelectedItem() {
        if (!this.selectable || this.menuItems.size() <= 0) {
            return null;
        }
        return getItemAt(this.selectedIndex);
    }
    public final MenuItem getHeaderItem() {
        int expandedIdx;
        if (this.screenType != ScreenManager.TYPE_FULLSCREEN_NOSCROLL_ALT || (expandedIdx = findVisibleExpanded()) >= this.menuItems.size()) {
            return null;
        }
        return getItemAt(expandedIdx);
    }
    public final ListView setHeader(int iconId, String title) {
        this.headerItem = MenuItem.createSeparator().addText(ResourceAccessor.str(StringResKeys.STR_PLACEHOLDER_TEXT), UIKeys.GFX_INDEX_BOLD, 0).setLabelInternal(iconId, title, UIKeys.GFX_INDEX_BOLD, 0);
        recalcLayout();
        return this;
    }
    public final ListView addItem(MenuItem menuItem) {
        if (this.layoutMode == LAYOUT_VERTICAL) {
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
            this.scrollRange = ((this.contentBottom - OUTER_PADDING) * this.contentHeight) / this.totalHeight;
            this.hasScrollbar = true;
        }
        if (this.selectable && this.selectedIndex < 0 && menuItem.isEnabled()) {
            this.selectedIndex = this.menuItems.size() - 1;
        }
        return this;
    }
    public final ListView buildLayout() {
        if (this.contentHeight - this.totalHeight > LAYOUT_REBUILD_THRESHOLD && this.layoutMode == LAYOUT_VERTICAL) {
            this.containerHeight = this.headerHeight + this.totalHeight + OUTER_PADDING;
            recalcLayout();
        }
        int maxItemWidth = 0;
        Enumeration elements = this.menuItems.elements();
        while (elements.hasMoreElements()) {
            int prevMax = maxItemWidth;
            int itemWidth = ((MenuItem) elements.nextElement()).getContentWidth();
            if (prevMax < itemWidth) {
                maxItemWidth = itemWidth;
            }
        }
        int neededWidth = maxItemWidth + MIN_TAB_HEIGHT;
        if (neededWidth < this.containerWidth) {
            this.containerWidth = neededWidth;
        }
        recalcLayout();
        return this;
    }
    public final void paint(GraphicsContext g, boolean isTop, boolean isModal) {
        if (this.layoutMode != LAYOUT_MAP_VIEW) {
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
            if (isTop && SettingsState.isStatusBarVisible()) {
                paintSoftKeys(g);
            }
        }
        if (this.screenType == ScreenManager.TYPE_FULLSCREEN_ALT || this.screenType == ScreenManager.TYPE_MAP_ALT) {
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
        int x = this.offsetX;
        int y = this.offsetY;
        int w = this.containerWidth;
        int h = this.containerHeight;
        Graphics gfx = g.graphics;
        g.setColorFromPalette(isTop ? PALETTE_BACKGROUND : PALETTE_BACKGROUND_ALT);
        gfx.fillRect(x, y, w, h);
        g.setColorFromPalette(PALETTE_BORDER);
        gfx.drawRect(x, y, w - 1, h - 1);
    }

    private void paintHeaderGradient(GraphicsContext g) {
        int x = this.offsetX + 1;
        int y = this.offsetY + 1;
        g.setClip(x, y, this.innerWidth, this.headerHeight);
        int themeIdx = SettingsState.getColorTheme();
        int startColor = Palette.getColor(themeIdx, Palette.GRADIENT_START);
        if (startColor != Palette.getColor(themeIdx, Palette.GRADIENT_END)) {
            int startR = (startColor >> 16) & 0xFF;
            int startG = (startColor >> 8) & 0xFF;
            int startB = startColor & 0xFF;
            for (int row = 1; row < this.headerHeight; row++) {
                int r = interpolateChannel(startR, row, this.headerHeight);
                int gr = interpolateChannel(startG, row, this.headerHeight);
                int b = interpolateChannel(startB, row, this.headerHeight);
                g.setColor((r << 16) | (gr << 8) | b);
                g.drawRect(x, y + row, this.innerWidth, 0);
            }
        } else {
            g.setColor(startColor);
            g.fillRect(x, y, this.innerWidth, this.headerHeight);
        }
        this.headerItem.render(g, x, y, 0);
    }

    private static int interpolateChannel(int channelValue, int row, int height) {
        return 0xFF - ((row * (0xFF - channelValue)) / height);
    }

    private void paintMenuItems(GraphicsContext g) {
        int baseX = this.offsetX + INNER_MARGIN;
        int baseY = this.offsetY + this.contentTop;
        int availWidth = this.hasScrollbar ? this.contentWidth : this.contentWidth + INNER_MARGIN;
        int size = this.menuItems.size();
        boolean isGridLayout = this.layoutMode != LAYOUT_VERTICAL;
        int scrollOff = this.scrollOffset;
        for (int idx = 0; idx < size; idx++) {
            int itemY = getItemY(idx);
            if (itemY - scrollOff > this.contentHeight) {
                break;
            }
            MenuItem menuItem = getItemAt(idx);
            int itemHeight = menuItem.getTotalHeight();
            if (itemY + itemHeight >= scrollOff) {
                int itemX = getItemX(idx);
                int itemWidth = menuItem.getTotalWidth();
                g.setClip(baseX, baseY, availWidth, this.contentHeight);
                int drawX = baseX + itemX;
                int drawY = (baseY + itemY) - scrollOff;
                int drawWidth = this.layoutMode == LAYOUT_VERTICAL ? availWidth : itemWidth;
                int itemType = menuItem.id;
                if (this.selectable && idx == this.selectedIndex && itemType != MenuItem.TYPE_GRAPHICS) {
                    g.setColorFromPalette(PALETTE_SELECTION);
                    g.fillRect(drawX, drawY, drawWidth, itemHeight);
                }
                if (itemType == MenuItem.TYPE_EXPANDABLE && menuItem.visible) {
                    g.setColorFromPalette(PALETTE_SELECTION);
                    g.fillRect(drawX, drawY, drawWidth, itemHeight);
                }
                boolean isVisible = !isGridLayout || intersectClip(g, drawX, drawY, drawWidth, itemHeight);
                if (isVisible) {
                    menuItem.render(g, drawX, drawY, availWidth);
                }
            }
        }
    }

    private boolean intersectClip(GraphicsContext g, int x, int y, int width, int height) {
        Graphics gfx = g.graphics;
        int clipX = gfx.getClipX();
        int clipY = gfx.getClipY();
        int clipRight = clipX + gfx.getClipWidth();
        int clipBottom = clipY + gfx.getClipHeight();
        if (x + width < clipX || y + height < clipY || x > clipRight || y > clipBottom) {
            return false;
        }
        if (clipRight < x + width) {
            width = clipRight - x;
        }
        if (clipBottom < y + height) {
            height = clipBottom - y;
        }
        if (clipX > x) {
            int delta = clipX - x;
            x = clipX;
            width -= delta;
        }
        if (width <= 0) {
            return false;
        }
        if (clipY > y) {
            int delta = clipY - y;
            y = clipY;
            height -= delta;
        }
        if (height <= 0) {
            return false;
        }
        gfx.setClip(x, y, width, height);
        return true;
    }

    private void paintScrollbar(GraphicsContext g) {
        int barX = this.offsetX + this.borderWidth;
        int barY = this.offsetY + this.contentStart;
        g.setClip(barX, barY, SCROLLBAR_WIDTH, this.contentBottom + OUTER_PADDING);
        g.setColorFromPalette(PALETTE_BORDER);
        g.fillRect(barX + 1, barY + (this.totalHeight == 0 ? 0 : Utils.min(((this.contentBottom - OUTER_PADDING) * this.scrollOffset) / this.totalHeight, (this.contentBottom - OUTER_PADDING) - this.scrollRange)), 1, this.scrollRange + INNER_MARGIN);
        g.drawRect(barX, barY - 1, INNER_MARGIN, this.contentBottom + 1);
    }

    private void paintBottomTabBar(GraphicsContext g) {
        int barHeight = Utils.max(UIState.getFontHeight(), MIN_TAB_HEIGHT);
        int screenHeight = UIState.getHeight() - 1;
        int screenWidth = UIState.getScreenWidth();
        g.setClip(0, (screenHeight - barHeight) - BORDER_INSET, screenWidth, barHeight + OUTER_PADDING).setColorFromPalette(PALETTE_BORDER).fillRect(0, (screenHeight - barHeight) - BORDER_INSET, screenWidth, barHeight + OUTER_PADDING).setColorFromPalette(PALETTE_TAB_FILL).fillRect(1, (screenHeight - barHeight) - INNER_MARGIN, screenWidth - INNER_MARGIN, barHeight + INNER_MARGIN).setColorFromPalette(PALETTE_TEXT).setFont(UIState.getGfxContext(UIKeys.GFX_INDEX_DEFAULT));
        Vector tabs = this.tabItems;
        int tabX = BORDER_INSET;
        boolean pastLabel = false;
        int centerY = ((screenHeight - barHeight) - 1) + ScreenManager.getCenterOffset();
        for (int idx = 0; idx < tabs.size(); idx++) {
            Object element = tabs.elementAt(idx);
            if (!(element instanceof Integer)) {
                pastLabel = true;
                g.drawString((String) element, tabX, (screenHeight - barHeight) - 1, Graphics.TOP | Graphics.LEFT);
                tabX = screenWidth;
            } else if (pastLabel) {
                tabX -= TAB_ICON_SPACING;
                g.drawIcon(((Integer) element).intValue(), tabX, centerY);
            } else {
                g.drawIcon(((Integer) element).intValue(), BORDER_INSET, centerY);
                tabX += TAB_ICON_SPACING;
            }
        }
    }

    private void paintSoftKeys(GraphicsContext g) {
        int screenWidth = UIState.getScreenWidth();
        int screenHeight = UIState.getHeight();
        g.setClip(0, 0, screenWidth, MAX_CLIP_HEIGHT + screenHeight);
        g.setFont(UIState.getGfxContext(UIKeys.GFX_INDEX_DEFAULT));
        g.setColorFromPalette(PALETTE_SOFT_KEY_TEXT);
        if (this.titleLeft != null) {
            g.drawString(this.titleLeft, 1, screenHeight, Graphics.TOP | Graphics.LEFT);
        }
        if (this.titleRight != null) {
            g.drawString(this.titleRight, screenWidth - 1, screenHeight, Graphics.TOP | Graphics.RIGHT);
        }
        if (AppController.clockWidth + this.titleMaxWidth < screenWidth - CLOCK_MARGIN) {
            g.drawString(Utils.defaultStr(UIState.getClockString()), screenWidth >> 1, screenHeight, Graphics.TOP | Graphics.HCENTER);
        }
    }

    private void paintTopTabBar(GraphicsContext g) {
        TabBar.paintTopBar(g);
    }

    private void paintMapOverlay(GraphicsContext g) {
        MapRenderer.paintOverlay(g, this.offsetX + INNER_MARGIN, this.offsetY + this.contentTop, this.containerWidth, this.contentHeight);
    }

    private void paintContactPopup(GraphicsContext g) {
        ContactListManager.paintPopup(g, this.offsetX + INNER_MARGIN, this.offsetY + this.contentTop, this.containerWidth, this.contentHeight);
    }
    public final boolean isAtEnd() {
        int size = this.menuItems.size();
        if (size == 0) {
            return true;
        }
        int current = this.selectedIndex;
        int next = (current + 1) % size;
        return next <= current || getItemX(next) <= getItemX(current);
    }

    public final boolean isAtStart() {
        int size = this.menuItems.size();
        if (size == 0) {
            return true;
        }
        int current = this.selectedIndex;
        int prev = ((current + size) - 1) % size;
        return prev >= current || getItemX(prev) >= getItemX(current);
    }
    public final void onActionKey() {
        MenuItem selectedItem = getSelectedItem();
        if (selectedItem != null && selectedItem.enabled) {
            EventDispatcher.postSelectEvent();
            return;
        }
        if (this.screenId == ScreenId.MAP) {
            MapState.setScrollDirection(0);
            return;
        }
        if (this.screenId != ScreenId.CONTACT_LIST) {
            if (this.layoutMode == LAYOUT_GRID) {
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
        int current = this.selectedIndex;
        int next = (current + 1) % size;
        if (next <= current || getItemX(next) <= getItemX(current)) {
            return;
        }
        this.selectedIndex = next;
        invalidateLayout();
    }
    public final int pageUp() {
        if (this.menuItems.isEmpty()) {
            return 0;
        }
        if (this.selectable) {
            this.scrollOffset -= this.contentHeight - SCROLL_STEP;
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
            this.scrollOffset -= this.contentHeight - SCROLL_STEP;
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
        }
        invalidateLayout();
        return 0;
    }
    public final int pageDown() {
        int size = this.menuItems.size();
        if (size == 0) {
            return 0;
        }
        findLastVisible();
        this.scrollOffset += this.contentHeight - SCROLL_STEP;
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
    }    public final void scrollUp() {
        if (this.screenId == ScreenId.MAP) {
            MapState.setScrollDirection(2);
            return;
        }
        if (this.menuItems.size() == 0) {
            return;
        }
        if (!this.selectable) {
            if (this.screenType != ScreenManager.TYPE_FULLSCREEN_NOSCROLL_ALT) {
                scrollOffsetUp(SCROLL_STEP);
            } else {
                scrollExpandable(EXPAND_UP);
            }
            return;
        }
        if (this.layoutMode == LAYOUT_VERTICAL) {
            scrollUpVertical();
        } else {
            scrollUpGrid(getItemX(this.selectedIndex), getItemY(this.selectedIndex));
        }
    }

    private void scrollOffsetUp(int amount) {
        this.scrollOffset -= amount;
        if (this.scrollOffset < 0) {
            this.scrollOffset = 0;
        }
    }

    private boolean scrollOffsetDown(int amount) {
        if (this.scrollOffset + this.contentHeight < this.totalHeight) {
            this.scrollOffset += amount;
            return true;
        }
        return false;
    }

    private void scrollUpVertical() {
        int itemY = getItemY(this.selectedIndex);
        if (itemY < this.scrollOffset) {
            this.scrollOffset -= SCROLL_STEP;
            return;
        }
        boolean atTop;
        if (this.selectedIndex == 0) {
            atTop = true;
        } else {
            atTop = true;
            for (int idx = this.selectedIndex - 1; idx >= 0; idx--) {
                if (getItemAt(idx).isEnabled()) {
                    atTop = false;
                    break;
                }
            }
        }
        if (!atTop) {
            for (int idx = this.selectedIndex - 1; idx >= 0; idx--) {
                if (getItemAt(idx).isEnabled()) {
                    this.selectedIndex = idx;
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
        this.selectedIndex = this.menuItems.size() - 1;
        this.scrollOffset = this.totalHeight - this.contentHeight;
        if (this.scrollOffset < 0) {
            this.scrollOffset = 0;
        }
    }

    private void scrollUpGrid(int currentX, int currentY) {
        int bestIdx = -1;
        int bestY = 0;
        for (int idx = this.selectedIndex - 1; idx >= 0; idx--) {
            int rowY = getItemY(idx);
            if (rowY != currentY) {
                if (bestIdx == -1) {
                    bestIdx = idx;
                    bestY = rowY;
                } else if (rowY < bestY) {
                    this.selectedIndex = bestIdx;
                    invalidateLayout();
                    return;
                }
            }
            int colX = getItemX(idx);
            if (colX == currentX || (colX == 0 && rowY != currentY)) {
                this.selectedIndex = idx;
                invalidateLayout();
                return;
            }
        }
        this.selectedIndex = this.menuItems.size() - 1;
        invalidateLayout();
    }
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
    public final ListView measureContent() {
        this.scrollOffset = Utils.max(0, this.totalHeight - this.contentHeight);
        int size = this.menuItems.size();
        if (size > 1) {
            this.scrollOffset = Utils.min(this.scrollOffset, getItemY(size - 2));
        }
        if (this.screenType == ScreenManager.TYPE_FULLSCREEN_NOSCROLL_ALT) {
            int anyExpanded = findAnyExpanded();
            if (anyExpanded < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(anyExpanded)).visible = true;
                this.visibleExpandedIndex = anyExpanded;
            } else {
                this.visibleExpandedIndex = NO_EXPANDED_ITEM;
            }
            this.expandDirection = EXPAND_DOWN;
        }
        return this;
    }
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
            if (this.screenType != ScreenManager.TYPE_FULLSCREEN_NOSCROLL_ALT) {
                scrollOffsetDown(SCROLL_STEP);
            } else {
                scrollExpandable(EXPAND_DOWN);
            }
            return;
        }
        if (this.layoutMode != LAYOUT_VERTICAL) {
            scrollDownGrid(getItemX(this.selectedIndex), getItemY(this.selectedIndex), size);
        } else {
            scrollDownVertical(size);
        }
    }

    private void scrollDownVertical(int size) {
        if (this.selectedIndex >= size - 1) {
            int prevOffset = this.scrollOffset;
            this.scrollOffset += SCROLL_STEP;
            int itemHeight = getItemAt(this.selectedIndex).getTotalHeight();
            int itemY = getItemY(this.selectedIndex);
            if (this.scrollOffset > (itemY + itemHeight) - this.contentHeight) {
                this.scrollOffset -= this.scrollOffset - ((itemY + itemHeight) - this.contentHeight);
            }
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
            if (this.scrollOffset != prevOffset || this.reverseScroll) {
                return;
            }
            this.scrollOffset = 0;
            this.selectedIndex = 0;
            if (this.selectable) {
                while (!getItemAt(this.selectedIndex).isEnabled()) {
                    this.selectedIndex++;
                }
            }
            return;
        }
        MenuItem nextItem = null;
        for (int idx = this.selectedIndex + 1; idx < size; idx++) {
            MenuItem item = getItemAt(idx);
            if (item.isEnabled()) {
                this.selectedIndex = idx;
                nextItem = item;
                break;
            }
        }
        if (nextItem == null) {
            return;
        }
        int itemHeight = nextItem.getTotalHeight();
        int itemY = getItemY(this.selectedIndex);
        if (itemY + itemHeight >= this.scrollOffset + this.contentHeight) {
            if (itemHeight <= this.contentHeight) {
                this.scrollOffset = (itemY + itemHeight) - this.contentHeight;
            } else {
                this.scrollOffset += SCROLL_STEP;
            }
        }
    }

    private void scrollDownGrid(int currentX, int currentY, int size) {
        int bestIdx = -1;
        int bestY = 0;
        int idx = this.selectedIndex;
        do {
            idx++;
            if (idx >= size) {
                this.selectedIndex = 0;
                invalidateLayout();
                return;
            }
            int rowY = getItemY(idx);
            if (rowY != currentY) {
                if (bestIdx == -1) {
                    bestIdx = idx;
                    bestY = rowY;
                } else if (rowY > bestY) {
                    this.selectedIndex = bestIdx;
                    invalidateLayout();
                    return;
                }
            }
        } while (getItemX(idx) != currentX);
        this.selectedIndex = idx;
        invalidateLayout();
    }
    private int findLastVisible() {
        int lo = this.selectable ? this.selectedIndex : 0;
        int hi = this.menuItems.size() - 1;
        int mid = -1;
        int prev;
        do {
            prev = mid;
            mid = (lo + hi) >> 1;
            if (getItemY(mid) > this.scrollOffset + this.contentHeight) {
                hi = mid - 1;
            } else {
                lo = mid;
            }
        } while (mid != prev);
        if (mid < this.menuItems.size() - 1 && isItemVisible(mid + 1)) {
            mid++;
        }
        int result = mid;
        if (!isItemFullyVisible(result) && !getItemAt(result).isEnabled()) {
            result--;
        }
        return result;
    }
    private MenuItem getItemAt(int index) {
        if (index >= 0) {
            return (MenuItem) this.menuItems.elementAt(index);
        }
        return null;
    }

    private int getItemX(int index) {
        return this.layoutCache[(index << 1) + 1];
    }

    private int getItemY(int index) {
        return this.layoutCache[(index << 1) + 2];
    }

    private boolean isItemVisible(int index) {
        int itemY = getItemY(index);
        return itemY < this.scrollOffset + this.contentHeight && itemY + getItemAt(index).getTotalHeight() > this.scrollOffset;
    }

    private boolean isItemFullyVisible(int index) {
        int itemY = getItemY(index);
        return itemY >= this.scrollOffset && (itemY + getItemAt(index).getTotalHeight()) - 1 <= this.scrollOffset + this.contentHeight;
    }
    private int findFirstVisible() {
        int hi = this.selectable ? this.selectedIndex : this.menuItems.size() - 1;
        int lo = 0;
        int mid = -1;
        int prev;
        do {
            prev = mid;
            mid = (lo + hi) >> 1;
            if (getItemY(mid) + getItemAt(mid).getTotalHeight() < this.scrollOffset) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        } while (mid != prev);
        if (!isItemVisible(mid)) {
            mid++;
        } else if (mid > 0 && isItemVisible(mid - 1)) {
            mid--;
        }
        return mid;
    }
    public final ListView addTextPair(String label, String value, int width) {
        return addFullItem(MenuItem.NO_ICON, label, value, width, null);
    }

    public final ListView addIconItem(int iconId, String text, int width) {
        return addFullItem(iconId, null, text, width, null);
    }

    public final ListView addIconItemWithData(int iconId, String text, int width, Object data) {
        return addFullItem(iconId, null, text, width, data);
    }

    public final ListView addLabelValue(String label, String value) {
        return addTextPair(label, value, DEFAULT_ITEM_WIDTH);
    }

    public final ListView addIconById(int iconId, int stringKey, int width) {
        return addIconItem(iconId, AppState.getString(stringKey), width);
    }

    public final ListView addExpandableItem(int iconId, String text, int width, Object data) {
        MenuItem expandItem = new MenuItem(MenuItem.TYPE_EXPANDABLE, AppState.emptyStr).setIcon(iconId).addText(text, TEXT_FORMAT_EXPANDABLE, width);
        expandItem.data = data;
        return addItem(expandItem);
    }

    public final ListView addSeparator(String text, int textStyle) {
        return addItem(MenuItem.createSeparator().addText(text, UIKeys.GFX_INDEX_BOLD, textStyle));
    }

    public final ListView addActionById(int iconId, int stringKey, int width) {
        String labelStr = AppState.getString(stringKey);
        MenuItem actionItem = MenuItem.createWithWidth(labelStr, width).setIcon(iconId).setLabel(labelStr).setIcon(MenuItem.ICON_ALIGN_RIGHT);
        actionItem.enabled = true;
        return addItem(actionItem);
    }

    public final ListView addTextItem(String text) {
        return addIconItem(MenuItem.NO_ICON, text, DEFAULT_ITEM_WIDTH);
    }

    public final ListView addIconTextItem(int iconId, String text, int width) {
        return addItem(MenuItem.createWithWidth(text, width).setIcon(iconId));
    }

    public final ListView addLabelById(int stringKey) {
        return addItem(MenuItem.createSeparator().setLabel(AppState.getString(stringKey)));
    }

    public final ListView addFullItem(int iconId, String label, String text, int width, Object data) {
        RemoteLogger.log("SCR", "addFullItem icon=" + iconId + " str2='" + text + "' w=" + width);
        MenuItem newItem = MenuItem.createWithWidth(text, width);
        if (iconId >= 0) {
            newItem.setIcon(iconId);
        }
        if (label != null) {
            newItem.addText(label, 0, TEXT_FORMAT_SECONDARY);
        }
        if (text != null) {
            int separatorIdx = text.indexOf(0);
            newItem.setLabel(separatorIdx < 0 ? text : StringUtils.prefix(text, separatorIdx));
        }
        newItem.data = data;
        return addItem(newItem);
    }
    public final ListView selectByTitle(String title) {
        if (title != null) {
            int idx = 0;
            Enumeration elements = this.menuItems.elements();
            while (elements.hasMoreElements()) {
                if (title.equals(((MenuItem) elements.nextElement()).title)) {
                    this.selectedIndex = idx;
                    break;
                }
                idx++;
            }
        }
        return this;
    }
    public final void rebuildItems() {
        Vector newItems = ObjectPool.newVector();
        for (int idx = this.menuItems.size() - 1; idx >= 0; idx--) {
            newItems.addElement(this.menuItems.elementAt(idx));
        }
        this.menuItems.removeAllElements();
        this.layoutCache[0] = 0;
        this.totalHeight = 0;
        for (int idx = newItems.size() - 1; idx >= 0; idx--) {
            addItem((MenuItem) newItems.elementAt(idx));
        }
        ObjectPool.releaseVector(newItems);
    }
    public final ListView setSoftKeys(String left, String right, int leftCmd, int centerCmd, int rightCmd) {
        GraphicsContext gfxCtx = UIState.getGfxContext(UIKeys.GFX_INDEX_DEFAULT);
        this.titleLeft = left;
        int textWidth = gfxCtx.stringWidth(left);
        this.titleRight = right;
        this.titleMaxWidth = Utils.max(textWidth, gfxCtx.stringWidth(right)) << 1;
        this.softKeyLeft = leftCmd;
        this.softKeyCenter = centerCmd;
        this.softKeyRight = rightCmd;
        return this;
    }
    public final boolean onPointerEvent(int eventX, int eventY, int startX, int startY, boolean wasDragged) {
        if (!this.touchConsumed) {
            return true;
        }
        AppController.needsRepaint = true;
        this.touchConsumed = false;
        int localX = eventX - this.offsetX;
        int localY = eventY - this.offsetY;
        int localStartX = startX - this.offsetX;
        int localStartY = startY - this.offsetY;
        if (this.screenId == ScreenId.MAP) {
            MapController.toggleMapControls(this);
            MapRenderer.onTap(localX, localY - this.contentTop);
            return true;
        }
        if (this.selectable) {
            int hitX = (wasDragged ? localStartX : localX) - INNER_MARGIN;
            int hitY = ((wasDragged ? localStartY : localY) + this.scrollOffset) - this.contentTop;
            int size = this.menuItems.size();
            int availWidth = this.hasScrollbar ? this.contentWidth : this.contentWidth + INNER_MARGIN;
            boolean itemHit = false;
            for (int idx = 0; idx < size; idx++) {
                MenuItem menuItem = getItemAt(idx);
                int itemX = getItemX(idx);
                int itemY = getItemY(idx);
                if (menuItem.isEnabled() && hitX > itemX && hitY > itemY) {
                    if (hitX < itemX + (this.layoutMode == LAYOUT_VERTICAL ? availWidth : menuItem.getTotalWidth()) && hitY < itemY + menuItem.getTotalHeight()) {
                        if (this.selectedIndex != idx || wasDragged) {
                            this.selectedIndex = idx;
                        } else {
                            AppController.onItemSelected();
                        }
                        itemHit = true;
                        break;
                    }
                }
            }
            if (itemHit) {
                return true;
            }
        }
        if (wasDragged) {
            return false;
        }
        if (this.screenType != ScreenManager.TYPE_FULLSCREEN_NOSCROLL_ALT) {
            if (this.screenType == ScreenManager.TYPE_FULLSCREEN || this.screenType == ScreenManager.TYPE_FULLSCREEN_ALT) {
                return true;
            }
            AppController.onItemSelected();
            return true;
        }
        handleExpandableTap(localX, localY);
        return true;
    }

    // === Expandable items ===

    private void scrollExpandable(int direction) {
        int expandedIdx = findVisibleExpanded();
        if (expandedIdx < this.menuItems.size()) {
            ((MenuItem) this.menuItems.elementAt(expandedIdx)).visible = false;
            int neighbor = direction == EXPAND_UP ? findPrevExpanded(expandedIdx) : findNextExpanded(expandedIdx);
            if (neighbor < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(neighbor)).visible = true;
                this.visibleExpandedIndex = neighbor;
            } else if (direction == EXPAND_UP) {
                scrollOffsetUp(SCROLL_STEP);
            } else {
                scrollOffsetDown(SCROLL_STEP);
            }
        } else if (this.visibleExpandedIndex < this.menuItems.size()) {
            int neighbor = direction == EXPAND_UP ? findPrevExpanded(this.visibleExpandedIndex) : findNextExpanded(this.visibleExpandedIndex);
            int itemY = getItemY(this.visibleExpandedIndex);
            int itemBottom = itemY + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight();
            boolean isVisible = itemY > this.scrollOffset && itemBottom - this.scrollOffset <= this.contentHeight;
            if (isVisible && this.expandDirection != direction) {
                neighbor = this.visibleExpandedIndex;
            }
            if (isVisible && this.expandDirection == direction) {
                boolean atEdge = direction == EXPAND_UP
                        ? itemY - this.scrollOffset <= SCROLL_STEP
                        : this.contentHeight - (itemBottom - this.scrollOffset) <= SCROLL_STEP;
                if (atEdge) {
                    neighbor = this.visibleExpandedIndex;
                }
            }
            if (neighbor < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(neighbor)).visible = true;
                this.visibleExpandedIndex = neighbor;
            } else if (direction == EXPAND_UP) {
                scrollOffsetUp(SCROLL_STEP);
            } else {
                scrollOffsetDown(SCROLL_STEP);
            }
        } else {
            int anyExpanded = findAnyExpanded();
            if (anyExpanded < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(anyExpanded)).visible = true;
                this.visibleExpandedIndex = anyExpanded;
            } else if (direction == EXPAND_UP) {
                scrollOffsetUp(SCROLL_STEP);
            } else {
                scrollOffsetDown(SCROLL_STEP);
            }
        }
        this.expandDirection = direction;
    }

    private void handleExpandableTap(int tapLocalX, int tapLocalY) {
        int tapX = tapLocalX - INNER_MARGIN;
        int tapScrollY = (tapLocalY + this.scrollOffset) - this.contentTop;
        int availWidth = this.hasScrollbar ? this.contentWidth : this.contentWidth + INNER_MARGIN;
        int expandedIdx = findVisibleExpanded();
        if (expandedIdx < this.menuItems.size()) {
            ((MenuItem) this.menuItems.elementAt(expandedIdx)).visible = false;
        }
        for (int idx = this.menuItems.size() - 1; idx >= 0; idx--) {
            MenuItem menuItem = getItemAt(idx);
            int itemX = getItemX(idx);
            int itemY = getItemY(idx);
            if (menuItem.id == MenuItem.TYPE_EXPANDABLE && tapX > itemX && tapScrollY > itemY && tapX < itemX + availWidth && tapScrollY < itemY + menuItem.getTotalHeight()) {
                menuItem.visible = true;
                break;
            }
        }
        AppController.onItemSelected();
    }
    private int findVisibleExpanded() {
        int result = this.menuItems.size() + 1;
        for (int idx = 0; idx < this.menuItems.size(); idx++) {
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(idx);
            if (menuItem.id == MenuItem.TYPE_EXPANDABLE && menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
                result = idx;
            }
        }
        return result;
    }
    private int findPrevExpanded(int fromIndex) {
        int result = this.menuItems.size() + 1;
        for (int idx = 0; idx < this.menuItems.size(); idx++) {
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(idx);
            if (menuItem.id == MenuItem.TYPE_EXPANDABLE && !menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(idx) < getItemY(fromIndex)) {
                result = idx;
            }
        }
        return result;
    }

    private int findNextExpanded(int fromIndex) {
        int result = this.menuItems.size() + 1;
        for (int idx = 0; idx < this.menuItems.size(); idx++) {
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(idx);
            if (menuItem.id == MenuItem.TYPE_EXPANDABLE && !menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(idx) > getItemY(fromIndex)) {
                result = idx;
                break;
            }
        }
        return result;
    }
    private int findAnyExpanded() {
        int result = this.menuItems.size() + 1;
        for (int idx = 0; idx < this.menuItems.size(); idx++) {
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(idx);
            if (menuItem.id == MenuItem.TYPE_EXPANDABLE && !menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
                result = idx;
            }
        }
        return result;
    }
    public final int getSelectedY() {
        int itemY = 0;
        if (this.selectedIndex > 0) {
            itemY = getItemY(this.selectedIndex);
        }
        return this.offsetY + itemY;
    }
    public final ListView setOffset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
        return this;
    }
}
