package com.trykote.mobileagent.protocol.xmpp;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
public final class XmppContact extends Contact {

    // Icon IDs
    private static final int ICON_DEFAULT = 381;
    private static final int ICON_BLINK_FLAG = 16384;
    private static final int ICON_SUBSCRIPTION_PENDING = 384;
    private static final int ICON_MAILRU_OFFSET = 4;

    // Bit masks
    private static final int MASK_LOWER_16 = 65535;
    private static final int FLAG_SUBSCRIPTION_TO = 20578304;

    // XMPP presence status codes
    private static final int PRESENCE_OFFLINE = 0;
    private static final int PRESENCE_ONLINE = 1;
    private static final int PRESENCE_AWAY = 2;
    private static final int PRESENCE_INVISIBLE = 3;
    private static final int PRESENCE_CHAT = 4;
    private static final int PRESENCE_DND = 5;
    private static final int PRESENCE_XA = 6;

    // Unread count for online contacts
    private static final int UNREAD_COUNT_ONLINE = 3;

    // Presence subscription feature IDs
    private static final int FEATURE_SUBSCRIBE = 1;
    private static final int FEATURE_UNSUBSCRIBE = 0;

    public String jabberId;

    private int status;

    private int unreadCount;

    private String statusMessage;

    private String vCardHash;

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
    public final void clearUnread() {
        this.status = PRESENCE_OFFLINE;
        this.defaultIcon = ICON_DEFAULT;
        this.statusMessage = null;
        this.vCardHash = null;
        this.unreadCount = 0;
        super.clearUnread();
    }

    @Override // p000.Contact
    public final String getIdentifier() {
        return this.jabberId;
    }

    public XmppContact(Account account, ByteBuffer buffer) {
        super(account);
        this.jabberId = buffer.readWideStr();
        setDisplayName(buffer.readUTF8Str((String) null));
        this.identifier = account.encodeId().writeRawString(this.jabberId).getStringAndClear();
        this.status = PRESENCE_OFFLINE;
        this.defaultIcon = XmppProtocol.getIconForError(PRESENCE_OFFLINE);
        account.registerContact(this);
        updateRenderState();
        this.extra = this.jabberId;
    }

    @Override // p000.Contact
    public final void deserialize(ByteBuffer buffer) {
        buffer.writeStringLatin1(this.jabberId).writeStringUTF16(this.displayName);
    }

    private final int getDisplayIcon() {
        int icon = getIcon();
        int i = icon & MASK_LOWER_16;
        return (!(this.account instanceof XmppMailRuProtocol) || i < ICON_DEFAULT || i > ICON_SUBSCRIPTION_PENDING) ? icon : icon + ICON_MAILRU_OFFSET;
    }

    @Override // p000.Contact
    public final MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier).setIcon(getDisplayIcon()).addText(this.displayName, 0, this.unreadCount);
        menuItem.data = this;
        return menuItem;
    }

    @Override
    public final int getIcon() {
        int icon = super.getIcon();
        if (icon == ICON_BLINK_FLAG) {
            return icon;
        }
        if (this.online) {
            return this.defaultIcon;
        }
        int i = icon;
        if (StringUtils.matchesKey(PackedStringKeys.ATTR_FROM, this.statusMessage) || StringUtils.matchesKey(PackedStringKeys.VALUE_NONE, this.statusMessage) || StringUtils.matchesKey(PackedStringKeys.ATTR_ASK, this.statusMessage)) {
            i = ICON_SUBSCRIPTION_PENDING;
        }
        if (StringUtils.matchesKey(PackedStringKeys.ATTR_TO, this.statusMessage)) {
            i = (i & MASK_LOWER_16) | FLAG_SUBSCRIPTION_TO;
        }
        return i;
    }

    @Override // p000.Contact
    public final boolean canDelete() {
        return false;
    }

    @Override // p000.Contact
    public final boolean canBlock() {
        return false;
    }

    @Override // p000.Contact
    public final boolean canUnblock() {
        return false;
    }

    @Override // p000.Contact
    public final boolean isOnline() {
        return this.online;
    }

    @Override // p000.Contact
    public final boolean hasUnread() {
        return StringUtils.matchesKey(PackedStringKeys.VALUE_NONE, this.statusMessage) || StringUtils.matchesKey(PackedStringKeys.ATTR_FROM, this.statusMessage);
    }

    @Override // p000.Contact
    public final void performAction() {
    }
    public final void updateFromPresence(String str, XmlElement element) {
        int i = PRESENCE_OFFLINE;
        this.vCardHash = str;
        this.status = PRESENCE_OFFLINE;
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_AVAILABLE, str)) {
            i = PRESENCE_ONLINE;
            XmlElement showChild = element.findChildByKey(PackedStringKeys.TAG_SHOW);
            if (showChild != null) {
                String statusText = StringUtils.fromBuffer(showChild.textContent);
                if (!StringUtils.isEmpty(statusText)) {
                    if (StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_CHAT, statusText)) {
                        i = PRESENCE_CHAT;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_AWAY, statusText)) {
                        i = PRESENCE_AWAY;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_XA, statusText)) {
                        i = PRESENCE_XA;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_DND, statusText)) {
                        i = PRESENCE_DND;
                    } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_INV, statusText)) {
                        i = PRESENCE_INVISIBLE;
                    }
                }
            }
            this.status = i;
        }
        updateFromContact(this);
    }

    public final void markOnlineIfOffline() {
        if (this.status == PRESENCE_OFFLINE) {
            this.status = PRESENCE_ONLINE;
            updateFromContact(this);
        }
    }

    public final void updateFromContact(XmppContact other) {
        this.status = other != null ? other.status : PRESENCE_OFFLINE;
        this.vCardHash = other != null ? other.vCardHash : null;
        this.online = this.status != PRESENCE_OFFLINE;
        this.highlighted = this.online;
        this.defaultIcon = XmppProtocol.getIconForError(this.status);
        this.unreadCount = this.online ? UNREAD_COUNT_ONLINE : 0;
        this.dirty = true;
        updateRenderState();
    }

    @Override // p000.Contact
    public final void clearRegistrationData() {
        RegistrationState.clearParam2();
    }

    public final int sendPresence(int i) {
        int result = ((XmppProtocol) this.account).updateContactPresence(this, i);
        if (result != i) {
            return result;
        }
        setPresenceFeature(FEATURE_SUBSCRIBE);
        setPresenceFeature(FEATURE_UNSUBSCRIBE);
        return i;
    }

    public final void setPresenceFeature(int i) {
        ((XmppProtocol) this.account).updatePresenceStatus(this.jabberId, i);
    }

    public final ContactInfo getContactInfo() {
        return new ContactInfo(this).setImageWidth(getDisplayIcon());
    }
}
