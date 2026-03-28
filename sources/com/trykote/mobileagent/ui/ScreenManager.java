package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
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

/* renamed from: ad */
/* loaded from: MobileAgent_3.9.jar:ad.class */
public abstract class ScreenManager {

    // ListView types (lower 4 bits of typeAndFlags in createScreen header)
    private static final int TYPE_FULLSCREEN = 0;
    private static final int TYPE_FULLSCREEN_ALT = 1;
    private static final int TYPE_DIALOG_CENTER = 2;
    private static final int TYPE_DIALOG_BOTTOM = 3;
    private static final int TYPE_DIALOG_CORNER = 4;
    private static final int TYPE_FULLSCREEN_NOSCROLL = 5;
    private static final int TYPE_MAP = 6;
    private static final int TYPE_TOAST = 7;
    private static final int TYPE_TOAST_CENTER = 8;
    private static final int TYPE_FULLSCREEN_NOSCROLL_ALT = 9;
    private static final int TYPE_POPUP = 10;
    private static final int TYPE_DIALOG_LOW = 11;
    private static final int TYPE_MAP_ALT = 12;

    // Item types (lower 4 bits in parseScreenItem/processFormField)
    private static final int ITEM_ACTION = 0;
    private static final int ITEM_SEPARATOR = 1;
    private static final int ITEM_CHECKBOX = 2;
    private static final int ITEM_DROPDOWN = 3;
    private static final int ITEM_TEXT_SEPARATOR = 4;
    private static final int ITEM_TEXT_INPUT = 5;
    private static final int ITEM_LABEL_SEPARATOR = 6;
    private static final int ITEM_CONDITIONAL_IF = 7;
    private static final int ITEM_CONDITIONAL_UNLESS = 8;
    private static final int ITEM_LOGIN = 9;
    private static final int ITEM_PASSWORD = 10;
    private static final int ITEM_IMAGE = 11;
    private static final int ITEM_REDIRECT = 12;

