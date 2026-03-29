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
import java.util.Vector;

/* renamed from: ah */
/* loaded from: MobileAgent_3.9.jar:ah.class */
public final class TabBar {

    /* renamed from: a */
    public static boolean scrollEnabled;

    /* renamed from: b */
    public static int currentIndex;

    /* renamed from: c */
    public String title;

    /* renamed from: d */
    public int iconId;

    /* renamed from: e */
    public int width;

    /* renamed from: f */
    public int xOffset;

    /* renamed from: g */
    public int type;

    /* renamed from: h */
    public final Account account;

    /* renamed from: i */
    public String selectedTitle;

    /* renamed from: j */
    public int selectedIndex;

    /* renamed from: k */
    public static Account currentAccount;

    private TabBar(int i, String str, int i2, Account acct) {
        this.iconId = i;
        this.title = str;
        this.type = i2;
        this.width = GraphicsContext.getIconSize(i) + Storage.state().getGfxContext(UIKeys.GFX_INDEX_BOLD).stringWidth(str);
        this.account = acct;
    }

    /* renamed from: a */
    public static final void initialize() {
        RemoteLogger.log("TAB", "initialize: accounts=" + Storage.state().getVector(SessionKeys.VEC_ACCOUNTS).size() + " multiAcct=" + Storage.state().getBool(SettingsKeys.SETTING_MULTI_ACCOUNT));
        currentIndex = 0;
        currentAccount = null;
        Storage.state().setObject(UIKeys.VEC_TAB_BARS, ObjectPool.newVector());
        Vector tabs = Storage.state().getVector(SessionKeys.VEC_ACCOUNTS);
        int size = tabs.size();
        if (size == 0 || !Storage.state().getBool(SettingsKeys.SETTING_MULTI_ACCOUNT)) {
            RemoteLogger.log("TAB", "addTab DEFAULT: icon=156 title=" + Storage.resources().getString(StringResKeys.STR_TAB_CONTACTS));
            addTab(156, Storage.resources().getString(StringResKeys.STR_TAB_CONTACTS), 4, null);
        } else {
            for (int i = 0; i < size; i++) {
                Account acct = (Account) tabs.elementAt(i);
                RemoteLogger.log("TAB", "addTab ACCOUNT: icon=" + acct.getIconId() + " name=" + acct.shortName + " login=" + acct.login);
                addTab(acct.getIconId(), acct.shortName, 4, acct);
                if (i == 0) {
                    currentAccount = acct;
                }
            }
        }
        if (Storage.state().getBool(SettingsKeys.SETTING_MAIL_TAB_ENABLED)) {
            RemoteLogger.log("TAB", "addTab MAIL: getBool(67)=" + Storage.state().getBool(SettingsKeys.SETTING_MAIL_TAB_ENABLED));
            addTab(240, Storage.resources().getString(StringResKeys.STR_TAB_MAIL), 36, null);
        }
        if (Storage.state().getBool(SettingsKeys.SETTING_SEARCH_TAB_ENABLED)) {
            RemoteLogger.log("TAB", "addTab SEARCH: getBool(68)=" + Storage.state().getBool(SettingsKeys.SETTING_SEARCH_TAB_ENABLED));
            addTab(264, Storage.resources().getString(StringResKeys.STR_TAB_SEARCH), 6, null);
        }
        layout();
        RemoteLogger.log("TAB", "initialize done: totalTabs=" + Storage.state().getVector(UIKeys.VEC_TAB_BARS).size());
        AppController.needsRepaint = true;
    }

