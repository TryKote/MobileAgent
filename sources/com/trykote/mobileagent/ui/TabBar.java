package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;

import javax.microedition.lcdui.Graphics;
import java.util.Vector;

public final class TabBar {

    // Tab types
    public static final int TYPE_CONTACTS = 4;
    public static final int TYPE_SEARCH = 6;
    public static final int TYPE_MAIL = 36;

    // Icon resource IDs
    private static final int ICON_CONTACTS = 156;
    private static final int ICON_MAIL = 240;
    private static final int ICON_SEARCH = 264;

    // Blinking icon IDs (bit 14 = blink flag)
    private static final int ICON_UNREAD_BLINK = 16384;
    private static final int ICON_CONNECTION_BLINK = 16385;
    private static final int ICON_ONLINE_BLINK = 16386;

    // Overflow indicator icon IDs (left/right arrows)
    private static final int ICON_OVERFLOW_RIGHT = 246;
    private static final int ICON_OVERFLOW_LEFT = 248;

    // Icon pixel size
    private static final int ICON_SIZE = 16;

    // Layout constants
    private static final int TAB_MARGIN = 20;
    private static final int TAB_WIDTH_PADDING = 26;
    private static final int OVERFLOW_BUFFER = 32;
    private static final int TAB_BAR_HEIGHT = 22;
    private static final int FONT_HEIGHT_OFFSET = 7;

    // Tab shape rendering constants
    private static final int TAB_BORDER_MARGIN = 2;
    private static final int TAB_CORNER_Y = 6;
    private static final int TAB_CONTENT_INSET = 4;
    private static final int TAB_FILL_START_Y = 3;
    private static final int TAB_FILL_RIGHT_INSET = 3;
    private static final int TEXT_LEFT_PADDING = 6;

    // Palette indices
    private static final int PALETTE_TEXT = 0;
    private static final int PALETTE_BACKGROUND = 1;
    private static final int PALETTE_BORDER = 16;
    private static final int PALETTE_INACTIVE_BG = 17;

    public static boolean scrollEnabled;

    public static int currentIndex;

    public static Account currentAccount;

    public String title;

    public int iconId;

    public int width;

    public int xOffset;

    public int type;

    public final Account account;

    public String selectedTitle;

    public int selectedIndex;

    private TabBar(int iconId, String title, int type, Account account) {
        this.iconId = iconId;
        this.title = title;
        this.type = type;
        this.width = GraphicsContext.getIconSize(iconId)
            + UIState.getGfxContext(UIKeys.GFX_INDEX_BOLD).stringWidth(title);
        this.account = account;
    }

    public static void initialize() {
        RemoteLogger.log("TAB", "initialize: accounts=" + SessionState.getAccounts().size()
            + " multiAcct=" + SettingsState.isMultiAccount());
        currentIndex = 0;
        currentAccount = null;
        UIState.setTabBars(ObjectPool.newVector());
        Vector accounts = SessionState.getAccounts();
        int accountCount = accounts.size();
        if (accountCount == 0 || !SettingsState.isMultiAccount()) {
            RemoteLogger.log("TAB", "addTab DEFAULT: icon=" + ICON_CONTACTS
                + " title=" + ResourceAccessor.str(StringResKeys.STR_TAB_CONTACTS));
            addTab(ICON_CONTACTS, ResourceAccessor.str(StringResKeys.STR_TAB_CONTACTS),
                TYPE_CONTACTS, null);
        } else {
            for (int idx = 0; idx < accountCount; idx++) {
                Account acct = (Account) accounts.elementAt(idx);
                RemoteLogger.log("TAB", "addTab ACCOUNT: icon=" + acct.getIconId()
                    + " name=" + acct.shortName + " login=" + acct.login);
                addTab(acct.getIconId(), acct.shortName, TYPE_CONTACTS, acct);
                if (idx == 0) {
                    currentAccount = acct;
                }
            }
        }
        if (SettingsState.isMailTabEnabled()) {
            RemoteLogger.log("TAB", "addTab MAIL: getBool(67)="
                + SettingsState.isMailTabEnabled());
            addTab(ICON_MAIL, ResourceAccessor.str(StringResKeys.STR_TAB_MAIL),
                TYPE_MAIL, null);
        }
        if (SettingsState.isSearchTabEnabled()) {
            RemoteLogger.log("TAB", "addTab SEARCH: getBool(68)="
                + SettingsState.isSearchTabEnabled());
            addTab(ICON_SEARCH, ResourceAccessor.str(StringResKeys.STR_TAB_SEARCH),
                TYPE_SEARCH, null);
        }
        layout();
        RemoteLogger.log("TAB", "initialize done: totalTabs="
            + UIState.getTabBars().size());
        AppController.needsRepaint = true;
    }

