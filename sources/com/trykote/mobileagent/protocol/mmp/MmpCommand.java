package com.trykote.mobileagent.protocol.mmp;

public final class MmpCommand {

    // Capability and authentication
    public static final int SET_CAPABILITIES = 258;
    public static final int AUTH_RESULT = 271;
    public static final int SET_STATUS = 286;
    public static final int AUTH_REQUEST = 287;
    public static final int EXTENDED_AUTH = 516;

    // Contact presence
    public static final int CONTACT_ONLINE = 779;
    public static final int CONTACT_OFFLINE = 780;

    // Messaging
    public static final int ACK = 1025;
    public static final int SET_PREFS = 1026;
    public static final int SEND_MESSAGE = 1030;
    public static final int FILE_TRANSFER = 1031;
    public static final int MSG_DELIVERED = 1035;
    public static final int MSG_READ = 1036;
    public static final int CONTACT_STATUS_CHANGE = 1044;

    // Contact list management
    public static final int GET_CONTACT_LIST = 4868;
    public static final int CONTACT_LIST_RESPONSE = 4870;
    public static final int CONTACT_LIST_ACK = 4871;
    public static final int ADD_CONTACT = 4872;
    public static final int MODIFY_CONTACT = 4873;
    public static final int DELETE_CONTACT = 4874;
    public static final int CONTACT_INFO_RESPONSE = 4878;
    public static final int GET_CONTACTS_SYNC = 4881;
    public static final int SYNC_CONTACTS = 4882;
    public static final int AUTH_GRANT = 4884;
    public static final int TYPING_NOTIFY = 4885;
    public static final int SEND_AUTH_MESSAGE = 4888;
    public static final int MESSAGE_RECEIVED = 4889;
    public static final int AUTH_RECEIVED = 4891;
    public static final int SYSTEM_MESSAGE = 4892;

    // Search and spam
    public static final int SPAM_REPORT_ACK = 5377;
    public static final int SEARCH = 5378;
    public static final int SEARCH_RESPONSE = 5379;

    // Packet types
    public static final int PACKET_HANDSHAKE = 1;
    public static final int PACKET_COMMAND = 2;
    public static final int PACKET_NOTIFICATION = 4;
    public static final int PACKET_DISCONNECT = 4;
    public static final int PACKET_KEEPALIVE = 5;

    private MmpCommand() {}
}
