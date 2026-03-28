package com.trykote.mobileagent.core;

public final class Debug {
    private Debug() {}

    public static final boolean ENABLED = true;

    public static void assertState(boolean condition, String message) {
        if (ENABLED && !condition) {
            throw new RuntimeException("Assert: " + message);
        }
    }

    public static void assertNotNull(Object obj, String name) {
        if (ENABLED && obj == null) {
            throw new RuntimeException("Null: " + name);
        }
    }
}
