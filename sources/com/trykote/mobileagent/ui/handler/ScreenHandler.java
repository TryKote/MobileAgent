package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.Screen;

public interface ScreenHandler {

    void buildScreen(int screenId);

    int onMenuItemSelected(Screen screen, MenuItem item, String title, int action, Object data);

    int onMenuItemAction(Screen screen, MenuItem item, Object data);

    void onScreenClosed(Screen screen);

    int onItemSelected(Screen screen, MenuItem item, String title, int selectedOption,
                       Object data, Object headerData);

    int onIdleProcess(Screen screen, MenuItem item, Object data, String title);
}
