package com.trykote.mobileagent.protocol.xmpp;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
/* renamed from: af */
/* loaded from: MobileAgent_3.9.jar:af.class */
public final class XmppContact extends Contact {

    /* renamed from: a */
    public String jabberId;

    /* renamed from: c */
    private int status;

    /* renamed from: d */
    private int unreadCount;

    /* renamed from: e */
    private String statusMessage;

    /* renamed from: f */
    private String vCardHash;

    /* renamed from: b */
    public boolean online;

    public XmppContact(XmppProtocol protocol, String str, String str2, String str3) {
        super(protocol);
        this.extra = str;
        this.jabberId = str;
        this.statusMessage = str3;
        this.unreadCount = 0;
        setDisplayName(Utils.defaultIfBlank(str2, str));
        this.defaultIcon = XmppProtocol.getIconForError(this.status);
        this.identifier = protocol.encodeId().writeRawString(str).getStringAndClear();
        protocol.registerContact(this);
        updateRenderState();
    }

    @Override // p000.Contact
    /* renamed from: c */
    public final void clearUnread() {
        this.status = 0;
        this.defaultIcon = 381;
        this.statusMessage = null;
        this.vCardHash = null;
        this.unreadCount = 0;
        super.clearUnread();
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final String getIdentifier() {
        return this.jabberId;
    }

    public XmppContact(Account account, ByteBuffer buffer) {
        super(account);
        this.jabberId = buffer.readWideStr();
        setDisplayName(buffer.readUTF8Str((String) null));
        this.identifier = account.encodeId().writeRawString(this.jabberId).getStringAndClear();
        this.status = 0;
        this.defaultIcon = XmppProtocol.getIconForError(0);
        account.registerContact(this);
        updateRenderState();
        this.extra = this.jabberId;
    }

    @Override // p000.Contact
    /* renamed from: a */
    public final void deserialize(ByteBuffer buffer) {
        buffer.writeStringLatin1(this.jabberId).writeStringUTF16(this.displayName);
    }

    /* renamed from: o */
    private final int getDisplayIcon() {
        int icon = getIcon();
        int i = icon & 65535;
        return (!(this.account instanceof XmppMailRuProtocol) || i < 381 || i > 384) ? icon : icon + 4;
    }

    @Override // p000.Contact
    /* renamed from: b */
    public final MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier).setIcon(getDisplayIcon()).addText(this.displayName, 0, this.unreadCount);
        menuItem.data = this;
        return menuItem;
    }

    @Override // p000.Contact
    /* renamed from: e */
    public final int getIcon() {
        int icon = super.getIcon();
        int i = icon;
        if (icon == 16384) {
            return i;
        }
        if (StringUtils.matchesKey(PackedStringKeys.ATTR_FROM, this.statusMessage) || StringUtils.matchesKey(PackedStringKeys.VALUE_NONE, this.statusMessage) || StringUtils.matchesKey(PackedStringKeys.ATTR_ASK, this.statusMessage)) {
            i = 384;
        }
        if (StringUtils.matchesKey(PackedStringKeys.ATTR_TO, this.statusMessage)) {
            i = (i & 65535) | 20578304;
        }
        return i;
    }

    @Override // p000.Contact
    /* renamed from: i */
    public final boolean canDelete() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: j */
    public final boolean canBlock() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: k */
    public final boolean canUnblock() {
        return false;
    }

    @Override // p000.Contact
    /* renamed from: m */
    public final boolean isOnline() {
        return this.online;
    }

    @Override // p000.Contact
    /* renamed from: l */
    public final boolean hasUnread() {
        return StringUtils.matchesKey(PackedStringKeys.VALUE_NONE, this.statusMessage) || StringUtils.matchesKey(PackedStringKeys.ATTR_FROM, this.statusMessage);
    }

    @Override // p000.Contact
    /* renamed from: h */
    public final void performAction() {
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0070  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void updateFromPresence(String str, XmlElement element) {
        int i = 0;
        this.vCardHash = str;
        this.status = 0;
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_AVAILABLE, str)) {
            XmlElement showChild = element.findChildByKey(PackedStringKeys.TAG_SHOW);
            if (showChild != null) {
                String statusText = StringUtils.fromBuffer(showChild.textContent);
                if (StringUtils.isEmpty(statusText)) {
                    i = 1;
                    this.status = i;
                } else {
                    if (StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_CHAT, statusText)) {
                        i = 4;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_AWAY, statusText)) {
                        i = 2;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_XA, statusText)) {
                        i = 6;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_DND, statusText)) {
                        i = 5;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_INV, statusText)) {
                        i = 3;
                    }
                    this.status = i;
                }
            }
        }
        updateFromContact(this);
    }

    /* renamed from: a */
    public final void updateFromContact(XmppContact other) {
        this.status = other != null ? other.status : 0;
        this.vCardHash = other != null ? other.vCardHash : null;
        this.highlighted = this.status != 0;
        this.defaultIcon = XmppProtocol.getIconForError(this.status);
        this.unreadCount = this.status == 0 ? 0 : 3;
        this.dirty = true;
        updateRenderState();
    }

    @Override // p000.Contact
    /* renamed from: L */
    public final void mo148L() {
        AppState.clearIndex(StateKeys.SLOT_REG_PARAM_2);
    }

    /* renamed from: a */
    public final int sendPresence(int i) {
        int result = ((XmppProtocol) this.account).updateContactPresence(this, i);
        if (result != i) {
            return result;
        }
        setPresenceFeature(1);
        setPresenceFeature(0);
        return i;
    }

    /* renamed from: b */
    public final void setPresenceFeature(int i) {
        ((XmppProtocol) this.account).updatePresenceStatus(this.jabberId, i);
    }

    /* JADX DEBUG: Possible override for method l.f()Ln; */
    /* renamed from: f */
    public final ContactInfo getContactInfo() {
        return new ContactInfo(this).setImageWidth(getDisplayIcon());
    }
}
