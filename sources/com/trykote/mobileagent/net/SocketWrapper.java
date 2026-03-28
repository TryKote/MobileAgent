package com.trykote.mobileagent.net;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.MapKeys;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class SocketWrapper {

    public Connection connection;
    public InputStream inputStream;
    public OutputStream outputStream;
    public Throwable error;
    public ByteBuffer asyncBuffer;
    private final boolean async;

    private SocketWrapper(boolean async) {
        this.async = async;
    }

    public static SocketWrapper open(String url, boolean async) throws IOException {
        RemoteLogger.log("NET", "SocketWrapper.open url=" + url + " async=" + async);
        long t0 = System.currentTimeMillis();
        SocketWrapper wrapper = new SocketWrapper(async);
        try {
            SocketConnection socketConnection = (SocketConnection) IOUtils.registerResource((Object) Connector.open(url, 3));
            RemoteLogger.log("NET", "SocketWrapper.open connected in " + (System.currentTimeMillis() - t0) + "ms");
            wrapper.connection = socketConnection;
            try {
                if (socketConnection instanceof SocketConnection) {
                    byte b = 5;
                    while (true) {
                        byte b2 = (byte) (b - 1);
                        b = b2;
                        if (b2 < 2) {
                            break;
                        }
                        try {
                            int optionValue = AppState.getInt(b + 107);
                            if (optionValue >= 0) {
                                socketConnection.setSocketOption(b, optionValue);
                            }
                        } catch (Throwable unused) {
                        }
                    }
                }
            } catch (Throwable unused2) {
            }
            wrapper.inputStream = (InputStream) IOUtils.registerResource((Object) socketConnection.openInputStream());
            wrapper.outputStream = (OutputStream) IOUtils.registerResource((Object) socketConnection.openOutputStream());
            if (async) {
                wrapper.asyncBuffer = new ByteBuffer();
                new AsyncTask(AsyncTaskId.SOCKET_READER, wrapper);
            }
            AppState.getVector(MapKeys.SLOT_MAP_TILE_REQUEST).addElement(wrapper);
            return wrapper;
        } catch (IOException e) {
            RemoteLogger.log("NET", "SocketWrapper.open FAILED after " + (System.currentTimeMillis() - t0) + "ms", e);
            wrapper.closeImmediate();
            throw e;
        } catch (RuntimeException e) {
            RemoteLogger.log("NET", "SocketWrapper.open FAILED (RE) after " + (System.currentTimeMillis() - t0) + "ms", e);
            wrapper.closeImmediate();
            throw e;
        } catch (Error e) {
            RemoteLogger.log("NET", "SocketWrapper.open FAILED (Error) after " + (System.currentTimeMillis() - t0) + "ms: " + e);
            wrapper.closeImmediate();
            throw e;
        }
    }

    public boolean isAsync() {
        return this.async;
    }

    public int available() throws IOException {
        if (!this.async) {
            return this.inputStream.available();
        }
        synchronized (this) {
            int i = this.asyncBuffer.length;
            if (i > 0) {
                return i;
            }
            Throwable th = this.error;
            if (th != null) {
                if (th instanceof IOException) throw (IOException) th;
                if (th instanceof RuntimeException) throw (RuntimeException) th;
                if (th instanceof Error) throw (Error) th;
                throw new RuntimeException(th.toString());
            }
            return 0;
        }
    }

    public int read(byte[] buf, int offset, int length) throws IOException {
        if (!this.async) {
            return this.inputStream.read(buf, offset, length);
        }
        synchronized (this) {
            this.asyncBuffer.readInto(buf, offset, length);
        }
        return length;
    }

    public void write(byte[] buf, int length) throws IOException {
        this.outputStream.write(buf, 0, length);
        this.outputStream.flush();
    }

    public void close() {
        RemoteLogger.log("NET", "closeConnection");
        closeImpl(false);
    }

    public void closeImmediate() {
        closeImpl(true);
    }

    private void closeImpl(boolean immediate) {
        IOUtils.closeInput(this.inputStream);
        IOUtils.closeOutput(this.outputStream);
        Connection conn = this.connection;
        if (conn == null || immediate) {
            IOUtils.closeConn(conn);
        } else {
            new AsyncTask(AsyncTaskId.DELAYED_CLOSE, conn);
        }
        this.connection = null;
        this.inputStream = null;
        this.outputStream = null;
        Utils.removeFrom(AppState.getVector(MapKeys.SLOT_MAP_TILE_REQUEST), this);
    }

    public void asyncReaderLoop() {
        RemoteLogger.log("NET", "asyncReaderLoop started");
        int bytesRead;
        byte[] buf = new byte[1024];
        do {
            try {
                bytesRead = readWithTimeout(buf);
                if (bytesRead > 0) {
                    synchronized (this) {
                        this.asyncBuffer.writeBytesAt(buf, 0, bytesRead);
                    }
                }
                if (bytesRead < 1024) {
                    Thread.sleep(100L);
                }
            } catch (Throwable th) {
                RemoteLogger.log("NET", "asyncReaderLoop error", th);
                try {
                    Thread.sleep(3000);
                } catch (Throwable unused) {
                }
                this.error = th;
                ObjectPool.releaseBytes(buf);
                return;
            }
        } while (bytesRead >= 0);
        throw new RuntimeException("EOFException");
    }

    private int readWithTimeout(byte[] buf) throws IOException {
        long startTime = System.currentTimeMillis();
        try {
            return this.inputStream.read(buf);
        } catch (IOException th) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= 50000 && elapsed <= 70000) {
                return 0;
            }
            throw th;
        } catch (RuntimeException th) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= 50000 && elapsed <= 70000) {
                return 0;
            }
            throw th;
        } catch (Throwable th) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed >= 50000 && elapsed <= 70000) {
                return 0;
            }
            throw new RuntimeException(th.toString());
        }
    }

    /* renamed from: c */
    public static final void closeAll() {
        RemoteLogger.log("NET", "closeAllConnections");
        java.util.Vector connections = AppState.getVector(MapKeys.SLOT_MAP_TILE_REQUEST);
        int size = connections.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                ((SocketWrapper) connections.elementAt(size)).closeImmediate();
            }
        }
    }
}
