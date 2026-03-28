package com.trykote.mobileagent.protocol.mrim;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Vector;

public final class MrimChatRoomManager {

    public Vector list;
    public boolean loaded;
    public String nickname;

    public int getCount() {
        if (this.list == null) {
            return 0;
        }
        return this.list.size();
    }

    public void parseFromJson(Object obj) {
        this.loaded = false;
        boolean hasExisting = true;
        if (this.list == null) {
            hasExisting = false;
            this.list = ObjectPool.newVector();
        }
        Object roomsArray = JsonParser.getValue(obj, AppState.getString(StateKeys.STR_RES_PARAM_3));
        for (int i = 0; i < ((Vector) roomsArray).size(); i++) {
            Object roomObj = JsonParser.getVectorElement(roomsArray, i);
            ChatRoom existingRoom = findById(JsonParser.getIntValue(roomObj, AppState.getString(StateKeys.STR_RES_AT_SIGN)));
            if (existingRoom == null) {
                this.list.addElement(new ChatRoom(roomObj));
            } else {
                existingRoom.parseJson(roomObj);
            }
        }
        this.nickname = JsonParser.getStringValue(obj, AppState.getString(StateKeys.STR_RES_HEADER_NAME_3));
        assignDefault(hasExisting);
    }

    public ChatRoom findById(int i) {
        Enumeration elements = this.list.elements();
        while (elements.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) elements.nextElement();
            if (chatRoom.id == i) {
                return chatRoom;
            }
        }
        return null;
    }

    public ChatRoom getLast() {
        return (ChatRoom) this.list.lastElement();
    }

    public ChatRoom findByName(String str) {
        Enumeration elements = this.list.elements();
        while (elements.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) elements.nextElement();
            if (chatRoom.hasMessage(str)) {
                return chatRoom;
            }
        }
        return null;
    }

    public void removeUser(String str) {
        int roomCount = getCount();
        while (true) {
            roomCount--;
            if (roomCount < 0) {
                return;
            }
            ChatRoom chatRoom = (ChatRoom) this.list.elementAt(roomCount);
            if (chatRoom.hasMessage(str)) {
                chatRoom.messageIds.removeElement(str);
                chatRoom.readMessages.removeElement(str);
                chatRoom.messages.remove(str);
                if (str.equals(chatRoom.subject)) {
                    chatRoom.subject = AppState.emptyStr;
                }
            }
        }
    }

    public ChatRoom findDefault() {
        ChatRoom defaultRoom = findByNameExact(AppState.getString(StateKeys.STR_MAIN_CHATROOM));
        return defaultRoom != null ? defaultRoom : findByNameExact(AppState.getString(StateKeys.STR_DEFAULT_CHATROOM));
    }

    void assignDefault(boolean hasExisting) {
        boolean found;
        if (hasExisting) {
            return;
        }
        int i = 0;
        do {
            found = false;
            i++;
            Enumeration elements = this.list.elements();
            while (elements.hasMoreElements()) {
                if (((ChatRoom) elements.nextElement()).id == i) {
                    found = true;
                }
            }
        } while (found);
        this.list.addElement(new ChatRoom(i));
    }

    private ChatRoom findByNameExact(String str) {
        Enumeration elements = this.list.elements();
        while (elements.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) elements.nextElement();
            if (chatRoom.name.equals(str)) {
                return chatRoom;
            }
        }
        return null;
    }

    public void serialize(ByteBuffer buffer) throws Throwable {
        int roomCount = getCount();
        if (this.nickname == null || roomCount < 3) {
            throw new Throwable();
        }
        buffer.writeStringUTF16(this.nickname);
        buffer.writeIntLE(0);
        buffer.writeIntLE(roomCount - 1);
        for (int i = 0; i < roomCount; i++) {
            ChatRoom chatRoom = (ChatRoom) this.list.elementAt(i);
            if (chatRoom != getLast()) {
                chatRoom.serialize(buffer);
            }
        }
        buffer.writeShortBE(21554);
    }

    public void deserialize(ByteBuffer extraBuffer) {
        this.nickname = extraBuffer.readUTF8Str((String) null);
        extraBuffer.readWideStr();
        this.list = ObjectPool.newVector();
        int chatRoomCount = extraBuffer.readInt();
        for (int i = 0; i < chatRoomCount; i++) {
            this.list.addElement(ChatRoom.deserialize(extraBuffer));
        }
        if (extraBuffer.readShortBE() != 21554) {
            throw new RuntimeException();
        }
        assignDefault(false);
    }
}
