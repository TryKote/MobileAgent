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

/* renamed from: c */
/* loaded from: MobileAgent_3.9.jar:c.class */
public final class MenuItem {

    /* renamed from: a */
    public final int id;

    /* renamed from: b */
    public String title;

    /* renamed from: c */
    public int width;

    /* renamed from: g */
    private int totalWidth;

    /* renamed from: h */
    private int maxHeight;

    /* renamed from: i */
    private Vector elements;

    /* renamed from: j */
    private int[] positions;

    /* renamed from: d */
    public Object data;

    /* renamed from: e */
    public boolean enabled;

    /* renamed from: f */
    public boolean visible;

    /* renamed from: k */
    private int wrapWidth;

    public MenuItem(int i, String str) {
        this.id = i;
        this.elements = ObjectPool.newVector();
        this.positions = new int[16];
        this.title = str;
        this.width = 200;
    }

    private MenuItem(String str, int i) {
        this(1, str);
        this.width = i;
    }

    /* renamed from: a */
    public final MenuItem clear() {
        this.elements.removeAllElements();
        this.positions[0] = 0;
        this.totalWidth = 0;
        this.maxHeight = 0;
        return this;
    }

    /* renamed from: b */
    public final boolean isEnabled() {
        return this.id != 0;
    }

    /* renamed from: c */
    public static final MenuItem createDefault() {
        return new MenuItem(1, AppState.getString(StringResKeys.STR_EMPTY));
    }

    /* renamed from: a */
    public static final MenuItem create(String str) {
        return new MenuItem(1, str);
    }

    /* renamed from: a */
    public static final MenuItem createWithWidth(String str, int i) {
        return new MenuItem(str, i);
    }

    /* renamed from: d */
    public static final MenuItem createSeparator() {
        return new MenuItem(0, AppState.emptyStr);
    }

    /* renamed from: a */
    public static final MenuItem createCheckbox(String str, boolean z) {
        MenuItem item = new MenuItem(2, str).setIconAndLabel(z ? 25 : 24, str);
        item.data = ResourceManager.booleanOf(z);
        return item;
    }

    /* renamed from: a */
    public final MenuItem setAction(Object obj, String str, Object obj2, Object obj3, Object obj4) {
        String str2 = Utils.nonEmpty(str) ? str : null;
        if (obj instanceof String) {
            setLabel(Utils.appendSpace(this.title));
        } else {
            setIcon(((Integer) obj).intValue());
        }
        if (str2 != null) {
            addText(((Integer) obj3).intValue() != 327680 ? str2 : Utils.maskPassword(str2), 1, 7);
        } else {
            setDefaultFont();
        }
        this.data = new Object[]{str, obj2, obj3, obj4, obj};
        return this;
    }

    /* renamed from: a */
    public final MenuItem setChoices(Vector vector, int i, String str) {
        int size = vector.size();
        int i2 = size;
        String[] strArr = new String[size];
        while (true) {
            i2--;
            if (i2 < 0) {
                ObjectPool.releaseVector(vector);
                MenuItem menuItem = clear().setLabel(Utils.appendSpace(str)).addText(strArr[i], 1, 7).setIcon(247);
                menuItem.data = new Object[]{ResourceManager.integerOf(i), strArr};
                return menuItem;
            }
            strArr[i2] = (String) vector.elementAt(i2);
        }
    }

    /* renamed from: a */
    public static final MenuItem createGraphics(GraphicsContext gfx) {
        MenuItem graphicsItem = new MenuItem(11, AppState.emptyStr);
        graphicsItem.addElement(new ImageElement(gfx.image));
        return graphicsItem;
    }

