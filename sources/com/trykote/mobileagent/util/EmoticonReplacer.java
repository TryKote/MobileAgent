package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import java.util.Vector;

public final class EmoticonReplacer {

    public static Vector wrapText(Vector vector, String str, int i, int i2, int i3, int i4, int i5) {
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
                    vector.addElement(new IconElement(16, i7 + 110));
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
                    vector.addElement(new IconElement(16, i10 + 318));
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
                    int iconCode = i13 < 74 ? i13 + 36 : i13 == 74 ? 142 : i13 == 75 ? 137 : i13 == 76 ? 210 : 205;
                    vector.addElement(new IconElement(16, iconCode));
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
                            vector.addElement(LineBreak.INSTANCE);
                            i15 = i16 + 1;
                        }
                        i16++;
                    }
                }
            } else {
                int length = str.length();
                vector.addElement(new TextElement(str, AppState.getGfxContext(i3).substringWidth(str, 0, length), AppState.getIntOffset(i3), 0, length, i3, i4));
            }
        }
        return vector;
    }

    private static void wrapTextLine(Vector vector, String str, GraphicsContext gfx, int i, int i2, int i3, int i4, int i5) {
        int textW = gfx.substringWidth(str, i2, i3);
        if (textW < (AppState.getInt(StateKeys.INT_SCREEN_WIDTH) << 2) / 5) {
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
