package com.trykote.mobileagent.util;

/**
 * Build-time configuration for RemoteLogger.
 * Values below are defaults; Makefile replaces them from config/remote_logger.cfg
 */
public final class RemoteLoggerConfig {
    public static final boolean ENABLED = false;            // @@REMOTE_LOGGER_ENABLED@@
    public static final String HOST = "127.0.0.1";         // @@REMOTE_LOGGER_HOST@@
    public static final int PORT = 0;                       // @@REMOTE_LOGGER_PORT@@
}
