package com.trykote.mobileagent.protocol.mmp;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.util.*;

public abstract class MmpMessageParser {

    // File transfer message type IDs
    private static final int FILE_MSG_TEXT = 1;
    private static final int FILE_MSG_RICH = 2;
    private static final int FILE_MSG_NOTIFICATION = 4;

    // Encoding type values
    private static final int ENCODING_UNICODE = 2;

    // Message encoding TLV types
    static final int MSG_TLV_BODY = 257;
    static final int MSG_TLV_FLAGS = 262;
    static final int MSG_TLV_HEADER = 1281;
    static final int MSG_TLV_RICH_CONTAINER = 10001;

    // Message section types
    private static final int SECTION_TYPE_BODY = 2;
    private static final int SECTION_TYPE_RICH = 5;

    // Notification types
    private static final int NOTIF_TYPE_STANDARD = 1;
    private static final int NOTIF_TYPE_RICH = 4;

    // Rich message header skip size
    private static final int RICH_MSG_COLOR_HEADER_SIZE = 24;

    // Sender filter values
    private static final int ENCODED_SYSTEM_SENDER = 875573297;
    private static final int ENCODED_BLOCKED_SENDER = 49;

    public static void handleFileTransfer(MmpProtocol protocol, ByteBuffer packet) {
        int bodyLength;
        String messageBody;
        String richText;
        String decodedText;
        long timestamp = packet.readLong();
        int messageType = packet.readShortBE();
        String senderId = packet.readLenPrefixStr();
        packet.readShortBE();
        int headerCount = packet.readShortBE();
        for (int headerIndex = headerCount - 1; headerIndex >= 0; headerIndex--) {
            packet.readShortBE();
            packet.skip(packet.readShortBE());
        }
        int sectionType;
        do {
            sectionType = packet.readShortBE();
            bodyLength = packet.readShortBE();
            if (sectionType != SECTION_TYPE_BODY && sectionType != SECTION_TYPE_RICH) {
                packet.skip(bodyLength);
            }
        } while (sectionType != SECTION_TYPE_BODY && sectionType != SECTION_TYPE_RICH);
        switch (messageType) {
            case FILE_MSG_TEXT:
                int remaining = bodyLength;
                while (true) {
                    if (remaining <= 0) {
                        decodedText = null;
                        break;
                    } else {
                        int tlvType = packet.readShortBE();
                        int tlvLength = packet.readShortBE();
                        int afterHeader = remaining - 4;
                        if (tlvType == MSG_TLV_BODY) {
                            int encoding = packet.readShortBE();
                            packet.readShortBE();
                            decodedText = encoding == ENCODING_UNICODE ? packet.readUnicodeChars(tlvLength - 4) : packet.readByteChars(tlvLength - 4);
                            break;
                        } else {
                            packet.skip(tlvLength);
                            remaining = afterHeader - tlvLength;
                        }
                    }
                }
                messageBody = decodedText;
                break;
            case FILE_MSG_RICH:
                if (packet.readShortBE() == 0) {
                    packet.skip(RICH_MSG_COLOR_HEADER_SIZE);
                    int richRemaining = bodyLength - 26;
                    while (richRemaining > 0) {
                        int tlvType = packet.readShortBE();
                        int tlvLength = packet.readShortBE();
                        richRemaining -= tlvLength + 4;
                        if (tlvType == MSG_TLV_RICH_CONTAINER) {
                            packet.readShortLE();
                            packet.readShortLE();
                            int colorWord1 = packet.readIntBE();
                            int colorWord2 = packet.readIntBE();
                            int colorWord3 = packet.readIntBE();
                            int colorWord4 = packet.readIntBE();
                            packet.readShortBE();
                            packet.readInt();
                            packet.readByte();
                            packet.readShortBE();
                            int fontDataLen = packet.readShortLE();
                            packet.readShortBE();
                            packet.skip(fontDataLen - 2);
                            if ((colorWord1 | colorWord2 | colorWord3 | colorWord4) == 0) {
                                packet.readShortBE();
                                packet.readShortLE();
                                packet.readShortLE();
                                richText = packet.readModifiedStrTrim();
                            } else {
                                richText = null;
                            }
                            messageBody = richText;
                            if (richText != null && messageBody.length() > 0) {
                                protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, MmpCommand.MSG_DELIVERED, new ByteBuffer().writeLong(timestamp).writeShortBE(2).writeByteLenStr(senderId).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER).writeShortLE(protocol.getConnectionModeValue()).writeCompressed(PackedStringKeys.MMP_CAPS_HEADER_2)));
                                break;
                            }
                        } else {
                            packet.skip(tlvLength);
                        }
                    }
                    messageBody = null;
                } else {
                    messageBody = null;
                }
                break;
            case 3:
            default:
                messageBody = null;
                break;
            case FILE_MSG_NOTIFICATION:
                packet.readIntBE();
                int notifType = packet.readShortBE();
                messageBody = (notifType == NOTIF_TYPE_STANDARD || notifType == NOTIF_TYPE_RICH) ? packet.readByteChars(packet.readShortLE() - 1) : null;
                break;
        }
        if (!Utils.nonEmpty(messageBody) || StringUtils.matchesEncoded(senderId, ENCODED_SYSTEM_SENDER)) {
            return;
        }
        if (StringUtils.matchesEncoded(senderId, ENCODED_BLOCKED_SENDER)) {
            return;
        }
        protocol.onMessage(senderId, 0L, messageBody);
    }

    public static ByteBuffer createSendMessageCmd(MmpProtocol protocol, MmpContact contact, String messageText) {
        return ProtocolFactory.createMmpCommand(protocol, MmpCommand.SEND_AUTH_MESSAGE, new ByteBuffer().writeByteLenStr(contact.identifier).writeUTF(messageText).writeShortBE(0));
    }

    static void writeTaggedStr(ByteBuffer buf, int tag, String value) {
        int len = value.length();
        if (len > 0) {
            buf.writeShortBE(tag).writeShortLE(len + 3).writeShortLE(len + 1).writeCharBytes(value).writeByte(0);
        }
    }
}
