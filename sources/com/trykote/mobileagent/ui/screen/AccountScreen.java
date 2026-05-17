package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.xmpp.XmppMailRuProtocol;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.ui.TabBar;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Vector;

public final class AccountScreen extends ScreenView {

    private static final int ICON_TOGGLE_ENABLED = 25;
    private static final int ICON_TOGGLE_DISABLED = 24;
    private static final int MRIM_ACCOUNT_ICON = 156;
    private static final int REG_DATA_ARRAY_SIZE = 8;

    public AccountScreen(int screenId) {
        super(ScreenManager.TYPE_FULLSCREEN, screenId);
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.ACCOUNT_LIST:
                buildAccountList();
                break;
            case ScreenId.ACCOUNTS_MENU:
                SessionState.setMultipleMrim(AccountManager.getMrimAccountList().size() > 1);
                SessionState.setMultipleXmpp(AccountManager.copyAllAccounts().size() > 1);
                Screens.accountsMenu().show();
                break;
            case ScreenId.ACCOUNT_SWITCHER:
                buildAccountSwitcher();
                break;
            case ScreenId.REGISTRATION:
                SessionState.setProtocolType(0);
                Screens.registration().show();
                break;
            case ScreenId.MULTI_ACCOUNT_LIST:
                buildMultiAccountList();
                break;
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                SettingsState.setMultiAccountCache(SettingsState.isMultiAccount() ? 1 : 0);
                Screens.multiAccountSettings().show();
                break;
            case ScreenId.MAIL_ACCOUNT_LIST:
                showMailAccountList();
                break;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                buildAccountCheckboxList();
                break;
            case ScreenId.SUBMIT_REGISTRATION:
                buildSubmitRegistration();
                break;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                buildAccountSwitchOptions();
                break;
            case ScreenId.XMPP_LOGIN:
                XmppMailRuProtocol.showLoginScreen();
                break;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                NotificationHelper.showAlertBuffer(77, ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_ALERT_PREFIX)).append(AppState.getAccount().login).append(ObjectPool.unpackChars(16167)));
                break;
            case ScreenId.XMPP_LOGIN_ALT:
                XmppMailRuProtocol.showLoginScreen();
                ScreenManager.getCurrentScreen().screenId = ScreenId.XMPP_LOGIN_ALT;
                break;
            case ScreenId.MMP_ACCOUNT_SELECT:
                buildMmpAccountSelect();
                break;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                buildMrimAccountSelect();
                break;
            case ScreenId.WIFI_ACCOUNT_LIST:
                buildWifiAccountList();
                break;
        }
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.ACCOUNT_LIST:
                return MapController.handleMapMenuOption(action);
            case ScreenId.ACCOUNTS_MENU:
                return handleChatSettingsOption(action);
            case ScreenId.ACCOUNT_SWITCHER:
                return AccountManager.handleMenuAction(title, data);
            case ScreenId.MULTI_ACCOUNT_LIST:
                return AccountManager.handleInputAction(action, data);
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                ScreenManager.processScreenForm();
                if (SettingsState.getMultiAccountCache() != (SettingsState.isMultiAccount() ? 1 : 0)) {
                    TabBar.initialize();
                }
                return 0;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                return AccountManager.handleAction(data);
            case ScreenId.SUBMIT_REGISTRATION:
                return -1;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                return handleAccountSwitchOption(action);
            case ScreenId.XMPP_LOGIN:
                return XmppMailRuProtocol.performLogin();
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                return AccountManager.handleInviteResult();
            case ScreenId.XMPP_LOGIN_ALT:
                int loginResult = XmppMailRuProtocol.performLogin();
                return loginResult == 0 ? ScreenId.CONTACT_LIST : loginResult;
            case ScreenId.MMP_ACCOUNT_SELECT:
                return handleMmpAccountSelect();
            case ScreenId.MRIM_ACCOUNT_SELECT:
                return AccountManager.handleObjectAction(data);
            case ScreenId.WIFI_ACCOUNT_LIST:
                return handleItemAction(data);
            default:
                return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.ACCOUNT_LIST:
                return MapController.handleMapMenuOption(selectedOption);
            case ScreenId.ACCOUNTS_MENU:
                return handleChatSettingsOption(selectedOption);
            case ScreenId.ACCOUNT_SWITCHER:
                return AccountManager.handleMenuAction(title, data);
            case ScreenId.MULTI_ACCOUNT_LIST:
                return AccountManager.handleInputAction(selectedOption, data);
            case ScreenId.MAIL_ACCOUNT_LIST:
                return selectMailAccount(data);
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                return AccountManager.handleAction(data);
            case ScreenId.SUBMIT_REGISTRATION:
                return -1;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                return handleAccountSwitchOption(selectedOption);
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                return AccountManager.handleInviteResult();
            case ScreenId.MRIM_ACCOUNT_SELECT:
                return AccountManager.handleObjectAction(data);
            case ScreenId.WIFI_ACCOUNT_LIST:
                return handleItemAction(data);
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        switch (screenId) {
            case ScreenId.MAIL_ACCOUNT_LIST:
                return selectMailAccount(data);
            case ScreenId.XMPP_LOGIN_ALT:
                return ScreenId.CLOSE;
            default:
                return 0;
        }
    }

    public int onIdle(MenuItem item, Object data, String title) {
        switch (screenId) {
            case ScreenId.MULTI_ACCOUNT_LIST:
                return SessionState.getAccountSelectionObj() != null ? ScreenId.PRESENCE_ACTION : 0;
            case ScreenId.SUBMIT_REGISTRATION:
                int errorMsgId = RuntimeState.getErrorMsgIndex();
                return errorMsgId != 0 ? NotificationHelper.showError(errorMsgId) : RegistrationState.getParam4() == null ? 0 : ScreenId.SEARCH_RESULT_LIST;
            default:
                return 0;
        }
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.MULTI_ACCOUNT_LIST:
                SessionState.clearCurrentAccount();
                break;
            case ScreenId.MAIL_ACCOUNT_LIST:
                TabBar.removeSettingsTab();
                break;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                SessionState.clearFilteredAccounts();
                break;
            case ScreenId.XMPP_LOGIN:
            case ScreenId.XMPP_LOGIN_ALT:
                XmppMailRuProtocol.clearLoginFields();
                break;
        }
    }

    // --- Build helpers ---

    private static void buildAccountList() {
        int size = SessionState.getAccounts().size();
        SessionState.setHasMrimAccounts(size > 0);
        SessionState.setHasMultipleMrim(size > 1);
        SessionState.setHasMrimAccounts2(size > 0);
        SessionState.setHasXmppAccounts(size > 0);
        Screens.accountList().show();
    }

    private static void buildAccountSwitcher() {
        ListView screen = ContactListManager.addContactItems(Screens.accountSwitcher(), SessionState.getAccounts());
        Account currentAccount = TabBar.currentAccount;
        if (currentAccount != null) {
            screen.selectByTitle(currentAccount.getSignature());
        }
        ScreenManager.showScreen(screen);
    }

    private static void buildMultiAccountList() {
        Screen screen = Screens.multiAccountList();
        Vector accounts = SessionState.getAccounts();
        int size = accounts.size();
        for (int i = 0; i < size; i++) {
            screen.addItem(((Account) accounts.elementAt(i)).createFlagMenuItem());
        }
        ScreenManager.showScreen(screen.addAction(-1, AppState.getString(531), 16).addIcon(-1, AppState.getString(532), 1).addIcon(-1, AppState.getString(533), 3).addIcon(-1, AppState.getString(534), 2));
    }

    private static void buildAccountCheckboxList() {
        Vector accountList = SessionState.getFilteredAccounts();
        Screen screen = Screens.multiAccountList();
        screen.screenId = ScreenId.ACCOUNT_CHECKBOX_LIST;
        screen.showCheckboxes = true;
        int size = accountList.size();
        boolean showFlags = UIState.isShowStatusFlags();
        for (int i = 0; i < size; i++) {
            Object element = accountList.elementAt(i);
            if (!(element instanceof Account)) {
                screen.addAction(11, AppState.getString(548), 0);
            } else if (showFlags) {
                screen.addItem(((Account) element).createFlagMenuItem());
            } else {
                screen.addItem(((Account) element).createMenuItem());
            }
        }
        Account currentAccount = TabBar.currentAccount;
        if (currentAccount != null) {
            screen.selectByTitle(currentAccount.getSignature());
        }
        ScreenManager.showScreen(screen);
        ObjectPool.releaseVector(accountList);
    }

    private static void buildSubmitRegistration() {
        ScreenManager.prepareFormData();
        Account account = AppState.getAccount();
        String[] regData;
        if (account.getType() == Account.TYPE_MRIM) {
            regData = StringUtils.buildRegData();
        } else {
            regData = new String[REG_DATA_ARRAY_SIZE];
            int domainIdx = RegistrationState.getRegDomainIndex();
            regData[0] = domainIdx > 0 ? StringUtils.intern(Integer.toString(domainIdx)) : AppState.emptyStr;
            regData[1] = StringPool.get(RegistrationState.isRegSmsMode() ? 1046 : 1038);
            regData[2] = Utils.defaultStr(RegistrationState.getSearchField(1));
            regData[3] = Utils.defaultStr(RegistrationState.getSearchField(2));
            regData[4] = Utils.defaultStr(RegistrationState.getSearchField(3));
            regData[5] = Utils.defaultStr(RegistrationState.getSearchField(4));
            regData[6] = Utils.defaultStr(RegistrationState.getSearchField(5));
            regData[7] = Utils.defaultStr(RegistrationState.getSearchField(6));
        }
        NotificationHelper.showErrorOrConfirm(44, 729, account.validateObject(regData));
    }

    private static void buildAccountSwitchOptions() {
        Contact contact = AppState.getCurrentContact();
        RuntimeState.setDeleteButtonIcon(contact.canDelete() ? ICON_TOGGLE_ENABLED : ICON_TOGGLE_DISABLED);
        RuntimeState.setBlockButtonIcon(contact.canBlock() ? ICON_TOGGLE_ENABLED : ICON_TOGGLE_DISABLED);
        Screens.accountSwitchOptions().show();
    }

    private static void buildMmpAccountSelect() {
        StringBuffer sb = ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_ACCOUNTS_HEADER));
        Vector mmpAccounts = AccountManager.getSyncedMrimAccounts();
        int selectedIdx = 0;
        int size = mmpAccounts.size();
        for (int i = size - 1; i >= 0; i--) {
            String login = ((MrimAccount) mmpAccounts.elementAt(i)).login;
            sb.append(login);
            if (i != 0) {
                sb.append((char) 0);
            }
            if (login.equals(SessionState.getLastAccountName())) {
                selectedIdx = size - i;
            }
        }
        SessionState.setAccountIndex(selectedIdx);
        SessionState.setAccountListText((Object) ObjectPool.toStringAndRelease(sb));
        Screens.mmpAccountSelect().show();
    }

    private static void buildMrimAccountSelect() {
        Screen screen = Screens.mrimAccountSelect();
        Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
        for (int idx = onlineAccounts.size() - 1; idx >= 0; idx--) {
            MrimAccount mrimAccount = (MrimAccount) onlineAccounts.elementAt(idx);
            screen.addIconItemWithData(MRIM_ACCOUNT_ICON, mrimAccount.login, 0, mrimAccount);
        }
        ScreenManager.showScreen(screen);
    }

    private static void buildWifiAccountList() {
        if (!RegistrationState.isRegistrationDone()) {
            Vector mmpAccounts = AccountManager.getSyncedMrimAccounts();
            int size = mmpAccounts.size();
            if (size > 0) {
                Screen screen = Screens.wifiAccountList();
                for (int i = size - 1; i >= 0; i--) {
                    MrimAccount mrimAccount = (MrimAccount) mmpAccounts.elementAt(i);
                    int iconId = mrimAccount.getIconId();
                    String login = mrimAccount.login;
                    screen.addIconItemWithData(iconId, login, 153, login);
                }
                ScreenManager.showScreen(screen);
                return;
            }
        }
        MiscScreen.showWiFiNetworks();
    }

    // --- Public static (used externally) ---

    public static void showMailAccountList() {
        SessionState.clearCurrentAccount();
        Screen screen = Screens.genericList();
        Vector accounts = AccountManager.getMrimAccountList();
        int size = accounts.size();
        if (size > 0) {
            screen.addLabel(AppState.getString(832));
            for (int i = 0; i < size; i++) {
                screen.addItem(((MrimAccount) accounts.elementAt(i)).createMenuItem());
            }
        } else {
            screen.selectable = false;
            screen.addLabel(AppState.getString(551));
        }
        ObjectPool.releaseVector(accounts);
        ScreenManager.pushScreen(screen);
        TabBar.ensureSettingsTab();
        TabBar.findTab(TabBar.TYPE_MAIL, (Account) null);
    }

    public static int selectMailAccount(Object obj) {
        if (obj == null) {
            return -1;
        }
        UIState.setScreenAction(ScreenId.CHAT_ROOM_INIT);
        AppState.setAccount(obj);
        return 0;
    }

    public static int handleChatSettingsOption(int optionId) {
        if (optionId == 22 || optionId == 143 || optionId == 24 || optionId == 23) {
            return AccountManager.showAccountList(AccountManager.getMrimAccountList(), optionId, false);
        }
        if (optionId == 21 || optionId == 69 || optionId == 124) {
            return AccountManager.showAccountList(AccountManager.copyAllAccounts(), optionId, false);
        }
        return 0;
    }

    public static int handleItemAction(Object accountName) {
        RegistrationState.setRegistrationDone(true);
        if (accountName != null) {
            SessionState.setLastAccountName(accountName);
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static int handleAccountSwitchOption(int optionId) {
        Contact contact = AppState.getCurrentContact();
        switch (optionId) {
            case 0:
                int blockError = contact.validateBlock();
                return blockError != 0 ? NotificationHelper.showError(blockError) : ScreenId.CONTACT_LIST;
            case 1:
                int unblockError = contact.validateUnblock();
                return unblockError != 0 ? NotificationHelper.showError(unblockError) : ScreenId.CONTACT_LIST;
            default:
                return 0;
        }
    }

    private static int handleMmpAccountSelect() {
        ScreenManager.processScreenForm();
        MapState.setInitialized(true);
        int accountIdx = SessionState.getAccountIndex();
        if (accountIdx > 0) {
            RegistrationState.setRegistrationDone(true);
            SessionState.setLastAccountName((Object) Utils.splitAndGet(1252, accountIdx));
        } else {
            SessionState.setLastAccountName((Object) AppState.emptyStr);
        }
        return 0;
    }
}
