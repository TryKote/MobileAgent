package p000;

import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

/* renamed from: ak */
/* loaded from: MobileAgent_3.9.jar:ak.class */
public final class MainCanvas extends Canvas implements CommandListener {

    /* renamed from: e */
    private Command okCommand;

    /* renamed from: f */
    private Command cancelCommand;

    /* renamed from: h */
    private String okLabel;

    /* renamed from: i */
    private String cancelLabel;

    /* renamed from: j */
    private boolean isShown;

    /* renamed from: k */
    private int parentWidth;

    /* renamed from: l */
    private int parentHeight;

    /* renamed from: a */
    public static int pointerDownX;

    /* renamed from: b */
    public static int pointerDownY;

    /* renamed from: o */
    private boolean fullScreen;

    /* renamed from: c */
    public static long pointerDownTime;

    /* renamed from: d */
    public static boolean pointerDragged;

    /* renamed from: m */
    private int canvasWidth = getWidth();

    /* renamed from: n */
    private int canvasHeight = getHeight();

    /* renamed from: g */
    private GraphicsContext graphicsContext = new GraphicsContext();

    public MainCanvas(int i, int i2) {
        this.parentWidth = i;
        this.parentHeight = i2;
        updateFullScreenMode();
        AppState.setDimensions(getEffectiveWidth(), getEffectiveHeight());
        AppState.setScreen(this);
    }

    /* renamed from: c */
    private int getEffectiveHeight() {
        return this.fullScreen ? this.parentHeight : this.canvasHeight;
    }

    /* renamed from: d */
    private int getEffectiveWidth() {
        return this.fullScreen ? this.parentWidth : this.canvasWidth;
    }

    public final void hideNotify() {
        this.isShown = false;
        IOUtils.postBackEvent();
        AppController.f147a[0] = 0;
    }

    public final void showNotify() {
        this.isShown = true;
        IOUtils.postBackEvent();
        AppController.m304a(0, AppController.m376E());
    }

    public final boolean isShown() {
        return this.isShown && super/*javax.microedition.lcdui.Displayable*/.isShown();
    }

    /* renamed from: a */
    public final void updateFullScreenMode() {
        boolean zM587e = AppState.getBool(71);
        if (zM587e) {
            if (this.okCommand != null) {
                removeCommand(this.okCommand);
                this.okCommand = null;
            }
            if (this.cancelCommand != null) {
                removeCommand(this.cancelCommand);
                this.cancelCommand = null;
            }
        }
        setFullScreenMode(zM587e);
        this.fullScreen = zM587e;
        AppState.setDimensions(getEffectiveWidth(), getEffectiveHeight());
    }

    public final void paint(Graphics graphics) {
        GraphicsContext c0012al = this.graphicsContext;
        c0012al.graphics = graphics;
        try {
            synchronized (AppController.f150d) {
                if (!AppController.f151e) {
                    XmppContactGroup.incrementCacheCounter();
                    Vector vectorM614m = AppState.getVector(1272);
                    int size = vectorM614m.size();
                    if (size > 0) {
                        int i = size;
                        do {
                            i--;
                            if (i < 0) {
                                break;
                            }
                        } while (((Screen) vectorM614m.elementAt(i)).offsetY != 0);
                        int iM586d = AppState.getInt(1528);
                        int iM586d2 = AppState.getInt(1529);
                        c0012al.setClip(0, 0, iM586d, iM586d2);
                        c0012al.setColorFromPalette(14);
                        c0012al.fillRect(0, 0, iM586d, iM586d2);
                        while (i < size) {
                            boolean z = i == size - 1;
                            boolean z2 = z;
                            if (z && AppState.getBool(66) && AppState.getBool(1535)) {
                                int iM586d3 = AppState.getInt(1528);
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
                                Graphics graphics2 = c0012al.graphics;
                                int iM605e = AppState.getHeight();
                                while (true) {
                                    iM605e--;
                                    if (iM605e < 0) {
                                        break;
                                    } else {
                                        graphics2.drawRGB(iArr, 0, iM586d3, 0, iM605e, iM586d3, 1, true);
                                    }
                                }
                            }
                            ((Screen) vectorM614m.elementAt(i)).paint(c0012al, z2, false);
                            c0012al.setClip(0, 0, iM586d, iM586d2);
                            i++;
                        }
                    }
                    if (ScreenManager.hasModal()) {
                        c0012al.setClip(0, 0, 2048, 2048);
                        int iM586d4 = AppState.getInt(1528) - 17;
                        if (AppController.m413P() != 0) {
                            c0012al.drawIcon(16384, iM586d4, 1);
                            iM586d4 -= 17;
                        }
                        if (AppController.m410N()) {
                            c0012al.drawIcon(16385, iM586d4, 1);
                        }
                    }
                    XmppContactGroup.cleanupExpiredImages();
                }
            }
        } catch (Throwable unused) {
        }
        AppController.f153g = false;
    }

    public final void keyPressed(int i) {
        handleKeyInput(i, 0);
    }

