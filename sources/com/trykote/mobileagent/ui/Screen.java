package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Image;
import java.util.Vector;

/**
 * A screen definition that IS a ListView.
 * Knows its handler, can show itself, provides typed item helpers.
 * Tracks form bindings for processForm() writeback.
 */
public class Screen extends ListView {

    // Form binding types (packed into formBindings array)
    private static final int BIND_BOOL = 1;    // [1, itemIdx, stateKey]
    private static final int BIND_INT = 2;     // [2, itemIdx, indexKey]
    private static final int BIND_STRING = 3;  // [3, itemIdx, valueKey]
    private static final int BIND_NUMERIC = 4; // [4, itemIdx, stateKey, min, max, default]

    private static final int BINDINGS_INITIAL_CAPACITY = 24;

    private int[] formBindings;
    private int formBindingsSize;

    public Screen(int type, int screenId) {
        super(layoutFor(type), screenId, widthFor(type), heightFor(type), scrollableFor(type));
        this.screenType = type;
    }

    // --- Form binding helpers ---

    private void ensureBindingCapacity(int needed) {
        if (formBindings == null) {
            formBindings = new int[Math.max(BINDINGS_INITIAL_CAPACITY, needed)];
        } else if (formBindingsSize + needed > formBindings.length) {
            int[] larger = new int[Math.max(formBindings.length * 2, formBindingsSize + needed)];
            System.arraycopy(formBindings, 0, larger, 0, formBindingsSize);
            formBindings = larger;
        }
    }

    private void addBinding(int type, int menuItemIndex, int targetKey) {
        ensureBindingCapacity(3);
        formBindings[formBindingsSize++] = type;
        formBindings[formBindingsSize++] = menuItemIndex;
        formBindings[formBindingsSize++] = targetKey;
    }

    private void addNumericBinding(int menuItemIndex, int stateKey, int min, int max, int def) {
        ensureBindingCapacity(6);
        formBindings[formBindingsSize++] = BIND_NUMERIC;
        formBindings[formBindingsSize++] = menuItemIndex;
        formBindings[formBindingsSize++] = stateKey;
        formBindings[formBindingsSize++] = min;
        formBindings[formBindingsSize++] = max;
        formBindings[formBindingsSize++] = def;
    }

    // --- Display ---

    public void show() {
        ScreenManager.showScreen(this);
    }

    // --- Header (behavior depends on screen type) ---

    public void configureHeader(int headerMode, String title) {
        String t = Utils.defaultStr(title);
        if (isOverlayType()) {
            return;
        }
        if (screenType == ScreenManager.TYPE_TOAST) {
            addItem(new MenuItem(0, t).addText(t, 1, 0));
        } else {
            setHeader(headerMode, t);
        }
    }

    // --- Soft keys ---

    public void configureSoftKeys(String leftLabel, int leftCmd,
                                  String rightLabel, int rightCmd, int extraCmd) {
        setSoftKeys(leftLabel, rightLabel, leftCmd, rightCmd, extraCmd);
    }

    // --- Conditional items ---

    public void addConditionalIf(int condKey, String label, int icon, int width) {
        if (AppState.getBool(condKey)) {
            addAction(icon, label, width);
        }
    }

    public void addConditionalUnless(int condKey, String label, int icon, int width) {
        if (!AppState.getBool(condKey)) {
            addAction(icon, label, width);
        }
    }

    // --- Item helpers for generated screen code ---

    public void addSeparator(String label, String sublabel) {
        String l = Utils.defaultStr(label);
        MenuItem sep = MenuItem.createSeparator().addText(l + " ", 0, 0);
        String sub = Utils.defaultStr(sublabel);
        if (sub.length() > 0) {
            sep.addText(sub, 0, 6);
        }
        addItem(sep);
    }

    public void addTextSeparator(String label) {
        addItem(MenuItem.createSeparator()
            .addText(Utils.defaultStr(label), 1, 0));
    }

    public void addLabelSeparator(String label) {
        addItem(MenuItem.createSeparator()
            .setLabel(Utils.defaultStr(label)));
    }

    public void addCheckbox(String label, int stateKey) {
        addItem(MenuItem.createCheckbox(Utils.defaultStr(label),
            AppState.getBool(stateKey)));
        addBinding(BIND_BOOL, menuItems.size() - 1, stateKey);
    }

    public void addDropdown(String label, int choicesKey, int indexKey) {
        String l = Utils.defaultStr(label);
        Vector choices = Utils.splitByNull(
            Utils.defaultStr(AppState.getString(choicesKey)));
        addItem(new MenuItem(MenuItem.TYPE_DROPDOWN, l)
            .setChoices(choices, AppState.getInt(indexKey), l));
        addBinding(BIND_INT, menuItems.size() - 1, indexKey);
    }

    public void addTextInput(String data, int inputType, String hint,
                             int validation, int valueKey) {
        String d = Utils.defaultStr(data);
        String h = Utils.defaultStr(hint);
        String value = Utils.defaultStr(AppState.getString(valueKey));
        addItem(new MenuItem(MenuItem.TYPE_TEXT_INPUT, d)
            .setAction(d, value, ObjectPool.integerOf(inputType),
                       ObjectPool.integerOf(validation), h));
        addBinding(BIND_STRING, menuItems.size() - 1, valueKey);
    }

    // --- Additional item helpers ---

