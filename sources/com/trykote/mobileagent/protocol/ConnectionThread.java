package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.net.SocketWrapper;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.Utils;

import java.io.IOException;
import java.util.Vector;

public final class ConnectionThread {
    public static final int STATE_ERROR = -1;
    public static final int STATE_CLOSED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CLOSING = 3;

    private static final long CONNECT_TIMEOUT_MS = 30000;
    private static final int MAX_LOG_BYTES = 500;

    public Throwable exception;

    private SocketWrapper socket;
    private String connUrl;
    private final ByteBuffer inBuffer = new ByteBuffer();
    public final ByteBuffer outBuffer = new ByteBuffer();
    public int state = STATE_CONNECTING;

    private Thread connectThread;
    private long connectDeadline;
    private Throwable connectError;

    public ConnectionThread(String url) {
        this.connUrl = url;
        RemoteLogger.info("CONN", "new ConnectionThread url=" + url);
        Vector mediaControl = UIState.getMediaControl();
        if (mediaControl != null) {
            synchronized (mediaControl) {
                mediaControl.addElement(IOUtils.registerResource(this));
            }
        }
    }

    public int getState() throws Throwable {
        if (this.exception != null) {
            throw this.exception;
        }
        return this.state;
    }

    public void drainInput(ByteBuffer dest) throws Throwable {
        synchronized (this.socket) {
            if (this.inBuffer.length <= 0) {
                if (this.exception != null) {
                    throw this.exception;
                }
                return;
            }
            ByteBuffer src = this.inBuffer;
            int len = src.length;
            synchronized (src) {
                dest.writeBytesAt(src.data, src.offset, len);
                src.offset += len;
                src.length -= len;
                src.compact();
            }
        }
    }

    public void process() {
        switch (this.state) {
            case STATE_CONNECTING:
                processConnecting();
                return;
            case STATE_CONNECTED:
                readFromSocket();
                writeToSocket();
                return;
            case STATE_CLOSING:
                readFromSocket();
                writeToSocket();
                RemoteLogger.info("CONN", "state 3->0 (closing)");
                this.socket.close();
                this.state = STATE_CLOSED;
                return;
            default:
                unregister();
                return;
        }
    }

    private void unregister() {
        Vector mediaControl = UIState.getMediaControl();
        if (mediaControl != null) {
            synchronized (mediaControl) {
                mediaControl.removeElement(this);
                Utils.trimIfEmpty(mediaControl);
                IOUtils.unregisterResource(this);
            }
        }
    }

    private void processConnecting() {
        if (this.connectThread == null) {
            startConnectThread();
            return;
        }

        synchronized (this) {
            if (this.socket != null) {
                this.state = STATE_CONNECTED;
                this.connUrl = null;
                this.connectThread = null;
                RemoteLogger.info("CONN", "state 1->2 (socket opened)");
                return;
            }
            if (this.connectError != null) {
                setError(this.connectError);
                RemoteLogger.error("CONN", "connect failed: " + this.connectError, this.connectError);
                this.connectThread = null;
                return;
            }
        }

        if (System.currentTimeMillis() > this.connectDeadline) {
            setError(new IOException("connect timeout: " + this.connUrl));
            RemoteLogger.info("CONN", "connect timeout after " + CONNECT_TIMEOUT_MS + "ms: " + this.connUrl);
            this.connectThread = null;
        }
    }

    private void startConnectThread() {
        this.connectDeadline = System.currentTimeMillis() + CONNECT_TIMEOUT_MS;
        final String url = new ByteBuffer().writeCharBytes("socket://")
            .writeRawString(this.connUrl).getStringAndClear();
        final boolean async = SettingsState.isCompressionEnabled();
        this.connectThread = new Thread() {
            public void run() {
                try {
                    SocketWrapper result = SocketWrapper.open(url, async);
                    synchronized (ConnectionThread.this) {
                        ConnectionThread.this.socket = result;
                    }
                } catch (Throwable th) {
                    synchronized (ConnectionThread.this) {
                        ConnectionThread.this.connectError = th;
                    }
                }
            }
        };
        this.connectThread.start();
        RemoteLogger.info("CONN", "connect started to " + this.connUrl);
    }

    private void setError(Throwable error) {
        this.state = STATE_ERROR;
        this.exception = error;
    }

    private void readFromSocket() {
        if (this.state != STATE_CONNECTED) {
            return;
        }
        try {
            SocketWrapper sock = this.socket;
            int available = sock.available();
            if (available <= 0) {
                return;
            }
            byte[] readBuf = ObjectPool.newBytes(available);
            int totalRead = 0;
            do {
                totalRead += sock.read(readBuf, totalRead, available - totalRead);
            } while (totalRead != available);
            ByteBuffer buf = this.inBuffer;
            synchronized (buf) {
                buf.ensureCapacity(available);
                Utils.arraycopy((Object) readBuf, 0, (Object) buf.data, buf.length, available);
                buf.length += available;
                buf.compact();
            }
            RemoteLogger.trace("CONN", "IN " + available + "b: " + formatBytes(readBuf, available));
            ObjectPool.releaseBytes(readBuf);
        } catch (Throwable th) {
            setError(th);
            RemoteLogger.error("CONN", "readFromSocket ERROR: " + th, th);
            this.socket.close();
        }
    }

    private void writeToSocket() {
        if (this.state != STATE_CONNECTED) {
            return;
        }
        try {
            ByteBuffer buf = this.outBuffer;
            SocketWrapper sock = this.socket;
            synchronized (buf) {
                int len = buf.length;
                if (len <= 0) {
                    return;
                }
                byte[] writeBuf = ObjectPool.newBytes(len);
                Utils.arraycopy((Object) buf.data, buf.offset, (Object) writeBuf, 0, len);
                buf.offset += len;
                buf.length -= len;
                buf.compact();
                RemoteLogger.trace("CONN", "OUT " + len + "b: " + formatBytes(writeBuf, len));
                sock.write(writeBuf, len);
                ObjectPool.releaseBytes(writeBuf);
            }
        } catch (Throwable th) {
            setError(th);
            RemoteLogger.error("CONN", "writeToSocket ERROR: " + th, th);
            this.socket.close();
        }
    }

    private static String formatBytes(byte[] data, int len) {
        int displayLen = len > MAX_LOG_BYTES ? MAX_LOG_BYTES : len;
        char[] chars = new char[displayLen];
        for (int idx = 0; idx < displayLen; idx++) {
            int byteVal = data[idx] & 0xFF;
            chars[idx] = (byteVal >= ' ' && byteVal < 127) ? (char) byteVal : '.';
        }
        return new String(chars) + (len > MAX_LOG_BYTES ? "..." : "");
    }
}
