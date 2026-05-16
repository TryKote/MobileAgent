package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.net.NetworkLock;
import com.trykote.mobileagent.net.SocketWrapper;
import com.trykote.mobileagent.net.TrafficAccounting;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import java.io.IOException;
import java.util.Vector;

public final class TileCache {

    // Maximum tile cache size in bytes (200 KB)
    private static final int MAX_CACHE_SIZE = 204800;

    // HTTP header end-of-headers marker state value
    // State machine: LF=+1, CR=+16; end at state 34 (CR LF CR LF)
    private static final int HEADER_END_MARKER = 34;

    // HTTP response status parsing offsets
    private static final int HTTP_STATUS_OFFSET = 9;
    private static final int HTTP_STATUS_LENGTH = 12;
    private static final int HTTP_STATUS_OK = 200;

    // Content-Length header value offset
    private static final int CONTENT_LENGTH_VALUE_OFFSET = 16;

    // HTTP header state machine increments
    private static final int STATE_LF_INCREMENT = 1;
    private static final int STATE_CR_INCREMENT = 16;

    // Traffic overhead estimate for tile response
    private static final int TILE_TRAFFIC_OVERHEAD = 255;

    public static final void calculateCacheSize() {
        int size = 0;
        String[] storeNames = StringUtils.listRecordStores();
        if (storeNames != null) {
            for (int idx = storeNames.length - 1; idx >= 0; idx--) {
                String str = storeNames[idx];
                if (str.startsWith(StringPool.get(PackedStringKeys.MAP_TILES))) {
                    RecordStore recordStore = null;
                    try {
                        RecordStore store = IOUtils.openRecordStore(str, false);
                        recordStore = store;
                        size += store.getSize();
                        IOUtils.closeRecordStore(recordStore);
                    } catch (Throwable unused) {
                        IOUtils.closeRecordStore(recordStore);
                    }
                }
            }
        }
        MapState.setTileCacheSize( size);
    }

    private static void saveTileToCache(TileRequest resource, byte[] bArr, int i, int i2) {
        if (resource == null || bArr == null) {
            return;
        }
        String cacheKey = buildTileCacheKey(resource);
        ByteBuffer tileData = new ByteBuffer().writeStringLatin1(resource.tileUrl).writeLong(System.currentTimeMillis()).writeBytesAt(bArr, 0, i2);
        for (int i3 = 3; i3 > 0; i3--) {
            try {
                try {
                    if (tileData.length + MapState.getTileCacheSize() >= MAX_CACHE_SIZE) {
                        throw new Throwable();
                    }
                    RecordStore store = IOUtils.openRecordStore(cacheKey, true);
                    byte[] bArr2 = tileData.data;
                    int i4 = tileData.offset;
                    int i5 = tileData.length;
                    store.addRecord(bArr2, i4, i5);
                    MapState.setTileCacheSize( MapState.getTileCacheSize() + i5);
                    tileData.clear();
                    IOUtils.closeRecordStore(store);
                    return;
                } catch (Throwable unused) {
                    evictOldestCache();
                    IOUtils.closeRecordStore((RecordStore) null);
                }
            } catch (RuntimeException e) {
                IOUtils.closeRecordStore((RecordStore) null);
                throw e;
            } catch (Error e) {
                IOUtils.closeRecordStore((RecordStore) null);
                throw e;
            }
        }
    }

    public static final Image loadTileFromCache(TileRequest resource) {
        String cacheKey = buildTileCacheKey(resource);
        RecordStore recordStore = null;
        try {
            String str = resource.tileUrl;
            RecordStore store = IOUtils.openRecordStore(cacheKey, false);
            recordStore = store;
            int numRecords = store.getNumRecords();
            for (int i = 1; i <= numRecords; i++) {
                ByteBuffer recordBuf = new ByteBuffer().setData(recordStore.getRecord(i));
                if (recordBuf.readWideStr().equals(str)) {
                    recordBuf.readLong();
                    Image image = recordBuf.toImage();
                    IOUtils.closeRecordStore(recordStore);
                    return image;
                }
                recordBuf.clear();
            }
            IOUtils.closeRecordStore(recordStore);
            return null;
        } catch (RuntimeException th) {
            IOUtils.closeRecordStore(recordStore);
            throw th;
        } catch (Throwable th) {
            IOUtils.closeRecordStore(recordStore);
            throw new RuntimeException(th.toString());
        }
    }

