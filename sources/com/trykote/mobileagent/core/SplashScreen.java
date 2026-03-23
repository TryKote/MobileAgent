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
/* renamed from: t */
/* loaded from: MobileAgent_3.9.jar:t.class */
public final class SplashScreen extends Canvas implements Runnable, CommandListener {

    /* renamed from: a */
    private Midlet midlet;

    /* renamed from: b */
    private Image splashImage;

    public SplashScreen(Midlet midlet) {
        this.midlet = midlet;
        if (midlet != null) {
            new Thread(this).start();
        } else {
            try {
                new AsyncTask(null, 0, 0);
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
                } else if (i >= 3 && System.currentTimeMillis() - jCurrentTimeMillis >= 200) {
                    new AsyncTask(this.midlet, getWidth(), getHeight());
                    return;
                }
                System.gc();
                Thread.sleep(80L);
                i++;
            }
        } catch (Throwable th) {
            new SplashScreen(this.midlet, th);
        }
    }

    public final void paint(Graphics graphics) {
        try {
            graphics.setColor(18060);
            int width = getWidth();
            int height = getHeight();
            graphics.fillRect(0, 0, width, height);
            if (this.splashImage == null) {
                this.splashImage = Image.createImage("/splash.png");
            }
            int i = width >> 1;
            graphics.drawImage(this.splashImage, i, height >> 1, 3);
            graphics.setColor(0);
            graphics.drawString("Версия: 3.9", i, height - 2, 33);
            graphics.setColor(16777215);
            graphics.drawString("Версия: 3.9", i - 1, height - 3, 33);
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

    /* renamed from: a */
    private static void clearRecordStores() {
        try {
            String[] strArrListRecordStores = RecordStore.listRecordStores();
            int length = strArrListRecordStores.length;
            while (true) {
                length--;
                if (length < 0) {
                    return;
                } else {
                    try {
                        RecordStore.deleteRecordStore(strArrListRecordStores[length]);
                    } catch (Throwable unused) {
                    }
                }
            }
        } catch (Throwable unused2) {
        }
    }
}