    public static void updateTitle(int iconId, String title) {
        Vector tabs = UIState.getTabBars();
        TabBar firstTab = (TabBar) tabs.elementAt(0);
        if (firstTab.iconId == iconId && firstTab.title == title) {
            return;
        }
        String savedTitle = firstTab.selectedTitle;
        int savedIndex = firstTab.selectedIndex;
        TabBar newTab = new TabBar(iconId, title, TYPE_CONTACTS, null);
        tabs.setElementAt(newTab, 0);
        newTab.selectedTitle = savedTitle;
        newTab.selectedIndex = savedIndex;
        layout();
        RemoteLogger.log("TAB", "initialize done: totalTabs="
            + UIState.getTabBars().size());
        AppController.needsRepaint = true;
    }

    private static void addTab(int iconId, String title, int tabType, Account account) {
        UIState.getTabBars().addElement(new TabBar(iconId, title, tabType, account));
    }

    public int selectTab() {
        Vector tabs = UIState.getTabBars();
        for (int idx = tabs.size() - 1; idx >= 0; idx--) {
            if (tabs.elementAt(idx) == this) {
                selectTabByIndex(idx);
                return this.type;
            }
        }
        return 0;
    }

    public static TabBar getNextTab() {
        int nextIndex = currentIndex + 1;
        Vector tabs = UIState.getTabBars();
        if (nextIndex < tabs.size()) {
            return (TabBar) tabs.elementAt(nextIndex);
        }
        return null;
    }

    public static TabBar getPreviousTab() {
        int prevIndex = currentIndex - 1;
        if (prevIndex >= 0) {
            return (TabBar) UIState.getTabBars().elementAt(prevIndex);
        }
        return null;
    }

    public static void ensureSettingsTab() {
        ensureDefaultTab();
        if (findTab(TYPE_MAIL, null) == null) {
            addTab(ICON_MAIL, ResourceAccessor.str(StringResKeys.STR_TAB_MAIL),
                TYPE_MAIL, null);
        }
    }

    public static void removeSettingsTab() {
        removeTabByType(SettingsState.isMailTabEnabled(), TYPE_MAIL);
    }

    public static void ensureSearchTab() {
        ensureDefaultTab();
        if (findTab(TYPE_SEARCH, null) == null) {
            addTab(ICON_SEARCH, ResourceAccessor.str(StringResKeys.STR_TAB_SEARCH),
                TYPE_SEARCH, null);
        }
    }

    public static void removeSearchTab() {
        removeTabByType(SettingsState.isSearchTabEnabled(), TYPE_SEARCH);
    }

    private static void removeTabByType(boolean enabled, int tabType) {
        if (enabled) return;
        Vector tabs = UIState.getTabBars();
        for (int idx = tabs.size() - 1; idx >= 0; idx--) {
            TabBar tab = (TabBar) tabs.elementAt(idx);
            if (tab.type == tabType) {
                tabs.removeElement(tab);
                layout();
                RemoteLogger.log("TAB", "initialize done: totalTabs="
                    + UIState.getTabBars().size());
                AppController.needsRepaint = true;
                return;
            }
        }
    }

    private static void ensureDefaultTab() {
        if (SettingsState.isMultiAccount()) return;
        updateTitle(ICON_CONTACTS, ResourceAccessor.str(StringResKeys.STR_TAB_CONTACTS));
    }

    public static TabBar getCurrentTab() {
        return (TabBar) UIState.getTabBars().elementAt(currentIndex);
    }

    public static TabBar findTab(int tabType, Account account) {
        Vector tabs = UIState.getTabBars();
        for (int idx = tabs.size() - 1; idx >= 0; idx--) {
            TabBar tab = (TabBar) tabs.elementAt(idx);
            if (tab.type == tabType && (account == null || tab.account == account)) {
                return tab;
            }
        }
        return null;
    }

    private static TabBar selectTabByIndex(int index) {
        if (currentIndex != index) {
            currentIndex = index;
            layout();
        }
        TabBar tab = (TabBar) UIState.getTabBars().elementAt(index);
        currentAccount = tab.account;
        return tab;
    }

    // --- Layout computation ---