    private static final String findOldestCacheStore() {
        String str = null;
        long j = 0;
        String[] storeNames = StringUtils.listRecordStores();
        if (storeNames != null) {
            String cachePrefix = StringPool.get(PackedStringKeys.MAP_TILES);
            for (int idx = storeNames.length - 1; idx >= 0; idx--) {
                String str2 = storeNames[idx];
                if (str2.startsWith(cachePrefix)) {
                    RecordStore store = null;
                    try {
                        store = IOUtils.openRecordStore(str2, false);
                        long lastModified = store.getLastModified();
                        if (lastModified < j || j == 0) {
                            j = lastModified;
                            str = str2;
                        }
                        IOUtils.closeRecordStore(store);
                    } catch (RuntimeException th) {
                        IOUtils.closeRecordStore(store);
                        throw th;
                    } catch (Throwable th) {
                        IOUtils.closeRecordStore(store);
                        throw new RuntimeException(th.toString());
                    }
                }
            }
        }
        return str;
    }

    private static final void evictOldestCache() {
        String oldestStore = findOldestCacheStore();
        if (oldestStore != null) {
            RecordStore recordStore = null;
            try {
                RecordStore store = IOUtils.openRecordStore(oldestStore, false);
                recordStore = store;
                int numRecords = store.getNumRecords();
                for (int i = 1; i <= numRecords; i++) {
                    MapState.setTileCacheSize( MapState.getTileCacheSize() - recordStore.getRecordSize(i));
                }
                IOUtils.closeRecordStore(recordStore);
                try {
                    RecordStore.deleteRecordStore(oldestStore);
                } catch (Throwable unused) {
                }
            } catch (Throwable unused2) {
                IOUtils.closeRecordStore(recordStore);
                try {
                    RecordStore.deleteRecordStore(oldestStore);
                } catch (Throwable unused3) {
                }
            }
        }
    }

