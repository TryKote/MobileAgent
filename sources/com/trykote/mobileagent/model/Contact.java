package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
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
import java.util.Vector;

public abstract class Contact implements Sortable {

    // Render state bit flags (used for contact list sort priority)
    private static final int RENDER_HAS_NOTIFICATION = 0x40000000;
    private static final int RENDER_IS_HIGHLIGHTED = 0x20000000;
    private static final int RENDER_HAS_MESSAGES = 0x10000000;
    private static final int RENDER_NOT_FULLY_ONLINE = 0x08000000;
    private static final int RENDER_ALL_READ = 0x04000000;
    private static final int RENDER_AVAILABLE = 0x02000000;

    // Masks to clear incompatible render bits
    private static final int RENDER_OFFLINE_CLEAR_MASK =
            ~(RENDER_HAS_NOTIFICATION | RENDER_IS_HIGHLIGHTED | RENDER_HAS_MESSAGES | RENDER_AVAILABLE);
    private static final int RENDER_ONLINE_CLEAR_MASK =
            ~(RENDER_ALL_READ | RENDER_AVAILABLE);

    // Icon IDs
    static final int ICON_BLINK_UNREAD = 16384;
    private static final int ICON_BLINK_NOTIFICATION = 16386;
    static final int ICON_TYPING = 26;

    // Message flag types (used in addFlag / receiveMessageFull)
    private static final int MSG_FLAG_INCOMING = 4;
    private static final int MSG_FLAG_SPECIAL = 64;

    // Stored message types (written to message buffer)
    private static final int MSG_TYPE_OUTGOING = 1;
    private static final int MSG_TYPE_INCOMING = 8;
    private static final int MSG_TYPE_FORWARDED = 16;

    // Message display color styles
    private static final int COLOR_OUTGOING = 11;
    private static final int COLOR_INCOMING = 12;

    // Message header size in bytes (type + timestamp + sentTime + length prefix)
    private static final int MSG_HEADER_SIZE = 17;

    // Maximum preview length for message summary truncation
    private static final int SUMMARY_TRUNCATION_LENGTH = 50;

    public final Account account;

    private ByteBuffer messageBuffer;

    public boolean highlighted;

    public int statusCode;

    public int defaultIcon;

    public byte flags;

    public boolean dirty;

    public String displayName;

    public String sortKey;

    private int renderState;

    private long lastMessageTime;

    public String identifier;

    public String extra;

    public Contact(Account acct) {
        this.account = acct;
    }

    public abstract void deserialize(ByteBuffer buffer);

    public int getIcon() {
        if (this.flags != 0) {
            return (this.flags & 1) != 0 ? ICON_BLINK_UNREAD : ICON_BLINK_NOTIFICATION;
        }
        if (this.statusCode != 0) {
            return ICON_TYPING;
        }
        return this.defaultIcon;
    }

    public final void addFlag(int flagBit) {
        this.flags = (byte) (this.flags | flagBit);
        ContactListManager.markContactRead(this);
        this.dirty = true;
        this.lastMessageTime = SessionState.getTimestampCurrent();
        updateRenderState();
    }

    public String getDefaultName() {
        return AppState.emptyStr;
    }

    public final void updateRenderState() {
        int state = this.flags != 0 ? RENDER_HAS_NOTIFICATION : 0;
        if (this.lastMessageTime != 0) {
            state |= RENDER_HAS_MESSAGES;
        }
        if (this.highlighted) {
            state |= RENDER_IS_HIGHLIGHTED;
        }
        if (!hasUnread()) {
            state |= RENDER_ALL_READ;
        }
        state = !isOffline() ? state | RENDER_AVAILABLE : state & RENDER_OFFLINE_CLEAR_MASK;
        state = !isOnline() ? state | RENDER_NOT_FULLY_ONLINE : state & RENDER_ONLINE_CLEAR_MASK;
        if (state != this.renderState) {
            this.renderState = state;
            AppController.needsLayoutUpdate = true;
        }
    }

    public final void initMessageBuffer() {
        this.messageBuffer = new ByteBuffer();
        saveMessageBuffer();
    }