    /* renamed from: a */
    public final int execute(ListView screen) {
        if (this.id == 2) {
            if (this.data != null) {
                Boolean checked = ResourceManager.booleanOf(!((Boolean) this.data).booleanValue());
                this.data = checked;
                this.elements.setElementAt(createIconData(checked.booleanValue() ? 25 : 24), 0);
            }
            EventDispatcher.postEvent(new MenuItemEvent(this));
            return 0;
        }
        if (this.id == 15) {
            new TextInputHandler(screen, this);
            return 0;
        }
        if (this.id != 9) {
            if (this.id == 4) {
                NotificationHelper.showMessageById(Utils.defaultStr(AppState.getString(SessionKeys.SLOT_ACCOUNT_DISPLAY_NAME)).length() > 0 ? 427 : 428);
                return 0;
            }
            if (this.id != 5) {
                return -1;
            }
            NotificationHelper.showMessageById(429);
            return 0;
        }
        ListView choiceScreen = ScreenManager.createScreen(ScreenDef.CHOICE_DIALOG);
        Object[] objArr = (Object[]) this.data;
        String[] strArr = (String[]) objArr[1];
        int iIntValue = ((Integer) objArr[0]).intValue();
        Object[] objArr2 = {objArr, this, screen};
        for (String str : strArr) {
            MenuItem choiceItem = new MenuItem(13, str).setLabel(str);
            choiceItem.data = objArr2;
            choiceScreen.addItem(choiceItem);
        }
        choiceScreen.selectByTitle(strArr[iIntValue]);
        ScreenManager.showScreen(choiceScreen);
        return 0;
    }

    /* renamed from: e */
    public final MenuItem setDefaultFont() {
        return addElement(new SpacerElement(16, AppState.getInt(UIKeys.INT_FONT_HEIGHT)));
    }

    /* renamed from: a */
    public final MenuItem setIcon(int i) {
        return i >= 0 ? addElement(createIconData(i)) : this;
    }

    /* renamed from: c */
    private static IconElement createIconData(int i) {
        return new IconElement(GraphicsContext.getIconSize(i), i);
    }

    /* renamed from: b */
    public final MenuItem setLabel(String str) {
        return setLabelInternal(-1, str, 0, 0);
    }

    /* renamed from: a */
    public final MenuItem setIconAndLabel(int i, String str) {
        return setLabelInternal(i, str, 0, 0);
    }

    /* renamed from: a */
    public final MenuItem setLabelInternal(int i, String str, int i2, int i3) {
        if (i >= 0) {
            setIcon(i);
        }
        return addText(str, i2, i3);
    }

    /* renamed from: a */
    public final MenuItem addText(String str, int i, int i2) {
        return addTextInternal(str, i, i2, -1);
    }

    /* renamed from: a */
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

    /* renamed from: a */
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

    /* renamed from: a */
    public final MenuItem setLayout(int i, int i2) {
        if (i > 1) {
            this.wrapWidth = i2;
        }
        return this;
    }

    /* renamed from: b */
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
                    if (next instanceof IconElement && ((IconElement) next).iconCode == 244) {
                        i5 = 16;
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

    /* renamed from: f */
    public final int getMaxHeight() {
        int lineH = 0;
        Vector vector = this.elements;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return lineH + 4;
            }
            lineH = Utils.max(lineH, this.positions[(size << 1) + 1] + ((RenderElement) vector.elementAt(size)).getWidth());
        }
    }

    /* renamed from: g */
    public final int getTotalWidth() {
        return this.wrapWidth != 0 ? this.wrapWidth : this.totalWidth + 4;
    }

    /* renamed from: h */
    public final int getTotalHeight() {
        return Utils.max(this.maxHeight, AppState.getInt(UIKeys.INT_FONT_HEIGHT)) + 4;
    }

    /* renamed from: a */
    public final void render(GraphicsContext gfx, int i, int i2, int i3) {
        Vector vector = this.elements;
        int size = vector.size();
        int baseX = i + 2;
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            RenderElement elem = (RenderElement) vector.elementAt(size);
            if (!(elem instanceof LineBreak)) {
                int elemX = baseX + this.positions[(size << 1) + 1];
                int elemY = i2 + 2 + this.positions[(size << 1) + 1 + 1];
                elem.render(gfx, elemX, elemY, baseX, i3);
            }
        }
    }
}
