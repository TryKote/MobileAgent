package com.trykote.mobileagent.util;

/**
 * Build-time test account configuration.
 * Values below are defaults; Makefile replaces them from config/test_account.cfg
 */
public final class TestConfig {
    public static final boolean ENABLED = false;            // @@TEST_ACCOUNT_ENABLED@@
    public static final String LOGIN = "";                  // @@TEST_ACCOUNT_LOGIN@@
    public static final String PASSWORD = "";               // @@TEST_ACCOUNT_PASSWORD@@
    public static final int ACCOUNT_TYPE = 0;               // @@TEST_ACCOUNT_TYPE@@
    public static final boolean USE_TLS = false;            // @@TEST_ACCOUNT_USE_TLS@@
}
