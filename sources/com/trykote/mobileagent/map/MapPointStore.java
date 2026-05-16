package com.trykote.mobileagent.map;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.net.RequestQueue;
import com.trykote.mobileagent.net.ServiceRegistry;
import com.trykote.mobileagent.util.Base64;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.Utils;

import java.util.Vector;

public final class MapPointStore {

    // Map data freshness
    private static final long MAP_DATA_FRESHNESS_MS = 45000L;

    public static long lastCheckTs;

    public static void addMapPointIfNew(Vector points, MapPoint point, int unused, int maxSize) {
        if (point == null || points.contains(point)) {
            return;
        }
        if (findMapPointByName(points, point.name) != null) {
            return;
        }
        if (maxSize > 0 && points.size() >= maxSize) {
            points.removeElementAt(0);
        }
        points.insertElementAt(point, 0);
    }

    private static MapPoint findMapPointByName(Vector points, String name) {
        try {
            for (int pi = points.size() - 1; pi >= 0; pi--) {
                MapPoint point = (MapPoint) points.elementAt(pi);
                if (name.equals(point.name)) {
                    return point;
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static Vector parseMapPointsFromStr(String data) {
        Vector points = ObjectPool.newVector();
        try {
            Vector lines = Utils.splitReplace(data, '\r', '\n');
            int lineCount = lines.size();
            for (int li = 0; li < lineCount; li++) {
                Vector fields = Utils.splitNonEmpty((String) lines.elementAt(li), '|');
                MapPoint point = new MapPoint((String) fields.elementAt(0), Long.parseLong((String) fields.elementAt(2)), Long.parseLong((String) fields.elementAt(1)), Utils.parseInt(fields.elementAt(3)));
                point.height = 1;
                point.typeCode = Utils.parseInt(fields.elementAt(4));
                point.objectCode = Utils.parseInt(fields.elementAt(5));
                points.addElement(point);
                ObjectPool.releaseVector(fields);
            }
            ObjectPool.releaseVector(lines);
            Utils.trimIfEmpty(points);
        } catch (Throwable unused) {
        }
        return points;
    }

    public static void saveMapPoints(Vector points, int stateKey) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            int size = points.size();
            buffer.writeIntLE(size);
            for (int pi = 0; pi < size; pi++) {
                MapPoint point = (MapPoint) points.elementAt(pi);
                buffer.writeStringUTF16(point.name).writeLong(point.boundsMinLon).writeLong(point.boundsMinLat).writeLong(point.boundsMaxLon).writeLong(point.boundsMaxLat).writeLong(point.longitude).writeLong(point.latitude).writeIntLE(point.zoomLevel).writeIntLE(point.height).writeIntLE(point.objectCode).writeIntLE(point.typeCode);
            }
            AppState.setObject(stateKey, (Object) buffer.toBase64());
        } catch (Throwable unused) {
        }
    }

    public static Vector loadMapPoints(int stateKey) {
        Vector points = ObjectPool.newVector();
        try {
            ByteBuffer buffer = Base64.decode(AppState.getString(stateKey));
            if (buffer.length > 4) {
                int count = buffer.readInt();
                for (int pi = 0; pi < count; pi++) {
                    points.addElement(new MapPoint(buffer));
                }
            }
            Utils.trimIfEmpty(points);
        } catch (Throwable unused) {
        }
        return points;
    }

    public static void startMapAnimation(Vector points) {
        for (int pi = points.size() - 1; pi >= 0; pi--) {
            ((MapPoint) points.elementAt(pi)).markInactive();
        }
    }

    public static void stopMapAnimation(Vector points) {
        for (int pi = points.size() - 1; pi >= 0; pi--) {
            ((MapPoint) points.elementAt(pi)).markActive();
        }
    }

    public static void initializeMapData() {
        RequestQueue.lastUpdateTs = System.currentTimeMillis();
        Vector contactIds = ServiceRegistry.getServiceContactIds(1);
        long lon = MapRenderer.currentLon;
        long lat = MapRenderer.currentLat;
        StringBuffer idList = ObjectPool.newStringBuffer();
        for (int si = contactIds.size() - 1; si >= 0; si--) {
            idList.append(contactIds.elementAt(si));
            if (si > 0) {
                idList.append(',');
            }
        }
        ByteBuffer requestUrl = new ByteBuffer().writeCharBytes("http://mobile.mail.ru/data/map_point/view_object?").writeUInt(15713);
        String contactIdStr = ObjectPool.toStringAndRelease(idList);
        new AsyncTask(AsyncTaskId.FETCH_SHARED_CONTACTS, requestUrl.writeRawString(contactIdStr).writeUInt(4022822).writeRawString(new ByteBuffer().writeRawString(contactIdStr).writeCharBytes("Secret_389").encryptMD5().toHexString()).writeUInt(4023078).writeLongAsString(lon).writeUInt(4023334).writeLongAsString(lat).getStringAndClear());
    }

    public static boolean isMapDataRecent() {
        return System.currentTimeMillis() - lastCheckTs < MAP_DATA_FRESHNESS_MS;
    }
}
