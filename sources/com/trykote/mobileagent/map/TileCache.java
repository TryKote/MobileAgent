package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;

public final class TileCache {

    public static final void calculateCacheSize() {
        int size = 0;
        String[] storeNames = StringUtils.listRecordStores();
        if (storeNames != null) {
            int length = storeNames.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                String str = storeNames[length];
                if (str.startsWith(AppState.getString(StringResKeys.STR_RES_URL_PARAM_1))) {
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
        AppState.setInt(MapKeys.INT_TILE_CACHE_SIZE, size);
    }

    private static void saveTileToCache(ResourceManager resource, byte[] bArr, int i, int i2) {
        if (resource == null || bArr == null) {
            return;
        }
        String cacheKey = buildTileCacheKey(resource);
        ByteBuffer tileData = new ByteBuffer().writeStringLatin1(resource.tileUrl).writeLong(System.currentTimeMillis()).writeBytesAt(bArr, 0, i2);
        int i3 = 4;
        while (true) {
            i3--;
            if (i3 <= 0) {
                return;
            }
            try {
                try {
                    if (tileData.length + AppState.getInt(MapKeys.INT_TILE_CACHE_SIZE) >= 204800) {
                        throw new Throwable();
                    }
                    RecordStore store = IOUtils.openRecordStore(cacheKey, true);
                    byte[] bArr2 = tileData.data;
                    int i4 = tileData.offset;
                    int i5 = tileData.length;
                    store.addRecord(bArr2, i4, i5);
                    AppState.setInt(MapKeys.INT_TILE_CACHE_SIZE, AppState.getInt(MapKeys.INT_TILE_CACHE_SIZE) + i5);
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

    public static final Image loadTileFromCache(ResourceManager resource) {
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
            throw new RuntimeException(th);
        }
    }

    private static final String findOldestCacheStore() {
        String str = null;
        long j = 0;
        String[] storeNames = StringUtils.listRecordStores();
        if (storeNames != null) {
            String cachePrefix = AppState.getString(StringResKeys.STR_RES_URL_PARAM_1);
            int length = storeNames.length;
            while (true) {
                length--;
                if (length < 0) {
                    break;
                }
                String str2 = storeNames[length];
                if (str2.startsWith(cachePrefix)) {
                    RecordStore store = null;
                    try {
                        store = IOUtils.openRecordStore(str2, false);
                        long j2 = j;
                        long lastModified = store.getLastModified();
                        if (j2 > j2 || j == 0) {
                            j = lastModified;
                            str = str2;
                        }
                        IOUtils.closeRecordStore(store);
                    } catch (RuntimeException th) {
                        IOUtils.closeRecordStore(store);
                        throw th;
                    } catch (Throwable th) {
                        IOUtils.closeRecordStore(store);
                        throw new RuntimeException(th);
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
                    AppState.setInt(MapKeys.INT_TILE_CACHE_SIZE, AppState.getInt(MapKeys.INT_TILE_CACHE_SIZE) - recordStore.getRecordSize(i));
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

    private static final String buildTileCacheKey(ResourceManager resource) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_RES_URL_PARAM_1)).append(resource.tileType).append('z').append(resource.zoomLevel).append('x').append((resource.tileX / 4) << 2).append('y').append((resource.tileY / 4) << 2));
    }

    private static final boolean reconnectHttp() {
        try {
            NetworkLock.acquireNetworkLock();
            SocketWrapper oldSocket = (SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS];
            if (oldSocket != null) {
                oldSocket.close();
            }
            AppState.pool[MapKeys.OBJ_MENU_ACTIONS] = SocketWrapper.open(new ByteBuffer().writeCompressed(PackedStringKeys.SCHEME_SOCKET).writeCompressed(PackedStringKeys.HOST_MOBILEMAPS_2043).getStringAndClear(), false);
            return true;
        } catch (Throwable unused) {
            return false;
        } finally {
            NetworkLock.releaseNetworkLock();
        }
    }

    public static final Image fetchTileImage(ResourceManager resource) throws IOException {
        ByteBuffer requestBuf = new ByteBuffer().writeCompressed(PackedStringKeys.HTTP_GET_TILESENDER).writeRawString(resource.tileUrl).writeCompressed(PackedStringKeys.HTTP_MAP_TILE_HEADER).writeExtendedInt(2950495).writeEncodedInt(222).writeCompressed(PackedStringKeys.HTTP_TILE_HEADERS);
        try {
            SocketWrapper socket = (SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS];
            byte[] bArr = requestBuf.data;
            int i = requestBuf.length;
            socket.write(bArr, i);
            TrafficAccounting.addUploadBytes(i);
        } catch (Throwable unused) {
            if (!reconnectHttp()) {
                throw new IOException();
            }
            SocketWrapper socket2 = (SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS];
            byte[] bArr2 = requestBuf.data;
            int i2 = requestBuf.length;
            socket2.write(bArr2, i2);
            TrafficAccounting.addUploadBytes(i2);
        } finally {
            requestBuf.clear();
        }
        String headers = readHttpHeaders();
        if (headers == null) {
            ((SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS]).close();
            throw new IOException();
        }
        AppState.addInt(RuntimeKeys.INT_XMPP_TRAFFIC_BYTES, headers.getBytes().length);
        if (parseHttpStatus(headers) != 200) {
            int contentLen = parseContentLength(headers);
            try {
                if (contentLen > 0) {
                    ((SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS]).inputStream.skip(contentLen);
                } else {
                    ((SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS]).close();
                }
                return null;
            } catch (Throwable unused2) {
                return null;
            }
        }
        ByteBuffer bodyBuf = readHttpBody(parseContentLength(headers));
        if (bodyBuf == null) {
            ((SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS]).close();
            throw new IOException();
        }
        AppState.addInt(RuntimeKeys.INT_XMPP_TRAFFIC_BYTES, bodyBuf.length);
        byte[] bArr3 = bodyBuf.data;
        int i3 = bodyBuf.length;
        if (AppState.getBool(MapKeys.FLAG_TILE_CACHE_ENABLED)) {
            saveTileToCache(resource, bArr3, 0, i3);
        }
        TrafficAccounting.addDownloadBytes(bodyBuf.length + 255);
        return bodyBuf.toImage();
    }

    private static final String readHttpHeaders() {
        SocketWrapper socket = (SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS];
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
                    i++;
                    if (i == 34) {
                        return buf.getStringAndClear();
                    }
                } else {
                    i = i2 == 13 ? i + 16 : 0;
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
            SocketWrapper socket = (SocketWrapper) AppState.pool[MapKeys.OBJ_MENU_ACTIONS];
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
            return Integer.parseInt(StringUtils.substring(str, 9, 12));
        } catch (Throwable unused) {
            return 0;
        }
    }

    private static final int parseContentLength(String str) {
        try {
            int headerOffset = StringUtils.indexOfPoolString(StringUtils.intern(str.toLowerCase()), 1052310) + 16;
            return Integer.parseInt(StringUtils.substring(str, headerOffset, str.indexOf(13, headerOffset)));
        } catch (Throwable unused) {
            return -1;
        }
    }
}