    private static final String buildTileCacheKey(TileRequest resource) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringPool.get(PackedStringKeys.MAP_TILES)).append(resource.tileType).append('z').append(resource.zoomLevel).append('x').append((resource.tileX / 4) << 2).append('y').append((resource.tileY / 4) << 2));
    }

    private static final boolean reconnectHttp() {
        try {
            NetworkLock.acquireNetworkLock();
            SocketWrapper oldSocket = (SocketWrapper) MapState.getMenuActions();
            if (oldSocket != null) {
                oldSocket.close();
            }
            MapState.setMenuActions(SocketWrapper.open(new ByteBuffer().writeCharBytes("socket://").writeCharBytes("mobilemaps.mail.ru:2043").getStringAndClear(), false));
            return true;
        } catch (Throwable unused) {
            return false;
        } finally {
            NetworkLock.releaseNetworkLock();
        }
    }

    public static final Image fetchTileImage(TileRequest resource) throws IOException {
        ByteBuffer requestBuf = new ByteBuffer().writeCharBytes("GET /TileSender.aspx?ModeKey=tiles").writeRawString(resource.tileUrl).writeCharBytes(" HTTP/1.1\r\nAccept: image/png\r\nUser-Agent: J2ME MailAgent v.").writeExtendedInt(2950495).writeEncodedInt(222).writeCharBytes("\r\nConnection: keep-alive\r\nCache-Control: no-transform\r\nContent-Length: 0\r\nHost: mobilemaps.mail.ru\r\n\r\n");
        try {
            SocketWrapper socket = (SocketWrapper) MapState.getMenuActions();
            byte[] bArr = requestBuf.data;
            int i = requestBuf.length;
            socket.write(bArr, i);
            TrafficAccounting.addXmppOutbound(i);
        } catch (Throwable unused) {
            if (!reconnectHttp()) {
                throw new IOException();
            }
            SocketWrapper socket2 = (SocketWrapper) MapState.getMenuActions();
            byte[] bArr2 = requestBuf.data;
            int i2 = requestBuf.length;
            socket2.write(bArr2, i2);
            TrafficAccounting.addXmppOutbound(i2);
        } finally {
            requestBuf.clear();
        }
        String headers = readHttpHeaders();
        if (headers == null) {
            ((SocketWrapper) MapState.getMenuActions()).close();
            throw new IOException();
        }
        RuntimeState.addXmppTrafficBytes(headers.getBytes().length);
        if (parseHttpStatus(headers) != HTTP_STATUS_OK) {
            int contentLen = parseContentLength(headers);
            try {
                if (contentLen > 0) {
                    ((SocketWrapper) MapState.getMenuActions()).inputStream.skip(contentLen);
                } else {
                    ((SocketWrapper) MapState.getMenuActions()).close();
                }
                return null;
            } catch (Throwable unused2) {
                return null;
            }
        }
        ByteBuffer bodyBuf = readHttpBody(parseContentLength(headers));
        if (bodyBuf == null) {
            ((SocketWrapper) MapState.getMenuActions()).close();
            throw new IOException();
        }
        RuntimeState.addXmppTrafficBytes(bodyBuf.length);
        byte[] bArr3 = bodyBuf.data;
        int i3 = bodyBuf.length;
        if (MapState.isTileCacheEnabled()) {
            saveTileToCache(resource, bArr3, 0, i3);
        }
        TrafficAccounting.addXmppInbound(bodyBuf.length + TILE_TRAFFIC_OVERHEAD);
        return bodyBuf.toImage();
    }

    private static final String readHttpHeaders() {
        SocketWrapper socket = (SocketWrapper) MapState.getMenuActions();
        ByteBuffer buf = new ByteBuffer();
        int i = 0;
        while (true) {
            try {
                int i2 = socket.inputStream.read();
                if (i2 == -1) {
                    return null;
                }
                buf.writeByte(i2);
                if (i2 == 10) {
                    i += STATE_LF_INCREMENT;
                    if (i == HEADER_END_MARKER) {
                        return buf.getStringAndClear();
                    }
                } else {
                    i = i2 == 13 ? i + STATE_CR_INCREMENT : 0;
                }
            } catch (Throwable unused) {
                return null;
            }
        }
    }

    private static final ByteBuffer readHttpBody(int i) {
        if (i <= 0) {
            return null;
        }
        try {
            ByteBuffer buf = new ByteBuffer();
            int bytesRead = 0;
            int i2 = 0;
            byte[] readBuf = ObjectPool.newBytes(8192);
            int length = readBuf.length;
            SocketWrapper socket = (SocketWrapper) MapState.getMenuActions();
            while (i2 != i && bytesRead != -1) {
                bytesRead = socket.read(readBuf, 0, Utils.min(length, i - i2));
                buf.writeBytesAt(readBuf, 0, bytesRead);
                i2 += bytesRead;
            }
            ObjectPool.releaseBytes(readBuf);
            return buf;
        } catch (Throwable unused) {
            return null;
        }
    }

    private static final int parseHttpStatus(String str) {
        try {
            return Integer.parseInt(StringUtils.substring(str, HTTP_STATUS_OFFSET, HTTP_STATUS_LENGTH));
        } catch (Throwable unused) {
            return 0;
        }
    }

    private static final int parseContentLength(String str) {
        try {
            int headerOffset = StringUtils.indexOfPoolString(StringUtils.intern(str.toLowerCase()), 1052310) + CONTENT_LENGTH_VALUE_OFFSET;
            return Integer.parseInt(StringUtils.substring(str, headerOffset, str.indexOf(13, headerOffset)));
        } catch (Throwable unused) {
            return -1;
        }
    }

    public static void removeTileRequest(TileRequest tile) {
        Vector requestQueue = ChatState.getTileRequestQueue();
        synchronized (requestQueue) {
            requestQueue.removeElement(tile);
        }
    }

    public static TileRequest peekTileRequest() {
        TileRequest tile;
        Vector requestQueue = ChatState.getTileRequestQueue();
        synchronized (requestQueue) {
            tile = (TileRequest) (requestQueue.size() != 0 ? requestQueue.firstElement() : null);
        }
        return tile;
    }

    public static void enqueueTileRequest(TileRequest tile) {
        Vector requestQueue = ChatState.getTileRequestQueue();
        synchronized (requestQueue) {
            if (!requestQueue.contains(tile)) {
                if (tile.tileType == TileRequest.TYPE_OVERLAY) {
                    requestQueue.addElement(tile);
                } else {
                    int size = requestQueue.size();
                    while (size > 0 && ((TileRequest) requestQueue.elementAt(size - 1)).tileType != TileRequest.TYPE_MAP) {
                        size--;
                    }
                    requestQueue.insertElementAt(tile, size);
                }
            }
        }
    }
}
