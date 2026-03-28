package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* renamed from: bc */
/* loaded from: MobileAgent_3.9.jar:bc.class */
public abstract class ChatRenderer {

    /* renamed from: a */
    public static int offsetX;

    /* renamed from: b */
    public static int offsetY;

    /* renamed from: l */
    private static long scaleTimestamp;

    /* renamed from: m */
    private static long scaleZoom;

    /* renamed from: n */
    private static int scaleBarWidth;

    /* renamed from: o */
    private static String scaleLabel;

    /* renamed from: p */
    private static boolean scaleValid;

    /* renamed from: c */
    public static int[] buttonBounds;

    /* renamed from: d */
    public static long coord1;

    /* renamed from: e */
    public static long coord2;

    /* renamed from: f */
    public static long coord3;

    /* renamed from: g */
    public static long coord4;

    /* renamed from: h */
    public static ListItem[] mapItems;

    /* renamed from: i */
    public static long markerLon;

    /* renamed from: j */
    public static long markerLat;

    /* renamed from: k */
    public static int markerRadius;

    /* renamed from: a */
    public static final void renderScaleBar(Graphics graphics, int i, long j) {
        int i2;
        if (i < 0 || i > 17) {
            i = 0;
        }
        if (i != scaleZoom || Utils.absLong(j - scaleTimestamp) > 10000 || !scaleValid) {
            scaleValid = true;
            scaleTimestamp = j;
            scaleZoom = i;
            int i3 = i;
            int metersPerBar = (int) SoftFloat.floatToLong(SoftFloat.multiply(SoftFloat.cosFull(SoftFloat.reciprocal(SoftFloat.parseFloat(MapUtils.pixelToLatitude(j)))), SoftFloat.longToFloat((50 * MapUtils.getZoomNumerator(i3)) / MapUtils.getZoomDenominator(i3))));
            int step = metersPerBar < 100 ? 25 : metersPerBar < 1000 ? 100 : metersPerBar < 10000 ? 1000 : metersPerBar < 100000 ? 10000 : 100000;
            int roundedMeters = (metersPerBar / step) * step;
            scaleBarWidth = scaleToPixels(roundedMeters, i, j);
            StringBuffer sb = ObjectPool.newStringBuffer();
            if (roundedMeters < 1000) {
                sb.append(roundedMeters);
            } else {
                sb.append(roundedMeters / 1000).append((char) 1082);
            }
            scaleLabel = ObjectPool.toStringAndRelease(sb.append((char) 1084));
        }
        Font font = graphics.getFont();
        int color = graphics.getColor();
        Font scaleFont = AppState.getFont();
        graphics.setFont(scaleFont);
        int softKeyHeight = AppState.getInt(StateKeys.INT_FONT_HEIGHT);
        int barWidth = Utils.max(scaleBarWidth, scaleFont.stringWidth(scaleLabel));
        int height = scaleFont.getHeight();
        if (AppState.getBool(StateKeys.SETTING_CUSTOM_VIEW_MODE)) {
            i2 = -(softKeyHeight > 16 ? softKeyHeight : 18);
        } else {
            i2 = 0;
        }
        int i6 = i2;
        int clipWidth = (graphics.getClipWidth() - barWidth) - 5;
        int clipHeight = (graphics.getClipHeight() - height) - 13;
        graphics.drawString(scaleLabel, clipWidth, i6 + clipHeight, 20);
        graphics.setColor(16777215);
        graphics.fillRect(clipWidth, i6 + clipHeight + height + 5, scaleBarWidth / 2, 3);
        graphics.fillRect(clipWidth + (scaleBarWidth / 2), i6 + clipHeight + height + 2, scaleBarWidth / 2, 3);
        graphics.setColor(0);
        graphics.fillRect(clipWidth, i6 + clipHeight + height + 2, scaleBarWidth / 2, 4);
        graphics.fillRect(clipWidth + (scaleBarWidth / 2), i6 + clipHeight + height + 5, scaleBarWidth / 2, 3);
        graphics.drawRect(clipWidth, i6 + clipHeight + height + 2, scaleBarWidth, 6);
        graphics.setColor(color);
        graphics.setFont(font);
    }

