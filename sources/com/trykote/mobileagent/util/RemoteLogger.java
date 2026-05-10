package com.trykote.mobileagent.util;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Remote logging over TCP socket.
 * On server: nc -lk PORT  or  socat TCP-LISTEN:PORT,reuseaddr,fork -
 *
 * Configuration: config/remote_logger.cfg (build-time only, not included in JAR).
 * Values are compiled into RemoteLoggerConfig.java by Makefile.
 */
public final class RemoteLogger implements Runnable {
    private static final int MAX_QUEUE = 500;
    private static final Vector queue = new Vector();
    private static long startTime;
    private static boolean initialized;

    public static void init() {
        if (initialized) return;
        initialized = true;
        startTime = System.currentTimeMillis();
        if (!RemoteLoggerConfig.ENABLED) {
            System.out.println("[RemoteLogger] disabled by config");
            return;
        }
        Thread t = new Thread(new RemoteLogger());
        t.setName("RemoteLogger");
        t.start();
        log("LOG", "RemoteLogger started, target=" + RemoteLoggerConfig.HOST + ":" + RemoteLoggerConfig.PORT);
    }

    public static void log(String tag, String msg) {
        long dt = System.currentTimeMillis() - startTime;
        String line = "[" + dt + "] " + tag + " [" + Thread.currentThread().getName() + "] " + msg;
        System.out.println(line);
        if (!RemoteLoggerConfig.ENABLED) return;
        synchronized (queue) {
            if (queue.size() < MAX_QUEUE) {
                queue.addElement(line + "\n");
            }
            queue.notify();
        }
    }

    public static void log(String tag, String msg, Throwable t) {
        log(tag, msg + " :: " + t.getClass().getName() + ": " + t.getMessage());
    }

    public void run() {
        while (true) {
            SocketConnection conn = null;
            OutputStream out = null;
            try {
                conn = (SocketConnection) Connector.open("socket://" + RemoteLoggerConfig.HOST + ":" + RemoteLoggerConfig.PORT);
                out = conn.openOutputStream();
                while (true) {
                    String line;
                    synchronized (queue) {
                        while (queue.size() == 0) {
                            queue.wait(5000);
                        }
                        if (queue.size() == 0) continue;
                        line = (String) queue.elementAt(0);
                        queue.removeElementAt(0);
                    }
                    byte[] bytes = line.getBytes("UTF-8");
                    out.write(bytes);
                    out.flush();
                }
            } catch (Throwable t) {
                System.out.println("[RemoteLogger] connection error: " + t);
                try { if (out != null) out.close(); } catch (Throwable ignored) {}
                try { if (conn != null) conn.close(); } catch (Throwable ignored) {}
                try { Thread.sleep(3000); } catch (Throwable ignored) {}
            }
        }
    }
}