    public void addNumericInput(String data, int inputType, String hint,
                                int stateKey, int min, int max, int def) {
        String d = Utils.defaultStr(data);
        String h = Utils.defaultStr(hint);
        int currentValue = AppState.getInt(stateKey);
        String value = currentValue != def ? String.valueOf(currentValue) : String.valueOf(def);
        addItem(new MenuItem(MenuItem.TYPE_TEXT_INPUT, d)
            .setAction(d, value, ObjectPool.integerOf(inputType),
                       ObjectPool.integerOf(2), h));
        addNumericBinding(menuItems.size() - 1, stateKey, min, max, def);
    }

    public void addLogin(String label, int valueKey) {
        String l = Utils.defaultStr(label);
        String displayName = Utils.defaultStr(
            SessionState.getAccountDisplayName());
        String username = Utils.defaultStr(AppState.getString(valueKey));
        MenuItem item = new MenuItem(MenuItem.TYPE_LOGIN, l)
            .setIcon(MenuItem.ICON_LOGIN);
        if (displayName.length() > 0) {
            item.addText(displayName, 1, 0);
        } else {
            item.setDefaultFont();
        }
        item.data = new String[]{displayName, username};
        addItem(item);
        addBinding(BIND_STRING, menuItems.size() - 1, valueKey);
    }

    public void addPassword(int valueKey) {
        String password = Utils.defaultStr(AppState.getString(valueKey));
        MenuItem item = new MenuItem(MenuItem.TYPE_PASSWORD, AppState.emptyStr)
            .setIcon(MenuItem.ICON_PASSWORD);
        if (password.length() > 0) {
            item.addText(Utils.maskPassword(password), 1, 7);
        } else {
            item.setDefaultFont();
        }
        item.data = password;
        addItem(item);
        addBinding(BIND_STRING, menuItems.size() - 1, valueKey);
    }

    public void addImage(int poolIndex) {
        Image img = AppState.getImage(poolIndex);
        if (img != null) {
            addItem(MenuItem.createGraphics(new GraphicsContext(img)));
        }
    }

    // --- Form processing ---

    public void processForm() {
        if (formBindings == null || formBindingsSize == 0) {
            return;
        }
        int pos = 0;
        while (pos < formBindingsSize) {
            int type = formBindings[pos++];
            int itemIdx = formBindings[pos++];
            MenuItem item = (MenuItem) menuItems.elementAt(itemIdx);
            switch (type) {
                case BIND_BOOL:
                    AppState.setBool(formBindings[pos++],
                        ((Boolean) item.data).booleanValue());
                    break;
                case BIND_INT:
                    AppState.setInt(formBindings[pos++],
                        ((Integer) ((Object[]) item.data)[0]).intValue());
                    break;
                case BIND_STRING: {
                    int key = formBindings[pos++];
                    String val = null;
                    if (item.data instanceof String[]) {
                        val = ((String[]) item.data)[1];
                    } else if (item.data instanceof String) {
                        val = (String) item.data;
                    } else {
                        val = (String) ((Object[]) item.data)[0];
                    }
                    AppState.setString(key, val);
                    break;
                }
                case BIND_NUMERIC: {
                    int stateKey = formBindings[pos++];
                    int min = formBindings[pos++];
                    int max = formBindings[pos++];
                    int def = formBindings[pos++];
                    String text = (String) ((Object[]) item.data)[0];
                    AppState.setInt(stateKey,
                        Utils.parseIntBounded(text, min, max, def));
                    break;
                }
            }
        }
    }

    // --- Type → layout mapping ---

    private static int layoutFor(int type) {
        if (type == ScreenManager.TYPE_MAP || type == ScreenManager.TYPE_MAP_ALT) {
            return LAYOUT_GRID;
        }
        return LAYOUT_VERTICAL;
    }

    private static int widthFor(int type) {
        int w = UIState.getScreenWidth();
        if (isReducedSize(type)) {
            return (w * 9) / 10;
        }
        return w;
    }

    private static int heightFor(int type) {
        int h = UIState.getHeight();
        if (isReducedSize(type)) {
            return (h * 9) / 10;
        }
        return h;
    }

    private static boolean scrollableFor(int type) {
        return type != ScreenManager.TYPE_FULLSCREEN_NOSCROLL
            && type != ScreenManager.TYPE_FULLSCREEN_NOSCROLL_ALT
            && type != ScreenManager.TYPE_TOAST
            && type != ScreenManager.TYPE_TOAST_CENTER;
    }

    private static boolean isReducedSize(int type) {
        return type == ScreenManager.TYPE_DIALOG_CENTER
            || type == ScreenManager.TYPE_DIALOG_BOTTOM
            || type == ScreenManager.TYPE_DIALOG_CORNER
            || type == ScreenManager.TYPE_DIALOG_LOW
            || type == ScreenManager.TYPE_POPUP
            || type == ScreenManager.TYPE_TOAST
            || type == ScreenManager.TYPE_TOAST_CENTER;
    }

    private boolean isOverlayType() {
        return screenType == ScreenManager.TYPE_DIALOG_CENTER
            || screenType == ScreenManager.TYPE_DIALOG_BOTTOM
            || screenType == ScreenManager.TYPE_DIALOG_CORNER
            || screenType == ScreenManager.TYPE_DIALOG_LOW
            || screenType == ScreenManager.TYPE_POPUP
            || screenType == ScreenManager.TYPE_TOAST_CENTER;
    }
}
