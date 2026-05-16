package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.net.SocketWrapper;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.Utils;

import java.util.Vector;

public final class ConnectionThread {

    // Connection state constants
    public static final int STATE_ERROR = -1;
    public static final int STATE_CLOSED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CLOSING = 3;

    public Throwable exception;

    private SocketWrapper socket;

    private String connUrl;

    private ByteBuffer inBuffer = new ByteBuffer();

    public final ByteBuffer outBuffer = new ByteBuffer();

    public int state = STATE_CONNECTING;

    public ConnectionThread(String url) {
        this.connUrl = url;
        RemoteLogger.log("CONN", "new ConnectionThread url=" + url);
        Vector mediaControl = UIState.getMediaControl();
        if (mediaControl != null) {
            synchronized (mediaControl) {
                mediaControl.addElement(IOUtils.registerResource(this));
            }
        }
    }

    public final int getState() throws Throwable {
        if (this.exception != null) {
            throw this.exception;
        }
        return this.state;
    }

    public final void drainInput(ByteBuffer dest) throws Throwable {
        synchronized (this.socket) {
            if (this.inBuffer.length > 0) {
                ByteBuffer src = this.inBuffer;
                int len = src.length;
                if (len > 0) {
                    synchronized (src) {
                        dest.writeBytesAt(src.data, src.offset, len);
                        src.offset += len;
                        src.length -= len;
                        src.compact();
                    }
                }
            } else if (this.exception != null) {
                throw this.exception;
            }
        }
    }

    public final void process() {
        switch (this.state) {
            case STATE_CONNECTING:
                try {
                    this.socket = SocketWrapper.open(new ByteBuffer().writeCharBytes("socket://").writeRawString(this.connUrl).getStringAndClear(), SettingsState.isCompressionEnabled());
                    if (this.state == STATE_CONNECTING) {
                        this.state = STATE_CONNECTED;
                        RemoteLogger.log("CONN", "state 1->2 (socket opened)");
                    }
                    this.connUrl = null;
                    return;
                } catch (Throwable th) {
                    this.state = STATE_ERROR;
                    this.exception = th;
                    RemoteLogger.log("CONN", "state 1 ERROR: " + th, th);
                    if (this.socket != null) {
                        this.socket.close();
                    }
                    return;
                }
            case STATE_CONNECTED:
                readFromSocket();
                writeToSocket();
                return;
            case STATE_CLOSING:
                readFromSocket();
                writeToSocket();
                RemoteLogger.log("CONN", "state 3->0 (closing)");
                this.socket.close();
                this.state = STATE_CLOSED;
                return;
            default:
                Vector mediaControl = UIState.getMediaControl();
                if (mediaControl != null) {
                    synchronized (mediaControl) {
                        mediaControl.removeElement(this);
                        Utils.trimIfEmpty(mediaControl);
                        IOUtils.unregisterResource(this);
                    }
                    return;
                }
                return;
        }
    }

    private final void readFromSocket() {
        try {
            if (this.state == STATE_CONNECTED) {
                ByteBuffer buf = this.inBuffer;
                SocketWrapper sock = this.socket;
                int available = sock.available();
                if (available > 0) {
                    RemoteLogger.log("CONN", "readFromSocket: " + available + " bytes available");
                    byte[] readBuf = ObjectPool.newBytes(available);
                    int totalRead = 0;
                    do {
                        totalRead += sock.read(readBuf, totalRead, available - totalRead);
                    } while (totalRead != available);
                    synchronized (buf) {
                        buf.ensureCapacity(available);
                        Utils.arraycopy((Object) readBuf, 0, (Object) buf.data, buf.length, available);
                        buf.length += available;
                        buf.compact();
                    }
                    ObjectPool.releaseBytes(readBuf);
                }
            }
        } catch (Throwable th) {
            this.state = STATE_ERROR;
            this.exception = th;
            RemoteLogger.log("CONN", "readFromSocket ERROR: " + th, th);
            this.socket.close();
        }
    }

    private final void writeToSocket() {
        try {
            if (this.state == STATE_CONNECTED) {
                ByteBuffer buf = this.outBuffer;
                SocketWrapper sock = this.socket;
                synchronized (buf) {
                    int len = buf.length;
                    if (len > 0) {
                        RemoteLogger.log("CONN", "writeToSocket: sending " + len + " bytes");
                        byte[] writeBuf = ObjectPool.newBytes(len);
                        Utils.arraycopy((Object) buf.data, buf.offset, (Object) writeBuf, 0, len);
                        buf.offset += len;
                        buf.length -= len;
                        buf.compact();
                        sock.write(writeBuf, len);
                        ObjectPool.releaseBytes(writeBuf);
                    }
                }
            }
        } catch (Throwable th) {
            this.state = STATE_ERROR;
            this.exception = th;
            RemoteLogger.log("CONN", "writeToSocket ERROR: " + th, th);
            this.socket.close();
        }
    }
}
