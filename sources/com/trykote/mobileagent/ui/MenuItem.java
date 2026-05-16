package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.core.event.MenuItemEvent;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.util.BitMath;
import com.trykote.mobileagent.util.EmoticonReplacer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.Utils;

import java.util.Vector;

public final class MenuItem {

    // Item type IDs
    private static final int TYPE_SEPARATOR = 0;
    private static final int TYPE_REGULAR = 1;
    static final int TYPE_CHECKBOX = 2;
    static final int TYPE_LOGIN = 4;
    static final int TYPE_PASSWORD = 5;
    static final int TYPE_DROPDOWN = 9;
    static final int TYPE_GRAPHICS = 11;
    static final int TYPE_EXPANDABLE = 13;
    static final int TYPE_TEXT_INPUT = 15;

    // Execute result codes
    private static final int RESULT_HANDLED = 0;
    private static final int RESULT_NOT_HANDLED = -1;

    // Icon codes
    private static final int ICON_UNCHECKED = 24;
    private static final int ICON_CHECKED = 25;
    static final int ICON_LOGIN = 221;
    static final int ICON_PASSWORD = 219;
    static final int ICON_ALIGN_RIGHT = 244;
    static final int ICON_DROPDOWN = 247;

    // No icon sentinel
    static final int NO_ICON = -1;

    // Input constraints for password masking (MIDP TextField.PASSWORD | TextField.SENSITIVE)
    static final int PASSWORD_INPUT_TYPE = 327680;

    // Object pool string resource indices (validation messages)
    private static final int MSG_LOGIN_HAS_NAME = 427;
    private static final int MSG_LOGIN_NO_NAME = 428;
    private static final int MSG_PASSWORD_HINT = 429;

    // Text style for input fields
    private static final int COLOR_INPUT_TEXT = 7;

    // Layout constants
    static final int SPACER_SIZE = 16;
    private static final int DEFAULT_WIDTH = 200;
    private static final int PADDING = 4;
    private static final int MARGIN = 2;
    private static final int INITIAL_POSITIONS_CAPACITY = 16;

    public final int id;

    public String title;

    public int width;

    private int totalWidth;

    private int maxHeight;

    private Vector elements;

    private int[] positions;

    public Object data;

    public boolean enabled;

    public boolean visible;

    private int wrapWidth;

    public MenuItem(int id, String title) {
        this.id = id;
        this.elements = ObjectPool.newVector();
        this.positions = new int[INITIAL_POSITIONS_CAPACITY];
        this.title = title;
        this.width = DEFAULT_WIDTH;
    }

    private MenuItem(String title, int width) {
        this(TYPE_REGULAR, title);
        this.width = width;
    }

    public MenuItem clear() {
        this.elements.removeAllElements();
        this.positions[0] = 0;
        this.totalWidth = 0;
        this.maxHeight = 0;
        return this;
    }

    public boolean isEnabled() {
        return this.id != TYPE_SEPARATOR;
    }

    public static MenuItem createDefault() {
        return new MenuItem(TYPE_REGULAR, StringPool.get(StringResKeys.STR_EMPTY));
    }

    public static MenuItem create(String title) {
        return new MenuItem(TYPE_REGULAR, title);
    }

    public static MenuItem createWithWidth(String title, int width) {
        return new MenuItem(title, width);
    }

    public static MenuItem createSeparator() {
        return new MenuItem(TYPE_SEPARATOR, AppState.emptyStr);
    }

    public static MenuItem createCheckbox(String label, boolean checked) {
        MenuItem item = new MenuItem(TYPE_CHECKBOX, label)
            .setIconAndLabel(checked ? ICON_CHECKED : ICON_UNCHECKED, label);
        item.data = ObjectPool.booleanOf(checked);
        return item;
    }