    // Masks and flags
    private static final int MASK_TYPE = 15;
    private static final int FLAG_CHECKBOXES = 16;
    private static final int FLAG_DYNAMIC = 32;
    /* renamed from: a */
    public static final void initializeFonts() {
        int iM586d = AppState.getInt(SettingsKeys.SETTING_FONT_SIZE_CHAT);
        int i = iM586d == 0 ? 8 : iM586d == 1 ? 0 : 16;
        GraphicsContext normalGfx = new GraphicsContext(0, i);
        AppState.pool[UIKeys.GFX_CONTEXT_BASE] = normalGfx;
        GraphicsContext boldGfx = new GraphicsContext(1, i);
        AppState.pool[UIKeys.GFX_CONTEXT_BOLD] = boldGfx;
        GraphicsContext titleGfx = AppState.getBool(SettingsKeys.SETTING_BOLD_TITLE_FONT) ? new GraphicsContext(2, i) : normalGfx;
        AppState.pool[UIKeys.GFX_CONTEXT_TITLE] = titleGfx;
        AppState.pool[UIKeys.GFX_CONTEXT_NORMAL] = normalGfx;
        AppState.pool[UIKeys.GFX_CONTEXT_NORMAL_2] = normalGfx;
        AppState.pool[UIKeys.GFX_CONTEXT_BOLD_2] = boldGfx;
        AppState.setInt(UIKeys.INT_FONT_HEIGHT, normalGfx.font.getHeight());
        AppState.setInt(UIKeys.INT_NORMAL_FONT_HEIGHT, normalGfx.font.getHeight());
        AppState.setInt(UIKeys.INT_NORMAL_FONT_HEIGHT_2, normalGfx.font.getHeight());
        AppState.setInt(UIKeys.INT_BOLD_FONT_HEIGHT_2, boldGfx.font.getHeight());
        AppState.setInt(UIKeys.INT_BOLD_FONT_HEIGHT, boldGfx.font.getHeight());
        AppState.setInt(UIKeys.INT_TITLE_FONT_HEIGHT, titleGfx.font.getHeight());
        Vector screens = AppState.getVector(UIKeys.VEC_SCREEN_STACK);
        int size = screens.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((ListView) screens.elementAt(size)).rebuildItems();
            }
        }
    }

    /* renamed from: b */
    public static final ListView getCurrentScreen() {
        Vector screens = AppState.getVector(UIKeys.VEC_SCREEN_STACK);
        if (screens.isEmpty()) {
            return null;
        }
        return (ListView) screens.lastElement();
    }

    /* renamed from: c */
    public static final String getCurrentTitle() {
        if (AppState.getVector(UIKeys.VEC_SCREEN_STACK).size() > 0) {
            return getCurrentScreen().getSelectedTitle();
        }
        return null;
    }

    /* renamed from: d */
    public static final int getCurrentWidth() {
        if (AppState.getVector(UIKeys.VEC_SCREEN_STACK).size() > 0) {
            return getCurrentScreen().getSelectedWidth();
        }
        return 200;
    }

    /* renamed from: e */
    public static final MenuItem getCurrentMenuItem() {
        if (AppState.getVector(UIKeys.VEC_SCREEN_STACK).size() > 0) {
            return getCurrentScreen().getSelectedItem();
        }
        return null;
    }

    /* renamed from: a */
    public static final void pushScreen(ListView screen) {
        Vector screens = AppState.getVector(UIKeys.VEC_SCREEN_STACK);
        while (screens.size() > 0) {
            ScreenBuilder.onScreenClosed();
        }
        screens.addElement(screen);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0166  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void showScreen(ListView screen) {
        RemoteLogger.log("SCR", "showScreen id=" + (screen != null ? screen.screenId : -1));
        ListView prevScreen = null;
        Vector screens = AppState.getVector(UIKeys.VEC_SCREEN_STACK);
        int size = screens.size() - 1;
        int i = size >= 0 ? ((ListView) screens.elementAt(size)).screenId : -1;
        if (i == 137 || i == 63) {
            ListView savedScreen = (ListView) screens.elementAt(size);
            prevScreen = savedScreen;
            screens.removeElement(savedScreen);
        }
        int i2 = screen.screenId;
        if (i2 != 112) {
            int size2 = screens.size();
            for (int i3 = 0; i3 < size2; i3++) {
                if (((ListView) screens.elementAt(i3)).screenId == i2) {
                    size2 = i3;
                }
            }
            while (screens.size() > size2) {
                ScreenBuilder.onScreenClosed();
            }
        }
        int i4 = screen.screenType;
        if (((1 << i4) & 3484) != 0) {
            screen.buildLayout();
        }
        int i5 = screen.containerWidth;
        int i6 = screen.containerHeight;
        int iM586d = AppState.getInt(UIKeys.INT_SCREEN_WIDTH) - i5;
        int screenH = AppState.getHeight() - i6;
        switch (i4) {
            case TYPE_DIALOG_CENTER:
            case TYPE_TOAST:
            case TYPE_TOAST_CENTER:
                screen.setOffset(iM586d >> 1, screenH >> 1);
                break;
            case TYPE_DIALOG_BOTTOM:
                screen.setOffset(0, screenH);
                break;
            case TYPE_DIALOG_CORNER:
                screen.setOffset(iM586d, screenH);
                break;
            case TYPE_POPUP:
                ListView curScreen = getCurrentScreen();
                if (curScreen != null) {
                    int iM586d2 = curScreen.offsetX + curScreen.containerWidth;
                    int selectedY = curScreen.getSelectedY();
                    if (iM586d2 + screen.containerWidth > AppState.getInt(UIKeys.INT_SCREEN_WIDTH)) {
                        iM586d2 = AppState.getInt(UIKeys.INT_SCREEN_WIDTH) - screen.containerWidth;
                    }
                    if (selectedY + screen.containerHeight > AppState.getHeight()) {
                        selectedY = AppState.getHeight() - screen.containerHeight;
                    }
                    screen.setOffset(iM586d2, selectedY);
                    break;
                }
                break;
            case TYPE_DIALOG_LOW:
                screen.setOffset(iM586d >> 1, (AppState.getHeight() - i6) - (i6 / 10));
                break;
        }
        int size3 = screens.size();
        for (int i7 = 0; i7 < size3; i7++) {
            if (((ListView) screens.elementAt(i7)).screenType == TYPE_TOAST) {
                size3 = i7;
            }
        }
        while (screens.size() > size3) {
            ScreenBuilder.onScreenClosed();
        }
        screen.invalidateLayout();
        screens.addElement(screen);
        if (prevScreen != null) {
            screens.addElement(prevScreen);
        }
    }

    /* renamed from: a */
    public static final boolean hasScreen(int i) {
        Vector screens = AppState.getVector(UIKeys.VEC_SCREEN_STACK);
        int size = screens.size();
        do {
            size--;
            if (size < 0) {
                return false;
            }
        } while (((ListView) screens.elementAt(size)).screenId != i);
        return true;
    }

    /* renamed from: f */
    public static final int getCenterOffset() {
        return Utils.max(0, (AppState.getInt(UIKeys.INT_FONT_HEIGHT) - 16) >> 1);
    }

    /* renamed from: g */
    public static final int handleScreenClose() {
        if (!AppState.getBool(UIKeys.FLAG_KNOWN_DEVICE)) {
            return NotificationHelper.showError(470);
        }
        AppState.setScreen(new Object());
        return 0;
    }

    /* renamed from: b */
    public static final ListView createScreen(int offset) {
        ListView screen;
        int pos = offset;
        String title = Utils.defaultStr(AppState.getString(AppState.getInt(pos++)));
        int screenId = AppState.getInt(pos++);
        int typeAndFlags = AppState.getInt(pos++);
        boolean showCheckboxes = (typeAndFlags & FLAG_CHECKBOXES) != 0;
        int screenType = typeAndFlags & MASK_TYPE;
        int headerMode = AppState.getInt(pos++);
        int leftKeyLabel = AppState.getInt(pos++);
        int rightKeyLabel = AppState.getInt(pos++);
        int leftKeyCmd = AppState.getInt(pos++);
        int rightKeyCmd = AppState.getInt(pos++);
        int extraKeyCmd = AppState.getInt(pos++);
        int itemCount = AppState.getInt(pos++);
        RemoteLogger.log("SCR", "createScreen(" + offset + "): type=" + screenType + " items=" + itemCount + " id=" + screenId);
        int screenW = AppState.getInt(UIKeys.INT_SCREEN_WIDTH);
        int screenH = AppState.getHeight();
        switch (screenType) {
            case TYPE_FULLSCREEN:
            case TYPE_FULLSCREEN_ALT:
                screen = new ListView(0, screenId, screenW, screenH, true);
                break;
            case TYPE_DIALOG_CENTER:
            case TYPE_DIALOG_BOTTOM:
            case TYPE_DIALOG_CORNER:
            case TYPE_POPUP:
            case TYPE_DIALOG_LOW:
                screen = new ListView(0, screenId, (screenW * 9) / 10, (screenH * 9) / 10, true);
                break;
            case TYPE_FULLSCREEN_NOSCROLL:
            case TYPE_FULLSCREEN_NOSCROLL_ALT:
                screen = new ListView(0, screenId, screenW, screenH, false);
                break;
            case TYPE_MAP:
            case TYPE_MAP_ALT:
                screen = new ListView(1, screenId, screenW, screenH, true);
                break;
            case TYPE_TOAST:
            case TYPE_TOAST_CENTER:
                screen = new ListView(0, screenId, (screenW * 9) / 10, (screenH * 9) / 10, false);
                break;
            default:
                screen = null;
                break;
        }
        RemoteLogger.log("SCR", "ListView created OK");
        if (screenType != TYPE_DIALOG_BOTTOM && screenType != TYPE_DIALOG_CORNER && screenType != TYPE_DIALOG_CENTER && screenType != TYPE_DIALOG_LOW && screenType != TYPE_POPUP && screenType != TYPE_TOAST_CENTER) {
            if (screenType != TYPE_TOAST) {
                RemoteLogger.log("SCR", "setHeader(" + headerMode + ", " + title + ")");
                screen.setHeader(headerMode, title);
                RemoteLogger.log("SCR", "setHeader done");
            } else {
                screen.addItem(new MenuItem(0, title).addText(title, 1, 0));
            }
        }
        screen.showCheckboxes = showCheckboxes;
        screen.definitionOffset = offset;
        RemoteLogger.log("SCR", "items loop start");
        for (int i = 0; i < itemCount; i++) {
            pos = parseScreenItem(screen, pos, screenId);
        }
        ListView configuredScreen = screen.setSoftKeys(leftKeyLabel > 0 ? AppState.getString(leftKeyLabel) : null, rightKeyLabel > 0 ? AppState.getString(rightKeyLabel) : null, leftKeyCmd, rightKeyCmd, extraKeyCmd);
        configuredScreen.screenType = screenType;
        RemoteLogger.log("SCR", "createScreen(" + offset + ") done: items=" + screen.menuItems.size() + " skL=" + leftKeyCmd + " skR=" + extraKeyCmd);
        return configuredScreen;
    }

    /* renamed from: c */
    public static final ListView createDialogScreen(int i) {
        ListView screen = new ListView(0, i, (AppState.getInt(UIKeys.INT_SCREEN_WIDTH) * 9) / 10, (AppState.getHeight() * 9) / 10, true);
        screen.screenType = TYPE_DIALOG_CENTER;
        screen.showCheckboxes = true;
        return screen;
    }

    /* renamed from: h */
    public static final boolean hasModal() {
        Vector screens = AppState.getVector(UIKeys.VEC_SCREEN_STACK);
        int size = screens.size();
        do {
            size--;
            if (size <= 0) {
                return false;
            }
        } while (((ListView) screens.elementAt(size)).offsetY != 0);
        return true;
    }

    /* renamed from: a */
    private static final int addItemToScreen(boolean isVisible, ListView screen, int pos, boolean isAction) {
        int labelKey = AppState.getInt(pos++);
        int iconKey = AppState.getInt(pos++);
        int cmdKey = AppState.getInt(pos++);
        if (isVisible) {
            if (isAction) {
                screen.addActionById(iconKey, cmdKey, labelKey);
            } else {
                screen.addIconById(iconKey, cmdKey, labelKey);
            }
        }
        return pos;
    }

    /* renamed from: a */
    private static final int parseScreenItem(ListView screen, int pos, int screenId) {
        int nextPos;
        Object itemData;
        int afterPos;
        String title;
        int typeFlags = AppState.getInt(pos++);
        RemoteLogger.log("SCR", "parseItem pos=" + (pos - 1) + " type=" + (typeFlags & MASK_TYPE));
        boolean isEnabled = (typeFlags & FLAG_CHECKBOXES) != 0;
        boolean isDynamic = (typeFlags & FLAG_DYNAMIC) != 0;
        switch (typeFlags & MASK_TYPE) {
            case ITEM_ACTION:
                if (isDynamic) {
                    pos++;
                    isEnabled = AppState.getBool(AppState.getInt(pos));
                }
                int labelKey = AppState.getInt(pos++);
                int iconKey = AppState.getInt(pos++);
                int cmdKey = AppState.getInt(pos++);
                if (isEnabled) {
                    screen.addActionById(iconKey, cmdKey, labelKey);
                } else {
                    screen.addIconById(iconKey, cmdKey, labelKey);
                }
                return pos;
            case ITEM_SEPARATOR:
                MenuItem separator = MenuItem.createSeparator().addText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Utils.defaultStr(AppState.getString(AppState.getInt(pos++)))).append(' ')), 0, 0);
                String sublabel = Utils.defaultStr(AppState.getString(AppState.getInt(pos++)));
                if (!StringUtils.isEmpty(sublabel)) {
                    separator.addText(sublabel, 0, 6);
                }
                screen.addItem(separator);
                return pos;
            case ITEM_CHECKBOX:
                String checkLabel = Utils.defaultStr(AppState.getString(AppState.getInt(pos++)));
                screen.addItem(MenuItem.createCheckbox(checkLabel, AppState.getBool(AppState.getInt(pos++))));
                return pos;
            case ITEM_DROPDOWN:
                String choiceLabel = Utils.defaultStr(AppState.getString(AppState.getInt(pos++)));
                Vector choices = Utils.splitByNull(Utils.defaultStr(AppState.getString(AppState.getInt(pos++))));
                screen.addItem(new MenuItem(9, choiceLabel).setChoices(choices, AppState.getInt(AppState.getInt(pos++)), choiceLabel));
                return pos;
            case ITEM_TEXT_SEPARATOR:
                screen.addItem(MenuItem.createSeparator().addText(Utils.defaultStr(AppState.getString(AppState.getInt(pos++))), 1, 0));
                return pos;
            case ITEM_TEXT_INPUT:
                if (screenId == 49) {
                    int dataKey = AppState.getInt(pos++);
                    itemData = (dataKey < 268 || dataKey > 304) ? (dataKey < 161 || dataKey > 210) ? AppState.getString(dataKey) : ResourceManager.integerOf(dataKey) : ResourceManager.integerOf(dataKey);
                } else {
                    itemData = Utils.defaultStr(AppState.getString(AppState.getInt(pos++)));
                }
                Object obj = itemData;
                int inputType = AppState.getInt(pos++);
                String hintText = Utils.defaultStr(AppState.getString(AppState.getInt(pos++)));
                int validationType = AppState.getInt(pos++);
                if (validationType == 2) {
                    pos += 3;
                    int numValue = AppState.getInt(AppState.getInt(pos++));
                    title = numValue >= 0 ? StringUtils.intern(Integer.toString(numValue)) : AppState.emptyStr;
                } else {
                    title = Utils.defaultStr(Utils.defaultStr(AppState.getString(AppState.getInt(pos++))));
                }
                screen.addItem(new MenuItem(15, obj instanceof String ? (String) obj : AppState.emptyStr).setAction(obj, title, ResourceManager.integerOf(inputType), ResourceManager.integerOf(validationType), hintText));
                return pos;
            case ITEM_LABEL_SEPARATOR:
                screen.addItem(MenuItem.createSeparator().setLabel(Utils.defaultStr(AppState.getString(AppState.getInt(pos++)))));
                return pos;
            case ITEM_CONDITIONAL_IF:
                return addItemToScreen(AppState.getBool(AppState.getInt(pos)), screen, pos + 1, isEnabled);
            case ITEM_CONDITIONAL_UNLESS:
                return addItemToScreen(!AppState.getBool(AppState.getInt(pos)), screen, pos + 1, isEnabled);
            case ITEM_LOGIN:
                String loginLabel = Utils.defaultStr(AppState.getString(AppState.getInt(pos)));
                String loginValue = Utils.defaultStr(AppState.getString(AppState.getInt(pos + 1)));
                MenuItem loginItem = new MenuItem(4, (String) null).clear().setIcon(221).addText(Utils.nonEmpty(loginValue) ? loginValue : loginLabel, 1, 7);
                loginItem.data = new String[]{loginLabel, loginValue};
                screen.addItem(loginItem);
                return pos + 2;
            case ITEM_PASSWORD:
                String passwordStr = Utils.defaultStr(AppState.getString(AppState.getInt(pos)));
                MenuItem menuItem = new MenuItem(5, (String) null);
                menuItem.clear();
                menuItem.setIcon(219);
                if (Utils.nonEmpty(passwordStr)) {
                    int idx = passwordStr.indexOf(0);
                    menuItem.addText(idx < 0 ? passwordStr : StringUtils.prefix(passwordStr, idx), 1, 7);
                } else {
                    menuItem.setDefaultFont();
                }
                menuItem.data = passwordStr;
                screen.addItem(menuItem);
                return pos + 1;
            case ITEM_IMAGE:
                screen.addItem(MenuItem.createGraphics(new GraphicsContext((Image) AppState.pool[AppState.getInt(pos)])));
                return pos + 1;
            default:
                RemoteLogger.log("SCR", "parseItem DEFAULT type=" + (typeFlags & MASK_TYPE) + " recurse to " + AppState.getInt(pos));
                parseScreenItem(screen, AppState.getInt(pos), screenId);
                return pos + 1;
        }
    }

    /* renamed from: a */
    private static final int processFormField(int pos, Object data) {
        int nextIdx;
        int idx = pos + 1;
        switch (AppState.getInt(pos)) {
            case ITEM_SEPARATOR:
                idx += 2;
                break;
            case ITEM_CHECKBOX:
                AppState.setBool(AppState.getInt(idx + 1), ((Boolean) data).booleanValue());
                idx += 2;
                break;
            case ITEM_DROPDOWN:
                AppState.setIntInd(idx + 2, ((Integer) ((Object[]) data)[0]).intValue());
                idx += 3;
                break;
            case ITEM_TEXT_SEPARATOR:
                idx++;
                break;
            case ITEM_TEXT_INPUT:
                int baseIdx = idx + 3;
                String value = (String) ((Object[]) data)[0];
                int curIdx = baseIdx + 1;
                if (AppState.getInt(baseIdx) == 2) {
                    int minIdx = curIdx + 1;
                    int minValue = AppState.getInt(curIdx);
                    int maxIdx = minIdx + 1;
                    int maxValue = AppState.getInt(minIdx);
                    int defIdx = maxIdx + 1;
                    int parsedValue = Utils.parseIntBounded(value, minValue, maxValue, AppState.getInt(maxIdx));
                    nextIdx = defIdx + 1;
                    AppState.setIntInd(defIdx, parsedValue);
                } else {
                    nextIdx = curIdx + 1;
                    AppState.setStringInd(curIdx, value);
                }
                idx += nextIdx - idx;
                break;
            case ITEM_LABEL_SEPARATOR:
                idx++;
                break;
            case ITEM_CONDITIONAL_IF:
            case ITEM_CONDITIONAL_UNLESS:
                idx += 3;
                break;
            case ITEM_LOGIN:
                AppState.setStringInd(idx + 1, ((String[]) data)[1]);
                idx += 2;
                break;
            case ITEM_PASSWORD:
                AppState.setStringInd(idx, (String) data);
                idx++;
                break;
            case ITEM_IMAGE:
                idx++;
                break;
            case ITEM_REDIRECT:
                processFormField(AppState.getInt(idx), data);
                idx++;
                break;
        }
        return idx;
    }

    /* renamed from: d */
    public static final int processScreenForm() {
        ListView screen = getCurrentScreen();
        int i = screen.definitionOffset + 9;
        Vector items = screen.menuItems;
        int fieldIdx = i + 1;
        int fieldCount = AppState.getInt(i);
        for (int i2 = 0; i2 < fieldCount; i2++) {
            fieldIdx = processFormField(fieldIdx, ((MenuItem) items.elementAt(i2)).data);
        }
        return 0;
    }

    public static final int processPhoneInput(String fieldId) {
        String newValue = AppState.getString(UIKeys.SLOT_STATUS_TEXT);
        int i = 15;
        do {
            i--;
            if (i < 0) {
                return 0;
            }
        } while (AppState.getString(i + 48) != fieldId);
        AppState.setObject(i + 48, (Object) newValue);
        return 0;
    }

    public static final void setFormFields(String param1, String param2, String param3, String param4, String param5) {
        AppState.setObject(UIKeys.SLOT_LANGUAGE_OPTION, (Object) param5);
        AppState.setFromBuffer(UIKeys.SLOT_INIT_PARAMS, Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(ObjectPool.newStringBuffer(), 262572, param1), 262576, param2), 524724, param3), 590268, param4), 524741, param5));
        TimerManager.setTimer(TimerManager.SLOT_SCREEN_INIT, computeInitialState());
    }

    public static final String[] getLanguageOptions() {
        if (!AppState.getBool(SessionKeys.FLAG_CAPTCHA_SHOWN)) {
            setFormFields(null, null, null, null, null);
        } else if (TimerManager.checkTimer(13, computeInitialState())) {
            AppState.clearIndex(UIKeys.SLOT_INIT_PARAMS);
        }
        String formData = AppState.getString(UIKeys.SLOT_INIT_PARAMS);
        if (formData == null) {
            return null;
        }
        String[] strArr = new String[2];
        strArr[0] = formData;
        String langOption = AppState.getString(UIKeys.SLOT_LANGUAGE_OPTION);
        strArr[1] = langOption != null ? langOption : AppState.getString(StringResKeys.STR_DEFAULT_LANGUAGE);
        return strArr;
    }

    public static final void prepareFormData() {
        AppState.setInt(RuntimeKeys.INT_ERROR_MSG_INDEX, 0);
        AppState.clearIndex(RegistrationKeys.SLOT_REG_PARAM_4);
        AppState.pool[RegistrationKeys.SLOT_REG_PARAM_3] = ObjectPool.newVector();
    }

    public static final void clearFormFields() {
        AppState.clearRange(UIKeys.SLOT_INPUT_TEXT, UIKeys.RANGE_INPUT_TEXT_END);
    }

    public static final int validateServerAddress(String address) {
        AppState.setFromBuffer(UIKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(address));
        return 0;
    }

    public static final int processInputText(String label) {
        AppState.setBool(UIKeys.FLAG_SPECIAL_KEY_MODE, StringUtils.matchesKey(859, label));
        return 0;
    }

    public static final int handleFormSubmit(Object listItem) {
        MapController.mapContextItem = (ListItem) listItem;
        return 0;
    }

    private static final int computeInitialState() {
        return AppState.getBytes(StringResKeys.RES_UPDATE_DATA) != null ? 60000 : 300000;
    }

    public static final int getThemeColor(int errorId) {
        int size = AppState.getVector(SessionKeys.VEC_ACCOUNTS).size();
        while (true) {
            size--;
            if (size < 0) {
                return 0;
            }
            AccountManager.getAccountByIndex(size).onError(errorId);
        }
    }

    public static final int getThemeBackground(int optionId) {
        switch (optionId) {
            case 0:
                Conversation.incrementZoom();
                break;
            case 1:
                Conversation.decrementZoom();
                break;
            case 2:
                AppState.setInt(SettingsKeys.SETTING_CUSTOM_VIEW_MODE, 1);
                break;
            case 3:
                AppState.setInt(SettingsKeys.SETTING_CUSTOM_VIEW_MODE, 0);
                break;
            default:
                return 0;
        }
        MapRenderer.needsRedraw = true;
        return ScreenId.MAP;
    }

    public static final int getScreenMode1() {
        return computeLayoutParam(1004);
    }

    public static final int getScreenMode2() {
        return computeLayoutParam(1005);
    }

    public static final int getScreenMode3() {
        return Integer.parseInt(StringUtils.getSystemProp(1006));
    }

    public static final int getScreenMode4() {
        return Integer.parseInt(StringUtils.getSystemProp(1007));
    }

    public static final int computeLayoutParam(int i) {
        return Integer.parseInt(StringUtils.getSystemProp(i), 16);
    }

    public static final int handleThemeOption(int optionId) {
        if (optionId == 10) {
            return ScreenManager.getIconOffset();
        }
        return 0;
    }

    public static final int handleSoundOption(int statusIndex) {
        AppState.setFromBuffer(UIKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(AppState.getString(statusIndex + (AppState.getCurrentContact() instanceof MmpContact ? 1141 : AppState.getCurrentContact() instanceof XmppContact ? 1184 : 1063))));
        return ScreenId.STATUS_INPUT;
    }

    public static final int getIconOffset() {
        return AppState.getBool(SettingsKeys.SETTING_FAST_CONNECTION) ? 10 : 55;
    }
}
