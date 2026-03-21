package p000;

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
        this.messageIds = NetworkUtils.newVector();
        this.readMessages = NetworkUtils.newVector();
        this.messages = new Hashtable();
        this.isActive = true;
    }

    public ChatRoom(Object obj) {
        this();
        parseJson(obj);
        this.isActive = true;
    }

    /* renamed from: a */
    public final void serialize(ByteBuffer c0043n) {
        c0043n.writeStringUTF16(this.name).writeIntLE(this.memberCount).writeIntLE(this.id).writeIntLE(this.unreadCount).writeStringLatin1(this.subject);
        if (this.messageIds.size() > 20) {
            this.messageIds.setSize(20);
        }
        int size = this.messageIds.size();
        c0043n.writeIntLE(size);
        for (int i = 0; i < size; i++) {
            String strM521a = Utils.getVectorString(this.messageIds, i);
            c0043n.writeStringLatin1(strM521a);
            Message c0026azM1415b = getMessage(strM521a);
            c0043n.writeLong(c0026azM1415b.timestamp);
            XmppMailRuProtocol.writeAddressPairs(c0026azM1415b.toList, c0043n);
            XmppMailRuProtocol.writeAddressPairs(c0026azM1415b.ccList, c0043n);
            c0043n.writeIntLE(c0026azM1415b.priority).writeIntLE(c0026azM1415b.flags).writeStringUTF16(Utils.defaultStr(c0026azM1415b.subject));
            if (c0026azM1415b.body == null || c0026azM1415b.body.length() > 3072) {
                c0043n.writeIntLE(0).writeIntLE(0);
            } else {
                c0043n.writeIntLE(1).writeStringUTF16(c0026azM1415b.body).writeIntLE(1);
                Object[] objArr = c0026azM1415b.attachments;
                if (objArr == null) {
                    c0043n.writeIntLE(0);
                } else {
                    c0043n.writeIntLE(objArr.length);
                    for (Object obj : objArr) {
                        String[] strArr = (String[]) obj;
                        for (int i2 = 0; i2 < 6; i2++) {
                            c0043n.writeStringUTF16(strArr[i2]);
                        }
                    }
                }
            }
            c0026azM1415b.toList = null;
            c0026azM1415b.ccList = null;
            c0026azM1415b.subject = null;
            c0026azM1415b.body = null;
            c0026azM1415b.attachments = null;
        }
    }

    /* renamed from: b */
    public static final ChatRoom deserialize(ByteBuffer c0043n) {
        ChatRoom c0052w = new ChatRoom();
        c0052w.name = c0043n.readUTF8Str((String) null);
        c0052w.memberCount = c0043n.readInt();
        c0052w.id = c0043n.readInt();
        c0052w.unreadCount = c0043n.readInt();
        c0052w.subject = c0043n.readWideStr();
        int iM1328e = c0043n.readInt();
        for (int i = 0; i < iM1328e; i++) {
            Vector vector = c0052w.messageIds;
            String strM1334g = c0043n.readWideStr();
            vector.addElement(strM1334g);
            c0052w.messages.put(strM1334g, new Message(c0043n, strM1334g));
        }
        return c0052w;
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
        this.messageIds = NetworkUtils.newVector();
        this.readMessages = NetworkUtils.newVector();
        this.messages = new Hashtable();
        this.metadata = new Hashtable();
        this.participants = NetworkUtils.newVector();
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
        String strM1413g = getFormattedName();
        if (StringUtils.matchesKey(896, strM1413g) || StringUtils.matchesKey(900, strM1413g)) {
            return 1;
        }
        return (StringUtils.matchesKey(898, strM1413g) || StringUtils.matchesKey(899, strM1413g)) ? 2 : 3;
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
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(getFormattedName()).append(' ').append('[').append(this.unreadCount).append('/').append(this.memberCount).append(']'));
    }
}
