package com.trykote.mobileagent.protocol;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

/* Extracted from AppController: account management subsystem */
public final class AccountManager {

    /* renamed from: a */
    public static int showAccountList(Vector vector, int i, boolean z) {
        AppState.setBool(StateKeys.FLAG_SHOW_STATUS_FLAGS, z);
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        int size = vector.size();
        if (size == 0) {
            return NotificationHelper.showError(551);
        }
        if (size == 1) {
            AppState.setAccount(vector.firstElement());
            return i;
        }
        AppState.pool[StateKeys.VEC_FILTERED_ACCOUNTS] = vector;
        AppState.setInt(StateKeys.INT_TARGET_STATE, i);
        return 39;
    }

    /* renamed from: a */
    public static final void setCurrentAccount(Account account) {
        Vector accounts = AppState.getVector(StateKeys.VEC_ACCOUNT_SELECTION);
        if (accounts == null) {
            accounts = ObjectPool.newVector();
            AppState.pool[StateKeys.VEC_ACCOUNT_SELECTION] = accounts;
        }
        accounts.addElement(account);
    }

    /* renamed from: N */
    public static final boolean hasActiveConnection() {
        return AppState.getVector(StateKeys.VEC_ACTIVE_CONNECTIONS).size() != 0;
    }

    /* renamed from: b */
    public static final void markAccountHighlighted(MrimAccount account) {
        AppState.getVector(StateKeys.VEC_ACTIVE_CONNECTIONS).removeElement(account);
        TabBar.layout();
    }

    /* renamed from: O */
    public static final void updateTabBar() {
        AppState.getVector(StateKeys.VEC_ACTIVE_CONNECTIONS).removeAllElements();
        TabBar.layout();
    }

    /* renamed from: P */
    public static final int handleTabAction() {
        int i = 0;
        Vector contacts = AppState.getVector(StateKeys.VEC_ONLINE_CONTACTS);
        int size = contacts.size();
        while (true) {
            size--;
            if (size < 0) {
                return i;
            }
            i |= ((Contact) contacts.elementAt(size)).flags;
        }
    }

    /* renamed from: b */
    public static final boolean isAccountOnline(Account account) {
        if (account == null) {
            return false;
        }
        Vector contacts = AppState.getVector(StateKeys.VEC_ONLINE_CONTACTS);
        int size = contacts.size();
        do {
            size--;
            if (size < 0) {
                return false;
            }
        } while (((Contact) contacts.elementAt(size)).account != account);
        return true;
    }

    /* renamed from: c */
    public static final int getAccountStatus(Account account) {
        if (account == null) {
            return 16384;
        }
        Vector contacts = AppState.getVector(StateKeys.VEC_ONLINE_CONTACTS);
        int size = contacts.size();
        do {
            size--;
            if (size < 0) {
                return account.getIconId();
            }
        } while (((Contact) contacts.elementAt(size)).account != account);
        return 16384;
    }

    /* renamed from: a */
    public static final void updateAccountStatus(Account account, int i) {
        account.resetSyncIfChanged(TrafficAccounting.initStartupState());
        int[] iArr = account.syncArray;
        iArr[0] = iArr[0] + i;
        iArr[2] = iArr[2] + i;
        iArr[4] = iArr[4] + i;
        iArr[6] = iArr[6] + i;
        AppState.addInt(StateKeys.TRAFFIC_MRIM_SENT_BYTES, i);
        AppState.addInt(StateKeys.TRAFFIC_MRIM_SENT_PACKETS, i);
        AppState.addInt(StateKeys.TRAFFIC_MRIM_SENT_MSGS, i);
        AppState.addInt(StateKeys.TRAFFIC_MRIM_SENT_FILES, i);
        AppState.addInt(StateKeys.COUNTER_TOTAL_TRAFFIC, i);
    }

    /* renamed from: b */
    public static final void setAccountOption(Account account, int i) {
        account.resetSyncIfChanged(TrafficAccounting.initStartupState());
        int[] iArr = account.syncArray;
        iArr[1] = iArr[1] + i;
        iArr[3] = iArr[3] + i;
        iArr[5] = iArr[5] + i;
        iArr[7] = iArr[7] + i;
        AppState.addInt(StateKeys.TRAFFIC_MRIM_RECV_BYTES, i);
        AppState.addInt(StateKeys.TRAFFIC_MRIM_RECV_PACKETS, i);
        AppState.addInt(StateKeys.TRAFFIC_MRIM_RECV_MSGS, i);
        AppState.addInt(StateKeys.TRAFFIC_MRIM_RECV_FILES, i);
        AppState.addInt(StateKeys.COUNTER_RESERVED, i);
    }

