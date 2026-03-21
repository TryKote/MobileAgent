package p000;

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

    public Contact(Account abstractC0037h) {
        this.account = abstractC0037h;
    }

    /* renamed from: a */
    public abstract void deserialize(ByteBuffer c0043n);

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
        AppController.m414a(this);
        this.dirty = true;
        this.lastMessageTime = AppState.getLong(1530);
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
            AppController.f152f = true;
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
        receiveMessageFull(j, NetworkUtils.bufToStringCached(stringBuffer), 4);
    }

    /* renamed from: a */
    public final void receiveMessageFull(long j, String str, int i) {
        TabBar c0008ah;
        AppState.setObject(1237, (Object) this.identifier);
        ResourceManager.playNotificationSound(2);
        addFlag(i);
        this.account.markRead(getIdentifier());
        clearStatus();
        appendMessage(i != 4 ? 0 : 8, str, j, 0L);
        ContactGroup abstractC0046qM1080g = this.account.findGroup(this);
        if (abstractC0046qM1080g != null && abstractC0046qM1080g.isSpecial) {
            abstractC0046qM1080g.toggleSpecial();
        }
        updateRenderState();
        Account abstractC0037h = this.account;
        String str2 = this.identifier;
        if (abstractC0037h == null || str2 == null) {
            return;
        }
        Vector vectorM614m = AppState.getVector(1246);
        int size = vectorM614m.size();
        do {
            size--;
            if (size < 0) {
                return;
            } else {
                c0008ah = (TabBar) vectorM614m.elementAt(size);
            }
        } while (c0008ah.account != abstractC0037h);
        c0008ah.selectedTitle = str2;
        c0008ah.selectedIndex = 0;
    }

    /* renamed from: b */
    public final int sendMessage(String str) {
        ResourceManager.playNotificationSound(4);
        if (StringUtils.isEmpty(str)) {
            return 309;
        }
        Account abstractC0037h = this.account;
        long jM598g = AppState.getLong(1530);
        int iMo125a = abstractC0037h.validateSend(this, str, jM598g);
        if (0 != iMo125a) {
            return iMo125a;
        }
        appendMessage(1, str, jM598g, jM598g);
        this.lastMessageTime = AppState.getLong(1530);
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
        Contact abstractC0041l = (Contact) obj;
        int i = abstractC0041l.renderState - this.renderState;
        if (i != 0) {
            return i;
        }
        long j = abstractC0041l.lastMessageTime - this.lastMessageTime;
        return j != 0 ? j < 0 ? -1 : 1 : this.sortKey.compareTo(abstractC0041l.sortKey);
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
        ByteBuffer c0043nM851h = this.messageBuffer == null ? XmppMailRuProtocol.readChunkedRecord(this.identifier) : this.messageBuffer;
        this.messageBuffer = c0043nM851h;
        int i2 = c0043nM851h.length;
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= i2) {
                saveMessageBuffer();
                return;
            }
            int iM1351l = c0043nM851h.peekShortBE(i4);
            int i5 = i4 + 3 + 8;
            if (j == ((c0043nM851h.peekIntAt(i5) & 4294967295L) | (c0043nM851h.peekIntAt(i5 + 4) << 32))) {
                c0043nM851h.data[c0043nM851h.offset + i4 + 2] = (byte) (c0043nM851h.peekByteAt(i4 + 2) | i);
            }
            i3 = i4 + iM1351l + 2;
        }
    }

    /* renamed from: a */
    public final void appendMessage(int i, String str, long j, long j2) {
        this.dirty = true;
        ByteBuffer c0043nM851h = this.messageBuffer == null ? XmppMailRuProtocol.readChunkedRecord(this.identifier) : this.messageBuffer;
        this.messageBuffer = c0043nM851h;
        int iM586d = AppState.getInt(102) - 1;
        ByteBuffer c0043n = this.messageBuffer;
        int i2 = 0;
        int i3 = 0;
        int i4 = c0043n.length;
        while (i4 > 0) {
            int iM1351l = c0043n.peekShortBE(i3);
            i3 += iM1351l + 2;
            i4 -= iM1351l + 2;
            i2++;
        }
        while (i2 > iM586d) {
            c0043n.skip(c0043n.readShortBE());
            i2--;
        }
        c0043nM851h.writeShortBE(17 + (str.length() << 1)).writeByte(i).writeLong((j != 0 ? j : System.currentTimeMillis()) + ((AppState.getInt(246) - 13) * 3600000)).writeLong(j2).writeAsShorts(str).compact();
        saveMessageBuffer();
        this.lastMessageTime = AppState.getLong(1530);
        updateRenderState();
    }

    /* renamed from: G */
    public final boolean hasMessages() {
        return this.lastMessageTime != 0;
    }

    /* renamed from: H */
    public final long getLastSentTime() {
        long j = 0;
        ByteBuffer c0043nM1380F = getMessageBuffer().duplicate();
        while (c0043nM1380F.length > 0) {
            int iM1353u = c0043nM1380F.readShortBE();
            byte bM1344o = c0043nM1380F.readByte();
            c0043nM1380F.readLong();
            long jM1341m = c0043nM1380F.readLong();
            c0043nM1380F.skip(iM1353u - 17);
            if (bM1344o == 16) {
                j = jM1341m;
            }
        }
        c0043nM1380F.clear();
        return j;
    }

    /* renamed from: I */
    public final Screen showMessages() {
        this.dirty = false;
        String str = this.displayName;
        AppState.setObject(1290, (Object) str);
        int iMo139e = getIcon();
        if ((this instanceof XmppContact) && ((XmppProtocol) this.account).mo83f() && iMo139e >= 381 && iMo139e <= 384) {
            iMo139e += 4;
        }
        AppState.setInt(2594, iMo139e);
        Screen c0013amM75b = ScreenManager.createScreen(2591);
        ByteBuffer c0043nM1380F = getMessageBuffer().duplicate();
        int iM624l = AppState.getDateCode();
        while (c0043nM1380F.length > 0) {
            int iM1353u = c0043nM1380F.readShortBE();
            byte bM1344o = c0043nM1380F.readByte();
            long jM1341m = c0043nM1380F.readLong() - AppState.getLong(1532);
            long jM1341m2 = c0043nM1380F.readLong();
            String strM539n = Utils.m539n(c0043nM1380F.readUnicodeChars(iM1353u - 17));
            int i = (bM1344o == 0 || bM1344o == 16 || bM1344o == 8) ? 0 : bM1344o == 1 ? 11 : (bM1344o & 64) == 0 ? 12 : 0;
            if (bM1344o == 16) {
                c0013amM75b.addSeparator(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(this.displayName).append(AppState.getString(311)).append(formatTime(jM1341m, iM624l))), 8);
                c0013amM75b.addIconItem(2, strM539n, 0);
                if (this.account.isConnected()) {
                    c0013amM75b.addExpandableItem(-1, AppState.getString(839), i, new Object[]{ResourceManager.integerOf(1), strM539n, str, new Long(jM1341m2)});
                }
            } else if (bM1344o == 8) {
                int iIndexOf = strM539n.indexOf(10);
                String strM13b = StringUtils.prefix(strM539n, iIndexOf);
                String strM15c = StringUtils.suffix(strM539n, iIndexOf + 1);
                c0013amM75b.addSeparator(StringUtils.concat(strM13b, formatTime(jM1341m, iM624l)), 8);
                addMessageLines(c0013amM75b, strM15c, i);
            } else {
                c0013amM75b.addSeparator(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(bM1344o == 0 ? this.displayName : this.account.displayName).append(',').append(' ').append(formatTime(jM1341m, iM624l))), bM1344o == 0 ? 8 : 9);
                addMessageLines(c0013amM75b, strM539n, i);
            }
        }
        c0043nM1380F.clear();
        return c0013amM75b;
    }

    /* renamed from: a */
    private final void addMessageLines(Screen c0013am, String str, int i) {
        Vector vectorM1098a = Conversation.parseConversation(str);
        int size = vectorM1098a.size();
        for (int i2 = 0; i2 < size; i2++) {
            String str2 = (String) vectorM1098a.elementAt(i2);
            if (Conversation.isValidFormat(str2)) {
                c0013am.addExpandableItem(264, Conversation.decodeMessage(str2), i, new Object[]{ResourceManager.integerOf(0), str2});
            } else {
                c0013am.addItem(MenuItem.createSeparator().addTextInternal(str2, 0, i, this.account.getType()));
            }
        }
        NetworkUtils.releaseVector(vectorM1098a);
    }

    /* renamed from: f */
    private final ByteBuffer getMessageBuffer() {
        if (this.messageBuffer == null) {
            this.messageBuffer = XmppMailRuProtocol.readChunkedRecord(this.identifier);
        }
        return this.messageBuffer;
    }

    /* renamed from: o */
    private final void saveMessageBuffer() {
        XmppMailRuProtocol.writeChunkedRecord(this.identifier, getMessageBuffer().duplicate());
    }

    /* renamed from: J */
    public final Screen showMessageSummary() {
        String strM1215a;
        Screen c0013amM75b = ScreenManager.createScreen(2631);
        ByteBuffer c0043nM1380F = getMessageBuffer().duplicate();
        while (c0043nM1380F.length > 0) {
            int iM1353u = c0043nM1380F.readShortBE();
            c0043nM1380F.readByte();
            c0043nM1380F.readLong();
            c0043nM1380F.readLong();
            String strM1369q = c0043nM1380F.readUnicodeChars(iM1353u - 17);
            if (strM1369q.length() > 50) {
                strM1215a = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(StringUtils.prefix(strM1369q, 50)).append((char) 8230));
            } else {
                strM1215a = strM1369q;
            }
            c0013amM75b.addFullItem(-1, (String) null, strM1215a, 200, strM1369q);
        }
        c0043nM1380F.clear();
        return c0013amM75b;
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
        Calendar calendarM622k = AppState.getCalendar();
        calendarM622k.setTime(new Date(j));
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int i2 = calendarM622k.get(1) << 16;
        int i3 = calendarM622k.get(2);
        int i4 = i2 + (i3 << 8);
        int i5 = calendarM622k.get(5);
        if (i4 + i5 != i) {
            stringBufferM1217h.append(Utils.zeroPad(i5)).append('/').append(Utils.zeroPad(i3 + 1)).append(' ');
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h.append(Utils.zeroPad(calendarM622k.get(11))).append(':').append(Utils.zeroPad(calendarM622k.get(12))));
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
        AppController.f152f = true;
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
