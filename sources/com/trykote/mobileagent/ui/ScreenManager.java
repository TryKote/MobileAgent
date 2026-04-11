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

    // Text format flag for secondary/sublabel text
    private static final int TEXT_FORMAT_SECONDARY = 6;

    // AppState key ranges for integer-valued form data (CHAT_ROOM_CONFIG screen)
    private static final int INT_KEY_RANGE_START = 161;
    private static final int INT_KEY_RANGE_END = 210;
    private static final int INT_KEY_RANGE_START_2 = 268;
    private static final int INT_KEY_RANGE_END_2 = 304;

    public static final void initializeFonts() {
        int fontSizeSetting = Storage.state().getInt(SettingsKeys.SETTING_FONT_SIZE_CHAT);
        int fontSizeCode = fontSizeSetting == 0 ? FONT_SIZE_MEDIUM : fontSizeSetting == 1 ? FONT_SIZE_SMALL : FONT_SIZE_LARGE;
        GraphicsContext normalGfx = new GraphicsContext(0, fontSizeCode);
        Storage.state().setObject(UIKeys.GFX_CONTEXT_BASE, normalGfx);
        GraphicsContext boldGfx = new GraphicsContext(1, fontSizeCode);
        Storage.state().setObject(UIKeys.GFX_CONTEXT_BOLD, boldGfx);
        GraphicsContext titleGfx = Storage.state().getBool(SettingsKeys.SETTING_BOLD_TITLE_FONT) ? new GraphicsContext(2, fontSizeCode) : normalGfx;
        Storage.state().setObject(UIKeys.GFX_CONTEXT_TITLE, titleGfx);
        Storage.state().setObject(UIKeys.GFX_CONTEXT_NORMAL, normalGfx);
        Storage.state().setObject(UIKeys.GFX_CONTEXT_NORMAL_2, normalGfx);
        Storage.state().setObject(UIKeys.GFX_CONTEXT_BOLD_2, boldGfx);
        Storage.state().setInt(UIKeys.INT_FONT_HEIGHT, normalGfx.font.getHeight());
        Storage.state().setInt(UIKeys.INT_NORMAL_FONT_HEIGHT, normalGfx.font.getHeight());
        Storage.state().setInt(UIKeys.INT_NORMAL_FONT_HEIGHT_2, normalGfx.font.getHeight());
        Storage.state().setInt(UIKeys.INT_BOLD_FONT_HEIGHT_2, boldGfx.font.getHeight());
        Storage.state().setInt(UIKeys.INT_BOLD_FONT_HEIGHT, boldGfx.font.getHeight());
        Storage.state().setInt(UIKeys.INT_TITLE_FONT_HEIGHT, titleGfx.font.getHeight());
        Vector screens = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
        for (int idx = screens.size() - 1; idx >= 0; idx--) {
            ((ListView) screens.elementAt(idx)).rebuildItems();
        }
    }

    public static final ListView getCurrentScreen() {
        Vector screens = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
        if (screens.isEmpty()) {
            return null;
        }
        return (ListView) screens.lastElement();
    }

    public static final String getCurrentTitle() {
        if (Storage.state().getVector(UIKeys.VEC_SCREEN_STACK).size() > 0) {
            return getCurrentScreen().getSelectedTitle();
        }
        return null;
    }

    public static final int getCurrentWidth() {
        if (Storage.state().getVector(UIKeys.VEC_SCREEN_STACK).size() > 0) {
            return getCurrentScreen().getSelectedWidth();
        }
        return DEFAULT_WIDTH;
    }

    public static final MenuItem getCurrentMenuItem() {
        if (Storage.state().getVector(UIKeys.VEC_SCREEN_STACK).size() > 0) {
            return getCurrentScreen().getSelectedItem();
        }
        return null;
    }

    public static final void pushScreen(ListView screen) {
        Vector screens = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
        while (screens.size() > 0) {
            ScreenBuilder.onScreenClosed();
        }
        screens.addElement(screen);
    }
    public static final void showScreen(ListView screen) {
        RemoteLogger.log("SCR", "showScreen id=" + (screen != null ? screen.screenId : -1));
        ListView prevScreen = null;
        Vector screens = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
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
        int marginX = Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH) - contentWidth;
        int marginY = Storage.state().getHeight() - contentHeight;
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
                ListView curScreen = getCurrentScreen();
                if (curScreen != null) {
                    int popupX = curScreen.offsetX + curScreen.containerWidth;
                    int popupY = curScreen.getSelectedY();
                    if (popupX + screen.containerWidth > Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH)) {
                        popupX = Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH) - screen.containerWidth;
                    }
                    if (popupY + screen.containerHeight > Storage.state().getHeight()) {
                        popupY = Storage.state().getHeight() - screen.containerHeight;
                    }
                    screen.setOffset(popupX, popupY);
                    break;
                }
                break;
            case TYPE_DIALOG_LOW:
                screen.setOffset(marginX >> 1, (Storage.state().getHeight() - contentHeight) - (contentHeight / DIALOG_LOW_OFFSET_DIVISOR));
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

    public static final boolean hasScreen(int screenId) {
        Vector screens = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
        int idx = screens.size();
        do {
            idx--;
            if (idx < 0) {
                return false;
            }
        } while (((ListView) screens.elementAt(idx)).screenId != screenId);
        return true;
    }

    public static final int getCenterOffset() {
        return Utils.max(0, (Storage.state().getInt(UIKeys.INT_FONT_HEIGHT) - ICON_HEIGHT) >> 1);
    }

    public static final int handleScreenClose() {
        if (!Storage.state().getBool(UIKeys.FLAG_KNOWN_DEVICE)) {
            return NotificationHelper.showError(470);
        }
        Storage.state().setScreen(new Object());
        return 0;
    }

    public static final ListView createScreen(int offset) {
        ListView screen;
        int pos = offset;
        String title = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)));
        int screenId = Storage.state().getInt(pos++);
        int typeAndFlags = Storage.state().getInt(pos++);
        boolean showCheckboxes = (typeAndFlags & FLAG_CHECKBOXES) != 0;
        int screenType = typeAndFlags & MASK_TYPE;
        int headerMode = Storage.state().getInt(pos++);
        int leftKeyLabel = Storage.state().getInt(pos++);
        int rightKeyLabel = Storage.state().getInt(pos++);
        int leftKeyCmd = Storage.state().getInt(pos++);
        int rightKeyCmd = Storage.state().getInt(pos++);
        int extraKeyCmd = Storage.state().getInt(pos++);
        int itemCount = Storage.state().getInt(pos++);
        RemoteLogger.log("SCR", "createScreen(" + offset + "): type=" + screenType + " items=" + itemCount + " id=" + screenId);
        int screenW = Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH);
        int screenH = Storage.state().getHeight();
        switch (screenType) {
            case TYPE_FULLSCREEN:
            case TYPE_FULLSCREEN_ALT:
                screen = new ListView(ListView.LAYOUT_VERTICAL, screenId, screenW, screenH, true);
                break;
            case TYPE_DIALOG_CENTER:
            case TYPE_DIALOG_BOTTOM:
            case TYPE_DIALOG_CORNER:
            case TYPE_POPUP:
            case TYPE_DIALOG_LOW:
                screen = new ListView(ListView.LAYOUT_VERTICAL, screenId, (screenW * 9) / 10, (screenH * 9) / 10, true);
                break;
            case TYPE_FULLSCREEN_NOSCROLL:
            case TYPE_FULLSCREEN_NOSCROLL_ALT:
                screen = new ListView(ListView.LAYOUT_VERTICAL, screenId, screenW, screenH, false);
                break;
            case TYPE_MAP:
            case TYPE_MAP_ALT:
                screen = new ListView(ListView.LAYOUT_GRID, screenId, screenW, screenH, true);
                break;
            case TYPE_TOAST:
            case TYPE_TOAST_CENTER:
                screen = new ListView(ListView.LAYOUT_VERTICAL, screenId, (screenW * 9) / 10, (screenH * 9) / 10, false);
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
        ListView configuredScreen = screen.setSoftKeys(leftKeyLabel > 0 ? Storage.state().getString(leftKeyLabel) : null, rightKeyLabel > 0 ? Storage.state().getString(rightKeyLabel) : null, leftKeyCmd, rightKeyCmd, extraKeyCmd);
        configuredScreen.screenType = screenType;
        RemoteLogger.log("SCR", "createScreen(" + offset + ") done: items=" + screen.menuItems.size() + " skL=" + leftKeyCmd + " skR=" + extraKeyCmd);
        return configuredScreen;
    }

    public static final ListView createDialogScreen(int screenId) {
        ListView screen = new ListView(ListView.LAYOUT_VERTICAL, screenId, (Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH) * 9) / 10, (Storage.state().getHeight() * 9) / 10, true);
        screen.screenType = TYPE_DIALOG_CENTER;
        screen.showCheckboxes = true;
        return screen;
    }

    public static final boolean hasModal() {
        Vector screens = Storage.state().getVector(UIKeys.VEC_SCREEN_STACK);
        int idx = screens.size();
        do {
            idx--;
            if (idx <= 0) {
                return false;
            }
        } while (((ListView) screens.elementAt(idx)).offsetY != 0);
        return true;
    }

    private static final int addItemToScreen(boolean isVisible, ListView screen, int pos, boolean isAction) {
        int labelKey = Storage.state().getInt(pos++);
        int iconKey = Storage.state().getInt(pos++);
        int cmdKey = Storage.state().getInt(pos++);
        if (isVisible) {
            if (isAction) {
                screen.addActionById(iconKey, cmdKey, labelKey);
            } else {
                screen.addIconById(iconKey, cmdKey, labelKey);
            }
        }
        return pos;
    }

    private static final int parseScreenItem(ListView screen, int pos, int screenId) {
        Object itemData;
        String title;
        int typeFlags = Storage.state().getInt(pos++);
        RemoteLogger.log("SCR", "parseItem pos=" + (pos - 1) + " type=" + (typeFlags & MASK_TYPE));
        boolean isEnabled = (typeFlags & FLAG_CHECKBOXES) != 0;
        boolean isDynamic = (typeFlags & FLAG_DYNAMIC) != 0;
        switch (typeFlags & MASK_TYPE) {
            case ITEM_ACTION:
                if (isDynamic) {
                    pos++;
                    int condKey = Storage.state().getInt(pos);
                    RemoteLogger.log("SCR", "ACTION dyn condKey=" + condKey + " pos=" + pos);
                    isEnabled = Storage.state().getBool(condKey);
                    RemoteLogger.log("SCR", "ACTION dyn isEnabled=" + isEnabled);
                }
                int labelKey = Storage.state().getInt(pos++);
                int iconKey = Storage.state().getInt(pos++);
                int cmdKey = Storage.state().getInt(pos++);
                RemoteLogger.log("SCR", "ACTION label=" + labelKey + " icon=" + iconKey + " cmd=" + cmdKey + " enabled=" + isEnabled);
                if (isEnabled) {
                    screen.addActionById(iconKey, cmdKey, labelKey);
                } else {
                    screen.addIconById(iconKey, cmdKey, labelKey);
                }
                return pos;
            case ITEM_SEPARATOR:
                MenuItem separator = MenuItem.createSeparator().addText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)))).append(' ')), 0, 0);
                String sublabel = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)));
                if (!StringUtils.isEmpty(sublabel)) {
                    separator.addText(sublabel, 0, TEXT_FORMAT_SECONDARY);
                }
                screen.addItem(separator);
                return pos;
            case ITEM_CHECKBOX:
                String checkLabel = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)));
                screen.addItem(MenuItem.createCheckbox(checkLabel, Storage.state().getBool(Storage.state().getInt(pos++))));
                return pos;
            case ITEM_DROPDOWN:
                String choiceLabel = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)));
                Vector choices = Utils.splitByNull(Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++))));
                screen.addItem(new MenuItem(MenuItem.TYPE_DROPDOWN, choiceLabel).setChoices(choices, Storage.state().getInt(Storage.state().getInt(pos++)), choiceLabel));
                return pos;
            case ITEM_TEXT_SEPARATOR:
                screen.addItem(MenuItem.createSeparator().addText(Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++))), 1, 0));
                return pos;
            case ITEM_TEXT_INPUT:
                if (screenId == ScreenId.CHAT_ROOM_CONFIG) {
                    int dataKey = Storage.state().getInt(pos++);
                    itemData = (dataKey < INT_KEY_RANGE_START_2 || dataKey > INT_KEY_RANGE_END_2) ? (dataKey < INT_KEY_RANGE_START || dataKey > INT_KEY_RANGE_END) ? Storage.state().getString(dataKey) : ObjectPool.integerOf(dataKey) : ObjectPool.integerOf(dataKey);
                } else {
                    itemData = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)));
                }
                int inputType = Storage.state().getInt(pos++);
                String hintText = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)));
                int validationType = Storage.state().getInt(pos++);
                if (validationType == 2) {
                    pos += 3;
                    int numValue = Storage.state().getInt(Storage.state().getInt(pos++));
                    title = numValue >= 0 ? StringUtils.intern(Integer.toString(numValue)) : Storage.emptyStr;
                } else {
                    title = Utils.defaultStr(Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++))));
                }
                screen.addItem(new MenuItem(MenuItem.TYPE_TEXT_INPUT, itemData instanceof String ? (String) itemData : Storage.emptyStr).setAction(itemData, title, ObjectPool.integerOf(inputType), ObjectPool.integerOf(validationType), hintText));
                return pos;
            case ITEM_LABEL_SEPARATOR:
                screen.addItem(MenuItem.createSeparator().setLabel(Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos++)))));
                return pos;
            case ITEM_CONDITIONAL_IF:
                return addItemToScreen(Storage.state().getBool(Storage.state().getInt(pos)), screen, pos + 1, isEnabled);
            case ITEM_CONDITIONAL_UNLESS:
                return addItemToScreen(!Storage.state().getBool(Storage.state().getInt(pos)), screen, pos + 1, isEnabled);
            case ITEM_LOGIN:
                String loginLabel = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos)));
                String loginValue = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos + 1)));
                MenuItem loginItem = new MenuItem(MenuItem.TYPE_LOGIN, (String) null).clear().setIcon(221).addText(Utils.nonEmpty(loginValue) ? loginValue : loginLabel, 1, 7);
                loginItem.data = new String[]{loginLabel, loginValue};
                screen.addItem(loginItem);
                return pos + 2;
            case ITEM_PASSWORD:
                String passwordStr = Utils.defaultStr(Storage.state().getString(Storage.state().getInt(pos)));
                MenuItem menuItem = new MenuItem(MenuItem.TYPE_PASSWORD, (String) null);
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
                screen.addItem(MenuItem.createGraphics(new GraphicsContext((Image) Storage.state().getObject(Storage.state().getInt(pos)))));
                return pos + 1;
            default:
                RemoteLogger.log("SCR", "parseItem DEFAULT type=" + (typeFlags & MASK_TYPE) + " recurse to " + Storage.state().getInt(pos));
                parseScreenItem(screen, Storage.state().getInt(pos), screenId);
                return pos + 1;
        }
    }

    private static final int processFormField(int pos, Object data) {
        int nextIdx;
        int idx = pos + 1;
        switch (Storage.state().getInt(pos)) {
            case ITEM_SEPARATOR:
                idx += 2;
                break;
            case ITEM_CHECKBOX:
                Storage.state().setBool(Storage.state().getInt(idx + 1), ((Boolean) data).booleanValue());
                idx += 2;
                break;
            case ITEM_DROPDOWN:
                Storage.state().setIntIndirect(idx + 2, ((Integer) ((Object[]) data)[0]).intValue());
                idx += 3;
                break;
            case ITEM_TEXT_SEPARATOR:
                idx++;
                break;
            case ITEM_TEXT_INPUT:
                int baseIdx = idx + 3;
                String value = (String) ((Object[]) data)[0];
                int curIdx = baseIdx + 1;
                if (Storage.state().getInt(baseIdx) == 2) {
                    int minIdx = curIdx + 1;
                    int minValue = Storage.state().getInt(curIdx);
                    int maxIdx = minIdx + 1;
                    int maxValue = Storage.state().getInt(minIdx);
                    int defIdx = maxIdx + 1;
                    int parsedValue = Utils.parseIntBounded(value, minValue, maxValue, Storage.state().getInt(maxIdx));
                    nextIdx = defIdx + 1;
                    Storage.state().setIntIndirect(defIdx, parsedValue);
                } else {
                    nextIdx = curIdx + 1;
                    Storage.state().setStringIndirect(curIdx, value);
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
                Storage.state().setStringIndirect(idx + 1, ((String[]) data)[1]);
                idx += 2;
                break;
            case ITEM_PASSWORD:
                Storage.state().setStringIndirect(idx, (String) data);
                idx++;
                break;
            case ITEM_IMAGE:
                idx++;
                break;
            case ITEM_REDIRECT:
                processFormField(Storage.state().getInt(idx), data);
                idx++;
                break;
        }
        return idx;
    }

    public static final int processScreenForm() {
        ListView screen = getCurrentScreen();
        int countPos = screen.definitionOffset + 9;
        Vector items = screen.menuItems;
        int fieldIdx = countPos + 1;
        int fieldCount = Storage.state().getInt(countPos);
        for (int idx = 0; idx < fieldCount; idx++) {
            fieldIdx = processFormField(fieldIdx, ((MenuItem) items.elementAt(idx)).data);
        }
        return 0;
    }

    public static final int processPhoneInput(String fieldId) {
        String newValue = Storage.state().getString(UIKeys.SLOT_STATUS_TEXT);
        int idx = 15;
        do {
            idx--;
            if (idx < 0) {
                return 0;
            }
        } while (Storage.resources().getBlockString(StringResKeys.PHONE_STRINGS_BASE, idx) != fieldId);
        Storage.state().setObject(StringResKeys.PHONE_STRINGS_BASE + idx, (Object) newValue);
        return 0;
    }

    public static final void setFormFields(String param1, String param2, String param3, String param4, String param5) {
        Storage.state().setObject(UIKeys.SLOT_LANGUAGE_OPTION, (Object) param5);
        Storage.state().setFromBuffer(UIKeys.SLOT_INIT_PARAMS, Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(ObjectPool.newStringBuffer(), 262572, param1), 262576, param2), 524724, param3), 590268, param4), 524741, param5));
        TimerManager.setTimer(TimerManager.SLOT_SCREEN_INIT, computeInitialState());
    }

    public static final String[] getLanguageOptions() {
        if (!Storage.state().getBool(SessionKeys.FLAG_CAPTCHA_SHOWN)) {
            setFormFields(null, null, null, null, null);
        } else if (TimerManager.checkTimer(TimerManager.SLOT_SCREEN_INIT, computeInitialState())) {
            Storage.state().clearIndex(UIKeys.SLOT_INIT_PARAMS);
        }
        String formData = Storage.state().getString(UIKeys.SLOT_INIT_PARAMS);
        if (formData == null) {
            return null;
        }
        String[] result = new String[2];
        result[0] = formData;
        String langOption = Storage.state().getString(UIKeys.SLOT_LANGUAGE_OPTION);
        result[1] = langOption != null ? langOption : Storage.resources().getString(StringResKeys.STR_DEFAULT_LANGUAGE);
        return result;
    }

    public static final void prepareFormData() {
        Storage.state().setInt(RuntimeKeys.INT_ERROR_MSG_INDEX, 0);
        Storage.state().clearIndex(RegistrationKeys.SLOT_REG_PARAM_4);
        Storage.state().setObject(RegistrationKeys.SLOT_REG_PARAM_3, ObjectPool.newVector());
    }

    public static final void clearFormFields() {
        Storage.state().clearRange(UIKeys.SLOT_INPUT_TEXT, UIKeys.RANGE_INPUT_TEXT_END);
    }

    public static final int validateServerAddress(String address) {
        Storage.state().setFromBuffer(UIKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(address));
        return 0;
    }

    public static final int processInputText(String label) {
        Storage.state().setBool(UIKeys.FLAG_SPECIAL_KEY_MODE, StringUtils.matchesKey(859, label));
        return 0;
    }

    public static final int handleFormSubmit(Object listItem) {
        MapController.mapContextItem = (ListItem) listItem;
        return 0;
    }

    private static final int computeInitialState() {
        return Storage.resources().getBytes(StringResKeys.RES_UPDATE_DATA) != null ? CACHE_TIMEOUT_SHORT_MS : CACHE_TIMEOUT_LONG_MS;
    }

    public static final int getThemeColor(int errorId) {
        for (int idx = Storage.state().getVector(SessionKeys.VEC_ACCOUNTS).size() - 1; idx >= 0; idx--) {
            AccountManager.getAccountByIndex(idx).onError(errorId);
        }
        return 0;
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
                Storage.state().setInt(SettingsKeys.SETTING_CUSTOM_VIEW_MODE, 1);
                break;
            case 3:
                Storage.state().setInt(SettingsKeys.SETTING_CUSTOM_VIEW_MODE, 0);
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

    public static final int computeLayoutParam(int propKey) {
        return Integer.parseInt(StringUtils.getSystemProp(propKey), 16);
    }

    public static final int handleThemeOption(int optionId) {
        if (optionId == 10) {
            return ScreenManager.getIconOffset();
        }
        return 0;
    }

    public static final int handleSoundOption(int statusIndex) {
        Storage.state().setFromBuffer(UIKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(Storage.state().getString(statusIndex + (Storage.state().getCurrentContact() instanceof MmpContact ? 1141 : Storage.state().getCurrentContact() instanceof XmppContact ? 1184 : 1063))));
        return ScreenId.STATUS_INPUT;
    }

    public static final int getIconOffset() {
        return Storage.state().getBool(SettingsKeys.SETTING_FAST_CONNECTION) ? 10 : 55;
    }
}