    /* renamed from: a */
    public static final void updateTitle(int i, String str) {
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_BARS);
        TabBar tab = (TabBar) tabs.elementAt(0);
        if (tab.iconId == i && tab.title == str) {
            return;
        }
        String str2 = tab.selectedTitle;
        int i2 = tab.selectedIndex;
        TabBar iterTab = new TabBar(i, str, 4, null);
        tabs.setElementAt(iterTab, 0);
        iterTab.selectedTitle = str2;
        iterTab.selectedIndex = i2;
        layout();
        RemoteLogger.log("TAB", "initialize done: totalTabs=" + Storage.state().getVector(UIKeys.VEC_TAB_BARS).size());
        AppController.needsRepaint = true;
    }

    /* renamed from: a */
    private static void addTab(int i, String str, int i2, Account acct) {
        Storage.state().getVector(UIKeys.VEC_TAB_BARS).addElement(new TabBar(i, str, i2, acct));
    }

    /* renamed from: b */
    public final int selectTab() {
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_BARS);
        int size = tabs.size();
        do {
            size--;
            if (size < 0) {
                return 0;
            }
        } while (tabs.elementAt(size) != this);
        selectTabByIndex(size);
        return this.type;
    }

    /* renamed from: c */
    public static final TabBar getNextTab() {
        int i = currentIndex + 1;
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_BARS);
        if (i < tabs.size()) {
            return (TabBar) tabs.elementAt(i);
        }
        return null;
    }

    /* renamed from: d */
    public static final TabBar getPreviousTab() {
        int i = currentIndex - 1;
        if (i >= 0) {
            return (TabBar) Storage.state().getVector(UIKeys.VEC_TAB_BARS).elementAt(i);
        }
        return null;
    }

    /* renamed from: e */
    public static final void ensureSettingsTab() {
        ensureDefaultTab();
        if (findTab(36, (Account) null) == null) {
            addTab(240, Storage.resources().getString(StringResKeys.STR_TAB_MAIL), 36, null);
        }
    }

    /* renamed from: f */
    public static final void removeSettingsTab() {
        removeTabByType(67, 36);
    }

    /* renamed from: g */
    public static final void ensureSearchTab() {
        ensureDefaultTab();
        if (findTab(6, (Account) null) == null) {
            addTab(264, Storage.resources().getString(StringResKeys.STR_TAB_SEARCH), 6, null);
        }
    }

    /* renamed from: h */
    public static final void removeSearchTab() {
        removeTabByType(68, 6);
    }

    /* renamed from: b */
    private static final void removeTabByType(int i, int i2) {
        TabBar tab;
        if (Storage.state().getBool(i)) {
            return;
        }
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_BARS);
        int size = tabs.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                tab = (TabBar) tabs.elementAt(size);
            }
        } while (tab.type != i2);
        tabs.removeElement(tab);
        layout();
        RemoteLogger.log("TAB", "initialize done: totalTabs=" + Storage.state().getVector(UIKeys.VEC_TAB_BARS).size());
        AppController.needsRepaint = true;
    }

    /* renamed from: k */
    private static final void ensureDefaultTab() {
        if (Storage.state().getBool(SettingsKeys.SETTING_MULTI_ACCOUNT)) {
            return;
        }
        updateTitle(156, Storage.resources().getString(StringResKeys.STR_TAB_CONTACTS));
    }

    /* renamed from: i */
    public static final TabBar getCurrentTab() {
        return (TabBar) Storage.state().getVector(UIKeys.VEC_TAB_BARS).elementAt(currentIndex);
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0032, code lost:
    
        return selectTabByIndex(r6);
     */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final TabBar findTab(int i, Account acct) {
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_BARS);
        int size = tabs.size();
        while (true) {
            size--;
            if (size >= 0) {
                TabBar tab = (TabBar) tabs.elementAt(size);
                if (tab.type == i && (acct == null || tab.account == acct)) {
                    return tab;
                }
            } else {
                return null;
            }
        }
    }

    /* renamed from: a */
    private static final TabBar selectTabByIndex(int i) {
        if (currentIndex != i) {
            currentIndex = i;
            layout();
        }
        TabBar tab = (TabBar) Storage.state().getVector(UIKeys.VEC_TAB_BARS).elementAt(i);
        currentAccount = tab.account;
        return tab;
    }

    /* renamed from: j */
    public static final void layout() {
        int i;
        Object elem;
        int[] iArr;
        Object leftElem;
        int[] iArr2;
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_BARS);
        RemoteLogger.log("TAB", "layout: tabs=" + (tabs != null ? String.valueOf(tabs.size()) : "null") + " currentIdx=" + currentIndex);
        if (tabs == null) {
            return;
        }
        ObjectPool.releaseVector(Storage.state().getVector(UIKeys.VEC_TAB_ITEMS));
        Vector layoutItems = ObjectPool.newVector();
        Storage.state().setObject(UIKeys.VEC_TAB_ITEMS, layoutItems);
        int size = tabs.size();
        int i2 = currentIndex;
        TabBar tab = (TabBar) tabs.elementAt(i2);
        int i3 = 0;
        int i4 = 0;
        int i5 = 20;
        for (int i6 = 0; i6 < size; i6++) {
            TabBar iterTab = (TabBar) tabs.elementAt(i6);
            iterTab.width = 26 + Storage.state().getGfxContext(UIKeys.GFX_INDEX_BOLD).stringWidth(iterTab.title);
            iterTab.xOffset = i5;
            layoutItems.addElement(iterTab);
            i5 += iterTab.width;
            i3 += iterTab.width;
            if (i6 == i2) {
                i4 = i3;
            }
        }
        int i7 = i3;
        int availWidth = Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH) - 20;
        while (i4 >= availWidth - 32) {
            int i8 = 0;
            while (true) {
                leftElem = tabs.elementAt(i8);
                if (layoutItems.contains(leftElem)) {
                    break;
                } else {
                    i8++;
                }
            }
            TabBar leftTab = (TabBar) leftElem;
            if (leftTab == tab) {
                break;
            }
            int i9 = leftTab.width;
            layoutItems.removeElement(leftTab);
            Object firstElem = layoutItems.firstElement();
            if (firstElem instanceof int[]) {
                iArr2 = (int[]) firstElem;
            } else {
                int[] iArr3 = {20, 248};
                iArr2 = iArr3;
                layoutItems.insertElementAt(iArr3, 0);
                i9 -= 16;
            }
            if (leftTab.type == 36 && AccountManager.hasActiveConnection()) {
                if (iArr2[1] == 248) {
                    layoutItems.insertElementAt(new int[]{20, 16385}, 0);
                    int[] iArr4 = iArr2;
                    iArr4[0] = iArr4[0] + 16;
                } else {
                    layoutItems.insertElementAt(new int[]{36, 16385}, 1);
                    int[] iArr5 = (int[]) layoutItems.elementAt(2);
                    iArr5[0] = iArr5[0] + 16;
                }
                i9 -= 16;
            }
            if (leftTab.type == 4) {
                Account acct = leftTab.account;
                if (AccountManager.isAccountOnline(acct) && Storage.state().getBool(SettingsKeys.SETTING_MAIL_TAB_ENABLED) && iArr2[1] == 248) {
                    layoutItems.insertElementAt(new int[]{20, AccountManager.getAccountStatus(acct)}, 0);
                    int[] iArr6 = iArr2;
                    iArr6[0] = iArr6[0] + 16;
                    i9 -= 16;
                }
            }
            for (int i10 = 0; i10 < size; i10++) {
                ((TabBar) tabs.elementAt(i10)).xOffset -= i9;
            }
            i4 -= i9;
            i7 -= i9;
        }
        while (true) {
            if (i7 < availWidth) {
                break;
            }
            int i11 = size - 1;
            while (true) {
                elem = tabs.elementAt(i11);
                if (layoutItems.contains(elem)) {
                    break;
                } else {
                    i11--;
                }
            }
            TabBar rightTab = (TabBar) elem;
            if (rightTab == tab) {
                break;
            }
            int i12 = rightTab == tabs.lastElement() ? 16 : 0;
            if (rightTab.width > (i7 - availWidth) + i12) {
                int i13 = (i7 - availWidth) + i12;
                rightTab.width -= i13;
                int size2 = layoutItems.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        break;
                    }
                    Object shrinkElem = layoutItems.elementAt(size2);
                    if (!(shrinkElem instanceof int[])) {
                        break;
                    }
                    int[] iArr7 = (int[]) shrinkElem;
                    iArr7[0] = iArr7[0] - i13;
                }
            } else {
                int i14 = rightTab.width;
                int i15 = rightTab.xOffset;
                layoutItems.removeElement(rightTab);
                Object endElem = layoutItems.lastElement();
                if (endElem instanceof int[]) {
                    iArr = (int[]) endElem;
                    int size3 = layoutItems.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            break;
                        }
                        Object innerElem = layoutItems.elementAt(size3);
                        if (!(innerElem instanceof int[])) {
                            break;
                        }
                        int[] iArr8 = (int[]) innerElem;
                        iArr8[0] = iArr8[0] - i14;
                    }
                } else {
                    int[] iArr9 = {i15, 246};
                    iArr = iArr9;
                    layoutItems.addElement(iArr9);
                    i14 -= 16;
                }
                if (rightTab.type == 36 && AccountManager.hasActiveConnection() && Storage.state().getBool(SettingsKeys.SETTING_MAIL_TAB_ENABLED)) {
                    layoutItems.addElement(new int[]{i15 + 16, 16385});
                    i14 -= 16;
                }
                if (rightTab.type == 4) {
                    Account acct2 = rightTab.account;
                    if (AccountManager.isAccountOnline(acct2)) {
                        if (iArr[1] == 246) {
                            layoutItems.addElement(new int[]{i15 + 16, AccountManager.getAccountStatus(acct2)});
                            i14 -= 16;
                        } else {
                            int size4 = layoutItems.size() - 2;
                            int i16 = ((int[]) layoutItems.elementAt(size4))[1];
                            if (i16 != 16384 && i16 != 16386) {
                                layoutItems.insertElementAt(new int[]{i15 + 16, AccountManager.getAccountStatus(acct2)}, size4 + 1);
                                int[] iArr10 = iArr;
                                iArr10[0] = iArr10[0] + 16;
                                i14 -= 16;
                            }
                        }
                    }
                }
                i7 -= i14;
            }
        }
        Object lastElem = layoutItems.lastElement();
        if (lastElem instanceof TabBar) {
            TabBar lastTab = (TabBar) lastElem;
            i = (lastTab.xOffset + lastTab.width) - 20;
        } else {
            i = ((int[]) lastElem)[0] - 4;
        }
        if (i <= availWidth) {
            return;
        }
        int i17 = i - availWidth;
        tab.width -= i17;
        int size5 = layoutItems.size();
        while (true) {
            size5--;
            if (size5 < 0) {
                return;
            }
            Object tailElem = layoutItems.elementAt(size5);
            if (!(tailElem instanceof int[])) {
                return;
            }
            int[] iArr11 = (int[]) tailElem;
            iArr11[0] = iArr11[0] - i17;
        }
    }

    public static void paintTopBar(GraphicsContext g) {
        int paintMode;
        g.setFont(Storage.state().getGfxContext(UIKeys.GFX_INDEX_BOLD));
        TabBar tab = (TabBar) Storage.state().getVector(UIKeys.VEC_TAB_BARS).elementAt(TabBar.currentIndex);
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_ITEMS);
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
                int tabX = tab2.xOffset;
                int tabWidth = tab2.width;
                int textOffset = Storage.state().getIntOffset(UIKeys.OFFSET_BOLD_FONT_HEIGHT) + 7;
                gfx.setClip(tabX, 2, tabWidth, textOffset - 2).drawLine(tab2.xOffset, textOffset, tab2.xOffset, 6).drawLine(tab2.xOffset, 6, tab2.xOffset + 4, 2).drawLine(tab2.xOffset + 4, 2, (tab2.xOffset + tab2.width) - 2, 2).drawLine((tab2.xOffset + tab2.width) - 2, 2, (tab2.xOffset + tab2.width) - 2, textOffset).setColorFromPalette(isSelected ? 1 : 17);
                int fillBottom = isSelected ? textOffset : textOffset - 1;
                for (int row = 3; row < fillBottom; row++) {
                    g.drawLine(tab2.xOffset + 1 + (row < 6 ? 6 - row : 0), row, (tab2.xOffset + tab2.width) - 3, row);
                }
                if (tab2.account == null) {
                    int iconId = tab2.iconId;
                    paintMode = (iconId == 240 && AccountManager.hasActiveConnection()) ? 16385 : (iconId == 240 || iconId == 264 || Storage.state().getVector(UIKeys.VEC_ONLINE_CONTACTS).size() <= 0) ? iconId : 16384;
                } else {
                    paintMode = AccountManager.getAccountStatus(tab2.account);
                }
                g.drawIcon(paintMode, tab2.xOffset + 4, 4 + ScreenManager.getCenterOffset()).setColorFromPalette(0).setClip(tab2.xOffset, 2, tab2.width - 3, textOffset - 2).drawString(tab2.title, tab2.xOffset + 6 + 16, 4, 20);
            } else {
                int[] iconData = (int[]) objElementAt2;
                int iconX = iconData[0];
                int centerY2 = 4 + ScreenManager.getCenterOffset();
                g.setClip(iconX, centerY2, 16, 16);
                g.drawIcon(iconData[1], iconX, centerY2);
            }
        }
    }

    /* renamed from: a */
    public static final Object hitTest(int i, int i2) {
        Vector tabs = Storage.state().getVector(UIKeys.VEC_TAB_ITEMS);
        int size = tabs.size();
        while (true) {
            size--;
            if (size < 0) {
                return null;
            }
            Object elem = tabs.elementAt(size);
            if (elem instanceof int[]) {
                int[] iArr = (int[]) elem;
                if (i >= iArr[0] && i < iArr[0] + 16 && i2 >= 0 && i2 <= 22) {
                    return iArr;
                }
            } else {
                TabBar tab = (TabBar) elem;
                int i3 = tab.xOffset;
                if (i >= i3 && i2 >= 0 && i <= i3 + tab.width && i2 <= 22) {
                    return tab;
                }
            }
        }
    }
}
