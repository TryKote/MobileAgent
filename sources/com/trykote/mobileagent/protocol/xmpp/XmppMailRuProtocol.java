package com.trykote.mobileagent.protocol.xmpp;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;

public final class XmppMailRuProtocol extends XmppProtocol {

    private static final int DEFAULT_PORT = 43289;
    private static final int ICON_MAILRU_START = 381;
    private static final int ICON_MAILRU_END = 384;
    private static final int ICON_MAILRU_OFFSET = 4;

    private static final int HTTP_OK = 200;
    private static final int HTTP_FORBIDDEN = 403;

    private static final char CHAR_AT = '@';

    public XmppMailRuProtocol(int id, String login, String password) {
        super(id, login, password);
        this.serverAddress = ResourceAccessor.str(PackedStringKeys.HOST_VKMESSENGER);
        this.serverPort = DEFAULT_PORT;
    }

    @Override
    public final int getType() {
        return TYPE_XMPP_MAILRU;
    }

    public XmppMailRuProtocol(ByteBuffer buf) {
        super(buf);
        this.serverAddress = ResourceAccessor.str(PackedStringKeys.HOST_VKMESSENGER);
        this.serverPort = DEFAULT_PORT;
    }

    @Override
    public final int getIconId() {
        int iconId = super.getIconId();
        int baseIcon = iconId & 0xFFFF;
        return (baseIcon < ICON_MAILRU_START || baseIcon > ICON_MAILRU_END) ? iconId : iconId + ICON_MAILRU_OFFSET;
    }

    @Override
    public final boolean isMailRuVariant() {
        return true;
    }

    @Override
    public final String getStreamDomain() {
        return this.serverAddress;
    }

    @Override
    public final String getAuthUsername() {
        return this.serverResourceId;
    }

    @Override
    public final int getSessionStringKey() {
        return 595126;
    }

    private static final int getAccountType() {
        Account account = AppState.getAccount();
        return account != null ? account.getType() : SessionState.getProtocolType();
    }

    public static final void showLoginScreen() {
        if (getAccountType() == TYPE_MMP) {
            Account account = AppState.getAccount();
            if (account != null && account.isConnecting()) {
                NotificationHelper.showMessageById(300);
                return;
            }
            clearLoginFields();
            if (account != null) {
                ChatState.setChatName(account.login);
                RegistrationState.setPassword(account.password);
            }
            Screens.xmppLogin(null).show();
            return;
        }
        if (getAccountType() == TYPE_XMPP) {
            XmppProtocol xmppAccount = (XmppProtocol) AppState.getAccount();
            if (xmppAccount != null && xmppAccount.isConnecting()) {
                NotificationHelper.showMessageById(300);
                return;
            }
            clearLoginFields();
            SessionState.setServerIndex(0);
            if (xmppAccount != null) {
                String loginStr = xmppAccount.login;
                Vector parts = Utils.splitNonEmpty(ResourceAccessor.str(StringResKeys.STR_SERVER_LIST), '\0');
                int count = 0;
                for (int ci = Utils.vectorSize(parts) - 1; ci >= 1; ci--) {
                    int idx = loginStr.indexOf((String) parts.elementAt(ci));
                    if (idx >= 0) {
                        loginStr = StringUtils.prefix(loginStr, idx);
                        count = ci;
                        break;
                    }
                }
                SessionState.setServerIndex(count);
                ChatState.setChatName(loginStr);
                RegistrationState.setPassword(xmppAccount.password);
                ContactState.setDisplayName(xmppAccount.displayName);
            } else if (TestConfig.ENABLED && TestConfig.ACCOUNT_TYPE == TYPE_XMPP) {
                ChatState.setChatName(TestConfig.LOGIN);
                RegistrationState.setPassword(TestConfig.PASSWORD);
            }
            Screens.xmppLoginAlt(null).show();
            return;
        }
        if (getAccountType() == TYPE_XMPP_MAILRU) {
            XmppMailRuProtocol mailRuAccount = (XmppMailRuProtocol) AppState.getAccount();
            if (mailRuAccount != null && mailRuAccount.isConnecting()) {
                NotificationHelper.showMessageById(300);
                return;
            }
            clearLoginFields();
            if (mailRuAccount != null) {
                ChatState.setChatName(mailRuAccount.login);
                RegistrationState.setPassword(mailRuAccount.password);
                ContactState.setDisplayName(mailRuAccount.displayName);
            }
            Screens.xmppLoginAlt2(null).show();
            return;
        }
        clearLoginFields();
        SessionState.setServerIndex(0);
        Account currentAccount = AppState.getAccount();
        if (currentAccount != null) {
            RegistrationState.setPassword(currentAccount.password);
            String login = currentAccount.login;
            Vector domains = Utils.splitNonEmpty(ResourceAccessor.str(StringResKeys.STR_DOMAIN_LIST), '\0');
            int size = domains.size();
            for (int i = 0; i <= size; i++) {
                if (i == size) {
                    ChatState.setChatName(login);
                    break;
                }
                int idx = login.indexOf((String) domains.elementAt(i));
                if (idx >= 0) {
                    SessionState.setServerIndex(i);
                    ChatState.setChatName(StringUtils.prefix(login, idx));
                    break;
                }
            }
        }
        Screens.xmppContextMenu(null).show();
    }

