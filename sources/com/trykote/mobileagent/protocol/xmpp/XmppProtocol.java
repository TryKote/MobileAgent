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
import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: ae */
/* loaded from: MobileAgent_3.9.jar:ae.class */
public class XmppProtocol extends Account {

    /* renamed from: a */
    public final Vector elementQueue;

    /* renamed from: f */
    private Object[] parserState;

    /* renamed from: g */
    private Object[] authState;

    /* renamed from: b */
    public String serverAddress;

    /* renamed from: c */
    public int serverPort;

    /* renamed from: d */
    public Object authResult;

    /* renamed from: h */
    private Throwable lastException;

    /* renamed from: e */
    public String serverResourceId;

    public XmppProtocol(int i, String str, String str2) {
        super(i, str, str2);
        this.configFlags = 1;
        this.elementQueue = NetworkUtils.newVector();
        XmppContactGroup defaultGrp = new XmppContactGroup(this, 0, AppState.getString(StateKeys.STR_GROUP_DEFAULT));
        defaultGrp.isSpecial = true;
        this.defaultGroup = defaultGrp;
        this.serverAddress = AppState.emptyStr;
        this.serverResourceId = AppState.emptyStr;
    }

    @Override // p000.Account
    /* renamed from: a */
    public int getType() {
        return 2;
    }

    /* renamed from: r */
    private String generateMessageId() {
        StringBuffer sb = NetworkUtils.newStringBuffer().append('m');
        int i = this.state + 1;
        this.state = i;
        return NetworkUtils.bufToStringCached(sb.append(i));
    }

    public XmppProtocol(ByteBuffer buffer) {
        super(buffer);
        this.elementQueue = NetworkUtils.newVector();
        int groupCount = buffer.readInt();
        while (true) {
            groupCount--;
            if (groupCount < 0) {
                break;
            } else {
                addGroup((ContactGroup) new XmppContactGroup(this, buffer));
            }
        }
        XmppContactGroup defaultGrp = new XmppContactGroup(this, buffer);
        int contactIdx = Utils.vectorSize(defaultGrp.contacts);
        while (true) {
            contactIdx--;
            if (contactIdx < 0) {
                defaultGrp.isSpecial = true;
                this.defaultGroup = defaultGrp;
                this.serverAddress = buffer.readWideStr();
                this.serverPort = buffer.readShortBE();
                this.serverResourceId = buffer.readWideStr();
                return;
            }
            ((XmppContact) defaultGrp.contacts.elementAt(contactIdx)).online = true;
        }
    }

    @Override // p000.Account
    /* renamed from: a */
    public final Account serializeAccount(ByteBuffer buffer, boolean z, boolean z2) {
        super.serializeAccount(buffer, z, z2);
        buffer.writeStringLatin1(this.serverAddress).writeShortBE(this.serverPort).writeStringLatin1(this.serverResourceId);
        return this;
    }

    /* renamed from: f */
    public boolean mo83f() {
        return false;
    }

    /* renamed from: j */
    public String mo84j() {
        return StringUtils.getDomain(this.login);
    }

