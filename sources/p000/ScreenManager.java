package p000;

import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: ad */
/* loaded from: MobileAgent_3.9.jar:ad.class */
public abstract class ScreenManager {
    /* renamed from: a */
    public static final void initializeFonts() {
        int iM586d = AppState.getInt(73);
        int i = iM586d == 0 ? 8 : iM586d == 1 ? 0 : 16;
        GraphicsContext c0012al = new GraphicsContext(0, i);
        AppState.pool[1273] = c0012al;
        GraphicsContext c0012al2 = new GraphicsContext(1, i);
        AppState.pool[1274] = c0012al2;
        GraphicsContext c0012al3 = AppState.getBool(70) ? new GraphicsContext(2, i) : c0012al;
        AppState.pool[1275] = c0012al3;
        AppState.pool[1276] = c0012al;
        AppState.pool[1277] = c0012al;
        AppState.pool[1278] = c0012al2;
        AppState.setInt(1450, c0012al.font.getHeight());
        AppState.setInt(1453, c0012al.font.getHeight());
        AppState.setInt(1454, c0012al.font.getHeight());
        AppState.setInt(1455, c0012al2.font.getHeight());
        AppState.setInt(1451, c0012al2.font.getHeight());
        AppState.setInt(1452, c0012al3.font.getHeight());
        Vector vectorM614m = AppState.getVector(1272);
        int size = vectorM614m.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((Screen) vectorM614m.elementAt(size)).m258q();
            }
        }
    }

    /* renamed from: b */
    public static final Screen getCurrentScreen() {
        Vector vectorM614m = AppState.getVector(1272);
        if (vectorM614m.isEmpty()) {
            return null;
        }
        return (Screen) vectorM614m.lastElement();
    }

    /* renamed from: c */
    public static final String getCurrentTitle() {
        if (AppState.getVector(1272).size() > 0) {
            return getCurrentScreen().m220b();
        }
        return null;
    }

    /* renamed from: d */
    public static final int getCurrentWidth() {
        if (AppState.getVector(1272).size() > 0) {
            return getCurrentScreen().m221c();
        }
        return 200;
    }

    /* renamed from: e */
    public static final MenuItem getCurrentMenuItem() {
        if (AppState.getVector(1272).size() > 0) {
            return getCurrentScreen().m222d();
        }
        return null;
    }

    /* renamed from: a */
    public static final void pushScreen(Screen c0013am) {
        Vector vectorM614m = AppState.getVector(1272);
        while (vectorM614m.size() > 0) {
            ScreenBuilder.onScreenClosed();
        }
        vectorM614m.addElement(c0013am);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0166  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void showScreen(Screen c0013am) {
        Screen c0013am2 = null;
        Vector vectorM614m = AppState.getVector(1272);
        int size = vectorM614m.size() - 1;
        int i = size >= 0 ? ((Screen) vectorM614m.elementAt(size)).f94a : -1;
        if (i == 137 || i == 63) {
            Screen c0013am3 = (Screen) vectorM614m.elementAt(size);
            c0013am2 = c0013am3;
            vectorM614m.removeElement(c0013am3);
        }
        int i2 = c0013am.f94a;
        if (i2 != 112) {
            int size2 = vectorM614m.size();
            for (int i3 = 0; i3 < size2; i3++) {
                if (((Screen) vectorM614m.elementAt(i3)).f94a == i2) {
                    size2 = i3;
                }
            }
            while (vectorM614m.size() > size2) {
                ScreenBuilder.onScreenClosed();
            }
        }
        int i4 = c0013am.f97d;
        if (((1 << i4) & 3484) != 0) {
            c0013am.m226f();
        }
        int i5 = c0013am.f95b;
        int i6 = c0013am.f96c;
        int iM586d = AppState.getInt(1528) - i5;
        int iM605e = AppState.getHeight() - i6;
        switch (i4) {
            case 2:
            case 7:
            case 8:
                c0013am.m266a(iM586d >> 1, iM605e >> 1);
                break;
            case 3:
                c0013am.m266a(0, iM605e);
                break;
            case 4:
                c0013am.m266a(iM586d, iM605e);
                break;
            case 10:
                Screen c0013amM66b = getCurrentScreen();
                if (c0013amM66b != null) {
                    int iM586d2 = c0013amM66b.f98e + c0013amM66b.f95b;
                    int iM265r = c0013amM66b.m265r();
                    if (iM586d2 + c0013am.f95b > AppState.getInt(1528)) {
                        iM586d2 = AppState.getInt(1528) - c0013am.f95b;
                    }
                    if (iM265r + c0013am.f96c > AppState.getHeight()) {
                        iM265r = AppState.getHeight() - c0013am.f96c;
                    }
                    c0013am.m266a(iM586d2, iM265r);
                    break;
                }
                break;
            case 11:
                c0013am.m266a(iM586d >> 1, (AppState.getHeight() - i6) - (i6 / 10));
                break;
        }
        int size3 = vectorM614m.size();
        for (int i7 = 0; i7 < size3; i7++) {
            if (((Screen) vectorM614m.elementAt(i7)).f97d == 7) {
                size3 = i7;
            }
        }
        while (vectorM614m.size() > size3) {
            ScreenBuilder.onScreenClosed();
        }
        c0013am.m235n();
        vectorM614m.addElement(c0013am);
        if (c0013am2 != null) {
            vectorM614m.addElement(c0013am2);
        }
    }

    /* renamed from: a */
    public static final boolean hasScreen(int i) {
        Vector vectorM614m = AppState.getVector(1272);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return false;
            }
        } while (((Screen) vectorM614m.elementAt(size)).f94a != i);
        return true;
    }

    /* renamed from: f */
    public static final int getCenterOffset() {
        return Utils.max(0, (AppState.getInt(1450) - 16) >> 1);
    }

    /* renamed from: g */
    public static final int handleScreenClose() {
        if (!AppState.getBool(1543)) {
            return AppController.m338l(470);
        }
        AppState.setScreen(new Object());
        return 0;
    }

    /* renamed from: b */
    public static final Screen createScreen(int i) {
        Screen c0013am;
        int i2 = i + 1;
        String strM522f = Utils.defaultStr(AppState.getString(AppState.getInt(i)));
        int i3 = i2 + 1;
        int iM586d = AppState.getInt(i2);
        int i4 = i3 + 1;
        int iM586d2 = AppState.getInt(i3);
        boolean z = (iM586d2 & 16) != 0;
        int i5 = iM586d2 & 15;
        int i6 = i4 + 1;
        int iM586d3 = AppState.getInt(i4);
        int i7 = i6 + 1;
        int iM586d4 = AppState.getInt(i6);
        int i8 = i7 + 1;
        int iM586d5 = AppState.getInt(i7);
        int i9 = i8 + 1;
        int iM586d6 = AppState.getInt(i8);
        int i10 = i9 + 1;
        int iM586d7 = AppState.getInt(i9);
        int i11 = i10 + 1;
        int iM586d8 = AppState.getInt(i10);
        int iM79a = i11 + 1;
        int iM586d9 = AppState.getInt(i11);
        int iM586d10 = AppState.getInt(1528);
        int iM605e = AppState.getHeight();
        switch (i5) {
            case 0:
            case 1:
                c0013am = new Screen(0, iM586d, iM586d10, iM605e, true);
                break;
            case 2:
            case 3:
            case 4:
            case 10:
            case 11:
                c0013am = new Screen(0, iM586d, (iM586d10 * 9) / 10, (iM605e * 9) / 10, true);
                break;
            case 5:
            case 9:
                c0013am = new Screen(0, iM586d, iM586d10, iM605e, false);
                break;
            case 6:
            case 12:
                c0013am = new Screen(1, iM586d, iM586d10, iM605e, true);
                break;
            case 7:
            case 8:
                c0013am = new Screen(0, iM586d, (iM586d10 * 9) / 10, (iM605e * 9) / 10, false);
                break;
            default:
                c0013am = null;
                break;
        }
        if (i5 != 3 && i5 != 4 && i5 != 2 && i5 != 11 && i5 != 10 && i5 != 8) {
            if (i5 != 7) {
                c0013am.m224a(iM586d3, strM522f);
            } else {
                c0013am.m225a(new MenuItem(0, strM522f).addText(strM522f, 1, 0));
            }
        }
        c0013am.f125x = z;
        c0013am.f100g = i;
        for (int i12 = 0; i12 < iM586d9; i12++) {
            iM79a = parseScreenItem(c0013am, iM79a, iM586d);
        }
        Screen c0013amM259a = c0013am.m259a(iM586d4 > 0 ? AppState.getString(iM586d4) : null, iM586d5 > 0 ? AppState.getString(iM586d5) : null, iM586d6, iM586d7, iM586d8);
        c0013amM259a.f97d = i5;
        return c0013amM259a;
    }

    /* renamed from: c */
    public static final Screen createDialogScreen(int i) {
        Screen c0013am = new Screen(0, i, (AppState.getInt(1528) * 9) / 10, (AppState.getHeight() * 9) / 10, true);
        c0013am.f97d = 2;
        c0013am.f125x = true;
        return c0013am;
    }

    /* renamed from: h */
    public static final boolean hasModal() {
        Vector vectorM614m = AppState.getVector(1272);
        int size = vectorM614m.size();
        do {
            size--;
            if (size <= 0) {
                return false;
            }
        } while (((Screen) vectorM614m.elementAt(size)).f99f != 0);
        return true;
    }

    /* renamed from: a */
    private static final int addItemToScreen(boolean z, Screen c0013am, int i, boolean z2) {
        int i2 = i + 1;
        int iM586d = AppState.getInt(i);
        int i3 = i2 + 1;
        int iM586d2 = AppState.getInt(i2);
        int i4 = i3 + 1;
        int iM586d3 = AppState.getInt(i3);
        if (z) {
            if (z2) {
                c0013am.m252b(iM586d2, iM586d3, iM586d);
            } else {
                c0013am.m249a(iM586d2, iM586d3, iM586d);
            }
        }
        return i4;
    }

    /* renamed from: a */
    private static final int parseScreenItem(Screen c0013am, int i, int i2) {
        int i3;
        Object objM522f;
        int i4;
        String strM522f;
        int i5 = i + 1;
        int iM586d = AppState.getInt(i);
        boolean z = (iM586d & 16) != 0;
        boolean z2 = (iM586d & 32) != 0;
        switch (iM586d & 15) {
            case 0:
                boolean zM587e = z;
                int i6 = i5;
                if (z2) {
                    i6++;
                    zM587e = AppState.getBool(AppState.getInt(i6));
                }
                int i7 = i6;
                int i8 = i6 + 1;
                int iM586d2 = AppState.getInt(i7);
                int i9 = i8 + 1;
                int iM586d3 = AppState.getInt(i8);
                int i10 = i9 + 1;
                int iM586d4 = AppState.getInt(i9);
                if (zM587e) {
                    c0013am.m252b(iM586d3, iM586d4, iM586d2);
                } else {
                    c0013am.m249a(iM586d3, iM586d4, iM586d2);
                }
                return i10;
            case 1:
                int i11 = i5 + 1;
                MenuItem c0032cM901a = MenuItem.createSeparator().addText(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(Utils.defaultStr(AppState.getString(AppState.getInt(i5)))).append(' ')), 0, 0);
                int i12 = i11 + 1;
                String strM522f2 = Utils.defaultStr(AppState.getString(AppState.getInt(i11)));
                if (!StringUtils.isEmpty(strM522f2)) {
                    c0032cM901a.addText(strM522f2, 0, 6);
                }
                c0013am.m225a(c0032cM901a);
                return i12;
            case 2:
                int i13 = i5 + 1;
                String strM522f3 = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                int i14 = i13 + 1;
                c0013am.m225a(MenuItem.createCheckbox(strM522f3, AppState.getBool(AppState.getInt(i13))));
                return i14;
            case 3:
                int i15 = i5 + 1;
                String strM522f4 = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                int i16 = i15 + 1;
                Vector vectorM512e = Utils.splitByNull(Utils.defaultStr(AppState.getString(AppState.getInt(i15))));
                int i17 = i16 + 1;
                c0013am.m225a(new MenuItem(9, strM522f4).setChoices(vectorM512e, AppState.getInt(AppState.getInt(i16)), strM522f4));
                return i17;
            case 4:
                int i18 = i5 + 1;
                c0013am.m225a(MenuItem.createSeparator().addText(Utils.defaultStr(AppState.getString(AppState.getInt(i5))), 1, 0));
                return i18;
            case 5:
                if (i2 == 49) {
                    i3 = i5 + 1;
                    int iM586d5 = AppState.getInt(i5);
                    objM522f = (iM586d5 < 268 || iM586d5 > 304) ? (iM586d5 < 161 || iM586d5 > 210) ? AppState.getString(iM586d5) : ResourceManager.integerOf(iM586d5) : ResourceManager.integerOf(iM586d5);
                } else {
                    i3 = i5 + 1;
                    objM522f = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                }
                Object obj = objM522f;
                int i19 = i3;
                int i20 = i3 + 1;
                int iM586d6 = AppState.getInt(i19);
                int i21 = i20 + 1;
                String strM522f5 = Utils.defaultStr(AppState.getString(AppState.getInt(i20)));
                int i22 = i21 + 1;
                int iM586d7 = AppState.getInt(i21);
                if (iM586d7 == 2) {
                    int i23 = i22 + 3;
                    i4 = i23 + 1;
                    int iM586d8 = AppState.getInt(AppState.getInt(i23));
                    strM522f = iM586d8 >= 0 ? StringUtils.intern(Integer.toString(iM586d8)) : AppState.emptyStr;
                } else {
                    i4 = i22 + 1;
                    strM522f = Utils.defaultStr(Utils.defaultStr(AppState.getString(AppState.getInt(i22))));
                }
                c0013am.m225a(new MenuItem(15, obj instanceof String ? (String) obj : AppState.emptyStr).setAction(obj, strM522f, ResourceManager.integerOf(iM586d6), ResourceManager.integerOf(iM586d7), strM522f5));
                return i4;
            case 6:
                int i24 = i5 + 1;
                c0013am.m225a(MenuItem.createSeparator().setLabel(Utils.defaultStr(AppState.getString(AppState.getInt(i5)))));
                return i24;
            case 7:
                return addItemToScreen(AppState.getBool(AppState.getInt(i5)), c0013am, i5 + 1, z);
            case 8:
                return addItemToScreen(!AppState.getBool(AppState.getInt(i5)), c0013am, i5 + 1, z);
            case 9:
                String strM522f6 = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                String strM522f7 = Utils.defaultStr(AppState.getString(AppState.getInt(i5 + 1)));
                MenuItem c0032cM901a2 = new MenuItem(4, (String) null).clear().setIcon(221).addText(Utils.nonEmpty(strM522f7) ? strM522f7 : strM522f6, 1, 7);
                c0032cM901a2.data = new String[]{strM522f6, strM522f7};
                c0013am.m225a(c0032cM901a2);
                return i5 + 2;
            case 10:
                String strM522f8 = Utils.defaultStr(AppState.getString(AppState.getInt(i5)));
                MenuItem c0032c = new MenuItem(5, (String) null);
                c0032c.clear();
                c0032c.setIcon(219);
                if (Utils.nonEmpty(strM522f8)) {
                    int iIndexOf = strM522f8.indexOf(0);
                    c0032c.addText(iIndexOf < 0 ? strM522f8 : StringUtils.prefix(strM522f8, iIndexOf), 1, 7);
                } else {
                    c0032c.setDefaultFont();
                }
                c0032c.data = strM522f8;
                c0013am.m225a(c0032c);
                return i5 + 1;
            case 11:
                c0013am.m225a(MenuItem.createGraphics(new GraphicsContext((Image) AppState.pool[AppState.getInt(i5)])));
                return i5 + 1;
            default:
                parseScreenItem(c0013am, AppState.getInt(i5), i2);
                return i5 + 1;
        }
    }
}
