package com.trykote.mobileagent.protocol.mrim;

public final class MrimCommand {

    // Header constants
    public static final int MAGIC = -559038737; // 0xDEADBEEF
    public static final int VERSION = 65557;

    // Client-server command codes
    public static final int CS_HELLO = 4097;
    public static final int CS_HELLO_ACK = 4098;
    public static final int CS_LOGOUT = 4100;
    public static final int CS_MAIL_NOTIFY = 4101;
    public static final int CS_PING = 4102;
    public static final int CS_MESSAGE = 4104;
    public static final int CS_MESSAGE_ACK = 4105;
    public static final int CS_USER_STATUS = 4111;
    public static final int CS_CONTACT_LIST_REPLY = 4114;
    public static final int CS_LOGOUT_FORCE = 4115;
    public static final int CS_USER_INFO = 4117;
    public static final int CS_ADD_CONTACT = 4121;
    public static final int CS_ADD_CONTACT_ACK = 4122;
    public static final int CS_MODIFY_CONTACT = 4123;
    public static final int CS_MODIFY_CONTACT_ACK = 4124;
    public static final int CS_OFFLINE_MESSAGE_ACK = 4125;
    public static final int CS_DELETE_OFFLINE_MESSAGE = 4126;
    public static final int CS_AUTHORIZE = 4128;
    public static final int CS_AUTHORIZE_ACK = 4129;
    public static final int CS_CHANGE_STATUS = 4130;
    public static final int CS_CONTACT_LIST_ACK = 4133;
    public static final int CS_ANKETA_UPDATE_ACK = 4136;
    public static final int CS_WP_REQUEST = 4137;
    public static final int CS_CONTACT_LIST2 = 4151;
    public static final int CS_SEARCH_RESULT_ACK = 4160;
    public static final int CS_WP_REQUEST2 = 4162;
    public static final int CS_ANKETA_INFO = 4163;
    public static final int CS_MAILBOX_STATUS = 4168;
    public static final int CS_MPOP_SESSION = 4180;
    public static final int CS_MPOP_SESSION_ACK = 4182;
    public static final int CS_LOGOUT_CMD = 4194;
    public static final int CS_CUSTOM_NOTE = 4195;
    public static final int CS_ANKETA_UPDATE = 4213;
    public static final int CS_ANKETA_UPDATE_PHOTOS = 4214;
    public static final int CS_ANKETA_INFO_ACK = 4215;
    public static final int CS_LOGIN2 = 4216;
    public static final int CS_PROFILE_DATA = 4229;

    public static final int CS_AUTH_UPDATE = 4132;
    public static final int CS_MESSAGE_EXT = 4153;

    private MrimCommand() {}
}
