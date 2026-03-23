package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.*;
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

    /* renamed from: a */
    public static final ByteBuffer createMrimPacket(MrimAccount account, int command, ByteBuffer payload) {
        ByteBuffer header = new ByteBuffer().writeIntLE(-559038737).writeIntLE(65557);
        int sequenceNum = account.state;
        account.state = sequenceNum + 1;
        return header.writeIntLE(sequenceNum).writeIntLE(command).writeIntLE(payload != null ? payload.length : 0).writeZeros(24).writeBuffer(payload);
    }

    /* renamed from: a */
    public static final ByteBuffer createPingPacket(MmpProtocol protocol, int i) {
        ByteBuffer packet = new ByteBuffer().writeByte(42).writeByte(i);
        int sequenceNum = protocol.state + 1;
        protocol.state = sequenceNum;
        return packet.writeShortBE((sequenceNum & 0xFFFFFF) % 32768).writeShortBE(0);
    }

    /* renamed from: a */
    public static final ByteBuffer createAuthData(MmpProtocol protocol) {
        ByteBuffer buffer = new ByteBuffer().writeShortBE(5);
        int authSlot = protocol.reserved2;
        buffer = buffer.writeShortBE(64 + (authSlot == 0 ? 0 : 16));
        int fieldIndex = 4;
        while (true) {
            fieldIndex--;
            if (fieldIndex < 0) {
                break;
            }
            buffer.writeCompressed(fieldIndex + 904);
        }
        if (authSlot != 0) {
            buffer.writeBytesAt(AppState.getBytes(908), (authSlot - 1) << 4, 16);
        }
        return createMmpCommand(protocol, 516, buffer);
    }

    /* renamed from: a */
    public static final ByteBuffer createMrimAuthPacket(MrimAccount account) {
        return createMrimPacket(account, 4097, new ByteBuffer().writeIntLE(120));
    }

    /* renamed from: b */
    public static final ByteBuffer createPasswordAuthCmd(MrimAccount account, String str) {
        return createMrimPacket(account, 4128, new ByteBuffer().writeStringLatin1(str));
    }

    /* renamed from: a */
    public static final ByteBuffer createChatRoomCmd(MrimAccount account, String str, int i) {
        ByteBuffer buffer = new ByteBuffer().writeIntLE(0);
        int atIndex = str.indexOf(64);
        return account.createAndQueueCommand(new Object[]{createMrimPacket(account, 4137, buffer.writeStringLatin1(StringUtils.prefix(str, atIndex)).writeIntLE(1).writeStringLatin1(StringUtils.suffix(str, atIndex + 1))), ResourceManager.integerOf(i)});
    }

    /* renamed from: a */
    public static final ByteBuffer createMmpCommand(MmpProtocol protocol, int command, ByteBuffer buffer) {
        ByteBuffer cmdBuffer = createPingPacket(protocol, 2).writeShortBE(command >> 8).writeShortBE(command & 255).writeShortBE(0);
        int sequenceNum = protocol.messageSequence + 1;
        protocol.messageSequence = sequenceNum;
        return cmdBuffer.writeIntBE(sequenceNum).writeBuffer(buffer).updateLength();
    }
}
