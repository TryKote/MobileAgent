package com.trykote.mobileagent.util;


import javax.microedition.rms.RecordStore;

public final class ChunkedRecordStore {

    public static final ByteBuffer readChunkedRecord(String str) {
        RemoteLogger.log("PERSIST", "readChunkedRecord: name='" + str + "'");
        ByteBuffer buf = new ByteBuffer();
        int i = 0;
        while (true) {
            RecordStore recordStore = null;
            try {
                int i2 = i;
                i++;
                String chunkName = buildChunkName(str, i2);
                RecordStore store = IOUtils.openRecordStore(chunkName, false);
                recordStore = store;
                byte[] record = store.getRecord(1);
                RemoteLogger.log("PERSIST", "readChunkedRecord: chunk " + i2 + " ('" + chunkName + "') = " + record.length + " bytes");
                buf.writeBytes(record);
                ObjectPool.releaseBytes(record);
                IOUtils.closeRecordStore(recordStore);
            } catch (RuntimeException th) {
                IOUtils.closeRecordStore(recordStore);
                throw th;
            } catch (Throwable th) {
                IOUtils.closeRecordStore(recordStore);
                RemoteLogger.log("PERSIST", "readChunkedRecord: done, total " + buf.length + " bytes (" + i + " chunks tried, exception: " + th.getClass().getName() + ")");
                return buf;
            }
        }
    }

    public static final void writeRecord(String str, ByteBuffer buf, boolean z) {
        RemoteLogger.log("PERSIST", "writeRecord: name='" + str + "', length=" + buf.length + ", chunked=" + z);
        if (z) {
            writeChunkedRecord(str, buf);
            return;
        }
        int i = buf.length;
        if (i == 0) {
            RemoteLogger.log("PERSIST", "writeRecord: DELETING (empty buffer)");
            String[] storeNames = StringUtils.listRecordStores();
            int i2 = 0;
            while (true) {
                int i3 = i2;
                i2++;
                String chunkName = buildChunkName(str, i3);
                if (!recordStoreExists(storeNames, chunkName)) {
                    break;
                } else {
                    try {
                        RecordStore.deleteRecordStore(chunkName);
                    } catch (Throwable unused) {
                    }
                }
            }
        } else {
            RecordStore recordStore = null;
            try {
                byte[] bArr = buf.compact().data;
                RecordStore store = IOUtils.openRecordStore(str, true);
                recordStore = store;
                if (store.getNumRecords() == 0) {
                    recordStore.addRecord(bArr, 0, i);
                } else {
                    recordStore.setRecord(1, bArr, 0, i);
                }
                String[] storeNames2 = StringUtils.listRecordStores();
                int i4 = 0;
                while (true) {
                    i4++;
                    String chunkName2 = buildChunkName(str, i4);
                    if (!recordStoreExists(storeNames2, chunkName2)) {
                        break;
                    } else {
                        try {
                            RecordStore.deleteRecordStore(chunkName2);
                        } catch (Throwable unused2) {
                        }
                    }
                }
                IOUtils.closeRecordStore(recordStore);
                RemoteLogger.log("PERSIST", "writeRecord: OK, wrote " + i + " bytes to '" + str + "'");
            } catch (RuntimeException th) {
                IOUtils.closeRecordStore(recordStore);
                RemoteLogger.log("PERSIST", "writeRecord FAILED (RE): " + th);
                throw th;
            } catch (Throwable th) {
                IOUtils.closeRecordStore(recordStore);
                RemoteLogger.log("PERSIST", "writeRecord FAILED: " + th);
                throw new RuntimeException(th);
            }
        }
        buf.clear();
    }

    public static final void writeChunkedRecord(String str, ByteBuffer buf) {
        int i = 0;
        int i2 = buf.length;
        if (i2 > 0) {
            byte[] bArr = buf.compact().data;
            int written = 0;
            while (true) {
                int i3 = written;
                if (i3 >= i2) {
                    break;
                }
                int i4 = i;
                i++;
                written = i3 + writeRecordChunk(buildChunkName(str, i4), bArr, i3, i2 - i3);
            }
        }
        String[] storeNames = StringUtils.listRecordStores();
        while (true) {
            int i5 = i;
            i++;
            String chunkName = buildChunkName(str, i5);
            if (!recordStoreExists(storeNames, chunkName)) {
                return;
            } else {
                try {
                    RecordStore.deleteRecordStore(chunkName);
                } catch (Throwable unused) {
                }
            }
        }
    }

    private static final int writeRecordChunk(String str, byte[] bArr, int i, int i2) {
        RecordStore recordStore = null;
        try {
            try {
                RecordStore.deleteRecordStore(str);
            } catch (Throwable unused) {
            }
            RecordStore store = IOUtils.openRecordStore(str, true);
            recordStore = store;
            int chunkSize = Utils.min(i2, Utils.max(recordStore.getSizeAvailable() - 128, 2048));
            store.addRecord(bArr, i, chunkSize);
            IOUtils.closeRecordStore(recordStore);
            return chunkSize;
        } catch (RuntimeException th) {
            IOUtils.closeRecordStore(recordStore);
            throw th;
        } catch (Throwable th) {
            IOUtils.closeRecordStore(recordStore);
            throw new RuntimeException(th);
        }
    }

    private static final boolean recordStoreExists(String[] strArr, String str) {
        if (strArr == null) {
            return false;
        }
        int length = strArr.length;
        do {
            length--;
            if (length < 0) {
                return false;
            }
        } while (!str.equals(strArr[length]));
        return true;
    }

    private static final String buildChunkName(String str, int i) {
        if (i == 0) {
            return str.length() <= 32 ? str : StringUtils.prefix(str, 32);
        }
        StringBuffer sb = ObjectPool.newStringBuffer().append(i);
        StringBuffer sb2 = ObjectPool.newStringBuffer().append('s').append(str).append('s');
        while (sb2.length() + sb.length() > 32) {
            sb2.setLength(sb2.length() - 1);
        }
        return ObjectPool.toStringAndRelease(sb2.append((Object) sb));
    }
}
