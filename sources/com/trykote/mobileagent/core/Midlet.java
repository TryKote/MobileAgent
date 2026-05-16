package com.trykote.mobileagent.core;


import com.trykote.mobileagent.ui.SplashScreen;

import javax.microedition.midlet.MIDlet;
public final class Midlet extends MIDlet {

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
