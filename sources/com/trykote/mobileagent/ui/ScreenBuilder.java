package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.Debug;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.core.event.MenuItemEvent;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;

import java.util.Vector;

public final class ScreenBuilder {

    private static final int MAX_SCREEN_ID = ScreenId.ASYNC_TASK;
    private static final int SOFT_KEY_DEFAULT_ACTION = 199;
    private static final int SOFT_KEY_NO_ACTION = 200;
    private static final int RESULT_VETO = -1;

    public static void openScreen(int screenId) {
        Debug.assertState(screenId >= 0 && screenId <= MAX_SCREEN_ID, "screenId out of range: " + screenId);
        RemoteLogger.debug("UI", "openScreen(" + screenId + ")");
        TimerManager.resetBacklightTimer();
        AppController.needsRepaint = true;
        while (ScreenManager.hasScreen(screenId) || hasToastInStack()) {
            onScreenClosed();
        }
        ScreenView view = ScreenView.create(screenId);
        if (view != null) {
            ListView topBefore = ScreenManager.getCurrentScreen();
            view.buildContent();
            if (ScreenManager.getCurrentScreen() == topBefore) {
                view.showSelf();
            }
            AppController.clearInitParamsAndReport();
        }
    }

    public static void onMenuItemSelected() {
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        ListView currentScreen = ScreenManager.getCurrentScreen();
        String title = ScreenManager.getCurrentTitle();
        int action = ScreenManager.getCurrentWidth();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object itemData = menuItem == null ? null : menuItem.data;
        int nextScreen = dispatchSelect(currentScreen, menuItem, title, action, itemData);
        RemoteLogger.debug("UI", "onMenuItemSelected screenId=" + currentScreen.screenId + " next=" + nextScreen + " skL=" + currentScreen.softKeyLeft);
        if (nextScreen == RESULT_VETO) {
            return;
        }
        if (nextScreen == ScreenId.CLOSE) {
            onScreenClosed();
            return;
        }
        if (nextScreen != ScreenId.NONE) {
            openScreen(nextScreen);
            return;
        }
        processSoftKeyLeft(currentScreen, action);
    }

    public static void onMenuItemAction() {
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        ListView currentScreen = ScreenManager.getCurrentScreen();
        int screenId = currentScreen.screenId;
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object itemData = menuItem == null ? null : menuItem.data;
        int result = 0;
        ScreenView view = resolveView(currentScreen);
        if (view != null) {
            result = view.onAction(menuItem, itemData);
        }
        RemoteLogger.debug("UI", "onMenuItemAction screenId=" + screenId + " result=" + result + " skC=" + currentScreen.softKeyCenter);
        if (result == RESULT_VETO) {
            return;
        }
        if (result == ScreenId.CLOSE) {
            onScreenClosed();
            return;
        }
        if (result != ScreenId.NONE) {
            openScreen(result);
            return;
        }
        int softKeyAction = currentScreen.softKeyCenter;
        if (softKeyAction != SOFT_KEY_NO_ACTION) {
            if (softKeyAction == ScreenId.CLOSE) {
                onScreenClosed();
            } else if (softKeyAction != ScreenId.NONE) {
                openScreen(softKeyAction);
            }
        }
    }

    public static int handleDropdownSelect(String selectedTitle, MenuItem menuItem) {
        Object[] dropdownData = (Object[]) menuItem.data;
        Object[] optionsData = (Object[]) dropdownData[0];
        MenuItem targetItem = (MenuItem) dropdownData[1];
        ListView parentScreen = (ListView) dropdownData[2];
        String[] options = (String[]) optionsData[1];
        int selectedIndex = 0;
        for (int idx = options.length - 1; idx >= 0; idx--) {
            if (selectedTitle == options[idx]) {
                selectedIndex = idx;
            }
        }
        targetItem.clear().setLabel(Utils.appendSpace(targetItem.title)).addText(options[selectedIndex], 1, 7).setIcon(MenuItem.ICON_DROPDOWN).data = new Object[]{ObjectPool.integerOf(selectedIndex), options};
        parentScreen.rebuildItems();
        EventDispatcher.postEvent(new MenuItemEvent(targetItem));
        return 0;
    }

    public static void onScreenClosed() {
        AppController.needsRepaint = true;
        ListView closingScreen = ScreenManager.getCurrentScreen();
        ScreenView closingView = resolveView(closingScreen);
        if (closingView != null) {
            closingView.onClosed();
        }
        ScreenView.clearActive(closingScreen);
        UIState.setScreenAction(0);
        Vector screenStack = UIState.getScreenStack();
        int topIndex = screenStack.size() - 1;
        ListView closedScreen = (ListView) screenStack.elementAt(topIndex);
        int closedScreenId = closedScreen.screenId;
        ObjectPool.releaseVector(closedScreen.tabItems);
        ObjectPool.releaseVector(closedScreen.menuItems);
        screenStack.removeElementAt(topIndex);
        Utils.trimIfEmpty(screenStack);
        if (screenStack.size() > 0) {
            ListView resumedScreen = (ListView) screenStack.lastElement();
            ScreenView resumedView = resolveView(resumedScreen);
            if (resumedView != null) {
                resumedView.onResumed(closedScreenId);
            }
        }
    }

    // --- Private helpers ---

    private static boolean hasToastInStack() {
        Vector screenStack = UIState.getScreenStack();
        for (int idx = screenStack.size() - 1; idx >= 0; idx--) {
            int type = ((ListView) screenStack.elementAt(idx)).screenType;
            if (type == ScreenManager.TYPE_TOAST || type == ScreenManager.TYPE_TOAST_CENTER) {
                return true;
            }
        }
        return false;
    }

    public static ScreenView resolveView(ListView screen) {
        if (screen instanceof ScreenView) {
            return (ScreenView) screen;
        }
        return ScreenView.getActive(screen.screenId);
    }

    private static int dispatchSelect(ListView currentScreen, MenuItem menuItem,
                                      String title, int action, Object itemData) {
        ScreenView view = resolveView(currentScreen);
        if (view != null) {
            return view.onItemSelected(menuItem, title, action, itemData);
        }
        if (currentScreen.screenId == ScreenId.UNUSED_32) {
            return handleDropdownSelect(title, menuItem);
        }
        return 0;
    }

    private static void processSoftKeyLeft(ListView screen, int action) {
        int softKeyAction = screen.softKeyLeft;
        if (softKeyAction == SOFT_KEY_NO_ACTION) {
            return;
        }
        int resolvedAction = softKeyAction == SOFT_KEY_DEFAULT_ACTION ? action : softKeyAction;
        RemoteLogger.debug("UI", "softKeyLeft action=" + resolvedAction);
        if (resolvedAction == ScreenId.CLOSE) {
            onScreenClosed();
        } else if (resolvedAction != ScreenId.NONE) {
            openScreen(resolvedAction);
        }
    }
}
