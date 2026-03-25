package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.StateKeys;
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
import java.util.Vector;

/* renamed from: l */
/* loaded from: MobileAgent_3.9.jar:l.class */
public abstract class Contact implements Sortable {

    /* renamed from: o */
    public final Account account;

    /* renamed from: a */
    private ByteBuffer messageBuffer;

    /* renamed from: p */
    public boolean highlighted;

    /* renamed from: q */
    public int statusCode;

    /* renamed from: r */
    public int defaultIcon;

    /* renamed from: s */
    public byte flags;

    /* renamed from: t */
    public boolean dirty;

    /* renamed from: u */
    public String displayName;

    /* renamed from: v */
    public String sortKey;

    /* renamed from: b */
    private int renderState;

    /* renamed from: c */
    private long lastMessageTime;

    /* renamed from: w */
    public String identifier;

    /* renamed from: x */
    public String extra;

    public Contact(Account acct) {
        this.account = acct;
    }

    /* renamed from: a */
    public abstract void deserialize(ByteBuffer buffer);

    /* renamed from: e */
    public int getIcon() {
        if (this.flags != 0) {
            return (this.flags & 1) != 0 ? 16384 : 16386;
        }
        if (this.statusCode != 0) {
            return 26;
        }
        return this.defaultIcon;
    }

    /* renamed from: c */
    public final void addFlag(int i) {
        this.flags = (byte) (this.flags | i);
        AppController.markContactRead(this);
        this.dirty = true;
        this.lastMessageTime = AppState.getLong(StateKeys.TIMESTAMP_CURRENT);
        updateRenderState();
    }

    /* renamed from: g */
    public String getDefaultName() {
        return AppState.emptyStr;
    }

    /* renamed from: A */
    public final void updateRenderState() {
        int i = this.flags != 0 ? 1073741824 : 0;
        if (this.lastMessageTime != 0) {
            i |= 268435456;
        }
        if (this.highlighted) {
            i |= 536870912;
        }
        if (!hasUnread()) {
            i |= 67108864;
        }
        int i2 = !isOffline() ? i | 33554432 : i & (-1912602625);
        int i3 = !isOnline() ? i2 | 134217728 : i2 & (-100663297);
        if (i3 != this.renderState) {
            this.renderState = i3;
            AppController.needsLayoutUpdate = true;
        }
    }

    /* renamed from: B */
    public final void initMessageBuffer() {
        this.messageBuffer = new ByteBuffer();
        saveMessageBuffer();
    }

    /* renamed from: m */
    public abstract boolean isOnline();

    /* renamed from: l */
    public abstract boolean hasUnread();

    /* renamed from: n */
    public boolean isSystem() {
        return false;
    }

    /* renamed from: C */
    public final void clearStatus() {
        this.statusCode = 0;
        this.dirty = true;
    }

    /* renamed from: a */
    public final void receiveMessage(long j, StringBuffer stringBuffer) {
        receiveMessageFull(j, ObjectPool.toStringAndRelease(stringBuffer), 4);
    }

