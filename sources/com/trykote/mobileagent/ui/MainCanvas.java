package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.core.event.PointerEvent;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;

public final class MainCanvas extends Canvas implements CommandListener {

    // Transparent overlay color (ARGB: ~120 alpha, white)
    private static final int COLOR_TRANSPARENT_OVERLAY = 2030043135;

    // Maximum clip dimension for full-screen icon drawing
    private static final int MAX_CLIP_SIZE = 2048;

    // Icon width (16) + spacing (1)
    private static final int ICON_STEP = 17;

    // Maximum events in queue before dropping key repeats
    private static final int EVENT_QUEUE_LIMIT = 3;

    // Game action constants (javax.microedition.lcdui.Canvas)
    private static final int ACTION_UP = 1;
    private static final int ACTION_DOWN = 2;
    private static final int ACTION_LEFT = 5;
    private static final int ACTION_RIGHT = 6;
    private static final int ACTION_FIRE = 8;

    // Soft key codes
    private static final int KEY_SOFT_LEFT = -6;
    private static final int KEY_SOFT_RIGHT = -7;
    private static final int KEY_UP = -1;
    private static final int KEY_SELECT = -4;

    // ASCII key ranges
    private static final int KEY_DIGIT_0 = 48;
    private static final int KEY_DIGIT_9 = 57;
    private static final int KEY_STAR = 42;
    private static final int KEY_HASH = 35;

    // Clear/delete key
    private static final int KEY_CLEAR = 11;

    // Minimum pointer movement to register as drag (pixels)
    private static final int DRAG_THRESHOLD = 5;

    // MIDP Command types
    private static final int COMMAND_SCREEN = 3;
    private static final int COMMAND_BACK = 4;

    // Blinking icon IDs for notification overlays
    private static final int ICON_UNREAD_BLINK = 16384;
    private static final int ICON_CONNECTION_BLINK = 16385;

    private Command okCommand;

    private Command cancelCommand;

    private String okLabel;

    private String cancelLabel;

    private boolean isShown;

    private int parentWidth;

    private int parentHeight;

    public static int pointerDownX;

    public static int pointerDownY;

    private boolean fullScreen;

    public static long pointerDownTime;

    public static boolean pointerDragged;

    private int canvasWidth = getWidth();

    private int canvasHeight = getHeight();

    private GraphicsContext graphicsContext = new GraphicsContext();

    public MainCanvas(int i, int i2) {
        this.parentWidth = i;
        this.parentHeight = i2;
        updateFullScreenMode();
        UIState.setDimensions(getEffectiveWidth(), getEffectiveHeight());
        AppState.setScreen(this);
    }

    private int getEffectiveHeight() {
        return this.fullScreen ? this.parentHeight : this.canvasHeight;
    }

    private int getEffectiveWidth() {
        return this.fullScreen ? this.parentWidth : this.canvasWidth;
    }

    public final void hideNotify() {
        this.isShown = false;
        EventDispatcher.postBackEvent();
        TimerManager.timers[TimerManager.SLOT_BACKLIGHT] = 0;
    }

    public final void showNotify() {
        this.isShown = true;
        EventDispatcher.postBackEvent();
        TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
    }

    public final boolean isShown() {
        return this.isShown && super/*javax.microedition.lcdui.Displayable*/.isShown();
    }

    public final void updateFullScreenMode() {
        boolean isFullScreen = SettingsState.isStatusBarVisible();
        if (isFullScreen) {
            if (this.okCommand != null) {
                removeCommand(this.okCommand);
                this.okCommand = null;
            }
            if (this.cancelCommand != null) {
                removeCommand(this.cancelCommand);
                this.cancelCommand = null;
            }
        }
        setFullScreenMode(isFullScreen);
        this.fullScreen = isFullScreen;
        UIState.setDimensions(getEffectiveWidth(), getEffectiveHeight());
    }

