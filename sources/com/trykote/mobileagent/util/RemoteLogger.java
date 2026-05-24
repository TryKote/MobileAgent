package com.trykote.mobileagent.util;

import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.rms.RecordStore;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Remote logging over TCP socket.
 * On server: nc -lk PORT  or  socat TCP-LISTEN:PORT,reuseaddr,fork -
 *
 * Configuration: config/remote_logger.cfg (build-time only, not included in JAR).
 * Values are compiled into RemoteLoggerConfig.java by Makefile.
 *
 * Levels: trace(0) < debug(1) < info(2) < warning(3) < error(4).
 * Config LEVEL filters: only messages with level >= LEVEL are emitted.
 * No level option in cfg => LEVEL=0 => everything passes.
 */
public final class RemoteLogger implements Runnable {
    public static final int LEVEL_TRACE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO  = 2;
    public static final int LEVEL_WARN  = 3;
    public static final int LEVEL_ERROR = 4;

    private static final char[] LEVEL_CHAR = {'T', 'D', 'I', 'W', 'E'};

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
        Thread t = new Thread(new RemoteLogger(), "RemoteLogger");
        t.start();
        info("LOG", "RemoteLogger started, target=" + RemoteLoggerConfig.HOST + ":" + RemoteLoggerConfig.PORT + " level=" + LEVEL_CHAR[RemoteLoggerConfig.LEVEL]);
    }

    public static void trace(String tag, String msg) { logAt(LEVEL_TRACE, tag, msg); }
    public static void debug(String tag, String msg) { logAt(LEVEL_DEBUG, tag, msg); }
    public static void info (String tag, String msg) { logAt(LEVEL_INFO,  tag, msg); }
    public static void warn (String tag, String msg) { logAt(LEVEL_WARN,  tag, msg); }
    public static void error(String tag, String msg) { logAt(LEVEL_ERROR, tag, msg); }

    public static void trace(String tag, String msg, Throwable t) { logAt(LEVEL_TRACE, tag, msg, t); }
    public static void debug(String tag, String msg, Throwable t) { logAt(LEVEL_DEBUG, tag, msg, t); }
    public static void info (String tag, String msg, Throwable t) { logAt(LEVEL_INFO,  tag, msg, t); }
    public static void warn (String tag, String msg, Throwable t) { logAt(LEVEL_WARN,  tag, msg, t); }
    public static void error(String tag, String msg, Throwable t) { logAt(LEVEL_ERROR, tag, msg, t); }

    private static final String[] KVM_PROPERTIES = {
        "microedition.platform",
        "microedition.configuration",
        "microedition.profiles",
        "microedition.locale",
        "microedition.encoding",
        "microedition.commports",
        "microedition.hostname",
        "com.nokia.mid.memoryram",
        "com.nokia.memoryramfree",
        "com.nokia.mid.imei",
        "com.nokia.network.access",
        "com.nokia.mid.dateformat",
        "com.sonyericsson.java.platform",
        "com.siemens.OS.name",
        "com.motorola.IMEI",
        "device.model",
        "device.software.version",
    };

    public static void logKvmInfo(int level, int screenWidth, int screenHeight) {
        logHeapStats(level);
        logAt(level, "ENV", "screen=" + screenWidth + "x" + screenHeight);
        for (int i = 0; i < KVM_PROPERTIES.length; i++) {
            logProperty(level, KVM_PROPERTIES[i]);
        }
        probeRmsCapacity(level);
    }

    public static void logHeapStats(int level) {
        Runtime rt = Runtime.getRuntime();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long used = total - free;
        int usedPercent = total > 0 ? (int) (used * 100L / total) : 0;
        logAt(level, "ENV", "heap total=" + total + " free=" + free + " used=" + used + " (" + usedPercent + "%)");
    }

    private static void logProperty(int level, String name) {
        try {
            String value = System.getProperty(name);
            if (value != null) {
                logAt(level, "ENV", name + "=" + value);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void probeRmsCapacity(int level) {
        RecordStore rs = null;
        try {
            rs = RecordStore.openRecordStore("envprobe", true);
            logAt(level, "ENV", "rms size=" + rs.getSize()
                    + " available=" + rs.getSizeAvailable()
                    + " records=" + rs.getNumRecords());
        } catch (Throwable t) {
            warn("ENV", "rms probe failed: " + t);
        } finally {
            if (rs != null) {
                try { rs.closeRecordStore(); } catch (Throwable ignored) {}
            }
        }
    }

    private static void logAt(int level, String tag, String msg) {
        if (level < RemoteLoggerConfig.LEVEL) return;
        writeLine(LEVEL_CHAR[level], tag, msg);
    }

    private static void logAt(int level, String tag, String msg, Throwable t) {
        if (level < RemoteLoggerConfig.LEVEL) return;
        writeLine(LEVEL_CHAR[level], tag, msg + " :: " + t.getClass().getName() + ": " + t.getMessage());
        t.printStackTrace();
    }

    private static void writeLine(char levelChar, String tag, String msg) {
        long dt = System.currentTimeMillis() - startTime;
        String line = "[" + dt + "] " + levelChar + " " + tag + " [" + Thread.currentThread().getName() + "] " + msg;
        System.out.println(line);
        if (!RemoteLoggerConfig.ENABLED) return;
        synchronized (queue) {
            if (queue.size() < MAX_QUEUE) {
                queue.addElement(line + "\n");
            }
            queue.notify();
        }
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
