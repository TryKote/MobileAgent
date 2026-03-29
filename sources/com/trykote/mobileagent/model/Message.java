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
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: az */
/* loaded from: MobileAgent_3.9.jar:az.class */
public final class Message {

    /* renamed from: a */
    public String from;

    /* renamed from: b */
    public long timestamp;

    /* renamed from: c */
    public Vector toList;

    /* renamed from: d */
    public Vector ccList;

    /* renamed from: e */
    public int priority;

    /* renamed from: f */
    public int flags;

    /* renamed from: g */
    public String subject;

    /* renamed from: h */
    public String body;

    /* renamed from: i */
    public Object[] attachments;

    public Message(Hashtable hashtable) {
        this.from = JsonParser.getStringValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_MESSAGE_ID));
        this.timestamp = JsonParser.getIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_DATE)) * 1000;
        this.toList = MailHelper.parseAddressHeader(JsonParser.getStringValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_FROM_EMAIL)), JsonParser.getStringValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_FROM_DISPLAY)));
        this.ccList = MailHelper.parseAddressHeader(JsonParser.getStringValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_TO_EMAIL)), JsonParser.getStringValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_TO_NAME)));
        this.priority = JsonParser.getIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_CLEAR_SIZE));
        setFlag(4, JsonParser.getIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FLAG_UNREAD)) != 0);
        setFlag(1, JsonParser.getIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FLAG_ATTACH)) != 0);
        this.subject = Conversation.decodeHtmlSpecial(JsonParser.getStringValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_SUBJECT)));
    }

    public Message(Vector vector, String str, String str2) {
        MrimAccount account = (MrimAccount) AppState.getAccount();
        this.toList = MailHelper.addUniqueAddress(ObjectPool.newVector(), MailHelper.createAddressPair(account.login, Utils.defaultStr(account.chatRoomManager.nickname)));
        this.ccList = vector;
        this.subject = str;
        this.body = str2;
    }

    public Message(ByteBuffer buffer, String str) {
        this.from = str;
        this.timestamp = buffer.readLong();
        this.toList = MailHelper.readAddressPairs(buffer);
        this.ccList = MailHelper.readAddressPairs(buffer);
        this.priority = buffer.readInt();
        this.flags = buffer.readInt();
        this.subject = buffer.readUTF8Str((String) null);
        if (buffer.readInt() != 0) {
            this.body = buffer.readUTF8Str((String) null);
        }
        if (buffer.readInt() != 0) {
            this.attachments = readAttachmentArray(buffer);
        }
    }

    /* renamed from: a */
    public static final Object[] readAttachmentArray(ByteBuffer buffer) {
        try {
            int count = buffer.readInt();
            if (count == 0) {
                return null;
            }
            Object[] objArr = new Object[count];
            for (int i = 0; i < count; i++) {
                String[] strArr = new String[6];
                for (int i2 = 0; i2 < 6; i2++) {
                    strArr[i2] = buffer.readUTF8Str((String) null);
                }
                objArr[i] = strArr;
            }
            return objArr;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: a */
    public final boolean isRead() {
        return !hasFlag(4);
    }

    /* renamed from: a */
    public final MenuItem createMenuItem(ChatRoom chatRoom) {
        boolean z;
        int roomType;
        String[] ccRecipient;
        String[] toRecipient;
        boolean isUnread = hasFlag(4);
        int i = isUnread ? 1 : 0;
        int i2 = i;
        int ellipsisWidth = AppState.getGfxContext(i).stringWidth(AppState.getEllipsis());
        int availWidth = (((AppState.getInt(UIKeys.INT_SCREEN_WIDTH) - ellipsisWidth) - 240) + 227) - 10;
        int i3 = isUnread ? 0 : 19;
        MrimAccount account = (MrimAccount) AppState.getAccount();
        MenuItem item = MenuItem.create(this.from);
        item.data = this;
        String str = this.from;
        Enumeration elements = account.chatRoomManager.list.elements();
        while (true) {
            if (!elements.hasMoreElements()) {
                z = false;
                break;
            }
            if (((ChatRoom) elements.nextElement()).isMessageRead(str)) {
                z = true;
                break;
            }
        }
        boolean z2 = z;
        MenuItem iconItem = item.setIcon(z ? 25 : -1);
        Calendar cal = AppState.getCalendar();
        int i4 = cal.get(1);
        int i5 = cal.get(2);
        int i6 = cal.get(5);
        cal.setTime(new Date(this.timestamp));
        StringBuffer sb = ObjectPool.newStringBuffer();
        String dateStr = Utils.appendSpace(ObjectPool.toStringAndRelease((i4 == cal.get(1) && i5 == cal.get(2) && i6 == cal.get(5)) ? sb.append(Conversation.formatNumber(cal.get(11), 2)).append(':').append(Conversation.formatNumber(cal.get(12), 2)) : sb.append(Conversation.formatNumber(cal.get(5), 2)).append('.').append(Conversation.formatNumber(cal.get(2) + 1, 2)).append('.').append(Conversation.formatNumber(cal.get(1) - 2000, 2))));
        MenuItem textItem = iconItem.addText(dateStr, i2, 10);
        String priorityStr = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append('[').append(this.priority).append(AppState.getString(StringResKeys.STR_PRIORITY_SUFFIX)));
        MenuItem mainItem = textItem.addText(priorityStr, i2, i3);
        int textWidth = AppState.getGfxContext(i2).stringWidth(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(dateStr).append(priorityStr)));
        if (hasFlag(1)) {
            mainItem.setIcon(221);
            textWidth += 20;
        }
        if (z2) {
            textWidth += 20;
        }
        if (chatRoom == account.chatRoomManager.getLast()) {
            roomType = account.chatRoomManager.findById(chatRoom.getPriority(this.from)).getType();
        } else {
            roomType = (chatRoom == account.chatRoomManager.getLast() || !chatRoom.hasMessage(this.from)) ? 3 : chatRoom.getType();
        }
        int i7 = roomType;
        boolean z3 = false;
        if ((i7 & 1) != 0 && (toRecipient = MailHelper.getFirstRecipient(getToList())) != null) {
            mainItem.addText(truncateText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_MSG_FORWARDED)).append(' ').append(toRecipient[1])), i2, availWidth - textWidth, ellipsisWidth, true), i2, i3);
            z3 = true;
        }
        if ((i7 & 2) != 0 && (ccRecipient = MailHelper.getFirstRecipient(getCcList())) != null) {
            mainItem.addText(truncateText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_MSG_REPLIED)).append(' ').append(ccRecipient[1])), i2, availWidth - (z3 ? 0 : textWidth), ellipsisWidth, true), i2, i3);
        }
        boolean z4 = chatRoom == account.chatRoomManager.getLast();
        mainItem.setLabelInternal(isUnread ? 225 : 237, truncateText(getSubject(), i2, availWidth - 22, ellipsisWidth, z4), i2, i3);
        if (z4) {
            mainItem.setIcon(234);
            mainItem.addText(truncateText(account.chatRoomManager.findById(chatRoom.getPriority(this.from)).name, i2, availWidth - 22, ellipsisWidth, false), i2, i3);
        }
        return mainItem;
    }

    /* renamed from: b */
    public final Vector getToList() {
        return this.toList == null ? ObjectPool.newVector() : this.toList;
    }

    /* renamed from: c */
    public final Vector getCcList() {
        return this.ccList == null ? ObjectPool.newVector() : this.ccList;
    }

    /* renamed from: a */
    public final void setFlag(int i, boolean z) {
        if (z) {
            this.flags |= i;
        } else {
            this.flags &= i ^ (-1);
        }
    }

    /* renamed from: a */
    public final boolean hasFlag(int i) {
        return (this.flags & i) != 0;
    }

    /* JADX DEBUG: Move duplicate insns, count: 4 to block B:12:0x0049 */
    /* renamed from: a */
    private static String truncateText(String str, int i, int i2, int i3, boolean z) {
        int i4;
        int substringW;
        GraphicsContext gfx = AppState.getGfxContext(i);
        if (gfx.stringWidth(str) > i2 + i3) {
            int i5 = 4;
            int i6 = 4;
            int length = str.length();
            int i7 = length;
            while (true) {
                i4 = (i5 + length) >> 1;
                if (i4 == i6 || (substringW = gfx.substringWidth(str, 0, i4)) == i2) {
                    break;
                }
                if (substringW > i2) {
                    i7 = i4;
                } else {
                    i6 = i4;
                }
                i5 = i6;
                length = i7;
            }
            str = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.prefix(str, i4 + 1)).append((char) 8230));
        }
        return z ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(str).append('\n')) : str;
    }

    /* renamed from: d */
    public final String getSubject() {
        return (this.subject == null || this.subject.length() == 0) ? AppState.getString(StringResKeys.STR_NO_SUBJECT) : this.subject;
    }

    /* renamed from: e */
    public final Object toHashtable() {
        Hashtable hashtable = new Hashtable();
        String[] ccRecipient = MailHelper.getFirstRecipient(this.toList);
        if (ccRecipient != null) {
            hashtable.put(AppState.getString(PackedStringKeys.MAIL_FIELD_FROM), ccRecipient[1]);
        }
        String ccKey = AppState.getString(PackedStringKeys.MAIL_FIELD_TO);
        Vector vector = this.ccList;
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (vector != null) {
            String str = AppState.emptyStr;
            ObjectPool.unpackChars(60);
            ObjectPool.unpackChars(62);
            String separator = ObjectPool.unpackChars(44);
            Enumeration elements = vector.elements();
            while (elements.hasMoreElements()) {
                sb.append(sb.length() > 0 ? separator : str).append(str).append(((String[]) elements.nextElement())[0]).append(str);
            }
        }
        hashtable.put(ccKey, ObjectPool.toStringAndRelease(sb));
        hashtable.put(AppState.getString(PackedStringKeys.MAIL_FIELD_SUBJECT), this.subject);
        hashtable.put(AppState.getString(PackedStringKeys.MAIL_FIELD_BODY), this.body);
        JsonParser.putIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_ACTION_COPY), 1);
        JsonParser.putIntValue(hashtable, AppState.getString(PackedStringKeys.ACTION_SEND), 1);
        JsonParser.putIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FOLDER_DRAFT), 0);
        JsonParser.putIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_RECEIPT), 0);
        JsonParser.putIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FIELD_RECEIPT_ARRIVED), 0);
        Vector attachList = ObjectPool.newVector();
        int length = this.attachments == null ? 0 : this.attachments.length;
        for (int i = 0; i < length; i++) {
            String[] strArr = (String[]) this.attachments[i];
            Hashtable hashtable2 = new Hashtable();
            for (int i2 = 1227; i2 <= 1232; i2++) {
                hashtable2.put(AppState.getString(i2), strArr[i2 - 1227]);
            }
            attachList.addElement(hashtable2);
        }
        hashtable.put(AppState.getString(PackedStringKeys.MAIL_FIELD_ATTACHMENTS), attachList);
        if (attachList.size() > 0) {
            JsonParser.putIntValue(hashtable, AppState.getString(PackedStringKeys.MAIL_FLAG_ATTACH), 1);
        }
        return hashtable;
    }
}
