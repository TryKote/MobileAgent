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

public final class Message {

    // Message flag bits
    public static final int FLAG_HAS_ATTACHMENT = 1;
    public static final int FLAG_UNREAD = 4;

    // Attachment field count per attachment entry
    public static final int ATTACHMENT_FIELD_COUNT = 6;

    // Attachment field key indices (state pool string keys)
    public static final int ATTACHMENT_KEY_FIRST = 1227;
    public static final int ATTACHMENT_KEY_LAST = 1232;

    // Layout dimensions for message list items
    private static final int LAYOUT_BASE_WIDTH = 240;
    private static final int LAYOUT_DATE_WIDTH = 227;
    private static final int LAYOUT_PADDING = 10;
    private static final int ICON_WIDTH = 20;
    private static final int SUBJECT_PADDING = 22;

    // Text style for read messages
    private static final int TEXT_STYLE_READ = 19;

    public String from;

    public long timestamp;

    public Vector toList;

    public Vector ccList;

    public int priority;

    public int flags;

    public String subject;

    public String body;

    public Object[] attachments;

    public Message(Hashtable fields) {
        this.from = JsonParser.getStringValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_MESSAGE_ID));
        this.timestamp = JsonParser.getIntValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_DATE)) * 1000;
        this.toList = MailHelper.parseAddressHeader(JsonParser.getStringValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_FROM_EMAIL)), JsonParser.getStringValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_FROM_DISPLAY)));
        this.ccList = MailHelper.parseAddressHeader(JsonParser.getStringValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_TO_EMAIL)), JsonParser.getStringValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_TO_NAME)));
        this.priority = JsonParser.getIntValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_CLEAR_SIZE));
        setFlag(FLAG_UNREAD, JsonParser.getIntValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FLAG_UNREAD)) != 0);
        setFlag(FLAG_HAS_ATTACHMENT, JsonParser.getIntValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FLAG_ATTACH)) != 0);
        this.subject = Conversation.decodeHtmlSpecial(JsonParser.getStringValue(fields, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_SUBJECT)));
    }

    public Message(Vector recipients, String subject, String body) {
        MrimAccount account = (MrimAccount) Storage.state().getAccount();
        this.toList = MailHelper.addUniqueAddress(ObjectPool.newVector(), MailHelper.createAddressPair(account.login, Utils.defaultStr(account.chatRoomManager.nickname)));
        this.ccList = recipients;
        this.subject = subject;
        this.body = body;
    }

    public Message(ByteBuffer buffer, String messageId) {
        this.from = messageId;
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

    public static final Object[] readAttachmentArray(ByteBuffer buffer) {
        try {
            int count = buffer.readInt();
            if (count == 0) {
                return null;
            }
            Object[] attachments = new Object[count];
            for (int i = 0; i < count; i++) {
                String[] fields = new String[ATTACHMENT_FIELD_COUNT];
                for (int j = 0; j < ATTACHMENT_FIELD_COUNT; j++) {
                    fields[j] = buffer.readUTF8Str((String) null);
                }
                attachments[i] = fields;
            }
            return attachments;
        } catch (Throwable unused) {
            return null;
        }
    }

    public final boolean isRead() {
        return !hasFlag(FLAG_UNREAD);
    }

    public final MenuItem createMenuItem(ChatRoom chatRoom) {
        boolean isMarkedRead;
        int roomType;
        String[] ccRecipient;
        String[] toRecipient;
        boolean isUnread = hasFlag(FLAG_UNREAD);
        int fontStyle = isUnread ? 1 : 0;
        int ellipsisWidth = Storage.state().getGfxContext(fontStyle).stringWidth(Storage.state().getEllipsis());
        int availWidth = (((Storage.state().getInt(UIKeys.INT_SCREEN_WIDTH) - ellipsisWidth) - LAYOUT_BASE_WIDTH) + LAYOUT_DATE_WIDTH) - LAYOUT_PADDING;
        int textStyle = isUnread ? 0 : TEXT_STYLE_READ;
        MrimAccount account = (MrimAccount) Storage.state().getAccount();
        MenuItem item = MenuItem.create(this.from);
        item.data = this;
        String msgId = this.from;
        Enumeration elements = account.chatRoomManager.list.elements();
        while (true) {
            if (!elements.hasMoreElements()) {
                isMarkedRead = false;
                break;
            }
            if (((ChatRoom) elements.nextElement()).isMessageRead(msgId)) {
                isMarkedRead = true;
                break;
            }
        }
        boolean hasReadMark = isMarkedRead;
        MenuItem iconItem = item.setIcon(isMarkedRead ? 25 : -1);
        Calendar cal = Storage.state().getCalendar();
        int todayYear = cal.get(1);
        int todayMonth = cal.get(2);
        int todayDay = cal.get(5);
        cal.setTime(new Date(this.timestamp));
        StringBuffer sb = ObjectPool.newStringBuffer();
        String dateStr = Utils.appendSpace(ObjectPool.toStringAndRelease((todayYear == cal.get(1) && todayMonth == cal.get(2) && todayDay == cal.get(5)) ? sb.append(Conversation.formatNumber(cal.get(11), 2)).append(':').append(Conversation.formatNumber(cal.get(12), 2)) : sb.append(Conversation.formatNumber(cal.get(5), 2)).append('.').append(Conversation.formatNumber(cal.get(2) + 1, 2)).append('.').append(Conversation.formatNumber(cal.get(1) - 2000, 2))));
        MenuItem textItem = iconItem.addText(dateStr, fontStyle, 10);
        String priorityStr = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append('[').append(this.priority).append(Storage.resources().getString(StringResKeys.STR_PRIORITY_SUFFIX)));
        MenuItem mainItem = textItem.addText(priorityStr, fontStyle, textStyle);
        int textWidth = Storage.state().getGfxContext(fontStyle).stringWidth(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(dateStr).append(priorityStr)));
        if (hasFlag(FLAG_HAS_ATTACHMENT)) {
            mainItem.setIcon(221);
            textWidth += ICON_WIDTH;
        }
        if (hasReadMark) {
            textWidth += ICON_WIDTH;
        }
        if (chatRoom == account.chatRoomManager.getLast()) {
            roomType = account.chatRoomManager.findById(chatRoom.getPriority(this.from)).getType();
        } else {
            roomType = (chatRoom == account.chatRoomManager.getLast() || !chatRoom.hasMessage(this.from)) ? ChatRoom.TYPE_OTHER : chatRoom.getType();
        }
        boolean hasForwardInfo = false;
        if ((roomType & 1) != 0 && (toRecipient = MailHelper.getFirstRecipient(getToList())) != null) {
            mainItem.addText(truncateText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_MSG_FORWARDED)).append(' ').append(toRecipient[1])), fontStyle, availWidth - textWidth, ellipsisWidth, true), fontStyle, textStyle);
            hasForwardInfo = true;
        }
        if ((roomType & 2) != 0 && (ccRecipient = MailHelper.getFirstRecipient(getCcList())) != null) {
            mainItem.addText(truncateText(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_MSG_REPLIED)).append(' ').append(ccRecipient[1])), fontStyle, availWidth - (hasForwardInfo ? 0 : textWidth), ellipsisWidth, true), fontStyle, textStyle);
        }
        boolean isLastRoom = chatRoom == account.chatRoomManager.getLast();
        mainItem.setLabelInternal(isUnread ? 225 : 237, truncateText(getSubject(), fontStyle, availWidth - SUBJECT_PADDING, ellipsisWidth, isLastRoom), fontStyle, textStyle);
        if (isLastRoom) {
            mainItem.setIcon(234);
            mainItem.addText(truncateText(account.chatRoomManager.findById(chatRoom.getPriority(this.from)).name, fontStyle, availWidth - SUBJECT_PADDING, ellipsisWidth, false), fontStyle, textStyle);
        }
        return mainItem;
    }

    public final Vector getToList() {
        return this.toList == null ? ObjectPool.newVector() : this.toList;
    }

    public final Vector getCcList() {
        return this.ccList == null ? ObjectPool.newVector() : this.ccList;
    }

    public final void setFlag(int flagBit, boolean enabled) {
        if (enabled) {
            this.flags |= flagBit;
        } else {
            this.flags &= flagBit ^ (-1);
        }
    }

    public final boolean hasFlag(int flagBit) {
        return (this.flags & flagBit) != 0;
    }

    private static String truncateText(String text, int fontStyle, int maxWidth, int ellipsisWidth, boolean appendNewline) {
        int mid;
        int substringW;
        GraphicsContext gfx = Storage.state().getGfxContext(fontStyle);
        if (gfx.stringWidth(text) > maxWidth + ellipsisWidth) {
            int lo = 4;
            int lastLo = 4;
            int hi = text.length();
            int lastHi = hi;
            while (true) {
                mid = (lo + hi) >> 1;
                if (mid == lastLo || (substringW = gfx.substringWidth(text, 0, mid)) == maxWidth) {
                    break;
                }
                if (substringW > maxWidth) {
                    lastHi = mid;
                } else {
                    lastLo = mid;
                }
                lo = lastLo;
                hi = lastHi;
            }
            text = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.prefix(text, mid + 1)).append((char) 8230));
        }
        return appendNewline ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(text).append('\n')) : text;
    }

    public final String getSubject() {
        return (this.subject == null || this.subject.length() == 0) ? Storage.resources().getString(StringResKeys.STR_NO_SUBJECT) : this.subject;
    }

    public final Object toHashtable() {
        Hashtable result = new Hashtable();
        String[] fromRecipient = MailHelper.getFirstRecipient(this.toList);
        if (fromRecipient != null) {
            result.put(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_FROM), fromRecipient[1]);
        }
        String toKey = Storage.resources().getString(PackedStringKeys.MAIL_FIELD_TO);
        Vector ccAddresses = this.ccList;
        StringBuffer sb = ObjectPool.newStringBuffer();
        if (ccAddresses != null) {
            String empty = Storage.emptyStr;
            ObjectPool.unpackChars(60);
            ObjectPool.unpackChars(62);
            String separator = ObjectPool.unpackChars(44);
            Enumeration elements = ccAddresses.elements();
            while (elements.hasMoreElements()) {
                sb.append(sb.length() > 0 ? separator : empty).append(empty).append(((String[]) elements.nextElement())[0]).append(empty);
            }
        }
        result.put(toKey, ObjectPool.toStringAndRelease(sb));
        result.put(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_SUBJECT), this.subject);
        result.put(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_BODY), this.body);
        JsonParser.putIntValue(result, Storage.resources().getString(PackedStringKeys.MAIL_ACTION_COPY), 1);
        JsonParser.putIntValue(result, Storage.resources().getString(PackedStringKeys.ACTION_SEND), 1);
        JsonParser.putIntValue(result, Storage.resources().getString(PackedStringKeys.MAIL_FOLDER_DRAFT), 0);
        JsonParser.putIntValue(result, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_RECEIPT), 0);
        JsonParser.putIntValue(result, Storage.resources().getString(PackedStringKeys.MAIL_FIELD_RECEIPT_ARRIVED), 0);
        Vector attachList = ObjectPool.newVector();
        int count = this.attachments == null ? 0 : this.attachments.length;
        for (int i = 0; i < count; i++) {
            String[] attachFields = (String[]) this.attachments[i];
            Hashtable attachEntry = new Hashtable();
            for (int j = ATTACHMENT_KEY_FIRST; j <= ATTACHMENT_KEY_LAST; j++) {
                attachEntry.put(Storage.state().getString(j), attachFields[j - ATTACHMENT_KEY_FIRST]);
            }
            attachList.addElement(attachEntry);
        }
        result.put(Storage.resources().getString(PackedStringKeys.MAIL_FIELD_ATTACHMENTS), attachList);
        if (attachList.size() > 0) {
            JsonParser.putIntValue(result, Storage.resources().getString(PackedStringKeys.MAIL_FLAG_ATTACH), 1);
        }
        return result;
    }
}
