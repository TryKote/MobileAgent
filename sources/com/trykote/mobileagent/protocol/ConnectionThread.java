package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

/* renamed from: j */
/* loaded from: MobileAgent_3.9.jar:j.class */
public final class ConnectionThread {

    // Connection state constants
    public static final int STATE_ERROR = -1;
    public static final int STATE_CLOSED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CLOSING = 3;

    /* renamed from: b */
    public Throwable exception;

    /* renamed from: j */
    private SocketWrapper socket;

    /* renamed from: k */
    private String connUrl;

    /* renamed from: i */
    private ByteBuffer inBuffer = new ByteBuffer();

    /* renamed from: a */
    public final ByteBuffer outBuffer = new ByteBuffer();

    /* renamed from: c */
    public int state = STATE_CONNECTING;

    public ConnectionThread(String str) {
        this.connUrl = str;
        RemoteLogger.log("CONN", "new ConnectionThread url=" + str);
        Vector vectorM614m = Storage.state().getVector(UIKeys.SLOT_MEDIA_CONTROL);
        if (vectorM614m != null) {
            synchronized (vectorM614m) {
                vectorM614m.addElement(IOUtils.registerResource(this));
            }
        }
    }

    /* renamed from: a */
    public final int getState() throws Throwable {
        if (this.exception != null) {
            throw this.exception;
        }
        return this.state;
    }

    /* renamed from: a */
    public final void drainInput(ByteBuffer c0043n) throws Throwable {
        synchronized (this.socket) {
            if (this.inBuffer.length > 0) {
                ByteBuffer c0043n2 = this.inBuffer;
                int i = c0043n2.length;
                if (i > 0) {
                    synchronized (c0043n2) {
                        c0043n.writeBytesAt(c0043n2.data, c0043n2.offset, i);
                        c0043n2.offset += i;
                        c0043n2.length -= i;
                        c0043n2.compact();
                    }
                }
            } else if (this.exception != null) {
                throw this.exception;
            }
        }
    }

    /* renamed from: b */
    public final void process() {
        switch (this.state) {
            case STATE_CONNECTING:
                try {
                    this.socket = SocketWrapper.open(new ByteBuffer().writeCompressed(PackedStringKeys.SCHEME_SOCKET).writeRawString(this.connUrl).getStringAndClear(), Storage.state().getBool(SettingsKeys.SETTING_COMPRESSION_ENABLED));
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
                Vector vectorM614m = Storage.state().getVector(UIKeys.SLOT_MEDIA_CONTROL);
                if (vectorM614m != null) {
                    synchronized (vectorM614m) {
                        vectorM614m.removeElement(this);
                        Utils.trimIfEmpty(vectorM614m);
                        IOUtils.unregisterResource(this);
                    }
                    return;
                }
                return;
        }
    }

    /* renamed from: o */
    private final void readFromSocket() {
        int iM1190a;
        try {
            if (this.state == STATE_CONNECTED) {
                ByteBuffer c0043n = this.inBuffer;
                SocketWrapper sock = this.socket;
                int iM1188c = sock.available();
                if (iM1188c > 0) {
                    byte[] bArrM1211a = ObjectPool.newBytes(iM1188c);
                    int i = 0;
                    do {
                        iM1190a = i + sock.read(bArrM1211a, i, iM1188c - i);
                        i = iM1190a;
                    } while (iM1190a != iM1188c);
                    synchronized (c0043n) {
                        c0043n.ensureCapacity(iM1188c);
                        Utils.arraycopy((Object) bArrM1211a, 0, (Object) c0043n.data, c0043n.length, iM1188c);
                        c0043n.length += iM1188c;
                        c0043n.compact();
                    }
                    ObjectPool.releaseBytes(bArrM1211a);
                }
            }
        } catch (Throwable th) {
            this.state = STATE_ERROR;
            this.exception = th;
            RemoteLogger.log("CONN", "readFromSocket ERROR: " + th, th);
            this.socket.close();
        }
    }

    /* renamed from: p */
    private final void writeToSocket() {
        try {
            if (this.state == STATE_CONNECTED) {
                ByteBuffer c0043n = this.outBuffer;
                SocketWrapper sock = this.socket;
                synchronized (c0043n) {
                    int i = c0043n.length;
                    if (i > 0) {
                        byte[] bArrM1211a = ObjectPool.newBytes(i);
                        Utils.arraycopy((Object) c0043n.data, c0043n.offset, (Object) bArrM1211a, 0, i);
                        c0043n.offset += i;
                        c0043n.length -= i;
                        c0043n.compact();
                        sock.write(bArrM1211a, i);
                        ObjectPool.releaseBytes(bArrM1211a);
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
