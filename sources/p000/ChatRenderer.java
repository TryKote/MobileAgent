package p000;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* renamed from: bc */
/* loaded from: MobileAgent_3.9.jar:bc.class */
public abstract class ChatRenderer {

    /* renamed from: a */
    public static int f241a;

    /* renamed from: b */
    public static int f242b;

    /* renamed from: l */
    private static long f243l;

    /* renamed from: m */
    private static long f244m;

    /* renamed from: n */
    private static int f245n;

    /* renamed from: o */
    private static String f246o;

    /* renamed from: p */
    private static boolean f247p;

    /* renamed from: c */
    public static int[] f248c;

    /* renamed from: d */
    public static long f249d;

    /* renamed from: e */
    public static long f250e;

    /* renamed from: f */
    public static long f251f;

    /* renamed from: g */
    public static long f252g;

    /* renamed from: h */
    public static ListItem[] f253h;

    /* renamed from: i */
    public static long f254i;

    /* renamed from: j */
    public static long f255j;

    /* renamed from: k */
    public static int f256k;

    /* renamed from: a */
    public static final void m828a(Graphics graphics, int i, long j) {
        int i2;
        if (i < 0 || i > 17) {
            i = 0;
        }
        if (i != f244m || Utils.m505a(j - f243l) > 10000 || !f247p) {
            f247p = true;
            f243l = j;
            f244m = i;
            int i3 = i;
            int iM689d = (int) SoftFloat.m689d(SoftFloat.m692d(SoftFloat.m713k(SoftFloat.m699e(SoftFloat.m697a(IOUtils.m810b(j)))), SoftFloat.m687b((50 * AppController.m315d(i3)) / AppController.m316e(i3))));
            int i4 = iM689d < 100 ? 25 : iM689d < 1000 ? 100 : iM689d < 10000 ? 1000 : iM689d < 100000 ? 10000 : 100000;
            int i5 = (iM689d / i4) * i4;
            f245n = m835a(i5, i, j);
            StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
            if (i5 < 1000) {
                stringBufferM1217h.append(i5);
            } else {
                stringBufferM1217h.append(i5 / 1000).append((char) 1082);
            }
            f246o = NetworkUtils.m1215a(stringBufferM1217h.append((char) 1084));
        }
        Font font = graphics.getFont();
        int color = graphics.getColor();
        Font fontM625m = AppState.m625m();
        graphics.setFont(fontM625m);
        int iM586d = AppState.m586d(1450);
        int iM502a = Utils.m502a(f245n, fontM625m.stringWidth(f246o));
        int height = fontM625m.getHeight();
        if (AppState.m587e(230)) {
            i2 = -(iM586d > 16 ? iM586d : 18);
        } else {
            i2 = 0;
        }
        int i6 = i2;
        int clipWidth = (graphics.getClipWidth() - iM502a) - 5;
        int clipHeight = (graphics.getClipHeight() - height) - 13;
        graphics.drawString(f246o, clipWidth, i6 + clipHeight, 20);
        graphics.setColor(16777215);
        graphics.fillRect(clipWidth, i6 + clipHeight + height + 5, f245n / 2, 3);
        graphics.fillRect(clipWidth + (f245n / 2), i6 + clipHeight + height + 2, f245n / 2, 3);
        graphics.setColor(0);
        graphics.fillRect(clipWidth, i6 + clipHeight + height + 2, f245n / 2, 4);
        graphics.fillRect(clipWidth + (f245n / 2), i6 + clipHeight + height + 5, f245n / 2, 3);
        graphics.drawRect(clipWidth, i6 + clipHeight + height + 2, f245n, 6);
        graphics.setColor(color);
        graphics.setFont(font);
    }

