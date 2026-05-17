package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.MessageListener;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.net.InlineImageCache;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ChunkedRecordStore;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

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

    // Flag bits for contact notification state
    private static final int FLAG_HAS_UNREAD = 1;

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

    // Error codes (string resource pool indices)
    private static final int ERROR_EMPTY_MESSAGE = 309;
    private static final int ERROR_CONTACT_ONLINE = 310;

    // Separator header text styles
    private static final int HEADER_STYLE_RECEIVED = 8;
    private static final int HEADER_STYLE_SENT = 9;

    // Icon IDs for message rendering
    private static final int ICON_FORWARDED = 2;
    private static final int ICON_EXPANDABLE = 264;

    // Message summary layout width
    private static final int MESSAGE_SUMMARY_WIDTH = 200;

    // Context menu action IDs (tab bar system)
    private static final int ACTION_DELETE_CONTACT = 267;
    private static final int ACTION_BLOCK_CONTACT = 266;
    private static final int NO_ACTION = -1;

    // Timezone calculation constants
    private static final int TIMEZONE_BASE_OFFSET = 13;
    private static final int MILLIS_PER_HOUR = 3600000;

    private static final char CHAR_ELLIPSIS = '…';

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

    public abstract void serialize(ByteBuffer buffer);

    public int getIcon() {
        if (this.flags != 0) {
            return (this.flags & FLAG_HAS_UNREAD) != 0 ? ICON_BLINK_UNREAD : ICON_BLINK_NOTIFICATION;
        }
        if (this.statusCode != 0) {
            return ICON_TYPING;
        }
        return this.defaultIcon;
    }

    public int getDisplayIcon() {
        return getIcon();
    }

    public final void addFlag(int flagBit) {
        this.flags = (byte) (this.flags | flagBit);
        this.account.notifyContactActivated(this);
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
            this.account.notifyContactListUpdated();
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
        addFlag(flagType);
        this.account.markRead(getIdentifier());
        clearStatus();
        appendMessage(flagType != MSG_FLAG_INCOMING ? 0 : MSG_TYPE_INCOMING, text, timestamp, 0L);
        ContactGroup group = this.account.findGroup(this);
        if (group != null && group.isSpecial) {
            group.toggleSpecial();
        }
        updateRenderState();
        this.account.notifyMessageReceived(this, MessageListener.SOUND_MESSAGE_RECEIVED);
    }

    public final int sendMessage(String text) {
        this.account.notifyMessageSent(this);
        if (StringUtils.isEmpty(text)) {
            return ERROR_EMPTY_MESSAGE;
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
            return ERROR_CONTACT_ONLINE;
        }
        return this.account.validateContactBlock(this);
    }

    public final int validateUnblock() {
        if (isOnline()) {
            return ERROR_CONTACT_ONLINE;
        }
        return this.account.validateContactUnblock(this);
    }

    @Override
    public final int compareTo(Object obj) {
        Contact other = (Contact) obj;
        int stateDiff = other.renderState - this.renderState;
        if (stateDiff != 0) {
            return stateDiff;
        }
        long timeDiff = other.lastMessageTime - this.lastMessageTime;
        if (timeDiff != 0) {
            return timeDiff < 0 ? -1 : 1;
        }
        return this.sortKey.compareTo(other.sortKey);
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
        while (pos < bufferLength) {
            int pktLen = msgBuf.peekShortBE(pos);
            int sentTimeOffset = pos + 3 + 8;
            long sentTime = (msgBuf.peekIntAt(sentTimeOffset) & 0xFFFFFFFFL) | ((long) msgBuf.peekIntAt(sentTimeOffset + 4) << 32);
            if (targetTime == sentTime) {
                msgBuf.data[msgBuf.offset + pos + 2] = (byte) (msgBuf.peekByteAt(pos + 2) | flagBit);
            }
            pos = pos + pktLen + 2;
        }
        saveMessageBuffer();
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
        long effectiveTime = timestamp != 0 ? timestamp : System.currentTimeMillis();
        long adjustedTime = effectiveTime + ((SettingsState.getTimezoneOffset() - TIMEZONE_BASE_OFFSET) * MILLIS_PER_HOUR);
        int textByteLength = text.length() << 1;
        msgBuf.writeShortBE(MSG_HEADER_SIZE + textByteLength)
              .writeByte(msgType)
              .writeLong(adjustedTime)
              .writeLong(sentTime)
              .writeAsShorts(text)
              .compact();
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
        RuntimeState.setMessageIcon(getDisplayIcon());
        Screen msgScreen = Screens.messageSummary();
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
                renderForwardedMessage(msgScreen, name, msgText, msgTime, sentTime, colorStyle, dateCode);
            } else if (msgType == MSG_TYPE_INCOMING) {
                renderIncomingMessage(msgScreen, msgText, colorStyle, msgTime, dateCode);
            } else {
                renderOutgoingMessage(msgScreen, msgType, msgText, colorStyle, msgTime, dateCode);
            }
        }
        dupe.clear();
        return msgScreen;
    }

    private void renderForwardedMessage(ListView screen, String name, String msgText,
            long msgTime, long sentTime, int colorStyle, int dateCode) {
        screen.addSeparator(ObjectPool.toStringAndRelease(
                ObjectPool.newStringBuffer().append(name)
                        .append(StringPool.get(StringResKeys.STR_NAME_SEPARATOR))
                        .append(formatTime(msgTime, dateCode))), HEADER_STYLE_RECEIVED);
        screen.addIconItem(ICON_FORWARDED, msgText, 0);
        if (this.account.isConnected()) {
            screen.addExpandableItem(NO_ACTION,
                    StringPool.get(StringResKeys.STR_EXPAND_MESSAGE), colorStyle,
                    new Object[]{ObjectPool.integerOf(1), msgText, name, new Long(sentTime)});
        }
    }

    private void renderIncomingMessage(ListView screen, String msgText, int colorStyle,
            long msgTime, int dateCode) {
        int nlIdx = msgText.indexOf('\n');
        String header = StringUtils.prefix(msgText, nlIdx);
        String body = StringUtils.suffix(msgText, nlIdx + 1);
        screen.addSeparator(StringUtils.concat(header, formatTime(msgTime, dateCode)),
                HEADER_STYLE_RECEIVED);
        addMessageLines(screen, body, colorStyle);
    }

    private void renderOutgoingMessage(ListView screen, int msgType, String msgText,
            int colorStyle, long msgTime, int dateCode) {
        String senderName = msgType == 0 ? this.displayName : this.account.displayName;
        screen.addSeparator(ObjectPool.toStringAndRelease(
                ObjectPool.newStringBuffer().append(senderName)
                        .append(',').append(' ')
                        .append(formatTime(msgTime, dateCode))),
                msgType == 0 ? HEADER_STYLE_RECEIVED : HEADER_STYLE_SENT);
        addMessageLines(screen, msgText, colorStyle);
    }

    private void addMessageLines(ListView screen, String text, int colorStyle) {
        Vector lines = Conversation.parseConversation(text);
        int size = lines.size();
        for (int k = 0; k < size; k++) {
            String lineText = (String) lines.elementAt(k);
            if (Conversation.isValidFormat(lineText)) {
                screen.addExpandableItem(ICON_EXPANDABLE, Conversation.decodeMessage(lineText),
                        colorStyle, new Object[]{ObjectPool.integerOf(0), lineText});
            } else if (InlineImageCache.isImageUrl(lineText)) {
                screen.addExpandableItem(-1, "[Картинка]", colorStyle,
                        new Object[]{ObjectPool.integerOf(2), lineText});
            } else {
                screen.addItem(MenuItem.createSeparator().addTextInternal(lineText, 0, colorStyle,
                        this.account.getType()));
            }
        }
        ObjectPool.releaseVector(lines);
    }

    private ByteBuffer getMessageBuffer() {
        if (this.messageBuffer == null) {
            this.messageBuffer = ChunkedRecordStore.readChunkedRecord(this.identifier);
        }
        return this.messageBuffer;
    }

    private void saveMessageBuffer() {
        ChunkedRecordStore.writeChunkedRecord(this.identifier, getMessageBuffer().duplicate());
    }

    public final ListView showMessageSummary() {
        Screen msgScreen = Screens.messageDetail();
        ByteBuffer dupe = getMessageBuffer().duplicate();
        while (dupe.length > 0) {
            int entryLen = dupe.readShortBE();
            dupe.readByte();
            dupe.readLong();
            dupe.readLong();
            String fullText = dupe.readUnicodeChars(entryLen - MSG_HEADER_SIZE);
            String truncated;
            if (fullText.length() > SUMMARY_TRUNCATION_LENGTH) {
                truncated = ObjectPool.toStringAndRelease(
                        ObjectPool.newStringBuffer()
                                .append(StringUtils.prefix(fullText, SUMMARY_TRUNCATION_LENGTH))
                                .append(CHAR_ELLIPSIS));
            } else {
                truncated = fullText;
            }
            msgScreen.addFullItem(NO_ACTION, null, truncated, MESSAGE_SUMMARY_WIDTH, fullText);
        }
        dupe.clear();
        return msgScreen;
    }

    public final int getDefaultAction() {
        if (getMessageBuffer().length > 0 || !this.account.isConnected()) {
            return ScreenId.CLEAR_SEARCH;
        }
        if (isOffline()) {
            return ContactListManager.clearSmsFields();
        }
        return ScreenId.STATUS_INPUT;
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

    // Protocol-specific email/login identifier (for ContactInfo, email composition, etc.)
    // MrimContact -> simpleIdentifier, MmpContact -> raw identifier, XmppContact -> jabberId
    public String getContactEmail() { return getIdentifier(); }

    // Whether this is an MRIM-type contact (for type code dispatch, group membership checks)
    public boolean isMrimType() { return false; }

    // Whether this contact supports XMPP presence subscription
    public boolean canSubscribe() { return false; }

    // Perform presence subscription action. Returns result code.
    public int subscribe(int subscriptionType) { return 0; }

    // Whether user details can be requested (MRIM "wake up" / detail request)
    public boolean canRequestDetails() { return false; }

    // Request user details from server. Returns error code or 0.
    public int requestDetails() { return 0; }

    // Whether this contact has location/VCard data for map display
    public boolean hasLocationData() { return false; }

    // Whether this contact can be edited (renamed, etc.)
    public boolean isEditable() { return false; }

    // Emoticon resource base index for this contact's protocol
    public int getEmoticonBase() { return StringResKeys.EMOTICON_NAMES_BASE; }

    // Populate protocol-specific fields into a ContactInfo object
    public void populateContactInfo(Object contactInfo) {}

    // Whether this contact has loaded vCard/profile data
    public boolean hasVCard() { return false; }

    // Whether this contact supports file transfer
    public boolean supportsFileTransfer() { return false; }

    // Group membership list for MRIM phone contacts
    public Vector getGroupMembership() { return null; }

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
        this.account.notifyContactListUpdated();
    }

    public final String toString() {
        return this.displayName;
    }

    public void clearRegistrationData() {
    }

    public final int getContextAction() {
        if (canDelete()) {
            return ACTION_DELETE_CONTACT;
        }
        return canBlock() ? ACTION_BLOCK_CONTACT : NO_ACTION;
    }
}
