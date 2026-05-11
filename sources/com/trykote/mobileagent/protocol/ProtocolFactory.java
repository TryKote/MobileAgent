package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
/* Extracted from AppController: protocol packet construction */
public final class ProtocolFactory {

    // Character code for '@'
    private static final char CHAR_AT = '@';

    // MRIM packet header: 24 bytes of reserved zeros
    private static final int MRIM_HEADER_RESERVED_SIZE = 24;

    // MRIM hello timeout (seconds)
    private static final int MRIM_HELLO_TIMEOUT = 120;

    // MMP auth data sizes
    private static final int AUTH_DATA_BASE_SIZE = 64;
    private static final int AUTH_SLOT_SIZE = 16;
    private static final int AUTH_FIELD_COUNT = 4;
    private static final int AUTH_FIELDS_BASE_KEY = 904;

    // MMP packet header length (before body)
    private static final int MMP_HEADER_SIZE = 6;

    public static final ByteBuffer createMrimPacket(MrimAccount account, int command, ByteBuffer payload) {
        ByteBuffer header = new ByteBuffer().writeIntLE(MrimCommand.MAGIC).writeIntLE(MrimCommand.VERSION);
        int sequenceNum = account.state;
        account.state = sequenceNum + 1;
        return header.writeIntLE(sequenceNum).writeIntLE(command).writeIntLE(payload != null ? payload.length : 0).writeZeros(MRIM_HEADER_RESERVED_SIZE).writeBuffer(payload);
    }

    public static final ByteBuffer createPingPacket(MmpProtocol protocol, int i) {
        ByteBuffer packet = new ByteBuffer().writeByte(42).writeByte(i);
        int sequenceNum = protocol.state + 1;
        protocol.state = sequenceNum;
        return packet.writeShortBE((sequenceNum & 0xFFFFFF) % 32768).writeShortBE(0);
    }

    public static final ByteBuffer createAuthData(MmpProtocol protocol) {
        ByteBuffer buffer = new ByteBuffer().writeShortBE(5);
        int authSlot = protocol.reserved2;
        buffer = buffer.writeShortBE(AUTH_DATA_BASE_SIZE + (authSlot == 0 ? 0 : AUTH_SLOT_SIZE));
        for (int fieldIndex = AUTH_FIELD_COUNT - 1; fieldIndex >= 0; fieldIndex--) {
            buffer.writeCompressed(fieldIndex + AUTH_FIELDS_BASE_KEY);
        }
        if (authSlot != 0) {
            buffer.writeBytesAt(ResourceAccessor.bytes(StringResKeys.RES_AUTH_SLOT_GUIDS), (authSlot - 1) << 4, AUTH_SLOT_SIZE);
        }
        return createMmpCommand(protocol, MmpCommand.EXTENDED_AUTH, buffer);
    }

    public static final ByteBuffer createMrimAuthPacket(MrimAccount account) {
        return createMrimPacket(account, MrimCommand.CS_HELLO, new ByteBuffer().writeIntLE(MRIM_HELLO_TIMEOUT));
    }

    public static final ByteBuffer createPasswordAuthCmd(MrimAccount account, String str) {
        return createMrimPacket(account, MrimCommand.CS_AUTHORIZE, new ByteBuffer().writeStringLatin1(str));
    }

    public static final ByteBuffer createChatRoomCmd(MrimAccount account, String str, int i) {
        ByteBuffer buffer = new ByteBuffer().writeIntLE(0);
        int atIndex = str.indexOf(CHAR_AT);
        return account.createAndQueueCommand(new Object[]{createMrimPacket(account, MrimCommand.CS_WP_REQUEST, buffer.writeStringLatin1(StringUtils.prefix(str, atIndex)).writeIntLE(1).writeStringLatin1(StringUtils.suffix(str, atIndex + 1))), ObjectPool.integerOf(i)});
    }

    public static final ByteBuffer createMmpCommand(MmpProtocol protocol, int command, ByteBuffer buffer) {
        ByteBuffer cmdBuffer = createPingPacket(protocol, MmpCommand.PACKET_COMMAND).writeShortBE(command >> 8).writeShortBE(command & 255).writeShortBE(0);
        int sequenceNum = protocol.messageSequence + 1;
        protocol.messageSequence = sequenceNum;
        return updateMmpPacketLength(cmdBuffer.writeIntBE(sequenceNum).writeBuffer(buffer));
    }

    public static ByteBuffer updateMmpPacketLength(ByteBuffer packet) {
        packet.ensureCapacity(0);
        int bodyLength = packet.length - MMP_HEADER_SIZE;
        packet.data[4] = (byte) (bodyLength >> 8);
        packet.data[5] = (byte) bodyLength;
        return packet;
    }
}