    public MenuItem setAction(Object iconOrLabel, String value, Object maxLength,
                              Object constraints, Object hint) {
        String displayValue = Utils.nonEmpty(value) ? value : null;
        if (iconOrLabel instanceof String) {
            setLabel(Utils.appendSpace(this.title));
        } else {
            setIcon(((Integer) iconOrLabel).intValue());
        }
        if (displayValue != null) {
            int constraintFlags = ((Integer) constraints).intValue();
            addText(constraintFlags != PASSWORD_INPUT_TYPE
                    ? displayValue : Utils.maskPassword(displayValue),
                    UIKeys.GFX_INDEX_BOLD, COLOR_INPUT_TEXT);
        } else {
            setDefaultFont();
        }
        this.data = new Object[]{value, maxLength, constraints, hint, iconOrLabel};
        return this;
    }

    public MenuItem setChoices(Vector options, int selectedIndex, String label) {
        int count = options.size();
        String[] items = new String[count];
        for (int idx = count - 1; idx >= 0; idx--) {
            items[idx] = (String) options.elementAt(idx);
        }
        ObjectPool.releaseVector(options);
        MenuItem result = clear()
            .setLabel(Utils.appendSpace(label))
            .addText(items[selectedIndex], UIKeys.GFX_INDEX_BOLD, COLOR_INPUT_TEXT)
            .setIcon(ICON_DROPDOWN);
        result.data = new Object[]{ObjectPool.integerOf(selectedIndex), items};
        return result;
    }

    public static MenuItem createGraphics(GraphicsContext gfx) {
        MenuItem graphicsItem = new MenuItem(TYPE_GRAPHICS, AppState.emptyStr);
        graphicsItem.addElement(new ImageElement(gfx.image));
        return graphicsItem;
    }

    public int execute(ListView screen) {
        if (this.id == TYPE_CHECKBOX) {
            if (this.data != null) {
                boolean checked = !((Boolean) this.data).booleanValue();
                this.data = ObjectPool.booleanOf(checked);
                this.elements.setElementAt(
                    createIconData(checked ? ICON_CHECKED : ICON_UNCHECKED), 0);
            }
            EventDispatcher.postEvent(new MenuItemEvent(this));
            return RESULT_HANDLED;
        } else if (this.id == TYPE_TEXT_INPUT) {
            new TextInputHandler(screen, this);
            return RESULT_HANDLED;
        } else if (this.id == TYPE_LOGIN) {
            NotificationHelper.showMessageById(
                Utils.defaultStr(SessionState.getAccountDisplayName()).length() > 0
                    ? MSG_LOGIN_HAS_NAME : MSG_LOGIN_NO_NAME);
            return RESULT_HANDLED;
        } else if (this.id == TYPE_PASSWORD) {
            NotificationHelper.showMessageById(MSG_PASSWORD_HINT);
            return RESULT_HANDLED;
        } else if (this.id == TYPE_DROPDOWN) {
            Screen choiceScreen = Screens.choiceDialog();
            Object[] choiceData = (Object[]) this.data;
            String[] items = (String[]) choiceData[1];
            int selectedIndex = ((Integer) choiceData[0]).intValue();
            Object[] choiceContext = {choiceData, this, screen};
            for (int idx = 0; idx < items.length; idx++) {
                MenuItem choiceItem = new MenuItem(TYPE_EXPANDABLE, items[idx])
                    .setLabel(items[idx]);
                choiceItem.data = choiceContext;
                choiceScreen.addItem(choiceItem);
            }
            choiceScreen.selectByTitle(items[selectedIndex]);
            ScreenManager.showScreen(choiceScreen);
            return RESULT_HANDLED;
        }
        return RESULT_NOT_HANDLED;
    }

    public MenuItem setDefaultFont() {
        return addElement(new SpacerElement(SPACER_SIZE, UIState.getFontHeight()));
    }

    public MenuItem setIcon(int iconCode) {
        return iconCode >= 0 ? addElement(createIconData(iconCode)) : this;
    }

    private static IconElement createIconData(int iconCode) {
        return new IconElement(GraphicsContext.getIconSize(iconCode), iconCode);
    }

    public MenuItem setLabel(String text) {
        return setLabelInternal(NO_ICON, text, 0, 0);
    }

    public MenuItem setIconAndLabel(int iconCode, String text) {
        return setLabelInternal(iconCode, text, 0, 0);
    }