    /* renamed from: a */
    public static final void processAccountData(Account account, ByteBuffer buffer) {
        updateAccountStatus(account, buffer.length);
    }

    /* renamed from: ag */
    public static void loadSavedAccounts() {
        RemoteLogger.log("ACCT", "loadSavedAccounts START");
        Vector accounts = ObjectPool.newVector();
        ByteBuffer buffer = XmppMailRuProtocol.readChunkedRecord(ObjectPool.unpackChars(6513505));
        while (buffer.length > 0) {
            try {
                Account account = null;
                byte typeByte = buffer.readByte();
                switch (typeByte & 7) {
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
                if ((typeByte & 8) != 0) {
                    account.loadProperties(buffer);
                }
            } catch (Throwable unused) {
            }
        }
        RemoteLogger.log("ACCT", "loadSavedAccounts: loaded " + accounts.size() + " accounts");
        AppState.pool[StateKeys.VEC_ACCOUNTS] = accounts;
    }

    /* renamed from: Q */
    public static final int getActiveAccountCount() {
        return AppState.getVector(StateKeys.VEC_ACCOUNTS).size();
    }

    /* renamed from: I */
    public static final Account getAccountByIndex(int i) {
        return (Account) AppState.getVector(StateKeys.VEC_ACCOUNTS).elementAt(i);
    }

    /* renamed from: ah */
    public static final void saveAccountList() {
        saveState(false, false);
        AppState.saveDelta(true);
    }

    /* renamed from: a */
    public static final void saveState(boolean z, boolean z2) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            Vector accounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
            if (z2) {
                buffer.ensureCapacity(20480);
                while (accounts.size() > 0) {
                    ((Account) Utils.dequeue(accounts)).serializeAccount(buffer, z, true).saveProperties(buffer);
                }
            } else {
                buffer.ensureCapacity(3072);
                for (int i = 0; i < accounts.size(); i++) {
                    ((Account) accounts.elementAt(i)).serializeAccount(buffer, z, false).saveProperties(buffer);
                }
            }
            XmppMailRuProtocol.writeRecord(ObjectPool.unpackChars(6513505), buffer, z);
        } catch (Throwable unused) {
        }
    }

    /* renamed from: a */
    public static final int validateCredentials(int i, Account existingAccount, String str, String str2) {
        Account foundAccount;
        if (StringUtils.isEmpty(str)) {
            return 301;
        }
        if (StringUtils.isEmpty(str2)) {
            return 306;
        }
        Vector accounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
        int size = accounts.size();
        while (true) {
            size--;
            if (size >= 0) {
                Account candidate = (Account) accounts.elementAt(size);
                if (i == candidate.getType() && str.equals(candidate.login)) {
                    foundAccount = candidate;
                    break;
                }
            } else {
                foundAccount = null;
                break;
            }
        }
        if (existingAccount != null) {
            if (foundAccount == null || foundAccount == existingAccount) {
                return existingAccount.setCredentials(str, str2);
            }
            return 307;
        }
        if (foundAccount != null) {
            return 307;
        }
        Vector allAccounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
        int totalSize = allAccounts.size();
        int newId = 0;
        while (true) {
            boolean idTaken = false;
            int j = totalSize;
            while (true) {
                j--;
                if (j < 0) {
                    break;
                }
                if (((Account) allAccounts.elementAt(j)).accountId == newId) {
                    idTaken = true;
                    break;
                }
            }
            if (!idTaken) {
                break;
            }
            newId++;
        }
        if (i == Account.TYPE_MRIM) {
            allAccounts.addElement(new MrimAccount(newId, str, str2));
        } else if (i == Account.TYPE_MMP) {
            allAccounts.addElement(new MmpProtocol(newId, str, str2));
        } else if (i == Account.TYPE_XMPP) {
            allAccounts.addElement(new XmppProtocol(newId, str, str2));
        } else if (i == Account.TYPE_XMPP_MAILRU) {
            allAccounts.addElement(new XmppMailRuProtocol(newId, str, str2));
        }
        RemoteLogger.log("ACCT", "validateCredentials OK, new account type=" + i + " login=" + str);
        TabBar.initialize();
        saveAccountList();
        return 0;
    }

    /* renamed from: b */
    public static final Account createAccount(int i, String str) {
        Vector accounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return null;
            }
            Account account = (Account) accounts.elementAt(size);
            if (str.equals(account.login) && account.getType() == i) {
                return account;
            }
        }
    }

    /* renamed from: R */
    public static final Vector getMrimAccountList() {
        Vector result = ObjectPool.newVector();
        Vector allAccounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
        int size = allAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            Object element = allAccounts.elementAt(size);
            if (element instanceof MrimAccount) {
                result.insertElementAt(element, 0);
            }
        }
    }

    /* renamed from: S */
    public static final Vector getOnlineMrimAccounts() {
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return accounts;
            }
            if (!findMrimAccount(accounts, size).isConnected()) {
                accounts.removeElementAt(size);
            }
        }
    }

    /* renamed from: T */
    public static final Vector getMmpAccountList() {
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return accounts;
            }
            if (findMrimAccount(accounts, size).syncSeq == 0) {
                accounts.removeElementAt(size);
            }
        }
    }

    /* renamed from: U */
    public static final int getActiveScreenId() {
        int i = 0;
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                ObjectPool.releaseVector(accounts);
                return i;
            }
            i += findMrimAccount(accounts, size).syncSeq;
        }
    }

    /* renamed from: V */
    public static final Vector getXmppAccountList() {
        Vector result = ObjectPool.newVector();
        Vector allAccounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
        int size = allAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            result.insertElementAt(allAccounts.elementAt(size), 0);
        }
    }

    /* renamed from: ai */
    public static void rebuildAccountCaches() {
        boolean allDisconnected = true;
        int size = AppState.getVector(StateKeys.VEC_ACCOUNTS).size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (getAccountByIndex(size).isConnecting()) {
                allDisconnected = false;
            }
        }
        int size2 = AppState.getVector(StateKeys.VEC_ACCOUNTS).size();
        while (true) {
            size2--;
            if (size2 < 0) {
                return;
            }
            Account account = getAccountByIndex(size2);
            if (account.isConnecting()) {
                if (!allDisconnected) {
                    account.disconnect();
                }
            } else if (allDisconnected) {
                account.connect(0);
            }
        }
    }

    /* renamed from: W */
    public static final Vector getAllAccountsList() {
        Vector result = ObjectPool.newVector();
        Vector allAccounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
        int accountIdx = Utils.vectorSize(allAccounts);
        while (true) {
            accountIdx--;
            if (accountIdx < 0) {
                return result;
            }
            Vector contacts = ((Account) allAccounts.elementAt(accountIdx)).getAllContacts();
            int contactIdx = Utils.vectorSize(contacts);
            while (true) {
                contactIdx--;
                if (contactIdx < 0) {
                    break;
                }
                result.addElement(contacts.elementAt(contactIdx));
            }
            ObjectPool.releaseVector(contacts);
        }
    }

    /* renamed from: d */
    public static final Vector getAccountConversations(Account targetAccount) {
        if (targetAccount == null) {
            Vector result = ObjectPool.newVector();
            int count = getActiveAccountCount();
            while (true) {
                count--;
                if (count < 0) {
                    return result;
                }
                Account account = getAccountByIndex(count);
                int size = account.groups.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    result.addElement(account.getGroup(size));
                }
            }
        } else {
            Vector result2 = ObjectPool.newVector();
            int count2 = getActiveAccountCount();
            while (true) {
                count2--;
                if (count2 < 0) {
                    return result2;
                }
                Account account2 = getAccountByIndex(count2);
                if (account2 == targetAccount) {
                    int size2 = account2.groups.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        ContactGroup group = account2.getGroup(size2);
                        if (group != account2.defaultGroup && group != account2.onlineGroup && group != account2.offlineGroup && group != account2.blockedGroup) {
                            result2.addElement(group);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: a */
    public static final MrimAccount findMrimAccount(Vector vector, int i) {
        return (MrimAccount) vector.elementAt(i);
    }
}
