package com.trykote.mobileagent.core;

/**
 * Typed facade over AppState with two zones:
 * - state(): read-write zone for runtime flags, counters, session data
 * - resources(): read-only zone for immutable strings, byte arrays, lists
 */
public abstract class Storage {

    private static ReadWriteZone stateZone;
    private static ReadOnlyZone resourceZone;

    /** Empty string constant (initialized from pool). */
    public static String emptyStr;
    /** Empty byte array constant. */
    public static byte[] emptyBytes;
    /** Pending screen to display on next repaint. */
    public static Object currentScreen;
    /** Packed string encoding threshold. IDs above this are offset/length packed. */
    public static final int PACKED_STRING_THRESHOLD = 5179;

    public static ReadWriteZone state() {
        return stateZone;
    }

    public static ReadOnlyZone resources() {
        return resourceZone;
    }

    static void init() {
        stateZone = new ReadWriteZone();
        resourceZone = new ReadOnlyZone();
    }

    /** Called after initObjectPool to copy shared constants. */
    static void initConstants() {
        emptyStr = AppState.emptyStr;
        emptyBytes = AppState.emptyBytes;
    }
}
