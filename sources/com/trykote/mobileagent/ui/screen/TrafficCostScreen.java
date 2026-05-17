package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.net.TrafficAccounting;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.Utils;

/**
 * Traffic cost configuration screen: set price per KB.
 */
public final class TrafficCostScreen extends ScreenView {

    public TrafficCostScreen() {
        super(ScreenManager.TYPE_FULLSCREEN, ScreenId.TRAFFIC_COST);
    }

    public void buildContent() {
        StringBuffer sb = ObjectPool.newStringBuffer();
        int intVal = SettingsState.getTrafficCost();
        UIState.setScreenValueFromBuffer(
                sb.append(intVal / 100).append('.').append(Utils.zeroPad(intVal % 100)));

        configureHeader(29, AppState.getString(541));
        addTextInput(AppState.getString(626), 10, AppState.getString(1262), 0, 1286);
        addNumericInput(AppState.getString(627), 10, AppState.getString(1262), 114, 1, 1024, 1024);
        addTextInput(AppState.getString(628), 10, AppState.getString(424), 0, 117);
        configureSoftKeys(AppState.getString(1053), 12, AppState.getString(1050), 12, 0);
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        return TrafficAccounting.parseBalance();
    }

    public void onClosed() {
        UIState.clearScreenValue();
    }
}
