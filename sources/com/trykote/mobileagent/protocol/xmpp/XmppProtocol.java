package com.trykote.mobileagent.protocol.xmpp;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.ConnectionThread;
import com.trykote.mobileagent.util.Base64;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;
import com.trykote.mobileagent.util.XmlElement;
import com.trykote.mobileagent.util.XmlParser;

import javax.microedition.lcdui.Image;
import java.util.Vector;

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
    private static final char CHAR_QUOTE = '"';

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

    // Error/validation codes
    private static final int ERR_NOT_SUPPORTED = 1032;
    private static final int ERR_AUTH_FAILED = 1033;
    private static final int ERR_NOT_CONNECTED = 299;
    private static final int ERR_ALREADY_CONNECTING = 487;

    // XMPP keepalive byte (space character)
    private static final byte KEEPALIVE_BYTE = 32;

    // Avatar metadata key
    private static final int AVATAR_KEY = 25;

    // Nonce prefix length in DIGEST-MD5 ("nonce=" is 7 chars including quote)
    private static final int NONCE_PREFIX_LENGTH = 7;

    // Realm prefix in DIGEST-MD5 challenge
    private static final String DIGEST_REALM_PREFIX = "realm=\"";

    public final Vector elementQueue;

    private Object[] parserState;

    private Object[] authState;

    public String serverAddress;

    public int serverPort;

    public Object authResult;

    private Throwable lastException;

    private String serverDomain;

    public String serverResourceId;

    public XmppProtocol(int accountId, String login, String password) {
        super(accountId, login, password);
        this.configFlags = STATUS_ONLINE;
        this.elementQueue = ObjectPool.newVector();
        XmppContactGroup defaultGrp = new XmppContactGroup(this, 0, ResourceAccessor.str(StringResKeys.STR_GROUP_DEFAULT));
        defaultGrp.isSpecial = true;
        this.defaultGroup = defaultGrp;
        this.serverAddress = AppState.emptyStr;
        this.serverResourceId = AppState.emptyStr;
    }

    @Override
    public int getType() {
        return TYPE_XMPP;
    }

    private String generateMessageId() {
        StringBuffer sb = ObjectPool.newStringBuffer().append('m');
        int id = this.state + 1;
        this.state = id;
        return ObjectPool.toStringAndRelease(sb.append(id));
    }

    public XmppProtocol(ByteBuffer buffer) {
        super(buffer);
        this.elementQueue = ObjectPool.newVector();
        int groupCount = buffer.readInt();
        for (int i = groupCount - 1; i >= 0; i--) {
            addGroup(new XmppContactGroup(this, buffer));
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

    @Override
    public final Account serializeAccount(ByteBuffer buffer, boolean includeGroups, boolean includePrivate) {
        super.serializeAccount(buffer, includeGroups, includePrivate);
        buffer.writeStringLatin1(this.serverAddress).writeShortBE(this.serverPort).writeStringLatin1(this.serverResourceId);
        return this;
    }

    @Override
    public boolean isMailRuVariant() {
        return false;
    }

    public String getStreamDomain() {
        return StringUtils.getDomain(this.login);
    }

    @Override
    public final ContactGroup createOnlineGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override
    public final ContactGroup createBlockedGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_TEMPORARY));
    }

    @Override
    public final ContactGroup createOfflineGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_IGNORE));
    }

    @Override
    public final ContactGroup createSpecialGroup() {
        return new XmppContactGroup(this, -1, ResourceAccessor.str(StringResKeys.STR_GROUP_PHONE_CONTACTS));
    }

    @Override
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        return 0;
    }

    private int sendRawBytes(byte[] data) {
        long timeoutMillis = UIState.isWifiConnection() ? TIMEOUT_WIFI : TIMEOUT_CELLULAR;
        this.timeout = timeoutMillis;
        this.deadline = System.currentTimeMillis() + timeoutMillis;
        return sendData(new ByteBuffer().writeBytes(data));
    }

    private int sendXmlElement(XmlElement element) {
        String xml = element.toString();
        RemoteLogger.log("XMPP", ">>> send: " + xml.substring(0, Math.min(xml.length(), 300)));
        return sendData(new ByteBuffer().writeUTFNoLen(xml));
    }

    private int sendElementWithId(XmlElement element) {
        return sendXmlElement(element.setAttrValue(PackedStringKeys.ATTR_ID, generateMessageId()));
    }

    private void clearParserState() {
        this.dataBuffer.clear();
        Object[] state = this.parserState;
        if (state != null) {
            state[2] = null;
            state[1] = null;
            state[0] = null;
        }
    }

    public final void setException(Throwable exception) {
        if (this.progress == PROGRESS_RESOLVING) {
            this.lastException = exception;
        }
    }

    public final void setAuthParameters(String address, int port) {
        if (this.progress == PROGRESS_RESOLVING) {
            this.serverPort = port;
            this.serverAddress = address;
        }
    }

    private boolean isGmailDomain() {
        return getType() == TYPE_XMPP && this.login.endsWith(ResourceAccessor.str(PackedStringKeys.DOMAIN_GMAIL_COM));
    }

    // --- loadData: connection state machine ---

    @Override
    public final void loadData() throws Throwable {
        switch (this.progress) {
            case PROGRESS_DISCONNECTED:
                handleDisconnected();
                break;
            case PROGRESS_STARTING:
                handleStarting();
                break;
            case PROGRESS_RESOLVING:
                handleResolving();
                break;
            case PROGRESS_CONNECTING:
                handleConnecting();
                break;
            case PROGRESS_OPENING_STREAM:
                handleOpeningStream();
                break;
            default:
                handleProcessing();
                break;
        }
        if (this.lastError != 0 && this.connection != null && this.connection.getState() == ConnectionThread.STATE_CLOSED) {
            this.progress = PROGRESS_DISCONNECTED;
            closeConnection();
            this.lastError = getDefaultError();
        }
        if (this.timeout > 0 && TimerManager.isTimerExpired(this.deadline)) {
            sendRawBytes(new byte[]{KEEPALIVE_BYTE});
        }
    }

    private void handleDisconnected() {
        clearParserState();
        Object[] prevAuthState = this.authState;
        if (prevAuthState != null) {
            prevAuthState[0] = null;
        }
        this.authState = null;
        this.lastException = null;
        this.msgCount = 0;
    }

    private void handleStarting() {
        RemoteLogger.log("XMPP", "progress STARTING, login=" + this.login + " isMailRu=" + isMailRuVariant());
        this.msgCount = 10;
        if (isMailRuVariant()) {
            if (Utils.nonEmpty(this.serverResourceId)) {
                this.progress = PROGRESS_CONNECTING;
            } else {
                this.progress = PROGRESS_RESOLVING;
                Object[] taskArgs;
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
        notifyConnectionProgressChanged();
    }

    private void handleResolving() {
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
        notifyConnectionProgressChanged();
    }

    private void handleConnecting() {
        this.msgCount = 30;
        this.state = 0;
        String connAddr = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(this.serverAddress).append(':').append(this.serverPort));
        RemoteLogger.log("XMPP", "progress CONNECTING to " + connAddr);
        this.connection = new ConnectionThread(connAddr);
        this.progress = PROGRESS_OPENING_STREAM;
        notifyConnectionProgressChanged();
    }

    private void handleOpeningStream() throws Throwable {
        clearParserState();
        this.msgCount = 40;
        if (isGmailDomain()) {
            if (this.authResult != null && this.authResult instanceof Throwable) {
                handleConnError();
            }
            return;
        }
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
            sendStreamHeader();
        } else if (this.connection.getState() <= ConnectionThread.STATE_CLOSED) {
            closeConnection();
        }
        notifyConnectionProgressChanged();
    }

    // --- Processing: stanza dispatch ---

    private void handleProcessing() throws Throwable {
        this.connection.drainInput(this.dataBuffer);
        AccountManager.recordInboundTraffic(this, this.dataBuffer.length);
        feedParserBuffer();
        XmlElement element = (XmlElement) Utils.dequeue(this.elementQueue);
        if (element == null) {
            return;
        }
        String tagName = element.tagName;
        RemoteLogger.log("XMPP", "<<< element: " + element.toString().substring(0, Math.min(element.toString().length(), 300)));
        extractServerDomain(element, tagName);
        if (!StringUtils.matchesKey(PackedStringKeys.XMPP_STREAM_STREAM, tagName)) {
            dispatchStanza(element, tagName);
            notifyConnectionProgressChanged();
            notifyContactListUpdated();
        }
    }

    private void feedParserBuffer() {
        Object[] state = this.parserState;
        ByteBuffer inputBuffer = this.dataBuffer;
        ByteBuffer parserBuffer = (ByteBuffer) state[1];
        synchronized (parserBuffer) {
            parserBuffer.writeBytesAt(inputBuffer.data, inputBuffer.offset, inputBuffer.length);
            inputBuffer.clear();
        }
    }

    private void extractServerDomain(XmlElement element, String tagName) {
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STREAM_STREAM, tagName)) {
            String fromDomain = element.getIntAttribute(PackedStringKeys.ATTR_FROM);
            if (fromDomain != null) {
                this.serverDomain = fromDomain;
                RemoteLogger.log("XMPP", "server domain: " + fromDomain);
            }
        }
    }

    private void dispatchStanza(XmlElement element, String tagName) {
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STREAM_FEATURES, tagName)) {
            handleStreamFeatures(element);
        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_CHALLENGE, tagName)) {
            handleChallenge(element);
        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_SUCCESS, tagName)) {
            sendStreamHeader();
        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_PRESENCE, tagName)) {
            handlePresence(element);
        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_MESSAGE, tagName)) {
            handleMessage(element);
        } else if (StringUtils.matchesKey(PackedStringKeys.TAG_FAILURE, tagName)) {
            handleComplete();
        } else if (StringUtils.matchesKey(PackedStringKeys.XMPP_IQ, tagName)) {
            handleIq(element);
        }
    }

    private void handleStreamFeatures(XmlElement element) {
        XmlElement mechanisms = element.findByName(ResourceAccessor.str(PackedStringKeys.XMPP_MECHANISMS));
        if (mechanisms != null) {
            selectAuthMechanism(mechanisms);
        } else if (element.findByName(ResourceAccessor.str(PackedStringKeys.XMPP_BIND)) != null) {
            XmlElement bindRequest = XmlElement.createFromState(PackedStringKeys.XMPP_IQ).addNameAttr(PackedStringKeys.XMPP_TYPE_SET);
            bindRequest.addChildWithId(PackedStringKeys.XMPP_BIND, PackedStringKeys.XMPP_NS_BIND).addTextChild(ResourceAccessor.str(PackedStringKeys.XMPP_RESOURCE), ResourceAccessor.str(PackedStringKeys.PLATFORM_J2ME));
            sendElementWithId(bindRequest);
            this.msgCount = 60;
        } else {
            EventDispatcher.postAccountError(this, ERR_AUTH_FAILED);
            closeConnection();
            this.lastError = getDefaultError();
        }
    }

    private void selectAuthMechanism(XmlElement mechanisms) {
        XmlElement authElement = XmlElement.createFromState(PackedStringKeys.TAG_AUTH).addIdAttr(PackedStringKeys.XMPP_NS_SASL);
        String digestMd5 = ResourceAccessor.str(PackedStringKeys.AUTH_DIGEST_MD5);
        if (mechanisms.findChildByText(digestMd5) != null) {
            sendXmlElement(authElement.setAttrValue(PackedStringKeys.ATTR_MECHANISM, digestMd5));
            return;
        }
        String xGoogleToken = ResourceAccessor.str(PackedStringKeys.HEADER_X_GOOGLE_TOKEN);
        if (mechanisms.findChildByText(xGoogleToken) != null) {
            String credentials = new ByteBuffer().writeByte(0).writeRawString(this.shortName).writeByte(0).writeRawString((String) this.authResult).toBase64();
            sendXmlElement(authElement.setAttrValue(PackedStringKeys.ATTR_MECHANISM, xGoogleToken).appendText((Object) credentials));
            return;
        }
        String plain = ResourceAccessor.str(PackedStringKeys.AUTH_MECHANISM_PLAIN);
        if (mechanisms.findChildByText(plain) != null) {
            String credentials = new ByteBuffer().writeUTFNoLen(new ByteBuffer().writeRawString(this.shortName).writeByte(CHAR_AT).writeRawString(this.serverAddress).readAllByteStr()).writeByte(0).writeUTFNoLen(this.shortName).writeByte(0).writeUTFNoLen(this.password).toBase64();
            sendXmlElement(authElement.setAttrValue(PackedStringKeys.ATTR_MECHANISM, plain).appendText((Object) credentials));
        }
    }

    private void handleChallenge(XmlElement element) {
        XmlElement challengeResponse = XmlElement.createFromState(PackedStringKeys.TAG_RESPONSE).addIdAttr(PackedStringKeys.XMPP_NS_SASL);
        String decoded = Base64.decode(StringUtils.fromBuffer(element.textContent)).getStringAndClear();
        RemoteLogger.log("XMPP", "DIGEST-MD5 challenge: " + decoded);
        int idx = decoded.indexOf(ResourceAccessor.str(PackedStringKeys.DIGEST_NONCE_EQ));
        if (idx >= 0) {
            buildDigestMd5Response(challengeResponse, decoded, idx);
        }
        sendXmlElement(challengeResponse);
    }

    private void buildDigestMd5Response(XmlElement responseElement, String challenge, int nonceIdx) {
        int nonceStart = nonceIdx + NONCE_PREFIX_LENGTH;
        String username = getAuthUsername();
        String password = this.password;
        String realm = parseDigestRealm(challenge);
        RemoteLogger.log("XMPP", "DIGEST-MD5 auth: user=" + username + " realm=" + realm);
        String nonce = StringUtils.substring(challenge, nonceStart, challenge.indexOf(CHAR_QUOTE, nonceStart));
        String cnonce = Utils.generateRandomHash();

        String ha1Hex = new ByteBuffer().writeRawString(username).writeByte(':').writeRawString(realm).writeByte(':').writeRawString(password).encryptMD5().writeByte(':').writeRawString(nonce).writeByte(':').writeRawString(cnonce).encryptMD5().toHexString();
        String ha2Hex = new ByteBuffer().writeCompressed(PackedStringKeys.DIGEST_AUTH_XMPP).writeRawString(realm).encryptMD5().toHexString();
        String responseHex = new ByteBuffer().writeRawString(ha1Hex).writeByte(':').writeRawString(nonce).writeCompressed(PackedStringKeys.DIGEST_NC).writeRawString(cnonce).writeByte(':').writeCompressed(PackedStringKeys.TAG_AUTH).writeByte(':').writeRawString(ha2Hex).encryptMD5().toHexString();

        ByteBuffer digestBuffer = new ByteBuffer()
            .writeCompressed(PackedStringKeys.DIGEST_USERNAME).writeRawString(username)
            .writeCompressed(PackedStringKeys.DIGEST_REALM).writeRawString(realm)
            .writeCompressed(PackedStringKeys.DIGEST_NONCE).writeRawString(nonce)
            .writeCompressed(PackedStringKeys.DIGEST_NC_CNONCE).writeRawString(cnonce)
            .writeCompressed(PackedStringKeys.DIGEST_QOP_AUTH).writeRawString(realm)
            .writeCompressed(PackedStringKeys.DIGEST_RESPONSE).writeRawString(responseHex)
            .writeCompressed(PackedStringKeys.DIGEST_CHARSET);
        responseElement.appendText((Object) digestBuffer.toBase64());
    }

    private String parseDigestRealm(String challenge) {
        int realmIdx = challenge.indexOf(DIGEST_REALM_PREFIX);
        if (realmIdx >= 0) {
            int realmStart = realmIdx + DIGEST_REALM_PREFIX.length();
            return StringUtils.substring(challenge, realmStart, challenge.indexOf(CHAR_QUOTE, realmStart));
        }
        return this.serverAddress;
    }

    private void handlePresence(XmlElement element) {
        String nameAttr = element.getNameAttr();
        String presenceType = nameAttr != null ? nameAttr : ResourceAccessor.str(PackedStringKeys.XMPP_STATUS_AVAILABLE);
        String jid = extractBareJid(element.getIntAttribute(PackedStringKeys.ATTR_FROM));
        if (jid == null) {
            return;
        }
        XmppContact contact = findContactByJid(jid);
        if (StringUtils.matchesKey(PackedStringKeys.XMPP_SUBSCRIBE, presenceType)) {
            if (contact == null) {
                XmppContact newContact = new XmppContact(this, jid, extractDisplayName(element, jid), null);
                newContact.online = true;
                contact = newContact;
                this.defaultGroup.addContact((Object) newContact);
            }
            contact.updateFromPresence(presenceType, element);
            onMessage(jid, 0L, ResourceAccessor.str(StringResKeys.STR_XMPP_AUTH_REQUEST));
        } else if (contact != null) {
            contact.updateFromPresence(presenceType, element);
        }
    }

    private void handleMessage(XmlElement element) {
        String senderJid = extractBareJid(element.getIntAttribute(PackedStringKeys.ATTR_FROM));
        XmppContact sender = findContactByJid(senderJid);
        if (sender == null) {
            return;
        }
        sender.markOnlineIfOffline();
        StringBuffer sb = ObjectPool.newStringBuffer();
        XmlElement subjectChild = element.findChildByKey(PackedStringKeys.TAG_SUBJECT);
        if (subjectChild != null) {
            String subjectText = StringUtils.fromBuffer(subjectChild.textContent);
            if (subjectText != null) {
                sb.append(subjectText).append('\n');
            }
        }
        XmlElement bodyChild = element.findChildByKey(PackedStringKeys.TAG_BODY);
        if (bodyChild != null) {
            String bodyText = StringUtils.fromBuffer(bodyChild.textContent);
            if (bodyText != null) {
                sb.append(bodyText);
            }
        }
        String messageText = ObjectPool.toStringAndRelease(sb);
        if (messageText.length() > 0) {
            onMessage(senderJid, 0L, messageText);
        }
    }

    private void handleIq(XmlElement element) {
        if (handlePing(element)) return;
        if (processRosterUpdate(element)) return;
        if (handleBindResult(element)) return;
        if (handleSessionResult(element)) return;
        if (handleDiscoInfo(element)) return;
        handleRosterResponse(element);
    }

    private boolean handlePing(XmlElement element) {
        if (element.findByAttrs(PackedStringKeys.TAG_PING, PackedStringKeys.XMPP_NS_PING) == null) return false;
        if (!StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_GET, element.getNameAttr())) return false;
        XmlElement pingReply = element.cloneElement();
        pingReply.children = null;
        sendXmlElement(pingReply);
        return true;
    }

    private boolean handleBindResult(XmlElement element) {
        if (element.findByAttrs(PackedStringKeys.XMPP_BIND, PackedStringKeys.XMPP_NS_BIND) == null) return false;
        if (!StringUtils.matchesKey(PackedStringKeys.TAG_RESULT, element.getNameAttr())) return false;
        sendElementWithId(XmlElement.createFromState(PackedStringKeys.XMPP_IQ).addNameAttr(PackedStringKeys.XMPP_TYPE_SET).addSimpleChild(PackedStringKeys.TAG_SESSION, PackedStringKeys.XMPP_NS_SESSION));
        this.msgCount = 70;
        return true;
    }

    private boolean handleSessionResult(XmlElement element) {
        if (this.msgCount != 70) return false;
        if (!StringUtils.matchesKey(PackedStringKeys.TAG_RESULT, element.getNameAttr())) return false;
        sendElementWithId(XmlElement.createFromState(PackedStringKeys.XMPP_IQ).addNameAttr(PackedStringKeys.XMPP_TYPE_GET).addSimpleChild(PackedStringKeys.TAG_QUERY, PackedStringKeys.XMPP_NS_ROSTER));
        this.msgCount = 80;
        return true;
    }

    private boolean handleDiscoInfo(XmlElement element) {
        XmlElement discoInfoElement = element.findByAttrs(PackedStringKeys.TAG_QUERY, PackedStringKeys.XMPP_NS_VERSION);
        if (discoInfoElement == null) return false;
        if (!StringUtils.matchesKey(PackedStringKeys.XMPP_TYPE_GET, element.getNameAttr())) return false;
        discoInfoElement.setIntAttribute(PackedStringKeys.ATTR_NAME, PackedStringKeys.XMPP_CLIENT_NAME)
            .setIntAttribute(PackedStringKeys.TAG_VERSION, PackedStringKeys.PLATFORM_J2ME)
            .setIntAttribute(PackedStringKeys.TAG_OS, PackedStringKeys.PLATFORM_J2ME);
        sendXmlElement(element.cloneElement());
        return true;
    }

    private void handleRosterResponse(XmlElement element) {
        XmlElement rosterElement = element.findByAttrs(PackedStringKeys.TAG_QUERY, PackedStringKeys.XMPP_NS_ROSTER);
        if (rosterElement == null) {
            return;
        }
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

    // --- End loadData decomposition ---

    private void handleException(Throwable exception) {
        RemoteLogger.log("XMPP", "handleException login=" + this.login, exception);
        EventDispatcher.postAccountMessage(this, exception.toString());
        closeConnection();
        this.lastError = getDefaultError();
    }

    public final void updatePresenceStatus(String targetJid, int subscriptionType) {
        if (isConnected()) {
            sendXmlElement(XmlElement.createFromState(PackedStringKeys.TAG_PRESENCE).setAttrValue(PackedStringKeys.ATTR_TO, targetJid).addNameAttr(subscriptionType == 0 ? PackedStringKeys.XMPP_SUBSCRIBE : subscriptionType == 1 ? PackedStringKeys.XMPP_SUBSCRIBED : PackedStringKeys.XMPP_UNSUBSCRIBED).addChild(XmlElement.createFromState(PackedStringKeys.TAG_NICK).addIdAttr(PackedStringKeys.XMPP_NS_NICK).appendText((Object) this.displayName)));
        } else {
            EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_XMPP_EVENT));
        }
    }

    @Override
    public final void onError(int errorCode) {
        int statusMode;
        switch (errorCode) {
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

    private void sendPresence(int statusMode) {
        if (isMailRuVariant()) {
            statusMode = STATUS_ONLINE;
        }
        this.lastError = statusMode;
        XmlElement presence = XmlElement.createFromState(PackedStringKeys.TAG_PRESENCE);
        int statusStringId = 0;
        switch (statusMode) {
            case STATUS_ONLINE:
                statusStringId = STATUS_STR_ONLINE;
                break;
            case STATUS_BUSY:
                presence.setIntAttribute(PackedStringKeys.TAG_SHOW, PackedStringKeys.XMPP_STATUS_AWAY);
                statusStringId = STATUS_STR_BUSY;
                break;
            case STATUS_XA:
                presence.addNameAttr(PackedStringKeys.XMPP_STATUS_INVISIBLE);
                break;
            case STATUS_AWAY:
                presence.setIntAttribute(PackedStringKeys.TAG_SHOW, PackedStringKeys.XMPP_TYPE_CHAT);
                statusStringId = STATUS_STR_AWAY;
                break;
            case STATUS_DND:
                presence.setIntAttribute(PackedStringKeys.TAG_SHOW, PackedStringKeys.XMPP_STATUS_DND);
                statusStringId = STATUS_STR_DND;
                break;
            case STATUS_CUSTOM:
                presence.setIntAttribute(PackedStringKeys.TAG_SHOW, PackedStringKeys.XMPP_STATUS_XA);
                statusStringId = STATUS_STR_CUSTOM;
                break;
        }
        if (statusStringId != 0) {
            presence.addTextChild(ResourceAccessor.str(PackedStringKeys.XMPP_PRIORITY), ResourceAccessor.str(PackedStringKeys.XMPP_PRIORITY_DEFAULT));
            presence.setIntAttribute(PackedStringKeys.TAG_STATUS, statusStringId);
            presence.addChildWithId(PackedStringKeys.TAG_NICK, PackedStringKeys.XMPP_NS_NICK).appendText((Object) this.displayName);
        }
        sendXmlElement(presence);
    }

    @Override
    public final int setCredentials(String newLogin, String newPassword) {
        int result = super.setCredentials(newLogin, newPassword);
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

    @Override
    public int handleStatusOption(int optionIndex) {
        return setStatusMode(optionIndex);
    }

    @Override
    public void setContactPresenceFeature(Contact contact, int feature) {
        if (contact instanceof XmppContact) {
            ((XmppContact) contact).setPresenceFeature(feature);
        }
    }

    public final int setStatusMode(int statusMode) {
        this.configFlags = statusMode;
        if (isConnected()) {
            sendPresence(statusMode);
            return 0;
        }
        if (isConnecting()) {
            return ERR_ALREADY_CONNECTING;
        }
        return connect(0);
    }

    @Override
    public final int validateContactDelete(Contact contact) {
        return ERR_NOT_SUPPORTED;
    }

    @Override
    public final int validateContactBlock(Contact contact) {
        return ERR_NOT_SUPPORTED;
    }

    @Override
    public final int validateContactUnblock(Contact contact) {
        return ERR_NOT_SUPPORTED;
    }

    @Override
    public final Contact newContact(String contactAddress) {
        return null;
    }

    @Override
    public int getIconId() {
        if (this.progress < PROGRESS_STARTING || this.progress >= PROGRESS_CONNECTED) {
            return getIconForError(this.lastError);
        }
        return ICON_CONNECTING;
    }

    private void sendStreamHeader() {
        sendRawBytes(new ByteBuffer().writeCompressed(PackedStringKeys.XML_XMPP_STREAM_HEADER).writeRawString(getStreamDomain()).writeCompressed(PackedStringKeys.XML_CLOSE_TAG_END).toByteArray());
    }

    @Override
    public int getSessionStringKey() {
        return PackedStringKeys.XMPP_RESOURCE_JABBER;
    }

    private XmppContact findContactByJid(String jid) {
        return (XmppContact) getContact((Object) jid);
    }

    @Override
    public final int validateModify(Contact contact, Object[] params) {
        int result = super.validateModify(contact, params);
        return result != 0 ? result : createRosterUpdate(((XmppContact) contact).jabberId, (String) params[0], findGroup(contact).name);
    }

    @Override
    public final int validateMove(Contact contact, ContactGroup fromGroup, ContactGroup toGroup) {
        int result = super.validateMove(contact, fromGroup, toGroup);
        return result != 0 ? result : createRosterUpdate(((XmppContact) contact).jabberId, contact.displayName, toGroup.name);
    }

    @Override
    public final int validateDelete(Contact contact) {
        if (!isConnected()) {
            return ERR_NOT_CONNECTED;
        }
        RegistrationState.setParam2(new Object[]{generateMessageId(), ((XmppContact) contact).getContactInfo()});
        this.state--;
        return sendElementWithId(XmlElement.createFromState(PackedStringKeys.XMPP_IQ).addNameAttr(PackedStringKeys.XMPP_TYPE_GET).setAttrValue(PackedStringKeys.ATTR_TO, contact.getIdentifier()).addSimpleChild(PackedStringKeys.TAG_VCARD_UPPER, PackedStringKeys.XMPP_NS_VCARD_TEMP));
    }

    @Override
    public final int validateObject(Object obj) {
        return 0;
    }

    @Override
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

    private int createRosterUpdate(String jid, String displayName, String groupName) {
        XmlElement queryElement = XmlElement.createFromState(PackedStringKeys.TAG_QUERY).addIdAttr(PackedStringKeys.XMPP_NS_ROSTER);
        XmlElement itemElement = XmlElement.createFromState(PackedStringKeys.TAG_ITEM).setAttrValue(PackedStringKeys.ATTR_JID, jid).setAttrValue(PackedStringKeys.ATTR_NAME, displayName).setAttrValue(PackedStringKeys.ATTR_SUBSCRIPTION, displayName == null ? ResourceAccessor.str(PackedStringKeys.TAG_REMOVE) : null);
        if (groupName != null && !StringUtils.matchesKey(PackedStringKeys.XMPP_GROUP_GENERAL, groupName)) {
            itemElement.addTextChild(ResourceAccessor.str(PackedStringKeys.XMPP_GROUP_TAG), groupName);
        }
        return sendElementWithId(XmlElement.createFromState(PackedStringKeys.XMPP_IQ).addNameAttr(PackedStringKeys.XMPP_TYPE_SET).addChild(queryElement.addChild(itemElement)));
    }

    @Override
    public final int validateResend(Contact contact) {
        if (isConnected()) {
            createRosterUpdate(contact.getIdentifier(), (String) null, (String) null);
            return 0;
        }
        EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_XMPP_EVENT));
        return 0;
    }

    public final int updateContactPresence(XmppContact contact, int result) {
        if (!isConnected()) {
            return ERR_NOT_CONNECTED;
        }
        String jid = contact.jabberId;
        String name = contact.displayName;
        ContactGroup group = findGroup(contact);
        createRosterUpdate(jid, name, (group == this.onlineGroup || contact.online) ? ResourceAccessor.str(PackedStringKeys.XMPP_GROUP_GENERAL) : group.name);
        return result;
    }

    @Override
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

    private XmppContactGroup findGroupByName(String name) {
        Vector groups = this.groups;
        for (int i = Utils.vectorSize(groups) - 1; i >= 0; i--) {
            XmppContactGroup group = (XmppContactGroup) groups.elementAt(i);
            if (StringUtils.equals(name, group.name)) {
                return group;
            }
        }
        return null;
    }

    @Override
    public final int validateGroupCreate(String groupName) {
        int result = super.validateGroupCreate(groupName);
        if (result != 0) {
            return result;
        }
        if (findGroupByName(groupName) != null) {
            return 0;
        }
        this.groups.addElement(new XmppContactGroup(this, 1, groupName));
        return 0;
    }

    @Override
    public final int validateGroupDelete(ContactGroup group) {
        int result = super.validateGroupDelete(group);
        if (result != 0) {
            return result;
        }
        this.groups.removeElement(group);
        return 0;
    }

    @Override
    public final int validateGroupRename(ContactGroup group, String newName) {
        int result = super.validateGroupRename(group, newName);
        if (result != 0) {
            return result;
        }
        if (Utils.vectorSize(group.contacts) != 0) {
            return ERR_NOT_SUPPORTED;
        }
        group.setNameIfChanged(newName);
        return 0;
    }

    @Override
    public final int validateSend(Contact contact, String message, long timestamp) {
        int result = super.validateSend(contact, message, timestamp);
        if (result != 0) {
            return result;
        }
        this.sentCount++;
        return sendXmlElement(XmlElement.createFromState(PackedStringKeys.TAG_MESSAGE).setAttrValue(PackedStringKeys.ATTR_TO, contact.getIdentifier()).addNameAttr(PackedStringKeys.XMPP_TYPE_CHAT).addChild(XmlElement.createFromState(PackedStringKeys.TAG_BODY).appendText((Object) message)).addSimpleChild(PackedStringKeys.XMPP_CHATSTATES_ACTIVE, PackedStringKeys.XMPP_NS_CHATSTATES));
    }

    private boolean processRosterUpdate(XmlElement element) {
        if (element.findByAttrs(PackedStringKeys.TAG_VCARD, PackedStringKeys.XMPP_NS_VCARD_TEMP) == null) {
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
                    contactInfo.put(ObjectPool.integerOf(AVATAR_KEY), avatar);
                }
                RegistrationState.setParam1(contactInfo);
            }
            return true;
        } catch (Throwable unused2) {
            return true;
        }
    }

    private void parseRosterItems(XmlElement rosterQuery) {
        Vector groups = this.groups;
        Vector children = rosterQuery.children;
        for (int i = Utils.vectorSize(children) - 1; i >= 0; i--) {
            XmlElement itemElement = (XmlElement) children.elementAt(i);
            if (StringUtils.matchesKey(PackedStringKeys.TAG_ITEM, itemElement.tagName)) {
                String jid = itemElement.getIntAttribute(PackedStringKeys.ATTR_JID);
                if (jid != null && jid.indexOf(CHAR_AT) < 0 && this.serverDomain != null) {
                    jid = jid + CHAR_AT + this.serverDomain;
                    RemoteLogger.log("XMPP", "roster: bare JID fixed -> " + jid);
                }
                String subscription = itemElement.getIntAttribute(PackedStringKeys.ATTR_SUBSCRIPTION);
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

    private static String extractDisplayName(XmlElement element, String fallbackJid) {
        try {
            return StringUtils.fromBuffer(element.findChildByKey(PackedStringKeys.TAG_NICK).textContent);
        } catch (Throwable unused) {
            return fallbackJid;
        }
    }

    private static String extractBareJid(String fullJid) {
        if (fullJid == null) {
            return null;
        }
        int idx = fullJid.indexOf(CHAR_SLASH);
        return idx <= 0 ? fullJid : StringUtils.prefix(fullJid, idx);
    }

    public static int getIconForError(int statusMode) {
        switch (statusMode) {
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

    private Image extractImageFromElement(XmlElement element) {
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
            Image image = extractImageFromElement(element.getChildAt(i));
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    private StringBuffer buildContactDescription(StringBuffer sb, XmlElement element) {
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
