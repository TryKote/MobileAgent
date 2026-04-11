package com.trykote.mobileagent.map;

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

public abstract class ChatRenderer {

    // Colors
    private static final int COLOR_WHITE = 16777215;
    private static final int COLOR_BLUE = 255;
    private static final int COLOR_MARKER_OUTLINE = 14474460;
    private static final int COLOR_ROUTE = 13311;
    private static final int COLOR_BLACK = 0;

    // Scale bar rendering
    private static final int SCALE_BAR_PIXEL_WIDTH = 50;
    private static final int SCALE_BAR_HEIGHT = 3;
    private static final int SCALE_BAR_TOTAL_HEIGHT = 6;
    private static final int SCALE_BAR_MARGIN_RIGHT = 5;
    private static final int SCALE_BAR_MARGIN_BOTTOM = 13;
    private static final int SCALE_BAR_OFFSET_TOP = 5;
    private static final int SCALE_BAR_OFFSET_INNER = 2;
    private static final int SCALE_BAR_THICK = 4;

    // Scale recalculation threshold (10 seconds)
    private static final long SCALE_RECALC_THRESHOLD_MS = 10000;

    // Scale thresholds (meters)
    private static final int SCALE_THRESHOLD_100 = 100;
    private static final int SCALE_THRESHOLD_1000 = 1000;
    private static final int SCALE_THRESHOLD_10000 = 10000;
    private static final int SCALE_THRESHOLD_100000 = 100000;
    private static final int SCALE_STEP_25 = 25;
    private static final int SCALE_STEP_100 = 100;
    private static final int SCALE_STEP_1000 = 1000;
    private static final int SCALE_STEP_10000 = 10000;

    // Minimum soft key bar height
    private static final int MIN_SOFTKEY_HEIGHT = 18;

    // Marker rendering
    private static final int MARKER_DOT_RADIUS = 6;
    private static final int MARKER_DOT_DIAMETER = 12;
    private static final int MARKER_OUTLINE_RADIUS = 7;
    private static final int MARKER_OUTLINE_DIAMETER = 14;
    private static final int FULL_CIRCLE = 360;

    // Tooltip rendering
    private static final int TOOLTIP_PADDING_H = 10;
    private static final int TOOLTIP_PADDING_V = 6;
    private static final int TOOLTIP_TEXT_INSET_X = 5;
    private static final int TOOLTIP_TEXT_INSET_Y = 3;
    private static final int TOOLTIP_MIN_TEXT_WIDTH = 20;
    private static final int TOOLTIP_CORNER_RADIUS = 10;
    private static final int TOOLTIP_MIN_ARROW_SIZE = 3;
    private static final int TOOLTIP_ARROW_DIVISOR = 25;

    // Bubble rendering
    private static final int BUBBLE_TEXT_MARGIN = 40;
    private static final int BUBBLE_DEFAULT_OFFSET_Y = 22;
    private static final int BUBBLE_COMPACT_OFFSET_Y = 4;
    private static final int BUBBLE_ICON_SIZE = 16;
    private static final int BUBBLE_PADDING = 6;
    private static final int BUBBLE_INNER_PADDING = 2;
    private static final int BUBBLE_BUTTON_ICON_SIZE = 24;
    private static final int BUBBLE_BUTTON_GAP = 2;
    private static final int BUBBLE_MIN_TOP_MARGIN = 10;
    private static final int BUBBLE_SCROLL_EXTRA = 20;

    // Route point rendering
    private static final int ROUTE_POINT_NORMAL_SIZE = 9;
    private static final int ROUTE_POINT_SELECTED_SIZE = 11;
    private static final int ROUTE_POINT_HOVER_DISTANCE = 20;

    // Route tooltip hover distance (pixels in map coords)
    private static final int ROUTE_LABEL_HOVER_DISTANCE = 7;

    // Distance thresholds for isDistant()
    private static final int DISTANT_THRESHOLD = 5;
    private static final int DISTANT_DIAG_MAX = 4;
    private static final int DISTANT_DIAG_MIN = 3;

    // Route label bar
    private static final int ROUTE_LABEL_DEFAULT_X = 22;
    private static final int ROUTE_LABEL_COMPACT_SHIFT = 20;
    private static final int ROUTE_LABEL_BOTTOM_MARGIN = 1;
    private static final int ROUTE_LABEL_SOFTKEY_MARGIN = 2;

