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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
public final class SplashScreen extends Canvas implements Runnable, CommandListener {

    private static final int REPAINT_THRESHOLD = 3;
    private static final long FRAME_DELAY_MS = 80L;
    private static final long MIN_DISPLAY_MS = 200L;

    private static final int COLOR_BACKGROUND = 18060;
    private static final int COLOR_BLACK = 0;
    private static final int COLOR_WHITE = 0xFFFFFF;

    private static final int ANCHOR_HCENTER_TOP = 3;
    private static final int ANCHOR_BOTTOM_HCENTER = 33;

    private static final int TEXT_OFFSET_SHADOW = 2;
    private static final int TEXT_OFFSET_MAIN = 3;

    private Midlet midlet;

    private Image splashImage;

    public SplashScreen(Midlet midlet) {
        this.midlet = midlet;
        if (midlet != null) {
            Thread t = new Thread(this);
            t.setName("Splash");
            t.start();
        } else {
            try {
                AsyncTask.shutdown();
            } catch (Throwable unused) {
            }
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        try {
            setFullScreenMode(true);
            Display.getDisplay(this.midlet).setCurrent(this);
            long jCurrentTimeMillis = System.currentTimeMillis();
            int i = 0;
            while (true) {
                if (i == 0) {
                    repaint();
                    serviceRepaints();
                } else if (i >= REPAINT_THRESHOLD && System.currentTimeMillis() - jCurrentTimeMillis >= MIN_DISPLAY_MS) {
                    new AsyncTask(this.midlet, getWidth(), getHeight());
                    return;
                }
                System.gc();
                Thread.sleep(FRAME_DELAY_MS);
                i++;
            }
        } catch (Throwable th) {
            new SplashScreen(this.midlet, th);
        }
    }

    public final void paint(Graphics graphics) {
        try {
            graphics.setColor(COLOR_BACKGROUND);
            int width = getWidth();
            int height = getHeight();
            graphics.fillRect(0, 0, width, height);
            if (this.splashImage == null) {
                this.splashImage = Image.createImage("/splash.png");
            }
            int i = width >> 1;
            graphics.drawImage(this.splashImage, i, height >> 1, ANCHOR_HCENTER_TOP);
            graphics.setColor(COLOR_BLACK);
            graphics.drawString("Версия: 3.9", i, height - TEXT_OFFSET_SHADOW, ANCHOR_BOTTOM_HCENTER);
            graphics.setColor(COLOR_WHITE);
            graphics.drawString("Версия: 3.9", i - 1, height - TEXT_OFFSET_MAIN, ANCHOR_BOTTOM_HCENTER);
        } catch (Throwable unused) {
        }
    }

    private SplashScreen(Midlet midlet, Throwable th) {
        this.midlet = midlet;
        try {
            Form form = new Form("Ошибка");
            form.append(new StringBuffer().append("При запуске программы произошла ошибка.\nПереустановите программу с сайта m.mail.ru или обратитесь к разработчику.\nДетали ошибки:\n").append(th).append("\nВерсия: ").append(midlet.getAppProperty("MIDlet-Version")).append("\nПамять: ").append(Runtime.getRuntime().freeMemory()).append(" / ").append(Runtime.getRuntime().totalMemory()).append("\nМодули: ").append(midlet.getAppProperty("Agent-Modules")).append("\n").toString());
            form.addCommand(new Command("OK", 4, 0));
            form.setCommandListener(this);
            Display.getDisplay(midlet).setCurrent(form);
            clearRecordStores();
        } catch (Throwable unused) {
        }
    }

    public final void commandAction(Command command, Displayable displayable) {
        try {
            switch (command.getPriority()) {
                case 0:
                    this.midlet.destroyApp(true);
                    break;
                case 1:
                    this.midlet.platformRequest("http://m.mail.ru");
                    this.midlet.destroyApp(true);
                    break;
                case 2:
                    new SplashScreen(this.midlet);
                    break;
            }
        } catch (Throwable unused) {
        }
    }

    private static void clearRecordStores() {
        try {
            String[] strArrListRecordStores = RecordStore.listRecordStores();
            for (int idx = strArrListRecordStores.length - 1; idx >= 0; idx--) {
                try {
                    RecordStore.deleteRecordStore(strArrListRecordStores[idx]);
                } catch (Throwable unused) {
                }
            }
        } catch (Throwable unused2) {
        }
    }
}