    @Override // p000.Account
    /* renamed from: b */
    public final ContactGroup createOnlineGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(StateKeys.STR_GROUP_NOT_IN_LIST));
    }

    @Override // p000.Account
    /* renamed from: c */
    public final ContactGroup createBlockedGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(StateKeys.STR_GROUP_TEMPORARY));
    }

    @Override // p000.Account
    /* renamed from: d */
    public final ContactGroup createOfflineGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(StateKeys.STR_GROUP_IGNORE));
    }

    @Override // p000.Account
    /* renamed from: e */
    public final ContactGroup createSpecialGroup() {
        return new XmppContactGroup(this, -1, AppState.getString(StateKeys.STR_GROUP_PHONE_CONTACTS));
    }

    @Override // p000.Account
    /* renamed from: g */
    public final int getDefaultError() {
        closeConnection();
        this.deadline = 0L;
        this.timeout = 0L;
        markAllRead();
        return 0;
    }

    /* renamed from: a */
    private final int sendRawBytes(byte[] bArr) {
        long j = AppState.getBool(StateKeys.FLAG_WIFI_CONNECTION) ? 25000L : 60000L;
        this.timeout = j;
        this.deadline = System.currentTimeMillis() + j;
        return sendData(new ByteBuffer().writeBytes(bArr));
    }

    /* renamed from: a */
    private int sendXmlElement(XmlElement element) {
        return sendData(new ByteBuffer().writeUTFNoLen(element.toString()));
    }

    /* renamed from: b */
    private int sendElementWithId(XmlElement element) {
        return sendXmlElement(element.setAttrValue(131550, generateMessageId()));
    }

    /* renamed from: s */
    private final void clearParserState() {
        this.dataBuffer.clear();
        Object[] state = this.parserState;
        if (state != null) {
            state[2] = null;
            state[1] = null;
            state[0] = null;
        }
    }

    /* renamed from: a */
    public final void setException(Throwable th) {
        if (this.progress == 2) {
            this.lastException = th;
        }
    }

    /* renamed from: a */
    public final void setAuthParameters(String str, int i) {
        if (this.progress == 2) {
            this.serverPort = i;
            this.serverAddress = str;
        }
    }

    /* renamed from: t */
    private final boolean isMailRuXmpp() {
        return getType() == 2 && this.login.endsWith(AppState.getString(StateKeys.STR_RES_PROTOCOL_ATTR_3));
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x0236  */
    @Override // p000.Account
    /* renamed from: i */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void loadData() throws Throwable {
        RemoteLogger.log("XMPP", "loadData progress=" + this.progress + " login=" + this.login);
        boolean handledPing;
        boolean handledSession;
        boolean handledBind;
        boolean handledDiscoInfo;
        XmlElement rosterElement;
        String bodyText;
        String subjectText;
        Object[] taskArgs;
        switch (this.progress) {
            case 0:
                clearParserState();
                Object[] prevAuthState = this.authState;
                if (prevAuthState != null) {
                    prevAuthState[0] = null;
                }
                this.authState = null;
                this.lastException = null;
                this.msgCount = 0;
                break;
            case 1:
                this.msgCount = 10;
                if (mo83f()) {
                    if (Utils.nonEmpty(this.serverResourceId)) {
                        this.progress = 3;
                    } else {
                        this.progress = 2;
                        if (this.login.indexOf(64) <= 0) {
                            ((XmppMailRuProtocol) this).serverResourceId = this.login;
                            taskArgs = null;
                        } else {
                            String hashPrefix = StringUtils.prefix(Utils.generateRandomHash(), 16);
                            Object[] authArgs = {this, hashPrefix, new ByteBuffer().writeCompressed(5249005).writeRawString(hashPrefix).readAllByteStr(), ResourceManager.integerCache[0], this.login, this.password};
                            new AsyncTask(34, authArgs);
                            taskArgs = authArgs;
                        }
                        this.authState = taskArgs;
                    }
                } else if (Utils.nonEmpty(this.serverAddress)) {
                    this.progress = 3;
                } else {
                    this.progress = 2;
                    Object[] resolveArgs = {this};
                    new AsyncTask(33, resolveArgs);
                    this.authState = resolveArgs;
                }
                AppController.needsRepaint = true;
                break;
            case 2:
                this.msgCount = 20;
                if (mo83f()) {
                    if (Utils.nonEmpty(this.serverResourceId)) {
                        this.progress = 3;
                    } else if (this.lastException != null) {
                        handleException(this.lastException);
                    }
                } else if (Utils.nonEmpty(this.serverAddress)) {
                    this.progress = 3;
                } else if (this.lastException != null) {
                    handleException(this.lastException);
                }
                AppController.needsRepaint = true;
                break;
            case 3:
                this.msgCount = 30;
                this.state = 0;
                this.connection = new ConnectionThread(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(this.serverAddress).append(':').append(this.serverPort)));
                this.progress = 4;
                if (isMailRuXmpp()) {
                    new AsyncTask(30, new Object[]{this, new ByteBuffer().writeCompressed(2365173).writeCompressed(3807001).writeRawString(this.shortName).writeCompressed(1316577).writeRawString(this.password).readAllByteStr(), ResourceManager.integerCache[0]});
                }
                AppController.needsRepaint = true;
                break;
            case 4:
                clearParserState();
                this.msgCount = 40;
                if (!isMailRuXmpp()) {
                    if (this.connection.getState() == 2) {
                        this.msgCount = 50;
                        this.progress = 5;
                        Object[] parserArgs = new Object[3];
                        parserArgs[0] = this;
                        parserArgs[1] = new ByteBuffer();
                        parserArgs[2] = null;
                        parserArgs[2] = new XmlParser(parserArgs);
                        new AsyncTask(29, parserArgs);
                        this.parserState = parserArgs;
                        AppController.needsRepaint = true;
                        sendPresenceSubscription();
                    } else if (this.connection.getState() <= 0) {
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
                AccountManager.updateAccountStatus(this, this.dataBuffer.length);
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
                    if (!StringUtils.matchesKey(857301, tagName)) {
                        if (StringUtils.matchesKey(988737, tagName)) {
                            XmlElement mechanisms = element.findByName(AppState.getString(StateKeys.STR_RES_PROTOCOL_ATTR_1));
                            if (mechanisms != null) {
                                XmlElement authElement = XmlElement.createFromState(263757).addIdAttr(2102710);
                                String plainMech = AppState.getString(StateKeys.STR_RES_PROTOCOL_ATTR_2);
                                if (mechanisms.findChildByText(plainMech) != null) {
                                    sendXmlElement(authElement.setAttrValue(594936, plainMech));
                                } else {
                                    String xTokenMech = AppState.getString(StateKeys.STR_RES_URL_TEMPLATE_3);
                                    if (mechanisms.findChildByText(xTokenMech) != null) {
                                        sendXmlElement(authElement.setAttrValue(594936, xTokenMech).appendText((Object) new ByteBuffer().writeByte(0).writeRawString(this.shortName).writeByte(0).writeRawString((String) this.authResult).toBase64()));
                                    } else {
                                        String digestMech = AppState.getString(StateKeys.STR_RES_URL_PARAM_2);
                                        if (mechanisms.findChildByText(digestMech) != null) {
                                            sendXmlElement(authElement.setAttrValue(594936, digestMech).appendText((Object) new ByteBuffer().writeUTFNoLen(new ByteBuffer().writeRawString(this.shortName).writeByte(64).writeRawString(this.serverAddress).readAllByteStr()).writeByte(0).writeUTFNoLen(this.shortName).writeByte(0).writeUTFNoLen(this.password).toBase64()));
                                        }
                                    }
                                }
                            } else if (element.findByName(AppState.getString(StateKeys.STR_RES_DATE_SEPARATOR)) != null) {
                                XmlElement bindRequest = XmlElement.createFromState(136604).addNameAttr(198841);
                                bindRequest.addChildWithId(267762, 2102742).addTextChild(AppState.getString(StateKeys.STR_RES_URL_PATH_2), AppState.getString(StateKeys.STR_RES_DOT));
                                sendElementWithId(bindRequest);
                                this.msgCount = 60;
                            } else {
                                IOUtils.postAccountError(this, 1033);
                                closeConnection();
                                this.lastError = getDefaultError();
                            }
                        } else if (StringUtils.matchesKey(595536, tagName)) {
                            XmlElement challengeResponse = XmlElement.createFromState(529537).addIdAttr(2102710);
                            String decoded = Base64.decode(StringUtils.fromBuffer(element.textContent)).getStringAndClear();
                            int idx = decoded.indexOf(AppState.getString(StateKeys.STR_RES_LABEL_TEXT_1));
                            if (idx >= 0) {
                                int nonceStart = idx + 7;
                                String username = mo128m();
                                String password = this.password;
                                String realm = this.serverAddress;
                                String nonce = StringUtils.substring(decoded, nonceStart, decoded.indexOf(34, nonceStart));
                                ByteBuffer digestBuffer = new ByteBuffer().writeCompressed(660529).writeRawString(username).writeCompressed(595003).writeRawString(realm).writeCompressed(595012).writeRawString(nonce).writeCompressed(1446989);
                                String cnonce = Utils.generateRandomHash();
                                challengeResponse.appendText((Object) digestBuffer.writeRawString(cnonce).writeCompressed(1840227).writeRawString(realm).writeCompressed(791679).writeRawString(new ByteBuffer().writeRawString(new ByteBuffer().writeRawString(username).writeByte(58).writeRawString(realm).writeByte(58).writeRawString(password).encryptMD5().writeByte(58).writeRawString(nonce).writeByte(58).writeRawString(cnonce).encryptMD5().toHexString()).writeByte(58).writeRawString(nonce).writeCompressed(660619).writeRawString(cnonce).writeByte(58).writeCompressed(263757).writeByte(58).writeRawString(new ByteBuffer().writeCompressed(1184917).writeRawString(realm).encryptMD5().toHexString()).encryptMD5().toHexString()).writeCompressed(988327).toBase64());
                            }
                            sendXmlElement(challengeResponse);
                        } else if (StringUtils.matchesKey(464473, tagName)) {
                            sendPresenceSubscription();
                        } else if (StringUtils.matchesKey(530016, tagName)) {
                            String nameAttr = element.getNameAttr();
                            String presenceType = nameAttr != null ? nameAttr : AppState.getString(StateKeys.STR_RES_XMPP_STANZA_1);
                            String jid = extractBareJid(element.getIntAttribute(262852));
                            if (jid != null) {
                                XmppContact contact = findContactByJid(jid);
                                if (StringUtils.matchesKey(594926, presenceType)) {
                                    if (contact == null) {
                                        ContactGroup group = this.defaultGroup;
                                        XmppContact newContact = new XmppContact(this, jid, extractDisplayName(element, jid), null);
                                        newContact.online = true;
                                        contact = newContact;
                                        group.addContact((Object) newContact);
                                    }
                                    contact.updateFromPresence(presenceType, element);
                                    ResourceManager.playNotificationSound(3);
                                    onMessage(jid, 0L, AppState.getString(StateKeys.STR_XMPP_AUTH_REQUEST));
                                } else if (contact != null) {
                                    contact.updateFromPresence(presenceType, element);
                                }
                            }
                        } else if (StringUtils.matchesKey(464488, tagName)) {
                            String senderJid = extractBareJid(element.getIntAttribute(262852));
                            if (findContactByJid(senderJid) != null) {
                                StringBuffer sb = NetworkUtils.newStringBuffer();
                                XmlElement subjectChild = element.findChildByKey(464558);
                                if (subjectChild != null && (subjectText = StringUtils.fromBuffer(subjectChild.textContent)) != null) {
                                    sb.append(subjectText).append('\n');
                                }
                                XmlElement bodyChild = element.findChildByKey(267946);
                                if (bodyChild != null && (bodyText = StringUtils.fromBuffer(bodyChild.textContent)) != null) {
                                    sb.append(bodyText);
                                }
                                String messageText = NetworkUtils.bufToStringCached(sb);
                                if (messageText.length() > 0) {
                                    onMessage(senderJid, 0L, messageText);
                                }
                            }
                        } else if (StringUtils.matchesKey(464495, tagName)) {
                            handleComplete();
                        } else if (StringUtils.matchesKey(136604, element.tagName)) {
                            if (element.findByAttrs(267810, 857625) == null || !StringUtils.matchesKey(196633, element.getNameAttr())) {
                                handledPing = false;
                            } else {
                                XmlElement pingReply = element.cloneElement();
                                pingReply.children = null;
                                sendXmlElement(pingReply);
                                handledPing = true;
                            }
                            if (!handledPing && !processRosterUpdate(element)) {
                                if (element.findByAttrs(267762, 2102742) == null || !StringUtils.matchesKey(398982, element.getNameAttr())) {
                                    handledSession = false;
                                } else {
                                    sendElementWithId(XmlElement.createFromState(136604).addNameAttr(198841).addSimpleChild(461668, 2299382));
                                    this.msgCount = 70;
                                    handledSession = true;
                                }
                                if (!handledSession) {
                                    if (this.msgCount == 70 && StringUtils.matchesKey(398982, element.getNameAttr())) {
                                        sendElementWithId(XmlElement.createFromState(136604).addNameAttr(196633).addSimpleChild(333360, 1054101));
                                        this.msgCount = 80;
                                        handledBind = true;
                                    } else {
                                        handledBind = false;
                                    }
                                    if (!handledBind) {
                                        XmlElement discoInfoElement = element.findByAttrs(333360, 1119653);
                                        if (discoInfoElement == null || !StringUtils.matchesKey(196633, element.getNameAttr())) {
                                            handledDiscoInfo = false;
                                        } else {
                                            discoInfoElement.setIntAttribute(262601, 1119195).setIntAttribute(459728, 1375).setIntAttribute(133230, 264455);
                                            sendXmlElement(element.cloneElement());
                                            handledDiscoInfo = true;
                                        }
                                        if (!handledDiscoInfo && (rosterElement = element.findByAttrs(333360, 1054101)) != null) {
                                            String iqType = element.getNameAttr();
                                            if (StringUtils.matchesKey(198841, iqType)) {
                                                parseRosterItems(rosterElement);
                                                XmlElement ackElement = element.cloneElement();
                                                ackElement.children = null;
                                                sendXmlElement(ackElement);
                                            } else if (StringUtils.matchesKey(398982, iqType)) {
                                                removeAllContacts();
                                                parseRosterItems(rosterElement);
                                                if (Utils.vectorSize(this.groups) == 0) {
                                                    this.groups.addElement(new XmppContactGroup(this, 1, AppState.getString(StateKeys.STR_RES_MENU_ITEM_2)));
                                                }
                                                this.progress = 100;
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
        if (this.lastError != 0 && this.connection != null && this.connection.getState() == 0) {
            this.progress = 0;
            closeConnection();
            this.lastError = getDefaultError();
        }
        if (this.timeout <= 0 || !AppController.isTimerExpired(this.deadline)) {
            return;
        }
        sendRawBytes(new byte[]{32});
    }

    /* renamed from: b */
    private void handleException(Throwable th) {
        IOUtils.postAccountMessage(this, th.toString());
        closeConnection();
        this.lastError = getDefaultError();
    }

    /* renamed from: b */
    public final void updatePresenceStatus(String str, int i) {
        if (isConnected()) {
            sendXmlElement(XmlElement.createFromState(530016).setAttrValue(131590, str).addNameAttr(i == 0 ? 594926 : i == 1 ? 660462 : 791532).addChild(XmlElement.createFromState(267628).addIdAttr(2037073).appendText((Object) this.displayName)));
        } else {
            IOUtils.postEvent((Object) AppState.getString(StateKeys.STR_XMPP_EVENT));
        }
    }

    @Override // p000.Account
    /* renamed from: c */
    public final void onError(int i) {
        int statusMode;
        switch (i) {
            case 0:
                statusMode = 1;
                break;
            case 1:
                statusMode = 4;
                break;
            case 2:
                statusMode = 2;
                break;
            case 3:
                statusMode = 5;
                break;
            case 4:
                statusMode = 3;
                break;
            default:
                disconnect();
                return;
        }
        if (isConnected()) {
            sendError(statusMode);
            return;
        }
        this.configFlags = statusMode;
        if (isConnecting()) {
            return;
        }
        connect(0);
    }

    /* renamed from: h */
    private final void sendError(int i) {
        if (mo83f()) {
            i = 1;
        }
        this.lastError = i;
        XmlElement presence = XmlElement.createFromState(530016);
        int statusStringId = 0;
        switch (i) {
            case 1:
                statusStringId = 642;
                break;
            case 2:
                presence.setIntAttribute(267927, 267829);
                statusStringId = 644;
                break;
            case 3:
                presence.addNameAttr(594975);
                break;
            case 4:
                presence.setIntAttribute(267927, 265215);
                statusStringId = 643;
                break;
            case 5:
                presence.setIntAttribute(267927, 202299);
                statusStringId = 645;
                break;
            case 6:
                presence.setIntAttribute(267927, 136761);
                statusStringId = 648;
                break;
        }
        if (statusStringId != 0) {
            presence.addTextChild(AppState.getString(StateKeys.STR_RES_URL_PATH_3), AppState.getString(StateKeys.STR_RES_COLON));
            presence.setIntAttribute(394658, statusStringId);
            presence.addChildWithId(267628, 2037073).appendText((Object) this.displayName);
        }
        sendXmlElement(presence);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int setCredentials(String str, String str2) {
        int result = super.setCredentials(str, str2);
        if (result != 0) {
            return result;
        }
        if (mo83f()) {
            this.serverResourceId = AppState.emptyStr;
            return 0;
        }
        this.serverAddress = AppState.emptyStr;
        return 0;
    }

    /* renamed from: b */
    public final int setStatusMode(int i) {
        this.configFlags = i;
        if (isConnected()) {
            sendError(i);
            return 0;
        }
        if (isConnecting()) {
            return 487;
        }
        return connect(0);
    }

    @Override // p000.Account
    /* renamed from: c */
    public final int validateContactDelete(Contact contact) {
        return 1032;
    }

    @Override // p000.Account
    /* renamed from: d */
    public final int validateContactBlock(Contact contact) {
        return 1032;
    }

    @Override // p000.Account
    /* renamed from: e */
    public final int validateContactUnblock(Contact contact) {
        return 1032;
    }

    @Override // p000.Account
    /* renamed from: b */
    public final Contact newContact(String str) {
        return null;
    }

    @Override // p000.Account
    /* renamed from: h */
    public int getIconId() {
        if (this.progress < 1 || this.progress >= 100) {
            return getIconForError(this.lastError);
        }
        return 382;
    }

    /* renamed from: u */
    private void sendPresenceSubscription() {
        sendRawBytes(new ByteBuffer().writeCompressed(8131775).writeRawString(mo84j()).writeCompressed(136911).toByteArray());
    }

    @Override // p000.Account
    /* renamed from: p */
    public int mo110p() {
        return 398518;
    }

    /* renamed from: f */
    private XmppContact findContactByJid(String str) {
        return (XmppContact) getContact((Object) str);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateModify(Contact contact, Object[] params) {
        int result = super.validateModify(contact, params);
        return 0 != result ? result : createRosterUpdate(((XmppContact) contact).jabberId, (String) params[0], findGroup(contact).name);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateMove(Contact contact, ContactGroup fromGroup, ContactGroup toGroup) {
        int result = super.validateMove(contact, fromGroup, toGroup);
        return 0 != result ? result : createRosterUpdate(((XmppContact) contact).jabberId, contact.displayName, toGroup.name);
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateDelete(Contact contact) {
        if (!isConnected()) {
            return 299;
        }
        AppState.pool[StateKeys.SLOT_REG_PARAM_2] = new Object[]{generateMessageId(), ((XmppContact) contact).getContactInfo()};
        this.state--;
        return sendElementWithId(XmlElement.createFromState(136604).addNameAttr(196633).setAttrValue(131590, contact.getIdentifier()).addSimpleChild(333452, 661030));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateObject(Object obj) {
        return 0;
    }

    /* renamed from: k */
    public final int addNewContact() {
        if (!isConnected()) {
            IOUtils.postEvent((Object) AppState.getString(StateKeys.STR_XMPP_EVENT));
            return 0;
        }
        String contactJid = Utils.defaultStr(AppState.getString(StateKeys.SLOT_CONTACT_JID));
        createRosterUpdate(contactJid, Utils.defaultStr(AppState.getString(StateKeys.SLOT_DISPLAY_NAME)), ((ContactGroup) AppState.getVector(StateKeys.VEC_GROUP_LIST).elementAt(AppState.getInt(StateKeys.INT_GROUP_OPERATION_RESULT))).name);
        updatePresenceStatus(contactJid, 0);
        updatePresenceStatus(contactJid, 1);
        return 0;
    }

    /* renamed from: a */
    private final int createRosterUpdate(String str, String str2, String str3) {
        XmlElement queryElement = XmlElement.createFromState(333360).addIdAttr(1054101);
        XmlElement itemElement = XmlElement.createFromState(267942).setAttrValue(202421, str).setAttrValue(262601, str2).setAttrValue(792248, str2 == null ? AppState.getString(StateKeys.STR_RES_LABEL_TEXT_2) : null);
        if (str3 != null && !StringUtils.matchesKey(459528, str3)) {
            itemElement.addTextChild(AppState.getString(StateKeys.STR_RES_URL_PARAM_4), str3);
        }
        return sendElementWithId(XmlElement.createFromState(136604).addNameAttr(198841).addChild(queryElement.addChild(itemElement)));
    }

    @Override // p000.Account
    /* renamed from: b */
    public final int validateResend(Contact contact) {
        if (isConnected()) {
            createRosterUpdate(contact.getIdentifier(), (String) null, (String) null);
            return 0;
        }
        IOUtils.postEvent((Object) AppState.getString(StateKeys.STR_XMPP_EVENT));
        return 0;
    }

    /* renamed from: a */
    public final int updateContactPresence(XmppContact contact, int i) {
        if (!isConnected()) {
            return 299;
        }
        String jid = contact.jabberId;
        String name = contact.displayName;
        ContactGroup group = findGroup(contact);
        createRosterUpdate(jid, name, (group == this.onlineGroup || contact.online) ? AppState.getString(StateKeys.STR_RES_MENU_ITEM_2) : group.name);
        return i;
    }

    @Override // p000.Account
    /* renamed from: l */
    public final int disconnect() {
        int result = super.disconnect();
        if (0 != result) {
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

    /* renamed from: g */
    private XmppContactGroup findGroupByName(String str) {
        XmppContactGroup group;
        Vector groups = this.groups;
        int i = Utils.vectorSize(groups);
        do {
            i--;
            if (i < 0) {
                return null;
            }
            group = (XmppContactGroup) groups.elementAt(i);
        } while (!StringUtils.equals(str, group.name));
        return group;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupCreate(String str) {
        int result = super.validateGroupCreate(str);
        if (0 != result) {
            return result;
        }
        if (findGroupByName(str) != null) {
            return 0;
        }
        this.groups.addElement(new XmppContactGroup(this, 1, str));
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupDelete(ContactGroup group) {
        int result = super.validateGroupDelete(group);
        if (0 != result) {
            return result;
        }
        this.groups.removeElement(group);
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateGroupRename(ContactGroup group, String str) {
        int result = super.validateGroupRename(group, str);
        if (0 != result) {
            return result;
        }
        if (Utils.vectorSize(group.contacts) != 0) {
            return 1032;
        }
        group.setNameIfChanged(str);
        return 0;
    }

    @Override // p000.Account
    /* renamed from: a */
    public final int validateSend(Contact contact, String str, long j) {
        int result = super.validateSend(contact, str, j);
        if (0 != result) {
            return result;
        }
        this.sentCount++;
        return sendXmlElement(XmlElement.createFromState(464488).setAttrValue(131590, contact.getIdentifier()).addNameAttr(265215).addChild(XmlElement.createFromState(267946).appendText((Object) str)).addSimpleChild(398993, 2430320));
    }

    /* renamed from: c */
    private final boolean processRosterUpdate(XmlElement element) {
        if (element.findByAttrs(333350, 661030) == null) {
            return false;
        }
        if (!StringUtils.matchesKey(398982, element.getNameAttr())) {
            if (!StringUtils.matchesKey(333441, element.getNameAttr())) {
                return false;
            }
            try {
                Object[] pendingRequest = AppState.getObjectArray(StateKeys.SLOT_REG_PARAM_2);
                if (((String) pendingRequest[0]).equals(element.getIntAttribute(131550))) {
                    AppState.pool[StateKeys.SLOT_REG_PARAM_1] = ((ContactInfo) pendingRequest[1]).setDescriptionBis(element.toString());
                }
                return true;
            } catch (Throwable unused) {
                return true;
            }
        }
        try {
            Object[] pendingRequest = AppState.getObjectArray(StateKeys.SLOT_REG_PARAM_2);
            if (((String) pendingRequest[0]).equals(element.getIntAttribute(131550))) {
                ContactInfo contactInfo = ((ContactInfo) pendingRequest[1]).setDescriptionBis(NetworkUtils.bufToStringCached(buildContactDescription(NetworkUtils.newStringBuffer(), element)));
                Image avatar = extractImageFromElement(element);
                if (avatar != null) {
                    contactInfo.put(ResourceManager.integerOf(25), avatar);
                }
                AppState.pool[StateKeys.SLOT_REG_PARAM_1] = contactInfo;
            }
            return true;
        } catch (Throwable unused2) {
            return true;
        }
    }

    /* renamed from: d */
    private final void parseRosterItems(XmlElement element) {
        Vector groups = this.groups;
        Vector children = element.children;
        int i = Utils.vectorSize(children);
        while (true) {
            i--;
            if (i < 0) {
                return;
            }
            XmlElement itemElement = (XmlElement) children.elementAt(i);
            if (StringUtils.matchesKey(267942, itemElement.tagName)) {
                String jid = itemElement.getIntAttribute(202421);
                String subscription = itemElement.getIntAttribute(792248);
                itemElement.getIntAttribute(202403);
                String displayName = itemElement.getIntAttribute(262601);
                boolean isRemoved = StringUtils.matchesKey(399049, subscription);
                if (displayName == null) {
                    displayName = jid;
                }
                String groupName = itemElement.getChildText(AppState.getString(StateKeys.STR_RES_URL_PARAM_4));
                String resolvedGroupName = groupName;
                if (!Utils.nonEmpty(groupName)) {
                    resolvedGroupName = AppState.getString(StateKeys.STR_RES_MENU_ITEM_2);
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

    /* renamed from: m */
    public String mo128m() {
        return this.shortName;
    }

    /* renamed from: a */
    private static String extractDisplayName(XmlElement element, String str) {
        try {
            return StringUtils.fromBuffer(element.findChildByKey(267628).textContent);
        } catch (Throwable unused) {
            return str;
        }
    }

    /* renamed from: h */
    private static String extractBareJid(String str) {
        if (str == null) {
            return null;
        }
        int idx = str.indexOf(47);
        return idx <= 0 ? str : StringUtils.prefix(str, idx);
    }

    /* renamed from: d */
    public static final int getIconForError(int i) {
        switch (i) {
            case 0:
                return 381;
            case 1:
                return 383;
            case 2:
                return 16318847;
            case 3:
                return 16515455;
            case 4:
                return 16384383;
            case 5:
                return 16449919;
            default:
                return 16580991;
        }
    }

    /* renamed from: e */
    private final Image extractImageFromElement(XmlElement element) {
        Image image;
        String text;
        if (StringUtils.matchesKey(398966, element.tagName) && (text = StringUtils.fromBuffer(element.textContent)) != null) {
            String trimmed = Utils.trimAll(text);
            if (Utils.nonEmpty(trimmed)) {
                try {
                    return Base64.decode(trimmed).toImage();
                } catch (Throwable unused) {
                }
            }
        }
        int i = Utils.vectorSize(element.children);
        do {
            i--;
            if (i < 0) {
                return null;
            }
            image = extractImageFromElement(element.getChildAt(i));
        } while (image == null);
        return image;
    }

    /* renamed from: a */
    private final StringBuffer buildContactDescription(StringBuffer sb, XmlElement element) {
        if (!StringUtils.matchesKey(333436, element.tagName)) {
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
}
