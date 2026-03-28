package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.UIKeys;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public final class IOUtils {

    public static Vector openResources;

    public static final Object registerResource(Object obj) {
        if (obj != null) {
            openResources.addElement(obj);
        }
        return obj;
    }

    public static final void unregisterResource(Object obj) {
        if (obj != null) {
            Utils.removeFrom(openResources, obj);
        }
    }

    public static final void closeInput(InputStream inputStream) {
        if (inputStream != null) {
            try {
                unregisterResource(inputStream);
                inputStream.close();
            } catch (Throwable unused) {
            }
        }
    }

    public static final void closeOutput(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                unregisterResource(outputStream);
                outputStream.close();
            } catch (Throwable unused) {
            }
        }
    }

    public static final void closeConn(Connection connection) {
        if (connection != null) {
            try {
                unregisterResource(connection);
                connection.close();
            } catch (Throwable unused) {
            }
        }
    }

    public static final void closeRecordStore(RecordStore recordStore) {
        if (recordStore != null) {
            try {
                unregisterResource(recordStore);
                recordStore.closeRecordStore();
            } catch (Throwable unused) {
            }
        }
    }

    public static final RecordStore openRecordStore(String str, boolean z) throws RecordStoreException {
        return (RecordStore) registerResource((Object) RecordStore.openRecordStore(str, z));
    }

    public static final void setSelectedItems(Object obj) {
        AppState.pool[UIKeys.SLOT_MEDIA_STREAM] = obj;
    }
}
