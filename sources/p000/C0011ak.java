package p000;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

/* renamed from: ak */
/* loaded from: MobileAgent_3.9.jar:ak.class */
public final class C0011ak extends Canvas implements CommandListener {

    /* renamed from: e */
    private Command f76e;

    /* renamed from: f */
    private Command f77f;

    /* renamed from: h */
    private String f79h;

    /* renamed from: i */
    private String f80i;

    /* renamed from: j */
    private boolean f81j;

    /* renamed from: k */
    private int f82k;

    /* renamed from: l */
    private int f83l;

    /* renamed from: a */
    public static int f86a;

    /* renamed from: b */
    public static int f87b;

    /* renamed from: o */
    private boolean f88o;

    /* renamed from: c */
    public static long f89c;

    /* renamed from: d */
    public static boolean f90d;

    /* renamed from: m */
    private int f84m = getWidth();

    /* renamed from: n */
    private int f85n = getHeight();

    /* renamed from: g */
    private C0012al f78g = new C0012al();

    public C0011ak(int i, int i2) {
        this.f82k = i;
        this.f83l = i2;
        m201a();
        AppState.m606f(m200d(), m199c());
        AppState.m604b(this);
    }

    /* renamed from: c */
    private int m199c() {
        return this.f88o ? this.f83l : this.f85n;
    }

    /* renamed from: d */
    private int m200d() {
        return this.f88o ? this.f82k : this.f84m;
    }

    public final void hideNotify() {
        this.f81j = false;
        C0029bb.m776j();
        C0015ao.f147a[0] = 0;
    }

    public final void showNotify() {
        this.f81j = true;
        C0029bb.m776j();
        C0015ao.m304a(0, C0015ao.m376E());
    }

    public final boolean isShown() {
        return this.f81j && super/*javax.microedition.lcdui.Displayable*/.isShown();
    }

    /* renamed from: a */
    public final void m201a() {
        boolean zM587e = AppState.m587e(71);
        if (zM587e) {
            if (this.f76e != null) {
                removeCommand(this.f76e);
                this.f76e = null;
            }
            if (this.f77f != null) {
                removeCommand(this.f77f);
                this.f77f = null;
            }
        }
        setFullScreenMode(zM587e);
        this.f88o = zM587e;
        AppState.m606f(m200d(), m199c());
    }

    public final void paint(Graphics graphics) {
        C0012al c0012al = this.f78g;
        c0012al.f92b = graphics;
        try {
            synchronized (C0015ao.f150d) {
                if (!C0015ao.f151e) {
                    C0036g.m1020e();
                    Vector vectorM614m = AppState.m614m(1272);
                    int size = vectorM614m.size();
                    if (size > 0) {
                        int i = size;
                        do {
                            i--;
                            if (i < 0) {
                                break;
                            }
                        } while (((C0013am) vectorM614m.elementAt(i)).f99f != 0);
                        int iM586d = AppState.m586d(1528);
                        int iM586d2 = AppState.m586d(1529);
                        c0012al.m208a(0, 0, iM586d, iM586d2);
                        c0012al.m207b(14);
                        c0012al.m210c(0, 0, iM586d, iM586d2);
                        while (i < size) {
                            boolean z = i == size - 1;
                            boolean z2 = z;
                            if (z && AppState.m587e(66) && AppState.m587e(1535)) {
                                int iM586d3 = AppState.m586d(1528);
                                int[] iArr = new int[iM586d3];
                                int i2 = iM586d3;
                                while (true) {
                                    i2--;
                                    if (i2 < 0) {
                                        break;
                                    } else {
                                        iArr[i2] = 2030043135;
                                    }
                                }
                                Graphics graphics2 = c0012al.f92b;
                                int iM605e = AppState.m605e();
                                while (true) {
                                    iM605e--;
                                    if (iM605e < 0) {
                                        break;
                                    } else {
                                        graphics2.drawRGB(iArr, 0, iM586d3, 0, iM605e, iM586d3, 1, true);
                                    }
                                }
                            }
                            ((C0013am) vectorM614m.elementAt(i)).m227a(c0012al, z2, false);
                            c0012al.m208a(0, 0, iM586d, iM586d2);
                            i++;
                        }
                    }
                    if (AbstractC0004ad.m77h()) {
                        c0012al.m208a(0, 0, 2048, 2048);
                        int iM586d4 = AppState.m586d(1528) - 17;
                        if (C0015ao.m413P() != 0) {
                            c0012al.m216a(16384, iM586d4, 1);
                            iM586d4 -= 17;
                        }
                        if (C0015ao.m410N()) {
                            c0012al.m216a(16385, iM586d4, 1);
                        }
                    }
                    C0036g.m1022f();
                }
            }
        } catch (Throwable unused) {
        }
        C0015ao.f153g = false;
    }

    public final void keyPressed(int i) {
        m203a(i, 0);
    }

    public final void keyRepeated(int i) {
        if (AppState.m614m(1266).size() < 3) {
            m203a(i, 1);
        }
    }

    public final void keyReleased(int i) {
        C0015ao.m304a(3, 10000L);
        C0029bb.m776j();
    }

    /* renamed from: a */
    private final String m202a(int i) {
        try {
            return getKeyName(i).toUpperCase();
        } catch (Throwable unused) {
            return AppState.f181d;
        }
    }

