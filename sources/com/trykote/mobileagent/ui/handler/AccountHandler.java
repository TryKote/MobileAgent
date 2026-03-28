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

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.ACCOUNT_LIST:
                int size2 = AppState.getVector(StateKeys.VEC_ACCOUNTS).size();
                AppState.setBool(StateKeys.FLAG_HAS_MRIM_ACCOUNTS, size2 > 0);
                AppState.setBool(StateKeys.FLAG_HAS_MULTIPLE_MRIM, size2 > 1);
                AppState.setBool(StateKeys.FLAG_HAS_MRIM_ACCOUNTS_2, AppState.getBool(StateKeys.FLAG_HAS_MRIM_ACCOUNTS));
                AppState.setBool(StateKeys.FLAG_HAS_XMPP_ACCOUNTS, size2 > 0);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ACCOUNT_LIST));
                return;
            case ScreenId.ACCOUNTS_MENU:
                AppState.setBool(StateKeys.FLAG_MULTIPLE_MRIM, AccountManager.getMrimAccountList().size() > 1);
                AppState.setBool(StateKeys.FLAG_MULTIPLE_XMPP, AccountManager.copyAllAccounts().size() > 1);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ACCOUNTS_MENU));
                return;
            case ScreenId.ACCOUNT_SWITCHER:
                ListView contactListScreen = ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.ACCOUNT_SWITCHER), AppState.getVector(StateKeys.VEC_ACCOUNTS));
                Account currentAccount = TabBar.currentAccount;
                if (currentAccount != null) {
                    contactListScreen.selectByTitle(currentAccount.getSignature());
                }
                ScreenManager.showScreen(contactListScreen);
                return;
            case ScreenId.REGISTRATION:
                AppState.setInt(StateKeys.INT_PROTOCOL_TYPE, 0);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.REGISTRATION));
                return;
            case ScreenId.MULTI_ACCOUNT_LIST: {
                ListView screen = ScreenManager.createScreen(ScreenDef.MULTI_ACCOUNT_LIST);
                Vector accounts = AppState.getVector(StateKeys.VEC_ACCOUNTS);
                int size3 = accounts.size();
                for (int i6 = 0; i6 < size3; i6++) {
                    screen.addItem(((Account) accounts.elementAt(i6)).createFlagMenuItem());
                }
                ScreenManager.showScreen(screen.addActionById(-1, 531, 16).addIconById(-1, 532, 1).addIconById(-1, 533, 3).addIconById(-1, 534, 2));
                return;
            }
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, AppState.getInt(StateKeys.SETTING_MULTI_ACCOUNT));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MULTI_ACCOUNT_SETTINGS));
                return;
            case ScreenId.MAIL_ACCOUNT_LIST:
                ResourceManager.showMailAccountList();
                return;
            case ScreenId.ACCOUNT_CHECKBOX_LIST: {
                Vector accountList = AppState.getVector(StateKeys.VEC_FILTERED_ACCOUNTS);
                ListView screen2 = ScreenManager.createScreen(ScreenDef.MULTI_ACCOUNT_LIST);
                screen2.screenId = ScreenId.ACCOUNT_CHECKBOX_LIST;
                screen2.showCheckboxes = true;
                int size4 = accountList.size();
                boolean showFlags = AppState.getBool(StateKeys.FLAG_SHOW_STATUS_FLAGS);
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
                Account account2 = AppState.getAccount();
                String[] regData;
                if (AppState.getAccount().getType() == Account.TYPE_MRIM) {
                    regData = StringUtils.buildRegData();
                } else {
                    regData = new String[8];
                    int intVal3 = AppState.getInt(StateKeys.INT_REG_DOMAIN_INDEX);
                    regData[0] = intVal3 > 0 ? StringUtils.intern(Integer.toString(intVal3)) : AppState.emptyStr;
                    regData[1] = AppState.getString(AppState.getBool(StateKeys.FLAG_REG_SMS_MODE) ? 1046 : 1038);
                    regData[2] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_1));
                    regData[3] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_2));
                    regData[4] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_3));
                    regData[5] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_4));
                    regData[6] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_5));
                    regData[7] = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_FIELD_6));
                }
                NotificationHelper.showErrorOrConfirm(44, 729, account2.validateObject(regData));
                return;
            }
            case ScreenId.ACCOUNT_SWITCH_OPTIONS: {
                Contact contact2 = AppState.getCurrentContact();
                AppState.setInt(StateKeys.INT_DELETE_BUTTON_ICON, contact2.canDelete() ? 25 : 24);
                AppState.setInt(StateKeys.INT_BLOCK_BUTTON_ICON, contact2.canBlock() ? 25 : 24);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ACCOUNT_SWITCH_OPTIONS));
                return;
            }
            case ScreenId.XMPP_LOGIN:
                XmppMailRuProtocol.showLoginScreen();
                return;
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
                NotificationHelper.showAlertBuffer(77, ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_ALERT_PREFIX)).append(AppState.getAccount().login).append(ObjectPool.unpackChars(16167)));
                return;
            case ScreenId.XMPP_LOGIN_ALT:
                XmppMailRuProtocol.showLoginScreen();
                ScreenManager.getCurrentScreen().screenId = ScreenId.XMPP_LOGIN_ALT;
                return;
            case ScreenId.MMP_ACCOUNT_SELECT: {
                StringBuffer sbAccounts = ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_ACCOUNTS_HEADER));
                Vector mmpAccounts = AccountManager.getSyncedMrimAccounts();
                int i20 = 0;
                int size12 = mmpAccounts.size();
                int i21 = size12;
                while (true) {
                    i21--;
                    if (i21 < 0) {
                        AppState.setInt(StateKeys.INT_ACCOUNT_INDEX, i20);
                        AppState.setObject(StateKeys.SLOT_ACCOUNT_LIST_TEXT, (Object) ObjectPool.toStringAndRelease(sbAccounts));
                        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MMP_ACCOUNT_SELECT));
                        return;
                    } else {
                        String str3 = ((MrimAccount) mmpAccounts.elementAt(i21)).login;
                        sbAccounts.append(str3);
                        if (i21 != 0) {
                            sbAccounts.append((char) 0);
                        }
                        if (str3.equals(AppState.getString(StateKeys.LAST_ACCOUNT_NAME))) {
                            i20 = size12 - i21;
                        }
                    }
                }
            }
            case ScreenId.MRIM_ACCOUNT_SELECT: {
                ListView screen19 = ScreenManager.createScreen(ScreenDef.MRIM_ACCOUNT_SELECT);
                Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
                int size15 = onlineAccounts.size();
                while (true) {
                    size15--;
                    if (size15 < 0) {
                        ScreenManager.showScreen(screen19);
                        return;
                    } else {
                        MrimAccount mrimAccount7 = (MrimAccount) onlineAccounts.elementAt(size15);
                        screen19.addIconItemWithData(156, mrimAccount7.login, 0, mrimAccount7);
                    }
                }
            }
            case ScreenId.WIFI_ACCOUNT_LIST:
                if (!AppState.getBool(StateKeys.FLAG_REGISTRATION_DONE)) {
                    Vector mmpAccounts2 = AccountManager.getSyncedMrimAccounts();
                    int size13 = mmpAccounts2.size();
                    int i24 = size13;
                    if (size13 > 0) {
                        ListView screen17 = ScreenManager.createScreen(ScreenDef.WIFI_ACCOUNT_LIST);
                        while (true) {
                            i24--;
                            if (i24 < 0) {
                                ScreenManager.showScreen(screen17);
                                return;
                            }
                            MrimAccount mrimAccount6 = (MrimAccount) mmpAccounts2.elementAt(i24);
                            int iconId = mrimAccount6.getIconId();
                            String str4 = mrimAccount6.login;
                            screen17.addIconItemWithData(iconId, str4, 153, str4);
                        }
                    }
                }
                ResourceManager.showWiFiNetworks();
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
                if (AppState.getInt(StateKeys.INT_SETTINGS_THEME) != AppState.getInt(StateKeys.SETTING_MULTI_ACCOUNT)) {
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
                return 0 == loginResult ? ScreenId.CONTACT_LIST : loginResult;
            }
            case ScreenId.MMP_ACCOUNT_SELECT:
                ScreenManager.processScreenForm();
                AppState.setInt(StateKeys.MAP_INITIALIZED, 1);
                int intVal2 = AppState.getInt(StateKeys.INT_ACCOUNT_INDEX);
                if (intVal2 > 0) {
                    AppState.setInt(StateKeys.FLAG_REGISTRATION_DONE, 1);
                    AppState.setObject(StateKeys.LAST_ACCOUNT_NAME, (Object) Utils.splitAndGet(1252, intVal2));
                } else {
                    AppState.setObject(StateKeys.LAST_ACCOUNT_NAME, (Object) AppState.emptyStr);
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
                return ResourceManager.selectMailAccount(data);
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
                AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
                break;
            case ScreenId.MAIL_ACCOUNT_LIST:
                TabBar.removeSettingsTab();
                break;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                AppState.clearIndex(StateKeys.VEC_FILTERED_ACCOUNTS);
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
                return ResourceManager.selectMailAccount(data);
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
        AppState.setInt(StateKeys.FLAG_REGISTRATION_DONE, 1);
        if (accountName != null) {
            AppState.pool[StateKeys.LAST_ACCOUNT_NAME] = accountName;
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static int handleAccountSwitchOption(int optionId) {
        Contact contact = AppState.getCurrentContact();
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
                return AppState.pool[StateKeys.VEC_ACCOUNT_SELECTION] != null ? ScreenId.PRESENCE_ACTION : 0;
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
                return 0;
            case ScreenId.MAIL_ACCOUNT_LIST:
                return 0;
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
                return 0;
            case ScreenId.SUBMIT_REGISTRATION: {
                int stateInt5 = AppState.getInt(StateKeys.INT_ERROR_MSG_INDEX);
                return 0 != stateInt5 ? NotificationHelper.showError(stateInt5) : AppState.pool[StateKeys.SLOT_REG_PARAM_4] == null ? 0 : ScreenId.SEARCH_RESULT_LIST;
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
