package com.trykote.mobileagent.protocol.xmpp;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public class XmppProtocol extends Account {

    // XMPP progress states
    public static final int PROGRESS_RESOLVING = 2;
    public static final int PROGRESS_CONNECTING = 3;
    public static final int PROGRESS_OPENING_STREAM = 4;
    public static final int PROGRESS_PROCESSING = 5;

    // XMPP status modes
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_BUSY = 2;
    public static final int STATUS_XA = 3;
    public static final int STATUS_AWAY = 4;
    public static final int STATUS_DND = 5;
    public static final int STATUS_CUSTOM = 6;

    // Character constants
    private static final char CHAR_AT = '@';
    private static final char CHAR_SLASH = '/';

    // Hash prefix length for auth token
    private static final int AUTH_HASH_PREFIX_LENGTH = 16;

    // XMPP icon IDs
    private static final int ICON_DISCONNECTED = 381;
    private static final int ICON_CONNECTING = 382;
    private static final int ICON_ONLINE = 383;
    private static final int ICON_BUSY = 16318847;
    private static final int ICON_XA = 16515455;
    private static final int ICON_AWAY = 16384383;
    private static final int ICON_DND = 16449919;
    private static final int ICON_CUSTOM = 16580991;

    // Presence status string resource IDs
    private static final int STATUS_STR_ONLINE = 642;
    private static final int STATUS_STR_AWAY = 643;
    private static final int STATUS_STR_BUSY = 644;
    private static final int STATUS_STR_DND = 645;
    private static final int STATUS_STR_CUSTOM = 648;

    // XMPP connection timeouts (millis)
    private static final long TIMEOUT_WIFI = 25000L;
    private static final long TIMEOUT_CELLULAR = 60000L;

    public final Vector elementQueue;

    private Object[] parserState;

    private Object[] authState;

    public String serverAddress;

    public int serverPort;

    public Object authResult;

    private Throwable lastException;

    public String serverResourceId;

    public XmppProtocol(int i, String str, String str2) {
        super(i, str, str2);
        this.configFlags = 1;
        this.elementQueue = ObjectPool.newVector();
        XmppContactGroup defaultGrp = new XmppContactGroup(this, 0, ResourceAccessor.str(StringResKeys.STR_GROUP_DEFAULT));
        defaultGrp.isSpecial = true;
        this.defaultGroup = defaultGrp;
        this.serverAddress = AppState.emptyStr;
        this.serverResourceId = AppState.emptyStr;
    }

    @Override // p000.Account
    public int getType() {
        return TYPE_XMPP;
    }

    private String generateMessageId() {
        StringBuffer sb = ObjectPool.newStringBuffer().append('m');
        int i = this.state + 1;
        this.state = i;
        return ObjectPool.toStringAndRelease(sb.append(i));
    }

    public XmppProtocol(ByteBuffer buffer) {
        super(buffer);
        this.elementQueue = ObjectPool.newVector();
        int groupCount = buffer.readInt();
        for (int i = groupCount - 1; i >= 0; i--) {
            addGroup((ContactGroup) new XmppContactGroup(this, buffer));
        }
        XmppContactGroup defaultGrp = new XmppContactGroup(this, buffer);
        for (int i = Utils.vectorSize(defaultGrp.contacts) - 1; i >= 0; i--) {
            ((XmppContact) defaultGrp.contacts.elementAt(i)).online = true;
        }
        defaultGrp.isSpecial = true;
        this.defaultGroup = defaultGrp;
        this.serverAddress = buffer.readWideStr();
        this.serverPort = buffer.readShortBE();
        this.serverResourceId = buffer.readWideStr();
    }

    @Override // p000.Account
    public final Account serializeAccount(ByteBuffer buffer, boolean z, boolean z2) {
        super.serializeAccount(buffer, z, z2);
        buffer.writeStringLatin1(this.serverAddress).writeShortBE(this.serverPort).writeStringLatin1(this.serverResourceId);
        return this;
    }

    public boolean isMailRuVariant() {
        return false;
    }

    public String getStreamDomain() {
        return StringUtils.getDomain(this.login);
    }

    @Override // p000.Account
    public final ContactGroup createOnlineGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override // p000.Account
    public final ContactGroup createBlockedGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_TEMPORARY));
    }

    @Override // p000.Account
    public final ContactGroup createOfflineGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_IGNORE));
    }

    @Override // p000.Account
    public final ContactGroup createSpecialGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_PHONE_CONTACTS));
    }

    @Override // p000.Account
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        return 0;
    }

    private final int sendRawBytes(byte[] bArr) {
        long j = UIState.isWifiConnection() ? TIMEOUT_WIFI : TIMEOUT_CELLULAR;
        this.timeout = j;
        this.deadline = System.currentTimeMillis() + j;
        return sendData(new ByteBuffer().writeBytes(bArr));
    }

    private int sendXmlElement(XmlElement element) {
        return sendData(new ByteBuffer().writeUTFNoLen(element.toString()));
    }

    private int sendElementWithId(XmlElement element) {
        return sendXmlElement(element.setAttrValue(131550, generateMessageId()));
    }

    private final void clearParserState() {
        this.dataBuffer.clear();
        Object[] state = this.parserState;
        if (state != null) {
            state[2] = null;
            state[1] = null;
            state[0] = null;
        }
    }

    public final void setException(Throwable th) {
        if (this.progress == PROGRESS_RESOLVING) {
            this.lastException = th;
        }
    }

    public final void setAuthParameters(String str, int i) {
        if (this.progress == PROGRESS_RESOLVING) {
            this.serverPort = i;
            this.serverAddress = str;
        }
    }

    private final boolean isMailRuXmpp() {
        return getType() == TYPE_XMPP && this.login.endsWith(ResourceAccessor.str(PackedStringKeys.DOMAIN_GMAIL_COM));
    }

    @Override // p000.Account
    public final void loadData() throws Throwable {
        boolean handledPing;
        boolean handledSession;
        boolean handledBind;
        boolean handledDiscoInfo;
        XmlElement rosterElement;
        String bodyText;
        String subjectText;
        Object[] taskArgs;
        switch (this.progress) {
            case PROGRESS_DISCONNECTED:
                clearParserState();
                Object[] prevAuthState = this.authState;
                if (prevAuthState != null) {
                    prevAuthState[0] = null;
                }
                this.authState = null;
                this.lastException = null;
                this.msgCount = 0;
                break;
            case PROGRESS_STARTING:
                RemoteLogger.log("XMPP", "progress STARTING, login=" + this.login + " isMailRu=" + isMailRuVariant());
                this.msgCount = 10;
                if (isMailRuVariant()) {
                    if (Utils.nonEmpty(this.serverResourceId)) {
                        this.progress = PROGRESS_CONNECTING;
                    } else {
                        this.progress = PROGRESS_RESOLVING;
                        if (this.login.indexOf(CHAR_AT) <= 0) {
                            ((XmppMailRuProtocol) this).serverResourceId = this.login;
                            taskArgs = null;
                        } else {
                            String hashPrefix = StringUtils.prefix(Utils.generateRandomHash(), AUTH_HASH_PREFIX_LENGTH);
                            Object[] authArgs = {this, hashPrefix, new ByteBuffer().writeCompressed(PackedStringKeys.URL_VK_AUTH_TOKEN_SECURE).writeRawString(hashPrefix).readAllByteStr(), ObjectPool.integerOf(0), this.login, this.password};
                            new AsyncTask(AsyncTaskId.XMPP_HTTP_AUTH, authArgs);
                            taskArgs = authArgs;
                        }
                        this.authState = taskArgs;
                    }
                } else if (Utils.nonEmpty(this.serverAddress)) {
                    this.progress = PROGRESS_CONNECTING;
                } else {
                    this.progress = PROGRESS_RESOLVING;
                    Object[] resolveArgs = {this};
                    new AsyncTask(AsyncTaskId.RESOLVE_XMPP_SERVER, resolveArgs);
                    this.authState = resolveArgs;
                }
                AppController.needsRepaint = true;
                break;
            case PROGRESS_RESOLVING:
                this.msgCount = 20;
                if (isMailRuVariant()) {
                    if (Utils.nonEmpty(this.serverResourceId)) {
                        this.progress = PROGRESS_CONNECTING;
                    } else if (this.lastException != null) {
                        handleException(this.lastException);
                    }
                } else if (Utils.nonEmpty(this.serverAddress)) {
                    RemoteLogger.log("XMPP", "RESOLVING done -> CONNECTING, addr=" + this.serverAddress + ":" + this.serverPort);
                    this.progress = PROGRESS_CONNECTING;
                } else if (this.lastException != null) {
                    RemoteLogger.log("XMPP", "RESOLVING failed: " + this.lastException);
                    handleException(this.lastException);
                } else {
                    RemoteLogger.log("XMPP", "RESOLVING: still waiting, serverAddress=" + this.serverAddress + " exception=" + this.lastException);
                }
                AppController.needsRepaint = true;
                break;
            case PROGRESS_CONNECTING:
                this.msgCount = 30;
                this.state = 0;
                String connAddr = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(this.serverAddress).append(':').append(this.serverPort));
                RemoteLogger.log("XMPP", "progress CONNECTING to " + connAddr);
                this.connection = new ConnectionThread(connAddr);
                this.progress = PROGRESS_OPENING_STREAM;
                AppController.needsRepaint = true;
                break;
            case PROGRESS_OPENING_STREAM:
                clearParserState();
                this.msgCount = 40;
                if (!isMailRuXmpp()) {
                    if (this.connection.getState() == ConnectionThread.STATE_CONNECTED) {
                        RemoteLogger.log("XMPP", "stream connected, opening XMPP stream");
                        this.msgCount = 50;
                        this.progress = PROGRESS_PROCESSING;
                        Object[] parserArgs = new Object[3];
                        parserArgs[0] = this;
                        parserArgs[1] = new ByteBuffer();
                        parserArgs[2] = null;
                        parserArgs[2] = new XmlParser(parserArgs);
                        new AsyncTask(AsyncTaskId.PROCESS_XMPP_STREAM, parserArgs);
                        this.parserState = parserArgs;
                        AppController.needsRepaint = true;
                        sendStreamHeader();
                    } else if (this.connection.getState() <= ConnectionThread.STATE_CLOSED) {
                        closeConnection();
                    }
                    AppController.needsRepaint = true;
                    break;
                } else if (this.authResult != null) {
                    if (this.authResult instanceof Throwable) {
                        handleConnError();
                        break;
                    }
                }
                break;
            default:
                this.connection.drainInput(this.dataBuffer);
                AccountManager.recordInboundTraffic(this, this.dataBuffer.length);
                Object[] state = this.parserState;
                ByteBuffer inputBuffer = this.dataBuffer;
                ByteBuffer parserBuffer = (ByteBuffer) state[1];
                synchronized (parserBuffer) {
                    parserBuffer.writeBytesAt(inputBuffer.data, inputBuffer.offset, inputBuffer.length);
                    inputBuffer.clear();
                }
                XmlElement element = (XmlElement) Utils.dequeue(this.elementQueue);
                if (element != null) {
                    String tagName = element.tagName;
                    if (!StringUtils.matchesKey(PackedStringKeys.XMPP_STREAM_STREAM, tagName)) {
                        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STREAM_FEATURES, tagName)) {
                            XmlElement mechanisms = element.findByName(ResourceAccessor.str(PackedStringKeys.XMPP_MECHANISMS));
                            if (mechanisms != null) {
                                XmlElement authElement = XmlElement.createFromState(263757).addIdAttr(2102710);
                                String plainMech = ResourceAccessor.str(PackedStringKeys.AUTH_DIGEST_MD5);
                                if (mechanisms.findChildByText(plainMech) != null) {
                                    sendXmlElement(authElement.setAttrValue(594936, plainMech));
                                } else {
                                    String xTokenMech = ResourceAccessor.str(PackedStringKeys.HEADER_X_GOOGLE_TOKEN);
                                    if (mechanisms.findChildByText(xTokenMech) != null) {
                                        sendXmlElement(authElement.setAttrValue(594936, xTokenMech).appendText((Object) new ByteBuffer().writeByte(0).writeRawString(this.shortName).writeByte(0).writeRawString((String) this.authResult).toBase64()));
                                    } else {
                                        String digestMech = ResourceAccessor.str(PackedStringKeys.AUTH_MECHANISM_PLAIN);
                                        if (mechanisms.findChildByText(digestMech) != null) {
                                            sendXmlElement(authElement.setAttrValue(594936, digestMech).appendText((Object) new ByteBuffer().writeUTFNoLen(new ByteBuffer().writeRawString(this.shortName).writeByte(64).writeRawString(this.serverAddress).readAllByteStr()).writeByte(0).writeUTFNoLen(this.shortName).writeByte(0).writeUTFNoLen(this.password).toBase64()));
                                        }
                                    }
                                }
                            } else if (element.findByName(ResourceAccessor.str(PackedStringKeys.XMPP_BIND)) != null) {
                                XmlElement bindRequest = XmlElement.createFromState(136604).addNameAttr(198841);
                                bindRequest.addChildWithId(267762, 2102742).addTextChild(ResourceAccessor.str(PackedStringKeys.XMPP_RESOURCE), ResourceAccessor.str(PackedStringKeys.PLATFORM_J2ME));
                                sendElementWithId(bindRequest);
                                this.msgCount = 60;
                            } else {
                                EventDispatcher.postAccountError(this, 1033);
                                closeConnection();
                                this.lastError = getDefaultError();
                            }
                        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_CHALLENGE, tagName)) {
                            XmlElement challengeResponse = XmlElement.createFromState(529537).addIdAttr(2102710);
                            String decoded = Base64.decode(StringUtils.fromBuffer(element.textContent)).getStringAndClear();
                            int idx = decoded.indexOf(ResourceAccessor.str(PackedStringKeys.DIGEST_NONCE_EQ));
                            if (idx >= 0) {
                                int nonceStart = idx + 7;
                                String username = getAuthUsername();
                                String password = this.password;
                                String realm = this.serverAddress;
                                String nonce = StringUtils.substring(decoded, nonceStart, decoded.indexOf(34, nonceStart));
                                ByteBuffer digestBuffer = new ByteBuffer().writeCompressed(PackedStringKeys.DIGEST_USERNAME).writeRawString(username).writeCompressed(PackedStringKeys.DIGEST_REALM).writeRawString(realm).writeCompressed(PackedStringKeys.DIGEST_NONCE).writeRawString(nonce).writeCompressed(PackedStringKeys.DIGEST_NC_CNONCE);
                                String cnonce = Utils.generateRandomHash();
                                challengeResponse.appendText((Object) digestBuffer.writeRawString(cnonce).writeCompressed(PackedStringKeys.DIGEST_QOP_AUTH).writeRawString(realm).writeCompressed(PackedStringKeys.DIGEST_RESPONSE).writeRawString(new ByteBuffer().writeRawString(new ByteBuffer().writeRawString(username).writeByte(58).writeRawString(realm).writeByte(58).writeRawString(password).encryptMD5().writeByte(58).writeRawString(nonce).writeByte(58).writeRawString(cnonce).encryptMD5().toHexString()).writeByte(58).writeRawString(nonce).writeCompressed(PackedStringKeys.DIGEST_NC).writeRawString(cnonce).writeByte(58).writeCompressed(PackedStringKeys.TAG_AUTH).writeByte(58).writeRawString(new ByteBuffer().writeCompressed(PackedStringKeys.DIGEST_AUTH_XMPP).writeRawString(realm).encryptMD5().toHexString()).encryptMD5().toHexString()).writeCompressed(PackedStringKeys.DIGEST_CHARSET).toBase64());
                            }
                            sendXmlElement(challengeResponse);
                        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_SUCCESS, tagName)) {
                            sendStreamHeader();
                        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_PRESENCE, tagName)) {
                            String nameAttr = element.getNameAttr();
                            String presenceType = nameAttr != null ? nameAttr : ResourceAccessor.str(PackedStringKeys.XMPP_STATUS_AVAILABLE);
                            String jid = extractBareJid(element.getIntAttribute(PackedStringKeys.ATTR_FROM));
                            if (jid != null) {
                                XmppContact contact = findContactByJid(jid);
                                if (StringUtils.matchesKey(PackedStringKeys.XMPP_SUBSCRIBE, presenceType)) {
                                    if (contact == null) {
                                        ContactGroup group = this.defaultGroup;
                                        XmppContact newContact = new XmppContact(this, jid, extractDisplayName(element, jid), null);
                                        newContact.online = true;
                                        contact = newContact;
                                        group.addContact((Object) newContact);
                                    }
                                    contact.updateFromPresence(presenceType, element);
                                    NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CONVERSATION_MESSAGE);
                                    onMessage(jid, 0L, ResourceAccessor.str(StringResKeys.STR_XMPP_AUTH_REQUEST));
                                } else if (contact != null) {
                                    contact.updateFromPresence(presenceType, element);
                                }
                            }
                        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_MESSAGE, tagName)) {
                            String senderJid = extractBareJid(element.getIntAttribute(PackedStringKeys.ATTR_FROM));
                            if (findContactByJid(senderJid) != null) {
                                StringBuffer sb = ObjectPool.newStringBuffer();
                                XmlElement subjectChild = element.findChildByKey(PackedStringKeys.TAG_SUBJECT);
                                if (subjectChild != null && (subjectText = StringUtils.fromBuffer(subjectChild.textContent)) != null) {
                                    sb.append(subjectText).append('\n');
                                }
                                XmlElement bodyChild = element.findChildByKey(PackedStringKeys.TAG_BODY);
                                if (bodyChild != null && (bodyText = StringUtils.fromBuffer(bodyChild.textContent)) != null) {
                                    sb.append(bodyText);
                                }
                                String messageText = ObjectPool.toStringAndRelease(sb);
                                if (messageText.length() > 0) {
                                    onMessage(senderJid, 0L, messageText);
                                }
                            }
                        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_FAILURE, tagName)) {
                            handleComplete();
                        } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_IQ, element.tagName)) {
                            if (element.findByAttrs(267810, 857625) == null || !StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_GET, element.getNameAttr())) {
                                handledPing = false;
                            } else {
                                XmlElement pingReply = element.cloneElement();
                                pingReply.children = null;
                                sendXmlElement(pingReply);
                                handledPing = true;
                            }
                            if (!handledPing && !processRosterUpdate(element)) {
                                if (element.findByAttrs(267762, 2102742) == null || !StringUtils.matchesKey(PackedStringKeys.TAG_RESULT, element.getNameAttr())) {
                                    handledSession = false;
                                } else {
                                    sendElementWithId(XmlElement.createFromState(136604).addNameAttr(198841).addSimpleChild(461668, 2299382));
                                    this.msgCount = 70;
                                    handledSession = true;
                                }
                                if (!handledSession) {
                                    if (this.msgCount == 70 && StringUtils.matchesKey(PackedStringKeys.TAG_RESULT, element.getNameAttr())) {
                                        sendElementWithId(XmlElement.createFromState(136604).addNameAttr(196633).addSimpleChild(333360, 1054101));
                                        this.msgCount = 80;
                                        handledBind = true;
                                    } else {
                                        handledBind = false;
                                    }
                                    if (!handledBind) {
                                        XmlElement discoInfoElement = element.findByAttrs(333360, 1119653);
                                        if (discoInfoElement == null || !StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_GET, element.getNameAttr())) {
                                            handledDiscoInfo = false;
                                        } else {
                                            discoInfoElement.setIntAttribute(262601, 1119195).setIntAttribute(459728, 1375).setIntAttribute(133230, 264455);
                                            sendXmlElement(element.cloneElement());
                                            handledDiscoInfo = true;
                                        }
                                        if (!handledDiscoInfo && (rosterElement = element.findByAttrs(333360, 1054101)) != null) {
                                            String iqType = element.getNameAttr();
                                            if (StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_SET, iqType)) {
                                                parseRosterItems(rosterElement);
                                                XmlElement ackElement = element.cloneElement();
                                                ackElement.children = null;
                                                sendXmlElement(ackElement);
                                            } else if (StringUtils.matchesKey(PackedStringKeys.TAG_RESULT, iqType)) {
                                                removeAllContacts();
                                                parseRosterItems(rosterElement);
                                                if (Utils.vectorSize(this.groups) == 0) {
                                                    this.groups.addElement(new XmppContactGroup(this, 1, ResourceAccessor.str(PackedStringKeys.XMPP_GROUP_GENERAL)));
                                                }
                                                this.progress = PROGRESS_CONNECTED;
                                                setStatusMode(this.configFlags);
                                                this.msgCount = 100;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    AppController.needsRepaint = true;
                    AppController.needsLayoutUpdate = true;
                    break;
                }
                break;
        }
        if (this.lastError != 0 && this.connection != null && this.connection.getState() == ConnectionThread.STATE_CLOSED) {
            this.progress = PROGRESS_DISCONNECTED;
            closeConnection();
            this.lastError = getDefaultError();
        }
        if (this.timeout <= 0 || !TimerManager.isTimerExpired(this.deadline)) {
            return;
        }
        sendRawBytes(new byte[]{32});
    }

    private void handleException(Throwable th) {
        RemoteLogger.log("XMPP", "handleException login=" + this.login, th);
        EventDispatcher.postAccountMessage(this, th.toString());
        closeConnection();
        this.lastError = getDefaultError();
    }

    public final void updatePresenceStatus(String str, int i) {
        if (isConnected()) {
            sendXmlElement(XmlElement.createFromState(530016).setAttrValue(131590, str).addNameAttr(i == 0 ? 594926 : i == 1 ? 660462 : 791532).addChild(XmlElement.createFromState(267628).addIdAttr(2037073).appendText((Object) this.displayName)));
        } else {
            EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_XMPP_EVENT));
        }
    }

    @Override // p000.Account
    public final void onError(int i) {
        int statusMode;
        switch (i) {
            case 0:
                statusMode = STATUS_ONLINE;
                break;
            case 1:
                statusMode = STATUS_AWAY;
                break;
            case 2:
                statusMode = STATUS_BUSY;
                break;
            case 3:
                statusMode = STATUS_DND;
                break;
            case 4:
                statusMode = STATUS_XA;
                break;
            default:
                disconnect();
                return;
        }
        if (isConnected()) {
            sendPresence(statusMode);
            return;
        }
        this.configFlags = statusMode;
        if (isConnecting()) {
            return;
        }
        connect(0);
    }

    private final void sendPresence(int i) {
        if (isMailRuVariant()) {
            i = 1;
        }
        this.lastError = i;
        XmlElement presence = XmlElement.createFromState(530016);
        int statusStringId = 0;
        switch (i) {
            case STATUS_ONLINE:
                statusStringId = STATUS_STR_ONLINE;
                break;
            case STATUS_BUSY:
                presence.setIntAttribute(267927, 267829);
                statusStringId = STATUS_STR_BUSY;
                break;
            case STATUS_XA:
                presence.addNameAttr(594975);
                break;
            case STATUS_AWAY:
                presence.setIntAttribute(267927, 265215);
                statusStringId = STATUS_STR_AWAY;
                break;
            case STATUS_DND:
                presence.setIntAttribute(267927, 202299);
                statusStringId = STATUS_STR_DND;
                break;
            case STATUS_CUSTOM:
                presence.setIntAttribute(267927, 136761);
                statusStringId = STATUS_STR_CUSTOM;
                break;
        }
        if (statusStringId != 0) {
            presence.addTextChild(ResourceAccessor.str(PackedStringKeys.XMPP_PRIORITY), ResourceAccessor.str(PackedStringKeys.XMPP_PRIORITY_DEFAULT));
            presence.setIntAttribute(394658, statusStringId);
            presence.addChildWithId(267628, 2037073).appendText((Object) this.displayName);
        }
        sendXmlElement(presence);
    }

    @Override // p000.Account
    public final int setCredentials(String str, String str2) {
        int result = super.setCredentials(str, str2);
        if (result != 0) {
            return result;
        }
        if (isMailRuVariant()) {
            this.serverResourceId = AppState.emptyStr;
            return 0;
        }
        this.serverAddress = AppState.emptyStr;
        return 0;
    }

    public final int setStatusMode(int i) {
        this.configFlags = i;
        if (isConnected()) {
            sendPresence(i);
            return 0;
        }
        if (isConnecting()) {
            return 487;
        }
        return connect(0);
    }

    @Override // p000.Account
    public final int validateContactDelete(Contact contact) {
        return 1032;
    }

    @Override // p000.Account
    public final int validateContactBlock(Contact contact) {
        return 1032;
    }

    @Override // p000.Account
    public final int validateContactUnblock(Contact contact) {
        return 1032;
    }

    @Override // p000.Account
    public final Contact newContact(String str) {
        return null;
    }

    @Override // p000.Account
    public int getIconId() {
        if (this.progress < PROGRESS_STARTING || this.progress >= PROGRESS_CONNECTED) {
            return getIconForError(this.lastError);
        }
        return ICON_CONNECTING;
    }

    private void sendStreamHeader() {
        sendRawBytes(new ByteBuffer().writeCompressed(PackedStringKeys.XML_XMPP_STREAM_HEADER).writeRawString(getStreamDomain()).writeCompressed(PackedStringKeys.XML_CLOSE_TAG_END).toByteArray());
    }

    @Override // p000.Account
    public int getSessionStringKey() {
        return 398518;
    }

    private XmppContact findContactByJid(String str) {
        return (XmppContact) getContact((Object) str);
    }

    @Override // p000.Account
    public final int validateModify(Contact contact, Object[] params) {
        int result = super.validateModify(contact, params);
        return result != 0 ? result : createRosterUpdate(((XmppContact) contact).jabberId, (String) params[0], findGroup(contact).name);
    }

    @Override // p000.Account
    public final int validateMove(Contact contact, ContactGroup fromGroup, ContactGroup toGroup) {
        int result = super.validateMove(contact, fromGroup, toGroup);
        return result != 0 ? result : createRosterUpdate(((XmppContact) contact).jabberId, contact.displayName, toGroup.name);
    }

    @Override // p000.Account
    public final int validateDelete(Contact contact) {
        if (!isConnected()) {
            return 299;
        }
        RegistrationState.setParam2(new Object[]{generateMessageId(), ((XmppContact) contact).getContactInfo()});
        this.state--;
        return sendElementWithId(XmlElement.createFromState(136604).addNameAttr(196633).setAttrValue(131590, contact.getIdentifier()).addSimpleChild(333452, 661030));
    }

    @Override // p000.Account
    public final int validateObject(Object obj) {
        return 0;
    }

    public final int addNewContact() {
        if (!isConnected()) {
            EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_XMPP_EVENT));
            return 0;
        }
        String contactJid = Utils.defaultStr(ContactState.getJid());
        createRosterUpdate(contactJid, Utils.defaultStr(ContactState.getDisplayName()), ((ContactGroup) ContactState.getGroupList().elementAt(ContactState.getGroupOperationResult())).name);
        updatePresenceStatus(contactJid, 0);
        updatePresenceStatus(contactJid, 1);
        return 0;
    }

    private final int createRosterUpdate(String str, String str2, String str3) {
        XmlElement queryElement = XmlElement.createFromState(333360).addIdAttr(1054101);
        XmlElement itemElement = XmlElement.createFromState(267942).setAttrValue(202421, str).setAttrValue(262601, str2).setAttrValue(792248, str2 == null ? ResourceAccessor.str(PackedStringKeys.TAG_REMOVE) : null);
        if (str3 != null && !StringUtils.matchesKey(PackedStringKeys.XMPP_GROUP_GENERAL, str3)) {
            itemElement.addTextChild(ResourceAccessor.str(PackedStringKeys.XMPP_GROUP_TAG), str3);
        }
        return sendElementWithId(XmlElement.createFromState(136604).addNameAttr(198841).addChild(queryElement.addChild(itemElement)));
    }

    @Override // p000.Account
    public final int validateResend(Contact contact) {
        if (isConnected()) {
            createRosterUpdate(contact.getIdentifier(), (String) null, (String) null);
            return 0;
        }
        EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_XMPP_EVENT));
        return 0;
    }

    public final int updateContactPresence(XmppContact contact, int i) {
        if (!isConnected()) {
            return 299;
        }
        String jid = contact.jabberId;
        String name = contact.displayName;
        ContactGroup group = findGroup(contact);
        createRosterUpdate(jid, name, (group == this.onlineGroup || contact.online) ? ResourceAccessor.str(PackedStringKeys.XMPP_GROUP_GENERAL) : group.name);
        return i;
    }

    @Override // p000.Account
    public final int disconnect() {
        int result = super.disconnect();
        if (result != 0) {
            return result;
        }
        closeConnection();
        this.lastError = getDefaultError();
        this.authState = null;
        this.lastException = null;
        this.msgCount = 0;
        clearParserState();
        return 0;
    }

    private XmppContactGroup findGroupByName(String str) {
        Vector groups = this.groups;
        for (int i = Utils.vectorSize(groups) - 1; i >= 0; i--) {
            XmppContactGroup group = (XmppContactGroup) groups.elementAt(i);
            if (StringUtils.equals(str, group.name)) {
                return group;
            }
        }
        return null;
    }

    @Override // p000.Account
    public final int validateGroupCreate(String str) {
        int result = super.validateGroupCreate(str);
        if (result != 0) {
            return result;
        }
        if (findGroupByName(str) != null) {
            return 0;
        }
        this.groups.addElement(new XmppContactGroup(this, 1, str));
        return 0;
    }

    @Override // p000.Account
    public final int validateGroupDelete(ContactGroup group) {
        int result = super.validateGroupDelete(group);
        if (result != 0) {
            return result;
        }
        this.groups.removeElement(group);
        return 0;
    }

    @Override // p000.Account
    public final int validateGroupRename(ContactGroup group, String str) {
        int result = super.validateGroupRename(group, str);
        if (result != 0) {
            return result;
        }
        if (Utils.vectorSize(group.contacts) != 0) {
            return 1032;
        }
        group.setNameIfChanged(str);
        return 0;
    }

    @Override // p000.Account
    public final int validateSend(Contact contact, String str, long j) {
        int result = super.validateSend(contact, str, j);
        if (result != 0) {
            return result;
        }
        this.sentCount++;
        return sendXmlElement(XmlElement.createFromState(464488).setAttrValue(131590, contact.getIdentifier()).addNameAttr(265215).addChild(XmlElement.createFromState(267946).appendText((Object) str)).addSimpleChild(398993, 2430320));
    }

    private final boolean processRosterUpdate(XmlElement element) {
        if (element.findByAttrs(333350, 661030) == null) {
            return false;
        }
        if (!StringUtils.matchesKey(PackedStringKeys.TAG_RESULT, element.getNameAttr())) {
            if (!StringUtils.matchesKey(PackedStringKeys.TAG_ERROR, element.getNameAttr())) {
                return false;
            }
            try {
                Object[] pendingRequest = RegistrationState.getParam2();
                if (((String) pendingRequest[0]).equals(element.getIntAttribute(PackedStringKeys.ATTR_ID))) {
                    RegistrationState.setParam1(((ContactInfo) pendingRequest[1]).setDescriptionBis(element.toString()));
                }
                return true;
            } catch (Throwable unused) {
                return true;
            }
        }
        try {
            Object[] pendingRequest = RegistrationState.getParam2();
            if (((String) pendingRequest[0]).equals(element.getIntAttribute(PackedStringKeys.ATTR_ID))) {
                ContactInfo contactInfo = ((ContactInfo) pendingRequest[1]).setDescriptionBis(ObjectPool.toStringAndRelease(buildContactDescription(ObjectPool.newStringBuffer(), element)));
                Image avatar = extractImageFromElement(element);
                if (avatar != null) {
                    contactInfo.put(ObjectPool.integerOf(25), avatar);
                }
                RegistrationState.setParam1(contactInfo);
            }
            return true;
        } catch (Throwable unused2) {
            return true;
        }
    }

    private final void parseRosterItems(XmlElement element) {
        Vector groups = this.groups;
        Vector children = element.children;
        for (int i = Utils.vectorSize(children) - 1; i >= 0; i--) {
            XmlElement itemElement = (XmlElement) children.elementAt(i);
            if (StringUtils.matchesKey(PackedStringKeys.TAG_ITEM, itemElement.tagName)) {
                String jid = itemElement.getIntAttribute(PackedStringKeys.ATTR_JID);
                String subscription = itemElement.getIntAttribute(PackedStringKeys.ATTR_SUBSCRIPTION);
                itemElement.getIntAttribute(PackedStringKeys.ATTR_ASK);
                String displayName = itemElement.getIntAttribute(PackedStringKeys.ATTR_NAME);
                boolean isRemoved = StringUtils.matchesKey(PackedStringKeys.TAG_REMOVE, subscription);
                if (displayName == null) {
                    displayName = jid;
                }
                String groupName = itemElement.getChildText(ResourceAccessor.str(PackedStringKeys.XMPP_GROUP_TAG));
                String resolvedGroupName = groupName;
                if (!Utils.nonEmpty(groupName)) {
                    resolvedGroupName = ResourceAccessor.str(PackedStringKeys.XMPP_GROUP_GENERAL);
                }
                XmppContact existingContact = (XmppContact) getContact((Object) jid);
                removeContact(existingContact, isRemoved);
                if (!isRemoved) {
                    XmppContactGroup foundGroup = findGroupByName(resolvedGroupName);
                    XmppContactGroup targetGroup = foundGroup;
                    if (foundGroup == null) {
                        XmppContactGroup newGroup = new XmppContactGroup(this, 1, resolvedGroupName);
                        targetGroup = newGroup;
                        groups.addElement(newGroup);
                    }
                    XmppContact newContact = new XmppContact(this, jid, displayName, subscription);
                    targetGroup.addContact((Object) newContact);
                    newContact.updateFromContact(existingContact);
                }
            }
        }
    }

    public String getAuthUsername() {
        return this.shortName;
    }

    private static String extractDisplayName(XmlElement element, String str) {
        try {
            return StringUtils.fromBuffer(element.findChildByKey(PackedStringKeys.TAG_NICK).textContent);
        } catch (Throwable unused) {
            return str;
        }
    }

    private static String extractBareJid(String str) {
        if (str == null) {
            return null;
        }
        int idx = str.indexOf(CHAR_SLASH);
        return idx <= 0 ? str : StringUtils.prefix(str, idx);
    }

    public static final int getIconForError(int i) {
        switch (i) {
            case STATUS_DISCONNECTED:
                return ICON_DISCONNECTED;
            case STATUS_ONLINE:
                return ICON_ONLINE;
            case STATUS_BUSY:
                return ICON_BUSY;
            case STATUS_XA:
                return ICON_XA;
            case STATUS_AWAY:
                return ICON_AWAY;
            case STATUS_DND:
                return ICON_DND;
            default:
                return ICON_CUSTOM;
        }
    }

    private final Image extractImageFromElement(XmlElement element) {
        Image image;
        String text;
        if (StringUtils.matchesKey(PackedStringKeys.TAG_BINVAL, element.tagName) && (text = StringUtils.fromBuffer(element.textContent)) != null) {
            String trimmed = Utils.trimAll(text);
            if (Utils.nonEmpty(trimmed)) {
                try {
                    return Base64.decode(trimmed).toImage();
                } catch (Throwable unused) {
                }
            }
        }
        for (int i = Utils.vectorSize(element.children) - 1; i >= 0; i--) {
            image = extractImageFromElement(element.getChildAt(i));
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    private final StringBuffer buildContactDescription(StringBuffer sb, XmlElement element) {
        if (!StringUtils.matchesKey(PackedStringKeys.TAG_PHOTO, element.tagName)) {
            String text = StringUtils.fromBuffer(element.textContent);
            if (text != null) {
                String trimmed = Utils.trimAll(text);
                if (Utils.nonEmpty(trimmed)) {
                    sb.append(trimmed).append('\n');
                }
            }
            for (int i = 0; i < Utils.vectorSize(element.children); i++) {
                buildContactDescription(sb, element.getChildAt(i));
            }
        }
        return sb;
    }

    public static void processXmppStream(Object[] parserState) {
        while (true) {
            XmppProtocol xmpp = (XmppProtocol) parserState[0];
            XmlElement element = ((XmlParser) parserState[2]).parse();
            synchronized (xmpp.elementQueue) {
                xmpp.elementQueue.addElement(element);
            }
        }
    }

    public static int readUtf8Char(Object[] parserState) {
        while (true) {
            ByteBuffer buffer = (ByteBuffer) parserState[1];
            synchronized (buffer) {
                int available = buffer.length;
                if (available > 0) {
                    int byte1 = buffer.peekByteAt(0);
                    if ((byte1 & 128) == 0) {
                        return buffer.readUByte();
                    }
                    if (available != 1) {
                        int byte2 = buffer.peekByteAt(1);
                        if (byte1 < 224) {
                            buffer.skip(2);
                            return ((byte1 & 31) << 6) | (byte2 & 63);
                        }
                        if (available != 2) {
                            buffer.skip(2);
                            return ((byte1 & 15) << 12) | ((byte2 & 63) << 6) | (buffer.readUByte() & 63);
                        }
                    }
                }
            }
            try { Thread.sleep(10); } catch (InterruptedException e) { }
        }
    }
}
