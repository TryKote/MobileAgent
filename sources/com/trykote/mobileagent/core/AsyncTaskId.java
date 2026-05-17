package com.trykote.mobileagent.core;

public final class AsyncTaskId {
    public static final int PROCESS_SOFTKEY = 0;
    public static final int DOWNLOAD_PHOTO = 1;
    public static final int FETCH_TILE_BUFFER = 2;
    public static final int CONNECTION_LOOP = 3;
    public static final int SOCKET_READER = 4;
    public static final int API_REAUTH = 5;
    public static final int FETCH_CITY_ZOOM = 6;
    public static final int DELAYED_CLOSE = 7;
    public static final int TILE_LOADER = 8;
    public static final int FETCH_MAP_POINTS = 9;
    public static final int FETCH_GEO_CONFIG = 10;
    public static final int FETCH_MMP_ROUTE = 11;
    public static final int PERIODIC_TIME_SYNC = 13;
    public static final int DOWNLOAD_CACHED_PHOTO = 14;
    public static final int FETCH_SHARED_CONTACTS = 15;
    public static final int HTTP_FIRE_AND_FORGET = 16;
    public static final int SEND_SMS_REQUEST = 17;
    public static final int SEND_DIAGNOSTIC = 18;
    public static final int FETCH_SAVED_LOCATIONS = 19;
    public static final int PARSE_CONTACTS_SYNC = 20;
    public static final int PARSE_CONTACTS_ASYNC = 21;
    public static final int FETCH_LOCATION_PROFILE = 22;
    public static final int EXECUTE_REGISTRATION = 24;
    public static final int SEND_SMS_DIRECT = 26;
    public static final int WAIT_FOR_COMPLETION = 27;
    public static final int PROCESS_XMPP_STREAM = 29;
    public static final int PERFORM_XMPP_AUTH = 30;
    public static final int FETCH_HISTORY = 31;
    public static final int FETCH_UPDATE_STATUS = 32;
    public static final int RESOLVE_XMPP_SERVER = 33;
    public static final int XMPP_HTTP_AUTH = 34;
    public static final int DOWNLOAD_INLINE_IMAGE = 35;

    private AsyncTaskId() {}
}