    public static int offsetX;

    public static int offsetY;

    private static long scaleTimestamp;

    private static long scaleZoom;

    private static int scaleBarWidth;

    private static String scaleLabel;

    private static boolean scaleValid;

    public static int[] buttonBounds;

    public static long coord1;

    public static long coord2;

    public static long coord3;

    public static long coord4;

    public static ListItem[] mapItems;

    public static long markerLon;

    public static long markerLat;

    public static int markerRadius;

    public static final void renderScaleBar(Graphics graphics, int i, long j) {
        int i2;
        if (i < 0 || i > 17) {
            i = 0;
        }
        if (i != scaleZoom || Utils.absLong(j - scaleTimestamp) > SCALE_RECALC_THRESHOLD_MS || !scaleValid) {
            scaleValid = true;
            scaleTimestamp = j;
            scaleZoom = i;
            int i3 = i;
            int metersPerBar = (int) SoftFloat.floatToLong(SoftFloat.multiply(SoftFloat.cosFull(SoftFloat.reciprocal(SoftFloat.parseFloat(MapUtils.pixelToLatitude(j)))), SoftFloat.longToFloat((SCALE_BAR_PIXEL_WIDTH * MapUtils.getZoomNumerator(i3)) / MapUtils.getZoomDenominator(i3))));
            int step = metersPerBar < SCALE_THRESHOLD_100 ? SCALE_STEP_25 : metersPerBar < SCALE_THRESHOLD_1000 ? SCALE_STEP_100 : metersPerBar < SCALE_THRESHOLD_10000 ? SCALE_STEP_1000 : metersPerBar < SCALE_THRESHOLD_100000 ? SCALE_STEP_10000 : SCALE_THRESHOLD_100000;
            int roundedMeters = (metersPerBar / step) * step;
            scaleBarWidth = scaleToPixels(roundedMeters, i, j);
            StringBuffer sb = ObjectPool.newStringBuffer();
            if (roundedMeters < 1000) {
                sb.append(roundedMeters);
            } else {
                sb.append(roundedMeters / SCALE_THRESHOLD_1000).append((char) 1082);
            }
            scaleLabel = ObjectPool.toStringAndRelease(sb.append((char) 1084));
        }
        Font font = graphics.getFont();
        int color = graphics.getColor();
        Font scaleFont = Storage.state().getFont();
        graphics.setFont(scaleFont);
        int softKeyHeight = Storage.state().getInt(UIKeys.INT_FONT_HEIGHT);
        int barWidth = Utils.max(scaleBarWidth, scaleFont.stringWidth(scaleLabel));
        int height = scaleFont.getHeight();
        if (Storage.state().getBool(SettingsKeys.SETTING_CUSTOM_VIEW_MODE)) {
            i2 = -(softKeyHeight > BUBBLE_ICON_SIZE ? softKeyHeight : MIN_SOFTKEY_HEIGHT);
        } else {
            i2 = 0;
        }
        int i6 = i2;
        int clipWidth = (graphics.getClipWidth() - barWidth) - SCALE_BAR_MARGIN_RIGHT;
        int clipHeight = (graphics.getClipHeight() - height) - SCALE_BAR_MARGIN_BOTTOM;
        graphics.drawString(scaleLabel, clipWidth, i6 + clipHeight, 20);
        graphics.setColor(COLOR_WHITE);
        graphics.fillRect(clipWidth, i6 + clipHeight + height + SCALE_BAR_OFFSET_TOP, scaleBarWidth / 2, SCALE_BAR_HEIGHT);
        graphics.fillRect(clipWidth + (scaleBarWidth / 2), i6 + clipHeight + height + SCALE_BAR_OFFSET_INNER, scaleBarWidth / 2, SCALE_BAR_HEIGHT);
        graphics.setColor(COLOR_BLACK);
        graphics.fillRect(clipWidth, i6 + clipHeight + height + SCALE_BAR_OFFSET_INNER, scaleBarWidth / 2, SCALE_BAR_THICK);
        graphics.fillRect(clipWidth + (scaleBarWidth / 2), i6 + clipHeight + height + SCALE_BAR_OFFSET_TOP, scaleBarWidth / 2, SCALE_BAR_HEIGHT);
        graphics.drawRect(clipWidth, i6 + clipHeight + height + SCALE_BAR_OFFSET_INNER, scaleBarWidth, SCALE_BAR_TOTAL_HEIGHT);
        graphics.setColor(color);
        graphics.setFont(font);
    }

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
        graphics.setColor(COLOR_BLUE);
        graphics.fillArc(markerX - MARKER_DOT_RADIUS, markerY - MARKER_DOT_RADIUS, MARKER_DOT_DIAMETER, MARKER_DOT_DIAMETER, 0, FULL_CIRCLE);
        int radiusPx = scaleToPixels(markerRadius, i, j3);
        int diameter = radiusPx << 1;
        graphics.drawArc(markerX - radiusPx, markerY - radiusPx, diameter, diameter, 0, FULL_CIRCLE);
        graphics.setColor(COLOR_MARKER_OUTLINE);
        graphics.drawArc(markerX - MARKER_OUTLINE_RADIUS, markerY - MARKER_OUTLINE_RADIUS, MARKER_OUTLINE_DIAMETER, MARKER_OUTLINE_DIAMETER, 0, FULL_CIRCLE);
        graphics.setColor(color);
    }

    private static final int getMaxTextWidth(Vector vector, Font font) {
        int maxWidth = TOOLTIP_MIN_TEXT_WIDTH;
        for (int idx = vector.size() - 1; idx >= 0; idx--) {
            maxWidth = Utils.max(maxWidth, font.stringWidth((String) vector.elementAt(idx)));
        }
        return maxWidth;
    }

    public static final void renderTooltip(Graphics graphics, String str, Font font, int i, int i2, int i3) {
        Vector lines = Utils.wrapText(str, font, i);
        int size = lines.size();
        int boxWidth = getMaxTextWidth(lines, font) + TOOLTIP_PADDING_H;
        int height = font.getHeight();
        int i4 = (height * size) + TOOLTIP_PADDING_V;
        Font font2 = graphics.getFont();
        int color = graphics.getColor();
        int themeIdx = Storage.state().getInt(SettingsKeys.SETTING_COLOR_THEME);
        graphics.setColor(Storage.state().getInt(PaletteKeys.MAP_FILL + themeIdx));
        int arrowSize = Utils.min(boxWidth / TOOLTIP_ARROW_DIVISOR, TOOLTIP_MIN_ARROW_SIZE);
        int arrowH = arrowSize << 1;
        int boxX = i2 - (boxWidth / 2);
        int boxY = (i3 - arrowH) - i4;
        graphics.fillRoundRect(boxX, boxY, boxWidth, i4, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS);
        graphics.setColor(COLOR_BLACK);
        graphics.drawRoundRect(boxX, boxY, boxWidth, i4, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS);
        graphics.setFont(font);
        graphics.setColor(Storage.state().getInt(PaletteKeys.COLORS_BASE + themeIdx));
        for (int i8 = size - 1; i8 >= 0; i8--) {
            graphics.drawString((String) lines.elementAt(i8), boxX + TOOLTIP_TEXT_INSET_X, boxY + TOOLTIP_TEXT_INSET_Y + (i8 * height), 20);
        }
        graphics.setColor(Storage.state().getInt(PaletteKeys.MAP_FILL + themeIdx));
        graphics.fillTriangle(i2 + arrowSize, i3 - (arrowSize << 1), i2 + (arrowSize << 2), i3 - (arrowSize << 1), i2, i3);
        graphics.setColor(COLOR_BLACK);
        graphics.drawLine(i2 + arrowSize, i3 - (arrowSize << 1), i2, i3);
        graphics.drawLine(i2 + (arrowSize << 2), i3 - (arrowSize << 1), i2, i3);
        graphics.setFont(font2);
        graphics.setColor(color);
    }

    public static final void renderBubble(Graphics graphics, int i, int i2, int i3, long j, long j2, ListItem item) {
        if (item == null || !item.isSelected()) {
            return;
        }
        int fontSize = Storage.state().getInt(SettingsKeys.SETTING_FONT_SIZE_CHAT);
        Font font = Font.getFont(64, 0, fontSize == 0 ? 8 : fontSize == 1 ? 0 : 16);
        int bubbleX = (int) ((i / 2) + (item.getCommandId(i3) - j));
        int bubbleY = (int) ((i2 / 2) + (j2 - item.executeCommand(i3)));
        int i4 = 8;
        int i5 = BUBBLE_DEFAULT_OFFSET_Y;
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
            i5 = BUBBLE_COMPACT_OFFSET_Y;
        }
        if (itemType == 8) {
            int i7 = ((UserSearchResult) item).gender;
            i6 = i7 == 1 ? 377 : i7 == 2 ? 378 : 379;
            i4 = 0;
            i5 = BUBBLE_COMPACT_OFFSET_Y;
        }
        if (itemType == 7 || itemType == 5) {
            i4 = 0;
            i5 = BUBBLE_COMPACT_OFFSET_Y;
            i6 = 380;
        }
        int i8 = i6;
        int i9 = bubbleX + i4;
        int i10 = bubbleY - i5;
        Vector textLines = Utils.wrapText(Utils.defaultStr(item.getText()), font, i - BUBBLE_TEXT_MARGIN);
        int size = textLines.size();
        int textWidth = getMaxTextWidth(textLines, font);
        int iStringWidth = font.stringWidth(Storage.resources().getString(StringResKeys.STR_SHOW_ROUTE)) + BUBBLE_PADDING + BUBBLE_BUTTON_ICON_SIZE;
        int height = font.getHeight();
        Font font2 = graphics.getFont();
        int color = graphics.getColor();
        int i11 = height > BUBBLE_ICON_SIZE ? height : BUBBLE_ICON_SIZE;
        int i12 = BUBBLE_PADDING + ((hasAction ? 2 : 1) * i11) + ((size - 1) * height);
        int i13 = textWidth + (i8 != 0 ? BUBBLE_ICON_SIZE : 0) + BUBBLE_PADDING;
        int i14 = i13;
        if (i13 < iStringWidth) {
            i14 = iStringWidth;
        }
        int themeIdx = Storage.state().getInt(SettingsKeys.SETTING_COLOR_THEME);
        graphics.setColor(Storage.state().getInt(PaletteKeys.MAP_FILL + themeIdx));
        int i15 = i14 / TOOLTIP_ARROW_DIVISOR;
        int i16 = i15;
        if (i15 < TOOLTIP_MIN_ARROW_SIZE) {
            i16 = TOOLTIP_MIN_ARROW_SIZE;
        }
        int i17 = (i10 - i12) - i16;
        if (i17 < BUBBLE_MIN_TOP_MARGIN) {
            if (!(MapRenderer.autoScrollCount > 0)) {
                int scrollAmount = Utils.abs(i17) + BUBBLE_SCROLL_EXTRA;
                if (MapRenderer.autoScrollCount <= 0) {
                    MapRenderer.autoScrollCount = scrollAmount;
                    MapRenderer.autoScrollTimestamp = System.currentTimeMillis();
                    MapRenderer.tooltipLocked = true;
                }
            }
        }
        int i18 = i16 << 1;
        graphics.fillRoundRect(i9 - (i14 / 2), (i10 - i18) - i12, i14, i12, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS);
        graphics.setColor(COLOR_BLACK);
        graphics.drawRoundRect(i9 - (i14 / 2), (i10 - i18) - i12, i14, i12, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS);
        GraphicsContext gfx = new GraphicsContext(graphics);
        if (i8 != 0) {
            gfx.drawIcon(i8, (i9 - (i14 / 2)) + BUBBLE_INNER_PADDING, ((i10 - i18) - i12) + BUBBLE_INNER_PADDING);
        }
        graphics.setFont(font);
        graphics.setColor(Storage.state().getInt(PaletteKeys.COLORS_BASE + themeIdx));
        for (int i19 = 0; i19 < size; i19++) {
            graphics.drawString((String) textLines.elementAt(i19), (i9 - (i14 / 2)) + BUBBLE_INNER_PADDING + (i8 != 0 ? BUBBLE_ICON_SIZE : 0), ((i10 - i18) - i12) + i11 + BUBBLE_INNER_PADDING + ((i19 - 1) * height), 20);
        }
        Image buttonImage = XmppContactGroup.getOrLoadImage(19);
        if (buttonBounds == null) {
            int[] iArr = new int[4];
            buttonBounds = iArr;
            iArr[2] = buttonImage.getWidth();
            buttonBounds[3] = buttonImage.getHeight();
        }
        if (hasAction) {
            int i20 = (i9 - (i14 / 2)) + BUBBLE_INNER_PADDING + ((i14 - iStringWidth) / 2);
            buttonBounds[0] = i20;
            int i21 = ((i10 - i18) - i12) + (BUBBLE_INNER_PADDING * 2) + i11 + (height * (size - 1)) + (i11 / 2);
            buttonBounds[1] = i21;
            graphics.drawImage(buttonImage, i20, i21, 6);
            graphics.drawString(Storage.resources().getString(StringResKeys.STR_SHOW_ROUTE), (i9 - (i14 / 2)) + BUBBLE_INNER_PADDING + ((i14 - iStringWidth) / 2) + BUBBLE_BUTTON_ICON_SIZE + BUBBLE_BUTTON_GAP, ((i10 - i18) - i12) + (BUBBLE_INNER_PADDING * 2) + i11 + (height * (size - 1)), 20);
        }
        graphics.setColor(Storage.state().getInt(PaletteKeys.MAP_FILL + themeIdx));
        graphics.fillTriangle(i9 + i16, i10 - i18, i9 + (i16 << 2), i10 - i18, i9, i10);
        graphics.setColor(COLOR_BLACK);
        graphics.drawLine(i9 + i16, i10 - i18, i9, i10);
        graphics.drawLine(i9 + (i16 << 2), i10 - i18, i9, i10);
        graphics.setFont(font2);
        graphics.setColor(color);
    }

    public static final boolean isDistant(int i, int i2, int i3, int i4) {
        int dx = Utils.abs(i2 - i);
        int dy = Utils.abs(i4 - i3);
        int maxDist = Utils.max(dx, dy);
        if (maxDist >= DISTANT_THRESHOLD) {
            return true;
        }
        return maxDist == DISTANT_DIAG_MAX && Utils.min(dx, dy) >= DISTANT_DIAG_MIN;
    }

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

    private static int scaleToPixels(int i, int i2, long j) {
        return (int) SoftFloat.floatToLong(SoftFloat.divide(SoftFloat.longToFloat(MapUtils.coordToPixel(i, i2)), SoftFloat.cosFull(SoftFloat.reciprocal(SoftFloat.parseFloat(MapUtils.pixelToLatitude(j))))));
    }
    public static final void renderMapOverlay(Graphics graphics, long j, long j2, long j3, long j4, int i, int i2, int i3) {
        boolean z;
        int i4;
        if (!MmpContact.locationEnabled && !MmpContact.hasFirstToken() && !MmpContact.hasSecondToken()) {
            Storage.state().setInt(MapKeys.FLAG_CHAT_HAS_ITEMS, 0);
            return;
        }
        int color = graphics.getColor();
        int fontSize = Storage.state().getInt(SettingsKeys.SETTING_FONT_SIZE_CHAT);
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
        Storage.state().setBool(MapKeys.FLAG_CHAT_HAS_ITEMS, size2 > 0);
        String str = null;
        int i18 = 0;
        int i19 = 0;
        if (totalPoints > 1) {
            graphics.setColor(COLOR_ROUTE);
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
                                if (Utils.absLong(j - px3) < ROUTE_LABEL_HOVER_DISTANCE && Utils.absLong(j2 - px4) < ROUTE_LABEL_HOVER_DISTANCE && str == null) {
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
                    if (Utils.absLong(j - px13) >= ROUTE_POINT_HOVER_DISTANCE || Utils.absLong(j2 - px14) >= ROUTE_POINT_HOVER_DISTANCE || z2) {
                        if (!z2) {
                            Storage.state().setInt(UIKeys.FLAG_ROUTE_POINT_HIDDEN, 0);
                            Storage.state().setBool(UIKeys.FLAG_ROUTE_POINT_VISIBLE, Storage.state().getBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE) && !Storage.state().getBool(UIKeys.FLAG_ROUTE_POINT_HIDDEN));
                            MmpContact.mapDataCache = null;
                        }
                        i4 = ROUTE_POINT_NORMAL_SIZE;
                        graphics.setColor(40, 221, 22);
                    } else {
                        Storage.state().setBool(UIKeys.FLAG_ROUTE_POINT_HIDDEN, Storage.state().getBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE));
                        Storage.state().setBool(UIKeys.FLAG_ROUTE_POINT_VISIBLE, Storage.state().getBool(UIKeys.FLAG_ROUTE_LOCATION_ACTIVE) && !Storage.state().getBool(UIKeys.FLAG_ROUTE_POINT_HIDDEN));
                        i4 = ROUTE_POINT_SELECTED_SIZE;
                        graphics.setColor(45, 253, 24);
                        MmpContact.mapDataCache = objArr3;
                        z2 = true;
                    }
                    graphics.fillArc(i23 - (i4 / 2), i24 - (i4 / 2), i4, i4, 0, FULL_CIRCLE);
                    graphics.setColor(COLOR_ROUTE);
                    graphics.drawArc(i23 - (i4 / 2), i24 - (i4 / 2), i4, i4, 0, FULL_CIRCLE);
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
            if (Utils.abs(i25 - px15) >= ROUTE_LABEL_HOVER_DISTANCE || Utils.abs(i26 - px16) >= ROUTE_LABEL_HOVER_DISTANCE || labels == null) {
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
            if (Utils.abs(i25 - px17) >= ROUTE_LABEL_HOVER_DISTANCE || Utils.abs(i26 - px18) >= ROUTE_LABEL_HOVER_DISTANCE || labels2 == null || z3) {
                graphics.drawImage(XmppContactGroup.getOrLoadImage(21), i31, i32, 36);
            } else if (labels2[0] != null) {
                str2 = labels2[0];
                i27 = i31;
                i28 = i32;
                z3 = true;
            }
            if (z3) {
                renderTooltip(graphics, str2, font, i2 - BUBBLE_TEXT_MARGIN, i27, i28);
            }
            z = z3;
        }
        boolean z4 = z;
        if (str != null && !z4) {
            renderTooltip(graphics, str, font, i2 - BUBBLE_TEXT_MARGIN, i18, i19);
        }
        if (totalPoints > 1) {
            int i33 = (int) j2;
            Font font2 = graphics.getFont();
            graphics.setFont(font);
            int[] coords4 = MmpContact.getRoutePointAt(0);
            int px19 = (int) MapUtils.coordToPixel(coords4[0], i);
            int px20 = (int) MapUtils.coordToPixel(coords4[1], i);
            if (Utils.abs(((int) j) - px19) < ROUTE_LABEL_HOVER_DISTANCE && Utils.abs(i33 - px20) < ROUTE_LABEL_HOVER_DISTANCE) {
                int height = font.getHeight();
                int clipHeight = (graphics.getClipHeight() - height) - ROUTE_LABEL_BOTTOM_MARGIN;
                int i34 = ROUTE_LABEL_DEFAULT_X;
                if (Storage.state().getBool(SettingsKeys.SETTING_CUSTOM_VIEW_MODE)) {
                    clipHeight -= (height > MIN_SOFTKEY_HEIGHT ? height : MIN_SOFTKEY_HEIGHT) + ROUTE_LABEL_SOFTKEY_MARGIN;
                    i34 = ROUTE_LABEL_DEFAULT_X - ROUTE_LABEL_COMPACT_SHIFT;
                }
                String routeLabel = Storage.resources().getString(StringResKeys.STR_ROUTE_LABEL);
                int labelWidth = font.stringWidth(routeLabel) + BUBBLE_PADDING;
                int themeIdx2 = Storage.state().getInt(SettingsKeys.SETTING_COLOR_THEME);
                graphics.setColor(Storage.state().getInt(themeIdx2 + 5050));
                graphics.fillRoundRect(i34, clipHeight, labelWidth, height, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS);
                graphics.setColor(Storage.state().getInt(themeIdx2 + 4914));
                graphics.drawRoundRect(i34, clipHeight, labelWidth, height, TOOLTIP_CORNER_RADIUS, TOOLTIP_CORNER_RADIUS);
                graphics.drawString(routeLabel, i34 + TOOLTIP_TEXT_INSET_Y, clipHeight, 20);
                graphics.setFont(font2);
            }
        }
        graphics.setColor(color);
    }
}
