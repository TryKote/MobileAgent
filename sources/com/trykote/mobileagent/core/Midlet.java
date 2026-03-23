package com.trykote.mobileagent.core;


import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import javax.microedition.midlet.MIDlet;
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
