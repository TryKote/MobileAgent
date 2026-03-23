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
import javax.microedition.lcdui.Image;

/* renamed from: ad */
/* loaded from: MobileAgent_3.9.jar:ad.class */
public abstract class ScreenManager {
    /* renamed from: a */
    public static final void initializeFonts() {
        int iM586d = AppState.getInt(73);
        int i = iM586d == 0 ? 8 : iM586d == 1 ? 0 : 16;
        GraphicsContext normalGfx = new GraphicsContext(0, i);
        AppState.pool[1273] = normalGfx;
        GraphicsContext boldGfx = new GraphicsContext(1, i);
        AppState.pool[1274] = boldGfx;
        GraphicsContext titleGfx = AppState.getBool(70) ? new GraphicsContext(2, i) : normalGfx;
        AppState.pool[1275] = titleGfx;
        AppState.pool[1276] = normalGfx;
        AppState.pool[1277] = normalGfx;
        AppState.pool[1278] = boldGfx;
        AppState.setInt(1450, normalGfx.font.getHeight());
        AppState.setInt(1453, normalGfx.font.getHeight());
        AppState.setInt(1454, normalGfx.font.getHeight());
        AppState.setInt(1455, boldGfx.font.getHeight());
        AppState.setInt(1451, boldGfx.font.getHeight());
        AppState.setInt(1452, titleGfx.font.getHeight());
        Vector screens = AppState.getVector(1272);
        int size = screens.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((Screen) screens.elementAt(size)).rebuildItems();
            }
        }
    }

    /* renamed from: b */
    public static final Screen getCurrentScreen() {
        Vector screens = AppState.getVector(1272);
        if (screens.isEmpty()) {
            return null;
        }
        return (Screen) screens.lastElement();
    }

    /* renamed from: c */
    public static final String getCurrentTitle() {
        if (AppState.getVector(1272).size() > 0) {
            return getCurrentScreen().getSelectedTitle();
        }
        return null;
    }

    /* renamed from: d */
    public static final int getCurrentWidth() {
        if (AppState.getVector(1272).size() > 0) {
            return getCurrentScreen().getSelectedWidth();
        }
        return 200;
    }

    /* renamed from: e */
    public static final MenuItem getCurrentMenuItem() {
        if (AppState.getVector(1272).size() > 0) {
            return getCurrentScreen().getSelectedItem();
        }
        return null;
    }

    /* renamed from: a */
    public static final void pushScreen(Screen screen) {
        Vector screens = AppState.getVector(1272);
        while (screens.size() > 0) {
            ScreenBuilder.onScreenClosed();
        }
        screens.addElement(screen);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0166  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void showScreen(Screen screen) {
        RemoteLogger.log("SCR", "showScreen id=" + (screen != null ? screen.screenId : -1));
        Screen prevScreen = null;
        Vector screens = AppState.getVector(1272);
        int size = screens.size() - 1;
        int i = size >= 0 ? ((Screen) screens.elementAt(size)).screenId : -1;
        if (i == 137 || i == 63) {
            Screen savedScreen = (Screen) screens.elementAt(size);
            prevScreen = savedScreen;
            screens.removeElement(savedScreen);
        }
        int i2 = screen.screenId;
        if (i2 != 112) {
            int size2 = screens.size();
            for (int i3 = 0; i3 < size2; i3++) {
                if (((Screen) screens.elementAt(i3)).screenId == i2) {
                    size2 = i3;
                }
            }
            while (screens.size() > size2) {
                ScreenBuilder.onScreenClosed();
            }
        }
        int i4 = screen.screenType;
        if (((1 << i4) & 3484) != 0) {
            screen.buildLayout();
        }
        int i5 = screen.containerWidth;
        int i6 = screen.containerHeight;
        int iM586d = AppState.getInt(1528) - i5;
        int screenH = AppState.getHeight() - i6;
        switch (i4) {
            case 2:
            case 7:
            case 8:
                screen.setOffset(iM586d >> 1, screenH >> 1);
                break;
            case 3:
                screen.setOffset(0, screenH);
                break;
            case 4:
                screen.setOffset(iM586d, screenH);
                break;
            case 10:
                Screen curScreen = getCurrentScreen();
                if (curScreen != null) {
                    int iM586d2 = curScreen.offsetX + curScreen.containerWidth;
                    int selectedY = curScreen.getSelectedY();
                    if (iM586d2 + screen.containerWidth > AppState.getInt(1528)) {
                        iM586d2 = AppState.getInt(1528) - screen.containerWidth;
                    }
                    if (selectedY + screen.containerHeight > AppState.getHeight()) {
                        selectedY = AppState.getHeight() - screen.containerHeight;
                    }
                    screen.setOffset(iM586d2, selectedY);
                    break;
                }
                break;
            case 11:
                screen.setOffset(iM586d >> 1, (AppState.getHeight() - i6) - (i6 / 10));
                break;
        }
        int size3 = screens.size();
        for (int i7 = 0; i7 < size3; i7++) {
            if (((Screen) screens.elementAt(i7)).screenType == 7) {
                size3 = i7;
            }
        }
        while (screens.size() > size3) {
            ScreenBuilder.onScreenClosed();
        }
        screen.invalidateLayout();
        screens.addElement(screen);
        if (prevScreen != null) {
            screens.addElement(prevScreen);
        }
    }

    /* renamed from: a */
    public static final boolean hasScreen(int i) {
        Vector screens = AppState.getVector(1272);
        int size = screens.size();
        do {
            size--;
            if (size < 0) {
                return false;
            }
        } while (((Screen) screens.elementAt(size)).screenId != i);
        return true;
    }

    /* renamed from: f */
    public static final int getCenterOffset() {
        return Utils.max(0, (AppState.getInt(1450) - 16) >> 1);
    }

    /* renamed from: g */
    public static final int handleScreenClose() {
        if (!AppState.getBool(1543)) {
            return NotificationHelper.showError(470);
        }
        AppState.setScreen(new Object());
        return 0;
    }

    /* renamed from: b */
    public static final Screen createScreen(int i) {
        Screen screen;
        int i2 = i + 1;
        String title = Utils.defaultStr(AppState.getString(AppState.getInt(i)));
        int i3 = i2 + 1;
        int iM586d = AppState.getInt(i2);
        int i4 = i3 + 1;
        int iM586d2 = AppState.getInt(i3);
        boolean z = (iM586d2 & 16) != 0;
        int i5 = iM586d2 & 15;
        int i6 = i4 + 1;
        int iM586d3 = AppState.getInt(i4);
        int i7 = i6 + 1;
        int iM586d4 = AppState.getInt(i6);
        int i8 = i7 + 1;
        int iM586d5 = AppState.getInt(i7);
        int i9 = i8 + 1;
        int iM586d6 = AppState.getInt(i8);
        int i10 = i9 + 1;
        int iM586d7 = AppState.getInt(i9);
        int i11 = i10 + 1;
        int iM586d8 = AppState.getInt(i10);
        int pos = i11 + 1;
        int itemCount = AppState.getInt(i11);
        RemoteLogger.log("SCR", "createScreen(" + i + "): type=" + i5 + " items=" + itemCount + " id=" + iM586d);
        int screenW = AppState.getInt(1528);
        int screenH = AppState.getHeight();
        switch (i5) {
            case 0:
            case 1:
                screen = new Screen(0, iM586d, screenW, screenH, true);
                break;
            case 2:
            case 3:
            case 4:
            case 10:
            case 11:
                screen = new Screen(0, iM586d, (screenW * 9) / 10, (screenH * 9) / 10, true);
                break;
            case 5:
            case 9:
                screen = new Screen(0, iM586d, screenW, screenH, false);
                break;
            case 6:
            case 12:
                screen = new Screen(1, iM586d, screenW, screenH, true);
                break;
            case 7:
            case 8:
                screen = new Screen(0, iM586d, (screenW * 9) / 10, (screenH * 9) / 10, false);
                break;
            default:
                screen = null;
                break;
        }
        RemoteLogger.log("SCR", "Screen created OK");
        if (i5 != 3 && i5 != 4 && i5 != 2 && i5 != 11 && i5 != 10 && i5 != 8) {
            if (i5 != 7) {
                RemoteLogger.log("SCR", "setHeader(" + iM586d3 + ", " + title + ")");
                screen.setHeader(iM586d3, title);
                RemoteLogger.log("SCR", "setHeader done");
            } else {
                screen.addItem(new MenuItem(0, title).addText(title, 1, 0));
            }
        }
        screen.showCheckboxes = z;
        screen.screenFlags = i;
        RemoteLogger.log("SCR", "items loop start");
        for (int i12 = 0; i12 < itemCount; i12++) {
            pos = parseScreenItem(screen, pos, iM586d);
        }
        Screen configuredScreen = screen.setSoftKeys(iM586d4 > 0 ? AppState.getString(iM586d4) : null, iM586d5 > 0 ? AppState.getString(iM586d5) : null, iM586d6, iM586d7, iM586d8);
        configuredScreen.screenType = i5;
        RemoteLogger.log("SCR", "createScreen(" + i + ") done: items=" + screen.menuItems.size() + " skL=" + iM586d6 + " skR=" + iM586d8);
        return configuredScreen;
    }

    /* renamed from: c */
    public static final Screen createDialogScreen(int i) {
        Screen screen = new Screen(0, i, (AppState.getInt(1528) * 9) / 10, (AppState.getHeight() * 9) / 10, true);
        screen.screenType = 2;
        screen.showCheckboxes = true;
        return screen;
    }

    /* renamed from: h */
    public static final boolean hasModal() {
        Vector screens = AppState.getVector(1272);
        int size = screens.size();
        do {
            size--;
            if (size <= 0) {
                return false;
            }
        } while (((Screen) screens.elementAt(size)).offsetY != 0);
        return true;
    }

    /* renamed from: a */
    private static final int addItemToScreen(boolean z, Screen screen, int i, boolean z2) {
        int i2 = i + 1;
        int iM586d = AppState.getInt(i);
        int i3 = i2 + 1;
        int iM586d2 = AppState.getInt(i2);
        int i4 = i3 + 1;
        int iM586d3 = AppState.getInt(i3);
        if (z) {
            if (z2) {
                screen.addActionById(iM586d2, iM586d3, iM586d);
            } else {
                screen.addIconById(iM586d2, iM586d3, iM586d);
            }
        }
        return i4;
    }

    /* renamed from: a */
    private static final int parseScreenItem(Screen screen, int i, int i2) {
        int i3;
        Object itemData;
        int i4;
        String title;
        int i5 = i + 1;
        int iM586d = AppState.getInt(i);
        RemoteLogger.log("SCR", "parseItem pos=" + i + " type=" + (iM586d & 15));
        boolean z = (iM586d & 16) != 0;
        boolean z2 = (iM586d & 32) != 0;
        switch (iM586d & 15) {
            case 0:
                boolean isEnabled = z;
                int i6 = i5;
                if (z2) {
                    i6++;
                    isEnabled = AppState.getBool(AppState.getInt(i6));
                }
                int i7 = i6;
                int i8 = i6 + 1;
                int iM586d2 = AppState.getInt(i7);
                int i9 = i8 + 1;
                int iM586d3 = AppState.getInt(i8);
                int i10 = i9 + 1;
                int iM586d4 = AppState.getInt(i9);
                if (isEnabled) {
                    screen.addActionById(iM586d3, iM586d4, iM586d2);
                } else {
                    screen.addIconById(iM586d3, iM586d4, iM586d2);
                }
                return i10;
            case 1:
                int i11 = i5 + 1;
                MenuItem separator = MenuItem.createSeparator().addText(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(Utils.defaultStr(AppState.getString(AppState.getInt(i5)))).append(' ')), 0, 0);
                int i12 = i11 + 1;
                String sublabel = Utils.defaultStr(AppState.getString(AppState.getInt(i11)));
                if (!StringUtils.isEmpty(sublabel)) {
                    separator.addText(sublabel, 0, 6);
                }
                screen.addItem(separator);
                return i12;
            case 2:
                int i13 = i5 + 1;
                String checkLabel = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                int i14 = i13 + 1;
                screen.addItem(MenuItem.createCheckbox(checkLabel, AppState.getBool(AppState.getInt(i13))));
                return i14;
            case 3:
                int i15 = i5 + 1;
                String choiceLabel = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                int i16 = i15 + 1;
                Vector choices = Utils.splitByNull(Utils.defaultStr(AppState.getString(AppState.getInt(i15))));
                int i17 = i16 + 1;
                screen.addItem(new MenuItem(9, choiceLabel).setChoices(choices, AppState.getInt(AppState.getInt(i16)), choiceLabel));
                return i17;
            case 4:
                int i18 = i5 + 1;
                screen.addItem(MenuItem.createSeparator().addText(Utils.defaultStr(AppState.getString(AppState.getInt(i5))), 1, 0));
                return i18;
            case 5:
                if (i2 == 49) {
                    i3 = i5 + 1;
                    int iM586d5 = AppState.getInt(i5);
                    itemData = (iM586d5 < 268 || iM586d5 > 304) ? (iM586d5 < 161 || iM586d5 > 210) ? AppState.getString(iM586d5) : ResourceManager.integerOf(iM586d5) : ResourceManager.integerOf(iM586d5);
                } else {
                    i3 = i5 + 1;
                    itemData = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                }
                Object obj = itemData;
                int i19 = i3;
                int i20 = i3 + 1;
                int iM586d6 = AppState.getInt(i19);
                int i21 = i20 + 1;
                String hintText = Utils.defaultStr(AppState.getString(AppState.getInt(i20)));
                int i22 = i21 + 1;
                int iM586d7 = AppState.getInt(i21);
                if (iM586d7 == 2) {
                    int i23 = i22 + 3;
                    i4 = i23 + 1;
                    int iM586d8 = AppState.getInt(AppState.getInt(i23));
                    title = iM586d8 >= 0 ? StringUtils.intern(Integer.toString(iM586d8)) : AppState.emptyStr;
                } else {
                    i4 = i22 + 1;
                    title = Utils.defaultStr(Utils.defaultStr(AppState.getString(AppState.getInt(i22))));
                }
                screen.addItem(new MenuItem(15, obj instanceof String ? (String) obj : AppState.emptyStr).setAction(obj, title, ResourceManager.integerOf(iM586d6), ResourceManager.integerOf(iM586d7), hintText));
                return i4;
            case 6:
                int i24 = i5 + 1;
                screen.addItem(MenuItem.createSeparator().setLabel(Utils.defaultStr(AppState.getString(AppState.getInt(i5)))));
                return i24;
            case 7:
                return addItemToScreen(AppState.getBool(AppState.getInt(i5)), screen, i5 + 1, z);
            case 8:
                return addItemToScreen(!AppState.getBool(AppState.getInt(i5)), screen, i5 + 1, z);
            case 9:
                String loginLabel = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                String loginValue = Utils.defaultStr(AppState.getString(AppState.getInt(i5 + 1)));
                MenuItem loginItem = new MenuItem(4, (String) null).clear().setIcon(221).addText(Utils.nonEmpty(loginValue) ? loginValue : loginLabel, 1, 7);
                loginItem.data = new String[]{loginLabel, loginValue};
                screen.addItem(loginItem);
                return i5 + 2;
            case 10:
                String passwordStr = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                MenuItem menuItem = new MenuItem(5, (String) null);
                menuItem.clear();
                menuItem.setIcon(219);
                if (Utils.nonEmpty(passwordStr)) {
                    int idx = passwordStr.indexOf(0);
                    menuItem.addText(idx < 0 ? passwordStr : StringUtils.prefix(passwordStr, idx), 1, 7);
                } else {
                    menuItem.setDefaultFont();
                }
                menuItem.data = passwordStr;
                screen.addItem(menuItem);
                return i5 + 1;
            case 11:
                screen.addItem(MenuItem.createGraphics(new GraphicsContext((Image) AppState.pool[AppState.getInt(i5)])));
                return i5 + 1;
            default:
                RemoteLogger.log("SCR", "parseItem DEFAULT type=" + (iM586d & 15) + " recurse to " + AppState.getInt(i5));
                parseScreenItem(screen, AppState.getInt(i5), i2);
                return i5 + 1;
        }
    }
}