    /* renamed from: a */
    private final void m203a(int i, int i2) {
        C0015ao.m357z();
        C0015ao.m304a(0, C0015ao.m376E());
        C0015ao.m304a(3, 10000L);
        int gameAction = 0;
        boolean zM587e = AppState.m587e(1511);
        try {
            gameAction = getGameAction(i);
        } catch (Throwable unused) {
        }
        if (gameAction == 1 || gameAction == 2 || gameAction == 5 || gameAction == 6) {
            C0029bb.m777a(i, gameAction, i2);
            return;
        }
        if (gameAction == 8 && (i == -6 || i == -7 || i == -1 || i == -4)) {
            gameAction = 0;
        }
        if (gameAction == 8) {
            if (!zM587e) {
                C0029bb.m775i();
                return;
            } else {
                AppState.m599a(218, false);
                C0029bb.m773g();
                return;
            }
        }
        if ((i >= 48 && i <= 57) || i == 42 || i == 35) {
            C0029bb.m777a(i, gameAction, i2);
            return;
        }
        if (i2 == 0) {
            try {
                if (AppState.m587e(71)) {
                    String strM202a = m202a(i);
                    if (i == -6 || strM202a.indexOf("SEND") >= 0 || strM202a.indexOf("SOFT1") >= 0 || strM202a.equals("SOFTKEY 1")) {
                        C0029bb.m773g();
                        return;
                    }
                    if (i == -7 || i == 11 || strM202a.indexOf("CLEAR") >= 0 || strM202a.indexOf("SOFT2") >= 0 || strM202a.equals("SOFTKEY 4")) {
                        if (zM587e) {
                            AppState.m599a(218, false);
                        }
                        C0029bb.m774h();
                    } else if (zM587e) {
                        AppState.m599a(218, false);
                        C0029bb.m773g();
                    }
                }
            } catch (Throwable unused2) {
            }
        }
    }

    public final void pointerPressed(int i, int i2) {
        f86a = i;
        f87b = i2;
        C0015ao.m304a(3, 10000L);
        C0015ao.m304a(0, C0015ao.m376E());
        Vector vectorM614m = AppState.m614m(1266);
        synchronized (vectorM614m) {
            vectorM614m.addElement(new int[]{5, i, i2});
        }
        f89c = System.currentTimeMillis();
        f90d = false;
        C0015ao.m357z();
    }

    public final void pointerDragged(int i, int i2) {
        if (Utils.m504c(i - f86a) > 5 || Utils.m504c(i2 - f87b) > 5) {
            int i3 = f86a;
            int i4 = f87b;
            Vector vectorM614m = AppState.m614m(1266);
            synchronized (vectorM614m) {
                int iM541c = Utils.m541c(vectorM614m);
                while (true) {
                    iM541c--;
                    if (iM541c < 0) {
                        break;
                    }
                    Object objElementAt = vectorM614m.elementAt(iM541c);
                    if (objElementAt instanceof int[]) {
                        int i5 = ((int[]) objElementAt)[0];
                        if (i5 == 6) {
                            vectorM614m.removeElementAt(iM541c);
                        } else if (i5 == 5 || i5 == 7) {
                            break;
                        }
                    }
                }
                vectorM614m.addElement(new int[]{6, i, i2, i3, i4});
            }
            f90d = true;
        }
    }

    public final void pointerReleased(int i, int i2) {
        int i3 = f86a;
        int i4 = f87b;
        boolean z = f90d;
        Vector vectorM614m = AppState.m614m(1266);
        synchronized (vectorM614m) {
            int[] iArr = new int[6];
            iArr[0] = 7;
            iArr[1] = i;
            iArr[2] = i2;
            iArr[3] = i3;
            iArr[4] = i4;
            iArr[5] = z ? 1 : 0;
            vectorM614m.addElement(iArr);
        }
        f89c = 0L;
    }

    public final void commandAction(Command command, Displayable displayable) {
        C0015ao.m304a(3, 10000L);
        if (command != null) {
            if (command == this.f76e) {
                C0029bb.m773g();
            } else if (command == this.f77f) {
                C0029bb.m774h();
            }
        }
    }

    /* renamed from: b */
    public final C0011ak m204b() {
        String str = this.f79h;
        this.f79h = null;
        String str2 = this.f80i;
        this.f80i = null;
        m205a(str, str2);
        return this;
    }

    /* renamed from: a */
    public final void m205a(String str, String str2) {
        if (this.f79h == str && this.f80i == str2) {
            return;
        }
        this.f79h = str;
        this.f80i = str2;
        if (this.f76e != null) {
            removeCommand(this.f76e);
        }
        if (this.f77f != null) {
            removeCommand(this.f77f);
        }
        boolean zM587e = AppState.m587e(65);
        if (str != null) {
            Command command = new Command(str, zM587e ? 3 : 4, 1);
            this.f76e = command;
            addCommand(command);
        }
        if (str2 != null) {
            Command command2 = new Command(str2, zM587e ? 4 : 3, 1);
            this.f77f = command2;
            addCommand(command2);
        }
        setCommandListener(this);
        C0015ao.f153g = true;
    }
}