    /* renamed from: a */
    public final void receiveMessageFull(long j, String str, int i) {
        TabBar tabBar;
        AppState.setObject(StateKeys.SLOT_CURRENT_CONTACT_ID, (Object) this.identifier);
        ResourceManager.playNotificationSound(2);
        addFlag(i);
        this.account.markRead(getIdentifier());
        clearStatus();
        appendMessage(i != 4 ? 0 : 8, str, j, 0L);
        ContactGroup group = this.account.findGroup(this);
        if (group != null && group.isSpecial) {
            group.toggleSpecial();
        }
        updateRenderState();
        Account acct = this.account;
        String str2 = this.identifier;
        if (acct == null || str2 == null) {
            return;
        }
        Vector tabs = AppState.getVector(StateKeys.VEC_TAB_BARS);
        int size = tabs.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                tabBar = (TabBar) tabs.elementAt(size);
            }
        } while (tabBar.account != acct);
        tabBar.selectedTitle = str2;
        tabBar.selectedIndex = 0;
    }

    /* renamed from: b */
    public final int sendMessage(String str) {
        ResourceManager.playNotificationSound(4);
        if (StringUtils.isEmpty(str)) {
            return 309;
        }
        Account acct = this.account;
        long now = AppState.getLong(StateKeys.TIMESTAMP_CURRENT);
        int sendResult = acct.validateSend(this, str, now);
        if (0 != sendResult) {
            return sendResult;
        }
        appendMessage(1, str, now, now);
        this.lastMessageTime = AppState.getLong(StateKeys.TIMESTAMP_CURRENT);
        updateRenderState();
        return 0;
    }

    /* renamed from: D */
    public final int validateDelete() {
        return this.account.validateContactDelete(this);
    }

    /* renamed from: E */
    public final int validateBlock() {
        if (isOnline()) {
            return 310;
        }
        return this.account.validateContactBlock(this);
    }

    /* renamed from: F */
    public final int validateUnblock() {
        if (isOnline()) {
            return 310;
        }
        return this.account.validateContactUnblock(this);
    }

    @Override // p000.Sortable
    /* renamed from: a */
    public final int compareTo(Object obj) {
        Contact other = (Contact) obj;
        int i = other.renderState - this.renderState;
        if (i != 0) {
            return i;
        }
        long j = other.lastMessageTime - this.lastMessageTime;
        return j != 0 ? j < 0 ? -1 : 1 : this.sortKey.compareTo(other.sortKey);
    }

    /* renamed from: c */
    public void clearUnread() {
        if (isOnline()) {
            this.lastMessageTime = 0L;
        }
        this.highlighted = false;
        updateRenderState();
    }

    /* JADX DEBUG: Move duplicate insns, count: 1 to block B:11:0x0097 */
    /* renamed from: a */
    public final void updateMessageFlag(long j, int i) {
        this.dirty = true;
        ByteBuffer msgBuf = this.messageBuffer == null ? ChunkedRecordStore.readChunkedRecord(this.identifier) : this.messageBuffer;
        this.messageBuffer = msgBuf;
        int i2 = msgBuf.length;
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= i2) {
                saveMessageBuffer();
                return;
            }
            int pktLen = msgBuf.peekShortBE(i4);
            int i5 = i4 + 3 + 8;
            if (j == ((msgBuf.peekIntAt(i5) & 4294967295L) | (msgBuf.peekIntAt(i5 + 4) << 32))) {
                msgBuf.data[msgBuf.offset + i4 + 2] = (byte) (msgBuf.peekByteAt(i4 + 2) | i);
            }
            i3 = i4 + pktLen + 2;
        }
    }

    /* renamed from: a */
    public final void appendMessage(int i, String str, long j, long j2) {
        this.dirty = true;
        ByteBuffer msgBuf = this.messageBuffer == null ? ChunkedRecordStore.readChunkedRecord(this.identifier) : this.messageBuffer;
        this.messageBuffer = msgBuf;
        int maxCount = AppState.getInt(StateKeys.SETTING_MAX_CONTACTS) - 1;
        ByteBuffer buffer = this.messageBuffer;
        int i2 = 0;
        int i3 = 0;
        int i4 = buffer.length;
        while (i4 > 0) {
            int pktLen = buffer.peekShortBE(i3);
            i3 += pktLen + 2;
            i4 -= pktLen + 2;
            i2++;
        }
        while (i2 > maxCount) {
            buffer.skip(buffer.readShortBE());
            i2--;
        }
        msgBuf.writeShortBE(17 + (str.length() << 1)).writeByte(i).writeLong((j != 0 ? j : System.currentTimeMillis()) + ((AppState.getInt(StateKeys.SETTING_TIMEZONE_OFFSET) - 13) * 3600000)).writeLong(j2).writeAsShorts(str).compact();
        saveMessageBuffer();
        this.lastMessageTime = AppState.getLong(StateKeys.TIMESTAMP_CURRENT);
        updateRenderState();
    }

    /* renamed from: G */
    public final boolean hasMessages() {
        return this.lastMessageTime != 0;
    }

    /* renamed from: H */
    public final long getLastSentTime() {
        long j = 0;
        ByteBuffer dupe = getMessageBuffer().duplicate();
        while (dupe.length > 0) {
            int entryLen = dupe.readShortBE();
            byte msgType = dupe.readByte();
            dupe.readLong();
            long msgTime = dupe.readLong();
            dupe.skip(entryLen - 17);
            if (msgType == 16) {
                j = msgTime;
            }
        }
        dupe.clear();
        return j;
    }

    /* renamed from: I */
    public final Screen showMessages() {
        this.dirty = false;
        String str = this.displayName;
        AppState.setObject(StateKeys.SLOT_CURRENT_MSG_TEXT, (Object) str);
        int icon = getIcon();
        if ((this instanceof XmppContact) && ((XmppProtocol) this.account).isMailRuVariant() && icon >= 381 && icon <= 384) {
            icon += 4;
        }
        AppState.setInt(StateKeys.INT_MESSAGE_ICON, icon);
        Screen msgScreen = ScreenManager.createScreen(ScreenDef.MESSAGE_SUMMARY);
        ByteBuffer dupe = getMessageBuffer().duplicate();
        int dateCode = AppState.getDateCode();
        while (dupe.length > 0) {
            int entryLen = dupe.readShortBE();
            byte msgType = dupe.readByte();
            long msgTime = dupe.readLong() - AppState.getLong(StateKeys.TIMESTAMP_OFFSET);
            long sentTime = dupe.readLong();
            String msgText = Utils.normalizeSpaces(dupe.readUnicodeChars(entryLen - 17));
            int i = (msgType == 0 || msgType == 16 || msgType == 8) ? 0 : msgType == 1 ? 11 : (msgType & 64) == 0 ? 12 : 0;
            if (msgType == 16) {
                msgScreen.addSeparator(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(this.displayName).append(AppState.getString(StateKeys.STR_NAME_SEPARATOR)).append(formatTime(msgTime, dateCode))), 8);
                msgScreen.addIconItem(2, msgText, 0);
                if (this.account.isConnected()) {
                    msgScreen.addExpandableItem(-1, AppState.getString(StateKeys.STR_EXPAND_MESSAGE), i, new Object[]{ResourceManager.integerOf(1), msgText, str, new Long(sentTime)});
                }
            } else if (msgType == 8) {
                int nlIdx = msgText.indexOf(10);
                String header = StringUtils.prefix(msgText, nlIdx);
                String body = StringUtils.suffix(msgText, nlIdx + 1);
                msgScreen.addSeparator(StringUtils.concat(header, formatTime(msgTime, dateCode)), 8);
                addMessageLines(msgScreen, body, i);
            } else {
                msgScreen.addSeparator(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(msgType == 0 ? this.displayName : this.account.displayName).append(',').append(' ').append(formatTime(msgTime, dateCode))), msgType == 0 ? 8 : 9);
                addMessageLines(msgScreen, msgText, i);
            }
        }
        dupe.clear();
        return msgScreen;
    }

    /* renamed from: a */
    private final void addMessageLines(Screen screen, String str, int i) {
        Vector lines = Conversation.parseConversation(str);
        int size = lines.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str2 = (String) lines.elementAt(i2);
            if (Conversation.isValidFormat(str2)) {
                screen.addExpandableItem(264, Conversation.decodeMessage(str2), i, new Object[]{ResourceManager.integerOf(0), str2});
            } else {
                screen.addItem(MenuItem.createSeparator().addTextInternal(str2, 0, i, this.account.getType()));
            }
        }
        ObjectPool.releaseVector(lines);
    }

    /* renamed from: f */
    private final ByteBuffer getMessageBuffer() {
        if (this.messageBuffer == null) {
            this.messageBuffer = ChunkedRecordStore.readChunkedRecord(this.identifier);
        }
        return this.messageBuffer;
    }

    /* renamed from: o */
    private final void saveMessageBuffer() {
        ChunkedRecordStore.writeChunkedRecord(this.identifier, getMessageBuffer().duplicate());
    }

    /* renamed from: J */
    public final Screen showMessageSummary() {
        String truncated;
        Screen msgScreen = ScreenManager.createScreen(ScreenDef.MESSAGE_DETAIL);
        ByteBuffer dupe = getMessageBuffer().duplicate();
        while (dupe.length > 0) {
            int entryLen = dupe.readShortBE();
            dupe.readByte();
            dupe.readLong();
            dupe.readLong();
            String fullText = dupe.readUnicodeChars(entryLen - 17);
            if (fullText.length() > 50) {
                truncated = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.prefix(fullText, 50)).append((char) 8230));
            } else {
                truncated = fullText;
            }
            msgScreen.addFullItem(-1, (String) null, truncated, 200, fullText);
        }
        dupe.clear();
        return msgScreen;
    }

    /* renamed from: K */
    public final int getDefaultAction() {
        if (getMessageBuffer().length > 0 || !this.account.isConnected()) {
            return 40;
        }
        if (isOffline()) {
            return ResourceManager.clearSmsFields();
        }
        return 63;
    }

    /* renamed from: b */
    public abstract MenuItem createMenuItem();

    /* renamed from: i */
    public abstract boolean canDelete();

    /* renamed from: j */
    public abstract boolean canBlock();

    /* renamed from: k */
    public abstract boolean canUnblock();

    /* renamed from: b */
    private static String formatTime(long j, int i) {
        Calendar cal = AppState.getCalendar();
        cal.setTime(new Date(j));
        StringBuffer sb = ObjectPool.newStringBuffer();
        int i2 = cal.get(1) << 16;
        int i3 = cal.get(2);
        int i4 = i2 + (i3 << 8);
        int i5 = cal.get(5);
        if (i4 + i5 != i) {
            sb.append(Utils.zeroPad(i5)).append('/').append(Utils.zeroPad(i3 + 1)).append(' ');
        }
        return ObjectPool.toStringAndRelease(sb.append(Utils.zeroPad(cal.get(11))).append(':').append(Utils.zeroPad(cal.get(12))));
    }

    /* renamed from: a */
    public abstract String getIdentifier();

    /* renamed from: d */
    public boolean isOffline() {
        return false;
    }

    /* renamed from: h */
    public abstract void performAction();

    /* renamed from: c */
    public final void setDisplayName(String str) {
        if (StringUtils.equals(str, this.displayName)) {
            return;
        }
        this.displayName = str;
        this.sortKey = StringUtils.intern(str.toLowerCase());
        AppController.needsLayoutUpdate = true;
    }

    public final String toString() {
        return this.displayName;
    }

    /* renamed from: L */
    public void mo148L() {
    }

    /* renamed from: M */
    public final int getContextAction() {
        if (canDelete()) {
            return 267;
        }
        return canBlock() ? 266 : -1;
    }
}