    public final void keyRepeated(int i) {
        if (AppState.getVector(1266).size() < 3) {
            handleKeyInput(i, 1);
        }
    }

    public final void keyReleased(int i) {
        AppController.m304a(3, 10000L);
        IOUtils.postBackEvent();
    }

    /* renamed from: a */
    private final String getKeyNameUpper(int i) {
        try {
            return getKeyName(i).toUpperCase();
        } catch (Throwable unused) {
            return AppState.emptyStr;
        }
    }

    /* renamed from: a */
    private final void handleKeyInput(int i, int i2) {
        AppController.m357z();
        AppController.m304a(0, AppController.m376E());
        AppController.m304a(3, 10000L);
        int gameAction = 0;
        boolean zM587e = AppState.getBool(1511);
        try {
            gameAction = getGameAction(i);
        } catch (Throwable unused) {
        }
        if (gameAction == 1 || gameAction == 2 || gameAction == 5 || gameAction == 6) {
            IOUtils.postNavigationEvent(i, gameAction, i2);
            return;
        }
        if (gameAction == 8 && (i == -6 || i == -7 || i == -1 || i == -4)) {
            gameAction = 0;
        }
        if (gameAction == 8) {
            if (!zM587e) {
                IOUtils.postSelectEvent();
                return;
            } else {
                AppState.setBool(218, false);
                IOUtils.postOkEvent();
                return;
            }
        }
        if ((i >= 48 && i <= 57) || i == 42 || i == 35) {
            IOUtils.postNavigationEvent(i, gameAction, i2);
            return;
        }
        if (i2 == 0) {
            try {
                if (AppState.getBool(71)) {
                    String strM202a = getKeyNameUpper(i);
                    if (i == -6 || strM202a.indexOf("SEND") >= 0 || strM202a.indexOf("SOFT1") >= 0 || strM202a.equals("SOFTKEY 1")) {
                        IOUtils.postOkEvent();
                        return;
                    }
                    if (i == -7 || i == 11 || strM202a.indexOf("CLEAR") >= 0 || strM202a.indexOf("SOFT2") >= 0 || strM202a.equals("SOFTKEY 4")) {
                        if (zM587e) {
                            AppState.setBool(218, false);
                        }
                        IOUtils.postCancelEvent();
                    } else if (zM587e) {
                        AppState.setBool(218, false);
                        IOUtils.postOkEvent();
                    }
                }
            } catch (Throwable unused2) {
            }
        }
    }

    public final void pointerPressed(int i, int i2) {
        pointerDownX = i;
        pointerDownY = i2;
        AppController.m304a(3, 10000L);
        AppController.m304a(0, AppController.m376E());
        Vector vectorM614m = AppState.getVector(1266);
        synchronized (vectorM614m) {
            vectorM614m.addElement(new int[]{5, i, i2});
        }
        pointerDownTime = System.currentTimeMillis();
        pointerDragged = false;
        AppController.m357z();
    }

    public final void pointerDragged(int i, int i2) {
        if (Utils.abs(i - pointerDownX) > 5 || Utils.abs(i2 - pointerDownY) > 5) {
            int i3 = pointerDownX;
            int i4 = pointerDownY;
            Vector vectorM614m = AppState.getVector(1266);
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
            pointerDragged = true;
        }
    }

    public final void pointerReleased(int i, int i2) {
        int i3 = pointerDownX;
        int i4 = pointerDownY;
        boolean z = pointerDragged;
        Vector vectorM614m = AppState.getVector(1266);
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
        pointerDownTime = 0L;
    }

    public final void commandAction(Command command, Displayable displayable) {
        AppController.m304a(3, 10000L);
        if (command != null) {
            if (command == this.okCommand) {
                IOUtils.postOkEvent();
            } else if (command == this.cancelCommand) {
                IOUtils.postCancelEvent();
            }
        }
    }

    /* renamed from: b */
    public final MainCanvas updateCommands() {
        String str = this.okLabel;
        this.okLabel = null;
        String str2 = this.cancelLabel;
        this.cancelLabel = null;
        setCommands(str, str2);
        return this;
    }

    /* renamed from: a */
    public final void setCommands(String str, String str2) {
        if (this.okLabel == str && this.cancelLabel == str2) {
            return;
        }
        this.okLabel = str;
        this.cancelLabel = str2;
        if (this.okCommand != null) {
            removeCommand(this.okCommand);
        }
        if (this.cancelCommand != null) {
            removeCommand(this.cancelCommand);
        }
        boolean zM587e = AppState.getBool(65);
        if (str != null) {
            Command command = new Command(str, zM587e ? 3 : 4, 1);
            this.okCommand = command;
            addCommand(command);
        }
        if (str2 != null) {
            Command command2 = new Command(str2, zM587e ? 4 : 3, 1);
            this.cancelCommand = command2;
            addCommand(command2);
        }
        setCommandListener(this);
        AppController.f153g = true;
    }
}