    public static void layout() {
        Vector tabs = UIState.getTabBars();
        RemoteLogger.log("TAB", "layout: tabs="
            + (tabs != null ? String.valueOf(tabs.size()) : "null")
            + " currentIdx=" + currentIndex);
        if (tabs == null) return;

        ObjectPool.releaseVector(UIState.getTabItems());
        Vector layoutItems = ObjectPool.newVector();
        UIState.setTabItems(layoutItems);

        int tabCount = tabs.size();
        int selectedIdx = currentIndex;
        TabBar selectedTab = (TabBar) tabs.elementAt(selectedIdx);

        int totalWidth = 0;
        int selectedTabRight = 0;
        int xPos = TAB_MARGIN;
        for (int idx = 0; idx < tabCount; idx++) {
            TabBar tab = (TabBar) tabs.elementAt(idx);
            tab.width = TAB_WIDTH_PADDING
                + UIState.getGfxContext(UIKeys.GFX_INDEX_BOLD).stringWidth(tab.title);
            tab.xOffset = xPos;
            layoutItems.addElement(tab);
            xPos += tab.width;
            totalWidth += tab.width;
            if (idx == selectedIdx) {
                selectedTabRight = totalWidth;
            }
        }

        int availWidth = UIState.getScreenWidth() - TAB_MARGIN;
        totalWidth = trimLeftOverflow(layoutItems, tabs, selectedTab,
            selectedTabRight, totalWidth, availWidth);
        trimRightOverflow(layoutItems, tabs, selectedTab, totalWidth, availWidth);
        adjustFinalOverflow(layoutItems, selectedTab, availWidth);
    }

    private static int trimLeftOverflow(Vector layoutItems, Vector tabs,
            TabBar selectedTab, int selectedTabRight, int totalWidth,
            int availWidth) {
        while (selectedTabRight >= availWidth - OVERFLOW_BUFFER) {
            int leftIdx = findFirstVisibleIndex(tabs, layoutItems);
            TabBar leftTab = (TabBar) tabs.elementAt(leftIdx);
            if (leftTab == selectedTab) break;

            int removedWidth = leftTab.width;
            layoutItems.removeElement(leftTab);

            // Add or update left overflow arrow
            Object first = layoutItems.firstElement();
            int[] leftArrow;
            if (first instanceof int[]) {
                leftArrow = (int[]) first;
            } else {
                leftArrow = new int[]{TAB_MARGIN, ICON_OVERFLOW_LEFT};
                layoutItems.insertElementAt(leftArrow, 0);
                removedWidth -= ICON_SIZE;
            }

            // Connection blink for hidden mail tab
            if (leftTab.type == TYPE_MAIL && AccountManager.hasActiveConnection()) {
                if (leftArrow[1] == ICON_OVERFLOW_LEFT) {
                    layoutItems.insertElementAt(
                        new int[]{TAB_MARGIN, ICON_CONNECTION_BLINK}, 0);
                    leftArrow[0] += ICON_SIZE;
                } else {
                    layoutItems.insertElementAt(
                        new int[]{TAB_MARGIN + ICON_SIZE, ICON_CONNECTION_BLINK}, 1);
                    ((int[]) layoutItems.elementAt(2))[0] += ICON_SIZE;
                }
                removedWidth -= ICON_SIZE;
            }

            // Account status for hidden contacts tab
            if (leftTab.type == TYPE_CONTACTS) {
                Account account = leftTab.account;
                if (AccountManager.isAccountOnline(account)
                        && SettingsState.isMailTabEnabled()
                        && leftArrow[1] == ICON_OVERFLOW_LEFT) {
                    layoutItems.insertElementAt(new int[]{TAB_MARGIN,
                        AccountManager.getAccountStatus(account)}, 0);
                    leftArrow[0] += ICON_SIZE;
                    removedWidth -= ICON_SIZE;
                }
            }

            // Shift all tab offsets left
            for (int idx = 0; idx < tabs.size(); idx++) {
                ((TabBar) tabs.elementAt(idx)).xOffset -= removedWidth;
            }
            selectedTabRight -= removedWidth;
            totalWidth -= removedWidth;
        }
        return totalWidth;
    }