    public static final int performLogin() {
        ScreenManager.processScreenForm();
        if (getAccountType() == TYPE_MMP) {
            Account account = AppState.getAccount();
            String login = getLoginLowerCase();
            int errorCode = AccountManager.validateCredentials(TYPE_MMP, account, login, Utils.defaultStr(RegistrationState.getPassword()));
            if (errorCode != 0) {
                return NotificationHelper.showError(errorCode);
            }
            AccountManager.addToAccountSelection(AccountManager.findAccountByLogin(TYPE_MMP, login));
            return 0;
        }
        if (getAccountType() == TYPE_XMPP) {
            return loginXmpp(TYPE_XMPP);
        }
        if (getAccountType() == TYPE_XMPP_MAILRU) {
            SessionState.setServerIndex(0);
            return loginXmpp(TYPE_XMPP_MAILRU);
        }
        String password = Utils.defaultStr(RegistrationState.getPassword());
        String login = getLoginLowerCase();
        String fullLogin = login;
        if (StringUtils.isEmpty(login)) {
            return NotificationHelper.showError(301);
        }
        if (!containsDomainSuffix(fullLogin, StringResKeys.STR_DOMAIN_LIST) && !containsDomainSuffix(fullLogin, StringResKeys.STR_SERVER_SUFFIX_LIST)) {
            fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(StringResKeys.STR_DOMAIN_LIST, SessionState.getServerIndex()));
        }
        if (!isValidUsername(fullLogin)) {
            return NotificationHelper.showError(559);
        }
        int errorCode = AccountManager.validateCredentials(TYPE_MRIM, AppState.getAccount(), fullLogin, password);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        AccountManager.addToAccountSelection(AccountManager.findAccountByLogin(TYPE_MRIM, fullLogin));
        return 0;
    }

    public static final void clearLoginFields() {
        AppState.clearRange(ChatKeys.SLOT_CHAT_NAME, RegistrationKeys.SLOT_PASSWORD);
        ContactState.setDisplayName(null);
    }

    public static final boolean isMailRuDomain(String login) {
        return containsDomainSuffix(login, StringResKeys.STR_DOMAIN_LIST);
    }

    private static final boolean containsDomainSuffix(String login, int domainListKey) {
        Vector parts = Utils.splitNonEmpty(AppState.getString(domainListKey), '\0');
        for (int i = parts.size() - 1; i >= 0; i--) {
            if (login.indexOf((String) parts.elementAt(i)) >= 0) {
                return true;
            }
        }
        ObjectPool.releaseVector(parts);
        return false;
    }

    public static final String getLoginLowerCase() {
        return StringUtils.intern(Utils.defaultStr(ChatState.getChatName()).toLowerCase());
    }

    public static final boolean isValidUsername(String username) {
        for (int i = username.length() - 1; i >= 0; i--) {
            char ch = username.charAt(i);
            if (ch < 'A' || ch > 'Z') {
                if (ch < 'a' || ch > 'z') {
                    if (ch < '0' || ch > '9') {
                        if (ch != '.' && ch != '_' && ch != '-' && ch != '@') {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static final void resolveXmppServer(Object[] taskArgs) {
        try {
            XmppProtocol xmppAccount = (XmppProtocol) taskArgs[0];
            String login = xmppAccount.login;
            String domain = StringUtils.suffix(login, login.indexOf('@') + 1);

            // Skip DNS SRV for IP addresses — connect directly
            if (isIpAddress(domain)) {
                RemoteLogger.log("XMPP", "domain is IP address, skipping SRV: " + domain + ":" + DEFAULT_PORT);
                xmppAccount.setAuthParameters(domain, DEFAULT_PORT);
                return;
            }

            String srvQuery = StringUtils.concatKey(PackedStringKeys.SRV_XMPP_CLIENT_TCP, domain);
            RemoteLogger.log("XMPP", "DNS SRV lookup: " + srvQuery);
            String srvRecord = dnsLookupSrv(srvQuery);
            RemoteLogger.log("XMPP", "DNS SRV result: " + srvRecord);
            if (srvRecord == null || srvRecord.indexOf(':') <= 0) {
                String fallback = xmppAccount.getStreamDomain();
                RemoteLogger.log("XMPP", "SRV failed, using fallback: " + fallback + ":" + DEFAULT_PORT);
                xmppAccount.setAuthParameters(fallback, DEFAULT_PORT);
            } else {
                Vector parts = Utils.splitNonEmpty(srvRecord, ':');
                String host = Utils.getVectorString(parts, 0);
                int port = Integer.parseInt(Utils.getVectorString(parts, 1));
                RemoteLogger.log("XMPP", "SRV resolved: " + host + ":" + port);
                xmppAccount.setAuthParameters(host, port);
                ObjectPool.releaseVector(parts);
            }
        } catch (Throwable th) {
            RemoteLogger.log("XMPP", "resolveXmppServer FAILED", th);
            ((XmppProtocol) taskArgs[0]).setException(th);
        }
    }

    private static boolean isIpAddress(String str) {
        if (str == null || str.length() == 0) return false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c != '.' && (c < '0' || c > '9')) return false;
        }
        return true;
    }

    private static final String dnsLookupSrv(String srvName) {
        final int DNS_BUFFER_SIZE = 512;
        String result;
        DatagramConnection datagramConnection = null;
        try {
            RemoteLogger.log("XMPP", "dnsLookupSrv acquiring network lock");
            NetworkLock.acquireNetworkLock();
            RemoteLogger.log("XMPP", "dnsLookupSrv opening datagram to: " + ResourceAccessor.str(PackedStringKeys.HOST_NSRPUB_DNS));
            Vector parts = Utils.splitNonEmpty(srvName, '.');
            ByteBuffer requestBuf = new ByteBuffer().writeCompressed(PackedStringKeys.MMP_PADDING_12);
            for (int i = 0; i < Utils.vectorSize(parts); i++) {
                requestBuf.writeByteLenStr(Utils.getVectorString(parts, i));
            }
            ObjectPool.releaseVector(parts);
            requestBuf.writeCompressed(PackedStringKeys.MMP_SPACER);
            datagramConnection = (DatagramConnection) IOUtils.registerResource(Connector.open(ResourceAccessor.str(PackedStringKeys.HOST_NSRPUB_DNS)));
            datagramConnection.send(datagramConnection.newDatagram(requestBuf.data, requestBuf.length));
            requestBuf.clear();
            Datagram datagram = datagramConnection.newDatagram(DNS_BUFFER_SIZE);
            datagramConnection.receive(datagram);
            ByteBuffer recordBuf = new ByteBuffer().setData(datagram.getData());
            recordBuf.skip(6);
            if (recordBuf.readShortBE() <= 0) {
                result = null;
            } else {
                recordBuf.readInt();
                while (true) {
                    int labelLen = recordBuf.readUByte();
                    int remaining = labelLen;
                    if (labelLen == 0) {
                        break;
                    }
                    for (int ri = remaining - 1; ri >= 0; ri--) {
                        recordBuf.readUByte();
                    }
                }
                recordBuf.skip(20);
                int port = recordBuf.readShortBE();
                ByteBuffer hostBuf = new ByteBuffer();
                int labelLen = recordBuf.readUByte();
                while (true) {
                    labelLen--;
                    if (labelLen < 0) {
                        int nextLen = recordBuf.readUByte();
                        labelLen = nextLen;
                        if (nextLen == 0) {
                            break;
                        }
                        hostBuf.writeByte('.');
                    } else {
                        hostBuf.writeByte(recordBuf.readUByte());
                    }
                }
                result = hostBuf.writeByte(':').writeIntAsString(port).getStringAndClear();
            }
            String dnsResult = result;
            IOUtils.closeConn((Connection) datagramConnection);
            NetworkLock.releaseNetworkLock();
            return dnsResult;
        } catch (RuntimeException th) {
            IOUtils.closeConn((Connection) datagramConnection);
            NetworkLock.releaseNetworkLock();
            throw th;
        } catch (Throwable th) {
            IOUtils.closeConn((Connection) datagramConnection);
            NetworkLock.releaseNetworkLock();
            throw new RuntimeException(th.toString());
        }
    }

    public static final int loginXmpp(int accountType) {
        String password = Utils.defaultStr(RegistrationState.getPassword());
        String login = getLoginLowerCase();
        String fullLogin = login;
        if (StringUtils.isEmpty(login)) {
            return NotificationHelper.showError(301);
        }
        int serverIndex = SessionState.getServerIndex();
        if (serverIndex != 0 && fullLogin.indexOf(CHAR_AT) < 0) {
            fullLogin = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(fullLogin).append(Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_SERVER_LIST)).elementAt(serverIndex)));
        }
        if (accountType == 2 && fullLogin.indexOf(CHAR_AT) < 0) {
            return NotificationHelper.showError(699);
        }
        int errorCode = AccountManager.validateCredentials(accountType, AppState.getAccount(), fullLogin, password);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        AccountManager.addToAccountSelection(AccountManager.findAccountByLogin(accountType, fullLogin).setDisplayName(Utils.defaultStr(ContactState.getDisplayName())));
        return 0;
    }

    public static final void performXmppAuth(Object[] objArr) {
        try {
            try {
                NetworkLock.acquireNetworkLock();
                HttpClient httpClient = HttpClient.createHttpClient((String) objArr[1], (Account) objArr[0], 0);
                int responseCode = httpClient.getResponseCode();
                if (responseCode == HTTP_OK) {
                    Vector responseLines = Utils.splitNonEmpty(new ByteBuffer(httpClient).getStringAndClear(), '\n');
                    if (((Integer) objArr[2]).intValue() == 0) {
                        objArr[2] = ObjectPool.integerOf(1);
                        objArr[1] = new ByteBuffer().writeCompressed(PackedStringKeys.URL_GOOGLE_ACCOUNTS).writeCompressed(PackedStringKeys.GOOGLE_ISSUE_AUTH_TOKEN).writeObjectStr((String) responseLines.elementAt(0)).writeByte(38).writeObjectStr((String) responseLines.elementAt(1)).readAllByteStr();
                        new AsyncTask(AsyncTaskId.PERFORM_XMPP_AUTH, objArr);
                    } else {
                        setAuthResult(objArr, responseLines.elementAt(0));
                    }
                    ObjectPool.releaseVector(responseLines);
                } else {
                    if (responseCode != HTTP_FORBIDDEN) {
                        throw new Throwable(StringUtils.intern(Integer.toString(responseCode)));
                    }
                    ((XmppProtocol) objArr[0]).handleComplete();
                }
                HttpClient.closeAndUpdateStats(httpClient);
                NetworkLock.releaseNetworkLock();
            } catch (Throwable th) {
                setAuthResult(objArr, th);
                HttpClient.closeAndUpdateStats((HttpClient) null);
                NetworkLock.releaseNetworkLock();
            }
        } catch (RuntimeException e) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw e;
        } catch (Error e) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw e;
        }
    }

    private static final void setAuthResult(Object[] objArr, Object obj) {
        ((XmppProtocol) objArr[0]).authResult = obj;
    }
}
