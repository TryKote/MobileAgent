package com.trykote.mobileagent.core;

public final class ProtocolEvent extends Event {
    public static final int MAP_LOCATIONS_LOADED = 3;
    public static final int PHONE_SEARCH_RESULT = 4;
    public static final int ADD_CONTACT_CONFIRM = 5;
    public static final int ACCOUNT_SYNC = 6;
    public static final int MAP_CONTROL = 7;

    public final int type;
    public final Object data;

    public ProtocolEvent(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