    private static void trimRightOverflow(Vector layoutItems, Vector tabs,
            TabBar selectedTab, int totalWidth, int availWidth) {
        while (totalWidth >= availWidth) {
            int rightIdx = findLastVisibleIndex(tabs, layoutItems);
            TabBar rightTab = (TabBar) tabs.elementAt(rightIdx);
            if (rightTab == selectedTab) break;

            int lastTabBonus = (rightTab == tabs.lastElement()) ? ICON_SIZE : 0;
            int overflow = (totalWidth - availWidth) + lastTabBonus;

            if (rightTab.width > overflow) {
                rightTab.width -= overflow;
                shiftTrailingIcons(layoutItems, -overflow);
            } else {
                totalWidth -= removeRightTab(layoutItems, rightTab);
            }
        }
    }

    private static int removeRightTab(Vector layoutItems, TabBar rightTab) {
        int removedWidth = rightTab.width;
        int tabX = rightTab.xOffset;
        layoutItems.removeElement(rightTab);

        // Add or update right overflow arrow
        Object last = layoutItems.lastElement();
        int[] rightArrow;
        if (last instanceof int[]) {
            rightArrow = (int[]) last;
            shiftTrailingIcons(layoutItems, -removedWidth);
        } else {
            rightArrow = new int[]{tabX, ICON_OVERFLOW_RIGHT};
            layoutItems.addElement(rightArrow);
            removedWidth -= ICON_SIZE;
        }

        // Connection blink for hidden mail tab
        if (rightTab.type == TYPE_MAIL && AccountManager.hasActiveConnection()
                && SettingsState.isMailTabEnabled()) {
            layoutItems.addElement(
                new int[]{tabX + ICON_SIZE, ICON_CONNECTION_BLINK});
            removedWidth -= ICON_SIZE;
        }

        // Account status for hidden contacts tab
        if (rightTab.type == TYPE_CONTACTS
                && AccountManager.isAccountOnline(rightTab.account)) {
            removedWidth = addRightAccountStatus(
                layoutItems, rightArrow, tabX, rightTab.account, removedWidth);
        }

        return removedWidth;
    }

    private static int addRightAccountStatus(Vector layoutItems, int[] rightArrow,
            int tabX, Account account, int removedWidth) {
        int statusIcon = AccountManager.getAccountStatus(account);
        if (rightArrow[1] == ICON_OVERFLOW_RIGHT) {
            layoutItems.addElement(new int[]{tabX + ICON_SIZE, statusIcon});
            return removedWidth - ICON_SIZE;
        }
        int beforeLastIdx = layoutItems.size() - 2;
        int existingIcon = ((int[]) layoutItems.elementAt(beforeLastIdx))[1];
        if (existingIcon != ICON_UNREAD_BLINK && existingIcon != ICON_ONLINE_BLINK) {
            layoutItems.insertElementAt(
                new int[]{tabX + ICON_SIZE, statusIcon}, beforeLastIdx + 1);
            rightArrow[0] += ICON_SIZE;
            return removedWidth - ICON_SIZE;
        }
        return removedWidth;
    }

    private static void adjustFinalOverflow(Vector layoutItems, TabBar selectedTab,
            int availWidth) {
        Object lastElem = layoutItems.lastElement();
        int lastRight;
        if (lastElem instanceof TabBar) {
            TabBar lastTab = (TabBar) lastElem;
            lastRight = (lastTab.xOffset + lastTab.width) - TAB_MARGIN;
        } else {
            lastRight = ((int[]) lastElem)[0] - TAB_CONTENT_INSET;
        }
        if (lastRight <= availWidth) return;

        int excess = lastRight - availWidth;
        selectedTab.width -= excess;
        shiftTrailingIcons(layoutItems, -excess);
    }

    private static int findFirstVisibleIndex(Vector tabs, Vector layoutItems) {
        for (int idx = 0; idx < tabs.size(); idx++) {
            if (layoutItems.contains(tabs.elementAt(idx))) return idx;
        }
        return 0;
    }

    private static int findLastVisibleIndex(Vector tabs, Vector layoutItems) {
        for (int idx = tabs.size() - 1; idx >= 0; idx--) {
            if (layoutItems.contains(tabs.elementAt(idx))) return idx;
        }
        return 0;
    }

    private static void shiftTrailingIcons(Vector layoutItems, int delta) {
        for (int idx = layoutItems.size() - 1; idx >= 0; idx--) {
            Object elem = layoutItems.elementAt(idx);
            if (!(elem instanceof int[])) break;
            ((int[]) elem)[0] += delta;
        }
    }

    // --- Rendering ---