    public final void paint(Graphics graphics) {
        GraphicsContext gfx = this.graphicsContext;
        gfx.graphics = graphics;
        try {
            synchronized (AppController.appLock) {
                if (!AppController.isShuttingDown) {
                    XmppContactGroup.incrementCacheCounter();
                    Vector events = UIState.getScreenStack();
                    int size = events.size();
                    if (size > 0) {
                        int i = size;
                        do {
                            i--;
                            if (i < 0) {
                                break;
                            }
                        } while (((ListView) events.elementAt(i)).offsetY != 0);
                        int w = UIState.getScreenWidth();
                        int h = UIState.getScreenHeight();
                        gfx.setClip(0, 0, w, h);
                        gfx.setColorFromPalette(14);
                        gfx.fillRect(0, 0, w, h);
                        while (i < size) {
                            boolean z = i == size - 1;
                            boolean z2 = z;
                            if (z && SettingsState.isTransparencyEnabled() && UIState.isSupportsAlpha()) {
                                int scanW = UIState.getScreenWidth();
                                int[] iArr = new int[scanW];
                                for (int i2 = scanW - 1; i2 >= 0; i2--) {
                                    iArr[i2] = COLOR_TRANSPARENT_OVERLAY;
                                }
                                Graphics graphics2 = gfx.graphics;
                                for (int y = UIState.getHeight() - 1; y >= 0; y--) {
                                    graphics2.drawRGB(iArr, 0, scanW, 0, y, scanW, 1, true);
                                }
                            }
                            ((ListView) events.elementAt(i)).paint(gfx, z2, false);
                            gfx.setClip(0, 0, w, h);
                            i++;
                        }
                    }
                    if (ScreenManager.hasModal()) {
                        gfx.setClip(0, 0, MAX_CLIP_SIZE, MAX_CLIP_SIZE);
                        int iconX = UIState.getScreenWidth() - ICON_STEP;
                        if (AccountManager.getCombinedContactFlags() != 0) {
                            gfx.drawIcon(ICON_UNREAD_BLINK, iconX, 1);
                            iconX -= ICON_STEP;
                        }
                        if (AccountManager.hasActiveConnection()) {
                            gfx.drawIcon(ICON_CONNECTION_BLINK, iconX, 1);
                        }
                    }
                    XmppContactGroup.cleanupExpiredImages();
                }
            }
        } catch (Throwable unused) {
        }
        AppController.needsRepaint = false;
    }

    public final void keyPressed(int i) {
        RemoteLogger.log("INPUT", "keyPressed: " + i);
        handleKeyInput(i, 0);
    }

    public final void keyRepeated(int i) {
        if (SessionState.getEventQueue().size() < EVENT_QUEUE_LIMIT) {
            handleKeyInput(i, 1);
        }
    }

    public final void keyReleased(int i) {
        TimerManager.setTimer(TimerManager.SLOT_ANIMATION, 10000L);
        EventDispatcher.postBackEvent();
    }

    private final String getKeyNameUpper(int i) {
        try {
            return getKeyName(i).toUpperCase();
        } catch (Throwable unused) {
            return AppState.emptyStr;
        }
    }

    private final void handleKeyInput(int i, int i2) {
        TimerManager.enableBacklight();
        TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
        TimerManager.setTimer(TimerManager.SLOT_ANIMATION, 10000L);
        int gameAction = 0;
        boolean isFullScreen = UIState.isFullscreenActive();
        try {
            gameAction = getGameAction(i);
        } catch (Throwable unused) {
        }
        if (gameAction == ACTION_UP || gameAction == ACTION_DOWN || gameAction == ACTION_LEFT || gameAction == ACTION_RIGHT) {
            EventDispatcher.postNavigationEvent(i, gameAction, i2);
            return;
        }
        if (gameAction == ACTION_FIRE && (i == KEY_SOFT_LEFT || i == KEY_SOFT_RIGHT || i == KEY_UP || i == KEY_SELECT)) {
            gameAction = 0;
        }
        if (gameAction == ACTION_FIRE) {
            if (!isFullScreen) {
                EventDispatcher.postSelectEvent();
                return;
            } else {
                UIState.setFullscreenRequested(false);
                EventDispatcher.postOkEvent();
                return;
            }
        }
        if ((i >= KEY_DIGIT_0 && i <= KEY_DIGIT_9) || i == KEY_STAR || i == KEY_HASH) {
            EventDispatcher.postNavigationEvent(i, gameAction, i2);
            return;
        }
        if (i2 == 0) {
            try {
                if (SettingsState.isStatusBarVisible()) {
                    String keyName = getKeyNameUpper(i);
                    if (i == KEY_SOFT_LEFT || keyName.indexOf("SEND") >= 0 || keyName.indexOf("SOFT1") >= 0 || keyName.equals("SOFTKEY 1")) {
                        EventDispatcher.postOkEvent();
                        return;
                    }
                    if (i == KEY_SOFT_RIGHT || i == KEY_CLEAR || keyName.indexOf("CLEAR") >= 0 || keyName.indexOf("SOFT2") >= 0 || keyName.equals("SOFTKEY 4")) {
                        if (isFullScreen) {
                            UIState.setFullscreenRequested(false);
                        }
                        EventDispatcher.postCancelEvent();
                    } else if (isFullScreen) {
                        UIState.setFullscreenRequested(false);
                        EventDispatcher.postOkEvent();
                    }
                }
            } catch (Throwable unused2) {
            }
        }
    }