    public abstract boolean isOnline();

    public abstract boolean hasUnread();

    public boolean isSystem() {
        return false;
    }

    public final void clearStatus() {
        this.statusCode = 0;
        this.dirty = true;
    }

    public final void receiveMessage(long timestamp, StringBuffer textBuffer) {
        receiveMessageFull(timestamp, ObjectPool.toStringAndRelease(textBuffer), MSG_FLAG_INCOMING);
    }

    public final void receiveMessageFull(long timestamp, String text, int flagType) {
        ContactState.setContactId(this.identifier);
        NotificationHelper.playNotificationSound(NotificationHelper.SOUND_MESSAGE_RECEIVED);
        addFlag(flagType);
        this.account.markRead(getIdentifier());
        clearStatus();
        appendMessage(flagType != MSG_FLAG_INCOMING ? 0 : MSG_TYPE_INCOMING, text, timestamp, 0L);
        ContactGroup group = this.account.findGroup(this);
        if (group != null && group.isSpecial) {
            group.toggleSpecial();
        }
        updateRenderState();
        Account acct = this.account;
        String contactId = this.identifier;
        if (acct == null || contactId == null) {
            return;
        }
        Vector tabs = UIState.getTabBars();
        for (int k = tabs.size() - 1; k >= 0; k--) {
            TabBar tabBar = (TabBar) tabs.elementAt(k);
            if (tabBar.account == acct) {
                tabBar.selectedTitle = contactId;
                tabBar.selectedIndex = 0;
                return;
            }
        }
    }

    public final int sendMessage(String text) {
        NotificationHelper.playNotificationSound(NotificationHelper.SOUND_MESSAGE_SENT);
        if (StringUtils.isEmpty(text)) {
            return 309;
        }
        Account acct = this.account;
        long now = SessionState.getTimestampCurrent();
        int sendResult = acct.validateSend(this, text, now);
        if (sendResult != 0) {
            return sendResult;
        }
        appendMessage(MSG_TYPE_OUTGOING, text, now, now);
        this.lastMessageTime = SessionState.getTimestampCurrent();
        updateRenderState();
        return 0;
    }

    public final int validateDelete() {
        return this.account.validateContactDelete(this);
    }

    public final int validateBlock() {
        if (isOnline()) {
            return 310;
        }
        return this.account.validateContactBlock(this);
    }

    public final int validateUnblock() {
        if (isOnline()) {
            return 310;
        }
        return this.account.validateContactUnblock(this);
    }

    @Override // p000.Sortable
    public final int compareTo(Object obj) {
        Contact other = (Contact) obj;
        int stateDiff = other.renderState - this.renderState;
        if (stateDiff != 0) {
            return stateDiff;
        }
        long timeDiff = other.lastMessageTime - this.lastMessageTime;
        return timeDiff != 0 ? timeDiff < 0 ? -1 : 1 : this.sortKey.compareTo(other.sortKey);
    }

    public void clearUnread() {
        if (isOnline()) {
            this.lastMessageTime = 0L;
        }
        this.highlighted = false;
        updateRenderState();
    }

    public final void updateMessageFlag(long targetTime, int flagBit) {
        this.dirty = true;
        ByteBuffer msgBuf = this.messageBuffer == null ? ChunkedRecordStore.readChunkedRecord(this.identifier) : this.messageBuffer;
        this.messageBuffer = msgBuf;
        int bufferLength = msgBuf.length;
        int pos = 0;
        while (true) {
            if (pos >= bufferLength) {
                saveMessageBuffer();
                return;
            }
            int pktLen = msgBuf.peekShortBE(pos);
            int timeOffset = pos + 3 + 8;
            if (targetTime == ((msgBuf.peekIntAt(timeOffset) & 4294967295L) | (msgBuf.peekIntAt(timeOffset + 4) << 32))) {
                msgBuf.data[msgBuf.offset + pos + 2] = (byte) (msgBuf.peekByteAt(pos + 2) | flagBit);
            }
            pos = pos + pktLen + 2;
        }
    }

