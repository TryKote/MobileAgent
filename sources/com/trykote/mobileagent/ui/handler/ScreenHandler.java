package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.ListView;

public interface ScreenHandler {

    void buildScreen(int screenId);

    int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data);

    int onMenuItemAction(ListView screen, MenuItem item, Object data);

    void onScreenClosed(ListView screen);

    int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                       Object data, Object headerData);

    int onIdleProcess(ListView screen, MenuItem item, Object data, String title);
}
