package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.handler.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

/* renamed from: au */
/* loaded from: MobileAgent_3.9.jar:au.class */
public final class ScreenBuilder {
    /* renamed from: a */
    public static final void openScreen(int i) {
        Debug.assertState(i >= 0 && i <= 180, "screenId out of range: " + i);
        RemoteLogger.log("UI", "openScreen(" + i + ")");
        boolean z;
        int i2;
        TimerManager.resetBacklightTimer();
        AppController.needsRepaint = true;
        while (true) {
            if (!ScreenManager.hasScreen(i)) {
                Vector screenStack = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
                int size = screenStack.size();
                z = false;
                while (--size >= 0) {
                    i2 = ((ListView) screenStack.elementAt(size)).screenType;
                    if (i2 == 7 || i2 == 8) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    break;
                }
            }
            onScreenClosed();
        }
        ScreenHandler handler = ScreenHandlerRegistry.getHandler(i);
        if (handler != null) {
            handler.buildScreen(i);
            AppController.clearInitParamsAndReport();
            return;
        }
        switch (i) {
            case ScreenId.NONE:
                return;
            case ScreenId.CLOSE:
                return;
            case ScreenId.UNUSED_18:
                return;
            case ScreenId.UNUSED_23:
                return;
            case ScreenId.UNUSED_24:
                return;
            case ScreenId.UNUSED_31:
            default:
                return;
            case ScreenId.UNUSED_32:
                return;
            case ScreenId.UNUSED_45:
                return;
            case ScreenId.UNUSED_46:
                return;
            case ScreenId.UNUSED_74:
                return;
            case ScreenId.UNUSED_75:
                return;
            case ScreenId.UNUSED_133:
                return;
            case ScreenId.UNUSED_134:
                return;
            case ScreenId.UNUSED_135:
                return;
            case ScreenId.UNUSED_136:
                return;
            case ScreenId.UNUSED_138:
                return;
            case ScreenId.UNUSED_139:
                return;
            case ScreenId.UNUSED_141:
                return;
            case ScreenId.UNUSED_148:
                return;
            case ScreenId.UNUSED_149:
                return;
        }
    }

    /* renamed from: a */
    public static final void onMenuItemSelected() {
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        ListView currentScreen = ScreenManager.getCurrentScreen();
        String title = ScreenManager.getCurrentTitle();
        int action = ScreenManager.getCurrentWidth();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object obj = menuItem == null ? null : menuItem.data;
        int nextScreen = 0;
        ScreenHandler handler = ScreenHandlerRegistry.getHandler(ScreenManager.getCurrentScreen().screenId);
        if (handler != null) {
            nextScreen = handler.onMenuItemSelected(currentScreen, menuItem, title, action, obj);
        } else switch (ScreenManager.getCurrentScreen().screenId) {
            case ScreenId.UNUSED_18:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_23:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_24:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_32:
                nextScreen = handleDropdownSelect(title, menuItem);
                break;
            case ScreenId.UNUSED_45:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_46:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_74:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_75:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_133:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_134:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_135:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_136:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_138:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_139:
                nextScreen = ScreenId.MMP_ACCOUNT_SELECT;
                break;
            case ScreenId.UNUSED_141:
                nextScreen = -1;
                break;
            case ScreenId.UNUSED_148:
                nextScreen = 0;
                break;
            case ScreenId.UNUSED_149:
                nextScreen = 0;
                break;
        }
        RemoteLogger.log("UI", "onMenuItemSelected screenId=" + ScreenManager.getCurrentScreen().screenId + " next=" + nextScreen + " skL=" + currentScreen.softKeyLeft);
        if (nextScreen != -1) {
            if (nextScreen == 12) {
                onScreenClosed();
                return;
            }
            if (nextScreen != 0) {
                openScreen(nextScreen);
                return;
            }
            int i8 = currentScreen.softKeyLeft;
            if (i8 != 200) {
                int i9 = i8 == 199 ? action : i8;
                int i10 = i9;
                RemoteLogger.log("UI", "softKeyLeft action=" + i10);
                if (i9 == 12) {
                    onScreenClosed();
                } else if (i10 != 0) {
                    openScreen(i10);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: b */
    public static final void onMenuItemAction() {
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        ListView currentScreen = ScreenManager.getCurrentScreen();
        int i = ScreenManager.getCurrentScreen().screenId;
        ScreenManager.getCurrentTitle();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object obj = menuItem == null ? null : menuItem.data;
        int result = 0;
        ScreenHandler handler = ScreenHandlerRegistry.getHandler(i);
        if (handler != null) {
            result = handler.onMenuItemAction(currentScreen, menuItem, obj);
        }
        RemoteLogger.log("UI", "onMenuItemAction screenId=" + ScreenManager.getCurrentScreen().screenId + " result=" + result + " skC=" + currentScreen.softKeyCenter);
        if (result != -1) {
            if (result == 12) {
                onScreenClosed();
                return;
            }
            if (result != 0) {
                openScreen(result);
                return;
            }
            int i2 = currentScreen.softKeyCenter;
            if (i2 != 200) {
                if (i2 == 12) {
                    onScreenClosed();
                } else if (i2 != 0) {
                    openScreen(i2);
                }
            }
        }
    }

    /* renamed from: a */
    public static final int handleDropdownSelect(String str, MenuItem menuItem) {
        Object[] objArr = (Object[]) menuItem.data;
        Object[] objArr2 = (Object[]) objArr[0];
        MenuItem targetItem = (MenuItem) objArr[1];
        ListView parentScreen = (ListView) objArr[2];
        String[] strArr = (String[]) objArr2[1];
        int i = 0;
        int length = strArr.length;
        while (true) {
            length--;
            if (length < 0) {
                targetItem.clear().setLabel(Utils.appendSpace(targetItem.title)).addText(strArr[i], 1, 7).setIcon(247).data = new Object[]{ObjectPool.integerOf(i), strArr};
                parentScreen.rebuildItems();
                EventDispatcher.postEvent(new MenuItemEvent(targetItem));
                return 0;
            }
            if (str == strArr[length]) {
                i = length;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: c */
    public static final void onScreenClosed() {
        AppController.needsRepaint = true;
        ScreenHandler handler = ScreenHandlerRegistry.getHandler(ScreenManager.getCurrentScreen().screenId);
        if (handler != null) {
            handler.onScreenClosed(ScreenManager.getCurrentScreen());
        } else switch (ScreenManager.getCurrentScreen().screenId) {
            case ScreenId.UNUSED_138:
                ContactListManager.refreshContactList();
                break;
        }
        Storage.state().setInt(UIKeys.INT_SCREEN_ACTION, 0);
        Vector screenStack = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
        int size = screenStack.size() - 1;
        ListView closedScreen = (ListView) screenStack.elementAt(size);
        int closedScreenId = closedScreen.screenId;
        ObjectPool.releaseVector(closedScreen.tabItems);
        ObjectPool.releaseVector(closedScreen.menuItems);
        screenStack.removeElementAt(size);
        Utils.trimIfEmpty(screenStack);
        if (screenStack.size() > 0) {
            ListView resumedScreen = (ListView) screenStack.lastElement();
            ScreenHandler resumedHandler = ScreenHandlerRegistry.getHandler(resumedScreen.screenId);
            if (resumedHandler != null) {
                resumedHandler.onScreenResumed(resumedScreen, closedScreenId);
            }
        }
    }
}
