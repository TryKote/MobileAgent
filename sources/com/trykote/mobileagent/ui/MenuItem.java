package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.StateKeys;
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
        return new MenuItem(1, AppState.getString(StateKeys.STR_EMPTY));
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
        graphicsItem.data = gfx;
        graphicsItem.totalWidth = gfx.image.getWidth();
        graphicsItem.maxHeight = gfx.image.getHeight() + 5;
        return graphicsItem;
    }

    /* renamed from: a */
    public final int execute(Screen screen) {
        if (this.id == 2) {
            if (this.data != null) {
                Boolean checked = ResourceManager.booleanOf(!((Boolean) this.data).booleanValue());
                this.data = checked;
                this.elements.setElementAt(createIconData(checked.booleanValue() ? 25 : 24), 0);
            }
            IOUtils.postEvent(new MenuItemEvent(this));
            return 0;
        }
        if (this.id == 15) {
            new AsyncTask(screen, this);
            return 0;
        }
        if (this.id != 9) {
            if (this.id == 4) {
                NotificationHelper.showMessageById(Utils.defaultStr(AppState.getString(StateKeys.SLOT_ACCOUNT_DISPLAY_NAME)).length() > 0 ? 427 : 428);
                return 0;
            }
            if (this.id != 5) {
                return -1;
            }
            NotificationHelper.showMessageById(429);
            return 0;
        }
        Screen choiceScreen = ScreenManager.createScreen(2351);
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
        return addElement(new int[]{16, AppState.getInt(StateKeys.INT_FONT_HEIGHT)});
    }

    /* renamed from: a */
    public final MenuItem setIcon(int i) {
        return i >= 0 ? addElement(createIconData(i)) : this;
    }

    /* renamed from: c */
    private static Object createIconData(int i) {
        return new int[]{GraphicsContext.getIconSize(i), 16, i};
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
            Vector parts = wrapText(ObjectPool.newVector(), str, 0, str.length(), i, i2, i3);
            int size = parts.size();
            for (int i4 = 0; i4 < size; i4++) {
                addElement(parts.elementAt(i4));
            }
            ObjectPool.releaseVector(parts);
        }
        return this;
    }

    /* renamed from: a */
    private MenuItem addElement(Object obj) {
        this.elements.addElement(obj);
        this.positions = BitMath.resizeArray(this.positions, this.totalWidth, 0);
        this.totalWidth += getElementWidth(obj);
        int i = this.maxHeight;
        int elemH = getElementHeight(obj);
        if (i < elemH) {
            this.maxHeight = elemH;
        }
        return this;
    }

    /* renamed from: b */
    private static int getElementWidth(Object obj) {
        if (obj == AppState.pool[StateKeys.ARR_EMPTY_INT]) {
            return 0;
        }
        return obj instanceof int[] ? ((int[]) obj)[0] + 2 : ((int[]) ((Object[]) obj)[1])[0];
    }

    /* renamed from: c */
    private static int getElementHeight(Object obj) {
        if (obj == AppState.pool[StateKeys.ARR_EMPTY_INT]) {
            return 0;
        }
        return obj instanceof int[] ? ((int[]) obj)[1] : ((int[]) ((Object[]) obj)[1])[1];
    }

    /* renamed from: a */
    private final void wrapTextLine(Vector vector, String str, GraphicsContext gfx, int i, int i2, int i3, int i4, int i5) {
        int textW = gfx.substringWidth(str, i2, i3);
        if (textW < (AppState.getInt(StateKeys.INT_SCREEN_WIDTH) << 2) / 5) {
            vector.addElement(new Object[]{str, new int[]{textW, i, i2, i3, i4, i5}});
            return;
        }
        int i6 = 0;
        while (true) {
            if (i6 >= i3 - 1) {
                break;
            }
            char ch = str.charAt(i2 + i6);
            if ((ch >= ' ' && ch <= '/') || (ch >= ':' && ch <= '@') || ((ch >= '[' && ch <= '`') || (ch >= '{' && ch <= '~'))) {
                wrapTextLine(vector, str, gfx, i, i2, i6 + 1, i4, i5);
                wrapTextLine(vector, str, gfx, i, i2 + i6 + 1, (i3 - i6) - 1, i4, i5);
                break;
            }
            i6++;
        }
        if (i6 == i3 - 1) {
            int i7 = i3 >> 1;
            wrapTextLine(vector, str, gfx, i, i2, i7, i4, i5);
            wrapTextLine(vector, str, gfx, i, i2 + i7, i3 - i7, i4, i5);
        }
    }

    /* renamed from: a */
    private final Vector wrapText(Vector vector, String str, int i, int i2, int i3, int i4, int i5) {
        int iIndexOf;
        int iIndexOf2;
        if (i < i2) {
            if (i5 == 1) {
                int i6 = -1;
                int i7 = -1;
                for (int i8 = 0; i8 < 43; i8++) {
                    String pattern = AppState.getString(i8 + 1141);
                    if (null != pattern && (iIndexOf2 = str.indexOf(pattern, i)) >= 0 && (iIndexOf2 < i6 || i6 == -1)) {
                        i6 = iIndexOf2;
                        i7 = i8;
                    }
                }
                if (i6 < 0 || i6 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, -1);
                } else {
                    wrapText(vector, str, i, i6, i3, i4, -1);
                    vector.addElement(new int[]{16, 16, i7 + 110});
                    wrapText(vector, str, i6 + AppState.getString(i7 + 1141).length(), i2, i3, i4, 1);
                }
            } else if (i5 == 2 || i5 == 3) {
                int i9 = -1;
                int i10 = -1;
                for (int i11 = 0; i11 < 37; i11++) {
                    String pattern2 = AppState.getString(i11 + 1184);
                    if (null != pattern2 && (iIndexOf = str.indexOf(pattern2, i)) >= 0 && (iIndexOf < i9 || i9 == -1)) {
                        i9 = iIndexOf;
                        i10 = i11;
                    }
                }
                if (i9 < 0 || i9 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, -1);
                } else {
                    wrapText(vector, str, i, i9, i3, i4, -1);
                    vector.addElement(new int[]{16, 16, i10 + 318});
                    wrapText(vector, str, i9 + AppState.getString(i10 + 1184).length(), i2, i3, i4, 2);
                }
            } else if (i5 == 0) {
                int i12 = -1;
                int i13 = -1;
                for (int i14 = 0; i14 < 78; i14++) {
                    int iIndexOf3 = str.indexOf(AppState.getString(i14 + 1063), i);
                    if (iIndexOf3 >= 0 && (iIndexOf3 < i12 || i12 == -1)) {
                        i12 = iIndexOf3;
                        i13 = i14;
                    }
                }
                if (i12 < 0 || i12 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, -1);
                } else {
                    wrapText(vector, str, i, i12, i3, i4, -1);
                    int[] iArr = new int[3];
                    iArr[0] = 16;
                    iArr[1] = 16;
                    iArr[2] = i13 < 74 ? i13 + 36 : i13 == 74 ? 142 : i13 == 75 ? 137 : i13 == 76 ? 210 : 205;
                    vector.addElement(iArr);
                    wrapText(vector, str, i12 + AppState.getString(i13 + 1063).length(), i2, i3, i4, 0);
                }
            } else if (str != AppState.getString(StateKeys.STR_PLACEHOLDER_TEXT)) {
                GraphicsContext fontGfx = AppState.getGfxContext(i3);
                int offsetH = AppState.getIntOffset(i3);
                int i15 = i;
                int i16 = i;
                while (true) {
                    if (i16 > i2) {
                        break;
                    }
                    if (i16 == i2) {
                        int i17 = i16 - i15;
                        if (i17 > 0) {
                            wrapTextLine(vector, str, fontGfx, offsetH, i15, i17, i3, i4);
                        }
                        break;
                    } else {
                        char ch = str.charAt(i16);
                        if (ch == ' ') {
                            int i18 = (i16 - i15) + 1;
                            if (i18 > 1) {
                                wrapTextLine(vector, str, fontGfx, offsetH, i15, i18, i3, i4);
                            }
                            i15 = i16 + 1;
                        } else if (ch == '\r' || ch == '\n') {
                            int i19 = i16 - i15;
                            if (i19 > 0) {
                                wrapTextLine(vector, str, fontGfx, offsetH, i15, i19, i3, i4);
                            }
                            vector.addElement(AppState.pool[StateKeys.ARR_EMPTY_INT]);
                            i15 = i16 + 1;
                        }
                        i16++;
                    }
                }
            } else {
                int length = str.length();
                vector.addElement(new Object[]{str, new int[]{AppState.getGfxContext(i3).substringWidth(str, 0, length), AppState.getIntOffset(i3), 0, length, i3, i4}});
            }
        }
        return vector;
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
        if (this.id == 11) {
            return this;
        }
        this.positions[0] = 0;
        Vector vector = this.elements;
        int size = vector.size();
        int i2 = 0;
        int i3 = 0;
        int lineH = 0;
        this.maxHeight = 0;
        for (int i4 = 0; i4 < size; i4++) {
            Object elem = vector.elementAt(i4);
            if (elem != AppState.pool[StateKeys.ARR_EMPTY_INT]) {
                int elemW = getElementWidth(elem);
                int elemH = getElementHeight(elem);
                int i5 = 0;
                if (i4 == size - 2 && (vector.elementAt(i4 + 1) instanceof int[])) {
                    int[] iArr = (int[]) vector.elementAt(i4 + 1);
                    if (iArr.length == 3 && iArr[2] == 244) {
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
            lineH = Utils.max(lineH, this.positions[(size << 1) + 1] + getElementWidth(vector.elementAt(size)));
        }
    }

    /* renamed from: g */
    public final int getTotalWidth() {
        return this.wrapWidth != 0 ? this.wrapWidth : this.totalWidth + 4;
    }

    /* renamed from: h */
    public final int getTotalHeight() {
        return Utils.max(this.maxHeight, AppState.getInt(StateKeys.INT_FONT_HEIGHT)) + 4;
    }

    /* renamed from: a */
    public final void render(GraphicsContext gfx, int i, int i2, int i3) {
        if (this.id == 11) {
            gfx.graphics.drawImage(((GraphicsContext) this.data).image, i, i2, 20);
            return;
        }
        Vector vector = this.elements;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            }
            Object elem = vector.elementAt(size);
            if (elem != AppState.pool[StateKeys.ARR_EMPTY_INT]) {
                int i4 = i + 2;
                int i5 = this.positions[(size << 1) + 1];
                int i6 = i2 + 2 + this.positions[(size << 1) + 1 + 1];
                int i7 = i4 + i5;
                if (elem instanceof int[]) {
                    int[] iArr = (int[]) elem;
                    if (iArr.length == 3) {
                        int i8 = iArr[2];
                        gfx.drawIcon(i8, i8 != 244 ? i7 : (i4 + i3) - 13, i6 + ScreenManager.getCenterOffset());
                    } else if (iArr.length == 2) {
                        gfx.setColorFromPalette(18).drawRect(i7, i6, i3 - i7, iArr[1]);
                    }
                } else {
                    String str = (String) ((Object[]) elem)[0];
                    int[] iArr2 = (int[]) ((Object[]) elem)[1];
                    int i9 = iArr2[4];
                    GraphicsContext fontGfx = AppState.getGfxContext(i9);
                    GraphicsContext colorGfx = gfx.setFont(fontGfx).setColorFromPalette(iArr2[5]);
                    int i10 = iArr2[2];
                    int i11 = iArr2[3];
                    if (i6 > 0 && i6 < AppState.getInt(StateKeys.INT_SCREEN_HEIGHT)) {
                        colorGfx.graphics.drawSubstring(str, i10, i11, i7, i6, 20);
                    }
                    if (i9 == 3) {
                        gfx.drawRect(i7, i6 + (AppState.getInt(StateKeys.INT_FONT_HEIGHT) >> 1), fontGfx.substringWidth(str, iArr2[2], iArr2[3]), 0);
                    } else if (i9 == 5) {
                        gfx.drawRect(i7, i6 + AppState.getInt(StateKeys.INT_FONT_HEIGHT), fontGfx.substringWidth(str, iArr2[2], iArr2[3]), 0);
                    }
                }
            }
        }
    }
}
