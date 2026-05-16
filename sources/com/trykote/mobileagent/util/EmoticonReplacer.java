package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.ui.GraphicsContext;
import com.trykote.mobileagent.ui.IconElement;
import com.trykote.mobileagent.ui.LineBreak;
import com.trykote.mobileagent.ui.TextElement;

import java.util.Vector;

public final class EmoticonReplacer {

    private static final int MMP_EMOTICON_COUNT = 43;
    private static final int XMPP_EMOTICON_COUNT = 37;
    private static final int MRIM_EMOTICON_COUNT = 78;

    private static final int MMP_ICON_OFFSET = 110;
    private static final int XMPP_ICON_OFFSET = 318;
    private static final int MRIM_ICON_OFFSET = 36;
    private static final int MRIM_STANDARD_LIMIT = 74;
    private static final int MRIM_ICON_SPECIAL_74 = 142;
    private static final int MRIM_ICON_SPECIAL_75 = 137;
    private static final int MRIM_ICON_SPECIAL_76 = 210;
    private static final int MRIM_ICON_SPECIAL_77 = 205;

    private static final int EMOTICON_TYPE_MRIM = 0;
    private static final int EMOTICON_TYPE_MMP = 1;
    private static final int EMOTICON_TYPE_XMPP = 2;
    private static final int EMOTICON_TYPE_XMPP_ALT = 3;
    private static final int EMOTICON_TYPE_NONE = -1;

    private static final int ICON_SIZE = 16;

    public static Vector wrapText(Vector vector, String str, int i, int i2, int i3, int i4, int i5) {
        int iIndexOf;
        int iIndexOf2;
        if (i < i2) {
            if (i5 == EMOTICON_TYPE_MMP) {
                int i6 = -1;
                int i7 = -1;
                for (int i8 = 0; i8 < MMP_EMOTICON_COUNT; i8++) {
                    String pattern = StringPool.get(StringResKeys.MMP_EMOTICONS_BASE + i8);
                    if (pattern != null && (iIndexOf2 = str.indexOf(pattern, i)) >= 0 && (iIndexOf2 < i6 || i6 == -1)) {
                        i6 = iIndexOf2;
                        i7 = i8;
                    }
                }
                if (i6 < 0 || i6 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, EMOTICON_TYPE_NONE);
                } else {
                    wrapText(vector, str, i, i6, i3, i4, EMOTICON_TYPE_NONE);
                    vector.addElement(new IconElement(ICON_SIZE, i7 + MMP_ICON_OFFSET));
                    wrapText(vector, str, i6 + StringPool.get(StringResKeys.MMP_EMOTICONS_BASE + i7).length(), i2, i3, i4, EMOTICON_TYPE_MMP);
                }
            } else if (i5 == EMOTICON_TYPE_XMPP || i5 == EMOTICON_TYPE_XMPP_ALT) {
                int i9 = -1;
                int i10 = -1;
                for (int i11 = 0; i11 < XMPP_EMOTICON_COUNT; i11++) {
                    String pattern2 = StringPool.get(StringResKeys.XMPP_EMOTICONS_BASE + i11);
                    if (pattern2 != null && (iIndexOf = str.indexOf(pattern2, i)) >= 0 && (iIndexOf < i9 || i9 == -1)) {
                        i9 = iIndexOf;
                        i10 = i11;
                    }
                }
                if (i9 < 0 || i9 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, EMOTICON_TYPE_NONE);
                } else {
                    wrapText(vector, str, i, i9, i3, i4, EMOTICON_TYPE_NONE);
                    vector.addElement(new IconElement(ICON_SIZE, i10 + XMPP_ICON_OFFSET));
                    wrapText(vector, str, i9 + StringPool.get(StringResKeys.XMPP_EMOTICONS_BASE + i10).length(), i2, i3, i4, EMOTICON_TYPE_XMPP);
                }
            } else if (i5 == EMOTICON_TYPE_MRIM) {
                int i12 = -1;
                int i13 = -1;
                for (int i14 = 0; i14 < MRIM_EMOTICON_COUNT; i14++) {
                    int iIndexOf3 = str.indexOf(StringPool.get(StringResKeys.EMOTICON_NAMES_BASE + i14), i);
                    if (iIndexOf3 >= 0 && (iIndexOf3 < i12 || i12 == -1)) {
                        i12 = iIndexOf3;
                        i13 = i14;
                    }
                }
                if (i12 < 0 || i12 >= i2) {
                    wrapText(vector, str, i, i2, i3, i4, EMOTICON_TYPE_NONE);
                } else {
                    wrapText(vector, str, i, i12, i3, i4, EMOTICON_TYPE_NONE);
                    int iconCode = i13 < MRIM_STANDARD_LIMIT ? i13 + MRIM_ICON_OFFSET
                            : i13 == MRIM_STANDARD_LIMIT ? MRIM_ICON_SPECIAL_74
                            : i13 == MRIM_STANDARD_LIMIT + 1 ? MRIM_ICON_SPECIAL_75
                            : i13 == MRIM_STANDARD_LIMIT + 2 ? MRIM_ICON_SPECIAL_76
                            : MRIM_ICON_SPECIAL_77;
                    vector.addElement(new IconElement(ICON_SIZE, iconCode));
                    wrapText(vector, str, i12 + StringPool.get(StringResKeys.EMOTICON_NAMES_BASE + i13).length(), i2, i3, i4, EMOTICON_TYPE_MRIM);
                }
            } else if (str != StringPool.get(StringResKeys.STR_PLACEHOLDER_TEXT)) {
                GraphicsContext fontGfx = UIState.getGfxContext(i3);
                int offsetH = UIState.getIntOffset(i3);
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
                            vector.addElement(LineBreak.INSTANCE);
                            i15 = i16 + 1;
                        }
                        i16++;
                    }
                }
            } else {
                int length = str.length();
                vector.addElement(new TextElement(str, UIState.getGfxContext(i3).substringWidth(str, 0, length), UIState.getIntOffset(i3), 0, length, i3, i4));
            }
        }
        return vector;
    }

    private static void wrapTextLine(Vector vector, String str, GraphicsContext gfx, int i, int i2, int i3, int i4, int i5) {
        int textW = gfx.substringWidth(str, i2, i3);
        if (textW < (UIState.getScreenWidth() << 2) / 5) {
            vector.addElement(new TextElement(str, textW, i, i2, i3, i4, i5));
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
}
