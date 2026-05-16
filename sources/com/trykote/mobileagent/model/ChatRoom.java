package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.JsonParser;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Hashtable;
import java.util.Vector;

public final class ChatRoom {

    // Maximum number of messages to serialize
    public static final int MAX_SERIALIZED_MESSAGES = 20;

    // Attachment field count per attachment
    public static final int ATTACHMENT_FIELD_COUNT = 6;

    // Number of localized mailbox names
    public static final int MAILBOX_NAME_COUNT = 5;

    // Mailbox type constants
    public static final int TYPE_SENT = 1;
    public static final int TYPE_DRAFT = 2;
    public static final int TYPE_OTHER = 3;

    // Maximum body length for serialization
    private static final int MAX_BODY_LENGTH = 3072;

    public int id;

    public String name;

    public int memberCount;

    public int unreadCount;

    public String subject;

    public final Vector messageIds;

    public final Vector readMessages;

    public final Hashtable messages;

    public Hashtable metadata;

    public Vector participants;

    public boolean isInitialized;

    public boolean isActive;

    public ChatRoom() {
        this.messageIds = ObjectPool.newVector();
        this.readMessages = ObjectPool.newVector();
        this.messages = new Hashtable();
        this.isActive = true;
    }

    public ChatRoom(Object jsonData) {
        this();
        parseJson(jsonData);
        this.isActive = true;
    }

    public final void serialize(ByteBuffer buffer) {
        buffer.writeStringUTF16(this.name).writeIntLE(this.memberCount).writeIntLE(this.id).writeIntLE(this.unreadCount).writeStringLatin1(this.subject);
        if (this.messageIds.size() > MAX_SERIALIZED_MESSAGES) {
            this.messageIds.setSize(MAX_SERIALIZED_MESSAGES);
        }
        int size = this.messageIds.size();
        buffer.writeIntLE(size);
        for (int i = 0; i < size; i++) {
            String msgId = Utils.getVectorString(this.messageIds, i);
            buffer.writeStringLatin1(msgId);
            Message msg = getMessage(msgId);
            buffer.writeLong(msg.timestamp);
            MailHelper.writeAddressPairs(msg.toList, buffer);
            MailHelper.writeAddressPairs(msg.ccList, buffer);
            buffer.writeIntLE(msg.priority).writeIntLE(msg.flags).writeStringUTF16(Utils.defaultStr(msg.subject));
            if (msg.body == null || msg.body.length() > MAX_BODY_LENGTH) {
                buffer.writeIntLE(0).writeIntLE(0);
            } else {
                buffer.writeIntLE(1).writeStringUTF16(msg.body).writeIntLE(1);
                Object[] attachments = msg.attachments;
                if (attachments == null) {
                    buffer.writeIntLE(0);
                } else {
                    buffer.writeIntLE(attachments.length);
                    for (int ai = 0; ai < attachments.length; ai++) {
                        String[] attachFields = (String[]) attachments[ai];
                        for (int fi = 0; fi < ATTACHMENT_FIELD_COUNT; fi++) {
                            buffer.writeStringUTF16(attachFields[fi]);
                        }
                    }
                }
            }
            msg.toList = null;
            msg.ccList = null;
            msg.subject = null;
            msg.body = null;
            msg.attachments = null;
        }
    }

    public static final ChatRoom deserialize(ByteBuffer buffer) {
        ChatRoom room = new ChatRoom();
        room.name = buffer.readUTF8Str((String) null);
        room.memberCount = buffer.readInt();
        room.id = buffer.readInt();
        room.unreadCount = buffer.readInt();
        room.subject = buffer.readWideStr();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            String msgKey = buffer.readWideStr();
            room.messageIds.addElement(msgKey);
            room.messages.put(msgKey, new Message(buffer, msgKey));
        }
        return room;
    }

    public final void parseJson(Object jsonData) {
        this.name = JsonParser.getStringByInt(jsonData, 263472);
        this.memberCount = JsonParser.getIntByInt(jsonData, 526252);
        this.id = JsonParser.getIntByInt(jsonData, 132297);
        this.unreadCount = JsonParser.getIntByInt(jsonData, 395188);
        this.subject = AppState.emptyStr;
        this.isInitialized = true;
    }

    public ChatRoom(int roomId) {
        this.id = roomId;
        this.messageIds = ObjectPool.newVector();
        this.readMessages = ObjectPool.newVector();
        this.messages = new Hashtable();
        this.metadata = new Hashtable();
        this.participants = ObjectPool.newVector();
    }

    private String getFormattedName() {
        for (int i = MAILBOX_NAME_COUNT - 1; i >= 0; i--) {
            if (this.name.equals(ResourceAccessor.blockStr(StringResKeys.MAILBOX_NAMES_EN_BASE, i))) {
                return ResourceAccessor.blockStr(StringResKeys.MAILBOX_NAMES_RU_BASE, i);
            }
        }
        return this.name;
    }

    public final boolean isMessageRead(String msgId) {
        return this.readMessages.contains(msgId);
    }

    public final Message getMessage(String msgId) {
        if (msgId != null) {
            return (Message) this.messages.get(msgId);
        }
        return null;
    }

    public final boolean hasMessage(String msgId) {
        return this.messageIds.contains(msgId);
    }

    public final void markMessageRead(String msgId) {
        this.readMessages.removeElement(msgId);
    }

    public final int getType() {
        String formatted = getFormattedName();
        if (StringUtils.matchesKey(StringResKeys.MAILBOX_NAMES_RU_BASE, formatted) || StringUtils.matchesKey(StringResKeys.MAILBOX_NAMES_RU_BASE + 4, formatted)) {
            return TYPE_SENT;
        }
        return (StringUtils.matchesKey(StringResKeys.MAILBOX_NAMES_RU_BASE + 2, formatted) || StringUtils.matchesKey(StringResKeys.MAILBOX_NAMES_RU_BASE + 3, formatted)) ? TYPE_DRAFT : TYPE_OTHER;
    }

    public final void decrementUnread() {
        this.unreadCount--;
    }

    public final void incrementUnread() {
        this.unreadCount++;
    }

    public final void decrementMembers() {
        this.memberCount--;
    }

    public final void clear() {
        this.subject = null;
        this.messageIds.removeAllElements();
        this.readMessages.removeAllElements();
        this.participants.removeAllElements();
        this.messages.clear();
        this.metadata.clear();
    }

    public final int getPriority(String msgId) {
        return Integer.parseInt((String) this.metadata.get(msgId));
    }

    public final void setActive(boolean active) {
        this.isActive = active;
        this.isInitialized = true;
    }

    public final String getDisplayName() {
        if (AppState.getAccount().isLastChatRoom(this)) {
            return this.name;
        }
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(getFormattedName()).append(' ').append('[').append(this.unreadCount).append('/').append(this.memberCount).append(']'));
    }
}