    public final void appendMessage(int msgType, String text, long timestamp, long sentTime) {
        this.dirty = true;
        ByteBuffer msgBuf = this.messageBuffer == null ? ChunkedRecordStore.readChunkedRecord(this.identifier) : this.messageBuffer;
        this.messageBuffer = msgBuf;
        int maxCount = SettingsState.getMaxContacts() - 1;
        ByteBuffer buffer = this.messageBuffer;
        int msgCount = 0;
        int pos = 0;
        int remaining = buffer.length;
        while (remaining > 0) {
            int pktLen = buffer.peekShortBE(pos);
            pos += pktLen + 2;
            remaining -= pktLen + 2;
            msgCount++;
        }
        while (msgCount > maxCount) {
            buffer.skip(buffer.readShortBE());
            msgCount--;
        }
        msgBuf.writeShortBE(MSG_HEADER_SIZE + (text.length() << 1)).writeByte(msgType).writeLong((timestamp != 0 ? timestamp : System.currentTimeMillis()) + ((SettingsState.getTimezoneOffset() - 13) * 3600000)).writeLong(sentTime).writeAsShorts(text).compact();
        saveMessageBuffer();
        this.lastMessageTime = SessionState.getTimestampCurrent();
        updateRenderState();
    }

    public final boolean hasMessages() {
        return this.lastMessageTime != 0;
    }

    public final long getLastSentTime() {
        long lastForwardedTime = 0;
        ByteBuffer dupe = getMessageBuffer().duplicate();
        while (dupe.length > 0) {
            int entryLen = dupe.readShortBE();
            byte msgType = dupe.readByte();
            dupe.readLong();
            long msgTime = dupe.readLong();
            dupe.skip(entryLen - MSG_HEADER_SIZE);
            if (msgType == MSG_TYPE_FORWARDED) {
                lastForwardedTime = msgTime;
            }
        }
        dupe.clear();
        return lastForwardedTime;
    }

