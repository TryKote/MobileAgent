package p001main;

import javax.microedition.midlet.MIDlet;
import p000.SplashScreen;

/* loaded from: MobileAgent_3.9.jar:main/Midlet.class */
public final class Midlet extends MIDlet {

    /* renamed from: a */
    private boolean started;

    public final void startApp() {
        if (this.started) {
            return;
        }
        this.started = true;
        new SplashScreen(this);
    }

    public final void pauseApp() {
        notifyPaused();
    }

    public final void destroyApp(boolean z) {
        new SplashScreen(null);
        notifyDestroyed();
    }
}
