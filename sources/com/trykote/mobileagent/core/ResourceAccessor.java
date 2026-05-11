package com.trykote.mobileagent.core;

/**
 * Static utility for read-only access to string resources and byte data.
 * Replaces ResourceAccessor.str(key) / getBytes(key) pattern.
 *
 * All StringResKeys and PackedStringKeys lookups should go through here.
 */
public final class ResourceAccessor {
    private ResourceAccessor() {}

    /** Get a string resource by key (StringResKeys / PackedStringKeys). */
    public static String str(int key) {
        return AppState.getString(key);
    }

    /** Get a byte array resource by key. */
    public static byte[] bytes(int key) {
        return AppState.getBytes(key);
    }

    /** Read a string from a contiguous block: getString(baseKey + offset). */
    public static String blockStr(int baseKey, int offset) {
        return AppState.getString(baseKey + offset);
    }
}
