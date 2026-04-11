package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public final class AccountHandler extends BaseScreenHandler {

    // Icon IDs for toggle buttons (enabled/disabled)
    private static final int ICON_TOGGLE_ENABLED = 25;
    private static final int ICON_TOGGLE_DISABLED = 24;

    // MRIM account icon resource ID
    private static final int MRIM_ACCOUNT_ICON = 156;

    // Registration data array size for XMPP accounts
    private static final int REG_DATA_ARRAY_SIZE = 8;

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.ACCOUNT_LIST:
                int size2 = Storage.state().getVector(SessionKeys.VEC_ACCOUNTS).size();
                Storage.state().setBool(SessionKeys.FLAG_HAS_MRIM_ACCOUNTS, size2 > 0);
                Storage.state().setBool(SessionKeys.FLAG_HAS_MULTIPLE_MRIM, size2 > 1);
                Storage.state().setBool(SessionKeys.FLAG_HAS_MRIM_ACCOUNTS_2, Storage.state().getBool(SessionKeys.FLAG_HAS_MRIM_ACCOUNTS));
                Storage.state().setBool(SessionKeys.FLAG_HAS_XMPP_ACCOUNTS, size2 > 0);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ACCOUNT_LIST));
                return;
            case ScreenId.ACCOUNTS_MENU:
                Storage.state().setBool(SessionKeys.FLAG_MULTIPLE_MRIM, AccountManager.getMrimAccountList().size() > 1);
                Storage.state().setBool(SessionKeys.FLAG_MULTIPLE_XMPP, AccountManager.copyAllAccounts().size() > 1);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ACCOUNTS_MENU));
                return;
            case ScreenId.ACCOUNT_SWITCHER:
                ListView contactListScreen = ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.ACCOUNT_SWITCHER), Storage.state().getVector(SessionKeys.VEC_ACCOUNTS));
                Account currentAccount = TabBar.currentAccount;
                if (currentAccount != null) {
                    contactListScreen.selectByTitle(currentAccount.getSignature());
                }
                ScreenManager.showScreen(contactListScreen);
                return;
            case ScreenId.REGISTRATION:
                Storage.state().setInt(SessionKeys.INT_PROTOCOL_TYPE, 0);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.REGISTRATION));
                return;
            case ScreenId.MULTI_ACCOUNT_LIST: {
                ListView screen = ScreenManager.createScreen(ScreenDef.MULTI_ACCOUNT_LIST);
                Vector accounts = Storage.state().getVector(SessionKeys.VEC_ACCOUNTS);
                int size3 = accounts.size();
                for (int i6 = 0; i6 < size3; i6++) {
                    screen.addItem(((Account) accounts.elementAt(i6)).createFlagMenuItem());
                }
                ScreenManager.showScreen(screen.addActionById(-1, 531, 16).addIconById(-1, 532, 1).addIconById(-1, 533, 3).addIconById(-1, 534, 2));
                return;
            }
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                Storage.state().setInt(SettingsKeys.INT_MULTI_ACCOUNT_CACHE, Storage.state().getInt(SettingsKeys.SETTING_MULTI_ACCOUNT));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MULTI_ACCOUNT_SETTINGS));
                return;
            case ScreenId.MAIL_ACCOUNT_LIST:
                showMailAccountList();
                return;
            case ScreenId.ACCOUNT_CHECKBOX_LIST: {
                Vector accountList = Storage.state().getVector(SessionKeys.VEC_FILTERED_ACCOUNTS);
                ListView screen2 = ScreenManager.createScreen(ScreenDef.MULTI_ACCOUNT_LIST);
                screen2.screenId = ScreenId.ACCOUNT_CHECKBOX_LIST;
                screen2.showCheckboxes = true;
                int size4 = accountList.size();
                boolean showFlags = Storage.state().getBool(UIKeys.FLAG_SHOW_STATUS_FLAGS);
                for (int i7 = 0; i7 < size4; i7++) {
                    Object element = accountList.elementAt(i7);
                    if (!(element instanceof Account)) {
                        screen2.addActionById(11, 548, 0);
                    } else if (showFlags) {
                        screen2.addItem(((Account) element).createFlagMenuItem());
                    } else {
                        screen2.addItem(((Account) element).createMenuItem());
                    }
                }
                Account currentAccount2 = TabBar.currentAccount;
                if (currentAccount2 != null) {
                    screen2.selectByTitle(currentAccount2.getSignature());
                }
                ScreenManager.showScreen(screen2);
                ObjectPool.releaseVector(accountList);
                return;
            }
            case ScreenId.SUBMIT_REGISTRATION: {
                ScreenManager.prepareFormData();
                Account account2 = Storage.state().getAccount();
                String[] regData;
                if (Storage.state().getAccount().getType() == Account.TYPE_MRIM) {
                    regData = StringUtils.buildRegData();
                } else {
                    regData = new String[REG_DATA_ARRAY_SIZE];
                    int intVal3 = Storage.state().getInt(RegistrationKeys.INT_REG_DOMAIN_INDEX);
                    regData[0] = intVal3 > 0 ? StringUtils.intern(Integer.toString(intVal3)) : Storage.emptyStr;
                    regData[1] = Storage.state().getString(Storage.state().getBool(RegistrationKeys.FLAG_REG_SMS_MODE) ? 1046 : 1038);
                    regData[2] = Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_FIELD_1));
                    regData[3] = Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_FIELD_2));
                    regData[4] = Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_FIELD_3));
                    regData[5] = Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_FIELD_4));
                    regData[6] = Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_FIELD_5));
                    regData[7] = Utils.defaultStr(Storage.state().getString(RegistrationKeys.SLOT_SEARCH_FIELD_6));
                }
                NotificationHelper.showErrorOrConfirm(44, 729, account2.validateObject(regData));
                return;
            }
            case ScreenId.ACCOUNT_SWITCH_OPTIONS: {
                Contact contact2 = Storage.state().getCurrentContact();
                Storage.state().setInt(RuntimeKeys.INT_DELETE_BUTTON_ICON, contact2.canDelete() ? ICON_TOGGLE_ENABLED : ICON_TOGGLE_DISABLED);
                Storage.state().setInt(RuntimeKeys.INT_BLOCK_BUTTON_ICON, contact2.canBlock() ? ICON_TOGGLE_ENABLED : ICON_TOGGLE_DISABLED);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ACCOUNT_SWITCH_OPTIONS));
                return;
            }
            case ScreenId.XMPP_LOGIN:
                XmppMailRuProtocol.showLoginScreen();
                return;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                NotificationHelper.showAlertBuffer(77, ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ALERT_PREFIX)).append(Storage.state().getAccount().login).append(ObjectPool.unpackChars(16167)));
                return;
            case ScreenId.XMPP_LOGIN_ALT:
                XmppMailRuProtocol.showLoginScreen();
                ScreenManager.getCurrentScreen().screenId = ScreenId.XMPP_LOGIN_ALT;
                return;
            case ScreenId.MMP_ACCOUNT_SELECT: {
                StringBuffer sbAccounts = ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ACCOUNTS_HEADER));
                Vector mmpAccounts = AccountManager.getSyncedMrimAccounts();
                int i20 = 0;
                int size12 = mmpAccounts.size();
                for (int i21 = size12 - 1; i21 >= 0; i21--) {
                    String str3 = ((MrimAccount) mmpAccounts.elementAt(i21)).login;
                    sbAccounts.append(str3);
                    if (i21 != 0) {
                        sbAccounts.append((char) 0);
                    }
                    if (str3.equals(Storage.state().getString(SessionKeys.LAST_ACCOUNT_NAME))) {
                        i20 = size12 - i21;
                    }
                }
                Storage.state().setInt(SessionKeys.INT_ACCOUNT_INDEX, i20);
                Storage.state().setObject(SessionKeys.SLOT_ACCOUNT_LIST_TEXT, (Object) ObjectPool.toStringAndRelease(sbAccounts));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MMP_ACCOUNT_SELECT));
                return;
            }
            case ScreenId.MRIM_ACCOUNT_SELECT: {
                ListView screen19 = ScreenManager.createScreen(ScreenDef.MRIM_ACCOUNT_SELECT);
                Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
                for (int idx = onlineAccounts.size() - 1; idx >= 0; idx--) {
                    MrimAccount mrimAccount7 = (MrimAccount) onlineAccounts.elementAt(idx);
                    screen19.addIconItemWithData(MRIM_ACCOUNT_ICON, mrimAccount7.login, 0, mrimAccount7);
                }
                ScreenManager.showScreen(screen19);
                return;
            }
            case ScreenId.WIFI_ACCOUNT_LIST:
                if (!Storage.state().getBool(RegistrationKeys.FLAG_REGISTRATION_DONE)) {
                    Vector mmpAccounts2 = AccountManager.getSyncedMrimAccounts();
                    int size13 = mmpAccounts2.size();
                    if (size13 > 0) {
                        ListView screen17 = ScreenManager.createScreen(ScreenDef.WIFI_ACCOUNT_LIST);
                        for (int i24 = size13 - 1; i24 >= 0; i24--) {
                            MrimAccount mrimAccount6 = (MrimAccount) mmpAccounts2.elementAt(i24);
                            int iconId = mrimAccount6.getIconId();
                            String str4 = mrimAccount6.login;
                            screen17.addIconItemWithData(iconId, str4, 153, str4);
                        }
                        ScreenManager.showScreen(screen17);
                        return;
                    }
                }
                MiscHandler.showWiFiNetworks();
                return;
        }
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.ACCOUNT_LIST:
                return MapController.handleMapMenuOption(action);
            case ScreenId.ACCOUNTS_MENU:
                return handleChatSettingsOption(action);
            case ScreenId.ACCOUNT_SWITCHER:
                return AccountManager.handleMenuAction(title, data);
            case ScreenId.REGISTRATION:
                return 0;
            case ScreenId.MULTI_ACCOUNT_LIST:
                return AccountManager.handleInputAction(action, data);
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                ScreenManager.processScreenForm();
                if (Storage.state().getInt(SettingsKeys.INT_MULTI_ACCOUNT_CACHE) != Storage.state().getInt(SettingsKeys.SETTING_MULTI_ACCOUNT)) {
                    TabBar.initialize();
                }
                return 0;
            case ScreenId.MAIL_ACCOUNT_LIST:
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
            case ScreenId.XMPP_LOGIN_ALT: {
                int loginResult = XmppMailRuProtocol.performLogin();
                return loginResult == 0 ? ScreenId.CONTACT_LIST : loginResult;
            }
            case ScreenId.MMP_ACCOUNT_SELECT:
                ScreenManager.processScreenForm();
                Storage.state().setInt(MapKeys.MAP_INITIALIZED, 1);
                int intVal2 = Storage.state().getInt(SessionKeys.INT_ACCOUNT_INDEX);
                if (intVal2 > 0) {
                    Storage.state().setInt(RegistrationKeys.FLAG_REGISTRATION_DONE, 1);
                    Storage.state().setObject(SessionKeys.LAST_ACCOUNT_NAME, (Object) Utils.splitAndGet(1252, intVal2));
                } else {
                    Storage.state().setObject(SessionKeys.LAST_ACCOUNT_NAME, (Object) Storage.emptyStr);
                }
                return 0;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                return AccountManager.handleObjectAction(data);
            case ScreenId.WIFI_ACCOUNT_LIST:
                return handleItemAction(data);
        }
        return 0;
    }

    public int onMenuItemAction(ListView screen, MenuItem item, Object data) {
        switch (screen.screenId) {
            case ScreenId.ACCOUNT_LIST:
                return 0;
            case ScreenId.ACCOUNTS_MENU:
                return 0;
            case ScreenId.ACCOUNT_SWITCHER:
                return 0;
            case ScreenId.REGISTRATION:
                return 0;
            case ScreenId.MULTI_ACCOUNT_LIST:
                return 0;
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                return 0;
            case ScreenId.MAIL_ACCOUNT_LIST:
                return selectMailAccount(data);
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                return 0;
            case ScreenId.SUBMIT_REGISTRATION:
                return 0;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                return 0;
            case ScreenId.XMPP_LOGIN:
                return 0;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                return 0;
            case ScreenId.XMPP_LOGIN_ALT:
                return ScreenId.CLOSE;
            case ScreenId.MMP_ACCOUNT_SELECT:
                return 0;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                return 0;
            case ScreenId.WIFI_ACCOUNT_LIST:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.MULTI_ACCOUNT_LIST:
                Storage.state().clearIndex(SessionKeys.SLOT_CURRENT_ACCOUNT);
                break;
            case ScreenId.MAIL_ACCOUNT_LIST:
                TabBar.removeSettingsTab();
                break;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                Storage.state().clearIndex(SessionKeys.VEC_FILTERED_ACCOUNTS);
                break;
            case ScreenId.XMPP_LOGIN:
                XmppMailRuProtocol.clearLoginFields();
                break;
            case ScreenId.XMPP_LOGIN_ALT:
                XmppMailRuProtocol.clearLoginFields();
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.ACCOUNT_LIST:
                return MapController.handleMapMenuOption(selectedOption);
            case ScreenId.ACCOUNTS_MENU:
                return handleChatSettingsOption(selectedOption);
            case ScreenId.ACCOUNT_SWITCHER:
                return AccountManager.handleMenuAction(title, data);
            case ScreenId.REGISTRATION:
                return 0;
            case ScreenId.MULTI_ACCOUNT_LIST:
                return AccountManager.handleInputAction(selectedOption, data);
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                return 0;
            case ScreenId.MAIL_ACCOUNT_LIST:
                return selectMailAccount(data);
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                return AccountManager.handleAction(data);
            case ScreenId.SUBMIT_REGISTRATION:
                return -1;
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                return handleAccountSwitchOption(selectedOption);
            case ScreenId.XMPP_LOGIN:
                return 0;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                return AccountManager.handleInviteResult();
            case ScreenId.XMPP_LOGIN_ALT:
                return 0;
            case ScreenId.MMP_ACCOUNT_SELECT:
                return 0;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                return AccountManager.handleObjectAction(data);
            case ScreenId.WIFI_ACCOUNT_LIST:
                return handleItemAction(data);
        }
        return 0;
    }

    public static final void showMailAccountList() {
        Storage.state().clearIndex(SessionKeys.SLOT_CURRENT_ACCOUNT);
        ListView screen = ScreenManager.createScreen(ScreenDef.GENERIC_LIST);
        Vector accounts = AccountManager.getMrimAccountList();
        int size = accounts.size();
        if (size > 0) {
            screen.addLabelById(832);
            for (int i = 0; i < size; i++) {
                screen.addItem(((MrimAccount) accounts.elementAt(i)).createMenuItem());
            }
        } else {
            screen.selectable = false;
            screen.addLabelById(551);
        }
        ObjectPool.releaseVector(accounts);
        ScreenManager.pushScreen(screen);
        TabBar.ensureSettingsTab();
        TabBar.findTab(TabBar.TYPE_MAIL, (Account) null);
    }

    public static final int selectMailAccount(Object obj) {
        if (obj == null) {
            return -1;
        }
        Storage.state().setInt(UIKeys.INT_SCREEN_ACTION, ScreenId.CHAT_ROOM_INIT);
        Storage.state().setAccount(obj);
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
        Storage.state().setInt(RegistrationKeys.FLAG_REGISTRATION_DONE, 1);
        if (accountName != null) {
            Storage.state().setObject(SessionKeys.LAST_ACCOUNT_NAME, accountName);
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static int handleAccountSwitchOption(int optionId) {
        Contact contact = Storage.state().getCurrentContact();
        switch (optionId) {
            case 0:
                int blockError = contact.validateBlock();
                if (blockError != 0) {
                    return NotificationHelper.showError(blockError);
                }
                return ScreenId.CONTACT_LIST;
            case 1:
                int unblockError = contact.validateUnblock();
                if (unblockError != 0) {
                    return NotificationHelper.showError(unblockError);
                }
                return ScreenId.CONTACT_LIST;
            default:
                return 0;
        }
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.ACCOUNT_LIST:
                return 0;
            case ScreenId.ACCOUNTS_MENU:
                return 0;
            case ScreenId.ACCOUNT_SWITCHER:
                return 0;
            case ScreenId.REGISTRATION:
                return 0;
            case ScreenId.MULTI_ACCOUNT_LIST:
                return Storage.state().getObject(SessionKeys.VEC_ACCOUNT_SELECTION) != null ? ScreenId.PRESENCE_ACTION : 0;
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                return 0;
            case ScreenId.MAIL_ACCOUNT_LIST:
                return 0;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                return 0;
            case ScreenId.SUBMIT_REGISTRATION: {
                int stateInt5 = Storage.state().getInt(RuntimeKeys.INT_ERROR_MSG_INDEX);
                return stateInt5 != 0 ? NotificationHelper.showError(stateInt5) : Storage.state().getObject(RegistrationKeys.SLOT_REG_PARAM_4) == null ? 0 : ScreenId.SEARCH_RESULT_LIST;
            }
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
                return 0;
            case ScreenId.XMPP_LOGIN:
                return 0;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                return 0;
            case ScreenId.XMPP_LOGIN_ALT:
                return 0;
            case ScreenId.MMP_ACCOUNT_SELECT:
                return 0;
            case ScreenId.MRIM_ACCOUNT_SELECT:
                return 0;
            case ScreenId.WIFI_ACCOUNT_LIST:
                return 0;
        }
        return 0;
    }
}