    /* renamed from: a */
    public static final void renderMarker(Graphics graphics, long j, long j2, int i, int i2, int i3, long j3) {
        if (markerLon == 0 || markerLat == 0) {
            return;
        }
        int markerX = (int) ((i2 / 2) + (MapUtils.coordToPixel(markerLon, i) - j));
        int markerY = (int) ((i3 / 2) + (j2 - MapUtils.coordToPixel(markerLat, i)));
        if (markerX < 0 || markerY < 0 || markerX >= i2 || markerY >= i3) {
            return;
        }
        int color = graphics.getColor();
        graphics.setColor(255);
        graphics.fillArc(markerX - 6, markerY - 6, 12, 12, 0, 360);
        int radiusPx = scaleToPixels(markerRadius, i, j3);
        int diameter = radiusPx << 1;
        graphics.drawArc(markerX - radiusPx, markerY - radiusPx, diameter, diameter, 0, 360);
        graphics.setColor(14474460);
        graphics.drawArc(markerX - 7, markerY - 7, 14, 14, 0, 360);
        graphics.setColor(color);
    }

    /* renamed from: a */
    private static final int getMaxTextWidth(Vector vector, Font font) {
        int maxWidth = 20;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return maxWidth;
            }
            maxWidth = Utils.max(maxWidth, font.stringWidth((String) vector.elementAt(size)));
        }
    }

    /* renamed from: a */
    public static final void renderTooltip(Graphics graphics, String str, Font font, int i, int i2, int i3) {
        Vector lines = Utils.wrapText(str, font, i);
        int size = lines.size();
        int boxWidth = getMaxTextWidth(lines, font) + 10;
        int height = font.getHeight();
        int i4 = (height * size) + 6;
        Font font2 = graphics.getFont();
        int color = graphics.getColor();
        int themeIdx = AppState.getInt(StateKeys.SETTING_COLOR_THEME);
        graphics.setColor(AppState.getInt(PaletteKeys.MAP_FILL + themeIdx));
        int arrowSize = Utils.min(boxWidth / 25, 3);
        int arrowH = arrowSize << 1;
        int boxX = i2 - (boxWidth / 2);
        int boxY = (i3 - arrowH) - i4;
        graphics.fillRoundRect(boxX, boxY, boxWidth, i4, 10, 10);
        graphics.setColor(0);
        graphics.drawRoundRect(boxX, boxY, boxWidth, i4, 10, 10);
        graphics.setFont(font);
        graphics.setColor(AppState.getInt(PaletteKeys.COLORS_BASE + themeIdx));
        int i8 = size;
        while (true) {
            i8--;
            if (i8 < 0) {
                graphics.setColor(AppState.getInt(PaletteKeys.MAP_FILL + themeIdx));
                graphics.fillTriangle(i2 + arrowSize, i3 - (arrowSize << 1), i2 + (arrowSize << 2), i3 - (arrowSize << 1), i2, i3);
                graphics.setColor(0);
                graphics.drawLine(i2 + arrowSize, i3 - (arrowSize << 1), i2, i3);
                graphics.drawLine(i2 + (arrowSize << 2), i3 - (arrowSize << 1), i2, i3);
                graphics.setFont(font2);
                graphics.setColor(color);
                return;
            }
            graphics.drawString((String) lines.elementAt(i8), boxX + 5, boxY + 3 + (i8 * height), 20);
        }
    }

    /* renamed from: a */
    public static final void renderBubble(Graphics graphics, int i, int i2, int i3, long j, long j2, ListItem item) {
        if (item == null || !item.isSelected()) {
            return;
        }
        int fontSize = AppState.getInt(StateKeys.SETTING_FONT_SIZE_CHAT);
        Font font = Font.getFont(64, 0, fontSize == 0 ? 8 : fontSize == 1 ? 0 : 16);
        int bubbleX = (int) ((i / 2) + (item.getCommandId(i3) - j));
        int bubbleY = (int) ((i2 / 2) + (j2 - item.executeCommand(i3)));
        int i4 = 8;
        int i5 = 22;
        boolean hasAction = item.isHighlighted();
        int itemType = item.getHeight();
        int i6 = itemType == 1 ? 303 : 360;
        if (itemType == 2) {
            i6 = 308;
            i4 = 0;
        }
        if (itemType == 3 || itemType == 9 || itemType == 6) {
            i6 = 0;
            i4 = 0;
            i5 = 4;
        }
        if (itemType == 8) {
            int i7 = ((UserSearchResult) item).gender;
            i6 = i7 == 1 ? 377 : i7 == 2 ? 378 : 379;
            i4 = 0;
            i5 = 4;
        }
        if (itemType == 7 || itemType == 5) {
            i4 = 0;
            i5 = 4;
            i6 = 380;
        }
        int i8 = i6;
        int i9 = bubbleX + i4;
        int i10 = bubbleY - i5;
        Vector textLines = Utils.wrapText(Utils.defaultStr(item.getText()), font, i - 40);
        int size = textLines.size();
        int textWidth = getMaxTextWidth(textLines, font);
        int iStringWidth = font.stringWidth(AppState.getString(StateKeys.STR_SHOW_ROUTE)) + 6 + 24;
        int height = font.getHeight();
        Font font2 = graphics.getFont();
        int color = graphics.getColor();
        int i11 = height > 16 ? height : 16;
        int i12 = 6 + ((hasAction ? 2 : 1) * i11) + ((size - 1) * height);
        int i13 = textWidth + (i8 != 0 ? 16 : 0) + 6;
        int i14 = i13;
        if (i13 < iStringWidth) {
            i14 = iStringWidth;
        }
        int themeIdx = AppState.getInt(StateKeys.SETTING_COLOR_THEME);
        graphics.setColor(AppState.getInt(PaletteKeys.MAP_FILL + themeIdx));
        int i15 = i14 / 25;
        int i16 = i15;
        if (i15 < 3) {
            i16 = 3;
        }
        int i17 = (i10 - i12) - i16;
        if (i17 < 10) {
            if (!(MapRenderer.autoScrollCount > 0)) {
                int scrollAmount = Utils.abs(i17) + 20;
                if (MapRenderer.autoScrollCount <= 0) {
                    MapRenderer.autoScrollCount = scrollAmount;
                    MapRenderer.autoScrollTimestamp = System.currentTimeMillis();
                    MapRenderer.tooltipLocked = true;
                }
            }
        }
        int i18 = i16 << 1;
        graphics.fillRoundRect(i9 - (i14 / 2), (i10 - i18) - i12, i14, i12, 10, 10);
        graphics.setColor(0);
        graphics.drawRoundRect(i9 - (i14 / 2), (i10 - i18) - i12, i14, i12, 10, 10);
        GraphicsContext gfx = new GraphicsContext(graphics);
        if (i8 != 0) {
            gfx.drawIcon(i8, (i9 - (i14 / 2)) + 2, ((i10 - i18) - i12) + 2);
        }
        graphics.setFont(font);
        graphics.setColor(AppState.getInt(PaletteKeys.COLORS_BASE + themeIdx));
        for (int i19 = 0; i19 < size; i19++) {
            graphics.drawString((String) textLines.elementAt(i19), (i9 - (i14 / 2)) + 2 + (i8 != 0 ? 16 : 0), ((i10 - i18) - i12) + i11 + 2 + ((i19 - 1) * height), 20);
        }
        Image buttonImage = XmppContactGroup.getOrLoadImage(19);
        if (buttonBounds == null) {
            int[] iArr = new int[4];
            buttonBounds = iArr;
            iArr[2] = buttonImage.getWidth();
            buttonBounds[3] = buttonImage.getHeight();
        }
        if (hasAction) {
            int i20 = (i9 - (i14 / 2)) + 2 + ((i14 - iStringWidth) / 2);
            buttonBounds[0] = i20;
            int i21 = ((i10 - i18) - i12) + 4 + i11 + (height * (size - 1)) + (i11 / 2);
            buttonBounds[1] = i21;
            graphics.drawImage(buttonImage, i20, i21, 6);
            graphics.drawString(AppState.getString(StateKeys.STR_SHOW_ROUTE), (i9 - (i14 / 2)) + 2 + ((i14 - iStringWidth) / 2) + 24 + 2, ((i10 - i18) - i12) + 4 + i11 + (height * (size - 1)), 20);
        }
        graphics.setColor(AppState.getInt(PaletteKeys.MAP_FILL + themeIdx));
        graphics.fillTriangle(i9 + i16, i10 - i18, i9 + (i16 << 2), i10 - i18, i9, i10);
        graphics.setColor(0);
        graphics.drawLine(i9 + i16, i10 - i18, i9, i10);
        graphics.drawLine(i9 + (i16 << 2), i10 - i18, i9, i10);
        graphics.setFont(font2);
        graphics.setColor(color);
    }

    /* renamed from: a */
    public static final boolean isDistant(int i, int i2, int i3, int i4) {
        int dx = Utils.abs(i2 - i);
        int dy = Utils.abs(i4 - i3);
        int maxDist = Utils.max(dx, dy);
        if (maxDist >= 5) {
            return true;
        }
        return maxDist == 4 && Utils.min(dx, dy) >= 3;
    }

    /* renamed from: b */
    private static final int computeOutCode(int i, int i2, int i3, int i4) {
        int i5 = 0;
        if (i2 > i4) {
            i5 = 0 + 1;
        } else if (i2 < 0) {
            i5 = 0 + 2;
        }
        if (i > i3) {
            i5 += 4;
        } else if (i < 0) {
            i5 += 8;
        }
        return i5;
    }

    /* renamed from: a */
    private static int scaleToPixels(int i, int i2, long j) {
        return (int) SoftFloat.floatToLong(SoftFloat.divide(SoftFloat.longToFloat(MapUtils.coordToPixel(i, i2)), SoftFloat.cosFull(SoftFloat.reciprocal(SoftFloat.parseFloat(MapUtils.pixelToLatitude(j))))));
    }

    /* JADX WARN: Code restructure failed: missing block: B:132:0x0416, code lost:
    
        r45 = r0;
        r44 = r0;
        r43 = r0;
        r31 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:133:0x042e, code lost:
    
        r35 = computeOutCode(r31, r43, r19, r20);
        r0 = computeOutCode(r44, r45, r19, r20);
     */
    /* JADX WARN: Code restructure failed: missing block: B:134:0x0454, code lost:
    
        if ((r35 & r0) == 0) goto L136;
     */
    /* JADX WARN: Code restructure failed: missing block: B:135:0x0457, code lost:
    
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x045b, code lost:
    
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x045c, code lost:
    
        if (r0 == false) goto L139;
     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x045f, code lost:
    
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:140:0x0468, code lost:
    
        if ((r35 | r0) != 0) goto L142;
     */
    /* JADX WARN: Code restructure failed: missing block: B:141:0x046b, code lost:
    
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:142:0x046f, code lost:
    
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:143:0x0470, code lost:
    
        if (r0 == false) goto L145;
     */
    /* JADX WARN: Code restructure failed: missing block: B:144:0x0473, code lost:
    
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:146:0x0479, code lost:
    
        if (r35 != 0) goto L148;
     */
    /* JADX WARN: Code restructure failed: missing block: B:147:0x047c, code lost:
    
        r0 = r31;
        r31 = r44;
        r44 = r0;
        r0 = r43;
        r43 = r45;
        r45 = r0;
        r35 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:149:0x049c, code lost:
    
        if ((r35 & 1) == 0) goto L351;
     */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x049f, code lost:
    
        r31 = r31 + (((r44 - r31) * (r20 - r43)) / (r45 - r43));
        r43 = r20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:152:0x04c0, code lost:
    
        if ((r35 & 2) == 0) goto L353;
     */
    /* JADX WARN: Code restructure failed: missing block: B:153:0x04c3, code lost:
    
        r31 = r31 + (((r31 - r44) * r43) / (r45 - r43));
        r43 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x04e0, code lost:
    
        if ((r35 & 4) == 0) goto L355;
     */
    /* JADX WARN: Code restructure failed: missing block: B:156:0x04e3, code lost:
    
        r43 = r43 + (((r45 - r43) * (r19 - r31)) / (r44 - r31));
        r31 = r19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x0505, code lost:
    
        if ((r35 & 8) == 0) goto L359;
     */
    /* JADX WARN: Code restructure failed: missing block: B:159:0x0508, code lost:
    
        r43 = r43 + (((r43 - r45) * r31) / (r44 - r31));
        r31 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:160:0x0521, code lost:
    
        if (r0 == false) goto L344;
     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x0524, code lost:
    
        r0 = p000.Utils.abs(r0 - r0);
        r0 = p000.Utils.abs(r0 - r0);
        r0 = p000.AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
     */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x0555, code lost:
    
        if (r0 == 15) goto L165;
     */
    /* JADX WARN: Code restructure failed: missing block: B:164:0x055c, code lost:
    
        if (r0 != 16) goto L166;
     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x055f, code lost:
    
        r0 = 11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x0568, code lost:
    
        if (r0 != 14) goto L169;
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x056b, code lost:
    
        r0 = 9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:169:0x0570, code lost:
    
        r0 = 6;
     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x0572, code lost:
    
        r38 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:171:0x0578, code lost:
    
        if (r0 <= r0) goto L173;
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x057b, code lost:
    
        r0 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:173:0x057f, code lost:
    
        r0 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:174:0x0580, code lost:
    
        r1 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:175:0x0583, code lost:
    
        if (r0 == false) goto L177;
     */
    /* JADX WARN: Code restructure failed: missing block: B:176:0x0586, code lost:
    
        r0 = r0 - 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:177:0x058d, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:178:0x058f, code lost:
    
        r29 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x0593, code lost:
    
        if (r1 == false) goto L181;
     */
    /* JADX WARN: Code restructure failed: missing block: B:180:0x0596, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x059b, code lost:
    
        r0 = r0 - 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x059f, code lost:
    
        r30 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:183:0x05a3, code lost:
    
        if (r1 == false) goto L185;
     */
    /* JADX WARN: Code restructure failed: missing block: B:184:0x05a6, code lost:
    
        r0 = r0 + 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x05ad, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:186:0x05af, code lost:
    
        r33 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:187:0x05b3, code lost:
    
        if (r1 == false) goto L189;
     */
    /* JADX WARN: Code restructure failed: missing block: B:188:0x05b6, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:189:0x05bb, code lost:
    
        r0 = r0 + 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:190:0x05bf, code lost:
    
        r34 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:191:0x05c3, code lost:
    
        if (r1 == false) goto L193;
     */
    /* JADX WARN: Code restructure failed: missing block: B:192:0x05c6, code lost:
    
        r0 = r0 + 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:193:0x05cd, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:194:0x05cf, code lost:
    
        r40 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:195:0x05d3, code lost:
    
        if (r1 == false) goto L197;
     */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x05d6, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:197:0x05db, code lost:
    
        r0 = r0 + 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x05df, code lost:
    
        r41 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x05e3, code lost:
    
        if (r1 == false) goto L201;
     */
    /* JADX WARN: Code restructure failed: missing block: B:200:0x05e6, code lost:
    
        r0 = r0 - 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:201:0x05ed, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:202:0x05ef, code lost:
    
        r42 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:203:0x05f3, code lost:
    
        if (r1 == false) goto L205;
     */
    /* JADX WARN: Code restructure failed: missing block: B:204:0x05f6, code lost:
    
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x05fb, code lost:
    
        r0 = r0 - 3;
     */
    /* JADX WARN: Code restructure failed: missing block: B:206:0x05ff, code lost:
    
        r9.fillTriangle(r29, r30, r33, r34, r40, r41);
        r9.fillTriangle(r40, r41, r42, r0, r29, r30);
        r9.fillArc(r0 - (r38 / 2), r0 - (r38 / 2), r38, r38, 0, 360);
        r9.fillArc(r0 - (r38 / 2), r0 - (r38 / 2), r38, r38, 0, 360);
     */
    /* JADX WARN: Removed duplicated region for block: B:97:0x0233  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void renderMapOverlay(Graphics graphics, long j, long j2, long j3, long j4, int i, int i2, int i3) {
        boolean z;
        int i4;
        if (!MmpContact.locationEnabled && !MmpContact.hasFirstToken() && !MmpContact.hasSecondToken()) {
            AppState.setInt(StateKeys.FLAG_CHAT_HAS_ITEMS, 0);
            return;
        }
        int color = graphics.getColor();
        int fontSize = AppState.getInt(StateKeys.SETTING_FONT_SIZE_CHAT);
        Font font = Font.getFont(64, 0, fontSize == 0 ? 8 : fontSize == 1 ? 0 : 16);
        int halfSpan = (i2 / 2) * SoftFloat.floatToInt(SoftFloat.multiply(SoftFloat.longToFloat(1 << (17 - i)), 4608057598812004689L));
        int i5 = (int) (j3 - halfSpan);
        int i6 = (int) (j3 + halfSpan);
        int i7 = (int) (j4 + halfSpan);
        int i8 = (int) (j4 - halfSpan);
        Vector visibleRegions = ObjectPool.newVector();
        int size = MmpContact.routeRegions.size();
        for (int i9 = 0; i9 < size; i9++) {
            int[] iArr = (int[]) ((Object[]) MmpContact.routeRegions.elementAt(i9))[0];
            int i10 = iArr[0];
            int i11 = iArr[1];
            int i12 = iArr[2];
            int i13 = iArr[3];
            if (!(i6 - i5 > i12 - i10 && i7 - i8 > i11 - i13 ? i10 > i5 && i12 < i6 && i11 < i7 && i13 > i8 : i5 > i10 && i6 < i12 && i7 < i11 && i8 > i13)) {
                int i14 = iArr[0];
                int i15 = iArr[1];
                int i16 = iArr[2];
                int i17 = iArr[3];
                if ((i15 <= i7 && i15 >= i8 && ((i5 >= i14 && i5 <= i16) || (i6 >= i14 && i6 <= i16))) || (i17 <= i7 && i17 >= i8 && ((i5 >= i14 && i5 <= i16) || (i6 >= i14 && i6 <= i16))) || ((i16 >= i5 && i16 <= i6 && ((i15 >= i7 && i17 <= i7) || (i15 >= i8 && i17 <= i8))) || (i14 >= i5 && i14 <= i6 && ((i15 >= i7 && i17 <= i7) || (i15 >= i8 && i17 <= i8))))) {
                    visibleRegions.addElement(MmpContact.routeRegions.elementAt(i9));
                }
            }
        }
        int size2 = visibleRegions.size();
        int totalPoints = MmpContact.getTotalRoutePoints();
        AppState.setBool(StateKeys.FLAG_CHAT_HAS_ITEMS, size2 > 0);
        String str = null;
        int i18 = 0;
        int i19 = 0;
        if (totalPoints > 1) {
            graphics.setColor(13311);
            for (int i20 = 0; i20 < size2; i20++) {
                Object[] objArr = (Object[]) ((Object[]) visibleRegions.elementAt(i20))[1];
                int length = objArr.length;
                int i21 = length - 1;
                while (i21 > 0) {
                    if (objArr[i21] != null) {
                        int px = (int) (MapUtils.coordToPixel(((int[]) ((Object[]) objArr[i21])[0])[0], i) - (j - (i2 / 2)));
                        int px2 = (int) ((j2 + (i3 / 2)) - MapUtils.coordToPixel(((int[]) ((Object[]) objArr[i21])[0])[1], i));
                        while (true) {
                            Object[] objArr2 = (Object[]) objArr[length - i21];
                            String[] strArr = null;
                            if (objArr2 != null && objArr2.length == 3) {
                                String[] strArr2 = new String[2];
                                strArr = strArr2;
                                strArr2[0] = (String) objArr2[1];
                                strArr[1] = (String) objArr2[2];
                            }
                            if (strArr != null) {
                                int px3 = (int) MapUtils.coordToPixel(((int[]) objArr2[0])[0], i);
                                int px4 = (int) MapUtils.coordToPixel(((int[]) objArr2[0])[1], i);
                                int px5 = (int) (MapUtils.coordToPixel(((int[]) objArr2[0])[0], i) - (j - (i2 / 2)));
                                int px6 = (int) ((j2 + (i3 / 2)) - MapUtils.coordToPixel(((int[]) objArr2[0])[1], i));
                                if (Utils.absLong(j - px3) < 7 && Utils.absLong(j2 - px4) < 7 && str == null) {
                                    str = strArr[0];
                                    i18 = px5;
                                    i19 = px6;
                                }
                            }
                            if (objArr[i21 - 1] != null) {
                                int px7 = (int) (MapUtils.coordToPixel(((int[]) ((Object[]) objArr[i21 - 1])[0])[0], i) - (j - (i2 / 2)));
                                int px8 = (int) ((j2 + (i3 / 2)) - MapUtils.coordToPixel(((int[]) ((Object[]) objArr[i21 - 1])[0])[1], i));
                                if (isDistant(px, px7, px2, px8) || i21 - 1 == 0) {
                                    break;
                                } else if (i21 > 0) {
                                    i21--;
                                }
                            }
                        }
                    }
                    i21--;
                }
            }
        } else {
            if (MmpContact.hasFirstToken()) {
                int px9 = (int) (MapUtils.coordToPixel(MmpContact.lastTokenPair[0], i) - (j - (i2 / 2)));
                int px10 = (int) ((j2 + (i3 / 2)) - MapUtils.coordToPixel(MmpContact.lastTokenPair[1], i));
                if (px9 > 0 && px9 < i2 && px10 > 0 && px10 < i3) {
                    graphics.drawImage(XmppContactGroup.getOrLoadImage(20), px9, px10, 36);
                }
            }
            if (MmpContact.hasSecondToken()) {
                int px11 = (int) (MapUtils.coordToPixel(MmpContact.currentTokenPair[0], i) - (j - (i2 / 2)));
                int px12 = (int) ((j2 + (i3 / 2)) - MapUtils.coordToPixel(MmpContact.currentTokenPair[1], i));
                if (px11 > 0 && px11 < i2 && px12 > 0 && px12 < i3) {
                    graphics.drawImage(XmppContactGroup.getOrLoadImage(21), px11, px12, 36);
                }
            }
        }
        Vector vector = MmpContact.nearestPoints;
        int size3 = vector.size();
        int color2 = graphics.getColor();
        boolean z2 = false;
        for (int i22 = 0; i22 < size3; i22++) {
            Object[] objArr3 = (Object[]) vector.elementAt(i22);
            int[] coords = objArr3[0] != null ? MmpContact.getRoutePointAt(((Integer) objArr3[0]).intValue()) : null;
            if (coords == null) {
                coords = (int[]) objArr3[1];
            }
            if (coords != null) {
                int px13 = (int) MapUtils.coordToPixel(coords[0], i);
                int px14 = (int) MapUtils.coordToPixel(coords[1], i);
                int i23 = px13 - (((int) j) - (i2 / 2));
                int i24 = (((int) j2) + (i3 / 2)) - px14;
                if (i23 > 0 && i23 < i2 && i24 > 0 && i24 < i3) {
                    if (Utils.absLong(j - px13) >= 20 || Utils.absLong(j2 - px14) >= 20 || z2) {
                        if (!z2) {
                            AppState.setInt(StateKeys.FLAG_TYPING_HIDDEN, 0);
                            AppState.setBool(StateKeys.FLAG_TYPING_VISIBLE, AppState.getBool(StateKeys.FLAG_TYPING_INDICATOR) && !AppState.getBool(StateKeys.FLAG_TYPING_HIDDEN));
                            MmpContact.mapDataCache = null;
                        }
                        i4 = 9;
                        graphics.setColor(40, 221, 22);
                    } else {
                        AppState.setBool(StateKeys.FLAG_TYPING_HIDDEN, AppState.getBool(StateKeys.FLAG_TYPING_INDICATOR));
                        AppState.setBool(StateKeys.FLAG_TYPING_VISIBLE, AppState.getBool(StateKeys.FLAG_TYPING_INDICATOR) && !AppState.getBool(StateKeys.FLAG_TYPING_HIDDEN));
                        i4 = 11;
                        graphics.setColor(45, 253, 24);
                        MmpContact.mapDataCache = objArr3;
                        z2 = true;
                    }
                    graphics.fillArc(i23 - (i4 / 2), i24 - (i4 / 2), i4, i4, 0, 360);
                    graphics.setColor(13311);
                    graphics.drawArc(i23 - (i4 / 2), i24 - (i4 / 2), i4, i4, 0, 360);
                }
            }
        }
        graphics.setColor(color2);
        int i25 = (int) j;
        int i26 = (int) j2;
        int totalPoints2 = MmpContact.getTotalRoutePoints();
        if (totalPoints2 < 2) {
            z = false;
        } else {
            boolean z3 = false;
            String str2 = null;
            int i27 = 0;
            int i28 = 0;
            int[] coords2 = MmpContact.getRoutePointAt(0);
            int px15 = (int) MapUtils.coordToPixel(coords2[0], i);
            int px16 = (int) MapUtils.coordToPixel(coords2[1], i);
            int i29 = px15 - (i25 - (i2 / 2));
            int i30 = (i26 + (i3 / 2)) - px16;
            String[] labels = MmpContact.getRouteLabelsAt(0);
            if (Utils.abs(i25 - px15) >= 7 || Utils.abs(i26 - px16) >= 7 || labels == null) {
                graphics.drawImage(XmppContactGroup.getOrLoadImage(20), i29, i30, 36);
            } else if (labels[0] != null) {
                str2 = labels[0];
                i27 = i29;
                i28 = i30;
                z3 = true;
            }
            int[] coords3 = MmpContact.getRoutePointAt(totalPoints2 - 1);
            int px17 = (int) MapUtils.coordToPixel(coords3[0], i);
            int px18 = (int) MapUtils.coordToPixel(coords3[1], i);
            int i31 = px17 - (i25 - (i2 / 2));
            int i32 = (i26 + (i3 / 2)) - px18;
            String[] labels2 = MmpContact.getRouteLabelsAt(totalPoints2 - 1);
            if (Utils.abs(i25 - px17) >= 7 || Utils.abs(i26 - px18) >= 7 || labels2 == null || z3) {
                graphics.drawImage(XmppContactGroup.getOrLoadImage(21), i31, i32, 36);
            } else if (labels2[0] != null) {
                str2 = labels2[0];
                i27 = i31;
                i28 = i32;
                z3 = true;
            }
            if (z3) {
                renderTooltip(graphics, str2, font, i2 - 40, i27, i28);
            }
            z = z3;
        }
        boolean z4 = z;
        if (str != null && !z4) {
            renderTooltip(graphics, str, font, i2 - 40, i18, i19);
        }
        if (totalPoints > 1) {
            int i33 = (int) j2;
            Font font2 = graphics.getFont();
            graphics.setFont(font);
            int[] coords4 = MmpContact.getRoutePointAt(0);
            int px19 = (int) MapUtils.coordToPixel(coords4[0], i);
            int px20 = (int) MapUtils.coordToPixel(coords4[1], i);
            if (Utils.abs(((int) j) - px19) < 7 && Utils.abs(i33 - px20) < 7) {
                int height = font.getHeight();
                int clipHeight = (graphics.getClipHeight() - height) - 1;
                int i34 = 22;
                if (AppState.getBool(StateKeys.SETTING_CUSTOM_VIEW_MODE)) {
                    clipHeight -= (height > 18 ? height : 18) + 2;
                    i34 = 22 - 20;
                }
                String routeLabel = AppState.getString(StateKeys.STR_ROUTE_LABEL);
                int labelWidth = font.stringWidth(routeLabel) + 6;
                int themeIdx2 = AppState.getInt(StateKeys.SETTING_COLOR_THEME);
                graphics.setColor(AppState.getInt(themeIdx2 + 5050));
                graphics.fillRoundRect(i34, clipHeight, labelWidth, height, 10, 10);
                graphics.setColor(AppState.getInt(themeIdx2 + 4914));
                graphics.drawRoundRect(i34, clipHeight, labelWidth, height, 10, 10);
                graphics.drawString(routeLabel, i34 + 3, clipHeight, 20);
                graphics.setFont(font2);
            }
        }
        graphics.setColor(color);
    }
}
