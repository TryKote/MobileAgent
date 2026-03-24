package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.Screen;

public abstract class BaseScreenHandler implements ScreenHandler {

    public void buildScreen(int screenId) {
    }

    public int onMenuItemSelected(Screen screen, MenuItem item, String title, int action, Object data) {
        return 0;
    }

    public int onMenuItemAction(Screen screen, MenuItem item, Object data) {
        return 0;
    }

    public void onScreenClosed(Screen screen) {
    }

    public int onItemSelected(Screen screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        return 0;
    }

    public int onIdleProcess(Screen screen, MenuItem item, Object data, String title) {
        return 0;
    }
}
