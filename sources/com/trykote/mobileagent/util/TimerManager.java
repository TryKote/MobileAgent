package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;

import javax.microedition.lcdui.Display;

public abstract class TimerManager {

    // Timer slot indices
    public static final int SLOT_BACKLIGHT = 0;
    public static final int SLOT_CONTACT_REFRESH = 1;
    public static final int SLOT_ANIMATION = 3;
    public static final int SLOT_REPAINT = 5;
    public static final int SLOT_SOUND = 6;
    public static final int SLOT_MAP_IDLE = 7;
    public static final int SLOT_NOTIFICATION_COOLDOWN = 8;
    public static final int SLOT_PHONE_INPUT_CHECK = 9;
    public static final int SLOT_MAP_CROSSHAIR = 10;
    public static final int SLOT_SCREEN_INIT = 13;
    public static final int SLOT_COUNT = 14;

    public static long[] timers;

    public static final void setTimer(int slot, long delayMs) {
        timers[slot] = System.currentTimeMillis() + delayMs;
    }

    public static boolean isTimerType(int slot) {
        return timers[slot] < System.currentTimeMillis();
    }

    public static final boolean isTimerExpired(long timestamp) {
        return timestamp != 0 && timestamp < System.currentTimeMillis();
    }

    public static final boolean checkTimer(int slot, long delayMs) {
        long[] timerArray = timers;
        long timerValue = timerArray[slot];
        long currentTime = System.currentTimeMillis();
        if (timerValue >= timerValue) {
            return false;
        }
        timerArray[slot] = currentTime + delayMs;
        return true;
    }

    public static final void disableBacklight() {
        flashBacklight(0);
    }

    public static final void resetBacklightTimer() {
        if (SessionState.hasMrimAccount()) {
            flashBacklight(Integer.MAX_VALUE);
            setTimer(SLOT_BACKLIGHT, getSessionTimestamp());
        }
    }

    public static final void enableBacklight() {
        flashBacklight(Integer.MAX_VALUE);
    }

    private static final void flashBacklight(int duration) {
        if (SessionState.hasSavedAccounts()) {
            try {
                Display.getDisplay(AppState.getMidlet()).flashBacklight(duration);
            } catch (Throwable unused) {
            }
        }
    }

    public static final long getSessionTimestamp() {
        switch (SettingsState.getNetworkMode()) {
            case 1:
                return 15000L;
            case 2:
                return 30000L;
            case 3:
                return 60000L;
            case 4:
                return 300000L;
            default:
                return 4294967295L;
        }
    }
}
