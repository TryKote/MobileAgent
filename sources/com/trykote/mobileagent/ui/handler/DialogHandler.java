package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public final class DialogHandler extends BaseScreenHandler {


    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.STATUS_DIALOG:
                Screen dialogScreen = ScreenManager.createDialogScreen(3);
                Account account = AppState.getAccount();
                switch (account.getType()) {
                    case 0:
                        dialogScreen.addIconById(156, 642, 0).addIconById(159, 643, 1).addIconById(157, 644, 2).addIconById(160, 645, 3).addIconById(158, 646, 4).addIconById(155, 647, 5).addActionById(-1, 718, 6);
                        break;
                    case 1:
                        MmpProtocol mmpProtocol = (MmpProtocol) account;
                        int iconResId = mmpProtocol.getIconResourceId();
                        dialogScreen.addIconById(iconResId, 642, 0).addIconById(iconResId | 16384000, 643, 1).addIconById(iconResId | 16449536, 645, 3).addIconById(iconResId | 16318464, 644, 4).addIconById(iconResId | 16580608, 648, 5).addIconById(iconResId | 16646144, 654, 6).addIconById(iconResId | 17039360, 649, 7).addIconById(iconResId | 16973824, 650, 8).addIconById(iconResId | 16908288, 651, 9).addIconById(iconResId | 16842752, 652, 10).addIconById(iconResId | 17104896, 653, 11).addIconById(iconResId | 16515072, 646, 2).addIconById(255, 647, 12).addActionById(mmpProtocol.getExtType(), 655, 14).addActionById(-1, 718, 13);
                        break;
                    default:
                        if (((XmppProtocol) account).mo83f()) {
                            dialogScreen.addIconById(387, 642, 1).addIconById(385, 647, 0);
                        } else {
                            dialogScreen.addIconById(383, 642, 1).addIconById(16384383, 643, 4).addIconById(16318847, 644, 2).addIconById(16449919, 645, 5).addIconById(16580991, 648, 6).addIconById(16515455, 646, 3).addIconById(381, 647, 0);
                        }
                        break;
                }
                ScreenManager.showScreen(dialogScreen.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_YES), AppState.getString(StateKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.ABOUT:
                AppState.setInt(StateKeys.FLAG_SHOW_NOTIFICATION, 1);
                AppState.setObject(StateKeys.SLOT_SCREEN_TITLE, (Object) StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory())));
                AppState.setObject(StateKeys.SLOT_SCREEN_SUBTITLE, (Object) AppController.getAppVersion());
                AppState.setFromBuffer(StateKeys.SLOT_APP_VERSION_STRING, ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_APP_NAME)).append(AppState.getString(StateKeys.STR_APP_BUILD_SUFFIX)));
                AppState.setObject(StateKeys.SLOT_DEVICE_ID, (Object) new ByteBuffer().writeLongBytes(7234309766870429269L).writeByte(44).writeRawString(AppState.getAppProperty(StateKeys.STR_APP_PROPERTY_NAME)).getStringAndClear());
                ScreenManager.showScreen(ScreenManager.createScreen(2448));
                return;
            case ScreenId.BLOCK_CONFIRM:
                NotificationHelper.showAlertById(10, 710);
                return;
            case ScreenId.UNBLOCK_CONFIRM:
                NotificationHelper.showAlertBuffer(11, ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_BLOCK_CONFIRM)).append(AppState.getCurrentContact().displayName).append(ObjectPool.unpackChars(16167)));
                return;
            case ScreenId.CONFIRM_EXIT:
                NotificationHelper.showConfirmDialog(13, 505);
                return;
            case ScreenId.TRAFFIC_COST:
                StringBuffer sb = ObjectPool.newStringBuffer();
                int intVal = AppState.getInt(StateKeys.SETTING_TRAFFIC_COST);
                AppState.setFromBuffer(StateKeys.SLOT_SCREEN_VALUE, sb.append(intVal / 100).append('.').append(Utils.zeroPad(intVal % 100)));
                ScreenManager.showScreen(ScreenManager.createScreen(3183));
                return;
            case ScreenId.EMOTICON_DIALOG:
                Screen dialogScreen2 = ScreenManager.createDialogScreen(17);
                if (AppState.getAccount() instanceof MmpProtocol) {
                    for (int i3 = 0; i3 <= 36; i3++) {
                        dialogScreen2.addIconById(i3 + 268, i3 + 118, i3);
                    }
                } else {
                    for (int i4 = 0; i4 <= 49; i4++) {
                        if (i4 != 21 && i4 != 27) {
                            dialogScreen2.addIconById(i4 + 161, i4 + 155, i4 + 4);
                        }
                    }
                }
                ScreenManager.showScreen(dialogScreen2.setSoftKeys(AppState.getString(StateKeys.STR_SOFTKEY_YES), AppState.getString(StateKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.DELETE_CONFIRM:
                NotificationHelper.showConfirmDialog(55, 761);
                return;
            case ScreenId.INPUT_DIALOG:
                ScreenManager.showScreen(ScreenManager.createScreen(4711));
                return;
            case ScreenId.STATUS_INPUT:
                XmppContactGroup.showTextInputDialog(AppState.getCurrentContact().displayName, AppState.getString(StateKeys.SLOT_STATUS_TEXT), 1000, StringUtils.isKnownDevice2 ? 2097152 : 0, AppState.getString(StateKeys.STR_INPUT_MODE_DEFAULT), 1059, 1055, new AsyncTask());
                AppState.setInt(StateKeys.INT_LAST_POLL_TIMESTAMP, 0);
                AppState.setInt(StateKeys.INT_LAST_CHECK_TIMESTAMP, 0);
                AppState.setInt(StateKeys.INT_LAST_LIST_SIZE, 0);
                ScreenManager.showScreen(new Screen());
                return;
            case ScreenId.SOFTKEY_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(4633));
                return;
            case ScreenId.EMOTICON_PICKER:
                Screen screen5 = ScreenManager.createScreen(2621);
                if (AppState.getCurrentContact() instanceof MmpContact) {
                    for (int i10 = 0; i10 < 43; i10++) {
                        if (AppState.getString(i10 + 1141) != null) {
                            screen5.addIconTextItem(i10 + 110, StringUtils.intern(Integer.toString(i10)), i10);
                        }
                    }
                } else if (AppState.getCurrentContact() instanceof XmppContact) {
                    for (int i11 = 0; i11 < 37; i11++) {
                        if (AppState.getString(i11 + 1184) != null) {
                            screen5.addIconTextItem(i11 + 318, StringUtils.intern(Integer.toString(i11)), i11);
                        }
                    }
                } else {
                    for (int i12 = 10; i12 < 74; i12++) {
                        screen5.addIconTextItem(i12 + 36, StringUtils.intern(Integer.toString(i12)), i12);
                    }
                    for (int i13 = 0; i13 < 10; i13++) {
                        screen5.addIconTextItem(i13 + 36, StringUtils.intern(Integer.toString(i13)), i13);
                    }
                    screen5.addIconTextItem(142, StringUtils.intern(Integer.toString(74)), 74);
                    screen5.addIconTextItem(137, StringUtils.intern(Integer.toString(75)), 75);
                    screen5.addIconTextItem(210, StringUtils.intern(Integer.toString(76)), 76);
                    screen5.addIconTextItem(205, StringUtils.intern(Integer.toString(77)), 77);
                }
                ScreenManager.showScreen(screen5);
                return;
            case ScreenId.CAPTCHA:
                Screen screen12 = ScreenManager.createScreen(3840);
                Object obj4 = ((Object[]) AppState.pool[StateKeys.OBJ_REGISTRATION_DATA])[2];
                if (obj4 instanceof javax.microedition.lcdui.Image) {
                    screen12.addItem(MenuItem.createGraphics(new GraphicsContext((javax.microedition.lcdui.Image) obj4)));
                } else {
                    screen12.addLabelById(((Integer) obj4).intValue());
                }
                ScreenManager.showScreen(screen12);
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                return;
            case ScreenId.CLEAR_NOTIFICATIONS:
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.PRIVACY_MODE:
                ResourceManager.playNotificationSound(4);
                AppState.setInt(StateKeys.INT_NOTIFICATION_SCREEN_ID, ScreenId.PRIVACY_MODE);
                AppState.setFromPool(StateKeys.SLOT_NOTIFICATION_TITLE, StateKeys.STR_PRIVACY_MODE_BASE);
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.STATUS_PREVIEW:
                String inputText = XmppContactGroup.getTextInputValue();
                AppState.setObject(StateKeys.SLOT_STATUS_TEXT, (Object) inputText);
                AppState.setBool(StateKeys.FLAG_STATUS_TEXT_SET, !StringUtils.isEmpty(inputText));
                ScreenManager.showScreen(ScreenManager.createScreen(2299));
                return;
            case ScreenId.PRESENCE_ACTION:
                NotificationHelper.showAlertById(122, 535);
                return;
            case ScreenId.BLOG_POST:
                String[] langOptions = AppController.getLanguageOptions();
                if (langOptions != null) {
                    AppState.setObject(StateKeys.SLOT_SCREEN_SUBTITLE, (Object) langOptions[0]);
                    AppState.setFromBuffer(StateKeys.SLOT_SCREEN_TITLE, ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_LANGUAGE_PREFIX)).append(langOptions[1]));
                    if (AppState.getInt(StateKeys.COUNTER_SEARCH_RESULTS) != 0) {
                        AppState.clearIndex(StateKeys.SLOT_LANG_OPTION_1);
                        AppState.clearIndex(StateKeys.SLOT_LANG_OPTION_2);
                    }
                    AppState.setFromPool(StateKeys.SLOT_SCREEN_VALUE, StateKeys.SLOT_LANG_OPTION_1);
                    if (AppState.getString(StateKeys.SLOT_SCREEN_DESCRIPTION) != null) {
                        AppState.setFromPool(StateKeys.SLOT_SCREEN_VALUE, StateKeys.SLOT_SCREEN_DESCRIPTION);
                        AppState.clearIndex(StateKeys.SLOT_SCREEN_DESCRIPTION);
                    }
                    ScreenManager.showScreen(ScreenManager.createScreen(2482));
                    return;
                }
                break;
            case ScreenId.CLEAR_SEARCH:
                AppController.clearSearchState();
                return;
            case ScreenId.ASYNC_CONFIRM:
                NotificationHelper.showConfirmDialog(165, 505);
                AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = RegistrationService.newRequest();
                return;
            case ScreenId.UPDATE_ALERT:
                NotificationHelper.showAlertById(171, 787);
                AppState.setInt(StateKeys.FLAG_UPDATE_AVAILABLE, 1);
                return;
            case ScreenId.INVITE_ALERT:
                NotificationHelper.showAlertById(173, 416);
                return;
            case ScreenId.TRAFFIC_STATS:
                ResourceManager.showTrafficStats();
                return;
        }
        AppController.finishScreenBuild();
    }

    public int onMenuItemSelected(Screen screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.STATUS_DIALOG:
                return IOUtils.handleStatusChange(action);
            case ScreenId.ABOUT:
                return 0;
            case ScreenId.BLOCK_CONFIRM:
                return ScreenId.DELETE_CONFIRM;
            case ScreenId.UNBLOCK_CONFIRM:
                return AppController.handleLeftKey();
            case ScreenId.CONFIRM_EXIT:
                return -1;
            case ScreenId.TRAFFIC_COST:
                return ResourceManager.parseBalance();
            case ScreenId.EMOTICON_DIALOG:
                return AppController.handleAccountOption(action);
            case ScreenId.DELETE_CONFIRM:
                return -1;
            case ScreenId.INPUT_DIALOG:
                return AppController.processInputText(title);
            case ScreenId.STATUS_INPUT:
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas().updateCommands());
                ScreenBuilder.onScreenClosed();
                return ScreenId.STATUS_PREVIEW;
            case ScreenId.SOFTKEY_MENU:
                return AppController.handleSoftKeyAction(title);
            case ScreenId.EMOTICON_PICKER:
                return AppController.handleSoundOption(action);
            case ScreenId.CAPTCHA:
                return 0;
            case ScreenId.CLEAR_NOTIFICATIONS:
                return -1;
            case ScreenId.PRIVACY_MODE:
                return AppController.handleHashKey();
            case ScreenId.STATUS_PREVIEW:
                return ResourceManager.handleMessageInputAction(title, action);
            case ScreenId.PRESENCE_ACTION:
                return AppController.handlePresenceAction();
            case ScreenId.BLOG_POST:
                int errorCode2;
                ScreenManager.processScreenForm();
                String messageText6 = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SCREEN_VALUE));
                if (StringUtils.isEmpty(messageText6)) {
                    errorCode2 = NotificationHelper.showError(523);
                } else {
                    MrimAccount mrimAccount5 = (MrimAccount) AppState.getAccount();
                    new AsyncTask(AsyncTaskId.SEND_SMS_REQUEST, new ByteBuffer().writeCompressed(1442705).writeCompressed(1049531).writeUInt(4022591).writeRawString(mrimAccount5.login).writeUInt(4022822).writeRawString(mrimAccount5.password).writeCompressed(459757).writeCompressed(459750).writeRawString(Conversation.urlEncodeCyrillic((Object) messageText6)).writeRawString(Utils.defaultStr(AppState.getBool(StateKeys.FLAG_CAPTCHA_SHOWN) ? AppState.getString(StateKeys.SLOT_SCREEN_SUBTITLE) : null)).getStringAndClear());
                    AppState.addInt(StateKeys.COUNTER_SEARCH_RESULTS, 1);
                    errorCode2 = 0;
                }
                return errorCode2;
            case ScreenId.CLEAR_SEARCH:
                return 0;
            case ScreenId.ASYNC_CONFIRM:
                return -1;
            case ScreenId.UPDATE_ALERT:
                return AppController.handleRightKey();
            case ScreenId.INVITE_ALERT:
                return AppController.handleInviteAction();
            case ScreenId.TRAFFIC_STATS:
                int intVal = AppState.getInt(StateKeys.INT_PERIOD_INDEX);
                Account account2 = AppState.getAccount();
                if (account2 != null) {
                    account2.syncArray[intVal + intVal + 1] = 0;
                    account2.syncArray[intVal + intVal] = 0;
                } else {
                    for (int i3 = 0; i3 < 4; i3++) {
                        TrafficAccounting.addTrafficCount(i3, intVal, 0);
                        TrafficAccounting.addTrafficCount(i3, intVal, 1);
                    }
                }
                return 0;
        }
        return 0;
    }

    public int onMenuItemAction(Screen screen, MenuItem item, Object data) {
        switch (screen.screenId) {
            case ScreenId.STATUS_DIALOG:
                return 0;
            case ScreenId.ABOUT:
                return 0;
            case ScreenId.BLOCK_CONFIRM:
                return 0;
            case ScreenId.UNBLOCK_CONFIRM:
                return 0;
            case ScreenId.CONFIRM_EXIT:
                return 0;
            case ScreenId.TRAFFIC_COST:
                return 0;
            case ScreenId.EMOTICON_DIALOG:
                return 0;
            case ScreenId.DELETE_CONFIRM:
                return -1;
            case ScreenId.INPUT_DIALOG:
                return 0;
            case ScreenId.STATUS_INPUT:
                int sendMsgResult;
                String inputText = XmppContactGroup.getTextInputValue();
                if (!StringUtils.isEmpty(inputText) && 0 != (sendMsgResult = AppState.getCurrentContact().sendMessage(inputText))) {
                    ScreenBuilder.onScreenClosed();
                    IOUtils.postNotification(AppState.getString(sendMsgResult));
                }
                AppState.setInt(StateKeys.FLAG_STATUS_TEXT_SET, 0);
                AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas());
                ScreenBuilder.onScreenClosed();
                return ScreenId.CLEAR_SEARCH;
            case ScreenId.SOFTKEY_MENU:
                return 0;
            case ScreenId.EMOTICON_PICKER:
                return 0;
            case ScreenId.CAPTCHA:
                return 0;
            case ScreenId.CLEAR_NOTIFICATIONS:
                return 0;
            case ScreenId.PRIVACY_MODE:
                return AppController.handleHashKey();
            case ScreenId.STATUS_PREVIEW:
                ScreenBuilder.onScreenClosed();
                return 0;
            case ScreenId.PRESENCE_ACTION:
                return 0;
            case ScreenId.BLOG_POST:
                return 0;
            case ScreenId.CLEAR_SEARCH:
                return 0;
            case ScreenId.ASYNC_CONFIRM:
                return 0;
            case ScreenId.UPDATE_ALERT:
                AppState.setInt(StateKeys.FLAG_APP_STARTING, 0);
                MapController.toggleScrollMode();
                return ScreenId.MAP;
            case ScreenId.INVITE_ALERT:
                AppState.setAccount((Object) null);
                return ScreenId.CLOSE;
            case ScreenId.TRAFFIC_STATS:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(Screen screen) {
        switch (screen.screenId) {
            case ScreenId.ABOUT:
                AppController.clearPreviewState();
                break;
            case ScreenId.TRAFFIC_COST:
                AppState.clearIndex(StateKeys.SLOT_SCREEN_VALUE);
                break;
            case ScreenId.SOFTKEY_MENU:
                IOUtils.setSelectedItems((Object) null);
                break;
            case ScreenId.CLEAR_SEARCH:
                AppState.clearIndex(StateKeys.SLOT_CURRENT_MSG_TEXT);
                AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
                break;
            case ScreenId.PRESENCE_ACTION:
                ObjectPool.releaseVector(AppState.getVector(StateKeys.VEC_ACCOUNT_SELECTION));
                AppState.clearIndex(StateKeys.VEC_ACCOUNT_SELECTION);
                break;
            case ScreenId.BLOG_POST:
                AppController.clearPreviewState();
                break;
        }
    }

    public int onItemSelected(Screen screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.STATUS_DIALOG:
                return IOUtils.handleStatusChange(selectedOption);
            case ScreenId.ABOUT:
                return 0;
            case ScreenId.BLOCK_CONFIRM:
                return ScreenId.DELETE_CONFIRM;
            case ScreenId.UNBLOCK_CONFIRM:
                return AppController.handleLeftKey();
            case ScreenId.CONFIRM_EXIT:
                return -1;
            case ScreenId.TRAFFIC_COST:
                return 0;
            case ScreenId.EMOTICON_DIALOG:
                return AppController.handleAccountOption(selectedOption);
            case ScreenId.DELETE_CONFIRM:
                return -1;
            case ScreenId.INPUT_DIALOG:
                return AppController.processInputText(title);
            case ScreenId.STATUS_INPUT:
                return 0;
            case ScreenId.SOFTKEY_MENU:
                return AppController.handleSoftKeyAction(title);
            case ScreenId.EMOTICON_PICKER:
                return AppController.handleSoundOption(selectedOption);
            case ScreenId.CAPTCHA:
                return 0;
            case ScreenId.CLEAR_NOTIFICATIONS:
                return 0;
            case ScreenId.PRIVACY_MODE:
                return AppController.handleHashKey();
            case ScreenId.STATUS_PREVIEW:
                return ResourceManager.handleMessageInputAction(title, selectedOption);
            case ScreenId.PRESENCE_ACTION:
                return AppController.handlePresenceAction();
            case ScreenId.BLOG_POST:
                return 0;
            case ScreenId.CLEAR_SEARCH:
                int nextState;
                if (headerData != null) {
                    Object[] objArr = (Object[]) headerData;
                    if (((Integer) objArr[0]).intValue() == 0) {
                        MapPoint mapPoint = new MapPoint((String) objArr[1]);
                        mapPoint.height = 2;
                        MapController.navigateToPoint(mapPoint, false);
                        AppState.setInt(StateKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
                        nextState = ScreenId.MAP;
                    } else {
                        String str = (String) objArr[1];
                        String str2 = (String) objArr[2];
                        long jLongValue = ((Long) objArr[3]).longValue();
                        AppController.clearPreviewState();
                        AppState.setInt(StateKeys.INT_GROUP_OPERATION_RESULT, 0);
                        AppState.setObject(StateKeys.SLOT_DEVICE_ID, (Object) str);
                        AppState.setFromBuffer(StateKeys.SLOT_SCREEN_TITLE, ObjectPool.newStringBuffer().append(str2).append(':'));
                        AppState.setLong(StateKeys.TIMESTAMP_SELECTED_MSG, jLongValue);
                        nextState = ScreenId.MESSAGE_INPUT;
                    }
                } else {
                    AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
                    Contact currentContact = AppState.getCurrentContact();
                    nextState = !currentContact.account.isConnected() ? NotificationHelper.showError(299) : currentContact.isOffline() ? ResourceManager.clearSmsFields() : 63;
                }
                return nextState;
            case ScreenId.ASYNC_CONFIRM:
                return -1;
            case ScreenId.UPDATE_ALERT:
                return AppController.handleRightKey();
            case ScreenId.INVITE_ALERT:
                return AppController.handleInviteAction();
            case ScreenId.TRAFFIC_STATS:
                return 0;
        }
        return 0;
    }

    public int onIdleProcess(Screen screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.STATUS_DIALOG:
                return 0;
            case ScreenId.ABOUT:
                return 0;
            case ScreenId.BLOCK_CONFIRM:
                return 0;
            case ScreenId.UNBLOCK_CONFIRM:
                return 0;
            case ScreenId.CONFIRM_EXIT:
                Object[] objArr = (Object[]) AppState.pool[StateKeys.OBJ_REGISTRATION_DATA];
                Object obj2 = objArr[0];
                if (obj2 == null) {
                    String str2 = (String) objArr[20];
                    if ((str2 != null && Utils.parseInt((Object) str2) == 0) || objArr[3] != null) {
                        return RegistrationService.handleRegSubmit(objArr);
                    }
                } else {
                    NotificationHelper.showNotification(StringUtils.concatKeyObj(506, obj2));
                }
                return 0;
            case ScreenId.TRAFFIC_COST:
                return 0;
            case ScreenId.EMOTICON_DIALOG:
                return 0;
            case ScreenId.DELETE_CONFIRM:
                SocketWrapper.closeAll();
                ResourceManager.clearImageCache();
                ResourceManager.clearMathTables();
                System.gc();
                try {
                    Thread.sleep(50);
                } catch (Throwable unused) {
                }
                AppState.addInt(StateKeys.COUNTER_ERRORS, 1);
                AppController.saveOnExit = true;
                AppController.isBackgrounded = true;
                return 0;
            case ScreenId.INPUT_DIALOG:
                return 0;
            case ScreenId.STATUS_INPUT:
                return ResourceManager.updateMessageInput();
            case ScreenId.SOFTKEY_MENU:
                return 0;
            case ScreenId.EMOTICON_PICKER:
                return 0;
            case ScreenId.CAPTCHA:
                return 0;
            case ScreenId.CLEAR_NOTIFICATIONS:
                return 0;
            case ScreenId.PRIVACY_MODE:
                return 0;
            case ScreenId.STATUS_PREVIEW:
                return 0;
            case ScreenId.PRESENCE_ACTION:
                return 0;
            case ScreenId.BLOG_POST:
                return 0;
            case ScreenId.CLEAR_SEARCH:
                Contact currentContact = AppState.getCurrentContact();
                return (currentContact.flags != 0) || currentContact.dirty ? ScreenId.CLEAR_SEARCH : 0;
            case ScreenId.ASYNC_CONFIRM:
                Object[] stateArr = AppState.getObjectArray(StateKeys.OBJ_REGISTRATION_DATA);
                Object obj4 = stateArr[0];
                if (obj4 != null) {
                    NotificationHelper.showNotification(StringUtils.concatKeyObj(506, obj4));
                    return 0;
                }
                return stateArr[3] == null ? 0 : RegistrationService.handleRegSubmit(stateArr);
            case ScreenId.UPDATE_ALERT:
                return 0;
            case ScreenId.INVITE_ALERT:
                return 0;
            case ScreenId.TRAFFIC_STATS:
                return 0;
        }
        return 0;
    }
}
