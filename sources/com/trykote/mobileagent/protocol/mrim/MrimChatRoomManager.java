package com.trykote.mobileagent.protocol.mrim;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Message;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.JsonParser;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class MrimChatRoomManager {

    // Serialization end marker
    private static final int SERIALIZATION_MARKER = 21554;

    // Minimum room count required for serialization
    private static final int MIN_ROOMS_FOR_SAVE = 3;

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
        Object roomsArray = JsonParser.getValue(obj, ResourceAccessor.str(PackedStringKeys.MAIL_PARAM_FLIST));
        for (int i = 0; i < ((Vector) roomsArray).size(); i++) {
            Object roomObj = ((Vector) roomsArray).elementAt(i);
            ChatRoom existingRoom = findById(JsonParser.getIntValue(roomObj, ResourceAccessor.str(PackedStringKeys.ATTR_ID_UPPER)));
            if (existingRoom == null) {
                this.list.addElement(new ChatRoom(roomObj));
            } else {
                existingRoom.parseJson(roomObj);
            }
        }
        this.nickname = JsonParser.getStringValue(obj, ResourceAccessor.str(PackedStringKeys.MAIL_FIELD_REAL_NAME));
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
        for (int i = getCount() - 1; i >= 0; i--) {
            ChatRoom chatRoom = (ChatRoom) this.list.elementAt(i);
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
        ChatRoom defaultRoom = findByNameExact(ResourceAccessor.str(StringResKeys.STR_MAIN_CHATROOM));
        return defaultRoom != null ? defaultRoom : findByNameExact(ResourceAccessor.str(StringResKeys.STR_DEFAULT_CHATROOM));
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
        if (this.nickname == null || roomCount < MIN_ROOMS_FOR_SAVE) {
            return;
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
        buffer.writeShortBE(SERIALIZATION_MARKER);
    }

    public void deserialize(ByteBuffer extraBuffer) {
        this.nickname = extraBuffer.readUTF8Str((String) null);
        extraBuffer.readWideStr();
        this.list = ObjectPool.newVector();
        int chatRoomCount = extraBuffer.readInt();
        for (int i = 0; i < chatRoomCount; i++) {
            this.list.addElement(ChatRoom.deserialize(extraBuffer));
        }
        if (extraBuffer.readShortBE() != SERIALIZATION_MARKER) {
            throw new RuntimeException();
        }
        assignDefault(false);
    }

    public static final void showChatRoomSelector() {
        Screen screen = Screens.dialogScreen();
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Enumeration chatRooms = account.chatRoomManager.list.elements();
        while (chatRooms.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) chatRooms.nextElement();
            if (chatRoom != account.chatRoomManager.getLast()) {
                MenuItem menuItem = MenuItem.createDefault().setIcon(234).setLabel(chatRoom.name);
                menuItem.data = chatRoom;
                screen.addItem(menuItem);
            }
        }
        ScreenManager.showScreen(screen);
    }

    public static final void sendChatRoomRequest(Object[] objArr) {
        AccountManager.clearAccountHighlight((MrimAccount) AppState.getAccount());
        RegistrationState.setRegistrationData(ApiClient.submitAsync(objArr));
    }
}
