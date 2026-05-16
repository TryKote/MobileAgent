package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import com.trykote.mobileagent.core.AppState;

/**
 * About screen: displays app version, memory info, device details.
 */
public final class AboutScreen extends ScreenView {

    private static final long ABOUT_SCREEN_KEY = 7234309766870429269L;

    public AboutScreen() {
        super(ScreenManager.TYPE_FULLSCREEN_NOSCROLL, ScreenId.ABOUT);
    }

    public void buildContent() {
        UIState.setShowNotification(1);
        UIState.setScreenTitle((Object) StringUtils.intern(
                Long.toString(Runtime.getRuntime().totalMemory())));
        UIState.setScreenSubtitle((Object) Utils.getFreeMemoryString());
        UIState.setAppVersionStringFromBuffer(ObjectPool.newStringBuffer()
                .append(AppState.getString(StringResKeys.STR_APP_NAME))
                .append(StringPool.get(StringResKeys.STR_APP_BUILD_SUFFIX)));
        RegistrationState.setDeviceId(new ByteBuffer()
                .writeLongBytes(ABOUT_SCREEN_KEY).writeByte(',')
                .writeRawString(AppState.getAppProperty(StringResKeys.STR_APP_PROPERTY_NAME))
                .getStringAndClear());

        configureHeader(9, 510);
        addSeparator(512, 1288);
        addSeparator(853022, 1180715);
        addSeparator(513, 1287);
        addSeparator(514, 1377341);
        addSeparator(515, 1376);
        addSeparator(516, 1377);
        addSeparator(517, 1284);
        addSeparator(518, 1285);
        configureSoftKeys(507, 57, 1050, 12, 0);
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        return 0;
    }

    public void onClosed() {
        UIState.clearScreenProperties();
    }
}
