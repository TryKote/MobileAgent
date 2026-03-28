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

public final class ListView {
    public int screenId;
    public int containerWidth;
    public int containerHeight;
    public int screenType;
    public int offsetX;
    public int offsetY;
    public int definitionOffset;
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
        this.layoutMode = 2;
        this.screenId = ScreenId.STATUS_INPUT;
    }
    public final ListView initTabs() {
        if (AppState.getBool(StateKeys.SETTING_HEADER_VISIBLE)) {
            this.tabItems = ObjectPool.newVector();
            recalcLayout();
        }
        return this;
    }

    private ListView(int layoutMode, int screenId, int width, int height) {
        this.menuItems = ObjectPool.newVector();
        this.layoutCache = new int[16];
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
    private final ListView recalcLayout() {
        int usableWidth = this.containerWidth - 4;
        int usableHeight = this.containerHeight - 4;
        this.innerWidth = this.containerWidth - 2;
        if (this.headerItem != null) {
            this.headerHeight = this.headerItem.getTotalHeight() + 2;
        } else {
            this.headerHeight = 0;
        }
        this.borderWidth = this.containerWidth - 3;
        this.contentStart = 1 + this.headerHeight;
        this.contentBottom = (usableHeight - this.contentStart) + 3;
        this.contentTop = this.contentStart + 1;
        this.contentWidth = usableWidth - 2;
        this.contentHeight = this.contentBottom - 2;
        if (this.tabItems != null) {
            int prevBottom = this.contentBottom;
            int barHeight = Utils.max(AppState.getInt(StateKeys.INT_FONT_HEIGHT), 16) + 3;
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
            return 200;
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
        if (this.screenType != 9 || (expandedIdx = findVisibleExpanded()) >= this.menuItems.size()) {
            return null;
        }
        return getItemAt(expandedIdx);
    }
    public final ListView setHeader(int iconId, String title) {
        this.headerItem = MenuItem.createSeparator().addText(AppState.getString(StateKeys.STR_PLACEHOLDER_TEXT), 1, 0).setLabelInternal(iconId, title, 1, 0);
        recalcLayout();
        return this;
    }
    public final ListView addItem(MenuItem menuItem) {
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
    public final ListView buildLayout() {
        if (this.contentHeight - this.totalHeight > 5 && this.layoutMode == 0) {
            this.containerHeight = this.headerHeight + this.totalHeight + 4;
            recalcLayout();
        }
        int maxItemHeight = 0;
        Enumeration elements = this.menuItems.elements();
        while (elements.hasMoreElements()) {
            int prevMax = maxItemHeight;
            int itemMax = ((MenuItem) elements.nextElement()).getMaxHeight();
            if (prevMax < itemMax) {
                maxItemHeight = itemMax;
            }
        }
        int neededWidth = maxItemHeight + 16;
        if (neededWidth < this.containerWidth) {
            this.containerWidth = neededWidth;
        }
        recalcLayout();
        return this;
    }
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
        int x = this.offsetX;
        int y = this.offsetY;
        int w = this.containerWidth;
        int h = this.containerHeight;
        Graphics gfx = g.graphics;
        g.setColorFromPalette(isTop ? 1 : 2);
        gfx.fillRect(x, y, w, h);
        g.setColorFromPalette(16);
        gfx.drawRect(x, y, w - 1, h - 1);
    }

    private void paintHeaderGradient(GraphicsContext g) {
        int x = this.offsetX + 1;
        int y = this.offsetY + 1;
        g.setClip(x, y, this.innerWidth, this.headerHeight);
        int themeIdx = AppState.getInt(StateKeys.SETTING_COLOR_THEME);
        int startColor = AppState.getInt(PaletteKeys.GRADIENT_START + themeIdx);
        if (startColor != AppState.getInt(PaletteKeys.GRADIENT_END + themeIdx)) {
            for (int row = 1; row < this.headerHeight; row++) {
                g.setColor(((255 - ((row * (255 - (startColor >> 16))) / this.headerHeight)) << 16) | ((255 - ((row * (255 - ((startColor >> 8) & 255))) / this.headerHeight)) << 8) | (255 - ((row * (255 - (startColor & 255))) / this.headerHeight)));
                g.drawRect(x, y + row, this.innerWidth, 0);
            }
        } else {
            g.setColor(startColor);
            g.fillRect(x, y, this.innerWidth, this.headerHeight);
        }
        this.headerItem.render(g, x, y, 0);
    }

    private void paintMenuItems(GraphicsContext g) {
        int baseX = this.offsetX + 2;
        int baseY = this.offsetY + this.contentTop;
        int availWidth = this.hasScrollbar ? this.contentWidth : this.contentWidth + 2;
        int size = this.menuItems.size();
        boolean isGridLayout = this.layoutMode != 0;
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
                int drawWidth = this.layoutMode == 0 ? availWidth : itemWidth;
                int itemType = menuItem.id;
                if (this.selectable && idx == this.selectedIndex && itemType != 11) {
                    g.setColorFromPalette(13);
                    g.fillRect(drawX, drawY, drawWidth, itemHeight);
                }
                if (itemType == 13 && menuItem.visible) {
                    g.setColorFromPalette(13);
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
        g.setClip(barX, barY, 7, this.contentBottom + 4);
        g.setColorFromPalette(16);
        g.fillRect(barX + 1, barY + (this.totalHeight == 0 ? 0 : Utils.min(((this.contentBottom - 4) * this.scrollOffset) / this.totalHeight, (this.contentBottom - 4) - this.scrollRange)), 1, this.scrollRange + 2);
        g.drawRect(barX, barY - 1, 2, this.contentBottom + 1);
    }

    private void paintBottomTabBar(GraphicsContext g) {
        int barHeight = Utils.max(AppState.getInt(StateKeys.INT_FONT_HEIGHT), 16);
        int screenHeight = AppState.getHeight() - 1;
        int screenWidth = AppState.getInt(StateKeys.INT_SCREEN_WIDTH);
        g.setClip(0, (screenHeight - barHeight) - 3, screenWidth, barHeight + 4).setColorFromPalette(16).fillRect(0, (screenHeight - barHeight) - 3, screenWidth, barHeight + 4).setColorFromPalette(17).fillRect(1, (screenHeight - barHeight) - 2, screenWidth - 2, barHeight + 2).setColorFromPalette(0).setFont(AppState.getGfxContext(StateKeys.GFX_INDEX_DEFAULT));
        Vector tabs = this.tabItems;
        int tabX = 3;
        boolean pastLabel = false;
        int centerY = ((screenHeight - barHeight) - 1) + ScreenManager.getCenterOffset();
        for (int idx = 0; idx < tabs.size(); idx++) {
            Object element = tabs.elementAt(idx);
            if (!(element instanceof Integer)) {
                pastLabel = true;
                g.drawString((String) element, tabX, (screenHeight - barHeight) - 1, 20);
                tabX = screenWidth;
            } else if (pastLabel) {
                tabX -= 18;
                g.drawIcon(((Integer) element).intValue(), tabX, centerY);
            } else {
                g.drawIcon(((Integer) element).intValue(), 3, centerY);
                tabX += 18;
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
        TabBar.paintTopBar(g);
    }

    private void paintMapOverlay(GraphicsContext g) {
        MapRenderer.paintOverlay(g, this.offsetX + 2, this.offsetY + this.contentTop, this.containerWidth, this.contentHeight);
    }

    private void paintContactPopup(GraphicsContext g) {
        ContactListManager.paintPopup(g, this.offsetX + 2, this.offsetY + this.contentTop, this.containerWidth, this.contentHeight);
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
        int current = this.selectedIndex;
        int next = (current + 1) % size;
        if (next <= current || getItemX(next) <= getItemX(current)) {
            return;
        }
        this.selectedIndex = next;
        invalidateLayout();
    }
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
            AppState.setInt(StateKeys.INT_MAP_SCROLL_DIRECTION, 2);
            return;
        }
        if (this.menuItems.size() == 0) {
            return;
        }
        if (!this.selectable) {
            if (this.screenType != 9) {
                scrollOffsetUp(20);
            } else {
                scrollExpandable(1);
            }
            return;
        }
        if (this.layoutMode == 0) {
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
            this.scrollOffset -= 20;
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
        if (this.selectable) {
            this.selectedIndex = this.menuItems.size() - 1;
            this.scrollOffset = this.totalHeight - this.contentHeight;
            if (this.scrollOffset < 0) {
                this.scrollOffset = 0;
            }
            return;
        }
        if (this.totalHeight < this.contentHeight) {
            this.scrollOffset = 0;
        } else if (((MenuItem) this.menuItems.lastElement()).getTotalHeight() < this.contentHeight) {
            this.scrollOffset = this.totalHeight - this.contentHeight;
        } else {
            int[] cache = this.layoutCache;
            this.scrollOffset = cache[cache[0]];
        }
    }

    private void scrollUpGrid(int currentX, int currentY) {
        int bestIdx = -1;
        int bestY = 0;
        int idx = this.selectedIndex;
        while (true) {
            idx--;
            if (idx < 0) {
                this.selectedIndex = this.menuItems.size() - 1;
                invalidateLayout();
                return;
            }
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
                break;
            }
        }
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
                scrollOffsetDown(20);
            } else {
                scrollExpandable(2);
            }
            return;
        }
        if (this.layoutMode != 0) {
            scrollDownGrid(getItemX(this.selectedIndex), getItemY(this.selectedIndex), size);
        } else {
            scrollDownVertical(size);
        }
    }

    private void scrollDownVertical(int size) {
        if (this.selectedIndex >= size - 1) {
            int prevOffset = this.scrollOffset;
            this.scrollOffset += 20;
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
        MenuItem currentItem = null;
        int idx = this.selectedIndex;
        while (true) {
            idx++;
            if (idx > size) {
                break;
            }
            if (idx == size) {
                return;
            }
            currentItem = getItemAt(idx);
            if (currentItem.isEnabled()) {
                this.selectedIndex = idx;
                break;
            }
        }
        int itemHeight = currentItem.getTotalHeight();
        int itemY = getItemY(this.selectedIndex);
        if (itemY + itemHeight >= this.scrollOffset + this.contentHeight) {
            if (itemHeight <= this.contentHeight) {
                this.scrollOffset = (itemY + itemHeight) - this.contentHeight;
            } else {
                this.scrollOffset += 20;
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
    private final int findLastVisible() {
        int mid;
        int lo = this.selectable ? this.selectedIndex : 0;
        int hi = this.menuItems.size() - 1;
        int prev = -1;
        while (true) {
            mid = (lo + hi) >> 1;
            if (mid == prev) {
                break;
            }
            prev = mid;
            if (getItemY(mid) > this.scrollOffset + this.contentHeight) {
                hi = mid - 1;
            } else {
                lo = mid;
            }
        }
        if (mid < this.menuItems.size() - 1 && isItemVisible(mid + 1)) {
            mid++;
        }
        int result = mid;
        if (!isItemFullyVisible(result) && !getItemAt(result).isEnabled()) {
            result--;
        }
        return result;
    }
    private final MenuItem getItemAt(int index) {
        if (index >= 0) {
            return (MenuItem) this.menuItems.elementAt(index);
        }
        return null;
    }

    private final int getItemX(int index) {
        return this.layoutCache[(index << 1) + 1];
    }

    private final int getItemY(int index) {
        return this.layoutCache[(index << 1) + 1 + 1];
    }

    private final boolean isItemVisible(int index) {
        int itemY = getItemY(index);
        return itemY < this.scrollOffset + this.contentHeight && itemY + getItemAt(index).getTotalHeight() > this.scrollOffset;
    }

    private final boolean isItemFullyVisible(int index) {
        int itemY = getItemY(index);
        return itemY >= this.scrollOffset && (itemY + getItemAt(index).getTotalHeight()) - 1 <= this.scrollOffset + this.contentHeight;
    }
    private final int findFirstVisible() {
        int mid;
        int hi = this.selectable ? this.selectedIndex : this.menuItems.size() - 1;
        int lo = 0;
        int prev = -1;
        while (true) {
            mid = (lo + hi) >> 1;
            if (mid == prev) {
                break;
            }
            prev = mid;
            if (getItemY(mid) + getItemAt(mid).getTotalHeight() < this.scrollOffset) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        if (!isItemVisible(mid)) {
            mid++;
        } else if (mid > 0 && isItemVisible(mid - 1)) {
            mid--;
        }
        return mid;
    }
    public final ListView addTextPair(String label, String value, int width) {
        return addFullItem(-1, label, value, width, (Object) null);
    }

    public final ListView addIconItem(int iconId, String text, int width) {
        return addFullItem(iconId, (String) null, text, width, (Object) null);
    }

    public final ListView addIconItemWithData(int iconId, String text, int width, Object data) {
        return addFullItem(iconId, (String) null, text, width, data);
    }

    public final ListView addLabelValue(String label, String value) {
        return addTextPair(label, value, 200);
    }

    public final ListView addIconById(int iconId, int stringKey, int width) {
        return addIconItem(iconId, AppState.getString(stringKey), width);
    }

    public final ListView addExpandableItem(int iconId, String text, int width, Object data) {
        MenuItem expandItem = new MenuItem(13, AppState.emptyStr).setIcon(iconId).addText(text, 5, width);
        expandItem.data = data;
        return addItem(expandItem);
    }

    public final ListView addSeparator(String text, int textStyle) {
        return addItem(MenuItem.createSeparator().addText(text, 1, textStyle));
    }

    public final ListView addActionById(int iconId, int stringKey, int width) {
        String labelStr = AppState.getString(stringKey);
        MenuItem actionItem = MenuItem.createWithWidth(labelStr, width).setIcon(iconId).setLabel(labelStr).setIcon(244);
        actionItem.enabled = true;
        return addItem(actionItem);
    }

    public final ListView addTextItem(String text) {
        return addIconItem(-1, text, 200);
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
            newItem.addText(label, 0, 6);
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
            while (true) {
                if (!elements.hasMoreElements()) {
                    break;
                }
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
    public final ListView setSoftKeys(String left, String right, int leftCmd, int centerCmd, int rightCmd) {
        GraphicsContext gfxCtx = AppState.getGfxContext(StateKeys.GFX_INDEX_DEFAULT);
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
            int hitX = (wasDragged ? localStartX : localX) - 2;
            int hitY = ((wasDragged ? localStartY : localY) + this.scrollOffset) - this.contentTop;
            int size = this.menuItems.size();
            int availWidth = this.hasScrollbar ? this.contentWidth : this.contentWidth + 2;
            boolean itemHit = false;
            for (int idx = 0; idx < size; idx++) {
                MenuItem menuItem = getItemAt(idx);
                int itemX = getItemX(idx);
                int itemY = getItemY(idx);
                if (menuItem.isEnabled() && hitX > itemX && hitY > itemY) {
                    if (hitX < itemX + (this.layoutMode == 0 ? availWidth : menuItem.getTotalWidth()) && hitY < itemY + menuItem.getTotalHeight()) {
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
        if (this.screenType != 9) {
            if (this.screenType == 0 || this.screenType == 1) {
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
            int neighbor = direction == 1 ? findPrevExpanded(expandedIdx) : findNextExpanded(expandedIdx);
            if (neighbor < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(neighbor)).visible = true;
                this.visibleExpandedIndex = neighbor;
            } else if (direction == 1) {
                scrollOffsetUp(20);
            } else {
                scrollOffsetDown(20);
            }
        } else if (this.visibleExpandedIndex < this.menuItems.size()) {
            int neighbor = direction == 1 ? findPrevExpanded(this.visibleExpandedIndex) : findNextExpanded(this.visibleExpandedIndex);
            int itemY = getItemY(this.visibleExpandedIndex);
            int itemBottom = itemY + ((MenuItem) this.menuItems.elementAt(this.visibleExpandedIndex)).getTotalHeight();
            boolean isVisible = itemY > this.scrollOffset && itemBottom - this.scrollOffset <= this.contentHeight;
            if (isVisible && this.expandDirection != direction) {
                neighbor = this.visibleExpandedIndex;
            }
            if (isVisible && this.expandDirection == direction) {
                boolean atEdge = direction == 1
                        ? itemY - this.scrollOffset <= 20
                        : this.contentHeight - (itemBottom - this.scrollOffset) <= 20;
                if (atEdge) {
                    neighbor = this.visibleExpandedIndex;
                }
            }
            if (neighbor < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(neighbor)).visible = true;
                this.visibleExpandedIndex = neighbor;
            } else if (direction == 1) {
                scrollOffsetUp(20);
            } else {
                scrollOffsetDown(20);
            }
        } else {
            int anyExpanded = findAnyExpanded();
            if (anyExpanded < this.menuItems.size()) {
                ((MenuItem) this.menuItems.elementAt(anyExpanded)).visible = true;
                this.visibleExpandedIndex = anyExpanded;
            } else if (direction == 1) {
                scrollOffsetUp(20);
            } else {
                scrollOffsetDown(20);
            }
        }
        this.expandDirection = direction;
    }

    private void handleExpandableTap(int tapLocalX, int tapLocalY) {
        int tapX = tapLocalX - 2;
        int tapScrollY = (tapLocalY + this.scrollOffset) - this.contentTop;
        int availWidth = this.hasScrollbar ? this.contentWidth : this.contentWidth + 2;
        int expandedIdx = findVisibleExpanded();
        if (expandedIdx < this.menuItems.size()) {
            ((MenuItem) this.menuItems.elementAt(expandedIdx)).visible = false;
        }
        for (int idx = this.menuItems.size() - 1; idx >= 0; idx--) {
            MenuItem menuItem = getItemAt(idx);
            int itemX = getItemX(idx);
            int itemY = getItemY(idx);
            if (menuItem.id == 13 && tapX > itemX && tapScrollY > itemY && tapX < itemX + availWidth && tapScrollY < itemY + menuItem.getTotalHeight()) {
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
            if (menuItem.id == 13 && menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
                result = idx;
            }
        }
        return result;
    }
    private int findPrevExpanded(int fromIndex) {
        int result = this.menuItems.size() + 1;
        for (int idx = 0; idx < this.menuItems.size(); idx++) {
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(idx);
            if (menuItem.id == 13 && !menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(idx) < getItemY(fromIndex)) {
                result = idx;
            }
        }
        return result;
    }

    private int findNextExpanded(int fromIndex) {
        int result = this.menuItems.size() + 1;
        for (int idx = 0; idx < this.menuItems.size(); idx++) {
            MenuItem menuItem = (MenuItem) this.menuItems.elementAt(idx);
            if (menuItem.id == 13 && !menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight && getItemY(idx) > getItemY(fromIndex)) {
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
            if (menuItem.id == 13 && !menuItem.visible && getItemY(idx) > this.scrollOffset && (getItemY(idx) + menuItem.getTotalHeight()) - this.scrollOffset <= this.contentHeight) {
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
