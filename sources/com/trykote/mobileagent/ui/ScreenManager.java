package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public abstract class ScreenManager {

    // ListView types (lower 4 bits of typeAndFlags in createScreen header)
    static final int TYPE_FULLSCREEN = 0;
    static final int TYPE_FULLSCREEN_ALT = 1;
    static final int TYPE_DIALOG_CENTER = 2;
    static final int TYPE_DIALOG_BOTTOM = 3;
    static final int TYPE_DIALOG_CORNER = 4;
    static final int TYPE_FULLSCREEN_NOSCROLL = 5;
    static final int TYPE_MAP = 6;
    static final int TYPE_TOAST = 7;
    static final int TYPE_TOAST_CENTER = 8;
    static final int TYPE_FULLSCREEN_NOSCROLL_ALT = 9;
    static final int TYPE_POPUP = 10;
    static final int TYPE_DIALOG_LOW = 11;
    static final int TYPE_MAP_ALT = 12;

    // Bitmask of screen types requiring pre-show layout
    private static final int DIALOG_TYPE_MASK = 3484;

    // Font size codes for GraphicsContext
    private static final int FONT_SIZE_SMALL = 0;
    private static final int FONT_SIZE_MEDIUM = 8;
    private static final int FONT_SIZE_LARGE = 16;

    // Default screen width fallback
    private static final int DEFAULT_WIDTH = 200;

    // Icon vertical centering reference height
    private static final int ICON_HEIGHT = 16;

    // Cache timeout durations (milliseconds)
    private static final int CACHE_TIMEOUT_SHORT_MS = 60000;
    private static final int CACHE_TIMEOUT_LONG_MS = 300000;

    // Layout divisor for dialog-low vertical offset
    private static final int DIALOG_LOW_OFFSET_DIVISOR = 10;

    // Dialog size as fraction of screen: numerator / denominator = 90%
    private static final int DIALOG_SIZE_NUMER = 9;
    private static final int DIALOG_SIZE_DENOM = 10;

    // Phone input field count
    private static final int PHONE_FIELD_COUNT = 15;

    public static void initializeFonts() {
        int fontSizeSetting = SettingsState.getFontSizeChat();
        int fontSizeCode = fontSizeSetting == 0 ? FONT_SIZE_MEDIUM : fontSizeSetting == 1 ? FONT_SIZE_SMALL : FONT_SIZE_LARGE;
        GraphicsContext normalGfx = new GraphicsContext(0, fontSizeCode);
        UIState.setGfxContextBase(normalGfx);
        GraphicsContext boldGfx = new GraphicsContext(1, fontSizeCode);
        UIState.setGfxContextBold(boldGfx);
        GraphicsContext titleGfx = SettingsState.isBoldTitleFont() ? new GraphicsContext(2, fontSizeCode) : normalGfx;
        UIState.setGfxContextTitle(titleGfx);
        UIState.setGfxContextNormal(normalGfx);
        UIState.setGfxContextNormal2(normalGfx);
        UIState.setGfxContextBold2(boldGfx);
        UIState.setFontHeight(normalGfx.font.getHeight());
        UIState.setNormalFontHeight(normalGfx.font.getHeight());
        UIState.setNormalFontHeight2(normalGfx.font.getHeight());
        UIState.setBoldFontHeight2(boldGfx.font.getHeight());
        UIState.setBoldFontHeight(boldGfx.font.getHeight());
        UIState.setTitleFontHeight(titleGfx.font.getHeight());
        Vector screens = UIState.getScreenStack();
        for (int idx = screens.size() - 1; idx >= 0; idx--) {
            ((ListView) screens.elementAt(idx)).rebuildItems();
        }
    }

    public static ListView getCurrentScreen() {
        Vector screens = UIState.getScreenStack();
        if (screens.isEmpty()) {
            return null;
        }
        return (ListView) screens.lastElement();
    }

    public static String getCurrentTitle() {
        if (UIState.getScreenStack().size() > 0) {
            return getCurrentScreen().getSelectedTitle();
        }
        return null;
    }

    public static int getCurrentWidth() {
        if (UIState.getScreenStack().size() > 0) {
            return getCurrentScreen().getSelectedWidth();
        }
        return DEFAULT_WIDTH;
    }

    public static MenuItem getCurrentMenuItem() {
        if (UIState.getScreenStack().size() > 0) {
            return getCurrentScreen().getSelectedItem();
        }
        return null;
    }

    public static void pushScreen(ListView screen) {
        Vector screens = UIState.getScreenStack();
        while (screens.size() > 0) {
            ScreenBuilder.onScreenClosed();
        }
        screens.addElement(screen);
    }
    public static void showScreen(ListView screen) {
        RemoteLogger.log("SCR", "showScreen id=" + (screen != null ? screen.screenId : -1));
        ListView prevScreen = null;
        Vector screens = UIState.getScreenStack();
        int topIndex = screens.size() - 1;
        int topScreenId = topIndex >= 0 ? ((ListView) screens.elementAt(topIndex)).screenId : -1;
        if (topScreenId == ScreenId.MAIN_SCREEN || topScreenId == ScreenId.STATUS_INPUT) {
            ListView savedScreen = (ListView) screens.elementAt(topIndex);
            prevScreen = savedScreen;
            screens.removeElement(savedScreen);
        }
        int newScreenId = screen.screenId;
        if (newScreenId != ScreenId.CLEAR_NOTIFICATIONS) {
            int duplicateIdx = screens.size();
            for (int idx = 0; idx < duplicateIdx; idx++) {
                if (((ListView) screens.elementAt(idx)).screenId == newScreenId) {
                    duplicateIdx = idx;
                }
            }
            while (screens.size() > duplicateIdx) {
                ScreenBuilder.onScreenClosed();
            }
        }
        int screenType = screen.screenType;
        if (((1 << screenType) & DIALOG_TYPE_MASK) != 0) {
            screen.buildLayout();
        }
        int contentWidth = screen.containerWidth;
        int contentHeight = screen.containerHeight;
        int marginX = UIState.getScreenWidth() - contentWidth;
        int marginY = UIState.getHeight() - contentHeight;
        switch (screenType) {
            case TYPE_DIALOG_CENTER:
            case TYPE_TOAST:
            case TYPE_TOAST_CENTER:
                screen.setOffset(marginX >> 1, marginY >> 1);
                break;
            case TYPE_DIALOG_BOTTOM:
                screen.setOffset(0, marginY);
                break;
            case TYPE_DIALOG_CORNER:
                screen.setOffset(marginX, marginY);
                break;
            case TYPE_POPUP:
                positionPopup(screen);
                break;
            case TYPE_DIALOG_LOW:
                screen.setOffset(marginX >> 1, (UIState.getHeight() - contentHeight) - (contentHeight / DIALOG_LOW_OFFSET_DIVISOR));
                break;
        }
        int toastIdx = screens.size();
        for (int idx = 0; idx < toastIdx; idx++) {
            if (((ListView) screens.elementAt(idx)).screenType == TYPE_TOAST) {
                toastIdx = idx;
            }
        }
        while (screens.size() > toastIdx) {
            ScreenBuilder.onScreenClosed();
        }
        screen.invalidateLayout();
        screens.addElement(screen);
        if (prevScreen != null) {
            screens.addElement(prevScreen);
        }
    }

    private static void positionPopup(ListView screen) {
        ListView curScreen = getCurrentScreen();
        if (curScreen == null) {
            return;
        }
        int popupX = curScreen.offsetX + curScreen.containerWidth;
        int popupY = curScreen.getSelectedY();
        if (popupX + screen.containerWidth > UIState.getScreenWidth()) {
            popupX = UIState.getScreenWidth() - screen.containerWidth;
        }
        if (popupY + screen.containerHeight > UIState.getHeight()) {
            popupY = UIState.getHeight() - screen.containerHeight;
        }
        screen.setOffset(popupX, popupY);
    }

    public static boolean hasScreen(int screenId) {
        Vector screens = UIState.getScreenStack();
        int idx = screens.size();
        do {
            idx--;
            if (idx < 0) {
                return false;
            }
        } while (((ListView) screens.elementAt(idx)).screenId != screenId);
        return true;
    }

    public static int getCenterOffset() {
        return Utils.max(0, (UIState.getFontHeight() - ICON_HEIGHT) >> 1);
    }

    public static int handleScreenClose() {
        if (!UIState.isKnownDevice()) {
            return NotificationHelper.showError(470);
        }
        AppState.setScreen(new Object());
        return 0;
    }

    public static ListView createDialogScreen(int screenId) {
        ListView screen = new ListView(ListView.LAYOUT_VERTICAL, screenId, (UIState.getScreenWidth() * DIALOG_SIZE_NUMER) / DIALOG_SIZE_DENOM, (UIState.getHeight() * DIALOG_SIZE_NUMER) / DIALOG_SIZE_DENOM, true);
        screen.screenType = TYPE_DIALOG_CENTER;
        screen.showCheckboxes = true;
        return screen;
    }

    public static boolean hasModal() {
        Vector screens = UIState.getScreenStack();
        int idx = screens.size();
        do {
            idx--;
            if (idx <= 0) {
                return false;
            }
        } while (((ListView) screens.elementAt(idx)).offsetY != 0);
        return true;
    }


    public static int processScreenForm() {
        ((Screen) getCurrentScreen()).processForm();
        return 0;
    }

    public static int processPhoneInput(String fieldId) {
        String newValue = UIState.getStatusText();
        int idx = PHONE_FIELD_COUNT;
        do {
            idx--;
            if (idx < 0) {
                return 0;
            }
        } while (ResourceAccessor.blockStr(StringResKeys.PHONE_STRINGS_BASE, idx) != fieldId);
        AppState.setObject(StringResKeys.PHONE_STRINGS_BASE + idx, (Object) newValue);
        return 0;
    }

    public static void setFormFields(String param1, String param2, String param3, String param4, String param5) {
        UIState.setLanguageOption((Object) param5);
        UIState.setInitParamsFromBuffer(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(ObjectPool.newStringBuffer(), 262572, param1), 262576, param2), 524724, param3), 590268, param4), 524741, param5));
        TimerManager.setTimer(TimerManager.SLOT_SCREEN_INIT, computeInitialState());
    }

    public static String[] getLanguageOptions() {
        if (!SessionState.isCaptchaShown()) {
            setFormFields(null, null, null, null, null);
        } else if (TimerManager.checkTimer(TimerManager.SLOT_SCREEN_INIT, computeInitialState())) {
            UIState.clearInitParams();
        }
        String formData = UIState.getInitParams();
        if (formData == null) {
            return null;
        }
        String[] result = new String[2];
        result[0] = formData;
        String langOption = UIState.getLanguageOption();
        result[1] = langOption != null ? langOption : ResourceAccessor.str(StringResKeys.STR_DEFAULT_LANGUAGE);
        return result;
    }

    public static void prepareFormData() {
        RuntimeState.setErrorMsgIndex(0);
        RegistrationState.clearParam4();
        RegistrationState.setParam3(ObjectPool.newVector());
    }

    public static void clearFormFields() {
        UIState.clearInputRange();
    }

    public static int validateServerAddress(String address) {
        UIState.setStatusTextFromBuffer(Utils.getMessageBuffer().append(address));
        return 0;
    }

    public static int processInputText(String label) {
        UIState.setSpecialKeyMode(StringUtils.matchesKey(859, label));
        return 0;
    }

    public static int handleFormSubmit(Object listItem) {
        MapController.mapContextItem = (ListItem) listItem;
        return 0;
    }

    private static int computeInitialState() {
        return ResourceAccessor.bytes(StringResKeys.RES_UPDATE_DATA) != null ? CACHE_TIMEOUT_SHORT_MS : CACHE_TIMEOUT_LONG_MS;
    }

    public static int getThemeColor(int errorId) {
        for (int idx = SessionState.getAccounts().size() - 1; idx >= 0; idx--) {
            AccountManager.getAccountByIndex(idx).onError(errorId);
        }
        return 0;
    }

    public static int getThemeBackground(int optionId) {
        switch (optionId) {
            case 0:
                Conversation.incrementZoom();
                break;
            case 1:
                Conversation.decrementZoom();
                break;
            case 2:
                SettingsState.setCustomViewMode(1);
                break;
            case 3:
                SettingsState.setCustomViewMode(0);
                break;
            default:
                return 0;
        }
        MapRenderer.needsRedraw = true;
        return ScreenId.MAP;
    }

    public static int getScreenMode1() {
        return computeLayoutParam(1004);
    }

    public static int getScreenMode2() {
        return computeLayoutParam(1005);
    }

    public static int getScreenMode3() {
        return Integer.parseInt(StringUtils.getSystemProp(1006));
    }

    public static int getScreenMode4() {
        return Integer.parseInt(StringUtils.getSystemProp(1007));
    }

    public static int computeLayoutParam(int propKey) {
        return Integer.parseInt(StringUtils.getSystemProp(propKey), 16);
    }

    public static int handleThemeOption(int optionId) {
        if (optionId == 10) {
            return ScreenManager.getIconOffset();
        }
        return 0;
    }

    public static int handleSoundOption(int statusIndex) {
        UIState.setStatusTextFromBuffer(Utils.getMessageBuffer().append(AppState.getString(statusIndex + (AppState.getCurrentContact() instanceof MmpContact ? StringResKeys.MMP_EMOTICONS_BASE : AppState.getCurrentContact() instanceof XmppContact ? StringResKeys.XMPP_EMOTICONS_BASE : StringResKeys.EMOTICON_NAMES_BASE))));
        return ScreenId.STATUS_INPUT;
    }

    public static int getIconOffset() {
        return SettingsState.isFastConnection() ? 10 : 55;
    }
}
