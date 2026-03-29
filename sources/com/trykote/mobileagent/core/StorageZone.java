package com.trykote.mobileagent.core;

import java.util.Vector;

/**
 * Base class for typed access to the AppState object/int pool.
 * Subclasses provide read-only or read-write semantics.
 */
public abstract class StorageZone {

    public abstract String getString(int key);

    public abstract int getInt(int key);

    public abstract boolean getBool(int key);

    public abstract long getLong(int key);

    public abstract byte[] getBytes(int key);

    public abstract Vector getVector(int key);

    public abstract Object getObject(int key);

    /** Read a string from a contiguous block: getString(baseKey + offset). */
    public String getBlockString(int baseKey, int offset) {
        return getString(baseKey + offset);
    }

    /** Read an int from a contiguous block: getInt(baseKey + offset). */
    public int getBlockInt(int baseKey, int offset) {
        return getInt(baseKey + offset);
    }
}
