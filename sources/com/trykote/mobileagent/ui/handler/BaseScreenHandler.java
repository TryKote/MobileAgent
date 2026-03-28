package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.ListView;

public abstract class BaseScreenHandler implements ScreenHandler {

    public void buildScreen(int screenId) {
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        return 0;
    }

    public int onMenuItemAction(ListView screen, MenuItem item, Object data) {
        return 0;
    }

    public void onScreenClosed(ListView screen) {
    }

    public void onScreenResumed(ListView screen, int closedScreenId) {
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        return 0;
    }

    public void onMenuItemEvent(ListView screen, MenuItem item) {
    }
}
