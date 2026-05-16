package com.trykote.mobileagent.protocol.xmpp;

import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;
import com.trykote.mobileagent.util.XmlElement;
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

    public XmppContact(XmppProtocol protocol, String jid, String displayName, String statusMessage) {
        super(protocol);
        this.extra = jid;
        this.jabberId = jid;
        this.statusMessage = statusMessage;
        this.unreadCount = 0;
        setDisplayName(Utils.defaultIfBlank(displayName, jid));
        this.defaultIcon = XmppProtocol.getIconForError(this.status);
        this.identifier = protocol.encodeId().writeRawString(jid).getStringAndClear();
        protocol.registerContact(this);
        updateRenderState();
    }

    public XmppContact(Account account, ByteBuffer buffer) {
        super(account);
        this.jabberId = buffer.readWideStr();
        setDisplayName(buffer.readUTF8Str(null));
        this.identifier = account.encodeId().writeRawString(this.jabberId).getStringAndClear();
        this.status = PRESENCE_OFFLINE;
        this.defaultIcon = XmppProtocol.getIconForError(PRESENCE_OFFLINE);
        account.registerContact(this);
        updateRenderState();
        this.extra = this.jabberId;
    }

    @Override
    public void clearUnread() {
        this.status = PRESENCE_OFFLINE;
        this.defaultIcon = ICON_DEFAULT;
        this.statusMessage = null;
        this.vCardHash = null;
        this.unreadCount = 0;
        super.clearUnread();
    }

    @Override
    public String getIdentifier() {
        return this.jabberId;
    }

    @Override
    public void serialize(ByteBuffer buffer) {
        buffer.writeStringLatin1(this.jabberId).writeStringUTF16(this.displayName);
    }

    @Override
    public int getDisplayIcon() {
        int icon = getIcon();
        int baseIcon = icon & MASK_LOWER_16;
        if (this.account instanceof XmppMailRuProtocol
                && baseIcon >= ICON_DEFAULT && baseIcon <= ICON_SUBSCRIPTION_PENDING) {
            return icon + ICON_MAILRU_OFFSET;
        }
        return icon;
    }

    @Override
    public MenuItem createMenuItem() {
        MenuItem menuItem = MenuItem.create(this.identifier)
                .setIcon(getDisplayIcon())
                .addText(this.displayName, 0, this.unreadCount);
        menuItem.data = this;
        return menuItem;
    }

    @Override
    public int getIcon() {
        int icon = super.getIcon();
        if (icon == ICON_BLINK_FLAG) {
            return icon;
        }
        if (this.online) {
            return this.defaultIcon;
        }
        int subscriptionIcon = icon;
        if (StringUtils.matchesKey(PackedStringKeys.ATTR_FROM, this.statusMessage)
                || StringUtils.matchesKey(PackedStringKeys.VALUE_NONE, this.statusMessage)
                || StringUtils.matchesKey(PackedStringKeys.ATTR_ASK, this.statusMessage)) {
            subscriptionIcon = ICON_SUBSCRIPTION_PENDING;
        }
        if (StringUtils.matchesKey(PackedStringKeys.ATTR_TO, this.statusMessage)) {
            subscriptionIcon = (subscriptionIcon & MASK_LOWER_16) | FLAG_SUBSCRIPTION_TO;
        }
        return subscriptionIcon;
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public boolean canBlock() {
        return false;
    }

    @Override
    public boolean canUnblock() {
        return false;
    }

    @Override
    public boolean isOnline() {
        return this.online;
    }

    @Override
    public boolean hasUnread() {
        return StringUtils.matchesKey(PackedStringKeys.VALUE_NONE, this.statusMessage)
                || StringUtils.matchesKey(PackedStringKeys.ATTR_FROM, this.statusMessage);
    }

    @Override
    public void performAction() {
    }

    @Override
    public String getContactEmail() {
        return this.jabberId;
    }

    @Override
    public boolean canSubscribe() {
        return true;
    }

    @Override
    public int subscribe(int subscriptionType) {
        return sendPresence(subscriptionType);
    }

    @Override
    public int getEmoticonBase() {
        return StringResKeys.XMPP_EMOTICONS_BASE;
    }

    @Override
    public void populateContactInfo(Object contactInfo) {
        ((ContactInfo) contactInfo).setXmppId(this.jabberId);
    }

    @Override
    public boolean isEditable() {
        return !((XmppProtocol) this.account).isMailRuVariant();
    }

    public void updateFromPresence(String presenceType, XmlElement element) {
        this.vCardHash = presenceType;
        this.status = PRESENCE_OFFLINE;
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_AVAILABLE, presenceType)) {
            this.status = parseShowStatus(element);
        }
        updateFromContact(this);
    }

    private int parseShowStatus(XmlElement presenceElement) {
        XmlElement showChild = presenceElement.findChildByKey(PackedStringKeys.TAG_SHOW);
        if (showChild == null) {
            return PRESENCE_ONLINE;
        }
        String showText = StringUtils.fromBuffer(showChild.textContent);
        if (StringUtils.isEmpty(showText)) {
            return PRESENCE_ONLINE;
        }
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_CHAT, showText)) {
            return PRESENCE_CHAT;
        }
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_AWAY, showText)) {
            return PRESENCE_AWAY;
        }
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_XA, showText)) {
            return PRESENCE_XA;
        }
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_DND, showText)) {
            return PRESENCE_DND;
        }
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STATUS_INV, showText)) {
            return PRESENCE_INVISIBLE;
        }
        return PRESENCE_ONLINE;
    }

    public void markOnlineIfOffline() {
        if (this.status == PRESENCE_OFFLINE) {
            this.status = PRESENCE_ONLINE;
            updateFromContact(this);
        }
    }

    public void updateFromContact(XmppContact other) {
        this.status = other != null ? other.status : PRESENCE_OFFLINE;
        this.vCardHash = other != null ? other.vCardHash : null;
        this.online = this.status != PRESENCE_OFFLINE;
        this.highlighted = this.online;
        this.defaultIcon = XmppProtocol.getIconForError(this.status);
        this.unreadCount = this.online ? UNREAD_COUNT_ONLINE : 0;
        this.dirty = true;
        updateRenderState();
    }

    @Override
    public void clearRegistrationData() {
        RegistrationState.clearParam2();
    }

    public int sendPresence(int subscriptionType) {
        int result = ((XmppProtocol) this.account).updateContactPresence(this, subscriptionType);
        if (result != subscriptionType) {
            return result;
        }
        setPresenceFeature(FEATURE_SUBSCRIBE);
        setPresenceFeature(FEATURE_UNSUBSCRIBE);
        return subscriptionType;
    }

    public void setPresenceFeature(int featureId) {
        ((XmppProtocol) this.account).updatePresenceStatus(this.jabberId, featureId);
    }

    public ContactInfo getContactInfo() {
        return new ContactInfo(this).setImageWidth(getDisplayIcon());
    }
}
