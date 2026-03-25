package com.trykote.mobileagent.protocol.xmpp;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
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

    private static final int DEFAULT_PORT = 5222;
    private static final int ICON_MAILRU_START = 381;
    private static final int ICON_MAILRU_END = 384;
    private static final int ICON_MAILRU_OFFSET = 4;

    public XmppMailRuProtocol(int id, String login, String password) {
        super(id, login, password);
        this.serverAddress = AppState.getString(StateKeys.STR_RES_URL_TEMPLATE_4);
        this.serverPort = DEFAULT_PORT;
    }

    @Override
    public final int getType() {
        return TYPE_XMPP_MAILRU;
    }

    public XmppMailRuProtocol(ByteBuffer buf) {
        super(buf);
        this.serverAddress = AppState.getString(StateKeys.STR_RES_URL_TEMPLATE_4);
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
        return account != null ? account.getType() : AppState.getInt(StateKeys.INT_PROTOCOL_TYPE);
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
                AppState.setObject(StateKeys.SLOT_CHAT_NAME, account.login);
                AppState.setObject(StateKeys.SLOT_PASSWORD, account.password);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.XMPP_LOGIN));
            return;
        }
        if (getAccountType() == TYPE_XMPP) {
            XmppProtocol xmppAccount = (XmppProtocol) AppState.getAccount();
            if (xmppAccount != null && xmppAccount.isConnecting()) {
                NotificationHelper.showMessageById(300);
                return;
            }
            clearLoginFields();
            AppState.setInt(StateKeys.INT_SERVER_INDEX, 0);
            if (xmppAccount != null) {
                String loginStr = xmppAccount.login;
                Vector parts = Utils.splitNonEmpty(AppState.getString(StateKeys.STR_SERVER_LIST), '\0');
                int count = Utils.vectorSize(parts);
                while (true) {
                    count--;
                    if (count < 1) {
                        break;
                    }
                    int idx = loginStr.indexOf((String) parts.elementAt(count));
                    if (idx >= 0) {
                        loginStr = StringUtils.prefix(loginStr, idx);
                        break;
                    }
                }
                AppState.setInt(StateKeys.INT_SERVER_INDEX, count);
                AppState.setObject(StateKeys.SLOT_CHAT_NAME, loginStr);
                AppState.setObject(StateKeys.SLOT_PASSWORD, xmppAccount.password);
                AppState.setObject(StateKeys.SLOT_DISPLAY_NAME, xmppAccount.displayName);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.XMPP_LOGIN_ALT));
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
                AppState.setObject(StateKeys.SLOT_CHAT_NAME, mailRuAccount.login);
                AppState.setObject(StateKeys.SLOT_PASSWORD, mailRuAccount.password);
                AppState.setObject(StateKeys.SLOT_DISPLAY_NAME, mailRuAccount.displayName);
            }
            ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.XMPP_LOGIN_ALT2));
            return;
        }
        clearLoginFields();
        AppState.setInt(StateKeys.INT_SERVER_INDEX, 0);
        Account account2 = AppState.getAccount();
        if (account2 != null) {
            AppState.setObject(StateKeys.SLOT_PASSWORD, account2.password);
            String login = account2.login;
            Vector domains = Utils.splitNonEmpty(AppState.getString(StateKeys.STR_DOMAIN_LIST), '\0');
            int size = domains.size();
            int i = 0;
            while (true) {
                if (i > size) {
                    break;
                }
                if (i == size) {
                    AppState.setObject(StateKeys.SLOT_CHAT_NAME, login);
                    break;
                }
                int idx = login.indexOf((String) domains.elementAt(i));
                if (idx >= 0) {
                    AppState.setInt(StateKeys.INT_SERVER_INDEX, i);
                    AppState.setObject(StateKeys.SLOT_CHAT_NAME, StringUtils.prefix(login, idx));
                    break;
                }
                i++;
            }
        }
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.XMPP_CONTEXT_MENU));
    }

    public static final int performLogin() {
        ScreenManager.processScreenForm();
        if (getAccountType() == TYPE_MMP) {
            Account account = AppState.getAccount();
            String login = getLoginLowerCase();
            int errorCode = AccountManager.validateCredentials(TYPE_MMP, account, login, Utils.defaultStr(AppState.getString(StateKeys.SLOT_PASSWORD)));
            if (errorCode != 0) {
                return NotificationHelper.showError(errorCode);
            }
            AccountManager.setCurrentAccount(AccountManager.createAccount(TYPE_MMP, login));
            return 0;
        }
        if (getAccountType() == TYPE_XMPP) {
            return IOUtils.loginXmpp(TYPE_XMPP);
        }
        if (getAccountType() == TYPE_XMPP_MAILRU) {
            AppState.setInt(StateKeys.INT_SERVER_INDEX, 0);
            return IOUtils.loginXmpp(TYPE_XMPP_MAILRU);
        }
        String password = Utils.defaultStr(AppState.getString(StateKeys.SLOT_PASSWORD));
        String login = getLoginLowerCase();
        String fullLogin = login;
        if (StringUtils.isEmpty(login)) {
            return NotificationHelper.showError(301);
        }
        if (!containsDomainSuffix(fullLogin, StateKeys.STR_DOMAIN_LIST) && !containsDomainSuffix(fullLogin, StateKeys.STR_SERVER_SUFFIX_LIST)) {
            fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(StateKeys.STR_DOMAIN_LIST, AppState.getInt(StateKeys.INT_SERVER_INDEX)));
        }
        if (!isValidUsername(fullLogin)) {
            return NotificationHelper.showError(559);
        }
        int errorCode = AccountManager.validateCredentials(TYPE_MRIM, AppState.getAccount(), fullLogin, password);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        AccountManager.setCurrentAccount(AccountManager.createAccount(TYPE_MRIM, fullLogin));
        return 0;
    }

    public static final void clearLoginFields() {
        AppState.clearRange(StateKeys.SLOT_CHAT_NAME, StateKeys.SLOT_PASSWORD);
        AppState.clearIndex(StateKeys.SLOT_DISPLAY_NAME);
    }

    public static final boolean isMailRuDomain(String login) {
        return containsDomainSuffix(login, StateKeys.STR_DOMAIN_LIST);
    }

    private static final boolean containsDomainSuffix(String login, int domainListKey) {
        Vector parts = Utils.splitNonEmpty(AppState.getString(domainListKey), '\0');
        int size = parts.size();
        do {
            size--;
            if (size < 0) {
                ObjectPool.releaseVector(parts);
                return false;
            }
        } while (login.indexOf((String) parts.elementAt(size)) < 0);
        return true;
    }

    public static final String getLoginLowerCase() {
        return StringUtils.intern(Utils.defaultStr(AppState.getString(StateKeys.SLOT_CHAT_NAME)).toLowerCase());
    }

    public static final boolean isValidUsername(String username) {
        int length = username.length();
        while (true) {
            length--;
            if (length < 0) {
                return true;
            }
            char ch = username.charAt(length);
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
    }

    public static final void resolveXmppServer(Object[] taskArgs) {
        try {
            String login = ((XmppProtocol) taskArgs[0]).login;
            String srvRecord = dnsLookupSrv(StringUtils.concatKey(PackedStringKeys.SRV_XMPP_CLIENT_TCP, StringUtils.suffix(login, login.indexOf('@') + 1)));
            if (srvRecord == null || srvRecord.indexOf(':') <= 0) {
                XmppProtocol xmppAccount = (XmppProtocol) taskArgs[0];
                xmppAccount.setAuthParameters(xmppAccount.getStreamDomain(), DEFAULT_PORT);
            } else {
                Vector parts = Utils.splitNonEmpty(srvRecord, ':');
                ((XmppProtocol) taskArgs[0]).setAuthParameters(Utils.getVectorString(parts, 0), Integer.parseInt(Utils.getVectorString(parts, 1)));
                ObjectPool.releaseVector(parts);
            }
        } catch (Throwable th) {
            ((XmppProtocol) taskArgs[0]).setException(th);
        }
    }

    private static final String dnsLookupSrv(String srvName) {
        final int DNS_BUFFER_SIZE = 512;
        String result;
        DatagramConnection datagramConnection = null;
        try {
            NetworkLock.acquireNetworkLock();
            Vector parts = Utils.splitNonEmpty(srvName, '.');
            ByteBuffer requestBuf = new ByteBuffer().writeCompressed(PackedStringKeys.MMP_PADDING_12);
            for (int i = 0; i < Utils.vectorSize(parts); i++) {
                requestBuf.writeByteLenStr(Utils.getVectorString(parts, i));
            }
            ObjectPool.releaseVector(parts);
            requestBuf.writeCompressed(PackedStringKeys.MMP_SPACER);
            datagramConnection = (DatagramConnection) IOUtils.registerResource(Connector.open(AppState.getString(StateKeys.STR_RES_VERY_LONG_API_4)));
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
                    while (true) {
                        remaining--;
                        if (remaining < 0) {
                            break;
                        }
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
            throw new RuntimeException(th);
        }
    }
}