    public final ListView showMessages() {
        this.dirty = false;
        String name = this.displayName;
        RuntimeState.setCurrentMsgText(name);
        int icon = getIcon();
        if ((this instanceof XmppContact) && ((XmppProtocol) this.account).isMailRuVariant() && icon >= 381 && icon <= 384) {
            icon += 4;
        }
        RuntimeState.setMessageIcon(icon);
        Screen msgScreen = Screens.messageSummary(null);
        ByteBuffer dupe = getMessageBuffer().duplicate();
        int dateCode = AppState.getDateCode();
        while (dupe.length > 0) {
            int entryLen = dupe.readShortBE();
            byte msgType = dupe.readByte();
            long msgTime = dupe.readLong() - SessionState.getTimestampOffset();
            long sentTime = dupe.readLong();
            String msgText = Utils.normalizeSpaces(dupe.readUnicodeChars(entryLen - MSG_HEADER_SIZE));
            int colorStyle = (msgType == 0 || msgType == MSG_TYPE_FORWARDED || msgType == MSG_TYPE_INCOMING) ? 0
                    : msgType == MSG_TYPE_OUTGOING ? COLOR_OUTGOING
                    : (msgType & MSG_FLAG_SPECIAL) == 0 ? COLOR_INCOMING : 0;
            if (msgType == MSG_TYPE_FORWARDED) {
                msgScreen.addSeparator(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(this.displayName).append(ResourceAccessor.str(StringResKeys.STR_NAME_SEPARATOR)).append(formatTime(msgTime, dateCode))), 8);
                msgScreen.addIconItem(2, msgText, 0);
                if (this.account.isConnected()) {
                    msgScreen.addExpandableItem(-1, ResourceAccessor.str(StringResKeys.STR_EXPAND_MESSAGE), colorStyle, new Object[]{ObjectPool.integerOf(1), msgText, name, new Long(sentTime)});
                }
            } else if (msgType == MSG_TYPE_INCOMING) {
                int nlIdx = msgText.indexOf(10);
                String header = StringUtils.prefix(msgText, nlIdx);
                String body = StringUtils.suffix(msgText, nlIdx + 1);
                msgScreen.addSeparator(StringUtils.concat(header, formatTime(msgTime, dateCode)), 8);
                addMessageLines(msgScreen, body, colorStyle);
            } else {
                msgScreen.addSeparator(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(msgType == 0 ? this.displayName : this.account.displayName).append(',').append(' ').append(formatTime(msgTime, dateCode))), msgType == 0 ? 8 : 9);
                addMessageLines(msgScreen, msgText, colorStyle);
            }
        }
        dupe.clear();
        return msgScreen;
    }

    private final void addMessageLines(ListView screen, String text, int colorStyle) {
        Vector lines = Conversation.parseConversation(text);
        int size = lines.size();
        for (int k = 0; k < size; k++) {
            String lineText = (String) lines.elementAt(k);
            if (Conversation.isValidFormat(lineText)) {
                screen.addExpandableItem(264, Conversation.decodeMessage(lineText), colorStyle, new Object[]{ObjectPool.integerOf(0), lineText});
            } else {
                screen.addItem(MenuItem.createSeparator().addTextInternal(lineText, 0, colorStyle, this.account.getType()));
            }
        }
        ObjectPool.releaseVector(lines);
    }

    private final ByteBuffer getMessageBuffer() {
        if (this.messageBuffer == null) {
            this.messageBuffer = ChunkedRecordStore.readChunkedRecord(this.identifier);
        }
        return this.messageBuffer;
    }

    private final void saveMessageBuffer() {
        ChunkedRecordStore.writeChunkedRecord(this.identifier, getMessageBuffer().duplicate());
    }

    public final ListView showMessageSummary() {
        String truncated;
        Screen msgScreen = Screens.messageDetail(null);
        ByteBuffer dupe = getMessageBuffer().duplicate();
        while (dupe.length > 0) {
            int entryLen = dupe.readShortBE();
            dupe.readByte();
            dupe.readLong();
            dupe.readLong();
            String fullText = dupe.readUnicodeChars(entryLen - MSG_HEADER_SIZE);
            if (fullText.length() > SUMMARY_TRUNCATION_LENGTH) {
                truncated = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.prefix(fullText, SUMMARY_TRUNCATION_LENGTH)).append((char) 8230));
            } else {
                truncated = fullText;
            }
            msgScreen.addFullItem(-1, (String) null, truncated, 200, fullText);
        }
        dupe.clear();
        return msgScreen;
    }

    public final int getDefaultAction() {
        if (getMessageBuffer().length > 0 || !this.account.isConnected()) {
            return 40;
        }
        if (isOffline()) {
            return ContactListManager.clearSmsFields();
        }
        return 63;
    }

    public abstract MenuItem createMenuItem();

    public abstract boolean canDelete();

    public abstract boolean canBlock();

    public abstract boolean canUnblock();

    private static String formatTime(long millis, int todayDateCode) {
        Calendar cal = AppState.getCalendar();
        cal.setTime(new Date(millis));
        StringBuffer sb = ObjectPool.newStringBuffer();
        int yearPart = cal.get(Calendar.YEAR) << 16;
        int month = cal.get(Calendar.MONTH);
        int datePart = yearPart + (month << 8);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (datePart + day != todayDateCode) {
            sb.append(Utils.zeroPad(day)).append('/').append(Utils.zeroPad(month + 1)).append(' ');
        }
        return ObjectPool.toStringAndRelease(sb.append(Utils.zeroPad(cal.get(Calendar.HOUR_OF_DAY))).append(':').append(Utils.zeroPad(cal.get(Calendar.MINUTE))));
    }

    public abstract String getIdentifier();

    public boolean isOffline() {
        return false;
    }

    public abstract void performAction();

    public final void setDisplayName(String name) {
        if (StringUtils.equals(name, this.displayName)) {
            return;
        }
        this.displayName = name;
        this.sortKey = StringUtils.intern(name.toLowerCase());
        AppController.needsLayoutUpdate = true;
    }

    public final String toString() {
        return this.displayName;
    }

    public void clearRegistrationData() {
    }

    public final int getContextAction() {
        if (canDelete()) {
            return 267;
        }
        return canBlock() ? 266 : -1;
    }
}