    public MenuItem setLabelInternal(int iconCode, String text, int gfxIndex, int colorIndex) {
        if (iconCode >= 0) {
            setIcon(iconCode);
        }
        return addText(text, gfxIndex, colorIndex);
    }

    public MenuItem addText(String text, int gfxIndex, int colorIndex) {
        return addTextInternal(text, gfxIndex, colorIndex, -1);
    }

    public MenuItem addTextInternal(String text, int gfxIndex, int colorIndex, int emoticonType) {
        if (text != null) {
            Vector parts = EmoticonReplacer.wrapText(
                ObjectPool.newVector(), text, 0, text.length(),
                gfxIndex, colorIndex, emoticonType);
            int count = parts.size();
            for (int idx = 0; idx < count; idx++) {
                addElement((RenderElement) parts.elementAt(idx));
            }
            ObjectPool.releaseVector(parts);
        }
        return this;
    }

    private MenuItem addElement(RenderElement elem) {
        this.elements.addElement(elem);
        this.positions = BitMath.resizeArray(this.positions, this.totalWidth, 0);
        this.totalWidth += elem.getWidth();
        int currentMax = this.maxHeight;
        int elemHeight = elem.getHeight();
        if (currentMax < elemHeight) {
            this.maxHeight = elemHeight;
        }
        return this;
    }

    public MenuItem setLayout(int columns, int columnWidth) {
        if (columns > 1) {
            this.wrapWidth = columnWidth;
        }
        return this;
    }

    public MenuItem layout(int availableWidth) {
        this.positions[0] = 0;
        Vector elems = this.elements;
        int count = elems.size();
        int lineX = 0;
        int lineY = 0;
        int lineHeight = 0;
        this.maxHeight = 0;
        for (int idx = 0; idx < count; idx++) {
            RenderElement elem = (RenderElement) elems.elementAt(idx);
            if (!(elem instanceof LineBreak)) {
                int elemWidth = elem.getWidth();
                int elemHeight = elem.getHeight();
                int extraSpace = 0;
                if (idx == count - 2) {
                    Object next = elems.elementAt(idx + 1);
                    if (next instanceof IconElement
                            && ((IconElement) next).iconCode == ICON_ALIGN_RIGHT) {
                        extraSpace = SPACER_SIZE;
                    }
                }
                if (this.wrapWidth == 0 && lineX + elemWidth + extraSpace > availableWidth) {
                    lineX = 0;
                    lineY += lineHeight;
                    this.maxHeight += lineHeight;
                    lineHeight = 0;
                }
                this.positions = BitMath.resizeArray(this.positions, lineX, lineY);
                lineHeight = Utils.max(lineHeight, elemHeight);
                lineX += elemWidth;
            } else {
                lineX = 0;
                lineY += lineHeight;
                this.maxHeight += lineHeight;
                lineHeight = 0;
                this.positions = BitMath.resizeArray(this.positions, 0, 0);
            }
        }
        this.maxHeight += lineHeight;
        return this;
    }

    public int getContentWidth() {
        int maxRight = 0;
        Vector elems = this.elements;
        for (int idx = elems.size() - 1; idx >= 0; idx--) {
            maxRight = Utils.max(maxRight,
                this.positions[(idx << 1) + 1]
                    + ((RenderElement) elems.elementAt(idx)).getWidth());
        }
        return maxRight + PADDING;
    }

    public int getTotalWidth() {
        return this.wrapWidth != 0 ? this.wrapWidth : this.totalWidth + PADDING;
    }

    public int getTotalHeight() {
        return Utils.max(this.maxHeight, UIState.getFontHeight()) + PADDING;
    }

    public void render(GraphicsContext gfx, int x, int y, int containerWidth) {
        Vector elems = this.elements;
        int baseX = x + MARGIN;
        for (int idx = elems.size() - 1; idx >= 0; idx--) {
            RenderElement elem = (RenderElement) elems.elementAt(idx);
            if (!(elem instanceof LineBreak)) {
                int elemX = baseX + this.positions[(idx << 1) + 1];
                int elemY = y + MARGIN + this.positions[(idx << 1) + 2];
                elem.render(gfx, elemX, elemY, baseX, containerWidth);
            }
        }
    }
}
