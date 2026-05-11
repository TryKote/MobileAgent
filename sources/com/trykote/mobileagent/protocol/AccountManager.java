package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public final class AccountManager {

    private static final int MSG_NO_ACCOUNTS = 551;
    private static final int MSG_ACCOUNT_ACTIVE = 300;
    private static final int MSG_EMPTY_LOGIN = 301;
    private static final int MSG_EMPTY_PASSWORD = 306;
    private static final int MSG_DUPLICATE_ACCOUNT = 307;
    private static final int MSG_NO_MRIM_ACCOUNTS = 549;
    private static final int MSG_TOGGLE_ALL = 548;
    private static final int ICON_DEFAULT = 16384;

    private static final int ACCOUNT_STORE_KEY = 6513505;
    private static final int SAVE_BUFFER_DESTRUCTIVE = 20480;
    private static final int SAVE_BUFFER_NORMAL = 3072;

    private static final int TYPE_MASK = 7;
    private static final int HAS_PROPERTIES_FLAG = 8;

    private static final int MRIM_STATUS_DISCONNECT = 5;
    private static final int MRIM_STATUS_EMOTICON = 6;
    private static final int[] MRIM_STATUS_MODES = {1, 260, 2, 516, 3};

    private static final int MMP_STATUS_DISCONNECT = 12;
    private static final int MMP_STATUS_EMOTICON = 13;
    private static final int MMP_STATUS_VERSION = 14;
    private static final int[] MMP_CONNECTION_MODES = {0, 32, 256, 2, 1, 4, 16, 24576, 20480, 16384, 12288, 8193};

    private static final int XMPP_STATUS_DISCONNECT = 0;

    public static int showAccountList(Vector accounts, int targetScreenId, boolean showStatusFlags) {
        UIState.setShowStatusFlags(showStatusFlags);
        SessionState.clearCurrentAccount();
        int size = accounts.size();
        if (size == 0) {
            return NotificationHelper.showError(MSG_NO_ACCOUNTS);
        }
        if (size == 1) {
            AppState.setAccount(accounts.firstElement());
            return targetScreenId;
        }
        SessionState.setFilteredAccounts(accounts);
        SessionState.setTargetState(targetScreenId);
        return ScreenId.ACCOUNT_CHECKBOX_LIST;
    }

    public static void addToAccountSelection(Account account) {
        Vector accounts = SessionState.getAccountSelection();
        if (accounts == null) {
            accounts = ObjectPool.newVector();
            SessionState.setAccountSelection(accounts);
        }
        accounts.addElement(account);
    }

    public static boolean hasActiveConnection() {
        return UIState.getActiveConnections().size() != 0;
    }

    public static void clearAccountHighlight(MrimAccount account) {
        UIState.getActiveConnections().removeElement(account);
        TabBar.layout();
    }

    public static void clearAllHighlights() {
        UIState.getActiveConnections().removeAllElements();
        TabBar.layout();
    }

    public static int getCombinedContactFlags() {
        int combinedFlags = 0;
        Vector contacts = UIState.getOnlineContacts();
        for (int i = contacts.size() - 1; i >= 0; i--) {
            combinedFlags |= ((Contact) contacts.elementAt(i)).flags;
        }
        return combinedFlags;
    }

    public static boolean isAccountOnline(Account account) {
        if (account == null) {
            return false;
        }
        Vector contacts = UIState.getOnlineContacts();
        for (int i = contacts.size() - 1; i >= 0; i--) {
            if (((Contact) contacts.elementAt(i)).account == account) {
                return true;
            }
        }
        return false;
    }

    public static int getAccountStatus(Account account) {
        if (account == null) {
            return ICON_DEFAULT;
        }
        Vector contacts = UIState.getOnlineContacts();
        for (int i = contacts.size() - 1; i >= 0; i--) {
            if (((Contact) contacts.elementAt(i)).account == account) {
                return ICON_DEFAULT;
            }
        }
        return account.getIconId();
    }

    public static void recordInboundTraffic(Account account, int byteCount) {
        account.resetSyncIfChanged(TrafficAccounting.initStartupState());
        int[] sync = account.syncArray;
        sync[0] = sync[0] + byteCount;
        sync[2] = sync[2] + byteCount;
        sync[4] = sync[4] + byteCount;
        sync[6] = sync[6] + byteCount;
        TrafficAccounting.addMrimInbound(byteCount);
    }

    public static void recordOutboundTraffic(Account account, int byteCount) {
        account.resetSyncIfChanged(TrafficAccounting.initStartupState());
        int[] sync = account.syncArray;
        sync[1] = sync[1] + byteCount;
        sync[3] = sync[3] + byteCount;
        sync[5] = sync[5] + byteCount;
        sync[7] = sync[7] + byteCount;
        TrafficAccounting.addMrimOutbound(byteCount);
    }

    public static void recordInboundPacket(Account account, ByteBuffer buffer) {
        recordInboundTraffic(account, buffer.length);
    }

    public static void loadSavedAccounts() {
        RemoteLogger.log("ACCT", "loadSavedAccounts START");
        Vector accounts = ObjectPool.newVector();
        ByteBuffer buffer = ChunkedRecordStore.readChunkedRecord(ObjectPool.unpackChars(ACCOUNT_STORE_KEY));
        while (buffer.length > 0) {
            try {
                Account account = null;
                byte typeByte = buffer.readByte();
                switch (typeByte & TYPE_MASK) {
                    case Account.TYPE_MRIM:
                        MrimAccount mrimAccount = new MrimAccount(buffer);
                        account = mrimAccount;
                        accounts.addElement(mrimAccount);
                        RemoteLogger.log("ACCT", "loaded MRIM: " + mrimAccount.login);
                        break;
                    case Account.TYPE_MMP:
                        MmpProtocol mmpProtocol = new MmpProtocol(buffer);
                        account = mmpProtocol;
                        accounts.addElement(mmpProtocol);
                        RemoteLogger.log("ACCT", "loaded MMP: " + mmpProtocol.login);
                        break;
                    case Account.TYPE_XMPP:
                        XmppProtocol xmppProtocol = new XmppProtocol(buffer);
                        account = xmppProtocol;
                        accounts.addElement(xmppProtocol);
                        RemoteLogger.log("ACCT", "loaded XMPP: " + xmppProtocol.login);
                        break;
                    case Account.TYPE_XMPP_MAILRU:
                        XmppMailRuProtocol xmppMailRu = new XmppMailRuProtocol(buffer);
                        account = xmppMailRu;
                        accounts.addElement(xmppMailRu);
                        RemoteLogger.log("ACCT", "loaded MailRu: " + xmppMailRu.login);
                        break;
                }
                if ((typeByte & HAS_PROPERTIES_FLAG) != 0) {
                    account.loadProperties(buffer);
                }
            } catch (Throwable unused) {
            }
        }
        RemoteLogger.log("ACCT", "loadSavedAccounts: loaded " + accounts.size() + " accounts");
        SessionState.setAccounts(accounts);
    }

    public static int getActiveAccountCount() {
        return SessionState.getAccounts().size();
    }

    public static Account getAccountByIndex(int index) {
        return (Account) SessionState.getAccounts().elementAt(index);
    }

    public static void saveAccountList() {
        saveState(false, false);
        AppState.saveAllDeltas(true);
    }

    public static void saveState(boolean chunked, boolean destructive) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            Vector accounts = SessionState.getAccounts();
            if (destructive) {
                buffer.ensureCapacity(SAVE_BUFFER_DESTRUCTIVE);
                while (accounts.size() > 0) {
                    ((Account) Utils.dequeue(accounts)).serializeAccount(buffer, chunked, true).saveProperties(buffer);
                }
            } else {
                buffer.ensureCapacity(SAVE_BUFFER_NORMAL);
                for (int i = 0; i < accounts.size(); i++) {
                    ((Account) accounts.elementAt(i)).serializeAccount(buffer, chunked, false).saveProperties(buffer);
                }
            }
            RemoteLogger.log("PERSIST", "saveAccounts: " + buffer.length + " bytes, chunked=" + chunked + ", destructive=" + destructive);
            ChunkedRecordStore.writeRecord(ObjectPool.unpackChars(ACCOUNT_STORE_KEY), buffer, chunked);
            RemoteLogger.log("PERSIST", "saveAccounts: writeRecord done");
        } catch (Throwable th) {
            RemoteLogger.log("PERSIST", "saveAccounts FAILED", th);
        }
    }

    public static int validateCredentials(int protocolType, Account existingAccount, String login, String password) {
        if (StringUtils.isEmpty(login)) {
            return MSG_EMPTY_LOGIN;
        }
        if (StringUtils.isEmpty(password)) {
            return MSG_EMPTY_PASSWORD;
        }
        Account foundAccount = findAccountByType(protocolType, login);
        if (existingAccount != null) {
            if (foundAccount == null || foundAccount == existingAccount) {
                return existingAccount.setCredentials(login, password);
            }
            return MSG_DUPLICATE_ACCOUNT;
        }
        if (foundAccount != null) {
            return MSG_DUPLICATE_ACCOUNT;
        }
        int newId = allocateNewAccountId();
        if (protocolType == Account.TYPE_MRIM) {
            SessionState.getAccounts().addElement(new MrimAccount(newId, login, password));
        } else if (protocolType == Account.TYPE_MMP) {
            SessionState.getAccounts().addElement(new MmpProtocol(newId, login, password));
        } else if (protocolType == Account.TYPE_XMPP) {
            SessionState.getAccounts().addElement(new XmppProtocol(newId, login, password));
        } else if (protocolType == Account.TYPE_XMPP_MAILRU) {
            SessionState.getAccounts().addElement(new XmppMailRuProtocol(newId, login, password));
        }
        RemoteLogger.log("ACCT", "validateCredentials OK, new account type=" + protocolType + " login=" + login);
        TabBar.initialize();
        saveAccountList();
        return 0;
    }

    private static Account findAccountByType(int protocolType, String login) {
        Vector accounts = SessionState.getAccounts();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            Account candidate = (Account) accounts.elementAt(i);
            if (protocolType == candidate.getType() && login.equals(candidate.login)) {
                return candidate;
            }
        }
        return null;
    }

    private static int allocateNewAccountId() {
        Vector accounts = SessionState.getAccounts();
        int totalSize = accounts.size();
        int newId = 0;
        while (true) {
            boolean idTaken = false;
            for (int j = totalSize - 1; j >= 0; j--) {
                if (((Account) accounts.elementAt(j)).accountId == newId) {
                    idTaken = true;
                    break;
                }
            }
            if (!idTaken) {
                return newId;
            }
            newId++;
        }
    }

    public static Account findAccountByLogin(int protocolType, String login) {
        Vector accounts = SessionState.getAccounts();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            Account account = (Account) accounts.elementAt(i);
            if (login.equals(account.login) && account.getType() == protocolType) {
                return account;
            }
        }
        return null;
    }

    public static Vector getMrimAccountList() {
        Vector result = ObjectPool.newVector();
        Vector allAccounts = SessionState.getAccounts();
        for (int i = allAccounts.size() - 1; i >= 0; i--) {
            Object element = allAccounts.elementAt(i);
            if (element instanceof MrimAccount) {
                result.insertElementAt(element, 0);
            }
        }
        return result;
    }

    public static Vector getOnlineMrimAccounts() {
        Vector accounts = getMrimAccountList();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            if (!getMrimAccount(accounts, i).isConnected()) {
                accounts.removeElementAt(i);
            }
        }
        return accounts;
    }

    public static Vector getSyncedMrimAccounts() {
        Vector accounts = getMrimAccountList();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            if (getMrimAccount(accounts, i).syncSeq == 0) {
                accounts.removeElementAt(i);
            }
        }
        return accounts;
    }

    public static int getTotalSyncCount() {
        int total = 0;
        Vector accounts = getMrimAccountList();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            total += getMrimAccount(accounts, i).syncSeq;
        }
        ObjectPool.releaseVector(accounts);
        return total;
    }

    public static Vector copyAllAccounts() {
        Vector result = ObjectPool.newVector();
        Vector allAccounts = SessionState.getAccounts();
        for (int i = allAccounts.size() - 1; i >= 0; i--) {
            result.insertElementAt(allAccounts.elementAt(i), 0);
        }
        return result;
    }

    public static void toggleAllConnections() {
        boolean allDisconnected = true;
        for (int i = SessionState.getAccounts().size() - 1; i >= 0; i--) {
            if (getAccountByIndex(i).isConnecting()) {
                allDisconnected = false;
            }
        }
        for (int i = SessionState.getAccounts().size() - 1; i >= 0; i--) {
            Account account = getAccountByIndex(i);
            if (account.isConnecting()) {
                if (!allDisconnected) {
                    account.disconnect();
                }
            } else if (allDisconnected) {
                account.connect(0);
            }
        }
    }

    public static Vector getAllContacts() {
        Vector result = ObjectPool.newVector();
        Vector allAccounts = SessionState.getAccounts();
        for (int accountIdx = Utils.vectorSize(allAccounts) - 1; accountIdx >= 0; accountIdx--) {
            Vector contacts = ((Account) allAccounts.elementAt(accountIdx)).getAllContacts();
            for (int contactIdx = Utils.vectorSize(contacts) - 1; contactIdx >= 0; contactIdx--) {
                result.addElement(contacts.elementAt(contactIdx));
            }
            ObjectPool.releaseVector(contacts);
        }
        return result;
    }

    public static Vector getContactGroups(Account targetAccount) {
        if (targetAccount == null) {
            Vector result = ObjectPool.newVector();
            for (int i = getActiveAccountCount() - 1; i >= 0; i--) {
                Account account = getAccountByIndex(i);
                for (int j = account.groups.size() - 1; j >= 0; j--) {
                    result.addElement(account.getGroup(j));
                }
            }
            return result;
        }
        Vector result = ObjectPool.newVector();
        for (int i = getActiveAccountCount() - 1; i >= 0; i--) {
            Account account = getAccountByIndex(i);
            if (account == targetAccount) {
                for (int j = account.groups.size() - 1; j >= 0; j--) {
                    ContactGroup group = account.getGroup(j);
                    if (group != account.defaultGroup && group != account.onlineGroup && group != account.offlineGroup && group != account.blockedGroup) {
                        result.addElement(group);
                    }
                }
            }
        }
        return result;
    }

    public static MrimAccount getMrimAccount(Vector vector, int index) {
        return (MrimAccount) vector.elementAt(index);
    }

    public static int handleAction(Object account) {
        int targetState = SessionState.getTargetState();
        if (account != null) {
            AppState.setAccount(account);
            return targetState;
        }
        if (targetState != ScreenId.MAP_VIEW_SETTINGS) {
            return ScreenId.COLOR_PICKER;
        }
        SessionState.clearCurrentAccount();
        return ScreenId.MAP_VIEW_SETTINGS;
    }

    public static int handleMenuAction(String label, Object accountObj) {
        if (StringUtils.matchesKey(MSG_TOGGLE_ALL, label)) {
            toggleAllConnections();
            return ScreenId.CONTACT_LIST;
        }
        Account account = (Account) accountObj;
        int errorCode = account.isConnecting() ? account.disconnect() : account.connect(0);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    public static int handleObjectAction(Object account) {
        AppState.setAccount(account);
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static int handleInputAction(int protocolType, Object existingAccount) {
        AppState.setAccount(existingAccount);
        if (existingAccount != null) {
            return ScreenId.CONTACT_GROUPS;
        }
        if (protocolType > Account.TYPE_XMPP_MAILRU) {
            return protocolType;
        }
        SessionState.setProtocolType(protocolType);
        return ScreenId.XMPP_LOGIN;
    }

    public static int handleInviteResult() {
        Account account = AppState.getAccount();
        if (account.isConnecting()) {
            return NotificationHelper.showError(MSG_ACCOUNT_ACTIVE);
        }
        SessionState.getAccounts().removeElement(account);
        TabBar.initialize();
        saveAccountList();
        return ScreenId.MULTI_ACCOUNT_LIST;
    }

    public static int handleSendKey() {
        AppState.setAccount(getMrimAccountList().firstElement());
        return ScreenId.INVITE_TOS;
    }

    public static int handlePresenceAction() {
        Vector accounts = SessionState.getAccountSelection();
        for (int i = accounts.size() - 1; i >= 0; i--) {
            ((Account) accounts.elementAt(i)).connect(0);
        }
        return ScreenId.CONTACT_LIST;
    }

    public static int handleGroupRename(int screenId) {
        if (screenId != ScreenId.BLOG_POST && screenId != ScreenId.UNUSED_133 && screenId != ScreenId.TOS_SCREEN) {
            return 0;
        }
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        if (size == 0) {
            return NotificationHelper.showError(MSG_NO_MRIM_ACCOUNTS);
        }
        if (size != 1) {
            return showAccountList(accounts, screenId, false);
        }
        AppState.setAccount(accounts.firstElement());
        return screenId;
    }

    public static int handleConnectionOption(int feature) {
        ((XmppContact) AppState.getCurrentContact()).setPresenceFeature(feature);
        return 0;
    }

    public static int handleGroupSelection(int optionId) {
        Message message = ((MrimAccount) AppState.getAccount()).chatRoomManager.findById(ChatState.getChatRoomId()).getMessage(RuntimeState.getMessageId());
        String body = message.body;
        message.body = optionId == 0 ? Conversation.encodeAlternate(body) : Conversation.decodeAlternate(body);
        return ScreenId.MESSAGE_PREVIEW;
    }

    public static int handleStatusChange(int optionIndex) {
        Account account = AppState.getAccount();
        switch (account.getType()) {
            case Account.TYPE_MRIM:
                return handleMrimStatus((MrimAccount) account, optionIndex);
            case Account.TYPE_MMP:
                return handleMmpStatus((MmpProtocol) account, optionIndex);
            default:
                return handleXmppStatus((XmppProtocol) account, optionIndex);
        }
    }

    private static int handleMrimStatus(MrimAccount account, int optionIndex) {
        if (optionIndex == MRIM_STATUS_EMOTICON) {
            return ScreenId.EMOTICON_DIALOG;
        }
        if (optionIndex == MRIM_STATUS_DISCONNECT) {
            int errorCode = account.disconnect();
            if (errorCode != 0) {
                return NotificationHelper.showError(errorCode);
            }
            return ScreenId.CONTACT_LIST;
        }
        int errorCode = account.setConfiguration(MRIM_STATUS_MODES[optionIndex]);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    private static int handleMmpStatus(MmpProtocol protocol, int optionIndex) {
        if (optionIndex == MMP_STATUS_EMOTICON) {
            return ScreenId.EMOTICON_DIALOG;
        }
        if (optionIndex == MMP_STATUS_VERSION) {
            return ScreenId.VERSION_SELECT;
        }
        if (optionIndex == MMP_STATUS_DISCONNECT) {
            int errorCode = protocol.disconnect();
            if (errorCode != 0) {
                return NotificationHelper.showError(errorCode);
            }
            return ScreenId.CONTACT_LIST;
        }
        int errorCode = protocol.updateConnectionMode(MMP_CONNECTION_MODES[optionIndex]);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    private static int handleXmppStatus(XmppProtocol protocol, int optionIndex) {
        if (optionIndex == XMPP_STATUS_DISCONNECT) {
            int errorCode = protocol.disconnect();
            if (errorCode != 0) {
                return NotificationHelper.showError(errorCode);
            }
            return ScreenId.CONTACT_LIST;
        }
        int errorCode = protocol.setStatusMode(optionIndex);
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }
}