    /* renamed from: a */
    public static final void m829a(Graphics graphics, long j, long j2, int i, int i2, int i3, long j3) {
        if (f254i == 0 || f255j == 0) {
            return;
        }
        int iM317a = (int) ((i2 / 2) + (AppController.m317a(f254i, i) - j));
        int iM317a2 = (int) ((i3 / 2) + (j2 - AppController.m317a(f255j, i)));
        if (iM317a < 0 || iM317a2 < 0 || iM317a >= i2 || iM317a2 >= i3) {
            return;
        }
        int color = graphics.getColor();
        graphics.setColor(255);
        graphics.fillArc(iM317a - 6, iM317a2 - 6, 12, 12, 0, 360);
        int iM835a = m835a(f256k, i, j3);
        int i4 = iM835a << 1;
        graphics.drawArc(iM317a - iM835a, iM317a2 - iM835a, i4, i4, 0, 360);
        graphics.setColor(14474460);
        graphics.drawArc(iM317a - 7, iM317a2 - 7, 14, 14, 0, 360);
        graphics.setColor(color);
    }

    /* renamed from: a */
    private static final int m830a(Vector vector, Font font) {
        int iM502a = 20;
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                return iM502a;
            }
            iM502a = Utils.m502a(iM502a, font.stringWidth((String) vector.elementAt(size)));
        }
    }

    /* renamed from: a */
    public static final void m831a(Graphics graphics, String str, Font font, int i, int i2, int i3) {
        Vector vectorM543a = Utils.m543a(str, font, i);
        int size = vectorM543a.size();
        int iM830a = m830a(vectorM543a, font) + 10;
        int height = font.getHeight();
        int i4 = (height * size) + 6;
        Font font2 = graphics.getFont();
        int color = graphics.getColor();
        int iM586d = AppState.m586d(72);
        graphics.setColor(AppState.m586d(iM586d + 5050));
        int iM503b = Utils.m503b(iM830a / 25, 3);
        int i5 = iM503b << 1;
        int i6 = i2 - (iM830a / 2);
        int i7 = (i3 - i5) - i4;
        graphics.fillRoundRect(i6, i7, iM830a, i4, 10, 10);
        graphics.setColor(0);
        graphics.drawRoundRect(i6, i7, iM830a, i4, 10, 10);
        graphics.setFont(font);
        graphics.setColor(AppState.m586d(iM586d + 4914));
        int i8 = size;
        while (true) {
            i8--;
            if (i8 < 0) {
                graphics.setColor(AppState.m586d(iM586d + 5050));
                graphics.fillTriangle(i2 + iM503b, i3 - (iM503b << 1), i2 + (iM503b << 2), i3 - (iM503b << 1), i2, i3);
                graphics.setColor(0);
                graphics.drawLine(i2 + iM503b, i3 - (iM503b << 1), i2, i3);
                graphics.drawLine(i2 + (iM503b << 2), i3 - (iM503b << 1), i2, i3);
                graphics.setFont(font2);
                graphics.setColor(color);
                return;
            }
            graphics.drawString((String) vectorM543a.elementAt(i8), i6 + 5, i7 + 3 + (i8 * height), 20);
        }
    }

    /* renamed from: a */
    public static final void m832a(Graphics graphics, int i, int i2, int i3, long j, long j2, ListItem interfaceC0044o) {
        if (interfaceC0044o == null || !interfaceC0044o.isSelected()) {
            return;
        }
        int iM586d = AppState.m586d(73);
        Font font = Font.getFont(64, 0, iM586d == 0 ? 8 : iM586d == 1 ? 0 : 16);
        int iMo282a = (int) ((i / 2) + (interfaceC0044o.getCommandId(i3) - j));
        int iMo283b = (int) ((i2 / 2) + (j2 - interfaceC0044o.executeCommand(i3)));
        int i4 = 8;
        int i5 = 22;
        boolean zMo281z = interfaceC0044o.isHighlighted();
        int iMo276r = interfaceC0044o.getHeight();
        int i6 = iMo276r == 1 ? 303 : 360;
        if (iMo276r == 2) {
            i6 = 308;
            i4 = 0;
        }
        if (iMo276r == 3 || iMo276r == 9 || iMo276r == 6) {
            i6 = 0;
            i4 = 0;
            i5 = 4;
        }
        if (iMo276r == 8) {
            int i7 = ((UserSearchResult) interfaceC0044o).f393d;
            i6 = i7 == 1 ? 377 : i7 == 2 ? 378 : 379;
            i4 = 0;
            i5 = 4;
        }
        if (iMo276r == 7 || iMo276r == 5) {
            i4 = 0;
            i5 = 4;
            i6 = 380;
        }
        int i8 = i6;
        int i9 = iMo282a + i4;
        int i10 = iMo283b - i5;
        Vector vectorM543a = Utils.m543a(Utils.m522f(interfaceC0044o.getText()), font, i - 40);
        int size = vectorM543a.size();
        int iM830a = m830a(vectorM543a, font);
        int iStringWidth = font.stringWidth(AppState.m584b(982)) + 6 + 24;
        int height = font.getHeight();
        Font font2 = graphics.getFont();
        int color = graphics.getColor();
        int i11 = height > 16 ? height : 16;
        int i12 = 6 + ((zMo281z ? 2 : 1) * i11) + ((size - 1) * height);
        int i13 = iM830a + (i8 != 0 ? 16 : 0) + 6;
        int i14 = i13;
        if (i13 < iStringWidth) {
            i14 = iStringWidth;
        }
        int iM586d2 = AppState.m586d(72);
        graphics.setColor(AppState.m586d(iM586d2 + 5050));
        int i15 = i14 / 25;
        int i16 = i15;
        if (i15 < 3) {
            i16 = 3;
        }
        int i17 = (i10 - i12) - i16;
        if (i17 < 10) {
            if (!(MapRenderer.f208n > 0)) {
                int iM504c = Utils.m504c(i17) + 20;
                if (MapRenderer.f208n <= 0) {
                    MapRenderer.f208n = iM504c;
                    MapRenderer.f209o = System.currentTimeMillis();
                    MapRenderer.f204l = true;
                }
            }
        }
        int i18 = i16 << 1;
        graphics.fillRoundRect(i9 - (i14 / 2), (i10 - i18) - i12, i14, i12, 10, 10);
        graphics.setColor(0);
        graphics.drawRoundRect(i9 - (i14 / 2), (i10 - i18) - i12, i14, i12, 10, 10);
        GraphicsContext c0012al = new GraphicsContext(graphics);
        if (i8 != 0) {
            c0012al.m216a(i8, (i9 - (i14 / 2)) + 2, ((i10 - i18) - i12) + 2);
        }
        graphics.setFont(font);
        graphics.setColor(AppState.m586d(iM586d2 + 4914));
        for (int i19 = 0; i19 < size; i19++) {
            graphics.drawString((String) vectorM543a.elementAt(i19), (i9 - (i14 / 2)) + 2 + (i8 != 0 ? 16 : 0), ((i10 - i18) - i12) + i11 + 2 + ((i19 - 1) * height), 20);
        }
        Image imageM1023b = XmppContactGroup.m1023b(19);
        if (f248c == null) {
            int[] iArr = new int[4];
            f248c = iArr;
            iArr[2] = imageM1023b.getWidth();
            f248c[3] = imageM1023b.getHeight();
        }
        if (zMo281z) {
            int i20 = (i9 - (i14 / 2)) + 2 + ((i14 - iStringWidth) / 2);
            f248c[0] = i20;
            int i21 = ((i10 - i18) - i12) + 4 + i11 + (height * (size - 1)) + (i11 / 2);
            f248c[1] = i21;
            graphics.drawImage(imageM1023b, i20, i21, 6);
            graphics.drawString(AppState.m584b(982), (i9 - (i14 / 2)) + 2 + ((i14 - iStringWidth) / 2) + 24 + 2, ((i10 - i18) - i12) + 4 + i11 + (height * (size - 1)), 20);
        }
        graphics.setColor(AppState.m586d(iM586d2 + 5050));
        graphics.fillTriangle(i9 + i16, i10 - i18, i9 + (i16 << 2), i10 - i18, i9, i10);
        graphics.setColor(0);
        graphics.drawLine(i9 + i16, i10 - i18, i9, i10);
        graphics.drawLine(i9 + (i16 << 2), i10 - i18, i9, i10);
        graphics.setFont(font2);
        graphics.setColor(color);
    }

    /* renamed from: a */
    public static final boolean m833a(int i, int i2, int i3, int i4) {
        int iM504c = Utils.m504c(i2 - i);
        int iM504c2 = Utils.m504c(i4 - i3);
        int iM502a = Utils.m502a(iM504c, iM504c2);
        if (iM502a >= 5) {
            return true;
        }
        return iM502a == 4 && Utils.m503b(iM504c, iM504c2) >= 3;
    }

    /* renamed from: b */
    private static final int m834b(int i, int i2, int i3, int i4) {
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
    private static int m835a(int i, int i2, long j) {
        return (int) SoftFloat.m689d(SoftFloat.m693e(SoftFloat.m687b(AppController.m317a(i, i2)), SoftFloat.m713k(SoftFloat.m699e(SoftFloat.m697a(IOUtils.m810b(j))))));
    }

    /* JADX WARN: Code restructure failed: missing block: B:132:0x0416, code lost:
    
        r45 = r0;
        r44 = r0;
        r43 = r0;
        r31 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:133:0x042e, code lost:
    
        r35 = m834b(r31, r43, r19, r20);
        r0 = m834b(r44, r45, r19, r20);
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
    
        r0 = p000.Utils.m504c(r0 - r0);
        r0 = p000.Utils.m504c(r0 - r0);
        r0 = p000.AppState.m586d(39);
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
    public static final void m836a(Graphics graphics, long j, long j2, long j3, long j4, int i, int i2, int i3) {
        boolean z;
        int i4;
        if (!MmpContact.f71y && !MmpContact.m188p() && !MmpContact.m189q()) {
            AppState.m594c(1546, 0);
            return;
        }
        int color = graphics.getColor();
        int iM586d = AppState.m586d(73);
        Font font = Font.getFont(64, 0, iM586d == 0 ? 8 : iM586d == 1 ? 0 : 16);
        int iM688c = (i2 / 2) * SoftFloat.m688c(SoftFloat.m692d(SoftFloat.m687b(1 << (17 - i)), 4608057598812004689L));
        int i5 = (int) (j3 - iM688c);
        int i6 = (int) (j3 + iM688c);
        int i7 = (int) (j4 + iM688c);
        int i8 = (int) (j4 - iM688c);
        Vector vectorM1213g = NetworkUtils.m1213g();
        int size = MmpContact.f70n.size();
        for (int i9 = 0; i9 < size; i9++) {
            int[] iArr = (int[]) ((Object[]) MmpContact.f70n.elementAt(i9))[0];
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
                    vectorM1213g.addElement(MmpContact.f70n.elementAt(i9));
                }
            }
        }
        int size2 = vectorM1213g.size();
        int iM192t = MmpContact.m192t();
        AppState.m599a(1546, size2 > 0);
        String str = null;
        int i18 = 0;
        int i19 = 0;
        if (iM192t > 1) {
            graphics.setColor(13311);
            for (int i20 = 0; i20 < size2; i20++) {
                Object[] objArr = (Object[]) ((Object[]) vectorM1213g.elementAt(i20))[1];
                int length = objArr.length;
                int i21 = length - 1;
                while (i21 > 0) {
                    if (objArr[i21] != null) {
                        int iM317a = (int) (AppController.m317a(((int[]) ((Object[]) objArr[i21])[0])[0], i) - (j - (i2 / 2)));
                        int iM317a2 = (int) ((j2 + (i3 / 2)) - AppController.m317a(((int[]) ((Object[]) objArr[i21])[0])[1], i));
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
                                int iM317a3 = (int) AppController.m317a(((int[]) objArr2[0])[0], i);
                                int iM317a4 = (int) AppController.m317a(((int[]) objArr2[0])[1], i);
                                int iM317a5 = (int) (AppController.m317a(((int[]) objArr2[0])[0], i) - (j - (i2 / 2)));
                                int iM317a6 = (int) ((j2 + (i3 / 2)) - AppController.m317a(((int[]) objArr2[0])[1], i));
                                if (Utils.m505a(j - iM317a3) < 7 && Utils.m505a(j2 - iM317a4) < 7 && str == null) {
                                    str = strArr[0];
                                    i18 = iM317a5;
                                    i19 = iM317a6;
                                }
                            }
                            if (objArr[i21 - 1] != null) {
                                int iM317a7 = (int) (AppController.m317a(((int[]) ((Object[]) objArr[i21 - 1])[0])[0], i) - (j - (i2 / 2)));
                                int iM317a8 = (int) ((j2 + (i3 / 2)) - AppController.m317a(((int[]) ((Object[]) objArr[i21 - 1])[0])[1], i));
                                if (m833a(iM317a, iM317a7, iM317a2, iM317a8) || i21 - 1 == 0) {
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
            if (MmpContact.m188p()) {
                int iM317a9 = (int) (AppController.m317a(MmpContact.f64i[0], i) - (j - (i2 / 2)));
                int iM317a10 = (int) ((j2 + (i3 / 2)) - AppController.m317a(MmpContact.f64i[1], i));
                if (iM317a9 > 0 && iM317a9 < i2 && iM317a10 > 0 && iM317a10 < i3) {
                    graphics.drawImage(XmppContactGroup.m1023b(20), iM317a9, iM317a10, 36);
                }
            }
            if (MmpContact.m189q()) {
                int iM317a11 = (int) (AppController.m317a(MmpContact.f65j[0], i) - (j - (i2 / 2)));
                int iM317a12 = (int) ((j2 + (i3 / 2)) - AppController.m317a(MmpContact.f65j[1], i));
                if (iM317a11 > 0 && iM317a11 < i2 && iM317a12 > 0 && iM317a12 < i3) {
                    graphics.drawImage(XmppContactGroup.m1023b(21), iM317a11, iM317a12, 36);
                }
            }
        }
        Vector vector = MmpContact.f67l;
        int size3 = vector.size();
        int color2 = graphics.getColor();
        boolean z2 = false;
        for (int i22 = 0; i22 < size3; i22++) {
            Object[] objArr3 = (Object[]) vector.elementAt(i22);
            int[] iArrM193a = objArr3[0] != null ? MmpContact.m193a(((Integer) objArr3[0]).intValue()) : null;
            if (iArrM193a == null) {
                iArrM193a = (int[]) objArr3[1];
            }
            if (iArrM193a != null) {
                int iM317a13 = (int) AppController.m317a(iArrM193a[0], i);
                int iM317a14 = (int) AppController.m317a(iArrM193a[1], i);
                int i23 = iM317a13 - (((int) j) - (i2 / 2));
                int i24 = (((int) j2) + (i3 / 2)) - iM317a14;
                if (i23 > 0 && i23 < i2 && i24 > 0 && i24 < i3) {
                    if (Utils.m505a(j - iM317a13) >= 20 || Utils.m505a(j2 - iM317a14) >= 20 || z2) {
                        if (!z2) {
                            AppState.m594c(1575, 0);
                            AppState.m599a(1574, AppState.m587e(1573) && !AppState.m587e(1575));
                            MmpContact.f68m = null;
                        }
                        i4 = 9;
                        graphics.setColor(40, 221, 22);
                    } else {
                        AppState.m599a(1575, AppState.m587e(1573));
                        AppState.m599a(1574, AppState.m587e(1573) && !AppState.m587e(1575));
                        i4 = 11;
                        graphics.setColor(45, 253, 24);
                        MmpContact.f68m = objArr3;
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
        int iM192t2 = MmpContact.m192t();
        if (iM192t2 < 2) {
            z = false;
        } else {
            boolean z3 = false;
            String str2 = null;
            int i27 = 0;
            int i28 = 0;
            int[] iArrM193a2 = MmpContact.m193a(0);
            int iM317a15 = (int) AppController.m317a(iArrM193a2[0], i);
            int iM317a16 = (int) AppController.m317a(iArrM193a2[1], i);
            int i29 = iM317a15 - (i25 - (i2 / 2));
            int i30 = (i26 + (i3 / 2)) - iM317a16;
            String[] strArrM194b = MmpContact.m194b(0);
            if (Utils.m504c(i25 - iM317a15) >= 7 || Utils.m504c(i26 - iM317a16) >= 7 || strArrM194b == null) {
                graphics.drawImage(XmppContactGroup.m1023b(20), i29, i30, 36);
            } else if (strArrM194b[0] != null) {
                str2 = strArrM194b[0];
                i27 = i29;
                i28 = i30;
                z3 = true;
            }
            int[] iArrM193a3 = MmpContact.m193a(iM192t2 - 1);
            int iM317a17 = (int) AppController.m317a(iArrM193a3[0], i);
            int iM317a18 = (int) AppController.m317a(iArrM193a3[1], i);
            int i31 = iM317a17 - (i25 - (i2 / 2));
            int i32 = (i26 + (i3 / 2)) - iM317a18;
            String[] strArrM194b2 = MmpContact.m194b(iM192t2 - 1);
            if (Utils.m504c(i25 - iM317a17) >= 7 || Utils.m504c(i26 - iM317a18) >= 7 || strArrM194b2 == null || z3) {
                graphics.drawImage(XmppContactGroup.m1023b(21), i31, i32, 36);
            } else if (strArrM194b2[0] != null) {
                str2 = strArrM194b2[0];
                i27 = i31;
                i28 = i32;
                z3 = true;
            }
            if (z3) {
                m831a(graphics, str2, font, i2 - 40, i27, i28);
            }
            z = z3;
        }
        boolean z4 = z;
        if (str != null && !z4) {
            m831a(graphics, str, font, i2 - 40, i18, i19);
        }
        if (iM192t > 1) {
            int i33 = (int) j2;
            Font font2 = graphics.getFont();
            graphics.setFont(font);
            int[] iArrM193a4 = MmpContact.m193a(0);
            int iM317a19 = (int) AppController.m317a(iArrM193a4[0], i);
            int iM317a20 = (int) AppController.m317a(iArrM193a4[1], i);
            if (Utils.m504c(((int) j) - iM317a19) < 7 && Utils.m504c(i33 - iM317a20) < 7) {
                int height = font.getHeight();
                int clipHeight = (graphics.getClipHeight() - height) - 1;
                int i34 = 22;
                if (AppState.m587e(230)) {
                    clipHeight -= (height > 18 ? height : 18) + 2;
                    i34 = 22 - 20;
                }
                String strM584b = AppState.m584b(981);
                int iStringWidth = font.stringWidth(strM584b) + 6;
                int iM586d2 = AppState.m586d(72);
                graphics.setColor(AppState.m586d(iM586d2 + 5050));
                graphics.fillRoundRect(i34, clipHeight, iStringWidth, height, 10, 10);
                graphics.setColor(AppState.m586d(iM586d2 + 4914));
                graphics.drawRoundRect(i34, clipHeight, iStringWidth, height, 10, 10);
                graphics.drawString(strM584b, i34 + 3, clipHeight, 20);
                graphics.setFont(font2);
            }
        }
        graphics.setColor(color);
    }
}
