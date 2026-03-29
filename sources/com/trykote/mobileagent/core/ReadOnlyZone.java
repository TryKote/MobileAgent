package com.trykote.mobileagent.core;

import java.util.Vector;

/**
 * Read-only access to the AppState pool (resource strings, byte arrays, etc.).
 * Delegates all reads to AppState.
 */
public final class ReadOnlyZone extends StorageZone {

    public String getString(int key) {
        return AppState.getString(key);
    }

    public int getInt(int key) {
        return AppState.getInt(key);
    }

    public boolean getBool(int key) {
        return AppState.getBool(key);
    }

    public long getLong(int key) {
        return AppState.getLong(key);
    }

    public byte[] getBytes(int key) {
        return AppState.getBytes(key);
    }

    public Vector getVector(int key) {
        return AppState.getVector(key);
    }

    public Object getObject(int key) {
        return AppState.pool[key];
    }
}
