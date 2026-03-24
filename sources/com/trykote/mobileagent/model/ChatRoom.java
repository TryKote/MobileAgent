package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: w */
/* loaded from: MobileAgent_3.9.jar:w.class */
public final class ChatRoom {

    /* renamed from: a */
    public int id;

    /* renamed from: b */
    public String name;

    /* renamed from: c */
    public int memberCount;

    /* renamed from: d */
    public int unreadCount;

    /* renamed from: e */
    public String subject;

    /* renamed from: f */
    public final Vector messageIds;

    /* renamed from: g */
    public final Vector readMessages;

    /* renamed from: h */
    public final Hashtable messages;

    /* renamed from: i */
    public Hashtable metadata;

    /* renamed from: j */
    public Vector participants;

    /* renamed from: k */
    public boolean isInitialized;

    /* renamed from: l */
    public boolean isActive;

    public ChatRoom() {
        this.messageIds = ObjectPool.newVector();
        this.readMessages = ObjectPool.newVector();
        this.messages = new Hashtable();
        this.isActive = true;
    }

    public ChatRoom(Object obj) {
        this();
        parseJson(obj);
        this.isActive = true;
    }

    /* renamed from: a */
    public final void serialize(ByteBuffer buffer) {
        buffer.writeStringUTF16(this.name).writeIntLE(this.memberCount).writeIntLE(this.id).writeIntLE(this.unreadCount).writeStringLatin1(this.subject);
        if (this.messageIds.size() > 20) {
            this.messageIds.setSize(20);
        }
        int size = this.messageIds.size();
        buffer.writeIntLE(size);
        for (int i = 0; i < size; i++) {
            String msgId = Utils.getVectorString(this.messageIds, i);
            buffer.writeStringLatin1(msgId);
            Message msg = getMessage(msgId);
            buffer.writeLong(msg.timestamp);
            XmppMailRuProtocol.writeAddressPairs(msg.toList, buffer);
            XmppMailRuProtocol.writeAddressPairs(msg.ccList, buffer);
            buffer.writeIntLE(msg.priority).writeIntLE(msg.flags).writeStringUTF16(Utils.defaultStr(msg.subject));
            if (msg.body == null || msg.body.length() > 3072) {
                buffer.writeIntLE(0).writeIntLE(0);
            } else {
                buffer.writeIntLE(1).writeStringUTF16(msg.body).writeIntLE(1);
                Object[] objArr = msg.attachments;
                if (objArr == null) {
                    buffer.writeIntLE(0);
                } else {
                    buffer.writeIntLE(objArr.length);
                    for (Object obj : objArr) {
                        String[] strArr = (String[]) obj;
                        for (int i2 = 0; i2 < 6; i2++) {
                            buffer.writeStringUTF16(strArr[i2]);
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

    /* renamed from: b */
    public static final ChatRoom deserialize(ByteBuffer buffer) {
        ChatRoom room = new ChatRoom();
        room.name = buffer.readUTF8Str((String) null);
        room.memberCount = buffer.readInt();
        room.id = buffer.readInt();
        room.unreadCount = buffer.readInt();
        room.subject = buffer.readWideStr();
        int count = buffer.readInt();
        for (int i = 0; i < count; i++) {
            Vector vector = room.messageIds;
            String msgKey = buffer.readWideStr();
            vector.addElement(msgKey);
            room.messages.put(msgKey, new Message(buffer, msgKey));
        }
        return room;
    }

    /* renamed from: a */
    public final void parseJson(Object obj) {
        this.name = JsonParser.getStringByInt(obj, 263472);
        this.memberCount = JsonParser.getIntByInt(obj, 526252);
        this.id = JsonParser.getIntByInt(obj, 132297);
        this.unreadCount = JsonParser.getIntByInt(obj, 395188);
        this.subject = AppState.emptyStr;
        this.isInitialized = true;
    }

    public ChatRoom(int i) {
        this.id = i;
        this.messageIds = ObjectPool.newVector();
        this.readMessages = ObjectPool.newVector();
        this.messages = new Hashtable();
        this.metadata = new Hashtable();
        this.participants = ObjectPool.newVector();
    }

    /* renamed from: g */
    private String getFormattedName() {
        int i = 5;
        do {
            i--;
            if (i < 0) {
                return this.name;
            }
        } while (!this.name.equals(AppState.getString(i + 891)));
        return AppState.getString(i + 896);
    }

    /* renamed from: a */
    public final boolean isMessageRead(String str) {
        return this.readMessages.contains(str);
    }

    /* renamed from: b */
    public final Message getMessage(String str) {
        if (str != null) {
            return (Message) this.messages.get(str);
        }
        return null;
    }

    /* renamed from: c */
    public final boolean hasMessage(String str) {
        return this.messageIds.contains(str);
    }

    /* renamed from: d */
    public final void markMessageRead(String str) {
        this.readMessages.removeElement(str);
    }

    /* renamed from: a */
    public final int getType() {
        String formatted = getFormattedName();
        if (StringUtils.matchesKey(896, formatted) || StringUtils.matchesKey(900, formatted)) {
            return 1;
        }
        return (StringUtils.matchesKey(898, formatted) || StringUtils.matchesKey(899, formatted)) ? 2 : 3;
    }

    /* renamed from: b */
    public final void decrementUnread() {
        this.unreadCount--;
    }

    /* renamed from: c */
    public final void incrementUnread() {
        this.unreadCount++;
    }

    /* renamed from: d */
    public final void decrementMembers() {
        this.memberCount--;
    }

    /* renamed from: e */
    public final void clear() {
        this.subject = null;
        this.messageIds.removeAllElements();
        this.readMessages.removeAllElements();
        this.participants.removeAllElements();
        this.messages.clear();
        this.metadata.clear();
    }

    /* renamed from: e */
    public final int getPriority(String str) {
        return Integer.parseInt((String) this.metadata.get(str));
    }

    /* renamed from: a */
    public final void setActive(boolean z) {
        this.isActive = z;
        this.isInitialized = true;
    }

    /* renamed from: f */
    public final String getDisplayName() {
        if (this == ((MrimAccount) AppState.getAccount()).getLastChatRoom()) {
            return this.name;
        }
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(getFormattedName()).append(' ').append('[').append(this.unreadCount).append('/').append(this.memberCount).append(']'));
    }
}
