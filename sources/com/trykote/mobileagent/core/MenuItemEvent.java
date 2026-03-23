package com.trykote.mobileagent.core;

import com.trykote.mobileagent.ui.MenuItem;

public final class MenuItemEvent extends Event {
    public final MenuItem item;

    public MenuItemEvent(MenuItem item) {
        this.item = item;
    }
}