    public static void paintTopBar(GraphicsContext g) {
        g.setFont(UIState.getGfxContext(UIKeys.GFX_INDEX_BOLD));
        TabBar selectedTab = (TabBar) UIState.getTabBars().elementAt(currentIndex);
        Vector items = UIState.getTabItems();
        for (int idx = items.size() - 1; idx >= 0; idx--) {
            Object element = items.elementAt(idx);
            if (element instanceof TabBar) {
                paintTab(g, (TabBar) element,
                    element == selectedTab && !scrollEnabled);
            } else {
                int[] iconData = (int[]) element;
                int iconX = iconData[0];
                int centerY = TAB_CONTENT_INSET + ScreenManager.getCenterOffset();
                g.setClip(iconX, centerY, ICON_SIZE, ICON_SIZE);
                g.drawIcon(iconData[1], iconX, centerY);
            }
        }
    }

    private static void paintTab(GraphicsContext g, TabBar tab, boolean isSelected) {
        int tabX = tab.xOffset;
        int tabWidth = tab.width;
        int textBottom = UIState.getIntOffset(UIKeys.OFFSET_BOLD_FONT_HEIGHT)
            + FONT_HEIGHT_OFFSET;

        // Draw tab border
        g.setColorFromPalette(PALETTE_BORDER)
            .setClip(tabX, TAB_BORDER_MARGIN, tabWidth,
                textBottom - TAB_BORDER_MARGIN)
            .drawLine(tabX, textBottom, tabX, TAB_CORNER_Y)
            .drawLine(tabX, TAB_CORNER_Y,
                tabX + TAB_CONTENT_INSET, TAB_BORDER_MARGIN)
            .drawLine(tabX + TAB_CONTENT_INSET, TAB_BORDER_MARGIN,
                tabX + tabWidth - TAB_BORDER_MARGIN, TAB_BORDER_MARGIN)
            .drawLine(tabX + tabWidth - TAB_BORDER_MARGIN, TAB_BORDER_MARGIN,
                tabX + tabWidth - TAB_BORDER_MARGIN, textBottom)
            .setColorFromPalette(
                isSelected ? PALETTE_BACKGROUND : PALETTE_INACTIVE_BG);

        // Fill tab interior
        int fillBottom = isSelected ? textBottom : textBottom - 1;
        for (int row = TAB_FILL_START_Y; row < fillBottom; row++) {
            int leftInset = (row < TAB_CORNER_Y) ? TAB_CORNER_Y - row : 0;
            g.drawLine(tabX + 1 + leftInset, row,
                tabX + tabWidth - TAB_FILL_RIGHT_INSET, row);
        }

        // Resolve tab icon
        int iconCode;
        if (tab.account == null) {
            iconCode = resolveTabIcon(tab.iconId);
        } else {
            iconCode = AccountManager.getAccountStatus(tab.account);
        }

        // Draw icon and title
        int centerY = TAB_CONTENT_INSET + ScreenManager.getCenterOffset();
        g.drawIcon(iconCode, tabX + TAB_CONTENT_INSET, centerY)
            .setColorFromPalette(PALETTE_TEXT)
            .setClip(tabX, TAB_BORDER_MARGIN,
                tabWidth - TAB_FILL_RIGHT_INSET,
                textBottom - TAB_BORDER_MARGIN)
            .drawString(tab.title, tabX + TEXT_LEFT_PADDING + ICON_SIZE,
                TAB_CONTENT_INSET, Graphics.TOP | Graphics.LEFT);
    }

    private static int resolveTabIcon(int iconId) {
        if (iconId == ICON_MAIL && AccountManager.hasActiveConnection()) {
            return ICON_CONNECTION_BLINK;
        }
        if (iconId == ICON_MAIL || iconId == ICON_SEARCH
                || UIState.getOnlineContacts().size() <= 0) {
            return iconId;
        }
        return ICON_UNREAD_BLINK;
    }

    // --- Hit testing ---

    public static Object hitTest(int x, int y) {
        Vector items = UIState.getTabItems();
        for (int idx = items.size() - 1; idx >= 0; idx--) {
            Object elem = items.elementAt(idx);
            if (elem instanceof int[]) {
                int[] iconData = (int[]) elem;
                if (x >= iconData[0] && x < iconData[0] + ICON_SIZE
                        && y >= 0 && y <= TAB_BAR_HEIGHT) {
                    return iconData;
                }
            } else {
                TabBar tab = (TabBar) elem;
                if (x >= tab.xOffset && y >= 0
                        && x <= tab.xOffset + tab.width
                        && y <= TAB_BAR_HEIGHT) {
                    return tab;
                }
            }
        }
        return null;
    }
}