    public final void pointerPressed(int i, int i2) {
        if (Debug.ENABLED) { return; }
        pointerDownX = i;
        pointerDownY = i2;
        TimerManager.setTimer(TimerManager.SLOT_ANIMATION, 10000L);
        TimerManager.setTimer(TimerManager.SLOT_BACKLIGHT, TimerManager.getSessionTimestamp());
        Vector events = SessionState.getEventQueue();
        synchronized (events) {
            events.addElement(PointerEvent.press(i, i2));
        }
        pointerDownTime = System.currentTimeMillis();
        pointerDragged = false;
        TimerManager.enableBacklight();
    }

    public final void pointerDragged(int i, int i2) {
        if (Debug.ENABLED) { return; }
        if (Utils.abs(i - pointerDownX) > DRAG_THRESHOLD || Utils.abs(i2 - pointerDownY) > DRAG_THRESHOLD) {
            int i3 = pointerDownX;
            int i4 = pointerDownY;
            Vector events = SessionState.getEventQueue();
            synchronized (events) {
                for (int idx = Utils.vectorSize(events) - 1; idx >= 0; idx--) {
                    Object event = events.elementAt(idx);
                    if (event instanceof PointerEvent) {
                        int action = ((PointerEvent) event).action;
                        if (action == PointerEvent.DRAG) {
                            events.removeElementAt(idx);
                        } else if (action == PointerEvent.PRESS || action == PointerEvent.RELEASE) {
                            break;
                        }
                    }
                }
                events.addElement(PointerEvent.drag(i, i2, i3, i4));
            }
            pointerDragged = true;
        }
    }

    public final void pointerReleased(int i, int i2) {
        if (Debug.ENABLED) { return; }
        int i3 = pointerDownX;
        int i4 = pointerDownY;
        boolean z = pointerDragged;
        Vector events = SessionState.getEventQueue();
        synchronized (events) {
            events.addElement(PointerEvent.release(i, i2, i3, i4, z));
        }
        pointerDownTime = 0L;
    }

    public final void commandAction(Command command, Displayable displayable) {
        TimerManager.setTimer(TimerManager.SLOT_ANIMATION, 10000L);
        if (command != null) {
            if (command == this.okCommand) {
                RemoteLogger.log("UI", "commandAction: OK pressed");
                EventDispatcher.postOkEvent();
            } else if (command == this.cancelCommand) {
                RemoteLogger.log("UI", "commandAction: Cancel pressed");
                EventDispatcher.postCancelEvent();
            } else {
                RemoteLogger.log("UI", "commandAction: unknown cmd " + command.getLabel());
            }
        }
    }

    public final MainCanvas updateCommands() {
        String str = this.okLabel;
        this.okLabel = null;
        String str2 = this.cancelLabel;
        this.cancelLabel = null;
        setCommands(str, str2);
        return this;
    }

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
        boolean isFullScreen = SettingsState.isFullscreen();
        if (str != null) {
            Command command = new Command(str, isFullScreen ? COMMAND_SCREEN : COMMAND_BACK, 1);
            this.okCommand = command;
            addCommand(command);
        }
        if (str2 != null) {
            Command command2 = new Command(str2, isFullScreen ? COMMAND_BACK : COMMAND_SCREEN, 1);
            this.cancelCommand = command2;
            addCommand(command2);
        }
        setCommandListener(this);
        AppController.needsRepaint = true;
    }
}
