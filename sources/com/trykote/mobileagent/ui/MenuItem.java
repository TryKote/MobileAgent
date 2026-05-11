package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.core.event.MenuItemEvent;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public final class MenuItem {

    // Item type IDs
    private static final int TYPE_CHECKBOX = 2;
    static final int TYPE_LOGIN = 4;
    static final int TYPE_PASSWORD = 5;
    static final int TYPE_DROPDOWN = 9;
    static final int TYPE_GRAPHICS = 11;
    static final int TYPE_EXPANDABLE = 13;
    static final int TYPE_TEXT_INPUT = 15;

    // Icon codes
    private static final int ICON_UNCHECKED = 24;
    private static final int ICON_CHECKED = 25;
    static final int ICON_LOGIN = 221;
    static final int ICON_PASSWORD = 219;
    static final int ICON_ALIGN_RIGHT = 244;
    static final int ICON_DROPDOWN = 247;

    // Input type for password masking
    static final int PASSWORD_INPUT_TYPE = 327680;

    // Object pool string resource indices (validation messages)
    private static final int MSG_LOGIN_HAS_NAME = 427;
    private static final int MSG_LOGIN_NO_NAME = 428;
    private static final int MSG_PASSWORD_HINT = 429;

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

    public MenuItem(int i, String str) {
        this.id = i;
        this.elements = ObjectPool.newVector();
        this.positions = new int[INITIAL_POSITIONS_CAPACITY];
        this.title = str;
        this.width = DEFAULT_WIDTH;
    }

    private MenuItem(String str, int i) {
        this(1, str);
        this.width = i;
    }

    public final MenuItem clear() {
        this.elements.removeAllElements();
        this.positions[0] = 0;
        this.totalWidth = 0;
        this.maxHeight = 0;
        return this;
    }

    public final boolean isEnabled() {
        return this.id != 0;
    }

    public static final MenuItem createDefault() {
        return new MenuItem(1, ResourceAccessor.str(StringResKeys.STR_EMPTY));
    }

    public static final MenuItem create(String str) {
        return new MenuItem(1, str);
    }

    public static final MenuItem createWithWidth(String str, int i) {
        return new MenuItem(str, i);
    }

    public static final MenuItem createSeparator() {
        return new MenuItem(0, AppState.emptyStr);
    }

    public static final MenuItem createCheckbox(String str, boolean z) {
        MenuItem item = new MenuItem(TYPE_CHECKBOX, str).setIconAndLabel(z ? ICON_CHECKED : ICON_UNCHECKED, str);
        item.data = ObjectPool.booleanOf(z);
        return item;
    }

    public final MenuItem setAction(Object obj, String str, Object obj2, Object obj3, Object obj4) {
        String str2 = Utils.nonEmpty(str) ? str : null;
        if (obj instanceof String) {
            setLabel(Utils.appendSpace(this.title));
        } else {
            setIcon(((Integer) obj).intValue());
        }
        if (str2 != null) {
            addText(((Integer) obj3).intValue() != PASSWORD_INPUT_TYPE ? str2 : Utils.maskPassword(str2), 1, 7);
        } else {
            setDefaultFont();
        }
        this.data = new Object[]{str, obj2, obj3, obj4, obj};
        return this;
    }

    public final MenuItem setChoices(Vector vector, int i, String str) {
        int size = vector.size();
        String[] strArr = new String[size];
        for (int idx = size - 1; idx >= 0; idx--) {
            strArr[idx] = (String) vector.elementAt(idx);
        }
        ObjectPool.releaseVector(vector);
        MenuItem menuItem = clear().setLabel(Utils.appendSpace(str)).addText(strArr[i], 1, 7).setIcon(ICON_DROPDOWN);
        menuItem.data = new Object[]{ObjectPool.integerOf(i), strArr};
        return menuItem;
    }

    public static final MenuItem createGraphics(GraphicsContext gfx) {
        MenuItem graphicsItem = new MenuItem(TYPE_GRAPHICS, AppState.emptyStr);
        graphicsItem.addElement(new ImageElement(gfx.image));
        return graphicsItem;
    }

    public final int execute(ListView screen) {
        if (this.id == TYPE_CHECKBOX) {
            if (this.data != null) {
                Boolean checked = ObjectPool.booleanOf(!((Boolean) this.data).booleanValue());
                this.data = checked;
                this.elements.setElementAt(createIconData(checked.booleanValue() ? ICON_CHECKED : ICON_UNCHECKED), 0);
            }
            EventDispatcher.postEvent(new MenuItemEvent(this));
            return 0;
        }
        if (this.id == TYPE_TEXT_INPUT) {
            new TextInputHandler(screen, this);
            return 0;
        }
        if (this.id != TYPE_DROPDOWN) {
            if (this.id == TYPE_LOGIN) {
                NotificationHelper.showMessageById(Utils.defaultStr(SessionState.getAccountDisplayName()).length() > 0 ? MSG_LOGIN_HAS_NAME : MSG_LOGIN_NO_NAME);
                return 0;
            }
            if (this.id != TYPE_PASSWORD) {
                return -1;
            }
            NotificationHelper.showMessageById(MSG_PASSWORD_HINT);
            return 0;
        }
        Screen choiceScreen = Screens.choiceDialog(null);
        Object[] objArr = (Object[]) this.data;
        String[] strArr = (String[]) objArr[1];
        int iIntValue = ((Integer) objArr[0]).intValue();
        Object[] objArr2 = {objArr, this, screen};
        for (String str : strArr) {
            MenuItem choiceItem = new MenuItem(TYPE_EXPANDABLE, str).setLabel(str);
            choiceItem.data = objArr2;
            choiceScreen.addItem(choiceItem);
        }
        choiceScreen.selectByTitle(strArr[iIntValue]);
        ScreenManager.showScreen(choiceScreen);
        return 0;
    }

    public final MenuItem setDefaultFont() {
        return addElement(new SpacerElement(SPACER_SIZE, UIState.getFontHeight()));
    }

    public final MenuItem setIcon(int i) {
        return i >= 0 ? addElement(createIconData(i)) : this;
    }

    private static IconElement createIconData(int i) {
        return new IconElement(GraphicsContext.getIconSize(i), i);
    }

    public final MenuItem setLabel(String str) {
        return setLabelInternal(-1, str, 0, 0);
    }

    public final MenuItem setIconAndLabel(int i, String str) {
        return setLabelInternal(i, str, 0, 0);
    }

    public final MenuItem setLabelInternal(int i, String str, int i2, int i3) {
        if (i >= 0) {
            setIcon(i);
        }
        return addText(str, i2, i3);
    }

    public final MenuItem addText(String str, int i, int i2) {
        return addTextInternal(str, i, i2, -1);
    }

    public final MenuItem addTextInternal(String str, int i, int i2, int i3) {
        if (str != null) {
            Vector parts = EmoticonReplacer.wrapText(ObjectPool.newVector(), str, 0, str.length(), i, i2, i3);
            int size = parts.size();
            for (int i4 = 0; i4 < size; i4++) {
                addElement((RenderElement) parts.elementAt(i4));
            }
            ObjectPool.releaseVector(parts);
        }
        return this;
    }

    private MenuItem addElement(RenderElement elem) {
        this.elements.addElement(elem);
        this.positions = BitMath.resizeArray(this.positions, this.totalWidth, 0);
        this.totalWidth += elem.getWidth();
        int i = this.maxHeight;
        int elemH = elem.getHeight();
        if (i < elemH) {
            this.maxHeight = elemH;
        }
        return this;
    }

    public final MenuItem setLayout(int i, int i2) {
        if (i > 1) {
            this.wrapWidth = i2;
        }
        return this;
    }

    public final MenuItem layout(int i) {
        this.positions[0] = 0;
        Vector vector = this.elements;
        int size = vector.size();
        int i2 = 0;
        int i3 = 0;
        int lineH = 0;
        this.maxHeight = 0;
        for (int i4 = 0; i4 < size; i4++) {
            RenderElement elem = (RenderElement) vector.elementAt(i4);
            if (!(elem instanceof LineBreak)) {
                int elemW = elem.getWidth();
                int elemH = elem.getHeight();
                int i5 = 0;
                if (i4 == size - 2) {
                    Object next = vector.elementAt(i4 + 1);
                    if (next instanceof IconElement && ((IconElement) next).iconCode == ICON_ALIGN_RIGHT) {
                        i5 = SPACER_SIZE;
                    }
                }
                if (this.wrapWidth == 0 && i2 + elemW + i5 > i) {
                    i2 = 0;
                    i3 += lineH;
                    this.maxHeight += lineH;
                    lineH = 0;
                }
                this.positions = BitMath.resizeArray(this.positions, i2, i3);
                lineH = Utils.max(lineH, elemH);
                i2 += elemW;
            } else {
                i2 = 0;
                i3 += lineH;
                this.maxHeight += lineH;
                lineH = 0;
                this.positions = BitMath.resizeArray(this.positions, 0, 0);
            }
        }
        this.maxHeight += lineH;
        return this;
    }

    public final int getMaxHeight() {
        int lineH = 0;
        Vector vector = this.elements;
        for (int idx = vector.size() - 1; idx >= 0; idx--) {
            lineH = Utils.max(lineH, this.positions[(idx << 1) + 1] + ((RenderElement) vector.elementAt(idx)).getWidth());
        }
        return lineH + PADDING;
    }

    public final int getTotalWidth() {
        return this.wrapWidth != 0 ? this.wrapWidth : this.totalWidth + PADDING;
    }

    public final int getTotalHeight() {
        return Utils.max(this.maxHeight, UIState.getFontHeight()) + PADDING;
    }

    public final void render(GraphicsContext gfx, int i, int i2, int i3) {
        Vector vector = this.elements;
        int baseX = i + MARGIN;
        for (int idx = vector.size() - 1; idx >= 0; idx--) {
            RenderElement elem = (RenderElement) vector.elementAt(idx);
            if (!(elem instanceof LineBreak)) {
                int elemX = baseX + this.positions[(idx << 1) + 1];
                int elemY = i2 + MARGIN + this.positions[(idx << 1) + 1 + 1];
                elem.render(gfx, elemX, elemY, baseX, i3);
            }
        }
    }
}
